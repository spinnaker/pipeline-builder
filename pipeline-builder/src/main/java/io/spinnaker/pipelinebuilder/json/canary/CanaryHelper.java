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

package io.spinnaker.pipelinebuilder.json.canary;

import io.spinnaker.pipelinebuilder.json.Stage;
import io.spinnaker.pipelinebuilder.json.contexts.EvalVarsContext;
import io.spinnaker.pipelinebuilder.json.enums.FailureStrategy;
import io.spinnaker.pipelinebuilder.json.stages.model.EvaluateVariable;
import io.spinnaker.pipelinebuilder.json.stages.model.StageTypes;
import java.util.List;

/** * A helper for building out stages regarding canaries. */
public abstract class CanaryHelper {

    /**
     * Will build the necessary stages to look up a canary ID by name and if
     * found, it will use that canary config ID.
     *
     * In the event that the canary config ID was not found, the default config
     * ID will be used instead
     *
     * @return the list of stages that will find and use a canary config.
     */
    public static List<Stage> buildStagesToLookupCanaryId(
        String application, String configName, String defaultConfigId, String targetVariableName) {

        String tempVariableName = "tempConfigId";
        Stage attemptConfigIdLookupStage = Stage.builder()
            .name("Attempt Canary Config ID lookup")
            .type(StageTypes.EVALUATE_VARIABLES)
            .failOnFailedExpressions(true) // set by Deck for EvaluateVariablesStage
            .comments("Try to resolve the canary config ID")
            .contextObject(EvalVarsContext.ofVariables(List.of(new EvaluateVariable(tempVariableName,
                "${ #canaryConfigNameToId('" + configName + "', '" + application + "') }"))))
            .onFailure(FailureStrategy.IGNORE_FAILURE) // continue even if #canaryConfigNameToId() fails
            .build();

        Stage useConfigIdOrDefaultStage = Stage.builder()
            .name("Use Resolved Config ID or Default Value")
            .type(StageTypes.EVALUATE_VARIABLES)
            .failOnFailedExpressions(true) // set by Deck for EvaluateVariablesStage
            .parentStage(attemptConfigIdLookupStage)
            .comments("If found, use the canary config ID from the previous stage, otherwise use the default constant")
            .contextObject(EvalVarsContext.ofVariables(List.of(new EvaluateVariable(targetVariableName,
                "${ ! " + tempVariableName + ".startsWith('${')" // the `${` prefix would still be there if the call had failed
                    + " ? " + tempVariableName
                    + " : '" + defaultConfigId + "' }"))))
            .build();

        return List.of(attemptConfigIdLookupStage, useConfigIdOrDefaultStage);
    }
}
