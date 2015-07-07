package com.brick.batchjob.to;

import com.brick.base.to.BaseTo;

public class SuplCustCaseTo extends BaseTo {

	private static final long serialVersionUID=1L;

	private String suplCustCaseId;
	private String creditId;
	private String suplName;
	private String custId;
	private String custName;
	private String leaseCode;
	private double payMoney;
	private String totalPeriod;
	private String payPeriod;
	private double dunMoney;
	private String rectId;
	private String recpId;
	private String caseType;
	public String getSuplCustCaseId() {
		return suplCustCaseId;
	}
	public void setSuplCustCaseId(String suplCustCaseId) {
		this.suplCustCaseId = suplCustCaseId;
	}
	public String getCreditId() {
		return creditId;
	}
	public void setCreditId(String creditId) {
		this.creditId = creditId;
	}
	public String getSuplName() {
		return suplName;
	}
	public void setSuplName(String suplName) {
		this.suplName = suplName;
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getLeaseCode() {
		return leaseCode;
	}
	public void setLeaseCode(String leaseCode) {
		this.leaseCode = leaseCode;
	}
	public double getPayMoney() {
		return payMoney;
	}
	public void setPayMoney(double payMoney) {
		this.payMoney = payMoney;
	}
	public String getTotalPeriod() {
		return totalPeriod;
	}
	public void setTotalPeriod(String totalPeriod) {
		this.totalPeriod = totalPeriod;
	}
	public String getPayPeriod() {
		return payPeriod;
	}
	public void setPayPeriod(String payPeriod) {
		this.payPeriod = payPeriod;
	}
	public double getDunMoney() {
		return dunMoney;
	}
	public void setDunMoney(double dunMoney) {
		this.dunMoney = dunMoney;
	}
	public String getRectId() {
		return rectId;
	}
	public void setRectId(String rectId) {
		this.rectId = rectId;
	}
	public String getRecpId() {
		return recpId;
	}
	public void setRecpId(String recpId) {
		this.recpId = recpId;
	}
	public String getCaseType() {
		return caseType;
	}
	public void setCaseType(String caseType) {
		this.caseType = caseType;
	}
	
}
