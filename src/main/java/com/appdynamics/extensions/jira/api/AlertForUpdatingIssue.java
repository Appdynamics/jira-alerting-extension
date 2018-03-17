/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 */
package com.appdynamics.extensions.jira.api;

import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Created by balakrishnavadavalasa on 19/09/16.
 */
@JsonRootName(value = "update")
public class AlertForUpdatingIssue {

    private Comment[] comment;

    public Comment[] getComment() {
        return comment;
    }

    public void setComment(Comment[] comment) {
        this.comment = comment;
    }
}
