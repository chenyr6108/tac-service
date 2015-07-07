package com.brick.businessSupport.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.command.BaseCommand;
import com.brick.businessSupport.service.CreditDisabledEnabledService;
import com.brick.businessSupport.to.CreditTo;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;

public class CreditDisabledEnabledCommand extends BaseCommand {

	private CreditDisabledEnabledService creditDisabledEnabledService;

	public CreditDisabledEnabledService getCreditDisabledEnabledService() {
		return creditDisabledEnabledService;
	}

	public void setCreditDisabledEnabledService(
			CreditDisabledEnabledService creditDisabledEnabledService) {
		this.creditDisabledEnabledService = creditDisabledEnabledService;
	}
	
	public void query(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		List<CreditTo> resultList=this.creditDisabledEnabledService.queryForPage(context,outputMap);
		
		outputMap.put("CUST_NAME",context.contextMap.get("CUST_NAME"));
		outputMap.put("resultList",resultList);
		//设置分页
//		outputMap.put("pageTotalSize",context.contextMap.get("pageTotalSize"));
//		outputMap.put("currentPage",context.contextMap.get("currentPage"));
//		outputMap.put("pageCount",context.contextMap.get("pageCount"));
//		outputMap.put("pageSize",context.contextMap.get("pageSize"));
		
		Output.jspOutput(outputMap,context,"/businessSupport/creditDisabledEnabled.jsp");
	}
}
