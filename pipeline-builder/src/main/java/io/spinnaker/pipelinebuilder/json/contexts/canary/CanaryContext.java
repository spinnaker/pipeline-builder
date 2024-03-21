package io.spinnaker.pipelinebuilder.json.contexts.canary;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.spinnaker.pipelinebuilder.json.contexts.ContextObject;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Context class for the "Canary" stage.
 *
 */
@Getter
@Builder
public class CanaryContext implements ContextObject {
    private StageCanaryConfig canaryConfig;
    private String analysisType;
}
