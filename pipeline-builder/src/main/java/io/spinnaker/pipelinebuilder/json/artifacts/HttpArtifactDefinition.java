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
 * An HTTP file artifact is a reference to a file stored in plaintext and
 * reachable via HTTP.
 *
 * These artifacts are generally consumed by stages that
 * read configuration from text files, such as a Deploy Manifest stage.
 *
 * A file represented by an HTTP file artifact can be downloaded using HTTP
 * Basic authentication.
 *
 * @see <a href="https://spinnaker.io/docs/reference/ref-artifacts/types/http-file/">HTTP File Artifact</a>
 *
 * <pre>
 * String url = "https://some-artifact-url.com";
 * // some account that may be tied to the artifact's url
 * String account = "someAccount";
 *
 * ArtifactsDefinition def = HttpArtifactDefinition.builder()
 *   .id(UUID.randomUUID().toString())
 *   .name("my artifact name")
 *   .url(url)
 *   .artifactAccount(account)
 *   .build()
 * </pre>
 */
@JsonInclude(Include.NON_NULL) // don't serialize null fields
public class HttpArtifactDefinition implements ArtifactDefinition {
    /**
     * An ID that will be used to reference the artifact.
     *
     * This should generally be a UUID. If an ID shares an ID with another
     * artifact, one artifact will stomp over the other.
     */
    @JsonProperty @Getter private final String id;
    /** An optional identifier used for future references to the artifact. */
    @JsonProperty @Getter private final String name;
    /** The fully-qualified URL from which the file can be read. */
    @JsonProperty @Getter private final String reference;
    /** An HTTP artifact account. */
    @JsonProperty @Getter private final String artifactAccount;

    @Builder
    public HttpArtifactDefinition(final String id, final String name, final String reference, final String artifactAccount) {
        this.id = Objects.requireNonNullElse(id, UUID.randomUUID().toString());
        this.name = name;
        this.reference = reference;
        this.artifactAccount = artifactAccount;
    }

    @JsonProperty("type")
    public ArtifactType getType() {
        return ArtifactType.HTTP_FILE;
    }
}
