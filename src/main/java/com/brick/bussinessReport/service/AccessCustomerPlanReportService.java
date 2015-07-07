package com.brick.bussinessReport.service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.service.BaseService;
import com.brick.base.util.LeaseUtil;
import com.brick.bussinessReport.dao.AccessCustomerPlanReportDAO;
import com.brick.bussinessReport.to.AccessCustomerPlanReportTO;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.util.DateUtil;
import com.brick.util.poi.ExcelPOI;

public class AccessCustomerPlanReportService extends BaseService {
	
	Log logger=LogFactory.getLog(AccessCustomerPlanReportService.class);
	
	private AccessCustomerPlanReportDAO accessCustomerPlanReportDAO;
	private MailUtilService mailUtilService;
	
	public AccessCustomerPlanReportDAO getAccessCustomerPlanReportDAO() {
		return accessCustomerPlanReportDAO;
	}

	public void setAccessCustomerPlanReportDAO(
			AccessCustomerPlanReportDAO accessCustomerPlanReportDAO) {
		this.accessCustomerPlanReportDAO = accessCustomerPlanReportDAO;
	}

	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}

	public void createReport() throws Exception {
	
		List<AccessCustomerPlanReportTO> resultList=null;
		try {
			logger.debug("客户拜访计划Job----------开始");
			resultList=this.accessCustomerPlanReportDAO.accessCustomerPlanReportList();
			
			Map<String,String> param=new HashMap<String,String>();
			Calendar cal=Calendar.getInstance();
			cal.add(Calendar.DATE,-3);
			param.put("TO_DATE",DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd"));
			cal.add(Calendar.DATE,-4);
			param.put("FROM_DATE",DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd"));
			logger.debug("客户拜访计划Job----------创建附件开始");
			ExcelPOI excel=new ExcelPOI();
			File file=new File("//"+LeaseUtil.getIPAddress()+"/home/filsoft/financelease/upload/accessCustomerPlanReport/report("+param.get("FROM_DATE")+"~"+param.get("TO_DATE")+").xls");
			logger.debug("客户拜访计划Job----------创建附件结束");
			logger.debug("客户拜访计划Job----------写入Excel开始");
			FileOutputStream stream=new FileOutputStream(file);
			logger.debug("客户拜访计划Job----------写入Excel结束");
			
			excel.accessCustomerPlanReport(resultList,param).write(stream);
			
			MailSettingTo mailSettingTo=new MailSettingTo();
			mailSettingTo.setEmailSubject("客户拜访计划("+param.get("FROM_DATE")+"~"+param.get("TO_DATE")+")");
			mailSettingTo.setEmailAttachPath("\\\\"+LeaseUtil.getIPAddress()+"\\home\\filsoft\\financelease\\upload\\accessCustomerPlanReport\\report("+param.get("FROM_DATE")+"~"+param.get("TO_DATE")+").xls");
			this.mailUtilService.sendMail(4,mailSettingTo);
			logger.debug("客户拜访计划Job----------结束");
		} catch (Exception e) {
			logger.debug("每周客户拜访计划Job出错!");
			throw e;
		}
	}
}
