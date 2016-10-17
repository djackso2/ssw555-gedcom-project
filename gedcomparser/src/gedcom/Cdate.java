
package gedcom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
 

public class Cdate {
	
	private String sDate = "None";
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
		
	
	// Date String can have the following formats:
	//    [d]d MMM yyyy  (25 DEC 1944 or 1 JAN 1950)
	//    MMM yyyy       (DEC 1944)
	//    yyyy           (1944)
	public void set(String date)
	{
		int pos = 0;
		String del = " ";
		String sDay = "1", sMonth = "JAN", sYear;
		String s1 = "", s2 = "", s3 = "";
		int day = 0, month = 0, year = 0;
		int cnt;

		sDate = date;
		initMonthList();
		
		// Parse the string as 1, 2 or 3 parts
		if (((pos = date.indexOf(del)) == -1))
		{
			s1 = date;
			cnt = 1;
		}
		else
		{
			s1 = date.substring(0, pos);
			date = date.substring(pos + del.length());
			s2 = date;
			cnt = 2;
		}
		if (((pos = date.indexOf(del)) != -1))
		{
			s2 = date.substring(0, pos);	
			s3 = date.substring(pos + del.length());
			cnt = 3;
		
		}
		
		if (cnt == 1)
		{
			sYear = s1;
		}
		else if (cnt == 2)
		{
			sYear = s2;
			sMonth = s1;
		}
		else
		{
			sYear = s3;
			sMonth = s2;
			sDay = s1;
		}
		// Validate
		day =  Integer.parseInt(sDay);
		month = monthList.indexOf(sMonth);
		year = Integer.parseInt(sYear);
		
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

    // isBefore
    //   true - if this date is before date2
    public boolean isBefore(Cdate date2)
    {
       return cal.before(date2.getCal());
    }
    
    // isAfter
    //   true - if this date is after date2
    public boolean isAfter(Cdate date2)
    {
       return cal.after(date2.getCal());
    }
    
    // isWithin
    //   true - if this date and date2 are within yrs-mnths-days
    //   false - if they are not within yrs-mnths-days
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
    
    // anniversaryIsWithin
    //   true - if the yearly anniversary is within the number of days
    //   false - if they are not within yrs-mnths-days
    public boolean anniversaryIsWithin(Cdate today, int days)
    {
    	java.util.Calendar date;
    	java.util.Calendar event = GregorianCalendar.getInstance();
    	int day, month, year;
    
    	date = (Calendar) today.getCal().clone();
    
    	day = cal.get(Calendar.DATE);
		month = cal.get(Calendar.MONTH);
		year = date.get(Calendar.YEAR);
    	
    	event.set(year, month, day);
    	
    	if (event.before(date)) event.add(Calendar.YEAR, 1);
    	date.add(Calendar.DATE, days+1);
    	
    	return (event.before(date));
    }
	
    //********************************************************
	// added for US05
	//********************************************************
    public String getStringDate(){
		DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
    	return (dateFormat.format(getCal().getTime())).toUpperCase();
    }

}
