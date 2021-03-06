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

package it.dontesta.labs.liferay.gogo.scheduler.manager.quartz.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.kernel.util.Validator;

import it.dontesta.labs.liferay.gogo.scheduler.manager.quartz.pojo.FiredTrigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

/**
 * @author Antonio Musarra <antonio.musarra@gmail.com>
 */
public class QuartzUtils {

	/**
	 * @param triggerName
	 * @return
	 */
	public static int getFiredJobCount(String triggerName) {
		int countFiredJobCount = 0;

		try (PreparedStatement pst = _getConnection().prepareStatement(
				_SQL_FIRED_JOBS_COUNT_BY_TRIGGER_NAME)) {

			pst.setString(1, triggerName);

			try (ResultSet rs = pst.executeQuery()) {
				if (rs.next()) {
					countFiredJobCount = rs.getInt(1);
				}
			}
			catch (SQLException sqle) {
				_log.warn(sqle.getMessage(), sqle);
			}
		}
		catch (SQLException sqle) {
			_log.warn(sqle.getMessage(), sqle);
		}

		return countFiredJobCount;
	}

	/**
	 * @param triggerGroup
	 * @return
	 */
	public static int getFiredJobsCount(String triggerGroup) {
		int countFiredJobsCount = 0;

		try (PreparedStatement pst = _getConnection().prepareStatement(
				_SQL_FIRED_JOBS_COUNT_BY_TRIGGER_GROUP)) {

			pst.setString(1, triggerGroup);

			try (ResultSet rs = pst.executeQuery()) {
				if (rs.next()) {
					countFiredJobsCount = rs.getInt(1);
				}
			}
			catch (SQLException sqle) {
				_log.warn(sqle.getMessage(), sqle);
			}
		}
		catch (SQLException sqle) {
			_log.warn(sqle.getMessage(), sqle);
		}

		return countFiredJobsCount;
	}

	/**
	 * @param triggerGroup
	 * @return
	 */
	public static List<FiredTrigger> getFiredTrigger(String triggerGroup) {
		List<FiredTrigger> firedTriggersList = new ArrayList<>();

		try (PreparedStatement pst = _getConnection().prepareStatement(
				_SQL_FIRED_JOBS_BY_TRIGGER_GROUP)) {

			pst.setString(1, triggerGroup);

			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					FiredTrigger firedTrigger = new FiredTrigger();

					firedTrigger.setSchedulerName(
						rs.getString(_FIELD_SCHED_NAME));
					firedTrigger.setEntryId(rs.getString(_FIELD_ENTRY_ID));
					firedTrigger.setTriggerName(
						rs.getString(_FIELD_TRIGGER_NAME));
					firedTrigger.setTriggerGroup(
						rs.getString(_FIELD_TRIGGER_GROUP));
					firedTrigger.setInstanceName(
						rs.getString(_FIELD_INSTANCE_NAME));
					firedTrigger.setState(rs.getString(_FIELD_STATE));

					firedTrigger.setFiredTime(
						new Date(rs.getLong(_FIELD_FIRED_TIME)));

					firedTriggersList.add(firedTrigger);
				}
			}
			catch (SQLException sqle) {
				_log.warn(sqle.getMessage(), sqle);
			}
		}
		catch (SQLException sqle) {
			_log.warn(sqle.getMessage(), sqle);
		}

		return firedTriggersList;
	}

	protected QuartzUtils() {
	}

	private static Connection _getConnection() {
		if (Validator.isNotNull(_connection)) {
			return _connection;
		}

		try {
			DataSource dataSource = InfrastructureUtil.getDataSource();

			_connection = dataSource.getConnection();
		}
		catch (Exception e) {
			_log.warn(e, e);
		}

		return _connection;
	}

	private static final String _FIELD_ENTRY_ID = "ENTRY_ID";

	private static final String _FIELD_FIRED_TIME = "FIRED_TIME";

	private static final String _FIELD_INSTANCE_NAME = "INSTANCE_NAME";

	private static final String _FIELD_SCHED_NAME = "SCHED_NAME";

	private static final String _FIELD_STATE = "STATE";

	private static final String _FIELD_TRIGGER_GROUP = "TRIGGER_GROUP";

	private static final String _FIELD_TRIGGER_NAME = "TRIGGER_NAME";

	private static final String _SQL_FIRED_JOBS_BY_TRIGGER_GROUP =
		"SELECT * FROM QUARTZ_FIRED_TRIGGERS WHERE TRIGGER_GROUP = ?";

	private static final String _SQL_FIRED_JOBS_COUNT_BY_TRIGGER_GROUP =
		"SELECT COUNT(*) FROM QUARTZ_FIRED_TRIGGERS WHERE TRIGGER_GROUP = ?";

	private static final String _SQL_FIRED_JOBS_COUNT_BY_TRIGGER_NAME =
		"SELECT COUNT(*) FROM QUARTZ_FIRED_TRIGGERS WHERE TRIGGER_NAME = ?";

	private static final Log _log = LogFactoryUtil.getLog(QuartzUtils.class);

	private static Connection _connection = null;

}