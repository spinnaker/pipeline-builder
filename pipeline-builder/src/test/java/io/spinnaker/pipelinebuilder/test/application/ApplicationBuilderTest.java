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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spinnaker.pipelinebuilder.json.application.Application;
import io.spinnaker.pipelinebuilder.json.application.ApplicationPermissions;
import io.spinnaker.pipelinebuilder.json.application.DataSources;
import io.spinnaker.pipelinebuilder.json.application.DataSourcesType;
import io.spinnaker.pipelinebuilder.json.application.ProviderSettings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * Tests the builder for {@link Application}
 */
public class ApplicationBuilderTest {
    @Test
    public void noNameThrows() {
        NullPointerException thrown = Assertions.assertThrows(NullPointerException.class, () -> Application.builder().build());
        Assertions.assertTrue(thrown.getMessage().contains("name"));
    }

    @Test
    public void nameOnlyIsEnough() {
        Application.builder()
                .name("this is a name")
                .build();
    }

    @Test
    public void platformHealthsAreFalse() {
        Application application = Application.builder().name("this is a name").build();
        Assertions.assertFalse(application.isPlatformHealthOnly());
        Assertions.assertFalse(application.isPlatformHealthOnlyShowOverride());
    }

    @Test
    public void applicationWithEverything() {
        Application.builder()
                .name("application")
                .dataSources(DataSources.builder().disabled(List.of(DataSourcesType.SECURITY_GROUPS)).build())
                .providerSettings(ProviderSettings.builder().build())
                .permissions(ApplicationPermissions.builder().executeGroups(List.of("exec")).build())
                .cloudProviders("kubernetes")
                .description("desc")
                .email("some@example.com")
                .platformHealthOnly(true)
                .repoProjectKey("repoKey")
                .repoType("git")
                .repoSlug("repository")
                .build();
    }

    @Test
    public void applicationToJson() {
        Executable exec = () -> Application.builder().name("app").build().toJson();

        Assertions.assertDoesNotThrow(exec);
    }

    @Test
    public void applicationValidJson() throws IOException {
        Application application = Application.builder().name("app").build();

        Path path = Paths.get("src", "test", "resources", "io", "spinnaker", "pipelinebuilder", "test",
                "application", "minimalApplication.json");

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> appAsMap = objectMapper.readValue(application.toJson(), Map.class);
        Map<String, Object> expectedAsMap = objectMapper.readValue(Files.readString(path), Map.class);

        Assertions.assertEquals(appAsMap, expectedAsMap);
    }

    @Test
    public void applicationWithPermissionsValidJson() throws IOException {
        ApplicationPermissions permissions = ApplicationPermissions.builder()
                .executeGroups(List.of("aGroup"))
                .readGroups(List.of("0444"))
                .writeGroups(List.of("writers", "otherWriters"))
                .build();
        Application application = Application.builder().name("app").permissions(permissions).build();

        Path path = Paths.get("src", "test", "resources", "io", "spinnaker", "pipelinebuilder", "test",
                "application", "minimalApplicationWithPermissions.json");

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> appAsMap = objectMapper.readValue(application.toJson(), Map.class);
        Map<String, Object> expectedAsMap = objectMapper.readValue(Files.readString(path), Map.class);

        Assertions.assertEquals(appAsMap, expectedAsMap);
    }
}
