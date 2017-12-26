package com.lottery.api.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lottery.api.dto.AccountInfoVo;
import com.lottery.api.dto.LoginParamVo;
import com.lottery.api.dto.NoticeInfoVo;
import com.lottery.api.dto.NoticeTypeVo;
import com.lottery.api.dto.OffAccountInfoVo;
import com.lottery.api.dto.UpdateAccountPerVo;
import com.lottery.api.dto.UpdateAccountRatioVo;
import com.lottery.api.dto.UpdateAccountRiskVo;
import com.lottery.api.dto.UpdateAccountStateVo;
import com.lottery.api.dto.UpdateOffAccountVo;
import com.lottery.api.dto.UpdatePlayAmountVo;
import com.lottery.api.dto.UpdatePlayPassVo;
import com.lottery.api.dto.UpdatePlayRatioVo;
import com.lottery.api.dto.UpdatePlayStateVo;
import com.lottery.api.util.ToolsUtil;
import com.lottery.orm.bo.AccountDetail;
import com.lottery.orm.bo.AccountInfo;
import com.lottery.orm.bo.NoticeInfo;
import com.lottery.orm.bo.OffAccountInfo;
import com.lottery.orm.dao.AccountDetailMapper;
import com.lottery.orm.dao.AccountInfoMapper;
import com.lottery.orm.dao.NoticeInfoMapper;
import com.lottery.orm.dao.OffAccountInfoMapper;
import com.lottery.orm.dto.OffAccountDto;
import com.lottery.orm.dto.OffsAccountDto;
import com.lottery.orm.result.OffAccountListResult;
import com.lottery.orm.result.OffAccountResult;
import com.lottery.orm.result.RemarkResult;
import com.lottery.orm.result.RestResult;
import com.lottery.orm.service.AccountInfoService;
import com.lottery.orm.service.OffAccountInfoService;
import com.lottery.orm.util.EnumType;
import com.lottery.orm.util.MessageTool;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@RequestMapping(value = "/offAccount", produces = {"application/json;charset=UTF-8"})
@Api(value = "/offAccount", description = "代理帐号信息接口")
@Controller
public class OffAccountInfoController {
	public static final Logger LOG = Logger.getLogger(OffAccountInfoController.class);
	
	@Autowired
	private Mapper mapper;
	
	@Autowired
    private AccountInfoService accountInfoService;
	
	@Autowired
    private OffAccountInfoService offAccountInfoService;
	
	@Autowired
	private OffAccountInfoMapper offAccountInfoMapper;
	
	@Autowired
    private AccountInfoMapper accountInfoMapper;
	
	@Autowired
    private AccountDetailMapper accountDetailMapper;
	
	@Autowired
    private NoticeInfoMapper noticeInfoMapper;
	
	@ApiOperation(value = "新增下线代理", notes = "新增下线代理", httpMethod = "POST")
	@RequestMapping(value = "/addOffAccountInfo", method = RequestMethod.POST)
	@ResponseBody
	public RestResult addOffAccountInfo(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody OffAccountInfoVo param) throws Exception {
		RestResult result = new RestResult();
		try {
			
			String username = param.getUsername();
			String ausername = param.getAusername();
			String password = param.getPassword();
			//String supusername = param.getSupusername();
			Double percentage = param.getPercentage();
			
				
			//参数合规性校验，必要参数不能为空
			if (ToolsUtil.isEmptyTrim(username)||ToolsUtil.isEmptyTrim(password)){
			      result.fail("用户名，密码",MessageTool.Code_2002);
			      LOG.info(result.getMessage());
			      return result;
			}
			
			
			//数字型
			if (null != percentage){
				if (ToolsUtil.isNumeric(String.valueOf(percentage))){
				      result.fail("代理占成",MessageTool.Code_1004);
				      LOG.info(result.getMessage());
				      return result;		
				}
			}
			param.setPassword(DigestUtils.md5Hex(password));
			AccountInfo paraInfo = mapper.map(param, AccountInfo.class);		
			AccountInfo OffAccountInfo = accountInfoMapper.selectByUsername(paraInfo.getUsername());
		    if(OffAccountInfo!=null){ 	
		    	result.fail(username,MessageTool.Code_2005);
			    LOG.info(result.getMessage());
			    return result;
		    }else{

		    	//获取管理员level
		    	AccountInfo OffAccountInfo1 = accountInfoMapper.selectByPrimaryKey(param.getAccountid());
		    	if (OffAccountInfo1 == null){
		    		result.fail(username,MessageTool.Code_2006);
				    LOG.info(result.getMessage());
				    return result;
		    	}
		    	String level = OffAccountInfo1.getLevel();
				//判断是否有权限新增下线
				if (level.equals("3")){
			        result.fail(MessageTool.Code_2004);
			        LOG.info(result.getMessage());
			        return result;	
				}
		    	
				//代理占比逻辑
				if (percentage>OffAccountInfo1.getPercentage()){
				      result.fail("代理占成",MessageTool.Code_1008);
				      LOG.info(result.getMessage());
				      return result;	
				}
				
				//邀请码是否存在
				List<AccountInfo> list = accountInfoMapper.selectByCodeNo(param.getCode());
				if (list.size()>0){
			        result.fail("邀请码已存在，请重新输入邀请码");
			        LOG.info(result.getMessage());
			        return result;	
				}
					
			    paraInfo.setState("1");//默认状态正常
			    paraInfo.setLevel(ToolsUtil.decideLevel(level));
			    paraInfo.setOfftype("1");
			    paraInfo.setInputdate(new Date());
			    paraInfo.setUsermoney(BigDecimal.valueOf(0.0));
			    paraInfo.setSupuserid(param.getAccountid());
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
	

	@ApiOperation(value = "获取该代理下的代理列表", notes = "获取该代理下的代理列表", httpMethod = "POST")
	@RequestMapping(value = "/getAllOffAccountInfo", method = RequestMethod.POST)
	@ResponseBody
	public OffAccountListResult getAllOffAccountInfo(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody AccountInfoVo param) throws Exception {
	    OffAccountListResult result = new OffAccountListResult();
		try {
			
			AccountInfo offacount = accountInfoMapper.selectByPrimaryKey(param.getAccountid());
			//OffAccountInfo offacount = offAccountInfoMapper.selectByUseridAndType(param.getUserid(), EnumType.OffType.Agency.ID);
			if(offacount==null){
				  result.fail(MessageTool.Code_3001);
			      LOG.info(result.getMessage());
			      return result;
			}
			List<OffsAccountDto> list = offAccountInfoMapper.selectOffSupuserId(offacount.getAccountid(), EnumType.OffType.Agency.ID,param.getBeginRow(), param.getPageSize());
		    result.success(list);
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(),e);
		}
		return result;
	}
	
	@ApiOperation(value = "代理用户修改下线密码", notes = "代理用户修改下线密码", httpMethod = "POST")
	@RequestMapping(value = "/updateOffAccountPass", method = RequestMethod.POST)
	@ResponseBody
	public RestResult updateOffAccountPass(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody UpdatePlayPassVo param) throws Exception {
		RestResult result = new RestResult();
		try {			
			AccountInfo offAccountInfo = accountInfoMapper.selectByPrimaryKey(param.getAccountid());
			if(offAccountInfo==null){
			      result.fail(MessageTool.Code_3001);
			      LOG.info(result.getMessage());
			      return result;
			}else{
				offAccountInfo.setPassword(DigestUtils.md5Hex(param.getPassword()));
				offAccountInfo.setIp(param.getIp());
				accountInfoMapper.updateByPrimaryKey(offAccountInfo);
			    LOG.info("修改密码记录详情为："+" 管理员："+param.getSupuserid()+" IP："+param.getIp()+" 修改下家ID"+param.getAccountid()+" 密码修改为"+offAccountInfo.getPassword());
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
	@ApiOperation(value = "代理用户修改玩家密码", notes = "代理用户修改玩家密码", httpMethod = "POST")
	@RequestMapping(value = "/updatePlayPass", method = RequestMethod.POST)
	@ResponseBody
	public RestResult updateAccountInfo(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody UpdatePlayPassVo param) throws Exception {
		RestResult result = new RestResult();
		try {
			int userid = param.getUserid();
			String password = param.getPassword();
            String supusername = param.getSupusername();
            int offtype = param.getOfftype();
			String ip = param.getIp();
			
			if (0==userid){
			      result.fail("用户ID",MessageTool.Code_2002);
			      LOG.info(result.getMessage());
			      return result;
			}
			
			AccountInfo accountInfo = accountInfoMapper.selectByPrimaryKey(param.getUserid());
			if(accountInfo==null){
			      result.fail(MessageTool.Code_3001);
			}else{
				accountInfo.setPassword(DigestUtils.md5Hex(password));
				accountInfo.setIp(ip);
			    accountInfoService.updateAccountInfo(accountInfo);
			    LOG.info("修改密码记录详情为："+" 管理员："+supusername+" 账户类型："+offtype+" IP："+ip+" 修改玩家ID"+userid+" 密码修改为"+accountInfo.getPassword());
			    result.success();
			}
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(),e);
		}
		return result;
	}
	
	@ApiOperation(value = "代理用户修改玩家洗码比", notes = "代理用户修改玩家洗码比", httpMethod = "POST")
	@RequestMapping(value = "/updatePlayRatio", method = RequestMethod.POST)
	@ResponseBody
	public RestResult updatePlayRatio(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody UpdatePlayRatioVo param) throws Exception {
		RestResult result = new RestResult();
		try {
			int userid = param.getUserid();
			double ratio = param.getRatio();
            String supusername = param.getSupusername();
            int offtype = param.getOfftype();
			String ip = param.getIp();
			
			if (0==userid){
			      result.fail("用户ID",MessageTool.Code_2002);
			      LOG.info(result.getMessage());
			      return result;
			}
			
			AccountInfo accountInfo = accountInfoMapper.selectByPrimaryKey(param.getUserid());
			if(accountInfo==null){
			      result.fail(MessageTool.Code_3001);
			}else{
				//洗码比逻辑 
				OffAccountInfo supAccount = offAccountInfoMapper.selectByUsername(accountInfo.getUsername());
		    	if (supAccount==null){
		    		result.fail(accountInfo.getUsername(),MessageTool.Code_2005);
		    		return result;
		    	}
		    	if (ratio>supAccount.getRatio()){
				      result.fail("洗码比",MessageTool.Code_1008);
				      LOG.info(result.getMessage());
				      return result;	
				}
				accountInfo.setRatio(ratio);
				accountInfo.setIp(ip);
			    accountInfoService.updateAccountInfo(accountInfo);
			    LOG.info("修改玩家洗码比记录详情为："+" 管理员："+supusername+" 账户类型："+offtype+" IP："+ip+" 修改玩家ID"+userid+" 玩家洗码比修改为"+accountInfo.getRatio());
			    result.success();
			}
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(),e);
		}
		return result;
	}
	
	
	@ApiOperation(value = "代理用户修改玩家状态", notes = "代理用户修改玩家状态", httpMethod = "POST")
	@RequestMapping(value = "/updatePlayState", method = RequestMethod.POST)
	@ResponseBody
	public RestResult updatePlayState(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody UpdatePlayStateVo param) throws Exception {
		RestResult result = new RestResult();
		try {
			int userid = param.getUserid();
			String state = param.getState();
            String supusername = param.getSupusername();
            int offtype = param.getOfftype();
			String ip = param.getIp();
			
			if (0==userid){
			      result.fail("用户ID",MessageTool.Code_2002);
			      LOG.info(result.getMessage());
			      return result;
			}
			if (!(state.equals("0")||state.equals("1"))){
			      result.fail("状态",MessageTool.Code_1005);
			      LOG.info(result.getMessage());
			      return result;
			}
			AccountInfo accountInfo = accountInfoMapper.selectByPrimaryKey(param.getUserid());
			if(accountInfo==null){
			      result.fail(MessageTool.Code_3001);
			}else{
				accountInfo.setState(state);
				accountInfo.setIp(ip);
				//修改用户状态
			    accountInfoService.updateAccountInfo(accountInfo);
			    
			    //修改账户状态
			    AccountDetail accountDetail = accountDetailMapper.selectByUserId(accountInfo.getAccountid(), "3");
			    accountDetail.setState(state);
			    accountDetailMapper.updateByPrimaryKey(accountDetail);
			   
			    LOG.info("修改玩家状态记录详情为："+" 管理员："+supusername+" 账户类型："+offtype+" IP："+ip+" 修改玩家ID"+userid+" 状态修改为"+accountInfo.getState());
			    result.success();
			}
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(),e);
		}
		return result;
	}
	
	
	@ApiOperation(value = "代理用户修改下线密码", notes = "代理用户修改下线密码", httpMethod = "POST")
	@RequestMapping(value = "/updateAccountPass", method = RequestMethod.POST)
	@ResponseBody
	public RestResult updateAccountPass(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody UpdatePlayPassVo param) throws Exception {
		RestResult result = new RestResult();
		try {
			int userid = param.getUserid();
			String password = param.getPassword();
            String supusername = param.getSupusername();
            int offtype = param.getOfftype();
			String ip = param.getIp();
			
			if (0==userid){
			      result.fail("用户ID",MessageTool.Code_2002);
			      LOG.info(result.getMessage());
			      return result;
			}
			
			OffAccountInfo offAccountInfo = offAccountInfoMapper.selectByPrimaryKey(param.getUserid());
			if(offAccountInfo==null){
			      result.fail(MessageTool.Code_3001);
			}else{
				offAccountInfo.setPassword(DigestUtils.md5Hex(password));
				offAccountInfo.setIp(ip);
				offAccountInfoMapper.updateByPrimaryKey(offAccountInfo);
			    LOG.info("修改密码记录详情为："+" 管理员："+supusername+" 账户类型："+offtype+" IP："+ip+" 修改下家ID"+userid+" 密码修改为"+offAccountInfo.getPassword());
			    result.success();
			}
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(),e);
		}
		return result;
	}
	
	
	@ApiOperation(value = "代理用户修改下线风险限额", notes = "代理用户修改下线风险限额", httpMethod = "POST")
	@RequestMapping(value = "/updateAccountRisk", method = RequestMethod.POST)
	@ResponseBody
	public RestResult updateAccountRisk(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody UpdateAccountRiskVo param) throws Exception {
		RestResult result = new RestResult();
		try {
			int userid = param.getUserid();
			String riskamount = param.getRiskamount();
            String supusername = param.getSupusername();
            int offtype = param.getOfftype();
			String ip = param.getIp();
			
			if (0==userid){
			      result.fail("用户ID",MessageTool.Code_2002);
			      LOG.info(result.getMessage());
			      return result;
			}
			
			OffAccountInfo offAccountInfo = offAccountInfoMapper.selectByPrimaryKey(param.getUserid());
			if(offAccountInfo==null){
			      result.fail(MessageTool.Code_3001);
			}else{
				offAccountInfo.setRiskamount(riskamount);
				offAccountInfo.setIp(ip);
				offAccountInfoService.updateOffAccountInfo(offAccountInfo);
				//offAccountInfoMapper.updateByPrimaryKey(offAccountInfo);
			    LOG.info("修改风险限额记录详情为："+" 管理员："+supusername+" 账户类型："+offtype+" IP："+ip+" 修改下家ID"+userid+" 风险限额修改为"+offAccountInfo.getRiskamount());
			    result.success();
			}
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(),e);
		}
		return result;
	}
	*/
	
	@ApiOperation(value = "代理用户修改下线代理占成", notes = "代理用户修改下线代理占成", httpMethod = "POST")
	@RequestMapping(value = "/updateAccountPercent", method = RequestMethod.POST)
	@ResponseBody
	public RestResult updateAccountPercent(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody UpdateAccountPerVo param) throws Exception {
		RestResult result = new RestResult();
		try {
			int supaccountId = param.getSupaccountid();
			int accountId = param.getAccountid();
			double percentage = param.getPetcentage();
			String ip = param.getIp();
			
			AccountInfo offAccountInfo = accountInfoMapper.selectByPrimaryKey(supaccountId);
			if(offAccountInfo==null){
			      result.fail(MessageTool.Code_3001);
			      return result;
			}else{
				if (!offAccountInfo.getLevel().equals("0")){
					if(!ToolsUtil.checkUpdatePeriod()){
						result.fail("修改失败，请于结算时间段重新操作!");
						LOG.info(result.getMessage());
					    return result;
					}
				}
				AccountInfo accountInfo = accountInfoMapper.selectByPrimaryKey(accountId);
		    	if (accountInfo==null){
		    		result.fail(accountInfo.getUsername(),MessageTool.Code_3001);
		    		return result;
		    	}
				//代理占比逻辑
				if (percentage>offAccountInfo.getPercentage()){
				      result.fail("代理占成",MessageTool.Code_1008);
				      LOG.info(result.getMessage());
				      return result;	
				}
				double ratio = (accountInfo.getPercentage()-percentage)/accountInfo.getPercentage();
				accountInfo.setPercentage(percentage);
				accountInfo.setIp(ip);
				accountInfoMapper.updateByPrimaryKey(accountInfo);
				
				System.out.println("ces----------------"+accountId+".."+ratio);
				accountInfoMapper.updateOffPercentage(ratio, accountId);
			    LOG.info("修改代理占成记录详情为："+" 管理员："+supaccountId+" IP："+ip+" 修改下家ID"+accountId+" 代理占成修改为"+percentage);
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
	@ApiOperation(value = "代理用户修改下线洗码比", notes = "代理用户修改下线代洗码比", httpMethod = "POST")
	@RequestMapping(value = "/updateAccountRatio", method = RequestMethod.POST)
	@ResponseBody
	public RestResult updateAccountRatio(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody UpdateAccountRatioVo param) throws Exception {
		RestResult result = new RestResult();
		try {
			int userid = param.getUserid();
			double ratio = param.getRatio();
            String supusername = param.getSupusername();
            int offtype = param.getOfftype();
			String ip = param.getIp();
			
			if (0==userid){
			      result.fail("用户ID",MessageTool.Code_2002);
			      LOG.info(result.getMessage());
			      return result;
			}
			
			if(!ToolsUtil.checkUpdatePeriod()){
				result.fail("修改失败，请于结算时间段重新操作!");
				LOG.info(result.getMessage());
			    return result;
			}
			
			OffAccountInfo offAccountInfo = offAccountInfoMapper.selectByPrimaryKey(param.getUserid());
			if(offAccountInfo==null){
			      result.fail(MessageTool.Code_3001);
			}else{
				//洗码比逻辑 
				OffAccountInfo supAccount = offAccountInfoMapper.selectByUsername(offAccountInfo.getSupusername());
		    	if (supAccount==null){
		    		result.fail(offAccountInfo.getSupusername(),MessageTool.Code_2005);
		    		return result;
		    	}
		    	if (ratio>supAccount.getRatio()){
				      result.fail("洗码比",MessageTool.Code_1008);
				      LOG.info(result.getMessage());
				      return result;	
				}
				//下线的洗码比对比
				List<OffAccountInfo> offAccountInfos = offAccountInfoMapper.selectBySupuserAndRatio(offAccountInfo.getUsername(),offAccountInfo.getOfftype());
                if (offAccountInfos.size()>=1){
                	if (ratio<offAccountInfos.get(0).getRatio()){
      			        result.fail("下级代理洗码比为"+offAccountInfos.get(0).getRatio()+",",MessageTool.Code_2007);
    			        LOG.info(result.getMessage());
    			        return result;
                	}
                }
				offAccountInfo.setRatio(ratio);
				offAccountInfo.setIp(ip);
				offAccountInfoService.updateOffAccountInfo(offAccountInfo);
				//offAccountInfoMapper.updateByPrimaryKey(offAccountInfo);
			    LOG.info("修改洗码比记录详情为："+" 管理员："+supusername+" 账户类型："+offtype+" IP："+ip+" 修改下家ID"+userid+" 代理洗码比修改为"+offAccountInfo.getRatio());
			    result.success();
			}
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(),e);
		}
		return result;
	}
	*/
	@ApiOperation(value = "代理用户修改下线状态", notes = "代理用户修改下线状态", httpMethod = "POST")
	@RequestMapping(value = "/updateAccountState", method = RequestMethod.POST)
	@ResponseBody
	public RestResult updateAccountState(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody UpdateAccountStateVo param) throws Exception {
		RestResult result = new RestResult();
		try {
			int supaccountId = param.getSupaccountid();
			int accountId = param.getAccountid();
			String state = param.getState();
			String ip = param.getIp();
			
			if (!(state.equals("0")||state.equals("1"))){
			      result.fail("状态",MessageTool.Code_1005);
			      LOG.info(result.getMessage());
			      return result;
			}
			
			AccountInfo offAccountInfo = accountInfoMapper.selectByPrimaryKey(accountId);
			if(offAccountInfo==null){
			      result.fail(MessageTool.Code_3001);
			      return result;
			}else{
				offAccountInfo.setState(state);
				offAccountInfo.setIp(ip);
				offAccountInfo.setPercentage(0.0);
				accountInfoMapper.updateByPrimaryKey(offAccountInfo);
				//修改用户的状态
				accountInfoMapper.updateOffState(state, accountId);
			    LOG.info("修改状态记录详情为："+" 管理员："+supaccountId+" IP："+ip+" 修改下家ID"+accountId+" 状态修改为"+state);
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
	@ApiOperation(value = "超级用户修改公告", notes = "超级用户修改公告", httpMethod = "POST")
	@RequestMapping(value = "/updateNotice", method = RequestMethod.POST)
	@ResponseBody
	public RestResult updateAccountRemark(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody NoticeInfoVo param) throws Exception {
		RestResult result = new RestResult();
		try {
			int noticeid = 1; //默认
			String title = param.getTitle();
			String notice = param.getNotice();
			String stype = param.getStype();
            String supusername = param.getSupusername();
            int offtype = param.getOfftype();
			String ip = param.getIp();
	
			if (stype.equals("")||!(stype.equals("0")||stype.equals("1"))){
			      result.fail("公告类型",MessageTool.Code_1005);
			      LOG.info(result.getMessage());
			      return result;
			}
			if (stype.equals("0"))
				noticeid = 1;
			else if (stype.equals("1"))
				noticeid = 2;
			//NoticeInfo noticeInfo  = mapper.map(param, NoticeInfo.class);

            NoticeInfo noticeInfo = noticeInfoMapper.selectByPrimaryKey(noticeid);
			if(noticeInfo==null){
			      result.fail(MessageTool.Code_4001);
			}else{
				noticeInfo.setTitle(title);
				noticeInfo.setNotice(notice);
				noticeInfo.setStype(stype);
				noticeInfo.setSupusername(supusername);
				noticeInfo.setOfftype(String.valueOf(offtype));
				noticeInfo.setUpdateip(ip);
				noticeInfo.setState("1");
				noticeInfo.setUpdatedate(new Date());
				noticeInfoMapper.updateByPrimaryKey(noticeInfo);
			    LOG.info("修改公告记录详情为："+" 管理员："+supusername+" 账户类型："+offtype+" IP："+ip+" 修改公告ID"+noticeid+" 状态修改为"+noticeInfo.getNoticeid());
			    result.success();
			}
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(),e);
		}
		return result;
	}
	
*/
	
}
