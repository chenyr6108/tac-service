package com.brick.bussinessReport.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;
import com.brick.bussinessReport.to.AchievementTo;
import com.brick.service.entity.Context;

public class AchievementCaseDAO extends BaseDAO {

	public List<AchievementTo> getPayMoney() throws Exception {
		
		List<AchievementTo> resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getPayMoney");
		
		if(resultList==null) {
			resultList=new ArrayList<AchievementTo>();
		}
		
		return resultList;
	}
	
	public List<AchievementTo> getPayCount() throws Exception {
		
		List<AchievementTo> resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getPayCount");
		
		if(resultList==null) {
			resultList=new ArrayList<AchievementTo>();
		}
		
		return resultList;
	}
	
	public List<AchievementTo> getApproveCount() throws Exception {
		
		List<AchievementTo> resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getApproveCount");
		
		if(resultList==null) {
			resultList=new ArrayList<AchievementTo>();
		}
		
		return resultList;
	}
	
	public List<AchievementTo> getPendingApproveCount() throws Exception {
		
		List<AchievementTo> resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getPendingApproveCount");
		
		if(resultList==null) {
			resultList=new ArrayList<AchievementTo>();
		}
		
		return resultList;
	}
	
	public List<AchievementTo> getCautionCount(Map<String,String> paramMap) throws Exception {
		
		List<AchievementTo> resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getCautionCount",paramMap);
		
		if(resultList==null) {
			resultList=new ArrayList<AchievementTo>();
		}
		
		return resultList;
	}
	
	public List<AchievementTo> getAuditCount() throws Exception {
		
		List<AchievementTo> resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getAuditCount");
		
		if(resultList==null) {
			resultList=new ArrayList<AchievementTo>();
		}
		
		return resultList;
	}
	
	public List<AchievementTo> getHasApproveCountAmount() throws Exception {
		
		List<AchievementTo> resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getHasApproveCountAmount");
		
		if(resultList==null) {
			resultList=new ArrayList<AchievementTo>();
		}
		
		return resultList;
	}
	
	public List<AchievementTo> getAchievementMoney(Map<String,Object> param) throws Exception {
		
		List<AchievementTo> resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getAchievementMoney",param);
		
		if(resultList==null) {
			resultList=new ArrayList<AchievementTo>();
		}
		
		return resultList;
	}
	
	public List<AchievementTo> getAchievementCount(Map<String,Object> param) throws Exception {
		
		List<AchievementTo> resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getAchievementCount",param);
		
		if(resultList==null) {
			resultList=new ArrayList<AchievementTo>();
		}
		
		return resultList;
	}
	
	public List<AchievementTo> getTargetMoney() throws Exception {
		
		List<AchievementTo> resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getTargetMoney");
		
		if(resultList==null) {
			resultList=new ArrayList<AchievementTo>();
		}
		
		return resultList;
	}
	
	public void insertData(AchievementTo achievementTo) throws Exception {
		
		this.getSqlMapClientTemplate().insert("businessReport.insertData",achievementTo);
	}
	
	public List<AchievementTo> query(Context context) throws Exception {
		
		List<AchievementTo> resultList=this.getSqlMapClientTemplate().queryForList("businessReport.query",context.contextMap);
		
		if(resultList==null) {
			resultList=new ArrayList<AchievementTo>();
		}
		
		return resultList;
	}
	
	public List<AchievementTo> getLastAchievementCountMoney(Map<String,Object> param) throws Exception {
		
		List<AchievementTo> resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getLastAchievementCountMoney",param);
		
		if(resultList==null) {
			resultList=new ArrayList<AchievementTo>();
		}
		
		return resultList;
	}
	
	public List<AchievementTo> getNextDayPayMoney(String date) throws Exception {
		
		Map<String,String> param=new HashMap<String,String>();
		param.put("DATE",date);
		List<AchievementTo> resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getNextDayPayMoney",param);
		
		if(resultList==null) {
			resultList=new ArrayList<AchievementTo>();
		}
		
		return resultList;
	}
	
	public List<AchievementTo> getFinanceDateInOneDayCount() throws Exception {
		
		List<AchievementTo> resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getFinanceDateInOneDayCount");
		
		if(resultList==null) {
			resultList=new ArrayList<AchievementTo>();
		}
		
		return resultList;
	}
	
	public AchievementTo getFinanceDateInOneDayMoney(AchievementTo achievementTo) throws Exception {
		
		AchievementTo result=(AchievementTo)this.getSqlMapClientTemplate().queryForObject("businessReport.getFinanceDateInOneDayMoney",achievementTo);
		
		if(result==null) {
			result=new AchievementTo();
		}
		
		return result;
	}
	
	public AchievementTo getNextWorkDay(String date) throws Exception {
		
		Map<String,String> param=new HashMap<String,String>();
		param.put("DATE",date);
		
		AchievementTo result=(AchievementTo)this.getSqlMapClientTemplate().queryForObject("businessReport.getNextWorkDay",param);
		
		if(result==null) {
			result=new AchievementTo();
		}
		
		return result;
	}
	
	public List<AchievementTo> getInfoAcessAuditApprove() throws Exception {
		
		List<AchievementTo> resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getInfoAcessAuditApprove");
		
		if(resultList==null) {
			resultList=new ArrayList<AchievementTo>();
		}
		
		return resultList;
	}
	
	public AchievementTo getLoanMoneyToday(Context context) throws Exception {
		
		AchievementTo result=(AchievementTo)this.getSqlMapClientTemplate().queryForObject("businessReport.getLoanMoneyToday",context.contextMap);
	
		return result;
	}
	
	public AchievementTo getLoanMoneyMonth(Context context) throws Exception {
		
		AchievementTo result=(AchievementTo)this.getSqlMapClientTemplate().queryForObject("businessReport.getLoanMoneyMonth",context.contextMap);
	
		return result;
	}
	
	public List<AchievementTo> queryAchievementCase(java.sql.Date beginTime,java.sql.Date endTime) throws Exception{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("beginTime", beginTime);
		params.put("endTime", endTime);
		return this.getSqlMapClientTemplate().queryForList("businessReport.queryAchievementCase",params);
	}
	
	public List<Map<String,String>> queryCreditSpecialCode() throws Exception{
		return this.getSqlMapClientTemplate().queryForList("businessReport.queryCreditSpecialCode");
	}
}
