/*
 * Copyright 2023 Apple, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spinnaker.pipelinebuilder.test;

import io.spinnaker.pipelinebuilder.json.PipelineParameter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class PipelineParameterTests {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void parameterEquals() {
        List<String> options = List.of("foo", "bar");
        PipelineParameter parameterOne = PipelineParameter.builder()
                .name("param")
                .options(options)
                .build();

        PipelineParameter parameterTwo = PipelineParameter.builder()
                .name("param")
                .options(options)
                .build();
        Assertions.assertEquals(parameterOne, parameterTwo);
    }

    @Test
    public void parameterNeedsName() {
        NullPointerException npe = Assertions.assertThrows(NullPointerException.class, () -> PipelineParameter.builder().build());
        Assertions.assertTrue(npe.getMessage().contains("must have a name"));
    }

    @Test
    public void parameterOptionsGetWrapped() {
        List<String> options = List.of("foo", "bar");
        PipelineParameter parameter = PipelineParameter.builder()
            .name("param")
            .options(options)
            .build();
        Assertions.assertEquals("param", parameter.getName());

        Map<String, Object> asMap = objectMapper.convertValue(parameter, Map.class);
        Assertions.assertEquals(parameter.getName(), asMap.get("name"));
        Assertions.assertTrue(asMap.containsKey("options"));
        Assertions.assertTrue(List.class.isAssignableFrom(asMap.get("options").getClass()));
        List<?> optionsFromMap = (List<?>) asMap.get("options");
        Assertions.assertEquals(2, optionsFromMap.size());
        for (int i = 0; i < 2; i++) {
            Assertions.assertFalse(optionsFromMap.get(i) instanceof String);    // no longer plain strings
            Assertions.assertEquals(Map.of("value", options.get(i)), optionsFromMap.get(i));    // now wrapped in an object, in `value` field
        }
    }

    /**
     * Generates all possible kinds of `PipelineParameter` objects, checking that null fields don't get serialized
     * but that all fields except `name` *are* nullable.
     * We also check that the getter that corresponds to each field (e.g. `getLabel()` for the `label` field) returns
     * exactly the same value as what was provided in the builder for that field, even if it doesn't necessarily correspond
     * to what gets serialized (e.g.`options` get wrapped in JSON, but the getter doesn't leak this detail).
     */
    @ParameterizedTest(name = "label={0} required={1} pinned={2} description={3} defaultValue={4} options={5}")
    @MethodSource("provideBuilderValues")
    public void nullFieldsAreNotSerialized(String label, Boolean required, Boolean pinned, String description, String defaultValue, List<String> options) {
        PipelineParameter parameter = PipelineParameter.builder()
            .name("always present")
            .label(label)
            .required(required)
            .pinned(pinned)
            .description(description)
            .defaultValue(defaultValue)
            .options(options)
            .build();
        Map<String, Object> asMap = objectMapper.convertValue(parameter, Map.class);

        checkField(label, parameter.getLabel(), "label", asMap);
        checkField(required, parameter.getRequired(), "required", asMap);
        checkField(pinned, parameter.getPinned(), "pinned", asMap);
        checkField(description, parameter.getDescription(), "description", asMap);
        checkField(defaultValue, parameter.getDefaultValue(), "default", asMap); // key is different for this one

        // options are different since they get wrapped
        if (options != null) {
            Assertions.assertEquals(options, parameter.getOptions()); // getter still returns the input from the builder
            Assertions.assertEquals(options.stream().map(v -> Map.of("value", v)).collect(Collectors.toList()), asMap.get("options"));
        } else {
            Assertions.assertFalse(asMap.containsKey("options"));
        }
    }

    private <T> void checkField(final T inputValue, final T valueFromGetter, final String key, final Map<String, Object> asMap) {
        if (inputValue != null) {
            Assertions.assertEquals(inputValue, valueFromGetter);
            Assertions.assertEquals(inputValue, asMap.get(key));
        } else {
            Assertions.assertFalse(asMap.containsKey(key));
        }
    }

    private static Stream<Object[]> provideBuilderValues() {
        List<Object[]> inputs = new ArrayList<>();
        stringStream().forEach(label -> booleanStream().forEach(required -> booleanStream().forEach(pinned ->
            stringStream().forEach(description -> stringStream().forEach(defaultValue -> listStream().forEach(options ->
                    inputs.add(new Object[]{label, required, pinned, description, defaultValue, options})))))));
        return inputs.stream();
    }

    private static Stream<List<?>> listStream() {
        return Stream.of(List.of("1", "2"), List.of(), null);
    }

    private static Stream<Boolean> booleanStream() {
        return Stream.of(true, false, null);
    }

    private static Stream<String> stringStream() {
        return Stream.of("strvalue", null);
    }
}
