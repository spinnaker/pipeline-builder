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

import io.spinnaker.pipelinebuilder.json.application.TrafficGuard;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

/**
 * Tests the builder for {@link TrafficGuard}
 */
public class TrafficGuardTest {
    private static Stream<Object[]> provideTrafficGuards() {
        return Stream.of(
                new Object[]{"account", "stack", "detail", "location", true},
                new Object[]{"otherAccount", "newStack", "someDetail", "thisLocation", false}
        );
    }

    @ParameterizedTest(name = "account={0}, stack={1}, detail={2}, location={3}, enabled={4}")
    @MethodSource("provideTrafficGuards")
    public void randomValues(String account, String stack, String detail, String location, boolean enabled) throws Throwable {
        Executable exec = () -> TrafficGuard.builder()
                .account(account)
                .stack(stack)
                .detail(detail)
                .location(location)
                .enabled(enabled)
                .build();

        exec.execute();
    }
}
