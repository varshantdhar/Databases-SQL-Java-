package simpledb;

import java.io.IOException;

/**
 * The delete operator. Delete reads tuples from its child operator and removes
 * them from the table they belong to.
 */
public class Delete extends Operator {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor specifying the transaction that this delete belongs to as
     * well as the child to read from.
     * 
     * @param t
     *            The transaction this delete runs in
     * @param child
     *            The child operator from which to read tuples for deletion
     */
    
	private TransactionId tid;
	private OpIterator child;
	private TupleDesc td;
	private boolean fetched;
    public Delete(TransactionId t, OpIterator child) {
        // some code goes here

		this.tid = t;
		this.child = child;
		this.td = new TupleDesc(new Type[] { Type.INT_TYPE });
		this.fetched=false;
    }

    public TupleDesc getTupleDesc() {
        // some code goes here
        return this.td;
    }

    public void open() throws DbException, TransactionAbortedException {
        // some code goes here
    	super.open();
    	this.child.open();
    }

    public void close() {
        // some code goes here
    	super.close();
    	child.close();
    }

    public void rewind() throws DbException, TransactionAbortedException {
        // some code goes here
    	child.rewind();
    }

    /**
     * Deletes tuples as they are read from the child operator. Deletes are
     * processed via the buffer pool (which can be accessed via the
     * Database.getBufferPool() method.
     * 
     * @return A 1-field tuple containing the number of deleted records.
     * @see Database#getBufferPool
     * @see BufferPool#deleteTuple
     */
    protected Tuple fetchNext() throws TransactionAbortedException, DbException {
        // some code goes here
    	Tuple t = new Tuple(td);
       	int count = 0;
       	try{
       		if (fetched)
       			return null;
       		fetched = true;
       		while(child.hasNext()) {
       			Tuple tup = child.next();
       			Database.getBufferPool().deleteTuple(tid, tup);
       			count++;
       		}
       	} catch (DbException e) {
       		e.printStackTrace();
       	} catch (IOException e) {
			e.printStackTrace();
		}
       	Field fd = new IntField(count);
       	t.setField(0, fd);
       	return t;
    }

    @Override
    public OpIterator[] getChildren() {
        // some code goes here
    	return new OpIterator[] { this.child };
    }

    @Override
    public void setChildren(OpIterator[] children) {
        // some code goes here
    	this.child = children[0];
    }

}
