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
