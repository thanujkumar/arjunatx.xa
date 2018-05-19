package spf2.arjunatx.local.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.XADataSource;

import oracle.jdbc.xa.client.OracleXADataSource;

public class LocalJdbcTransaction {

	static XADataSource getDS1() throws SQLException {
		// Get XADataSource
		OracleXADataSource xaDS1 = new OracleXADataSource();
		// set XADataSource with information for connection to happen
		xaDS1.setURL("jdbc:oracle:thin:@localhost:1521/orcl");
		xaDS1.setUser("testdb1");
		xaDS1.setPassword("testdb");
		return xaDS1;
	}

	static XADataSource getDS2() throws SQLException {
		// Get XADataSource
		OracleXADataSource xaDS1 = new OracleXADataSource();
		// set XADataSource with information for connection to happen
		xaDS1.setURL("jdbc:oracle:thin:@localhost:1521/orcl");
		xaDS1.setUser("testdb2");
		xaDS1.setPassword("testdb");
		return xaDS1;
	}

	public static void main(String[] args) throws Exception {
		
		XADataSource dsXA1 =getDS1();
        Connection conn1 = dsXA1.getXAConnection().getConnection();
        conn1.setAutoCommit(false);
        
        XADataSource dsXA2 =getDS2();
        Connection conn2 = dsXA2.getXAConnection().getConnection();
        conn2.setAutoCommit(false);
        
        
        
        try {
            PreparedStatement ps1 = conn1.prepareStatement("INSERT INTO TEST_TX1 (ID, NAME, AGE) VALUES(?, ?, ?)");
            ps1.setString(1, new Date().toString());
            ps1.setString(2, "Arjuna");
            ps1.setInt(3, 12);
           
            
           int count =  ps1.executeUpdate();
           System.out.println("PS1 -> "+ count);
           
           
            PreparedStatement ps2 = conn2.prepareStatement("INSERT INTO TEST_TX2 (ID, NAME, AGE) VALUES(?, ?, ?)");
            ps2.setString(1, new Date().toString());
            ps2.setString(2, "Narayana");
            ps2.setInt(3, 12);


            count = ps2.executeUpdate();
            System.out.println("PS2 -> "+ count);

            conn1.commit();
           
            conn2.commit();
        } catch (Exception e) {
            conn1.rollback();
            conn2.rollback();
        } finally {
            conn1.close();
            conn2.close();
        }
        
       
    }
	
}
