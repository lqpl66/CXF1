package com.yl.beans;

import java.math.BigDecimal;

public class Speciality {

	private Integer id;
	private String imgUrl;
	private String goodName;
	private BigDecimal price;
	private Integer lEvaluateNum;
	private Integer mEvaluateNum;
	private Integer sEvaluateNum;
	private Integer totalEvaluateNum;
	private Integer goodEvaluateCate;
	private String fileCode;
	private String shopFileCode;
	private String intro;
	private Integer inventory;
	private Integer goodsstandardId;
	private String goodsstandardName;
	

	public Integer getInventory() {
		return inventory;
	}

	public void setInventory(Integer inventory) {
		this.inventory = inventory;
	}

	public Integer getGoodsstandardId() {
		return goodsstandardId;
	}

	public void setGoodsstandardId(Integer goodsstandardId) {
		this.goodsstandardId = goodsstandardId;
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

	public Integer getGoodEvaluateCate() {
		return goodEvaluateCate;
	}

	public void setGoodEvaluateCate(Integer goodEvaluateCate) {
		this.goodEvaluateCate = goodEvaluateCate;
	}

	public Integer getlEvaluateNum() {
		return lEvaluateNum;
	}

	public void setlEvaluateNum(Integer lEvaluateNum) {
		this.lEvaluateNum = lEvaluateNum;
	}

	public Integer getmEvaluateNum() {
		return mEvaluateNum;
	}

	public void setmEvaluateNum(Integer mEvaluateNum) {
		this.mEvaluateNum = mEvaluateNum;
	}

	public Integer getsEvaluateNum() {
		return sEvaluateNum;
	}

	public void setsEvaluateNum(Integer sEvaluateNum) {
		this.sEvaluateNum = sEvaluateNum;
	}

	public Integer getTotalEvaluateNum() {
		return totalEvaluateNum;
	}

	public void setTotalEvaluateNum(Integer totalEvaluateNum) {
		this.totalEvaluateNum = totalEvaluateNum;
	}

	public String getFileCode() {
		return fileCode;
	}

	public void setFileCode(String fileCode) {
		this.fileCode = fileCode;
	}

	public String getShopFileCode() {
		return shopFileCode;
	}

	public void setShopFileCode(String shopFileCode) {
		this.shopFileCode = shopFileCode;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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
