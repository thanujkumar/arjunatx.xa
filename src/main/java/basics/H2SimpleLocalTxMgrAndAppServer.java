package basics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import javax.sql.XAConnection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;

import basics.H2DSFactory.H2WrapperDataSouce;

public class H2SimpleLocalTxMgrAndAppServer {

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

			int ret = xaRes.prepare(xid);
			if (ret == XAResource.XA_OK) {
				xaRes.commit(xid, false);
				//xaRes.rollback(xid);
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
