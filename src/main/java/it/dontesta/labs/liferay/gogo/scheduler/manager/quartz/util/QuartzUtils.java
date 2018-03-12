package it.dontesta.labs.liferay.gogo.scheduler.manager.quartz.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import it.dontesta.labs.liferay.gogo.scheduler.manager.quartz.pojo.FiredTrigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Antonio Musarra <antonio.musarra@gmail.com>
 */
public class QuartzUtils {

	protected QuartzUtils() {

	}

	/**
	 *
	 * @param triggerName
	 * @return
	 */
	public static int getFiredJobCount(String triggerName) {

		int count = 0;
		PreparedStatement pst = null;

		try {
			pst =
				_connection.prepareStatement(
					_SQL_FIRED_JOBS_COUNT_BY_TRIGGER_NAME);
			pst.setString(1, triggerName);

			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				count = rs.getInt(1);
			}
		}
		catch (SQLException e) {
			_log.warn(e.getMessage(), e);
		} finally {
			if (pst != null) {
				try {
					pst.close();
				}
				catch (SQLException e) {
					_log.warn(e.getMessage(), e);
				}
			}
		}

		return count;
	}

	/**
	 *
	 * @param triggerGroup
	 * @return
	 */
	public static int getFiredJobsCount(String triggerGroup) {
		int count = 0;
		PreparedStatement pst = null;

		try {
			pst =
				_connection.prepareStatement(
					_SQL_FIRED_JOBS_COUNT_BY_TRIGGER_GROUP);
			pst.setString(1, triggerGroup);

			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				count = rs.getInt(1);
			}
		}
		catch (SQLException e) {
			_log.warn(e.getMessage(), e);
		} finally {
			if (pst != null) {
				try {
					pst.close();
				}
				catch (SQLException e) {
					_log.warn(e.getMessage(), e);
				}
			}
		}

		return count;

	}

	/**
	 *
	 * @param triggerGroup
	 * @return
	 */
	public static List<FiredTrigger> getFiredTrigger(String triggerGroup) {
		PreparedStatement pst = null;
		List<FiredTrigger> firedTriggersList = new ArrayList<FiredTrigger>();

		try {
			pst =
				_connection.prepareStatement(
					_SQL_FIRED_JOBS_BY_TRIGGER_GROUP);
			pst.setString(1, triggerGroup);

			ResultSet rs = pst.executeQuery();

			while (rs.next()) {

				FiredTrigger firedTrigger = new FiredTrigger();

				firedTrigger.setSchedulerName(rs.getString(_FIELD_SCHED_NAME));
				firedTrigger.setEntryId(rs.getString(_FIELD_ENTRY_ID));
				firedTrigger.setTriggerName(rs.getString(_FIELD_TRIGGER_NAME));
				firedTrigger.setTriggerGroup(rs.getString(_FIELD_TRIGGER_GROUP));
				firedTrigger.setInstanceName(rs.getString(_FIELD_INSTANCE_NAME));
				firedTrigger.setState(rs.getString(_FIELD_STATE));

				firedTrigger.setFiredTime(
					new Timestamp(
						Long.parseLong(rs.getString(_FIELD_FIRED_TIME)))
				);

				firedTriggersList.add(firedTrigger);
			}
		}
		catch (SQLException e) {
			_log.warn(e.getMessage(), e);
		} finally {
			if (pst != null) {
				try {
					pst.close();
				}
				catch (SQLException e) {
					_log.warn(e.getMessage(), e);
				}
			}
		}

		return firedTriggersList;
	}


	private static final String _SQL_FIRED_JOBS_COUNT_BY_TRIGGER_NAME =
		"SELECT COUNT(*) FROM QUARTZ_FIRED_TRIGGERS WHERE TRIGGER_NAME = ?";

	private static final String _SQL_FIRED_JOBS_COUNT_BY_TRIGGER_GROUP =
		"SELECT COUNT(*) FROM QUARTZ_FIRED_TRIGGERS WHERE TRIGGER_GROUP = ?";

	private static final String _SQL_FIRED_JOBS_BY_TRIGGER_GROUP =
		"SELECT * FROM QUARTZ_FIRED_TRIGGERS WHERE TRIGGER_GROUP = ?";

	private static final String _FIELD_SCHED_NAME = "SCHED_NAME";

	private static final String _FIELD_ENTRY_ID = "ENTRY_ID";

	private static final String _FIELD_TRIGGER_NAME = "TRIGGER_NAME";

	private static final String _FIELD_TRIGGER_GROUP = "TRIGGER_GROUP";

	private static final String _FIELD_INSTANCE_NAME = "INSTANCE_NAME";

	private static final String _FIELD_FIRED_TIME = "FIRED_TIME";

	private static final String _FIELD_STATE = "STATE";


	private static final Log _log = LogFactoryUtil.getLog(
		QuartzConnectionProvider.class);

	private static Connection _connection =
		QuartzConnectionProvider.getConnection();

}
