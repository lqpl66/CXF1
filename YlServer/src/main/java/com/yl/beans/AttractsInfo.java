package com.yl.beans;

import java.io.Serializable;
import java.math.BigDecimal;

public class AttractsInfo implements Serializable {

	/**
	 * 景点详情
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer scenicId;
	private String attractionsName;
	private String attractsImgList;
	private String attractsIntorductionList;
	private String fileCode;
	private String scenicFileCode;
//	private String lineLog;
//	private String aline;
//	private String vipLineList;
//	private String vipLineLogList;


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

	public String getAttractsImgList() {
		return attractsImgList;
	}

	public void setAttractsImgList(String attractsImgList) {
		this.attractsImgList = attractsImgList;
	}

	public String getAttractsIntorductionList() {
		return attractsIntorductionList;
	}

	public void setAttractsIntorductionList(String attractsIntorductionList) {
		this.attractsIntorductionList = attractsIntorductionList;
	}

}
