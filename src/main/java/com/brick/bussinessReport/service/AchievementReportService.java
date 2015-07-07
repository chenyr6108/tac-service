package com.brick.bussinessReport.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.activityLog.to.LoanTo;
import com.brick.base.service.BaseService;
import com.brick.bussinessReport.dao.AchievementReportDAO;
import com.brick.log.service.LogPrint;
import com.brick.service.entity.Context;

public class AchievementReportService extends BaseService {

	Log logger = LogFactory.getLog(AchievementReportService.class);

	private AchievementReportDAO achievementReportDAO;

	public AchievementReportDAO getAchievementReportDAO() {
		return achievementReportDAO;
	}

	public void setAchievementReportDAO(AchievementReportDAO achievementReportDAO) {
		this.achievementReportDAO = achievementReportDAO;
	}

	public List<Map> query(Context context) {

		String log="employeeId="+context.contextMap.get("s_employeeId")+"......query";
		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}

		List<String> errList=context.errList;
		List<Map> result=null;
		
		try {
			result=this.achievementReportDAO.query(context);
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
			errList.add("数据分析--各区业绩报表出错!请联系管理员");
		}

		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}

		if(result==null) {
			result=new ArrayList<Map>();
		}
		
		return result;
	}

	public List<String> getYearAchievementReport(Context context) {

		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getYearAchievementReport";
		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}

		List<String> errList=context.errList;
		List<String> result=null;
		try {
			result=this.achievementReportDAO.getYearAchievementReport();
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
			errList.add("数据分析--各区业绩报表出错!请联系管理员");
		}

		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}

		if(result==null) {
			result=new ArrayList<String>();
		}
		return result;
	}
	
	public List<Map> getDeptList(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getDeptList";
		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		List<String> errList=context.errList;
		List<Map> result=null;
		
		try {
			result=this.achievementReportDAO.getDeptList(context);
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
			errList.add("数据分析--各区业绩报表出错!请联系管理员");
		}

		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}

		if(result==null) {
			result=new ArrayList<Map>();
		}
		
		return result;
	}
	
	public List<Map> getAchievementTotal(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getAchievementTotal";
		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		List<String> errList=context.errList;
		List<Map> result=null;
		
		try {
			result=this.achievementReportDAO.getAchievementTotal(context);
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
			errList.add("数据分析--各区业绩报表出错!请联系管理员");
		}

		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}

		if(result==null) {
			result=new ArrayList<Map>();
		}
		
		return result;
	}
	
	public List<Map> showDetailByDeptId(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......showDetailByDeptId";
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		List<String> errList=context.errList;
		List<Map> result=null;
		
		try {
			result=this.achievementReportDAO.showDetailByDeptId(context);
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
			errList.add("数据分析--各区业绩报表出错!请联系管理员");
		}

		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}

		if(result==null) {
			result=new ArrayList<Map>();
		}
		
		return result;
		
	}
	
	public List<Map> getDetailAchievement(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getDetailAchievement";
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		List<String> errList=context.errList;
		List<Map> result=null;
		
		try {
			result=this.achievementReportDAO.getDetailAchievement(context);
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
			errList.add("数据分析--各区业绩报表出错!请联系管理员");
		}

		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}

		if(result==null) {
			result=new ArrayList<Map>();
		}
		
		return result;
		
	}
	
	public List<Map> getTotalTarget(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getTotalTarget";
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		List<String> errList=context.errList;
		List<Map> result=null;
		
		try {
			result=this.achievementReportDAO.getTotalTarget(context);
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
			errList.add("数据分析--各区业绩报表出错!请联系管理员");
		}

		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}

		if(result==null) {
			result=new ArrayList<Map>();
		}
		
		return result;
		
	}
	
	public List<Map> getTotalAchievement(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getTotalAchievement";
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		List<String> errList=context.errList;
		List<Map> result=null;
		try {
			result=this.achievementReportDAO.getTotalAchievement(context);
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
			errList.add("数据分析--各区业绩报表出错!请联系管理员");
		}

		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}

		if(result==null) {
			result=new ArrayList<Map>();
		}
		
		return result;
		
	}
	
	public List<LoanTo> getLoanInfoGroupByDateDept(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getLoanInfoGroupByDateDept";
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		List<String> errList=context.errList;
		List<LoanTo> result=null;
		
		try {
			result=this.achievementReportDAO.getLoanInfoGroupByDateDept(context);
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
			errList.add("数据分析--各区业绩报表出错!请联系管理员(getLoanInfoGroupByDateDept)");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		
		return result;
	}
	
	public List<LoanTo> getLoanInfoGroupByUser(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getLoanInfoGroupByUser";
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		List<String> errList=context.errList;
		List<LoanTo> result=null;
		
		try {
			result=this.achievementReportDAO.getLoanInfoGroupByUser(context);
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
			errList.add("数据分析--各区业绩报表出错!请联系管理员(getLoanInfoGroupByUser)");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		
		return result;
	}
	
	public List<Map<String,Object>> getTargetByYear(Map<String,Object> param) throws Exception {
		return this.achievementReportDAO.getTargetByYear(param);
	}
	public List<Map<String,Object>> getAchievementByYear(Map<String,Object> param) throws Exception {
		return this.achievementReportDAO.getAchievementByYear(param);
	}
}
