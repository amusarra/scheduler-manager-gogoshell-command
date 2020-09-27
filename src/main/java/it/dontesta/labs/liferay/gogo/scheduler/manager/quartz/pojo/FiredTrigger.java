/*
  MIT License
  Copyright (c) 2020 Antonio Musarra's Blog - https://www.dontesta.it

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.
 */

package it.dontesta.labs.liferay.gogo.scheduler.manager.quartz.pojo;

import java.util.Date;

/**
 * @author Antonio Musarra <antonio.musarra@gmail.com>
 */
public class FiredTrigger {

	public String getEntryId() {
		return _entryId;
	}

	public Date getFiredTime() {
		return _firedTime;
	}

	public String getInstanceName() {
		return _instanceName;
	}

	public String getSchedulerName() {
		return _schedulerName;
	}

	public String getState() {
		return _state;
	}

	public String getTriggerGroup() {
		return _triggerGroup;
	}

	public String getTriggerName() {
		return _triggerName;
	}

	public void setEntryId(String entryId) {
		_entryId = entryId;
	}

	public void setFiredTime(Date firedTime) {
		_firedTime = firedTime;
	}

	public void setInstanceName(String instanceName) {
		_instanceName = instanceName;
	}

	public void setSchedulerName(String schedulerName) {
		_schedulerName = schedulerName;
	}

	public void setState(String state) {
		_state = state;
	}

	public void setTriggerGroup(String triggerGroup) {
		_triggerGroup = triggerGroup;
	}

	public void setTriggerName(String triggerName) {
		_triggerName = triggerName;
	}

	private String _entryId;
	private Date _firedTime;
	private String _instanceName;
	private String _schedulerName;
	private String _state;
	private String _triggerGroup;
	private String _triggerName;

}