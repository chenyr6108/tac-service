package com.brick.job.service;

import org.springframework.transaction.annotation.Transactional;

import com.brick.base.service.BaseService;

public class DunReportDailyJob extends BaseService {
	
	@Transactional
	public void doService(){
		//update("job.deleteDunDaily");
		//insert("job.insertDunDaily");
		//新逾期
		update("job.deleteDunDaily_new");
		insert("job.insertDunDaily_new");
	}
	
	@Transactional
	public void doDunReportPro(){
		//dunReportPro
		queryForObj("job.dunReportPro");
	}
}
