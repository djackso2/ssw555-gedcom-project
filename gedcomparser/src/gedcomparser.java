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

<<<<<<< HEAD
=======
	
	private static void initTagLists()
	{
		p2tagList = new ArrayList<String>();
		p1tagList = new ArrayList<String>();
		p2tagList.add("INDI");
		p1tagList.add("NAME");
		p1tagList.add("SEX");
		p1tagList.add("BIRT");
		p1tagList.add("DEAT");
		p1tagList.add("FAMC");
		p1tagList.add("FAMS");
		p2tagList.add("FAM");
		p1tagList.add("MARR");
		p1tagList.add("HUSB");
		p1tagList.add("WIFE");
		p1tagList.add("CHIL");
		p1tagList.add("DIV");
		p1tagList.add("DATE");
		p1tagList.add("HEAD");
		p1tagList.add("TRLR");
		p1tagList.add("NOTE");
	}
	
	//********************************************************************
	// Print a list of Indiv from the list
	//********************************************************************
	private static void printIndiv()
	{
		Cindiv indiv;
    
		System.out.println("\nList of Individuals");
		
		for (int num=0; num < indivContainer.getSize(); num++)
		{
			indiv = indivContainer.getIndiv(num);
			System.out.println(indiv.getId() + "\t" + indiv.getName());
		}
	}
	
	//********************************************************************
	// Print a list of Families from the list
	// Assumption: familyContainer is populated
	// Assumption: indivContainer is populated
	//********************************************************************
	private static void printFam()
	{
    	CFamily fam;
    	
    	System.out.println("\nList of Families");
    	
		for (int num=0; num < familyContainer.getSize(); num++)
		{
			fam = familyContainer.getFam(num);
			System.out.println("\nFamily ID: " + fam.getFamID());
			
			System.out.println("Husband  : ID-" + indivContainer.findIndiv(fam.getHusbandID()).getId() +
				"  \tName- " + indivContainer.findIndiv(fam.getHusbandID()).getName());
			System.out.println("Wife     : ID-" + indivContainer.findIndiv(fam.getWifeID()).getId() +
					"  \tName- " + indivContainer.findIndiv(fam.getWifeID()).getName());
			
			// The following portion of this method prints out the children of  a family.
			// Originally developed for Project_03 deliverable but unused.
			//if (fam.getNumberOfChildren() > 0)
			//{
			//	System.out.println("Children: ");
			//	for(int i = 0; i<fam.getNumberOfChildren(); i++){					
			//		System.out.println("Child    : ID-" + indivContainer.findIndiv(fam.getChildID(i)).getId() +
			//				"  \tName- " + indivContainer.findIndiv(fam.getChildID(i)).getName());															
			//	}			
			//}else{
			//	System.out.println("No Children");
			//}
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
	private static String getTag(String items[])
	{
		String tag;
		
		if (p1tagList.contains(items[1])) // is second item a valid tag
		{
			tag = items[1];
		}
		else if (p2tagList.contains(items[2])) // is third item a valid tag
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
	// Open the gedcom file 
	// read each line and build Indiv and family lists
	//********************************************************************
	private static void parseFile(String gedcomfile)
	{
		FileReader fileReader;
    	Cindiv indiv = null;
    	CFamily fam = null;
    	int level;
    	LVL0 lvl0 = LVL0.NONE;
    	LVL1 lvl1 = LVL1.NONE;

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
				tag = getTag(lineItems);
				level = Integer.parseInt(lineItems[0]);
			
				// Print out the line
				//System.out.println("\nLine  - " + line);
				//System.out.println("Level - " + level);
				//System.out.println("Tag   - " + tag);
			
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
	    
        // Check for too many children
        Functions.checkIfTooManyKids();	    
    }
}
