package com.lottery.orm.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class TradeInfoDto {

    @ApiModelProperty(value = "存款金额")
    private  BigDecimal tradeamount;
    
    @ApiModelProperty(value = "存款时间")
    private Date tradetime;

	public BigDecimal getTradeamount() {
		return tradeamount;
	}

	public void setTradeamount(BigDecimal tradeamount) {
		this.tradeamount = tradeamount;
	}

	public Date getTradetime() {
		return tradetime;
	}

	public void setTradetime(Date tradetime) {
		this.tradetime = tradetime;
	}


    
    
}
