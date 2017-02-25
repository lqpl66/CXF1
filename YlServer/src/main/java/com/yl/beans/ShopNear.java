package com.yl.beans;

import java.io.Serializable;
import java.math.BigDecimal;

public class ShopNear implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer shopNo;
	private String shopName;
	private BigDecimal placeY;
	private BigDecimal placeX;
//	private String type;
//
//	public String getType() {
//		return type;
//	}
//
//	public void setType(String type) {
//		this.type = type;
//	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getShopNo() {
		return shopNo;
	}

	public void setShopNo(Integer shopNo) {
		this.shopNo = shopNo;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public BigDecimal getPlaceY() {
		return placeY;
	}

	public void setPlaceY(BigDecimal placeY) {
		this.placeY = placeY;
	}

	public BigDecimal getPlaceX() {
		return placeX;
	}

	public void setPlaceX(BigDecimal placeX) {
		this.placeX = placeX;
	}

}
