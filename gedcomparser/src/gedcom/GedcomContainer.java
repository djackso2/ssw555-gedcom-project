package gedcom;

import java.util.LinkedList;
import java.util.List;

public class GedcomContainer<E> {
	protected  List<E> myList; 
	
	public GedcomContainer()
	{
		myList = new LinkedList<E>();
	}
	
	public int getSize()
	{
		return myList.size();
	}
	
	public Object getItem(int indx)
	{
	   return( myList.get(indx));
	}
	
	public Object addItem(int indx, E id)
	{
		myList.add(indx, id);
		return( myList.get(indx));
	}
}
