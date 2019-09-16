package simpledb;

import java.io.Serializable;
import java.util.*;

/**
 * TupleDesc describes the schema of a tuple.
 */
public class TupleDesc implements Serializable {

	/**
	 * A help class to facilitate organizing the information of each field
	 */
	public static class TDItem implements Serializable {

		private static final long serialVersionUID = 1L;
		/**
		 * The type of the field
		 */
		public final Type fieldType;

		/**
		 * The name of the field
		 */
		public final String fieldName;

		public TDItem(Type t, String n) {
			this.fieldName = n;
			this.fieldType = t;
		}

		public String toString() {
			return fieldName + "(" + fieldType + ")";
		}
	}

	// vector to hold stuff for the class
	private Vector<TDItem> tupleDescVec;

	/**
	 * @return An iterator which iterates over all the field TDItems that are
	 *         included in this TupleDesc
	 */
	public Iterator<TDItem> iterator() {
		// return the iterator of the vector with the tupleDesc items
		return tupleDescVec.iterator();
	}

	private static final long serialVersionUID = 1L;

	/**
	 * Create a new TupleDesc with typeAr.length fields with fields of the specified
	 * types, with associated named fields.
	 * 
	 * @param typeAr  array specifying the number of and types of fields in this
	 *                TupleDesc. It must contain at least one entry.
	 * @param fieldAr array specifying the names of the fields. Note that names may
	 *                be null.
	 */
	public TupleDesc(Type[] typeAr, String[] fieldAr) {
		tupleDescVec = new Vector<TDItem>(typeAr.length);
		for (int i = 0; i < typeAr.length; i++) {
			tupleDescVec.add(new TDItem(typeAr[i], fieldAr[i]));
		}
	}

	/**
	 * Constructor. Create a new tuple desc with typeAr.length fields with fields of
	 * the specified types, with anonymous (unnamed) fields.
	 * 
	 * @param typeAr array specifying the number of and types of fields in this
	 *               TupleDesc. It must contain at least one entry.
	 */
	public TupleDesc(Type[] typeAr) {
		tupleDescVec = new Vector<TDItem>(typeAr.length);
		for (int i = 0; i < typeAr.length; i++) {
			tupleDescVec.add(new TDItem(typeAr[i], null));
		}
	}

	/**
	 * @return the number of fields in this TupleDesc
	 */
	public int numFields() {
		/*
		 * Corresponds to the size of the tupleDescVector which holds all the types
		 */
		return tupleDescVec.size();
	}

	/**
	 * Gets the (possibly null) field name of the ith field of this TupleDesc.
	 * 
	 * @param i index of the field name to return. It must be a valid index.
	 * @return the name of the ith field
	 * @throws NoSuchElementException if i is not a valid field reference.
	 */
	public String getFieldName(int i) throws NoSuchElementException {
		// Checks if the index i is in range if not throws else returns the name
		if (i < 0 || i >= this.numFields()) {
			throw new NoSuchElementException();
		}
		return tupleDescVec.get(i).fieldName;
	}

	/**
	 * Gets the type of the ith field of this TupleDesc.
	 * 
	 * @param i The index of the field to get the type of. It must be a valid index.
	 * @return the type of the ith field
	 * @throws NoSuchElementException if i is not a valid field reference.
	 */
	public Type getFieldType(int i) throws NoSuchElementException {
		// checks if i in range else and returns if so else throws
		if (i < 0 || i >= this.numFields()) {
			throw new NoSuchElementException("Index i out of range");
		}
		return tupleDescVec.get(i).fieldType;
	}

	/**
	 * Find the index of the field with a given name.
	 * 
	 * @param name name of the field.
	 * @return the index of the field that is first to have the given name.
	 * @throws NoSuchElementException if no field with a matching name is found.
	 */
	public int fieldNameToIndex(String name) throws NoSuchElementException {
		// iterate through the vec check if it's found if not raise exception else
		// return index
		for (int i = 0; i < this.numFields(); i++) {
			TDItem item = tupleDescVec.get(i);
			if (item.fieldName == null) {
				continue;
			}
			if (item.fieldName.equals(name)) {
				return i;
			}
		}
		throw new NoSuchElementException("Element not found");
	}

	/**
	 * @return The size (in bytes) of tuples corresponding to this TupleDesc. Note
	 *         that tuples from a given TupleDesc are of a fixed size.
	 */
	public int getSize() {
		//
		int size = 0;
		for (int i = 0; i < numFields(); i++) {
			size += tupleDescVec.get(i).fieldType.getLen();
		}
		return size;
	}

	/**
	 * Merge two TupleDescs into one, with td1.numFields + td2.numFields fields,
	 * with the first td1.numFields coming from td1 and the remaining from td2.
	 * 
	 * @param td1 The TupleDesc with the first fields of the new TupleDesc
	 * @param td2 The TupleDesc with the last fields of the TupleDesc
	 * @return the new TupleDesc
	 */
	public static TupleDesc merge(TupleDesc td1, TupleDesc td2) {
		// some code goes here
		int td1Size = td1.numFields();
		int td2Size = td2.numFields();
		int size = td1Size + td2Size;
		Type[] newTypes = new Type[size];
		String[] newNames = new String[size];

		for (int i = 0; i < td1Size; i++) {
			newTypes[i] = td1.getFieldType(i);
			newNames[i] = td1.getFieldName(i);
		}
		for (int j = 0; j < td2Size; j++) {
			newTypes[j + td1Size] = td2.getFieldType(j);
			newNames[j + td1Size] = td2.getFieldName(j);
		}

		return new TupleDesc(newTypes, newNames);
	}

	/**
	 * Compares the specified object with this TupleDesc for equality. Two
	 * TupleDescs are considered equal if they have the same number of items and if
	 * the i-th type in this TupleDesc is equal to the i-th type in o for every i.
	 * 
	 * @param o the Object to be compared for equality with this TupleDesc.
	 * @return true if the object is equal to this TupleDesc.
	 */

	public boolean equals(Object o) {
		// Fist check if o is not null and of correct type
		// then check if right size and right number fields
		// finally check if all field types are equal
		if (o == null || !(o instanceof TupleDesc)) {
			return false;
		}

		TupleDesc td = (TupleDesc) o;
		if (this.getSize() != td.getSize() || this.numFields() != td.numFields()) {
			return false;
		}

		for (int i = 0; i < td.numFields(); i++) {
			if (!this.getFieldType(i).equals(td.getFieldType(i)))
				return false;
		}

		return true;

	}

	public int hashCode() {
		// If you want to use TupleDesc as keys for HashMap, implement this so
		// that equal objects have equals hashCode() results
		return this.toString().hashCode();
	}

	/**
	 * Returns a String describing this descriptor. It should be of the form
	 * "fieldType[0](fieldName[0]), ..., fieldType[M](fieldName[M])", although the
	 * exact format does not matter.
	 * 
	 * @return String describing this descriptor.
	 */
	public String toString() {
		// some code goes here
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.numFields(); i++) {
			sb.append(tupleDescVec.get(i).fieldType + "[" + i + "]" + "(" + tupleDescVec.get(i).fieldName + "), ");
		}

		return sb.toString();
	}
}
