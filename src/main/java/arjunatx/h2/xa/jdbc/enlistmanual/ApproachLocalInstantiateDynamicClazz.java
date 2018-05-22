package arjunatx.h2.xa.jdbc.enlistmanual;

import java.sql.SQLException;

import javax.sql.XADataSource;

import org.h2.jdbcx.JdbcDataSource;

import com.arjuna.ats.internal.jdbc.DynamicClass;

public class ApproachLocalInstantiateDynamicClazz implements DynamicClass {
	
	private static final String DB_USER = "";
	private static final String DB_PASSWORD = "";
	private static final String DB_URL = "jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1";

	@Override
	public XADataSource getDataSource(String dbName) throws SQLException {
		JdbcDataSource ds = new JdbcDataSource();;
		ds.setPassword(DB_PASSWORD);
		ds.setUser(DB_USER);
		ds.setUrl(String.format(DB_URL, dbName));
		return ds;
	}

}
