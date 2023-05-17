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

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

/**
 * A time range is used to specify a given window for when a stage is able to
 * be executed.
 *
 * <h3>Example</h3>
 * <pre>
 * // creates a window from 12pm to midnight
 * TimeRange window = TimeRange.builder()
 *     .startHour(12)
 *     .startMin(0)
 *     .endHour(23)
 *     .endMin(59)
 *     .build();
 * </pre>
 */
@Getter
public class TimeRange {
    /**
     * The starting hour for when a stage can be exeucted.
     *
     * The hour must be within 24 hours, 0 {@literal <=} <code>startHour</code> {@literal <} 24
     */
    @JsonProperty private final int startHour;
    /**
     * The starting minute for when a stage can be exeucted.
     *
     * The minute must be within 60 seconds, 0 {@literal <=} <code>startMin</code> {@literal <} 60
     */
    @JsonProperty private final int startMin;
    /**
     * The ending hour for when a stage can be exeucted.
     *
     * The hour must be within 24 seconds, 0 {@literal <=} <code>startMin</code> {@literal <} 24
     */
    @JsonProperty private final int endHour;
    /**
     * The ending minute for when a stage can be exeucted.
     *
     * The minute must be within 60 seconds, 0 {@literal <=} <code>startMin</code> {@literal <} 60
     */
    @JsonProperty private final int endMin;

    @Builder
    public TimeRange(final int startHour, final int startMin, final int endHour, final int endMin) {
        this.startHour = validate("startHour", 24, startHour);
        this.startMin = validate("startMin", 60, startMin);
        this.endHour = validate("endHour", 24, endHour);
        this.endMin = validate("endMin", 60, endMin);
    }

    private int validate(final String name, final int maxValue, final int value) {
        if (value < 0  || value >= maxValue) {
            throw new IllegalArgumentException(String.format("Invalid value for '%s': %d", name, value));
        }
        return value;
    }
}
