package com.brick.credit.vip.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.brick.base.to.SelectionTo;
import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.OPERATION_TYPE;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DataUtil;
import com.brick.util.FileExcelUpload;
import com.brick.util.FileUpload;
import com.brick.util.StringUtils;
import com.brick.util.web.HTMLUtil;
import com.ibatis.common.resources.Resources;
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
	public void getCreditPriorRecords(Context context) {
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
		
		//上一个报告的id
		String listCreditId = "" ;
		try {			
			//查询上一次报告的id
			listCreditId = (String) DataAccessor.query("creditPriorRecords.getLestCreditId", context.contextMap, DataAccessor.RS_TYPE.OBJECT) ;
			
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

			
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--过往记录错误!请联系管理员") ;
		}
		
		outputMap.put("showFlag", 3);
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/credit_vip/creditFrame.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}

	@SuppressWarnings("unchecked")
	public void getCreditPriorRecordsForShow(Context context) {
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

			/*priorContractInfo = (List) DataAccessor.query(
				"creditPriorRecords.getContractInfoBycust_id",
				context.contextMap, DataAccessor.RS_TYPE.LIST);*/
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
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--过往记录错误!请联系管理员") ;
		}
		outputMap.put("examineFlag", context.contextMap.get("examineFlag"));
		outputMap.put("showFlag", 3);
		if(errList.isEmpty()){
		if(DataUtil.intUtil(context.contextMap.get("commit_flag"))==1){
			outputMap.put("commit_flag", context.contextMap.get("commit_flag"));
			Output.jspOutput(outputMap, context, "/credit_vip/creditFrameCommit.jsp");
		}else{
			Output.jspOutput(outputMap, context, "/credit_vip/creditFrameShow.jsp");
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
		  //主要往来客户总备注的保存
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
					map.put("s_employeeId", context.contextMap.get("s_employeeId"));
					map.put("credit_id", context.contextMap.get("credit_id"));
					sqlMapper.insert("creditPriorRecords.createCreditPriorCC",map);
				}
						
			}
		   }

		   
		   
		   
		 //进货厂商的信息保存
		    String[] BUYFACTORYNAMEs =HTMLUtil.getParameterValues(context.getRequest(), "BUYFACTORYNAME", "");
		   
		    String[] BUYTHINGKINDs =HTMLUtil.getParameterValues(context.getRequest(), "BUYTHINGKIND", "");

		    String[] BUYMONTHINGOPRICEs =HTMLUtil.getParameterValues(context.getRequest(), "BUYMONTHINGOPRICE", "0");

		    String[] BUYPERCENTGRAVEs =HTMLUtil.getParameterValues(context.getRequest(), "BUYPERCENTGRAVE", "0");

		    String[] BUYPAYCONDITIONs =HTMLUtil.getParameterValues(context.getRequest(), "BUYPAYCONDITIONS", "");
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
			
			
				//生产经营情况说明
			/*Map map = new HashMap();
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
						"defaultDispatcher?__action=creditVoucherVip.getCreditVouchers&showFlag=4&credit_id="
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
				"defaultDispatcher?__action=creditPriorRecordsVip.getCreditPriorRecords&showFlag=3&credit_id="
									+ context.contextMap.get("credit_id"));
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void getCorpReports(Context context){
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
			/*
			corpReportLists=(List)DataAccessor.query("creditPriorRecords.getCorpReports", context.contextMap, DataAccessor.RS_TYPE.LIST);
			*//**
			 * 	如果客户为续签客户类型&&目前项目还没有财务报表
			 *  添加项目时引入该续签客户之前项目的财务报表
			 *//*				
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
			*/
			List<Map<String, Object>> cropImgList = (List<Map<String, Object>>) DataAccessor.query("creditPriorRecords.getCorpImg", context.contextMap, RS_TYPE.LIST);
			/*Integer file_type = null;
			for (Map<String, Object> cropImg : cropImgList) {
				file_type = cropImg.get("FILE_TYPE") == null ? 0 : (Integer)cropImg.get("FILE_TYPE");
				if (file_type == 1) {
					outputMap.put("cropImg1", cropImg);
					continue;
				}
				if (file_type == 2) {
					outputMap.put("cropImg2", cropImg);
					continue;
				}
				if (file_type == 3) {
					outputMap.put("cropImg3", cropImg);
					continue;
				}
			}*/
			outputMap.put("cropImgList", cropImgList);
			outputMap.put("basePath", FileUpload.getUploadPath("cropReportImg"));
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--财务报表显示错误!请联系管理员") ;
		}
		outputMap.put("showStatus", context.getContextMap().get("showStatus"));
		outputMap.put("showFlag", 2);
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/credit_vip/creditFrame.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	@SuppressWarnings("unchecked")
	public void getCorpReportsForShow(Context context){
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
			List<Map<String, Object>> cropImgList = (List<Map<String, Object>>) DataAccessor.query("creditPriorRecords.getCorpImg", context.contextMap, RS_TYPE.LIST);
			outputMap.put("cropImgList", cropImgList);
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--财务报表查看错误!请联系管理员") ;
		}
		outputMap.put("examineFlag", context.contextMap
				.get("examineFlag"));
		outputMap.put("showStatus", context.getContextMap().get("showStatus"));
		outputMap.put("showFlag", 2);
		if(errList.isEmpty()){
			if(DataUtil.intUtil(context.contextMap.get("commit_flag"))==1){
				outputMap.put("commit_flag", context.contextMap.get("commit_flag"));
				Output.jspOutput(outputMap, context, "/credit_vip/creditFrameCommit.jsp");
			}else{
				Output.jspOutput(outputMap, context, "/credit_vip/creditFrameShow.jsp");
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
	
	public void uploadCorpReport(Context context){
		System.out.println("============success============");
		List fileItems = (List) context.contextMap.get("uploadList");
		List errList = context.errList ;
		Map outputMap = new HashMap() ;
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		for (Iterator iterator = fileItems.iterator(); iterator.hasNext();) {
			FileItem fileItem = (FileItem) iterator.next();
			//InputStream in = fileItem.getInputStream();
			//System.out.println(fileItem.getFieldName());
			if (!fileItem.getName().equals("")) {
				SqlMapClient sqlMapClient = DataAccessor.getSession();
				try {
					sqlMapClient.startTransaction();
					saveFileToDisk(context, fileItem, sqlMapClient);
					sqlMapClient.commitTransaction();
				} catch (Exception e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				} finally {
					try {
						sqlMapClient.endTransaction();
					} catch (SQLException e) {
						e.printStackTrace();
						LogPrint.getLogStackTrace(e, logger);
					}
				}
			}
		}
		getCorpReports(context);
	}
	
	/**
	 * 保存文件到硬盘中 并将保存信息存入数据库
	 * 
	 * @param context
	 * @param fileItem
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void saveFileToDisk(Context context, FileItem fileItem,
			SqlMapClient sqlMapClient) {
		String filePath = fileItem.getName();
		List errList = context.errList;
		logger.info("文件大小==========>>" + fileItem.getSize());
		if (fileItem.getSize() > 2097152) {
			errList.add("附件太大了，不能大于2M了。") ;
		}
		String new_file_name = (String) context.contextMap.get("new_file_name");
		String type = filePath.substring(filePath.lastIndexOf(".") + 1);
		Map contextMap = context.contextMap;
		String bootPath = null;
		bootPath = FileUpload.getUploadPath("cropReportImg");
		String file_path = "";
		String file_name = "";
		Long syupId = null;
		if (bootPath != null) {
			//Modify by Michael 2012 07-13 上传附档增加日期文件夹
			File realPath = new File(bootPath+ File.separator+new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + File.separator + type);
			if (!realPath.exists())
				realPath.mkdirs();
			String excelNewName = FileExcelUpload.getNewFileName();
			File uploadedFile = new File(realPath.getPath() + File.separator
					+ excelNewName + "." + type);
			file_path = File.separator+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+File.separator + type + File.separator + excelNewName
					+ "." + type;
			file_name = excelNewName + "." + type;
			try {
				if (errList.isEmpty()) {
					fileItem.write(uploadedFile);
					contextMap.put("file_path", file_path);
					contextMap.put("file_name", StringUtils.isEmpty(new_file_name) ? fileItem.getName() : new_file_name);
					contextMap.put("file_type", type);
					//contextMap.put("title", "暂收款水单附件");

					sqlMapClient.insert("creditPriorRecords.insertCropImg", contextMap);
				}
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} finally {
				try {
					fileItem.getInputStream().close();
				} catch (IOException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
				fileItem.delete();
			}
		}
	}
	
	public void deleteCorpImg(Context context){
		try {
			DataAccessor.execute("creditPriorRecords.updateCropImg", context.contextMap, OPERATION_TYPE.UPDATE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		getCorpReports(context);
	}
	
	/**
	 * 
	 * @return 下载
	 */
	public void downloadImg(Context context) {
		System.out.println("===========================下载图片咯===============================");
		String savaPath = (String) context.contextMap.get("img_path");
		String name = (String) context.contextMap.get("img_name");
		String bootPath = FileUpload.getUploadPath("cropReportImg");
		String path = bootPath + savaPath;
		System.out.println(path);
		path.replace("\\", "/");
		File file = new File(path);
		context.response.reset();
		context.response.setCharacterEncoding("gb2312");
		OutputStream output = null;
		FileInputStream fis = null;
		try {
			//context.response.setContentType("Content-type: image/jpeg");
			context.response.setHeader("Content-Disposition",
					"attachment; filename="
							+ new String(name.getBytes("gb2312"), "iso8859-1"));

			output = context.response.getOutputStream();
			fis = new FileInputStream(file);

			byte[] b = new byte[1024];
			int i = 0;

			while ((i = fis.read(b)) != -1) {

				output.write(b, 0, i);
			}
			output.write(b, 0, b.length);

			output.flush();
			context.response.flushBuffer();
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				}
				fis = null;
			}
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				}
				output = null;
			}
		}
	}
	
}
