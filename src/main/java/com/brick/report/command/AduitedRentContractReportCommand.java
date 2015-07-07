package com.brick.report.command;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.command.BaseCommand;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;
import com.brick.util.StringUtils;

public class AduitedRentContractReportCommand extends BaseCommand{
	
	/**
	 * 业管文审统计表
	 * @param context
	 * @throws Exception
	 */
	public void getReport(Context context) throws Exception{
		
		Map<String, Object> outputMap = new HashMap<String, Object>();
		
		int currentYear = Integer.parseInt(DateUtil.dateToString(new Date(), "yyyy"));
		int searchYear = currentYear;
		String year = (String) context.contextMap.get("year");
		if (!StringUtils.isEmpty(year)) {
			try {
				searchYear = Integer.parseInt(year);
			} catch (NumberFormatException e) {
				searchYear = currentYear;
			}
		}
		outputMap.put("year", searchYear);
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("year", searchYear);
		List<Map<String,Object>> resultList = (List<Map<String, Object>>) baseService.queryForList("report.getAduitedRentContractReport", paramMap);
		
		List<Integer> yearList = (List<Integer>)DataAccessor.query("report.getRentContractYearList",null,RS_TYPE.LIST);
													
		outputMap.put("yearList", yearList);
		outputMap.put("resultList", resultList);
		Output.jspOutput(outputMap, context, "/report/aduitedRentContractReport.jsp");
	}
	
	/**
	 * 业管文审详细列表
	 * @param context
	 * @throws Exception
	 */
	public void showDetail(Context context) throws Exception{
		PagingInfo<Object> pagingInfo=null; 
		int year = Integer.parseInt((String) context.contextMap.get("year"));	

		context.contextMap.put("year", year);
		
		String month = (String) context.contextMap.get("month");
		if(!StringUtils.isEmpty(month)){
			context.contextMap.put("month", Integer.parseInt(month));
		}
		
		String userid = (String) context.contextMap.get("userid");
		
		if(!StringUtils.isEmpty(userid)){
			context.contextMap.put("userid", Integer.parseInt(userid));
		}
						
		pagingInfo = baseService.queryForListWithPaging("report.queryAduitedRentContractDtl",context.contextMap,"HW_AUDIT_TIME",ORDER_TYPE.DESC);
		
		Map<String, Object> outputMap = new HashMap<String, Object>();
		outputMap.put("year", year);
		outputMap.put("month", month);
		outputMap.put("userid", userid);
		outputMap.put("dw", pagingInfo);
		Output.jspOutput(outputMap, context, "/report/aduitedRentContractList.jsp");
		
	}

}
