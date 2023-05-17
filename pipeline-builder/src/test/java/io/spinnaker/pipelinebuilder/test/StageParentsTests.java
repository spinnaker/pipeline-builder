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

import io.spinnaker.pipelinebuilder.json.Stage;
import io.spinnaker.pipelinebuilder.json.stages.model.StageTypes;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests the parent ID(s) logic for {@link Stage}.
 */
public class StageParentsTests {

    public static final String ID_1 = "static-1";
    public static final String ID_2 = "static-2";

    @ParameterizedTest(name = "parentId={0}, parentIds={1}, parentStage={2}, parentStages={3}, valid={4}")
    @MethodSource("provideParentValues")
    public void noNameThrows(String parentId, List<String> parentIds, Stage parentStage, List<Stage> parentStages, boolean valid) throws Throwable {
        Executable exec = () -> Stage.builder()
            .name("wait stage")
            .type(StageTypes.WAIT)
            .parentStageId(parentId)
            .parentStageIds(parentIds)
            .parentStage(parentStage)
            .parentStages(parentStages)
            .build();
        if (valid) {
            exec.execute();
        } else {
            Assertions.assertThrows(IllegalArgumentException.class, exec);
        }
    }

    private static Stream<Object[]> provideParentValues() {
        Stage s1 = Stage.builder().name("s1").id(ID_1).type("wait").build();
        Stage s2 = Stage.builder().name("s2").id(ID_2).type("wait").build();
        return Stream.of(
            new Object[] { null, null, null, null, true }, // no parent
            new Object[] { ID_1, null, null, null, true }, // single ID
            new Object[] { null, List.of(ID_1, ID_2), null, null, true }, // list of IDs
            new Object[] { null, List.of(ID_1, ID_1), null, null, false }, // list of IDs with duplicates
            new Object[] { null, List.of(), null, null, true }, // empty list of IDs
            new Object[] { "", List.of(), null, null, false }, // empty ID and empty list of IDs
            new Object[] { "", List.of(ID_1, ID_2), null, null, false }, // empty ID and list of IDs
            new Object[] { ID_1, List.of(ID_1, ID_2), null, null, false }, // ID and list of IDs
            new Object[] { null, null, s1, null, true }, // single stage
            new Object[] { null, null, null, List.of(s1, s2), true }, // list of stages
            new Object[] { null, null, null, List.of(s1, s1, s2), false }, // list of stages with duplicates
            new Object[] { null, null, null, List.of(), true }, // empty list of stages
            new Object[] { ID_1, null, s1, null, false }, // single ID and single stage, same ID
            new Object[] { ID_1, null, s2, null, false }, // single ID and single stage, different IDs
            new Object[] { null, List.of(ID_1), s2, null, false }, // list of IDs and single stage
            new Object[] { ID_1, null, null, List.of(s2), false }, // single ID and list of stages
            new Object[] { null, List.of(ID_1), null, List.of(s2), false }, // list of IDs and list of stages
            new Object[] { null, List.of(), null, List.of(), false } // empty list of IDs and empty list of stages
            );
    }

    @Test
    public void stageDependsOnItself() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Stage.builder()
            .name("wait stage")
            .type("wait")
            .id("42")
            .parentStageId("42")
            .build());
    }
}
