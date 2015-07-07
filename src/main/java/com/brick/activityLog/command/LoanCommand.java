package com.brick.activityLog.command;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.activityLog.service.LoanService;
import com.brick.activityLog.to.LoanTo;
import com.brick.base.command.BaseCommand;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;

public class LoanCommand extends BaseCommand {

	Log logger = LogFactory.getLog(LoanCommand.class);

	private LoanService loanService;

	private MailUtilService mailUtilService;
	
	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}

	public LoanService getLoanService() {
		return loanService;
	}

	public void setLoanService(LoanService loanService) {
		this.loanService = loanService;
	}

	public void query(Context context) {

		String log="employeeId="+context.contextMap.get("s_employeeId")+"......query";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		PagingInfo<Object> pagingInfo=null;
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		context.contextMap.put("TYPE1","委贷类型");
		context.contextMap.put("TYPE2","委贷案况");
		/*List<LoanTo> result=this.loanService.query(context);*/
				
		List<String> resourceIdList=null;
		
		//获得租赁方式
		context.contextMap.put("dataType","委贷类型");
		List<Map<String,String>> loanModeList=this.loanService.queryDataDictionary(context);
		outputMap.put("loanModeList",loanModeList);
		try {
			pagingInfo=baseService.queryForListWithPaging("loan.query",context.contextMap,"loanId",ORDER_TYPE.DESC);
			
			resourceIdList=(List<String>)DataAccessor.query("common.getPermissions",context.contextMap,DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
		}
		boolean apply=false;
		boolean approve=false;
		for(int i=0;resourceIdList!=null&&i<resourceIdList.size();i++) {
			if("applyPay".equals(resourceIdList.get(i))) {
				apply=true;
			} else if("approvePay".equals(resourceIdList.get(i))) {
				approve=true;
			}
		}
		if(context.errList.isEmpty()) {

			
			outputMap.put("pagingInfo",pagingInfo);
			outputMap.put("LEASE_WAY",context.contextMap.get("LEASE_WAY"));
			outputMap.put("CUSTOMER_NAME",context.contextMap.get("CUST_NAME"));
			outputMap.put("PAY_DATE_BEGIN",context.contextMap.get("PAY_DATE_BEGIN"));
			outputMap.put("PAY_DATE_END",context.contextMap.get("PAY_DATE_END"));
			
			outputMap.put("apply",apply);
			outputMap.put("approve",approve);

			Output.jspOutput(outputMap,context,"/activitiesLog/loan/loan.jsp");
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}

		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}

	//通过LOAN_ID初始化修改信息
	public void initModify(Context context) {

		String log="employeeId="+context.contextMap.get("s_employeeId")+"......initModify";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}

		Map<String,Object> outputMap=new HashMap<String,Object>();

		LoanTo loanTo=this.loanService.query(context).get(0);

		if(context.errList.isEmpty()) {

			outputMap.put("loanTo",loanTo);
			Output.jsonOutput(outputMap,context);
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}

		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}

	//修改信贷信息By Loan_id
	public void updateLoan(Context context) {

		String log="employeeId="+context.contextMap.get("s_employeeId")+"......updateLoan";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}

		Map<String,Object> outputMap=new HashMap<String,Object>();
		context.contextMap.put("NAME",context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")");
		this.loanService.updateLoanByLoanId(context);

		if(context.errList.isEmpty()) {
			context.contextMap.remove("LOAN_ID");
			this.query(context);
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}

		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}

	//获得客户信息
	public void toAddPage(Context context) {

		String log="employeeId="+context.contextMap.get("s_employeeId")+"......toAddPage";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}

		Map<String,Object> outputMap=new HashMap<String,Object>();

		List<LoanTo> custList=this.loanService.getCustomer(context);

		//获得租赁方式
		context.contextMap.put("dataType","委贷类型");
		List<Map<String,String>> loanModeList=this.loanService.queryDataDictionary(context);

		//获得案件状况
		context.contextMap.put("dataType","委贷案况");
		List<Map<String,String>> caseStatusList=this.loanService.queryDataDictionary(context);

		//获得经办人
		context.contextMap.put("JOB","业务员");
		List<LoanTo> userList=this.loanService.getUser(context);

		//获得办事处的drop down List
		context.contextMap.put("decp_id", "2");//2代表的是拿分公司
		List<Map<String,Object>> deptList=this.loanService.getDeptList(context);

		//获得供货商
		List<LoanTo> supList=this.loanService.getSupplier(context);
		
		if(context.errList.isEmpty()) {

			outputMap.put("custList",custList);
			outputMap.put("supList",supList);
			outputMap.put("loanModeList",loanModeList);
			outputMap.put("caseStatusList",caseStatusList);
			outputMap.put("userList",userList);
			outputMap.put("deptList",deptList);

			Output.jspOutput(outputMap,context,"/activitiesLog/loan/addLoan.jsp");
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}

		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
	
	public void initLoanInfo(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();

		List<LoanTo> custList=this.loanService.getCustomer(context);
		outputMap.put("custList",custList);

		//获得租赁方式
		context.contextMap.put("dataType","委贷类型");
		List<Map<String,String>> loanModeList=this.loanService.queryDataDictionary(context);
		outputMap.put("loanModeList",loanModeList);

		//获得案件状况
		context.contextMap.put("dataType","委贷案况");
		List<Map<String,String>> caseStatusList=this.loanService.queryDataDictionary(context);
		outputMap.put("caseStatusList",caseStatusList);

		//获得经办人
		context.contextMap.put("JOB","业务员");
		List<LoanTo> userList=this.loanService.getUser(context);
		outputMap.put("userList",userList);

		//获得办事处的drop down List
		context.contextMap.put("decp_id", "2");//2代表的是拿分公司
		List<Map<String,Object>> deptList=this.loanService.getDeptList(context);
		outputMap.put("deptList",deptList);

		//获得供货商
		List<LoanTo> supList=this.loanService.getSupplier(context);
		outputMap.put("supList",supList);
		
		Output.jsonOutput(outputMap,context);
	}
	
	public void getLoanModeList(Context context) {
		
		//获得租赁方式
		context.contextMap.put("dataType","委贷类型");
		List<Map<String, String>> loanModeList=this.loanService.queryDataDictionary(context);
		
		Output.jsonArrayListOutput(loanModeList,context);
	}
	
	public void getLoanStatusList(Context context) {
		
		//获得案件状况
		context.contextMap.put("dataType","委贷案况");
		List<Map<String,String>> caseStatusList=this.loanService.queryDataDictionary(context);
		
		Output.jsonArrayListOutput(caseStatusList,context);
	}

	public void save(Context context) {

		String log="employeeId="+context.contextMap.get("s_employeeId")+"......save";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}

		Map<String,Object> outputMap=new HashMap<String,Object>();

		//验证前台信息
		LoanTo loanTo=new LoanTo();
		loanTo.setLoanId(String.valueOf(System.currentTimeMillis()));
		loanTo.setCustCode((String)context.contextMap.get("CUST_CODE"));
		loanTo.setSupCode((String)context.contextMap.get("SUP_CODE"));
		loanTo.setLoanCode((String)context.contextMap.get("LOAN_CODE"));
		loanTo.setIntroducer((String)context.contextMap.get("INTRODUCER"));
		loanTo.setLoanMode((String)context.contextMap.get("LOAN_MODE"));
		loanTo.setCostMoney(new BigDecimal("".equals(context.contextMap.get("COST_MONEY"))?"0":(String)context.contextMap.get("COST_MONEY")));
		loanTo.setCautionMoney(new BigDecimal("".equals(context.contextMap.get("CAUTION_MONEY"))?"0":(String)context.contextMap.get("CAUTION_MONEY")));
		loanTo.setCaseStatusId((String)context.contextMap.get("CASE_STATUS_ID"));
		loanTo.setUserId((String)context.contextMap.get("USER_ID"));
		loanTo.setDeptId((String)context.contextMap.get("DEPT_ID"));
		loanTo.setApproveMoney(new BigDecimal("".equals(context.contextMap.get("APPROVE_MONEY"))?"0":(String)context.contextMap.get("APPROVE_MONEY")));
		loanTo.setPayMoney(new BigDecimal("".equals(context.contextMap.get("PAY_MONEY"))?"0":(String)context.contextMap.get("PAY_MONEY")));
		loanTo.setApproveDateDescr((String)context.contextMap.get("APPROVE_DATE"));
		loanTo.setExpDateDescr((String)context.contextMap.get("EXP_DATE"));
		loanTo.setPayDateDescr((String)context.contextMap.get("PAY_DATE"));
		String name=context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")";
		loanTo.setCreateBy(name);
		loanTo.setLastUpdateBy(name);
		loanTo.setFirstAccessDescr((String)context.contextMap.get("FIRST_ACCESS_DATE"));
		loanTo.setAccrual(new BigDecimal("".equals(context.contextMap.get("ACCRUAL"))?"0":(String)context.contextMap.get("ACCRUAL")));
		loanTo.setStartDateDescr((String)context.contextMap.get("START_DATE"));

		this.validate(loanTo,context.errList);

		if(context.errList.isEmpty()) {
			this.loanService.addLoan(loanTo,context);
			this.query(context);
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}

		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}

	//后台验证
	private void validate(LoanTo loanTo,List<String> errList) {

		if(loanTo.getCostMoney()==null) {
			errList.add("成本不能为空!");
		}
		if(loanTo.getCautionMoney()==null) {
			errList.add("保证金不能为空!");
		}
		if(loanTo.getApproveMoney()==null) {
			errList.add("核准额度不能为空!");
		}
		if(loanTo.getPayMoney()==null) {
			errList.add("起租额度不能为空!");
		}
		if(loanTo.getApproveDateDescr()==null||"".equals(loanTo.getApproveDateDescr())) {
			errList.add("核准日期不能为空!");
		}
		if(loanTo.getExpDateDescr()==null||"".equals(loanTo.getExpDateDescr())) {
			errList.add("预估拨款日不能为空!");
		}
		if(loanTo.getPayDateDescr()==null||"".equals(loanTo.getPayDateDescr())) {
			errList.add("起租日期不能为空!");
		}
		if(loanTo.getCostMoney().doubleValue()<loanTo.getCautionMoney().doubleValue()) {
			errList.add("成本不能小于保证金!");
		}
	}
	
	public void applyPayClose(Context context) {
		
		LoanTo to=new LoanTo();
		to.setLoanId((String)context.contextMap.get("APPLY_ID"));
		to.setRemark((String)context.contextMap.get("APPLY_REMARK"));
		to.setPayCloseDateDescr((String)context.contextMap.get("APPLY_DATE"));
		try {
			this.loanService.applyPayClose(to);
			
			MailSettingTo mailSettingTo=new MailSettingTo();
			
			mailSettingTo.setEmailContent((String)context.contextMap.get("LOAN_NAME")+"的委贷案件"+(String)context.contextMap.get("APPLY_DATE")+"结清!(备注:"+(String)context.contextMap.get("APPLY_REMARK")+")");
			this.mailUtilService.sendMail(5,mailSettingTo);
			//结清申请提醒 xuwei 2014 3/4
			mailSettingTo=new MailSettingTo();		
			mailSettingTo.setEmailContent((String)context.contextMap.get("LOAN_NAME")+"的委贷案件"+(String)context.contextMap.get("APPLY_DATE")+"结清!此案已经结清无需租金扣款。");
			this.mailUtilService.sendMail(2007,mailSettingTo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.query(context);
	}
	
	public void approvePayClose(Context context) {
		
		LoanTo to=new LoanTo();
		to.setLoanId((String)context.contextMap.get("APPROVE_ID"));
		to.setPayCloseStatus(Integer.valueOf((String)context.contextMap.get("APPROVE_STATUS")));
		
		try {
			this.loanService.approvePayClose(to);
			
			MailSettingTo mailSettingTo=new MailSettingTo();
			
			String result="";
			if("2".equals((String)context.contextMap.get("APPROVE_STATUS"))) {
				result="通过";
			} else {
				result="驳回";
			}
			
			mailSettingTo.setEmailContent((String)context.contextMap.get("APPROVE_NAME")+"的委贷案件结清<b>"+result+"</b>!");
			
			this.mailUtilService.sendMail(6,mailSettingTo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.query(context);
	}
	
	public void queryLoanTarget(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		List<LoanTo> result=null;
		try {
			result=this.loanService.getLoanTarget(context);
		} catch (Exception e) {
			
		}
		
		outputMap.put("result",result);
		outputMap.put("YEAR",context.contextMap.get("YEAR"));
		Output.jspOutput(outputMap,context,"/activitiesLog/loan/loanTarget.jsp");
	}
	
	public void checkExistYear(Context context) {
		
		List<LoanTo> result=null;
		try {
			result=this.loanService.getLoanTarget(context);
		} catch (Exception e) {
			
		}
		
		if(result==null||result.size()==0) {
			Output.jsonFlageOutput(true,context);
		} else {
			Output.jsonFlageOutput(false,context);
		}
	}
	
	public void saveTarget(Context context) {

		try {
			this.loanService.saveTarget(context);
		} catch (Exception e) {
			
		}
		context.contextMap.remove("YEAR");
		context.contextMap.put("__action","loanCommand.queryLoanTarget");
		this.queryLoanTarget(context);
	}
	
	public void updateTarget(Context context) {
		
		try {
			this.loanService.updateTarget(context);
		} catch (Exception e) {
			
		}
		context.contextMap.remove("YEAR");
		context.contextMap.put("__action","loanCommand.queryLoanTarget");
		this.queryLoanTarget(context);
	}
}
