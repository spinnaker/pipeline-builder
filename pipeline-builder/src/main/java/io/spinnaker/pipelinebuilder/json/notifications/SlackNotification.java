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

import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Allows for Slack notifications for when the pipeline or stage starts,
 * completes, or fails.
 *
 * To receive notifications, the Slack application linked to the API token
 * must be present in the given channel; it might need to be invited.
 *
 * <h3>Example</h3>
 * <pre>
 * SlackNotification slackNotification = SlackNotification.builder()
 *     .channel("my-channel")
 *     .message(Map.of(
 *         NotificationEvent.PIPELINE_STARTING, "custom message here on starting!",
 *         NotificationEvent.PIPELINE_COMPLETE, "custom message here on completion!",
 *         NotificationEvent.PIPELINE_FAILED, "custom message here on failed!"))
 *     .build();
 *
 * Pipeline pipeline = Pipeline.builder()
 *     .name("custom pipeline")
 *     .notification(slackNotification)
 *     .build();
 * </pre>
 */
@Getter
@Builder
@JsonInclude(Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class SlackNotification extends Notification {

    /**
     * The Slack channel to send notifications to.
     */
    @JsonProperty("address")
    private String channel;

    /**
     * A set of message used to indicate whether to notify.
     *
     * A custom message can be provided for the specific event.
     */
    @Builder.Default
    private Map<NotificationEvent, String> message = Collections.emptyMap();

    @JsonProperty
    private final NotificationType type = NotificationType.SLACK; // excluded from builder
}
