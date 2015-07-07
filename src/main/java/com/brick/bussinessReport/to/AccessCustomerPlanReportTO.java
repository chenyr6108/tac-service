package com.brick.bussinessReport.to;

import com.brick.customerVisit.to.CustomerTO;

public class AccessCustomerPlanReportTO extends CustomerTO {

	private String fromDateDescr;
	private String DateDescr;
	private String deptName;
	private String provinceName;
	private String cityName;
	private String areaName;
	
	public String getFromDateDescr() {
		return fromDateDescr;
	}
	public void setFromDateDescr(String fromDateDescr) {
		this.fromDateDescr = fromDateDescr;
	}
	public String getDateDescr() {
		return DateDescr;
	}
	public void setDateDescr(String dateDescr) {
		DateDescr = dateDescr;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public String getProvinceName() {
		return provinceName;
	}
	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
}
