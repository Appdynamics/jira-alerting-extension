@echo off

﻿
java -Dlog4j.configuration=file:conf/log4j.xml -jar jira-alerting-extension.jar %*