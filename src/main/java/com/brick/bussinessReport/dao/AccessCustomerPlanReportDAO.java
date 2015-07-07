package com.brick.bussinessReport.dao;

import java.util.ArrayList;
import java.util.List;

import com.brick.base.dao.BaseDAO;
import com.brick.bussinessReport.to.AccessCustomerPlanReportTO;

public class AccessCustomerPlanReportDAO extends BaseDAO {

	public List<AccessCustomerPlanReportTO> accessCustomerPlanReportList() throws Exception {
		
		List<AccessCustomerPlanReportTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.accessCustomerPlanReportList");
		
		if(resultList==null) {
			resultList=new ArrayList<AccessCustomerPlanReportTO>();
		}
		
		return resultList;
	}
}
