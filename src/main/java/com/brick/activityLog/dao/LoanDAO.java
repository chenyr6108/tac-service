package com.brick.activityLog.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.brick.activityLog.to.LoanTo;
import com.brick.base.dao.BaseDAO;
import com.brick.service.entity.Context;

public class LoanDAO extends BaseDAO {

	public List<LoanTo> query(Context context) throws Exception {
		
		List<LoanTo> result=this.getSqlMapClientTemplate().queryForList("loan.query",context.contextMap);
	    if(result==null) {
	    	result=new ArrayList<LoanTo>();
	    }
	    return result;
	}
	
	public int updateLoanByLoanId(Context context) throws Exception {
		
		int result=this.getSqlMapClientTemplate().update("loan.updateLoanByLoanId",context.contextMap);
		
		return result;
	}
	
	public List<LoanTo> getCustomer(Context context) throws Exception {
		
		List<LoanTo> result=this.getSqlMapClientTemplate().queryForList("loan.getCustomer",context.contextMap);
	    if(result==null) {
	    	result=new ArrayList<LoanTo>();
	    }
	    return result;
	}	
	
	public List<LoanTo> getSupplier(Context context) throws Exception {
		
		List<LoanTo> result=this.getSqlMapClientTemplate().queryForList("loan.getSupplier",context.contextMap);
	    if(result==null) {
	    	result=new ArrayList<LoanTo>();
	    }
	    return result;
	}
	
	public List<Map<String,String>> queryDataDictionary(Context context) throws Exception {
		
		List<Map<String,String>> result=this.getSqlMapClientTemplate().queryForList("dataDictionary.queryDataDictionary",context.contextMap);
	    if(result==null) {
	    	result=new ArrayList<Map<String,String>>();
	    }
	    return result;
	}
	
	public List<LoanTo> getUser(Context context) throws Exception {
		
		List<LoanTo> result=this.getSqlMapClientTemplate().queryForList("loan.getUser",context.contextMap);
	    if(result==null) {
	    	result=new ArrayList<LoanTo>();
	    }
	    return result;
	}
	
	public List<Map<String,Object>> getDeptList(Context context) throws Exception {

		List<Map<String,Object>> result=this.getSqlMapClientTemplate().queryForList("employee.getCompany",context.contextMap);

		if(result==null) {
			result=new ArrayList<Map<String,Object>>();
		}
		return result;
	}
	
	public void addLoan(LoanTo loanTo) throws Exception {
		
		this.getSqlMapClientTemplate().insert("loan.addLoan", loanTo);
	}
	
	public void applyPayClose(LoanTo loanTo) throws Exception {
		this.getSqlMapClientTemplate().update("loan.applyPayClose", loanTo);
	}
	
	public void approvePayClose(LoanTo loanTo) throws Exception {
		this.getSqlMapClientTemplate().update("loan.approvePayClose", loanTo);
	}
	
	public List<LoanTo> getLoanTarget(Context context) throws Exception {
		
		return (List<LoanTo>)this.getSqlMapClientTemplate().queryForList("loan.getLoanTarget",context.contextMap);
	}
	
	public void saveTarget(Context context) throws Exception {
		
		this.getSqlMapClientTemplate().insert("loan.saveTarget",context.contextMap);
	}
	
	public void updateTarget(Context context) throws Exception {
		
		this.getSqlMapClientTemplate().update("loan.updateTarget",context.contextMap);
	}
}
