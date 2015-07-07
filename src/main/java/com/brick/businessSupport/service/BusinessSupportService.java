package com.brick.businessSupport.service;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.struts2.components.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.exception.ServiceException;
import com.brick.base.service.BaseService;
import com.brick.base.util.BirtReportEngine;
import com.brick.base.util.LeaseUtil;
import com.brick.businessSupport.dao.BusinessSupportDAO;
import com.brick.businessSupport.to.SqlTO;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DataUtil;
import com.brick.util.StringUtils;
import com.brick.util.web.HTMLUtil;
import com.ibatis.sqlmap.client.SqlMapClient;

public class BusinessSupportService extends BaseService{
	
	private BusinessSupportDAO businessSupportDAO;
	
	private MailUtilService mailUtilService;
	
	public BusinessSupportDAO getBusinessSupportDAO() {
		return businessSupportDAO;
	}
	public void setBusinessSupportDAO(BusinessSupportDAO businessSupportDAO) {
		this.businessSupportDAO = businessSupportDAO;
	}
	public void extensionProject(Context context){
		Map<String, Object> output = new HashMap<String, Object>();
		Output.jspOutput(output, context, "/businessSupport/extensionProjectValidDate.jsp");
	}
	
	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}
	
	public Integer updateExtensionProjectValidDate(Map paramMap) throws Exception{
		Integer flag = null;
		SqlMapClient sqlMap = DataAccessor.getSession();
		flag = sqlMap.update("businessSupport.extensionProjectValidDate", paramMap);
		return flag;
	}
	
	public String createSql(SqlTO sql){
		return (String) insert("businessSupport.createSql", sql);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void doTest(String id) {
		SqlTO sql = businessSupportDAO.getSql(id);
		try {
			sql = executeSql(sql);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		sql.setStatus(1);
		updateSql(sql);
		throw new ServiceException("测试执行，直接回滚");
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public SqlTO doExecute(String id) {
		SqlTO sql = businessSupportDAO.getSql(id);
		try {
			sql = executeSql(sql);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e);
		}
		sql.setStatus(2);
		sql.setExecuted_time(new Timestamp(new Date().getTime()));
		updateSql(sql);
		return sql;
	}
	
	public SqlTO executeSql(SqlTO sql) throws Exception{
		if ("UPDATE".equals(sql.getSql_type())) {
			sql.setResult(businessSupportDAO.executeUpdateSql(sql));
		} else if ("INSERT".equals(sql.getSql_type())) {
			sql.setResult(businessSupportDAO.executeUpdateSql(sql));
		} else if ("SELECT".equals(sql.getSql_type())) {
			List<LinkedHashMap<String, Object>> list = businessSupportDAO.executeSelectSql(sql);
			sql.setResultList(dataComposer(list));
			sql.setResult(list.size());
		} else {
			throw new ServiceException("无效Sql语句。");
		}
		return sql;
	}
	
	private List<List<String>> dataComposer(List<LinkedHashMap<String, Object>> data) {
		List<List<String>> resultList = new ArrayList<List<String>>();
		List<String> keyList = new ArrayList<String>();
		for (String key : data.get(0).keySet()) {
			keyList.add(key);
		}
		resultList.add(keyList);
		Object o = null;
		String s = null;
		List<String> dataList = null;
		for (Map<String, Object> m : data) {
			dataList = new ArrayList<String>();
			for (String k : keyList) {
				o = m.get(k);
				s = null;
				try {
					//if (o instanceof java.sql.Date) {
						//s = DataUtil.dateToStringUtil((java.sql.Date)o, "yyyy-MM-dd HH:mm");
					//} else {
						s = String.valueOf(o);
					//}
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				dataList.add(s);
			}
			resultList.add(dataList);
		}
		return resultList;
	}
	
	public void updateSql(SqlTO sql){
		businessSupportDAO.updateSql(sql);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void doUpdateSuplPledge(Context context) throws Exception {
		String creditId = (String) context.contextMap.get("ID");
		String old_supl_true = (String) context.contextMap.get("old_supl_true");
		String new_supl_true = (String) context.contextMap.get("SUPL_TRUE");
		//修改报告方案表
		update("businessSupport.doUpdateSuplPledge", context.contextMap);
		//修改合同方案表
		String rectId = LeaseUtil.getRectIdByCreditId(creditId);
		if (!StringUtils.isEmpty(rectId)) {
			context.contextMap.put("rectId", rectId);
			update("businessSupport.doUpdateSuplPledgeForRect", context.contextMap);
			//修改支付表
			update("businessSupport.doUpdateSuplPledgeForRecp", context.contextMap);
		}
		
		
		String memo = "将供应商保证【" + getDataDictionaryFlagByCode("供应商保证", old_supl_true) + "】" +
				"修改为：【" + getDataDictionaryFlagByCode("供应商保证", new_supl_true) + "】";
		//addBusinessLog(creditId, "修改", "修改供应商保证", memo, String.valueOf(context.contextMap.get("s_employeeId")), String.valueOf(context.contextMap.get("IP")));
		addBusinessLog(creditId, "业务支撑", "修改供应商保证", memo, context.request.getSession());
		
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void doUpdateEqmt(Context context) throws Exception{
		StringBuffer sb = new StringBuffer("");
		String creditId = (String) context.contextMap.get("ID");
		String supl_id = LeaseUtil.getSuplIdByCreditId(creditId);
		if (StringUtils.isEmpty(supl_id)) {
			throw new Exception("供应商丢失，请重试。");
		}
		List<Map<String, Object>> list = (List<Map<String, Object>>) queryForList("businessSupport.showUpdateEqmt", context.contextMap); 
		Map<String, Object> updateMap = null;
		String new_thing_name = null;
		String new_model_spec = null;
		String new_thing_type = null;
		String new_manufacturer = null;
		boolean updateFlag = false; //是否需要更新
		for (Map<String, Object> map : list) {
			updateFlag = false; //初始化
			new_thing_name = (String) context.contextMap.get("THING_NAME_" + map.get("PRCE_ID"));
			new_model_spec = (String) context.contextMap.get("MODEL_SPEC_" + map.get("PRCE_ID"));
			new_thing_type = (String) context.contextMap.get("THING_TYPE_" + map.get("PRCE_ID"));
			new_manufacturer = (String) context.contextMap.get("MANUFACTURER_" + map.get("PRCE_ID"));
			if (StringUtils.isEmpty(new_thing_name)) {
				throw new Exception("设备名称不能为空。");
			}
			if (StringUtils.isEmpty(new_model_spec)) {
				throw new Exception("型号不能为空。");
			}
			if (StringUtils.isEmpty(new_thing_type)) {
				throw new Exception("厂牌不能为空。");
			}
			if (StringUtils.isEmpty(new_manufacturer)) {
				throw new Exception("制造商不能为空。");
			}
			
			updateMap = new HashMap<String, Object>();
			updateMap.put("THING_NAME", new_thing_name);
			updateMap.put("MODEL_SPEC", new_model_spec);
			updateMap.put("THING_TYPE", new_thing_type);
			updateMap.put("MANUFACTURER", new_manufacturer);
			updateMap.put("supl_id", supl_id);
			updateMap.put("PRCE_ID", map.get("PRCE_ID"));
			updateMap.put("SUEQ_ID", map.get("SUEQ_ID"));
			if (!String.valueOf(new_thing_name).equals(map.get("THING_NAME"))) {
				sb.append("设备名称：【");
				sb.append(map.get("THING_NAME"));
				sb.append("】");
				sb.append("改为：【");
				sb.append(new_thing_name);
				sb.append("】；");
				updateFlag = true;
			}
			if (!String.valueOf(new_model_spec).equals(map.get("MODEL_SPEC"))) {
				sb.append("设备型号：【");
				sb.append(map.get("MODEL_SPEC"));
				sb.append("】");
				sb.append("改为：【");
				sb.append(new_model_spec);
				sb.append("】；");
				updateFlag = true;
			}
			if (!String.valueOf(new_thing_type).equals(map.get("THING_TYPE"))) {
				sb.append("设备厂牌：【");
				sb.append(map.get("THING_TYPE"));
				sb.append("】");
				sb.append("改为：【");
				sb.append(new_thing_type);
				sb.append("】；");
				updateFlag = true;
			}
			if (!String.valueOf(new_manufacturer).equals(map.get("MANUFACTURER"))) {
				sb.append("制造商：【");
				sb.append(map.get("MANUFACTURER"));
				sb.append("】");
				sb.append("改为：【");
				sb.append(new_manufacturer);
				sb.append("】；");
				updateFlag = true;
			}
			if (!updateFlag) {
				//不需要更新就跳过
				System.out.println("不需要更新");
				continue;
			}
			String sueq = generateSueq(updateMap);
			updateMap.put("sueq", sueq);
			//更新报告设备
			update("businessSupport.updateCreditEquipment", updateMap);
			//更新合同设备
			String rectId = LeaseUtil.getRectIdByCreditId(creditId);
			if (!StringUtils.isEmpty(rectId)) {
				updateMap.put("rectId", rectId);
				List<Map<String, Object>> needUpdateForRect = (List<Map<String, Object>>) queryForList("businessSupport.getNeedUpdateForRect", updateMap);
				for (Map<String, Object> n : needUpdateForRect) {
					updateMap.put("RECD_ID", n.get("RECD_ID"));
					updateMap.put("EQMT_ID", n.get("EQMT_ID"));
					update("businessSupport.updateRectDetailInfo", updateMap);
					update("businessSupport.updateRectEqmt", updateMap);
				}
			}
		}
		if (updateFlag) {
			addBusinessLog(creditId, "业务支撑", "修改设备", sb.toString(), context.request.getSession());
		}
	}
	
	private String generateSueq(Map<String, Object> updateMap) throws Exception {
		String sueq = null;
		//判断是否已经有厂牌和制造商
		List<String> resultList = (List<String>) queryForList("businessSupport.getTypeByName", updateMap);
		boolean flag = false;	//标示是否直接新建
		String type_id = null;
		String kind_id = null;
		String product_id = null;
		//厂牌制造商
		if (resultList != null && resultList.size() > 0) {
			type_id = resultList.get(0);
		} else {
			type_id = (String) insert("businessSupport.insertType", updateMap);
			flag = true;
		}
		if (StringUtils.isEmpty(type_id)) {
			throw new Exception("厂牌制造商生成出错，请重试。");
		}
		updateMap.put("type_id", type_id);
		
		//设备名称
		if (flag) {
			kind_id = (String) insert("businessSupport.insertKind", updateMap);
		} else {
			resultList = (List<String>) queryForList("businessSupport.getKindByNameAndType", updateMap);
			if (resultList != null && resultList.size() > 0) {
				kind_id = resultList.get(0);
			} else {
				kind_id = (String) insert("businessSupport.insertKind", updateMap);
				flag = true;
			}
		}
		if (StringUtils.isEmpty(kind_id)) {
			throw new Exception("产品名称生成出错，请重试。");
		}
		updateMap.put("kind_id", kind_id);
		
		//设备型号
		if (flag) {
			product_id = (String) insert("businessSupport.insertProduct", updateMap);
		} else {
			resultList = (List<String>) queryForList("businessSupport.getProductByNameAndKind", updateMap);
			if (resultList != null && resultList.size() > 0) {
				product_id = resultList.get(0);
			} else {
				product_id = (String) insert("businessSupport.insertProduct", updateMap);
				flag = true;
			}
		}
		if (StringUtils.isEmpty(product_id)) {
			throw new Exception("产品型号生成出错，请重试。");
		}
		updateMap.put("product_id", product_id);
		
		//供应商产品
		if (flag) {
			sueq = (String) insert("businessSupport.insertSueq", updateMap);
		}  else {
			resultList = (List<String>) queryForList("businessSupport.getSueqBySuplAndProduct", updateMap);
			if (resultList != null && resultList.size() > 0) {
				sueq = resultList.get(0);
			} else {
				sueq = (String) insert("businessSupport.insertSueq", updateMap);
			}
		}
		if (StringUtils.isEmpty(sueq)) {
			throw new Exception("供应商产品生成出错，请重试。");
		}
		return sueq;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void doUpdateMemo(Context context) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String oldMemo = null;
		String creditId = (String) context.contextMap.get("ID");
		String logType = "业务支撑";
		HttpSession session = context.request.getSession();
		//修改业务端意见
		String project_content = (String) context.contextMap.get("project_content");
		String project_content_id = (String) context.contextMap.get("project_content_id");
		paramMap.put("memo", project_content);
		paramMap.put("memo_id", project_content_id);
		oldMemo = (String) queryForObj("riskAudit.getProjectMemo", paramMap);
		update("riskAudit.updateProjectMemo", paramMap);
		if (!project_content.equals(oldMemo)) {
			addBusinessLog(creditId, logType, "修改建议承做理由", project_content, session);
		}
		
		String project_content_other = (String) context.contextMap.get("project_content_other");
		String project_content_other_id = (String) context.contextMap.get("project_content_other_id");
		paramMap.put("memo", project_content_other);
		paramMap.put("memo_id", project_content_other_id);
		oldMemo = (String) queryForObj("riskAudit.getProjectMemo", paramMap);
		update("riskAudit.updateProjectMemo", paramMap);
		if (!project_content_other.equals(oldMemo)) {
			addBusinessLog(creditId, logType, "修改其他租赁条件", project_content_other, session);
		}
		
		String memo_manage = (String) context.contextMap.get("memo_manage");
		String memo_manage_id = (String) context.contextMap.get("memo_manage_id");
		paramMap.put("memo", memo_manage);
		paramMap.put("memo_id", memo_manage_id);
		oldMemo = (String) queryForObj("riskAudit.getProjectMemoForManage", paramMap);
		update("riskAudit.updateProjectMemoForManage", paramMap);
		if (!memo_manage.equals(oldMemo)) {
			addBusinessLog(creditId, logType, "修改区域主管审核意见", memo_manage, session);
		}
		
		String memo_manage_dgm = (String) context.contextMap.get("memo_manage_dgm");
		String memo_manage_dgm_id = (String) context.contextMap.get("memo_manage_dgm_id");
		paramMap.put("memo", memo_manage_dgm);
		paramMap.put("memo_id", memo_manage_dgm_id);
		oldMemo = (String) queryForObj("riskAudit.getProjectMemoForManage", paramMap);
		update("riskAudit.updateProjectMemoForManage", paramMap);
		if (!memo_manage_dgm.equals(oldMemo)) {
			addBusinessLog(creditId, logType, "修改业务副总审核意见", memo_manage_dgm, session);
		}
		
		//审查意见
		String prcm_id_str = (String) context.contextMap.get("prcm_id_str");
		if (StringUtils.isEmpty(prcm_id_str)) {
			return;
		}
		String[] prcm_ids = prcm_id_str.split(",");
		String oldRiskMemo = null;
		String newRiskMemo = null;
		String LOG_TITLE = null;
		for (String prcm_id : prcm_ids) {
			//getRiskMemoById
			paramMap.put("prcm_id", prcm_id);
			oldRiskMemo = (String) queryForObj("businessSupport.getRiskMemoById", paramMap);
			newRiskMemo = (String) context.contextMap.get("PRCM_CONTEXT_" + prcm_id);
			LOG_TITLE = (String) context.contextMap.get("LOG_TITLE_" + prcm_id);
			paramMap.put("newRiskMemo", newRiskMemo);
			update("businessSupport.updateRiskMemo", paramMap);
			if (!newRiskMemo.equals(oldRiskMemo)) {
				addBusinessLog(creditId, logType, LOG_TITLE, newRiskMemo, session);
			}
		}
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void updateCustName(String cust_name_updated, String credit_id, HttpSession session) throws Exception {
		String cust_id = LeaseUtil.getCustIdByCreditId(credit_id);
		String old_cust_name = LeaseUtil.getCustNameByCustId(cust_id);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cust_name", cust_name_updated);
		paramMap.put("cust_id", cust_id);
		//验证客户名称是否重复
		Integer custCount = (Integer) queryForObj("businessSupport.getCustCountByName", paramMap);
		if (custCount > 0) {
			throw new Exception("客户名称已存在，无法修改。");
		}
		//更新客户信息中的客户名称
		update("businessSupport.updateCustNameById", paramMap);
		
		//查询出所有用到这个客户的报告
		List<Map<String, Object>> creditByCust = (List<Map<String, Object>>) queryForList("businessSupport.getProjectForUpdateCust", paramMap);
		String rectId = null;
		//遍历，修改每一个报告的客户名称
		for (Map<String, Object> map : creditByCust) {
			map.put("newName", cust_name_updated);
			//修改报告公司沿革中的客户名称
			update("businessSupport.updateProjectCust", map);
			rectId = LeaseUtil.getRectIdByCreditId(credit_id);
			if (!StringUtils.isEmpty(rectId)) {
				//修改合同表中的客户名称
				update("businessSupport.updateRectCust", map);
			}
			addBusinessLog(String.valueOf(map.get("ID")), "业务支撑", "修改客户名称", "客户名称由【" + old_cust_name + "】改为：【" + cust_name_updated + "】", session);
		}
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void updateCustNameForRelated(String cust_name_related, String credit_id, HttpSession session) throws Exception {
		String old_cust_name = LeaseUtil.getCustNameByCreditId(credit_id);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cust_id", cust_name_related);
		Map<String, Object> cust = (Map<String, Object>) queryForObj("customer.readInfo", paramMap);
		paramMap.put("creditId", credit_id);
		//修改报告中的客户ID
		update("businessSupport.updateCustIdForProject", paramMap);
		//修改报告公司沿革中的客户名称
		paramMap.put("newName", cust.get("CUST_NAME"));
		update("businessSupport.updateProjectCust", paramMap);
		String rectId = LeaseUtil.getRectIdByCreditId(credit_id);
		if (!StringUtils.isEmpty(rectId)) {
			paramMap.put("ID", credit_id);
			//修改合同表中的客户名称
			update("businessSupport.updateRectCust", paramMap);
		}
		addBusinessLog(credit_id, "业务支撑", "修改客户名称", "客户名称由【" + old_cust_name + "】改为：【" + cust.get("CUST_NAME") + "】", session);
	}
	
	/**
	 * 修改供应商
	 * @param cust_name_updated
	 * @param credit_id
	 * @param session
	 * @throws Exception
	 */
	@Transactional(rollbackFor=Exception.class)
	public void updateSuplName(String supl_name_updated, String credit_id, HttpSession session) throws Exception {
		String supl_id = LeaseUtil.getSuplIdByCreditId(credit_id);
		String old_supl_name = LeaseUtil.getSuplNameByCreditId(credit_id);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("supl_name", supl_name_updated);
		paramMap.put("supl_id", supl_id);
		paramMap.put("old_supl_name", old_supl_name);
		//验证供应商名称是否重复
		Integer custCount = (Integer) queryForObj("businessSupport.getSuplCountByName", paramMap);
		if (custCount > 0) {
			throw new ServiceException("供应商名称已存在，无法修改。");
		}
		//更新供应商名称（供应商表）
		update("businessSupport.updateSuplNameById", paramMap);
		
		//修改报告设备表中的供应商名称
		update("businessSupport.updateCreditEqmtBrand", paramMap);
		
		//修改合同设备表中的供应商名称
		update("businessSupport.updateRentEqmtBrand", paramMap);
		update("businessSupport.updateEqmtBrand", paramMap);
		
		addBusinessLog(credit_id, "业务支撑", "修改供应商名称", "供应商名称由【" + old_supl_name + "】改为：【" + supl_name_updated + "】", session);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void updateSuplNameForRelated(String supl_name_related, String credit_id, HttpSession session) throws Exception {
		String old_supl_name = LeaseUtil.getSuplNameByCreditId(credit_id);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("supl_id", LeaseUtil.getSuplIdByCreditId(credit_id));
		paramMap.put("new_supl_id", supl_name_related);
		//新供应商
		Map<String, Object> supl = (Map<String, Object>) queryForObj("businessSupport.getSuplById", paramMap);
		paramMap.put("creditId", credit_id);
		paramMap.put("supl_name", supl.get("NAME"));
		paramMap.put("old_supl_name", old_supl_name);
		boolean updateRentFlag = false;		//是否需要更新合同设备表
		String rect_id = LeaseUtil.getRectIdByCreditId(credit_id);
		if (!StringUtils.isEmpty(rect_id)) {
			updateRentFlag = true;
			paramMap.put("rect_id", rect_id);
		}
		List<String> emtIds = null; //		合同设备库表ID
		//查询所有需要更新的设备
		List<Map<String, Object>> dataList = (List<Map<String, Object>>) queryForList("businessSupport.getDataForUpdateSupl", paramMap);
		String new_sueq = null;
		List<String> resultList = null;
		for (Map<String, Object> data : dataList) {
			data.put("supl_id", supl_name_related);
			data.put("product_id", data.get("PRODUCT_ID"));
			resultList = (List<String>) queryForList("businessSupport.getSueqBySuplAndProduct", data);
			if (resultList != null && resultList.size() > 0) {
				new_sueq = resultList.get(0);
			} else {
				new_sueq = (String) insert("businessSupport.insertSueq", data);
			}
			paramMap.put("new_sueq", new_sueq);
			paramMap.put("old_sueq", data.get("SUEQ_ID"));
			update("businessSupport.updateCreditEqmtBySueq", paramMap);
			if (updateRentFlag) {
				emtIds = (List<String>) queryForList("businessSupport.getRentEqmtByRent", paramMap);
				for (String emtId : emtIds) {
					paramMap.put("emtId", emtId);
					update("businessSupport.updateRentEqmtByEqmt", paramMap);
					update("businessSupport.updateEqmtByEqmt", paramMap);
				}
			}
		}
		addBusinessLog(credit_id, "业务支撑", "修改供应商名称", "供应商名称由【" + old_supl_name + "】改为：【" + supl.get("NAME") + "】", session);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void doUpdateInvoiceInfo(Context context) throws Exception {
		String PJCCC_ID = (String) context.contextMap.get("PJCCC_ID");
		String NATU_ID = (String) context.contextMap.get("NATU_ID");
		if (!StringUtils.isEmpty(PJCCC_ID)) {
			doUpdateCorpInfoAndSendMail(context);
		}
		if (!StringUtils.isEmpty(NATU_ID)) {
			doOnlyUpdateNatu(context);
		}
	}
	
	private void doOnlyUpdateNatu(Context context){
		String[] bankIds = HTMLUtil.getParameterValues(context.getRequest(), "PCCBA_ID", "");
		String CODE = (String) context.contextMap.get("CODE");
		String PHONE = (String) context.contextMap.get("PHONE");
		String ADDRESS = (String) context.contextMap.get("ADDRESS");
		String NATU_ID = (String) context.contextMap.get("NATU_ID");
		String user_id = String.valueOf(context.contextMap.get("s_employeeId"));
		String creditId = (String) context.contextMap.get("ID");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("CODE", CODE);
		paramMap.put("PHONE", PHONE);
		paramMap.put("ADDRESS", ADDRESS);
		paramMap.put("user_id", user_id);
		paramMap.put("NATU_ID", NATU_ID);
		update("businessSupport.updateNatuInfo", paramMap);
		
		//拼装日志
		StringBuffer sb = new StringBuffer();
		sb.append("身份证号改为：");
		sb.append(CODE);
		sb.append("; 家庭电话改为：");
		sb.append(PHONE);
		sb.append("; 家庭地址改为：");
		sb.append(ADDRESS);
		String bank_name = null;
		String bank_acc = null;
		if (bankIds != null && bankIds.length > 0) {
			//有银行账号，修改动作
			for (String bankId : bankIds) {
				paramMap.put("bankId", bankId);
				bank_name = (String) context.contextMap.get("bank_name_" + bankId);
				bank_acc = (String) context.contextMap.get("bank_acc_" + bankId);
				paramMap.put("bank_name", bank_name);
				paramMap.put("bank_acc", bank_acc);
				update("businessSupport.updateBankInfo", paramMap);
				sb.append("; 开户行修改为：");
				sb.append(bank_name);
				sb.append(",账号：");
				sb.append(bank_acc);
			}
		} else {
			//新增
			bank_name = (String) context.contextMap.get("bank_name");
			bank_acc = (String) context.contextMap.get("bank_acc");
			paramMap.put("bank_name", bank_name);
			paramMap.put("bank_acc", bank_acc);
			insert("businessSupport.addBankInfo", paramMap);
			sb.append("; 添加开户行：");
			sb.append(bank_name);
			sb.append(",账号：");
			sb.append(bank_acc);
		}
		//记录日志
		addBusinessLog(creditId, "业务支撑", "修改开票资料", sb.toString(), context.getRequest().getSession());
		
	}
	
	private void doUpdateCorpInfoAndSendMail(Context context) throws Exception{
		String[] bankIds = HTMLUtil.getParameterValues(context.getRequest(), "PCCBA_ID", "");
		String CODE = (String) context.contextMap.get("CODE");
		String PHONE = (String) context.contextMap.get("PHONE");
		String ADDRESS = (String) context.contextMap.get("ADDRESS");
		String creditId = (String) context.contextMap.get("ID");
		String user_id = String.valueOf(context.contextMap.get("s_employeeId"));
		//查询旧数据
		Map<String, Object> oldItem = (Map<String, Object>) queryForObj("businessSupport.getInvoiceInfoForCustInfo", context.contextMap);
		List<Map<String, Object>> oldList = (List<Map<String, Object>>) queryForList("businessSupport.getInvoiceInfoForBankInfo", context.contextMap);
		Map<String, Object> newMap = new HashMap<String, Object>();
		oldItem.put("oldList", oldList);
		
		//更新公司沿革表
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("TAX_REGISTRATION_NUMBER", CODE);
		paramMap.put("TELEPHONE", PHONE);
		paramMap.put("REGISTERED_OFFICE_ADDRESS", ADDRESS);
		paramMap.put("creditId", creditId);
		paramMap.put("user_id", user_id);
		update("businessSupport.updateInvoiceInfoForCustInfo", paramMap);
		
		//组装新数据
		newMap.putAll(paramMap);
		
		//拼装日志
		StringBuffer sb = new StringBuffer();
		sb.append("税务登记号改为：");
		sb.append(CODE);
		sb.append("; 公司电话改为：");
		sb.append(PHONE);
		sb.append("; 注册地址：");
		sb.append(ADDRESS);
		String bank_name = null;
		String bank_acc = null;
		
		//新增或修改银行账号
		if (bankIds != null && bankIds.length > 0) {
			//有银行账号，修改动作
			for (String bankId : bankIds) {
				paramMap.put("bankId", bankId);
				bank_name = (String) context.contextMap.get("bank_name_" + bankId);
				bank_acc = (String) context.contextMap.get("bank_acc_" + bankId);
				paramMap.put("bank_name", bank_name);
				paramMap.put("bank_acc", bank_acc);
				update("businessSupport.updateBankInfo", paramMap);
				sb.append("; 开户行修改为：");
				sb.append(bank_name);
				sb.append(",账号：");
				sb.append(bank_acc);
				newMap.put("bank_name_" + bankId, bank_name);
				newMap.put("bank_acc_" + bankId, bank_acc);
			}
		} else {
			//新增
			bank_name = (String) context.contextMap.get("bank_name");
			bank_acc = (String) context.contextMap.get("bank_acc");
			paramMap.put("bank_name", bank_name);
			paramMap.put("bank_acc", bank_acc);
			insert("businessSupport.addBankInfo", paramMap);
			sb.append("; 添加开户行：");
			sb.append(bank_name);
			sb.append(",账号：");
			sb.append(bank_acc);
			newMap.put("bank_name", bank_name);
			newMap.put("bank_acc", bank_acc);
		}
		sb.append("。");
		
		//记录日志
		addBusinessLog(creditId, "业务支撑", "修改开票资料", sb.toString(), context.getRequest().getSession());
		
		//发送Mail
		MailSettingTo mailSettingTo = new MailSettingTo();
		mailSettingTo.setEmailContent(writeContentForUpdateInvoiceInfo(oldItem, newMap, creditId));
		mailUtilService.sendMail(161, mailSettingTo);
	}
	
	private String writeContentForUpdateInvoiceInfo(Map<String, Object> oldItem, Map<String, Object> newMap, String creditId) throws Exception{
		StringBuffer sb = new StringBuffer("<html>");
		sb.append("<style type=\"text/css\">");
		sb.append(".panel_table {");
		sb.append("	width : 100%;");
		sb.append("	border-collapse:collapse;");
		sb.append("	border:solid #A6C9E2; ");
		sb.append("	border-width:1px 0 0 1px;");
		sb.append("	overflow: hidden;");
		sb.append("}");
		sb.append(".panel_table th {");
		sb.append("	border:solid #A6C9E2;");
		sb.append(" border-width:0 1px 1px 0;");
		sb.append("	background-color: #E1EFFB;");
		sb.append("	padding : 2;");
		sb.append("	margin : 1;");
		sb.append("	font-weight: bold;");
		sb.append("	text-align: center;");
		sb.append("	white-space: pre-wrap;");
		sb.append("	color: #2E6E9E;");
		sb.append("	height: 28px;");
		sb.append("	font-size: 14px;");
		sb.append("	font-family: \"微软雅黑\";");
		sb.append("}");

		sb.append(".panel_table th *{");
		sb.append("	text-align: center;");
		sb.append("	font-size: 14px;");
		sb.append("	font-family: \"微软雅黑\";");
		sb.append("}");

		sb.append(".panel_table tr{");
		sb.append("	cursor: default;");
		sb.append("	overflow: hidden;");
		sb.append("}");
		sb.append(".panel_table td {");
		sb.append("	border:solid #A6C9E2;");
		sb.append(" border-width:0 1px 1px 0;");
		sb.append(" text-align: left;");
		sb.append("	white-space: pre-wrap;");
		sb.append("	overflow: hidden;");
		sb.append("	background-color: #FFFFFF;");
		sb.append("	padding : 5px 5px;");
		sb.append("	font-size: 12px;");
		sb.append("	font-weight: normal;");
		sb.append("	color: black;");
		sb.append("	font-family: \"微软雅黑\";");
		sb.append("}");
		sb.append(".panel_table td *{");
		sb.append("	font-weight: normal;");
		sb.append("	color: black;");
		sb.append("	text-align: left;");
		sb.append("	font-size: 12px;");
		sb.append("	font-family: \"微软雅黑\";");
		sb.append("}");
		sb.append("</style>");
		
		sb.append("<table class=\"panel_table\">");
		
		sb.append("<tr>");
		sb.append("<th>客户名称：</th>");
		sb.append("<td colspan=\"2\">" + LeaseUtil.getCustNameByCreditId(creditId) + "</td>");
		sb.append("</tr>");
		
		sb.append("<tr>");
		sb.append("<th>案件号：</th>");
		sb.append("<td colspan=\"2\">" + LeaseUtil.getRunCodeByCreditId(creditId) + "</td>");
		sb.append("</tr>");
		
		sb.append("<tr>");
		sb.append("<th>合同号：</th>");
		sb.append("<td colspan=\"2\">" + LeaseUtil.getLeaseCodeByCreditId(creditId) + "</td>");
		sb.append("</tr>");
		
		sb.append("<tr>");
		sb.append("<th>\\</th>");
		sb.append("<th>修改前</th>");
		sb.append("<th>修改后</th>");
		sb.append("</tr>");
		
		sb.append("<tr>");
		sb.append("<th>税务登记号</th>");
		sb.append("<td>" + oldItem.get("CODE") + "</td>");
		sb.append("<td>" + newMap.get("TAX_REGISTRATION_NUMBER") + "</td>");
		sb.append("</tr>");
		
		sb.append("<tr>");
		sb.append("<th>公司电话</th>");
		sb.append("<td>" + oldItem.get("PHONE") + "</td>");
		sb.append("<td>" + newMap.get("TELEPHONE") + "</td>");
		sb.append("</tr>");
		
		sb.append("<tr>");
		sb.append("<th>注册地址</th>");
		sb.append("<td>" + oldItem.get("ADDRESS") + "</td>");
		sb.append("<td>" + newMap.get("REGISTERED_OFFICE_ADDRESS") + "</td>");
		sb.append("</tr>");
		
		List<Map<String, Object>> oldList = (List<Map<String, Object>>) oldItem.get("oldList");
		if (oldList != null && oldList.size() > 0) {
			sb.append("<tr>");
			sb.append("<th>银行账号</th>");
			//修改前
			sb.append("<td>");
			sb.append("<table class=\"panel_table\">");
			
			sb.append("<tr>");
			sb.append("<th>类型</th>");
			sb.append("<th>开户银行</th>");
			sb.append("<th>账号</th>");
			sb.append("</tr>");
			
			for (Map<String, Object> map : oldList) {
				sb.append("<tr>");
				sb.append("<td>" + ("0".equals(String.valueOf(map.get("STATE"))) ? "基本账户" : "其他账户") + "</td>");
				sb.append("<td>" + map.get("BANK_NAME") + "</td>");
				sb.append("<td>" + map.get("BANK_ACCOUNT") + "</td>");
				sb.append("</tr>");
			}
			sb.append("</table>");
			
			sb.append("</td>");
			
			//修改后
			sb.append("<td>");
			sb.append("<table class=\"panel_table\">");
			
			sb.append("<tr>");
			sb.append("<th>类型</th>");
			sb.append("<th>开户银行</th>");
			sb.append("<th>账号</th>");
			sb.append("</tr>");
			
			for (Map<String, Object> map : oldList) {
				sb.append("<tr>");
				sb.append("<td>" + ("0".equals(String.valueOf(map.get("STATE"))) ? "基本账户" : "其他账户") + "</td>");
				sb.append("<td>" + newMap.get("bank_name_" + map.get("PCCBA_ID")) + "</td>");
				sb.append("<td>" + newMap.get("bank_acc_" + map.get("PCCBA_ID")) + "</td>");
				sb.append("</tr>");
			}
			sb.append("</table>");
			
			sb.append("</td>");
			sb.append("</tr>");
		} else {
			sb.append("<tr>");
			sb.append("<th>银行账号</th>");
			sb.append("<td>无</td>");
			sb.append("<td>");
			sb.append("<table class=\"panel_table\">");
			
			sb.append("<tr>");
			sb.append("<th>类型</th>");
			sb.append("<th>开户银行</th>");
			sb.append("<th>账号</th>");
			sb.append("</tr>");
			
			sb.append("<tr>");
			sb.append("<td>基本账户</td>");
			sb.append("<td>" + newMap.get("bank_name") + "</td>");
			sb.append("<td>" + newMap.get("bank_acc") + "</td>");
			sb.append("</tr>");
			sb.append("</table>");
			
			sb.append("</td>");
			sb.append("</tr>");
		}
		
		sb.append("</table>");
		sb.append("</html>");
		return sb.toString();
	}
	public void doUpdatePayWay(Context context) {
		String credit_id = String.valueOf(context.contextMap.get("ID"));
		String PAYPERCENT_0 = (String) context.contextMap.get("PAYPERCENT_0");
		String APPRORIATEMON_0 = (String) context.contextMap.get("APPRORIATEMON_0");
		String APPRORIATENAME_0 = (String) context.contextMap.get("APPRORIATENAME_0");
		String PAYPERCENT_1 = (String) context.contextMap.get("PAYPERCENT_1");
		String APPRORIATEMON_1 = (String) context.contextMap.get("APPRORIATEMON_1");
		String APPRORIATENAME_1 = (String) context.contextMap.get("APPRORIATENAME_1");
		String SPONSOR = (String) context.contextMap.get("SPONSOR");
		String INVOICE_PERSON = (String) context.contextMap.get("INVOICE_PERSON");
		String user_id = String.valueOf(context.contextMap.get("s_employeeId"));
		Map<String, Object> paramMap = new HashMap<String, Object>();
		//拼装日志
		StringBuffer sb = new StringBuffer();
		paramMap.put("CREDIT_ID", credit_id);
		Map<String, Object> scMap = (Map<String, Object>) queryForObj("businessSupport.getScInfoForUpdatePayWay", context.contextMap);
		if (!String.valueOf(SPONSOR).equals(String.valueOf(scMap.get("SPONSOR")))) {
			paramMap.put("SPONSOR", String.valueOf(SPONSOR));
			update("businessSupport.updateScInfoForUpdatePayWay", paramMap);
			sb.append("介绍人修改为：" + String.valueOf(SPONSOR) + "；");
		}
		if (!String.valueOf(INVOICE_PERSON).equals(String.valueOf(scMap.get("INVOICE_PERSON")))) {
			paramMap.put("INVOICE_PERSON", String.valueOf(INVOICE_PERSON));
			update("businessSupport.updateScInfoForUpdatePayWay", paramMap);
			sb.append("发票人修改为：" + String.valueOf(INVOICE_PERSON) + "；");
		}
		delete("creditReportManage.deleteAppropiateMon", paramMap);
		sb.append("拨款方式改为：");
		if (!StringUtils.isEmpty(PAYPERCENT_0) && !StringUtils.isEmpty(APPRORIATEMON_0)) {
			paramMap.put("TYPE", 0);
			paramMap.put("CREATE_USER_ID", user_id);
			paramMap.put("MODIFY_USER_ID", user_id);
			paramMap.put("PAYPERCENT", PAYPERCENT_0);
			paramMap.put("APPROPIATEMON", APPRORIATEMON_0);
			paramMap.put("APPRORIATENAME", APPRORIATENAME_0);
			insert("creditReportManage.createAppropiateMon", paramMap);
			sb.append("【交机前：比例");
			sb.append(PAYPERCENT_0);
			sb.append("%,金额");
			sb.append(APPRORIATEMON_0);
			sb.append(",拨款给（");
			sb.append(APPRORIATENAME_0);
			sb.append("）】");
		}
		if (!StringUtils.isEmpty(PAYPERCENT_1) && !StringUtils.isEmpty(APPRORIATEMON_1)) {
			paramMap.put("TYPE", 1);
			paramMap.put("CREATE_USER_ID", user_id);
			paramMap.put("MODIFY_USER_ID", user_id);
			paramMap.put("PAYPERCENT", PAYPERCENT_1);
			paramMap.put("APPROPIATEMON", APPRORIATEMON_1);
			paramMap.put("APPRORIATENAME", APPRORIATENAME_1);
			insert("creditReportManage.createAppropiateMon", paramMap);
			sb.append("【交机后：比例");
			sb.append(PAYPERCENT_1);
			sb.append("%,金额");
			sb.append(APPRORIATEMON_1);
			sb.append(",拨款给（");
			sb.append(APPRORIATENAME_1);
			sb.append("）】");
		}
		context.contextMap.put("msg", "修改成功");
		//记录日志
		addBusinessLog(credit_id, "业务支撑", "", sb.toString(), context.getRequest().getSession());
	}
	
}
