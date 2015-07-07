package com.brick.risk_audit.to;

import java.sql.Timestamp;

import com.brick.base.to.BaseTo;

public class RiskAuditTo extends BaseTo {
	
	private static final long serialVersionUID = 1L;
	
	private String prcId;
	private int state;
	private int status;
	private String prcNode;
	private String prcLevelId;
	private String realPrcCode;
	private String prcCode;
	private Integer riskLevel = 1;
	private String riskLevelMsg;
	private String prcmContext;
	private String prcmLevel;
	private Timestamp commitTime;
	private Timestamp finishTime;
	private String returnClassLevelOne;
	private String returnClassLevelTwo;
	
	public Integer getRiskLevel() {
		return riskLevel;
	}
	public void setRiskLevel(Integer riskLevel) {
		this.riskLevel = riskLevel;
	}
	public String getRiskLevelMsg() {
		return riskLevelMsg;
	}
	public void setRiskLevelMsg(String riskLevelMsg) {
		this.riskLevelMsg = riskLevelMsg;
	}
	public String getPrcId() {
		return prcId;
	}
	public void setPrcId(String prcId) {
		this.prcId = prcId;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getPrcNode() {
		return prcNode;
	}
	public void setPrcNode(String prcNode) {
		this.prcNode = prcNode;
	}
	public String getPrcLevelId() {
		return prcLevelId;
	}
	public void setPrcLevelId(String prcLevelId) {
		this.prcLevelId = prcLevelId;
	}
	public String getRealPrcCode() {
		return realPrcCode;
	}
	public void setRealPrcCode(String realPrcCode) {
		this.realPrcCode = realPrcCode;
	}
	public String getPrcCode() {
		return prcCode;
	}
	public void setPrcCode(String prcCode) {
		this.prcCode = prcCode;
	}
	public String getPrcmContext() {
		return prcmContext;
	}
	public void setPrcmContext(String prcmContext) {
		this.prcmContext = prcmContext;
	}
	public String getPrcmLevel() {
		return prcmLevel;
	}
	public void setPrcmLevel(String prcmLevel) {
		this.prcmLevel = prcmLevel;
	}
	public Timestamp getCommitTime() {
		return commitTime;
	}
	public void setCommitTime(Timestamp commitTime) {
		this.commitTime = commitTime;
	}
	public Timestamp getFinishTime() {
		return finishTime;
	}
	public void setFinishTime(Timestamp finishTime) {
		this.finishTime = finishTime;
	}
	public String getReturnClassLevelOne() {
		return returnClassLevelOne;
	}
	public void setReturnClassLevelOne(String returnClassLevelOne) {
		this.returnClassLevelOne = returnClassLevelOne;
	}
	public String getReturnClassLevelTwo() {
		return returnClassLevelTwo;
	}
	public void setReturnClassLevelTwo(String returnClassLevelTwo) {
		this.returnClassLevelTwo = returnClassLevelTwo;
	}
}
