package arjunatx.h2.xa.jdbc.enlistmanual;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ServiceLoader;

import javax.naming.InitialContext;
import javax.sql.XADataSource;
import javax.transaction.TransactionManager;

import org.h2.jdbcx.JdbcDataSource;

import com.arjuna.ats.internal.jdbc.DirectRecoverableConnection;
import com.arjuna.ats.internal.jdbc.IndirectRecoverableConnection;
//import com.arjuna.ats.internal.jdbc.drivers.PropertyFileDynamicClass;
import com.arjuna.ats.jdbc.TransactionalDriver;

import jndi.custom.LocalInitialContextFactory;

/*
 * When using Narayana transaction driver, we need XADataSource to be provided to transaction driver.
 * Next we request connection from transaction driver and not directly from XADataSource as transactional driver
 * wraps connection and controls the connection by enlisting to active transaction.
 * 
 * http://jbossts.blogspot.in/2017/12/narayana-jdbc-transactional-driver.html
 * 
 * 3 possibilities on how we provide XADataSource to transactional driver 
 *  - Direct XADataSource
 *  - Indirect way of providing XADataSource (thru JNDI)
 *  - Using dynamic class (this can be used the way we want to create XADataSource)
 * 
 */
public class H2NarayanaTransactionalDriverTxMgr {

	private static final String DB_DRIVER = "org.h2.Driver";
	private static final String DB_URL = "jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1";
	private static final String DB_1 = "testdb1";
	private static final String DB_2 = "testdb2";
	private static final String DB_USER = "";
	private static final String DB_PASSWORD = "";

	final static String CreateQuery = "CREATE TABLE PERSON(id int primary key, name varchar(255))";
	final static String InsertQuery = "INSERT INTO PERSON" + "(id, name) values" + "(?,?)";
	final static String SelectQuery = "select * from PERSON";

	static XADataSource getXADataSource(String dbName) throws SQLException {
		// Get XADataSource
		JdbcDataSource ds = new JdbcDataSource();
		// set XADataSource with information for connection to happen
		ds.setURL(String.format(DB_URL, dbName));
		ds.setUser(DB_USER);
		ds.setPassword(DB_USER);
		return ds;
	}

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
			System.out.println(DB_1 + " - > " + rs1.getInt(1));
			System.out.println(DB_1 + " - > " + rs1.getString(2));
		}
		rs1.close();
		pstmt1.close();
		con1.close();

		Connection con2 = DriverManager.getConnection(String.format(DB_URL, DB_2), DB_USER, DB_PASSWORD);
		PreparedStatement pstmt2 = con2.prepareStatement(SelectQuery);
		ResultSet rs2 = pstmt2.executeQuery();
		while (rs2.next()) {
			System.out.println(DB_2 + " - > " + rs2.getInt(1));
			System.out.println(DB_2 + " - > " + rs2.getString(2));
		}
		rs2.close();
		pstmt2.close();
		con2.close();

	}

	public static void main(String[] args) throws Exception {
		ServiceLoader<java.sql.Driver> loader = ServiceLoader.load(java.sql.Driver.class);
		for (Driver d : loader) {
			System.out.println("Drivers := " + d);
		}

		setUp();
		verify();

		try {

			// Using XADataSource with manual enlisted
			processDriverProvidedXADataSource(() -> {
				try {
					Thread.sleep(100);
					// throw new RuntimeException();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

		// Verify whether commit or rollback based on exception set in processTx
		verify();

		
		try {

			// Using XADataSource with manual enlisted
			processIndirectDriverProvidedXADataSource(() -> {
				try {
					Thread.sleep(100);
					// throw new RuntimeException();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Verify whether commit or rollback based on exception set in processTx
		verify();
		
		try {

			// Using XADataSource with manual enlisted
			processDynamicClassXADataSource(() -> {
				try {
					Thread.sleep(100);
					// throw new RuntimeException();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Verify whether commit or rollback based on exception set in processTx
		verify();
		
		
		try {

			// Using XADataSource with manual enlisted
			processFileDynamicClassXADataSource(() -> {
				try {
					Thread.sleep(100);
					// throw new RuntimeException();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Verify whether commit or rollback based on exception set in processTx
		verify();
				
		// Print Registered Drivers
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			System.out.println("Registered Drivers :=> " + drivers.nextElement());
		}
	}

	/*
	 * Here we are setting XADataSource
	 * @see DirectRecoverableConnection
	 */
	public static void processDriverProvidedXADataSource(Runnable process) throws Exception {
		String narayanaTransactionDriver = TransactionalDriver.arjunaDriver;

		XADataSource xaDS1 = getXADataSource(DB_1);
		Properties prop1 = new Properties();
		prop1.put(TransactionalDriver.XADataSource, xaDS1); //Telling TransctionalDriver where to get Connections
		prop1.put(TransactionalDriver.userName, DB_USER);
		prop1.put(TransactionalDriver.password, DB_PASSWORD);
		prop1.put(TransactionalDriver.poolConnections, true);
		prop1.put(TransactionalDriver.maxConnections, 10);
		Connection conn1 = DriverManager.getConnection(narayanaTransactionDriver, prop1);

		XADataSource xaDS2 = getXADataSource(DB_2);
		Properties prop2 = new Properties();
		prop2.put(TransactionalDriver.XADataSource, xaDS2);
		prop2.put(TransactionalDriver.userName, DB_USER);
		prop2.put(TransactionalDriver.password, DB_PASSWORD);
		// No Pool
		// prop2.put(TransactionalDriver.poolConnections, true);
		// prop2.put(TransactionalDriver.maxConnections, 10);
		Connection conn2 = DriverManager.getConnection(narayanaTransactionDriver, prop2);

		TransactionManager tm = com.arjuna.ats.jta.TransactionManager.transactionManager();
		tm.begin();

		PreparedStatement ps1 = conn1.prepareStatement(InsertQuery);
		ps1.setInt(1, 2);
		ps1.setString(2, "Ajeeth-1TX1");

		PreparedStatement ps2 = conn2.prepareStatement(InsertQuery);
		ps2.setInt(1, 2);
		ps2.setString(2, "Ajeeth-2TX2");

		try {
			ps1.executeUpdate();
			process.run();
			ps2.executeUpdate();
			tm.commit();
		} catch (Exception e) {
			tm.rollback();
			throw e;
		} finally {
			conn2.close();
			conn1.close();
		}
	}

	/*
	 * XADataSource bound to JNDI, which is looked up by TransactionalDriver
	 * @see IndirectRecoverableConnection
	 */
	public static void processIndirectDriverProvidedXADataSource(Runnable process) throws Exception {
		/* Already registered above */
		//DriverManager.registerDriver(new TransactionalDriver());
		
		System.setProperty(InitialContext.INITIAL_CONTEXT_FACTORY, LocalInitialContextFactory.class.getName());
		InitialContext context = new InitialContext();
		
		XADataSource xaDS1 = getXADataSource(DB_1);
		String xaDS1Jndi = TransactionalDriver.arjunaDriver + "ds1"; 
        context.bind("ds1", xaDS1);
		
		Properties prop1 = new Properties();
		prop1.put(TransactionalDriver.userName, DB_USER);
		prop1.put(TransactionalDriver.password, DB_PASSWORD);
		prop1.put(TransactionalDriver.poolConnections, true);
		prop1.put(TransactionalDriver.maxConnections, 10);
		Connection conn1 = DriverManager.getConnection(xaDS1Jndi, prop1);

		XADataSource xaDS2 = getXADataSource(DB_2);
		String xaDS2Jndi = TransactionalDriver.arjunaDriver + "ds2";
        context.bind("ds2", xaDS2);
		Properties prop2 = new Properties();
		prop2.put(TransactionalDriver.userName, DB_USER);
		prop2.put(TransactionalDriver.password, DB_PASSWORD);
		// No Pool
		// prop2.put(TransactionalDriver.poolConnections, true);
		// prop2.put(TransactionalDriver.maxConnections, 10);
		Connection conn2 = DriverManager.getConnection(xaDS2Jndi, prop2);

		TransactionManager tm = com.arjuna.ats.jta.TransactionManager.transactionManager();
		tm.begin();

		PreparedStatement ps1 = conn1.prepareStatement(InsertQuery);
		ps1.setInt(1, 3);
		ps1.setString(2, "Ajeeth-jnd1");

		PreparedStatement ps2 = conn2.prepareStatement(InsertQuery);
		ps2.setInt(1, 3);
		ps2.setString(2, "Ajeeth-jnd2");

		try {
			ps1.executeUpdate();
			process.run();
			ps2.executeUpdate();
			tm.commit();
		} catch (Exception e) {
			tm.rollback();
			throw e;
		} finally {
			conn2.close();
			conn1.close();
		}
	}
	
	
	/*
	 * Direct and controlled by dynamic class implementation 
	 */
	public static void processDynamicClassXADataSource(Runnable process) throws Exception {
		/* Already registered above */
		//DriverManager.registerDriver(new TransactionalDriver());
		
		Properties prop1 = new Properties();
		prop1.put(TransactionalDriver.dynamicClass, ApproachLocalInstantiateDynamicClazz.class.getName());
		//prop1.put(TransactionalDriver.userName, DB_USER);
		//prop1.put(TransactionalDriver.password, DB_PASSWORD);
		prop1.put(TransactionalDriver.poolConnections, true);
		prop1.put(TransactionalDriver.maxConnections, 10);
		Connection conn1 = DriverManager.getConnection(TransactionalDriver.arjunaDriver+DB_1, prop1);

		Properties prop2 = new Properties();
		prop2.put(TransactionalDriver.dynamicClass, ApproachLocalInstantiateDynamicClazz.class.getName());
		//prop2.put(TransactionalDriver.userName, DB_USER);
		//prop2.put(TransactionalDriver.password, DB_PASSWORD);
		// No Pool
		// prop2.put(TransactionalDriver.poolConnections, true);
		// prop2.put(TransactionalDriver.maxConnections, 10);
		Connection conn2 = DriverManager.getConnection(TransactionalDriver.arjunaDriver+DB_2, prop2);

		TransactionManager tm = com.arjuna.ats.jta.TransactionManager.transactionManager();
		tm.begin();

		PreparedStatement ps1 = conn1.prepareStatement(InsertQuery);
		ps1.setInt(1, 4);
		ps1.setString(2, "Ajeeth-dynamic1");

		PreparedStatement ps2 = conn2.prepareStatement(InsertQuery);
		ps2.setInt(1, 4);
		ps2.setString(2, "Ajeeth-dynamic2");

		try {
			ps1.executeUpdate();
			process.run();
			ps2.executeUpdate();
			tm.commit();
		} catch (Exception e) {
			tm.rollback();
			throw e;
		} finally {
			conn2.close();
			conn1.close();
		}
	}


	public static void processFileDynamicClassXADataSource(Runnable process) throws Exception {
		/* Already registered above */
		//DriverManager.registerDriver(new TransactionalDriver());
		String propertyFile = "dynamic-property.properties";
		
		Properties prop1 = new Properties();
		prop1.put(TransactionalDriver.dynamicClass, ApproachPropertiesInstantiateDynamicClazz.class.getName());
		prop1.put(TransactionalDriver.poolConnections, true);
		prop1.put(TransactionalDriver.maxConnections, 10);
		Connection conn1 = DriverManager.getConnection(TransactionalDriver.arjunaDriver+DB_1+":"+propertyFile, prop1);

		Properties prop2 = new Properties();
		prop2.put(TransactionalDriver.dynamicClass, ApproachPropertiesInstantiateDynamicClazz.class.getName());
		prop2.put(TransactionalDriver.poolConnections, true);
		prop2.put(TransactionalDriver.maxConnections, 10);
		Connection conn2 = DriverManager.getConnection(TransactionalDriver.arjunaDriver+DB_2+":"+propertyFile, prop2);

		TransactionManager tm = com.arjuna.ats.jta.TransactionManager.transactionManager();
		tm.begin();

		PreparedStatement ps1 = conn1.prepareStatement(InsertQuery);
		ps1.setInt(1, 5);
		ps1.setString(2, "Ajeeth-dynamicFile1");

		PreparedStatement ps2 = conn2.prepareStatement(InsertQuery);
		ps2.setInt(1, 5);
		ps2.setString(2, "Ajeeth-dynamicFile2");

		try {
			ps1.executeUpdate();
			process.run();
			ps2.executeUpdate();
			tm.commit();
		} catch (Exception e) {
			tm.rollback();
			throw e;
		} finally {
			conn2.close();
			conn1.close();
		}
	}

}
