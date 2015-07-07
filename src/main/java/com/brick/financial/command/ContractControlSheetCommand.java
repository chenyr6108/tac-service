package com.brick.financial.command;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.command.BaseCommand;
import com.brick.base.to.PagingInfo;
import com.brick.base.to.ReportDateTo;
import com.brick.base.util.LeaseUtil;
import com.brick.base.util.ReportDateUtil;
import com.brick.financial.service.ContractControlSheetService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;

public class ContractControlSheetCommand extends BaseCommand {

	private ContractControlSheetService contractControlSheetService;

	public ContractControlSheetService getContractControlSheetService() {
		return contractControlSheetService;
	}

	public void setContractControlSheetService(
			ContractControlSheetService contractControlSheetService) {
		this.contractControlSheetService = contractControlSheetService;
	}
	
	public void queryContractControlSheet(Context context) {
		
		PagingInfo<Object> pagingInfo=null;
		List<Map<String,Object>> contractList=null;
		Map<String,Object> outputMap=new HashMap<String,Object>();
		double totalPayMoney=0;
		
		if(context.contextMap.get("fromDate")==null) {
			Calendar cal=Calendar.getInstance();
			//cal.set(Calendar.DAY_OF_MONTH,1);
			
			ReportDateTo to=ReportDateUtil.getDateByDate(DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd"));
			context.contextMap.put("fromDate",to.getBeginTime());
		}
		if(context.contextMap.get("toDate")==null) {
			Calendar cal=Calendar.getInstance();
			//cal.set(Calendar.DAY_OF_MONTH,cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			
			ReportDateTo to=ReportDateUtil.getDateByDate(DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd"));
			
			context.contextMap.put("toDate",to.getEndTime());
		}
		try {
			
			DataAccessor.query("rentContract.getContractControlSheet",context.contextMap,RS_TYPE.LIST);
			context.contextMap.put("dataType","融资租赁合同类型");
			contractList=(List<Map<String,Object>>)DataAccessor.query("dataDictionary.queryDataDictionary",context.contextMap,RS_TYPE.LIST);
			pagingInfo=baseService.queryForListWithPaging("rentContract.getContractControlSheet",context.contextMap,"PAY_DATE");
			totalPayMoney=this.contractControlSheetService.getContractControlSheetPayMoneyTotal(context);
		} catch (Exception e) {
			
		}
		
		outputMap.put("fromDate",context.contextMap.get("fromDate"));
		outputMap.put("toDate",context.contextMap.get("toDate"));
		outputMap.put("content",context.contextMap.get("content"));
		outputMap.put("contractType",context.contextMap.get("contractType"));
		outputMap.put("contractList",contractList);
		outputMap.put("payOrder",context.contextMap.get("payOrder")==null?"":context.contextMap.get("payOrder"));
		outputMap.put("pagingInfo",pagingInfo);
		outputMap.put("totalPayMoney",totalPayMoney);
		outputMap.put("companyCode", context.contextMap.get("companyCode"));
		outputMap.put("companys", LeaseUtil.getCompanys());	
		Output.jspOutput(outputMap,context,"/financial/contractControlSheet.jsp");
	}
	
	public void queryContractControlSheetDetail(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List<Map<String,Object>> resultList=null;
		
		try {
			resultList=this.contractControlSheetService.getContractControlSheetDetail(context);
		} catch (Exception e) {
			
		}
		
		outputMap.put("resultList",resultList);
		outputMap.put("payOrder",context.contextMap.get("payOrder"));
		Output.jspOutput(outputMap,context,"/financial/contractControlSheetDetail.jsp");
	}
}
