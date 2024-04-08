/*
 * Copyright 2024 Apple, Inc.
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

package com.apple.spinnaker.examples;

import io.spinnaker.pipelinebuilder.json.Pipeline;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FullPipelineCreationTest {

    @Test
    public void createTutorialPipeline() {
        TutorialPipelineBuilder builder = new TutorialPipelineBuilder();
        builder.setApplication("my-application");
        Pipeline pipeline = builder.build();
        String asJson = pipeline.toJson();
        System.out.println(asJson);

        Assertions.assertTrue(asJson.contains("\"1049ea1a-874a-3d4b-8b29-e53d814df48f\""));
    }
}
