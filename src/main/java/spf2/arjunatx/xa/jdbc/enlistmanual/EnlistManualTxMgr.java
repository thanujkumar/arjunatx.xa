package spf2.arjunatx.xa.jdbc.enlistmanual;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.TransactionManager;

import oracle.jdbc.xa.client.OracleXADataSource;

/*
 * http://jbossts.blogspot.in/2017/12/narayana-jdbc-transactional-driver.html
 */
public class EnlistManualTxMgr {
	
	public static void main(String[] args) throws Exception {
		
		TransactionManager txMgr = com.arjuna.ats.jta.TransactionManager.transactionManager();
		txMgr.begin();
		
			//get XAConnection
		XADataSource xaDS1 = getDS1();
		XAConnection xaCon1 = xaDS1.getXAConnection();
		//enlist the connection XAResource
		txMgr.getTransaction().enlistResource(xaCon1.getXAResource());
		
		XADataSource xaDS2 = getDS2();
		XAConnection xaCon2 = xaDS2.getXAConnection();
		//enlist the connection XAResource
		txMgr.getTransaction().enlistResource(xaCon2.getXAResource());
		
		
		PreparedStatement ps1 = xaCon1.getConnection().prepareStatement("INSERT INTO TEST_TX1 (ID, NAME, AGE) VALUES(?, ?, ?)");
		ps1.setString(1, "1");
		ps1.setString(2, "Ajeeth");
		ps1.setInt(3, 13);
		
		PreparedStatement ps2 = xaCon2.getConnection().prepareStatement("INSERT INTO TEST_TX2 (ID, NAME, AGE) VALUES(?, ?, ?)");
		ps2.setString(1, "1");
		ps2.setString(2, "Ajeeth2");
		ps2.setInt(3, 13);
		
		
		try {
			ps1.executeUpdate();
			ps2.executeUpdate();
			txMgr.commit();
		} catch (SecurityException | HeuristicMixedException | HeuristicRollbackException  e) {
			txMgr.rollback();
			throw e;
		} finally {
			xaCon1.close();
			xaCon2.close();
		}
		
		
	}
	
	static XADataSource getDS1() throws SQLException {
		//Get XADataSource
		OracleXADataSource xaDS1 = new OracleXADataSource();
		//set XADataSource with information for connection to happen
		xaDS1.setURL("jdbc:oracle:thin:@localhost:1521/orcl");
		xaDS1.setUser("testdb1");
		xaDS1.setPassword("testdb");
        return xaDS1;
	}

	static XADataSource getDS2() throws SQLException {
		//Get XADataSource
		OracleXADataSource xaDS1 = new OracleXADataSource();
		//set XADataSource with information for connection to happen
		xaDS1.setURL("jdbc:oracle:thin:@localhost:1521/orcl");
		xaDS1.setUser("testdb2");
		xaDS1.setPassword("testdb");
        return xaDS1;
	}
}
