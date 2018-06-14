package basics.wrapper;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.ConnectionEventListener;
import javax.sql.StatementEventListener;
import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;

import org.h2.jdbcx.JdbcXAConnection;

//TODO  
public class XAConnectionWrapper implements  XAConnection {

	JdbcXAConnection h2XA;
	XAResourceWrapper xaRes;

	public XAConnectionWrapper(JdbcXAConnection xaCon) {
		h2XA = xaCon;
		xaRes = new XAResourceWrapper(xaCon);
	}
	
	public int hashCode() {
		return h2XA.hashCode();
	}

	public XAResource getXAResource() {
		System.out.println("=> "+getClass() + " - getXAResource()");
		return xaRes;
	}

	public void close() throws SQLException {
		h2XA.close();
	}

	public Connection getConnection() throws SQLException {
		System.out.println("=> "+getClass() + " - getConnection() ");
		return new ConnectionWrapper(h2XA.getConnection());
	}

	public int getTraceId() {
		return h2XA.getTraceId();
	}

	public String getTraceObjectName() {
		return h2XA.getTraceObjectName();
	}

	public void addConnectionEventListener(ConnectionEventListener listener) {
		h2XA.addConnectionEventListener(listener);
	}

	public boolean equals(Object obj) {
		return h2XA.equals(obj);
	}

	public void removeConnectionEventListener(ConnectionEventListener listener) {
		h2XA.removeConnectionEventListener(listener);
	}

	public void addStatementEventListener(StatementEventListener listener) {
		h2XA.addStatementEventListener(listener);
	}

	public void removeStatementEventListener(StatementEventListener listener) {
		h2XA.removeStatementEventListener(listener);
	}

	public String toString() {
		return h2XA.toString();
	}
	

}
