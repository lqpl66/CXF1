package com.yl.beans;

//景点路线推荐
public class AttractsRcmdPath {
	private Integer id;
	private Integer scenicId;
	private String attractionsName;
	private Integer marginTop;
	private Integer marginLeft;
	private Integer sortId;
	private String attractionsUrl;

	public String getAttractionsUrl() {
		return attractionsUrl;
	}

	public void setAttractionsUrl(String attractionsUrl) {
		this.attractionsUrl = attractionsUrl;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getScenicId() {
		return scenicId;
	}

	public void setScenicId(Integer scenicId) {
		this.scenicId = scenicId;
	}

	public String getAttractionsName() {
		return attractionsName;
	}

	public void setAttractionsName(String attractionsName) {
		this.attractionsName = attractionsName;
	}

	public Integer getMarginTop() {
		return marginTop;
	}

	public void setMarginTop(Integer marginTop) {
		this.marginTop = marginTop;
	}

	public Integer getMarginLeft() {
		return marginLeft;
	}

	public void setMarginLeft(Integer marginLeft) {
		this.marginLeft = marginLeft;
	}

	public Integer getSortId() {
		return sortId;
	}

	public void setSortId(Integer sortId) {
		this.sortId = sortId;
	}

}
