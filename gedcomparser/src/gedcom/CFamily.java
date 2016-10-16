package gedcom;

import java.util.LinkedList;
import java.util.Set;

//Dan Jackson
//SSW555 
//14Sep16

//Container class for a single traditional family
public class CFamily{
	
	private String famID;
	private String wifeID;
	private String husbandID;
	private LinkedList<String> childrenIDs;
	private Cdate dateMarried; //String for now, can be changed
	private Cdate dateDivorced; //String for now, can be changed
	private Boolean isDivorced;
	
	public CFamily(String fID) {
		famID = fID;
		childrenIDs = new LinkedList<String>();
		wifeID = "None";
		husbandID = "None";
		dateMarried = new Cdate();
		dateDivorced = new Cdate();
		isDivorced = false;
	}
	
	//General info getters and setters
	public String getFamID(){
		return famID;
	}		
	public String getDateMarried(){
		return dateMarried.get();
	}
	public Cdate getMarriedDate(){
		return dateMarried;
	}
	public void setDateMarried(String m){
		dateMarried.set(m);
	}		
	public String getDateDivorced(){
		return dateDivorced.get();
	}	
	public void setDateDivorced(String d){
		isDivorced = true;
		dateDivorced.set(d);
	}	
	public Boolean isDivorced()
	{
	   return isDivorced;
	}

	public Cdate getDivorcedDate()
	{
	   return dateDivorced;
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
	public int getNumberOfChildren(){
		return childrenIDs.size();
	}	

}
