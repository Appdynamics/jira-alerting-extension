/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 */
package com.appdynamics.extensions.jira.common;

import com.appdynamics.TaskInputArgs;
import com.appdynamics.extensions.crypto.CryptoUtil;
import com.appdynamics.extensions.http.Http4ClientBuilder;
import com.appdynamics.extensions.jira.Configuration;
import com.appdynamics.extensions.jira.Proxy;
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
    public static final String UPDATE_ISSUE_REST_URL = "rest/api/2/issue/%s";
    public static final String FORWARD_SLASH = "/";
    private static Logger logger = Logger.getLogger(HttpHandler.class);
    final Configuration config;

    public HttpHandler(Configuration config) {
        this.config = config;
    }

    public String postAlertDataToJira(String data) {
        if (data != null) {
            CloseableHttpClient httpClient = buildHttpClient();
            CloseableHttpResponse response = null;
            try {
                String targetUrl = buildCreateUrl();
                logger.info("Posting data to create issue Jira at: " + targetUrl);
                logger.debug("Posting data to JIRA:" + data);
                HttpPost post = new HttpPost(targetUrl);
                addRequestHeaders(post);
                post.setEntity(new StringEntity(data));
                response = httpClient.execute(post);
                int statusCode = response.getStatusLine().getStatusCode();
                String responseString = EntityUtils.toString(response.getEntity());
                if (statusCode == HttpURLConnection.HTTP_OK || statusCode == HttpURLConnection.HTTP_CREATED) {
                    logger.info("Data successfully posted to Jira to create the issue ");
                    logger.debug("Jira response " + responseString);
                    return responseString;
                }
                logger.error("Data post to Jira failed with status " + statusCode + " and error message[" + responseString + "]");
            } catch (Exception e) {
                logger.error("Error while posting data to Jira ", e);
            } finally {
                closeResponse(response);
                closeHttpClient(httpClient);
            }
        }
        return null;
    }

    public void putAlertDataToJira(String data, String issueId) {
        if (data != null) {
            CloseableHttpClient httpClient = buildHttpClient();
            CloseableHttpResponse response = null;
            try {
                String targetUrl = buildUpdateUrl(issueId);
                logger.info("Posting data to Jira to update issue at " + targetUrl);
                logger.debug("Updating existing JIRA issue: issue id:" + issueId + " data:" + data);
                HttpPut put = new HttpPut(targetUrl);
                addRequestHeaders(put);

                put.setEntity(new StringEntity(data));
                response = httpClient.execute(put);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpURLConnection.HTTP_OK || statusCode == HttpURLConnection.HTTP_NO_CONTENT) {
                    logger.info("Data successfully posted to Jira to update the issue " + issueId);
                } else {
                    logger.error("Data post to Jira to upate issue:" + issueId + " failed with status " + statusCode);
                }
            } catch (Exception e) {
                logger.error("Error while posting data to Jira ", e);
            } finally {
                closeResponse(response);
                closeHttpClient(httpClient);
            }
        }
    }

    private HttpRequestBase addRequestHeaders(HttpRequestBase httpMethod) {
        httpMethod.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        StringBuilder sb = new StringBuilder();
        sb.append(config.getUsername()).append(":").append(getPassword(config.getPassword(), config.getEncryptedPassword()));
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
        server.put(TaskInputArgs.PASSWORD, getPassword(config.getPassword(), config.getEncryptedPassword()));
        list.add(server);

        Map connectionProps = new HashMap();
        connectionProps.put("socketTimeout", config.getSocketTimeout());
        connectionProps.put("connectTimeout", config.getConnectTimeout());
        map.put("connection", connectionProps);

        HashMap proxyProps = new HashMap();
        if(config.getProxy() != null) {
            Proxy proxy = config.getProxy();
            proxyProps.put(TaskInputArgs.URI, proxy.getUri());
            proxyProps.put(TaskInputArgs.USER, proxy.getUser());
            proxyProps.put(TaskInputArgs.PASSWORD, getPassword(proxy.getPassword(), proxy.getEncryptedPassword()));
            map.put("proxy", proxyProps);
        }

        return map;
    }

    private String getPassword(String password, String passwordEncrypted) {

        Map<String, String> map = new HashMap<String, String>();

        if (password != null) {
            logger.debug("Using provided password");
            map.put(TaskInputArgs.PASSWORD, password);
        }

        if (passwordEncrypted != null) {
            logger.debug("Using provided passwordEncrypted");
            map.put(TaskInputArgs.PASSWORD_ENCRYPTED, passwordEncrypted);
            map.put(TaskInputArgs.ENCRYPTION_KEY, config.getEncryptionKey());
        }

        String plainPassword = CryptoUtil.getPassword(map);

        return plainPassword;
    }

    private String buildCreateUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(config.getDomain());
        sb.append(FORWARD_SLASH).append(CREATE_ISSUE_REST_URL);
        return sb.toString();
    }

    private String buildUpdateUrl(String issueId) {
        StringBuilder sb = new StringBuilder();
        sb.append(config.getDomain());
        sb.append(FORWARD_SLASH).append(String.format(UPDATE_ISSUE_REST_URL, issueId));
        return sb.toString();
    }

    private CloseableHttpClient buildHttpClient() {
        Map<String, String> httpConfigMap = createHttpConfigMap();
        logger.debug("Building the httpClient");
        HttpClientBuilder clientBuilder = Http4ClientBuilder.getBuilder(httpConfigMap);
        return clientBuilder.build();

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
