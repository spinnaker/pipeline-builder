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
