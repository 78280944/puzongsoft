package com.lottery.api.dto;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class NoticeTypeVo {
	
	@ApiModelProperty(value = "账户类型，00：玩家账户；1：玩家账户；2：代理账户；3：子账户", required = true)
	private String offtype;

	public String getOfftype() {
		return offtype;
	}

	public void setOfftype(String offtype) {
		this.offtype = offtype;
	}


}
