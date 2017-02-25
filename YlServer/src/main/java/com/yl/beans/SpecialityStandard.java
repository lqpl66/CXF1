package com.yl.beans;

import java.math.BigDecimal;

public class SpecialityStandard {

	private Integer id;
	private Integer goodId;
	private String name;
	private BigDecimal marketPrice;
	private BigDecimal price;
	private Integer inventory;
	private Integer sellNumber;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getMarketPrice() {
		return marketPrice;
	}

	public void setMarketPrice(BigDecimal marketPrice) {
		this.marketPrice = marketPrice;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getInventory() {
		return inventory;
	}

	public void setInventory(Integer inventory) {
		this.inventory = inventory;
	}

	public Integer getSellNumber() {
		return sellNumber;
	}

	public void setSellNumber(Integer sellNumber) {
		this.sellNumber = sellNumber;
	}

}
