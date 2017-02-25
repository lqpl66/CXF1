package com.yl.beans;

import java.io.Serializable;

public class LogRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String userName;
	private String smsCode;
	private Integer operateType;
	private String smsPwdExpiry;
	private String addTime;

	public Integer getOperateType() {
		return operateType;
	}

	public void setOperateType(Integer operateType) {
		this.operateType = operateType;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSmsCode() {
		return smsCode;
	}

	public void setSmsCode(String smsCode) {
		this.smsCode = smsCode;
	}

	public String getSmsPwdExpiry() {
		return smsPwdExpiry;
	}

	public void setSmsPwdExpiry(String smsPwdExpiry) {
		this.smsPwdExpiry = smsPwdExpiry;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

}
