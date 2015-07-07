package com.brick.payMoney.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.exception.ServiceException;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.BaseTo;
import com.brick.base.to.PagingInfo;
import com.brick.baseManage.service.BusinessLog;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.kingDeer.constants.KingDeerConstants;
import com.brick.log.service.LogPrint;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.OPERATION_TYPE;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.Constants;
import com.brick.util.DataUtil;
import com.brick.util.StringUtils;
import com.brick.util.web.HTMLUtil;
import com.ibatis.sqlmap.client.SqlMapClient;

public class payMoneyMessageService extends BaseCommand
{
	Log logger = LogFactory.getLog(payMoneyMessageService.class);
	
	private MailUtilService mailUtilService;
	
	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}
	
	private PayMoneyService payMoneyService;

	public PayMoneyService getPayMoneyService() {
		return payMoneyService;
	}

	public void setPayMoneyService(PayMoneyService payMoneyService) {
		this.payMoneyService = payMoneyService;
	}

	/**
	 * 付款信息查询
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryPayMoney (Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		PagingInfo<Object> dw = null;

		Map rsMap = null;
		Map paramMap = new HashMap();
		
		String BACKSTATE="";
		paramMap.put("id", context.contextMap.get("s_employeeId"));
		if(context.contextMap.get("BACKSTATE")==null){
			BACKSTATE="0";
			
		}else{
			BACKSTATE=context.contextMap.get("BACKSTATE").toString();
		}
		context.contextMap.put("BACKSTATE", Integer.parseInt(BACKSTATE));
		
		if(StringUtils.isEmpty((String)context.contextMap.get("PAY_ORDER"))) {
			context.contextMap.put("PAY_ORDER",0);
		}
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				rsMap = (Map) DataAccessor.query("employee.getEmpInforById", paramMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("p_usernode", rsMap.get("NODE"));
				if(BACKSTATE.equals("3"))
				{
					dw = baseService.queryForListWithPaging("payMoney.queryPayMoneyByInsurance", context.contextMap, "EXPECTEDDATE", ORDER_TYPE.DESC);
				}
				else if(BACKSTATE.equals("2"))
				{
					dw = baseService.queryForListWithPaging("payMoney.queryPayMoneyByInsurance", context.contextMap, "EXPECTEDDATE", ORDER_TYPE.DESC);
				}else if(BACKSTATE.equals("5")){
					dw = baseService.queryForListWithPaging("rentContract.queryPayMoneyByReturnMoney", context.contextMap, "ID", ORDER_TYPE.DESC);
				}else if(BACKSTATE.equals("9")){
					context.contextMap.put("__orderBy", "APPLICATION_DATE");
					if (StringUtils.isEmpty(context.contextMap.get("shen_pi_STATE"))) {
						context.contextMap.put("shen_pi_STATE", "0");
					}
					outputMap.put("shen_pi_STATE", context.contextMap.get("shen_pi_STATE"));
					dw = baseService.queryForListWithPaging("rentContract.queryHandlingChargeByOrder", context.contextMap, "ID", ORDER_TYPE.DESC);
				}else if(BACKSTATE.equals("10")){
					context.contextMap.put("__orderBy", "APPLICATION_DATE");
					if (StringUtils.isEmpty(context.contextMap.get("shen_pi_STATE"))) {
						context.contextMap.put("shen_pi_STATE", "0");
					}
					outputMap.put("shen_pi_STATE", context.contextMap.get("shen_pi_STATE"));
					dw = baseService.queryForListWithPaging("rentContract.queryBrokerageByOrder", context.contextMap, "ID", ORDER_TYPE.DESC);
				}else{
					dw = baseService.queryForListWithPaging("payMoney.queryPayMoney", context.contextMap, "EXPECTEDDATE", ORDER_TYPE.DESC);
					//加入总计
					double con = 0.0 ;
					if(dw != null && dw.getResultList() != null ){
						List list = (List) dw.getResultList() ;
						for(int i = 0 ; i < list.size() ; i++){
							Map temp = (Map) list.get(i) ;
							if(temp != null && temp.get("PAY_MONEY") != null)
							con += Double.parseDouble(temp.get("PAY_MONEY") + "") ;
						}
					}
					outputMap.put("TOTAL", con) ;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);

			}

		
		//权限
		BaseTo baseTo = new BaseTo();
		baseTo.setModify_by(context.contextMap.get("s_employeeId").toString());
		baseTo.setResource_code("auditPayMoney");
		try {
			outputMap.put("auditPayMoney", baseService.checkAccessForResource(baseTo));
		} catch (ServiceException e) {
			errList.add(e);
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
		}
		
		/*-------- output --------*/
		outputMap.put("dw", dw);
		outputMap.put("PAY_ORDER",context.contextMap.get("PAY_ORDER"));
		outputMap.put("PLAYDETIL_STATE", context.contextMap.get("PLAYDETIL_STATE")) ;
		outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
		outputMap.put("QSTART_DATE", context.contextMap.get("QSTART_DATE"));
		outputMap.put("QEND_DATE", context.contextMap.get("QEND_DATE"));
		outputMap.put("RENTSTAUTS", context.contextMap.get("RENTSTAUTS"));
		outputMap.put("BACKSTATE", BACKSTATE);
		outputMap.put("companyCode", context.contextMap.get("companyCode"));
		if(errList.isEmpty()){
			if(BACKSTATE.equals("3"))
			{
				Output.jspOutput(outputMap, context, "/payMoney/payMoneyInsurance.jsp");
			}
			else if(BACKSTATE.equals("2"))
			{
				Output.jspOutput(outputMap, context, "/payMoney/payMoneyBonus.jsp");
			}else if(BACKSTATE.equals("5")){
				Output.jspOutput(outputMap, context, "/payMoney/payMoneyFundsReturn.jsp");
			}else if(BACKSTATE.equals("9")){
				Output.jspOutput(outputMap, context, "/payMoney/payMoneyHandlingCharge.jsp");
			}else if(BACKSTATE.equals("10")){
				Output.jspOutput(outputMap, context, "/payMoney/payMoneyBrokerage.jsp");
			}else
			{
				Output.jspOutput(outputMap, context, "/payMoney/payMoneyContract.jsp");
			}
			

		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		}

	}
	
	@SuppressWarnings("unchecked")
	public void payMoneyManagerByOne(Context context)
	{
		Map outputMap = new HashMap();
		List payDw=null;
		List auditRe = null;
		List errList = context.errList;
		Map payMoney=null;
		if (errList.isEmpty()) {		
			try {
				context.contextMap.put("dataType","拨款方式");
				payMoney = (Map) DataAccessor.query("payMoney.payMoneyManager", context.contextMap,DataAccessor.RS_TYPE.MAP);
				auditRe = (List) DataAccessor.query("rentContract.getLogByCreditId",context.contextMap, DataAccessor.RS_TYPE.LIST);
				Object BACKSTATE=payMoney.get("BACKSTATE");
				String backState=null;
				if(BACKSTATE!=null)
				{
					backState=payMoney.get("BACKSTATE").toString();
				}
				if(backState.equals("0"))
				{
					payDw= (List) DataAccessor.query("rentContract.payMoneyBankManagerByRECTID", context.contextMap,DataAccessor.RS_TYPE.LIST);
				}
				else
				{
					payDw= (List) DataAccessor.query("rentContract.payMoneyBankManagerByRECTIDByMargin", context.contextMap,DataAccessor.RS_TYPE.LIST);
				}

			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("auditRe", auditRe);
			outputMap.put("payMoney", payMoney);
			outputMap.put("payDw", payDw);
			outputMap.put("FSS_ID", context.contextMap.get("FSS_ID"));
			Output.jspOutput(outputMap, context,"/payMoney/payMoney.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	@SuppressWarnings("unchecked")
	public void payMoneyManagerByOneInsurance(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map payMoney=null;
		if (errList.isEmpty()) {		
			try {
				payMoney = (Map) DataAccessor.query("payMoney.payMoneyManagerInsurance", context.contextMap,DataAccessor.RS_TYPE.MAP);
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("payMoney", payMoney);
			Output.jspOutput(outputMap, context,"/payMoney/payInsurance.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	//奖金付款
	@SuppressWarnings("unchecked")
	public void payMoneyManagerByOneBonus(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;
		List auditRe = null;
		Map payMoney=null;
		if (errList.isEmpty()) {		
			try {
				payMoney = (Map) DataAccessor.query("payMoney.payMoneyManagerInsurance", context.contextMap,DataAccessor.RS_TYPE.MAP);
				auditRe = (List) DataAccessor.query("rentContract.getLogByCreditId",context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("auditRe",auditRe);
			outputMap.put("payMoney", payMoney);
			Output.jspOutput(outputMap, context,"/payMoney/payBonus.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	@SuppressWarnings("unchecked")
	public void payMoneyManagerByOneShow(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map payMoney=null;
		List payDw=null;
		List auditRe = null;
		if (errList.isEmpty()) {		
			try {
				payMoney = (Map) DataAccessor.query("payMoney.payMoneyManager", context.contextMap,DataAccessor.RS_TYPE.MAP);
				auditRe = (List) DataAccessor.query("rentContract.getLogByCreditId",context.contextMap, DataAccessor.RS_TYPE.LIST);
				Object BACKSTATE=payMoney.get("BACKSTATE");
				String backState=null;
				if(BACKSTATE!=null)
				{
					backState=payMoney.get("BACKSTATE").toString();
				}
				if(backState.equals("0"))
				{
					payDw= (List) DataAccessor.query("rentContract.payMoneyBankManagerByRECTID", context.contextMap,DataAccessor.RS_TYPE.LIST);
				}
				else
				{
					payDw= (List) DataAccessor.query("rentContract.payMoneyBankManagerByRECTIDByMargin", context.contextMap,DataAccessor.RS_TYPE.LIST);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("auditRe", auditRe);
			outputMap.put("payMoney", payMoney);
			outputMap.put("payDw", payDw);
			Output.jspOutput(outputMap, context,"/payMoney/payMoneyShow.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public void payMoneyManagerByOneInsuranceShow(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map payMoney=null;
		if (errList.isEmpty()) {		
			try {
				payMoney = (Map) DataAccessor.query("payMoney.payMoneyManagerInsurance", context.contextMap,DataAccessor.RS_TYPE.MAP);
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("payMoney", payMoney);
			Output.jspOutput(outputMap, context,"/payMoney/payMoneyInsuranceShow.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	//奖金拨款查看页
	@SuppressWarnings("unchecked")
	public void payMoneyManagerByOneBonusShow(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;
		List auditRe = null;
		Map payMoney=null;
		if (errList.isEmpty()) {		
			try {
				payMoney = (Map) DataAccessor.query("payMoney.payMoneyManagerInsurance", context.contextMap,DataAccessor.RS_TYPE.MAP);
				auditRe = (List) DataAccessor.query("rentContract.getLogByCreditId",context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("auditRe",auditRe);
			outputMap.put("payMoney", payMoney);
			Output.jspOutput(outputMap, context,"/payMoney/payMoneyBonusShow.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public void updatePayMoneyPass(Context context) throws Exception{
		try {
			payMoneyService.doPayMoney(context);
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
		//插入金蝶待抛转数据 add by ShenQi
		Map<String,Object> creditMap=(Map<String,Object>)DataAccessor.query("kingDeer.queryPayDetail",context.contextMap,RS_TYPE.MAP);
		if(creditMap!=null) {
			//乘用车委托贷款
			if(Constants.CONTRACT_TYPE_8.equals(creditMap.get("CONTRACT_TYPE")+"")||Constants.CONTRACT_TYPE_14.equals(creditMap.get("CONTRACT_TYPE")+"")) {
				creditMap.put("SYSTEM_CODE",KingDeerConstants.CAR_PAY_1);
				DataAccessor.execute("kingDeer.insertPayForCar",creditMap,OPERATION_TYPE.INSERT);
			} else if(Constants.CONTRACT_TYPE_10.equals(creditMap.get("CONTRACT_TYPE")+"")||//乘用车回租
						Constants.CONTRACT_TYPE_12.equals(creditMap.get("CONTRACT_TYPE")+"")||
						Constants.CONTRACT_TYPE_13.equals(creditMap.get("CONTRACT_TYPE")+"")) {
				
			}
		}
		queryPayMoney(context);
	}
	
	@SuppressWarnings("unchecked")
	public void updatePayMoneyBack(Context context){
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		SqlMapClient sqlMap = DataAccessor.getSession();
		context.contextMap.put("userId", context.request.getSession().getAttribute("s_employeeId"));
		if (errList.isEmpty()) {		
			try {
				sqlMap.startTransaction();
				sqlMap.update("payMoney.payMoneyBack", context.contextMap);
				
				sqlMap.update("rentContract.updateRentFileStateByManage",
						context.contextMap);
				BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap
						.get("CREDIT_ID")), Long
		.parseLong(StringUtils
				.isEmpty((String) context.contextMap
						.get("RECT_ID")) ? "0"
				: (String) context.contextMap
						.get("RECT_ID")), 
		"付款管理", "审核", "", "总经理驳回", 1, DataUtil.longUtil(context.contextMap
				.get("s_employeeId")), null, context.contextMap.get("IP").toString());
				sqlMap.commitTransaction();	
			} catch (Exception e) {
				try {
					sqlMap.endTransaction();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context,"/servlet/defaultDispatcher?__action=payMoney.queryPayMoney");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	
	/**
	 * 根据合同编号新建一条付款记录
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void createPayMoneyManager(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;		
		if(errList.isEmpty()) {
			try {			
				DataAccessor.execute("payMoney.createPayMoneyManager", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if(errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=payMoney.queryPayMoney");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}	
	
	//批量审批通过 add by ShenQi
	@SuppressWarnings("unchecked")
	public void batchPass(Context context) throws Exception {
		
		//参数从前台payMoneyContract.jsp的复选框获得,参数依次包括ID,CREDIT_ID,BACKSTATE,LEASE_CODE,CUST_NAME
		String [] params=HTMLUtil.getParameterValues(context.getRequest(), "id|credit", null);
		
		//设定pass参数是为了调用this.updatePayMoneyPass不进行页面跳转
		context.contextMap.put("pass", "batchPass");
		//验证前台
		if(params==null) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=payMoney.queryPayMoney");
			return;
		}
		//设备款,保证金款批量更新
		if("common".equalsIgnoreCase(params[0].split("=")[5])) {
			String [] param=null;
			String id=null;
			String creditId=null;
			String num=null;
			String userId=String.valueOf(context.contextMap.get("s_employeeId"));
			String leaseCode=null;
			String custName=null;
			Map payMoney=null;
			List errList=context.errList;
			Map outputMap=new HashMap();

			for(int i=0;i<params.length;i++) {
				param=params[i].split("=");
				id=param[0];
				creditId=param[1]; 
				num=param[2];
				leaseCode=param[3];
				custName=param[4];
				context.contextMap.put("ID", id);
				context.contextMap.put("payMoneyId", id);
				context.contextMap.put("userId", userId);
				context.contextMap.put("Num", num);
				context.contextMap.put("CREDIT_ID",creditId);
				context.contextMap.put("leascode",leaseCode);
				context.contextMap.put("custName",custName);
				context.contextMap.put("dataType","拨款方式");
				try {
					//调用原先的方法,没有可参考的注释 - -!
					payMoney=(Map)DataAccessor.query("payMoney.payMoneyManager", context.contextMap,DataAccessor.RS_TYPE.MAP);
					if("0".equals(num)) {
						if(payMoney==null) {
							context.contextMap.put("FINANCECONTRACT_DATE",null);
						} else {
							context.contextMap.put("FINANCECONTRACT_DATE",payMoney.get("FINANCECONTRACT_DATE"));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
				if(!errList.isEmpty()) {
					outputMap.put("errList", errList);
					Output.jspOutput(outputMap, context, "/error.jsp");
				}
				this.updatePayMoneyPass(context);
			}//奖金拨款   TODO暂时没有此功能
		} else if("bonus".equalsIgnoreCase(params[0].split("=")[5])) {
			String [] param=null;
			String id=null;
			String userId=String.valueOf(context.contextMap.get("s_employeeId"));
			
			for(int i=0;i<params.length;i++) {
				param=params[i].split("=");
				id=param[0];
				
				context.contextMap.put("ID", id);
				context.contextMap.put("userId", userId);
				
				this.updatePayMoneyPass(context);
			}
		}
		
		//Output.jspSendRedirect(context,"defaultDispatcher?__action=payMoney.queryPayMoney");
		this.queryPayMoney(context);
	}
	
	//查询该供应商的所有付款记录
//	public void payMoneyByRect_ID(Context context)
//	{
//		Map outputMap = new HashMap();
//		List errList = context.errList;
//		List dw=null;
//		
//		if (errList.isEmpty()) {		
//			try {
//				
//				//查询该供应商的所有付款记录
//				dw = (List) DataAccessor.query("payMoney.payMoneyByRect_ID", context.contextMap,DataAccessor.RS_TYPE.LIST);
//				
//			} catch (Exception e) {
//				e.printStackTrace();
//				LogPrint.getLogStackTrace(e, logger);
//				errList.add(e);
//			}
//		}		
//		if (errList.isEmpty()) {
//			
//			outputMap.put("dw", dw);
//			Output.jspOutput(outputMap, context,"/backMoney/backMoneyDetal.jsp"); 
//		} else {
//			outputMap.put("errList", errList);
//			Output.jspOutput(outputMap, context, "/error.jsp");
//		}
//	}

	@SuppressWarnings("unchecked")
	public void updateReturnMoneyPass(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList;
		SqlMapClient sqlMap = DataAccessor.getSession();
		if (errList.isEmpty()) {		
			try {
				sqlMap.startTransaction();
				if("0".equals(context.contextMap.get("num"))){   //0 通过
					sqlMap.update("payMoney.payMoneyPass", context.contextMap);
				}else{
					sqlMap.update("payMoney.payMoneyBack", context.contextMap);
				}
				
				String memo = "退款审批-总经理审核-"
						+ ("0".equals(context.contextMap.get("num")) ? "通过"
								: "驳回");   //0 通过  1 驳回
				context.contextMap.put("memo",memo);
				sqlMap.insert("rentContract.addRenturnMoneyLog", context.contextMap);
				StringBuffer mailTo = new StringBuffer();
				StringBuffer mailCc = new StringBuffer();
				Map sendEmailMap = (Map)DataAccessor.query("payMoney.getReturnMoneyEmail", context.contextMap, DataAccessor.RS_TYPE.MAP);
				List<String> assistantMail = sqlMap.queryForList("payMoney.getAssistantMail", context.contextMap);
				
				if(!StringUtils.isEmpty((String)sendEmailMap.get("EMAIL"))) {
					context.contextMap.put("BACKSTATE", "5");
					Map result=(Map)DataAccessor.query("decompose.getEmaliContent",context.contextMap,DataAccessor.RS_TYPE.MAP);
						
					StringBuffer content=new StringBuffer();
					content.append(memo);
					if(result!=null) {
						content.append("<br>"+"<table><tr><td>申请人</td><td>收款人</td><td>退款金额</td><td>预计退款日</td></tr>" +
								"<tr><td>"+result.get("NAME")+"</td><td>"+result.get("BACKCOMP")+"</td><td>"+result.get("PAY_MONEY")+"</td><td>"+result.get("EXPECTEDDATE")+"</td></tr></table>");
					}
					mailTo.append(sendEmailMap.get("EMAIL"));
					mailCc.append(sendEmailMap.get("UPPER_EMAIL"));
					if (assistantMail != null && assistantMail.size() > 0) {
						for (String string : assistantMail) {
							mailTo.append(";");
							mailTo.append(string);
						}
					}
					MailSettingTo mailSettingTo=new MailSettingTo();
					mailSettingTo.setEmailFrom("tacfinance_service@tacleasing.cn");
					mailSettingTo.setEmailTo(mailTo.toString());
					mailSettingTo.setEmailCc(mailCc.toString());
					mailSettingTo.setEmailSubject("退款付款审批");
					mailSettingTo.setEmailContent(content.toString());
					mailSettingTo.setCreateBy("184");//184是系统
	
					this.mailUtilService.sendMail(mailSettingTo);
				} 
				sqlMap.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}finally {
				try {
					sqlMap.endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}	
		
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context,"/servlet/defaultDispatcher?__action=payMoney.queryPayMoney&BACKSTATE=5");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/**
	 * 乘用车手续费/佣金付款审批
	 * @param context
	 */
	public void updateHandlingChargePass(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList;
		SqlMapClient sqlMap = DataAccessor.getSession();
		String backState = (String) context.contextMap.get("BACKSTATE");
		String payType = null;
		if ("9".equals(backState)) {
			payType = "手续费";
		} else if ("10".equals(backState)) {
			payType = "佣金";
		}
		if (errList.isEmpty()) {		
			try {
				sqlMap.startTransaction();
				if("0".equals(context.contextMap.get("num"))){   //0 通过
					sqlMap.update("payMoney.payMoneyPass", context.contextMap);
				}else{
					sqlMap.update("payMoney.payMoneyBack", context.contextMap);
				}
				
				String memo = payType + "拨款审批-总经理审核-"
						+ ("0".equals(context.contextMap.get("num")) ? "通过"
								: "驳回");   //0 通过  1 驳回
				context.contextMap.put("memo",memo);
				StringBuffer mailTo = new StringBuffer();
				StringBuffer mailCc = new StringBuffer();
				Map sendEmailMap = (Map)DataAccessor.query("payMoney.getHandlingChargeEmail", context.contextMap, DataAccessor.RS_TYPE.MAP);
				List<String> assistantMail = sqlMap.queryForList("payMoney.getAssistantMail", context.contextMap);
				
				if(!StringUtils.isEmpty((String)sendEmailMap.get("EMAIL"))) {
					Map result=(Map)DataAccessor.query("decompose.getEmaliContent",context.contextMap,DataAccessor.RS_TYPE.MAP);
						
					StringBuffer content=new StringBuffer();
					content.append(memo);
					if(result!=null) {
						content.append("<br>" + "申请人：" + result.get("NAME"));
						content.append("<br>" + "申请日期：" + context.contextMap.get("application_date"));
						content.append("<br>" + "受款人：" + context.contextMap.get("payee"));
						content.append("<br>" + "银行名称：" + context.contextMap.get("bank_name"));
						content.append("<br>" + "银行账号：" + context.contextMap.get("bank_account"));
						content.append("<br>" + "金额：" + context.contextMap.get("amount"));
						content.append("<br>" + "支付方式：" + context.contextMap.get("pay_type"));
						
					}
					mailTo.append(sendEmailMap.get("EMAIL"));
					mailCc.append(sendEmailMap.get("UPPER_EMAIL"));
					if (assistantMail != null && assistantMail.size() > 0) {
						for (String string : assistantMail) {
							mailTo.append(";");
							mailTo.append(string);
						}
					}
					MailSettingTo mailSettingTo=new MailSettingTo();
					mailSettingTo.setEmailFrom("tacfinance_service@tacleasing.cn");
					mailSettingTo.setEmailTo(mailTo.toString());
					mailSettingTo.setEmailCc(mailCc.toString());
					mailSettingTo.setEmailSubject("乘用车" + payType + "付款审批");
					mailSettingTo.setEmailContent(content.toString());
					mailSettingTo.setCreateBy("184");//184是系统
					this.mailUtilService.sendMail(mailSettingTo);
				} 
				sqlMap.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}finally {
				try {
					sqlMap.endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}	
		
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context,"/servlet/defaultDispatcher?__action=payMoney.queryPayMoney&BACKSTATE=" + backState);
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	public void approvalAllInsuPay(Context context) throws Exception{
		String[] pay_ids = context.request.getParameterValues("pay_id");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", context.request.getSession().getAttribute("s_employeeId"));
		for (String pay_id : pay_ids) {
			System.out.println(pay_id);
			paramMap.put("ID", pay_id);
			baseService.update("payMoney.payMoneyPass", paramMap);
		}
		queryPayMoney(context);
	}
	
}
