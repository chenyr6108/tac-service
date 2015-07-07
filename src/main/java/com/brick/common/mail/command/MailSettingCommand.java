package com.brick.common.mail.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.exception.ServiceException;
import com.brick.base.to.PagingInfo;
import com.brick.common.mail.service.MailSettingService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.employee.to.EmployeeTO;
import com.brick.log.service.LogPrint;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.StringUtils;

public class MailSettingCommand extends BaseCommand {

	Log logger=LogFactory.getLog(MailSettingCommand.class);
	
	private MailSettingService mailSettingService;

	public MailSettingService getMailSettingService() {
		return mailSettingService;
	}

	public void setMailSettingService(MailSettingService mailSettingService) {
		this.mailSettingService = mailSettingService;
	}
	
	public void query(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......query";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List<MailSettingTo> resultList=null;
		
//		if(context.contextMap.get("STATUS")==null||"".equals(context.contextMap.get("STATUS"))) {
//			context.contextMap.put("STATUS",0);
//		}
		if(context.contextMap.get("SEND_RESULT")==null) {
			context.contextMap.put("SEND_RESULT","1");
		}
		
		Map<String,List<MailSettingTo>> filterMap=new HashMap<String,List<MailSettingTo>>();
		try {
			context.contextMap.put("TYPE","邮件发送结果");
			context.contextMap.put("TYPE1","邮件所属功能");
			resultList=this.mailSettingService.query(context,outputMap);
			
			filterMap=this.mailSettingService.getFilterList(context);
			
		} catch(Exception e) {
			context.errList.add("邮件查询出错!请联系管理员");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e,logger);
		}
		
		if(context.errList.isEmpty()) {
			outputMap.put("MAIL_ADDRESS",context.contextMap.get("MAIL_ADDRESS"));
			outputMap.put("SEND_FROM_TIME",context.contextMap.get("SEND_FROM_TIME"));
			outputMap.put("SEND_TO_TIME",context.contextMap.get("SEND_TO_TIME"));
			outputMap.put("MAIL_SUBJECT",context.contextMap.get("MAIL_SUBJECT"));
			outputMap.put("MAIL_PATH",context.contextMap.get("MAIL_PATH"));
			
			outputMap.put("SEND_RESULT",context.contextMap.get("SEND_RESULT"));
			outputMap.put("MAIL_TYPE",context.contextMap.get("MAIL_TYPE"));
			outputMap.put("resultList",resultList);
			outputMap.put("sendResultList",filterMap.get("sendResultList"));
			outputMap.put("mailTypeList",filterMap.get("mailTypeList"));
			Output.jspOutput(outputMap,context,"/common/mail/mailQuery.jsp");
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
	
	public void getMialTypeList(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getMialTypeList";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List<MailSettingTo> resultList=null;
		
		try {
			context.contextMap.put("TYPE","邮件所属功能");
			resultList=this.mailSettingService.getMailTypeList(context);
		} catch (Exception e) {
			context.errList.add("获得邮件所属功能出错!请联系管理员");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e,logger);
		}
		
		if(context.errList.isEmpty()) {
			Output.jsonArrayOutputForObject(resultList,context);
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
	
	public void getMailTypeGroupList(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getMialTypeList";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List<MailSettingTo> resultList=null;
		
		try {
			context.contextMap.put("TYPE","邮件所属功能分类");
			resultList=this.mailSettingService.getMailTypeList(context);
		} catch (Exception e) {
			context.errList.add("获得邮件所属功能出错!请联系管理员");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e,logger);
		}
		
		if(context.errList.isEmpty()) {
			Output.jsonArrayOutputForObject(resultList,context);
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
	
	public void getDeptList(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getDeptList";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List<Map> resultList=null;
		
		try {
			resultList=this.mailSettingService.getDeptList(context);
		} catch (Exception e) {
			context.errList.add("获得部门出错!请联系管理员");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e,logger);
		}
		
		if(context.errList.isEmpty()) {
			Output.jsonArrayOutputForList(resultList,context);
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
	
	public void disableEmail(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......disableEmail";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		context.contextMap.put("status",-1);
		try {
			this.mailSettingService.updateEmail(context);
			
		} catch (Exception e) {
			context.errList.add("废除邮件出错!请联系管理员");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e,logger);
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		this.queryOfConfig(context);
	}
	
	public void enableEmail(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......enableEmail";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		context.contextMap.put("status",0);
		try {
			this.mailSettingService.updateEmail(context);
			
		} catch (Exception e) {
			context.errList.add("启用邮件出错!请联系管理员");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e,logger);
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		this.queryOfConfig(context);
	}
	
	public void getMailSetting(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getMailSetting";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		context.contextMap.put("TYPE","邮件所属功能");
		try {
			MailSettingTo mailSettingTo=this.mailSettingService.getEmailSetting(context);
			Output.jsonObjectOutputForTo(mailSettingTo,context);
		} catch (Exception e) {
			context.errList.add("获得邮件出错!请联系管理员");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e,logger);
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
	
	public void getEmailAddress(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getEmailAddress";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		MailSettingTo mailSettingTo=null;
		
		try {
			context.contextMap.put("TYPE","邮件所属功能");
			mailSettingTo=this.mailSettingService.getEmailSetting(context);
		} catch (Exception e) {
			context.errList.add("获得邮件地址出错!请联系管理员");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e,logger);
		}
		if(context.errList.isEmpty()) {
			outputMap.put("mailSettingTo",mailSettingTo);
			Output.jspOutput(outputMap,context,"/common/mail/mailAddress.jsp");
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
	
	public void queryOfConfig(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......queryOfConfig";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		PagingInfo<Object> pagingInfo = null;
		List<MailSettingTo> outputList = new ArrayList<MailSettingTo>();
		
		if(context.contextMap.get("STATUS")==null) {
			context.contextMap.put("STATUS",0);
		}
		
		Map<String,List<MailSettingTo>> filterMap=new HashMap<String,List<MailSettingTo>>();
		try {
			context.contextMap.put("TYPE","邮件所属功能");
			pagingInfo = (PagingInfo<Object>)baseService.queryForListWithPaging("common.getMailSettingListPage", context.contextMap, "createOn");
			for (Object o : pagingInfo.getResultList()) {
				MailSettingTo mailSettingTo = (MailSettingTo) o;
				if(mailSettingTo.getDeptId()!=null) {
					mailSettingTo.setDeptList(Arrays.asList(mailSettingTo.getDeptId().split(",")));
				}
				mailSettingTo.setMailToList(getEmpByMail(mailSettingTo.getEmailTo()));
				mailSettingTo.setMailCcList(getEmpByMail(mailSettingTo.getEmailCc()));
				outputList.add(mailSettingTo);
			}
			
			/*for(int i=0;i<resultList.size();i++) {//由于邮件的to的收件人很多,所以用代码处理,每5个换行
				if(resultList.get(i).getEmailTo().length()>140) {
					StringBuffer emailTo=new StringBuffer();
					for(int j=0;j<resultList.get(i).getEmailTo().split(";").length;j++) {
						if((j+1)%5==0) {
							emailTo.append(resultList.get(i).getEmailTo().split(";")[j]+";<br>&nbsp;");
						} else {
							emailTo.append(resultList.get(i).getEmailTo().split(";")[j]+";");
						}
					}
					resultList.get(i).setEmailTo(emailTo.toString());
				}
			}
			
			for(int i=0;i<resultList.size();i++) {//由于邮件的cc的收件人很多,所以用代码处理,每5个换行
				if(resultList.get(i).getEmailCc().length()>140) {
					StringBuffer emailCc=new StringBuffer();
					for(int j=0;j<resultList.get(i).getEmailCc().split(";").length;j++) {
						if((j+1)%5==0) {
							emailCc.append(resultList.get(i).getEmailCc().split(";")[j]+";<br>&nbsp;");
						} else {
							emailCc.append(resultList.get(i).getEmailCc().split(";")[j]+";");
						}
					}
					resultList.get(i).setEmailCc(emailCc.toString());
				}
			}*/
			filterMap=this.mailSettingService.getFilterList(context);
		} catch (Exception e) {
			context.errList.add("邮件设置出错!请联系管理员");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e,logger);
		}
		
		if(context.errList.isEmpty()) {
//			outputMap.put("pageTotalSize",context.contextMap.get("pageTotalSize"));
//			outputMap.put("currentPage",context.contextMap.get("currentPage"));
//			outputMap.put("pageCount",context.contextMap.get("pageCount"));
			outputMap.put("pagingInfo", pagingInfo);
			outputMap.put("STATUS",context.contextMap.get("STATUS"));
			outputMap.put("QUERY_EMAIL_TYPE",context.contextMap.get("QUERY_EMAIL_TYPE"));
			outputMap.put("QUERY_EMAIL_TYPE_GROUP",context.contextMap.get("QUERY_EMAIL_TYPE_GROUP"));
			outputMap.put("resultList",outputList);
			outputMap.put("mailTypeList",filterMap.get("mailTypeList"));
			outputMap.put("mailTypeGroupList",filterMap.get("mailTypeGroupList"));
			Output.jspOutput(outputMap,context,"/common/mail/mailSet.jsp");
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
	
	private List<EmployeeTO> getEmpByMail(String mailStr) throws ServiceException {
		List<EmployeeTO> eList = new ArrayList<EmployeeTO>();
		if(mailStr==null) {
			return eList;
		}
		EmployeeTO e = null;
		String[] mailStrs = null;
		Map<String, Object> paraMap = new HashMap<String, Object>();
		mailStrs = mailStr.split(";");
		for (String mail : mailStrs) {
			if (mail.contains("@")) {
				paraMap.put("mailStr", mail);
				e = (EmployeeTO) baseService.queryForObj("employee.getEmpByMail", paraMap);
				if (e == null) {
					e = new EmployeeTO();
					e.setEmail(mail);
					e.setName(mail);
				}
				eList.add(e);
			}
		}
		return eList;
	}
	
	
	
	public void addEmailSetting(Context context) {

		String log="employeeId="+context.contextMap.get("s_employeeId")+"......addEmailSetting";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		this.validate(context,"");
		if(!context.errList.isEmpty()) {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
			return;
		}
		
		MailSettingTo mailSettingTo=new MailSettingTo();
		
		mailSettingTo.setEmailFrom(context.contextMap.get("EMAIL_FROM").toString());
		mailSettingTo.setEmailTo(context.contextMap.get("EMAIL_TO").toString());
		mailSettingTo.setEmailCc(context.contextMap.get("EMAIL_CC").toString());
		mailSettingTo.setEmailBcc(context.contextMap.get("EMAIL_BCC").toString());
		mailSettingTo.setEmailType(context.contextMap.get("EMAIL_TYPE").toString());
		mailSettingTo.setEmailContent(context.contextMap.get("EMAIL_CONTENT").toString());
		mailSettingTo.setEmailSubject(context.contextMap.get("EMAIL_SUBJECT").toString());
		mailSettingTo.setEmailAttachPath(context.contextMap.get("EMAIL_ATTACH_PATH").toString());
		mailSettingTo.setEmailTypeGroup(context.contextMap.get("EMAIL_TYPE_GROUP").toString());
		mailSettingTo.setDeptId(context.contextMap.get("DEPT_ID").toString());
		mailSettingTo.setCreateBy(context.contextMap.get("s_employeeId").toString());
		
		try {
			
			if (context.contextMap.get("DEPT_ID") != null && !"".equals(context.contextMap.get("DEPT_ID"))) {
				String[] deptList = context.contextMap.get("DEPT_ID").toString().split(",");
				String deptName = "";
				for (int i = 0; i < deptList.length; i++) {
					deptName += this.mailSettingService.getDeptName(Integer.valueOf(deptList[i]));
					if (i != deptList.length-1) {
						deptName += ";";
					}
				}
				mailSettingTo.setDeptName(deptName);
			}
			
			this.mailSettingService.saveEmailSetting(mailSettingTo);
		} catch (Exception e) {
			context.errList.add("邮件设置保存出错!请联系管理员");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e,logger);
		}
		
		context.contextMap.remove("EMAIL_TYPE");
		this.queryOfConfig(context);
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
	
	public void updateEmailSetting(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......updateEmailSetting";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		this.validate(context,"1");
		if(!context.errList.isEmpty()) {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
			return;
		}
		
		MailSettingTo mailSettingTo=new MailSettingTo();
		
		mailSettingTo.setId(Integer.valueOf(context.contextMap.get("ID").toString()));
		mailSettingTo.setEmailFrom(context.contextMap.get("EMAIL_FROM1").toString());
		mailSettingTo.setEmailTo(context.contextMap.get("EMAIL_TO1").toString());
		mailSettingTo.setEmailCc(context.contextMap.get("EMAIL_CC1").toString());
		mailSettingTo.setEmailBcc(context.contextMap.get("EMAIL_BCC1").toString());
		mailSettingTo.setEmailType(context.contextMap.get("EMAIL_TYPE1").toString());
		mailSettingTo.setEmailContent(context.contextMap.get("EMAIL_CONTENT1").toString());
		mailSettingTo.setEmailSubject(context.contextMap.get("EMAIL_SUBJECT1").toString());
		mailSettingTo.setEmailAttachPath(context.contextMap.get("EMAIL_ATTACH_PATH1").toString());
		mailSettingTo.setEmailTypeGroup(context.contextMap.get("EMAIL_TYPE_GROUP1").toString());
		mailSettingTo.setDeptId(context.contextMap.get("DEPT_ID1").toString());
		
		try {
			
			if (context.contextMap.get("DEPT_ID1") != null && !"".equals(context.contextMap.get("DEPT_ID1"))) {
				String[] deptList = context.contextMap.get("DEPT_ID1").toString().split(",");
				String deptName = "";
				for (int i = 0; i < deptList.length; i++) {
					deptName += this.mailSettingService.getDeptName(Integer.valueOf(deptList[i]));
					if (i != deptList.length-1) {
						deptName += ";";
					}
				}
				mailSettingTo.setDeptName(deptName);
			}
			
			this.mailSettingService.updateEmailSetting(mailSettingTo);
		} catch (Exception e) {
			context.errList.add("邮件设置保存出错!请联系管理员");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e,logger);
		}
		
		context.contextMap.remove("EMAIL_TYPE");
		this.queryOfConfig(context);
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
	
	//j的作用,因为插入的验证和更新的验证都用此方法,但是插入的DIV中所有的name比更新的DIV的name少1
	private void validate(Context context,String j) {

		//验证邮件FROM  
		if(context.contextMap.get("EMAIL_FROM"+j).toString().indexOf("@")!=context.contextMap.get("EMAIL_FROM"+j).toString().lastIndexOf("@")) {
			context.errList.add("邮件From不能有2个地址!");
			return;
		}
		
		//邮件地址验证
		if(context.contextMap.get("EMAIL_TO"+j)==null||"".equals(context.contextMap.get("EMAIL_TO"+j))) {
			context.errList.add("邮件发送人不能为空!");
			return;
		} else {
			if(context.contextMap.get("EMAIL_TO"+j).toString().indexOf("；")!=-1) {
				context.errList.add("邮件发送人格式不合法!");
				return;
			}
			if(context.contextMap.get("EMAIL_TO"+j).toString().indexOf("@")==-1) {
				context.errList.add("邮件发送人格式不合法!");
				return;
			}
			if(context.contextMap.get("EMAIL_TO"+j).toString().indexOf("@")==context.contextMap.get("EMAIL_TO"+j).toString().lastIndexOf("@")) {
				String regex="\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
				if(!context.contextMap.get("EMAIL_TO"+j).toString().split(";")[0].matches(regex)) {
					context.errList.add("邮件发送人格式不合法!");
					return;
				}
				if(context.contextMap.get("EMAIL_TO"+j).toString().indexOf("@")==0||context.contextMap.get("EMAIL_TO"+j).toString().indexOf("@")==context.contextMap.get("EMAIL_TO"+j).toString().length()-1) {
					context.errList.add("邮件发送人格式不合法!");
					return;
				}
			}
			if(context.contextMap.get("EMAIL_TO"+j).toString().indexOf("@")!=context.contextMap.get("EMAIL_TO"+j).toString().lastIndexOf("@")) {
				if(context.contextMap.get("EMAIL_TO"+j).toString().split(";").length==1) {
					context.errList.add("邮件发送人格式不合法!");
					return;
				} else {
					for(int i=0;i<context.contextMap.get("EMAIL_TO"+j).toString().split(";").length;i++) {
						String regex="\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
						if(!context.contextMap.get("EMAIL_TO"+j).toString().split(";")[i].matches(regex)) {
							context.errList.add("邮件发送人格式不合法!");
							return;
						}
						if(context.contextMap.get("EMAIL_TO"+j).toString().split(";")[i].indexOf("@")==-1) {
							context.errList.add("邮件发送人格式不合法!");
							return;
						}
						if(context.contextMap.get("EMAIL_TO"+j).toString().split(";")[i].indexOf("@")!=context.contextMap.get("EMAIL_TO"+j).toString().split(";")[i].lastIndexOf("@")) {
							context.errList.add("邮件发送人格式不合法!");
							return;
						}
					}
				}
			}
		}
		
		//邮件CC地址验证
		if(context.contextMap.get("EMAIL_CC"+j)==null||"".equals(context.contextMap.get("EMAIL_CC"+j))) {
			
		} else {
			if(context.contextMap.get("EMAIL_CC"+j).toString().indexOf("；")!=-1) {
				context.errList.add("邮件抄送人格式不合法!");
				return;
			}
			if(context.contextMap.get("EMAIL_CC"+j).toString().indexOf("@")==-1) {
				context.errList.add("邮件抄送人格式不合法!");
				return;
			}
			if(context.contextMap.get("EMAIL_CC"+j).toString().indexOf("@")==context.contextMap.get("EMAIL_CC"+j).toString().lastIndexOf("@")) {
				String regex="\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
				if(!context.contextMap.get("EMAIL_CC"+j).toString().split(";")[0].matches(regex)) {
					context.errList.add("邮件发送人格式不合法!");
					return;
				}
				if(context.contextMap.get("EMAIL_CC"+j).toString().indexOf("@")==0||context.contextMap.get("EMAIL_CC"+j).toString().indexOf("@")==context.contextMap.get("EMAIL_CC"+j).toString().length()-1) {
					context.errList.add("邮件抄送人格式不合法!");
					return;
				}
			}
			if(context.contextMap.get("EMAIL_CC"+j).toString().indexOf("@")!=context.contextMap.get("EMAIL_CC"+j).toString().lastIndexOf("@")) {
				if(context.contextMap.get("EMAIL_CC"+j).toString().split(";").length==1) {
					context.errList.add("邮件抄送人格式不合法!");
					return;
				} else {
					for(int i=0;i<context.contextMap.get("EMAIL_CC"+j).toString().split(";").length;i++) {
						String regex="\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
						if(!context.contextMap.get("EMAIL_CC"+j).toString().split(";")[i].matches(regex)) {
							context.errList.add("邮件发送人格式不合法!");
							return;
						}
						if(context.contextMap.get("EMAIL_CC"+j).toString().split(";")[i].indexOf("@")==-1) {
							context.errList.add("邮件抄送人格式不合法!");
							return;
						}
						if(context.contextMap.get("EMAIL_CC"+j).toString().split(";")[i].indexOf("@")!=context.contextMap.get("EMAIL_CC"+j).toString().split(";")[i].lastIndexOf("@")) {
							context.errList.add("邮件抄送人格式不合法!");
							return;
						}
					}
				}
			}
		}
		
		//邮件BCC地址验证
				if(context.contextMap.get("EMAIL_BCC"+j)==null||"".equals(context.contextMap.get("EMAIL_BCC"+j))) {
					
				} else {
					if(context.contextMap.get("EMAIL_BCC"+j).toString().indexOf("；")!=-1) {
						context.errList.add("邮件暗送人格式不合法!");
						return;
					}
					if(context.contextMap.get("EMAIL_BCC"+j).toString().indexOf("@")==-1) {
						context.errList.add("邮件暗送人格式不合法!");
						return;
					}
					if(context.contextMap.get("EMAIL_BCC"+j).toString().indexOf("@")==context.contextMap.get("EMAIL_BCC"+j).toString().lastIndexOf("@")) {
						String regex="\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
						if(!context.contextMap.get("EMAIL_BCC"+j).toString().split(";")[0].matches(regex)) {
							context.errList.add("邮件发送人格式不合法!");
							return;
						}
						if(context.contextMap.get("EMAIL_BCC"+j).toString().indexOf("@")==0||context.contextMap.get("EMAIL_BCC"+j).toString().indexOf("@")==context.contextMap.get("EMAIL_BCC"+j).toString().length()-1) {
							context.errList.add("邮件暗送人格式不合法!");
							return;
						}
					}
					if(context.contextMap.get("EMAIL_BCC"+j).toString().indexOf("@")!=context.contextMap.get("EMAIL_BCC"+j).toString().lastIndexOf("@")) {
						if(context.contextMap.get("EMAIL_BCC"+j).toString().split(";").length==1) {
							context.errList.add("邮件暗送人格式不合法!");
							return;
						} else {
							for(int i=0;i<context.contextMap.get("EMAIL_BCC"+j).toString().split(";").length;i++) {
								String regex="\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
								if(!context.contextMap.get("EMAIL_BCC"+j).toString().split(";")[i].matches(regex)) {
									context.errList.add("邮件发送人格式不合法!");
									return;
								}
								if(context.contextMap.get("EMAIL_BCC"+j).toString().split(";")[i].indexOf("@")==-1) {
									context.errList.add("邮件暗送人格式不合法!");
									return;
								}
								if(context.contextMap.get("EMAIL_BCC"+j).toString().split(";")[i].indexOf("@")!=context.contextMap.get("EMAIL_BCC"+j).toString().split(";")[i].lastIndexOf("@")) {
									context.errList.add("邮件暗送人格式不合法!");
									return;
								}
							}
						}
					}
				}
				
		//验证附件地址
		if(context.contextMap.get("EMAIL_ATTACH_PATH"+j)!=null&&!"".equals(context.contextMap.get("EMAIL_ATTACH_PATH"+j))) {
			if(context.contextMap.get("EMAIL_ATTACH_PATH"+j).toString().indexOf(":")==-1) {
				context.errList.add("邮件附件路径格式不合法!");
				return;
			}
			String [] attachPath=context.contextMap.get("EMAIL_ATTACH_PATH"+j).toString().split(":");
			if(attachPath[0].length()!=1) {
				context.errList.add("邮件附件路径格式不合法!");
				return;
			} else {
				if(!"C".equalsIgnoreCase(attachPath[0])&&!"D".equalsIgnoreCase(attachPath[0])&&!"E".equalsIgnoreCase(attachPath[0])
						&&!"F".equalsIgnoreCase(attachPath[0])&&!"G".equalsIgnoreCase(attachPath[0])&&!"H".equalsIgnoreCase(attachPath[0])) {
					context.errList.add("邮件附件路径格式不合法!");
					return;
				}
			}
		}
		
		//验证时间
//		if(context.contextMap.get("EMAIL_SEND_TIME")==null||"".equals(context.contextMap.get("EMAIL_SEND_TIME"))) {
//			context.errList.add("邮件发送时间不能为空!");
//			return;
//		} else {
//			if(context.contextMap.get("EMAIL_SEND_TIME").toString().split(":").length!=3) {
//				context.errList.add("邮件发送时间格式不合法!");
//				return;
//			} else {
//				String [] emailSendTime=context.contextMap.get("EMAIL_SEND_TIME").toString().split(":");
//				try {
//					if(Integer.valueOf(emailSendTime[0])<0||Integer.valueOf(emailSendTime[0])>23) {
//						context.errList.add("邮件发送时间格式不合法!");
//						return;
//					}
//					if(Integer.valueOf(emailSendTime[1])<0||Integer.valueOf(emailSendTime[1])>59) {
//						context.errList.add("邮件发送时间格式不合法!");
//						return;
//					}
//					if(Integer.valueOf(emailSendTime[2])<0||Integer.valueOf(emailSendTime[2])>59) {
//						context.errList.add("邮件发送时间格式不合法!");
//						return;
//					}
//				} catch(Exception e) {
//					context.errList.add("邮件发送时间格式不合法!");
//					return;
//				}
//			}
//		}
	}
	
	public void getSetMail(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getSetMail";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		context.contextMap.put("TYPE","邮件所属功能");
		try {
			MailSettingTo mailSettingTo=this.mailSettingService.getSetMail(context);
			if (mailSettingTo.getDeptId() != null) {
				mailSettingTo.setDeptList(Arrays.asList(mailSettingTo.getDeptId().split(",")));
			}
			Output.jsonObjectOutputForTo(mailSettingTo,context);
		} catch (Exception e) {
			context.errList.add("获得邮件出错!请联系管理员");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e,logger);
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
	
	public void getEmailContent(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getEmailContent";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		MailSettingTo mailSettingTo=null;
		
		try {
			context.contextMap.put("TYPE","邮件所属功能");
			mailSettingTo=this.mailSettingService.getSetMail(context);
		} catch (Exception e) {
			context.errList.add("获得邮件地址出错!请联系管理员");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e,logger);
		}
		if(context.errList.isEmpty()) {
			outputMap.put("mailSettingTo",mailSettingTo);
			Output.jspOutput(outputMap,context,"/common/mail/mailContent.jsp");
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
	
	public void delMailFromSetting(Context context){
		boolean flag = true;
		String type = (String) context.contextMap.get("type");
		String mail = (String) context.contextMap.get("mail");
		try {
			if ("to".equals(type)) {
				String allMail = (String) baseService.queryForObj("common.getMailToById", context.contextMap);
				String newMail = delMailFromMailStr(allMail, mail);
				context.contextMap.put("newMail", newMail);
				baseService.update("common.updateMailTo", context.contextMap);
			} else if ("cc".equals(type)) {
				String allMail = (String) baseService.queryForObj("common.getMailCcById", context.contextMap);
				String newMail = delMailFromMailStr(allMail, mail);
				context.contextMap.put("newMail", newMail);
				baseService.update("common.updateMailCc", context.contextMap);
			} else {
				flag = false;
			}
		} catch (Exception e) {
			flag = false;
		}
		Output.jsonFlageOutput(flag, context);
	}
	
	public void addMailFromSetting(Context context){
		boolean flag = true;
		String type = (String) context.contextMap.get("type");
		String mail = (String) context.contextMap.get("mail");
		try {
			if ("to".equals(type)) {
				String allMail = (String) baseService.queryForObj("common.getMailToById", context.contextMap);
				String newMail = addMailFromMailStr(allMail, mail);
				context.contextMap.put("newMail", newMail);
				baseService.update("common.updateMailTo", context.contextMap);
			} else if ("cc".equals(type)) {
				String allMail = (String) baseService.queryForObj("common.getMailCcById", context.contextMap);
				String newMail = addMailFromMailStr(allMail, mail);
				context.contextMap.put("newMail", newMail);
				baseService.update("common.updateMailCc", context.contextMap);
			} else {
				flag = false;
			}
		} catch (Exception e) {
			logger.warn(e);
			flag = false;
		}
		Output.jsonFlageOutput(flag, context);
	}
	
	private String delMailFromMailStr(String mailStr, String delItem){
		String newMailStr = null;
		StringBuffer mailBuffer = new StringBuffer();
		String[] mailArray = mailStr.split(";");
		for (String m : mailArray) {
			if (m.contains("@") && !m.equals(delItem)) {
				mailBuffer.append(m);
				mailBuffer.append(";");
			}
		}
		if (mailBuffer.length() > 0) {
			newMailStr = mailBuffer.substring(0, mailBuffer.length() - 1);
		}
		return newMailStr;
	}
	
	private String addMailFromMailStr(String mailStr, String addItem){
		String newMailStr = null;
		StringBuffer mailBuffer = new StringBuffer();
		String[] mailArray = null;
		if (!StringUtils.isEmpty(mailStr)) {
			mailArray = mailStr.split(";");
			for (String m : mailArray) {
				if (m.contains("@")) {
					mailBuffer.append(m);
					mailBuffer.append(";");
				}
			}
		}
		mailBuffer.append(addItem);
		mailBuffer.append(";");
		if (mailBuffer.length() > 0) {
			newMailStr = mailBuffer.substring(0, mailBuffer.length() - 1);
		}
		return newMailStr;
	}
	
	
}
