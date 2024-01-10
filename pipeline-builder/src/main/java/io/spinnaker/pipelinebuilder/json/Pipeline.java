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

import io.spinnaker.pipelinebuilder.exceptions.PipelineBuilderException;
import io.spinnaker.pipelinebuilder.json.artifacts.ExpectedArtifact;
import io.spinnaker.pipelinebuilder.json.expressions.ConditionalExpression;
import io.spinnaker.pipelinebuilder.json.helpers.ListHelpers;
import io.spinnaker.pipelinebuilder.json.notifications.Notification;
import io.spinnaker.pipelinebuilder.json.notifications.NotificationEvent;
import io.spinnaker.pipelinebuilder.json.notifications.NotificationLevel;
import io.spinnaker.pipelinebuilder.json.triggers.Trigger;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@JsonInclude(Include.NON_EMPTY) // do not serialize null fields
public class Pipeline {

    // these are not provided by the builder
    @Getter @Setter private String id;
    @Getter @Setter private String application;

    // no default value
    private String name;
    private String lastModifiedBy;
    private String description;
    private ConditionalExpression stageEnabled;
    private Boolean disabled;

    // these fields have a default value
    private boolean keepWaitingPipelines;
    private boolean limitConcurrent;
    private Integer maxConcurrentExecutions;
    private List<Stage> stages;
    private List<PipelineParameter> parameterConfig;
    private List<Trigger> triggers;
    private List<Notification> notifications;
    private List<ExpectedArtifact> expectedArtifacts;
    private PipelineLock locked;
    private List<String> roles;
    private List<PipelineTag> tags;

    @Builder
    public Pipeline(String name, String lastModifiedBy, String description, Boolean keepWaitingPipelines, Boolean limitConcurrent, Integer maxConcurrentExecutions,
        Stage stage, List<Stage> stages, PipelineParameter parameter, List<PipelineParameter> parameters, Trigger trigger, List<Trigger> triggers,
        Notification notification, List<Notification> notifications,
        String conditionalOnExpression, PipelineLock locked, ExpectedArtifact expectedArtifact, List<ExpectedArtifact> expectedArtifacts,
        List<String> roles, Map<String, String> tags, Boolean disabled) {
        this.name = Objects.requireNonNull(name, "Pipeline must have a name");
        this.lastModifiedBy = lastModifiedBy;
        this.description = description;
        this.stageEnabled = conditionalOnExpression != null ? new ConditionalExpression(conditionalOnExpression) : null;
        this.disabled = disabled;

        // default values set here when the constructor parameter is null
        this.keepWaitingPipelines = keepWaitingPipelines != null ? keepWaitingPipelines : false;
        this.limitConcurrent = limitConcurrent != null ? limitConcurrent : true;
        this.maxConcurrentExecutions = maxConcurrentExecutions; // null is ok
        this.stages = ListHelpers.listWithOneOf("stage", stage, stages, Collections.emptyList());
        this.parameterConfig = ListHelpers.listWithOneOf("parameter", parameter, parameters, null);
        this.triggers = ListHelpers.listWithOneOf("trigger", trigger, triggers, Collections.emptyList());
        this.notifications = ListHelpers.listWithOneOf("notification", notification, notifications, null);
        this.expectedArtifacts = ListHelpers.listWithOneOf("expectedArtifacts", expectedArtifact, expectedArtifacts, null);
        this.locked = locked;
        this.roles = roles;
        this.tags = tags != null ? mapToListOfTags(tags) : null;

        validateNotificationTypes(this.notifications);
        validateStagesAreAllPresentOnce(this.stages);
        detectLoopsInStageLinks(this.stages);
    }

    private List<PipelineTag> mapToListOfTags(final Map<String, String> tags) {
        return tags.entrySet().stream()
            .map(entry -> new PipelineTag(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }

    private void validateNotificationTypes(final List<Notification> notifications) {
        boolean allExpectedEvents = notifications == null || notifications.stream()
            .flatMap(notification -> notification.getMessage().keySet().stream())
            .allMatch(NotificationEvent::isPipelineEvent);
        if (!allExpectedEvents) {
            throw new IllegalArgumentException("Invalid notification types: only pipeline event notifications were expected");
        }
    }

    private void validateStagesAreAllPresentOnce(final List<Stage> stages) {
        Set<String> stageIds = new HashSet<>();
        Set<String> requiredIds = new HashSet<>();
        stages.forEach(stage -> {
            stageIds.add(stage.getId());
            requiredIds.addAll(stage.getParentIds());
        });
        SetView<String> requiredIdsNotInStageList = Sets.difference(requiredIds, stageIds);
        if (!requiredIdsNotInStageList.isEmpty()) {
            throw new IllegalArgumentException("Some stages are used as parents but not present in the list of stages for the pipeline. Ids: " + Joiner.on(",").join(requiredIdsNotInStageList));
        }
        if (stages.size() != stageIds.size()) {
            String repeatedIds = stages.stream()
                .collect(Collectors.groupingBy(Stage::getId)) // group by stage ID
                .entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1) // find duplicates
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(", "));
            throw new IllegalArgumentException("Some stages appear multiple times. Ids: " + repeatedIds);
        }
    }

    /**
     * Ensures there is no circular dependency between stages.
     */
    private void detectLoopsInStageLinks(final List<Stage> stages) {
        MutableGraph<String> graph = GraphBuilder.directed()
            .allowsSelfLoops(false)
            .expectedNodeCount(stages.size())
            .build();
        stages.forEach(stage -> {
            graph.addNode(stage.getId());
            stage.getParentIds().forEach(parentId -> graph.putEdge(stage.getId(), parentId));
        });
        if (Graphs.hasCycle(graph)) {
            throw new IllegalArgumentException("Circular dependency found among the stages");
        }
    }

    @JsonProperty("notifications")
    public List<Notification> getNotifications() {
        if (notifications != null) {
            notifications.forEach(n -> n.setLevel(NotificationLevel.PIPELINE));
        }
        return notifications;
    }

    /**
     * Serializes the pipeline as JSON.
     *
     * @return the JSON representation of the pipeline
     * @throws PipelineBuilderException if the pipeline cannot be serialized
     */
    public String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.setConfig(objectMapper.getSerializationConfig()
                    .with(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
                    .with(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS))
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new PipelineBuilderException("Failed to serialize pipeline to JSON", e);
        }
    }
}
