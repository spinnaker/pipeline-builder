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

package io.spinnaker.pipelinebuilder.json.artifacts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

/**
 * @deprecated replaced by {@link Base64ArtifactDefinition}
 *
 * <pre>
 * Looks like this: {"artifactAccount": "custom-artifact", "id": "(uuid)", "type": "custom/object", "customKind": true},
 * </pre>
 */
@JsonInclude(Include.NON_NULL) // don't serialize null fields
@Deprecated
public class CustomObjectArtifactDefinition implements ArtifactDefinition {
    @JsonProperty private final String id;
    @JsonProperty private final boolean customKind;

    @Builder // this creates a builder with just the `id` field
    public CustomObjectArtifactDefinition(String id) {
        this.id = id;
        this.customKind = true;
    }

    @JsonIgnore
    public String getReference() {
        return null;
    }

    @JsonIgnore
    public ArtifactType getType() {
        return ArtifactType.CUSTOM_OBJECT;
    }

    @JsonIgnore
    public String getArtifactAccount() {
        return "custom-artifact";
    }
}
