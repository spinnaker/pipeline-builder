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

import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

/**
 * Within a pipeline trigger or stage, you can declare that the trigger or
 * stage expects a particular artifact to be available, and this artifact is
 * called an expected artifact.
 *
 * Spinnaker compares an incoming artifact (for example, a manifest file stored
 * in GitHub) to the expected artifact (for example, a manifest with the file
 * path path/to/my/manifest.yml); if the incoming artifact matches the
 * specified expected artifact, the incoming artifact is bound to that expected
 * artifact and used by the trigger or stage.
 *
 * @see <a href="https://deesprinter.github.io/spinnaker.github.io/reference/artifacts-with-artifactsrewrite/#expected-artifacts">Expected Artifacts</a>
 */
@Getter
@JsonInclude(Include.NON_NULL)
public class ExpectedArtifact {
    /**
     * An ID that will be used to reference the artifact.
     *
     * This should generally be a UUID. If an ID shares an ID with another
     * artifact, one artifact will stomp over the other.
     */
    @JsonProperty private final String id;
    /**
     * The display name that will be visible to users and be used as a
     * reference to the actual artifact.
     */
    @JsonProperty private final String displayName;
    /**
     * When declaring an expected artifact for a trigger, you can use fields
     * under Match Artifact to specify metadata against which to compare the
     * incoming artifact.
     *
     * This is how you can distinguish between similar
     * artifacts coming from the same artifact account (for example, multiple
     * manifest files stored in a single Git repository) and specify that the
     * trigger should begin pipeline execution only if the incoming artifact
     * matches the parameters that you provided.
     *
     * @see <a href="https://deesprinter.github.io/spinnaker.github.io/reference/artifacts-with-artifactsrewrite/#match-artifact">Match Artifact</a>
     */
    @JsonProperty private final ArtifactDefinition matchArtifact;
    /**
     * In the event that no artifact is found, enabling useDefaultArtifact will
     * use the {@link #defaultArtifact} that was provided.
     */
    @JsonProperty private final boolean useDefaultArtifact;
    /**
     * In the event that no artifact is found, enabling usePriorArtifact will
     * use the previous artifact in the last execution.
     */
    @JsonProperty private final boolean usePriorArtifact;
    /**
     * The default artifact to use when {@link #useDefaultArtifact} is enabled
     * and no artifact has been found.
     */
    @JsonProperty private final ArtifactDefinition defaultArtifact;

    @Builder
    public ExpectedArtifact(String id, String displayName, ArtifactDefinition matchArtifact, Boolean useDefaultArtifact,
        Boolean usePriorArtifact, ArtifactDefinition defaultArtifact) {
        this.id = Objects.requireNonNullElse(id, UUID.randomUUID().toString());
        this.displayName = Objects.requireNonNull(displayName);
        this.matchArtifact = matchArtifact;
        this.useDefaultArtifact = useDefaultArtifact != null ? useDefaultArtifact : false;
        this.usePriorArtifact = usePriorArtifact != null ? usePriorArtifact : false;
        this.defaultArtifact = defaultArtifact;
    }
}
