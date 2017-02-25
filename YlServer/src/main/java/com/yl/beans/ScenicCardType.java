package com.yl.beans;

import java.math.BigDecimal;

public class ScenicCardType {
	private Integer cardType;
	private String date;
	private BigDecimal price;
	private BigDecimal usePrice;
	private BigDecimal totalPrice;
	private Integer remindNum;

	public Integer getRemindNum() {
		return remindNum;
	}

	public void setRemindNum(Integer remindNum) {
		this.remindNum = remindNum;
	}

	public Integer getCardType() {
		return cardType;
	}

	public void setCardType(Integer cardType) {
		this.cardType = cardType;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getUsePrice() {
		return usePrice;
	}

	public void setUsePrice(BigDecimal usePrice) {
		this.usePrice = usePrice;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

}
