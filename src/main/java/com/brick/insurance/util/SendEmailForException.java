package com.brick.insurance.util;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;

public class SendEmailForException {
	
	public Logger logger = Logger.getLogger(this.getClass());
	private MailUtilService mailUtilService;
	
	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}
	
	/**
	 * 发送错误提醒
	 * @param string
	 * @param i
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void sendExceptionByEmail(String string, int i) {
		MailSettingTo mailSettingTo = new MailSettingTo();
		mailSettingTo.setEmailContent(string);
		try {
			mailUtilService.sendMail(121, mailSettingTo);
		} catch (Exception e) {
			logger.error(e);
		}
	}
}
