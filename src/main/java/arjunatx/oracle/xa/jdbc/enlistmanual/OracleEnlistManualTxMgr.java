package arjunatx.oracle.xa.jdbc.enlistmanual;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.TransactionManager;

import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import oracle.ucp.jdbc.PoolXADataSource;

/*
 * http://jbossts.blogspot.in/2017/12/narayana-jdbc-transactional-driver.html
 */
public class OracleEnlistManualTxMgr {
	
	static {
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
		ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setLevel(Level.ALL);
		consoleHandler.setFormatter(new SimpleFormatter());

		Logger app1 = Logger.getLogger("oracle.ucp"); // If you want to log everything just create logger with empty
														// string
		app1.setLevel(Level.FINEST);
		app1.addHandler(consoleHandler);

		Logger app2 = Logger.getLogger("com.arjuna");
		app2.setLevel(Level.FINEST);
		app2.addHandler(consoleHandler);
	}
	
	
	public static void  printStatistics(PoolDataSource ds) throws Exception {
		System.out.println("----------------------------------------------------------");
		System.out.println(ds.getConnectionPoolName());
		System.out.println(ds.getStatistics());
		System.out.println("----------------------------------------------------------");
	}
	
	static XADataSource getOracleXA(String user, String password) throws SQLException {
		//Get XADataSource
		//OracleXADataSource xaDS1 = new OracleXADataSource();
		PoolXADataSource xaDS1 = PoolDataSourceFactory.getPoolXADataSource();;
		//set XADataSource with information for connection to happen
		xaDS1.setURL("jdbc:oracle:thin:@localhost:1521/orcl");
		xaDS1.setConnectionFactoryClassName("oracle.jdbc.xa.client.OracleXADataSource");
		xaDS1.setUser(user);
		xaDS1.setPassword(password);
		xaDS1.setInitialPoolSize(2);
		xaDS1.setMaxPoolSize(5);
		xaDS1.setConnectionPoolName(user+"-Arjuna");
		
		
		XAConnection xaCon = xaDS1.getXAConnection();
		
		if (Proxy.isProxyClass(xaCon.getClass())) {
			InvocationHandler ihandle = Proxy.getInvocationHandler(xaCon);
			System.out.println(xaCon + "->Proxy->" + ihandle + " [XAConnection : " + (xaCon instanceof XAConnection)
					+ ", Connection :" + (xaCon instanceof Connection) + "]");
		}
		
		Connection con = xaCon.getConnection();
		ResultSet rs = con.prepareStatement("select CURRENT_DATE from dual").executeQuery();
		while (rs.next()) {
			System.out.println("++++++++++++++++++++++ " + rs.getString(1));
		}
		rs.close();
		con.close();
		
        return xaDS1;
	}
	
	public static void main(String[] args) throws Exception {
		
		//get XAConnection
		XADataSource xaDS1 = getOracleXA("TESTDB1","testdb");
		printStatistics((PoolDataSource) xaDS1);
		XADataSource xaDS2 = getOracleXA("TESTDB2","testdb");
		printStatistics((PoolDataSource) xaDS2);
		
		TransactionManager txMgr = com.arjuna.ats.jta.TransactionManager.transactionManager();
		txMgr.begin();
		
	
		XAConnection xaCon1 = xaDS1.getXAConnection();
		//enlist the connection XAResource
		txMgr.getTransaction().enlistResource(xaCon1.getXAResource());
		
		
		XAConnection xaCon2 = xaDS2.getXAConnection();
		//enlist the connection XAResource
		txMgr.getTransaction().enlistResource(xaCon2.getXAResource());
		
		
		PreparedStatement ps1 = xaCon1.getConnection().prepareStatement("INSERT INTO TEST_TX2 (ID, NAME, AGE) VALUES(?, ?, ?)");
		ps1.setString(1, "1");
		ps1.setString(2, "Ajeeth");
		ps1.setInt(3, 13);
		
		PreparedStatement ps2 = xaCon2.getConnection().prepareStatement("INSERT INTO TEST_TX2 (ID, NAME, AGE) VALUES(?, ?, ?)");
		ps2.setString(1, "1");
		ps2.setString(2, "Ajeeth2");
		ps2.setInt(3, 13);
		
		int count;
		try {
			count = ps1.executeUpdate();
			System.out.println("PS1 -> " + count);
			//doSomeWork();
			count = ps2.executeUpdate();
			System.out.println("PS2 -> " + count);
			txMgr.commit();
		} catch (RuntimeException  | HeuristicMixedException | HeuristicRollbackException e) {
			txMgr.rollback();
			throw e;
		} finally {
			xaCon1.close();
			xaCon2.close();
		}
		
	}
	
   static void doSomeWork() {
	    throw new RuntimeException();
   }
}
