package com.lottery.api.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class UserRechargeVo {
	
	@ApiModelProperty(value = "用户ID", required = true)
    @NotNull(message = "用户ID不能为空")
    @Min(value=0, message = "用户ID格式不正确")
    private Integer accountId;
	
	@ApiModelProperty(value = "请求流水号", required = true)
    @NotNull(message = "请求流水号不能为空")
	private String requestNo;
	
	@ApiModelProperty(value = "版本号", required = true)
    @NotNull(message = "版本号不能为空")
	private String version;
	
	@ApiModelProperty(value = "商户号", required = true)
    @NotNull(message = "商户号不能为空")
	private String merNo;
	
	@ApiModelProperty(value = "产品编号", required = true)
    @NotNull(message = "产品编号不能为空")
	private String productId;
	
	@ApiModelProperty(value = "交易日期,日期格式yyyyMMdd", required = true)
    @NotNull(message = "交易日期不能为空")
	private String orderDate;
	
	@ApiModelProperty(value = "订单号", required = true)
    @NotNull(message = "订单号不能为空")
	private String orderNo;
	
	@ApiModelProperty(value = "交易金额", required = true)
    @NotNull(message = "交易金额不能为空")
	private Double transAmt;
	
	@ApiModelProperty(value = "页面通知地址", required = true)
	private String returnUrl;
	
	@ApiModelProperty(value = "异步通知地址", required = true)
    private String notifyUrl;

	@ApiModelProperty(value = "商品描述信息", required = true)
    private String commodityName;

	@ApiModelProperty(value = "支付链接", required = true)
    private String mwebUrl;

	@ApiModelProperty(value = "交易备注", required = true)
    private String remark;

	@ApiModelProperty(value = "扩展参数", required = true)
    private String extendField;

	@ApiModelProperty(value = "应答码", required = true)
    private String respCode;

	@ApiModelProperty(value = "应答码描述", required = true)
    private String respDesc;

	@ApiModelProperty(value = "验签字段", required = true)
    private String signature;

	@ApiModelProperty(value = "IP", required = true)
    private String orderIp;
	
	@ApiModelProperty(value = "充值状态，01：充值成功；02：充值失败", required = true)
    private String orderState;
	
	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public String getRequestNo() {
		return requestNo;
	}

	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getMerNo() {
		return merNo;
	}

	public void setMerNo(String merNo) {
		this.merNo = merNo;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Double getTransAmt() {
		return transAmt;
	}

	public void setTransAmt(Double transAmt) {
		this.transAmt = transAmt;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public String getCommodityName() {
		return commodityName;
	}

	public void setCommodityName(String commodityName) {
		this.commodityName = commodityName;
	}

	public String getMwebUrl() {
		return mwebUrl;
	}

	public void setMwebUrl(String mwebUrl) {
		this.mwebUrl = mwebUrl;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getExtendField() {
		return extendField;
	}

	public void setExtendField(String extendField) {
		this.extendField = extendField;
	}

	public String getRespCode() {
		return respCode;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}

	public String getRespDesc() {
		return respDesc;
	}

	public void setRespDesc(String respDesc) {
		this.respDesc = respDesc;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getOrderIp() {
		return orderIp;
	}

	public void setOrderIp(String orderIp) {
		this.orderIp = orderIp;
	}

	public String getOrderState() {
		return orderState;
	}

	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}
	

}
