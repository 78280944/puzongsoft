package com.lottery.orm.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
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
	
	//01,本日;02,上周;03,本周;04,上期;05,本期;
   public static Date[] getDateTime(Date startTime,Date endTime) throws ParseException {
			Date[] sTime = new Date[2];
			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String imptimeBegin = sdf.format(startTime)+" 00:00:01"; 
			String imptimeEnd = sdf.format(endTime)+" 23:59:59";
			sTime[0] = sdf.parse(imptimeBegin);
			sTime[1] = sdf.parse(imptimeEnd);
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
	     String lastBeginDate = sdf.format(calendar1.getTime())+" 00:00:01";  
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
	     String imptimeBegin = sdf.format(cal.getTime())+" 00:00:01";  
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
	     String imptimeBegin = sdf.format(cal.getTime())+" 00:00:01";  
	     // System.out.println("所在周星期一的日期：" + imptimeBegin);  
	    
	     String imptimeEnd = sdf.format(cal.getTime())+ " 23:59:59";  
	     // System.out.println("所在周星期日的日期：" + imptimeEnd);  
	     return imptimeBegin + "," + imptimeEnd;  
	}
	
	public static String getCurrentMills(){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	    String sTime = sdf.format(date);
	    return sTime;
	}
	
	public static String getArrayString(String str){
		String res = "";
		for (int i = 0; i < str.length(); i++) {
		    res += ("," + str.charAt(i));
		}
		return res.substring(1, res.length());
	}
	
	public static Date getStringToDate(String dateString){
		 Date date = new Date();
		 dateString = dateString.replaceAll("/", "-");
	    try  
	    {  
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
	        date = sdf.parse(dateString);  
	    }  
	    catch (ParseException e)  
	    {  
	        System.out.println(e.getMessage());  
	    }  
	    return date;
	}
	
	public static Date getStringToMillon(String dateString,Integer seconds){
		 Date date = new Date();
		 dateString = dateString.replaceAll("/", "-");
	    try  
	    {  
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
	        date = sdf.parse(dateString);  
	        long times = date.getTime() - seconds*1000;
	        date = new Date(times);
	        //dateStr = sdf.format(date);
	    }  
	    catch (ParseException e)  
	    {  
	        System.out.println(e.getMessage());  
	    }  
	    return date;
	}
	
	public static String[] getStringResult(String[] results,String orders,String type){
		String[] ors = orders.split(",");
		String result = "";
		int count = 0;
		int mod = 0;
		for (int i =0;i<results.length;i++){
			for (int j=0;j<ors.length;j++){
				if (i==Integer.valueOf(ors[j])-1){
					System.out.println("..."+i+"..."+j+".."+results[i]);
					result = result + results[i]+",";
					count = count + Integer.valueOf(results[i]);
				
					break;
				}
			}
		}
	    String [] value = new String[8];
	    String [] strs = new String[3];
	    String [] order = new String[3];
	    value[0] = result.substring(0,result.length()-1);
	    value[1] = String.valueOf(count);
		mod = count%10;
		//value[2]=String.valueOf(mod);
		order = getOrdeNum(value[0],type);
		value[2] = order[0];
		value[3] = order[1];
		value[4] = order[2];
		strs = getStringResultNo(value[0],mod,type);
		value[5] = strs[0];
		value[6] = strs[1];
		value[7] = strs[2];
	    return value;
	}
	
	public static String[] getOrdeNum(String result,String type){
		String[] str = result.split(",");
		String[] stemp = new String[3];

		String t ="";
		if (type.equals("01")&&str.length==3){
			stemp[0] = str[0];
			stemp[1] = str[1];
			stemp[2] = str[2];
			if(Integer.valueOf(stemp[0])<Integer.valueOf(stemp[1])){
				t=stemp[0];
				stemp[0]=stemp[1];
				stemp[1]=t;
			}//取得a b 大的
			if(Integer.valueOf(stemp[0])<Integer.valueOf(stemp[2])){
				t=stemp[0];
				stemp[0]=stemp[2];
				stemp[2]=t;
		    }
			if(Integer.valueOf(stemp[1])<Integer.valueOf(stemp[2])){
				t=stemp[1];
				stemp[1]=stemp[2];
				stemp[2]=t;
			}
		} else if (type.equals("02")&&str.length==2){
			stemp[0] = str[0];
			stemp[1] = str[1];
			stemp[2] = null;
			if(Integer.valueOf(stemp[0])<Integer.valueOf(stemp[1])){
				t = stemp[0];
				stemp[0] = stemp[1];
				stemp[1] = t;
			}
		
		}
		return stemp;
	}
	
	public static String[] getStringResultNo(String result,int mod,String type){
		String[] str = new String[3];
		if (type.equals("01")){
		    String[] re = result.split(",");
			if (re[0].equals(re[1])&&re[0].equals(re[2])&&re[1].equals(re[2])){
			    str[0] = EnumType.LotteryResultNiu.Result_niuniu_bz.ID;
			    str[1] = EnumType.LotteryResultNiu.Result_niuniu_bz.NAME;
			    str[2] = String.valueOf(EnumType.LotteryResultNiu.Result_niuniu_bz.RATIO);
			    return str;
			}
			if (mod==0){
			    str[0] = EnumType.LotteryResultNiu.Result_niuniu_nn.ID;
			    str[1] = EnumType.LotteryResultNiu.Result_niuniu_nn.NAME;
			    str[2] = String.valueOf(EnumType.LotteryResultNiu.Result_niuniu_nn.RATIO);
			    return str;
			}
			if (Integer.valueOf(re[2])>Integer.valueOf(re[1])&&Integer.valueOf(re[1])>Integer.valueOf(re[0])){
				if ((Integer.valueOf(re[2])==Integer.valueOf(re[1])+1)&&(Integer.valueOf(re[1])==Integer.valueOf(re[0])+1)){
				    str[0] = EnumType.LotteryResultNiu.Result_niuniu_sz.ID;
				    str[1] = EnumType.LotteryResultNiu.Result_niuniu_sz.NAME;
				    str[2] = String.valueOf(EnumType.LotteryResultNiu.Result_niuniu_sz.RATIO);
			        return str;
				}
			}
			if (Integer.valueOf(re[0])>Integer.valueOf(re[1])&&Integer.valueOf(re[1])>Integer.valueOf(re[2])){
				if ((Integer.valueOf(re[0])==Integer.valueOf(re[1])+1)&&(Integer.valueOf(re[1])==Integer.valueOf(re[2])+1)){
				    str[0] = EnumType.LotteryResultNiu.Result_niuniu_ds.ID;
				    str[1] = EnumType.LotteryResultNiu.Result_niuniu_ds.NAME;
				    str[2] = String.valueOf(EnumType.LotteryResultNiu.Result_niuniu_ds.RATIO);
				    return str;
				}
			}
			System.out.println("0--"+re[0]+".."+re[1]+".."+re[2]);
			if (re[0].equals(re[1])||re[0].equals(re[2])||re[1].equals(re[2])){
			    str[0] = EnumType.LotteryResultNiu.Result_niuniu_dz.ID;
			    str[1] = EnumType.LotteryResultNiu.Result_niuniu_dz.NAME;
			    str[2] = String.valueOf(EnumType.LotteryResultNiu.Result_niuniu_dz.RATIO);
			    return str;
			}
			if (mod==1){
			    str[0] = EnumType.LotteryResultNiu.Result_niuniu_n1.ID;
			    str[1] = EnumType.LotteryResultNiu.Result_niuniu_n1.NAME;
			    str[2] = String.valueOf(EnumType.LotteryResultNiu.Result_niuniu_n1.RATIO);
			    return str;
			}
			if (mod==2){
			    str[0] = EnumType.LotteryResultNiu.Result_niuniu_n2.ID;
			    str[1] = EnumType.LotteryResultNiu.Result_niuniu_n2.NAME;
			    str[2] = String.valueOf(EnumType.LotteryResultNiu.Result_niuniu_n2.RATIO);
			    return str;
			}
			if (mod==3){
			    str[0] = EnumType.LotteryResultNiu.Result_niuniu_n3.ID;
			    str[1] = EnumType.LotteryResultNiu.Result_niuniu_n3.NAME;
			    str[2] = String.valueOf(EnumType.LotteryResultNiu.Result_niuniu_n3.RATIO);
			    return str;
			}
			if (mod==4){
			    str[0] = EnumType.LotteryResultNiu.Result_niuniu_n4.ID;
			    str[1] = EnumType.LotteryResultNiu.Result_niuniu_n4.NAME;
			    str[2] = String.valueOf(EnumType.LotteryResultNiu.Result_niuniu_n4.RATIO);
			    return str;
			}
			if (mod==5){
			    str[0] = EnumType.LotteryResultNiu.Result_niuniu_n5.ID;
			    str[1] = EnumType.LotteryResultNiu.Result_niuniu_n5.NAME;
			    str[2] = String.valueOf(EnumType.LotteryResultNiu.Result_niuniu_n5.RATIO);
			    return str;
			}
			if (mod==6){
			    str[0] = EnumType.LotteryResultNiu.Result_niuniu_n6.ID;
			    str[1] = EnumType.LotteryResultNiu.Result_niuniu_n6.NAME;
			    str[2] = String.valueOf(EnumType.LotteryResultNiu.Result_niuniu_n6.RATIO);
			    return str;
			}
			if (mod==7){
			    str[0] = EnumType.LotteryResultNiu.Result_niuniu_n7.ID;
			    str[1] = EnumType.LotteryResultNiu.Result_niuniu_n7.NAME;
			    str[2] = String.valueOf(EnumType.LotteryResultNiu.Result_niuniu_n7.RATIO);
			    return str;
			}
			if (mod==8){
			    str[0] = EnumType.LotteryResultNiu.Result_niuniu_n8.ID;
			    str[1] = EnumType.LotteryResultNiu.Result_niuniu_n8.NAME;
			    str[2] = String.valueOf(EnumType.LotteryResultNiu.Result_niuniu_n8.RATIO);
			    return str;
			}
			if (mod==9){
			    str[0] = EnumType.LotteryResultNiu.Result_niuniu_n9.ID;
			    str[1] = EnumType.LotteryResultNiu.Result_niuniu_n9.NAME;
			    str[2] = String.valueOf(EnumType.LotteryResultNiu.Result_niuniu_n9.RATIO);
			    return str;
			}
		}else if (type.equals("02")){
			str[0] = null;
			str[1] = null;
			str[2] = "1";
		}
        return str;
	}
	
	 public static int RamdomNum(){    
	        Date date = new Date();    
	        long timeMill = date.getTime();    
	        System.out.println(timeMill);    
	        Random rand = new Random(timeMill);    
	       /*
	        for(int i = 0; i < 20; i++)    
	        {    
	            System.out.println(rand.nextInt(50));    
	        }
	        */
	        return rand.nextInt(50);
	    }    
	 
	   /**
      * 随机产生几位字符串：例：maxLength=3,则结果可能是 aAz
      * @param maxLength 传入数必须是正数。
      */
     public static String produceString(int maxLength){
             String source = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
             return doProduce(maxLength, source);
     }
	
     /**
      * 生产结果
      */
     private static String doProduce(int maxLength, String source) {
             StringBuffer sb = new StringBuffer(100);
             for (int i = 0; i < maxLength; i++) {
                     final int number =  produceNumber(source.length());
                     sb.append(source.charAt(number));
             }
             return sb.toString();
     }
     
     /**
      * 随机产生几位数字：例：maxLength=3,则结果可能是 012
      */
     public static final int produceNumber(int maxLength){
             Random random = new Random();
             return random.nextInt(maxLength);
     }
     
    	 
     /**
      * 判断给定时间在否在给定两个时间之前
      */
     public static boolean dateRange(){
    	 Calendar cal = Calendar.getInstance();// 当前日期
    	 int hour = cal.get(Calendar.HOUR_OF_DAY);// 获取小时
    	 int minute = cal.get(Calendar.MINUTE);// 获取分钟
    	
    	 if (hour >= 22 || hour < 2)
    	 {
    	   // 晚上22点（含）到凌晨2点（不含）之间
    		 return true;
    	 }
    	 else
    	 {
    		 return false;
    	   // 上述之外的时间段
    	 }
     }
     
     /**
      * 判断比较
      */
     public static String[][] doHandle(String[][] str){
    	 String[][] d = new String[2][5];
    	 d[0][0] = "";
    	 d[0][1] = "";
    	 d[0][2] = "";
    	 d[0][3]="";
    	 d[0][4] = "";
         str[0][0]="4000";
         str[0][1]="4000";
         str[0][2]="4000";
         str[0][3]="1019";
         str[0][4]="1001";
         str[0][5]="0";
         str[0][6]="5";
         str[1][0]="5000";
         str[1][1]="5000";
         str[1][2]="5000";
         str[1][3]="1020";
         str[1][4]="1001";
         str[1][5]="0";
         str[1][6]="4";
         str[2][0]="8000";
         str[2][1]="8000";
         str[2][2]="8000";
         str[2][3]="1011";
         str[2][4]="1001";
         str[2][5]="0";
         str[2][6]="2";
         String[][] str1 = str;
         int i = 0;
         int j=str.length-1;
         System.out.println("7---"+j);
         int tmp = Integer.valueOf(str[0][1]);
         while (i<j){
        	 if (tmp>=Integer.valueOf(str[j][1])){
        		 tmp = tmp - Integer.valueOf(str[j][1]);
        		 str[j][1] = "0";
        		 str[j][5] = "1";
        		 j--;
        		 if(i==j){
        			 str[i][1] = String.valueOf(Integer.valueOf(str[i][1]) - tmp);
        		 }
        			System.out.println("98---"+tmp);
        	 }else{
        		 str[j][1] = String.valueOf((Integer.valueOf(str[j][1]) - tmp));
        		 str[j][5] = "1";
        		 i++;
        		 tmp = Integer.valueOf(str[i][1]);
        		//System.out.println("9---"+tmp);
        	 }
         }
         for (i=0;i<=str.length-1;i++){
         	System.out.println("1----"+i+".."+str[i][1]);
         }
         for (i=str.length-1;i>=j;i--){
        	 if(str[i][5].equals("1")){
        		 str[i][1] = String.valueOf(Integer.valueOf(str[i][1])  - Integer.valueOf(str[i][0]));
        		 // System.out.println("0----0-"+i+".."+str1[i][1]);
        	 }
         }
        for (i=0;i<=str1.length-1;i++){
        	System.out.println("0----"+i+".."+str[i][1]);
        }
         
         return str;
         
     }
     
     /**
      * 判断比较,无庄逻辑处理
      */
     public static String[][] doNoBankerHandle(String[][] str){
    	/*
         str[0][0]="1200";
         str[0][1]="1200";
         str[0][2]="1200";
         str[0][3]="1019";
         str[0][4]="1001";
         str[0][5]="0";
         str[0][6]="5";
         str[0][7]="0";
         str[0][8]="牛牛";
         str[1][0]="2000";
         str[1][1]="2000";
         str[1][2]="2000";
         str[1][3]="1020";
         str[1][4]="1001";
         str[1][5]="0";
         str[1][6]="5";
         str[1][7]="0";
         str[1][8]="牛牛";
         str[2][0]="2000";
         str[2][1]="2000";
         str[2][2]="2000";
         str[2][3]="1011";
         str[2][4]="1001";
         str[2][5]="0";
         str[2][6]="1";
         str[2][7]="0";
         str[2][8]="牛牛";
         str[3][0]="700";
         str[3][1]="700";
         str[3][2]="700";
         str[3][3]="1011";
         str[3][4]="1001";
         str[3][5]="0";
         str[3][6]="1";
         str[3][7]="0";
         str[3][8]="牛牛";
         */
         int i = 0;
         int j=str.length-1;
        // System.out.println("7---"+j);
         str[0][1] = String.valueOf(Integer.valueOf(str[0][0])*Integer.valueOf(str[0][6]));
         str[j][1] = String.valueOf(Integer.valueOf(str[j][0])*Integer.valueOf(str[0][6]));
         str[j][6] = str[0][6];
         str[j][7] = String.valueOf(1);
         int tmp = Integer.valueOf(str[0][1]);
         //System.out.println("7ddd---"+i+".."+j);
         while (i<j){
        	 if (tmp>=Integer.valueOf(str[j][1])){
        		 tmp = tmp - Integer.valueOf(str[j][1]);
        		 str[j][1] = "0";
        		 str[j][5] = "1";
        		 if (Integer.valueOf(str[j][7]) == 0){
        			 str[j][6] = str[i][6];
        			 str[j][7] = String.valueOf(1);
        		 }
        		 j--;
        		 if(i==j){
        			 str[i][1] = String.valueOf(Integer.valueOf(str[i][0])*Integer.valueOf(str[i][6]) - tmp);
        			 str[i][2] = str[i][1];
        		 }else{
        			 str[j][1] = String.valueOf(Integer.valueOf(str[j][0])*Integer.valueOf(str[i][6]));
        		 }
        	 }else{
        		 str[j][1] = String.valueOf((Integer.valueOf(str[j][1]) - tmp));
        		 str[j][5] = "1";
        		 if (Integer.valueOf(str[j][7]) == 0){
        			 str[j][6] = str[i][6];
        			 str[j][7] = String.valueOf(1);
        		 }
        		 i++;
        		 if(i < j){
	        		 str[i][1] = String.valueOf(Integer.valueOf(str[i][0])*Integer.valueOf(str[i][6])); 		
	        		 tmp = Integer.valueOf(str[i][1]);
        		 }
        		//System.out.println("9---"+tmp);
        	 }
         }
        // System.out.println("7ddd-sdsd--"+i+".."+j);
         for (i=str.length-1;i>=j;i--){
        	 if(str[i][5].equals("1")){
        		 str[i][2] = String.valueOf(Integer.valueOf(str[i][1])  - Integer.valueOf(str[i][0])*Integer.valueOf(str[i][6]));
        		  //System.out.println("0----0-"+i+".."+str1[i][1]);
        	 }
         }
        for (i=0;i<j;i++){
        	 str[i][2] =  str[i][1] ;
        	//System.out.println("0----"+i+".."+str[i][1]);
        }

         return str;
         
     }
     
     
     /**
      * 庄判断比较,输庄
      */
     public static String[][] doBankerHandle1(String[][] str){
    	 String[][] d = new String[2][5];
    	 d[0][0] = "";
    	 d[0][1] = "";
    	 d[0][2] = "";
    	 d[0][3]="";
    	 d[0][4] = "";
    	 int count = 25000;
    	 int times = 5;
    	 str[0][0]="100";
         str[0][1]="100";
         str[0][2]="100";
         str[0][3]="1019";
         str[0][4]="1001";
         str[0][5]="0";
         str[0][6]="5";
         str[0][7]="0";
         str[0][8]="牛牛";
         str[1][0]="200";
         str[1][1]="200";
         str[1][2]="200";
         str[1][3]="1020";
         str[1][4]="1001";
         str[1][5]="0";
         str[1][6]="5";
         str[1][7]="0";
         str[1][8]="牛牛";
         str[2][0]="5000";
         str[2][1]="5000";
         str[2][2]="5000";
         str[2][3]="1011";
         str[2][4]="1001";
         str[2][5]="0";
         str[2][6]="1";
         str[2][7]="0";
         String[][] str1 = str;
         int i = 0;
         int j=str.length-1;
         System.out.println("7---"+j);
         int base = count;
         for (;j>=0;j--){
        	 //System.out.println("90---"+str[j][1]);
        	 if (Integer.valueOf(str[j][1])*times<base){
        		 base = base - Integer.valueOf(str[j][0])*times;
        		 str[j][1] = "0";
        	 }else{
        		 str[j][1] = String.valueOf((Integer.valueOf(str[j][0])*times - base));
                 break;
        	 }
         }
         for (i=0;i<=str.length-1;i++){
         	if (i<j)
         		str[i][2] = String.valueOf(0);
         	else{
         		str[i][2] = String.valueOf(Integer.valueOf(str[i][1]) - Integer.valueOf(str[i][0])*times);
         	    count = count - Integer.valueOf(str[i][2]);
         	}
         }
  
         /*
        for (i=0;i<=str.length-1;i++){
        	System.out.println("0----"+i+".."+str[i][2]+"..");
        }
        */
        System.out.println("0----"+count);
         
         return str;
         
     }
     
     
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception{
		//testDate();
		System.out.println(",---------"+Integer.parseInt("100.00".replaceAll("\\.00", "")));
		String[][] d =new String[3][9];
		System.out.println(doBankerHandle1(d));
		String[] a = "2,9,2,2,7".split(",");
		String[] c =CommonUtils.getOrdeNum("2,10","02");
		//System.out.println("7----"+c[0]+".."+c[1]+".."+c[2]+"..."+System.currentTimeMillis());
		//public static String[] getStringResultNo(String result,int mod,String type){
		String[] b = CommonUtils.getStringResultNo("5,9,5", 9,"01");
		int t = 9%10;
		System.out.println(".."+b[0]+"..."+b[1]+".."+b[2]+"..");
		String m = (CommonUtils.getCurrentMills());
		System.out.println(".."+CommonUtils.getStringToMillon("2017-10-21 22:40:45",470));
		Date[] data = CommonUtils.getDateBetween(new Date(),new Date(),"03");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(data[0]+".."+data[1]+"..."+sdf.format(data[0])+".."+sdf.format(data[1]));
	}
}
