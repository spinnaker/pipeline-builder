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

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

/**
 * An artifact that is encoded with base64 encoding.
 *
 * The contents do not need to be passed in as base64 as that is done
 * automatically by the class
 *
 * <pre>
 * String filePath = "/path/to/file.yml";
 * String contents = "";
 *
 * try {
 *   contents = new String(Files.readAllBytes(Paths.get(filePath));
 * } catch (IOException e) {
 *   e.printStackTrace();
 *   return;
 * }
 *
 * ArtifactsDefinition def = Base64ArtifactDefinition.builder()
 *   .id(UUID.randomUUID().toString())
 *   .name("my artifact name")
 *   .contents(contents)
 *   .build()
 * </pre>
 */
@JsonInclude(Include.NON_NULL) // don't serialize null fields
public class Base64ArtifactDefinition implements ArtifactDefinition {
    /**
     * An ID that will be used to reference the artifact.
     *
     * This should generally be a UUID. If an ID shares an ID with another
     * artifact, one artifact will stomp over the other.
     */
    @JsonProperty protected final String id;
    /** An optional identifier used for future references to the artifact. */
    @JsonProperty @Getter private final String name;
    /** 
     * The string contents that will later be base64 encoded during
     * serialization
     */
    @JsonIgnore protected final String contents;

    @Builder // this creates a builder with just these two fields
    public Base64ArtifactDefinition(String id, String name, String contents) {
        this.id = Objects.requireNonNullElse(id, UUID.randomUUID().toString());
        this.name = name;
        this.contents = contents;
    }

    @JsonProperty("reference")
    public String getReference() {
        return contents == null ? null : Base64.getEncoder()
            .encodeToString(contents.getBytes(StandardCharsets.UTF_8));
    }

    @JsonProperty("type")
    public ArtifactType getType() {
        return ArtifactType.EMBEDDED_BASE64;
    }

    @JsonProperty("artifactAccount")
    public String getArtifactAccount() {
        return "embedded-artifact";
    }
}
