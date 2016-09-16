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
	public void setDateMarried(String m){
		dateMarried = m;
	}		
	public String getDateDivorced(){
		return dateDivorced;
	}	
	public void setDateDivorced(String d){
		dateDivorced = d;
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
	//Returns the ID of the child at index i
	public String getChildID(int i){
		return childrenIDs.get(i);
	}
	public LinkedList<String> getChildren(){
		return childrenIDs;
	}	

}