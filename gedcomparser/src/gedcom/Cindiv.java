package gedcom;
import java.util.LinkedList;


//Eileen Roberson
//SSW555
//14Sept16


//container for an individual
public class Cindiv {
	
	private String id;
	private String name;
	private String gender;
	private String dateBirth;
	private Boolean isAlive;
	private String dateDeath;
	private String famc;  
	private LinkedList<String> fams;
	
	public Cindiv(String i){
		id = i;
		name = "";
		gender = "";
		dateBirth = "";
		isAlive = true;
		dateDeath = "";
		famc = "";
		fams = new LinkedList<String>();
	}
	public String getId(){
		return id;
	}	
	public String getName(){
		return name;
	}	
	public void setName(String n){
		name = n;		
	}	
	public String getGender(){
		return gender;
	}	
	public void setGender(String g){
		gender = g;
	}	
	public String getDateBirth(){
		return dateBirth;
	}	
	public void setDateBirth(String b){
		dateBirth = b;
	}	
	public String getDateDeath(){
		return dateDeath;
	}
	public void setDateDeath(String d){
		dateDeath = d;
	}	
	public boolean getIsAlive(){
		return isAlive;
	}	
	public void setIsAlive(boolean i){
		isAlive = i;
	}	
	public LinkedList<String> getFamS(){
		return fams;
	}
	public void addToFamS(String ID){
		fams.add(id);
	}
	public void setFamS(LinkedList<String> f){
		fams = f;
	}
	public String getFamC(){
		return famc;
	}
	public void setFamC(String f){
		famc = f;
	}
	
}
