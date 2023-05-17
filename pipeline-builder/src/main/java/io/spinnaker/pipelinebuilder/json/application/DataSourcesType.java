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

package io.spinnaker.pipelinebuilder.json.application;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Different data sources that are grouped into different types based on
 * various concepts of Spinnaker.
 *
 * These data types are peeled from Deck
 * @see <a href="https://github.com/spinnaker/deck/blob/70b1ded668f557a8304fd2d4b9cc43ea93dd5c5d/packages/core/src/application/service/ApplicationDataSourceRegistry.ts#L6-L14">ApplicationDataSourceRegistry</a>
 */
public enum DataSourcesType {
    ENVIRONMENTS("environments"),
    SERVER_GROUPS("serverGroups"),
    EXECUTIONS("executions"),
    LOAD_BALANCERS("loadBalancers"),
    SECURITY_GROUPS("securityGroups"),
    TASKS("tasks"),
    CONFIG("config"),
    ;

    private final String jsonValue;

    DataSourcesType(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    @JsonValue // called by Jackson to serialize an instance of this enum
    public String getJsonValue() {
        return jsonValue;
    }
}
