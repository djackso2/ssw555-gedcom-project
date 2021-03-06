import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import org.junit.Test;

import gedcom.Cindiv;

public class Functions_test {

	
	@Test
	public void test_isDescendantOf(){
		
		Functions.parseFile("gedcom.ged");

		assertEquals(false, Functions.isDescendantOf("@I1@","@I2@"));
		assertEquals(true, Functions.isDescendantOf("@I2@","@I1@"));
		assertEquals(false, Functions.isDescendantOf("@I16@","@I10@"));
		assertEquals(true, Functions.isDescendantOf("@I16@","@I11@"));
	}
	
	
	@Test
	public void test_checkUniqueIndividuals(){
			
		// Redirect System.out to capture a stream for comparison
		final ByteArrayOutputStream myOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(myOut));
		
		String testString = "\n" + 
				"Error: US23: Individual @I3@ Brenda Louise /Turner/ with birthdate 23 OCT 1964 is not unique in this GEDCOM file.\n" + 
				"Duplicate individual ID is @I4@.\n" + 
				"\nError: US25: Duplicate individuals are also children of the same family.\n" +
				"Family ID in which individuals are duplicates is @F1@\n";
		
		Functions.parseFile("gedcom2.ged");		
		Functions.checkUniqueIndividuals();		
		
		assertEquals(myOut.toString(), testString);		
	}	
	
	@Test
	public void testIndivCompare(){
		Cindiv ti1 = new Cindiv("Test ID", 1);
		ti1.setName("Test Individual");
		ti1.setDateBirth("11 OCT 2016");
		
		Cindiv ti2 = new Cindiv("Test ID", 2);
		ti2.setName("Test Individual");
		ti2.setDateBirth("11 OCT 2016");
		
		Cindiv ti3 = new Cindiv("Test ID", 3);
		ti3.setName("DifferentTest Individual");
		ti3.setDateBirth("10 OCT 2016");
		
		assertTrue(Functions.isSameNameBDate(ti1, ti2));
		assertTrue(!Functions.isSameNameBDate(ti1, ti3));
		assertTrue(!Functions.isSameNameBDate(ti2, ti3));		
	}


	@Test
	public void test_FindRecentDied(){
		Functions.parseFile("gedcomUS37.ged");
		ArrayList<Cindiv> dead = Functions.findRecentDied();
		assertEquals(dead.size(), 1);
		assertEquals(dead.get(0).getId(), "@I3@");
	}
	
	@Test
	public void test_NumDescendants(){
		Functions.parseFile("gedcomUS37.ged");
		ArrayList<Cindiv> descendants;
		
		//Redirecting output to keep from outputting to console during test
		final ByteArrayOutputStream myOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(myOut));				
		
		descendants = Functions.listSurvivors();		
		assertEquals(descendants.size(),2);		
		assertEquals(descendants.get(0).getId(),"@I4@");
		assertEquals(descendants.get(1).getId(),"@I5@");
	}		
	
	@Test
	public void test_CheckMaxAge(){
		Functions.parseFile("gedcom2.ged");
		
		final ByteArrayOutputStream myOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(myOut));		
		
		Functions.checkMaxAge();
		
		String testString = "\n" + 
				"Error: US07: Individual @I5@ is over 150 years old. Age is 156.44 years.\n";
		
		assertEquals(myOut.toString(), testString);	
	}

}
