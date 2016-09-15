package gedcom;
import java.util.LinkedList;
import java.util.List;


//Eileen Roberson
//SSW555
//14Sept16


//container for an individual
public class Cindiv {
	
	private String id;
	private String name;
	private String gender;
	private Cdate birth;
	private Boolean alive;
	private Cdate death;
	private String famc;  //family id?
	private List<String> fams;

	
	
	
	// individual not married
	public Cindiv (String indivId, String indivName, String indivGender, Cdate indivBirth, Boolean indivAlive, Cdate indivDeath, String indivFamc, List<String> iFams) {
	
	indivId = id;
	indivName = name;
	indivGender = gender;
	indivBirth = birth;
	indivAlive = alive;
	indivDeath = death;
	indivFamc = famc;
	List<String> indivFams = fams;
	

	}

	public String getIndivId (){
		String indivId;
		return indivId;
	}
	

	
	public void printIndividual(){
	     System.out.println("Individual's ID" + indivId);
	     System.out.println("Name" + indivName);
	     System.out.printl"Gender" + indivGender);
	     System.out.println("Birth Date" + indivBirth);
	     System.out.println("is this individual alive?" + indivAlive)
	     if (indivAlive  == 0){
     	     System.out.println("Date of Death" + indivDeath);
	     }
	     System.out.println("Family" + indivFamc);
	}
	
	
	
	// Set and Access methods including Addfams()
	
