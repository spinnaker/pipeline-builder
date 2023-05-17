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
import io.spinnaker.pipelinebuilder.json.enums.FailureStrategy;
import io.spinnaker.pipelinebuilder.json.stages.model.StageTypes;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests the booleans in {@link FailureStrategy}
 */
public class FailureStrategyTests {

    @ParameterizedTest(name = "failPipeline={0}, completeOtherBranchesThenFail={1}, continuePipeline={2}")
    @MethodSource("provideBooleanValues")
    public void noNameThrows(Boolean failPipeline, Boolean completeOtherBranchesThenFail, Boolean continuePipeline) {
        FailureStrategy failureStrategy = FailureStrategy.buildCustom(failPipeline, completeOtherBranchesThenFail, continuePipeline);
        Stage stage = Stage.builder()
            .name("wait stage")
            .type(StageTypes.WAIT)
            .onFailure(failureStrategy)
            .build();
        Assertions.assertEquals(failPipeline, stage.get("failPipeline"));
        Assertions.assertEquals(completeOtherBranchesThenFail, stage.get("completeOtherBranchesThenFail"));
        Assertions.assertEquals(continuePipeline, stage.get("continuePipeline"));
    }

    /**
     * @return a stream of all possible triplets of booleans
     */
    private static Stream<Object[]> provideBooleanValues() {
        return IntStream.range(0, 8).boxed()
            .map(i -> new Object[] { (i & 4) != 0, (i & 2) != 0, (i & 1) != 0 });
    }
}
