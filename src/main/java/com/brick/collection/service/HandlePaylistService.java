package com.brick.collection.service;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.baseManage.service.BusinessLog;
import com.brick.collection.CollectionConstants;
import com.brick.collection.core.IRRUtils;
import com.brick.collection.support.PayRate;
import com.brick.collection.util.PayUtils;
import com.brick.collection.util.PaylistUtil;
import com.brick.credit.service.CreditPaylistService;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DataUtil;
import com.brick.util.web.HTMLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;

/**
 * @author wujw
 * @date Jul 16, 2010
 * @version 
 */
public class HandlePaylistService extends AService {

	static Log logger = LogFactory.getLog(HandlePaylistService.class);
	
	private Map paylist = null;
	private Map oldPaylist = null;
	
	private Integer rePayline = null;
	private Integer addVersion = null;
	private Integer changeNum = null;
	/**
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void initSetPayDate(Context context) {
		logger.info("设定起租日期初始化...");
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		//
		Map paylist = null;
		List<Map> payEquipments = null;
		//
		List<Map> payWays = null;
		
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {
				//
				paylist = (Map) DataAccessor.query("collectionManage.readPaylistById", context.contextMap, DataAccessor.RS_TYPE.MAP);
				//
				payEquipments = (List<Map>) DataAccessor.query("collectionManage.readPayEquipments", context.contextMap, DataAccessor.RS_TYPE.LIST);
				paylist.put("payEquipments", payEquipments);
				//
				outputMap.put("paylist", paylist);
				//
				Map dataDictionaryMap = new HashMap();
				dataDictionaryMap.put("dataType", "支付方式");
				payWays = (List) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("payWays", payWays);
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
				
			} catch (Exception e) {
				errList.add(e.getMessage());
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
			
		}

		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/collection/setPayDate.jsp");
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
	public void setPayDate(Context context) {
		logger.info("设定起租日期...");
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		//
		Map paylist = null;
		List<Map> paylines = null;
		
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {
				DataAccessor.getSession().startTransaction();
				
				//
				paylist = (Map) DataAccessor.getSession().queryForObject("collectionManage.readPaylistById", context.contextMap);
				//
				int payWay = DataUtil.intUtil(paylist.get("PAY_WAY"));
				int leaseTerm = DataUtil.intUtil(paylist.get("LEASE_TERM"));
				//
				paylines = (List<Map>) DataAccessor.getSession().queryForList("collectionManage.readPaylines", context.contextMap);
				paylist.put("paylines", paylines);
				//
				Date startDate = DataUtil.dateUtil(context.contextMap.get("START_DATE"), "yyyy-MM-dd");
				Date endDate = null;
				//胡昭卿加
				Date firstPayDate = DataUtil.dateUtil(context.contextMap.get("FIRST_PAYDATE"), "yyyy-MM-dd");
				//胡昭卿加
				Calendar calendar = Calendar.getInstance();
	            calendar.setTime(startDate);
	            //Add by Michael 2012 02/01  增加 FinanceDate
				Calendar financeDate = Calendar.getInstance();
				financeDate.setTime(firstPayDate);
				/*
	            if (payWay == CollectionConstants.PAY_WAY_END_EQUAL_CAPITAL
						|| payWay == CollectionConstants.PAY_WAY_END_EQUAL_RATE
						|| payWay == CollectionConstants.PAY_WAY_END_UNEQUAL) {
	            	calendar.add(Calendar.MONTH, leaseTerm);
				} 
				*/
	            int index = 0;
	            for (Map payline : paylines) {
	            	calendar.setTime(startDate);
	            	financeDate.setTime(firstPayDate);
	            	int num = 0 ;
	            	if(payline.get("PERIOD_NUM") != null){
	            		num = Integer.parseInt(payline.get("PERIOD_NUM").toString()) ;
	            	}
	            	if (index != 0) {
	            		calendar.add(Calendar.MONTH, leaseTerm * (num - 1) );
	            		financeDate.add(Calendar.MONTH, leaseTerm * (num - 1) );
	            	}
            		payline.put("PAY_DATE", calendar.getTime());
            		payline.put("S_EMPLOYEEID", context.contextMap.get("s_employeeId"));
            		endDate = calendar.getTime();
            		DataAccessor.getSession().update("handlePaylist.updatePayDate", payline);
            		
            		//Add by Michael 2012 02/01  增加 FinanceDate
            		payline.put("finance_date", financeDate.getTime());
            		DataAccessor.getSession().update("handlePaylist.updateFinanceDate", payline);
            		index ++;
            	}
	            //
	            DataAccessor.getSession().executeBatch();
				
	            //Add by Michael 2012 5-16 增加修改起租日、支付日记录日志
	            String oldfirstPayDate = String.valueOf(paylist.get("STR_FIRST_PAYDATE"));
	            String oldstartDate = String.valueOf(paylist.get("STR_START_DATE"));
	            
	            paylist.put("S_EMPLOYEEID", context.contextMap.get("s_employeeId"));
	            paylist.put("START_DATE", startDate);
	            paylist.put("END_DATE", endDate);
	          //胡昭卿加
	            paylist.put("FIRST_PAYDATE", firstPayDate);
	          //胡昭卿加
	            DataAccessor.getSession().update("handlePaylist.updateStartDate", paylist);
	            
	            //Add by Michael 2012 5-16 增加修改起租日、支付日记录日志-------------------------
	            String memo="起租日期由"+String.valueOf(oldfirstPayDate)+"变更为"+String.valueOf(context.contextMap.get("FIRST_PAYDATE"))+";首期支付日由"+String.valueOf(oldstartDate)+"变更为"+String.valueOf(context.contextMap.get("START_DATE"));
	            paylist.put("MEMO", memo);
	            paylist.put("OLD_FIRST_PAYDATE", oldfirstPayDate);
	            paylist.put("OLD_START_DATE", oldstartDate);	            
	            DataAccessor.getSession().insert("handlePaylist.insertChangePayDateLog", paylist);
	            //------------------------------------------------------------------------------
	            
	            DataAccessor.getSession().commitTransaction();
	            
	            //
	            context.contextMap.put("RECT_ID", paylist.get("RECT_ID"));
	            context.contextMap.put("RECP_CODE", paylist.get("RECP_CODE"));
	            
			} catch (Exception e) {
				errList.add(e.getMessage());
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} finally {
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
			Output.jspSendRedirect(context, "defaultDispatcher?__action=collectionManage.queryChangePayDatePaylist");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
	}
	/*
	 * Add by Michael 2012 01/11 增加财务日期设定
	 */
	public void setFinanceDate(Context context) {
		logger.info("设定财务日期...");
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		//
		Map paylist = null;
		List<Map> paylines = null;
		
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {	
				//
				paylist = (Map) DataAccessor.getSession().queryForObject("collectionManage.readPaylistById", context.contextMap);
				//
				int payWay = DataUtil.intUtil(paylist.get("PAY_WAY"));
				int leaseTerm = DataUtil.intUtil(paylist.get("LEASE_TERM"));
				//
				paylines = (List<Map>) DataAccessor.getSession().queryForList("collectionManage.readPaylines", context.contextMap);
				paylist.put("paylines", paylines);
				//
				Date startDate = DataUtil.dateUtil(paylist.get("FIRST_PAYDATE"), "yyyy-MM-dd");
				Date endDate = null;

				//胡昭卿加
				Calendar calendar = Calendar.getInstance();
	            calendar.setTime(startDate);

	            int index = 0;
	            for (Map payline : paylines) {
	            	calendar.setTime(startDate);
	            	int num = 0 ;
	            	if(payline.get("PERIOD_NUM") != null){
	            		num = Integer.parseInt(payline.get("PERIOD_NUM").toString()) ;
	            	}
	            	if (index != 0) {
	            		calendar.add(Calendar.MONTH, leaseTerm * (num - 1) );
	            	}
            		payline.put("finance_date", calendar.getTime());
            		payline.put("S_EMPLOYEEID", context.contextMap.get("s_employeeId"));
            		endDate = calendar.getTime();
            		DataAccessor.getSession().update("handlePaylist.updateFinanceDate", payline);
            		index ++;
            	}
	            //
	            DataAccessor.getSession().executeBatch();
	            
			} catch (Exception e) {
				errList.add(e.getMessage());
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} 	
		}

		if (errList.isEmpty()) {
			Output.jspSendRedirect(context, "defaultDispatcher?__action=collectionManage.queryPaylist");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
	}
	
	/*
	 * Add by Michael 2012 01/11 更新所有支付表财务日期
	 * 
	 */	
	public void updateAllFinanceDate(Context context) {
		List errList = context.errList;
		//
		List<Map> paylists = null;
	
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {	
				//
				paylists = (List<Map>) DataAccessor.getSession().queryForList("handlePaylist.queryAllCollectionPlan", context.contextMap);
	            for (Map paylist : paylists) {
	            	context.contextMap.put("RECP_ID",paylist.get("RECP_ID") );
	            	setFinanceDate(context);
            	}
	            
			} catch (Exception e) {
				errList.add(e.getMessage());
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} 	
		}	
	}
	
	/**
	 * 
	 * @param RECP_ID 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void initChangePaylist(Context context) {
		
		logger.info("变更支付表初始化...");
		
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
				paylist.put("CONTRACT_PRICE", paylist.get("LEASE_TOPRIC"));
				
				//Add by Michael 2012 1/6  增加查询租赁方案
				//readPaylistSchemaByID
				Map contractSchema = null;
				contractSchema = (Map) DataAccessor.query("collectionManage.readPaylistSchemaByID", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("contractSchema", contractSchema);
				//--------------------------------------------------------------------------------------
				
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
				// package the irr month price of the paylist
				// StartPayService.packagePaylines(paylist);
				
				//
				List<Map> irrMonthPaylines = (List<Map>)DataAccessor.query("collectionManage.readCollectionplanSchemaIrrByRecpid", context.contextMap, DataAccessor.RS_TYPE.LIST);
				// 2010-09-26 wjw v1.6
				StartPayService.setIrrMonthPayline(paylist, irrMonthPaylines);				
				
				outputMap.put("paylist", paylist);
				//
				Map baseRate = PayRate.getBaseRate();
				outputMap.put("baseRateJson", Output.serializer.serialize(baseRate));
				//
				insureCompanyList = (List<Map>) DataAccessor.query("insuCompany.queryInsureCompanyListForSelect", null, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureCompanyList", insureCompanyList);
				insureTypeList = (List<Map>) DataAccessor.query("insureType.queryInsureTypeList", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureTypeList", insureTypeList);
				outputMap.put("insureTypeJsonList", Output.serializer.serialize(insureTypeList));
				
				Map dataDictionaryMap = new HashMap();
				dataDictionaryMap.put("dataType", "支付方式");
				payWays = (List) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("payWays", payWays);
				
				dataDictionaryMap.put("dataType", "租赁期满处理方式");
				dealWays = (List) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("dealWays", dealWays);
				
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
				
			} catch (Exception e) {
				e.printStackTrace();
				errList.add(e);
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/collection/changePaylist.jsp");
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
	public void changePaylist(Context context,Map outputMap,List errList) {
		
		logger.info("变更支付表预览...");

		List<Map> payEquipments = null;
		List<Map> payInusres = null;
		List<Map> payOtherFees = null;
		List<Map> paylines = null;
		
		List<Map> insureCompanyList = null;
		List<Map> insureTypeList = null;
		List<Map> payWays = null;
		List<Map> dealWays = null;
		List suplList=null;
		//
		rePayline = DataUtil.intUtil(context.contextMap.get("REPAYLINE"));
		addVersion = DataUtil.intUtil(context.contextMap.get("ADDVERSION"));
		changeNum = DataUtil.intUtil(context.contextMap.get("CHANGE_NUM"));
		
		logger.info("是否重新配平：" + rePayline + ";是否新增版本：" + addVersion + ";开始变更期次" + changeNum);
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {
				//
				payEquipments = (List<Map>) DataAccessor.query("collectionManage.readPayEquipments", context.contextMap, DataAccessor.RS_TYPE.LIST);
				//
				if (rePayline == 1 || changeNum == 1) {
					
					paylist = StartPayService.changePaylist(context, payEquipments);
				
				} else {
					//
					oldPaylist = (Map) DataAccessor.query("collectionManage.readPaylistById", context.contextMap, DataAccessor.RS_TYPE.MAP);
					oldPaylist.put("payEquipments", payEquipments);
					//
					payInusres = (List<Map>) DataAccessor.query("collectionManage.readPayInusres", context.contextMap, DataAccessor.RS_TYPE.LIST);
					oldPaylist.put("payInusres", payInusres);
					//
					payOtherFees = (List<Map>) DataAccessor.query("collectionManage.readPayOtherFees", context.contextMap, DataAccessor.RS_TYPE.LIST);
					oldPaylist.put("payOtherFees", payOtherFees);
					//
					paylines = (List<Map>) DataAccessor.query("collectionManage.readPaylines", context.contextMap, DataAccessor.RS_TYPE.LIST);
					oldPaylist.put("paylines", paylines);
					//
					List<Map> lockedPaylineList = new ArrayList<Map>();
					List<Map> holdPaylistList = new ArrayList<Map>();
					Map lastPayline = null;
					int passedIndex = 0;
					Double sumHoldPrice = 0D;
					
					for(Map payline : paylines) {
						int periodNum = DataUtil.intUtil(payline.get("PERIOD_NUM")); 
						if ( periodNum == (changeNum - 1)) {
							lastPayline = payline;
						}
						//
						double passed = DataUtil.doubleUtil(payline.get("REDUCE_OWN_PRICE")) 
								+ DataUtil.doubleUtil(payline.get("REDUCE_REN_PRICE")) 
								+ DataUtil.doubleUtil(payline.get("REDUCE_OTHER_PRICE")) 
								+ DataUtil.doubleUtil(payline.get("REDUCE_LOSS_PRICE"));
						
						if(passed > 0) {
							passedIndex ++ ;
						}
						if(periodNum < changeNum){
							Map tempMap = new HashMap();
							StartPayService.copyPayline(tempMap, payline);
							holdPaylistList.add(tempMap);
							sumHoldPrice += DataUtil.doubleUtil(payline.get("OWN_PRICE"));
						}

					}

					for (Map payline : StartPayService.upPackagePaylines(context)) {
						int periodNum = DataUtil.intUtil(payline.get("PERIOD_NUM"));
						if (periodNum >= changeNum) {
							payline.put("PERIOD_NUM", periodNum + 1 - changeNum);
							lockedPaylineList.add(payline);
						}
					}
					
					paylist = StartPayService.changePaylist(context, errList, oldPaylist, payEquipments, lockedPaylineList, holdPaylistList, lastPayline, passedIndex, sumHoldPrice);
		        
				}
				
				//
				int maxVersion = (Integer)DataAccessor.query("handlePaylist.readMaxVersionByRecpCode", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
				if (addVersion == 1) {
					paylist.put("VERSION_CODE", maxVersion + 1);
				} else {
					paylist.put("VERSION_CODE", maxVersion);
				}
				
				outputMap.put("paylist", paylist);
				//
				Map baseRate = PayRate.getBaseRate();
				outputMap.put("baseRateJson", Output.serializer.serialize(baseRate));
				//
				insureCompanyList = (List<Map>) DataAccessor.query("insuCompany.queryInsureCompanyListForSelect", null, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureCompanyList", insureCompanyList);
				insureTypeList = (List<Map>) DataAccessor.query("insureType.queryInsureTypeList", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureTypeList", insureTypeList);
				outputMap.put("insureTypeJsonList", Output.serializer.serialize(insureTypeList));
				
				Map dataDictionaryMap = new HashMap();
				dataDictionaryMap.put("dataType", "支付方式");
				payWays = (List) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("payWays", payWays);
				
				dataDictionaryMap.put("dataType", "租赁期满处理方式");
				dealWays = (List) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("dealWays", dealWays);
				
				suplList=(List)DictionaryUtil.getDictionary("供应商保证");
				outputMap.put("suplList", suplList);
				
			} catch (Exception e) {
				e.printStackTrace();
				errList.add("异常信息："+e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		
		paylist.put("LEASE_CODE", context.contextMap.get("LEASE_CODE"));
		paylist.put("CUST_NAME", context.contextMap.get("CUST_NAME"));
		paylist.put("RECP_CODE", context.contextMap.get("RECP_CODE"));
		paylist.put("RECT_ID", context.contextMap.get("RECT_ID"));
		paylist.put("RECP_ID", context.contextMap.get("RECP_ID"));
		paylist.put("CONTRACT_PRICE", context.contextMap.get("LEASE_TOPRIC"));
		
		paylist.put("LEASE_TOPRIC", context.contextMap.get("TOTAL_PRICE"));
		paylist.put("PAYDATE_FLAG", context.contextMap.get("PAYDATE_FLAG"));
		paylist.put("START_DATE", HTMLUtil.parseDateParam(String.valueOf(context.contextMap.get("START_DATE")), null));
		paylist.put("FIRST_PAYDATE", HTMLUtil.parseDateParam(String.valueOf(context.contextMap.get("FIRST_PAYDATE")), null));
		
		paylist.put("PLEDGE_ENTER_AGRATE", context.contextMap.get("PLEDGE_ENTER_AGRATE"));
		paylist.put("PLEDGE_ENTER_MCTOAG", context.contextMap.get("PLEDGE_ENTER_MCTOAG"));
		paylist.put("PLEDGE_ENTER_MCTOAGRATE", context.contextMap.get("PLEDGE_ENTER_MCTOAGRATE"));
		paylist.put("SUPL_TRUE", context.contextMap.get("SUPL_TRUE"));
		
		outputMap.put("errList", errList);

		outputMap.put("REPAYLINE", context.contextMap.get("REPAYLINE"));
		outputMap.put("ADDVERSION", context.contextMap.get("ADDVERSION"));
		outputMap.put("CHANGE_NUM", context.contextMap.get("CHANGE_NUM"));
		
	}
	/**
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void viewChangePaylist(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		this.changePaylist(context,outputMap,errList);
		
		Output.jspOutput(outputMap, context, "/collection/changePaylist.jsp");
	
	}
	/**
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void saveChangePaylist(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map schema = null;
		
		//
		List<Map> rePaylineList = StartPayService.upPackagePaylines(context);
		this.changePaylist(context,outputMap,errList);
		
		Long recpId = null;
		
		paylist.put("S_EMPLOYEEID", context.contextMap.get("s_employeeId"));
		
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
			try {
				DataAccessor.getSession().startTransaction();
				
				if (addVersion == 1) {
					// insert
					DataAccessor.getSession().update("handlePaylist.revokePaylist", paylist);
					Long oldRecpId = DataUtil.longUtil(paylist.get("RECP_ID"));
					recpId = (Long) DataAccessor.getSession().insert("collectionManage.createPaylist", paylist);
					
					// equipment
					paylist.put("OLDRECPID", oldRecpId);
					paylist.put("NEWRECPID", recpId);
					DataAccessor.getSession().insert("handlePaylist.createPayEquipmentsByOldRecpId", paylist);
					DataAccessor.getSession().update("handlePaylist.revokePayEquipments", paylist);
					
					// payline
					DataAccessor.getSession().update("handlePaylist.revokePayline", paylist);
					List<Map> paylines = (List<Map>) paylist.get("paylines");
					for (Map payline : paylines) {
						
						payline.put("RECP_ID", recpId);
						payline.put("S_EMPLOYEEID", context.contextMap.get("s_employeeId"));
						
						DataAccessor.getSession().insert("collectionManage.createPaylines", payline);
						
					}
					
					// insure
					DataAccessor.getSession().update("handlePaylist.revokePayInsure", paylist);
					List<Map> payInusres = (List<Map>) paylist.get("payInusres");
					for (Map insure : payInusres) {
						
						insure.put("RECP_ID", recpId);
						insure.put("S_EMPLOYEEID", context.contextMap.get("s_employeeId"));
						
						DataAccessor.getSession().insert("collectionManage.createPayInsures", insure);
					
					}
					
					// other fee
					DataAccessor.getSession().update("handlePaylist.revokePayOtherFee", paylist);
					List<Map>  payOtherFees = (List<Map>) paylist.get("payOtherFees");
					for (Map otherFee : payOtherFees) {
						
						otherFee.put("RECP_ID", recpId);
						otherFee.put("S_EMPLOYEEID", context.contextMap.get("s_employeeId"));
						
						DataAccessor.getSession().insert("collectionManage.createPayOtherFee", otherFee);
						
					}
					
					// remarks
					DataAccessor.getSession().insert("handlePaylist.createPayRemarksByOldRecpId", paylist);
					DataAccessor.getSession().update("handlePaylist.revokePayRemarks", paylist);
					
					//修改打包数据
					/*
					deleteCollectionIrrMonthPrice(paylist);
					System.out.println("delete paylist irrmonthpaylings=================");
					operateCollectionIrrMonthPrice(paylist);
					*/
				} else {
					//
					recpId = DataUtil.longUtil(paylist.get("RECP_ID"));
					// paylist
					DataAccessor.getSession().update("handlePaylist.updatePaylist", paylist);
					
					// payline
					int paylineCount = (Integer) DataAccessor.getSession().queryForObject("handlePaylist.queryPaylineCountByRecpId", paylist);
					DataAccessor.getSession().update("handlePaylist.deletePayline", paylist);
					List<Map> paylines = (List<Map>) paylist.get("paylines");
					for (Map payline : paylines) {
						
						payline.put("RECP_ID", recpId);
						payline.put("S_EMPLOYEEID", context.contextMap.get("s_employeeId"));
						
						int periodNum = DataUtil.intUtil(payline.get("PERIOD_NUM"));
						if (periodNum <= paylineCount) {
							DataAccessor.getSession().update("handlePaylist.updatePayline", payline);
						} else {
							DataAccessor.getSession().insert("collectionManage.createPaylines", payline);
						}
						
					}
					
					// insure
					DataAccessor.getSession().update("handlePaylist.deletePayInsure", paylist);
					List<Map> payInusres = (List<Map>) paylist.get("payInusres");
					for (Map insure : payInusres) {
						
						insure.put("RECP_ID", recpId);
						insure.put("S_EMPLOYEEID", context.contextMap.get("s_employeeId"));
						
						Long reciId = DataUtil.longUtil(insure.get("RECI_ID"));
						if (reciId != 0l) {
							DataAccessor.getSession().update("handlePaylist.updatePayInsure", insure);
						} else {
							DataAccessor.getSession().insert("collectionManage.createPayInsures", insure);
						}
					
					}
					
					// other fee
					DataAccessor.getSession().update("handlePaylist.deletePayOtherFee", paylist);
					List<Map>  payOtherFees = (List<Map>) paylist.get("payOtherFees");
					for (Map otherFee : payOtherFees) {
						
						otherFee.put("RECP_ID", recpId);
						otherFee.put("S_EMPLOYEEID", context.contextMap.get("s_employeeId"));
						
						Long recoId = DataUtil.longUtil(otherFee.get("RECO_ID"));
						if (recoId != 0l) {
							DataAccessor.getSession().update("handlePaylist.updatePayOtherFee", otherFee);
						} else {
							DataAccessor.getSession().insert("collectionManage.createPayOtherFee", otherFee);
						}
						
					}

				}
				//修改应付租金列表时要把页面对应的列表保存起来
				deleteCollectionIrrMonthPrice(paylist);
				
				operateCollectionIrrMonthPrice(paylist);
				DataAccessor.getSession().commitTransaction();
				
			} catch (SQLException e) {
				logger.info("支付表变更保存错误！" + e.getMessage());
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} finally {
				try {
					DataAccessor.getSession().endTransaction();
				} catch (SQLException e) {
					logger.info("支付表变更保存关闭事务错误！" + e.getMessage());
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
			}
		}
		
		if (errList.isEmpty()) {
			Output.jspSendRedirect(context, "defaultDispatcher?__action=collectionManage.showPaylist&FLAG=3&RECP_ID="+recpId);
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
		
	}
	
	/* (non-Javadoc)
	 * @see com.brick.service.core.AService#afterExecute(java.lang.String, com.brick.service.entity.Context)
	 */
	@Override
	protected void afterExecute(String action, Context context) {
		//
		if ("handlePaylistService.setPayDate".equals(action)) {
			
			Long creditId = 0l;
			Long contractId = DataUtil.longUtil(context.contextMap.get("RECT_ID"));
			String logType = "融资租赁合同";
			String logTitle = "支付表设定起租日期";
			String logCode = String.valueOf(context.contextMap.get("RECP_CODE"));
			String memo = "融资租赁合同支付表["+logCode+"]设定起租日期";
			int state = 1;
			Long userId = DataUtil.longUtil(context.contextMap.get("s_employeeId"));
			Long otherId = null;
			
			BusinessLog.addBusinessLog(creditId, contractId, logType, logTitle, logCode, memo, state, userId, otherId, (String)context.contextMap.get("IP"));
		}
		//
		if ("handlePaylistService.saveChangePaylist".equals(action)) {
			
			Long creditId = 0l;
			Long contractId = DataUtil.longUtil(context.contextMap.get("RECT_ID"));
			String logType = "融资租赁合同";
			String logTitle = "支付表变更";
			String logCode = String.valueOf(context.contextMap.get("RECP_CODE"));
			String memo = "融资租赁合同支付表["+logCode+"]变更";
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
	 * 支付表备注
	 * AJAX
	 */
	@SuppressWarnings("unchecked")
	public void showRemarkLog(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();

		List remark = new ArrayList();
		List info = new ArrayList();
		if (errList.isEmpty()) {

			try {

				remark = (List) DataAccessor.query("handlePaylist.queryMsg", context.getContextMap(), DataAccessor.RS_TYPE.LIST);

				info = (List) DataAccessor.query("handlePaylist.queryInfo", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				errList.add("支付表备注查询出错" + e.getMessage());
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}

		if (errList.isEmpty()) {
			outputMap.put("remark", remark);
			outputMap.put("info", info);

			Output.jsonOutput(outputMap, context);
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	/**
	 * 备注保存
	 */
	@SuppressWarnings("unchecked")
	public void conserve(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();

		String remark = HTMLUtil.getStrParam(context.getRequest(), "remark", "1");
		String unusual = HTMLUtil.getStrParam(context.getRequest(), "unusual", "1");
		int id = HTMLUtil.getIntParam(context.getRequest(), "id", 0);
		Map content = new HashMap();

		if (errList.isEmpty()) {
			if (id != 0) {

				content.put("REMARK", remark);
				content.put("ID", id);
				content.put("EMPLOYEEID", context.contextMap.get("s_employeeId"));

				try {

					DataAccessor.getSession().startTransaction();

					if (!(remark.equals("1"))) {

						content.put("TYPE", 0);
						DataAccessor.getSession().insert("handlePaylist.createCollecionRemark", content);

					}

					if (!(unusual.equals("1"))) {

						content.put("TYPE", 1);
						content.put("REMARK", unusual);

						DataAccessor.getSession().insert("handlePaylist.createCollecionRemark", content);

					}
					DataAccessor.getSession().commitTransaction();

				} catch (Exception e) {
					errList.add("支付表备注添加错误！" + e.getMessage());
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				} finally {
					try {
						DataAccessor.getSession().endTransaction();
					} catch (SQLException e) {
						errList.add("支付表备注事务关闭错误！" + e.getMessage());
						e.printStackTrace();
						LogPrint.getLogStackTrace(e, logger);
						errList.add(e);
					}
				}

			}
		}

		if (errList.isEmpty()) {
			outputMap.put("result", 1);
			Output.jsonOutput(outputMap, context);
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	
	/**
	 * 导入支付表初始化
	 * @param RECP_ID 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void initImportPaylist(Context context) {
		
		logger.info("导入支付表初始化...");
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		Map paylist = null;
		List<Map> payEquipments = null;
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
				payEquipments = (List<Map>) DataAccessor.query("collectionManage.readPayEquipments", context.contextMap, DataAccessor.RS_TYPE.LIST);
				paylist.put("payEquipments", payEquipments);
				//

				//
				paylines = (List<Map>) DataAccessor.query("collectionManage.readPaylines", context.contextMap, DataAccessor.RS_TYPE.LIST);
				paylist.put("paylines", paylines);
				// package the irr month price of the paylist
				StartPayService.packagePaylines(paylist);
				
				outputMap.put("paylist", paylist);
				//
				Map baseRate = PayRate.getBaseRate();
				outputMap.put("baseRateJson", Output.serializer.serialize(baseRate));
				//
				insureCompanyList = (List<Map>) DataAccessor.query("insuCompany.queryInsureCompanyListForSelect", null, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureCompanyList", insureCompanyList);
				insureTypeList = (List<Map>) DataAccessor.query("insureType.queryInsureTypeList", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureTypeList", insureTypeList);
				outputMap.put("insureTypeJsonList", Output.serializer.serialize(insureTypeList));
				
				Map dataDictionaryMap = new HashMap();
				dataDictionaryMap.put("dataType", "支付方式");
				payWays = (List) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("payWays", payWays);
				
				dataDictionaryMap.put("dataType", "租赁期满处理方式");
				dealWays = (List) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("dealWays", dealWays);
				
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
				
			} catch (Exception e) {
				e.printStackTrace();
				errList.add(e);
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/collection/improtPaylist.jsp");
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
	public void viewImportPaylist(Context context) {
		
		logger.info("导入支付表预览...");

		Map outputMap = new HashMap();
		List errList = context.errList;
		
		List<Map> payEquipments = null;
		List<Map> paylines = new ArrayList<Map>();;
		
		List<Map> insureCompanyList = null;
		List<Map> insureTypeList = null;
		List<Map> payWays = null;
		List<Map> dealWays = null;
		
		List<Map> payInusres = null;
		List<Map> payOtherFees = null;
		List suplList=null;
		Map paylist = new HashMap();

		
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {
				paylist.put("LEASE_CODE", context.contextMap.get("LEASE_CODE"));
				paylist.put("CUST_NAME", context.contextMap.get("CUST_NAME"));
				paylist.put("RECP_CODE", context.contextMap.get("RECP_CODE"));
				paylist.put("RECT_ID", context.contextMap.get("RECT_ID"));
				paylist.put("RECP_ID", context.contextMap.get("RECP_ID"));
				paylist.put("CONTRACT_PRICE", DataUtil.doubleUtil(context.contextMap.get("LEASE_TOPRIC")));
				paylist.put("LEASE_TOPRIC", DataUtil.doubleUtil(context.contextMap.get("TOTAL_PRICE")));
				paylist.put("PAYDATE_FLAG", context.contextMap.get("PAYDATE_FLAG"));
				paylist.put("SUPL_TRUE", context.contextMap.get("SUPL_TRUE"));
				//
				payEquipments = (List<Map>) DataAccessor.query("collectionManage.readPayEquipments", context.contextMap, DataAccessor.RS_TYPE.LIST);
				//
				//方案
				paylist.put("LEASE_PERIOD", context.contextMap.get("LEASE_PERIOD"));
				paylist.put("LEASE_TERM", context.contextMap.get("LEASE_TERM"));
				paylist.put("PLEDGE_PRICE_RATE", DataUtil.doubleUtil(context.contextMap.get("PLEDGE_PRICE_RATE")));
				paylist.put("PLEDGE_PRICE", DataUtil.doubleUtil(context.contextMap.get("PLEDGE_PRICE")));
				paylist.put("PLEDGE_WAY", context.contextMap.get("PLEDGE_WAY"));
				paylist.put("PLEDGE_REALPRIC", DataUtil.doubleUtil(context.contextMap.get("PLEDGE_REALPRIC")));
				paylist.put("PLEDGE_PERIOD", context.contextMap.get("PLEDGE_PERIOD"));
				paylist.put("MANAGEMENT_FEE_RATE", context.contextMap.get("MANAGEMENT_FEE_RATE"));
				paylist.put("HEAD_HIRE_PERCENT", DataUtil.doubleUtil(context.contextMap.get("HEAD_HIRE_PERCENT")));
				paylist.put("MANAGEMENT_FEE", DataUtil.doubleUtil(context.contextMap.get("MANAGEMENT_FEE")));
				paylist.put("HEAD_HIRE", DataUtil.doubleUtil(context.contextMap.get("HEAD_HIRE")));
				paylist.put("FLOAT_RATE", context.contextMap.get("FLOAT_RATE"));
				paylist.put("YEAR_INTEREST", context.contextMap.get("YEAR_INTEREST"));
				paylist.put("YEAR_INTEREST_TYPE", context.contextMap.get("YEAR_INTEREST_TYPE"));
				//paylist.put("TR_RATE", context.contextMap.get("TR_RATE"));
				//paylist.put("TR_IRR_RATE", context.contextMap.get("TR_IRR_RATE"));
				//paylist.put("TR_RATE", 0);
				//paylist.put("TR_IRR_RATE", 0);
				PaylistUtil.setBaseRate(paylist);
				//paylist.put("SALES_TAX_RATE", context.contextMap.get("SALES_TAX_RATE"));
				//paylist.put("INSURE_BASE_RATE", context.contextMap.get("INSURE_BASE_RATE"));
				//paylist.put("STAMP_TAX_TOPRIC", context.contextMap.get("STAMP_TAX_TOPRIC"));
				//paylist.put("STAMP_TAX_MONTHPRIC", context.contextMap.get("STAMP_TAX_MONTHPRIC"));
				//paylist.put("STAMP_TAX_INSUREPRIC", context.contextMap.get("STAMP_TAX_INSUREPRIC"));
				//paylist.put("FINE_RATE", context.contextMap.get("FINE_RATE"));
				//paylist.put("FINE_TYPE", context.contextMap.get("FINE_TYPE"));
				paylist.put("LEASE_RZE", context.contextMap.get("LEASE_RZE"));
				paylist.put("PAY_WAY", context.contextMap.get("PAY_WAY"));
				paylist.put("START_DATE", HTMLUtil.parseDateParam(String.valueOf(context.contextMap.get("START_DATE")), null));
				paylist.put("BUSINESS_TRIP_PRICE", DataUtil.doubleUtil(context.contextMap.get("BUSINESS_TRIP_PRICE")));
				paylist.put("EQUPMENT_ADDRESS", context.contextMap.get("EQUPMENT_ADDRESS"));
				paylist.put("DEAL_WAY", context.contextMap.get("DEAL_WAY"));
				paylist.put("BUY_INSURANCE_WAY", context.contextMap.get("BUY_INSURANCE_WAY"));
				paylist.put("INSURANCE_COMPANY_ID", context.contextMap.get("INSURANCE_COMPANY_ID"));
				paylist.put("BUY_INSURANCE_TIME", context.contextMap.get("BUY_INSURANCE_TIME"));
				paylist.put("INSURE_REBATE_RATE", context.contextMap.get("INSURE_REBATE_RATE"));
				
				paylist.put("PLEDGE_AVE_PRICE", DataUtil.doubleUtil(context.contextMap.get("PLEDGE_AVE_PRICE")));
				paylist.put("PLEDGE_BACK_PRICE", DataUtil.doubleUtil(context.contextMap.get("PLEDGE_BACK_PRICE")));
				paylist.put("PLEDGE_LAST_PRICE", DataUtil.doubleUtil(context.contextMap.get("PLEDGE_LAST_PRICE")));
				paylist.put("PLEDGE_LAST_PERIOD", context.contextMap.get("PLEDGE_LAST_PERIOD"));
				paylist.put("PLEDGE_ENTER_WAY", context.contextMap.get("PLEDGE_ENTER_WAY"));
				paylist.put("PLEDGE_ENTER_CMPRICE", DataUtil.doubleUtil(context.contextMap.get("PLEDGE_ENTER_CMPRICE")));
				paylist.put("PLEDGE_ENTER_CMRATE", DataUtil.doubleUtil(context.contextMap.get("PLEDGE_ENTER_CMRATE")));
				paylist.put("PLEDGE_ENTER_AG", DataUtil.doubleUtil(context.contextMap.get("PLEDGE_ENTER_AG")));
				//paylist.put("LOAN_RATE", context.contextMap.get("LOAN_RATE"));
				//paylist.put("MANAGE_RATE", context.contextMap.get("MANAGE_RATE"));
				
				
				if (paylist.get("START_DATE") != null ) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime((Date)paylist.get("START_DATE"));
					calendar.add(Calendar.MONTH, (DataUtil.intUtil(paylist.get("LEASE_PERIOD")) - 1)*DataUtil.intUtil(paylist.get("LEASE_TERM")));
					paylist.put("END_DATE", calendar.getTime());
				} else {
					paylist.put("END_DATE", "");
				}
				
				String textarea = context.getRequest().getParameter("textarea");
				String[] t = textarea.trim().split("\n");

				for(int i = 0 ;i<t.length ; i++){
					//每行支付表
					String s = t[i];
					String[] t2 = s.trim().split("\t");
					//将每行支付表内容 放到 一个List里
					List list = new ArrayList<String>();
					
					for(int x=0;x<t2.length;x++){
						String s2 = t2[x];
						//去除多余空格项
						if(!s2.isEmpty()){
							list.add(s2.trim());
						}						
					}
					paylines.add(ListToMap(list));
				}
				//
				
				paylist.put("paylines", paylines);
				
				StartPayService.packagePaylines(paylist);
				List<Map> irrMonthPaylines = new ArrayList<Map>();
				irrMonthPaylines = (List<Map>) paylist.get("irrMonthPaylines");
															
				payInusres = (List<Map>) DataAccessor.query("collectionManage.readPayInusres", context.contextMap, DataAccessor.RS_TYPE.LIST);
				paylist.put("payInusres", payInusres);
				//
				payOtherFees = (List<Map>) DataAccessor.query("collectionManage.readPayOtherFees", context.contextMap, DataAccessor.RS_TYPE.LIST);
				paylist.put("payOtherFees", payOtherFees);
				
				//计算印花税
				stampTax(paylist);
				//PayUtils.calcalatePaylineIRR(paylist);
				
				outputMap.put("paylist", paylist);
				context.getRequest().getSession().setAttribute("s_importPaylist", paylist);
				
				//
				Map baseRate = PayRate.getBaseRate();
				outputMap.put("baseRateJson", Output.serializer.serialize(baseRate));
				//
				insureCompanyList = (List<Map>) DataAccessor.query("insuCompany.queryInsureCompanyListForSelect", null, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureCompanyList", insureCompanyList);
				insureTypeList = (List<Map>) DataAccessor.query("insureType.queryInsureTypeList", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureTypeList", insureTypeList);
				outputMap.put("insureTypeJsonList", Output.serializer.serialize(insureTypeList));
				
				Map dataDictionaryMap = new HashMap();
				dataDictionaryMap.put("dataType", "支付方式");
				payWays = (List) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("payWays", payWays);
				
				dataDictionaryMap.put("dataType", "租赁期满处理方式");
				dealWays = (List) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("dealWays", dealWays);
				
				suplList=(List)DictionaryUtil.getDictionary("供应商保证");
				outputMap.put("suplList", suplList);
				
			} catch (Exception e) {
				e.printStackTrace();
				errList.add("异常信息："+e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		
		
		outputMap.put("errList", errList);

		Output.jspOutput(outputMap, context, "/collection/importPaylistShow.jsp");
	}
	
	private void stampTax(Map paylist) {
		//康侃 支付表设定启租日和更改
		double HUNDRED = 100D;
		int MONTHS_OF_YEAR = 12;
		//设备总价
		double leaseTopric = DataUtil.doubleUtil(paylist.get("LEASE_TOPRIC"));
		//购销合同印花税比率
		double stampTaxTopric = DataUtil.doubleUtil(paylist.get("STAMP_TAX_TOPRIC"));
		
		List<Map> paylines = (List<Map>) paylist.get("paylines");
		double sumMonthPrice = 0d;
		double monthPrice = 0d;
		//租赁合同印花税比率
		double stampTaxMonthpric = DataUtil.doubleUtil(paylist.get("STAMP_TAX_MONTHPRIC"));
		// 保险费率/100
		double insureBaseRate = DataUtil.doubleUtil(paylist.get("INSURE_BASE_RATE")) / HUNDRED;
		
		//期数
		int leasePeriod = DataUtil.intUtil(paylist.get("LEASE_PERIOD"));
		//周期
		int leaseTerm = DataUtil.intUtil(paylist.get("LEASE_TERM"));
		
		//
		double tempTotalInsurePrice = leaseTopric * insureBaseRate * leasePeriod * leaseTerm / MONTHS_OF_YEAR;
		
		for (Map payline : paylines) {		
			monthPrice = DataUtil.doubleUtil(payline.get("MONTH_PRICE"));		
			sumMonthPrice += monthPrice;	
		}
		
		double stampTaxInsurepric = DataUtil.doubleUtil(paylist.get("STAMP_TAX_INSUREPRIC"));
		
		double stampTax = leaseTopric * stampTaxTopric / HUNDRED 
				+ sumMonthPrice * stampTaxMonthpric / HUNDRED
				+ tempTotalInsurePrice * stampTaxInsurepric / HUNDRED;
		paylist.put("STAMP_TAX_PRICE", stampTax);
		
		//保证金平均冲抵
		double pledgeAvePrice = DataUtil.doubleUtil(paylist.get("PLEDGE_AVE_PRICE"));
		
		double salesTax = 0d;	
		double irrMonthPrice = 0d;		
		double irrPrice = 0d;
		
		double depositPrice = Math.round( pledgeAvePrice / leasePeriod * HUNDRED) / HUNDRED;
		
		
		double[] cashFlowsTr = new double[leasePeriod+1];		
		double[] cashFlowsIrr = new double[leasePeriod+1];
		
		double leaseRZE = DataUtil.doubleUtil(paylist.get("LEASE_RZE"));
		double pv_own_price=0d;
		double pledgeLastPriod = DataUtil.intUtil(paylist.get("PLEDGE_LAST_PERIOD"));
		
		cashFlowsTr[0] = -leaseRZE;

		double pledgePeriod = DataUtil.doubleUtil(paylist.get("PLEDGE_PERIOD"));
		if (pledgePeriod != 0) {
			cashFlowsIrr[0] =  -leaseTopric;
		} else {
			cashFlowsIrr[0] =  -leaseRZE;
		}
			
		int i = 1;
		int periodNum = 1;
		double renPrice = 0d;
		double pledgeBackPrice = DataUtil.doubleUtil(paylist.get("PLEDGE_BACK_PRICE"));
		double pledgeLastPrice = DataUtil.doubleUtil(paylist.get("PLEDGE_LAST_PRICE"));
		//
		double salesTaxRate = DataUtil.doubleUtil(paylist.get("SALES_TAX_RATE")) / HUNDRED;
		/*if("3".equals(String.valueOf(paylist.get("TAX_PLAN_CODE")))) {//add by ShenQi  加入增值税内含 方案
			salesTaxRate=1/1.17*0.187;
		}*/
		//支付方式
		int payWay = DataUtil.intUtil(paylist.get("PAY_WAY"));
		for (Map payline : paylines) {

			periodNum = DataUtil.intUtil(payline.get("PERIOD_NUM"));			
			renPrice = DataUtil.doubleUtil(payline.get("REN_PRICE"));

			salesTax = Math.round(renPrice * salesTaxRate * HUNDRED) / HUNDRED;

			monthPrice = DataUtil.doubleUtil(payline.get("MONTH_PRICE"));
			
			if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL
					|| payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE
					|| payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {  // 期初				
				if (periodNum >= (leasePeriod - pledgeLastPriod) || periodNum == paylines.size()) {
					irrMonthPrice = 0d;
				} else {
					irrMonthPrice = monthPrice - depositPrice;
				}				
			} else { // 期末				
				if (periodNum > (leasePeriod - pledgeLastPriod)) {
					irrMonthPrice = 0d;
				} else {
					irrMonthPrice = monthPrice - depositPrice;
				}
			}

			if (periodNum == 1) {				
				payline.put("INSURE_PRICE", tempTotalInsurePrice);			
				irrPrice = irrMonthPrice - salesTax - stampTax - tempTotalInsurePrice;				
			} else {			
				payline.put("INSURE_PRICE", 0d);				
				irrPrice = irrMonthPrice - salesTax;				
			}
        	//保证金收入
			if (pledgePeriod != 0 && pledgePeriod == periodNum) {
				irrPrice = irrPrice + pledgeAvePrice + pledgeLastPrice;
			}
			//保证金退回
			if (periodNum == leasePeriod) {
				irrPrice -= pledgeBackPrice;
				irrMonthPrice -= pledgeBackPrice;
			}
			//为利差算的实际本金
			if (periodNum == 1) {
				
				pv_own_price = leaseRZE - irrMonthPrice +renPrice;
				
			} else {
				
				pv_own_price =pv_own_price-irrMonthPrice +renPrice;
				
			}
			
        	irrPrice = Math.round(irrPrice * HUNDRED) / HUNDRED;      	
        	irrMonthPrice = Math.round(irrMonthPrice * Math.pow(10, 3))/Math.pow(10, 3);
        	
        	payline.put("DEPOSIT_PRICE", depositPrice);      	
        	payline.put("SALES_TAX", salesTax);       	
        	//payline.put("REAL_OWN_PRICE", DataUtil.doubleUtil(payline.get("LAST_PRICE")) - depositPrice);
        	payline.put("REAL_OWN_PRICE", pv_own_price);
        	cashFlowsTr[i] = irrMonthPrice;
        	
        	cashFlowsIrr[i] = irrPrice;
        	i++;      	
		}
				
		double trRate = Math.round(IRRUtils.getIRR(cashFlowsTr, Double.NaN) * 12.0d * Math.pow(10, 10)) / Math.pow(10, 8);
		//根据应付租金算 客户TR
		paylist.put("TR_RATE", trRate);		
		double trIrrRate = Math.round(IRRUtils.getIRR(cashFlowsIrr, Double.NaN) * 12.0d * Math.pow(10, 10)) / Math.pow(10, 8);
		//根据净现金流 实际TR
		paylist.put("TR_IRR_RATE", trIrrRate);
		
	}

	/**
	 *
	 * @param list
	 * @return
	 */
	public static Map ListToMap(List list){
		Map map = new HashMap();
		map.put("PERIOD_NUM", list.get(0));
		map.put("PAY_DATE", HTMLUtil.parseDateParam(String.valueOf(list.get(1)),null));		
		map.put("IRR_MONTH_PRICE", DataUtil.doubleUtil(list.get(2)));
		map.put("IRR_PRICE", DataUtil.doubleUtil(list.get(3)));
		map.put("MONTH_PRICE", DataUtil.doubleUtil(list.get(4)));
		map.put("OWN_PRICE", DataUtil.doubleUtil(list.get(5)));
		map.put("REN_PRICE", DataUtil.doubleUtil(list.get(6)));
		map.put("LAST_PRICE", DataUtil.doubleUtil(list.get(7)));
		map.put("SALES_TAX", DataUtil.doubleUtil(list.get(8)));
		return map;
	}
	/**
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void saveImportPaylist(Context context) {	
		Map paylist = (Map) context.getRequest().getSession().getAttribute("s_importPaylist");
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		Long newrecpid = null;	
		
		paylist.put("S_EMPLOYEEID", context.contextMap.get("s_employeeId"));		
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
			try {
				DataAccessor.getSession().startTransaction();
				
				DataAccessor.getSession().update("handlePaylist.revokePaylist", paylist);
				newrecpid = (Long) DataAccessor.getSession().insert("handlePaylist.insertNewVersionPaylist", paylist);
				
				paylist.put("OLDRECPID", paylist.get("RECP_ID"));
				paylist.put("NEWRECPID", newrecpid);
				DataAccessor.getSession().insert("handlePaylist.createPayEquipmentsByOldRecpId", paylist);
				DataAccessor.getSession().update("handlePaylist.revokePayEquipments", paylist);
				
				
				
				DataAccessor.getSession().update("handlePaylist.revokeIrr", paylist);
				List<Map> irrMonthPaylines = (List<Map>) paylist.get("irrMonthPaylines");
				for (Map irrMonthPayline : irrMonthPaylines) {				
					irrMonthPayline.put("RECP_ID", newrecpid);
					irrMonthPayline.put("S_EMPLOYEEID", context.contextMap.get("s_employeeId"));				
					DataAccessor.getSession().insert("handlePaylist.createIrr", irrMonthPayline);
					
				}
				
				
				DataAccessor.getSession().update("handlePaylist.revokePayline", paylist);
				List<Map> paylines = (List<Map>) paylist.get("paylines");
				for (Map payline : paylines) {				
					payline.put("RECP_ID", newrecpid);
					payline.put("S_EMPLOYEEID", context.contextMap.get("s_employeeId"));				
					DataAccessor.getSession().insert("collectionManage.createPaylines", payline);
					
				}
				

				DataAccessor.getSession().update("handlePaylist.revokePayInsure", paylist);
				List<Map> payInusres = (List<Map>) paylist.get("payInusres");
				for (Map insure : payInusres) {				
					insure.put("RECP_ID", newrecpid);
					insure.put("S_EMPLOYEEID", context.contextMap.get("s_employeeId"));				
					DataAccessor.getSession().insert("collectionManage.createPayInsures", insure);
				
				}
				

				DataAccessor.getSession().update("handlePaylist.revokePayOtherFee", paylist);
				List<Map>  payOtherFees = (List<Map>) paylist.get("payOtherFees");
				for (Map otherFee : payOtherFees) {					
					otherFee.put("RECP_ID", newrecpid);
					otherFee.put("S_EMPLOYEEID", context.contextMap.get("s_employeeId"));					
					DataAccessor.getSession().insert("collectionManage.createPayOtherFee", otherFee);
					
				}
				

				DataAccessor.getSession().insert("handlePaylist.createPayRemarksByOldRecpId", paylist);
				DataAccessor.getSession().update("handlePaylist.revokePayRemarks", paylist);
				

				DataAccessor.getSession().commitTransaction();
				
			} catch (SQLException e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} finally {
				try {
					DataAccessor.getSession().endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
			}
		}
		
		context.getRequest().getSession().removeAttribute("s_importPaylist");
		
		if (errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=collectionManage.queryPaylist");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
		
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
	
	
	public static void deleteCollectionIrrMonthPrice(Map paylist) throws SQLException {
		
		List<Map> irrMonthPaylines = (List<Map>) paylist.get("irrMonthPaylines");
		
		HashMap map = new HashMap();
		map.put("recp_id", paylist.get("RECP_ID"));
		map.put("S_EMPLOYEEID", paylist.get("S_EMPLOYEEID"));

		DataAccessor.getSession().startBatch();
		
		
		DataAccessor.getSession().insert("collectionManage.deleteCollectionplanIrr", map);
		
		DataAccessor.getSession().executeBatch();
	}
}
