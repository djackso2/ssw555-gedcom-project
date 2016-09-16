
package gedcom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
 

public class Cdate {
	
	private String sDate;
	java.util.Calendar cal = GregorianCalendar.getInstance();
	private static List<String>monthList = new ArrayList<String>();

	private static void initMonthList()
	{
		monthList.add("JAN");
		monthList.add("FEB");
		monthList.add("MAR");
		monthList.add("APR");
		monthList.add("MAY");
		monthList.add("JUN");
		monthList.add("JUL");
		monthList.add("AUG");
		monthList.add("SEP");
		monthList.add("OCT");
		monthList.add("NOV");
		monthList.add("DEC");
	}
		
	
	// Will be adding other compare methods
	public void set(String date)
	{
		int pos = 0;
		String del = " ";
		String sDay, sMonth, sYear;
		int day = 0, month = 0, year = 0;

		sDate = date;
		initMonthList();
		
		if (((pos = date.indexOf(del)) != -1))
		{
			sDay = date.substring(0, pos);
			date = date.substring(pos + del.length());
			day =  Integer.parseInt(sDay);
		}
		if (((pos = date.indexOf(del)) != -1))
		{
			sMonth = date.substring(0, pos);
			sYear = date.substring(pos + del.length());
			month = monthList.indexOf(sMonth);
			year = Integer.parseInt(sYear);
		}
		
		cal.set(year, month, day);
	}
	
	public Calendar getCal()
	{
		return cal;
	}
	
    public String get()
    {
    	return sDate;
    }

    public boolean isBefore(Cdate date2)
    {
       return cal.before(date2.getCal());
    }
    
    public boolean isAfter(Cdate date2)
    {
       return cal.after(date2.getCal());
    }
    
    public boolean isWithin(Cdate date2, int yrs, int mnths, int days)
    {
    	java.util.Calendar  tmp1, tmp2;
    	if (cal.before(date2.getCal()))
    	{
    		tmp1 = (Calendar) cal.clone();
    		tmp2 = (Calendar) date2.getCal().clone();
    	}
    	else
    	{
    		tmp2 = (Calendar) cal.clone();
    		tmp1 = (Calendar) date2.getCal().clone();
    	}
    	tmp1.add(Calendar.YEAR, yrs);
    	tmp1.add(Calendar.MONTH, mnths);
    	tmp1.add(Calendar.DATE, days);
    	
    	return tmp1.after(tmp2);
    }
}
