package com.brick.customer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.customer.CustomerConstants;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.DataUtil;
import com.brick.util.web.HTMLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;

public class CustomerCredit extends AService {
	Log logger = LogFactory.getLog(CustomerCredit.class);
	public static enum GUIHU_TYPE {OWN, IRR};
	/**
	 * 查询全部客户授信
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryAllCustCredit(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;
		DataWrap dw = null;		
		
		if (errList.isEmpty()) {		
			try {
				
				dw = (DataWrap) DataAccessor.query("custCredit.queryAllCustCredit", context.contextMap,DataAccessor.RS_TYPE.PAGED);		
				if(dw != null){
					List rs = (List) dw.getRs() ;
					for(int i=0;i < rs.size();i++){
						Map temp = (Map) rs.get(i) ;
						if(temp.get("CUGP_ID") != null){
							temp.put("LAST_PRICE",this.getCustCredit(temp.get("CUST_ID"))) ;
						}
					}
				}
				outputMap.put("custInfor", DictionaryUtil.getDictionary("承租人级别")); 
			
			} catch (Exception e) {
				errList.add("com.brick.customer.service.queryAllCustCredit"+ e.toString());
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("dw", dw);
			outputMap.put("res", HTMLUtil.getStrParam(context.request, "res", ""));
			outputMap.put("QCUSTLEVEL", context.contextMap.get("QCUSTLEVEL"));
			outputMap.put("QSTARTMONEY", context.contextMap.get("QSTARTMONEY"));
			outputMap.put("QENDMONEY", context.contextMap.get("QENDMONEY"));
			outputMap.put("QCUGPDTATUS", context.contextMap.get("QCUGPDTATUS"));
			outputMap.put("QSEARCHVALUE", context.contextMap.get("QSEARCHVALUE"));
			if("ccm".equals(HTMLUtil.getStrParam(context.request, "res", "") ))
			{
				Output.jspOutput(outputMap, context,"/customercredit/custcreditManage.jsp"); 
			}
			else if("cs".equals(HTMLUtil.getStrParam(context.request, "res", "") ))
			{
				Output.jspOutput(outputMap, context,"/customercredit/custShow.jsp"); 
			}else
			{
				Output.jspOutput(outputMap, context,"/customercredit/custcreditManage.jsp"); 
			}
		} else {
			// 跳转到错误页面
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/**
	 * 查询日志
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryLog(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;
		DataWrap dw = null;	
		double last_price=0.0;
		Map cust = null ;
		Map grant = null ;
		if (errList.isEmpty()) {		
			try {
				context.contextMap.put("cust_id", context.contextMap.get("CUST_ID"));
				cust = (Map) DataAccessor.query("customer.readCustNatu", context.contextMap, DataAccessor.RS_TYPE.MAP);
				grant =  (Map)DataAccessor.query("custCredit.queryAllGrantplanById", context.contextMap,DataAccessor.RS_TYPE.MAP);
				last_price=this.getCustCredit(context.contextMap.get("CUST_ID"));
				if(grant!=null){
					grant.put("LAST_PRICE", last_price);
				}
				dw = (DataWrap) DataAccessor.query("custCredit.queryLog", context.contextMap,DataAccessor.RS_TYPE.PAGED);		
				outputMap.put("custInfor", DictionaryUtil.getDictionary("承租人级别")); 
				
			} catch (Exception e) {
				errList.add("取消客户授信出错"+ e.toString());
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("cust", cust);
			outputMap.put("grantplan", grant);
			outputMap.put("dw", dw);
			outputMap.put("content", context.contextMap.get("content"));
//			Output.jspSendRedirect(context,"defaultDispatcher?__action=custCredit.queryAllCustCredit");
			Output.jspOutput(outputMap, context,"/customercredit/showlog.jsp"); 
		} else {
			// 跳转到错误页面
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/**
	 * 进入授信管理页面
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getCustCreditJsp(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map cust = null;		
		Map grantplan = null;	
		double last_price=0.0;
		DataWrap grantdetail = null;		
		if (errList.isEmpty()) {		
			try {
				cust = (Map) DataAccessor.query("custCredit.queryAllCustCredit", context.contextMap, DataAccessor.RS_TYPE.MAP);
				grantplan =  (Map)DataAccessor.query("custCredit.queryAllGrantplanById", context.contextMap,DataAccessor.RS_TYPE.MAP);	
				last_price=this.getCustCredit(context.contextMap.get("CUST_ID"));
				
				if(grantplan!=null)
				{   grantplan.put("LAST_PRICE", last_price);
					context.contextMap.put("CUGP_ID", grantplan.get("CUGP_ID"));
					grantdetail =  (DataWrap)DataAccessor.query("custCredit.queryAllGrantdetailById", context.contextMap,DataAccessor.RS_TYPE.PAGED);	
				}
				outputMap.put("payInfor", DictionaryUtil.getDictionary("支付方式"));
			} catch (Exception e) {
				e.printStackTrace();
				errList.add("com.brick.customer.service.getCustCreditJsp"+ e.toString());
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("cust", cust);
			outputMap.put("grantplan", grantplan);
			outputMap.put("grantdetail", grantdetail);
			Output.jspOutput(outputMap, context,"/customercredit/createCustCredit.jsp"); 
		} else {
			// 跳转到错误页面
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/**
	 * 添加客户授信、授信方案
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void creatCustCreditPlan(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map cust = null;		
		Map grant = null;		
		Map grantplan = null;		
		DataWrap grantdetail = null;		
		Map result = null ;
		if (errList.isEmpty()) {		
			try {
				cust = (Map) DataAccessor.query("customer.readCustNatu", context.contextMap, DataAccessor.RS_TYPE.MAP);
				grant =  (Map)DataAccessor.query("custCredit.queryAllGrantplanById", context.contextMap,DataAccessor.RS_TYPE.MAP);		
				outputMap.put("payInfor", DictionaryUtil.getDictionary("支付方式"));
				context.contextMap.put("CUGP_STATUS", 0);
				if(grant == null)
				{
					context.contextMap.put("CREATE_ID", DataUtil.intUtil(context.request.getSession().getAttribute("s_employeeId")));
					context.contextMap.put("MOFIFY_ID", DataUtil.intUtil(context.request.getSession().getAttribute("s_employeeId")));
					DataAccessor.execute("custCredit.creatCustCreditPlan", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
					result = (Map)DataAccessor.query("custCredit.queryAllGrantplanById", context.contextMap, DataAccessor.RS_TYPE.MAP);
					//添加日志
					context.contextMap.put("CREATE_ID", DataUtil.intUtil(context.request.getSession().getAttribute("s_employeeId")));
					context.contextMap.put("MODIFY_ID", DataUtil.intUtil(context.request.getSession().getAttribute("s_employeeId")));
					CustomerCredit.customercreditlog(context.contextMap, CustomerConstants.CUGL_STATUS_CREATE);
				}
				else
				{
					context.contextMap.put("MOFIFY_ID", context.request.getSession().getAttribute("s_employeeId"));
					DataAccessor.execute("custCredit.updateCustCreditPlan", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
					//添加日志
					context.contextMap.put("CREATE_ID", DataUtil.intUtil(context.request.getSession().getAttribute("s_employeeId")));
					context.contextMap.put("MODIFY_ID", DataUtil.intUtil(context.request.getSession().getAttribute("s_employeeId")));
					CustomerCredit.customercreditlog(context.contextMap, CustomerConstants.CUGL_STATUS_MODIFY);
				}
				String CUGP_ID = HTMLUtil.getStrParam(context.request, "CUGP_ID", "");
				if(CUGP_ID == null || "".equals(CUGP_ID))
				{
					CUGP_ID = result.get("CUGP_ID").toString();
				}
				String[] LEASE_PERIOD1 = HTMLUtil.getParameterValues(context.request, "LEASE_PERIOD1", "0");
				String[] LEASE_TERM1 = HTMLUtil.getParameterValues(context.request, "LEASE_TERM1", "0");
				String[] PAY_WAY1 = HTMLUtil.getParameterValues(context.request, "PAY_WAY1", "0");
				String[] MANAGEMENT_FEE_RATE1 = HTMLUtil.getParameterValues(context.request, "MANAGEMENT_FEE_RATE1", "0");
				String[] HEAD_HIRE_PERCENT1 = HTMLUtil.getParameterValues(context.request, "HEAD_HIRE_PERCENT1", "0");
				String[] PLEDGE_PRICE_RATE1 = HTMLUtil.getParameterValues(context.request, "PLEDGE_PRICE_RATE1", "0");
				String[] FLOAT_RATE1 = HTMLUtil.getParameterValues(context.request, "FLOAT_RATE1", "0");
				
				String[] CUGD_ID = HTMLUtil.getParameterValues(context.request, "CUGD_ID", "0");
				String[] LEASE_PERIOD = HTMLUtil.getParameterValues(context.request, "LEASE_PERIOD", "0");
				String[] LEASE_TERM = HTMLUtil.getParameterValues(context.request, "LEASE_TERM", "0");
				String[] PAY_WAY = HTMLUtil.getParameterValues(context.request, "PAY_WAY", "0");
				String[] MANAGEMENT_FEE_RATE = HTMLUtil.getParameterValues(context.request, "MANAGEMENT_FEE_RATE", "0");
				String[] HEAD_HIRE_PERCENT = HTMLUtil.getParameterValues(context.request, "HEAD_HIRE_PERCENT", "0");
				String[] PLEDGE_PRICE_RATE = HTMLUtil.getParameterValues(context.request, "PLEDGE_PRICE_RATE", "0");
				String[] FLOAT_RATE = HTMLUtil.getParameterValues(context.request, "FLOAT_RATE", "0");
				
				
				if (LEASE_PERIOD1.length != 0) {
					for (int i = 0; i < LEASE_PERIOD1.length; i++) {
						
						Map mapCreate = new HashMap();
						
						mapCreate.put("CUGP_ID", DataUtil.intUtil(CUGP_ID));
						mapCreate.put("LEASE_PERIOD", DataUtil.intUtil(LEASE_PERIOD1[i]));
						mapCreate.put("LEASE_TERM", DataUtil.intUtil(LEASE_TERM1[i]));
						mapCreate.put("PAY_WAY", DataUtil.intUtil(PAY_WAY1[i]));
						mapCreate.put("MANAGEMENT_FEE_RATE", DataUtil.doubleUtil(MANAGEMENT_FEE_RATE1[i]));
						mapCreate.put("HEAD_HIRE_PERCENT",  DataUtil.doubleUtil(HEAD_HIRE_PERCENT1[i]));
						mapCreate.put("PLEDGE_PRICE_RATE",  DataUtil.doubleUtil(PLEDGE_PRICE_RATE1[i]));
						mapCreate.put("FLOAT_RATE",  DataUtil.doubleUtil(FLOAT_RATE1[i]));
						
						mapCreate.put("CREATE_ID", DataUtil.intUtil(context.request.getSession().getAttribute("s_employeeId")));
						mapCreate.put("MODIFY_ID", DataUtil.intUtil(context.request.getSession().getAttribute("s_employeeId")));
						DataAccessor.execute("custCredit.createCustCreditDetail",mapCreate,DataAccessor.OPERATION_TYPE.INSERT);
						
					}
				}
				if(CUGD_ID.length != 0)
				{
					for (int j = 0; j < CUGD_ID.length; j++) {
						Map mapUpdate = new HashMap();
						mapUpdate.put("CUGP_ID", DataUtil.intUtil(CUGP_ID));
						mapUpdate.put("CUGD_ID", DataUtil.intUtil(CUGD_ID[j]));
						mapUpdate.put("LEASE_PERIOD", DataUtil.intUtil(LEASE_PERIOD[j]));
						mapUpdate.put("LEASE_TERM", DataUtil.intUtil(LEASE_TERM[j]));
						mapUpdate.put("PAY_WAY", DataUtil.intUtil(PAY_WAY[j]));
						mapUpdate.put("MANAGEMENT_FEE_RATE", DataUtil.doubleUtil(MANAGEMENT_FEE_RATE[j]));
						mapUpdate.put("HEAD_HIRE_PERCENT", DataUtil.doubleUtil(HEAD_HIRE_PERCENT[j]));
						mapUpdate.put("PLEDGE_PRICE_RATE", DataUtil.doubleUtil(PLEDGE_PRICE_RATE[j]));
						mapUpdate.put("FLOAT_RATE", DataUtil.doubleUtil(FLOAT_RATE[j]));
						mapUpdate.put("MOFIFY_ID", DataUtil.intUtil(context.request.getSession().getAttribute("s_employeeId")));
						DataAccessor.execute("custCredit.updateCustCreditDetail",mapUpdate,DataAccessor.OPERATION_TYPE.UPDATE);
					}
				}
				grantplan =  (Map)DataAccessor.query("custCredit.queryAllGrantplanById", context.contextMap,DataAccessor.RS_TYPE.MAP);	
				context.contextMap.put("CUGP_ID", grantplan.get("CUGP_ID"));
				grantdetail =  (DataWrap)DataAccessor.query("custCredit.queryAllGrantdetailById", context.contextMap,DataAccessor.RS_TYPE.PAGED);
				
			} catch (Exception e) {
				e.printStackTrace();
				errList.add("creatCustCreditPlan 出错"+ e.toString());
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("cust", cust);
			outputMap.put("grantplan", grantplan);
			outputMap.put("grantdetail", grantdetail);
			Output.jspSendRedirect(context,"defaultDispatcher?__action=custCredit.queryAllCustCredit");
//			Output.jspOutput(outputMap, context,"/customercredit/createCustCredit.jsp"); 
		} else {
			// 跳转到错误页面
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/**
	 * 2010-12-07 guanl
	 * 添加日志
	 * @param paramMap
	 * @param LAST_PRICE 此次释放资金
	 * @param S_EMPLOYEEID 操作人
	 * @param CUST_ID 承租人id
	 * @param MEMO 情况说明
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public static void customercreditlog(Map paramMap,Integer customerConstants) throws Exception {
		String memo = CustomerCredit.createDemo(paramMap, customerConstants);
		paramMap.put("MEMO", memo);
		paramMap.put("GRANT_PRICE", DataUtil.floatUtil(paramMap.get("GRANT_PRICE")));
		paramMap.put("LAST_PRICE", DataUtil.floatUtil(paramMap.get("LAST_PRICE")));
		// 添加授信日志
		paramMap.put("CUGL_STATUS", customerConstants);//CustomerConstants.CUGL_STATUS_MODIFY);
		DataAccessor.execute("custCredit.customercreditlog", paramMap, DataAccessor.OPERATION_TYPE.INSERT);
		
	}
	
	public static String createDemo(Map paramMap,Integer customerConstants) throws Exception {
		String memo = "";
		if(customerConstants == 5)
		{
			Map result = (Map)DataAccessor.query("custCredit.queryAllGrantplanById", paramMap, DataAccessor.RS_TYPE.MAP);
			String newCUGP_CODE = (String)paramMap.get("CUGP_CODE");
			String oldCUGP_CODE= (String)result.get("CUGP_CODE");
			if(newCUGP_CODE.equals(oldCUGP_CODE))
			{
				memo += "协议编号未修改！" ;
			}
			else
			{
				memo += "协议编号从"+oldCUGP_CODE+"修改为"+newCUGP_CODE+"！" ;
			}
			String newGRANT_PRICE = paramMap.get("GRANT_PRICE").toString();
			String oldGRANT_PRICE= result.get("GRANT_PRICE").toString();
			if(newGRANT_PRICE.equals(oldGRANT_PRICE))
			{
				memo += "授信金额未修改！" ;
			}
			else
			{
				memo += "授信金额从"+oldGRANT_PRICE+"修改为"+newGRANT_PRICE+"！" ;
			}
			
			String newLAST_PRICE = (String)paramMap.get("LAST_PRICE");
			String oldLAST_PRICE= (String)result.get("LAST_PRICE").toString();
			if(newLAST_PRICE.equals(oldLAST_PRICE))
			{
				memo += "授信余额未修改！" ;
			}
			else
			{
				memo += "授信余额从"+oldLAST_PRICE+"修改为"+newLAST_PRICE+"！" ;
			}
			
			String newSTART_DATE = (String)paramMap.get("START_DATE");
			String oldSTART_DATE= (String)result.get("START_DATE").toString();
			if(newSTART_DATE.equals(oldSTART_DATE))
			{
				memo += "起始日期未修改！" ;
			}
			else
			{
				memo += "起始日期从"+oldSTART_DATE+"修改为"+newSTART_DATE+"！" ;
			}
			
			String newEND_DATE = (String)paramMap.get("END_DATE");
			String oldEND_DATE= (String)result.get("END_DATE").toString();
			if(newCUGP_CODE.equals(oldCUGP_CODE))
			{
				memo += "结束日期未修改！" ;
			}
			else
			{
				memo += "结束日期从"+oldEND_DATE+"修改为"+newEND_DATE+"！" ;
			}
			
		}
		else if(customerConstants == 4)
		{
			memo += "删除记录";
		}
		else if(customerConstants == 3)
		{
			memo += "取消授信";
		}
		else if(customerConstants == 0)
		{
			memo += "添加授信";
		}
		return memo ;
	}
	
	/**
	 * 取消授信
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void delCustCreditPlan(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;
		DataWrap dw = null;		
		if (errList.isEmpty()) {		
			try {
				DataAccessor.execute("custCredit.delCreditPlan", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
				dw = (DataWrap) DataAccessor.query("custCredit.queryAllCustCredit", context.contextMap,DataAccessor.RS_TYPE.PAGED);		
				outputMap.put("custInfor", DictionaryUtil.getDictionary("承租人级别")); 
				//添加日志
				context.contextMap.put("CREATE_ID", DataUtil.intUtil(context.request.getSession().getAttribute("s_employeeId")));
				context.contextMap.put("MODIFY_ID", DataUtil.intUtil(context.request.getSession().getAttribute("s_employeeId")));
				CustomerCredit.customercreditlog(context.contextMap, CustomerConstants.CUGL_STATUS_DEL);

			} catch (Exception e) {
				errList.add("取消客户授信出错"+ e.toString());
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("dw", dw);
			outputMap.put("content", context.contextMap.get("content"));
			Output.jspSendRedirect(context,"defaultDispatcher?__action=custCredit.queryAllCustCredit");
//			Output.jspOutput(outputMap, context,"/customercredit/custcreditManage.jsp"); 
		} else {
			// 跳转到错误页面
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	/**
	 * 删除授信方案
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void delCustCreditDetail(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map cust = null;		
		Map grantplan = null;		
		DataWrap grantdetail = null;		
		if (errList.isEmpty()) {		
			try {
				cust = (Map) DataAccessor.query("customer.readCustNatu", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("payInfor", DictionaryUtil.getDictionary("支付方式"));
				
				String[] CUGD_ID = HTMLUtil.getParameterValues(context.request, "oldbox", "0");
				if (CUGD_ID.length != 0) {
					for (int i = 0; i < CUGD_ID.length; i++) {
						
						Map map = new HashMap();
						map.put("CUGD_ID", DataUtil.intUtil(CUGD_ID[i]));
						map.put("STATUS", -2);
						DataAccessor.execute("custCredit.delCustCreditDetail",map,DataAccessor.OPERATION_TYPE.UPDATE);
					}
				}
				grantplan =  (Map)DataAccessor.query("custCredit.queryAllGrantplanById", context.contextMap,DataAccessor.RS_TYPE.MAP);	
				context.contextMap.put("CUGP_ID", grantplan.get("CUGP_ID"));
				grantdetail =  (DataWrap)DataAccessor.query("custCredit.queryAllGrantdetailById", context.contextMap,DataAccessor.RS_TYPE.PAGED);
			} catch (Exception e) {
				e.printStackTrace();
				errList.add("删除收信方案"+ e.toString());
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("cust", cust);
			outputMap.put("grantplan", grantplan);
			outputMap.put("grantdetail", grantdetail);
			Output.jspOutput(outputMap, context,"/customercredit/createCustCredit.jsp"); 
		} else {
			// 跳转到错误页面
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/**
	 * 2010-12-07 wujw
	 * 占用承租人授信余额
	 * @param paramMap
	 * @param LAST_PRICE 此次占用资金
	 * @param S_EMPLOYEEID 操作人
	 * @param CUST_ID 承租人id
	 * @param MEMO 占用情况说明
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public static void plusCreditLastPrice(Map paramMap) throws Exception {
		// 占用授信余额
		DataAccessor.execute("custCredit.plus-credit-lastprice", paramMap, DataAccessor.OPERATION_TYPE.UPDATE);
		// 添加授信日志
		paramMap.put("CUGL_STATUS", CustomerConstants.CUGL_STATUS_OCCUPY);
		DataAccessor.execute("custCredit.create-customer-creditlog", paramMap, DataAccessor.OPERATION_TYPE.INSERT);
		
	}
	/**
	 * 2010-12-07 wujw
	 * 释放承租人授信余额
	 * @param paramMap
	 * @param LAST_PRICE 此次释放资金
	 * @param S_EMPLOYEEID 操作人
	 * @param CUST_ID 承租人id
	 * @param MEMO 释放情况情况说明
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public static void subCreditLastprice(Map paramMap) throws Exception {
		// 释放授信余额
		DataAccessor.execute("custCredit.sub-credit-lastprice", paramMap, DataAccessor.OPERATION_TYPE.UPDATE);
		// 添加授信日志
		paramMap.put("CUGL_STATUS", CustomerConstants.CUGL_STATUS_FREE);
		DataAccessor.execute("custCredit.create-customer-creditlog", paramMap, DataAccessor.OPERATION_TYPE.INSERT);
	
	}
	/**
	 * yqc
	 * 通过承租人id取得授信额度
	 * @param custId 承租人id
	 */
	public static double getCustCredit(Object custId) throws Exception{
		Double price = 0.0d ;
		if(custId != null){
			Map<String,Object> temp = new HashMap<String,Object>() ;
			temp.put("cust_id", custId) ;
			price = (Double) DataAccessor.query("custCredit.getCustCreditByCustId", temp, DataAccessor.RS_TYPE.OBJECT) ;
			if(price == null){
				price = 0.0 ;
			}
		}
		return price ;
	}
	/**
	 * yqc
	 * 通过承租人id取得归户
	 * @param custId 承租人id
	 * 
	 */
	public static double getCustGuiHu(Object custId,GUIHU_TYPE type) throws Exception{
		Double price = 0.0d ;
		if(custId != null){
			Map<String,Object> temp = new HashMap<String,Object>() ;
			temp.put("cust_id", custId) ;
			switch(type){
			case OWN :
				price = (Double) DataAccessor.query("custCredit.getCustGuiHuOWNByCustId", temp, DataAccessor.RS_TYPE.OBJECT) ;
				break ;
			case IRR :
				price = (Double) DataAccessor.query("custCredit.getCustGuiHuIRRByCustId", temp, DataAccessor.RS_TYPE.OBJECT) ;
				break ;
			}
			if(price == null){
				price = 0.0 ;
			}
		}
		return price ;
	}
	
	public static double getRentInfoByCreditId(Object credit_id,GUIHU_TYPE type) throws Exception{
		Double price = 0.0d ;
		if(credit_id != null){
			Map<String,Object> temp = new HashMap<String,Object>() ;
			temp.put("credit_id", credit_id) ;
			switch(type){
			case OWN :
				price = (Double) DataAccessor.query("custCredit.getCustGuiHuOWNByCreditId", temp, DataAccessor.RS_TYPE.OBJECT) ;
				break ;
			case IRR :
				price = (Double) DataAccessor.query("custCredit.getCustGuiHuIRRByCreditId", temp, DataAccessor.RS_TYPE.OBJECT) ;
				break ;
			}
			if(price == null){
				price = 0.0 ;
			}
		}
		return price ;
	}
	
	
}
