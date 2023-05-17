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
 * A helm chart artifact defintion that will retrieve a helm chart from a
 * reference url.
 *
 * @see <a href="https://docs.google.com/document/d/14n4IGisxVo7oqP0quznjBoRpFxrIhqskN4qBc-iGaqs/">helm/chart artifact configuration</a>
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
 * ArtifactsDefinition def = HelmArtifactDefinition.builder()
 *   .artifactAccount("my-helm-account") // this account is defined in the helm configuration
 *   .reference("helm-chart-name")
 *   .version("1.0.0")
 *   .build()
 * </pre>
 */
@JsonInclude(Include.NON_NULL) // don't serialize null fields
public class HelmArtifactDefinition implements ArtifactDefinition {
    @JsonProperty @Getter private final String id;
    /** The account contains url the charts can be found */
    @JsonProperty @Getter private final String artifactAccount;
    /** The name of chart you want to trigger on changes to */
    @JsonProperty @Getter private final String name;
    /** * The name of Helm chart.  */
    @JsonProperty @Getter private final String reference;
    /** The version of chart you want to trigger on changes to */
    @JsonProperty @Getter private final String version;

    @Builder
    public HelmArtifactDefinition(String id, String artifactAccount, String name, String reference, String version) {
        this.id = Objects.requireNonNullElse(id, UUID.randomUUID().toString());
        this.artifactAccount = artifactAccount;
        this.name = name;
        this.reference = reference;
        this.version = version;
    }

    @JsonProperty("type")
    public ArtifactType getType() {
        return ArtifactType.HELM_CHART;
    }
}
