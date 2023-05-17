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

package io.spinnaker.pipelinebuilder.json.stages.model;

import io.spinnaker.pipelinebuilder.json.contexts.CheckConditionsContext;
import io.spinnaker.pipelinebuilder.json.contexts.EvalVarsContext;
import io.spinnaker.pipelinebuilder.json.contexts.WaitContext;
import io.spinnaker.pipelinebuilder.json.contexts.WebhookContext;
import lombok.experimental.UtilityClass;

/**
 * Various stage types used in Spinnaker.
 * These are not necessarily exhaustive, and their use is not required either:
 * they are just strings after all, but they help with not having to remember
 * the internal "type" key for each kind of stage.
 */
@UtilityClass
public class StageTypes {

    // common stages, not linked to a cloud provider:
    public final String CANARY_ANALYSIS = "kayentaCanary";
    /**
     * Evaluates a list of SpEL expression to dictate whether the
     * pipeline should advance to the next stage.
     *
     * @see CheckConditionsContext
     */
    public final String CHECK_PRECONDITIONS = "checkPreconditions";
    /**
     * Used to define variables which can be used by other stages.
     *
     * @see EvalVarsContext
     */
    public final String EVALUATE_VARIABLES = "evaluateVariables";
    /** Find and bind artifacts from another execution. */
    public final String FIND_ARTIFACT_FROM_EXECUTION = "findArtifactFromExecution";
    /**
     * Runs a new Spinnaker pipeline, potentially waiting for it to finish
     * before moving to the next stage
     */
    public final String PIPELINE = "pipeline";
    /**
     * Wait a given amount of time before moving to the next stage.
     *
     * @see WaitContext
     */
    public final String WAIT = "wait";
    /**
     * Run a custom HTTP(S) request.
     *
     * @see WebhookContext
     */
    public final String WEBHOOK = "webhook";
    /** Saves pipelines defined in an artifact. */
    public final String SAVE_PIPELINES_FROM_ARTIFACT = "savePipelinesFromArtifact";
    /** Waits for user input before continuing */
    public final String MANUAL_JUDGMENT = "manualJudgment";

    @UtilityClass
    public class Kubernetes {
        /**
         * Bake a manifest (or multi-doc manifest set) using a template
         * renderer such as Helm.
         */
        public final String BAKE_MANIFEST = "bakeManifest";
        /**
         * Destroy a Kubernetes object created from a manifest.
         * If multiple label selectors are specified, they are combined with
         * the logical AND operator. 
         *
         * @see <a href="https://kubernetes.io/docs/concepts/overview/working-with-objects/labels/#label-selectors">Kubernetes reference</a>
         */
        public final String DELETE_MANIFEST = "deleteManifest";
        /** Deploy a Kubernetes manifest. */
        public final String DEPLOY_MANIFEST = "deployManifest";
        /** Find artifacts from a Kubernetes resource. */
        public final String FIND_ARTIFACTS_FROM_RESOURCE = "findArtifactsFromResource";
        public final String ROLLING_RESTART_MANIFEST = "rollingRestartManifest";
        /** Rollback a manifest a target number of revisions. */
        public final String UNDO_ROLLOUT_MANIFEST = "undoRolloutManifest";
        /** Run a Job manifest. */
        public final String RUN_JOB_MANIFEST = "runJobManifest";
    }

    @UtilityClass
    public class Jenkins {
        /**
         * Run the specified job in Jenkins.
         *
         * You must <a href="https://spinnaker.io/docs/setup/other_config/ci/jenkins/">set up</a> Jenkins in order to use this stage. Once Jenkins is
         * configured, your Jenkins master and available jobs are automatically
         * populated in the respective drop-down menus.
         */
        public final String JENKINS = "jenkins";
    }
}
