/*
 * Copyright 2024 Apple, Inc.
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
package io.spinnaker.pipelinebuilder.json.contexts;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.spinnaker.pipelinebuilder.json.artifacts.ExpectedArtifact;
import io.spinnaker.pipelinebuilder.json.artifacts.InputArtifact;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BakingManifestContext implements ContextObject {
  private final String account;
  private final String templateRenderer;
  private final List<InputArtifact> inputArtifacts;
  private final List<ExpectedArtifact> expectedArtifacts;
  @JsonUnwrapped private final HelmOptions helmOptions;

  @RequiredArgsConstructor
  public enum TemplateRenderer {
    HELM2("HELM2"),
    HELM3("HELM3"),
    ;

    private final String value;

    @Override
    public String toString() {
      return this.value;
    }
  }

  @Getter
  @Builder
  @RequiredArgsConstructor
  public static class HelmOptions {
    private final String namespace;
    private final String outputName;
  }
}
