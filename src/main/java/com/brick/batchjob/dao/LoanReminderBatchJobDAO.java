package com.brick.batchjob.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.brick.activityLog.to.LoanTo;
import com.brick.base.dao.BaseDAO;

public class LoanReminderBatchJobDAO extends BaseDAO {
	
	public List<LoanTo> getLoanReminder(Map<String,String> param) throws Exception {
		
		List<LoanTo> result=this.getSqlMapClientTemplate().queryForList("loan.getLoanReminder",param);
	    if(result==null) {
	    	result=new ArrayList<LoanTo>();
	    }
	    return result;
	}
	
}
