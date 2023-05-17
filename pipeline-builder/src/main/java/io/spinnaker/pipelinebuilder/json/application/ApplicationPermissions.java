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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

/**
 * Applications may have permissions that may grant or restrict access to other
 * users.
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // do not serialize null fields
public class ApplicationPermissions {
    /** A list of groups that have execute permissions */
    @JsonProperty("EXECUTE")
    @Singular("execute")
    private final List<String> executeGroups;

    /** A list of groups that have read permissions */
    @JsonProperty("READ")
    @Singular("read")
    private final List<String> readGroups;

    /** A list of groups that have write permissions */
    @JsonProperty("WRITE")
    @Singular("write")
    private final List<String> writeGroups;
}
