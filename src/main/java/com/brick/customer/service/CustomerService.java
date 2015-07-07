package com.brick.customer.service;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.service.BaseService;
import com.brick.base.util.BirtReportEngine;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.util.DateUtil;
import com.ibatis.sqlmap.client.SqlMapClient;

public class CustomerService extends BaseService {
	
	Log logger = LogFactory.getLog(CustomerService.class);
	private BirtReportEngine birt;
	private MailUtilService mailUtilService;
	private static SqlMapClient sqlMap;
	
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
	public SqlMapClient getSqlMap() {
		return sqlMap;
	}
	public void setSqlMap(SqlMapClient sqlMap) {
		this.sqlMap = sqlMap;
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void doApplyVirtualAccount() throws Exception{
		doApplyStep1();
		//裕融
		doApplyStep2();
		//裕国,每周一发
		Calendar calendar = Calendar.getInstance();
		if(calendar.get(Calendar.DAY_OF_WEEK) == 2){
			doApplyStep3();
		}
	}
	
	/**
	 * 将设备类的客户的虚拟账号转移到待申请状态
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void doApplyStep1(){
		update("customer.updateExportFlagForSendBySystemForYR");
		//裕国周一发送
		Calendar calendar = Calendar.getInstance();
		if(calendar.get(Calendar.DAY_OF_WEEK) == 2){
			update("customer.updateExportFlagForSendBySystemForYG");
		}
	}
	
	/**
	 * 将待申请的虚拟账号做申请动作（满30个）
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void doApplyStep2() throws Exception{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("companyCode", 1);
		List<Map<String, Object>> list = (List<Map<String, Object>>) queryForList("customer.getCustForApplyVirtualAccount",param);
		if (list == null || list.size() < 30) {
			logger.info("虚拟账号未满30个，不做申请。");
			return;
		}
		String dateStr = DateUtil.dateToString(new Date(), "yyyyMMddHHmmSSS");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String fileName = null;
		try {
			fileName = DateUtil.dateToString(new Date(), "yyyy_MM_dd") + File.separator + "虚拟账号(裕融)" + dateStr + ".pdf";
			// 制作
			paramMap.put("companyCode", 1);
			birt.executeReport("customer/virtualAccountManager.rptdesign", fileName, paramMap);
		} catch (Exception e) {
			logger.error("导出虚拟账号出错-" + e.getMessage());
			throw new Exception("导出虚拟账号出错-" + e.getMessage());
		}
		
		// 发送Email
		try {
			// 发邮件
			MailSettingTo mailSettingTo = new MailSettingTo();
			mailSettingTo.setEmailAttachPath(birt.getOutputPath() + File.separator + fileName);
			//mailSettingTo.setEmailSubject("申请虚拟账号");
			mailUtilService.sendMail(150,mailSettingTo);
		} catch (Exception e) {
			logger.error("发送虚拟账号出错(投保单并没有发送)-" + e.getMessage());
			throw new Exception("发送虚拟账号出错(虚拟账号并没有发送)-" + e.getMessage());
		}
	}
	
	/**
	 * 将待申请的虚拟账号做申请动作（满30个）
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void doApplyStep3() throws Exception{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("companyCode", 2);
		List<Map<String, Object>> list = (List<Map<String, Object>>) queryForList("customer.getCustForApplyVirtualAccount",param);
		if (list == null || list.size() == 0) {
			logger.info("无虚拟账号，不做申请。");
			return;
		}
		String dateStr = DateUtil.dateToString(new Date(), "yyyyMMddHHmmSSS");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String fileName = null;
		try {
			fileName = DateUtil.dateToString(new Date(), "yyyy_MM_dd") + File.separator + "虚拟账号(裕国)" + dateStr + ".pdf";
			// 制作
			paramMap.put("companyCode", 2);
			birt.executeReport("customer/virtualAccountManager.rptdesign", fileName, paramMap);
		} catch (Exception e) {
			logger.error("导出虚拟账号出错-" + e.getMessage());
			throw new Exception("导出虚拟账号出错-" + e.getMessage());
		}
		
		// 发送Email
		try {
			// 发邮件
			MailSettingTo mailSettingTo = new MailSettingTo();
			mailSettingTo.setEmailAttachPath(birt.getOutputPath() + File.separator + fileName);
			//mailSettingTo.setEmailSubject("申请虚拟账号");
			mailUtilService.sendMail(151,mailSettingTo);
		} catch (Exception e) {
			logger.error("发送虚拟账号出错(投保单并没有发送)-" + e.getMessage());
			throw new Exception("发送虚拟账号出错(虚拟账号并没有发送)-" + e.getMessage());
		}
	}
	
	public static Map<String, Object> getApplyVirtualAccount(int companyCode){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> list = null;
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("companyCode", companyCode);
		try {
			list = (List<Map<String, Object>>) sqlMap.queryForList("customer.getCustForApplyVirtualAccount", param);
			if(companyCode == 1){
				for (Map<String, Object> map : list) {
					sqlMap.update("customer.applyVirtualAccount", map);
				}
			} else if(companyCode == 2){
				for (Map<String, Object> map : list) {
					sqlMap.update("customer.applyVirtualAccount1", map);
				}
			}
			resultMap.put("list", list);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultMap;
	}
	
	
}
