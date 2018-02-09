package com.lottery.orm.service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lottery.orm.bo.AccountAmount;
import com.lottery.orm.bo.AccountInfo;
import com.lottery.orm.bo.AccountRecharge;
import com.lottery.orm.bo.LotteryAddAccount;
import com.lottery.orm.bo.LotteryGame;
import com.lottery.orm.bo.LotteryGameDetail;
import com.lottery.orm.bo.LotteryGameOrder;
import com.lottery.orm.bo.LotteryGameResults;
import com.lottery.orm.bo.LotteryGameResultsKey;
import com.lottery.orm.bo.LotteryGameRound;
import com.lottery.orm.bo.LotteryOrderRecord;
import com.lottery.orm.bo.LotteryRoom;
import com.lottery.orm.bo.LotteryRoomDetail;
import com.lottery.orm.bo.LotteryRound;
import com.lottery.orm.bo.LotteryService;
import com.lottery.orm.bo.SysCom;
import com.lottery.orm.bo.SysFee;
import com.lottery.orm.bo.TradeInfo;
import com.lottery.orm.dao.AccountAmountMapper;
import com.lottery.orm.dao.AccountRechargeMapper;
import com.lottery.orm.dao.CustomLotteryMapper;
import com.lottery.orm.dao.LotteryAddAccountMapper;
import com.lottery.orm.dao.LotteryGameDetailMapper;
import com.lottery.orm.dao.LotteryGameMapper;
import com.lottery.orm.dao.LotteryGameOrderMapper;
import com.lottery.orm.dao.LotteryGameResultsMapper;
import com.lottery.orm.dao.LotteryGameRoundMapper;
import com.lottery.orm.dao.LotteryOrderRecordMapper;
import com.lottery.orm.dao.LotteryRoomDetailMapper;
import com.lottery.orm.dao.LotteryRoomMapper;
import com.lottery.orm.dao.LotteryServiceMapper;
import com.lottery.orm.dao.SysComMapper;
import com.lottery.orm.dto.AddLotteryAmountDto;
import com.lottery.orm.dto.LotteryAmountDto;
import com.lottery.orm.dto.LotteryServiceDto;
import com.lottery.orm.service.LotteryRoundService;
import com.lottery.orm.util.CommonUtils;
import com.lottery.orm.util.ConfigUtils;
import com.lottery.orm.util.EnumType;
import com.lottery.orm.util.HttpclientTool;
import com.lottery.orm.util.MessageTool;
import com.wordnik.swagger.annotations.ApiModelProperty;

@Service
@Transactional
public class LotteryTaskService {
	public final Logger log = Logger.getLogger(this.getClass());

	private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final Integer ROUND_INTERVAL_MINUTES = 10;//游戏间隔时间
	
	@Autowired
	private LotteryRoundService lotteryRoundService;
	
	@Autowired
	private CustomLotteryMapper customLotteryMapper;
	
	@Autowired
	private LotteryGameMapper lotteryGameMapper;
	
	@Autowired
	private SysComMapper sysComMapper;
	
	@Autowired
	private LotteryRoomMapper lotteryRoomMapper;
	
	@Autowired
	private LotteryGameRoundMapper lotteryGameRoundMapper;
	
	@Autowired
	private LotteryGameResultsMapper lotteryGameResultsMapper;
	
	@Autowired
	private LotteryRoomDetailMapper lotteryRoomDetailMapper;
	
	@Autowired
	private LotteryGameOrderMapper lotteryGameOrderMapper;
	
	@Autowired
	private AccountInfoService accountInfoService;
	
	@Autowired
	private LotteryOrderRecordMapper lotteryOrderRecordMapper;
	
	@Autowired
	private TradeInfoService tradeInfoService;
	
	@Autowired
	private AccountAmountMapper accountAmountMapper;
	
	@Autowired
	private LotteryGameDetailMapper lotteryGameDetailMapper;
	
	@Autowired
	private AccountRechargeMapper accountRechargeMapper;
	
	@Autowired
	private LotteryInfoService lotteryInfoService;
	
	@Autowired
	private JobsTaskService jobsTaskService;
	
	@Autowired
	private LotteryAddAccountMapper lotteryAddAcountMapper;
	
	@Autowired
    private LotteryServiceMapper lotteryServiceMapper;
	
	@Value("${lottery.apiUrl.cqklsf}")
    private String lotteryApiUrlCQ;
	
	@Value("${lottery.apiUrl.cqssc1}")
	private String lotteryApiUrlCQSSC1;
	
	@Value("${lottery.apiUrl.cqssc2}")
	private String lotteryApiUrlCQSSC2;
	
	@Value("${lottery.apiUrl.hljssc1}")
	private String lotteryApiUrlHLJSSC1;
	
	@Value("${lottery.apiUrl.hljssc2}")
	private String lotteryApiUrlHLJSSC2;
	
	@Value("${lottery.apiUrl.tjssc1}")
	private String lotteryApiUrlTJSSC1;
	
	@Value("${lottery.apiUrl.tjssc2}")
	private String lotteryApiUrlTJSSC2;
	
	
	@Value("${lottery.apiUrl.xjssc1}")
	private String lotteryApiUrlXJSSC1;
	
	@Value("${lottery.apiUrl.xjssc2}")
	private String lotteryApiUrlXJSSC2;
	
	@Value("${lottery.apiUrl.ynssc1}")
	private String lotteryApiUrlYNSSC1;
	
	@Value("${lottery.apiUrl.ynssc2}")
	private String lotteryApiUrlYNSSC2;
	
	@Value("${lottery.apiUrl.bjsc1}")
	private String lotteryApiUrlBJSC1;
	
	@Value("${lottery.apiUrl.bjsc2}")
	private String lotteryApiUrlBJSC2;

	@Value("${lottery.apiUrl.xyft1}")
	private String lotteryApiUrlXYFT1;
	
	@Value("${lottery.apiUrl.xyft2}")
	private String lotteryApiUrlXYFT2;
	
	@Value("${lottery.apiUrl.gdklsf}")
    private String lotteryApiUrlGD;
	
	@Value("${lottery.apiUrl.tjklsf}")
    private String lotteryApiUrlTJ;
	
	@Value("${outResultUrl}")
    private String outResultUrl;
	
	/**
	 * 获取重庆时时彩
	 * @throws Exception 
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
	public synchronized void getCQSSCLotteryResult() throws Exception{
		getLotteryOriginResultTotal(EnumType.LotteryType.CQSSC.ID, lotteryApiUrlCQSSC2);
	}
	
	/**
	 * 获取天津时时彩
	 * @throws Exception 
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
	public synchronized void getTJSSCLotteryResult() throws Exception{
		getLotteryOriginResultTotal(EnumType.LotteryType.TJSSC.ID, lotteryApiUrlTJSSC2);
		
	}
	
	/**
	 * 获取新疆时时彩
	 * @throws Exception 
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
	public synchronized void getXJSSCLotteryResult() throws Exception{
		getLotteryOriginResultTotal(EnumType.LotteryType.XJSSC.ID, lotteryApiUrlXJSSC2);
	}
	
	/**
	 * 获取云南时时彩
	 * @throws Exception 
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
	public synchronized void getYNSSCLotteryResult() throws Exception{
		getLotteryOriginResultTotal(EnumType.LotteryType.YNSSC.ID, lotteryApiUrlYNSSC2);
	}
	
	/**
	 * 获取黑龙江时时彩
	 * @throws Exception 
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
	public synchronized void getHLJSSCLotteryResult() throws Exception{
		getLotteryOriginResultTotal(EnumType.LotteryType.HLJSSC.ID, lotteryApiUrlHLJSSC2);
	}
	
	/**
	 * 获取北京赛车时时彩
	 * @throws Exception 
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
	public synchronized void getBJSCLotteryResult() throws Exception{
		getLotteryOriginResultTotal(EnumType.LotteryType.BJSC.ID, lotteryApiUrlBJSC2);
	}
	
	/**
	 * 获取幸运飞艇时时彩
	 * @throws Exception 
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
	public synchronized void getXYFTLotteryResult() throws Exception{
		getLotteryOriginResultTotal(EnumType.LotteryType.XYFT.ID, lotteryApiUrlXYFT2);
	}
	
	/**
	 * 增值服务第一次投注
	 * @throws Exception 
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
	public synchronized void getFirstValueLotteryResult(LotteryGameRound lgr) throws Exception{
		
		int []numbers = {50,100,200};
        Random random = new Random();
        int index = random.nextInt(numbers.length);
        String accountids = "";
        LotteryOrderRecord lrc = lotteryOrderRecordMapper.selectValueByLottery(lgr.getSid(), lgr.getLotteryterm());
        if (null == lrc){
        	return;
        }else{
        	// (int t = 0;t<lor.size();t++){
        	//	LotteryOrderRecord lrc = new LotteryOrderRecord();	
        	//	lrc = lor.get(t);
        	    accountids = lrc.getAccoundids();
        	    int noids = 0;
		        //01：成功;02:失败
        	    System.out.println("date = "+new Date()+".."+lrc.getFirsttime());
		        if (new Date().after(lrc.getFirsttime())&&!lrc.getFirstvalue().equals("01")){
			        LotteryGameOrder order = new LotteryGameOrder();
					List<LotteryGameOrder> list = lotteryGameOrderMapper.selectByNoResultValue(lrc.getSid(),lrc.getLotteryterm());   
			        String[] lorslist = accountids.split(",");
			        if (lrc.getSid()==2001||lrc.getSid()==2002)
			        	noids = 5;
			        else 
			        	noids = 10;
			        for (int m = 0;m<lorslist.length;m++){
				    	order.setAccountid(Integer.valueOf(lorslist[m].replaceAll("'", "")));
					    for (int i=0;i<list.size();i++){
					    	LotteryGameOrder lr = new LotteryGameOrder();
					    	lr = list.get(i);
					    	for (int j=1;j<=noids;j++){
					    		if (lr.getPlayoridle().equals("1")&&lr.getNoid()==j){
					    			order.setPlayoridle("1");
					    			order.setNoid(lr.getNoid());
					    			order.setLtdid(j);
					    		}else{
					    			order.setNoid(j);
					    			order.setLtdid(j);	
					    			order.setPlayoridle("2");
					    		}
						    	order.setSid(lrc.getSid());
						    	order.setRmid(lr.getRmid());
						    	order.setLotteryterm(lrc.getLotteryterm());
						    	order.setOrderamount(BigDecimal.valueOf(numbers[random.nextInt(numbers.length)]));
						    	order.setOrdertime(new Date());
						    	order.setOpentime(lr.getOpentime());
						        lotteryGameOrderMapper.insertSelective(order);
						        accountInfoService.updateResultAccountMount(BigDecimal.valueOf(0).subtract(order.getOrderamount()), order.getAccountid());
					    	}
					    }
			    }
			        lrc.setFirstvalue("01");//完成；
			        lrc.setFovertime(new Date());
		            lotteryOrderRecordMapper.updateByPrimaryKey(lrc);
		        }

	        	}
        	}
        	
	
	private synchronized String getAddService() throws Exception {
		LotteryService lotteryService = lotteryServiceMapper.selectByPrimaryKey(1000);
		String state = lotteryService.getAddedservice();
		return state;
	}
	
	private synchronized void getLotteryOriginResultTotal(String lotteryType, String apiUrl) throws Exception {
		
		boolean result = getLotteryOriginResult2(lotteryType,apiUrl);
	    /*
		//if (!result){
			String url = getUrl(lotteryType);
			getLotteryOriginResult1(lotteryType,url);
		//}
		*/
	}
	
	private synchronized String getUrl(String lotteryType)throws Exception{
		String url = "";
		if (lotteryType.equals(EnumType.LotteryType.XYFT.ID))
			url = lotteryApiUrlXYFT1;
		else if (lotteryType.equals(EnumType.LotteryType.CQSSC.ID))
			url = lotteryApiUrlCQSSC1;
		else if (lotteryType.equals(EnumType.LotteryType.TJSSC.ID))
			url = lotteryApiUrlTJSSC1;
		else if (lotteryType.equals(EnumType.LotteryType.HLJSSC.ID))
			url = lotteryApiUrlHLJSSC1;
		else if (lotteryType.equals(EnumType.LotteryType.YNSSC.ID))
			url = lotteryApiUrlYNSSC1;
		else if (lotteryType.equals(EnumType.LotteryType.XJSSC.ID))
			url = lotteryApiUrlXJSSC1;
		else if (lotteryType.equals(EnumType.LotteryType.BJSC.ID))
			url = lotteryApiUrlBJSC1;
		return url;
	}
	/**
	 * 获取开奖结果
	 * @throws Exception 
	 */
	private synchronized boolean getLotteryOriginResult1(String lotteryType, String apiUrl) throws Exception {
		//String result = HttpclientTool.get(apiUrl);
		String result = "";
		System.out.println("8-------------:"+lotteryType+".."+result);
		/*
	
		result = "{\"success\":true,\"data\":[{\"preDrawCode\":\"88131\","
				+ "\"drawIssue\":\"2017112355\",\"drawTime\":\"2017/11/23 19:10:45\","
				+ "\"preDrawTime\":\"2017-11-23 19:00:50\",\"preDrawIssue\":\"2017112354\","
				+ "\"drawCount\":\"39\",\"totalCount\":\"120\"}]}";
          //result = "{\"sucess\":true,";
		
		//10,07,04,03,02,07,02,03,03,01
	*/
		
		result = "{\"success\":true,\"data\":[{\"preDrawCode\":\"4,9,4,7,9\","
				+ "\"drawIssue\":\"20180202063\",\"drawTime\":\"2018/1/24 16:00:45\","
				+ "\"preDrawTime\":\"2017-11-26 16:50:50\",\"preDrawIssue\":\"20180202062\","
				+ "\"drawCount\":\"39\",\"totalCount\":\"120\"}]}";
				
         // result = "{\"sucess\":true,";
	
		log.info("批处理测试中....."+result);
		//{"success":true,"data":[{"preDrawCode":"26570","drawIssue":"20171018074",
		//"drawTime":"2017/10/18 18:20:45","preDrawTime":"2017-10-18 18:10:45",
		//"preDrawIssue":"20171018073","drawCount":"47","totalCount":"120"}]}
		LotteryGameRound lgr = new LotteryGameRound();
		LotteryGameRound gRound = new LotteryGameRound();
		if(StringUtils.isNotBlank(result)&&result.trim().startsWith("{")){
			JSONObject jObj = new JSONObject(result);
			JSONArray jArray = jObj.getJSONArray("data");
			//上期
			lgr.setSid(Integer.valueOf(lotteryType));
			lgr.setLotteryterm(jArray.getJSONObject(0).getString("preDrawIssue"));
			lgr.setLotteryresult(jArray.getJSONObject(0).getString("preDrawCode").contains(",")?jArray.getJSONObject(0).getString("preDrawCode"):CommonUtils.getArrayString(jArray.getJSONObject(0).getString("preDrawCode")));
			
			if ((lgr.getSid()==1001&&CommonUtils.dateRange())||lgr.getSid()==2001||lgr.getSid()==2002){
			    //lgr.setStarttime(CommonUtils.getStringToMillon(jArray.getJSONObject(0).getString("preDrawTime"),260));
			    //lgr.setOvertime(CommonUtils.getStringToMillon(jArray.getJSONObject(0).getString("preDrawTime"),30));
				lgr.setStarttime(CommonUtils.getStringToMillon(jArray.getJSONObject(0).getString("preDrawTime"),200));
			    lgr.setOvertime(CommonUtils.getStringToMillon(jArray.getJSONObject(0).getString("preDrawTime"),-30));
			}
			else{
				//lgr.setStarttime(CommonUtils.getStringToMillon(jArray.getJSONObject(0).getString("preDrawTime"),9*60));
				//lgr.setOvertime(CommonUtils.getStringToMillon(jArray.getJSONObject(0).getString("preDrawTime"),60));
				lgr.setStarttime(CommonUtils.getStringToMillon(jArray.getJSONObject(0).getString("preDrawTime"),510));
				lgr.setOvertime(CommonUtils.getStringToMillon(jArray.getJSONObject(0).getString("preDrawTime"),0));
			}
			
			//lgr.setOpentime(CommonUtils.getStringToDate(jArray.getJSONObject(0).getString("preDrawTime")));
			//lgr.setClosetime(CommonUtils.getStringToMillon(jArray.getJSONObject(0).getString("preDrawTime"),80));
			  lgr.setOpentime(CommonUtils.getStringToMillon(jArray.getJSONObject(0).getString("preDrawTime"),-60));
			  lgr.setClosetime(CommonUtils.getStringToMillon(jArray.getJSONObject(0).getString("preDrawTime"),-30));
			  
			try{
			gRound = lotteryRoundService.getLotteryTermResult(lgr.getSid(), lgr.getLotteryterm());
			//getFirstValueLotteryResult(lgr);
			if (gRound!=null){
				//System.out.println("9----------------"+gRound.getLotteryresult());
			    //if (gRound.getLotteryterm().length()>0){
				if ((null == gRound.getLotteryresult()) ||gRound.getLotteryresult().equals("")){
					//更新结果
					//System.out.println("9----------------"+lgr.getLotteryresult());
					gRound.setLotteryresult(lgr.getLotteryresult());
					gRound.setActopentime(new Date());
					gRound.setActclosetime(new Date());
					lotteryGameRoundMapper.updateByPrimaryKeySelective(gRound);
				
					//结果更新
					LotteryResultHandle2(gRound);
					//结果排名
					LotteryResultHandle3(gRound);	
					//账户处理
					List<LotteryGameOrder> list = lotteryGameOrderMapper.selectGameRmid(lgr.getSid(), lgr.getLotteryterm());
					//sleep(20000);
					// LotteryGameDetail record = new LotteryGameDetail();
	        		// record.setLgmid(1002);
					//lotteryGameDetailMapper.insert(record);
					for (int i = 0;i<list.size();i++){
						LotteryGameOrder lg = new LotteryGameOrder();
						lg = list.get(i);
						
						LotteryIsOrNotHandle(lgr.getLotteryterm(),lgr.getSid(),lg.getRmid(),"0",1);	
						//sleep(20000);
					 	//getSecondValueLotteryResult(gRound,lg.getRmid());
					 	
						//LotteryIsOrNotHandle(lgr.getLotteryterm(),lgr.getSid(),lg.getRmid());
					}
				}
			}else{
			    lgr.setActopentime(new Date());
				lgr.setActclosetime(new Date());
				lotteryGameRoundMapper.insertSelective(lgr);
			}

			//本期
			LotteryGameRound lgr1 = new LotteryGameRound();
			lgr1.setSid(Integer.valueOf(lotteryType));
			lgr1.setLotteryterm(jArray.getJSONObject(0).getString("drawIssue"));
			
			if ((lgr.getSid()==1001&&CommonUtils.dateRange())||lgr.getSid()==2001||lgr.getSid()==2002){
			    //lgr1.setStarttime(CommonUtils.getStringToMillon(jArray.getJSONObject(0).getString("drawTime"),260));
			    //lgr1.setOvertime(CommonUtils.getStringToMillon(jArray.getJSONObject(0).getString("drawTime"),30));
				lgr1.setStarttime(CommonUtils.getStringToMillon(jArray.getJSONObject(0).getString("drawTime"),200));
			    lgr1.setOvertime(CommonUtils.getStringToMillon(jArray.getJSONObject(0).getString("drawTime"),-30));
			}
			else{
			    //lgr1.setStarttime(CommonUtils.getStringToMillon(jArray.getJSONObject(0).getString("drawTime"),9*60));
			    //lgr1.setOvertime(CommonUtils.getStringToMillon(jArray.getJSONObject(0).getString("drawTime"),60));
				lgr1.setStarttime(CommonUtils.getStringToMillon(jArray.getJSONObject(0).getString("drawTime"),510));
				lgr1.setOvertime(CommonUtils.getStringToMillon(jArray.getJSONObject(0).getString("drawTime"),0));
			}
			//lgr1.setOpentime(CommonUtils.getStringToDate(jArray.getJSONObject(0).getString("drawTime")));
			//lgr1.setClosetime(CommonUtils.getStringToMillon(jArray.getJSONObject(0).getString("drawTime"),80));
			lgr1.setOpentime(CommonUtils.getStringToMillon(jArray.getJSONObject(0).getString("drawTime"),-60));
			lgr1.setClosetime(CommonUtils.getStringToMillon(jArray.getJSONObject(0).getString("drawTime"),-30));
			
			LotteryGameRound gRound1 = lotteryRoundService.getLotteryTermResult(Integer.valueOf(lotteryType), lgr1.getLotteryterm());
			//System.out.println("9--45--------------"+gRound+".."+lgr1.getLotteryterm()+".."+lgr1.getSid());
			//getFirstValueLotteryResult(lgr1);
			
			if (gRound1==null){
				//System.out.println("9--678--"+lgr1.getSid()+".."+lgr1.getLotteryterm()+".."+lgr1.getLotteryresult());
				
				lgr1.setLotteryresult(null);
				lotteryGameRoundMapper.insertSelective(lgr1);
				LotteryGame lr = new LotteryGame();
				lr.setSid(Integer.valueOf(lgr1.getSid()));
				lr.setGameterm(lgr1.getLotteryterm());
				lr.setGamestarttime(lgr1.getStarttime());
				lr.setGameovertime(lgr1.getOvertime());
				lotteryGameMapper.updateLotteryTime(lr.getSid(),lr.getGameterm(),lr.getGamestarttime(), lr.getGameovertime());
				//System.out.println("9----hello-----"+lgr1.getSid()+".."+lr.getGamestarttime());
		        //结果处理1
				LotteryResultHandle1(lr);
				//试玩处理
				LotteryResultHandle4(lgr1);
				//增值服务
				//LotteryResultValue(lgr1);
			}else{
				lgr1.setLgrid(gRound1.getLgrid());
				lotteryGameRoundMapper.updateByPrimaryKey(lgr1);
				LotteryGame lr = new LotteryGame();
				lr.setSid(Integer.valueOf(lgr1.getSid()));
				lr.setGameterm(lgr1.getLotteryterm());
				lr.setGamestarttime(lgr1.getStarttime());
				lr.setGameovertime(lgr1.getOvertime());
				lotteryGameMapper.updateLotteryTime(lr.getSid(),lr.getGameterm(),lr.getGamestarttime(), lr.getGameovertime());
			}
			
			return true;
			}catch(Exception e){
				System.out.println(e);
				e.printStackTrace();
				return false;
			}
			//System.out.println("2----"+jObj.getJSONArray("data"));
			//System.out.println("1--"+jObj.getString("preDrawCode")+".."+jObj.getString("drawIssue")+".."+jObj.getString("preDrawIssue"));
			//JSONObject openObj = openArray.getJSONObject(i);
		}
		return false;
	}
	public synchronized void LotteryPlayerDelete() throws Exception{
		jobsTaskService.LotteryPlayerDelete();
	}
	
	/**
	 * 获取开奖结果
	 * @throws Exception 
	 */
	private synchronized boolean getLotteryOriginResult2(String lotteryType, String apiUrl) throws Exception {
		
		LotteryGameRound lgr = new LotteryGameRound();
		LotteryGameRound gRound = new LotteryGameRound();
		String result = HttpclientTool.get(apiUrl);
		System.out.println("API接口访问："+lotteryType+".."+apiUrl+"..."+result);	
		try{
		    if(StringUtils.isNotBlank(result)&&result.trim().startsWith("{")){
			    JSONObject jObj = new JSONObject(result);
			//Date latestOpenTime = (new DateTime()).toDate();
			    //String addState = getAddService();
			   // System.out.println("00---"+addState);
			    String addState = "0";
			    if(jObj.has("next")){
					JSONArray nextArray = jObj.getJSONArray("next");//获取下一轮游戏期次
					Date nextOpenTime = null;//最近的下一轮游戏
					JSONObject nextObj = null;
					for(int t=0; t<nextArray.length(); t++){
						JSONObject tempObj = nextArray.getJSONObject(t);
						String opentimeStr = tempObj.getString("opentime");
						Date tempOpenTime = format.parse(opentimeStr.replace("**", "01"));
						if(nextOpenTime==null||nextOpenTime.after(tempOpenTime)){
							nextOpenTime = tempOpenTime;
							nextObj = tempObj;
						}
					}
					//本期
					LotteryGameRound lgr1 = new LotteryGameRound();
					lgr1.setSid(Integer.valueOf(lotteryType));
					lgr1.setLotteryterm(nextObj.getString("expect"));
					String nOpentime = "";
					//System.out.println("ceshi0---------------"+lotteryType+".."+CommonUtils.StrToDate(nextObj.getString("opentime"))+".."+new Date()+"..."+(CommonUtils.getCompareMin(CommonUtils.StrToDate(nextObj.getString("opentime")), new Date())>12));
				   /*
					if (CommonUtils.getCompareMin(CommonUtils.StrToDate(nextObj.getString("opentime")), new Date())>12)
						nOpentime = CommonUtils.dateAddMin(10);
				    else 
				    	nOpentime = nextObj.getString("opentime");
				    	*/
					if ((lgr1.getSid()==1001&&CommonUtils.dateRange())||lgr1.getSid()==2001||lgr1.getSid()==2002){
						//nOpentime = CommonUtils.dateAddMin(5);
						if (CommonUtils.getCompareMin(CommonUtils.StrToDate(nextObj.getString("opentime")), new Date())>6)
							nOpentime = CommonUtils.dateAddMin(5);
					    else 
					    	nOpentime = nextObj.getString("opentime"); 
						//nOpentime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(nextOpenTime);
					    lgr1.setStarttime(CommonUtils.getStringToMillon(nOpentime,255));
					    lgr1.setOvertime(CommonUtils.getStringToMillon(nOpentime,40));
					    lgr1.setOpentime(CommonUtils.getStringToMillon(nOpentime,0));
						lgr1.setClosetime(CommonUtils.getStringToMillon(nOpentime,40));
					}
					else{
						//nOpentime = CommonUtils.dateAddMin(10);
						//nOpentime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(nextOpenTime);
						if (CommonUtils.getCompareMin(CommonUtils.StrToDate(nextObj.getString("opentime")), new Date())>12)
							nOpentime = CommonUtils.dateAddMin(10);
					    else 
					    	nOpentime = nextObj.getString("opentime"); 
						lgr1.setStarttime(CommonUtils.getStringToMillon(nOpentime,560));
						lgr1.setOvertime(CommonUtils.getStringToMillon(nOpentime,60));
						lgr1.setOpentime(CommonUtils.getStringToMillon(nOpentime,0));
						lgr1.setClosetime(CommonUtils.getStringToMillon(nOpentime,60));
					}
					
					LotteryGameRound gRound1 = lotteryRoundService.getLotteryTermResult(Integer.valueOf(lotteryType), lgr1.getLotteryterm());
					jobsTaskService.taskplayoridle(lgr1);
					/*
					if (addState.equals("1"))
					    getFirstValueLotteryResult(lgr1);
					    */
					//System.out.println("9--45--------------"+gRound+".."+lgr1.getLotteryterm()+".."+lgr1.getSid());
					if (gRound1==null){
						//System.out.println("9--678--"+lgr1.getSid()+".."+lgr1.getLotteryterm()+".."+lgr1.getLotteryresult());
						
						lgr1.setLotteryresult(null);
						lotteryGameRoundMapper.insertSelective(lgr1);
						LotteryGame lr = new LotteryGame();
						lr.setSid(Integer.valueOf(lgr1.getSid()));
						lr.setGameterm(lgr1.getLotteryterm());
						lr.setGamestarttime(lgr1.getStarttime());
						lr.setGameovertime(lgr1.getOvertime());
						lotteryGameMapper.updateLotteryTime(lr.getSid(),lr.getGameterm(),lr.getGamestarttime(), lr.getGameovertime());
						//System.out.println("9----hello-----"+lgr1.getSid()+".."+lr.getGamestarttime());
				        //结果处理1
						LotteryResultHandle1(lr);
						//试玩处理
						LotteryResultHandle4(lgr1);
						/*
						//增值服务
						if (addState.equals("1"))
						    LotteryResultValue(lgr1);
						    */
					}
					/*
					else{
						lgr1.setLgrid(gRound1.getLgrid());
						lotteryGameRoundMapper.updateByPrimaryKey(lgr1);
						LotteryGame lr = new LotteryGame();
						lr.setSid(Integer.valueOf(lgr1.getSid()));
						lr.setGameterm(lgr1.getLotteryterm());
						lr.setGamestarttime(lgr1.getStarttime());
						lr.setGameovertime(lgr1.getOvertime());
						lotteryGameMapper.updateLotteryTime(lr.getSid(),lr.getGameterm(),lr.getGamestarttime(), lr.getGameovertime());
					}*/	
				}
			    
			    
				if(jObj.has("open")){
					JSONArray openArray = jObj.getJSONArray("open");//更新游戏开奖结果
					//openArray.length()
					for(int i=0; i<20; i++){
						JSONObject openObj = openArray.getJSONObject(i);
						//上期
						lgr.setSid(Integer.valueOf(lotteryType));
						lgr.setLotteryterm(openObj.getString("expect"));
						lgr.setLotteryresult(openObj.getString("opencode").contains(",")?openObj.getString("opencode"):CommonUtils.getArrayString(openObj.getString("opencode")));
						if ((lgr.getSid()==1001&&CommonUtils.dateRange())||lgr.getSid()==2001||lgr.getSid()==2002){
							/*
						    lgr.setStarttime(CommonUtils.getStringToMillon(openObj.getString("opentime"),260));
						    lgr.setOvertime(CommonUtils.getStringToMillon(openObj.getString("opentime"),30));
						    lgr.setOpentime(CommonUtils.getStringToMillon(openObj.getString("opentime"),0));
							lgr.setClosetime(CommonUtils.getStringToMillon(openObj.getString("opentime"),30));
							*/
						    lgr.setStarttime(CommonUtils.getStringToMillon(openObj.getString("opentime"),255));
						    lgr.setOvertime(CommonUtils.getStringToMillon(openObj.getString("opentime"),40));
						    lgr.setOpentime(CommonUtils.getStringToMillon(openObj.getString("opentime"),0));
							lgr.setClosetime(CommonUtils.getStringToMillon(openObj.getString("opentime"),40));
						}
						else{
							/*
							lgr.setStarttime(CommonUtils.getStringToMillon(openObj.getString("opentime"),525-40+5));
							lgr.setOvertime(CommonUtils.getStringToMillon(openObj.getString("opentime"),60-35+30));
							lgr.setOpentime(CommonUtils.getStringToMillon(openObj.getString("opentime"),-35));
							lgr.setClosetime(CommonUtils.getStringToMillon(openObj.getString("opentime"),30-35));
							*/
							lgr.setStarttime(CommonUtils.getStringToMillon(openObj.getString("opentime"),560));
							lgr.setOvertime(CommonUtils.getStringToMillon(openObj.getString("opentime"),60));
							lgr.setOpentime(CommonUtils.getStringToMillon(openObj.getString("opentime"),0));
							lgr.setClosetime(CommonUtils.getStringToMillon(openObj.getString("opentime"),60));
						}
					
					        gRound = lotteryRoundService.getLotteryTermResult(lgr.getSid(), lgr.getLotteryterm());
							if (gRound!=null){
								//System.out.println("9----------------"+gRound.getLotteryresult());
							    //if (gRound.getLotteryterm().length()>0){
								if ((null == gRound.getLotteryresult()) ||gRound.getLotteryresult().equals("")){
									//更新结果
									//System.out.println("9----------------"+lgr.getLotteryresult());
									gRound.setLotteryresult(lgr.getLotteryresult());
									gRound.setActopentime(new Date());
									gRound.setActclosetime(new Date());
									lotteryGameRoundMapper.updateByPrimaryKeySelective(gRound);
								
									//结果更新
									LotteryResultHandle2(gRound);
									//结果排名
									LotteryResultHandle3(gRound);	
									//账户处理
									List<LotteryGameOrder> list = lotteryGameOrderMapper.selectGameRmid(lgr.getSid(), lgr.getLotteryterm());
									//sleep(20000);
									// LotteryGameDetail record = new LotteryGameDetail();
					        		// record.setLgmid(1002);
									//lotteryGameDetailMapper.insert(record);
									for (int m = 0;m<list.size();m++){
										LotteryGameOrder lg = new LotteryGameOrder();
										lg = list.get(m);
										LotteryIsOrNotHandle(lgr.getLotteryterm(),lgr.getSid(),lg.getRmid(),addState,1);	
										//sleep(20000);
										/*
										if (addState.equals("1")){
									        getSecondValueLotteryResult(gRound,lg.getRmid());
										    LotteryIsOrNotHandle(lgr.getLotteryterm(),lgr.getSid(),lg.getRmid(),"1",2);
										}
										*/
									}
								}
							}else{
							    lgr.setActopentime(new Date());
								lgr.setActclosetime(new Date());
								lotteryGameRoundMapper.insertSelective(lgr);
							}
						
						}
					}
					
					return true;	
		    }
				
			}catch(Exception e){
				System.out.println(e);
				e.printStackTrace();
				return false;
			}
		return false;
	}
	

	/**
	 * 结果处理1,游戏的初始化
	 * @throws Exception 
	 */
	public synchronized void LotteryResultHandle1(LotteryGame lottery) throws Exception{
		//System.out.println("00--------------------"+lotteryApiUrlCQ);
		//getLotteryOriginResult(EnumType.LotteryType.CQ.ID, lotteryApiUrlCQ);
	    lotteryGameResultsMapper.insertBatch(lottery);
	}
	
	/**
	 * 结果处理2,更新结果
	 * @throws Exception 
	 */
	public synchronized void LotteryResultHandle2(LotteryGameRound lottery) throws Exception{
		//System.out.println("00--------------------"+lotteryApiUrlCQ);
		//getLotteryOriginResult(EnumType.LotteryType.CQ.ID, lotteryApiUrlCQ);
		List<LotteryGameResults> list = lotteryGameResultsMapper.selectGameResults(lottery.getSid(),lottery.getLotteryterm());
	    for (int i=0;i<list.size();i++){
	    	LotteryGameResults lr = list.get(i);
	    	String[] re = CommonUtils.getStringResult(lottery.getLotteryresult().split(","), lr.getOrders(), lr.getType());
	    	lr.setResults(re[0]);
	    	lr.setScount(Integer.valueOf(re[1]));
	    	lr.setFirst(re[2]);
	    	lr.setSecond(re[3]);
	    	lr.setThird(re[4]);
	    	lr.setResultno(re[5]);
	    	lr.setResultvalue(re[6]);
	    	lr.setRatio(Double.valueOf(re[7]));
	    	lotteryGameResultsMapper.updateByPrimaryKey(lr);
	    }
		
	}
	
	/**
	 * 结果处理3,更新名次
	 * @throws Exception 
	 */
	public synchronized void LotteryResultHandle3(LotteryGameRound lottery) throws Exception{
		List<LotteryGameResults> list = lotteryGameResultsMapper.selectGameResults(lottery.getSid(),lottery.getLotteryterm());
	    String[] com = new String[5];
    	com[0]="0";
    	com[1]="0";
    	com[2]="0";
    	com[3]="0";
    	com[4]="0";
	    int j=1;
	    LotteryRoomDetail lrd = new LotteryRoomDetail();
    	lrd.setRmid(lottery.getSid());
    	lrd.setSid(lottery.getSid());
    	lrd.setLotteryterm(lottery.getLotteryterm());
    	lrd.setGamestarttime(lottery.getStarttime());
    	lrd.setGameovertime(lottery.getOvertime());
		for (int i=0;i<list.size();i++){
	    	LotteryGameResults lr = list.get(i);
	    	if (lr.getNoid()==1){
	    		lrd.setNo1(lr.getResultvalue());
	    	}else if (lr.getNoid()==2){
	    		lrd.setNo2(lr.getResultvalue());
	    	}else if (lr.getNoid()==3){
	    		lrd.setNo3(lr.getResultvalue());
	    	}else if (lr.getNoid()==4){
	    		lrd.setNo4(lr.getResultvalue());
	    	}else if (lr.getNoid()==5){
	    		lrd.setNo5(lr.getResultvalue());
	    	}else if (lr.getNoid()==6){
	    		lrd.setNo6(lr.getResultvalue());
	    	}else if (lr.getNoid()==7){
	    		lrd.setNo7(lr.getResultvalue());
	    	}else if (lr.getNoid()==8){
	    		lrd.setNo8(lr.getResultvalue());
	    	}else if (lr.getNoid()==9){
	    		lrd.setNo9(lr.getResultvalue());
	    	}else if (lr.getNoid()==10){
	    		lrd.setNo10(lr.getResultvalue());
	    	}
	    	String[] re = CommonUtils.getStringResult(lottery.getLotteryresult().split(","), lr.getOrders(), lr.getType());
	        //System.out.println("22--------"+re[1]+".."+re[2]+".."+re[3]+".."+re[4]+".."+re[5]);
	    	if (com[0].equals(re[1])&&com[1].equals(re[2])&&com[2].equals(re[3])&&com[3].equals(re[4])&&com[4].equals(re[5])){
	    		lr.setAscc(j);
	    		com[0]=re[1];
	    		com[1]=re[2];
	    		com[2]=re[3];
	    		com[3]=re[4];
	    		com[4]=re[5];
	    		
	    	}else{
	    		j = i+1;
	    		lr.setAscc(j);
	    		com[0]=re[1];
	    		com[1]=re[2];
	    		com[2]=re[3];
	    		com[3]=re[4];
	    		com[4]=re[5];
	    		
	    	}
	    	
	    	if (lr.getType().equals("02")){
	    		String[] str = CommonUtils.getResultValue(lr.getAscc());
	    		lr.setResultno(str[0]);
	    	}
	    	//System.out.println("0-----------"+lr.getLgrsid()+".."+lr.getAscc());
	    	lotteryGameResultsMapper.updateByPrimaryKeySelective(lr);
	    	LotteryGameResults ls = new LotteryGameResults();
	    	LotteryGameResultsKey key = new LotteryGameResultsKey();
	    	key.setLgrsid(lr.getLgrsid());
	    	key.setLotteryterm(lr.getLotteryterm());
	    	key.setSid(lr.getSid());
	    	key.setType(lr.getType());
	    	ls = lotteryGameResultsMapper.selectByPrimaryKey(key);
	    	//System.out.println("0---sd--------"+ls.getAscc()+".."+ls.getLgrsid());
	    }
		    lotteryRoomDetailMapper.insertSelective(lrd);
	    	//System.out.println("123-3444----");
     }
		
	/**
	 * 结果处理4,试玩
	 * @throws Exception 
	 */
	public synchronized void LotteryResultHandle4(LotteryGameRound lottery) throws Exception{
		java.util.Random random=new java.util.Random();// 定义随机类
		//int result=random.nextInt(10);// 返回[0,10)集合中的整数，注意不包括10 
		int result;
		
	    List<LotteryRoom> list = lotteryRoomMapper.selectDistinctSid(lottery.getSid());
	    int []numbers = {50,100,200};
	    int []players = {992,993,994};
	    //order.setOrderamount(BigDecimal.valueOf(numbers[random.nextInt(numbers.length)]));
	    for (int i=0;i<list.size();i++){
	       LotteryGameOrder order = new LotteryGameOrder();
    	   for (int m=988;m<=players[random.nextInt(players.length)];m++){
	   	    	order.setAccountid(m);
		    	//for (int j=0;j<10;j++){
			    	LotteryRoom lr = new LotteryRoom();
			    	lr = list.get(i);
			    	order.setSid(lr.getSid());
			    	order.setRmid(lr.getRmid());
			    	if (String.valueOf(lr.getRmid()).substring(1, 2).equals("1"))
			    	    result = random.nextInt(9);
			        else 
			        	result = random.nextInt(4);
		    	    		
			    	order.setLtdid(result+1);
			    	order.setNoid(result+1);
			    	order.setPlayoridle("2");
			    	//order.setPlayoridle(String.valueOf(random.nextInt(2)+1));
			    	order.setLotteryterm(lottery.getLotteryterm());
			    	//if (order.getPlayoridle().equals("1"))
			    	 //   order.setOrderamount(BigDecimal.valueOf(20000));
			    	//else
			    	order.setOrderamount(BigDecimal.valueOf(numbers[random.nextInt(numbers.length)]));
			    	order.setOrdertime(new Date());
			    	order.setOpentime(lottery.getOpentime());
			        lotteryGameOrderMapper.insertSelective(order);
		    	//}
		    }
	    }
	}
	
	/**
	 * 上庄处理
	 * @throws Exception 
	 */
	public synchronized void LotteryPlayoridleHandle(LotteryGameRound lottery) throws Exception{
		LotteryOrderRecord record = new LotteryOrderRecord();
		record.setLotteryterm(lottery.getLotteryterm());
		record.setSid(lottery.getSid());
		System.out.println("time----------"+CommonUtils.getStringToMillon(lottery.getOvertime().toString(),1*60));
		
		record.setFirsttime(CommonUtils.getStringToMillon(lottery.getOvertime().toString(),1*60));
		record.setAccoundids("1008,1009");
		LotteryOrderRecord lr = lotteryOrderRecordMapper.selectByKeyValue(lottery.getSid(), lottery.getLotteryterm());
		if (lr == null){
			lotteryOrderRecordMapper.insertSelective(record);
		}
		
	}
	
	
	/**
	 * 结果处理,增值服务对象
	 * @throws Exception 
	 */
	public synchronized void LotteryResultValue(LotteryGameRound lottery) throws Exception{
		LotteryOrderRecord record = new LotteryOrderRecord();
		record.setLotteryterm(lottery.getLotteryterm());
		record.setSid(lottery.getSid());
		System.out.println("time----------"+lottery.getOvertime()+".."+CommonUtils.getStringToMillon(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lottery.getOvertime()),1*60));
		
		record.setFirsttime(CommonUtils.getStringToMillon(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lottery.getOvertime()),1*40));
		record.setFirstvalue("0");
		record.setSecondvalue("0");
		List<LotteryAddAccount> list = lotteryAddAcountMapper.selectByOffAccount();
		java.util.Random random=new java.util.Random();// 定义随机类
		int result=random.nextInt(list.size())+1;
		String accountids = "";
		if ((list.size()-result-10)>=0) {
			for (int t = result;t<result+10;t++){
				accountids = accountids + "'"+list.get(t).getAccountid()+"',";
			}
		}else{
			int m = (result+10)-list.size();
			for (int t = result;t<list.size();t++){
				accountids = accountids + "'"+list.get(t).getAccountid()+"',";
			}
			for (int t = 0;t<m;t++)
				accountids = accountids + "'"+list.get(t).getAccountid()+"',";
		}
		//record.setAccoundids("1008,1009");
		accountids = accountids.substring(0, accountids.length()-1);
		record.setAccoundids(accountids);
		LotteryOrderRecord lr = lotteryOrderRecordMapper.selectByKeyValue(lottery.getSid(), lottery.getLotteryterm());
		if (lr == null){
			lotteryOrderRecordMapper.insertSelective(record);
		}
		
	}
		
	
	
	/**
	 * 增值服务,第二次
	 *@throws Exception 
	 ** 
	 */
	public synchronized void getSecondValueLotteryResult(LotteryGameRound lottery,Integer rmid) throws Exception{		
		//增值投注
		//增值
		String accountids = "";
		System.out.println("12-------------"+lottery.getSid()+".."+lottery.getLotteryterm());
        LotteryOrderRecord lor = lotteryOrderRecordMapper.selectByKeyValue(lottery.getSid(), lottery.getLotteryterm());
        if (lor==null){
        	return;
        }else{
        	accountids = lor.getAccoundids();
        }
		LotteryGameOrder order = new LotteryGameOrder();
		List<AddLotteryAmountDto> list = lotteryGameOrderMapper.selectByResultValue(lottery.getSid(),lottery.getLotteryterm(),rmid);
		int count = 0;
		int noid = 0;
		String[] lorslist = accountids.split(",");
		//for (int m = 0;m<lorslist.length;m++)
	    	//order.setAccountid(Integer.valueOf(lorslist[m]));
		    for (int i=0;i<list.size();i++){
		    	AddLotteryAmountDto lr = new AddLotteryAmountDto();
		    	lr = list.get(i);
		    	if (lr.getOrderno()==2&&rmid!=lr.getRmid()&&noid!=lr.getNoid()){
		    		count = count + (lr.getOrderamount().intValue());
		    		
		    	}else if (lr.getOrderno()==3&&rmid!=lr.getRmid()&&noid!=lr.getNoid()){
		    		count = count + (lr.getOrderamount().intValue());
		    		count = (int)(count/2);
		    		order.setSid(lr.getSid());
			    	order.setRmid(lr.getRmid());
			    	order.setLtdid(lr.getNoid());
			    	order.setNoid(lr.getNoid());
			    	order.setPlayoridle(lr.getPlayoridle());
			    	order.setLotteryterm(lottery.getLotteryterm());
			    	order.setOrderamount(BigDecimal.valueOf(count - (count%10)));
			    	order.setOrdertime(CommonUtils.getStringToMillon(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lottery.getOpentime()),65));
			    	order.setOpentime(lottery.getOpentime());
			    	List<LotteryGameOrder> lgs = lotteryGameOrderMapper.selectAddAccountIds(order.getSid(), order.getRmid(), order.getNoid(), order.getLotteryterm(), accountids);
			        if (lgs.size()!=0){
			        	System.out.println("89-567----"+lgs.size());
			        	for (int r=0;r<lgs.size();r++){
			        		LotteryGameOrder gos = new LotteryGameOrder();
			        		gos = lgs.get(r);
			        		System.out.println("89-----"+gos.getAccountid());
			        		order.setAccountid(gos.getAccountid());
					    	lotteryGameOrderMapper.insertSelective(order);
					    	accountInfoService.updateResultAccountMount(BigDecimal.valueOf(0).subtract(order.getOrderamount()), order.getAccountid());
			        	}
			        }

			        count = 0;
			        rmid = lr.getRmid();
			        noid = lr.getNoid();
		    	}
		    	
		    }
	}
	
	
	
		/**
		 * 结果处理是否有庄,无庄
		 * @throws Exception 
		 */
		public synchronized void LotteryIsOrNotHandle(String lotteryterm,Integer sid,Integer rmid,String addstate,Integer time) throws Exception{
			
			List<LotteryAmountDto> list =  lotteryGameOrderMapper.selectGameIsOrNotBank(lotteryterm, sid, rmid);
			if (list.size()>0)
				LotteryResultHandle6(lotteryterm,sid,rmid,addstate,time);	
			else 
				LotteryResultHandle5(lotteryterm,sid,rmid,addstate,time);	
		}
	
		/**
		 * 结果处理5,无庄比较
		 * @throws Exception 
		 */
		public synchronized void LotteryResultHandle5(String lotteryterm,Integer sid,Integer rmid,String addstate,Integer time) throws Exception{
			
			System.out.println("8--5----------"+lotteryterm+".."+sid+".."+rmid);
			List<LotteryAmountDto> list =  lotteryGameOrderMapper.selectGameAmountResult(lotteryterm, sid, rmid);
			List<LotteryAmountDto> list1 =  lotteryGameOrderMapper.selectGameAmountResult1(lotteryterm, sid, rmid);
		
			int m = list.size();			
			LotteryAmountDto last = null;
			String[][] str = new String[list.size()][11];
			String[][] str1 = new String[list.size()][11];
			String type = "";
		    for (int i = 0;i<list.size();i++){
		    	last = new LotteryAmountDto();
		    	last = list.get(i);
		    	//System.out.println("hello-------------"+last.getNoid()+".."+last.getOrderamount());
		    	str[i][0]=String.valueOf(last.getOrderamount()).replaceAll("\\.00", "");
		        str[i][1]="0";
		        str[i][2]="0";
		        str[i][3]=String.valueOf(last.getAccountid());
		        str[i][4]=String.valueOf(last.getLgmid());
		        str[i][5]="0";
		        str[i][6]=String.valueOf(last.getRatio()).replaceAll("\\.0", "");
		        str[i][7]="0";
		        str[i][8]=last.getResultvalue();	
		        str[i][9]=String.valueOf(last.getNoid());	
		        str[i][10] =String.valueOf(last.getAscc());
		       // System.out.println("123----444-----------"+str[i][10]);
		        if (i==0){
		        	type = last.getType();
		        }
		    }	
		    
		    for (int i = 0;i<list1.size();i++){
		    	last = new LotteryAmountDto();
		    	last = list1.get(i);
		    	//System.out.println("hello-------------"+last.getNoid()+".."+last.getOrderamount());
		    	str1[i][0]=String.valueOf(last.getOrderamount()).replaceAll("\\.00", "");
		    	str1[i][1]="0";
		    	str1[i][2]="0";
		    	str1[i][3]=String.valueOf(last.getAccountid());
		    	str1[i][4]=String.valueOf(last.getLgmid());
		    	str1[i][5]="0";
		    	str1[i][6]=String.valueOf(last.getRatio()).replaceAll("\\.0", "");
		    	str1[i][7]="0";
		    	str1[i][8]=last.getResultvalue();	
		    	str1[i][9]=String.valueOf(last.getNoid());	
		        str1[i][10] =String.valueOf(last.getAscc());
		    }
		    
		    
			SysCom sc = sysComMapper.selectByGameType(type);
			//System.out.println("123---------------"+str[1][10]);
			str = lotteryInfoService.doNoBankerHandle(str,str1,addstate,time);
			//str = CommonUtils.doNoBankerHandle(str);
			doTradeHandle(lotteryterm,sid,rmid,str,sc.getCommission(),addstate,time);
			
		    }
		

		/**
		 * 结果处理6,庄比较
		 * @throws Exception 
		 */
		public synchronized void LotteryResultHandle6(String lotteryterm,Integer sid,Integer rmid,String addstate,Integer time) throws Exception{
			
			//List<LotteryAmountDto> listMore =  lotteryGameOrderMapper.selectGameAmountMoreResult(lotteryterm, sid, rmid);
			//List<LotteryAmountDto> listLess =  lotteryGameOrderMapper.selectGameAmountLessResult(lotteryterm, sid, rmid);
			List<LotteryAmountDto> listEqual =  lotteryGameOrderMapper.selectGameAmountEqualResult(lotteryterm, sid, rmid);
			List<LotteryAmountDto> listNoid = null;
			
			int count = 0;
			int times = 0;
			int equalcount = 0;
			String[][] strEqual = new String[listEqual.size()][11];
			
			String type = "";
			int noid = 0;
			int noids = 0;
			int ascc = 0;
			int gains = 0;
			int values = 0;
			int single = 0;
			int ratio = 0;
			Map<Integer, Object> map = new HashMap<Integer, Object>();
			for (int j = 0;j<listEqual.size();j++){
				LotteryAmountDto lad = listEqual.get(j);
				count = count + (int)lad.getOrderamount().doubleValue();
				equalcount = equalcount + (int)lad.getOrderamount().doubleValue();
				if (j==0)
					times = Integer.valueOf(String.valueOf(lad.getRatio()).replaceAll("\\.0", ""));
				strEqual[j][0]=String.valueOf(lad.getOrderamount()).replaceAll("\\.00", "");
				strEqual[j][1]=String.valueOf(lad.getOrderamount()).replaceAll("\\.00", "");
				strEqual[j][2]="0";
				strEqual[j][3]=String.valueOf(lad.getAccountid());
				strEqual[j][4]=String.valueOf(lad.getLgmid());
				strEqual[j][5]="0";
				strEqual[j][6]="1";
				strEqual[j][7]="0";
				strEqual[j][8]=lad.getResultvalue();	
				strEqual[j][9]=lad.getPlayoridle();	
				strEqual[j][10]=String.valueOf(lad.getNoid());
		        if (j==0){
		        	type = lad.getType();
		        	noid = lad.getNoid();
		        	ascc = lad.getAscc();
		        	ratio = lad.getRatio().intValue();
		        }
			}
			System.out.println("567--------------------"+count);
			if (type.equals("01"))
				noids = 10;
			else if (type.equals("02"))
				noids = 5;
			values = count * 2;
			SysCom sc = sysComMapper.selectByGameType(type);
			for(int m=noid+1;m<=noids;m++){
				listNoid =  lotteryGameOrderMapper.selectGameAmountNoidResult(lotteryterm, sid, rmid,m);
				if (null == listNoid || listNoid.size() ==0){
					continue;
				}
				LotteryAmountDto la = listNoid.get(0);
				String[][] strNoid = new String[listNoid.size()][11];
				if (ascc == la.getAscc()||single==1){
					for (int j = 0;j<listNoid.size();j++){
						LotteryAmountDto lad = listNoid.get(j);
						strNoid[j][0]=String.valueOf(lad.getOrderamount()).replaceAll("\\.00", "");
						strNoid[j][1]=String.valueOf(lad.getOrderamount()).replaceAll("\\.00", "");
						strNoid[j][2]="0";
						strNoid[j][3]=String.valueOf(lad.getAccountid());
						strNoid[j][4]=String.valueOf(lad.getLgmid());
						strNoid[j][5]="0";
						strNoid[j][6]=String.valueOf(lad.getRatio()).replaceAll("\\.0", "");
						strNoid[j][7]="0";
						strNoid[j][8]=lad.getResultvalue();	
						strNoid[j][9]=lad.getPlayoridle();		
						strNoid[j][10]=String.valueOf(lad.getNoid());
					}
					map = CommonUtils.doBankerHandleSameEqual(gains,count,values,1,strNoid);
					gains = (int)map.get(1);
					count = (int)map.get(2);
					values = (int)map.get(3);
					String[][] str1 = (String[][])map.get(4);
					doTradeHandle(lotteryterm,sid,rmid,str1,sc.getCommission(),addstate,time);
					
				}else{
					if (ascc < la.getAscc()){
						for (int j = 0;j<listNoid.size();j++){
							LotteryAmountDto lad = listNoid.get(j);
							strNoid[j][0]=String.valueOf(lad.getOrderamount()).replaceAll("\\.00", "");
							strNoid[j][1]=String.valueOf(lad.getOrderamount()).replaceAll("\\.00", "");
							strNoid[j][2]=String.valueOf(lad.getOrderamount()).replaceAll("\\.00", "");
							strNoid[j][3]=String.valueOf(lad.getAccountid());
							strNoid[j][4]=String.valueOf(lad.getLgmid());
							strNoid[j][5]="0";
							strNoid[j][6]=String.valueOf(lad.getRatio()).replaceAll("\\.0", "");
							strNoid[j][7]="0";
							strNoid[j][8]=lad.getResultvalue();	
							strNoid[j][9]=lad.getPlayoridle();	
							strNoid[j][10]=String.valueOf(lad.getNoid());
						}
						map = lotteryInfoService.doBankerHandleLess(gains,count,values,ratio,strNoid,strEqual,addstate,time);
						gains = (int)map.get(1);
						count = (int)map.get(2);
						values = (int)map.get(3);
						String[][] str1 = (String[][])map.get(4);
						doTradeHandle(lotteryterm,sid,rmid,str1,sc.getCommission(),addstate,time);
						if ((gains+count) == values){
							single = 1;
							//break;
						}
					}else if (ascc > la.getAscc()){
						int laratio = 0;
						for (int j = 0;j<listNoid.size();j++){
							LotteryAmountDto lad = listNoid.get(j);
							strNoid[j][0]=String.valueOf(lad.getOrderamount()).replaceAll("\\.00", "");
							strNoid[j][1]=String.valueOf(lad.getOrderamount()).replaceAll("\\.00", "");
							strNoid[j][2]="0";
							strNoid[j][3]=String.valueOf(lad.getAccountid());
							strNoid[j][4]=String.valueOf(lad.getLgmid());
							strNoid[j][5]="0";
							strNoid[j][6]=String.valueOf(lad.getRatio()).replaceAll("\\.0", "");
							if (j==0)
								laratio = Integer.valueOf(strNoid[j][6]);
							strNoid[j][7]="0";
							strNoid[j][8]=lad.getResultvalue();	
							strNoid[j][9]=lad.getPlayoridle();		
							strNoid[j][10]=String.valueOf(lad.getNoid());
						}
						map = lotteryInfoService.doBankerHandleMore(gains,count,values,laratio,strNoid,strEqual,addstate,times);
						gains = (int)map.get(1);
						count = (int)map.get(2);
						values = (int)map.get(3);
						String[][] str1 = (String[][])map.get(4);
						doTradeHandle(lotteryterm,sid,rmid,str1,sc.getCommission(),addstate,time);
						if (count == 0){
							single = 1;
							//break;
						}
					}
				}
			}
			
			//if (single !=1)
			for(int m=1;m<noid;m++){
				listNoid =  lotteryGameOrderMapper.selectGameAmountNoidResult(lotteryterm, sid, rmid,m);
				if (null == listNoid || listNoid.size() ==0){
					continue;
				}
				LotteryAmountDto la = listNoid.get(0);
				String[][] strNoid = new String[listNoid.size()][11];
				if (ascc == la.getAscc()||single==1){
					for (int j = 0;j<listNoid.size();j++){
						LotteryAmountDto lad = listNoid.get(j);
						strNoid[j][0]=String.valueOf(lad.getOrderamount()).replaceAll("\\.00", "");
						strNoid[j][1]=String.valueOf(lad.getOrderamount()).replaceAll("\\.00", "");
						strNoid[j][2]="0";
						strNoid[j][3]=String.valueOf(lad.getAccountid());
						strNoid[j][4]=String.valueOf(lad.getLgmid());
						strNoid[j][5]="0";
						strNoid[j][6]=String.valueOf(lad.getRatio()).replaceAll("\\.0", "");
						strNoid[j][7]="0";
						strNoid[j][8]=lad.getResultvalue();	
						strNoid[j][9]=lad.getPlayoridle();		
						strNoid[j][10]=String.valueOf(lad.getNoid());
					}
					map = CommonUtils.doBankerHandleSameEqual(gains,count,values,1,strNoid);
					gains = (int)map.get(1);
					count = (int)map.get(2);
					values = (int)map.get(3);
					String[][] str1 = (String[][])map.get(4);
					doTradeHandle(lotteryterm,sid,rmid,str1,sc.getCommission(),addstate,time);
				}else{
					if (ascc < la.getAscc()){
						for (int j = 0;j<listNoid.size();j++){
							LotteryAmountDto lad = listNoid.get(j);
							strNoid[j][0]=String.valueOf(lad.getOrderamount()).replaceAll("\\.00", "");
							strNoid[j][1]=String.valueOf(lad.getOrderamount()).replaceAll("\\.00", "");
							strNoid[j][2]=String.valueOf(lad.getOrderamount()).replaceAll("\\.00", "");
							strNoid[j][3]=String.valueOf(lad.getAccountid());
							strNoid[j][4]=String.valueOf(lad.getLgmid());
							strNoid[j][5]="0";
							strNoid[j][6]=String.valueOf(lad.getRatio()).replaceAll("\\.0", "");
							strNoid[j][7]="0";
							strNoid[j][8]=lad.getResultvalue();	
							strNoid[j][9]=lad.getPlayoridle();	
							strNoid[j][10]=String.valueOf(lad.getNoid());
						}
						map = lotteryInfoService.doBankerHandleLess(gains,count,values,ratio,strNoid,strEqual,addstate,time);
						gains = (int)map.get(1);
						count = (int)map.get(2);
						values = (int)map.get(3);
						String[][] str1 = (String[][])map.get(4);
						doTradeHandle(lotteryterm,sid,rmid,str1,sc.getCommission(),addstate,time);
						if ((gains+count) == values){
							single = 1;
							//break;
						}
					}else if (ascc > la.getAscc()){
						int laratio = 0;
						for (int j = 0;j<listNoid.size();j++){
							LotteryAmountDto lad = listNoid.get(j);
							strNoid[j][0]=String.valueOf(lad.getOrderamount()).replaceAll("\\.00", "");
							strNoid[j][1]=String.valueOf(lad.getOrderamount()).replaceAll("\\.00", "");
							strNoid[j][2]="0";
							strNoid[j][3]=String.valueOf(lad.getAccountid());
							strNoid[j][4]=String.valueOf(lad.getLgmid());
							strNoid[j][5]="0";
							strNoid[j][6]=String.valueOf(lad.getRatio()).replaceAll("\\.0", "");
							if (j==0)
								laratio = Integer.valueOf(strNoid[j][6]);
							strNoid[j][7]="0";
							strNoid[j][8]=lad.getResultvalue();	
							strNoid[j][9]=lad.getPlayoridle();	
							strNoid[j][10]=String.valueOf(lad.getNoid());
						}
						map = lotteryInfoService.doBankerHandleMore(gains,count,values,laratio,strNoid,strEqual,addstate,time);
						gains = (int)map.get(1);
						count = (int)map.get(2);
						values = (int)map.get(3);
						String[][] str1 = (String[][])map.get(4);
						doTradeHandle(lotteryterm,sid,rmid,str1,sc.getCommission(),addstate,time);
						
						if (count == 0){
							single = 1;
							//break;
						}
					}
				}
			}
			
			String[][] strs = null;
		    if (map.size()!=0)
			    strs = CommonUtils.doBankerHandleEqual((int)map.get(1) +(int)map.get(2)-(int)(values/2), strEqual);
		    else
		    	strs = strEqual;
			//str1 = (String[][])map.get(2);
			doTradeHandle(lotteryterm,sid,rmid,strs,sc.getCommission(),addstate,time);
		  
		    }
		
		public synchronized void doTradeHandle(String lotteryterm,Integer sid,Integer rmid,String[][] str1,double ratio,String addstate,Integer time){
			for (int k = 0;k<str1.length;k++){
				BigDecimal fee = BigDecimal.valueOf(Integer.valueOf(str1[k][2]) * ratio);
				lotteryGameOrderMapper.updateOrderResult(Integer.valueOf(str1[k][4]), new Date(), str1[k][8], BigDecimal.valueOf(Integer.valueOf(str1[k][2])).subtract(fee.doubleValue()>0?fee:BigDecimal.valueOf(0)));
		        TradeInfo tr = new TradeInfo();
		        if ((String.valueOf(rmid).length()==7&&addstate.equals("0"))||(String.valueOf(rmid).length()==7&&addstate.equals("1")&&time==2)){

			        tr.setAccountid(Integer.valueOf(str1[k][3]));
			        tr.setTradetype(EnumType.TradeType.Trade.ID);
			        tr.setRelativeid(EnumType.RalativeType.Order.NOID);
			        tr.setRelativetype(EnumType.RalativeType.Order.ID);
			        tr.setTradeamount(Double.parseDouble(str1[k][0]));
			        tr.setInputtime(new Date());
			        tr.setRemark("会员返本金,游戏号:"+sid+",游戏期次:"+lotteryterm);
			        tradeInfoService.addInoutTradeInfo(tr);
			     if (fee.doubleValue()>0){
			    	tr.setAccountid(Integer.valueOf(str1[k][3])); 
			    	tr.setTradetype(EnumType.TradeType.Trade.ID);
			        tr.setRelativeid(EnumType.RalativeType.PlayerWin.NOID);
			        tr.setRelativetype(EnumType.RalativeType.PlayerWin.ID);
			        tr.setTradeamount(Double.parseDouble(str1[k][2])-(fee.doubleValue()));
			        tr.setInputtime(new Date());
			        tr.setRemark("盈利金额,游戏号:"+sid+",游戏期次:"+lotteryterm);
			        tradeInfoService.addInoutTradeInfo(tr);
		         }else{
		        	tr.setAccountid(Integer.valueOf(str1[k][3]));
		        	tr.setTradetype(EnumType.TradeType.Trade.ID);
			        tr.setRelativeid(EnumType.RalativeType.PlayerWin.NOID);
			        tr.setRelativetype(EnumType.RalativeType.PlayerWin.ID);
			        tr.setTradeamount(Double.parseDouble(str1[k][2]));
			        tr.setRemark("输赢金额,游戏号:"+sid+",游戏期次:"+lotteryterm);
			        tr.setInputtime(new Date());
			        tradeInfoService.addInoutTradeInfo(tr);
		        }
		        
			    if (str1[k][3].length()>=4){
			        AccountAmount aa = new AccountAmount();
			        aa.setAccountid(Integer.valueOf(str1[k][3]));
			        aa.setSid(sid);
			        aa.setLotteryterm(lotteryterm);
			        aa.setLoss(fee.doubleValue()<0?BigDecimal.valueOf(Math.abs(Integer.valueOf(str1[k][2]))):BigDecimal.valueOf(0));
			        aa.setEarns(fee.doubleValue()>0?BigDecimal.valueOf(Double.parseDouble(str1[k][2])):BigDecimal.valueOf(0));
			        aa.setGains(fee.doubleValue()>0?BigDecimal.valueOf(fee.doubleValue()):BigDecimal.valueOf(0));
			        aa.setCfee(BigDecimal.valueOf(0));
			        aa.setProfits(BigDecimal.valueOf(0));
			        aa.setStarttime(new Date());
			        aa.setOvertime(new Date());
			        accountAmountMapper.insert(aa);
			        //代理返钱
			        //System.out.println("ceshi-----"+tr.getTradeamount());
			        tradeInfoService.addAgencyTradeInfo(tr,fee.doubleValue()>0?fee.doubleValue():0,sid,lotteryterm,ratio,aa);
			    }
		        }
			}
		}
	
	
		/**
		 * 获取取现结果
		 * @throws Exception 
		 */
		@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
		public synchronized void getCashResult() throws Exception{
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
			if (hourOfDay >= 6 && hourOfDay <= 22) {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				
		        HttpPost postMethod = new HttpPost(outResultUrl);
		        HttpResponse resp = httpClient.execute(postMethod);
		        String str = EntityUtils.toString(resp.getEntity(), "UTF-8");
		        System.out.println("返回取现结果："+str);
		        com.alibaba.fastjson.JSONObject respJSONObject = JSON.parseObject(str);
		        String returnStr = respJSONObject.getString("data");
		        if (returnStr.equals("success")){
		        	System.out.println("取现状态查询：success");
		        }
			}
		}
		@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
		public void closeCQLottery() {
				String lotteryType = EnumType.LotteryType.CQ.ID;
				if(lotteryRoundService.closeLotteryRound(lotteryType)){
					log.info("游戏"+lotteryType+"封盘成功!");
				}else{
					log.info("游戏"+lotteryType+"封盘失败!");
				}
		}
		
		@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
		public void closeGDLottery() {
				String lotteryType = EnumType.LotteryType.GD.ID;
				if(lotteryRoundService.closeLotteryRound(lotteryType)){
					log.info("游戏"+lotteryType+"封盘成功!");
				}else{
					log.info("游戏"+lotteryType+"封盘失败!");
				}
		}
		
		@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
		public void closeTJLottery() {
				String lotteryType = EnumType.LotteryType.TJ.ID;
				if(lotteryRoundService.closeLotteryRound(lotteryType)){
					log.info("游戏"+lotteryType+"封盘成功!");
				}else{
					log.info("游戏"+lotteryType+"封盘失败!");
				}
		}
		
		public static void main(String args[]){
			//LotteryTaskService lt = new LotteryTaskService();
			System.out.println(ConfigUtils.getProperty("outResultUrl"));
		}
}
