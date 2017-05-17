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

import com.appdynamics.extensions.alerts.customevents.*;
import com.appdynamics.extensions.jira.Configuration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;

/**
 * Builds an Alert from Health Rule violation event.
 */

public class AlertBuilder {
    private static final String NEW_LINE = "\n";
    private static Logger logger = Logger.getLogger(AlertBuilder.class);

    public AlertForCreatingIssue buildAlert(Event event, Configuration config) {
        if (config != null) {
            AlertForCreatingIssue alert = new AlertForCreatingIssue();

            String summary;
            String description;
            if (event instanceof HealthRuleViolationEvent) {
                HealthRuleViolationEvent violationEvent = (HealthRuleViolationEvent) event;
                summary = getSummary(violationEvent);
                description = getDescription(violationEvent);
                alert.setAlertId(violationEvent.getIncidentID());
            } else {
                OtherEvent otherEvent = (OtherEvent) event;
                summary = getSummary(otherEvent);
                description = getDescription(otherEvent);
                alert.setAlertId(otherEvent.getEventNotificationId());
            }

            alert.getProject().put(FieldKeys.PROJECT_KEY, config.getProjectKey());

            alert.getIssuetype().put(FieldKeys.ISSUE_TYPE_NAME, getIssueType(config));

            if (config.isPriorityNotRequired() == false) {
                alert.getPriority().put(FieldKeys.PRIORITY_ID, event.getPriority());
            }

            if(!Strings.isNullOrEmpty(config.getComponent())) {
                Component component = new Component();
                component.setName(config.getComponent());
                Component [] components = {component};
                alert.setComponents(components);
            }

            alert.setSummary(summary);
            alert.setDescription(description);

            return alert;
        }
        return null;
    }

    public AlertForUpdatingIssue buildAlertForUpdatingIssue(Event event) {
        AlertForUpdatingIssue alertUpdate = new AlertForUpdatingIssue();
        if (event != null) {
            String msg;
            if(event instanceof HealthRuleViolationEvent) {
                HealthRuleViolationEvent violationEvent = (HealthRuleViolationEvent) event;
                StringBuilder sb = new StringBuilder();
                sb.append("Severity : ").append(violationEvent.getSeverity()).append(NEW_LINE);
                sb.append("Event Type : ").append(violationEvent.getEventType()).append(NEW_LINE);
                sb.append("Summary Message : ").append(violationEvent.getSummaryMessage()).append(NEW_LINE);
                msg = sb.toString();
            } else {
                OtherEvent otherEvent = (OtherEvent) event;
                msg = "Deep Link URL: " + otherEvent.getDeepLinkUrl();
            }

            Add add = new Add();
            add.setBody(msg);
            Comment comment = new Comment();
            comment.setAdd(add);
            Comment[] comments = {comment};
            alertUpdate.setComment(comments);
        }
        return alertUpdate;
    }

    private String getIssueType(Configuration config) {
        if (!Strings.isNullOrEmpty(config.getIssueType())) {
            return config.getIssueType();
        }
        return "Bug";
    }

    private String getSummary(OtherEvent otherEvent) {
        return "Event : " + otherEvent.getEventNotificationName() + ", Severity: " + otherEvent.getSeverity();
    }

    private String getSummary(HealthRuleViolationEvent violationEvent) {
        return "Health Rule: " + violationEvent.getHealthRuleName() + " violated";

    }

    private String getDescription(HealthRuleViolationEvent violationEvent) {
        StringBuilder description = new StringBuilder();

        description.append("Application Name : ").append(violationEvent.getAppName()).append(NEW_LINE);
        description.append("Policy Violation Alert Time : ").append(violationEvent.getPvnAlertTime()).append(NEW_LINE);
        description.append("Severity : ").append(violationEvent.getSeverity()).append(NEW_LINE);
        description.append("Health rule that violated : ").append(violationEvent.getHealthRuleName()).append(NEW_LINE);
        description.append("Event Type : ").append(violationEvent.getEventType()).append(NEW_LINE);
        description.append("Violation time in minutes : ").append(violationEvent.getPvnTimePeriodInMinutes()).append(NEW_LINE);
        description.append("Affected Entity Type : ").append(violationEvent.getAffectedEntityType()).append(NEW_LINE);
        description.append("Name of Affected Entity : ").append(violationEvent.getAffectedEntityName()).append(NEW_LINE);

        List<EvaluationEntity> evaluationEntities = violationEvent.getEvaluationEntity();
        for (int i = 0; i < evaluationEntities.size(); i++) {
            EvaluationEntity evaluationEntity = evaluationEntities.get(i);
            description.append(NEW_LINE);
            description.append("EVALUATION ENTITY #").append(i + 1).append(":").append(NEW_LINE);
            description.append("Evaluation Entity : ").append(evaluationEntity.getType()).append(NEW_LINE);
            description.append("Evaluation Entity Name : ").append(evaluationEntity.getName()).append(NEW_LINE);

            List<TriggerCondition> triggeredConditions = evaluationEntity.getTriggeredConditions();
            for (int j = 0; j < triggeredConditions.size(); j++) {
                TriggerCondition triggerCondition = triggeredConditions.get(j);
                description.append(NEW_LINE);
                description.append("Triggered Condition #").append(j + 1).append(":").append(NEW_LINE);
                description.append("Scope Type : ").append(triggerCondition.getScopeType()).append(NEW_LINE);
                description.append("Scope Name : ").append(triggerCondition.getScopeName()).append(NEW_LINE);

                if (triggerCondition.getConditionUnitType() != null && triggerCondition.getConditionUnitType().toUpperCase().startsWith("BASELINE")) {
                    description.append("Is Default Baseline?").append(triggerCondition.isUseDefaultBaseline() ? "true" : "false").append(NEW_LINE);
                    if (!triggerCondition.isUseDefaultBaseline()) {
                        description.append("Baseline Name : ").append(triggerCondition.getBaselineName()).append(NEW_LINE);
                    }
                }
                description.append(triggerCondition.getConditionName()).append(triggerCondition.getOperator()).append(triggerCondition.getThresholdValue()).append(NEW_LINE);
                description.append("Violation Value : ").append(triggerCondition.getObservedValue()).append(NEW_LINE).append(NEW_LINE);
            }
        }
        description.append("DeepLink URL : ").append(violationEvent.getDeepLinkUrl()).append(violationEvent.getIncidentID()).append(NEW_LINE);

        description.append("Summary Message : ").append(violationEvent.getSummaryMessage()).append(NEW_LINE);

        return description.toString();
    }

    private String getDescription(OtherEvent otherEvent) {
        StringBuilder description = new StringBuilder();

        description.append("Application Name : ").append(otherEvent.getAppName()).append(NEW_LINE);
        description.append("Event Notification Time : ").append(otherEvent.getEventNotificationTime()).append(NEW_LINE);
        description.append("Severity : ").append(otherEvent.getSeverity()).append(NEW_LINE);
        description.append("Event Name : ").append(otherEvent.getEventNotificationName()).append(NEW_LINE);
        description.append("Event Notification Interval in Minutes : ").append(otherEvent.getEventNotificationIntervalInMin()).append(NEW_LINE);

        List<EventType> eventTypes = otherEvent.getEventTypes();
        for (int i = 0; i < eventTypes.size(); i++) {
            EventType eventType = eventTypes.get(i);
            description.append(NEW_LINE);
            description.append("Event Type #").append(i + 1).append(":").append(NEW_LINE);
            description.append("Event Type : ").append(eventType.getEventType()).append(NEW_LINE);
            description.append("Number of events of this type : ").append(eventType.getEventTypeNum()).append(NEW_LINE);
        }
        List<EventSummary> eventSummaries = otherEvent.getEventSummaries();
        for (int j = 0; j < eventSummaries.size(); j++) {
            EventSummary eventSummary = eventSummaries.get(j);
            description.append(NEW_LINE);
            description.append("Summary #").append(j + 1).append(":").append(NEW_LINE);
            description.append("Summary Type : ").append(eventSummary.getEventSummaryType()).append(NEW_LINE);
            description.append("Summary Time : ").append(eventSummary.getEventSummaryTime()).append(NEW_LINE);
            description.append("Summary Severity : ").append(eventSummary.getEventSummarySeverity()).append(NEW_LINE);
            description.append("Summary Message : ").append(eventSummary.getEventSummaryString()).append(NEW_LINE);
        }
        description.append("Deep Link URL : ").append(otherEvent.getDeepLinkUrl()).append(otherEvent.getEventNotificationId());
        return description.toString();
    }


    public String convertIntoJsonString(Object alert) {
        if (alert != null) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
            try {
                return mapper.writeValueAsString(alert);
            } catch (JsonProcessingException e) {
                logger.error("Error while converting the Object to Json string " + alert, e);
            }
        }
        return null;
    }

    public String getEventId(Event event) {
        if (event instanceof HealthRuleViolationEvent) {
            return ((HealthRuleViolationEvent) event).getIncidentID();
        } else {
            return ((OtherEvent) event).getEventNotificationId();
        }
    }

    public static void main(String[] args) throws JsonProcessingException {
        String msg = "Summary Message : ";
        Add add = new Add();
        add.setBody(msg);
        Comment comment = new Comment();
        comment.setAdd(add);
        Comment[] comments = {comment};
        AlertForUpdatingIssue alert = new AlertForUpdatingIssue();
        alert.setComment(comments);
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(alert));
    }
}
