package com.lottery.orm.util;

public interface EnumType {
	enum Lottery {
	    YMZ("01", "玉米籽");
	    private Lottery(String ID, String NAME) {
	      this.ID = ID;
	      this.NAME = NAME;
	    }

	    public final String ID;
	    public final String NAME;
	    public final static String enumDesc = "游戏";
	  }
  enum LotteryType {
    CQ("01", "重庆快乐十分玉米籽"),GD("02", "广东快乐十分玉米籽"),TJ("03", "天津快乐十分玉米籽");
    private LotteryType(String ID, String NAME) {
      this.ID = ID;
      this.NAME = NAME;
    }

    public final String ID;
    public final String NAME;
    public final static String enumDesc = "玉米籽类型";
  }
  enum ItemType {
    Type_01("01", "番摊玉米籽"), Type_02("02", "广西快乐十分");
    private ItemType(String ID, String NAME) {
      this.ID = ID;
      this.NAME = NAME;
    }

    public final String ID;
    public final String NAME;
    public final static String enumDesc = "投注项类型";
  }
  
  enum RoundStatus {
    Open("Open", "开盘中"), Close("Close", "已封盘"), End("End", "已开奖");
    private RoundStatus(String ID, String NAME) {
      this.ID = ID;
      this.NAME = NAME;
    }

    public final String ID;
    public final String NAME;
    public final static String enumDesc = "游戏状态";
  }
  
  enum TradeType {
    Inout("Inout", "出入金"), Trade("Trade", "交易");
    private TradeType(String ID, String NAME) {
      this.ID = ID;
      this.NAME = NAME;
    }

    public final String ID;
    public final String NAME;
    public final static String enumDesc = "业务类型";
  }
  
  enum RalativeType {
    In("In", "入金"),Out("Out", "出金"), Commision("Commision", "公司损益"),Order("Order", "下注本金"), PlayerWin("PlayerWin", "会员输赢"),AgencyWin("AgencyWin", "代理输赢"), Return("Return", "返利");
    private RalativeType(String ID, String NAME) {
      this.ID = ID;
      this.NAME = NAME;
    }

    public final String ID;
    public final String NAME;
    public final static String enumDesc = "业务相关类型";
  }
  
  enum OffType {
	    Admin("0", "超级账户"),Agency("1", "代理账户"), Sub("2", "子账户类型"), Play("3", "会员账户");
	    private OffType(String ID, String NAME) {
	      this.ID = ID;
	      this.NAME = NAME;
	    }

	    public final String ID;
	    public final String NAME;
	    public final static String enumDesc = "账户类型";
	  }
}
