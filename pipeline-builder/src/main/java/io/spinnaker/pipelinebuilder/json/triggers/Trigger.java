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

package io.spinnaker.pipelinebuilder.json.triggers;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

/** Base class to be used for Trigger classes. */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // do not serialize null fields
public abstract class Trigger {
    /**
     * An id that is used to distinguish from other Triggers.
     *
     * If no id was specified, a random UUID will be used.
     */
    private final String id;
    /** Used to dictate whether or not the trigger is enabled*/
    private final boolean enabled;
    /**
     * A service account to run the pipeline as.
     *
     * The current user must have access to the specified service account, and
     * the service account must have access to the current application.
     *
     * Otherwise, you'll receive an 'Access is denied' error.
     */
    private final String runAsUser;
    /** Expected artifacts to be used to match the incoming artifact */
    private final List<String> expectedArtifactIds;

    protected Trigger(String id, Boolean enabled, String runAsUser, List<String> expectedArtifactIds) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.enabled = enabled != null ? enabled : true;
        this.runAsUser = runAsUser;
        this.expectedArtifactIds = expectedArtifactIds;
    }

    public abstract TriggerType getType(); // forces definition of `type` field in subclasses
}
