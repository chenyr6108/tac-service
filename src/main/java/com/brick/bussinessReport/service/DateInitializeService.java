package com.brick.bussinessReport.service;

import com.brick.base.service.BaseService;
import com.brick.bussinessReport.dao.DateInitializeDAO;

public class DateInitializeService extends BaseService {

	private DateInitializeDAO dateInitializeDAO;

	public DateInitializeDAO getDateInitializeDAO() {
		return dateInitializeDAO;
	}

	public void setDateInitializeDAO(DateInitializeDAO dateInitializeDAO) {
		this.dateInitializeDAO = dateInitializeDAO;
	}
	
	public void dateInitializeConfig(int day) {
		
		this.dateInitializeDAO.dateInitializeConfig(day);
		
	}
	
	public void insertLeapYear(String year,String date) {
		
		this.dateInitializeDAO.insertLeapYear(year,date);
	}
}
