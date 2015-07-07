package com.brick.credit.vip.service;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.brick.baseManage.service.BusinessLog;
import com.brick.coderule.service.CodeRule;
import com.brick.customer.service.CustomerCredit;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.Constants;
import com.brick.util.DataUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;
import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 尽职调查报告承租人信息管理
 * 
 * @author li shaojie
 * @date Apr 26, 2010
 * 
 */

public class CreditCustomerManage extends AService {
	Log logger = LogFactory.getLog(CreditCustomerManage.class);

	/**
	 * 初始化尽职调查报告承租人信息添加页面
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void initCreditCustomerAdd(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List provinces = null;
		List creditTypes = null;
		List companyList = null;
		List contractType = null;
		List customerCome = null;
		
		
		try {
			provinces = (List) DataAccessor.query("area.getProvinces",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			context.contextMap.put("dictionaryType", "公司");

			context.contextMap.put("dictionaryType", "尽职调查报告类型");
			creditTypes = (List) DataAccessor.query("creditCustomer.getItems",
					context.contextMap, DataAccessor.RS_TYPE.LIST);

			companyList = (List) DataAccessor.query(
					"companyManage.queryCompanyAlias", null,
					DataAccessor.RS_TYPE.LIST);
			outputMap.put("companyList", companyList);
			context.contextMap.put("dataType", "融资租赁合同类型");
			contractType = (List<Map>) DataAccessor.query(
					"dataDictionary.queryDataDictionary", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			outputMap.put("contractType", contractType);
			context.contextMap.put("dataType", "客户来源");
			customerCome = (List<Map>) DataAccessor.query(
					"dataDictionary.queryDataDictionary", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			outputMap.put("customerCome", customerCome);
			
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--报告添加页面初始化错误!请联系管理员");
		}
		outputMap.put("provinces", provinces);
		outputMap.put("creditTypes", creditTypes);
		double token = Math.random();
		HttpSession session = context.request.getSession();
		session.setAttribute("s_token", token);
		outputMap.put("token", token);
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context,
					"/credit_vip/creditCustomerCreate.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	/**
	 * 根据省份ID获取该省份下的所有市，区。
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getCitysByProvinceId(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List citys = null;
		try {
			citys = (List) DataAccessor.query("area.getCitysByProvinceId",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--地区列表 省份 错误!请联系管理员");
		}
		outputMap.put("citys", citys);
		if (errList.isEmpty()) {
			Output.jsonOutput(outputMap, context);
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	/**
	 * 根据市ID获取该市下的所有区。
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getAreaByCityId(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List area = null;
		try {
			area = (List) DataAccessor.query("area.getCitysByProvinceId",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--地区列表 城市 错误!请联系管理员");
		}
		outputMap.put("area", area);
		if (errList.isEmpty()) {
			Output.jsonOutput(outputMap, context);
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	/**
	 * 生成一个尽职调查报告
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void add(Context context) {
		long custId = 0;
		long creditId = 0;
		String credti_code = CodeRule
				.generateProjectCreditCode(context.contextMap.get("decp_id"));
		//Add by Michael 2012 06-29 每个报告都将产生一个流水号
		String creditRunCode=CodeRule.geneCreditRunCode();
		context.contextMap.put("credit_runcode", creditRunCode);
		
		List errList = context.errList;
		Map outputMap = new HashMap() ;
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		try {
			sqlMapper.startTransaction() ;
			if (context.contextMap.get("cust_idcard1") != null) {
				context.contextMap.put("cust_idcard", context.contextMap
						.get("cust_idcard1"));
			} else if (context.contextMap.get("cust_idcard2") != null) {
				context.contextMap.put("cust_idcard", context.contextMap
						.get("cust_idcard2"));
			}
			Object obj = sqlMapper.queryForObject("creditCustomer.validateCustomer",
					context.contextMap);
			if (obj == null) {

				// 添加承租人编号
				// context.contextMap.put("cust_code", "123456789876543");
				// CodeRule codeRule = new CodeRule();
				String code = CodeRule.generateCustCode(context);
				context.contextMap.put("cust_code", code);
				//增加客户虚拟账号
				context.contextMap.put("virtual_code", "88"+code);
				String cust_type = (String) context.contextMap.get("cust_type");
				if (cust_type.equals("1")) {
					custId = (Long)sqlMapper.insert("creditCustomer.createCustCrop",context.contextMap);
				} else {
					String id_card_type = (String) context.contextMap
							.get("id_card_type");
					if (id_card_type.equals("1")) {
						context.contextMap.put("cust_idcard",
								context.contextMap.get("cust_idcard1"));
					} else {
						context.contextMap.put("cust_idcard",
								context.contextMap.get("cust_idcard2"));
					}
					custId = (Long) sqlMapper.insert("creditCustomer.createCustNatu",context.contextMap);
				}
			} else {
				custId = (Integer) obj;
			}
			context.contextMap.put("cust_id", custId);

			//添加一个主档
			context.contextMap.put("id", context.contextMap.get("s_employeeId"));
			Long actilog_id = (Long) DataAccessor.execute("activitiesLog.createActivitiesLog", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
			context.contextMap.put("ACTILOG_ID", actilog_id == null ? "" : actilog_id.toString());
			
			// 添加业务申请ID
			context.contextMap.put("project_id", "");
			context.contextMap.put("credti_code", credti_code);
			if (((String) context.contextMap.get("city_id")).equals("-1")) {
				context.contextMap.put("city_id", "");
			}
			
			//报告生成时候插入产品类别 add by ShenQi
			String contractType=context.contextMap.get("contract_type").toString();
			String productionType="";
			if(Constants.CONTRACT_TYPE_2.equals(contractType)||Constants.CONTRACT_TYPE_5.equals(contractType)) {
				productionType=Constants.PRODUCTION_TYPE_1;//设备类型
			} else if(Constants.CONTRACT_TYPE_4.equals(contractType)) {
				productionType=Constants.PRODUCTION_TYPE_2;//商用车类型
			} else if(Constants.CONTRACT_TYPE_6.equals(contractType)||Constants.CONTRACT_TYPE_8.equals(contractType)) {
				productionType=Constants.PRODUCTION_TYPE_3;//乘用车类型
			}
			
			context.contextMap.put("production_type",productionType);
			//绿色通道 标示
			context.contextMap.put("vip_flag", 1);
			creditId = (Long) sqlMapper.insert("creditCustomer.addCredit_vip",
					context.contextMap);
			//添加到T_LOG_ACTIVITIESLOG
			context.contextMap.put("creditId", creditId);
			if( !"".equals((String)context.contextMap.get("ACTILOG_ID"))){
				sqlMapper.update("creditCustomer.updatelog", context.contextMap);
			}
			BusinessLog.addBusinessLog(creditId, null, "现场调查报告", "生成", credti_code,
					"", 1, Long.parseLong(context.contextMap.get("s_employeeId")+ ""), null,sqlMapper,(String)context.contextMap.get("IP"));
			sqlMapper.commitTransaction() ;
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--报告添加错误!请联系管理员");
		} finally{
			try {
				sqlMapper.endTransaction() ;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
//		BusinessLog.addBusinessLog(creditId, null, "现场调查报告", "生成", credti_code,
//				"", 1, Long.parseLong(context.contextMap.get("s_employeeId")
//						+ ""), null);
		if (errList.isEmpty()) {
			Output.jspSendRedirect(context,
					"defaultDispatcher?__action=creditReportVip.selectCreditForUpdate&credit_id="
							+ creditId);
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	/**
	 * 承租人自动提示
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getCustomersForHint(Context context) {
		Map outputMap = new HashMap();
		List customers = null;
		List errList = context.errList ;
		try {
			customers = (List) DataAccessor.query(
					"creditCustomer.getCustomersForHint", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--承租人自动提示错误!请联系管理员");
		}
		outputMap.put("customers", customers);
		Output.jsonOutput(outputMap, context);
	}

	/**
	 * 添加报告页面 在客户自动提示时 获取客户id 根据客户id查询 该客户 项目 合同等信息 显示在添加报告页面中
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getCustInfo(Context context) {
		Map outputMap = new HashMap();
		Map custInfo = null;
		List<Map> custLevel = null;
		Map grantcustinfo = null;
		List<Map> groupNumIdlist = null;
		List errList = context.errList ;
		String groupNumId=context.contextMap.get("s_employeeId").toString();
		context.contextMap.put("groupNumId", Integer.parseInt(groupNumId));
		try {
			custInfo = (Map) DataAccessor.query("creditCustomer.custInfo",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
			custLevel = DictionaryUtil.getDictionary("承租人级别");
			for (int i = 0; i < custLevel.size(); i++) {
				if (DataUtil.intUtil(custInfo.get("CUST_LEVEL")) == DataUtil
						.intUtil(custLevel.get(i).get("CODE"))) {
					custInfo.put("custLevel", custLevel.get(i).get("FLAG"));
				}
			}
			grantcustinfo = (Map) DataAccessor.query(
					"creditCustomer.grantcustInfo", context.contextMap,
					DataAccessor.RS_TYPE.MAP);
			if (grantcustinfo != null) {
				if (grantcustinfo.get("GRANT_PRICE") != null) {
					grantcustinfo.put("GRANT_PRICE", "￥"
							+ updateMon(grantcustinfo.get("GRANT_PRICE")));
					if (grantcustinfo.get("CUST_ID") != null) {
						grantcustinfo.put("LAST_PRICE", "￥"
								+ updateMon(CustomerCredit.getCustCredit(grantcustinfo.get("CUST_ID"))));
					}
				}
				
			}
			groupNumIdlist = (List<Map>) DataAccessor.query("customer.groupNumId", context.contextMap,DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--报告添加中提示客户信息错误!请联系管理员");
		}
		outputMap.put("custInfo", custInfo);
		outputMap.put("grantcustInfo", grantcustinfo);
		outputMap.put("groupNumIdlist", groupNumIdlist);
		Output.jsonOutput(outputMap, context);
	}

	/** 财务格式 0.00 */
	private String updateMon(Object content) {
		String str = "";

		if (content == null || DataUtil.doubleUtil(content) == 0.0) {

			str += "0.00";
			return str;

		} else {

			DecimalFormat df1 = new DecimalFormat("#,###.00");

			str += df1.format(Double.parseDouble(content.toString()));
			return str;
		}
	}
}
