package com.brick.bussinessReport.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.activityLog.to.LoanTo;
import com.brick.base.service.BaseService;
import com.brick.bussinessReport.dao.AchievementCompareDAO;
import com.brick.log.service.LogPrint;
import com.brick.service.entity.Context;

public class AchievementCompareService extends BaseService {

	Log logger = LogFactory.getLog(AchievementCompareService.class);

	private AchievementCompareDAO achievementCompareDAO;

	public AchievementCompareDAO getAchievementCompareDAO() {
		return achievementCompareDAO;
	}

	public void setAchievementCompareDAO(AchievementCompareDAO achievementCompareDAO) {
		this.achievementCompareDAO = achievementCompareDAO;
	}

	public int getCountWorkDayOfCurrentMonth(Context context) {

		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getCountWorkDayOfCurrentMonth";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}

		List<String> errList=context.errList;
		int result=0;
		
		try {
			result=this.achievementCompareDAO.getCountWorkDayOfCurrentMonth(context);
		} catch(Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
			errList.add("数据分析--各区同期比出错!请联系管理员(getCountWorkDayOfCurrentMonth)");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		return result;
	}
	
	public int getCountWorkDayOfLastMonth(Context context) {

		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getCountWorkDayOfLastMonth";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}

		List<String> errList=context.errList;
		int result=0;
		
		try {
			result=this.achievementCompareDAO.getCountWorkDayOfLastMonth(context);
		} catch(Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
			errList.add("数据分析--各区同期比出错!请联系管理员(getCountWorkDayOfLastMonth)");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		return result;
	}
	
	public List<Map<String,Object>> getLastMonthWorkDay(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getLastMonthWorkDay";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		List<String> errList=context.errList;
		List<Map<String,Object>> result=null;
		
		try {
			result=this.achievementCompareDAO.getLastMonthWorkDay(context);
		} catch(Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
			errList.add("数据分析--各区同期比出错!请联系管理员(getLastMonthWorkDay)");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		
		if(result==null) {
			result=new ArrayList<Map<String,Object>>();
		}
		return result;
	}
	
	public List<Map<String,Object>> getCompareAchievement(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getCompareAchievement";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		List<String> errList=context.errList;
		List<Map<String,Object>> result=null;
		
		try {
			result=this.achievementCompareDAO.getCompareAchievement(context);
		} catch(Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
			errList.add("数据分析--各区同期比出错!请联系管理员(getCompareAchievement)");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		
		if(result==null) {
			result=new ArrayList<Map<String,Object>>();
		}
		return result;
	}
	
	public List<Map<String,Object>> getCompareTarget(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getCompareTarget";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		List<String> errList=context.errList;
		List<Map<String,Object>> result=null;
		try {
			result=this.achievementCompareDAO.getCompareTarget(context);
		} catch(Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
			errList.add("数据分析--各区同期比出错!请联系管理员(getCompareTarget)");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		
		if(result==null) {
			result=new ArrayList<Map<String,Object>>();
		}
		return result;
	}
	
	public List<Map<String,Object>> getCurrentBeginEndDate(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getCurrentBeginEndDate";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		List<String> errList=context.errList;
		List<Map<String,Object>> result=null;
		
		try {
			result=this.achievementCompareDAO.getCurrentBeginEndDate(context);
		} catch(Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
			errList.add("数据分析--各区同期比出错!请联系管理员(getCurrentBeginEndDate)");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		
		if(result==null) {
			result=new ArrayList<Map<String,Object>>();
		}
		return result;
	}
	
	public List<Map<String,Object>> getLastMonthWorkDayForMonthSecond(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getLastMonthWorkDayForMonthSecond";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		List<String> errList=context.errList;
		List<Map<String,Object>> result=null;
		
		try {
			result=this.achievementCompareDAO.getLastMonthWorkDayForMonthSecond(context);
		} catch(Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
			errList.add("数据分析--各区同期比出错!请联系管理员(getLastMonthWorkDayForMonthSecond)");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		
		if(result==null) {
			result=new ArrayList<Map<String,Object>>();
		}
		return result;
	}
	
	public int getWorkDayCount(Context context) {

		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getWorkDayCount";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}

		List<String> errList=context.errList;

		int result=0;
		
		try {
			result=this.achievementCompareDAO.getWorkDayCount(context);
		} catch(Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
			errList.add("数据分析--各区同期比出错!请联系管理员(getWorkDayCount)");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		return result;
	}
	
	public List<Map<String,Object>> getLastDateOfMonth(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getLastDateOfMonth";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		List<String> errList=context.errList;
		List<Map<String,Object>> result=null;
		
		try {
			result=this.achievementCompareDAO.getLastDateOfMonth(context);
		} catch(Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
			errList.add("数据分析--各区同期比出错!请联系管理员(getLastDateOfMonth)");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		
		if(result==null) {
			result=new ArrayList<Map<String,Object>>();
		}
		return result;
	}
	
	public List<LoanTo> getLoanInfoByPeriod(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getLoanInfoByPeriod";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		List<String> errList=context.errList;
		List<LoanTo> result=null;
		
		try {
			result=this.achievementCompareDAO.getLoanInfoByPeriod(context);
		} catch(Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
			errList.add("数据分析--各区同期比出错!请联系管理员(getLoanInfoByPeriod)");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		
		if(result==null) {
			result=new ArrayList<LoanTo>();
		}
		return result;
	}
}
