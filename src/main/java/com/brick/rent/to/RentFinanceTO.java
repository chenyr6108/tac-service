package com.brick.rent.to;

import java.util.Date;

public class RentFinanceTO {

	private int incomeId;//来款表主键
	private Date incomeDate;//来款时间
	private String incomeDateDescr;//来款时间String类型
	private String incomeName;//来款户名
	private double incomeMoney;//来款金额
	private String incomeNameTrue;//系统匹配客户名称
	private String incomeAccount;//来款帐号
	private String virtualAccount;//虚拟帐号
	private String receiptName;//收款单位
	private String receiptAccount;//收款帐号
	private String dealWay;//交易方式
	private String uploadName;//上传来款人员
	private String remark;//备注
	private Date uploadTime;//上传时间
	
	public int getIncomeId() {
		return incomeId;
	}
	public void setIncomeId(int incomeId) {
		this.incomeId = incomeId;
	}
	public Date getIncomeDate() {
		return incomeDate;
	}
	public void setIncomeDate(Date incomeDate) {
		this.incomeDate = incomeDate;
	}
	public String getIncomeDateDescr() {
		return incomeDateDescr;
	}
	public void setIncomeDateDescr(String incomeDateDescr) {
		this.incomeDateDescr = incomeDateDescr;
	}
	public String getIncomeName() {
		return incomeName;
	}
	public void setIncomeName(String incomeName) {
		this.incomeName = incomeName;
	}
	public double getIncomeMoney() {
		return incomeMoney;
	}
	public void setIncomeMoney(double incomeMoney) {
		this.incomeMoney = incomeMoney;
	}
	public String getIncomeNameTrue() {
		return incomeNameTrue;
	}
	public void setIncomeNameTrue(String incomeNameTrue) {
		this.incomeNameTrue = incomeNameTrue;
	}
	public String getIncomeAccount() {
		return incomeAccount;
	}
	public void setIncomeAccount(String incomeAccount) {
		this.incomeAccount = incomeAccount;
	}
	public String getVirtualAccount() {
		return virtualAccount;
	}
	public void setVirtualAccount(String virtualAccount) {
		this.virtualAccount = virtualAccount;
	}
	public String getReceiptName() {
		return receiptName;
	}
	public void setReceiptName(String receiptName) {
		this.receiptName = receiptName;
	}
	public String getReceiptAccount() {
		return receiptAccount;
	}
	public void setReceiptAccount(String receiptAccount) {
		this.receiptAccount = receiptAccount;
	}
	public String getDealWay() {
		return dealWay;
	}
	public void setDealWay(String dealWay) {
		this.dealWay = dealWay;
	}
	public String getUploadName() {
		return uploadName;
	}
	public void setUploadName(String uploadName) {
		this.uploadName = uploadName;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Date getUploadTime() {
		return uploadTime;
	}
	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}
	
}
