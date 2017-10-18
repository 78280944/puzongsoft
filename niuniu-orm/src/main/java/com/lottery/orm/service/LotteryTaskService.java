package com.lottery.orm.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.lottery.orm.bo.LotteryGameRound;
import com.lottery.orm.bo.LotteryRound;
import com.lottery.orm.dao.CustomLotteryMapper;
import com.lottery.orm.service.LotteryRoundService;
import com.lottery.orm.util.CommonUtils;
import com.lottery.orm.util.EnumType;
import com.lottery.orm.util.HttpclientTool;

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
		System.out.println("00--------------------"+lotteryApiUrlCQSSC2);
		getLotteryOriginResult1(EnumType.LotteryType.CQSSC.ID, lotteryApiUrlCQSSC1);
		
	}
	/**
	 * 获取开奖结果
	 * @throws Exception 
	 */
	private void getLotteryOriginResult1(String lotteryType, String apiUrl) throws Exception {
		String result = HttpclientTool.get(apiUrl);
		System.out.println("8---------------"+result);
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
			lgr.setLotteryterm(jArray.getString(5));
			lgr.setLotteryresult(CommonUtils.getArrayString(jArray.getString(1)));
			lgr.setStarttime(CommonUtils.getStringToDate(jArray.getString(4)));
			lgr.setOpentime(CommonUtils.getStringToDate(jArray.getString(4)));
			gRound = lotteryRoundService.getLotteryTermResult(lgr.getSid(), lgr.getLotteryterm());
			if (gRound!=null){
				if (gRound.getLotteryresult().equals("")){
					//更新结果
					
				}
			}else{
				
			}
			//本期
			
			System.out.println("2----"+jObj.getJSONArray("data"));
			//System.out.println("1--"+jObj.getString("preDrawCode")+".."+jObj.getString("drawIssue")+".."+jObj.getString("preDrawIssue"));
			//JSONObject openObj = openArray.getJSONObject(i);
		}
	}
	
	/**
	 * 获取广西快乐十分开奖结果
	 * @throws Exception 
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
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
