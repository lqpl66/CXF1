package com.yl.beans;

import java.io.Serializable;
import java.math.BigDecimal;

public class ScenicHome implements Serializable {

	/**
	 * 景区详情
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String scenicName;
	private String cityName;
	private String provinceName;
	private String address;
	private String planPic;
	private String coverImg;
	private String openTime;
	private String intro;
	private String recommendImg;
	private BigDecimal averageScore;
	private BigDecimal price;
	private Integer evaluateCount;
	private Integer collectCount;
	private BigDecimal placeY;
	private BigDecimal placeX;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getScenicName() {
		return scenicName;
	}

	public void setScenicName(String scenicName) {
		this.scenicName = scenicName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPlanPic() {
		return planPic;
	}

	public void setPlanPic(String planPic) {
		this.planPic = planPic;
	}

	public String getCoverImg() {
		return coverImg;
	}

	public void setCoverImg(String coverImg) {
		this.coverImg = coverImg;
	}

	public String getOpenTime() {
		return openTime;
	}

	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public BigDecimal getAverageScore() {
		return averageScore;
	}

	public void setAverageScore(BigDecimal averageScore) {
		this.averageScore = averageScore;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getEvaluateCount() {
		return evaluateCount;
	}

	public void setEvaluateCount(Integer evaluateCount) {
		this.evaluateCount = evaluateCount;
	}

	public Integer getCollectCount() {
		return collectCount;
	}

	public void setCollectCount(Integer collectCount) {
		this.collectCount = collectCount;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
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

	public String getRecommendImg() {
		return recommendImg;
	}

	public void setRecommendImg(String recommendImg) {
		this.recommendImg = recommendImg;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}
	

}
