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

package com.apple.spinnaker.examples;

import io.spinnaker.pipelinebuilder.json.Pipeline;
import io.spinnaker.pipelinebuilder.json.Stage;
import io.spinnaker.pipelinebuilder.json.artifacts.Base64ArtifactDefinition;
import io.spinnaker.pipelinebuilder.json.artifacts.ExpectedArtifact;
import io.spinnaker.pipelinebuilder.json.stages.model.StageTypes;
import io.spinnaker.pipelinebuilder.pipelines.JsonPipelineBuilder;
import java.util.Map;
import java.util.UUID;

public class ArtifactsPipelineBuilder extends JsonPipelineBuilder {

    @Override
    public String getUniqueName() {
        return "examples.artifacts";
    }

    @Override
    protected Pipeline buildPipeline() {
        ExpectedArtifact inlineArtifact =
            ExpectedArtifact.builder()
                .id(computeStableIdForArtifact("kubernetes-manifest")) // stable ID
                .displayName("manifest.yml")    // visible in the UI
                .useDefaultArtifact(true)       // will use the base-64 definition we provide
                .usePriorArtifact(false)
                .defaultArtifact(Base64ArtifactDefinition.builder()
                    .name("manifest.yml")
                    .id(UUID.randomUUID().toString())
                    .contents(getResourceContents(getClass().getClassLoader(), // read the file contents from the JAR's resources
                        "embeddedd-resources/manifest.yml"))
                    .build())
                .matchArtifact(Base64ArtifactDefinition.builder() // the artifact we try to match: not something that actually exists
                    .name("no-such-artifact")
                    .build())
                .build();

        Stage declareArtifactStage = Stage.builder()
            .type(StageTypes.FIND_ARTIFACT_FROM_EXECUTION)
            .name("Declare artifact containing a manifest")
            .context(Map.of("application", getApplication(),
                "pipeline", computePipelineIdForClass(this.getClass()), // self-reference
                "executionOptions", Map.of("successful", true)))
            .expectedArtifact(inlineArtifact) // expected artifact is attached here
            .build();

        return Pipeline.builder()
            .name("Artifacts demo")
            .stage(declareArtifactStage)
            .build();
    }
}
