package com.brick.aprv.to;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 核准函模型
 * @author zhangyizhou
 *
 */
public class ApprovalTo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 核准函ID
	 */
	private Integer aprvId;
	
	/**
	 * 核准函编号
	 */
	private String aprvCode;
	
	/**
	 * 核准函发起人
	 */
	private Integer applyUserId;
	
	private String applyUserName;
	
	/**
	 * 上级
	 */
	private Integer upUserId;
	
	/**
	 * 公司
	 */
	private Integer companyCode;
	
	/**
	 * 概要
	 */
	private String summary;
	
	/**
	 * 正文
	 */
	private String content;
	
	/**
	 * 流程编号
	 */
	private Integer processId;
	
	/**
	 * 流程结点状态
	 */
	private Integer flowStatus;
	
	private String currentCharge;
	
	private String currentDelegate;
	
	/**
	 * 历史流程
	 */
	private String hisProcess;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	/**
	 * 合同ID
	 */
	private Integer rectId;
	
	private Integer prcId;
	
	/**
	 * 合同号
	 */
	private String leaseCode;
	
	private String deptName;
	
	private String custName;
	
	private Integer payed;
	
	private String auditData;
	
	private Date payDate;
	
	private BigDecimal payMoney;
	
	private String riskUser;
	
	private String upUser;

	public Integer getAprvId() {
		return aprvId;
	}

	public void setAprvId(Integer aprvId) {
		this.aprvId = aprvId;
	}

	public String getAprvCode() {
		return aprvCode;
	}

	public void setAprvCode(String aprvCode) {
		this.aprvCode = aprvCode;
	}

	public Integer getApplyUserId() {
		return applyUserId;
	}

	public void setApplyUserId(Integer applyUserId) {
		this.applyUserId = applyUserId;
	}

	public Integer getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(Integer companyCode) {
		this.companyCode = companyCode;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getProcessId() {
		return processId;
	}

	public void setProcessId(Integer processId) {
		this.processId = processId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getApplyUserName() {
		return applyUserName;
	}

	public void setApplyUserName(String applyUserName) {
		this.applyUserName = applyUserName;
	}

	public Integer getRectId() {
		return rectId;
	}

	public void setRectId(Integer rectId) {
		this.rectId = rectId;
	}

	public String getLeaseCode() {
		return leaseCode;
	}

	public void setLeaseCode(String leaseCode) {
		this.leaseCode = leaseCode;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public Integer getPayed() {
		return payed;
	}

	public void setPayed(Integer payed) {
		this.payed = payed;
	}

	public String getAuditData() {
		return auditData;
	}

	public void setAuditData(String auditData) {
		this.auditData = auditData;
	}

	public Date getPayDate() {
		return payDate;
	}

	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}

	public BigDecimal getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(BigDecimal payMoney) {
		this.payMoney = payMoney;
	}

	public Integer getUpUserId() {
		return upUserId;
	}

	public void setUpUserId(Integer upUserId) {
		this.upUserId = upUserId;
	}

	public String getHisProcess() {
		return hisProcess;
	}

	public void setHisProcess(String hisProcess) {
		this.hisProcess = hisProcess;
	}

	public Integer getFlowStatus() {
		return flowStatus;
	}

	public void setFlowStatus(Integer flowStatus) {
		this.flowStatus = flowStatus;
	}

	public String getCurrentCharge() {
		return currentCharge;
	}

	public void setCurrentCharge(String currentCharge) {
		this.currentCharge = currentCharge;
	}

	public String getCurrentDelegate() {
		return currentDelegate;
	}

	public void setCurrentDelegate(String currentDelegate) {
		this.currentDelegate = currentDelegate;
	}

	public String getRiskUser() {
		return riskUser;
	}

	public void setRiskUser(String riskUser) {
		this.riskUser = riskUser;
	}

	public String getUpUser() {
		return upUser;
	}

	public void setUpUser(String upUser) {
		this.upUser = upUser;
	}

	public Integer getPrcId() {
		return prcId;
	}

	public void setPrcId(Integer prcId) {
		this.prcId = prcId;
	}

}
