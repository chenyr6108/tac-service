package com.brick.dun.to;

import java.util.Date;



import com.brick.base.to.BaseTo;

public class DunTaskEveryTo extends BaseTo {
	
	private static final long serialVersionUID = 1L;
	private String area; //区域
	private String manager;//经办人
	private String leaseCode;//合同编号
	private String custName;//承租人
	private Integer leasePeriod;//总期数
	private Integer period;//已缴期数
	private Integer dunDay;//逾期天数
	private String callUser;//催收人员
	private Date callTime;//催收时间
	private String callResult;//催收结果
	private java.sql.Date visitDate;//回访时间
	
	private String visitUser;//回访人员
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getManager() {
		return manager;
	}
	public void setManager(String manager) {
		this.manager = manager;
	}
	public String getLeaseCode() {
		return leaseCode;
	}
	public void setLeaseCode(String leaseCode) {
		this.leaseCode = leaseCode;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public Integer getLeasePeriod() {
		return leasePeriod;
	}
	public void setLeasePeriod(Integer leasePeriod) {
		this.leasePeriod = leasePeriod;
	}
	public Integer getPeriod() {
		return period;
	}
	public void setPeriod(Integer period) {
		this.period = period;
	}
	public Integer getDunDay() {
		return dunDay;
	}
	public void setDunDay(Integer dunDay) {
		this.dunDay = dunDay;
	}
	public String getCallUser() {
		return callUser;
	}
	public void setCallUser(String callUser) {
		this.callUser = callUser;
	}
	public Date getCallTime() {
		return callTime;
	}
	public void setCallTime(Date callTime) {
		this.callTime = callTime;
	}
	public String getCallResult() {
		return callResult;
	}
	public void setCallResult(String callResult) {
		this.callResult = callResult;
	}
	
	public String getVisitUser() {
		return visitUser;
	}
	public void setVisitUser(String visitUser) {
		this.visitUser = visitUser;
	}
	public java.sql.Date getVisitDate() {
		return visitDate;
	}
	public void setVisitDate(java.sql.Date visitDate) {
		this.visitDate = visitDate;
	}
}
