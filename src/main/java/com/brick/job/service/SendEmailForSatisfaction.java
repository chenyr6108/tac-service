package com.brick.job.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.brick.base.service.BaseService;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.util.StringUtils;

public class SendEmailForSatisfaction extends BaseService {
	
	Logger logger = Logger.getLogger(SendEmailForSatisfaction.class);
	
	private MailUtilService mailUtilService;
	
	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}

	public void doService() throws Exception{
		logger.info("====================================理赔异常发送Email,Job.start=======================================");
		try {
			if (!super.isWorkingDay()) {
				logger.info("====================================理赔异常发送Email,Job.end.非工作日不发送.=======================================");
				return;
			}
			// 发给业管
			doSendEmailAll();
			// 发给业务
			doSendEmail();
			logger.info("====================================理赔异常发送Email,Job.end.success=======================================");
		} catch (Exception e) {
			logger.error("====================================理赔异常发送Email,Job.end.failed=======================================");
			throw e;
		}
	}
	
	private void doSendEmailAll() throws Exception{
		List<Map<String, Object>> resultList = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		int index = 0;
		try {
			StringBuffer sb = new StringBuffer("<html>" +
					"<head><style type=\"text/css\">" +
					"#dataBody td {border: 1px solid #A6C9E2;} " +
					"#dataBody th {border: 1px solid white;background-color: #A6C9E2;} " +
					"#dataBody table {background-color: white;border: 1px solid #A6C9E2;}" +
					"</style></head>" +
					"<body><div id='dataBody'>");
			paramMap.put("orderBy", "insa.CREATE_DATE");
			resultList = (List<Map<String, Object>>) super.queryForList("businessSupport.getSatisfactionForEmail", paramMap);
			sb.append("理赔超过60天未结案列表：共计：" + (resultList == null ? 0 : resultList.size()) + "件。");
			if (resultList.size() > 0) {
				sb.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"3\" style=\"width: 1200px;\">");
				sb.append("<tr>");
				sb.append("<th>序号</th>");
				sb.append("<th>客户名称</th>");
				sb.append("<th>设备名称</th>");
				sb.append("<th>合同号</th>");
				sb.append("<th>保单号</th>");
				sb.append("<th>保险公司</th>");
				sb.append("<th>起始日期</th>");
				sb.append("<th>距今天数</th>");
				sb.append("<th>处理状态</th>");
				sb.append("<th>处理时间</th>");
				sb.append("</tr>");
				for (Map<String, Object> map : resultList) {
					index ++;
					sb.append("<tr>");
					sb.append("<td>" + index + "&nbsp;</td>");
					sb.append("<td>" + map.get("CUST_NAME") + "&nbsp;</td>");
					sb.append("<td>" + map.get("EQMT_ID") + "&nbsp;</td>");
					sb.append("<td>" + map.get("RECT_CODE") + "&nbsp;</td>");
					sb.append("<td>" + map.get("INCU_CODE") + "&nbsp;</td>");
					sb.append("<td>" + map.get("INCP_NAME") + "&nbsp;</td>");
					sb.append("<td>" + map.get("CREATE_DATE") + "&nbsp;</td>");
					sb.append("<td>" + map.get("DAY_DIFF") + "&nbsp;</td>");
					sb.append("<td>" + map.get("RECORD_REMARK") + "&nbsp;</td>");
					sb.append("<td>" + map.get("RECORD_DATE") + "&nbsp;</td>");
					sb.append("</tr>");
				}
				sb.append("</table>");
			}
			sb.append("<br/>");
			sb.append("</div></body></html>");
			MailSettingTo mailSettingTo = new MailSettingTo();
			mailSettingTo.setEmailContent(sb.toString().replace("null", ""));
			mailUtilService.sendMail(103, mailSettingTo);
		} catch (Exception e) {
			throw e;
		}
	}
	
	private void doSendEmail() throws Exception{
		List<Map<String, Object>> resultList = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		int index = 0;
		try {
			StringBuffer sb_top = new StringBuffer("<html>" +
					"<head><style type=\"text/css\">" +
					"#dataBody td {border: 1px solid #A6C9E2;} " +
					"#dataBody th {border: 1px solid white;background-color: #A6C9E2;} " +
					"#dataBody table {background-color: white;border: 1px solid #A6C9E2;}" +
					"</style></head>" +
					"<body><div id='dataBody'>");
			paramMap.put("orderBy", "u.EMAIL");
			resultList = (List<Map<String, Object>>) super.queryForList("businessSupport.getSatisfactionForEmail", paramMap);
			sb_top.append("理赔超过60天未结案列表：");
			if (resultList.size() > 0) {
				sb_top.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"3\" style=\"width: 1200px;\">");
				sb_top.append("<tr>");
				sb_top.append("<th>序号</th>");
				sb_top.append("<th>客户名称</th>");
				sb_top.append("<th>设备名称</th>");
				sb_top.append("<th>合同号</th>");
				sb_top.append("<th>保单号</th>");
				sb_top.append("<th>保险公司</th>");
				sb_top.append("<th>起始日期</th>");
				sb_top.append("<th>距今天数</th>");
				sb_top.append("<th>处理状态</th>");
				sb_top.append("<th>处理时间</th>");
				sb_top.append("</tr>");
				
				StringBuffer sb_bot = new StringBuffer("</table>");
				sb_bot.append("<br/>");
				sb_bot.append("</div></body></html>");
				
				StringBuffer sb_body = new StringBuffer();
				MailSettingTo mailSettingTo = new MailSettingTo();
				String mailTo = null;
				String mailCc = null;
				String temp = null;
				for (Map<String, Object> map : resultList) {
					temp = (String) map.get("EMAIL");
					if (temp == null) {
						continue;
					}
					if (!temp.equals(mailTo)) {
						index = 0;
						if (!StringUtils.isEmpty(sb_body.toString())){
							mailSettingTo.setEmailTo(mailTo);
							mailSettingTo.setEmailCc(mailCc);
							mailSettingTo.setEmailSubject("理赔超过60天未结案列表");
							mailSettingTo.setEmailContent(sb_top + sb_body.toString().replace("null", "") + sb_bot);
							mailUtilService.sendMail(mailSettingTo);
						}
						mailTo = temp;
						mailCc = (String) map.get("UP_EMAIL");
						sb_body = new StringBuffer();
						mailSettingTo = new MailSettingTo();
					}
					index ++;
					sb_body.append("<tr>");
					sb_body.append("<td>" + index + "&nbsp;</td>");
					sb_body.append("<td>" + map.get("CUST_NAME") + "&nbsp;</td>");
					sb_body.append("<td>" + map.get("EQMT_ID") + "&nbsp;</td>");
					sb_body.append("<td>" + map.get("RECT_CODE") + "&nbsp;</td>");
					sb_body.append("<td>" + map.get("INCU_CODE") + "&nbsp;</td>");
					sb_body.append("<td>" + map.get("INCP_NAME") + "&nbsp;</td>");
					sb_body.append("<td>" + map.get("CREATE_DATE") + "&nbsp;</td>");
					sb_body.append("<td>" + map.get("DAY_DIFF") + "&nbsp;</td>");
					sb_body.append("<td>" + map.get("RECORD_REMARK") + "&nbsp;</td>");
					sb_body.append("<td>" + map.get("RECORD_DATE") + "&nbsp;</td>");
					sb_body.append("</tr>");
				}
				if (!StringUtils.isEmpty(sb_body.toString())){
					mailSettingTo.setEmailTo(mailTo);
					mailSettingTo.setEmailCc(mailCc);
					mailSettingTo.setEmailSubject("理赔超过60天未结案列表");
					mailSettingTo.setEmailContent(sb_top + sb_body.toString().replace("null", "") + sb_bot);
					mailUtilService.sendMail(mailSettingTo);
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}
}
