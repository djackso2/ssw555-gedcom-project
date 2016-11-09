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
        
        // US37 show descendants of recently deceased
        Functions.listSurvivors();     
        
        // US39 check for upcoming anniversaries
        Functions.checkForUpcomingAnniversaries();
        
        // US41 show date short formats
        Functions.showShortDates();
        
        // Parsed GEDCOM tree validation section --------------------------------------
        // Check Individual Events
        Functions.checkIndivEvents();
        
        //Check Unique Individuals US23
        Functions.checkUniqueIndividuals();
        
        //Check Unique Families
        Functions.checkUniqueFamilies();
        
        //Check Spouse Genders US21
        Functions.checkSpouseGenders();
	
        //Check to see if there are more than 15 kids
        Functions.checkIfTooManyKids();
	    
        // Check to see if the birthdates of the siblings are too close    
        Functions.checkIfSibsNotTooClose();    
        
        // US17 check for married descendants
        Functions.checkForMarDescendants();
	    
        //US05 check to see if date of death is before date of marriage
        Functions.checkEventsBeforeDeath();
        
        //US11 check to see if there are issues with married dates
        Functions.checkForBigamy();
        
        //US07 check to make sure no one is older than 150
        Functions.checkMaxAge();
    
        //US26 check for corresponding family entries
        Functions.checkCorFamTags();
        
        //US01 check for future events
        Functions.checkEventDatePriorToCurrentDate();
        
        // Checks for invalid child's birth dates
        Functions.checkChildBDate();
	    
        // US 29 lists all deceased individuals
        Functions.listAllDeceasedIndividuals();
    }
}
