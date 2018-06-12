package basics;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.sql.XAConnection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import basics.H2DSFactory.H2WrapperDataSouce;

/*
 * This example shows how to recover prepared or heuristically completed
 * transaction branches during failure recovery. It first tries to rollback each
 * branch; if it fails, it tries to tell resource manager to discard knowledge
 * about the transaction.
 */
public class H2SimpleTxMgr2PhaseRecovery {

	static H2WrapperDataSouce getDataSource(String DB_NAME) throws Exception{
		H2WrapperDataSouce ds = H2DSFactory.getDataSource();
		ds.setURL(String.format(H2DSFactory.DB_URL, DB_NAME));
		ds.setPassword(H2DSFactory.DB_PASSWORD);
		ds.setUser(H2DSFactory.DB_USER);
		return ds;
	}
	
	//Test with single datasource
	public static void main(String[] args) throws Exception {
		H2WrapperDataSouce ds = getDataSource(H2DSFactory.DB_1);
		
		XAConnection xaCon = ds.getXAConnection(H2DSFactory.DB_USER, H2DSFactory.DB_PASSWORD);
		XAResource xaRes = xaCon.getXAResource();
		
		Xid[] xids;
		xids = xaRes.recover(XAResource.TMSTARTRSCAN | XAResource.TMENDRSCAN);
		
		for (int i = 0; xids != null && i < xids.length; i++) {
			System.out.println("Recovering : "+ xids[i]);
			try {
				xaRes.rollback(xids[i]);
			} catch (XAException ex) {
				try {
					xaRes.forget(xids[i]);
				} catch (XAException ex1) {
					System.out.println("rollback/forget failed: " + ex1.errorCode);
				}
			}
		}
		System.out.println("===============After Tx==========================");
		H2WrapperDataSouce.verify();
		
	}
}
