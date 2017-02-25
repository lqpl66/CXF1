package com.yl.beans;

import java.io.Serializable;

//景点路线导览
public class AttractsPathGuide implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer scenicId;
	private String attractionsName;
	private Integer marginTop;
	private Integer marginLeft;
	private String fileFormat;
	private String fileUrl;
	private String fileCode;

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

	public String getFileFormat() {
		return fileFormat;
	}

	public void setFileFormat(String fileFormat) {
		this.fileFormat = fileFormat;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

}
