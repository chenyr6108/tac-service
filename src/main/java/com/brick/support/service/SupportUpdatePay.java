package com.brick.support.service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;



/**
 * 
 * @author wuzd
 * @date 2010,7,30
 */
public class SupportUpdatePay extends AService{		
	Log logger = LogFactory.getLog(SupportUpdatePay.class);
	/**
	 * ajax检测新旧合同号是否存在
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void checkRent(Context context) {
		Map outputMap = new HashMap();
		Map oldRentMap = new HashMap();
		Map newRentMap = new HashMap();
		try {
			//检测旧合同号是否存在
			context.contextMap.put("LEA_CODE", context.contextMap.get("oldRent").toString());
			oldRentMap = (Map) DataAccessor.query("supportUpdatePay.checkRent",context.contextMap, DataAccessor.RS_TYPE.MAP);
			//检测新合同号是否存在
			context.contextMap.put("LEA_CODE", context.contextMap.get("newRent").toString());
			newRentMap = (Map) DataAccessor.query("supportUpdatePay.checkRent",context.contextMap, DataAccessor.RS_TYPE.MAP);			
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} finally {
			outputMap.put("oldRentMap",oldRentMap.get("CNT"));
			outputMap.put("newRentMap",newRentMap.get("CNT"));
			Output.jsonOutput(outputMap, context);
		}
	}	
	/**
	 * ajax修改合同号 支付表号
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void updateRent(Context context) {
		Map outputMap = new HashMap();
		Map rentMap = new HashMap();
		Map creditMap = new HashMap();
		try {
			//修改合同号
			DataAccessor.execute("supportUpdatePay.updateRent", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
			// 根据合同号查找合同ID
			rentMap = (Map) DataAccessor.query("supportUpdatePay.checkRentById",context.contextMap, DataAccessor.RS_TYPE.MAP);			
			//修改支付表号
			context.contextMap.put("RECT_ID",rentMap.get("RECT_ID"));
			DataAccessor.execute("supportUpdatePay.updatePay2", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
			// 根据合同ID查找资信ID
			creditMap = (Map) DataAccessor.query("supportUpdatePay.checkCreditID",context.contextMap, DataAccessor.RS_TYPE.MAP);
			context.contextMap.put("PRCD_ID", creditMap.get("PRCD_ID"));
			//修改资信表里的合同号
			DataAccessor.execute("supportUpdatePay.updateCreditId", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} finally {
			Output.jsonOutput(outputMap, context);
		}
	}
	
	
	
	
	/**
	 * ajax检测新旧支付表号是否存在
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void checkPay(Context context) {
		Map outputMap = new HashMap();
		Map oldPayMap = new HashMap();
		Map newPayMap = new HashMap();
		Map oldRentMap = new HashMap();
		Map newRentMap = new HashMap();		
		try {
			//检测旧支付表号所引用的合同号是否存在
			context.contextMap.put("LEA_CODE", context.contextMap.get("oldPay").toString().substring(0,14));
			oldRentMap = (Map) DataAccessor.query("supportUpdatePay.checkRent",context.contextMap, DataAccessor.RS_TYPE.MAP);			
			//检测新支付表号所引用的合同号是否存在
			context.contextMap.put("LEA_CODE", context.contextMap.get("newPay").toString().substring(0,14));
			newRentMap = (Map) DataAccessor.query("supportUpdatePay.checkRent",context.contextMap, DataAccessor.RS_TYPE.MAP);			
			//检测旧支付表号是否存在
			context.contextMap.put("REC_CODE", context.contextMap.get("oldPay").toString());
			oldPayMap = (Map) DataAccessor.query("supportUpdatePay.checkPay",context.contextMap, DataAccessor.RS_TYPE.MAP);
			//检测新支付表号是否存在
			context.contextMap.put("REC_CODE", context.contextMap.get("newPay").toString());
			newPayMap = (Map) DataAccessor.query("supportUpdatePay.checkPay",context.contextMap, DataAccessor.RS_TYPE.MAP);			
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} finally {
			outputMap.put("oldRentMap",oldRentMap.get("CNT"));
			outputMap.put("newRentMap",newRentMap.get("CNT"));			
			outputMap.put("oldPayMap",oldPayMap.get("CNT"));
			outputMap.put("newPayMap",newPayMap.get("CNT"));
			Output.jsonOutput(outputMap, context);
		}
	}	
	/**
	 * ajax修改支付表号
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void updatePay(Context context) {
		Map outputMap = new HashMap();
		Map rentMap = new HashMap();
		try {
			//修改支付表号
			DataAccessor.execute("supportUpdatePay.updatePay", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} finally {
			Output.jsonOutput(outputMap, context);
		}
	}

	
	
	
	/**
	 * 逾期信息更新
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void dunUpdate(Context context) { 
		Map outputMap = new HashMap();
		try {	
			DataAccessor.execute("supportUpdatePay.DunUpdatePro",context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Output.jsonOutput(outputMap, context);
	}
	
	
	/**
	 * 逾期信息更新
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void logDayUpdate(Context context) { 
		Map outputMap = new HashMap();
		try {	
			DataAccessor.execute("supportUpdatePay.LogDayUpdatePro",context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Output.jsonOutput(outputMap, context);
	}
	
	
	/**
	 * 删除合同前AJAX验证合同是否存在
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void checkDeleteRent(Context context) {
		Map outputMap = new HashMap() ;
		try {
			//检测旧合同号是否存在
			outputMap = (Map) DataAccessor.query("supportUpdatePay.checkRent",context.contextMap, DataAccessor.RS_TYPE.MAP);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} finally {
			Output.jsonOutput(outputMap, context);
		}
	}
	/**
	 * 删除合同(通过存储过程delete_contract)
	 * contextMap 需要含有合同code:"rect_code"
	 */
	public void deleteRent(Context context) {
		Map outputMap = new HashMap() ;
		List errList = context.errList ;
		try{
			DataAccessor.execute("supportUpdatePay.deleteRent",context.contextMap, DataAccessor.OPERATION_TYPE.DELETE ) ;
		} catch(Exception e) {
			e.printStackTrace() ;
			LogPrint.getLogStackTrace( e, logger) ;
			errList.add("逾期信息更新--删除合同错误!请联系管理员") ;
		}
		if(errList.isEmpty()){
			Output.jsonOutput(outputMap, context) ;
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
}
