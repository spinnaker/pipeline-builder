package com.apple.spinnaker.examples;

import io.spinnaker.pipelinebuilder.json.Pipeline;
import io.spinnaker.pipelinebuilder.json.Stage;
import io.spinnaker.pipelinebuilder.json.contexts.RunPipelineContext;
import io.spinnaker.pipelinebuilder.json.stages.model.StageTypes;
import io.spinnaker.pipelinebuilder.pipelines.JsonPipelineBuilder;

public class ParentPipelineBuilder extends JsonPipelineBuilder {

    @Override
    public String getUniqueName() {
        return "examples.parent";
    }

    @Override
    protected Pipeline buildPipeline() {
        Stage runPipelineStage = Stage.builder()
            .type(StageTypes.PIPELINE)
            .name("Run child pipeline")
            .contextObject(RunPipelineContext.builder()
                .application(getApplication())
                .pipelineId(computePipelineIdForClass(TutorialPipelineBuilder.class))
                .build())
            .build();

        return Pipeline.builder()
            .name("Parent")
            .stage(runPipelineStage)
            .build();
    }
}
