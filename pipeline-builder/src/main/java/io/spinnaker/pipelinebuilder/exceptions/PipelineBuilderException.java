package io.spinnaker.pipelinebuilder.exceptions;

/**
 * Thrown when we encountered an error building a pipeline.
 */
public class PipelineBuilderException extends RuntimeException {

    public PipelineBuilderException(String message) {
        super(message);
    }

    public PipelineBuilderException(String message, Throwable cause) {
        super(message, cause);
    }
}
