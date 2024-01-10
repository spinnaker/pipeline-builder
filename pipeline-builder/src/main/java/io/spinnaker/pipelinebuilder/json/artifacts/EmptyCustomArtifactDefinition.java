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

package io.spinnaker.pipelinebuilder.json.artifacts;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * A special custom object case for empty artifacts.
 *
 * Looks like this:
 * <pre>
 *   "defaultArtifact": {"id": "e54c42eb-a7ed-4039-b80b-a9be850bf96b"},
 * </pre>
 */
@JsonInclude(Include.NON_NULL) // don't serialize null fields
public class EmptyCustomArtifactDefinition extends Base64ArtifactDefinition {
    public EmptyCustomArtifactDefinition() {
      super(UUID.randomUUID().toString(), null, "", true);
    }

    // @Builder comes from Base64ArtifactDefinition
    public EmptyCustomArtifactDefinition(String id) {
      super(id, null, "", true);
    }
}
