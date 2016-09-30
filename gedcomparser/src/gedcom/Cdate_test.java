package gedcom;

import static org.junit.Assert.*;

import org.junit.Test;

public class Cdate_test {

	@Test
	public void test_full_date() {
		Cdate dt = new Cdate();
		Cdate dt2 = new Cdate();
		
		dt.set("19 AUG 1963");
		assertEquals("19 AUG 1963",dt.get());
		
		dt2.set("1 AUG 1963");
		assertEquals(false, dt.isBefore(dt2));
		assertEquals(true, dt.isAfter(dt2));
		
		dt2.set("31 AUG 1963");
		assertEquals(true, dt.isBefore(dt2));
		assertEquals(false, dt.isAfter(dt2));
		assertEquals(true, dt.isWithin(dt2, 0, 0, 30));
		assertEquals(false, dt.isWithin(dt2, 0, 0, 3));
		
		dt2.set("03 DEC 1962");
		assertEquals(false, dt.isBefore(dt2));
		assertEquals(true, dt.isAfter(dt2));
		assertEquals(false, dt.isWithin(dt2, 0, 0, 30));
		assertEquals(true, dt.isWithin(dt2, 1, 0, 0));
	}

	@Test
	public void test_monthYear_date() {
		Cdate dt = new Cdate();
		Cdate dt2 = new Cdate();
		
		dt.set("AUG 1963");
		assertEquals("AUG 1963",dt.get());
		
		dt2.set("31 JUL 1963");
		assertEquals(false, dt.isBefore(dt2));
		assertEquals(true, dt.isAfter(dt2));
		
		dt2.set("15 AUG 1963");
		assertEquals(true, dt.isBefore(dt2));
		assertEquals(false, dt.isAfter(dt2));
		assertEquals(true, dt.isWithin(dt2, 0, 0, 30));
		assertEquals(false, dt.isWithin(dt2, 0, 0, 3));
		
		dt2.set("03 DEC 1962");
		assertEquals(false, dt.isBefore(dt2));
		assertEquals(true, dt.isAfter(dt2));
		assertEquals(false, dt.isWithin(dt2, 0, 0, 30));
		assertEquals(true, dt.isWithin(dt2, 1, 0, 0));
	}

	@Test
	public void test_Year_date() {
		Cdate dt = new Cdate();
		Cdate dt2 = new Cdate();
		
		dt.set("1963");
		assertEquals("1963",dt.get());
		
		dt2.set("31 DEC 1962");
		assertEquals(false, dt.isBefore(dt2));
		assertEquals(true, dt.isAfter(dt2));
		
		dt2.set("15 JAN 1964");
		assertEquals(true, dt.isBefore(dt2));
		assertEquals(false, dt.isAfter(dt2));
		
		dt2.set("03 DEC 1960");
		assertEquals(false, dt.isBefore(dt2));
		assertEquals(true, dt.isAfter(dt2));
		assertEquals(false, dt.isWithin(dt2, 1, 0, 0));
		assertEquals(true, dt.isWithin(dt2, 5, 0, 0));
	}
	
}
