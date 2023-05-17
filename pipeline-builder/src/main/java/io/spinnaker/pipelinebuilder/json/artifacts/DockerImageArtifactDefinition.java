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
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/**
 * A container image definition that will expose metadata about a container
 * image.
 *
 * @see <a href="https://spinnaker.io/docs/reference/ref-artifacts/types/docker-image/">docker/image configuration</a>
 */
@Getter
@JsonInclude(Include.NON_NULL) // don't serialize null fields
public class DockerImageArtifactDefinition implements ArtifactDefinition {
    private final String id;
    private final String artifactAccount;
    private final String name;
    private final String reference;

    @Builder
    public DockerImageArtifactDefinition(String artifactAccount, String name, String reference) {
        this.id = UUID.randomUUID().toString();
        this.artifactAccount = artifactAccount;
        this.name = name;
        this.reference = reference;
    }

    @JsonProperty("type")
    public ArtifactType getType() {
        return ArtifactType.DOCKER_IMAGE;
    }
}
