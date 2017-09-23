package com.lottery.orm.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class CommonUtils {

	//01,本日;02,上周;03,本周;04,上期;05,本期;
	public static Date[] getDateBetween(Date startTime,Date endTime,String time) throws ParseException {
		Date[] sTime = new Date[2];
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (time.equals("01")){
			String str = getCurrentTime();
			sTime[0] = sdf.parse(str.split(",")[0]);
			sTime[1] = sdf.parse(str.split(",")[1]);
		}else if (time.equals("02")){
			String str = getLastTimeInterval();
			sTime[0] = sdf.parse(str.split(",")[0]);
			sTime[1] = sdf.parse(str.split(",")[1]);
		}else if (time.equals("03")){
			String str = getTimeInterval();
			sTime[0] = sdf.parse(str.split(",")[0]);
			sTime[1] = sdf.parse(str.split(",")[1]);
		}
		
		return sTime;
	}
	
	public static String getMonday(){
		String monday = "";
		Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.add(Calendar.DATE, -1*7);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		monday = new SimpleDateFormat("yyyy-mm-dd").format(cal.getTime());
		return monday+"00:00:00";
	}
	
	public static String getSunday(){
		String sunday = "";
		Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.add(Calendar.DATE, -1*7);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		sunday = new SimpleDateFormat("yyyy-mm-dd").format(cal.getTime());
		return sunday+"00:00:00";
	}
	
	 /** 
	* 根据当前日期获得上周的日期区间（上周周一和周日日期） 
	*  
	* @return 
	* @author zhaoxuepu 
	*/  
	public static String getLastTimeInterval() {  
	     Calendar calendar1 = Calendar.getInstance();  
	     Calendar calendar2 = Calendar.getInstance();  
	     int dayOfWeek = calendar1.get(Calendar.DAY_OF_WEEK) - 1;  
	     int offset1 = 1 - dayOfWeek;  
	     int offset2 = 7 - dayOfWeek;  
	     calendar1.add(Calendar.DATE, offset1 - 7);  
	     calendar2.add(Calendar.DATE, offset2 - 7);  
	     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	     // System.out.println(sdf.format(calendar1.getTime()));// last Monday  
	     String lastBeginDate = sdf.format(calendar1.getTime())+" 00:00:00";  
	     // System.out.println(sdf.format(calendar2.getTime()));// last Sunday  
	     String lastEndDate = sdf.format(calendar2.getTime())+" 23:59:59"; 
	     return lastBeginDate + "," + lastEndDate;  
	}  
	
	
	 /** 
	* 根据当前日期获得所在周的日期区间（周一和周日日期） 
	*  
	* @return 
	* @author zhaoxuepu 
	* @throws ParseException 
	*/  
	public static String getTimeInterval() {  
		 Date date = new Date();
	     Calendar cal = Calendar.getInstance();  
	     cal.setTime(date);  
	     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	     // 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了  
	     int dayWeek = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天  
	     if (1 == dayWeek) {  
	        cal.add(Calendar.DAY_OF_MONTH, -1);  
	     }  
	     // System.out.println("要计算日期为:" + sdf.format(cal.getTime())); // 输出要计算日期  
	     // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一  
	     cal.setFirstDayOfWeek(Calendar.MONDAY);  
	     // 获得当前日期是一个星期的第几天  
	     int day = cal.get(Calendar.DAY_OF_WEEK);  
	     // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值  
	     cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);  
	     String imptimeBegin = sdf.format(cal.getTime())+" 00:00:00";  
	     // System.out.println("所在周星期一的日期：" + imptimeBegin);  
	     cal.add(Calendar.DATE, 6);  
	     String imptimeEnd = sdf.format(cal.getTime())+" 23:59:59";
	     // System.out.println("所在周星期日的日期：" + imptimeEnd);  
	     return imptimeBegin + "," + imptimeEnd;  
	}  
	
	public static String getCurrentTime() {  
		 Date date = new Date();
	     Calendar cal = Calendar.getInstance();  
	     cal.setTime(date);  
	     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	     // 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了  
	     String imptimeBegin = sdf.format(cal.getTime())+" 00:00:00";  
	     // System.out.println("所在周星期一的日期：" + imptimeBegin);  
	    
	     String imptimeEnd = sdf.format(cal.getTime())+ " 23:59:59";  
	     // System.out.println("所在周星期日的日期：" + imptimeEnd);  
	     return imptimeBegin + "," + imptimeEnd;  
	}
	
	public static void main(String args[]) throws Exception{
		Date[] data = CommonUtils.getDateBetween(new Date(),new Date(),"03");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(data[0]+".."+data[1]+"..."+sdf.format(data[0])+".."+sdf.format(data[1]));
	}
}
