package com.yl.beans;

import java.math.BigDecimal;

public class GoodsExpand {
	private Integer id;
	private Integer goodId;
	private Integer goodsStandardId;
	private Integer goodNumber;
	private String orderNo;
	private String goodsStandardName;
	private String goodName;
	private BigDecimal price;
	
	
	public String getGoodsStandardName() {
		return goodsStandardName;
	}

	public void setGoodsStandardName(String goodsStandardName) {
		this.goodsStandardName = goodsStandardName;
	}

	public String getGoodName() {
		return goodName;
	}

	public void setGoodName(String goodName) {
		this.goodName = goodName;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getGoodId() {
		return goodId;
	}

	public void setGoodId(Integer goodId) {
		this.goodId = goodId;
	}

	public Integer getGoodsStandardId() {
		return goodsStandardId;
	}

	public void setGoodsStandardId(Integer goodsStandardId) {
		this.goodsStandardId = goodsStandardId;
	}

	public Integer getGoodNumber() {
		return goodNumber;
	}

	public void setGoodNumber(Integer goodNumber) {
		this.goodNumber = goodNumber;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

}
