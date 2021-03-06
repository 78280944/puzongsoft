package com.lottery.api.dto;

import java.util.Date;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class UpdatePalyAccountVo extends BaseAccountInfoVo{
	
        @ApiModelProperty(value = "用户id", required = true)
        private int userid;
         
	    @ApiModelProperty(value = "用户名")
	    private String username;
	    
	    @ApiModelProperty(value = "别名")
	    private String ausername;
	    
	    @ApiModelProperty(value = "密码")
	    private String password;

	    @ApiModelProperty(value = "点数限额")
	    private Double limited;
	    
	    @ApiModelProperty(value = "洗码比")
	    private Double ratio;
	    
	    @ApiModelProperty(value = "状态")
	    private String state;

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

		public String getAusername() {
			return ausername;
		}

		public void setAusername(String ausername) {
			this.ausername = ausername;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public Double getLimited() {
			return limited;
		}

		public void setLimited(Double limited) {
			this.limited = limited;
		}

		public Double getRatio() {
			return ratio;
		}

		public void setRatio(Double ratio) {
			this.ratio = ratio;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

}
