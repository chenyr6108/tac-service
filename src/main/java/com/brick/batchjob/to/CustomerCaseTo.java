package com.brick.batchjob.to;

import java.util.Date;

import com.brick.base.to.BaseTo;
import com.brick.util.DateUtil;

public class CustomerCaseTo extends BaseTo{
	
	private static final long serialVersionUID=1L;
	
	private String custCaseId;
	private String creditId;
	private String caseType;
	private String deptId;
	private String deptName;
	private String custId;
	private String custName;
	private String introducer;
	private String leaseType;
	private String equName;
	private double confirmMoney;
	private double payMoney;
	private Date visitDate;
	private Date giveDate;
	private Date confirmDate;
	private Date expectedDate;
	private Date payDate;
	private Date startDate;
	private String visitDateDescr;
	private String giveDateDescr;
	private String confirmDateDescr;
	private String expectedDateDescr;
	private String payDateDescr;
	private String startDateDescr;
	private String custCode;
	private String userName;
	private String suplName;
	
	public String getCustCaseId() {
		return custCaseId;
	}
	public void setCustCaseId(String custCaseId) {
		this.custCaseId = custCaseId;
	}
	public String getCreditId() {
		return creditId;
	}
	public void setCreditId(String creditId) {
		this.creditId = creditId;
	}
	public String getCaseType() {
		return caseType;
	}
	public void setCaseType(String caseType) {
		this.caseType = caseType;
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
	public String getIntroducer() {
		return introducer;
	}
	public void setIntroducer(String introducer) {
		this.introducer = introducer;
	}
	public String getLeaseType() {
		return leaseType;
	}
	public void setLeaseType(String leaseType) {
		this.leaseType = leaseType;
	}
	public String getEquName() {
		return equName;
	}
	public void setEquName(String equName) {
		this.equName = equName;
	}
	public double getConfirmMoney() {
		return confirmMoney;
	}
	public void setConfirmMoney(double confirmMoney) {
		this.confirmMoney = confirmMoney;
	}
	public double getPayMoney() {
		return payMoney;
	}
	public void setPayMoney(double payMoney) {
		this.payMoney = payMoney;
	}
	public Date getVisitDate() {
		return visitDate;
	}
	public void setVisitDate(Date visitDate) {
		this.visitDate = visitDate;
	}
	public Date getGiveDate() {
		return giveDate;
	}
	public void setGiveDate(Date giveDate) {
		this.giveDate = giveDate;
	}
	public Date getConfirmDate() {
		return confirmDate;
	}
	public void setConfirmDate(Date confirmDate) {
		this.confirmDate = confirmDate;
	}
	public Date getExpectedDate() {
		return expectedDate;
	}
	public void setExpectedDate(Date expectedDate) {
		this.expectedDate = expectedDate;
	}
	public Date getPayDate() {
		return payDate;
	}
	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public String getCustCode() {
		return custCode;
	}
	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getSuplName() {
		return suplName;
	}
	public void setSuplName(String suplName) {
		this.suplName = suplName;
	}
	
	public String getVisitDateDescr() {
		return DateUtil.dateToString(visitDate,"yyyy-MM-dd");
	}
	public void setVisitDateDescr(String visitDateDescr) {
		this.visitDateDescr = visitDateDescr;
	}
	public String getGiveDateDescr() {
		return DateUtil.dateToString(giveDate,"yyyy-MM-dd");
	}
	public void setGiveDateDescr(String giveDateDescr) {
		this.giveDateDescr = giveDateDescr;
	}
	public String getConfirmDateDescr() {
		return DateUtil.dateToString(confirmDate,"yyyy-MM-dd");
	}
	public void setConfirmDateDescr(String confirmDateDescr) {
		this.confirmDateDescr = confirmDateDescr;
	}
	public String getExpectedDateDescr() {
		return DateUtil.dateToString(expectedDate,"yyyy-MM-dd");
	}
	public void setExpectedDateDescr(String expectedDateDescr) {
		this.expectedDateDescr = expectedDateDescr;
	}
	public String getPayDateDescr() {
		return DateUtil.dateToString(payDate,"yyyy-MM-dd");
	}
	public void setPayDateDescr(String payDateDescr) {
		this.payDateDescr = payDateDescr;
	}
	public String getStartDateDescr() {
		return DateUtil.dateToString(startDate,"yyyy-MM-dd");
	}
	public void setStartDateDescr(String startDateDescr) {
		this.startDateDescr = startDateDescr;
	}
	
	
}
