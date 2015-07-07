package com.brick.birtReport.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibatis.sqlmap.client.SqlMapClient;

public class BirtReportService {

	static Log logger = LogFactory.getLog(BirtReportService.class);
	private static SqlMapClient sqlMap;
	
	public SqlMapClient getSqlMap() {
		return sqlMap;
	}
	public void setSqlMap(SqlMapClient sqlMap) {
		this.sqlMap = sqlMap;
	}
	
	public static List<Map<String, Object>> exportEstimatesPayDateReport(String dateFrom, String dateTo){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("dateFrom", dateFrom);
		paramMap.put("dateTo", dateTo);
		List<Map<String, Object>> resultList = null;
		try {
			resultList = sqlMap.queryForList("report.exportEstimatesPayDateReport", paramMap);
		} catch (SQLException e) {
			logger.error(e);
		}
		return resultList;
	}
	
}
