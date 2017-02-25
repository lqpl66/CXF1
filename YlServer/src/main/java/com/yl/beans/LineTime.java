package com.yl.beans;

public class LineTime {

	private Integer lineTimeId;
	private Integer lineDateId;
	private String scenicId;
	private String startTime;
	private String endTime;
	private Integer type;
	private String lineDate;

	public Integer getLineTimeId() {
		return lineTimeId;
	}

	public void setLineTimeId(Integer lineTimeId) {
		this.lineTimeId = lineTimeId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getLineDateId() {
		return lineDateId;
	}

	public void setLineDateId(Integer lineDateId) {
		this.lineDateId = lineDateId;
	}

	public String getScenicId() {
		return scenicId;
	}

	public void setScenicId(String scenicId) {
		this.scenicId = scenicId;
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
