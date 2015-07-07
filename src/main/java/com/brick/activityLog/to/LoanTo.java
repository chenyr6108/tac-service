package com.brick.activityLog.to;

import java.math.BigDecimal;
import java.sql.Date;

import com.brick.base.to.BaseTo;

public class LoanTo extends BaseTo {

	private static final long serialVersionUID = 1L;

	private String loanId;//信贷ID
	private String custCode;
	private String custCodeDescr;//客户名称
	private String introducer;//介绍人
	private String supCode;
	private String supName;//供应商名称
	private String loanMode;
	private String loanModeDescr;//租赁方式
	private String loanMemo;//备注
	private BigDecimal costMoney;//成本
	private BigDecimal cautionMoney;//保证金
	private String caseStatusId;//案件ID
	private String caseStatusDescr;//案件状况
	private String userId;//员工ID
	private String userName;//经办人
	private String deptId;//部门ID
	private BigDecimal approveMoney;//核准额度
	private BigDecimal payMoney;//拨款额度    此栏位是计算业绩的栏位
	private String firstAccessDescr;//首次访厂日
	private String giveDateDescr;//送件日
	private String approveDateDescr;//核准日期
	private String expDateDescr;//预估拨款日
	private String payDateDescr;//拨款日期
	private String startDateDescr;//起租日期
	private String status;//状态  0可用  -1不可用
	private String createBy;//创建人
	private String createOnDescr;//创建时间
	private String lastUpdateBy;//最后更新的人
	private String lastUpdateOnDescr;//最后更新时间
	private BigDecimal accrual;//总利息
	private BigDecimal originalMoney;//本金
	private int month;
	private String year;
	private int count;
	private int payCloseStatus;
	private Date payCloseDate;
	private String payCloseDateDescr;
	private String loanCode;
	
	private double target;
	private double achievePer;
	private double limitMonth;
	
	public String getPayCloseDateDescr() {
		return payCloseDateDescr;
	}
	public void setPayCloseDateDescr(String payCloseDateDescr) {
		this.payCloseDateDescr = payCloseDateDescr;
	}
	private String remark;
	public String getLoanId() {
		return loanId;
	}
	public void setLoanId(String loanId) {
		this.loanId = loanId;
	}
	public String getCustCode() {
		return custCode;
	}
	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}
	public String getCustCodeDescr() {
		return custCodeDescr;
	}
	public void setCustCodeDescr(String custCodeDescr) {
		this.custCodeDescr = custCodeDescr;
	}
	public String getIntroducer() {
		if(introducer==null||"".equals(introducer)) {
			return "无";
		}
		return introducer;
	}
	public void setIntroducer(String introducer) {
		this.introducer = introducer;
	}
	public String getSupCode() {
		return supCode;
	}
	public void setSupCode(String supCode) {
		this.supCode = supCode;
	}
	public String getSupName() {
		if(supName==null||"".equals(supName)) {
			return "无";
		}
		return supName;
	}
	public void setSupName(String supName) {
		this.supName = supName;
	}
	public String getLoanMode() {
		return loanMode;
	}
	public void setLoanMode(String loanMode) {
		this.loanMode = loanMode;
	}
	public String getLoanModeDescr() {
		return loanModeDescr;
	}
	public void setLoanModeDescr(String loanModeDescr) {
		this.loanModeDescr = loanModeDescr;
	}
	public String getLoanMemo() {
		return loanMemo;
	}
	public void setLoanMemo(String loanMemo) {
		this.loanMemo = loanMemo;
	}
	public BigDecimal getCostMoney() {
		return costMoney;
	}
	public void setCostMoney(BigDecimal costMoney) {
		this.costMoney = costMoney;
	}
	public BigDecimal getCautionMoney() {
		return cautionMoney;
	}
	public void setCautionMoney(BigDecimal cautionMoney) {
		this.cautionMoney = cautionMoney;
	}
	public String getCaseStatusId() {
		return caseStatusId;
	}
	public void setCaseStatusId(String caseStatusId) {
		this.caseStatusId = caseStatusId;
	}
	public String getCaseStatusDescr() {
		return caseStatusDescr;
	}
	public void setCaseStatusDescr(String caseStatusDescr) {
		this.caseStatusDescr = caseStatusDescr;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
	public BigDecimal getApproveMoney() {
		return approveMoney;
	}
	public void setApproveMoney(BigDecimal approveMoney) {
		this.approveMoney = approveMoney;
	}
	public BigDecimal getPayMoney() {
		return payMoney;
	}
	public void setPayMoney(BigDecimal payMoney) {
		this.payMoney = payMoney;
	}
	public String getFirstAccessDescr() {
		return firstAccessDescr;
	}
	public void setFirstAccessDescr(String firstAccessDescr) {
		this.firstAccessDescr = firstAccessDescr;
	}
	public String getGiveDateDescr() {
		return giveDateDescr;
	}
	public void setGiveDateDescr(String giveDateDescr) {
		this.giveDateDescr = giveDateDescr;
	}
	public String getApproveDateDescr() {
		return approveDateDescr;
	}
	public void setApproveDateDescr(String approveDateDescr) {
		this.approveDateDescr = approveDateDescr;
	}
	public String getExpDateDescr() {
		return expDateDescr;
	}
	public void setExpDateDescr(String expDateDescr) {
		this.expDateDescr = expDateDescr;
	}
	public String getPayDateDescr() {
		return payDateDescr;
	}
	public void setPayDateDescr(String payDateDescr) {
		this.payDateDescr = payDateDescr;
	}
	public String getStartDateDescr() {
		return startDateDescr;
	}
	public void setStartDateDescr(String startDateDescr) {
		this.startDateDescr = startDateDescr;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	public String getCreateOnDescr() {
		return createOnDescr;
	}
	public void setCreateOnDescr(String createOnDescr) {
		this.createOnDescr = createOnDescr;
	}
	public String getLastUpdateBy() {
		return lastUpdateBy;
	}
	public void setLastUpdateBy(String lastUpdateBy) {
		this.lastUpdateBy = lastUpdateBy;
	}
	public String getLastUpdateOnDescr() {
		return lastUpdateOnDescr;
	}
	public void setLastUpdateOnDescr(String lastUpdateOnDescr) {
		this.lastUpdateOnDescr = lastUpdateOnDescr;
	}
	public BigDecimal getAccrual() {
		return accrual;
	}
	public void setAccrual(BigDecimal accrual) {
		this.accrual = accrual;
	}
	public BigDecimal getOriginalMoney() {
		return originalMoney;
	}
	public void setOriginalMoney(BigDecimal originalMoney) {
		this.originalMoney = originalMoney;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getPayCloseStatus() {
		return payCloseStatus;
	}
	public void setPayCloseStatus(int payCloseStatus) {
		this.payCloseStatus = payCloseStatus;
	}
	public Date getPayCloseDate() {
		return payCloseDate;
	}
	public void setPayCloseDate(Date payCloseDate) {
		this.payCloseDate = payCloseDate;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public double getTarget() {
		return target;
	}
	public void setTarget(double target) {
		this.target = target;
	}
	public double getAchievePer() {
		return achievePer;
	}
	public void setAchievePer(double achievePer) {
		this.achievePer = achievePer;
	}
	public double getLimitMonth() {
		return limitMonth;
	}
	public void setLimitMonth(double limitMonth) {
		this.limitMonth = limitMonth;
	}
	public String getLoanCode() {
		return loanCode;
	}
	public void setLoanCode(String loanCode) {
		this.loanCode = loanCode;
	}
	
}
