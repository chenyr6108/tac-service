package com.brick.quotation.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.collection.service.StartPayService;
import com.brick.collection.support.PayRate;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.Constants;
import com.brick.util.DataUtil;
import com.brick.util.web.HTMLUtil;
import com.ibatis.sqlmap.client.SqlMapClient;
public class Quotation extends AService {
	Log logger = LogFactory.getLog(Quotation.class);

	public void initQuotation(Context context) {
		List errList = context.errList;
		Map outputMap = context.contextMap;
		if (errList.isEmpty()) {
			try {
				List feeListRZE = null;
				feeListRZE = (List) DataAccessor.query(
						"creditReportManage.getCreditFeeListRZE",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("feeListRZE", feeListRZE);
				List feeList = null;
				feeList = (List) DataAccessor.query(
						"creditReportManage.getCreditFeeList",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("feeList", feeList);

				List feeSetListRZE = null;
				feeSetListRZE = (List) DataAccessor.query(
						"creditReportManage.getFeeSetListRZE",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("feeSetListRZE", feeSetListRZE);
				List feeSetList = null;
				feeSetList = (List) DataAccessor.query(
						"creditReportManage.getFeeSetList", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
				outputMap.put("feeSetList", feeSetList);
				// -------------------------------------------------------------------
				Map baseRate = PayRate.getBaseRate();
				logger.error(baseRate.get("SIX_MONTHS").toString()+"====================================================");
				outputMap.put("baseRateJson", Output.serializer.serialize(baseRate));
				
				outputMap.put("payWays", DictionaryUtil.getDictionary("支付方式"));
				if("4".equals(context.contextMap.get("taxPlanCode"))) {
					List result=new ArrayList();
					Map re=new HashMap();
					re.put("CODE",4);
					re.put("FLAG","直接租赁税费方案");
					result.add(re);
					outputMap.put("taxPlanList", result);
				} else if(Constants.TAX_PLAN_CODE_6.equals(context.contextMap.get("taxPlanCode"))) {
					List result=new ArrayList();
					Map re=new HashMap();
					re.put("CODE",6);
					re.put("FLAG","设备售后回租方案");
					result.add(re);
					outputMap.put("taxPlanList", result);
				} else if(Constants.TAX_PLAN_CODE_7.equals(context.contextMap.get("taxPlanCode"))) {
					List result=new ArrayList();
					Map re=new HashMap();
					re.put("CODE",7);
					re.put("FLAG","乘用车售后回租方案");
					result.add(re);
					outputMap.put("taxPlanList", result);
				} else if(Constants.TAX_PLAN_CODE_8.equals(context.contextMap.get("taxPlanCode"))) {
					List result=new ArrayList();
					Map re=new HashMap();
					re.put("CODE",8);
					re.put("FLAG","商用车售后回租方案");
					result.add(re);
					outputMap.put("taxPlanList", result);
				} else {
					List result=DictionaryUtil.getDictionary("税费方案");
					for(int i=0;i<result.size();i++) {
						if("4".equals(((Map)result.get(i)).get("CODE"))||Constants.TAX_PLAN_CODE_6.equals(context.contextMap.get("taxPlanCode"))||Constants.TAX_PLAN_CODE_7.equals(context.contextMap.get("taxPlanCode"))||Constants.TAX_PLAN_CODE_8.equals(context.contextMap.get("taxPlanCode"))) {
							result.remove(i);
							break;
						}
					}
					outputMap.put("taxPlanList", result);
				}
				outputMap.put("disabled", "disabled");
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("报价单管理--报价单管理!请联系管理员");
			}
		}
		if (errList.isEmpty()) {
			if("4".equals(context.contextMap.get("taxPlanCode"))||
					Constants.TAX_PLAN_CODE_6.equals(context.contextMap.get("taxPlanCode"))||Constants.TAX_PLAN_CODE_7.equals(context.contextMap.get("taxPlanCode"))||Constants.TAX_PLAN_CODE_8.equals(context.contextMap.get("taxPlanCode"))) {
				outputMap.put("init","Y");
				Output.jspOutput(outputMap, context, "/quotation/directQotation.jsp");
			} else if("5".equals(context.contextMap.get("taxPlanCode"))) {
				outputMap.put("init","Y");
				Output.jspOutput(outputMap, context, "/quotation/motorQotation.jsp");
			} else {
				Output.jspOutput(outputMap, context, "/quotation/quotation.jsp");
			}
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	//重车报价单
	public void initCarQuotation(Context context) {
		List errList = context.errList;
		Map outputMap = context.contextMap;
		if (errList.isEmpty()) {
			try {
				List feeListRZE = null;
				feeListRZE = (List) DataAccessor.query(
						"creditReportManage.getCreditFeeListRZE",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("feeListRZE", feeListRZE);
				List feeList = null;
				feeList = (List) DataAccessor.query(
						"creditReportManage.getCreditFeeList",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("feeList", feeList);

				List feeSetListRZE = null;
				feeSetListRZE = (List) DataAccessor.query(
						"creditReportManage.getFeeSetListRZE",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("feeSetListRZE", feeSetListRZE);
				List feeSetList = null;
				feeSetList = (List) DataAccessor.query(
						"creditReportManage.getFeeSetList", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
				outputMap.put("feeSetList", feeSetList);
				// -------------------------------------------------------------------
				Map baseRate = PayRate.getBaseRate();
				logger.error(baseRate.get("SIX_MONTHS").toString()+"====================================================");
				outputMap.put("baseRateJson", Output.serializer.serialize(baseRate));
				
				List result=DictionaryUtil.getDictionary("税费方案");
				for(int i=0;i<result.size();i++) {
					if("4".equals(((Map)result.get(i)).get("CODE"))) {
						result.remove(i);
						break;
					}
				}
				outputMap.put("taxPlanList", result);
				outputMap.put("payWays", DictionaryUtil.getDictionary("支付方式"));
				outputMap.put("disabled", "disabled");
				outputMap.put("contractType",context.contextMap.get("contractType"));
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("报价单管理--报价单管理!请联系管理员");
			}
		}
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/carQuotation/quotation.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	
	@SuppressWarnings("unchecked")
	public void calculateQuotationPaylistIRR (Context context) {

		Map outputMap = new HashMap();
		List errList = context.errList;
		Map schema = null;
		Map paylist = null;
		List<Map> feeSetList=null;
		List<Map> feeSetListRZE=null;
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {

				schema = copySchema(schema, context.contextMap);
				//
				List<Map> rePaylineList = StartPayService.upPackagePaylines(context);

	/*			schema.put("CONTRACT_TYPE", String.valueOf("1"));*/
				if(schema!=null
						&&(Constants.TAX_PLAN_CODE_4.equals(schema.get("TAX_PLAN_CODE"))
								||Constants.TAX_PLAN_CODE_6.equals(schema.get("TAX_PLAN_CODE"))
								||Constants.TAX_PLAN_CODE_7.equals(schema.get("TAX_PLAN_CODE"))
								||Constants.TAX_PLAN_CODE_8.equals(schema.get("TAX_PLAN_CODE")))) {
					schema.put("payList",rePaylineList);
					schema.put("PLEDGE_AVE_PRICE",schema.get("PLEDGE_AVE_PRICE")==null||"".equals(schema.get("PLEDGE_AVE_PRICE"))?0:schema.get("PLEDGE_AVE_PRICE"));
					schema.put("PLEDGE_BACK_PRICE",schema.get("PLEDGE_BACK_PRICE")==null||"".equals(schema.get("PLEDGE_BACK_PRICE"))?"0":schema.get("PLEDGE_BACK_PRICE"));
					schema.put("MAGR_FEE",schema.get("MANAGEMENT_FEE")==null||"".equals(schema.get("MANAGEMENT_FEE"))?0:schema.get("MANAGEMENT_FEE"));
					schema.put("PLEDGE_LAST_PERIOD",schema.get("PLEDGE_LAST_PERIOD")==null||"".equals(schema.get("PLEDGE_LAST_PERIOD"))?0:schema.get("PLEDGE_LAST_PERIOD"));
				}
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
						}else if("1".equals(schema.get("TAX_PLAN_CODE"))||"3".equals(schema.get("TAX_PLAN_CODE"))||Constants.TAX_PLAN_CODE_4.equals(schema.get("TAX_PLAN_CODE"))
								||Constants.TAX_PLAN_CODE_6.equals(schema.get("TAX_PLAN_CODE"))
								||Constants.TAX_PLAN_CODE_7.equals(schema.get("TAX_PLAN_CODE"))
								||Constants.TAX_PLAN_CODE_8.equals(schema.get("TAX_PLAN_CODE"))){
							totalFeeSet += DataUtil.doubleUtil(context.contextMap.get(where.get("CREATE_FILED_NAME")));
						}
					}										
					schema.put("FEESET_TOTAL", totalFeeSet);
				} catch (Exception e) {
					e.printStackTrace();
				}
				//-----------------------------------------------------------------------------
				
				paylist = StartPayService.createCreditPaylistIRR(schema,rePaylineList, StartPayService.keepPackagePayline(context));
				paylist.put("STAYBUY_PRICE",schema.get("STAYBUY_PRICE"));
				paylist.put("PLEDGE_AVG_PRICE",schema.get("PLEDGE_AVG_PRICE"));
				paylist.put("IS_TAX",schema.get("IS_TAX"));
				paylist.put("SCHEME_ID",String.valueOf(schema.get("SCHEME_ID")));
				paylist.put("CUST_NAME",schema.get("CUST_NAME"));
				paylist.put("TAX_PLAN_CODE",schema.get("TAX_PLAN_CODE"));
				paylist.put("LEASE_ALLPRIC",schema.get("LEASE_ALLPRIC"));
				outputMap.put("paylist", paylist);

				List<Map> feeListRZE = (List<Map>) DataAccessor.query("creditReportManage.getCreditFeeListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
				List<Map> feeList = (List<Map>) DataAccessor.query("creditReportManage.getCreditFeeList",context.contextMap, DataAccessor.RS_TYPE.LIST);
				for (Map tempMap : feeListRZE) {
					tempMap.put("FEE", context.contextMap.get(tempMap.get("CREATE_FILED_NAME")));
				}

				for (Map tempMap : feeList) {
					tempMap.put("FEE", context.contextMap.get(tempMap.get("CREATE_FILED_NAME")));
				}
				outputMap.put("feeListRZE", feeListRZE);
				outputMap.put("feeList", feeList);	
				
				feeSetListRZE = (List<Map>) DataAccessor.query("creditReportManage.getFeeSetListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
				feeSetList = (List<Map>) DataAccessor.query("creditReportManage.getFeeSetList",context.contextMap, DataAccessor.RS_TYPE.LIST);
				
				for (Map tempMap : feeSetListRZE) {
					tempMap.put("FEE", context.contextMap.get(tempMap.get("CREATE_FILED_NAME")));
				}

				for (Map tempMap : feeSetList) {
					tempMap.put("FEE", context.contextMap.get(tempMap.get("CREATE_FILED_NAME")));
				}
				outputMap.put("feeSetListRZE", feeSetListRZE);
				outputMap.put("feeSetList", feeSetList);
				//-------------------------------------------------------------------	
				
				List<Map> equipmentsList=new ArrayList<Map>();
				String[] TYPE_NAME = context.request
						.getParameterValues("TYPE_NAME");
				String[] KIND_NAME = context.request
						.getParameterValues("KIND_NAME");
				String[] AMOUNT = context.request
						.getParameterValues("AMOUNT");
				String[] SUPL_NAME= context.request
						.getParameterValues("SUPL_NAME");
				Map map;
				for (int i = 0; i < TYPE_NAME.length; i++) {
						map = new HashMap();
						map.put("TYPE_NAME", HTMLUtil.parseStrParam2(
								TYPE_NAME[i], "0"));
						map.put("KIND_NAME", HTMLUtil.parseStrParam2(
								KIND_NAME[i], ""));
						map.put("AMOUNT", HTMLUtil.parseStrParam2(
								AMOUNT[i], ""));
						map.put("SUPL_NAME", HTMLUtil.parseStrParam2(
								SUPL_NAME[i], ""));
						equipmentsList.add(map);
				}
				outputMap.put("equipmentsList", equipmentsList);
				Map baseRate = PayRate.getBaseRate();
				outputMap.put("baseRateJson", Output.serializer.serialize(baseRate));
				//
				outputMap.put("payWays", DictionaryUtil.getDictionary("支付方式"));
				
				List result=DictionaryUtil.getDictionary("税费方案");
				for(int i=0;i<result.size();i++) {
					if("4".equals(((Map)result.get(i)).get("CODE"))) {
						result.remove(i);
						break;
					}
				}
				outputMap.put("taxPlanList", result);
				
				List irrMonthTaxPaylines= new ArrayList();
				String[] irrMonthPirces = HTMLUtil.getParameterValues(context.request, "PAY_IRR_MONTH_PRICE", "0");
				String[] startNums = HTMLUtil.getParameterValues(context.request, "PAY_IRR_MONTH_PRICE_START", "0");
				String[] endNums = HTMLUtil.getParameterValues(context.request, "PAY_IRR_MONTH_PRICE_END", "0");
				Map paramMap;
				for (int i=0; i<irrMonthPirces.length; i++) {
					paramMap = new HashMap();
					paramMap.put("IRR_MONTH_PRICE", DataUtil.doubleUtil(irrMonthPirces[i]));
					paramMap.put("IRR_MONTH_PRICE_START", startNums[i]);
					paramMap.put("IRR_MONTH_PRICE_END", endNums[i]);
					irrMonthTaxPaylines.add(paramMap);
				}
				paylist.put("irrMonthPaylines", irrMonthTaxPaylines);
				packagePaylinesForValueAddedForCalculate(paylist);
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("报价管理--报价-根据【应收租金】测算支付表错误!请联系管理员");
			}
		
		}
		
		List<Map> equipmentsList=new ArrayList<Map>();
		String[] TYPE_NAME = context.request
				.getParameterValues("TYPE_NAME");
		String[] KIND_NAME = context.request
				.getParameterValues("KIND_NAME");
		String[] AMOUNT = context.request
				.getParameterValues("AMOUNT");
		String[] SUPL_NAME= context.request
				.getParameterValues("SUPL_NAME");
		Map map;
		for (int i = 0; i < TYPE_NAME.length; i++) {
				map = new HashMap();
				map.put("TYPE_NAME", HTMLUtil.parseStrParam2(
						TYPE_NAME[i], "0"));
				map.put("KIND_NAME", HTMLUtil.parseStrParam2(
						KIND_NAME[i], ""));
				map.put("AMOUNT", HTMLUtil.parseStrParam2(
						AMOUNT[i], ""));
				map.put("SUPL_NAME", HTMLUtil.parseStrParam2(
						SUPL_NAME[i], ""));
				equipmentsList.add(map);
		}
		outputMap.put("equipmentsList", equipmentsList);
		Map baseRate = PayRate.getBaseRate();
		outputMap.put("baseRateJson", Output.serializer.serialize(baseRate));
		
		try {
			outputMap.put("payWays", DictionaryUtil.getDictionary("支付方式"));
		} catch (Exception e1) {
		}
		
		SqlMapClient sqlMapper = null;
		Map where=null;
		
		outputMap.put("REMARK",context.contextMap.get("REMARK"));
		outputMap.put("SCHEME_ID",context.contextMap.get("SCHEME_ID"));
		try {
			if("Y".equals(context.contextMap.get("needSave"))) {
				
				sqlMapper = DataAccessor.getSession();
				sqlMapper.startTransaction();
				schema.put("SCHEME_ID",context.contextMap.get("SCHEME_ID"));
				sqlMapper.delete("quotation.deleteQuotationScheme",
						schema);
				sqlMapper.delete("quotation.deleteQuotationSchemeIrr",
						schema);
				sqlMapper.delete("quotation.deleteQuotationFee",
						schema);
				sqlMapper.delete("quotation.deleteQuotationEquipment",
						schema);
				sqlMapper.delete("quotation.deleteQuotationOwnprice",
						schema);
				
				Map param=new HashMap();
				schema.put("s_employeeId",context.contextMap.get("s_employeeId"));
				schema.put("WEISHUI", context.contextMap.get("weishui"));
				schema.put("HANSHUI", context.contextMap.get("hanshui"));
				schema.put("CHA", context.contextMap.get("cha"));
				schema.put("JINXIANG", context.contextMap.get("jinxiang"));
				schema.put("TOTALRENPRICE",context.contextMap.get("total_price"));
				schema.put("REN_PRICE",context.contextMap.get("REN_PRICE"));
				schema.put("pay__money",context.contextMap.get("pay__money"));
				schema.put("incomePay",context.contextMap.get("incomePay"));
				schema.put("outPay",context.contextMap.get("outPay"));
				long scheme_id=(Long)sqlMapper.insert("quotation.createQuotationScheme1",schema);
				for (int i = 0; i < TYPE_NAME.length; i++) {
					map = new HashMap();
					map.put("TYPE_NAME", HTMLUtil.parseStrParam2(
							TYPE_NAME[i], "0"));
					map.put("KIND_NAME", HTMLUtil.parseStrParam2(
							KIND_NAME[i], ""));
					map.put("AMOUNT", HTMLUtil.parseStrParam2(
							AMOUNT[i], ""));
					map.put("s_employeeId", context.request
							.getSession().getAttribute("s_employeeId"));
					map.put("SCHEME_ID", scheme_id);
					map.put("SUPL_NAME", HTMLUtil.parseStrParam2(
							SUPL_NAME[i], ""));
					sqlMapper.insert("quotation.createQuotationEquipment",map);
				}
				
				String[] irrMonthPrice =  context.request
						.getParameterValues("PAY_IRR_MONTH_PRICE");
				String[] irrMonthPriceStart = context.request
						.getParameterValues("PAY_IRR_MONTH_PRICE_START");
				String[] irrMonthPriceEnd = context.request
						.getParameterValues("PAY_IRR_MONTH_PRICE_END");
				
				for (int i=0; i<irrMonthPrice.length; i++) {
					Map paramMap = new HashMap();
					paramMap.put("IRR_MONTH_PRICE", DataUtil.doubleUtil(irrMonthPrice[i]));
					paramMap.put("IRR_MONTH_PRICE_START", irrMonthPriceStart[i]);
					paramMap.put("IRR_MONTH_PRICE_END", irrMonthPriceEnd[i]);
					paramMap.put("S_EMPLOYEEID", context.contextMap.get("s_employeeId"));
					paramMap.put("SCHEME_ID", scheme_id);
					paramMap.put("REN_RATE",0);
					sqlMapper.insert("quotation.createQuotationSchemaIrr", paramMap);
				}
				
				for (int i = 0; i < feeSetListRZE.size(); i++) {
					Map tempMap = new HashMap();
					where = (Map) feeSetListRZE.get(i);
					tempMap.put("FEE_SET_ID", where.get("ID"));
					tempMap.put("CREATE_SHOW_NAME", where.get("CREATE_SHOW_NAME"));
					tempMap.put("IS_LEASERZE_COST", where.get("IS_LEASERZE_COST"));
					tempMap.put("CREATE_FILED_NAME", where.get("CREATE_FILED_NAME")) ;
					tempMap.put("CREATE_ID", context.contextMap.get("s_employeeId"));
					tempMap.put("SCHEME_ID", scheme_id);
					tempMap.put("FEE", DataUtil.doubleUtil(context.contextMap.get(where.get("CREATE_FILED_NAME"))));
					sqlMapper.insert("quotation.insertQuotationFeeList", tempMap);
				}
				
				for (int i = 0; i < feeSetList.size(); i++) {
					Map tempMap = new HashMap();
					where = (Map) feeSetList.get(i);
					tempMap.put("FEE_SET_ID", where.get("ID"));
					tempMap.put("CREATE_SHOW_NAME", where.get("CREATE_SHOW_NAME"));
					tempMap.put("IS_LEASERZE_COST", where.get("IS_LEASERZE_COST"));
					tempMap.put("CREATE_FILED_NAME", where.get("CREATE_FILED_NAME")) ;
					tempMap.put("CREATE_ID", context.contextMap.get("s_employeeId"));
					tempMap.put("SCHEME_ID", scheme_id);
					tempMap.put("FEE", DataUtil.doubleUtil(context.contextMap.get(where.get("CREATE_FILED_NAME"))));
					sqlMapper.insert("quotation.insertQuotationFeeList", tempMap);
				}
				
				for (Map payline : (List<Map>) paylist.get("paylines")) {
					Map ownPriceMap = new HashMap();
					ownPriceMap.put("SCHEME_ID", scheme_id);
					ownPriceMap.put("REN_PRICE", DataUtil.doubleUtil(payline.get("REN_PRICE")));
					ownPriceMap.put("OWN_PRICE", DataUtil.doubleUtil(payline.get("OWN_PRICE")));
					ownPriceMap.put("LAST_PRICE", DataUtil.doubleUtil(payline.get("LAST_PRICE")));
					ownPriceMap.put("MONTH_PRICE", DataUtil.doubleUtil(payline.get("MONTH_PRICE")));
					ownPriceMap.put("VALUE_ADDED_TAX", DataUtil.doubleUtil(payline.get("VALUE_ADDED_TAX")));
					ownPriceMap.put("PERIOD_NUM", payline.get("PERIOD_NUM"));
					sqlMapper.insert("quotation.insertQuotationOwnprice", ownPriceMap);
				}
				
				context.contextMap.remove("needSave");
				sqlMapper.commitTransaction();
				outputMap.put("SCHEME_ID",scheme_id);
			}
		} catch(Exception e) {
			try {
				sqlMapper.endTransaction();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		if (errList.isEmpty()) {
			if ("4".equals(schema.get("CONTRACT_TYPE"))||"6".equals(schema.get("CONTRACT_TYPE"))){
				context.contextMap.put("contractType",schema.get("CONTRACT_TYPE"));
				outputMap.put("contractType",schema.get("CONTRACT_TYPE"));
				Output.jspOutput(outputMap, context, "/carQuotation/quotation.jsp");
			} else if("8".equals(schema.get("CONTRACT_TYPE"))) {
				outputMap.put("pay__money",context.contextMap.get("pay__money"));
				outputMap.put("incomePay",context.contextMap.get("incomePay"));
				outputMap.put("outPay",context.contextMap.get("outPay"));
				Output.jspOutput(outputMap, context, "/quotation/motorQotation.jsp");
			} else{
				Output.jspOutput(outputMap, context, "/quotation/quotation.jsp");
			}
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
	}
	
	public void calculateDirectQuotationPaylistIRR(Context context) throws Exception {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map schema = null;
		Map paylist = null;
		
		schema = copySchema(schema, context.contextMap);
		outputMap.put("REMARK",context.contextMap.get("REMARK"));
		List<Map> rePaylineList = StartPayService.upPackagePaylines(context);
		
		if(schema!=null&&Constants.TAX_PLAN_CODE_4.equals(schema.get("TAX_PLAN_CODE"))) {
			schema.put("payList",rePaylineList);
			schema.put("PLEDGE_AVE_PRICE",schema.get("PLEDGE_AVE_PRICE")==null||"".equals(schema.get("PLEDGE_AVE_PRICE"))?0:schema.get("PLEDGE_AVE_PRICE"));
			schema.put("PLEDGE_BACK_PRICE",schema.get("PLEDGE_BACK_PRICE")==null||"".equals(schema.get("PLEDGE_BACK_PRICE"))?"0":schema.get("PLEDGE_BACK_PRICE"));
			schema.put("MAGR_FEE",schema.get("MANAGEMENT_FEE")==null||"".equals(schema.get("MANAGEMENT_FEE"))?0:schema.get("MANAGEMENT_FEE"));
			schema.put("PLEDGE_LAST_PERIOD",schema.get("PLEDGE_LAST_PERIOD")==null||"".equals(schema.get("PLEDGE_LAST_PERIOD"))?0:schema.get("PLEDGE_LAST_PERIOD"));
		} else if(schema!=null&&(Constants.TAX_PLAN_CODE_6.equals(schema.get("TAX_PLAN_CODE"))||Constants.TAX_PLAN_CODE_7.equals(schema.get("TAX_PLAN_CODE"))||Constants.TAX_PLAN_CODE_8.equals(schema.get("TAX_PLAN_CODE")))) {
			schema.put("payList",rePaylineList);
			schema.put("PLEDGE_AVE_PRICE",schema.get("PLEDGE_AVE_PRICE")==null||"".equals(schema.get("PLEDGE_AVE_PRICE"))?0:schema.get("PLEDGE_AVE_PRICE"));
			schema.put("PLEDGE_BACK_PRICE",schema.get("PLEDGE_BACK_PRICE")==null||"".equals(schema.get("PLEDGE_BACK_PRICE"))?"0":schema.get("PLEDGE_BACK_PRICE"));
			schema.put("MAGR_FEE",schema.get("MANAGEMENT_FEE")==null||"".equals(schema.get("MANAGEMENT_FEE"))?0:schema.get("MANAGEMENT_FEE"));
			schema.put("PLEDGE_LAST_PERIOD",schema.get("PLEDGE_LAST_PERIOD")==null||"".equals(schema.get("PLEDGE_LAST_PERIOD"))?0:schema.get("PLEDGE_LAST_PERIOD"));
			if(Constants.TAX_PLAN_CODE_7.equals(schema.get("TAX_PLAN_CODE"))) {
				schema.put("SALES_PAY",context.contextMap.get("pay__money"));
				outputMap.put("SALES_PAY",context.contextMap.get("pay__money"));
			}
		}
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
				}else if("1".equals(schema.get("TAX_PLAN_CODE"))||"3".equals(schema.get("TAX_PLAN_CODE"))||Constants.TAX_PLAN_CODE_4.equals(schema.get("TAX_PLAN_CODE"))
						||Constants.TAX_PLAN_CODE_6.equals(schema.get("TAX_PLAN_CODE"))||Constants.TAX_PLAN_CODE_7.equals(schema.get("TAX_PLAN_CODE"))||Constants.TAX_PLAN_CODE_8.equals(schema.get("TAX_PLAN_CODE"))){
					totalFeeSet += DataUtil.doubleUtil(context.contextMap.get(where.get("CREATE_FILED_NAME")));
				}
			}										
			schema.put("FEESET_TOTAL", totalFeeSet);
		} catch (Exception e) {
			
		}
		
		paylist = StartPayService.createCreditPaylistIRR(schema,rePaylineList, StartPayService.keepPackagePayline(context));
		paylist.put("STAYBUY_PRICE",schema.get("STAYBUY_PRICE"));
		paylist.put("PLEDGE_AVG_PRICE",schema.get("PLEDGE_AVG_PRICE"));
		paylist.put("IS_TAX",schema.get("IS_TAX"));
		paylist.put("SCHEME_ID",String.valueOf(schema.get("SCHEME_ID")));
		paylist.put("CUST_NAME",schema.get("CUST_NAME"));
		paylist.put("TAX_PLAN_CODE",schema.get("TAX_PLAN_CODE"));
		paylist.put("LEASE_ALLPRIC",schema.get("LEASE_ALLPRIC"));
		outputMap.put("paylist", paylist);

		List<Map> feeListRZE = (List<Map>) DataAccessor.query("creditReportManage.getCreditFeeListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
		List<Map> feeList = (List<Map>) DataAccessor.query("creditReportManage.getCreditFeeList",context.contextMap, DataAccessor.RS_TYPE.LIST);
		for (Map tempMap : feeListRZE) {
			tempMap.put("FEE", context.contextMap.get(tempMap.get("CREATE_FILED_NAME")));
		}

		for (Map tempMap : feeList) {
			tempMap.put("FEE", context.contextMap.get(tempMap.get("CREATE_FILED_NAME")));
		}
		outputMap.put("feeListRZE", feeListRZE);
		outputMap.put("feeList", feeList);	
		
		List<Map> feeSetListRZE = (List<Map>) DataAccessor.query("creditReportManage.getFeeSetListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
		List<Map> feeSetList = (List<Map>) DataAccessor.query("creditReportManage.getFeeSetList",context.contextMap, DataAccessor.RS_TYPE.LIST);
		
		for (Map tempMap : feeSetListRZE) {
			tempMap.put("FEE", context.contextMap.get(tempMap.get("CREATE_FILED_NAME")));
		}

		for (Map tempMap : feeSetList) {
			tempMap.put("FEE", context.contextMap.get(tempMap.get("CREATE_FILED_NAME")));
		}
		outputMap.put("feeSetListRZE", feeSetListRZE);
		outputMap.put("feeSetList", feeSetList);
		//-------------------------------------------------------------------	
		
		List<Map> equipmentsList=new ArrayList<Map>();
		String[] TYPE_NAME = context.request
				.getParameterValues("TYPE_NAME");
		String[] KIND_NAME = context.request
				.getParameterValues("KIND_NAME");
		String[] AMOUNT = context.request
				.getParameterValues("AMOUNT");
		String[] SUPL_NAME= context.request
				.getParameterValues("SUPL_NAME");
		Map map;
		for (int i = 0; i < TYPE_NAME.length; i++) {
				map = new HashMap();
				map.put("TYPE_NAME", HTMLUtil.parseStrParam2(
						TYPE_NAME[i], "0"));
				map.put("KIND_NAME", HTMLUtil.parseStrParam2(
						KIND_NAME[i], ""));
				map.put("AMOUNT", HTMLUtil.parseStrParam2(
						AMOUNT[i], ""));
				map.put("SUPL_NAME", HTMLUtil.parseStrParam2(
						SUPL_NAME[i], ""));
				equipmentsList.add(map);
		}
		outputMap.put("equipmentsList", equipmentsList);
		Map baseRate = PayRate.getBaseRate();
		outputMap.put("baseRateJson", Output.serializer.serialize(baseRate));
		
		outputMap.put("payWays", DictionaryUtil.getDictionary("支付方式"));
		
		List result=new ArrayList();
		Map res=new HashMap();
		if(Constants.TAX_PLAN_CODE_4.equals(schema.get("TAX_PLAN_CODE"))) {
			res.put("CODE",4);
			res.put("FLAG","直接租赁税费方案");
			result.add(res);
		} else if(Constants.TAX_PLAN_CODE_6.equals(schema.get("TAX_PLAN_CODE"))) {
			res.put("CODE",6);
			res.put("FLAG","设备售后回租方案");
			result.add(res);
		} else if(Constants.TAX_PLAN_CODE_7.equals(schema.get("TAX_PLAN_CODE"))) {
			res.put("CODE",7);
			res.put("FLAG","乘用车售后回租方案");
			result.add(res);
		} else if(Constants.TAX_PLAN_CODE_8.equals(schema.get("TAX_PLAN_CODE"))) {
			res.put("CODE",8);
			res.put("FLAG","商用车售后回租方案");
			result.add(res);
		}
		
		outputMap.put("taxPlanList", result);
		
		SqlMapClient sqlMapper = null;
		try {
			if("Y".equals(context.contextMap.get("needSave"))) {
				
				sqlMapper = DataAccessor.getSession();
				sqlMapper.startTransaction();
				schema.put("SCHEME_ID",context.contextMap.get("SCHEME_ID"));
				sqlMapper.delete("quotation.deleteQuotationScheme",
						schema);
				sqlMapper.delete("quotation.deleteQuotationSchemeIrr",
						schema);
				sqlMapper.delete("quotation.deleteQuotationFee",
						schema);
				sqlMapper.delete("quotation.deleteQuotationEquipment",
						schema);
				sqlMapper.delete("quotation.deleteQuotationOwnprice",
						schema);
				
				Map param=new HashMap();
				schema.put("s_employeeId",context.contextMap.get("s_employeeId"));
				schema.put("WEISHUI", context.contextMap.get("weishui"));
				schema.put("HANSHUI", context.contextMap.get("hanshui"));
				schema.put("CHA", context.contextMap.get("cha"));
				schema.put("JINXIANG", context.contextMap.get("jinxiang"));
				schema.put("TOTALRENPRICE",context.contextMap.get("total_price"));
				schema.put("REN_PRICE",context.contextMap.get("total_ren_price"));
				schema.put("pay__money",context.contextMap.get("pay__money"));
				long scheme_id=(Long)sqlMapper.insert("quotation.createQuotationScheme1",schema);
				for (int i = 0; i < TYPE_NAME.length; i++) {
					map = new HashMap();
					map.put("TYPE_NAME", HTMLUtil.parseStrParam2(
							TYPE_NAME[i], "0"));
					map.put("KIND_NAME", HTMLUtil.parseStrParam2(
							KIND_NAME[i], ""));
					map.put("AMOUNT", HTMLUtil.parseStrParam2(
							AMOUNT[i], ""));
					map.put("s_employeeId", context.request
							.getSession().getAttribute("s_employeeId"));
					map.put("SCHEME_ID", scheme_id);
					map.put("SUPL_NAME", HTMLUtil.parseStrParam2(
							SUPL_NAME[i], ""));
					sqlMapper.insert("quotation.createQuotationEquipment",map);
				}
				
				String[] irrMonthPrice =  context.request
						.getParameterValues("PAY_IRR_MONTH_PRICE");
				String[] irrMonthPriceStart = context.request
						.getParameterValues("PAY_IRR_MONTH_PRICE_START");
				String[] irrMonthPriceEnd = context.request
						.getParameterValues("PAY_IRR_MONTH_PRICE_END");
				
				for (int i=0; i<irrMonthPrice.length; i++) {
					Map paramMap = new HashMap();
					paramMap.put("IRR_MONTH_PRICE", DataUtil.doubleUtil(irrMonthPrice[i]));
					paramMap.put("IRR_MONTH_PRICE_START", irrMonthPriceStart[i]);
					paramMap.put("IRR_MONTH_PRICE_END", irrMonthPriceEnd[i]);
					paramMap.put("S_EMPLOYEEID", context.contextMap.get("s_employeeId"));
					paramMap.put("SCHEME_ID", scheme_id);
					paramMap.put("REN_RATE",0);
					sqlMapper.insert("quotation.createQuotationSchemaIrr", paramMap);
				}
				
				for (int i = 0; i < feeSetListRZE.size(); i++) {
					Map tempMap = new HashMap();
					where = (Map) feeSetListRZE.get(i);
					tempMap.put("FEE_SET_ID", where.get("ID"));
					tempMap.put("CREATE_SHOW_NAME", where.get("CREATE_SHOW_NAME"));
					tempMap.put("IS_LEASERZE_COST", where.get("IS_LEASERZE_COST"));
					tempMap.put("CREATE_FILED_NAME", where.get("CREATE_FILED_NAME")) ;
					tempMap.put("CREATE_ID", context.contextMap.get("s_employeeId"));
					tempMap.put("SCHEME_ID", scheme_id);
					tempMap.put("FEE", DataUtil.doubleUtil(context.contextMap.get(where.get("CREATE_FILED_NAME"))));
					sqlMapper.insert("quotation.insertQuotationFeeList", tempMap);
				}
				
				for (int i = 0; i < feeSetList.size(); i++) {
					Map tempMap = new HashMap();
					where = (Map) feeSetList.get(i);
					tempMap.put("FEE_SET_ID", where.get("ID"));
					tempMap.put("CREATE_SHOW_NAME", where.get("CREATE_SHOW_NAME"));
					tempMap.put("IS_LEASERZE_COST", where.get("IS_LEASERZE_COST"));
					tempMap.put("CREATE_FILED_NAME", where.get("CREATE_FILED_NAME")) ;
					tempMap.put("CREATE_ID", context.contextMap.get("s_employeeId"));
					tempMap.put("SCHEME_ID", scheme_id);
					tempMap.put("FEE", DataUtil.doubleUtil(context.contextMap.get(where.get("CREATE_FILED_NAME"))));
					sqlMapper.insert("quotation.insertQuotationFeeList", tempMap);
				}
				
				for (Map payline : (List<Map>) paylist.get("paylines")) {
					Map ownPriceMap = new HashMap();
					ownPriceMap.put("SCHEME_ID", scheme_id);
					ownPriceMap.put("REN_PRICE", DataUtil.doubleUtil(payline.get("REN_PRICE")));
					ownPriceMap.put("OWN_PRICE", DataUtil.doubleUtil(payline.get("OWN_PRICE")));
					ownPriceMap.put("LAST_PRICE", DataUtil.doubleUtil(payline.get("LAST_PRICE")));
					ownPriceMap.put("MONTH_PRICE", DataUtil.doubleUtil(payline.get("MONTH_PRICE")));
					ownPriceMap.put("VALUE_ADDED_TAX", DataUtil.doubleUtil(payline.get("VALUE_ADDED_TAX")));
					ownPriceMap.put("PERIOD_NUM", payline.get("PERIOD_NUM"));
					sqlMapper.insert("quotation.insertQuotationOwnprice", ownPriceMap);
				}
				
				context.contextMap.remove("needSave");
				sqlMapper.commitTransaction();
				outputMap.put("SCHEME_ID",scheme_id);
			}
		} catch(Exception e) {
			sqlMapper.endTransaction();
		}
		
		Output.jspOutput(outputMap,context,"/quotation/directQotation.jsp");
	}
	
	public static Map copySchema(Map dest, Map src) throws ParseException {
		
		if (dest == null) {
			dest = new HashMap();
		}
		
		dest.put("TOTAL_PRICE",  DataUtil.doubleUtil(src.get("PAY_LEASE_TOPRIC")));
		dest.put("LEASE_TOPRIC",  DataUtil.doubleUtil(src.get("PAY_LEASE_TOPRIC")));
		dest.put("FLOAT_RATE", DataUtil.doubleUtil(src.get("PAY_FLOAT_RATE")));
		dest.put("LEASE_RZE",  DataUtil.doubleUtil(src.get("PAY_LEASE_RZE")));
		dest.put("LEASE_PERIOD", src.get("PAY_LEASE_PERIOD"));
		dest.put("LEASE_TERM", src.get("PAY_LEASE_TERM"));
		dest.put("PLEDGE_PRICE_RATE",  DataUtil.doubleUtil(src.get("PAY_PLEDGE_PRICE_RATE")));
		dest.put("PLEDGE_PRICE", DataUtil.doubleUtil(src.get("PAY_PLEDGE_PRICE")));
		dest.put("HEAD_HIRE_PERCENT",  DataUtil.doubleUtil(src.get("PAY_HEAD_HIRE_PERCENT")));
		dest.put("HEAD_HIRE", DataUtil.doubleUtil(src.get("PAY_HEAD_HIRE")));
		dest.put("YEAR_INTEREST", DataUtil.doubleUtil(src.get("PAY_YEAR_INTEREST")));
		dest.put("PAY_WAY", src.get("PAY_PAY_WAY"));
		
		dest.put("PLEDGE_WAY", src.get("PAY_PLEDGE_WAY"));

		dest.put("PLEDGE_AVE_PRICE", DataUtil.doubleUtil(src.get("PAY_PLEDGE_AVE_PRICE")));
		dest.put("PLEDGE_BACK_PRICE", DataUtil.doubleUtil(src.get("PAY_PLEDGE_BACK_PRICE")));
		dest.put("PLEDGE_LAST_PRICE", DataUtil.doubleUtil(src.get("PAY_PLEDGE_LAST_PRICE")));
		dest.put("PLEDGE_LAST_PERIOD", src.get("PAY_PLEDGE_LAST_PERIOD"));
		dest.put("PLEDGE_PERIOD", src.get("PAY_PLEDGE_PERIOD"));
		dest.put("PLEDGE_ENTER_WAY", src.get("PAY_PLEDGE_ENTER_WAY"));
		dest.put("PLEDGE_REALPRIC", DataUtil.doubleUtil(src.get("PAY_PLEDGE_REALPRIC")));
		
		dest.put("TR_RATE", DataUtil.doubleUtil(src.get("TR_RATE")));
		dest.put("TR_IRR_RATE", DataUtil.doubleUtil(src.get("TR_IRR_RATE")));
		dest.put("RATE_DIFF", DataUtil.doubleUtil(src.get("RATE_DIFF")));
		dest.put("REN_PRICE", DataUtil.doubleUtil(src.get("REN_PRICE")));
		dest.put("SALES_TAX", DataUtil.doubleUtil(src.get("SALES_TAX")));

		dest.put("STAYBUY_PRICE",  DataUtil.doubleUtil(src.get("STAYBUY_PRICE")));
		dest.put("CUST_NAME", src.get("CUST_NAME"));
		dest.put("IS_TAX", src.get("IS_TAX"));
		dest.put("SCHEME_ID", src.get("SCHEME_ID"));
		dest.put("PLEDGE_AVG_PRICE", DataUtil.doubleUtil(src.get("PLEDGE_AVG_PRICE")));
		dest.put("CONTRACT_TYPE", src.get("CONTRACT_TYPE"));
		dest.put("TAX_PLAN_CODE", src.get("PAY_TAX_PLAN_CODE"));
		dest.put("TOTAL_VALUEADDED_TAX", src.get("TOTAL_VALUEADDED_TAX")==null||"".equals(src.get("TOTAL_VALUEADDED_TAX"))?"0":src.get("TOTAL_VALUEADDED_TAX"));
		dest.put("PLEDGE_LAST_PRICE_TAX", src.get("PLEDGE_LAST_PRICE_TAX")==null||"".equals(src.get("PLEDGE_LAST_PRICE_TAX"))?"0":src.get("PLEDGE_LAST_PRICE_TAX"));
		//Add by Michael 2013 02-01 增加延迟拨款期数
		dest.put("DEFER_PERIOD", src.get("PAY_DEFER_PERIOD"));
		dest.put("LEASE_ALLPRIC", src.get("PAY_LEASE_ALLPRIC"));
		//add by zhangbo 07 26 增加报价单备注
		dest.put("REMARK", src.get("REMARK"));
		return dest;
	}
	
	public void createScheme(Context context) {
		SqlMapClient sqlMapper = null;
		List errList = context.errList ;
		Map outputMap = new HashMap() ;
		sqlMapper = DataAccessor.getSession();
		Map schema = null;
		Map paylist = null;
		try {
			schema = copySchema(schema, context.contextMap);
			sqlMapper.startTransaction();
			List<Map> feeSetList =null;
			Map where=null;
			//
			List<Map> rePaylineList = StartPayService.upPackagePaylines(context);


			if(schema!=null
					&&(Constants.TAX_PLAN_CODE_4.equals(schema.get("TAX_PLAN_CODE"))
							||Constants.TAX_PLAN_CODE_6.equals(schema.get("TAX_PLAN_CODE"))
							||Constants.TAX_PLAN_CODE_7.equals(schema.get("TAX_PLAN_CODE"))
							||Constants.TAX_PLAN_CODE_8.equals(schema.get("TAX_PLAN_CODE")))) {
					schema.put("payList",rePaylineList);
					schema.put("PLEDGE_AVE_PRICE",schema.get("PLEDGE_AVE_PRICE")==null||"".equals(schema.get("PLEDGE_AVE_PRICE"))?0:schema.get("PLEDGE_AVE_PRICE"));
					schema.put("PLEDGE_BACK_PRICE",schema.get("PLEDGE_BACK_PRICE")==null||"".equals(schema.get("PLEDGE_BACK_PRICE"))?"0":schema.get("PLEDGE_BACK_PRICE"));
					schema.put("MAGR_FEE",schema.get("MANAGEMENT_FEE")==null||"".equals(schema.get("MANAGEMENT_FEE"))?0:schema.get("MANAGEMENT_FEE"));
					schema.put("PLEDGE_LAST_PERIOD",schema.get("PLEDGE_LAST_PERIOD")==null||"".equals(schema.get("PLEDGE_LAST_PERIOD"))?0:schema.get("PLEDGE_LAST_PERIOD"));
			}
			List feeSetLists =null;
			double totalFeeSet=0.0d;
			try {
				feeSetLists = (List) DataAccessor.query(
						"creditReportManage.getFeeSetListAllRZE", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
				for (int i = 0; i < feeSetLists.size(); i++) {
					where = (Map) feeSetLists.get(i);
					
					if("2".equals(schema.get("TAX_PLAN_CODE"))){
						totalFeeSet+=new BigDecimal(DataUtil.doubleUtil(context.contextMap.get(where.get("CREATE_FILED_NAME")))/1.06).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
					}else if("1".equals(schema.get("TAX_PLAN_CODE"))||"3".equals(schema.get("TAX_PLAN_CODE"))||Constants.TAX_PLAN_CODE_4.equals(schema.get("TAX_PLAN_CODE"))
							||Constants.TAX_PLAN_CODE_6.equals(schema.get("TAX_PLAN_CODE"))
							||Constants.TAX_PLAN_CODE_7.equals(schema.get("TAX_PLAN_CODE"))
							||Constants.TAX_PLAN_CODE_8.equals(schema.get("TAX_PLAN_CODE"))){
						totalFeeSet += DataUtil.doubleUtil(context.contextMap.get(where.get("CREATE_FILED_NAME")));
					}
				}										
				schema.put("FEESET_TOTAL", totalFeeSet);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//-----------------------------------------------------------------------------
			
			paylist = StartPayService.createCreditPaylistIRR(schema,rePaylineList, StartPayService.keepPackagePayline(context));
//			schema.put("CONTRACT_TYPE", String.valueOf("1"));
			schema.put("TR_RATE", paylist.get("TR_RATE"));
			schema.put("TR_IRR_RATE", paylist.get("TR_IRR_RATE"));
			double RATE_DIF= 0d;
			for (Map payline : (List<Map>) paylist.get("paylines")) {
				RATE_DIF = new BigDecimal(RATE_DIF).add(new BigDecimal(DataUtil.doubleUtil(payline.get("PV_PRICE")))).doubleValue() ;
			}
			RATE_DIF = new BigDecimal(RATE_DIF).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue(); 
			
			schema.put("RATE_DIFF", RATE_DIF);
			
			sqlMapper.delete("quotation.deleteQuotationScheme",
					schema);
			sqlMapper.delete("quotation.deleteQuotationSchemeIrr",
					schema);
			sqlMapper.delete("quotation.deleteQuotationFee",
					schema);
			sqlMapper.delete("quotation.deleteQuotationEquipment",
					schema);
			sqlMapper.delete("quotation.deleteQuotationOwnprice",
					schema);
			
			Map baseRate = PayRate.getBaseRate();
			outputMap.put("baseRateJson", Output.serializer.serialize(baseRate));
			//
			outputMap.put("payWays", DictionaryUtil.getDictionary("支付方式"));
			
			schema.put("s_employeeId", context.contextMap.get("s_employeeId"));
			
			String[] irrMonthPrice =  context.request
					.getParameterValues("PAY_IRR_MONTH_PRICE");
			String[] irrMonthPriceStart = context.request
					.getParameterValues("PAY_IRR_MONTH_PRICE_START");
			String[] irrMonthPriceEnd = context.request
					.getParameterValues("PAY_IRR_MONTH_PRICE_END");
			
			Double totalRenPriceDouble=0.0;
			String[] TYPE_NAME = context.request
					.getParameterValues("TYPE_NAME");
			String[] KIND_NAME = context.request
					.getParameterValues("KIND_NAME");
			String[] AMOUNT = context.request
					.getParameterValues("AMOUNT");
			String[] SUPL_NAME= context.request
					.getParameterValues("SUPL_NAME");
			for (int i=0; i<irrMonthPrice.length; i++) {
				Map paramMap = new HashMap();
				paramMap.put("IRR_MONTH_PRICE", DataUtil.doubleUtil(irrMonthPrice[i]));
				paramMap.put("IRR_MONTH_PRICE_START", irrMonthPriceStart[i]);
				paramMap.put("IRR_MONTH_PRICE_END", irrMonthPriceEnd[i]);
				totalRenPriceDouble+=DataUtil.doubleUtil(irrMonthPrice[i])*(DataUtil.intUtil(irrMonthPriceEnd[i])-DataUtil.intUtil(irrMonthPriceStart[i])+1);
			}
			schema.put("TOTALRENPRICE", totalRenPriceDouble);
			long scheme_id=(Long)sqlMapper.insert("quotation.createQuotationScheme", schema);
			context.contextMap.put("SCHEME_ID", scheme_id);
			feeSetList = (List) DataAccessor.query(
				"creditReportManage.getFeeSetListAll", context.contextMap,
				DataAccessor.RS_TYPE.LIST);
			Map tempMap;
			for (int i = 0; i < feeSetList.size(); i++) {
				tempMap = new HashMap();
				where = (Map) feeSetList.get(i);
				tempMap.put("FEE_SET_ID", where.get("ID"));
				tempMap.put("CREATE_SHOW_NAME", where.get("CREATE_SHOW_NAME"));
				tempMap.put("IS_LEASERZE_COST", where.get("IS_LEASERZE_COST"));
				tempMap.put("CREATE_FILED_NAME", where.get("CREATE_FILED_NAME")) ;
				tempMap.put("CREATE_ID", context.contextMap.get("s_employeeId"));
				tempMap.put("SCHEME_ID", context.contextMap.get("SCHEME_ID"));
				tempMap.put("FEE", DataUtil.doubleUtil(context.contextMap.get(where.get("CREATE_FILED_NAME"))));
				sqlMapper.insert("quotation.insertQuotationFeeList", tempMap);
			}	
			
			Map ownPriceMap ;
			sqlMapper.startBatch() ;
			List<Map> paylines = (List<Map>) paylist.get("paylines");
			if("3".equals(String.valueOf(schema.get("TAX_PLAN_CODE")))) {//增值税内含
				for (Map payline : paylines) {
					ownPriceMap = new HashMap();
					ownPriceMap.put("SCHEME_ID", context.contextMap.get("SCHEME_ID"));
					ownPriceMap.put("PERIOD_NUM", payline.get("PERIOD_NUM"));
					ownPriceMap.put("IRR_MONTH_PRICE", payline.get("IRR_MONTH_PRICE"));
					ownPriceMap.put("IRR_PRICE", payline.get("IRR_PRICE"));
					ownPriceMap.put("MONTH_PRICE", payline.get("MONTH_PRICE"));
					ownPriceMap.put("OWN_PRICE", payline.get("OWN_PRICE"));
					ownPriceMap.put("REN_PRICE", payline.get("REN_PRICE"));
					ownPriceMap.put("LAST_PRICE", payline.get("LAST_PRICE"));
					sqlMapper.insert("quotation.insertQuotationOwnpriceForInternal", ownPriceMap);
				}
			}  else {//增值税
				for (Map payline : paylines) {
					ownPriceMap = new HashMap();
					ownPriceMap.put("SCHEME_ID", context.contextMap.get("SCHEME_ID"));
					ownPriceMap.put("REN_PRICE", DataUtil.doubleUtil(payline.get("REN_PRICE")));
					ownPriceMap.put("OWN_PRICE", DataUtil.doubleUtil(payline.get("OWN_PRICE")));
					ownPriceMap.put("LAST_PRICE", DataUtil.doubleUtil(payline.get("LAST_PRICE")));
					ownPriceMap.put("MONTH_PRICE", DataUtil.doubleUtil(payline.get("MONTH_PRICE")));
					ownPriceMap.put("VALUE_ADDED_TAX", DataUtil.doubleUtil(payline.get("VALUE_ADDED_TAX")));
					ownPriceMap.put("PERIOD_NUM", payline.get("PERIOD_NUM"));
					sqlMapper.insert("quotation.insertQuotationOwnprice", ownPriceMap);
				}
			}
			sqlMapper.executeBatch() ;
			
			Map map;
			for (int i = 0; i < TYPE_NAME.length; i++) {
				map = new HashMap();
				map.put("TYPE_NAME", HTMLUtil.parseStrParam2(
						TYPE_NAME[i], "0"));
				map.put("KIND_NAME", HTMLUtil.parseStrParam2(
						KIND_NAME[i], ""));
				map.put("AMOUNT", HTMLUtil.parseStrParam2(
						AMOUNT[i], ""));
				map.put("s_employeeId", context.request
						.getSession().getAttribute("s_employeeId"));
				map.put("SCHEME_ID", context.contextMap.get("SCHEME_ID"));
				map.put("SUPL_NAME", HTMLUtil.parseStrParam2(
						SUPL_NAME[i], ""));
				sqlMapper.insert(
						"quotation.createQuotationEquipment",
						map);
			}
			
			sqlMapper.startBatch() ;
			for (int i=0; i<irrMonthPrice.length; i++) {
				Map paramMap = new HashMap();
				paramMap.put("IRR_MONTH_PRICE", DataUtil.doubleUtil(irrMonthPrice[i]));
				paramMap.put("IRR_MONTH_PRICE_START", irrMonthPriceStart[i]);
				paramMap.put("IRR_MONTH_PRICE_END", irrMonthPriceEnd[i]);
				paramMap.put("S_EMPLOYEEID", context.contextMap.get("s_employeeId"));
				paramMap.put("SCHEME_ID", context.contextMap.get("SCHEME_ID"));
				paramMap.put("REN_RATE", DataUtil.doubleUtil(irrMonthPrice[i])/totalRenPriceDouble*100);
				sqlMapper.insert("quotation.createQuotationSchemaIrr", paramMap);
			}
			sqlMapper.executeBatch() ;
			
			
			sqlMapper.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("报价单管理--报价单管理信息添加错误!请联系管理员") ;
		} finally {
			try {
				sqlMapper.endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}
		
		if (errList.isEmpty()) {
			if ("4".equals(schema.get("CONTRACT_TYPE"))){
				Output.jspSendRedirect(context, "defaultDispatcher?__action=quotation.queryCarQuotationManage");
			} else if("6".equals(schema.get("CONTRACT_TYPE"))) {
				Output.jspSendRedirect(context, "defaultDispatcher?__action=quotation.queryLittleCarQuotationManage");
			} else{
				Output.jspSendRedirect(context, "defaultDispatcher?__action=quotation.queryQuotationManage");
			}
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	
	public void updateScheme(Context context) {
		SqlMapClient sqlMapper = null;
		List errList = context.errList ;
		Map outputMap = new HashMap() ;
		sqlMapper = DataAccessor.getSession();
		Map schema = null;
		
		try {
			schema = copySchema(schema, context.contextMap);
			sqlMapper.startTransaction();
			List feeSetList =null;
			Map where=null;
			
			long scheme_id=(Long)sqlMapper.insert("quotation.createQuotationScheme", schema);
			context.contextMap.put("SCHEME_ID", scheme_id);
			feeSetList = (List) DataAccessor.query(
				"creditReportManage.getFeeSetListAll", context.contextMap,
				DataAccessor.RS_TYPE.LIST);
			Map tempMap;
			for (int i = 0; i < feeSetList.size(); i++) {
				tempMap = new HashMap();
				where = (Map) feeSetList.get(i);
				tempMap.put("FEE_SET_ID", where.get("ID"));
				tempMap.put("CREATE_SHOW_NAME", where.get("CREATE_SHOW_NAME"));
				tempMap.put("IS_LEASERZE_COST", where.get("IS_LEASERZE_COST"));
				tempMap.put("CREATE_FILED_NAME", where.get("CREATE_FILED_NAME")) ;
				tempMap.put("CREATE_ID", context.contextMap.get("s_employeeId"));
				tempMap.put("SCHEME_ID", context.contextMap.get("SCHEME_ID"));
				tempMap.put("FEE", DataUtil.doubleUtil(context.contextMap.get(where.get("CREATE_FILED_NAME"))));
				sqlMapper.insert("quotation.insertQuotationFeeList", tempMap);
			}	
			sqlMapper.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("报价单管理--报价单管理信息修改错误!请联系管理员") ;
		} finally {
			try {
				sqlMapper.endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}
	}

	/**
	 * 重车管理页面查询
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryCarQuotationManage (Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		DataWrap dw = null;

		Map rsMap = null;
		Map paramMap = new HashMap();
		paramMap.put("id", context.contextMap.get("s_employeeId"));
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				rsMap = (Map) DataAccessor.query("employee.getEmpInforById", paramMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("p_usernode", rsMap.get("NODE"));
				
				dw = (DataWrap) DataAccessor.query("quotation.queryCarQuotationList", context.contextMap, DataAccessor.RS_TYPE.PAGED);
				
				context.contextMap.put("contractType","4");
				outputMap.put("contractType","4");
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		
		/*-------- output --------*/
		outputMap.put("dw", dw);
		
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/carQuotation/quotationManage.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}
	
	@SuppressWarnings("unchecked")
	public void queryLittleCarQuotationManage(Context context) {
		
		List errList=context.errList;
		Map<String,Object> outputMap=new HashMap<String,Object>();
		DataWrap dw=null;

		Map rsMap=null;
		Map paramMap=new HashMap();
		paramMap.put("id",context.contextMap.get("s_employeeId"));
		/*-------- data access --------*/		
		if(errList.isEmpty()) {		
			try {
				rsMap=(Map<String,Object>)DataAccessor.query("employee.getEmpInforById",paramMap,DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("p_usernode",rsMap.get("NODE"));
				dw=(DataWrap)DataAccessor.query("quotation.queryLittleCarQuotationList",context.contextMap,DataAccessor.RS_TYPE.PAGED);
				outputMap.put("contractType","6");
			} catch(Exception e) {
				LogPrint.getLogStackTrace(e,logger);
				errList.add(e);
			}
		}
		
		/*-------- output --------*/
		outputMap.put("dw",dw);
		context.contextMap.put("contractType","6");
		outputMap.put("contractType","6");
		if(errList.isEmpty()) {
			Output.jspOutput(outputMap,context,"/carQuotation/quotationManage.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}

	}
	/**
	 * 管理页面查询
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryQuotationManage (Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		DataWrap dw = null;

		Map rsMap = null;
		Map paramMap = new HashMap();
		paramMap.put("id", context.contextMap.get("s_employeeId"));
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				rsMap = (Map) DataAccessor.query("employee.getEmpInforById", paramMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("p_usernode", rsMap.get("NODE"));
				
				if("4".equals(context.contextMap.get("taxPlanCode"))) {
					dw = (DataWrap) DataAccessor.query("quotation.queryQuotationList1", context.contextMap, DataAccessor.RS_TYPE.PAGED);
				} else if("5".equals(context.contextMap.get("taxPlanCode"))) {
					dw = (DataWrap) DataAccessor.query("quotation.queryQuotationList2", context.contextMap, DataAccessor.RS_TYPE.PAGED);
				} else if(Constants.TAX_PLAN_CODE_6.equals(context.contextMap.get("taxPlanCode"))) {
					dw = (DataWrap) DataAccessor.query("quotation.queryQuotationList3", context.contextMap, DataAccessor.RS_TYPE.PAGED);
				} else if(Constants.TAX_PLAN_CODE_7.equals(context.contextMap.get("taxPlanCode"))) {
					dw = (DataWrap) DataAccessor.query("quotation.queryQuotationList4", context.contextMap, DataAccessor.RS_TYPE.PAGED);
				} else if(Constants.TAX_PLAN_CODE_8.equals(context.contextMap.get("taxPlanCode"))) {
					dw = (DataWrap) DataAccessor.query("quotation.queryQuotationList5", context.contextMap, DataAccessor.RS_TYPE.PAGED);
				} else {
					dw = (DataWrap) DataAccessor.query("quotation.queryQuotationList", context.contextMap, DataAccessor.RS_TYPE.PAGED);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		
		/*-------- output --------*/
		outputMap.put("dw", dw);
		outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
		outputMap.put("taxPlanCode", context.contextMap.get("taxPlanCode"));
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/quotation/quotationManage.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}
	
	
	
	@SuppressWarnings("unchecked")
	public void queryQuotationMDF (Context context) {

		Map outputMap = new HashMap();
		List errList = context.errList;
		Map schema = null;
		List equipmentList=null;
		List quotationFee=null;
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {

				Map baseRate = PayRate.getBaseRate();
				outputMap.put("baseRateJson", Output.serializer.serialize(baseRate));
				//
				outputMap.put("payWays", DictionaryUtil.getDictionary("支付方式"));
				
				schema=(Map) DataAccessor.query("quotation.queryQuotationSchema",context.contextMap, DataAccessor.RS_TYPE.MAP);
				equipmentList=(List) DataAccessor.query("quotation.queryQuotationEquipment",context.contextMap, DataAccessor.RS_TYPE.LIST);
				quotationFee=(List) DataAccessor.query("quotation.queryQuotationFee",context.contextMap, DataAccessor.RS_TYPE.LIST);
				List<Map> irrMonthPaylines = (List) DataAccessor.query("quotation.queryQuotationSchemaIrr",context.contextMap,DataAccessor.RS_TYPE.LIST);
				//Add by Michael 2012 1/5 For 方案的查询
				schema.put("irrMonthPaylines", irrMonthPaylines);
				outputMap.put("schema", schema);
				outputMap.put("equipmentList", equipmentList);
				
				List feeSetLists =null;
				double totalFeeSet=0.0d;
				Map where=null;

				List<Map> feeListRZE = (List<Map>) DataAccessor.query("quotation.getCreditFeeListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
				List<Map> feeList = (List<Map>) DataAccessor.query("quotation.getCreditFeeList",context.contextMap, DataAccessor.RS_TYPE.LIST);

				outputMap.put("feeListRZE", feeListRZE);
				outputMap.put("feeList", feeList);	
				
				List<Map> feeSetListRZE = (List<Map>) DataAccessor.query("quotation.getFeeSetListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
				List<Map> feeSetList = (List<Map>) DataAccessor.query("quotation.getFeeSetList",context.contextMap, DataAccessor.RS_TYPE.LIST);
				
				outputMap.put("feeSetListRZE", feeSetListRZE);
				outputMap.put("feeSetList", feeSetList);
				//-------------------------------------------------------------------	
				outputMap.put("disabled", "disabled");
				outputMap.put("taxPlanList", DictionaryUtil.getDictionary("税费方案"));
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("报价管理--报价单修改!请联系管理员");
			}
		
		}
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/quotation/quotationMDF.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public void queryQuotationShow (Context context) {

		Map outputMap = new HashMap();
		List errList = context.errList;
		Map schema = null;
		List equipmentList=null;
		List quotationFee=null;
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {

				Map baseRate = PayRate.getBaseRate();
				outputMap.put("baseRateJson", Output.serializer.serialize(baseRate));
				//
				outputMap.put("payWays", DictionaryUtil.getDictionary("支付方式"));
				
				schema=(Map) DataAccessor.query("quotation.queryQuotationSchema",context.contextMap, DataAccessor.RS_TYPE.MAP);
				equipmentList=(List) DataAccessor.query("quotation.queryQuotationEquipment",context.contextMap, DataAccessor.RS_TYPE.LIST);
				quotationFee=(List) DataAccessor.query("quotation.queryQuotationFee",context.contextMap, DataAccessor.RS_TYPE.LIST);
				List<Map> irrMonthPaylines = (List) DataAccessor.query("quotation.queryQuotationSchemaIrr",context.contextMap,DataAccessor.RS_TYPE.LIST);
				//Add by Michael 2012 1/5 For 方案的查询
				schema.put("irrMonthPaylines", irrMonthPaylines);
				outputMap.put("schema", schema);
				outputMap.put("equipmentList", equipmentList);
				packagePaylinesForValueAddedForCalculate(schema);
				List feeSetLists =null;
				double totalFeeSet=0.0d;
				Map where=null;

				List<Map> feeListRZE = (List<Map>) DataAccessor.query("quotation.getCreditFeeListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
				List<Map> feeList = (List<Map>) DataAccessor.query("quotation.getCreditFeeList",context.contextMap, DataAccessor.RS_TYPE.LIST);

				outputMap.put("feeListRZE", feeListRZE);
				outputMap.put("feeList", feeList);	
				
				List<Map> feeSetListRZE = (List<Map>) DataAccessor.query("quotation.getFeeSetListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
				List<Map> feeSetList = (List<Map>) DataAccessor.query("quotation.getFeeSetList",context.contextMap, DataAccessor.RS_TYPE.LIST);
				
				outputMap.put("feeSetListRZE", feeSetListRZE);
				outputMap.put("feeSetList", feeSetList);
				//-------------------------------------------------------------------	
				outputMap.put("taxPlanList", DictionaryUtil.getDictionary("税费方案"));
				outputMap.put("disabled", "disabled");
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("报价管理--报价单修改!请联系管理员");
			}
		
		}
		if (errList.isEmpty()) {
			if("4".equals(context.contextMap.get("taxPlanCode"))||Constants.TAX_PLAN_CODE_6.equals(context.contextMap.get("taxPlanCode"))
					||Constants.TAX_PLAN_CODE_7.equals(context.contextMap.get("taxPlanCode"))
					||Constants.TAX_PLAN_CODE_8.equals(context.contextMap.get("taxPlanCode"))) {
				Output.jspOutput(outputMap, context, "/quotation/directQotationShow.jsp");
			} else if("5".equals(context.contextMap.get("taxPlanCode"))) {
				outputMap.put("incomePay", schema.get("INCOME_PAY"));
				outputMap.put("outPay", schema.get("OUT_PAY"));
				Output.jspOutput(outputMap, context, "/quotation/motorQotationShow.jsp");
			} else {
				Output.jspOutput(outputMap, context, "/quotation/quotationShow.jsp");
			}
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
	}
	
	
	public static Map<String,Object> exportQuotationMDF (String ID) {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map context= new HashMap();
		List schema = null;
		List equipmentList=null;
		List<Map> quotationFee=null;
		context.put("ID", ID);
		/*-------- data access --------*/		
			try {
	
				Map baseRate = PayRate.getBaseRate();
				//
				context.put("payway", "支付方式");
				schema=(List) DataAccessor.query("quotation.queryQuotationForExport",context, DataAccessor.RS_TYPE.LIST);
				equipmentList=(List) DataAccessor.query("quotation.queryQuotationEquipment",context, DataAccessor.RS_TYPE.LIST);
				quotationFee=(List) DataAccessor.query("quotation.queryExportQuotationFee",context, DataAccessor.RS_TYPE.LIST);
				List<Map> irrMonthPaylines = (List) DataAccessor.query("quotation.queryQuotationSchemaIrr",context,DataAccessor.RS_TYPE.LIST);
				//Add by Michael 2012 1/5 For 方案的查询
				Map schemaTempMap=(Map) schema.get(0);
				
				if (schemaTempMap!=null){
					schemaTempMap.put("irrMonthPaylines", irrMonthPaylines);
					packagePaylinesForValueAdded(schemaTempMap);
				}
				
				resultMap.put("irrMonthPaylines", schemaTempMap.get("irrMonthPaylines"));
				resultMap.put("schema", schema);
				resultMap.put("equipmentList", equipmentList);
				resultMap.put("quotationFee", quotationFee);
				String allFeeName="";
				Double allFeeValue=0.0;
				int i=1;
				for(Map fee : quotationFee){
					allFeeValue+=DataUtil.doubleUtil(fee.get("FEE"));
					if (i==1){
						allFeeName=String.valueOf(fee.get("CREATE_SHOW_NAME"));
					}else{
						allFeeName=allFeeName+"、"+String.valueOf(fee.get("CREATE_SHOW_NAME"));
					}
					i++;
				}
				resultMap.put("allFeeName", allFeeName);
				resultMap.put("allFeeValue", allFeeValue);
				
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
		return resultMap;		
	}
		
	public static Map<String,Object> exportOwnPrice (String ID) {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map context= new HashMap();
		List ownPriceList=null;
		context.put("ID", ID);
		Map schema;
//		Map schema;
		/*-------- data access --------*/		
			try {
				context.put("payway", "支付方式");
				schema=(Map) DataAccessor.query("quotation.queryQuotationForExport",context, DataAccessor.RS_TYPE.MAP);
				ownPriceList=(List) DataAccessor.query("quotation.queryExportOwnprice",context, DataAccessor.RS_TYPE.LIST);
				resultMap.put("ownPriceList", ownPriceList);
				resultMap.put("TOTAL_VALUEADDED_TAX", schema.get("TOTAL_VALUEADDED_TAX"));
				resultMap.put("CUST_NAME", schema.get("CUST_NAME"));
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
		return resultMap;		
	}
	
	public void queryQuotationExportLog (Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		DataWrap dw = null;

		if(errList.isEmpty()){		
			try {
				dw = (DataWrap) DataAccessor.query("quotation.queryQuotationExportLog", context.contextMap, DataAccessor.RS_TYPE.PAGED);
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		
		/*-------- output --------*/
		outputMap.put("dw", dw);
		
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/quotation/quotationLogManage.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}

	
	public void createQuotationExportLog (Context context) {
		SqlMapClient sqlMapper = null;
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map schema = null;
		if(errList.isEmpty()){	
			try {

				schema=(Map) DataAccessor.query("quotation.queryQuotationSchema",context.contextMap, DataAccessor.RS_TYPE.MAP);
				sqlMapper = DataAccessor.getSession();
				context.contextMap.put("CUST_NAME", schema.get("CUST_NAME"));
				context.contextMap.put("MEMO", context.contextMap.get("s_employeeName")+"导出"+context.contextMap.get("strFlag"));
				sqlMapper.startTransaction();
				sqlMapper.insert("quotation.createQuotationExportlog", context.contextMap);
				sqlMapper.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("报价管理--增加导出日志!请联系管理员");
			}finally {
				try {
					sqlMapper.endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				}
			}
		
		}
		if(context.errList.isEmpty()) {
			Output.jsonOutput(outputMap,context);
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
	}
	
	public void copyDirectQuotationScheme(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map schema = null;
		List equipmentList=null;
		List quotationFee=null;
		List result=new ArrayList();
		Map res=new HashMap();
		if(Constants.TAX_PLAN_CODE_4.equals(context.contextMap.get("taxPlanCode"))) {
			res.put("CODE",4);
			res.put("FLAG","直接租赁税费方案");
		} else if(Constants.TAX_PLAN_CODE_6.equals(context.contextMap.get("taxPlanCode"))) {
			res.put("CODE",6);
			res.put("FLAG","设备售后回租方案");
		} else if(Constants.TAX_PLAN_CODE_7.equals(context.contextMap.get("taxPlanCode"))) {
			res.put("CODE",7);
			res.put("FLAG","乘用车售后回租方案");
		} else if(Constants.TAX_PLAN_CODE_8.equals(context.contextMap.get("taxPlanCode"))) {
			res.put("CODE",8);
			res.put("FLAG","商用车售后回租方案");
		}
		result.add(res);
		outputMap.put("taxPlanList", result);
		
		try {
			Map baseRate = PayRate.getBaseRate();
			outputMap.put("baseRateJson", Output.serializer.serialize(baseRate));
			//
			outputMap.put("payWays", DictionaryUtil.getDictionary("支付方式"));
			
			schema=(Map) DataAccessor.query("quotation.queryQuotationSchema",context.contextMap, DataAccessor.RS_TYPE.MAP);
			equipmentList=(List) DataAccessor.query("quotation.queryQuotationEquipment",context.contextMap, DataAccessor.RS_TYPE.LIST);
			quotationFee=(List) DataAccessor.query("quotation.queryQuotationFee",context.contextMap, DataAccessor.RS_TYPE.LIST);
			List<Map> irrMonthPaylines = (List) DataAccessor.query("quotation.queryQuotationSchemaIrr",context.contextMap,DataAccessor.RS_TYPE.LIST);
			
			schema.put("irrMonthPaylines", irrMonthPaylines);
			outputMap.put("schema", schema);
			outputMap.put("equipmentList", equipmentList);
			
			List feeSetLists =null;
			double totalFeeSet=0.0d;
			Map where=null;

			List<Map> feeListRZE = (List<Map>) DataAccessor.query("quotation.getCreditFeeListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
			List<Map> feeList = (List<Map>) DataAccessor.query("quotation.getCreditFeeList",context.contextMap, DataAccessor.RS_TYPE.LIST);

			outputMap.put("feeListRZE", feeListRZE);
			outputMap.put("feeList", feeList);	
			
			List<Map> feeSetListRZE = (List<Map>) DataAccessor.query("quotation.getFeeSetListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
			List<Map> feeSetList = (List<Map>) DataAccessor.query("quotation.getFeeSetList",context.contextMap, DataAccessor.RS_TYPE.LIST);
			
			outputMap.put("feeSetListRZE", feeSetListRZE);
			outputMap.put("feeSetList", feeSetList);
			
			outputMap.put("disabled", "disabled");
		} catch(Exception e) {
			
		}
		Output.jspOutput(outputMap, context, "/quotation/directQuotationCopyScheme.jsp");
	}
	public void copyDirectQuotationScheme1(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map schema = null;
		List equipmentList=null;
		List quotationFee=null;
		List result=new ArrayList();
		Map res=new HashMap();
		res.put("CODE",5);
		res.put("FLAG","乘用车委贷方案");
		result.add(res);
		outputMap.put("taxPlanList", result);
		
		try {
			Map baseRate = PayRate.getBaseRate();
			outputMap.put("baseRateJson", Output.serializer.serialize(baseRate));
			//
			outputMap.put("payWays", DictionaryUtil.getDictionary("支付方式"));
			
			schema=(Map) DataAccessor.query("quotation.queryQuotationSchema",context.contextMap, DataAccessor.RS_TYPE.MAP);
			equipmentList=(List) DataAccessor.query("quotation.queryQuotationEquipment",context.contextMap, DataAccessor.RS_TYPE.LIST);
			quotationFee=(List) DataAccessor.query("quotation.queryQuotationFee",context.contextMap, DataAccessor.RS_TYPE.LIST);
			List<Map> irrMonthPaylines = (List) DataAccessor.query("quotation.queryQuotationSchemaIrr",context.contextMap,DataAccessor.RS_TYPE.LIST);
			
			schema.put("irrMonthPaylines", irrMonthPaylines);
			outputMap.put("schema", schema);
			outputMap.put("equipmentList", equipmentList);
			
			List feeSetLists =null;
			double totalFeeSet=0.0d;
			Map where=null;

			List<Map> feeListRZE = (List<Map>) DataAccessor.query("quotation.getCreditFeeListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
			List<Map> feeList = (List<Map>) DataAccessor.query("quotation.getCreditFeeList",context.contextMap, DataAccessor.RS_TYPE.LIST);

			outputMap.put("feeListRZE", feeListRZE);
			outputMap.put("feeList", feeList);	
			
			List<Map> feeSetListRZE = (List<Map>) DataAccessor.query("quotation.getFeeSetListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
			List<Map> feeSetList = (List<Map>) DataAccessor.query("quotation.getFeeSetList",context.contextMap, DataAccessor.RS_TYPE.LIST);
			
			outputMap.put("feeSetListRZE", feeSetListRZE);
			outputMap.put("feeSetList", feeSetList);
			
			outputMap.put("disabled", "disabled");
		} catch(Exception e) {
			
		}
		Output.jspOutput(outputMap, context, "/quotation/motorQuotationCopyScheme.jsp");
	}
	
	@SuppressWarnings("unchecked")
	public void copyQuotationScheme (Context context) {

		Map outputMap = new HashMap();
		List errList = context.errList;
		Map schema = null;
		List equipmentList=null;
		List quotationFee=null;
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {

				Map baseRate = PayRate.getBaseRate();
				outputMap.put("baseRateJson", Output.serializer.serialize(baseRate));
				//
				outputMap.put("payWays", DictionaryUtil.getDictionary("支付方式"));
				
				schema=(Map) DataAccessor.query("quotation.queryQuotationSchema",context.contextMap, DataAccessor.RS_TYPE.MAP);
				equipmentList=(List) DataAccessor.query("quotation.queryQuotationEquipment",context.contextMap, DataAccessor.RS_TYPE.LIST);
				quotationFee=(List) DataAccessor.query("quotation.queryQuotationFee",context.contextMap, DataAccessor.RS_TYPE.LIST);
				List<Map> irrMonthPaylines = (List) DataAccessor.query("quotation.queryQuotationSchemaIrr",context.contextMap,DataAccessor.RS_TYPE.LIST);
				//Add by Michael 2012 1/5 For 方案的查询
				schema.put("irrMonthPaylines", irrMonthPaylines);
				outputMap.put("schema", schema);
				outputMap.put("equipmentList", equipmentList);
				
				List feeSetLists =null;
				double totalFeeSet=0.0d;
				Map where=null;

				List<Map> feeListRZE = (List<Map>) DataAccessor.query("quotation.getCreditFeeListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
				List<Map> feeList = (List<Map>) DataAccessor.query("quotation.getCreditFeeList",context.contextMap, DataAccessor.RS_TYPE.LIST);

				outputMap.put("feeListRZE", feeListRZE);
				outputMap.put("feeList", feeList);	
				
				List<Map> feeSetListRZE = (List<Map>) DataAccessor.query("quotation.getFeeSetListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
				List<Map> feeSetList = (List<Map>) DataAccessor.query("quotation.getFeeSetList",context.contextMap, DataAccessor.RS_TYPE.LIST);
				
				outputMap.put("feeSetListRZE", feeSetListRZE);
				outputMap.put("feeSetList", feeSetList);
				//-------------------------------------------------------------------	
				List result=DictionaryUtil.getDictionary("税费方案");
				for(int i=0;i<result.size();i++) {
					if("4".equals(((Map)result.get(i)).get("CODE"))) {
						result.remove(i);
						break;
					}
				}
				outputMap.put("taxPlanList", result);
				outputMap.put("disabled", "disabled");
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("报价管理--报价单修改!请联系管理员");
			}
		
		}
		if (errList.isEmpty()) {
			if ("4".equals(String.valueOf(schema.get("CONTRACT_TYPE")))||"6".equals(String.valueOf(schema.get("CONTRACT_TYPE")))){
				context.contextMap.put("contractType",schema.get("CONTRACT_TYPE"));
				outputMap.put("contractType",schema.get("CONTRACT_TYPE"));
				Output.jspOutput(outputMap, context, "/carQuotation/quotationCopyScheme.jsp");
			}else{
				Output.jspOutput(outputMap, context, "/quotation/quotationCopyScheme.jsp");
			}
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
	}
	
	public void deleteQuotation (Context context) {
		SqlMapClient sqlMapper = null;
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map schema = null;
		if(errList.isEmpty()){	
			try {
				schema=(Map) DataAccessor.query("quotation.queryQuotationSchema",context.contextMap, DataAccessor.RS_TYPE.MAP);
				sqlMapper = DataAccessor.getSession();
				sqlMapper.startTransaction();
				DataAccessor.execute("quotation.deleteQuotation",context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
				context.contextMap.put("CUST_NAME", schema.get("CUST_NAME"));
				context.contextMap.put("MEMO", context.contextMap.get("s_employeeName")+"删除报价单");
				sqlMapper.insert("quotation.createQuotationExportlog", context.contextMap);
				sqlMapper.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("报价管理--增加导出日志!请联系管理员");
			}finally {
				try {
					sqlMapper.endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				}
			}
		}
		if(context.errList.isEmpty()) {
			Output.jsonOutput(outputMap,context);
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
	}
	
	
	public static Map<String,Object> exportOfficeAddress (String ID) {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map context= new HashMap();
		List ownPriceList=null;
		context.put("ID", ID);
		Map schema;
//		Map schema;
		/*-------- data access --------*/		
			try {
				context.put("payway", "支付方式");
				schema=(Map) DataAccessor.query("quotation.queryQuotationForExport",context, DataAccessor.RS_TYPE.MAP);
				ownPriceList=(List) DataAccessor.query("quotation.queryExportOwnprice",context, DataAccessor.RS_TYPE.LIST);
				resultMap.put("ownPriceList", ownPriceList);
				resultMap.put("CUST_NAME", schema.get("CUST_NAME"));
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
		return resultMap;		
	}
	
	@SuppressWarnings("unchecked")
	public static void packagePaylinesForValueAdded(Map paylist) {
	    
	    int payWay = DataUtil.intUtil(paylist.get("PAY_WAY"));
	    double totalValueAddedTax=DataUtil.doubleUtil(paylist.get("TOTAL_VALUEADDED_TAX"));
		//平均每期的增值税
	    double valueAddedTax=0.0;
	    double tempTotalValueAdded =0.0;
	    double lastValueAddedTax=0.0;
		valueAddedTax=Math.ceil(new BigDecimal(totalValueAddedTax).divide(new BigDecimal(String.valueOf(paylist.get("LEASE_PERIOD"))),2,BigDecimal.ROUND_HALF_UP).doubleValue());
		
		for (int i=1;i<=DataUtil.intUtil(paylist.get("LEASE_PERIOD"));i++){
			if(i==DataUtil.intUtil(paylist.get("LEASE_PERIOD"))){
				lastValueAddedTax=new BigDecimal(totalValueAddedTax).subtract(new BigDecimal(tempTotalValueAdded)).doubleValue();
			}
			tempTotalValueAdded+= valueAddedTax;
		}
		
		List<Map> irrMonthPaylinesTemp = paylist == null ? null : (List<Map>)paylist.get("irrMonthPaylines");
		if(irrMonthPaylinesTemp == null){
			irrMonthPaylinesTemp = new ArrayList() ;
		}
		List irrMonthPaylines =new ArrayList();
		
		if(irrMonthPaylinesTemp.size() != 0){
			int endNum = Integer.parseInt(((Map)irrMonthPaylinesTemp.get(irrMonthPaylinesTemp.size()-1)).get("IRR_MONTH_PRICE_END").toString()) ;
			for(int i=0;i<irrMonthPaylinesTemp.size();i++){
				Map temp = (Map) irrMonthPaylinesTemp.get(i) ;
				Map map = null ;
				int start = Integer.parseInt(temp.get("IRR_MONTH_PRICE_START").toString()) ;
				int end = Integer.parseInt(temp.get("IRR_MONTH_PRICE_END").toString()) ;
				double price = Double.parseDouble(temp.get("IRR_MONTH_PRICE").toString()) ;
				if(i == irrMonthPaylinesTemp.size() - 1  ){
					if(valueAddedTax!=lastValueAddedTax){
						if(start != end ){
							map = new HashMap() ;
							map.put("IRR_MONTH_PRICE_START",start ) ;
							map.put("IRR_MONTH_PRICE_END",end - 1 ) ;
							map.put("IRR_MONTH_PRICE", price ) ;
							map.put("VALUE_ADDED_TAX", valueAddedTax ) ;
							map.put("MONTH_PRICE_TAX", price + valueAddedTax) ;
							irrMonthPaylines.add(map) ;
						} 
						map = new HashMap() ;
						map.put("IRR_MONTH_PRICE_START",end ) ;
						map.put("IRR_MONTH_PRICE_END",end ) ;
						map.put("IRR_MONTH_PRICE", price ) ;
						map.put("VALUE_ADDED_TAX", lastValueAddedTax ) ;
						map.put("MONTH_PRICE_TAX", price + lastValueAddedTax) ;
						irrMonthPaylines.add(map) ;
					}else{
						map = new HashMap() ;
						map.put("IRR_MONTH_PRICE_START",start ) ;
						map.put("IRR_MONTH_PRICE_END",end ) ;
						map.put("IRR_MONTH_PRICE", price ) ;
						map.put("VALUE_ADDED_TAX", valueAddedTax ) ;
						map.put("MONTH_PRICE_TAX", price + valueAddedTax) ;
						irrMonthPaylines.add(map) ;
					}
					
				}else {
					map = new HashMap() ;
					map.put("IRR_MONTH_PRICE_START",start ) ;
					map.put("IRR_MONTH_PRICE_END",end ) ;
					map.put("IRR_MONTH_PRICE", price ) ;
					map.put("VALUE_ADDED_TAX", valueAddedTax ) ;
					map.put("MONTH_PRICE_TAX", price + valueAddedTax) ;
					irrMonthPaylines.add(map) ;
				}
			}
		}
		
		paylist.put("irrMonthPaylines", irrMonthPaylines);
		paylist.put("valueAddedTax", valueAddedTax);
		paylist.put("lastValueAddedTax", lastValueAddedTax);

	}
	
	@SuppressWarnings("unchecked")
	public static void packagePaylinesForValueAddedForCalculate(Map paylist) {
	    
	    int payWay = DataUtil.intUtil(paylist.get("PAY_WAY"));
	    double totalValueAddedTax=DataUtil.doubleUtil(paylist.get("TOTAL_VALUEADDED_TAX"));
		//平均每期的增值税
	    double valueAddedTax=0.0;
	    double tempTotalValueAdded =0.0;
	    double lastValueAddedTax=0.0;
		valueAddedTax=Math.ceil(new BigDecimal(totalValueAddedTax).divide(new BigDecimal(String.valueOf(paylist.get("LEASE_PERIOD"))),2,BigDecimal.ROUND_HALF_UP).doubleValue());
		
		for (int i=1;i<=DataUtil.intUtil(paylist.get("LEASE_PERIOD"));i++){
			if(i==DataUtil.intUtil(paylist.get("LEASE_PERIOD"))){
				lastValueAddedTax=new BigDecimal(totalValueAddedTax).subtract(new BigDecimal(tempTotalValueAdded)).doubleValue();
			}
			tempTotalValueAdded+= valueAddedTax;
		}
		
		List<Map> irrMonthPaylinesTemp = paylist == null ? null : (List<Map>)paylist.get("irrMonthPaylines");
		if(irrMonthPaylinesTemp == null){
			irrMonthPaylinesTemp = new ArrayList() ;
		}
		List irrMonthPaylines =new ArrayList();
		
		if(irrMonthPaylinesTemp.size() != 0){
			int endNum = Integer.parseInt(((Map)irrMonthPaylinesTemp.get(irrMonthPaylinesTemp.size()-1)).get("IRR_MONTH_PRICE_END").toString()) ;
			for(int i=0;i<irrMonthPaylinesTemp.size();i++){
				Map temp = (Map) irrMonthPaylinesTemp.get(i) ;
				Map map = null ;
				int start = Integer.parseInt(temp.get("IRR_MONTH_PRICE_START").toString()) ;
				int end = Integer.parseInt(temp.get("IRR_MONTH_PRICE_END").toString()) ;
				double price = Double.parseDouble(temp.get("IRR_MONTH_PRICE").toString()) ;
				if(i == irrMonthPaylinesTemp.size() - 1  ){
					if(start != end ){
						map = new HashMap() ;
						map.put("IRR_MONTH_PRICE_START",start ) ;
						map.put("IRR_MONTH_PRICE_END",end - 1 ) ;
						map.put("IRR_MONTH_PRICE", price ) ;
						map.put("VALUE_ADDED_TAX", valueAddedTax ) ;
						map.put("MONTH_PRICE_TAX", price + valueAddedTax) ;
						irrMonthPaylines.add(map) ;
					} 
					map = new HashMap() ;
					map.put("IRR_MONTH_PRICE_START",end ) ;
					map.put("IRR_MONTH_PRICE_END",end ) ;
					map.put("IRR_MONTH_PRICE", price ) ;
					map.put("VALUE_ADDED_TAX", lastValueAddedTax ) ;
					map.put("MONTH_PRICE_TAX", price + lastValueAddedTax) ;
					irrMonthPaylines.add(map) ;
				}else {
					map = new HashMap() ;
					map.put("IRR_MONTH_PRICE_START",start ) ;
					map.put("IRR_MONTH_PRICE_END",end ) ;
					map.put("IRR_MONTH_PRICE", price ) ;
					map.put("VALUE_ADDED_TAX", valueAddedTax ) ;
					map.put("MONTH_PRICE_TAX", price + valueAddedTax) ;
					irrMonthPaylines.add(map) ;
				}
			}
		}
		
		paylist.put("irrMonthTaxPaylines", irrMonthPaylines);
		paylist.put("valueAddedTax", valueAddedTax);
		paylist.put("lastValueAddedTax", lastValueAddedTax);

	}
	
	public static Map<String,Object> exportOwnPriceForInternal (String ID) {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map context= new HashMap();
		List ownPriceList=null;
		context.put("ID", ID);
		Map schema;
		/*-------- data access --------*/		
			try {
				context.put("payway", "支付方式");
				schema=(Map) DataAccessor.query("quotation.queryQuotationForExport",context, DataAccessor.RS_TYPE.MAP);
				ownPriceList=(List) DataAccessor.query("quotation.queryExportOwnpriceForInternal",context, DataAccessor.RS_TYPE.LIST);
				resultMap.put("ownPriceList", ownPriceList);
				resultMap.put("TOTAL_VALUEADDED_TAX", schema.get("TOTAL_VALUEADDED_TAX"));
				resultMap.put("CUST_NAME", schema.get("CUST_NAME"));
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
		return resultMap;		
	}
	
	public void saveScheme(Context context) {
		context.contextMap.put("needSave","Y");//需要保存
		try {
			this.calculateDirectQuotationPaylistIRR(context);
		} catch (Exception e) {
		}
	}
	
	public void saveScheme1(Context context) {
		context.contextMap.put("needSave","Y");//需要保存
		try {
			this.calculateQuotationPaylistIRR(context);
		} catch (Exception e) {
		}
	}
}
