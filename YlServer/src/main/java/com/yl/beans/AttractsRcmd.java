package com.yl.beans;

import java.io.Serializable;

public class AttractsRcmd implements Serializable {

	/**
	 * 景点推荐列表
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer scenicId;
	private String attractionsName;
	private String attractsImgUrl;
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

	public String getAttractsImgUrl() {
		return attractsImgUrl;
	}

	public void setAttractsImgUrl(String attractsImgUrl) {
		this.attractsImgUrl = attractsImgUrl;
	}

}
