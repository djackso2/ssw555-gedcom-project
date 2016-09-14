package gedcom;


//TBD change type to indiv

public class IndivContainer extends GedcomContainer<String> {
	
	public IndivContainer()
	{
		
	}
	
	public String getIndiv(int indx)
	{
	   return( myList.get(indx));
	}
	
	public String addIndiv(String id)
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
