package com.yl.beans;

import java.io.Serializable;
import java.math.BigDecimal;

public class AttractsLine implements Serializable {

	/**
	 * 景点排队列表
	 */
	private static final long serialVersionUID = 1L;
	private Integer attractsId;
	private Integer scenicId;
	private String scenicName;
	private String attractsName;
	private Integer totalNum;
	private Integer avgTime;
	private Integer lineUserCount;
	private Integer limitNum;
	private String markedWords;
	
	

	public String getMarkedWords() {
		return markedWords;
	}

	public void setMarkedWords(String markedWords) {
		this.markedWords = markedWords;
	}

	public Integer getLimitNum() {
		return limitNum;
	}

	public void setLimitNum(Integer limitNum) {
		this.limitNum = limitNum;
	}

	public Integer getAttractsId() {
		return attractsId;
	}

	public void setAttractsId(Integer attractsId) {
		this.attractsId = attractsId;
	}

	public Integer getScenicId() {
		return scenicId;
	}

	public void setScenicId(Integer scenicId) {
		this.scenicId = scenicId;
	}

	public String getScenicName() {
		return scenicName;
	}

	public void setScenicName(String scenicName) {
		this.scenicName = scenicName;
	}

	public String getAttractsName() {
		return attractsName;
	}

	public void setAttractsName(String attractsName) {
		this.attractsName = attractsName;
	}

	public Integer getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(Integer totalNum) {
		this.totalNum = totalNum;
	}

	public Integer getAvgTime() {
		return avgTime;
	}

	public void setAvgTime(Integer avgTime) {
		this.avgTime = avgTime;
	}

	public Integer getLineUserCount() {
		return lineUserCount;
	}

	public void setLineUserCount(Integer lineUserCount) {
		this.lineUserCount = lineUserCount;
	}

}
