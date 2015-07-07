package com.brick.credit.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.brick.base.util.LeaseUtil;
import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.OPERATION_TYPE;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DataUtil;
import com.brick.util.web.HTMLUtil;
import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 资信客户往来记录
 * 
 * @author li shaojie
 * @date May 17, 2010
 */

public class CreditPriorRecordsService extends AService {
	Log logger = LogFactory.getLog(CreditPriorRecordsService.class);

	private static SqlMapClient sqlMapper;

	@SuppressWarnings("unchecked")
	public void getCreditPriorRecords(Context context) throws SQLException {
		Map outputMap = new HashMap();
		List errList = context.errList ;
		Map creditMap = null;
		List creditPriorRecords = null;
		List creditPriorProject = null;
		List creditFinaceStatement = null;
		Map cFS_Remark = null;
		List priorContractInfo=null;
		List<Map> memoList = null;	
		
		Map creditPriorFactoryBuld=null;
		Map submitMachineMap=null;
		Map EuqipmentsRemarkMap=null;
		List EuqipmentsList=null;
		List BuyFactoriesList=null;
		List CellFactoriesList=null;
		Map CellFactoriesAllRemarkMap=null;
		List BankCheckBillList=null;
		
		
		List ContactCustomerList=null;
		Map ContactCustomerRemarkMap=null;
		
		List<Map<String,Object>> stockCompanyList=null;
 		//上一个报告的id
		String listCreditId = "" ;
		try {			
			//查询上一次报告的id
			listCreditId = (String) DataAccessor.query("creditPriorRecords.getLestCreditId", context.contextMap, DataAccessor.RS_TYPE.OBJECT) ;
			
			//TODO*******************************************************************************************************************
			//加入查询进货厂商照会记录 add by ShenQi 2012-7-12
			/*stockCompanyList=(List<Map<String,Object>>)DataAccessor.query("creditPriorRecords.getStockCompany",context.getContextMap(),DataAccessor.RS_TYPE.LIST);
			outputMap.put("stockCompanyList",stockCompanyList);*/
			//*******************************************************************************************************************
			ContactCustomerList=(List)DataAccessor.query(
						"creditPriorRecords.queryCCByCreditId",
						context.getContextMap(), DataAccessor.RS_TYPE.LIST);
			if((ContactCustomerList == null || ContactCustomerList.size() <= 0) && listCreditId != null && !listCreditId.equals("")){
				Map temp = new HashMap() ;
				temp.put("credit_id", listCreditId) ;
				ContactCustomerList=(List)DataAccessor.query(
						"creditPriorRecords.queryCCByCreditId",
							temp, DataAccessor.RS_TYPE.LIST);
			}
			outputMap.put("ContactCustomerList", ContactCustomerList);
			
			ContactCustomerRemarkMap=(Map)DataAccessor.query(
					"creditPriorRecords.queryCCRemarkByCreditId",
					context.getContextMap(), DataAccessor.RS_TYPE.MAP);
			outputMap.put("ContactCustomerRemarkMap", ContactCustomerRemarkMap);

			
			memoList = (List) DataAccessor.query(
					"creditReportManage.selectMemo", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			outputMap.put("memoList", memoList);
			context.contextMap.put("data_type", "客户来源");
			creditMap = (Map) DataAccessor.query(
					"creditReportManage.selectCreditBaseInfo",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
			outputMap.put("creditMap", creditMap);
			context.contextMap.put("cust_id", creditMap.get("CUST_ID"));
			/*priorContractInfo = (List) DataAccessor.query(
					"creditPriorRecords.getContractInfoBycust_id",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
					outputMap.put("priorContractInfo", priorContractInfo);*/
			List<Map<String,Object>> resultList=(List<Map<String,Object>>)DataAccessor.query("collectionManage.getRecpListByCustId",context.contextMap,DataAccessor.RS_TYPE.LIST);
			//拿实际剩余本金
			Map<String,Object> settleMap = null;
			List<Map<String,Object>> paylines = null;
			context.contextMap.put("zujin", "租金");				
			context.contextMap.put("zujinfaxi", "租金罚息");
			context.contextMap.put("sblgj", "设备留购价");
			
			for(int i=0;i<resultList.size();i++) {
				int totalPeriod=0;
				int payPeriod=0;
				double totalPrice=0;
				double payPrice=0;
				double restPrice=0;
				context.contextMap.put("RECP_ID",resultList.get(i).get("RECP_ID"));
				settleMap = (Map<String,Object>) DataAccessor.query("settleManage.selectSettlePrice", context.contextMap,DataAccessor.RS_TYPE.MAP);
				paylines = (List<Map<String,Object>>) DataAccessor.query("collectionManage.readPaylines",context.contextMap,DataAccessor.RS_TYPE.LIST);
				resultList.get(i).put("OWN_PRICE", settleMap.get("SUM_OWN_PRICE"));
				if(paylines!=null) {
					totalPeriod=paylines.size();
					for(int j=0;j<paylines.size();j++) {
						totalPrice=totalPrice+Double.valueOf(paylines.get(j).get("IRR_MONTH_PRICE").toString());
						if("PAY".equalsIgnoreCase((String)paylines.get(j).get("FLAG"))) {
							payPeriod=payPeriod+1;
							payPrice=payPrice+Double.valueOf(paylines.get(j).get("IRR_MONTH_PRICE").toString());
						}
					}
				}
				resultList.get(i).put("LEASE_PERIOD", totalPeriod);
				resultList.get(i).put("LEASE_PAY_PERIOD", payPeriod);
				restPrice=totalPrice-payPrice;
				resultList.get(i).put("REST_PRICE", restPrice);
				resultList.get(i).put("REMAINING_PRINCIPAL", LeaseUtil.getRemainingPrincipalByRecpId(String.valueOf(resultList.get(i).get("RECP_ID"))));
			}

			outputMap.put("priorContractInfo", resultList);
			
			creditPriorProject = (List) DataAccessor.query(
					"creditPriorRecords.getCreditPriorProjects",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("creditPriorProject", creditPriorProject);
			
			creditPriorFactoryBuld = (Map) DataAccessor.query(
					"creditPriorRecords.getcreditPriorFactoryBuld",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
			outputMap.put("creditPriorFactoryBuld", creditPriorFactoryBuld);
			submitMachineMap = (Map) DataAccessor.query(
					"creditPriorRecords.getcreditPriorSubmitMachine",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
			outputMap.put("submitMachineMap", submitMachineMap);
			
			EuqipmentsRemarkMap = (Map) DataAccessor.query(
					"creditPriorRecords.getcreditPriorALLEquipRemarks",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
			outputMap.put("EuqipmentsRemarkMap", EuqipmentsRemarkMap);
			
			context.contextMap.put("cust_id", creditMap.get("CUST_ID"));
			//过往记录的设备明细
			EuqipmentsList = (List) DataAccessor.query(
					"creditPriorRecords.getcreditPriorEquipments",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			if((EuqipmentsList == null || EuqipmentsList.size() <= 0) && listCreditId != null && !listCreditId.equals("")){
				Map temp = new HashMap() ;
				temp.put("credit_id", listCreditId) ;
				EuqipmentsList = (List) DataAccessor.query(
						"creditPriorRecords.getcreditPriorEquipments",
						temp, DataAccessor.RS_TYPE.LIST) ;
			}
			outputMap.put("EuqipmentsList", EuqipmentsList);
			
			BuyFactoriesList = (List) DataAccessor.query(
					"creditPriorRecords.getcreditPriorBuyFactorys",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			if((BuyFactoriesList == null || BuyFactoriesList.size() <= 0) && listCreditId != null && !listCreditId.equals("")){
				Map temp = new HashMap() ;
				temp.put("credit_id", listCreditId) ;
				BuyFactoriesList = (List) DataAccessor.query(
						"creditPriorRecords.getcreditPriorBuyFactorys",
						temp, DataAccessor.RS_TYPE.LIST);
			}
			outputMap.put("BuyFactoriesList", BuyFactoriesList);
			
			CellFactoriesList = (List) DataAccessor.query(
					"creditPriorRecords.getcreditPriorCellFactorys",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("CellFactoriesList", CellFactoriesList);
			CellFactoriesAllRemarkMap= (Map) DataAccessor.query(
					"creditPriorRecords.getcreditPriorALLCellFacRemark",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
			outputMap.put("CellFactoriesAllRemarkMap", CellFactoriesAllRemarkMap);
			
			BankCheckBillList = (List) DataAccessor.query(
					"creditPriorRecords.getcreditPriorBankCheckBillSix",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("BankCheckBillList", BankCheckBillList);
			List companyList = null;
			companyList = (List) DataAccessor.query(
					"companyManage.queryCompanyAlias", null,
					DataAccessor.RS_TYPE.LIST);
			//System.out.println(creditMap.get("DECP_ID").toString()+"=========");
			outputMap.put("companyList", companyList);
			
			if(creditPriorProject.size()==0){
				Object obj = DataAccessor.query("creditReportManage.getMaxCredit_id",
						context.contextMap, DataAccessor.RS_TYPE.OBJECT);
				if(obj!=null){
					context.contextMap.put("credit_id", obj);
					creditPriorProject = (List) DataAccessor.query(
							"creditPriorRecords.getCreditPriorProjects",
							context.contextMap, DataAccessor.RS_TYPE.LIST);
					outputMap.put("creditPriorProject", creditPriorProject);
					creditPriorRecords = (List) DataAccessor.query(
							"creditPriorRecords.getCreditPriorRecords",
							context.contextMap, DataAccessor.RS_TYPE.LIST);
					outputMap.put("creditPriorRecords", creditPriorRecords);

					

					creditFinaceStatement = (List) DataAccessor.query(
							"creditPriorRecords.getCreditFinaceStatement",
							context.contextMap, DataAccessor.RS_TYPE.LIST);
					Map v1 = null;
					Map v2 = null;
					Map v3 = null;
					Map v4 = null;
					if (creditFinaceStatement.size() >= 1) {
						v1 = (Map) creditFinaceStatement.get(0);
						v2 = (Map) creditFinaceStatement.get(1);
						v3 = (Map) creditFinaceStatement.get(2);
						v4 = (Map) creditFinaceStatement.get(3);
					}
					outputMap.put("v1", v1);
					outputMap.put("v2", v2);
					outputMap.put("v3", v3);
					outputMap.put("v4", v4);
					cFS_Remark = (Map) DataAccessor.query(
							"creditPriorRecords.getCFS_Remark", context.contextMap,
							DataAccessor.RS_TYPE.MAP);
					outputMap.put("cFS_Remark", cFS_Remark);
				}
			}else{
				creditPriorRecords = (List) DataAccessor.query(
						"creditPriorRecords.getCreditPriorRecords",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("creditPriorRecords", creditPriorRecords);

				

				creditFinaceStatement = (List) DataAccessor.query(
						"creditPriorRecords.getCreditFinaceStatement",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
				Map v1 = null;
				Map v2 = null;
				Map v3 = null;
				Map v4 = null;
				if (creditFinaceStatement.size() >= 1) {
					v1 = (Map) creditFinaceStatement.get(0);
					v2 = (Map) creditFinaceStatement.get(1);
					v3 = (Map) creditFinaceStatement.get(2);
					v4 = (Map) creditFinaceStatement.get(3);
				}
				outputMap.put("v1", v1);
				outputMap.put("v2", v2);
				outputMap.put("v3", v3);
				outputMap.put("v4", v4);
				cFS_Remark = (Map) DataAccessor.query(
						"creditPriorRecords.getCFS_Remark", context.contextMap,
						DataAccessor.RS_TYPE.MAP);
				outputMap.put("cFS_Remark", cFS_Remark);
			}

			Map<String,Object> industryMap=null;
			
			outputMap.put("READ",false);
			industryMap=(Map<String,Object>)DataAccessor.query("creditReportManage.queryIndustryEnvironment",context.contextMap,RS_TYPE.MAP);
			outputMap.put("industryMap",industryMap);
			
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--过往记录错误!请联系管理员") ;
		}
		
		outputMap.put("showFlag", context.contextMap.get("showFlag")==null?"2":context.contextMap.get("showFlag"));//TODO
		if(errList.isEmpty()){
//			Output.jspOutput(outputMap, context, "/credit/creditFrame.jsp");
			//根据报告类型
			int productionType = LeaseUtil.getProductionTypeByCreditId(String.valueOf(context.contextMap.get("credit_id")));
			if(productionType==1){
				Output.jspOutput(outputMap, context, "/credit/equip/creditFrame.jsp");
			}else if(productionType==2){
				Output.jspOutput(outputMap, context, "/credit/truck/creditFrame.jsp");
			}else if(productionType==3){
				Output.jspOutput(outputMap, context, "/credit/car/creditFrame.jsp");
			}
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}

	@SuppressWarnings("unchecked")
	public void getCreditPriorRecordsForShow(Context context) throws SQLException {
		Map outputMap = new HashMap();
		List errList = context.errList ;
		Map creditMap = null;
		List creditPriorRecords = null;
		List creditPriorProject = null;
		List creditFinaceStatement = null;
		Map cFS_Remark = null;
		Map memoMap = null;
		List<Map> memoList = null;
		List priorContractInfo=null;
		
		Map creditPriorFactoryBuld=null;
		Map submitMachineMap=null;
		
		Map EuqipmentsRemarkMap=null;
		List EuqipmentsList=null;
		List BuyFactoriesList=null;
		List CellFactoriesList=null;
		Map CellFactoriesAllRemarkMap=null;
		
		List BankCheckBillList=null;
		
		List ContactCustomerList=null;
		Map ContactCustomerRemarkMap=null;
		try {
			
			ContactCustomerList=(List)DataAccessor.query(
						"creditPriorRecords.queryCCByCreditId",
						context.getContextMap(), DataAccessor.RS_TYPE.LIST);
			outputMap.put("ContactCustomerList", ContactCustomerList);
			
			ContactCustomerRemarkMap=(Map)DataAccessor.query(
					"creditPriorRecords.queryCCRemarkByCreditId",
					context.getContextMap(), DataAccessor.RS_TYPE.MAP);
			outputMap.put("ContactCustomerRemarkMap", ContactCustomerRemarkMap);
			
			memoList = (List) DataAccessor.query(
					"creditReportManage.selectMemo", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			outputMap.put("memoList", memoList);
			context.contextMap.put("data_type", "客户来源");
			creditMap = (Map) DataAccessor.query(
					"creditReportManage.selectCreditBaseInfo",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
			outputMap.put("creditMap", creditMap);
			
			context.contextMap.put("cust_id", creditMap.get("CUST_ID"));
			
			memoMap = (Map) DataAccessor.query(
					"creditReportManage.selectNewMemo", context.contextMap,
					DataAccessor.RS_TYPE.MAP);
			outputMap.put("memoMap", memoMap);
			creditPriorRecords = (List) DataAccessor.query(
					"creditPriorRecords.getCreditPriorRecords",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("creditPriorRecords", creditPriorRecords);

			/*priorContractInfo = (List) DataAccessor.query(原先过往合同记录有错误
				"creditPriorRecords.getContractInfoBycust_id",
				context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("priorContractInfo", priorContractInfo);*/
			//抓取新的过往合同记录 add by ShenQi
			//1首先通过cust_id 拿到过往合同的recpId
			List<Map<String,Object>> resultList=(List<Map<String,Object>>)DataAccessor.query("collectionManage.getRecpListByCustId",context.contextMap,DataAccessor.RS_TYPE.LIST);
			//拿实际剩余本金
			Map<String,Object> settleMap = null;
			List<Map<String,Object>> paylines = null;
			context.contextMap.put("zujin", "租金");				
			context.contextMap.put("zujinfaxi", "租金罚息");
			context.contextMap.put("sblgj", "设备留购价");
			
			for(int i=0;i<resultList.size();i++) {
				int totalPeriod=0;
				int payPeriod=0;
				double totalPrice=0;
				double payPrice=0;
				double restPrice=0;
				context.contextMap.put("RECP_ID",resultList.get(i).get("RECP_ID"));
				settleMap = (Map<String,Object>) DataAccessor.query("settleManage.selectSettlePrice", context.contextMap,DataAccessor.RS_TYPE.MAP);
				paylines = (List<Map<String,Object>>) DataAccessor.query("collectionManage.readPaylines",context.contextMap,DataAccessor.RS_TYPE.LIST);
				resultList.get(i).put("OWN_PRICE", settleMap.get("SUM_OWN_PRICE"));
				if(paylines!=null) {
					totalPeriod=paylines.size();
					for(int j=0;j<paylines.size();j++) {
						totalPrice=totalPrice+Double.valueOf(paylines.get(j).get("IRR_MONTH_PRICE").toString());
						if("PAY".equalsIgnoreCase((String)paylines.get(j).get("FLAG"))) {
							payPeriod=payPeriod+1;
							payPrice=payPrice+Double.valueOf(paylines.get(j).get("IRR_MONTH_PRICE").toString());
						}
					}
				}
				resultList.get(i).put("LEASE_PERIOD", totalPeriod);
				resultList.get(i).put("LEASE_PAY_PERIOD", payPeriod);
				restPrice=totalPrice-payPrice;
				resultList.get(i).put("REST_PRICE", restPrice);
				resultList.get(i).put("REMAINING_PRINCIPAL", LeaseUtil.getRemainingPrincipalByRecpId(String.valueOf(resultList.get(i).get("RECP_ID"))));
				
			}

			outputMap.put("priorContractInfo", resultList);
			
			creditPriorProject = (List) DataAccessor.query(
					"creditPriorRecords.getCreditPriorProjects",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("creditPriorProject", creditPriorProject);

			creditFinaceStatement = (List) DataAccessor.query(
					"creditPriorRecords.getCreditFinaceStatement",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			
			creditPriorFactoryBuld = (Map) DataAccessor.query(
					"creditPriorRecords.getcreditPriorFactoryBuld",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
			outputMap.put("creditPriorFactoryBuld", creditPriorFactoryBuld);
			
			submitMachineMap = (Map) DataAccessor.query(
					"creditPriorRecords.getcreditPriorSubmitMachine",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
			outputMap.put("submitMachineMap", submitMachineMap);
			
			EuqipmentsRemarkMap = (Map) DataAccessor.query(
					"creditPriorRecords.getcreditPriorALLEquipRemarks",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
			outputMap.put("EuqipmentsRemarkMap", EuqipmentsRemarkMap);
			
			context.contextMap.put("cust_id", creditMap.get("CUST_ID"));
			EuqipmentsList = (List) DataAccessor.query(
					"creditPriorRecords.getcreditPriorEquipments",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("EuqipmentsList", EuqipmentsList);
			
			BuyFactoriesList = (List) DataAccessor.query(
					"creditPriorRecords.getcreditPriorBuyFactorys",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("BuyFactoriesList", BuyFactoriesList);
			
			CellFactoriesAllRemarkMap= (Map) DataAccessor.query(
					"creditPriorRecords.getcreditPriorALLCellFacRemark",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
			outputMap.put("CellFactoriesAllRemarkMap", CellFactoriesAllRemarkMap);
			CellFactoriesList = (List) DataAccessor.query(
					"creditPriorRecords.getcreditPriorCellFactorys",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("CellFactoriesList", CellFactoriesList);
			
			BankCheckBillList = (List) DataAccessor.query(
					"creditPriorRecords.getcreditPriorBankCheckBillSix",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("BankCheckBillList", BankCheckBillList);
			
			Map v1 = null;
			Map v2 = null;
			Map v3 = null;
			Map v4 = null;
			if (creditFinaceStatement.size() >= 1) {
				v1 = (Map) creditFinaceStatement.get(0);
				v2 = (Map) creditFinaceStatement.get(1);
				v3 = (Map) creditFinaceStatement.get(2);
				v4 = (Map) creditFinaceStatement.get(3);
			}
			outputMap.put("v1", v1);
			outputMap.put("v2", v2);
			outputMap.put("v3", v3);
			outputMap.put("v4", v4);
			cFS_Remark = (Map) DataAccessor.query(
					"creditPriorRecords.getCFS_Remark", context.contextMap,
					DataAccessor.RS_TYPE.MAP);
			outputMap.put("cFS_Remark", cFS_Remark);
			
			outputMap.put("READ",true);
			Map<String,Object> industryMap=(Map<String,Object>)DataAccessor.query("creditReportManage.queryIndustryEnvironment",context.contextMap,RS_TYPE.MAP);
			outputMap.put("industryMap",industryMap);
			
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--过往记录错误!请联系管理员") ;
		}
		outputMap.put("examineFlag", context.contextMap.get("examineFlag"));
		outputMap.put("showFlag", context.contextMap.get("showFlag"));
		
		if(errList.isEmpty()){
//			if(DataUtil.intUtil(context.contextMap.get("commit_flag"))==1){
//				outputMap.put("commit_flag", context.contextMap.get("commit_flag"));
//				outputMap.put("isSalesDesk", context.contextMap.get("isSalesDesk"));
//				Output.jspOutput(outputMap, context, "/credit/creditFrameCommit.jsp");
//			}else{
//				Output.jspOutput(outputMap, context, "/credit/creditFrameShow.jsp");
//			}
			//根据报告类型
			int productionType = LeaseUtil.getProductionTypeByCreditId((String)context.contextMap.get("credit_id"));
			if(DataUtil.intUtil(context.contextMap.get("commit_flag"))==1){
				outputMap.put("commit_flag", context.contextMap.get("commit_flag"));
				outputMap.put("isSalesDesk", context.contextMap.get("isSalesDesk"));

				if(productionType==1){
					Output.jspOutput(outputMap, context, "/credit/equip/creditFrameCommit.jsp");
				}else if(productionType==2){
					Output.jspOutput(outputMap, context, "/credit/truck/creditFrameCommit.jsp");
				}else if(productionType==3){
					Output.jspOutput(outputMap, context, "/credit/car/creditFrameCommit.jsp");
				}
			}else{
				if(productionType==1){
					Output.jspOutput(outputMap, context, "/credit/equip/creditFrameShow.jsp");
				}else if(productionType==2){
					Output.jspOutput(outputMap, context, "/credit/truck/creditFrameShow.jsp");
				}else if(productionType==3){
					Output.jspOutput(outputMap, context, "/credit/car/creditFrameShow.jsp");
				}
			}
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}

	/**
	 * 添加或更新客户往来记录
	 * 
	 * @param context
	 */

	@SuppressWarnings({ "unchecked" })
	public void savecreditPriorRecords(Context context) {
		Map outputMap = new HashMap() ;
		List errList = context.errList ;
		sqlMapper = DataAccessor.getSession();
		try {
			// 开启事务
			sqlMapper.startTransaction();

			sqlMapper.delete("creditPriorRecords.deleteCreditPriorRecords",
					context.contextMap);
			String credit_id = (String) context.contextMap.get("credit_id");
			String[] LEASE_CODE = context.getRequest().getParameterValues(
					"LEASE_CODE");
			if (LEASE_CODE != null) {
				LEASE_CODE = HTMLUtil.getParameterValues(context.getRequest(),
						"LEASE_CODE", "");
				String[] EQUIPMENT_NAME = HTMLUtil.getParameterValues(context
						.getRequest(), "EQUIPMENT_NAME", "");
				String[] SALES_PRICE = HTMLUtil.getParameterValues(context
						.getRequest(), "SALES_PRICE", "0");
				String[] GATHERING_PRICE = HTMLUtil.getParameterValues(context
						.getRequest(), "GATHERING_PRICE", "0");
				String[] INFO_REMARK = HTMLUtil.getParameterValues(context
						.getRequest(), "INFO_REMARK", "");
				for (int i = 0; i < LEASE_CODE.length; i++) {
					if (!LEASE_CODE[i].equals("")) {
						Map map = new HashMap();
						map.put("LEASE_CODE", LEASE_CODE[i]);
						map.put("EQUIPMENT_NAME", EQUIPMENT_NAME[i]);
						map.put("SALES_PRICE", SALES_PRICE[i]);
						map.put("GATHERING_PRICE", GATHERING_PRICE[i]);
						map.put("INFO_REMARK", INFO_REMARK[i]);
						map.put("credit_id", credit_id);
						sqlMapper.insert(
								"creditPriorRecords.createCreditPriorRecords",
								map);
					}
				}
			}
				//承租人与供应商的往来记录与分析
			sqlMapper.delete("creditPriorRecords.deleteCreditPriorProjects",
					context.contextMap);
			String[] PROJECT_NAME0 = HTMLUtil.getParameterValues(context
 					.getRequest(), "PROJECT_NAME0", "");  
			String[] PROJECT_CONTENT0 = HTMLUtil.getParameterValues(context
					.getRequest(), "PROJECT_CONTENT0", "");  
		    for (int i = 0; i < PROJECT_CONTENT0.length; i++) {
				if(!PROJECT_NAME0.equals("")){
					Map map = new HashMap();
					map.put("PROJECT_NAME", PROJECT_NAME0[i]);
					map.put("PROJECT_CONTENT", PROJECT_CONTENT0[i]);
					map.put("STATE", 0);
					map.put("credit_id", credit_id);
					sqlMapper.insert(
							"creditPriorRecords.createCreditPriorProjects",map);
				}
						
			}
		    
		    //厂房信息保存
		    sqlMapper.delete("creditPriorRecords.deleteCreditPriorFactoryBulding",
					context.contextMap);
		    sqlMapper.insert("creditPriorRecords.createCreditPriorFactoryBulding",
		    		context.contextMap);
		    
			//经营效益分析保存
		   sqlMapper.delete("creditPriorRecords.deleteCreditPriorSubmitMachine",
					context.contextMap);
		    sqlMapper.insert("creditPriorRecords.createCreditPriorSubmitMachine",
		 		context.contextMap);
		      
		    //设备总备注的保存
		    
		    sqlMapper.delete("creditPriorRecords.deleteCreditPriorALLEquipRemarks",
					context.contextMap);
		    Object allremarkID = sqlMapper.insert("creditPriorRecords.createCreditPriorALLEquipRemarks",
		    		context.contextMap);
		    
		    //设备明细的保存
		    
		    String[] projectequipMentNames =HTMLUtil.getParameterValues(context.getRequest(), "sequipmentname", "");
		    String[] projectequipMentNums =HTMLUtil.getParameterValues(context.getRequest(), "sequipmentnum", "0");

		    String[] projectequipMentModels =HTMLUtil.getParameterValues(context.getRequest(), "sequipmentmaker", "");
		    String[] projectequipMentBuyMons =HTMLUtil.getParameterValues(context.getRequest(), "sequipmentmoney", "0");

		    String[] projectequipMentDates =HTMLUtil.getParameterValues(context.getRequest(), "sequipmentdate", "");

		    String[] projectequipMentRemarks =HTMLUtil.getParameterValues(context.getRequest(), "sequipmentramark", "");
		    sqlMapper.delete("creditPriorRecords.deleteCreditPriorEquipments",
					context.contextMap);
		   if(projectequipMentNums!=null){
		    
		    for (int i = 0; i < projectequipMentNames.length; i++) {
		    	
				if(!projectequipMentNames.equals("")){
					Map map = new HashMap();

					map.put("ALLREMARK_ID", allremarkID);						
					map.put("EQUPNAME", projectequipMentNames[i]);					
					map.put("EQUPNUM", (int)Double.parseDouble(projectequipMentNums[i]));
					map.put("EQUPMODEL", projectequipMentModels[i]);
					map.put("EQUPBUYDATE", projectequipMentDates[i]);
					map.put("EQUPBUYMON", Double.parseDouble(projectequipMentBuyMons[i]));
					map.put("EQUPREMARK", projectequipMentRemarks[i]);
					map.put("s_employeeId", context.contextMap.get("s_employeeId"));
					map.put("credit_id", context.contextMap.get("credit_id"));
					map.put("ALLREMARK_ID", context.contextMap.get("cust_id"));
					sqlMapper.insert(
							"creditPriorRecords.createCreditPriorEquipments",map);
				}
						
			}
		   }
		   
		  //**********************************************************************************************
		  //往来客户修改为进货厂商照会记录和销货厂商照会记录 add by ShenQi 2012-7-12
		  
		   //主要往来客户总备注的保存 remove part
		    sqlMapper.delete("creditPriorRecords.deleteCreditPriorCCALLRemarks",
					context.contextMap);
		    Object allccremarkID = sqlMapper.insert("creditPriorRecords.createCreditPriorCCALLRemarks",
		    		context.contextMap);

		 //主要往来客户保存
		    String[] cc_names =HTMLUtil.getParameterValues(context.getRequest(),"cc_name","");
		    String[] cc_addresss =HTMLUtil.getParameterValues(context.getRequest(),"cc_address","");
		    String[] cc_types =HTMLUtil.getParameterValues(context.getRequest(),"cc_type","");
		    String[] cc_moneys =HTMLUtil.getParameterValues(context.getRequest(),"cc_money","0");
		    String[] cc_percents = HTMLUtil.getParameterValues(context.getRequest(),"cc_percent","0");
		    String[] cc_conditions = HTMLUtil.getParameterValues(context.getRequest(),"cc_condition","");
		    String[] CC_LINK_MAN=HTMLUtil.getParameterValues(context.getRequest(),"cc_link_man","");
		    String[] LINK_MAN_PHONE=HTMLUtil.getParameterValues(context.getRequest(),"link_man_phone","");
		    sqlMapper.delete("creditPriorRecords.deleteCreditPriorCC",
					context.contextMap);
		   if(cc_names!=null){
		    
		    for (int i = 0; i < cc_names.length; i++) {
		    	
				if(!cc_names.equals("")){
					Map map = new HashMap();
					
					map.put("ALLREMARK_ID", allccremarkID);
					map.put("CC_NAME",  cc_names[i]);
					map.put("CC_ADDRESS", cc_addresss[i]);					
					map.put("CC_TYPE", cc_types[i]);
					map.put("CC_MONEY", Float.parseFloat(cc_moneys[i]));
					map.put("CC_PERCENT", Float.parseFloat(cc_percents[i]));
					map.put("CC_CONDITION",cc_conditions[i]);
					map.put("CC_LINK_MAN",CC_LINK_MAN[i]);
					map.put("LINK_MAN_PHONE",LINK_MAN_PHONE[i]);
					map.put("s_employeeId", context.contextMap.get("s_employeeId"));
					map.put("credit_id", context.contextMap.get("credit_id"));
					sqlMapper.insert("creditPriorRecords.createCreditPriorCC",map);
				}
						
			}
		   }
		 /*//进货厂商照会记录部分
		 //先通过credit_id删除照会记录
		 sqlMapper.delete("creditPriorRecords.deleteStockCompany",context.contextMap);
		 
		 String[] STOCK_BASE_DATE=HTMLUtil.getParameterValues(context.getRequest(),"STOCK_BASE_DATE","1900-1-1");//基准日
		 String[] STOCK_COMPANY=HTMLUtil.getParameterValues(context.getRequest(),"STOCK_COMPANY","");//公司名称
		 String[] STOCK_PHONE=HTMLUtil.getParameterValues(context.getRequest(),"STOCK_PHONE","");//联络电话
		 String[] STOCK_LINK_MAN=HTMLUtil.getParameterValues(context.getRequest(),"STOCK_LINK_MAN","");
		 String[] STOCK_MEET_DATE=HTMLUtil.getParameterValues(context.getRequest(),"STOCK_BASE_DATE","1900-1-1");
		 String[] STOCK_PRODUCTION=HTMLUtil.getParameterValues(context.getRequest(),"STOCK_PRODUCTION","");
		 String[] STOCK_PERIOD=HTMLUtil.getParameterValues(context.getRequest(),"STOCK_PERIOD","");
		 String[] STOCK_MONEY=HTMLUtil.getParameterValues(context.getRequest(),"STOCK_MONEY","0");
		 String[] STOCK_CONDITION=HTMLUtil.getParameterValues(context.getRequest(),"STOCK_CONDITION","");
		 String[] STOCK_CONTENT=HTMLUtil.getParameterValues(context.getRequest(),"STOCK_CONTENT","");
		 
		 Map<String,Object> paramMap=new HashMap<String,Object>();
		 
		 for(int i=0;STOCK_BASE_DATE!=null&&i<STOCK_BASE_DATE.length;i++) {
			 
			 paramMap.put("CREDIT_ID",context.contextMap.get("credit_id"));
			 paramMap.put("CREDTE_BY",context.contextMap.get("s_employeeId"));
			 
			 paramMap.put("STOCK_BASE_DATE",STOCK_BASE_DATE[i]);
			 paramMap.put("STOCK_COMPANY",STOCK_COMPANY[i]);
			 paramMap.put("STOCK_PHONE",STOCK_PHONE[i]);
			 paramMap.put("STOCK_LINK_MAN",STOCK_LINK_MAN[i]);
			 paramMap.put("STOCK_MEET_DATE",STOCK_MEET_DATE[i]);
			 paramMap.put("STOCK_PRODUCTION",STOCK_PRODUCTION[i]);
			 paramMap.put("STOCK_PERIOD",STOCK_PERIOD[i]);
			 paramMap.put("STOCK_MONEY",STOCK_MONEY[i]);
			 paramMap.put("STOCK_CONDITION",STOCK_CONDITION[i]);
			 paramMap.put("STOCK_CONTENT",STOCK_CONTENT[i]);
			 
			 sqlMapper.insert("creditPriorRecords.insertStockCompany",paramMap);
		 }*/
		 //TODO
		 //出货厂商照会记录部分
		 //**********************************************************************************************
		   
		   
		   
		 //进货厂商的信息保存
		    String[] BUYFACTORYNAMEs =HTMLUtil.getParameterValues(context.getRequest(), "BUYFACTORYNAME", "");
		   
		    String[] BUYTHINGKINDs =HTMLUtil.getParameterValues(context.getRequest(), "BUYTHINGKIND", "");

		    String[] BUYMONTHINGOPRICEs =HTMLUtil.getParameterValues(context.getRequest(), "BUYMONTHINGOPRICE", "0");

		    String[] BUYPERCENTGRAVEs =HTMLUtil.getParameterValues(context.getRequest(), "BUYPERCENTGRAVE", "0");

		    String[] BUYPAYCONDITIONs =HTMLUtil.getParameterValues(context.getRequest(), "BUYPAYCONDITIONS", "");
		    
		    String[] BUY_LINK_MAN=HTMLUtil.getParameterValues(context.getRequest(), "BUY_LINK_MAN", "");
		    
		    String[] BUY_LINK_MAN_PHONE=HTMLUtil.getParameterValues(context.getRequest(), "BUY_LINK_MAN_PHONE", "");
		    sqlMapper.delete("creditPriorRecords.deleteCreditPriorBuyFactorys",
					context.contextMap);
		    if(BUYFACTORYNAMEs!=null){
		    	
		    	for (int i = 0; i < BUYFACTORYNAMEs.length; i++) {
			    	
					if(!BUYFACTORYNAMEs.equals("")){
						
						Map mapbuyfac = new HashMap();
						if(i==0){
							mapbuyfac.put("ALLBUYREMARK", context.contextMap.get("ALLBUYREMARK"));
						}else{
							mapbuyfac.put("ALLBUYREMARK", "");						
						}
						mapbuyfac.put("BUYFACTORYNAME", BUYFACTORYNAMEs[i]);					
						mapbuyfac.put("BUYTHINGKIND", BUYTHINGKINDs[i]);
						mapbuyfac.put("BUYMONTHINGOPRICE", Double.parseDouble(BUYMONTHINGOPRICEs[i]));
						mapbuyfac.put("BUYPERCENTGRAVE", Double.parseDouble(BUYPERCENTGRAVEs[i]));
						mapbuyfac.put("BUYPAYCONDITIONS", BUYPAYCONDITIONs[i]);
						mapbuyfac.put("BUY_LINK_MAN", BUY_LINK_MAN[i]);
						mapbuyfac.put("BUY_LINK_MAN_PHONE", BUY_LINK_MAN_PHONE[i]);
						mapbuyfac.put("s_employeeId", context.contextMap.get("s_employeeId"));
						mapbuyfac.put("credit_id", context.contextMap.get("credit_id"));
						
						sqlMapper.insert("creditPriorRecords.createCreditPriorBuyFactorys",
								mapbuyfac);
					}
		    	}
		    }
		  //销货厂商的备注保存
		    sqlMapper.delete("creditPriorRecords.deleteCreditPriorALLCellFacRemark",
					context.contextMap);
		    @SuppressWarnings("unused")
			Object ALLCELLREMARK_ID = sqlMapper.insert("creditPriorRecords.createCreditPriorALLCellFacRemark",
		    		context.contextMap); 
//		  //销货厂商的信息保存
//		    String[] CELLFACTORYNAMEs =HTMLUtil.getParameterValues(context.getRequest(), "CELLFACTORYNAME", "");
//		    
//		    String[] CELLFACTORYADDRs =HTMLUtil.getParameterValues(context.getRequest(), "CELLFACTORYADDR", "");
//		   
//		    String[] CELLTHINGKINDs =HTMLUtil.getParameterValues(context.getRequest(), "CELLTHINGKIND", "");
//
//		    String[] CELLMONTHINGOPRICEs =HTMLUtil.getParameterValues(context.getRequest(), "CELLMONTHINGOPRICE", "0");
//
//		    String[] CELLPERCENTGRAVEs =HTMLUtil.getParameterValues(context.getRequest(), "CELLPERCENTGRAVE", "0");
//
//		    String[] CELLPAYCONDITIONSs =HTMLUtil.getParameterValues(context.getRequest(), "CELLPAYCONDITIONS", ""); 
//		    sqlMapper.delete("creditPriorRecords.deleteCreditPriorCellFactorys",
//					context.contextMap);
//		    if(CELLFACTORYNAMEs!=null){
//		    	
//		    	for (int i = 0; i < CELLFACTORYNAMEs.length; i++) {
//			    	
//					if(!BUYFACTORYNAMEs.equals("")){
//						
//						Map mapcellfac = new HashMap();
//						mapcellfac.put("ALLCELLREMARK_ID", ALLCELLREMARK_ID);
//						mapcellfac.put("CELLFACTORYNAME", CELLFACTORYNAMEs[i]);					
//						mapcellfac.put("CELLFACTORYADDR", CELLFACTORYADDRs[i]);					
//						mapcellfac.put("CELLTHINGKIND", CELLTHINGKINDs[i]);
//						mapcellfac.put("CELLMONTHINGOPRICE", Double.parseDouble(CELLMONTHINGOPRICEs[i]));
//						mapcellfac.put("CELLPERCENTGRAVE", Double.parseDouble(CELLPERCENTGRAVEs[i]));
//						mapcellfac.put("CELLPAYCONDITIONS", CELLPAYCONDITIONSs[i]);
//						mapcellfac.put("s_employeeId", context.contextMap.get("s_employeeId"));
//						mapcellfac.put("credit_id", context.contextMap.get("credit_id"));
//						
//						sqlMapper.insert("creditPriorRecords.createCreditPriorCellFactorys",
//								mapcellfac);
//					}
//		    	}
//		    }
//		    
		    
		  //duizhangdan的信息保存
		    //说明
		    String[] REMARKs =HTMLUtil.getParameterValues(context.getRequest(), "BANK_REMARK", "");
		    
		    String[] BANKCUSTNAMEs =HTMLUtil.getParameterValues(context.getRequest(), "BANKCUSTNAME", ""); 
		    String[] BANKBRANCHs =HTMLUtil.getParameterValues(context.getRequest(), "BANKBRANCH", "");
		    String[] BANKCUSTCODEs =HTMLUtil.getParameterValues(context.getRequest(), "BANKCUSTCODE", "");
		    String[] BANKNAMEs =HTMLUtil.getParameterValues(context.getRequest(), "BANKNAME", "");
		    
		    String[] CHECKMONTHONEs =HTMLUtil.getParameterValues(context.getRequest(), "CHECKMONTHONE", "");
		    String[] CHECKMONTHTWOs =HTMLUtil.getParameterValues(context.getRequest(), "CHECKMONTHTWO", "");
		    String[] CHECKMONTHTHREEs =HTMLUtil.getParameterValues(context.getRequest(), "CHECKMONTHTHREE", "");
		    String[] CHECKMONTHFOURs =HTMLUtil.getParameterValues(context.getRequest(), "CHECKMONTHFOUR", "");
		    String[] CHECKMONTHFIVEs =HTMLUtil.getParameterValues(context.getRequest(), "CHECKMONTHFIVE", "");
		    String[] CHECKMONTHSIXs =HTMLUtil.getParameterValues(context.getRequest(), "CHECKMONTHSIX", "");
		    
		    String[] LASTSUMONEs =  HTMLUtil.getParameterValues(context.getRequest(), "LASTSUMONE", "0");
		    String[] LASTSUMTWOs = HTMLUtil.getParameterValues(context.getRequest(), "LASTSUMTWO", "0");
		    String[] LASTSUMTHREEs = HTMLUtil.getParameterValues(context.getRequest(), "LASTSUMTHREE", "0");
		    String[] LASTSUMFOURs = HTMLUtil.getParameterValues(context.getRequest(), "LASTSUMFOUR", "0");
		    String[] LASTSUMFIVEs = HTMLUtil.getParameterValues(context.getRequest(), "LASTSUMFIVE", "0");
		    String[] LASTSUMSIXs = HTMLUtil.getParameterValues(context.getRequest(), "LASTSUMSIX", "0");
		   
		    String[] MONTHINCOMEONEs =HTMLUtil.getParameterValues(context.getRequest(), "MONTHINCOMEONE", "0");
		    String[] MONTHINCOMETWOs =HTMLUtil.getParameterValues(context.getRequest(), "MONTHINCOMETWO", "0");
		    String[] MONTHINCOMETHREEs =HTMLUtil.getParameterValues(context.getRequest(), "MONTHINCOMETHREE", "0");
		    String[] MONTHINCOMEFOURs =HTMLUtil.getParameterValues(context.getRequest(), "MONTHINCOMEFOUR", "0");
		    String[] MONTHINCOMEFIVEs =HTMLUtil.getParameterValues(context.getRequest(), "MONTHINCOMEFIVE", "0");
		    String[] MONTHINCOMESIXs =HTMLUtil.getParameterValues(context.getRequest(), "MONTHINCOMESIX", "0");

		    String[] MONTHCOSTONEs =HTMLUtil.getParameterValues(context.getRequest(), "CHECKMONTHCOSTONE", "0");
		    String[] MONTHCOSTTWOs =HTMLUtil.getParameterValues(context.getRequest(), "CHECKMONTHCOSTTWO", "0"); 
		    String[] MONTHCOSTTHREEs =HTMLUtil.getParameterValues(context.getRequest(), "CHECKMONTHCOSTTHREE", "0");
		    String[] MONTHCOSTFOURs =HTMLUtil.getParameterValues(context.getRequest(), "CHECKMONTHCOSTFOUR", "0");
		    String[] MONTHCOSTFIVEs =HTMLUtil.getParameterValues(context.getRequest(), "CHECKMONTHCOSTFIVE", "0");
		    String[] MONTHCOSTSIXs =HTMLUtil.getParameterValues(context.getRequest(), "CHECKMONTHCOSTSIX", "0");

		    String[] THISSUMONEs =HTMLUtil.getParameterValues(context.getRequest(), "THISSUMONE", "0");
		    String[] THISSUMTWOs =HTMLUtil.getParameterValues(context.getRequest(), "THISSUMTWO", "0");
		    String[] THISSUMTHREEs =HTMLUtil.getParameterValues(context.getRequest(), "THISSUMTHREE", "0");
		    String[] THISSUMFOURs =HTMLUtil.getParameterValues(context.getRequest(), "THISSUMFOUR", "0"); 
		    String[] THISSUMFIVEs =HTMLUtil.getParameterValues(context.getRequest(), "THISSUMFIVE", "0");
		    String[] THISSUMSIXs =HTMLUtil.getParameterValues(context.getRequest(), "THISSUMSIX", "0");

		    String[] MONEYFLOWINONEs =HTMLUtil.getParameterValues(context.getRequest(), "MONEYFLOWINONE", "0");
		    String[] MONEYFLOWINTWOs =HTMLUtil.getParameterValues(context.getRequest(), "MONEYFLOWINTWO", "0");
		    String[] MONEYFLOWINTHREEs =HTMLUtil.getParameterValues(context.getRequest(), "MONEYFLOWINTHREE", "0");
		    String[] MONEYFLOWINFOURs =HTMLUtil.getParameterValues(context.getRequest(), "MONEYFLOWINFOUR", "0");
		    String[] MONEYFLOWINFIVEs =HTMLUtil.getParameterValues(context.getRequest(), "MONEYFLOWINFIVE", "0");
		    String[] MONEYFLOWINSIXs =HTMLUtil.getParameterValues(context.getRequest(), "MONEYFLOWINSIX", "0");
		    sqlMapper.delete("creditPriorRecords.deleteCreditPriorBankCheckBillSix",
					context.contextMap);
		    if(BANKCUSTNAMEs!=null){
		    	
		    	for (int i = 0; i < BANKCUSTNAMEs.length; i++) {
			    	
					if(!BANKCUSTNAMEs.equals("")){
						Map mapbankcheckbill = new HashMap();
						
						mapbankcheckbill.put("REMARK", REMARKs[i]);
						
						mapbankcheckbill.put("BANKCUSTNAME", BANKCUSTNAMEs[i]);
						mapbankcheckbill.put("BANKBRANCH", BANKBRANCHs[i]);
						mapbankcheckbill.put("BANKCUSTCODE", BANKCUSTCODEs[i]);
						mapbankcheckbill.put("BANKNAME", BANKNAMEs[i]);
			
					if(CHECKMONTHONEs[i].length()>6||CHECKMONTHONEs[i].length()==0){
						mapbankcheckbill.put("CHECKMONTHONE", CHECKMONTHONEs[i]);
				
					}else{
						mapbankcheckbill.put("CHECKMONTHONE", CHECKMONTHONEs[i]+"01");
					}
					if(CHECKMONTHTWOs[i].length()>6||CHECKMONTHTWOs[i].length()==0){
						mapbankcheckbill.put("CHECKMONTHTWO", CHECKMONTHTWOs[i]);
						
					}else{
						mapbankcheckbill.put("CHECKMONTHTWO", CHECKMONTHTWOs[i]+"01");
					}
					if(CHECKMONTHTHREEs[i].length()>6||CHECKMONTHTHREEs[i].length()==0){
						mapbankcheckbill.put("CHECKMONTHTHREE", CHECKMONTHTHREEs[i]);
						
					}else{
						mapbankcheckbill.put("CHECKMONTHTHREE", CHECKMONTHTHREEs[i]+"01");
					}
					if(CHECKMONTHFOURs[i].length()>6||CHECKMONTHFOURs[i].length()==0){
						mapbankcheckbill.put("CHECKMONTHFOUR", CHECKMONTHFOURs[i]);
						
					}else{
						mapbankcheckbill.put("CHECKMONTHFOUR", CHECKMONTHFOURs[i]+"01");
					}
					if(CHECKMONTHFIVEs[i].length()>6||CHECKMONTHFIVEs[i].length()==0){
						mapbankcheckbill.put("CHECKMONTHFIVE", CHECKMONTHFIVEs[i]);
						
					}else{
						mapbankcheckbill.put("CHECKMONTHFIVE", CHECKMONTHFIVEs[i]+"01");
					}
					if(CHECKMONTHSIXs[i].length()>6||CHECKMONTHSIXs[i].length()==0){
						mapbankcheckbill.put("CHECKMONTHSIX", CHECKMONTHSIXs[i]);
						
					}else{
						mapbankcheckbill.put("CHECKMONTHSIX", CHECKMONTHSIXs[i]+"01");
					}
						
						mapbankcheckbill.put("LASTSUMONE", Double.parseDouble(LASTSUMONEs[i]));
						mapbankcheckbill.put("LASTSUMTWO", Double.parseDouble(LASTSUMTWOs[i]));
						mapbankcheckbill.put("LASTSUMTHREE", Double.parseDouble(LASTSUMTHREEs[i]));
						mapbankcheckbill.put("LASTSUMFOUR", Double.parseDouble(LASTSUMFOURs[i]));
						mapbankcheckbill.put("LASTSUMFIVE", Double.parseDouble(LASTSUMFIVEs[i]));
						mapbankcheckbill.put("LASTSUMSIX", Double.parseDouble(LASTSUMSIXs[i]));
						
						mapbankcheckbill.put("MONTHINCOMEONE", Double.parseDouble(MONTHINCOMEONEs[i]));
						mapbankcheckbill.put("MONTHINCOMETWO", Double.parseDouble(MONTHINCOMETWOs[i]));
						mapbankcheckbill.put("MONTHINCOMETHREE", Double.parseDouble(MONTHINCOMETHREEs[i]));
						mapbankcheckbill.put("MONTHINCOMEFOUR", Double.parseDouble(MONTHINCOMEFOURs[i]));
						mapbankcheckbill.put("MONTHINCOMEFIVE", Double.parseDouble(MONTHINCOMEFIVEs[i]));
						mapbankcheckbill.put("MONTHINCOMESIX", Double.parseDouble(MONTHINCOMESIXs[i]));
						
						mapbankcheckbill.put("MONTHCOSTONE", Double.parseDouble(MONTHCOSTONEs[i]));
						mapbankcheckbill.put("MONTHCOSTTWO", Double.parseDouble(MONTHCOSTTWOs[i]));
						mapbankcheckbill.put("MONTHCOSTTHREE", Double.parseDouble(MONTHCOSTTHREEs[i]));
						mapbankcheckbill.put("MONTHCOSTFOUR", Double.parseDouble(MONTHCOSTFOURs[i]));
						mapbankcheckbill.put("MONTHCOSTFIVE", Double.parseDouble(MONTHCOSTFIVEs[i]));
						mapbankcheckbill.put("MONTHCOSTSIX", Double.parseDouble(MONTHCOSTSIXs[i]));
						
						mapbankcheckbill.put("THISSUMONE", Double.parseDouble(THISSUMONEs[i]));
						mapbankcheckbill.put("THISSUMTWO", Double.parseDouble(THISSUMTWOs[i]));
						mapbankcheckbill.put("THISSUMTHREE", Double.parseDouble(THISSUMTHREEs[i]));
						mapbankcheckbill.put("THISSUMFOUR", Double.parseDouble(THISSUMFOURs[i]));
						mapbankcheckbill.put("THISSUMFIVE", Double.parseDouble(THISSUMFIVEs[i]));
						mapbankcheckbill.put("THISSUMSIX", Double.parseDouble(THISSUMSIXs[i]));
						
						mapbankcheckbill.put("MONEYFLOWINONE", Double.parseDouble(MONEYFLOWINONEs[i]));
						mapbankcheckbill.put("MONEYFLOWINTWO", Double.parseDouble(MONEYFLOWINTWOs[i]));
						mapbankcheckbill.put("MONEYFLOWINTHREE", Double.parseDouble(MONEYFLOWINTHREEs[i]));
						mapbankcheckbill.put("MONEYFLOWINFOUR", Double.parseDouble(MONEYFLOWINFOURs[i]));
						mapbankcheckbill.put("MONEYFLOWINFIVE", Double.parseDouble(MONEYFLOWINFIVEs[i]));
						mapbankcheckbill.put("MONEYFLOWINSIX", Double.parseDouble(MONEYFLOWINSIXs[i]));
						
						mapbankcheckbill.put("s_employeeId", context.contextMap.get("s_employeeId"));
						mapbankcheckbill.put("credit_id", context.contextMap.get("credit_id"));
						
						sqlMapper.insert("creditPriorRecords.createCreditPriorBankCheckBillSix",
								mapbankcheckbill);
					}
		    	}
		    }
		    
			//承租人与金融机构的往来记录与分析
			Map map9 = new HashMap();
			map9.put("PROJECT_NAME", context.contextMap.get("PROJECT_NAME9"));
			map9.put("PROJECT_CONTENT", context.contextMap.get("PROJECT_CONTENT9"));
			
			map9.put("STATE", 9);
			map9.put("credit_id", credit_id);
			
			sqlMapper.insert("creditPriorRecords.createCreditPriorProjects",
				map9);
			
			
				//生产经营情况说明   这块插入动作移动到租赁方案中 add by ShenQi 2012-8-16
/*			Map map = new HashMap();
			map.put("PROJECT_NAME", context.contextMap.get("PROJECT_NAME1"));
			map.put("PROJECT_CONTENT", context.contextMap.get("PROJECT_CONTENT1"));
			
			map.put("STATE", 1);
			map.put("credit_id", credit_id);
			
			sqlMapper.insert("creditPriorRecords.createCreditPriorProjects",
					map);*/
			
			//本次租赁还款能力说明
			Map map11 = new HashMap();
			map11.put("PROJECT_NAME", context.contextMap.get("PROJECT_NAME11"));
			map11.put("PROJECT_CONTENT", context.contextMap.get("PROJECT_CONTENT11"));
			
			map11.put("STATE", 11);
			map11.put("credit_id", credit_id);
			
			sqlMapper.insert("creditPriorRecords.createCreditPriorProjects",
				map11);
			
			
			//本次租赁担保增信说明
			Map map12 = new HashMap();
			map12.put("PROJECT_NAME", context.contextMap.get("PROJECT_NAME12"));
			map12.put("PROJECT_CONTENT", context.contextMap.get("PROJECT_CONTENT12"));
			
			map12.put("STATE", 12);
			map12.put("credit_id", credit_id);
			
			sqlMapper.insert("creditPriorRecords.createCreditPriorProjects",
				map12);
			

			
			//财务
//			sqlMapper.delete("creditPriorRecords.deleteCreditFinaceStatement",
//					map);
//			sqlMapper.delete("creditPriorRecords.deleteCFS_Remark", map);
//			String[] PROJECT_NAME = HTMLUtil.getParameterValues(context
//					.getRequest(), "PROJECT_NAME", "");
//			String[] MONEY_FUNDS = HTMLUtil.getParameterValues(context
//					.getRequest(), "MONEY_FUNDS", "");
//			String[] ACCOUNTS_RECEIVABLE = HTMLUtil.getParameterValues(context
//					.getRequest(), "ACCOUNTS_RECEIVABLE", "");
//			String[] STOCK = HTMLUtil.getParameterValues(context.getRequest(),
//					"STOCK", "");
//			String[] CAPITAL_ASSERTS = HTMLUtil.getParameterValues(context
//					.getRequest(), "CAPITAL_ASSERTS", "");
//			String[] TOTAL_ASSERTS = HTMLUtil.getParameterValues(context
//					.getRequest(), "TOTAL_ASSERTS", "");
//			String[] SHORTTIME_LOAN = HTMLUtil.getParameterValues(context
//					.getRequest(), "SHORTTIME_LOAN", "");
//			String[] ACCOUNTS_PAYABLE = HTMLUtil.getParameterValues(context
//					.getRequest(), "ACCOUNTS_PAYABLE", "");
//			String[] TOTAL_OWES = HTMLUtil.getParameterValues(context
//					.getRequest(), "TOTAL_OWES", "");
//			String[] CONTRIBUTED_CAPITAL = HTMLUtil.getParameterValues(context
//					.getRequest(), "CONTRIBUTED_CAPITAL", "");
//			String[] CAPITAL_RESERVE = HTMLUtil.getParameterValues(context
//					.getRequest(), "CAPITAL_RESERVE", "");
//			String[] UNDISTRIBUTED_PROFIT = HTMLUtil.getParameterValues(context
//					.getRequest(), "UNDISTRIBUTED_PROFIT", "");
//			String[] SALES_REVENUE = HTMLUtil.getParameterValues(context
//					.getRequest(), "SALES_REVENUE", "");
//			String[] COST_OF_MARKETING = HTMLUtil.getParameterValues(context
//					.getRequest(), "COST_OF_MARKETING", "");
//			String[] PERIOD_EXPENSE = HTMLUtil.getParameterValues(context
//					.getRequest(), "PERIOD_EXPENSE", "");
//			String[] TOTAL_PROFIT = HTMLUtil.getParameterValues(context
//					.getRequest(), "TOTAL_PROFIT", "");
//			String[] DEBTR = HTMLUtil.getParameterValues(context.getRequest(),
//					"DEBTR", "");
//			String[] PROFIT_MARGIN = HTMLUtil.getParameterValues(context
//					.getRequest(), "PROFIT_MARGIN", "");
//			String[] TTM = HTMLUtil.getParameterValues(context.getRequest(),
//					"TTM", "");
//			String[] SALES_GROWTH = HTMLUtil.getParameterValues(context
//					.getRequest(), "SALES_GROWTH", "");
//			String[] NAGR = HTMLUtil.getParameterValues(context.getRequest(),
//					"NAGR", "");
//			for (int i = 0; i < PROJECT_NAME.length; i++) {
//				Map tempMap = new HashMap();
//				tempMap.put("PROJECT_NAME", PROJECT_NAME[i]);
//				tempMap.put("MONEY_FUNDS", MONEY_FUNDS[i]);
//				tempMap.put("ACCOUNTS_RECEIVABLE", ACCOUNTS_RECEIVABLE[i]);
//				tempMap.put("STOCK", STOCK[i]);
//				tempMap.put("CAPITAL_ASSERTS", CAPITAL_ASSERTS[i]);
//				tempMap.put("TOTAL_ASSERTS", TOTAL_ASSERTS[i]);
//				tempMap.put("SHORTTIME_LOAN", SHORTTIME_LOAN[i]);
//				tempMap.put("ACCOUNTS_PAYABLE", ACCOUNTS_PAYABLE[i]);
//				tempMap.put("TOTAL_OWES", TOTAL_OWES[i]);
//				tempMap.put("CONTRIBUTED_CAPITAL", CONTRIBUTED_CAPITAL[i]);
//				tempMap.put("CAPITAL_RESERVE", CAPITAL_RESERVE[i]);
//				tempMap.put("UNDISTRIBUTED_PROFIT", UNDISTRIBUTED_PROFIT[i]);
//				tempMap.put("SALES_REVENUE", SALES_REVENUE[i]);
//				tempMap.put("COST_OF_MARKETING", COST_OF_MARKETING[i]);
//				tempMap.put("PERIOD_EXPENSE", PERIOD_EXPENSE[i]);
//				tempMap.put("TOTAL_PROFIT", TOTAL_PROFIT[i]);
//				tempMap.put("DEBTR", DEBTR[i]);
//				tempMap.put("PROFIT_MARGIN", PROFIT_MARGIN[i]);
//				tempMap.put("TTM", TTM[i]);
//				tempMap.put("SALES_GROWTH", SALES_GROWTH[i]);
//				tempMap.put("NAGR", NAGR[i]);
//				tempMap.put("credit_id", credit_id);
//				tempMap.put("s_employeeId", context.contextMap
//						.get("s_employeeId"));
//				if (i != 4) {
//					sqlMapper.insert(
//							"creditPriorRecords.createCreditFinaneStatement",
//							tempMap);
//				} else {
//					sqlMapper.insert("creditPriorRecords.createCFS_Remark",
//							tempMap);
//				}
//			}

			String PROJECT_NAME2[] = HTMLUtil.getParameterValues(context
					.getRequest(), "PROJECT_NAME2", "");
			
			String PROJECT_CONTENT2[] = HTMLUtil.getParameterValues(context
					.getRequest(), "PROJECT_CONTENT2", "");
			if (PROJECT_NAME2 != null) {
				
				for(int i = 0; i<PROJECT_NAME2.length;i++){
					
						Map map2 = new HashMap();
						map2.put("PROJECT_NAME", PROJECT_NAME2[i]);
						map2.put("PROJECT_CONTENT", PROJECT_CONTENT2[i]);
						map2.put("STATE", 2);
						map2.put("credit_id", credit_id);
						sqlMapper.insert(
								"creditPriorRecords.createCreditPriorProjects",
								map2);
					}

				
			}
			Map map3 = new HashMap();
			map3.put("PROJECT_NAME", context.contextMap.get("PROJECT_NAME3"));
			map3.put("PROJECT_CONTENT", context.contextMap
					.get("PROJECT_CONTENT3"));
			map3.put("STATE", 3);
			map3.put("credit_id", credit_id);
			sqlMapper.insert("creditPriorRecords.createCreditPriorProjects",
					map3);

			// 提交事务
			sqlMapper.commitTransaction();
		} catch (SQLException e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--添加/更新客户往来记录错误!请联系管理员") ;
		} finally {
			try {
				// 关闭事务
				sqlMapper.endTransaction();
				if(errList.isEmpty()){
					Output.jspSendRedirect(context,
						"defaultDispatcher?__action=creditVoucher.getCreditVouchers&showFlag=3&credit_id="
											+ context.contextMap.get("credit_id"));
				} else {
					outputMap.put("errList",errList) ;
					Output.jspOutput(outputMap, context, "/error.jsp") ;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void saveCreditReport(Context context){
		List errList = context.errList ;
		Map outputMap = new HashMap() ;
		sqlMapper=DataAccessor.getSession();
		try {
			sqlMapper.startTransaction();
			sqlMapper.delete("creditPriorRecords.deleteCorpReport", context.contextMap);
			String[] project_item= HTMLUtil.getParameterValues(context.getRequest(), "project_item", "0");
			String [] ca_cash_price= HTMLUtil.getParameterValues(context.getRequest(), "ca_cash_price", "0");
			String [] ca_short_Invest= HTMLUtil.getParameterValues(context.getRequest(), "ca_short_Invest", "0");
			String [] ca_bills_should= HTMLUtil.getParameterValues(context.getRequest(), "ca_bills_should", "0");
			String [] ca_Funds_should= HTMLUtil.getParameterValues(context.getRequest(), "ca_Funds_should", "0");
			String [] ca_Goods_stock= HTMLUtil.getParameterValues(context.getRequest(), "ca_Goods_stock", "0");
			String [] ca_other= HTMLUtil.getParameterValues(context.getRequest(), "ca_other", "0");
			String [] fa_land= HTMLUtil.getParameterValues(context.getRequest(), "fa_land", "0");
			String [] fa_buildings= HTMLUtil.getParameterValues(context.getRequest(), "fa_buildings", "0");
			String [] fa_equipments= HTMLUtil.getParameterValues(context.getRequest(), "fa_equipments", "0");
			String [] fa_rent_Assets= HTMLUtil.getParameterValues(context.getRequest(), "fa_rent_Assets", "0");
			String [] fa_transports= HTMLUtil.getParameterValues(context.getRequest(), "fa_transports", "0");
			String [] fa_other= HTMLUtil.getParameterValues(context.getRequest(), "fa_other", "0");
			String [] fa_Depreciations= HTMLUtil.getParameterValues(context.getRequest(), "fa_Depreciations", "0");
			String [] fa_Incompleted_projects= HTMLUtil.getParameterValues(context.getRequest(), "fa_Incompleted_projects", "0");
			String [] lang_Invest= HTMLUtil.getParameterValues(context.getRequest(), "lang_Invest", "0");
			String [] other_Assets= HTMLUtil.getParameterValues(context.getRequest(), "other_Assets", "0");
			String [] sd_short_debt= HTMLUtil.getParameterValues(context.getRequest(), "sd_short_debt", "0");
			String [] sd_bills_should= HTMLUtil.getParameterValues(context.getRequest(), "sd_bills_should", "0");
			String [] sd_funds_should= HTMLUtil.getParameterValues(context.getRequest(), "sd_funds_should", "0");
			String [] sd_other_pay= HTMLUtil.getParameterValues(context.getRequest(), "sd_other_pay", "0");
			String [] sd_shareholders= HTMLUtil.getParameterValues(context.getRequest(), "sd_shareholders", "0");
			String [] sd_one_year= HTMLUtil.getParameterValues(context.getRequest(), "sd_one_year", "0");
			String [] sd_other= HTMLUtil.getParameterValues(context.getRequest(), "sd_other", "0");
			String [] lang_debt= HTMLUtil.getParameterValues(context.getRequest(), "lang_debt", "0");
			String [] other_long_debt= HTMLUtil.getParameterValues(context.getRequest(), "other_long_debt", "0");
			String [] other_debt= HTMLUtil.getParameterValues(context.getRequest(), "other_debt", "0");
			String [] share_capital= HTMLUtil.getParameterValues(context.getRequest(), "share_capital", "0");
			String [] surplus_Capital= HTMLUtil.getParameterValues(context.getRequest(), "surplus_Capital", "0");
			String [] surplus_income= HTMLUtil.getParameterValues(context.getRequest(), "surplus_income", "0");
			String [] this_losts= HTMLUtil.getParameterValues(context.getRequest(), "this_losts", "0");
			String [] project_changed= HTMLUtil.getParameterValues(context.getRequest(), "project_changed", "0");
			String [] s_start_date= HTMLUtil.getParameterValues(context.getRequest(), "s_start_date", "0");
			String [] s_sale_net_income= HTMLUtil.getParameterValues(context.getRequest(), "s_sale_net_income", "0");
			String [] s_sale_cost= HTMLUtil.getParameterValues(context.getRequest(), "s_sale_cost", "0");
			String [] s_other_gross_profit= HTMLUtil.getParameterValues(context.getRequest(), "s_other_gross_profit", "0");
			String [] s_operating_expenses= HTMLUtil.getParameterValues(context.getRequest(), "s_operating_expenses", "0");
			String [] s_nonbusiness_income= HTMLUtil.getParameterValues(context.getRequest(), "s_nonbusiness_income", "0");
			String [] s_interest_expense= HTMLUtil.getParameterValues(context.getRequest(), "s_interest_expense", "0");
			String [] s_other_nonbusiness_expense= HTMLUtil.getParameterValues(context.getRequest(), "s_other_nonbusiness_expense", "0");
			String [] s_income_tax_expense= HTMLUtil.getParameterValues(context.getRequest(), "s_income_tax_expense", "0");
			String [] ca_other_Funds_should= HTMLUtil.getParameterValues(context.getRequest(), "ca_other_Funds_should", "0");
			
			for(int i=0;i<project_item.length;i++){
				Map tempMap=new HashMap();
				tempMap.put("project_item", project_item[i]);
				tempMap.put("ca_cash_price", ca_cash_price[i]);
				tempMap.put("ca_short_Invest", ca_short_Invest[i]);
				tempMap.put("ca_bills_should", ca_bills_should[i]);
				tempMap.put("ca_Funds_should", ca_Funds_should[i]);
				tempMap.put("ca_Goods_stock", ca_Goods_stock[i]);
				tempMap.put("ca_other", ca_other[i]);
				tempMap.put("fa_land", fa_land[i]);
				tempMap.put("fa_buildings", fa_buildings[i]);
				tempMap.put("fa_equipments", fa_equipments[i]);
				tempMap.put("fa_rent_Assets", fa_rent_Assets[i]);
				tempMap.put("fa_transports", fa_transports[i]);
				tempMap.put("fa_other", fa_other[i]);
				tempMap.put("fa_Depreciations", fa_Depreciations[i]);
				tempMap.put("fa_Incompleted_projects", fa_Incompleted_projects[i]);
				tempMap.put("lang_Invest", lang_Invest[i]);
				tempMap.put("other_Assets", other_Assets[i]);
				tempMap.put("sd_short_debt", sd_short_debt[i]); 
				tempMap.put("sd_bills_should", sd_bills_should[i]); 
				tempMap.put("sd_funds_should", sd_funds_should[i]); 
				tempMap.put("sd_other_pay", sd_other_pay[i]); 
				tempMap.put("sd_shareholders", sd_shareholders[i]); 
				tempMap.put("sd_one_year", sd_one_year[i]); 
				tempMap.put("sd_other", sd_other[i]); 
				tempMap.put("lang_debt", lang_debt[i]); 
				tempMap.put("other_long_debt", other_long_debt[i]); 
				tempMap.put("other_debt", other_debt[i]); 
				tempMap.put("share_capital", share_capital[i]); 
				tempMap.put("surplus_Capital", surplus_Capital[i]); 
				tempMap.put("surplus_income", surplus_income[i]); 
				tempMap.put("this_losts", this_losts[i]); 
				tempMap.put("project_changed", project_changed[i]); 
				tempMap.put("s_start_date", s_start_date[i]); 
				tempMap.put("s_sale_net_income", s_sale_net_income[i]); 
				tempMap.put("s_sale_cost", s_sale_cost[i]); 
				tempMap.put("s_other_gross_profit", s_other_gross_profit[i]); 
				tempMap.put("s_operating_expenses", s_operating_expenses[i]); 
				tempMap.put("s_nonbusiness_income", s_nonbusiness_income[i]); 
				tempMap.put("s_interest_expense", s_interest_expense[i]); 
				tempMap.put("s_other_nonbusiness_expense", s_other_nonbusiness_expense[i]); 
				tempMap.put("s_income_tax_expense", s_income_tax_expense[i]);  
				tempMap.put("ca_other_Funds_should", ca_other_Funds_should[i]);  
				tempMap.put("credit_id", context.getContextMap().get("credit_id"));
				sqlMapper.insert("creditPriorRecords.createCorpReport",tempMap);
			}
			
			//财务报表添加备注  type=1为 负债表   type=0为 损益表
			context.contextMap.put("credit_id", context.contextMap.get("credit_id"));
			if(context.contextMap.get("fuzhai")!=null){
				context.contextMap.put("remark", context.contextMap.get("fuzhai"));
				context.contextMap.put("type",1);
				sqlMapper.insert("creditPriorRecords.insertCorpReportRemark", context.contextMap);
			}
			if(context.contextMap.get("sunyi")!=null){
				context.contextMap.put("remark", context.contextMap.get("sunyi"));
				context.contextMap.put("type",0);
				sqlMapper.insert("creditPriorRecords.insertCorpReportRemark", context.contextMap);			
			}
			sqlMapper.commitTransaction();		
		} catch (SQLException e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--财务报表保存错误!请联系管理员") ;
		}finally {
			try {
				// 关闭事务
				sqlMapper.endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			} 
		} 
		if(errList.isEmpty()){
			Output.jspSendRedirect(context,
				"defaultDispatcher?__action=creditPriorRecords.getCreditPriorRecords&showFlag=6&credit_id="
									+ context.contextMap.get("credit_id"));
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void getCorpReports(Context context) throws SQLException{
		List corpReportLists=null;
		Map outputMap=new HashMap();
		Map creditMap = null; 
		Map memoMap = null;
		List<Map> memoList = null;
		Map remark1Map = null;
		Map remark2Map = null;
		List errList = context.errList ;
		try {			
			memoList = (List) DataAccessor.query(
					"creditReportManage.selectMemo", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			outputMap.put("memoList", memoList);
			context.contextMap.put("data_type", "客户来源");
			creditMap = (Map) DataAccessor.query(
					"creditReportManage.selectCreditBaseInfo",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
			outputMap.put("creditMap", creditMap);
			memoMap = (Map) DataAccessor.query(
					"creditReportManage.selectNewMemo", context.contextMap,
					DataAccessor.RS_TYPE.MAP);
			outputMap.put("memoMap", memoMap);
			List companyList = null;
			companyList = (List) DataAccessor.query(
					"companyManage.queryCompanyAlias", null,
					DataAccessor.RS_TYPE.LIST);
			//System.out.println(creditMap.get("DECP_ID").toString()+"=========");
			outputMap.put("companyList", companyList);
			corpReportLists=(List)DataAccessor.query("creditPriorRecords.getCorpReports", context.contextMap, DataAccessor.RS_TYPE.LIST);
			/**
			 * 	如果客户为续签客户类型&&目前项目还没有财务报表
			 *  添加项目时引入该续签客户之前项目的财务报表
			 */				
			if("续签客户".equals(creditMap.get("TYPE"))&&corpReportLists.size()==0){
				Object obj = DataAccessor.query("creditPriorRecords.getXQCreditId", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
				context.contextMap.put("credit_id", obj);
				corpReportLists=(List)DataAccessor.query("creditPriorRecords.getCorpReports", context.contextMap, DataAccessor.RS_TYPE.LIST);
				//System.out.println(context.contextMap.get("credit_id"));
				//System.out.println(corpReportLists);
			}					
			if(corpReportLists.size()>0){
				outputMap.put("obj1", corpReportLists.get(0));
				outputMap.put("obj2", corpReportLists.get(1));
				outputMap.put("obj3", corpReportLists.get(2));
			}
			
			context.contextMap.put("type", 1);
			remark1Map = (Map) DataAccessor.query(
					"creditPriorRecords.selectCorpReportsRemark", context.contextMap,
					DataAccessor.RS_TYPE.MAP);
			outputMap.put("remark1Map", remark1Map);
			
			context.contextMap.put("type", 0);
			remark2Map = (Map) DataAccessor.query(
					"creditPriorRecords.selectCorpReportsRemark", context.contextMap,
					DataAccessor.RS_TYPE.MAP);
			outputMap.put("remark2Map", remark2Map);
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--财务报表显示错误!请联系管理员") ;
		}
		outputMap.put("showStatus", context.getContextMap().get("showStatus"));
		outputMap.put("showFlag", context.contextMap.get("showFlag"));
		if(errList.isEmpty()){
//			Output.jspOutput(outputMap, context, "/credit/creditFrame.jsp");
			//根据报告类型
			int productionType = LeaseUtil.getProductionTypeByCreditId((String)context.contextMap.get("credit_id"));
			if(productionType==1){
				Output.jspOutput(outputMap, context, "/credit/equip/creditFrame.jsp");
			}else if(productionType==2){
				Output.jspOutput(outputMap, context, "/credit/truck/creditFrame.jsp");
			}else if(productionType==3){
				Output.jspOutput(outputMap, context, "/credit/car/creditFrame.jsp");
			}
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	@SuppressWarnings("unchecked")
	public void getCorpReportsForShow(Context context) throws SQLException{
		List corpReportLists=null;
		Map outputMap=new HashMap();
		Map creditMap = null; 
		Map memoMap = null;
		List<Map> memoList = null;	
		Map remark1Map = null;
		Map remark2Map = null;
		List errList = context.errList ;
		try {			
			memoList = (List) DataAccessor.query(
					"creditReportManage.selectMemo", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			outputMap.put("memoList", memoList);
			context.contextMap.put("data_type", "客户来源");
			creditMap = (Map) DataAccessor.query(
					"creditReportManage.selectCreditBaseInfo",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
			outputMap.put("creditMap", creditMap);
			memoMap = (Map) DataAccessor.query(
					"creditReportManage.selectNewMemo", context.contextMap,
					DataAccessor.RS_TYPE.MAP);
			outputMap.put("memoMap", memoMap);
			corpReportLists=(List)DataAccessor.query("creditPriorRecords.getCorpReports", context.contextMap, DataAccessor.RS_TYPE.LIST);
			if(corpReportLists.size()>0){
				outputMap.put("obj1", corpReportLists.get(0));
				outputMap.put("obj2", corpReportLists.get(1));
				outputMap.put("obj3", corpReportLists.get(2));
			}
			context.contextMap.put("type", 1);
			remark1Map = (Map) DataAccessor.query(
					"creditPriorRecords.selectCorpReportsRemark", context.contextMap,
					DataAccessor.RS_TYPE.MAP);
			outputMap.put("remark1Map", remark1Map);
			
			context.contextMap.put("type", 0);
			remark2Map = (Map) DataAccessor.query(
					"creditPriorRecords.selectCorpReportsRemark", context.contextMap,
					DataAccessor.RS_TYPE.MAP);
			outputMap.put("remark2Map", remark2Map);
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--财务报表查看错误!请联系管理员") ;
		}
		outputMap.put("examineFlag", context.contextMap
				.get("examineFlag"));
		outputMap.put("showStatus", context.getContextMap().get("showStatus"));
		outputMap.put("showFlag", context.contextMap.get("showFlag"));
		if(errList.isEmpty()){
//			if(DataUtil.intUtil(context.contextMap.get("commit_flag"))==1){
//				outputMap.put("commit_flag", context.contextMap.get("commit_flag"));
//				outputMap.put("isSalesDesk", context.contextMap.get("isSalesDesk"));
//				Output.jspOutput(outputMap, context, "/credit/creditFrameCommit.jsp");
//			}else{
//				Output.jspOutput(outputMap, context, "/credit/creditFrameShow.jsp");
//			}
			//根据报告类型
			int productionType = LeaseUtil.getProductionTypeByCreditId((String)context.contextMap.get("credit_id"));
			if(DataUtil.intUtil(context.contextMap.get("commit_flag"))==1){
				outputMap.put("commit_flag", context.contextMap.get("commit_flag"));
				outputMap.put("isSalesDesk", context.contextMap.get("isSalesDesk"));


				if(productionType==1){
					Output.jspOutput(outputMap, context, "/credit/equip/creditFrameCommit.jsp");
				}else if(productionType==2){
					Output.jspOutput(outputMap, context, "/credit/truck/creditFrameCommit.jsp");
				}else if(productionType==3){
					Output.jspOutput(outputMap, context, "/credit/car/creditFrameCommit.jsp");
				}
			}else{
				
				if(productionType==1){
					Output.jspOutput(outputMap, context, "/credit/equip/creditFrameShow.jsp");
				}else if(productionType==2){
					Output.jspOutput(outputMap, context, "/credit/truck/creditFrameShow.jsp");
				}else if(productionType==3){
					Output.jspOutput(outputMap, context, "/credit/car/creditFrameShow.jsp");
				}
			}
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}	
	
	/**
	 * 导出 excel 财务报表
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void expCreditReport(Context context){  
		List<Map> corpReportLists=null;
		Map obj1 = new HashMap();
		Map obj2 = new HashMap();
		Map obj3 = new HashMap();
		Map<String, Map> exportMap = new HashMap<String, Map>();
		try {
			corpReportLists=(List<Map>)DataAccessor.query("creditPriorRecords.getCorpReports", context.contextMap, DataAccessor.RS_TYPE.LIST);
			//加入说明
			context.contextMap.put("type", 1);
			exportMap.put("remark1Map",(Map) DataAccessor.query(
					"creditPriorRecords.selectCorpReportsRemark", context.contextMap,
					DataAccessor.RS_TYPE.MAP) );
			
			context.contextMap.put("type", 0);
			exportMap.put("remark2Map",(Map) DataAccessor.query(
					"creditPriorRecords.selectCorpReportsRemark", context.contextMap,
					DataAccessor.RS_TYPE.MAP));
			if(corpReportLists.size()>0){
				obj1=corpReportLists.get(0);
				obj2=corpReportLists.get(1);
				obj3=corpReportLists.get(2);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		
		//计算第一页 sum值
		CreditReport.valueChangeTable1(obj1);
		CreditReport.valueChangeTable1(obj2);
		CreditReport.valueChangeTable1(obj3);
		
		//计算第二页 sum值
		CreditReport.valueChangeTable2(obj1);
		CreditReport.valueChangeTable2(obj2);
		CreditReport.valueChangeTable2(obj3);
				
		exportMap.put("obj1", obj1);
		exportMap.put("obj2", obj2);
		exportMap.put("obj3", obj3);
		
		//计算第一页 比率及差异
		CreditReport.initAllData(exportMap);
		
		//计算第二页 比率 成长率
		CreditReport.initTable2Data(exportMap);
		
		//计算第三页 需要数据
		CreditReport.ratio(exportMap);
				
		ByteArrayOutputStream baos = null;
		String strFileName = "财务报表("+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+").xls";
		
		ExportExcelUtil exportExcelUtil = new ExportExcelUtil();
		exportExcelUtil.createexl();
		baos = exportExcelUtil.exportExcel(exportMap);
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");		
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
			context.response.setContentType("application nd.ms-excel;charset=GB2312");
			ServletOutputStream out1 = context.response.getOutputStream();
			exportExcelUtil.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}

	/**
	 * @author ShenQi
	 * @param context(credit_id)
	 * @throws SQLException 
	 * @功能:通过读取excel文件插入过往记录下的银行对账单的数据到数据库
	 * @excel模版:在上传页面提供下载
	 * */
	public void uploadExcel(Context context) throws SQLException {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......uploadExcel";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		Map<String,List> outputMap=new HashMap<String,List>();
		
		Workbook workbook=null;
		
		InputStream in=(InputStream)context.getContextMap().get("excelInputStream");//获得页面上传的附件,数据流
		
		if(in==null) {//后台验证上传附件
			context.errList.add("上传附件为空或者格式错误!");
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
			return;
		}
		java.text.SimpleDateFormat df=new java.text.SimpleDateFormat("yyyy-MM-dd");
		try {
			workbook=WorkbookFactory.create(in);//创建excel workbook
			
			Sheet sheet=workbook.getSheetAt(0);//获得第一个sheet
			
			Iterator<Row> rowIterator=sheet.iterator();//遍历行
			
			Map<String,Object> bankInfo=new HashMap<String,Object>();//准备插入数据源的map
			
			String number=null;
			
			while(rowIterator.hasNext()) {
				
				Row row=(Row)rowIterator.next();//获得行
				
				Iterator<Cell> cellIterator=row.cellIterator();//遍历单元格
				
				while(cellIterator.hasNext()) {
					
					Cell cell=(Cell)cellIterator.next();//获得单元格
					
					if(row.getRowNum()==0) {//excel第一行期间(月)
						if(cell.getColumnIndex()==0) {
							//第一行第一列是TITLE
						} else {
							number=this.transferNumber(cell.getColumnIndex());
							//项目中先前插入语句的别名是
							/*CHECKMONTHONE
							  CHECKMONTHTWO
							  CHECKMONTHTHREE
							  CHECKMONTHFOUR
							  CHECKMONTHFIVE
							  CHECKMONTHSIX
							  所以使用CHECKMONTH+number
							*/
							if(cell.getDateCellValue()!=null) {//如果单元格不为空则插入数据
								bankInfo.put("CHECKMONTH"+number,df.format(cell.getDateCellValue()));
							} else {
								bankInfo.put("CHECKMONTH"+number,"");
							}
						}
					} else if(row.getRowNum()==1) {//excel第二行上期结余
						if(cell.getColumnIndex()==0) {
							
						} else {
							number=this.transferNumber(cell.getColumnIndex());
							
							if(cell.getCellType()!=3) {//3代表单元格是空,没有填写数字
								bankInfo.put("LASTSUM"+number,cell.getNumericCellValue());
							} else {
								bankInfo.put("LASTSUM"+number,0);
							}
						}
					} else if(row.getRowNum()==2) {//excel第三行每月收入
						if(cell.getColumnIndex()==0) {
							
						} else {
							number=this.transferNumber(cell.getColumnIndex());
							
							if(cell.getCellType()!=3) {//3代表单元格是空,没有填写数字
								bankInfo.put("MONTHINCOME"+number,cell.getNumericCellValue());
							} else {
								bankInfo.put("MONTHINCOME"+number,0);
							}
						}
					} else if(row.getRowNum()==3) {//excel第四行每月支出
						if(cell.getColumnIndex()==0) {
							
						} else {
							number=this.transferNumber(cell.getColumnIndex());
							
							if(cell.getCellType()!=3) {//3代表单元格是空,没有填写数字
								bankInfo.put("MONTHCOST"+number,cell.getNumericCellValue());
							} else {
								bankInfo.put("MONTHCOST"+number,0);
							}
						}
					} else if(row.getRowNum()==4) {//excel第五行本期结余
						if(cell.getColumnIndex()==0) {
							
						} else {
							number=this.transferNumber(cell.getColumnIndex());
							
							if(cell.getCellType()!=3) {//3代表单元格是空,没有填写数字
								bankInfo.put("THISSUM"+number,cell.getNumericCellValue());
							} else {
								bankInfo.put("THISSUM"+number,0);
							}
						}
					} else if(row.getRowNum()==5) {//excel第六行现金流入
						if(cell.getColumnIndex()==0) {
							
						} else {
							number=this.transferNumber(cell.getColumnIndex());
							
							if(cell.getCellType()!=3) {//3代表单元格是空,没有填写数字
								bankInfo.put("MONEYFLOWIN"+number,cell.getNumericCellValue());
							} else {
								bankInfo.put("MONEYFLOWIN"+number,0);
							}
						}
					} else if(row.getRowNum()==6) {//excel第七行户名,银行支行,帐号
						if(cell.getColumnIndex()==1) {//户名
							bankInfo.put("BANKCUSTNAME",cell.getStringCellValue()==null?"":cell.getStringCellValue());
						} else if(cell.getColumnIndex()==3) {
							bankInfo.put("BANKBRANCH",cell.getStringCellValue()==null?"":cell.getStringCellValue());//银行支行
						} else if(cell.getColumnIndex()==5) {//账户
							if(cell.getCellType()!=3) {
								String account=String.valueOf(cell.getNumericCellValue());
								bankInfo.put("BANKCUSTCODE",account.substring(0,account.length()-2));//银行支行
							} else {
								bankInfo.put("BANKCUSTCODE","");
							}
						}
					}
				}
			}
			
			bankInfo.put("s_employeeId",context.contextMap.get("s_employeeId"));
			bankInfo.put("credit_id",context.contextMap.get("credit_id"));
			DataAccessor.execute("creditPriorRecords.createCreditPriorBankCheckBillSix",bankInfo,OPERATION_TYPE.INSERT);
			
		} catch (InvalidFormatException e) {
			LogPrint.getLogStackTrace(e,logger);//日志输出错误信息
			context.errList.add("上传银行对账单出错,请联系管理员!");
			e.printStackTrace();
		} catch (IOException e) {
			LogPrint.getLogStackTrace(e,logger);
			context.errList.add("上传银行对账单出错,请联系管理员!");
			e.printStackTrace();
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e,logger);
			context.errList.add("上传银行对账单出错,请联系管理员!");
			e.printStackTrace();
		}
		
		if(context.errList.isEmpty()) {
			context.contextMap.put("showFlag",3);//设定showFlag=3
			//页面点击跳转过往记录的链接如下,有2个参数credit_id,showFlag
			//<a href="#tabs-3" 
			//onclick="javascript:location.href='${ctx }/servlet/defaultDispatcher?
			//__action=creditPriorRecords.getCreditPriorRecords&credit_id=${creditMap.ID }&showFlag=3'">过往记录</a>
			//重新加载跳转到过往记录
			this.getCreditPriorRecords(context);
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
	
	//数字转换1=ONE,2=TWO,3=THREE,4=FOUR,5=FIVE,6=SIX
	private String transferNumber(int columnIndex) {
		
		String number="";
		if(columnIndex==1) {
			number="ONE";
		} else if(columnIndex==2) {
			number="TWO";
		} else if(columnIndex==3) {
			number="THREE";
		} else if(columnIndex==4) {
			number="FOUR";
		} else if(columnIndex==5) {
			number="FIVE";
		} else if(columnIndex==6) {
			number="SIX";
		}
		return number;
	}
}
