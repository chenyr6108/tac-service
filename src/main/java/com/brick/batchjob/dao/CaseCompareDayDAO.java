package com.brick.batchjob.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;
import com.brick.base.exception.DaoException;
import com.brick.batchjob.to.CaseCompareDayTo;

public class CaseCompareDayDAO extends BaseDAO {

	public List<CaseCompareDayTo> getCaseCompareDay(Map<String,String> param) throws DaoException {
		
		List<CaseCompareDayTo> resultList=null;
		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getCaseCompareDay",param);
		
		if(resultList==null) {
			resultList=new ArrayList<CaseCompareDayTo>();
		}
		
		return resultList;
	}
	
	public void insertCaseCompareDay(CaseCompareDayTo caseCompareDayTo) throws DaoException {
		
		this.getSqlMapClientTemplate().insert("businessReport.insertCaseCompareDay",caseCompareDayTo);
	}
	
	public List<CaseCompareDayTo> queryCaseCompareDay(Map<String,Object> param) throws DaoException {
		
		List<CaseCompareDayTo> resultList=null;
		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.queryCaseCompareDay",param);
				
		if(resultList==null) {
			resultList=new ArrayList<CaseCompareDayTo>();
		}
		return resultList;
	}
	
	public Map<String,List<CaseCompareDayTo>> getCaseCompareDayFilter() throws DaoException {
		
		Map<String,List<CaseCompareDayTo>> resultMap=new HashMap<String,List<CaseCompareDayTo>>();
		
		List<CaseCompareDayTo> dateList=this.getSqlMapClientTemplate().queryForList("businessReport.getCaseCompareDateList");
		List<CaseCompareDayTo> userList=this.getSqlMapClientTemplate().queryForList("businessReport.getCaseCompareUserList");
		List<CaseCompareDayTo> deptList=this.getSqlMapClientTemplate().queryForList("businessReport.getCaseCompareDeptList");
		
		resultMap.put("dateList",dateList==null?new ArrayList<CaseCompareDayTo>():dateList);
		resultMap.put("userList",userList==null?new ArrayList<CaseCompareDayTo>():userList);
		resultMap.put("deptList",deptList==null?new ArrayList<CaseCompareDayTo>():deptList);
		
		return resultMap;
	}
}
