/**
 * Copyright 2016 AppDynamics, Inc.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appdynamics.extensions.jira.common;

import com.appdynamics.TaskInputArgs;
import com.appdynamics.extensions.http.Response;
import com.appdynamics.extensions.http.SimpleHttpClient;
import com.appdynamics.extensions.jira.Configuration;
import com.google.common.base.Strings;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.log4j.Logger;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

public class HttpHandler {

    public static final String CREATE_ISSUE_REST_URL = "rest/api/2/issue";
    public static final String FORWARD_SLASH = "/";
    private static Logger logger = Logger.getLogger(HttpHandler.class);
    final Configuration config;

    public HttpHandler(Configuration config) {
        this.config = config;
    }

    /**
     * Posts the data on Jira Endpoint.
     *
     * @param data
     * @return
     */
    public Response postAlert(String data) {
        Map<String, String> httpConfigMap = createHttpConfigMap();
        logger.debug("Building the httpClient");
        SimpleHttpClient simpleHttpClient = SimpleHttpClient.builder(httpConfigMap)
                .connectionTimeout(config.getConnectTimeout())
                .socketTimeout(config.getSocketTimeout())
                .build();
        String targetUrl = buildTargetUrl();

        // WARN org.apache.commons.httpclient.HttpMethodBase - Cookie rejected: Illegal domain attribute
        // http://stackoverflow.com/questions/7459279/httpclient-warning-cookie-rejected-illegal-domain-attribute
        HttpClientParams clientParams = simpleHttpClient.getHttpClient().getParams();
        clientParams.setBooleanParameter(HttpClientParams.ALLOW_CIRCULAR_REDIRECTS, true);
        clientParams.setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);

        logger.debug("Posting data to Jira at " + targetUrl);
        Response response = simpleHttpClient.target(targetUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .post(data);
        logger.debug("HTTP Response status from Jira " + response.getStatus());
        return response;
    }


    private Map<String, String> createHttpConfigMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(TaskInputArgs.USER, config.getUsername());
        map.put(TaskInputArgs.PASSWORD, config.getPassword());

        if (this.config.getProxy() != null) {
            if (!Strings.isNullOrEmpty(this.config.getProxy().getHost())) {
                map.put(TaskInputArgs.PROXY_HOST, this.config.getProxy().getHost());
            }
            if (!Strings.isNullOrEmpty(this.config.getProxy().getPort())) {
                map.put(TaskInputArgs.PROXY_PORT, this.config.getProxy().getPort());
            }
            if (!Strings.isNullOrEmpty(this.config.getProxy().getUri())) {
                map.put(TaskInputArgs.PROXY_URI, this.config.getProxy().getUri());
            }
            if (!Strings.isNullOrEmpty(this.config.getProxy().getUser())) {
                map.put(TaskInputArgs.PROXY_USER, this.config.getProxy().getUser());
                // Don't put any password if not specified
                if (!Strings.isNullOrEmpty(this.config.getProxy().getPassword())) {
                    map.put(TaskInputArgs.PROXY_PASSWORD, this.config.getProxy().getPassword());
                }
            }
        }
        return map;
    }

    private String buildTargetUrl() {
        StringBuilder sb = new StringBuilder();

        sb.append(config.getDomain());

        sb.append(FORWARD_SLASH).append(CREATE_ISSUE_REST_URL);

        return sb.toString();
    }
}
