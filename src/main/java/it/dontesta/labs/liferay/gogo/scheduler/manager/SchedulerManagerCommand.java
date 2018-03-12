/*
 * MIT License
 * Copyright (c) 2017 Antonio Musarra's Blog - https://www.dontesta.it
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
 **/

package it.dontesta.labs.liferay.gogo.scheduler.manager;

import static org.fusesource.jansi.Ansi.ansi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.felix.service.command.Descriptor;
import org.apache.felix.service.command.Parameter;
import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelperUtil;
import com.liferay.portal.kernel.scheduler.SchedulerException;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.StringPool;
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
				"osgi.command.scope=scheduler" 
		}, 
		service = Object.class
)
@Descriptor("Gogo Shell Command Series for Liferay "
		+ "JOBS Management (Example: job list, pause jobs, resume jobs, stop, etc.).")
public class SchedulerManagerCommand {
	
	/**
	 * Print the list of the all Jobs filtered by state (default ALL)
	 * 
	 * @param triggerState
	 *            The trigger state. Possible values are
	 *            COMPLETE,NORMAL,EXPIRED,PAUSED,UNSCHEDULED
	 * @throws PortalException
	 */
	@Descriptor("List of the all Jobs filtered by state (default ALL)")
	public void list(
		@Descriptor("Filter the jobs by trigger state {state: COMPLETE,NORMAL,EXPIRED,PAUSED,UNSCHEDULED}") 
		@Parameter(names = {
			"--status", "-s"
		}, absentValue = "ALL") String triggerState)
		throws PortalException {

		System.out.println(
			ansi().eraseScreen().render(
				"@|green List of the jobs filtered by state:|@ @|red " + triggerState + " |@"));
		System.out.println(getJobsListTableHeader());
		System.out.println(getJobsListTableRows(triggerState));
	}

	/**
	 * Print detail info of the job.
	 * 
	 * @param jobName
	 *            The name of the job
	 * @param groupName
	 *            The group name of the job
	 * @param storageType
	 *            The Storage Type of the job. The Storage Type values are
	 *            MEMORY, MEMORY_CLUSTERED, PERSISTED
	 * @throws PortalException
	 */
	@Descriptor("Detail info of the job")
	public void info(
		@Descriptor("The JobName") String jobName,
		@Descriptor("The GroupName") String groupName,
		@Descriptor("The StorageType {MEMORY, MEMORY_CLUSTERED, PERSISTED}") String storageType)
		throws PortalException {

		SchedulerResponse schedulerResponse =
			SchedulerEngineHelperUtil.getScheduledJob(
				jobName, groupName, StorageType.valueOf(storageType));

		if (Validator.isNull(schedulerResponse)) {
			throw new PortalException("Job not found with the name " + jobName);
		}
		
		SimpleDateFormat df = new SimpleDateFormat(DateUtil.ISO_8601_PATTERN);
		AsciiTable at = new AsciiTable();

		at.setPadding(5);
		at.addRule();
		at.addRow("Job Name", schedulerResponse.getJobName());
		at.addRule();
		at.addRow("Group Name", schedulerResponse.getGroupName());
		at.addRule();
		at.addRow(
			"State",
			SchedulerEngineHelperUtil.getJobState(schedulerResponse).name());
		at.addRule();
	
		if (Validator.isNotNull(
			schedulerResponse.getTrigger().getStartDate())) {
			at.addRow("Start Time",
				df.format(schedulerResponse.getTrigger().getStartDate()));
			at.addRule();
		}
		else {
			at.addRow("Start Time", StringPool.DASH);
			at.addRule();
		}

		if (Validator.isNotNull(
			SchedulerEngineHelperUtil.getPreviousFireTime(
				schedulerResponse))) {
			at.addRow("Previous Fire Time",
				df.format(
					SchedulerEngineHelperUtil.getPreviousFireTime(
						schedulerResponse)));
			at.addRule();
		}
		else {
			at.addRow("Previous Fire Time", StringPool.DASH);
			at.addRule();
		}

		if (Validator.isNotNull(
			SchedulerEngineHelperUtil.getNextFireTime(schedulerResponse))) {
			at.addRow("Next Fire Time",
				df.format(
					SchedulerEngineHelperUtil.getNextFireTime(
						schedulerResponse)));
			at.addRule();
		}
		else {
			at.addRow("Next Fire Time", StringPool.DASH);
			at.addRule();
		}

		at.addRow("Cron Expression", SchedulerEngineHelperUtil.getCronText(Calendar.getInstance(), false));
		at.addRule();
		at.addRow("Destination Name", schedulerResponse.getDestinationName());
		at.addRule();
		at.addRow("Storage Type", schedulerResponse.getStorageType());
		at.addRule();
		
		if (Validator.isNotNull(SchedulerEngineHelperUtil.getJobExceptions(
				jobName, groupName, StorageType.valueOf(storageType)))) {
			at.addRow(
				"Job Exceptions", SchedulerEngineHelperUtil.getJobExceptions(
					jobName, groupName, StorageType.valueOf(storageType)));
			at.addRule();
		} else {
			at.addRow("Job Exceptions", StringPool.DASH);
			at.addRule();
		}

		System.out.println(
			ansi().eraseScreen().render(
				"@|green Detail of the job:|@ @|red " + jobName + " |@"));
		System.out.println(at.render(160));
	}

	/**
	 * Pause Job by Job Name, Group Name and Storage Type
	 * 
	 * @param jobName
	 *            The name of the job
	 * @param groupName
	 *            The group name of the job
	 * @param storageType
	 *            The Storage Type of the job. The Storage Type values are
	 *            MEMORY, MEMORY_CLUSTERED, PERSISTED
	 * @throws PortalException
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
	 * @param groupName
	 *            The group name of the job
	 * @param storageType
	 *            The Storage Type of the job. The Storage Type values are
	 *            MEMORY, MEMORY_CLUSTERED, PERSISTED
	 * @throws PortalException
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
	 * @param jobName
	 *            The name of the job
	 * @param groupName
	 *            The group name of the job
	 * @param storageType
	 *            The Storage Type of the job. The Storage Type values are
	 *            MEMORY, MEMORY_CLUSTERED, PERSISTED
	 * @throws PortalException
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
	 * @param groupName
	 *            The group name of the job
	 * @param storageType
	 *            The Storage Type of the job. The Storage Type values are
	 *            MEMORY, MEMORY_CLUSTERED, PERSISTED
	 * @throws PortalException
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
	 * Return the jobs list table header.
	 * 
	 * @return String Formatted table header
	 */
	private String getJobsListTableHeader() {
		AsciiTable at = new AsciiTable();
		at.addRule();
		at.addRow(
			"Job Name", "Group Name", "State", "Start Time",
			"Previous Fire Time", "Next Fire Time", "Storage Type");
		at.addRule();;
		return at.render(160);
	}
	
	/**
	 * Return the jobs list table rows.
	 * 
	 * @param status
	 * @return String Formatted table rows
	 * @throws SchedulerException
	 */
	private String getJobsListTableRows(String status) throws SchedulerException {
		List<SchedulerResponse> schedulerResponses =
						SchedulerEngineHelperUtil.getScheduledJobs();
		List<SchedulerResponse> schedulerResponsesFiltered =
			new ArrayList<SchedulerResponse>();

		AsciiTable at = new AsciiTable();
		SimpleDateFormat df = new SimpleDateFormat(DateUtil.ISO_8601_PATTERN);

		if (!"ALL".equals(status)) {
			
			schedulerResponsesFiltered = schedulerResponses.stream().filter(
				schedulerResponse -> status.equals(
					SchedulerEngineHelperUtil.getJobState(
						schedulerResponse).name())).collect(
							Collectors.toList());
		} else {
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

		if (schedulerResponsesFiltered.size() == 0) {
			at.addRow("No Jobs found");
			at.setTextAlignment(TextAlignment.CENTER);
			at.addRule();
		}
		
		return at.render(160);
	}
}