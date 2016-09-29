# AppDynamics Alerting Extension for use with Atlassian JIRA

This extension works only with a dedicated SaaS controller or an on-prem controller.

##Use Case

JIRA from Atlassian, Inc. is a bug and issue tracking product used for project management. AppDynamics integrates directly with Atlassian JIRA to create JIRA tickets in response to AppDynamics alerts. With the Atlassian JIRA extension you can leverage your existing ticketing infrastructure to notify the operations team and resolve performance degradation issues.


##Installation

1. To build from source, clone this repository and run `mvn clean install`. This will produce a jira-alert-<version>.zip in the target directory.

2. Unzip the jira-alert-<version>.zip file into `<CONTROLLER_HOME_DIR>/custom/actions/`.

3. Check if you have custom.xml file in `<CONTROLLER_HOME_DIR>/custom/actions/` directory. If yes, add the following xml to the <custom-actions> element.
       
      ```
          <action>
                  <type>jira-alert</type>
              <!-- For Linux/Unix *.sh -->
                  <executable>jira-alert.sh</executable>
              <!-- For windows *.bat -->
                  <!--<executable>jira-alert.bat</executable>-->
          </action>
      ```
       
   If you don't have custom.xml already, create one with the below xml content   
       
      ```
          <custom-actions>
             <action>
                <type>jira-alert</type>
                <!-- For Linux/Unix *.sh -->
                <executable>jira-alert.sh</executable>
                <!-- For windows *.bat -->
               <!--<executable>jira-alert.bat</executable>-->
             </action>
          </custom-actions>
      ```
            
   Uncomment the appropriate executable tag based on windows or linux/unix machine.

4. Update the config.yml file in `<CONTROLLER_HOME_DIR>/custom/actions/jira-alert/conf/` directory with the required parameters. Please make sure to not use tab (\t) while editing yaml files. You may want to validate the yaml file using a yaml validator http://yamllint.com/
  
      ```
      domain: "https://sampledomain.atlassian.net"
      username: "<USER>"
      password: "<PASSWORD>"
      projectKey: "PROJECT_KEY"
      #Optional, if empty logged as Bug
      issueType: "Bug"
      #http timeouts
      connectTimeout: 10000
      socketTimeout: 10000
      #proxy details
      proxy:
       uri:
       username:
       password:
      ```
  
5. Please refer to the following doc to create Custom Actions
     * [Creating custom action](https://docs.appdynamics.com/display/PRO42/Custom+Actions)         
   Now you are ready to use this extension as a custom action. In the AppDynamics UI, go to Alert & Respond -> Actions. Click Create Action. Select Custom Action and click OK. In the drop-down menu you can find the action called 'jira-alert'.
   
   A policy has to be associated with this action for it to be triggered. Refer the docs below
     * [Configure Policy Actions](https://docs.appdynamics.com/display/PRO42/Configure+Policies)
     * [Build an Custom Action](https://docs.appdynamics.com/display/PRO42/Build+a+Custom+Action)

6. Look for the newest created ticket in Jira. 
Screenshots of the workflow are depicted below:

### Action
![](https://raw.githubusercontent.com/Appdynamics/jira-alerting-extension/master/screenshots/CreateAction.png)

### Policy
![](https://raw.githubusercontent.com/Appdynamics/jira-alerting-extension/master/screenshots/CreatePolicy.png)

### Policy Violation
![](https://raw.githubusercontent.com/Appdynamics/jira-alerting-extension/master/screenshots/PolicyViolation.png)

### Jira Issue
![](https://raw.githubusercontent.com/Appdynamics/jira-alerting-extension/master/screenshots/JIRA_Issue.png)


##Contributing

Always feel free to fork and contribute any changes directly here at GitHub.

##Community

Find out more in the [Exchange](https://www.appdynamics.com/community/exchange/extension/atlassian-jira-alerting-extension/) page.

##Support

For any questions or feature request, please contact [AppDynamics Support](mailto:help@appdynamics.com).
