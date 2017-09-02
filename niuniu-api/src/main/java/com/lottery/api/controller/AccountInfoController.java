package com.lottery.api.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lottery.api.dto.AccountInfoVo;
import com.lottery.api.dto.LoginParamVo;
import com.lottery.api.dto.PlayAccountInfoVo;
import com.lottery.api.dto.UpdateAccountVo;
import com.lottery.api.filter.LockedClientException;
import com.lottery.api.util.Des3Util;
import com.lottery.api.util.ToolsUtil;
import com.lottery.orm.bo.AccountDetail;
import com.lottery.orm.bo.AccountInfo;
import com.lottery.orm.bo.OffAccountInfo;
import com.lottery.orm.dao.AccountDetailMapper;
import com.lottery.orm.dao.AccountInfoMapper;
import com.lottery.orm.dao.OffAccountInfoMapper;
import com.lottery.orm.dto.AccountInfoDto;
import com.lottery.orm.dto.RemarkDto;
import com.lottery.orm.result.AccountListResult;
import com.lottery.orm.result.AccountResult;
import com.lottery.orm.result.RemarkResult;
import com.lottery.orm.result.RestResult;
import com.lottery.orm.service.AccountInfoService;
import com.lottery.orm.util.MessageTool;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@RequestMapping(value = "/account", produces = {"application/json;charset=UTF-8"})
@Api(value = "/account", description = "玩家帐号信息接口")
@Controller
public class AccountInfoController {
	public static final Logger LOG = Logger.getLogger(AccountInfoController.class);
	
	@Autowired
	private Mapper mapper;
	
	@Autowired
    private AccountInfoService accountInfoService;
	
	@Autowired
    private AccountInfoMapper accountInfoMapper;
	
	@Autowired
    private AccountDetailMapper accountDetailMapper;
	
	@Autowired
    private OffAccountInfoMapper offAccountInfoMapper;
	
	@Value("${jwt.splitter}")
    private String tokenSplitter;
	
	@Value("${jwt.secret}")
    private String tokenSecret;
	
	@Value("${lottery.shareUrl}")
    private String shareUrl;
	
	@Value("${lottery.shareCodeImg}")
    private String shareCodeImg;
	
	@Value("${lottery.ruleUrl}")
    private String ruleUrl;
	
	@Value("${lottery.serviceTel}")
    private String serviceTel;
	
	@Value("${lottery.androidAppVersion}")
    private String androidAppVersion;
	
	@Value("${lottery.iosAppVersion}")
    private String iosAppVersion;

	
	@ApiOperation(value = "获取玩家或者代理商、子代理商信息", notes = "获取玩家或者代理商、子代理商信息", httpMethod = "POST")
	@RequestMapping(value = "/getAccountInfo", method = RequestMethod.POST)
	@ResponseBody
	public AccountResult getAccountInfo(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody LoginParamVo param) throws Exception {
		AccountResult result = new AccountResult();
		try {
			
			String username = param.getUsername();
			String password = param.getPassword();
			
			//参数合规性校验，必要参数不能为空
			if (ToolsUtil.isEmptyTrim(username)||ToolsUtil.isEmptyTrim(password)){
			      result.fail(MessageTool.Code_2002);
			      LOG.info(result.getMessage());
			      return result;
			}
			param.setPassword(DigestUtils.md5Hex(password));
		    AccountInfo paraInfo = mapper.map(param, AccountInfo.class);
		    AccountInfo accountInfo = accountInfoMapper.selectByLogin(paraInfo);
		    if(accountInfo!=null){
		    	AccountDetail accountDetail = accountDetailMapper.selectByUserId(accountInfo.getUserid(), "3");
		    	//获取上级的限额等信息
			    OffAccountInfo leOffAccountInfo = offAccountInfoMapper.selectByUsername(accountInfo.getSupusername());
				if(leOffAccountInfo==null){ 	
				      result.fail("管理员",MessageTool.Code_3002);
				      LOG.info(result.getMessage());
				      return result;
				  }
				
				if(accountDetail.getState().equals("0")){
					throw new LockedClientException();
				}
				    	
				AccountInfoDto rAcDto = new AccountInfoDto();
				rAcDto.setUserid(null==accountInfo.getUserid()||"".equals(accountInfo.getUserid())||0.0==accountInfo.getUserid() ?0:accountInfo.getUserid());
				rAcDto.setUsername(accountInfo.getUsername());
				rAcDto.setAusername(accountInfo.getAusername());
				rAcDto.setPassword(accountInfo.getPassword());
				//rAcDto.setLimited(null==accountInfo.getLimited()||"".equals(accountInfo.getLimited())||0.0==accountInfo.getLimited() ?0.0:accountInfo.getLimited());
				rAcDto.setRatio(null==accountInfo.getRatio()||"".equals(accountInfo.getRatio())||0.0==accountInfo.getRatio() ?0:accountInfo.getRatio());
				rAcDto.setState(null==accountInfo.getState()||"".equals(accountInfo.getState()) ? "":accountInfo.getState());
				rAcDto.setSupusername(null==accountInfo.getSupusername()||"".equals(accountInfo.getSupusername()) ? "":accountInfo.getSupusername());
				rAcDto.setLevel(null==accountInfo.getLevel()||"".equals(accountInfo.getLevel()) ? "":accountInfo.getLevel());
				rAcDto.setPhone(null==accountInfo.getPhone()||"".equals(accountInfo.getPhone()) ? "":accountInfo.getPhone());
				rAcDto.setWebchat(null==accountInfo.getWebchat()||"".equals(accountInfo.getWebchat()) ? "":accountInfo.getWebchat());
				rAcDto.setPercentage(0.0);
				rAcDto.setQuery("");
				rAcDto.setOfftype("3");
				rAcDto.setAccountID(accountDetail.getAccountid());
				rAcDto.setRiskamount("");
			    //rAcDto.setLelimited(leOffAccountInfo.getLimited());
			    rAcDto.setLepercentage(leOffAccountInfo.getPercentage());
			    rAcDto.setLeratio(leOffAccountInfo.getRatio());
			    rAcDto.setLeriskamount(leOffAccountInfo.getRiskamount());
				rAcDto.setAccountAmount(null==accountDetail.getMoney()||"".equals(accountDetail.getMoney())||BigDecimal.valueOf(0) == accountDetail.getMoney()?BigDecimal.valueOf(0):accountDetail.getMoney());
				rAcDto.setToken((new Des3Util()).encode(accountDetail.getAccountid()+tokenSplitter+tokenSecret));
				result.success(rAcDto);
		    }else {
		    	OffAccountInfo offparaInfo = mapper.map(param, OffAccountInfo.class);
		    	OffAccountInfo offaccountInfo = offAccountInfoMapper.selectByLogin(offparaInfo);
		    	if (offaccountInfo!=null){
			    	//判断子账户
			    	if (offaccountInfo.getOfftype().equals("2")){
			    		String query = offaccountInfo.getQuery();
			    		offaccountInfo = offAccountInfoMapper.selectByUsername(offaccountInfo.getSupusername());
			    		offaccountInfo.setQuery(query);
			    	}
		    		
			    	AccountDetail accountDetail = accountDetailMapper.selectByUserId(offaccountInfo.getUserid(), offaccountInfo.getOfftype());
			    	if (accountDetail == null){
					      result.fail(MessageTool.Code_3002);
					      LOG.info(result.getMessage());
					      return result;
			    	}
			    	if(accountDetail.getState().equals("0")){
						throw new LockedClientException();
					}
					AccountInfoDto rAcDto = new AccountInfoDto();
					rAcDto.setUserid(null==offaccountInfo.getUserid()||"".equals(offaccountInfo.getUserid())||0.0==offaccountInfo.getUserid() ?0:offaccountInfo.getUserid());
					rAcDto.setUsername(offaccountInfo.getUsername());
					rAcDto.setAusername(offaccountInfo.getAusername());
					rAcDto.setPassword(offaccountInfo.getPassword());
					//rAcDto.setLimited(null==offaccountInfo.getLimited()||"".equals(offaccountInfo.getLimited())||0.0==offaccountInfo.getLimited() ?0.0:offaccountInfo.getLimited());
					rAcDto.setRatio(null==offaccountInfo.getRatio()||"".equals(offaccountInfo.getRatio())||0.0==offaccountInfo.getRatio() ?0:offaccountInfo.getRatio());
					rAcDto.setState(null==offaccountInfo.getState()||"".equals(offaccountInfo.getState()) ? "":offaccountInfo.getState());
					rAcDto.setSupusername(null==offaccountInfo.getSupusername()||"".equals(offaccountInfo.getSupusername()) ? "":offaccountInfo.getSupusername());
					rAcDto.setLevel(null==offaccountInfo.getLevel()||"".equals(offaccountInfo.getLevel()) ? "":offaccountInfo.getLevel());
					rAcDto.setPhone("");
					rAcDto.setWebchat("");
					rAcDto.setPercentage(null==offaccountInfo.getPercentage()||"".equals(offaccountInfo.getPercentage())||0.0==offaccountInfo.getPercentage() ?0.0:offaccountInfo.getPercentage());
					rAcDto.setQuery(null==offaccountInfo.getQuery()||"".equals(offaccountInfo.getQuery()) ? "":offaccountInfo.getQuery());
					//rAcDto.setManage(null==offaccountInfo.getManage()||"".equals(offaccountInfo.getManage()) ? "":offaccountInfo.getManage());
					rAcDto.setOfftype(offaccountInfo.getOfftype());
					rAcDto.setAccountID(accountDetail.getAccountid());
					rAcDto.setAccountAmount(null==accountDetail.getMoney()||"".equals(accountDetail.getMoney())||BigDecimal.valueOf(0) == accountDetail.getMoney()?BigDecimal.valueOf(0):accountDetail.getMoney());
					rAcDto.setRiskamount(null==offaccountInfo.getRiskamount()||"".equals(offaccountInfo.getRiskamount()) ?"":offaccountInfo.getRiskamount());
					rAcDto.setToken((new Des3Util()).encode(accountDetail.getAccountid()+tokenSplitter+tokenSecret));
					result.success(rAcDto);	
		    	}else
		    	   result.fail(MessageTool.Code_3001);
		    }
			LOG.info(username+","+result.getMessage()+","+new Date());
		} catch (LockedClientException e) {
			throw new LockedClientException();
		}catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(),e);
		}
		return result;

	}
	
	@ApiOperation(value = "新增玩家", notes = "新增玩家", httpMethod = "POST")
	@RequestMapping(value = "/addAccountInfo", method = RequestMethod.POST)
	@ResponseBody
	public RestResult addAccountInfo(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody PlayAccountInfoVo param) throws Exception {
		RestResult result = new RestResult();
		try {
			String username = param.getUsername();
			String password = param.getPassword();
			String supusername = param.getSupusername();
			String level = param.getLevel();
			//Double limited =  null;
			Double ratio = null;
			
			if (null != param.getRatio())
				ratio = param.getRatio();		
			
			//参数合规性校验，必要参数不能为空
			if (ToolsUtil.isEmptyTrim(username)||ToolsUtil.isEmptyTrim(password)){
			      result.fail("用户名，密码",MessageTool.Code_2002);
			      LOG.info(result.getMessage());
			      return result;
			}
			

			//最长14个英文或者数字组合
			if (ToolsUtil.validatName(username)){
			      result.fail("用户名",MessageTool.Code_1006);
			      LOG.info(result.getMessage());
			      return result;
			}
			
			if (ToolsUtil.isEmptyTrim(supusername)||ToolsUtil.isEmptyTrim(level)){
			      result.fail("管理操作员，代理级别",MessageTool.Code_2002);
			      LOG.info(result.getMessage());
			      return result;
			}
			
			//数字型
			if (null != ratio){
				if (ToolsUtil.isNumeric(String.valueOf(ratio))){
				      result.fail("洗码比",MessageTool.Code_1004);
				      LOG.info(result.getMessage());
				      return result;		
				}
			}
			
			//玩家是否存在，用户名不能一致
			param.setPassword(DigestUtils.md5Hex(password));
		    AccountInfo paraInfo = mapper.map(param, AccountInfo.class);
		    AccountInfo accountInfo = accountInfoMapper.selectByUsername(paraInfo.getUsername());
		    if (accountInfo!=null){
			      result.fail(username,MessageTool.Code_2005);
		    }else{
		    	//代理账户是否存在，用户名不能一致
		    	OffAccountInfo temmpAccountInfo = offAccountInfoMapper.selectByUsername(paraInfo.getUsername());
		    	if (temmpAccountInfo!=null){
				      result.fail(username,MessageTool.Code_2005);
				      LOG.info(result.getMessage());
				      return result;	
		    	}
		    	
		    	OffAccountInfo offAccountInfo = offAccountInfoMapper.selectByUsername(paraInfo.getSupusername());
		    	if (offAccountInfo == null){
		    		result.fail(supusername,MessageTool.Code_2006);
				    LOG.info(result.getMessage());
				    return result;
		    	}
		    	paraInfo.setLevel(offAccountInfo.getLevel());
			    paraInfo.setState("1");//默认状态正常
			    paraInfo.setInputdate(new Date());
			    paraInfo.setLimited(Double.parseDouble("0.0"));
			    accountInfoService.addAccountInfo(paraInfo);
			    result.success();
		    }
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(),e);
		}
		return result;
	}
	
	@ApiOperation(value = "修改玩家", notes = "修改玩家", httpMethod = "POST")
	@RequestMapping(value = "/updateAccountInfo", method = RequestMethod.POST)
	@ResponseBody
	public RestResult updatePlayAccountInfo(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody UpdateAccountVo param) throws Exception {
		RestResult result = new RestResult();
		try {
			int userid = param.getUserid();
			String username = param.getUsername();
			String password = param.getPassword();
			String state = param.getState();
			String phone =  param.getPhone();
			String webchat = param.getWebchat();			
			
			//参数合规性校验，必要参数不能为空
			if (ToolsUtil.isEmptyTrim(username)){
			      result.fail("用户名",MessageTool.Code_2002);
			      LOG.info(result.getMessage());
			      return result;
			}
			
		
			if (0==userid){
			      result.fail("用户ID",MessageTool.Code_2002);
			      LOG.info(result.getMessage());
			      return result;
			}
			
			AccountInfo accountInfo = accountInfoMapper.selectByPrimaryKey(param.getUserid());
			if(accountInfo==null){
			      result.fail(MessageTool.Code_3001);
			}else{
				password = DigestUtils.md5Hex(password);	
			    AccountInfo paraInfo = mapper.map(param, AccountInfo.class);
			    AccountInfo accountInfocheck = accountInfoMapper.selectByUserAndId(paraInfo);
			    if (accountInfocheck!=null){
				      result.fail(username,MessageTool.Code_2005);
				      LOG.info(result.getMessage());
				      return result;	
			    }
			    paraInfo.setUsername(null==param.getUsername()||"".equals(param.getUsername()) ? accountInfo.getUsername():param.getUsername());
			    paraInfo.setAusername(accountInfo.getAusername());
			    paraInfo.setPassword(null==param.getPassword()||"".equals(param.getPassword()) ? accountInfo.getPassword():password);
			    paraInfo.setState(null==param.getState()||"".equals(param.getState()) ?  accountInfo.getState():param.getState());
			    paraInfo.setPhone(null==param.getPhone()||"".equals(param.getPhone()) ?  accountInfo.getPhone():param.getPhone());
			    paraInfo.setWebchat(null==param.getWebchat()||"".equals(param.getWebchat()) ?  accountInfo.getWebchat():param.getWebchat());
			    paraInfo.setUpdateip(null==param.getIp()||"".equals(param.getIp()) ? accountInfo.getIp():param.getIp());
			    paraInfo.setUpdatedate(new Date());
			    paraInfo.setSupusername(accountInfo.getSupusername());
			    paraInfo.setLevel(accountInfo.getLevel());
			    paraInfo.setLimited(Double.parseDouble("0.0"));
			    accountInfoService.updateAccountInfo(paraInfo);
			    result.success();
			}
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(),e);
		}
		return result;
	}
	
	
	@ApiOperation(value = "获取玩家列表", notes = "获取该代理下的玩家列表", httpMethod = "POST")
	@RequestMapping(value = "/getAllAccountInfo", method = RequestMethod.POST)
	@ResponseBody
	public AccountListResult getAllAccountInfo(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody AccountInfoVo param) throws Exception {
	    AccountListResult result = new AccountListResult();
		try {
			OffAccountInfo offacount = offAccountInfoMapper.selectByPrimaryKey(param.getUserid());
			if (offacount==null){
				result.fail(MessageTool.Code_3001);
			}else{
				List<AccountInfo> accountInfos = accountInfoMapper.selectBySupusername(offacount.getUsername(), param.getBeginRow(), param.getPageSize());
	
				List<AccountInfoDto> list = new ArrayList<AccountInfoDto>();
				for (int i = 0;i<accountInfos.size();i++){
					AccountDetail accountDetail =  accountDetailMapper.selectByUserId(accountInfos.get(i).getUserid(),"3");
			    	//获取上级的限额等信息
				    OffAccountInfo leOffAccountInfo = offAccountInfoMapper.selectByUsername(accountInfos.get(i).getSupusername());
					if(leOffAccountInfo==null){ 	
					      result.fail("管理员",MessageTool.Code_3002);
					      LOG.info(result.getMessage());
					      return result;
					  }
					    
					AccountInfoDto rAcDto = new AccountInfoDto();
			        rAcDto.setUserid(null==accountInfos.get(i).getUserid()||"".equals(accountInfos.get(i).getUserid())||0==accountInfos.get(i).getUserid() ?0:accountInfos.get(i).getUserid());
			        rAcDto.setUsername(null==accountInfos.get(i).getUsername()||"".equals(accountInfos.get(i).getUsername()) ?"":accountInfos.get(i).getUsername());
			        rAcDto.setAusername(null==accountInfos.get(i).getAusername()||"".equals(accountInfos.get(i).getAusername()) ?"":accountInfos.get(i).getAusername());
			        rAcDto.setPassword(null==accountInfos.get(i).getPassword()||"".equals(accountInfos.get(i).getPassword()) ?"":accountInfos.get(i).getPassword());
			        //rAcDto.setLimited(null==accountInfos.get(i).getLimited()||"".equals(accountInfos.get(i).getLimited())||0.0==accountInfos.get(i).getLimited() ?0.0:accountInfos.get(i).getLimited());
			        rAcDto.setRatio(null==accountInfos.get(i).getRatio()||"".equals(accountInfos.get(i).getRatio())||0.0==accountInfos.get(i).getRatio() ?0.0:accountInfos.get(i).getRatio());
			        rAcDto.setLevel(null==accountInfos.get(i).getLevel()||"".equals(accountInfos.get(i).getLevel()) ?"":accountInfos.get(i).getLevel());
			        rAcDto.setState(null==accountInfos.get(i).getState()||"".equals(accountInfos.get(i).getState()) ?"":accountInfos.get(i).getState());
			        rAcDto.setSupusername(null==accountInfos.get(i).getSupusername()||"".equals(accountInfos.get(i).getSupusername()) ?"":accountInfos.get(i).getSupusername());
			        rAcDto.setOfftype("3");
			        rAcDto.setAccountID(accountDetail.getAccountid());
			        rAcDto.setAccountAmount(accountDetail.getMoney());
				    //rAcDto.setLelimited(leOffAccountInfo.getLimited());
				    rAcDto.setLepercentage(leOffAccountInfo.getPercentage());
				    rAcDto.setLeratio(leOffAccountInfo.getRatio());
				    rAcDto.setLeriskamount(leOffAccountInfo.getRiskamount());
			        list.add(rAcDto);
				}
				result.success(list);
			}
		    
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(),e);
		}
		return result;
	}
	
	@ApiOperation(value = "获取在线客服、分享链接、规则说明", notes = "获取在线客服、分享链接、规则说明", httpMethod = "POST")
	@RequestMapping(value = "/getRemarkInfo", method = RequestMethod.POST)
	@ResponseBody
	public RemarkResult getAllAccountInfo() throws Exception {
		RemarkResult result = new RemarkResult();
		RemarkDto remark = new RemarkDto();
			
		remark.setOnline(serviceTel);
		remark.setShare(shareUrl);
		remark.setRule(ruleUrl);
		remark.setShareCode(shareCodeImg);
		remark.setAndroidAppVersion(androidAppVersion);
		remark.setIosAppVersion(iosAppVersion);
		result.success(remark);

		return result;
	}
	
}
