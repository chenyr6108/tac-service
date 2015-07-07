package com.brick.dun.to;

import java.sql.Date;

import com.brick.base.to.BaseTo;

public class DunLetterTo extends BaseTo {
	
	private static final long serialVersionUID = 1L;
	private String sensorId;
	private Date shouldPayDate;
	private String custId;
	private String custCode;
	private String leaseCode;
	private String custName;
	private String dunDay;
	private String suplTrue;
	private String name;
	private String upName;
	private String brand;
	private String rectId;
	private double dunAllPrice;
	private double dunFine;
	private double noDunFine;
	private String linkName;
	
	
	
	public String getLinkName() {
		return linkName;
	}
	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}
	public double getDunAllPrice() {
		return dunAllPrice;
	}
	public void setDunAllPrice(double dunAllPrice) {
		this.dunAllPrice = dunAllPrice;
	}
	public double getDunFine() {
		return dunFine;
	}
	public void setDunFine(double dunFine) {
		this.dunFine = dunFine;
	}
	public double getNoDunFine() {
		return noDunFine;
	}
	public void setNoDunFine(double noDunFine) {
		this.noDunFine = noDunFine;
	}
	public String getRectId() {
		return rectId;
	}
	public void setRectId(String rectId) {
		this.rectId = rectId;
	}
	public String getSensorId() {
		return sensorId;
	}
	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}
	public Date getShouldPayDate() {
		return shouldPayDate;
	}
	public void setShouldPayDate(Date shouldPayDate) {
		this.shouldPayDate = shouldPayDate;
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getCustCode() {
		return custCode;
	}
	public void setCustCode(String custCode) {
		this.custCode = custCode;
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
	public String getDunDay() {
		return dunDay;
	}
	public void setDunDay(String dunDay) {
		this.dunDay = dunDay;
	}
	public String getSuplTrue() {
		return suplTrue;
	}
	public void setSuplTrue(String suplTrue) {
		this.suplTrue = suplTrue;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUpName() {
		return upName;
	}
	public void setUpName(String upName) {
		this.upName = upName;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	
}
