package com.lottery.orm.dao;

import java.util.List;

import com.lottery.orm.bo.NoticeInfo;

public interface NoticeInfoMapper {
    int deleteByPrimaryKey(Integer noticeid);

    int insert(NoticeInfo record);

    int insertSelective(NoticeInfo record);

    NoticeInfo selectByPrimaryKey(Integer noticeid);

    int updateByPrimaryKeySelective(NoticeInfo record);

    int updateByPrimaryKey(NoticeInfo record);
    
    NoticeInfo selectByNotice(String offtype);
    
    List<NoticeInfo> selectByHisNotice(String offtype);
    
} 