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
import java.util.ArrayList;
import java.util.List;

import gedcom.*;

public class Functions {
	// Parsing section****************************************************************************************
	//********************************************************************
	//
	// Main function to parse a GEDCOM file and populate containers of 
	// individuals and families.
	//
	//********************************************************************	
	public static void parseFile(String gedcomfile, IndivContainer indivContainer, FamilyContainer familyContainer){
		
		FileReader fileReader;
    	Cindiv indiv = null;
    	CFamily fam = null;
    	int level;
    	gedcomparser.LVL0 lvl0 = gedcomparser.LVL0.NONE;
    	gedcomparser.LVL1 lvl1 = gedcomparser.LVL1.NONE;
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
					lvl0 = gedcomparser.LVL0.INDI;
				}
				//  new Family 
				else if ((level == 0) && (tag.equals("FAM")))
				{
					fam = familyContainer.addFam(lineItems[1]);
					lvl0 = gedcomparser.LVL0.FAM; 
				}
				// other level 0 tags
				else if (level == 0)
				{
					lvl0 = gedcomparser.LVL0.NONE; 
				}
				// Process Indiv level-1
				else if ((level == 1) && (lvl0 == gedcomparser.LVL0.INDI))
				{
					lvl1 = gedcomparser.LVL1.NONE;
					switch (tag) {
						case "NAME":
							indiv.setName(lineItems[2]);
							break;
						case "SEX":
							indiv.setGender(lineItems[2]);
							break;
						case "BIRT":
							lvl1 = gedcomparser.LVL1.BIRT;
							break;
						case "DEAT":
							lvl1 = gedcomparser.LVL1.DEAT;
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
				else if ((level == 1) && (lvl0 == gedcomparser.LVL0.FAM))
				{
					lvl1 = gedcomparser.LVL1.NONE;
					switch (tag) {
						case "MARR":
							lvl1 = gedcomparser.LVL1.MARR;
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
							lvl1 = gedcomparser.LVL1.DIV;
							break;
						default:
							System.out.println("ERROR: Unprocessed FAM Level 1 line");
					}		
				}
				// other level 1 tags
				else if (level == 1)
				{
					lvl1 = gedcomparser.LVL1.NONE; 
				}
				// Process level-2
				else if ((level == 2) && (tag.equals("DATE")))
				{
					if ((lvl0 == gedcomparser.LVL0.INDI) && (lvl1 == gedcomparser.LVL1.BIRT))
					{
						indiv.setDateBirth(lineItems[2]);
					}
					else if ((lvl0 == gedcomparser.LVL0.INDI) && (lvl1 == gedcomparser.LVL1.DEAT))
					{
						indiv.setDateDeath(lineItems[2]);
					}
					else if ((lvl0 == gedcomparser.LVL0.FAM) && (lvl1 == gedcomparser.LVL1.MARR))
					{
						fam.setDateMarried(lineItems[2]);
					}
					else if ((lvl0 == gedcomparser.LVL0.FAM) && (lvl1 == gedcomparser.LVL1.DIV))
					{
						fam.setDateDivorced(lineItems[2]);
					}
				}
				else
				{
					lvl1 = gedcomparser.LVL1.NONE; 
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
	//********************************************************************	
	public static void checkUniqueIndividuals(IndivContainer indivContainer){
		Cindiv indiv;
		Cindiv indivForCompare;
		ArrayList<Cindiv> duplicates = new ArrayList<Cindiv>();
		
		for (int i=0; i<indivContainer.getSize();i++){
			indiv = indivContainer.getIndiv(i);
			for(int j=0;j<indivContainer.getSize();j++){
				indivForCompare = indivContainer.getIndiv(j);				
				if(!indiv.getId().equals(indivForCompare.getId()) && !duplicates.contains(indiv)){
					if(indiv.getName().equals(indivForCompare.getName()) && 
						indiv.getDateBirth().equals(indivForCompare.getDateBirth())){						
						String errorString = "Individual " + indiv.getId() + " " + indiv.getName() + 
								" with birthdate " +indiv.getDateBirth()+ " is not unique in this GEDCOM file.\n"
								+ "Duplicate individual ID is " + indivForCompare.getId() + ".";
						duplicates.add(indivForCompare);
						printError(true, "US23", errorString);						
					}
				}
			}
		}
	}
	
	
	//********************************************************************
	// Check husband is male and wife is female
	// US21
	//********************************************************************	
	public static void checkSpouseGenders(IndivContainer indivContainer, FamilyContainer familyContainer){
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
	public static void checkDeathDate(IndivContainer indivContainer)
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
	
	// Print section****************************************************************************************	
	//********************************************************************
	// Print a list of Indiv from the list
	//********************************************************************
	public static void printIndiv(IndivContainer indivContainer)
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
	public static void printFam(IndivContainer indivContainer, FamilyContainer familyContainer)
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
	// User Story 15
	// Print anomaly if more than 15 children
	//********************************************************************
	private static void checkIfTooManyKids(FamilyContainer familyContainer){

		CFamily fam;
		
		for (int num = 0; num < familyContainer.getSize(); num++){
			fam = familyContainer.getFam(num);
			if(fam.getNumberOfChildren()>15){
				String anom = new String("Greater than 15 children in this family. " 
					+ fam.getFamID() + " Number of children: " + fam.getNumberOfChildren());
				printError(true, "US15", anom);
			}
		}

	}
	// end get number of children	

	
	
	
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
