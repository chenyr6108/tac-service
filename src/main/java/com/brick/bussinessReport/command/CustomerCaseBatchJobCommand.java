package com.brick.bussinessReport.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.dao.BaseDAO;
import com.brick.base.exception.DaoException;
import com.brick.batchjob.service.CustomerCaseBatchJobService;
import com.brick.batchjob.to.CustomerCaseTo;
import com.brick.log.service.LogPrint;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;

public class CustomerCaseBatchJobCommand extends BaseCommand {

	Log logger=LogFactory.getLog(CustomerCaseBatchJobCommand.class);
	
	private CustomerCaseBatchJobService customerCaseBatchJobService;
	private BaseDAO baseDAO;
	
	public CustomerCaseBatchJobService getCustomerCaseBatchJobService() {
		return customerCaseBatchJobService;
	}

	public void setCustomerCaseBatchJobService(
			CustomerCaseBatchJobService customerCaseBatchJobService) {
		this.customerCaseBatchJobService = customerCaseBatchJobService;
	}

	public BaseDAO getBaseDAO() {
		return baseDAO;
	}

	public void setBaseDAO(BaseDAO baseDAO) {
		this.baseDAO = baseDAO;
	}
	
	public void query(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......query";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List<CustomerCaseTo> resultList=null;
		List<Map<String,Object>> brandList=null;//供应商
		List<CustomerCaseTo> deptList=null;//办事处
		List<Map<String,Object>> caseTypeList=null;//案况状态
		
		Map<String,Object> rsMap=null;
		Map<String,String> paramMap = new HashMap<String,String>();
		paramMap.put("id",context.contextMap.get("s_employeeId").toString());
		
		try {
			rsMap=(Map<String,Object>)DataAccessor.query("employee.getEmpInforById",paramMap,DataAccessor.RS_TYPE.MAP);
			context.contextMap.put("p_usernode",rsMap.get("NODE"));
			
			context.contextMap.put("TYPE","客户案况表中的案况状态");
			resultList=(List<CustomerCaseTo>)this.baseDAO.queryForPage("businessReport.queryCustomerCase","businessReport.queryCustomerCaseCount",context,outputMap);
			
			brandList=(List<Map<String,Object>>)DataAccessor.query("creditReportManage.getBrand",null,RS_TYPE.LIST);
    		
    		for(int i=0;resultList!=null&&i<resultList.size();i++) {
    			boolean flag=true;
    			for(int j=0;brandList!=null&&j<brandList.size();j++) {
    				if(resultList.get(i).getCreditId().equals(String.valueOf(brandList.get(j).get("CREDIT_ID")))) {
    					if(flag) {
    						resultList.get(i).setSuplName((String)brandList.get(j).get("BRAND"));
    						flag=false;
    					} else {
    						resultList.get(i).setSuplName(resultList.get(i).getSuplName()+"<b>,</b><br>"+brandList.get(j).get("BRAND"));
    					}
    				}
    			}
    		}
    		
    		deptList=this.customerCaseBatchJobService.getDeptList(context);
    		
    		caseTypeList=this.customerCaseBatchJobService.getCaseTypeList(context);
    		
		} catch(DaoException e) {
			context.errList.add("客户案况表出错!请联系管理员");
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
		} catch(Exception e) {
			context.errList.add("客户案况表出错!请联系管理员");
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
		}
		
//		outputMap.put("pageTotalSize",context.contextMap.get("pageTotalSize"));
//		outputMap.put("currentPage",context.contextMap.get("currentPage"));
//		outputMap.put("pageCount",context.contextMap.get("pageCount"));
//		outputMap.put("pageSize",context.contextMap.get("pageSize"));
		outputMap.put("resultList",resultList);
		outputMap.put("USER_NAME",context.contextMap.get("USER_NAME"));
		outputMap.put("CUST_NAME",context.contextMap.get("CUST_NAME"));
		outputMap.put("DEPT_ID",context.contextMap.get("DEPT_ID"));
		outputMap.put("CASE_TYPE",context.contextMap.get("CASE_TYPE"));
		outputMap.put("DATE_TYPE",context.contextMap.get("DATE_TYPE"));
		outputMap.put("START_DATE",context.contextMap.get("START_DATE"));
		outputMap.put("END_DATE",context.contextMap.get("END_DATE"));
		outputMap.put("deptList",deptList);
		outputMap.put("caseTypeList",caseTypeList);
		
		if(context.errList.isEmpty()) {
			Output.jspOutput(outputMap,context,"/activitiesLog/custCase/custCase.jsp");
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
}
