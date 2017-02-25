package com.yl.beans;

import java.io.Serializable;
import java.math.BigDecimal;

public class Shop implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer shopNo;
	private String shopName;
	private Integer workState;
	private Integer provinceId;
	private Integer cityId;
	private String provinceName;
	private String cityName;
	private String address;
	private String linkManName;
	private String linkManMobile;
	private String openTime;
	private String goodsType;
	private String express;
	private String intro;
	private String fileCode;
	private String logo;
	private String totalSpcialityNum;
	private String totalCollectNum;
	private Integer isCollect;

	public Integer getIsCollect() {
		return isCollect;
	}

	public void setIsCollect(Integer isCollect) {
		this.isCollect = isCollect;
	}

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

	public Integer getWorkState() {
		return workState;
	}

	public void setWorkState(Integer workState) {
		this.workState = workState;
	}

	public Integer getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(Integer provinceId) {
		this.provinceId = provinceId;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
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

	public String getLinkManName() {
		return linkManName;
	}

	public void setLinkManName(String linkManName) {
		this.linkManName = linkManName;
	}

	public String getLinkManMobile() {
		return linkManMobile;
	}

	public void setLinkManMobile(String linkManMobile) {
		this.linkManMobile = linkManMobile;
	}

	public String getOpenTime() {
		return openTime;
	}

	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}

	public String getGoodsType() {
		return goodsType;
	}

	public void setGoodsType(String goodsType) {
		this.goodsType = goodsType;
	}

	public String getExpress() {
		return express;
	}

	public void setExpress(String express) {
		this.express = express;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getFileCode() {
		return fileCode;
	}

	public void setFileCode(String fileCode) {
		this.fileCode = fileCode;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getTotalSpcialityNum() {
		return totalSpcialityNum;
	}

	public void setTotalSpcialityNum(String totalSpcialityNum) {
		this.totalSpcialityNum = totalSpcialityNum;
	}

	public String getTotalCollectNum() {
		return totalCollectNum;
	}

	public void setTotalCollectNum(String totalCollectNum) {
		this.totalCollectNum = totalCollectNum;
	}

}
