package com.lottery.api.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class HisOrderVo {

    @ApiModelProperty(value = "账户ID", required = true)
    @NotNull(message = "账户ID不能为空")
    @Min(value=0, message = "账户ID格式不正确")
    private Integer accountId;

    @ApiModelProperty(value = "游戏ID", required = true)
    @NotNull(message = "游戏ID不能为空")
    private Integer roundId;

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

}
