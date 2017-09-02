package com.lottery.api.dto;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class NoticeTypeVo {
	
	@ApiModelProperty(value = "公告类型，0：代理公告；1，玩家公告", required = true)
	private String stype;

	public String getStype() {
		return stype;
	}

	public void setStype(String stype) {
		this.stype = stype;
	}
	
}
