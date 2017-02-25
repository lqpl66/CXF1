package com.yl.beans;

import java.math.BigDecimal;

public class LinePriceDetails {

	private Integer linepriceId;
	private Integer lineTimeId;
	private Integer lineDateId;
	private Integer attractsId;
	private String scenicId;
	private BigDecimal price;
	private Integer pollNumber;
	private String startTime;
	private String endTime;
	private String lineDate;

	public Integer getLinepriceId() {
		return linepriceId;
	}

	public void setLinepriceId(Integer linepriceId) {
		this.linepriceId = linepriceId;
	}

	public Integer getLineTimeId() {
		return lineTimeId;
	}

	public void setLineTimeId(Integer lineTimeId) {
		this.lineTimeId = lineTimeId;
	}

	public Integer getLineDateId() {
		return lineDateId;
	}

	public void setLineDateId(Integer lineDateId) {
		this.lineDateId = lineDateId;
	}

	public Integer getAttractsId() {
		return attractsId;
	}

	public void setAttractsId(Integer attractsId) {
		this.attractsId = attractsId;
	}

	public String getScenicId() {
		return scenicId;
	}

	public void setScenicId(String scenicId) {
		this.scenicId = scenicId;
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

	public String getLineDate() {
		return lineDate;
	}

	public void setLineDate(String lineDate) {
		this.lineDate = lineDate;
	}

}
