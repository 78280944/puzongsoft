package com.lottery.api.dto;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class GameLobbyVo {
	
	@ApiModelProperty(value = "游戏大厅编号", required = true)
	private Integer sid;

	public Integer getSid() {
		return sid;
	}

	public void setSid(Integer sid) {
		this.sid = sid;
	}
	
	
}
