package oracle.ucp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Properties;

import javax.sql.XAConnection;

import oracle.jdbc.driver.OracleConnection;
import oracle.ucp.admin.UniversalConnectionPoolManager;
import oracle.ucp.admin.UniversalConnectionPoolManagerImpl;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import oracle.ucp.jdbc.PoolXADataSource;

/*
 
 Name	           Short Name	Type	Description
user	             n/a	    String	the user name for logging into the database
password	         n/a	    String	the password for logging into the database
database	         server	    String	the connect string for the database
internal_logon	     n/a	    String	a role, such as sysdba or sysoper, that allows you to log on as sys
defaultRowPrefetch  prefetch	String (containing integer value)	the default number of rows to prefetch from the server (default value is "10")
remarksReporting    remarks	    String (containing boolean value)	"true" if getTables() and getColumns() should report TABLE_REMARKS; equivalent to using setRemarksReporting() (default value is "false")
defaultBatchValue   batchvalue	String (containing integer value)	the default batch value that triggers an execution request (default value is "10")
includeSynonyms	    synonyms	String (containing boolean value)	"true" to include column information from predefined "synonym" SQL entities when you execute a DataBaseMetaData getColumns() call; equivalent to connection setIncludeSynonyms() call (default value is "false")
processEscapes		            String (containing boolean value)	"true" if escape processing is enabled for all statements, "false" if escape processing is disabled (default value is "false")

 
 */

public class OracleUCPXAConProperties extends Logging {

	static String url = "jdbc:oracle:thin:@(DESCRIPTION=(SOURCE_ROUTE=YES)(COMPRESSION=on)(COMPRESSION_LEVELS=(LEVEL=low)(LEVEL=high)) (ADDRESS=(PROTOCOL=TCP)(HOST=localhost)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=orcl)))";
	static String user = "sys";
	static String password = "oracle";
	

	public static void main(String[] args) throws Exception {
		UniversalConnectionPoolManager ucpm = UniversalConnectionPoolManagerImpl.getUniversalConnectionPoolManager();
		
		printThreads();
		
		Properties conProp = new Properties();
		conProp.put(OracleConnection.CONNECTION_PROPERTY_USER_NAME, user);
		conProp.put(OracleConnection.CONNECTION_PROPERTY_PASSWORD, password);
		conProp.put(OracleConnection.CONNECTION_PROPERTY_INTERNAL_LOGON, "sysdba");
		conProp.put(OracleConnection.CONNECTION_PROPERTY_NETWORK_COMPRESSION, "on");
		conProp.put("remarksReporting", "true");
		
		
		PoolXADataSource pool = PoolDataSourceFactory.getPoolXADataSource(); //XA
		pool.setConnectionFactoryClassName("oracle.jdbc.xa.client.OracleXADataSource");
		pool.setURL(url);
		pool.setInitialPoolSize(5);
		pool.setMaxPoolSize(10);
		pool.setConnectionProperties(conProp);
		
		

		XAConnection xaCon = pool.getXAConnection(); // This is com.sun.proxy object


		if (Proxy.isProxyClass(xaCon.getClass())) {
			InvocationHandler ihandle = Proxy.getInvocationHandler(xaCon);
			System.out.println(xaCon + "->Proxy->" + ihandle + " [XAConnection : " + (xaCon instanceof XAConnection)
					+ ", Connection :" + (xaCon instanceof Connection) + "]");
		}
		
		printStatistics(pool);

		Connection con = xaCon.getConnection();
		
		printThreads();

		ResultSet rs = con.prepareStatement("select CURRENT_DATE from dual").executeQuery();

		while (rs.next()) {
			System.out.println(rs.getString(1));
		}

		rs.close();
		con.close();

		printThreads();
		
		printStatistics(pool);
		
		ucpm.destroyConnectionPool(pool.getConnectionPoolName());
	}

}
