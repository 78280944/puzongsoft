package com.lottery.api.controller;

import java.math.BigDecimal;
import java.math.BigInteger;
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
import com.lottery.api.dto.AccountRecordVo;
import com.lottery.api.dto.LoginParamVo;
import com.lottery.api.dto.NoticeTypeVo;
import com.lottery.api.dto.PlayAccountInfoVo;
import com.lottery.api.dto.UpdateAccountVo;
import com.lottery.api.filter.LockedClientException;
import com.lottery.api.util.Des3Util;
import com.lottery.api.util.ToolsUtil;
import com.lottery.orm.bo.AccountDetail;
import com.lottery.orm.bo.AccountInfo;
import com.lottery.orm.bo.AccountRecord;
import com.lottery.orm.bo.NoticeInfo;
import com.lottery.orm.bo.OffAccountInfo;
import com.lottery.orm.dao.AccountDetailMapper;
import com.lottery.orm.dao.AccountInfoMapper;
import com.lottery.orm.dao.AccountRecordMapper;
import com.lottery.orm.dao.NoticeInfoMapper;
import com.lottery.orm.dao.OffAccountInfoMapper;
import com.lottery.orm.dto.AccountInfoDto;
import com.lottery.orm.dto.AccountSimInfoDto;
import com.lottery.orm.dto.RemarkDto;
import com.lottery.orm.result.AccountListResult;
import com.lottery.orm.result.AccountResult;
import com.lottery.orm.result.AccountSimResult;
import com.lottery.orm.result.RemarkResult;
import com.lottery.orm.result.RestResult;
import com.lottery.orm.service.AccountInfoService;
import com.lottery.orm.util.CommonUtils;
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
	
	@Autowired
    private NoticeInfoMapper noticeInfoMapper;
	
	@Autowired
    private AccountRecordMapper accountRecordMapper;
	
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
			
			//参数合规性校验，必要参数不能为空;
			if (ToolsUtil.isEmptyTrim(username)||ToolsUtil.isEmptyTrim(password)){
			      result.fail(MessageTool.Code_2002);
			      LOG.info(result.getMessage());
			      return result;
			}
			param.setPassword(DigestUtils.md5Hex(password));
		    AccountInfo paraInfo = mapper.map(param, AccountInfo.class);
		    AccountInfo accountInfo = accountInfoMapper.selectByLogin(paraInfo);
		    if(accountInfo!=null){	
		    	
				if(accountInfo.getState().equals("0")){
					throw new LockedClientException();
				}
				AccountRecord aRecord = new AccountRecord();
				String sRecordid = CommonUtils.getCurrentMills();
				
				aRecord.setRecordid(sRecordid);
				System.out.println(sRecordid+"..."+aRecord.getRecordid());
				aRecord.setAccountid(accountInfo.getAccountid());
				aRecord.setIp(param.getIp());
				aRecord.setLevel(accountInfo.getLevel());
				aRecord.setOfftype(accountInfo.getOfftype());
				aRecord.setInputtime(new Date());
				accountRecordMapper.insert(aRecord);
				AccountInfoDto rAcDto = new AccountInfoDto();
				rAcDto = mapper.map(accountInfo, AccountInfoDto.class);
			    rAcDto.setToken((new Des3Util()).encode(accountInfo.getAccountid()+tokenSplitter+tokenSecret));
			    rAcDto.setRecordid(sRecordid);
			    result.success(rAcDto);
		    }else {
			      result.fail(MessageTool.Code_3001);
			      LOG.info(result.getMessage());
			      return result;
		    }
			LOG.info(username+","+result.getMessage()+","+new Date());
			//登录
			
		} catch (LockedClientException e) {
			throw new LockedClientException();
		}catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(),e);
		}
		return result;

	}
	
	@ApiOperation(value = "获取账户信息", notes = "获取账户信息", httpMethod = "POST")
	@RequestMapping(value = "/getAccountSimInfo", method = RequestMethod.POST)
	@ResponseBody
	public AccountSimResult getAccountSimpleInfo(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody LoginParamVo param) throws Exception {
		AccountSimResult result = new AccountSimResult();
		try {
			String username = param.getUsername();
			String password = param.getPassword();
			
			//参数合规性校验，必要参数不能为空;
			if (ToolsUtil.isEmptyTrim(username)||ToolsUtil.isEmptyTrim(password)){
			      result.fail(MessageTool.Code_2002);
			      LOG.info(result.getMessage());
			      return result;
			}
			param.setPassword(DigestUtils.md5Hex(password));
		    AccountInfo paraInfo = mapper.map(param, AccountInfo.class);
		    AccountInfo accountInfo = accountInfoMapper.selectByLogin(paraInfo);
		    if(accountInfo!=null){	
				if(accountInfo.getState().equals("0")){
					throw new LockedClientException();
				}
				    	
				AccountSimInfoDto rAcDto = new AccountSimInfoDto();
				rAcDto = mapper.map(accountInfo, AccountSimInfoDto.class);
			    //rAcDto.setToken((new Des3Util()).encode(accountInfo.getAccountid()+tokenSplitter+tokenSecret));
				result.success(rAcDto);
		    }else {
			      result.fail(MessageTool.Code_3001);
			      LOG.info(result.getMessage());
			      return result;
		    }
			//LOG.info(username+","+result.getMessage()+","+new Date());
		} catch (LockedClientException e) {
			throw new LockedClientException();
		}catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(),e);
		}
		return result;

	}
	
	
	
	@ApiOperation(value = "玩家注册", notes = "玩家注册", httpMethod = "POST")
	@RequestMapping(value = "/addAccountInfo", method = RequestMethod.POST)
	@ResponseBody
	public RestResult addAccountInfo(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody PlayAccountInfoVo param) throws Exception {
		RestResult result = new RestResult();
		try {
			AccountInfo accountInfo = mapper.map(param,AccountInfo.class);			
			//参数合规性校验，必要参数不能为空
			if (ToolsUtil.isEmptyTrim(accountInfo.getUsername())||ToolsUtil.isEmptyTrim(accountInfo.getPassword())){
			      result.fail("用户名，密码",MessageTool.Code_2002);
			      LOG.info(result.getMessage());
			      return result;
			}
			
			//最长14个英文或者数字组合
			if (ToolsUtil.validatName(accountInfo.getUsername())){
			      result.fail("用户名",MessageTool.Code_1006);
			      LOG.info(result.getMessage());
			      return result;
			}
			
			if ("".equals(accountInfo.getCode())){
			      result.fail("邀请码",MessageTool.Code_1009);
			      LOG.info(result.getMessage());
			      return result;
			}
				
			//玩家是否存在，用户名不能一致
		    //AccountInfo paraInfo = mapper.map(param, AccountInfo.class);
		    AccountInfo accountInfo1 = accountInfoMapper.selectByUsername(accountInfo.getUsername());
		    if (accountInfo1!=null){
			      result.fail(accountInfo.getUsername(),MessageTool.Code_2005);
			      return result;
		    }else{
		    	
		    	//根据邀请码判断上级
		    	
		    	AccountInfo accountInfo2 = accountInfoMapper.selectByCode(accountInfo.getCode(), "9", "2");
		    	System.out.println("34-----------"+accountInfo2);
		    	if (accountInfo2!=null){
		    		accountInfo.setSupuserid(accountInfo2.getAccountid());
			    	accountInfo.setPassword(DigestUtils.md5Hex(accountInfo.getPassword())); 
			    	//默认账户类型,试玩00;超级账户0；代理商1；子账户2；会员账户3
			    	accountInfo.setOfftype("3");
			    	//默认9，玩家账户
			    	accountInfo.setLevel("9");
			    	accountInfo.setState("1");//默认状态正常
			    	accountInfo.setInputdate(new Date());
			    	accountInfo.setUsermoney(BigDecimal.valueOf(0.0));
				    accountInfoService.addAccountInfo(accountInfo);
				    result.success();
		    	}else {
				     result.fail("邀请码",MessageTool.Code_3003);
				     return result;
		    	}

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
	public RestResult updateAccountInfo(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody UpdateAccountVo param) throws Exception {
		RestResult result = new RestResult();
		try {
			AccountInfo accountInfo = accountInfoMapper.selectByPrimaryKey(param.getAccountid());
			if(accountInfo==null){
			      result.fail(MessageTool.Code_3001);
			}else{
				param.setPassword(DigestUtils.md5Hex(param.getPassword()));
			    AccountInfo paraInfo = mapper.map(param, AccountInfo.class);
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
	/*
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
			    //paraInfo.getSupuserid(accountInfo.getSupuserid());
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
					AccountDetail accountDetail =  accountDetailMapper.selectByUserId(accountInfos.get(i).getAccountid(),"3");
			    	//获取上级的限额等信息
				    OffAccountInfo leOffAccountInfo = offAccountInfoMapper.selectByUsername(accountInfos.get(i).getUsername());
					if(leOffAccountInfo==null){ 	
					      result.fail("管理员",MessageTool.Code_3002);
					      LOG.info(result.getMessage());
					      return result;
					  }
					    
					AccountInfoDto rAcDto = new AccountInfoDto();
			        //rAcDto.setUserid(null==accountInfos.get(i).getUserid()||"".equals(accountInfos.get(i).getUserid())||0==accountInfos.get(i).getUserid() ?0:accountInfos.get(i).getUserid());
			        rAcDto.setUsername(null==accountInfos.get(i).getUsername()||"".equals(accountInfos.get(i).getUsername()) ?"":accountInfos.get(i).getUsername());
			        rAcDto.setAusername(null==accountInfos.get(i).getAusername()||"".equals(accountInfos.get(i).getAusername()) ?"":accountInfos.get(i).getAusername());
			        rAcDto.setPassword(null==accountInfos.get(i).getPassword()||"".equals(accountInfos.get(i).getPassword()) ?"":accountInfos.get(i).getPassword());
			        //rAcDto.setLimited(null==accountInfos.get(i).getLimited()||"".equals(accountInfos.get(i).getLimited())||0.0==accountInfos.get(i).getLimited() ?0.0:accountInfos.get(i).getLimited());
			        //rAcDto.setRatio(null==accountInfos.get(i).getRatio()||"".equals(accountInfos.get(i).getRatio())||0.0==accountInfos.get(i).getRatio() ?0.0:accountInfos.get(i).getRatio());
			        rAcDto.setLevel(null==accountInfos.get(i).getLevel()||"".equals(accountInfos.get(i).getLevel()) ?"":accountInfos.get(i).getLevel());
			        rAcDto.setState(null==accountInfos.get(i).getState()||"".equals(accountInfos.get(i).getState()) ?"":accountInfos.get(i).getState());
			        //rAcDto.setSupusername(null==accountInfos.get(i).getSupuserid()||"".equals(accountInfos.get(i).getSupuserid()) ?"":accountInfos.get(i).getSupuserid());
			        rAcDto.setOfftype("3");
			        //rAcDto.setAccountID(accountDetail.getAccountid());
			      //rAcDto.setAccountAmount(accountDetail.getMoney());
				    //rAcDto.setLelimited(leOffAccountInfo.getLimited());
				   /// rAcDto.setLepercentage(leOffAccountInfo.getPercentage());
				   // rAcDto.setLeratio(leOffAccountInfo.getRatio());
				   // rAcDto.setLeriskamount(leOffAccountInfo.getRiskamount());
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
	
	*/
	
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
	
	@ApiOperation(value = "获取公告", notes = "获取公告", httpMethod = "POST")
	@RequestMapping(value = "/lotteryMessage", method = RequestMethod.POST)
	@ResponseBody
	public RestResult getLotteryMessage(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody NoticeTypeVo param) throws Exception {
		RestResult result = new RestResult();
		try {
			String stype = param.getOfftype();
			if (stype.equals("")||!(stype.equals("00")||stype.equals("1")||stype.equals("2"))||stype.equals("3")){
			      result.fail("公告类型",MessageTool.Code_1005);
			      LOG.info(result.getMessage());
			      return result;
			}
			int noticeid = 1;
			if (stype.equals("0")||stype.equals("00"))
				noticeid = 1;
			else if (stype.equals("1")||stype.equals("2")||stype.equals("3"))
				noticeid = 2;

            NoticeInfo noticeInfo = noticeInfoMapper.selectByPrimaryKey(noticeid);
			if(noticeInfo==null){
			      result.fail(MessageTool.Code_4001);
			}else{
				result.success(noticeInfo);
			}
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(),e);
		}
		return result;
	}
	
	@ApiOperation(value = "退出", notes = "退出", httpMethod = "POST")
	@RequestMapping(value = "/userExit", method = RequestMethod.POST)
	@ResponseBody
	public RestResult userExit(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody AccountRecordVo param) throws Exception {
		RestResult result = new RestResult();
		try {
			AccountRecord aRecord = new AccountRecord();
			aRecord.setRecordid(param.getRecordid());
			aRecord.setAccountid(param.getAccountid());
			aRecord.setOuttime(new Date());
			accountRecordMapper.updateByPrimaryKeySelective(aRecord);
			result.success();
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(),e);
		}
		return result;
	}
	
	
}
