package com.brick.bonus.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.to.ReportDateTo;
import com.brick.base.util.ReportDateUtil;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.RS_TYPE;

public class BonusDetailReport {
	public static List<Map<String, Object>> exportDetailReport(String yearAndMonth){
		List<Map<String, Object>> resultList = null;
		if (yearAndMonth == null) {
			return resultList;
		}
		String[] year_month_array = yearAndMonth.split("-");
		if (year_month_array == null || year_month_array.length != 2) {
			return resultList;
		}
		String month = year_month_array[1];
		String year = year_month_array[0];
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("month", month);
		paramMap.put("year", year);
		try {
			ReportDateTo monthReportDateTo = ReportDateUtil.getDateByYearAndMonth(Integer.parseInt(year), Integer.parseInt(month));
			paramMap.put("month_start_date", monthReportDateTo.getBeginTime());
			paramMap.put("month_end_date", monthReportDateTo.getEndTime());
			resultList = (List<Map<String, Object>>) DataAccessor.query("bonusManage.bonusDetailReport", paramMap, RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}
	
	public static List<Map<String, Object>> exportEmpBonusReport(String year){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<Map<String, Object>> bonusList = null;
		paramMap.put("year", year);
		try {
			bonusList = (List<Map<String, Object>>) DataAccessor.query("bonusManage.getEmpReportForShow", paramMap, RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bonusList;
	}
}
