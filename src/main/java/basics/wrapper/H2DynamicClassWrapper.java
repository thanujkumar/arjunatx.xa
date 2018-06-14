package basics.wrapper;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.ServiceLoader;

import javax.sql.XADataSource;

import org.h2.jdbcx.JdbcDataSource;

import com.arjuna.ats.internal.jdbc.DynamicClass;

public class H2DynamicClassWrapper implements DynamicClass {

	@Override
	public XADataSource getDataSource(String dbName) throws SQLException {
		ServiceLoader<java.sql.Driver> loader = ServiceLoader.load(java.sql.Driver.class);
		for (Driver d : loader) {
			System.out.println("Drivers := " + d);
		}

		String url = "jdbc:" + dbName;
		System.out.println(url);

		DataSourceWrapper ds = new DataSourceWrapper(new JdbcDataSource());
		
		ds.setPassword("");
		ds.setUser("");
		ds.setUrl(url);
		return ds;
	}

}
