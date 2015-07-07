package com.brick.sys.service;

import com.brick.base.service.BaseService;
import com.brick.common.dao.CommonDAO;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.util.DateUtil;

public class SystemMessageAndEmailReportService extends BaseService{
	private CommonDAO commonDAO;
	
	private MailUtilService mailUtilService;
	
	public void messageAndEmailReport() throws Exception{
		
		int messageCount = (Integer) commonDAO.queryForObject("common.getMessageCountLastDay", null);
		int emailCount = (Integer) commonDAO.queryForObject("common.getEmailCountLastDay", null);
		int hrEmailCount = (Integer) commonDAO.queryForObject("common.getHrEmailCountLastDay", null);
		StringBuffer content = new StringBuffer("<table style=\"border:1px solid\"  cellpadding=\"5\" cellspacing=\"0\">");
		content.append("<tr>");
		content.append("<td style=\"border: 1px solid;width:200px;text-align:center\"><b>昨日短信发送总数：<b></td>");
		content.append("<td style=\"border: 1px solid;width:50px;text-align:center\">" + String.valueOf(messageCount)+ "</td>");
		content.append("</tr>");
		
		content.append("<tr>");
		content.append("<td style=\"border: 1px solid;width:200px;text-align:center\"><b>昨日邮件发送总数：<b></td>");
		content.append("<td style=\"border: 1px solid;width:50px;text-align:center\">" + String.valueOf(emailCount+hrEmailCount)+ "</td>");
		content.append("</tr>");
		content.append("<tr><td style=\"border: 1px solid;text-align:right\" colspan=\"2\">统计日期:"+DateUtil.getCurrentDate()+"</td></tr>");
		content.append("</table>");
		MailSettingTo mailSettingTo = new MailSettingTo();
		mailSettingTo.setEmailContent(content.toString());
		mailUtilService.sendMail(2000, mailSettingTo);
	}

	public CommonDAO getCommonDAO() {
		return commonDAO;
	}

	public void setCommonDAO(CommonDAO commonDAO) {
		this.commonDAO = commonDAO;
	}

	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}
	
	
}
