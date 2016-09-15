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
	
	
	//Constructor for family with no children
	public CFamily(String fID, String w, String h, String dMarried, String dVorced){
		famID = fID;
		wifeID = w;
		husbandID = h;
		dateMarried = dMarried;
		dateDivorced = dVorced;
		childrenIDs = new LinkedList<String>();
	}
	
	//Constructor for family with children
	public CFamily(String fID, String w, String h, String dMarried, String dVorced, LinkedList<String> kids){
		famID = fID;
		wifeID = w;
		husbandID = h;
		dateMarried = dMarried;
		dateDivorced = dVorced;
		childrenIDs = kids;
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
	
	
	//CIndiv access
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
	
	//Print methods
	//Since CFamily doesn't contain CIndivs, these are probably obsolete
	public void printHusbAndWife(){
		System.out.print("Husband ID: " + husbandID);
		System.out.print("Wife ID: " + wifeID);
	}	

	public void printKids(){
		Iterator<String> listIt = childrenIDs.iterator();
		while(listIt.hasNext()){
			System.out.println(listIt.next());
		}
	}
	
}