package com.brick.auditReport.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;
import com.brick.auditReport.to.AuditReportTo;
import com.brick.base.service.BaseService;
import com.brick.base.to.ReportDateTo;
import com.brick.base.util.ReportDateUtil;
import com.brick.service.core.DataAccessor;
import com.brick.util.DateUtil;
import com.brick.util.StringUtils;

public class AuditReportService extends BaseService {
	
	@Transactional(rollbackFor = Exception.class)
	public void doAuditReportDailyJob() throws Exception{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String todayStr = DateUtil.dateToString(new Date());//"2012-9-3";//
		paramMap.put("date", todayStr);
		ReportDateTo reportDate = null;
		List<AuditReportTo> auditReportInfoList = null;
		Double finish_percent = 0D;
		Double approved_percent = 0D;
		Double finish_percent_pro = 0D;
		Double approved_percent_pro = 0D;
		try {
			//删除当天记录
			baseDAO.delete("auditReport.deleteTodayInfo", paramMap);
			
			reportDate = ReportDateUtil.getDateByDate(todayStr);
			paramMap.put("start_date", reportDate.getBeginTime());
			paramMap.put("end_date", reportDate.getEndTime());
			//跑当天的日报表
			baseDAO.insert("auditReport.auditReportInsert", paramMap);
			
			//查出当天的进件状况核准状况，计算“完成率”、“核准率”
			//拿基础数据
			auditReportInfoList = (List<AuditReportTo>) this.queryForList("auditReport.getBaseInfoForPercent", paramMap);
			for (AuditReportTo auditReport : auditReportInfoList) {
				if (auditReport.getCommitTotal() > 0 && auditReport.getCommitProTotal() > 0) {
					//完成率（次数）：(累计无条件通过+不通过附条件+婉拒)/累计进件（次数）
					finish_percent = ((auditReport.getSumApproved().doubleValue() 
										+ auditReport.getSumReject().doubleValue() 
										+ auditReport.getSumReturn().doubleValue()) 
									/ auditReport.getCommitTotal().doubleValue()) * 100;
					auditReport.setFinishPercent(finish_percent >= 0 ? finish_percent : 0);
					//核准率（次数）：累计无条件通过/累计进件（次数）
					approved_percent = auditReport.getSumApproved().doubleValue() / auditReport.getCommitTotal().doubleValue() * 100;
					auditReport.setApprovedPercent(approved_percent >= 0 ? approved_percent : 0);
					//完成率（件数）：(累计进件（件数）-未完成)/累计进件（件数）
					finish_percent_pro = ((auditReport.getCommitProTotal().doubleValue() - auditReport.getUnaudited().doubleValue()) 
										/ auditReport.getCommitProTotal().doubleValue()) * 100;
					auditReport.setFinishPercentPro(finish_percent_pro >= 0 ? finish_percent_pro : 0);
					//核准率（件数）：累计无条件通过/累计进件（件数）
					approved_percent_pro = auditReport.getSumApproved().doubleValue() / auditReport.getCommitProTotal().doubleValue() * 100;
					auditReport.setApprovedPercentPro(approved_percent_pro >= 0 ? approved_percent_pro : 0);
					this.update("auditReport.updatePercent", auditReport);
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 审查日报表
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public static List<AuditReportTo> getDailyReport(String decp_id, String yearStr, String monthStr){
		List<AuditReportTo> resultList = null;
		AuditReportTo paramTo = new AuditReportTo();
		paramTo.setDecpId(Integer.parseInt(decp_id));
		String defaultYear = DateUtil.getCurrentYear();
		String defaultMonth = DateUtil.getCurrentMonth();
		String year = null;
		String month = null;
		try {
			year = yearStr;
			month = monthStr;
			new SimpleDateFormat("yyyy-MM").parse(year + "-" + month);
			paramTo.setAuditYear(year);
			paramTo.setAuditMonth(month);
		} catch (Exception e) {
			paramTo.setAuditYear(defaultYear);
			paramTo.setAuditMonth(defaultMonth);
		}
		try {
			resultList = DataAccessor.query("auditReport.getDailyReport", paramTo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}
	
	/**
	 * 月度办事处汇总表
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getDailyReportByDecp(String date){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<AuditReportTo> resultList = null;
		AuditReportTo auditReportTotal = null;
		AuditReportTo paramTo = new AuditReportTo();
		paramTo.setDate(new java.sql.Date(DateUtil.strToDay(date).getTime()));
		try {
			resultList = (List<AuditReportTo>) DataAccessor.query("auditReport.getDailyReportByDecp", paramTo);
			resultMap.put("resultList", resultList);
			auditReportTotal = (AuditReportTo) DataAccessor.queryForObj("auditReport.getDailyReportByDecpForMonthTotal", paramTo);
			resultMap.put("auditReportTotal", auditReportTotal);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}
	
	
	/**
	 * 年度汇总表
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public static List<AuditReportTo> getAuditReportByYear(String year){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		List<AuditReportTo> resultList = null;
		AuditReportTo paramTo = new AuditReportTo();
		String defaultYear = DateUtil.getCurrentYear();
		paramTo.setAuditYear(StringUtils.isEmpty(year) ? defaultYear : year);
		try {
			resultList = (List<AuditReportTo>) DataAccessor.query("auditReport.getAuditReportByYear", paramTo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}
	
}
