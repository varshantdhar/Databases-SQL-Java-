package uchidb;
import java.util.*;

/**
 * @author aelmore
 */
public class HW0Runner<T, S> implements Containers<T, S>{

	//TODO you likely will need to add member variable
	private static HW0Runner instance = null;

	public Set<T> mySet = null;
	public List<T> myList = null;
	public Map<S,T> myMap = null;

	private HW0Runner() {
		// instantiation
	}

	public Set<T> initSet(T[] tArray) {
			mySet = new HashSet<T>(Arrays.asList(tArray));
			return mySet;
	}
	public List<T> initList(T[] tArray) {
			myList = Arrays.asList(tArray);
			return myList;
	}
	public Map<S,T> initEmptyMap() {
			myMap = new HashMap<S, T>();
			return myMap;
	}
	public void storeMap(Map<S,T> mapToStoreInClass) {
			myMap.putAll(mapToStoreInClass);
	}
	public boolean addToMap(S key, T value, boolean overwriteExistingKey) {
			if (overwriteExistingKey){
				myMap.put(key, value);
				return true;
			} else {
				if (myMap.putIfAbsent(key, value) == null) {
					return true;
				} else {
					return false;
				}
			}
	}

	public T getValueFromMap(S key) {
		return myMap.get(key);
	}
	public T getValueFromMap(S key, T defaultValue) {
		if (myMap.containsKey(key)){
			return myMap.get(key);
		} else {
			return defaultValue;
		}
	}

	// This class is a factory for a singleton containers class.
	// https://www.tutorialspoint.com/java/java_using_singleton.htm
	public static Containers<Integer, String> getContainers() {
		// TODO fix this function
		if (instance == null) {
			instance = new HW0Runner();
		}
		return instance;
	}


	public static void main(String[] args){

	}
}
