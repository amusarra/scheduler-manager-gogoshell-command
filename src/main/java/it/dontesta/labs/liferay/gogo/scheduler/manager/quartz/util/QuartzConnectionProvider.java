package it.dontesta.labs.liferay.gogo.scheduler.manager.quartz.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.InfrastructureUtil;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author Antonio Musarra <antonio.musarra@gmail.com>
 */
public class QuartzConnectionProvider {

	protected QuartzConnectionProvider() {
	}

	public static Connection getConnection() {

		if (_connection == null) {
			synchronized (QuartzConnectionProvider.class) {
				if (_connection == null) {

					try {
						DataSource dataSource =
							InfrastructureUtil.getDataSource();

						_connection = dataSource.getConnection();
					}

					catch (Exception e) {
						_log.warn(e, e);
					}
				}
			}
		}

		return _connection;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		QuartzConnectionProvider.class);

	private static Connection _connection = null;

}
