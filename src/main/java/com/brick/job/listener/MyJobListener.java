package com.brick.job.listener;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.listeners.JobListenerSupport;

import com.brick.job.service.JobService;
import com.brick.job.to.JobTo;
import com.brick.util.Constants;
import com.brick.util.DateUtil;

public class MyJobListener extends JobListenerSupport{

	Logger logger = Logger.getLogger(MyJobListener.class);
	
	private JobService jobService;
	
	public JobService getJobService() {
		return jobService;
	}

	public void setJobService(JobService jobService) {
		this.jobService = jobService;
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context,
			JobExecutionException jobException) {
		// TODO Auto-generated method stub
		try{
			boolean emailFlag = false;
			JobTo jobTo = new JobTo();
			jobTo.setJobName(context.getJobDetail().getName());
			jobTo.setFireTime(context.getFireTime());
			jobTo.setNextTime(context.getNextFireTime());
			jobTo.setRunTime(context.getJobRunTime());
			if(jobException == null){
				//System.out.println("正常");
				jobTo.setErrorCode(0);
				jobTo.setFireStatus("成功");
				jobTo.setRemark("成功");
				jobTo.setStatus(0);
			} else {
				//System.out.println("有异常");
				emailFlag = true;
				jobTo.setErrorCode(jobException.getErrorCode());
				jobTo.setFireStatus("失败");
				jobTo.setRemark(jobException.getCause().getCause().getCause().getMessage());
				jobTo.setStatus(1);
			}
			jobService.doJobRunLog(jobTo);
			if (emailFlag) {
				jobTo.setDescr(context.getTrigger().getDescription());
				sendMail(jobTo, jobException);
			}
		} catch (Exception e) {
			logger.error(e);
		}
		super.jobWasExecuted(context, jobException);
	}

	@Override
	public String getName() {
		return "myJobListener";
	}
	
	private void sendMail(JobTo jobTo, Exception ex) throws Exception{
		try {
			StringWriter sw = new StringWriter();
			ex.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            str = str.replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
            		.replaceAll("\r\n", "<br/>");
            sw.flush();
            sw.close();
            sw = null;
			String mailContent = "<head> " +
				"<style type='text/css' media='screen'> " +
				"body{font-family: '微软雅黑';}" +
				"	table { " +
				"	border-collapse:collapse;  " +
				"	border:solid #999; " +
				"	border-width:1px 0 0 1px;  " +
				"	} " +
				"	table th {border:solid #999;border-width:0 1px 1px 0;padding:2px;text-align:right;white-space: nowrap; vertical-align: top;} " +
				"	table td {border:solid #999;border-width:0 1px 1px 0;padding:2px;text-align:left;} " +
				"</style> " +
				"</head> " +
				"<body> " +
				"Quartz Job 运行失败提醒：" +
				"<table> " +
				"	<tr> " +
				"		<th>Job Name：</th> " +
				"		<td>" + jobTo.getJobName() + "</td> " +
				"	</tr> " +
				"	<tr> " +
				"		<th>Job Description：</th> " +
				"		<td>" + jobTo.getDescr() + "</td> " +
				"	</tr> " +
				"	<tr> " +
				"		<th>Fire Time：</th> " +
				"		<td>" + DateUtil.dateToString(jobTo.getFireTime(), "yyyy-MM-dd HH:mm") + "</td> " +
				"	</tr> " +
				"	<tr> " +
				"		<th>Error Code：</th> " +
				"		<td>" + jobTo.getErrorCodeStr() + "</td> " +
				"	</tr> " +
				"	<tr> " +
				"		<th>Error Message：</th> " +
				"		<td>" + jobTo.getRemark() + "</td> " +
				"	</tr> " +
				"	<tr> " +
				"		<th>Detail Message：</th> " +
				"		<td style='font-size: 11px;'>" + str + "</td> " +
				"	</tr> " +
				"</table> " +
				"</body>";
			//"\tat " + trace[i]
			Properties props=System.getProperties();
			//设定邮件发送服务器
			props.setProperty("mail.smtp.host","mail.tacleasing.cn"); 
			props.setProperty("mail.smtp.auth","true"); 
			Session session = Session.getDefaultInstance(props,new Authenticator() { 
				public PasswordAuthentication getPasswordAuthentication() { 
					return new PasswordAuthentication(Constants.EMAIL_FROM,"1qaz2wsx5tgb"); 
				} 
			}); 
			MimeMessage mimeMsg;
			mimeMsg = new MimeMessage(session); 
			InternetAddress sentFrom = new InternetAddress(Constants.EMAIL_FROM); 
			mimeMsg.setFrom(sentFrom);  
			mimeMsg.setRecipients(MimeMessage.RecipientType.CC, "kyle@tacleasing.cn");
			mimeMsg.setRecipients(MimeMessage.RecipientType.TO, "IT@tacleasing.cn"); 
//			mimeMsg.setRecipients(MimeMessage.RecipientType.TO, "yangyun@tacleasing.cn"); 
			mimeMsg.setSubject("Quartz Job 运行失败提醒！");
			MimeBodyPart messageBodyPart = new MimeBodyPart(); 
			messageBodyPart.setContent(mailContent, "text/html; charset=utf-8");
			Multipart multipart=new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			mimeMsg.setContent(multipart); 
			mimeMsg.setSentDate(new Timestamp(System.currentTimeMillis()));
			Transport.send(mimeMsg);
		} catch(Exception e) {
			throw e;
		}
	}

}
