package com.brick.batchjob.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;

public class ReportCancelBatchJobDAO extends BaseDAO {

	public List<Integer> getInvalidReportId() {
		
		List<Integer> result=this.getSqlMapClientTemplate().queryForList("creditReportManage.getInvalidReportId");
		
		if(result==null) {
			result=new ArrayList<Integer>();
		}
		
		return result;
	}
	
	public void cancelCreditByCreditId(Map<String,Object> param) {
		
		this.getSqlMapClientTemplate().update("creditReportManage.cancelCreditByCreditId",param);
	}
	
	public void cancelCaseByCreditId(Map<String,Object> param) {
		
		this.getSqlMapClientTemplate().update("creditReportManage.cancelCaseByCreditId",param);
	}
	
	public void insertLog(Map<String,Object> param) {
		
		this.getSqlMapClientTemplate().insert("sysBusinessLog.add",param);
	}
}
