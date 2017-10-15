package com.lottery.orm.result;

import java.util.List;

import com.lottery.orm.bo.LotteryRoom;
import com.lottery.orm.dto.LotteryGameDto;
import com.wordnik.swagger.annotations.ApiModelProperty;

public class GameRoomResult extends RestResult{
	
	@ApiModelProperty(value = "游戏类型", required = true)
	public List<LotteryRoom> data = null;
	
	public void success(List<LotteryRoom> data) {
		success();
		this.data = data;
	}
	public List<LotteryRoom> getData() {
		return data;
	}

	public void setData(List<LotteryRoom> data) {
		this.data = data;
	}
}
