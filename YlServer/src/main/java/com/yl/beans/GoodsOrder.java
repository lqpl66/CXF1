package com.yl.beans;

import java.math.BigDecimal;

public class GoodsOrder {
	private Integer id;
	private String orderNo;
	private Integer userId;
	private BigDecimal paymentAmount;
	private BigDecimal deductionAmount;
	private Integer status;
	private Integer orderType;
	private Integer shopId;
	private Integer isSelf;
	private String shopName;
	private Integer goodsFreightExpandId;
	private String freightNo;
	private String freightOrderNo;
	private Integer goodsTotalNum;
	private Integer isEvaluate;
	private String shopFileCode;
	// private String goodsList;
	private String endTime;
	private Integer userAddressId;
	private String provinceName;
	private String cityName;
	private String districtName;
	private String addressInfo;
	private String Zip;
	private String recipient;
	private String recipientMobile;
	private Integer workState;

	public Integer getWorkState() {
		return workState;
	}

	public void setWorkState(Integer workState) {
		this.workState = workState;
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

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public String getAddressInfo() {
		return addressInfo;
	}

	public void setAddressInfo(String addressInfo) {
		this.addressInfo = addressInfo;
	}

	public String getZip() {
		return Zip;
	}

	public void setZip(String zip) {
		Zip = zip;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getRecipientMobile() {
		return recipientMobile;
	}

	public void setRecipientMobile(String recipientMobile) {
		this.recipientMobile = recipientMobile;
	}

	public Integer getUserAddressId() {
		return userAddressId;
	}

	public void setUserAddressId(Integer userAddressId) {
		this.userAddressId = userAddressId;
	}

	public BigDecimal getDeductionAmount() {
		return deductionAmount;
	}

	public void setDeductionAmount(BigDecimal deductionAmount) {
		this.deductionAmount = deductionAmount;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	// public String getGoodsList() {
	// return goodsList;
	// }
	//
	// public void setGoodsList(String goodsList) {
	// this.goodsList = goodsList;
	// }

	public String getShopFileCode() {
		return shopFileCode;
	}

	public void setShopFileCode(String shopFileCode) {
		this.shopFileCode = shopFileCode;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public BigDecimal getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(BigDecimal paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public Integer getShopId() {
		return shopId;
	}

	public void setShopId(Integer shopId) {
		this.shopId = shopId;
	}

	public Integer getIsSelf() {
		return isSelf;
	}

	public void setIsSelf(Integer isSelf) {
		this.isSelf = isSelf;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public Integer getGoodsFreightExpandId() {
		return goodsFreightExpandId;
	}

	public void setGoodsFreightExpandId(Integer goodsFreightExpandId) {
		this.goodsFreightExpandId = goodsFreightExpandId;
	}

	public String getFreightNo() {
		return freightNo;
	}

	public void setFreightNo(String freightNo) {
		this.freightNo = freightNo;
	}

	public String getFreightOrderNo() {
		return freightOrderNo;
	}

	public void setFreightOrderNo(String freightOrderNo) {
		this.freightOrderNo = freightOrderNo;
	}

	public Integer getGoodsTotalNum() {
		return goodsTotalNum;
	}

	public void setGoodsTotalNum(Integer goodsTotalNum) {
		this.goodsTotalNum = goodsTotalNum;
	}

	public Integer getIsEvaluate() {
		return isEvaluate;
	}

	public void setIsEvaluate(Integer isEvaluate) {
		this.isEvaluate = isEvaluate;
	}

}
