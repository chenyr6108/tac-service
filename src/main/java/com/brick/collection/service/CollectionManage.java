package com.brick.collection.service;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import com.brick.base.command.BaseCommand;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.base.util.LeaseUtil;
import com.brick.baseManage.service.BusinessLog;
import com.brick.collection.support.PayRate;
import com.brick.collection.util.PaylistUtil;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.Constants;
import com.brick.util.DataUtil;
import com.brick.util.InterestMarginUtil;
import com.brick.util.NumberUtils;
import com.brick.util.web.HTMLUtil;
import com.ibatis.sqlmap.client.SqlMapClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;

/**
 * @author wujw
 * @date May 26, 2010
 * @version 
 */
public class CollectionManage extends BaseCommand {
	Log logger = LogFactory.getLog(CollectionManage.class);

	/**
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryPaylist(Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		PagingInfo<Object> dw = null;

		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			//-- Add BY Michael 增加权限，业务员只能看到自己的案件------------------------
			Map paramMap = new HashMap();
			Map rsMap = null;
			paramMap.put("id", context.contextMap.get("s_employeeId"));
			try {
				rsMap = (Map) DataAccessor.query("employee.getEmpInforById", paramMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("p_usernode", rsMap.get("NODE"));
	//------------------------------------------------------------------------
				dw = baseService.queryForListWithPaging("collectionManage.queryPaylist", context.contextMap, "RECP_ID", ORDER_TYPE.DESC);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}

		/*-------- output --------*/
		outputMap.put("dw", dw);
		outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
		outputMap.put("QSTART_DATE", context.contextMap.get("QSTART_DATE"));
		outputMap.put("QEND_DATE", context.contextMap.get("QEND_DATE"));
		outputMap.put("JQSTART_DATE", context.contextMap.get("JQSTART_DATE"));
		outputMap.put("JQEND_DATE", context.contextMap.get("JQEND_DATE"));
		outputMap.put("QSELECT_STATUS", context.contextMap.get("QSELECT_STATUS"));

		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/collection/queryPaylist.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}
	
	
	/**
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryPaylistForExport(Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		PagingInfo<Object> dw = null;
		
		Boolean addCarPayAccount=false;
		
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
	//-- Add BY Michael 增加权限，业务员只能看到自己的案件------------------------
			Map paramMap = new HashMap();
			Map rsMap = null;
			paramMap.put("id", context.contextMap.get("s_employeeId"));
			try {
				rsMap = (Map) DataAccessor.query("employee.getEmpInforById", paramMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("p_usernode", rsMap.get("NODE"));
	//------------------------------------------------------------------------
				dw = baseService.queryForListWithPaging("collectionManage.queryPaylist", context.contextMap, "RECP_ID", ORDER_TYPE.DESC);
			
				//Add by Michael 2012 5-25 增加维护重车还款账户权限
				
				List<String> resourceIdList=(List<String>) DataAccessor.query("supplier.getResourceIdListByEmplId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				for(int i=0;resourceIdList!=null&&i<resourceIdList.size();i++) {
					if("219".equals(resourceIdList.get(i))) {
						addCarPayAccount=true;
					}
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
		outputMap.put("QSTART_DATE", context.contextMap.get("QSTART_DATE"));
		outputMap.put("QEND_DATE", context.contextMap.get("QEND_DATE"));
		outputMap.put("JQSTART_DATE", context.contextMap.get("JQSTART_DATE"));
		outputMap.put("JQEND_DATE", context.contextMap.get("JQEND_DATE"));
		outputMap.put("QSELECT_STATUS", context.contextMap.get("QSELECT_STATUS"));
		outputMap.put("companyCode", context.contextMap.get("companyCode"));
		outputMap.put("addCarPayAccount", addCarPayAccount);

		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/collection/queryPaylistForExport.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}
	
	
	/**
	 * 查询设备
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryEquipment(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		Map rentContract = null;
		List<Map> equipList = null;
		Map countMap = null;
		
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {
				
				outputMap.put("RECT_ID", context.contextMap.get("RECT_ID"));

				rentContract = (Map) DataAccessor.query("rentContract.readRentContractByRectId", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("rentContract", rentContract);
			
				equipList = (List<Map>) DataAccessor.query("collectionManage.queryEquipment", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("equipList", equipList);
				
				//在还款页面加入,单价,含税价,数量的统计继续算 add by ShenQi,see mantis 286
				countMap=(Map)DataAccessor.query("collectionManage.queryEquipmentForCount", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("unitPrice", countMap.get("UNIT_PRICE"));
				outputMap.put("amount", countMap.get("AMOUNT"));
				outputMap.put("taxPrice", countMap.get("TAX_PRICE"));
				
				//查询数据字典
				Map temp = new HashMap() ;
				temp.put("dataType", "锁码方式");
				outputMap.put("lockList",(List) DataAccessor.query("dataDictionary.queryDataDictionary", temp, DataAccessor.RS_TYPE.LIST));
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		
		}
		
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/collection/selectEquipment.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
	
	}
	/**
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void createCollection(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		Map rentContract = null;
		List<Map> equipList = null;
		
		Map contractSchema = null;
		
		List<Map> insureCompanyList = null;
		List<Map> insureTypeList = null;
		List<Map> insureList = null;
		List<Map> otherFeeList = null;
		List<Map> irrMonthPaylines=null;
		List suplList=null;
		
		String type = (String) context.contextMap.get("DEAL_TYPE");
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {
				sqlMapper.startTransaction() ;
				
				String[] recdIds = null;
				if("0".equals(type)){
					recdIds = HTMLUtil.getParameterValues(context.request, "RECD_ID", "0");
					for(int i = 0 ; i < recdIds.length ; i++){
						String lastLockCode = (String) context.contextMap.get("LAST_LOCK_CODE"+recdIds[i]) ;
						String lockCode = (String) context.contextMap.get("LOCK_CODE"+recdIds[i]) ;
						if(!lastLockCode.equals(lockCode)){
							Map temp = new HashMap() ;
							temp.put("LOCK_CODE", lockCode) ;
							temp.put("RECD_ID", recdIds[i]) ;
							sqlMapper.update("collectionManage.updateLockCode", temp) ;
						}
					}
				}else{
					recdIds = HTMLUtil.getParameterValues(context.request, "RECD_IDs", "0");
				}
				
	
				rentContract = (Map) DataAccessor.query("rentContract.readRentContractByRectId", context.contextMap, DataAccessor.RS_TYPE.MAP);
				
				Map recdMap = new HashMap();
				recdMap.put("recdIds", recdIds);
				equipList = (List<Map>) DataAccessor.query("collectionManage.queryEquipmentByIds", recdMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("equipList", equipList);
				contractSchema = (Map) DataAccessor.query("rentContract.readSchemaByRectId", context.contextMap, DataAccessor.RS_TYPE.MAP);
				
				insureCompanyList = (List<Map>) DataAccessor.query("insuCompany.queryInsureCompanyListForSelect", null, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureCompanyList", insureCompanyList);
				
				insureTypeList = (List<Map>) DataAccessor.query("insureType.queryInsureTypeList", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureTypeList", insureTypeList);
				outputMap.put("insureTypeJsonList", Output.serializer.serialize(insureTypeList));
				insureList = (List<Map>) DataAccessor.query("rentContract.readInsureByRectId", context.contextMap, DataAccessor.RS_TYPE.LIST);

				otherFeeList = (List<Map>) DataAccessor.query("rentContract.readOtherFeeByRectId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				
				List<Map> oldPaylists = (List<Map>) DataAccessor.query("rentContractPact.queryPaylistByRectId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				//

				//Add by Michael 2012 01/14 For 方案费用查询 影响概算成本为1 不影响为0
				List feeListRZE=null;
				feeListRZE = (List) DataAccessor.query("rentContract.getCreditFeeListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("feeListRZE", feeListRZE);
				List feeList=null;
				feeList = (List) DataAccessor.query("rentContract.getCreditFeeList",context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("feeList", feeList);	
				
				//费用设定明细 影响概算成本为1 不影响为0
				List feeSetListRZE=null;
				feeSetListRZE = (List) DataAccessor.query("rentContract.getFeeSetListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("feeSetListRZE", feeSetListRZE);
				List feeSetList=null;
				feeSetList = (List) DataAccessor.query("rentContract.getFeeSetList",context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("feeSetList", feeSetList);
				//-------------------------------------------------------------------				
				
				//add by Michael 把管理费收入总和传过来，计算营业税收入，会影响TR计算----------------------
				double totalFeeSet=0.0d;
				if(Constants.TAX_PLAN_CODE_2.equals(contractSchema.get("TAX_PLAN_CODE"))){
					List<Map> listTotalFeeSet=(List) DataAccessor.query("rentContract.getTotalFeeByRectID",context.contextMap, DataAccessor.RS_TYPE.LIST);
					for(Map map:listTotalFeeSet){
						totalFeeSet+=new BigDecimal(DataUtil.doubleUtil(map.get("FEE"))/1.06).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
					}	
				}else if(Constants.TAX_PLAN_CODE_1.equals(contractSchema.get("TAX_PLAN_CODE"))){
					totalFeeSet=(Double)DataAccessor.query("rentContract.sumTotalFeeByRectID",context.contextMap, DataAccessor.RS_TYPE.OBJECT);
				} else if(Constants.TAX_PLAN_CODE_3.equals(contractSchema.get("TAX_PLAN_CODE"))||Constants.TAX_PLAN_CODE_4.equals(contractSchema.get("TAX_PLAN_CODE"))||Constants.TAX_PLAN_CODE_5.equals(contractSchema.get("TAX_PLAN_CODE"))
						||Constants.TAX_PLAN_CODE_6.equals(contractSchema.get("TAX_PLAN_CODE"))||Constants.TAX_PLAN_CODE_7.equals(contractSchema.get("TAX_PLAN_CODE"))||Constants.TAX_PLAN_CODE_8.equals(contractSchema.get("TAX_PLAN_CODE"))) {//加入增值税内含方案  add by ShenQi
					totalFeeSet=(Double)DataAccessor.query("rentContract.sumTotalFeeByRectID",context.contextMap, DataAccessor.RS_TYPE.OBJECT);
				}
				
				if(contractSchema!=null&&Constants.TAX_PLAN_CODE_5.equals(contractSchema.get("TAX_PLAN_CODE"))) {
					Map param=new HashMap();
					param.put("credit_id",rentContract.get("PRCD_ID"));
					Map resultMap=(Map)DataAccessor.query("creditReportManage.selectCreditScheme", param, DataAccessor.RS_TYPE.MAP);
					outputMap.put("SALES_PAY",resultMap.get("SALES_PAY"));
					outputMap.put("INCOME_PAY",resultMap.get("INCOME_PAY"));
					outputMap.put("OUT_PAY",resultMap.get("OUT_PAY"));
				}
				
				if(contractSchema!=null&&Constants.TAX_PLAN_CODE_7.equals(contractSchema.get("TAX_PLAN_CODE"))) {
					Map param=new HashMap();
					param.put("credit_id",rentContract.get("PRCD_ID"));
					Map resultMap=(Map)DataAccessor.query("creditReportManage.selectCreditScheme", param, DataAccessor.RS_TYPE.MAP);
					outputMap.put("SALES_PAY",resultMap.get("SALES_PAY"));
				}
				contractSchema.put("FEESET_TOTAL", totalFeeSet);
				//-----------------------------------------------------------------------------
				
				// contain RECS_ID
				irrMonthPaylines = (List<Map>) DataAccessor.query("rentContract.readSchemaIrrByRecsId", contractSchema, DataAccessor.RS_TYPE.LIST);
				outputMap.put("irrMonthPaylines", irrMonthPaylines);
				List<Map> rePaylineList = StartPayService.upPackagePaylines(irrMonthPaylines);
				
				Map paylist = StartPayService.createPaylist(rentContract, contractSchema, equipList, insureList, otherFeeList, oldPaylists, rePaylineList,irrMonthPaylines);
				paylist.put("PLEDGE_ENTER_AGRATE", contractSchema.get("PLEDGE_ENTER_AGRATE"));
				paylist.put("PLEDGE_ENTER_MCTOAG", contractSchema.get("PLEDGE_ENTER_MCTOAG"));
				paylist.put("PLEDGE_ENTER_MCTOAGRATE", contractSchema.get("PLEDGE_ENTER_MCTOAGRATE"));
				paylist.put("SUPL_TRUE", contractSchema.get("SUPL_TRUE"));
				
				paylist.put("TAX_PLAN_CODE", contractSchema.get("TAX_PLAN_CODE"));
				paylist.put("PLEDGE_LAST_PRICE_TAX", contractSchema.get("PLEDGE_LAST_PRICE_TAX"));
				paylist.put("DEFER_PERIOD", contractSchema.get("DEFER_PERIOD"));
				
				outputMap.put("paylist", paylist);
				
				//Modify by Michael 2012 1/5 TR、利差不要重新计算，从之前值带过来
				outputMap.put("contractSchema", contractSchema);
				//Add by Michael 2012 09-21 增加税费测算方案
				outputMap.put("taxPlanList", DictionaryUtil.getDictionary("税费方案"));
				//增加利差 --开始
//				double pvPrice = 0.0d ;
//				List payLines = (List) paylist.get("paylines") ;
//				for(int i = 0 ;i<payLines.size() ; i++){
//					pvPrice += (Double)((Map)payLines.get(i)).get("PV_PRICE");
//				}
//				outputMap.put("TOTAL_PV_PRICE", pvPrice);
				//增加利差 --结束
				Map baseRate = PayRate.getBaseRate();
				outputMap.put("baseRateJson", Output.serializer.serialize(baseRate));
				
				//
				outputMap.put("payWays", DictionaryUtil.getDictionary("支付方式"));
				outputMap.put("dealWays", DictionaryUtil.getDictionary("租赁期满处理方式"));
				//查询数据字典
				Map temp = new HashMap() ;
				temp.put("dataType", "锁码方式");
				outputMap.put("lockList",(List) DataAccessor.query("dataDictionary.queryDataDictionary", temp, DataAccessor.RS_TYPE.LIST));
				
				suplList=(List)DictionaryUtil.getDictionary("供应商保证");
				outputMap.put("suplList", suplList);
				
				//Add by Michael 2012 12-20  增加费用来源
				outputMap.put("feeSourceList", DictionaryUtil.getDictionary("费用来源"));
				
				sqlMapper.commitTransaction() ;
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} finally {
				try {
					sqlMapper.endTransaction() ;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		}
		if (errList.isEmpty()) {
			/*if (payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL || payWay == CollectionConstants.PAY_WAY_END_UNEQUAL){
				Output.jspOutput(outputMap, context, "/collection/createRecalculate.jsp");
			} else {
			}*/
			if("0".equals(type)){//判断是否是点 预览支付明细表
				Output.jspOutput(outputMap, context, "/collection/createCalculate.jsp");
			}else{
				//复核中点击预览支付表
				Output.jspOutput(outputMap, context, "/collection/createCalculate2.jsp");
			}
			
			
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
	
	}
	/*
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void createCalculate(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		Map rentContract = null;
		List<Map> equipList = null;
		
		List<Map> insureCompanyList = null;
		List<Map> insureTypeList = null;
		List suplList=null;
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {

				
				
				rentContract = (Map) DataAccessor.query("rentContract.readRentContractByRectId", context.contextMap, DataAccessor.RS_TYPE.MAP);
				
				String[] recdIds = HTMLUtil.getParameterValues(context.request, "RECD_ID", "0");
				Map recdMap = new HashMap();
				recdMap.put("recdIds", recdIds);
				equipList = (List<Map>) DataAccessor.query("collectionManage.queryEquipmentByIds", recdMap, DataAccessor.RS_TYPE.LIST);
				
				insureCompanyList = (List<Map>) DataAccessor.query("insuCompany.queryInsureCompanyListForSelect", null, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureCompanyList", insureCompanyList);
				
				insureTypeList = (List<Map>) DataAccessor.query("insureType.queryInsureTypeList", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureTypeList", insureTypeList);
				outputMap.put("insureTypeJsonList", Output.serializer.serialize(insureTypeList));

				//add by Michael 把管理费收入总和传过来，计算营业税收入，会影响TR计算----------------------
				double totalFeeSet=0.0d;
				
				if("2".equals(context.contextMap.get("TAX_PLAN_CODE"))){
					List<Map> listTotalFeeSet=(List) DataAccessor.query("rentContract.getTotalFeeByRectID",context.contextMap, DataAccessor.RS_TYPE.LIST);
					for(Map map:listTotalFeeSet){
						totalFeeSet+=new BigDecimal(DataUtil.doubleUtil(map.get("FEE"))/1.06).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
					}	
				}else if("1".equals(context.contextMap.get("TAX_PLAN_CODE"))){
					totalFeeSet=(Double)DataAccessor.query("rentContract.sumTotalFeeByRectID",context.contextMap, DataAccessor.RS_TYPE.OBJECT);
				} else if("3".equals(context.contextMap.get("TAX_PLAN_CODE"))||"4".equals(context.contextMap.get("TAX_PLAN_CODE"))||"5".equals(context.contextMap.get("TAX_PLAN_CODE"))) {//加入增值税内含方案 add by ShenQi
					totalFeeSet=(Double)DataAccessor.query("rentContract.sumTotalFeeByRectID",context.contextMap, DataAccessor.RS_TYPE.OBJECT);
				}
				
				rentContract.put("FEESET_TOTAL", totalFeeSet);
				//-----------------------------------------------------------------------------
				//
				List<Map> oldPaylists = (List<Map>) DataAccessor.query("rentContractPact.queryPaylistByRectId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				//
				Map paylist = StartPayService.createPaylist(context, rentContract,equipList,oldPaylists);
				paylist.put("PLEDGE_ENTER_AGRATE", context.contextMap.get("PLEDGE_ENTER_AGRATE"));
				paylist.put("PLEDGE_ENTER_MCTOAG", context.contextMap.get("PLEDGE_ENTER_MCTOAG"));
				paylist.put("PLEDGE_ENTER_MCTOAGRATE", context.contextMap.get("PLEDGE_ENTER_MCTOAGRATE"));
				// Add By Michael 2012 1/5 增加TR、IRR_TR、利差 RATE_DIFF,这三项不重新计算
				paylist.put("TR_RATE", context.contextMap.get("TR_RATE"));
				paylist.put("TR_IRR_RATE", context.contextMap.get("TR_IRR_RATE"));
				paylist.put("RATE_DIFF", context.contextMap.get("RATE_DIFF"));
				paylist.put("DEFER_PERIOD", context.contextMap.get("DEFER_PERIOD"));
				
				//---------------------------------------------------------------------

				//Add by Michael 2012 09-21 增加税费测算方案
				paylist.put("TAX_PLAN_CODE", context.contextMap.get("TAX_PLAN_CODE"));
				paylist.put("PLEDGE_LAST_PRICE_TAX", context.contextMap.get("PLEDGE_LAST_PRICE_TAX"));
				
				if("5".equals(paylist.get("TAX_PLAN_CODE"))) {
					Map param=new HashMap();
					param.put("credit_id",rentContract.get("PRCD_ID"));
					Map resultMap=(Map)DataAccessor.query("creditReportManage.selectCreditScheme", param, DataAccessor.RS_TYPE.MAP);
					outputMap.put("SALES_PAY",resultMap.get("SALES_PAY"));
					outputMap.put("INCOME_PAY",resultMap.get("INCOME_PAY"));
					outputMap.put("OUT_PAY",resultMap.get("OUT_PAY"));
				}
				outputMap.put("taxPlanList", DictionaryUtil.getDictionary("税费方案"));
				
				//Add by Michael 2012 01/14 For 方案费用查询 影响概算成本为1 不影响为0
				List feeListRZE=null;
				feeListRZE = (List) DataAccessor.query("rentContract.getCreditFeeListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("feeListRZE", feeListRZE);
				List feeList=null;
				feeList = (List) DataAccessor.query("rentContract.getCreditFeeList",context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("feeList", feeList);	
				
				//费用设定明细 影响概算成本为1 不影响为0
				List feeSetListRZE=null;
				feeSetListRZE = (List) DataAccessor.query("rentContract.getFeeSetListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("feeSetListRZE", feeSetListRZE);
				List feeSetList=null;
				feeSetList = (List) DataAccessor.query("rentContract.getFeeSetList",context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("feeSetList", feeSetList);
				//-------------------------------------------------------------------	
				
				paylist.put("SUPL_TRUE", context.contextMap.get("SUPL_TRUE"));
				outputMap.put("paylist", paylist);
				//胡昭卿加
				context.contextMap.put("FIRST_PAYDATE", HTMLUtil.parseDateParam(String.valueOf(context.contextMap.get("FIRST_PAYDATE")), null));
				paylist.put("FIRST_PAYDATE", context.contextMap.get("FIRST_PAYDATE"));
				//胡昭卿加
				context.request.getSession().setAttribute("s_paylist", paylist);
				//
				outputMap.put("payWays", DictionaryUtil.getDictionary("支付方式"));
				outputMap.put("dealWays", DictionaryUtil.getDictionary("租赁期满处理方式"));
				
				suplList=(List)DictionaryUtil.getDictionary("供应商保证");
				outputMap.put("suplList", suplList);
				
				//Add by Michael 2012 12-20  增加费用来源
				outputMap.put("feeSourceList", DictionaryUtil.getDictionary("费用来源"));
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		
		}
		
		if (errList.isEmpty()) {
			//Output.jspOutput(outputMap, context, "/collection/createCalculateView.jsp");
			Output.jspOutput(outputMap, context, "/collection/createCalculateView2.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/*
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void createCalculateForRepayMent(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		Map rentContract = null;
		List<Map> equipList = null;
		
		List<Map> insureCompanyList = null;
		List<Map> insureTypeList = null;
		List suplList=null;
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {

				
				
				rentContract = (Map) DataAccessor.query("rentContract.readRentContractByRectId", context.contextMap, DataAccessor.RS_TYPE.MAP);
				
				String[] recdIds = HTMLUtil.getParameterValues(context.request, "RECD_ID", "0");
				Map recdMap = new HashMap();
				recdMap.put("recdIds", recdIds);
				equipList = (List<Map>) DataAccessor.query("collectionManage.queryEquipmentByIds", recdMap, DataAccessor.RS_TYPE.LIST);
				
				insureCompanyList = (List<Map>) DataAccessor.query("insuCompany.queryInsureCompanyListForSelect", null, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureCompanyList", insureCompanyList);
				
				insureTypeList = (List<Map>) DataAccessor.query("insureType.queryInsureTypeList", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureTypeList", insureTypeList);
				outputMap.put("insureTypeJsonList", Output.serializer.serialize(insureTypeList));

				//add by Michael 把管理费收入总和传过来，计算营业税收入，会影响TR计算----------------------
				double totalFeeSet=0.0d;
				
				if(Constants.TAX_PLAN_CODE_2.equals(context.contextMap.get("TAX_PLAN_CODE"))){
					List<Map> listTotalFeeSet=(List) DataAccessor.query("rentContract.getTotalFeeByRectID",context.contextMap, DataAccessor.RS_TYPE.LIST);
					for(Map map:listTotalFeeSet){
						totalFeeSet+=new BigDecimal(DataUtil.doubleUtil(map.get("FEE"))/1.06).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
					}	
				}else if(Constants.TAX_PLAN_CODE_1.equals(context.contextMap.get("TAX_PLAN_CODE"))){
					totalFeeSet=(Double)DataAccessor.query("rentContract.sumTotalFeeByRectID",context.contextMap, DataAccessor.RS_TYPE.OBJECT);
				} else if(Constants.TAX_PLAN_CODE_3.equals(context.contextMap.get("TAX_PLAN_CODE"))||Constants.TAX_PLAN_CODE_4.equals(context.contextMap.get("TAX_PLAN_CODE"))||Constants.TAX_PLAN_CODE_5.equals(context.contextMap.get("TAX_PLAN_CODE"))
						||Constants.TAX_PLAN_CODE_6.equals(context.contextMap.get("TAX_PLAN_CODE"))||Constants.TAX_PLAN_CODE_7.equals(context.contextMap.get("TAX_PLAN_CODE"))||Constants.TAX_PLAN_CODE_8.equals(context.contextMap.get("TAX_PLAN_CODE"))) {//加入增值税内含方案 add by ShenQi
					totalFeeSet=(Double)DataAccessor.query("rentContract.sumTotalFeeByRectID",context.contextMap, DataAccessor.RS_TYPE.OBJECT);
				}
				
				rentContract.put("FEESET_TOTAL", totalFeeSet);
				//-----------------------------------------------------------------------------
				//
				List<Map> oldPaylists = (List<Map>) DataAccessor.query("rentContractPact.queryPaylistByRectId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				//
				Map paylist = StartPayService.createPaylist(context, rentContract,equipList,oldPaylists);
				paylist.put("PLEDGE_ENTER_AGRATE", context.contextMap.get("PLEDGE_ENTER_AGRATE"));
				paylist.put("PLEDGE_ENTER_MCTOAG", context.contextMap.get("PLEDGE_ENTER_MCTOAG"));
				paylist.put("PLEDGE_ENTER_MCTOAGRATE", context.contextMap.get("PLEDGE_ENTER_MCTOAGRATE"));
				// Add By Michael 2012 1/5 增加TR、IRR_TR、利差 RATE_DIFF,这三项不重新计算
				paylist.put("TR_RATE", context.contextMap.get("TR_RATE"));
				paylist.put("TR_IRR_RATE", context.contextMap.get("TR_IRR_RATE"));
				paylist.put("RATE_DIFF", context.contextMap.get("RATE_DIFF"));
				paylist.put("DEFER_PERIOD", context.contextMap.get("DEFER_PERIOD"));
				
				//---------------------------------------------------------------------

				//Add by Michael 2012 09-21 增加税费测算方案
				paylist.put("TAX_PLAN_CODE", context.contextMap.get("TAX_PLAN_CODE"));
				paylist.put("PLEDGE_LAST_PRICE_TAX", context.contextMap.get("PLEDGE_LAST_PRICE_TAX"));
				
				if(Constants.TAX_PLAN_CODE_5.equals(paylist.get("TAX_PLAN_CODE"))) {
					Map param=new HashMap();
					param.put("credit_id",rentContract.get("PRCD_ID"));
					Map resultMap=(Map)DataAccessor.query("creditReportManage.selectCreditScheme", param, DataAccessor.RS_TYPE.MAP);
					outputMap.put("SALES_PAY",resultMap.get("SALES_PAY"));
					outputMap.put("INCOME_PAY",resultMap.get("INCOME_PAY"));
					outputMap.put("OUT_PAY",resultMap.get("OUT_PAY"));
				}
				outputMap.put("taxPlanList", DictionaryUtil.getDictionary("税费方案"));
				
				//Add by Michael 2012 01/14 For 方案费用查询 影响概算成本为1 不影响为0
				List feeListRZE=null;
				feeListRZE = (List) DataAccessor.query("rentContract.getCreditFeeListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("feeListRZE", feeListRZE);
				List feeList=null;
				feeList = (List) DataAccessor.query("rentContract.getCreditFeeList",context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("feeList", feeList);	
				
				//费用设定明细 影响概算成本为1 不影响为0
				List feeSetListRZE=null;
				feeSetListRZE = (List) DataAccessor.query("rentContract.getFeeSetListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("feeSetListRZE", feeSetListRZE);
				List feeSetList=null;
				feeSetList = (List) DataAccessor.query("rentContract.getFeeSetList",context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("feeSetList", feeSetList);
				//-------------------------------------------------------------------	
				
				paylist.put("SUPL_TRUE", context.contextMap.get("SUPL_TRUE"));
				outputMap.put("paylist", paylist);
				//胡昭卿加
				context.contextMap.put("FIRST_PAYDATE", HTMLUtil.parseDateParam(String.valueOf(context.contextMap.get("FIRST_PAYDATE")), null));
				paylist.put("FIRST_PAYDATE", context.contextMap.get("FIRST_PAYDATE"));

				//
				outputMap.put("payWays", DictionaryUtil.getDictionary("支付方式"));
				outputMap.put("dealWays", DictionaryUtil.getDictionary("租赁期满处理方式"));
				
				suplList=(List)DictionaryUtil.getDictionary("供应商保证");
				outputMap.put("suplList", suplList);
				
				//Add by Michael 2012 12-20  增加费用来源
				outputMap.put("feeSourceList", DictionaryUtil.getDictionary("费用来源"));
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		
		}
		outputMap.put("FIRST_PAYDATE", context.contextMap.get("FIRST_PAYDATE"));
		outputMap.put("START_DATE", context.contextMap.get("START_DATE"));
		outputMap.put("RECT_ID", context.contextMap.get("RECT_ID"));
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/collection/createCalculateView.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	/**
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void createRecalculate(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		Map rentContract = null;
		List<Map> equipList = null;
		
		List<Map> insureCompanyList = null;
		List<Map> insureTypeList = null;
		List payWays = null;
		List dealWays = null;
		List suplList=null;
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {
				
				rentContract = (Map) DataAccessor.query("rentContract.readRentContractByRectId", context.contextMap, DataAccessor.RS_TYPE.MAP);
				
				String[] recdIds = HTMLUtil.getParameterValues(context.request, "RECD_ID", "0");
				Map recdMap = new HashMap();
				recdMap.put("recdIds", recdIds);
				equipList = (List<Map>) DataAccessor.query("collectionManage.queryEquipmentByIds", recdMap, DataAccessor.RS_TYPE.LIST);
				
				insureCompanyList = (List<Map>) DataAccessor.query("insuCompany.queryInsureCompanyListForSelect", null, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureCompanyList", insureCompanyList);
				
				insureTypeList = (List<Map>) DataAccessor.query("insureType.queryInsureTypeList", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureTypeList", insureTypeList);
				outputMap.put("insureTypeJsonList", Output.serializer.serialize(insureTypeList));
				//
				List<Map> oldPaylists = (List<Map>) DataAccessor.query("rentContractPact.queryPaylistByRectId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				//
				Map paylist = StartPayService.createPaylistByRecalculate(context, rentContract, equipList,oldPaylists);
				paylist.put("SUPL_TRUE", context.contextMap.get("SUPL_TRUE"));
				outputMap.put("paylist", paylist);
				
				paylist.put("DEFER_PERIOD", context.contextMap.get("DEFER_PERIOD"));
				
				context.request.getSession().setAttribute("s_paylist", paylist);
				//
				Map dataDictionaryMap = new HashMap();
				dataDictionaryMap.put("dataType", "支付方式");
				payWays = (List) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("payWays", payWays);
				
				dataDictionaryMap.put("dataType", "租赁期满处理方式");
				dealWays = (List) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("dealWays", dealWays);
				
				suplList=(List)DictionaryUtil.getDictionary("供应商保证");
				outputMap.put("suplList", suplList);
				
				//Add by Michael 2012 12-20  增加费用来源
				outputMap.put("feeSourceList", DictionaryUtil.getDictionary("费用来源"));
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		
		}
		
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/collection/createCalculateView.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
	
	}
	
	/**
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void saveCalculate(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		Long recpId = 0l;
		
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {
				DataAccessor.getSession().startTransaction();
				
				Map paylist = (Map) context.request.getSession().getAttribute("s_paylist");
				paylist.put("S_EMPLOYEEID", context.contextMap.get("s_employeeId"));
				paylist.put("VERSION_CODE", Integer.valueOf(1));
				paylist.put("PAYDATE_FLAG", Integer.valueOf(0));
				//
				recpId = (Long)DataAccessor.getSession().insert("collectionManage.createPaylist", paylist);
				paylist.put("RECP_ID", recpId);
				
				//Modify by Michael 2012 09-25 根据税费方案类别选择不同的支付表明细保存方案
				if("1".equals(context.contextMap.get("TAX_PLAN_CODE"))||"3".equals(context.contextMap.get("TAX_PLAN_CODE"))||"5".equals(context.contextMap.get("TAX_PLAN_CODE"))){
					operatePayline(paylist);
				}else if("2".equals(context.contextMap.get("TAX_PLAN_CODE"))){
					operatePaylineByValueAdded(paylist);
				}else if("4".equals(context.contextMap.get("TAX_PLAN_CODE"))) {
					operatePayline1(paylist);
				}
				
				operateEquipment(paylist);
				
				operateInsure(paylist);
				
				operateOtherFee(paylist);
				
				operateCollectionIrrMonthPrice(paylist);
				
				DataAccessor.getSession().commitTransaction();
				
				//
				context.contextMap.put("RECP_CODE", paylist.get("RECP_CODE"));
				context.contextMap.put("RECT_ID", paylist.get("RECT_ID"));
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} finally {
				//
				context.request.getSession().removeAttribute("s_paylist");
				
				try {
					DataAccessor.getSession().endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
			}
		
		}
		
		if (errList.isEmpty()) {
			Output.jspSendRedirect(context, "defaultDispatcher?__action=collectionManage.showPaylist&FLAG=0&RECP_ID="+recpId+"&RECT_ID="+context.contextMap.get("RECT_ID"));
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	
	}
	
	public void showPayListByCredit(Context context) throws SQLException{
		String creditId = (String) context.contextMap.get("creditId");
		String rectId = LeaseUtil.getRectIdByCreditId(creditId);
		String recpId = LeaseUtil.getRecpIdByCreditId(creditId);
		context.contextMap.put("FLAG", "1");
		context.contextMap.put("RECP_ID", recpId);
		context.contextMap.put("RECT_ID", rectId);
		showPaylist(context);
	}
	
	/**
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void showPaylist(Context context) {

		Map outputMap = new HashMap();
		List errList = context.errList;
		
		Map paylist = null;
		List<Map> payEquipments = null;
		List<Map> payInusres = null;
		List<Map> payOtherFees = null;
		List<Map> paylines = null;
		
		List<Map> insureCompanyList = null;
		List<Map> insureTypeList = null;
		List<Map> payWays = null;
		List<Map> dealWays = null;
		List suplList=null;
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {
				paylist = (Map) DataAccessor.query("collectionManage.readPaylistById", context.contextMap, DataAccessor.RS_TYPE.MAP);
				//
				if(paylist!=null&&"5".equals(paylist.get("TAX_PLAN_CODE"))) {
					Map param=new HashMap();
					param.put("credit_id",paylist.get("PRCD_ID"));
					Map resultMap=(Map)DataAccessor.query("creditReportManage.selectCreditScheme", param, DataAccessor.RS_TYPE.MAP);
					outputMap.put("SALES_PAY",resultMap.get("SALES_PAY"));
					outputMap.put("INCOME_PAY",resultMap.get("INCOME_PAY"));
					outputMap.put("OUT_PAY",resultMap.get("OUT_PAY"));
				}
				payEquipments = (List<Map>) DataAccessor.query("collectionManage.readPayEquipments", context.contextMap, DataAccessor.RS_TYPE.LIST);
				paylist.put("payEquipments", payEquipments);
				//
				payInusres = (List<Map>) DataAccessor.query("collectionManage.readPayInusres", context.contextMap, DataAccessor.RS_TYPE.LIST);
				paylist.put("payInusres", payInusres);
				//
				payOtherFees = (List<Map>) DataAccessor.query("collectionManage.readPayOtherFees", context.contextMap, DataAccessor.RS_TYPE.LIST);
				paylist.put("payOtherFees", payOtherFees);
				//
				paylines = (List<Map>) DataAccessor.query("collectionManage.readPaylines", context.contextMap, DataAccessor.RS_TYPE.LIST);
				paylist.put("paylines", paylines);
				//
				List<Map> irrMonthPaylines = (List<Map>)DataAccessor.query("collectionManage.readCollectionplanSchemaIrrByRecpid", context.contextMap, DataAccessor.RS_TYPE.LIST);
				// 2010-09-26 wjw v1.6
				StartPayService.setIrrMonthPayline(paylist, irrMonthPaylines);
				
				outputMap.put("paylist", paylist);
				
				insureCompanyList = (List<Map>) DataAccessor.query("insuCompany.queryInsureCompanyListForSelect", null, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureCompanyList", insureCompanyList);
				insureTypeList = (List<Map>) DataAccessor.query("insureType.queryInsureTypeList", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureTypeList", insureTypeList);
				
				Map dataDictionaryMap = new HashMap();
				dataDictionaryMap.put("dataType", "支付方式");
				payWays = (List) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("payWays", payWays);
				
				dataDictionaryMap.put("dataType", "租赁期满处理方式");
				dealWays = (List) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("dealWays", dealWays);
				
				dataDictionaryMap.put("dataType", "锁码方式");
				outputMap.put("lockList",DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST));
				
				suplList=(List)DictionaryUtil.getDictionary("供应商保证");
				outputMap.put("suplList", suplList);
				
				//Add by Michael 2012 01/14 For 方案费用查询 影响概算成本为1 不影响为0
				List feeListRZE=null;
				feeListRZE = (List) DataAccessor.query("rentContract.getCreditFeeListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("feeListRZE", feeListRZE);
				List feeList=null;
				feeList = (List) DataAccessor.query("rentContract.getCreditFeeList",context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("feeList", feeList);	
				
				//费用设定明细 影响概算成本为1 不影响为0
				List feeSetListRZE=null;
				feeSetListRZE = (List) DataAccessor.query("rentContract.getFeeSetListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("feeSetListRZE", feeSetListRZE);
				List feeSetList=null;
				feeSetList = (List) DataAccessor.query("rentContract.getFeeSetList",context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("feeSetList", feeSetList);
				//-------------------------------------------------------------------	
				
				//Add by Michael 2012 3-23 增加实际剩余本金信息----------------------------
				/*Map settleMap = null;
				context.contextMap.put("zujin", "租金") ;				
				context.contextMap.put("zujinfaxi", "租金罚息") ;
				context.contextMap.put("sblgj", "设备留购价") ;
				settleMap = (Map) DataAccessor.query("settleManage.selectSettlePrice", context.contextMap,DataAccessor.RS_TYPE.MAP);*/
				Map<String,Object> settleMap=new HashMap<String,Object>();
				/*settleMap=(Map<String,Object>)DataAccessor.query("settleManage.getContractPlan",context.contextMap,DataAccessor.RS_TYPE.MAP);
				if("1".equals(settleMap.get("RECP_STATUS").toString())||"3".equals(settleMap.get("RECP_STATUS").toString())) {//结清与提前结清
					settleMap.put("SUM_OWN_PRICE",0);
				} else {
					if("4".equals(settleMap.get("TAX_PLAN_CODE").toString())) {//直租类型
						settleMap.put("SUM_OWN_PRICE",DataAccessor.query("settleManage.getRealOwnPriceForDirect",context.contextMap,DataAccessor.RS_TYPE.OBJECT));
					} else {//非直租类型
						settleMap.put("SUM_OWN_PRICE",DataAccessor.query("settleManage.getRealOwnPriceForNotDirect",context.contextMap,DataAccessor.RS_TYPE.OBJECT));
					}
				}*/
				settleMap=(Map<String,Object>)DataAccessor.query("settleManage.getContractPlan",context.contextMap,DataAccessor.RS_TYPE.MAP);
				if("1".equals(settleMap.get("RECP_STATUS").toString())||"3".equals(settleMap.get("RECP_STATUS").toString())) {//结清与提前结清
					settleMap.put("SUM_OWN_PRICE",0);
				} else {
					context.contextMap.put("recpId",context.contextMap.get("RECP_ID"));
					settleMap.put("SUM_OWN_PRICE",DataAccessor.query("rentFinance.getSettlementOwnPrice",context.contextMap,DataAccessor.RS_TYPE.OBJECT));
				}
				outputMap.put("settleMap",settleMap) ;
				//-------------------------------------------------------------------
				
				//Add by Michael 2012 12-20  增加费用来源
				outputMap.put("feeSourceList", DictionaryUtil.getDictionary("费用来源"));
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		
		/*if(paylist!=null&&"7".equals(paylist.get("TAX_PLAN_CODE"))) {
			DecimalFormat df=new DecimalFormat(".##");
			double renWithoutTax=0;
			double ownWithoutTax=0;
			double owntax=0;
			double tax=0;
				for(int i=0;i<paylines.size();i++) {
					renWithoutTax=Double.valueOf(df.format(Double.valueOf(paylines.get(i).get("REN_PRICE").toString())/1.17));
					ownWithoutTax=Double.valueOf(df.format(Double.valueOf(paylines.get(i).get("OWN_PRICE").toString())/1.17));
					tax=Double.valueOf(df.format(Double.valueOf(paylines.get(i).get("REN_PRICE").toString())))-renWithoutTax;
					owntax=Double.valueOf(df.format(Double.valueOf(paylines.get(i).get("OWN_PRICE").toString())))-ownWithoutTax;
					paylines.get(i).put("OWN_PRICE",df.format(Double.valueOf(paylines.get(i).get("OWN_PRICE").toString())));
					paylines.get(i).put("REN_PRICE",df.format(Double.valueOf(paylines.get(i).get("REN_PRICE").toString())));
					paylines.get(i).put("RENWITHOUTTAX",renWithoutTax);
					paylines.get(i).put("TAX",tax);
					paylines.get(i).put("OWNWITHOUTTAX",ownWithoutTax);
					paylines.get(i).put("OWNTAX",owntax);
				}
		}*/
		outputMap.put("FLAG", context.contextMap.get("FLAG"));
		outputMap.put("RECP_ID", context.contextMap.get("RECP_ID"));
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/collection/showPaylist.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	
	}
	/**
	 * insure
	 * @param paylist
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public static void operatePayline(Map paylist) throws SQLException {
		
		List<Map> paylines = (List<Map>) paylist.get("paylines");
		
		HashMap map = new HashMap();
		map.put("RECP_ID", paylist.get("RECP_ID"));
		map.put("S_EMPLOYEEID", paylist.get("S_EMPLOYEEID"));

		//Add by Michael 2012 02/01  增加 FinanceDate----------------------------
		try {
			Date firstPayDate= DataUtil.dateUtil(paylist.get("FIRST_PAYDATE"), "yyyy-MM-dd");	   
			Calendar financeDate = Calendar.getInstance();
			financeDate.setTime(firstPayDate);
			int index = 0;
			int leaseTerm = DataUtil.intUtil(paylist.get("LEASE_TERM"));
			//-------------------------------------------------------------------
			
			DataAccessor.getSession().startBatch();
			
			for (Map payline : paylines) {
				map.put("LOCKED", payline.get("LOCKED"));
				map.put("PERIOD_NUM", payline.get("PERIOD_NUM"));
				map.put("PAY_DATE", payline.get("PAY_DATE"));
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
				//-----------------------------------------------------------------			
				
				//Add by Michael 2012 02/01  增加 FinanceDate--------------
	        	financeDate.setTime(firstPayDate);
	        	int num = 0 ;
	        	if(payline.get("PERIOD_NUM") != null){
	        		num = Integer.parseInt(payline.get("PERIOD_NUM").toString()) ;
	        	}
	        	if (index != 0) {
	        		financeDate.add(Calendar.MONTH, leaseTerm * (num - 1) );
	        	}    		
	        	map.put("FINANCE_DATE", financeDate.getTime());
	        	index ++;
	    		//---------------------------------------------------------------
				
				DataAccessor.getSession().insert("collectionManage.createPaylines", map);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}     	
		DataAccessor.getSession().executeBatch();
	
	}
	
	//如果选择新税费方案吧,  这里代码实在太乱, 直接硬改了。。。
	public static void operatePayline1(Map paylist) throws SQLException {
		
		List<Map> paylines = (List<Map>) paylist.get("paylines");
		
		HashMap map = new HashMap();
		map.put("RECP_ID", paylist.get("RECP_ID"));
		map.put("S_EMPLOYEEID", paylist.get("S_EMPLOYEEID"));

		try {
			Date firstPayDate= DataUtil.dateUtil(paylist.get("FIRST_PAYDATE"), "yyyy-MM-dd");	   
			Calendar financeDate = Calendar.getInstance();
			financeDate.setTime(firstPayDate);
			int index = 0;
			int leaseTerm = DataUtil.intUtil(paylist.get("LEASE_TERM"));
			//-------------------------------------------------------------------
			
			DataAccessor.getSession().startBatch();
			
			for (Map payline : paylines) {
				map.put("LOCKED", payline.get("LOCKED"));
				map.put("PERIOD_NUM", payline.get("PERIOD_NUM"));
				map.put("PAY_DATE", payline.get("PAY_DATE"));
				map.put("MONTH_PRICE", payline.get("MONTH_PRICE")==null||"".equals(payline.get("MONTH_PRICE"))?0:payline.get("MONTH_PRICE"));
				map.put("OWN_PRICE", payline.get("OWN_PRICE")==null||"".equals(payline.get("OWN_PRICE"))?0:payline.get("OWN_PRICE"));
				map.put("REN_PRICE", payline.get("REN_PRICE")==null||"".equals(payline.get("REN_PRICE"))?0:payline.get("REN_PRICE"));
				map.put("LAST_PRICE", payline.get("LAST_PRICE")==null||"".equals(payline.get("LAST_PRICE"))?0:payline.get("LAST_PRICE"));
				map.put("DEPOSIT_PRICE", payline.get("DEPOSIT_PRICE")==null||"".equals(payline.get("DEPOSIT_PRICE"))?0:payline.get("DEPOSIT_PRICE"));
				map.put("IRR_PRICE", payline.get("IRR_PRICE")==null||"".equals(payline.get("IRR_PRICE"))?0:payline.get("IRR_PRICE"));
				map.put("IRR_MONTH_PRICE", payline.get("IRR_MONTH_PRICE")==null||"".equals(payline.get("IRR_MONTH_PRICE"))?0:payline.get("IRR_MONTH_PRICE"));
				map.put("SALES_TAX", payline.get("SALES_TAX")==null||"".equals(payline.get("SALES_TAX"))?0:payline.get("SALES_TAX"));
				map.put("INSURE_PRICE", payline.get("INSURE_PRICE")==null||"".equals(payline.get("INSURE_PRICE"))?0:payline.get("INSURE_PRICE"));
				map.put("REAL_OWN_PRICE", payline.get("REAL_OWN_PRICE")==null||"".equals(payline.get("REAL_OWN_PRICE"))?0:payline.get("REAL_OWN_PRICE"));
				map.put("NETCURRENTFINANCE", payline.get("NETCURRENTFINANCE")==null||"".equals(payline.get("NETCURRENTFINANCE"))?0:payline.get("NETCURRENTFINANCE"));
				map.put("CURRENTRENPRICE", payline.get("CURRENTRENPRICE")==null||"".equals(payline.get("CURRENTRENPRICE"))?0:payline.get("CURRENTRENPRICE"));
				map.put("NETFINANCE", payline.get("LAST_PRICE")==null||"".equals(payline.get("LAST_PRICE"))?0:payline.get("LAST_PRICE"));
				map.put("CURRENTFINANCECOSTREN", payline.get("CURRENTFINANCECOSTREN")==null||"".equals(payline.get("CURRENTFINANCECOSTREN"))?0:payline.get("CURRENTFINANCECOSTREN"));
				map.put("PV_PRICE", payline.get("PV_PRICE")==null||"".equals(payline.get("PV_PRICE"))?0:payline.get("PV_PRICE"));
				//-----------------------------------------------------------------			
				
	        	financeDate.setTime(firstPayDate);
	        	int num = 0 ;
	        	if(payline.get("PERIOD_NUM") != null){
	        		num = Integer.parseInt(payline.get("PERIOD_NUM").toString()) ;
	        	}
	        	if (index != 0) {
	        		financeDate.add(Calendar.MONTH, leaseTerm * (num - 1) );
	        	}    		
	        	map.put("FINANCE_DATE", financeDate.getTime());
	        	index ++;
	    		//---------------------------------------------------------------
				
				DataAccessor.getSession().insert("collectionManage.createPaylines", map);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}     	
		DataAccessor.getSession().executeBatch();
	
	}
	/*
	 * Add by Michael 2012 09-25 
	 * 如果选择的是增值税方案的话，则有此方法进行支付表
	 */
	@SuppressWarnings("unchecked")
	public static void operatePaylineByValueAdded(Map paylist) throws SQLException {
		
		List<Map> paylines = (List<Map>) paylist.get("paylines");
		
		HashMap map = new HashMap();
		map.put("RECP_ID", paylist.get("RECP_ID"));
		map.put("S_EMPLOYEEID", paylist.get("S_EMPLOYEEID"));

		//Add by Michael 2012 02/01  增加 FinanceDate----------------------------
		try {
			Date firstPayDate= DataUtil.dateUtil(paylist.get("FIRST_PAYDATE"), "yyyy-MM-dd");	   
			Calendar financeDate = Calendar.getInstance();
			financeDate.setTime(firstPayDate);
			int index = 0;
			int leaseTerm = DataUtil.intUtil(paylist.get("LEASE_TERM"));
			//-------------------------------------------------------------------
			
			DataAccessor.getSession().startBatch();
			
			for (Map payline : paylines) {
				map.put("LOCKED", payline.get("LOCKED"));
				map.put("PERIOD_NUM", payline.get("PERIOD_NUM"));
				map.put("PAY_DATE", payline.get("PAY_DATE"));
				map.put("MONTH_PRICE", payline.get("MONTH_PRICE"));
				map.put("OWN_PRICE", payline.get("OWN_PRICE"));
				map.put("REN_PRICE", payline.get("REN_PRICE"));
				map.put("LAST_PRICE", payline.get("LAST_PRICE"));
				map.put("DEPOSIT_PRICE", payline.get("DEPOSIT_PRICE"));
				map.put("IRR_PRICE", payline.get("IRR_PRICE"));
				map.put("IRR_MONTH_PRICE", payline.get("IRR_MONTH_PRICE"));
				//将营业税改为增值税
				map.put("VALUE_ADDED_TAX", payline.get("VALUE_ADDED_TAX"));
				//实际每期增值税
				map.put("VALUE_ADDED_TAX_TRUE", payline.get("VALUE_ADDED_TAX_TRUE"));
				
				map.put("INSURE_PRICE", payline.get("INSURE_PRICE"));
				map.put("REAL_OWN_PRICE", payline.get("REAL_OWN_PRICE"));
				//Add by Michael 2012 01/16 当期本金 当期利息 当期本金余额----------------------------------------
				map.put("NETCURRENTFINANCE", payline.get("NETCURRENTFINANCE"));
				map.put("CURRENTRENPRICE", payline.get("CURRENTRENPRICE"));
				map.put("NETFINANCE", payline.get("NETFINANCE"));  //前期净本金余额	
				map.put("CURRENTFINANCECOSTREN", payline.get("CURRENTFINANCECOSTREN"));  //当期资金成本息	
				map.put("PV_PRICE", payline.get("PV_PRICE"));
				//-----------------------------------------------------------------			
				
				//Add by Michael 2012 02/01  增加 FinanceDate--------------
	        	financeDate.setTime(firstPayDate);
	        	int num = 0 ;
	        	if(payline.get("PERIOD_NUM") != null){
	        		num = Integer.parseInt(payline.get("PERIOD_NUM").toString()) ;
	        	}
	        	if (index != 0) {
	        		financeDate.add(Calendar.MONTH, leaseTerm * (num - 1) );
	        	}    		
	        	map.put("FINANCE_DATE", financeDate.getTime());
	        	index ++;
	    		//---------------------------------------------------------------
				
				DataAccessor.getSession().insert("collectionManage.createPaylinesByValueAdded", map);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}     	
		DataAccessor.getSession().executeBatch();
	
	}
	
	
	/**
	 * equipment
	 * @param paylist
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public static void operateEquipment(Map paylist) throws SQLException {
		
		List<Map> payEquipments = (List<Map>) paylist.get("payEquipments");
		HashMap map = new HashMap();
		map.put("RECP_ID", paylist.get("RECP_ID"));
		map.put("S_EMPLOYEEID", paylist.get("S_EMPLOYEEID"));
		
		DataAccessor.getSession().startBatch();
		
		for(Map equip : payEquipments) {
			map.put("RECD_ID", equip.get("RECD_ID"));
			DataAccessor.getSession().update("collectionManage.updateRentContractDetail", map);
		}
		DataAccessor.getSession().executeBatch();
	}
	/**
	 * insure
	 * @param paylist
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public static void operateInsure(Map paylist) throws SQLException {
		
		List<Map> payInusres = (List<Map>) paylist.get("payInusres");
		
		HashMap map = new HashMap();
		map.put("RECP_ID", paylist.get("RECP_ID"));
		map.put("S_EMPLOYEEID", paylist.get("S_EMPLOYEEID"));

		DataAccessor.getSession().startBatch();
		for (Map insure : payInusres) {
			map.put("INSURE_ITEM", insure.get("INSURE_ITEM"));
			map.put("START_DATE", insure.get("START_DATE"));
			map.put("END_DATE", insure.get("END_DATE"));
			map.put("INSURE_RATE", insure.get("INSURE_RATE"));
			map.put("INSURE_PRICE", insure.get("INSURE_PRICE"));
			map.put("MEMO", insure.get("MEMO"));
			DataAccessor.getSession().insert("collectionManage.createPayInsures", map);
		}
		
		DataAccessor.getSession().executeBatch();

	}
	/**
	 * insure
	 * @param paylist
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public static void operateOtherFee(Map paylist) throws SQLException {
		
		List<Map> payOtherFees = (List<Map>) paylist.get("payOtherFees");
		
		HashMap map = new HashMap();
		map.put("RECP_ID", paylist.get("RECP_ID"));
		map.put("S_EMPLOYEEID", paylist.get("S_EMPLOYEEID"));

		DataAccessor.getSession().startBatch();
		
		for (Map otherFee : payOtherFees) {
			map.put("OTHER_NAME", otherFee.get("OTHER_NAME"));
			map.put("OTHER_DATE", otherFee.get("OTHER_DATE"));
			map.put("OTHER_PRICE", otherFee.get("OTHER_PRICE"));
			map.put("MEMO", otherFee.get("MEMO"));
			DataAccessor.getSession().insert("collectionManage.createPayOtherFee", map);
		}
		
		DataAccessor.getSession().executeBatch();
	}
	/**
	 * 
	 * @param context
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public static void operateCollectionIrrMonthPrice(Map paylist) throws SQLException {
		
		List<Map> irrMonthPaylines = (List<Map>) paylist.get("irrMonthPaylines");
		
		HashMap map = new HashMap();
		map.put("RECP_ID", paylist.get("RECP_ID"));
		map.put("S_EMPLOYEEID", paylist.get("S_EMPLOYEEID"));

		DataAccessor.getSession().startBatch();
		
		for (Map irrMonthPayline : irrMonthPaylines) {
			map.put("IRR_MONTH_PRICE", irrMonthPayline.get("IRR_MONTH_PRICE"));
			map.put("IRR_MONTH_PRICE_START", irrMonthPayline.get("IRR_MONTH_PRICE_START"));
			map.put("IRR_MONTH_PRICE_END", irrMonthPayline.get("IRR_MONTH_PRICE_END"));
			DataAccessor.getSession().insert("collectionManage.createCollectionIrrMonthPrice", map);
		}
		
		DataAccessor.getSession().executeBatch();
	}
	
	@SuppressWarnings("unchecked")
	public void expPayToExcel(Context context){  
		String[] ids=context.request.getParameterValues("ids");
		
		context.contextMap.put("ids", ids);
		context.contextMap.put("pay_way_type", "支付方式");
		context.contextMap.put("deal_way_type", "租赁期满处理方式");
		context.contextMap.put("supl_true_type", "供应商保证");
		
		List paylists=null;	
		SqlMapClient client=null;
		try { 
			client=DataAccessor.getSession();
			client.startTransaction();
			paylists=(List)client.queryForList("collectionManage.paylistForExportExcel", context.contextMap);
			for (Object object : paylists) {
				Map paylist=(Map)object;
				List equipments=(List)client.queryForList("collectionManage.equipmentsForExportExcel", paylist.get("RECP_ID"));
				paylist.put("equipments", equipments);
				List insures=(List)client.queryForList("collectionManage.inusresForExportExcel", paylist.get("RECP_ID"));
				paylist.put("insures", insures);
				List otherfees=(List)client.queryForList("collectionManage.otherFeesForExportExcel", paylist.get("RECP_ID"));
				paylist.put("otherfees", otherfees);
				List paylines=(List)client.queryForList("collectionManage.paylinesForExportExcel", paylist.get("RECP_ID"));
				
				//Add by Michael 2012 01/14 For 方案费用查询 影响概算成本为1 不影响为0
				List feeListRZE=null;
				feeListRZE = (List) client.queryForList("collectionManage.getCreditFeeListRZE",paylist.get("RECT_ID"));
				paylist.put("feeListRZE", feeListRZE);
				List feeList=null;
				feeList = (List) client.queryForList("collectionManage.getCreditFeeList",paylist.get("RECT_ID"));
				paylist.put("feeList", feeList);
				//-------------------------------------------------------------------	
				
				//-- Modify by Michael 2012 02/02导出excel时不重新计算利差,直接取值 ---------------------------------
//				double pv_own_price=0;
//				for (Object object2 : paylines) {
//					Map payline=(Map)object2;
//					double rate=(DataUtil.doubleUtil(paylist.get("LOAN_RATE")==null?"0":paylist.get("LOAN_RATE").toString())+DataUtil.doubleUtil(paylist.get("MANAGE_RATE")==null?"0":paylist.get("MANAGE_RATE").toString()))/100;					
//					double loanrate=DataUtil.doubleUtil(paylist.get("LOAN_RATE"))/100;
//					if (Integer.parseInt(payline.get("PERIOD_NUM").toString()) == 1) {
//						
//						pv_own_price = DataUtil.doubleUtil(paylist.get("LEASE_RZE")==null?"0":paylist.get("LEASE_RZE").toString()) - DataUtil.doubleUtil(payline.get("IRR_MONTH_PRICE")==null?"0":payline.get("IRR_MONTH_PRICE").toString()) +DataUtil.doubleUtil(payline.get("REN_PRICE"));
//					
//					} else {
//						
//						pv_own_price =pv_own_price-DataUtil.doubleUtil(payline.get("IRR_MONTH_PRICE")==null?"0":payline.get("IRR_MONTH_PRICE").toString()) +DataUtil.doubleUtil(payline.get("REN_PRICE"));;
//					
//					}
//					if (Integer.parseInt(payline.get("PERIOD_NUM").toString()) == Integer.parseInt(paylist.get("LEASE_PERIOD").toString())) {
//						
//						pv_own_price = 0;
//					
//					}
//					double costPrice = pv_own_price * rate / 12*Integer.parseInt(paylist.get("LEASE_TERM")==null?"1":paylist.get("LEASE_TERM").toString());
//					//利息
//					double ren_price = DataUtil.doubleUtil(payline.get("REN_PRICE"));
//					//营业税
//					double sales_tax = DataUtil.doubleUtil(payline.get("SALES_TAX"));
//
//					double pv_price=PVUtils.pv2(loanrate, Integer.parseInt(payline.get("PERIOD_NUM").toString()), ren_price - sales_tax -costPrice);
//					payline.put("PV_PRICE", pv_price);
//					//System.out.println("ren_price:"+ren_price+"sales_tax"+sales_tax+"costPrice"+costPrice+"rentprice"+(ren_price - sales_tax -costPrice));
//				}
				//-------------------------------------------------------------------	
				
				paylist.put("paylines", paylines);	
				context.contextMap.put("RECP_ID", paylist.get("RECP_ID"));
				List<Map> irrMonthPaylines = (List<Map>)DataAccessor.query("collectionManage.readCollectionplanSchemaIrrByRecpid", context.contextMap, DataAccessor.RS_TYPE.LIST);
				StartPayService.setIrrMonthPayline(paylist, irrMonthPaylines);
				//StartPayService.packagePaylines(paylist);
			}
			client.commitTransaction();
			client.endTransaction();
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			try {
				client.endTransaction();
			} catch (SQLException e1) { 
				e1.printStackTrace();
				LogPrint.getLogStackTrace(e1, logger);
			}
		}
		
		/*if(paylists!=null&&"7".equals(((Map)paylists.get(0)).get("TAX_PLAN_CODE"))) {
			DecimalFormat df=new DecimalFormat(".##");
			double renWithoutTax=0;
			double ownWithoutTax=0;
			double owntax=0;
			double tax=0;
			List<Map<String,Object>> loop=((List)((Map)paylists.get(0)).get("paylines"));
				for(int i=0;i<loop.size();i++) {
					renWithoutTax=Double.valueOf(df.format(Double.valueOf(loop.get(i).get("REN_PRICE").toString())/1.17));
					ownWithoutTax=Double.valueOf(df.format(Double.valueOf(loop.get(i).get("OWN_PRICE").toString())/1.17));
					tax=Double.valueOf(df.format(Double.valueOf(loop.get(i).get("REN_PRICE").toString())))-renWithoutTax;
					owntax=Double.valueOf(df.format(Double.valueOf(loop.get(i).get("OWN_PRICE").toString())))-ownWithoutTax;
					loop.get(i).put("OWN_PRICE",df.format(Double.valueOf(loop.get(i).get("OWN_PRICE").toString())));
					loop.get(i).put("REN_PRICE",df.format(Double.valueOf(loop.get(i).get("REN_PRICE").toString())));
					loop.get(i).put("RENWITHOUTTAX",renWithoutTax);
					loop.get(i).put("TAX",tax);
					loop.get(i).put("OWNWITHOUTTAX",ownWithoutTax);
					loop.get(i).put("OWNTAX",owntax);
				}
		}*/
		
		ByteArrayOutputStream baos = null;
		String strFileName = "";
		if(paylists.size()==1){
			strFileName = ((Map)paylists.get(0)).get("RECP_CODE")+"";
		}else{
			strFileName = "支付表明细("+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+").xls";
		}
		ExportPaylistToExcel exl = new ExportPaylistToExcel();
		exl.createexl(); 
		baos = exl.exportExcel(paylists);
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1")+".xls");
			ServletOutputStream out1 = context.response.getOutputStream();
			exl.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {
			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		
	}
	
	//Add by Michael 2012 09-27 For增值税 税费方案
	public void expPayToExcelByValueAdded(Context context){  
		String[] ids=context.request.getParameterValues("ids");
		
		context.contextMap.put("ids", ids);
		context.contextMap.put("pay_way_type", "支付方式");
		context.contextMap.put("deal_way_type", "租赁期满处理方式");
		context.contextMap.put("supl_true_type", "供应商保证");
		
		List paylists=null;	
		SqlMapClient client=null;
		try { 
			client=DataAccessor.getSession();
			client.startTransaction();
			paylists=(List)client.queryForList("collectionManage.paylistForExportExcel", context.contextMap);
			for (Object object : paylists) {
				Map paylist=(Map)object;
				List equipments=(List)client.queryForList("collectionManage.equipmentsForExportExcel", paylist.get("RECP_ID"));
				paylist.put("equipments", equipments);
				List insures=(List)client.queryForList("collectionManage.inusresForExportExcel", paylist.get("RECP_ID"));
				paylist.put("insures", insures);
				List otherfees=(List)client.queryForList("collectionManage.otherFeesForExportExcel", paylist.get("RECP_ID"));
				paylist.put("otherfees", otherfees);
				List paylines=(List)client.queryForList("collectionManage.paylinesForExportExcel", paylist.get("RECP_ID"));
				
				//Add by Michael 2012 01/14 For 方案费用查询 影响概算成本为1 不影响为0
				List feeListRZE=null;
				feeListRZE = (List) client.queryForList("collectionManage.getCreditFeeListRZE",paylist.get("RECT_ID"));
				paylist.put("feeListRZE", feeListRZE);
				List feeList=null;
				feeList = (List) client.queryForList("collectionManage.getCreditFeeList",paylist.get("RECT_ID"));
				paylist.put("feeList", feeList);
				//-------------------------------------------------------------------	

				paylist.put("paylines", paylines);	
				context.contextMap.put("RECP_ID", paylist.get("RECP_ID"));
				List<Map> irrMonthPaylines = (List<Map>)DataAccessor.query("collectionManage.readCollectionplanSchemaIrrByRecpid", context.contextMap, DataAccessor.RS_TYPE.LIST);
				StartPayService.setIrrMonthPayline(paylist, irrMonthPaylines);
				//StartPayService.packagePaylines(paylist);
			}
			client.commitTransaction();
			client.endTransaction();
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			try {
				client.endTransaction();
			} catch (SQLException e1) { 
				e1.printStackTrace();
				LogPrint.getLogStackTrace(e1, logger);
			}
		}
		
		ByteArrayOutputStream baos = null;
		String strFileName = "";
		if(paylists.size()==1){
			strFileName = ((Map)paylists.get(0)).get("RECP_CODE")+"";
		}else{
			strFileName = "支付表明细("+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+").xls";
		}
		ExportPaylistToExcel exl = new ExportPaylistToExcel();
		exl.createexl(); 
		baos = exl.exportExcelForValueAdded(paylists);
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1")+".xls");
			ServletOutputStream out1 = context.response.getOutputStream();
			exl.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {
			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		
	}
	
	
	@SuppressWarnings("unchecked")
	public void expRZSYToExcel(Context context){  
		String[] ids=context.request.getParameterValues("ids");
			
		List paylists = new ArrayList();
		
		for(int i = 0;i<ids.length;i++){
			List<Map> paylist = null;
			context.contextMap.put("id", ids[i]);
			try {
				paylist = (List<Map>) DataAccessor.query("collectionManage.queryRZSY", context.contextMap, DataAccessor.RS_TYPE.LIST);
				Date startDate = (Date) DataAccessor.query("collectionManage.queryFirstPayDate", context.contextMap, DataAccessor.RS_TYPE.OBJECT) ;
				double yue = 0d;
				for(int n = 0; n<paylist.size();n++){	
					if(startDate != null && !"".equals(startDate.toString())){
						Calendar c = Calendar.getInstance();
						c.setTime(startDate);
						c.add(Calendar.MONTH, n);
//						SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
						paylist.get(n).put("PAY_DATE", c.getTime()) ;
					} else {
						paylist.get(n).put("PAY_DATE", "") ;
					}
					double LEASE_TOPRIC =DataUtil.doubleUtil(paylist.get(n).get("LEASE_TOPRIC"));
					double jianshaoe = DataUtil.doubleUtil( paylist.get(n).get("JIANSHAOE"));
					if(n==0){
						yue = LEASE_TOPRIC - jianshaoe ;
						paylist.get(n).put("YUE", yue);
					}else{
						double yue2 = DataUtil.doubleUtil(paylist.get(n-1).get("YUE"));
						yue = yue2 - jianshaoe;
						yue = NumberUtils.retain2rounded(yue);
						paylist.get(n).put("YUE", yue);
					}
				}
				paylists.add(paylist);
			} catch (Exception e) {
				LogPrint.getLogStackTrace(e, logger);
			}
			
		}
				
		ByteArrayOutputStream baos = null;
		String strFileName = "未实现融资收益分配表("+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+").xls";
		ExportPaylistToExcel exl = new ExportPaylistToExcel();
		exl.createexl(); 
		baos = exl.exportRZSYExcel(paylists);
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
			ServletOutputStream out1 = context.response.getOutputStream();
			exl.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {
			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		
	}
	
	//Add by Michael 2012 4-20 增加导出本金摊还表
	@SuppressWarnings("unchecked")
	public void expOwnPriceToExcel(Context context){  
		String[] ids=context.request.getParameterValues("ids");
			
		List paylists = new ArrayList();
		List<Map> paylist = null;
		for(int i = 0;i<ids.length;i++){
			
			context.contextMap.put("id", ids[i]);
			try {
				paylist = (List<Map>) DataAccessor.query("collectionManage.queryOwnLastPrice", context.contextMap, DataAccessor.RS_TYPE.LIST);
				paylists.add(paylist);
			} catch (Exception e) {
				LogPrint.getLogStackTrace(e, logger);
			}
			
		}
				
		ByteArrayOutputStream baos = null;
		String strFileName = "本息摊还表("+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+").xls";
		ExportPaylistToExcel exl = new ExportPaylistToExcel();
		exl.createexl(); 
		baos = exl.exportOwnPriceExcel(paylists);
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
			ServletOutputStream out1 = context.response.getOutputStream();
			exl.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {
			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		
	}

	
	/* (non-Javadoc)
	 * @see com.brick.service.core.AService#afterExecute(java.lang.String, com.brick.service.entity.Context)
	 */
	@Override
	protected void afterExecute(String action, Context context) {
		//
		if ("collectionManage.saveCalculate".equals(action)) {
			
			Long creditId = null;
			Long contractId = DataUtil.longUtil(context.contextMap.get("RECT_ID"));
			String logType = "融资租赁合同";
			String logTitle = "还款";
			String logCode = String.valueOf(context.contextMap.get("RECP_CODE"));
			String memo = "融资租赁合同还款"+logCode;
			int state = 1;
			Long userId = DataUtil.longUtil(context.contextMap.get("s_employeeId"));
			Long otherId = null;
			
			BusinessLog.addBusinessLog(creditId, contractId, logType, logTitle, logCode, memo, state, userId, otherId, (String)context.contextMap.get("IP"));
		}
	}

	/* (non-Javadoc)
	 * @see com.brick.service.core.AService#preExecute(java.lang.String, com.brick.service.entity.Context)
	 */
	@Override
	protected boolean preExecute(String action, Context context) {
		// TODO Auto-generated method stub
		return super.preExecute(action, context);
	}
	
	
	
	/**
	 * 查看支付表详细
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void showPaylineDetail(Context context) {

		Map outputMap = new HashMap();
		List errList = context.errList;
		
		Map paylist = null;
		List<Map> paylines = null;
		
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {

				paylist = (Map) DataAccessor.query("collectionManage.readPaylistById", context.contextMap, DataAccessor.RS_TYPE.MAP);

				paylines = (List<Map>) DataAccessor.query("collectionManage.readPaylines", context.contextMap, DataAccessor.RS_TYPE.LIST);
				paylist.put("paylines", paylines);
				
//				if(paylines != null && paylines.size() > 0){
//					double pv_own_price = 0d ;
//					for(int i=0; i<paylines.size() ; i++){
//						int periodNum = DataUtil.intUtil(paylines.get(i).get("PERIOD_NUM")) ;
//						Double leaseRZE = DataUtil.doubleUtil(paylines.get(i).get("LEASE_RZE")) ;
//						int leasePeriod = DataUtil.intUtil(paylines.get(i).get("LEASE_PERIOD")) ;
//						Double irrMonthPrice = DataUtil.doubleUtil( paylines.get(i).get("IRR_MONTH_PRICE")) ;
//						Double renPrice = DataUtil.doubleUtil(paylines.get(i).get("REN_PRICE")) ;
//						if (periodNum == 1) {
//							pv_own_price = leaseRZE - irrMonthPrice +renPrice;
//						} else {
//							pv_own_price =pv_own_price-irrMonthPrice +renPrice;
//						}
//						if (periodNum == leasePeriod) {
//							
//							pv_own_price = 0;
//						}
//						paylines.get(i).put("REAL_OWN_PRICE", pv_own_price);
//					}
//				}
				
				PaylistUtil.calculateLoanPaylistCostPrice(paylist);
				
				if(paylist != null){
					List list = (List) paylist.get("paylines") ;
					for(int i=0 ;i< list.size() ; i++){
						Map temp = (Map) list.get(i) ;
						if(paylines.get(i) != null){
							temp.put("REAL_OWN_PRICE",((Map)paylines.get(i)).get("REAL_OWN_PRICE") ) ;
						}
					}
				}
				outputMap.put("paylist", paylist);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}

		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/collection/showPaylineDetail.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	
	}
	
	public void testCSV(Context context)
	{
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
	
	
	//Add by Michael 2012 5-15 增加修改起租日、支付日业务支撑 功能
	@SuppressWarnings("unchecked")
	public void queryChangePayDatePaylist(Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		DataWrap dw = null;

		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			//-- Add BY Michael 增加权限，业务员只能看到自己的案件------------------------
			Map paramMap = new HashMap();
			Map rsMap = null;
			paramMap.put("id", context.contextMap.get("s_employeeId"));
			try {
				rsMap = (Map) DataAccessor.query("employee.getEmpInforById", paramMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("p_usernode", rsMap.get("NODE"));
	//------------------------------------------------------------------------
				dw = (DataWrap) DataAccessor.query("collectionManage.queryPaylist", context.contextMap, DataAccessor.RS_TYPE.PAGED);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}

		/*-------- output --------*/
		outputMap.put("dw", dw);
		outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
		outputMap.put("QSTART_DATE", context.contextMap.get("QSTART_DATE"));
		outputMap.put("QEND_DATE", context.contextMap.get("QEND_DATE"));
		outputMap.put("JQSTART_DATE", context.contextMap.get("JQSTART_DATE"));
		outputMap.put("JQEND_DATE", context.contextMap.get("JQEND_DATE"));
		outputMap.put("QSELECT_STATUS", context.contextMap.get("QSELECT_STATUS"));

		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/collection/changePayDatePaylist.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}
	
	
	
	/*
	 * Add by Michael 2012 5-25
	 * 创建重车还款账户
	 */
	@SuppressWarnings("unchecked")
	public void createCarPayAccount(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;

		/*-------- data access --------*/	
		Map rsMap ;
		if(errList.isEmpty()){	
		
			try {
				rsMap = (Map) DataAccessor.query("collectionManage.queryCarPayAccount", context.contextMap, DataAccessor.RS_TYPE.MAP);
				DataAccessor.getSession().startTransaction();
				if (rsMap!=null){
					DataAccessor.getSession().update("collectionManage.modifyCarPayAccount", context.contextMap);
				}else{
					DataAccessor.getSession().insert("collectionManage.createCarPayAccount", context.contextMap) ;
				}
				DataAccessor.getSession().commitTransaction();
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}finally {
				try {
					DataAccessor.getSession().endTransaction() ;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		}
		
		if (errList.isEmpty()) {
			Output.jspSendRedirect(context, "defaultDispatcher?__action=collectionManage.queryPaylistForExport");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
	}
	

	/*
	 * Add by Michael 2012 5-25
	 * 查询重车还款账户
	 */
	public void queryCarPayAccount(Context context)
	{
		Map outputMap = new HashMap();

		Map writeBackDetails = null;	

		List errList = context.errList ;
		try {
			writeBackDetails = (Map) DataAccessor.query("collectionManage.queryCarPayAccount", context.contextMap, DataAccessor.RS_TYPE.MAP);
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("查询重车还款账户!请联系管理员");			
		}
		
		outputMap.put("writeBackDetails", writeBackDetails);	
		if(errList.isEmpty()){
			Output.jsonOutput(outputMap, context);
		}else{
			outputMap.put("errList", errList);
		}
	}
	
	/*
	 * Add by Michael 2012 09-12 
	 * 查询出所有案件
	 * 增加法务费用的管理查询
	 */
	@SuppressWarnings("unchecked")
	public void queryPaylistForAddLawyFee(Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		PagingInfo<Object> dw = null;
		
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				dw = baseService.queryForListWithPaging("collectionManage.queryLawyFeePaylist", context.contextMap, "RECP_ID", ORDER_TYPE.DESC);
				outputMap.put("lawyFeeList", DictionaryUtil.getDictionary("法务费用"));
			
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}

		/*-------- output --------*/
		outputMap.put("dw", dw);
		outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
		outputMap.put("QSTART_DATE", context.contextMap.get("QSTART_DATE"));
		outputMap.put("QEND_DATE", context.contextMap.get("QEND_DATE"));
		outputMap.put("JQSTART_DATE", context.contextMap.get("JQSTART_DATE"));
		outputMap.put("JQEND_DATE", context.contextMap.get("JQEND_DATE"));
		outputMap.put("QSELECT_STATUS", context.contextMap.get("QSELECT_STATUS"));
		outputMap.put("QUERY_FEE_NAME", context.contextMap.get("QUERY_FEE_NAME"));
		outputMap.put("companyCode", context.contextMap.get("companyCode"));
		outputMap.put("companys", LeaseUtil.getCompanys());
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/lawyFeeManage/queryLawyFeeManage.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/*
	 * Add by Michael 2012 09-12 
	 * 根据支付表ID查询法务费用List
	 */
	@SuppressWarnings("unchecked")
	public void queryLawyFeeList(Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		List lawFeeList = null;
		List lawFeePayList=null;
		boolean modifyLawFee=false;
		
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			Map paramMap = new HashMap();
			Map rsMap = null;
			try {
				lawFeeList = baseService.queryForList("collectionManage.queryLawFeeListByRecpID", context.contextMap);
				
				lawFeePayList = baseService.queryForList("collectionManage.queryLawFeePayListByRecpID", context.contextMap);
				
				//增加法务费用的修改、删除权限
				List<String> resourceIdList=(List<String>) DataAccessor.query("supplier.getResourceIdListByEmplId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				for(int i=0;resourceIdList!=null&&i<resourceIdList.size();i++) {
					if("263".equals(resourceIdList.get(i))) {
						modifyLawFee=true;
					}
				}
				outputMap.put("modifyLawFee", modifyLawFee);
				//将法务费用加入进来
				outputMap.put("lawyFeeList", DictionaryUtil.getDictionary("法务费用"));
			
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}

		/*-------- output --------*/
		outputMap.put("lawFeeList", lawFeeList);
		outputMap.put("lawFeePayList", lawFeePayList);
		
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/lawyFeeManage/lawyFeeList.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	//法务费用增加
	public void createLawFeeList(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;

		/*-------- data access --------*/	
		Map rsMap ;
		if(errList.isEmpty()){	
		
			try {
				rsMap = (Map) DataAccessor.query("collectionManage.queryLawFeeListByID", context.contextMap, DataAccessor.RS_TYPE.MAP);
				DataAccessor.getSession().startTransaction();
				if (rsMap!=null){
					DataAccessor.getSession().update("collectionManage.modifyLawFee", context.contextMap);
				}else{
					DataAccessor.getSession().insert("collectionManage.createLawFee", context.contextMap) ;
					context.contextMap.put("ANSWERPHONE_NAME", "000");
					context.contextMap.put("PHONE_NUMBER", "000");
					context.contextMap.put("LAWYFEERECORD", context.contextMap.get("FEE_NAME_TEXT")+":"+context.contextMap.get("FEE_VALUE")+"元。备注："+context.contextMap.get("MEMO"));
					DataAccessor.getSession().insert("collectionManage.createDunRecord", context.contextMap) ;
				}
				
				DataAccessor.getSession().commitTransaction();
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}finally {
				try {
					DataAccessor.getSession().endTransaction() ;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		}
		Map writeBackDetails=new HashMap();
		writeBackDetails.put("strReturn","操作成功！");
		outputMap.put("writeBackDetails", writeBackDetails);	
		if(errList.isEmpty()){
			Output.jsonOutput(outputMap, context);
		}else{
			outputMap.put("errList", errList);
		}
	}
	
	//查询法务费用
	public void showLawFeeList(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;

		/*-------- data access --------*/	
		Map rsMap=null ;
		if(errList.isEmpty()){	
		
			try {
				rsMap = (Map) DataAccessor.query("collectionManage.queryLawFeeListByID", context.contextMap, DataAccessor.RS_TYPE.MAP);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		outputMap.put("writeBackDetails", rsMap);	
		if(errList.isEmpty()){
			Output.jsonOutput(outputMap, context);
		}else{
			outputMap.put("errList", errList);
		}
	}
	
	//将法务费用作废
	public void deleteLawFeeList(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;

		/*-------- data access --------*/	
		Map rsMap=null ;
		if(errList.isEmpty()){	
		
		try {
				DataAccessor.getSession().startTransaction();
				DataAccessor.getSession().update("collectionManage.deleteLawFee", context.contextMap) ;
				DataAccessor.getSession().commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}finally {
				try {
					DataAccessor.getSession().endTransaction() ;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		Map writeBackDetails=new HashMap();
		writeBackDetails.put("strReturn","操作成功！");	
		outputMap.put("writeBackDetails", writeBackDetails);
		if(errList.isEmpty()){
			Output.jsonOutput(outputMap, context);
		}else{
			outputMap.put("errList", errList);
		}
	}
	
	public static Map<String,Object> exportLawyFeeList (String QSTART_DATE,String QEND_DATE,String QUERY_FEE_NAME,String QSEARCH_VALUE,String company_code) {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map context= new HashMap();
		List lawyFeeList = null;

		context.put("QSTART_DATE", QSTART_DATE);
		context.put("QEND_DATE", QEND_DATE);
		context.put("QUERY_FEE_NAME", QUERY_FEE_NAME);
		context.put("QSEARCH_VALUE", QSEARCH_VALUE);
		
		try {
			lawyFeeList=(List) DataAccessor.query("collectionManage.queryLawyFeePaylistForExport",context, DataAccessor.RS_TYPE.LIST);

			resultMap.put("lawyFeeList", lawyFeeList);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return resultMap;		
	}
	
}
