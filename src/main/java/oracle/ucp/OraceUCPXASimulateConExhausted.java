package oracle.ucp;

import java.sql.Connection;
import java.sql.ResultSet;

import javax.sql.XAConnection;

import oracle.ucp.admin.UniversalConnectionPoolManager;
import oracle.ucp.admin.UniversalConnectionPoolManagerImpl;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import oracle.ucp.jdbc.PoolXADataSource;

public class OraceUCPXASimulateConExhausted extends Logging {

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
 	}


	public static void main(String[] args) throws Exception {
		createPool();
		
		/*
		 * The setInvalid method of the ValidConnection interface indicates that a
		 * connection should be removed from the connection pool when it is closed. The
		 * method is typically used when a connection is no longer usable, such as after
		 * an exception or if the isValid method of the ValidConnection interface
		 * returns false. The method can also be used if an application deems the state
		 * on a connection to be bad
		 */
		
		
		for (int i = 0; i <= 15; i++) {
			try {
				
				XAConnection xacon = pool.getXAConnection();
				
				System.out.println("oracle.ucp.jdbc.ValidConnection.isValid : "	+ ((oracle.ucp.jdbc.ValidConnection) xacon).isValid());

				Connection con = xacon.getConnection();

				ResultSet rs = con.prepareStatement("select CURRENT_DATE from dual").executeQuery();

				while (rs.next()) {
					System.out.println(rs.getString(1));
				}

				System.out.println("Is current connection valid : " + con.isValid(0));
				rs.close();
				con.close();
				System.out.println("Is current connection valid : " + con.isValid(0));

				System.out.println("oracle.ucp.jdbc.ValidConnection.isValid : "	+ ((oracle.ucp.jdbc.ValidConnection) xacon).isValid());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		

		//Thread.sleep(100000);
		ucpm.destroyConnectionPool(pool.getConnectionPoolName());
	}
}
