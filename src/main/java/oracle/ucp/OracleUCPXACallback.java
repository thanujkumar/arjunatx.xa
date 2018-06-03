package oracle.ucp;

import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.PlatformLoggingMXBean;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.LogManager;

import javax.sql.XAConnection;

import oracle.ucp.admin.UniversalConnectionPoolManager;
import oracle.ucp.admin.UniversalConnectionPoolManagerImpl;
import oracle.ucp.jdbc.ConnectionInitializationCallback;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import oracle.ucp.jdbc.PoolXADataSource;

public class OracleUCPXACallback {

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
		
		// For JMX  pool to be initialized we need to call XAConnection first time
		XAConnection xacon = pool.getXAConnection();
		xacon.close();
		checkMBean();
	}

	public static void checkMBean() throws Exception {
		String loader = Thread.currentThread().getContextClassLoader().toString().replaceAll("[,=:\"]+", "");
		System.out.println("Loader Name: "+ loader);
		javax.management.ObjectName name = new javax.management.ObjectName("com.oracle.jdbc:type=diagnosability,name=" + loader);
		javax.management.MBeanServer mbs = java.lang.management.ManagementFactory.getPlatformMBeanServer();
	
		System.out.println("LoggingEnabled = " + mbs.getAttribute(name, "LoggingEnabled"));
		mbs.setAttribute(name, new javax.management.Attribute("LoggingEnabled", true));
		System.out.println("LoggingEnabled = " + mbs.getAttribute(name, "LoggingEnabled"));

		PlatformLoggingMXBean logging = ManagementFactory.getPlatformMXBean(PlatformLoggingMXBean.class);
		System.out.println("PlatformLoggingMXBean = " + logging);
	}

	public static void main(String[] args) throws Exception {
		createPool();
		pool.registerConnectionInitializationCallback(new ConnectionInitializationCallbackImpl());

		XAConnection xacon = pool.getXAConnection();
		
		Connection con = xacon.getConnection();

		ResultSet rs = con.prepareStatement("select CURRENT_DATE from dual").executeQuery();

		while (rs.next()) {
			System.out.println(rs.getString(1));
		}
       
		ucpm.destroyConnectionPool(pool.getConnectionPoolName());
	}

	////// Connection Initialization Callback

	/*
	 * One callback is created for every connection pool. This callback is not used
	 * if a labeling callback is registered for the connection pool
	 */

	static class ConnectionInitializationCallbackImpl implements ConnectionInitializationCallback {

		@Override
		public void initialize(Connection con) throws SQLException {
			if (Proxy.isProxyClass(con.getClass())) {
				InvocationHandler ihandle = Proxy.getInvocationHandler(con);
				System.out.println("Connection Initialized => " + con + "->Proxy->" + ihandle + " [XAConnection : "
						+ (con instanceof XAConnection) + ", Connection :" + (con instanceof Connection) + "]");
			} else {
				System.out.println("Connection Initialized => " + con);
			}
		}

	}
}
