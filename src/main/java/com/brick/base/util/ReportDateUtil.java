package com.brick.base.util;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.brick.base.to.ReportDateTo;
import com.brick.util.DateUtil;
import com.ibatis.sqlmap.client.SqlMapClient;

public class ReportDateUtil {
	private ReportDateUtil(){};
	private static SqlMapClient sqlMap;
	
	public SqlMapClient getSqlMap() {
		return sqlMap;
	}
	public void setSqlMap(SqlMapClient sql) {
		sqlMap = sql;
	}
	
	private static Map<String, ReportDateTo> map = null;
	
	/**
	 * 根据日期获取开始时间和截止时间
	 * @param date
	 * @return Map ("BEGINTIME","ENDTIME")
	 */
	public static ReportDateTo getDateByDate(String date) {
		
		Iterator<ReportDateTo> it=map.values().iterator();
		for(;it.hasNext();) {
			ReportDateTo to=it.next();
			if(to.getEndTime().compareTo(DateUtil.strToDate(date,"yyyy-MM-dd"))>=0&&
					DateUtil.strToDate(date,"yyyy-MM-dd").compareTo(to.getBeginTime())>=0) {
				return to;//如果传进来的时间在开始时间与结束时间之间 返回to
			}
		}
		
		return new ReportDateTo();
	}
	/**
	 * 根据年与月获取开始时间和截止时间
	 * @param year
	 * @param month
	 * @return Map ("BEGINTIME","ENDTIME")
	 */
	public static ReportDateTo getDateByYearAndMonth(int year,int month){
		String key =  String.valueOf(year) + "_" + String.valueOf(month);
		ReportDateTo date = (ReportDateTo) map.get(key);
		return date;
	}
	
	/**
	 * 根据年获取开始时间和截止时间
	 * @param year
	 * @return Map ("BEGINTIME","ENDTIME")
	 */
	public static ReportDateTo getDateByYear(int year){
		ReportDateTo result = new ReportDateTo();
		result.setYear(year);
		String key =  String.valueOf(year) + "_1";
		ReportDateTo date = map.get(key);
		if(date!=null){
			result.setBeginTime(date.getBeginTime());
		}
		key =  String.valueOf(year) + "_12";
		date = map.get(key);
		if(date!=null){
			result.setEndTime(date.getEndTime());
		}
		return result;
	}
	/**
	 * 初始化信息
	 * @throws SQLException 
	 */
	public void initReportDateInfo() throws SQLException{
		if(map == null){
			map = new HashMap <String,ReportDateTo>();
		}
		@SuppressWarnings("unchecked")
		List<ReportDateTo> resultList = sqlMap.queryForList("reportDateUtil.queryReportDateInfo");
		
		for(ReportDateTo result:resultList){
			int year = result.getYear();
			short month = result.getMonth();
			String key =  String.valueOf(year) + "_" + String.valueOf(month);
			map.put(key, result);
		}
	};
	@SuppressWarnings("unchecked")
	public static List<ReportDateTo> getReportDateList() throws SQLException{
		return sqlMap.queryForList("reportDateUtil.queryReportDateInfo");
	}
}
