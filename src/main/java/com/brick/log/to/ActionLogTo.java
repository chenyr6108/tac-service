package com.brick.log.to;

import com.brick.base.to.BaseTo;

public class ActionLogTo extends BaseTo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String logId;
	private String logBy;
	private String logAction;
	private String logContent;
	private String logIp;
	
	public ActionLogTo() {
		super();
	}
	public ActionLogTo(String logBy, String logAction, String logContent, String logIp) {
		super();
		this.logBy = logBy;
		this.logAction = logAction;
		this.logContent = logContent;
		this.logIp = logIp;
	}
	public String getLogId() {
		return logId;
	}
	public void setLogId(String logId) {
		this.logId = logId;
	}
	public String getLogAction() {
		return logAction;
	}
	public void setLogAction(String logAction) {
		this.logAction = logAction;
	}
	public String getLogContent() {
		return logContent;
	}
	public void setLogContent(String logContent) {
		this.logContent = logContent;
	}
	public String getLogIp() {
		return logIp;
	}
	public void setLogIp(String logIp) {
		this.logIp = logIp;
	}
	public String getLogBy() {
		return logBy;
	}
	public void setLogBy(String logBy) {
		this.logBy = logBy;
	}
}
