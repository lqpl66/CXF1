package com.yl.beans;

import java.math.BigDecimal;

public class CardOrder {
	private Integer scenicId;
	private String scenicName;
	private String scenicCoverImgUrl;
	private String orderNo;
	private BigDecimal paymentAmount;
	private String intro;
	private Integer status;
	private String cardExpandList;
	private String provinceName;
	private String cityName;
	private String address;
	private String addTime;
	private String appointment;


	public String getAppointment() {
		return appointment;
	}

	public void setAppointment(String appointment) {
		this.appointment = appointment;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCardExpandList() {
		return cardExpandList;
	}

	public void setCardExpandList(String cardExpandList) {
		this.cardExpandList = cardExpandList;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Integer getScenicId() {
		return scenicId;
	}

	public void setScenicId(Integer scenicId) {
		this.scenicId = scenicId;
	}

	public String getScenicName() {
		return scenicName;
	}

	public void setScenicName(String scenicName) {
		this.scenicName = scenicName;
	}

	public String getScenicCoverImgUrl() {
		return scenicCoverImgUrl;
	}

	public void setScenicCoverImgUrl(String scenicCoverImgUrl) {
		this.scenicCoverImgUrl = scenicCoverImgUrl;
	}

	public BigDecimal getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(BigDecimal paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
