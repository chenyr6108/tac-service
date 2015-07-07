package com.brick.risk_audit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;


public class RiskLevelManage extends AService {
	Log logger = LogFactory.getLog(RiskLevelManage.class);
	/**
	 * 进入风控评审级别配置页面 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryRiskLevAllInfo(Context context) {		
		Map outputMap = new HashMap();
		List errList = context.errList;
		DataWrap dw = null;		
		if (errList.isEmpty()) {		
			try {			
				dw = (DataWrap) DataAccessor.query("riskLevel.queryRiskLevAllInfo", context.contextMap,DataAccessor.RS_TYPE.PAGED);			
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("评审设置--风控评审级别配置管理页错误!请联系管理员");
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("dw", dw);
			outputMap.put("content", context.contextMap.get("content"));
			Output.jspOutput(outputMap, context,"/risk_audit/risk_level_manage.jsp");
		} else {
			// 跳转到错误页面
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	/**
	 * 保存评审级别
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void createRiskLevel(Context context) {		
		Map outputMap = new HashMap();
		List errList = context.errList;
		if (errList.isEmpty()) {		
			try {			
				DataAccessor.execute("riskLevel.createRiskLevel", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);			
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("评审设置--风控评审级别配置添加错误!请联系管理员");
			}
		}		
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context,"/servlet/defaultDispatcher?__action=riskLevel.queryRiskLevAllInfo");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	/**
	 * 评审级别查看  根据id
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getRiskLevelById(Context context) {		
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map riskLevel = null;
		if (errList.isEmpty()) {		
			try {			
				riskLevel = (Map) DataAccessor.query("riskLevel.queryByid", context.contextMap, DataAccessor.RS_TYPE.MAP);
			}catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("评审设置--风控评审级别配置查看错误!请联系管理员");
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("riskLevel", riskLevel);
			Output.jsonOutput(outputMap, context);		
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	/**
	 * 评审级别修改  根据id
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void updateRiskLevel(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		if (errList.isEmpty()) {
			try {
				DataAccessor.execute("riskLevel.updateRiskLevel", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			} catch (Exception e) {
				e.printStackTrace();	
				LogPrint.getLogStackTrace(e, logger);
				errList.add("评审设置--风控评审级别配置修改错误!请联系管理员");
			} 
		}
		if (errList.isEmpty()){
			Output.jspOutput(outputMap, context,"/servlet/defaultDispatcher?__action=riskLevel.queryRiskLevAllInfo");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	/**
	 * 评审级别删除  根据id
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void deleteRiskLevelById(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		if (errList.isEmpty()) {
			try {
				DataAccessor.execute("riskLevel.deleteRiskLevelById", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			} catch (Exception e) {
				e.printStackTrace();	
				LogPrint.getLogStackTrace(e, logger);
				errList.add("评审设置--风控评审级别配置删除错误!请联系管理员");
			} 
		}
		if (errList.isEmpty()){
			Output.jspOutput(outputMap, context,"/servlet/defaultDispatcher?__action=riskLevel.queryRiskLevAllInfo");
		} else {
			outputMap.put("errList", errList )	;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
}
