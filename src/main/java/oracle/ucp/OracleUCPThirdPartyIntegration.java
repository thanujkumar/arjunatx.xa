package oracle.ucp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;

import javax.sql.XAConnection;

import oracle.ucp.admin.UniversalConnectionPoolManager;
import oracle.ucp.admin.UniversalConnectionPoolManagerImpl;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import oracle.ucp.jdbc.PoolDataSourceImpl;

/*
 * UCP Integration with Third-Party Products
 * 
 * Two data source classes are available as integration points with UCP:
 * PoolDataSourceImpl for non-XA connection pools and PoolXADataSourceImpl for
 * XA connection pools. These classes are implementations of the PoolDataSource
 * and PoolXADataSource interfaces, respectively, and contain default
 * constructors
 * 
 * These implementations explicitly create connection pool instances and can return connections
 */
public class OracleUCPThirdPartyIntegration extends Logging {

	static String url = "jdbc:oracle:thin:@(DESCRIPTION=(SOURCE_ROUTE=YES) (ADDRESS=(PROTOCOL=TCP)(HOST=localhost)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=orcl)))";
	static String password = "app";
	static String user = "appdata";
	static UniversalConnectionPoolManager ucpm;
//	static PoolXADataSource pool;
	static PoolDataSource pool;
	
	static void createPool() throws Exception {
		ucpm = UniversalConnectionPoolManagerImpl.getUniversalConnectionPoolManager();
//		pool = PoolDataSourceFactory.getPoolDataSource();
		pool = new PoolDataSourceImpl();
		
//		pool = PoolDataSourceFactory.getPoolXADataSource();
//		pool = new PoolXADataSourceImpl();
		
		pool.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");
//		pool.setConnectionFactoryClassName("oracle.jdbc.xa.client.OracleXADataSource");
		pool.setURL(url);
		pool.setUser(user);
		pool.setPassword(password);
		pool.setInitialPoolSize(5);
		pool.setMaxPoolSize(10);
		pool.setFastConnectionFailoverEnabled(true);
		

		pool.setValidateConnectionOnBorrow(true);
	}

	public static void main(String[] args) throws Exception {	
		createPool();

		Connection con = pool.getConnection(); //For XA java.sql.SQLException: The method is disabled so pool.getXAConnection().getConnection()
		System.out.println("oracle.ucp.jdbc.ValidConnection.isValid : "+ ((oracle.ucp.jdbc.ValidConnection)con).isValid());
		

		if (Proxy.isProxyClass(con.getClass())) {
			InvocationHandler ihandle = Proxy.getInvocationHandler(con);
			System.out.println(con + "->Proxy->" + ihandle + " [XAConnection : " + (con instanceof XAConnection)
					+ ", Connection :" + (con instanceof Connection) + "]");
		}
		
		ResultSet rs = con.prepareStatement("select CURRENT_DATE from dual").executeQuery();

		while (rs.next()) {
			System.out.println(rs.getString(1));
		}

		System.out.println("Is current connection valid : "+con.isValid(0));
		rs.close();
		con.close();
		System.out.println("Is current connection valid : "+con.isValid(0));
		
		System.out.println("oracle.ucp.jdbc.ValidConnection.isValid : "+ ((oracle.ucp.jdbc.ValidConnection)con).isValid());
       
		ucpm.destroyConnectionPool(pool.getConnectionPoolName());
	}
}
