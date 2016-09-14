package gedcom;

//TBD change type to Family

public class FamilyContainer extends GedcomContainer<String> {
	
	public FamilyContainer()
	{
		
	}
	
	public String getFam(int indx)
	{
	   return( myList.get(indx));
	}
	
	public String addFam(String id)
	{
		int indx;
		String listId;
		
		for (indx = 0; indx < myList.size(); indx++)
		{
			listId = myList.get(indx);
			if ((listId.length() >= id.length()) &&
				(listId.compareTo(id) > 0))
			{
				break;
			}
		}
		myList.add(indx, id);
		return( myList.get(indx));
	}
}
