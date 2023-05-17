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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A base definition used by all artifact implementations.
 *
 * These fields are always present for an artifact definition.
 * @see <a href="https://spinnaker.io/reference/artifacts-with-artifactsrewrite/">Artifacts</a>
 *
 * Depending on the artifact type, there might be more information too.
 */
public interface ArtifactDefinition {
    @JsonProperty ArtifactType getType();
    @JsonProperty String getReference();
    @JsonProperty String getArtifactAccount();
}
