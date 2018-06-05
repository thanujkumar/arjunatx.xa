package oracle.ucp;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Properties;

import oracle.ucp.admin.UniversalConnectionPoolManager;
import oracle.ucp.jdbc.LabelableConnection;
import oracle.ucp.jdbc.PoolDataSource;

public class OracleUCPLabelingCallback {

	static String url = "jdbc:oracle:thin:@(DESCRIPTION=(SOURCE_ROUTE=YES) (ADDRESS=(PROTOCOL=TCP)(HOST=localhost)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=orcl)))";
	static String password = "app";
	static String user = "appdata";
	static UniversalConnectionPoolManager ucpm;
	static PoolDataSource pool;

	static void createPool() throws Exception {
		//pool = PoolDataSourceFactory.getPoolDataSource(); // XA
		//pool.setConnectionFactoryClassName("oracle.jdbc.xa.client.OracleXADataSource");
		//pool.setConnectionFactoryClassName("oracle.jdbc.replay.OracleXADataSourceImpl");
		
		
		pool = new oracle.ucp.jdbc.PoolDataSourceImpl();
		//pool.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");
		pool.setConnectionFactoryClassName("oracle.jdbc.replay.OracleDataSourceImpl");
		pool.setURL(url);
		pool.setUser(user);
		pool.setPassword(password);
		pool.setInitialPoolSize(5);
		pool.setMaxPoolSize(10);
		pool.setFastConnectionFailoverEnabled(true);
		pool.registerConnectionLabelingCallback(new UCPConnectionLabellingCallback());

	}
	
	public static void main(String[] args) throws Exception {
		createPool();
		
		Properties requestLabels = new Properties();
		requestLabels.put("SCHEMA", "test");
        
		 //NOTE- pool.getConnection should be based on label request
		Connection con = pool.getConnection(user, password, requestLabels);
		
		ResultSet rs = con.prepareStatement("select CURRENT_DATE from dual").executeQuery();

		while (rs.next()) {
			System.out.println(rs.getString(1));
		}

		rs.close();
		con.close();
		
		con = pool.getConnection();
		con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		((LabelableConnection) con).applyConnectionLabel("NAME", "THANUJ");
		
		rs = con.prepareStatement("select CURRENT_DATE from dual").executeQuery();

		while (rs.next()) {
			System.out.println(rs.getString(1));
		}

		rs.close();
		con.close();
	}
	
	static class UCPConnectionLabellingCallback implements ConnectionLabelingCallback {
	 
	    public int cost(Properties requestedLabels, Properties currentLabels) {
	    	System.out.println("Calculating the cost...");
	        if(requestedLabels.equals(currentLabels)) {
	            System.out.println("Found a connection with the requested label");
	            return 0;
	        }
	        System.out.println("No connection found with requested labels");
	        return Integer.MAX_VALUE;
	    }
	 
	    public boolean configure(Properties requestedLabels, Object connection) {
	        System.out.println("Returning true always for requestedLabels "+ requestedLabels);
	        return true;
	    }
	}
}
