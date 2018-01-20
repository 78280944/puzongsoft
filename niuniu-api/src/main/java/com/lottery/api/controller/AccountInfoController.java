package com.lottery.api.controller;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.message.BasicNameValuePair;
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
import com.lottery.api.dto.CashOrderVo;
import com.lottery.api.dto.DemoInfoVo;
import com.lottery.api.dto.LoginParamVo;
import com.lottery.api.dto.NoticeHisTypeVo;
import com.lottery.api.dto.NoticeTypeVo;
import com.lottery.api.dto.PlayAccountInfoVo;
import com.lottery.api.dto.UpdateAccountVo;
import com.lottery.api.dto.UserCashVo;
import com.lottery.api.dto.UserOrderNoticeVo;
import com.lottery.api.dto.UserOrderVo;
import com.lottery.api.dto.UserRechargeResVo;
import com.lottery.api.dto.UserRechargeVo;
import com.lottery.api.filter.LockedClientException;
import com.lottery.api.util.Des3Util;
import com.lottery.api.util.ToolsUtil;
import com.lottery.orm.bo.AccountDetail;
import com.lottery.orm.bo.AccountInfo;
import com.lottery.orm.bo.AccountRecharge;
import com.lottery.orm.bo.AccountRecord;
import com.lottery.orm.bo.BankCash;
import com.lottery.orm.bo.NoticeInfo;
import com.lottery.orm.bo.OffAccountInfo;
import com.lottery.orm.bo.SysBene;
import com.lottery.orm.bo.SysFee;
import com.lottery.orm.dao.AccountAmountMapper;
import com.lottery.orm.dao.AccountDetailMapper;
import com.lottery.orm.dao.AccountInfoMapper;
import com.lottery.orm.dao.AccountRechargeMapper;
import com.lottery.orm.dao.AccountRecordMapper;
import com.lottery.orm.dao.BankCashMapper;
import com.lottery.orm.dao.LotteryGameOrderMapper;
import com.lottery.orm.dao.LotteryOrderMapper;
import com.lottery.orm.dao.NoticeInfoMapper;
import com.lottery.orm.dao.OffAccountInfoMapper;
import com.lottery.orm.dao.SysBeneMapper;
import com.lottery.orm.dao.SysFeeMapper;
import com.lottery.orm.dao.TradeInfoMapper;
import com.lottery.orm.dto.AccountInfoDto;
import com.lottery.orm.dto.AccountSimInfoDto;
import com.lottery.orm.dto.RemarkDto;
import com.lottery.orm.dto.UserRechargeDto;
import com.lottery.orm.dto.UserRechargeResDto;
import com.lottery.orm.result.AccountListResult;
import com.lottery.orm.result.AccountResult;
import com.lottery.orm.result.AccountSimResult;
import com.lottery.orm.result.BankCashResult;
import com.lottery.orm.result.NoticeResult;
import com.lottery.orm.result.RemarkResult;
import com.lottery.orm.result.RestResult;
import com.lottery.orm.result.UserRechargeResult;
import com.lottery.orm.service.AccountInfoService;
import com.lottery.orm.service.EasemobService;
import com.lottery.orm.util.CommonUtils;
import com.lottery.orm.util.EnumType;
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
	
	@Autowired
    private AccountRechargeMapper accountRechargeMapper;
	
	@Autowired
	private LotteryGameOrderMapper lotteryGameOrderMapper;
	
	@Autowired
	private AccountAmountMapper accountAmountMapper;
	
	@Autowired
	private TradeInfoMapper tradeInfoMapper;
	
	@Autowired
	private SysFeeMapper sysFeeMapper;
	
	@Autowired
	private SysBeneMapper sysBeneMapper;
	
	@Autowired
	private TransScanCodePayTest transScanCodePayTest;
	
	@Autowired
	private TransProxyPayTest transProxyPayTest;
	
	@Autowired
	private QueryTransStatusTest queryTransStatusTest;
	
	@Autowired
	private BankCashMapper bankCashMapper;
	
	@Autowired
	private EasemobService easemobService;
	
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

	@Value("${lottery.playoridle}")
    private String playoridle;
	
	@Value("${lottery.noplayoridle}")
    private String noplayoridle;
	
	
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
				if (accountInfo.getOfftype().equals("2")){
					accountInfo =  accountInfoMapper.selectByPrimaryKey(accountInfo.getSupuserid());
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
				if (accountInfo.getOfftype().equals("99"))
					rAcDto.setPassword("123456XYV");
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
				if (accountInfo.getOfftype().equals("99"))
					rAcDto.setPassword("123456XYV");
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
	
	
	@ApiOperation(value = "试玩获取", notes = "试玩获取", httpMethod = "POST")
	@RequestMapping(value = "/getAccountDemoInfo", method = RequestMethod.POST)
	@ResponseBody
	public AccountResult getAccountDemoInfo(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody DemoInfoVo param) throws Exception {
		AccountResult result = new AccountResult();
		try {
			//99：试玩用户
			AccountInfo info = new AccountInfo();
			info.setUsername("Test"+CommonUtils.produceString(4)+CommonUtils.RamdomNum());
			info.setAusername(info.getUsername());
			info.setPassword(DigestUtils.md5Hex("123456"));
			info.setOfftype("99");
			info.setLevel("99");
			info.setInputdate(new Date());
			info.setUsermoney(BigDecimal.valueOf(20000));
			info.setSupuserid(1000);
			info.setState("1");
			accountInfoService.addAccountInfo(info);
			
			AccountInfo paraInfo = mapper.map(info, AccountInfo.class);
			AccountInfo accountInfo = accountInfoMapper.selectByLogin(paraInfo);
			
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
		    rAcDto.setPassword("123456XYV");
		    result.success(rAcDto);
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
		    	
		    	AccountInfo accountInfo2 = accountInfoMapper.selectByCode(accountInfo.getCode(), "9", "1");
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
			    	//注册环信帐号
	                Boolean easeRegisterResult = easemobService.registerEaseMobUser(accountInfo.getUsername(), accountInfo.getUsername());
	                if (easeRegisterResult) {
	                    //环信帐号注册成功
	                    accountInfoService.addAccountInfo(accountInfo);
	                    result.success();
	                } else {
	                    result.fail("环信注册",MessageTool.Code_3002);
	                }
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
		remark.setPlayoridle(playoridle);
		remark.setNoplayoridle(noplayoridle);
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
			if (stype.equals("")||!(stype.equals("99")||stype.equals("0")||stype.equals("1")||stype.equals("2")||stype.equals("3"))){
			      result.fail("公告类型",MessageTool.Code_1005);
			      LOG.info(result.getMessage());
			      return result;
			}
			String offtype = "";
			if (stype.equals("3")||stype.equals("99"))
				offtype = "1";
			else if (stype.equals("0")||stype.equals("1")||stype.equals("2"))
				offtype = "2";

            NoticeInfo noticeInfo = noticeInfoMapper.selectByNotice(offtype);
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
	
	@ApiOperation(value = "获取历史公告", notes = "获取历史公告", httpMethod = "POST")
	@RequestMapping(value = "/lotteryHisMessage", method = RequestMethod.POST)
	@ResponseBody
	public NoticeResult getLotteryHisMessage(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody NoticeHisTypeVo param) throws Exception {
		NoticeResult result = new NoticeResult();
		try {
			String stype = param.getOfftype();
			if (stype.equals("")||!(stype.equals("99")||stype.equals("0")||stype.equals("1")||stype.equals("2")||stype.equals("3"))){
			      result.fail("公告类型",MessageTool.Code_1005);
			      LOG.info(result.getMessage());
			      return result;
			}
			String offtype = "";
			if (stype.equals("3")||stype.equals("99"))
				offtype = "1";
			else if (stype.equals("0")||stype.equals("1")||stype.equals("2"))
				offtype = "2";

            List<NoticeInfo> list = noticeInfoMapper.selectByHisNotice(offtype,param.getBeginRow(), param.getPageSize());
			if(list==null){
			      result.fail(MessageTool.Code_4001);
			}else{
				result.success(list);
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
			AccountInfo aInfo = new AccountInfo();
			aInfo.setAccountid(param.getAccountid());
			aInfo.setPassword(DigestUtils.md5Hex("123456"));
			System.out.println("123------------"+aInfo.getAccountid());
			AccountInfo aInfo1 = accountInfoMapper.selectByPrimaryKey(param.getAccountid());
			if (aInfo1.getLevel().equals("99")){
				tradeInfoMapper.deleteByPlayer(aInfo1.getAccountid());
				accountInfoMapper.deleteByPrimaryKey(aInfo1.getAccountid());
				lotteryGameOrderMapper.deleteByPlayerOrder(aInfo1.getAccountid());
				accountAmountMapper.deleteByPlayer(aInfo1.getAccountid());
				
			}
			
			result.success();
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(),e);
		}
		return result;
	}
	
	
	@ApiOperation(value = "用户充值", notes = "用户充值", httpMethod = "POST")
	@RequestMapping(value = "/userRecharge", method = RequestMethod.POST)
	@ResponseBody
	public  UserRechargeResult userRecharge(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody UserRechargeVo param) throws Exception {
		UserRechargeResult result = new UserRechargeResult();
		try {
			AccountRecharge aRecharge = new AccountRecharge();
			aRecharge = mapper.map(param, AccountRecharge.class);	
	    	AccountInfo aInfo = accountInfoMapper.selectByPrimaryKey(aRecharge.getAccountid());
	    	if (null == aInfo){
			      result.fail("该用户不存在！");
			      LOG.info(result.getMessage());
			      return result;
	    	}
	    	if (!(param.getUsername().equals(aInfo.getUsername()))){
	    	      result.fail("该用户名与ID不匹配！");
			      LOG.info(result.getMessage());
			      return result;
	    	}
	    	aRecharge = transScanCodePayTest.getPayTrans(aRecharge);
	    	if (null == aRecharge){
	    	      result.fail("该用户充值出现异常！");
			      LOG.info(result.getMessage());
			      return result;
	    	}
	    	aRecharge.setTransamt((int)(param.getTransAmt()/100));
			aRecharge.setRelativetype(EnumType.RalativeType.In.ID);
			aRecharge.setOpuserid(String.valueOf(param.getAccountId()));
			aRecharge.setOpusertime(new Date());
			aRecharge.setOrderstate("03");//处理中
	    	accountRechargeMapper.insert(aRecharge);
	    	UserRechargeResDto urDto = mapper.map(aRecharge, UserRechargeResDto.class);	 
	    	urDto.setUsername(param.getUsername());
	    	urDto.setTransAmt(param.getTransAmt());
			result.success(urDto);
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(),e);
		}
		return result;
	}
	
	/*
	@ApiOperation(value = "用户充值结果", notes = "用户充值结果", httpMethod = "POST")
	@RequestMapping(value = "/userRechargeResult", method = RequestMethod.POST)
	@ResponseBody
	public RestResult userRechargeResult(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody UserRechargeResVo param) throws Exception {
		RestResult result = new RestResult();
		try {
			AccountRecharge aRecharge = new AccountRecharge();
			aRecharge = mapper.map(param, AccountRecharge.class);	
			AccountInfo aInfo = accountInfoMapper.selectByPrimaryKey(aRecharge.getAccountid());
	    	if (null == aInfo){
			      result.fail("该用户不存在！");
			      LOG.info(result.getMessage());
			      return result;
	    	}
	    	if (!(param.getUsername().equals(aInfo.getUsername()))){
	    	      result.fail("该用户名与ID不匹配！");
			      LOG.info(result.getMessage());
			      return result;
	    	}
	    	if (null == aRecharge.getPayno()||"".equals(aRecharge.getPayno())||null==aRecharge.getRespcode()||"".equals(aRecharge.getRespcode())){
	    	      result.fail("平台支付订单信息不能为空!");
			      LOG.info(result.getMessage());
			      return result;
	    	}
	    	AccountRecharge ar = accountRechargeMapper.selectByOrderNo(aRecharge.getOrderno(), "In", aRecharge.getAccountid());
	    	if (null == ar){
	    	      result.fail("该订单信息有误！");
			      LOG.info(result.getMessage());
			      return result;
	    	}else{
	    		if (!(aRecharge.getRequestno().equals(ar.getRequestno()))||!(aRecharge.getTransamt().equals(ar.getTransamt()))){
		    	      result.fail("平台支付订单信息或者金额不匹配有误！");
				      LOG.info(result.getMessage());
				      return result;
	    		}
	    	}
	    	if (param.getOrderState().equals("01")){
	    		SysBene sb = sysBeneMapper.selectByAmount(BigDecimal.valueOf(param.getTransAmt()/100));
				Double bene = 0.0;
				Double amount = 0.0;
				if (null == sb||sb.getBenefit() == null||sb.getBenefit() == BigDecimal.valueOf(0.0)){
					bene = 0.0;
				}else{
					bene = sb.getBenefit().doubleValue();
				}
				SysFee sf = sysFeeMapper.selectByPrimaryKey(1001);
				ar.setDonatamt(bene);//赠送金额
				ar.setFee(ar.getTransamt()/100*sf.getRefee().doubleValue());//充值费用
				ar.setPayamt(ar.getTransamt()/100-ar.getFee());//实际金额
				ar.setInputtime(new Date());
		    	
				amount = ar.getPayamt()+ar.getDonatamt();
				ar.setAccountamount(aInfo.getUsermoney().add(BigDecimal.valueOf(amount)));
		    	aInfo.setUsermoney(aInfo.getUsermoney().add(BigDecimal.valueOf(amount)));
		    	accountInfoMapper.updateByPrimaryKey(aInfo);
		    	if (ar.getFee()>0){
		    		aInfo = accountInfoMapper.selectByPrimaryKey(1000);
		    		aInfo.setUsermoney(aInfo.getUsermoney().add(BigDecimal.valueOf(ar.getFee())));
			    	accountInfoMapper.updateByPrimaryKey(aInfo);
		    	}

	    	}else if (param.getOrderState().equals("02")){

	    	}else{
	    	      result.fail("订单状态无效！");
			      LOG.info(result.getMessage());
			      return result;
	    	}
    		ar.setPayno(aRecharge.getPayno());
	    	ar.setOrderstate(aRecharge.getOrderstate());
	    	ar.setOrderip(aRecharge.getOrderip());
	    	ar.setOrderip(aRecharge.getOrderip());
	    	ar.setUpuserid(String.valueOf(param.getAccountId()));
	    	ar.setUpusertime(new Date());
	    	ar.setRespcode(param.getRespCode());
	    	ar.setRespdesc(param.getRespDesc());
			accountRechargeMapper.updateByPrimaryKey(ar);
			result.success();
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(),e);
		}
		return result;
	}
	*/
	
	@ApiOperation(value = "用户充值结果", notes = "用户充值结果", httpMethod = "POST")
	@RequestMapping(value = "/userRechargeResult", method = RequestMethod.POST)
	@ResponseBody
	public synchronized RestResult userRechargeResult(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody UserRechargeResVo param) throws Exception {
		RestResult result = new RestResult();
		try {
			String message = accountInfoService.checkResult(param.getOrderNo(), param.getPayNo(), param.getTransAmt(), param.getOrderDate(), param.getRespCode(), param.getRespDesc());
			result.success(message);
			return result;
	    
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(),e);
		}
		return result;
	}
	
	@ApiOperation(value = "用户取现申请", notes = "用户取现申请", httpMethod = "POST")
	@RequestMapping(value = "/userCashApply", method = RequestMethod.POST)
	@ResponseBody
	public synchronized RestResult userCashApply(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody UserCashVo param) throws Exception {
		RestResult result = new RestResult();
		AccountRecharge aRecharge = new AccountRecharge();
		aRecharge = mapper.map(param, AccountRecharge.class);	
		aRecharge.setRelativetype(EnumType.RalativeType.Out.ID);
		SysFee sf = sysFeeMapper.selectByPrimaryKey(1001);
		aRecharge.setFee(aRecharge.getTransamt()/100*sf.getCafee().doubleValue());
		aRecharge.setPayamt(aRecharge.getTransamt()/100-aRecharge.getFee());
		aRecharge.setOrderstate("03");//未处理,审核中
		aRecharge.setUpstate("03");//未处理，审核中
		aRecharge.setInputtime(new Date());
		AccountInfo aInfo = accountInfoMapper.selectByPrimaryKey(aRecharge.getAccountid());
		if (null == aInfo){
		      result.fail("该用户不存在！");
		      LOG.info(result.getMessage());
		      return result;
		}
		
		if(aInfo.getUsermoney().subtract(BigDecimal.valueOf((double)(aRecharge.getTransamt()/100))).doubleValue()<0){
			result.fail("取现金额不能大于账户金额");
			return result;
		}
		String checkInfo = accountInfoService.checkCashMoneyInfo(aInfo, Double.valueOf(aRecharge.getTransamt()/100));
		if ((!"true".equals(checkInfo))){
			result.fail(checkInfo);
			return result;
		}
		LOG.info(aInfo.getUsername()+",账户金额:"+aInfo.getUsermoney()+",取现金额:"+(aRecharge.getTransamt()/100)+",账户取现后余额："+aInfo.getUsermoney().subtract(BigDecimal.valueOf((double)(aRecharge.getTransamt()/100))));
		aInfo.setUsermoney(aInfo.getUsermoney().subtract(BigDecimal.valueOf((double)(aRecharge.getTransamt()/100))));
		aRecharge.setAccountamount(aInfo.getUsermoney().subtract(BigDecimal.valueOf((double)(aRecharge.getTransamt()/100))));
		accountInfoMapper.updateByPrimaryKey(aInfo);
		if((aRecharge.getTransamt()/100) % (sf.getRatio().doubleValue()) != 0){
			result.fail("取现必须为"+sf.getRatio().doubleValue()+"的倍数");
			return result;
	    }
		List<AccountRecharge> list =  accountRechargeMapper.selectByTime(aRecharge.getOrderdate(),EnumType.RalativeType.Out.ID,param.getAccountId());
		if (null != list&&list.size()>sf.getTime()){
			result.fail("当天取现次数不允许超过"+sf.getTime()+"次");
			return result;
		}
		if (aRecharge.getFee()>0){
    		aInfo = accountInfoMapper.selectByPrimaryKey(1000);
    		aInfo.setUsermoney(aInfo.getUsermoney().add(BigDecimal.valueOf(aRecharge.getFee())));
	    	accountInfoMapper.updateByPrimaryKey(aInfo);
    	}
		aRecharge.setTransamt(aRecharge.getTransamt()/100);
		aRecharge.setRemark("取现金额:"+aRecharge.getTransamt()/100+",取现时间:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		accountRechargeMapper.insert(aRecharge);
		result.success();
		LOG.info(result.getMessage());
		return result;
      }
	
	@ApiOperation(value = "后台审核", notes = "后台审核", httpMethod = "POST")
	@RequestMapping(value = "/userCashAudit", method = RequestMethod.POST)
	@ResponseBody
	public synchronized RestResult userCashAudit(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody CashOrderVo param) throws Exception {
		RestResult result = new RestResult();
		AccountRecharge aRecharge = new AccountRecharge();
		aRecharge = mapper.map(param, AccountRecharge.class);	
		SysFee sf = sysFeeMapper.selectByPrimaryKey(1001);
		aRecharge = accountRechargeMapper.selectByPrimaryKey(aRecharge.getArid());
		aRecharge.setTransamt(aRecharge.getTransamt()*100);
		AccountInfo aInfo = accountInfoMapper.selectByPrimaryKey(aRecharge.getAccountid());
		if (null == aInfo){
		      result.fail("该用户不存在！");
		      LOG.info(result.getMessage());
		      return result;
		}
		
		String checkInfo = accountInfoService.checkCashMoneyInfo(aInfo, Double.valueOf(aRecharge.getTransamt()/100));
		if ((!"true".equals(checkInfo))){
			result.fail(checkInfo);
			return result;
		}
		
		if((aRecharge.getTransamt()/100) % (sf.getRatio().doubleValue()) != 0){
			result.fail("取现必须为"+sf.getRatio().doubleValue()+"的倍数");
			return result;
	    }
		List<AccountRecharge> list =  accountRechargeMapper.selectByTime(aRecharge.getOrderdate(),EnumType.RalativeType.Out.ID,aRecharge.getAccountid());
		if (null != list&&list.size()>sf.getTime()){
			result.fail("当天取现次数不允许超过"+sf.getTime()+"次");
			return result;
		}
		
		result.success("success");
		LOG.info(result.getMessage());
		return result;
      }
	
	@ApiOperation(value = "后台打款", notes = "后台打款", httpMethod = "POST")
	@RequestMapping(value = "/userCashdo", method = RequestMethod.POST)
	@ResponseBody
	public synchronized RestResult userCashdo(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody CashOrderVo param) throws Exception {
		RestResult result = new RestResult();
		AccountRecharge aRecharge = new AccountRecharge();
		aRecharge = mapper.map(param, AccountRecharge.class);	
		aRecharge = accountRechargeMapper.selectByPrimaryKey(aRecharge.getArid());
		if (null == aRecharge){
		      result.fail("取现订单异常！");
		      LOG.info(result.getMessage());
		      return result;
		}
		if (null!=aRecharge.getRespcode()&&aRecharge.getRespcode().equals("P000")){
		      result.fail("该订单已取现！");
		      LOG.info(result.getMessage());
		      return result;
		}
		aRecharge.setOrderno(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		aRecharge.setOrderdate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
		aRecharge.setOpusertime(new Date());
		aRecharge.setOpuserid(param.getOpuserid());
		SysFee sf = sysFeeMapper.selectByPrimaryKey(1001);
		aRecharge.setTransamt(aRecharge.getTransamt()*100);
		AccountInfo aInfo = accountInfoMapper.selectByPrimaryKey(aRecharge.getAccountid());
		if (null == aInfo){
		      result.fail("该用户不存在！");
		      LOG.info(result.getMessage());
		      return result;
		}
		
		String checkInfo = accountInfoService.checkCashMoneyInfo(aInfo, Double.valueOf(aRecharge.getTransamt()/100));
		if ((!"true".equals(checkInfo))){
			result.fail(checkInfo);
			return result;
		}
		
		if((aRecharge.getTransamt()/100) % (sf.getRatio().doubleValue()) != 0){
			result.fail("取现必须为"+sf.getRatio().doubleValue()+"的倍数");
			return result;
	    }
		List<AccountRecharge> list =  accountRechargeMapper.selectByTime(aRecharge.getOrderdate(),EnumType.RalativeType.Out.ID,aRecharge.getAccountid());
		if (null != list&&list.size()>sf.getTime()){
			result.fail("当天取现次数不允许超过"+sf.getTime()+"次");
			return result;
		}
		String results = transProxyPayTest.getPayTrans(aRecharge);
    	if (null == results||results.equals("")||results.equals("false")){
    	      result.fail("该用户充值出现异常！");
		      LOG.info(result.getMessage());
		      return result;
    	}
		result.success("success");
		LOG.info(result.getMessage());
		return result;
      }
	
	@ApiOperation(value = "后台取消", notes = "后台取消", httpMethod = "POST")
	@RequestMapping(value = "/userCashCancel", method = RequestMethod.POST)
	@ResponseBody
	public synchronized RestResult userCashCancel(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody CashOrderVo param) throws Exception {
		RestResult result = new RestResult();
		AccountRecharge aRecharge = new AccountRecharge();
		aRecharge = mapper.map(param, AccountRecharge.class);	
		aRecharge = accountRechargeMapper.selectByPrimaryKey(aRecharge.getArid());
		if (null == aRecharge){
		      result.fail("取现订单异常！");
		      LOG.info(result.getMessage());
		      return result;
		}
		AccountInfo aInfo = accountInfoMapper.selectByPrimaryKey(aRecharge.getAccountid());
		if (null == aInfo){
		      result.fail("该用户不存在！");
		      LOG.info(result.getMessage());
		      return result;
		}

		if (null!=aRecharge.getRespcode()&&aRecharge.getRespcode().equals("P000")){
		      result.fail("该订单已取现或已人工处理，无法取消！");
		      LOG.info(result.getMessage());
		      return result;
		}
		aInfo.setUsermoney(aInfo.getUsermoney().add(BigDecimal.valueOf(aRecharge.getTransamt().doubleValue())));
		aRecharge.setAccountamount(aInfo.getUsermoney().add(BigDecimal.valueOf(aRecharge.getTransamt().doubleValue())));
		accountInfoMapper.updateByPrimaryKey(aInfo);
		aInfo = accountInfoMapper.selectByPrimaryKey(1000);
		aInfo.setUsermoney(aInfo.getUsermoney().subtract(BigDecimal.valueOf(aRecharge.getTransamt().doubleValue())));
		accountInfoMapper.updateByPrimaryKey(aInfo);
		aRecharge.setOrderstate("04");
		aRecharge.setOpuserid(param.getOpuserid());
		aRecharge.setOpusertime(new Date());
		accountRechargeMapper.updateByPrimaryKey(aRecharge);
		result.success("success");
		LOG.info(result.getMessage());
		return result;
      }
	
	@ApiOperation(value = "用户取现支持的银行列表", notes = "用户取现支持的银行列表", httpMethod = "POST")
	@RequestMapping(value = "/bankCashList", method = RequestMethod.POST)
	@ResponseBody
	public BankCashResult bankCashList() throws Exception {
		BankCashResult result = new BankCashResult();
		List<BankCash> list = bankCashMapper.selectBankCash();
		result.success(list);
		LOG.info(result.getMessage());
		return result;
      }
	
	
	@ApiOperation(value = "后台取现结果查询", notes = "后台取现结果查询", httpMethod = "POST")
	@RequestMapping(value = "/userOutResult", method = RequestMethod.POST)
	@ResponseBody
	public synchronized RestResult userOutResult() throws Exception {
		RestResult result = new RestResult();
		List<AccountRecharge> list = accountRechargeMapper.selectByOutResult(EnumType.RalativeType.Out.ID);
		if (null != list){
			  for (int i = 0;i<list.size();i++){
				  AccountRecharge aRecharge = new AccountRecharge();
				  aRecharge = list.get(i);
				  String results = queryTransStatusTest.getPayResults(aRecharge);
				  System.out.println(results +",accountid = "+aRecharge.getAccountid()+",transAmt = "+aRecharge.getTransamt());
				  LOG.info(results +",accountid = "+aRecharge.getAccountid()+",transAmt = "+aRecharge.getTransamt());
			  }
		}
		result.success("success");
		LOG.info(result.getMessage());
		return result;
      }
/*
	@ApiOperation(value = "用户取现结果通知", notes = "用户取现结果通知", httpMethod = "POST")
	@RequestMapping(value = "/userCashResultNotice", method = RequestMethod.POST)
	@ResponseBody
	public RestResult userCashResultNotice(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody UserOrderNoticeVo param) throws Exception {
		RestResult result = new RestResult();
		AccountRecharge aRecharge = new AccountRecharge();
		System.out.println("ces-12---"+param.getOrderNo());
		aRecharge = accountRechargeMapper.selectByOrderNo(param.getOrderNo(), EnumType.RalativeType.Out.ID,param.getAccountId());
		aRecharge.setOrderstate(param.getOrderState());
		System.out.println("ces------------"+aRecharge.getAccountid()+".."+aRecharge.getArid()+".."+aRecharge.getOrderdate()+".."+aRecharge.getRelativetype());
		accountRechargeMapper.updateByPrimaryKey(aRecharge);
		result.success();
		LOG.info(result.getMessage());
		return result;
      }
	*/
}
