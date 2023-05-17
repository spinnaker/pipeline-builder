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

package io.spinnaker.pipelinebuilder.json.notifications;

import io.spinnaker.pipelinebuilder.json.Stage;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import io.spinnaker.pipelinebuilder.json.Pipeline;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

import lombok.Getter;
import lombok.Setter;

/**
 * Base class used for notification types such as {@link EmailNotification} and
 * {@link SlackNotification}.
 *
 * This class provides some helping functionality around the notification event
 * map.
 */
@JsonInclude(Include.NON_NULL)
public abstract class Notification {
    // methods that subclasses have to implement
    @JsonProperty("type") abstract NotificationType getType();

    public abstract Map<NotificationEvent, String> getMessage(); // values declared as plain strings

    // `message` values are serialized with a wrapper here
    @JsonProperty("message") Map<NotificationEvent, NotificationText> getMessageStructured() {
        Map<NotificationEvent, String> message = getMessage();
        if (message == null) {
            return null;
        }
        Map<NotificationEvent, NotificationText> filteredMessageMap = message.entrySet().stream()
            .filter(e -> !Strings.isNullOrEmpty(e.getValue())) // do not include empty values in the `messages` field (the keys are listed in `when` anyway)
            .collect(Collectors.toMap(Entry::getKey, e -> new NotificationText(e.getValue())));

        return filteredMessageMap.isEmpty() ? null : filteredMessageMap; // do not return an empty map, that's now how Deck stores it
    }

    @JsonProperty("when")
    private List<NotificationEvent> getWhen() { // extracted from the messages map instead of duplicating entries
        Map<NotificationEvent, String> message = getMessage();
        return message == null ? null : message.keySet().stream().collect(Collectors.toList());
    }

    /**
     * level is set during {@link Pipeline#getNotifications()} or {@link Stage} constructor.
     */
    @Getter
    @Setter
    @JsonProperty NotificationLevel level;
}
