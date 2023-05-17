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
import io.spinnaker.pipelinebuilder.json.application.ApplicationPermissions;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

/**
 * Tests the builder for {@link ApplicationPermissions}
 */
public class ApplicationPermissionsTest {
    private ObjectMapper objectMapper;

    private static Stream<Object[]> providePermissions() {
        return Stream.of(
                new Object[]{PermissionType.READ, List.of("read")},
                new Object[]{PermissionType.WRITE, List.of("someRole")},
                new Object[]{PermissionType.EXECUTE, List.of("execute")},
                new Object[]{PermissionType.EXECUTE, List.of("null", "notNull", "more")}
        );
    }

    @Test
    public void permissionsDefaultNull() {
        Assertions.assertEquals(null, Application.builder().name("this is a name").build().getPermissions());
    }

    @ParameterizedTest(name = "type={0}, roles={1}")
    @MethodSource("providePermissions")
    public void addPermissions(PermissionType type, List<String> roles) {
        ApplicationPermissions permissions;

        switch (type) {
            case READ:
                permissions = ApplicationPermissions.builder().readGroups(roles).build();
                Assertions.assertEquals(roles, permissions.getReadGroups());
                Assertions.assertEquals(permissions.getWriteGroups(), List.of());
                break;
            case WRITE:
                permissions = ApplicationPermissions.builder().writeGroups(roles).build();
                Assertions.assertEquals(roles, permissions.getWriteGroups());
                Assertions.assertNotEquals(roles, permissions.getExecuteGroups());
                break;
            case EXECUTE:
                permissions = ApplicationPermissions.builder().executeGroups(roles).build();
                Assertions.assertEquals(roles, permissions.getExecuteGroups());
                Assertions.assertNotEquals(roles, permissions.getReadGroups());
                break;
        }
    }

    @ParameterizedTest(name = "type={0}, roles={1}")
    @MethodSource("providePermissions")
    public void addPermission(PermissionType type, List<String> roles) {
        ApplicationPermissions.ApplicationPermissionsBuilder permissionsBuilder;
        ApplicationPermissions permissions;

        switch (type) {
            case READ:
                permissionsBuilder = ApplicationPermissions.builder();
                roles.stream().forEach((r) -> permissionsBuilder.read(r));
                permissions = permissionsBuilder.build();
                Assertions.assertEquals(roles, permissions.getReadGroups());
                Assertions.assertEquals(permissions.getWriteGroups(), List.of());
                break;
            case WRITE:
                permissionsBuilder = ApplicationPermissions.builder();
                roles.stream().forEach((r) -> permissionsBuilder.write(r));
                permissions = permissionsBuilder.build();
                Assertions.assertEquals(roles, permissions.getWriteGroups());
                Assertions.assertNotEquals(roles, permissions.getExecuteGroups());
                break;
            case EXECUTE:
                permissionsBuilder = ApplicationPermissions.builder();
                roles.stream().forEach((r) -> permissionsBuilder.execute(r));
                permissions = permissionsBuilder.build();
                Assertions.assertEquals(roles, permissions.getExecuteGroups());
                Assertions.assertNotEquals(roles, permissions.getReadGroups());
                break;
        }
    }

    enum PermissionType {
        READ("READ"),
        WRITE("WRITE"),
        EXECUTE("EXECUTE"),
        ;

        private final String jsonValue;

        PermissionType(String jsonValue) {
            this.jsonValue = jsonValue;
        }
    }
}
