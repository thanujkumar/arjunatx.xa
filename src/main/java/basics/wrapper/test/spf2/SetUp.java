package basics.wrapper.test.spf2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SetUp {

	//Note same database configuration is used in applicationContext-wrapper (driver is transactional driver from Arjuna so that i could wrap my classes)
	
	private static final String DB_DRIVER = "org.h2.Driver";
	private static final String DB_URL = "jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1";
	private static final String DB_1 = "testdb1";
	private static final String DB_2 = "testdb2";
	private static final String DB_USER = "";
	private static final String DB_PASSWORD = "";

	final static String CreateQuery = "CREATE TABLE PERSON(id int primary key, name varchar(255))";
	final static String InsertQuery = "INSERT INTO PERSON (id, name) values(?,?)";
	final static String SelectQuery = "select * from PERSON";


	 static void setUp() throws Exception {
		Class.forName(DB_DRIVER);
		Connection con1 = DriverManager.getConnection(String.format(DB_URL, DB_1), DB_USER, DB_PASSWORD);
		PreparedStatement pstmt1 = con1.prepareStatement(CreateQuery);
		pstmt1.executeUpdate();
		pstmt1.close();

		pstmt1 = con1.prepareStatement(InsertQuery);
		pstmt1.setInt(1, 1);
		pstmt1.setString(2, "TestWrapper-1-db1");
		pstmt1.executeUpdate();
		pstmt1.close();

		con1.close();

		Connection con2 = DriverManager.getConnection(String.format(DB_URL, DB_2), DB_USER, DB_PASSWORD);
		PreparedStatement pstmt2 = con2.prepareStatement(CreateQuery);
		pstmt2.executeUpdate();
		pstmt2.close();

		pstmt2 = con2.prepareStatement(InsertQuery);
		pstmt2.setInt(1, 1);
		pstmt2.setString(2, "TestWrapper-1-db2");
		pstmt2.executeUpdate();
		pstmt2.close();

		con2.close();
	}

	 static void verify() throws Exception {
		Connection con1 = DriverManager.getConnection(String.format(DB_URL, DB_1), DB_USER, DB_PASSWORD);
		PreparedStatement pstmt1 = con1.prepareStatement(SelectQuery);
		ResultSet rs1 = pstmt1.executeQuery();
		while (rs1.next()) {
			System.out.println(DB_1 + " - > " + rs1.getInt(1) +" - "+ rs1.getString(2));
		}
		rs1.close();
		pstmt1.close();
		con1.close();

		Connection con2 = DriverManager.getConnection(String.format(DB_URL, DB_2), DB_USER, DB_PASSWORD);
		PreparedStatement pstmt2 = con2.prepareStatement(SelectQuery);
		ResultSet rs2 = pstmt2.executeQuery();
		while (rs2.next()) {
			System.out.println(DB_2 + " - > " + rs2.getInt(1) + " - "+  rs2.getString(2));
		}
		rs2.close();
		pstmt2.close();
		con2.close();

	}

}
