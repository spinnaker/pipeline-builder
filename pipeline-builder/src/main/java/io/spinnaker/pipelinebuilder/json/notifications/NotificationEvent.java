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

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enums that describe different notification event types.
 *
 * Events can happen either at the pipeline or stage level.
 */
public enum NotificationEvent {
    PIPELINE_STARTING("pipeline.starting", true, false),
    PIPELINE_COMPLETE("pipeline.complete", true, false),
    PIPELINE_FAILED("pipeline.failed", true, false),

    STAGE_STARTING("stage.starting", false, true),
    STAGE_COMPLETE("stage.complete", false, true),
    STAGE_FAILED("stage.failed", false, true),

    MANUAL_JUDGMENT("manualJudgment", false, true),
    MANUAL_JUDGMENT_CONTINUE("manualJudgmentContinue", false, true),
    MANUAL_JUDGMENT_STOP("manualJudgmentStop", false, true),
    ;

    /** the JSON key used to signify what type of notification. */
    private final String key;
    /**
     * isPipelineEvent indicates whether or not the notification lives on the pipeline.
     *
     * isPipelineEvent and {@link isStageEvent} are mutually exclusive
     */
    private final boolean isPipelineEvent;
    /**
     * isStageEvent indicates whether or not the notification lives on the stage.
     *
     * isStageEvent and {@link isPipelineEvent} are mutually exclusive
     */
    private final boolean isStageEvent;

    NotificationEvent(String key, boolean isPipelineEvent, boolean isStageEvent) {
        this.key = key;
        this.isPipelineEvent = isPipelineEvent;
        this.isStageEvent = isStageEvent;
    }

    /**
     *  Called by Jackson to serialize the enum value.
     */
    @JsonValue
    public String toJson() {
        return key;
    }

    public boolean isPipelineEvent() {
        return isPipelineEvent;
    }

    public boolean isStageEvent() {
        return isStageEvent;
    }
}
