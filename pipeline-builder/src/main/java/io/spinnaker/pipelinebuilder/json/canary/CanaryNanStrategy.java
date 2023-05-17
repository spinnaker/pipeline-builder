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

import lombok.Getter;

/** The strategy to apply when a NAN or invalid data is encountered. */
public enum CanaryNanStrategy {
    /** Will remove NaN metrics */
    DEFAULT_REMOVE(null),
    /** Will replcae NaNs with 0 */
    REPLACE_WITH_ZERO("replace"),
    /**
     * Will remove NaN metrics.
     *
     * No Different than {@link DEFAULT_REMOVE}
     * */
    REMOVE("remove"),
    ;

    @Getter
    private final String fieldValue;

    CanaryNanStrategy(String fieldValue) {
        this.fieldValue = fieldValue;
    }
}
