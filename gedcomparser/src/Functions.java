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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import gedcom.*;

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
	// Check unique Name/BirthDate combinations for all individuals
	// US23
	// Include second error and family ID if the duplicate individuals 
	// are both listed as children in the same family
	// US25
	//********************************************************************	
	public static void checkUniqueIndividuals(){
		Cindiv indiv;
		Cindiv indivForCompare;
		ArrayList<Cindiv> duplicates = new ArrayList<Cindiv>();
		
		for (int i=0; i<indivContainer.getSize()-1;i++){
			indiv = indivContainer.getIndiv(i);
			for(int j=(i+1);j<indivContainer.getSize();j++){
				indivForCompare = indivContainer.getIndiv(j);				
				if(!indiv.getId().equals(indivForCompare.getId()) && !duplicates.contains(indiv)){
			
					if(isSameNameBDate(indiv, indivForCompare)){						
						String errorString = "Individual " + indiv.getId() + " " + indiv.getName() + 
								" with birthdate " +indiv.getDateBirth()+ " is not unique in this GEDCOM file.\n"
								+ "Duplicate individual ID is " + indivForCompare.getId() + ".";
						duplicates.add(indivForCompare);
				
						printError(true, "US23", errorString);
						
						if((indiv.getFamC().equals(indivForCompare.getFamC()))&&!indiv.getFamC().equals("None")){
							errorString = "Duplicate individuals are also children of the same family.\n"
									+ "Family ID in which individuals are duplicates is " + indiv.getFamC();
							
							printError(true,"US25", errorString);
						}			
					}			
				}
			}
		}
	}
	
	//********************************************************************
	// Helper to reduce multi-line conditionals when searching for
	// duplicate individuals
	//********************************************************************	
	public static boolean isSameNameBDate(Cindiv i1, Cindiv i2){
		return i1.getName().equals(i2.getName()) && (i1.getDateBirth().equals(i2.getDateBirth()));
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
	// Check Death Date on Individuals
	// 
	//********************************************************************
	public static void checkDeathDate()
	{
		Cindiv indiv;
		
		for (int num = 0; num < indivContainer.getSize(); num++)
		{
			indiv = indivContainer.getIndiv(num);
			if (indiv.isDeathBeforeBirth()) {
				String text = String.format("Death date (%s) of %s (%s) occurs before birth date (%s)", 
						indiv.getDateDeath(), indiv.getName(), indiv.getId(), indiv.getDateBirth());
				printError(true, "US03", text);
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
		                if ((child1.getBirthDate().isWithin(child2.getBirthDate(), 0, 8, 0)) &&
		                       (!child1.getBirthDate().isWithin(child2.getBirthDate(), 0, 0, 2)))
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
		//********************************************************************
		public static void checkMarriageBeforeDeath()
		{
			for (int num=0; num<familyContainer.getSize(); num++)
			{
				CFamily fam = familyContainer.getFam(num); // get the family
				Cindiv husb = indivContainer.findIndiv(fam.getHusbandID());
				Cindiv wife = indivContainer.findIndiv(fam.getWifeID());
				
				if(!husb.getIsAlive() || !wife.getIsAlive())
				{
					if(husb.getDateDeath().isBefore(fam.getMarriedDate()))
					{
		            	String anom = new String("Husband ID " + husb.getId() + " date of death " 
					    + husb.getDateDeath().getStringDate() + " is before marriage date "
		                + fam.getDateMarried() + "for family " + fam.getFamID());
		            	printError(true, "US05", anom);
					}		
					if(wife.getDateDeath().isBefore(fam.getMarriedDate()))
					{
		            	String anom = new String("Wife ID " + wife.getId() + " date of death " 
					    + wife.getDateDeath().getStringDate() + " is before marriage date "
		                + fam.getDateMarried() + "for family " + fam.getFamID());
		            	printError(true, "US05", anom);				
					}
				}
			}// for loop
			
		 }// end marriageBeforeDeath
	
	
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
			System.out.printf("%-20s%s\n", "Date of Birth:", indiv.getDateBirth());
			System.out.printf("%-20s%s\n", "Alive:", indiv.getIsAlive());
			if(!indiv.getIsAlive())
				System.out.printf("%-20s%s\n", "Date of Death:", indiv.getDateDeath());
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
}
