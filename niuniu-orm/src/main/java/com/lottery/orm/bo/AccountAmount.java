package com.lottery.orm.bo;

import java.math.BigDecimal;
import java.util.Date;

public class AccountAmount {
	private Integer aaid;

    private Integer accountid;

    private String lotteryterm;

    private BigDecimal loss;

    private BigDecimal earns;

    private BigDecimal gains;

    private BigDecimal profits;

    private Date starttime;

    private Date overtime;

	public String getLotteryterm() {
		return lotteryterm;
	}

	public void setLotteryterm(String lotteryterm) {
		this.lotteryterm = lotteryterm;
	}

	public Date getStarttime() {
		return starttime;
	}

	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}

	public Date getOvertime() {
		return overtime;
	}

	public void setOvertime(Date overtime) {
		this.overtime = overtime;
	}

	public Integer getAaid() {
		return aaid;
	}

	public void setAaid(Integer aaid) {
		this.aaid = aaid;
	}

	public Integer getAccountid() {
		return accountid;
	}

	public void setAccountid(Integer accountid) {
		this.accountid = accountid;
	}

	public BigDecimal getLoss() {
		return loss;
	}

	public void setLoss(BigDecimal loss) {
		this.loss = loss;
	}

	public BigDecimal getEarns() {
		return earns;
	}

	public void setEarns(BigDecimal earns) {
		this.earns = earns;
	}

	public BigDecimal getGains() {
		return gains;
	}

	public void setGains(BigDecimal gains) {
		this.gains = gains;
	}

	public BigDecimal getProfits() {
		return profits;
	}

	public void setProfits(BigDecimal profits) {
		this.profits = profits;
	}

}