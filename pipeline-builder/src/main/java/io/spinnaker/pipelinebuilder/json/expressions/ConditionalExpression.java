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

package io.spinnaker.pipelinebuilder.json.expressions;

import lombok.Builder;
import lombok.Getter;

import io.spinnaker.pipelinebuilder.json.helpers.SpelHelper;

/**
 * An expression to be judged using Spel.
 *
 * <p>Does very basic validation to ensure that the SPeL expression starts with
 * <code>${</code> and ends with <code>}</code></p>
 */
@Getter
public class ConditionalExpression {
    private final String type = "expression";
    private final String expression; // only field to include in the builder

    /**
     * ConditionalExpression will do additional validation on the expression to
     * ensure that it is a proper Spel expression.
     *
     * @param expression a valid Spel expression which must be prefixed with
     * "${" and suffixed with "}"
     */
    @Builder
    public ConditionalExpression(final String expression) {
        SpelHelper.ValidateExpression(expression);
        this.expression = expression;
    }
}
