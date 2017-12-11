package com.lottery.orm.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lottery.orm.bo.AccountRecharge;

public interface AccountRechargeMapper {
    int deleteByPrimaryKey(Integer arid);

    int insert(AccountRecharge record);

    int insertSelective(AccountRecharge record);

    AccountRecharge selectByPrimaryKey(Integer arid);

    int updateByPrimaryKeySelective(AccountRecharge record);

    int updateByPrimaryKey(AccountRecharge record);
    
    List<AccountRecharge> selectByTime(@Param("orderdate")String orderdate,@Param("relativetype")String relativetype,@Param("accountid")Integer accountid);
    
    AccountRecharge selectByOrderNo(@Param("orderno")String orderno,@Param("relativetype")String relativetype,@Param("accountid")Integer accountid);
    
}