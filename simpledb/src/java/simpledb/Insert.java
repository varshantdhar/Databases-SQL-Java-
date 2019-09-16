package simpledb;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * Inserts tuples read from the child operator into the tableId specified in the
 * constructor
 */
public class Insert extends Operator {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 *
	 * @param t       The transaction running the insert.
	 * @param child   The child operator from which to read tuples to be inserted.
	 * @param tableId The table in which to insert tuples.
	 * @throws DbException if TupleDesc of child differs from table into which we
	 *                     are to insert.
	 */
	private boolean inserted = false;
	private TransactionId t;
	private OpIterator child;
	private int tableId;
	private TupleDesc td;

	public Insert(TransactionId t, OpIterator child, int tableId) throws DbException {
		// some code goes here
		if (!(Database.getCatalog().getTupleDesc(tableId).equals(child.getTupleDesc()))) {
			throw new DbException("Table tuple desc does not match child tuple desc");
		}

		this.t = t;
		this.child = child;
		this.tableId = tableId;
		this.td = new TupleDesc(new Type[] { Type.INT_TYPE });
	}

	public TupleDesc getTupleDesc() {
		// some code goes here
		return this.td;

	}

	public void open() throws DbException, TransactionAbortedException {
		// some code goes here
		super.open();
	}

	public void close() {
		// some code goes here
		super.close();
	}

	public void rewind() throws DbException, TransactionAbortedException {
		// some code goes here
	
	}

	/**
	 * Inserts tuples read from child into the tableId specified by the constructor.
	 * It returns a one field tuple containing the number of inserted records.
	 * Inserts should be passed through BufferPool. An instances of BufferPool is
	 * available via Database.getBufferPool(). Note that insert DOES NOT need check
	 * to see if a particular tuple is a duplicate before inserting it.
	 *
	 * @return A 1-field tuple containing the number of inserted records, or null if
	 *         called more than once.
	 * @see Database#getBufferPool
	 * @see BufferPool#insertTuple
	 */
	protected Tuple fetchNext() throws TransactionAbortedException, DbException {
		// some code goes here
		int numInserted = 0;
		if (this.inserted) {
			return null;
		}

		this.child.open();

		while (this.child.hasNext()) {
			try {
				Tuple nextTuple = child.next();
				Database.getBufferPool().insertTuple(this.t, this.tableId, nextTuple);
			} catch (NoSuchElementException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			numInserted++;
		}

		this.child.close();
		this.inserted = true;
		Tuple t = new Tuple(this.td);
		t.setField(0, new IntField(numInserted));
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
