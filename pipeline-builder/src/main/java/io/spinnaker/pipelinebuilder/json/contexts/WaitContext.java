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

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Context class for the "Wait" stage.
 *
 * This provides input for the wait stage and will configure how long to wait
 * for, in seconds, before progressing to the next stage.
 *
 * <h3>Example</h3>
 * <pre>
 * Stage waitStage = Stage.builder()
 *     .name("Wait 30s")
 *     .parentStage(parentStage)
 *     .type(StageTypes.WAIT)
 *     .contextObject(WaitContext.ofSeconds(30))
 *     .build();
 * </pre>
 */
@Getter
@RequiredArgsConstructor(staticName="ofSeconds")
public class WaitContext implements ContextObject {
    @JsonProperty("waitTime") private final int waitTimeSeconds;
}
