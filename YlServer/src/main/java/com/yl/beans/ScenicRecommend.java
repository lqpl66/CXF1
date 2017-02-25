package com.yl.beans;

import java.io.Serializable;

public class ScenicRecommend implements Serializable {

	/**
	 * 景区推荐
	 */
	private static final long serialVersionUID = 1L;
//	private Integer id;
	private Integer scenicId;
	private Integer recommendId;
	private String recommendName;
	private String recommendSort;
	private String recommendModel;

//	public Integer getId() {
//		return id;
//	}
//
//	public void setId(Integer id) {
//		this.id = id;
//	}

	public Integer getScenicId() {
		return scenicId;
	}

	public void setScenicId(Integer scenicId) {
		this.scenicId = scenicId;
	}

	public Integer getRecommendId() {
		return recommendId;
	}

	public void setRecommendId(Integer recommendId) {
		this.recommendId = recommendId;
	}

	public String getRecommendName() {
		return recommendName;
	}

	public void setRecommendName(String recommendName) {
		this.recommendName = recommendName;
	}

	public String getRecommendSort() {
		return recommendSort;
	}

	public void setRecommendSort(String recommendSort) {
		this.recommendSort = recommendSort;
	}

	public String getRecommendModel() {
		return recommendModel;
	}

	public void setRecommendModel(String recommendModel) {
		this.recommendModel = recommendModel;
	}

}
