package com.apple.spinnaker.examples;

import io.spinnaker.pipelinebuilder.json.Pipeline;
import io.spinnaker.pipelinebuilder.json.Stage;
import io.spinnaker.pipelinebuilder.json.contexts.WaitContext;
import io.spinnaker.pipelinebuilder.json.notifications.EmailNotification;
import io.spinnaker.pipelinebuilder.json.notifications.NotificationEvent;
import io.spinnaker.pipelinebuilder.json.notifications.SlackNotification;
import io.spinnaker.pipelinebuilder.json.stages.model.StageTypes;
import io.spinnaker.pipelinebuilder.pipelines.JsonPipelineBuilder;
import java.util.List;
import java.util.Map;

public class NotificationsPipelineBuilder extends JsonPipelineBuilder {

    @Override
    public String getUniqueName() {
        return "examples.notifications";
    }

    @Override
    protected Pipeline buildPipeline() {
        Stage waitStage = Stage.builder()
            .type(StageTypes.WAIT)
            .name("Wait")
            .contextObject(WaitContext.ofSeconds(5))
            .notifications(List.of(
                SlackNotification.builder()
                    .message(Map.of(NotificationEvent.STAGE_STARTING, "Starting wait stage"))
                    .channel("#spinnaker-notifications")
                    .build()))
            .build();

        return Pipeline.builder()
            .name("Notifications example")
            .stages(List.of(waitStage))
            .notifications(List.of(
                EmailNotification.builder()
                    .message(Map.of(
                        NotificationEvent.PIPELINE_COMPLETE, "Pipeline ended successfully",
                        NotificationEvent.PIPELINE_FAILED, "Pipeline ended with a failure"))
                    .address("spinnaker-notifications@example.com")
                    .build()))
            .build();
    }
}
