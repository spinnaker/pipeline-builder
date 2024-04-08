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
