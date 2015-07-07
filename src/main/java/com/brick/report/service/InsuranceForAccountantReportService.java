package com.brick.report.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.service.BaseService;
import com.brick.base.to.ReportDateTo;
import com.brick.base.util.ReportDateUtil;
import com.brick.contract.service.ContractBuilder;
import com.brick.util.DateUtil;
import com.brick.util.StringUtils;
import com.ibatis.sqlmap.client.SqlMapClient;

public class InsuranceForAccountantReportService extends BaseService {
	
	static Log logger = LogFactory.getLog(ContractBuilder.class);
	private static SqlMapClient sqlMap;
	
	public SqlMapClient getSqlMap() {
		return sqlMap;
	}
	public void setSqlMap(SqlMapClient sqlMap) {
		this.sqlMap = sqlMap;
	}
	
	public static List<Map<String, Object>> getInsuDataForAcc(String date,String companyCode) throws Exception{
		List<Map<String, Object>> result = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String year = null;
		String month = null;
		try {
			if (StringUtils.isEmpty(date)) {
				date = DateUtil.getCurrentYearMonth();
			}
			String[] dateInfo = date.split("-");
			if (dateInfo.length != 2) {
				year = DateUtil.getCurrentYear();
				month = DateUtil.getCurrentMonth();
			} else {
				year = dateInfo[0];
				month = dateInfo[1];
			}
			ReportDateTo reportDate = ReportDateUtil.getDateByYearAndMonth(Integer.parseInt(year), Integer.parseInt(month));
			paramMap.put("start_date", reportDate.getBeginTime());
			paramMap.put("end_date", reportDate.getEndTime());
			paramMap.put("companyCode", companyCode);
			result = (List<Map<String, Object>>) sqlMap.queryForList("report.getInsuDataForAcc", paramMap);
		} catch (Exception e) {
			logger.error(e);
		}
		return result;
	}
	
	public static List<Map<String, Object>> getSettleDataForAcc(String date,String companyCode) throws Exception{
		List<Map<String, Object>> result = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String year = null;
		String month = null;
		try {
			if (StringUtils.isEmpty(date)) {
				date = DateUtil.getCurrentYearMonth();
			}
			String[] dateInfo = date.split("-");
			if (dateInfo.length != 2) {
				year = DateUtil.getCurrentYear();
				month = DateUtil.getCurrentMonth();
			} else {
				year = dateInfo[0];
				month = dateInfo[1];
			}
			ReportDateTo reportDate = ReportDateUtil.getDateByYearAndMonth(Integer.parseInt(year), Integer.parseInt(month));
			paramMap.put("start_date", reportDate.getBeginTime());
			paramMap.put("end_date", reportDate.getEndTime());
			paramMap.put("companyCode", companyCode);
			result = (List<Map<String, Object>>) sqlMap.queryForList("report.getSettleDataForAcc", paramMap);
		} catch (Exception e) {
			logger.error(e);
		}
		return result;
	}
	
}
