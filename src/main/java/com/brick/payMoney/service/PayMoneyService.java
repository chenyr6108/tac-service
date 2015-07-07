package com.brick.payMoney.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.brick.service.entity.Context;
import com.brick.util.Constants;

public class PayMoneyService extends BaseService {
	
	Log logger = LogFactory.getLog(PayMoneyService.class);
	
	private final String MONEY_TYPE_EQMT = "0";
	
	private MailUtilService mailUtilService;
	
	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}
	
	private SmsUtilService sms;
	
	public SmsUtilService getSms() {
		return sms;
	}

	public void setSms(SmsUtilService sms) {
		this.sms = sms;
	}

	@Transactional
	public void doPayMoney(Context context){
		String userId = String.valueOf(context.contextMap.get("s_employeeId"));
		String ip = String.valueOf(context.contextMap.get("IP"));
		String creditId = String.valueOf(context.contextMap.get("CREDIT_ID") == null ? "0" : context.contextMap.get("CREDIT_ID"));
		String moneyType = String.valueOf(context.contextMap.get("Num"));
		String payId = 	String.valueOf(context.contextMap.get("ID"));
		try {
			doPayMoneyPass(payId,creditId);
			if (MONEY_TYPE_EQMT.equals(moneyType)) {
				updateLeaseInfo(creditId);
				updatePayOrder(payId, creditId);
				doSendMailAndSmsForEqmtMoney(creditId);
			}
			this.addBusinessLog(creditId, "付款管理", "通过", "付款通过", userId, ip);
		} catch (Exception e) {
			logger.error(e);
			throw new ServiceException(e);
		}
	}
	
	public void doPayMoneyPass(String payId,String creditId) throws Exception{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			paramMap.put("ID", payId);
			String taxPlanCode  = LeaseUtil.getTaxPlanCodeByCreditId(creditId);
			if("5".equals(taxPlanCode)){//乘用车委贷税费方案
				update("payMoney.payMoneyPassForCar", paramMap);				
			}else{
				update("payMoney.payMoneyPass", paramMap);
			}		
		} catch (Exception e) {
			logger.error("付款管理-通过-更新付款通过状态失败");
			throw e;
		}
	}
	
	public void updateLeaseInfo(String creditId) throws Exception{
		java.util.Date fDate = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			fDate = LeaseUtil.getFinancecontractDate(creditId);
			if (fDate == null) {	//首拨款
				paramMap.put("CREDIT_ID", creditId);
				//更新首拨款日
				String taxPlanCode = LeaseUtil.getTaxPlanCodeByCreditId(creditId);
				if(!"5".equals(taxPlanCode)){
					update("payMoney.updateCreditFinanceContractDate", paramMap) ;
				}
				String orgSensorId = LeaseUtil.getSensorIdByCreditId(creditId);
				String orgUpId = LeaseUtil.getUpUserByUserId(orgSensorId);
				String orgDecpId = LeaseUtil.getDecpIdByCreditId(creditId);
				paramMap.put("orgSensorId", orgSensorId);
				paramMap.put("orgUpId", orgUpId);
				paramMap.put("orgDecpId", orgDecpId);
				//资信表更新
				update("payMoney.updateCreditForOrg", paramMap);
				//合同表更新
				update("payMoney.updateContractForOrg", paramMap);
			}
		} catch (Exception e) {
			logger.error("付款管理-通过-更新首拨款日或者原始经办人失败");
			throw e;
		}
	}
	
	public void doSendMailAndSmsForEqmtMoney(String creditId) throws Exception{
		try {
			String leascode = LeaseUtil.getLeaseCodeByCreditId(creditId);
			String custName = LeaseUtil.getCustNameByCreditId(creditId);
			String msg = leascode + "，" + custName + " - " + "设备款" + "已拨款--管理部";
			//String decpId = LeaseUtil.getDecpIdByCreditId(creditId);
			//String classLeader = LeaseUtil.getClassLeaderByDecp(decpId);
			String classLeader = LeaseUtil.getClassLeaderByCreditId(creditId);
			String classLeaderEmail = null;
			String classLeaderMobile = null;
			if (classLeader != null) {
				classLeaderEmail = LeaseUtil.getEmailByUserId(classLeader);
				classLeaderMobile = LeaseUtil.getMobileByUserId(classLeader);
			}
			String sensor = LeaseUtil.getSensorIdByCreditId(creditId);
			String sensorEmail = LeaseUtil.getEmailByUserId(sensor);
			String sensorMobile = LeaseUtil.getMobileByUserId(sensor);
			String upUser = LeaseUtil.getUpUserByUserId(sensor);
			String upUserEmail = LeaseUtil.getEmailByUserId(upUser);
			String upUserMobile = LeaseUtil.getMobileByUserId(upUser);
			//邮件
			String mailTo = sensorEmail + (classLeader == null ? "" : (";" + classLeaderEmail));
			String mailCc = Constants.GM_MAIL + (upUserEmail == null ? "" : (";" + upUserEmail));
			MailSettingTo mailSettingTo=new MailSettingTo();
			mailSettingTo.setEmailFrom("tacfinance_service@tacleasing.cn");
			mailSettingTo.setEmailTo(mailTo);
			mailSettingTo.setEmailCc(mailCc);
			mailSettingTo.setEmailSubject("付款提醒");
			mailSettingTo.setEmailContent(msg);
			mailSettingTo.setCreateBy(Constants.SYSTEM_ID);
			this.mailUtilService.sendMail(mailSettingTo);
			//短信
			List<String> mobileList = new ArrayList<String>();
			mobileList.add(sensorMobile);
			if (classLeaderMobile != null) {
				mobileList.add(classLeaderMobile);
			}
			if (upUserMobile != null) {
				mobileList.add(upUserMobile);
			}
			sms.sendSms(mobileList, msg, Constants.SYSTEM_ID);
		} catch (Exception e) {
			logger.error("付款管理-通过-发送email或者短信失败");
			throw e;
		}
	}
	
	public void updatePayOrder(String payId, String creditId){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("CREDIT_ID", creditId);
		//如果首次拨款 resultList.size=1
		try {
			List<Map<String,Object>> resultList = (List<Map<String, Object>>) queryForList("payMoney.countPayOrder", paramMap);
			paramMap.put("NUMBER", resultList.size());
			paramMap.put("ID", payId);
			update("payMoney.updatePayOrder", paramMap);
		} catch (ServiceException e) {
			logger.error("付款管理-通过-更新拨款次数失败");
			throw e;
		}
	}
	
	
	
	
	
	
}
