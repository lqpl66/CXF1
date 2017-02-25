package com.yl.beans;

import java.math.BigDecimal;

public class LinePrice {
	private Integer linepriceId;
	private Integer lineTimeId;
	private Integer attractsId;
	private String attractsName;
	private String scenicId;
	private String scenicName;
	private BigDecimal price;
	private Integer pollNumber;
	private Integer limitNum;
	private String startTime;
	private String lineDate;
	private String endTime;
	private Integer isBuy;

	public String getLineDate() {
		return lineDate;
	}

	public void setLineDate(String lineDate) {
		this.lineDate = lineDate;
	}

	public Integer getIsBuy() {
		return isBuy;
	}

	public void setIsBuy(Integer isBuy) {
		this.isBuy = isBuy;
	}

	public Integer getLinepriceId() {
		return linepriceId;
	}

	public void setLinepriceId(Integer linepriceId) {
		this.linepriceId = linepriceId;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public Integer getLimitNum() {
		return limitNum;
	}

	public void setLimitNum(Integer limitNum) {
		this.limitNum = limitNum;
	}

	public Integer getLineTimeId() {
		return lineTimeId;
	}

	public void setLineTimeId(Integer lineTimeId) {
		this.lineTimeId = lineTimeId;
	}

	public Integer getAttractsId() {
		return attractsId;
	}

	public void setAttractsId(Integer attractsId) {
		this.attractsId = attractsId;
	}

	public String getAttractsName() {
		return attractsName;
	}

	public void setAttractsName(String attractsName) {
		this.attractsName = attractsName;
	}

	public String getScenicId() {
		return scenicId;
	}

	public void setScenicId(String scenicId) {
		this.scenicId = scenicId;
	}

	public String getScenicName() {
		return scenicName;
	}

	public void setScenicName(String scenicName) {
		this.scenicName = scenicName;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getPollNumber() {
		return pollNumber;
	}

	public void setPollNumber(Integer pollNumber) {
		this.pollNumber = pollNumber;
	}

}
