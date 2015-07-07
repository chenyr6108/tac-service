package com.brick.bussinessReport.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.brick.activityLog.to.LoanTo;
import com.brick.base.dao.BaseDAO;
import com.brick.service.entity.Context;

public class AchievementCompareDAO extends BaseDAO {

	public int getCountWorkDayOfCurrentMonth(Context context) {

		int result=(Integer)this.getSqlMapClientTemplate().queryForObject("caseReportService.getCountWorkDayOfCurrentMonth",context.contextMap);

		return result;

	}
	
	public int getCountWorkDayOfLastMonth(Context context) {

		int result=(Integer)this.getSqlMapClientTemplate().queryForObject("caseReportService.getCountWorkDayOfLastMonth",context.contextMap);

		return result;

	}

	public List<Map<String,Object>> getLastMonthWorkDay(Context context) {

		List<Map<String,Object>> result=(List<Map<String,Object>>)this.getSqlMapClientTemplate().queryForList("caseReportService.getLastMonthWorkDay",context.contextMap);

		if(result==null) {
			return new ArrayList<Map<String,Object>>();
		}

		return result;
	}

	public List<Map<String,Object>> getCompareAchievement(Context context) {

		List<Map<String,Object>> result=(List<Map<String,Object>>)this.getSqlMapClientTemplate().queryForList("caseReportService.getCompareAchievement",context.contextMap);

		if(result==null) {
			return new ArrayList<Map<String,Object>>();
		}

		return result;
	}

	public List<Map<String,Object>> getCompareTarget(Context context) {

		List<Map<String,Object>> result=(List<Map<String,Object>>)this.getSqlMapClientTemplate().queryForList("caseReportService.getCompareTarget",context.contextMap);

		if(result==null) {
			return new ArrayList<Map<String,Object>>();
		}

		return result;
	}
	
	public List<Map<String,Object>> getCurrentBeginEndDate(Context context) {
		
		List<Map<String,Object>> result=(List<Map<String,Object>>)this.getSqlMapClientTemplate().queryForList("caseReportService.getCurrentBeginEndDate",context.contextMap);

		if(result==null) {
			return new ArrayList<Map<String,Object>>();
		}

		return result;
	}
	
	public List<Map<String,Object>> getLastMonthWorkDayForMonthSecond(Context context) {
		
		List<Map<String,Object>> result=(List<Map<String,Object>>)this.getSqlMapClientTemplate().queryForList("caseReportService.getLastMonthWorkDayForMonthSecond",context.contextMap);

		if(result==null) {
			return new ArrayList<Map<String,Object>>();
		}

		return result;
	}
	
	public int getWorkDayCount(Context context) {

		int result=(Integer)this.getSqlMapClientTemplate().queryForObject("caseReportService.getWorkDayCount",context.contextMap);

		return result;

	}
	
	public List<Map<String,Object>> getLastDateOfMonth(Context context) {
		
		List<Map<String,Object>> result=(List<Map<String,Object>>)this.getSqlMapClientTemplate().queryForList("caseReportService.getLastDateOfMonth",context.contextMap);

		if(result==null) {
			return new ArrayList<Map<String,Object>>();
		}

		return result;
	}
	
	public List<LoanTo> getLoanInfoByPeriod(Context context) {
		
		List<LoanTo> result=(List<LoanTo>)this.getSqlMapClientTemplate().queryForList("loan.getLoanInfoByPeriod",context.contextMap);

		if(result==null) {
			return new ArrayList<LoanTo>();
		}

		return result;
	}
}
