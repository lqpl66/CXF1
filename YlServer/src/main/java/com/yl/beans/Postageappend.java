package com.yl.beans;

import java.math.BigDecimal;
//运费附加模板
public class Postageappend {
	private Integer postageappendId;
	private Integer shopId;
	private BigDecimal defaultPrice;
	private BigDecimal maxPrice;
	private Integer postageId;
	private Integer provinceId;
	public Integer getPostageappendId() {
		return postageappendId;
	}
	public void setPostageappendId(Integer postageappendId) {
		this.postageappendId = postageappendId;
	}
	public Integer getShopId() {
		return shopId;
	}
	public void setShopId(Integer shopId) {
		this.shopId = shopId;
	}
	public BigDecimal getDefaultPrice() {
		return defaultPrice;
	}
	public void setDefaultPrice(BigDecimal defaultPrice) {
		this.defaultPrice = defaultPrice;
	}
	public BigDecimal getMaxPrice() {
		return maxPrice;
	}
	public void setMaxPrice(BigDecimal maxPrice) {
		this.maxPrice = maxPrice;
	}
	public Integer getPostageId() {
		return postageId;
	}
	public void setPostageId(Integer postageId) {
		this.postageId = postageId;
	}
	public Integer getProvinceId() {
		return provinceId;
	}
	public void setProvinceId(Integer provinceId) {
		this.provinceId = provinceId;
	}
	
}
