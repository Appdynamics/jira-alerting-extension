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
package com.appdynamics.extensions.jira;

import com.appdynamics.extensions.alerts.customevents.Event;
import com.appdynamics.extensions.alerts.customevents.EventBuilder;
import com.appdynamics.extensions.http.Response;
import com.appdynamics.extensions.jira.api.Alert;
import com.appdynamics.extensions.jira.api.AlertBuilder;
import com.appdynamics.extensions.jira.common.HttpHandler;
import com.appdynamics.extensions.yml.YmlReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.log4j.Logger;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.Arrays;

/**
 * Created by balakrishnavadavalasa on 18/08/16.
 */
public class JiraAlertExtension {
    public static final String CONFIG_FILENAME = "." + File.separator + "conf" + File.separator + "config.yml";

    private static Logger logger = Logger.getLogger(JiraAlertExtension.class);

    final AlertBuilder alertBuilder = new AlertBuilder();
    Configuration config;

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
                logger.info("Jira Extension completed successfully.");
                logger.info("******************END******************");
                return;
            }
        } catch (Exception e) {
            logger.error("Error processing an event", e);
        }
        logger.error("Jira Extension completed with errors");
    }

    private static String getImplementationTitle() {
        return JiraAlertExtension.class.getPackage().getImplementationTitle();
    }

    public boolean processAnEvent(String[] args) {
        Event event = new EventBuilder().build(args);
        if (event != null) {
            Alert alert = alertBuilder.buildAlert(event, config);
            if (alert != null) {
                try {
                    HttpHandler handler = new HttpHandler(config);
                    String json = alertBuilder.convertIntoJsonString(alert);
                    logger.debug("Json posted to Jira ::" + json);
                    Response response = handler.postAlert(json);
                    if (response != null && response.getStatus() == HttpURLConnection.HTTP_OK || response.getStatus() == HttpURLConnection.HTTP_CREATED) {
                        logger.info("Data successfully posted to Jira");
                        return true;
                    }
                    logger.error("Data post failed");
                } catch (JsonProcessingException e) {
                    logger.error("Cannot serialized object into Json." + e);
                }
            }
        }
        return false;
    }
}
