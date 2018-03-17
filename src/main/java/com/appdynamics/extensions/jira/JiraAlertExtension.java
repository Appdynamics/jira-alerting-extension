/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 */
package com.appdynamics.extensions.jira;

import com.appdynamics.extensions.alerts.customevents.Event;
import com.appdynamics.extensions.alerts.customevents.EventBuilder;
import com.appdynamics.extensions.jira.api.AlertBuilder;
import com.appdynamics.extensions.jira.api.AlertForCreatingIssue;
import com.appdynamics.extensions.jira.api.AlertForUpdatingIssue;
import com.appdynamics.extensions.jira.common.FileSystemStore;
import com.appdynamics.extensions.jira.common.HttpHandler;
import com.appdynamics.extensions.yml.YmlReader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by balakrishnavadavalasa on 18/08/16.
 */
public class JiraAlertExtension {
    public static final String CONFIG_FILENAME = "." + File.separator + "conf" + File.separator + "config.yml";

    private static Logger logger = Logger.getLogger(JiraAlertExtension.class);

    final AlertBuilder alertBuilder = new AlertBuilder();
    final Configuration config;

    public JiraAlertExtension(Configuration config) {
        this.config = config;
    }

    public static void main(String[] args) {
        logger.info("*****************START******************");
        String msg = "Using Jira Alerting Extension Version [" + getImplementationTitle() + "]";
        logger.info(msg);

        if (args == null || args.length == 0) {
            logger.error("No arguments passed to the extension, exiting the program.");
            return;
        }
        logger.debug("Arguments passed :: " + Arrays.asList(args));
        Configuration config;
        try {
            config = YmlReader.readFromFile(CONFIG_FILENAME, Configuration.class);
            JiraAlertExtension alertExtension = new JiraAlertExtension(config);
            boolean status = alertExtension.processAnEvent(args);
            if (status) {
                logger.info("Jira Extension execution completed.");
                logger.info("******************END******************");
                return;
            }
        } catch (Exception e) {
            logger.error("Error processing an event ", e);
        }
        logger.error("Jira Extension completed with errors");
    }

    private static String getImplementationTitle() {
        return JiraAlertExtension.class.getPackage().getImplementationTitle();
    }

    public boolean processAnEvent(String[] args) {
        Event event = new EventBuilder().build(args);
        if (event != null) {
            try {
                HttpHandler handler = new HttpHandler(config);
                String jsonPayload;
                String issueId = FileSystemStore.INSTANCE.getFromStore(alertBuilder.getEventId(event));
                if (issueId == null) {
                    // create
                    AlertForCreatingIssue alert = alertBuilder.buildAlert(event, config);
                    jsonPayload = alertBuilder.convertIntoJsonString(alert);
                    String response = handler.postAlertDataToJira(jsonPayload);
                    if (!Strings.isNullOrEmpty(response)) {
                        FileSystemStore.INSTANCE.putInStore(alert.getAlertId(), getIssueIDFromResponse(response));
                    }
                } else {
                    //update
                    AlertForUpdatingIssue alertUpdate = alertBuilder.buildAlertForUpdatingIssue(event);
                    jsonPayload = alertBuilder.convertIntoJsonString(alertUpdate);
                    handler.putAlertDataToJira(jsonPayload, issueId);
                }
                return true;
            } catch (Exception e) {
                logger.error("Error while processing event data " + event, e);
            } finally {
                FileSystemStore.INSTANCE.closeStore();
            }
        }
        return false;
    }

    private String getIssueIDFromResponse(String response) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = null;
        try {
            node = mapper.readTree(response);
        } catch (IOException e) {
            logger.error("Error while reading the Json tree: " + response, e);
        }
        return node.get("id").textValue();
    }
}
