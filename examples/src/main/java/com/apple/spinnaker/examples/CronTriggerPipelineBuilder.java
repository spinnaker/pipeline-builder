package com.apple.spinnaker.examples;

import io.spinnaker.pipelinebuilder.json.Pipeline;
import io.spinnaker.pipelinebuilder.json.Stage;
import io.spinnaker.pipelinebuilder.json.contexts.EvalVarsContext;
import io.spinnaker.pipelinebuilder.json.stages.model.EvaluateVariable;
import io.spinnaker.pipelinebuilder.json.stages.model.StageTypes;
import io.spinnaker.pipelinebuilder.json.triggers.CronTrigger;
import io.spinnaker.pipelinebuilder.pipelines.JsonPipelineBuilder;
import java.util.List;

public class CronTriggerPipelineBuilder extends JsonPipelineBuilder {

    @Override
    public String getUniqueName() {
        return "examples.crontrigger";
    }

    @Override
    protected Pipeline buildPipeline() {
        Stage evalVarsStage = Stage.builder()
                .type(StageTypes.EVALUATE_VARIABLES)
            .name("Show pipeline trigger")
            .contextObject(EvalVarsContext.ofVariables(List.of(
                new EvaluateVariable("pipelineTrigger", "${ execution.trigger }"))))
            .build();

        return Pipeline.builder()
            .name("Cron trigger example")
            .stages(List.of(evalVarsStage))
            .trigger(CronTrigger.builder()
                .cronExpression("0 0 0/1 1/1 * ? *")
                .enabled(true)
                .runAsUser("us-west-service-account")
                .build())
            .build();
    }
}
