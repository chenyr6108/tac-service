package com.brick.report.command;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.command.BaseCommand;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;
import com.brick.util.StringUtils;

public class VipProjectInfoReportCommand extends BaseCommand {
	
	public void getReport(Context context) throws Exception{
		Map<String, Object> outputMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<Map<String, Object>> resultList = null;
		String date = (String) context.contextMap.get("date");
		if (StringUtils.isEmpty(date)) {
			date = DateUtil.dateToString(new Date(), "yyyy-MM");
		}
		
		try {
			String[] yearMonth = date.split("-");
			if (yearMonth.length == 2) {
				paramMap.put("year", yearMonth[0]);
				paramMap.put("month", yearMonth[1]);
				resultList = (List<Map<String, Object>>) baseService.queryForList("report.getVipProjectInfoReport", paramMap);
			}
			outputMap.put("resultList", resultList);
			outputMap.put("date", date);
			Output.jspOutput(outputMap, context, "/report/vipProjectInfoReport.jsp");
		} catch (Exception e) {
			throw e;
		}
	}
}
