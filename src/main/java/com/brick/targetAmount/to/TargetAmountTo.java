package com.brick.targetAmount.to;



import java.sql.Date;

import com.brick.base.to.BaseTo;

public class TargetAmountTo  extends BaseTo{

	private static final long serialVersionUID = 1L;
	
	private double weekMoney;
	private String area;
	private Date startDate;
	private Date endDate;
	private Integer days;//周1、算每周的天数求每周目标值     月 2、存每月的值（jsp页面）
	private double targetMoney;
	
	private double monthTargetMoney;
	private double monthMoney;
	
	
	public double getMonthTargetMoney() {
		return monthTargetMoney;
	}
	public void setMonthTargetMoney(double monthTargetMoney) {
		this.monthTargetMoney = monthTargetMoney;
	}
	public double getMonthMoney() {
		return monthMoney;
	}
	public void setMonthMoney(double monthMoney) {
		this.monthMoney = monthMoney;
	}
	public Integer getDays() {
		return days;
	}
	public void setDays(Integer days) {
		this.days = days;
	}
	
	
	public double getTargetMoney() {
		return targetMoney;
	}
	public void setTargetMoney(double targetMoney) {
		this.targetMoney = targetMoney;
	}
	public double getWeekMoney() {
		return weekMoney;
	}
	public void setWeekMoney(double weekMoney) {
		this.weekMoney = weekMoney;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
