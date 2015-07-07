package com.brick.insurance.to;

import java.util.Date;

import com.brick.base.to.BaseTo;

public class InsuranceTo extends BaseTo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String insuId;
	private String rectId;
	private String leaseCode;
	private Integer contractType;
	private String insuCode;
	private String parentInsuId;
	private String parentInsuCode;
	private Integer status;
	private Integer insuStatus;
	private Integer exceptionStatus;
	private String custName;
	private String incpName;
	private String incpNameManual;
	private Integer incpId;
	private Double insuRate;
	private Double insuRateMore;
	private Integer insuType;
	private Date insuStartDate;
	private Date insuEndDate;
	private java.sql.Date insuStartDateSql;
	private java.sql.Date insuEndDateSql;
	private Integer yearDiff;
	private Integer dayDiff;
	private String remark;
	private Integer decpId;
	private String creditRuncode;
	private Double insuPrice;
	private Double insuAmount;
	private String chargeCode;
	private java.sql.Date affirmInsuDate;
	private java.sql.Date affirmSeizeDate;
	private java.sql.Date printInsuDate;
	private Double lease_rze;
	private String isRenewal;
	private Date surrenderDate;
	private String surrenderCode;
	private Double surrenderPrice;
	private Integer surrenderStatus;
	private String surrenderReason;
	private String groupCode;
	private String companyCode;
	
	public String getInsuId() {
		return insuId;
	}
	public void setInsuId(String insuId) {
		this.insuId = insuId;
	}
	public String getRectId() {
		return rectId;
	}
	public void setRectId(String rectId) {
		this.rectId = rectId;
	}
	public String getLeaseCode() {
		return leaseCode;
	}
	public void setLeaseCode(String leaseCode) {
		this.leaseCode = leaseCode;
	}
	public Integer getContractType() {
		return contractType;
	}
	public void setContractType(Integer contractType) {
		this.contractType = contractType;
	}
	public String getInsuCode() {
		return insuCode;
	}
	public void setInsuCode(String insuCode) {
		this.insuCode = insuCode;
	}
	public Integer getStatus() {
		return status;
	}
	
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getInsuStatus() {
		return insuStatus;
	}
	
	/**
	 * 0 = 待投保、 
	 * 10 = 待录入、
	 * 20 = 正常、
	 * 30 = 终止、
	 * 40 = 结清待退保、
	 * 50 = 退保
	 * @param status
	 */
	public void setInsuStatus(Integer insuStatus) {
		this.insuStatus = insuStatus;
	}
	public Integer getExceptionStatus() {
		return exceptionStatus;
	}
	public void setExceptionStatus(Integer exceptionStatus) {
		this.exceptionStatus = exceptionStatus;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getIncpName() {
		return incpName;
	}
	public void setIncpName(String incpName) {
		this.incpName = incpName;
	}
	public Integer getIncpId() {
		return incpId;
	}
	public void setIncpId(Integer incpId) {
		this.incpId = incpId;
	}
	public Double getInsuRate() {
		return insuRate;
	}
	public void setInsuRate(Double insuRate) {
		this.insuRate = insuRate;
	}
	public Date getInsuStartDate() {
		if (this.insuStartDate == null && this.insuStartDateSql != null) {
			this.insuStartDate = this.insuStartDateSql;
		}
		return insuStartDate;
	}
	public void setInsuStartDate(Date insuStartDate) {
		this.insuStartDate = insuStartDate;
	}
	
	public Date getInsuEndDate() {
		if (this.insuEndDate == null && this.insuEndDateSql != null) {
			this.insuEndDate = this.insuEndDateSql;
		}
		return insuEndDate;
	}
	public void setInsuEndDate(Date insuEndDate) {
		this.insuEndDate = insuEndDate;
	}
	public Integer getYearDiff() {
		return yearDiff;
	}
	public void setYearDiff(Integer yearDiff) {
		this.yearDiff = yearDiff;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getDecpId() {
		return decpId;
	}
	public void setDecpId(Integer decpId) {
		this.decpId = decpId;
	}
	public String getParentInsuCode() {
		return parentInsuCode;
	}
	public void setParentInsuCode(String parentInsuCode) {
		this.parentInsuCode = parentInsuCode;
	}
	public String getCreditRuncode() {
		return creditRuncode;
	}
	public void setCreditRuncode(String creditRuncode) {
		this.creditRuncode = creditRuncode;
	}
	public Double getInsuPrice() {
		return insuPrice;
	}
	public void setInsuPrice(Double insuPrice) {
		this.insuPrice = insuPrice;
	}
	public String getChargeCode() {
		return chargeCode;
	}
	public void setChargeCode(String chargeCode) {
		this.chargeCode = chargeCode;
	}
	public String getIncpNameManual() {
		return incpNameManual;
	}
	public void setIncpNameManual(String incpNameManual) {
		this.incpNameManual = incpNameManual;
	}
	public java.sql.Date getAffirmInsuDate() {
		return affirmInsuDate;
	}
	public void setAffirmInsuDate(java.sql.Date affirmInsuDate) {
		this.affirmInsuDate = affirmInsuDate;
	}
	public java.sql.Date getAffirmSeizeDate() {
		return affirmSeizeDate;
	}
	public void setAffirmSeizeDate(java.sql.Date affirmSeizeDate) {
		this.affirmSeizeDate = affirmSeizeDate;
	}
	public java.sql.Date getPrintInsuDate() {
		return printInsuDate;
	}
	public void setPrintInsuDate(java.sql.Date printInsuDate) {
		this.printInsuDate = printInsuDate;
	}
	public Double getInsuAmount() {
		return insuAmount;
	}
	public void setInsuAmount(Double insuAmount) {
		this.insuAmount = insuAmount;
	}
	public Double getInsuRateMore() {
		return insuRateMore;
	}
	public void setInsuRateMore(Double insuRateMore) {
		this.insuRateMore = insuRateMore;
	}
	public Integer getInsuType() {
		return insuType == null ? 1 : insuType;
	}
	public void setInsuType(Integer insuType) {
		this.insuType = insuType;
	}
	public Double getLease_rze() {
		return lease_rze;
	}
	public void setLease_rze(Double lease_rze) {
		this.lease_rze = lease_rze;
	}
	public Integer getDayDiff() {
		return dayDiff;
	}
	public void setDayDiff(Integer dayDiff) {
		this.dayDiff = dayDiff;
	}
	public java.sql.Date getInsuStartDateSql() {
		return insuStartDateSql;
	}
	public void setInsuStartDateSql(java.sql.Date insuStartDateSql) {
		this.insuStartDateSql = insuStartDateSql;
	}
	public java.sql.Date getInsuEndDateSql() {
		return insuEndDateSql;
	}
	public void setInsuEndDateSql(java.sql.Date insuEndDateSql) {
		this.insuEndDateSql = insuEndDateSql;
	}
	public String getParentInsuId() {
		return parentInsuId;
	}
	public void setParentInsuId(String parentInsuId) {
		this.parentInsuId = parentInsuId;
	}
	public String getIsRenewal() {
		return isRenewal;
	}
	public void setIsRenewal(String isRenewal) {
		this.isRenewal = isRenewal;
	}
	public Date getSurrenderDate() {
		return surrenderDate;
	}
	public void setSurrenderDate(java.sql.Date surrenderDate) {
		this.surrenderDate = surrenderDate;
	}
	public String getSurrenderCode() {
		return surrenderCode;
	}
	public void setSurrenderCode(String surrenderCode) {
		this.surrenderCode = surrenderCode;
	}
	public Double getSurrenderPrice() {
		return surrenderPrice;
	}
	public void setSurrenderPrice(Double surrenderPrice) {
		this.surrenderPrice = surrenderPrice;
	}
	public Integer getSurrenderStatus() {
		return surrenderStatus;
	}
	public void setSurrenderStatus(Integer surrenderStatus) {
		this.surrenderStatus = surrenderStatus;
	}
	public String getSurrenderReason() {
		return surrenderReason;
	}
	public void setSurrenderReason(String surrenderReason) {
		this.surrenderReason = surrenderReason;
	}
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
}
