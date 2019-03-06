package oracle.ucp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;

import javax.sql.XAConnection;

import oracle.ucp.admin.UniversalConnectionPoolManager;
import oracle.ucp.admin.UniversalConnectionPoolManagerImpl;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import oracle.ucp.jdbc.PoolXADataSource;

/*
 * Oracle recommends 1-10 connections per CPU core
 * 
 * https://laurent-leturgez.com/2015/06/01/oracle-12c-application-continuity-and-its-resources-usage/
 */
public class OracleUCPOptimization extends Logging {

	static String url = "jdbc:oracle:thin:@(DESCRIPTION=(SOURCE_ROUTE=YES) (ADDRESS=(PROTOCOL=TCP)(HOST=localhost)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=orcl)))";
	static String password = "app";
	static String user = "appdata";
	static UniversalConnectionPoolManager ucpm;
	static PoolXADataSource pool;
	
	static void createPoolFromUniversalConnectionPoolManager() throws Exception {
		ucpm = UniversalConnectionPoolManagerImpl.getUniversalConnectionPoolManager();
		
		pool = PoolDataSourceFactory.getPoolXADataSource();
		
		pool.setURL(url);
		pool.setPassword(password);
		pool.setUser(user);
		pool.setConnectionFactoryClassName("oracle.jdbc.xa.client.OracleXADataSource");
		pool.setInitialPoolSize(5);
		pool.setMaxPoolSize(10);
		
		//pool.setMaxConnectionReuseTime(10);
		
		ucpm.createConnectionPool((UniversalConnectionPoolAdapter) pool);
		
	}

	public static void main(String[] args) throws Exception {	
		createPoolFromUniversalConnectionPoolManager();

		XAConnection xaCon = pool.getXAConnection();
		System.out.println("oracle.ucp.jdbc.ValidConnection.isValid : "+ ((oracle.ucp.jdbc.ValidConnection)xaCon).isValid());
		
		Connection con = xaCon.getConnection(); 
		

		if (Proxy.isProxyClass(xaCon.getClass())) {
			InvocationHandler ihandle = Proxy.getInvocationHandler(xaCon);
			System.out.println(xaCon + "->Proxy->" + ihandle + " [XAConnection : " + (xaCon instanceof XAConnection)
					+ ", Connection :" + (xaCon instanceof Connection) + "]");
		}
		
		ResultSet rs = con.prepareStatement("select CURRENT_DATE from dual").executeQuery();

		while (rs.next()) {
			System.out.println(rs.getString(1));
		}

		System.out.println("Is current connection valid : "+con.isValid(0));
		rs.close();
		
		//Thread.sleep(15000);
		
		con.close();
		System.out.println("Is current connection valid : "+con.isValid(0));
		
		System.out.println("oracle.ucp.jdbc.ValidConnection.isValid : "+ ((oracle.ucp.jdbc.ValidConnection)xaCon).isValid());
       
		printStatistics(pool);
		
		ucpm.destroyConnectionPool(pool.getConnectionPoolName());
	}
}
