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

package io.spinnaker.pipelinebuilder.json.enums;

import lombok.Getter;

/**
 * A strategy to perform in the event that a pipeline has failed.
 */
public class FailureStrategy {
    /**
     * Immediately halts execution of all running stages and fails the entire
     * execution.
     */
    public static final FailureStrategy HALT_ENTIRE_PIPELINE = buildCustom(true, false, false);
    /**
     * Prevents any stages that depend on this stage from running, but allows
     * other branches of the pipeline to run.
     */
    public static final FailureStrategy HALT_CURRENT_BRANCH = buildCustom(false, false, false);
    /**
     * Prevents any stages that depend on this stage from running, but allows
     * other branches of the pipeline to run.
     *
     * The pipeline will be marked as failed once complete.
     */
    public static final FailureStrategy HALT_BRANCH_AND_FAIL_PIPELINE = buildCustom(false, true, false);
    /**
     * Continues execution of downstream stages, marking this stage as
     * failed/continuing.
     */
    public static final FailureStrategy IGNORE_FAILURE = buildCustom(false, false, true);

    @Getter private final Boolean failPipeline;
    @Getter private final Boolean completeOtherBranchesThenFail;
    @Getter private final Boolean continuePipeline;

    private FailureStrategy(Boolean failPipeline, Boolean completeOtherBranchesThenFail, Boolean continuePipeline) {
        this.failPipeline = failPipeline;
        this.completeOtherBranchesThenFail = completeOtherBranchesThenFail;
        this.continuePipeline = continuePipeline;
    }

    /**
     * Builds a custom `FailureStrategy` object.
     *
     * Yes this is the same as the constructor, but calling the public method
     * "buildCustom" makes it clear that this is reserved for building a
     * special type of `FailureStrategy`. Most use cases should rely on the
     * pre-built public instances.
     */
    public static FailureStrategy buildCustom(Boolean failPipeline, Boolean completeOtherBranchesThenFail, Boolean continuePipeline) {
        return new FailureStrategy(failPipeline, completeOtherBranchesThenFail, continuePipeline);
    }
}
