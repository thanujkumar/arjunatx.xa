
public class JDBC_JTA_JTS {

	// Application level
	/*
	 * Programmatically control transaction boundaries, starts a global transaction
	 * and associates the transaction with thread
	 */
	javax.transaction.UserTransaction usrTx;
	javax.transaction.Transaction tx; /* Operations to be performed on transaction (not programmatically) */

	// Application Sever
	/*
	 * Defines methods that allow application server to control transaction
	 * boundaries on behalf of the application being managed
	 */
	javax.transaction.TransactionManager txMgr;

	// JTA
	/*
	 * Java mapping of the industry standard XA interface based on the X/Open CAE
	 * Specification
	 */
	javax.transaction.xa.XAResource xaRes; /* should be supported by JDBC Driver*/
	
	
	//JDBC
	javax.sql.XAConnection xaCon;
	javax.sql.XADataSource xaDs;
	javax.sql.DataSource ds;
	javax.sql.PooledConnection pdc;
	javax.sql.ConnectionPoolDataSource cpD;
}
