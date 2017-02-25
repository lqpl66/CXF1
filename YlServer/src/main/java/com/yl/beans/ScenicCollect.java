package com.yl.beans;

import java.io.Serializable;
import java.math.BigDecimal;

public class ScenicCollect implements Serializable {

	/**
	 * 收藏景区
	 */
	private static final long serialVersionUID = 1L;
	private Integer scenicId;
	private String scenicName;
	private String cityName;
	private String provinceName;
	private String coverImg;
	private String intro;
	private BigDecimal price;
	private String fileCode;

	public Integer getScenicId() {
		return scenicId;
	}

	public void setScenicId(Integer scenicId) {
		this.scenicId = scenicId;
	}

	public String getScenicName() {
		return scenicName;
	}

	public void setScenicName(String scenicName) {
		this.scenicName = scenicName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getCoverImg() {
		return coverImg;
	}

	public void setCoverImg(String coverImg) {
		this.coverImg = coverImg;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getFileCode() {
		return fileCode;
	}

	public void setFileCode(String fileCode) {
		this.fileCode = fileCode;
	}

}
