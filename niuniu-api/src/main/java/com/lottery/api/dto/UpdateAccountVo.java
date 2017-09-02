package com.lottery.api.dto;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class UpdateAccountVo{
	
	    @ApiModelProperty(value = "用户id", required = true)
        private int userid;
      
	    @ApiModelProperty(value = "用户名")
	    private String username;
	    
	    
	    @ApiModelProperty(value = "密码")
	    private String password;

	    @ApiModelProperty(value = "联系电话")
	    private String phone;
	    
	    @ApiModelProperty(value = "微信号")
	    private String webchat;
	    
	    @ApiModelProperty(value = "状态")
	    private String state;
	    
	    @ApiModelProperty(value = "ip")
	    private String ip;    
	    
		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}

		public int getUserid() {
			return userid;
		}

		public void setUserid(int userid) {
			this.userid = userid;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
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

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}
	    
	    
}
