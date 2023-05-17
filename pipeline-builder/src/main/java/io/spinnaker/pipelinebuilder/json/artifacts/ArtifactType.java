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

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Various artifact types to distinguish from.
 *
 * <code>custom/object</code> deprecated
 * <code>embedded/base64</code> is an artifact that is base64 encoded
 * <code>git/repo</code> is an artifact that is represented as a Git repo.
 * <code>github/file</code> is an artifact that is represented as a GitHub file.
 * <code>helm/chart</code> is an artifact that represents a helm chart
 * <code>http/file</code> is an artifact that is represented as an HTTP file
 * <code>docker/image</code> is an artifact that represents a container image
 * <code>kubernetes/configMap</code> is an artifact that represents a Kubernetes ConfigMap object
 * <code>kubernetes/deployment</code> is an artifact that represents a Kubernetes Deployment object
 * <code>kubernetes/replicaSet</code> is an artifact that represents a Kubernetes ReplicaSet object
 * <code>kubernetes/secret</code> is an artifact that represents a Kubernetes Secret object
 */
public enum ArtifactType {
    CUSTOM_OBJECT("custom/object"),
    EMBEDDED_BASE64("embedded/base64"),
    GIT_REPO("git/repo"),
    GITHUB_FILE("github/file"),
    HELM_CHART("helm/chart"),
    HTTP_FILE("http/file"),
    DOCKER_IMAGE("docker/image"),
    KUBERNETES_CONFIGMAP("kubernetes/configMap"),
    KUBERNETES_DEPLOYMENT("kubernetes/deployment"),
    KUBERNETES_REPLICASET("kubernetes/replicaSet"),
    KUBERNETES_SECRET("kubernetes/secret"),
    ;

    private final String type;

    ArtifactType(String type) {
        this.type = type;
    }

    @JsonValue
    public String jsonValue() {
        return type;
    }
}
