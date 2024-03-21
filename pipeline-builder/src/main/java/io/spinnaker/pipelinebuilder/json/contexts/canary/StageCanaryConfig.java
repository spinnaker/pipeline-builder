package io.spinnaker.pipelinebuilder.json.contexts.canary;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
class StageCanaryConfig {
    private String baselineAnalysisOffsetInMins;
    private String lifetimeDuration;
    private String canaryAnalysisIntervalMins;
    private String beginCanaryAnalysisAfterMins;
    private List<StageCanaryScope> scopes;
    private StageCanaryScoreThresholds scoreThresholds;
}
