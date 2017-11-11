package com.lottery.orm.service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lottery.orm.bo.AccountAmount;
import com.lottery.orm.bo.AccountInfo;
import com.lottery.orm.bo.LotteryGame;
import com.lottery.orm.bo.LotteryGameOrder;
import com.lottery.orm.bo.LotteryGameResults;
import com.lottery.orm.bo.LotteryGameRound;
import com.lottery.orm.bo.LotteryOrderRecord;
import com.lottery.orm.bo.LotteryRoom;
import com.lottery.orm.bo.LotteryRoomDetail;
import com.lottery.orm.bo.LotteryRound;
import com.lottery.orm.bo.SysCom;
import com.lottery.orm.bo.TradeInfo;
import com.lottery.orm.dao.AccountAmountMapper;
import com.lottery.orm.dao.CustomLotteryMapper;
import com.lottery.orm.dao.LotteryGameMapper;
import com.lottery.orm.dao.LotteryGameOrderMapper;
import com.lottery.orm.dao.LotteryGameResultsMapper;
import com.lottery.orm.dao.LotteryGameRoundMapper;
import com.lottery.orm.dao.LotteryOrderRecordMapper;
import com.lottery.orm.dao.LotteryRoomDetailMapper;
import com.lottery.orm.dao.LotteryRoomMapper;
import com.lottery.orm.dao.SysComMapper;
import com.lottery.orm.dto.LotteryAmountDto;
import com.lottery.orm.service.LotteryRoundService;
import com.lottery.orm.util.CommonUtils;
import com.lottery.orm.util.EnumType;
import com.lottery.orm.util.HttpclientTool;
import com.lottery.orm.util.MessageTool;
import com.wordnik.swagger.annotations.ApiModelProperty;

@Service
@Transactional
public class LotteryTaskService {
	public final Logger log = Logger.getLogger(this.getClass());
	//private final String LOTTERY_API_URL = "http://c.apiplus.net/newly.do?token=ed91e13bc38ac8d1&code=cqklsf&format=json&extend=true";
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
	
	
	/**
	 * 获取重庆时时彩
	 * @throws Exception 
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
	public void getCQSSCLotteryResult() throws Exception{
		System.out.println("00--------------------"+lotteryApiUrlCQSSC1);
		getLotteryOriginResult1(EnumType.LotteryType.CQSSC.ID, lotteryApiUrlCQSSC1);
		
	}
	
	/**
	 * 获取天津时时彩
	 * @throws Exception 
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
	public void getTJSSCLotteryResult() throws Exception{
		System.out.println("00--------------------"+lotteryApiUrlTJSSC1);
		getLotteryOriginResult1(EnumType.LotteryType.TJSSC.ID, lotteryApiUrlTJSSC1);
		
	}
	
	/**
	 * 获取新疆时时彩
	 * @throws Exception 
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
	public void getXJSSCLotteryResult() throws Exception{
		//System.out.println("00--------------------"+lotteryApiUrlCQSSC2);
		getLotteryOriginResult1(EnumType.LotteryType.XJSSC.ID, lotteryApiUrlXJSSC1);
		
	}
	
	/**
	 * 获取云南时时彩
	 * @throws Exception 
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
	public void getYNSSCLotteryResult() throws Exception{
		//System.out.println("00--------------------"+lotteryApiUrlCQSSC2);
		getLotteryOriginResult1(EnumType.LotteryType.YNSSC.ID, lotteryApiUrlYNSSC1);
		
	}
	
	/**
	 * 获取黑龙江时时彩
	 * @throws Exception 
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
	public void getHLJSSCLotteryResult() throws Exception{
		System.out.println("00-HLJ-------------------"+lotteryApiUrlHLJSSC1);
		getLotteryOriginResult1(EnumType.LotteryType.HLJSSC.ID, lotteryApiUrlHLJSSC1);
		
	}
	
	/**
	 * 获取北京赛车时时彩
	 * @throws Exception 
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
	public void getBJSCLotteryResult() throws Exception{
		//System.out.println("00--------------------"+lotteryApiUrlCQSSC2);
		getLotteryOriginResult1(EnumType.LotteryType.BJSC.ID, lotteryApiUrlBJSC1);
		
	}
	
	/**
	 * 获取幸运飞艇时时彩
	 * @throws Exception 
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
	public void getXYFTLotteryResult() throws Exception{
		//System.out.println("00--------------------"+lotteryApiUrlCQSSC2);
		getLotteryOriginResult1(EnumType.LotteryType.XYFT.ID, lotteryApiUrlXYFT1);
		
	}
	
	/**
	 * 增值服务第一次投注
	 * @throws Exception 
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
	public synchronized void getFirstValueLotteryResult() throws Exception{
		int []numbers = {50,100,200};
        Random random = new Random();
        int index = random.nextInt(numbers.length);
        String accountids = "";
        List<LotteryOrderRecord> lor = lotteryOrderRecordMapper.selectValueList();
        if (lor.size() == 0){
        	return;
        }else{
        	for (int t = 0;t<lor.size();t++){
        		LotteryOrderRecord lrc = new LotteryOrderRecord();	
        		lrc = lor.get(t);
        	    accountids = lrc.getAccoundids();
		        //01：成功;02:失败
		        if (new Date().after(lrc.getFirsttime())&&!lrc.getFirstvalue().equals("01")){
			        LotteryGameOrder order = new LotteryGameOrder();
					List<LotteryGameOrder> list = lotteryGameOrderMapper.selectByNoResultValue(lrc.getSid(),lrc.getLotteryterm());   
			        String[] lorslist = accountids.split(",");
			        for (int m = 0;m<lorslist.length;m++)
				    	order.setAccountid(Integer.valueOf(lorslist[m]));
					    for (int i=0;i<list.size();i++){
					    	LotteryGameOrder lr = new LotteryGameOrder();
					    	lr = list.get(i);
					    	for (int j=1;j<=10;j++){
						    	order.setSid(lrc.getSid());
						    	order.setRmid(lr.getRmid());
						    	order.setLtdid(j);
						    	order.setNoid(j);
						    	order.setPlayoridle(lr.getPlayoridle());
						    	order.setLotteryterm(lrc.getLotteryterm());
						    	order.setOrderamount(BigDecimal.valueOf(random.nextInt(numbers.length)));
						    	order.setOrdertime(new Date());
						    	order.setOpentime(lr.getOpentime());
						        lotteryGameOrderMapper.insertSelective(order);
					    	}
					    }
			    }
	        	}
	   }
	  }
	
	
	/**
	 * 获取开奖结果
	 * @throws Exception 
	 */
	private synchronized void getLotteryOriginResult1(String lotteryType, String apiUrl) throws Exception {
		System.out.println("8--d-------------"+apiUrl+"..."+lotteryType);
		String result = HttpclientTool.get(apiUrl);
		//String result = "";
		System.out.println("8---------------"+result+"..."+lotteryType);
	/*
		result = "{\"success\":true,\"data\":[{\"preDrawCode\":\"69754\","
				+ "\"drawIssue\":\"20171105082\",\"drawTime\":\"2017/11/5 19:40:45\","
				+ "\"preDrawTime\":\"2017-11-05 19:30:45\",\"preDrawIssue\":\"20171105081\","
				+ "\"drawCount\":\"39\",\"totalCount\":\"120\"}]}";
          //result = "{\"sucess\":true,";
      */
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
			if ((lgr.getSid()==1001&&CommonUtils.dateRange())||lgr.getSid()==2001||lgr.getSid()==2002)
			    lgr.setStarttime(CommonUtils.getStringToMillon(jArray.getJSONObject(0).getString("preDrawTime"),4*60));
			else
				lgr.setStarttime(CommonUtils.getStringToMillon(jArray.getJSONObject(0).getString("preDrawTime"),9*60));
			lgr.setOvertime(CommonUtils.getStringToMillon(jArray.getJSONObject(0).getString("preDrawTime"),60));
			lgr.setOpentime(CommonUtils.getStringToDate(jArray.getJSONObject(0).getString("preDrawTime")));
			lgr.setClosetime(CommonUtils.getStringToMillon(jArray.getJSONObject(0).getString("preDrawTime"),80));
			try{
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
					for (int i = 0;i<list.size();i++){
						LotteryGameOrder lg = new LotteryGameOrder();
						lg = list.get(i);
						LotteryIsOrNotHandle(lgr.getLotteryterm(),lgr.getSid(),lg.getRmid());	
						//LotteryResultSecondValue3(gRound);
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
			if ((lgr.getSid()==1001&&CommonUtils.dateRange())||lgr.getSid()==2001||lgr.getSid()==2002)
			    lgr1.setStarttime(CommonUtils.getStringToMillon(jArray.getJSONObject(0).getString("drawTime"),4*60));
			else
			    lgr1.setStarttime(CommonUtils.getStringToMillon(jArray.getJSONObject(0).getString("drawTime"),9*60));
			lgr1.setOvertime(CommonUtils.getStringToMillon(jArray.getJSONObject(0).getString("drawTime"),1*60));
			lgr1.setOpentime(CommonUtils.getStringToDate(jArray.getJSONObject(0).getString("drawTime")));
			lgr1.setClosetime(CommonUtils.getStringToMillon(jArray.getJSONObject(0).getString("drawTime"),80));
			LotteryGameRound gRound1 = lotteryRoundService.getLotteryTermResult(Integer.valueOf(lotteryType), lgr1.getLotteryterm());
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
				System.out.println("9----hello-----"+lgr1.getSid()+".."+lr.getGamestarttime());
		        //结果处理1
				LotteryResultHandle1(lr);
				//试玩处理
				LotteryResultHandle4(lgr1);
				
				//增值服务
				//LotteryResultValue(lgr1);
			}
			}catch(Exception e){
				System.out.println(e);
				e.printStackTrace();
			}
			System.out.println("2----"+jObj.getJSONArray("data"));
			//System.out.println("1--"+jObj.getString("preDrawCode")+".."+jObj.getString("drawIssue")+".."+jObj.getString("preDrawIssue"));
			//JSONObject openObj = openArray.getJSONObject(i);
		}
	}
	
	/**
	 * 结果处理1,游戏的初始化
	 * @throws Exception 
	 */
	public void LotteryResultHandle1(LotteryGame lottery) throws Exception{
		//System.out.println("00--------------------"+lotteryApiUrlCQ);
		//getLotteryOriginResult(EnumType.LotteryType.CQ.ID, lotteryApiUrlCQ);
	    lotteryGameResultsMapper.insertBatch(lottery);
	}
	
	/**
	 * 结果处理2,更新结果
	 * @throws Exception 
	 */
	public void LotteryResultHandle2(LotteryGameRound lottery) throws Exception{
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
	    	lotteryGameResultsMapper.updateByPrimaryKeySelective(lr);
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
		LotteryGameOrder order = new LotteryGameOrder();
	    List<LotteryRoom> list = lotteryRoomMapper.selectDistinctSid();
	    for (int m=991;m<=999;m++){
	    	order.setAccountid(m);
		    for (int i=0;i<list.size();i++){
		    	for (int j=0;j<10;j++){
		    	LotteryRoom lr = new LotteryRoom();
		    	lr = list.get(i);
		    	order.setSid(lr.getSid());
		    	order.setRmid(lr.getRmid());
		    	result = random.nextInt(10);
		    	order.setLtdid(result+1);
		    	order.setNoid(result+1);
		    	order.setPlayoridle("2");
		    	order.setLotteryterm(lottery.getLotteryterm());
		    	order.setOrderamount(BigDecimal.valueOf(100));
		    	order.setOrdertime(new Date());
		    	order.setOpentime(lottery.getOpentime());
		        lotteryGameOrderMapper.insertSelective(order);
		    	}
		    }
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
		System.out.println("time----------"+CommonUtils.getStringToMillon(lottery.getOvertime().toString(),1*60));
		
		record.setFirsttime(CommonUtils.getStringToMillon(lottery.getOvertime().toString(),1*60));
		record.setAccoundids("1008,1009");
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
	public synchronized void getSecondValueLotteryResult(LotteryGameRound lottery) throws Exception{
		java.util.Random random=new java.util.Random();// 定义随机类
		//int result=random.nextInt(10);// 返回[0,10)集合中的整数，注意不包括10 
		
		//增值服务人员筛选
		
		//增值投注
		//增值
		String accountids = "";
        LotteryOrderRecord lor = lotteryOrderRecordMapper.selectByKeyValue(lottery.getSid(), lottery.getLotteryterm());
        if (lor==null){
        	return;
        }else{
        	accountids = lor.getAccoundids();
        }
		LotteryGameOrder order = new LotteryGameOrder();
		List<LotteryGameOrder> list = lotteryGameOrderMapper.selectByResultValue(lottery.getSid(),lottery.getLotteryterm(),accountids);
		int count = 0;
		String[] lorslist = accountids.split(",");
		for (int m = 0;m<lorslist.length;m++)
	    	order.setAccountid(Integer.valueOf(lorslist[m]));
		    for (int i=0;i<list.size();i++){
		    	LotteryGameOrder lr = new LotteryGameOrder();
		    	lr = list.get(i);
		    	if ((i+1)%2==0&&i>0){
		    		count = count + (lr.getOrderamount().intValue());
		    		order.setSid(lr.getSid());
			    	order.setRmid(lr.getRmid());
			    	order.setLtdid(lr.getLtdid());
			    	order.setNoid(lr.getNoid());
			    	order.setPlayoridle(lr.getPlayoridle());
			    	order.setLotteryterm(lottery.getLotteryterm());
			    	order.setOrderamount(BigDecimal.valueOf(count - count%10));
			    	order.setOrdertime(new Date());
			    	order.setOpentime(lottery.getOpentime());
			        lotteryGameOrderMapper.insertSelective(order);
			        count = 0;
		    	}else{
		    		count = count + (lr.getOrderamount().intValue());
		    	}
		    	
		    }
	}
	
	
	
		/**
		 * 结果处理是否有庄,无庄
		 * @throws Exception 
		 */
		public synchronized void LotteryIsOrNotHandle(String lotteryterm,Integer sid,Integer rmid) throws Exception{
			
			List<LotteryAmountDto> list =  lotteryGameOrderMapper.selectGameIsOrNotBank(lotteryterm, sid, rmid);
			if (list.size()>0)
				LotteryResultHandle6(lotteryterm,sid,rmid);	
			else 
				LotteryResultHandle5(lotteryterm,sid,rmid);	
		}
	
		/**
		 * 结果处理5,无庄比较
		 * @throws Exception 
		 */
		public synchronized void LotteryResultHandle5(String lotteryterm,Integer sid,Integer rmid) throws Exception{
			
			System.out.println("8--5----------"+lotteryterm+".."+sid+".."+rmid);
			List<LotteryAmountDto> list =  lotteryGameOrderMapper.selectGameAmountResult(lotteryterm, sid, rmid);
		
			int m = list.size();			
			LotteryAmountDto last = null;
			String[][] str = new String[list.size()][9];
			String type = "";
		    for (int i = 0;i<list.size();i++){
		    	last = new LotteryAmountDto();
		    	last = list.get(i);
		    	System.out.println("hello-------------"+last.getOrderamount());
		    	str[i][0]=String.valueOf(last.getOrderamount()).replaceAll("\\.00", "");
		        str[i][1]=String.valueOf(last.getOrderamount()).replaceAll("\\.00", "");
		        str[i][2]=String.valueOf(last.getOrderamount()).replaceAll("\\.00", "");
		        str[i][3]=String.valueOf(last.getAccountid());
		        str[i][4]=String.valueOf(last.getLgmid());
		        str[i][5]="0";
		        str[i][6]=String.valueOf(last.getRatio()).replaceAll("\\.0", "");
		        str[i][7]="0";
		        str[i][8]=last.getResultvalue();	
		        if (i==0){
		        	type = last.getType();
		        }
		    }	
		    
			SysCom sc = sysComMapper.selectByGameType(type);
			
			str = CommonUtils.doNoBankerHandle(str);
			Date now = new Date();
			BigDecimal fee = BigDecimal.valueOf(0);
		    for (int j = 0;j<list.size();j++){
		        //if (Integer.valueOf(str[j][2])>0)
		        	fee = BigDecimal.valueOf(Integer.valueOf(str[j][2]) * sc.getCommission());
		        //else 
		        //	fee = BigDecimal.valueOf(0);
		        	System.out.println("9-9-----------"+str[j][2]+".."+(fee.doubleValue()>0));
		        	System.out.println("9---"+sc+".."+type+".."+fee+"..");
		        lotteryGameOrderMapper.updateOrderResult(Integer.valueOf(str[j][4]), now, str[j][8], BigDecimal.valueOf(Integer.valueOf(str[j][2])).subtract(fee.doubleValue()>0?fee:BigDecimal.valueOf(0)));
		        TradeInfo tr = new TradeInfo();
		        if (fee.doubleValue()>0){
		       
		        tr.setAccountid(Integer.valueOf(str[j][3]));
		        tr.setTradetype(EnumType.TradeType.Trade.ID);
		        tr.setRelativeid(EnumType.RalativeType.Order.NOID);
		        tr.setRelativetype(EnumType.RalativeType.Order.ID);
		        tr.setTradeamount(Double.parseDouble(str[j][1]));
		        tr.setInputtime(new Date());
		        tr.setRemark("会员返本金");
		        tradeInfoService.addInoutTradeInfo(tr);
		      
			        tr.setRelativeid(EnumType.RalativeType.PlayerWin.NOID);
			        tr.setRelativetype(EnumType.RalativeType.PlayerWin.ID);
			        tr.setTradeamount(Double.parseDouble(str[j][2])-(fee.doubleValue()));
			        tr.setInputtime(new Date());
			        tr.setRemark("盈利金额");
			        tradeInfoService.addInoutTradeInfo(tr);
		        }else{
		        	tr.setAccountid(Integer.valueOf(str[j][3]));
		        	tr.setTradetype(EnumType.TradeType.Trade.ID);
			        tr.setRelativeid(EnumType.RalativeType.PlayerWin.NOID);
			        tr.setRelativetype(EnumType.RalativeType.PlayerWin.ID);
			        tr.setTradeamount(Double.parseDouble(str[j][2]));
			        tr.setInputtime(new Date());
		        }
		        
		        AccountAmount aa = new AccountAmount();
		        aa.setAccountid(Integer.valueOf(str[j][3]));
		        aa.setSid(sid);
		        aa.setLotteryterm(lotteryterm);
		        aa.setLoss(fee.doubleValue()<0?BigDecimal.valueOf(Math.abs(Integer.valueOf(str[j][2]))):BigDecimal.valueOf(0));
		        aa.setEarns(fee.doubleValue()>0?BigDecimal.valueOf(Double.parseDouble(str[j][2])-(fee.doubleValue())):BigDecimal.valueOf(0));
		        aa.setGains(fee.doubleValue()>0?BigDecimal.valueOf(fee.doubleValue()):BigDecimal.valueOf(0));
		        aa.setCfee(BigDecimal.valueOf(0));
		        aa.setProfits(BigDecimal.valueOf(0));
		        aa.setStarttime(new Date());
		        aa.setOvertime(new Date());
		        accountAmountMapper.insert(aa);
		        //代理返钱
		        System.out.println("ceshi-----"+tr.getTradeamount());
		        tradeInfoService.addAgencyTradeInfo(tr,fee.doubleValue()>0?fee.doubleValue():0,sid,lotteryterm,sc.getCommission());
		        
		    }
		    }
		
		private BigDecimal BigDecimal(int i) {
			// TODO Auto-generated method stub
			return null;
		}

		/**
		 * 结果处理6,庄比较
		 * @throws Exception 
		 */
		public synchronized void LotteryResultHandle6(String lotteryterm,Integer sid,Integer rmid) throws Exception{
			
			List<LotteryAmountDto> listMore =  lotteryGameOrderMapper.selectGameAmountMoreResult(lotteryterm, sid, rmid);
			List<LotteryAmountDto> listLess =  lotteryGameOrderMapper.selectGameAmountLessResult(lotteryterm, sid, rmid);
			List<LotteryAmountDto> listEqual =  lotteryGameOrderMapper.selectGameAmountEqualResult(lotteryterm, sid, rmid);
			
			int count = 0;
			int times = 0;
			int equalcount = 0;
			String[][] strMore = new String[listMore.size()][10];
			String[][] strLess = new String[listLess.size()][10];
			String[][] strEqual = new String[listEqual.size()][10];
			String type = "";
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
		        if (j==0){
		        	type = lad.getType();
		        }
			}
			
			for (int j = 0;j<listLess.size();j++){
				LotteryAmountDto lad = listLess.get(j);
				strLess[j][0]=String.valueOf(lad.getOrderamount()).replaceAll("\\.00", "");
				strLess[j][1]=String.valueOf(lad.getOrderamount()).replaceAll("\\.00", "");
				strLess[j][2]=String.valueOf(lad.getOrderamount()).replaceAll("\\.00", "");
				strLess[j][3]=String.valueOf(lad.getAccountid());
				strLess[j][4]=String.valueOf(lad.getLgmid());
				strLess[j][5]="0";
				strLess[j][6]=String.valueOf(lad.getRatio()).replaceAll("\\.0", "");
				strLess[j][7]="0";
				strLess[j][8]=lad.getResultvalue();	
				strLess[j][9]=lad.getPlayoridle();		
			}
			
			
			SysCom sc = sysComMapper.selectByGameType(type);
			System.out.println("9---"+sc+".."+type);
			map = CommonUtils.doBankerHandleLess(count, times, strLess);
			Date now = new Date();
			System.out.println("21---------------"+(int)map.get(1));
			BigDecimal fee = BigDecimal.valueOf(0);
			String[][] str1 = new String[listLess.size()][10];
			str1 = (String[][])map.get(2);
			for (int m=0;m<str1.length;m++){
				System.out.println("87-------------"+str1[m][2]);
			}
		    for (int j = 0;j<listLess.size();j++){
		        lotteryGameOrderMapper.updateOrderResult(Integer.valueOf(str1[j][4]), now, str1[j][8], BigDecimal.valueOf(Integer.valueOf(str1[j][2])));
		    	accountInfoService.updateResultAccountMount(BigDecimal.valueOf(Integer.valueOf(str1[j][2])), Integer.valueOf(str1[j][3]));   
		    }
			
			for (int j = 0;j<listMore.size();j++){
				LotteryAmountDto lad = listMore.get(j);
				System.out.println("78----"+lad.getLgmid());
				strMore[j][0]=String.valueOf(lad.getOrderamount()).replaceAll("\\.00", "");
				strMore[j][1]=String.valueOf(lad.getOrderamount()).replaceAll("\\.00", "");
				strMore[j][2]="0";
				strMore[j][3]=String.valueOf(lad.getAccountid());
				strMore[j][4]=String.valueOf(lad.getLgmid());
				strMore[j][5]="0";
				strMore[j][6]=String.valueOf(lad.getRatio()).replaceAll("\\.0", "");
				strMore[j][7]="0";
				strMore[j][8]=lad.getResultvalue();	
				strMore[j][9]=lad.getPlayoridle();		
			}
			
			for (int st= 0;st<strMore.length;st++)
				System.out.println("21--23--6-----------"+(int)map.get(1)+".."+strMore[st][2]+".."+strMore[st][9]+".."+strMore[st][3]+".."+strMore[st][8]);
			map = CommonUtils.doBankerHandleMore((int)map.get(1), times, strMore);
			str1 = (String[][])map.get(2);
			for (int m=0;m<str1.length;m++){
				System.out.println("8723-------------"+str1[m][2]+".."+str1[m][4]);
			}
		    for (int j = 0;j<listMore.size();j++){
		        if (Integer.valueOf(str1[j][2])>0)
		        	fee = BigDecimal.valueOf(Integer.valueOf(str1[j][2]) * sc.getCommission());
		        else 
		        	fee = BigDecimal.valueOf(0);
		        System.out.println("9-----------"+fee+"..."+str1[j][4]+".."+BigDecimal.valueOf(Integer.valueOf(str1[j][2])).subtract(fee));
		        lotteryGameOrderMapper.updateOrderResult(Integer.valueOf(str1[j][4]), now, str1[j][8], BigDecimal.valueOf(Integer.valueOf(str1[j][2])).subtract(fee));
		    	accountInfoService.updateResultAccountMount(BigDecimal.valueOf(Integer.valueOf(str1[j][2])).subtract(fee), Integer.valueOf(str1[j][3]));   
		    	accountInfoService.updateResultAccountMount(fee, 1000);
		    }
		
		    System.out.println("21--23----23---12------"+(int)map.get(1)+"..."+equalcount+".."+((int)map.get(1) - equalcount));
			str1 = CommonUtils.doBankerHandleEqual((int)map.get(1) - equalcount, strEqual);
			//str1 = (String[][])map.get(2);
			for (int m=0;m<str1.length;m++){
				System.out.println("8723-23------------"+str1[m][2]);
			}
		    for (int j = 0;j<listEqual.size();j++){
		        if (Integer.valueOf(str1[j][2])>0)
		        	fee = BigDecimal.valueOf(Integer.valueOf(str1[j][2]) * sc.getCommission());
		        else 
		        	fee = BigDecimal.valueOf(0);
		        lotteryGameOrderMapper.updateOrderResult(Integer.valueOf(str1[j][4]), now, str1[j][8], BigDecimal.valueOf(Integer.valueOf(str1[j][2])).subtract(fee));
		    	accountInfoService.updateResultAccountMount(BigDecimal.valueOf(Integer.valueOf(str1[j][2])).subtract(fee), Integer.valueOf(str1[j][3]));   
		    	accountInfoService.updateResultAccountMount(fee, 1000);
		    }
		  
		    }
	
	    /*
	     * 
		for (OrderDetailVo orderDetailVo : param.getOrderDetails()) {
			order.setOrdertime(new Date());
			order.setNoid(orderDetailVo.getNoId());
			order.setLtdid(orderDetailVo.getNoId());
			order.setOrderamount(orderDetailVo.getOrderAmount());
			order.setPlayoridle(orderDetailVo.getPlayOridle());
			
		List<LotteryGameResults> list = lotteryGameResultsMapper.selectGameResults(lottery.getSid(),lottery.getLotteryterm());
	    String[] com = new String[5];
	    
	    	    @ApiModelProperty(value = "账户ID", required = true)
	    @NotNull(message = "账户ID不能为空")
	    @Min(value=0, message = "账户ID格式不正确")
	    private Integer accountId;

	    @ApiModelProperty(value = "游戏ID", required = true)
	    private Integer sid;

	    @ApiModelProperty(value = "房间ID", required = true)
	    private Integer rmid;
	    
		@ApiModelProperty(value = "游戏期次", required = true)
	    private String lotteryTerm;
	    
	    @ApiModelProperty(value = "投注详情", required = true)
	*/
	
	
	/**
	 * 获取广西快乐十分开奖结果
	 * @throws Exception 
	 */

	public void getCQLotteryResult() throws Exception{
		//System.out.println("00--------------------"+lotteryApiUrlCQ);
		//getLotteryOriginResult(EnumType.LotteryType.CQ.ID, lotteryApiUrlCQ);
	}
	
	/**
	 * 获取广东快乐十分开奖结果
	 * @throws Exception 
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
	public void getGDLotteryResult() throws Exception{
		//getLotteryOriginResult(EnumType.LotteryType.GD.ID, lotteryApiUrlGD);
	}
	
	/**
	 * 获取天津快乐十分开奖结果
	 * @throws Exception 
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
	public void getTJLotteryResult() throws Exception{
		//getLotteryOriginResult(EnumType.LotteryType.TJ.ID, lotteryApiUrlTJ);
	}
	
	/**
	 * 获取开奖结果
	 * @throws Exception 
	 */
	private void getLotteryOriginResult(String lotteryType, String apiUrl) throws Exception {
			String result = HttpclientTool.get(apiUrl);
			if(StringUtils.isNotBlank(result)&&result.trim().startsWith("{")){
				JSONObject jObj = new JSONObject(result);
				//Date latestOpenTime = (new DateTime()).toDate();
				JSONObject latestOpenObj = null;
				if(jObj.has("open")){
					JSONArray openArray = jObj.getJSONArray("open");//更新游戏开奖结果
					for(int i=0; i<openArray.length(); i++){
						JSONObject openObj = openArray.getJSONObject(i);
						if(i==0){
							latestOpenObj = openObj;
						}
						LotteryRound openRound = customLotteryMapper.selectRoundByTypeAndTerm(lotteryType, openObj.getString("expect"));
						if(openRound!=null&&!openRound.getRoundstatus().equals(EnumType.RoundStatus.End.ID)){
							int endFlag = lotteryRoundService.endLotteryRound(openRound, openObj.getString("opencode"), format.parse(openObj.getString("opentime")));
							if(endFlag>0){
								lotteryRoundService.prizeLotteryRound(openRound);
							}
							log.info("更新游戏"+openRound.getLotterytype()+"开奖信息:"+openRound.getLotteryterm());
						}
					}
				}
				
				
				
				if(jObj.has("next")){
					JSONArray nextArray = jObj.getJSONArray("next");//获取下一轮游戏期次
					Date nextOpenTime = null;//最近的下一轮游戏
					JSONObject nextObj = null;
					for(int i=0; i<nextArray.length(); i++){
						JSONObject tempObj = nextArray.getJSONObject(i);
						String opentimeStr = tempObj.getString("opentime");
						Date tempOpenTime = format.parse(opentimeStr.replace("**", "01"));
						if(nextOpenTime==null||nextOpenTime.after(tempOpenTime)){
							nextOpenTime = tempOpenTime;
							nextObj = tempObj;
						}
					}
					if(nextObj!=null){
						String opentimeStr = nextObj.getString("opentime");
						Date opentime = format.parse(opentimeStr.replace("**", "01"));
						if(opentimeStr.contains("**")&&latestOpenObj!=null){//用于测试
							Date latestOpenTime = format.parse(latestOpenObj.getString("opentime"));
							opentime = (new DateTime(latestOpenTime)).plusMinutes(ROUND_INTERVAL_MINUTES).toDate();
						}
						addNextRound(lotteryType, nextObj.getString("expect"), opentime);
					}
				}else{
					/*if(latestOpenObj!=null){//当没有下期信息时用于测试
						DateTime latestOpenTime = new DateTime(format.parse(latestOpenObj.getString("opentime")));
						DateTime opentime = latestOpenTime.plusMinutes(ROUND_INTERVAL_MINUTES);
						Period period=new Period(latestOpenTime,opentime);
						String nextExpect = null;
						if(period.getDays()==0){
							nextExpect = String.valueOf(Long.parseLong(latestOpenObj.getString("expect"))+1);
						}else{
							nextExpect = String.valueOf(Integer.parseInt(opentime.toString("yyyyMMdd"))*1000+1);
						}
						addNextRound(nextExpect, opentime.toDate());
					}*/
					log.info("未获取到下期游戏信息！");
				}
				
				
			}else{
				log.error("获取的接口数据有问题!");
			}
	}
	
	private void addNextRound(String lotteryType, String term, Date opentime) throws Exception{
		LotteryRound nextRound = new LotteryRound();
		nextRound.setLotterytype(lotteryType);//玉米籽
		nextRound.setLotteryterm(term);//下一轮游戏期次
		nextRound.setOpentime(opentime);//预计开奖时间
		if(lotteryRoundService.addLotteryRound(nextRound)){
			log.info("新增一期游戏"+nextRound.getLotterytype()+":"+nextRound.getLotteryterm());
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
	
	 
}
