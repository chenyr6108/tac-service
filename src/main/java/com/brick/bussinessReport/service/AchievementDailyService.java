package com.brick.bussinessReport.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.activityLog.to.LoanTo;
import com.brick.base.service.BaseService;
import com.brick.bussinessReport.dao.AchievementDailyDAO;
import com.brick.log.service.LogPrint;
import com.brick.service.entity.Context;

public class AchievementDailyService extends BaseService {

	Log logger = LogFactory.getLog(AchievementDailyService.class);
	
	private AchievementDailyDAO achievementDailyDAO;

	public AchievementDailyDAO getAchievementDailyDAO() {
		return achievementDailyDAO;
	}

	public void setAchievementDailyDAO(AchievementDailyDAO achievementDailyDAO) {
		this.achievementDailyDAO = achievementDailyDAO;
	}
	
	public List<Map<String,Object>> getDailyAchievement(Context context) {

		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getDailyAchievement";
		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}

		List<String> errList=context.errList;
		List<Map<String,Object>> result=null;
		
		try {
			result=this.achievementDailyDAO.getDailyAchievement(context);
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
			errList.add("数据分析--日业绩表出错!请联系管理员(getDailyAchievement)");
		}

		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}

		if(result==null) {
			result=new ArrayList<Map<String,Object>>();
		}
		
		return result;
	}
	
	public List<Map<String,Object>> getDailyCount(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getDailyCount";
		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}

		List<String> errList=context.errList;
		List<Map<String,Object>> result=null;
		
		try {
			result=this.achievementDailyDAO.getDailyCount();
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
			errList.add("数据分析--日业绩表出错!请联系管理员(getOverlapCount)");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		
		if(result==null) {
			result=new ArrayList<Map<String,Object>>();
		}
		
		return result;
	}
	
	public List<LoanTo> getLoanInfoGroupByDate(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getLoanInfoGroupByDate";
		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}

		List<String> errList=context.errList;
		List<LoanTo> result=null;
		
		try {
			result=this.achievementDailyDAO.getLoanInfoGroupByDate();
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
			errList.add("数据分析--日业绩表出错!请联系管理员(getLoanInfoGroupByDate)");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		
		if(result==null) {
			result=new ArrayList<LoanTo>();
		}
		
		return result;
	}
	
	public List<LoanTo> getLoanCountGroupByDate(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getLoanCountGroupByDate";
		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}

		List<String> errList=context.errList;
		List<LoanTo> result=null;
		
		try {
			result=this.achievementDailyDAO.getLoanCountGroupByDate();
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
			errList.add("数据分析--日业绩表出错!请联系管理员(getLoanCountGroupByDate)");
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
