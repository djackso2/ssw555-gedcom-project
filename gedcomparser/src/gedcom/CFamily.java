package gedcom;
import java.util.Iterator;
import java.util.LinkedList;

//Dan Jackson
//SSW555 
//14Sep16

//Container class for a single traditional family
public class CFamily{
	
	private String famID;
	private CIndiv wife;
	private CIndiv husband;
	private LinkedList<CIndiv> children;
	private String dateMarried; //String for now, can be changed
	private String dateDivorced; //String for now, can be changed
	
	
	//Constructor for family with no children
	public CFamily(String fID, CIndiv w, CIndiv h, String dMarried, String dVorced){
		famID = fID;
		wife= w;
		husband = h;
		dateMarried = dMarried;
		dateDivorced = dVorced;
		children = new LinkedList<CIndiv>();
	}
	
	//Constructor for family with children
	public CFamily(String fID, CIndiv w, CIndiv h, String dMarried, String dVorced, LinkedList<CIndiv> kids){
		famID = fID;
		wife= w;
		husband = h;
		dateMarried = dMarried;
		dateDivorced = dVorced;
		children = kids;
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
	public CIndiv getWife(){
		return wife;
	}	
	public CIndiv getHusband(){
		return husband;
	}
	public void setHusb(CIndiv husb){
		husband = husb;
	}
	public void setWife(CIndiv w){
		wife = w;
	}
	
	//Adding and accessing children	
	public void addChild(CIndiv kid){
		children.add(kid);
	}
	public LinkedList<CIndiv> getChildren(){
		return children;
	}
	
	//Print methods
	public void printHusbAndWife(){
		System.out.print("Husband: ");
		husband.printIndivIDAndName();
		System.out.print("Wife: ");
		husband.printIndivIDAndName();
	}	
	public void printKids(){
		Iterator<CIndiv> listIt = children.iterator();
		System.out.println("Children:");
		while(listIt.hasNext()){
			listIt.next().printIndivIDAndName();
		}
	}
	
}