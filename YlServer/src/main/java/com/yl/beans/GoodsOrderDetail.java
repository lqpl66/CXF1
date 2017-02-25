package com.yl.beans;

import java.math.BigDecimal;

public class GoodsOrderDetail {
	private Integer id;
	private String orderNo;
	private Integer userId;
	private Integer shopId;
	private String shopName;
	private String shopFileCode;
	private Integer isSelf;
	private BigDecimal postageAmount;
	private BigDecimal goodsAmount;
	private BigDecimal deductionAmount;
	private BigDecimal payableAmount;
	private BigDecimal paymentAmount;
	private Integer status;
	private String remark;
	private String addTime;
	private String endTime;
	private Integer orderType;
	private Integer goodsFreightExpandId;
	private String freightNo;
	private String freightOrderNo;
	private Integer userAddressId;
	private Integer paymentType;
	private Integer isEvaluate;

	public Integer getIsEvaluate() {
		return isEvaluate;
	}

	public void setIsEvaluate(Integer isEvaluate) {
		this.isEvaluate = isEvaluate;
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

	public Integer getShopId() {
		return shopId;
	}

	public void setShopId(Integer shopId) {
		this.shopId = shopId;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getShopFileCode() {
		return shopFileCode;
	}

	public void setShopFileCode(String shopFileCode) {
		this.shopFileCode = shopFileCode;
	}

	public Integer getIsSelf() {
		return isSelf;
	}

	public void setIsSelf(Integer isSelf) {
		this.isSelf = isSelf;
	}

	public BigDecimal getPostageAmount() {
		return postageAmount;
	}

	public void setPostageAmount(BigDecimal postageAmount) {
		this.postageAmount = postageAmount;
	}

	public BigDecimal getGoodsAmount() {
		return goodsAmount;
	}

	public void setGoodsAmount(BigDecimal goodsAmount) {
		this.goodsAmount = goodsAmount;
	}

	public BigDecimal getDeductionAmount() {
		return deductionAmount;
	}

	public void setDeductionAmount(BigDecimal deductionAmount) {
		this.deductionAmount = deductionAmount;
	}

	public BigDecimal getPayableAmount() {
		return payableAmount;
	}

	public void setPayableAmount(BigDecimal payableAmount) {
		this.payableAmount = payableAmount;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
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

	public Integer getUserAddressId() {
		return userAddressId;
	}

	public void setUserAddressId(Integer userAddressId) {
		this.userAddressId = userAddressId;
	}

	public Integer getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(Integer paymentType) {
		this.paymentType = paymentType;
	}

}
