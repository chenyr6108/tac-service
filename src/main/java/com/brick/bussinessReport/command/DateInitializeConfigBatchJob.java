package com.brick.bussinessReport.command;


import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.bussinessReport.service.DateInitializeService;

public class DateInitializeConfigBatchJob extends BaseCommand {
		
	Log logger = LogFactory.getLog(DateInitializeConfigBatchJob.class);
	
	private DateInitializeService dateInitializeService;
	
	public DateInitializeService getDateInitializeService() {
		return dateInitializeService;
	}

	public void setDateInitializeService(DateInitializeService dateInitializeService) {
		this.dateInitializeService = dateInitializeService;
	}

	public void dateInitialize() {
		
		if(logger.isDebugEnabled()) {
			logger.debug("batch job for dateInitialize start  --------------------");
		}
		
		for(int day=0;day<365;day++) {
			this.dateInitializeService.dateInitializeConfig(day);
		}
		//如果是闰年插入2月29号
		int year=Calendar.getInstance().get(Calendar.YEAR);
		if((year%4==0&&year%100!=0)||(year%100==0&&year%400==0)) {
			this.dateInitializeService.insertLeapYear(String.valueOf(year),String.valueOf(year)+"-02-29");
		}
		if(logger.isDebugEnabled()) {
			logger.debug("batch job for dateInitialize end  --------------------");
		}
	}
}
