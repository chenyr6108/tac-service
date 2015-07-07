package com.brick.contract.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.crypto.Data;

import org.apache.log4j.Logger;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.web.HTMLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;
/**
 * @author wujw
 * @创建日期 2010-6-29
 * @版本 V 1.1
 */
public class PucsContract extends AService {
	Log logger = LogFactory.getLog(PucsContract.class);
	
	public static final Logger log = Logger.getLogger(PucsContract.class);
	
	/**
	 * query equipments by RECT_ID
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryEquipment(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		List<Map> equipList = null;
		Map rentContract = null;
		
		if(errList.isEmpty()) {
			
			try {
				//
				rentContract = (Map) DataAccessor.query("rentContract.readRentContractByRectId", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("rentContract", rentContract);
				//
				equipList = (List<Map>)DataAccessor.query("pucsContract.queryEquipmentsByRectId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("equipList", equipList);
		
			} catch (Exception e) {
				e.printStackTrace();
				errList.add("购销合同之读取设备信息出错");
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		
		if(errList.isEmpty()) {
			Output.jspOutput(outputMap, context,"/pucscontract/selectEquipment.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
	}
	
	/**
	 * init create
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void initCreatePucsContract(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		//
		List<Map> equipList = null;
		//
		Map supplier = null;
		List<Map> supplierList = null;
		// 
		Map custMap = null;
		
		if(errList.isEmpty()) {
			
			try {
				
				outputMap.put("SUPL_ID", context.contextMap.get("SUPL_ID"));
				outputMap.put("RECT_ID", context.contextMap.get("RECT_ID"));
				outputMap.put("pucsContractcode", context.contextMap.get("pucsContractcode"));
				//
				String[] recdIds = HTMLUtil.getParameterValues(context.request, "RECD_ID", "0");
				Map recdMap = new HashMap();
				recdMap.put("recdIds", recdIds);
				equipList = (List<Map>) DataAccessor.query("pucsContract.queryEquipmentsByRecdIds", recdMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("equipList", equipList);
				// 供货商
				supplier = (Map)DataAccessor.query("pucsContract.readSupplierBySuplId", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("supplier", supplier);
				//
				// supplierList = (List<Map>)	DataAccessor.query("pucsContract.querySuppliers", null, DataAccessor.RS_TYPE.LIST);
				// outputMap.put("supplierList", Output.serializer.serialize(supplierList));
				//
				Map companyMap = (Map) DataAccessor.query("companyManage.readCompanyAliasByRectId", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("companyMap", companyMap);
				//
				custMap = (Map)DataAccessor.query("pucsContract.getCustomerByRectId", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("custMap", custMap);
				
			} catch (Exception e) {
				e.printStackTrace();
				errList.add("初始化创建购销合同出错！" + e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		
		if(errList.isEmpty()) {
			Output.jspOutput(outputMap, context,"/pucscontract/createPucsContract.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/**
	 * create
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void createPucsContract(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		Long puctId = null;
		if(errList.isEmpty()) {
			
			try {
				// 开启事物
				DataAccessor.getSession().startTransaction();
				//
				String[] eqmtIds = HTMLUtil.getParameterValues(context.request,"EQMT_ID", "0");
				
				puctId = (Long)DataAccessor.getSession().insert("pucsContract.createPucsContract", context.contextMap);
				context.contextMap.put("PUCT_ID", puctId);
				context.contextMap.put("eqmtIds", eqmtIds);
				
				DataAccessor.getSession().insert("pucsContract.createPucsContractPlan", context.contextMap);
				DataAccessor.getSession().insert("pucsContract.createPucsContractDetail", context.contextMap);
				
				DataAccessor.getSession().commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				errList.add("保存购销合同出错"+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} finally {
				try {
					DataAccessor.getSession().endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
			}
		}
		
		if(errList.isEmpty()) {
			Output.jspSendRedirect(context, "defaultDispatcher?__action=pucsContract.showPucsContract&FLAG=0&PUCT_ID=" + puctId);
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/**
	 * show
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void showPucsContract(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		//
		Map pucsContract = null;
		Map pucsContractPlan = null;
		List<Map> pucsContractDetail = null;
		
		if(errList.isEmpty()) {
			
			try {
				
				outputMap.put("PUCT_ID", context.contextMap.get("PUCT_ID"));
				outputMap.put("FLAG", context.contextMap.get("FLAG"));
				//
				pucsContract = (Map) DataAccessor.query("pucsContract.readPucsContract", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("pucsContract", pucsContract);
				//
				pucsContractPlan = (Map) DataAccessor.query("pucsContract.readPucsContractPlan", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("pucsContractPlan", pucsContractPlan);
				//
				pucsContractDetail = (List<Map>) DataAccessor.query("pucsContract.readPucsContractDetail", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("pucsContractDetail", pucsContractDetail);
				
			} catch (Exception e) {
				e.printStackTrace();
				errList.add("查看购销合同出错！" + e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		
		if(errList.isEmpty()) {
			Output.jspOutput(outputMap, context,"/pucscontract/showPucsContract.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/**
	 * init update
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void initUpdatePucsContract(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		//
		Map pucsContract = null;
		Map pucsContractPlan = null;
		List<Map> pucsContractDetail = null;
		
		if(errList.isEmpty()) {
			
			try {
				
				outputMap.put("PUCT_ID", context.contextMap.get("PUCT_ID"));
				//
				pucsContract = (Map) DataAccessor.query("pucsContract.readPucsContract", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("pucsContract", pucsContract);
				//
				pucsContractPlan = (Map) DataAccessor.query("pucsContract.readPucsContractPlan", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("pucsContractPlan", pucsContractPlan);
				//
				pucsContractDetail = (List<Map>) DataAccessor.query("pucsContract.readPucsContractDetail", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("pucsContractDetail", pucsContractDetail);
				
			} catch (Exception e) {
				e.printStackTrace();
				errList.add("修改购销合同准备数据出错！" + e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		
		if(errList.isEmpty()) {
			Output.jspOutput(outputMap, context,"/pucscontract/updatePucsContract.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/**
	 * update
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void updatePucsContract(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		if(errList.isEmpty()) {
			
			try {
				// 开启事物
				DataAccessor.getSession().startTransaction();
				//
				// String[] eqmtIds = HTMLUtil.getParameterValues(context.request,"EQMT_ID", "0");
				
				DataAccessor.getSession().update("pucsContract.updatePucsContract", context.contextMap);
				// cancel old pucsContractPlan & create new pucsContractPlan
				DataAccessor.getSession().update("pucsContract.deletePucsContractPlan", context.contextMap);
				DataAccessor.getSession().insert("pucsContract.createPucsContractPlan", context.contextMap);
				// no message about the equipments
				
				DataAccessor.getSession().commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				errList.add("修改购销合同出错"+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} finally {
				try {
					DataAccessor.getSession().endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
			}
		}
		
		if(errList.isEmpty()) {
			Output.jspSendRedirect(context, "defaultDispatcher?__action=pucsContract.showPucsContract&FLAG=0&PUCT_ID=" + context.contextMap.get("PUCT_ID"));
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	@Override
	protected void afterExecute(String action, Context context) {
		super.afterExecute(action, context);
	}

	@Override
	protected boolean preExecute(String action, Context context) {
		return super.preExecute(action, context);
	}
}
