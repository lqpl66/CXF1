package com.yl.beans;

import java.math.BigDecimal;

public class GoodsExpandDetail {
	private Integer id;
	private Integer goodId;
	private Integer goodNumber;
	private Integer goodsStandardId;
	private Integer evaluateId;
	private String goodsstandardName;
	private String orderNo;
	private String fileCode;
	private String intro;
	private String imgUrl;
	private String goodName;
	private BigDecimal price;
	private Integer isDel;
	private Integer isSell;
	private Integer workState;

	public Integer getIsDel() {
		return isDel;
	}

	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}

	public Integer getIsSell() {
		return isSell;
	}

	public void setIsSell(Integer isSell) {
		this.isSell = isSell;
	}

	public Integer getWorkState() {
		return workState;
	}

	public void setWorkState(Integer workState) {
		this.workState = workState;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getEvaluateId() {
		return evaluateId;
	}

	public void setEvaluateId(Integer evaluateId) {
		this.evaluateId = evaluateId;
	}

	public Integer getGoodId() {
		return goodId;
	}

	public void setGoodId(Integer goodId) {
		this.goodId = goodId;
	}

	public Integer getGoodsStandardId() {
		return goodsStandardId;
	}

	public void setGoodsStandardId(Integer goodsStandardId) {
		this.goodsStandardId = goodsStandardId;
	}

	public Integer getGoodNumber() {
		return goodNumber;
	}

	public void setGoodNumber(Integer goodNumber) {
		this.goodNumber = goodNumber;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getFileCode() {
		return fileCode;
	}

	public void setFileCode(String fileCode) {
		this.fileCode = fileCode;
	}

	public String getGoodsstandardName() {
		return goodsstandardName;
	}

	public void setGoodsstandardName(String goodsstandardName) {
		this.goodsstandardName = goodsstandardName;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getGoodName() {
		return goodName;
	}

	public void setGoodName(String goodName) {
		this.goodName = goodName;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

}
