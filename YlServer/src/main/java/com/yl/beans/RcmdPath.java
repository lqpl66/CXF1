package com.yl.beans;

import java.io.Serializable;

public class RcmdPath implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer scenicId;
	private String attractionsId;
	private String attractionsName;
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

	public String getAttractionsId() {
		return attractionsId;
	}

	public void setAttractionsId(String attractionsId) {
		this.attractionsId = attractionsId;
	}

	public String getAttractionsName() {
		return attractionsName;
	}

	public void setAttractionsName(String attractionsName) {
		this.attractionsName = attractionsName;
	}

}
