/*
 * Copyright 2024 Apple, Inc.
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
package io.spinnaker.pipelinebuilder.json.triggers;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class GitTrigger extends Trigger {
  private final TriggerType type = TriggerType.GIT;
  private final String branch;
  private final String pathConstraint;
  private final GitTriggerSource source;

  @Builder
  public GitTrigger(
      String id,
      String branch,
      String pathConstraint,
      GitTriggerSource source,
      String runAsUser,
      Boolean enabled,
      List<String> expectedArtifactIds) {
    super(id, enabled, runAsUser, expectedArtifactIds);
    this.branch = branch;
    this.pathConstraint = pathConstraint;
    this.source = source;
  }
}
