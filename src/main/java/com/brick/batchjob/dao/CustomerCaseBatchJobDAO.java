package com.brick.batchjob.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;
import com.brick.base.exception.DaoException;
import com.brick.batchjob.to.CustomerCaseTo;
import com.brick.service.entity.Context;

public class CustomerCaseBatchJobDAO extends BaseDAO {

	public List<CustomerCaseTo> getCustCaseInfo() throws DaoException {

		List<CustomerCaseTo> resultList=null;

		Map<String,String> param=new HashMap<String,String>();
		param.put("TYPE","融资租赁合同类型");

		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getCustCaseInfo",param);

		if(resultList==null) {
			resultList=new ArrayList<CustomerCaseTo>();
		}

		return resultList;
	}

	public void insertCustCaseData(CustomerCaseTo customerCaseTo) {

		this.getSqlMapClientTemplate().insert("businessReport.insertCustCaseData",customerCaseTo);
	}

	public List<CustomerCaseTo> getDeptList(Context context) throws DaoException {

		List<CustomerCaseTo> resultList=null;

		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getDeptList",context.contextMap);

		if(resultList==null) {
			resultList=new ArrayList<CustomerCaseTo>();
		}

		return resultList;
	}
	
	public List<Map<String,Object>> getCaseTypeList(Context context) throws DaoException {

		List<Map<String,Object>> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getCaseTypeList",context.contextMap);

		if(resultList==null) {
			resultList=new ArrayList<Map<String,Object>>();
		}

		return resultList;
	}
}
