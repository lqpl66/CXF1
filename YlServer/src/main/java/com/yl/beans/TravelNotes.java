package com.yl.beans;

import java.io.Serializable;

public class TravelNotes implements Serializable {
	/**
	 * 游记列表
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer userId;
	private String uuId;
	private String title;
	private String nickName;
	private String provinceName;
	private String cityName;
	private String coverImg;
	private Integer browseCount;
	private String addTime;
	private Integer leavel;
	private Integer totalPraiseNum;
	private Integer isUnderway;
	private Integer state;
	private String fileCode;
	private Integer cityId;
	private Integer provinceId;
	private Integer openLevel;
	private Integer isRecommend;
	private String headImg;
	private String describeInfo;
	

	public String getDescribeInfo() {
		return describeInfo;
	}

	public void setDescribeInfo(String describeInfo) {
		this.describeInfo = describeInfo;
	}

	public String getHeadImg() {
		return headImg;
	}

	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public Integer getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(Integer provinceId) {
		this.provinceId = provinceId;
	}

	public Integer getOpenLevel() {
		return openLevel;
	}

	public void setOpenLevel(Integer openLevel) {
		this.openLevel = openLevel;
	}

	public String getUuId() {
		return uuId;
	}

	public void setUuId(String uuId) {
		this.uuId = uuId;
	}

	public Integer getIsRecommend() {
		return isRecommend;
	}

	public void setIsRecommend(Integer isRecommend) {
		this.isRecommend = isRecommend;
	}

	public String getFileCode() {
		return fileCode;
	}

	public void setFileCode(String fileCode) {
		this.fileCode = fileCode;
	}

	public Integer getIsUnderway() {
		return isUnderway;
	}

	public void setIsUnderway(Integer isUnderway) {
		this.isUnderway = isUnderway;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCoverImg() {
		return coverImg;
	}

	public void setCoverImg(String coverImg) {
		this.coverImg = coverImg;
	}

	public Integer getBrowseCount() {
		return browseCount;
	}

	public void setBrowseCount(Integer browseCount) {
		this.browseCount = browseCount;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public Integer getLeavel() {
		return leavel;
	}

	public void setLeavel(Integer leavel) {
		this.leavel = leavel;
	}

	public Integer getTotalPraiseNum() {
		return totalPraiseNum;
	}

	public void setTotalPraiseNum(Integer totalPraiseNum) {
		this.totalPraiseNum = totalPraiseNum;
	}

}
