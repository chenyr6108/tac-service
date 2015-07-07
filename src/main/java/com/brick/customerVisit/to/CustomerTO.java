package com.brick.customerVisit.to;

/**
 * @author ShenQi
 * */
public class CustomerTO {

	private String id;
	private String employeeId;
	private String name;//业务员
	private String date;//日期
	private String expectFromTime;//预计开始时间
	private String expectFromHour;
	private String expectToHour;
	private String expectFromMin;
	private String expectToMin;
	private String expectToTime;//预计结束时间
	private String actualFromTime;//实际开始时间
	private String actualFromHour;
	private String actualToHour;
	private String actualFromMin;
	private String actualToMin;
	private String actualToTime;//实际结束时间
	private String object;//对象
	private String empolderWay;//开拓方式
	private String empolderWayDescr;
	private String provinceId;//省
	private String cityId;//市
	private String areaId;//区
	private String intent;//目的
	private String intentDescr;
	private String withSupervisor;//主管陪同
	private String holiday;//是否请假
	private String holidayFromTime;
	private String holidayToTime;
	private String holidayFromHour;
	private String holidayToHour;
	private String holidayFromMin;
	private String holidayToMin;
	private String importantRecord;
	private String importantRecordDescr;
	private String remark;
	private String lastUpdatedBy;
	private String lastUpdatedOn;
	
	private String sunday;
	private String monday;
	private String tuesday;
	private String wednesday;
	private String thursday;
	private String friday;
	private String saturday;
	private String currentWeekFlag;
	private String value;
	
	private int rowMaxNum;

	private String needColleague;
	private String employee;
	
	public String getNeedColleague() {
		return needColleague;
	}

	public void setNeedColleague(String needColleague) {
		this.needColleague = needColleague;
	}

	public String getEmployee() {
		return employee;
	}

	public void setEmployee(String employee) {
		this.employee = employee;
	}

	public int getRowMaxNum() {
		return rowMaxNum;
	}

	public void setRowMaxNum(int rowMaxNum) {
		this.rowMaxNum = rowMaxNum;
	}

	public CustomerTO() {
		
	}
	
	public CustomerTO(String sunday, String monday, String tuesday,
			String wednesday, String thursday, String friday, String saturday) {
		super();
		this.sunday = sunday;
		this.monday = monday;
		this.tuesday = tuesday;
		this.wednesday = wednesday;
		this.thursday = thursday;
		this.friday = friday;
		this.saturday = saturday;
		this.currentWeekFlag = currentWeekFlag;
	}

	public CustomerTO(String id, String employeeId, String actualFromTime,
			String actualToTime, String object, String empolderWay,
			String intent, String withSupervisor) {
		super();
		this.id = id;
		this.employeeId = employeeId;
		this.actualFromTime = actualFromTime;
		this.actualToTime = actualToTime;
		this.object = object;
		this.empolderWay = empolderWay;
		this.intent = intent;
		this.withSupervisor = withSupervisor;
	}

	public CustomerTO(String employeeId, String name, String date,
			String expectFromTime, String expectToTime, String actualFromTime,
			String actualToTime, String object, String empolderWayDescr,
			String provinceId, String cityId, String areaId, String intentDescr,
			String withSupervisor, String holiday, String holidayFromTime,
			String holidayToTime, String lastUpdatedBy, String lastUpdatedOn,
			String sunday, String monday, String tuesday, String wednesday,
			String thursday, String friday, String saturday,
			String currentWeekFlag, String value) {
		super();
		this.employeeId = employeeId;
		this.name = name;
		this.date = date;
		this.expectFromTime = expectFromTime;
		this.expectToTime = expectToTime;
		this.actualFromTime = actualFromTime;
		this.actualToTime = actualToTime;
		this.object = object;
		this.empolderWayDescr = empolderWayDescr;
		this.provinceId = provinceId;
		this.cityId = cityId;
		this.areaId = areaId;
		this.intentDescr = intentDescr;
		this.withSupervisor = withSupervisor;
		this.holiday = holiday;
		this.holidayFromTime = holidayFromTime;
		this.holidayToTime = holidayToTime;
		this.lastUpdatedBy = lastUpdatedBy;
		this.lastUpdatedOn = lastUpdatedOn;
		this.sunday = sunday;
		this.monday = monday;
		this.tuesday = tuesday;
		this.wednesday = wednesday;
		this.thursday = thursday;
		this.friday = friday;
		this.saturday = saturday;
		this.currentWeekFlag = currentWeekFlag;
		this.value = value;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getExpectFromTime() {
		if(expectFromTime==null||"".equals(expectFromTime)||expectFromTime.indexOf(".")==-1) {
			return expectFromTime;
		}
		return expectFromTime.substring(0,expectFromTime.length()-11);
	}
	public void setExpectFromTime(String expectFromTime) {
		this.expectFromTime = expectFromTime;
	}
	public String getExpectToTime() {
		if(expectToTime==null||"".equals(expectToTime)||expectToTime.indexOf(".")==-1) {
			return expectToTime;
		}
		return expectToTime.substring(0,expectToTime.length()-11);
	}
	public void setExpectToTime(String expectToTime) {
		this.expectToTime = expectToTime;
	}
	public String getActualFromTime() {
		if(actualFromTime==null||"".equals(actualFromTime)||actualFromTime.indexOf(".")==-1) {
			return actualFromTime;
		}
		return actualFromTime.substring(0,actualFromTime.length()-11);
	}
	public void setActualFromTime(String actualFromTime) {
		this.actualFromTime = actualFromTime;
	}
	public String getActualToTime() {
		if(actualToTime==null||"".equals(actualToTime)||actualToTime.indexOf(".")==-1) {
			return actualToTime;
		}
		return actualToTime.substring(0,actualToTime.length()-11);
	}
	public void setActualToTime(String actualToTime) {
		this.actualToTime = actualToTime;
	}
	public String getObject() {
		return object;
	}
	public void setObject(String object) {
		this.object = object;
	}
	public String getEmpolderWay() {
		return empolderWay;
	}
	public void setEmpolderWay(String empolderWay) {
		this.empolderWay = empolderWay;
	}
	public String getProvinceId() {
		return provinceId;
	}
	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}
	public String getCityId() {
		return cityId;
	}
	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	public String getAreaId() {
		return areaId;
	}
	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}
	public String getIntent() {
		return intent;
	}
	public void setIntent(String intent) {
		this.intent = intent;
	}
	public String getWithSupervisor() {
		return withSupervisor;
	}
	public void setWithSupervisor(String withSupervisor) {
		this.withSupervisor = withSupervisor;
	}
	public String getHoliday() {
		return holiday;
	}
	public void setHoliday(String holiday) {
		this.holiday = holiday;
	}
	public String getHolidayFromTime() {
		if(holidayFromTime==null||"".equals(holidayFromTime)||holidayFromTime.indexOf(".")==-1) {
			return holidayFromTime;
		}
		return holidayFromTime.substring(0,holidayFromTime.length()-11);
	}
	public void setHolidayFromTime(String holidayFromTime) {
		this.holidayFromTime = holidayFromTime;
	}
	public String getHolidayToTime() {
		if(holidayToTime==null||"".equals(holidayToTime)||holidayToTime.indexOf(".")==-1) {
			return holidayToTime;
		}
		return holidayToTime.substring(0,holidayToTime.length()-11);
	}
	public void setHolidayToTime(String holidayToTime) {
		this.holidayToTime = holidayToTime;
	}
	public String getSunday() {
		return sunday;
	}
	public void setSunday(String sunday) {
		this.sunday = sunday;
	}
	public String getMonday() {
		return monday;
	}
	public void setMonday(String monday) {
		this.monday = monday;
	}
	public String getTuesday() {
		return tuesday;
	}
	public void setTuesday(String tuesday) {
		this.tuesday = tuesday;
	}
	public String getWednesday() {
		return wednesday;
	}
	public void setWednesday(String wednesday) {
		this.wednesday = wednesday;
	}
	public String getThursday() {
		return thursday;
	}
	public void setThursday(String thursday) {
		this.thursday = thursday;
	}
	public String getFriday() {
		return friday;
	}
	public void setFriday(String friday) {
		this.friday = friday;
	}
	public String getSaturday() {
		return saturday;
	}
	public void setSaturday(String saturday) {
		this.saturday = saturday;
	}
	public String getCurrentWeekFlag() {
		return currentWeekFlag;
	}
	public void setCurrentWeekFlag(String currentWeekFlag) {
		this.currentWeekFlag = currentWeekFlag;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}
	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}
	public String getLastUpdatedOn() {
		return lastUpdatedOn;
	}
	public void setLastUpdatedOn(String lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public String getEmpolderWayDescr() {
		return empolderWayDescr;
	}

	public void setEmpolderWayDescr(String empolderWayDescr) {
		this.empolderWayDescr = empolderWayDescr;
	}

	public String getIntentDescr() {
		return intentDescr;
	}

	public void setIntentDescr(String intentDescr) {
		this.intentDescr = intentDescr;
	}
	
	private String provinceName;
	private String cityName;
	private String areaName;

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

	public String getExpectFromHour() {
		return expectFromTime.split(":")[0];
	}

	public void setExpectFromHour(String expectFromHour) {
		this.expectFromHour = expectFromHour;
	}

	public String getExpectToHour() {
		return expectToTime.split(":")[0];
	}

	public void setExpectToHour(String expectToHour) {
		this.expectToHour = expectToHour;
	}

	public String getExpectFromMin() {
		return expectFromTime.split(":")[1];
	}

	public void setExpectFromMin(String expectFromMin) {
		this.expectFromMin = expectFromMin;
	}

	public String getExpectToMin() {
		return expectToTime.split(":")[1];
	}

	public void setExpectToMin(String expectToMin) {
		this.expectToMin = expectToMin;
	}

	public String getActualFromHour() {
		if(actualFromTime==null) {
			return "01";
		}
		return actualFromTime.split(":")[0];
	}

	public void setActualFromHour(String actualFromHour) {
		this.actualFromHour = actualFromHour;
	}

	public String getActualToHour() {
		if(actualToTime==null) {
			return "01";
		}
		return actualToTime.split(":")[0];
	}

	public void setActualToHour(String actualToHour) {
		this.actualToHour = actualToHour;
	}

	public String getActualFromMin() {
		if(actualFromTime==null) {
			return "00";
		}
		return actualFromTime.split(":")[1];
	}

	public void setActualFromMin(String actualFromMin) {
		this.actualFromMin = actualFromMin;
	}

	public String getActualToMin() {
		if(actualToTime==null) {
			return "00";
		}
		return actualToTime.split(":")[1];
	}

	public void setActualToMin(String actualToMin) {
		this.actualToMin = actualToMin;
	}

	public String getHolidayFromHour() {
		return holidayFromTime.split(":")[0];
	}

	public void setHolidayFromHour(String holidayFromHour) {
		this.holidayFromHour = holidayFromHour;
	}

	public String getHolidayToHour() {
		return holidayToTime.split(":")[0];
	}

	public void setHolidayToHour(String holidayToHour) {
		this.holidayToHour = holidayToHour;
	}

	public String getHolidayFromMin() {
		return holidayFromTime.split(":")[1];
	}

	public void setHolidayFromMin(String holidayFromMin) {
		this.holidayFromMin = holidayFromMin;
	}

	public String getHolidayToMin() {
		return holidayToTime.split(":")[1];
	}

	public void setHolidayToMin(String holidayToMin) {
		this.holidayToMin = holidayToMin;
	}
	public String getImportantRecord() {
		return importantRecord;
	}
	public void setImportantRecord(String importantRecord) {
		this.importantRecord = importantRecord;
	}
	public String getRemark() {
		if(remark==null) {
			return "";
		}
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getImportantRecordDescr() {
		return importantRecordDescr;
	}
	public void setImportantRecordDescr(String importantRecordDescr) {
		this.importantRecordDescr = importantRecordDescr;
	}
}
