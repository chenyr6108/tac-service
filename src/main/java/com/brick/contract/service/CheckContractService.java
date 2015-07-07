package com.brick.contract.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.util.LeaseUtil;
import com.brick.baseManage.service.BusinessLog;
import com.brick.collection.service.CollectionManage;
import com.brick.collection.service.StartPayService;
import com.brick.collection.support.PayRate;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.Constants;
import com.brick.util.DataUtil;
import com.brick.util.web.HTMLUtil;
import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 合同复核
 * 
 * @author  li shaojie
 * @date Jul 12, 2010
 */

public class CheckContractService extends AService {
	Log logger = LogFactory.getLog(CheckContractService.class);

	public static final int allRight=0;
	
	public static final int contractRight=1;
	
	public static final int creditRight=2;
	
	public static final int noRight=3;
	
	/**
	 * 合同复核--根据合同ID查询相关信息用于复核
	 * lisj
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void checkContract(Context context){
		Map outputMap=new HashMap();
		Map customerInfoMap=null;
		Map creditSchema=null;
		Map contractSchema=null;
		List insureCompanyList=null;
		List creditEquipments=null;
		List contractEquipments=null;
		List creditInsure=null;
		List contractInsure=null;
		List contractOtherPrice=null;
		List creditOtherPrice=null;
		Integer cust_type =null ;
		try {
			
			context.contextMap.put("RECT_ID",context.contextMap.get("rect_id"));
			customerInfoMap=(Map)DataAccessor.query("checkContract.getContractAndCreditCustomerInfoByRect_Id", context.contextMap, DataAccessor.RS_TYPE.MAP);
			context.contextMap.put("credit_id", customerInfoMap.get("PRCD_ID"));
			cust_type = DataUtil.intUtil( customerInfoMap.get("CUST_TYPE"));        
			contractSchema=(Map)DataAccessor.query("checkContract.getContractSchemaByCredit_id", context.contextMap, DataAccessor.RS_TYPE.MAP);
			creditSchema=(Map)DataAccessor.query("checkContract.getCreditSchemaByCredit_id", context.contextMap, DataAccessor.RS_TYPE.MAP);
			insureCompanyList = (List<Map>) DataAccessor.query("insuCompany.queryInsureCompanyListForSelect", null, DataAccessor.RS_TYPE.LIST);
			contractEquipments = (List<Map>) DataAccessor.query("checkContract.getContractEquipmentByContract_Id",  context.contextMap, DataAccessor.RS_TYPE.LIST);
			creditEquipments = (List<Map>) DataAccessor.query("checkContract.getCreditEquipmentByCredit_Id",  context.contextMap, DataAccessor.RS_TYPE.LIST);
			creditInsure = (List<Map>) DataAccessor.query("checkContract.getCreditInsureByCredit_Id",  context.contextMap, DataAccessor.RS_TYPE.LIST);
			contractInsure = (List<Map>) DataAccessor.query("checkContract.getContractInsureByContract_Id",  context.contextMap, DataAccessor.RS_TYPE.LIST);
			contractOtherPrice = (List<Map>) DataAccessor.query("checkContract.getContractOtherPriceByContract_Id",  context.contextMap, DataAccessor.RS_TYPE.LIST);
			creditOtherPrice = (List<Map>) DataAccessor.query("checkContract.getCreditOtherPriceByCredit_Id",  context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("customerInfoMap", customerInfoMap);
		outputMap.put("creditSchema", creditSchema);
		outputMap.put("contractSchema", contractSchema);
		outputMap.put("insureCompanyList", insureCompanyList);
		outputMap.put("creditEquipments", creditEquipments);
		outputMap.put("contractEquipments", contractEquipments);
		outputMap.put("creditInsure", creditInsure);
		outputMap.put("contractInsure", contractInsure);
		outputMap.put("contractOtherPrice", contractOtherPrice);
		outputMap.put("creditOtherPrice", creditOtherPrice);
		
		
		
	
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
		
		if (cust_type == 1){
			Output.jspOutput(outputMap, context, "/rentcontract/checkContractCorp.jsp");
		}else if(cust_type ==0){
			Output.jspOutput(outputMap, context, "/rentcontract/checkContractNatu.jsp");
		}
	}
	/**
	 * 复核（通过/驳回）
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void saveDifference(Context context){
		String flag=""+context.contextMap.get("flag");
		Map dataMap=new HashMap();
		Map updateMap=new HashMap();
		SqlMapClient sqlMapper=null;
		List<Map> equipmentsList = null;
		List<Map> insuresList = null;
		List<Map> otherPriceList = null;
		List<Map> contractDetailList=null;
		try {
			sqlMapper=DataAccessor.getSession();
			sqlMapper.startTransaction();
			if(flag.equals("1")){
				//判断支付表是否存在，若已存在则不生成新的支付表
				String recpid = LeaseUtil.getRecpIdByRectId((String)context.contextMap.get("rect_id"));
				if(recpid!=null){
					Output.jspSendRedirect(context, "defaultDispatcher?__action=rentContract.queryRentContract");
					return;
				}
				
				dataMap=getSaveMap(context,dataMap);
				//System.out.println(dataMap);
				dataMap.put("RECC_TYPE", 1);
				sqlMapper.insert("checkContract.saveDifference", dataMap);
				updateMap=this.getUpdateContractMap(dataMap, updateMap);
				//System.out.println(updateMap);
				sqlMapper.update("checkContract.updateContractInfo", updateMap);
				sqlMapper.update("checkContract.updateContractSchema", updateMap); 
				

				if((dataMap.get("EQUIPMENT_STATUS").toString()).equals("2")){
					sqlMapper.update("rentContract.deleteRentContractEquipmentFalse2", dataMap);
					sqlMapper.update("rentContract.deleteRentContractDetailByRectIdFalse", dataMap);
					equipmentsList = (List) DataAccessor.query("creditReportManage.selectCreditEquipment",
							context.contextMap, DataAccessor.RS_TYPE.LIST);
					for (Map map : equipmentsList) {
						int k=Integer.parseInt(""+map.get("AMOUNT"));
						for (int i = 0; i <k ; i++) {
							map.put("RECT_ID", context.contextMap.get("rect_id"));
							map.put("RECS_ID", context.contextMap.get("recs_id"));
							if(map.get("MEMO")==null){
								map.put("MEMO", "");
							}
							map.put("AMOUNT", 1);
							map.put("s_employeeId", context.contextMap.get("s_employeeId"));
							long eqmt_id=(Long)sqlMapper.insert("rentContract.createRentContractEquip", map);
							map.put("EQMT_ID", eqmt_id);
							sqlMapper.insert("rentContract.createRentContractDetail", map);
						}
					} 
				}
				
				if((dataMap.get("INSURE_STATUS").toString()).equals("2")){
					sqlMapper.update("rentContract.deleteRentContractInsureByRectIdFalse", dataMap);
					insuresList = (List) DataAccessor.query("creditReportManage.selectCreditInsure",
							context.contextMap, DataAccessor.RS_TYPE.LIST);
					for (Map map : insuresList) {
						map.put("RECT_ID", context.contextMap.get("rect_id"));
						map.put("s_employeeId", context.contextMap.get("s_employeeId"));
						sqlMapper.insert("rentContract.createRentContractInsure", map);
					}
				}
				if((dataMap.get("OTHERPRICE_STATUS").toString()).equals("2")){ 
					sqlMapper.update("rentContract.deleteRentContractOtherFeeByRectIdFalse", dataMap);
					otherPriceList = (List) DataAccessor.query("creditReportManage.selectCreditOtherPrice",
							context.contextMap, DataAccessor.RS_TYPE.LIST);
					for (Map map : otherPriceList) {
						map.put("RECT_ID", context.contextMap.get("rect_id"));
						map.put("s_employeeId", context.contextMap.get("s_employeeId"));
						DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
						map.put("OTHER_DATE", format.format(map.get("OTHER_DATE")));
						sqlMapper.insert("rentContract.createRentContractOtherFee", map);
					}
					
				}
				//展开支付表
				CollectionManage collectionManage = new CollectionManage();
				Long recpId = 0l;
				Map paylist = (Map) context.request.getSession().getAttribute("s_paylist");
				paylist.put("S_EMPLOYEEID", context.contextMap.get("s_employeeId"));
				paylist.put("VERSION_CODE", Integer.valueOf(1));
				paylist.put("PAYDATE_FLAG", Integer.valueOf(0));
				//
				recpId = (Long)DataAccessor.getSession().insert("collectionManage.createPaylist", paylist);
				paylist.put("RECP_ID", recpId);
				
				//Modify by Michael 2012 09-25 根据税费方案类别选择不同的支付表明细保存方案
				if(Constants.TAX_PLAN_CODE_1.equals(context.contextMap.get("TAX_PLAN_CODE"))||Constants.TAX_PLAN_CODE_3.equals(context.contextMap.get("TAX_PLAN_CODE"))||Constants.TAX_PLAN_CODE_5.equals(context.contextMap.get("TAX_PLAN_CODE"))){
					collectionManage.operatePayline(paylist);
				}else if(Constants.TAX_PLAN_CODE_2.equals(context.contextMap.get("TAX_PLAN_CODE"))){
					collectionManage.operatePaylineByValueAdded(paylist);
				}else if(Constants.TAX_PLAN_CODE_4.equals(context.contextMap.get("TAX_PLAN_CODE"))||Constants.TAX_PLAN_CODE_6.equals(context.contextMap.get("TAX_PLAN_CODE"))||Constants.TAX_PLAN_CODE_7.equals(context.contextMap.get("TAX_PLAN_CODE"))||Constants.TAX_PLAN_CODE_8.equals(context.contextMap.get("TAX_PLAN_CODE"))) {
					collectionManage.operatePayline1(paylist);
				}
				
				collectionManage.operateEquipment(paylist);
				
				collectionManage.operateInsure(paylist);
				
				collectionManage.operateOtherFee(paylist);
				
				collectionManage.operateCollectionIrrMonthPrice(paylist);
				
				sqlMapper.commitTransaction(); 
				
				//
				context.contextMap.put("RECP_CODE", paylist.get("RECP_CODE"));
				context.contextMap.put("RECT_ID", paylist.get("RECT_ID"));
				Output.jspSendRedirect(context, "defaultDispatcher?__action=collectionManage.showPaylist&FLAG=0&RECP_ID="+recpId+"&RECT_ID="+context.contextMap.get("RECT_ID"));
				return;
				
			}else{
				dataMap=getSaveMap(context,dataMap);
				dataMap.put("RECC_TYPE", 2);
				sqlMapper.update("checkContract.updateContractDifference", dataMap);
				sqlMapper.update("checkContract.updateContractStatus", dataMap);
				sqlMapper.insert("checkContract.saveDifference", dataMap);
				sqlMapper.commitTransaction(); 
			}			
		} catch (Exception e) {  
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}finally {
			try {
				sqlMapper.endTransaction();
			} catch (SQLException e) { 
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}
		
		Output.jspSendRedirect(context, "defaultDispatcher?__action=rentContract.queryRentContract");
		
	}
	

	
	@SuppressWarnings("unchecked")
	public void showDifference(Context context){
		Map outputMap=new HashMap();
		Map differenceMap=null;
		try {
			differenceMap=(Map)DataAccessor.query("checkContract.getContractDifferencebyContract_Id", context.contextMap, DataAccessor.RS_TYPE.MAP);
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("differenceMap", differenceMap);
		Output.jspOutput(outputMap, context, "/rentcontract/showDifference.jsp");
	}

	/**
	 * 法人差异数据
	 * @param context
	 * @param dataMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map getSaveMap(Context context,Map dataMap){ 
		Map map=context.contextMap;
		dataMap.put("RECT_ID", map.get("rect_id"));
		dataMap.put("CREATE_USER_ID", context.contextMap.get("s_employeeId"));
		dataMap=dealWithData("LEASE_CODE",map,dataMap);
		dataMap=dealWithData("CUST_NAME",map,dataMap);
		dataMap=dealWithData("CORP_ORAGNIZATION_CODE",map,dataMap);
		dataMap=dealWithData("OPEN_BANK",map,dataMap);
		dataMap=dealWithData("BANK_ACCOUNTS",map,dataMap);
		dataMap=dealWithData("CUST_PHONE",map,dataMap);
		dataMap=dealWithData("CUST_FAX",map,dataMap);
		dataMap=dealWithData("CUST_LINKMAN",map,dataMap);
		dataMap=dealWithData("CUST_ZIP",map,dataMap);
		//dataMap=dealWithData("CUST_ADDRESS",map,dataMap);
		dataMap=dealWithData("LEASE_TOPRIC",map,dataMap);
		dataMap=dealWithData("LEASE_PERIOD",map,dataMap);
		dataMap=dealWithData("LEASE_TERM",map,dataMap);
		dataMap=dealWithData("PLEDGE_PRICE_RATE",map,dataMap);
		dataMap=dealWithData("PLEDGE_PRICE",map,dataMap);
		dataMap=dealWithData("MANAGEMENT_FEE_RATE",map,dataMap);
		dataMap=dealWithData("MANAGEMENT_FEE",map,dataMap);
		dataMap=dealWithData("BUSINESS_TRIP_PRICE",map,dataMap);
		dataMap=dealWithData("FLOAT_RATE",map,dataMap);
		dataMap=dealWithData("YEAR_INTEREST_TYPE",map,dataMap);
		dataMap=dealWithData("YEAR_INTEREST",map,dataMap);
		dataMap=dealWithData("FINE_TYPE",map,dataMap);
		dataMap=dealWithData("FINE_RATE",map,dataMap);
		dataMap=dealWithData("LEASE_RZE",map,dataMap);
		dataMap=dealWithData("PAY_WAY",map,dataMap);
		dataMap=dealWithData("START_DATE",map,dataMap);
		dataMap=dealWithData("EQUPMENT_ADDRESS",map,dataMap);
		dataMap=dealWithData("DEAL_WAY",map,dataMap);
		dataMap=dealWithData("INSURANCE_COMPANY_ID",map,dataMap);
		dataMap=dealWithData("BUY_INSURANCE_WAY",map,dataMap);
		dataMap=dealWithData("BUY_INSURANCE_TIME",map,dataMap);
		dataMap=dealWithData("INSURE_REBATE_RATE",map,dataMap); 
		dataMap=dealWithData2("EQUIPMENT",map,dataMap); 
		dataMap=dealWithData2("INSURE",map,dataMap); 
		dataMap=dealWithData2("OTHERPRICE",map,dataMap); 
		dataMap=dealWithData("PLEDGE_WAY",map,dataMap); 
		dataMap=dealWithData("PLEDGE_PERIOD",map,dataMap); 
		dataMap=dealWithData("SALES_TAX_RATE",map,dataMap); 
		dataMap=dealWithData("INSURE_BASE_RATE",map,dataMap); 
		dataMap=dealWithData("STAMP_TAX_TOPRIC",map,dataMap); 
		dataMap=dealWithData("STAMP_TAX_MONTHPRIC",map,dataMap); 
		dataMap=dealWithData("NATU_IDCARD",map,dataMap); 
		dataMap=dealWithData("NATU_MATE_NAME",map,dataMap); 
		dataMap=dealWithData("NATU_MATE_IDCARD",map,dataMap); 
		dataMap=dealWithData("CORP_REGISTE_ADDRESS",map,dataMap);
		dataMap=dealWithData("CORP_WORK_ADDRESS",map,dataMap);
		return dataMap;
	}
	
	/**
	 * 整理数据
	 * @param context
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map dealWithData(String name,Map map1,Map dataMap){ 
		dataMap.put(name+"_A", map1.get(name+"_A"));
		dataMap.put(name+"_B", map1.get(name+"_B"));
		dataMap.put(name+"_C", map1.get(name+"_C"));
		if(map1.get(name+"_C")!=null){
			if(map1.get(name+"_C").equals("")){ 
				if((map1.get(name+"_A").toString()).equals((map1.get(name+"_B")).toString())){
					dataMap.put(name+"_STATUS",allRight);
				}else{
					if(map1.get(name+"").equals("1")){ 
						dataMap.put(name+"_STATUS",contractRight);
					}else{
						dataMap.put(name+"_STATUS",creditRight);
					}
				}
			}else{
				dataMap.put(name+"_STATUS",noRight);
			}
		}else{

		}

		return dataMap;
	}

	@SuppressWarnings("unchecked")
	public Map dealWithData2(String name, Map map1, Map dataMap){
		if(map1.get(name+"status").equals("1")){
			dataMap.put(name+"_STATUS",allRight);
		}else{
			if(map1.get(name+"").equals("1")){ 
				dataMap.put(name+"_STATUS",contractRight);
			}else{
				dataMap.put(name+"_STATUS",creditRight);
			}
		}
		return dataMap;
	}
	
	@SuppressWarnings("unchecked")
	public Map getUpdateContractMap(Map map,Map dataMap){
		dataMap.put("RECT_ID", map.get("RECT_ID"));
		dataMap=dealWithData3("LEASE_CODE",map,dataMap); 
		dataMap=dealWithData3("CUST_NAME",map,dataMap);
		dataMap=dealWithData3("CORP_ORAGNIZATION_CODE",map,dataMap);
		dataMap=dealWithData3("OPEN_BANK",map,dataMap);
		dataMap=dealWithData3("BANK_ACCOUNTS",map,dataMap);
		dataMap=dealWithData3("CUST_PHONE",map,dataMap);
		dataMap=dealWithData3("CUST_FAX",map,dataMap);
		dataMap=dealWithData3("CUST_LINKMAN",map,dataMap);
		dataMap=dealWithData3("CUST_ZIP",map,dataMap);
		//dataMap=dealWithData3("CUST_ADDRESS",map,dataMap);
		dataMap=dealWithData3("LEASE_TOPRIC",map,dataMap);
		dataMap=dealWithData3("LEASE_PERIOD",map,dataMap);
		dataMap=dealWithData3("LEASE_TERM",map,dataMap);
		dataMap=dealWithData3("PLEDGE_PRICE_RATE",map,dataMap);
		dataMap=dealWithData3("PLEDGE_PRICE",map,dataMap);
		dataMap=dealWithData3("MANAGEMENT_FEE_RATE",map,dataMap);
		dataMap=dealWithData3("MANAGEMENT_FEE",map,dataMap);
		dataMap=dealWithData3("BUSINESS_TRIP_PRICE",map,dataMap);
		dataMap=dealWithData3("FLOAT_RATE",map,dataMap);
		dataMap=dealWithData3("YEAR_INTEREST_TYPE",map,dataMap);
		dataMap=dealWithData3("YEAR_INTEREST",map,dataMap);
		dataMap=dealWithData3("FINE_TYPE",map,dataMap);
		dataMap=dealWithData3("FINE_RATE",map,dataMap);
		dataMap=dealWithData3("LEASE_RZE",map,dataMap);
		dataMap=dealWithData3("PAY_WAY",map,dataMap);
		dataMap=dealWithData3("START_DATE",map,dataMap);
		dataMap=dealWithData3("EQUPMENT_ADDRESS",map,dataMap);
		dataMap=dealWithData3("DEAL_WAY",map,dataMap);
		dataMap=dealWithData3("INSURANCE_COMPANY_ID",map,dataMap);
		dataMap=dealWithData3("BUY_INSURANCE_WAY",map,dataMap);
		dataMap=dealWithData3("BUY_INSURANCE_TIME",map,dataMap);
		dataMap=dealWithData3("INSURE_REBATE_RATE",map,dataMap); 
		dataMap=dealWithData3("PLEDGE_WAY",map,dataMap); 
		dataMap=dealWithData3("PLEDGE_PERIOD",map,dataMap); 
		dataMap=dealWithData3("SALES_TAX_RATE",map,dataMap); 
		dataMap=dealWithData3("INSURE_BASE_RATE",map,dataMap); 
		dataMap=dealWithData3("STAMP_TAX_TOPRIC",map,dataMap); 
		dataMap=dealWithData3("STAMP_TAX_MONTHPRIC",map,dataMap); 
		dataMap=dealWithData3("NATU_IDCARD",map,dataMap); 
		dataMap=dealWithData3("NATU_MATE_NAME",map,dataMap); 
		dataMap=dealWithData3("NATU_MATE_IDCARD",map,dataMap); 
		dataMap=dealWithData3("CORP_REGISTE_ADDRESS",map,dataMap);
		dataMap=dealWithData3("CORP_WORK_ADDRESS",map,dataMap);
		return dataMap;
	}
	
	@SuppressWarnings("unchecked")
	public Map dealWithData3(String name,Map map,Map dataMap){
		if(map.get(name+"_STATUS")!=null){
			if((map.get(name+"_STATUS").toString()).equals("0")||(map.get(name+"_STATUS").toString()).equals("1")){
				dataMap.put(name+"", map.get(name+"_A"));
			}else if((map.get(name+"_STATUS").toString()).equals("2")){
				dataMap.put(name+"", map.get(name+"_B"));
			}else{
				dataMap.put(name+"", map.get(name+"_C"));
			}
		}else{
			
		}
		return dataMap;
	}

	@Override
	protected void afterExecute(String action, Context context) {
		 if ("checkContract.saveDifference".equals(action)) {
			String flag=""+context.contextMap.get("flag");
			Long creditId = DataUtil.longUtil(context.contextMap.get("credit_id"));
			Long contractId = DataUtil.longUtil(context.contextMap.get("rect_id"));
			String logType = "融资租赁合同";
			String logTitle = "复核通过";
			if("2".equals(flag)){
				logTitle = "复核驳回";
			}
			String logCode = String.valueOf(context.contextMap.get("LEASE_CODE"));
			String memo = "融资租赁合同"+logTitle;
			int state = 1;
			Long userId = DataUtil.longUtil(context.contextMap.get("s_employeeId"));
			Long otherId = null;
			
			BusinessLog.addBusinessLog(creditId, contractId, logType, logTitle, logCode, memo, state, userId, otherId, (String)context.contextMap.get("IP"));
		}
	}
	
	
}
