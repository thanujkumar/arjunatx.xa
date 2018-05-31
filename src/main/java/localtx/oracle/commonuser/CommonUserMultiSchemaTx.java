package localtx.oracle.commonuser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;
import javax.sql.XADataSource;

import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

/*
 * --Login as sys (SYSDBA)
 * --Common user to connect to TESTDB1 and TESTDB2
 * create user commonuser identified by common;
 * 
 * grant connect, resource to commonuser;
 * 
 * grant select, update, insert, delete on TESTDB1.TEST_TX2 to commonuser;
 * grant select, update, insert, delete on TESTDB2.TEST_TX2 to commonuser;
 * 
 */
public class CommonUserMultiSchemaTx {
	
	static DataSource getCommonDS() throws SQLException {
		//Get XADataSource
		//OracleXADataSource xaDS1 = new OracleXADataSource();
		PoolDataSource nonXaDS = PoolDataSourceFactory.getPoolDataSource();
		//set XADataSource with information for connection to happen
		nonXaDS.setURL("jdbc:oracle:thin:@localhost:1521/orcl");
		nonXaDS.setConnectionFactoryClassName("oracle.jdbc.xa.client.OracleXADataSource");
		nonXaDS.setUser("commonuser");
		nonXaDS.setPassword("common");
		nonXaDS.setInitialPoolSize(2);
		nonXaDS.setMaxPoolSize(5);
		nonXaDS.setConnectionPoolName("CommonUser-MultiSchema");
		return nonXaDS;
	}

	public static void  printStatistics(PoolDataSource ds) throws Exception {
		
		System.out.println("----------------------------------------------------------");
		System.out.println(ds.getStatistics());
		System.out.println("----------------------------------------------------------");
	}
	
	
	public static void main(String[] args)  throws Exception {
		DataSource ds =getCommonDS();
        Connection conn1 =ds.getConnection();
        conn1.setAutoCommit(false);
        
        printStatistics((PoolDataSource) ds);
        
        try {
            PreparedStatement ps1 = conn1.prepareStatement("INSERT INTO TESTDB1.TEST_TX2 (ID, NAME, AGE) VALUES(?, ?, ?)");
            ps1.setString(1, new Date().toString());
            ps1.setString(2, "Arjuna");
            ps1.setInt(3, 12);
           
            
           int count =  ps1.executeUpdate();
           System.out.println("PS1 -> "+ count);
           
           
            PreparedStatement ps2 = conn1.prepareStatement("INSERT INTO TESTDB2.TEST_TX2 (ID, NAME, AGE) VALUES(?, ?, ?)");
            ps2.setString(1, new Date().toString());
            ps2.setString(2, "Narayana");
            ps2.setInt(3, 12);


            count = ps2.executeUpdate();
            System.out.println("PS2 -> "+ count);

            throw new RuntimeException();
            
            //conn1.commit();
           
        } catch (Exception e) {
        	e.printStackTrace();
            conn1.rollback();
        } finally {
            conn1.close();
        }
        
        printStatistics((PoolDataSource) ds);
       
    }
	
}
