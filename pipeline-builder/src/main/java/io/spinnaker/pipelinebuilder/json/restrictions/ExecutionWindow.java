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

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

/**
 * Execution window allows you to restrict the time of day or week when
 * deployments can happen.
 *
 * <p>By using execution windows, you can ensure that deployments don’t interfere
 * with times where your service is at peak demand. You can also use execution
 * windows to make sure that there is always someone in the office ready to
 * manually intervene or rollback your pipeline.</p>
 *
 * <h3>Example</h3>
 * <pre>
 * Stage waitStage = Stage.builder()
 *     .name("stage name")
 *     .type(StageTypes.WAIT)
 *     .context(Map.of("waitTime", 1))
 *     .restrictExecutionTime(ExecutionWindow.builder()
 *         .days(ExecutionDays.WEEKDAYS)
 *         .addRandomJitter(RandomJitter.ofMinMaxSeconds(10, 60))
 *         .skipJitterWhenManual(true)
 *         .withWarningWhenSkipped("skipping exec time window!")
 *         .timesOfDay(List.of(
 *             TimeRange.builder().startHour(10).startMin(30).endHour(14).endMin(45).build(),
 *             TimeRange.builder().startHour(18).startMin(0).endHour(20).endMin(15).build()))
 *         .build())
 *     .build();
 * </pre>
 */
@Getter
@JsonInclude(Include.NON_NULL)
public class ExecutionWindow {

    @JsonProperty private final List<ExecutionDay> days;
    @JsonProperty private final List<TimeRange> whitelist;
    @JsonProperty private final ExecutionJitter jitter;
    @JsonIgnore private final String withWarningWhenSkipped; // attached one level above, in context object root

    @Builder
    public ExecutionWindow(List<ExecutionDay> days, TimeRange timeOfDay, List<TimeRange> timesOfDay,
        RandomJitter addRandomJitter, Boolean skipJitterWhenManual, String withWarningWhenSkipped) {
        this.days = days;
        this.whitelist = timeRangeList(timeOfDay, timesOfDay);
        this.withWarningWhenSkipped = withWarningWhenSkipped;

        if (addRandomJitter != null) {
            this.jitter = ExecutionJitter.builder()
                .enabled(true)
                .minDelay(addRandomJitter.getMinSeconds())
                .maxDelay(addRandomJitter.getMaxSeconds())
                .skipManual(Boolean.TRUE.equals(skipJitterWhenManual))
                .build();
        } else {
            if (Boolean.TRUE.equals(skipJitterWhenManual)) {
                throw new IllegalArgumentException("skipJitterWhenManual is enabled but no jitter is configured");
            }
            this.jitter = null;
        }
    }

    private List<TimeRange> timeRangeList(final TimeRange timeOfDay, final List<TimeRange> timesOfDay) {
        if (timeOfDay != null && timesOfDay != null) {
            throw new IllegalArgumentException("Only one value needed between timeOfDay and timesOfDay");
        } else if (timeOfDay != null) {
            return List.of(timeOfDay);
        } else if (timesOfDay != null) {
            return timesOfDay;
        } else {
            return Collections.emptyList();
        }
    }
}
