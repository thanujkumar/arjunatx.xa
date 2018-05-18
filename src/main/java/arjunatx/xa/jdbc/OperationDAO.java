package arjunatx.xa.jdbc;

import java.sql.Types;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class OperationDAO extends JdbcDaoSupport{
	String INSERT_SQL = "INSERT INTO TEST_TX (ID, NAME, AGE) VALUES(?, ?, ?)";
	int[] sqlTypes = {Types.VARCHAR, Types.VARCHAR, Types.INTEGER};
	
	public int insertOpr(TEST_TX object) {
		Object[] params = new Object[] {object.hashCode(),object.getName(), object.getAge() };
		return getJdbcTemplate().update(INSERT_SQL, params, sqlTypes);
	}
	
	public int deleteOpr() {
		return 0;
	}
	
	public Object readOpr() {
		return null;
	}
	
	public Object updateOpr() {
		return null;
	}

}
