package com.brick.batchjob.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;
import com.brick.base.exception.DaoException;
import com.brick.batchjob.to.SuplCustCaseTo;
import com.brick.service.entity.Context;

public class SupplerCustomerCaseBatchJobDAO extends BaseDAO {

	public List<SuplCustCaseTo> getSuplCustCase() throws Exception {
		
		List<SuplCustCaseTo> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getSuplCustCase");
				
		if(resultList==null) {
			resultList=new ArrayList<SuplCustCaseTo>();
		}
		
		return resultList;
	}
	
	public List<SuplCustCaseTo> getDunMoneyByCust() throws Exception {
		
		List<SuplCustCaseTo> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getDunMoneyByCust");
				
		if(resultList==null) {
			resultList=new ArrayList<SuplCustCaseTo>();
		}
		
		return resultList;
	}
	
	public void insertSupplerCustomerCase(SuplCustCaseTo suplCustCaseTo) {
		
		this.getSqlMapClientTemplate().insert("businessReport.insertSupplerCustomerCase",suplCustCaseTo);
	}
	

	public List<Map<String,Object>> getCaseTypeList(Context context) throws Exception {

		List<Map<String,Object>> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getCaseTypeList",context.contextMap);

		if(resultList==null) {
			resultList=new ArrayList<Map<String,Object>>();
		}

		return resultList;
	}
	
}
