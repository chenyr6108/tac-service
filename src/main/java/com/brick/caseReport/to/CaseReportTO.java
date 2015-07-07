package com.brick.caseReport.to;

public class CaseReportTO {

	private String decpId;
	private String decpNameCn;
	
	private String infoCount;
	private String infoAmount;
	
	private String hasAccessCount;
	private String hasAccessAmount;
	
	private String auditCount;
	private String auditAmount;
	
	private String approveCount;
	private String approveAmount;
	
	public CaseReportTO(String decpId,String decpNameCn) {
		this.decpId=decpId;
		this.decpNameCn=decpNameCn;
		this.infoCount="0";
		this.infoAmount="0";
		this.hasAccessCount="0";
		this.hasAccessAmount="0";
		this.auditCount="0";
		this.auditAmount="0";
		this.approveCount="0";
		this.approveAmount="0";
	}
	
	public String getDecpId() {
		return decpId;
	}
	public void setDecpId(String decpId) {
		this.decpId = decpId;
	}
	public String getDecpNameCn() {
		return decpNameCn;
	}
	public void setDecpNameCn(String decpNameCn) {
		this.decpNameCn = decpNameCn;
	}
	public String getInfoCount() {
		return infoCount;
	}
	public void setInfoCount(String infoCount) {
		this.infoCount = infoCount;
	}
	public String getInfoAmount() {
		return infoAmount;
	}
	public void setInfoAmount(String infoAmount) {
		this.infoAmount = infoAmount;
	}
	public String getHasAccessCount() {
		return hasAccessCount;
	}

	public void setHasAccessCount(String hasAccessCount) {
		this.hasAccessCount = hasAccessCount;
	}

	public String getHasAccessAmount() {
		return hasAccessAmount;
	}

	public void setHasAccessAmount(String hasAccessAmount) {
		this.hasAccessAmount = hasAccessAmount;
	}

	public String getAuditCount() {
		return auditCount;
	}
	public void setAuditCount(String auditCount) {
		this.auditCount = auditCount;
	}
	public String getAuditAmount() {
		return auditAmount;
	}
	public void setAuditAmount(String auditAmount) {
		this.auditAmount = auditAmount;
	}
	public String getApproveCount() {
		return approveCount;
	}
	public void setApproveCount(String approveCount) {
		this.approveCount = approveCount;
	}
	public String getApproveAmount() {
		return approveAmount;
	}
	public void setApproveAmount(String approveAmount) {
		this.approveAmount = approveAmount;
	}
	
}
