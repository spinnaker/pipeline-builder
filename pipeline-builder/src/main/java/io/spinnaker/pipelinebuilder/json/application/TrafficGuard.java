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

package io.spinnaker.pipelinebuilder.json.application;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * A traffic guard is a cluster which you have designated as always having
 * at least one active instance.
 *
 * With traffic guard enabled, if a user or process tries to delete,
 * disable, or resize the server group, Spinnaker will verify the operation
 * will not leave the cluster with no active instances, and fail the
 * operation if it would.
 *
 * @see <a href="https://spinnaker.io/docs/guides/user/applications/configure/#designate-a-traffic-guard-cluster">Traffic Guards</a>
 */
@Getter
@Builder
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // do not serialize null fields
public class TrafficGuard {
    private final String account;
    private final String stack;
    private final String detail;
    private final String location;
    private final boolean enabled;
}
