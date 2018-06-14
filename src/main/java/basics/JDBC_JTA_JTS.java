package basics;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

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
	javax.transaction.xa.XAResource xaRes; /* should be supported by JDBC Driver */

	// JDBC API (including optional package) 
	javax.sql.XAConnection xaCon;
	javax.sql.XADataSource xaDs;
	javax.sql.DataSource ds;
	javax.sql.PooledConnection pdc;
	javax.sql.ConnectionPoolDataSource cpD;
	// JDBC driver developers need only be concerned with the XAResource interface

	/*
	 * 1. The first step of the distributed transaction process is for the
	 * application to send a request for the transaction to the transaction manager
	 * 
	 * 2. Although the final commit/rollback decision treats the transaction as a
	 * single logical unit, there can be many transaction branches involved
	 * 
	 * 3. A transaction branch is associated with a request to each resource manager
	 * involved in the distributed transaction
	 * 
	 * 4. Requests to three different RDBMSs, therefore, require three transaction
	 * branches. Each transaction branch must be committed or rolled back by the
	 * local resource manager
	 * 
	 * 5. The transaction manager controls the boundaries of the transaction and is
	 * responsible for the final decision as to whether or not the total transaction
	 * should commit or rollback. This decision is made in two phases, called the
	 * Two-Phase Commit Protocol
	 * 
	 * 6. In the first phase, the transaction manager polls all of the resource
	 * managers (RDBMSs) involved in the distributed transaction to see if each one
	 * is ready to commit. If a resource manager cannot commit, it responds
	 * negatively and rolls back its particular part of the transaction so that data
	 * is not altered.
	 * 
	 * 7. In the second phase, the transaction manager determines if any of the
	 * resource managers have responded negatively, and, if so, rolls back the whole
	 * transaction. If there are no negative responses, the translation manager
	 * commits the whole transaction, and returns the results to the application
	 * 
	 */
	
	//Local single resource manager
	PlatformTransactionManager ptxMgr;
	DataSourceTransactionManager dsTxMgr;
}
