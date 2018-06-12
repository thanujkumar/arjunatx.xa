package basics;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.sql.XAConnection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;

import basics.H2DSFactory.H2WrapperDataSouce;

public class H2SimpleTxMgr2PhaseResume {

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
		
		MyXid xid = new MyXid(100, new byte[]{0x01}, new byte[]{0x02});
		
		try {
			xaRes.start(xid, XAResource.TMNOFLAGS);
			
            pstmt = con.prepareStatement(H2DSFactory.InsertQuery);
			pstmt.setInt(1, 2);
			pstmt.setString(2, "XATest");
			pstmt.executeUpdate(); 
			xaRes.end(xid, XAResource.TMSUSPEND);//suspend the tx
			
			// This update is done outside of transaction scope, so it
			// is not affected by the XA rollback.
			int x = con.createStatement().executeUpdate("INSERT INTO PERSON(id, name) values(3,'Suspend')");
			System.out.println("Update : "+ x +" after tx suspend");
			
			//Resume now
			xaRes.start(xid, XAResource.TMRESUME);
			//Also i can do another transaction
			con.createStatement().executeUpdate("INSERT INTO PERSON(id, name) values(4,'Another Suspend')");
			xaRes.end(xid, XAResource.TMSUCCESS);
			int ret = xaRes.prepare(xid);
			
			if (ret == XAResource.XA_OK) {
				//xaRes.commit(xid, false);
				xaRes.rollback(xid); //rollback should not insert data from line 38,39 and 51, only insert 45
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
