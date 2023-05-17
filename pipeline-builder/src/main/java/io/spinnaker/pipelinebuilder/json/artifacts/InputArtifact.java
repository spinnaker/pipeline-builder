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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

/**
 * An artifact to be provided to the pipeline/stage during execution.
 *
 * <pre>
 * InputArtifact inputArtifact = InputArtifact.builder()
 *     .account("helm-test")
 *     .artifact(HelmArtifactDefinition.builder()
 *         .artifactAccount("helm-test")
 *         .name("nginx")
 *         .reference("helm-test")
 *         .version("0.2.4")
 *         .build())
 *     .build();
 * </pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class InputArtifact {
    /**
     * An ID that will be used to reference the artifact.
     *
     * This should generally be a UUID. If an ID shares an ID with another
     * artifact, one artifact will stomp over the other.
     */
    @JsonProperty private final String id;
    /** Account associated with the input artifact */
    @JsonProperty private final String account;
    @JsonProperty private final ArtifactDefinition artifact;
}
