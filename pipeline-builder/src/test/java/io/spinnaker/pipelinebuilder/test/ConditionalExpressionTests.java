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

package io.spinnaker.pipelinebuilder.test;

import io.spinnaker.pipelinebuilder.json.expressions.ConditionalExpression;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.expression.spel.SpelParseException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class ConditionalExpressionTests {
    @RequiredArgsConstructor
    private static class TestVector {
      @NonNull
      public final String expression;
      public final boolean shouldThrow;

    }

    private final TestVector[] cases = new TestVector[]{
      new TestVector("${foo.bar > 1}", false),
      new TestVector("${ foo.status == 'who' }", false),
      new TestVector("${ #stage(\"someVal\").something }", false),
      new TestVector("${ foo.status baz 'who' }", true),
      new TestVector("1", true),
      new TestVector("1 > 0", true),
      new TestVector("'hello world'", true),
      new TestVector("true", true),
      new TestVector("#foo()", true),
      new TestVector("#{ #stages().bar > 0 }", true),
    };

    @Test
    public void parseExpressions() {
        for (final TestVector vector : cases) {
            try {
                ConditionalExpression.builder().expression(vector.expression).build();
                Assertions.assertFalse(vector.shouldThrow, "expected to throw, but didn't: " + vector.expression);
            } catch (final Exception e) {
                Assertions.assertTrue(vector.shouldThrow, e.toString());
                Assertions.assertTrue(e instanceof SpelParseException);
            }
        }
    }
}

