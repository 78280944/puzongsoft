package com.lottery.orm.result;

import java.util.List;

import com.lottery.orm.dto.OffAccountDto;
import com.wordnik.swagger.annotations.ApiModelProperty;

public class OffAccountListResult extends BaseRestResult {
	
	@ApiModelProperty(value = "代理账户列表数据", required = true)
	private List<OffAccountDto> data;

	public void success(List<OffAccountDto> data) {
		success();
		this.data = data;
	}

	public List<OffAccountDto> getData() {
		return data;
	}

	public void setData(List<OffAccountDto> list) {
		this.data = list;
	}
}
