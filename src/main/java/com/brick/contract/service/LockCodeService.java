package com.brick.contract.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.exception.ServiceException;
import com.brick.base.service.BaseService;
import com.brick.base.util.LeaseUtil;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.common.sms.service.SmsUtilService;
import com.brick.util.StringUtils;

public class LockCodeService extends BaseService {
	Log logger = LogFactory.getLog(LockCodeService.class);
	private MailUtilService mailUtilService;
	private SmsUtilService sms;
	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}
	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}
	
	public SmsUtilService getSms() {
		return sms;
	}
	public void setSms(SmsUtilService sms) {
		this.sms = sms;
	}
	@Transactional
	public void doSendLockCodeByManualForDirect(Map<String, Object> info) throws Exception{
		sendLockCodeByDirect(info);
		updateManualLockCode(info);
	}
	
	@Transactional
	public void doSendLockCodeByManualForIndirect(Map<String, Object> info) throws Exception{
		sendLockCodeByIndirect(info);
		updateManualLockCode(info);
	}
	
	public void updateManualLockCode(Map<String, Object> info){
		update("lockManagement.updateManualLockCode", info);
	}
	
	public void sendLockCodeByDirect(Map<String, Object> info) throws Exception{
		if (StringUtils.isEmpty(info.get("RENTER_EMAIL"))) {
			throw new ServiceException("锁码维护人的邮箱为空。");
		}
		String strCustMessage = "你好！我是【裕融租赁有限公司】 业管课，机器密码已经发送到您的邮箱请及时查收！";
		MailSettingTo mailSettingTo = new MailSettingTo();
		mailSettingTo.setEmailSubject("解码");
		mailSettingTo.setEmailTo("lune@tacleasing.cn" + ";" + String.valueOf(info.get("RENTER_EMAIL") == null ? "" : info.get("RENTER_EMAIL")));
		mailSettingTo.setEmailCc("lune@tacleasing.cn" + ";" + String.valueOf(info.get("U_EMAIL")) + ";" + String.valueOf(info.get("UU_EMAIL"))
				+ ";" + LeaseUtil.getAssistantEmailByUserId(String.valueOf(info.get("USER_ID"))));
		String filePathString = "";
		StringBuilder textContext = new StringBuilder();
		textContext.append("<html><head></head><body>");
		textContext.append("您好！");
		textContext.append("      我是【裕融租赁有限公司】 业管课，如有解码问题请致电业管课：Tel:  18913510191 ");
		textContext.append("<br>");
		textContext.append("<br>");
		textContext.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">");
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
		textContext.append("<tr>");
		textContext.append("<td align=\"center\">");
		textContext.append(String.valueOf(info.get("CUST_NAME")));
		textContext.append("</td>");
		textContext.append("<td align=\"center\">");
		textContext.append(String.valueOf(info.get("THING_KIND")));
		textContext.append("</td>");
		textContext.append("<td align=\"center\">");
		textContext.append(String.valueOf(info.get("MODEL_SPEC")));
		textContext.append("</td>");
		textContext.append("<td align=\"center\">");
		textContext.append(String.valueOf(info.get("THING_NUMBER")));
		textContext.append("</td>");
		textContext.append("<td align=\"center\">");
		textContext.append(String.valueOf(info.get("PASSWORDS") == null ? "&nbsp;" : info.get("PASSWORDS")));
		textContext.append("</td>");
		textContext.append("<td align=\"center\"><a href='"+String.valueOf(info.get("FILE_NAME") == null ? "#" : info.get("FILE_NAME"))+"'>");
		textContext.append(String.valueOf(info.get("FILE_NAME") == null ? "&nbsp;" : info.get("FILE_NAME")));
		textContext.append("</a></td>");
		textContext.append("</tr>");
		textContext.append("</table>");
		textContext.append("</body></html>");
		if (null != info.get("PATH")) {
			filePathString = "\\"
					+ "\\"+LeaseUtil.getIPAddress()+"\\home\\filsoft\\financelease\\upload\\lockcode\\password"
					+ String.valueOf(info.get("PATH"));
		}
		mailSettingTo.setEmailAttachPath(filePathString);
		mailSettingTo.setEmailContent(textContext.toString());
		if (!mailUtilService.sendMail(mailSettingTo)) {
			throw new ServiceException("发送失败.");
		}
		if (!StringUtils.isEmpty(info.get("RENTER_PHONE"))) {
			sms.sendSmsBySystem(String.valueOf(info.get("RENTER_PHONE")), strCustMessage);
		} else {
			throw new ServiceException("锁码维护人的手机号为空。");
		}
	}
	
	private void sendToCust(Map<String, Object> info) throws SQLException{
		MailSettingTo mailSettingTo = new MailSettingTo();
		mailSettingTo.setEmailTo("lune@tacleasing.cn" + ";" + String.valueOf(info.get("LINK_EMAIL") == null ? "" : info.get("LINK_EMAIL")));
		mailSettingTo.setEmailCc("lune@tacleasing.cn" + ";" + String.valueOf(info.get("U_EMAIL")) + ";" + String.valueOf(info.get("UU_EMAIL"))
				+ ";" + LeaseUtil.getAssistantEmailByUserId(String.valueOf(info.get("USER_ID"))));
		mailSettingTo.setEmailSubject("解码");
		StringBuilder textContext = new StringBuilder();
		textContext.append("<html><head></head><body>");
		textContext.append("您好！");
		textContext.append("      我是【裕融租赁有限公司】 业管课，已通知供应商帮您解码，如有解码问题请致电 : 18913510191");
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
		textContext.append("<tr>");
		textContext.append("<td align=\"center\">");
		textContext.append(String.valueOf(info.get("BRAND")));
		textContext.append("</td>");
		textContext.append("<td align=\"center\">");
		textContext.append(String.valueOf(info.get("RENTER_NAME")));
		textContext.append("</td>");
		textContext.append("<td align=\"center\">");
		textContext.append(String.valueOf(info.get("RENTER_PHONE")));
		textContext.append("</td>");
		textContext.append("<td align=\"center\">");
		textContext.append(String.valueOf(info.get("RENTER_EMAIL")));
		textContext.append("</td>");
		textContext.append("<td align=\"center\">");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("query_date", info.get("NEXT_LOCK_DATE"));
		textContext.append(queryForObj("lockManagement.getWorkDayByAfterWithThis", paramMap));
		textContext.append("</td>");
		textContext.append("</tr>");
		textContext.append("</table>");
		textContext.append("</body></html>");
		mailSettingTo.setEmailContent(textContext.toString());
		if (!mailUtilService.sendMail(mailSettingTo)) {
			throw new ServiceException("发送失败.");
		}
	}
	
	private void sendToBrand(Map<String, Object> info) throws SQLException{
		MailSettingTo mailSettingTo = new MailSettingTo();
		mailSettingTo.setEmailTo("lune@tacleasing.cn" + ";" + String.valueOf(info.get("RENTER_EMAIL") == null ? "" : info.get("RENTER_EMAIL")));
		mailSettingTo.setEmailCc("lune@tacleasing.cn" + ";" + String.valueOf(info.get("U_EMAIL")) + ";" + String.valueOf(info.get("UU_EMAIL"))
				+ ";" + LeaseUtil.getAssistantEmailByUserId(String.valueOf(info.get("USER_ID"))));
		mailSettingTo.setEmailSubject("解码");
		StringBuilder textContext = new StringBuilder();
		textContext.append("<html><head></head><body>");
		textContext.append("您好！");
		textContext.append("      我是【裕融租赁有限公司】 业管课，如有解码问题请致电业管课： Tel:  18913510191");
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
		textContext.append("<tr>");
		textContext.append("<td align=\"center\">");
		textContext.append(String.valueOf(info.get("CUST_NAME")));
		textContext.append("</td>");
		textContext.append("<td align=\"center\">");
		textContext.append(String.valueOf(info.get("LINK_NAME")));
		textContext.append("</td>");
		textContext.append("<td align=\"center\">");
		textContext.append(String.valueOf(info.get("LINK_MOBILE")));
		textContext.append("</td>");
		textContext.append("<td align=\"center\">");
		textContext.append(String.valueOf(info.get("LINK_EMAIL")));
		textContext.append("</td>");
		textContext.append("<td align=\"center\">");
		textContext.append(String.valueOf(info.get("MODEL_SPEC")));
		textContext.append("</td>");
		textContext.append("<td align=\"center\">");
		textContext.append(String.valueOf(info.get("THING_NUMBER")));
		textContext.append("</td>");
		textContext.append("<td align=\"center\">");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("query_date", info.get("NEXT_LOCK_DATE"));
		textContext.append(queryForObj("lockManagement.getWorkDayByAfterWithThis", paramMap));
		textContext.append("</td>");
		textContext.append("</tr>");
		textContext.append("</table>");
		textContext.append("</body></html>");
		mailSettingTo.setEmailContent(textContext.toString());
		if (!mailUtilService.sendMail(mailSettingTo)) {
			throw new ServiceException("发送失败.");
		}
	}
	
	public void sendLockCodeByIndirect(Map<String, Object> info) throws SQLException{
		String strBrandMessage = "你好！我是【裕融租赁有限公司】 业管课，已将需要解码之客户明细发送至您的邮箱请查收并及时处理！";
		String strCustMessage = "你好！我是【裕融租赁有限公司】 业管课，已通知供应商帮您解码，如有解码问题请致电供应商，联系方式已发送至您的邮箱请及时查收！";
		if (StringUtils.isEmpty(info.get("RENTER_EMAIL"))) {
			throw new ServiceException("锁码维护人的邮箱为空。");
		}
		if (StringUtils.isEmpty(info.get("LINK_EMAIL"))) {
			throw new ServiceException("客户主要联系人的邮箱为空。");
		}
		sendToBrand(info);
		sendToCust(info);
		if (!StringUtils.isEmpty(info.get("RENTER_PHONE"))) {
			sms.sendSmsBySystem(String.valueOf(info.get("RENTER_PHONE")), strBrandMessage);
		} else {
			throw new ServiceException("锁码维护人的手机号为空。");
		}
		if (!StringUtils.isEmpty(info.get("LINK_MOBILE"))) {
			sms.sendSmsBySystem(String.valueOf(info.get("LINK_MOBILE")), strCustMessage);
		} else {
			throw new ServiceException("客户主要联系人的手机号为空。");
		}
	}
}
