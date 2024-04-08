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
import io.spinnaker.pipelinebuilder.json.PipelineParameter;
import io.spinnaker.pipelinebuilder.json.Stage;
import io.spinnaker.pipelinebuilder.json.contexts.EvalVarsContext;
import io.spinnaker.pipelinebuilder.json.contexts.WaitContext;
import io.spinnaker.pipelinebuilder.json.stages.model.EvaluateVariable;
import io.spinnaker.pipelinebuilder.json.stages.model.StageTypes;
import io.spinnaker.pipelinebuilder.pipelines.JsonPipelineBuilder;
import java.util.List;

public class ContextObjectsPipelineBuilder extends JsonPipelineBuilder {

    @Override
    public String getUniqueName() {
        return "examples.contextobjects";
    }

    @Override
    protected Pipeline buildPipeline() {
        Stage waitStage = Stage.builder()
            .type(StageTypes.WAIT)
            .name("Wait a moment")
            .contextObject(WaitContext.ofSeconds(5))
            .build();

        Stage evalSumStage = Stage.builder()
            .type(StageTypes.EVALUATE_VARIABLES)
            .name("Evaluate sum")
            .parentStage(waitStage)
            .contextObject(EvalVarsContext.ofVariables(List.of(
                new EvaluateVariable("sum", "${ #toInt(parameters.a) + #toInt(parameters.b) }"))))
            .build();

        return Pipeline.builder()
            .parameters(List.of(
                PipelineParameter.builder()
                    .name("a")
                    .defaultValue("17")
                    .build(),
                PipelineParameter.builder()
                    .name("b")
                    .defaultValue("25")
                    .build()))
            .name("Tutorial")
            .stages(List.of(waitStage, evalSumStage))
            .build();
    }
}
