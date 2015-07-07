package com.brick.sms.email;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.mail.MailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.brick.base.service.BaseService;
import com.brick.base.util.LeaseUtil;
import com.brick.common.mail.service.MailSettingService;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.log.service.LogPrint;
import com.brick.rent.to.LeaseBackCaseTo;
import com.brick.risk_audit.SelectReportInfo;
import com.brick.service.core.DataAccessor;
import com.brick.util.DataUtil;
import com.brick.util.StringUtils;
import com.ibatis.sqlmap.client.SqlMapClient;

public class MailTest extends BaseService {
	Log logger = LogFactory.getLog(MailTest.class);
	private JavaMailSender mailSender;
	private MailMessage mailMessage;
	private MimeMessage mimeMessage;
	private MailUtilService mailUtilService;
	private MailSettingService mailSettingService = null;

	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}

	public JavaMailSender getMailSender() {
		return mailSender;
	}

	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public MailMessage getMailMessage() {
		return mailMessage;
	}

	public void setMailMessage(MailMessage mailMessage) {
		this.mailMessage = mailMessage;
	}

	public MimeMessage getMimeMessage() {
		return mimeMessage;
	}

	public void setMimeMessage(MimeMessage mimeMessage) {
		this.mimeMessage = mimeMessage;
	}

	public void sendBrandRenterEmail(Map context) throws MessagingException, SQLException {
		MailSettingTo mailSettingTo = new MailSettingTo();
		List listLockMsg = (List) context.get("LISTLOCKMSG");
		String RECT_ID = String.valueOf(context.get("RECT_ID"));
		Map temp;
		Map tempLockMsgMap;
		if (listLockMsg != null) {
			mailSettingTo.setEmailTo(String.valueOf(context.get("TOEMAIL")));
			mailSettingTo.setEmailSubject("解码");
			mailSettingTo.setEmailCc("lune@tacleasing.cn");
			
			if (context.get("TOEMAIL_SENSOR") != null) {
				String user_id = LeaseUtil.getSensorIdByCreditId(String.valueOf(LeaseUtil.getCreditIdByRectId(RECT_ID)));
				mailSettingTo.setEmailCc(String.valueOf(context.get("TOEMAIL_SENSOR")) + ";" + LeaseUtil.getAssistantEmailByUserId(user_id));
			}
			
			StringBuilder textContext = new StringBuilder();
			textContext.append("<html><head></head><body>");
			textContext.append("您好！");
			textContext.append("      我是【裕融租赁有限公司】 业管课，如有解码问题请致电业管课： Tel:  18913510191");

			textContext.append("<br>");
			textContext
					.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">");
			textContext.append("<tr>");
			textContext.append("<th width=\"200\">");
			textContext.append("客户名称");
			textContext.append("</th>");
			textContext.append("<th width=\"80\">");
			textContext.append("联系人");
			textContext.append("</th>");
			textContext.append("<th width=\"80\">");
			textContext.append("手机");
			textContext.append("</th>");
			textContext.append("<th width=\"200\">");
			textContext.append("E-mail");
			textContext.append("</th>");
			textContext.append("<th width=\"100\">");
			textContext.append("型号");
			textContext.append("</th>");
			textContext.append("<th width=\"100\">");
			textContext.append("机号");
			textContext.append("</th>");
			textContext.append("<th width=\"120\">");
			textContext.append("密码设备时限");
			textContext.append("</th>");
			textContext.append("</tr>");
			Map paramMap=null;
			Map workDayMap=null;
			for (int i = 0; i < listLockMsg.size(); i++) {
				tempLockMsgMap = (Map) listLockMsg.get(i);
				textContext.append("<tr>");
				textContext.append("<td align=\"center\">");
				textContext.append(String.valueOf(tempLockMsgMap
						.get("CUST_NAME")));
				textContext.append("</td>");
				textContext.append("<td align=\"center\">");
				textContext.append(String.valueOf(tempLockMsgMap
						.get("LINK_NAME")));
				textContext.append("</td>");
				textContext.append("<td align=\"center\">");
				textContext.append(String.valueOf(tempLockMsgMap
						.get("LINK_MOBILE")));
				textContext.append("</td>");
				textContext.append("<td align=\"center\">");
				textContext.append(String.valueOf(tempLockMsgMap
						.get("LINK_EMAIL")));
				textContext.append("</td>");
				textContext.append("<td align=\"center\">");
				textContext.append(String.valueOf(tempLockMsgMap
						.get("MODEL_SPEC")));
				textContext.append("</td>");
				textContext.append("<td align=\"center\">");
				textContext.append(String.valueOf(tempLockMsgMap
						.get("THING_NUMBER")));
				textContext.append("</td>");
				textContext.append("<td align=\"center\">");
				//TODO
				paramMap=new HashMap();
				paramMap.put("query_date", tempLockMsgMap.get("NEXT_LOCK_DATE"));
				
				String strDate="";
				if("全解".equals(tempLockMsgMap.get("NEXT_LOCK_DATE"))){
					strDate="全解";
				}else{
					try {
						workDayMap = (Map) DataAccessor.query(
								"financeDecomposeReport.getTodayIsHD", paramMap,
								DataAccessor.RS_TYPE.MAP);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					
					if (workDayMap != null) {
						List queryDayList = null;
						try {
							queryDayList = (List) DataAccessor.query(
									"financeDecomposeReport.getQueryDayList", paramMap,
									DataAccessor.RS_TYPE.LIST);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						for (int k = 0; k < queryDayList.size(); k++) {
							if (null != (Map) queryDayList.get(k)) {
								if (((Map) queryDayList.get(k)).get("DAY_TYPE").equals("WD")) {
									strDate=String.valueOf(((Map) queryDayList.get(k)).get("DATE"));
									break;
								}
							}
						}
					}else{
						strDate=String.valueOf(tempLockMsgMap.get("NEXT_LOCK_DATE"));
					}
				}
				textContext.append(strDate);
				textContext.append("</td>");
				textContext.append("</tr>");
				try {
					DataAccessor
							.getSession()
							.update("financeDecomposeReport.updateLockMsgEmailSendFlag",
									tempLockMsgMap);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			textContext.append("</table>");
			textContext.append("</body></html>");
			mailSettingTo.setEmailContent(textContext.toString());
			try {
				mailUtilService.sendMail(mailSettingTo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	public void sendCustLinkerEmail(Map context,SqlMapClient sqlMapper) throws MessagingException, Exception {
		MailSettingTo mailSettingTo = new MailSettingTo();
		List listLockMsg = (List) context.get("LISTLOCKMSG");
		String RECT_ID = String.valueOf(context.get("RECT_ID"));
		Map temp;
		Map tempLockMsgMap;
		StringBuffer mailTo = new StringBuffer();
		if (listLockMsg != null) {
			mailTo.append("lune@tacleasing.cn");
			if (context.get("TOEMAIL") != null && !"".equals(String.valueOf(context.get("TOEMAIL")))) {
				mailTo.append(";");
				mailTo.append(String.valueOf(context.get("TOEMAIL")));
			}
			
			mailSettingTo.setEmailTo(mailTo.toString());
			mailSettingTo.setEmailSubject("解码");
			/*if (context.get("TOEMAIL_SENSOR") != null) {
				String user_id = LeaseUtil.getSensorIdByCreditId(String.valueOf(LeaseUtil.getCreditIdByRectId(RECT_ID)));
				mailSettingTo.setEmailCc(String.valueOf(context.get("TOEMAIL_SENSOR")) + ";" + LeaseUtil.getAssistantEmailByUserId(user_id));
			}*/
			
			StringBuilder textContext = new StringBuilder();
			textContext.append("<html><head></head><body>");
			textContext.append("您好！");
			textContext.append("      我是【裕融租赁有限公司】 业管课，已通知供应商帮您解码，如有解码问题请致电: 18913510191");

			textContext.append("<br>");
			textContext
					.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">");
			textContext.append("<tr>");
			textContext.append("<th width=\"200\">");
			textContext.append("供应商");
			textContext.append("</th>");
			textContext.append("<th width=\"60\">");
			textContext.append("联系人");
			textContext.append("</th>");
			textContext.append("<th width=\"80\">");
			textContext.append("手机");
			textContext.append("</th>");
			textContext.append("<th width=\"200\">");
			textContext.append("E-mail");
			textContext.append("</th>");
			textContext.append("<th width=\"120\">");
			textContext.append("密码设备时限");
			textContext.append("</th>");
			textContext.append("</tr>");
			for (int i = 0; i < listLockMsg.size(); i++) {
				tempLockMsgMap = (Map) listLockMsg.get(i);
				textContext.append("<tr>");
				textContext.append("<td align=\"center\">");
				textContext.append(String.valueOf(tempLockMsgMap.get("BRAND")));
				textContext.append("</td>");
				textContext.append("<td align=\"center\">");
				textContext.append(String.valueOf(tempLockMsgMap
						.get("RENTER_NAME")));
				textContext.append("</td>");
				textContext.append("<td align=\"center\">");
				textContext.append(String.valueOf(tempLockMsgMap
						.get("RENTER_PHONE")));
				textContext.append("</td>");
				textContext.append("<td align=\"center\">");
				textContext.append(String.valueOf(tempLockMsgMap
						.get("RENTER_EMAIL")));
				textContext.append("</td>");
				textContext.append("<td align=\"center\">");
				textContext.append(String.valueOf(tempLockMsgMap
						.get("NEXT_LOCK_DATE")));
				textContext.append("</td>");
				textContext.append("</tr>");
				try {
					sqlMapper.update("financeDecomposeReport.updateLockMsgCustLinkerEmailSendFlag",
									tempLockMsgMap);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			textContext.append("</table>");
			textContext.append("</body></html>");

			mailSettingTo.setEmailContent(textContext.toString());
			try {
				mailUtilService.sendMail(mailSettingTo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// 发送间接锁码Email
	public void sendDirectLockEmail() {
		Map context = new HashMap();

		List listRenterEmail = new ArrayList();
		List listLockMsg = new ArrayList();
		List listLikerEmail = new ArrayList();
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		Map tempMap;
		
		String strBrandMessage = "你好！我是【裕融租赁有限公司】 业管课，已将需要解码之客户明细发送至您的邮箱请查收并及时处理！";
		String strCustMessage = "你好！我是【裕融租赁有限公司】 业管课，已通知供应商帮您解码，如有解码问题请致电供应商，联系方式已发送至您的邮箱请及时查收！";
		System.out.println("--------  发送间接锁码的Email 开始------------------");
		try {
			
			listRenterEmail = (List) DataAccessor.query(
					"financeDecomposeReport.getDirectLockRenterEmail", context,
					DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < listRenterEmail.size(); i++) {
				sqlMapper.startTransaction();
				tempMap = (Map) listRenterEmail.get(i);
				listLockMsg = (List) DataAccessor
						.query("financeDecomposeReport.getDirectLockRenterEmailByEmail",
								tempMap, DataAccessor.RS_TYPE.LIST);
				context.put("TOEMAIL", tempMap.get("RENTER_EMAIL"));
				context.put("LISTLOCKMSG", listLockMsg);

				if (null != tempMap.get("RENTER_EMAIL")
						&& !"".equals(tempMap.get("RENTER_EMAIL"))) {
					this.sendBrandRenterEmail(context);
				}

				tempMap.put("MESSAGE", strBrandMessage);
				tempMap.put("MTEL", tempMap.get("RENTER_PHONE"));

				// 给供应商发短信
				if (null != tempMap.get("RENTER_PHONE")
						&& !"".equals(tempMap.get("RENTER_PHONE"))) {
					sqlMapper.insert(
							"financeDecomposeReport.createSendMsg", tempMap);
				}
				sqlMapper.commitTransaction();
			}

			listLikerEmail = (List) DataAccessor.query(
					"financeDecomposeReport.getDirectLockLinkerEmail", context,
					DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < listLikerEmail.size(); i++) {
				sqlMapper.startTransaction();
				tempMap = (Map) listLikerEmail.get(i);
				listLockMsg = (List) DataAccessor
						.query("financeDecomposeReport.getDirectLockLinkerEmailByMobile",
								tempMap, DataAccessor.RS_TYPE.LIST);
				context.put("TOEMAIL", tempMap.get("LINK_EMAIL"));
				context.put("TOEMAIL_SENSOR", tempMap.get("SENSOR_MAIL"));
				context.put("LISTLOCKMSG", listLockMsg);
				context.put("RECT_ID", tempMap.get("RECT_ID"));
				// this.testSetAttachMail(context);

				tempMap.put("MESSAGE", strCustMessage);
				tempMap.put("MTEL", tempMap.get("LINK_MOBILE"));

				/*if (null != tempMap.get("LINK_EMAIL")
						&& !"".equals(tempMap.get("LINK_EMAIL"))) {*/
					this.sendCustLinkerEmail(context,sqlMapper);
				/*}*/

				// 给客户联系人发短息
				if (null != tempMap.get("LINK_MOBILE")
						&& !"".equals(tempMap.get("LINK_MOBILE"))
						&& !"13823704615".equals(tempMap.get("LINK_MOBILE"))) {
					sqlMapper.insert(
							"financeDecomposeReport.createSendMsg", tempMap);
				}
				sqlMapper.commitTransaction();
			}
			
			System.out.println("--------  发送间接锁码的Email 成功 ------------------");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("-------- 发送间接锁码的Email 失败 ------------------");
		} finally {
			try {
				sqlMapper.endTransaction();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	// Add by Michael 2012 6-19 判断邮箱地址是否合法
	// [a-zA-Z0-9][a-zA-Z0-9._-]{2,16}[a-zA-Z0-9]@[a-zA-Z0-9]+.[a-zA-Z0-9]+
	// 验证代码如下：
	public static boolean isVaildEmail(String email) {
		// String
		// emailPattern="[a-zA-Z0-9][a-zA-Z0-9._-]{2,16}[a-zA-Z0-9]@[a-zA-Z0-9]+.[a-zA-Z0-9]+";
		//String emailPattern = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		//String emailPattern = "\\p{Alpha}\\w{2,15}[@][a-z0-9]{3,}[.]\\p{Lower}{2,}";
		//String emailPattern = "\\w+(\\.\\w+)*@\\w+(\\.\\w+)+";
		
		String emailPattern1 ="^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		String emailPattern2 = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$" ;  
		String aa="1.休假申请：<a href ='###' onclick='if(typeof(CreateNewTabWin) == 'function'){CreateNewTabWin('../JHSoft.Web.HrmAttendance/Attendance_Leave_Approve.aspx?Vmt3SVhrRzBCTWVUU1BRUythQTJBN1BjSHlKVkVrREdDeTZOcFdUcStoNE9BVjB1NDFLcGhES2ZkQlRsMWlUeFZtVGc2NEZwRGJjeDQ5aHBTSDJoTkFNV2tIOFhHRmllSTFubU43eVBBQkVZMlJPdjhxOEsrMDFWS21McXY3MFpEckl6VjYvYjdpUT0=&&');} else{parent.CreateNewTabWin('../JHSoft.Web.HrmAttendance/Attendance_Leave_Approve.aspx?Vmt3SVhrRzBCTWVUU1BRUythQTJBN1BjSHlKVkVrREdDeTZOcFdUcStoNE9BVjB1NDFLcGhES2ZkQlRsMWlUeFZtVGc2NEZwRGJjeDQ5aHBTSDJoTkFNV2tIOFhHRmllSTFubU43eVBBQkVZMlJPdjhxOEsrMDFWS21McXY3MFpEckl6VjYvYjdpUT0=&&');}'>病假申请</a>"; 
		boolean result = Pattern.matches(emailPattern2, email);
		String rrr = "(?:<a[^>]*>)(.*?)(?:<\\/a[^>]*>)/gi";	
		aa.matches(rrr);
		System.out.println(aa.matches(rrr));
		return result;
	}

	// 发送直接锁码Email
	public void sendAllDirectLockCodeEmail() throws Exception {
		Map context = new HashMap();

		List listRenterEmail = new ArrayList();
		List listLockMsg = new ArrayList();
		List listLikerEmail = new ArrayList();
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		Map tempMap;

		String strCustMessage = "你好！我是【裕融租赁有限公司】 业管课，机器密码已经发送到您的邮箱请及时查收！";
		System.out.println("--------  发送直接锁码的Email 开始------------------");
		try {
			listLikerEmail = (List) DataAccessor.query(
					"financeDecomposeReport.getDirectLockCodeCustEmail",
					context, DataAccessor.RS_TYPE.LIST);
			
			//Add by Michael 2012 08-20 增加如果没有查到资料则返回
			if (listLikerEmail.size()==0){
				return ;
			}
			
			for (int i = 0; i < listLikerEmail.size(); i++) {
				sqlMapper.startTransaction();
				tempMap = (Map) listLikerEmail.get(i);
				listLockMsg = (List) DataAccessor
						.query("financeDecomposeReport.getDirectLockCodeCustEmailByMobile",
								tempMap, DataAccessor.RS_TYPE.LIST);
				context.put("TOEMAIL", tempMap.get("RENTER_EMAIL"));
				context.put("TOEMAIL_SENSOR", tempMap.get("SENSOR_MAIL"));
				context.put("LISTLOCKMSG", listLockMsg);
				context.put("RECT_ID", tempMap.get("RECT_ID"));

				tempMap.put("MESSAGE", strCustMessage);
				tempMap.put("MTEL", tempMap.get("RENTER_PHONE"));

				this.sendDirectLockCodeCustLinkerEmail(context);

				// 给客户联系人发短息
				if (null != tempMap.get("RENTER_PHONE")
						&& !"".equals(tempMap.get("RENTER_PHONE"))) {
					sqlMapper.insert(
							"financeDecomposeReport.createSendMsg", tempMap);
				}
				sqlMapper.commitTransaction();
			}
			
			System.out.println("--------  发送直接锁码的Email 成功 ------------------");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("-------- 发送直接锁码的Email 失败 ------------------");
			throw e;
		} finally {
			try {
				System.out.println("-------- 发送直接锁码的Email 结束 ------------------");
				sqlMapper.endTransaction();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void sendDirectLockCodeCustLinkerEmail(Map context)
			throws MessagingException, SQLException {
		MailSettingTo mailSettingTo = new MailSettingTo();
		/**
		 * emailTo emailCc emailBcc emailSubject emailContent emailAttachPath
		 * createBy 创建人的userId
		 */
		List listLockMsg = (List) context.get("LISTLOCKMSG");
		Map temp;
		Map tempLockMsgMap;
		String filePathString = "";
		String toemail_sensor = null;
		if (listLockMsg != null && listLockMsg.size()>0) {
			toemail_sensor = (String) context.get("TOEMAIL_SENSOR");
			mailSettingTo.setEmailTo(String.valueOf(context.get("TOEMAIL")));
			mailSettingTo.setEmailSubject("解码");
			String RECT_ID = String.valueOf(context.get("RECT_ID"));
			String user_id = LeaseUtil.getSensorIdByCreditId(String.valueOf(LeaseUtil.getCreditIdByRectId(RECT_ID)));
			
			mailSettingTo.setEmailCc("lune@tacleasing.cn" + (StringUtils.isEmpty(toemail_sensor) ? "" : (";" + toemail_sensor)) + ";" + LeaseUtil.getAssistantEmailByUserId(user_id));
			StringBuilder textContext = new StringBuilder();
			textContext.append("<html><head></head><body>");
			textContext.append("您好！");
			textContext
					.append("      我是【裕融租赁有限公司】 业管课，如有解码问题请致电业管课：Tel:  18913510191 ");

			textContext.append("<br>");
			textContext.append("<br>");
			textContext
					.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">");
			textContext.append("<tr>");
			textContext.append("<th width=\"200\">");
			textContext.append("客户名称");
			textContext.append("</th>");
			textContext.append("<th width=\"200\">");
			textContext.append("机器名称");
			textContext.append("</th>");
			textContext.append("<th width=\"120\">");
			textContext.append("型号");
			textContext.append("</th>");
			textContext.append("<th width=\"100\">");
			textContext.append("机号");
			textContext.append("</th>");
			textContext.append("<th width=\"120\">");
			textContext.append("密码");
			textContext.append("</th>");
			textContext.append("<th width=\"200\">");
			textContext.append("密码文件名");
			textContext.append("</th>");
			textContext.append("</tr>");
			for (int i = 0; i < listLockMsg.size(); i++) {
				tempLockMsgMap = (Map) listLockMsg.get(i);
				textContext.append("<tr>");
				textContext.append("<td align=\"center\">");
				textContext.append(String.valueOf(tempLockMsgMap
						.get("CUST_NAME")));
				textContext.append("</td>");
				textContext.append("<td align=\"center\">");
				textContext.append(String.valueOf(tempLockMsgMap
						.get("KIND_NAME")));
				textContext.append("</td>");
				textContext.append("<td align=\"center\">");
				textContext.append(String.valueOf(tempLockMsgMap
						.get("MODEL_SPEC")));
				textContext.append("</td>");
				textContext.append("<td align=\"center\">");
				textContext.append(String.valueOf(tempLockMsgMap
						.get("THING_NUMBER")));
				textContext.append("</td>");
				textContext.append("<td align=\"center\">");
				textContext.append(String.valueOf(tempLockMsgMap
						.get("PASSWORDS")));
				textContext.append("</td>");
				textContext.append("<td align=\"center\"><a href='"+String.valueOf(tempLockMsgMap
						.get("FILE_NAME"))+"'>");
				textContext.append(String.valueOf(tempLockMsgMap
						.get("FILE_NAME")));
				textContext.append("</td>");
				textContext.append("</tr>");
				if (null != tempLockMsgMap.get("PATH")) {
					if ("".equals(filePathString)) {
						filePathString = "\\"
								+ "\\"+LeaseUtil.getIPAddress()+"\\home\\filsoft\\financelease\\upload\\lockcode\\password"
								+ String.valueOf(tempLockMsgMap.get("PATH"));
					} else {
						filePathString = filePathString
								+ ";"
								+ "\\"
								+ "\\"+LeaseUtil.getIPAddress()+"\\home\\filsoft\\financelease\\upload\\lockcode\\password"
								+ String.valueOf(tempLockMsgMap.get("PATH"));
					}
				}
				
				try {
					DataAccessor
					.getSession()
					.update("financeDecomposeReport.updateLockMsgEmailSendFlag",
							tempLockMsgMap);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			textContext.append("</table>");
			textContext.append("</body></html>");
			mailSettingTo.setEmailAttachPath(filePathString);
			mailSettingTo.setEmailContent(textContext.toString());
			try {
				mailUtilService.sendMail(mailSettingTo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*
	 * 将没有密码和密码附件的信息发送给业管人员
	 */
	public void sendAllDirectLockCodeNoPassworsEmail() {
		Map context = new HashMap();

		List listRenterEmail = new ArrayList();
		List listLockMsg = new ArrayList();
		List listLikerEmail = new ArrayList();
		Map tempMap;
		System.out.println("将没有密码和密码附件的信息发送给业管人员----开始");
		try {
			DataAccessor.getSession().startTransaction();

			listLikerEmail = (List) DataAccessor
					.query("financeDecomposeReport.getDirectLockCodeCustNoPasswordsEmail",
							context, DataAccessor.RS_TYPE.LIST);
			if(listLikerEmail.size()>0){
				context.put("LISTLOCKMSG", listLikerEmail);
				this.sendDirectLockCodeNoPasswordsCustLinkerEmail(context);
			}
			DataAccessor.getSession().commitTransaction();
			System.out.println("将没有密码和密码附件的信息发送给业管人员----结束");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				DataAccessor.getSession().endTransaction();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void sendDirectLockCodeNoPasswordsCustLinkerEmail(Map context)
			throws MessagingException {
		MailSettingTo mailSettingTo = new MailSettingTo();
		/**
		 * emailTo emailCc emailBcc emailSubject emailContent emailAttachPath
		 * createBy 创建人的userId
		 */
		List listLockMsg = (List) context.get("LISTLOCKMSG");
		Map temp;
		Map tempLockMsgMap;
		if (listLockMsg != null) {

			mailSettingTo.setEmailTo("lune@tacleasing.cn");
			mailSettingTo.setEmailSubject("直接锁码没有密码及密码附件案件");
			mailSettingTo.setEmailCc("michael@tacleasing.cn");
			StringBuilder textContext = new StringBuilder();
			textContext.append("<html><head></head><body>");
			textContext.append("以下直接锁码案件没有密码及附件");

			textContext.append("<br>");
			textContext.append("<br>");
			textContext
					.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">");
			textContext.append("<tr>");
			textContext.append("<th width=\"80\">");
			textContext.append("合同号");
			textContext.append("</th>");
			textContext.append("<th width=\"200\">");
			textContext.append("承租人");
			textContext.append("</th>");
			textContext.append("<th width=\"200\">");
			textContext.append("机器名称");
			textContext.append("</th>");
			textContext.append("<th width=\"120\">");
			textContext.append("型号");
			textContext.append("</th>");
			textContext.append("<th width=\"100\">");
			textContext.append("机号");
			textContext.append("</th>");
			textContext.append("<th width=\"120\">");
			textContext.append("联系人");
			textContext.append("</th>");
			textContext.append("<th width=\"120\">");
			textContext.append("手机");
			textContext.append("</th>");
			textContext.append("<th width=\"120\">");
			textContext.append("Email");
			textContext.append("</th>");
			textContext.append("</tr>");
			for (int i = 0; i < listLockMsg.size(); i++) {
				tempLockMsgMap = (Map) listLockMsg.get(i);
				textContext.append("<tr>");
				textContext.append("<td align=\"center\">");
				textContext.append(String.valueOf(tempLockMsgMap
						.get("LEASE_CODE")));
				textContext.append("</td>");
				textContext.append("<td align=\"center\">");
				textContext.append(String.valueOf(tempLockMsgMap
						.get("CUST_NAME")));
				textContext.append("</td>");
				textContext.append("<td align=\"center\">");
				textContext.append(String.valueOf(tempLockMsgMap
						.get("KIND_NAME")));
				textContext.append("</td>");
				textContext.append("<td align=\"center\">");
				textContext.append(String.valueOf(tempLockMsgMap
						.get("MODEL_SPEC")));
				textContext.append("</td>");
				textContext.append("<td align=\"center\">");
				textContext.append(String.valueOf(tempLockMsgMap
						.get("THING_NUMBER")));
				textContext.append("</td>");
				textContext.append("<td align=\"center\">");
				textContext.append(String.valueOf(tempLockMsgMap
						.get("RENTER_NAME")));
				textContext.append("</td>");
				textContext.append("<td align=\"center\">");
				textContext.append(String.valueOf(tempLockMsgMap
						.get("RENTER_PHONE")));
				textContext.append("</td>");
				textContext.append("<td align=\"center\">");
				textContext.append(String.valueOf(tempLockMsgMap
						.get("RENTER_EMAIL")));
				textContext.append("</td>");
				textContext.append("</tr>");
			}
			textContext.append("</table>");
			textContext.append("</body></html>");

			mailSettingTo.setEmailContent(textContext.toString());
			try {
				mailUtilService.sendMail(mailSettingTo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// Add by Michael 2012 08-08 将已拨款，且缺件的案件发送给相关人员
	public void sendAllUnCompletedFileList() throws MessagingException {
		//判断今天是否是工作日，如果是工作日才发送邮件,否则不发邮件
		try {
			if (isWorkingDay()==false) {
				return; 
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {
			Map tempMap = new HashMap();
			List listUnCompletedFile = null;
			listUnCompletedFile = (List) DataAccessor.query(
					"rentFile.getAllUnCompletedFileList", tempMap,
					DataAccessor.RS_TYPE.LIST);
			
			//如果没有待补文件则直接返回，不需要再发送Mail
			if (listUnCompletedFile.size()==0){
				return;
			}
			
			StringBuffer mailContent = new StringBuffer();
			mailContent.append("<html><head></head>");
			mailContent
					.append("<style>.rhead { background-color: #006699}"
							+ ".Body2 { font-family: Arial, Helvetica, sans-serif; font-weight: normal; color: #000000; font-size: 9pt; text-decoration: none}"
							+ ".Body2BoldWhite2 { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #FFFFFF; font-size: 11pt; text-decoration: none }"
							+ ".Body2Bold { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #000000; font-size: 8pt; text-decoration: none }"
							+ ".r11 {  background-color: #C4E2EC}"
							+ ".r12 { background-color: #D2EFF0}</style><body>");
			mailContent.append("<font size='3'><b>各位好:<b><br></font>"
					+ "<font size='2'>麻烦查收待补，谢谢咯~</font><br><br>");
			mailContent
					.append("<table border='1' cellspacing='0' width='1050px;' cellpadding='0'>"
							+ "<tr class='rhead'>"
							+ "<td class='Body2BoldWhite2' style='width:30px;' align='center'>序号</td>"
							+ "<td class='Body2BoldWhite2' style='width:100px;' align='center'>合同号</td>"
							+ "<td class='Body2BoldWhite2' align='center'>客户名称</td>"
							+ "<td class='Body2BoldWhite2' align='center'>待补文件名称</td>"
							+ "<td class='Body2BoldWhite2' align='center'>办事处</td>"
							+ "<td class='Body2BoldWhite2' align='center'>经办人</td>"
							+ "<td class='Body2BoldWhite2' align='center'>供应商</td>"
							+ "<td class='Body2BoldWhite2' align='center'>拨款日</td>"
							+ "<td class='Body2BoldWhite2' align='center'>应补回日</td>"
							+ "<td class='Body2BoldWhite2' align='center'>延迟天数</td>"
							+ "<td class='Body2BoldWhite2' align='center'>待补原因</td>"
							+ "<td class='Body2BoldWhite2' align='center'>拨款方式</td>"
							+ "<td class='Body2BoldWhite2' align='center'>备注</td></tr>");
			int num = 0;
			if (listUnCompletedFile != null) {
				for (int i = 0; i < listUnCompletedFile.size(); i++) {
					num++;
					tempMap = (Map) listUnCompletedFile.get(i);
					mailContent.append("<tr class='r12'>" + "<td class=body2 >"
							+ num
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) listUnCompletedFile.get(i))
									.get("LEASE_CODE")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) listUnCompletedFile.get(i))
									.get("CUST_NAME")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) listUnCompletedFile.get(i))
									.get("FILE_NAME")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) listUnCompletedFile.get(i))
									.get("DECP_NAME_CN")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listUnCompletedFile.get(i)).get("NAME")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) listUnCompletedFile.get(i)).get("BRAND")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listUnCompletedFile.get(i))
									.get("FINANCECONTRACT_DATE")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listUnCompletedFile.get(i))
									.get("SHOULD_FINISH_DATE")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listUnCompletedFile.get(i))
									.get("DELAY_DAY")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) listUnCompletedFile.get(i))
									.get("ISSURE_REASON")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) listUnCompletedFile.get(i)).get("TYPE")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) listUnCompletedFile.get(i))
									.get("FILE_MEMO") + "</td></tr>");
				}
			}
			mailContent.append("</table>");
			mailContent.append("</body></html>");
			MailSettingTo mailSettingTo = new MailSettingTo();
			mailSettingTo.setEmailContent(mailContent.toString());
			mailUtilService.sendMail(201, mailSettingTo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void queryCarPayAccountDetailForSendMail() throws Exception{
		queryCommercialCarPayAccountDetailForSendMail();
		queryPassengerCarPayAccountDetailForSendMail();
	}
	
	public void queryCommercialCarPayAccountDetailForSendMail() throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map workDayMap = null;
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		paramMap.put("query_date", sf.format(new Date()));
		workDayMap = (Map) DataAccessor.query(
				"financeDecomposeReport.getTodayIsWD", paramMap,
				DataAccessor.RS_TYPE.MAP);
		if (workDayMap != null) {
			List queryDayList = new ArrayList();
			queryDayList = (List) DataAccessor.query(
					"financeDecomposeReport.getQueryDayList", paramMap,
					DataAccessor.RS_TYPE.LIST);
			Map queryDay;
			String strDate = "";
			for (int i = 0; i < queryDayList.size(); i++) {
				queryDay = (Map) queryDayList.get(i);
				if (null != queryDay) {
					if (queryDay.get("DAY_TYPE").equals("WD")) {
						if (i==0){
							strDate += "convert(date,'"
									+ String.valueOf(queryDay.get("DATE")) + "')";
						}else{
							strDate += ",convert(date,'"
									+ String.valueOf(queryDay.get("DATE")) + "')";
						}
						break;
					}else{
						if (i==0){
							strDate += "convert(date,'"
									+ String.valueOf(queryDay.get("DATE")) + "')";
						}else{
							strDate += ",convert(date,'"
									+ String.valueOf(queryDay.get("DATE")) + "')";
						}
					}
				}
			}
			paramMap.put("DATES", strDate);
			paramMap.put("contract_type", 2);
			List carPayApplyInfoList = new ArrayList();
			carPayApplyInfoList = (List) DataAccessor.query(
					"financeDecomposeReport.getCarPayApplyInfo", paramMap,
					DataAccessor.RS_TYPE.LIST);
			if (carPayApplyInfoList.size() > 0) {
				StringBuffer mailContent = new StringBuffer();
				mailContent.append("<html><head></head>");
				mailContent
						.append("<style>.rhead { background-color: #006699}"
								+ ".Body2 { font-family: Arial, Helvetica, sans-serif; font-weight: normal; color: #000000; font-size: 9pt; text-decoration: none}"
								+ ".Body2BoldWhite2 { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #FFFFFF; font-size: 11pt; text-decoration: none }"
								+ ".Body2Bold { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #000000; font-size: 8pt; text-decoration: none }"
								+ ".r11 {  background-color: #C4E2EC}"
								+ ".r12 { background-color: #D2EFF0}</style><body>");
				mailContent.append("<font size='3'><b>各位好:<b><br></font>"
						+ "<font size='2'>以下为重车扣款申请提醒列表，请参阅~</font><br><br>");
				mailContent
						.append("<table border='1' cellspacing='0' width='1050px;' cellpadding='0'>"
								+ "<tr class='rhead'>"
								+ "<td class='Body2BoldWhite2' style='width:40px;' align='center'>序号</td>"
								+ "<td class='Body2BoldWhite2' style='width:100px;' align='center'>合同号</td>"
								+ "<td class='Body2BoldWhite2' align='center'>公司名称</td>"
								+ "<td class='Body2BoldWhite2' align='center'>实际各期租金</td>"
								+ "<td class='Body2BoldWhite2' align='center'>租金应缴日</td>"
								+ "<td class='Body2BoldWhite2' align='center'>还款期数</td>"
								+ "<td class='Body2BoldWhite2' align='center'>还款人</td>"
								+ "<td class='Body2BoldWhite2' align='center'>开户行</td>"
								+ "<td class='Body2BoldWhite2' align='center'>还款账号</td>"
								+ "<td class='Body2BoldWhite2' align='center'>证件号码</td>"
								+ "<td class='Body2BoldWhite2' align='center'>扣款日</td>"
								+ "<td class='Body2BoldWhite2' align='center'>扣款失败</td>"
								+ "<td class='Body2BoldWhite2' align='center'>备注</td></tr>");
				int num = 0;
				for (int i = 0; i < carPayApplyInfoList.size(); i++) {
					num++;
					mailContent.append("<tr class='r12'>" + "<td class=body2 >"
							+ num
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) carPayApplyInfoList.get(i))
									.get("LEASE_CODE")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) carPayApplyInfoList.get(i))
									.get("CUST_NAME")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) carPayApplyInfoList.get(i))
									.get("IRR_MONTH_PRICE")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) carPayApplyInfoList.get(i))
									.get("PAY_DATE")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) carPayApplyInfoList.get(i))
									.get("PERIOD_NUM")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) carPayApplyInfoList.get(i)).get("PAY_MAN")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) carPayApplyInfoList.get(i))
									.get("BANK_NAME")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) carPayApplyInfoList.get(i))
									.get("BANK_ACCOUNT") + "</td>"
							+ "<td class=body2 >"
							+ ((Map) carPayApplyInfoList.get(i)).get("ID_CAR")
							+ "</td>" + "<td class=body2 >" + "   " + "</td>"
							+ "<td class=body2 >" + "   " + "</td>"
							+ "<td class=body2 >" + "   " + "</td></tr>");
				}
				mailContent.append("</table>");
				mailContent.append("</body></html>");
				MailSettingTo mailSettingTo = new MailSettingTo();
				mailSettingTo.setEmailContent(mailContent.toString());
				mailUtilService.sendMail(202, mailSettingTo);
			}
		}
	}
	
	public void queryPassengerCarPayAccountDetailForSendMail() throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map workDayMap = null;
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		paramMap.put("query_date", sf.format(new Date()));
		workDayMap = (Map) DataAccessor.query(
				"financeDecomposeReport.getTodayIsWD", paramMap,
				DataAccessor.RS_TYPE.MAP);
		if (workDayMap != null) {
			List queryDayList = new ArrayList();
			queryDayList = (List) DataAccessor.query(
					"financeDecomposeReport.getQueryDayList", paramMap,
					DataAccessor.RS_TYPE.LIST);
			Map queryDay;
			String strDate = "";
			for (int i = 0; i < queryDayList.size(); i++) {
				queryDay = (Map) queryDayList.get(i);
				if (null != queryDay) {
					if (queryDay.get("DAY_TYPE").equals("WD")) {
						if (i==0){
							strDate += "convert(date,'"
									+ String.valueOf(queryDay.get("DATE")) + "')";
						}else{
							strDate += ",convert(date,'"
									+ String.valueOf(queryDay.get("DATE")) + "')";
						}
						break;
					}else{
						if (i==0){
							strDate += "convert(date,'"
									+ String.valueOf(queryDay.get("DATE")) + "')";
						}else{
							strDate += ",convert(date,'"
									+ String.valueOf(queryDay.get("DATE")) + "')";
						}
					}
				}
			}
			paramMap.put("DATES", strDate);
			paramMap.put("contract_type", 3);
			List carPayApplyInfoList = new ArrayList();
			carPayApplyInfoList = (List) DataAccessor.query(
					"financeDecomposeReport.getCarPayApplyInfo", paramMap,
					DataAccessor.RS_TYPE.LIST);
			if (carPayApplyInfoList.size() > 0) {
				StringBuffer mailContent = new StringBuffer();
				mailContent.append("<html><head></head>");
				mailContent
						.append("<style>.rhead { background-color: #006699}"
								+ ".Body2 { font-family: Arial, Helvetica, sans-serif; font-weight: normal; color: #000000; font-size: 9pt; text-decoration: none}"
								+ ".Body2BoldWhite2 { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #FFFFFF; font-size: 11pt; text-decoration: none }"
								+ ".Body2Bold { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #000000; font-size: 8pt; text-decoration: none }"
								+ ".r11 {  background-color: #C4E2EC}"
								+ ".r12 { background-color: #D2EFF0}</style><body>");
				mailContent.append("<font size='3'><b>各位好:<b><br></font>"
						+ "<font size='2'>以下为乘用车车扣款申请提醒列表，请参阅~</font><br><br>");
				mailContent
						.append("<table border='1' cellspacing='0' width='1050px;' cellpadding='0'>"
								+ "<tr class='rhead'>"
								+ "<td class='Body2BoldWhite2' style='width:40px;' align='center'>序号</td>"
								+ "<td class='Body2BoldWhite2' style='width:100px;' align='center'>合同号</td>"
								+ "<td class='Body2BoldWhite2' align='center'>公司名称</td>"
								+ "<td class='Body2BoldWhite2' align='center'>实际各期租金</td>"
								+ "<td class='Body2BoldWhite2' align='center'>租金应缴日</td>"
								+ "<td class='Body2BoldWhite2' align='center'>还款期数</td>"
								+ "<td class='Body2BoldWhite2' align='center'>还款人</td>"
								+ "<td class='Body2BoldWhite2' align='center'>开户行</td>"
								+ "<td class='Body2BoldWhite2' align='center'>还款账号</td>"
								+ "<td class='Body2BoldWhite2' align='center'>证件号码</td>"
								+ "<td class='Body2BoldWhite2' align='center'>扣款日</td>"
								+ "<td class='Body2BoldWhite2' align='center'>扣款失败</td>"
								+ "<td class='Body2BoldWhite2' align='center'>备注</td></tr>");
				int num = 0;
				for (int i = 0; i < carPayApplyInfoList.size(); i++) {
					num++;
					mailContent.append("<tr class='r12'>" + "<td class=body2 >"
							+ num
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) carPayApplyInfoList.get(i))
									.get("LEASE_CODE")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) carPayApplyInfoList.get(i))
									.get("CUST_NAME")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) carPayApplyInfoList.get(i))
									.get("IRR_MONTH_PRICE")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) carPayApplyInfoList.get(i))
									.get("PAY_DATE")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) carPayApplyInfoList.get(i))
									.get("PERIOD_NUM")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) carPayApplyInfoList.get(i)).get("PAY_MAN")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) carPayApplyInfoList.get(i))
									.get("BANK_NAME")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) carPayApplyInfoList.get(i))
									.get("BANK_ACCOUNT") + "</td>"
							+ "<td class=body2 >"
							+ ((Map) carPayApplyInfoList.get(i)).get("ID_CAR")
							+ "</td>" + "<td class=body2 >" + "   " + "</td>"
							+ "<td class=body2 >" + "   " + "</td>"
							+ "<td class=body2 >" + "   " + "</td></tr>");
				}
				mailContent.append("</table>");
				mailContent.append("</body></html>");
				MailSettingTo mailSettingTo = new MailSettingTo();
				mailSettingTo.setEmailContent(mailContent.toString());
				mailUtilService.sendMail(140, mailSettingTo);
			}
		}
	}

	// Add by Michael 2012 08-31 将已拨款，且缺件的案件发送给相关人员
	public void sendAbleSettleRentList() throws MessagingException {
		//判断今天是否是工作日，如果是工作日才发送邮件,否则不发邮件
		try {
			if (isWorkingDay()==false) {
				return; 
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {
			Map tempMap = new HashMap();
			List listAbleSettleRentList = null;
			listAbleSettleRentList = (List) DataAccessor.query(
					"settleManage.queryAbleSettleRentList", tempMap,
					DataAccessor.RS_TYPE.LIST);
			
			//如果没有待补文件则直接返回，不需要再发送Mail
			if (listAbleSettleRentList.size()==0){
				return;
			}
			
			StringBuffer mailContent = new StringBuffer();
			mailContent.append("<html><head></head>");
			mailContent
					.append("<style>.rhead { background-color: #006699}"
							+ ".Body2 { font-family: Arial, Helvetica, sans-serif; font-weight: normal; color: #000000; font-size: 9pt; text-decoration: none}"
							+ ".Body2BoldWhite2 { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #FFFFFF; font-size: 11pt; text-decoration: none }"
							+ ".Body2Bold { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #000000; font-size: 8pt; text-decoration: none }"
							+ ".r11 {  background-color: #C4E2EC}"
							+ ".r12 { background-color: #D2EFF0}</style><body>");
			mailContent.append("<font size='3'><b>各位好:<b><br></font>"
					+ "<font size='2'>以下为可结清案件明细表，请参阅~</font><br><br>");
			mailContent
					.append("<table border='1' cellspacing='0' width='1050px;' cellpadding='0'>"
							+ "<tr class='rhead'>"
							+ "<td class='Body2BoldWhite2' style='width:30px;' align='center'>序号</td>"
							+ "<td class='Body2BoldWhite2' style='width:110px;' align='center'>合同号</td>"
							+ "<td class='Body2BoldWhite2' align='center'>客户名称</td>"
							+ "<td class='Body2BoldWhite2' align='center'>办事处</td>"
							+ "<td class='Body2BoldWhite2' align='center'>业务经办</td>"
							+ "<td class='Body2BoldWhite2' align='center'>租赁期数</td>"
							+ "<td class='Body2BoldWhite2' align='center'>已交期数</td>"
							+ "<td class='Body2BoldWhite2' align='center'>未缴罚息</td>"
							+ "<td class='Body2BoldWhite2' align='center'>未缴期满购买金</td>"
							+ "<td class='Body2BoldWhite2' align='center'>最后一期来款时间</td>"
							+ "<td class='Body2BoldWhite2' align='center'>法务费用</td>"
							+ "<td class='Body2BoldWhite2' align='center'>距今天数</td>"
							+ "<td class='Body2BoldWhite2' align='center'>状态</td></tr>");
			int num = 0;
			if (listAbleSettleRentList != null) {
				for (int i = 0; i < listAbleSettleRentList.size(); i++) {
					num++;
					tempMap = (Map) listAbleSettleRentList.get(i);
					mailContent.append("<tr class='r12'>" + "<td class=body2 >"
							+ num
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) listAbleSettleRentList.get(i))
									.get("LEASE_CODE")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) listAbleSettleRentList.get(i))
									.get("CUST_NAME")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) listAbleSettleRentList.get(i))
									.get("DECP_NAME_CN")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleRentList.get(i)).get("NAME")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) listAbleSettleRentList.get(i)).get("LEASE_PERIOD")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleRentList.get(i))
									.get("LEASE_PERIOD")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleRentList.get(i))
									.get("DUN_PRICE")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleRentList.get(i))
									.get("LGJ")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleRentList.get(i))
									.get("OPPOSINGDATE")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleRentList.get(i))
									.get("TOTALLAWYFEE")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleRentList.get(i))
									.get("DIFF_DAY")
							+ "天</td>"
							+ "<td class=body2 >正常</td></tr>");
					addSendCompletedFileListLog(tempMap);
				}
			}
			mailContent.append("</table>");
			mailContent.append("</body></html>");
			MailSettingTo mailSettingTo = new MailSettingTo();
			mailSettingTo.setEmailContent(mailContent.toString());
			mailUtilService.sendMail(203, mailSettingTo);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//将可结清案件明细表发送给相应的业务经办
	public void sendAbleSettleRentListByPerson()
			throws MessagingException {
		MailSettingTo mailSettingTo = new MailSettingTo();
		Map paramMap =new HashMap();
		List  listAbleSettleEmailBySelf=null;
		try {
		listAbleSettleEmailBySelf = (List) DataAccessor.query(
				"settleManage.queryAbleSettleEmailBySelf", paramMap,
				DataAccessor.RS_TYPE.LIST);
		}catch (Exception e) {
			e.printStackTrace();
		}
		if (listAbleSettleEmailBySelf != null) {
			for (int i = 0; i < listAbleSettleEmailBySelf.size(); i++) {
				
				mailSettingTo.setEmailSubject("可结清案件明细表");
				mailSettingTo.setEmailTo(String.valueOf(((Map) listAbleSettleEmailBySelf.get(i)).get("EMAIL")));
				//mailSettingTo.setEmailCc(String.valueOf(((Map) listAbleSettleEmailBySelf.get(i)).get("UP_EMAIL")));
				paramMap.put("ID", ((Map) listAbleSettleEmailBySelf.get(i)).get("ID"));
				StringBuffer mailContent = new StringBuffer();
				List listAbleSettleRentListSelf=null;
				try{
					listAbleSettleRentListSelf = (List) DataAccessor.query(
							"settleManage.queryAbleSettleRentListSelf", paramMap,
							DataAccessor.RS_TYPE.LIST);
				}catch (Exception e) {
					e.printStackTrace();
				}
				int num = 0;
				if (listAbleSettleRentListSelf != null) {
					mailContent.append("<html><head></head>");
					mailContent
							.append("<style>.rhead { background-color: #006699}"
									+ ".Body2 { font-family: Arial, Helvetica, sans-serif; font-weight: normal; color: #000000; font-size: 9pt; text-decoration: none}"
									+ ".Body2BoldWhite2 { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #FFFFFF; font-size: 11pt; text-decoration: none }"
									+ ".Body2Bold { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #000000; font-size: 8pt; text-decoration: none }"
									+ ".r11 {  background-color: #C4E2EC}"
									+ ".r12 { background-color: #D2EFF0}</style><body>");
					mailContent.append("<font size='3'><b>各位好:<b><br></font>"
							+ "<font size='2'>以下为可结清案件明细表，请参阅~</font><br><br>");
					mailContent
							.append("<table border='1' cellspacing='0' width='1050px;' cellpadding='0'>"
									+ "<tr class='rhead'>"
									+ "<td class='Body2BoldWhite2' style='width:30px;' align='center'>序号</td>"
									+ "<td class='Body2BoldWhite2' style='width:110px;' align='center'>合同号</td>"
									+ "<td class='Body2BoldWhite2' align='center'>客户名称</td>"
									+ "<td class='Body2BoldWhite2' align='center'>业务经办</td>"
									+ "<td class='Body2BoldWhite2' align='center'>租赁期数</td>"
									+ "<td class='Body2BoldWhite2' align='center'>已交期数</td>"
									+ "<td class='Body2BoldWhite2' align='center'>未缴罚息</td>"
									+ "<td class='Body2BoldWhite2' align='center'>未缴期满购买金</td>"
									+ "<td class='Body2BoldWhite2' align='center'>最后一期来款日期</td>"
									+ "<td class='Body2BoldWhite2' align='center'>法务费用</td>"
									+ "<td class='Body2BoldWhite2' align='center'>距今天数</td>"
									+ "<td class='Body2BoldWhite2' align='center'>状态</td></tr>");
					
					for (int j = 0; j < listAbleSettleRentListSelf.size(); j++) {
						num++;
						mailContent.append("<tr class='r12'>" + "<td class=body2 >"
								+ num
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listAbleSettleRentListSelf.get(j))
										.get("LEASE_CODE")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listAbleSettleRentListSelf.get(j))
										.get("CUST_NAME")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleRentListSelf.get(j)).get("NAME")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listAbleSettleRentListSelf.get(j)).get("LEASE_PERIOD")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleRentListSelf.get(j))
										.get("LEASE_PERIOD")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleRentListSelf.get(j))
										.get("DUN_PRICE")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleRentListSelf.get(j))
										.get("LGJ")
								+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleRentListSelf.get(j))
									.get("OPPOSINGDATE")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleRentListSelf.get(j))
									.get("TOTALLAWYFEE")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleRentListSelf.get(j))
									.get("DIFF_DAY")
							+ "天</td>"								
								+ "<td class=body2 >正常</td></tr>");
					}
				}
				mailContent.append("</table>");
				mailContent.append("</body></html>");	
				mailSettingTo.setEmailContent(mailContent.toString());
				try {
					mailUtilService.sendMail(mailSettingTo);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	//将可结清案件明细表发送给相应的办事处  苏州设备
	public void sendAbleSettleRentListForSuzhou()
			throws MessagingException {
		MailSettingTo mailSettingTo = new MailSettingTo();
		Map paramMap =new HashMap();
		List  listAbleSettleEmailByDept=null;
		try {
			listAbleSettleEmailByDept = (List) DataAccessor.query(
				"settleManage.queryAbleSettleRentListSuzhou", paramMap,
				DataAccessor.RS_TYPE.LIST);
		}catch (Exception e) {
			e.printStackTrace();
		}
		if (listAbleSettleEmailByDept != null && listAbleSettleEmailByDept.size()>0) {
			StringBuffer mailContent = new StringBuffer();
			mailSettingTo.setEmailSubject("可结清案件明细表");
			mailContent.append("<html><head></head>");
			mailContent
					.append("<style>.rhead { background-color: #006699}"
						+ ".Body2 { font-family: Arial, Helvetica, sans-serif; font-weight: normal; color: #000000; font-size: 9pt; text-decoration: none}"
						+ ".Body2BoldWhite2 { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #FFFFFF; font-size: 11pt; text-decoration: none }"
						+ ".Body2Bold { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #000000; font-size: 8pt; text-decoration: none }"
						+ ".r11 {  background-color: #C4E2EC}"
						+ ".r12 { background-color: #D2EFF0}</style><body>");
			mailContent.append("<font size='3'><b>各位好:<b><br></font>"
						+ "<font size='2'>以下为可结清案件明细表，请参阅~</font><br><br>");
			mailContent
						.append("<table border='1' cellspacing='0' width='1050px;' cellpadding='0'>"
								+ "<tr class='rhead'>"
								+ "<td class='Body2BoldWhite2' style='width:30px;' align='center'>序号</td>"
								+ "<td class='Body2BoldWhite2' style='width:110px;' align='center'>合同号</td>"
								+ "<td class='Body2BoldWhite2' align='center'>客户名称</td>"
								+ "<td class='Body2BoldWhite2' align='center'>业务经办</td>"
								+ "<td class='Body2BoldWhite2' align='center'>租赁期数</td>"
								+ "<td class='Body2BoldWhite2' align='center'>已交期数</td>"
								+ "<td class='Body2BoldWhite2' align='center'>未缴罚息</td>"
								+ "<td class='Body2BoldWhite2' align='center'>未缴期满购买金</td>"
								+ "<td class='Body2BoldWhite2' align='center'>最后一期来款日期</td>"
								+ "<td class='Body2BoldWhite2' align='center'>法务费用</td>"
								+ "<td class='Body2BoldWhite2' align='center'>距今天数</td>"
								+ "<td class='Body2BoldWhite2' align='center'>状态</td></tr>");
					int num=0;
					for (int j = 0; j < listAbleSettleEmailByDept.size(); j++) {
						num++;
						mailContent.append("<tr class='r12'>" + "<td class=body2 >"
								+ num
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("LEASE_CODE")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("CUST_NAME")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j)).get("NAME")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listAbleSettleEmailByDept.get(j)).get("LEASE_PERIOD")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("LEASE_PERIOD")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("DUN_PRICE")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("LGJ")
								+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleEmailByDept.get(j))
									.get("OPPOSINGDATE")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleEmailByDept.get(j))
									.get("TOTALLAWYFEE")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleEmailByDept.get(j))
									.get("DIFF_DAY")
							+ "天</td>"								
								+ "<td class=body2 >正常</td></tr>");
					}
				
					mailContent.append("</table>");
					mailContent.append("</body></html>");	
					mailSettingTo.setEmailContent(mailContent.toString());
				try {
					mailUtilService.sendMail(204,mailSettingTo);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}

	//将可结清案件明细表发送给相应的办事处  苏州重车
	public void sendAbleSettleRentListForSuzhouCar()
			throws MessagingException {
		MailSettingTo mailSettingTo = new MailSettingTo();
		Map paramMap =new HashMap();
		List  listAbleSettleEmailByDept=null;
		try {
			listAbleSettleEmailByDept = (List) DataAccessor.query(
				"settleManage.queryAbleSettleRentListSuzhouCar", paramMap,
				DataAccessor.RS_TYPE.LIST);
		}catch (Exception e) {
			e.printStackTrace();
		}
		if (listAbleSettleEmailByDept != null && listAbleSettleEmailByDept.size()>0) {
			StringBuffer mailContent = new StringBuffer();
			mailSettingTo.setEmailSubject("可结清案件明细表");
			mailContent.append("<html><head></head>");
			mailContent
					.append("<style>.rhead { background-color: #006699}"
						+ ".Body2 { font-family: Arial, Helvetica, sans-serif; font-weight: normal; color: #000000; font-size: 9pt; text-decoration: none}"
						+ ".Body2BoldWhite2 { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #FFFFFF; font-size: 11pt; text-decoration: none }"
						+ ".Body2Bold { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #000000; font-size: 8pt; text-decoration: none }"
						+ ".r11 {  background-color: #C4E2EC}"
						+ ".r12 { background-color: #D2EFF0}</style><body>");
			mailContent.append("<font size='3'><b>各位好:<b><br></font>"
						+ "<font size='2'>以下为可结清案件明细表，请参阅~</font><br><br>");
			mailContent
						.append("<table border='1' cellspacing='0' width='1050px;' cellpadding='0'>"
								+ "<tr class='rhead'>"
								+ "<td class='Body2BoldWhite2' style='width:30px;' align='center'>序号</td>"
								+ "<td class='Body2BoldWhite2' style='width:110px;' align='center'>合同号</td>"
								+ "<td class='Body2BoldWhite2' align='center'>客户名称</td>"
								+ "<td class='Body2BoldWhite2' align='center'>业务经办</td>"
								+ "<td class='Body2BoldWhite2' align='center'>租赁期数</td>"
								+ "<td class='Body2BoldWhite2' align='center'>已交期数</td>"
								+ "<td class='Body2BoldWhite2' align='center'>未缴罚息</td>"
								+ "<td class='Body2BoldWhite2' align='center'>未缴期满购买金</td>"
								+ "<td class='Body2BoldWhite2' align='center'>最后一期来款日期</td>"
								+ "<td class='Body2BoldWhite2' align='center'>法务费用</td>"
								+ "<td class='Body2BoldWhite2' align='center'>距今天数</td>"
								+ "<td class='Body2BoldWhite2' align='center'>状态</td></tr>");
					int num=0;
					for (int j = 0; j < listAbleSettleEmailByDept.size(); j++) {
						num++;
						mailContent.append("<tr class='r12'>" + "<td class=body2 >"
								+ num
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("LEASE_CODE")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("CUST_NAME")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j)).get("NAME")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listAbleSettleEmailByDept.get(j)).get("LEASE_PERIOD")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("LEASE_PERIOD")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("DUN_PRICE")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("LGJ")
								+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleEmailByDept.get(j))
									.get("OPPOSINGDATE")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleEmailByDept.get(j))
									.get("TOTALLAWYFEE")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleEmailByDept.get(j))
									.get("DIFF_DAY")
							+ "天</td>"									
								+ "<td class=body2 >正常</td></tr>");
					}
				
					mailContent.append("</table>");
					mailContent.append("</body></html>");	
					mailSettingTo.setEmailContent(mailContent.toString());
				try {
					mailUtilService.sendMail(213,mailSettingTo);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}

	//将可结清案件明细表发送给相应的办事处  昆山
	public void sendAbleSettleRentListForKunshan()
			throws MessagingException {
		MailSettingTo mailSettingTo = new MailSettingTo();
		Map paramMap =new HashMap();
		List  listAbleSettleEmailByDept=null;
		try {
			listAbleSettleEmailByDept = (List) DataAccessor.query(
				"settleManage.queryAbleSettleRentListKunshan", paramMap,
				DataAccessor.RS_TYPE.LIST);
		}catch (Exception e) {
			e.printStackTrace();
		}
		if (listAbleSettleEmailByDept != null && listAbleSettleEmailByDept.size()>0) {
			StringBuffer mailContent = new StringBuffer();
			mailSettingTo.setEmailSubject("可结清案件明细表");
			mailContent.append("<html><head></head>");
			mailContent
					.append("<style>.rhead { background-color: #006699}"
						+ ".Body2 { font-family: Arial, Helvetica, sans-serif; font-weight: normal; color: #000000; font-size: 9pt; text-decoration: none}"
						+ ".Body2BoldWhite2 { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #FFFFFF; font-size: 11pt; text-decoration: none }"
						+ ".Body2Bold { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #000000; font-size: 8pt; text-decoration: none }"
						+ ".r11 {  background-color: #C4E2EC}"
						+ ".r12 { background-color: #D2EFF0}</style><body>");
			mailContent.append("<font size='3'><b>各位好:<b><br></font>"
						+ "<font size='2'>以下为可结清案件明细表，请参阅~</font><br><br>");
			mailContent
						.append("<table border='1' cellspacing='0' width='1050px;' cellpadding='0'>"
								+ "<tr class='rhead'>"
								+ "<td class='Body2BoldWhite2' style='width:30px;' align='center'>序号</td>"
								+ "<td class='Body2BoldWhite2' style='width:110px;' align='center'>合同号</td>"
								+ "<td class='Body2BoldWhite2' align='center'>客户名称</td>"
								+ "<td class='Body2BoldWhite2' align='center'>业务经办</td>"
								+ "<td class='Body2BoldWhite2' align='center'>租赁期数</td>"
								+ "<td class='Body2BoldWhite2' align='center'>已交期数</td>"
								+ "<td class='Body2BoldWhite2' align='center'>未缴罚息</td>"
								+ "<td class='Body2BoldWhite2' align='center'>未缴期满购买金</td>"
								+ "<td class='Body2BoldWhite2' align='center'>最后一期来款日期</td>"
								+ "<td class='Body2BoldWhite2' align='center'>法务费用</td>"
								+ "<td class='Body2BoldWhite2' align='center'>距今天数</td>"
								+ "<td class='Body2BoldWhite2' align='center'>状态</td></tr>");
					int num=0;
					for (int j = 0; j < listAbleSettleEmailByDept.size(); j++) {
						num++;
						mailContent.append("<tr class='r12'>" + "<td class=body2 >"
								+ num
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("LEASE_CODE")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("CUST_NAME")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j)).get("NAME")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listAbleSettleEmailByDept.get(j)).get("LEASE_PERIOD")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("LEASE_PERIOD")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("DUN_PRICE")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("LGJ")
								+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleEmailByDept.get(j))
									.get("OPPOSINGDATE")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleEmailByDept.get(j))
									.get("TOTALLAWYFEE")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleEmailByDept.get(j))
									.get("DIFF_DAY")
							+ "天</td>"									
								+ "<td class=body2 >正常</td></tr>");
					}
				
					mailContent.append("</table>");
					mailContent.append("</body></html>");	
					mailSettingTo.setEmailContent(mailContent.toString());
				try {
					mailUtilService.sendMail(205,mailSettingTo);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}

	//将可结清案件明细表发送给相应的办事处  上海
	public void sendAbleSettleRentListForShanghai()
			throws MessagingException {
		MailSettingTo mailSettingTo = new MailSettingTo();
		Map paramMap =new HashMap();
		List  listAbleSettleEmailByDept=null;
		try {
			listAbleSettleEmailByDept = (List) DataAccessor.query(
				"settleManage.queryAbleSettleRentListShanghai", paramMap,
				DataAccessor.RS_TYPE.LIST);
		}catch (Exception e) {
			e.printStackTrace();
		}
		if (listAbleSettleEmailByDept != null && listAbleSettleEmailByDept.size()>0) {
			StringBuffer mailContent = new StringBuffer();
			mailSettingTo.setEmailSubject("可结清案件明细表");
			mailContent.append("<html><head></head>");
			mailContent
					.append("<style>.rhead { background-color: #006699}"
						+ ".Body2 { font-family: Arial, Helvetica, sans-serif; font-weight: normal; color: #000000; font-size: 9pt; text-decoration: none}"
						+ ".Body2BoldWhite2 { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #FFFFFF; font-size: 11pt; text-decoration: none }"
						+ ".Body2Bold { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #000000; font-size: 8pt; text-decoration: none }"
						+ ".r11 {  background-color: #C4E2EC}"
						+ ".r12 { background-color: #D2EFF0}</style><body>");
			mailContent.append("<font size='3'><b>各位好:<b><br></font>"
						+ "<font size='2'>以下为可结清案件明细表，请参阅~</font><br><br>");
			mailContent
						.append("<table border='1' cellspacing='0' width='1050px;' cellpadding='0'>"
								+ "<tr class='rhead'>"
								+ "<td class='Body2BoldWhite2' style='width:30px;' align='center'>序号</td>"
								+ "<td class='Body2BoldWhite2' style='width:110px;' align='center'>合同号</td>"
								+ "<td class='Body2BoldWhite2' align='center'>客户名称</td>"
								+ "<td class='Body2BoldWhite2' align='center'>业务经办</td>"
								+ "<td class='Body2BoldWhite2' align='center'>租赁期数</td>"
								+ "<td class='Body2BoldWhite2' align='center'>已交期数</td>"
								+ "<td class='Body2BoldWhite2' align='center'>未缴罚息</td>"
								+ "<td class='Body2BoldWhite2' align='center'>未缴期满购买金</td>"
								+ "<td class='Body2BoldWhite2' align='center'>最后一期来款日期</td>"
								+ "<td class='Body2BoldWhite2' align='center'>法务费用</td>"
								+ "<td class='Body2BoldWhite2' align='center'>距今天数</td>"
								+ "<td class='Body2BoldWhite2' align='center'>状态</td></tr>");
					int num=0;
					for (int j = 0; j < listAbleSettleEmailByDept.size(); j++) {
						num++;
						mailContent.append("<tr class='r12'>" + "<td class=body2 >"
								+ num
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("LEASE_CODE")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("CUST_NAME")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j)).get("NAME")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listAbleSettleEmailByDept.get(j)).get("LEASE_PERIOD")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("LEASE_PERIOD")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("DUN_PRICE")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("LGJ")
								+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleEmailByDept.get(j))
									.get("OPPOSINGDATE")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleEmailByDept.get(j))
									.get("TOTALLAWYFEE")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleEmailByDept.get(j))
									.get("DIFF_DAY")
							+ "天</td>"									
								+ "<td class=body2 >正常</td></tr>");
					}
				
					mailContent.append("</table>");
					mailContent.append("</body></html>");	
					mailSettingTo.setEmailContent(mailContent.toString());
				try {
					mailUtilService.sendMail(206,mailSettingTo);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}

	//将可结清案件明细表发送给相应的办事处  南京
	public void sendAbleSettleRentListForNanjing()
			throws MessagingException {
		MailSettingTo mailSettingTo = new MailSettingTo();
		Map paramMap =new HashMap();
		List  listAbleSettleEmailByDept=null;
		try {
			listAbleSettleEmailByDept = (List) DataAccessor.query(
				"settleManage.queryAbleSettleRentListNanjing", paramMap,
				DataAccessor.RS_TYPE.LIST);
		}catch (Exception e) {
			e.printStackTrace();
		}
		if (listAbleSettleEmailByDept != null && listAbleSettleEmailByDept.size()>0) {
			StringBuffer mailContent = new StringBuffer();
			mailSettingTo.setEmailSubject("可结清案件明细表");
			mailContent.append("<html><head></head>");
			mailContent
					.append("<style>.rhead { background-color: #006699}"
						+ ".Body2 { font-family: Arial, Helvetica, sans-serif; font-weight: normal; color: #000000; font-size: 9pt; text-decoration: none}"
						+ ".Body2BoldWhite2 { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #FFFFFF; font-size: 11pt; text-decoration: none }"
						+ ".Body2Bold { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #000000; font-size: 8pt; text-decoration: none }"
						+ ".r11 {  background-color: #C4E2EC}"
						+ ".r12 { background-color: #D2EFF0}</style><body>");
			mailContent.append("<font size='3'><b>各位好:<b><br></font>"
						+ "<font size='2'>以下为可结清案件明细表，请参阅~</font><br><br>");
			mailContent
						.append("<table border='1' cellspacing='0' width='1050px;' cellpadding='0'>"
								+ "<tr class='rhead'>"
								+ "<td class='Body2BoldWhite2' style='width:30px;' align='center'>序号</td>"
								+ "<td class='Body2BoldWhite2' style='width:110px;' align='center'>合同号</td>"
								+ "<td class='Body2BoldWhite2' align='center'>客户名称</td>"
								+ "<td class='Body2BoldWhite2' align='center'>业务经办</td>"
								+ "<td class='Body2BoldWhite2' align='center'>租赁期数</td>"
								+ "<td class='Body2BoldWhite2' align='center'>已交期数</td>"
								+ "<td class='Body2BoldWhite2' align='center'>未缴罚息</td>"
								+ "<td class='Body2BoldWhite2' align='center'>未缴期满购买金</td>"
								+ "<td class='Body2BoldWhite2' align='center'>最后一期来款日期</td>"
								+ "<td class='Body2BoldWhite2' align='center'>法务费用</td>"
								+ "<td class='Body2BoldWhite2' align='center'>距今天数</td>"
								+ "<td class='Body2BoldWhite2' align='center'>状态</td></tr>");
					int num=0;
					for (int j = 0; j < listAbleSettleEmailByDept.size(); j++) {
						num++;
						mailContent.append("<tr class='r12'>" + "<td class=body2 >"
								+ num
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("LEASE_CODE")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("CUST_NAME")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j)).get("NAME")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listAbleSettleEmailByDept.get(j)).get("LEASE_PERIOD")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("LEASE_PERIOD")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("DUN_PRICE")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("LGJ")
								+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleEmailByDept.get(j))
									.get("OPPOSINGDATE")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleEmailByDept.get(j))
									.get("TOTALLAWYFEE")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleEmailByDept.get(j))
									.get("DIFF_DAY")
							+ "天</td>"									
								+ "<td class=body2 >正常</td></tr>");
					}
				
					mailContent.append("</table>");
					mailContent.append("</body></html>");	
					mailSettingTo.setEmailContent(mailContent.toString());
				try {
					mailUtilService.sendMail(207,mailSettingTo);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}

	//将可结清案件明细表发送给相应的办事处  重庆
	public void sendAbleSettleRentListForChongqing()
			throws MessagingException {
		MailSettingTo mailSettingTo = new MailSettingTo();
		Map paramMap =new HashMap();
		List  listAbleSettleEmailByDept=null;
		try {
			listAbleSettleEmailByDept = (List) DataAccessor.query(
				"settleManage.queryAbleSettleRentListChongqing", paramMap,
				DataAccessor.RS_TYPE.LIST);
		}catch (Exception e) {
			e.printStackTrace();
		}
		if (listAbleSettleEmailByDept != null && listAbleSettleEmailByDept.size()>0) {
			StringBuffer mailContent = new StringBuffer();
			mailSettingTo.setEmailSubject("可结清案件明细表");
			mailContent.append("<html><head></head>");
			mailContent
					.append("<style>.rhead { background-color: #006699}"
						+ ".Body2 { font-family: Arial, Helvetica, sans-serif; font-weight: normal; color: #000000; font-size: 9pt; text-decoration: none}"
						+ ".Body2BoldWhite2 { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #FFFFFF; font-size: 11pt; text-decoration: none }"
						+ ".Body2Bold { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #000000; font-size: 8pt; text-decoration: none }"
						+ ".r11 {  background-color: #C4E2EC}"
						+ ".r12 { background-color: #D2EFF0}</style><body>");
			mailContent.append("<font size='3'><b>各位好:<b><br></font>"
						+ "<font size='2'>以下为可结清案件明细表，请参阅~</font><br><br>");
			mailContent
						.append("<table border='1' cellspacing='0' width='1050px;' cellpadding='0'>"
								+ "<tr class='rhead'>"
								+ "<td class='Body2BoldWhite2' style='width:30px;' align='center'>序号</td>"
								+ "<td class='Body2BoldWhite2' style='width:110px;' align='center'>合同号</td>"
								+ "<td class='Body2BoldWhite2' align='center'>客户名称</td>"
								+ "<td class='Body2BoldWhite2' align='center'>业务经办</td>"
								+ "<td class='Body2BoldWhite2' align='center'>租赁期数</td>"
								+ "<td class='Body2BoldWhite2' align='center'>已交期数</td>"
								+ "<td class='Body2BoldWhite2' align='center'>未缴罚息</td>"
								+ "<td class='Body2BoldWhite2' align='center'>未缴期满购买金</td>"
								+ "<td class='Body2BoldWhite2' align='center'>最后一期来款日期</td>"
								+ "<td class='Body2BoldWhite2' align='center'>法务费用</td>"
								+ "<td class='Body2BoldWhite2' align='center'>距今天数</td>"
								+ "<td class='Body2BoldWhite2' align='center'>状态</td></tr>");
					int num=0;
					for (int j = 0; j < listAbleSettleEmailByDept.size(); j++) {
						num++;
						mailContent.append("<tr class='r12'>" + "<td class=body2 >"
								+ num
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("LEASE_CODE")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("CUST_NAME")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j)).get("NAME")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listAbleSettleEmailByDept.get(j)).get("LEASE_PERIOD")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("LEASE_PERIOD")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("DUN_PRICE")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("LGJ")
								+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleEmailByDept.get(j))
									.get("OPPOSINGDATE")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleEmailByDept.get(j))
									.get("TOTALLAWYFEE")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleEmailByDept.get(j))
									.get("DIFF_DAY")
							+ "天</td>"									
								+ "<td class=body2 >正常</td></tr>");
					}
				
					mailContent.append("</table>");
					mailContent.append("</body></html>");	
					mailSettingTo.setEmailContent(mailContent.toString());
				try {
					mailUtilService.sendMail(208,mailSettingTo);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}

	//将可结清案件明细表发送给相应的办事处  成都
	public void sendAbleSettleRentListForChengdu()
			throws MessagingException {
		MailSettingTo mailSettingTo = new MailSettingTo();
		Map paramMap =new HashMap();
		List  listAbleSettleEmailByDept=null;
		try {
			listAbleSettleEmailByDept = (List) DataAccessor.query(
				"settleManage.queryAbleSettleRentListChengdu", paramMap,
				DataAccessor.RS_TYPE.LIST);
		}catch (Exception e) {
			e.printStackTrace();
		}
		if (listAbleSettleEmailByDept != null && listAbleSettleEmailByDept.size()>0) {
			StringBuffer mailContent = new StringBuffer();
			mailSettingTo.setEmailSubject("可结清案件明细表");
			mailContent.append("<html><head></head>");
			mailContent
					.append("<style>.rhead { background-color: #006699}"
						+ ".Body2 { font-family: Arial, Helvetica, sans-serif; font-weight: normal; color: #000000; font-size: 9pt; text-decoration: none}"
						+ ".Body2BoldWhite2 { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #FFFFFF; font-size: 11pt; text-decoration: none }"
						+ ".Body2Bold { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #000000; font-size: 8pt; text-decoration: none }"
						+ ".r11 {  background-color: #C4E2EC}"
						+ ".r12 { background-color: #D2EFF0}</style><body>");
			mailContent.append("<font size='3'><b>各位好:<b><br></font>"
						+ "<font size='2'>以下为可结清案件明细表，请参阅~</font><br><br>");
			mailContent
						.append("<table border='1' cellspacing='0' width='1050px;' cellpadding='0'>"
								+ "<tr class='rhead'>"
								+ "<td class='Body2BoldWhite2' style='width:30px;' align='center'>序号</td>"
								+ "<td class='Body2BoldWhite2' style='width:110px;' align='center'>合同号</td>"
								+ "<td class='Body2BoldWhite2' align='center'>客户名称</td>"
								+ "<td class='Body2BoldWhite2' align='center'>业务经办</td>"
								+ "<td class='Body2BoldWhite2' align='center'>租赁期数</td>"
								+ "<td class='Body2BoldWhite2' align='center'>已交期数</td>"
								+ "<td class='Body2BoldWhite2' align='center'>未缴罚息</td>"
								+ "<td class='Body2BoldWhite2' align='center'>未缴期满购买金</td>"
								+ "<td class='Body2BoldWhite2' align='center'>最后一期来款日期</td>"
								+ "<td class='Body2BoldWhite2' align='center'>法务费用</td>"
								+ "<td class='Body2BoldWhite2' align='center'>距今天数</td>"
								+ "<td class='Body2BoldWhite2' align='center'>状态</td></tr>");
					int num=0;
					for (int j = 0; j < listAbleSettleEmailByDept.size(); j++) {
						num++;
						mailContent.append("<tr class='r12'>" + "<td class=body2 >"
								+ num
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("LEASE_CODE")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("CUST_NAME")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j)).get("NAME")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listAbleSettleEmailByDept.get(j)).get("LEASE_PERIOD")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("LEASE_PERIOD")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("DUN_PRICE")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("LGJ")
								+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleEmailByDept.get(j))
									.get("OPPOSINGDATE")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleEmailByDept.get(j))
									.get("TOTALLAWYFEE")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleEmailByDept.get(j))
									.get("DIFF_DAY")
							+ "天</td>"									
								+ "<td class=body2 >正常</td></tr>");
					}
				
					mailContent.append("</table>");
					mailContent.append("</body></html>");	
					mailSettingTo.setEmailContent(mailContent.toString());
				try {
					mailUtilService.sendMail(209,mailSettingTo);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}

	//将可结清案件明细表发送给相应的办事处  东莞
	public void sendAbleSettleRentListForDongguan()
			throws MessagingException {
		MailSettingTo mailSettingTo = new MailSettingTo();
		Map paramMap =new HashMap();
		List  listAbleSettleEmailByDept=null;
		try {
			listAbleSettleEmailByDept = (List) DataAccessor.query(
				"settleManage.queryAbleSettleRentListDongguan", paramMap,
				DataAccessor.RS_TYPE.LIST);
		}catch (Exception e) {
			e.printStackTrace();
		}
		if (listAbleSettleEmailByDept != null && listAbleSettleEmailByDept.size()>0) {
			StringBuffer mailContent = new StringBuffer();
			mailSettingTo.setEmailSubject("可结清案件明细表");
			mailContent.append("<html><head></head>");
			mailContent
					.append("<style>.rhead { background-color: #006699}"
						+ ".Body2 { font-family: Arial, Helvetica, sans-serif; font-weight: normal; color: #000000; font-size: 9pt; text-decoration: none}"
						+ ".Body2BoldWhite2 { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #FFFFFF; font-size: 11pt; text-decoration: none }"
						+ ".Body2Bold { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #000000; font-size: 8pt; text-decoration: none }"
						+ ".r11 {  background-color: #C4E2EC}"
						+ ".r12 { background-color: #D2EFF0}</style><body>");
			mailContent.append("<font size='3'><b>各位好:<b><br></font>"
						+ "<font size='2'>以下为可结清案件明细表，请参阅~</font><br><br>");
			mailContent
						.append("<table border='1' cellspacing='0' width='1050px;' cellpadding='0'>"
								+ "<tr class='rhead'>"
								+ "<td class='Body2BoldWhite2' style='width:30px;' align='center'>序号</td>"
								+ "<td class='Body2BoldWhite2' style='width:110px;' align='center'>合同号</td>"
								+ "<td class='Body2BoldWhite2' align='center'>客户名称</td>"
								+ "<td class='Body2BoldWhite2' align='center'>业务经办</td>"
								+ "<td class='Body2BoldWhite2' align='center'>租赁期数</td>"
								+ "<td class='Body2BoldWhite2' align='center'>已交期数</td>"
								+ "<td class='Body2BoldWhite2' align='center'>未缴罚息</td>"
								+ "<td class='Body2BoldWhite2' align='center'>未缴期满购买金</td>"
								+ "<td class='Body2BoldWhite2' align='center'>最后一期来款日期</td>"
								+ "<td class='Body2BoldWhite2' align='center'>法务费用</td>"
								+ "<td class='Body2BoldWhite2' align='center'>距今天数</td>"
								+ "<td class='Body2BoldWhite2' align='center'>状态</td></tr>");
					int num=0;
					for (int j = 0; j < listAbleSettleEmailByDept.size(); j++) {
						num++;
						mailContent.append("<tr class='r12'>" + "<td class=body2 >"
								+ num
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("LEASE_CODE")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("CUST_NAME")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j)).get("NAME")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listAbleSettleEmailByDept.get(j)).get("LEASE_PERIOD")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("LEASE_PERIOD")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("DUN_PRICE")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("LGJ")
								+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleEmailByDept.get(j))
									.get("OPPOSINGDATE")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleEmailByDept.get(j))
									.get("TOTALLAWYFEE")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleEmailByDept.get(j))
									.get("DIFF_DAY")
							+ "天</td>"									
								+ "<td class=body2 >正常</td></tr>");
					}
				
					mailContent.append("</table>");
					mailContent.append("</body></html>");	
					mailSettingTo.setEmailContent(mailContent.toString());
				try {
					mailUtilService.sendMail(210,mailSettingTo);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}

	//将可结清案件明细表发送给相应的办事处  佛山
	public void sendAbleSettleRentListForFoshan()
			throws MessagingException {
		MailSettingTo mailSettingTo = new MailSettingTo();
		Map paramMap =new HashMap();
		List  listAbleSettleEmailByDept=null;
		try {
			listAbleSettleEmailByDept = (List) DataAccessor.query(
				"settleManage.queryAbleSettleRentListFoshan", paramMap,
				DataAccessor.RS_TYPE.LIST);
		}catch (Exception e) {
			e.printStackTrace();
		}
		if (listAbleSettleEmailByDept != null && listAbleSettleEmailByDept.size()>0) {
			StringBuffer mailContent = new StringBuffer();
			mailSettingTo.setEmailSubject("可结清案件明细表");
			mailContent.append("<html><head></head>");
			mailContent
					.append("<style>.rhead { background-color: #006699}"
						+ ".Body2 { font-family: Arial, Helvetica, sans-serif; font-weight: normal; color: #000000; font-size: 9pt; text-decoration: none}"
						+ ".Body2BoldWhite2 { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #FFFFFF; font-size: 11pt; text-decoration: none }"
						+ ".Body2Bold { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #000000; font-size: 8pt; text-decoration: none }"
						+ ".r11 {  background-color: #C4E2EC}"
						+ ".r12 { background-color: #D2EFF0}</style><body>");
			mailContent.append("<font size='3'><b>各位好:<b><br></font>"
						+ "<font size='2'>以下为可结清案件明细表，请参阅~</font><br><br>");
			mailContent
						.append("<table border='1' cellspacing='0' width='1050px;' cellpadding='0'>"
								+ "<tr class='rhead'>"
								+ "<td class='Body2BoldWhite2' style='width:30px;' align='center'>序号</td>"
								+ "<td class='Body2BoldWhite2' style='width:110px;' align='center'>合同号</td>"
								+ "<td class='Body2BoldWhite2' align='center'>客户名称</td>"
								+ "<td class='Body2BoldWhite2' align='center'>业务经办</td>"
								+ "<td class='Body2BoldWhite2' align='center'>租赁期数</td>"
								+ "<td class='Body2BoldWhite2' align='center'>已交期数</td>"
								+ "<td class='Body2BoldWhite2' align='center'>未缴罚息</td>"
								+ "<td class='Body2BoldWhite2' align='center'>未缴期满购买金</td>"
								+ "<td class='Body2BoldWhite2' align='center'>最后一期来款日期</td>"
								+ "<td class='Body2BoldWhite2' align='center'>法务费用</td>"
								+ "<td class='Body2BoldWhite2' align='center'>距今天数</td>"
								+ "<td class='Body2BoldWhite2' align='center'>状态</td></tr>");
					int num=0;
					for (int j = 0; j < listAbleSettleEmailByDept.size(); j++) {
						num++;
						mailContent.append("<tr class='r12'>" + "<td class=body2 >"
								+ num
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("LEASE_CODE")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("CUST_NAME")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j)).get("NAME")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listAbleSettleEmailByDept.get(j)).get("LEASE_PERIOD")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("LEASE_PERIOD")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("DUN_PRICE")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("LGJ")
								+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleEmailByDept.get(j))
									.get("OPPOSINGDATE")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleEmailByDept.get(j))
									.get("TOTALLAWYFEE")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleEmailByDept.get(j))
									.get("DIFF_DAY")
							+ "天</td>"									
								+ "<td class=body2 >正常</td></tr>");
					}
				
					mailContent.append("</table>");
					mailContent.append("</body></html>");	
					mailSettingTo.setEmailContent(mailContent.toString());
				
				try {
					mailUtilService.sendMail(211,mailSettingTo);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}

	//将可结清案件明细表发送给相应的办事处  厦门
	public void sendAbleSettleRentListForXiamen()
			throws MessagingException {
		MailSettingTo mailSettingTo = new MailSettingTo();
		Map paramMap =new HashMap();
		List  listAbleSettleEmailByDept=null;
		try {
			listAbleSettleEmailByDept = (List) DataAccessor.query(
				"settleManage.queryAbleSettleRentListXiamen", paramMap,
				DataAccessor.RS_TYPE.LIST);
		}catch (Exception e) {
			e.printStackTrace();
		}
		if (listAbleSettleEmailByDept != null && listAbleSettleEmailByDept.size()>0) {
			StringBuffer mailContent = new StringBuffer();
			mailSettingTo.setEmailSubject("可结清案件明细表");
			mailContent.append("<html><head></head>");
			mailContent
					.append("<style>.rhead { background-color: #006699}"
						+ ".Body2 { font-family: Arial, Helvetica, sans-serif; font-weight: normal; color: #000000; font-size: 9pt; text-decoration: none}"
						+ ".Body2BoldWhite2 { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #FFFFFF; font-size: 11pt; text-decoration: none }"
						+ ".Body2Bold { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #000000; font-size: 8pt; text-decoration: none }"
						+ ".r11 {  background-color: #C4E2EC}"
						+ ".r12 { background-color: #D2EFF0}</style><body>");
			mailContent.append("<font size='3'><b>各位好:<b><br></font>"
						+ "<font size='2'>以下为可结清案件明细表，请参阅~</font><br><br>");
			mailContent
						.append("<table border='1' cellspacing='0' width='1050px;' cellpadding='0'>"
								+ "<tr class='rhead'>"
								+ "<td class='Body2BoldWhite2' style='width:30px;' align='center'>序号</td>"
								+ "<td class='Body2BoldWhite2' style='width:110px;' align='center'>合同号</td>"
								+ "<td class='Body2BoldWhite2' align='center'>客户名称</td>"
								+ "<td class='Body2BoldWhite2' align='center'>业务经办</td>"
								+ "<td class='Body2BoldWhite2' align='center'>租赁期数</td>"
								+ "<td class='Body2BoldWhite2' align='center'>已交期数</td>"
								+ "<td class='Body2BoldWhite2' align='center'>未缴罚息</td>"
								+ "<td class='Body2BoldWhite2' align='center'>未缴期满购买金</td>"
								+ "<td class='Body2BoldWhite2' align='center'>最后一期来款日期</td>"
								+ "<td class='Body2BoldWhite2' align='center'>法务费用</td>"
								+ "<td class='Body2BoldWhite2' align='center'>距今天数</td>"
								+ "<td class='Body2BoldWhite2' align='center'>状态</td></tr>");
					int num=0;
					for (int j = 0; j < listAbleSettleEmailByDept.size(); j++) {
						num++;
						mailContent.append("<tr class='r12'>" + "<td class=body2 >"
								+ num
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("LEASE_CODE")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("CUST_NAME")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j)).get("NAME")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listAbleSettleEmailByDept.get(j)).get("LEASE_PERIOD")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("LEASE_PERIOD")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("DUN_PRICE")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("LGJ")
								+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleEmailByDept.get(j))
									.get("OPPOSINGDATE")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleEmailByDept.get(j))
									.get("TOTALLAWYFEE")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) listAbleSettleEmailByDept.get(j))
									.get("DIFF_DAY")
							+ "天</td>"									
								+ "<td class=body2 >正常</td></tr>");
					}
				
					mailContent.append("</table>");
					mailContent.append("</body></html>");	
					mailSettingTo.setEmailContent(mailContent.toString());
					try {
						mailUtilService.sendMail(212,mailSettingTo);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
	}
//发送给各个办事处的可结清案件	
	public void sendAllDeptAbleSettleRentList() throws MessagingException{
		//sendAbleSettleRentListOverThreeM();
		sendAbleSettleRentListForXiamen();
		sendAbleSettleRentListForFoshan();
		sendAbleSettleRentListForDongguan();
		sendAbleSettleRentListForChengdu();
		sendAbleSettleRentListForChongqing();
		sendAbleSettleRentListForNanjing();
		sendAbleSettleRentListForShanghai();
		sendAbleSettleRentListForKunshan();
		sendAbleSettleRentListForSuzhouCar();
		sendAbleSettleRentListForSuzhou();
	}

	/*
	 * Add by Michael 2012 09-19 节假日发送直接锁码给杨晶晶
	 * 如果第二天是假日，或者接下是假日，就要将锁码是这些日期里的锁码信息发送出来
	 */
	public void sendDirectLockForHoliday() throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map workDayMap = null;
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		paramMap.put("query_date",sf.format(new Date()));
		workDayMap = (Map) DataAccessor.query(
				"financeDecomposeReport.getTodayIsWD", paramMap,
				DataAccessor.RS_TYPE.MAP);
		if (workDayMap != null) {
			List queryDayList = new ArrayList();
			queryDayList = (List) DataAccessor.query(
					"financeDecomposeReport.getQueryDayList", paramMap,
					DataAccessor.RS_TYPE.LIST);
			Map queryDay;
			String strDate = "";
			for (int i = 0; i < queryDayList.size(); i++) {
				queryDay = (Map) queryDayList.get(i);
				if (null != queryDay) {
					if(i==0){
						if(queryDay.get("DAY_TYPE").equals("HD")){
							strDate += "convert(date,'"
									+ String.valueOf(queryDay.get("DATE")) + "')";
						}else{
							break;
						}
					}else if(queryDay.get("DAY_TYPE").equals("HD")){
						strDate += ",convert(date,'"
								+ String.valueOf(queryDay.get("DATE")) + "')";
					}
				}
			}
			
			if(!"".equals(strDate)){
				paramMap.put("DATES", strDate);
				List directLockForHolidayList = new ArrayList();
				directLockForHolidayList = (List) DataAccessor.query(
						"financeDecomposeReport.sendDirectLockForHoliday", paramMap,
						DataAccessor.RS_TYPE.LIST);
				String filePathString = "";
				if (directLockForHolidayList.size() > 0) {
					StringBuffer mailContent = new StringBuffer();
					mailContent.append("<html><head></head>");
					mailContent
							.append("<style>.rhead { background-color: #006699}"
									+ ".Body2 { font-family: Arial, Helvetica, sans-serif; font-weight: normal; color: #000000; font-size: 9pt; text-decoration: none}"
									+ ".Body2BoldWhite2 { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #FFFFFF; font-size: 11pt; text-decoration: none }"
									+ ".Body2Bold { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #000000; font-size: 8pt; text-decoration: none }"
									+ ".r11 {  background-color: #C4E2EC}"
									+ ".r12 { background-color: #D2EFF0}</style><body>");
					mailContent.append("<font size='3'><b>各位好:<b><br></font>"
							+ "<font size='2'>假日期间直接锁码信息列表如下：请参阅~</font><br><br>");
					mailContent
							.append("<table border='1' cellspacing='0' width='1050px;' cellpadding='0'>"
									+ "<tr class='rhead'>"
									+ "<td class='Body2BoldWhite2' style='width:40px;' align='center'>序号</td>"
									+ "<td class='Body2BoldWhite2' style='width:100px;' align='center'>合同号</td>"
									+ "<td class='Body2BoldWhite2' align='center'>客户名称</td>"
									+ "<td class='Body2BoldWhite2' align='center'>客户联系人</td>"
									+ "<td class='Body2BoldWhite2' align='center'>联系人电话</td>"
									+ "<td class='Body2BoldWhite2' align='center'>联系人Email</td>"
									+ "<td class='Body2BoldWhite2' align='center'>密码</td>"
									+ "<td class='Body2BoldWhite2' align='center'>密码文件</td>"
									+ "<td class='Body2BoldWhite2' align='center'>设备型号</td>"
									+ "<td class='Body2BoldWhite2' align='center'>机号</td>"
									+ "<td class='Body2BoldWhite2' align='center'>锁码日</td>"
									+ "<td class='Body2BoldWhite2' align='center'>缴款说明</td></tr>");
					int num = 0;
					for (int i = 0; i < directLockForHolidayList.size(); i++) {
						num++;
						mailContent.append("<tr class='r12'>" + "<td class=body2 >"
								+ num
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) directLockForHolidayList.get(i))
										.get("LEASE_CODE")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) directLockForHolidayList.get(i))
										.get("CUST_NAME")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) directLockForHolidayList.get(i))
										.get("RENTER_NAME")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) directLockForHolidayList.get(i))
										.get("RENTER_PHONE")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) directLockForHolidayList.get(i))
										.get("RENTER_EMAIL")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) directLockForHolidayList.get(i)).get("PASSWORDS")
								+ "</td>"
								+ "<td class=body2><a href='"+((Map) directLockForHolidayList.get(i)).get("FILE_NAME")+"'>"
								+ ((Map) directLockForHolidayList.get(i))
										.get("FILE_NAME")
								+ "</a></td>"
								+ "<td class=body2>"
								+ ((Map) directLockForHolidayList.get(i))
										.get("MODEL_SPEC")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) directLockForHolidayList.get(i))
										.get("THING_NUMBER")
								+ "</td>"	
								+ "<td class=body2>"
								+ ((Map) directLockForHolidayList.get(i))
										.get("LOCK_DATE")
								+ "</td>"							
								+ "<td class=body2>"
								+ ((Map) directLockForHolidayList.get(i))
										.get("MEMO") + "</td></tr>");
							
						if (null != ((Map) directLockForHolidayList.get(i)).get("PATH")) {
								if ("".equals(filePathString)) {
									filePathString = "\\"
											+ "\\"+LeaseUtil.getIPAddress()+"\\home\\filsoft\\financelease\\upload\\lockcode\\password"
											+ String.valueOf(((Map) directLockForHolidayList.get(i)).get("PATH"));
								} else {
									filePathString = filePathString
											+ ";"
											+ "\\"
											+ "\\"+LeaseUtil.getIPAddress()+"\\home\\filsoft\\financelease\\upload\\lockcode\\password"
											+ String.valueOf(((Map) directLockForHolidayList.get(i)).get("PATH"));
								}
							}
					}
					mailContent.append("</table>");
					mailContent.append("</body></html>");
					MailSettingTo mailSettingTo = new MailSettingTo();
					mailSettingTo.setEmailAttachPath(filePathString);
					mailSettingTo.setEmailContent(mailContent.toString());
					mailSettingTo.setEmailTo("lune@tacleasing.cn");
					mailSettingTo.setEmailSubject("假日期间直接锁码信息报表");
					mailSettingTo.setEmailCc("robin_chantw@tacleasing.cn");
					mailUtilService.sendMail(mailSettingTo);
				}
			}
		}
	}
	
	/*
	 * Add by Michael 2012 09-19 节假日发送间接锁码给杨晶晶
	 * 如果第二天是假日，或者接下是假日，就要将锁码是这些日期里的锁码信息发送出来
	 */
	public void sendUnDirectLockForHoliday() throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map workDayMap = null;
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		paramMap.put("query_date", sf.format(new Date()));
		workDayMap = (Map) DataAccessor.query(
				"financeDecomposeReport.getTodayIsWD", paramMap,
				DataAccessor.RS_TYPE.MAP);
		if (workDayMap != null) {
			List queryDayList = new ArrayList();
			queryDayList = (List) DataAccessor.query(
					"financeDecomposeReport.getQueryDayList", paramMap,
					DataAccessor.RS_TYPE.LIST);
			Map queryDay;
			String strDate = "";
			for (int i = 0; i < queryDayList.size(); i++) {
				queryDay = (Map) queryDayList.get(i);
				if (null != queryDay) {
					if(i==0){
						if(queryDay.get("DAY_TYPE").equals("HD")){
							strDate += "convert(date,'"
									+ String.valueOf(queryDay.get("DATE")) + "')";
						}else{
							break;
						}
					}else if(queryDay.get("DAY_TYPE").equals("HD")){
						strDate += ",convert(date,'"
								+ String.valueOf(queryDay.get("DATE")) + "')";
					}
				}
			}
			if(!"".equals(strDate)){
				paramMap.put("DATES", strDate);
				List unDirectLockForHolidayList =null;
				unDirectLockForHolidayList = (List) DataAccessor.query(
						"financeDecomposeReport.sendUnDirectLockForHoliday", paramMap,
						DataAccessor.RS_TYPE.LIST);
				if (unDirectLockForHolidayList.size() > 0) {
					StringBuffer mailContent = new StringBuffer();
					mailContent.append("<html><head></head>");
					mailContent
							.append("<style>.rhead { background-color: #006699}"
									+ ".Body2 { font-family: Arial, Helvetica, sans-serif; font-weight: normal; color: #000000; font-size: 9pt; text-decoration: none}"
									+ ".Body2BoldWhite2 { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #FFFFFF; font-size: 11pt; text-decoration: none }"
									+ ".Body2Bold { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #000000; font-size: 8pt; text-decoration: none }"
									+ ".r11 {  background-color: #C4E2EC}"
									+ ".r12 { background-color: #D2EFF0}</style><body>");
					mailContent.append("<font size='3'><b>各位好:<b><br></font>"
							+ "<font size='2'>假日期间间接锁码信息列表如下：请参阅~</font><br><br>");
					mailContent
							.append("<table border='1' cellspacing='0' width='1050px;' cellpadding='0'>"
									+ "<tr class='rhead'>"
									+ "<td class='Body2BoldWhite2' style='width:40px;' align='center'>序号</td>"
									+ "<td class='Body2BoldWhite2' style='width:100px;' align='center'>合同号</td>"
									+ "<td class='Body2BoldWhite2' align='center'>客户名称</td>"
									+ "<td class='Body2BoldWhite2' align='center'>客户联系人</td>"
									+ "<td class='Body2BoldWhite2' align='center'>联系人电话</td>"
									+ "<td class='Body2BoldWhite2' align='center'>联系人Email</td>"
									+ "<td class='Body2BoldWhite2' align='center'>供应商</td>"
									+ "<td class='Body2BoldWhite2' align='center'>供应商联系人</td>"
									+ "<td class='Body2BoldWhite2' align='center'>供应商电话</td>"
									+ "<td class='Body2BoldWhite2' align='center'>供应商Email</td>"
									+ "<td class='Body2BoldWhite2' align='center'>设备型号</td>"
									+ "<td class='Body2BoldWhite2' align='center'>机号</td>"
									+ "<td class='Body2BoldWhite2' align='center'>锁码日</td>"
									+ "<td class='Body2BoldWhite2' align='center'>租金缴款说明</td></tr>");
					int num = 0;
					for (int i = 0; i < unDirectLockForHolidayList.size(); i++) {
						num++;
						mailContent.append("<tr class='r12'>" + "<td class=body2 >"
								+ num
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) unDirectLockForHolidayList.get(i))
										.get("LEASE_CODE")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) unDirectLockForHolidayList.get(i))
										.get("CUST_NAME")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) unDirectLockForHolidayList.get(i))
										.get("LINK_NAME")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) unDirectLockForHolidayList.get(i))
										.get("LINK_MOBILE")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) unDirectLockForHolidayList.get(i))
										.get("LINK_EMAIL")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) unDirectLockForHolidayList.get(i))
										.get("BRAND")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) unDirectLockForHolidayList.get(i))
										.get("RENTER_NAME")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) unDirectLockForHolidayList.get(i))
										.get("RENTER_PHONE")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) unDirectLockForHolidayList.get(i))
										.get("RENTER_EMAIL")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) unDirectLockForHolidayList.get(i))
										.get("MODEL_SPEC")
								+ "</td>"							
								+ "<td class=body2>"
								+ ((Map) unDirectLockForHolidayList.get(i))
										.get("THING_NUMBER")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) unDirectLockForHolidayList.get(i))
										.get("LOCK_DATE")
								+ "</td>"							
								+ "<td class=body2>"
								+ ((Map) unDirectLockForHolidayList.get(i))
										.get("MEMO") + "</td></tr>");
					}
					mailContent.append("</table>");
					mailContent.append("</body></html>");
					MailSettingTo mailSettingTo = new MailSettingTo();
					mailSettingTo.setEmailTo("lune@tacleasing.cn");
					mailSettingTo.setEmailSubject("假日期间间接锁码信息报表");
					mailSettingTo.setEmailCc("robin_chantw@tacleasing.cn");
					mailSettingTo.setEmailContent(mailContent.toString());
					mailUtilService.sendMail(mailSettingTo);
				}
			}
		}
	}

	public void sendAllDirectLockForHoliday() throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
			List directLockForHolidayList = new ArrayList();
			String filePathString = "";
			directLockForHolidayList = (List) DataAccessor.query(
					"financeDecomposeReport.sendAllDirectLockForHoliday", paramMap,
					DataAccessor.RS_TYPE.LIST);
				StringBuffer mailContent = new StringBuffer();
				StringBuffer mailContent1 = new StringBuffer();
				mailContent1.append("<html><head></head>");
				mailContent1
						.append("<style>.rhead { background-color: #006699}"
								+ ".Body2 { font-family: Arial, Helvetica, sans-serif; font-weight: normal; color: #000000; font-size: 9pt; text-decoration: none}"
								+ ".Body2BoldWhite2 { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #FFFFFF; font-size: 11pt; text-decoration: none }"
								+ ".Body2Bold { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #000000; font-size: 8pt; text-decoration: none }"
								+ ".r11 {  background-color: #C4E2EC}"
								+ ".r12 { background-color: #D2EFF0}</style><body>");
				mailContent1.append("<font size='3'><b>各位好:<b><br></font>"
						+ "<font size='2'>假日期间直接锁码信息列表如下：请参阅~</font><br><br>");
				mailContent1
						.append("<table border='1' cellspacing='0' width='1050px;' cellpadding='0'>"
								+ "<tr class='rhead'>"
								+ "<td class='Body2BoldWhite2' style='width:40px;' align='center'>序号</td>"
								+ "<td class='Body2BoldWhite2' style='width:100px;' align='center'>合同号</td>"
								+ "<td class='Body2BoldWhite2' align='center'>客户名称</td>"
								+ "<td class='Body2BoldWhite2' align='center'>联系人</td>"
								+ "<td class='Body2BoldWhite2' align='center'>联系人电话</td>"
								+ "<td class='Body2BoldWhite2' align='center'>联系人Email</td>"
								+ "<td class='Body2BoldWhite2' align='center'>密码</td>"
								+ "<td class='Body2BoldWhite2' align='center'>密码文件</td>"
								+ "<td class='Body2BoldWhite2' align='center'>设备型号</td>"
								+ "<td class='Body2BoldWhite2' align='center'>机号</td>"
								+ "<td class='Body2BoldWhite2' align='center'>锁码日</td>"
								+ "<td class='Body2BoldWhite2' align='center'>期数</td>"
								+ "<td class='Body2BoldWhite2' align='center'>锁码方式</td>"
								+ "<td class='Body2BoldWhite2' align='center'>缴款说明</td></tr>");
				int num = 0;
				for (int i = 0; i < directLockForHolidayList.size(); i++) {
					num++;
					mailContent.append("<tr class='r12'>" + "<td class=body2 >"
							+ num
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) directLockForHolidayList.get(i))
									.get("LEASE_CODE")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) directLockForHolidayList.get(i))
									.get("CUST_NAME")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) directLockForHolidayList.get(i))
									.get("RENTER_NAME")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) directLockForHolidayList.get(i))
									.get("RENTER_PHONE")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) directLockForHolidayList.get(i))
									.get("RENTER_EMAIL")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) directLockForHolidayList.get(i)).get("PASSWORDS")
							+ "</td>"
							+ "<td class=body2> <a href='"+((Map) directLockForHolidayList.get(i)).get("FILE_NAME")+"'>"
							+ ((Map) directLockForHolidayList.get(i))
									.get("FILE_NAME")
							+ "</a></td>"
							+ "<td class=body2>"
							+ ((Map) directLockForHolidayList.get(i))
									.get("MODEL_SPEC")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) directLockForHolidayList.get(i))
									.get("THING_NUMBER")
							+ "</td>"	
							+ "<td class=body2>"
							+ ((Map) directLockForHolidayList.get(i))
									.get("LOCK_DATE")
							+ "</td>"
							+ "<td class=body2>第"
							+ ((Map) directLockForHolidayList.get(i))
									.get("PERIOD_NUM")
							+ "期</td>"
							+ "<td class=body2>"
							+ ((Map) directLockForHolidayList.get(i))
									.get("LOCK_MEMO")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) directLockForHolidayList.get(i))
									.get("MEMO") + "</td></tr>");
						
					if (null != ((Map) directLockForHolidayList.get(i)).get("PATH")) {
							if ("".equals(filePathString)) {
								filePathString = "\\"
										+ "\\"+LeaseUtil.getIPAddress()+"\\home\\filsoft\\financelease\\upload\\lockcode\\password"
										+ String.valueOf(((Map) directLockForHolidayList.get(i)).get("PATH"));
							} else {
								filePathString = filePathString
										+ ";"
										+ "\\"
										+ "\\"+LeaseUtil.getIPAddress()+"\\home\\filsoft\\financelease\\upload\\lockcode\\password"
										+ String.valueOf(((Map) directLockForHolidayList.get(i)).get("PATH"));
							}
					}
					if (num % 300==0){
						mailContent.append("</table>");
						mailContent.append("</body></html>");
						MailSettingTo mailSettingTo = new MailSettingTo();
						mailSettingTo.setEmailAttachPath(filePathString);
						mailSettingTo.setEmailContent(mailContent1.toString()+mailContent.toString());
						mailSettingTo.setEmailTo("lune@tacleasing.cn");
						mailSettingTo.setEmailSubject("10.1假日期间直接锁码信息报表");
						mailSettingTo.setEmailCc("michael@tacleasing.cn");
						mailUtilService.sendMail(mailSettingTo);
						filePathString="";
						mailContent=new StringBuffer();
					}else if(num==directLockForHolidayList.size()){
						mailContent.append("</table>");
						mailContent.append("</body></html>");
						MailSettingTo mailSettingTo = new MailSettingTo();
						mailSettingTo.setEmailAttachPath(filePathString);
						mailSettingTo.setEmailContent(mailContent1.toString()+mailContent.toString());
						mailSettingTo.setEmailTo("lune@tacleasing.cn");
						mailSettingTo.setEmailSubject("10.1假日期间直接锁码信息报表");
						mailSettingTo.setEmailCc("michael@tacleasing.cn");
						mailUtilService.sendMail(mailSettingTo);
						filePathString="";
						mailContent=new StringBuffer();
					} 
					
				}

			
	}

	//当日锁码未发送Email资料
	public void dayNoSendLockEmailInfo() throws Exception{
		Map<String, Object> paramMap = new HashMap<String, Object>();

			List noSendLockEmailInfo = null;
			noSendLockEmailInfo = (List) DataAccessor.query(
					"financeDecomposeReport.queryDayNoSendLockEmailInfo", paramMap,
					DataAccessor.RS_TYPE.LIST);

			if (noSendLockEmailInfo.size() > 0) {
				StringBuffer mailContent = new StringBuffer();
				mailContent.append("<html><head></head>");
				mailContent
						.append("<style>.rhead { background-color: #006699}"
								+ ".Body2 { font-family: Arial, Helvetica, sans-serif; font-weight: normal; color: #000000; font-size: 9pt; text-decoration: none}"
								+ ".Body2BoldWhite2 { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #FFFFFF; font-size: 11pt; text-decoration: none }"
								+ ".Body2Bold { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #000000; font-size: 8pt; text-decoration: none }"
								+ ".r11 {  background-color: #C4E2EC}"
								+ ".r12 { background-color: #D2EFF0}</style><body>");
				mailContent.append("<font size='3'><b>各位好:<b><br></font>"
						+ "<font size='2'>以下为未成功发送解码邮件：请参阅~</font><br><br>");
				mailContent
						.append("<table border='1' cellspacing='0' width='1050px;' cellpadding='0'>"
								+ "<tr class='rhead'>"
								+ "<td class='Body2BoldWhite2' style='width:40px;' align='center'>序号</td>"
								+ "<td class='Body2BoldWhite2' style='width:100px;' align='center'>合同号</td>"
								+ "<td class='Body2BoldWhite2' align='center'>客户名称</td>"
								+ "<td class='Body2BoldWhite2' align='center'>供应商</td>"
								+ "<td class='Body2BoldWhite2' align='center'>设备维护人</td>"
								+ "<td class='Body2BoldWhite2' align='center'>维护人电话</td>"
								+ "<td class='Body2BoldWhite2' align='center'>维护人Email</td>"
								+ "<td class='Body2BoldWhite2' align='center'>客户联系人</td>"
								+ "<td class='Body2BoldWhite2' align='center'>联系人电话</td>"
								+ "<td class='Body2BoldWhite2' align='center'>联系人Email</td>"
								+ "<td class='Body2BoldWhite2' align='center'>锁码方式</td>"
								+ "<td class='Body2BoldWhite2' align='center'>设备型号</td>"
								+ "<td class='Body2BoldWhite2' align='center'>机号</td>"
								+ "<td class='Body2BoldWhite2' align='center'>锁码日</td></tr>");
				int num = 0;
				for (int i = 0; i < noSendLockEmailInfo.size(); i++) {
					num++;
					mailContent.append("<tr class='r12'>" + "<td class=body2 >"
							+ num
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) noSendLockEmailInfo.get(i))
									.get("LEASE_CODE")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) noSendLockEmailInfo.get(i))
									.get("CUST_NAME")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) noSendLockEmailInfo.get(i))
									.get("BRAND")
							+ "</td>"							
							+ "<td class=body2 >"
							+ ((Map) noSendLockEmailInfo.get(i))
									.get("RENTER_NAME")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) noSendLockEmailInfo.get(i))
									.get("RENTER_PHONE")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) noSendLockEmailInfo.get(i))
									.get("RENTER_EMAIL")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) noSendLockEmailInfo.get(i)).get("LINK_NAME")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) noSendLockEmailInfo.get(i)).get("LINK_MOBILE")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) noSendLockEmailInfo.get(i)).get("LINK_EMAIL")
							+ "</td>"
							+ "<td class=body2 >"
							+ ("1".equals(String.valueOf(((Map) noSendLockEmailInfo.get(i)).get("LOCK_CODE")))?"间接锁码":"直接锁码")
							+ "</td>"							
							+ "<td class=body2>"
							+ ((Map) noSendLockEmailInfo.get(i))
									.get("MODEL_SPEC")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) noSendLockEmailInfo.get(i))
									.get("THING_NUMBER")
							+ "</td>"	
							+ "<td class=body2>"
							+ ((Map) noSendLockEmailInfo.get(i))
									.get("LOCK_DATE")
							+ "</td></tr>");
				}
				mailContent.append("</table>");
				mailContent.append("</body></html>");
				MailSettingTo mailSettingTo = new MailSettingTo();
				mailSettingTo.setEmailContent(mailContent.toString());
				mailSettingTo.setEmailTo("lune@tacleasing.cn");
				mailSettingTo.setEmailSubject("当日解码邮件发送失败信息");
				mailSettingTo.setEmailCc("robin_chantw@tacleasing.cn");
				mailUtilService.sendMail(mailSettingTo);
			}
		}
	
		//发送给各个办事处的待补文件追述	
		public void sendAllUnCompletedFileListByDept() throws Exception{
			//判断今天是否是工作日，如果是工作日才发送邮件,否则不发邮件
			try {
				if (isWorkingDay()==false) {
					return; 
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			//1为待补文件追述分组代号
			List<MailSettingTo> mailSettingList = mailSettingService.getMailSettingListByGroup("1");
			
			for (MailSettingTo mailSettingTo : mailSettingList) {
				if(mailSettingTo.getDeptId()==null) {
					continue;
				}
				Map tempMap = new HashMap();
				tempMap.put("decpId", mailSettingTo.getDeptId());
				List listUnCompletedFile = null;
				listUnCompletedFile = (List) DataAccessor.query(
						"rentFile.getAllUnCompletedFileListForDept", tempMap,
						DataAccessor.RS_TYPE.LIST);
				if (listUnCompletedFile.size()==0){
					continue;
				}
				mailSettingTo.setEmailContent(sendUnCompletedFileListByDeptModel(listUnCompletedFile));
				mailUtilService.sendMail(mailSettingTo);
			}
			
		}
	
		//发送给各个办事处的待补文件追述(乘用车用)	
		public void sendAllUnCompletedFileListByDeptForCar() throws Exception{
			//判断今天是否是工作日，如果是工作日才发送邮件,否则不发邮件
			try {
				if (isWorkingDay()==false) {
					return; 
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			//1为待补文件追述分组代号
			List<MailSettingTo> mailSettingList = mailSettingService.getMailSettingListByGroup("2");
			
			for (MailSettingTo mailSettingTo : mailSettingList) {
				if(mailSettingTo.getDeptId()==null) {
					continue;
				}
				Map tempMap = new HashMap();
				tempMap.put("decpId", mailSettingTo.getDeptId());
				List listUnCompletedFile = null;
				listUnCompletedFile = (List) DataAccessor.query(
						"rentFile.getAllUnCompletedFileListForDept", tempMap,
						DataAccessor.RS_TYPE.LIST);
				if (listUnCompletedFile.size()==0){
					continue;
				}
				mailSettingTo.setEmailContent(sendUnCompletedFileListByDeptModel(listUnCompletedFile));
				mailUtilService.sendMail(mailSettingTo);
				
				try {
					Integer.valueOf(mailSettingTo.getDeptId());
				} catch (NumberFormatException ex){
					continue;
				}
				
				//该部门人员个人案件
				Map deptMap = new HashMap();
				deptMap.put("deptId", mailSettingTo.getDeptId());
				List<Map> userEmails = (List<Map>) DataAccessor.query("rentFile.getDeptUserEmail", deptMap, DataAccessor.RS_TYPE.LIST);
				for (Map userEmail : userEmails) {
					String userId = userEmail.get("ID").toString();
					String email = userEmail.get("EMAIL").toString();
					Map userMap = new HashMap();
					userMap.put("userId", userId);
					List listUnCompletedFileforUser = (List) DataAccessor.query("rentFile.getAllUnCompletedFileListForUser", userMap, DataAccessor.RS_TYPE.LIST);
					if (listUnCompletedFileforUser.size()==0){
						continue;
					}
					mailSettingTo.setEmailTo(email);
					mailSettingTo.setEmailContent(sendUnCompletedFileListByDeptModel(listUnCompletedFileforUser));
					mailUtilService.sendMail(mailSettingTo);
				}
				
			}
			
		}

		//给每个办事处发送待补文件明细
		public void sendUnCompletedFileListForXiamen() throws MessagingException {
			try {
				Map tempMap = new HashMap();
				List listUnCompletedFile = null;
				listUnCompletedFile = (List) DataAccessor.query(
						"rentFile.getAllUnCompletedFileListXiamen", tempMap,
						DataAccessor.RS_TYPE.LIST);
				
				if (listUnCompletedFile.size()==0){
					return;
				}
			
				MailSettingTo mailSettingTo = new MailSettingTo();
				//套用模板内容
				mailSettingTo.setEmailContent(sendUnCompletedFileListByDeptModel(listUnCompletedFile));
				mailUtilService.sendMail(222, mailSettingTo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//给每个办事处发送待补文件明细
		public void sendUnCompletedFileListForFoshan() throws MessagingException {
			try {
				Map tempMap = new HashMap();
				List listUnCompletedFile = null;
				listUnCompletedFile = (List) DataAccessor.query(
						"rentFile.getAllUnCompletedFileListFoshan", tempMap,
						DataAccessor.RS_TYPE.LIST);
				
				if (listUnCompletedFile.size()==0){
					return;
				}
			
				MailSettingTo mailSettingTo = new MailSettingTo();
				//套用模板内容
				mailSettingTo.setEmailContent(sendUnCompletedFileListByDeptModel(listUnCompletedFile));
				mailUtilService.sendMail(221, mailSettingTo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//给每个办事处发送待补文件明细
		public void sendUnCompletedFileListForDongguan() throws MessagingException {
			try {
				Map tempMap = new HashMap();
				List listUnCompletedFile = null;
				listUnCompletedFile = (List) DataAccessor.query(
						"rentFile.getAllUnCompletedFileListDongguan", tempMap,
						DataAccessor.RS_TYPE.LIST);
				
				if (listUnCompletedFile.size()==0){
					return;
				}
			
				MailSettingTo mailSettingTo = new MailSettingTo();
				//套用模板内容
				mailSettingTo.setEmailContent(sendUnCompletedFileListByDeptModel(listUnCompletedFile));
				mailUtilService.sendMail(220, mailSettingTo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//给每个办事处发送待补文件明细
		public void sendUnCompletedFileListForChengdu() throws MessagingException {
			try {
				Map tempMap = new HashMap();
				List listUnCompletedFile = null;
				listUnCompletedFile = (List) DataAccessor.query(
						"rentFile.getAllUnCompletedFileListChengdu", tempMap,
						DataAccessor.RS_TYPE.LIST);
				
				if (listUnCompletedFile.size()==0){
					return;
				}
			
				MailSettingTo mailSettingTo = new MailSettingTo();
				//套用模板内容
				mailSettingTo.setEmailContent(sendUnCompletedFileListByDeptModel(listUnCompletedFile));
				mailUtilService.sendMail(219, mailSettingTo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//给每个办事处发送待补文件明细
		public void sendUnCompletedFileListForChongqing() throws MessagingException {
			try {
				Map tempMap = new HashMap();
				List listUnCompletedFile = null;
				listUnCompletedFile = (List) DataAccessor.query(
						"rentFile.getAllUnCompletedFileListChongqing", tempMap,
						DataAccessor.RS_TYPE.LIST);
				
				if (listUnCompletedFile.size()==0){
					return;
				}
			
				MailSettingTo mailSettingTo = new MailSettingTo();
				//套用模板内容
				mailSettingTo.setEmailContent(sendUnCompletedFileListByDeptModel(listUnCompletedFile));
				mailUtilService.sendMail(218, mailSettingTo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//给每个办事处发送待补文件明细
		public void sendUnCompletedFileListForNanjing() throws MessagingException {
			try {
				Map tempMap = new HashMap();
				List listUnCompletedFile = null;
				listUnCompletedFile = (List) DataAccessor.query(
						"rentFile.getAllUnCompletedFileListNanjing", tempMap,
						DataAccessor.RS_TYPE.LIST);
				
				if (listUnCompletedFile.size()==0){
					return;
				}
			
				MailSettingTo mailSettingTo = new MailSettingTo();
				//套用模板内容
				mailSettingTo.setEmailContent(sendUnCompletedFileListByDeptModel(listUnCompletedFile));
				mailUtilService.sendMail(217, mailSettingTo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//给每个办事处发送待补文件明细
		public void sendUnCompletedFileListForShanghai() throws MessagingException {
			try {
				Map tempMap = new HashMap();
				List listUnCompletedFile = null;
				listUnCompletedFile = (List) DataAccessor.query(
						"rentFile.getAllUnCompletedFileListShanghai", tempMap,
						DataAccessor.RS_TYPE.LIST);
				
				if (listUnCompletedFile.size()==0){
					return;
				}
			
				MailSettingTo mailSettingTo = new MailSettingTo();
				//套用模板内容
				mailSettingTo.setEmailContent(sendUnCompletedFileListByDeptModel(listUnCompletedFile));
				mailUtilService.sendMail(216, mailSettingTo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//给每个办事处发送待补文件明细
		public void sendUnCompletedFileListForKunshan() throws MessagingException {
			try {
				Map tempMap = new HashMap();
				List listUnCompletedFile = null;
				listUnCompletedFile = (List) DataAccessor.query(
						"rentFile.getAllUnCompletedFileListKunshan", tempMap,
						DataAccessor.RS_TYPE.LIST);
				
				if (listUnCompletedFile.size()==0){
					return;
				}
			
				MailSettingTo mailSettingTo = new MailSettingTo();
				//套用模板内容
				mailSettingTo.setEmailContent(sendUnCompletedFileListByDeptModel(listUnCompletedFile));
				mailUtilService.sendMail(215, mailSettingTo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//给每个办事处发送待补文件明细
		public void sendUnCompletedFileListForSuzhouCar() throws MessagingException {
			try {
				Map tempMap = new HashMap();
				List listUnCompletedFile = null;
				listUnCompletedFile = (List) DataAccessor.query(
						"rentFile.getAllUnCompletedFileListSuzhouCar", tempMap,
						DataAccessor.RS_TYPE.LIST);
				
				if (listUnCompletedFile.size()==0){
					return;
				}
			
				MailSettingTo mailSettingTo = new MailSettingTo();
				//套用模板内容
				mailSettingTo.setEmailContent(sendUnCompletedFileListByDeptModel(listUnCompletedFile));
				mailUtilService.sendMail(223, mailSettingTo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//给每个办事处发送待补文件明细
		public void sendUnCompletedFileListForSuzhou() throws MessagingException {
			try {
				Map tempMap = new HashMap();
				List listUnCompletedFile = null;
				listUnCompletedFile = (List) DataAccessor.query(
						"rentFile.getAllUnCompletedFileListSuzhou", tempMap,
						DataAccessor.RS_TYPE.LIST);
				
				if (listUnCompletedFile.size()==0){
					return;
				}
			
				MailSettingTo mailSettingTo = new MailSettingTo();
				//套用模板内容
				mailSettingTo.setEmailContent(sendUnCompletedFileListByDeptModel(listUnCompletedFile));
				mailUtilService.sendMail(214, mailSettingTo);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
					
		public String sendUnCompletedFileListByDeptModel(List listUnCompletedFile){
				Map tempMap = new HashMap();
				//如果没有待补文件则直接返回，不需要再发送Mail
				
				StringBuffer mailContent = new StringBuffer();
				mailContent.append("<html><head></head>");
				mailContent
						.append("<style>.rhead { background-color: #006699}"
								+ ".Body2 { font-family: Arial, Helvetica, sans-serif; font-weight: normal; color: #000000; font-size: 9pt; text-decoration: none}"
								+ ".Body2BoldWhite2 { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #FFFFFF; font-size: 11pt; text-decoration: none }"
								+ ".Body2Bold { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #000000; font-size: 8pt; text-decoration: none }"
								+ ".r11 {  background-color: #C4E2EC}"
								+ ".r12 { background-color: #D2EFF0}</style><body>");
				mailContent.append("<font size='3'><b>各位好:<b><br></font>"
						+ "<font size='2'>麻烦查收待补，谢谢咯~</font><br><br>");
				mailContent
						.append("<table border='1' cellspacing='0' width='1050px;' cellpadding='0'>"
								+ "<tr class='rhead'>"
								+ "<td class='Body2BoldWhite2' style='width:30px;' align='center'>序号</td>"
								+ "<td class='Body2BoldWhite2' style='width:100px;' align='center'>合同号</td>"
								+ "<td class='Body2BoldWhite2' align='center'>客户名称</td>"
								+ "<td class='Body2BoldWhite2' align='center'>待补文件名称</td>"
								+ "<td class='Body2BoldWhite2' align='center'>办事处</td>"
								+ "<td class='Body2BoldWhite2' align='center'>经办人</td>"
								+ "<td class='Body2BoldWhite2' align='center'>供应商</td>"
								+ "<td class='Body2BoldWhite2' align='center'>拨款日</td>"
								+ "<td class='Body2BoldWhite2' align='center'>应补回日</td>"
								+ "<td class='Body2BoldWhite2' align='center'>延迟天数</td>"
								+ "<td class='Body2BoldWhite2' align='center'>待补原因</td>"
								+ "<td class='Body2BoldWhite2' align='center'>拨款方式</td>"
								+ "<td class='Body2BoldWhite2' align='center'>备注</td></tr>");
				int num = 0;
				if (listUnCompletedFile != null) {
					for (int i = 0; i < listUnCompletedFile.size(); i++) {
						num++;
						tempMap = (Map) listUnCompletedFile.get(i);
						mailContent.append("<tr class='r12'>" + "<td class=body2 >"
								+ num
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listUnCompletedFile.get(i))
										.get("LEASE_CODE")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listUnCompletedFile.get(i))
										.get("CUST_NAME")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listUnCompletedFile.get(i))
										.get("FILE_NAME")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listUnCompletedFile.get(i))
										.get("DECP_NAME_CN")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listUnCompletedFile.get(i)).get("NAME")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listUnCompletedFile.get(i)).get("BRAND")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listUnCompletedFile.get(i))
										.get("FINANCECONTRACT_DATE")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listUnCompletedFile.get(i))
										.get("SHOULD_FINISH_DATE")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listUnCompletedFile.get(i))
										.get("DELAY_DAY")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listUnCompletedFile.get(i))
										.get("ISSURE_REASON")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listUnCompletedFile.get(i)).get("TYPE")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) listUnCompletedFile.get(i))
										.get("FILE_MEMO") + "</td></tr>");
					}
				}
				mailContent.append("</table>");
				mailContent.append("</body></html>");
				return mailContent.toString();
		}

		
		// Add by Michael 2012 10-23 供应商授信额度即将到期提醒Email
		public void sendAllSupplierGrantPriceExpireeList() throws MessagingException {
			try {
				Map tempMap = new HashMap();
				List<Map<String, Object>> supplierGrantPriceExpireeList = null;
				supplierGrantPriceExpireeList = (List<Map<String, Object>>) DataAccessor.query("applyCompanyManage.getSupplierGrantPriceExpire", tempMap, DataAccessor.RS_TYPE.LIST);
				
				//如果没有待补文件则直接返回，不需要再发送Mail
				if (supplierGrantPriceExpireeList == null || supplierGrantPriceExpireeList.size() == 0){
					return;
				}
				
				NumberFormat nfFSNum = new DecimalFormat("#,###,###,##0.00");
				nfFSNum.setGroupingUsed(true);
				nfFSNum.setMaximumFractionDigits(2);
				
				StringBuffer mailContent = new StringBuffer();
				mailContent.append("<html><head></head>");
				mailContent.append("<body>");
				mailContent.append("<font size='3'><b>各位好:<b><br></font>"
								+ "<font size='2'>供应商授信额度已到期及即将到期List，请参阅~</font><br><br>");
				mailContent.append("<table cellspacing='1' width='1050px;' cellpadding='2' style=\"background-color: black;\">"
								+ "<tr>"
								+ "<th style=\"background-color: #FFFFFF; width:30px;\">序号</th>"
								+ "<th style=\"background-color: #FFFFFF; width:100px;\">供应商(级别)</th>"
								
								+ "<th align='center' style=\"background-color: #FFFF99\">连保授信开始日期</th>"
								+ "<th align='center' style=\"background-color: #FFFF99\">连保授信结束日期</th>"
								+ "<th align='center' style=\"background-color: #FFFF99\">连保额度</th>"
								
								+ "<th align='center' style=\"background-color: #99CCFF\">回购授信开始日期</th>"
								+ "<th align='center' style=\"background-color: #99CCFF\">回购授信结束日期</th>"
								+ "<th align='center' style=\"background-color: #99CCFF\">回购额度</th>"
								
								
								+ "<th align='center' style=\"background-color: #33CCCC\">交机前拨款授信开始日期</th>"
								+ "<th align='center' style=\"background-color: #33CCCC\">交机前拨款授信结束日期</th>"
								+ "<th align='center' style=\"background-color: #33CCCC\">交机前拨款额度</th>"
								
								+ "<th align='center' style=\"background-color: #8080C0\">发票待补授信开始日期</th>"
								+ "<th align='center' style=\"background-color: #8080C0\">发票待补授信结束日期</th>"
								+ "<th align='center' style=\"background-color: #8080C0\">发票待补额度</th>"
								+ "</tr>"
						);
				
				for (int i = 0; i < supplierGrantPriceExpireeList.size(); i++) {
					tempMap = (Map) supplierGrantPriceExpireeList.get(i);
					mailContent.append("<tr>" 
							+ "<td style=\"background-color: #FFFFFF\">"
							+ (i + 1)
							+ "</td>"
							+ "<td style=\"background-color: #FFFFFF\">"
							+ supplierGrantPriceExpireeList.get(i).get("NAME")
							+ "(" + supplierGrantPriceExpireeList.get(i).get("SUPP_LEVEL") + ")"
							+ "</td>"
							
							+ "<td style=\"background-color: #FFFF99\">"
							+ supplierGrantPriceExpireeList.get(i).get("LIEN_START_DATE")
							+ "</td>"
							
							+ "<td style=\"background-color: #FFFF99\">"
							+ supplierGrantPriceExpireeList.get(i).get("LIEN_END_DATE")
							+ "</td>"
							+ "<td align='right' style=\"background-color: #FFFF99\">"
							+ updateMoney(DataUtil.doubleUtil(supplierGrantPriceExpireeList.get(i).get("LIEN_GRANT_PRICE")),nfFSNum)
							+ "</td>"
							
							+ "<td style=\"background-color: #99CCFF\">"
							+ supplierGrantPriceExpireeList.get(i).get("REPURCH_START_DATE")
							+ "</td>"
							+ "<td style=\"background-color: #99CCFF\">"
							+ supplierGrantPriceExpireeList.get(i).get("REPURCH_END_DATE")
							+ "</td>"
							+ "<td align='right' style=\"background-color: #99CCFF\">"
							+ updateMoney(DataUtil.doubleUtil(supplierGrantPriceExpireeList.get(i).get("REPURCH_GRANT_PRICE")),nfFSNum)
							+ "</td>"
							
							+ "<td style=\"background-color: #33CCCC\">"
							+ supplierGrantPriceExpireeList.get(i).get("ADVANCE_START_DATE")
							+ "</td>"
							+ "<td style=\"background-color: #33CCCC\">"
							+ supplierGrantPriceExpireeList.get(i).get("ADVANCE_END_DATE")
							+ "</td>"
							+ "<td align='right' style=\"background-color: #33CCCC\">"
							+ updateMoney(DataUtil.doubleUtil(supplierGrantPriceExpireeList.get(i).get("ADVANCEMACHINE_GRANT_PRICE")),nfFSNum)
							+ "</td>"
							
							+ "<td style=\"background-color: #8080C0\">"
							+ supplierGrantPriceExpireeList.get(i).get("VOICE_START_DATE")
							+ "</td>"
							+ "<td style=\"background-color: #8080C0\">"
							+ supplierGrantPriceExpireeList.get(i).get("VOICE_END_DATE")
							+ "</td>"
							+ "<td align='right' style=\"background-color: #8080C0\">"
							+ updateMoney(DataUtil.doubleUtil(supplierGrantPriceExpireeList.get(i).get("VOICE_CREDIT")),nfFSNum)
							+ "</td>"
							+ "</tr>"
					);
				}
				mailContent.append("</table>");
				mailContent.append("</body></html>");
				MailSettingTo mailSettingTo = new MailSettingTo();
				mailSettingTo.setEmailContent(mailContent.toString());
				mailUtilService.sendMail(226, mailSettingTo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private String updateMoney(Double dNum,NumberFormat nfFSNum) {
			String str="";
			if (dNum == 0d) {
				str+="0.00";
				return str;
			} else {
				str+=nfFSNum.format(dNum);
				return str;
			}
		}
		//增加可结清的案件的记录
		private void addSendCompletedFileListLog(Map<String, Object> map ){
			try {
				DataAccessor.execute("settleManage.insertCompletedFileLog", map, DataAccessor.OPERATION_TYPE.INSERT);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}
		/**
		 * 超过90天以上可结清案件
		 * @2012-12月3日 zhangbo
		 * @throws MessagingException
		 */
		public void sendAbleSettleRentListOverThreeM()throws MessagingException {
			MailSettingTo mailSettingTo = new MailSettingTo();
			Map paramMap =new HashMap();
			List  listAbleSettleEmailByDept=null;
			try {
				if (isWorkingDay()==false) {
					return; 
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				listAbleSettleEmailByDept = (List) DataAccessor.query(
					"settleManage.queryAbleSettleRentListOverThreeM", paramMap,
					DataAccessor.RS_TYPE.LIST);
			}catch (Exception e) {
				e.printStackTrace();
			}
			if (listAbleSettleEmailByDept != null && listAbleSettleEmailByDept.size()>0) {
				StringBuffer mailContent = new StringBuffer();
				mailSettingTo.setEmailSubject("可结清案件明细表_超过90天");
				mailContent.append("<html><head></head>");
				mailContent
						.append("<style>.rhead { background-color: #006699}"
							+ ".Body2 { font-family: Arial, Helvetica, sans-serif; font-weight: normal; color: #000000; font-size: 9pt; text-decoration: none}"
							+ ".Body2BoldWhite2 { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #FFFFFF; font-size: 11pt; text-decoration: none }"
							+ ".Body2Bold { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #000000; font-size: 8pt; text-decoration: none }"
							+ ".r11 {  background-color: #C4E2EC}"
							+ ".r12 { background-color: #D2EFF0}</style><body>");
				mailContent.append("<font size='3'><b>各位好:<b><br></font>"
							+ "<font size='2'>以下为可结清案件明细表_超过90天，请参阅~</font><br><br>");
				mailContent
							.append("<table border='1' cellspacing='0' width='1050px;' cellpadding='0'>"
									+ "<tr class='rhead'>"
									+ "<td class='Body2BoldWhite2' style='width:30px;' align='center'>序号</td>"
									+ "<td class='Body2BoldWhite2' style='width:110px;' align='center'>合同号</td>"
									+ "<td class='Body2BoldWhite2' align='center'>客户名称</td>"
									+ "<td class='Body2BoldWhite2' align='center'>业务经办</td>"
									+ "<td class='Body2BoldWhite2' align='center'>租赁期数</td>"
									+ "<td class='Body2BoldWhite2' align='center'>已交期数</td>"
									+ "<td class='Body2BoldWhite2' align='center'>未缴罚息</td>"
									+ "<td class='Body2BoldWhite2' align='center'>未缴期满购买金</td>"
									+ "<td class='Body2BoldWhite2' align='center'>最后一期来款日期</td>"
									+ "<td class='Body2BoldWhite2' align='center'>法务费用</td>"
									+ "<td class='Body2BoldWhite2' align='center'>距今天数</td>"
									+ "<td class='Body2BoldWhite2' align='center'>状态</td></tr>");
						int num=0;
						for (int j = 0; j < listAbleSettleEmailByDept.size(); j++) {
							Map tempMap = new HashMap();
							tempMap = (Map) listAbleSettleEmailByDept.get(j);
							num++;
							mailContent.append("<tr class='r12'>" + "<td class=body2 >"
									+ num
									+ "</td>"
									+ "<td class=body2 >"
									+ ((Map) listAbleSettleEmailByDept.get(j))
											.get("LEASE_CODE")
									+ "</td>"
									+ "<td class=body2 >"
									+ ((Map) listAbleSettleEmailByDept.get(j))
											.get("CUST_NAME")
									+ "</td>"
									+ "<td class=body2>"
									+ ((Map) listAbleSettleEmailByDept.get(j)).get("NAME")
									+ "</td>"
									+ "<td class=body2 >"
									+ ((Map) listAbleSettleEmailByDept.get(j)).get("LEASE_PERIOD")
									+ "</td>"
									+ "<td class=body2>"
									+ ((Map) listAbleSettleEmailByDept.get(j))
											.get("LEASE_PERIOD")
									+ "</td>"
									+ "<td class=body2>"
									+ ((Map) listAbleSettleEmailByDept.get(j))
											.get("DUN_PRICE")
									+ "</td>"
									+ "<td class=body2>"
									+ ((Map) listAbleSettleEmailByDept.get(j))
											.get("LGJ")
									+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("OPPOSINGDATE")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("TOTALLAWYFEE")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) listAbleSettleEmailByDept.get(j))
										.get("DIFF_DAY")
								+ "天</td>"									
									+ "<td class=body2 >正常</td></tr>");
							addSendCompletedFileListLog(tempMap);
						}
						mailContent.append("</table>");
						mailContent.append("</body></html>");	
						mailSettingTo.setEmailContent(mailContent.toString());
						try {
							//发送、抄送对象 通过400 邮件style来动态获取
							mailUtilService.sendMail(400,mailSettingTo);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
		}

		/**
		 * Add by Michael 2012 12-31 For 提醒支票退票提醒  For 财务 、业管人员
		 * @author michael
		 * @throws Exception
		 */
		public void queryRentCheckReturnForAll() throws Exception {
			List checkRentDelivery = new ArrayList();
			checkRentDelivery = (List) DataAccessor.query(
					"rentContract.getReturnCheckDetail", null,
					DataAccessor.RS_TYPE.LIST);
			List checkRentDelivery_new = new ArrayList();
			checkRentDelivery_new = (List) DataAccessor.query(
					"rentContract.getReturnCheckDetail_new", null,
					DataAccessor.RS_TYPE.LIST);
			if (checkRentDelivery.size() > 0 || checkRentDelivery_new.size()>0) {
				MailSettingTo mailSettingTo = new MailSettingTo();
				mailSettingTo.setEmailContent(formatContentToString(checkRentDelivery,checkRentDelivery_new));
				mailUtilService.sendMail(229, mailSettingTo);
			}
		}
		
		/**
		 * Add by Michael 2012 12-31
		 * @throws Exception
		 * 将退票信息发送给相关业务行政、业务、及单位主管
		 */
		public void queryRentCheckReturnForSales() throws Exception {
				List checkRentDelivery = new ArrayList();
				checkRentDelivery = (List) DataAccessor.query(
						"rentContract.getReturnCheckDetail", null,DataAccessor.RS_TYPE.LIST);
				List checkRentDelivery_new = new ArrayList();
				checkRentDelivery_new = (List) DataAccessor.query(
						"rentContract.getReturnCheckDetail_new", null,DataAccessor.RS_TYPE.LIST);
				if (checkRentDelivery.size() > 0 || checkRentDelivery_new.size()>0) {
					MailSettingTo mailSettingTo = new MailSettingTo();
					//查出退票相关的业务行政、业务、及单位主管
					List mailList=(List) DataAccessor.query(
							"rentContract.getReturnCheckLinkEmail", null,DataAccessor.RS_TYPE.LIST);
					String emailTo="";
					for (int i = 0; i < mailList.size(); i++) {
						if(i==0){
							if(((Map) mailList.get(i)).get("EMAIL")!=null && !"".equals(((Map) mailList.get(i)).get("EMAIL"))){
								emailTo=((Map) mailList.get(i)).get("EMAIL")+"";
							}
							
						}else{
							if(((Map) mailList.get(i)).get("EMAIL")!=null && !"".equals(((Map) mailList.get(i)).get("EMAIL"))){
								emailTo=emailTo+ ";"+((Map) mailList.get(i)).get("EMAIL");
							}
						}
					}

					List mailList_new=(List) DataAccessor.query(
							"rentContract.getReturnCheckLinkEmail_new", null,DataAccessor.RS_TYPE.LIST);
					for (int i = 0; i < mailList_new.size(); i++) {
						if(emailTo.length()==0){
							if(((Map) mailList_new.get(i)).get("EMAIL")!=null && !"".equals(((Map) mailList_new.get(i)).get("EMAIL"))){
								emailTo=((Map) mailList_new.get(i)).get("EMAIL")+"";
							}
							
						}else{
							if(((Map) mailList_new.get(i)).get("EMAIL")!=null && !"".equals(((Map) mailList_new.get(i)).get("EMAIL"))){
								emailTo=emailTo+ ";"+((Map) mailList_new.get(i)).get("EMAIL");
							}
						}
					}
					
					mailSettingTo.setEmailTo(emailTo);
					//mailSettingTo.setEmailCc("TEST@tacleasing.cn");
					mailSettingTo.setEmailContent(formatContentToString(checkRentDelivery,checkRentDelivery_new));
					mailUtilService.sendMail(229,mailSettingTo);
				}
		}
		
		/**
		 * Add by Michael 2012 12-31 传入List，将List中的内容画出格式ＨＴＭＬ
		 * @author michael
		 * @param list
		 * @return　　返回HTML格式的Sting
		 */
		public String formatContentToString(List list,List list2){
			StringBuffer mailContent = new StringBuffer();
			if (list.size() > 0||list2.size()>0) {
				mailContent.append("<html><head></head>");
				mailContent
						.append("<style>.rhead { background-color: #006699}"
								+ ".Body2 { font-family: Arial, Helvetica, sans-serif; font-weight: normal; color: #000000; font-size: 9pt; text-decoration: none}"
								+ ".Body2BoldWhite2 { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #FFFFFF; font-size: 11pt; text-decoration: none }"
								+ ".Body2Bold { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #000000; font-size: 8pt; text-decoration: none }"
								+ ".r11 {  background-color: #C4E2EC}"
								+ ".r12 { background-color: #D2EFF0}</style><body>");
				
				mailContent.append("<font size='3'><b>各位好:<b><br></font>"
						+ "<font size='2'>以下为退票支票明细列表，请参阅~</font><br><br>");
				mailContent
						.append("<table border='1' cellspacing='0' width='1050px;' cellpadding='0'>"
								+ "<tr class='rhead'>"
								+ "<td class='Body2BoldWhite2' style='width:40px;' align='center'>序号</td>"
								+ "<td class='Body2BoldWhite2' style='width:100px;' align='center'>支票状态</td>"
								+ "<td class='Body2BoldWhite2' style='width:100px;' align='center'>合同号</td>"
								+ "<td class='Body2BoldWhite2' align='center'>客户名称</td>"
								+ "<td class='Body2BoldWhite2' align='center'>区域办事处</td>"
								+ "<td class='Body2BoldWhite2' align='center'>客户经理</td>"
								+ "<td class='Body2BoldWhite2' align='center'>支票号码</td>"
								+ "<td class='Body2BoldWhite2' align='center'>出票日期</td>"
								+ "<td class='Body2BoldWhite2' align='center'>支票金额</td>"
								+ "<td class='Body2BoldWhite2' align='center'>退票日期</td>"
								+ "<td class='Body2BoldWhite2' align='center'>财务备注</td>"
								+ "<td class='Body2BoldWhite2' align='center'>退票原因</td></tr>");
				int num = 0;
				for (int i = 0; i < list.size(); i++) {
					String state="";
					if("1".equals(((Map)list.get(i)).get("STATE")==null?" ":String.valueOf(((Map)list.get(i)).get("STATE")))){
						  state="已作废";
					}
					if("4".equals(((Map)list.get(i)).get("STATE")==null?" ":String.valueOf(((Map)list.get(i)).get("STATE")))){
						 state="已退票";
					}
					num++;
					mailContent.append("<tr class='r12'>" + "<td class=body2 >"
							+ num
							+ "</td>"
							+ "<td class=body2 >"
							+ state
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) list.get(i))
									.get("LEASE_CODE")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) list.get(i))
									.get("CUST_NAME")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) list.get(i))
									.get("DECP_NAME_CN")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) list.get(i))
									.get("SENSOR_NAME")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) list.get(i))
									.get("CHECK_NUM")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) list.get(i)).get("CHECK_OUT_DATE")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) list.get(i)).get("CHECK_MONEY")
							+ "</td>"	
							+ "<td class=body2 >"
							+ ((Map) list.get(i)).get("RECORDED_DATE")
							+ "</td>"	
							+ "<td class=body2 >"
							+ ((Map) list.get(i)).get("FINANCE_MEMO")
							+ "</td>"	
							+ "<td class=body2>"
							+ ((Map) list.get(i))
									.get("FLAG")
							+ "</td></tr>");
				}
				for (int i = 0; i < list2.size(); i++) {
					String state="";
					if("1".equals(((Map)list2.get(i)).get("STATE")==null?" ":String.valueOf(((Map)list2.get(i)).get("STATE")))){
						  state="已作废";
					}
					if("4".equals(((Map)list2.get(i)).get("STATE")==null?" ":String.valueOf(((Map)list2.get(i)).get("STATE")))){
						 state="已退票";
					}
					num++;
					mailContent.append("<tr class='r12'>" + "<td class=body2 >"
							+ num
							+ "</td>"
							+ "<td class=body2 >"
							+ state
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) list2.get(i))
									.get("LEASE_CODE")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) list2.get(i))
									.get("CUST_NAME")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) list2.get(i))
									.get("DECP_NAME_CN")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) list2.get(i))
									.get("SENSOR_NAME")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) list2.get(i))
									.get("CHECK_NUM")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) list2.get(i)).get("CHECK_OUT_DATE")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) list2.get(i)).get("CHECK_MONEY")
							+ "</td>"	
							+ "<td class=body2 >"
							+ ((Map) list2.get(i)).get("RECORDED_DATE")
							+ "</td>"	
							+ "<td class=body2 >"
							+ ((Map) list2.get(i)).get("FINANCE_MEMO")
							+ "</td>"	
							+ "<td class=body2>"
							+ ((Map) list2.get(i))
									.get("FLAG")
							+ "</td></tr>");
				}
				mailContent.append("</table>");
				mailContent.append("</body></html>");
			}
			return mailContent.toString();
		}
		
		//modify by xuyuefei 2014/7/15 
		//增加公司别(分别向裕融和裕国的 财务发送支票投递提醒邮件)
		public void queryRentCheckForDelivery() throws Exception {
			System.out.println("邮件发送中......");
			Map<String, Object> paramMap = new HashMap<String, Object>();
			Map<String, Object> paramMap1 = new HashMap<String, Object>();
			Map workDayMap = null;
//			Date date=new Date();//取时间
//			Calendar calendar = new GregorianCalendar();
//			calendar.setTime(date);
//			calendar.add(calendar.DATE,1);//把日期往后增加一天.整数往后推,负数往前移动
//			date=calendar.getTime(); //这个时间就是日期往后推一天的结果 
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

			Date date1=new Date();//取时间
			paramMap.put("query_date", formatter.format(date1));
			String strDate = "convert(date,'"+ formatter.format(date1) + "')";
			workDayMap = (Map) DataAccessor.query(
					"financeDecomposeReport.getTodayIsWD", paramMap,
					DataAccessor.RS_TYPE.MAP);
			if (workDayMap != null) {
				//设置公司别  1：裕融   2：裕国
				paramMap.put("companyCode", 1);
				paramMap.put("DATES", strDate);
				paramMap1.put("companyCode", 2);
				paramMap1.put("DATES", strDate);
				List checkRentDelivery = new ArrayList();
				List checkRentDelivery1 = new ArrayList();
				checkRentDelivery = (List) DataAccessor.query(
						"rentContract.getRentCheckDelivery", paramMap,
						DataAccessor.RS_TYPE.LIST);
				checkRentDelivery1 = (List) DataAccessor.query(
						"rentContract.getRentCheckDelivery", paramMap1,
						DataAccessor.RS_TYPE.LIST);
				List checkRentDelivery_new = new ArrayList();
				List checkRentDelivery_new1 = new ArrayList();
				checkRentDelivery_new = (List) DataAccessor.query(
						"rentContract.getRentCheckDelivery_new", paramMap,
						DataAccessor.RS_TYPE.LIST);
				checkRentDelivery_new1 = (List) DataAccessor.query(
						"rentContract.getRentCheckDelivery_new", paramMap1,
						DataAccessor.RS_TYPE.LIST);	
				if (checkRentDelivery.size() > 0 || checkRentDelivery_new.size()>0) {
					StringBuffer mailContent = new StringBuffer();
					mailContent.append("<html><head></head>");
					mailContent
							.append("<style>.rhead { background-color: #006699}"
									+ ".Body2 { font-family: Arial, Helvetica, sans-serif; font-weight: normal; color: #000000; font-size: 9pt; text-decoration: none}"
									+ ".Body2BoldWhite2 { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #FFFFFF; font-size: 11pt; text-decoration: none }"
									+ ".Body2Bold { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #000000; font-size: 8pt; text-decoration: none }"
									+ ".r11 {  background-color: #C4E2EC}"
									+ ".r12 { background-color: #D2EFF0}</style><body>");
					mailContent.append("<font size='3'><b>各位好:<b><br></font>"
							+ "<font size='2'>以下为"+formatter.format(date1)+"需要投递的支票提醒列表，请参阅~</font><br><br>");
					mailContent
							.append("<table border='1' cellspacing='0' width='1050px;' cellpadding='0'>"
									+ "<tr class='rhead'>"
									+ "<td class='Body2BoldWhite2' style='width:40px;' align='center'>序号</td>"
									+ "<td class='Body2BoldWhite2' style='width:100px;' align='center'>合同号</td>"
									+ "<td class='Body2BoldWhite2' align='center'>客户名称</td>"
									+ "<td class='Body2BoldWhite2' align='center'>区域办事处</td>"
									+ "<td class='Body2BoldWhite2' align='center'>客户经理</td>"
									+ "<td class='Body2BoldWhite2' align='center'>支票号码</td>"
									+ "<td class='Body2BoldWhite2' align='center'>出票日期</td>"
									+ "<td class='Body2BoldWhite2' align='center'>支票金额</td>" 
									+ "<td class='Body2BoldWhite2' align='center'>支票类型</td>" 
									+"<td class='Body2BoldWhite2' align='center'>退票次数</td>" 
									+"<td class='Body2BoldWhite2' align='center'>最近一次退票原因</td>" 
									+"</tr>");
					int num = 0;
					for (int i = 0; i < checkRentDelivery.size(); i++) {
						num++;
						mailContent.append("<tr class='r12'>" + "<td class=body2 >"
								+ num
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) checkRentDelivery.get(i))
										.get("LEASE_CODE")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) checkRentDelivery.get(i))
										.get("CUST_NAME")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) checkRentDelivery.get(i))
										.get("DECP_NAME_CN")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) checkRentDelivery.get(i))
										.get("SENSOR_NAME")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) checkRentDelivery.get(i))
										.get("CHECK_NUM")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) checkRentDelivery.get(i)).get("CHECK_OUT_DATE")
								+ "</td>"								
								+ "<td class=body2>"
								+ ((Map) checkRentDelivery.get(i))
										.get("CHECK_MONEY")
								+ "</td>" 
								
								+ "<td class=body2>"
								+ ((Map) checkRentDelivery.get(i))
										.get("CHECK_TYPE")
								+ "</td>" 
								
								+ "<td class=body2>"
								+ ((((Map) checkRentDelivery.get(i)).get("NUM")==null ||((Map) checkRentDelivery.get(i)).get("NUM")=="")?0:((Map) checkRentDelivery.get(i)).get("NUM"))
								+ "</td>" 
								+ "<td class=body2>"
								+((((Map) checkRentDelivery.get(i)).get("RETURN_REASON_NEW")==null ||((Map) checkRentDelivery.get(i)).get("RETURN_REASON_NEW")=="")?"  ":((Map) checkRentDelivery.get(i)).get("RETURN_REASON_NEW"))
								+ "</td>" 								
								+"</tr>");
					}
					
					for (int i = 0; i < checkRentDelivery_new.size(); i++) {
							num++;
							mailContent.append("<tr class='r12'>" + "<td class=body2 >"
							+ num
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) checkRentDelivery_new.get(i))
									.get("LEASE_CODE")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) checkRentDelivery_new.get(i))
									.get("CUST_NAME")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) checkRentDelivery_new.get(i))
									.get("DECP_NAME_CN")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) checkRentDelivery_new.get(i))
									.get("SENSOR_NAME")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) checkRentDelivery_new.get(i))
									.get("CHECK_NUM")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) checkRentDelivery_new.get(i)).get("CHECK_OUT_DATE")
							+ "</td>"								
							+ "<td class=body2>"
							+ ((Map) checkRentDelivery_new.get(i))
									.get("CHECK_MONEY")
							+ "</td>" 
							+ "<td class=body2>"
								+ ((Map) checkRentDelivery_new.get(i))
										.get("CHECK_TYPE")
								+ "</td>" 
							+ "<td class=body2>"
							+ ((((Map) checkRentDelivery_new.get(i)).get("NUM")==null ||((Map) checkRentDelivery_new.get(i)).get("NUM")=="")?0:((Map) checkRentDelivery_new.get(i)).get("NUM"))
							+ "</td>" 
							+ "<td class=body2>"
							+((((Map) checkRentDelivery_new.get(i)).get("RETURN_REASON_NEW")==null ||((Map) checkRentDelivery_new.get(i)).get("RETURN_REASON_NEW")=="")?"  ":((Map) checkRentDelivery_new.get(i)).get("RETURN_REASON_NEW"))
							+ "</td>"							
							+"</tr>");
					}
					
					mailContent.append("</table>");
					mailContent.append("</body></html>");
					MailSettingTo mailSettingTo = new MailSettingTo();
					mailSettingTo.setEmailContent(mailContent.toString());
					mailUtilService.sendMail(228, mailSettingTo);
				}
				//对裕国发送邮件提醒
				if (checkRentDelivery1.size() > 0 || checkRentDelivery_new1.size()>0) {
					StringBuffer mailContent = new StringBuffer();
					mailContent.append("<html><head></head>");
					mailContent
							.append("<style>.rhead { background-color: #006699}"
									+ ".Body2 { font-family: Arial, Helvetica, sans-serif; font-weight: normal; color: #000000; font-size: 9pt; text-decoration: none}"
									+ ".Body2BoldWhite2 { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #FFFFFF; font-size: 11pt; text-decoration: none }"
									+ ".Body2Bold { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #000000; font-size: 8pt; text-decoration: none }"
									+ ".r11 {  background-color: #C4E2EC}"
									+ ".r12 { background-color: #D2EFF0}</style><body>");
					mailContent.append("<font size='3'><b>各位好:<b><br></font>"
							+ "<font size='2'>以下为"+formatter.format(date1)+"需要投递的支票提醒列表，请参阅~</font><br><br>");
					mailContent
							.append("<table border='1' cellspacing='0' width='1050px;' cellpadding='0'>"
									+ "<tr class='rhead'>"
									+ "<td class='Body2BoldWhite2' style='width:40px;' align='center'>序号</td>"
									+ "<td class='Body2BoldWhite2' style='width:100px;' align='center'>合同号</td>"
									+ "<td class='Body2BoldWhite2' align='center'>客户名称</td>"
									+ "<td class='Body2BoldWhite2' align='center'>区域办事处</td>"
									+ "<td class='Body2BoldWhite2' align='center'>客户经理</td>"
									+ "<td class='Body2BoldWhite2' align='center'>支票号码</td>"
									+ "<td class='Body2BoldWhite2' align='center'>出票日期</td>"
									+ "<td class='Body2BoldWhite2' align='center'>支票金额</td>" 
									+ "<td class='Body2BoldWhite2' align='center'>支票类型</td>" 
									+"<td class='Body2BoldWhite2' align='center'>退票次数</td>" 
									+"<td class='Body2BoldWhite2' align='center'>最近一次退票原因</td>" 
									+"</tr>");
					int num = 0;
					for (int i = 0; i < checkRentDelivery1.size(); i++) {
						num++;
						mailContent.append("<tr class='r12'>" + "<td class=body2 >"
								+ num
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) checkRentDelivery1.get(i))
										.get("LEASE_CODE")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) checkRentDelivery1.get(i))
										.get("CUST_NAME")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) checkRentDelivery1.get(i))
										.get("DECP_NAME_CN")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) checkRentDelivery1.get(i))
										.get("SENSOR_NAME")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) checkRentDelivery1.get(i))
										.get("CHECK_NUM")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) checkRentDelivery1.get(i)).get("CHECK_OUT_DATE")
								+ "</td>"								
								+ "<td class=body2>"
								+ ((Map) checkRentDelivery1.get(i))
										.get("CHECK_MONEY")
								+ "</td>" 
								
								+ "<td class=body2>"
								+ ((Map) checkRentDelivery1.get(i))
										.get("CHECK_TYPE")
								+ "</td>" 
								
								+ "<td class=body2>"
								+ ((((Map) checkRentDelivery1.get(i)).get("NUM")==null ||((Map) checkRentDelivery1.get(i)).get("NUM")=="")?0:((Map) checkRentDelivery1.get(i)).get("NUM"))
								+ "</td>" 
								+ "<td class=body2>"
								+((((Map) checkRentDelivery1.get(i)).get("RETURN_REASON_NEW")==null ||((Map) checkRentDelivery1.get(i)).get("RETURN_REASON_NEW")=="")?"  ":((Map) checkRentDelivery1.get(i)).get("RETURN_REASON_NEW"))
								+ "</td>" 								
								+"</tr>");
					}
					
					for (int i = 0; i < checkRentDelivery_new1.size(); i++) {
							num++;
							mailContent.append("<tr class='r12'>" + "<td class=body2 >"
							+ num
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) checkRentDelivery_new1.get(i))
									.get("LEASE_CODE")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) checkRentDelivery_new1.get(i))
									.get("CUST_NAME")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) checkRentDelivery_new1.get(i))
									.get("DECP_NAME_CN")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) checkRentDelivery_new1.get(i))
									.get("SENSOR_NAME")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) checkRentDelivery_new1.get(i))
									.get("CHECK_NUM")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) checkRentDelivery_new1.get(i)).get("CHECK_OUT_DATE")
							+ "</td>"								
							+ "<td class=body2>"
							+ ((Map) checkRentDelivery_new1.get(i))
									.get("CHECK_MONEY")
							+ "</td>" 
							+ "<td class=body2>"
								+ ((Map) checkRentDelivery_new1.get(i))
										.get("CHECK_TYPE")
								+ "</td>" 
							+ "<td class=body2>"
							+ ((((Map) checkRentDelivery_new1.get(i)).get("NUM")==null ||((Map) checkRentDelivery_new1.get(i)).get("NUM")=="")?0:((Map) checkRentDelivery_new1.get(i)).get("NUM"))
							+ "</td>" 
							+ "<td class=body2>"
							+((((Map) checkRentDelivery_new1.get(i)).get("RETURN_REASON_NEW")==null ||((Map) checkRentDelivery_new1.get(i)).get("RETURN_REASON_NEW")=="")?"  ":((Map) checkRentDelivery_new1.get(i)).get("RETURN_REASON_NEW"))
							+ "</td>"							
							+"</tr>");
					}
					
					mailContent.append("</table>");
					mailContent.append("</body></html>");
					MailSettingTo mailSettingTo = new MailSettingTo();
					mailSettingTo.setEmailContent(mailContent.toString());
					mailSettingTo.setEmailTo("liqing@tacleasing.cn");
					mailSettingTo.setEmailCc("fannie@tacleasing.cn");
					mailSettingTo.setSendCount(0);
					mailSettingTo.setSendFlag(0);
					mailSettingTo.setEmailSubject("支票投递提醒");
					mailUtilService.sendMail(mailSettingTo);
				}
			}
		}
		
		public void findAllApplyCompany () {
			Map context= new HashMap();
			Map outputMap = new HashMap();
			List dw = null;
			// 数字格式
			
			//Add by Michael 2012 02/07 供应商增加 连保、回购 金额统计
			double lianbao=0d;
			double huigou=0d;
			Map tempSuplTrue=null;
			
			int guihutype=0;


			context.put("C", "租金");
	        NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
	        nfFSNum.setGroupingUsed(true);
	        nfFSNum.setMaximumFractionDigits(2);
				try {
					//根据查询类型不同要调用不同的sql,默认是供应商
					if(guihutype==0){
						dw =(List) DataAccessor.query("applyCompanyManage.findAllApplyCompany", context,DataAccessor.RS_TYPE.LIST);
						if(dw != null){
							for(int i=0;i < dw.size();i++){
								Map temp = (Map) dw.get(i) ;
								
								//Modify by Michael 2012 08-06
								//分别根据留购和连保的已用额度进行加总统计
//								Double lienLastPrice =(Double) (SelectReportInfo.selectApplyLienLastPrice(Integer.parseInt(temp.get("ID").toString()))==null ? 0.0 :SelectReportInfo.selectApplyLienLastPrice(Integer.parseInt(temp.get("ID").toString())));
//								Double repurchLastPrice=(Double) (SelectReportInfo.selectApplyRepurchLastPrice(Integer.parseInt(temp.get("ID").toString()))==null ? 0.0 :SelectReportInfo.selectApplyRepurchLastPrice(Integer.parseInt(temp.get("ID").toString())));
								//已用留购额度加上已用连保额度
								if(temp.get("GRANT_PRICE") != null){
									//temp.put("LAST_PRICE",(lienLastPrice>0?lienLastPrice:0.0)+(repurchLastPrice>0?repurchLastPrice:0.0));
									temp.put("LAST_PRICE",SelectReportInfo.selectApplyLastPrice(Integer.parseInt(temp.get("ID").toString()))==null ? 0.0 :SelectReportInfo.selectApplyLastPrice(Integer.parseInt(temp.get("ID").toString())));
								}
								
								Map LastPrice=(Map)DataAccessor.query("beforeMakeContract.selectApplySumIrrMonthAndLastPrice", temp, DataAccessor.RS_TYPE.MAP);
								if(LastPrice!=null){	
									temp.put("SUMLASTPRICE", LastPrice.get("SHENGYUBENJIN"));
								}
								
								//Add by Michael 2012 02/07 供应商增加 连保、回购 金额统计--------------
								Map allLastPriceMap=SelectReportInfo.selectApplyAllLastPrice(Integer.parseInt(temp.get("ID").toString()));
								if (allLastPriceMap!=null){
									lianbao=DataUtil.doubleUtil(allLastPriceMap.get("shouxinjianshaoe_lien"));
									temp.put("LIANBAO", lianbao);
									huigou=DataUtil.doubleUtil(allLastPriceMap.get("shouxinjianshaoe_repurch"));
									temp.put("HUIGOU", huigou);
								}
								//-------------------------------------------------------------------
								
							}
						}
					}
					
					if (dw.size() > 0) {
					StringBuffer mailContent = new StringBuffer();
					mailContent.append("<html><head></head>");
					mailContent
							.append("<style>.rhead { background-color: #006699}"
									+ ".Body2 { font-family: Arial, Helvetica, sans-serif; font-weight: normal; color: #000000; font-size: 9pt; text-decoration: none}"
									+ ".Body2BoldWhite2 { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #FFFFFF; font-size: 11pt; text-decoration: none }"
									+ ".Body2Bold { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #000000; font-size: 8pt; text-decoration: none }"
									+ ".r11 {  background-color: #C4E2EC}"
									+ ".r12 { background-color: #D2EFF0}</style><body>");
					mailContent.append("<font size='3'><b>各位好:<b><br></font>"
							+ "<font size='2'>以下为供应商额度到期提醒列表，请参阅~</font><br><br>");
					mailContent
							.append("<table border='1' cellspacing='0' width='1050px;' cellpadding='0'>"
									+ "<tr class='rhead'>"
									+ "<td class='Body2BoldWhite2' style='width:40px;' align='center'>序号</td>"
									+ "<td class='Body2BoldWhite2' style='width:100px;' align='center'>供应商</td>"
									+ "<td class='Body2BoldWhite2' align='center'>连保额度</td>"
									+ "<td class='Body2BoldWhite2' align='center'>回购额度</td>"
									+ "<td class='Body2BoldWhite2' align='center'>已用连保额度</td>"
									+ "<td class='Body2BoldWhite2' align='center'>已用回购额度</td></tr>");
					int num = 0;
					for (int i = 0; i < dw.size(); i++) {
						num++;
						mailContent.append("<tr class='r12'>" + "<td class=body2 >"
								+ num
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) dw.get(i))
										.get("NAME")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) dw.get(i))
										.get("LIEN_GRANT_PRICE")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) dw.get(i))
										.get("REPURCH_GRANT_PRICE")
								+ "</td>"
								+ "<td class=body2 >"
								+ ((Map) dw.get(i))
										.get("LIANBAO")
								+ "</td>"
								+ "<td class=body2>"
								+ ((Map) dw.get(i))
										.get("HUIGOU")
								+ "</td></tr>");
					}
					mailContent.append("</table>");
					mailContent.append("</body></html>");
					MailSettingTo mailSettingTo = new MailSettingTo();
					mailSettingTo.setEmailTo("michael@tacleasing.cn");
					mailSettingTo.setEmailSubject("供应商授信");
					mailSettingTo.setEmailContent(mailContent.toString());
					mailUtilService.sendMail(mailSettingTo);
					}
				}catch (Exception e) {
					// TODO: handle exception
				}
		}

		
		/**
		 * Add by Michael 2012 12-31 For 提醒支票换票提醒  For 财务 、业管人员
		 * @author michael
		 * @throws Exception
		 */
		public void queryRentCheckChangeTicketForAll() throws Exception {
			List checkRentDelivery = new ArrayList();
			checkRentDelivery = (List) DataAccessor.query(
					"rentContract.getChangeTicketDetail", null,
					DataAccessor.RS_TYPE.LIST);
			List checkRentDelivery_new = new ArrayList();
			checkRentDelivery_new = (List) DataAccessor.query(
					"rentContract.getChangeTicketDetail_new", null,
					DataAccessor.RS_TYPE.LIST);
			if (checkRentDelivery.size() > 0 || checkRentDelivery_new.size()>0) {
				MailSettingTo mailSettingTo = new MailSettingTo();
				mailSettingTo.setEmailContent(formatChangeTicketContentToString(checkRentDelivery,checkRentDelivery_new));
				mailUtilService.sendMail(230, mailSettingTo);
			}
		}
		
		/**
		 * Add by Michael 2012 12-31
		 * @throws Exception
		 * 将换票信息发送给相关业务行政、业务、及单位主管
		 */
		public void queryRentCheckChangeTicketForSales() throws Exception {
			List checkRentDelivery = new ArrayList();
			checkRentDelivery = (List) DataAccessor.query(
					"rentContract.getChangeTicketDetail", null,DataAccessor.RS_TYPE.LIST);
			List checkRentDelivery_new = new ArrayList();
			checkRentDelivery_new = (List) DataAccessor.query(
					"rentContract.getChangeTicketDetail_new", null,DataAccessor.RS_TYPE.LIST);
			if (checkRentDelivery.size() > 0 || checkRentDelivery_new.size()>0) {
				MailSettingTo mailSettingTo = new MailSettingTo();
				//查出退票相关的业务行政、业务、及单位主管
				List mailList=(List) DataAccessor.query(
						"rentContract.getCheckChangeCheckLinkEmail", null,DataAccessor.RS_TYPE.LIST);
				String emailTo="";
				for (int i = 0; i < mailList.size(); i++) {
					if(i==0){
						if(((Map) mailList.get(i)).get("EMAIL")!=null && !"".equals(((Map) mailList.get(i)).get("EMAIL"))){
							emailTo=((Map) mailList.get(i)).get("EMAIL")+"";
						}
						
					}else{
						if(((Map) mailList.get(i)).get("EMAIL")!=null && !"".equals(((Map) mailList.get(i)).get("EMAIL"))){
							emailTo=emailTo+ ";"+((Map) mailList.get(i)).get("EMAIL");
						}
					}
				}

				List mailList_new=(List) DataAccessor.query(
						"rentContract.getCheckChangeCheckLinkEmail_new", null,DataAccessor.RS_TYPE.LIST);
				for (int i = 0; i < mailList_new.size(); i++) {
					if(emailTo.length()==0){
						if(((Map) mailList_new.get(i)).get("EMAIL")!=null && !"".equals(((Map) mailList_new.get(i)).get("EMAIL"))){
							emailTo=((Map) mailList_new.get(i)).get("EMAIL")+"";
						}
						
					}else{
						if(((Map) mailList_new.get(i)).get("EMAIL")!=null && !"".equals(((Map) mailList_new.get(i)).get("EMAIL"))){
							emailTo=emailTo+ ";"+((Map) mailList_new.get(i)).get("EMAIL");
						}
					}
				}
				
				mailSettingTo.setEmailTo(emailTo);
				mailSettingTo.setEmailCc("TEST@tacleasing.cn");
				mailSettingTo.setEmailContent(formatChangeTicketContentToString(checkRentDelivery,checkRentDelivery_new));
				mailUtilService.sendMail(230,mailSettingTo);
			}
		}
		
		/**
		 * Add by Michael 2012 12-31 传入List，将List中的内容画出格式ＨＴＭＬ
		 * @author michael
		 * @param list
		 * @return　　返回HTML格式的Sting
		 */
		public String formatChangeTicketContentToString(List list,List list2){
			StringBuffer mailContent = new StringBuffer();
			if (list.size() > 0||list2.size()>0) {
				mailContent.append("<html><head></head>");
				mailContent
						.append("<style>.rhead { background-color: #006699}"
								+ ".Body2 { font-family: Arial, Helvetica, sans-serif; font-weight: normal; color: #000000; font-size: 9pt; text-decoration: none}"
								+ ".Body2BoldWhite2 { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #FFFFFF; font-size: 11pt; text-decoration: none }"
								+ ".Body2Bold { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #000000; font-size: 8pt; text-decoration: none }"
								+ ".r11 {  background-color: #C4E2EC}"
								+ ".r12 { background-color: #D2EFF0}</style><body>");
				mailContent.append("<font size='3'><b>各位好:<b><br></font>"
						+ "<font size='2'>以下为需要换票支票明细列表，请参阅~</font><br><br>");
				mailContent
						.append("<table border='1' cellspacing='0' width='1050px;' cellpadding='0'>"
								+ "<tr class='rhead'>"
								+ "<td class='Body2BoldWhite2' style='width:40px;' align='center'>序号</td>"
								+ "<td class='Body2BoldWhite2' style='width:100px;' align='center'>合同号</td>"
								+ "<td class='Body2BoldWhite2' align='center'>客户名称</td>"
								+ "<td class='Body2BoldWhite2' align='center'>区域办事处</td>"
								+ "<td class='Body2BoldWhite2' align='center'>客户经理</td>"
								+ "<td class='Body2BoldWhite2' align='center'>支票号码</td>"
								+ "<td class='Body2BoldWhite2' align='center'>出票日期</td>"
								+ "<td class='Body2BoldWhite2' align='center'>支票金额</td>"
								+ "<td class='Body2BoldWhite2' align='center'>财务备注</td></tr>");
				int num = 0;
				for (int i = 0; i < list.size(); i++) {
					num++;
					mailContent.append("<tr class='r12'>" + "<td class=body2 >"
							+ num
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) list.get(i))
									.get("LEASE_CODE")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) list.get(i))
									.get("CUST_NAME")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) list.get(i))
									.get("DECP_NAME_CN")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) list.get(i))
									.get("SENSOR_NAME")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) list.get(i))
									.get("CHECK_NUM")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) list.get(i)).get("CHECK_OUT_DATE")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) list.get(i)).get("CHECK_MONEY")
							+ "</td>"	
							+ "<td class=body2 >"
							+ ((Map) list.get(i)).get("FINANCE_MEMO")
							+ "</td></tr>");
				}
				for (int i = 0; i < list2.size(); i++) {
					num++;
					mailContent.append("<tr class='r12'>" + "<td class=body2 >"
							+ num
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) list2.get(i))
									.get("LEASE_CODE")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) list2.get(i))
									.get("CUST_NAME")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) list2.get(i))
									.get("DECP_NAME_CN")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) list2.get(i))
									.get("SENSOR_NAME")
							+ "</td>"
							+ "<td class=body2>"
							+ ((Map) list2.get(i))
									.get("CHECK_NUM")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) list2.get(i)).get("CHECK_OUT_DATE")
							+ "</td>"
							+ "<td class=body2 >"
							+ ((Map) list2.get(i)).get("CHECK_MONEY")
							+ "</td>"	
							+ "<td class=body2 >"
							+ ((Map) list2.get(i)).get("FINANCE_MEMO")
							+ "</td></tr>");
				}
				mailContent.append("</table>");
				mailContent.append("</body></html>");
			}
			return mailContent.toString();
		}
		
		/**
		 * Add by Michael 2013 03-21
		 * 获取要发送的HR Email讯息
		 * @throws Exception 
		 */
		public void getHRSendEmailInfo() throws Exception{
			List hrEmailInfoList=null;
			Map context= new HashMap();
			Map tempMap;
			
			int call_ID=(Integer) DataAccessor.query("financeDecomposeReport.getCallID", null, DataAccessor.RS_TYPE.OBJECT);
			context.put("CALL_ID", call_ID);
			hrEmailInfoList=(List) DataAccessor.query("financeDecomposeReport.getHRCallInfo", context, DataAccessor.RS_TYPE.LIST);
			
			for (int i = 0; i < hrEmailInfoList.size(); i++) {
				tempMap = (Map) hrEmailInfoList.get(i);
				DataAccessor.getSession().insert("financeDecomposeReport.insertIntoHREmailInfo", tempMap);
			}

		}
		
		public static void main(String args[]){
				// String
				// emailPattern="[a-zA-Z0-9][a-zA-Z0-9._-]{2,16}[a-zA-Z0-9]@[a-zA-Z0-9]+.[a-zA-Z0-9]+";
				//String emailPattern = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
				//String emailPattern = "\\p{Alpha}\\w{2,15}[@][a-z0-9]{3,}[.]\\p{Lower}{2,}";
				//String emailPattern = "\\w+(\\.\\w+)*@\\w+(\\.\\w+)+";
				
				String emailPattern1 ="^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
				String emailPattern2 = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$" ;  
				String aa="1.休假申请：<a href ='###' onclick='if(typeof(CreateNewTabWin) == 'function'){CreateNewTabWin('../JHSoft.Web.HrmAttendance/Attendance_Leave_Approve.aspx?Vmt3SVhrRzBCTWVUU1BRUythQTJBN1BjSHlKVkVrREdDeTZOcFdUcStoNE9BVjB1NDFLcGhES2ZkQlRsMWlUeFZtVGc2NEZwRGJjeDQ5aHBTSDJoTkFNV2tIOFhHRmllSTFubU43eVBBQkVZMlJPdjhxOEsrMDFWS21McXY3MFpEckl6VjYvYjdpUT0=&&');} else{parent.CreateNewTabWin('../JHSoft.Web.HrmAttendance/Attendance_Leave_Approve.aspx?Vmt3SVhrRzBCTWVUU1BRUythQTJBN1BjSHlKVkVrREdDeTZOcFdUcStoNE9BVjB1NDFLcGhES2ZkQlRsMWlUeFZtVGc2NEZwRGJjeDQ5aHBTSDJoTkFNV2tIOFhHRmllSTFubU43eVBBQkVZMlJPdjhxOEsrMDFWS21McXY3MFpEckl6VjYvYjdpUT0=&&');}'>病假申请</a>"; 
				//boolean result = Pattern.matches(emailPattern2, email);
				String rrr = "(?:<a[^>]*>)(.*?)(?:<\\/a[^>]*>)/gi";	
				aa.matches(rrr);
				System.out.println(aa.matches(rrr));
		}

		public void setMailSettingService(MailSettingService mailSettingService) {
			this.mailSettingService = mailSettingService;
		}
		
		// add by xuyuefei 2014/8/29 每周一向乘用车业务员发送乘用车回租案件开票资料报表
		public void sendEmaiToSessorByWeek() throws Exception {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			List<LeaseBackCaseTo> LeaseBackCaseToList=new ArrayList<LeaseBackCaseTo>();
			List<LeaseBackCaseTo> LeaseBackCaseToList0=new ArrayList<LeaseBackCaseTo>();

				LeaseBackCaseToList0 = (List<LeaseBackCaseTo>) DataAccessor.query(
						"rentContract.getCaseByLastWeek", paramMap,
						DataAccessor.RS_TYPE.LIST);
				for(int k=0;LeaseBackCaseToList0!=null&&k<LeaseBackCaseToList0.size();k++){
					//如果6个栏位中都为正常收件，此案件不再发送
					LeaseBackCaseTo t=LeaseBackCaseToList0.get(k);
					if(t.getPersonal().equals("Y")&&t.getTacLeasingContract().equals("Y")&&t.getIdcard().equals("Y")
							&&t.getSalesInvoice().equals("Y")&&t.getDriverLicense().equals("Y")&&t.getContract().equals("Y")){
						
					}else{
						LeaseBackCaseToList.add(t);
					}
				}

				if (LeaseBackCaseToList.size() > 0) {
					StringBuffer mailContent = new StringBuffer();
					mailContent.append("<html><head></head>");
					mailContent
							.append("<style>.rhead { background-color: #006699}"
									+ ".Body2 { font-family: Arial, Helvetica, sans-serif; font-weight: normal; color: #000000; font-size: 9pt; text-decoration: none}"
									+ ".Body2BoldWhite2 { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #FFFFFF; font-size: 11pt; text-decoration: none }"
									+ ".Body2Bold { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #000000; font-size: 8pt; text-decoration: none }"
									+ ".r11 {  background-color: #C4E2EC}"
									+ ".r12 { background-color: #D2EFF0}</style><body>");
					mailContent.append("<font size='3'><b>各位好:<b><br></font>"
							+ "<font size='2'>以下为上周乘用车回租案件代开发票资料控管表，请参阅~</font><br><br>");
					mailContent
							.append("<table border='1' cellspacing='0' width='1050px;' cellpadding='0'>"
									+ "<tr class='rhead'>"
									+ "<td class='Body2BoldWhite2' style='width:40px;' align='center'>序号</td>"
									+ "<td class='Body2BoldWhite2' align='center'>客户名称</td>"
									+ "<td class='Body2BoldWhite2' style='width:100px;' align='center'>合同号</td>"
									+ "<td class='Body2BoldWhite2' align='center'>业务员</td>"
									+ "<td class='Body2BoldWhite2' align='center'>业务主管</td>"
									+ "<td class='Body2BoldWhite2' align='center'>办事处</td>"
									+ "<td class='Body2BoldWhite2' align='center'>设备总价款</td>" 
									+ "<td class='Body2BoldWhite2' align='center'>拨款日</td>"
									+ "<td class='Body2BoldWhite2' align='center'>个人委托书</td>" 
									+"<td class='Body2BoldWhite2' align='center'>个人身份证</td>" 
									+"<td class='Body2BoldWhite2' align='center'>销售发票</td>"
									+"<td class='Body2BoldWhite2' align='center'>行驶证</td>"
									+"<td class='Body2BoldWhite2' align='center'>裕融租赁合同</td>"
									+"<td class='Body2BoldWhite2' align='center'>买卖合同</td>"
									+"</tr>");
					int num = 0;
					for (int i = 0; i < LeaseBackCaseToList.size(); i++) {
						num++;
						mailContent.append("<tr class='r12'>" + "<td class=body2 >"
								+ num
								+ "</td>"
								+ "<td class=body2 >"
								+ (LeaseBackCaseToList.get(i).getCustName())
								+ "</td>"
								+ "<td class=body2 >"
								+ (LeaseBackCaseToList.get(i).getLeaseCode())
								+ "</td>"
								+ "<td class=body2 >"
								+ (LeaseBackCaseToList.get(i).getSessor())
								+ "</td>"
								+ "<td class=body2 >"
								+ (LeaseBackCaseToList.get(i).getClark())
								+ "</td>"
								+ "<td class=body2>"
								+ (LeaseBackCaseToList.get(i).getDept())
								+ "</td>"
								+ "<td class=body2 >"
								+ (LeaseBackCaseToList.get(i).getTotalPrice())
								+ "</td>"								
								+ "<td class=body2>"
								+ (LeaseBackCaseToList.get(i).getPayDate())
								+ "</td>" 
								+ "<td class=body2>"
								+ (LeaseBackCaseToList.get(i).getPersonal())
								+ "</td>" 
								+ "<td class=body2>"
								+ (LeaseBackCaseToList.get(i).getIdcard())
								+ "</td>" 
								+ "<td class=body2>"
								+ (LeaseBackCaseToList.get(i).getSalesInvoice())
								+ "</td>"
								+ "<td class=body2>"
								+ (LeaseBackCaseToList.get(i).getDriverLicense())
								+ "</td>"
							    + "<td class=body2>"
								+ (LeaseBackCaseToList.get(i).getTacLeasingContract())
								+ "</td>"
								+ "<td class=body2>"
								+ (LeaseBackCaseToList.get(i).getContract())
								+ "</td>"
								+"</tr>");
					}
					mailContent.append("</table>");
					mailContent.append("</body></html>");
					MailSettingTo mailSettingTo = new MailSettingTo();
					mailSettingTo.setEmailContent(mailContent.toString());
					mailSettingTo.setEmailSubject("乘用车回租案件代开发票资料周报表");
					mailUtilService.sendMail(7777,mailSettingTo);
				}
		}
		
		//每月26号向乘用车业务员发送乘用车回租案件代开发票资料报表
		public void sendEmaiToSessorByMonth() throws Exception{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Map<String, Object> paramMap = new HashMap<String, Object>();
			List<LeaseBackCaseTo> LeaseBackCaseToList=new ArrayList<LeaseBackCaseTo>();
			Calendar calendar=Calendar.getInstance();
			int year=calendar.get(Calendar.YEAR);
			int month=calendar.get(Calendar.MONTH)+1;
			int day=calendar.get(Calendar.DATE);
			/*ReportDateTo to=ReportDateUtil.getDateByDate("2014-7-26");
			if("2014-7-26".equals(DateUtil.dateToString(to.getBeginTime(),"yyyy-MM-dd"))) {
				
			}*/
			if(month==2||month==3||month==4||month==5||month==6||month==7||month==8||month==9||month==10||month==11){
				paramMap.put("beginDate", formatter.parse(year+"-"+(month-1)+"-"+26));
				paramMap.put("endDate", formatter.parse(year+"-"+month+"-"+25));
			}
			   //每个月26号，12月拨款的乘用车回租案件1月1号 发送给乘用车业务员
               if((month==1&&day==26)||(month==2&&day==26)||(month==3&&day==26)||(month==4&&day==26)||(month==5&&day==26)
            		                 ||(month==6&&day==26)||(month==7&&day==26)||(month==8&&day==26)||(month==9&&day==26)||(month==10&&day==26)
            		                 ||(month==11&&day==26)||(month==1&&day==1)){
            	   //如果是1月1号，发送1月份拨款的案子
       			   if(month==1&&day==26){
    				paramMap.put("beginDate", formatter.parse(year+"-"+month+"-"+1));
    				paramMap.put("endDate", formatter.parse(year+"-"+month+"-"+25));
    			    }
            	   //如果是1月1号，发送去年12月份拨款的案子
            	   if(month==1&&day==1){
       				paramMap.put("beginDate", formatter.parse((year-1)+"-"+11+"-"+26));
    				paramMap.put("endDate", formatter.parse((year-1)+"-"+12+"-"+31));
            	   }
				LeaseBackCaseToList = (List<LeaseBackCaseTo>) DataAccessor.query(
						"rentContract.getCaseByLastMonth", paramMap,
						DataAccessor.RS_TYPE.LIST);

				if (LeaseBackCaseToList.size() > 0) {
					StringBuffer mailContent = new StringBuffer();
					mailContent.append("<html><head></head>");
					mailContent
							.append("<style>.rhead { background-color: #006699}"
									+ ".Body2 { font-family: Arial, Helvetica, sans-serif; font-weight: normal; color: #000000; font-size: 9pt; text-decoration: none}"
									+ ".Body2BoldWhite2 { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #FFFFFF; font-size: 11pt; text-decoration: none }"
									+ ".Body2Bold { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #000000; font-size: 8pt; text-decoration: none }"
									+ ".r11 {  background-color: #C4E2EC}"
									+ ".r12 { background-color: #D2EFF0}</style><body>");
					mailContent.append("<font size='3'><b>各位好:<b><br></font>"
							+ "<font size='2'>以下为上月乘用车回租案件代开发票资料控管表，请参阅~</font><br><br>");
					mailContent
							.append("<table border='1' cellspacing='0' width='1050px;' cellpadding='0'>"
									+ "<tr class='rhead'>"
									+ "<td class='Body2BoldWhite2' style='width:40px;' align='center'>序号</td>"
									+ "<td class='Body2BoldWhite2' align='center'>客户名称</td>"
									+ "<td class='Body2BoldWhite2' style='width:100px;' align='center'>合同号</td>"
									+ "<td class='Body2BoldWhite2' align='center'>业务员</td>"
									+ "<td class='Body2BoldWhite2' align='center'>业务主管</td>"
									+ "<td class='Body2BoldWhite2' align='center'>办事处</td>"
									+ "<td class='Body2BoldWhite2' align='center'>设备总价款</td>" 
									+ "<td class='Body2BoldWhite2' align='center'>拨款日</td>"
									+ "<td class='Body2BoldWhite2' align='center'>个人委托书</td>" 
									+"<td class='Body2BoldWhite2' align='center'>个人身份证</td>" 
									+"<td class='Body2BoldWhite2' align='center'>销售发票</td>"
									+"<td class='Body2BoldWhite2' align='center'>行驶证</td>"
									+"<td class='Body2BoldWhite2' align='center'>裕融租赁合同</td>"
									+"<td class='Body2BoldWhite2' align='center'>买卖合同</td>"
									+"</tr>");
					int num = 0;
					for (int i = 0; i < LeaseBackCaseToList.size(); i++) {
						num++;
						mailContent.append("<tr class='r12'>" + "<td class=body2 >"
								+ num
								+ "</td>"
								+ "<td class=body2 >"
								+ (LeaseBackCaseToList.get(i).getCustName())
								+ "</td>"
								+ "<td class=body2 >"
								+ (LeaseBackCaseToList.get(i).getLeaseCode())
								+ "</td>"
								+ "<td class=body2 >"
								+ (LeaseBackCaseToList.get(i).getSessor())
								+ "</td>"
								+ "<td class=body2 >"
								+ (LeaseBackCaseToList.get(i).getClark())
								+ "</td>"
								+ "<td class=body2>"
								+ (LeaseBackCaseToList.get(i).getDept())
								+ "</td>"
								+ "<td class=body2 >"
								+ (LeaseBackCaseToList.get(i).getTotalPrice())
								+ "</td>"								
								+ "<td class=body2>"
								+ (LeaseBackCaseToList.get(i).getPayDate())
								+ "</td>" 
								+ "<td class=body2>"
								+ (LeaseBackCaseToList.get(i).getPersonal())
								+ "</td>" 
								+ "<td class=body2>"
								+ (LeaseBackCaseToList.get(i).getIdcard())
								+ "</td>" 
								+ "<td class=body2>"
								+ (LeaseBackCaseToList.get(i).getSalesInvoice())
								+ "</td>"
								+ "<td class=body2>"
								+ (LeaseBackCaseToList.get(i).getDriverLicense())
								+ "</td>"
							    + "<td class=body2>"
								+ (LeaseBackCaseToList.get(i).getTacLeasingContract())
								+ "</td>"
								+ "<td class=body2>"
								+ (LeaseBackCaseToList.get(i).getContract())
								+ "</td>"
								+"</tr>");
					}
					mailContent.append("</table>");
					mailContent.append("</body></html>");
					MailSettingTo mailSettingTo = new MailSettingTo();
					mailSettingTo.setEmailContent(mailContent.toString());
					mailSettingTo.setEmailSubject("乘用车回租案件代开发票资料月报表");
					mailUtilService.sendMail(8888,mailSettingTo);
				}
              }
		}
}
