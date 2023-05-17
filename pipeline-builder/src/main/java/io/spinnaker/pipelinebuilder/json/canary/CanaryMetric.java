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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;

/**
 * A canary metric is used to evaluate the health of the deployment.
 */
@JsonInclude(Include.NON_NULL) // do not serialize null fields
public class CanaryMetric {
    /** * The name of the metric.  */
    private final String name;
    /**
     * The name of the metric scope.
     *
     * <p>Metric scope defines where, when, and on what the canary analysis
     * occurs.</p>
     *
     * <p>It describes the specific baseline and canary server groups, the
     * start and end times and interval, and the cloud resource on which the
     * baseline and canary are running.</p>
     */
    private final String scopeName;
    /**
     * A list of metric groups.
     *
     * <p>Metric groups are used to compute more complex scores based on a group
     * of metrics rather than a single metric.</p>
     */
    private final List<String> groups;
    /** A query that will be used to be sent to the metrics provider. */
    private final CanaryQuery query;
    /**
     * Analysis configuration contains the canary configuration to be
     * applied to the canary metric.
     *
     * <pre>
     * "analysisConfigurations": {
     *    "canary": {
     *    "direction": "increase",
     *    "nanStrategy": "replace"
     *  }
     * },
     * </pre>
     */
    private final Map<String, Map<String, String>> analysisConfigurations;

    @Builder
    public CanaryMetric(String name, String scopeName, List<String> groups, CanaryFailOn failOn, CanaryNanStrategy nanStrategy, CanaryQuery query) {
        this.name = name;
        this.scopeName = scopeName;
        this.groups = groups;
        this.query = query;

        final Map<String, String> analysisConfig = new HashMap<>();
        analysisConfig.put("direction", failOn.getFieldValue());
        if (nanStrategy.getFieldValue() != null) {
            analysisConfig.put("nanStrategy", nanStrategy.getFieldValue()); // if "default (remove)" is selected, no key is added to this map.
        }
        analysisConfigurations = Map.of("canary", analysisConfig);
    }
}
