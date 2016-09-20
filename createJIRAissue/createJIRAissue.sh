#!/bin/bash

###
# Copyright 2013 AppDynamics
# 
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
# http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
###


## Before making this work you must activate JSON Web Service Plugin in ServiceNow
## System Definition > Plugins > Search for 'JSON' > right click on JSON Web Service and click 'Activate/Upgrade'

## Import external parameters
. params.sh

## Create full domain
FULL_DOMAIN=$DOMAIN"""/rest/api/2/issue/"

## POLICY VIOLATION VARIABLES
APP_NAME="${1//\"/}"
APP_ID="${2//\"/}"
PVN_ALERT_TIME="${3//\"/}"
PRIORITY="${4//\"/}"
SEVERITY="${5//\"/}"
TAG="${6//\"/}"
POLICY_NAME="${7//\"/}"
POLICY_ID="${8//\"/}"
PVN_TIME_PERIOD_IN_MINUTES="${9//\"/}"
AFFECTED_ENTITY_TYPE="${10//\"/}"
AFFECTED_ENTITY_NAME="${11//\"/}"
AFFECTED_ENTITY_ID="${12//\"/}"
NUMBER_OF_EVALUATION_ENTITIES="${13//\"/}"

## Append a P to the front of a PRIORITY
#PRIORITY="P"""$PRIORITY
## Loop through all evaluation entity variables
## Reissue SEVERITY Variable
if [ "$SEVERITY" = "ERROR" ]; then
    SEVERITY=1
elif [ "$SEVERITY" = "WARN" ]; then
    SEVERITY=2
elif [ "$SEVERITY" = "INFO" ]; then
    SEVERITY=3
fi

## Summary Variable
SUMMARY="Application Name: $APP_NAME\n\
Policy Violation Alert Time: $PVN_ALERT_TIME\n\
Severity: $SEVERITY\n\
Name of Violated Policy: $POLICY_NAME\n\
Affected Entity Type: $AFFECTED_ENTITY_TYPE\n\
Name of Affected Entity: $AFFECTED_ENTITY_NAME\n\n"

## Current Parameter Location
CURP=13
for i in `seq 1 $NUMBER_OF_EVALUATION_ENTITIES`
do
    SUMMARY=$SUMMARY"""EVALUATION ENTITY #"""$i""":\n"

    ((CURP = 1 + $CURP))
    EVALUATION_ENTITY_TYPE="${!CURP}"
    EVALUATION_ENTITY_TYPE="${EVALUATION_ENTITY_TYPE//\"/}"

    SUMMARY=$SUMMARY"""Evaluation Entity: """$EVALUATION_ENTITY_TYPE"""\n"

    ((CURP = 1 + $CURP))
    EVALUATION_ENTITY_NAME="${!CURP}"
    EVALUATION_ENTITY_NAME="${EVALUATION_ENTITY_NAME//\"/}"
    
    SUMMARY=$SUMMARY"""Evaluation Entity Name: """$EVALUATION_ENTITY_NAME"""\n"

    ((CURP = 1 + $CURP))
    EVALUATION_ENTITY_ID="${!CURP}"
    EVALUATION_ENTITY_ID="${EVALUATION_ENTITY_ID//\"/}"
    
    ((CURP = 1 + $CURP))
    NUMBER_OF_TRIGGERED_CONDITIONS_PER_EVALUATION_ENTITY="${!CURP}"
    NUMBER_OF_TRIGGERED_CONDITIONS_PER_EVALUATION_ENTITY="${NUMBER_OF_TRIGGERED_CONDITIONS_PER_EVALUATION_ENTITY//\"/}"


    ## GET VARIABLES OF TRIGGERED CONDITIONS
    for trig in `seq 1 $NUMBER_OF_TRIGGERED_CONDITIONS_PER_EVALUATION_ENTITY`
    do

        SUMMARY=$SUMMARY"""\n  #Triggered Condition """$trig""":\n\n"

	((CURP = 1 + $CURP))
	SCOPE_TYPE_x="${!CURP}"
	SCOPE_TYPE_x="${SCOPE_TYPE_x//\"/}"
        
        SUMMARY=$SUMMARY"""  Scope Type: """$SCOPE_TYPE_x"""\n"

	((CURP = 1 + $CURP))
	SCOPE_NAME_x="${!CURP}"
	SCOPE_NAME_x="${SCOPE_NAME_x//\"/}"
        
	SUMMARY=$SUMMARY"""  Scope Name: """$SCOPE_NAME_x"""\n"

	((CURP = 1 + $CURP))
	SCOPE_ID_x="${!CURP}"
	SCOPE_ID_x="${SCOPE_ID_x//\"/}"

	((CURP = 1 + $CURP))
	CONDITION_NAME_x="${!CURP}"
	CONDITION_NAME_x="${CONDITION_NAME_x//\"/}"

	((CURP = 1 + $CURP))
	CONDITION_ID_x="${!CURP}"
	CONDITION_ID_x="${CONDITION_ID_x//\"/}"

	((CURP = 1 + $CURP))
	OPERATOR_x="${!CURP}"
	OPERATOR_x="${OPERATOR_x//\"/}"

	if [ "$OPERATOR_x" = "LESS_THAN" ]; then
	    OPERATOR_x="<"
	elif [ "$OPERATOR_x" = "LESS_THAN_EQUALS" ]; then
	    OPERATOR_x="<="
	elif [ "$OPERATOR_x" = "GREATER_THAN" ]; then
	    OPERATOR_x=">"
	elif [ "$OPERATOR_x" = "GREATER_THAN_EQUALS" ]; then
	    OPERATOR_x=">="
	elif [ "$OPERATOR_x" = "EQUALS" ]; then
	    OPERATOR_x="=="
	elif [ "$OPERATOR" = "NOT_EQUALS" ]; then
	    OPERATOR_x="!="
        fi 

	((CURP = 1 + $CURP))
	CONDITION_UNIT_TYPE_x="${!CURP}"
	CONDITION_UNIT_TYPE_x="${CONDITION_UNIT_TYPE_x//\"/}"


        ISBASELINE=${CONDITION_UNIT_TYPE_x:0:8}

	if [ "$ISBASELINE" == "BASELINE_" ]
  	  then
	    ((CURP = 1 + $CURP)) 
	    USE_DEFAULT_BASELINE_x="${!CURP}"
	    USE_DEFAULT_BASELINE_x="${USE_DEFAULT_BASELINE_x//\"/}"

	    ((CURP = 1 + $CURP))
	    BASELINE_NAME_x="${!CURP}"
	    BASELINE_NAME_x="${BASELINE_NAME_x//\"/}"

	    ((CURP = 1 + $CURP)) 
	    BASELINE_ID_x="${!CURP}"
	    BASELINE_ID_x="${BASELINE_ID_x//\"/}"
	fi

	((CURP = 1 + $CURP))
	THRESHOLD_VALUE_x="${!CURP}"
	THRESHOLD_VALUE_x="${THRESHOLD_VALUE_x//\"/}"

	SUMMARY=$SUMMARY"""  """$CONDITION_NAME_x""" """$OPERATOR_x""" """$THRESHOLD_VALUE_x"""\n"

	((CURP = 1 + $CURP))
	OBSERVED_VALUE_x="${!CURP}"
	OBSERVED_VALUE_x="${OBSERVED_VALUE_x//\"/}"

	SUMMARY=$SUMMARY"""  Violation Value: """$OBSERVED_VALUE_x"""\n"

    done
done

((CURP=1+$CURP))
SUMMARY_MESSAGE="${!CURP}"
SUMMARY_MESSAGE="${SUMMARY_MESSAGE//\"/}"

((CURP = 1 + $CURP))
INCIDENT_ID="${!CURP}"
INCIDENT_ID="${INCIDENT_ID//\"/}"

((CURP = 1 + $CURP))
DEEP_LINK_URL="${!CURP}"
DEEP_LINK_URL="${DEEP_LINK_URL//\"/}"

SUMMARY=$SUMMARY"""\nIncident URL: """$DEEP_LINK_URL""$INCIDENT_ID"""\n"
LOG_FILE=../../../logs/jira_custom_notification.log
echo > $LOG_FILE 
echo ------ Action Initiated ------ >> $LOG_FILE
echo "Initiate PagerDuty Alert Action at " $PVN_ALERT_TIME >> $LOG_FILE
echo ====== PARAMETERS ====== >> $LOG_FILE
echo "service_key: " $API_KEY >> $LOG_FILE
echo "event_type: " "trigger" >> $LOG_FILE
echo "description: " $POLICY_NAME >> $LOG_FILE
echo "details: " $SUMMARY >> $LOG_FILE
echo ====== RESPONSE ====== >> $LOG_FILE 

curl -D- -u $USER:$PASSWORD -X POST --data '{
    "fields": {
       "project":
       {
          "key": "'"$PROJECT"'"
       },
       "summary": "Policy Violated",
       "description": "'"$SUMMARY"'",
       "issuetype": {
          "name": "Bug"
       },
       "priority" : {
          "id": "'"$PRIORITY"'"
       }
   }
}' -H "Content-Type: application/json" $DOMAIN >> $LOG_FILE

echo ------ Action Completed ------ >> $LOG_FILE