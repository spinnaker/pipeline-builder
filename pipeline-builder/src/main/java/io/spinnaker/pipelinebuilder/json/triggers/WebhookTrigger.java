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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Use a webhook to trigger a pipeline.
 *
 * @see <a href="https://spinnaker.io/docs/guides/user/pipeline/triggers/webhooks/">Webhooks</a>
 * <h3>Example</h3>
 * <pre>
 * Pipeline pipeline = Pipeline.builder()
 *     .name("My pipeline")
 *     .stages(stages)
 *     .trigger(WebhookTrigger.builder()
 *         .enabled(true)
 *         .runAsUser("my-user")
 *         .source("https://webhook.url")
 *         .payloadConstraints(Map.of("constraintField", "customConstraintValue"))
 *         .build())
 *     .build();
 * </pre>
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class WebhookTrigger extends Trigger {
    private final TriggerType type = TriggerType.WEBHOOK;

    /** Determines the target URL required to trigger this pipeline */
    private final String source;
    /**
     * When provided, only a webhook with a payload containing at least the
     * specified key/value pairs will be allowed to trigger this pipeline.
     *
     * For example, if you wanted to lock down the systems/users that can
     * trigger this pipeline via this webhook, you could require the key
     * "secret" and value "something-secret" as a constraint.
     *
     * The constraint values may be supplied as regex.
     */
    private final Map<String, String> payloadConstraints;

    @Builder
    public WebhookTrigger(String id, String source, Map<String, String> payloadConstraints, Boolean enabled, String runAsUser,
        List<String> expectedArtifactIds) {
        super(id, enabled, runAsUser, expectedArtifactIds);
        this.source = Objects.requireNonNull(source);
        this.payloadConstraints = payloadConstraints != null ? payloadConstraints : Collections.emptyMap();
    }
}
