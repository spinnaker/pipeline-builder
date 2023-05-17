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

package io.spinnaker.pipelinebuilder.json.contexts;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Builder;
import lombok.Getter;

/**
 * WebhookContext is used as input for a given webhook.
 *
 * <h3>Example</h3>
 * <pre>
 * WebhookContext ctx = WebhookContext.builder()
 *     .method(Method.GET)
 *     .url("https://api.example.com/v1/endpoint")
 *     .headers(Map.of("Accept", "application/json"))
 *     .build()
 * </pre>
 */
@Getter
@Builder
@JsonInclude(Include.NON_NULL)
public class WebhookContext implements ContextObject {
    /**
     * HTTP Method definition as defined by the RFC.
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7231#section-4.3">rfc7231</a>
     */
    public enum Method {
        CONNECT,
        DELETE,
        GET,
        HEAD,
        OPTIONS,
        PATCH,
        POST,
        PUT,
        TRACE,
        ;
    }

    /**
     * Describes how the response is handled.
     */
    public enum StatusUrl {
        GET_METHOD("getMethod"),                // GET method against "status" URL
        LOCATION_HEADER("locationHeader"),      // use Location header as the status URL
        WEBHOOK_RESPONSE("webhookResponse"),    // extract status URL from initial webhook response
        ;

        private final String jsonValue;
        StatusUrl(String jsonValue) {
            this.jsonValue = jsonValue;
        }

        @JsonValue
        public String getJsonValue() {
            return jsonValue;
        }
    }

    /** webhook's HTTP method */
    private Method method;
    /** webhook's URL */
    private String url;
    /**
     * Custom headers to pass to the Webhook stage.
     *
     * It should be noted that authentication, API keys, or any sensitive
     * information should not be added.
     */
    @JsonProperty("customHeaders") private Map<String, String> headers;
    /**
     * Comma-separated HTTP status codes (e.g <code>404</code>, <code>502</code>, <code>503</code>) that will cause this
     * webhook stage to fail without retrying.
     */
    private List<Integer> failFastHttpStatuses;

    /**
     * If not checked, we consider the stage succeeded if the webhook returns
     * an HTTP status code 2xx, otherwise it will fail.
     *
     * If checked, it will poll a status url (defined below) to determine the
     * progress of the stage.
     */
    @Builder.Default private Boolean waitForCompletion = false;
    @JsonProperty("statusUrlResolution") @Builder.Default private StatusUrl statusUrl = StatusUrl.GET_METHOD;
    /**
     * JSON path to the status url in the webhook's response JSON. (i.e. <samp>$.buildInfo.url</samp>)
     */
    private String statusUrlJsonPath;
    /**
     * Optional delay (in seconds) to wait before starting to poll the endpoint
     * for monitoring status
     */
    private String waitBeforeMonitor;

    /**
     * You can specify additional status codes here that will cause the monitor
     * to retry (e.g <samp>404, 418</samp>).
     *
     * By default, webhook stages only retry on 429 and 5xx status codes.
     */
    private List<Integer> retryStatusCodes;
    /**
     * JSON path to the status information in the webhook's response JSON (e.g <samp>$.buildInfo.status</samp>).
     *
     * If left empty, a 200 response from the status endpoint will be treated as a success.
     */
    private String statusJsonPath;
    /**
     * JSON path to a descriptive message about the progress in the webhook's
     * response JSON. (e.g. <samp>$.buildInfo.progress</samp>)
     */
    private String progressJsonPath;
    /**
     * Comma-separated list of strings (that will be returned in the response
     * body in the previously defined `statusJsonPath` field) that will be
     * considered as SUCCESS status.
     */
    private String successStatuses;
    /**
     * Comma-separated list of strings (that will be returned in the response
     * body in the previously defined `statusJsonPath` field) that will be
     * considered as CANCELED status.
     */
    private String canceledStatuses;
    /**
     * Comma-separated list of strings (that will be returned in the response
     * body in the previously defined `statusJsonPath` field) that will be
     * considered as TERMINAL status.
     */
    private String terminalStatuses;

    private String cancelEndpoint;
    private Method cancelMethod;
    /**
     * JSON payload to be added to the webhook call when it is called in
     * response to a cancellation.
     */
    private Map<String, Object> cancelPayload;

    /** JSON payload to be added to the webhook call */
    private Map<String, Object> payload;
}
