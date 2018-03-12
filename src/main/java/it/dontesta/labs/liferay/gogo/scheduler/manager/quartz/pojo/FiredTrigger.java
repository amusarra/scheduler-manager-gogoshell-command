package it.dontesta.labs.liferay.gogo.scheduler.manager.quartz.pojo;

import java.util.Date;

/**
 * @author Antonio Musarra <antonio.musarra@gmail.com>
 */
public class FiredTrigger {

	private String schedulerName;
	private String entryId;
	private String triggerName;
	private String triggerGroup;
	private String instanceName;
	private String firedTime;
	private String state;


	public String getSchedulerName() {
		return schedulerName;
	}

	public void setSchedulerName(String schedulerName) {
		this.schedulerName = schedulerName;
	}

	public String getEntryId() {
		return entryId;
	}

	public void setEntryId(String entryId) {
		this.entryId = entryId;
	}

	public String getTriggerName() {
		return triggerName;
	}

	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}

	public String getTriggerGroup() {
		return triggerGroup;
	}

	public void setTriggerGroup(String triggerGroup) {
		this.triggerGroup = triggerGroup;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public String getFiredTime() {
		return firedTime;
	}

	public void setFiredTime(String firedTime) {
		this.firedTime = firedTime;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}
