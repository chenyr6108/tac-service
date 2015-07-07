package com.brick.sms.email;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.mail.Session;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import com.brick.service.core.DataAccessor;

public class CommonEmail {
    private String host;
    private String username;
    private String password;

    public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void sendBrandRenterEmail(Map context){
			
		HtmlEmail email = new HtmlEmail();
		// 设置邮件编码
		email.setCharset("UTF-8");
		// 设置邮件服务器
		email.setHostName(this.host);
		// 设置登录邮件服务器用户名和密码
		email.setAuthentication(this.username, this.password);

//			EmailAttachment attachment = new EmailAttachment();
//			// 要发送的附件
//			File file = new File("D:\\attachment.jar");
//			attachment.setPath(file.getPath());
//			attachment.setName(file.getName());
//			// 设置附件描述
//			attachment.setDescription("Attachment Description");
//			// 设置附件类型
//			attachment.setDisposition(EmailAttachment.ATTACHMENT);
//			// 添加附件
//			email.attach(attachment);
			
        List listLockMsg=(List) context.get("LISTLOCKMSG");
        Map temp;
        Map tempLockMsgMap;
        if (listLockMsg!=null){    
				
			try {
				// 添加收件人
				email.addTo(String.valueOf(context.get("TOEMAIL")));
				//email.addTo("michael@tacleasing.cn");
				
				// 设置发件人
				email.setFrom(this.username, "");
				// 设置邮件标题
				email.setSubject("解码");
				email.addCc("lune@tacleasing.cn");
				// 设置邮件抄送
				email.addCc("michael@tacleasing.cn");
		        StringBuilder textContext=new StringBuilder();
		        textContext.append("<html><head></head><body>");
		        textContext.append("您好！");
		        textContext.append("      我是【裕融租赁有限公司】 业管课，请帮以下客户解码。");
		        
		        textContext.append("<br>");
		        textContext.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">");
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
		        for(int i=0;i<listLockMsg.size();i++){
		        	tempLockMsgMap=(Map) listLockMsg.get(i);
			        textContext.append("<tr>");
			        textContext.append("<td align=\"center\">");
			        textContext.append(String.valueOf(tempLockMsgMap.get("CUST_NAME")));
			        textContext.append("</td>");
			        textContext.append("<td align=\"center\">");
			        textContext.append(String.valueOf(tempLockMsgMap.get("LINK_NAME")));
			        textContext.append("</td>");
			        textContext.append("<td align=\"center\">");
			        textContext.append(String.valueOf(tempLockMsgMap.get("LINK_MOBILE")));
			        textContext.append("</td>");
			        textContext.append("<td align=\"center\">");
			        textContext.append(String.valueOf(tempLockMsgMap.get("LINK_EMAIL")));
			        textContext.append("</td>");
			        textContext.append("<td align=\"center\">");
			        textContext.append(String.valueOf(tempLockMsgMap.get("MODEL_SPEC")));
			        textContext.append("</td>");
			        textContext.append("<td align=\"center\">");
			        textContext.append(String.valueOf(tempLockMsgMap.get("THING_NUMBER")));
			        textContext.append("</td>");
			        textContext.append("<td align=\"center\">");
			        textContext.append(String.valueOf(tempLockMsgMap.get("NEXT_LOCK_DATE")));
			        textContext.append("</td>");
			        textContext.append("</tr>");	
			        try {
						DataAccessor.getSession().update("financeDecomposeReport.updateLockMsgEmailSendFlag", tempLockMsgMap);
					} catch (SQLException e) {
						e.printStackTrace();
					}
		        }	
		        textContext.append("</table>");	        
		        textContext.append("</body></html>");
				// 设置邮件正文内容
				email.setHtmlMsg(textContext.toString());
				 //发送邮件
				email.send();
				} catch (EmailException e1) {
					// 增加方法发送邮件给Michael
					e1.printStackTrace();
				}
	        }
	    }
	    
	    public void sendCustLinkerEmail(Map context) {
			HtmlEmail email = new HtmlEmail();
			// 设置邮件编码
			email.setCharset("UTF-8");
			// 设置邮件服务器
			email.setHostName(this.host);
			// 设置登录邮件服务器用户名和密码
			email.setAuthentication(this.username, this.password);

//				EmailAttachment attachment = new EmailAttachment();
//				// 要发送的附件
//				File file = new File("D:\\attachment.jar");
//				attachment.setPath(file.getPath());
//				attachment.setName(file.getName());
//				// 设置附件描述
//				attachment.setDescription("Attachment Description");
//				// 设置附件类型
//				attachment.setDisposition(EmailAttachment.ATTACHMENT);
//				// 添加附件
//				email.attach(attachment);
	        List listLockMsg=(List) context.get("LISTLOCKMSG");
	        Map temp;
	        Map tempLockMsgMap;
	        if (listLockMsg!=null){ 
			try {
				// 添加收件人
				email.addTo(String.valueOf(context.get("TOEMAIL")));
				//email.addTo("michael@tacleasing.cn");
				// 设置发件人
				email.setFrom(this.username, "");
				// 设置邮件标题
				email.setSubject("解码");
				email.addCc("lune@tacleasing.cn");
				// 设置邮件抄送
				email.addCc("michael@tacleasing.cn");
				
		        StringBuilder textContext=new StringBuilder();
		        textContext.append("<html><head></head><body>");
		        textContext.append("您好！");
		        textContext.append("      我是【裕融租赁有限公司】 业管课，已通知供应商帮您解码，如有解码问题请致电:18913510191");
		        
		        textContext.append("<br>");
		        textContext.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">");
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
		        for(int i=0;i<listLockMsg.size();i++){
		        	tempLockMsgMap=(Map) listLockMsg.get(i);
			        textContext.append("<tr>");
			        textContext.append("<td align=\"center\">");
			        textContext.append(String.valueOf(tempLockMsgMap.get("BRAND")));
			        textContext.append("</td>");
			        textContext.append("<td align=\"center\">");
			        textContext.append(String.valueOf(tempLockMsgMap.get("RENTER_NAME")));
			        textContext.append("</td>");
			        textContext.append("<td align=\"center\">");
			        textContext.append(String.valueOf(tempLockMsgMap.get("RENTER_PHONE")));
			        textContext.append("</td>");
			        textContext.append("<td align=\"center\">");
			        textContext.append(String.valueOf(tempLockMsgMap.get("RENTER_EMAIL")));
			        textContext.append("</td>");
			        textContext.append("<td align=\"center\">");
			        textContext.append(String.valueOf(tempLockMsgMap.get("NEXT_LOCK_DATE")));
			        textContext.append("</td>");
			        textContext.append("</tr>");	
			        try {
						DataAccessor.getSession().update("financeDecomposeReport.updateLockMsgCustLinkerEmailSendFlag", tempLockMsgMap);
					} catch (SQLException e) {
						e.printStackTrace();
					}
		        }	
		        textContext.append("</table>");	        
		        textContext.append("</body></html>");
	        
				// 设置邮件正文内容
				email.setHtmlMsg(textContext.toString());
				 //发送邮件
				email.send();
			} catch (EmailException e1) {
				// 增加方法发送邮件给Michael
				e1.printStackTrace();
			}
			}
	    }
	  
	    
	    /**
	     * 带附件的HTML格式邮件
	     * @throws MessagingException 
	     */

		public void sendDirectLockEmail(){
			Map context= new HashMap();

			List listRenterEmail=new ArrayList();
			List listLockMsg=new ArrayList();
			List listLikerEmail=new ArrayList();
			Map tempMap;
			
			String strBrandMessage="你好！我是【裕融租赁有限公司】 业管课，已将需要解码之客户明细发送至您的邮箱请查收并及时处理！";
			String strCustMessage="你好！我是【裕融租赁有限公司】 业管课，已通知供应商帮您解码，如有解码问题请致电供应商，联系方式已发送至您的邮箱请及时查收！";
			System.out.println("--------  发送间接锁码的Email 开始------------------");
			try {
				DataAccessor.getSession().startTransaction() ;
				listRenterEmail=(List) DataAccessor.query("financeDecomposeReport.getDirectLockRenterEmail", context, DataAccessor.RS_TYPE.LIST);
				for(int i=0;i<listRenterEmail.size();i++){
					tempMap=(Map) listRenterEmail.get(i);
					listLockMsg=(List) DataAccessor.query("financeDecomposeReport.getDirectLockRenterEmailByEmail", tempMap, DataAccessor.RS_TYPE.LIST);
					context.put("TOEMAIL", tempMap.get("RENTER_EMAIL"));
					context.put("LISTLOCKMSG", listLockMsg);
					
					if (null!=tempMap.get("RENTER_EMAIL")&&!"".equals(tempMap.get("RENTER_EMAIL"))&&isVaildEmail(String.valueOf(tempMap.get("RENTER_EMAIL")))){
						this.sendBrandRenterEmail(context);
					}
					
					tempMap.put("MESSAGE", strBrandMessage);
					tempMap.put("MTEL", tempMap.get("RENTER_PHONE"));
					
					//给供应商发短信
					if (null!=tempMap.get("RENTER_PHONE")&&!"".equals(tempMap.get("RENTER_PHONE"))){
						DataAccessor.getSession().insert("financeDecomposeReport.createSendMsg", tempMap);
					}
				}
				
				listLikerEmail=(List) DataAccessor.query("financeDecomposeReport.getDirectLockLinkerEmail", context, DataAccessor.RS_TYPE.LIST);
				for(int i=0;i<listLikerEmail.size();i++){
					tempMap=(Map) listLikerEmail.get(i);
					listLockMsg=(List) DataAccessor.query("financeDecomposeReport.getDirectLockLinkerEmailByMobile", tempMap, DataAccessor.RS_TYPE.LIST);
					context.put("TOEMAIL", tempMap.get("LINK_EMAIL"));
					context.put("LISTLOCKMSG", listLockMsg);
					//this.testSetAttachMail(context);
					
					tempMap.put("MESSAGE", strCustMessage);
					tempMap.put("MTEL", tempMap.get("LINK_MOBILE"));
					
					if (null!=tempMap.get("LINK_EMAIL")&&!"".equals(tempMap.get("LINK_EMAIL"))&&isVaildEmail(String.valueOf(tempMap.get("LINK_EMAIL")))){
						this.sendCustLinkerEmail(context);
					}
					
					//给客户联系人发短息
					if (null!=tempMap.get("LINK_MOBILE")&&!"".equals(tempMap.get("LINK_MOBILE"))){
						DataAccessor.getSession().insert("financeDecomposeReport.createSendMsg", tempMap);
					}	
				}
				DataAccessor.getSession().commitTransaction() ;
				System.out.println("--------  发送间接锁码的Email 成功 ------------------");
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("-------- 发送间接锁码的Email 失败 ------------------");
			}finally{
				try {
					DataAccessor.getSession().endTransaction() ;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}		
		}
		
		//Add by Michael 2012 6-19 判断邮箱地址是否合法
		//[a-zA-Z0-9][a-zA-Z0-9._-]{2,16}[a-zA-Z0-9]@[a-zA-Z0-9]+.[a-zA-Z0-9]+ 
		//验证代码如下：
		public static boolean isVaildEmail(String email){
		     //String emailPattern="[a-zA-Z0-9][a-zA-Z0-9._-]{2,16}[a-zA-Z0-9]@[a-zA-Z0-9]+.[a-zA-Z0-9]+";
		     
		     String emailPattern="^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		     boolean result=Pattern.matches(emailPattern, email);
		     return result;
		}
}
