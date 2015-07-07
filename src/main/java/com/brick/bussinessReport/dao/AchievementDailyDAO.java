package com.brick.bussinessReport.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.brick.activityLog.to.LoanTo;
import com.brick.base.dao.BaseDAO;
import com.brick.service.entity.Context;

public class AchievementDailyDAO extends BaseDAO {
	
	public List<Map<String,Object>> getDailyAchievement(Context context) {

		List<Map<String,Object>> result=this.getSqlMapClientTemplate().queryForList("caseReportService.getDailyAchievement",context.contextMap);

		if(result==null) {
			result=new ArrayList<Map<String,Object>>();
		}
		return result;

	}
	
	public List<Map<String,Object>> getDailyCount() {
		
		List<Map<String,Object>> result=this.getSqlMapClientTemplate().queryForList("caseReportService.getDailyCount");

		if(result==null) {
			result=new ArrayList<Map<String,Object>>();
		}
		return result;
		
	}
	
	public List<LoanTo> getLoanInfoGroupByDate() throws Exception {
		
		List<LoanTo> result=this.getSqlMapClientTemplate().queryForList("loan.getLoanInfoGroupByDate");

		if(result==null) {
			result=new ArrayList<LoanTo>();
		}
		return result;
	}
	
	public List<LoanTo> getLoanCountGroupByDate() throws Exception {
		
		List<LoanTo> result=this.getSqlMapClientTemplate().queryForList("loan.getLoanCountGroupByDate");

		if(result==null) {
			result=new ArrayList<LoanTo>();
		}
		return result;
	}
}
