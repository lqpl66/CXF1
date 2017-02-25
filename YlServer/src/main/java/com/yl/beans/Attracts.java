package com.yl.beans;

import java.io.Serializable;
import java.math.BigDecimal;

public class Attracts implements Serializable {

	/**
	 * 景点列表
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer scenicId;
	private String attractionsName;
	private String attractsImgUrl;
	private String fileUrl;
	private String fileFormat;
	private BigDecimal price;
	private String fileCode;
	private String scenicFileCode;

	
	public String getFileCode() {
		return fileCode;
	}

	public void setFileCode(String fileCode) {
		this.fileCode = fileCode;
	}

	public String getScenicFileCode() {
		return scenicFileCode;
	}

	public void setScenicFileCode(String scenicFileCode) {
		this.scenicFileCode = scenicFileCode;
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

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public String getFileFormat() {
		return fileFormat;
	}

	public void setFileFormat(String fileFormat) {
		this.fileFormat = fileFormat;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

}
