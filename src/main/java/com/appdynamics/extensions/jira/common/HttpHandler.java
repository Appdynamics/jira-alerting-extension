/**
 * Copyright 2016 AppDynamics, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appdynamics.extensions.jira.common;

import com.appdynamics.TaskInputArgs;
import com.appdynamics.extensions.http.Http4ClientBuilder;
import com.appdynamics.extensions.jira.Configuration;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHandler {

    public static final String CREATE_ISSUE_REST_URL = "rest/api/2/issue";
    public static final String UPDATE_ISSUE_REST_URL = "rest/api/2/issue/%s/comment";
    public static final String FORWARD_SLASH = "/";
    private static Logger logger = Logger.getLogger(HttpHandler.class);
    final Configuration config;

    private CloseableHttpClient httpClient;

    public HttpHandler(Configuration config) {
        this.config = config;
    }

    public String postAlertDataToJira(String data, String issueId) {
        if (data != null) {
            buildHttpClient();
            try {
                String targetUrl = buildTargetUrl(issueId);
                if (issueId != null) {
                    updateJiraTicket(targetUrl, issueId, data);
                    return null;
                } else {
                    String responseString = createNewJiraTicket(targetUrl, data);
                    return responseString;
                }
            } catch (Exception e) {
                logger.error("Error while posting data to Jira ", e);
            } finally {
                closeHttpClient(httpClient);
            }

        }
        return null;
    }

    private String createNewJiraTicket(String targetUrl, String data) throws IOException {
        CloseableHttpResponse response = null;
        try {
            logger.info("Posting data to create issue Jira at: " + targetUrl);
            logger.debug("Posting data to JIRA:" + data);
            HttpPost post = new HttpPost(targetUrl);
            addRequestHeaders(post);
            post.setEntity(new StringEntity(data));
            response = httpClient.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseString = EntityUtils.toString(response.getEntity());
            if (statusCode == HttpURLConnection.HTTP_CREATED) {
                logger.info("Data successfully posted to Jira to create the issue ");
                logger.debug("Jira response " + responseString);
                return responseString;
            }
            logger.error("Data post to Jira failed with status " + statusCode + " and error message[" + responseString + "]");
        } finally {
            closeResponse(response);
        }
        return null;
    }

    private void updateJiraTicket(String targetUrl, String issueId, String data) throws IOException {
        CloseableHttpResponse response = null;
        try {
            logger.info("Posting data to Jira to update issue at " + targetUrl);
            logger.debug("Updating existing JIRA issue: issue id:" + issueId + " data:" + data);
            HttpPut put = new HttpPut(targetUrl);
            addRequestHeaders(put);

            put.setEntity(new StringEntity(data));
            response = httpClient.execute(put);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpURLConnection.HTTP_NO_CONTENT) {
                logger.info("Data successfully posted to Jira to update the issue " + issueId);
            } else {
                logger.error("Data post to Jira to upate issue:" + issueId + " failed with status " + statusCode);
            }
        } finally {
            closeResponse(response);
        }
    }


    private HttpRequestBase addRequestHeaders(HttpRequestBase httpMethod) {
        httpMethod.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        StringBuilder sb = new StringBuilder();
        sb.append(config.getUsername()).append(":").append(config.getPassword());
        String authHeader = "Basic " + new String(Base64.encodeBase64(sb.toString().getBytes()));
        httpMethod.addHeader(HttpHeaders.AUTHORIZATION, authHeader);

        return httpMethod;
    }

    private Map createHttpConfigMap() {
        Map map = new HashMap();
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        map.put("servers", list);
        HashMap<String, String> server = new HashMap<String, String>();
        server.put(TaskInputArgs.URI, config.getDomain());
        server.put(TaskInputArgs.USER, config.getUsername());
        server.put(TaskInputArgs.PASSWORD, config.getPassword());
        server.put(TaskInputArgs.PASSWORD_ENCRYPTED, config.getEncryptedPassword());
        list.add(server);

        map.put(TaskInputArgs.ENCRYPTION_KEY, config.getEncryptionKey());


        Map connectionProps = new HashMap();
        connectionProps.put("socketTimeout", config.getSocketTimeout());
        connectionProps.put("connectTimeout", config.getConnectTimeout());
        map.put("connection", connectionProps);

        HashMap proxyProps = new HashMap();
        proxyProps.put(TaskInputArgs.URI, config.getProxy().getUri());
        proxyProps.put(TaskInputArgs.USER, config.getProxy().getUser());
        proxyProps.put(TaskInputArgs.PASSWORD, config.getProxy().getPassword());
        proxyProps.put(TaskInputArgs.PASSWORD_ENCRYPTED, config.getProxy().getEncryptedPassword());
        map.put("proxy", proxyProps);

        return map;
    }

    private String buildTargetUrl(String issueId) {
        StringBuilder sb = new StringBuilder();
        sb.append(config.getDomain());
        if (issueId != null) {
            sb.append(FORWARD_SLASH).append(String.format(UPDATE_ISSUE_REST_URL, issueId));
        } else {
            sb.append(FORWARD_SLASH).append(CREATE_ISSUE_REST_URL);
        }

        return sb.toString();
    }

    private void buildHttpClient() {
        Map<String, String> httpConfigMap = createHttpConfigMap();
        logger.debug("Building the httpClient");
        HttpClientBuilder clientBuilder = Http4ClientBuilder.getBuilder(httpConfigMap);
        httpClient = clientBuilder.build();
    }

    private void closeResponse(CloseableHttpResponse response) {
        if (response != null) {
            try {
                response.close();
            } catch (IOException e) {
                logger.error("Error while closing the HttpResponse ", e);
            }
        }
    }

    private void closeHttpClient(CloseableHttpClient httpClient) {
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (IOException e) {
                logger.error("Error while closing the HttpClient ", e);
            }
        }
    }
}
