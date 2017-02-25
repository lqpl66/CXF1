package com.yl.beans;

public class UserCard {

	private Integer id;
	private Integer baseCardId;
	private Integer userId;
	private Integer cardType;
	private String uuid;
	private Integer status;
	private String addTime;
	private Integer cardStatus;
	private String scenicNamelist;
	
	public String getScenicNamelist() {
		return scenicNamelist;
	}
	public void setScenicNamelist(String scenicNamelist) {
		this.scenicNamelist = scenicNamelist;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getBaseCardId() {
		return baseCardId;
	}
	public void setBaseCardId(Integer baseCardId) {
		this.baseCardId = baseCardId;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getCardType() {
		return cardType;
	}
	public void setCardType(Integer cardType) {
		this.cardType = cardType;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	public Integer getCardStatus() {
		return cardStatus;
	}
	public void setCardStatus(Integer cardStatus) {
		this.cardStatus = cardStatus;
	}

}
