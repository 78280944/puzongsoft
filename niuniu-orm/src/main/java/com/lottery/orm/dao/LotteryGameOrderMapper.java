package com.lottery.orm.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lottery.orm.bo.LotteryGameOrder;
import com.lottery.orm.dto.RoomHisOrderDto;
import com.lottery.orm.dto.RoomOrderItemDto;
import com.lottery.orm.dto.RoomOrderDto;

public interface LotteryGameOrderMapper {

	int deleteByPrimaryKey(Integer lgmid);

    int insert(LotteryGameOrder record);

    int insertSelective(LotteryGameOrder record);

    LotteryGameOrder selectByPrimaryKey(Integer lgmid);

    int updateByPrimaryKeySelective(LotteryGameOrder record);

    int updateByPrimaryKey(LotteryGameOrder record);
    
    List<RoomOrderDto> selectGameOrder(@Param("accountid")Integer accountid,@Param("sid")Integer sid,@Param("beginRow")Integer beginRow,@Param("pageSize")Integer pageSize);
   
    List<RoomHisOrderDto> selectGameHisOrder(@Param("startTime")Date startTime,@Param("endTime")Date endTime,@Param("accountid")Integer accountid,@Param("sid")Integer sid,@Param("beginRow")Integer beginRow,@Param("pageSize")Integer pageSize);
    
    List<RoomHisOrderDto> selectGameHisOrderBytime(@Param("accountid")Integer accountid,@Param("sid")Integer sid,@Param("time")String time,@Param("beginRow")Integer beginRow,@Param("pageSize")Integer pageSize);
   
    List<RoomOrderItemDto> selectGameOrderItem(@Param("accountid")Integer accountid,@Param("sid")Integer sid,@Param("rmid")Integer rmid,@Param("lotteryTerm")String lotteryTerm); 
}