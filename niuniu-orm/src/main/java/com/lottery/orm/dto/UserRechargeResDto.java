package com.lottery.orm.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class UserRechargeResDto {
	
	@ApiModelProperty(value = "用户ID", required = true)
    @NotNull(message = "用户ID不能为空")
    @Min(value=0, message = "用户ID格式不正确")
    private Integer accountId;
	
	@ApiModelProperty(value = "用户名", required = true)
    @NotNull(message = "用户名不能为空")
    private String username;
	
	@ApiModelProperty(value = "订单号", required = true)
    @NotNull(message = "订单号不能为空")
    private String orderNo;
	
	@ApiModelProperty(value = "请求编号", required = true)
	private String requestNo;
	
	@ApiModelProperty(value = "页面通知地址", required = true)
	private String returnUrl;
	
	@ApiModelProperty(value = "异步通知地址", required = true)
	private String notifyUrl;
	
	@ApiModelProperty(value = "商户号", required = true)
	private String merNo;
	
	@ApiModelProperty(value = "支付链接", required = true)
	private String mwebUrl;
	
	@ApiModelProperty(value = "交易类型", required = true)
	private String transId;
	
	@ApiModelProperty(value = "版本号", required = true)
	private String version;
	
	@ApiModelProperty(value = "验签字段", required = true)
	private String signature;
	
	@ApiModelProperty(value = "订单日期，格式yyyyMMdd", required = true)
    private String orderDate;
	
	@ApiModelProperty(value = "产品编号，微信1205", required = true)
    @NotNull(message = "产品编号不能为空")
	private String productId;
	
	@ApiModelProperty(value = "交易金额，单位为分，1元传100", required = true)
    @NotNull(message = "交易金额不能为空")
	private Double transAmt;
	
	@ApiModelProperty(value = "IP", required = true)
    private String orderIp;

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getRequestNo() {
		return requestNo;
	}

	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
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

	public String getMerNo() {
		return merNo;
	}

	public void setMerNo(String merNo) {
		this.merNo = merNo;
	}

	public String getMwebUrl() {
		return mwebUrl;
	}

	public void setMwebUrl(String mwebUrl) {
		this.mwebUrl = mwebUrl;
	}

	public String getTransId() {
		return transId;
	}

	public void setTransId(String transId) {
		this.transId = transId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public Double getTransAmt() {
		return transAmt;
	}

	public void setTransAmt(Double transAmt) {
		this.transAmt = transAmt;
	}

	public String getOrderIp() {
		return orderIp;
	}

	public void setOrderIp(String orderIp) {
		this.orderIp = orderIp;
	}

}
