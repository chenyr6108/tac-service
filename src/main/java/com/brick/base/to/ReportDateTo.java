package com.brick.base.to;

import java.sql.Date;

public class ReportDateTo extends BaseTo {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int year;
	private short month;
	private Date beginTime;
	private Date endTime;
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public short getMonth() {
		return month;
	}
	public void setMonth(short month) {
		this.month = month;
	}
	public Date getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	
  
  
}
