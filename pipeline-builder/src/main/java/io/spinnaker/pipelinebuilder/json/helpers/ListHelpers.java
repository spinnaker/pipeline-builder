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

package io.spinnaker.pipelinebuilder.json.helpers;

import java.util.List;

import lombok.experimental.UtilityClass;

/**
 * A helper class around the List class.
 *
 * <p>This class is useful for builders allowing users to pass in a single or list
 * of elements.</p>
 *
 * <h3>Example</h3>
 * <pre>
 * {@literal List<int>} statuses = ListHelper.listWithOneOf(
 *     "statusCodes",
 *     null,
 *     List.of(200, 301, 404, 503),
 *     List.of(501));
 * </pre>
 */
@UtilityClass   // can't be instantiated
public class ListHelpers {

    /**
     * Returns a list of elements based on either a single element or a list of
     * elements, but not both. In some places it is possible to pass in either
     * a single value or a list of values (e.g. parentStage vs parentStages), but
     * internally we always need to build a list even if a single element was
     * provided.
     * @param name the name of the field being set, only used to report errors
     * @param singleValue non-null when a single value is provided for the field.
     *                    Must not be set at the same time as <code>multipleValues</code>.
     * @param multipleValues non-null when a list of values is provided for the field
     *                    Must not be set at the same time as <code>singleValue</code>.
     * @param defaultValue when both <code>singleValue</code> and <code>multipleValues</code>
     *                     are null, use this value instead.
     */
    public static <T> List<T> listWithOneOf(final String name, final T singleValue, final List<T> multipleValues, List<T> defaultValue) {
        if (singleValue != null && multipleValues != null) {
            String message = String.format("Only one of '%s' and '%ss' can be used", name, name);
            throw new IllegalArgumentException(message);
        } else if (singleValue != null) {
            return List.of(singleValue);
        } else if (multipleValues != null) {
            return multipleValues;
        } else {
            return defaultValue;
        }
    }
}
