package com.yl.beans;

import java.io.Serializable;

public class ScenicImg implements Serializable {

	/**
	 *景区轮播图片
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String imgUrl;
	private Integer scenicId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public Integer getScenicId() {
		return scenicId;
	}

	public void setScenicId(Integer scenicId) {
		this.scenicId = scenicId;
	}

}
