/**
 * MIT License
 * Copyright (c) 2019 Antonio Musarra's Blog - https://www.dontesta.it
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package it.dontesta.labs.liferay.gogo.scheduler.manager;

import static org.fusesource.jansi.Ansi.ansi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.liferay.petra.string.StringPool;
import it.dontesta.labs.liferay.gogo.scheduler.manager.quartz.pojo.FiredTrigger;
import it.dontesta.labs.liferay.gogo.scheduler.manager.quartz.util.Console;
import it.dontesta.labs.liferay.gogo.scheduler.manager.quartz.util.QuartzUtils;
import org.apache.felix.service.command.Descriptor;
import org.apache.felix.service.command.Parameter;
import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelperUtil;
import com.liferay.portal.kernel.scheduler.SchedulerException;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.Validator;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;

/**
 * Gogo Shell Command Series for Liferay JOBS Management
 * (Example: job list, pause jobs, resume jobs, stop, etc.).
 *
 * @author Antonio Musarra <antonio.musarra@gmail.com>
 */
@Component(
	property = {
		"osgi.command.function=list",
		"osgi.command.function=info",
		"osgi.command.function=pause",
		"osgi.command.function=resume",
		"osgi.command.function=jobIsFired",
		"osgi.command.function=jobsIsFired",
		"osgi.command.function=listJobsInProgress",
		"osgi.command.scope=scheduler"
	},
	service = Object.class
)
@Descriptor("Gogo Shell Command Series for Liferay "
			+
			"JOBS Management (Example: job list, pause jobs, resume jobs, stop, etc.).")
public class SchedulerManagerCommand {

	/**
	 * Print the list of the all Jobs filtered by state (default ALL)
	 *
	 * @param triggerState The trigger state. Possible values are
	 *                     COMPLETE,NORMAL,EXPIRED,PAUSED,UNSCHEDULED
	 * @throws PortalException In the case of errors.
	 */
	@Descriptor("List of the all Jobs filtered by state (default ALL)")
	public void list(
		@Descriptor("Filter the jobs by trigger state {state: COMPLETE,NORMAL,EXPIRED,PAUSED,UNSCHEDULED}")
		@Parameter(names = {
			"--status", "-s"
		}, absentValue = "ALL") String triggerState)
		throws PortalException {

		Console.println(
			ansi().eraseScreen().render(
				"@|green List of the jobs filtered by state:|@ @|red " +
				triggerState + " |@"));
		Console.println(getJobsListTableHeader());
		Console.println(getJobsListTableRows(triggerState));
	}

	/**
	 * Print detail info of the job.
	 *
	 * @param jobName     The name of the job
	 * @param groupName   The group name of the job
	 * @param storageType The Storage Type of the job. The Storage Type values are
	 *                    MEMORY, MEMORY_CLUSTERED, PERSISTED
	 * @throws PortalException
	 */
	@Descriptor("Detail info of the job")
	public void info(
		@Descriptor("The JobName") String jobName,
		@Descriptor("The GroupName") String groupName,
		@Descriptor("The StorageType {MEMORY, MEMORY_CLUSTERED, PERSISTED}") String storageType)
		throws PortalException {

		SchedulerResponse schedulerResponse =
			SchedulerEngineHelperUtil.getScheduledJob(jobName, groupName,
				StorageType.valueOf(storageType));

		if (Validator.isNull(schedulerResponse)) {
			throw new PortalException("Job not found with the name " + jobName);
		}

		SimpleDateFormat df = new SimpleDateFormat(DateUtil.ISO_8601_PATTERN);
		AsciiTable at = new AsciiTable();

		at.setPadding(5);
		at.addRule();
		at.addRow(_COLUMN_JOB_NAME, schedulerResponse.getJobName());
		at.addRule();
		at.addRow(_COLUMN_GROUP_NAME, schedulerResponse.getGroupName());
		at.addRule();
		at.addRow(
			_COLUMN_STATE,
			SchedulerEngineHelperUtil.getJobState(schedulerResponse).name());
		at.addRule();

		if (Validator.isNotNull(
			schedulerResponse.getTrigger().getStartDate())) {
			at.addRow(
				_COLUMN_START_TIME,
				df.format(schedulerResponse.getTrigger().getStartDate()));
			at.addRule();
		}
		else {
			at.addRow(_COLUMN_START_TIME, StringPool.DASH);
			at.addRule();
		}

		if (Validator.isNotNull(
			SchedulerEngineHelperUtil.getPreviousFireTime(
				schedulerResponse))) {
			at.addRow(
				_COLUMN_PREVIOUS_FIRE_TIME,
				df.format(
					SchedulerEngineHelperUtil.getPreviousFireTime(
						schedulerResponse)));
			at.addRule();
		}
		else {
			at.addRow(_COLUMN_PREVIOUS_FIRE_TIME, StringPool.DASH);
			at.addRule();
		}

		if (Validator.isNotNull(
			SchedulerEngineHelperUtil.getNextFireTime(schedulerResponse))) {
			at.addRow(
				_COLUMN_NEXT_FIRE_TIME,
				df.format(
					SchedulerEngineHelperUtil.getNextFireTime(
						schedulerResponse)));
			at.addRule();
		}
		else {
			at.addRow(_COLUMN_NEXT_FIRE_TIME, StringPool.DASH);
			at.addRule();
		}

		at.addRow(
			"Cron Expression",
			SchedulerEngineHelperUtil.getCronText(
				Calendar.getInstance(),
				false));
		at.addRule();
		at.addRow(
			_COLUMN_DESTINATION_NAME, schedulerResponse.getDestinationName());
		at.addRule();
		at.addRow(_COLUMN_STORAGE_TYPE, schedulerResponse.getStorageType());
		at.addRule();

		if (Validator.isNotNull(SchedulerEngineHelperUtil.getJobExceptions(
			jobName, groupName, StorageType.valueOf(storageType)))) {
			at.addRow(
				"Job Exceptions", SchedulerEngineHelperUtil.getJobExceptions(
					jobName, groupName, StorageType.valueOf(storageType)));
			at.addRule();
		}
		else {
			at.addRow("Job Exceptions", StringPool.DASH);
			at.addRule();
		}

		Console.println(
			ansi().eraseScreen().render(
				"@|green Detail of the job:|@ @|red " + jobName + " |@"));
		Console.println(at.render(160));
	}

	/**
	 * Pause Job by Job Name, Group Name and Storage Type
	 *
	 * @param jobName     The name of the job
	 * @param groupName   The group name of the job
	 * @param storageType The Storage Type of the job. The Storage Type values are
	 *                    MEMORY, MEMORY_CLUSTERED, PERSISTED
	 * @throws PortalException In the case of errors.
	 */
	@Descriptor("Pause Job by Job Name, Group Name and Storage Type")
	public void pause(
		@Descriptor("The JobName") String jobName,
		@Descriptor("The GroupName") String groupName,
		@Descriptor("The StorageType {MEMORY, MEMORY_CLUSTERED, PERSISTED}") String storageType)
		throws PortalException {

		SchedulerEngineHelperUtil.pause(
			jobName, groupName, StorageType.valueOf(storageType));
	}

	/**
	 * Pause Jobs by Group Name and Storage Type
	 *
	 * @param groupName   The group name of the job
	 * @param storageType The Storage Type of the job. The Storage Type values are
	 *                    MEMORY, MEMORY_CLUSTERED, PERSISTED
	 * @throws PortalException In the case of errors.
	 */
	@Descriptor("Pause Jobs by Group Name and Storage Type")
	public void pause(
		@Descriptor("The GroupName") String groupName,
		@Descriptor("The StorageType {MEMORY, MEMORY_CLUSTERED, PERSISTED}") String storageType)
		throws PortalException {

		SchedulerEngineHelperUtil.pause(
			groupName, StorageType.valueOf(storageType));
	}


	/**
	 * Resume Job by Job Name, Group Name and Storage Type
	 *
	 * @param jobName     The name of the job
	 * @param groupName   The group name of the job
	 * @param storageType The Storage Type of the job. The Storage Type values are
	 *                    MEMORY, MEMORY_CLUSTERED, PERSISTED
	 * @throws PortalException In the case of errors.
	 */
	@Descriptor("Resume Job by Job Name, Group Name and Storage Type")
	public void resume(
		@Descriptor("The JobName") String jobName,
		@Descriptor("The GroupName") String groupName,
		@Descriptor("The StorageType {MEMORY, MEMORY_CLUSTERED, PERSISTED}") String storageType)
		throws PortalException {

		SchedulerEngineHelperUtil.resume(
			jobName, groupName, StorageType.valueOf(storageType));
	}

	/**
	 * Resume Jobs by Group Name and Storage Type
	 *
	 * @param groupName   The group name of the job
	 * @param storageType The Storage Type of the job. The Storage Type values are
	 *                    MEMORY, MEMORY_CLUSTERED, PERSISTED
	 * @throws PortalException In the case of errors.
	 */
	@Descriptor("Resume Jobs by Group Name and Storage Type")
	public void resume(
		@Descriptor("The GroupName") String groupName,
		@Descriptor("The StorageType {MEMORY, MEMORY_CLUSTERED, PERSISTED}") String storageType)
		throws PortalException {

		SchedulerEngineHelperUtil.resume(
			groupName, StorageType.valueOf(storageType));
	}

	/**
	 * Return the count of the Job by groupName that are running
	 *
	 * @param groupName The group name of the job
	 * @throws PortalException In the case of errors.
	 */
	@Descriptor("Return the count of the Job by groupName that are running. ONLY QUARTZ PERSISTED JOB!!!")
	public int jobsIsFired(
		@Descriptor("The GroupName") String groupName)
		throws PortalException {

		return QuartzUtils.getFiredJobsCount(groupName);
	}

	/**
	 * Return true if the Job running false otherwise
	 *
	 * @param jobName The job name of the job
	 * @throws PortalException In the case of errors.
	 */
	@Descriptor("Return true if the Job running false otherwise. ONLY QUARTZ PERSISTED JOB!!!")
	public boolean jobIsFired(
		@Descriptor("The JobName") String jobName)
		throws PortalException {

		return QuartzUtils.getFiredJobCount(jobName) > 0;
	}

	/**
	 * Print the list of the jobs that are in progress
	 *
	 * @param groupName The job name of the job
	 * @throws PortalException In the case of errors.
	 */
	@Descriptor("Print the list of the jobs that are in progress. ONLY QUARTZ PERSISTED JOB!!!")
	public void listJobsInProgress(
		@Descriptor("The GroupName") String groupName)
		throws PortalException {

		Console.println(
			ansi().eraseScreen().render(
				"@|green List of the jobs that are in progress filtered by groupName:|@ @|red " +
				groupName + " |@"));
		Console.println(getJobsListInProgressTableHeader());
		Console.println(getJobsListInProgressTableRows(groupName));


	}

	/**
	 * Return the jobs list in progress table header.
	 *
	 * @return String Formatted table header
	 */
	private String getJobsListInProgressTableHeader() {
		AsciiTable at = new AsciiTable();
		at.addRule();
		at.addRow(
			_COLUMN_JOB_NAME, _COLUMN_GROUP_NAME, _COLUMN_INSTANCE_NAME,
			_COLUMN_FIRED_TIME,
			_COLUMN_STATE);
		at.addRule();

		return at.render(160);
	}

	/**
	 * Return the jobs list table header.
	 *
	 * @return String Formatted table header
	 */
	private String getJobsListTableHeader() {
		AsciiTable at = new AsciiTable();
		at.addRule();
		at.addRow(
			_COLUMN_JOB_NAME, _COLUMN_GROUP_NAME, _COLUMN_STATE,
			_COLUMN_START_TIME,
			_COLUMN_PREVIOUS_FIRE_TIME, _COLUMN_NEXT_FIRE_TIME,
			_COLUMN_STORAGE_TYPE);
		at.addRule();

		return at.render(160);
	}

	/**
	 * Return the jobs list in progress table rows.
	 *
	 * @param groupName
	 * @return String Formatted table rows
	 */
	private String getJobsListInProgressTableRows(String groupName) {
		List<FiredTrigger> firedTriggerList =
			QuartzUtils.getFiredTrigger(groupName);

		AsciiTable at = new AsciiTable();
		SimpleDateFormat df = new SimpleDateFormat(DateUtil.ISO_8601_PATTERN);

		firedTriggerList.forEach(firedTrigger -> {
			Collection<String> columnsValue = new ArrayList<>();

			columnsValue.add(firedTrigger.getTriggerName());
			columnsValue.add(firedTrigger.getTriggerGroup());
			columnsValue.add(firedTrigger.getInstanceName());
			columnsValue.add(
				df.format(firedTrigger.getFiredTime()
				));
			columnsValue.add(firedTrigger.getState());

			at.addRow(columnsValue);
			at.addRule();
		});

		if (firedTriggerList.isEmpty()) {
			at.addRow("No Jobs in progress found");
			at.setTextAlignment(TextAlignment.CENTER);
			at.addRule();
		}

		return at.render(160);
	}

	/**
	 * Return the jobs list table rows.
	 *
	 * @param status
	 * @return String Formatted table rows
	 * @throws SchedulerException In the case of errors
	 */
	private String getJobsListTableRows(String status)
		throws SchedulerException {
		List<SchedulerResponse> schedulerResponses =
			SchedulerEngineHelperUtil.getScheduledJobs();
		List<SchedulerResponse> schedulerResponsesFiltered =
			new ArrayList<>();

		AsciiTable at = new AsciiTable();
		SimpleDateFormat df = new SimpleDateFormat(DateUtil.ISO_8601_PATTERN);

		if (!"ALL".equals(status)) {

			schedulerResponsesFiltered = schedulerResponses.stream().filter(
				schedulerResponse -> status.equals(
					SchedulerEngineHelperUtil.getJobState(
						schedulerResponse).name())).collect(
				Collectors.toList());
		}
		else {
			schedulerResponsesFiltered = schedulerResponses;
		}

		schedulerResponsesFiltered.forEach(schedulerResponse -> {
			Collection<String> columnsValue = new ArrayList<>();

			columnsValue.add(schedulerResponse.getJobName());
			columnsValue.add(schedulerResponse.getGroupName());
			columnsValue.add(
				SchedulerEngineHelperUtil.getJobState(
					schedulerResponse).name());

			if (Validator.isNotNull(
				schedulerResponse.getTrigger().getStartDate())) {
				columnsValue.add(
					df.format(schedulerResponse.getTrigger().getStartDate()));
			}
			else {
				columnsValue.add(StringPool.DASH);
			}

			if (Validator.isNotNull(
				SchedulerEngineHelperUtil.getPreviousFireTime(
					schedulerResponse))) {
				columnsValue.add(
					df.format(
						SchedulerEngineHelperUtil.getPreviousFireTime(
							schedulerResponse)));
			}
			else {
				columnsValue.add(StringPool.DASH);
			}

			if (Validator.isNotNull(
				SchedulerEngineHelperUtil.getNextFireTime(schedulerResponse))) {
				columnsValue.add(
					df.format(
						SchedulerEngineHelperUtil.getNextFireTime(
							schedulerResponse)));
			}
			else {
				columnsValue.add(StringPool.DASH);
			}

			columnsValue.add(schedulerResponse.getStorageType().name());

			at.addRow(columnsValue);
			at.addRule();
		});

		if (schedulerResponsesFiltered.isEmpty()) {
			at.addRow("No Jobs found");
			at.setTextAlignment(TextAlignment.CENTER);
			at.addRule();
		}

		return at.render(160);
	}

	private static final String _COLUMN_DESTINATION_NAME = "Destination Name";

	private static final String _COLUMN_JOB_NAME = "Job Name";

	private static final String _COLUMN_FIRED_TIME = "Fired Time";

	private static final String _COLUMN_GROUP_NAME = "Group Name";

	private static final String _COLUMN_INSTANCE_NAME = "Instance Name";

	private static final String _COLUMN_STATE = "State";

	private static final String _COLUMN_START_TIME = "Start Time";

	private static final String _COLUMN_PREVIOUS_FIRE_TIME =
		"Previous Fire Time";

	private static final String _COLUMN_NEXT_FIRE_TIME = "Next Fire Time";

	private static final String _COLUMN_STORAGE_TYPE = "Next Fire Time";

}