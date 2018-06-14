package basics.wrapper.test.spf2;

public class H2DbService {

    H2DbOperationDAO testdb1DAO;
    H2DbOperationDAO testdb2DAO;
	
	public void setTestDB1DAO(H2DbOperationDAO testdb1) {
		this.testdb1DAO = testdb1;
	}
	
	public void setTestDB2DAO(H2DbOperationDAO testdb2) {
		this.testdb2DAO = testdb2;
	}
	
	public H2DbOperationDAO getTestDB1DAO() {
		return testdb1DAO;
	}
	
	public H2DbOperationDAO getTestDB2DAO() {
		return testdb2DAO;
	}
	
	public int doTxInsert() {
		testdb1DAO.insertOpr(2, "fromTxDB1");
		testdb2DAO.insertOpr(2, "fromTxDB2");
		return 0;
	}
}
