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

import java.util.Objects;
import java.util.UUID;

/**
 * A GitHub file artifact defintion that will retrieve a GitHub file from a
 * reference url.
 *
 * @see <a href="https://spinnaker.io/docs/reference/ref-artifacts/types/github-file/">github/file artifact configuration</a>
 *
 * <pre>
 * String filePath = "path/to/file.yml";
 * String url = "https://api.github.com/repos/myorg/myrepo/contents/%s".formatted(filePath)
 *
 * ArtifactsDefinition def = GitHubArtifactDefinition.builder()
 *   .artifactAccount("my-github-account")
 *   .reference(url)
 *   .name(filePath)
 *   .version("main")
 *   .build()
 * </pre>
 */
public class GitHubArtifactDefinition implements ArtifactDefinition {
    @JsonProperty
    @Getter
    private final String id;
    /** The account to use to retrieve the file from GitHub */
    @JsonProperty @Getter private final String artifactAccount;
    /** The path to the artifact file, beginning at the root of the Git repository. */
    @JsonProperty @Getter private final String name;
    /** The full path (including filename) for retrieval via the GitHub API.  */
    @JsonProperty @Getter private final String reference;
    /** The commit hash, branch name, or tag to use retrieve. */
    @JsonProperty @Getter private final String version;

    @Builder
    public GitHubArtifactDefinition(String id, String artifactAccount, String name, String reference, String version) {
        this.id = Objects.requireNonNullElse(id, UUID.randomUUID().toString());
        this.artifactAccount = artifactAccount;
        this.name = name;
        this.reference = reference;
        this.version = version;
    }

    @Override
    public ArtifactType getType() {
        return ArtifactType.GITHUB_FILE;
    }
}
