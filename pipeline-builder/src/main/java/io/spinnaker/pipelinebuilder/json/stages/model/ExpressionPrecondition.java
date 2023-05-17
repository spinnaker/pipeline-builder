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

package io.spinnaker.pipelinebuilder.json.stages.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import io.spinnaker.pipelinebuilder.json.helpers.SpelHelper;

/** A representation of a SpEL expression. */
@Getter
public class ExpressionPrecondition {

    @Getter
    @AllArgsConstructor
    private static class Context {
        private final String expression;
        private final String failureMessage;
    }

    private final Context context;
    private final Boolean failPipeline;
    private final String type;

    @Builder
    public ExpressionPrecondition(String expression, String failureMessage) {
        this(expression, failureMessage, true);
    }

    @Builder
    public ExpressionPrecondition(String expression, String failureMessage, Boolean failPipeline) {
        SpelHelper.ValidateExpression(expression);

        this.context = new Context(expression, failureMessage);
        this.failPipeline = failPipeline;
        this.type = "expression";
    }
}
