package io.spinnaker.pipelinebuilder.json.artifacts;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Base64;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class Base64ArtifactDefinitionTest {
    @ParameterizedTest
    @MethodSource("referenceArguments")
    public void reference(String content, String expected, Boolean shouldEncode) {
        Base64ArtifactDefinition artifact = Base64ArtifactDefinition.builder()
                .shouldEncode(shouldEncode)
                .contents(content)
                .build();

        byte[] bytes = content.getBytes();
        String reference = artifact.getReference();
        // resolvedShouldEncode should consider whether we planned to encode,
        // and account for it being null.
        //
        // The default value for shouldEncode is true, so if null was passed in,
        // we need to ensure that resolvedShouldEncode is true.
        Boolean resolvedShouldEncode = shouldEncode == null || shouldEncode;
        // didContentBytesGetEncoded compares the bytes based on whether it
        // encoded the reference or not. So if the reference was base64 encoded,
        // they should be different, and same otherwise.
        Boolean didContentBytesGetEncoded = !Arrays.equals(bytes, reference.getBytes());
        assertEquals(resolvedShouldEncode, didContentBytesGetEncoded);
        assertEquals(expected, reference);
    }

    public static Stream<Arguments> referenceArguments() {
        String content = "hello world";
        String contentBase64 = Base64.getEncoder().encodeToString(content.getBytes());

        return Stream.of(
                Arguments.of(content, contentBase64, null),
                Arguments.of(content, contentBase64, true),
                Arguments.of(content, content, false)
        );
    }
}
