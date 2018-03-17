/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 */
package com.appdynamics.extensions.jira.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.google.common.collect.Lists;

import java.util.*;

@JsonRootName(value = "fields")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AlertForCreatingIssue {

    private Map<String, String> project = new HashMap<String, String>();
    private String summary;
    private String description;
    private Map<String, String> issuetype = new HashMap<String, String>();
    private Map<String, String> priority = new HashMap<String, String>();
    private Component [] components;

    @JsonIgnore
    private String alertId;

    public Map<String, String> getProject() {
        return project;
    }

    public void setProject(Map<String, String> project) {
        this.project = project;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getIssuetype() {
        return issuetype;
    }

    public void setIssuetype(Map<String, String> issuetype) {
        this.issuetype = issuetype;
    }

    public Map<String, String> getPriority() {
        return priority;
    }

    public void setPriority(Map<String, String> priority) {
        this.priority = priority;
    }

    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public Component[] getComponents() {
        return components;
    }

    public void setComponents(Component[] components) {
        this.components = components;
    }
}
