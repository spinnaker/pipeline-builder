package io.spinnaker.pipelinebuilder.json.contexts;

/**
 * Marker interface for context objects, which are used to configure stages.
 * Using objects with typed fields instead of Map<String, Object> provides better compile-time safety
 * and allows for better IDE support.
 * Any class can be a context object, as long as it is marked as such by implementing this interface.
 */
public interface ContextObject {
}
