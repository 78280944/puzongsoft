package com.lottery.orm.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.lottery.orm.bo.AccountDetail;
import com.lottery.orm.bo.AccountInfo;
import com.lottery.orm.dao.AccountDetailMapper;
import com.lottery.orm.dao.AccountInfoMapper;
import com.lottery.orm.result.AccountResult;
import com.lottery.orm.util.MessageTool;

@Service
@Transactional
public class AccountInfoService {

	@Autowired
	private AccountInfoMapper accountInfoMapper;

	@Autowired
	private AccountDetailMapper accountDetailMapper;

	// 添加账户
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor=Exception.class)
	public void addAccountInfo(AccountInfo paraInfo) {
		accountInfoMapper.insertSelective(paraInfo);

		AccountDetail accountDetail = new AccountDetail();
		accountDetail.setUserid(paraInfo.getUserid());
		accountDetail.setUsername(paraInfo.getUsername());
		accountDetail.setLimited(paraInfo.getLimited());
		accountDetail.setRatio(paraInfo.getRatio());
		accountDetail.setPercentage(0.0);
		accountDetail.setState("1");
		accountDetail.setSupusername(paraInfo.getSupusername());
		accountDetail.setLevel(paraInfo.getLevel());
		accountDetail.setOfftype("3");
		accountDetail.setMoney(BigDecimal.valueOf(0.0));
		accountDetailMapper.insertSelective(accountDetail);
	}

	// 更新账户
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor=Exception.class)
	public void updateAccountInfo(AccountInfo paraInfo) {
		accountInfoMapper.updateByPrimaryKeySelective(paraInfo);
		//获取accountid
		AccountDetail accountDetail = accountDetailMapper.selectByUserId(paraInfo.getUserid(), "3");
		accountDetail.setUserid(paraInfo.getUserid());
	    accountDetail.setUsername(paraInfo.getUsername());
	    accountDetail.setLimited(paraInfo.getLimited());
	    accountDetail.setRatio(paraInfo.getRatio());
	    accountDetail.setState(paraInfo.getState());
	    accountDetail.setSupusername(paraInfo.getSupusername());
	    accountDetail.setLevel(paraInfo.getLevel());
	    accountDetailMapper.updateByPrimaryKeySelective(accountDetail);
	}
	
	//更新剩余点数
	public void updateAccountMount(AccountDetail accountDetail) {
	    accountDetailMapper.updateByPrimaryKeySelective(accountDetail);
	}

}
