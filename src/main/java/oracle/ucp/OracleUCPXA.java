package oracle.ucp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Set;

import javax.sql.XAConnection;

import oracle.ucp.jdbc.PoolDataSourceFactory;
import oracle.ucp.jdbc.PoolXADataSource;

public class OracleUCPXA  extends Logging {
	
	static String url = "jdbc:oracle:thin:@localhost:1521/orcl";
	static String password = "app";
	static String user = "appdata";

	public static void main(String[] args) throws Exception {
		printThreads();
		
		PoolXADataSource pool = PoolDataSourceFactory.getPoolXADataSource(); //XA
		pool.setConnectionFactoryClassName("oracle.jdbc.xa.client.OracleXADataSource");
		pool.setURL(url);
		pool.setUser(user);
		pool.setPassword(password);
		pool.setInitialPoolSize(2);
		pool.setMaxPoolSize(5);

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
