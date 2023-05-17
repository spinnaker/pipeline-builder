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
 * Depicts at what level the notification lives.
 *
 * This is automatically set when building a stage or a pipeline that has
 * notifications.
 */
public enum NotificationLevel {
    PIPELINE,
    STAGE,
    ;
    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }
}
