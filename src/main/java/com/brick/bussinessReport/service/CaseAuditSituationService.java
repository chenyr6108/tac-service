package com.brick.bussinessReport.service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import com.brick.base.service.BaseService;
import com.brick.base.util.LeaseUtil;
import com.brick.bussinessReport.dao.CaseAuditSituationDAO;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.util.DateUtil;
import com.brick.util.poi.ExcelPOI;

public class CaseAuditSituationService extends BaseService {
	
	private CaseAuditSituationDAO caseAuditSituationDAO;
	private MailUtilService mailUtilService;
	
	public CaseAuditSituationDAO getCaseAuditSituationDAO() {
		return caseAuditSituationDAO;
	}

	public void setCaseAuditSituationDAO(CaseAuditSituationDAO caseAuditSituationDAO) {
		this.caseAuditSituationDAO = caseAuditSituationDAO;
	}

	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}

	public void createReport() throws Exception {
		
		//邮件每个工作日16:00:00发
		List<Map<String,Object>> resultList1=null;//业管前一天工作日 15:00:00后审核通过的,财务未审核的案子
		List<Map<String,Object>> resultList2=null;//业管当天工作日15:00:00前审核通过的,财务未审核的案子      
		
		try {
			if(!super.isWorkingDay()) {//非工作日不发送
				return;
			}
			resultList1=this.caseAuditSituationDAO.getLastWorkingDaySituation();
			resultList2=this.caseAuditSituationDAO.getCurrentWorkingDaySituation();
			
			ExcelPOI excel=new ExcelPOI();
			File file=new File("//"+LeaseUtil.getIPAddress()+"/home/filsoft/financelease/upload/caseAudit/caseAuditSituation("+DateUtil.getCurrentDate()+").xls");
			FileOutputStream stream=new FileOutputStream(file);
			excel.caseAuditSituationReport(resultList1,resultList2).write(stream);
			
			MailSettingTo mailSettingTo=new MailSettingTo();
			mailSettingTo.setEmailSubject("案件审核提醒"+DateUtil.getCurrentDate());
			mailSettingTo.setEmailAttachPath("\\\\"+LeaseUtil.getIPAddress()+"\\home\\filsoft\\financelease\\upload\\caseAudit\\caseAuditSituation("+DateUtil.getCurrentDate()+").xls");
			this.mailUtilService.sendMail(15,mailSettingTo);
			
		} catch(Exception e) {
			
		}
	}
}
