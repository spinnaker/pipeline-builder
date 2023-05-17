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

package io.spinnaker.pipelinebuilder.test.application;

import io.spinnaker.pipelinebuilder.json.application.Application;
import io.spinnaker.pipelinebuilder.json.application.ProviderSettings;
import io.spinnaker.pipelinebuilder.json.application.ProviderSettingsAws;
import io.spinnaker.pipelinebuilder.json.application.ProviderSettingsGce;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

/**
 * Tests the builder for {@link ProviderSettings}
 */
public class ProviderSettingsTest {
    private static Stream<Object[]> provideAllPermutations() {
        return Stream.of(
                new Object[]{true, true},
                new Object[]{true, false},
                new Object[]{false, true},
                new Object[]{false, false}
        );
    }

    @ParameterizedTest(name = "aws={0}, gce={1}")
    @MethodSource("provideAllPermutations")
    public void allPermutations(boolean aws, boolean gce) throws Throwable {
        ProviderSettingsAws providerSettingsAws = ProviderSettingsAws.builder().useAmiBlockDeviceMappings(aws).build();
        ProviderSettingsGce providerSettingsGce = ProviderSettingsGce.builder().associatePublicIpAddress(gce).build();
        Executable exec = () -> ProviderSettings.builder().aws(providerSettingsAws).gce(providerSettingsGce).build();
        exec.execute();
    }

    @Test
    public void empty() {
        ProviderSettings providerSettings = ProviderSettings.builder().build();
        ProviderSettingsAws providerSettingsAws = ProviderSettingsAws.builder().build();
        ProviderSettingsGce providerSettingsGce = ProviderSettingsGce.builder().build();

        Assertions.assertEquals(providerSettingsAws.isUseAmiBlockDeviceMappings(), providerSettings.getAws().isUseAmiBlockDeviceMappings());
        Assertions.assertEquals(providerSettingsGce.isAssociatePublicIpAddress(), providerSettings.getGce().isAssociatePublicIpAddress());
    }

    @Test
    public void providerInApplication() {
        ProviderSettingsAws providerSettingsAws = ProviderSettingsAws.builder().useAmiBlockDeviceMappings(true).build();
        Application application = Application.builder()
                .name("an Application")
                .providerSettings(ProviderSettings.builder().aws(providerSettingsAws).build())
                .build();

        // should be what we set it to
        Assertions.assertTrue(application.getProviderSettings().getAws().isUseAmiBlockDeviceMappings());
        // should be the default value, false
        Assertions.assertFalse(application.getProviderSettings().getGce().isAssociatePublicIpAddress());
    }
}
