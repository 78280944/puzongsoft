package com.lottery.orm.result;

import java.util.List;

import com.lottery.orm.dto.AgencyWinReportDto;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * POJO class for rest process result.
 * 
 */
public class AgencyWinReportResult extends BaseRestResult {

	@ApiModelProperty(value = "输赢报表数据", required = true)
	private List<AgencyWinReportDto> data;

	public void success(List<AgencyWinReportDto> data) {
		success();
		this.data = data;
	}

	public List<AgencyWinReportDto> getData() {
		return data;
	}

	public void setData(List<AgencyWinReportDto> data) {
		this.data = data;
	}

}
