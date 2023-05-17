/*
 * Copyright 2023 Apple, Inc.
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

package io.spinnaker.pipelinebuilder.json.triggers;

import io.spinnaker.pipelinebuilder.json.helpers.ListHelpers;
import io.spinnaker.pipelinebuilder.pipelines.JsonPipelineBuilder;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * A trigger that is activated when another pipeline finishes.
 *
 * <h3>Example</h3>
 * <pre>
 * Pipeline pipeline = Pipeline.builder()
 *     .name("My pipeline")
 *     .stages(stages)
 *     .trigger(PipelineTrigger.builder()
 *         .enabled(true)
 *         .runAsUser("my-user")
 *         .application("my-application")
 *         .pipeline("#pipelineId(\"nameOfPipeline\""))
 *         .status(List.of(PipelineStatus.SUCCESSFUL))
 *         .build())
 *     .build();
 * </pre>
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class PipelineTrigger extends Trigger {
    private final TriggerType type = TriggerType.PIPELINE;
    /** Application where the given pipeline lives. */
    private final String application;
    /**
     * ID of the pipeline whose execution will trigger this pipeline, once it
     * finishes.
     *
     * To retrieve the ID use either {@link JsonPipelineBuilder#computePipelineIdForClass}
     * or by setting pipeline to <code>#pipelineId("nameOfPipeline")</code>.
     * However, when using <code>computePipelineIdForClass</code>, this will
     * return an ID differs from how Spinnaker computes the pipeline ID, which
     * is just a random UUID. This prevents using that method with pipelines
     * that were created in Spinnaker.
     */
    private final String pipeline;
    /** What statuses the pipeline should be triggered on */
    private final List<PipelineStatus> status;

    @Builder
    public PipelineTrigger(String id, String runAsUser, Boolean enabled, List<String> expectedArtifactIds, // parent parameters first
        String application, String pipelineId, List<PipelineStatus> pipelineStatuses, PipelineStatus pipelineStatus) {
        super(id, enabled, runAsUser, expectedArtifactIds);

        this.application = Objects.requireNonNull(application, "Pipeline trigger needs an application");
        this.pipeline = Objects.requireNonNull(pipelineId, "Pipeline trigger needs a pipeline ID");
        this.status = ListHelpers.listWithOneOf("pipelineStatus", pipelineStatus, pipelineStatuses, Collections.emptyList());
        if (this.status.size() != (int)this.status.stream().distinct().count()) {
            throw new IllegalArgumentException("Duplicate status values");
        }
    }
}
