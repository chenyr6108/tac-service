package com.brick.credit.command;


import java.util.HashMap;
import java.util.Map;

import com.brick.base.command.BaseCommand;
import com.brick.base.exception.ServiceException;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;

public class AnomalousProjectCommand extends BaseCommand {
	
	public void getAnomalousByNotApproved(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		PagingInfo<Object> pagingInfo = null;
		Map rsMap = null;
		Map paramMap = new HashMap();
		try {
			paramMap.put("id", context.contextMap.get("s_employeeId"));
			rsMap = (Map) baseService.queryForObj("employee.getEmpInforById", paramMap);
			context.contextMap.put("p_usernode", rsMap.get("NODE"));
			pagingInfo = baseService.queryForListWithPaging("businessSupport.getAllNotApproved", context.contextMap, "dayDiff", ORDER_TYPE.DESC);
		} catch (ServiceException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		outputMap.put("pagingInfo", pagingInfo);
		outputMap.put("search_content", context.contextMap.get("search_content"));
		Output.jspOutput(outputMap, context, "/anomalousProject/notApproved.jsp");
	}
	
	public void getAnomalousByNotCommitted(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		PagingInfo<Object> pagingInfo = null;
		Map rsMap = null;
		Map paramMap = new HashMap();
		try {
			paramMap.put("id", context.contextMap.get("s_employeeId"));
			rsMap = (Map) baseService.queryForObj("employee.getEmpInforById", paramMap);
			context.contextMap.put("p_usernode", rsMap.get("NODE"));
			pagingInfo = baseService.queryForListWithPaging("businessSupport.getAllNotCommit", context.contextMap, "dayDiff", ORDER_TYPE.DESC);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		outputMap.put("pagingInfo", pagingInfo);
		outputMap.put("search_content", context.contextMap.get("search_content"));
		Output.jspOutput(outputMap, context, "/anomalousProject/notCommit.jsp");
	}
	
}
