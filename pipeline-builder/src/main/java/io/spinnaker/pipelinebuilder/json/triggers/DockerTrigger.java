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
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Spinnaker will poll a docker registry for any updates to a docker image, and
 * if there are, it will pull those down.
 *
 * To enable this feature in Spinnaker, the --track-digests flag must
 * be set to true.
 * @see <a href="https://spinnaker.io/docs/reference/halyard/commands/#parameters-412">Spinnaker Parameters</a>
 */
@Getter
@JsonInclude(value = Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class DockerTrigger extends Trigger {
    private final TriggerType type = TriggerType.DOCKER;
    /** Organization that the docker image is a part of. */
    private final String organization;
    /** The registry in which the docker image lives. */
    private final String registry;
    /** Repository that the docker image is a part of. */
    private final String repository;
    /** A tag associated with the docker image. */
    private final String tag;

    @Builder
    public DockerTrigger(String id, String runAsUser, Boolean enabled, List<String> expectedArtifactIds, // parent parameters first
        String organization, String registry, String repository, String tag) {
        super(id, enabled, runAsUser, expectedArtifactIds);

        this.organization = Objects.requireNonNull(organization, "Docker trigger needs an organization");
        this.registry = Objects.requireNonNull(registry, "Docker trigger needs a registry");
        this.repository = Objects.requireNonNull(repository, "Docker trigger needs a repository");
        this.tag = tag; // nullable
    }
}
