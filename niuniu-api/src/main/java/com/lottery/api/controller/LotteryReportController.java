package com.lottery.api.controller;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lottery.api.dto.AgencyWinReVo;
import com.lottery.api.dto.AgencyWinVo;
import com.lottery.api.dto.PlayTradeVo;
import com.lottery.api.dto.ReportParamVo;
import com.lottery.api.dto.RoomParamVo;
import com.lottery.api.dto.TradeReportVo;
import com.lottery.orm.bo.AccountDetail;
import com.lottery.orm.bo.AccountInfo;
import com.lottery.orm.dao.AccountDetailMapper;
import com.lottery.orm.dao.AccountInfoMapper;
import com.lottery.orm.dao.CustomLotteryMapper;
import com.lottery.orm.dao.LotteryReportMapper;
import com.lottery.orm.dao.LotteryRoomDetailMapper;
import com.lottery.orm.dto.AccAmountDto;
import com.lottery.orm.dto.AgencyWinReportDto;
import com.lottery.orm.dto.InoutAccReportDto;
import com.lottery.orm.dto.InoutReportDto;
import com.lottery.orm.dto.PlayerWinReportDto;
import com.lottery.orm.dto.ProAccAmountDto;
import com.lottery.orm.dto.QueryDateDto;
import com.lottery.orm.dto.QueryRoomDateDto;
import com.lottery.orm.dto.RoomHisOrderDto;
import com.lottery.orm.dto.TradeReportDto;
import com.lottery.orm.result.AccWinReportResult;
import com.lottery.orm.result.AgencyWinReportResult;
import com.lottery.orm.result.InoutAccReportResult;
import com.lottery.orm.result.InoutReportResult;
import com.lottery.orm.result.OrderListResult;
import com.lottery.orm.result.PlayerWinReportResult;
import com.lottery.orm.result.QueryDateResult;
import com.lottery.orm.result.RoomListResult;
import com.lottery.orm.result.TradeReportResult;
import com.lottery.orm.service.LotteryOrderService;
import com.lottery.orm.service.LotteryReportService;
import com.lottery.orm.util.CommonUtils;
import com.lottery.orm.util.EnumType;
import com.lottery.orm.util.MessageTool;
import com.lottery.orm.util.QueryTool;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@RequestMapping(value = "/report", produces = { "application/json;charset=UTF-8" })
@Api(value = "/report", description = "报表信息接口")
@Controller
public class LotteryReportController {
	public static final Logger LOG = Logger.getLogger(LotteryReportController.class);

	@Autowired
	LotteryReportMapper lotteryReportMapper;
	
	@Autowired
	CustomLotteryMapper customLotteryMapper;
	
	@Autowired
	AccountDetailMapper accountDetailMapper;
	
	@Autowired
	AccountInfoMapper accountInfoMapper;
	
	@Autowired
	LotteryRoomDetailMapper lotteryRoomDetailMapper;
	
	@Autowired
	LotteryOrderService lotteryOrderService;
	
	@Autowired
	LotteryReportService lotteryReportService;
	
	@Value("${lottery.initDate}")
    private String initDate;
	
	
	@ApiOperation(value = "获取本房战绩", notes = "获取本房战绩", httpMethod = "POST")
	@RequestMapping(value = "/getRoomQueryDate", method = RequestMethod.POST)
	@ResponseBody
	public RoomListResult getRoomQueryDate(
			@ApiParam(value = "Json参数", required = true) @Validated @RequestBody RoomParamVo param) throws Exception {
		RoomListResult result = new RoomListResult();
		try {
			DateTime initDate = new DateTime(this.initDate);
			Date startTime = QueryTool.getPeroidStartTime(initDate.toDate());
			Date[] param1 = CommonUtils.getDateTime(param.getStartDate(), param.getEndDate());
			List<QueryRoomDateDto> list = lotteryOrderService.selectRoomResult(param1[0], param1[1], param.getTime(), param.getSid(), param.getRmid(), param.getAccountid(), param.getBeginRow(), param.getPageSize());
			result.success(list);
		    LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(), e);
		}
		return result;

	}
	
	
	/*
	@ApiOperation(value = "获取当期日期", notes = "获取当期日期", httpMethod = "POST")
	@RequestMapping(value = "/getQueryDate", method = RequestMethod.POST)
	@ResponseBody
	public QueryDateResult getCurPeriodDate() throws Exception {
		QueryDateResult result = new QueryDateResult();
		try {
				DateTime initDate = new DateTime(this.initDate);
				Date startTime = QueryTool.getPeroidStartTime(initDate.toDate());
				Date endTime = (new DateTime(startTime)).plusDays(27).toDate();
				QueryDateDto queryDate = new QueryDateDto();
				queryDate.setStartDate(startTime);
				queryDate.setEndDate(endTime);
				result.success(queryDate);
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(), e);
		}
		return result;

	}
*/
	/*
	@ApiOperation(value = "获取玩家输赢报表", notes = "获取输赢报表", httpMethod = "POST")
	@RequestMapping(value = "/getPlayerWinReport", method = RequestMethod.POST)
	@ResponseBody
	public PlayerWinReportResult getPlayerWinReport(
			@ApiParam(value = "Json参数", required = true) @Validated @RequestBody ReportParamVo param) throws Exception {
		PlayerWinReportResult result = new PlayerWinReportResult();
		try {
			Date startTime = param.getStartTime();
			Date endTime = param.getEndTime();
			if(param.getIsCurPeriod()){
				DateTime initDate = new DateTime(this.initDate);
				startTime = QueryTool.getPeroidStartTime(initDate.toDate());
				endTime = (new DateTime(startTime)).plusDays(27).toDate();
			}
			AccountDetail accountDetail = accountDetailMapper.selectByPrimaryKey(param.getAccountId());
			if(accountDetail.getOfftype().equals(EnumType.OffType.Admin.ID)||accountDetail.getOfftype().equals(EnumType.OffType.Agency.ID)){
				List<PlayerWinReportDto> accountList = lotteryReportMapper.selectWinReportByPlayer(startTime,
							endTime, accountDetail.getUsername(), param.getBeginRow(), param.getPageSize());
				
				result.success(accountList);
			}else if(accountDetail.getOfftype().equals(EnumType.OffType.Sub.ID)){
				List<PlayerWinReportDto> accountList = lotteryReportMapper.selectWinReportByPlayer(startTime,
						endTime, accountDetail.getUsername(), param.getBeginRow(), param.getPageSize());
				result.success(accountList);
			}else{
				result.fail("该账户无权限查询");
			}
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(), e);
		}
		return result;

	}
	*/
	/*
	@ApiOperation(value = "获取代理输赢报表", notes = "获取输赢报表", httpMethod = "POST")
	@RequestMapping(value = "/getAgencyWinReport", method = RequestMethod.POST)
	@ResponseBody
	public AgencyWinReportResult getAgencyWinReport(
			@ApiParam(value = "Json参数", required = true) @Validated @RequestBody AgencyWinVo param) throws Exception {
		AgencyWinReportResult result = new AgencyWinReportResult();
		try {
			Date startTime = param.getStartTime();
			Date endTime = param.getEndTime();
			if(param.getIsCurPeriod()){
				DateTime initDate = new DateTime(this.initDate);
				startTime = QueryTool.getPeroidStartTime(initDate.toDate());
				endTime = (new DateTime(startTime)).plusDays(27).toDate();
			}
			AccountDetail accountDetail = accountDetailMapper.selectByPrimaryKey(param.getAccountId());
			if(accountDetail.getOfftype().equals(EnumType.OffType.Admin.ID)||accountDetail.getOfftype().equals(EnumType.OffType.Agency.ID)){
				List<AgencyWinReportDto> accountList = lotteryReportMapper.selectWinReportByAgency(startTime,
						endTime, accountDetail.getUsername(), param.getLevel(), param.getBeginRow(), param.getPageSize());
				result.success(accountList);
			}else if(accountDetail.getOfftype().equals(EnumType.OffType.Sub.ID)){
				List<AgencyWinReportDto> accountList = lotteryReportMapper.selectWinReportByAgency(startTime,
						endTime, accountDetail.getUsername(), param.getLevel(), param.getBeginRow(), param.getPageSize());
				result.success(accountList);
			}else{
				result.fail("该账户无权限查询");
			}
			
			
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(), e);
		}
		return result;

	}
*/
	@ApiOperation(value = "获取代理输赢报表", notes = "获取代理输赢报表", httpMethod = "POST")
	@RequestMapping(value = "/getAgencyWinReport", method = RequestMethod.POST)
	@ResponseBody
	public AgencyWinReportResult getAgencyWinReport(
			@ApiParam(value = "Json参数", required = true) @Validated @RequestBody AgencyWinReVo param) throws Exception {
		AgencyWinReportResult result = new AgencyWinReportResult();
		try {	
			Date[] param1 = CommonUtils.getDateTime(param.getStartTime(), param.getEndTime());
			List<ProAccAmountDto> list = lotteryReportService.getProAccWinReport(param1[0], param1[1], param.getAccountId(), param.getLevel(), param.getBeginRow(), param.getPageSize());		
			result.success(list);	
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(), e);
		}
		return result;
	}
	
	@ApiOperation(value = "获取会员输赢报表", notes = "获取会员输赢报表", httpMethod = "POST")
	@RequestMapping(value = "/getAccWinReport", method = RequestMethod.POST)
	@ResponseBody
	public AccWinReportResult getAccWinReport(
			@ApiParam(value = "Json参数", required = true) @Validated @RequestBody AgencyWinReVo param) throws Exception {
		AccWinReportResult result = new AccWinReportResult();
		try {	
			Date[] param1 = CommonUtils.getDateTime(param.getStartTime(), param.getEndTime());
			List<AccAmountDto> list = lotteryReportService.getAccWinReport(param1[0], param1[1], param.getAccountId(), param.getLevel(), param.getBeginRow(), param.getPageSize());		
			result.success(list);	
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(), e);
		}
		return result;
	}
	
	@ApiOperation(value = "获取交易报表", notes = "获取交易报表", httpMethod = "POST")
	@RequestMapping(value = "/getAccTradeReport", method = RequestMethod.POST)
	@ResponseBody
	public OrderListResult getAccTradeReport(
			@ApiParam(value = "Json参数", required = true) @Validated @RequestBody PlayTradeVo param) throws Exception {
		OrderListResult result = new OrderListResult();
		try {	
			AccountInfo aInfo = accountInfoMapper.selectByUsername(param.getUsername());
			Date[] param1 = CommonUtils.getDateTime(param.getStartTime(), param.getEndTime());
			if (aInfo == null){
			      result.fail(MessageTool.Code_1001);
			      LOG.info(result.getMessage());
			      return result;
			} 
			List<RoomHisOrderDto> orderList = lotteryOrderService.getLotteryHisAllOrder(param1[0], param1[1],aInfo.getAccountid(), 9999, param.getBeginRow(), param.getPageSize());
			LOG.info(result.getMessage());
			result.success(orderList);	
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(), e);
		}
		return result;
	}
	
	
	@ApiOperation(value = "获取玩家点数出入报表", notes = "获取玩家点数出入报表", httpMethod = "POST")
	@RequestMapping(value = "/getPlayerInoutReport", method = RequestMethod.POST)
	@ResponseBody
	public InoutAccReportResult getPlayerInoutReport(
			@ApiParam(value = "Json参数", required = true) @Validated @RequestBody AgencyWinReVo param) throws Exception {
		InoutAccReportResult result = new InoutAccReportResult();
		try {
			Date[] param1 = CommonUtils.getDateTime(param.getStartTime(), param.getEndTime());
			System.out.println("9---------------"+param1[0]+"..."+param1[1]);
			List<InoutAccReportDto> list = lotteryReportService.selectAccInoutReport(param1[0], param1[1], param.getAccountId(), param.getLevel(),param.getBeginRow(), param.getPageSize());		
			result.success(list);	
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(), e);
		}
		return result;
	}
	
	
	
	/*
	@ApiOperation(value = "获取玩家点数出入报表", notes = "获取玩家点数出入报表", httpMethod = "POST")
	@RequestMapping(value = "/getPlayerInoutReport", method = RequestMethod.POST)
	@ResponseBody
	public InoutReportResult getPlayerInoutReport(
			@ApiParam(value = "Json参数", required = true) @Validated @RequestBody ReportParamVo param) throws Exception {
		InoutReportResult result = new InoutReportResult();
		try {
			Date startTime = param.getStartTime();
			Date endTime = param.getEndTime();
			if(param.getIsCurPeriod()){
				DateTime initDate = new DateTime(this.initDate);
				startTime = QueryTool.getPeroidStartTime(initDate.toDate());
				endTime = (new DateTime(startTime)).plusDays(27).toDate();
			}
			AccountDetail accountDetail = accountDetailMapper.selectByPrimaryKey(param.getAccountId());
			if(accountDetail.getOfftype().equals(EnumType.OffType.Admin.ID)||accountDetail.getOfftype().equals(EnumType.OffType.Agency.ID)){
				List<InoutReportDto> accountList = lotteryReportMapper.selectByInoutReport(startTime,
						endTime, accountDetail.getUsername(), EnumType.OffType.Play.ID, param.getBeginRow(), param.getPageSize());
				result.success(accountList);
			}else if(accountDetail.getOfftype().equals(EnumType.OffType.Sub.ID)){
				List<InoutReportDto> accountList = lotteryReportMapper.selectByInoutReport(startTime,
						endTime, accountDetail.getUsername(), EnumType.OffType.Play.ID, param.getBeginRow(), param.getPageSize());
				result.success(accountList);
			}else{
				result.fail("该账户无权限查询");
			}
			
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(), e);
		}
		return result;

	}
	
	@ApiOperation(value = "获取下级代理点数出入报表", notes = "获取玩家点数出入报表", httpMethod = "POST")
	@RequestMapping(value = "/getAgencyInoutReport", method = RequestMethod.POST)
	@ResponseBody
	public InoutReportResult getAgencyInoutReport(
			@ApiParam(value = "Json参数", required = true) @Validated @RequestBody ReportParamVo param) throws Exception {
		InoutReportResult result = new InoutReportResult();
		try {
			Date startTime = param.getStartTime();
			Date endTime = param.getEndTime();
			if(param.getIsCurPeriod()){
				DateTime initDate = new DateTime(this.initDate);
				startTime = QueryTool.getPeroidStartTime(initDate.toDate());
				endTime = (new DateTime(startTime)).plusDays(27).toDate();
			}
			AccountDetail accountDetail = accountDetailMapper.selectByPrimaryKey(param.getAccountId());
			if(accountDetail.getOfftype().equals(EnumType.OffType.Admin.ID)||accountDetail.getOfftype().equals(EnumType.OffType.Agency.ID)){
				List<InoutReportDto> accountList = lotteryReportMapper.selectByInoutReport(startTime,
						endTime, accountDetail.getUsername(), EnumType.OffType.Agency.ID, param.getBeginRow(), param.getPageSize());
				result.success(accountList);
			}else if(accountDetail.getOfftype().equals(EnumType.OffType.Sub.ID)){
				List<InoutReportDto> accountList = lotteryReportMapper.selectByInoutReport(startTime,
						endTime, accountDetail.getUsername(), EnumType.OffType.Agency.ID, param.getBeginRow(), param.getPageSize());
				result.success(accountList);
			}else{
				result.fail("该账户无权限查询");
			}
			
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(), e);
		}
		return result;

	}
*/

	/*
	@ApiOperation(value = "获取交易报表", notes = "获取交易报表", httpMethod = "POST")
	@RequestMapping(value = "/getTradeReport", method = RequestMethod.POST)
	@ResponseBody
	public TradeReportResult getTradeReport(
			@ApiParam(value = "Json参数", required = true) @Validated @RequestBody TradeReportVo param) throws Exception {
		TradeReportResult result = new TradeReportResult();
		try {
			Date startTime = param.getStartTime();
			Date endTime = param.getEndTime();
			if(param.getIsCurPeriod()){
				DateTime initDate = new DateTime(this.initDate);
				startTime = QueryTool.getPeroidStartTime(initDate.toDate());
				endTime = (new DateTime(startTime)).plusDays(27).toDate();
			}
			AccountDetail accountDetail = accountDetailMapper.selectByPrimaryKey(param.getAccountId());
			if(accountDetail.getOfftype().equals(EnumType.OffType.Admin.ID)||accountDetail.getOfftype().equals(EnumType.OffType.Agency.ID)){
				List<TradeReportDto> accountList = lotteryReportMapper.selectByTradeReport(startTime,
						endTime, accountDetail.getUsername(), param.getPlayerUserName(), param.getBeginRow(), param.getPageSize());
				result.success(accountList);
			}else if(accountDetail.getOfftype().equals(EnumType.OffType.Sub.ID)){
				List<TradeReportDto> accountList = lotteryReportMapper.selectByTradeReport(startTime,
						endTime, accountDetail.getUsername(), param.getPlayerUserName(), param.getBeginRow(), param.getPageSize());
				result.success(accountList);
			}else{
				result.fail("该账户无权限查询");
			}
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(), e);
		}
		return result;

	}

*/
}
