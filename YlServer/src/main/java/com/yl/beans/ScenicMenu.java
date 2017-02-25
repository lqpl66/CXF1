package com.yl.beans;

import java.io.Serializable;

public class ScenicMenu implements Serializable {

	/**
	 * 景区菜单
	 */
	private static final long serialVersionUID = 1L;
	private Integer scenicId;
	private Integer menuId;
	private String menuName;
	private String menuModel;
	private Integer menuSort;

	public Integer getScenicId() {
		return scenicId;
	}

	public void setScenicId(Integer scenicId) {
		this.scenicId = scenicId;
	}

	public Integer getMenuId() {
		return menuId;
	}

	public void setMenuId(Integer menuId) {
		this.menuId = menuId;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public String getMenuModel() {
		return menuModel;
	}

	public void setMenuModel(String menuModel) {
		this.menuModel = menuModel;
	}

	public Integer getMenuSort() {
		return menuSort;
	}

	public void setMenuSort(Integer menuSort) {
		this.menuSort = menuSort;
	}

}
