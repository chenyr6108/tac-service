package com.brick.unnaturalCase.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;
import com.brick.unnaturalCase.to.UnnaturalCaseTO;

public class UnnaturalCaseDAO extends BaseDAO {
	
	public List<UnnaturalCaseTO> getCaseCompare(Map<String,String> param) throws Exception {
		
		List<UnnaturalCaseTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("unnaturalCase.getCaseCompare",param);
		
		if(resultList==null) {
			resultList=new ArrayList<UnnaturalCaseTO>();
		}
		return resultList;
	}
	
	public void insertCaseCompare(UnnaturalCaseTO unnaturalCaseTO) throws Exception {
		
		this.getSqlMapClientTemplate().insert("unnaturalCase.insertCaseCompare",unnaturalCaseTO);
	}
	
	public List<Map<String,Object>> getDateList(String flag) throws Exception {
		
		List<Map<String,Object>> resultList=null;
		
		if("1".equals(flag)) {
			resultList=this.getSqlMapClientTemplate().queryForList("unnaturalCase.getDateList1");
		} else if("2".equals(flag)) {
			resultList=this.getSqlMapClientTemplate().queryForList("unnaturalCase.getDateList2");
		} else if("3".equals(flag)) {
			resultList=this.getSqlMapClientTemplate().queryForList("unnaturalCase.getDateList3");
		} else if("4".equals(flag)) {
			resultList=this.getSqlMapClientTemplate().queryForList("unnaturalCase.getDateList4");
		} else if("5".equals(flag)) {
			resultList=this.getSqlMapClientTemplate().queryForList("unnaturalCase.getDateList5");
		} else if("6".equals(flag)) {
			resultList=this.getSqlMapClientTemplate().queryForList("unnaturalCase.getDateList6");
		} else if("7".equals(flag)) {
			resultList=this.getSqlMapClientTemplate().queryForList("unnaturalCase.getDateList7");
		} else if("8".equals(flag)) {
			resultList=this.getSqlMapClientTemplate().queryForList("unnaturalCase.getDateList8");
		}
		if(resultList==null) {
			resultList=new ArrayList<Map<String,Object>>();
		}
		return resultList;
	}
	
	public List<UnnaturalCaseTO> getUnnaturalCaseCompare(Map<String,String> param) throws Exception {
		
		List<UnnaturalCaseTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("unnaturalCase.getUnnaturalCaseCompare",param);
		
		if(resultList==null) {
			resultList=new ArrayList<UnnaturalCaseTO>();
		}
		return resultList;
		
	}
	
	public List<UnnaturalCaseTO> getDunCase() throws Exception {
		
		List<UnnaturalCaseTO> resultList=null;
		
		Map<String,String> param=new HashMap<String,String>();
		param.put("CODE1","供应商保证");
		param.put("CODE2","租金");
		resultList=this.getSqlMapClientTemplate().queryForList("unnaturalCase.getDunCase",param);
		
		if(resultList==null) {
			resultList=new ArrayList<UnnaturalCaseTO>();
		}
		return resultList;
	}
	
	public List<UnnaturalCaseTO> getLockCode(Map<String,String> param) throws Exception {
		
		List<UnnaturalCaseTO> resultList=null;
		
		param.put("CODE","锁码方式");
		resultList=this.getSqlMapClientTemplate().queryForList("unnaturalCase.getLockCode",param);
		
		if(resultList==null) {
			resultList=new ArrayList<UnnaturalCaseTO>();
		}
		return resultList;
		
	}
	
	public List<UnnaturalCaseTO> getSuplNameForDunCase(Map<String,String> param) throws Exception {
		
		List<UnnaturalCaseTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("unnaturalCase.getSuplNameForDunCase",param);
		
		if(resultList==null) {
			resultList=new ArrayList<UnnaturalCaseTO>();
		}
		return resultList;
		
	}
	
	public List<UnnaturalCaseTO> getSuplNameForUncompletedFile(Map<String,String> param) throws Exception {
		
		List<UnnaturalCaseTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("unnaturalCase.getSuplNameForUncompletedFile",param);
		
		if(resultList==null) {
			resultList=new ArrayList<UnnaturalCaseTO>();
		}
		return resultList;
		
	}
	
	public void insertDunCase(UnnaturalCaseTO to) {
		
		this.getSqlMapClientTemplate().insert("unnaturalCase.insertDunCase",to);
	}
	
	public List<UnnaturalCaseTO> getUnnaturalDunCase(Map<String,String> param) throws Exception {
		
		List<UnnaturalCaseTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("unnaturalCase.getUnnaturalDunCase",param);
		
		if(resultList==null) {
			resultList=new ArrayList<UnnaturalCaseTO>();
		}
		return resultList;
	}
	
	public Map<String,Object> getUnnaturalDunCaseCount(Map<String,String> param) throws Exception {
		
		Map<String,Object> resultMap=null;
		
		resultMap=(Map<String,Object>)this.getSqlMapClientTemplate().queryForObject("unnaturalCase.getUnnaturalDunCaseCount",param);
		
		if(resultMap==null) {
			resultMap=new HashMap<String,Object>();
		}
		return resultMap;
	}
	
	public List<UnnaturalCaseTO> getUncompletedFileCase() throws Exception {
		
		List<UnnaturalCaseTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("unnaturalCase.getUncompletedFileCase");
		
		if(resultList==null) {
			resultList=new ArrayList<UnnaturalCaseTO>();
		}
		return resultList;
	}
	
	public void insertUncompletedFileCase(UnnaturalCaseTO unnaturalCaseTO) throws Exception {
		
		this.getSqlMapClientTemplate().insert("unnaturalCase.insertUncompletedFileCase",unnaturalCaseTO);
	}
	
	public List<UnnaturalCaseTO> getOnGoingInsuranceCase() throws Exception {
		
		List<UnnaturalCaseTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("unnaturalCase.getOnGoingInsuranceCase");
		
		if(resultList==null) {
			resultList=new ArrayList<UnnaturalCaseTO>();
		}
		
		return resultList;
	}
	
	public void insertOnGoingInsuranceCase(UnnaturalCaseTO unnaturalCaseTO) {
		
		this.getSqlMapClientTemplate().insert("unnaturalCase.insertOnGoingInsuranceCase",unnaturalCaseTO);
	}
	
	public List<UnnaturalCaseTO> getPendingApproveCase() throws Exception {
		
		List<UnnaturalCaseTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("unnaturalCase.getPendingApproveCase");
		
		if(resultList==null) {
			resultList=new ArrayList<UnnaturalCaseTO>();
		}
		
		return resultList;
	}
	
	public List<UnnaturalCaseTO> getPendingCommitCase() throws Exception {
		
		List<UnnaturalCaseTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("unnaturalCase.getPendingCommitCase");
		
		if(resultList==null) {
			resultList=new ArrayList<UnnaturalCaseTO>();
		}
		
		return resultList;
	}
	
	public void insertPendingApproveCase(UnnaturalCaseTO unnaturalCaseTO) {
		
		this.getSqlMapClientTemplate().insert("unnaturalCase.insertPendingApproveCase",unnaturalCaseTO);
	}
	
	public void insertPendingCommitCase(UnnaturalCaseTO unnaturalCaseTO) {
		
		this.getSqlMapClientTemplate().insert("unnaturalCase.insertPendingCommitCase",unnaturalCaseTO);
	}
	
	public List<UnnaturalCaseTO> getNotVisitCustomer() throws Exception {
		
		List<UnnaturalCaseTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("unnaturalCase.getNotVisitCustomer");
		
		if(resultList==null) {
			resultList=new ArrayList<UnnaturalCaseTO>();
		}
		
		return resultList;
	}
	
	public List<UnnaturalCaseTO> getVisitCustomer() throws Exception {
		
		List<UnnaturalCaseTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("unnaturalCase.getVisitCustomer");
		
		if(resultList==null) {
			resultList=new ArrayList<UnnaturalCaseTO>();
		}
		
		return resultList;
	}
	
	public List<UnnaturalCaseTO> getFisrtWindControl() throws Exception {
		
		List<UnnaturalCaseTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("unnaturalCase.getFisrtWindControl");
		
		if(resultList==null) {
			resultList=new ArrayList<UnnaturalCaseTO>();
		}
		
		return resultList;
	}
	
	public List<UnnaturalCaseTO> getApprovedNotAudit() throws Exception {
		
		List<UnnaturalCaseTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("unnaturalCase.getApprovedNotAudit");
		
		if(resultList==null) {
			resultList=new ArrayList<UnnaturalCaseTO>();
		}
		
		return resultList;
	}
	
	public List<UnnaturalCaseTO> getHasAuditNotPay() throws Exception {
		
		List<UnnaturalCaseTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("unnaturalCase.getHasAuditNotPay");
		
		if(resultList==null) {
			resultList=new ArrayList<UnnaturalCaseTO>();
		}
		
		return resultList;
	}
	
	public List<UnnaturalCaseTO> getLastWindControl() throws Exception {
		
		List<UnnaturalCaseTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("unnaturalCase.getLastWindControl");
		
		if(resultList==null) {
			resultList=new ArrayList<UnnaturalCaseTO>();
		}
		
		return resultList;
	}
	
	public List<UnnaturalCaseTO> getHasCreditNotPay() throws Exception {
		
		List<UnnaturalCaseTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("unnaturalCase.getHasCreditNotPay");
		
		if(resultList==null) {
			resultList=new ArrayList<UnnaturalCaseTO>();
		}
		
		return resultList;
	}
	
	public List<UnnaturalCaseTO> getDynamicCaseCount(Map<String,String> param) throws Exception {
		
		List<UnnaturalCaseTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("unnaturalCase.getDynamicCaseCount",param);
		
		if(resultList==null) {
			resultList=new ArrayList<UnnaturalCaseTO>();
		}
		
		return resultList;
	}
	
	public void insertDynamicCase(UnnaturalCaseTO unnaturalCaseTO) {
		
		this.getSqlMapClientTemplate().insert("unnaturalCase.insertDynamicCase",unnaturalCaseTO);
	}
	
	public void insertDynamicCaseCount(UnnaturalCaseTO unnaturalCaseTO) {
		
		this.getSqlMapClientTemplate().insert("unnaturalCase.insertDynamicCaseCount",unnaturalCaseTO);
	}
	
	public List<UnnaturalCaseTO> getDunVisit() {
		
		List<UnnaturalCaseTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("unnaturalCase.getDunVisit");
		
		if(resultList==null) {
			resultList=new ArrayList<UnnaturalCaseTO>();
		}
		
		return resultList;
	}
	
	public void insertDunVisit(UnnaturalCaseTO unnaturalCaseTO) {
		
		this.getSqlMapClientTemplate().insert("unnaturalCase.insertDunVisit",unnaturalCaseTO);
	}
}
