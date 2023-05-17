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

package io.spinnaker.pipelinebuilder.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@JsonInclude(Include.NON_NULL) // do not serialize null fields (this is for `options` and `hasOptions`)
public class PipelineParameter {
    private final String name;
    private final String label;
    private final Boolean required; // nullable
    private final Boolean pinned;   // nullable
    private final String description;
    @JsonProperty("default") private final String defaultValue;
    @JsonIgnore private final List<String> optionsList; // not named `options` since we use a method to serialize this field

    @Builder // same order as in the UI
    public PipelineParameter(final String name, final String label, final Boolean required, final Boolean pinned,
        final String description, final String defaultValue, final List<String> options) {
        this.name = Objects.requireNonNull(name, "Parameter must have a name");
        this.label = label;
        this.required = required;
        this.pinned = pinned;
        this.description = description;
        this.defaultValue = defaultValue;
        this.optionsList = options;
    }

    @JsonProperty("options") public List<ParameterValue> getWrappedOptions() {
        return optionsList == null ? null // skip serializing if not set, otherwise wrap in objects with single `value` field.
            : optionsList.stream().map(ParameterValue::new).collect(Collectors.toList());
    }

    @SuppressWarnings("squid:S1125") // ignore presence of literal `true`
    @JsonProperty("hasOptions") public Boolean hasOptions() { // `hasOptions` is only ever `true` if present
        return optionsList == null || optionsList.isEmpty() ? null : true;
    }

    @JsonIgnore // do not use this to serialize. We have it just to match the name `options` used in the builder
    public List<String> getOptions() {
        return optionsList;
    }

    @Getter
    @AllArgsConstructor
    public static class ParameterValue {
        private final String value;
    }
}
