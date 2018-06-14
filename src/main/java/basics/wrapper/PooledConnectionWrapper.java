package basics.wrapper;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;
import javax.sql.StatementEventListener;

public class PooledConnectionWrapper implements PooledConnection {

	XAConnectionWrapper xaConW;
	
	public PooledConnectionWrapper(XAConnectionWrapper xaCon) {
		xaConW = xaCon;
	}
	
	@Override
	public Connection getConnection() throws SQLException {
		return new ConnectionWrapper(xaConW.getConnection());
	}

	@Override
	public void close() throws SQLException {
		xaConW.close();
	}

	@Override
	public void addConnectionEventListener(ConnectionEventListener listener) {
		xaConW.addConnectionEventListener(listener);
	}

	@Override
	public void removeConnectionEventListener(ConnectionEventListener listener) {
		xaConW.removeConnectionEventListener(listener);
	}

	@Override
	public void addStatementEventListener(StatementEventListener listener) {
		xaConW.addStatementEventListener(listener);
	}

	@Override
	public void removeStatementEventListener(StatementEventListener listener) {
		xaConW.removeStatementEventListener(listener);
	}

}
