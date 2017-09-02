package com.lottery.api.dto;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class LoginParamVo {
	
	@ApiModelProperty(value = "用户名", required = true)
	protected String username;
	@ApiModelProperty(value = "用户密码", required = true)
    private String password;
	@ApiModelProperty(value = "IP", required = true)
	private String ip;
	
	//private String  
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
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
	

}
