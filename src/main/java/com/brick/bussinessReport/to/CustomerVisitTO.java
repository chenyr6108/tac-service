package com.brick.bussinessReport.to;

import java.sql.Date;

public class CustomerVisitTO {
	 
	private String id;
	private String userId;
	private String name;
	private String deptId;
	private String deptName;
	private int caseCount;
	private double casePayMoney;
	private double caseRateDiff;
	private String totalTime;
	private String outsideTime;
	private String order;
	private Date value;
	private String descr;
	private String flag;
	private int ongoingCaseCount;
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public int getCaseCount() {
		return caseCount;
	}
	public void setCaseCount(int caseCount) {
		this.caseCount = caseCount;
	}
	public double getCasePayMoney() {
		return casePayMoney;
	}
	public void setCasePayMoney(double casePayMoney) {
		this.casePayMoney = casePayMoney;
	}
	public double getCaseRateDiff() {
		return caseRateDiff;
	}
	public void setCaseRateDiff(double caseRateDiff) {
		this.caseRateDiff = caseRateDiff;
	}
	public String getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(String totalTime) {
		this.totalTime = totalTime;
	}
	public String getOutsideTime() {
		return outsideTime;
	}
	public void setOutsideTime(String outsideTime) {
		this.outsideTime = outsideTime;
	}
	public Date getValue() {
		return value;
	}
	public void setValue(Date value) {
		this.value = value;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public int getOngoingCaseCount() {
		return ongoingCaseCount;
	}
	public void setOngoingCaseCount(int ongoingCaseCount) {
		this.ongoingCaseCount = ongoingCaseCount;
	}
	
}
