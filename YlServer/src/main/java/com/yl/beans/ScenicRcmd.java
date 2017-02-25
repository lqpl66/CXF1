package com.yl.beans;

import java.io.Serializable;
import java.math.BigDecimal;

public class ScenicRcmd implements Serializable {

	/**
	 * 景区列表详情
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String recommendImg;
	private String coverImg;
	private String planPic;
	private String fileCode;
	private String provinceName;
	private String cityName;
	private String scenicName;
	

	public String getScenicName() {
		return scenicName;
	}

	public void setScenicName(String scenicName) {
		this.scenicName = scenicName;
	}

	public String getFileCode() {
		return fileCode;
	}

	public void setFileCode(String fileCode) {
		this.fileCode = fileCode;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRecommendImg() {
		return recommendImg;
	}

	public void setRecommendImg(String recommendImg) {
		this.recommendImg = recommendImg;
	}

	public String getCoverImg() {
		return coverImg;
	}

	public void setCoverImg(String coverImg) {
		this.coverImg = coverImg;
	}

	public String getPlanPic() {
		return planPic;
	}

	public void setPlanPic(String planPic) {
		this.planPic = planPic;
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

}
