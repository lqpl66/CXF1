package com.yl.beans;

import java.io.Serializable;

public class AttractsImg implements Serializable {
	/**
	 * 景点轮播图片
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer attractsId;
	private String imgUrl;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getAttractsId() {
		return attractsId;
	}

	public void setAttractsId(Integer attractsId) {
		this.attractsId = attractsId;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

}
