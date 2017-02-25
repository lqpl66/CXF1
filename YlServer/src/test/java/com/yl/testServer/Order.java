package com.yl.testServer;

public class Order {
	Long orderid;
	String customer;
	String itemname;
	Integer itemcount;

	public Order(Long orderid, String customer, String itemname, Integer itemcount) {
		this.orderid = orderid;
		this.customer = customer;
		this.itemname = itemname;
		this.itemcount = itemcount;
	}

	public Long getOrderid() {
		return orderid;
	}

	public void setOrderid(Long orderid) {
		this.orderid = orderid;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getItemname() {
		return itemname;
	}

	public void setItemname(String itemname) {
		this.itemname = itemname;
	}

	public Integer getItemcount() {
		return itemcount;
	}

	public void setItemcount(Integer itemcount) {
		this.itemcount = itemcount;
	}

}
