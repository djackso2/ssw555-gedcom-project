
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;

public class gedcomparser {

	private static List<String> p1tagList;  // set of valid tags
	private static List<String> p2tagList;  // set of valid tags
	
	private static void initTagLists()
	{
		p2tagList = new ArrayList<String>();
		p1tagList = new ArrayList<String>();
		p2tagList.add("INDI");
		p1tagList.add("NAME");
		p1tagList.add("SEX");
		p1tagList.add("BIRT");
		p1tagList.add("SEX");
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
	// Gedcom file parser
	// outputs the Line, the Level and the tag for each line in the passed gedcom file
	//********************************************************************
    public static void main(String[] args) {
    	
    	FileReader fileReader;
    	IndivContainer indivContainer = new IndivContainer();
    	FamilyContainer familyContainer = new FamilyContainer();
    	
        initTagLists();
        
        // Verify file name is passed
        if (args.length == 0)
        {
        	System.out.println("Need to include a gedcom file");
        	return;
        }
        
        String gedcomfile = args[0]; 
        System.out.println(gedcomfile);
        
        // Open the file for reading
        try
        {
        	fileReader = new FileReader(gedcomfile);
        }
        catch (Exception e)
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
    			System.out.println("\nLine  - " + line);
    			parseLine(line, lineItems);
    			System.out.println("Level - " + lineItems[0]);
    			tag = getTag(lineItems);
    			System.out.println("Tag   - " + tag);
    			
    			if ((lineItems[0].equals("0")) && (tag.equals("INDI")))
    		    {
    				String ret;
    				ret = indivContainer.addIndiv(lineItems[1]);
    				System.out.println("Id   -" + ret); 
    		    }
    			else if ((lineItems[0].equals("0")) && (tag.equals("FAM")))
    			{
    				String ret;
    				ret = familyContainer.addFam(lineItems[1]);
    				System.out.println("Id   -" + ret); 
    		    }
    		}
            bufReader.close();
            
            for (int num=0; num < indivContainer.getSize(); num++)
            {
            	System.out.println(indivContainer.getIndiv(num));
            }
            
            for (int num=0; num < familyContainer.getSize(); num++)
            {
            	System.out.println(familyContainer.getFam(num));
            }
        }
        catch (Exception e)
        {
        	System.err.format("Exception occurred reading file");
        }

    	// Close the file
        try
        {
        	fileReader.close();
        }
        catch (Exception e)
        {
          System.err.format("Exception occurred closing file '%s'.", gedcomfile);
        }
    }
}
