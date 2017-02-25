package com.yl.beans;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonMethod;

@JsonAutoDetect(JsonMethod.FIELD)
public class Freight {
	@JsonProperty(value = "AcceptTime")
	private String acceptTime;
	@JsonProperty(value = "AcceptStation")
	private String acceptStation;
	@JsonProperty(value = "Remark")
	private String remark;

	public String getAcceptTime() {
		return acceptTime;
	}

	public void setAcceptTime(String acceptTime) {
		this.acceptTime = acceptTime;
	}

	public String getAcceptStation() {
		return acceptStation;
	}

	public void setAcceptStation(String acceptStation) {
		this.acceptStation = acceptStation;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
