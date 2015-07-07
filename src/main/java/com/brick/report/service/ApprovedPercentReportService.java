package com.brick.report.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.service.BaseService;
import com.brick.base.to.ReportDateTo;
import com.brick.base.util.ReportDateUtil;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.RS_TYPE;

public class ApprovedPercentReportService extends BaseService {
	
	public List<Map<String, Object>> getReport(int year) throws Exception{
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		//11年设备报表
		//获取11年提交的案件的所有类型
		paramMap.put("year", year);
		result = (List<Map<String, Object>>) this.queryForList("report.getTypesByYear", paramMap);
		//循环月份
		Integer total = 0;
		Integer approved = 0;
		Integer t_total = 0;
		Integer t_approved = 0;
		ReportDateTo reportDate = null;
		for (int i = 1; i <= 12; i++) {
			//paramMap.put("month", i);
			reportDate = ReportDateUtil.getDateByYearAndMonth(year, i);
			paramMap.put("start_date", reportDate.getBeginTime());
			paramMap.put("end_date", reportDate.getEndTime());
			for (int j = 0; j < result.size(); j++) {
				paramMap.put("type", result.get(j).get("T"));
				paramMap.put("isApproved", "N");
				total = (Integer) this.queryForObj("report.getInfoForApprovedReport", paramMap);
				
				paramMap.put("isApproved", "Y");
				approved = (Integer) this.queryForObj("report.getInfoForApprovedReport", paramMap);
				t_approved += approved;
				result.get(j).put("total_" + i, total);
				result.get(j).put("approved_" + i, approved);
				
				t_total = (Integer) result.get(j).get("t_total_" + i);
				t_total = t_total == null ? total : (t_total += total);
				result.get(j).put("t_total_" + i, t_total);
				
				t_approved = (Integer) result.get(j).get("t_approved_" + i);
				t_approved = t_approved == null ? approved : (t_approved += approved);
				result.get(j).put("t_approved_" + i, t_approved);
			}
		}
		for (int i = 0; i < result.size(); i++) {
			result.get(i).put("typeName", getTypeName(result.get(i).get("T")));
			if ("4".equals(String.valueOf(result.get(i).get("T")))) {
				result.get(i).put("group", "重车");
			} else {
				result.get(i).put("group", "设备");
			}
		}
		return result;
	}

	public static List<Map<String, Object>> getReportForBirt(int year) throws Exception {
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		//11年设备报表
		//获取11年提交的案件的所有类型
		paramMap.put("year", year);
		result = (List<Map<String, Object>>) DataAccessor.query("report.getTypesByYear", paramMap, RS_TYPE.LIST);
		//循环月份
		Integer total = 0;
		Integer approved = 0;
		Integer t_total = 0;
		Integer t_approved = 0;
		for (int i = 1; i <= 12; i++) {
			paramMap.put("month", i);
			for (int j = 0; j < result.size(); j++) {
				paramMap.put("type", result.get(j).get("T"));
				paramMap.put("isApproved", "N");
				total = (Integer) DataAccessor.query("report.getInfoForApprovedReport", paramMap, RS_TYPE.OBJECT);
				
				paramMap.put("isApproved", "Y");
				approved = (Integer) DataAccessor.query("report.getInfoForApprovedReport", paramMap, RS_TYPE.OBJECT);
				t_approved += approved;
				result.get(j).put("total_" + i, total);
				result.get(j).put("approved_" + i, approved);
				
				t_total = (Integer) result.get(j).get("t_total_" + i);
				t_total = t_total == null ? total : (t_total += total);
				result.get(j).put("t_total_" + i, t_total);
				
				t_approved = (Integer) result.get(j).get("t_approved_" + i);
				t_approved = t_approved == null ? approved : (t_approved += approved);
				result.get(j).put("t_approved_" + i, t_approved);
			}
		}
		for (int i = 0; i < result.size(); i++) {
			result.get(i).put("typeName", getTypeName(result.get(i).get("T")));
			if ("4".equals(String.valueOf(result.get(i).get("T")))) {
				result.get(i).put("group", "重车");
			} else {
				result.get(i).put("group", "设备");
			}
		}
		return result;
	}
	
	private static String getTypeName(Object object) {
		String s = String.valueOf(object);
		String result = null;
		if ("0".equals(s)) {
			result = "一般租赁";
		} else if ("1".equals(s)) {
			result = "委托购买";
		} else if ("2".equals(s)) {
			result = "回租";
		} else if ("4".equals(s)) {
			result = "重车";
		} else if ("5".equals(s)) {
			result = "新品回租";
		}
		return result;
	}
	
}
