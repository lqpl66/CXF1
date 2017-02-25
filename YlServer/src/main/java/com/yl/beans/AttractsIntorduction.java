package com.yl.beans;

import java.io.Serializable;

public class AttractsIntorduction implements Serializable {
	/**
	 * 景点介绍
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer attractsId;
	private String content;
	private Integer sort;

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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

}
