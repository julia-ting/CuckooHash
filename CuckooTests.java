import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class CuckooTests {

	// Put to empty hashtable
	@Test
	public void testPut1() {
		CuckooHashTable<Integer, String> test = 
				new CuckooHashTable<Integer, String>();
		test.put(5, "Hi");
		MapEntry<Integer, String>[] table = test.getTable();
		int key = table[5%table.length].getKey();
		String value = table[5%table.length].getValue();
		assertEquals(key, 5);
		assertEquals(value, "Hi");
		
		// Single collision
		test.put(14, "get kicked out lulz");
		table = test.getTable();
		MapEntry<Integer, String>[] tableTwo = test.getTableTwo();
		assertEquals((int)table[5%table.length].getKey(), 14);
		assertEquals(table[5%table.length].getValue(), "get kicked out lulz");
		//Check second table
		assertEquals((int)tableTwo[5%tableTwo.length].getKey(), 5);
		assertEquals(tableTwo[5%tableTwo.length].getValue(), "Hi");
		
		
	}
}
