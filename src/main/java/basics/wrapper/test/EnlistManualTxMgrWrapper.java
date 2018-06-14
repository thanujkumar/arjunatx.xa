package basics.wrapper.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.TransactionManager;

import org.h2.jdbcx.JdbcDataSource;

import basics.wrapper.DataSourceWrapper;

public class EnlistManualTxMgrWrapper {
	
	 private static final String DB_DRIVER = "org.h2.Driver";
	 private static final String DB_URL = "jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1";
	 private static final String DB_1 = "testdb1";
	 private static final String DB_2 = "testdb2";
	 private static final String DB_USER = "";
	 private static final String DB_PASSWORD = "";
	
	 final static String CreateQuery = "CREATE TABLE PERSON(id int primary key, name varchar(255))";
	 final static String InsertQuery = "INSERT INTO PERSON" + "(id, name) values" + "(?,?)";
	 final static String SelectQuery = "select * from PERSON";
	 
	 private static void setUp() throws Exception {
			Class.forName(DB_DRIVER);
			Connection con1 = DriverManager.getConnection(String.format(DB_URL, DB_1), DB_USER, DB_PASSWORD);
			PreparedStatement pstmt1 = con1.prepareStatement(CreateQuery);
			pstmt1.executeUpdate();
			pstmt1.close();
			
			pstmt1 = con1.prepareStatement(InsertQuery);
			pstmt1.setInt(1, 1);
			pstmt1.setString(2, "Ajeeth-1");
			pstmt1.executeUpdate();
			pstmt1.close();
			
			con1.close();
			
			Connection con2 = DriverManager.getConnection(String.format(DB_URL, DB_2), DB_USER, DB_PASSWORD);
			PreparedStatement pstmt2 = con2.prepareStatement(CreateQuery);
			pstmt2.executeUpdate();
			pstmt2.close();
			
			pstmt2 = con2.prepareStatement(InsertQuery);
			pstmt2.setInt(1, 1);
			pstmt2.setString(2, "Ajeeth-1");
			pstmt2.executeUpdate();
			pstmt2.close();
			
			con2.close();
		}
		
		private static void verify() throws Exception {
			Connection con1 = DriverManager.getConnection(String.format(DB_URL, DB_1), DB_USER, DB_PASSWORD);
			PreparedStatement pstmt1 = con1.prepareStatement(SelectQuery);
			ResultSet rs1 = pstmt1.executeQuery();
			while (rs1.next()) {
				System.out.println(DB_1 + " - > "+ rs1.getInt(1) +" - " + rs1.getString(2));
				}
			rs1.close();
			pstmt1.close();
			con1.close();
			
			Connection con2 = DriverManager.getConnection(String.format(DB_URL, DB_2), DB_USER, DB_PASSWORD);
			PreparedStatement pstmt2 = con2.prepareStatement(SelectQuery);
			ResultSet rs2 = pstmt2.executeQuery();
			while (rs2.next()) {
				System.out.println(DB_2 + " - > "+ rs2.getInt(1) + " - "+ rs2.getString(2));
			}
			rs2.close();
			pstmt2.close();
			con2.close();
			
		}
		
		static XADataSource getXADataSource(String dbName) throws SQLException {
			//Get XADataSource
			DataSourceWrapper ds = new DataSourceWrapper(new JdbcDataSource());
			//set XADataSource with information for connection to happen
			ds.setURL(String.format(DB_URL, dbName));
			ds.setUser(DB_USER);
			ds.setPassword(DB_USER);
	        return ds;
		}

		
		public static void main(String[] args) throws Exception {
			setUp();
			verify();
			
			try {

				// Using XADataSource with manual enlisted
				processTx(() -> {
					try {
						Thread.sleep(100);
						//throw new RuntimeException();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				});

			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//Verify whether commit or rollback based on exception set in processTx
			verify();
			
		}
		
		public static void processTx(Runnable r)  throws Exception {
			
			TransactionManager txMgr = com.arjuna.ats.jta.TransactionManager.transactionManager();
			txMgr.begin();
			
				//get XAConnection
			XADataSource xaDS1 = getXADataSource(DB_1);
			XAConnection xaCon1 = xaDS1.getXAConnection();
			//enlist the connection XAResource
			txMgr.getTransaction().enlistResource(xaCon1.getXAResource());
			
			XADataSource xaDS2 = getXADataSource(DB_2);
			XAConnection xaCon2 = xaDS2.getXAConnection();
			//enlist the connection XAResource
			txMgr.getTransaction().enlistResource(xaCon2.getXAResource());
			
			
			PreparedStatement ps1 = xaCon1.getConnection().prepareStatement(InsertQuery);
			ps1.setInt(1, 2);
			ps1.setString(2, "Ajeeth-TX1");
			
			
			PreparedStatement ps2 = xaCon2.getConnection().prepareStatement(InsertQuery);
			ps2.setInt(1, 2);
			ps2.setString(2, "Ajeeth-TX2");
			
			try {
				ps1.executeUpdate();
				r.run();
				ps2.executeUpdate();
				txMgr.commit();
			} catch (RuntimeException | HeuristicMixedException | HeuristicRollbackException  e) {
				txMgr.rollback();
				throw e;
			} finally {
				xaCon1.close();
				xaCon2.close();
			}

		}
}
