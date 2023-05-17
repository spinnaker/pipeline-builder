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

package io.spinnaker.pipelinebuilder.json.restrictions;

import lombok.Getter;

/**
 * A random jitter between minSeconds and maxSeconds.
 *
 * <h3>Example</h3>
 * <pre>
 * RandomJitter jitter = RandomJitter.ofMinMaxSeconds(1, 5);
 * </pre>
 */
@Getter
public class RandomJitter {
    private final int minSeconds;
    private final int maxSeconds;

    // explicit to allow *both* public constructor and static helper, something Lombok doesn't support
    public RandomJitter(int minSeconds, int maxSeconds) {
        this.minSeconds = minSeconds;
        this.maxSeconds = maxSeconds;
    }

    /**
     * Setters for both min and max seconds.
     *
     * Since lombok lacks the ability to add builder with multiple parameters,
     * helper methods with multiple parameters need to be handwritten.
     */
    public static RandomJitter ofMinMaxSeconds(int minSeconds, int maxSeconds) {
        return new RandomJitter(minSeconds, maxSeconds);
    }
}
