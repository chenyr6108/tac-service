package com.brick.bussinessReport.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.brick.activityLog.to.LoanTo;
import com.brick.base.dao.BaseDAO;
import com.brick.service.entity.Context;

public class AchievementReportDAO extends BaseDAO {

	public List<Map> query(Context context) {

		List<Map> result=this.getSqlMapClientTemplate().queryForList("caseReportService.queryOfAchievementReport",context.contextMap);

		if(result==null) {
			result=new ArrayList<Map>();
		}
		return result;

	}

	public List<String> getYearAchievementReport() {

		List<String> result=this.getSqlMapClientTemplate().queryForList("caseReportService.getYearAchievementReport");

		if(result==null) {
			result=new ArrayList<String>();
		}
		return result;
	}

	public List<Map> getDeptList(Context context) {

		List<Map> result=this.getSqlMapClientTemplate().queryForList("employee.getCompany",context.contextMap);

		if(result==null) {
			result=new ArrayList<Map>();
		}
		return result;
	}
	
	public List<Map> getAchievementTotal(Context context) {
		
		List<Map> result=this.getSqlMapClientTemplate().queryForList("caseReportService.getAchievementTotal",context.contextMap);
		
		if(result==null) {
			result=new ArrayList<Map>();
		}
		return result;
	}
	
	public List<Map> showDetailByDeptId(Context context) {
		
		List<Map> result=this.getSqlMapClientTemplate().queryForList("caseReportService.showDetailByDeptId",context.contextMap);
		
		if(result==null) {
			result=new ArrayList<Map>();
		}
		return result;
	}
	
	public List<Map> getDetailAchievement(Context context) {
		
		List<Map> result=this.getSqlMapClientTemplate().queryForList("caseReportService.getDetailAchievement",context.contextMap);
		
		if(result==null) {
			result=new ArrayList<Map>();
		}
		return result;
	}
	
	public List<Map> getTotalTarget(Context context) {
		
		List<Map> result=this.getSqlMapClientTemplate().queryForList("caseReportService.getTotalTarget",context.contextMap);
		
		if(result==null) {
			result=new ArrayList<Map>();
		}
		return result;
	}
	
	public List<Map> getTotalAchievement(Context context) {
		
		List<Map> result=this.getSqlMapClientTemplate().queryForList("caseReportService.getTotalAchievement",context.contextMap);
		
		if(result==null) {
			result=new ArrayList<Map>();
		}
		return result;
	}
	
	public List<LoanTo> getLoanInfoGroupByDateDept(Context context) {
		
		List<LoanTo> result=this.getSqlMapClientTemplate().queryForList("loan.getLoanInfoGroupByDateDept",context.contextMap);
		
		if(result==null) {
			result=new ArrayList<LoanTo>();
		}
		return result;
	}
	
	public List<LoanTo> getLoanInfoGroupByUser(Context context) {
		
		List<LoanTo> result=this.getSqlMapClientTemplate().queryForList("loan.getLoanInfoGroupByUser",context.contextMap);
		
		if(result==null) {
			result=new ArrayList<LoanTo>();
		}
		return result;
	}
	
	public List<Map<String,Object>> getTargetByYear(Map<String,Object> param) throws Exception {
		List<Map<String,Object>> resultList=this.getSqlMapClientTemplate().queryForList("caseReportService.getTargetByYear",param);
		return resultList;
	}
	
	public List<Map<String,Object>> getAchievementByYear(Map<String,Object> param) throws Exception {
		List<Map<String,Object>> resultList=this.getSqlMapClientTemplate().queryForList("caseReportService.getAchievementByYear",param);
		return resultList;
	}
}
