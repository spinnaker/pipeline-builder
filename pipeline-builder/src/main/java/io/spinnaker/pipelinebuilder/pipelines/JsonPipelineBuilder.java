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

package io.spinnaker.pipelinebuilder.pipelines;

import io.spinnaker.pipelinebuilder.json.Pipeline;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.CharStreams;

import lombok.Getter;

/**
 * JsonPipelineBuilder is a builder that will allow for building a Spinnaker
 * pipeline.
 */
public abstract class JsonPipelineBuilder {

    /**
     * This (optional) string is included in the computation of each pipeline's
     * UUID.
     * It lets us create 2 identical pipelines in the same Spinnaker
     * environment without having their IDs collide.  If you use this feature,
     * make sure to provide the same salt string to all pipelines of the same
     * app.
     */
    @Getter private String salt = "";

    /**
     * A way to set the `application` field on the Pipeline object, needed if
     * it is uploaded using the API and not just pasted in the UI.
     */
    @Getter private String application = null;

    /**
     * @return A unique name identifying this pipeline, also used to generate its storage UUID.
     */
    public abstract String getUniqueName();

    /**
     * @return A built Pipeline object, ready to be serialized to JSON.
     */
    protected abstract Pipeline buildPipeline();

    /**
     * build returns a new Pipeline that can be converted to JSON using the
     * toJson method
     */
    public Pipeline build() {
        final Pipeline generatedPipeline = buildPipeline();

        // generate deterministic ID based on the "unique name" associated with this pipeline builder
        generatedPipeline.setId(computePipelineId());

        if (!Strings.isNullOrEmpty(application)) {
            generatedPipeline.setApplication(application);
        }

        return generatedPipeline;
    }

    private String computePipelineId() {
        // generate pipeline ID based on unique name + salt
        final String saltedUniqueName = salt + getUniqueName();
        return UUID.nameUUIDFromBytes(saltedUniqueName.getBytes(StandardCharsets.UTF_8)).toString();
    }

    /**
     * Used to generate a pipeline ID based on some salt and unique name of the pipeline.
     *
     * An empty salt will be used if no salt has been set using {@link #setSalt}
     *
     * @return the result of calling `computePipelineId` on an object built from the provided pipeline builder class.
     */
    protected String computePipelineIdForClass(Class<? extends JsonPipelineBuilder> builderClass) {
        try {
            Constructor<? extends JsonPipelineBuilder> constructor = builderClass.getDeclaredConstructor(new Class[0]);
            JsonPipelineBuilder builder = constructor.newInstance();
            builder.setApplication(getApplication());
            builder.setSalt(getSalt());
            return builder.computePipelineId();
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate builder of type " + builderClass.getSimpleName(), e);
        }
    }

    /**
     * @param uniqueArtifactName A name identifying this artifact uniquely.
     * @return a UUID string identifier that can be used for an artifact definition.
     */
    protected String computeStableIdForArtifact(final String uniqueArtifactName) {
        String uuidInputString = getUniqueName() + uniqueArtifactName;
        return UUID.nameUUIDFromBytes(uuidInputString.getBytes(StandardCharsets.UTF_8)).toString();
    }

    /**
     * Reads an embedded resource and returns its contents
     * @param classLoader the ClassLoader to fetch the resource from
     * @param resourceName the file name
     * @return the resource contents
     */
    protected String getResourceContents(ClassLoader classLoader, String resourceName) {
        try {
            InputStream inputStream = classLoader.getResourceAsStream(resourceName);
            return CharStreams.toString(new InputStreamReader(inputStream, Charsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Failed to read resource", e);
        }
    }

    /* manual setters so that we can return `this` and chain them */

    public <T extends JsonPipelineBuilder> T setApplication(final String application) {
        this.application = application;
        return (T) this;
    }

    public <T extends JsonPipelineBuilder> T setSalt(final String salt) {
        this.salt = salt;
        return (T) this;
    }
}
