# AppDynamics Atlassian JIRA - Alerting Extension

##Use Case

JIRA from Atlassian, Inc. is a bug and issue tracking product used for project management. AppDynamics Pro 3.6 integrates directly with Atlassian JIRA to create JIRA tickets in response to AppDynamics alerts. With the Atlassian JIRA extension you can leverage your existing ticketing infrastructure to notify the operations team and resolve performance degradation issues.

##Package

Run ant from jira-alerting-extension directory. This will create the following file in the dist directory: JiraAlertingExtension.zip.

##Installation


###1. Download and unzip JiraAlertingExtension.zip
 
Download and unzip the JiraAlertingExtension.zip file into your <Controller-Home> directory.
 

###2. Modify params.sh File

   Located under the ``<Controller Home>/custom/actions/createJIRAissue/`` directory is a params.sh file. This file is must have its parameters filled out in the following manner:

   2.1 Modify the DOMAIN variable to be your JIRA domain. For example: 
   
      DOMAIN = "https://sampledomain.jira.com"

   2.2 Modify the USER variable to be the username that will issue these generated tickets.

   2.3 Modify the PASSWORD variable to be the password of the entered username that will issue these generated tickets.

   2.4 Modify the PROJECT variable to be the project code to which generated tickets should be issued.
   
   The createJIRAissue.sh file follows the following table to tie in the parameters together:

<table>
<tr>
  <th>AppDynamics Parameters
	</th>
	<th> JIRA Parameters
	</th>
	<th>Comments
	</th>
</tr>	
	<tr>
	<td>
	</td>
	<td> Project
	</td>
	<td>This is a parameter that helps identify which project in the desired JIRA domain to use. The main field under Project to use is key.
	</td>
</tr>
	<tr>
	<td>
	</td>
	<td> Summary
	</td>
	<td>This is a short description of what the new ticket will display in list form.
	</td>

</tr>

<tr>
	<td>APP_NAME, PVN_ALERT_TIME, SEVERITY, POLICY_NAME, AFFECTED_ENTITY_TYPE, AFFECTED_ENTITY_NAME, EVALUATION_TYPE, EVALUATION_ENTITY_NAME, SCOPE_TYPE_x, SCOPE_NAME_x, CONDITION_NAME_x, THRESHOLD_VALUE_x, OPERATOR_x, BASELINE_NAME_x, USE_DEFAULT_BASELINE_x, OBSERVED_VALUE_x, INCIDENT_ID, DEEP_LINK_URL
	</td>
	<td>Description
	</td>
	<td>The format is as follows for the following Policy Violation
Parameters:
	<table>
		<tr>	
			<th>Variable name
			</th>
			<th>Variable value
			</th>
		</tr>
		<tr>	
	<td>Application Name
	</td>
	<td>APP_NAME
	</td>
	
</tr>
<tr>	
	<td>Policy Violation Alert Time
	</td>
	<td>PVN_ALERT_TIME
	</td>
	
</tr>
<tr>
	<td>Severity
	</td>
	<td>SEVERITY
	</td>
	
</tr>
<tr>
	<td>Name of Violated Policy
	</td>
	<td>POLICY_NAME
	</td>
	
</tr>
<tr>
	<td>Affected Entity Type
	</td>
	<td>AFFECTED_ENTITY_TYPE
	</td>
	
</tr>
<tr>	
	<td>Name of Affected Entity
	</td>
	<td>AFFECTED_ENTITY_NAME
	</td>
	
</tr>
<tr>
	<td>Evaluation Entity #x * Evaluation Entity
	</td>
	<td>EVALUATION_TYPE
	</td>
	
</tr>
<tr>
	<td>Evaluation Entity Name
	</td>
	<td>EVALUATION_ENTITY_NAME
	</td>
	
</tr>
<tr>
	<td>Triggered Condition #x Scope Type
	</td>
	<td>SCOPE_TYPE_x
	</td>
	
</tr>
<tr>	
	<td>Scope Name
	</td>
	<td>SCOPE_NAME_x
	</td>
	
</tr>
<tr>
	<td>
	</td>
	<td>CONDITION_NAME_x OPERATOR_x THRESHOLD_VALUE_x (this is for
    ABSOLUTE conditions)
	</td>
	
</tr>
<tr>
	<td>Violation Value
	</td>
	<td>OBSERVED_VALUE_x
	</td>
	
</tr>
<tr>
	<td>Incident URL
	</td>
	<td>DEEP_LINK_URL + INCIDENT_ID
	</td>
	
</tr>


	</table>
	</td>
	
</tr>
<tr>	
	<td>
	</td>
	<td>issuetype
	</td>
	<td>We use the "name" "Bug" to signify that we are auto
generating a bug due to the Policy Violation.
	</td>
	
</tr>
<tr>
	<td>PRIORITY
	</td>
	<td>priority
	</td>
	<td>This may range depending on how your JIRA priorities are set. However if
the P1, P2, P3, P4, P5 convention for priorities are used for priorities
then simply append a P to the front of PRIORITY.
	</td>
	
</tr>
</table>


###2. Install Custom Actions

To create a Custom Action, first refer to the AppDynamics Pro 3.6 documentation:
[*Installing Custom Actions into the Controller*]
(http://docs.appdynamics.com/display/PRO12S/Configure+Custom+Notifications#Configure
CustomNotifications-InstallingCustomActionsontheController) (login required).

The custom.xml file and createJIRAissue directory used for this
custom notification are located within
the  directory ``<Controller Home>/custom/actions/``.

Place the createJIRAissue directory (containing params.sh and createJIRAissue.sh), along with the custom.xml file, into the
<Controller Home>/custom/actions/ directory.

###3. Look for the newest created issue in JIRA

Once a ticket is created and you click its summary you can see the details in JIRA:

![](http://appsphere.appdynamics.com/t5/image/serverpage/image-id/61iAE3FA4EF35D76DA1/image-size/original?v=mpbl-1&px=-1)


##Contributing

Always feel free to fork and contribute any changes directly via [GitHub](https://github.com/Appdynamics/jira-alerting-extension).

##Community

Find out more in the [AppSphere](http://appsphere.appdynamics.com/t5/Extensions/Atlassian-JIRA-Alerting-Extension/idi-p/749) community.

##Support

For any questions or feature request, please contact [AppDynamics Center of Excellence](mailto:ace-request@appdynamics.com).
