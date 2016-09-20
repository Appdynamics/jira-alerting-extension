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
package com.appdynamics.extensions.jira.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.HashMap;
import java.util.Map;

@JsonRootName(value = "fields")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AlertForCreatingIssue {

    private Map<String, String> project = new HashMap<String, String>();
    private String summary;
    private String description;
    private Map<String, String> issuetype = new HashMap<String, String>();
    private Map<String, String> priority = new HashMap<String, String>();

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
}
