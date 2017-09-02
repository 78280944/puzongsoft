package com.lottery.orm.bo;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class TradeInfo {

	private Integer tradeid;

    private Integer accountid;

    private String tradetype;

    private Integer relativeid;

    private String relativetype;

    private Double tradeamount;
    
    private Double budgetamount;

    private BigDecimal accountamount;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date inputtime;

    private String remark;

    public Integer getTradeid() {
        return tradeid;
    }

    public void setTradeid(Integer tradeid) {
        this.tradeid = tradeid;
    }

    public Integer getAccountid() {
        return accountid;
    }

    public void setAccountid(Integer accountid) {
        this.accountid = accountid;
    }

    public String getTradetype() {
        return tradetype;
    }

    public void setTradetype(String tradetype) {
        this.tradetype = tradetype == null ? null : tradetype.trim();
    }

    public Integer getRelativeid() {
        return relativeid;
    }

    public void setRelativeid(Integer relativeid) {
        this.relativeid = relativeid;
    }

    public String getRelativetype() {
        return relativetype;
    }

    public void setRelativetype(String relativetype) {
        this.relativetype = relativetype == null ? null : relativetype.trim();
    }

    public Double getTradeamount() {
        return tradeamount;
    }

    public void setTradeamount(Double tradeamount) {
        this.tradeamount = tradeamount;
    }
    
    public Double getBudgetamount() {
		return budgetamount;
	}

	public void setBudgetamount(Double budgetamount) {
		this.budgetamount = budgetamount;
	}

    public BigDecimal getAccountamount() {
        return accountamount;
    }

    public void setAccountamount(BigDecimal accountamount) {
        this.accountamount = accountamount;
    }

    public Date getInputtime() {
        return inputtime;
    }

    public void setInputtime(Date inputtime) {
        this.inputtime = inputtime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }
}