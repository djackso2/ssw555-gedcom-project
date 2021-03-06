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
	private Cdate dateBirth;
	private Boolean isAlive;
	private Cdate dateDeath;
	private String famc;  
	private LinkedList<String> fams;
	private int lineNumber;
	
	public Cindiv(String i, int lineNum){
		id = i;
		name = "";
		gender = "None";
		dateBirth = new Cdate();
		isAlive = true;
		dateDeath = new Cdate();
		famc = "None";
		fams = new LinkedList<String>();
		lineNumber = lineNum;
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
	public Cdate getDateBirth(){
		return dateBirth;
	}	
	public void setDateBirth(String b){
		dateBirth.set(b);
	}	
	public Cdate getDateDeath(){
		return dateDeath;
	}
	public void setDateDeath(String d){
		isAlive = false;
		dateDeath.set(d);
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
	public void addToFamS(String f){
		fams.add(f);
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
	public Boolean isDeathBeforeBirth(){
		Boolean retVal = false;
		if (!isAlive) retVal = dateDeath.isBefore(dateBirth);
		return retVal;
	}
	public int getLineNumber(){
		return lineNumber;
	}
	
}
