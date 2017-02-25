package com.yl.beans;

import java.io.Serializable;
import java.math.BigDecimal;

public class ShopNearForGoods implements Serializable {

	/**
	 * 用于附近店铺查询所属的商品列表
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer shopNo;
	private String shopName;
	private BigDecimal placeY;
	private BigDecimal placeX;
	private Integer isRecommend;
	private Integer isSell;
	private Integer workState;

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

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIsRecommend() {
		return isRecommend;
	}

	public void setIsRecommend(Integer isRecommend) {
		this.isRecommend = isRecommend;
	}

	public Integer getIsSell() {
		return isSell;
	}

	public void setIsSell(Integer isSell) {
		this.isSell = isSell;
	}

	public Integer getWorkState() {
		return workState;
	}

	public void setWorkState(Integer workState) {
		this.workState = workState;
	}

}
