package io.spinnaker.pipelinebuilder.json.contexts.canary;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StageCanaryScope {
    private String controlLocation;
    private String controlScope;
    private String experimentLocation;
    private String experimentScope;
    private String scopeName;
    private String step;
}
