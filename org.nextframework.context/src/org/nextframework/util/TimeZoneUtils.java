package org.nextframework.util;

import java.util.Calendar;
import java.util.Date;

public class TimeZoneUtils {
	
	public Date fromZoneTimeToGMT(Date date){
		Calendar c = Calendar.getInstance();
		return fromZoneTimeToGMT(c, date);
	}
	
	public Date fromGMTToZoneTime(Date date) {
		Calendar c = Calendar.getInstance();
		return fromGMTToZoneTime(c, date);
	}
	
	public Date fromZoneTimeToGMT(Calendar c, Date date){
		if(date == null){
			return null;
		}
		return new Date(date.getTime() - c.getTimeZone().getOffset(date.getTime()));
	}
	
	public Date fromGMTToZoneTime(Calendar c, Date date) {
		if(date == null){
			return null;
		}
		return new Date(date.getTime() + c.getTimeZone().getOffset(date.getTime()));
	}

	public static void main(String[] args) {
		Calendar instance = Calendar.getInstance();
		instance.set(Calendar.HOUR_OF_DAY, 10);
		instance.set(Calendar.MINUTE, 0);
//		instance.set(Calendar.MONTH, 5);
		System.out.println(new TimeZoneUtils().fromGMTToZoneTime(instance.getTime()));
		System.out.println(new TimeZoneUtils().fromZoneTimeToGMT(instance.getTime()));
	}
}
