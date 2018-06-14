package basics.wrapper;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.h2.jdbcx.JdbcXAConnection;

public class XAResourceWrapper implements XAResource {

	XAResource h2XA;
	
	
	public XAResourceWrapper(JdbcXAConnection xaCon) {
		h2XA = xaCon;
	}

	public int getTransactionTimeout() throws XAException {
		System.out.println("=> "+getClass() + " - getTransactionTimeout()");
		return h2XA.getTransactionTimeout();
	}

	public boolean isSameRM(XAResource xares) throws XAException {
		System.out.println("=> "+getClass() + " - isSameRM("+xares+")");
		return h2XA.isSameRM(xares);
	}

	public int prepare(Xid xid) throws XAException {
		System.out.println("=> "+getClass() + " - prepare("+xid+")");
		return h2XA.prepare(xid);
	}

	public void forget(Xid xid) throws XAException {
		System.out.println("=> "+getClass() + " - forget("+xid+")");
		h2XA.forget(xid);
	}

	public void rollback(Xid xid) throws XAException {
		System.out.println("=> "+getClass() + " - rollback("+xid+")");
		h2XA.rollback(xid);
	}

	public void end(Xid xid, int flags) throws XAException {
		System.out.println("=> "+getClass() + " - end("+xid+","+flags+")");
		h2XA.end(xid, flags);
	}

	public void start(Xid xid, int flags) throws XAException {
		System.out.println("=> "+getClass() + " - start("+xid+","+flags+")");
		h2XA.start(xid, flags);
	}

	public void commit(Xid xid, boolean onePhase) throws XAException {
		System.out.println("=> "+getClass() + " - commit("+xid+","+onePhase+")");
		h2XA.commit(xid, onePhase);
	}

	public boolean setTransactionTimeout(int seconds) throws XAException {
		System.out.println("=> "+getClass() + " - setTransactionTimeout("+seconds+")");
		return h2XA.setTransactionTimeout(seconds);
	}

	public Xid[] recover(int flag) throws XAException {
		System.out.println("=> "+getClass() + " - recover("+flag+")");
		return h2XA.recover(flag);
	}

}