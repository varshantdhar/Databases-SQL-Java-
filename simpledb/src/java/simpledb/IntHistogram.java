
package simpledb;

/** A class to represent a fixed-width histogram over a single integer-based field.
 */
public class IntHistogram {
    private int[] buckets;
    private int nBuckets, mod, totalVals, minVal;

    /**
     * Create a new IntHistogram.
     * 
     * This IntHistogram should maintain a histogram of integer values that it receives.
     * It should split the histogram into "buckets" buckets.
     * 
     * The values that are being histogrammed will be provided one-at-a-time through the "addValue()" function.
     * 
     * Your implementation should use space and have execution time that are both
     * constant with respect to the number of values being histogrammed.  For example, you shouldn't 
     * simply store every value that you see in a sorted list.
     * 
     * @param buckets The number of buckets to split the input value into.
     * @param min The minimum integer value that will ever be passed to this class for histogramming
     * @param max The maximum integer value that will ever be passed to this class for histogramming
     */
    public IntHistogram(int buckets, int min, int max) {
    	// some code goes here
        this.buckets = new int[buckets];
        this.nBuckets = buckets;
        this.mod = (int) Math.ceil((double) (max - min + 1)/buckets);
        this.totalVals = 0;
        this.minVal = min;
    }

    /**
     * Add a value to the set of values that you are keeping a histogram of.
     * @param v Value to add to the histogram
     */
    public void addValue(int v) {
    	// some code goes here
        int bucket = (v - this.minVal)/this.mod;
        this.buckets[bucket]++;
        this.totalVals++;
    }
    
    /* Created a helper function that helps me find the bucket within which 
     * a value v lies. If the bucket containing the value lies outside of 
     * the number of buckets, we return the last bucket. 
     */
    private int findBucket(int v)
    {
        int bucket = (v - this.minVal)/this.mod;
        if (bucket < 0)
        	bucket = -1;
        if (bucket >= this.nBuckets)
        	bucket = this.nBuckets;
        return bucket;
    }
    
    /**
     * Estimate the selectivity of a particular predicate and operand on this table.
     * 
     * For example, if "op" is "GREATER_THAN" and "v" is 5, 
     * return your estimate of the fraction of elements that are greater than 5.
     * 
     * @param op Operator
     * @param v Value
     * @return Predicted selectivity of this particular operator and value
     */
    public double estimateSelectivity(Predicate.Op op, int v) {
    	// some code goes here
        switch(op) {
            case EQUALS:
            case LIKE:
                return esEquals(v); 
                //call to helper function that deals with EQUALS selectivity
            case GREATER_THAN:
            case LESS_THAN:
                return esInequality(op,v);
                //call to helper function that deals with unequal selectivity
            case LESS_THAN_OR_EQ:
                return (esEquals(v) + esInequality(Predicate.Op.LESS_THAN,v));
                //organized my helped functions to be able to simplify an OR 
                //join of two existing helper functions.
            case GREATER_THAN_OR_EQ:
                return (esEquals(v) + esInequality(Predicate.Op.GREATER_THAN,v));
            case NOT_EQUALS:
                return 1.0 - esEquals(v);
                //NOT_EQUALS = 1 - EQUALS
            default:
                return -1.0; 
        }
    }
    
    /*
     * Helper function that deals with selectivity for EQUALS conditions
     */
    private double esEquals(int v) {
        int bucket = findBucket(v); 
        if (bucket < 0)
        	return 0.0;
        if (bucket >= this.nBuckets)
        	return 0.0;
        int height = this.buckets[bucket]; //height is number of vals in bucket
        return (double) ((double) height / this.mod) / this.totalVals;
    }
    
    /*
     * Helper function that deals with selectivity for GREATER_THAN,
     * LESSER_THAN conditions
     */
    private double esInequality(Predicate.Op op, int v) {
        int bucket = findBucket(v);
        int b_part, r_bucket, l_bucket, height;
        
        if (bucket < 0) {
        	// the value (v) falls below the range so greater than should return 
            // everything (1.0) and less than should return 0.0
        	r_bucket = 0;
        	l_bucket = -1;
            b_part = 0;
            height = 0;
        } else if (bucket >= this.nBuckets) {
        	// the value (v) falls above the range so greater than should return 0.0
            // and less than should return 1.0
        	r_bucket = this.nBuckets;
        	l_bucket = this.nBuckets - 1; 
            b_part = 0;
            height = 0;
        } else {
        	r_bucket = bucket + 1;
        	l_bucket = bucket - 1;
            b_part = -1;
            height = this.buckets[bucket];
        }
        double selectivity = 0.0;
        switch(op) {
        
            case GREATER_THAN:
                if (b_part == -1) {
                	// the value falls within range, we calculate b_part
                    b_part = ((r_bucket*this.mod) + this.minVal - v) / this.mod;
                }
                // calculate selectivity from b_part
                selectivity = (height * b_part) / this.totalVals;
            	if (r_bucket >= this.nBuckets)
            		// don't need to iterate through right buckets if right most
            		// bucket is bucket containing value
            		return selectivity / this.totalVals;
            	for (int i = r_bucket; i < this.nBuckets; i++)
            	{
            		selectivity += this.buckets[i];
            	}
            	return selectivity/this.totalVals;
            	
            case LESS_THAN:
                if (b_part == -1) {
                	// the value falls within range, we calculate b_part
                    b_part = (v-(l_bucket*this.mod) + this.minVal) / this.mod;
                }
                // calculate selectivity from b_part
                selectivity = (height * b_part) / this.totalVals;
            	if (l_bucket < 0)
            		// don't need to iterate through left buckets if left most
            		// bucket is bucket containing value
            		return selectivity/this.totalVals;
            	for (int i = l_bucket; i >= 0; i--)
            	{
            		selectivity += this.buckets[i];
            	}
            	return selectivity/this.totalVals;
            default:
                return -1.0;
        }
    }
    
    /**
     * @return
     *     the average selectivity of this histogram.
     *     
     *     This is not an indispensable method to implement the basic
     *     join optimization. It may be needed if you want to
     *     implement a more efficient optimization
     * */
    public double avgSelectivity()
    {
        // some code goes here
        return 1.0;
    }
    
    /**
     * @return A string describing this histogram, for debugging purposes
     */
    public String toString() {
    	 // some code goes here
    	String str = "";
    	for (int i = 0; i < this.nBuckets; i++) {
    		str += "bucket " + i + ": ";
    		for (int j = 0; j < this.buckets[i]; j++)
    		{
    			str += "|";
    		}
    		str += "\n";
    	}
        return str;
    }
}