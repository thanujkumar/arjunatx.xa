package oracle.ucp;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import oracle.ucp.admin.UniversalConnectionPoolManagerImpl;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import oracle.ucp.jdbc.oracle.OracleJDBCConnectionPoolStatistics;

/*
 * �  Only the JDBC Thin driver supports the Shared Pool feature, and not the
 * JDBC OCI driver.
 * 
 * � For using this feature, you must use an XML configuration file.
 * 
 * Following are the two scenarios in which this feature can be implemented:
 *  � Single Multitenant Data Source Using Shared Pool
 *  � One Data Source per Tenant Using Shared Pool
 *  
 *  With this configuration, multitenant applications have separate data sources per tenant 
 *  and a common Shared Pool for connections
 */
/**
 * 
 * @see OracleUCPSingleMultitenantSharedPool - for instructions
 *
 */
public class OracleUCPDataSourcePerTenantSharedPool extends Logging {

  //https://blogs.oracle.com/dev2dev/ucp-multi-tenant-shared-pool-configuration

  /* This require XML configuration */
	static {
		URL url = ClassLoader.getSystemClassLoader().getResource("DS_Per_PDB_SharePool.xml");
		String path = url.getProtocol()+":"+url.getPath();
		System.out.println(path);
		System.setProperty("oracle.ucp.jdbc.xmlConfigFile", path);
	}
	
	public static void main(String[] args) throws Exception {
		
	      PoolDataSource pds1 = PoolDataSourceFactory.getPoolDataSource("pds1");
	      Connection pds1Conn = pds1.getConnection();
	      // Run a SQL query to test the connection
	      testConnection(pds1Conn);
	      pds1Conn.close();
	      
	      
	      // Get the datasource instance, named as "pds2" in xml config file
	      PoolDataSource pds2 = PoolDataSourceFactory.getPoolDataSource("pds2");
	      Connection pds2Conn = pds2.getConnection();
	      testConnection(pds2Conn);
	      pds2Conn.close();
	      
	      int COUNT1 = 12;
	      Connection conn1[] = new Connection[COUNT1];
	      // Borrow 5 connections of pdb1 service using datasource pds1
	      for (int i = 0; i < COUNT1; i++) {
	        conn1[i] = pds1.getConnection();
	      }
	      
	      String pdb1_table = "SYSPDB1.TABLE_PDB1";
	      // Return the connections to pool
	      for (int i = 0; i < COUNT1; i++) {
	        if (conn1[i] != null) {
	        	new Thread(new ResultSetThread(conn1[i], pdb1_table)).start();
	        }
	      }
	      
	      int COUNT2 = 20;
	      Connection conn2[] = new Connection[COUNT2];
	    		  
	      // Borrow 5 connections of pdb2 service using datasource pds2
	      for (int i = 0; i < COUNT2; i++) {
	        conn2[i] = pds2.getConnection();
	      }
	      
	      
	      String pdb2_table = "SYSPDB2.TABLE_PDB2";
	      
	      // Return the connections to pool
	      for (int i = 0; i < COUNT2; i++) {
	        if (conn2[i] != null) {
	        	new Thread(new ResultSetThread(conn2[i], pdb2_table)).start();
	        }
	      }
	      // Print UCP pool statistics for pool1
	      printPoolStatistics("pool1"); 
	}
	
	static class ResultSetThread implements Runnable {

		final Connection conn;
		final String tab;
		
		ResultSetThread(Connection con, String tableName) {
			this.conn = con;
			this.tab = tableName;
		}
		
		@Override
		public void run() {
			ResultSet rs;
			try {
				rs = conn.prepareStatement("select NAME, AGE from "+tab).executeQuery();
		    	while (rs.next()) {
					System.out.println(rs.getString(1) + "-" + rs.getInt(2));
				}
		        rs.close();

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					conn.close();
				} catch (SQLException e) {}
			}
		}
		
	}
	
	static void printPoolStatistics(String poolName) throws Exception{
	      
	      UniversalConnectionPool pool = UniversalConnectionPoolManagerImpl.getUniversalConnectionPoolManager().getConnectionPool(poolName);
	      
	      OracleJDBCConnectionPoolStatistics stats = (OracleJDBCConnectionPoolStatistics) pool.getStatistics();
	      
	      System.out.println("Pool : " + poolName + " --> Connection # Repurpose Count = " + stats.getConnectionRepurposeCount());
	      System.out.println("Available Connection Count = " + stats.getAvailableConnectionsCount());
	      System.out.println("Borrowed Connection Count = "  + stats.getBorrowedConnectionsCount());
	      System.out.println(pool.getStatistics());

	}
	
	static void testConnection(Connection conn) throws SQLException {
	    Statement stmt = null;
	    ResultSet rs = null;
	    try {
	      stmt = conn.createStatement();
	      
	      String query = "select sys_context('userenv', 'instance_name')," + "sys_context('userenv', 'server_host'),"
	          + "sys_context('userenv', 'service_name')," + "sys_context('userenv', 'db_unique_name')" + ",user" + " from dual";
	      
	      rs = stmt.executeQuery(query);
	      
	      if (rs.next()) {
	        String serviceName = rs.getString(3);
	        String dbName = rs.getString(4);
	        String userName = rs.getString(5);
	        System.out.println("Connection Db name from sys context=" + dbName);
	        System.out.println("Connection Svc name from sys context="+ serviceName);
	        System.out.println("Connection user Name : " + userName);
	      }
	    } catch (SQLException sqlexc) {
	      throw sqlexc;
	    } finally {
	      if (rs != null)
	        rs.close();
	      if (stmt != null)
	        stmt.close();
	    }
	  }
}
