package com.brick.bussinessReport.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.command.BaseCommand;
import com.brick.batchjob.service.SupplerCustomerCaseBatchJobService;
import com.brick.batchjob.to.SuplCustCaseTo;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;

public class SupplerCustomerCaseBatchJobCommand extends BaseCommand {

	private SupplerCustomerCaseBatchJobService supplerCustomerCaseBatchJobService;

	public SupplerCustomerCaseBatchJobService getSupplerCustomerCaseBatchJobService() {
		return supplerCustomerCaseBatchJobService;
	}

	public void setSupplerCustomerCaseBatchJobService(
			SupplerCustomerCaseBatchJobService supplerCustomerCaseBatchJobService) {
		this.supplerCustomerCaseBatchJobService = supplerCustomerCaseBatchJobService;
	}
	
	public void query(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		List<Map<String,Object>> caseTypeList=null;//案况状态
		
		context.contextMap.put("TYPE","客户案况表中的案况状态");
		List<SuplCustCaseTo> resultList=this.supplerCustomerCaseBatchJobService.query(context,outputMap);
		
		caseTypeList=this.supplerCustomerCaseBatchJobService.getCaseTypeList(context);
		
		if(context.errList.isEmpty()) {
			
//			outputMap.put("pageTotalSize",context.contextMap.get("pageTotalSize"));
//			outputMap.put("currentPage",context.contextMap.get("currentPage"));
//			outputMap.put("pageCount",context.contextMap.get("pageCount"));
//			outputMap.put("pageSize",context.contextMap.get("pageSize"));
			outputMap.put("SUPL_NAME",context.contextMap.get("SUPL_NAME"));
			outputMap.put("CUST_NAME",context.contextMap.get("CUST_NAME"));
			outputMap.put("CASE_TYPE",context.contextMap.get("CASE_TYPE"));
			outputMap.put("LEASE_CODE",context.contextMap.get("LEASE_CODE"));
			outputMap.put("caseTypeList",caseTypeList);
			outputMap.put("resultList",resultList);
			Output.jspOutput(outputMap,context,"/supplerCustomerCase/supplerCustomerCase.jsp");
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
	}
}
