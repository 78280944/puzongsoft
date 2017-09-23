package com.lottery.orm.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lottery.orm.bo.LotteryRoomDetail;
import com.lottery.orm.dto.QueryRoomDateDto;


public interface LotteryRoomDetailMapper {
    int insert(LotteryRoomDetail record);

    int insertSelective(LotteryRoomDetail record);
    
    List<QueryRoomDateDto> selectLotteryRoomDetail(@Param("startTime")Date startTime,@Param("endTime")Date endTime,@Param("rmid")Integer rmid,@Param("beginRow")Integer beginRow,@Param("pageSize")Integer pageSize);
    
}