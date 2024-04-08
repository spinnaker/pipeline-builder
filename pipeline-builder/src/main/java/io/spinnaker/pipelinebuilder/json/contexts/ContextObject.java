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

package io.spinnaker.pipelinebuilder.json.contexts;

/**
 * Marker interface for context objects, which are used to configure stages.
 * Using objects with typed fields instead of Map<String, Object> provides better compile-time safety
 * and allows for better IDE support.
 * Any class can be a context object, as long as it is marked as such by implementing this interface.
 */
public interface ContextObject {
}
