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

package io.spinnaker.pipelinebuilder.test;

import io.spinnaker.pipelinebuilder.json.triggers.CronTrigger;
import io.spinnaker.pipelinebuilder.json.triggers.CronTrigger.CronTriggerBuilder;
import io.spinnaker.pipelinebuilder.json.triggers.DockerTrigger;
import io.spinnaker.pipelinebuilder.json.triggers.DockerTrigger.DockerTriggerBuilder;
import io.spinnaker.pipelinebuilder.json.triggers.HelmTrigger;
import io.spinnaker.pipelinebuilder.json.triggers.HelmTrigger.HelmTriggerBuilder;
import io.spinnaker.pipelinebuilder.json.triggers.PipelineStatus;
import io.spinnaker.pipelinebuilder.json.triggers.PipelineTrigger;
import io.spinnaker.pipelinebuilder.json.triggers.PipelineTrigger.PipelineTriggerBuilder;
import io.spinnaker.pipelinebuilder.json.triggers.Trigger;
import io.spinnaker.pipelinebuilder.json.triggers.TriggerType;
import io.spinnaker.pipelinebuilder.json.triggers.WebhookTrigger;
import io.spinnaker.pipelinebuilder.json.triggers.WebhookTrigger.WebhookTriggerBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for the trigger classes.
 */
public class TriggerTests {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void webhookTriggerTypeSerialization() {
        checkSerializedField(buildWebhookTriggerWithId(null), "type", TriggerType.WEBHOOK.getJsonValue());
    }

    @Test
    public void cronTriggerTypeSerialization() {
        checkSerializedField(buildCronTriggerWithId(null), "type", TriggerType.CRON.getJsonValue());
    }

    @Test
    public void pipelineTriggerTypeSerialization() {
        checkSerializedField(buildPipelineTriggerWithId(null), "type", TriggerType.PIPELINE.getJsonValue());
    }

    @Test
    public void dockerTriggerTypeSerialization() {
        checkSerializedField(buildDockerTriggerWithId(null), "type", TriggerType.DOCKER.getJsonValue());
    }

    @Test
    public void helmTriggerTypeSerialization() {
        checkSerializedField(buildHelmTriggerWithId(null), "type", TriggerType.HELM.getJsonValue());
    }

    @Test
    public void pipelineTriggerNeedsApplication() {
        NullPointerException npe = Assertions.assertThrows(NullPointerException.class, () -> PipelineTrigger.builder().runAsUser("user1").build());
        Assertions.assertTrue(npe.getMessage().contains("needs an application"));
    }

    @Test
    public void pipelineTriggerNeedsPipelineId() {
        NullPointerException npe = Assertions.assertThrows(NullPointerException.class, () -> PipelineTrigger.builder().runAsUser("user1").application("foo").build());
        Assertions.assertTrue(npe.getMessage().contains("needs a pipeline ID"));
    }

    @Test
    public void pipelineTriggerSingleStatus() {
        PipelineTrigger trigger = basePipelineTriggerBuilder()
            .pipelineStatus(PipelineStatus.FAILED)  // single value
            .build();
        checkSerializedField(trigger, "runAsUser", "user1");
        checkSerializedField(trigger, "application", "app1");
        checkSerializedField(trigger, "pipeline", "id1");
        checkSerializedField(trigger, "status", List.of(PipelineStatus.FAILED.getJsonValue()));
    }

    private PipelineTriggerBuilder basePipelineTriggerBuilder() {
        return PipelineTrigger.builder()
            .runAsUser("user1")
            .application("app1")
            .pipelineId("id1");
    }

    @Test
    public void pipelineTriggerMultipleStatuses() {
        PipelineTrigger trigger = basePipelineTriggerBuilder()
            .pipelineStatuses(List.of(PipelineStatus.FAILED, PipelineStatus.CANCELED))
            .build();
        checkSerializedField(trigger, "status", List.of(PipelineStatus.FAILED.getJsonValue(), PipelineStatus.CANCELED.getJsonValue()));
    }

    @Test
    public void pipelineTriggerDuplicateStatuses() {
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> basePipelineTriggerBuilder()
            .pipelineStatuses(List.of(PipelineStatus.FAILED, PipelineStatus.FAILED))    // duplicate values
            .build());
        Assertions.assertTrue(exception.getMessage().contains("Duplicate status"));
    }

    @Test
    public void pipelineTriggerSingleAndMultiStatuses() {
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> basePipelineTriggerBuilder()
            .pipelineStatus(PipelineStatus.FAILED)
            .pipelineStatuses(List.of(PipelineStatus.SUCCESSFUL, PipelineStatus.CANCELED))  // both fields set
            .build());
        Assertions.assertTrue(exception.getMessage().contains("Only one"));
    }

    private void checkSerializedField(final Trigger trigger, final String fieldName, final Object fieldValue) {
        Map<String, Object> asMap = objectMapper.convertValue(trigger, Map.class);
        Assertions.assertEquals(fieldValue, asMap.get(fieldName));
    }

    @Test
    public void webhookTriggerIdCanBeSet() {
        String customId = "custom-id";
        WebhookTrigger webhookTrigger = buildWebhookTriggerWithId(customId);
        checkSerializedField(webhookTrigger, "id", customId);
    }

    @Test
    public void webhookTriggerIdGenerated() {
        WebhookTrigger webhookTrigger = buildWebhookTriggerWithId(null);
        checkSerializedField(webhookTrigger, "id", webhookTrigger.getId());
    }

    private WebhookTrigger buildWebhookTriggerWithId(final String customId) {
        WebhookTriggerBuilder builder = WebhookTrigger.builder();
        if (customId != null) {
            builder.id(customId);
        }
        return builder
                .source("foo")
                .runAsUser("my-service-account")
                .build();
    }

    @Test
    public void cronTriggerIdCanBeSet() {
        String customId = "custom-id";
        CronTrigger cronTrigger = buildCronTriggerWithId(customId);
        checkSerializedField(cronTrigger, "id", customId);
    }

    @Test
    public void cronTriggerIdGenerated() {
        CronTrigger cronTrigger = buildCronTriggerWithId(null);
        checkSerializedField(cronTrigger, "id", cronTrigger.getId());
    }

    private CronTrigger buildCronTriggerWithId(final String customId) {
        CronTriggerBuilder builder = CronTrigger.builder();
        if (customId != null) {
            builder.id(customId);
        }
        return builder
                .id(customId)
                .cronExpression("5 4 * * *")
                .runAsUser("my-service-account")
                .build();
    }

    private PipelineTrigger buildPipelineTriggerWithId(final String customId) {
        final PipelineTriggerBuilder builder = PipelineTrigger.builder();
        if (customId != null) {
            builder.id(customId);
        }
        return builder
            .id(customId)
            .application("foo")
            .pipelineId("bar")
            .runAsUser("my-service-account")
            .build();
    }

    private DockerTrigger buildDockerTriggerWithId(final String customId) {
        final DockerTriggerBuilder builder = DockerTrigger.builder();
        if (customId != null) {
            builder.id(customId);
        }
        return builder
            .id(customId)
            .organization("org1")
            .registry("reg1")
            .repository("repo1")
            .tag("1.2.3")
            .runAsUser("my-service-account")
            .build();
    }

    private HelmTrigger buildHelmTriggerWithId(final String customId) {
        final HelmTriggerBuilder builder = HelmTrigger.builder();
        if (customId != null) {
            builder.id(customId);
        }
        return builder
            .id(customId)
            .account("account1")
            .chart("chart1")
            .version("1.2.3")
            .digest("hash1")
            .runAsUser("my-service-account")
            .build();
    }

    @Test
    public void pipelineTriggerIdCanBeSet() {
        String customId = "custom-id";
        PipelineTrigger pipelineTrigger = buildPipelineTriggerWithId(customId);
        checkSerializedField(pipelineTrigger, "id", customId);
    }

    @Test
    public void dockerTriggerIdCanBeSet() {
        String customId = "custom-id";
        DockerTrigger dockerTrigger = buildDockerTriggerWithId(customId);
        checkSerializedField(dockerTrigger, "id", customId);
    }

    @Test
    public void dockerTriggerNeedsOrganization() {
        NullPointerException npe = Assertions.assertThrows(NullPointerException.class, () ->
            DockerTrigger.builder().runAsUser("user1").build());
        Assertions.assertTrue(npe.getMessage().contains("needs an organization"));
    }

    @Test
    public void dockerTriggerNeedsRegistry() {
        NullPointerException npe = Assertions.assertThrows(NullPointerException.class, () ->
            DockerTrigger.builder().organization("org1").runAsUser("user1").build());
        Assertions.assertTrue(npe.getMessage().contains("needs a registry"));
    }

    @Test
    public void dockerTriggerNeedsRepository() {
        NullPointerException npe = Assertions.assertThrows(NullPointerException.class, () ->
            DockerTrigger.builder().organization("org1").registry("reg1").runAsUser("user1").build());
        Assertions.assertTrue(npe.getMessage().contains("needs a repository"));
    }

    @Test
    public void dockerTriggerSerializedFields() {
        DockerTrigger dockerTrigger = buildDockerTriggerWithId(null);
        checkSerializedField(dockerTrigger, "id", dockerTrigger.getId());
        checkSerializedField(dockerTrigger, "organization", dockerTrigger.getOrganization());
        checkSerializedField(dockerTrigger, "registry", dockerTrigger.getRegistry());
        checkSerializedField(dockerTrigger, "repository", dockerTrigger.getRepository());
        checkSerializedField(dockerTrigger, "tag", dockerTrigger.getTag());
    }

    @Test
    public void helmTriggerSerializedFields() {
        HelmTrigger helmTrigger = buildHelmTriggerWithId(null);
        checkSerializedField(helmTrigger, "id", helmTrigger.getId());
        checkSerializedField(helmTrigger, "account", helmTrigger.getAccount());
        checkSerializedField(helmTrigger, "chart", helmTrigger.getChart());
        checkSerializedField(helmTrigger, "version", helmTrigger.getVersion());
        checkSerializedField(helmTrigger, "digest", helmTrigger.getDigest());
    }

    @Test
    public void helmTriggerNeedsAccount() {
        NullPointerException npe = Assertions.assertThrows(NullPointerException.class, () ->
            HelmTrigger.builder().runAsUser("user1").build());
        Assertions.assertTrue(npe.getMessage().contains("needs an account"));
    }

    @Test
    public void helmTriggerNeedsChart() {
        NullPointerException npe = Assertions.assertThrows(NullPointerException.class, () ->
            HelmTrigger.builder().account("account1").runAsUser("user1").build());
        Assertions.assertTrue(npe.getMessage().contains("needs a chart"));
    }

    @Test
    public void helmTriggerNeedsVersion() {
        NullPointerException npe = Assertions.assertThrows(NullPointerException.class, () ->
            HelmTrigger.builder().account("account1").chart("chart-1").runAsUser("user1").build());
        Assertions.assertTrue(npe.getMessage().contains("needs a version"));
    }

    @Test
    public void helmTriggerDigestIsOptional() {
        HelmTrigger helmTrigger = HelmTrigger.builder()
            .account("account1")
            .chart("chart-1")
            .version("1.2.3")
            .runAsUser("user1")
            .build();
        Map<String, Object> asMap = objectMapper.convertValue(helmTrigger, Map.class);
        Assertions.assertFalse(asMap.containsKey("digest")); // not serialized if not set
    }
}
