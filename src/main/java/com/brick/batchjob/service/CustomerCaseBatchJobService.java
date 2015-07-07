package com.brick.batchjob.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.exception.DaoException;
import com.brick.base.service.BaseService;
import com.brick.batchjob.dao.CustomerCaseBatchJobDAO;
import com.brick.batchjob.to.CustomerCaseTo;
import com.brick.log.service.LogPrint;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;

public class CustomerCaseBatchJobService extends BaseService {

	Log logger=LogFactory.getLog(CustomerCaseBatchJobService.class);

	private CustomerCaseBatchJobDAO customerCaseBatchJobDAO;

	public CustomerCaseBatchJobDAO getCustomerCaseBatchJobDAO() {
		return customerCaseBatchJobDAO;
	}

	public void setCustomerCaseBatchJobDAO(
			CustomerCaseBatchJobDAO customerCaseBatchJobDAO) {
		this.customerCaseBatchJobDAO = customerCaseBatchJobDAO;
	}

	@Transactional(rollbackFor=Exception.class)
	public void batchJob() throws Exception {

		if(logger.isDebugEnabled()) {
			logger.debug("batch job for 客户案况表 start  --------------------");
		}

		List<CustomerCaseTo> insertDataList=null;
		try {
			insertDataList=this.customerCaseBatchJobDAO.getCustCaseInfo();
			for(int i=0;i<insertDataList.size();i++) {
				Thread.sleep(1);//防止主键重复
				insertDataList.get(i).setCustCaseId(String.valueOf(System.currentTimeMillis()));
				if(insertDataList.get(i).getCustId()==null) {
					continue;
				}
				this.customerCaseBatchJobDAO.insertCustCaseData(insertDataList.get(i));
			}
		} catch(DaoException e) {
			LogPrint.getLogStackTrace(e,logger);
			throw e;
		} catch(Exception e) {
			LogPrint.getLogStackTrace(e,logger);
			throw e;
		}

		if(logger.isDebugEnabled()) {
			logger.debug("batch job for 客户案况表 end  --------------------");
		}
	}

	public List<CustomerCaseTo> getDeptList(Context context) {

		List<CustomerCaseTo> resultList=null;

		try {
			resultList=this.customerCaseBatchJobDAO.getDeptList(context);
		} catch (DaoException e) {
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
		}

		return resultList;
	}

	public List<Map<String,Object>> getCaseTypeList(Context context) throws DaoException {

		List<Map<String,Object>> resultList=null;

		resultList=this.customerCaseBatchJobDAO.getCaseTypeList(context);

		if(resultList==null) {
			resultList=new ArrayList<Map<String,Object>>();
		}

		return resultList;
	}

	public static List<CustomerCaseTo> getCustCaseTotal(String USER_NAME,String DEPT_ID,String CUST_NAME,String CASE_TYPE,String DATE_TYPE,String START_DATE,String END_DATE) {

		List<CustomerCaseTo> resultList=null;
		List<Map<String,Object>> brandList=null;
		
		Map<String,String> param=new HashMap<String,String>();
		param.put("TYPE","客户案况表中的案况状态");
		param.put("USER_NAME",USER_NAME);
		param.put("DEPT_ID",DEPT_ID);
		param.put("CUST_NAME",CUST_NAME);
		param.put("CASE_TYPE",CASE_TYPE);
		param.put("DATE_TYPE",DATE_TYPE);
		param.put("START_DATE",START_DATE);
		param.put("END_DATE",END_DATE);
		try {
			resultList=(List<CustomerCaseTo>)DataAccessor.query("businessReport.queryCustomerCase",param,RS_TYPE.LIST);
			brandList=(List<Map<String,Object>>)DataAccessor.query("creditReportManage.getBrand",null,RS_TYPE.LIST);
			
    		for(int i=0;resultList!=null&&i<resultList.size();i++) {
    			boolean flag=true;
    			for(int j=0;brandList!=null&&j<brandList.size();j++) {
    				if(resultList.get(i).getCreditId().equals(String.valueOf(brandList.get(j).get("CREDIT_ID")))) {
    					if(flag) {
    						resultList.get(i).setSuplName((String)brandList.get(j).get("BRAND"));
    						flag=false;
    					} else {
    						resultList.get(i).setSuplName(resultList.get(i).getSuplName()+","+brandList.get(j).get("BRAND"));
    					}
    				}
    			}
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return resultList;
	}
}
