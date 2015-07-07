package com.brick.special.to;

import java.sql.Date;

public class CreditSpecialTO {

	private int id;
	private String propertyCode;
	private String propertyName;
	private String isNeed;
	private String name;
	private String modifyDateDescr;
	private String modifyDate;
	private String status;
	
	private String creditName;
	private String creditCode;
	private String startDateDescr;
	private String endDateDescr;
	
	private String value1;
	private String value2;
	private String checkValue;
	
	private String[] supplierIds;
	private String[] supplierNames;
	
	private String custName;
	private String logContent;
	private Date creditDate;
	private String creditDateDescr;
	
	public Date getCreditDate() {
		return creditDate;
	}
	public void setCreditDate(Date creditDate) {
		this.creditDate = creditDate;
	}
	public String getCreditDateDescr() {
		return creditDateDescr;
	}
	public void setCreditDateDescr(String creditDateDescr) {
		this.creditDateDescr = creditDateDescr;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPropertyCode() {
		return propertyCode;
	}
	public void setPropertyCode(String propertyCode) {
		this.propertyCode = propertyCode;
	}
	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	public String getIsNeed() {
		return isNeed;
	}
	public void setIsNeed(String isNeed) {
		this.isNeed = isNeed;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getModifyDateDescr() {
		return modifyDateDescr;
	}
	public void setModifyDateDescr(String modifyDateDescr) {
		this.modifyDateDescr = modifyDateDescr;
	}
	public String getModifyDate() {
		return modifyDate;
	}
	public void setModifyDate(String modifyDate) {
		this.modifyDate = modifyDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCreditName() {
		return creditName;
	}
	public void setCreditName(String creditName) {
		this.creditName = creditName;
	}
	public String getCreditCode() {
		return creditCode;
	}
	public void setCreditCode(String creditCode) {
		this.creditCode = creditCode;
	}
	public String getStartDateDescr() {
		return startDateDescr;
	}
	public void setStartDateDescr(String startDateDescr) {
		this.startDateDescr = startDateDescr;
	}
	public String getEndDateDescr() {
		return endDateDescr;
	}
	public void setEndDateDescr(String endDateDescr) {
		this.endDateDescr = endDateDescr;
	}
	public String getValue1() {
		return value1;
	}
	public void setValue1(String value1) {
		this.value1 = value1;
	}
	public String getValue2() {
		return value2;
	}
	public void setValue2(String value2) {
		this.value2 = value2;
	}
	public String getCheckValue() {
		return checkValue;
	}
	public void setCheckValue(String checkValue) {
		this.checkValue = checkValue;
	}
	public String[] getSupplierIds() {
		return supplierIds;
	}
	public void setSupplierIds(String[] supplierIds) {
		this.supplierIds = supplierIds;
	}
	public String[] getSupplierNames() {
		return supplierNames;
	}
	public void setSupplierNames(String[] supplierNames) {
		this.supplierNames = supplierNames;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getLogContent() {
		return logContent;
	}
	public void setLogContent(String logContent) {
		this.logContent = logContent;
	}
	
}
