package com.brick.job.service;

import org.springframework.transaction.annotation.Transactional;

import com.brick.base.service.BaseService;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;

public class ITJobService extends BaseService {
	
	private MailUtilService mailUtil;
	
	public MailUtilService getMailUtil() {
		return mailUtil;
	}

	public void setMailUtil(MailUtilService mailUtil) {
		this.mailUtil = mailUtil;
	}

	public void doService() throws Exception{
		if (this.isTheFirstWorkingDayOfWeek()) {
			mailUtil.sendMail(233, new MailSettingTo());
		}
	}
	
	@Transactional
	public void prc_CreateDispatchUserByDay(){
		queryForObj("job.prc_CreateDispatchUserByDay");
	}
	
	@Transactional
	public void prc_latest_paydetail(){
		queryForObj("job.prc_latest_paydetail");
	}
	
	@Transactional
	public void proc_activites_statistics(){
		queryForObj("job.proc_activites_statistics");
	}
	
	@Transactional
	public void proc_dun_report_equ(){
		queryForObj("job.proc_dun_report_equ");
	}
	
	@Transactional
	public void proc_dun_report_motor(){
		queryForObj("job.proc_dun_report_motor");
	}
	
	@Transactional
	public void prc_rent_settle(){
		queryForObj("job.prc_rent_settle");
	}
	
}
