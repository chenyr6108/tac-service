package com.brick.report.command;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.command.BaseCommand;
import com.brick.base.to.ReportDateTo;
import com.brick.base.util.ReportDateUtil;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;
import com.brick.util.StringUtils;

public class CompareSamePeriodReportCommand extends BaseCommand{
	
//	/**
//	 * 同期比
//	 * @param context
//	 * @throws Exception
//	 */
//	public void getReport(Context context) throws Exception{
//		
//		Map<String, Object> outputMap = new HashMap<String, Object>();
//		
//		int currentYear = Integer.parseInt(DateUtil.dateToString(new Date(), "yyyy"));
//		int searchYear = currentYear;
//		String year = (String) context.contextMap.get("year");
//		if (!StringUtils.isEmpty(year)) {
//			try {
//				searchYear = Integer.parseInt(year);
//			} catch (NumberFormatException e) {
//				searchYear = currentYear;
//			}
//		}
//		outputMap.put("year", searchYear);
//		
//		Map<String, Object> paramMap = new HashMap<String, Object>();
//		paramMap.put("year", searchYear);
//		List<Map<String,Object>> searchYearList = (List<Map<String, Object>>) baseService.queryForList("report.getCompareSamePeriodReport", paramMap);
//		
//		List<Integer> yearList = (List<Integer>)DataAccessor.query("report.getYearList",null,RS_TYPE.LIST);
//		
//		if(!yearList.isEmpty() && yearList.size()>1){
//			yearList.remove(0);
//		}
//		
//		
//		searchYear -= 1;
//		paramMap.put("year", searchYear);
//		
//		List<Map<String,Object>> lastYearList = (List<Map<String, Object>>) baseService.queryForList("report.getCompareSamePeriodReport", paramMap);
//		
//		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
//		
//		for(int i=0,size = searchYearList.size();i<size;i++ ){
//			Map<String,Object> item = new HashMap<String,Object>();
//			String pay_month = (String) searchYearList.get(i).get("PAY_MONTH");
//			BigDecimal searchYearPayMoney = (BigDecimal) searchYearList.get(i).get("PAY_MONEY");
//			
//			BigDecimal searchYearCreditMoney = (BigDecimal) searchYearList.get(i).get("CREDIT_MONEY");
//			
//			BigDecimal searchYearTotalMoney = (BigDecimal) searchYearList.get(i).get("TOTAL_MONEY");
//			
//			
//			BigDecimal lastYearPayMoney = (BigDecimal) lastYearList.get(i).get("PAY_MONEY");
//			
//			BigDecimal lastYearCreditMoney = (BigDecimal) lastYearList.get(i).get("CREDIT_MONEY");
//			
//			BigDecimal lastYearTotalMoney = (BigDecimal) lastYearList.get(i).get("TOTAL_MONEY");
//			
//			BigDecimal rate = new BigDecimal(0);
//			if(lastYearTotalMoney.compareTo(new BigDecimal(0))>0){
//				rate = searchYearTotalMoney.multiply(new BigDecimal(100)).divide(lastYearTotalMoney, 4, BigDecimal.ROUND_HALF_UP);				
//			}
//			item.put("COMPARE_RATE", rate);
//			item.put("PAY_MONTH", pay_month);
//			item.put("SEARCH_YEAR_PAY_MONEY", searchYearPayMoney);
//			item.put("SEARCH_YEAR_CREDIT_MONEY", searchYearCreditMoney);
//			item.put("SEARCH_YEAR_TOTAL_MONEY", searchYearTotalMoney);
//			
//			item.put("LAST_YEAR_PAY_MONEY", lastYearPayMoney);
//			item.put("LAST_YEAR_CREDIT_MONEY", lastYearCreditMoney);
//			item.put("LAST_YEAR_TOTAL_MONEY", lastYearTotalMoney);
//			resultList.add(item);
//		}
//		
//		
//		
//		outputMap.put("yearList", yearList);
//		outputMap.put("resultList", resultList);
//		Output.jspOutput(outputMap, context, "/report/compareSamePeriodReport.jsp");
//	}
	
	
	/**
	 * 同期比
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
		for(int i=1;i<=12;i++){
			ReportDateTo date = ReportDateUtil.getDateByYearAndMonth(searchYear, i);
			if(date != null){
			paramMap.put("begintime_"+i, date.getBeginTime());
			paramMap.put("endtime_"+i, date.getEndTime());
			}
		}
		ReportDateTo date = ReportDateUtil.getDateByYear(searchYear);
		if(date != null){
			paramMap.put("begintime", date.getBeginTime());
			paramMap.put("endtime", date.getEndTime());
		}
		List<Map<String,Object>> searchYearList = (List<Map<String, Object>>) baseService.queryForList("report.getCompareSamePeriodReport_new", paramMap);
		
		List<Integer> yearList = (List<Integer>)DataAccessor.query("report.getYearList",null,RS_TYPE.LIST);
		
		if(!yearList.isEmpty() && yearList.size()>1){
			yearList.remove(0);
		}
		
		
		searchYear -= 1;
		paramMap = new HashMap<String, Object>();
		for(int i=1;i<=12;i++){
			date = ReportDateUtil.getDateByYearAndMonth(searchYear, i);
			if(date != null){
			paramMap.put("begintime_"+i, date.getBeginTime());
			paramMap.put("endtime_"+i, date.getEndTime());
			}
		}
		date = ReportDateUtil.getDateByYear(searchYear);
		if(date != null){
			paramMap.put("begintime", date.getBeginTime());
			paramMap.put("endtime",  date.getEndTime());
		}
		
		
		List<Map<String,Object>> lastYearList = (List<Map<String, Object>>) baseService.queryForList("report.getCompareSamePeriodReport_new", paramMap);
		
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		
		for(int i=0,size = searchYearList.size();i<size;i++ ){
			Map<String,Object> item = new HashMap<String,Object>();
			String pay_month = (String) searchYearList.get(i).get("PAY_MONTH");
			BigDecimal searchYearPayMoney = (BigDecimal) searchYearList.get(i).get("PAY_MONEY");
			
			BigDecimal searchYearCreditMoney = (BigDecimal) searchYearList.get(i).get("CREDIT_MONEY");
			
			BigDecimal searchYearTotalMoney = (BigDecimal) searchYearList.get(i).get("TOTAL_MONEY");
			
			
			BigDecimal lastYearPayMoney = (BigDecimal) lastYearList.get(i).get("PAY_MONEY");
			
			BigDecimal lastYearCreditMoney = (BigDecimal) lastYearList.get(i).get("CREDIT_MONEY");
			
			BigDecimal lastYearTotalMoney = (BigDecimal) lastYearList.get(i).get("TOTAL_MONEY");
			
			BigDecimal rate = new BigDecimal(0);
			if(lastYearTotalMoney.compareTo(new BigDecimal(0))>0){
				rate = searchYearTotalMoney.multiply(new BigDecimal(100)).divide(lastYearTotalMoney, 4, BigDecimal.ROUND_HALF_UP);				
			}
			item.put("COMPARE_RATE", rate);
			item.put("PAY_MONTH", pay_month);
			item.put("SEARCH_YEAR_PAY_MONEY", searchYearPayMoney);
			item.put("SEARCH_YEAR_CREDIT_MONEY", searchYearCreditMoney);
			item.put("SEARCH_YEAR_TOTAL_MONEY", searchYearTotalMoney);
			
			item.put("LAST_YEAR_PAY_MONEY", lastYearPayMoney);
			item.put("LAST_YEAR_CREDIT_MONEY", lastYearCreditMoney);
			item.put("LAST_YEAR_TOTAL_MONEY", lastYearTotalMoney);
			resultList.add(item);
		}
		
		
		
		outputMap.put("yearList", yearList);
		outputMap.put("resultList", resultList);
		Output.jspOutput(outputMap, context, "/report/compareSamePeriodReport.jsp");
	}

}
