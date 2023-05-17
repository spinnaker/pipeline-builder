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

import io.spinnaker.pipelinebuilder.json.notifications.Notification;
import io.spinnaker.pipelinebuilder.json.notifications.NotificationEvent;
import io.spinnaker.pipelinebuilder.json.notifications.SlackNotification;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for the notification classes.
 */
public class NotificationTests {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void stageAndPipelineEventTypesAreMutuallyExclusive() {
        for (NotificationEvent event : NotificationEvent.values()) {
            Assertions.assertNotEquals(event.isPipelineEvent(), event.isStageEvent());
        }
    }

    @Test
    public void notificationEventsCustomSerialization() {
        for (NotificationEvent event : NotificationEvent.values()) {
            Notification notification = SlackNotification.builder()
                .message(Map.of(event, "msg"))
                .channel("#spinnaker-ci")
                .build();
            Map<String, Object> asMap = objectMapper.convertValue(notification, Map.class);

            // extract "message" field, it should be a map with one entry
            Object messageObj = asMap.get("message");
            Assertions.assertTrue(Map.class.isAssignableFrom(messageObj.getClass()));
            Map<String, Object> messageMap = (Map<String, Object>) messageObj;
            Assertions.assertEquals(1, messageMap.keySet().size());

            // check that it's serialized correctly
            Assertions.assertEquals(event.toJson(), messageMap.keySet().stream().findFirst().get());

            // and differently from the Java default
            Assertions.assertNotEquals(event.toJson(), event.name());
        }
    }
}
