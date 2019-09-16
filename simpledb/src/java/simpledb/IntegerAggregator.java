package simpledb;

import java.util.*;

/**
 * Knows how to compute some aggregate over a set of IntFields.
 */
public class IntegerAggregator implements Aggregator {

	private static final long serialVersionUID = 1L;
	
	  public class FieldTuple {
	    	// https://stackoverflow.com/questions/156275/what-is-the-equivalent-of-the-c-pairl-r-in-java
	    	// create pair class
	    	Field gbfield;
	    	ArrayList<Tuple> tuples;
	    	
	    	public FieldTuple(Field gbfield) {
	    		this.gbfield = gbfield;
	    		this.tuples = new ArrayList<Tuple>();
	    	}
	    }
	   
	int gbfield;
	Type gbfieldtype;
	int afield;
	Op what;
	FieldTuple groupless;
	String groupName;
	HashMap<Field, FieldTuple> groups;
	
	/**
	 * Aggregate constructor
	 * 
	 * @param gbfield     the 0-based index of the group-by field in the tuple, or
	 *                    NO_GROUPING if there is no grouping
	 * @param gbfieldtype the type of the group by field (e.g., Type.INT_TYPE), or
	 *                    null if there is no grouping
	 * @param afield      the 0-based index of the aggregate field in the tuple
	 * @param what        the aggregation operator
	 */
	
	 

	public IntegerAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
		// some code goes here
		this.gbfield = gbfield;
		this.gbfieldtype = gbfieldtype;
		this.afield = afield;
		this.what = what;
		this.groupless = new FieldTuple(null);
		this.groupName = null;
		this.groups = new HashMap<Field, FieldTuple>();
	}

	/**
	 * Merge a new tuple into the aggregate, grouping as indicated in the
	 * constructor
	 * 
	 * @param tup the Tuple containing an aggregate field and a group-by field
	 */
	public void mergeTupleIntoGroup(Tuple tup) {
		// some code goes here
		if (groupName == null && this.gbfieldtype != null) {
			groupName = tup.getTupleDesc().getFieldName(this.gbfield);
			if (groupName == null) {
				groupName = "null";
			}
		}

		if (this.gbfield == Aggregator.NO_GROUPING || this.gbfieldtype == null) {
			this.groupless.tuples.add(tup);
		} else {
			// if group exists we add the tuple to it's array of tuples
			Field tupField = tup.getField(this.gbfield);
			if (this.groups.containsKey(tupField)) {
				this.groups.get(tupField).tuples.add(tup);
			} else {
				// if group doesn't exist we add new group with the tuple
				
				FieldTuple newGroup = new FieldTuple(tupField);
				newGroup.tuples.add(tup);
				this.groups.put(tupField, newGroup);
				
			}
		}

	}

	/**
     * Create a OpIterator over group aggregate results.
     * 
     * @return a OpIterator whose tuples are the pair (groupVal, aggregateVal)
     *         if using group, or a single (aggregateVal) if no grouping. The
     *         aggregateVal is determined by the type of aggregate specified in
     *         the constructor.
     */
    public OpIterator iterator() {
        // some code goes here
//        throw new
//        UnsupportedOperationException("please implement me for lab2");
        return new OpIterator() {
        	
        	/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			private TupleDesc td;
        	private Iterator<FieldTuple>  it;
        	private boolean hasNext;
        	
			@Override
			public void open() throws DbException, TransactionAbortedException {
				// TODO Auto-generated method stub
				this.it =  groups.values().iterator();
				this.hasNext = true;
				if(!this.it.hasNext() && groupless.tuples.isEmpty()) {
					throw new DbException("integer aggregator has no values");
				}

				if (groupName != null) {
					Type[] typeAr = {gbfieldtype, Type.INT_TYPE};
					String[] nameAr = {groupName, what.toString()};
					td = new TupleDesc(typeAr, nameAr);
				} else {
					Type[] typeAr = {gbfieldtype};
					String[] nameAr = {what.toString()};
					td = new TupleDesc(typeAr, nameAr);
				}
				
				
			}

			@Override
			public boolean hasNext() throws DbException, TransactionAbortedException {
				// TODO Auto-generated method stub
				if (gbfield == Aggregator.NO_GROUPING) {
					return this.hasNext;
				}else {
					return this.it.hasNext();
				}
			}

			@Override
			public Tuple next() throws DbException, TransactionAbortedException, NoSuchElementException {
				// TODO Auto-generated method stub
				FieldTuple current;
				int rv = 0;
				if (gbfield == Aggregator.NO_GROUPING) {
					current= groupless;
					this.hasNext = false;
				} else {
					current = this.it.next();
				}
				
				
				 switch (what) {
		            case AVG: 
		            	for(Iterator<Tuple> tit = current.tuples.iterator();tit.hasNext();) {
		            		rv += ((IntField) tit.next().getField(afield)).getValue();
		            	}
		            	rv /= current.tuples.size();
		            	break;
					case COUNT:
						rv = current.tuples.size();
						break;
					case MAX:
						rv = Integer.MIN_VALUE;
						for(Iterator<Tuple> tit = current.tuples.iterator();tit.hasNext();) {
							int val = ((IntField) tit.next().getField(afield)).getValue();
		            		rv =  val > rv? val: rv;
		            	}
						break;
					case MIN:
						rv = Integer.MAX_VALUE;
						for(Iterator<Tuple> tit = current.tuples.iterator();tit.hasNext();) {
							int val = ((IntField) tit.next().getField(afield)).getValue();
		            		rv =  val < rv? val: rv;
		            	}
						break;
				
					case SUM:
						rv = 0;
						for(Iterator<Tuple> tit = current.tuples.iterator();tit.hasNext();) {
		            		rv += ((IntField) tit.next().getField(afield)).getValue();
		            	}
						break;
					default:
						throw new DbException("Aggregation type given not valid");
				 }
				 
				 Tuple t = new Tuple(this.getTupleDesc());
					if (this.td.numFields() == 1) {
						t.setField(0, new IntField(rv));
					} else {
						t.setField(0, current.gbfield);
						t.setField(1, new IntField(rv));
					}
					return t;
				 
			}

			@Override
			public void rewind() throws DbException, TransactionAbortedException {
				// TODO Auto-generated method stub
				
				this.hasNext = true;
				this.it = groups.values().iterator();
				
			}

			@Override
			public TupleDesc getTupleDesc() {
				// TODO Auto-generated method stub
				return this.td;
			}

			@Override
			public void close() {
				// TODO Auto-generated method stub
				this.it = null;
				this.hasNext = false;
			
			}
        	
        };
    }

}
