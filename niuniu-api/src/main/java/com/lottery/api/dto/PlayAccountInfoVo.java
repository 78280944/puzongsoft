package com.lottery.api.dto;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class PlayAccountInfoVo extends BaseAccountInfoVo{
	
	   // @ApiModelProperty(value = "点数限额")
	  //  private Double limited;
	    @ApiModelProperty(value = "电话")
	    private String phone; 
	    
	    @ApiModelProperty(value = "微信")
	    private String webchat;
	    
	    @ApiModelProperty(value = "银行户名")
	    private String bankid;
	    
	    @ApiModelProperty(value = "银行名称")
	    private String bankname;
	    
	    @ApiModelProperty(value = "开户行")
	    private String bankaddress;
	    
	    @ApiModelProperty(value = "银行账号")
	    private String bankaccount;
	    
	    @ApiModelProperty(value = "邀请码")
	    private String code;
	    
	    @ApiModelProperty(value = "安全码")
	    private String sfcode;
	        
	    
		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getSfcode() {
			return sfcode;
		}

		public void setSfcode(String sfcode) {
			this.sfcode = sfcode;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public String getWebchat() {
			return webchat;
		}

		public void setWebchat(String webchat) {
			this.webchat = webchat;
		}

		public String getBankid() {
			return bankid;
		}

		public void setBankid(String bankid) {
			this.bankid = bankid;
		}

		public String getBankname() {
			return bankname;
		}

		public void setBankname(String bankname) {
			this.bankname = bankname;
		}

		public String getBankaddress() {
			return bankaddress;
		}

		public void setBankaddress(String bankaddress) {
			this.bankaddress = bankaddress;
		}

		public String getBankaccount() {
			return bankaccount;
		}

		public void setBankaccount(String bankaccount) {
			this.bankaccount = bankaccount;
		}
	    
	  

}
