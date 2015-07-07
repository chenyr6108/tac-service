package com.brick.activityLog.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.activityLog.dao.LoanDAO;
import com.brick.activityLog.to.LoanTo;
import com.brick.base.service.BaseService;
import com.brick.log.service.LogPrint;
import com.brick.service.entity.Context;

public class LoanService extends BaseService{

	Log logger = LogFactory.getLog(LoanService.class);

	private LoanDAO loanDAO;

	public LoanDAO getLoanDAO() {
		return loanDAO;
	}

	public void setLoanDAO(LoanDAO loanDAO) {
		this.loanDAO = loanDAO;
	}

	public List<LoanTo> query(Context context) {

		String log="employeeId="+context.contextMap.get("s_employeeId")+"......query";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}

		List<String> errList=context.errList;
		List<LoanTo> result=null;

		try {
			result=this.loanDAO.query(context);
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
			errList.add("活动日志--委贷信息录入出错!请联系管理员(query)");
		}
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}

		return result;
	}

	public int updateLoanByLoanId(Context context) {

		String log="employeeId="+context.contextMap.get("s_employeeId")+"......updateLoanByLoanId";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		List<String> errList=context.errList;
		int result=0;
		try {
			result=this.loanDAO.updateLoanByLoanId(context);
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
			errList.add("活动日志--委贷信息录入出错!请联系管理员(updateLoanByLoanId)");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		
		return result;
	}
	
	public List<LoanTo> getCustomer(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getCustomer";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		List<String> errList=context.errList;
		List<LoanTo> result=null;
		
		try {
			result=this.loanDAO.getCustomer(context);
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
			errList.add("活动日志--委贷信息录入出错!请联系管理员(getCustomer)");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		
		return result;
	}
	
	public List<LoanTo> getSupplier(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getSupplier";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		List<String> errList=context.errList;
		List<LoanTo> result=null;
		
		try {
			result=this.loanDAO.getSupplier(context);
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
			errList.add("活动日志--委贷信息录入出错!请联系管理员(getSupplier)");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		
		return result;
	}
	
	public List<Map<String,String>> queryDataDictionary(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......queryDataDictionary";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		List<String> errList=context.errList;
		List<Map<String,String>> result=null;
		
		try {
			result=this.loanDAO.queryDataDictionary(context);
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
			errList.add("活动日志--委贷信息录入出错!请联系管理员(queryDataDictionary)");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		
		return result;
	}
	
	public List<LoanTo> getUser(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getUser";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		List<String> errList=context.errList;
		List<LoanTo> result=null;
		
		try {
			result=this.loanDAO.getUser(context);
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
			errList.add("活动日志--委贷信息录入出错!请联系管理员(getUser)");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		
		return result;
	}
	
	public List<Map<String,Object>> getDeptList(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getDeptList";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		List<String> errList=context.errList;
		List<Map<String,Object>> result=null;
		
		try {
			result=this.loanDAO.getDeptList(context);
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
			errList.add("活动日志--委贷信息录入出错!请联系管理员(getDeptList)");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		
		return result;
	}
	
	public void addLoan(LoanTo loanTo,Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......addLoan";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		List<String> errList=context.errList;
		
		try {
			this.loanDAO.addLoan(loanTo);
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
			errList.add("活动日志--委贷信息录入出错!请联系管理员(addLoan)");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
	
	public void applyPayClose(LoanTo loanTo) throws Exception {
		this.loanDAO.applyPayClose(loanTo);
	}
	
	public void approvePayClose(LoanTo loanTo) throws Exception {
		this.loanDAO.approvePayClose(loanTo);
	}
	
	public List<LoanTo> getLoanTarget(Context context) throws Exception {
		return this.loanDAO.getLoanTarget(context);
	}
	
	public void saveTarget(Context context) throws Exception {
		this.loanDAO.saveTarget(context);
	}
	
	public void updateTarget(Context context) throws Exception {
		this.loanDAO.updateTarget(context);
	}
}
