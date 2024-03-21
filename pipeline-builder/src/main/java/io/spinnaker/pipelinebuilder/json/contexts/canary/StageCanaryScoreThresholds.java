package io.spinnaker.pipelinebuilder.json.contexts.canary;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
class StageCanaryScoreThresholds {
    private int marginal;
    private int pass;
}
