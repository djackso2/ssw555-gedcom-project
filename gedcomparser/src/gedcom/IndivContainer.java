package gedcom;


//TBD change type to indiv

public class IndivContainer extends GedcomContainer<Cindiv> {
	
	public IndivContainer()
	{
		
	}
	
	public Cindiv getIndiv(int indx)
	{
	   return( myList.get(indx));
	}
	
	public Cindiv addIndiv(String id)
	{
		int indx;
		Cindiv indiv;
		String listId;
		
		for (indx = 0; indx < myList.size(); indx++)
		{
			indiv = myList.get(indx);
			listId = indiv.getIndivID();
			if ((listId.length() >= id.length()) &&
				(listId.compareTo(id) > 0))
			{
				break;
			}
		}
		indiv = new Cindiv(id);
		myList.add(indx, indiv);
		return( myList.get(indx));
	}
}
