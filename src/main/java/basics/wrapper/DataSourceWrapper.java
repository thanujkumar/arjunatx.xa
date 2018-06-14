package basics.wrapper;

import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.PooledConnection;
import javax.sql.XAConnection;
import javax.sql.XADataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.h2.jdbcx.JdbcXAConnection;

public class DataSourceWrapper
		implements XADataSource, DataSource, ConnectionPoolDataSource, Serializable, Referenceable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8129407195881750015L;
	
	JdbcDataSource h2ds;
	
	public  DataSourceWrapper(JdbcDataSource h2DS) {
		this.h2ds = h2DS;
	}

	public int hashCode() {
		return h2ds.hashCode();
	}

	public int getTraceId() {
		return h2ds.getTraceId();
	}

	public String getTraceObjectName() {
		return h2ds.getTraceObjectName();
	}

	public int getLoginTimeout() {
		return h2ds.getLoginTimeout();
	}

	public boolean equals(Object obj) {
		return h2ds.equals(obj);
	}

	public void setLoginTimeout(int timeout) {
		h2ds.setLoginTimeout(timeout);
	}

	public PrintWriter getLogWriter() {
		return h2ds.getLogWriter();
	}

	public void setLogWriter(PrintWriter out) {
		h2ds.setLogWriter(out);
	}

	public Connection getConnection() throws SQLException {
		System.out.println(getClass() + " - getConnection() ");
		return new ConnectionWrapper(h2ds.getConnection());
	}

	public Connection getConnection(String user, String password) throws SQLException {
		System.out.println(getClass() + " - getConnection("+user+","+password+")");
		return new ConnectionWrapper(h2ds.getConnection(user, password));
	}

	public String getURL() {
		return h2ds.getURL();
	}

	public void setURL(String url) {
		h2ds.setURL(url);
	}

	public String getUrl() {
		return h2ds.getUrl();
	}

	public void setUrl(String url) {
		h2ds.setUrl(url);
	}

	public void setPassword(String password) {
		h2ds.setPassword(password);
	}

	public void setPasswordChars(char[] password) {
		h2ds.setPasswordChars(password);
	}

	public String getPassword() {
		return h2ds.getPassword();
	}

	public String getUser() {
		return h2ds.getUser();
	}

	public void setUser(String user) {
		h2ds.setUser(user);
	}

	public String getDescription() {
		return h2ds.getDescription();
	}

	public void setDescription(String description) {
		h2ds.setDescription(description);
	}

	public Reference getReference() {
		return h2ds.getReference();
	}

	public XAConnection getXAConnection() throws SQLException {
		System.out.println("=> "+getClass() + " - getXAConnection() ");
		return new XAConnectionWrapper((JdbcXAConnection) h2ds.getXAConnection());
	}

	public XAConnection getXAConnection(String user, String password) throws SQLException {
		System.out.println("=> "+getClass() + " - getXAConnection("+user+","+password+")");
		return new XAConnectionWrapper((JdbcXAConnection) h2ds.getXAConnection(user, password));
	}

	public PooledConnection getPooledConnection() throws SQLException {
		System.out.println("=> "+getClass() + " - getPooledConnection");
		return new PooledConnectionWrapper((XAConnectionWrapper) getXAConnection());
	}

	public PooledConnection getPooledConnection(String user, String password) throws SQLException {
		System.out.println("=> "+getClass() + " - getPooledConnection("+user+","+password+")");
		return new PooledConnectionWrapper((XAConnectionWrapper) getPooledConnection(user, password));
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return h2ds.unwrap(iface);
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return h2ds.isWrapperFor(iface);
	}

	public Logger getParentLogger() {
		return h2ds.getParentLogger();
	}

	public String toString() {
		return h2ds.toString();
	}

}
