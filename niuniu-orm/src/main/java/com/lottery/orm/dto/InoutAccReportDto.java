package com.lottery.orm.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wordnik.swagger.annotations.ApiModelProperty;

public class InoutAccReportDto {
	
	@ApiModelProperty(value = "操作时间", required = true)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date inputTime;
	
	@ApiModelProperty(value = "账户ID", required = true)
	private Integer accountId;
	
	@ApiModelProperty(value = "所属代理", required = true)
	private String userName;
	
	@ApiModelProperty(value = "存/取款", required = true)
	private Double tradeAmount;

	@ApiModelProperty(value = "账户余额", required = true)
	private BigDecimal accountAmount;

	@ApiModelProperty(value = "备注")
	private String remark;

	public Date getInputTime() {
		return inputTime;
	}

	public void setInputTime(Date inputTime) {
		this.inputTime = inputTime;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Double getTradeAmount() {
		return tradeAmount;
	}

	public void setTradeAmount(Double tradeAmount) {
		this.tradeAmount = tradeAmount;
	}

	public BigDecimal getAccountAmount() {
		return accountAmount;
	}

	public void setAccountAmount(BigDecimal accountAmount) {
		this.accountAmount = accountAmount;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}
