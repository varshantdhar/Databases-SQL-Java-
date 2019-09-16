package simpledb;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ArrayList;
import java.lang.ArrayIndexOutOfBoundsException;

/**
 * Tuple maintains information about the contents of a tuple. Tuples have a
 * specified schema specified by a TupleDesc object and contain Field objects
 * with the data for each field.
 */
public class Tuple implements Serializable {
	private TupleDesc tupleDesc;
	private Field field[];
	private RecordId id;
	private static final long serialVersionUID = 1L;

	/**
	 * Create a new tuple with the specified schema (type).
	 *
	 * @param td the schema of this tuple. It must be a valid TupleDesc instance
	 *           with at least one field.
	 */
	public Tuple(TupleDesc td) {
		// some code goes here
		// create an array of fields, initialize one.
		field = new Field[td.numFields()];
		id = null;
		tupleDesc = td;
	}

	/**
	 * @return The TupleDesc representing the schema of this tuple.
	 */
	public TupleDesc getTupleDesc() {
		// some code goes here
		return tupleDesc;
	}

	/**
	 * @return The RecordId representing the location of this tuple on disk. May be
	 *         null.
	 */
	public RecordId getRecordId() {
		// some code goes here
		return id;
	}

	/**
	 * Set the RecordId information for this tuple.
	 *
	 * @param rid the new RecordId for this tuple.
	 */
	public void setRecordId(RecordId rid) {
		// some code goes here
		id = rid;
	}

	/**
	 * Change the value of the ith field of this tuple.
	 *
	 * @param i index of the field to change. It must be a valid index.
	 * @param f new value for the field.
	 */
	public void setField(int i, Field f) {
		// some code goes here
		try {
			field[i] = f;
		} catch (ArrayIndexOutOfBoundsException e) {
			return;
		}
	}

	/**
	 * @return the value of the ith field, or null if it has not been set.
	 *
	 * @param i field index to return. Must be a valid index.
	 */
	public Field getField(int i) {
		try {
			return field[i];
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Array out of Bounds");
		}
	}

	/**
	 * Returns the contents of this Tuple as a string. Note that to pass the system
	 * tests, the format needs to be as follows:
	 *
	 * column1\tcolumn2\tcolumn3\t...\tcolumnN
	 *
	 * where \t is any whitespace (except a newline)
	 */
	public String toString() {
		// some code goes here

		ArrayList<Field> fields = new ArrayList<Field>(Arrays.asList(field));
		StringBuilder sb = new StringBuilder();
		for (Field s : fields) {
			sb.append(s);
			sb.append("\t");
		}

		return sb.toString();

	}

	/**
	 * @return An iterator which iterates over all the fields of this tuple
	 */
	public Iterator<Field> fields() {

		ArrayList<Field> fieldlist = new ArrayList<Field>(Arrays.asList(field));
		return fieldlist.listIterator();
	}

	/**
	 * reset the TupleDesc of this tuple (only affecting the TupleDesc)
	 */
	public void resetTupleDesc(TupleDesc td) {
		td = new TupleDesc(null);
	}
}
