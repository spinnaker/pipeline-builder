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

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("squid:S2386") // ignore warnings about these fields being mutable: they are not.
public abstract class ExecutionDays {
    public static final List<ExecutionDay> ALL = Arrays.asList(ExecutionDay.values());
    public static final List<ExecutionDay> NONE = List.of();
    public static final List<ExecutionDay> WEEKDAYS = List.of(ExecutionDay.MONDAY,
        ExecutionDay.TUESDAY,
        ExecutionDay.WEDNESDAY,
        ExecutionDay.THURSDAY,
        ExecutionDay.FRIDAY);
    public static final List<ExecutionDay> WEEKEND = List.of(ExecutionDay.SATURDAY, ExecutionDay.SUNDAY);
}
