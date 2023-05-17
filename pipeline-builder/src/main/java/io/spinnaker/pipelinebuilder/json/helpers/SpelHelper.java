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

package io.spinnaker.pipelinebuilder.json.helpers;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.SpelParseException;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import lombok.experimental.UtilityClass;

@UtilityClass   // can't be instantiated
public class SpelHelper {
    private static final ExpressionParser parser = new SpelExpressionParser();
    private static final TemplateParserContext context = new TemplateParserContext("${", "}");

    /**
     * Validates whether or not the expression is valid.
     *
     * This utilizes Spring's SpEL parser which validate that the expression is
     * correct along with ensuring that the expression is an actual
     * SpelExpression.
     *
     * @param expression a valid Spel expression which must be prefixed with
     * "${" and suffixed with "}"
     * @throws SpelParseException if the expression is invalid
     */
    public static void ValidateExpression(String expression) {
        // let the parser do the work of verifying whether or not the
        // expression is valid.
        final Expression expr = parser.parseExpression(expression, context);
        if (!(expr instanceof SpelExpression)) {
            throw new SpelParseException(
                expression,
                0,
                SpelMessage.NOT_EXPECTED_TOKEN, "${", expression);
        }
    }
}
