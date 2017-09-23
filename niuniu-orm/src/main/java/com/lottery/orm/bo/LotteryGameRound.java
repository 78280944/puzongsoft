package com.lottery.orm.bo;

import java.util.Date;

public class LotteryGameRound {
    private Integer lgrid;

    private Integer sid;

    private String lotteryterm;

    private String lotteryresult;

    private Date starttime;

    private Date overtime;

    private Date opentime;

    private Date closetime;

    private Date actopentime;

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