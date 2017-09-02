package com.lottery.api.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
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
import com.lottery.api.dto.SubAccountInfoVo;
import com.lottery.api.dto.UpdatePlayPassVo;
import com.lottery.api.dto.UpdateSubAccAuserVo;
import com.lottery.api.dto.UpdateSubAccRightVo;
import com.lottery.api.dto.UpdateSubAccStateVo;
import com.lottery.api.util.ToolsUtil;
import com.lottery.orm.bo.AccountDetail;
import com.lottery.orm.bo.AccountInfo;
import com.lottery.orm.bo.OffAccountInfo;
import com.lottery.orm.dao.AccountDetailMapper;
import com.lottery.orm.dao.AccountInfoMapper;
import com.lottery.orm.dao.OffAccountInfoMapper;
import com.lottery.orm.dto.SubAccountDto;
import com.lottery.orm.result.RestResult;
import com.lottery.orm.result.SubAccountListResult;
import com.lottery.orm.service.OffAccountInfoService;
import com.lottery.orm.util.EnumType;
import com.lottery.orm.util.MessageTool;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@RequestMapping(value = "/subAccount", produces = {"application/json;charset=UTF-8"})
@Api(value = "/subAccount", description = "代理的子帐号信息接口")
@Controller
public class SubAccountInfoController {
	public static final Logger LOG = Logger.getLogger(SubAccountInfoController.class);
	
	@Autowired
	private Mapper mapper;
	
	@Autowired
    private OffAccountInfoService offAccountInfoService;
	
	@Autowired
	private OffAccountInfoMapper offAccountInfoMapper;
	
	@Autowired
    private AccountDetailMapper accountDetailMapper;
	
	@Autowired
    private AccountInfoMapper accountInfoMapper;
	
	@ApiOperation(value = "新增子帐号", notes = "新增子帐号", httpMethod = "POST")
	@RequestMapping(value = "/addSubAccountInfo", method = RequestMethod.POST)
	@ResponseBody
	public RestResult addSubAccountInfo(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody SubAccountInfoVo param) throws Exception {
		RestResult result = new RestResult();
		try {
			
			String username = param.getUsername();
			String password = param.getPassword();
			String supusername = param.getSupusername();	
			String level = param.getLevel();
			String query = param.getQuery()==null?"":param.getQuery();
			//String manage = param.getManage();
			//参数合规性校验，必要参数不能为空
			if (ToolsUtil.isEmptyTrim(username)||ToolsUtil.isEmptyTrim(password)){
			      result.fail("用户名，密码",MessageTool.Code_2002);
			      LOG.info(result.getMessage());
			      return result;
			}
			
			//参数合规性校验，必要参数不能为空
			if (ToolsUtil.isEmptyTrim(supusername)||ToolsUtil.isEmptyTrim(level)){
			      result.fail("管理操作员,代理级别",MessageTool.Code_2002);
			      LOG.info(result.getMessage());
			      return result;
			}
			
			if (!"".equals(query)){
		        if (ToolsUtil.checkQuery(query)){
			      result.fail("权限设置",MessageTool.Code_1005);
			      LOG.info(result.getMessage());
			      return result;
		        }
			}

			OffAccountInfo paraInfo = mapper.map(param, OffAccountInfo.class);
			OffAccountInfo offAccountInfo = offAccountInfoMapper.selectByUsername(paraInfo.getUsername());
		    if (offAccountInfo!=null){
			      result.fail(username,MessageTool.Code_2005);
		    }else{
		    	//账户唯一性判断
		    	AccountInfo accountInfo = accountInfoMapper.selectByUsername(paraInfo.getUsername());
		    	if (accountInfo!=null){
		    		result.fail(username,MessageTool.Code_2005);
				    LOG.info(result.getMessage());
				    return result;
		    	}
		    	//获取管理员level
		    	OffAccountInfo OffAccountInfo1 = offAccountInfoMapper.selectByUsername(supusername);
		    	if (OffAccountInfo1 == null){
		    		result.fail(supusername,MessageTool.Code_2006);
				    LOG.info(result.getMessage());
				    return result;
		    	}
		    	
		    	level = OffAccountInfo1.getLevel();		
		    	paraInfo.setLevel(level);
		    	paraInfo.setPassword(DigestUtils.md5Hex(password));
			    paraInfo.setState("1");//默认状态正常
			    paraInfo.setOfftype("2");
			    paraInfo.setManage(query);
			    paraInfo.setInputdate(new Date());
			    offAccountInfoService.addOffAccountInfo(paraInfo,"2");
			    result.success();
		    }

			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(),e);
		}
		return result;
	}
	

	@ApiOperation(value = "获取该代理的子账号列表", notes = "获取该代理的子账号列表", httpMethod = "POST")
	@RequestMapping(value = "/getAllSubAccountInfo", method = RequestMethod.POST)
	@ResponseBody
	public SubAccountListResult getAllSubAccountInfo(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody AccountInfoVo param) throws Exception {
	    	SubAccountListResult result = new SubAccountListResult();
		try {
			OffAccountInfo offacount = offAccountInfoMapper.selectByPrimaryKey(param.getUserid());
			//OffAccountInfo offacount = offAccountInfoMapper.selectByUseridAndType(param.getUserid(), EnumType.OffType.Sub.ID);
			if(offacount==null){
				result.fail(MessageTool.Code_3001);
			}else{
				List<OffAccountInfo> SubAccountInfos = offAccountInfoMapper.selectBySupusername(offacount.getUsername(),EnumType.OffType.Sub.ID,param.getBeginRow(), param.getPageSize());
				List<SubAccountDto> list = new ArrayList<SubAccountDto>();
				for (int i = 0;i<SubAccountInfos.size();i++){
					  AccountDetail accountDetail =  accountDetailMapper.selectByUserId(SubAccountInfos.get(i).getUserid(),SubAccountInfos.get(i).getOfftype());
				      SubAccountDto rAcDto = new SubAccountDto();
				      rAcDto.setUserid(null==SubAccountInfos.get(i).getUserid()||"".equals(SubAccountInfos.get(i).getUserid())||0==SubAccountInfos.get(i).getUserid() ?0:SubAccountInfos.get(i).getUserid());
				      rAcDto.setUsername(null==SubAccountInfos.get(i).getUsername()||"".equals(SubAccountInfos.get(i).getUsername()) ?"":SubAccountInfos.get(i).getUsername());
				      rAcDto.setAusername(null==SubAccountInfos.get(i).getAusername()||"".equals(SubAccountInfos.get(i).getAusername()) ?"":SubAccountInfos.get(i).getAusername());
				      rAcDto.setPassword(null==SubAccountInfos.get(i).getPassword()||"".equals(SubAccountInfos.get(i).getPassword()) ?"":SubAccountInfos.get(i).getPassword());
				      rAcDto.setQuery(null==SubAccountInfos.get(i).getQuery()||"".equals(SubAccountInfos.get(i).getQuery()) ?"":SubAccountInfos.get(i).getQuery());
				      //rAcDto.setManage(null==SubAccountInfos.get(i).getManage()||"".equals(SubAccountInfos.get(i).getManage()) ?"":SubAccountInfos.get(i).getManage());
				      rAcDto.setState(null==SubAccountInfos.get(i).getState()||"".equals(SubAccountInfos.get(i).getState()) ?"":SubAccountInfos.get(i).getState());
				      rAcDto.setSupusername(null==SubAccountInfos.get(i).getSupusername()||"".equals(SubAccountInfos.get(i).getSupusername()) ?"":SubAccountInfos.get(i).getSupusername());
				      rAcDto.setLevel(null==SubAccountInfos.get(i).getLevel()||"".equals(SubAccountInfos.get(i).getLevel()) ?"":SubAccountInfos.get(i).getLevel());
				      rAcDto.setOfftype(null==SubAccountInfos.get(i).getOfftype()||"".equals(SubAccountInfos.get(i).getOfftype()) ?"":SubAccountInfos.get(i).getOfftype());	 
				      rAcDto.setAccountID(accountDetail.getAccountid());
				      rAcDto.setAccountAmount(accountDetail.getMoney());
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
	

	@ApiOperation(value = "代理用户修改子账户别名", notes = "代理用户修改子账户别名", httpMethod = "POST")
	@RequestMapping(value = "/updateSubAccountAuser", method = RequestMethod.POST)
	@ResponseBody
	public RestResult updateSubAccountAuser(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody UpdateSubAccAuserVo param) throws Exception {
		RestResult result = new RestResult();
		try {
			int userid = param.getUserid();
			String ausername = param.getAusername();
            String supusername = param.getSupusername();
            int offtype = param.getOfftype();
			String ip = param.getIp();
			
			if (0==userid){
			      result.fail("用户ID",MessageTool.Code_2002);
			      LOG.info(result.getMessage());
			      return result;
			}
			
			OffAccountInfo subOffAccountInfo = offAccountInfoMapper.selectByPrimaryKey(param.getUserid());
			if(subOffAccountInfo==null){
			      result.fail(MessageTool.Code_3001);
			}else{

				subOffAccountInfo.setAusername(ausername);
				offAccountInfoMapper.updateByPrimaryKey(subOffAccountInfo);
			    LOG.info("修改子账户记录详情为："+" 管理员："+supusername+" 账户类型："+offtype+" IP："+ip+" 修改账户ID"+userid+" 别名修改为"+ausername);
			    result.success();
			}
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(),e);
		}
		return result;
	}
	
	
	@ApiOperation(value = "代理用户修改子账户密码", notes = "代理用户修改子账户密码", httpMethod = "POST")
	@RequestMapping(value = "/updateSubAccountPass", method = RequestMethod.POST)
	@ResponseBody
	public RestResult updateSubAccountPass(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody UpdatePlayPassVo param) throws Exception {
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
	
	@ApiOperation(value = "代理用户修改子账户状态", notes = "代理用户修改子账户状态", httpMethod = "POST")
	@RequestMapping(value = "/updateSubAccountState", method = RequestMethod.POST)
	@ResponseBody
	public RestResult updateSubAccountState(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody UpdateSubAccStateVo param) throws Exception {
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
			
			OffAccountInfo offAccountInfo = offAccountInfoMapper.selectByPrimaryKey(param.getUserid());
			if(offAccountInfo==null){
			      result.fail(MessageTool.Code_3001);
			}else{
				offAccountInfo.setState(state);
				offAccountInfoMapper.updateByPrimaryKey(offAccountInfo);
				//修改账户状态
				AccountDetail accountDetail = accountDetailMapper.selectByUserId(offAccountInfo.getUserid(), offAccountInfo.getOfftype());
				accountDetail.setState(state);
				accountDetailMapper.updateByPrimaryKey(accountDetail);
			    LOG.info("修改状态记录详情为："+" 管理员："+supusername+" 账户类型："+offtype+" IP："+ip+" 修改下家ID"+userid+" 状态修改为"+offAccountInfo.getState());
			    result.success();
			}
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(),e);
		}
		return result;
	}
	
	@ApiOperation(value = "代理用户修改子账户权限", notes = "代理用户修改子账户权限", httpMethod = "POST")
	@RequestMapping(value = "/updateSubAccountRight", method = RequestMethod.POST)
	@ResponseBody
	public RestResult updateSubAccountState(@ApiParam(value = "Json参数", required = true) @Validated @RequestBody UpdateSubAccRightVo param) throws Exception {
		RestResult result = new RestResult();
		try {
			int userid = param.getUserid();
			String query = param.getQuery();
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
				offAccountInfo.setQuery(query);
				offAccountInfoMapper.updateByPrimaryKey(offAccountInfo);
			    LOG.info("修改状态记录详情为："+" 管理员："+supusername+" 账户类型："+offtype+" IP："+ip+" 修改下家ID"+userid+" 权限修改为"+offAccountInfo.getQuery());
			    result.success();
			}
			LOG.info(result.getMessage());
		} catch (Exception e) {
			result.error();
			LOG.error(e.getMessage(),e);
		}
		return result;
	}
	
}
