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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

import lombok.Builder;
import lombok.Getter;

/**
 * DataSources is a collection of various data sources that can be enabled or disabled.
 *
 * Disabling a data source will prevent data from being sent to the client of
 * that particular type
 *
 * A particular data source cannot both be enabled and disabled. Doing so will
 * result in an exception
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // do not serialize null fields
public class DataSources {
    private final List<DataSourcesType> disabled;
    private final List<DataSourcesType> enabled;

    @Builder
    public DataSources(List<DataSourcesType> disabled, List<DataSourcesType> enabled) {
        this.disabled = disabled != null ? disabled : Collections.emptyList();
        this.enabled = enabled != null ? enabled : Collections.emptyList();

        validateSourcesAreNotInBothLists(this.enabled, this.disabled);
    }


    private void validateSourcesAreNotInBothLists(final List<DataSourcesType> enabled, final List<DataSourcesType> disabled) {
        Set<DataSourcesType> enabledSources = new HashSet<>(enabled);
        Set<DataSourcesType> disabledSources = new HashSet<>(disabled);
        Sets.SetView<DataSourcesType> duplicates = Sets.intersection(enabledSources, disabledSources);
        if (!duplicates.isEmpty()) {
            throw new IllegalArgumentException("Some data sources are listed as both enabled and disabled: " + Joiner.on(", ").join(duplicates));
        }
    }
}
