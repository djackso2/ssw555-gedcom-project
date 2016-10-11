import static org.junit.Assert.*;

import org.junit.Test;

public class Functions_test {

	
	@Test
	public void test_isDescendantOf(){
		
		Functions.parseFile("gedcom.ged");

		assertEquals(false, Functions.isDescendantOf("@I1@","@I2@"));
		assertEquals(true, Functions.isDescendantOf("@I2@","@I1@"));
		assertEquals(false, Functions.isDescendantOf("@I16@","@I10@"));
		assertEquals(true, Functions.isDescendantOf("@I16@","@I11@"));
	}
}
