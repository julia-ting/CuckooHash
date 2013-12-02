import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class CuckooTests {
	
	CuckooHashTable<Integer, String> test;
	MapEntry<Integer, String>[] table;
	MapEntry<Integer, String>[] tableTwo;
	
	//Test add existing entry
	// Test add to removed spot
	//Test collide to removed spot
	// Test infinite loop

	// Put to empty hashtable
	@Test
	public void testPut1() {
		test = new CuckooHashTable<Integer, String>();
		test.put(5, "Hi");
		table = test.getTable();
		int key = table[5%table.length].getKey();
		String value = table[5%table.length].getValue();
		assertEquals(key, 5);
		assertEquals(value, "Hi");
		
		// Single collision
		test.put(14, "get kicked out lulz");
		table = test.getTable();
		tableTwo = test.getTableTwo();
		assertEquals((int)table[5%table.length].getKey(), 14);
		assertEquals(table[5%table.length].getValue(), "get kicked out lulz");
		//Check second table
		assertEquals((int)tableTwo[5%tableTwo.length].getKey(), 5);
		assertEquals(tableTwo[5%tableTwo.length].getValue(), "Hi");
		
	}
	
	// Tests multiple collisions
	@Test
	public void testPut2() {
		test = new CuckooHashTable<Integer, String>();
		test.put(15, "15");		// 6 % 5 = 1
		System.out.println(test);

		test.put(10, "10");		// 1 % 5 = 1 they collide, 15 now in tableTwo at 5 % 5 = 0
		System.out.println(test);

		test.put(24, "24");
		
		System.out.println(test);
		table = test.getTable();
		tableTwo = test.getTableTwo();
		assertEquals(15, (int)table[1].getKey());
		assertEquals(10, (int)tableTwo[0].getKey());
		assertEquals(24, (int)tableTwo[3].getKey());
	}
	
	// Tests resize, going past max load factor
	@Test
	public void testPut3() {
		test = new CuckooHashTable<Integer, String>();
		test.put(15, "15");		// 6 % 5 = 1
		test.put(10, "10");		// 1 % 5 = 1 they collide, 15 now in tableTwo at 5 % 5 = 0
		test.put(24, "24");
		System.out.println(test);
		
		test.put(51, "51");
		System.out.println(test);
		table = test.getTable();
		tableTwo = test.getTableTwo();
		assertEquals(10, table.length);
		assertEquals(10, tableTwo.length);
		
		assertEquals(10, (int)table[1].getKey());
		assertEquals(15, (int)tableTwo[5].getKey());
		assertEquals(24, (int)tableTwo[8].getKey());
		assertEquals(51, (int)table[6].getKey());
	}
	
	// Test infinite loop & rehash
	@Test
	public void testPut4() {
		test = new CuckooHashTable<Integer, String>();
	}
	
	@Test
	public void testHashOne() {
		test = new CuckooHashTable<Integer, String>();
		assertEquals(test.hashOne(54), 9);
	}
	
	@Test
	public void testHashTwo() {
		test = new CuckooHashTable<Integer, String>();
		assertEquals(test.hashTwo(54), 20);
	}
}
