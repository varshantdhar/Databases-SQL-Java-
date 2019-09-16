package simpledb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;


/**
 * Knows how to compute some aggregate over a set of StringFields.
 */
public class StringAggregator implements Aggregator {


	  public class FieldTuple {
	    	//https://stackoverflow.com/questions/156275/what-is-the-equivalent-of-the-c-pairl-r-in-java
	    	// create pair class
	    	Field gbfield;
	    	int count;
	    	
	    	public FieldTuple(Field gbfield) {
	    		this.gbfield = gbfield;
	    		this.count=0;
	    	}
	    }
	   
	int gbfield;
	Type gbfieldtype;
	int afield;
	Op what;
	int groupless;
	String groupName;
	HashMap<Field, Integer> groups;

    private static final long serialVersionUID = 1L;

    /**
     * Aggregate constructor
     * @param gbfield the 0-based index of the group-by field in the tuple, or NO_GROUPING if there is no grouping
     * @param gbfieldtype the type of the group by field (e.g., Type.INT_TYPE), or null if there is no grouping
     * @param afield the 0-based index of the aggregate field in the tuple
     * @param what aggregation operator to use -- only supports COUNT
     * @throws IllegalArgumentException if what != COUNT
     */

    public StringAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
        // some code goes here
    	// some code goes here
    			this.gbfield = gbfield;
    			this.gbfieldtype = gbfieldtype;
    			this.afield = afield;
    			this.what = what;
    			this.groupless = 0;
    			this.groupName = null;
    			this.groups = new HashMap<Field, Integer>();
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the constructor
     * @param tup the Tuple containing an aggregate field and a group-by field
     */
    public void mergeTupleIntoGroup(Tuple tup) {
        // some code goes here

		if (this.gbfield == Aggregator.NO_GROUPING || this.gbfieldtype == null) {
			this.groupless++;
		} else {
			// get the field and then add new key or add to existing
			// getOrDefault returns the value or the second arg if no key with that  value
			 Field tupField = tup.getField(gbfield);
	         groups.put(tupField, groups.getOrDefault(tupField, 0) + 1);
		}
    }

    /**
     * Create a OpIterator over group aggregate results.
     *
     * @return a OpIterator whose tuples are the pair (groupVal,
     *   aggregateVal) if using group, or a single (aggregateVal) if no
     *   grouping. The aggregateVal is determined by the type of
     *   aggregate specified in the constructor.
     */
    public OpIterator iterator() {
        // some code goes here

        ArrayList<Tuple> tuples = new ArrayList<Tuple>();
        TupleDesc td;
        
        if (gbfield == Aggregator.NO_GROUPING) {
            td = Utility.getTupleDesc(1);
            if (groupless != 0) {
                tuples.add(Utility.getTuple(new int[] {groupless}, 1));
            }
        } else {
            td = new TupleDesc(new Type[] {gbfieldtype, Type.INT_TYPE});
            for (Map.Entry<Field, Integer> e : groups.entrySet()) {
                Tuple t = new Tuple(td);
                t.setField(0, e.getKey());
                t.setField(1, new IntField(e.getValue()));
                tuples.add(t);
            }
        }
        
        return new TupleIterator(td, tuples);
    }

}
