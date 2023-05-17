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

import lombok.Builder;

/**
 * A Kubernetes Secret definition that will represent a plaintext
 * Secret object.
 *
 * @see <a href="https://kubernetes.io/docs/reference/generated/kubernetes-api/v1.22/#secret-v1-core">Secret definition</a>
 * @see <a href="https://spinnaker.io/docs/reference/ref-artifacts/in-kubernetes-v2/">Artifacts In Kubernetes</a>
 * @see <a href="https://spinnaker.io/docs/reference/ref-artifacts/types/kubernetes-object/">Kubernetes Object</a>
 */
public class KubernetesSecretArtifactDefinition extends KubernetesArtifactDefinition {
    @Builder
    public KubernetesSecretArtifactDefinition(final String id, final String artifactAccount, final String name, final String reference) {
        super(id, artifactAccount, name, reference);
    }

    @Override
    public ArtifactType getType() {
        return ArtifactType.KUBERNETES_SECRET;
    }
}
