package com.brick.dun.serviceService;
import java.io.File;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.exception.DaoException;
import com.brick.base.exception.ServiceException;
import com.brick.base.service.BaseService;
import com.brick.base.util.BirtReportEngine;
import com.brick.base.util.LeaseUtil;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.insurance.to.InsuCompanyTo;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.util.Constants;
import com.brick.util.DateUtil;
public class DunTaskLawService  extends BaseService {
	Log logger = LogFactory.getLog(DunTaskLawService.class);
	
	private BirtReportEngine birt;
	private MailUtilService mailUtilService;
	//private SendEmailForException exceptionUtil;
	
	public BirtReportEngine getBirt() {
		return birt;
	}

	public void setBirt(BirtReportEngine birt) {
		this.birt = birt;
	}
	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}
	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}
	@Transactional(rollbackFor=Exception.class)
	public void getLawLetter(Context context) throws Exception{
		String dateStr = DateUtil.dateToString(new Date(), "yyyyMMddHHmmSSS");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("RECTID",context.contextMap.get("RECTID") );
		paramMap.put("dun_date", context.contextMap.get("dun_date") );
		String fileName = null;
		Map<String, Object> mapContent = new HashMap<String, Object>();
		paramMap.put("rectId",context.contextMap.get("RECTID") );
		try {
			mapContent = (Map<String,Object>)DataAccessor.query("dunTask.getLetterContentByRectId", paramMap, RS_TYPE.MAP);
			fileName = DateUtil.dateToString(new Date(), "yyyy_MM_dd") + File.separator+ "" + dateStr + ".doc";
			// 律师函
			String leaseCode = mapContent.get("LEASE_CODE")==null?"":mapContent.get("LEASE_CODE").toString();
			if(leaseCode.indexOf("0509") == 0 || leaseCode.indexOf("0507") == 0){
				//小车委贷
				birt.executeReport("lawLetter/letterCar.rptdesign", fileName, paramMap);
			} else {
				//公司别
				String companyCode = mapContent.get("COMPANY_CODE").toString()==null?"1":mapContent.get("COMPANY_CODE").toString();
				if("1".equals(companyCode)){
					birt.executeReport("lawLetter/letter.rptdesign", fileName, paramMap);
				}else{
					birt.executeReport("lawLetter/letter2.rptdesign", fileName, paramMap);
				}				
			}
		} catch (Exception e) {
			throw new Exception("律师函-" + e.getMessage());
		}
		// 发送Email
		try {
			//增加审核数据
			//查询总计份数
			int companyNum =(Integer)queryForObj("dunTask.getCompanyCountByRectId", paramMap);
			int personNum =(Integer)queryForObj("dunTask.getNatureCountByRectId", paramMap);
			String creditId = mapContent.get("CREDIT_ID").toString();
			int procudtionType = LeaseUtil.getProductionTypeByCreditId(creditId);
			if(procudtionType == 3){//乘用车，则需要查询共同还款人
				paramMap.put("credit_id", creditId);
				String mateName = (String) queryForObj("dunTask.getMateNameByCreditId",paramMap);
				if(mateName!=null && !"".equals(mateName.trim())){
					if(personNum>0){//判断 共同还款人是否在担保人中
						paramMap.put("mateName", mateName);
						int count = (Integer)queryForObj("dunTask.getNatuCounntByName", paramMap);
						if(count==0){
							companyNum++;
						}
					}else{
						companyNum++;
					}
				}
			}
			Map<String, Object> auditMap = new HashMap<String, Object>();
			auditMap.put("DAILY_ID",mapContent.get("DAILY_ID"));
			auditMap.put("APPLY_ID",context.contextMap.get("s_employeeId"));
			auditMap.put("APPLY_NAME",context.contextMap.get("s_employeeName"));
			auditMap.put("STATUS","0");
			auditMap.put("SEND_NUM",companyNum+personNum+1);
			auditMap.put("AUDIT_TYPE",Constants.AUDIT_TYPE_LAW);
			insert("dunTask.insertDunAudit", auditMap);
			MailSettingTo mailSettingTo = new MailSettingTo();
			//律师函审批的主管
			mailSettingTo.setEmailTo((String)context.contextMap.get("UPPER_EMAIL"));
			//抄送业务员和业务员的主管和申请人
			mailSettingTo.setEmailCc((String)context.contextMap.get("EMAIL")+";"+(String)mapContent.get("EMAIL")+";"+(String)mapContent.get("UPEMAIL"));
			mailSettingTo.setEmailAttachPath(birt.getOutputPath() + File.separator + fileName);
			mailSettingTo.setEmailSubject("[法务课]律师函寄发申请");
			mailSettingTo.setEmailContent(getMailContent(mapContent,(String)paramMap.get("dun_date")));
			mailUtilService.sendMail(mailSettingTo);
			
			
			//插入催收记录之中
			String custCode = (String) this.baseDAO.queryForObjUseMap("rentContract.getCustCodeByRectId", paramMap);
			Map dunRecord =  new HashMap();
			dunRecord.put("clerk_id", context.request.getSession().getAttribute("s_employeeId"));
			dunRecord.put("ANSWERPHONE_NAME", "000");
			dunRecord.put("PHONE_NUMBER", "000");
			dunRecord.put("RESULT", 16);
			dunRecord.put("CALL_CONTENT", "已申请寄发律师函。");
			dunRecord.put("CUST_CODE", custCode);
			this.baseDAO.insert("dunTask.addDunRecord", dunRecord);	
			
		} catch (Exception e) {
			throw new Exception("律师函-" + e.getMessage());
		}
		
	}
	private String getMailContent(Map<String, Object> mapContent,String dunDate){
		if (mapContent == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
			sb.append("<html><head></head>");
			sb.append("<font size='3'><b>您好:<b><br></font>" + "<font size='3'>有一張律师函寄发申请，需您审批，詳細資訊如下：</font><br><br><br>"
			+"<br><font size='2'>律师函生成日期:</font>" +dunDate
			+"<br><font size='2'>承租人名称：</font>"+mapContent.get("CUST_NAME")+
			"<br><font size='2'>合同號:</font>" +mapContent.get("LEASE_CODE")+
			"<br><font size='2'>逾期天数：</font>"+mapContent.get("DUN_DAY"));
			sb.append("</html>");
		return sb.toString();
	}
	//驳回
	public void nopassLaw(Context context) throws ServiceException{
		Map<String, Object> auditMap = new HashMap<String, Object>();
		auditMap.put("AUDIT_ID",context.contextMap.get("AUDIT_ID"));
		auditMap.put("UPPER_ID",context.contextMap.get("s_employeeId"));
		auditMap.put("STATUS","2");
		update("dunTask.updateDunAudit", auditMap);
		try {
		//插入催收记录之中
		Map audit =(Map)DataAccessor.query("dunTask.getAuditByAuditId", context.contextMap, RS_TYPE.MAP);
		Map dunRecord =  new HashMap();
		dunRecord.put("clerk_id", context.request.getSession().getAttribute("s_employeeId"));
		dunRecord.put("ANSWERPHONE_NAME", "000");
		dunRecord.put("PHONE_NUMBER", "000");
		dunRecord.put("RESULT", 16);
		dunRecord.put("CALL_CONTENT", "已驳回律师函。");
		dunRecord.put("CUST_CODE", audit.get("CUST_CODE"));	
			this.baseDAO.insert("dunTask.addDunRecord", dunRecord);
		} catch (Exception e) {
			e.printStackTrace();
		}	 
		

	}
	//律师函委寄通过
	@Transactional(rollbackFor=Exception.class)
	public void sendLaw(Context context) throws Exception{
		//查询案件的recpId
		Map audit =(Map)DataAccessor.query("dunTask.getAuditByAuditId", context.contextMap, RS_TYPE.MAP);
		Map<String, Object> auditMap = new HashMap<String, Object>();
		auditMap.put("AUDIT_ID",context.contextMap.get("AUDIT_ID"));
		auditMap.put("UPPER_ID",context.contextMap.get("s_employeeId"));
		auditMap.put("STATUS","1");
		update("dunTask.updateDunAudit", auditMap);
		//添加律师函寄送费用
		 	int letterNum =Integer.valueOf(String.valueOf(audit.get("SEND_NUM")));
			Map<String, Object> lawFee = new HashMap<String, Object>();
			lawFee.put("RECP_ID",audit.get("RECP_ID"));
			lawFee.put("FEE_NAME","lawyer_letter_fee");
			lawFee.put("FEE_VALUE",letterNum*25);
			lawFee.put("MEMO","系统生成委寄律师函费"+letterNum+"份");
			lawFee.put("s_employeeId",audit.get("APPLY_ID"));
		     insert("collectionManage.createLawFee", lawFee) ;
		     Map<String, Object> dunRecord = new HashMap<String, Object>();
		     dunRecord.put("ANSWERPHONE_NAME", "000");
		     dunRecord.put("s_employeeId",audit.get("APPLY_ID"));
		     dunRecord.put("PHONE_NUMBER", "000");
		     dunRecord.put("CUST_CODE", audit.get("CUST_CODE"));
		     dunRecord.put("LAWYFEERECORD", "律师函"+":"+lawFee.get("FEE_VALUE")+"元。备注："+lawFee.get("MEMO"));
			 insert("collectionManage.createDunRecord", dunRecord) ;
		
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void getOutVisit(Context context) throws Exception{
		String dateStr = DateUtil.dateToString(new Date(), "yyyyMMddHHmmSSS");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("RECTID",context.contextMap.get("RECTID") );
		paramMap.put("dun_date", context.contextMap.get("dun_date"));
		paramMap.put("out", "false");
		String fileName ="";
		try {
			//锁码方式
			Map<String, String> lockCodeMap = (Map<String,String>)DataAccessor.query("dunTask.getLockCodeByRectId", paramMap, RS_TYPE.MAP);
			paramMap.put("LOCK_CODE", lockCodeMap.get("FLAG")==null?"无":lockCodeMap.get("FLAG").toString());
			fileName = DateUtil.dateToString(new Date(), "yyyy_MM_dd") + File.separator+ "" + dateStr + ".doc";
			//委外回访记录表
			birt.executeReport("lawLetter/outVisitLog.rptdesign", fileName, paramMap);
		} catch (Exception e) {
			throw new Exception("委外回访记录表-" + e.getMessage());
		}
		// 发送Email
		try {
			Map<String, Object> mapContent = new HashMap<String, Object>();
			paramMap.put("rectId",context.contextMap.get("RECTID") );
			mapContent = (Map<String,Object>)DataAccessor.query("dunTask.getLetterContentByRectId", paramMap, RS_TYPE.MAP);
			//增加审核数据
			Map<String, Object> auditMap = new HashMap<String, Object>();
			auditMap.put("DAILY_ID",mapContent.get("DAILY_ID"));
			auditMap.put("APPLY_ID",context.contextMap.get("s_employeeId"));
			auditMap.put("APPLY_NAME",context.contextMap.get("s_employeeName"));
			auditMap.put("STATUS","0");
			auditMap.put("AUDIT_TYPE",Constants.AUDIT_TYPE_OUTVISIT);
			insert("dunTask.insertDunAudit", auditMap);
			MailSettingTo mailSettingTo = new MailSettingTo();
			 String title="有一份委外回访申请，需您审批，詳細資訊如下：";
			//委外回访审批的主管
			mailSettingTo.setEmailTo((String)context.contextMap.get("UPPER_EMAIL"));
			//抄送业务员和业务员的主管和申请人
			mailSettingTo.setEmailCc((String)context.contextMap.get("EMAIL")+";"+(String)mapContent.get("EMAIL")+";"+(String)mapContent.get("UPEMAIL"));
			mailSettingTo.setEmailAttachPath(birt.getOutputPath() + File.separator + fileName);
			mailSettingTo.setEmailSubject("[法务课]委外回访申请");
			mailSettingTo.setEmailContent(getOutVisitMailContent(mapContent,(String)paramMap.get("dun_date"),title));
			mailUtilService.sendMail(mailSettingTo);
			
			String custCode = (String) this.baseDAO.queryForObjUseMap("rentContract.getCustCodeByRectId", paramMap);
			//插入催收记录之中
			Map dunRecord =  new HashMap();
			dunRecord.put("clerk_id", context.request.getSession().getAttribute("s_employeeId"));
			dunRecord.put("ANSWERPHONE_NAME", "000");
			dunRecord.put("PHONE_NUMBER", "000");
			dunRecord.put("RESULT", 16);
			dunRecord.put("CALL_CONTENT", "已申请委访。");
			dunRecord.put("CUST_CODE", custCode);
			this.baseDAO.insert("dunTask.addDunRecord", dunRecord);	
		} catch (Exception e) {
			throw new Exception("委外回访记录表邮件-" + e.getMessage());
		}
		
	}

	//通过委外申请
	@Transactional(rollbackFor=Exception.class)
	public void getPassOutVisit(Context context) throws Exception{
		
		Map audit =(Map)DataAccessor.query("dunTask.getAuditByAuditId", context.contextMap, RS_TYPE.MAP);
		Map<String, Object> auditMap = new HashMap<String, Object>();
		auditMap.put("AUDIT_ID",context.contextMap.get("AUDIT_ID"));
		auditMap.put("UPPER_ID",context.contextMap.get("s_employeeId"));
		auditMap.put("STATUS","1");
		update("dunTask.updateDunAudit", auditMap);
		Map<String, Object> dunRecord = new HashMap<String, Object>();
		     dunRecord.put("ANSWERPHONE_NAME", "000");
		     dunRecord.put("s_employeeId",audit.get("APPLY_ID"));
		     dunRecord.put("PHONE_NUMBER", "000");
		     dunRecord.put("CUST_CODE", audit.get("CUST_CODE"));
		     dunRecord.put("LAWYFEERECORD", "通过委外回访申请。");
			 insert("collectionManage.createDunRecord", dunRecord) ;
			  	
		String dateStr = DateUtil.dateToString(new Date(), "yyyyMMddHHmmSSS");
		//通过context.contextMap.get("AUDIT_ID")得到RECT
		Map<String, Object> dunDaily = new HashMap<String, Object>();
		dunDaily = (Map<String,Object>)DataAccessor.query("dunTask.getDunDailyByDailyId", context.contextMap, RS_TYPE.MAP);
		if(dunDaily!=null){
			//前一天
			DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
			//得到前一天为逾期日期
			Calendar calendar = Calendar.getInstance();//
	        calendar.add(Calendar.DATE, -1);    
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("RECTID",dunDaily.get("RECT_ID"));
			paramMap.put("dun_date", format.format(calendar.getTime()));
			paramMap.put("out", "true");
			String fileName ="";
			try {
				//锁码方式
				Map<String, String> lockCodeMap = (Map<String,String>)DataAccessor.query("dunTask.getLockCodeByRectId", paramMap, RS_TYPE.MAP);
				paramMap.put("LOCK_CODE", lockCodeMap.get("FLAG")==null?"无":lockCodeMap.get("FLAG").toString());
				fileName = DateUtil.dateToString(new Date(), "yyyy_MM_dd") + File.separator+ "" + dateStr + ".doc";
				//委外回访记录表
				birt.executeReport("lawLetter/outVisitLog.rptdesign", fileName, paramMap);
			} catch (Exception e) {
				throw new Exception("委外回访记录表-" + e.getMessage());
			}
			Map<String, Object> mapContent = new HashMap<String, Object>();
			paramMap.put("rectId",dunDaily.get("RECT_ID"));
			mapContent = (Map<String,Object>)DataAccessor.query("dunTask.getLetterContentByRectId", paramMap, RS_TYPE.MAP);
		    MailSettingTo mailSettingTo = new MailSettingTo();
		    String title="委外回访申请已通过，詳細資訊如下";
		    //申请人
			mailSettingTo.setEmailTo((String)dunDaily.get("APPLY_ID_EMAIL"));
			//委外回访审批的主管抄送
			mailSettingTo.setEmailCc((String)context.contextMap.get("EMAIL"));
			mailSettingTo.setEmailAttachPath(birt.getOutputPath() + File.separator + fileName);
			mailSettingTo.setEmailSubject("[法务课]委外回访申请已通过，请查看。");
			mailSettingTo.setEmailContent(getOutVisitMailContent(mapContent,(String)paramMap.get("dun_date"),title));
			mailUtilService.sendMail(mailSettingTo);
			
		}
	}
	//委外回访驳回
	@Transactional(rollbackFor=Exception.class)
	public void nopassOutVisit(Context context) throws Exception{
			Map<String, Object> auditMap = new HashMap<String, Object>();
			auditMap.put("AUDIT_ID",context.contextMap.get("AUDIT_ID"));
			auditMap.put("UPPER_ID",context.contextMap.get("s_employeeId"));
			auditMap.put("STATUS","2");
			update("dunTask.updateDunAudit", auditMap);
			
			Map audit =(Map)DataAccessor.query("dunTask.getAuditByAuditId", context.contextMap, RS_TYPE.MAP);
			Map dunRecord =  new HashMap();
			dunRecord.put("clerk_id", context.request.getSession().getAttribute("s_employeeId"));
			dunRecord.put("ANSWERPHONE_NAME", "000");
			dunRecord.put("PHONE_NUMBER", "000");
			dunRecord.put("RESULT", 16);
			dunRecord.put("CALL_CONTENT", "已驳回委外回访申请。");
			dunRecord.put("CUST_CODE", audit.get("CUST_CODE"));
			this.baseDAO.insert("dunTask.addDunRecord", dunRecord);	 
			
			 Map<String, Object> dunDaily = (Map<String,Object>)DataAccessor.query("dunTask.getDunDailyByDailyId", context.contextMap, RS_TYPE.MAP);
			if(dunDaily!=null){
				Map<String, Object> mapContent = new HashMap<String, Object>();
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("dun_date",dunDaily.get("DUN_DATE"));
				paramMap.put("rectId",dunDaily.get("RECT_ID"));
				mapContent = (Map<String,Object>)DataAccessor.query("dunTask.getLetterContentByRectId", paramMap, RS_TYPE.MAP);
				MailSettingTo mailSettingTo = new MailSettingTo();
				String title="有一張委外回访申请，已被驳回，詳細資訊如下";
				//申请人
				mailSettingTo.setEmailTo((String)dunDaily.get("APPLY_ID_EMAIL"));
				//委外回访审批的主管抄送
				mailSettingTo.setEmailCc((String)context.contextMap.get("EMAIL"));
				mailSettingTo.setEmailSubject("[法务课]委外回访申请已被驳回，请查看。");
				mailSettingTo.setEmailContent(getOutVisitMailContent(mapContent,(String)dunDaily.get("DUN_DATE"),title));
				mailUtilService.sendMail(mailSettingTo);
			}
		}
		//邮件内容
		private String getOutVisitMailContent(Map<String, Object> mapContent,String dunDate,String title){
			if (mapContent == null) {
				return null;
			}
			StringBuffer sb = new StringBuffer();
				sb.append("<html><head></head>");
				sb.append("<font size='3'><b>您好:<b><br></font>" + "<font size='3'>"+title+"：</font><br><br><br>"
				+"<br><font size='2'>逾期日期:</font>" +dunDate
				+"<br><font size='2'>承租人名称：</font>"+mapContent.get("CUST_NAME")+
				"<br><font size='2'>合同號:</font>" +mapContent.get("LEASE_CODE")+
				"<br><font size='2'>逾期天数：</font>"+mapContent.get("DUN_DAY"));
				sb.append("</html>");
			return sb.toString();
		}
		
}
