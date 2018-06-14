package basics.wrapper;

import java.io.InputStream;
import java.io.Reader;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.h2.engine.SessionInterface;
import org.h2.jdbc.JdbcConnection;
import org.h2.value.Value;

public class ConnectionWrapper implements Connection {

	JdbcConnection jdbcCon;
	
	public ConnectionWrapper(Connection con) {
		jdbcCon = (JdbcConnection) con;
	}

	public int hashCode() {
		return jdbcCon.hashCode();
	}

	public int getTraceId() {
		return jdbcCon.getTraceId();
	}

	public String getTraceObjectName() {
		return jdbcCon.getTraceObjectName();
	}

	public boolean equals(Object obj) {
		return jdbcCon.equals(obj);
	}

	public Statement createStatement() throws SQLException {
		return jdbcCon.createStatement();
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		return jdbcCon.createStatement(resultSetType, resultSetConcurrency);
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return jdbcCon.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return jdbcCon.prepareStatement(sql);
	}

	public DatabaseMetaData getMetaData() throws SQLException {
		return jdbcCon.getMetaData();
	}

	public SessionInterface getSession() {
		return jdbcCon.getSession();
	}

	public void close() throws SQLException {
		jdbcCon.close();
	}

	public void setAutoCommit(boolean autoCommit) throws SQLException {
		System.out.println("=> "+getClass() + " - setAutoCommit("+autoCommit+")");
		jdbcCon.setAutoCommit(autoCommit);
	}

	public boolean getAutoCommit() throws SQLException {
		return jdbcCon.getAutoCommit();
	}

	public void commit() throws SQLException {
		System.out.println("=> "+getClass() + " - commit()");
		jdbcCon.commit();
	}

	public void rollback() throws SQLException {
		System.out.println("=> "+getClass() + " - rollback()");
		jdbcCon.rollback();
	}

	public boolean isClosed() throws SQLException {
		return jdbcCon.isClosed();
	}

	public String nativeSQL(String sql) throws SQLException {
		return jdbcCon.nativeSQL(sql);
	}

	public void setReadOnly(boolean readOnly) throws SQLException {
		jdbcCon.setReadOnly(readOnly);
	}

	public boolean isReadOnly() throws SQLException {
		return jdbcCon.isReadOnly();
	}

	public void setCatalog(String catalog) throws SQLException {
		jdbcCon.setCatalog(catalog);
	}

	public String getCatalog() throws SQLException {
		return jdbcCon.getCatalog();
	}

	public SQLWarning getWarnings() throws SQLException {
		return jdbcCon.getWarnings();
	}

	public void clearWarnings() throws SQLException {
		jdbcCon.clearWarnings();
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
			throws SQLException {
		return jdbcCon.prepareStatement(sql, resultSetType, resultSetConcurrency);
	}

	public void setTransactionIsolation(int level) throws SQLException {
		jdbcCon.setTransactionIsolation(level);
	}

	public void setQueryTimeout(int seconds) throws SQLException {
		jdbcCon.setQueryTimeout(seconds);
	}

	public int getTransactionIsolation() throws SQLException {
		return jdbcCon.getTransactionIsolation();
	}

	public void setHoldability(int holdability) throws SQLException {
		jdbcCon.setHoldability(holdability);
	}

	public int getHoldability() throws SQLException {
		return jdbcCon.getHoldability();
	}

	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return jdbcCon.getTypeMap();
	}

	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		jdbcCon.setTypeMap(map);
	}

	public CallableStatement prepareCall(String sql) throws SQLException {
		return jdbcCon.prepareCall(sql);
	}

	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return jdbcCon.prepareCall(sql, resultSetType, resultSetConcurrency);
	}

	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		return jdbcCon.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	public Savepoint setSavepoint() throws SQLException {
		return jdbcCon.setSavepoint();
	}

	public Savepoint setSavepoint(String name) throws SQLException {
		return jdbcCon.setSavepoint(name);
	}

	public void rollback(Savepoint savepoint) throws SQLException {
		jdbcCon.rollback(savepoint);
	}

	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		jdbcCon.releaseSavepoint(savepoint);
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		return jdbcCon.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		return jdbcCon.prepareStatement(sql, autoGeneratedKeys);
	}

	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		return jdbcCon.prepareStatement(sql, columnIndexes);
	}

	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		return jdbcCon.prepareStatement(sql, columnNames);
	}

	public int getPowerOffCount() {
		return jdbcCon.getPowerOffCount();
	}

	public void setPowerOffCount(int count) {
		jdbcCon.setPowerOffCount(count);
	}

	public void setExecutingStatement(Statement stat) {
		jdbcCon.setExecutingStatement(stat);
	}

	public Clob createClob() throws SQLException {
		return jdbcCon.createClob();
	}

	public Blob createBlob() throws SQLException {
		return jdbcCon.createBlob();
	}

	public NClob createNClob() throws SQLException {
		return jdbcCon.createNClob();
	}

	public SQLXML createSQLXML() throws SQLException {
		return jdbcCon.createSQLXML();
	}

	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		return jdbcCon.createArrayOf(typeName, elements);
	}

	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		return jdbcCon.createStruct(typeName, attributes);
	}

	public boolean isValid(int timeout) {
		return jdbcCon.isValid(timeout);
	}

	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		jdbcCon.setClientInfo(name, value);
	}

	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		jdbcCon.setClientInfo(properties);
	}

	public Properties getClientInfo() throws SQLException {
		return jdbcCon.getClientInfo();
	}

	public String getClientInfo(String name) throws SQLException {
		return jdbcCon.getClientInfo(name);
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return jdbcCon.unwrap(iface);
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return jdbcCon.isWrapperFor(iface);
	}

	public Value createClob(Reader x, long length) {
		return jdbcCon.createClob(x, length);
	}

	public Value createBlob(InputStream x, long length) {
		return jdbcCon.createBlob(x, length);
	}

	public void setSchema(String schema) throws SQLException {
		jdbcCon.setSchema(schema);
	}

	public String getSchema() throws SQLException {
		return jdbcCon.getSchema();
	}

	public void abort(Executor executor) {
		jdbcCon.abort(executor);
	}

	public void setNetworkTimeout(Executor executor, int milliseconds) {
		jdbcCon.setNetworkTimeout(executor, milliseconds);
	}

	public int getNetworkTimeout() {
		return jdbcCon.getNetworkTimeout();
	}

	public String toString() {
		return jdbcCon.toString();
	}

	public void setTraceLevel(int level) {
		jdbcCon.setTraceLevel(level);
	}
	
}
