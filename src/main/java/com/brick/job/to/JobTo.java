package com.brick.job.to;

import java.util.Date;

public class JobTo {
	private String jobId;
	private String jobName;
	private String jobGroup;
	private Date startTime;
	private Date stopTime;
	private Date previousTime;
	private Date nextTime ;
	private Date fireTime;
	private int runFlag;
	private int status;
	private Long runTime;
	private String descr;
	private String fireStatus;
	private String remark;
	private int errorCode;
	private String errorCodeStr;
	
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getJobGroup() {
		return jobGroup;
	}
	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getStopTime() {
		return stopTime;
	}
	public void setStopTime(Date stopTime) {
		this.stopTime = stopTime;
	}
	public Date getPreviousTime() {
		return previousTime;
	}
	public void setPreviousTime(Date previousTime) {
		this.previousTime = previousTime;
	}
	public Date getNextTime() {
		return nextTime;
	}
	public void setNextTime(Date nextTime) {
		this.nextTime = nextTime;
	}
	public int getRunFlag() {
		return runFlag;
	}
	public void setRunFlag(int runFlag) {
		this.runFlag = runFlag;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Long getRunTime() {
		return runTime;
	}
	public void setRunTime(Long runTime) {
		this.runTime = runTime;
	}
	public Date getFireTime() {
		return fireTime;
	}
	public void setFireTime(Date fireTime) {
		this.fireTime = fireTime;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getFireStatus() {
		return fireStatus;
	}
	public void setFireStatus(String fireStatus) {
		this.fireStatus = fireStatus;
	}
	public String getErrorCodeStr() {
		if (0 == this.errorCode) {
			errorCodeStr = "无";
		} else if (800 == this.errorCode) {
			errorCodeStr = "Job运行时有异常抛出！";
		} else {
			errorCodeStr = "其他异常！";
		}
		return errorCodeStr;
	}
	public void setErrorCodeStr(String errorCodeStr) {
		this.errorCodeStr = errorCodeStr;
	}
}
