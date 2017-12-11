package com.lottery.orm.result;

import com.lottery.orm.dto.UserRechargeDto;
import com.wordnik.swagger.annotations.ApiModelProperty;

public class UserRechargeResult  extends BaseRestResult {
	
	@ApiModelProperty(value = "用于取现记录", required = true)
	private UserRechargeDto data;

	public void success(UserRechargeDto data) {
		success();
		this.data = data;
	}

	public UserRechargeDto getData() {
		return data;
	}

	public void setData(UserRechargeDto data) {
		this.data = data;
	}
}
