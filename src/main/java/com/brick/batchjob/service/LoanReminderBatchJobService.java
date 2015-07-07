package com.brick.batchjob.service;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.brick.activityLog.to.LoanTo;
import com.brick.base.service.BaseService;
import com.brick.batchjob.dao.LoanReminderBatchJobDAO;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.log.service.LogPrint;
import com.brick.util.DateUtil;

public class LoanReminderBatchJobService extends BaseService {

	Log logger=LogFactory.getLog(LoanReminderBatchJobService.class);

	private LoanReminderBatchJobDAO loanReminderBatchJobDAO;
	private MailUtilService mailUtilService;
	
	public LoanReminderBatchJobDAO getLoanReminderBatchJobDAO() {
		return loanReminderBatchJobDAO;
	}

	public void setLoanReminderBatchJobDAO(
			LoanReminderBatchJobDAO loanReminderBatchJobDAO) {
		this.loanReminderBatchJobDAO = loanReminderBatchJobDAO;
	}

	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}

	/**
	 * @author ShenQi
	 * @功能 委贷案件状态更新提醒
	 * */
	@Transactional(rollbackFor=Exception.class)
	public void batchJob() throws Exception {

		if(logger.isDebugEnabled()) {
			logger.debug("batch job for loan reminder start  --------------------");
		}

		Map<String,String> param=new HashMap<String,String>();
		try {
			if(!super.isWorkingDay()) {
				//如果是holiday不发送mail
				if(logger.isDebugEnabled()) {
					logger.debug("batch job for loan reminder not run in holiday  --------------------");
				}
				return;
			}
			
			//取出邮件内容数据源
			param.put("TYPE1","委贷类型");
			param.put("TYPE2","委贷案况");
			List<LoanTo> resultList=this.loanReminderBatchJobDAO.getLoanReminder(param);
			
			if(resultList.size()==0) {
				if(logger.isDebugEnabled()) {
					logger.debug("batch job for loan reminder end  --------------------");
				}
				return;
			}
			
			//添加邮件内容
			StringBuffer mailContent=new StringBuffer();
			
			mailContent.append("<html><head></head>");
			
			mailContent.append("<style>.rhead { background-color: #006699}" +
					".Body2 { font-family: Arial, Helvetica, sans-serif; font-weight: normal; color: #000000; font-size: 9pt; text-decoration: none}" +
					".Body2BoldWhite2 { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #FFFFFF; font-size: 11pt; text-decoration: none }" +
					".Body2Bold { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #000000; font-size: 8pt; text-decoration: none }" +
					".r11 {  background-color: #C4E2EC}" +
					".r12 { background-color: #D2EFF0}</style><body>");
			
			mailContent.append("<font size='3'><b>大家好:<b><br></font>" +
					"<font size='2'>以下委贷案件已到拨款日期,請記得進入系統中維護案件狀況:</font><br><br>");
			
			mailContent.append("<table border='1' cellspacing='0' width='1050px;' cellpadding='0'>" +
					"<tr class='rhead'>" +
					"<td class='Body2BoldWhite2' style='width:40px;text-align:center;' align='center'>序号</td>" +
					"<td class='Body2BoldWhite2' style='width:200px;text-align:center;' align='center'>客户名称</td>" +
					"<td class='Body2BoldWhite2' style='width:200px;text-align:center;' align='center'>介绍人</td>" +
					"<td class='Body2BoldWhite2' style='width:200px;text-align:center;' align='center'>供货商</td>" +
					"<td class='Body2BoldWhite2' align='center' style='text-align:center;'>租赁方式</td>" +
					"<td class='Body2BoldWhite2' style='width:100px;text-align:center;' align='center'>申请额度</td>" + 
					"<td class='Body2BoldWhite2' style='width:100px;text-align:center;' align='center'>利息</td>" + 
					"<td class='Body2BoldWhite2' style='width:100px;text-align:center;' align='center'>案件状况</td>" + 
					"<td class='Body2BoldWhite2' style='width:100px;text-align:center;' align='center'>经办人</td>" + 
					"<td class='Body2BoldWhite2' style='width:100px;text-align:center;' align='center'>拨款额度</td>" +
					"<td class='Body2BoldWhite2' style='width:100px;text-align:center;' align='center'>拨款日期</td>"
					);
			int num=0;
			
			Calendar cal=Calendar.getInstance();
			String date=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
			
			DecimalFormat df=new DecimalFormat("#,###.00");
			for(int i=0;i<resultList.size();i++) {
				num++;
				mailContent.append("<tr class='r12'>");
				mailContent.append("<td class=body2 style='text-align:center;'>"+num+"</td>" +
								   "<td class=body2 style='text-align:center;'>"+resultList.get(i).getCustCodeDescr()+"</td>" +
								   "<td class=body2 style='text-align:center;'>"+resultList.get(i).getIntroducer()+"</td>" +
								   "<td class=body2 style='text-align:center;'>"+resultList.get(i).getSupName()+"</td>" +
								   "<td class=body2 style='text-align:center;'>"+resultList.get(i).getLoanModeDescr()+"</td>" +
								   "<td class=body2 style='text-align:right;'>"+df.format((Double.valueOf(resultList.get(i).getCostMoney().toString())-Double.valueOf(resultList.get(i).getCautionMoney().toString())))+"￥"+"</td>" +
								   "<td class=body2 style='text-align:right;'>"+df.format(Double.valueOf(resultList.get(i).getAccrual().toString()))+"￥"+"</td>" +
								   "<td class=body2 style='text-align:center;'>"+resultList.get(i).getCaseStatusDescr()+"</td>" +
								   "<td class=body2 style='text-align:center;'>"+resultList.get(i).getUserName()+"</td>" +
								   "<td class=body2 style='text-align:right;'>"+df.format(Double.valueOf(resultList.get(i).getPayMoney().toString()))+"￥"+"</td>");
								   if(date.equals(resultList.get(i).getPayDateDescr())) {
									   mailContent.append("<td class=body2 style='text-align:center;'>"+resultList.get(i).getPayDateDescr()+"</td>");
								   } else {
									   mailContent.append("<td class=body2 style='text-align:center;color:#FF0000;'>"+resultList.get(i).getPayDateDescr()+"</td>");
								   }
				mailContent.append("</tr>");
			}
			mailContent.append("</table><br><br>");
			
			mailContent.append("<a href='http://ap2.tacleasing.cn:8088/financelease' class=body2>登录租赁系统</a>");
			
			MailSettingTo mailSettingTo=new MailSettingTo();
			mailSettingTo.setEmailContent(mailContent.toString());
			this.mailUtilService.sendMail(2,mailSettingTo);
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e,logger);
			throw e;
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("batch job for loan reminder end  --------------------");
		}
	}
}
