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

package io.spinnaker.pipelinebuilder.json.application;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Builder;
import lombok.Getter;

/**
 * An Application is a Spinnaker application used to house things like
 * pipeline(s), clusters, permissions , and many other things.
 *
 * <pre>
 * Application app = Application.builder()
 *     .name("foo")
 *     .build();
 * </pre>
 */
@Getter
@JsonInclude(Include.NON_NULL) // do not serialize null fields
@JsonPropertyOrder(alphabetic = true)
public class Application {

    // no default value
    /**
     * A comma delimited list of cloud providers associated with the
     * application.
     */
    private final String cloudProviders;
    /** A description of the application. */
    private final String description;
    /** An email that is associated with the application. */
    private final String email;
    /** A unique name used for the Spinnaker application. */
    private final String name;
    /** A set of permissions associated with the application. */
    private final ApplicationPermissions permissions;
    /** Unique ID of the repository. */
    private final String repoProjectKey;
    /** The repository UUID which is usually the URL friendly version of the
     * repository owner's name. */
    private final String repoSlug;
    /**
     * The type of repository.
     * <pre>
     * valid values:
     *  <code>bitbucket</code>
     *  <code>github</code>
     *  <code>stash</code>
     *  <code>gitlab</code>
     * </pre>
     */
    private final String repoType;
    /** Owner of the application.
     *
     * This user is the same to whatever authentication login was used.
     * */
    private final String user;

    // these fields have a default value
    /** Various datasources that can be either enabled or disabled. */
    private final DataSources dataSources;
    /**
     * When this option is enabled, instance status as reported by the cloud
     * provider will be considered sufficient to determine task completion, and
     * when this option is disabled, tasks will normally need health status
     * reported by some other health provider (e.g a load balancer or
     * discovery service) to determine task completion.
     */
    private final boolean platformHealthOnly;
    /**
     * When this option is enabled, users will be able to toggle the option
     * above on a task-by-task basis.
     */
    private final boolean platformHealthOnlyShowOverride;
    /** Various setting for the different cloud providers */
    private final ProviderSettings providerSettings;
    /**
     * List of traffic guards to protect the application's clusters.
     */
    private final List<TrafficGuard> trafficGuards;

    @Builder
    public Application(String cloudProviders, DataSources dataSources, String description, String email, String name,
                       ApplicationPermissions permissions, boolean platformHealthOnly,
                       boolean platformHealthOnlyShowOverride, ProviderSettings providerSettings,
                       String repoProjectKey, String repoSlug, String repoType, List<TrafficGuard> trafficGuards,
                       String user) {
        this.cloudProviders = cloudProviders;
        this.dataSources = dataSources != null ? dataSources : DataSources.builder().build();
        this.description = description;
        this.email = email;
        this.name = Objects.requireNonNull(name, "Application must have a name");
        this.permissions = permissions;
        this.platformHealthOnly = platformHealthOnly;
        this.platformHealthOnlyShowOverride = platformHealthOnlyShowOverride;
        this.providerSettings = providerSettings != null ? providerSettings : ProviderSettings.builder().build();
        this.repoProjectKey = repoProjectKey;
        this.repoSlug = repoSlug;
        this.repoType = repoType;
        this.trafficGuards = trafficGuards != null ? trafficGuards : Collections.emptyList();
        this.user = user;
    }

    /**
     * Serializes the application as JSON
     */
    public String toJson() throws JsonProcessingException {
        return new ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(this);
    }
}
