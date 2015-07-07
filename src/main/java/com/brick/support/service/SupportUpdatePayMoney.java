package com.brick.support.service;


import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.baseManage.service.BusinessLog;
import com.brick.collection.service.HandlePaylistService;
import com.brick.collection.support.PayRate;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.DataUtil;
import com.brick.util.web.HTMLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;


/**
 * @author wuzd
 * @date 2010,7,29
 * @version 
 */
public class SupportUpdatePayMoney extends AService {
	static Log logger = LogFactory.getLog(SupportUpdatePayMoney.class);
	private Map paylist = new HashMap();
	private Integer addVersion = null;

	/**
	 * 管理页
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryPaylist(Context context) {	
		List errList = context.errList;
		Map outputMap = new HashMap();
		DataWrap dw = null;
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				dw = (DataWrap) DataAccessor.query("collectionManage.queryPaylist", context.contextMap, DataAccessor.RS_TYPE.PAGED);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		/*-------- output --------*/
		outputMap.put("dw", dw);
		outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
		outputMap.put("QSTART_DATE", context.contextMap.get("QSTART_DATE"));
		outputMap.put("QEND_DATE", context.contextMap.get("QEND_DATE"));
		outputMap.put("QSELECT_STATUS", context.contextMap.get("QSELECT_STATUS"));

		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/support/supportUpdatePayMoney.jsp");		
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	/**
	 * 修改其他费用初始化
	 * @param RECP_ID 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void showPaylist(Context context) {	
		
		logger.info("变更支付表初始化...");
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		Map paylist = null;
		List<Map> payEquipments = null;
		List<Map> payInusres = null;
		List<Map> payOtherFees = null;
		List<Map> paylines = null;
		
		List<Map> insureCompanyList = null;
		List<Map> insureTypeList = null;
		List<Map> payWays = null;
		List<Map> dealWays = null;
		
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {
				paylist = (Map) DataAccessor.query("collectionManage.readPaylistById", context.contextMap, DataAccessor.RS_TYPE.MAP);
				//
				payEquipments = (List<Map>) DataAccessor.query("collectionManage.readPayEquipments", context.contextMap, DataAccessor.RS_TYPE.LIST);
				paylist.put("payEquipments", payEquipments);
				//
				payInusres = (List<Map>) DataAccessor.query("collectionManage.readPayInusres", context.contextMap, DataAccessor.RS_TYPE.LIST);
				paylist.put("payInusres", payInusres);
				//
				payOtherFees = (List<Map>) DataAccessor.query("collectionManage.readPayOtherFees", context.contextMap, DataAccessor.RS_TYPE.LIST);
				paylist.put("payOtherFees", payOtherFees);
				//
				paylines = (List<Map>) DataAccessor.query("collectionManage.readPaylines", context.contextMap, DataAccessor.RS_TYPE.LIST);
				paylist.put("paylines", paylines);
				
				outputMap.put("paylist", paylist);
				//
				Map baseRate = PayRate.getBaseRate();
				outputMap.put("baseRateJson", Output.serializer.serialize(baseRate));
				//
				insureCompanyList = (List<Map>) DataAccessor.query("insuCompany.queryInsureCompanyListForSelect", null, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureCompanyList", insureCompanyList);
				insureTypeList = (List<Map>) DataAccessor.query("insureType.queryInsureTypeList", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureTypeList", insureTypeList);
				outputMap.put("insureTypeJsonList", Output.serializer.serialize(insureTypeList));
				
				Map dataDictionaryMap = new HashMap();
				dataDictionaryMap.put("dataType", "支付方式");
				payWays = (List) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("payWays", payWays);
				
				dataDictionaryMap.put("dataType", "租赁期满处理方式");
				dealWays = (List) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("dealWays", dealWays);
				
			} catch (Exception e) {
				e.printStackTrace();
				errList.add(e);
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/support/supportUpdatePayMoneylist.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	
	}
	
	/**
	 * 更新
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void saveChangePaylist(Context context) {
		Map outputMap = new HashMap();
		Map otherFee = new HashMap();
		List errList = context.errList;
	
		Long recpId = null;
		paylist.put("S_EMPLOYEEID",context.contextMap.get("s_employeeId"));
		
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
			try {
				DataAccessor.getSession().startTransaction();
					//
					recpId =Long.parseLong(context.contextMap.get("RECP_ID").toString());
					paylist.put("RECP_ID", recpId);
					// other fee
					DataAccessor.getSession().update("handlePaylist.deletePayOtherFee", paylist);
		
					otherFee.put("RECP_ID", recpId);
					otherFee.put("S_EMPLOYEEID", context.contextMap.get("s_employeeId"));
					
					String[] OTHER_NAME = HTMLUtil.getParameterValues(context.getRequest(),"OTHER_NAME","");
					String[] OTHER_PRICE = HTMLUtil.getParameterValues(context.getRequest(),"OTHER_PRICE","");
					String[] OTHER_DATE = HTMLUtil.getParameterValues(context.getRequest(),"OTHER_DATE","");
					String[] OTHER_MEMO = HTMLUtil.getParameterValues(context.getRequest(),"OTHER_MEMO","");
					String[] RECO_ID = HTMLUtil.getParameterValues(context.getRequest(),"RECO_ID","");
					for (int i = 0; i < OTHER_MEMO.length; i++) {
						otherFee.put("OTHER_NAME", OTHER_NAME[i]);
						otherFee.put("OTHER_PRICE", OTHER_PRICE[i]);
						otherFee.put("OTHER_DATE", OTHER_DATE[i]);
						otherFee.put("MEMO", OTHER_MEMO[i]);
						otherFee.put("RECO_ID",RECO_ID[i]);
						
						if (DataUtil.intUtil(RECO_ID[i]) != 0) {
							DataAccessor.getSession().update("handlePaylist.updatePayOtherFee", otherFee);
						} else {
							DataAccessor.getSession().insert("collectionManage.createPayOtherFee", otherFee);
						}
					}

						

				DataAccessor.getSession().commitTransaction();
				
			} catch (SQLException e) {
				logger.info("支付表变更保存错误！" + e.getMessage());
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} finally {
				try {
					DataAccessor.getSession().endTransaction();
				} catch (SQLException e) {
					logger.info("支付表变更保存关闭事务错误！" + e.getMessage());
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
			}
		}
		
		if (errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=supportUpdatePayMoney.queryPaylist");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
	}
	
	
	/**
	 * 保险费管理页
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryPayMoneylist(Context context) {	
		List errList = context.errList;
		Map outputMap = new HashMap();
		DataWrap dw = null;
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				dw = (DataWrap) DataAccessor.query("collectionManage.queryPaylist", context.contextMap, DataAccessor.RS_TYPE.PAGED);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		/*-------- output --------*/
		outputMap.put("dw", dw);
		outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
		outputMap.put("QSTART_DATE", context.contextMap.get("QSTART_DATE"));
		outputMap.put("QEND_DATE", context.contextMap.get("QEND_DATE"));
		outputMap.put("QSELECT_STATUS", context.contextMap.get("QSELECT_STATUS"));

		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/support/supportUpdatePayInsMoney.jsp");		
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	/**
	 * 修改保险费初始化
	 * @param RECP_ID 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void showPayInslist(Context context) {	
		
		logger.info("变更支付表初始化...");
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		Map paylist = null;
		List<Map> payEquipments = null;
		List<Map> payInusres = null;
		List<Map> payOtherFees = null;
		List<Map> paylines = null;
		
		List<Map> insureCompanyList = null;
		List<Map> insureTypeList = null;
		List<Map> payWays = null;
		List<Map> dealWays = null;
		
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {
				//支付表基本信息
				paylist = (Map) DataAccessor.query("collectionManage.readPaylistById", context.contextMap, DataAccessor.RS_TYPE.MAP);
				//设备信息
				payEquipments = (List<Map>) DataAccessor.query("collectionManage.readPayEquipments", context.contextMap, DataAccessor.RS_TYPE.LIST);
				paylist.put("payEquipments", payEquipments);
				//
				payInusres = (List<Map>) DataAccessor.query("collectionManage.readPayInusres", context.contextMap, DataAccessor.RS_TYPE.LIST);
				paylist.put("payInusres", payInusres);
				//
				payOtherFees = (List<Map>) DataAccessor.query("collectionManage.readPayOtherFees", context.contextMap, DataAccessor.RS_TYPE.LIST);
				paylist.put("payOtherFees", payOtherFees);
				//
				paylines = (List<Map>) DataAccessor.query("collectionManage.readPaylines", context.contextMap, DataAccessor.RS_TYPE.LIST);
				paylist.put("paylines", paylines);
				
				outputMap.put("paylist", paylist);
				//
				Map baseRate = PayRate.getBaseRate();
				outputMap.put("baseRateJson", Output.serializer.serialize(baseRate));
				//保险公司
				insureCompanyList = (List<Map>) DataAccessor.query("insuCompany.queryInsureCompanyListForSelect", null, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureCompanyList", insureCompanyList);
				//保险险种
				context.contextMap.put("INCP_ID", paylist.get("INSURANCE_COMPANY_ID"));
				insureTypeList = (List<Map>) DataAccessor.query("insureType.queryInsureTypeListById", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureTypeList", insureTypeList);
				outputMap.put("insureTypeJsonList", Output.serializer.serialize(insureTypeList));
				
				Map dataDictionaryMap = new HashMap();
				dataDictionaryMap.put("dataType", "支付方式");
				payWays = (List) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("payWays", payWays);
				
				dataDictionaryMap.put("dataType", "租赁期满处理方式");
				dealWays = (List) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("dealWays", dealWays);
				
			} catch (Exception e) {
				e.printStackTrace();
				errList.add(e);
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/support/supportUpdatePayInsMoneylist.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	
	}
	
	/**
	 * 更新支付表保险费
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void savePayInslist(Context context) {
		Map outputMap = new HashMap();
		Map insure = new HashMap();
		List errList = context.errList;

		
		Long recpId = null;
		
		paylist.put("S_EMPLOYEEID", context.contextMap.get("s_employeeId"));
		
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
			try {
				DataAccessor.getSession().startTransaction();

					insure.put("RECP_ID", context.contextMap.get("RECP_ID"));
					insure.put("INSURANCE_COMPANY_ID2", context.contextMap.get("INSURANCE_COMPANY_ID2"));
					insure.put("S_EMPLOYEEID", context.contextMap.get("s_employeeId"));							
					// insure
					DataAccessor.getSession().update("handlePaylist.deletePayInsure", insure);
					DataAccessor.getSession().update("collectionManage.updateSupportPay", insure);
						
		
					String[] INSURE_ITEM = HTMLUtil.getParameterValues(context.getRequest(),"INSURE_ITEM","");
					String[] INSURE_START_DATE = HTMLUtil.getParameterValues(context.getRequest(),"INSURE_START_DATE","");
					String[] INSURE_END_DATE = HTMLUtil.getParameterValues(context.getRequest(),"INSURE_END_DATE","");
					String[] INSURE_RATE = HTMLUtil.getParameterValues(context.getRequest(),"INSURE_RATE","");
					String[] INSURE_PRICE = HTMLUtil.getParameterValues(context.getRequest(),"INSURE_PRICE","");
					String[] INSURE_MEMO = HTMLUtil.getParameterValues(context.getRequest(),"INSURE_MEMO","");
					String[] RECI_ID = HTMLUtil.getParameterValues(context.getRequest(),"RECI_ID","");
					for (int i = 0; i < RECI_ID.length; i++) {
						insure.put("INSURE_ITEM", INSURE_ITEM[i]);
						insure.put("START_DATE", INSURE_START_DATE[i]);
						insure.put("END_DATE", INSURE_END_DATE[i]);
						insure.put("INSURE_RATE", INSURE_RATE[i]);
						insure.put("INSURE_PRICE",INSURE_PRICE[i]);
						insure.put("MEMO",INSURE_MEMO[i]);
						insure.put("RECI_ID",RECI_ID[i]);
						
						if (DataUtil.intUtil(RECI_ID[i]) != 0) {
							DataAccessor.getSession().update("handlePaylist.updatePayInsure", insure);
						} else {
							DataAccessor.getSession().insert("collectionManage.createPayInsures", insure);
						}
					}					
									
				DataAccessor.getSession().commitTransaction();
				
			} catch (SQLException e) {
				logger.info("支付表变更保存错误！" + e.getMessage());
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} finally {
				try {
					DataAccessor.getSession().endTransaction();
				} catch (SQLException e) {
					logger.info("支付表变更保存关闭事务错误！" + e.getMessage());
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
			}
		}
		
		if (errList.isEmpty()) {
			Output.jspSendRedirect(context, "defaultDispatcher?__action=supportUpdatePayMoney.queryPayMoneylist");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
				
	}

	/**
	 * ajax得到保险公司下的保险险种
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getComTypeById(Context context){
		Map outputMap = new HashMap();
		List insureTypeList=null;
		try {
			//保险险种
			insureTypeList = (List<Map>) DataAccessor.query("insureType.queryInsureTypeListById", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("insureTypeList", insureTypeList);
			outputMap.put("insureTypeJsonList", Output.serializer.serialize(insureTypeList));
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Output.jsonOutput(outputMap, context);
	}
}
