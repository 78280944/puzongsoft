package com.lottery.orm.result;

import java.util.List;

import com.lottery.orm.dto.SubAccountDto;
import com.wordnik.swagger.annotations.ApiModelProperty;

public class SubAccountListResult extends BaseRestResult {
	
	@ApiModelProperty(value = "子账户列表数据", required = true)
	private List<SubAccountDto> data;

	public void success(List<SubAccountDto> data) {
		success();
		this.data = data;
	}

	public List<SubAccountDto> getData() {
		return data;
	}

	public void setData(List<SubAccountDto> list) {
		this.data = list;
	}

}
