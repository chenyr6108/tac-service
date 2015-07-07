package com.brick.product.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.log.service.LogPrint;
import com.brick.product.ProductConstants;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.DataUtil;
import com.brick.util.web.HTMLUtil;

public class ProductCredit extends BaseCommand {
	Log logger = LogFactory.getLog(ProductCredit.class);
	
	private ProductCreditService productCreditService;
	
	public ProductCreditService getProductCreditService() {
		return productCreditService;
	}
	public void setProductCreditService(ProductCreditService productCreditService) {
		this.productCreditService = productCreditService;
	}
	/**
	 * 查询全部客户授信
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryAllProductCredit(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		PagingInfo<Object> dw = null;		
		try {				
			dw =  baseService.queryForListWithPaging("productCredit.queryAllproductCredit", context.contextMap, "MODITY_DATE", ORDER_TYPE.DESC);
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("dw", dw);			
		outputMap.put("res", HTMLUtil.getStrParam(context.request, "res", ""));			
		outputMap.put("QCUSTLEVEL", context.contextMap.get("QCUSTLEVEL"));
		outputMap.put("QSTARTMONEY", context.contextMap.get("QSTARTMONEY"));
		outputMap.put("QENDMONEY", context.contextMap.get("QENDMONEY"));
		outputMap.put("QCUGPDTATUS", context.contextMap.get("QCUGPDTATUS"));
		outputMap.put("QSEARCHVALUE", context.contextMap.get("QSEARCHVALUE"));
		//System.out.println(context.contextMap.get("res"));
		if("ccm".equals(HTMLUtil.getStrParam(context.request, "res", "") ))
		{
			Output.jspOutput(outputMap, context,"/productcredit/productcreditManage.jsp"); 
		}else if("cs".equals(HTMLUtil.getStrParam(context.request, "res", "") ))
		{
			Output.jspOutput(outputMap, context,"/productcredit/productShow.jsp"); 
		}else
		{
			//System.out.println(HTMLUtil.getStrParam(context.request, "res", ""));
			Output.jspOutput(outputMap, context,"/productcredit/productcreditManage.jsp"); 
		}
			
	}
	/**
	 * 进入授信管理页面
	 * @param context
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public void getProductCreditJsp(Context context) throws Exception
	{
		Map<String, Object> outputMap = new HashMap<String, Object>();
		Map<String, Object> product = null;		
		Map<String, Object> grantplan = null;
		PagingInfo<Object> logInfo = null;
		try {		
			product = (Map<String, Object>) DataAccessor.query("productCredit.queryAllproductCredit", context.contextMap, DataAccessor.RS_TYPE.MAP);
			grantplan =  (Map<String, Object>)DataAccessor.query("productCredit.queryAllGrantplanById", context.contextMap,DataAccessor.RS_TYPE.MAP);
			logInfo = baseService.queryForListWithPaging("productCredit.getGrantPlanLog", context.contextMap, "CREATE_DATE", ORDER_TYPE.DESC);
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
		outputMap.put("logInfo", logInfo);
		outputMap.put("product", product);
		outputMap.put("grantplan", grantplan);
		outputMap.put("modal", context.contextMap.get("modal"));
		//if(null!=context.contextMap.get("AUDIT")){
		//	Output.jspOutput(outputMap, context,"/productcredit/createProductCreditByAuditManager.jsp");
		//}else{
			Output.jspOutput(outputMap, context,"/productcredit/createProductCredit.jsp");
		//}
	}
	/**
	 * 添加客户授信、授信方案
	 * @param context
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public void creatProductCreditPlan(Context context) throws Exception{
		productCreditService.saveProductCreditPlan(context);
		//if("".equals(context.contextMap.get("AUDIT"))){
		//	Output.jspSendRedirect(context,"defaultDispatcher?__action=productCredit.queryAllProductCredit");
		//}else{
			//Output.jspSendRedirect(context,"defaultDispatcher?__action=productCredit.queryAllProductCreditForAuditMananger");
			getProductCreditJsp(context);
		//}
	}
	/**
	 * 添加客户授信、不关系授信方案
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void creatCreditPlanJust(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map grant = null;		
		if (errList.isEmpty()) {		
			try {
				grant =  (Map)DataAccessor.query("productCredit.queryAllproductCredit", context.contextMap,DataAccessor.RS_TYPE.MAP);	
				String PDGP_ID = null ;
				if(grant.get("PDGP_ID")!=null)
				{
					PDGP_ID = grant.get("PDGP_ID").toString();
				}
				context.contextMap.put("CUGP_STATUS", 0);
				if("".equals(PDGP_ID)||PDGP_ID==null)
				{
					context.contextMap.put("CREATE_ID", DataUtil.intUtil(context.request.getSession().getAttribute("s_employeeId")));
					context.contextMap.put("MOFIFY_ID", DataUtil.intUtil(context.request.getSession().getAttribute("s_employeeId")));
					DataAccessor.execute("productCredit.creatProductCreditPlan", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
					//添加日志
					context.contextMap.put("CREATE_ID", DataUtil.intUtil(context.request.getSession().getAttribute("s_employeeId")));
					context.contextMap.put("MODIFY_ID", DataUtil.intUtil(context.request.getSession().getAttribute("s_employeeId")));
					//productCreditService.productcreditlog(context.contextMap, ProductConstants.CUGL_STATUS_CREATE);
				}
				else
				{
					context.contextMap.put("MOFIFY_ID", context.request.getSession().getAttribute("s_employeeId"));
					DataAccessor.execute("productCredit.updateProductCreditPlan", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
					//添加日志
					context.contextMap.put("CREATE_ID", DataUtil.intUtil(context.request.getSession().getAttribute("s_employeeId")));
					context.contextMap.put("MODIFY_ID", DataUtil.intUtil(context.request.getSession().getAttribute("s_employeeId")));
					//productCreditService.productcreditlog(context.contextMap, ProductConstants.CUGL_STATUS_MODIFY);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				errList.add("creatProductCreditPlan 出错"+ e.toString());
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if (errList.isEmpty()) {
			Output.jsonOutput(outputMap, context);
//			Output.jspOutput(outputMap, context,"/customercredit/createCustCredit.jsp"); 
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
	public void delProductCreditDetail(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map product = null;		
		Map grantplan = null;		
		DataWrap grantdetail = null;		
		if (errList.isEmpty()) {		
			try {
				product = (Map) DataAccessor.query("supplier.query", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("payInfor", DictionaryUtil.getDictionary("支付方式"));
				
				String[] PDGD_ID = HTMLUtil.getParameterValues(context.request, "oldbox", "0");
				if (PDGD_ID.length != 0) {
					for (int i = 0; i < PDGD_ID.length; i++) {
						
						Map map = new HashMap();
						map.put("PDGD_ID", DataUtil.intUtil(PDGD_ID[i]));
						map.put("STATUS", -2);
						DataAccessor.execute("productCredit.delProductCreditDetail",map,DataAccessor.OPERATION_TYPE.UPDATE);
					}
				}
				grantplan =  (Map)DataAccessor.query("productCredit.queryAllGrantplanById", context.contextMap,DataAccessor.RS_TYPE.MAP);	
				context.contextMap.put("PDGP_ID", grantplan.get("PDGP_ID"));
				grantdetail =  (DataWrap)DataAccessor.query("productCredit.queryAllGrantdetailById", context.contextMap,DataAccessor.RS_TYPE.PAGED);
			} catch (Exception e) {
				e.printStackTrace();
				errList.add("删除收信方案"+ e.toString());
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("product", product);
			outputMap.put("grantplan", grantplan);
			outputMap.put("grantdetail", grantdetail);
			Output.jspOutput(outputMap, context,"/productcredit/createProductCredit.jsp"); 
		} else {
			// 跳转到错误页面
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	/**
	 * 取消授信
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void delProductCreditPlan(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;
		DataWrap dw = null;		
		if (errList.isEmpty()) {		
			try {
				DataAccessor.execute("productCredit.delCreditPlan", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
				
				DataAccessor.execute("productCredit.delUnionCreditPlan01", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
				DataAccessor.execute("productCredit.delUnionCreditPlan02", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
				
				
				/*
				 * 添加联合授信时 注释掉下面代码 不明白为什么要这样写 
				 * 因为跳转Output.jspSendRedirect(context,"defaultDispatcher?__action=productCredit.queryAllProductCredit");
				 * 
				 */
				//dw = (DataWrap) DataAccessor.query("productCredit.queryAllproductCredit", context.contextMap,DataAccessor.RS_TYPE.PAGED);		
				//outputMap.put("custInfor", DictionaryUtil.getDictionary("承租人级别")); 
				//添加日志
				context.contextMap.put("CREATE_ID", DataUtil.intUtil(context.request.getSession().getAttribute("s_employeeId")));
				context.contextMap.put("MODIFY_ID", DataUtil.intUtil(context.request.getSession().getAttribute("s_employeeId")));
				//productCreditService.productcreditlog(context.contextMap, ProductConstants.CUGL_STATUS_DEL);

			} catch (Exception e) {
				errList.add("取消客户授信出错"+ e.toString());
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if (errList.isEmpty()) {
			//outputMap.put("dw", dw);
			//outputMap.put("content", context.contextMap.get("content"));
			Output.jspSendRedirect(context,"defaultDispatcher?__action=productCredit.queryAllProductCredit");
//			Output.jspOutput(outputMap, context,"/customercredit/custcreditManage.jsp"); 
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
		Map product = null ;
		double last_price=0.0;
		Map grant = null ;
		List<Map> unionGrantPlan = null;
		if (errList.isEmpty()) {		
			try {
				unionGrantPlan = (List<Map>) DataAccessor.query("productCredit.queryUnionGrantPlan", context.contextMap, DataAccessor.RS_TYPE.LIST);
				product = (Map) DataAccessor.query("productCredit.queryAllproductCredit", context.contextMap, DataAccessor.RS_TYPE.MAP);
				grant =  (Map)DataAccessor.query("productCredit.queryAllGrantplanById", context.contextMap,DataAccessor.RS_TYPE.MAP);
				dw = (DataWrap) DataAccessor.query("productCredit.queryLog", context.contextMap,DataAccessor.RS_TYPE.PAGED);		
				outputMap.put("custInfor", DictionaryUtil.getDictionary("承租人级别")); 
				int PRODUCT_ID=Integer.parseInt(context.contextMap.get("PRODUCT_ID").toString());
//-------------Marked by Michael 2011 12/14 供应商授信余额分为连保、回购、回购含灭失，计算逻辑都改变----------	
//------------------------------------------------------------------------------------------------
//				if(SelectReportInfo.selectApplyLastPrice(PRODUCT_ID)==null){
//					last_price=0.0;
//				}else{
//					last_price=Double.parseDouble(SelectReportInfo.selectApplyLastPrice(PRODUCT_ID).toString());
//					   	
//				}
//				 if(grant!=null){
//			    	 grant.put("LAST_PRICE",last_price);
//			    }  
//---------------------------------------------------------------------------------------------
			    
			} catch (Exception e) {
				errList.add("取消客户授信出错"+ e.toString());
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("unionGrantPlan", unionGrantPlan);
			outputMap.put("product", product);
			outputMap.put("grantplan", grant);
			outputMap.put("dw", dw);
			outputMap.put("content", context.contextMap.get("content"));
//			Output.jspSendRedirect(context,"defaultDispatcher?__action=custCredit.queryAllCustCredit");
			Output.jspOutput(outputMap, context,"/productcredit/showlog.jsp"); 
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
		DataAccessor.execute("productCredit.plus-credit-lastprice", paramMap, DataAccessor.OPERATION_TYPE.UPDATE);
		// 添加授信日志
		paramMap.put("CUGL_STATUS", ProductConstants.CUGL_STATUS_OCCUPY);
		DataAccessor.execute("productCredit.create-customer-creditlog", paramMap, DataAccessor.OPERATION_TYPE.INSERT);
		
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
		DataAccessor.execute("productCredit.sub-credit-lastprice", paramMap, DataAccessor.OPERATION_TYPE.UPDATE);
		// 添加授信日志
		paramMap.put("CUGL_STATUS", ProductConstants.CUGL_STATUS_FREE);
		DataAccessor.execute("productCredit.create-customer-creditlog", paramMap, DataAccessor.OPERATION_TYPE.INSERT);
	
	}
	
	public static String createDemo(Map paramMap,Integer ProductConstants) throws Exception {
		String memo = "";
		Map result = (Map)DataAccessor.query("productCredit.queryAllGrantplanById", paramMap, DataAccessor.RS_TYPE.MAP);
		paramMap.put("PDGP_ID", result.get("PDGP_ID"));
		if(ProductConstants == 5)
		{
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
//			String newGRANT_PRICE = paramMap.get("GRANT_PRICE").toString();
//			String oldGRANT_PRICE= result.get("GRANT_PRICE").toString();
//			
//			if(new BigDecimal(newGRANT_PRICE).compareTo(new BigDecimal(oldGRANT_PRICE))==0)
//			{
//				memo += "授信金额未修改！" ;
//			}
//			else
//			{
//				memo += "授信金额从"+oldGRANT_PRICE+"修改为"+newGRANT_PRICE+"！" ;
//			}
			
//			String newLAST_PRICE = (String)paramMap.get("LAST_PRICE");
//			String oldLAST_PRICE= (String)result.get("LAST_PRICE").toString();
//			if(newLAST_PRICE.equals(oldLAST_PRICE))
//			{
//				memo += "授信余额未修改！" ;
//			}
//			else
//			{
//				memo += "授信余额从"+oldLAST_PRICE+"修改为"+newLAST_PRICE+"！" ;
//			}
//----------Add By michael 2011 12/14  增加连保、回购、回购含灭失额度的检核------------------------------------------
			/*
			 * 连保额度
			 */
			String newLIEN_GRANT_PRICE=(String)paramMap.get("LIEN_GRANT_PRICE");
			String oldLIEN_GRANT_PRICE= (String)result.get("LIEN_GRANT_PRICE").toString();
			if(new BigDecimal(newLIEN_GRANT_PRICE).compareTo(new BigDecimal(oldLIEN_GRANT_PRICE))==0)
			{
				memo += "连保授信金额未修改！" ;
			}
			else
			{
				memo += "连保授信金额从"+oldLIEN_GRANT_PRICE+"修改为"+newLIEN_GRANT_PRICE+"！" ;
			}
			/*
			 * 回购额度
			 */
			String newREPURCH_GRANT_PRICE=(String)paramMap.get("REPURCH_GRANT_PRICE");
			String oldREPURCH_GRANT_PRICE= (String)result.get("REPURCH_GRANT_PRICE").toString();
			if(new BigDecimal(newREPURCH_GRANT_PRICE).compareTo(new BigDecimal(oldREPURCH_GRANT_PRICE))==0)
			{
				memo += "回购授信金额未修改！" ;
			}
			else
			{
				memo += "回购授信金额从"+oldREPURCH_GRANT_PRICE+"修改为"+newREPURCH_GRANT_PRICE+"！" ;
			}
			/*
			 * 交机前拨款
			 */
			String newADVANCEMACHINE_GRANT_PRICE=(String)paramMap.get("ADVANCEMACHINE_GRANT_PRICE");
			String oldADVANCEMACHINE_GRANT_PRICE= (String)result.get("ADVANCEMACHINE_GRANT_PRICE").toString();
			if(new BigDecimal(oldADVANCEMACHINE_GRANT_PRICE).compareTo(new BigDecimal(newADVANCEMACHINE_GRANT_PRICE))==0)
			{
				memo += "交机前拨款授信金额未修改！" ;
			}
			else
			{
				memo += "交机前拨款授信金额从"+oldREPURCH_GRANT_PRICE+"修改为"+newREPURCH_GRANT_PRICE+"！" ;
			}
			
			/*
			 * 连保款循环授信
			 */
			String newLIEN_REPEAT_CREDIT=(String)paramMap.get("LIEN_REPEAT_CREDIT");
			String oldLIEN_REPEAT_CREDIT= (String)result.get("LIEN_REPEAT_CREDIT").toString();
			if(newLIEN_REPEAT_CREDIT.equals(oldLIEN_REPEAT_CREDIT))
			{
				memo += "连保循环授信未修改！" ;
			}
			else
			{
				memo += "连保循环授信从"+((oldLIEN_REPEAT_CREDIT.equals("0"))?"否":"是")+"修改为"+((newLIEN_REPEAT_CREDIT.equals("0"))?"否":"是")+"！" ;
			}
			/*
			 * 回购循环授信
			 */
			String newREPURCH_REPEAT_CREDIT=(String)paramMap.get("REPURCH_REPEAT_CREDIT");
			String oldREPURCH_REPEAT_CREDIT= (String)result.get("REPURCH_REPEAT_CREDIT").toString();	
			if(newREPURCH_REPEAT_CREDIT.equals(oldREPURCH_REPEAT_CREDIT))
			{
				memo += "回购循环授信未修改！" ;
			}
			else
			{
				memo += "回购循环授信从"+((oldREPURCH_REPEAT_CREDIT.equals("0"))?"否":"是")+"修改为"+((newREPURCH_REPEAT_CREDIT.equals("0"))?"否":"是")+"！" ;
			}
			/*
			 * 交机前循环授信
			 */
			String newADVANCE_MACHINEREPEAT_CREDIT=(String)paramMap.get("ADVANCE_MACHINEREPEAT_CREDIT");
			String oldADVANCE_MACHINEREPEAT_CREDIT= (String)result.get("ADVANCE_MACHINEREPEAT_CREDIT").toString();	
			if(newADVANCE_MACHINEREPEAT_CREDIT.equals(oldADVANCE_MACHINEREPEAT_CREDIT))
			{
				memo += "交机前循环授信未修改！" ;
			}
			else
			{
				memo += "交机前循环授信从"+((oldADVANCE_MACHINEREPEAT_CREDIT.equals("0"))?"否":"是")+"修改为"+((newADVANCE_MACHINEREPEAT_CREDIT.equals("0"))?"否":"是")+"！" ;
			}
			
//			/*
//			 * 回购含灭失额度
//			 */
//			String newREPURCHLOSS_GRANT_PRICE=(String)paramMap.get("REPURCHLOSS_GRANT_PRICE");
//			String oldREPURCHLOSS_GRANT_PRICE= (String)result.get("REPURCHLOSS_GRANT_PRICE").toString();
//			if(newREPURCHLOSS_GRANT_PRICE.equals(oldREPURCHLOSS_GRANT_PRICE))
//			{
//				memo += "回购含灭失金额未修改！" ;
//			}
//			else
//			{
//				memo += "回购含灭失金额从"+oldREPURCHLOSS_GRANT_PRICE+"修改为"+newREPURCHLOSS_GRANT_PRICE+"！" ;
//			}
			/*
			 * 连保余额额度
			 */
//			String newLIEN_LAST_PRICE=(String)paramMap.get("LIEN_LAST_PRICE");
//			String oldLIEN_LAST_PRICE= (String)result.get("LIEN_LAST_PRICE").toString();
//			if(newLIEN_LAST_PRICE.equals(oldLIEN_LAST_PRICE))
//			{
//				memo += "连保余额未修改！" ;
//			}
//			else
//			{
//				memo += "连保余额从"+oldLIEN_LAST_PRICE+"修改为"+newLIEN_LAST_PRICE+"！" ;
//			}
//			/*
//			 * 回购余额额度
//			 */
//			String newREPURCH_LAST_PRICE=(String)paramMap.get("REPURCH_LAST_PRICE");
//			String oldREPURCH_LAST_PRICE= (String)result.get("REPURCH_LAST_PRICE").toString();
//			if(newREPURCH_LAST_PRICE.equals(oldREPURCH_LAST_PRICE))
//			{
//				memo += "回购余额未修改！" ;
//			}
//			else
//			{
//				memo += "回购余额从"+oldREPURCH_LAST_PRICE+"修改为"+newREPURCH_LAST_PRICE+"！" ;
//			}
//			/*
//			 * 回购含灭失余额额度
//			 */
//			String newREPURCHLOSS_LAST_PRICE=(String)paramMap.get("REPURCHLOSS_LAST_PRICE");
//			String oldREPURCHLOSS_LAST_PRICE= (String)result.get("REPURCHLOSS_LAST_PRICE").toString();
//			if(newREPURCHLOSS_LAST_PRICE.equals(oldREPURCHLOSS_LAST_PRICE))
//			{
//				memo += "回购含灭失余额未修改！" ;
//			}
//			else
//			{
//				memo += "回购含灭失余额从"+oldREPURCHLOSS_LAST_PRICE+"修改为"+newREPURCHLOSS_LAST_PRICE+"！" ;
//			}
//-------------------------------------------------------------------------------------------------------------------			
			
			//String newSTART_DATE = (String)paramMap.get("START_DATE");
			//String oldSTART_DATE= (String)result.get("START_DATE").toString();
			/*if(newSTART_DATE.equals(oldSTART_DATE))
			{
				memo += "起始日期未修改！" ;
			}
			else
			{
				memo += "起始日期从"+oldSTART_DATE+"修改为"+newSTART_DATE+"！" ;
			}*/
			
			//String newEND_DATE = (String)paramMap.get("END_DATE");
			//String oldEND_DATE= (String)result.get("END_DATE").toString();
			/*if(newCUGP_CODE.equals(oldCUGP_CODE))
			{
				memo += "结束日期未修改！" ;
			}
			else
			{
				memo += "结束日期从"+oldEND_DATE+"修改为"+newEND_DATE+"！" ;
			}*/
			
		}
		else if(ProductConstants == 4)
		{
			memo += "删除记录";
		}
		else if(ProductConstants == 3)
		{
			memo += "取消授信";
		}
		else if(ProductConstants == 0)
		{
			memo += "添加授信";
		}
		return memo ;
	}
	/**
	 * 进入联合授信修改页面
	 * @param context
	 */
	public void getUnionByID(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		Map unionMap = null;
		if (errList.isEmpty()) {
			try {
				unionMap = (Map) DataAccessor.query("productCredit.getUnionByID", context.contextMap, DataAccessor.RS_TYPE.MAP);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} 
		}
		if (errList.isEmpty()) {
			outputMap.put("unionMap", unionMap);
			Output.jsonOutput(outputMap, context);
		} else {

		}
	}
	
	/**
	 * 进入联合授信修改页面
	 * @param context
	 */
	public void updateUnion(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		Map unionMap =null;
		if (errList.isEmpty()) {
			try {
				DataAccessor.getSession().startTransaction();
				DataAccessor.getSession().update("productCredit.updateUnionPriceByID01",context.contextMap);
				context.contextMap.put("PURP_ID", context.contextMap.get("UNION_ID"));
				unionMap = (Map) DataAccessor.getSession().queryForObject("productCredit.getUnionByID",context.contextMap);
				context.contextMap.put("UNION_SUPPLIER_ID", unionMap.get("UNION_SUPPLIER_ID"));
				context.contextMap.put("PRODUCT_ID", unionMap.get("PRODUCT_ID"));
				DataAccessor.getSession().update("productCredit.updateUnionPriceByID02",context.contextMap);
				DataAccessor.getSession().commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} finally{
				try {
					DataAccessor.getSession().endTransaction();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
			}
		}
		if (errList.isEmpty()){
			int id = DataUtil.intUtil(unionMap.get("PRODUCT_ID"));
			Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=productCredit.getProductCreditJsp&PRODUCT_ID="+id);
		}
	}
	
	/**
	 * 进入联合授信修改页面
	 * @param context
	 */
	public void delUnionPrice(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		Map unionMap =null;
		if (errList.isEmpty()) {
			try {
				DataAccessor.getSession().startTransaction();		
				context.contextMap.put("PURP_ID", context.contextMap.get("DEL_UNION_ID"));
				unionMap = (Map) DataAccessor.getSession().queryForObject("productCredit.getUnionByID",context.contextMap);
				context.contextMap.put("UNION_SUPPLIER_ID", unionMap.get("UNION_SUPPLIER_ID"));
				context.contextMap.put("PRODUCT_ID", unionMap.get("PRODUCT_ID"));
				
				DataAccessor.getSession().update("productCredit.delUnionPriceByID01",context.contextMap);
				
				DataAccessor.getSession().update("productCredit.delUnionPriceByID02",context.contextMap);
				DataAccessor.getSession().commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();	
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} finally{
				try {
					DataAccessor.getSession().endTransaction();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
			}
		}
		if (errList.isEmpty()){
			int id = DataUtil.intUtil(unionMap.get("PRODUCT_ID"));
			Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=productCredit.getProductCreditJsp&PRODUCT_ID="+id);
		}
	}


	/*
	 * Add by Michael 2012 08-14 
	 * 开放出交机前拨款额度添加，权限给审查经理，额度100W以下
	 */
	public void queryAllProductCreditForAuditMananger(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;
		DataWrap dw = null;		
		
		if (errList.isEmpty()) {		
			try {				
				dw = (DataWrap) DataAccessor.query("productCredit.queryAllproductCredit", context.contextMap, DataAccessor.RS_TYPE.PAGED);

			} catch (Exception e) {
				errList.add("com.brick.product.service.queryAllProductCredit"+ e.toString());
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
				Output.jspOutput(outputMap, context,"/productcredit/productcreditManageByAuditManager.jsp"); 
			}else if("cs".equals(HTMLUtil.getStrParam(context.request, "res", "") ))
			{
				Output.jspOutput(outputMap, context,"/productcredit/productShow.jsp"); 
			}else
			{
				Output.jspOutput(outputMap, context,"/productcredit/productcreditManageByAuditManager.jsp"); 
			}
			
		} else {
			// 跳转到错误页面
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
}
