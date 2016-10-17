// gedcomparser : Defines the entry point for the console application.
//
// Dan Jackson
// Eileen Roberson
// Stephen Matson
// 
// SSW-555  September 2016
//


public class gedcomparser {
	
	//********************************************************************
	//
	// Main entry point of Application
	//
	//********************************************************************
    public static void main(String[] args) {
    	    	
        // Verify file name is passed
        if (args.length == 0)
        {
        	System.out.println("Need to include a gedcom file");
        	return;
        }
        
        String gedcomfile = args[0]; 
        System.out.println("File: " + gedcomfile);
                
        // Open and parse the file
        Functions.parseFile(gedcomfile);
       
        // Print section---------------------------------------------------------------
        // Print the Individuals
        Functions.printIndiv();
            
        // Print the Families
        Functions.printFam();
        
        // US39 check for upcoming anniversaries
        Functions.checkForUpcomingAnniversaries();
        
        // Parsed GEDCOM tree validation section --------------------------------------
        // Check Dates US03
        Functions.checkDeathDate();
        
        //Check Unique Individuals US23
        Functions.checkUniqueIndividuals();
        
        //Check Spouse Genders US21
        Functions.checkSpouseGenders();
	
        //Check to see if there are more than 15 kids
        Functions.checkIfTooManyKids();
	    
        // Check to see if the birthdates of the siblings are too close    
        Functions.checkIfSibsNotTooClose();    
        
        // US17 check for married descendants
        Functions.checkForMarDescendants();
	    
        //US05 check to see if date of death is before date of marriage
        Functions.checkMarriageBeforeDeath();
        
        //US11 check to see if there are issues with married dates
        Functions.checkForBigamy();
    }
}
