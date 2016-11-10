package gedcom;

//TBD change type to Family

public class FamilyContainer extends GedcomContainer<CFamily> {
	
	public FamilyContainer()
	{
		
	}
	
	public CFamily getFam(int indx)
	{
	   return( myList.get(indx));
	}
	
	public CFamily findFam(String id){
		for (int i = 0; i <  myList.size(); i++){
			if (myList.get(i).getFamID().equals(id)){
				return myList.get(i);
			}
		}
		return null;
	}
	
	public CFamily addFam(String id, int lineNum)
	{
		int indx;
		CFamily fam;
		String listId;
		
		for (indx = 0; indx < myList.size(); indx++)
		{
			fam = myList.get(indx);
			listId = fam.getFamID();
			if ((listId.length() >= id.length()) &&
				(listId.compareTo(id) > 0))
			{
				break;
			}
		}
		fam = new CFamily(id, lineNum);
		myList.add(indx, fam);
		return( myList.get(indx));
	}
}
