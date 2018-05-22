package arjunatx.h2.xa.jdbc.enlistmanual;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.XADataSource;

import org.h2.jdbcx.JdbcDataSource;

import com.arjuna.ats.internal.jdbc.DynamicClass;

public class ApproachPropertiesInstantiateDynamicClazz implements DynamicClass{

	/* 
	 * Convention used is jdbc:arjuna:<dbkey>:propertyFile
	 */
	
	@Override
	public XADataSource getDataSource(String propertyFile) throws SQLException {
		//we get string after jdbc:arjuna which in this case is dbkey and propertyFile
		String values[] = propertyFile.split(":");
		String dbName = values[0];
		String file = values[1];
		JdbcDataSource xaDS = null;
		
		Properties prop = new Properties();
		try {
			prop.load(this.getClass().getClassLoader().getResourceAsStream(file));
			xaDS = new JdbcDataSource();
			xaDS.setURL(prop.getProperty(dbName+".url"));
			xaDS.setUser(prop.getProperty(dbName+".user"));
			xaDS.setUser(prop.getProperty(dbName+".password"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return xaDS;
	}

}
