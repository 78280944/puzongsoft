package com.lottery.orm.service;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lottery.orm.bo.AccountDetail;
import com.lottery.orm.bo.AccountInfo;
import com.lottery.orm.bo.OffAccountInfo;
import com.lottery.orm.bo.TradeInfo;
import com.lottery.orm.dao.AccountDetailMapper;
import com.lottery.orm.dao.AccountInfoMapper;
import com.lottery.orm.dao.OffAccountInfoMapper;
import com.lottery.orm.dao.TradeInfoMapper;
import com.lottery.orm.util.EnumType;

@Service
@Transactional
public class TradeInfoService {

    @Autowired
    private TradeInfoMapper tradeInfoMapper;

    @Autowired
    private AccountDetailMapper accountDetailMapper;
    
    // 添加出入金款项并更新账户
    public String addInoutTradeInfo(TradeInfo tradeInfo) {
	    AccountDetail supAccountDetail = accountDetailMapper.selectByPrimaryKey(tradeInfo.getRelativeid());
		AccountDetail accountDetail = accountDetailMapper.selectByPrimaryKey(tradeInfo.getAccountid());
		Double supAccountAmount = supAccountDetail.getMoney()==null?0.0:supAccountDetail.getMoney().doubleValue();
		Double accountAmount = accountDetail.getMoney()==null?0.0:accountDetail.getMoney().doubleValue();
		Double accountBudget = accountDetail.getBudget()==null?0.0:accountDetail.getBudget();
		
		Double tradeAmount = 0.0;//交易点数
		Double budgetAmount = 0.0;//预算款
		
		if(tradeInfo.getRelativetype().equals(EnumType.RalativeType.In.ID)){
			if(supAccountAmount>=tradeInfo.getTradeamount()||supAccountDetail.getOfftype().equals(EnumType.OffType.Admin.ID)){
				if(accountDetail.getOfftype().equals(EnumType.OffType.Agency.ID)){
					budgetAmount = tradeInfo.getTradeamount()*accountDetail.getPercentage();
					accountBudget = accountBudget + budgetAmount;
				}
				supAccountAmount = supAccountAmount - tradeInfo.getTradeamount();
			    accountAmount = accountAmount + tradeInfo.getTradeamount();
			    tradeAmount = tradeInfo.getTradeamount();
			}else{
				return "您帐户的点数小于上分的点数,无法给下级进行上分!";
			}
		}else if(tradeInfo.getRelativetype().equals(EnumType.RalativeType.Out.ID)){
			if(accountAmount>=tradeInfo.getTradeamount()){
				if(accountDetail.getOfftype().equals(EnumType.OffType.Agency.ID)){
					budgetAmount = 0.0 - tradeInfo.getTradeamount()*accountDetail.getPercentage();
					accountBudget = accountBudget + budgetAmount;
				}
				supAccountAmount = supAccountAmount + tradeInfo.getTradeamount();
			    accountAmount = accountAmount - tradeInfo.getTradeamount();
			    tradeAmount = 0.0 - tradeInfo.getTradeamount();
			}else{
				return "下级帐户的点数小于退分的点数,无法给下级进行退分!";
			}
		}else{
			return "不支持该交易类型";
		}
		tradeInfo.setTradeamount(tradeAmount);//转换为负数
		tradeInfo.setBudgetamount(budgetAmount);
		tradeInfo.setAccountamount(new BigDecimal(accountAmount));
		tradeInfo.setTradetype(EnumType.TradeType.Inout.ID);
		tradeInfo.setInputtime(new Date());
		
		if (tradeInfoMapper.insertSelective(tradeInfo) > 0) {
			accountDetail.setBudget(accountBudget);
			accountDetail.setMoney(new BigDecimal(accountAmount));
		    accountDetailMapper.updateByPrimaryKeySelective(accountDetail);
		    supAccountDetail.setMoney(new BigDecimal(supAccountAmount));
		    accountDetailMapper.updateByPrimaryKeySelective(supAccountDetail);
		    return "";
		}else{
			return "新增点数出入记录失败!";
		}
		
    }
    
}
