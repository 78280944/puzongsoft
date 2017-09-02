package com.lottery.api.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lottery.api.dto.TradeParamVo;
import com.lottery.orm.bo.TradeInfo;
import com.lottery.orm.result.RestResult;
import com.lottery.orm.service.TradeInfoService;
import com.lottery.orm.util.EnumType;
import com.lottery.orm.util.MessageTool;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@RequestMapping(value = "/trade", produces = { "application/json;charset=UTF-8" })
@Api(value = "/trade", description = "出入金款项接口")
@Controller
public class TradeInfoController {
	public static final Logger LOG = Logger.getLogger(TradeInfoController.class);

	@Autowired
	private Mapper mapper;

	@Autowired
	TradeInfoService tradeInfoService;

	@ApiOperation(value = "新增入金交易记录", notes = "新增资金交易记录", httpMethod = "POST")
	@RequestMapping(value = "/addInTradeInfo", method = RequestMethod.POST)
	@ResponseBody
	public RestResult addInTradeInfo(
			@ApiParam(value = "Json参数", required = true) @Validated @RequestBody TradeParamVo param) throws Exception {
		RestResult result = new RestResult();
		try {
			TradeInfo tradeInfo = mapper.map(param, TradeInfo.class);
			tradeInfo.setRelativeid(param.getSupAccountId());
			tradeInfo.setRelativetype(EnumType.RalativeType.In.ID);
			String returnInfo = tradeInfoService.addInoutTradeInfo(tradeInfo);
			if (StringUtils.isBlank(returnInfo)) {
				result.success(tradeInfo);
			} else {
				result.fail(returnInfo);
			}
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.fail(MessageTool.ErrorCode);
			LOG.error(e.getMessage(), e);
		}
		return result;
	}

	@ApiOperation(value = "新增出金交易记录", notes = "新增资金交易记录", httpMethod = "POST")
	@RequestMapping(value = "/addOutTradeInfo", method = RequestMethod.POST)
	@ResponseBody
	public RestResult addOutTradeInfo(
			@ApiParam(value = "Json参数", required = true) @Validated @RequestBody TradeParamVo param) throws Exception {
		RestResult result = new RestResult();
		try {
			TradeInfo tradeInfo = mapper.map(param, TradeInfo.class);
			tradeInfo.setRelativeid(param.getSupAccountId());
			tradeInfo.setRelativetype(EnumType.RalativeType.Out.ID);
			String returnInfo = tradeInfoService.addInoutTradeInfo(tradeInfo);
			if (StringUtils.isBlank(returnInfo)) {
				result.success(tradeInfo);
			} else {
				result.fail(returnInfo);
			}
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.fail(MessageTool.ErrorCode);
			LOG.error(e.getMessage(), e);
		}
		return result;
	}

}
