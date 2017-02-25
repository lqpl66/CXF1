package com.yl.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class Evaluate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;

	private Integer userId;

	private Integer fk;

	private BigDecimal score;

	private String content;
	private String model;
	private String addTime;
	private String nickName;
	private String headimg;
	private Integer goodsStandardId;
	private String orderNo;
	private String evaluateImgList;
	private Integer goodsOrderExpandId;
	private List<EvaluateImg>  evaluateImglist;
	
	public Integer getGoodsOrderExpandId() {
		return goodsOrderExpandId;
	}

	public void setGoodsOrderExpandId(Integer goodsOrderExpandId) {
		this.goodsOrderExpandId = goodsOrderExpandId;
	}

	

	public List<EvaluateImg> getEvaluateImglist() {
		return evaluateImglist;
	}

	public void setEvaluateImglist(List<EvaluateImg> evaluateImglist) {
		this.evaluateImglist = evaluateImglist;
	}

	public Integer getGoodsStandardId() {
		return goodsStandardId;
	}

	public void setGoodsStandardId(Integer goodsStandardId) {
		this.goodsStandardId = goodsStandardId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getEvaluateImgList() {
		return evaluateImgList;
	}

	public void setEvaluateImgList(String evaluateImgList) {
		this.evaluateImgList = evaluateImgList;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getFk() {
		return fk;
	}

	public void setFk(Integer fk) {
		this.fk = fk;
	}

	public BigDecimal getScore() {
		return score;
	}

	public void setScore(BigDecimal score) {
		this.score = score;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getHeadimg() {
		return headimg;
	}

	public void setHeadimg(String headimg) {
		this.headimg = headimg;
	}

	@Override
	public String toString() {
		return "Evaluate [id=" + id + ", userId=" + userId + ", fk=" + fk + ", score=" + score + ", content=" + content
				+ ", model=" + model + ", addTime=" + addTime + ", nickName=" + nickName + ", headimg=" + headimg
				+ ", goodsStandardId=" + goodsStandardId + ", orderNo=" + orderNo + ", evaluateImgList="
				+ evaluateImgList + ", evaluateImglist=" + evaluateImglist + "]";
	}

}
