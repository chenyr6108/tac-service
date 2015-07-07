package com.brick.bussinessReport.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.bussinessReport.service.CustomerVisitReportService;
import com.brick.bussinessReport.to.CustomerVisitTO;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;

public class CustomerVisitReportCommand extends BaseCommand {

	Log logger=LogFactory.getLog(CustomerVisitReportCommand.class);
	
	private CustomerVisitReportService customerVisitReportService;

	public CustomerVisitReportService getCustomerVisitReportService() {
		return customerVisitReportService;
	}

	public void setCustomerVisitReportService(
			CustomerVisitReportService customerVisitReportService) {
		this.customerVisitReportService = customerVisitReportService;
	}
	
	public void query(Context context) {
		
		if(logger.isDebugEnabled()) {
			logger.debug("--------------客户拜访计划外出时间--------------开始(employeeId:"+context.contextMap.get("s_employeed")+")");
		}
		
		PagingInfo<Object> pagingInfo=null;
		Map<String, Object> outputMap=new HashMap<String, Object>();
		List deptList=null;
		List<CustomerVisitTO> dateList=null;
		
		try {
			deptList=this.baseService.queryForList("customerVisit.getDeptList",outputMap);
			
			dateList=this.customerVisitReportService.getDateList();
			
			int day=this.customerVisitReportService.getDayCount(context.contextMap);
			
			context.contextMap.put("DAY",day);
			pagingInfo=this.baseService.queryForListWithPaging("businessReport.queryCustomerVisitTime",context.contextMap,"[order]",ORDER_TYPE.ASC);
			
			outputMap.put("DEPT_ID",context.contextMap.get("DEPT_ID"));
			outputMap.put("DATE",context.contextMap.get("DATE"));
			outputMap.put("dateList",dateList);
			outputMap.put("deptList",deptList);
			outputMap.put("DAY",day);
			outputMap.put("pagingInfo",pagingInfo);
			
			Output.jspOutput(outputMap,context,"/customerVisit/customerVisitReport.jsp");
		} catch(Exception e) {
			logger.debug("--------------客户拜访计划外出时间--------------出错(employeeId:"+context.contextMap.get("s_employeed")+")");
			e.printStackTrace();
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("--------------客户拜访计划外出时间--------------结束(employeeId:"+context.contextMap.get("s_employeed")+")");
		}
	}
}
