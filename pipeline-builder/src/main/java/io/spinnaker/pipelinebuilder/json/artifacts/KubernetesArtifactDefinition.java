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
import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

/**
 * A Kubernetes artifact definition.
 *
 * @see KubernetesConfigMapArtifactDefinition
 * @see KubernetesDeploymentArtifactDefinition
 * @see KubernetesReplicaSetArtifactDefinition
 * @see KubernetesSecretArtifactDefinition
 * @see <a href="https://spinnaker.io/docs/reference/ref-artifacts/in-kubernetes-v2/">Artifacts In Kubernetes</a>
 * @see <a href="https://spinnaker.io/docs/reference/ref-artifacts/types/kubernetes-object/">Kubernetes Objects</a>
 */
@Getter
@JsonInclude(Include.NON_NULL) // don't serialize null fields
public abstract class KubernetesArtifactDefinition implements ArtifactDefinition {
    /**
     * The unique ID for this specific Artifact
     */
    private final String id;
    /**
     * The account in Spinnaker, specifically the Kubernetes context, that can access the Artifact
     */
    private final String artifactAccount;
    /**
     * The name of the Artifact
     */
    private final String name;
    /**
     * The name of the Artifact
     */
    private final String reference;
    /**
     * The kind of Artifact this is
     */
    private final String kind = "custom";
    /**
     * If this is a customKind
     */
    private final Boolean customKind = true;

    /**
     * Kubernetes Artifact.
     *
     * @param artifactAccount {@link KubernetesArtifactDefinition#artifactAccount}
     * @param name {@link KubernetesArtifactDefinition#name}
     * @param reference {@link KubernetesArtifactDefinition#reference}
     */
    public KubernetesArtifactDefinition(final String id, final String artifactAccount, final String name, final String reference) {
        this.id = Objects.requireNonNullElse(id, UUID.randomUUID().toString());
        this.artifactAccount = artifactAccount;
        this.name = name;
        this.reference = reference;
    }

    /**
     * Get Artifact type.
     *
     * @return the type of artifact
     */
    @JsonProperty("type")
    public abstract ArtifactType getType();
}
