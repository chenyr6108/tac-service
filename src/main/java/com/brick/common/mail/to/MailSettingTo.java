package com.brick.common.mail.to;

import java.util.ArrayList;
import java.util.List;

import com.brick.base.to.BaseTo;
import com.brick.employee.to.EmployeeTO;

public class MailSettingTo extends BaseTo {

	private static final long serialVersionUID=1L;

	private int id;
	private String emailFrom;
	private String emailTo;//邮件发送人
	private String emailCc;//邮件抄送人
	private String emailBcc;//邮件暗送人
	private String emailSubject;//邮件主题
	private String emailContent;//邮件内容
	private String emailAttachPath;//邮件附件地址  ,绝对路径
	private String sendTime;//邮件发送时间
	private int sendFlag;//发送邮件结果code
	private String sendResult;//发送邮件结果description
	private int sendCount;//发送次数
	private String emailType;//邮件所属功能code
	private String emailTypeGroup;//邮件所属功能分类code
	private String emailTypeDescr;//邮件所属功能description
	private String emailTypeGroupDescr;//邮件所属功能分类description
	private String deptId;//部门编号
	private String deptName;//部门名称
	private List<String> deptList = new ArrayList<String>();
	private String createBy;//邮件创建人的userId
	private String name;
	private String lastUpdatedBy;
	private String status;
	private String emailSendDate;
	private String emailSendTime;
	private String code;
	private String descr;
	private List<EmployeeTO> mailToList;
	private List<EmployeeTO> mailCcList;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getEmailFrom() {
		return emailFrom;
	}
	public void setEmailFrom(String emailFrom) {
		this.emailFrom = emailFrom;
	}
	public String getEmailTo() {
		return emailTo;
	}
	public void setEmailTo(String emailTo) {
		this.emailTo = emailTo;
	}
	public String getEmailCc() {
		if(emailCc==null) {
			emailCc="";
		}
		return emailCc;
	}
	public void setEmailCc(String emailCc) {
		this.emailCc = emailCc;
	}
	public String getEmailBcc() {
		if(emailBcc==null) {
			emailBcc="";
		}
		return emailBcc;
	}
	public void setEmailBcc(String emailBcc) {
		this.emailBcc = emailBcc;
	}
	public String getEmailSubject() {
		if(emailSubject==null) {
			emailSubject="";
		}
		return emailSubject;
	}
	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}
	public String getEmailContent() {
		if(emailContent==null) {
			emailContent="";
		}
		return emailContent;
	}
	public void setEmailContent(String emailContent) {
		this.emailContent = emailContent;
	}
	public String getEmailAttachPath() {
		return emailAttachPath;
	}
	public void setEmailAttachPath(String emailAttachPath) {
		this.emailAttachPath = emailAttachPath;
	}
	public String getSendTime() {
		return sendTime;
	}
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	public int getSendFlag() {
		return sendFlag;
	}
	public void setSendFlag(int sendFlag) {
		this.sendFlag = sendFlag;
	}
	public String getSendResult() {
		return sendResult;
	}
	public void setSendResult(String sendResult) {
		this.sendResult = sendResult;
	}
	public int getSendCount() {
		return sendCount;
	}
	public void setSendCount(int sendCount) {
		this.sendCount = sendCount;
	}
	public String getEmailType() {
		return emailType;
	}
	public void setEmailType(String emailType) {
		this.emailType = emailType;
	}
	public String getEmailTypeDescr() {
		return emailTypeDescr;
	}
	public void setEmailTypeDescr(String emailTypeDescr) {
		this.emailTypeDescr = emailTypeDescr;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}
	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getEmailSendDate() {
		return emailSendDate;
	}
	public void setEmailSendDate(String emailSendDate) {
		this.emailSendDate = emailSendDate;
	}
	public String getEmailSendTime() {
		return emailSendTime;
	}
	public void setEmailSendTime(String emailSendTime) {
		this.emailSendTime = emailSendTime;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public List<EmployeeTO> getMailToList() {
		return mailToList;
	}
	public void setMailToList(List<EmployeeTO> mailToList) {
		this.mailToList = mailToList;
	}
	public List<EmployeeTO> getMailCcList() {
		return mailCcList;
	}
	public void setMailCcList(List<EmployeeTO> mailCcList) {
		this.mailCcList = mailCcList;
	}
	public String getEmailTypeGroup() {
		return emailTypeGroup;
	}
	public void setEmailTypeGroup(String emailTypeGroup) {
		this.emailTypeGroup = emailTypeGroup;
	}
	public String getEmailTypeGroupDescr() {
		return emailTypeGroupDescr;
	}
	public void setEmailTypeGroupDescr(String emailTypeGroupDescr) {
		this.emailTypeGroupDescr = emailTypeGroupDescr;
	}
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
	public List<String> getDeptList() {
		return deptList;
	}
	public void setDeptList(List<String> deptList) {
		this.deptList = deptList;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	
}
