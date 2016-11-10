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
	
	public Cindiv addIndiv(String id, int lineNum)
	{
		int indx;
		Cindiv indiv;
		String listId;
		
		for (indx = 0; indx < myList.size(); indx++)
		{
			indiv = myList.get(indx);
			listId = indiv.getId();
			if ((listId.length() >= id.length()) &&
				(listId.compareTo(id) > 0))
			{
				break;
			}
		}
		indiv = new Cindiv(id, lineNum);
		myList.add(indx, indiv);
		return( myList.get(indx));
	}
	
	// Returns Cindiv object with passed in String id else returns null
	public Cindiv findIndiv(String id){
		for(int i=0; i< myList.size(); i++){
			if (myList.get(i).getId().equals(id)){
				return myList.get(i);
			}
		}
		return null;
	}
}
