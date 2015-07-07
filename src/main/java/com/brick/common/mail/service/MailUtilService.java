package com.brick.common.mail.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.exception.ServiceException;
import com.brick.base.service.BaseService;
import com.brick.common.mail.dao.MailSettingDAO;
import com.brick.common.mail.dao.MailUtilDAO;
import com.brick.common.mail.to.EmailPlanTO;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.log.service.LogPrint;
import com.brick.util.Constants;

public class MailUtilService extends BaseService {

	Log logger=LogFactory.getLog(MailUtilService.class);
	private MailUtilDAO mailUtilDAO;
	private MailSettingDAO mailSettingDAO;
    
	public MailUtilDAO getMailUtilDAO() {
		return mailUtilDAO;
	}
	public void setMailUtilDAO(MailUtilDAO mailUtilDAO) {
		this.mailUtilDAO = mailUtilDAO;
	}	
	public MailSettingDAO getMailSettingDAO() {
		return mailSettingDAO;
	}
	public void setMailSettingDAO(MailSettingDAO mailSettingDAO) {
		this.mailSettingDAO = mailSettingDAO;
	}
	/**
	 * 参数说明
	 *emailTo邮件发送给
	 *emailCc邮件抄送给
	 *emailBcc邮件暗送给
	 *emailSubject邮件主题
	 *emailContent邮件内容
	 *emailAttachPath邮件附件路径
	 *createBy 创建人的userId
	 * @author shenqi
	 * @throws Exception 
	 * */
	public boolean sendMail(MailSettingTo mailSettingTo) throws ServiceException {
		
		try {
			if("null".equals(mailSettingTo.getEmailTo())) {
				return false;
			}
			mailSettingTo.setEmailFrom(mailSettingTo.getEmailFrom()==null?Constants.EMAIL_FROM:mailSettingTo.getEmailFrom());
			mailSettingTo.setSendFlag(0);//0表示未发送
			mailSettingTo.setSendCount(0);//邮件发送次数
			mailUtilDAO.insertEmailSendRecord(mailSettingTo);
			return true;
		} catch(Exception e) {
			logger.error(e);
			throw new ServiceException(e);
		}
	}
	
	public boolean sendMail(int mailType,MailSettingTo mailSettingTo) throws Exception {
		
		try {
			MailSettingTo insetTo=this.getEmailInfoByEmailType(mailType);
			if(insetTo.getId()==0) {
				//如果通过email所属功能无法找到则返回false
				return false;
			}
			insetTo.setEmailType(String.valueOf(mailType));
			insetTo.setSendFlag(0);//0表示未发送
			insetTo.setSendCount(0);
			if(mailSettingTo==null) {
				
			} else {
				if(mailSettingTo.getEmailTo()!=null&&!"".equals(mailSettingTo.getEmailTo())) {
					insetTo.setEmailTo(mailSettingTo.getEmailTo());
				}
				if(mailSettingTo.getEmailCc()!=null&&!"".equals(mailSettingTo.getEmailCc())) {
					insetTo.setEmailCc(mailSettingTo.getEmailCc());
				}
				if(mailSettingTo.getEmailBcc()!=null&&!"".equals(mailSettingTo.getEmailBcc())) {
					insetTo.setEmailBcc(mailSettingTo.getEmailBcc());
				}
				if(mailSettingTo.getEmailSubject()!=null&&!"".equals(mailSettingTo.getEmailSubject())) {
					insetTo.setEmailSubject(mailSettingTo.getEmailSubject());
				}
				if(mailSettingTo.getEmailContent()!=null&&!"".equals(mailSettingTo.getEmailContent())) {
					insetTo.setEmailContent(mailSettingTo.getEmailContent());
				}
				if(mailSettingTo.getEmailAttachPath()!=null&&!"".equals(mailSettingTo.getEmailAttachPath())) {
					insetTo.setEmailAttachPath(mailSettingTo.getEmailAttachPath());
				}
			}
			mailUtilDAO.insertEmailSendRecord(insetTo);
			return true;
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
			throw e;
		}
	}
	
	//通过邮件所属功能找到T_SET_EMAIL中储存的邮件信息
	private MailSettingTo getEmailInfoByEmailType(int mailType) throws Exception {
		return this.mailSettingDAO.getEmailInfoByEmailType(mailType);
	}
	
	public void sendEmailPlanByType(int type) throws Exception{
		List<EmailPlanTO> emailPlans = mailUtilDAO.queryEmailPlansByType(type);
		if(emailPlans!=null && emailPlans.size()>0){
			for(EmailPlanTO emailPlan:emailPlans){
				MailSettingTo mailSettingTo = new MailSettingTo();
				mailSettingTo.setEmailContent(emailPlan.getEmailContent());
				mailSettingTo.setEmailTo(emailPlan.getEmailTo());
				mailSettingTo.setEmailBcc(emailPlan.getEmailBcc());
				mailSettingTo.setEmailCc(emailPlan.getEmailCc());
				mailSettingTo.setEmailAttachPath(emailPlan.getEmailAttachPath());
				mailSettingTo.setEmailSubject(emailPlan.getEmailSubject());
				if(emailPlan.getEmailType()!=null && emailPlan.getEmailType()>0){;
					this.sendMail(emailPlan.getEmailType(), mailSettingTo);
				}else{
					this.sendMail(mailSettingTo);
				}
				mailUtilDAO.finsihEmailPlan(emailPlan.getId());				
			}
		}
	}
}
