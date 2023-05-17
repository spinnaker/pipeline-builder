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
