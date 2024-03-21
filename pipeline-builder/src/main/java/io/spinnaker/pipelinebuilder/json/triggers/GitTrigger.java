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
