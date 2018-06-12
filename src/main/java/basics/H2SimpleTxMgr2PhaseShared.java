package basics;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.sql.XAConnection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;

import basics.H2DSFactory.H2WrapperDataSouce;

/*
 * This example illustrates how one XA resource can be shared among different
 * transactions. Two transaction branches are created, but they do not belong to
 * the same distributed transaction. JTA allows the XA resource to do a
 * two-phase commit on the first branch even though the resource is still
 * associated with the second branch.
 */

public class H2SimpleTxMgr2PhaseShared {

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
		
		Connection con = xaCon.getConnection();
		PreparedStatement pstmt = null;
		
		MyXid xid1 = new MyXid(100, new byte[]{0x01}, new byte[]{0x02});
		MyXid xid2 = new MyXid(100, new byte[]{0x11}, new byte[]{0x22});
		
		try {
			xaRes.start(xid1, XAResource.TMNOFLAGS);
			
            pstmt = con.prepareStatement(H2DSFactory.InsertQuery);
			pstmt.setInt(1, 2);
			pstmt.setString(2, "XATest");
			pstmt.executeUpdate(); 
			xaRes.end(xid1, XAResource.TMSUCCESS);//This example uses the two-phase commit protocol to commit one transaction branch
			xaRes.start(xid2, XAResource.TMNOFLAGS); //Moving this stmt after below commit for xid1 will work

			// Should allow XA resource to do two-phase commit on
			// transaction 1 while associated to transaction 2
			int ret = xaRes.prepare(xid1);
			if (ret == XAResource.XA_OK) {
				xaRes.commit(xid1, false);
			}
		

			con.createStatement().executeUpdate("INSERT INTO PERSON(id, name) values(3,'Shared')");
			xaRes.end(xid2, XAResource.TMSUCCESS);
			ret = xaRes.prepare(xid2);
			if (ret == XAResource.XA_OK) {
				xaRes.commit(xid2, false);
				//xaRes.rollback(xid2);
			}
		} catch (XAException xa) {
			xa.printStackTrace();
		} finally {
			if (pstmt != null)
				pstmt.close();
			if (con != null)
				con.close();
		}
		
		xaCon.close();
		System.out.println("===============After Tx==========================");
		H2WrapperDataSouce.verify();
		
	}
}
