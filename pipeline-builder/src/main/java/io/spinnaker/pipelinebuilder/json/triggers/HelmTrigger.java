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

package io.spinnaker.pipelinebuilder.json.triggers;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * A Helm chart trigger.
 */
@Getter
@JsonInclude(value = Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class HelmTrigger extends Trigger {
    private final TriggerType type = TriggerType.HELM;
    /** Account tied to the helm chart. */
    private final String account;
    /** The helm chart name */
    private final String chart;
    /** The helm chart version, as semver */
    private final String version;
    /** Hash digest of the helm chart. */
    private final String digest;

    @Builder
    public HelmTrigger(String id, String runAsUser, Boolean enabled, List<String> expectedArtifactIds, // parent parameters first
        String account, String chart, String version, String digest) {
        super(id, enabled, runAsUser, expectedArtifactIds);
        this.account = Objects.requireNonNull(account, "Helm trigger needs an account");
        this.chart = Objects.requireNonNull(chart, "Helm trigger needs a chart");
        this.version = Objects.requireNonNull(version, "Helm trigger needs a version");
        this.digest = digest;
    }
}
