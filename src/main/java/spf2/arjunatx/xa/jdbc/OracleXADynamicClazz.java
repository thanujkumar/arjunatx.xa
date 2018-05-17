package spf2.arjunatx.xa.jdbc;

import java.sql.SQLException;

import javax.sql.XADataSource;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.arjuna.ats.internal.jdbc.DynamicClass;
import com.arjuna.ats.jdbc.TransactionalDriver;

import oracle.ucp.jdbc.PoolDataSourceFactory;
import oracle.ucp.jdbc.PoolXADataSource;

public class OracleXADynamicClazz implements DynamicClass {
	
	DriverManagerDataSource a;
	 private static final String driverName = "oracle:";
	 private static final String semicolon = ";";
	    

	public XADataSource getDataSource(String dbName) throws SQLException {
		return getDataSource(dbName, true);
	}

	public synchronized XADataSource getDataSource(String dbName, boolean create) throws SQLException {
		try {
			// MysqlXADataSource xads = new MysqlXADataSource();
			PoolXADataSource dataSource = PoolDataSourceFactory.getPoolXADataSource();
			//OracleXADataSource dataSource = new OracleXADataSource();
			int index1 = dbName.indexOf(OracleXADynamicClazz.driverName);

			if (index1 == -1) {
				throw new SQLException("Oralce.getDataSource - invalid database");
			} else {
				/*
				 * Strip off any spurious parameters.
				 */

				int index2 = dbName.indexOf(OracleXADynamicClazz.semicolon);
				String theDbName = null;

				if (index2 == -1) {
					theDbName = dbName.substring(index1 + OracleXADynamicClazz.driverName.length());
				} else {
					theDbName = dbName.substring(index1 + OracleXADynamicClazz.driverName.length(), index2);
				}

				System.out.println("URL->" + TransactionalDriver.jdbc + OracleXADynamicClazz.driverName + theDbName);

				dataSource.setURL(TransactionalDriver.jdbc + OracleXADynamicClazz.driverName + theDbName);
                dataSource.setConnectionFactoryClassName("oracle.jdbc.xa.client.OracleXADataSource");

//				dataSource.setLoginTimeout(10);
//                dataSource.setMinPoolSize(2);
//                dataSource.setMaxPoolSize(5);
//                dataSource.setInitialPoolSize(0);
//                dataSource.setConnectionPoolName("TEST-UCP-Arjuna");
				return dataSource;
			}
		} catch (SQLException e1) {
			throw e1;
		} catch (Exception e2) {
			throw new SQLException(
					"Oracle ->" + e2);
		}
	}

	public void shutdownDataSource(XADataSource xa) throws SQLException {
		// TODO Auto-generated method stub
		
	}

}
