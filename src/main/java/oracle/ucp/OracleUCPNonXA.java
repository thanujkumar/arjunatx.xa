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

public class OracleUCPNonXA  extends Logging{

	static String url = "jdbc:oracle:thin:@localhost:1521/orcl";
	static String password = "app";
	static String user = "appdata";

	public static void main(String[] args) throws Exception {
		UniversalConnectionPoolManager ucpm = UniversalConnectionPoolManagerImpl.getUniversalConnectionPoolManager();
				
		printThreads();
		
		PoolDataSource pool = PoolDataSourceFactory.getPoolDataSource(); //non XA
		pool.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");
		pool.setURL(url);
		pool.setUser(user);
		pool.setPassword(password);
		pool.setInitialPoolSize(2);
		pool.setMaxPoolSize(5);
        		
		
	
		Connection con = pool.getConnection();

		if (Proxy.isProxyClass(con.getClass())) {
			InvocationHandler ihandle = Proxy.getInvocationHandler(con);
			System.out.println(con + "->Proxy->" + ihandle + " [XAConnection : " + (con instanceof XAConnection)
					+ ", Connection :" + (con instanceof Connection) + "]");
		}
		

		printThreads();
		
		ResultSet rs = con.prepareStatement("select CURRENT_DATE from dual").executeQuery();

		while (rs.next()) {
			System.out.println(rs.getString(1));
		}

		printStatistics(pool);
		
		rs.close();
		con.close();
		printThreads();
		
		for (String s : ucpm.getConnectionPoolNames()) {
			System.out.println(s);
		}
		
		printStatistics(pool);
		
		ucpm.destroyConnectionPool(pool.getConnectionPoolName());
	}
	
}
