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

package io.spinnaker.pipelinebuilder.json.canary;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;

/**
 * CanaryConfig describes the configuration used by the Canary Analysis stage
 * to validate deployment.
 *
 * <p>Applications are not limited to a single canary config, and must
 * reference the config to be used when defining a canary stage</p>
 *
 * <h3>Details about Canaries</h3>
 * <p>
 * Canary is a deployment process in which a change is partially rolled out,
 * then evaluated against the current deployment (baseline) to ensure that the
 * new deployment is operating at least as well as the old, and this evaluation
 * is done using key metrics that are chosen when the canary is configured.
 * </p>
 *
 * <p>
 * Canaries are usually run against deployments containing changes to code, but
 * they can also be used for operational changes, including changes to
 * configuration.
 * </p>
 *
 * <h3>Prerequisites</h3>
 * <p>
 * To enable canaries in a Spinnaker application, canaries must be enabled
 * through the Application config.
 * </p>
 * @see <a href="https://spinnaker.io/docs/guides/user/canary/config/canary-config/#prerequisites">Prerequisites</a>
 */
@JsonInclude(Include.NON_NULL) // do not serialize null fields
public class CanaryConfig {
    /**
     * The default judge if none was provided.
     *
     * @see <a href="https://spinnaker.io/docs/guides/user/canary/judge/">How NetflixACAJudge works</a>
     */
    private static final Map<String, Object> NETFLIX_JUDGE = Map.of("judgeConfigurations", Collections.emptyMap(), "name", "NetflixACAJudge-v1.0");

    /** A list of applications associated with this canary config */
    private final List<String> applications;
    /** The name of the canary config */
    private final String name;
    private final String configVersion;
    /** Time, in epoch milliseconds, of when the canary config was created */
    private final long createdTimestamp;
    /** Time, in ISO 8601 format, of when the canary config was created */
    private final String createdTimestampIso;
    /** Currently reuses the createdTimestamp as the updatedTimestamp */
    private final long updatedTimestamp;
    /** Currently reuses the createdTimestampIso as the updatedTimestampIso */
    private final String updatedTimestampIso;
    /** Description of the canary config */
    private final String description;
    /**
     * A judgement is a spinnaker concept that will clean and validation data,
     * compare metrics, and compute a score against the baseline.
     *
     * @see <a href="https://spinnaker.io/docs/guides/user/canary/judge/">How canary judgements work</a>
     */
    private final Map<String, Object> judge;
    /**
     * A map of filter templates.
     *
     * <p>
     * Filter templates allow you to create advanced queries against your
     * telemetry provider. Filters are also written in the FreeMarker template
     * language
     * </p>
     *
     * @see <a href="https://freemarker.apache.org/">FreeMarker Template Language</a>
     *
     * <ul>
     * <li>
     * Metric Collection
     * <p>
     * Each filter template you create for a given canary configuration is
     * available to each individual metric you add.  For Spinnaker canary
     * purposes, the filter template contains only those refining selectors.
     * The metric type is provided by the list of metrics above.
     * </p>
     * </li>
     * <li>
     * Judgement
     * <p>
     * For Spinnaker canary purposes, the filter template contains only those
     * refining selectors. The metric type is provided by the list of metrics
     * above.
     * </p>
     * </li>
     *</ul>
     * @see <a href="https://spinnaker.io/docs/guides/user/canary/config/filter-templates/">Filter templates</a>
     */
    private final Map<String, Object> templates;
    /**
     * A list of canary metrics that will be used to evaluate against the
     * baseline.
     *
     * @see <a href="https://spinnaker.io/docs/guides/user/canary/config/canary-config/#create-metric-groups-and-add-metrics">How to add and group metrics</a>
     */
    private final List<CanaryMetric> metrics;

    @Builder
    public CanaryConfig(String application, String name, String description, List<CanaryMetric> metrics) {
        this.applications = Collections.singletonList(application);
        this.name = name;
        this.configVersion = "1";
        final Instant now = Instant.now();
        this.createdTimestamp = now.toEpochMilli();
        this.createdTimestampIso = DateTimeFormatter.ISO_INSTANT.format(now);
        this.updatedTimestamp = createdTimestamp; // TODO: change this?
        this.updatedTimestampIso = createdTimestampIso; // TODO: and this?
        this.description = description;
        this.judge = NETFLIX_JUDGE;
        this.templates = Collections.emptyMap();
        this.metrics = metrics;
    }
}
