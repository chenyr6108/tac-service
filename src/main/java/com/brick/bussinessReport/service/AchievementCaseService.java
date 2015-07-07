package com.brick.bussinessReport.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.service.BaseService;
import com.brick.bussinessReport.dao.AchievementCaseDAO;
import com.brick.bussinessReport.to.AchievementTo;
import com.brick.log.service.LogPrint;
import com.brick.service.entity.Context;

public class AchievementCaseService extends BaseService {

	Log logger = LogFactory.getLog(AchievementCaseService.class);
	
	private AchievementCaseDAO achievementCaseDAO;

	public AchievementCaseDAO getAchievementCaseDAO() {
		return achievementCaseDAO;
	}

	public void setAchievementCaseDAO(AchievementCaseDAO achievementCaseDAO) {
		this.achievementCaseDAO = achievementCaseDAO;
	}
	
	public List<AchievementTo> getPayMoney() {
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getPayMoney start.....");
		}
		
		List<AchievementTo> result=null;
		
		try {
			result=this.achievementCaseDAO.getPayMoney();
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getPayMoney end.....");
		}
		
		return result;
	}
	
	public List<AchievementTo> getPayCount() {
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getPayCount start.....");
		}
		
		List<AchievementTo> result=null;
		
		try {
			result=this.achievementCaseDAO.getPayCount();
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getPayCount end.....");
		}
		
		return result;
	}
	
	public List<AchievementTo> getApproveCount() {
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getApproveCount start.....");
		}
		
		List<AchievementTo> result=null;
		
		try {
			result=this.achievementCaseDAO.getApproveCount();
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getApproveCount end.....");
		}
		
		return result;
	}
	
	public List<AchievementTo> getPendingApproveCount() {
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getPendingApproveCount start.....");
		}
		
		List<AchievementTo> result=null;
		
		try {
			result=this.achievementCaseDAO.getPendingApproveCount();
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getPendingApproveCount end.....");
		}
		
		return result;
	}
	
	public List<AchievementTo> getCautionCount() {
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getCautionCount start.....");
		}
		
		List<AchievementTo> result=null;
		Map<String,String> paramMap=new HashMap<String,String>();
		paramMap.put("TYPE","保证金");
		
		try {
			result=this.achievementCaseDAO.getCautionCount(paramMap);
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getCautionCount end.....");
		}
		
		return result;
	}
	
	public List<AchievementTo> getAuditCount() {
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getAuditCount start.....");
		}
		
		List<AchievementTo> result=null;
		
		try {
			result=this.achievementCaseDAO.getAuditCount();
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getAuditCount end.....");
		}
		
		return result;
	}
	
	public List<AchievementTo> getHasApproveCountAmount() {
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getHasApproveCountAmount start.....");
		}
		
		List<AchievementTo> result=null;
		
		try {
			result=this.achievementCaseDAO.getHasApproveCountAmount();
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getHasApproveCountAmount end.....");
		}
		
		return result;
	}
	
	public List<AchievementTo> getAchievementMoney(Map<String,Object> param) {
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getAchievementMoney start.....");
		}
		
		List<AchievementTo> result=null;
		
		try {
			result=this.achievementCaseDAO.getAchievementMoney(param);
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getAchievementMoney end.....");
		}
		
		return result;
	}
	
	public List<AchievementTo> getAchievementCount(Map<String,Object> param) {
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getAchievementCount start.....");
		}
		
		List<AchievementTo> result=null;
		
		try {
			result=this.achievementCaseDAO.getAchievementCount(param);
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getAchievementCount end.....");
		}
		
		return result;
	}
	
	public List<AchievementTo> getTargetMoney() {
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getTargetMoney start.....");
		}
		
		List<AchievementTo> result=null;
		
		try {
			result=this.achievementCaseDAO.getTargetMoney();
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getTargetMoney end.....");
		}
		
		return result;
	}
	
	public void insertData(AchievementTo achievementTo) {
		
		try {
			this.achievementCaseDAO.insertData(achievementTo);
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
		}
	}
	
	public List<AchievementTo> query(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......query";
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		List<AchievementTo> result=null;
		
		try {
			result=this.achievementCaseDAO.query(context);
		} catch (Exception e) {
			context.errList.add("数据分析--客户案况汇总表出错!请联系管理员(query)");
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		
		return result;
	}
	
	public List<AchievementTo> getLastAchievementCountMoney(Map<String,Object> param) {
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getLastAchievementCountMoney start.....");
		}
		List<AchievementTo> resultList=null;
		
		try {
			resultList=this.achievementCaseDAO.getLastAchievementCountMoney(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(resultList==null) {
			resultList=new ArrayList<AchievementTo>();
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getLastAchievementCountMoney end.....");
		}
		return resultList;
	}
	
	public List<AchievementTo> getNextDayPayMoney(String date) {
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getNextDayPayMoney start.....");
		}
		List<AchievementTo> resultList=null;
		
		try {
			resultList=this.achievementCaseDAO.getNextDayPayMoney(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(resultList==null) {
			resultList=new ArrayList<AchievementTo>();
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getNextDayPayMoney end.....");
		}
		return resultList;
	}
	
	public List<AchievementTo> getFinanceDateInOneDayCount() {
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getFinanceDateInOneDayCount start.....");
		}
		
		List<AchievementTo> resultList=null;
		try {
			resultList=this.achievementCaseDAO.getFinanceDateInOneDayCount();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(resultList==null) {
			resultList=new ArrayList<AchievementTo>();
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getFinanceDateInOneDayCount end.....");
		}
		return resultList;
	}
	
	public AchievementTo getFinanceDateInOneDayMoney(AchievementTo achievementTo) {
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getFinanceDateInOneDayMoney start.....");
		}
		
		AchievementTo result=null;
		try {
			result=this.achievementCaseDAO.getFinanceDateInOneDayMoney(achievementTo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(result==null) {
			result=new AchievementTo();
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getFinanceDateInOneDayMoney end.....");
		}
		return result;
	}
	
	public AchievementTo getNextWorkDay(String date) {
		
		AchievementTo result=null;
		try {
			result=this.achievementCaseDAO.getNextWorkDay(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(result==null) {
			result=new AchievementTo();
		}
		
		return result;
	}
	
	public List<AchievementTo> getInfoAcessAuditApprove() {
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getInfoAcessAuditApprove start.....");
		}
		
		List<AchievementTo> resultList=null;
		try {
			resultList=this.achievementCaseDAO.getInfoAcessAuditApprove();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(resultList==null) {
			resultList=new ArrayList<AchievementTo>();
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getInfoAcessAuditApprove end.....");
		}
		return resultList;
	}
	
	public List<AchievementTo> queryAchievementCase(java.sql.Date beginTime,java.sql.Date endTime) throws Exception{
		
		if(logger.isDebugEnabled()) {
			logger.debug("......queryAchievementCase start.....");
		}
		List<AchievementTo> list = achievementCaseDAO.queryAchievementCase(beginTime, endTime);
		
		if(logger.isDebugEnabled()) {
			logger.debug("......queryAchievementCase end.....");
		}
		return list;
	}
	
	public List<Map<String,String>> queryCreditSpecialCode() throws Exception{
		if(logger.isDebugEnabled()) {
			logger.debug("......queryCreditSpecialCode start.....");
		}
		
		List<Map<String,String>> list = achievementCaseDAO.queryCreditSpecialCode();
		
		if(logger.isDebugEnabled()) {
			logger.debug("......queryCreditSpecialCode end.....");
		}
		return list;
	}
	
	
	public AchievementTo getLoanMoneyToday(Context context) throws Exception {
		
		return this.achievementCaseDAO.getLoanMoneyToday(context);
	
	}
	
	public AchievementTo getLoanMoneyMonth(Context context) throws Exception {
		
		return this.achievementCaseDAO.getLoanMoneyMonth(context);
	}
}
