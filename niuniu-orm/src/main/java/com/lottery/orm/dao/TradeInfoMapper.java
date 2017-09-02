package com.lottery.orm.dao;

import com.lottery.orm.bo.TradeInfo;

public interface TradeInfoMapper {

	int deleteByPrimaryKey(Integer tradeid);

    int insert(TradeInfo record);

    int insertSelective(TradeInfo record);

    TradeInfo selectByPrimaryKey(Integer tradeid);

    int updateByPrimaryKeySelective(TradeInfo record);

    int updateByPrimaryKey(TradeInfo record);
}