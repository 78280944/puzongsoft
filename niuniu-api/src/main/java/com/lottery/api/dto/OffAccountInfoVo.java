package com.lottery.api.dto;

import java.math.BigDecimal;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class OffAccountInfoVo extends BaseAccountInfoVo{
	
    //@ApiModelProperty(value = "点数限额")
   // private Double limited;
    @ApiModelProperty(value = "洗码比")
    private Double ratio;
    @ApiModelProperty(value = "代理占成")
    private Double percentage;
    @ApiModelProperty(value = "风控限额")
    private String riskamount;

    
	public String getRiskamount() {
		return riskamount;
	}

	public void setRiskamount(String riskamount) {
		this.riskamount = riskamount;
	}
/*
	public Double getLimited() {
		return limited;
	}

	public void setLimited(Double limited) {
		this.limited = limited;
	}
*/
	public Double getRatio() {
		return ratio;
	}

	public void setRatio(Double ratio) {
		this.ratio = ratio;
	}

	public Double getPercentage() {
		return percentage;
	}

	public void setPercentage(Double percentage) {
		this.percentage = percentage;
	}

	   
}
