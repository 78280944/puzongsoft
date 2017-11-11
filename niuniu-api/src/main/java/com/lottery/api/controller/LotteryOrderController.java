package com.lottery.api.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.dozer.Mapper;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lottery.api.dto.CurResultParamVo;
import com.lottery.api.dto.EndOrderVo;
import com.lottery.api.dto.HisOrderVo;
import com.lottery.api.dto.OrderDetailVo;
import com.lottery.api.dto.OrderParamVo;
import com.lottery.api.dto.ReportParamVo;
import com.lottery.api.dto.ResultAmountVo;
import com.lottery.api.dto.ResultParamVo;
import com.lottery.api.dto.RoomAmountVo;
import com.lottery.api.dto.RoomOrderVo;
import com.lottery.orm.bo.AccountDetail;
import com.lottery.orm.bo.AccountInfo;
import com.lottery.orm.bo.LotteryGameOrder;
import com.lottery.orm.bo.LotteryItem;
import com.lottery.orm.bo.LotteryOrder;
import com.lottery.orm.bo.LotteryOrderDetail;
import com.lottery.orm.bo.LotteryRound;
import com.lottery.orm.bo.SysLimit;
import com.lottery.orm.dao.AccountDetailMapper;
import com.lottery.orm.dao.AccountInfoMapper;
import com.lottery.orm.dao.CustomLotteryMapper;
import com.lottery.orm.dao.LotteryGameOrderMapper;
import com.lottery.orm.dao.LotteryOrderMapper;
import com.lottery.orm.dao.LotteryReportMapper;
import com.lottery.orm.dao.LotteryRoundMapper;
import com.lottery.orm.dao.SysLimitMapper;
import com.lottery.orm.dto.HistoryOrderDto;
import com.lottery.orm.dto.LotteryNoidDto;
import com.lottery.orm.dto.LotteryOrderDto;
import com.lottery.orm.dto.ResultAmountDto;
import com.lottery.orm.dto.RoomAmountDto;
import com.lottery.orm.dto.RoomHisOrderDto;
import com.lottery.orm.dto.RoomOrderDto;
import com.lottery.orm.dto.RoomOrderItemDto;
import com.lottery.orm.result.BaseRestResult;
import com.lottery.orm.result.GameOrderGrListResult;
import com.lottery.orm.result.GameOrderList;
import com.lottery.orm.result.GameOrderListResult;
import com.lottery.orm.result.HistoryOrderResult;
import com.lottery.orm.result.LotteryNoidResult;
import com.lottery.orm.result.OrderAmountResult;
import com.lottery.orm.result.OrderListResult;
import com.lottery.orm.result.OrderResult;
import com.lottery.orm.result.RestResult;
import com.lottery.orm.result.RoomAmountResult;
import com.lottery.orm.service.LotteryOrderService;
import com.lottery.orm.util.CommonUtils;
import com.lottery.orm.util.EnumType;
import com.lottery.orm.util.MessageTool;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiModelProperty;
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
	private LotteryOrderService  lotteryOrderService;
	
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
	
	@Autowired
	private LotteryGameOrderMapper lotteryGameOrderMapper;

	@Autowired
	private AccountInfoMapper accountInfoMapper;
	
	@Autowired
	private SysLimitMapper sysLimitMapper;
	
	@ApiOperation(value = "新增投注记录", notes = "新增投注记录", httpMethod = "POST")
	@RequestMapping(value = "/addLotteryOrder", method = RequestMethod.POST)
	@ResponseBody
	public RestResult addLotteryOrder(
			@ApiParam(value = "Json参数", required = true) @Validated @RequestBody OrderParamVo param) throws Exception {
		System.out.println("1---------------------------------------"+param.getRmid());
		RestResult result = new RestResult();
		String checkInfo = "";
		try {
			LotteryGameOrder order = mapper.map(param, LotteryGameOrder.class);
			System.out.println("8------------------"+order.getLotteryterm());
			AccountInfo accountInfo = accountInfoMapper.selectByPrimaryKey(order.getAccountid());
			if (accountInfo == null){
				result.fail(MessageTool.Code_3001);
				return result;
			}

			for (OrderDetailVo orderDetailVo : param.getOrderDetails()) {
				order.setOrdertime(new Date());
				order.setNoid(orderDetailVo.getNoId());
				order.setLtdid(orderDetailVo.getNoId());
				order.setOrderamount(orderDetailVo.getOrderAmount());
				order.setPlayoridle(orderDetailVo.getPlayOridle());
				
				SysLimit sys = sysLimitMapper.selectByOrder(String.valueOf(order.getSid()),order.getPlayoridle());
				if (sys == null){
					result.fail(MessageTool.Code_2002);
					return result;
				}
			
				//投注检查
				checkInfo = lotteryOrderService.checkLotteryOrderInfo(accountInfo, order, sys);
				if ((!"true".equals(checkInfo))){
					result.fail(checkInfo);
					return result;
				}
				//庄闲转换
				if (order.getPlayoridle().equals("1")){
					lotteryGameOrderMapper.updatePlayOridle(order.getSid(), order.getRmid(), order.getNoid(),order.getLotteryterm());
				}
				//账户变动
				lotteryOrderService.changeAccountAmount(accountInfo, order);
				
				//投注
				System.out.println("123-------------"+order.getLotteryterm());
				lotteryOrderService.insertLotteryGameOrder(order);
				
			}
			result.success();
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(), e);
		}
		return result;
	}
	
	
	
	/*
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
	*/
	
	/*
	@ApiOperation(value = "获取投注明细", notes = "获取投注明细", httpMethod = "POST")
	@RequestMapping(value = "/getOrderItem", method = RequestMethod.POST)
	@ResponseBody
	public GameOrderGrListResult getOrderItem(
			@ApiParam(value = "Json参数", required = true) @Validated @RequestBody RoomOrderVo param) throws Exception {
		GameOrderGrListResult  result =  new GameOrderGrListResult();
		try { 	
			/*
			List<RoomOrderDto> orderList = lotteryGameOrderMapper.selectGameOrder(param.getAccountid(), param.getSid(), param.getBeginRow(), param.getPageSize());
			System.out.println("900000-------------"+orderList.size());
			List<RoomOrderDto> orderList1 = lotteryGameOrderMapper.selectGameOrder(1010, param.getSid(), param.getBeginRow(), param.getPageSize());
			System.out.println("900555000-------------"+orderList1.size());
			List<List<RoomOrderDto>> a = new ArrayList<List<RoomOrderDto>>();
			a.add(orderList1);
			a.add(orderList1);
			
			int t=0;
			int m=0;
			List<List<RoomOrderItemDto>> list = lotteryOrderService.selectGameOrderItem(param.getLotteryterm(), param.getSid(), param.getRmid(), param.getAccountid());
			for (int i=0;i<list.size();i++){
				List<RoomOrderItemDto> list1 = list.get(i);
				for (int j = 0;j<list1.size();j++){
					RoomOrderItemDto ro = new RoomOrderItemDto();
					ro = list1.get(j);
					if (ro.getNoid()==t){
						t++;
						ro.setOrderno(t);
					}else{
					    t=1;
					    ro.setOrderno(t);
					}
				}
				
			}
			result.success(list);
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(), e);
		}
		return result;

	}
	*/
	
	@ApiOperation(value = "获取投注明细", notes = "获取投注明细", httpMethod = "POST")
	@RequestMapping(value = "/getOrderItem", method = RequestMethod.POST)
	@ResponseBody
	public GameOrderGrListResult getOrderItem(
			@ApiParam(value = "Json参数", required = true) @Validated @RequestBody RoomOrderVo param) throws Exception {
		GameOrderGrListResult  result =  new GameOrderGrListResult();
		try { 	
			List<RoomOrderItemDto> list = lotteryOrderService.selectGameOrderItem(param.getLotteryterm(), param.getSid(), param.getRmid(), param.getAccountid());
			result.success(list);
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(), e);
		}
		return result;

	}
	
	@ApiOperation(value = "获取本期注单", notes = "获取本期注单", httpMethod = "POST")
	@RequestMapping(value = "/getCurTermOrder", method = RequestMethod.POST)
	@ResponseBody
	public GameOrderListResult getCurTermOrder(
			@ApiParam(value = "Json参数", required = true) @Validated @RequestBody CurResultParamVo param) throws Exception {
		GameOrderListResult result = new GameOrderListResult();
		try {

			List<RoomOrderDto> orderList = lotteryGameOrderMapper.selectGameOrder(param.getAccountid(), param.getSid(), param.getBeginRow(), param.getPageSize());
			result.success(orderList);
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(), e);
		}
		return result;

	}
	
	@ApiOperation(value = "获取游戏桌台人数及庄台", notes = "获取游戏桌台人数及庄台", httpMethod = "POST")
	@RequestMapping(value = "/getLotteryGameNoid", method = RequestMethod.POST)
	@ResponseBody
	public LotteryNoidResult getLotteryGameNoid(
			@ApiParam(value = "Json参数", required = true) @Validated @RequestBody RoomOrderVo param) throws Exception {
		LotteryNoidResult result = new LotteryNoidResult();
		try {
			List<LotteryNoidDto> orderList = lotteryGameOrderMapper.selectGameNoid(param.getSid(), param.getRmid(),param.getLotteryterm());
			System.out.println("12-------------"+param.getSid()+".."+param.getRmid()+".."+param.getLotteryterm()+".."+orderList.size());
			
			result.success(orderList);
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(), e);
		}
		return result;

	}
	
	@ApiOperation(value = "获取历史注单", notes = "获取本历史注单", httpMethod = "POST")
	@RequestMapping(value = "/getHisOrder", method = RequestMethod.POST)
	@ResponseBody
	public OrderListResult getHisOrder(
			@ApiParam(value = "Json参数", required = true) @Validated @RequestBody ResultParamVo param) throws Exception {
		OrderListResult result = new OrderListResult();
		try {
			Date[] param1 = CommonUtils.getDateTime(param.getStartDate(), param.getEndDate());
			List<RoomHisOrderDto> orderList =null;
			if (param.getSid()==9999){
				System.out.println("111111-------------"+param.getSid());
			   orderList = lotteryOrderService.getLotteryHisAllOrder(param1[0], param1[1],param.getAccountid(), param.getSid(), param.getBeginRow(), param.getPageSize());
			}else{
				System.out.println("222222-------------"+param.getSid());
			   orderList = lotteryOrderService.getLotteryHisOrder(param1[0], param1[1],param.getAccountid(), param.getSid(), param.getBeginRow(), param.getPageSize());
			}
				result.success(orderList);
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(), e);
		}
		return result;

	}
	
	@ApiOperation(value = "获取注单汇总金额", notes = "获取注单汇总金额", httpMethod = "POST")
	@RequestMapping(value = "/getHisOrderAmount", method = RequestMethod.POST)
	@ResponseBody
	public OrderAmountResult getHisOrderAmount(
			@ApiParam(value = "Json参数", required = true) @Validated @RequestBody ResultAmountVo param) throws Exception {
		OrderAmountResult result = new OrderAmountResult();
		try {
			Date[] param1 = CommonUtils.getDateTime(param.getStartDate(), param.getEndDate());
			ResultAmountDto ra = null;
			if (param.getSid()==9999){
				ra = lotteryGameOrderMapper.selectGameAllHisAmount(param.getAccountid(),param.getSid(),param1[0], param1[1]);
			}else{
			    ra = lotteryGameOrderMapper.selectGameHisAmount(param.getAccountid(),param.getSid(),param1[0], param1[1]);
				
			}
			result.success(ra);
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(), e);
		}
		return result;
	}
	
	@ApiOperation(value = "获取房间收益", notes = "获取房间收益", httpMethod = "POST")
	@RequestMapping(value = "/getOrderAmount", method = RequestMethod.POST)
	@ResponseBody
	public RoomAmountResult getOrderAmount(
			@ApiParam(value = "Json参数", required = true) @Validated @RequestBody RoomAmountVo param) throws Exception {
		RoomAmountResult result = new RoomAmountResult();
		try {
			List<RoomAmountDto> list = lotteryGameOrderMapper.selectGameAmount(param.getRmid(), param.getLotteryterm());
			result.success(list);
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(), e);
		}
		return result;

	}
	
	
	/*
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
	*/
	
	/*
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
	*/
}
