
package gedcom;

import java.util.List;

public class Cindiv {
	
	private String Id;
	private String Name;
	private String Sex;
	private Cdate birth;
	private Boolean alive;
	private Cdate death;
	private String famc;  //family id?
	private List<String> fams;
	
	public Cindiv(String iID) {
		Id = iID;
	}
	// Set and Access methods including Addfams()
	public String getIndivID(){
		return Id;
	}
	
	// Print format methods?


}
