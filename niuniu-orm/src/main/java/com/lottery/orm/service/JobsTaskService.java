package com.lottery.orm.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lottery.orm.bo.LotteryGameOrder;
import com.lottery.orm.bo.LotteryGameRound;
import com.lottery.orm.dao.AccountAmountMapper;
import com.lottery.orm.dao.LotteryGameOrderMapper;
import com.lottery.orm.dao.LotteryGameRoundMapper;
import com.lottery.orm.dao.TradeInfoMapper;
import com.lottery.orm.dto.LotteryAmountDto;

@Service
@Transactional
public class JobsTaskService {
	
	@Autowired
	private LotteryGameRoundMapper lotteryGameRoundMapper;
	
	@Autowired
	private LotteryGameOrderMapper lotteryGameOrderMapper;
	
	
	@Autowired
	private AccountAmountMapper accountAmountMapper;
	
	@Autowired
	private TradeInfoMapper tradeInfoMapper;
	/**
	 *试玩账户删除
	 * @throws Exception 
	 */
	public synchronized void LotteryPlayerDelete() throws Exception{
		List<LotteryGameRound> list =  lotteryGameRoundMapper.selectLotteryOrderPlayer();
		Date openTime = null;
		int[] accoundis = {888,987,988,989,990,991,992,993,994,995,996,997,998,999};
		for (int i = 0;i<list.size();i++){
			LotteryGameRound lr = new LotteryGameRound();
			lr = list.get(i);	
			openTime = lr.getOpentime();
			if (new Date().getTime() - openTime.getTime()/1000>700){
			    lotteryGameOrderMapper.deleteByPlayerBatch(lr.getSid(),openTime);
			    for (int j = 0;j<accoundis.length;j++){
			    	tradeInfoMapper.deleteByPlayer(accoundis[j]);
			    	accountAmountMapper.deleteByPlayer(accoundis[j]);
			    }
			}
		}
	}
		
	/**
	 *公司上庄
	 * @throws Exception 
	 */
	public synchronized void Taskplayoridel() throws Exception{
		java.util.Random random=new java.util.Random();// 定义随机类
		List<LotteryGameRound> list =  lotteryGameRoundMapper.selectLotteryPlayoridle();
		String sid = "";
		Date currentDate = new Date();
		Date overTime = null;
	    int  noid  = 0;
		for (int i = 0;i<list.size();i++){
			LotteryGameRound lr = new LotteryGameRound();
			lr = list.get(i);	
			sid = String.valueOf(lr.getSid());
			overTime = lr.getOvertime();
			if ((currentDate.getTime()-overTime.getTime())/1000<=50){
				if (sid.substring(0, 1).equals("1"))
					noid = 10;
				else 
					noid = 5;
				List<LotteryGameOrder> lists =  lotteryGameOrderMapper.selectGamePlayoridle(lr.getLotteryterm(), lr.getSid());
				for (int j = 0;j<lists.size();j++){
					LotteryGameOrder la = new LotteryGameOrder();
					la = lists.get(j);
					int result = random.nextInt(noid);
			        LotteryGameOrder order = new LotteryGameOrder();
			        order.setSid(lr.getSid());
			    	order.setRmid(la.getRmid());
			    	order.setLtdid(result+1);
			    	order.setNoid(result+1);
			    	order.setPlayoridle("1");
			    	order.setLotteryterm(lr.getLotteryterm());
			    	order.setOrderamount(BigDecimal.valueOf(20000));
			    	order.setOrdertime(new Date());
			    	order.setOpentime(lr.getOpentime());
			        lotteryGameOrderMapper.insertSelective(order);
				}
			}
		}
	}
	

	public static void main(String[] args) {
		
	}
}
