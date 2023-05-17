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
import io.spinnaker.pipelinebuilder.json.application.DataSources;
import io.spinnaker.pipelinebuilder.json.application.DataSourcesType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Tests the builder for {@link DataSources}
 */
public class DataSourcesTest {
    private static Stream<Object[]> provideDataSources() {
        return Stream.of(
                new Object[]{List.of(DataSourcesType.CONFIG), List.of(DataSourcesType.ENVIRONMENTS), true},
                new Object[]{List.of(DataSourcesType.SERVER_GROUPS), List.of(DataSourcesType.EXECUTIONS, DataSourcesType.SERVER_GROUPS), false}
        );
    }

    @ParameterizedTest(name = "enabled={0}, disabled={1}, valid={2}")
    @MethodSource("provideDataSources")
    public void testDataSources(List<DataSourcesType> enabled, List<DataSourcesType> disabled, boolean valid) throws Throwable {
        Executable exec = () -> DataSources.builder()
                .enabled(enabled)
                .disabled(disabled)
                .build();

        if (valid) {
            exec.execute();
        } else {
            Assertions.assertThrows(IllegalArgumentException.class, exec);
        }
    }

    @Test
    public void testJsonValue() {
        List<DataSourcesType> enabled = List.of(DataSourcesType.CONFIG);
        DataSources dataSources = DataSources.builder().enabled(enabled).build();
        Assertions.assertEquals(enabled, dataSources.getEnabled());
        Assertions.assertEquals("config", enabled.get(0).getJsonValue());
        Assertions.assertEquals(Collections.emptyList(), dataSources.getDisabled());
    }

    @Test
    public void testDataSourcesInApplication() {
        DataSources dataSources = DataSources.builder().enabled(List.of(DataSourcesType.SECURITY_GROUPS)).build();
        Application application = Application.builder().name("dataSources").dataSources(dataSources).build();

        Assertions.assertEquals(dataSources, application.getDataSources());
        Assertions.assertEquals(DataSourcesType.SECURITY_GROUPS, application.getDataSources().getEnabled().get(0));
    }
}
