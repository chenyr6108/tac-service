package com.brick.backMoney.to;

import com.brick.base.to.BaseTo;

public class PayMoneyTO extends BaseTo{

	private static final long serialVersionUID = 1L;

	private String creditRunCode;
	private String suplName;
	private String custName;
	private String equipment;
	private double payMoney;
	private String expectedDate;
	private String payDate;
	
	public String getCreditRunCode() {
		return creditRunCode;
	}
	public void setCreditRunCode(String creditRunCode) {
		this.creditRunCode = creditRunCode;
	}
	public String getSuplName() {
		return suplName;
	}
	public void setSuplName(String suplName) {
		this.suplName = suplName;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getEquipment() {
		return equipment;
	}
	public void setEquipment(String equipment) {
		this.equipment = equipment;
	}
	public double getPayMoney() {
		return payMoney;
	}
	public void setPayMoney(double payMoney) {
		this.payMoney = payMoney;
	}
	public String getExpectedDate() {
		return expectedDate;
	}
	public void setExpectedDate(String expectedDate) {
		this.expectedDate = expectedDate;
	}
	public String getPayDate() {
		return payDate;
	}
	public void setPayDate(String payDate) {
		this.payDate = payDate;
	}
}
