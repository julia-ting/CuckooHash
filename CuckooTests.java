import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class CuckooTests {
	
	CuckooHashTable<Integer, String> test;
	MapEntry<Integer, String>[] table;
	MapEntry<Integer, String>[] tableTwo;
	
	// Test add to removed spot
	//Test collide to removed spot

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

		test.put(10, "10");		// 1 % 5 = 1 they collide, 15 now in tableTwo at 5 % 5 = 0

		test.put(24, "24");		
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
		test.put(3, "3");
		test.put(4, "4");
		test.put(0, "0");
		test.put(1, "1");
		test.put(2, "2");
		
		table = test.getTable();
		tableTwo = test.getTableTwo();
		assertEquals(10, table.length);
		assertEquals(10, tableTwo.length);
		
		assertEquals(0, (int)table[0].getKey());
		assertEquals(1, (int)table[1].getKey());
		assertEquals(2, (int)table[2].getKey());
		assertEquals(3, (int)table[3].getKey());
		assertEquals(4, (int)table[4].getKey());
		assertEquals(24, (int)table[6].getKey());
		assertEquals(10, (int)tableTwo[0].getKey());
		assertEquals(15, (int)tableTwo[5].getKey());
	}
	
	// Test infinite loop & rehash
	@Test
	public void testPut4() {
		test = new CuckooHashTable<Integer, String>();
		test.put(21, "21");
		System.out.println(test);
		test.put(17, "17");
		System.out.println(test);
		test.put(12, "12");
		System.out.println(test);
		assertEquals(3, test.size());
	}
	
	// Add existing entry
	@Test
	public void testPut5() {
		test = new CuckooHashTable<Integer, String>();
		test.put(21, "21");
		table = test.getTable();
		assertEquals("21", table[3].getValue());
		test.put(21, "twenty-one");
		table = test.getTable();
		assertEquals("twenty-one", table[3].getValue());
		assertEquals(1, test.size());
	}
	
	// Add existing entry after collision
	@Test
	public void testPut6() {
		test = new CuckooHashTable<Integer, String>();
		test.put(5, "5");
		test.put(0, "0");
		System.out.println(test);
		test.put(5, "five");
		table = test.getTable();
		tableTwo = test.getTableTwo();
		System.out.println(test);
		assertEquals("five", tableTwo[0].getValue());
		assertEquals(2, test.size());
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
