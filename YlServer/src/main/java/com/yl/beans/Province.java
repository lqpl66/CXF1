package com.yl.beans;

import java.io.Serializable;

public class Province implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer provinceId;

	private String provinceName;

	public Integer getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(Integer provinceId) {
		this.provinceId = provinceId;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

}
