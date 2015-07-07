package com.brick.decompose.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;

public class DunDailyService extends AService {
	Log logger = LogFactory.getLog(DunDailyService.class);
	/**
	 * 查询租赁合同对应的设备
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void dunDailyManage(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List dunDailyList = null;
		Map paramMap = new HashMap();
		Map rsMap = null;
		if(errList.isEmpty()) {
			try {				
				paramMap.put("id", context.contextMap.get("s_employeeId"));
				rsMap = (Map) DataAccessor.query("employee.getEmpInforById",paramMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("p_usernode", rsMap.get("node"));
				dunDailyList = (List)DataAccessor.query("dunDaily.queryDunDailyList", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("content", context.contextMap.get("content"));
				outputMap.put("create_date", context.contextMap.get("create_date"));
				outputMap.put("sdun_day", context.contextMap.get("sdun_day"));
				outputMap.put("bdun_day", context.contextMap.get("bdun_day"));
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("查询租赁合同设备错误!请联系管理员");
			}
		}		
		if(errList.isEmpty()) {
			outputMap.put("dunDailyList", dunDailyList);
			Output.jspOutput(outputMap, context,"/decompose/dunDailyManage.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	/**
	 * 查询催收记录
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryDunRecord(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List dunList = null;
		if(errList.isEmpty()) {
			try {				
				context.contextMap.put("CUST_CODE", context.contextMap.get("CUST_CODE"));
				dunList = (List)DataAccessor.query("dunDaily.queryDunRecordList", context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("查询催收记录错误!请联系管理员");
			}
		}		
		if(errList.isEmpty()) {
			outputMap.put("dunList", dunList);
			Output.jspOutput(outputMap, context,"/decompose/dunRecordList.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
}
