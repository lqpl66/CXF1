package com.yl.beans;

import java.io.Serializable;
import java.math.BigDecimal;

public class TravelNotesInfo implements Serializable {
	/**
	 * 游记详情
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer travelId;
	private String imgUrl;
	private String maximgUrl;
	private String describeInfo;
	private Integer praiseNum;
	private String placeName;
	private String addTime;
	private String crateDate;
	private String isPraise;
	private Integer userId;
	private Integer H;
	private Integer W;
	private BigDecimal placeX;
	private BigDecimal placeY;

	public BigDecimal getPlaceX() {
		return placeX;
	}

	public void setPlaceX(BigDecimal placeX) {
		this.placeX = placeX;
	}

	public BigDecimal getPlaceY() {
		return placeY;
	}

	public void setPlaceY(BigDecimal placeY) {
		this.placeY = placeY;
	}

	public String getMaximgUrl() {
		return maximgUrl;
	}

	public void setMaximgUrl(String maximgUrl) {
		this.maximgUrl = maximgUrl;
	}

	public Integer getH() {
		return H;
	}

	public void setH(Integer h) {
		H = h;
	}

	public Integer getW() {
		return W;
	}

	public void setW(Integer w) {
		W = w;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getIsPraise() {
		return isPraise;
	}

	public void setIsPraise(String isPraise) {
		this.isPraise = isPraise;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getTravelId() {
		return travelId;
	}

	public void setTravelId(Integer travelId) {
		this.travelId = travelId;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getDescribeInfo() {
		return describeInfo;
	}

	public void setDescribeInfo(String describeInfo) {
		this.describeInfo = describeInfo;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getCrateDate() {
		return crateDate;
	}

	public void setCrateDate(String crateDate) {
		this.crateDate = crateDate;
	}

	public Integer getPraiseNum() {
		return praiseNum;
	}

	public void setPraiseNum(Integer praiseNum) {
		this.praiseNum = praiseNum;
	}

	public String getPlaceName() {
		return placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

}
