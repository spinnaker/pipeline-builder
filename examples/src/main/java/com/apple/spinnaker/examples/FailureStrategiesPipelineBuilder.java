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
import io.spinnaker.pipelinebuilder.json.contexts.EvalVarsContext;
import io.spinnaker.pipelinebuilder.json.stages.model.EvaluateVariable;
import io.spinnaker.pipelinebuilder.json.stages.model.StageTypes;
import io.spinnaker.pipelinebuilder.pipelines.JsonPipelineBuilder;
import java.util.List;

public class FailureStrategiesPipelineBuilder extends JsonPipelineBuilder {

    @Override
    public String getUniqueName() {
        return "examples.failurestrategies";
    }

    @Override
    protected Pipeline buildPipeline() {
        Stage evalVarsStage = Stage.builder()
                .type(StageTypes.EVALUATE_VARIABLES)
            .name("Show pipeline trigger")
            .contextObject(EvalVarsContext.ofVariables(List.of(
                new EvaluateVariable("foo", "123"))))
            .failPipeline(false)
            .completeOtherBranchesThenFail(true)
            .continuePipeline(false)
            .build();

        return Pipeline.builder()
            .name("Failure strategies")
            .stages(List.of(evalVarsStage))
            .build();
    }
}
