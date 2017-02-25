package com.yl.beans;

import java.math.BigDecimal;

public class PresentApplication {
	private String expenseNo;
	private Integer accountId;
	private BigDecimal amount;
	private Integer status;
	private Integer operateType;
	private Integer operateId;

	public String getExpenseNo() {
		return expenseNo;
	}

	public void setExpenseNo(String expenseNo) {
		this.expenseNo = expenseNo;
	}


	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getOperateType() {
		return operateType;
	}

	public void setOperateType(Integer operateType) {
		this.operateType = operateType;
	}

	public Integer getOperateId() {
		return operateId;
	}

	public void setOperateId(Integer operateId) {
		this.operateId = operateId;
	}

}
