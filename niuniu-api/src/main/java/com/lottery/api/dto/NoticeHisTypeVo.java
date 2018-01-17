package com.lottery.api.dto;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class NoticeHisTypeVo extends PageParamVo {
	
	@ApiModelProperty(value = "账户类型，99：试玩账户；1：代理账户；2：子账户；3：玩家账户", required = true)
	private String offtype;

	public String getOfftype() {
		return offtype;
	}

	public void setOfftype(String offtype) {
		this.offtype = offtype;
	}


}