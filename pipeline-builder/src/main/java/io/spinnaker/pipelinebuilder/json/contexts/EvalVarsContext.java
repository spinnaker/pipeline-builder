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

import io.spinnaker.pipelinebuilder.json.stages.model.EvaluateVariable;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Context class for the "Evaluate Variables" stage.
 *
 * <h3>Example</h3>
 * <pre>
 *      {@literal List<EvaluateVariable>} variables = List.of(
 *          new EvaluateVariable("numberOfStages", "${ execution.stages.size() }"),
 *          new EvaluateVariable(${ execution.stages.?[status == 'SUCCEEDED'].size() {@literal >} execution.stages.?[status == 'TERMINAL'].size() }"));
 *
 *      Stage evaluateVariablesStage = Stage.builder()
 *          .name("Evaluate Variables")
 *          .type(StageTypes.EVALUATE_VARIABLES)
 *          .failOnFailedExpressions(true) // set by default for EvaluateVariablesStage
 *          .contextObject(EvalVarsContext.ofVariables(variables))
 *          .parentStage(someParentStage)
 *          .failOnFailedExpressions(true) // this is set by default by Deck on Evaluate Variables stages.
 *          .build();
 * </pre>
 */
@Getter
@RequiredArgsConstructor(staticName="ofVariables")
public class EvalVarsContext implements ContextObject {
    /**
     * List of variables that can be defined for use in other stages.
     *
     * The Evaluate Variables stage can be used to create reuseable variables
     * with custom keys paired with either static values or values as the
     * result of a pipeline expression.
     *
     * @see <a href="https://spinnaker.io/docs/reference/pipeline/expressions/">SPeL Reference</a>
     */
    private final List<EvaluateVariable> variables;
}
