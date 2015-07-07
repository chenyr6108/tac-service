package com.brick.insurance.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.exception.ServiceException;
import com.brick.base.service.BaseService;
import com.brick.base.util.BirtReportEngine;
import com.brick.base.util.LeaseUtil;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.insurance.to.InsuCompanyTo;
import com.brick.insurance.to.InsuranceTo;
import com.brick.insurance.util.SendEmailForException;
import com.brick.service.entity.Context;
import com.brick.util.Constants;
import com.brick.util.DataUtil;
import com.brick.util.DateUtil;
import com.brick.util.NumberUtils;
import com.brick.util.StringUtils;
import com.ibatis.sqlmap.client.SqlMapClient;

public class InsuranceService extends BaseService {
	
	Log logger = LogFactory.getLog(InsuranceService.class);
	
	private BirtReportEngine birt;
	private MailUtilService mailUtilService;
	private SendEmailForException exceptionUtil;
	
	public SendEmailForException getExceptionUtil() {
		return exceptionUtil;
	}

	public void setExceptionUtil(SendEmailForException exceptionUtil) {
		this.exceptionUtil = exceptionUtil;
	}

	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}
	
	public BirtReportEngine getBirt() {
		return birt;
	}

	public void setBirt(BirtReportEngine birt) {
		this.birt = birt;
	}

	/**
	 * 创建保单
	 * （保险的入口）
	 * @param rect_id
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class)
	public void startInsurance(String rect_id, String create_by) throws Exception{
		if (StringUtils.isEmpty(rect_id) || "0".equals(rect_id)) {
			throw new Exception("案件未初审，投保失败。");
		}
		// 创建保单
		InsuranceTo insuTo = new InsuranceTo();
		insuTo.setRectId(rect_id);
		insuTo.setInsuStatus(0);
		insuTo.setCreate_by(create_by);
		Integer i = (Integer) this.queryForObj("insurance.checkInsurance", insuTo);
		if (i != null && i > 0) {
			logger.info("该合同已投保，无需投保");
			return;
		}
		String insuId = (String)this.insert("insurance.startInsurance", insuTo);
		if (insuId == null) {
			throw new Exception("创建保单不成功。");
		}
		insuTo.setInsuId(insuId);
		// 关联设备
		this.insert("insurance.insertInsu2Eqmt", insuTo);
	}
	
	/**
	 * 创建保单
	 * （保险的入口）<br>
	 * 外部事物
	 * @param rect_id
	 * @param create_by
	 * @param sqlMap
	 * @throws Exception
	 */
	public void startInsurance(String rect_id, String create_by, SqlMapClient sqlMap) throws Exception{
		if (StringUtils.isEmpty(rect_id) || "0".equals(rect_id)) {
			throw new Exception("案件未初审，投保失败。");
		}
		// 创建保单
		InsuranceTo insuTo = new InsuranceTo();
		insuTo.setRectId(rect_id);
		insuTo.setInsuStatus(0);
		insuTo.setCreate_by(create_by);
		Integer i = (Integer) sqlMap.queryForObject("insurance.checkInsurance", insuTo);
		if (i != null && i > 0) {
			logger.info("该合同已投保，无需投保");
			return;
		}
		
		//如果此合同号已经存在首拨款了  返回Y,不能投保 add by ShenQi 2012-12-27
		String flag=(String)sqlMap.queryForObject("insurance.checkIsExistsFirstPay", insuTo);
		if("Y".equalsIgnoreCase(flag)) {
			logger.info("该合同已投保，无需投保");
			return;
		}
		
		String insuId = (String)sqlMap.insert("insurance.startInsurance", insuTo);
		if (insuId == null) {
			throw new Exception("创建保单不成功。");
		}
		insuTo.setInsuId(insuId);
		// 关联设备
		sqlMap.insert("insurance.insertInsu2Eqmt", insuTo);
	}
	
	public Map<String, Object> getInsuDetail(String insuId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		return resultMap;
	}
	
	/**
	 * 保存投保设置
	 * @param context
	 */
	@Transactional(rollbackFor = Exception.class)
	public void doSaveInsuSetting(Context context) throws Exception {
		String[] insu2decp_tac = context.request.getParameterValues("insu2decp_1");
		String[] insu2decp_yuguo = context.request.getParameterValues("insu2decp_2");
		String start_date_tac = (String) context.contextMap.get("start_date_1");
		String start_date_yuguo = (String) context.contextMap.get("start_date_2");
		List<Map<String, Object>> insuCompany = null;
		String decpPercent = null;
		try {
			//删除旧设置
			delete("insurance.deleteOldSetting");
			
			/*//更新保险公司的额度比例
			insuCompany = (List<Map<String, Object>>) queryForList("insurance.getCompany", new HashMap());
			for (Map<String, Object> map : insuCompany) {
				decpPercent = (String) context.contextMap.get("price_percent_" + String.valueOf(map.get("INCP_ID")));
				map.put("price_percent", decpPercent);
				update("insurance.updateIncpPricePercent", map);
			}*/
			
			//保存新设置
			String[] afterSplit = null;
			String incp_id = null;
			String decp_id = null;
			String percent = null;
			Map<String, Object> param = null;
			for (String s : insu2decp_tac) {
				afterSplit = s.split("_");
				if (afterSplit != null && afterSplit.length == 2) {
					incp_id = afterSplit[0];
					decp_id = afterSplit[1];
					param = new HashMap<String, Object>();
					param.put("incp_id", incp_id);
					param.put("decp_id", decp_id);
					param.put("start_date", start_date_tac);
					percent = (String) context.contextMap.get("price_percent_" + incp_id + "_1");
					percent = StringUtils.isEmpty(percent) ? "0" : percent;
					param.put("percent", percent);
					param.put("companyCode", 1);
					insert("insurance.saveInsuSetting", param);
				}
			}
			for (String s : insu2decp_yuguo) {
				afterSplit = s.split("_");
				if (afterSplit != null && afterSplit.length == 2) {
					incp_id = afterSplit[0];
					decp_id = afterSplit[1];
					param = new HashMap<String, Object>();
					param.put("incp_id", incp_id);
					param.put("decp_id", decp_id);
					param.put("start_date", start_date_yuguo);
					percent = (String) context.contextMap.get("price_percent_" + incp_id + "_2");
					percent = StringUtils.isEmpty(percent) ? "0" : percent;
					param.put("percent", percent);
					param.put("companyCode", 2);
					insert("insurance.saveInsuSetting", param);
				}
			}
		} catch (ServiceException e) {
			throw e;
		}
		
	}
	
	/**
	 * 投保(大保单)
	 * 类型：Job.
	 * 发送当期大保单(包含新投保和续保)
	 */
	@Transactional
	public void doInsurance() throws Exception{
		Exception exception = null;
		logger.info("=================================新建投保.Start==================================");
		try {
			doInsuranceService();
		} catch (Exception e) {
			logger.info("=================================新建投保.Error==================================");
			exception = e;
		}
		logger.info("=================================新建投保.End==================================");
		logger.info("=================================续保.Start==================================");
		try {
			doRenewalInsuranceService();
		} catch (Exception e) {
			logger.info("=================================续保.Error==================================");
			exception = e;
		}
		logger.info("=================================续保.End==================================");
		logger.info("=================================发送大保单.Start==================================");
		try {
			doSendInsurance();
		} catch (Exception e) {
			logger.info("=================================发送大保单.Error==================================");
			exception = e;
		}
		logger.info("=================================发送大保单.End==================================");
		if (exception != null) {
			throw exception;
		}
	}
	
	
	/**
	 * 投保 <br>
	 * 类型：Job. <br>
	 * 发送“待投保”的投保单给保险公司
	 * 并更新保单的状态
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void doInsuranceService() throws Exception{
		try {
			List<InsuranceTo> infoList = null;
			// 查出合同信息，判断投保的保险公司
			infoList = (List<InsuranceTo>) this.queryForList("insurance.getContractInfoForSend");
			if (infoList == null || infoList.size() == 0) {
				logger.info("没有需要投保的信息。");
				return;
			}
			Integer incpId = null;
			// 投保的保单ID
			List<String> insuIdForUpdateStatus = new ArrayList<String>();
			// 根据保险公司整理保单（key=保险公司ID， value=保单ID(s)）
			Map<Integer, StringBuffer> incp_insus = new HashMap<Integer, StringBuffer>();
			StringBuffer insus = null;
			String incpGroupCode = null;
			for (InsuranceTo insuranceTo : infoList) {
				// 1.根据投保规则判断投保的保险公司
				try {
					if (insuranceTo.getContractType() == 3 || insuranceTo.getContractType() == 4 || insuranceTo.getContractType() == 6) {
						// 重车的全部“自行投保（4）”
						incpId = 4;
						incpGroupCode = getGroupCodeByIncpId(incpId);
						incpGroupCode = getFullStr(insuranceTo.getCompanyCode(), 2) + "-" + incpGroupCode;
						insuranceTo.setGroupCode(incpGroupCode);
						insuranceTo.setIncpId(incpId);
						// 更新保单的保险公司ID
						this.update("insurance.updateInsuCompany", insuranceTo);
						insus = incp_insus.get(incpId);
						if (insus == null) {
							insus = new StringBuffer();
						}
						insus.append(insuranceTo.getInsuId() + ",");	//	记录合同ID
						incp_insus.put(incpId, insus);
					} else {
						/*//裕国的案件把办事处改成2000，用来判断裕国的案件
						if (String.valueOf(Constants.COMPANY_CODE_YUGUO).equals(insuranceTo.getCompanyCode())) {
							insuranceTo.setDecpId(2000);
						}*/
						//根据投保设置获取保险公司ID
						incpId = getIncpByDecp(insuranceTo.getDecpId(), insuranceTo.getLease_rze(), insuranceTo.getCompanyCode());
						if (incpId == null) {
							throw new Exception("保险公司ID为Null。");
						}
						//得到保险公司后获取大保单编号
						incpGroupCode = getGroupCodeByIncpId(incpId);
						incpGroupCode = getFullStr(insuranceTo.getCompanyCode(), 2) + "-" + incpGroupCode;
						
						//更新保险公司ID和大保单编号
						insuranceTo.setGroupCode(incpGroupCode);
						insuranceTo.setIncpId(incpId);
						this.update("insurance.updateInsuCompany", insuranceTo);
						//更新合同方案表中的保险公司
						this.update("insurance.updateInsuCompanyForRect", insuranceTo);
						//更新支付表中的保险公司
						this.update("insurance.updateInsuCompanyForRecp", insuranceTo);
						insus = incp_insus.get(incpId);
						if (insus == null) {
							insus = new StringBuffer();
						}
					}
					// 记录需要更新状态的保单ID
					insuIdForUpdateStatus.add(insuranceTo.getInsuId());
				} catch (Exception e) {
					//发Mail通知改案件自动选择保险公司失败
					exceptionUtil.sendExceptionByEmail("案件号为：【" + insuranceTo.getCreditRuncode() + "】在自动选择保险公司时出错,投保未成功-" + e.getMessage(), 121);
					// 保单异常，不做更新
					insuIdForUpdateStatus.remove(insuranceTo.getInsuId());
					continue;
				}
				insus.append(insuranceTo.getInsuId() + ",");
				incp_insus.put(incpId, insus);
			}
			if (insuIdForUpdateStatus.size() == 0) {
				logger.info("没有需要投保的信息。");
				return;
			}
			
			// 2.计算并更新保单信息--金额，日期等
			// 为了避免更新这些信息时出现错误，导致保单状态更新失败，而重复投保，
			// 故更新这些信息与更新保单状态分开，最大化减少出错。
			InsuranceTo paramInsu = null;
			Double insuRate = 0D;
			for (String string : insuIdForUpdateStatus) {
				try {
					paramInsu = new InsuranceTo();
					paramInsu.setInsuId(string);
					paramInsu = (InsuranceTo) this.queryForObj("insurance.getInsuInfoForUpdate", paramInsu);
					if (1 == paramInsu.getInsuType()) {	//全年投保
						if (getOneYearLater(paramInsu.getInsuStartDate()).after(paramInsu.getInsuEndDate())) {
							insuRate = paramInsu.getInsuRate();
						} else {
							insuRate = paramInsu.getInsuRateMore();
						}
					} else if (2 == paramInsu.getInsuType()) {	//分年投保
						if (getOneYearLater(paramInsu.getInsuStartDate()).after(paramInsu.getInsuEndDate())) {
							paramInsu.setInsuEndDate(paramInsu.getInsuEndDate());
						} else {
							paramInsu.setInsuEndDate(getOneYearLater(paramInsu.getInsuStartDate()));
						}
						insuRate = paramInsu.getInsuRate();
					} else {
						throw new Exception("无效的投保方式，请在保险公司维护页面设置正确的投保方式。");
					}
					paramInsu.setInsuRate(insuRate);
					paramInsu.setInsuPrice(getInsuPrice(paramInsu));
					this.update("insurance.updateInsuInfo", paramInsu);
				} catch (Exception e) {
					throw new Exception("在计算保单终止日或者保险费用时出错-" + e.getMessage());
				}
			}
			
			// 3.保单整理--根据保险公司出保单规则进行保单整理（大保单概念）
			/*Map<String, Object> paraMap = null;
			for (Integer incp_id : incp_insus.keySet()) {
				insus = incp_insus.get(incp_id);
				//将所有本次投保的保单归到当期编号
				paraMap = new HashMap<String, Object>();
				paraMap.put("insus", insus.substring(0, insus.length() - 1));
				paraMap.put("group_code", getGroupCodeByIncpId(incp_id));
				update("insurance.updateGroupCode", paraMap);
			}*/
		} catch (Exception e) {
			logger.error(e);
			exceptionUtil.sendExceptionByEmail("创建小保单时出错--投保--" + e.getMessage(), 121);
			throw e;
		}
	}
	
	/**
	 * 发送当期所有保单
	 * @throws Exception 
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
	public void doSendInsurance() throws Exception{
		StringBuffer incpNameForLog = new StringBuffer();
		
		try {
			List<InsuCompanyTo> incps = this.getAllIncp();
			
			for (InsuCompanyTo incp : incps) {
				// 判断是否需要投递保单 （根据保险公司保单规则）
				if (needInsu(incp)) {
					try {
						doSendByCompanyCode(incp);
					} catch (Exception e) {
						//每个保险公司单独一个事务
						
						//记录发送失败的保险公司
						incpNameForLog.append(incp.getIncp_name() + ",");
					}
				}
			}
		} catch (Exception e) {
			logger.error(e);
			exceptionUtil.sendExceptionByEmail("大保单发送失败-请确认【保险公司：" + incpNameForLog.toString() + "】" + e.getMessage(), 121);
			throw e;
		}
	}
	
	/**
	 * 发送大保单
	 * @param incp
	 * @throws Exception
	 */
	public void doSendByCompanyCode(InsuCompanyTo incp) throws Exception{
		String insusForSend_old = null;
		String insusForSend_tac = null;
		String insusForSend_yuGuo = null;
		String groupCodeOld = incp.getIncp_insu_code();
		//获取需要投保的大保单
		//1. 旧的-裕融
		insusForSend_old = getInsuForSendByCompanyCode(incp);
		//2. 裕融
		incp.setIncp_insu_code("01-" + groupCodeOld);
		insusForSend_tac = getInsuForSendByCompanyCode(incp);
		//3. 裕国
		incp.setIncp_insu_code("02-" + groupCodeOld);
		insusForSend_yuGuo = getInsuForSendByCompanyCode(incp);
		//更新大保单编号
		updateInsuGroup(incp);
		//制作并发送
		if (!StringUtils.isEmpty(insusForSend_old)) {
			sendInsurance(insusForSend_old, incp, Constants.COMPANY_NAME);
		}
		if (!StringUtils.isEmpty(insusForSend_tac)) {
			sendInsurance(insusForSend_tac, incp, Constants.COMPANY_NAME);
		}
		if (!StringUtils.isEmpty(insusForSend_yuGuo)) {
			sendInsurance(insusForSend_yuGuo, incp, Constants.COMPANY_NAME_YUGUO);
		}
	}
	
	/**
	 * 获取对应公司别的大保单中的小保单数据
	 * @param incp
	 * @return
	 */
	private String getInsuForSendByCompanyCode(InsuCompanyTo incp){
		String insus = null;
		List<Integer> insuByGroup = null;
		InsuranceTo paramInsu = null;
		StringBuffer insusForSend = null;
		insusForSend = new StringBuffer();
		// 查询当期所有保单
		insuByGroup = (List<Integer>) queryForList("insurance.getInsuByGroupCode", incp);
		if (insuByGroup == null || insuByGroup.size() == 0) {
			return insus;
		}
		for (Integer integer : insuByGroup) {
			// 更新保单状态（改为“待录入”：10）
			paramInsu = new InsuranceTo();
			paramInsu.setInsuId(String.valueOf(integer));	
			paramInsu.setInsuStatus(10);
			this.update("insurance.updateInsuStatus", paramInsu);
			insusForSend.append(integer);
			insusForSend.append(",");
		}
		insus = insusForSend.substring(0, insusForSend.length() - 1);
		return insus;
	}
	
	/**
	 * 制作并发送投保单
	 * @throws Exception 
	 */
	private void sendInsurance(String insus, InsuCompanyTo incp, String companyName) throws Exception{
		String dateStr = DateUtil.dateToString(new Date(), "yyyyMMddHHmmSSS");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String fileName = null;
		try {
			// 制作各个保险公司的投保单
			paramMap.put("insu_id", insus);
			paramMap.put("incp_name" , incp.getIncp_name());
			paramMap.put("companyName", companyName);
			fileName = DateUtil.dateToString(new Date(), "yyyy_MM_dd") + File.separator + incp.getIncp_name() + "-" + companyName + dateStr + ".xls";
			logger.info("[" + incp.getIncp_name() + "]rect_id = " + insus);
			// 制作
			birt.executeReport("insuReport/insuranceExcelForEmail.rptdesign", fileName, paramMap);
		} catch (Exception e) {
			logger.error("制作投保单出错-" + e.getMessage());
			throw new Exception("制作投保单出错-" + e.getMessage());
		}
		
		// 发送Email
		try {
			// 发邮件
			MailSettingTo mailSettingTo = new MailSettingTo();
			mailSettingTo.setEmailAttachPath(birt.getOutputPath() + File.separator + fileName);
			mailSettingTo.setEmailTo(incp.getIncp_mail());
			mailSettingTo.setEmailSubject("投保");
			mailUtilService.sendMail(120, mailSettingTo);
		} catch (Exception e) {
			logger.error("发送投保单出错(投保单并没有发送)-" + e.getMessage());
			throw new Exception("发送投保单出错(投保单并没有发送)-" + e.getMessage());
		}
	}
	
	
	/**
	 * 续保 <br>
	 * 类型：Job. <br>
	 * 发送“待续保”的投保单给保险公司
	 * 并更新保单的状态
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
	public void doRenewalInsuranceService() throws Exception{
		try {
			List<InsuranceTo> infoList = null;
			// 查出需要续保的保单
			infoList = (List<InsuranceTo>) this.queryForList("insurance.getContractInfoForRenewal");
			if (infoList == null || infoList.size() == 0) {
				logger.info("没有需要投保的信息。");
				return;
			}
			Integer incpId = null;
			List<String> insuIdForUpdateStatus = new ArrayList<String>();
			// 合同的ID，用于制作各个保险公司投保单附件
			Map<Integer, StringBuffer> incp_insus = new HashMap<Integer, StringBuffer>();
			StringBuffer insus = null;
			String insuId = null;
			String companyCode = null;
			for (InsuranceTo insuranceTo : infoList) {
				try {
					//获取公司别
					companyCode = String.valueOf(LeaseUtil.getCompanyCodeByCreditId(String.valueOf(LeaseUtil.getCreditIdByRectId(insuranceTo.getRectId()))));
					//判断保险公司是否要续保
					if ("N".equals(insuranceTo.getIsRenewal())) {
						//不续保，则重新投保新的保险公司，并按照现有的投保规则
						insuranceTo.setIncpId(getIncpByDecp(insuranceTo.getDecpId(), insuranceTo.getLease_rze(), companyCode));
					}
					//获取大保单编号
					insuranceTo.setGroupCode(getFullStr(companyCode, 2) + "-" + getGroupCodeByIncpId(insuranceTo.getIncpId()));
					//创建续保保单
					insuranceTo.setInsuStatus(0);
					insuId = (String) this.insert("insurance.insertRenewalInsu", insuranceTo);
					insuranceTo.setInsuId(insuId);
					// 关联设备
					this.insert("insurance.insertInsu2Eqmt", insuranceTo);
					incpId = insuranceTo.getIncpId();
					insus = incp_insus.get(incpId);
					if (insus == null) {
						insus = new StringBuffer();
					}
					insus.append(insuId + ",");
					incp_insus.put(incpId, insus);
					// 记录需要更新状态的保单ID
					insuIdForUpdateStatus.add(insuranceTo.getInsuId());
				} catch (Exception e) {
					throw new Exception("续保时创建保单出错-" + e.getMessage());
				}
			}
			if (insuIdForUpdateStatus.size() == 0) {
				logger.info("没有需要投保的信息。");
				return;
			}
			// 填写保单内容（投保日期，保险额等）
			// 更新保单的信息：金额，日期等。为了避免更新这些信息时出现错误，导致保单状态更新失败，而重复投保，
			// 故更新这些信息与更新保单状态分开，最大化减少出错。
			InsuranceTo paramInsu = null;
			Double insuRate = 0D;
			for (String string : insuIdForUpdateStatus) {
				try {
					paramInsu = new InsuranceTo();
					paramInsu.setInsuId(string);
					paramInsu = (InsuranceTo) this.queryForObj("insurance.getInsuInfoForUpdateRenewal", paramInsu);
					if (1 == paramInsu.getInsuType()) {
						if (getOneYearLater(paramInsu.getInsuStartDate()).after(paramInsu.getInsuEndDate())) {
							insuRate = paramInsu.getInsuRate();
						} else {
							insuRate = paramInsu.getInsuRateMore();
						}
					} else if (2 == paramInsu.getInsuType()) {
						if (getOneYearLater(paramInsu.getInsuStartDate()).after(paramInsu.getInsuEndDate())) {
							paramInsu.setInsuEndDate(paramInsu.getInsuEndDate());
						} else {
							paramInsu.setInsuEndDate(getOneYearLater(paramInsu.getInsuStartDate()));
						}
						insuRate = paramInsu.getInsuRate();
					} else {
						throw new Exception("无效的投保方式，请在保险公司维护页面设置正确的投保方式。");
					}
					paramInsu.setInsuRate(insuRate);
					paramInsu.setInsuPrice(getInsuPrice(paramInsu));
					this.update("insurance.updateInsuInfo", paramInsu);
				} catch (Exception e) {
					throw new Exception("在计算保单终止日或者保险费用时出错-" + e.getMessage());
				}
			}
			
			// 3.保单整理--根据保险公司出保单规则进行保单整理（大保单概念）
			/*Map<String, Object> paraMap = null;
			for (Integer incp_id : incp_insus.keySet()) {
				insus = incp_insus.get(incp_id);
				//将所有本次投保的保单归到当期编号
				paraMap = new HashMap<String, Object>();
				paraMap.put("insus", insus.substring(0, insus.length() - 1));
				paraMap.put("group_code", getGroupCodeByIncpId(incp_id));
				update("insurance.updateGroupCode", paraMap);
			}*/
		} catch (Exception e) {
			logger.error(e);
			exceptionUtil.sendExceptionByEmail("创建小保单是出错--续保--" + e.getMessage(), 121);
			throw e;
		}
	}
	
	/**
	 * 制作并发送续保保单
	 * @throws Exception 
	 */
	private void sendSurrender(String insus, InsuCompanyTo incp) throws Exception{
		String dateStr = DateUtil.dateToString(new Date(), "yyyyMMddHHmmSSS");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String fileName = null;
		try {
			// 制作各个保险公司的投保单
			paramMap.put("insu_id", insus);
			paramMap.put("incp_name" , incp.getIncp_name());
			fileName = DateUtil.dateToString(new Date(), "yyyy_MM_dd") + File.separator + incp.getIncp_name() + dateStr + ".xls";
			logger.info("[" + incp.getIncp_name() + "]rect_id = " + insus.substring(0, insus.length() - 1));
			// 制作
			birt.executeReport("insuReport/insuranceExcelForEmail.rptdesign", fileName, paramMap);
		} catch (Exception e) {
			throw new Exception("制作投保单出错-" + e.getMessage());
		}
		// 发送Email
		try {
			MailSettingTo mailSettingTo = new MailSettingTo();
			mailSettingTo.setEmailAttachPath(birt.getOutputPath() + File.separator + fileName);
			mailSettingTo.setEmailTo(incp.getIncp_mail());
			mailSettingTo.setEmailSubject("续保");
			mailUtilService.sendMail(120, mailSettingTo);
		} catch (Exception e) {
			throw new Exception("发送投保单出错(投保单并没有发送)-" + e.getMessage());
		}
	}
	
	
	/**
	 * 提前结清待退保<br>
	 * 40 = 结清待退保
	 * @throws Exception 
	 */
	@Transactional(rollbackFor = Exception.class)	
	public void doWaitingForSurrender() throws Exception{
		String fileName = null;
		Map<String, Object> paramMap = null;
		Map<Object, Object> dataMap = new HashMap<Object, Object>();
		List<String> attachNames = new ArrayList<String>();
		try {
			logger.info("=================================退保开始==================================");
			List<Map<String, Object>> dataList = (List<Map<String, Object>>) this.queryForList("insurance.getWaitingForSurrender");
			StringBuffer sb = null;
			for (Map<String, Object> map : dataList) {
				//sb.append(string + ",");
				sb = (StringBuffer) dataMap.get(map.get("INCP_ID"));
				if (sb == null) {
					sb = new StringBuffer();
				}
				sb.append(map.get("INCU_ID") + ",");
				dataMap.put(map.get("INCP_ID"), sb);
			}
			//创建退保单
			for (Map<String, Object> map : dataList) {
				//1. 查询保单信息，计算退保费
				insert("insurance.insertInfoForSurrender", map);
			}
			StringBuffer insuIds = null;
			String incp_name = null;
			try {
				for (Object incp : dataMap.keySet()) {
					insuIds = (StringBuffer) dataMap.get(incp);
					incp_name = getIncpNameById((Integer) incp);
					String dateStr = DateUtil.dateToString(new Date(), "yyyyMMddHHmmSSS");
					fileName = DateUtil.dateToString(new Date(), "yyyy_MM_dd") + File.separator + incp_name +  "提前结清待退保" + dateStr + ".xls";
					paramMap = new HashMap<String, Object>();
					paramMap.put("insu_ids", insuIds.substring(0, insuIds.length() - 1));
					birt.executeReport("insuReport/waitingForSurrender.rptdesign", fileName, paramMap);
					attachNames.add(fileName + "," + incp);
				}
			} catch (Exception e) {
				throw new Exception("结清待退保附件生成出错" + e.getMessage());
			}
			// 发送Email
			String[] data = null;
			String attachName = null;
			String incpIdStr = null;
			InsuranceTo insuToForEmail = null;
			String incpEmail = null;
			try {
				for (String str : attachNames) {
					data = str.split(",");
					attachName = data[0];	//附件名
					incpIdStr = data[1];	//保险公司ID
					// 查询保险公司的Email
					insuToForEmail = new InsuranceTo();
					insuToForEmail.setIncpId(Integer.valueOf(incpIdStr));
					incpEmail = (String) this.queryForObj("insurance.getIncpEmail", insuToForEmail);
					// 发邮件
					MailSettingTo mailSettingTo = new MailSettingTo();
					mailSettingTo.setEmailAttachPath(birt.getOutputPath() + File.separator + attachName);
					mailSettingTo.setEmailTo(incpEmail);
					mailSettingTo.setEmailSubject("结清待退保");
					mailUtilService.sendMail(120, mailSettingTo);
				}
			} catch (Exception e) {
				throw new Exception("退保单发送失败-" + e.getMessage());
			}
			InsuranceTo insu = null;
			try {
				for (Map<String, Object> m : dataList) {
					insu = new InsuranceTo();
					insu.setInsuStatus(40);
					insu.setInsuId(String.valueOf(m.get("INCU_ID")));
					update("insurance.updateInsuStatus", insu);
				}
			} catch (Exception e) {
				throw new Exception("退保单发送成功，但是状态更新失败，请及时处理-" + e.getMessage());
			}
			logger.info("=================================退保成功==================================");
		} catch (Exception e) {
			logger.info("=================================退保失败==================================");
			logger.error(e);
			exceptionUtil.sendExceptionByEmail("结清待退保失败-" + e.getMessage(), 121);
			throw e;
		}
	}
	
	/**
	 * 保单追踪
	 * @throws Exception
	 */
	public void doTrace() throws Exception{
		List<Map<String, Object>> resultList = null;
		String incp = null;
		Map<String, List<Map<String, Object>>> dataMap = new HashMap<String, List<Map<String,Object>>>();
		List<Map<String, Object>> dataList = null;
		String atta = null;
		String mail = null;
		try {
			//保单
			resultList = (List<Map<String, Object>>) this.queryForList("insurance.getDataForTrace");
			for (Map<String, Object> map : resultList) {
				incp = String.valueOf(map.get("SHORT_NAME"));
				dataList = dataMap.get(incp);
				if (dataList == null) {
					dataList = new ArrayList<Map<String,Object>>();
				}
				dataList.add(map);
				dataMap.put(incp, dataList);
			}
			for (String key : dataMap.keySet()) {
				atta = getFileForTrace("保单未回", key, dataMap.get(key));
				if (atta == null) {
					throw new Exception("生成附件出错！");
				}
				mail = (String) dataMap.get(key).get(0).get("INCP_EMAIL");
				if (mail == null) {
					throw new Exception("找不到保险公司【" + key + "】的Email");
				}
				MailSettingTo mailSettingTo = new MailSettingTo();
				mailSettingTo.setEmailAttachPath(atta);
				mailSettingTo.setEmailTo(mail);
				mailUtilService.sendMail(122, mailSettingTo);
			}
			
			//批单
			resultList = null;
			incp = null;
			dataMap = new HashMap<String, List<Map<String,Object>>>();
			dataList = null;
			atta = null;
			mail = null;
			resultList = (List<Map<String, Object>>) this.queryForList("insurance.getDataForTrace2");
			for (Map<String, Object> map : resultList) {
				incp = String.valueOf(map.get("SHORT_NAME"));
				dataList = dataMap.get(incp);
				if (dataList == null) {
					dataList = new ArrayList<Map<String,Object>>();
				}
				dataList.add(map);
				dataMap.put(incp, dataList);
			}
			for (String key : dataMap.keySet()) {
				atta = getFileForTrace("批单未回", key, dataMap.get(key));
				if (atta == null) {
					throw new Exception("生成附件出错！");
				}
				mail = (String) dataMap.get(key).get(0).get("INCP_EMAIL");
				if (mail == null) {
					throw new Exception("找不到保险公司【" + key + "】的Email");
				}
				MailSettingTo mailSettingTo = new MailSettingTo();
				mailSettingTo.setEmailAttachPath(atta);
				mailSettingTo.setEmailTo(mail);
				mailUtilService.sendMail(123, mailSettingTo);
			}
		} catch (ServiceException e) {
			logger.error(e);
			throw e;
		}
	}
	
	/**
	 * 生成追踪用的附件
	 * @param msg
	 * @param incp
	 * @param dataList
	 * @return
	 * @throws Exception
	 */
	public String getFileForTrace(String msg, String incp, List<Map<String, Object>> dataList) throws Exception{
		if (dataList == null || dataList.size() == 0) {
			return null;
		}
		String filePath = "\\\\"+LeaseUtil.getIPAddress()+"\\home\\filsoft\\financelease\\insuMailAtta" + File.separator + 
				DateUtil.dateToString(new Date(), "yyyy_MM_dd");
		String fileName = incp + "_" + msg + DateUtil.dateToString(new Date(), "HHmmSSS") +".xls";
		File f = null;
		OutputStream out = null;
		HSSFWorkbook wb = null;
		try {
			File path = new File(filePath);
			path.mkdirs();
			f = new File(path, fileName);
			wb = new HSSFWorkbook();
			//sheet1
			HSSFSheet sheet = wb.createSheet("sheet1");
			Map<String, Object> map = dataList.get(0);
			Set<String> keys = map.keySet();
			HSSFRow row = null;
			HSSFCell cell = null;
			HSSFCellStyle cellStyle = wb.createCellStyle();
			cellStyle.setBorderBottom((short)1);
			cellStyle.setBorderTop((short)1);
			cellStyle.setBorderLeft((short)1);
			cellStyle.setBorderRight((short)1);
			//表头
			row = sheet.createRow(0);
			List<String> head = new ArrayList<String>();
			int index = 0;
			for (String s : keys) {
				if ("INCP_EMAIL".equals(s)) {
					continue;
				}
				if ("SHORT_NAME".equals(s)) {
					continue;
				}
				cell = row.createCell(index);
				cell.setCellStyle(cellStyle);
				cell.setCellValue(s);
				head.add(s);
				index ++;
			}
			for (int i = 1; i <= dataList.size(); i++) {
				row = sheet.createRow(i);
				index = 0;
				for (String s : head) {
					cell = row.createCell(index);
					cell.setCellStyle(cellStyle);
					Object o = dataList.get(i - 1).get(s);
					if (o == null) {
						cell.setCellType(cell.CELL_TYPE_STRING);
						cell.setCellValue("");
					} else if (o instanceof String) {
						String value = (String) o;
						cell.setCellType(cell.CELL_TYPE_STRING);
						cell.setCellValue(value);
					} else if (o instanceof Double) {
						Double value = (Double) o;
						cell.setCellType(cell.CELL_TYPE_NUMERIC);
						cell.setCellValue(NumberUtils.retain2rounded(value));
					} else if (o instanceof java.sql.Date) {
						java.sql.Date value = (java.sql.Date) o;
						cell.setCellType(cell.CELL_TYPE_STRING);
						cell.setCellValue(DateUtil.dateToStr(value));
					} else if (o instanceof Timestamp) {
						Timestamp value = (Timestamp) o;
						cell.setCellType(cell.CELL_TYPE_STRING);
						cell.setCellValue(DateUtil.dateToStr(value));
					} else if (o instanceof BigDecimal) {
						BigDecimal value = (BigDecimal) o;
						cell.setCellType(cell.CELL_TYPE_NUMERIC);
						cell.setCellValue(NumberUtils.retain2rounded(value.doubleValue()));
					} else {
						String value = o.toString();
						cell.setCellType(cell.CELL_TYPE_STRING);
						cell.setCellValue(value);
					}
					index ++;
				}
			}
			out = new FileOutputStream(f);
			wb.write(out);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}
		return f.getPath();
	}
	
	public void doSurrender(){
		
	}
	
	/**
	 * 保险公司ID转换成保险公司名称
	 * @param incp
	 * @return
	 * @throws ServiceException 
	 */
	private String getIncpNameById(Integer incp) throws ServiceException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("incp_id", incp);
		String incpName = null;
		try {
			incpName = (String) queryForObj("insurance.getIncpNameById", paramMap);
		} catch (ServiceException e) {
			throw e;
		}
		return incpName;
	}

	/**
	 *根据部门ID计算出要投保的保险公司
	 * @param decpId
	 * @return
	 * @throws ServiceException 
	 */
	public Integer getIncpByDecp(Integer decpId, Double lease_rze, String companyCode) throws Exception {
		Integer incp_id = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("decpId", decpId);
		paramMap.put("companyCode", companyCode);
		List<Integer> incps = null;
		Double insuPriceTotal = null;
		Map<String, Object> percentMap = null;
		List<Map<String, Object>> percentList = new ArrayList<Map<String,Object>>();
		Double currentPrice = null;
		Double currentPercent = null;
		Double targetPercent = null;
		Double maxDiff = 0D;
		try {
			//查出该部门可以投保的保险公司
			incps = (List<Integer>) queryForList("insurance.getIncpByDecp", paramMap);
			if (incps == null || incps.size() == 0) {
				throw new Exception("没有找到要投保的保险公司，请确认投保设置是否正确。");
			}
			if (incps.size() == 1) {
				return incps.get(0);
			}
			//查出总保险额度
			insuPriceTotal = (Double) queryForObj("insurance.getInsuPriceForAll", paramMap);
			System.out.println("**********insuPriceTotal=" + insuPriceTotal);
			paramMap = new HashMap<String, Object>();
			paramMap.put("companyCode", companyCode);
			//计算各个保险公司的占比差
			for (Integer integer : incps) {
				paramMap.put("incp_id", integer);
				System.out.println("**********==>>>incp_id=" + integer);
				//当前额度
				currentPrice = (Double) queryForObj("insurance.getInsuPrice", paramMap);
				System.out.println("**********currentPrice=" + currentPrice);
				//计算当前占比
				currentPercent = currentPrice / insuPriceTotal * 100;
				System.out.println("**********currentPercent=" + currentPercent);
				//目标占比
				targetPercent = (Double) queryForObj("insurance.getTargetPercent", paramMap);
				System.out.println("**********targetPercent=" + targetPercent);
				percentMap = new HashMap<String, Object>();
				percentMap.put("incp_id", integer);
				//计算占比差
				percentMap.put("percentDiff", targetPercent - currentPercent);
				percentList.add(percentMap);
			}
			/*if (percentList.size() == 0) {
				return null;
			} else if (percentList.size() == 1) {
				return (Integer) percentList.get(0).get("incp_id");
			} else {*/
				//取最大占比差的
				for (Map<String, Object> map : percentList) {
					if ((Double)map.get("percentDiff") > maxDiff) {
						maxDiff = (Double)map.get("percentDiff");
					}
				}
				//获取最大占比差的保险公司ID
				for (Map<String, Object> map : percentList) {
					if ((Double)map.get("percentDiff") == maxDiff) {
						incp_id = (Integer) map.get("incp_id");
					}
				}
			/*}*/
		} catch (Exception e) {
			throw e;
		}
		System.out.println("**********incp_id=" + incp_id);
		return incp_id;
	}

	/**
	 * 计算一年之后的日期
	 * @param d
	 * @return
	 */
	private Date getOneYearLater(Date d){
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.add(Calendar.YEAR, 1);
		c.add(Calendar.DATE, -1);
		return c.getTime();
	}
	
	private int getDayDiff(Date from, Date to){
		return Integer.parseInt(String.valueOf((to.getTime() - from.getTime()) / 1000 / 60 / 60 / 24));
	}
	
	private double getInsuPrice(InsuranceTo insuranceTo){
		double insuPrice = 0;
		if (insuranceTo.getInsuAmount() == null || insuranceTo.getInsuAmount() == 0) {
			return insuPrice;
		}
		if (insuranceTo.getInsuStartDate() == null || insuranceTo.getInsuEndDate() == null) {
			return insuPrice;
		}
		if (insuranceTo.getInsuRate() == null || insuranceTo.getInsuRate() == 0) {
			return insuPrice;
		}
		System.out.println("from " + DateUtil.dateToFullStr(insuranceTo.getInsuStartDate()) + " to " + DateUtil.dateToFullStr(insuranceTo.getInsuEndDate()));
		int dayDiff = getDayDiff(insuranceTo.getInsuStartDate(), insuranceTo.getInsuEndDate()) + 1;
		System.out.println("dayDiff:============>>" + dayDiff);
		insuPrice = (insuranceTo.getInsuAmount() * (insuranceTo.getInsuRate() / 1000)) / 365 * dayDiff;
		return insuPrice;
	}

	private void updateInsuGroup(InsuCompanyTo incp) throws Exception{
		try {
			String newCode = null;
			String incpCode = incp.getIncp_code();
			String oldCode = incp.getIncp_insu_code();
			if (StringUtils.isEmpty(oldCode)) {
				newCode = incpCode + DateUtil.getCurrentYear() + "0001";
			} else {
				if (oldCode.contains("-")) {
					oldCode = oldCode.split("-")[1];
				}
				String yearcode = oldCode.substring(2, 6);
				String serialCode = oldCode.substring(6, 10);
				if (incpCode == null) {
					incpCode = oldCode.substring(0, 2);
				} else if (incpCode.length() < 2) {
					incpCode = oldCode.substring(0, 2);
				} else {
					incpCode = incpCode.substring(0, 2);
				}
				if (DateUtil.getCurrentYear().equals(yearcode)) {
					newCode = incpCode + yearcode + getFullStr(String.valueOf(Integer.parseInt(serialCode) + 1), 4);
				} else {
					newCode = incpCode + DateUtil.getCurrentYear() + "0001";
				}
			}
			incp.setIncp_insu_code(newCode);
			update("insurance.updateInsuGroupAtIncp", incp);
		} catch (ServiceException e) {
			throw new Exception("保单期数编号更新失败。");
		}
	}
	
	private String getFullStr(String code, int length){
		boolean flag = true;
		while(flag){
			if (code.length() < length) {
				code = "0" + code;
			} else {
				flag = false;
			}
		}
		return code;
	}
	
	private String getGroupCodeByIncpId(Integer incp_id) throws Exception{
		InsuCompanyTo incp = new InsuCompanyTo();
		incp.setIncp_id(incp_id);
		incp = (InsuCompanyTo) queryForObj("insurance.getIncpById", incp);
		String insuGroupCode = null;
		if (StringUtils.isEmpty(incp.getIncp_insu_code())) {
			insuGroupCode = incp.getIncp_code() + DateUtil.getCurrentYear() + "0001";
			incp.setIncp_insu_code(insuGroupCode);
			update("insurance.initIncpGroupCode", incp);
		} else {
			insuGroupCode = incp.getIncp_insu_code();
		}
		return insuGroupCode;
	}
	
	private boolean needInsu(InsuCompanyTo incp) throws ParseException{
		boolean flag = false;
		String insu_way = incp.getInsu_way();
		String insu_way_code = incp.getInsu_way_code();
		Calendar c = Calendar.getInstance(Locale.CHINESE);
		c.setTime(new Date());
		String dayOfWeek = getUpCase(c.get(Calendar.DAY_OF_WEEK) - 1);
		String dayOfMonth = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
		if ("天".equals(insu_way)) {
			flag = true;
		} else if ("周".equals(insu_way)) {
			if (dayOfWeek.equals(insu_way_code)) {
				flag = true;
			}
		} else if ("月".equals(insu_way)) {
			if (dayOfMonth.equals(insu_way_code)) {
				flag = true;
			}
		} else if ("0".equals(insu_way)) {
			flag = true;
		}
		return flag;
	}
	
	private String getUpCase(int code){
		String upCase = null;
		if (1 == code) {
			upCase = "一";
		} else if (2 == code) {
			upCase = "二";
		} else if (3 == code) {
			upCase = "三";
		} else if (4 == code) {
			upCase = "四";
		} else if (5 == code) {
			upCase = "五";
		} else if (6 == code) {
			upCase = "六";
		} else if (0 == code) {
			upCase = "日";
		}
		return upCase;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void doAddClaimInfo(Context context) throws Exception {
		MailSettingTo mailInfo = new MailSettingTo();
		String insu_code = (String) context.contextMap.get("insu_code");
		String eqmt_name = (String) context.contextMap.get("eqmt_name");
		insert("satisfaction.createSatisfactionByIncu", context.contextMap);
		String mailContent = "保单号为【" + (insu_code == null ? "" : insu_code) + "】，" +
				"设备名称为【" + (eqmt_name == null ? "未填写" : eqmt_name) + "】，" +
				"申请理赔，转交法务。";
		mailInfo.setEmailContent(mailContent);
		mailUtilService.sendMail(101, mailInfo);
	}
	
	public void insuTerminateDaily(){
		update("job.insuTerminateDaily");
	}
	
	private String getFullStr(String str, int length, String c){
		for (; str.length() < length;) {
			str = c + str;
		}
		return str;
	}
	
	
}
