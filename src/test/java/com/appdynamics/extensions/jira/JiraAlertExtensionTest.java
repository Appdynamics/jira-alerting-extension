/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 */
package com.appdynamics.extensions.jira;

import com.appdynamics.extensions.yml.YmlReader;
import org.junit.Test;

import java.io.FileNotFoundException;

/**
 * Created by balakrishnavadavalasa on 18/08/16.
 */
public class JiraAlertExtensionTest {

    EventArgs eventArgs = new EventArgs();

    @Test
    public void canPostHealthRuleViolationEvent() {

        Configuration configuration = YmlReader.readFromFile(this.getClass().getResource("/conf/config.yml").getFile(), Configuration.class);
        JiraAlertExtension alertExtension = new JiraAlertExtension(configuration);
        alertExtension.processAnEvent(eventArgs.getHealthRuleViolationEventWithOneEvalEntityAndTriggerNoBaseline());

    }

    @Test
    public void canPostOtherEvent(){
        Configuration configuration = YmlReader.readFromFile(this.getClass().getResource("/conf/config.yml").getFile(), Configuration.class);
        JiraAlertExtension alertExtension = new JiraAlertExtension(configuration);
        alertExtension.processAnEvent(eventArgs.getOtherEvent());
    }
}
