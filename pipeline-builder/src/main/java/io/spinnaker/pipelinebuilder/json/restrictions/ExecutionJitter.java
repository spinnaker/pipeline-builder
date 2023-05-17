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
 * An internal class used to set the jitter on the {@link ExecutionWindow}.
 *
 * jitter is a networking concept that allows for connections to perform
 * interactions at some duration between a minDelay and maxDelay. This benefits
 * the server during an outage/system error, as a good portion of connections
 * will be retrying. The retries will happen sporadically rather than bombard
 * the server at a single time.
 */
@Getter
@Builder
class ExecutionJitter { // used only in this package
    @JsonProperty private final Boolean enabled;
    @JsonProperty private final Integer minDelay;
    @JsonProperty private final Integer maxDelay;
    @JsonProperty private final Boolean skipManual;
}
