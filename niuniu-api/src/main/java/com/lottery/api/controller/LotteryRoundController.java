package com.lottery.api.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.validation.constraints.Min;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dozer.Mapper;
import org.hibernate.validator.constraints.NotBlank;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lottery.api.dto.HisRoundParamVo;
import com.lottery.api.dto.RoundParamVo;
import com.lottery.api.dto.UpdateRoundVo;
import com.lottery.orm.bo.LotteryRound;
import com.lottery.orm.dao.CustomLotteryMapper;
import com.lottery.orm.dao.LotteryRoundMapper;
import com.lottery.orm.result.BaseRestResult;
import com.lottery.orm.result.HisRoundResult;
import com.lottery.orm.result.RoundResult;
import com.lottery.orm.service.LotteryOrderService;
import com.lottery.orm.service.LotteryRoundService;
import com.lottery.orm.util.EnumType;
import com.lottery.orm.util.HttpclientTool;
import com.lottery.orm.util.MessageTool;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@RequestMapping(value = "/lottery", produces = { "application/json;charset=UTF-8" })
@Api(value = "/lottery", description = "游戏信息接口")
@Controller
public class LotteryRoundController {
	public static final Logger LOG = Logger.getLogger(LotteryRoundController.class);

	@Autowired
	private Mapper mapper;

	@Autowired
	LotteryRoundMapper lotteryRoundMapper;

	@Autowired
	LotteryRoundService lotteryRoundService;

	@Autowired
	CustomLotteryMapper customLotteryMapper;

	@Autowired
	LotteryOrderService lotteryOrderService;
	
	@Value("${lottery.apiUrlByDate.cqklsf}")
    private String apiUrlByDateCQ;
	
	@Value("${lottery.apiUrlByDate.gdklsf}")
    private String apiUrlByDateGD;
	
	@Value("${lottery.apiUrlByDate.tjklsf}")
    private String apiUrlByDateTJ;
	
	/*@ApiOperation(value = "新增一期游戏", notes = "新增游戏记录", httpMethod = "POST")
	@RequestMapping(value = "/addLotteryRound", method = RequestMethod.POST)
	@ResponseBody
	public RoundResult addLotteryRound(
			@ApiParam(value = "Json参数", required = true) @Validated @RequestBody RoundParamVo param) throws Exception {
		RoundResult result = new RoundResult();
		try {
			LotteryRound round = mapper.map(param, LotteryRound.class);
			round.setLotterytype(param.getLotteryType());
			round.setRoundstatus(EnumType.RoundStatus.Open.ID);
			round.setOpentime(param.getOpenTime());
			if (lotteryRoundService.addLotteryRound(round)) {
				result.success(round);
			} else {
				result.fail(MessageTool.FailCode);
			}
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.fail(MessageTool.ErrorCode);
			LOG.error(e.getMessage(), e);
		}
		return result;
	}*/

	@ApiOperation(value = "获取游戏及赔率信息", notes = "获取游戏及赔率信息", httpMethod = "POST")
	@RequestMapping(value = "/getLotteryRound/{lotteryType}", method = RequestMethod.POST)
	@ResponseBody
	public RoundResult getLotteryRound(@NotBlank(message = "游戏类型不能为空") @PathVariable String lotteryType) throws Exception {
		RoundResult result = new RoundResult();
		try {
			Integer roundId = customLotteryMapper.selectCurrentRoundId(lotteryType);
			if (roundId == null) {
				result.fail("目前没有游戏信息");
			} else {
				LotteryRound lotteryRound = customLotteryMapper.selectRoundByRoundId(roundId);
				if (lotteryRound != null) {
					result.success(lotteryRound);
				} else {
					result.fail(MessageTool.FailCode);
				}
			}
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.fail(MessageTool.ErrorCode);
			LOG.error(e.getMessage(), e);
		}
		return result;
	}
	
	@ApiOperation(value = "获取历史游戏信息", notes = "获取历史游戏信息", httpMethod = "POST")
	@RequestMapping(value = "/getHistoryRound", method = RequestMethod.POST)
	@ResponseBody
	public HisRoundResult getHistoryRound(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody HisRoundParamVo param) throws Exception {
		HisRoundResult result = new HisRoundResult();
		try {
				List<LotteryRound> roundList = customLotteryMapper.selectByHistoryRound(param.getLotteryType(), EnumType.RoundStatus.End.ID, param.getBeginRow(), param.getPageSize());
				if (roundList != null) {
					result.success(roundList);
				} else {
					result.fail(MessageTool.FailCode);
				}
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.fail(MessageTool.ErrorCode);
			LOG.error(e.getMessage(), e);
		}
		return result;
	}

	@ApiOperation(value = "游戏封盘", notes = "游戏封盘", httpMethod = "POST")
	@RequestMapping(value = "/closeLotteryRound/{roundId}", method = RequestMethod.POST)
	@ResponseBody
	public RoundResult closeLotteryRound(@Min(value = 0, message = "ID格式不正确") @PathVariable Integer roundId) throws Exception {
		RoundResult result = new RoundResult();
		try {
			LotteryRound lotteryRound = new LotteryRound();
			lotteryRound.setRoundid(roundId);
			lotteryRound.setRoundstatus(EnumType.RoundStatus.Close.ID);
			if (lotteryRoundMapper.updateByPrimaryKeySelective(lotteryRound) > 0) {
				result.success();
			} else {
				result.fail(MessageTool.FailCode);
			}
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.fail(MessageTool.ErrorCode);
			LOG.error(e.getMessage(), e);
		}
		return result;
	}

	@ApiOperation(value = "兑奖并结束游戏", notes = "兑奖并结束游戏", httpMethod = "POST")
	@RequestMapping(value = "/endLotteryRound", method = RequestMethod.POST)
	@ResponseBody
	public BaseRestResult endLotteryRound(
			@ApiParam(value = "Json参数", required = true) @Validated @RequestBody UpdateRoundVo param) throws Exception {
		BaseRestResult result = new BaseRestResult();
		try {
			String openResult = "";
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String apiUrlByDate = "";
			if(param.getLotteryType().equals(EnumType.LotteryType.CQ.ID)){
				apiUrlByDate = apiUrlByDateCQ;
			}else if(param.getLotteryType().equals(EnumType.LotteryType.GD.ID)){
				apiUrlByDate = apiUrlByDateGD;
			}else if(param.getLotteryType().equals(EnumType.LotteryType.TJ.ID)){
				apiUrlByDate = apiUrlByDateTJ;
			}else{
				result.fail("不支持该游戏类型!");
				return result;
			}
			String apiResult = HttpclientTool.get(apiUrlByDate+param.getDate());
			if(StringUtils.isNotBlank(apiResult)&&apiResult.trim().startsWith("{")){
				JSONObject jObj = new JSONObject(apiResult);
				if(jObj.has("data")){
					JSONArray openArray = jObj.getJSONArray("data");//更新游戏开奖结果
					for(int i=0; i<openArray.length(); i++){
						JSONObject openObj = openArray.getJSONObject(i);
						LotteryRound openRound = customLotteryMapper.selectRoundByTypeAndTerm(param.getLotteryType(), openObj.getString("expect"));
						if(openRound!=null&&!openRound.getRoundstatus().equals(EnumType.RoundStatus.End.ID)){
							int endFlag = lotteryRoundService.endLotteryRound(openRound, openObj.getString("opencode"), format.parse(openObj.getString("opentime")));
							if(endFlag>0){
								lotteryRoundService.prizeLotteryRound(openRound);
							}
							openResult += openRound.getLotteryterm() + ",";
							LOG.info("更新开奖信息:"+openRound.getLotteryterm());
						}
					}
					result.success();
					result.setMessage("成功开奖期次:"+openResult);
				}else{
					result.fail();
				}
				
			}else{
				result.fail();
			}
			
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.fail(MessageTool.ErrorCode);
			LOG.error(e.getMessage(), e);
		}
		return result;
	}

}
