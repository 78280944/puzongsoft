package com.lottery.orm.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lottery.orm.bo.AccountInfo;

public interface AccountInfoMapper {
    
    int deleteByPrimaryKey(Integer serialno);

    int insert(AccountInfo record);

    int insertSelective(AccountInfo record);

    AccountInfo selectByPrimaryKey(Integer serialno);

    int updateByPrimaryKeySelective(AccountInfo record);

    int updateByPrimaryKey(AccountInfo record);
    
    int updateAccountState(@Param("state")String state,@Param("supusername")String supusername);
    
    int updateAccountSupuserState(@Param("state")String state,@Param("supusername")String supusername);
  
    //get account info when login
    AccountInfo selectByLogin(AccountInfo record);
    
    List<AccountInfo> selectBySupusername(@Param("supusername")String supusername, @Param("beginrow")Integer beginrow, @Param("pageSize")Integer pageSize);
    
    AccountInfo selectByUsername(String username);
    
    AccountInfo selectByUserAndId(AccountInfo record);
}