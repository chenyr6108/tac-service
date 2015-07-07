package com.brick.bussinessReport.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;
import com.brick.bussinessReport.to.CustomerVisitTO;

public class CustomerVisitReportDAO extends BaseDAO {

	public List<CustomerVisitTO> getCustomerVisitTime() throws Exception {
		
		List<CustomerVisitTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getCustomerVisitTime");
		if(resultList==null) {
			resultList=new ArrayList<CustomerVisitTO>();
		}
		
		return resultList;
	}
	
	public void insertCustomerVisitTime(CustomerVisitTO customerVisitTO) throws Exception {
		
		this.getSqlMapClientTemplate().insert("businessReport.insertCustomerVisitTime",customerVisitTO);
	}
	
	public Integer getDayCount(Map contextMap) throws Exception {
		
		return (Integer)this.getSqlMapClientTemplate().queryForObject("businessReport.getDayCount",contextMap);
	}
	
	public List<CustomerVisitTO> getDateList() throws Exception {
		List<CustomerVisitTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getDateList");
		
		if(resultList==null) {
			resultList=new ArrayList<CustomerVisitTO>();
		}
		
		return resultList;
	}
}
