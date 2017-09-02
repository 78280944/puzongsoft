package com.lottery.api.dto;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class OrderDetailVo {

    @ApiModelProperty(value = "投注项编号", required = true)
    @NotBlank(message = "投注项编号不能为空")
    private String itemNo;

    @ApiModelProperty(value = "投注金额", required = true)
    @NotNull(message = "投注金额不能为空")
    @DecimalMin(value="0.01", message = "金额必须大于零")
    private Double detailAmount;

    public String getItemNo() {
	return itemNo;
    }

    public void setItemNo(String itemNo) {
	this.itemNo = itemNo;
    }

    public Double getDetailAmount() {
	return detailAmount;
    }

    public void setDetailAmount(Double detailAmount) {
	this.detailAmount = detailAmount;
    }

}
