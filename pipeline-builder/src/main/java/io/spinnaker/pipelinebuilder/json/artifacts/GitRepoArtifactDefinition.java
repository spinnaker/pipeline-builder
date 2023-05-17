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
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * A Git Repo artifact defintion that will retrieve a git repo from a
 * reference url.
 *
 * @see <a href="https://spinnaker.io/docs/reference/ref-artifacts/types/git-repo/">git/repo artifact configuration</a>
 *
 * <pre>
 * String subPath = "path/to";
 * String repoUrl = "https://github.com/myorg/myrepo"
 *
 * ArtifactsDefinition def = GitHubArtifactDefinition.builder()
 *   .reference(repoUrl)
 *   .subPath(subPath)
 *   .version("main")
 *   .build()
 * </pre>
 */
public class GitRepoArtifactDefinition implements ArtifactDefinition {
    @JsonProperty @Getter
    private final String id;
    /** The account to use to retrieve the repo from Git */
    @JsonProperty @Getter private final String artifactAccount;
    /** HTTPS or SSH URL of your Git repository. */
    @JsonProperty @Getter private final String reference;
    /** The commit hash, branch name, or tag to use retrieve. */
    @JsonProperty @Getter private final String version;
    /** Name of the branch to check out. */
    @JsonProperty @Getter private final Map<String, String> metadata;

    @Builder
    public GitRepoArtifactDefinition(
            final String id,
            final String artifactAccount,
            final String reference,
            final String version,
            final String subPath
    ) {
        this.id = Objects.requireNonNullElse(id, UUID.randomUUID().toString());
        this.artifactAccount = artifactAccount;
        this.reference = reference;
        this.version = version;
        this.metadata = new HashMap<>();
        if (subPath != null) {
            this.metadata.put("subPath",subPath);
        }
    }

    @Override
    public ArtifactType getType() {
        return ArtifactType.GIT_REPO;
    }
}
