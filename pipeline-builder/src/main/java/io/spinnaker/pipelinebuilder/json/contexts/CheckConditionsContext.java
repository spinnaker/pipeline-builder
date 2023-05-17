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

import io.spinnaker.pipelinebuilder.json.stages.model.ExpressionPrecondition;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Context class for the "Check Preconditions" stage.
 *
 * <h3>Example</h3>
 * <pre>
 *
 * ExpressionPrecondition precondition = new ExpressionPrecondition(
 *     "${ execution.trigger.type == 'manual' }",
 *     "This pipeline needs to be triggered manually");
 *
 * Stage checkPreconditionsAfterRemovalStage = Stage.builder()
 *     .name("Preconditions")
 *     .type(StageTypes.CHECK_PRECONDITIONS)
 *     .parentStage(someParentStage)
 *     .contextObject(CheckConditionsContext.ofConditions(List.of(precondition)))
 *     .build();
 * </pre>
 */
@Getter
@RequiredArgsConstructor(staticName="ofConditions")
public class CheckConditionsContext implements ContextObject {
    /**
     * A list of SPeL expressions to be evaluated during the "Check
     * Preconditions" stage.
     *
     * <p>
     * Used to evaluate a set of predicates are true at some point in the
     * pipeline.
     * </p>
     *
     * <p>
     * Can be used with the {@link EvalVarsContext} to help minimize SPeL
     * expressions for easier readability
     * </p>
     *
     * @see <a href="https://spinnaker.io/docs/reference/pipeline/expressions/">SPeL Reference</a>
     */
    private final List<ExpressionPrecondition> preconditions;
}
