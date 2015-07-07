package com.brick.statement.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.apache.log4j.Logger;

import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.DataUtil;
import com.brick.util.poi.ExcelPOI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;


public class StatementService extends AService {
	Log logger = LogFactory.getLog(StatementService.class);

	public static final Logger log = Logger.getLogger(StatementService.class);

	@Override
	protected void afterExecute(String action, Context context) {
		// TODO Auto-generated method stub
		super.afterExecute(action, context);
	}

	@Override
	protected boolean preExecute(String action, Context context) {
		// TODO Auto-generated method stub
		return super.preExecute(action, context);
	}

	/**
	 * 查询已经分解了的承租人信息
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryDecomposeCustInfo(Context context) {
		Map outputMap = new HashMap();
		List errorList = new ArrayList();
		errorList = context.errList;
		DataWrap custListpage = null;
//		String search_content=null;
//		if(context.contextMap.get("search_content")==null){
//			search_content="";
//		}else{
//			search_content=context.contextMap.get("search_content").toString().trim();
//		}
//		context.contextMap.put("search_content", search_content);
		if (errorList.isEmpty()) {
			try {
				//引用支付表管理的sql语句
				custListpage = (DataWrap) DataAccessor.query(
						"collectionManage.queryPaylist", context.contextMap,
						DataAccessor.RS_TYPE.PAGED);
//				custListpage = (DataWrap) DataAccessor.query(
//						"statement.queryDecomposeCustInfo", context.contextMap,
//						DataAccessor.RS_TYPE.PAGED);
			} catch (Exception e) {
				log
						.error("com.brick.statement.service.StatementService.queryDecomposeCustInfo"
								+ e.getMessage());
				e.printStackTrace();
				errorList
						.add("com.brick.statement.service.StatementService.queryDecomposeCustInfo"
								+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errorList.add(e);
			}
		}
		outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
		outputMap.put("dw", custListpage);
		Output.jspOutput(outputMap, context,
				"/statement/showStatementCustInfo.jsp");
	}
	
	/**
	 * 根据承租人编号查询对账单信息
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryCustStatementInfo(Context context) {
		Map outputMap = new HashMap();
		List errorList = new ArrayList();
		errorList = context.errList;
		List paymentList = new ArrayList();
		List incomeList = new ArrayList();
		if (errorList.isEmpty()) {
			try {
				if(context.contextMap.get("recp_id") == null) {//逾期催收通过cust_code查询
					paymentList = (List) DataAccessor.query(
							"statement.queryCustPaymentInfo", context.contextMap,
							DataAccessor.RS_TYPE.LIST);
					incomeList = (List) DataAccessor.query(
							"statement.queryCustIncomeInfo", context.contextMap,
							DataAccessor.RS_TYPE.LIST);
				} else {//资金对账通过支付表id查询
					context.contextMap.put("daifenjielaikuan", "待分解来款") ;
					paymentList = (List) DataAccessor.query(
							"statement.queryCustPaymentInfoByRecpId", context.contextMap,
							DataAccessor.RS_TYPE.LIST);
					incomeList = (List) DataAccessor.query(
							"statement.queryCustIncomeInfoByRecpId", context.contextMap,
							DataAccessor.RS_TYPE.LIST);
				}
			} catch (Exception e) {
				log
						.error("com.brick.statement.service.StatementService.queryCustStatementInfo"
								+ e.getMessage());
				e.printStackTrace();
				errorList
						.add("com.brick.statement.service.StatementService.queryCustStatementInfo"
								+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errorList.add(e);
			}
		}
		outputMap.put("cust_name", context.contextMap.get("cust_name"));
		outputMap.put("cust_code", context.contextMap.get("cust_code"));
		outputMap.put("paymentList", paymentList);
		outputMap.put("incomeList", incomeList);
		Output.jspOutput(outputMap, context,
				"/statement/showCustStatementInfo.jsp");
	}

	/**
	 * Add by Michael 2012 4-24
	 * 根据支付表号查询对账单信息
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryRecpCodeStatementInfo(Context context) {
		Map outputMap = new HashMap();
		List errorList = new ArrayList();
		errorList = context.errList;
		List paymentList = new ArrayList();
		List incomeList = new ArrayList();
		if (errorList.isEmpty()) {
			try {

				context.contextMap.put("daifenjielaikuan", "待分解来款") ;
				paymentList = (List) DataAccessor.query(
						"statement.queryCustPaymentInfoByRecpId", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
				
			} catch (Exception e) {
				log
						.error("com.brick.statement.service.StatementService.queryRecpCodeStatementInfo"
								+ e.getMessage());
				e.printStackTrace();
				errorList
						.add("com.brick.statement.service.StatementService.queryRecpCodeStatementInfo"
								+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errorList.add(e);
			}
		}
		outputMap.put("cust_name", context.contextMap.get("cust_name"));
		outputMap.put("RECP_CODE", context.contextMap.get("RECP_CODE"));
		outputMap.put("paymentList", paymentList);

		Output.jspOutput(outputMap, context,
				"/statement/showRecpCodeStatementInfo.jsp");
	}
	
	/**
	 * Add by Michael 2012 4-24
	 * 根据支付表号查询对账单信息
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryCustomerStatementInfo(Context context) {
		Map outputMap = new HashMap();
		List errorList = new ArrayList();
		errorList = context.errList;
		List paymentList = new ArrayList();
		List incomeList = new ArrayList();
		if (errorList.isEmpty()) {
			try {

				context.contextMap.put("daifenjielaikuan", "待分解来款") ;
				paymentList = (List) DataAccessor.query(
						"statement.queryCustomerStatementInfo", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
				
			} catch (Exception e) {
				log
						.error("com.brick.statement.service.StatementService.queryRecpCodeStatementInfo"
								+ e.getMessage());
				e.printStackTrace();
				errorList
						.add("com.brick.statement.service.StatementService.queryRecpCodeStatementInfo"
								+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errorList.add(e);
			}
		}
		outputMap.put("cust_name", context.contextMap.get("cust_name"));
		outputMap.put("cust_code", context.contextMap.get("cust_code"));
		outputMap.put("paymentList", paymentList);

		Output.jspOutput(outputMap, context,
				"/statement/showCustomerStatementInfo.jsp");
	}

	
	/**
	 * 根据承租人编号查询来款信息
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryCustIncomeInfo(Context context) {
		Map outputMap = new HashMap();
		List errorList = new ArrayList();
		errorList = context.errList;
		List incomeList = new ArrayList();
		if (errorList.isEmpty()) {
			try {
				
				incomeList = (List) DataAccessor.query(
						"statement.queryCustIncomeInfo", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
				
			} catch (Exception e) {
				log
						.error("com.brick.statement.service.StatementService.queryCustIncomeInfo"
								+ e.getMessage());
				e.printStackTrace();
				errorList
						.add("com.brick.statement.service.StatementService.queryCustIncomeInfo"
								+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errorList.add(e);
			}
		}
		outputMap.put("cust_code", context.contextMap.get("cust_code"));

		outputMap.put("incomeList", incomeList);
		Output.jspOutput(outputMap, context,
				"/statement/showCustomerIncomeInfo.jsp");
	}
	
	public void expCustStatementPDF(Context context){
		CustStatementPDF.expPDF(context) ;
	}
	
	public void exportExcel(Context context) {

		List paymentList = new ArrayList();
		List incomeList = new ArrayList();

		try {
			paymentList = (List) DataAccessor.query(
					"statement.queryCustPaymentInfo", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			incomeList = (List) DataAccessor.query(
					"statement.queryCustIncomeInfo", context.contextMap,
					DataAccessor.RS_TYPE.LIST);

			ExcelPOI excel=new ExcelPOI();

			context.response.setContentType("application/vnd.ms-excel;charset=GB2312");
			context.response.setHeader("Content-Disposition","attachment;filename="+ new String(("还款明细("+context.contextMap.get("cust_name")+").xls").getBytes("GBK"),"ISO-8859-1"));
			ServletOutputStream out=context.response.getOutputStream();

			context.contextMap.put("sheetName","还款明细");
			context.contextMap.put("CUST_NAME",context.contextMap.get("cust_name"));
			
			excel.generateCaseReport(paymentList,incomeList,context).write(out);
			
			out.flush();
			out.close();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public void queryCustomerBillForSales(Context context) {
		Map outputMap = new HashMap();
		List errorList = new ArrayList();
		errorList = context.errList;
		List paymentList = new ArrayList();
		List incomeList = new ArrayList();
		if (errorList.isEmpty()) {
			try {

				context.contextMap.put("zujin", "租金") ;
				context.contextMap.put("daifenjielaikuan", "待分解来款") ;
				context.contextMap.put("zujinfaxi", "租金罚息") ;
				paymentList = (List) DataAccessor.query(
						"decompose.getCustBillForSales", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
				
			} catch (Exception e) {
				log
						.error("com.brick.statement.service.StatementService.queryCustomerBillForSales"
								+ e.getMessage());
				e.printStackTrace();
				errorList
						.add("com.brick.statement.service.StatementService.queryCustomerBillForSales"
								+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errorList.add(e);
			}
		}
		outputMap.put("cust_name", context.contextMap.get("cust_name"));
		outputMap.put("CUST_ID",context.contextMap.get("CUST_ID"));
		outputMap.put("cust_code", context.contextMap.get("cust_code"));
		outputMap.put("paymentList", paymentList);

		Output.jspOutput(outputMap, context,
				"/statement/showCustomerIncomeInfo.jsp");
	}
	
	//For Birt  导出
	public static Map exportCustomerBillForSales(String cust_id) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map context=new HashMap();
		List paymentList = new ArrayList();
		Map custInfoMap=new HashMap();
		Double total_income_price=0.0;
		Double total_should_price=0.0;
		try {
			context.put("CUST_ID", cust_id) ;
			context.put("zujin", "租金") ;
			context.put("daifenjielaikuan", "待分解来款") ;
			context.put("zujinfaxi", "租金罚息") ;
			custInfoMap=(Map)DataAccessor.query(
					"statement.queryCustomerInfoByCustID", context,
					DataAccessor.RS_TYPE.MAP);
					
			paymentList = (List) DataAccessor.query(
					"decompose.getCustBillForSales", context,
					DataAccessor.RS_TYPE.LIST);
			if (paymentList != null) {
				for (int i = 0; i < paymentList.size(); i++) {
					total_should_price+=DataUtil.doubleUtil(((Map) paymentList.get(i)).get(
							"SHOULD_PRICE").toString());
					total_income_price+=DataUtil.doubleUtil(((Map) paymentList.get(i)).get(
							"INCOME_MONEY").toString());
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		resultMap.put("paymentList", paymentList);
		resultMap.put("cust_name", custInfoMap.get("CUST_NAME"));
		resultMap.put("total_should_price", total_should_price);
		resultMap.put("total_income_price", total_income_price);
		return resultMap;
	}

	@SuppressWarnings("unchecked")
	public void queryCustomerBillForFinance(Context context) {
		Map outputMap = new HashMap();
		List errorList = new ArrayList();
		errorList = context.errList;
		List paymentList = new ArrayList();
		List incomeList = new ArrayList();
		if (errorList.isEmpty()) {
			try {

				context.contextMap.put("zujin", "租金") ;
				context.contextMap.put("daifenjielaikuan", "待分解来款") ;
				context.contextMap.put("zujinfaxi", "租金罚息") ;
				paymentList = (List) DataAccessor.query(
						"decompose.getCustBillForFinance", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
				
			} catch (Exception e) {
				log
						.error("com.brick.statement.service.StatementService.queryCustomerBillForFinance"
								+ e.getMessage());
				e.printStackTrace();
				errorList
						.add("com.brick.statement.service.StatementService.queryCustomerBillForFinance"
								+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errorList.add(e);
			}
		}
		outputMap.put("cust_name", context.contextMap.get("cust_name"));
		outputMap.put("CUST_ID",context.contextMap.get("CUST_ID"));
		outputMap.put("cust_code", context.contextMap.get("cust_code"));
		outputMap.put("paymentList", paymentList);

		Output.jspOutput(outputMap, context,
				"/statement/showCustomerBillForFinance.jsp");
	}
	
	public static Map exportCustomerBillForFinance(String cust_id) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map context=new HashMap();
		List paymentList = new ArrayList();
		Map custInfoMap=new HashMap();
		Double total_income_price=0.0;
		Double total_should_price=0.0;
		try {
			context.put("CUST_ID", cust_id) ;
			context.put("zujin", "租金") ;
			context.put("daifenjielaikuan", "待分解来款") ;
			context.put("zujinfaxi", "租金罚息") ;
			custInfoMap=(Map)DataAccessor.query(
					"statement.queryCustomerInfoByCustID", context,
					DataAccessor.RS_TYPE.MAP);
					
			paymentList = (List) DataAccessor.query(
					"decompose.getCustBillForFinance", context,
					DataAccessor.RS_TYPE.LIST);
			if (paymentList != null) {
				for (int i = 0; i < paymentList.size(); i++) {
					total_should_price+=DataUtil.doubleUtil(((Map) paymentList.get(i)).get(
							"SHOULD_PRICE").toString());
					total_income_price+=DataUtil.doubleUtil(((Map) paymentList.get(i)).get(
							"INCOME_MONEY").toString());
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		resultMap.put("paymentList", paymentList);
		resultMap.put("cust_name", custInfoMap.get("CUST_NAME"));
		resultMap.put("total_should_price", total_should_price);
		resultMap.put("total_income_price", total_income_price);
		return resultMap;
	}
	@SuppressWarnings("unchecked")
	public void queryCustomerBillForSalesForNew(Context context) {
		Map outputMap = new HashMap();
		List errorList = new ArrayList();
		errorList = context.errList;
		List<Map> paymentList = new ArrayList();
		if (errorList.isEmpty()) {
			try {
				paymentList = (List<Map>) DataAccessor.query("decompose.getCustBillForSalesForNew", context.contextMap,DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				log.error("com.brick.statement.service.StatementService.queryCustomerBillForSales"+e.getMessage());
				e.printStackTrace();
				errorList.add("com.brick.statement.service.StatementService.queryCustomerBillForSales"+e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errorList.add(e);
			}
		}
		outputMap.put("cust_name", context.contextMap.get("cust_name"));
		outputMap.put("CUST_ID",context.contextMap.get("CUST_ID"));
		outputMap.put("cust_code", context.contextMap.get("cust_code"));
		outputMap.put("paymentList", paymentList);
		Output.jspOutput(outputMap, context,"/statement/showCustomerIncomeInfoNew.jsp");
	}

}
