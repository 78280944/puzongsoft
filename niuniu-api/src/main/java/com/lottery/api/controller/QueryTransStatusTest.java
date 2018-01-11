package com.lottery.api.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.colotnet.util.ConfigUtils;
import com.colotnet.util.SSLClient;
import com.colotnet.util.SignUtils;
import com.lottery.orm.bo.AccountInfo;
import com.lottery.orm.bo.AccountRecharge;
import com.lottery.orm.dao.AccountInfoMapper;
import com.lottery.orm.dao.AccountRechargeMapper;
/**
 * 类QueryTransStatusTest.java的实现描述：交易状态查询
 * @author pay 2016年4月27日 下午3:16:27
 */
@Service
@Transactional
public class QueryTransStatusTest {
	
	@Autowired
	private Mapper mapper;
	
	@Autowired
    private AccountRechargeMapper accountRechargeMapper;
	
	@Autowired
    private AccountInfoMapper accountInfoMapper;
	
	public synchronized  String getPayResults(AccountRecharge aRecharge) throws Exception{
    
   // public static void main(String[] args) throws Exception {
    	
        DefaultHttpClient httpClient = new SSLClient();
        HttpPost postMethod = new HttpPost(ConfigUtils.getProperty("trans_url"));
        List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        nvps.add(new BasicNameValuePair("requestNo", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())));
        nvps.add(new BasicNameValuePair("version", "V1.0"));
        nvps.add(new BasicNameValuePair("transId", "64"));
        nvps.add(new BasicNameValuePair("merNo", ConfigUtils.getProperty("merchant_no")));
        nvps.add(new BasicNameValuePair("orderDate", aRecharge.getOrderdate()));//new SimpleDateFormat("yyyyMMdd").format(new Date()))
        nvps.add(new BasicNameValuePair("orderNo",aRecharge.getOrderno()));//new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
        nvps.add(new BasicNameValuePair("remark", ""));
        nvps.add(new BasicNameValuePair("extendField", ""));
        nvps.add(new BasicNameValuePair("signature", SignUtils.signData(nvps)));
        try {
            postMethod.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            HttpResponse resp = httpClient.execute(postMethod);
            String str = EntityUtils.toString(resp.getEntity(), "UTF-8");
            System.out.println("请求返回数据："+str);
            int statusCode = resp.getStatusLine().getStatusCode();
            if (200 == statusCode) {
                boolean signFlag = SignUtils.verferSignData(str);
                if (!signFlag) {
                    System.out.println("验签失败");
                    return "false";
                }
                Map mapTypes = JSON.parseObject(str);
                Map<String, Object> data = new HashMap<String, Object>();
                for (Object obj : mapTypes.keySet()){  
                    if((!obj.toString().equals("signature"))){
                    	if (obj.toString().equals("respCode")){
                   		 aRecharge.setRespcode(mapTypes.get(obj).toString());
                   	 }else if (obj.toString().equals("respDesc")){
                   		 aRecharge.setRespdesc(mapTypes.get(obj).toString());
                   	 }
                    }
                }
                if (aRecharge.getRespcode().equals("0000"))
                	aRecharge.setOrderstate("01");
                else if (aRecharge.getRespcode().equals("P000")){
                	
                }else{
                	aRecharge.setOrderstate("02");
                	AccountInfo aInfo = accountInfoMapper.selectByPrimaryKey(aRecharge.getAccountid());
                	aInfo.setUsermoney(aInfo.getUsermoney().add(BigDecimal.valueOf((double)(aRecharge.getTransamt()))));
            		aRecharge.setAccountamount(aInfo.getUsermoney().add(BigDecimal.valueOf((double)(aRecharge.getTransamt()))));
            		accountInfoMapper.updateByPrimaryKey(aInfo);
            		accountRechargeMapper.updateByPrimaryKey(aRecharge);
                }
                accountRechargeMapper.updateByRechargeCashResult(aRecharge);
                System.out.println("验签成功");
                return "true";
            }
            System.out.println("返回错误码:" + statusCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    	return "fasle";
    }
  public static void main(String args[]) throws Exception{
		AccountRecharge aRecharge = new AccountRecharge();
		aRecharge.setAccountid(1000);
		aRecharge.setTransamt(1000);
		aRecharge.setProductid("1205");
	  QueryTransStatusTest t = new QueryTransStatusTest();
	  t.getPayResults(aRecharge);
  }
	
}
