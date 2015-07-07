package com.brick.settle.service;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.util.LeaseUtil;
import com.brick.baseManage.service.BusinessLog;
import com.brick.collection.service.StartPayService;
import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.DataUtil;
import com.brick.util.web.HTMLUtil;
import com.ibatis.sqlmap.client.SqlMapClient;

public class SettleManage extends AService {
	Log logger = LogFactory.getLog(SettleManage.class);
	
	
	/**
	 * 财务结清表
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void querySettleList(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		DataWrap dw = null;
		boolean applySettle=false;
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				/*2011/12/16 Yang Yun */
				//context.contextMap.put("fundStatus", 0) ;
				/*2011/12/16 Yang Yun */
				Map rsMap = null;
				Map paramMap = new HashMap();
				paramMap.put("id", context.contextMap.get("s_employeeId"));
				rsMap = (Map) DataAccessor.query("employee.getEmpInforById", paramMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("p_usernode", rsMap.get("NODE"));
				dw = (DataWrap) DataAccessor.query("settleManage.querySettleList", context.contextMap, DataAccessor.RS_TYPE.PAGED);
			//Add by Michael 2012 09-14  结清申请按钮权限单独切出来
				List<String> resourceIdList=(List<String>) DataAccessor.query("supplier.getResourceIdListByEmplId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				for(int i=0;resourceIdList!=null&&i<resourceIdList.size();i++) {
					if("259".equals(resourceIdList.get(i))) {
						applySettle=true;
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
		outputMap.put("applySettle", applySettle);
		outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
		outputMap.put("QSTART_DATE", context.contextMap.get("QSTART_DATE"));
		outputMap.put("QEND_DATE", context.contextMap.get("QEND_DATE"));
		outputMap.put("QSELECT_STATUS", context.contextMap.get("QSELECT_STATUS"));

		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/collection/querySettleList.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	/**
	 * 判断提前结清还是
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void querySettle(Context context){
		List errList = context.errList ;
		Map outputMap = new HashMap();
		Integer isSettle = 0 ;	
		Integer realPeriod = 0 ;
		Double selectSettleMoney = 0.0 ;//已缴金额
		if(context.contextMap.get("RECP_ID") != null && !context.contextMap.get("RECP_ID").toString().equals("")){
			try {
				//isSettle = (Integer) DataAccessor.query("settleManage.querySettle", context.contextMap, DataAccessor.RS_TYPE.OBJECT) ;
				context.contextMap.put("zujin", "租金") ;
				//realPeriod = (Integer)DataAccessor.query("settleManage.queryRealPeriod", context.contextMap, DataAccessor.RS_TYPE.OBJECT) ;
				//取出已缴金额
				//selectSettleMoney = (Double) DataAccessor.query("settleManage.selectSettleMoney", context.contextMap, DataAccessor.RS_TYPE.OBJECT) ;
				//outputMap.put("settleMoney", selectSettleMoney) ;
				//outputMap.put("realPeriod", realPeriod) ;
				outputMap.put("RECP_ID", context.contextMap.get("RECP_ID")) ;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Map paylist = null;
//			List<Map> payEquipments = null;
//			List<Map> payInusres = null;
//			List<Map> payOtherFees = null;
//			List<Map> paylines = null;
//			
//			List<Map> insureCompanyList = null;
//			List<Map> insureTypeList = null;
//			List<Map> payWays = null;
//			List<Map> dealWays = null;
			
			/*-------- data access --------*/		
			if(errList.isEmpty()){	
				
				try {
					paylist = (Map) DataAccessor.query("collectionManage.readPaylistById", context.contextMap, DataAccessor.RS_TYPE.MAP);
//					//
//					payEquipments = (List<Map>) DataAccessor.query("collectionManage.readPayEquipments", context.contextMap, DataAccessor.RS_TYPE.LIST);
//					paylist.put("payEquipments", payEquipments);
//					//
//					payInusres = (List<Map>) DataAccessor.query("collectionManage.readPayInusres", context.contextMap, DataAccessor.RS_TYPE.LIST);
//					paylist.put("payInusres", payInusres);
//					//
//					payOtherFees = (List<Map>) DataAccessor.query("collectionManage.readPayOtherFees", context.contextMap, DataAccessor.RS_TYPE.LIST);
//					paylist.put("payOtherFees", payOtherFees);
//					//
//					paylines = (List<Map>) DataAccessor.query("collectionManage.readPaylines", context.contextMap, DataAccessor.RS_TYPE.LIST);
//					paylist.put("paylines", paylines);
//					//
//					List<Map> irrMonthPaylines = (List<Map>)DataAccessor.query("collectionManage.readCollectionplanSchemaIrrByRecpid", context.contextMap, DataAccessor.RS_TYPE.LIST);
//					// 2010-09-26 wjw v1.6
//					StartPayService.setIrrMonthPayline(paylist, irrMonthPaylines);
//					
					outputMap.put("paylist", paylist);
//					
//					insureCompanyList = (List<Map>) DataAccessor.query("insuCompany.queryInsureCompanyListForSelect", null, DataAccessor.RS_TYPE.LIST);
//					outputMap.put("insureCompanyList", insureCompanyList);
//					insureTypeList = (List<Map>) DataAccessor.query("insureType.queryInsureTypeList", context.contextMap, DataAccessor.RS_TYPE.LIST);
//					outputMap.put("insureTypeList", insureTypeList);
//					
//					Map dataDictionaryMap = new HashMap();
//					dataDictionaryMap.put("dataType", "支付方式");
//					payWays = (List) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
//					outputMap.put("payWays", payWays);
//					
//					dataDictionaryMap.put("dataType", "租赁期满处理方式");
//					dealWays = (List) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
//					outputMap.put("dealWays", dealWays);
//						
//					
					} catch (Exception e) {
						e.printStackTrace();
						LogPrint.getLogStackTrace(e, logger);
						errList.add(e);
					}
				}
				//outputMap.put("FLAG", context.contextMap.get("FLAG"));
				//outputMap.put("RECP_ID", context.contextMap.get("RECP_ID"));
				
				//context.contextMap.put("recp_code", paylist.get("RECP_CODE")) ;
		} else {
			errList.add("列出接清单错误!请联系管理员") ;
			logger.error("RECP_ID取不到") ;
		}
//		if(isSettle > 0){
//			//正常结清
//			this.queryRecpNormalSettleInfo(context,outputMap) ;
//		} else {
//			//提前结清
//			this.queryRecpAheadSettleInfo(context,outputMap) ;
//		}
		//现在不管正常 提前 都是同一个算法
		this.queryRecpNormalSettleInfo(context,outputMap) ;
	}
	/**
	 *  提前结清
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryRecpAheadSettleInfo(Context context,Map outputMap) {
		List errorList = context.errList;
		Map custIncomeMap = null;
		List custDecomposeList = null;
		Integer settlePeriodNum = null ;//结清的期数
		String cust_name = "";
		if (errorList.isEmpty()) {
			try {
				custDecomposeList = (List) DataAccessor.query(
						"decompose.queryRecpAheadSettle", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
				//取出已缴期数
				settlePeriodNum = (Integer) DataAccessor.query("decompose.settleToPeriodNum", context.contextMap, DataAccessor.RS_TYPE.OBJECT) ;
				
			} catch (Exception e) {
				logger
						.error("com.brick.decompose.service.DecomposeManager.queryRecpAheadSettleInfo"
								+ e.getMessage());
				e.printStackTrace();
				errorList
						.add("com.brick.decompose.service.DecomposeManager.queryRecpAheadSettleInfo"
								+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errorList.add(e);
			}
			if(custDecomposeList != null && custDecomposeList.size() > 0 ){
				Double benJin = 0.0 ;
				Double weiYueJin = 0.0 ;
				Double liuGou = 0.0 ;
				for (int i = custDecomposeList.size() - 1 ; i >= 0 ;i--){
					Map map = (Map) custDecomposeList.get(i) ;
					if(settlePeriodNum != null && settlePeriodNum > 1 && map.get("RECD_PERIOD") != null &&  Integer.valueOf((map.get("RECD_PERIOD").toString())) > 0 && settlePeriodNum > Integer.valueOf((map.get("RECD_PERIOD").toString()))){
						custDecomposeList.remove(i) ;
					} else {
						if(map.get("FICB_ITEM").equals("本金")){
							benJin += (Double)map.get("SPRICE") ;
						} else if(map.get("FICB_ITEM").equals("违约金")){
							weiYueJin += (Double)map.get("SPRICE") ;
						} if(map.get("FICB_ITEM").equals("设备留购价")){
							liuGou += (Double)map.get("SPRICE") ;
						}
					}
				}
				outputMap.put("benJin", benJin) ;
				outputMap.put("weiYueJin", weiYueJin) ;
				outputMap.put("liuGou", liuGou) ;
				outputMap.put("heJi", benJin + weiYueJin + liuGou) ;
			}
			outputMap.put("cust_code", context.contextMap.get("cust_code"));
			outputMap.put("recp_code", context.contextMap.get("recp_code"));
			outputMap.put("settle_flag", "ahead");
			outputMap.put("cust_name", cust_name);
			outputMap.put("custIncomeMap", custIncomeMap);
			outputMap.put("custDecomposeList", custDecomposeList);
			outputMap.put("FUND_TYPE", 2) ;
			Output.jspOutput(outputMap, context,
					"/collection/showSettleList.jsp");
		}
	}
	/**
	 * 根据支付表查询需要正常结清的应分解项
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryRecpNormalSettleInfo(Context context,Map outputMap) {
		List errorList = context.errList;
		Map settleMap = null;
		if (errorList.isEmpty()) {
			try {
				context.contextMap.put("zujin", "租金") ;
				context.contextMap.put("zujinfaxi", "租金罚息") ;
				context.contextMap.put("sblgj", "设备留购价") ;
				context.contextMap.put("lawyfee", "法务费用") ;
				
				//Add by Michael 2012 12-21 增加是否是预估结清
				if(null!=context.contextMap.get("FLAG") && "FORCAST".equals(context.contextMap.get("FLAG"))){
					outputMap.put("QUERY_DATE", context.contextMap.get("QUERY_DATE"));
					//预估结清查询逻辑
					settleMap = (Map) DataAccessor.query("settleManage.selectForcastSettlePrice", context.contextMap,DataAccessor.RS_TYPE.MAP);
				}else{
					settleMap = (Map) DataAccessor.query("settleManage.selectSettlePrice", context.contextMap,DataAccessor.RS_TYPE.MAP);
				}
				outputMap.put("settleMap", settleMap) ;
			} catch (Exception e) {
				logger
						.error("com.brick.decompose.service.DecomposeManager.queryRecpNormalSettleInfo"
								+ e.getMessage());
				e.printStackTrace();
				errorList
						.add("com.brick.decompose.service.DecomposeManager.queryRecpNormalSettleInfo"
								+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errorList.add(e);
			}
			outputMap.put("cust_code", context.contextMap.get("cust_code"));
			outputMap.put("recp_code", context.contextMap.get("recp_code"));
			outputMap.put("settle_flag", "normal");
			outputMap.put("FUND_TYPE", 1) ;
			if(null!=context.contextMap.get("FLAG") && "FORCAST".equals(context.contextMap.get("FLAG"))){
				Output.jspOutput(outputMap, context,
						"/collection/showForcastSettleList.jsp");
			}else{
				Output.jspOutput(outputMap, context,
						"/collection/showSettleList.jsp");
			}

		}
	}
//	public void queryRecpNormalSettleInfo(Context context,Map outputMap) {
//		List errorList = context.errList;
//		Map custIncomeMap = null;
//		List custDecomposeList = null;
//		Integer settlePeriodNum = null ;//结清的期数
//		if (errorList.isEmpty()) {
//			try {
//				custDecomposeList = (List) DataAccessor.query(
//						"decompose.queryRecpNormalSettle", context.contextMap,
//						DataAccessor.RS_TYPE.LIST);
//				//取出已缴期数
//				settlePeriodNum = (Integer) DataAccessor.query("decompose.settleToPeriodNum", context.contextMap, DataAccessor.RS_TYPE.OBJECT) ;
//			} catch (Exception e) {
//				logger
//				.error("com.brick.decompose.service.DecomposeManager.queryRecpNormalSettleInfo"
//						+ e.getMessage());
//				e.printStackTrace();
//				errorList
//				.add("com.brick.decompose.service.DecomposeManager.queryRecpNormalSettleInfo"
//						+ e.getMessage());
//				LogPrint.getLogStackTrace(e, logger);
//				errorList.add(e);
//			}
//			if(custDecomposeList != null && custDecomposeList.size() > 0 ){
//				Double benJin = 0.0 ;
//				Double liXi = 0.0 ;
//				Double liuGou = 0.0 ;
//				for (int i = custDecomposeList.size() - 1 ; i >= 0 ;i--){
//					Map map = (Map) custDecomposeList.get(i) ;
//					if(settlePeriodNum != null && settlePeriodNum > 1 && map.get("RECD_PERIOD") != null &&  Integer.valueOf((map.get("RECD_PERIOD").toString())) > 0 && settlePeriodNum > Integer.valueOf((map.get("RECD_PERIOD").toString()))){
//						custDecomposeList.remove(i) ;
//					} else {
//						if(map.get("FICB_ITEM").equals("本金")){
//							benJin += (Double)map.get("SPRICE") ;
//						} else if(map.get("FICB_ITEM").equals("利息")){
//							liXi += (Double)map.get("SPRICE") ;
//						} if(map.get("FICB_ITEM").equals("设备留购价")){
//							liuGou += (Double)map.get("SPRICE") ;
//						}
//					}	
//				}
//				outputMap.put("benJin", benJin) ;
//				outputMap.put("liXi", liXi) ;
//				outputMap.put("liuGou", liuGou) ;
//				outputMap.put("heJi", benJin + liXi + liuGou) ;
//			}
//			outputMap.put("cust_code", context.contextMap.get("cust_code"));
//			outputMap.put("recp_code", context.contextMap.get("recp_code"));
//			outputMap.put("settle_flag", "normal");
//			outputMap.put("custIncomeMap", custIncomeMap);
//			outputMap.put("custDecomposeList", custDecomposeList);
//			
//			outputMap.put("FUND_TYPE", 1) ;
//			Output.jspOutput(outputMap, context,
//			"/collection/showSettleList.jsp");
//		}
//	}
	/**
	 * 添加结清表
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void create(Context context) {
		List errList = context.errList ;
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
		if(context.contextMap.get("startDate") != null && !context.contextMap.get("startDate").toString().trim().equals("")){
			try {
				context.contextMap.put("START_DATE", sf.parse((String) context.contextMap.get("startDate"))) ;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if(context.contextMap.get("endDate") != null && !context.contextMap.get("endDate").toString().trim().equals("")){
			try {
				context.contextMap.put("END_DATE", sf.parse((String) context.contextMap.get("endDate"))) ;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		try{
			DataAccessor.execute("settleManage.create", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT) ;
		}catch(Exception e){
			LogPrint.getLogStackTrace(e, logger) ;
			errList.add("添加结清表错误!请联系管理员") ;
		}
		if(errList.isEmpty()){
			Output.jspSendRedirect(context, "defaultDispatcher?__action=settleManage.querySettleList") ;
		}
	}
	
	/**
	 * 查询结清表
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void querySettleDetail(Context context) {
		Map outputMap = new HashMap() ;
		List errList = context.errList ;
		List dw = null ;
		try{
			dw = (List) DataAccessor.query("settleManage.querySettleDetail", context.contextMap, DataAccessor.RS_TYPE.LIST) ;
		} catch(Exception e){
			LogPrint.getLogStackTrace(e, logger) ;
			errList.add("结清表查询错误!请联系管理员") ; 
		}
		if(errList.isEmpty()){
			outputMap.put("dw", dw) ;
			Output.jspOutput(outputMap, context, "/collection/querySettleDetail.jsp") ;
		}
	}
	/**
	 * 结算分解列表
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryCheckSettleList(Context context){
		Map outputMap = new HashMap() ;
		List errList = context.errList ;
		DataWrap dw = null ;
		try{
			dw =  (DataWrap) DataAccessor.query("settleManage.queryCheckSettleList", context.contextMap, DataAccessor.RS_TYPE.PAGED) ;
		}catch(Exception e){
			LogPrint.getLogStackTrace(e, logger) ;
			errList.add("结清确认表查询错误!请联系管理员") ;
		}
		if(errList.isEmpty()){
			outputMap.put("dw", dw) ;
			outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
			outputMap.put("QSTART_DATE", context.contextMap.get("QSTART_DATE"));
			outputMap.put("QEND_DATE", context.contextMap.get("QEND_DATE"));
			outputMap.put("QSELECT_STATUS", context.contextMap.get("QSELECT_STATUS"));
			Output.jspOutput(outputMap, context, "/collection/queryCheckSettleList.jsp") ;
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	/**
	 * 结清确认查看页
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void showCheckSettle(Context context){
		Map outputMap = new HashMap() ;
		List errList = context.errList  ;
		Map showSettle = null ;
		Map payMap = null ;
		try{
			showSettle = (Map) DataAccessor.query("settleManage.showSettle", context.contextMap, DataAccessor.RS_TYPE.MAP) ;
			if(showSettle != null && showSettle.size() > 0){
				context.contextMap.put("RECP_ID", showSettle.get("RECP_ID")) ;
				payMap = (Map) DataAccessor.query("collectionManage.readPaylistById", context.contextMap, DataAccessor.RS_TYPE.MAP);
				if( showSettle.get("STATE") != null && "1".equals(showSettle.get("STATE").toString())
						&& payMap != null && payMap.get("RECP_STATUS") != null 
						&& !("1".equals(payMap.get("RECP_STATUS").toString())) 
						&& !("3".equals(payMap.get("RECP_STATUS").toString()))){
					context.contextMap.put("jieqing", "结清") ;
					outputMap.put("settleDecompose", DataAccessor.query("settleManage.existSettleDecompose", context.contextMap, DataAccessor.RS_TYPE.OBJECT));
				}
			}
		}catch(Exception e){
			LogPrint.getLogStackTrace(e, logger) ;
			errList.add("结清确认查看错误!请联系管理员") ;
		}
		if(errList.isEmpty()){
			outputMap.put("showSettle", showSettle) ;
			outputMap.put("payMap", payMap) ;
			Output.jspOutput(outputMap, context, "/collection/showCheckSettle.jsp") ;
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	/**
	 * 财务确认通过或驳回
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void updateState(Context context){
		Map outputMap = new HashMap() ;
		List errList = context.errList  ;
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		try{
			sqlMapper.startTransaction() ;
			
			sqlMapper.update("settleManage.updateState", context.contextMap) ;
//			if(context.contextMap.get("state").equals("1")){
//				if("1".equals(context.contextMap.get("fund_type"))){
//					context.contextMap.put("recp_status", "1") ;
//				} else {
//					context.contextMap.put("recp_status", "3") ;
//				}
				//财务确认 更改支付表状态为5表示 结清确认状态中 防止租金分解
			if(context.contextMap.get("state").equals("1")){
				context.contextMap.put("fund_type", 5) ;
				context.contextMap.put("recp_status", 5) ;
				sqlMapper.update("collectionManage.updateFundStatus", context.contextMap) ;
			}else if (context.contextMap.get("state").equals("2")){
				context.contextMap.put("fund_type", 0) ;
				context.contextMap.put("recp_status", 0) ;
				sqlMapper.update("collectionManage.updateFundStatus", context.contextMap) ;
			}
			sqlMapper.commitTransaction() ;
			
			//---- Add by Michael 2012 02/22 结清申请时 通过  or 驳回时 增加日志------
			Long creditId = null;
			Long contractId = (Long) DataAccessor.query("settleManage.queryRECTID", context.contextMap, DataAccessor.RS_TYPE.OBJECT) ;
			String logType = "结清确认";
			String logTitle = "结清确认";
			Long userId = DataUtil.longUtil(context.contextMap.get("s_employeeId"));
			Long otherId = null;
			int state = 1;
			String logCode = "";
			String memo = "";
					
			if(context.contextMap.get("state").equals("1")){					
				memo = logTitle+"-通过";					
			}else if (context.contextMap.get("state").equals("2")){
				memo = logTitle+"-驳回";	
			}
			BusinessLog.addBusinessLog(creditId, contractId, logType, logTitle, logCode, memo, state, userId, otherId,(String)context.contextMap.get("IP"));	
			//------------------------------------------------------------------------------------------------------------
		} catch(Exception e){
			LogPrint.getLogStackTrace(e, logger) ;
			errList.add("通过/驳回结清状态错误!请联系管理员") ;
		} finally{
			try {
				sqlMapper.endTransaction() ;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(errList.isEmpty()){
			Output.jspSendRedirect(context, "defaultDispatcher?__action=settleManage.queryCheckSettleList") ;
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	public void expPDF(Context context){
		SettlePDF.expSettlePDF(context) ;
	}
	public void expBlankPDF(Context context){
		SettlePDF.expBlankSettlePDF(context) ;
	}
	public void SettleTransferProvePDF(Context context){
		SettleTransferProvePDF.expPDF(context) ;
	}
	public void expExpcel(Context context){
		List errList = context.errList ;
		try{
			String[] ids = HTMLUtil.getParameterValues(context.getRequest(),"ids","");
			context.contextMap.put("ids",ids) ;
//			context.contextMap.put("jieqing", "结清") ;
			context.contextMap.put("content", DataAccessor.query("settleManage.expSettleDetailExcel", context.contextMap, DataAccessor.RS_TYPE.LIST)) ;
		}catch(Exception e){
			e.printStackTrace() ;
			LogPrint.getLogStackTrace(e, logger) ;
		}
		SettleExcel exl = new SettleExcel() ;
		exl.expExcel(context);
	}
	
	/**
	 * 支付表列表
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryAllRenList(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		DataWrap dw = null;
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				dw = (DataWrap) DataAccessor.query("settleManage.queryAllRenList", context.contextMap, DataAccessor.RS_TYPE.PAGED);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		/*-------- output --------*/
		outputMap.put("dw", dw);
		outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));

		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/collection/queryInvoiceSuspenList.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}	
	
	/*
	 * Add by Michael 2012-3-7
	 * 暂停发票开具申请
	 */
	public void createInvoiceSuspen(Context context) {
		List errList = context.errList ;	
		String errorMsg=null;
		try{
			Long num = (Long) DataAccessor.query("settleManage.queryInvoiceSuspenID", context.contextMap, DataAccessor.RS_TYPE.OBJECT) ;
			if(num.intValue()==0){
				DataAccessor.execute("settleManage.createInvoiceSuspen", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT) ;
			}else{
				errorMsg = "此合同已申请，请联系财务确认。";
			}
			}catch(Exception e){
			LogPrint.getLogStackTrace(e, logger) ;
			errList.add("申请暂停开发票错误!请联系管理员") ;
		}
		context.contextMap.put("errorMsg", errorMsg);
		if(errList.isEmpty()){
			Output.jspSendRedirect(context, "defaultDispatcher?__action=settleManage.queryAllRenList") ;
		}
	}
	
	/*
	 * Add by Michael 2012-3-7
	 * 续发票开具申请
	 */
	public void createApplyInvoiceSuspen(Context context) {
		List errList = context.errList ;	
		String errorMsg=null;
		try{
			Long num = (Long) DataAccessor.query("settleManage.queryApplyInvoiceSuspenID", context.contextMap, DataAccessor.RS_TYPE.OBJECT) ;
			if(num.intValue()==0){
				DataAccessor.execute("settleManage.createApplyInvoiceSuspen", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT) ;
			}else{
				errorMsg = "此合同已申请，请联系财务确认。";
			}
			}catch(Exception e){
			LogPrint.getLogStackTrace(e, logger) ;
			errList.add("申请暂停开发票错误!请联系管理员") ;
		}
		context.contextMap.put("errorMsg", errorMsg);
		if(errList.isEmpty()){
			Output.jspSendRedirect(context, "defaultDispatcher?__action=settleManage.queryAllRenList") ;
		}
	}	

	public void queryAllCkeckInvoiceSuspen(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		DataWrap dw = null;
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				dw = (DataWrap) DataAccessor.query("settleManage.queryAllInvoiceSuspen", context.contextMap, DataAccessor.RS_TYPE.PAGED);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		/*-------- output --------*/
		outputMap.put("dw", dw);
		outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
		outputMap.put("companyCode", context.contextMap.get("companyCode"));
		outputMap.put("companys", LeaseUtil.getCompanys());
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/collection/checkInvoiceSuspenList.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}	
	
	public void ckeckInvoiceSuspen(Context context)
	{
		List errList = context.errList ;	
		String errorMsg=null;
		try{
			Long num = (Long) DataAccessor.query("settleManage.queryApplyInvoiceSuspenID", context.contextMap, DataAccessor.RS_TYPE.OBJECT) ;
			if(num.intValue()==0){
				DataAccessor.execute("settleManage.createApplyInvoiceSuspen", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT) ;
			}else{
				errorMsg = "此合同已申请，请联系财务确认。";
			}
			}catch(Exception e){
			LogPrint.getLogStackTrace(e, logger) ;
			errList.add("暂停、续开发票确认错误!请联系管理员") ;
		}
		context.contextMap.put("errorMsg", errorMsg);
		if(errList.isEmpty()){
			Output.jspSendRedirect(context, "defaultDispatcher?__action=settleManage.queryAllCkeckInvoiceSuspen") ;
		}		
	}
	
	public void queryInvoiceSuspenDetail(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		Map invoiceSuspen = null;
		if(errList.isEmpty()){		
			try {
				invoiceSuspen = (HashMap) DataAccessor.query(
						"settleManage.queryInvoiceSuspenDetail", context.contextMap,
						DataAccessor.RS_TYPE.MAP);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		outputMap.put("invoiceSuspen", invoiceSuspen);
		outputMap.put("ID", context.contextMap.get("ID"));
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/collection/queryInvoiceSuspenDetail.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}	
	
	public void queryApplyInvoiceSuspenDetail(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		Map invoiceSuspen = null;
		if(errList.isEmpty()){		
			try {
				invoiceSuspen = (HashMap) DataAccessor.query(
						"settleManage.queryInvoiceSuspenDetail", context.contextMap,
						DataAccessor.RS_TYPE.MAP);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		outputMap.put("invoiceSuspen", invoiceSuspen);
		outputMap.put("ID", context.contextMap.get("ID"));
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/collection/queryApplyInvoiceSuspenDetail.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}	
	
	public void dueInvoiceSuspen(Context context) {
		Map outputMap = new HashMap();
		List errorList = context.errList;

		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		try {
			sqlMapper.startTransaction() ;			
			
			if (errorList.isEmpty()) {
				
				String check_flag = context.contextMap.get("check_flag").toString();
				String INVOICE_TYPE=String.valueOf(context.contextMap.get("INVOICE_TYPE"));
				Integer check_id = Integer.valueOf(context.contextMap.get(
						"s_employeeId").toString());
				context.contextMap.put("check_id", check_id);	
				if (check_flag.equals("Y")) {
					sqlMapper.update("settleManage.updateInvoiceSuspenPass", context.contextMap) ;
					if (INVOICE_TYPE.equals("0")) {
						sqlMapper.update("settleManage.updateRECPInvoiceSuspen", context.contextMap) ;
					}else if(INVOICE_TYPE.equals("1")){
						sqlMapper.update("settleManage.updateRECPApplyInvoiceSuspen", context.contextMap) ;
					}
				}else if(check_flag.equals("N")){
					sqlMapper.update("settleManage.updateInvoiceSuspenBack", context.contextMap) ;
				}
			}
			sqlMapper.commitTransaction() ;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				sqlMapper.endTransaction() ;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(errorList.isEmpty()){
			Output.jspSendRedirect(context, "defaultDispatcher?__action=settleManage.queryAllCkeckInvoiceSuspen") ;
		}			
	}

	public void showInvoiceSuspen(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		List<Map<String, Object>> resultList = null;
		try {
			resultList = (List<Map<String, Object>>) DataAccessor.query("settleManage.showInvoiceSuspenDetail", context.contextMap, RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
		}
		outputMap.put("resultList", resultList);
		Output.jspOutput(outputMap, context, "/collection/InvoiceSuspenDetail.jsp");
	}	
	
	/*
	 * Add by Michael 2012 -10-08
	 * 增加导出结清金额通知函
	 */
	public void expSettlePayNote(Context context){
		SettlePDF.expSettlePayNotePDF(context) ;
	}
	
	/**
	 * Add by Michael 2012 12-21 增加导出预估结清明细表
	 * @author michael
	 * @param context
	 */
	public void expForcastSettlePDF(Context context){
		SettlePDF.expForcastSettlePDF(context) ;
	}
}
