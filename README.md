# Scheduler Manager Gogo Shell Command

[![Antonio Musarra's Blog](https://img.shields.io/badge/maintainer-Antonio_Musarra's_Blog-purple.svg?colorB=6e60cc)](https://www.dontesta.it)
[![Build Status](https://travis-ci.org/amusarra/scheduler-manager-gogoshell-command.svg?branch=master)](https://travis-ci.org/amusarra/scheduler-manager-gogoshell-command)
[![Twitter Follow](https://img.shields.io/twitter/follow/antonio_musarra.svg?style=social&label=%40antonio_musarra%20on%20Twitter&style=plastic)](https://twitter.com/antonio_musarra)

This project implements a set of Gogo Shell commands that handle Liferay jobs. The tasks you can perform are:

1.  **list**: List of the all Jobs filtered by state (default ALL)
2.  **info**: Print detail info of the job
3.  **pause**: Pause one or more Jobs by Job Name, Group Name and Storage Type
4.  **resume**: Resume one or more Jobs by Job Name, Group Name and Storage Type

The following commands are valid only for PERSISTED jobs and managed by QUARTZ:

1. **jobIsFired**: Return true if the Job running false otherwise
2. **jobsIsFired**: Return the count of the Job by groupName that are running
3. **listJobsInProgress**: Print the list of the jobs that are in progress.

_The version of this project was tested on Liferay 7 CE GA4/GA5 and Liferay DXP SP14_

### 1. Getting Started
To start testing the plugin you need:


1.   clone this repository
2.   build project
3.   deploy OSGi module (it.dontesta.labs.liferay.gogo.scheduler.manager-$version.jar)

From your terminal execute the commands:

	$ git clone https://github.com/amusarra/scheduler-manager-gogoshell-command.git
	$ cd scheduler-manager-gogoshell-command
	$ ./gradlew clean build

The last gradle command, create a OSGi bundle that you must deploy on your Liferay instance. You can deploy with this command (replace auto deploy directory with your).


	$ cp build/libs/it.dontesta.labs.liferay.gogo.scheduler.manager-1.0.0.jar /opt/liferay-ce-portal-7.0-ga4/deploy/

You could deploy also with the deploy gradle task, but must setting the auto.deploy.dir in gradle.properties file.

	$ ./gradlew deploy

### 2. After deploy bundle
After the deploy of the bundle you can check if the bundle is correctly installed. Connect to Gogo Shell via telnet  and execute **lb** command.

	$ telnet localhost 11311
	g! lb|grep Scheduler
		533|Active     |   10|Scheduler Manager Gogo Shell Command (1.0.0)

Well done! The bundle is in state ACTIVE.


### 3. How to use commands
Via Gogo Shell we check deployed commands (that have scheduler as scope).


	g! help|grep scheduler
		scheduler:info
		scheduler:list
		scheduler:pause
		scheduler:resume
		scheduler:jobIsFired
		scheduler:jobsIsFired
		scheduler:listJobsInProgress
The list of commands obtained are those described at the beginning. You can see for each command the usage, by this command (_help scope:commandName_). Follow the help of the four available commands.

	g! help scheduler:info
	
	info - Detail info of the job
	   scope: scheduler
	   parameters:
	      String   The JobName
	      String   The GroupName
	      String   The StorageType {MEMORY, MEMORY_CLUSTERED, PERSISTED}

Command 1 - Scheduler:info

The storage types are defined in [StorageType](https://github.com/liferay/liferay-portal/blob/master/portal-kernel/src/com/liferay/portal/kernel/scheduler/StorageType.java "StorageType Enumeration")   

	g! help scheduler:list
	list - List of the all Jobs filtered by state (default ALL)
	   scope: scheduler
	   options:
	      --status, -s   Filter the jobs by trigger state {state: COMPLETE,NORMAL,EXPIRED,PAUSED,UNSCHEDULED} [optional]

Command 2 - List of the all Jobs filtered by state (default ALL) 

The states of the trigger defined in [TriggerState Enumeration](https://github.com/liferay/liferay-portal/blob/master/portal-kernel/src/com/liferay/portal/kernel/scheduler/TriggerState.java "TriggerState Enumeration") 

	g! help scheduler:pause
	pause - Pause one or more Jobs by Job Name, Group Name and Storage Type
	   scope: scheduler
	   parameters:
	      String   The JobName
	      String   The GroupName
	      String   The StorageType {MEMORY, MEMORY_CLUSTERED, PERSISTED}

Command 3 - Pause one or more Jobs by Job Name, Group Name and Storage Type 

	g! help scheduler:resume
	resume - Resume one or more Jobs by Job Name, Group Name and Storage Type
	   scope: scheduler
	   parameters:
	      String   The JobName
	      String   The GroupName
	      String   The StorageType {MEMORY, MEMORY_CLUSTERED, PERSISTED}
Command 4 - Resume one or more Jobs by Job Name, Group Name and Storage Type 

	g! help scheduler:jobIsFired
    
    jobIsFired - Return true if the Job running false otherwise
       scope: scheduler
       parameters:
          String   The JobName
Command 5 - Return true if the Job running false otherwise

	g! help scheduler:jobsIsFired
    
    jobsIsFired - Return the count of the Job by groupName that are running
       scope: scheduler
       parameters:
          String   The GroupName
Command 6 - Return the count of the Job by groupName that are running

	g! help scheduler:listJobsInProgress
    
    listJobsInProgress - Print the list of the jobs that are in progress
       scope: scheduler
       parameters:
          String   The GroupName
Command 7 - Print the list of the jobs that are in progress 

#### 3.1 Scheduler List


	g! scheduler:list

Command 8 - List of the jobs filtered by state (default state is ALL)

![List of the jobs filtered by state](https://www.dontesta.it/wp-content/uploads/2017/07/scheduler-manager-gogoshell-command-list-all.png "List of the jobs filtered by state") 


	g! scheduler:list --status PAUSED
Command 9 - List of the jobs filtered by state with PAUSED value 

![List of the jobs filtered by state](https://www.dontesta.it/wp-content/uploads/2017/07/scheduler-manager-gogoshell-command-list-paused.png "List of the jobs filtered by state") 


#### 3.2 Scheduler Pause and Resume

	g! scheduler:pause com.liferay.recent.documents.web.internal.messaging.RecentDocumentsMessageListener com.liferay.recent.documents.web.internal.messaging.RecentDocumentsMessageListener MEMORY_CLUSTERED
Command 10 - Pause the job with the name com.liferay...RecentDocumentsMessageListener

	scheduler:resume com.liferay.recent.documents.web.internal.messaging.RecentDocumentsMessageListener com.liferay.recent.documents.web.internal.messaging.RecentDocumentsMessageListener MEMORY_CLUSTERED

Command 11 - Resume the job with the name com.liferay...RecentDocumentsMessageListener

#### 3.3 Scheduler Info


	g! scheduler:info com.liferay.recent.documents.web.internal.messaging.RecentDocumentsMessageListener com.liferay.recent.documents.web.internal.messaging.RecentDocumentsMessageListener MEMORY_CLUSTERED
Command 12 - Detail of the job with the name com.liferay...RecentDocumentsMessageListener

This command (compared to the list of jobs) shows additional information:

1.  Cron Expression
2.  Destination Name
3.  Job Exceptions

![Detail of the jobs](https://www.dontesta.it/wp-content/uploads/2017/07/scheduler-manager-gogoshell-command-info.png "Detail of the jobs") 

#### 3.4 Scheduler Check if job fired

	g! scheduler:jobIsFired com.liferay...RecentDocumentsMessageListener
    false

	g! scheduler:jobIsFired com.liferay...RecentDocumentsMessageListener
    true
Command 13 - Check if job fired with the name com.liferay...RecentDocumentsMessageListener

#### 3.5 Scheduler Count job for group fired

	g! scheduler:jobsIsFired MyJobGroup
    10
Command 14 - Count job for group MyJobGroup fired


### 4. Resources

1.  [Liferay 7 CE/Liferay DXP Scheduled Task](https://web.liferay.com/it/web/user.26526/blog/-/blogs/liferay-7-ce-liferay-dxp-scheduled-tasks "Liferay 7 CE/Liferay DXP Scheduled Tasks") post by David H Nebinger (on Liferay Blog)
2.  [Scheduler Example](https://github.com/amusarra/liferay-italia-bo-usergroup/tree/master/modules/application-configuration/scheduler-app "Scheduler Example") on my GitHub account
3.	[How to implement a custom Gogo shell command for Liferay 7](http://www.marconapolitano.it/en/liferay/86-how-to-implement-a-custom-gogo-shell-command-for-liferay-7.html "How to implement a custom Gogo shell command for Liferay 7") post by Marco Napolitano

	      
### Project License
The MIT License (MIT)

Copyright &copy; 2017 Antonio Musarra's Blog - [https://www.dontesta.it](https://www.dontesta.it "Antonio Musarra's Blog") , [antonio.musarra@gmail.com](mailto:antonio.musarra@gmail.com "Antonio Musarra Email") 

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

<span style="color:#D83410">
	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
	SOFTWARE.
<span>
