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

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Trigger an event at a given interval using a cron expression.
 *
 * <h3>Example</h3>
 * <pre>
 * Stage cronStage = builder
 *    .id(customId)
 *    .cronExpression("5 4 * * *") // run at 5 after 4 every day
 *    .runAsUser("my-service-account")
 *    .build();
 * </pre>
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class CronTrigger extends Trigger {
    private final TriggerType type = TriggerType.CRON;
    /**
     * An expression used to dictate what interval to trigger at.
     *
     * @see <a href="https://man7.org/linux/man-pages/man5/crontab.5.html">crontab</a>
     */
    private final String cronExpression;

    @Builder
    public CronTrigger(String id, String cronExpression, String runAsUser, Boolean enabled, List<String> expectedArtifactIds) {
        super(id, enabled, runAsUser, expectedArtifactIds);
        this.cronExpression = Objects.requireNonNull(cronExpression);
    }
}
