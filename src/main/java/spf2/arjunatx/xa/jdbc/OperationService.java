package spf2.arjunatx.xa.jdbc;

public class OperationService {

	OperationDAO dao;
	
	public void setOperationDAO(OperationDAO operationDAO) {
		this.dao = operationDAO;
	}
	
	public OperationDAO getOperationDAO() {
		return this.dao;
	}
	
	public int doTxInsert(TEST_TX data) {
		return dao.insertOpr(data);
	}
}
