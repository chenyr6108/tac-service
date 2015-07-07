package com.brick.batchjob.service;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.brick.base.util.BirtReportEngine;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.util.DateUtil;

public class SupplierLevelBatchJobService {
	
	Logger logger=Logger.getLogger(this.getClass());
	
	private BirtReportEngine birtReportEngine;
	private MailUtilService mailUtilService;
	
	public BirtReportEngine getBirtReportEngine() {
		return birtReportEngine;
	}
	public void setBirtReportEngine(BirtReportEngine birtReportEngine) {
		this.birtReportEngine = birtReportEngine;
	}
	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}
	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}
	
	public void sendSupplierLevelMailForPayMoneyByMonth() {
		String fileName="";
		
		try {
			Calendar cal=Calendar.getInstance();
			cal.add(Calendar.MONTH,-1);
			fileName="每月拨款案件供应商评级信息"+File.separator+DateUtil.dateToString(cal.getTime(),"yyyy-MM")+".xls";
			
			birtReportEngine.executeReport("batchJob/supplierLevel.rptdesign",fileName,null);
			
			MailSettingTo mailSettingTo=new MailSettingTo();
			mailSettingTo.setEmailAttachPath(birtReportEngine.getOutputPath()+File.separator+fileName);
			mailUtilService.sendMail(10,mailSettingTo);
			
		} catch(Exception e) {
			logger.debug("每月拨款案件供应商评级信息生成出错!");
		}
	}
	
	public static List<Map<String,Object>> getSupplierLevelForPayMoneyByMonth() {
		
		List<Map<String,Object>> resultList=null;
		
		try {
			resultList=(List<Map<String,Object>>)DataAccessor.query("businessReport.getSupplierLevelForPayMoneyByMonth",null,RS_TYPE.LIST);
		} catch (Exception e) {
		}
		
		return resultList;
	}
}
