package com.brick.credit.vip.service;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.collection.service.StartPayService;
import com.brick.collection.support.PayRate;
import com.brick.collection.util.PaylistUtil;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DataUtil;
import com.brick.util.InterestMarginUtil;
import com.brick.util.NumberUtils;
import com.brick.util.web.HTMLUtil;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.brick.log.service.LogPrint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;
/**
 *  
 * @author wujw
 * @date Jul 6, 2010
 * @version 
 */
public class CreditPaylistService extends AService {

	Log logger = LogFactory.getLog(CreditPaylistService.class);
	
	/**
	 * show credit paylist
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void showCreditPaylist (Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		Map creditMap = null;
		Map schema = null;
		Map paylist = null;
		Map memoMap = null;
		
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {				
				// credit_id 
				context.contextMap.put("data_type", "客户来源");
				creditMap = (Map) DataAccessor.query("creditReportManage.selectCreditBaseInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("creditMap", creditMap);
				// 
				memoMap = (Map) DataAccessor.query("creditReportManage.selectNewMemo", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("memoMap", memoMap);
				//查询方案
				schema = (Map) DataAccessor.query("creditReportManage.selectCreditScheme",context.contextMap, DataAccessor.RS_TYPE.MAP);
				// 查询应付租金列表
				List<Map> irrMonthPaylines = StartPayService.queryPackagePayline(context.contextMap.get("credit_id"), Integer.valueOf(1));
				//Add by Michael 2012 1/5 For 方案的查询
				outputMap.put("irrMonthPaylines", irrMonthPaylines);
				
				// 解压irrMonthPaylines到每一期的钱
				List<Map> rePaylineList = StartPayService.upPackagePaylines(irrMonthPaylines);
				// 
				if(schema!=null&&"4".equals(schema.get("TAX_PLAN_CODE"))) {
					schema.put("payList",rePaylineList);
					schema.put("PLEDGE_AVE_PRICE",schema.get("PLEDGE_AVE_PRICE")==null||"".equals(schema.get("PLEDGE_AVE_PRICE"))?0:schema.get("PLEDGE_AVE_PRICE"));
					schema.put("PLEDGE_BACK_PRICE",schema.get("PLEDGE_BACK_PRICE")==null||"".equals(schema.get("PLEDGE_BACK_PRICE"))?"0":schema.get("PLEDGE_BACK_PRICE"));
					schema.put("MAGR_FEE",schema.get("MANAGEMENT_FEE")==null||"".equals(schema.get("MANAGEMENT_FEE"))?0:schema.get("MANAGEMENT_FEE"));
					schema.put("PLEDGE_LAST_PERIOD",schema.get("PLEDGE_LAST_PERIOD")==null||"".equals(schema.get("PLEDGE_LAST_PERIOD"))?0:schema.get("PLEDGE_LAST_PERIOD"));
				}
				List companyList = null;
				companyList = (List) DataAccessor.query(
						"companyManage.queryCompanyAlias", null,
						DataAccessor.RS_TYPE.LIST);
				//System.out.println(creditMap.get("DECP_ID").toString()+"=========");
				outputMap.put("companyList", companyList);
				if (schema != null) {
					//Add by Michael 2012 01/29 在方案里增加合同类型
					schema.put("CONTRACT_TYPE", String.valueOf(creditMap.get("CONTRACT_TYPE")));
					
					//add by Michael 把管理费收入总和传过来，计算营业税收入，会影响TR计算----------------------
					double totalFeeSet=0.0d;
					
					if("2".equals(schema.get("TAX_PLAN_CODE"))){
						List<Map> listTotalFeeSet=(List) DataAccessor.query("creditReportManage.getTotalFeeByRectID",context.contextMap, DataAccessor.RS_TYPE.LIST);
						for(Map map:listTotalFeeSet){
							totalFeeSet+=new BigDecimal(DataUtil.doubleUtil(map.get("FEE"))/1.06).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
						}	
					}else if("1".equals(schema.get("TAX_PLAN_CODE"))||"3".equals(schema.get("TAX_PLAN_CODE"))||"4".equals(schema.get("TAX_PLAN_CODE"))){
						totalFeeSet=(Double)DataAccessor.query("creditReportManage.sumTotalFeeByRectID",context.contextMap, DataAccessor.RS_TYPE.OBJECT);
					}
					
					schema.put("FEESET_TOTAL", totalFeeSet);
					//-----------------------------------------------------------------------------
					
					schema.put("TOTAL_PRICE", schema.get("LEASE_TOPRIC"));
					schema.put("LEASE_PERIOD", schema.get("LEASE_TERM"));
					schema.put("LEASE_TERM", schema.get("LEASE_COURSE"));
					// 
					if (irrMonthPaylines.size() > 0) {
						// 如果应付租金存在，则以应付租金的方式计算
						paylist = StartPayService.createCreditPaylistIRR(schema,rePaylineList,irrMonthPaylines);
					} else {
						// 如果应付租金不存在，则以年利率(合同利率)的方式计算
						paylist = StartPayService.createCreditPaylist(schema,new ArrayList<Map>());
					}
					paylist.put("PLEDGE_ENTER_MCTOAG", schema.get("PLEDGE_ENTER_MCTOAG"));
					paylist.put("PLEDGE_ENTER_AGRATE", schema.get("PLEDGE_ENTER_AGRATE"));	
					paylist.put("PLEDGE_ENTER_MCTOAGRATE", schema.get("PLEDGE_ENTER_MCTOAGRATE"));	
					
					
				}
				//
				outputMap.put("paylist", paylist);
				//
				//Add by Michael 2012 1/5 For 方案的查询
				outputMap.put("schemeMap", schema);
				//
				
				//Add by Michael 2012 01/14 For 方案费用查询 影响概算成本为1 不影响为0
				List feeListRZE=null;
				feeListRZE = (List) DataAccessor.query("creditReportManage.getCreditFeeListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("feeListRZE", feeListRZE);
				List feeList=null;
				feeList = (List) DataAccessor.query("creditReportManage.getCreditFeeList",context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("feeList", feeList);	
				
				//费用设定明细 影响概算成本为1 不影响为0
				List feeSetListRZE=null;
				feeSetListRZE = (List) DataAccessor.query("creditReportManage.getFeeSetListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("feeSetListRZE", feeSetListRZE);
				List feeSetList=null;
				feeSetList = (List) DataAccessor.query("creditReportManage.getFeeSetList",context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("feeSetList", feeSetList);
				//-------------------------------------------------------------------
				
				Map baseRate = PayRate.getBaseRate();
				logger.error(baseRate.get("SIX_MONTHS").toString()+"====================================================");
				outputMap.put("baseRateJson", Output.serializer.serialize(baseRate));
				
				//
				outputMap.put("payWays", DictionaryUtil.getDictionary("支付方式"));
				outputMap.put("examineFlag", context.contextMap.get("examineFlag"));
				//增加税费方案查询
				outputMap.put("taxPlanList", DataAccessor.query("dataDictionary.queryDataDictionaryByValueAdded", null, DataAccessor.RS_TYPE.LIST));
				
				//Add by Michael 2012 12-20  增加费用来源
				outputMap.put("feeSourceList", DictionaryUtil.getDictionary("费用来源"));
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("报告管理--现场调查报告租金测算错误!请联系管理员");
			}
		
		}
		if (errList.isEmpty()) {
			outputMap.put("showFlag", 5);
			outputMap.put("credit_id", context.contextMap.get("credit_id"));
			outputMap.put("word", context.contextMap.get("word"));
			if (context.contextMap.get("word").equals("up")) {
				Output.jspOutput(outputMap, context, "/credit_vip/creditFrame.jsp");
			}else {
				Output.jspOutput(outputMap, context, "/credit_vip/creditFrameShow.jsp");
			}
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
	
	}
	
	@SuppressWarnings("unchecked")
	public void createCreditPaylist (Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		Map creditMap = null;
		Map schema = null;
		Map paylist = null;
		Map memoMap = null;
		
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {				
				// credit_id 
				context.contextMap.put("data_type", "客户来源");
				creditMap = (Map) DataAccessor.query("creditReportManage.selectCreditBaseInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("creditMap", creditMap);
				// 
				memoMap = (Map) DataAccessor.query("creditReportManage.selectNewMemo", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("memoMap", memoMap);
				//查询方案
				schema = (Map) DataAccessor.query("creditReportManage.selectCreditScheme",context.contextMap, DataAccessor.RS_TYPE.MAP);
				// 查询应付租金列表
				List<Map> irrMonthPaylines = StartPayService.queryPackagePayline(context.contextMap.get("credit_id"), Integer.valueOf(1));
				//Add by Michael 2012 1/5 For 方案的查询
				outputMap.put("irrMonthPaylines", irrMonthPaylines);
				
				// 解压irrMonthPaylines到每一期的钱
				List<Map> rePaylineList = StartPayService.upPackagePaylines(irrMonthPaylines);
				// 
				if(schema!=null&&"4".equals(schema.get("TAX_PLAN_CODE"))) {
					schema.put("payList",rePaylineList);
					schema.put("PLEDGE_AVE_PRICE",schema.get("PLEDGE_AVE_PRICE")==null||"".equals(schema.get("PLEDGE_AVE_PRICE"))?0:schema.get("PLEDGE_AVE_PRICE"));
					schema.put("PLEDGE_BACK_PRICE",schema.get("PLEDGE_BACK_PRICE")==null||"".equals(schema.get("PLEDGE_BACK_PRICE"))?"0":schema.get("PLEDGE_BACK_PRICE"));
					schema.put("MAGR_FEE",schema.get("MANAGEMENT_FEE")==null||"".equals(schema.get("MANAGEMENT_FEE"))?0:schema.get("MANAGEMENT_FEE"));
					schema.put("PLEDGE_LAST_PERIOD",schema.get("PLEDGE_LAST_PERIOD")==null||"".equals(schema.get("PLEDGE_LAST_PERIOD"))?0:schema.get("PLEDGE_LAST_PERIOD"));
				}
				List companyList = null;
				companyList = (List) DataAccessor.query(
						"companyManage.queryCompanyAlias", null,
						DataAccessor.RS_TYPE.LIST);
				//System.out.println(creditMap.get("DECP_ID").toString()+"=========");
				outputMap.put("companyList", companyList);
				if (schema != null) {
					//Add by Michael 2012 01/29 在方案里增加合同类型
					schema.put("CONTRACT_TYPE", String.valueOf(creditMap.get("CONTRACT_TYPE")));
					
					//add by Michael 把管理费收入总和传过来，计算营业税收入，会影响TR计算----------------------
					double totalFeeSet=0.0d;
					
					if("2".equals(schema.get("TAX_PLAN_CODE"))){
						List<Map> listTotalFeeSet=(List) DataAccessor.query("creditReportManage.getTotalFeeByRectID",context.contextMap, DataAccessor.RS_TYPE.LIST);
						for(Map map:listTotalFeeSet){
							totalFeeSet+=new BigDecimal(DataUtil.doubleUtil(map.get("FEE"))/1.06).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
						}	
					}else if("1".equals(schema.get("TAX_PLAN_CODE"))||"3".equals(schema.get("TAX_PLAN_CODE"))||"4".equals(schema.get("TAX_PLAN_CODE"))){
						totalFeeSet=(Double)DataAccessor.query("creditReportManage.sumTotalFeeByRectID",context.contextMap, DataAccessor.RS_TYPE.OBJECT);
					}
					
					schema.put("FEESET_TOTAL", totalFeeSet);
					//-----------------------------------------------------------------------------
					
					schema.put("TOTAL_PRICE", schema.get("LEASE_TOPRIC"));
					schema.put("LEASE_PERIOD", schema.get("LEASE_TERM"));
					schema.put("LEASE_TERM", schema.get("LEASE_COURSE"));
					// 
					if (irrMonthPaylines.size() > 0) {
						// 如果应付租金存在，则以应付租金的方式计算
						paylist = StartPayService.createCreditPaylistIRR(schema,rePaylineList,irrMonthPaylines);
					} else {
						// 如果应付租金不存在，则以年利率(合同利率)的方式计算
						paylist = StartPayService.createCreditPaylist(schema,new ArrayList<Map>());
					}
					paylist.put("PLEDGE_ENTER_MCTOAG", schema.get("PLEDGE_ENTER_MCTOAG"));
					paylist.put("PLEDGE_ENTER_AGRATE", schema.get("PLEDGE_ENTER_AGRATE"));	
					paylist.put("PLEDGE_ENTER_MCTOAGRATE", schema.get("PLEDGE_ENTER_MCTOAGRATE"));	
					
					
				}
				//
				outputMap.put("paylist", paylist);
				//
				//Add by Michael 2012 1/5 For 方案的查询
				outputMap.put("schemeMap", schema);
				//
				
				//Add by Michael 2012 01/14 For 方案费用查询 影响概算成本为1 不影响为0
				List feeListRZE=null;
				feeListRZE = (List) DataAccessor.query("creditReportManage.getCreditFeeListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("feeListRZE", feeListRZE);
				List feeList=null;
				feeList = (List) DataAccessor.query("creditReportManage.getCreditFeeList",context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("feeList", feeList);	
				
				//费用设定明细 影响概算成本为1 不影响为0
				List feeSetListRZE=null;
				feeSetListRZE = (List) DataAccessor.query("creditReportManage.getFeeSetListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("feeSetListRZE", feeSetListRZE);
				List feeSetList=null;
				feeSetList = (List) DataAccessor.query("creditReportManage.getFeeSetList",context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("feeSetList", feeSetList);
				//-------------------------------------------------------------------
				
				Map baseRate = PayRate.getBaseRate();
				logger.error(baseRate.get("SIX_MONTHS").toString()+"====================================================");
				outputMap.put("baseRateJson", Output.serializer.serialize(baseRate));
				
				//
				outputMap.put("payWays", DictionaryUtil.getDictionary("支付方式"));
				outputMap.put("examineFlag", context.contextMap.get("examineFlag"));
				//增加税费方案查询
				outputMap.put("taxPlanList", DictionaryUtil.getDictionary("税费方案"));
				
				//Add by Michael 2012 12-20  增加费用来源
				outputMap.put("feeSourceList", DictionaryUtil.getDictionary("费用来源"));
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("报告管理--现场调查报告租金测算错误!请联系管理员");
			}
		
		}
		if (errList.isEmpty()) {
			outputMap.put("showFlag", context.contextMap.get("showFlag"));//TODO
			outputMap.put("credit_id", context.contextMap.get("credit_id"));
			outputMap.put("word", context.contextMap.get("word"));
			if (context.contextMap.get("word").equals("up")) {
				Output.jspOutput(outputMap, context, "/credit_vip/creditFrame.jsp");
			}else {
				Output.jspOutput(outputMap, context, "/credit_vip/creditFrameShow.jsp");
			}
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
	
	}

	
	/**
	 * calculateCreditPaylist
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void calculateCreditPaylist (Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		Map creditMap = null;
		Map schema = null;
		Map paylist = null;
		List payWays = null;
		
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {
				// credit_id 
				context.contextMap.put("data_type", "客户来源");
				creditMap = (Map) DataAccessor.query("creditReportManage.selectCreditBaseInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("creditMap", creditMap);
				//
				List companyList = null;
				companyList = (List) DataAccessor.query(
						"companyManage.queryCompanyAlias", null,
						DataAccessor.RS_TYPE.LIST);
				//System.out.println(creditMap.get("DECP_ID").toString()+"=========");
				outputMap.put("companyList", companyList);
				schema = copySchema(schema, context.contextMap);
				
				//Add by Michael 2012 01/29 在方案里增加合同类型
				schema.put("CONTRACT_TYPE", String.valueOf(creditMap.get("CONTRACT_TYPE")));			
				//
				
				//add by Michael 把管理费收入总和传过来，计算营业税收入，会影响TR计算----------------------
				double totalFeeSet=0.0d;
				
				if("2".equals(schema.get("TAX_PLAN_CODE"))){
					List<Map> listTotalFeeSet=(List) DataAccessor.query("creditReportManage.getTotalFeeByRectID",context.contextMap, DataAccessor.RS_TYPE.LIST);
					for(Map map:listTotalFeeSet){
						totalFeeSet+=new BigDecimal(DataUtil.doubleUtil(map.get("FEE"))/1.06).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
					}	
				}else if("1".equals(schema.get("TAX_PLAN_CODE"))||"3".equals(schema.get("TAX_PLAN_CODE"))){
					totalFeeSet=(Double)DataAccessor.query("creditReportManage.sumTotalFeeByRectID",context.contextMap, DataAccessor.RS_TYPE.OBJECT);
				}
				
				schema.put("FEESET_TOTAL", totalFeeSet);
				//-----------------------------------------------------------------------------				
				
				List<Map> rePaylineList = new ArrayList<Map>();
				
				String[] periodNums = HTMLUtil.getParameterValues(context.request, "PERIOD_NUM", "0");
				String[] monthPrices = HTMLUtil.getParameterValues(context.request, "MONTH_PRICE", "0");
				
				for (int i=0;i<(periodNums==null?0:periodNums.length);i++) {
					Map line = new HashMap();
					line.put("PERIOD_NUM", periodNums[i]);
					line.put("MONTH_PRICE", monthPrices[i]);
					rePaylineList.add(line);
				}
				//
				paylist = StartPayService.createCreditPaylist(schema,rePaylineList);
				//
				paylist.put("PLEDGE_ENTER_MCTOAG", context.contextMap.get("PAY_PLEDGE_ENTER_MCTOAG"));
				paylist.put("PLEDGE_ENTER_AGRATE", context.contextMap.get("PAY_PLEDGE_ENTER_AGRATE"));	
				paylist.put("PLEDGE_ENTER_MCTOAGRATE", context.contextMap.get("PAY_PLEDGE_ENTER_MCTOAGRATE"));	
				outputMap.put("paylist", paylist);
				
				//
				Map baseRate = PayRate.getBaseRate();
				outputMap.put("baseRateJson", Output.serializer.serialize(baseRate));
				
				//
				outputMap.put("payWays", DictionaryUtil.getDictionary("支付方式"));
				outputMap.put("examineFlag", context.contextMap.get("examineFlag"));
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("报告管理--现场调查报告-租金测算-融资租赁还款计划错误!请联系管理员");
			}
		
		}
		if (errList.isEmpty()) {
			outputMap.put("showFlag", 5);
			outputMap.put("credit_id", context.contextMap.get("credit_id"));
			outputMap.put("word", context.contextMap.get("word"));
			if (context.contextMap.get("word").equals("up")) {
				Output.jspOutput(outputMap, context, "/credit_vip/creditFrame.jsp");
			}else {
				Output.jspOutput(outputMap, context, "/credit_vip/creditFrameShow.jsp");
			}
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
	
	}
	/**
	 * calculate copy resource
	 * @param dest
	 * @param src
	 * @throws ParseException 
	 */
	@SuppressWarnings("unchecked")
	public static Map copySchema(Map dest, Map src) throws ParseException {
		
		if (dest == null) {
			dest = new HashMap();
		}
		
		dest.put("TOTAL_PRICE", src.get("PAY_LEASE_TOPRIC"));
		dest.put("LEASE_TOPRIC", src.get("PAY_LEASE_TOPRIC"));
		dest.put("FLOAT_RATE", src.get("PAY_FLOAT_RATE"));
		dest.put("LEASE_RZE", src.get("PAY_LEASE_RZE"));
		dest.put("LEASE_PERIOD", src.get("PAY_LEASE_PERIOD"));
		dest.put("LEASE_TERM", src.get("PAY_LEASE_TERM"));
		dest.put("PLEDGE_PRICE_RATE", src.get("PAY_PLEDGE_PRICE_RATE"));
		dest.put("PLEDGE_PRICE", src.get("PAY_PLEDGE_PRICE"));
		dest.put("HEAD_HIRE_PERCENT", src.get("PAY_HEAD_HIRE_PERCENT"));
		dest.put("HEAD_HIRE", src.get("PAY_HEAD_HIRE"));
		dest.put("MANAGEMENT_FEE_RATE", src.get("PAY_MANAGEMENT_FEE_RATE"));
		dest.put("MANAGEMENT_FEE", src.get("PAY_MANAGEMENT_FEE"));
		dest.put("START_DATE", DataUtil.dateUtil(src.get("PAY_START_DATE"), "yyyy-MM-dd"));
		dest.put("YEAR_INTEREST", src.get("PAY_YEAR_INTEREST"));
		dest.put("PAY_WAY", src.get("PAY_PAY_WAY"));
		
		dest.put("PLEDGE_WAY", src.get("PAY_PLEDGE_WAY"));
		// dest.put("PLEDGE_PERIOD", src.get("PAY_PLEDGE_PERIOD"));
		// dest.put("SALES_TAX_RATE", src.get("PAY_SALES_TAX_RATE"));
		// dest.put("INSURE_BASE_RATE", src.get("PAY_INSURE_BASE_RATE"));
		// dest.put("STAMP_TAX_TOPRIC", src.get("PAY_STAMP_TAX_TOPRIC"));
		// dest.put("STAMP_TAX_MONTHPRIC", src.get("PAY_STAMP_TAX_MONTHPRIC"));
		// dest.put("STAMP_TAX_INSUREPRIC", src.get("PAY_STAMP_TAX_INSUREPRIC"));
		dest.put("PLEDGE_AVE_PRICE", src.get("PAY_PLEDGE_AVE_PRICE"));
		dest.put("PLEDGE_BACK_PRICE", src.get("PAY_PLEDGE_BACK_PRICE"));
		dest.put("PLEDGE_LAST_PRICE", src.get("PAY_PLEDGE_LAST_PRICE"));
		dest.put("PLEDGE_LAST_PERIOD", src.get("PAY_PLEDGE_LAST_PERIOD"));
		dest.put("PLEDGE_PERIOD", src.get("PAY_PLEDGE_PERIOD"));
		dest.put("PLEDGE_ENTER_WAY", src.get("PAY_PLEDGE_ENTER_WAY"));
		dest.put("PLEDGE_ENTER_CMPRICE", src.get("PAY_PLEDGE_ENTER_CMPRICE"));
		dest.put("PLEDGE_ENTER_CMRATE", src.get("PAY_PLEDGE_ENTER_CMRATE"));
		dest.put("PLEDGE_ENTER_AG", src.get("PAY_PLEDGE_ENTER_AG"));
		
		//
		dest.put("PLEDGE_REALPRIC", src.get("PAY_PLEDGE_REALPRIC"));
		
		// Add by Michael 2012 1/5
		dest.put("TR_RATE", src.get("TR_RATE"));
		dest.put("TR_IRR_RATE", src.get("TR_IRR_RATE"));
		dest.put("RATE_DIFF", src.get("RATE_DIFF"));
		//Add by Michael 2012 09-21 增加税费方案编号
		dest.put("TAX_PLAN_CODE", src.get("PAY_TAX_PLAN_CODE"));
		
		//Add by Michael 2013 02-01 增加延迟拨款期数
		dest.put("DEFER_PERIOD", src.get("PAY_DEFER_PERIOD"));
		
		return dest;
	}

	/**
	 * 2010-09-01 wjw
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void calculateCreditPaylistIRR (Context context) {

		Map outputMap = new HashMap();
		List errList = context.errList;
		
		Map creditMap = null;
		Map schema = null;
		Map paylist = null;
		
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {
				// credit_id 
				context.contextMap.put("data_type", "客户来源");
				creditMap = (Map) DataAccessor.query("creditReportManage.selectCreditBaseInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("creditMap", creditMap);
				// 
				List companyList = null;
				companyList = (List) DataAccessor.query(
						"companyManage.queryCompanyAlias", null,
						DataAccessor.RS_TYPE.LIST);
				//System.out.println(creditMap.get("DECP_ID").toString()+"=========");
				outputMap.put("companyList", companyList);
				schema = copySchema(schema, context.contextMap);
				//
				List<Map> rePaylineList = StartPayService.upPackagePaylines(context);
				if("4".equals(schema.get("TAX_PLAN_CODE"))) {
					schema.put("payList",rePaylineList);
				}
				
				//
				//Add by Michael 2012 01/29 在方案里增加合同类型
				schema.put("CONTRACT_TYPE", String.valueOf(creditMap.get("CONTRACT_TYPE")));
				
				//add by Michael 把管理费收入总和传过来，计算营业税收入，会影响TR计算----------------------
				//Add by Michael 2012 01/29 增加费用保存
				List feeSetLists =null;
				double totalFeeSet=0.0d;
				Map where=null;
				try {
					feeSetLists = (List) DataAccessor.query(
							"creditReportManage.getFeeSetListAllRZE", context.contextMap,
							DataAccessor.RS_TYPE.LIST);
					for (int i = 0; i < feeSetLists.size(); i++) {
						where = (Map) feeSetLists.get(i);
						
						if("2".equals(schema.get("TAX_PLAN_CODE"))){
							totalFeeSet+=new BigDecimal(DataUtil.doubleUtil(context.contextMap.get(where.get("CREATE_FILED_NAME")))/1.06).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
						}else if("1".equals(schema.get("TAX_PLAN_CODE"))||"3".equals(schema.get("TAX_PLAN_CODE"))||"4".equals(schema.get("TAX_PLAN_CODE"))){
							totalFeeSet += DataUtil.doubleUtil(context.contextMap.get(where.get("CREATE_FILED_NAME")));
						}
					}										
					schema.put("FEESET_TOTAL", totalFeeSet);
				} catch (Exception e) {
					e.printStackTrace();
				}
				//-----------------------------------------------------------------------------
				
				paylist = StartPayService.createCreditPaylistIRR(schema,rePaylineList, StartPayService.keepPackagePayline(context));
				
				paylist.put("PLEDGE_ENTER_MCTOAG", context.contextMap.get("PAY_PLEDGE_ENTER_MCTOAG"));
				paylist.put("PLEDGE_ENTER_AGRATE", context.contextMap.get("PAY_PLEDGE_ENTER_AGRATE"));
				paylist.put("PLEDGE_ENTER_MCTOAGRATE", context.contextMap.get("PAY_PLEDGE_ENTER_MCTOAGRATE"));
				outputMap.put("paylist", paylist);

				//Add by Michael 2012 01/14 For 方案费用查询 影响概算成本为1 不影响为0
			
				List<Map> feeListRZE = (List<Map>) DataAccessor.query("creditReportManage.getCreditFeeListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
				List<Map> feeList = (List<Map>) DataAccessor.query("creditReportManage.getCreditFeeList",context.contextMap, DataAccessor.RS_TYPE.LIST);
				for (Map tempMap : feeListRZE) {
					tempMap.put("FEE", context.contextMap.get(tempMap.get("CREATE_FILED_NAME")));
					tempMap.put("SOURCE_CODE", context.contextMap.get(tempMap.get("CREATE_FILED_NAME")+"_SOURCE"));
				}

				for (Map tempMap : feeList) {
					tempMap.put("FEE", context.contextMap.get(tempMap.get("CREATE_FILED_NAME")));
					tempMap.put("SOURCE_CODE", context.contextMap.get(tempMap.get("CREATE_FILED_NAME")+"_SOURCE"));
				}
				outputMap.put("feeListRZE", feeListRZE);
				outputMap.put("feeList", feeList);	
				
				//费用设定明细 影响概算成本为1 不影响为0
				List<Map> feeSetListRZE = (List<Map>) DataAccessor.query("creditReportManage.getFeeSetListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
				List<Map> feeSetList = (List<Map>) DataAccessor.query("creditReportManage.getFeeSetList",context.contextMap, DataAccessor.RS_TYPE.LIST);
				
				for (Map tempMap : feeSetListRZE) {
					tempMap.put("FEE", context.contextMap.get(tempMap.get("CREATE_FILED_NAME")));
					tempMap.put("SOURCE_CODE", context.contextMap.get(tempMap.get("CREATE_FILED_NAME")+"_SOURCE"));
				}

				for (Map tempMap : feeSetList) {
					tempMap.put("FEE", context.contextMap.get(tempMap.get("CREATE_FILED_NAME")));
					tempMap.put("SOURCE_CODE", context.contextMap.get(tempMap.get("CREATE_FILED_NAME")+"_SOURCE"));
				}
				outputMap.put("feeSetListRZE", feeSetListRZE);
				outputMap.put("feeSetList", feeSetList);
				//-------------------------------------------------------------------					
				
				//
				Map baseRate = PayRate.getBaseRate();
				outputMap.put("baseRateJson", Output.serializer.serialize(baseRate));
				
				//
				outputMap.put("payWays", DictionaryUtil.getDictionary("支付方式"));
				outputMap.put("examineFlag", context.contextMap.get("examineFlag"));
				//Add by Michael 2012 09-21 增加税费测算方案
				outputMap.put("taxPlanList", DictionaryUtil.getDictionary("税费方案"));
				
				//Add by Michael 2012 12-20  增加费用来源
				outputMap.put("feeSourceList", DictionaryUtil.getDictionary("费用来源"));
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("报告管理--现场调查报告-租金测算-根据【应收租金】测算支付表错误!请联系管理员");
			}
		
		}
		if (errList.isEmpty()) {
			outputMap.put("showFlag", 5);
			outputMap.put("credit_id", context.contextMap.get("credit_id"));
			outputMap.put("word", context.contextMap.get("word"));
			if (context.contextMap.get("word").equals("up")) {
				Output.jspOutput(outputMap, context, "/credit_vip/creditFrame.jsp");
			}else {
				Output.jspOutput(outputMap, context, "/credit_vip/creditFrameShow.jsp");
			}
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
	
	}
	
	
	
	@SuppressWarnings("unchecked")
	public void saveSchemaToSession(Context context){
		Map map=new HashMap();
		map.put("PAY_LEASE_TOPRIC", context.contextMap.get("PAY_LEASE_TOPRIC"));
		map.put("PAY_LEASE_RZE", context.contextMap.get("PAY_LEASE_RZE"));
		map.put("base_rate", context.contextMap.get("base_rate"));
		map.put("PAY_YEAR_INTEREST", context.contextMap.get("PAY_YEAR_INTEREST"));
		map.put("PAY_FLOAT_RATE", context.contextMap.get("PAY_FLOAT_RATE"));
		map.put("PAY_LEASE_TERM", context.contextMap.get("PAY_LEASE_TERM"));
		map.put("PAY_LEASE_PERIOD", context.contextMap.get("PAY_LEASE_PERIOD"));
		map.put("PAY_LEASE_PERIOD", context.contextMap.get("PAY_LEASE_PERIOD"));
		map.put("each_price", context.contextMap.get("each_price"));
		map.put("PAY_PAY_WAY", context.contextMap.get("PAY_PAY_WAY"));
		map.put("total_price", context.contextMap.get("total_price"));
		map.put("PAY_MANAGEMENT_FEE", context.contextMap.get("PAY_MANAGEMENT_FEE")); 
		map.put("PAY_PLEDGE_WAY", context.contextMap.get("PAY_PLEDGE_WAY"));
		map.put("PAY_PLEDGE_PRICE", context.contextMap.get("PAY_PLEDGE_PRICE"));
		map.put("TOTAL_SALES_TAX", context.contextMap.get("TOTAL_SALES_TAX"));   
		map.put("STAMP_TAX_PRICE", context.contextMap.get("STAMP_TAX_PRICE"));   
		map.put("first_insure_price", context.contextMap.get("first_insure_price"));   
		map.put("total_insure_price", context.contextMap.get("total_insure_price"));   
		map.put("total_ren_price", context.contextMap.get("total_ren_price"));   
		map.put("cust_tr", context.contextMap.get("cust_tr"));   
		map.put("PAY_MANAGEMENT_FEE_RATE", context.contextMap.get("PAY_MANAGEMENT_FEE_RATE"));   
		map.put("PAY_HEAD_HIRE_PERCENT", context.contextMap.get("PAY_HEAD_HIRE_PERCENT"));   
		map.put("PAY_HEAD_HIRE", context.contextMap.get("PAY_HEAD_HIRE"));   
		map.put("PAY_PLEDGE_PRICE_RATE", context.contextMap.get("PAY_PLEDGE_PRICE_RATE"));   
		map.put("PAY_SALES_TAX_RATE", context.contextMap.get("PAY_SALES_TAX_RATE"));   
		map.put("PAY_INSURE_BASE_RATE", context.contextMap.get("PAY_INSURE_BASE_RATE"));   
		map.put("PAY_START_DATE", context.contextMap.get("PAY_START_DATE"));   
		map.put("PAY_PAY_WAY_VALUE", context.contextMap.get("PAY_PAY_WAY_VALUE"));   
		map.put("PAY_PLEDGE_WAY_VALUE", context.contextMap.get("PAY_PLEDGE_WAY_VALUE")); 
		map.put("PAY_LEASE_TERM_VALUE", context.contextMap.get("PAY_LEASE_TERM_VALUE")); 
		map.put("TR_IRR_RATE", context.contextMap.get("TR_IRR_RATE")); 
		map.put("STAMP_TAX_INSUREPRIC", context.contextMap.get("STAMP_TAX_INSUREPRIC")); 
		map.put("PAY_STAMP_TAX_TOPRIC", context.contextMap.get("PAY_STAMP_TAX_TOPRIC")); 
		map.put("PAY_STAMP_TAX_MONTHPRIC", context.contextMap.get("PAY_STAMP_TAX_MONTHPRIC")); 
		map.put("PAY_PLEDGE_PERIOD", context.contextMap.get("PAY_PLEDGE_PERIOD")); 
		map.put("PAY_PLEDGE_REALPRIC", context.contextMap.get("PAY_PLEDGE_REALPRIC")); 
		// wujw
		map.put("PAY_IRR_MONTH_PRICE", context.contextMap.get("PAY_IRR_MONTH_PRICE")); 
		map.put("PAY_IRR_MONTH_PRICE_START", context.contextMap.get("PAY_IRR_MONTH_PRICE_START")); 
		map.put("PAY_IRR_MONTH_PRICE_END", context.contextMap.get("PAY_IRR_MONTH_PRICE_END")); 
		// wujw 2011-01-14
		map.put("PAY_PLEDGE_AVE_PRICE", context.contextMap.get("PAY_PLEDGE_AVE_PRICE")); 
		map.put("PAY_PLEDGE_BACK_PRICE", context.contextMap.get("PAY_PLEDGE_BACK_PRICE")); 
		map.put("PAY_PLEDGE_LAST_PRICE", context.contextMap.get("PAY_PLEDGE_LAST_PRICE")); 
		map.put("PAY_PLEDGE_LAST_PERIOD", context.contextMap.get("PAY_PLEDGE_LAST_PERIOD")); 
		map.put("PAY_PLEDGE_ENTER_WAY", context.contextMap.get("PAY_PLEDGE_ENTER_WAY")); 
		map.put("PAY_PLEDGE_ENTER_CMPRICE", context.contextMap.get("PAY_PLEDGE_ENTER_CMPRICE")); 
		map.put("PAY_PLEDGE_ENTER_CMRATE", context.contextMap.get("PAY_PLEDGE_ENTER_CMRATE")); 
		map.put("PAY_PLEDGE_ENTER_AG", context.contextMap.get("PAY_PLEDGE_ENTER_AG")); 
		map.put("PLEDGE_ENTER_MCTOAG", context.contextMap.get("PLEDGE_ENTER_MCTOAG")); 
		map.put("PLEDGE_ENTER_MCTOAGRATE", context.contextMap.get("PLEDGE_ENTER_MCTOAGRATE")); 
		map.put("PLEDGE_ENTER_AGRATE", context.contextMap.get("PLEDGE_ENTER_AGRATE")); 
		map.put("FIRST_OWN_PRICE", context.contextMap.get("FIRST_OWN_PRICE")); 
		
		//增加 By Michael 2012 1/5 增加利差
		map.put("RATE_DIFF", context.contextMap.get("RATE_DIFF")); 
		
		//增加税费测算方案
		map.put("TAX_PLAN_CODE", context.contextMap.get("TAX_PLAN_CODE")); 
		map.put("TAX_PLAN_CODE_TEXT", context.contextMap.get("TAX_PLAN_CODE_TEXT"));
		map.put("TOTAL_VALUEADDED_TAX", context.contextMap.get("TOTAL_VALUEADDED_TAX"));
		
		map.put("DEFER_PERIOD", context.contextMap.get("DEFER_PERIOD"));
		
		//Add by Michael 2012 01/29 增加费用保存
		List feeSetList =null;
		List feeList=new ArrayList();
		Map where=null;
		try {
			feeSetList = (List) DataAccessor.query(
					"creditReportManage.getFeeSetListAll", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < feeSetList.size(); i++) {
				Map tempMap = new HashMap();
				where = (Map) feeSetList.get(i);
				tempMap.put("FEE_SET_ID", where.get("ID"));
				tempMap.put("CREATE_SHOW_NAME", where.get("CREATE_SHOW_NAME"));
				tempMap.put("IS_LEASERZE_COST", where.get("IS_LEASERZE_COST"));
				tempMap.put("CREATE_FILED_NAME", where.get("CREATE_FILED_NAME")) ;
				tempMap.put("CREATE_ID", context.contextMap.get("s_employeeId"));
				tempMap.put("CREDIT_ID", context.contextMap.get("credit_id"));
				tempMap.put("FEE", DataUtil.doubleUtil(context.contextMap.get(where.get("CREATE_FILED_NAME"))));
				
				//Add by Michael 2012 12-21 增加费用来源栏位
				tempMap.put("SOURCE_CODE",  context.contextMap.get(where.get("CREATE_FILED_NAME")+"_SOURCE"));
				
				feeList.add(tempMap);
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		//----------------------------------------------------------------		
		
		List schemasList=null;
		if(context.request.getSession().getAttribute("schemasList")==null){
			schemasList=new ArrayList();
		}else{
			schemasList=(List)context.request.getSession().getAttribute("schemasList");
		}
		if(schemasList.size()>=4){
			schemasList.remove(0);
		}
		schemasList.add(map);
		context.request.getSession().setAttribute("schemasList", schemasList);		
		Map outputMap=new HashMap();
		outputMap.put("schemasList", schemasList);
		
		//Add by Michael 2012 01/29 增加费用保存
		context.request.getSession().setAttribute("feeList", feeList);
		outputMap.put("feeList", feeList);
		
		Output.jsonOutput(outputMap, context);
	}
	
	
	@SuppressWarnings("unchecked")
	public void removeSchemaFromSession(Context context){
		List schemasList=(List)context.request.getSession().getAttribute("schemasList");
		if(context.contextMap.get("idValue")!=null){
			int idValue=Integer.parseInt(context.contextMap.get("idValue")+"");
			schemasList.remove(idValue);
			context.request.getSession().setAttribute("schemasList", schemasList);
		}
		Map outputMap=new HashMap();
		outputMap.put("schemasList", schemasList);
		Output.jsonOutput(outputMap, context);
		
	}
	
	
	@SuppressWarnings("unchecked")
	public void updateSchema(Context context){
		List schemasList=(List)context.request.getSession().getAttribute("schemasList");
		//Add by Michael 2012 01/29 增加费用保存
		List feeList=(List)context.request.getSession().getAttribute("feeList");
		
		List errList = context.errList ;
		Map outputMap = new HashMap() ;
		if(context.contextMap.get("session_schema")!=null){
			int idValue=Integer.parseInt(context.contextMap.get("session_schema")+"");
			Map schemaMap=(Map)schemasList.get(idValue);
			schemaMap.put("CREDIT_ID", context.contextMap.get("CREDIT_ID"));
			schemaMap.put("credit_id", context.contextMap.get("CREDIT_ID"));
			schemaMap.put("S_EMPLOYEEID", context.contextMap.get("s_employeeId"));
			schemaMap.put("s_employeeId", context.contextMap.get("s_employeeId"));
			
			//Marked by Michael 2012 01/30  重车不计算、不保存保险税率
			//PaylistUtil.setBaseRate(schemaMap);
			String contractType=(String) context.contextMap.get("contract_type");
			PaylistUtil.setBaseRate(schemaMap,contractType);
			
//			System.out.println("PAY_LEASE_PERIOD="+schemaMap.get("PAY_LEASE_PERIOD"));
//			System.out.println("PAY_LEASE_TERM_VALUE="+schemaMap.get("PAY_LEASE_TERM_VALUE"));
//			System.out.println("PAY_PLEDGE_PRICE="+schemaMap.get("PAY_PLEDGE_PRICE"));
//			System.out.println("PAY_HEAD_HIRE="+schemaMap.get("PAY_HEAD_HIRE"));
//			System.out.println("PAY_HEAD_HIRE_PERCENT="+schemaMap.get("PAY_HEAD_HIRE_PERCENT"));
//			System.out.println("PAY_FLOAT_RATE="+schemaMap.get("PAY_FLOAT_RATE"));
//			System.out.println("PAY_MANAGEMENT_FEE="+schemaMap.get("PAY_MANAGEMENT_FEE"));
//			System.out.println("PAY_YEAR_INTEREST="+schemaMap.get("PAY_YEAR_INTEREST"));
//			System.out.println("PAY_LEASE_TOPRIC="+schemaMap.get("PAY_LEASE_TOPRIC"));
//			System.out.println("PAY_PAY_WAY_VALUE="+schemaMap.get("PAY_PAY_WAY_VALUE"));
//			System.out.println("PAY_START_DATE="+schemaMap.get("PAY_START_DATE"));
//			System.out.println("PAY_LEASE_RZE="+schemaMap.get("PAY_LEASE_RZE"));
//			System.out.println("PAY_PLEDGE_PRICE_RATE="+schemaMap.get("PAY_PLEDGE_PRICE_RATE"));
//			System.out.println("PAY_MANAGEMENT_FEE_RATE="+schemaMap.get("PAY_MANAGEMENT_FEE_RATE"));
//			System.out.println("s_employeeId="+schemaMap.get("s_employeeId"));
//			System.out.println("getdate="+schemaMap.get("getdate")+new java.util.Date());
//			System.out.println("PAY_PLEDGE_WAY_VALUE="+schemaMap.get("PAY_PLEDGE_WAY_VALUE"));
//			System.out.println("PAY_INSURE_BASE_RATE="+schemaMap.get("PAY_INSURE_BASE_RATE"));
//			System.out.println("PAY_SALES_TAX_RATE="+schemaMap.get("PAY_SALES_TAX_RATE"));
//			System.out.println("cust_tr="+schemaMap.get("cust_tr"));
//			System.out.println("CREDIT_ID="+schemaMap.get("CREDIT_ID"));
//			System.out.println("0="+schemaMap.get(""));
//			System.out.println("TR_IRR_RATE="+schemaMap.get("TR_IRR_RATE"));
//			System.out.println("STAMP_TAX_INSUREPRIC="+schemaMap.get("STAMP_TAX_INSUREPRIC"));
//			System.out.println("1="+schemaMap.get(""));
//			System.out.println("1="+schemaMap.get(""));
//			System.out.println("1="+schemaMap.get(""));
//			System.out.println("PAY_STAMP_TAX_TOPRIC="+schemaMap.get("PAY_STAMP_TAX_TOPRIC"));
//			System.out.println("PAY_STAMP_TAX_MONTHPRIC="+schemaMap.get("PAY_STAMP_TAX_MONTHPRIC"));
//			System.out.println("PLEDGE_PERIOD="+schemaMap.get("PLEDGE_PERIOD"));
//			System.out.println("0="+schemaMap.get(""));
//			System.out.println("PAY_PLEDGE_REALPRIC="+schemaMap.get("PAY_PLEDGE_REALPRIC"));

			
			SqlMapClient sqlMapper = DataAccessor.getSession() ;
			try {
				sqlMapper.startTransaction() ;
				List obj=(List)sqlMapper.queryForList("creditReportManage.selectCreditScheme", schemaMap);
				if(obj.size()!=0){
					sqlMapper.update("creditReportManage.updateSchema", schemaMap);
				}else{
					sqlMapper.insert("creditReportManage.insertSchemaFromCompare", schemaMap);
				}
				fakeDeleteIrrMonth(sqlMapper,schemaMap);
				insertIrrMonth(sqlMapper,schemaMap);
				//Add by Michael 2012 01/14 增加费用保存
				context.contextMap.put("credit_id", schemaMap.get("CREDIT_ID"));
				sqlMapper.delete("creditReportManage.deletePayListFeeList",
						context.contextMap);
				Map where=null;

				for (int i = 0; i < feeList.size(); i++) {
					Map tempMap = new HashMap();
					where = (Map) feeList.get(i);
					tempMap.put("FEE_SET_ID", where.get("FEE_SET_ID"));
					tempMap.put("CREATE_SHOW_NAME", where.get("CREATE_SHOW_NAME"));
					tempMap.put("IS_LEASERZE_COST", where.get("IS_LEASERZE_COST"));
					tempMap.put("CREATE_FILED_NAME", where.get("CREATE_FILED_NAME")) ;
					tempMap.put("CREATE_ID", context.contextMap.get("s_employeeId"));
					tempMap.put("CREDIT_ID", context.contextMap.get("credit_id"));
					tempMap.put("FEE", DataUtil.doubleUtil(where.get("FEE")));
					
					//Add by Michael 2012 12-21 增加费用来源
					tempMap.put("SOURCE_CODE", where.get("SOURCE_CODE"));
					
					sqlMapper.insert("creditReportManage.insertPayListFeeList", tempMap);
				}			
				//----------------------------------------------------------------				
				
				sqlMapper.commitTransaction() ;
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("报告管理--现场调查报告-租金测算方案修改错误!请联系管理员") ;
			} finally{
				try {
					sqlMapper.endTransaction() ;
				} catch (SQLException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger) ;
				}
			}
			context.request.getSession().removeAttribute("schemasList");
 
		}
		if(errList.isEmpty()){
			Output.jspSendRedirect(context, "../servlet/defaultDispatcher?__action=creditReportVip.selectCreditForUpdate&credit_id="+context.contextMap.get("CREDIT_ID")+"&showFlag=0");
		} else {
			outputMap.put("errList",errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	/**
	 * wujw
	 * fake delete 
	 * @param map
	 * @throws Exception
	 */
	public static void fakeDeleteIrrMonth(SqlMapClient sqlMapper,Map map) throws Exception {
		sqlMapper.startBatch() ;
		sqlMapper.delete("creditReportManage.deleteCreditSchemaIrr", map);
		sqlMapper.executeBatch() ;
		// DataAccessor.execute("creditReportManage.update-credit-schema-irr", map, DataAccessor.OPERATION_TYPE.UPDATE);
	}
	/**
	 * wujw
	 * insert into T_PRJT_CREDITSCHEMEIRR
	 * @param schemaMap
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static void insertIrrMonth(SqlMapClient sqlMapper , Map schemaMap) throws Exception {
		String payIrrMonthPrices = String.valueOf(schemaMap.get("PAY_IRR_MONTH_PRICE"));
		String payIrrMonthPriceStart = String.valueOf(schemaMap.get("PAY_IRR_MONTH_PRICE_START"));
		String payIrrMonthPriceEnd = String.valueOf(schemaMap.get("PAY_IRR_MONTH_PRICE_END"));
		
		String[] irrMonthPrice = payIrrMonthPrices.split(",");
		String[] irrMonthPriceStart = payIrrMonthPriceStart.split(",");
		String[] irrMonthPriceEnd = payIrrMonthPriceEnd.split(",");
		
		sqlMapper.startBatch() ;
		for (int i=0; i<irrMonthPrice.length; i++) {
			Map paramMap = new HashMap();
			paramMap.put("IRR_MONTH_PRICE", irrMonthPrice[i]);
			paramMap.put("IRR_MONTH_PRICE_START", irrMonthPriceStart[i]);
			paramMap.put("IRR_MONTH_PRICE_END", irrMonthPriceEnd[i]);
			paramMap.put("CREDIT_ID", schemaMap.get("CREDIT_ID"));
			paramMap.put("S_EMPLOYEEID", schemaMap.get("S_EMPLOYEEID"));
			sqlMapper.insert("creditReportManage.create-credit-schema-irr", paramMap);
		}
		sqlMapper.executeBatch() ;
	}

	/* (non-Javadoc)
	 * @see com.brick.service.core.AService#afterExecute(java.lang.String, com.brick.service.entity.Context)
	 */
	@Override
	protected void afterExecute(String action, Context context) {
		// TODO Auto-generated method stub
		super.afterExecute(action, context);
	}

	/* (non-Javadoc)
	 * @see com.brick.service.core.AService#preExecute(java.lang.String, com.brick.service.entity.Context)
	 */
	@Override
	protected boolean preExecute(String action, Context context) {
		// TODO Auto-generated method stub
		return super.preExecute(action, context);
	}
	//此方法是合同浏览页时如果没有支付表时导出报告的租金测算的数据
	 public void exportPaylistBeforeByHu(Context context) {
		
		 Map outputMap = new HashMap();
		 List errList = context.errList;
		 Map creditMap = null;
		 Map schema = null;
		 Map paylist = null;
		 Map memoMap = null;
		 try{
			 context.contextMap.put("data_type", "客户来源");
				creditMap = (Map) DataAccessor.query("creditReportManage.selectCreditBaseInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("creditMap", creditMap);
				//查询方案
				schema = (Map) DataAccessor.query("creditReportManage.selectCreditScheme",context.contextMap, DataAccessor.RS_TYPE.MAP);
				// 查询应付租金列表
				List<Map> irrMonthPaylines = StartPayService.queryPackagePayline(context.contextMap.get("credit_id"), Integer.valueOf(1));
				
				// 解压irrMonthPaylines到每一期的钱
				List<Map> rePaylineList = StartPayService.upPackagePaylines(irrMonthPaylines);
				// 
				if (schema != null) {
					schema.put("TOTAL_PRICE", schema.get("LEASE_TOPRIC"));
					schema.put("LEASE_PERIOD", schema.get("LEASE_TERM"));
					schema.put("LEASE_TERM", schema.get("LEASE_COURSE"));
					
					//Add By Michael 2012 1/4
					schema.put("TR_RATE", schema.get("TR_RATE"));
					schema.put("TR_IRR_RATE", schema.get("TR_IRR_RATE"));
					schema.put("RATE_DIFF", schema.get("RATE_DIFF"));
					// 
					
					//add by Michael 把管理费收入总和传过来，计算营业税收入，会影响TR计算----------------------
					double totalFeeSet=0.0d;
					
					if("2".equals(schema.get("TAX_PLAN_CODE"))){
						List<Map> listTotalFeeSet=(List) DataAccessor.query("creditReportManage.getTotalFeeByRectID",context.contextMap, DataAccessor.RS_TYPE.LIST);
						for(Map map:listTotalFeeSet){
							totalFeeSet+=new BigDecimal(DataUtil.doubleUtil(map.get("FEE"))/1.06).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
						}	
					}else if("1".equals(schema.get("TAX_PLAN_CODE"))||"3".equals(schema.get("TAX_PLAN_CODE"))){
						totalFeeSet=(Double)DataAccessor.query("creditReportManage.sumTotalFeeByRectID",context.contextMap, DataAccessor.RS_TYPE.OBJECT);
					}
					
					schema.put("FEESET_TOTAL", totalFeeSet);
					//-----------------------------------------------------------------------------
				
					if (irrMonthPaylines.size() > 0) {
						// 如果应付租金存在，则以应付租金的方式计算
						paylist = StartPayService.createCreditPaylistIRR(schema,rePaylineList,irrMonthPaylines);

					} else {
						// 如果应付租金不存在，则以年利率(合同利率)的方式计算
						paylist = StartPayService.createCreditPaylist(schema,new ArrayList<Map>());
					}
					paylist.put("PLEDGE_ENTER_MCTOAG", schema.get("PLEDGE_ENTER_MCTOAG"));
					paylist.put("PLEDGE_ENTER_AGRATE", schema.get("PLEDGE_ENTER_AGRATE"));	
					paylist.put("PLEDGE_ENTER_MCTOAGRATE", schema.get("PLEDGE_ENTER_MCTOAGRATE"));	
					
					
				}
							
				context.contextMap.put("paylist", paylist);
				context.contextMap.put("schema", schema);
				context.contextMap.put("rePaylineList", rePaylineList);
				context.contextMap.put("irrMonthPaylines", irrMonthPaylines);
				context.contextMap.put("hu_rentcontractexportcredit", "true");
			 this.exportPaylist(context);
		 }catch(Exception e){
			 e.printStackTrace();
			 LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		 }
	 }
	
	@SuppressWarnings("unchecked")
	public void exportPaylist(Context context) {
		ByteArrayOutputStream baos = null;
		try {
			// 字体设置
			BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
			Font FontColumn = new Font(bfChinese, 12, Font.BOLD);
			Font FontColumnSmall = new Font(bfChinese, 10, Font.BOLD);
			Font FontDefault = new Font(bfChinese, 12, Font.NORMAL);
			Font fa = new Font(bfChinese, 15, Font.BOLD);
			// 数字格式
			NumberFormat nfFSNum = new DecimalFormat("###,###,###,###.00");
			nfFSNum.setGroupingUsed(true);
			nfFSNum.setMaximumFractionDigits(2);
			// 页面设置
			Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
			Document document = new Document(rectPageSize, 5, 5, 20, 0); // 其余4个参数，设置了页面的4个边距
			baos = new ByteArrayOutputStream();
			PdfWriter.getInstance(document, baos);
			//打开文档
			document.open();
			
			Map creditMap = null;
			Map schema = null;
			Map paylist = null;
			List<Map> payWays = null;
			
			Map dataDictionaryMap = new HashMap();
			dataDictionaryMap.put("dataType", "支付方式");
			payWays = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
					
			// credit_id 
			context.contextMap.put("data_type", "客户来源");
			creditMap = (Map) DataAccessor.query("creditReportManage.selectCreditBaseInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);
			// 
			context.contextMap.put("PAY_TAX_PLAN_CODE", "1");
			//---------------从页面取值--------------
			schema = copySchema(schema, context.contextMap);

			//Add by Michael 2012 01/29 在方案里增加合同类型
			schema.put("CONTRACT_TYPE", String.valueOf(creditMap.get("CONTRACT_TYPE")));	
			//
			List<Map> rePaylineList = StartPayService.upPackagePaylines(context);
			//以下是原来的数据源
			/*
			List<Map> rePaylineList = new ArrayList<Map>();
			
			String[] periodNums = HTMLUtil.getParameterValues(context.request, "PERIOD_NUM", "0");
			String[] monthPrices = HTMLUtil.getParameterValues(context.request, "MONTH_PRICE", "0");
			for (int i=0;i<(periodNums==null?0:periodNums.length);i++) {
				Map line = new HashMap();
				line.put("PERIOD_NUM", periodNums[i]);
				line.put("MONTH_PRICE", monthPrices[i]);
				rePaylineList.add(line);
			}
			
			paylist = StartPayService.createCreditPaylist(schema,rePaylineList);
			//---------------从页面取值--------------
			 * */
			//以上是原来的数据源0516胡昭卿注释
			//新的数据源
			
			if(context.contextMap.get("hu_rentcontractexportcredit")!=null){
				if(Boolean.parseBoolean(context.contextMap.get("hu_rentcontractexportcredit").toString())){
				paylist = (Map)context.contextMap.get("paylist");
				paylist.put("oldirrMonthPaylines", paylist.get("irrMonthPaylines"));
				paylist.put("irrMonthPaylines", paylist.get("irrMonthPaylines"));
				}
			}else{
			paylist = StartPayService.createCreditPaylistIRR(schema,rePaylineList,StartPayService.keepPackagePayline(context));
			paylist.put("oldirrMonthPaylines", paylist.get("irrMonthPaylines"));
			paylist.put("irrMonthPaylines", StartPayService.keepPackagePayline(context));
			StartPayService.packagePaylinesForMon(paylist);
			}
			
			//Add by Michael 2012 01/14 For 方案费用查询 影响概算成本为1 不影响为0
			List feeListRZE=null;
			feeListRZE = (List) DataAccessor.query("creditReportManage.getCreditFeeListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
			context.contextMap.put("feeListRZE", feeListRZE);
			List feeList=null;
			feeList = (List) DataAccessor.query("creditReportManage.getCreditFeeList",context.contextMap, DataAccessor.RS_TYPE.LIST);
			context.contextMap.put("feeList", feeList);				
			//---------------从数据库取值--------------
//			//查询方案
//			schema = (Map) DataAccessor.query("creditReportManage.selectCreditScheme",context.contextMap, DataAccessor.RS_TYPE.MAP);
//			// 查询应付租金列表
//			List<Map> irrMonthPaylines = StartPayService.queryPackagePayline(context.contextMap.get("credit_id"), Integer.valueOf(1));
//			// 解压irrMonthPaylines到每一期的钱
//			List<Map> rePaylineList = StartPayService.upPackagePaylines(irrMonthPaylines);
//			// 
//			if (schema != null) {
//				schema.put("TOTAL_PRICE", schema.get("LEASE_TOPRIC"));
//				schema.put("LEASE_PERIOD", schema.get("LEASE_TERM"));
//				schema.put("LEASE_TERM", schema.get("LEASE_COURSE"));
//				// 
//				if (irrMonthPaylines.size() > 0) {
//					// 如果应付租金存在，则以应付租金的方式计算
//					paylist = StartPayService.createCreditPaylistIRR(schema,rePaylineList,irrMonthPaylines);
//				} else {
//					// 如果应付租金不存在，则以年利率(合同利率)的方式计算
//					paylist = StartPayService.createCreditPaylist(schema,new ArrayList<Map>());
//				}
//				
//			}
			
			//---------------从数据库取值--------------
			
		    List<Map> paylines =null;
		    paylines = (List<Map>) paylist.get("paylines");
		    		
		    PdfPTable tT = new PdfPTable(1);
		    tT.setWidthPercentage(100f);
		    tT.addCell(makeCellWithNoBorder("测算表", PdfPCell.ALIGN_CENTER, fa));
		    tT.addCell(makeCellWithNoBorder("", PdfPCell.ALIGN_CENTER, fa));
		    tT.addCell(makeCellWithNoBorder("", PdfPCell.ALIGN_CENTER, fa));
		    document.add(tT);
		    
		    PdfPTable tT2 = new PdfPTable(2);
		    tT2.setWidthPercentage(100f);
		    tT2.addCell(makeCell("承租人姓名："+creditMap.get("CUST_NAME").toString(), PdfPCell.ALIGN_LEFT, FontDefault));
		    tT2.addCell(makeCell("客户经理："+creditMap.get("SENSOR_NAME").toString(), PdfPCell.ALIGN_LEFT, FontDefault));
		    tT2.addCell(makeCellWithNoBorder("", PdfPCell.ALIGN_LEFT, FontDefault));
		    tT2.addCell(makeCellWithNoBorder("", PdfPCell.ALIGN_LEFT, FontDefault));
		    document.add(tT2);
		    
		    PdfPTable t3 = new PdfPTable(4);
			t3.setWidthPercentage(100);
			t3.addCell(makeCellSetColspan("融资租赁方案", PdfPCell.ALIGN_LEFT, FontColumn, 4));
			t3.addCell(makeCell("融资租赁总价值", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell(NumberUtils.formatdigital(DataUtil.doubleUtil(paylist.get("LEASE_TOPRIC"))), PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("概算成本（RZE）", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell(NumberUtils.formatdigital(DataUtil.doubleUtil(paylist.get("LEASE_RZE"))), PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("租赁期数", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell(DataUtil.StringUtil(paylist.get("LEASE_PERIOD")), PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("租赁周期", PdfPCell.ALIGN_LEFT, FontDefault));
			String leaseterm = null;
			if(DataUtil.intUtil(paylist.get("LEASE_TERM"))==1){
				leaseterm = "月份";
			}else if(DataUtil.intUtil(paylist.get("LEASE_TERM"))==3){
				leaseterm = "季度";
			}else if(DataUtil.intUtil(paylist.get("LEASE_TERM"))==6){
				leaseterm = "半年";
			}else if(DataUtil.intUtil(paylist.get("LEASE_TERM"))==12){
				leaseterm = "年度";
			}
			t3.addCell(makeCell(leaseterm, PdfPCell.ALIGN_LEFT, FontDefault));
		
			//t3.addCell(makeCell("管理费", PdfPCell.ALIGN_LEFT, FontDefault));
			//t3.addCell(makeCell(NumberUtils.retain3rounded(DataUtil.doubleUtil(paylist.get("MANAGEMENT_FEE_RATE")))+"%    "+NumberUtils.formatdigital(DataUtil.doubleUtil(paylist.get("MANAGEMENT_FEE"))), PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("首期租金", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell(NumberUtils.retain3rounded(DataUtil.doubleUtil(paylist.get("HEAD_HIRE_PERCENT")==null?0:paylist.get("HEAD_HIRE_PERCENT")))+"%    "+NumberUtils.formatdigital(DataUtil.doubleUtil(paylist.get("HEAD_HIRE")==null?0:paylist.get("HEAD_HIRE"))), PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("保证金", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell(NumberUtils.retain3rounded(DataUtil.doubleUtil(paylist.get("PLEDGE_PRICE_RATE")))+"%    "+NumberUtils.formatdigital(DataUtil.doubleUtil(paylist.get("PLEDGE_PRICE"))), PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("", PdfPCell.ALIGN_LEFT, FontDefault));
			
			String PLEDGE_AVE_PRICE = "0";
			if(paylist.get("PLEDGE_AVE_PRICE")!=null){
				PLEDGE_AVE_PRICE =NumberUtils.formatdigital(DataUtil.doubleUtil(paylist.get("PLEDGE_AVE_PRICE")));
			}
			String PLEDGE_BACK_PRICE = "0";
			if(paylist.get("PLEDGE_BACK_PRICE")!=null){
				PLEDGE_BACK_PRICE =NumberUtils.formatdigital(DataUtil.doubleUtil(paylist.get("PLEDGE_BACK_PRICE")));
			}
			String PLEDGE_LAST_PRICE = "0";
			if(paylist.get("PLEDGE_LAST_PRICE")!=null){
				PLEDGE_LAST_PRICE =NumberUtils.formatdigital(DataUtil.doubleUtil(paylist.get("PLEDGE_LAST_PRICE"))+DataUtil.doubleUtil(paylist.get("PLEDGE_LAST_PRICE_TAX")));
			}
			String PLEDGE_LAST_PERIOD = "0";
			if(paylist.get("PLEDGE_LAST_PERIOD")!=null){
				PLEDGE_LAST_PERIOD =DataUtil.StringUtil(paylist.get("PLEDGE_LAST_PERIOD"));
			}
			String PLEDGE_PERIOD = "0";
			if(paylist.get("PLEDGE_PERIOD")!=null){
				PLEDGE_PERIOD =DataUtil.StringUtil(paylist.get("PLEDGE_PERIOD"));
			}
			
			t3.addCell(makeCell("用于平均抵冲金额", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell(PLEDGE_AVE_PRICE, PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("用于期末退还金额", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell(PLEDGE_BACK_PRICE, PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("用于最后抵冲含税金额/期数", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell(PLEDGE_LAST_PRICE+"   "+PLEDGE_LAST_PERIOD, PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("收入时间", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("第"+PLEDGE_PERIOD+"期", PdfPCell.ALIGN_LEFT, FontDefault));			
			t3.addCell(makeCell("保证金入账", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCellSetColspan("入我司 "+NumberUtils.retain3rounded(DataUtil.doubleUtil(paylist.get("PLEDGE_ENTER_CMPRICE")==null?0:paylist.get("PLEDGE_ENTER_CMPRICE")))+" 税金 "+NumberUtils.retain3rounded(DataUtil.doubleUtil(paylist.get("PLEDGE_ENTER_CMRATE")==null?0:paylist.get("PLEDGE_ENTER_CMRATE")))+" 我司入供应商 "+NumberUtils.retain3rounded(DataUtil.doubleUtil(context.contextMap.get("PAY_PLEDGE_ENTER_MCTOAG")==null?0:context.contextMap.get("PAY_PLEDGE_ENTER_MCTOAG")))+" 税金 "+NumberUtils.retain3rounded(DataUtil.doubleUtil(context.contextMap.get("PAY_PLEDGE_ENTER_MCTOAGRATE")==null?0:context.contextMap.get("PAY_PLEDGE_ENTER_MCTOAGRATE")))+"\n 入供应商 "+NumberUtils.retain3rounded(DataUtil.doubleUtil(paylist.get("PLEDGE_ENTER_AG")==null?0:paylist.get("PLEDGE_ENTER_AG")))+" 税金 "+NumberUtils.retain3rounded(DataUtil.doubleUtil(context.contextMap.get("PAY_PLEDGE_ENTER_AGRATE")==null?0:context.contextMap.get("PAY_PLEDGE_ENTER_AGRATE"))), PdfPCell.ALIGN_LEFT, FontDefault, 3));
			
			t3.addCell(makeCellSetColspan("管理费收入", PdfPCell.ALIGN_LEFT, FontColumn, 4));
	
			for (int i = 0; i < feeListRZE.size(); i++) {
				Map map = (Map) feeListRZE.get(i);
				t3.addCell(makeCell(map.get("CREATE_SHOW_NAME")+"", PdfPCell.ALIGN_LEFT, FontDefault));
				t3.addCell(makeCell(NumberUtils.formatdigital(DataUtil.doubleUtil(map.get("FEE"))), PdfPCell.ALIGN_LEFT, FontDefault));
				t3.addCell(makeCell("", PdfPCell.ALIGN_LEFT, FontDefault));
				t3.addCell(makeCell("", PdfPCell.ALIGN_LEFT, FontDefault));
			}	
			
			t3.addCell(makeCellSetColspan("非管理费收入", PdfPCell.ALIGN_LEFT, FontColumn, 4));
			for (int i = 0; i < feeList.size(); i++) {
				Map map = (Map) feeList.get(i);
				t3.addCell(makeCell(map.get("CREATE_SHOW_NAME")+"", PdfPCell.ALIGN_LEFT, FontDefault));
				t3.addCell(makeCell(NumberUtils.formatdigital(DataUtil.doubleUtil(map.get("FEE"))), PdfPCell.ALIGN_LEFT, FontDefault));
				t3.addCell(makeCell("", PdfPCell.ALIGN_LEFT, FontDefault));
				t3.addCell(makeCell("", PdfPCell.ALIGN_LEFT, FontDefault));
			}	
			
			
			t3.addCell(makeCell("支付方式", PdfPCell.ALIGN_LEFT, FontDefault));
			String payway = null;
			for(int x=0;x<payWays.size();x++){
				if(DataUtil.intUtil(payWays.get(x).get("CODE"))==DataUtil.intUtil(paylist.get("PAY_WAY"))){
					payway = DataUtil.StringUtil(payWays.get(x).get("FLAG"));
				}
			}
			t3.addCell(makeCell(payway, PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("预估起租日", PdfPCell.ALIGN_LEFT, FontDefault));

			if(paylist.get("START_DATE")!=null && !paylist.get("START_DATE").equals("")){
				t3.addCell(makeCell(DataUtil.dateToStringUtil(paylist.get("START_DATE"), "yyyy-MM-dd"), PdfPCell.ALIGN_LEFT, FontDefault));
			}else{
				t3.addCell(makeCell("", PdfPCell.ALIGN_LEFT, FontDefault));					
			}					
			t3.addCell(makeCell("客户TR", PdfPCell.ALIGN_LEFT, FontDefault));
			//Modify by Michael 2012 1/5  schemaMap
			//t3.addCell(makeCell(NumberUtils.retain3rounded(DataUtil.doubleUtil(paylist.get("TR_RATE")))+"%", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell(NumberUtils.retain3rounded(DataUtil.doubleUtil(schema.get("TR_RATE")))+"%", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("实际TR", PdfPCell.ALIGN_LEFT, FontDefault));
			//t3.addCell(makeCell(NumberUtils.retain3rounded(DataUtil.doubleUtil(paylist.get("TR_IRR_RATE")))+"%", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell(NumberUtils.retain3rounded(DataUtil.doubleUtil(schema.get("TR_IRR_RATE")))+"%", PdfPCell.ALIGN_LEFT, FontDefault));
			double RATE_DIFF=0d;
//			for(int i=0;i<paylines.size();i++){
//				RATE_DIFF = RATE_DIFF + DataUtil.doubleUtil(paylines.get(i).get("REN_PRICE"))-DataUtil.doubleUtil(paylines.get(i).get("COST_PRICE"));
//			}
			//Modify by Michael 2012 1/5 
//			for(int i=0;i<paylines.size();i++){
//				RATE_DIFF = RATE_DIFF + DataUtil.doubleUtil(paylines.get(i).get("PV_PRICE"));
//			}						
			t3.addCell(makeCell("利差", PdfPCell.ALIGN_LEFT, FontDefault));
			//Modify by Michael 2012 1/5 
			//t3.addCell(makeCell(NumberUtils.formatdigital(RATE_DIFF), PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell(NumberUtils.retain3rounded(DataUtil.doubleUtil(schema.get("RATE_DIFF")))+"", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("第一年保险费", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell(NumberUtils.formatdigital(DataUtil.doubleUtil(paylines.get(0).get("INSURE_PRICE"))), PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("印花税", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell(DataUtil.StringUtil(paylist.get("STAMP_TAX_INSUREPRIC")), PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("合同利率TRR", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell(NumberUtils.retain3rounded(DataUtil.doubleUtil(paylist.get("YEAR_INTEREST")))+"%", PdfPCell.ALIGN_LEFT, FontDefault));
			
			t3.addCell(makeCellSetColspan("融资租赁方案测算方式一", PdfPCell.ALIGN_LEFT, FontColumn, 4));
			List list = (List) paylist.get("oldirrMonthPaylines");
			for (int i = 0; i < list.size(); i++) {
				Map map = (Map) list.get(i);
				t3.addCell(makeCell("应付租金", PdfPCell.ALIGN_LEFT, FontDefault));
				t3.addCell(makeCell(NumberUtils.formatdigital(DataUtil.doubleUtil(map.get("IRR_MONTH_PRICE"))), PdfPCell.ALIGN_LEFT, FontDefault));
				t3.addCell(makeCell("对应期次", PdfPCell.ALIGN_LEFT, FontDefault));
				t3.addCell(makeCell("第"+map.get("IRR_MONTH_PRICE_START")+"期到第"+map.get("IRR_MONTH_PRICE_END"), PdfPCell.ALIGN_LEFT, FontDefault));
			}
			t3.addCell(makeCellSetColspan("融资租赁方案测算方式二", PdfPCell.ALIGN_LEFT, FontColumn, 4));
			t3.addCell(makeCell("", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("合同利率", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell(NumberUtils.retain3rounded(DataUtil.doubleUtil(paylist.get("YEAR_INTEREST")))+"%", PdfPCell.ALIGN_LEFT, FontDefault));
			
			document.add(t3);	
		    		    
		    float[] widthsStl = {0.05f,0.10f,0.10f,0.10f,/*0.10f,*/0.10f,0.10f,0.10f,0.07f,0.10f,0.10f};			
//		    float[] widthsStl = {0.05f,0.10f,0.10f,0.10f,0.10f,0.10f,0.10f,0.10f,0.07f,0.10f};			
			PdfPTable t2 = new PdfPTable(widthsStl);
			t2.setWidthPercentage(100);
			t2.addCell(makeCellSetColspan("还款计划", PdfPCell.ALIGN_LEFT, FontColumn, 11));
			t2.addCell(makeCell("期次", PdfPCell.ALIGN_CENTER, FontColumn));
			t2.addCell(makeCell("支付时间", PdfPCell.ALIGN_CENTER, FontColumn));
			t2.addCell(makeCell("应收租金", PdfPCell.ALIGN_CENTER, FontColumn));
			/*t2.addCell(makeCell("净现金流", PdfPCell.ALIGN_CENTER, FontColumn));*/
			t2.addCell(makeCell("预期租金", PdfPCell.ALIGN_CENTER, FontColumn));
			t2.addCell(makeCell("本金", PdfPCell.ALIGN_CENTER, FontColumn));
			t2.addCell(makeCell("利息", PdfPCell.ALIGN_CENTER, FontColumn));
			t2.addCell(makeCell("剩余本金", PdfPCell.ALIGN_CENTER, FontColumn));
			t2.addCell(makeCell("营业税", PdfPCell.ALIGN_CENTER, FontColumn));
			t2.addCell(makeCell("收入", PdfPCell.ALIGN_CENTER, FontColumn));
			t2.addCell(makeCell("实际本金", PdfPCell.ALIGN_CENTER, FontColumn));
			double irrmonthpriceall = 0d;
			double irrpriceall = 0d;
			double monthpriceall = 0d;
			double ownpriceall = 0d;
			double lastpriceall = 0d;
			double salestaxall = 0d;
			double incomeall = 0d;
			
//			double pledge_ave_price = DataUtil.doubleUtil(paylist.get("PLEDGE_AVE_PRICE"));
			double realownpriceall = 0d;
			for(int x=0;x<paylines.size();x++){
				t2.addCell(makeCell(DataUtil.StringUtil(paylines.get(x).get("PERIOD_NUM")), PdfPCell.ALIGN_CENTER, FontColumnSmall));
				t2.addCell(makeCell(DataUtil.StringUtil(DataUtil.dateToStringUtil(paylines.get(x).get("PAY_DATE"), "yyyy-MM-dd")), PdfPCell.ALIGN_CENTER, FontColumnSmall));
				double irrmonthprice = DataUtil.doubleUtil(paylines.get(x).get("IRR_MONTH_PRICE"));
				double irrprice = DataUtil.doubleUtil(paylines.get(x).get("IRR_PRICE"));
				double monthprice = DataUtil.doubleUtil(paylines.get(x).get("MONTH_PRICE"));
				double ownprice = DataUtil.doubleUtil(paylines.get(x).get("OWN_PRICE"));
				double lastprice = DataUtil.doubleUtil(paylines.get(x).get("LAST_PRICE"));
				double renprice = DataUtil.doubleUtil(paylines.get(x).get("REN_PRICE"));
				double salestax = DataUtil.doubleUtil(paylines.get(x).get("SALES_TAX"));
				double real_own_price = DataUtil.doubleUtil(paylines.get(x).get("REAL_OWN_PRICE"));
				double income = renprice - salestax;
//				double realownprice = lastprice - pledge_ave_price;
				t2.addCell(makeCell(NumberUtils.formatdigital(irrmonthprice), PdfPCell.ALIGN_RIGHT, FontColumnSmall));
				/*t2.addCell(makeCell(NumberUtils.formatdigital(irrprice), PdfPCell.ALIGN_RIGHT, FontColumnSmall));*/
				t2.addCell(makeCell(NumberUtils.formatdigital(monthprice), PdfPCell.ALIGN_RIGHT, FontColumnSmall));
				t2.addCell(makeCell(NumberUtils.formatdigital(ownprice), PdfPCell.ALIGN_RIGHT, FontColumnSmall));
				t2.addCell(makeCell(NumberUtils.formatdigital(renprice), PdfPCell.ALIGN_RIGHT, FontColumnSmall));
				t2.addCell(makeCell(NumberUtils.formatdigital(lastprice), PdfPCell.ALIGN_RIGHT, FontColumnSmall));
				t2.addCell(makeCell(NumberUtils.formatdigital(salestax), PdfPCell.ALIGN_RIGHT, FontColumnSmall));
				t2.addCell(makeCell(NumberUtils.formatdigital(income), PdfPCell.ALIGN_RIGHT, FontColumnSmall));	
				t2.addCell(makeCell(NumberUtils.formatdigital(real_own_price), PdfPCell.ALIGN_RIGHT, FontColumnSmall));	
				
				irrmonthpriceall = irrmonthprice + irrmonthpriceall;
				irrpriceall = irrprice + irrpriceall;
				monthpriceall = monthprice + monthpriceall;
				ownpriceall = ownprice + ownpriceall;
				lastpriceall = renprice + lastpriceall;
				salestaxall = salestax + salestaxall;
				incomeall = income + incomeall;
				realownpriceall = real_own_price + realownpriceall;
			}
			
			t2.addCell(makeCellSetColspan("合计", PdfPCell.ALIGN_CENTER, FontColumnSmall,2));
			t2.addCell(makeCell(NumberUtils.formatdigital(irrmonthpriceall), PdfPCell.ALIGN_RIGHT, FontColumnSmall));
			/*t2.addCell(makeCell(NumberUtils.formatdigital(irrpriceall), PdfPCell.ALIGN_RIGHT, FontColumnSmall));*/
			t2.addCell(makeCell(NumberUtils.formatdigital(monthpriceall), PdfPCell.ALIGN_RIGHT, FontColumnSmall));
			t2.addCell(makeCell(NumberUtils.formatdigital(ownpriceall), PdfPCell.ALIGN_RIGHT, FontColumnSmall));
			t2.addCell(makeCell(NumberUtils.formatdigital(lastpriceall), PdfPCell.ALIGN_RIGHT, FontColumnSmall));
			t2.addCell(makeCell("", PdfPCell.ALIGN_RIGHT, FontColumnSmall));
			t2.addCell(makeCell(NumberUtils.formatdigital(salestaxall), PdfPCell.ALIGN_RIGHT, FontColumnSmall));
			t2.addCell(makeCell(NumberUtils.formatdigital(incomeall), PdfPCell.ALIGN_RIGHT, FontColumnSmall));	
			t2.addCell(makeCell(NumberUtils.formatdigital(realownpriceall), PdfPCell.ALIGN_RIGHT, FontColumnSmall));	
				
			document.add(t2);
			
		
			document.close();
			context.response.setContentType("application/pdf");
			context.response.setCharacterEncoding("UTF-8");
			context.response.setHeader("Pragma", "public");
			context.response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
			context.response.setDateHeader("Expires", 0);
			context.response.setHeader("Content-Disposition","attachment; filename=paylist.pdf");			
			ServletOutputStream o = context.response.getOutputStream();
			baos.writeTo(o); 
			o.flush();				
			o.close();			
		} catch (Exception e) {
			e.printStackTrace();	
			LogPrint.getLogStackTrace(e, logger);
		}
		
	}	
	
	// make a PdfPCell ,for insert into pdf.
	private PdfPCell makeCell(String content, int align, Font FontDefault) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		return objCell;
	}
	/** 创建 无边框 单元格 */
	private PdfPCell makeCellWithNoBorder(String content, int align, Font FontDefault) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setBorder(0);
		return objCell;
	}
	/** 创建 有边框 合并 单元格 */
	private PdfPCell makeCellSetColspan(String content, int align, Font FontDefault,int colspan) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setColspan(colspan);
		
		return objCell;
	}
	/** 创建 无边框 合并 单元格 */
	private PdfPCell makeCellSetColspanWithNoBorder(String content, int align, Font FontDefault,int colspan) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setBorder(0);
		objCell.setColspan(colspan);
		return objCell;
	}
	
	
	/**
	 * 支付表详细
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void paylistDetail (Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		Map schema = null;
		Map paylist = null;

		//Add by Michael 2012 01/29 
		Map creditMap=null;
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {

				// 
				schema = copySchema(schema, context.contextMap);
				//
				/*
				List<Map> rePaylineList = new ArrayList<Map>();
				
				String[] periodNums = HTMLUtil.getParameterValues(context.request, "PERIOD_NUM", "0");
				String[] monthPrices = HTMLUtil.getParameterValues(context.request, "MONTH_PRICE", "0");
				
				for (int i=0;i<(periodNums==null?0:periodNums.length);i++) {
					Map line = new HashMap();
					line.put("PERIOD_NUM", periodNums[i]);
					line.put("MONTH_PRICE", monthPrices[i]);
					rePaylineList.add(line);
				}
				//
				paylist = StartPayService.createCreditPaylist(schema,rePaylineList);
				//
				outputMap.put("paylist", paylist);
				*/
				
				//Add by Michael 2012 01/29 在方案里增加合同类型
				context.contextMap.put("data_type", "客户来源");
				creditMap = (Map) DataAccessor.query("creditReportManage.selectCreditBaseInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);					
				schema.put("CONTRACT_TYPE", String.valueOf(creditMap.get("CONTRACT_TYPE")));	
				
				List<Map> rePaylineList = StartPayService.upPackagePaylines(context);
				//
				paylist = StartPayService.createCreditPaylistIRR(schema,rePaylineList, StartPayService.keepPackagePayline(context));
				outputMap.put("paylist", paylist);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		
		}
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/credit_vip/showPaylineDetail.jsp");
		} else {
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
	
	}
	
	public void testPVSprice(Context context){
		List<Map<String, Object>> resultList = null;
	       List<Map<String, Object>> listForCsv = new ArrayList<Map<String,Object>>();
	       BufferedWriter writer = null;
	       try {
	           resultList = (List<Map<String, Object>>) DataAccessor.query("yangYunTest.getAllPassed", null, DataAccessor.RS_TYPE.LIST);
	           File file = new File("D:/test/YangYunTest.csv");
	           if (!file.exists()) {
	              file.createNewFile();
	           }
	           for (Map<String, Object> map : resultList) {
	              map.put("inter", InterestMarginUtil.getInterestMargin(String.valueOf(map.get("ID"))));
	              listForCsv.add(map);
	           }
	           StringBuffer sb = new StringBuffer("");
	           sb.append("CREDIT_ID");
	           sb.append(",");
	           sb.append("合同号");
	           sb.append(",");
	           sb.append("客户");
	           sb.append(",");
	           sb.append("办事处");
	           sb.append(",");
	           sb.append("客户经理");
	           sb.append(",");
	           sb.append("主管");
	           sb.append(",");
	           sb.append("核准日期");
	           sb.append(",");
	           sb.append("新利差");
	           sb.append("\n");
	           for (Map<String, Object> map : listForCsv) {
	              sb.append(map.get("ID"));
	              sb.append(",");
	              sb.append(map.get("LEASE_CODE"));
	              sb.append(",");
	              sb.append(map.get("CUST_NAME"));
	              sb.append(",");
	              sb.append(map.get("DECP_NAME_CN"));
	              sb.append(",");
	              sb.append(map.get("USERNAME"));
	              sb.append(",");
	              sb.append(map.get("UPPER_USER"));
	              sb.append(",");
	              sb.append(map.get("WIND_RESULT_DATE"));
	              sb.append(",");
	              sb.append(map.get("inter"));
	              sb.append("\n");
	           }
	           writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
	           writer.write(sb.toString());
	       } catch (Exception e) {
	           // TODO Auto-generated catch block
	           e.printStackTrace();
	       } finally {
	           try {
	              writer.flush();
	              writer.close();
	           } catch (IOException e) {
	              // TODO Auto-generated catch block
	              e.printStackTrace();
	           }
	       }

	}
	
	public void insertPVSprice(Context context){
		List<Map<String, Object>> resultList = null;
	       List<Map<String, Object>> listForCsv = new ArrayList<Map<String,Object>>();
	       SqlMapClient sqlMapper = DataAccessor.getSession() ;
	       try {
	           resultList = (List<Map<String, Object>>) DataAccessor.query("yangYunTest.getAllPassed", null, DataAccessor.RS_TYPE.LIST);

	           for (Map<String, Object> map : resultList) {
	              map.put("inter", InterestMarginUtil.getInterestMargin(String.valueOf(map.get("ID"))));
	              listForCsv.add(map);
	           }
	           Map paramMap=null;
	       		sqlMapper.startBatch() ;
	       		for (Map<String, Object> map : listForCsv) {
	    			paramMap = new HashMap();
	    			paramMap.put("CREDIT_ID", map.get("ID"));
	    			paramMap.put("LEASE_CODE", map.get("LEASE_CODE"));
	    			paramMap.put("RATE_DIFF", map.get("inter"));
	    			sqlMapper.insert("yangYunTest.insertIntoPVPrice", paramMap);
	    		}
	    		sqlMapper.executeBatch() ;

	       } catch (Exception e) {
	           // TODO Auto-generated catch block
	           e.printStackTrace();
	       } 

	}
	
	
	//Add by Michael 2012 2/2 填充补齐 净本金余额 等栏位信息	
	public void testCreateNetFinancePaylist(Context context){
		try {
			List<Map> payList = (List<Map>) DataAccessor.query("creditReportManage.getAllPaylistRecpID", null, DataAccessor.RS_TYPE.LIST);
           for (Map map : payList) {
        	   context.contextMap.put("credit_id", map.get("CREDIT_ID"));
        	   context.contextMap.put("RECP_ID", map.get("RECP_ID"));
        	   createNetFinancePaylist(context);
           }
           
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//Add by Michael 2012 2/2 填充补齐 净本金余额 等栏位信息
	@SuppressWarnings("unchecked")
	public void createNetFinancePaylist (Context context) {		
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		Map creditMap = null;
		Map schema = null;
		Map paylist = null;
		Map memoMap = null;
		
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {				
				// credit_id 
				context.contextMap.put("data_type", "客户来源");
				creditMap = (Map) DataAccessor.query("creditReportManage.selectCreditBaseInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("creditMap", creditMap);
				// 
				//查询方案
				schema = (Map) DataAccessor.query("creditReportManage.selectCreditScheme",context.contextMap, DataAccessor.RS_TYPE.MAP);
				// 查询应付租金列表
				List<Map> irrMonthPaylines = StartPayService.queryPackagePayline(context.contextMap.get("credit_id"), Integer.valueOf(1));
				//Add by Michael 2012 1/5 For 方案的查询
				outputMap.put("irrMonthPaylines", irrMonthPaylines);
				
				// 解压irrMonthPaylines到每一期的钱
				List<Map> rePaylineList = StartPayService.upPackagePaylines(irrMonthPaylines);

				if (schema != null) {
					//Add by Michael 2012 01/29 在方案里增加合同类型
					schema.put("CONTRACT_TYPE", String.valueOf(creditMap.get("CONTRACT_TYPE")));
					
					//add by Michael 把管理费收入总和传过来，计算营业税收入，会影响TR计算----------------------
					double totalFeeSet=0.0d;
					
					if("2".equals(schema.get("TAX_PLAN_CODE"))){
						List<Map> listTotalFeeSet=(List) DataAccessor.query("creditReportManage.getTotalFeeByRectID",context.contextMap, DataAccessor.RS_TYPE.LIST);
						for(Map map:listTotalFeeSet){
							totalFeeSet+=new BigDecimal(DataUtil.doubleUtil(map.get("FEE"))/1.06).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
						}	
					}else if("1".equals(schema.get("TAX_PLAN_CODE"))||"3".equals(schema.get("TAX_PLAN_CODE"))){
						totalFeeSet=(Double)DataAccessor.query("creditReportManage.sumTotalFeeByRectID",context.contextMap, DataAccessor.RS_TYPE.OBJECT);
					}
					
					schema.put("FEESET_TOTAL", totalFeeSet);
					//-----------------------------------------------------------------------------
					
					schema.put("TOTAL_PRICE", schema.get("LEASE_TOPRIC"));
					schema.put("LEASE_PERIOD", schema.get("LEASE_TERM"));
					schema.put("LEASE_TERM", schema.get("LEASE_COURSE"));
					// 
					if (irrMonthPaylines.size() > 0) {
						// 如果应付租金存在，则以应付租金的方式计算
						paylist = StartPayService.createCreditPaylistIRR(schema,rePaylineList,irrMonthPaylines);
					} else {
						// 如果应付租金不存在，则以年利率(合同利率)的方式计算
						paylist = StartPayService.createCreditPaylist(schema,new ArrayList<Map>());
					}
				}
				//
				paylist.put("RECP_ID", context.contextMap.get("RECP_ID"));
				paylist.put("S_EMPLOYEEID", context.contextMap.get("s_employeeId"));
				operatePayline(paylist);
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("报告管理--现场调查报告租金测算错误!请联系管理员");
			}
		
		}
	}
	//Add by Michael 2012 2/2 填充补齐 净本金余额 等栏位信息	
	public static void operatePayline(Map paylist) throws SQLException, ParseException {		
		List<Map> paylines = (List<Map>) paylist.get("paylines");		
		HashMap map = new HashMap();
		map.put("RECP_ID", paylist.get("RECP_ID"));
		map.put("S_EMPLOYEEID", paylist.get("S_EMPLOYEEID"));	
		DataAccessor.getSession().startBatch();
		
		for (Map payline : paylines) {
			map.put("LOCKED", payline.get("LOCKED"));
			map.put("PERIOD_NUM", payline.get("PERIOD_NUM"));
			//map.put("PAY_DATE", DataUtil.dateUtil(payline.get("PAY_DATE"), "yyyy-MM-dd")); 
			map.put("MONTH_PRICE", payline.get("MONTH_PRICE"));
			map.put("OWN_PRICE", payline.get("OWN_PRICE"));
			map.put("REN_PRICE", payline.get("REN_PRICE"));
			map.put("LAST_PRICE", payline.get("LAST_PRICE"));
			map.put("DEPOSIT_PRICE", payline.get("DEPOSIT_PRICE"));
			map.put("IRR_PRICE", payline.get("IRR_PRICE"));
			map.put("IRR_MONTH_PRICE", payline.get("IRR_MONTH_PRICE"));
			map.put("SALES_TAX", payline.get("SALES_TAX"));
			map.put("INSURE_PRICE", payline.get("INSURE_PRICE"));
			map.put("REAL_OWN_PRICE", payline.get("REAL_OWN_PRICE"));
			//Add by Michael 2012 01/16 当期本金 当期利息 当期本金余额----------------------------------------
			map.put("NETCURRENTFINANCE", payline.get("NETCURRENTFINANCE"));
			map.put("CURRENTRENPRICE", payline.get("CURRENTRENPRICE"));
			map.put("NETFINANCE", payline.get("NETFINANCE"));  //前期净本金余额	
			map.put("CURRENTFINANCECOSTREN", payline.get("CURRENTFINANCECOSTREN"));  //当期资金成本息	
			map.put("PV_PRICE", payline.get("PV_PRICE"));
			
			DataAccessor.getSession().insert("collectionManage.createPaylinesTemp", map);
		}    	
		DataAccessor.getSession().executeBatch();
	
	}
	
	@SuppressWarnings("unchecked")
	public void exportPaylistByValueAdded(Context context) {
		ByteArrayOutputStream baos = null;
		try {
			// 字体设置
			BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
			Font FontColumn = new Font(bfChinese, 12, Font.BOLD);
			Font FontColumnSmall = new Font(bfChinese, 10, Font.BOLD);
			Font FontDefault = new Font(bfChinese, 12, Font.NORMAL);
			Font fa = new Font(bfChinese, 15, Font.BOLD);
			// 数字格式
			NumberFormat nfFSNum = new DecimalFormat("###,###,###,###.00");
			nfFSNum.setGroupingUsed(true);
			nfFSNum.setMaximumFractionDigits(2);
			// 页面设置
			Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
			Document document = new Document(rectPageSize, 5, 5, 20, 0); // 其余4个参数，设置了页面的4个边距
			baos = new ByteArrayOutputStream();
			PdfWriter.getInstance(document, baos);
			//打开文档
			document.open();
			
			Map creditMap = null;
			Map schema = null;
			Map paylist = null;
			List<Map> payWays = null;
			
			Map dataDictionaryMap = new HashMap();
			dataDictionaryMap.put("dataType", "支付方式");
			payWays = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
					
			// credit_id 
			context.contextMap.put("data_type", "客户来源");
			creditMap = (Map) DataAccessor.query("creditReportManage.selectCreditBaseInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);
			// 
			context.contextMap.put("PAY_TAX_PLAN_CODE", "2");
			//---------------从页面取值--------------
			schema = copySchema(schema, context.contextMap);

			//Add by Michael 2012 01/29 在方案里增加合同类型
			schema.put("CONTRACT_TYPE", String.valueOf(creditMap.get("CONTRACT_TYPE")));	
			//
			List<Map> rePaylineList = StartPayService.upPackagePaylines(context);
			//以下是原来的数据源
			//以上是原来的数据源0516胡昭卿注释
			//新的数据源
			
			if(context.contextMap.get("hu_rentcontractexportcredit")!=null){
				if(Boolean.parseBoolean(context.contextMap.get("hu_rentcontractexportcredit").toString())){
				paylist = (Map)context.contextMap.get("paylist");
				paylist.put("oldirrMonthPaylines", paylist.get("irrMonthPaylines"));
				paylist.put("irrMonthPaylines", paylist.get("irrMonthPaylines"));
				}
			}else{
			paylist = StartPayService.createCreditPaylistIRR(schema,rePaylineList,StartPayService.keepPackagePayline(context));
			paylist.put("oldirrMonthPaylines", paylist.get("irrMonthPaylines"));
			paylist.put("irrMonthPaylines", StartPayService.keepPackagePayline(context));
			StartPayService.packagePaylinesForMon(paylist);
			}
			
			//Add by Michael 2012 01/14 For 方案费用查询 影响概算成本为1 不影响为0
			List feeListRZE=null;
			feeListRZE = (List) DataAccessor.query("creditReportManage.getCreditFeeListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
			context.contextMap.put("feeListRZE", feeListRZE);
			List feeList=null;
			feeList = (List) DataAccessor.query("creditReportManage.getCreditFeeList",context.contextMap, DataAccessor.RS_TYPE.LIST);
			context.contextMap.put("feeList", feeList);				
			//---------------从数据库取值--------------
			
		    List<Map> paylines =null;
		    paylines = (List<Map>) paylist.get("paylines");
		    		
		    PdfPTable tT = new PdfPTable(1);
		    tT.setWidthPercentage(100f);
		    tT.addCell(makeCellWithNoBorder("测算表", PdfPCell.ALIGN_CENTER, fa));
		    tT.addCell(makeCellWithNoBorder("", PdfPCell.ALIGN_CENTER, fa));
		    tT.addCell(makeCellWithNoBorder("", PdfPCell.ALIGN_CENTER, fa));
		    document.add(tT);
		    
		    PdfPTable tT2 = new PdfPTable(2);
		    tT2.setWidthPercentage(100f);
		    tT2.addCell(makeCell("承租人姓名："+creditMap.get("CUST_NAME").toString(), PdfPCell.ALIGN_LEFT, FontDefault));
		    tT2.addCell(makeCell("客户经理："+creditMap.get("SENSOR_NAME").toString(), PdfPCell.ALIGN_LEFT, FontDefault));
		    tT2.addCell(makeCellWithNoBorder("", PdfPCell.ALIGN_LEFT, FontDefault));
		    tT2.addCell(makeCellWithNoBorder("", PdfPCell.ALIGN_LEFT, FontDefault));
		    document.add(tT2);
		    
		    PdfPTable t3 = new PdfPTable(4);
			t3.setWidthPercentage(100);
			t3.addCell(makeCellSetColspan("融资租赁方案", PdfPCell.ALIGN_LEFT, FontColumn, 4));
			t3.addCell(makeCell("融资租赁总价值", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell(NumberUtils.formatdigital(DataUtil.doubleUtil(paylist.get("LEASE_TOPRIC"))), PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("概算成本（RZE）", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell(NumberUtils.formatdigital(DataUtil.doubleUtil(paylist.get("LEASE_RZE"))), PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("租赁期数", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell(DataUtil.StringUtil(paylist.get("LEASE_PERIOD")), PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("租赁周期", PdfPCell.ALIGN_LEFT, FontDefault));
			String leaseterm = null;
			if(DataUtil.intUtil(paylist.get("LEASE_TERM"))==1){
				leaseterm = "月份";
			}else if(DataUtil.intUtil(paylist.get("LEASE_TERM"))==3){
				leaseterm = "季度";
			}else if(DataUtil.intUtil(paylist.get("LEASE_TERM"))==6){
				leaseterm = "半年";
			}else if(DataUtil.intUtil(paylist.get("LEASE_TERM"))==12){
				leaseterm = "年度";
			}
			t3.addCell(makeCell(leaseterm, PdfPCell.ALIGN_LEFT, FontDefault));
		
			t3.addCell(makeCell("首期未税租金", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell(NumberUtils.retain3rounded(DataUtil.doubleUtil(paylist.get("HEAD_HIRE_PERCENT")==null?0:paylist.get("HEAD_HIRE_PERCENT")))+"%    "+NumberUtils.formatdigital(DataUtil.doubleUtil(paylist.get("HEAD_HIRE")==null?0:paylist.get("HEAD_HIRE"))), PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("保证金", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell(NumberUtils.retain3rounded(DataUtil.doubleUtil(paylist.get("PLEDGE_PRICE_RATE")))+"%    "+NumberUtils.formatdigital(DataUtil.doubleUtil(paylist.get("PLEDGE_PRICE"))), PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("", PdfPCell.ALIGN_LEFT, FontDefault));
			
			String PLEDGE_AVE_PRICE = "0";
			if(paylist.get("PLEDGE_AVE_PRICE")!=null){
				PLEDGE_AVE_PRICE =NumberUtils.formatdigital(DataUtil.doubleUtil(paylist.get("PLEDGE_AVE_PRICE")));
			}
			String PLEDGE_BACK_PRICE = "0";
			if(paylist.get("PLEDGE_BACK_PRICE")!=null){
				PLEDGE_BACK_PRICE =NumberUtils.formatdigital(DataUtil.doubleUtil(paylist.get("PLEDGE_BACK_PRICE")));
			}
			String PLEDGE_LAST_PRICE = "0";
			if(paylist.get("PLEDGE_LAST_PRICE")!=null){
				PLEDGE_LAST_PRICE =NumberUtils.formatdigital(DataUtil.doubleUtil(paylist.get("PLEDGE_LAST_PRICE"))+DataUtil.doubleUtil(paylist.get("PLEDGE_LAST_PRICE_TAX")));
			}
			String PLEDGE_LAST_PERIOD = "0";
			if(paylist.get("PLEDGE_LAST_PERIOD")!=null){
				PLEDGE_LAST_PERIOD =DataUtil.StringUtil(paylist.get("PLEDGE_LAST_PERIOD"));
			}
			String PLEDGE_PERIOD = "0";
			if(paylist.get("PLEDGE_PERIOD")!=null){
				PLEDGE_PERIOD =DataUtil.StringUtil(paylist.get("PLEDGE_PERIOD"));
			}
			t3.addCell(makeCell("用于平均抵冲金额", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell(PLEDGE_AVE_PRICE, PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("用于期末退还金额", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell(PLEDGE_BACK_PRICE, PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("用于最后抵冲含税金额/期数", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell(PLEDGE_LAST_PRICE+"   "+PLEDGE_LAST_PERIOD, PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("收入时间", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("第"+PLEDGE_PERIOD+"期", PdfPCell.ALIGN_LEFT, FontDefault));			
			t3.addCell(makeCell("保证金入账", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCellSetColspan("入我司 "+NumberUtils.retain3rounded(DataUtil.doubleUtil(paylist.get("PLEDGE_ENTER_CMPRICE")==null?0:paylist.get("PLEDGE_ENTER_CMPRICE")))+" 税金 "+NumberUtils.retain3rounded(DataUtil.doubleUtil(paylist.get("PLEDGE_ENTER_CMRATE")==null?0:paylist.get("PLEDGE_ENTER_CMRATE")))+" 我司入供应商 "+NumberUtils.retain3rounded(DataUtil.doubleUtil(context.contextMap.get("PAY_PLEDGE_ENTER_MCTOAG")==null?0:context.contextMap.get("PAY_PLEDGE_ENTER_MCTOAG")))+" 税金 "+NumberUtils.retain3rounded(DataUtil.doubleUtil(context.contextMap.get("PAY_PLEDGE_ENTER_MCTOAGRATE")==null?0:context.contextMap.get("PAY_PLEDGE_ENTER_MCTOAGRATE")))+"\n 入供应商 "+NumberUtils.retain3rounded(DataUtil.doubleUtil(paylist.get("PLEDGE_ENTER_AG")==null?0:paylist.get("PLEDGE_ENTER_AG")))+" 税金 "+NumberUtils.retain3rounded(DataUtil.doubleUtil(context.contextMap.get("PAY_PLEDGE_ENTER_AGRATE")==null?0:context.contextMap.get("PAY_PLEDGE_ENTER_AGRATE"))), PdfPCell.ALIGN_LEFT, FontDefault, 3));
			
			t3.addCell(makeCellSetColspan("管理费收入", PdfPCell.ALIGN_LEFT, FontColumn, 4));
	
			for (int i = 0; i < feeListRZE.size(); i++) {
				Map map = (Map) feeListRZE.get(i);
				t3.addCell(makeCell(map.get("CREATE_SHOW_NAME")+"", PdfPCell.ALIGN_LEFT, FontDefault));
				t3.addCell(makeCell(NumberUtils.formatdigital(DataUtil.doubleUtil(map.get("FEE"))), PdfPCell.ALIGN_LEFT, FontDefault));
				t3.addCell(makeCell("", PdfPCell.ALIGN_LEFT, FontDefault));
				t3.addCell(makeCell("", PdfPCell.ALIGN_LEFT, FontDefault));
			}	
			
			t3.addCell(makeCellSetColspan("非管理费收入", PdfPCell.ALIGN_LEFT, FontColumn, 4));
			for (int i = 0; i < feeList.size(); i++) {
				Map map = (Map) feeList.get(i);
				t3.addCell(makeCell(map.get("CREATE_SHOW_NAME")+"", PdfPCell.ALIGN_LEFT, FontDefault));
				t3.addCell(makeCell(NumberUtils.formatdigital(DataUtil.doubleUtil(map.get("FEE"))), PdfPCell.ALIGN_LEFT, FontDefault));
				t3.addCell(makeCell("", PdfPCell.ALIGN_LEFT, FontDefault));
				t3.addCell(makeCell("", PdfPCell.ALIGN_LEFT, FontDefault));
			}	
			
			t3.addCell(makeCell("支付方式", PdfPCell.ALIGN_LEFT, FontDefault));
			String payway = null;
			for(int x=0;x<payWays.size();x++){
				if(DataUtil.intUtil(payWays.get(x).get("CODE"))==DataUtil.intUtil(paylist.get("PAY_WAY"))){
					payway = DataUtil.StringUtil(payWays.get(x).get("FLAG"));
				}
			}
			t3.addCell(makeCell(payway, PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("预估起租日", PdfPCell.ALIGN_LEFT, FontDefault));

			if(paylist.get("START_DATE")!=null && !paylist.get("START_DATE").equals("")){
				t3.addCell(makeCell(DataUtil.dateToStringUtil(paylist.get("START_DATE"), "yyyy-MM-dd"), PdfPCell.ALIGN_LEFT, FontDefault));
			}else{
				t3.addCell(makeCell("", PdfPCell.ALIGN_LEFT, FontDefault));					
			}					
			t3.addCell(makeCell("客户TR", PdfPCell.ALIGN_LEFT, FontDefault));
			//Modify by Michael 2012 1/5  schemaMap
			//t3.addCell(makeCell(NumberUtils.retain3rounded(DataUtil.doubleUtil(paylist.get("TR_RATE")))+"%", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell(NumberUtils.retain3rounded(DataUtil.doubleUtil(schema.get("TR_RATE")))+"%", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("实际TR", PdfPCell.ALIGN_LEFT, FontDefault));
			//t3.addCell(makeCell(NumberUtils.retain3rounded(DataUtil.doubleUtil(paylist.get("TR_IRR_RATE")))+"%", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell(NumberUtils.retain3rounded(DataUtil.doubleUtil(schema.get("TR_IRR_RATE")))+"%", PdfPCell.ALIGN_LEFT, FontDefault));
			double RATE_DIFF=0d;
			t3.addCell(makeCell("利差", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell(NumberUtils.retain3rounded(DataUtil.doubleUtil(schema.get("RATE_DIFF")))+"", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("第一年保险费", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell(NumberUtils.formatdigital(DataUtil.doubleUtil(paylines.get(0).get("INSURE_PRICE"))), PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("印花税", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell(DataUtil.StringUtil(paylist.get("STAMP_TAX_INSUREPRIC")), PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("合同利率TRR", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell(NumberUtils.retain3rounded(DataUtil.doubleUtil(paylist.get("YEAR_INTEREST")))+"%", PdfPCell.ALIGN_LEFT, FontDefault));
			
			t3.addCell(makeCellSetColspan("融资租赁方案测算方式一", PdfPCell.ALIGN_LEFT, FontColumn, 4));
			List list = (List) paylist.get("oldirrMonthPaylines");
			for (int i = 0; i < list.size(); i++) {
				Map map = (Map) list.get(i);
				t3.addCell(makeCell("未税应付租金", PdfPCell.ALIGN_LEFT, FontDefault));
				t3.addCell(makeCell(NumberUtils.formatdigital(DataUtil.doubleUtil(map.get("IRR_MONTH_PRICE"))), PdfPCell.ALIGN_LEFT, FontDefault));
				t3.addCell(makeCell("对应期次", PdfPCell.ALIGN_LEFT, FontDefault));
				t3.addCell(makeCell("第"+map.get("IRR_MONTH_PRICE_START")+"期到第"+map.get("IRR_MONTH_PRICE_END"), PdfPCell.ALIGN_LEFT, FontDefault));
			}
			t3.addCell(makeCellSetColspan("融资租赁方案测算方式二", PdfPCell.ALIGN_LEFT, FontColumn, 4));
			t3.addCell(makeCell("", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell("合同利率", PdfPCell.ALIGN_LEFT, FontDefault));
			t3.addCell(makeCell(NumberUtils.retain3rounded(DataUtil.doubleUtil(paylist.get("YEAR_INTEREST")))+"%", PdfPCell.ALIGN_LEFT, FontDefault));
			
			document.add(t3);	
		    		    
		    float[] widthsStl = {0.05f,0.10f,0.10f,0.10f,0.10f,0.10f,0.10f,0.10f,0.10f,0.10f};			
//		    float[] widthsStl = {0.05f,0.10f,0.10f,0.10f,0.10f,0.10f,0.10f,0.10f,0.07f,0.10f};			
			PdfPTable t2 = new PdfPTable(widthsStl);
			t2.setWidthPercentage(100);
			t2.addCell(makeCellSetColspan("还款计划", PdfPCell.ALIGN_LEFT, FontColumn, 10));
			t2.addCell(makeCell("期次", PdfPCell.ALIGN_CENTER, FontColumn));
			t2.addCell(makeCell("支付时间", PdfPCell.ALIGN_CENTER, FontColumn));
			t2.addCell(makeCell("含税应收租金", PdfPCell.ALIGN_CENTER, FontColumn));
			t2.addCell(makeCell("平均增值税", PdfPCell.ALIGN_CENTER, FontColumn));
			t2.addCell(makeCell("未税应收租金", PdfPCell.ALIGN_CENTER, FontColumn));
			t2.addCell(makeCell("合同预期租金", PdfPCell.ALIGN_CENTER, FontColumn));
			t2.addCell(makeCell("本金", PdfPCell.ALIGN_CENTER, FontColumn));
			t2.addCell(makeCell("利息", PdfPCell.ALIGN_CENTER, FontColumn));
			t2.addCell(makeCell("剩余本金", PdfPCell.ALIGN_CENTER, FontColumn));
			t2.addCell(makeCell("实际增值税", PdfPCell.ALIGN_CENTER, FontColumn));
			double irrmonthpriceall = 0d;
			double monthpriceall = 0d;
			double ownpriceall = 0d;
			double lastpriceall = 0d;
			double irrmonthpriTaxceall = 0d;
			double incomeall = 0d;
			double valueAddedceall=0d;
			double valueAddedTrueceall=0d;
			
//			double pledge_ave_price = DataUtil.doubleUtil(paylist.get("PLEDGE_AVE_PRICE"));
			double realownpriceall = 0d;
			for(int x=0;x<paylines.size();x++){
				t2.addCell(makeCell(DataUtil.StringUtil(paylines.get(x).get("PERIOD_NUM")), PdfPCell.ALIGN_CENTER, FontColumnSmall));
				t2.addCell(makeCell(DataUtil.StringUtil(DataUtil.dateToStringUtil(paylines.get(x).get("PAY_DATE"), "yyyy-MM-dd")), PdfPCell.ALIGN_CENTER, FontColumnSmall));
				double irrmonthpriceTax = DataUtil.doubleUtil(paylines.get(x).get("IRR_MONTH_PRICE"))+DataUtil.doubleUtil(paylines.get(x).get("VALUE_ADDED_TAX"));
				double irrmonthprice = DataUtil.doubleUtil(paylines.get(x).get("IRR_MONTH_PRICE"));
				double valueAdded = DataUtil.doubleUtil(paylines.get(x).get("VALUE_ADDED_TAX"));
				double monthprice = DataUtil.doubleUtil(paylines.get(x).get("MONTH_PRICE"));
				double ownprice = DataUtil.doubleUtil(paylines.get(x).get("OWN_PRICE"));
				double lastprice = DataUtil.doubleUtil(paylines.get(x).get("LAST_PRICE"));
				double renprice = DataUtil.doubleUtil(paylines.get(x).get("REN_PRICE"));
				double real_own_price = DataUtil.doubleUtil(paylines.get(x).get("REAL_OWN_PRICE"));
				double valueAddedTrue = DataUtil.doubleUtil(paylines.get(x).get("VALUE_ADDED_TAX_TRUE"));
//				double realownprice = lastprice - pledge_ave_price;
				t2.addCell(makeCell(NumberUtils.formatdigital(irrmonthpriceTax), PdfPCell.ALIGN_RIGHT, FontColumnSmall));
				t2.addCell(makeCell(NumberUtils.formatdigital(valueAdded), PdfPCell.ALIGN_RIGHT, FontColumnSmall));
				t2.addCell(makeCell(NumberUtils.formatdigital(irrmonthprice), PdfPCell.ALIGN_RIGHT, FontColumnSmall));
				t2.addCell(makeCell(NumberUtils.formatdigital(monthprice), PdfPCell.ALIGN_RIGHT, FontColumnSmall));
				t2.addCell(makeCell(NumberUtils.formatdigital(ownprice), PdfPCell.ALIGN_RIGHT, FontColumnSmall));
				t2.addCell(makeCell(NumberUtils.formatdigital(renprice), PdfPCell.ALIGN_RIGHT, FontColumnSmall));
				t2.addCell(makeCell(NumberUtils.formatdigital(lastprice), PdfPCell.ALIGN_RIGHT, FontColumnSmall));
				t2.addCell(makeCell(NumberUtils.formatdigital(valueAddedTrue), PdfPCell.ALIGN_RIGHT, FontColumnSmall));
				
				irrmonthpriTaxceall=irrmonthpriceTax+irrmonthpriTaxceall;
				valueAddedceall=valueAdded+valueAddedceall;
				irrmonthpriceall = irrmonthprice + irrmonthpriceall;
				monthpriceall = monthprice + monthpriceall;
				ownpriceall = ownprice + ownpriceall;
				lastpriceall = renprice + lastpriceall;
				valueAddedTrueceall=valueAddedTrue+valueAddedTrueceall;
				
			}
			
			t2.addCell(makeCellSetColspan("合计", PdfPCell.ALIGN_CENTER, FontColumnSmall,2));
			t2.addCell(makeCell(NumberUtils.formatdigital(irrmonthpriTaxceall), PdfPCell.ALIGN_RIGHT, FontColumnSmall));
			t2.addCell(makeCell(NumberUtils.formatdigital(valueAddedceall), PdfPCell.ALIGN_RIGHT, FontColumnSmall));
			t2.addCell(makeCell(NumberUtils.formatdigital(irrmonthpriceall), PdfPCell.ALIGN_RIGHT, FontColumnSmall));
			t2.addCell(makeCell(NumberUtils.formatdigital(monthpriceall), PdfPCell.ALIGN_RIGHT, FontColumnSmall));
			t2.addCell(makeCell(NumberUtils.formatdigital(ownpriceall), PdfPCell.ALIGN_RIGHT, FontColumnSmall));
			t2.addCell(makeCell(NumberUtils.formatdigital(lastpriceall), PdfPCell.ALIGN_RIGHT, FontColumnSmall));
			t2.addCell(makeCell("", PdfPCell.ALIGN_RIGHT, FontColumnSmall));
			t2.addCell(makeCell(NumberUtils.formatdigital(valueAddedTrueceall), PdfPCell.ALIGN_RIGHT, FontColumnSmall));
				
			document.add(t2);
		
			document.close();
			context.response.setContentType("application/pdf");
			context.response.setCharacterEncoding("UTF-8");
			context.response.setHeader("Pragma", "public");
			context.response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
			context.response.setDateHeader("Expires", 0);
			context.response.setHeader("Content-Disposition","attachment; filename=paylist.pdf");			
			ServletOutputStream o = context.response.getOutputStream();
			baos.writeTo(o); 
			o.flush();				
			o.close();			
		} catch (Exception e) {
			e.printStackTrace();	
			LogPrint.getLogStackTrace(e, logger);
		}
		
	}	
	
	
}
