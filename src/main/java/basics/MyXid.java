package basics;

import javax.transaction.xa.Xid;

/*
 * Before using JTA, you must first implement an Xid class for identifying
 * transactions (this would normally be done by the transaction manager). The
 * Xid contains three elements: formatID, gtrid (global transaction ID), and
 * bqual (branch qualifier ID).
 */
public class MyXid implements Xid {

	protected int formatId;
	protected byte globalTransactionId[];
	protected byte branchQualifier[];

	public MyXid(int fmtId, byte[] branQ, byte[] gTxId) {
		formatId = fmtId;
		branchQualifier = branQ;
		globalTransactionId = gTxId;
	}

	@Override
	public byte[] getBranchQualifier() {
		return branchQualifier;
	}

	@Override
	public int getFormatId() {
		return formatId;
	}

	@Override
	public byte[] getGlobalTransactionId() {
		return globalTransactionId;
	}

}
