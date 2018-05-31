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

public class OracleUCPXA  extends Logging { 
	
	static String _url2 = "jdbc:oracle:thin:@(DESCRIPTION_LIST=(LOAD_BALANCE=ON)(FAILOVER=ON)(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=localhost)(PORT=1521))(CONNECT_DATA=(SERVER=DEDICATED)(SERVICE_NAME=orcl)))(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=127.0.1)(PORT=1521))(CONNECT_DATA=(SERVER=DEDICATED)(SERVICE_NAME=orcl))))";
	static String _url = "jdbc:oracle:thin:@(DESCRIPTION=(SOURCE_ROUTE=YES) (ADDRESS=(PROTOCOL=TCP)(HOST=localhost)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=orcl)))";
	static String url = "jdbc:oracle:thin:@localhost:1521/orcl";
	static String password = "app";
	static String user = "appdata";

	public static void main(String[] args) throws Exception {
		UniversalConnectionPoolManager ucpm = UniversalConnectionPoolManagerImpl.getUniversalConnectionPoolManager();
		
		printThreads();
		
		PoolXADataSource pool = PoolDataSourceFactory.getPoolXADataSource(); //XA
		pool.setConnectionFactoryClassName("oracle.jdbc.xa.client.OracleXADataSource");
		pool.setURL(_url2);
		pool.setUser(user);
		pool.setPassword(password);
		pool.setInitialPoolSize(2);
		pool.setMaxPoolSize(5);
		pool.setFastConnectionFailoverEnabled(true);
		//pool.setONSConfiguration(arg0);

		XAConnection xaCon = pool.getXAConnection(); // This is com.sun.proxy object


		if (Proxy.isProxyClass(xaCon.getClass())) {
			InvocationHandler ihandle = Proxy.getInvocationHandler(xaCon);
			System.out.println(xaCon + "->Proxy->" + ihandle + " [XAConnection : " + (xaCon instanceof XAConnection)
					+ ", Connection :" + (xaCon instanceof Connection) + "]");
		}
		

		Connection con = xaCon.getConnection();
		
		printThreads();

		ResultSet rs = con.prepareStatement("select CURRENT_DATE from dual").executeQuery();

		while (rs.next()) {
			System.out.println(rs.getString(1));
		}

		rs.close();
		con.close();

		printThreads();
		
		ucpm.destroyConnectionPool(pool.getConnectionPoolName());
	}

}
