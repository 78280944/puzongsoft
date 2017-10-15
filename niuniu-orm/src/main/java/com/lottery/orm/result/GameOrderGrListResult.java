package com.lottery.orm.result;

import java.util.List;

import com.lottery.orm.dto.RoomOrderDto;
import com.lottery.orm.dto.RoomOrderItemDto;
import com.wordnik.swagger.annotations.ApiModelProperty;

public class GameOrderGrListResult extends RestResult{
	@ApiModelProperty(value = "投注明细", required = true)
	private List<List<RoomOrderItemDto>> data = null;

	public void success(List<List<RoomOrderItemDto>> data) {
		success();
		this.data = data;
	}

	public List<List<RoomOrderItemDto>> getData() {
		return data;
	}

	public void setData(List<List<RoomOrderItemDto>> list) {
		this.data = list;
	}
}
