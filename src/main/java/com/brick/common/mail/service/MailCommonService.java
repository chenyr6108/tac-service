package com.brick.common.mail.service;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.transaction.annotation.Transactional;

import com.brick.common.mail.dao.MailSettingDAO;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.log.service.LogPrint;
import com.brick.util.Constants;

public class MailCommonService {
	
    private String host;
    private String username;
    private String password;
    private int id;
    private int sendCount;
	private MailSettingDAO mailSettingDAO;
	private String subject;
	Log logger=LogFactory.getLog(MailCommonService.class);
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host=host;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username=username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password=password;
	}

	public MailSettingDAO getMailSettingDAO() {
		return mailSettingDAO;
	}

	public void setMailSettingDAO(MailSettingDAO mailSettingDAO) {
		this.mailSettingDAO=mailSettingDAO;
	}

	private SimpleEmail getSimpleEmail(String encode) {

		SimpleEmail simpleEmail=new SimpleEmail();
		//设置邮件编码
		simpleEmail.setCharset(encode);
		//设置邮件服务器
		simpleEmail.setHostName(this.host);
		//设置登录邮件服务器用户名和密码
		simpleEmail.setAuthentication(this.username,this.password);
		return simpleEmail;
	}
	
	private HtmlEmail getHtmlEmail(String encode) {

		HtmlEmail htmlEmail=new HtmlEmail();
		//设置邮件编码
		htmlEmail.setCharset(encode);
		//设置邮件服务器
		htmlEmail.setHostName(this.host);
		//设置登录邮件服务器用户名和密码
		htmlEmail.setAuthentication(this.username,this.password);
		return htmlEmail;
	}
	
	private MailSettingTo getEmailInfoByEmailType(int emailType) throws Exception {
		return this.mailSettingDAO.getEmailInfoByEmailType(emailType);
	}
	
	private void sendErrorMail(String errorMessage) throws Exception {
		
		SimpleEmail simpleEmail=this.getSimpleEmail("UTF-8");
		//获得error mail的from,to,cc
		MailSettingTo mailSettingTo=this.getEmailInfoByEmailType(0);
		simpleEmail.setFrom(mailSettingTo.getEmailFrom());
		for(int i=0;i<mailSettingTo.getEmailTo().split(";").length;i++) {
			simpleEmail.addTo(mailSettingTo.getEmailTo().split(";")[i]);
		}
		if("".equals(mailSettingTo.getEmailCc().split(";")[0])) {
			
		} else {
			for(int i=0;i<mailSettingTo.getEmailCc().split(";").length;i++) {
				simpleEmail.addCc(mailSettingTo.getEmailCc().split(";")[i]);
			}
		}
		simpleEmail.setSubject(mailSettingTo.getEmailSubject());
		simpleEmail.setMsg(errorMessage);
		simpleEmail.send();
	}
	
	public void sendErrorMail(String errorMessage,Exception e) throws Exception {
		
		SimpleEmail simpleEmail=this.getSimpleEmail("UTF-8");
		//获得error mail的from,to,cc
		MailSettingTo mailSettingTo=this.getEmailInfoByEmailType(0);
		simpleEmail.setFrom(mailSettingTo.getEmailFrom());
		for(int i=0;i<mailSettingTo.getEmailTo().split(";").length;i++) {
			simpleEmail.addTo(mailSettingTo.getEmailTo().split(";")[i]);
		}
		if("".equals(mailSettingTo.getEmailCc().split(";")[0])) {
			
		} else {
			for(int i=0;i<mailSettingTo.getEmailCc().split(";").length;i++) {
				simpleEmail.addCc(mailSettingTo.getEmailCc().split(";")[i]);
			}
		}
		simpleEmail.setSubject("batch job 运行失败");
		simpleEmail.setMsg(errorMessage+"(error message:"+e.getMessage()+")");
		simpleEmail.send();
	}
	
	public void sendMail() throws Exception {
		try {
		Properties props=System.getProperties();
		//设定邮件发送服务器
		props.setProperty("mail.smtp.host","mail.tacleasing.cn"); 
		props.setProperty("mail.smtp.auth","true"); 
		Session session=Session.getDefaultInstance(props,new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() { 
				return new PasswordAuthentication(Constants.EMAIL_FROM ,"6e84WO82Vd"); 
			} 
		}); 
		
		List<MailSettingTo> resultList=null;
		
		resultList=this.mailSettingDAO.getSendMailRecordWithoutQQ();
		MailSettingTo mailSettingTo=new MailSettingTo();
		
		for(int i=0;i<resultList.size();i++) {
			this.subject=resultList.get(i).getEmailSubject();
			
			MimeMessage mimeMsg;
			mimeMsg=new MimeMessage(session); 
			
			InternetAddress sentFrom=new InternetAddress(Constants.EMAIL_FROM); 
			mimeMsg.setFrom(sentFrom);  
			
			this.id=resultList.get(i).getId();
			this.sendCount=resultList.get(i).getSendCount();
			this.sendCount++;	
			InternetAddress[] sendTo=new InternetAddress[resultList.get(i).getEmailTo().split(";").length];
			InternetAddress[] sendCc=new InternetAddress[resultList.get(i).getEmailCc().split(";").length];
			InternetAddress[] sendBcc=new InternetAddress[resultList.get(i).getEmailBcc().split(";").length];
			for(int j=0;j<resultList.get(i).getEmailTo().split(";").length;j++) {
				sendTo[j]=new InternetAddress(resultList.get(i).getEmailTo().split(";")[j]);
			}
			if(!"".equals(resultList.get(i).getEmailCc().split(";")[0])) {
				for(int j=0;j<resultList.get(i).getEmailCc().split(";").length;j++) {
					sendCc[j]=new InternetAddress(resultList.get(i).getEmailCc().split(";")[j]);
				}
				mimeMsg.setRecipients(MimeMessage.RecipientType.CC,sendCc);
			}
			
			if(!"".equals(resultList.get(i).getEmailBcc().split(";")[0])) {
				for(int j=0;j<resultList.get(i).getEmailBcc().split(";").length;j++) {
					sendBcc[j]=new InternetAddress(resultList.get(i).getEmailBcc().split(";")[j]);
				}
				mimeMsg.setRecipients(MimeMessage.RecipientType.BCC,sendBcc);
			}
			mimeMsg.setRecipients(MimeMessage.RecipientType.TO,sendTo); 
			
			mimeMsg.setSubject(resultList.get(i).getEmailSubject());
			
			MimeBodyPart messageBodyPart=new MimeBodyPart(); 
			
			messageBodyPart.setContent(resultList.get(i).getEmailContent(),"text/html; charset=utf-8");
			
			Multipart multipart=new MimeMultipart();
			//将Multipart添加到MimeMessage中 
			multipart.addBodyPart(messageBodyPart);
			
			boolean isNeedSendErrorMail=false;
			if(resultList.get(i).getEmailAttachPath()!=null&&!"".equals(resultList.get(i).getEmailAttachPath())) {
				
				String [] emailAttachPath=resultList.get(i).getEmailAttachPath().split(";");
				
				for(int j=0;j<emailAttachPath.length;j++) {
					File file=new File(emailAttachPath[j]);
					
					if(file.isFile()) {
						//向Multipart添加附件 
						FileDataSource fds=new FileDataSource(file);
						MimeBodyPart mbpFile=new MimeBodyPart(); 
						//filename=ifile.next().toString(); 
						mbpFile.setDataHandler(new DataHandler(fds)); 
						mbpFile.setFileName(MimeUtility.encodeText(fds.getName())); 
						//将Multipart添加到MimeMessage中 
						multipart.addBodyPart(mbpFile); 

					} else {
						isNeedSendErrorMail=true;
					}
				}
				
			}
			
			mimeMsg.setContent(multipart); 
			mimeMsg.setSentDate(new Timestamp(System.currentTimeMillis()));
			
			Transport.send(mimeMsg);
			
			if(isNeedSendErrorMail) {
				//邮件能发送成功,但是附件路径找不到对应的文件,发送mail提示admin
				this.sendErrorMail("邮件能发送成功,但是有附件的路径找不到对应的文件或者附件路径有误!(邮件ID="+this.id+")");
			}
			
			mailSettingTo.setId(id);
			mailSettingTo.setSendCount(this.sendCount);
			mailSettingTo.setSendFlag(1);
			this.mailSettingDAO.updateSendMailRecord(mailSettingTo);
		}
		
		/*
		 * Add by Michael 2013-4-1 
		 * 发送HR系统 Email
		 */
		String alertStr="";
		resultList=this.mailSettingDAO.getSendHRMailRecord();
		
		for(int i=0;i<resultList.size();i++) {
			this.subject=resultList.get(i).getEmailSubject();
			
			MimeMessage mimeMsg;
			mimeMsg=new MimeMessage(session); 
						
			InternetAddress sentFrom=new InternetAddress(Constants.EMAIL_FROM); 
			mimeMsg.setFrom(sentFrom);  
			
			this.id=resultList.get(i).getId();

			InternetAddress[] sendTo=new InternetAddress[resultList.get(i).getEmailTo().split(";").length];
			
			for(int j=0;j<resultList.get(i).getEmailTo().split(";").length;j++) {
				sendTo[j]=new InternetAddress(resultList.get(i).getEmailTo().split(";")[j]);
			}
			
			mimeMsg.setRecipients(MimeMessage.RecipientType.TO,sendTo); 
			
			Matcher m=Pattern.compile(			"<a(\\s+.*?)?>(.*?)<\\/a>", 			
					Pattern.CASE_INSENSITIVE).matcher(resultList.get(i).getEmailContent());				
					while(m.find())			
						alertStr=m.group(2);	
			
			mimeMsg.setSubject("[HR系统通知]"+alertStr);
			
			MimeBodyPart messageBodyPart=new MimeBodyPart(); 
			
			messageBodyPart.setContent("HR系统有需要你处理的消息："+alertStr+"<br>请登入HR系统进行相关操作。<br>HR系统网址：http://hr.tacleasing.cn:8888/c6/","text/html; charset=utf-8");
			
			Multipart multipart=new MimeMultipart();
			//将Multipart添加到MimeMessage中 
			multipart.addBodyPart(messageBodyPart);
			
			mimeMsg.setContent(multipart); 
			mimeMsg.setSentDate(new Timestamp(System.currentTimeMillis()));
			
			Transport.send(mimeMsg);
			
			mailSettingTo.setId(id);
			mailSettingTo.setSendFlag(1);
			this.mailSettingDAO.updateSendHRMailRecord(mailSettingTo);
		}
		
		} catch(Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e,logger);
			
			MailSettingTo mailSettingTo=new MailSettingTo();
			mailSettingTo.setId(this.id);
			mailSettingTo.setSendCount(this.sendCount);
			mailSettingTo.setSendFlag(-1);
			this.mailSettingDAO.updateSendMailRecord(mailSettingTo);
			this.sendErrorMail(this.subject+"的邮件发送失败!(邮件ID="+this.id+")");
			throw e;
		}
	}
	
	/**
	 * 发动QQ邮箱的邮件
	 * @throws Exception
	 */
	public void sendMailForQQ() throws Exception {
		
		try {
		Properties props=System.getProperties();
		//设定邮件发送服务器
		props.setProperty("mail.smtp.host","mail.tacleasing.cn"); 
		props.setProperty("mail.smtp.auth","true"); 
		Session session=Session.getDefaultInstance(props,new Authenticator() { 
			public PasswordAuthentication getPasswordAuthentication() { 
				return new PasswordAuthentication(Constants.EMAIL_FROM,"6e84WO82Vd"); 
			} 
		}); 
		
		List<MailSettingTo> resultList=null;
		
		resultList=this.mailSettingDAO.getSendMailRecordForQQ();
		MailSettingTo mailSettingTo=new MailSettingTo();
		
		for(int i=0;i<resultList.size();i++) {
			this.subject=resultList.get(i).getEmailSubject();
			
			MimeMessage mimeMsg;
			mimeMsg=new MimeMessage(session); 
			
			InternetAddress sentFrom=new InternetAddress(Constants.EMAIL_FROM); 
			mimeMsg.setFrom(sentFrom);  
			
			this.id=resultList.get(i).getId();
			this.sendCount=resultList.get(i).getSendCount();
			this.sendCount++;	
			InternetAddress[] sendTo=new InternetAddress[resultList.get(i).getEmailTo().split(";").length];
			InternetAddress[] sendCc=new InternetAddress[resultList.get(i).getEmailCc().split(";").length];
			InternetAddress[] sendBcc=new InternetAddress[resultList.get(i).getEmailBcc().split(";").length];
			for(int j=0;j<resultList.get(i).getEmailTo().split(";").length;j++) {
				sendTo[j]=new InternetAddress(resultList.get(i).getEmailTo().split(";")[j]);
			}
			if(!"".equals(resultList.get(i).getEmailCc().split(";")[0])) {
				for(int j=0;j<resultList.get(i).getEmailCc().split(";").length;j++) {
					sendCc[j]=new InternetAddress(resultList.get(i).getEmailCc().split(";")[j]);
				}
				mimeMsg.setRecipients(MimeMessage.RecipientType.CC,sendCc);
			}
			
			if(!"".equals(resultList.get(i).getEmailBcc().split(";")[0])) {
				for(int j=0;j<resultList.get(i).getEmailBcc().split(";").length;j++) {
					sendBcc[j]=new InternetAddress(resultList.get(i).getEmailBcc().split(";")[j]);
				}
				mimeMsg.setRecipients(MimeMessage.RecipientType.BCC,sendBcc);
			}
			mimeMsg.setRecipients(MimeMessage.RecipientType.TO,sendTo); 
			
			mimeMsg.setSubject(resultList.get(i).getEmailSubject());
			
			MimeBodyPart messageBodyPart=new MimeBodyPart(); 
			
			messageBodyPart.setContent(resultList.get(i).getEmailContent(),"text/html; charset=utf-8");
			
			Multipart multipart=new MimeMultipart();
			//将Multipart添加到MimeMessage中 
			multipart.addBodyPart(messageBodyPart);
			
			boolean isNeedSendErrorMail=false;
			if(resultList.get(i).getEmailAttachPath()!=null&&!"".equals(resultList.get(i).getEmailAttachPath())) {
				
				String [] emailAttachPath=resultList.get(i).getEmailAttachPath().split(";");
				
				for(int j=0;j<emailAttachPath.length;j++) {
					File file=new File(emailAttachPath[j]);
					
					if(file.isFile()) {
						//向Multipart添加附件 
						FileDataSource fds=new FileDataSource(file);
						MimeBodyPart mbpFile=new MimeBodyPart(); 
						//filename=ifile.next().toString(); 
						mbpFile.setDataHandler(new DataHandler(fds)); 
						mbpFile.setFileName(MimeUtility.encodeText(fds.getName())); 
						//将Multipart添加到MimeMessage中 
						multipart.addBodyPart(mbpFile); 

					} else {
						isNeedSendErrorMail=true;
					}
				}
				
			}
			
			mimeMsg.setContent(multipart); 
			mimeMsg.setSentDate(new Timestamp(System.currentTimeMillis()));
			
			Transport.send(mimeMsg);
			
			if(isNeedSendErrorMail) {
				//邮件能发送成功,但是附件路径找不到对应的文件,发送mail提示admin
				this.sendErrorMail("邮件能发送成功,但是有附件的路径找不到对应的文件或者附件路径有误!(邮件ID="+this.id+")");
			}
			
			mailSettingTo.setId(id);
			mailSettingTo.setSendCount(this.sendCount);
			mailSettingTo.setSendFlag(1);
			this.mailSettingDAO.updateSendMailRecord(mailSettingTo);
		}
		
		} catch(Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e,logger);
			
			MailSettingTo mailSettingTo=new MailSettingTo();
			mailSettingTo.setId(this.id);
			mailSettingTo.setSendCount(this.sendCount);
			mailSettingTo.setSendFlag(-1);
			this.mailSettingDAO.updateSendMailRecord(mailSettingTo);
			this.sendErrorMail(this.subject+"的邮件发送失败!(邮件ID="+this.id+")");
			throw e;
		}
	}
	
	
	
public void sendHRMail() throws Exception {
		try {
		Properties props=System.getProperties();
		//设定邮件发送服务器
		props.setProperty("mail.smtp.host","mail.tacleasing.cn"); 
		props.setProperty("mail.smtp.auth","false"); 
		Session session=Session.getDefaultInstance(props,new Authenticator() { 
			public PasswordAuthentication getPasswordAuthentication() { 
				return new PasswordAuthentication("hr_system@tacleasing.cn","d1ygOtwtrB"); 
			} 
		}); 
				
		List<MailSettingTo> resultList=null;
		String alertStr="";
		
		resultList=this.mailSettingDAO.getSendHRMailRecord();
		MailSettingTo mailSettingTo=new MailSettingTo();
		
		for(int i=0;i<resultList.size();i++) {
			this.subject=resultList.get(i).getEmailSubject();
			
			MimeMessage mimeMsg;
			mimeMsg=new MimeMessage(session); 
						
			InternetAddress sentFrom=new InternetAddress("hr_system@tacleasing.cn"); 
			mimeMsg.setFrom(sentFrom);  
			
			this.id=resultList.get(i).getId();

			InternetAddress[] sendTo=new InternetAddress[resultList.get(i).getEmailTo().split(";").length];
			
			for(int j=0;j<resultList.get(i).getEmailTo().split(";").length;j++) {
				sendTo[j]=new InternetAddress(resultList.get(i).getEmailTo().split(";")[j]);
			}
			
			mimeMsg.setRecipients(MimeMessage.RecipientType.TO,sendTo); 
			
			Matcher m=Pattern.compile(			"<a(\\s+.*?)?>(.*?)<\\/a>", 			
					Pattern.CASE_INSENSITIVE).matcher(resultList.get(i).getEmailContent());				
					while(m.find())			
						alertStr=m.group(2);	
			
			mimeMsg.setSubject("[HR系统通知]"+alertStr);
			
			MimeBodyPart messageBodyPart=new MimeBodyPart(); 
			
			messageBodyPart.setContent("HR系统有需要你处理的消息："+alertStr+"<br>请登入HR系统进行相关操作。<br>HR系统网址：http://hr.tacleasing.cn:8888/c6/","text/html; charset=utf-8");
			
			Multipart multipart=new MimeMultipart();
			//将Multipart添加到MimeMessage中 
			multipart.addBodyPart(messageBodyPart);
			
			mimeMsg.setContent(multipart); 
			mimeMsg.setSentDate(new Timestamp(System.currentTimeMillis()));
			
			Transport.send(mimeMsg);
			
			mailSettingTo.setId(id);
			mailSettingTo.setSendFlag(1);
			this.mailSettingDAO.updateSendHRMailRecord(mailSettingTo);
		}
		
		} catch(Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e,logger);
			
			MailSettingTo mailSettingTo=new MailSettingTo();
			mailSettingTo.setId(this.id);
			mailSettingTo.setSendFlag(-1);
			this.mailSettingDAO.updateSendHRMailRecord(mailSettingTo);
			this.sendErrorMail(this.subject+"的邮件发送失败!(邮件ID="+this.id+")");
			throw e;
		}
	}

}
