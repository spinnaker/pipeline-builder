package io.spinnaker.pipelinebuilder.json.triggers;

import com.fasterxml.jackson.annotation.JsonValue;

public enum GitTriggerSource {
  GITHUB("github"),
  BITBUCKET("bitbucket"),
  STASH("stash"),
  GITLAB("gitlab"),
  ;

  private final String jsonValue;

  GitTriggerSource(String jsonValue) {
    this.jsonValue = jsonValue;
  }

  @JsonValue
  public String getJsonValue() {
    return jsonValue;
  }
}
