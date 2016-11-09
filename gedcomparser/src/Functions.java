
// Class is a collection of functions to parse, print, and validate/error check
// a GEDCOM file.
// NOTE: these were originally contained in gedcomparser Class but have been 
// moved in order to facilitate easier unit testing.
//
// Stephen Matson
// Dan Jackson
// Eileen Roberson
// 
// SSW-555  September 2016
//
import gedcom.CFamily;
import gedcom.Cdate;
import gedcom.Cindiv;
import gedcom.FamilyContainer;
import gedcom.IndivContainer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Functions {
	
	private static IndivContainer indivContainer;
	private static FamilyContainer familyContainer;
	
	// Levels that require next level data
	public static enum LVL0 {
	    NONE, INDI, FAM 
	}
	public static enum LVL1 {
	    NONE, BIRT, DEAT, MARR, DIV
	}
	
	// Parsing section****************************************************************************************
	//********************************************************************
	//
	// Main function to parse a GEDCOM file and populate containers of 
	// individuals and families.
	//
	//********************************************************************	
	public static void parseFile(String gedcomfile){
		// Clearing containers before parsing so parseFile can be re-called for test
		indivContainer = new IndivContainer();
		familyContainer = new FamilyContainer();
		
		FileReader fileReader;
    	Cindiv indiv = null;
    	CFamily fam = null;
    	int level;
    	LVL0 lvl0 = LVL0.NONE;
    	LVL1 lvl1 = LVL1.NONE;
    	List<String> p1tagList = initP1TagLists();  // set of valid tags
    	List<String> p2tagList = initP2TagLists();  // set of valid tags 	
    	
		// Open the file for reading
		try
		{
			fileReader = new FileReader(gedcomfile);
		}
		catch (IOException e)
		{
			System.err.format("Exception occurred trying to open '%s'.", gedcomfile);
			return;
		}
    
		// For each line in the file
		try
		{
			BufferedReader bufReader = new BufferedReader(fileReader);
			String line;
			String lineItems[] = {"","",""};
			String tag = "";
		
			while ((line = bufReader.readLine()) != null)
			{    			
				parseLine(line, lineItems);
				tag = getTag(lineItems, p1tagList, p2tagList);
				level = Integer.parseInt(lineItems[0]);
			
				// new Invid 
				if ((level == 0) && (tag.equals("INDI")))
				{
					indiv = indivContainer.addIndiv(lineItems[1]);
					lvl0 = LVL0.INDI;
				}
				//  new Family 
				else if ((level == 0) && (tag.equals("FAM")))
				{
					fam = familyContainer.addFam(lineItems[1]);
					lvl0 = LVL0.FAM; 
				}
				// other level 0 tags
				else if (level == 0)
				{
					lvl0 = LVL0.NONE; 
				}
				// Process Indiv level-1
				else if ((level == 1) && (lvl0 == LVL0.INDI))
				{
					lvl1 = LVL1.NONE;
					switch (tag) {
						case "NAME":
							indiv.setName(lineItems[2]);
							break;
						case "SEX":
							indiv.setGender(lineItems[2]);
							break;
						case "BIRT":
							lvl1 = LVL1.BIRT;
							break;
						case "DEAT":
							lvl1 = LVL1.DEAT;
							break;
						case "FAMC":
							indiv.setFamC(lineItems[2]);
							break;
						case "FAMS":
							indiv.addToFamS(lineItems[2]);
							break;
						case "NOTE":
							break;
						default:
							System.out.println("ERROR: Unprocessed INDI Level 1 line");
					}		
				}
				// Process Family level-1
				else if ((level == 1) && (lvl0 == LVL0.FAM))
				{
					lvl1 = LVL1.NONE;
					switch (tag) {
						case "MARR":
							lvl1 = LVL1.MARR;
							break;
						case "HUSB":
							fam.setHusbandID(lineItems[2]);
							break;
						case "WIFE":
							fam.setWifeID(lineItems[2]);
							break;
						case "CHIL":
							fam.addChild(lineItems[2]);
							break;
						case "DIV":
							lvl1 = LVL1.DIV;
							break;
						default:
							System.out.println("ERROR: Unprocessed FAM Level 1 line");
					}		
				}
				// other level 1 tags
				else if (level == 1)
				{
					lvl1 = LVL1.NONE; 
				}
				// Process level-2
				else if ((level == 2) && (tag.equals("DATE")))
				{
					if ((lvl0 == LVL0.INDI) && (lvl1 == LVL1.BIRT))
					{
						indiv.setDateBirth(lineItems[2]);
					}
					else if ((lvl0 == LVL0.INDI) && (lvl1 == LVL1.DEAT))
					{
						indiv.setDateDeath(lineItems[2]);
					}
					else if ((lvl0 == LVL0.FAM) && (lvl1 == LVL1.MARR))
					{
						fam.setDateMarried(lineItems[2]);
					}
					else if ((lvl0 == LVL0.FAM) && (lvl1 == LVL1.DIV))
					{
						fam.setDateDivorced(lineItems[2]);
					}
				}
				else
				{
					lvl1 = LVL1.NONE; 
				}	 
				
				
			}
			bufReader.close();
        
		}
		catch (IOException e)
		{
			System.err.format("Exception occurred reading file" + e.getMessage());
		}
		catch (NullPointerException nE){
			System.err.format("Null pointer: " + nE.getMessage() );
		}
	}
	
	//********************************************************************
	// Parse the Line up to 3 parts separated by space delimiters
	//********************************************************************
	private static void parseLine(String line, String result[])
	{
		int pos = 0;
		String del = " ";
		int i = 0;

		while (((pos = line.indexOf(del)) != -1) && (i < 2))
		{
			result[i++] = line.substring(0, pos);
			line = line.substring(pos + del.length());
		}
		result[i] = line;
	}
	
	//********************************************************************
	// Find the valid tag in the line and return it in the tag parameter
	//	    returns "Invalid tag" if a valid tag is not found 
	//********************************************************************
	private static String getTag(String items[], List<String> p1taglist, List<String> p2taglist)
	{
		String tag;
		
		if (p1taglist.contains(items[1])) // is second item a valid tag
		{
			tag = items[1];
		}
		else if (p2taglist.contains(items[2])) // is third item a valid tag
		{
			tag = items[2];
		}
		else
		{
			tag = "Invalid tag";
		}
		
		return tag;
	}
	
	//********************************************************************
	// Helper to populate a list of tags for parsing.
	// @returns a populated ArrayList of level 1 tags
	//********************************************************************	
	public static ArrayList<String> initP1TagLists()
	{
		ArrayList<String> p1tagList = new ArrayList<String>();
		p1tagList.add("NAME");
		p1tagList.add("SEX");
		p1tagList.add("BIRT");
		p1tagList.add("DEAT");
		p1tagList.add("FAMC");
		p1tagList.add("FAMS");
		p1tagList.add("MARR");
		p1tagList.add("HUSB");
		p1tagList.add("WIFE");
		p1tagList.add("CHIL");
		p1tagList.add("DIV");
		p1tagList.add("DATE");
		p1tagList.add("HEAD");
		p1tagList.add("TRLR");
		p1tagList.add("NOTE");
		return p1tagList;
	}		
	
	//********************************************************************
	// Helper to populate a list of tags for parsing.
	// @returns a populated ArrayList of level 2 tags
	//********************************************************************	
	public static ArrayList<String> initP2TagLists()
	{
		ArrayList<String>p2tagList = new ArrayList<String>();
		p2tagList.add("INDI");

		p2tagList.add("FAM");

		return p2tagList;
	}	
	
	// Validation section****************************************************************************************	
	//********************************************************************
	// US23 - Check unique Name/BirthDate combinations for all individuals
	// US25 - Include second error and family ID if the duplicate individuals 
	// are both listed as children in the same family
	// US22 - All IDs are unique
	//********************************************************************	
	public static void checkUniqueIndividuals(){
		Cindiv indiv;
		Cindiv indivForCompare;

		for (int i=0; i<indivContainer.getSize()-1;i++){
			indiv = indivContainer.getIndiv(i);
			for(int j=(i+1);j<indivContainer.getSize();j++){
				indivForCompare = indivContainer.getIndiv(j);				
			
				if(isSameNameBDate(indiv, indivForCompare)){						
					String errorString = "Individual " + indiv.getId() + " " + indiv.getName() + 
								" with birthdate " +indiv.getDateBirth().getStringDate()+ " is not unique in this GEDCOM file.\n"
								+ "Duplicate individual ID is " + indivForCompare.getId() + ".";
					printError(true, "US23", errorString);
						
					if((indiv.getFamC().equals(indivForCompare.getFamC()))&&!indiv.getFamC().equals("None")){
						errorString = "Duplicate individuals are also children of the same family.\n"
									+ "Family ID in which individuals are duplicates is " + indiv.getFamC();	
						printError(true,"US25", errorString);
					}			
				}
				if (indiv.getId().equals(indivForCompare.getId()))
				{
					String errorString = "There are duplicate Individual IDs (" + indiv.getId() + ") in the GEDCOM file.";
					printError(true, "US22", errorString);
				}
			}
		}
	} 
	//********************************************************************
	// US22 - All IDs are unique
	//********************************************************************	
	public static void checkUniqueFamilies(){
		CFamily fam;
		CFamily famForCompare;

		for (int i=0; i<familyContainer.getSize()-1;i++){
			fam = familyContainer.getFam(i);
			for(int j=(i+1);j<familyContainer.getSize();j++){
				famForCompare = familyContainer.getFam(j);				
			
				if (fam.getFamID().equals(famForCompare.getFamID()))
				{
					String errorString = "There are duplicate Family IDs (" + fam.getFamID() + ") in the GEDCOM file.";
					printError(true, "US22", errorString);
				}
			}
		}
	} 
	
	
	//********************************************************************
	// Helper to reduce multi-line conditionals when searching for
	// duplicate individuals
	//********************************************************************	
	public static boolean isSameNameBDate(Cindiv i1, Cindiv i2){
		return i1.getName().equals(i2.getName()) && (i1.getDateBirth().getStringDate().equals(i2.getDateBirth().getStringDate()));
	}
	
	//********************************************************************
	// Check husband is male and wife is female
	// US21
	//********************************************************************	
	public static void checkSpouseGenders(){
		CFamily fam;
		for (int i=0; i<familyContainer.getSize();i++){
			fam = familyContainer.getFam(i);
			Cindiv husb = indivContainer.findIndiv(fam.getHusbandID());
			Cindiv wife = indivContainer.findIndiv(fam.getWifeID());
			if(!husb.getGender().equals("M")){
				String errorString = "Family "+ fam.getFamID()+ " husband " + husb.getName() +
						" is not Male.";
				printError(true, "US21", errorString);
			}
			if(!wife.getGender().equals("F")){
				String errorString = "Family "+ fam.getFamID()+ " wife " + wife.getName() +
						" is not Female.";
				printError(true, "US21", errorString);				
			}			
		}
	}	
	
	//********************************************************************
	// Check Indiv Events
	//    US03 - Check Death Date on Individuals
	//    US08 - Birth before marriage of parents
	//    
	//********************************************************************
	public static void checkIndivEvents()
	{
		Cindiv indiv;
		CFamily fam;
		
		for (int num = 0; num < indivContainer.getSize(); num++)
		{
			indiv = indivContainer.getIndiv(num);
			if (indiv.isDeathBeforeBirth()) {
				String text = String.format("Death date (%s) of %s (%s) occurs before birth date (%s)", 
						indiv.getDateDeath().getStringDate(), indiv.getName(), indiv.getId(), indiv.getDateBirth().getStringDate());
				printError(true, "US03", text);
			}
			fam =  familyContainer.findFam(indiv.getFamC());
			if ((fam != null) && (indiv.getDateBirth().isBefore(fam.getMarriedDate()))) {
				String anomStr = String.format("Birth date (%s) of %s (%s) occurs before parents marriage date (%s)", 
						indiv.getDateBirth().getStringDate(), indiv.getName(), indiv.getId(), fam.getMarriedDate().getStringDate());
				printError(false, "US08", anomStr);
			}
			
		}
	}
	
	//********************************************************************
	// User Story 15
	// Print anomaly if more than 15 children
	//********************************************************************
	public static void checkIfTooManyKids(){

		CFamily fam;
		
		for (int num = 0; num < familyContainer.getSize(); num++){
			fam = familyContainer.getFam(num);
			if(fam.getNumberOfChildren()>15){
				String anom = new String("Greater than 15 children in this family. " 
					+ fam.getFamID() + " Number of children: " + fam.getNumberOfChildren());
				printError(false, "US15", anom);
			}
		}

	}
	// end get number of children	

	//********************************************************************
	// User Story 13
	// Print anomaly if more 2 children have birthdates that are more than
	// 2 days apart but less than 8 months
	//********************************************************************
	
	public static void checkIfSibsNotTooClose(){

		CFamily fam;

		// for each family
		for (int num=0; num < familyContainer.getSize(); num++)
		{
			fam = familyContainer.getFam(num);
			// if number of children > 1
		    if (fam.getNumberOfChildren() > 1)
		    {
		    	// for each child 
		        for(int i = 0; i < fam.getNumberOfChildren() - 1; i++)
		        {
		        	Cindiv child1;

		            // Get child1
		            child1 = indivContainer.findIndiv(fam.getChildID(i));

		            // for each additional child
		            for (int j = i+1; j < fam.getNumberOfChildren(); j++)
		            {
		            	Cindiv child2;
		            	  
		                // Get child2
		                child2 = indivContainer.findIndiv(fam.getChildID(j));

		                // if date is within 8 months and not within 2 days
		                if ((child1.getDateBirth().isWithin(child2.getDateBirth(), 0, 8, 0)) &&
		                       (!child1.getDateBirth().isWithin(child2.getDateBirth(), 0, 0, 2)))
			            {
		                	String anom = new String(child1.getId() + " and " + child2.getId() + " are not likely "
		                            		+ "to be siblings in family: " + fam.getFamID());
		                	printError(false, "US13", anom);
		                } // if
		            } // for j
		        } // for i
		    } // if children > 1
		} //for each family
	} // function
	
	
	//********************************************************************
	// Recursive routine to check if a person is a descendant of another
	//********************************************************************
	public static Boolean isDescendantOf(String parentId, String descId){
	
		CFamily fam;
		Cindiv parent;
		String childId;
		Boolean isADesc = false;
		
		parent = indivContainer.findIndiv(parentId);
		if (parent == null) return isADesc;
		
		// for each family the parent is a spouse 
		for (int famIndx = 0; (famIndx < parent.getFamS().size()) && !isADesc; famIndx++)
		{
			fam = familyContainer.findFam(parent.getFamS().get(famIndx));
			// for each child in the family
			for (int childIndx = 0; (childIndx < fam.getNumberOfChildren()) && !isADesc; childIndx++)
			{
				childId = fam.getChildID(childIndx);
				if (childId.equals(descId))
				{
					isADesc = true;
				}
				else
				{
					isADesc = isDescendantOf(childId, descId);
				}
			}
		}
	
		return isADesc;
	}
	
	//********************************************************************
	// User Story 17
	// Print error if a person is married to a descendant
	//********************************************************************
	public static void checkForMarDescendants(){
		
		CFamily fam;
		
		// for each family
		for (int num=0; num < familyContainer.getSize(); num++)
		{
			fam = familyContainer.getFam(num);
			if (isDescendantOf(fam.getHusbandID(), fam.getWifeID()))
			{
				String err = new String("In the family (" + fam.getFamID() + "), the wife (" + fam.getWifeID() + 
						") is a descendant of the husband (" + fam.getHusbandID() + ")");
                	
				printError(true, "US17", err);
			}
			if (isDescendantOf(fam.getWifeID(), fam.getHusbandID()))
			{
				String err = new String("In the family (" + fam.getFamID() + "), the husband (" + fam.getHusbandID() + 
						") is a descendant of the wife (" + fam.getWifeID() + ")");
                	
				printError(true, "US17", err);
			}
		}
	}
	
	//********************************************************************
	// User Story 39
	// Print error if a person is married to a descendant
	//********************************************************************
		public static void checkForUpcomingAnniversaries(){
			
			CFamily fam;
			int count = 0;
			Cdate today = new Cdate();
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			
			System.out.print("\n(US39) The following married couples celebrate wedding anniversaries within the next 30 days from today ");
			System.out.println(dateFormat.format(today.getCal().getTime()) + ":");
			
			// for each family
			for (int num=0; num < familyContainer.getSize(); num++)
			{
				fam = familyContainer.getFam(num);
				if ((fam.getMarriedDate().anniversaryIsWithin(today, 30)) &&
						indivContainer.findIndiv(fam.getHusbandID()).getIsAlive() &&
						indivContainer.findIndiv(fam.getWifeID()).getIsAlive())
				{
					System.out.print("     " + indivContainer.findIndiv(fam.getHusbandID()).getName());
					System.out.print(" and " + indivContainer.findIndiv(fam.getWifeID()).getName());
					System.out.println(" married " + fam.getDateMarried());
					count++;
				}
			}
			if (count == 0) System.out.println("None");
		}
		
		//********************************************************************
		//   
		//   Marriage should occur before death
		//   US_ID  05
		//
		//   Divorce should occur before death
		//   US_ID  06
		//   
		//********************************************************************
		public static void checkEventsBeforeDeath()
		{
			for (int num=0; num<familyContainer.getSize(); num++)
			{
				CFamily fam = familyContainer.getFam(num); // get the family
				Cindiv husb = indivContainer.findIndiv(fam.getHusbandID());
				Cindiv wife = indivContainer.findIndiv(fam.getWifeID());
				
				if (!husb.getIsAlive() && husb.getDateDeath().isBefore(fam.getMarriedDate()))
				{
		           	String errStr = new String("Husband ID " + husb.getId() + " date of death " 
				    + husb.getDateDeath().getStringDate() + " is before marriage date "
		            + fam.getDateMarried() + " for family " + fam.getFamID());
		           	printError(true, "US05", errStr);
				}		
				if (!wife.getIsAlive() && wife.getDateDeath().isBefore(fam.getMarriedDate()))
				{
		           	String errStr = new String("Wife ID " + wife.getId() + " date of death " 
				    + wife.getDateDeath().getStringDate() + " is before marriage date "
		            + fam.getDateMarried() + " for family " + fam.getFamID());
		          	printError(true, "US05", errStr);				
				}
				if (!husb.getIsAlive() && fam.isDivorced() && husb.getDateDeath().isBefore(fam.getDivorcedDate()))
				{
		           	String errStr = new String("Husband ID " + husb.getId() + " date of death " 
				    + husb.getDateDeath().getStringDate() + " is before divorce date "
		            + fam.getDateDivorced() + " for family " + fam.getFamID());
		           	printError(true, "US06", errStr);
				}		
				if (!wife.getIsAlive() && fam.isDivorced() && wife.getDateDeath().isBefore(fam.getDivorcedDate()))
				{
		           	String errStr = new String("Wife ID " + wife.getId() + " date of death " 
				    + wife.getDateDeath().getStringDate() + " is before divorce date "
		            + fam.getDateDivorced() + " for family " + fam.getFamID());
		           	printError(true, "US06", errStr);				
				}
			}// for loop
			
		 }// end marriageBeforeDeath
		
		//********************************************************************
		//   
		//   No bigamy
		//   US_ID  11
		//   
		//********************************************************************
		
		public static Boolean wasMarried(CFamily fam, Cdate dt)
		{
			Boolean wasMar = true;
			if (fam.getMarriedDate().isAfter(dt)) wasMar = false;
			if ((fam.isDivorced()) && dt.isAfter(fam.getDivorcedDate())) wasMar = false;
			Cindiv husb = indivContainer.findIndiv(fam.getHusbandID());
			Cindiv wife = indivContainer.findIndiv(fam.getWifeID());
			if (!husb.getIsAlive() && dt.isAfter(husb.getDateDeath())) wasMar = false;
			if (!wife.getIsAlive() && dt.isAfter(wife.getDateDeath())) wasMar = false;
			
			return wasMar;
		}
		
		public static void checkForBigamy()
		{
		    for (int num = 0; num < indivContainer.getSize(); num++)
		    {
		        Cindiv indiv = indivContainer.getIndiv(num);
		        if (indiv.getFamS().size() > 1)
		        {
		            for (int famx=0; famx < (indiv.getFamS().size() - 1); famx++)
		            {
		                for (int cmpx=famx+1; cmpx < indiv.getFamS().size(); cmpx++)
		                {
		                    CFamily fam = familyContainer.findFam(indiv.getFamS().get(famx));
		                    CFamily cmp = familyContainer.findFam(indiv.getFamS().get(cmpx));
		                    if (wasMarried(fam, cmp.getMarriedDate()) || wasMarried(cmp, fam.getMarriedDate()))
		                    {
		                        String anom = new String ("individual " + indiv.getId() + " is married to more than one person");
		                        printError(true, "US11", anom);
		                    }
		                }
		            }
		        }
		    }
					
		}// end of checkForBigamy
	
	//********************************************************************
	// Dates (birth, marriage, divorce, death) should not be after 
	//	the current date
	// US01
	//********************************************************************			
		
	public static void checkEventDatePriorToCurrentDate(){
		
		Cdate today = new Cdate();
		Cindiv indiv = null;
		
		//check each individual's  marriage date and/or divorce date
	    for (int num = 0; num < indivContainer.getSize(); num++)
	    {
	        indiv = indivContainer.getIndiv(num);
	        
	        // Check birthdate here 
			if (indiv.getDateBirth().isAfter(today)){
				String errBirth = new String ("Individual " + indiv.getId() + "date of birth is after today");
				printError(true,"US01",errBirth);
			}// end check birth date
			
			// check date of death
			if (indiv.getDateDeath().isAfter(today)){
				String errDeath = new String ("Individual " + indiv.getId() + "date of death is after today");
				printError(true,"US01",errDeath );
			}// end check death date

	        if (indiv.getFamS().size() > 1)
	        {
	            for (int famx=0; famx < (indiv.getFamS().size()); famx++)//family x
	            {
	            	CFamily fam = familyContainer.findFam(indiv.getFamS().get(famx));
	                // check divorce date
	                    
	                if (fam.getMarriedDate().isAfter(today))
	                {
	                    String errMarried = new String ("individual " + indiv.getId() + " date of marriage " + fam.getDateMarried()
	                        + " is after today");
	                    printError(true, "US01", errMarried);
	                }//end of check of marriage date
	                    
	                if (fam.getDivorcedDate().isAfter(today))
	                {
	                    String errDivorce = new String ("individual " + indiv.getId() + " date of divorce " + fam.getDivorcedDate().getStringDate() + " is after today");
	                    printError(true, "US01", errDivorce);
	                }//end of check of divorce date                    
	            }	 // end family loop           
	        }// end if family size
	    }// end of individual loop
	}// end checkEventDatePriorToCurrentDate
	
	//********************************************************************
	// US09 - Child should be born before death of mother and before 9 months  
	//	      after death of father
	// US12 - Mother should be less than 60 years older than her children
	//        and father should be less than 80 years older than his children
	//********************************************************************	
		
	public static void checkChildBDate(){
	    CFamily fam;
        Cindiv child, mom, dad;
        
        // for each family
	    for (int num=0; num < familyContainer.getSize(); num++)
	    {
	        fam = familyContainer.getFam(num);
            // if number of children > 0
	        if (fam.getNumberOfChildren() > 0)
            {
                mom = indivContainer.findIndiv(fam.getWifeID());
                dad = indivContainer.findIndiv(fam.getHusbandID());  

		        // for each child 
	            for(int i = 0; i < fam.getNumberOfChildren(); i++)
		        {
                    child = indivContainer.findIndiv(fam.getChildID(i));

                    // Check birthdate prior to mother's death
                    if (!mom.getIsAlive())
                    {
		                Cdate momDeathDate = mom.getDateDeath();
		                if (child.getDateBirth().isAfter(momDeathDate))
		                {
			                String errBirthAfter = new String ("Individual " + child.getId() + "date of birth is after mother's date of death");
			                printError(true,"US09",errBirthAfter);
		                }// end check mother's death date
                    }// end mom not alive
                    // Check birthdate prior to father's death (minus 9 months)
                    if (!dad.getIsAlive())
                    { 
		                Cdate dadDeathDate = dad.getDateDeath();
        	            if ((child.getDateBirth().isAfter(dadDeathDate )) &&
                            (child.getDateBirth().isWithin(dadDeathDate, 0, 9, 0)))
	     	            {
			                String errBirthAfter = new String ("Individual " + child.getId() + "date of birth is after dad's date of death plus 9 months");
			                printError(true,"US09",errBirthAfter);
		                }// check dad's death date
		            } // end id dad not alive
                    // Check birthdate is before Mother is 60
                    if (!child.getDateBirth().isWithin(mom.getDateBirth(), 60, 0, 0))
                    {
                    	String errBirth = new String ("Mother's (" + mom.getId() + ") birthdate (" + mom.getDateBirth().get() + ") is more than 60 years before child's (" 
                                          + child.getId() + ") birthdate (" + child.getDateBirth().get() + ")");
		                printError(true,"US12",errBirth);
                    }
                    // Check birthdate is before fathers is 80
                    if (!child.getDateBirth().isWithin(dad.getDateBirth(), 80, 0, 0))
                    {
                    	String errBirth = new String ("Father's (" + dad.getId() + ") birthdate (" + dad.getDateBirth().get() + ") is more than 80 years before child's (" 
                                          + child.getId() + ") birthdate (" + child.getDateBirth().get() + ")");
		                printError(true,"US12",errBirth);
                    }
		        }//end for each child
		    }// end if num of children > 0 
        }// end family for loop
	}// end checkChildBDatePriorToMotherOrFatherDeathDate
	
				
	//********************************************************************
	// Print error if ny individual is over 150 years old (alive or dead)
	// US07
	//********************************************************************				
	public static void checkMaxAge(){
		Cindiv indiv;
		
		for (int i = 0; i < indivContainer.getSize(); i++){
			indiv = indivContainer.getIndiv(i);
			double age = indiv.getDateBirth().getYearDif(indiv.getDateDeath());
			
			if(!(age < 150)){
				printError(true, "US07", "Individual " +indiv.getId() + " is over 150 years old. Age is " 
						+ new DecimalFormat("#.##").format(age) + " years.");			
			}			
		}
	}
		
	//********************************************************************
	// Check that all individuals with family tags have corresponding
	// entries in those indicated families.
	// US26
	//********************************************************************			
	public static void checkCorFamTags(){

		Cindiv indiv;
		CFamily family;
		boolean flag;				
		
		for(int fIndex = 0; fIndex < familyContainer.getSize(); fIndex++){					//iterate through families
			family = familyContainer.getFam(fIndex);			
			
			// Check a family's children for corresponding FamC tags
			for(int fMemIndex = 0; fMemIndex < family.getNumberOfChildren(); fMemIndex++){ 	//iterate through children
				indiv = indivContainer.findIndiv(family.getChildID(fMemIndex)); 			//get child with that index
				if(!(indiv.getFamC().equals(family.getFamID()))){							//if not same fam, error
					printError(true, "US26", "Individual " + indiv.getId() + " is indicated as a child of family "
							+ family.getFamID() + " in the family record but does not list the correct family ID.");
				}
			}
			// Check a family's spouses for corresponding FamS tags
			indiv = indivContainer.findIndiv(family.getHusbandID());						//husband
			if(!(indiv.getFamS().contains(family.getFamID()))){							//if not same fam, error				
				printError(true, "US26", "Individual " + indiv.getId() + " is indicated as the husband of family "
						+ family.getFamID() + " in the family record but does not list the correct family ID.");
			}			
			indiv = indivContainer.findIndiv(family.getWifeID());							//wife
			if(!(indiv.getFamS().contains(family.getFamID()))){							//if not same fam, error
				printError(true, "US26", "Individual " + indiv.getId() + " is indicated as the wife of family "
						+ family.getFamID() + " in the family record but does not list the correct family ID.");
			}					
		}						
		
		// check corresponding tags from individual's perspective
		for(int i = 0; i < indivContainer.getSize(); i++){
			indiv = indivContainer.getIndiv(i);
			// check individual's child relationship
			if(!(indiv.getFamC().equals("None"))){
				flag = false;
				family = familyContainer.findFam(indiv.getFamC());			
				for (int j = 0; j< family.getNumberOfChildren(); j++){
					if (family.getChildID(j).equals(indiv.getId())){						
						flag = true;
					}					
				}
				if(!flag){
					printError(true,"US26","Individual "+indiv.getId()+" is indicated as a child of family "+
							family.getFamID() + " but that family has no record of that individual as a child." );
				}
			}
			
			//check individual's spouse relationship
			for (int j = 0; j < indiv.getFamS().size(); j++){
				flag = false;
				
				family = familyContainer.findFam(indiv.getFamS().get(j));
				
				Cindiv husband = indivContainer.findIndiv(family.getHusbandID());
				Cindiv wife = indivContainer.findIndiv(family.getWifeID());
				
				for(int famInd = 0; famInd < husband.getFamS().size(); famInd++){
					if((husband.getFamS().get(famInd).equals(family.getFamID()))){
						flag = true;
					}
				}
				for(int famInd = 0; famInd < wife.getFamS().size(); famInd++){					
					if((wife.getFamS().get(famInd).equals(family.getFamID()))){
						flag = true;
					}
				}
				if(!flag){
					printError(true,"US26","Individual "+indiv.getId()+" is indicated as a spouse in family "+
						family.getFamID() + " but that family has no record of that individual as a spouse." );
				}
			}					
		}	
	}
	
	
	//***************************************************************************************
	// Determine individual's current age
	//
	// US27
	//***************************************************************************************
	
	public static void determineCurrentAge(){
		Cindiv indiv;
		Cdate today = new Cdate();
		double currentAge = 0;
		
		for (int i = 0; i < indivContainer.getSize(); i++)
		{
			indiv = indivContainer.getIndiv(i);
			currentAge = indiv.getDateBirth().getYearDif(today);
		}
            
			System.out.print("Current Age is       ");
			System.out.format("%-10.3f%n", currentAge);

	  }// end determineCurrentAge
	
	//*****************************************************************************************************
	//
	//   List all deceased individuals in a GEDCOM file
	//
	//   US 29
	//
	//*****************************************************************************************************
	
	public static void listAllDeceasedIndividuals()
	{
		Cindiv indiv;
	    
		System.out.println("\nList of Deceased Individuals");
		
		for (int num=0; num < indivContainer.getSize(); num++)
		{
			indiv = indivContainer.getIndiv(num);
			if(!indiv.getIsAlive())
			{
				System.out.printf("%-20s%s\n", "Name", indiv.getName());
				
			}// end if
			
		}// end for
		
		
		
	}// end listAllDeceasedIndividuals
	
	// Print section****************************************************************************************	
	//********************************************************************
	// Print a list of Indiv from the list
	//********************************************************************
	public static void printIndiv()
	{
		Cindiv indiv;
    
		System.out.println("\nList of Individuals");
		
		for (int num=0; num < indivContainer.getSize(); num++)
		{
			indiv = indivContainer.getIndiv(num);
			System.out.printf("%-20s%s\n", "ID:", indiv.getId());
			System.out.printf("%-20s%s\n", "Name", indiv.getName());
			System.out.printf("%-20s%s\n", "Gender:", indiv.getGender());
			System.out.printf("%-20s%s\n", "Date of Birth:", indiv.getDateBirth().get());
			System.out.printf("%-20s%s\n", "Alive:", indiv.getIsAlive());
			if(!indiv.getIsAlive())
				System.out.printf("%-20s%s\n", "Date of Death:", indiv.getDateDeath().get());
            		//added for US27
            		else determineCurrentAge();
			
			System.out.printf("%-20s%s\n", "Child of Family:", indiv.getFamC());
			System.out.printf("%-20s%s\n", "Spouse of Family:", indiv.getFamS());
			
			System.out.println();
		}
	}
	
	//********************************************************************
	// Print a list of Families from the list
	// Assumption: familyContainer is populated
	// Assumption: indivContainer is populated
	//********************************************************************
	public static void printFam()
	{
    	CFamily fam;
    	
    	System.out.println("\nList of Families\n");
    	
		for (int num=0; num < familyContainer.getSize(); num++)
		{
			fam = familyContainer.getFam(num);
			System.out.printf("%-20s%s\n", "Family ID:", fam.getFamID());
			System.out.printf("%-20s%s\n", "Date of Marriage:", fam.getDateMarried());
			System.out.printf("%-20s%s\n", "Date of Divorce:", fam.getDateDivorced());
			System.out.printf("%-20s%s - %s\n", "Husband:", fam.getHusbandID(), 
					indivContainer.findIndiv(fam.getHusbandID()).getName());
			System.out.printf("%-20s%s - %s\n", "Wife:", fam.getWifeID(), 
					indivContainer.findIndiv(fam.getWifeID()).getName());
			
			// The following portion of this method prints out the children of  a family.
			if (fam.getNumberOfChildren() > 0)
			{
				String title = "Children:";
				for(int i = 0; i < fam.getNumberOfChildren(); i++){					
					System.out.printf("%-20s%s - %s\n", title,  fam.getChildID(i),
							indivContainer.findIndiv(fam.getChildID(i)).getName());	
					title = "";
				}
			}			
			
			System.out.println();
		}
	}
	
	//********************************************************************
	// Print US41 
	//   Prints out short date formats
	//********************************************************************
	public static void showShortDates()
	{ 	
		System.out.println("\n(US41) The following are short date formats in the Gedcom file:");
		
		Cindiv indiv;
		
		for (int num=0; num < indivContainer.getSize(); num++)
		{
			indiv = indivContainer.getIndiv(num);
			if (indiv.getDateBirth().dateRes().equals("year") ||
					indiv.getDateBirth().dateRes().equals("month"))
			{
			   System.out.println("ID: " + indiv.getId() + " DOB: " + indiv.getDateBirth().get());
			}
		}
	}
	
	//********************************************************************
	// Print Error 
	//   err - true for Error, false for anomaly 
	//   US_ID - User Story ID
	//   text- text of error/anomaly
	//********************************************************************
	private static void printError(Boolean err, String US_ID, String text)
	{
		String msg = "Error";
		if (!err) msg = "Anomaly"; 
		
		System.out.printf("\n%s: %s: %s\n", msg, US_ID, text);
			
	}
	
	// MISC **************************************************************
	//********************************************************************
	// Print descendants and spouses of those who've died in the last 30 
	// days. 
	// NOTE: returns list of descendants for testing only
	// US37
	//********************************************************************	
	public static ArrayList<Cindiv> listSurvivors(){
		ArrayList<Cindiv> died = findRecentDied();
		ArrayList<Cindiv> descendants = new ArrayList<Cindiv>();

		for(int i = 0; i<died.size(); i++){
			for(int j = 0; j<indivContainer.getSize(); j++){
				if(isDescendantOf(died.get(i).getId(), indivContainer.getIndiv(j).getId())){
					descendants.add(indivContainer.getIndiv(j));
				}
			}
			
			if(descendants.size()>0){
				String msg = "\n(US37) Recently deceased individual "+died.get(i).getId()+" has the following descendants:\n";
				for(int j = 0; j<descendants.size(); j++){
					msg += descendants.get(j).getId();
					if(j<descendants.size()-1){
						msg += "\n";
					}
				}				
			
				System.out.println(msg);		
			}			
			
		}	
		return descendants;
	}

	//********************************************************************
	// @returns ArrayList of individuals who've died in last 30 days
	//********************************************************************	
	public static ArrayList<Cindiv> findRecentDied(){
		Cdate today = new Cdate();
		ArrayList<Cindiv> dec = new ArrayList<Cindiv>();
		
		for(int i = 0; i<indivContainer.getSize(); i++){		
			if(!indivContainer.getIndiv(i).getIsAlive() && indivContainer.getIndiv(i).getDateDeath().isWithin(today, 0, 0, 30)){
				dec.add(indivContainer.getIndiv(i));
			}
		}		
		return dec;
	}	
}
