import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;


public class CuckooHashTable<Integer, V> {
	/**
	 * This class will represent a modified linear probing hash table. 
	 * The modification is specified in the comments for the put method.
	 */
		
	/**
	 * Constant determining the max load factor
	 */
	private final double MAX_LOAD_FACTOR = 0.71;
	
	/**
	 * Constant determining the initial table size
	 */
	private final int INITIAL_TABLE_SIZE = 10;
	
	/**
	 * Number of elements in the table
	 */
	private int size;
	
	/**
	 * The backing array of this hash table
	 */
	private MapEntry<Integer,V>[] table;
	
	private MapEntry<Integer,V>[] tableTwo;
	
	private int tableOneHash;
	private int tableTwoHash;
	
	// Used in hashThree
	private int BASE = 197;

	/**
	 * Initialize the instance variables
	 * Initialize the backing array to the initial size given
	 */
	public CuckooHashTable() {
		size = 0;
		table = new MapEntry[INITIAL_TABLE_SIZE/2];
		tableTwo = new MapEntry[INITIAL_TABLE_SIZE/2];
		tableOneHash = 1;
		tableTwoHash = 2;
	}
	
	/**
	 * Add the key value pair in the form of a MapEntry
	 * Use the default hash code function for hashing
	 * This is a linear probing hash table so put the entry in the table accordingly
	 * 
	 * Make sure to use the given max load factor for resizing
	 * Also, resize by doubling and adding one. In other words:
	 * 
	 * newSize = (oldSize * 2) + 1
	 *
	 * The load factor should never exceed maxLoadFactor at any point. So if adding this element
	 * will cause the load factor to be exceeded, you should resize BEFORE adding it. Otherwise
	 * do not resize.
	 * 
	 * IMPORTANT Modification: If the given key already exists in the table
	 * then set it as the next entry for the already existing key. This means
	 * that you will never be replacing values in the hashtable, only adding or removing.
	 * This is similar to external chaining
	 * 
	 * @param key This will never be null
	 * @param value This can be null
	 */
	public void put(Integer key,V value) {
		if (((float)(size+1) / (table.length*2)) > MAX_LOAD_FACTOR) {
			// regrow
			MapEntry<Integer,V>[] temp = table;
			MapEntry<Integer,V>[] tempTwo = tableTwo;
			table = new MapEntry[temp.length * 2];
			tableTwo = new MapEntry[tempTwo.length * 2];
			size = 0;
			for (int i = 0; i < temp.length; i++) {
				MapEntry<Integer,V> curr = temp[i];
				if (curr != null) {
					addEntry(curr.getKey(), curr.getValue(), tableOneHash,
							tableTwoHash);
				}
			}
			for (int i = 0; i < tempTwo.length; i++) {
				MapEntry<Integer,V> curr = tempTwo[i];
				if (curr != null) {
					addEntry(curr.getKey(), curr.getValue(), tableOneHash,
							tableTwoHash);
				}
			}
		} 
		addEntry(key, value, tableOneHash, tableTwoHash);	 
	}
	
	private int hash(int hashNum, Integer key) {
		int hash = 0;
		if (hashNum == 1) {
			hash = hashOne((java.lang.Integer)key);
		} else if (hashNum == 2) {
			hash = hashTwo((java.lang.Integer)key);
		} else if (hashNum == 3) {
			hash = hashThree((java.lang.Integer)key);
		}
		return hash;
	}
	
	private void addEntry(Integer key, V value, 
			int tableOneHash, int tableTwoHash) {
		// Use first table hash according to hash code
		int index = hash(tableOneHash, key) % table.length; // index for first hash
		int indexTwo = hash(tableTwoHash, key) % tableTwo.length;
		MapEntry<Integer,V> newEntry = new MapEntry<Integer, V>(key, value);
		// Table at index has something already
		if (table[index] != null) {
			// that something has not been removed
			if (!(table[index].isRemoved())) {
				// and it equals the key currently being added
				if (table[index].getKey().equals(key)) {
					table[index] = newEntry;
					return;
				}
			// that something HAS been removed
			} else if (tableTwo[indexTwo] != null) {
				// check table Two to see if equal
				if (!(tableTwo[indexTwo].isRemoved())) {
					if (tableTwo[indexTwo].getKey().equals(key)) {
						tableTwo[indexTwo] = newEntry;
						return;
					}
				}
			}
			if (tableTwo[indexTwo] != null) {
				if (!(tableTwo[indexTwo].isRemoved())) {
					if (tableTwo[indexTwo].getKey().equals(key)) {
						tableTwo[indexTwo] = newEntry;
						return;
					}
				}
			}
		}
		// No existing key, adds accordingly
		// while (!isValidEntry(whichTable, index) && collisionTracker < size*100) {
		int collisionTracker = 0;
		MapEntry<Integer,V>[] currTable = tableTwo;
		MapEntry<Integer,V> currEntry;
		MapEntry<Integer,V> oldEntry;
		if (isValidEntryIndex(table, index)) {
			table[index] = newEntry;
		} else {
			collisionTracker++;
			currEntry = table[index];
			table[index] = newEntry;
			int currIndex = hash(tableTwoHash, currEntry.getKey()) % 
					tableTwo.length;
			while (!isValidEntryIndex(currTable, currIndex) &&
					collisionTracker < (table.length)*20) {
				oldEntry = currTable[currIndex];
				currTable[currIndex] = currEntry;
				currEntry = oldEntry;
				if (currTable == table) {
					currIndex = hash(tableTwoHash, currEntry.getKey()) %
							table.length;
					currTable = tableTwo;
				} else {
					currIndex = hash(tableOneHash, currEntry.getKey()) %
							tableTwo.length;
					currTable = table;
				}
				collisionTracker++;
				System.out.println("Collisions: " + collisionTracker);
			}
			if (collisionTracker >= table.length*20) {
				System.out.println("Rehashing!");
				rehash();
				addEntry(key, value, tableOneHash, tableTwoHash);
				return;
			}
			currTable[currIndex] = currEntry;
		}
		size++;

	}
	
	//Helper method - checks if valid index to enter;
	private boolean isValidEntryIndex(MapEntry<Integer,V>[] table, int i) {
		return (table[i] == null || table[i].isRemoved()); 
	}
	
	/**
	 * Remove the entry with the given key.
	 * 
	 * If there are multiple entries with the same key then remove the last one
	 * 
	 * @param key
	 * @return The value associated with the key removed
	 */
	public V remove(Integer key){
		if(!contains(key)){
			return null;
		}
		int[] tableAndIndex = findKey(key); //contains reference information
		int tableNum = tableAndIndex[0]; //number of the table to look at
		int tableIndex = tableAndIndex[1]; //index to look at
		if(tableNum == 1) {
			table[tableIndex].setRemoved(true);
			size--;
			V temp = table[tableIndex].getValue(); //return the removed value
			return temp;
		} else if(tableNum == 2) {
			tableTwo[tableIndex].setRemoved(true);
			size--;
			V temp = tableTwo[tableIndex].getValue(); //return the removed value
			return temp;
		} else {
			return null;
		}
	}
	
	/**
	 * Checks whether an entry with the given key exists in the hash table
	 * 
	 * @param key
	 * @return
	 */
	public boolean contains(Integer key){
		return (findKey(key)[1] > -1);
	}
	
	/**
	 * Return a collection of all the values
	 * 
	 * We recommend using an ArrayList here
	 *
	 * @return 
	 */
	public Collection<V> values(){
		Collection<V> values = new ArrayList<V>();
		for(int ii=0;ii<table.length;ii++){ //values from table
			if(table[ii]!=null && !table[ii].isRemoved()){
				values.add(table[ii].getValue());
			}
		}
		for(int ii=0;ii<tableTwo.length;ii++){ //values from tableTwo
			if(tableTwo[ii]!=null && !tableTwo[ii].isRemoved()){
				values.add(tableTwo[ii].getValue());
			}
		}
		return values;
	}
	
	/**
	 * Return a set of all the distinct keys
	 * 
	 * We recommend using a HashSet here
	 * 
	 * Note that the map can contain multiple entries with the same key
	 * 
	 * @return
	 */
	public Set<Integer> keySet(){
		Set<Integer> keys = new HashSet();
		for(int ii=0;ii<table.length;ii++){
			if(table[ii]!=null && !table[ii].isRemoved()){
				keys.add(table[ii].getKey());
			}
		}
		for(int ii=0;ii<tableTwo.length;ii++){
			if(tableTwo[ii]!=null && !tableTwo[ii].isRemoved()){
				keys.add(tableTwo[ii].getKey());
			}
		}
		return keys;
	}
	
	/**
	 * Return the number of values associated with one key
	 * Return -1 if the key does not exist in this table
	 * @param key
	 * @return
	 */
	public int keyValues(Integer key){
		if(!contains(key)){
			return -1;
		}
		else{
			int count = 1;
			MapEntry current = table[findKey(key)];
			while(current.getNext()!=null){
				count += 1;
				current=current.getNext();
			}
			return count;
		}
	}
	
	private int[] findKey(Integer key){
		int index = hash(tableOneHash, key) % table.length;
		int indexTwo = hash(tableTwoHash, key) % tableTwo.length;
		int[] tableAndIndex = new int[2]; //reference information
		if(table[index]!=null && !table[index].isRemoved() && table[index].getKey().equals(key)) {
				tableAndIndex[0] = 1; //set which table to look at
				tableAndIndex[1] = index; //set index to look at
				return tableAndIndex;
		} else if(tableTwo[indexTwo]!=null && !tableTwo[indexTwo].isRemoved() && tableTwo[indexTwo].getKey().equals(key)) {
				tableAndIndex[0] = 2; //set table to look at
				tableAndIndex[1] = indexTwo; //set index to look at
				return tableAndIndex;
		}
		return null;
	}
	/**
	 * Return a set of all the unique key-value entries
	 * 
	 * Note that two map entries with both the same key and value
	 * could exist in the map.
	 * 
	 * @return
	 */
	public Set<MapEntry<Integer,V>> entrySet(){
		Set<MapEntry<Integer,V>> entries = new HashSet();
		for(int ii=0;ii<table.length;ii++){
			if(table[ii]!=null && !table[ii].isRemoved()){
				entries.add(table[ii]); //entries from table
			}
		}
		for(int ii=0;ii<tableTwo.length;ii++){
			if(tableTwo[ii]!=null && !tableTwo[ii].isRemoved()){
				entries.add(tableTwo[ii]); //entries from tableTwo
			}
		}
		return entries;
	}
	
	/**
	 * Clears the hash table
	 */
	public void clear() {
		table = new MapEntry[INITIAL_TABLE_SIZE / 2];
		tableTwo = new MapEntry[INITIAL_TABLE_SIZE / 2];
		size = 0;
	}
	
	   public int hashOne(int key){
           int intLength = String.valueOf(key).length();
           int base = 10;
           int sum = 0;
           for(int x = 0; x < intLength; x++){
                   sum += (key%base)/(base/10);
                   base = base*10;
           }
           //System.out.println("HASH ONE - Key: " + key + " | HASH: " + sum);
           return sum;
   }

   public int hashTwo(int key){
           int intLength = String.valueOf(key).length();
           int base = 10;
           int product = 1;
           for(int x = 0; x < intLength; x++){
                   product *= (key%base)/(base/10);
                   base = base*10;
           }
         //  System.out.println("HASH TWO - Key: " + key + " | HASH: " + product);
           return product;
   }

   public int hashThree(int key){
           int intLength = String.valueOf(key).length();
           int base = 10;
           int result = 0;
           for(int x = 0; x < intLength; x++){
                   result += (key%base)/(base/10)*BASE;
                   base = base*10;
           }
           //System.out.println("HASH THREE - Key: " + key + " | HASH: " + base);
           return result;
   }
   
   public void rehash(){                	
   	Random rand = new Random();
   	int hashChanges = rand.nextInt(3);
   	if (hashChanges == 1){
   		tableOneHash = rand.nextInt(3);
   		while (tableOneHash == tableTwoHash){
   			tableOneHash = rand.nextInt(3);
   		}
   	} else if (hashChanges == 2){
   		tableTwoHash = rand.nextInt(3);
   		while (tableTwoHash == tableOneHash){
   			tableTwoHash = rand.nextInt(3);
   		}
   	} else if (hashChanges == 3){
   		 tableOneHash = rand.nextInt(3);
   		 tableTwoHash = rand.nextInt(3);
			while (tableOneHash == tableTwoHash){
				tableOneHash = rand.nextInt(3);
				tableTwoHash = rand.nextInt(3);
			}
   	}
   	MapEntry<Integer, V>[] tableClone = table.clone();
   	MapEntry<Integer, V>[] tableTwoClone = tableTwo.clone();
   	table = new MapEntry[table.length]; //size of table?
   	tableTwo = new MapEntry[tableTwo.length]; //size of table two?
   	this.size = 0;
   	for (int x = 0; x < tableClone.length; x++){
   		if (tableClone[x] != null){
   			put(tableClone[x].getKey(),tableClone[x].getValue());
   		}
   	}
   	for (int x = 0; x < tableTwoClone.length; x++){
   		if(tableTwoClone[x] != null){
   			put(tableTwoClone[x].getKey(),tableTwoClone[x].getValue());
   		}
   	}
   }
	
	/*
	 * The following methods will be used for grading purposes do not modify them
	 */
   public String toString() {
	   String result = "TABLE ONE: ";
	   for (int i = 0; i < table.length; i++) {
		   if (table[i] == null) {
			   result = result + i + "|NULL" + " - ";
		   } else {
			   result = result + i + "|" + table[i].getKey() + " - ";
		   }
	   }
	   result = result + "\nTABLE TWO: ";
	   for (int i = 0; i < tableTwo.length; i++) {
		   if (tableTwo[i] == null) {
			   result = result + i + "|NULL" + " - ";
		   } else {
			   result = result + i + "|" + tableTwo[i].getKey() + " - ";
		   }
	   }
	   result = result + "\n~~~~~~~~~~~~~";
	   
	   return result;
   }
	
	public int size(){
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public MapEntry<Integer, V>[] getTable() {
		return table;
	}
	
	public MapEntry<Integer, V>[] getTableTwo() {
		return tableTwo;
	}
	
	public void setTable(MapEntry<Integer, V>[] table) {
		this.table = table;
	}


}
