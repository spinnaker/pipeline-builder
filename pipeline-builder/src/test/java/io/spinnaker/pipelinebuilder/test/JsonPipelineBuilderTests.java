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

import io.spinnaker.pipelinebuilder.json.Pipeline;
import io.spinnaker.pipelinebuilder.json.Stage;
import io.spinnaker.pipelinebuilder.pipelines.JsonPipelineBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link JsonPipelineBuilder} wrapper.
 */
public class JsonPipelineBuilderTests {

    public static final String UNIQUE_NAME = "name-1";

    @Test
    public void applicationIsOptional() {
        getJsonPipelineBuilder("ci-pipeline").build();
    }

    @Test
    public void deterministicPipelineId() {
        Pipeline pipeline1 = getJsonPipelineBuilder(UNIQUE_NAME).build();
        Pipeline copy1 = getJsonPipelineBuilder(UNIQUE_NAME).build();
        Pipeline pipeline2 = getJsonPipelineBuilder("name-2").build();

        Assertions.assertEquals(pipeline1.getId(), copy1.getId());
        Assertions.assertNotEquals(pipeline1.getId(), pipeline2.getId());
    }

    @Test
    public void applicationPreservesPipelineId() {
        Pipeline noApp1 = getJsonPipelineBuilder(UNIQUE_NAME).build();

        JsonPipelineBuilder builder = getJsonPipelineBuilder(UNIQUE_NAME);
        builder.setApplication("test-app");
        Pipeline withApp1 = builder.build();

        Assertions.assertEquals(noApp1.getId(), withApp1.getId());
    }

    @Test
    public void saltAffectsPipelineId() {
        Pipeline noApp1 = getJsonPipelineBuilder(UNIQUE_NAME).build();

        JsonPipelineBuilder builderWithAppWithSalt = getJsonPipelineBuilder(UNIQUE_NAME);
        builderWithAppWithSalt.setApplication("test-app");
        builderWithAppWithSalt.setSalt("test-salt");
        Pipeline withAppWithSalt = builderWithAppWithSalt.build();

        Assertions.assertNotEquals(noApp1.getId(), withAppWithSalt.getId());
    }

    private JsonPipelineBuilder getJsonPipelineBuilder(final String uniqueName) {
        return new JsonPipelineBuilder() {
            @Override
            public String getUniqueName() {
                return uniqueName;
            }

            @Override
            protected Pipeline buildPipeline() {
                return Pipeline.builder()
                    .name("test pipeline")
                    .build();
            }
        };
    }

    public static class CustomBuilder extends JsonPipelineBuilder {
        @Override
        public String getUniqueName() {
            return "other-unique-name";
        }

        @Override
        protected Pipeline buildPipeline() {
            return Pipeline.builder()
                .name("test pipeline")
                .build();
        }
    }

    @Test
    public void lookupPipelineByIdNoSalt() {
        lookupPipelineById(false);
    }

    @Test
    public void lookupPipelineByIdWithSalt() {
        lookupPipelineById(true);
    }

    private void lookupPipelineById(boolean addSalt) {
        String salt = "custom-salt";

        CustomBuilder customBuilder = new CustomBuilder();
        if (addSalt) {
            customBuilder.setSalt(salt);
        }
        Pipeline other = customBuilder.build(); // build a pipeline with the class above

        JsonPipelineBuilder builder = new JsonPipelineBuilder() {
            @Override
            public String getUniqueName() {
                return UNIQUE_NAME;
            }

            @Override
            protected Pipeline buildPipeline() {
                // look up the pipeline ID based on its class
                String otherId = computePipelineIdForClass(CustomBuilder.class);

                // validates that a builder can call computePipelineIdForClass and it returns
                // the same ID as when the Pipeline is generated, regardless of the "salt" value
                Assertions.assertEquals(other.getId(), otherId);

                return Pipeline.builder()
                    .name("test pipeline")
                    .build();
            }
        };

        if (addSalt) {
            builder.setSalt(salt);
        }
        builder.build();
    }

    @Test
    public void alphabeticalKeysInJson() throws JsonProcessingException {
        // generate 100 random KV pairs for the stage context
        Map<String, Object> contextValues = IntStream.range(0, 100).boxed()
            .collect(Collectors.toMap(i -> RandomStringUtils.randomAlphabetic(20),
                i -> RandomUtils.nextLong()));

        Pipeline pipeline = Pipeline.builder()
            .name("test pipeline")
            .stage(Stage.builder()
                .name("test stage")
                .type("wait")
                .context(contextValues)
                .build())
            .build();

        String json = pipeline.toJson();

        // get context keys in alphabetical order
        List<String> sortedKeys = contextValues.keySet().stream()
            .sorted()
            .collect(Collectors.toList());

        // validate that each key appears before the next one in the JSON string
        int lastOffset = -1;
        for (String key: sortedKeys) {
            int currentOffset = json.indexOf(key);
            Assertions.assertNotEquals(currentOffset, -1);
            Assertions.assertTrue(currentOffset > lastOffset);
            lastOffset = currentOffset;
        }
    }
}
