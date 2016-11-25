package com.catchmodel.initdata;

import com.catchmodel.been.CatchBeen;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;


public class CatchBeenComparator implements Comparator<CatchBeen> {

	@Override
	public int compare(CatchBeen lhs, CatchBeen rhs) {
		// TODO Auto-generated method stub
		Date a = dateToString(lhs.getStoretime());
		Date b = dateToString(rhs.getStoretime());
		if (b.after(a)){
				return 1;
			}else{
				return 0;
			}
	}
	private Date dateToString(String str) {	 	
		 SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd-HH:mm");
		   Date date = null;
		    try {
				date = format.parse(str);
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  
		   return date;
	    }
}
