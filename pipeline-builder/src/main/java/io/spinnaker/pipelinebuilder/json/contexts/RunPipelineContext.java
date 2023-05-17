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

package io.spinnaker.pipelinebuilder.json.contexts;

import io.spinnaker.pipelinebuilder.pipelines.JsonPipelineBuilder;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Context class for the "Run Pipeline" stage.
 * Use either with the constructor or with the builder for better readability.
 *
 * <h3>Example</h3>
 * <pre>
 * Stage stage = Stage.builder()
 *     .name(name)
 *     .type(StageTypes.PIPELINE)
 *     .onFailure(FailureStrategy.IGNORE_FAILURE)
 *     .contextObject(RunPipelineContext.builder()
 *         .application("myApplication")
 *         .pipelineId(computePipelineIdForClass(MyCustomPipeline.class))
 *         .parameters(Collections.emptyMap())
 *         .waitForCompletion(true)
 *     .build())
 *     .failStageAfter(Duration.of(15, ChronoUnit.MINUTES))
 *     .build();
 * </pre>
 *
 * @see JsonPipelineBuilder#computePipelineIdForClass
 */
@Getter
@Builder
@JsonInclude(Include.NON_EMPTY)
@AllArgsConstructor
public class RunPipelineContext implements ContextObject {
    private String application;
    @JsonProperty("pipeline") private String pipelineId;
    /** Parameters specified by the targeted pipeline to be ran */
    @JsonProperty("pipelineParameters") private Map<String, String> parameters;
    /**
     * if unchecked, proceeds to the next stage right after triggering the
     * pipeline, without waiting for it to complete. 
     */
    @Builder.Default private Boolean waitForCompletion = true;
    /**
     * If set to true the output of the calling pipeline is not sent to the downstream pipeline.
     */
    private Boolean skipDownstreamOutput;
    /**
     * If set to true, the context of the parent pipeline is not sent to the child pipeline.
     */
    private Boolean omitParentContext;
}
