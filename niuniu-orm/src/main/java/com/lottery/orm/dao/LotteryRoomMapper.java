package com.lottery.orm.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lottery.orm.bo.LotteryRoom;

public interface LotteryRoomMapper {
    int deleteByPrimaryKey(Integer rmid);

    int insert(LotteryRoom record);

    int insertSelective(LotteryRoom record);

    LotteryRoom selectByPrimaryKey(Integer rmid);

    int updateByPrimaryKeySelective(LotteryRoom record);

    int updateByPrimaryKey(LotteryRoom record);
    
    List<LotteryRoom> selectLotteryGameRoom(@Param("sid")Integer sid,@Param("len")Integer len);
    
    List<LotteryRoom> selectDistinctSid();
}