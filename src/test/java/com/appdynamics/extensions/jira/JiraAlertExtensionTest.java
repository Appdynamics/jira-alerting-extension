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
