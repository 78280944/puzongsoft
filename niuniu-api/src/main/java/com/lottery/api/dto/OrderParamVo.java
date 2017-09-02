package com.lottery.api.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class OrderParamVo {

    @ApiModelProperty(value = "账户ID", required = true)
    @NotNull(message = "账户ID不能为空")
    @Min(value=0, message = "账户ID格式不正确")
    private Integer accountId;

    @ApiModelProperty(value = "游戏ID", required = true)
    @NotNull(message = "游戏ID不能为空")
    private Integer roundId;

    @ApiModelProperty(value = "投注详情", required = true)
    @NotEmpty(message = "投注详情不能为空")
    @Valid
    private List<OrderDetailVo> orderDetails;

    public Integer getAccountId() {
	return accountId;
    }

    public void setAccountId(Integer accountId) {
	this.accountId = accountId;
    }

    public Integer getRoundId() {
        return roundId;
    }

    public void setRoundId(Integer roundId) {
        this.roundId = roundId;
    }

    public List<OrderDetailVo> getOrderDetails() {
	return orderDetails;
    }

    public void setOrderDetails(List<OrderDetailVo> orderDetails) {
	this.orderDetails = orderDetails;
    }

}
