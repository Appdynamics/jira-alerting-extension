package com.appdynamics.extensions.jira;

import com.appdynamics.extensions.yml.YmlReader;
import org.junit.Test;

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
}
