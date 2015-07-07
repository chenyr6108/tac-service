package com.brick.credit.to;

import java.sql.Date;

import com.brick.base.to.BaseTo;

public class CreditTo extends BaseTo {

	private static final long serialVersionUID = 1L;
	
	private String creditId;
	private String custCode;
	private String custName;
	private String decpName;
	private String upperUser;
	private String sensorUser;
	private Date minCommitDate;
	private String minCommitDateStr;
	private Integer arg1;
	private Integer state;
	private Date realVisitDate;
	private String realVisitDateStr;
	private String addedInfo;
	private String creditRuncode;
	private Double leaseRze;
	private String suplName;
	private String areaName;
	private Integer businessStatus;
	private Integer businessStatusVersion;
	private String thingName;
	private String processer;
	private String memo;
	private String contractType;
	private double approriateMoney; 
	private String isNewProduction;
	private Integer dayDiff;

	public String getCreditId() {
		return creditId;
	}

	public void setCreditId(String creditId) {
		this.creditId = creditId;
	}

	public String getCustCode() {
		return custCode;
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getUpperUser() {
		return upperUser;
	}

	public void setUpperUser(String upperUser) {
		this.upperUser = upperUser;
	}

	public String getSensorUser() {
		return sensorUser;
	}

	public void setSensorUser(String sensorUser) {
		this.sensorUser = sensorUser;
	}

	public Date getMinCommitDate() {
		return minCommitDate;
	}

	public void setMinCommitDate(Date minCommitDate) {
		this.minCommitDate = minCommitDate;
	}

	public String getMinCommitDateStr() {
		return minCommitDateStr;
	}

	public void setMinCommitDateStr(String minCommitDateStr) {
		this.minCommitDateStr = minCommitDateStr;
	}

	public String getDecpName() {
		return decpName;
	}

	public void setDecpName(String decpName) {
		this.decpName = decpName;
	}

	public Integer getArg1() {
		return arg1;
	}

	public void setArg1(Integer arg1) {
		this.arg1 = arg1;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Date getRealVisitDate() {
		return realVisitDate;
	}

	public void setRealVisitDate(Date realVisitDate) {
		this.realVisitDate = realVisitDate;
	}

	public String getRealVisitDateStr() {
		return realVisitDateStr;
	}

	public void setRealVisitDateStr(String realVisitDateStr) {
		this.realVisitDateStr = realVisitDateStr;
	}

	public String getAddedInfo() {
		return addedInfo;
	}

	public void setAddedInfo(String addedInfo) {
		this.addedInfo = addedInfo;
	}

	public String getCreditRuncode() {
		return creditRuncode;
	}

	public void setCreditRuncode(String creditRuncode) {
		this.creditRuncode = creditRuncode;
	}

	public Double getLeaseRze() {
		return leaseRze;
	}

	public void setLeaseRze(Double leaseRze) {
		this.leaseRze = leaseRze;
	}

	public String getSuplName() {
		return suplName;
	}

	public void setSuplName(String suplName) {
		this.suplName = suplName;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public Integer getBusinessStatus() {
		this.businessStatus = this.businessStatus == null ? 0 : this.businessStatus;
		return businessStatus;
	}

	public void setBusinessStatus(Integer businessStatus) {
		this.businessStatus = businessStatus;
	}

	public Integer getBusinessStatusVersion() {
		this.businessStatusVersion = this.businessStatusVersion == null ? 0 : this.businessStatusVersion;
		return businessStatusVersion;
	}

	public void setBusinessStatusVersion(Integer businessStatusVersion) {
		this.businessStatusVersion = businessStatusVersion;
	}

	public String getThingName() {
		return thingName;
	}

	public void setThingName(String thingName) {
		this.thingName = thingName;
	}

	public String getProcesser() {
		return processer;
	}

	public void setProcesser(String processer) {
		this.processer = processer;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getContractType() {
		return contractType;
	}

	public void setContractType(String contractType) {
		this.contractType = contractType;
	}

	public double getApproriateMoney() {
		return approriateMoney;
	}

	public void setApproriateMoney(Double approriateMoney) {
		this.approriateMoney = approriateMoney == null ? 0 : approriateMoney;
	}

	public String getIsNewProduction() {
		return isNewProduction;
	}

	public void setIsNewProduction(String isNewProduction) {
		this.isNewProduction = isNewProduction;
	}

	public Integer getDayDiff() {
		return dayDiff;
	}

	public void setDayDiff(Integer dayDiff) {
		this.dayDiff = dayDiff;
	}
	
}
