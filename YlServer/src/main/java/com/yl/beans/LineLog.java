package com.yl.beans;

import java.math.BigDecimal;

public class LineLog {

	private Integer lineLogId;
	private Integer linepriceId;
	private Integer userId;
	private Integer attractsId;
	private String attractsName;
	private String scenicId;
	private String scenicName;
	private Integer totalNum;
	private Integer number;
	private Integer remindNum;
	private Integer usedNum;
	private String arriveStartTime;
	private String arriveEndTime;
	private BigDecimal amount;
	private Integer typeId;
	private Integer state;
	private Integer avgTime;
	private Integer lineUserCount;
	private String addTime;

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public Integer getUsedNum() {
		return usedNum;
	}

	public void setUsedNum(Integer usedNum) {
		this.usedNum = usedNum;
	}

	public Integer getLinepriceId() {
		return linepriceId;
	}

	public void setLinepriceId(Integer linepriceId) {
		this.linepriceId = linepriceId;
	}

	public Integer getRemindNum() {
		return remindNum;
	}

	public void setRemindNum(Integer remindNum) {
		this.remindNum = remindNum;
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

	public Integer getLineLogId() {
		return lineLogId;
	}

	public void setLineLogId(Integer lineLogId) {
		this.lineLogId = lineLogId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
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

	public Integer getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(Integer totalNum) {
		this.totalNum = totalNum;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getArriveStartTime() {
		return arriveStartTime;
	}

	public void setArriveStartTime(String arriveStartTime) {
		this.arriveStartTime = arriveStartTime;
	}

	public String getArriveEndTime() {
		return arriveEndTime;
	}

	public void setArriveEndTime(String arriveEndTime) {
		this.arriveEndTime = arriveEndTime;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

}
