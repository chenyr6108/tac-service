package com.brick.auditReport.command;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.auditReport.to.AuditReportTo;
import com.brick.base.command.BaseCommand;
import com.brick.base.exception.ServiceException;
import com.brick.base.to.ReportDateTo;
import com.brick.base.to.SelectionTo;
import com.brick.base.util.ReportDateUtil;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;
import com.brick.util.StringUtils;

public class AuditReportCommand extends BaseCommand {
	
	/**
	 * 审查日报表
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getDailyReport(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		List<AuditReportTo> resultList = null;
		List<SelectionTo> office = null;
		AuditReportTo paramTo = new AuditReportTo();
		paramTo.setDecpId(Integer.parseInt(context.contextMap.get("decp_id") == null ? "17" : (String)context.contextMap.get("decp_id")));
		ReportDateTo dateTo = ReportDateUtil.getDateByDate(DateUtil.getCurrentDate());
		String defaultYear = String.valueOf(dateTo.getYear());
		String defaultMonth = String.valueOf(dateTo.getMonth());
		String year = null;
		String month = null;
		try {
			year = (String) context.contextMap.get("year");
			month = (String) context.contextMap.get("month");
			new SimpleDateFormat("yyyy-MM").parse(year + "-" + month);
			paramTo.setAuditYear(year);
			paramTo.setAuditMonth(month);
		} catch (Exception e) {
			paramTo.setAuditYear(defaultYear);
			paramTo.setAuditMonth(defaultMonth);
		}
		try {
			ReportDateTo reportDate = ReportDateUtil.getDateByYearAndMonth(Integer.parseInt(paramTo.getAuditYear()), Integer.parseInt(paramTo.getAuditMonth()));
			paramTo.setStart_date(DateUtil.dateToString(reportDate.getBeginTime()));
			paramTo.setEnd_date(DateUtil.dateToString(reportDate.getEndTime()));
			resultList = (List<AuditReportTo>) baseService.queryForList("auditReport.getDailyReport", paramTo);
			office = baseService.getAllOffice();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		outputMap.put("resultList", resultList);
		outputMap.put("office", office);
		outputMap.put("decp_id", paramTo.getDecpId());
		outputMap.put("year", paramTo.getAuditYear());
		outputMap.put("month", paramTo.getAuditMonth());
		outputMap.put("now", DateUtil.getCurrentDate());
		Output.jspOutput(outputMap, context, "/auditReport/auditDailyReport.jsp");
	}
	
	/**
	 * 月度办事处汇总表
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getDailyReportByDecp(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		List<AuditReportTo> resultList = null;
		AuditReportTo auditReportTotal = null;
		AuditReportTo paramTo = new AuditReportTo();
		String dateStr = null;
		String date = (String) context.contextMap.get("date");
		String yearString = (String) context.contextMap.get("year");
		String monthString = (String) context.contextMap.get("month");
		if (!StringUtils.isEmpty(yearString) && !StringUtils.isEmpty(monthString)) {
			Date showDate = ReportDateUtil.getDateByYearAndMonth(Integer.valueOf(yearString), Integer.valueOf(monthString)).getEndTime();
			if (showDate.after(new java.util.Date())) {
				dateStr = DateUtil.dateToString(new java.util.Date());
			} else {
				dateStr = DateUtil.dateToString(showDate);
			}
			
		} else if (StringUtils.isEmpty(date)) {
			dateStr = DateUtil.getCurrentDate();
		} else {
			dateStr = date;
		}
		paramTo.setDateStr(dateStr);
		ReportDateTo reportDate = ReportDateUtil.getDateByDate(dateStr);
		paramTo.setStart_date(DateUtil.dateToString(reportDate.getBeginTime()));
		paramTo.setEnd_date(DateUtil.dateToString(reportDate.getEndTime()));
		try {
			resultList = (List<AuditReportTo>) baseService.queryForList("auditReport.getDailyReportByDecp", paramTo);
			auditReportTotal = (AuditReportTo) baseService.queryForObj("auditReport.getDailyReportByDecpForMonthTotal", paramTo);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		outputMap.put("resultList", resultList);
		outputMap.put("auditReportTotal", auditReportTotal);
		outputMap.put("date", dateStr);
		Output.jspOutput(outputMap, context, "/auditReport/auditDailyReportByDecp.jsp");
	}
	
	
	/**
	 * 年度汇总表
	 * @param context
	 */
	public void getAuditReportByYear(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		List<AuditReportTo> resultList = null;
		AuditReportTo reportTo = null;
		AuditReportTo paramTo = new AuditReportTo();
		String defaultYear = String.valueOf(ReportDateUtil.getDateByDate(DateUtil.getCurrentDate()).getYear());
		String year = (String)context.contextMap.get("year");
		paramTo = new AuditReportTo();
		paramTo.setAuditYear(StringUtils.isEmpty(year) ? defaultYear : year);
		try {
			resultList = new ArrayList<AuditReportTo>();
			for (int i = 1; i <= 12; i++) {
				paramTo.setAuditMonth(String.valueOf(i));
				ReportDateTo reportDate = ReportDateUtil.getDateByYearAndMonth(Integer.parseInt(paramTo.getAuditYear()), Integer.parseInt(paramTo.getAuditMonth()));
				paramTo.setStart_date(DateUtil.dateToString(reportDate.getBeginTime()));
				paramTo.setEnd_date(DateUtil.dateToString(reportDate.getEndTime()));
				reportTo = (AuditReportTo) baseService.queryForObj("auditReport.getAuditReportByYear", paramTo);
				reportTo.setAuditMonth(String.valueOf(i));
				resultList.add(reportTo);
			}
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		outputMap.put("resultList", resultList);
		outputMap.put("year", paramTo.getAuditYear());
		outputMap.put("currentMonth", String.valueOf(ReportDateUtil.getDateByDate(DateUtil.getCurrentDate()).getMonth()));
		Output.jspOutput(outputMap, context, "/auditReport/auditReportByYear.jsp");
	}
}
