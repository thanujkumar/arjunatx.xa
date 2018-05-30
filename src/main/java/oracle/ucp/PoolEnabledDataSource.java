package oracle.ucp;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

public class PoolEnabledDataSource extends Logging {
	
	static String url = "jdbc:oracle:thin:@localhost:1521/orcl";
	
	static PoolDataSource initPool() throws Exception {
		PoolDataSource pool = PoolDataSourceFactory.getPoolDataSource(); //non XA
		pool.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");
		//Below valid username and password should be set though we are switching to different schemas below
		pool.setUser("TESTDB2");
		pool.setPassword("testdb");
		pool.setURL(url);
		return pool;
	}
	
	public static void main(String[] args) throws Exception {
		PoolDataSource pool = initPool();
        testDB1(pool);	
        printStatistics(pool);
	    testDB2(pool);
	    printStatistics(pool);
	    testWhichSchema(pool);
	    printStatistics(pool);
	    Thread.sleep(10000);
    
	}
	

	
	public static void testDB1(PoolDataSource pool) throws Exception {
		Connection con1 = pool.getConnection("TESTDB1", "testdb");
		ResultSet rs = con1.prepareStatement("select CURRENT_DATE from dual").executeQuery();

		while (rs.next()) {
			System.out.println(rs.getString(1));
		}
		rs.close();
		
		rs = con1.prepareStatement("select ID, NAME, AGE from TEST_TX2").executeQuery();
		while (rs.next()) {
			System.out.println(rs.getString(1) +" - "+ rs.getString(2) + " - " + rs.getInt(3));
		}
	    rs.close();	
		con1.close();
		System.out.println("ConnectionPoolName : "+pool.getConnectionPoolName());
	}

	public static void testDB2(PoolDataSource pool) throws Exception {
		Connection con1 = pool.getConnection("TESTDB2", "testdb");
		ResultSet rs = con1.prepareStatement("select CURRENT_DATE from dual").executeQuery();

		while (rs.next()) {
			System.out.println(rs.getString(1));
		}
		rs.close();
		
		rs = con1.prepareStatement("select ID, NAME, AGE from TEST_TX2").executeQuery();
		while (rs.next()) {
			System.out.println(rs.getString(1) +" - "+ rs.getString(2) + " - " + rs.getInt(3));
		}
	    rs.close();	
		con1.close();
		System.out.println("ConnectionPoolName : "+pool.getConnectionPoolName());
	}
	
	/* This is going to be associated with schema that is used in init */
	public static void testWhichSchema(PoolDataSource pool) throws Exception {
		Connection con1 = pool.getConnection();
		ResultSet rs = con1.prepareStatement("select CURRENT_DATE from dual").executeQuery();

		while (rs.next()) {
			System.out.println(rs.getString(1));
		}
		rs.close();
		
		rs = con1.prepareStatement("select ID, NAME, AGE from TEST_TX2").executeQuery();
		while (rs.next()) {
			System.out.println(rs.getString(1) +" - "+ rs.getString(2) + " - " + rs.getInt(3));
		}
	    rs.close();	
		con1.close();
		System.out.println("ConnectionPoolName : "+pool.getConnectionPoolName());
	}
}
