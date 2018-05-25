package oracle.ucp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Set;

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

		rs.close();
		con.close();
		printThreads();
		
		for (String s : ucpm.getConnectionPoolNames()) {
			System.out.println(s);
		}
		
		ucpm.destroyConnectionPool(pool.getConnectionPoolName());
	}

	
	private static void printThreads() {
		Set<Thread> threads = Thread.getAllStackTraces().keySet();
		System.out.println("---------------------------------------------");
		for (Thread t : threads) {
			String name = t.getName();
		    Thread.State state = t.getState();
		    int priority = t.getPriority();
		    String type = t.isDaemon() ? "Daemon" : "Normal";
		    System.out.printf("%-20s \t %s \t %d \t %s\n", name, state, priority, type);
		}
		System.out.println("---------------------------------------------");
	}
	
	
}
