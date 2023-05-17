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
import io.spinnaker.pipelinebuilder.json.notifications.EmailNotification;
import io.spinnaker.pipelinebuilder.json.notifications.NotificationEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

/**
 * Tests the builder for {@link Pipeline}.
 */
public class PipelineBuilderTests {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void noNameThrows() {
        NullPointerException thrown = Assertions.assertThrows(NullPointerException.class, () -> Pipeline.builder().build());
        Assertions.assertTrue(thrown.getMessage().contains("name"));
    }

    @Test
    public void nameOnlyIsEnough() {
        Pipeline.builder()
            .name("no stages")
            .build();
    }

    @Test
    public void pipelineBuilderRejectsStageNotifications() throws Throwable {
        for (final NotificationEvent event : NotificationEvent.values()) {
            Executable executable = () -> Pipeline.builder()
                .name("pipeline notification")
                .notifications(List.of(EmailNotification.builder()
                    .message(Map.of(event, "event message"))
                    .build()))
                .build();
            if (event.isStageEvent()) {
                Assertions.assertThrows(IllegalArgumentException.class, executable);
            } else {
                executable.execute(); // no exception
            }
        }
    }

    @Test
    public void pipelineChecksForMissingStagesById() {
        Stage s1 = Stage.builder()
            .name("s1")
            .type("wait")
            .build();
        Stage s2 = Stage.builder()
            .name("s2")
            .parentStage(s1)
            .type("wait")
            .build();

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> Pipeline.builder()
            .name("pipeline with missing stage")
            .stages(List.of(s2))
            .build());
        Assertions.assertTrue(exception.getMessage().contains("not present in the list of stages"));
    }

    @Test
    public void pipelineChecksForDuplicateStageIds() {
        Stage s1 = Stage.builder()
            .id("id-1")
            .name("s1")
            .type("wait")
            .build();
        Stage s2 = Stage.builder()
            .id("id-2")
            .name("s2")
            .parentStage(s1)
            .type("wait")
            .build();

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> Pipeline.builder()
            .name("pipeline with duplicate stages")
            .stages(List.of(s1, s2, s1))
            .build());
        Assertions.assertTrue(exception.getMessage().contains("multiple times"));
        Assertions.assertTrue(exception.getMessage().contains("id-1"));
        Assertions.assertFalse(exception.getMessage().contains("id-2"));
    }

    @Test
    public void stagesWithCircularDependency() { // this crashes Deck
        Stage s1 = Stage.builder()
            .id("id-1")
            .name("s1")
            .type("wait")
            .parentStageId("id-2")
            .build();
        Stage s2 = Stage.builder()
            .id("id-2")
            .name("s2")
            .parentStage(s1)
            .type("wait")
            .build();

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> Pipeline.builder()
            .name("pipeline with circular dependency among its stages")
            .stages(List.of(s1, s2))
            .build());
        Assertions.assertTrue(exception.getMessage().contains("Circular dependency"));
    }

    @Test
    public void noTagsNoDescription() throws JsonProcessingException {
        Pipeline pipeline = Pipeline.builder()
            .name("test")
            .build();

        Map<String, Object> asMap = objectMapper.readValue(pipeline.toJson(), Map.class);
        Assertions.assertFalse(asMap.containsKey("tags"));
        Assertions.assertFalse(asMap.containsKey("description"));
    }

    @Test
    public void pipelineMetadata() throws JsonProcessingException {
        String description = "my test pipeline";
        Map<String, String> tags = Map.of("foo", "bar", "hello", "world");
        Pipeline pipeline = Pipeline.builder()
            .name("test")
            .description(description)
            .tags(tags)
            .build();

        Map<String, Object> asMap = objectMapper.readValue(pipeline.toJson(), Map.class);
        Assertions.assertEquals(description, asMap.get("description"));
        Assertions.assertTrue(asMap.containsKey("tags"));
        Object mapTags = asMap.get("tags");

        Assertions.assertTrue(mapTags instanceof List);
        List<Map<String, String>> tagsList = (List<Map<String, String>>) mapTags;
        Assertions.assertEquals(2, tagsList.size());
        String fooValue = null, helloValue = null;
        for (Map<String, String> entry : tagsList) { // looping since order is undefined
            Assertions.assertTrue(entry.containsKey("name"));
            Assertions.assertTrue(entry.containsKey("value"));
            Assertions.assertTrue(entry.get("name") instanceof String);
            Assertions.assertTrue(entry.get("value") instanceof String);
            if (entry.get("name").equals("foo")) {
                fooValue = entry.get("value");
            } else if (entry.get("name").equals("hello")) {
                helloValue = entry.get("value");
            } else {
                Assertions.fail("Unexpected tag name: " + entry.get("name"));
            }
        }
        Assertions.assertEquals("bar", fooValue);
        Assertions.assertEquals("world", helloValue);
    }
}
