package com.brick.financialReport.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;

public class InsuranceFeeRemainderReportDAO extends BaseDAO {

	public List<Map<String,Object>> getHistoryInsuranceFeeRemainderPay(Map<String,Object> param) throws Exception {
		
		List<Map<String,Object>> resultList=null;
		
		resultList=this.getSqlMapClient().queryForList("financialReport.getHistoryInsuranceFeeRemainderPay",param);
		if(resultList==null) {
			resultList=new ArrayList<Map<String,Object>>();
		}
		
		return resultList;
	}
	
	public int checkHistoryInsuranceFeeRemainderPay(Map<String,Object> param) throws Exception {
		return (Integer)this.getSqlMapClientTemplate().queryForObject("financialReport.checkHistoryInsuranceFeeRemainderPay",param);
	}
	
	public void cancelHistoryInsuranceFeeRemainderPay(Map<String,Object> param) throws Exception {
		this.getSqlMapClientTemplate().update("financialReport.cancelHistoryInsuranceFeeRemainderPay",param);
	}
	
	public void cancelHistoryInsuranceFeeRemainderFinancial(Map<String,Object> param) throws Exception {
		this.getSqlMapClientTemplate().update("financialReport.cancelHistoryInsuranceFeeRemainderFinancial",param);
	}
	
	public void generateHistoryInsuranceFeeRemainderPay(Map<String,Object> param) throws Exception {
		
		this.getSqlMapClient().insert("financialReport.generateHistoryInsuranceFeeRemainderPay",param);
	}
	
	public List<Map<String,Object>> getCurrentInsuranceFeeRemainderPay(Map<String,Object> param) throws Exception {
		
		List<Map<String,Object>> resultList=null;
		
		resultList=this.getSqlMapClient().queryForList("financialReport.getCurrentInsuranceFeeRemainderPay",param);
		if(resultList==null) {
			resultList=new ArrayList<Map<String,Object>>();
		}
		
		return resultList;
	}
	
	public List<Map<String,Object>> getCurrentInsuranceFeeRemainderPaySpecial() throws Exception {
		
		List<Map<String,Object>> resultList=null;
		
		resultList=this.getSqlMapClient().queryForList("financialReport.getCurrentInsuranceFeeRemainderPaySpecial");
		if(resultList==null) {
			resultList=new ArrayList<Map<String,Object>>();
		}
		
		return resultList;
	}
	
	public List<Map<String,Object>> getCurrentInsuranceFeeRemainderPayByRecpId(Map<String,Object> param) throws Exception {
		
		List<Map<String,Object>> resultList=null;
		
		resultList=this.getSqlMapClient().queryForList("financialReport.getCurrentInsuranceFeeRemainderPayByRecpId",param);
		if(resultList==null) {
			resultList=new ArrayList<Map<String,Object>>();
		}
		
		return resultList;
	}
	
	public Map<String,Object> getOtherInformation(Map<String,Object> param) throws Exception {
		
		return (Map<String,Object>)this.getSqlMapClient().queryForObject("financialReport.getOtherInformation",param);
	}
	
	public void generateInsuranceFeeRemainderReport(Map<String,Object> param) throws Exception {
		this.getSqlMapClient().insert("financialReport.generateInsuranceFeeRemainderReport",param);
	}
	
	public int getLastDayOfMonth() throws Exception {
		return (Integer)this.getSqlMapClient().queryForObject("financialReport.getLastDayOfMonth");
	}
	
	public List<String> getDateList() throws Exception {
		return (List<String>)this.getSqlMapClient().queryForList("financialReport.getDateList");
	}
	
	public List<Map<String,Object>> getCurrentInsuranceFeeRemainderPayByRecpCode(Map<String,Object> param) throws Exception {
		
		List<Map<String,Object>> resultList=null;
		
		resultList=this.getSqlMapClient().queryForList("financialReport.getCurrentInsuranceFeeRemainderPayByRecpCode",param);
		if(resultList==null) {
			resultList=new ArrayList<Map<String,Object>>();
		}
		
		return resultList;
	}
}
