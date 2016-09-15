
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
import gedcom.Cindiv;
import gedcom.CFamily;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class gedcomparser {

	private static List<String> p1tagList;  // set of valid tags
	private static List<String> p2tagList;  // set of valid tags
	
	private static IndivContainer indivContainer;
	private static FamilyContainer familyContainer;
	
	// Levels that require next level data
	public enum LVL0 {
	    NONE, INDI, FAM 
	}
	public enum LVL1 {
	    NONE, BIRT, DEAT, MARR, DIV
	}

	
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
    
		for (int num=0; num < indivContainer.getSize(); num++)
		{
			indiv = indivContainer.getIndiv(num);
			//TODOSystem.out.println(indiv.getName());
		}
	}
	
	//********************************************************************
	// Print a list of Families from the list
	//********************************************************************
	private static void printFam()
	{
    	CFamily fam;
    	
		for (int num=0; num < familyContainer.getSize(); num++)
		{
			fam = familyContainer.getFam(num);
			System.out.println("TODO: Print Husband And Wife Name and ID");;
			System.out.println("TODO: Print Kids Names and IDs");
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
				System.out.println("\nLine  - " + line);
				System.out.println("Level - " + level);
				System.out.println("Tag   - " + tag);
			
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
							//TODOindiv.setName(lineItems[2]);
							break;
						case "SEX":
							//TODOindiv.setSex(lineItems[2]);
							break;
						case "BIRT":
							lvl1 = LVL1.BIRT;
							break;
						case "DEAT":
							lvl1 = LVL1.DEAT;
							break;
						case "FAMC":
							//TODOindiv.setFamc(lineItems[2]);
							break;
						case "FAMS":
							//TODOindiv.setFams(lineItems[2]);
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
							// TODOfam.addHusb(lineItems[2]);
							break;
						case "WIFE":
							// TODOfam.addWife(lineItems[2]);
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
						//TODOindiv.setBirth(lineItems[2])
					}
					else if ((lvl0 == LVL0.INDI) && (lvl1 == LVL1.DEAT))
					{
						//TODOindiv.setDeath(lineItems[2])
					}
					else if ((lvl0 == LVL0.FAM) && (lvl1 == LVL1.MARR))
					{
						//TODOfam.setMarr(lineItems[2])
					}
					else if ((lvl0 == LVL0.FAM) && (lvl1 == LVL1.DIV))
					{
						//TODOfam.setDiv(lineItems[2])
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
    	    	
        initTagLists();
        
        // Verify file name is passed
        if (args.length == 0)
        {
        	System.out.println("Need to include a gedcom file");
        	return;
        }
        
        String gedcomfile = args[0]; 
        System.out.println("File: " + gedcomfile);
        
        // Open and parse the file
        parseFile(gedcomfile);
        
        // Print the Individuals
        printIndiv();
            
        // Print the Families
        printFam();
          
    }
}
