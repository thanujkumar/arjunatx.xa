package basics;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;
import javax.sql.XAConnection;
import javax.sql.XADataSource;

import org.h2.jdbcx.JdbcDataSource;

public class H2DSFactory {

	 static final String DB_DRIVER = "org.h2.Driver";
	 static final String DB_URL = "jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1";
	 static final String DB_1 = "testdb1";
	 static final String DB_2 = "testdb2";
	 static final String DB_USER = "";
	 static final String DB_PASSWORD = "";
	 final static String CreateQuery = "CREATE TABLE PERSON(id int primary key, name varchar(255))";
	 final static String InsertQuery = "INSERT INTO PERSON" + "(id, name) values" + "(?,?)";

	public static H2WrapperDataSouce getDataSource() throws Exception {
         return new H2WrapperDataSouce();
	}

	 static class H2WrapperDataSouce implements DataSource, XADataSource {

		final static String SelectQuery = "select * from PERSON";
		static JdbcDataSource ds;
		static volatile boolean isDatabaseSetup = false;

		//two database create before database is setup
		private void setUp() throws Exception {
			Class.forName(DB_DRIVER);
			Connection con1 = DriverManager.getConnection(String.format(DB_URL, DB_1), DB_USER, DB_PASSWORD);
			PreparedStatement pstmt1 = con1.prepareStatement(CreateQuery);
			pstmt1.executeUpdate();
			pstmt1.close();

			pstmt1 = con1.prepareStatement(InsertQuery);
			pstmt1.setInt(1, 1);
			pstmt1.setString(2, "Ajeeth-"+DB_1);
			pstmt1.executeUpdate();
			pstmt1.close();

			con1.close();

			Connection con2 = DriverManager.getConnection(String.format(DB_URL, DB_2), DB_USER, DB_PASSWORD);
			PreparedStatement pstmt2 = con2.prepareStatement(CreateQuery);
			pstmt2.executeUpdate();
			pstmt2.close();

			pstmt2 = con2.prepareStatement(InsertQuery);
			pstmt2.setInt(1, 1);
			pstmt2.setString(2, "Ajeeth-"+DB_2);
			pstmt2.executeUpdate();
			pstmt2.close();

			con2.close();
		}

		public static void verify() throws Exception {
			Connection con1 = DriverManager.getConnection(String.format(DB_URL, DB_1), DB_USER, DB_PASSWORD);
			PreparedStatement pstmt1 = con1.prepareStatement(SelectQuery);
			ResultSet rs1 = pstmt1.executeQuery();
			while (rs1.next()) {
				System.out.println(DB_1 + " - > " + rs1.getInt(1)+" , " + rs1.getString(2));
			}
			rs1.close();
			pstmt1.close();
			con1.close();

			Connection con2 = DriverManager.getConnection(String.format(DB_URL, DB_2), DB_USER, DB_PASSWORD);
			PreparedStatement pstmt2 = con2.prepareStatement(SelectQuery);
			ResultSet rs2 = pstmt2.executeQuery();
			while (rs2.next()) {
				System.out.println(DB_2 + " - > " + rs2.getInt(1) +" , " + rs2.getString(2));
			}
			rs2.close();
			pstmt2.close();
			con2.close();

		}
		
		H2WrapperDataSouce() throws Exception {
			// H2 datasource
			synchronized (H2WrapperDataSouce.class) {
				if (!isDatabaseSetup) {
					ds = new JdbcDataSource();
					setUp();
					verify();
				}

			}
		}
		

		@Override
		public PrintWriter getLogWriter() throws SQLException {
			return ds.getLogWriter();
		}

		@Override
		public void setLogWriter(PrintWriter out) throws SQLException {
			ds.setLogWriter(out);
		}

		@Override
		public void setLoginTimeout(int seconds) throws SQLException {
			ds.setLoginTimeout(seconds);

		}

		@Override
		public int getLoginTimeout() throws SQLException {

			return ds.getLoginTimeout();
		}

		@Override
		public Logger getParentLogger() throws SQLFeatureNotSupportedException {
			return ds.getParentLogger();
		}

		@Override
		public <T> T unwrap(Class<T> iface) throws SQLException {
			return ds.unwrap(iface);
		}

		@Override
		public boolean isWrapperFor(Class<?> iface) throws SQLException {
			return ds.isWrapperFor(iface);
		}

		@Override
		public Connection getConnection() throws SQLException {
			return ds.getConnection();
		}

		@Override
		public Connection getConnection(String username, String password) throws SQLException {
			return ds.getConnection(username, password);
		}

		@Override
		public XAConnection getXAConnection() throws SQLException {
			return ds.getXAConnection();
		}

		@Override
		public XAConnection getXAConnection(String user, String password) throws SQLException {
			return ds.getXAConnection(user, password);
		}
		
		//Internal Methods
		public void setURL(String url) {
			ds.setURL(url);
		}
		
		public void setPassword(String password) {
			ds.setPassword(password);
		}
		
		public void setUser(String user) {
			ds.setUser(user);
		}

	}
}
