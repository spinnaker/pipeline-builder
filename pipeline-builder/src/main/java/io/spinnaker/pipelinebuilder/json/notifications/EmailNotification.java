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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.Map;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Allows for email notifications for when the pipeline or stage starts,
 * completes, or fails.
 *
 * <pre>
 * EmailNotification emailNotification = EmailNotification.builder()
 *     .address("my-email@example.com")
 *     .message(Map.of(NotificationEvent.PIPELINE_COMPLETE, "my custom pipeline complete message!"))
 *     .build();
 *
 * Pipeline pipeline = Pipeline.builder()
 *     .name("custom pipeline")
 *     .notification(emailNotification)
 *     .build();
 * </pre>
 */
@Getter
@Builder
@JsonInclude(Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class EmailNotification extends Notification {

    /** Email address to send the notification to. */
    @JsonProperty
    private String address;

    /** CC address to send an email copy to. */
    @JsonProperty
    @Builder.Default
    private String cc = null;

    /**
     * A set of message used to indicate whether or not to notify.
     *
     * A custom message can be provided for the specific event.
     */
    @Builder.Default
    private Map<NotificationEvent, String> message = Collections.emptyMap();

    @JsonProperty
    private final NotificationType type = NotificationType.EMAIL; // excluded from builder
}
