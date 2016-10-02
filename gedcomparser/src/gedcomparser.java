// gedcomparser : Defines the entry point for the console application.
//
// Dan Jackson
// Eileen Roberson
// Stephen Matson
// 
// SSW-555  September 2016
//

import gedcom.IndivContainer;
import gedcom.FamilyContainer;

public class gedcomparser {
	
	private static IndivContainer indivContainer;
	private static FamilyContainer familyContainer;
	
	// Levels that require next level data
	public static enum LVL0 {
	    NONE, INDI, FAM 
	}
	public static enum LVL1 {
	    NONE, BIRT, DEAT, MARR, DIV
	}

	//********************************************************************
	//
	// Main entry point of Application
	//
	//********************************************************************
    public static void main(String[] args) {
   
    	indivContainer = new IndivContainer();
    	familyContainer = new FamilyContainer();
    	    	
        // Verify file name is passed
        if (args.length == 0)
        {
        	System.out.println("Need to include a gedcom file");
        	return;
        }
        
        String gedcomfile = args[0]; 
        System.out.println("File: " + gedcomfile);
                
        // Open and parse the file
        Functions.parseFile(gedcomfile, indivContainer, familyContainer);
       
        // Print section---------------------------------------------------------------
        // Print the Individuals
        Functions.printIndiv(indivContainer);
            
        // Print the Families
        Functions.printFam(indivContainer, familyContainer);
        
        // ERROR Checking--------------------------------------------------------------
        Functions.printError(true, "USTBD", "This is a test of an Error");
        Functions.printError(false, "USTBD", "This is a test of an Anomaly");
        
        // Parsed GEDCOM tree validation section --------------------------------------
        // Check Dates US03
        Functions.checkDeathDate(indivContainer);
        
        //Check Unique Individuals US23
        Functions.checkUniqueIndividuals(indivContainer);
        
        //Check Spouse Genders US21
        Functions.checkSpouseGenders(indivContainer, familyContainer);
	
	//Check to see if there are more than 15 kids
        Functions.checkIfTooManyKids(familyContainer);
	    
	// Check to see if the birthdates of the siblings are too close    
	Functions.checkIfSibsNotTooClose(IndivContainer indivContainer, FamilyContainer familyContainer);    
    }
}
