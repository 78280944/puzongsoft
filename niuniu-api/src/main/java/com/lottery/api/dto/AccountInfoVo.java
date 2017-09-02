package com.lottery.api.dto;

import javax.validation.constraints.Min;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class AccountInfoVo  extends PageParamVo{
	
	@ApiModelProperty(value = "用户ID", required = true)
	@Min(value = 0, message = "ID格式不正确")
    private Integer userid; 

    public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

}