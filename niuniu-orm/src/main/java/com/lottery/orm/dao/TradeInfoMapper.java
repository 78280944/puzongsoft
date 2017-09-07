package com.lottery.orm.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lottery.orm.bo.TradeInfo;

public interface TradeInfoMapper {

	int deleteByPrimaryKey(Integer tradeid);

    int insert(TradeInfo record);

    int insertSelective(TradeInfo record);

    TradeInfo selectByPrimaryKey(Integer tradeid);

    int updateByPrimaryKeySelective(TradeInfo record);

    int updateByPrimaryKey(TradeInfo record);
    
    List<TradeInfo> selectByTrade(@Param("relativetype")String relativetype, @Param("starttime")Date starttime,@Param("overtime")Date overtime,@Param("beginrow")Integer beginrow, @Param("pageSize")Integer pageSize);
    
}