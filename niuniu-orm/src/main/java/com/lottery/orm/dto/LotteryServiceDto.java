package com.lottery.orm.dto;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class LotteryServiceDto {
	 
    @ApiModelProperty(value = "登录服务")
    private String loginservice;
    
    @ApiModelProperty(value = "注册服务")
    private String registersercice;

    @ApiModelProperty(value = "增值服务")
    private String addedservice;

    @ApiModelProperty(value = "玩家服务")
    private String playservice;

    @ApiModelProperty(value = "牛牛房间数设置")
    private Integer lotteryniuroom;
    
    @ApiModelProperty(value = "牛牛房间数人数设置")
    private String lotteryniunpman;
    
    @ApiModelProperty(value = "牛牛上庄金额设置")
    private String lotteryniunbanker;
    
    @ApiModelProperty(value = "牛牛投注金额设置")
    private String lotteryniunpbet;
    
    @ApiModelProperty(value = "板九房间数设置")
    private String lotterybjroom;
    
    @ApiModelProperty(value = "板九房间人数设置")
    private String lotterybjman;
    
    @ApiModelProperty(value = "牛牛上庄金额设置")
    private String lotterybjbanker;
    
    @ApiModelProperty(value = "牛牛投注金额设置")
    private String lotterybjbet;
    
    @ApiModelProperty(value = "提现倍数")
    private String cashmultiple;
    
    @ApiModelProperty(value = "提现次数")
    private String cashtime;
    
    @ApiModelProperty(value = "充值服务费")
    private String topupfees;
    
    @ApiModelProperty(value = "提现服务费")
    private String cashfees;
     
    
    
	public Integer getLotteryniuroom() {
		return lotteryniuroom;
	}

	public void setLotteryniuroom(Integer lotteryniuroom) {
		this.lotteryniuroom = lotteryniuroom;
	}

	public String getLotteryniunpman() {
		return lotteryniunpman;
	}

	public void setLotteryniunpman(String lotteryniunpman) {
		this.lotteryniunpman = lotteryniunpman;
	}

	public String getLotteryniunbanker() {
		return lotteryniunbanker;
	}

	public void setLotteryniunbanker(String lotteryniunbanker) {
		this.lotteryniunbanker = lotteryniunbanker;
	}

	public String getLotteryniunpbet() {
		return lotteryniunpbet;
	}

	public void setLotteryniunpbet(String lotteryniunpbet) {
		this.lotteryniunpbet = lotteryniunpbet;
	}

	public String getLotterybjroom() {
		return lotterybjroom;
	}

	public void setLotterybjroom(String lotterybjroom) {
		this.lotterybjroom = lotterybjroom;
	}

	public String getLotterybjman() {
		return lotterybjman;
	}

	public void setLotterybjman(String lotterybjman) {
		this.lotterybjman = lotterybjman;
	}

	public String getLotterybjbanker() {
		return lotterybjbanker;
	}

	public void setLotterybjbanker(String lotterybjbanker) {
		this.lotterybjbanker = lotterybjbanker;
	}

	public String getLotterybjbet() {
		return lotterybjbet;
	}

	public void setLotterybjbet(String lotterybjbet) {
		this.lotterybjbet = lotterybjbet;
	}

	public String getCashmultiple() {
		return cashmultiple;
	}

	public void setCashmultiple(String cashmultiple) {
		this.cashmultiple = cashmultiple;
	}

	public String getCashtime() {
		return cashtime;
	}

	public void setCashtime(String cashtime) {
		this.cashtime = cashtime;
	}

	public String getTopupfees() {
		return topupfees;
	}

	public void setTopupfees(String topupfees) {
		this.topupfees = topupfees;
	}

	public String getCashfees() {
		return cashfees;
	}

	public void setCashfees(String cashfees) {
		this.cashfees = cashfees;
	}

	public String getLoginservice() {
		return loginservice;
	}

	public void setLoginservice(String loginservice) {
		this.loginservice = loginservice;
	}

	public String getRegistersercice() {
		return registersercice;
	}

	public void setRegistersercice(String registersercice) {
		this.registersercice = registersercice;
	}

	public String getAddedservice() {
		return addedservice;
	}

	public void setAddedservice(String addedservice) {
		this.addedservice = addedservice;
	}

	public String getPlayservice() {
		return playservice;
	}

	public void setPlayservice(String playservice) {
		this.playservice = playservice;
	}

    
    
}
