package com.lottery.orm.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lottery.orm.bo.LotteryGameDetail;
import com.lottery.orm.dao.LotteryGameDetailMapper;
import com.lottery.orm.dto.LotteryAmountDto;

@Service
@Transactional
public class LotteryService {
	
	@Autowired
	private LotteryGameDetailMapper lotteryGameDetailMapper;
	
	  /**
     * 判断比较,无庄逻辑处理
     */
    public synchronized String[][] doNoBankerHandle(String[][] str,String[][] str1){
   		
   /*
        str[0][0]="200";
        str[0][1]="0";
        str[0][2]="0";
        str[0][3]="1027";
        str[0][4]="1001";
        str[0][5]="0";  //对于赔付用户，是否需要显示负值
        str[0][6]="5";
        str[0][7]="0";  //对于赔付用户，倍率是否更新
        str[0][8]="2";
        str[0][9]="5";
        str[0][10]="5";
        str[1][0]="600";
        str[1][1]="0";
        str[1][2]="0";
        str[1][3]="1020";
        str[1][4]="1001";
        str[1][5]="0";
        str[1][6]="3";
        str[1][7]="0";
        str[1][8]="2";
        str[1][9]="4";
        str[1][10]="4";
        str[2][0]="300";
        str[2][1]="0";
        str[2][2]="0";
        str[2][3]="1013";
        str[2][4]="1001";
        str[2][5]="0";
        str[2][6]="2";
        str[2][7]="0";
        str[2][8]="3";
        str[2][9]="3";
        str[2][10]="3";
        str[3][0]="5000";
        str[3][1]="0";
        str[3][2]="0";
        str[3][3]="1019";
        str[3][4]="1001";
        str[3][5]="0";
        str[3][6]="3";
        str[3][7]="0";
        str[3][8]="3";
        str[3][9]="2";//noid
        str[3][10]="2";
      */
    	for (int u=0;u<str.length;u++)
    		System.out.println(str[u][0]+".."+str[u][8]);
        int i = 0;
        int j=str.length-1;
       // System.out.println("j--------------"+j);
       // System.out.println("7---"+j);
        if (str[0][9].equals(str[j][9])||str[0][10].equals(str[j][10]))
       	 return str;
		 str[i][0]=str1[i][0];
		 str[i][1]=str1[i][1];
		 str[i][2]=str1[i][2];
		 str[i][3]=str1[i][3];
		 str[i][4]=str1[i][4];
		 str[i][5]=str1[i][5];
		 str[i][6]=str1[i][6];
		 str[i][7]=str1[i][7];
		 str[i][8]=str1[i][8];
		 str[i][9]=str1[i][9];
		 str[i][10]=str1[i][10];		 
		 
        str[0][1] = String.valueOf(Integer.valueOf(str[0][0])*Integer.valueOf(str[0][6]));
        str[j][1] = String.valueOf(Integer.valueOf(str[j][0])*Integer.valueOf(str[0][6]));
        str[j][6] = str[0][6];
        str[j][7] = String.valueOf(1);
        
        int tmp = Integer.valueOf(str[0][1]);
        System.out.println("7ddd---"+i+".."+j+"..."+str[j][1]);
        while ((i<j) && (!(str[i][9].equals(str[j][9])||(str[i][10].equals(str[j][10]))))){
       	 if (tmp>Integer.valueOf(str[j][1])){
       		 tmp = tmp - Integer.valueOf(str[j][1]);
       		 System.out.println("j1--------------"+i+"..扣除。"+str[j][3]+"..-"+str[j][1]+".."+str[j][4]+"..."+j+".."+str[i][3]+".."+str[i][4]+"..0-"+str[j][1]+".."+tmp);
       		 LotteryGameDetail record = new LotteryGameDetail();
       		 record.setLgmid(Integer.valueOf(str[j][4]));
       		 record.setAccountid(Integer.valueOf(str[j][3]));
       		 record.setTrademoney(BigDecimal.valueOf(-Integer.valueOf(str[j][1])));
       		 record.setNoid(Integer.valueOf(str[j][9]));
       		 record.setRaccountid(Integer.valueOf(str[i][3]));
       		 record.setRnoid(Integer.valueOf(str[i][9]));
       		 record.setRresult(str[i][8]);
       		 lotteryGameDetailMapper.insert(record);
       		 record = new LotteryGameDetail();
       		// n.insetLotteryGameDetail(record);
       		 record = new LotteryGameDetail();
       		 record.setLgmid(Integer.valueOf(str[i][4]));
       		 record.setAccountid(Integer.valueOf(str[i][3]));
       		 record.setTrademoney(BigDecimal.valueOf(Integer.valueOf(str[j][1])));
       		 record.setNoid(Integer.valueOf(str[i][9]));
       		 record.setRaccountid(Integer.valueOf(str[j][3]));
       		 record.setRnoid(Integer.valueOf(str[j][9]));
       		 record.setRresult(str[j][8]);
       		 lotteryGameDetailMapper.insert(record); 
       		 
       		 str[j][1] = "0";
       		 str[j][5] = "1";
       		
       		 System.out.println("tmp1--------------"+tmp);
       		 if (Integer.valueOf(str[j][7]) == 0){
       			 if (tmp == 0)
       			     str[j][6] = str[i+1][6];
       			 else
       				str[j][6] = str[i][6];
       			 str[j][7] = String.valueOf(1);
       		 }
       		 j--;
       		 if(i==j){
       			 str[i][1] = String.valueOf(Integer.valueOf(str[i][0])*Integer.valueOf(str[i][6]) - tmp);
       			 str[i][2] = str[i][1];
       		 }else{

       			 if (!(str[i][9].equals(str[j][9])||str[i][10].equals(str[j][10]))){
       				 if (tmp == 0)
       			         str[j][1] = String.valueOf(Integer.valueOf(str[j][0])*Integer.valueOf(str[i+1][6]));
       				 else 
       					 str[j][1] = String.valueOf(Integer.valueOf(str[j][0])*Integer.valueOf(str[i][6]));
       			    // i++;
       			     System.out.println("j2--34------------"+i+".."+j+".."+str[j][1]+".."+str[j][4]);
       			 }
       			 else{

       					 
           			 str[i][1] = String.valueOf(Integer.valueOf(str[i][0])*Integer.valueOf(str[i][6]) - tmp);
           			 str[i][2] = str[i][1];
          			 if ((i+1!=str.length-1)&&(!(str[i+1][4].equals(str1[i+1][4])))){
    	       			 str[i+1][0]=str1[i+1][0];
    	       			 str[i+1][1]=str1[i+1][1];
    	       			 str[i+1][2]=str1[i+1][2];
    	      			 str[i+1][3]=str1[i+1][3];
    	      			 str[i+1][4]=str1[i+1][4];
    	      			 str[i+1][5]=str1[i+1][5];
    	      			 str[i+1][6]=str1[i+1][6];
    	      			 str[i+1][7]=str1[i+1][7];
    	      			 str[i+1][8]=str1[i+1][8];
    	      			 str[i+1][9]=str1[i+1][9];
    	      			 str[i+1][10]=str1[i+1][10];
          			 }
           			 System.out.println("i1--------------"+i+".."+j+".."+str[i][2]+"..."+str[j][2]+".."+str[j][4]);
           			 //System.out.println("stri2--------------"+str[i][2]+".."+tmp);
           		
       			 }
       		 }
       	 }else{
       		 str[j][1] = String.valueOf((Integer.valueOf(str[j][1]) - tmp));
       		 System.out.println("i12--------------"+i+".."+str[j][1]+".."+str[j][3]+".."+str[j][4]+"..-"+tmp+".."+str[i][3]+"..+"+str[i][4]+"..."+tmp);
       		 if (tmp !=0){
	       		 LotteryGameDetail record = new LotteryGameDetail();
	       		 record.setLgmid(Integer.valueOf(str[j][4]));
	       		 record.setAccountid(Integer.valueOf(str[j][3]));
	       		 record.setTrademoney(BigDecimal.valueOf(-tmp));
	       		 record.setNoid(Integer.valueOf(str[j][9]));
	       		 record.setRaccountid(Integer.valueOf(str[i][3]));
	       		 record.setRnoid(Integer.valueOf(str[i][9]));
	       		 record.setRresult(str[i][8]);
	       		 lotteryGameDetailMapper.insert(record);
	       		 
	       		 record.setLgmid(Integer.valueOf(str[i][4]));
	       		 record.setAccountid(Integer.valueOf(str[i][3]));
	       		 record.setTrademoney(BigDecimal.valueOf(tmp));
	       		 record.setNoid(Integer.valueOf(str[i][9]));
	       		 record.setRaccountid(Integer.valueOf(str[j][3]));
	       		 record.setRnoid(Integer.valueOf(str[j][9]));
	       		 record.setRresult(str[j][8]);
	       		 lotteryGameDetailMapper.insert(record);
       		 }
       		// System.out.println("strj1--------------"+str[j][1]);
       		 str[j][5] = "1";
       		 if (Integer.valueOf(str[j][7]) == 0){
       			if (tmp == 0)
      			     str[j][6] = str[i+1][6];
       			else
       			     str[j][6] = str[i][6];
       			 str[j][7] = String.valueOf(1);
       		 }
       		 i++;
       		 if(i < j){
       			 //System.out.println("indexi--------------"+i);
       			// System.out.println("indexj--------------"+j);
       			// System.out.println("tablei--------------"+str[i][9]);
       			 //System.out.println("tablej--------------"+str[i][0]+"..."+str1[j][0]+".."+str[j][4]);
       			 if (!(str[i][4].equals(str1[i][4]))){
       			 str[i][0]=str1[i][0];
       			 str[i][1]=str1[i][1];
       			 str[i][2]=str1[i][2];
      			 str[i][3]=str1[i][3];
      			 str[i][4]=str1[i][4];
      			 str[i][5]=str1[i][5];
      			 str[i][6]=str1[i][6];
      			 str[i][7]=str1[i][7];
      			 str[i][8]=str1[i][8];
      			 str[i][9]=str1[i][9];
      			 str[i][10]=str1[i][10];
       			 }
      			 if ((str[i][9].equals(str[i+1][9])&&(i+1!=str.length-1))&&(!(str[i][4].equals(str1[i][4])))){
	       			 str[i+1][0]=str1[i+1][0];
	       			 str[i+1][1]=str1[i+1][1];
	       			 str[i+1][2]=str1[i+1][2];
	      			 str[i+1][3]=str1[i+1][3];
	      			 str[i+1][4]=str1[i+1][4];
	      			 str[i+1][5]=str1[i+1][5];
	      			 str[i+1][6]=str1[i+1][6];
	      			 str[i+1][7]=str1[i+1][7];
	      			 str[i+1][8]=str1[i+1][8];
	      			 str[i+1][9]=str1[i+1][9];
	      			 str[i+1][10]=str1[i+1][10];
      			 }
      			 System.out.println("tablej-----update---------"+str[i][0]+"..."+str[i][3]+".."+str[i][4]);
       			 if (!(str[i][9].equals(str[j][9])||str[i][10].equals(str[j][10]))){
		        		 str[i][1] = String.valueOf(Integer.valueOf(str[i][0])*Integer.valueOf(str[i][6])); 		
		        		 tmp = Integer.valueOf(str[i][1]);
		        		 System.out.println("i21--233------------"+i+".."+j+".."+tmp+".."+str[i][1]+".."+str[i][4]);
		        		// System.out.println("stri1--------------"+str[i][1]);
           			// System.out.println("tmp2--------------"+tmp);
       			 }
       		 }
       		//System.out.println("9---"+tmp);
       	 }
        }
       // System.out.println("7ddd-sdsd--"+i+".."+j);
        for (i=str.length-1;i>=j;i--){
       	 if(str[i][5].equals("1")){
       		 str[i][2] = String.valueOf(Integer.valueOf(str[i][1])  - Integer.valueOf(str[i][0])*Integer.valueOf(str[i][6]));
       		 
       		//  System.out.println("0----0-"+i+".."+str[i][2]);
       	 }
        }
      //  System.out.println("12----------"+j);
        
       for (i=0;i<j;i++){
       	 str[i][2] =  str[i][1];
       	 //System.out.println("12----------"+str[i][2]);
       }
       
       for (i=0;i<str.length;i++){
       	    str[i][1] =  str[i][0]; 
      	   // System.out.println("98-----"+str[i][1]);
      	   // 
      	    
      }
   /*
      for (int w = 0;w<str.length;w++)
      System.out.println("90------------"+str[w][0]+"..."+str[w][1]+".."+str[w][2]+".."+str[w][3]+".."+str[w][4]+".."+str[w][8]);
    */
        return str;
       
    }
    
    /**
     * 庄判断比较,输庄
     */
    public synchronized Map<Integer, Object> doBankerHandleLess(int gains,int count,int values,int times,String[][] str,String[][] strEqual){
       /*
   	    str[0][0]="2000";
        str[0][1]="2000";
        str[0][2]="2000";
        str[0][3]="1019";
        str[0][4]="1001";
        str[0][5]="0";
        str[0][6]="5";
        str[0][7]="0";
        str[0][8]="牛牛";
        str[0][9]="1";
        str[0][10]="3";
        str[1][0]="200";
        str[1][1]="200";
        str[1][2]="200";
        str[1][3]="1020";
        str[1][4]="1001";
        str[1][5]="0";
        str[1][6]="5";
        str[1][7]="0";
        str[1][8]="牛牛";
        str[1][9]="1";
        str[1][10]="3";
        str[2][0]="2000";
        str[2][1]="2000";
        str[2][2]="2000";
        str[2][3]="1011";
        str[2][4]="1001";
        str[2][5]="0";
        str[2][6]="1";
        str[2][7]="0";
        str[2][9]="1";
        str[2][10]="3";
        */
        int i = 0;
        int j=str.length-1;

        String[][] strtemp = new String[str.length][11];
        Map<Integer, Object> map = new HashMap<Integer, Object>();
       
        int base = values-gains-count;
        if (base > count)
       	 base = count;
        System.out.println("7---"+j+".."+base);
        int tempgain = base;
        
        if (base == 0){
            map.put(1, gains);
            map.put(2, count);
            map.put(3, values);
            map.put(4, str); 
        }
        for (int m = 0;m<str.length;m++){
         	strtemp[j-m][0]=str[m][0];
         	strtemp[j-m][1]=str[m][1];
         	strtemp[j-m][2]=str[m][2];
         	strtemp[j-m][3]=str[m][3];
         	strtemp[j-m][4]=str[m][4];
         	strtemp[j-m][5]=str[m][5];
         	strtemp[j-m][6]=str[m][6];
         	strtemp[j-m][7]=str[m][7];
         	strtemp[j-m][8]=str[m][8];
         	strtemp[j-m][9]=str[m][9];
         	strtemp[j-m][10]=str[m][10];
         }
         str = strtemp;
        for (;j>=0;j--){
       	 System.out.println("90---"+str[j][1]+".."+times+".."+base);
       	 if (Integer.valueOf(str[j][1])*times<base){
       		 base = base - Integer.valueOf(str[j][0])*times;
       		 str[j][1] = "0";
       		 System.out.println("09----"+str[j][3]+"..-"+(Integer.valueOf(str[j][0])*times)+"...庄"+"...+"+(Integer.valueOf(str[j][0])*times)+".."+base);
       		 doCompareHandle1(strEqual,str,j,-Integer.valueOf(str[j][0])*times,Integer.valueOf(str[j][0])*times);		
     
       	 }else{
       		 str[j][1] = String.valueOf((Integer.valueOf(str[j][0])*times - base));
       		 System.out.println("09-34---庄"+"..+"+base+"..."+str[j][3]+"...-"+base);
       		 doCompareHandle1(strEqual,str,j,-base,base);
       		 
       		 base = 0;
       		 break;
       	 }
        }
        for (i=0;i<=str.length-1;i++){
        	if (i<j){
        		str[i][2] = String.valueOf(0);
        		str[i][1] = str[i][0];
        	}
        	else{
        		str[i][2] = String.valueOf(Integer.valueOf(str[i][1]) - Integer.valueOf(str[i][0])*times);
        		str[i][1] = str[i][0];
        	    //count = count - Integer.valueOf(str[i][2]);
        	}
        }
        gains = gains+tempgain-base;
        System.out.println("gains="+gains+"..count="+count+"..values="+values);
      
       for (i=0;i<=str.length-1;i++){
       	System.out.println("0----"+i+".."+str[i][2]+"..");
       }
       
      // System.out.println("0----"+count);
       map.put(1, gains);
       map.put(2, count);
       map.put(3, values);
       map.put(4, str); 
       return map;
        
    }
    
    
    /**
     * 庄判断比较,赢庄
     */
    public  synchronized Map<Integer, Object> doBankerHandleMore(int gains,int count,int values,int times,String[][] str,String[][] strEqual){
        
   	/*
       // count  =120;
   	 str[0][0]="2000";
        str[0][1]="2000";
        str[0][2]="0";
        str[0][3]="1019";
        str[0][4]="1001";
        str[0][5]="0";
        str[0][6]="5";
        str[0][7]="0";
        str[0][8]="牛牛";
        str[0][9]="牛牛";
        str[1][0]="3000";
        str[1][1]="3000";
        str[1][2]="0";
        str[1][3]="1020";
        str[1][4]="1001";
        str[1][5]="0";
        str[1][6]="5";
        str[1][7]="0";
        str[1][8]="牛牛";
        str[2][0]="500";
        str[2][1]="500";
        str[2][2]="0";
        str[2][3]="1011";
        str[2][4]="1001";
        str[2][5]="0";
        str[2][6]="5";
        str[2][7]="0";
        /*
        str[3][0]="1000";
        str[3][1]="1000";
        str[3][2]="0";
        str[3][3]="1011";
        str[3][4]="1001";
        str[3][5]="0";
        str[3][6]="1";
        str[3][7]="0";
        */
 
   	 
        int i = 0;
        int j=str.length-1;
        Map<Integer, Object> map = new HashMap<Integer, Object>();
        
        int base = doCompareCount(gains,count,values);
        System.out.println("7---"+base);
        for (i=0;i<=j;i++){
       	 if (base>(Integer.valueOf(str[i][0])*Integer.valueOf(str[i][6]))){
       		 str[i][2] = String.valueOf(Integer.valueOf(str[i][0])*Integer.valueOf(str[i][6]));
       		 base = base - Integer.valueOf(str[i][0])*Integer.valueOf(str[i][6]);
       		// count = count - Integer.valueOf(str[i][0])*Integer.valueOf(str[i][6]);
       		 System.out.println("ying-1---"+str[i][3]+"..+"+Integer.valueOf(str[i][0])*Integer.valueOf(str[i][6])+"..庄"+"..-"+Integer.valueOf(str[i][0])*Integer.valueOf(str[i][6]));
       		 doCompareHandle1(strEqual,str,i,Integer.valueOf(str[i][0])*Integer.valueOf(str[i][6]),-Integer.valueOf(str[i][0])*Integer.valueOf(str[i][6]));	
       		 
       	 }else{
       		 str[i][2] = String.valueOf(base);
       		// count = 0;
       		 System.out.println("ying-2---"+str[i][3]+"..+"+base+"..庄"+"..-"+base);
       		 doCompareHandle1(strEqual,str,i,base,-base);	
       		 base = 0;
             break;
       	 }
        }
        System.out.println("gq--"+count);
        for (i=0;i<=str.length-1;i++){
       	// System.out.println("800-"+str[i][2]);
       	 count = count - Integer.valueOf(str[i][2]);
        }
        /*
        System.out.println("gains="+gains+"..count="+count+"..values="+values);
       
       for (i=0;i<=str.length-1;i++){
       	System.out.println("0----"+i+".."+str[i][2]+"..");
       }
      */
       map.put(1, gains);
       map.put(2, count);
       map.put(3, values);
       map.put(4, str);
       
       return map;
        
    }
    
    public synchronized void doCompareHandle1(String[][] strEqual,String[][] str,int j,int comMoney1,int comMoney2){
    	for (int t = 0;t<strEqual.length;t++){
      		 LotteryGameDetail record = new LotteryGameDetail();
      		 record.setLgmid(Integer.valueOf(str[j][4]));
      		 record.setAccountid(Integer.valueOf(str[j][3]));
      		 if (t==0)
      		     record.setTrademoney(BigDecimal.valueOf(comMoney1));
      		 else 
      		    record.setTrademoney(BigDecimal.valueOf(0));
      		 record.setNoid(Integer.valueOf(str[j][10]));
      		 record.setRaccountid(Integer.valueOf(strEqual[t][3]));
      		 record.setRnoid(Integer.valueOf(strEqual[t][10]));
      		 record.setRresult(strEqual[t][8]);
  		     lotteryGameDetailMapper.insert(record);
  		 }
 		 for (int t = 0;t<strEqual.length;t++){
      		 LotteryGameDetail record1 = new LotteryGameDetail();
      		 record1.setLgmid(Integer.valueOf(strEqual[t][4]));
      		 record1.setAccountid(Integer.valueOf(strEqual[t][3]));
      		// if (t==0)
      		 record1.setTrademoney(BigDecimal.valueOf(comMoney2));
      		// else
      			// record.setTrademoney(BigDecimal.valueOf(0));
      		 record1.setNoid(Integer.valueOf(strEqual[t][10]));
      		 record1.setRaccountid(Integer.valueOf(str[j][3]));
      		 record1.setRnoid(Integer.valueOf(str[j][10]));
      		 record1.setRresult(str[j][8]);
  		     lotteryGameDetailMapper.insert(record1);
  		 }	 
 		 
    }
    
    public static int doCompareCount(int gains,int count,int values){
   	 int base = 0;
   	 if ((values - gains - count)>count){
   		 base = count;
   	 }
   	 else 
   		 base = values - gains -count;
   	 return base;
    } 
    
}
