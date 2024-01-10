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

package io.spinnaker.pipelinebuilder.json;

import io.spinnaker.pipelinebuilder.json.artifacts.ExpectedArtifact;
import io.spinnaker.pipelinebuilder.json.artifacts.InputArtifact;
import io.spinnaker.pipelinebuilder.json.contexts.ContextObject;
import io.spinnaker.pipelinebuilder.json.enums.FailureStrategy;
import io.spinnaker.pipelinebuilder.json.helpers.ListHelpers;
import io.spinnaker.pipelinebuilder.json.notifications.Notification;
import io.spinnaker.pipelinebuilder.json.notifications.NotificationEvent;
import io.spinnaker.pipelinebuilder.json.notifications.NotificationLevel;
import io.spinnaker.pipelinebuilder.json.restrictions.ExecutionWindow;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonInclude(Include.NON_EMPTY)
public class Stage extends HashMap<String, Object> {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static AtomicInteger nextStageId = new AtomicInteger(1); // generates unique stage IDs

    @Builder // generates a builder API with only the fields from this constructor; this lets us declare fields that we don't want to include, e.g. `id`
    public Stage(String id, String name, String type,
        String parentStageId, List<String> parentStageIds, Stage parentStage, List<Stage> parentStages,
        Map<String, Object> context, ContextObject contextObject, Boolean failOnFailedExpressions, Boolean failPipeline, Boolean continuePipeline,
        Boolean completeOtherBranchesThenFail, String conditionalOnExpression, FailureStrategy onFailure, Duration failStageAfter,
        List<Notification> notifications, InputArtifact inputArtifact, List<InputArtifact> inputArtifacts,
        ExpectedArtifact expectedArtifact, List<ExpectedArtifact> expectedArtifacts,
        String comments, ExecutionWindow restrictExecutionTime) {

        String stageId = id != null ? id : nextStageId(); // if `id` is not provided, generates a unique stage ID
        String stageName = Objects.requireNonNull(name, "Stage must have a name");
        put("name", stageName);
        put("refId", stageId);
        put("type", Objects.requireNonNull(type, "Stage must have a type"));
        List<String> parentIds = getParentStageIds(parentStageId, parentStageIds, parentStage, parentStages);
        if (parentIds.contains(stageId)) {
            throw new IllegalArgumentException("A stage cannot depend on itself");
        }
        put("requisiteStageRefIds", parentIds);
        if (failOnFailedExpressions != null) {
            put("failOnFailedExpressions", failOnFailedExpressions);
        }
        if (failPipeline != null) {
            put("failPipeline", failPipeline);
        }
        if (continuePipeline != null) {
            put("continuePipeline", continuePipeline);
        }
        if (completeOtherBranchesThenFail != null) {
            put("completeOtherBranchesThenFail", completeOtherBranchesThenFail);
        }
        if (!Strings.isNullOrEmpty(conditionalOnExpression)) {
            if (conditionalOnExpression.startsWith("${") && conditionalOnExpression.endsWith("}")) {
                throw new IllegalArgumentException("Invalid field: 'conditionalOnExpression' should not be wrapped in ${...}");
            }
            put("stageEnabled", Map.of("expression", conditionalOnExpression, "type", "expression"));
        }
        if (Stream.of(failOnFailedExpressions, failPipeline, continuePipeline, completeOtherBranchesThenFail).allMatch(Objects::isNull)) {
            addFailureOptions(onFailure != null ? onFailure : FailureStrategy.HALT_ENTIRE_PIPELINE); // this is the default in Deck;
        }
        if (failStageAfter != null) {
            put("stageTimeoutMs", TimeUnit.NANOSECONDS.toMillis(failStageAfter.toNanos()));
        }

        if (notifications != null && !notifications.isEmpty()) {
            validateNotificationTypes(notifications);
            notifications.forEach(n -> n.setLevel(NotificationLevel.STAGE));
            put("sendNotifications", true);
            put("notifications", notifications);
        }

        if (inputArtifact != null && inputArtifacts != null) {
            throw new IllegalArgumentException("Invalid field: 'inputArtifact' and 'inputArtifacts' are mutually exclusive");
        } else if (inputArtifact != null) {
            put("inputArtifact", inputArtifact);
        } else if (inputArtifacts != null && !inputArtifacts.isEmpty()) {
            put("inputArtifacts", inputArtifacts);
        }

        List<ExpectedArtifact> resolvedExpectedArtifacts = ListHelpers.listWithOneOf("expectedArtifact", expectedArtifact, expectedArtifacts, null);
        if (resolvedExpectedArtifacts != null && !resolvedExpectedArtifacts.isEmpty()) {
            put("expectedArtifacts", resolvedExpectedArtifacts);
        }
        if (!Strings.isNullOrEmpty(comments)) {
            put("comments", comments);
        }
        if (restrictExecutionTime != null) {
            put("restrictExecutionDuringTimeWindow", true);
            put("restrictedExecutionWindow", restrictExecutionTime);
            if (!Strings.isNullOrEmpty(restrictExecutionTime.getWithWarningWhenSkipped())) {
                put("skipWindowText", restrictExecutionTime.getWithWarningWhenSkipped());
            }
        }

        // we store all the keys from the `context` map at the root level of the stage object
        putAll(buildContextMap(context, contextObject));
    }

    /**
     * Generates a string containing a number, for use as a unique stage ID.
     * @return the value from an incrementing atomic counter, guaranteed not to return the same value twice.
     */
    public static String nextStageId() {
        return String.valueOf(nextStageId.getAndIncrement());
    }

    private void validateNotificationTypes(final List<Notification> notifications) {
        boolean allExpectedEvents = notifications == null || notifications.stream()
            .flatMap(notification -> notification.getMessage().keySet().stream())
            .allMatch(NotificationEvent::isStageEvent);
        if (!allExpectedEvents) {
            throw new IllegalArgumentException("Invalid notification types: only stage event notifications were expected");
        }
    }

    /**
     * Builds a context Map for the stage. This can happen in one of 3 ways: either one of the two parameters is set, or a default empty map is created.
     * @param context the actual Map provided to the builder – nothing to change
     * @param contextObject – an object representing the context – typically of the type used in a Task when we do `stage.mapTo(Context.class)`
     */
    private Map<String, Object> buildContextMap(final Map<String, Object> context, final ContextObject contextObject) {
        if (context == null && contextObject == null) { // nothing was provided
            return Collections.emptyMap();
        } else if (context != null && contextObject != null) { // invalid
            throw new IllegalArgumentException("Invalid builder parameters: provide either context(Map) or contextObject(Object), but not both");
        } else if (context != null) {
            return context;
        } else { // serialize as JSON and deserialize into a Map.
            try {
                String asJson = objectMapper.writeValueAsString(contextObject);
                return objectMapper.readValue(asJson, Map.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to create context map from object", e);
            }
        }
    }

    private List<String> getParentStageIds(final String singleParentId, final List<String> listOfParentIds, final Stage parentStage, final List<Stage> parentStages) {
        if (Stream.of(singleParentId, listOfParentIds, parentStage, parentStages).filter(Objects::nonNull).count() > 1) {
            throw new IllegalArgumentException("Invalid builder parameters: provide a single value for parentStage, parentStages, parentStageIds, or parentStageId");
        } else if (singleParentId != null) { // a single stage ID was provided
            return List.of(singleParentId);
        } else if (listOfParentIds != null) { // a list of stage IDs was provided
            return rejectDuplicates(listOfParentIds);
        } else if (parentStage != null) { // using a stage object
            return List.of(parentStage.getId());
        } else if (parentStages != null) { // using a list of stage objects
            return rejectDuplicates(parentStages.stream().map(Stage::getId).collect(Collectors.toList()));
        }
        return Collections.emptyList();
    }

    private List<String> rejectDuplicates(final List<String> values) {
        if (values.stream().distinct().count() != values.size()) {
            throw new IllegalArgumentException("Duplicate parent IDs: " + values);
        }
        return values;
    }

    private void addFailureOptions(FailureStrategy onFailure) {
        if (onFailure == null) { // leave default behavior, do not add explicit keys
            return;
        }
        final Map<String, Function<FailureStrategy, Boolean>> accessors = Map.of("failPipeline", FailureStrategy::getFailPipeline,
            "completeOtherBranchesThenFail", FailureStrategy::getCompleteOtherBranchesThenFail,
            "continuePipeline", FailureStrategy::getContinuePipeline);

        // we're only keeping these values in the context if they do have a boolean value; nulls aren't kept.
        accessors.entrySet().forEach(entry -> {
            Boolean fieldValue = entry.getValue().apply(onFailure);
            if (fieldValue != null) { // actually has a boolean value set
                put(entry.getKey(), fieldValue);
            }
        });
    }

    // these fields are sometimes read back
    public String getId() {
        return (String) get("refId");
    }

    public String getName() {
        return (String) get("name");
    }

    public String getType() {
        return (String) get("type");
    }

    public List<String> getParentIds() {
        return (List<String>) get("requisiteStageRefIds");
    }
}
