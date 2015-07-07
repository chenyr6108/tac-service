package com.brick.financialReport.dao;

import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;

public class InterestIncomeReportDAO extends BaseDAO {

	public List<Map<String,Object>> getHistoryInterestIncomePay(Map<String,Object> param) {
		return this.getSqlMapClientTemplate().queryForList("financialReport.getHistoryInterestIncomePay",param);
	}
	
	public void generateHistoryInterestIncomePay(Map<String,Object> param) {
		this.getSqlMapClientTemplate().insert("financialReport.generateHistoryInterestIncomePay",param);
	}
	
	public int checkHistoryInterestIncomePay(Map<String,Object> param) {
		return (Integer)this.getSqlMapClientTemplate().queryForObject("financialReport.checkHistoryInterestIncomePay",param);
	}
	
	public void cancelHistoryInterestIncomePay(Map<String,Object> param) {
		this.getSqlMapClientTemplate().update("financialReport.cancelHistoryInterestIncomePay",param);
	}
}
