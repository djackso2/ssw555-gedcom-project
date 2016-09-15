package gedcom;

import java.util.Iterator;
import java.util.LinkedList;

//Dan Jackson
//SSW555 
//14Sep16

//Container class for a single traditional family
public class CFamily{
	
	private String famID;
	private String wifeID;
	private String husbandID;
	private LinkedList<String> childrenIDs;
	private String dateMarried; //String for now, can be changed
	private String dateDivorced; //String for now, can be changed
	
	public CFamily(String fID) {
		famID = fID;
	}
	
	//General info getters and setters
	public String getFamID(){
		return famID;
	}		
	public String getDateMarried(){
		return dateMarried;
	}		
	public String getDateDivorced(){
		return dateDivorced;
	}	
	public void setDateDivorced(String dVorced){
		dateDivorced = dVorced;
	}	
	public String getWifeID(){
		return wifeID;
	}	
	public String getHusbandID(){
		return husbandID;
	}
	public void setHusbandID(String husb){
		husbandID = husb;
	}
	public void setWifeID(String w){
		wifeID = w;
	}
	
	//Adding and accessing children	
	public void addChild(String kid){
		childrenIDs.add(kid);
	}
	public LinkedList<String> getChildren(){
		return childrenIDs;
	}	
}