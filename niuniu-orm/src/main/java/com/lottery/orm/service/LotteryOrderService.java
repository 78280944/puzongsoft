package com.lottery.orm.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.lottery.orm.bo.AccountDetail;
import com.lottery.orm.bo.LotteryItem;
import com.lottery.orm.bo.LotteryOrder;
import com.lottery.orm.bo.LotteryOrderDetail;
import com.lottery.orm.bo.LotteryOrderResult;
import com.lottery.orm.bo.LotteryRound;
import com.lottery.orm.bo.LotteryRoundItem;
import com.lottery.orm.bo.OffAccountInfo;
import com.lottery.orm.bo.TradeInfo;
import com.lottery.orm.dao.AccountDetailMapper;
import com.lottery.orm.dao.CustomLotteryMapper;
import com.lottery.orm.dao.LotteryOrderDetailMapper;
import com.lottery.orm.dao.LotteryOrderMapper;
import com.lottery.orm.dao.LotteryOrderResultMapper;
import com.lottery.orm.dao.OffAccountInfoMapper;
import com.lottery.orm.dao.TradeInfoMapper;
import com.lottery.orm.util.EnumType;

@Service
@Transactional
public class LotteryOrderService {

	@Autowired
	private CustomLotteryMapper customLotteryMapper;

	@Autowired
	private LotteryOrderMapper lotteryOrderMapper;

	@Autowired
	private AccountDetailMapper accountDetailMapper;

	@Autowired
	private TradeInfoMapper tradeInfoMapper;

	@Autowired
	private LotteryOrderDetailMapper lotteryOrderDetailMapper;
	
	@Autowired
	private OffAccountInfoMapper offAccountInfoMapper;
	
	@Autowired
	private LotteryOrderResultMapper lotteryOrderResultMapper;

	// 添加投注单
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
	public LotteryOrder addLotteryOrder(AccountDetail accountDetail, LotteryOrder order) {
		Double orderAmount = 0.0;
		for (LotteryOrderDetail orderDetail : order.getOrderDetailList()) {
			orderAmount += orderDetail.getDetailamount();
		}
		order.setOrderamount(orderAmount);
		order.setOrdertime(new Date());
		lotteryOrderMapper.insertSelective(order);
		
		LotteryRound round = customLotteryMapper.selectRoundByRoundId(order.getRoundid());
		List<LotteryRoundItem> itemList = round.getRoundItemList();
		for (LotteryOrderDetail orderDetail : order.getOrderDetailList()) {
			LotteryRoundItem tempItem = new LotteryRoundItem();
			tempItem.setItemno(orderDetail.getItemno());
			LotteryRoundItem roundItem = itemList.get(itemList.indexOf(tempItem));

			orderDetail.setItemscale(roundItem.getItemscale());
			orderDetail.setOrderid(order.getOrderid());
			lotteryOrderDetailMapper.insertSelective(orderDetail);
			
		}
		//lotteryOrderMapper.updateByPrimaryKeySelective(order);
		addTradeInfo(accountDetail, order, 0.0-orderAmount, EnumType.RalativeType.Order.ID);//减去下注金额
		return order;
	}

	// 根据中奖结果更新投注单结果
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
	public String updateOrderByRound(LotteryRound round, LotteryOrder order, List<LotteryItem> itemList){
		Double windOrder = 0.0;
		Double lossOrder = 0.0;
		Double drawOrder = 0.0;//和局
		Double playerPrize = 0.0;//奖金，其中包含派彩跟抽水
		Double totalReturn = 0.0;//总返利
		Double playerReturn = 0.0;// 给玩家的返利
		Double agencyReturn = 0.0;//给代理的返利
		AccountDetail account = accountDetailMapper.selectByPrimaryKey(order.getAccountid());
		double playRatio = account.getRatio()==null?0.0:account.getRatio();
		double maxRatio = 0.0;
		
		List<AccountDetail> parentAccounts = customLotteryMapper.selectAccountBySupUserName(account.getSupusername());
		if(parentAccounts.size()<1){
			return "订单号"+order.getOrderid()+"对应的账号"+account.getAccountid()+"无上级代理,兑奖失败!";
		}else{
			maxRatio = parentAccounts.get(0).getRatio();//获取超级代理的返利
		}
		
		if(maxRatio<playRatio){
			return "订单号"+order.getOrderid()+"对应的账号"+account.getAccountid()+"设置的洗码比不能大于一级代理,兑奖失败!";
		}
		
		
		for (LotteryOrderDetail detail : order.getOrderDetailList()) {// 投注细项汇总
			LotteryItem tempItem = new LotteryItem();
			tempItem.setItemno(detail.getItemno());
			LotteryItem item = itemList.get(itemList.indexOf(tempItem));
			//double returnAmount = detail.getDetailamount() * maxRatio;//返利计算
			double returnAmount = detail.getDetailamount() * playRatio;//返利计算
			int winResult = isWinPrize(round, item);
			if (winResult==1) {
				Double prizeAmount = detail.getDetailamount() * (detail.getItemscale() - 1);
				detail.setPrizeamount(prizeAmount);
				detail.setActualamount(prizeAmount);
				windOrder += detail.getDetailamount();
				playerPrize += prizeAmount;
				
			}else if(winResult==0){
				//returnAmount = 0.0;//和局没有返利
				returnAmount = 0.0;
				drawOrder += detail.getDetailamount();
				detail.setPrizeamount(0.0);
				detail.setActualamount(0.0);
			}else {
				lossOrder += detail.getDetailamount();
				detail.setPrizeamount(0.0);
				detail.setActualamount(-detail.getDetailamount());
			}
			detail.setReturnamount(returnAmount);
			//totalReturn += returnAmount;
			//playerReturn += returnAmount;
			lotteryOrderDetailMapper.updateByPrimaryKeySelective(detail);
		}

		totalReturn = (windOrder+lossOrder)*maxRatio;
		playerReturn = (windOrder+lossOrder)*playRatio;
		agencyReturn = totalReturn - playerReturn;
		//AccountDetail systemAccount = parentAccounts.get(0);// 系统账户
		AccountDetail systemAccount = accountDetailMapper.selectByPrimaryKey(1);// 系统账户
		Double systemCommision = (windOrder+lossOrder) * 0.005;// 系统平台抽取佣金, 即公司损益
		
		Double playerWinloss = 0.0;//玩家的输赢,不包含返利的计算
		Double agencyWinloss = 0.0; //代理的输赢,包含公司损益、返利的计算
		Double agencyPrize = 0.0;//代理奖金
		Double feeAmount = totalReturn + systemCommision;//洗码费=返利+公司损益
		

		playerWinloss = playerPrize - lossOrder;
		agencyPrize = 0.0 - playerWinloss;
		agencyWinloss = agencyPrize - feeAmount;
		
		order.setPrizeamount(playerPrize);
		order.setActualamount(playerWinloss);
		order.setReturnamount(playerReturn);
		order.setPrizetime(new Date());
		order.setCommisionamount(agencyWinloss);
		order.setAgencyreturn(agencyReturn);
		order.setSystemamount(systemCommision);
		lotteryOrderMapper.updateByPrimaryKeySelective(order);
		
		if(windOrder+drawOrder>0.0){
			addTradeInfo(account, order, windOrder+drawOrder, EnumType.RalativeType.Order.ID);//玩家中奖跟和局的本金返还
		}
		
		if(playerPrize>0.0){
			addTradeInfo(account, order, playerPrize, EnumType.RalativeType.PlayerWin.ID);//玩家派彩
		}
		if(playerReturn>0.0){
			addTradeInfo(account, order, playerReturn, EnumType.RalativeType.Return.ID);// 给玩家的返利
		}
		Double childPercent = 0.0;//下级代理占比
		Double childRatio = playRatio;//下级洗码比
		for(int i=parentAccounts.size()-1; i>=0; i-- ){
			AccountDetail tempAccount = parentAccounts.get(i);
			double curPercentage = tempAccount.getPercentage()==null?0.0:tempAccount.getPercentage()-childPercent;
			double curWinloss = 0.0;
			double curPrize = 0.0;
			double curFee = 0.0;
			double curReturn =0.0;
			if(curPercentage>0.0){
				curPrize = agencyPrize*curPercentage;
				curFee = feeAmount*curPercentage;
				//curWinloss = agencyWinloss*curPercentage;//当前代理输赢
				childPercent = tempAccount.getPercentage();
			}
			
			//if(parentAccounts.size()==1||(parentAccounts.size()>1&&i!=0)){//玩家的上级是超级代理时计算超代的返利,玩家的上级不是超级代理时不计算超级代理的返利
			double curRatio = tempAccount.getRatio()==null?0.0:tempAccount.getRatio()-childRatio;
			if(curRatio>0.0){
				curReturn = (windOrder+lossOrder)*curRatio;//当前代理返利
				childRatio = tempAccount.getRatio();
			}
			//addTradeInfo(tempAccount, order, curWinloss+curReturn, EnumType.RalativeType.AgencyWin.ID);// 代理输赢
			curWinloss = curPrize - curFee + curReturn;
			LotteryOrderResult orderResult = new LotteryOrderResult();
			orderResult.setAccountid(tempAccount.getAccountid());
			orderResult.setOrderid(order.getOrderid());
			orderResult.setInputtime(new Date());
			orderResult.setPrizeamount(curPrize);
			orderResult.setFeeamount(curFee);
			orderResult.setReturnamount(curReturn);
			orderResult.setWinamount(curWinloss);
			lotteryOrderResultMapper.insert(orderResult);
			
		}
		addTradeInfo(parentAccounts.get(0), order, agencyWinloss+agencyReturn, EnumType.RalativeType.AgencyWin.ID);// 代理输赢
		addTradeInfo(systemAccount, order, systemCommision, EnumType.RalativeType.Commision.ID);// 系统平台抽取佣金
		return "";
	}

	// 新增一级代理佣金款项
	private void addTradeInfo(AccountDetail account, LotteryOrder order, Double tradeAmount, String relativeType) {
		BigDecimal accountAmount = account.getMoney();
		if(tradeAmount!=null){
			accountAmount = accountAmount.add(new BigDecimal(tradeAmount));
			TradeInfo tradeInfo = new TradeInfo();
			tradeInfo.setAccountid(account.getAccountid());
			tradeInfo.setTradetype(EnumType.TradeType.Trade.ID);
			tradeInfo.setRelativetype(relativeType);
			tradeInfo.setRelativeid(order.getOrderid());
			tradeInfo.setTradeamount(tradeAmount);
			tradeInfo.setAccountamount(accountAmount);
			tradeInfo.setInputtime(new Date());
			tradeInfoMapper.insertSelective(tradeInfo);
	
			account.setMoney(accountAmount);
			accountDetailMapper.updateByPrimaryKeySelective(account);
			
			if (order.getAccountid().equals(account.getAccountid())) {// 更新玩家下单记录的账户余额
				order.setAccountamount(accountAmount);
				lotteryOrderMapper.updateByPrimaryKeySelective(order);
			}
		}
	}
	
	// 根据中奖结果判断是否中奖
	private int isWinPrize(LotteryRound round, LotteryItem item) {
		String[] winNumbers = item.getWinitem().split(",");
		String[] prizeNumbers = round.getOriginresult().split(",");
		int prizeNumber = Integer.parseInt(prizeNumbers[prizeNumbers.length-1]);
		int cronNumber = Integer.parseInt(round.getResultstr());
		
		//int prizeNumber = Integer.parseInt(round.getOriginresult());
		//int cronNumber = prizeNumber % 4 == 0 ? 4 : prizeNumber % 4;
		

		// String[] prizeNumbers = round.getResultstr().split(",");
		if (item.getItemtype().equals(EnumType.ItemType.Type_01.ID)) {
			for (int i = 0; i < winNumbers.length; i++) {
				if (winNumbers[i].equals(String.valueOf(cronNumber))) {
					return 1;
				}else if(StringUtils.isNotBlank(item.getDrawitem())){
					String[] drawNumbers = item.getDrawitem().split(",");
					for (int j = 0; j < drawNumbers.length; j++) {
						if (drawNumbers[j].equals(String.valueOf(cronNumber))) {
							return 0;
						}
					}
				}
			}
		} else if (item.getItemtype().equals(EnumType.ItemType.Type_02.ID)) {
			for (int i = 0; i < winNumbers.length; i++) {
				if (winNumbers[i].equals(String.valueOf(prizeNumber))) {
					return 1;
				}
			}
		}
		return -1;
	}

	public String checkLotteryOrder(AccountDetail accountDetail, LotteryRound round, LotteryOrder order) {
		String[] typeOrder = {"角","连","番","正","三中","特码","色","大小","单双"};
		List<Map<String, String>> detailList = customLotteryMapper.selectOrderForCheck(order.getRoundid(), order.getAccountid());
		Map<String, Double> tempMap = new HashMap<String, Double>();
		
		OffAccountInfo accountInfo = offAccountInfoMapper.selectByUsername(accountDetail.getSupusername());
		List<LotteryItem> itemList = customLotteryMapper.selectItemByLottery(EnumType.Lottery.YMZ.ID);
		Map<String, String> itemGroupMap = new HashMap<String, String>();
		for (LotteryItem item : itemList) {
			itemGroupMap.put(item.getItemno(), item.getItemgroup());
		}
		
		for (Map<String, String> detailMap : detailList) {
			tempMap.put(detailMap.get("ItemGroup"), Double.parseDouble(String.valueOf(detailMap.get("Amount"))));
		}
		Double totalOrderAmount = 0.0;
		for (LotteryOrderDetail detail : order.getOrderDetailList()) {
			String itemGroup = itemGroupMap.get(detail.getItemno());
			if(itemGroup==null) return "不存在下注项:"+detail.getItemno();
			Double amount = detail.getDetailamount();
			if (tempMap.containsKey(itemGroup)) {
				tempMap.put(itemGroup, tempMap.get(itemGroup) + amount);
			} else {
				tempMap.put(itemGroup, amount);
			}
			totalOrderAmount += amount;
		}
		if(new BigDecimal(totalOrderAmount).compareTo(accountDetail.getMoney())>0){
			return "下注金额不能超过点数限额";
		}
		if(StringUtils.isBlank(accountInfo.getRiskamount()))	return "风控限额还未配置";
		String[] riskAmount = accountInfo.getRiskamount().split(";");
		if(riskAmount.length==riskAmount.length){
			for(int i=0; i<typeOrder.length; i++){
				if(getMapVal(tempMap, typeOrder[i])>Double.parseDouble(riskAmount[i])){
					return "下注金额不能超过风控限额";
				}
			}
		}else{
			return "风控限额配置有错误";
		}
		
		return "";
	}
	// 添加账户
	/*public boolean checkLotteryOrder(LotteryOrder order) {
		List<Map<String, String>> detailList = customLotteryMapper.selectOrderForCheck(order.getRoundid(), order.getAccountid());
		Map<String, Double> tempMap = new HashMap<String, Double>();
		AccountDetail account = accountDetailMapper.selectByPrimaryKey(order.getAccountid());
		Double topAmount = account.getLimited();
		for (Map<String, String> detailMap : detailList) {
			tempMap.put(detailMap.get("ItemNo"), Double.parseDouble(String.valueOf(detailMap.get("Amount"))));
		}
		for (LotteryOrderDetail detail : order.getOrderDetailList()) {
			String itemNo = detail.getItemno();
			Double amount = detail.getDetailamount();
			if (tempMap.containsKey(itemNo)) {
				tempMap.put(itemNo, tempMap.get(itemNo) + amount);
			} else {
				tempMap.put(itemNo, amount);
			}
		}
		if (Math.abs(getMapVal(tempMap, "BIG") - getMapVal(tempMap, "SMALL")) > topAmount) {
			return false;
		} else if (Math.abs(getMapVal(tempMap, "SINGLE") - getMapVal(tempMap, "DOUBLE")) > topAmount) {
			return false;
		} else {
			Double maxAmount = getMapVal(tempMap, "RED");
			Double minAmount = getMapVal(tempMap, "RED");
			if (maxAmount < getMapVal(tempMap, "BLUE")) {
				maxAmount = getMapVal(tempMap, "BLUE");
			}
			if (maxAmount < getMapVal(tempMap, "GREEN")) {
				maxAmount = getMapVal(tempMap, "GREEN");
			}

			if (minAmount > getMapVal(tempMap, "BLUE")) {
				minAmount = getMapVal(tempMap, "BLUE");
			}
			if (minAmount > getMapVal(tempMap, "GREEN")) {
				minAmount = getMapVal(tempMap, "GREEN");
			}
			if (maxAmount - minAmount > topAmount)
				return false;
		}
		return true;
	}*/

	private Double getMapVal(Map<String, Double> tempMap, String key) {
		if (tempMap.get(key) == null) {
			return 0.0;
		} else {
			return tempMap.get(key);
		}
	}

}
