package com.lottery.api.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lottery.api.dto.EndOrderVo;
import com.lottery.api.dto.HisOrderVo;
import com.lottery.api.dto.OrderDetailVo;
import com.lottery.api.dto.OrderParamVo;
import com.lottery.api.dto.ReportParamVo;
import com.lottery.orm.bo.AccountDetail;
import com.lottery.orm.bo.LotteryItem;
import com.lottery.orm.bo.LotteryOrder;
import com.lottery.orm.bo.LotteryOrderDetail;
import com.lottery.orm.bo.LotteryRound;
import com.lottery.orm.dao.AccountDetailMapper;
import com.lottery.orm.dao.CustomLotteryMapper;
import com.lottery.orm.dao.LotteryOrderMapper;
import com.lottery.orm.dao.LotteryReportMapper;
import com.lottery.orm.dao.LotteryRoundMapper;
import com.lottery.orm.dto.HistoryOrderDto;
import com.lottery.orm.dto.LotteryOrderDto;
import com.lottery.orm.result.BaseRestResult;
import com.lottery.orm.result.HistoryOrderResult;
import com.lottery.orm.result.OrderResult;
import com.lottery.orm.service.LotteryOrderService;
import com.lottery.orm.util.EnumType;
import com.lottery.orm.util.MessageTool;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@RequestMapping(value = "/order", produces = { "application/json;charset=UTF-8" })
@Api(value = "/order", description = "投注信息接口")
@Controller
public class LotteryOrderController {
	public static final Logger LOG = Logger.getLogger(LotteryOrderController.class);

	@Autowired
	private Mapper mapper;

	@Autowired
	private LotteryOrderService lotteryOrderService;
	
	@Autowired
	private LotteryOrderMapper lotteryOrderMapper;

	@Autowired
	private LotteryRoundMapper lotteryRoundMapper;
	
	@Autowired
	private CustomLotteryMapper customLotteryMapper;
	
	@Autowired
	private AccountDetailMapper accountDetailMapper;
	
	@Autowired
	private LotteryReportMapper reportLotteryMapper;

	@ApiOperation(value = "新增投注记录", notes = "新增投注记录", httpMethod = "POST")
	@RequestMapping(value = "/addLotteryOrder", method = RequestMethod.POST)
	@ResponseBody
	public OrderResult addLotteryOrder(
			@ApiParam(value = "Json参数", required = true) @Validated @RequestBody OrderParamVo param) throws Exception {
		OrderResult result = new OrderResult();
		try {
			LotteryOrder order = mapper.map(param, LotteryOrder.class);
			LotteryRound round = lotteryRoundMapper.selectByPrimaryKey(order.getRoundid());
			if (round == null) {
				result.fail("不存在该期游戏");
			} else if (!round.getRoundstatus().equals(EnumType.RoundStatus.Open.ID)) {
				result.fail("该期游戏目前无法投注");
			} else if(round.getClosetime().before(new Date())){
				result.fail("该期游戏已过封盘时间,目前无法投注");
			}else {
				List<LotteryOrderDetail> orderDetails = new ArrayList<LotteryOrderDetail>();
				for (OrderDetailVo orderDetailVo : param.getOrderDetails()) {
					LotteryOrderDetail orderDetail = mapper.map(orderDetailVo, LotteryOrderDetail.class);
					orderDetails.add(orderDetail);
				}
				order.setOrderDetailList(orderDetails);
				AccountDetail accountDetail = accountDetailMapper.selectByPrimaryKey(order.getAccountid());
				String checkInfo = lotteryOrderService.checkLotteryOrder(accountDetail, round, order);
				if (checkInfo.length()==0) {
					lotteryOrderService.addLotteryOrder(accountDetail, order);
					LotteryOrderDto orderDto = mapper.map(order, LotteryOrderDto.class);
					result.success(orderDto);
				} else {
					result.fail(checkInfo);
				}
			}
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(), e);
		}
		return result;
	}
	
	@ApiOperation(value = "获取当期下注记录", notes = "获取当期下注记录", httpMethod = "POST")
	@RequestMapping(value = "/getCurRoundOrder", method = RequestMethod.POST)
	@ResponseBody
	public HistoryOrderResult getCurRoundOrder(
			@ApiParam(value = "Json参数", required = true) @Validated @RequestBody HisOrderVo param) throws Exception {
		HistoryOrderResult result = new HistoryOrderResult();
		try {
			List<HistoryOrderDto> orderList = reportLotteryMapper.selectByCurRoundOrder(param.getRoundId(), param.getAccountId());
			result.success(orderList);
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(), e);
		}
		return result;

	}
	
	@ApiOperation(value = "获取历史下注单", notes = "获取交易报表", httpMethod = "POST")
	@RequestMapping(value = "/getHistoryOrder", method = RequestMethod.POST)
	@ResponseBody
	public HistoryOrderResult getHistoryOrder(
			@ApiParam(value = "Json参数", required = true) @Validated @RequestBody ReportParamVo param) throws Exception {
		HistoryOrderResult result = new HistoryOrderResult();
		try {
			List<HistoryOrderDto> orderList = reportLotteryMapper.selectByHistoryOrder(param.getStartTime(),
					param.getEndTime(), param.getAccountId(), param.getBeginRow(), param.getPageSize());
			result.success(orderList);
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(), e);
		}
		return result;

	}
	
	@ApiOperation(value = "兑奖订单", notes = "游戏已开奖，由于各种因素导致某一笔订单没有兑奖，则手工通过该接口兑奖", httpMethod = "POST")
	@RequestMapping(value = "/endLotteryOrder", method = RequestMethod.POST)
	@ResponseBody
	public BaseRestResult endLotteryOrder(
			@ApiParam(value = "Json参数", required = true) @Validated @RequestBody EndOrderVo param) throws Exception {
		BaseRestResult result = new BaseRestResult();
		try {
			LotteryOrder order = customLotteryMapper.selectOrderByOrderId(param.getOrderId());
			if(order!=null){
				LotteryRound round = lotteryRoundMapper.selectByPrimaryKey(order.getRoundid());
				List<LotteryItem> itemList = customLotteryMapper.selectItemByLottery(EnumType.Lottery.YMZ.ID);
				String updateResult = lotteryOrderService.updateOrderByRound(round, order, itemList);
				if(!updateResult.equals("")){
					result.fail(updateResult);
				}else{
					result.success();
				}
			}else{
				result.fail("不存在该订单！");
			}
			
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.fail(MessageTool.ErrorCode);
			LOG.error(e.getMessage(), e);
		}
		return result;
	}
}
