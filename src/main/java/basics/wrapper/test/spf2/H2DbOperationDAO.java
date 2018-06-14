package basics.wrapper.test.spf2;

import java.sql.Types;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

/* Look at SetUp class */
public class H2DbOperationDAO extends JdbcDaoSupport{
	
	String INSERT_SQL = "INSERT INTO PERSON (id, name) values(?,?)";
	int[] sqlTypes = {Types.INTEGER,  Types.VARCHAR};

	public int insertOpr(int id, String value) {
		Object[] params = new Object[] {id, value };
		return getJdbcTemplate().update(INSERT_SQL, params, sqlTypes);
	}
}
