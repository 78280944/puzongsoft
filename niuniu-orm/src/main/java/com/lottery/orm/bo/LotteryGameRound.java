package com.lottery.orm.bo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wordnik.swagger.annotations.ApiModelProperty;

public class LotteryGameRound {
    private Integer lgrid;

    private Integer sid;

    private String lotteryterm;

    private String lotteryresult;
    
	@ApiModelProperty(value = "游戏开始时间", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date starttime;
	
	@ApiModelProperty(value = "游戏结束时间", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date overtime;

	@ApiModelProperty(value = "接口游戏开始时间", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date opentime;

	@ApiModelProperty(value = "接口游戏结束时间", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date closetime;

	@ApiModelProperty(value = "接口实际游戏开始时间", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date actopentime;

	@ApiModelProperty(value = "接口实际游戏结束时间", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date actclosetime;

    public Integer getLgrid() {
        return lgrid;
    }

    public void setLgrid(Integer lgrid) {
        this.lgrid = lgrid;
    }

    public Integer getSid() {
        return sid;
    }

    public void setSid(Integer sid) {
        this.sid = sid;
    }

    public String getLotteryterm() {
        return lotteryterm;
    }

    public void setLotteryterm(String lotteryterm) {
        this.lotteryterm = lotteryterm == null ? null : lotteryterm.trim();
    }

    public String getLotteryresult() {
        return lotteryresult;
    }

    public void setLotteryresult(String lotteryresult) {
        this.lotteryresult = lotteryresult == null ? null : lotteryresult.trim();
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

    public Date getOpentime() {
        return opentime;
    }

    public void setOpentime(Date opentime) {
        this.opentime = opentime;
    }

    public Date getClosetime() {
        return closetime;
    }

    public void setClosetime(Date closetime) {
        this.closetime = closetime;
    }

    public Date getActopentime() {
        return actopentime;
    }

    public void setActopentime(Date actopentime) {
        this.actopentime = actopentime;
    }

    public Date getActclosetime() {
        return actclosetime;
    }

    public void setActclosetime(Date actclosetime) {
        this.actclosetime = actclosetime;
    }
}