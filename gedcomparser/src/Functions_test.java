import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

public class Functions_test {

	
	/*@Test
	public void test_isDescendantOf(){
		
		Functions.parseFile("gedcom.ged");

		assertEquals(false, Functions.isDescendantOf("@I1@","@I2@"));
		assertEquals(true, Functions.isDescendantOf("@I2@","@I1@"));
		assertEquals(false, Functions.isDescendantOf("@I16@","@I10@"));
		assertEquals(true, Functions.isDescendantOf("@I16@","@I11@"));
	}
	*/
	
	@Test
	public void test_checkUniqueIndividuals(){
			
		// Redirect System.out to capture a stream for comparison
		final ByteArrayOutputStream myOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(myOut));

		
		String testString = "\n" + 
				"Error: US23: Individual @I3@ Brenda Louise /Turner/ with birthdate 23 OCT 1964 is not unique in this GEDCOM file.\n" + 
				"Duplicate individual ID is @I4@.\n" + 
				"Duplicate individuals are also children of the same family. FamID: @F1@\n";
		
		Functions.parseFile("gedcom2.ged");		
		Functions.checkUniqueIndividuals();		
		
		assertEquals(myOut.toString(), testString);		
	}	
}
