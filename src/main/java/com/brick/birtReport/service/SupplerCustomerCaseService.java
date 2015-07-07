package com.brick.birtReport.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.batchjob.to.SuplCustCaseTo;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.RS_TYPE;

public class SupplerCustomerCaseService {

	public static List<SuplCustCaseTo> getSupplerCustomerCase(String suplName,String custName,String caseType,String leaseCode) {
		
		List<SuplCustCaseTo> resultList=null;
		Map<String,String> param=new HashMap<String,String>();
		param.put("TYPE","客户案况表中的案况状态");
		param.put("SUPL_NAME",suplName);
		param.put("CUST_NAME",custName);
		param.put("CASE_TYPE",caseType);
		param.put("LEASE_CODE",leaseCode);
		try {
			resultList=(List<SuplCustCaseTo>)DataAccessor.query("businessReport.getSupplerCustomerCase",param,RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}
}
