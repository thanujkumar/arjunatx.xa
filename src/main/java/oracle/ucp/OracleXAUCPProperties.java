package oracle.ucp;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.logging.LogManager;

import javax.sql.XAConnection;

import oracle.ucp.admin.UniversalConnectionPoolManager;
import oracle.ucp.admin.UniversalConnectionPoolManagerImpl;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import oracle.ucp.jdbc.PoolXADataSource;

public class OracleXAUCPProperties {
	static {

		// Setting logging
		oracle.jdbc.driver.OracleLog.setTrace(true);
		// OR
		System.setProperty("oracle.jdbc.Trace", "true");
		// Replace the existing Oracle driver with the _g version of the driver (for
		// example, ojdbc6_g.jar) in both the file system and in the Classpath field of
		// the Oracle JDBC Provider configuration. The _g driver is a debug version of
		// the driver and can be obtained from Oracle

		InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("mylogging.properties");
		try {
			LogManager.getLogManager().readConfiguration(is);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}

	}

	static String url = "jdbc:oracle:thin:@(DESCRIPTION=(SOURCE_ROUTE=YES) (ADDRESS=(PROTOCOL=TCP)(HOST=localhost)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=orcl)))";
	static String password = "app";
	static String user = "appdata";
	static UniversalConnectionPoolManager ucpm;
	static PoolXADataSource pool;

	static void createPool() throws Exception {

		ucpm = UniversalConnectionPoolManagerImpl.getUniversalConnectionPoolManager();

		// ucpm.setLogLevel(Level.FINEST);

		pool = PoolDataSourceFactory.getPoolXADataSource(); // XA
		pool.setConnectionFactoryClassName("oracle.jdbc.xa.client.OracleXADataSource");
		pool.setURL(url);
		pool.setUser(user);
		pool.setPassword(password);
		pool.setInitialPoolSize(5);
		pool.setMaxPoolSize(10);
		pool.setFastConnectionFailoverEnabled(true);

		pool.setValidateConnectionOnBorrow(true);
		/*
		 * The setSQLForValidateConnection property is not recommended when using an
		 * Oracle JDBC driver. UCP performs an internal ping when using an Oracle JDBC
		 * driver. The mechanism is faster than executing an SQL statement and is
		 * overridden if this property is set. Instead, set the
		 * setValidateConnectionOnBorrow property to true and do not include the
		 * setSQLForValidateConnection property
		 */
		pool.setSecondsToTrustIdleConnection(10); //till 10seconds no validation when borrowed
		
		// For pool to initialize we need to call XAConnection first time
		XAConnection xacon = pool.getXAConnection();
		xacon.close();
	}


	public static void main(String[] args) throws Exception {
		createPool();

		XAConnection xacon = pool.getXAConnection();
		System.out.println("oracle.ucp.jdbc.ValidConnection.isValid : "+ ((oracle.ucp.jdbc.ValidConnection)xacon).isValid());
		
		Connection con = xacon.getConnection();

		ResultSet rs = con.prepareStatement("select CURRENT_DATE from dual").executeQuery();

		while (rs.next()) {
			System.out.println(rs.getString(1));
		}

		System.out.println("Is current connection valid : "+con.isValid(0));
		rs.close();
		con.close();
		System.out.println("Is current connection valid : "+con.isValid(0));
		
		System.out.println("oracle.ucp.jdbc.ValidConnection.isValid : "+ ((oracle.ucp.jdbc.ValidConnection)xacon).isValid());
       
		ucpm.destroyConnectionPool(pool.getConnectionPoolName());
	}

}
