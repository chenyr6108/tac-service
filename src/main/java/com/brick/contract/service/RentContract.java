package com.brick.contract.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.birt.chart.computation.LabelLimiter.Option;
import com.brick.base.command.BaseCommand;
import com.brick.base.service.BaseService;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.base.util.LeaseUtil;
import com.brick.baseManage.service.BusinessLog;
import com.brick.collection.service.StartPayService;
import com.brick.collection.support.PayRate;
import com.brick.collection.util.PaylistUtil;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.EmailPlanTO;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.contract.util.ConfirmationLetterPDF;
import com.brick.contract.util.PasswordControlTablePDF;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.core.DataAccessor.OPERATION_TYPE;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.Constants;
import com.brick.util.DataUtil;
import com.brick.util.DateUtil;
import com.brick.util.FileExcelUpload;
import com.brick.util.StringUtils;
import com.brick.util.web.HTMLUtil;
import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 合同模块的操作
 * @author wujw
 * @date May 4, 2010
 * @version
 */
public class RentContract extends BaseCommand {

	Log logger = LogFactory.getLog(RentContract.class);
	
	private MailUtilService mailUtilService;
	
	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}
	
	/**
	 * 管理页面查询
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryRentContract (Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		PagingInfo<Object> dw = null;

		Map rsMap = null;
		Map paramMap = new HashMap();
		paramMap.put("id", context.contextMap.get("s_employeeId"));
		Boolean editThingNum=false;
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				rsMap = (Map) DataAccessor.query("employee.getEmpInforById", paramMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("p_usernode", rsMap.get("NODE"));
				
				dw = baseService.queryForListWithPaging("rentContract.queryRentContract", context.contextMap, "WIND_RESULT_DATE", ORDER_TYPE.DESC);
				
				//Add by Michael 2012 5-7 增加维护机号权限
				
				List<String> resourceIdList=(List<String>) DataAccessor.query("supplier.getResourceIdListByEmplId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				for(int i=0;resourceIdList!=null&&i<resourceIdList.size();i++) {
					if("209".equals(resourceIdList.get(i))) {
						editThingNum=true;
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
		outputMap.put("QSELECT_STATUS", context.contextMap.get("QSELECT_STATUS"));
		outputMap.put("RENTSTAUTS", context.contextMap.get("RENTSTAUTS"));
		outputMap.put("RECP_STATUS", context.contextMap.get("RECP_STATUS"));
		outputMap.put("editThingNum", editThingNum);
		
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/rentcontract/queryRentContract.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}
	
	/**
	 * 管理页面查询（合同浏览）
	 * @param context
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public void queryRentContractForShow (Context context) throws Exception {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		PagingInfo<Object> dw = null;

		Map rsMap = null;
		Map paramMap = new HashMap();
		paramMap.put("id", context.contextMap.get("s_employeeId"));
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				rsMap = (Map) DataAccessor.query("employee.getEmpInforById", paramMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("p_usernode", rsMap.get("NODE"));
				
				dw = baseService.queryForListWithPaging("rentContract.queryRentContract", context.contextMap, "WIND_RESULT_DATE", ORDER_TYPE.DESC);
				
				if("Y".equals(context.contextMap.get("isSalesDesk"))) {//业务人员综合界面进入合同浏览需要加入设备请款link
					List<Map<String,Object>> result=(List<Map<String,Object>>)DataAccessor.query("creditReportManage.getIsCanApplyMoney",context.contextMap,RS_TYPE.LIST);
					List<Map<String,Object>> result2=(List<Map<String,Object>>)DataAccessor.query("creditReportManage.getIsValidDay",context.contextMap,RS_TYPE.LIST);
					for(int i=0;dw!=null&&i<dw.getResultList().size();i++) {
						for(int j=0;result!=null&&j<result.size();j++) {
							if("Y".equals(((Map<String,Object>)(dw.getResultList().get(i))).get("FLAG"))&&
								String.valueOf(((Map<String,Object>)(dw.getResultList().get(i))).get("PRCD_ID")).equals(String.valueOf(result.get(j).get("CREDIT_ID")))) {
								((Map<String,Object>)(dw.getResultList().get(i))).put("FLAG",result.get(j).get("FLAG"));
								break;
							}
						}
						
						for(int j=0;result2!=null&&j<result2.size();j++) {
							if("Y".equals(((Map<String,Object>)(dw.getResultList().get(i))).get("FLAG"))&&
									String.valueOf(((Map<String,Object>)(dw.getResultList().get(i))).get("PRCD_ID")).equals(String.valueOf(result2.get(j).get("CREDIT_ID")))) {
								((Map<String,Object>)(dw.getResultList().get(i))).put("FLAG",result2.get(j).get("FLAG"));
								break;
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		boolean uploadRentFile = this.baseService.checkAccessForResource("uploadrentfile", String.valueOf(context.contextMap.get("s_employeeId")));

		outputMap.put("uploadRentFile", uploadRentFile);
		/*-------- output --------*/
		outputMap.put("dw", dw);
		outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
		outputMap.put("QSTART_DATE", context.contextMap.get("QSTART_DATE"));
		outputMap.put("QEND_DATE", context.contextMap.get("QEND_DATE"));
		outputMap.put("QSELECT_STATUS", context.contextMap.get("QSELECT_STATUS"));
		outputMap.put("RENTSTAUTS", context.contextMap.get("RENTSTAUTS"));
		outputMap.put("RECP_STATUS", context.contextMap.get("RECP_STATUS"));
		outputMap.put("isSalesDesk", context.contextMap.get("isSalesDesk"));
		outputMap.put("companyCode", context.contextMap.get("companyCode"));
		outputMap.put("CAR_NUMBER", context.contextMap.get("CAR_NUMBER"));
		
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/rentcontract/queryRentContractForShow.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}
	
	/**
	 * 管理页面查询
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryRentContractByPlay(Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		PagingInfo<Object> dw = null;

		Map rsMap = null;
		Map paramMap = new HashMap();
		paramMap.put("id", context.contextMap.get("s_employeeId"));
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				rsMap = (Map) DataAccessor.query("employee.getEmpInforById", paramMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("p_usernode", rsMap.get("NODE"));
				
				dw = (PagingInfo<Object>) baseService.queryForListWithPaging("rentContract.queryRentContractByPlay", context.contextMap, "PRCD_ID", ORDER_TYPE.DESC);
						//DataAccessor.query("rentContract.queryRentContractByPlay", context.contextMap, DataAccessor.RS_TYPE.PAGED);
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
		outputMap.put("QSELECT_STATUS", context.contextMap.get("QSELECT_STATUS"));
		outputMap.put("RENTSTAUTS", context.contextMap.get("RENTSTAUTS"));
		outputMap.put("search_thing_number", context.contextMap.get("search_thing_number"));

		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/lockManagement/lockManageByCreate.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}
	
	
	/**
	 * 进入初审页面，准备数据
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void initCreate(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		Map cust = null;
		Map company = null;
		Map schema = null;
		List<Map> typeList = null;
		List<Map> kindList = null;
		List<Map> productList = null;
		List<Map> supplierList = null;
		List<Map> equipmentList = null;
		List<Map> insureTypeList = null;
		List<Map> insureList = null;
		List<Map> otherFeeList = null;
		
		List<Map> insureCompanyList = null;
		List<Map> companyList = null;
		List<Map> provinces = null;
		List<Map> citys = null;
		List<Map> contractType = null;
		List<Map> payWays = null;
		List<Map> dealWays = null;
		List lockList = null;
		List<Map> area = null;
		List<Map> schemaIrrList = null;
//		List numberList = null ;
		Map decpname=null;
		List suplList=null;
		List manufacturer = null ;
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {
				outputMap.put("PRCD_ID", context.contextMap.get("PRCD_ID"));
				//System.out.println(context.contextMap.get("s_employeeId"));				
				cust = (Map) DataAccessor.query("rentContract.readCustByPrcdId", context.contextMap, DataAccessor.RS_TYPE.MAP);	    
				outputMap.put("cust", cust);

				//
				company = (Map) DataAccessor.query("companyManage.readCompanyAliasByUserId", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("company", company);
				//System.out.println(company.get("DECP_NAME_CN")+"*****&&&&&&&&&&&&%%%%%%%%");
				//
				schema = (Map) DataAccessor.query("rentContract.readCreditSchemaByPrcdId", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("schema", schema);
				
				if(schema!=null&&"5".equals(schema.get("TAX_PLAN_CODE"))) {
					Map param=new HashMap();
					param.put("credit_id",context.contextMap.get("PRCD_ID"));
					Map resultMap=(Map)DataAccessor.query("creditReportManage.selectCreditScheme", param, DataAccessor.RS_TYPE.MAP);
					outputMap.put("SALES_PAY",resultMap.get("SALES_PAY"));
					outputMap.put("INCOME_PAY",resultMap.get("INCOME_PAY"));
					outputMap.put("OUT_PAY",resultMap.get("OUT_PAY"));
				}
				if(schema!=null&&Constants.TAX_PLAN_CODE_7.equals(schema.get("TAX_PLAN_CODE"))) {
					Map param=new HashMap();
					param.put("credit_id",context.contextMap.get("PRCD_ID"));
					Map resultMap=(Map)DataAccessor.query("creditReportManage.selectCreditScheme", param, DataAccessor.RS_TYPE.MAP);
					outputMap.put("SALES_PAY",resultMap.get("SALES_PAY"));
				}
				//
				schemaIrrList = (List<Map>) DataAccessor.query("creditReportManage.readCreditSchemaIrr_Rent", context.contextMap, DataAccessor.RS_TYPE.LIST); 
				outputMap.put("schemaIrrList", schemaIrrList);
				//	
				//Add by Michael 2012 09-21 增加税费测算方案
				outputMap.put("taxPlanList", DictionaryUtil.getDictionary("税费方案"));
				
				context.contextMap.put("credit_id", context.contextMap.get("PRCD_ID"));
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
				
				typeList = (List<Map>) DataAccessor.query("suplEquipment.getAllType", context.contextMap, DataAccessor.RS_TYPE.LIST);
				manufacturer = (List) DataAccessor.query("suplEquipment.getAllManufacturer", context.contextMap, DataAccessor.RS_TYPE.LIST);				
				outputMap.put("typeList", typeList);
				outputMap.put("manufacturer", manufacturer);
				outputMap.put("typeJsonList", Output.serializer.serialize(typeList));
				
				//Modify by Michael 2012 5-4 将所有设备都列出来
				//equipmentList = (List<Map>) DataAccessor.query("rentContract.readCreditEquipmentByPrcdId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				equipmentList = (List<Map>) DataAccessor.query("rentContract.readCreditAllEquipmentByPrcdId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				
				for (Map map : equipmentList) {
					
					Map temMap = new HashMap();
					temMap.put("type_id", map.get("TYPE_ID"));
					temMap.put("kind_id", map.get("KIND_ID"));
					temMap.put("product_id", map.get("PRODUCT_ID"));

					kindList = (List<Map>) DataAccessor.query("suplEquipment.getAllKind", temMap, DataAccessor.RS_TYPE.LIST); 
					map.put("kindList", kindList);
					productList = (List<Map>) DataAccessor.query("suplEquipment.getAllProducts", temMap, DataAccessor.RS_TYPE.LIST);
					map.put("productList", productList);
					supplierList = (List<Map>) DataAccessor.query("suplEquipment.getAllSuppliers", temMap, DataAccessor.RS_TYPE.LIST);
					map.put("supplierList", supplierList);
//					机号不需要再查询
//					numberList = (List) DataAccessor.query("suplEquipment.getAllPRDCNumber", temMap, DataAccessor.RS_TYPE.LIST);
//					map.put("numberList", numberList);
				}
				outputMap.put("equipmentList", equipmentList);

				insureTypeList = (List<Map>) DataAccessor.query("insureType.queryInsureTypeList", null, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureTypeList", insureTypeList);
				outputMap.put("insureTypeJsonList", Output.serializer.serialize(insureTypeList));
				
				insureList = (List<Map>) DataAccessor.query("rentContract.readCreditInsureByPrcdId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureList", insureList);

				otherFeeList = (List<Map>) DataAccessor.query("rentContract.readCreditOtherFeeByPrcdId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("otherFeeList", otherFeeList);
				
				provinces = (List<Map>) DataAccessor.query( "area.getProvinces", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("provinces", provinces);
				context.contextMap.put("provinceId", cust.get("PROVINCE_ID"));
				citys = (List<Map>) DataAccessor.query("area.getCitysByProvinceId", context.contextMap,DataAccessor.RS_TYPE.LIST);
				outputMap.put("citys", citys);
				context.contextMap.put("cityId", cust.get("CITY_ID"));
				area = (List<Map>) DataAccessor.query("area.getAreaByCityId", context.contextMap,DataAccessor.RS_TYPE.LIST);
				outputMap.put("area", area);
				
				insureCompanyList = (List<Map>) DataAccessor.query("insuCompany.queryInsureCompanyListForSelect", null, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureCompanyList", insureCompanyList);
				// 
				companyList = (List) DataAccessor.query("companyManage.queryCompanyAlias", null, DataAccessor.RS_TYPE.LIST);
				outputMap.put("companyList", companyList);
				
				Map baseRate = PayRate.getBaseRate();
				outputMap.put("baseRateJson", Output.serializer.serialize(baseRate));
				
				
				Map dataDictionaryMap = new HashMap();
				
				dataDictionaryMap.put("dataType", "融资租赁合同类型");
				contractType = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("contractType", contractType);
				
				dataDictionaryMap.put("dataType", "支付方式");
				payWays = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("payWays", payWays);
				
				dataDictionaryMap.put("dataType", "租赁期满处理方式");
				dealWays = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("dealWays", dealWays);
				
				dataDictionaryMap.put("dataType", "锁码方式");
				lockList = (List) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("lockList", lockList);
				decpname = (Map) DataAccessor.query("rentContractPact.getchuzuren", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("decpname", decpname);
				
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
			Output.jspOutput(outputMap, context, "/rentcontract/createRentContract.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
	
	}
	
	/**
	 * 合同初审 录入
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void createRentContract(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		Long rect_id = null;
		Long recs_id = null;
		
		Integer custType = HTMLUtil.getIntParam(context.request, "CUST_TYPE", -1);

		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {
				DataAccessor.getSession().startTransaction();
				
				// 0自然人 1法人
			
				if (custType.equals(0)) {
					context.contextMap.put("CORP_ORAGNIZATION_CODE", "");
					context.contextMap.put("OPEN_BANK", "");
					context.contextMap.put("BANK_ACCOUNTS", "");
				} else if (custType.equals(1)) {
					context.contextMap.put("NATU_IDCARD", "");
					context.contextMap.put("NATU_MATE_NAME", "");
					context.contextMap.put("NATU_MATE_IDCARD", "");
				} else {
					errList.add("承租人类型非法");
					throw new Exception();
				}
				
				rect_id = (Long) DataAccessor.getSession().insert("rentContract.createRentContract", context.contextMap);
				context.contextMap.put("RECT_ID", rect_id);

				PaylistUtil.setBaseRate(context.contextMap);
				recs_id = (Long) DataAccessor.getSession().insert("rentContract.createRentContractSchema", context.contextMap);
				context.contextMap.put("RECS_ID", recs_id);

				operateEquipment(context);

				//operateInsure(context);
				
				operateOtherFee(context);
				
				operateRemark(context);
				
				operateContractIrr(context);
				
				//Add by Michael 2012 01/17 增加管理费收入
				operateManagerFee(context);
				
				/*
				//将循环授信的客户和供应商的授信余额减少
				context.contextMap.put("credit_id", context.contextMap.get("PRCD_ID"));
				List grantPriceList = (List) DataAccessor.query("creditReportManage.getLastGrantPrice", context.contextMap,DataAccessor.RS_TYPE.LIST);
				List<Map> equipmentList = (List<Map>) DataAccessor.query("rentContract.readCreditEquipmentByPrcdId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				for(int k=0;k<equipmentList.size();k++){
					HashMap equipmentMap=(HashMap)equipmentList.get(k);
					double baifenbi=0.0;
					baifenbi=(Double.parseDouble(equipmentMap.get("UNIT_PRICE").toString())*Integer.parseInt(equipmentMap.get("AMOUNT").toString()))/Double.parseDouble(((HashMap)grantPriceList.get(0)).get("LEASE_TOPRIC").toString());
					if(grantPriceList.size()>0){
						//for(int i=0;i<grantPriceList.size();i++){
						if((HashMap)grantPriceList.get(k)!=null){
							HashMap grantPrice=(HashMap)grantPriceList.get(k);
							//double first_own_price=Double.parseDouble(grantPrice.get("FIRST_OWN_PRICE")==null?"0":grantPrice.get("FIRST_OWN_PRICE").toString());
							double first_own_price=Double.parseDouble(grantPrice.get("LEASE_RZE")==null?"0":grantPrice.get("LEASE_RZE").toString());
							if(grantPrice!=null&&k==0){
								if(grantPrice.get("SUPL_TRUE")!=null&&Integer.parseInt(grantPrice.get("SUPL_TRUE").toString())!=4){
									//if(Integer.parseInt(grantPrice.get("CUST_REPEAT_CREDIT").toString())==1){							
											if(grantPrice.get("CUST_LASTPRICE")!=null&&(Double.parseDouble(grantPrice.get("CUST_LASTPRICE").toString())-first_own_price)>=0){
												HashMap decreaseCustMap=new HashMap();
												decreaseCustMap.put("LAST_PRICE", Double.parseDouble(grantPrice.get("CUST_LASTPRICE").toString())-first_own_price);
												decreaseCustMap.put("CUGP_ID", grantPrice.get("CUGP_ID"));										
												DataAccessor.getSession().update("creditReportManage.decreaseCustGrantLastprice",decreaseCustMap);
												decreaseCustMap.put("GRANT_PRICE",grantPrice.get("GRANT_PRICE"));
												decreaseCustMap.put("MEMO","客户对应的合同经过初审，授信余额要减少，减少的概算成本为"+first_own_price);
												decreaseCustMap.put("s_employeeId", context.request.getSession().getAttribute("s_employeeId"));
												DataAccessor.getSession().insert("creditReportManage.insertCustGrantLastpriceLog",decreaseCustMap);
											}
									//}
								}
							}
							for(int i=0;i<grantPriceList.size();i++){
								HashMap grantPriceMap=(HashMap)grantPriceList.get(i);
								if(grantPriceMap!=null){
									//System.out.println(grantPriceMap.get("SUPPER_ID").toString()+"========"+equipmentMap.get("SUPPLIER_ID").toString());
									if(grantPriceMap.get("SUPPER_ID").toString().equals(equipmentMap.get("SUPPLIER_ID").toString())){
										if(grantPriceMap.get("SUPL_TRUE")!=null&&Integer.parseInt(grantPriceMap.get("SUPL_TRUE").toString())!=4){
											//if(grantPrice.get("REAL_LAST_PRICE")!=null&&Double.parseDouble(grantPrice.get("REAL_LAST_PRICE").toString())-first_own_price*baifenbi>=0){
											if(grantPriceMap.get("REAL_LAST_PRICE")!=null){
												HashMap decreaseSupperMap=new HashMap();
												decreaseSupperMap.put("LAST_PRICE", Double.parseDouble(grantPriceMap.get("REAL_LAST_PRICE").toString())-first_own_price*baifenbi);
												//System.out.println(baifenbi+"=========="+first_own_price+"&&&&&&&&&&&&&"+baifenbi*first_own_price+"----------------");
												decreaseSupperMap.put("PDGP_ID", grantPriceMap.get("PDGP_ID"));
												DataAccessor.getSession().update("creditReportManage.decreaseSupperGrantLastprice",decreaseSupperMap);
												//decreaseSupperMap.put("LAST_PRICE", Double.parseDouble(grantPrice.get("REAL_LAST_PRICE").toString())-first_own_price*baifenbi);
												decreaseSupperMap.put("GRANT_PRICE",grantPriceMap.get("GRANT_PRICE"));
												decreaseSupperMap.put("MEMO","供应商"+grantPriceMap.get("SUPPERNAME")==null?"":grantPriceMap.get("SUPPERNAME")+"对应的合同经过初审，授信余额要减少，原来的授授信余额是"+grantPriceMap.get("REAL_LAST_PRICE")==null?"":grantPriceMap.get("REAL_LAST_PRICE")+"减少比例为"+baifenbi+"减少的概算成本为"+first_own_price+"减少的额度为"+first_own_price*baifenbi);
												decreaseSupperMap.put("s_employeeId",context.request.getSession().getAttribute("s_employeeId"));
												DataAccessor.getSession().insert("creditReportManage.insertSupperGrantLastpriceLog",decreaseSupperMap);
											}
										}
									}
								}
							}
						}
					}
				}
				*/
				
				/*	if(grantPrice.get("SUPL_TRUE")!=null&&Integer.parseInt(grantPrice.get("SUPL_TRUE").toString())!=4){
						//if(Integer.parseInt(grantPrice.get("SUPPER_REPEAT_CREDIT").toString())==1){
							//System.out.println(Double.parseDouble(grantPrice.get("REAL_LAST_PRICE").toString())-Double.parseDouble(grantPrice.get("LEASE_TOPRIC").toString())+"================");
							if(grantPrice.get("REAL_LAST_PRICE")!=null&&Double.parseDouble(grantPrice.get("REAL_LAST_PRICE").toString())-Double.parseDouble(grantPrice.get("LEASE_TOPRIC").toString())>=0){
								HashMap decreaseSupperMap=new HashMap();
								decreaseSupperMap.put("LAST_PRICE", Double.parseDouble(grantPrice.get("SUPPER_LASTPRICE").toString())-Double.parseDouble(grantPrice.get("LEASE_TOPRIC").toString()));
								decreaseSupperMap.put("PDGP_ID", grantPrice.get("PDGP_ID"));
								DataAccessor.getSession().update("creditReportManage.decreaseSupperGrantLastprice",decreaseSupperMap);
							}
					}					
				}
				*/
				/*
				//将联合授信的供应商的授信余额减少(对应的供应商只有联合授信)
				List grantUnionPrice = (List) DataAccessor.query("creditReportManage.getLastUnionGrantPrice", context.contextMap,DataAccessor.RS_TYPE.LIST);
				if(grantUnionPrice.size()>0&&grantPrice.get("REAL_LAST_PRICE")==null){
					for(int j=0;j<grantUnionPrice.size();j++){
						HashMap grantUnionPriceMap=(HashMap)grantUnionPrice.get(j);
						if(grantUnionPriceMap.get("SUPL_TRUE")!=null&&Integer.parseInt(grantUnionPriceMap.get("SUPL_TRUE").toString())!=4){
							//if(Integer.parseInt(grantUnionPriceMap.get("SUPPER_REPEAT_CREDIT").toString())==1){
								HashMap decreaseSupperMap=new HashMap();
								decreaseSupperMap.put("UNION_GRANT_PRICE", Double.parseDouble(grantUnionPriceMap.get("SUPPER_LASTPRICE").toString())-Double.parseDouble(grantUnionPriceMap.get("LEASE_TOPRIC").toString()));
								decreaseSupperMap.put("PURP_ID", grantUnionPriceMap.get("PURP_ID"));
								//decreaseSupperMap.put("LAST_PRICE", Double.parseDouble(grantUnionPriceMap.get("SUPPER_LASTPRICE").toString())-Double.parseDouble(grantUnionPriceMap.get("LEASE_TOPRIC").toString()));
								//decreaseSupperMap.put("PURP_ID", grantUnionPriceMap.get("PURP_ID"));
								DataAccessor.getSession().update("creditReportManage.decreaseSupperOnlyUnoinGrantprice",decreaseSupperMap);
							//}
						}
					}
				}
				*/
				/*
				//将报告对应的所有的循环授信的担保人的授信余额减少
				List grantVouchList = (List) DataAccessor.query("creditReportManage.getVouchLastGrantPrice", context.contextMap,DataAccessor.RS_TYPE.LIST);
				if(grantVouchList.size()>0){
					for(int i=0;i<grantVouchList.size();i++){
						HashMap grantVouchMap = new HashMap();
						grantVouchMap = (HashMap)grantVouchList.get(i);
						//double first_own_price=Double.parseDouble(grantVouchMap.get("FIRST_OWN_PRICE")==null?"0":grantVouchMap.get("FIRST_OWN_PRICE").toString());
						double first_own_price=Double.parseDouble(grantVouchMap.get("LEASE_RZE")==null?"0":grantVouchMap.get("LEASE_RZE").toString());
						if(grantVouchMap.get("SUPL_TRUE")!=null&&Integer.parseInt(grantVouchMap.get("SUPL_TRUE").toString())!=4){
							if(grantVouchMap.get("TYPE")!=null&&grantVouchMap.get("TYPE").toString().equals("0")){
								//grantVouchMap.put("PRCD_ID", context.contextMap.get("PRCD_ID"));
							
								if(grantVouchMap.get("VOUCH_LASTPRICE")!=null){
									grantVouchMap.put("LAST_PRICE", Double.parseDouble(grantVouchMap.get("VOUCH_LASTPRICE").toString())-first_own_price);
									grantVouchMap.put("PRODUCT_ID", grantVouchMap.get("PRODUCT_ID"));
									grantVouchMap.put("VOUCH_NAME", grantVouchMap.get("VOUCH_NAME"));
									grantVouchMap.put("VOUCH_CODE", grantVouchMap.get("VOUCH_CODE"));
									//System.out.println("自然人担保人授信余额减少");
									DataAccessor.getSession().update("creditReportManage.decreaseVouchNatuGrantLastprice",grantVouchMap);
									//grantVouchMap.put("GRANT_PRICE",grantVouchMap.get("GRANT_PRICE"));
									grantVouchMap.put("MEMO","自然人担保人对应的合同经过初审，授信余额要减少，减少的概算成本为"+first_own_price);
									grantVouchMap.put("s_employeeId", context.request.getSession().getAttribute("s_employeeId"));
									DataAccessor.getSession().insert("creditReportManage.insertVouchGrantLastpriceLog",grantVouchMap);
								}
							}
							if(grantVouchMap.get("TYPE")!=null&&grantVouchMap.get("TYPE").toString().equals("1")){
								//grantVouchMap.put("PRCD_ID", context.contextMap.get("PRCD_ID"));
								if(grantVouchMap.get("VOUCH_LASTPRICE")!=null){
									grantVouchMap.put("LAST_PRICE", Double.parseDouble(grantVouchMap.get("VOUCH_LASTPRICE").toString())-first_own_price);
									grantVouchMap.put("PRODUCT_ID", grantVouchMap.get("PRODUCT_ID"));
									grantVouchMap.put("VOUCH_NAME", grantVouchMap.get("VOUCH_NAME"));
									grantVouchMap.put("VOUCH_CODE", grantVouchMap.get("VOUCH_CODE"));
									//System.out.println("法人担保人授信余额减少");
									DataAccessor.getSession().update("creditReportManage.decreaseVouchCropGrantLastprice",grantVouchMap);
									grantVouchMap.put("MEMO","法人担保人对应的合同经过初审，授信余额要减少，减少的实际本金为"+first_own_price);
									grantVouchMap.put("s_employeeId", context.request.getSession().getAttribute("s_employeeId"));
									DataAccessor.getSession().insert("creditReportManage.insertVouchGrantLastpriceLog",grantVouchMap);
								}
							}
						}
					}
				}
				*/
				

				DataAccessor.getSession().commitTransaction();
				
			} catch (Exception e) {
				logger.info("--------  合同初审 录入错误");
				e.printStackTrace();
				errList.add("合同初审 录入错误");
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} finally {
				try {
					DataAccessor.getSession().endTransaction();
				} catch (SQLException e) {
					errList.add("合同初审 录入 关闭事物错误");
					logger.info("--------  合同初审 录入 关闭事物错误");
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
			}
			
			if (errList.isEmpty()) {
				try{
			//根据合同查报告Id
			Map creditByrectId=(Map)DataAccessor.query("rentContract.readRentContractByRectId", context.contextMap, DataAccessor.RS_TYPE.MAP);
			context.contextMap.put("credit_id", creditByrectId.get("PRCD_ID"));
			//修改主档
				//查询出该合同的客户经理
				Map creditById=(Map)DataAccessor.query("creditReportManage.selectSensor_IdById", context.contextMap, DataAccessor.RS_TYPE.MAP);
				
				//查询出在数据字典中的案件狀況細項下的提案的主键
				String prvLog="核准状态";
				String prvLogSun="合同已制作";
				context.contextMap.put("logFlag", prvLog);
				context.contextMap.put("logName", prvLogSun);
				Map logTypeMap=(Map)DataAccessor.query("activitiesLog.logActlog_idNotStatus", context.contextMap, DataAccessor.RS_TYPE.MAP);
				
				
				//有这个客户经理建立的主档
				context.contextMap.put("sensoridBycredit", creditById.get("SENSOR_ID"));
				context.contextMap.put("custidBycredit", creditById.get("CUST_ID"));
				Map logMaps=(Map)DataAccessor.query("activitiesLog.logFirstByCreditId", context.contextMap, DataAccessor.RS_TYPE.MAP);
				if(logMaps!=null)
				{
					if(logMaps.size()>0)
					{
						Map entityMap=new HashMap();
						entityMap.put("id", context.request.getAttribute("s_employeeId"));
						entityMap.put("casesun", prvLogSun);
						entityMap.put("actilog", logMaps.get("ACTILOG_ID"));
						entityMap.put("credit_id", context.contextMap.get("credit_id"));
						DataAccessor.execute("activitiesLog.updateCaseStateBycredit_id",entityMap, DataAccessor.OPERATION_TYPE.UPDATE);
						
						entityMap.put("caseFather", logTypeMap.get("DATA_ID"));
						entityMap.put("casesunId", logTypeMap.get("ACTLOG_ID"));
						DataAccessor.execute("activitiesLog.createLogByOther",entityMap, DataAccessor.OPERATION_TYPE.INSERT);
					}
				}
				else
				{
					Map logMap=(Map)DataAccessor.query("activitiesLog.logFirst", context.contextMap, DataAccessor.RS_TYPE.MAP);
					if(logMap!=null)
					{
						if(logMap.size()>0)
						{
							Map entityMap=new HashMap();
							entityMap.put("id", context.request.getAttribute("s_employeeId"));
							entityMap.put("casesun", prvLogSun);
							entityMap.put("actilog", logMap.get("ACTILOG_ID"));
							entityMap.put("credit_id", context.contextMap.get("credit_id"));
							DataAccessor.execute("activitiesLog.updateCaseStateBycredit_id",entityMap, DataAccessor.OPERATION_TYPE.UPDATE);
							
							entityMap.put("caseFather", logTypeMap.get("DATA_ID"));
							entityMap.put("casesunId", logTypeMap.get("ACTLOG_ID"));
							DataAccessor.execute("activitiesLog.createLogByOther",entityMap, DataAccessor.OPERATION_TYPE.INSERT);
						}
					}
				}
				
				}
				catch (Exception e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
			}
			
			if (errList.isEmpty()) {
				// Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=rentContract.queryRentContract");
				Output.jspSendRedirect(context, "defaultDispatcher?__action=rentContract.queryRentContract");
			} else {
				outputMap.put("errList", errList);
				Output.jspOutput(outputMap, context, "/error.jsp");
			}
			
		}
		
	
	}
	
	/**
	 * 初始化修改
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void initUpdate(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		Map rentContract = null;
		Map schema = null;
		
		List<Map> typeList = null;
		List<Map> kindList = null;
		List<Map> productList = null;
		List<Map> supplierList = null;
//		不需要机号
//		List<Map> numberList = null;
		List<Map> equipmentList = null;
		List<Map> insureTypeList = null;
		List<Map> insureList = null;
		List<Map> otherFeeList = null;
		List<Map> remarkList = null;
		
		List<Map> insureCompanyList = null;
		List<Map> companyList = null;
		List<Map> provinces = null;
		List<Map> citys = null;
		List<Map> contractType = null;
		List<Map> payWays = null;
		List<Map> dealWays = null;
		List lockList = null;
		List<Map> area = null;
		List<Map> schemaIrrList = null;
		List suplList=null;
		List manufacturer = null ;
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {
				outputMap.put("RECT_ID", context.contextMap.get("RECT_ID"));
				//
				rentContract = (Map) DataAccessor.query("rentContract.readRentContractByRectId", context.contextMap, DataAccessor.RS_TYPE.MAP);
				//用于查询机号是否重复
				outputMap.put("PRCD_ID", rentContract.get("PRCD_ID")) ;
				outputMap.put("rentContract", rentContract);
				//
				schema = (Map) DataAccessor.query("rentContract.readSchemaByRectId", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("schema", schema);
				//
				schemaIrrList = (List<Map>) DataAccessor.query("rentContract.readSchemaIrrByRecsId", schema, DataAccessor.RS_TYPE.LIST); 
				outputMap.put("schemaIrrList", schemaIrrList);
				//
				typeList = (List<Map>) DataAccessor.query("suplEquipment.getAllType", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("typeList", typeList);
				outputMap.put("typeJsonList", Output.serializer.serialize(typeList));
				//
				equipmentList = (List<Map>) DataAccessor.query("rentContract.readEquipmentByRectId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				//
				manufacturer = (List) DataAccessor.query("suplEquipment.getAllManufacturer", context.contextMap, DataAccessor.RS_TYPE.LIST);	
				outputMap.put("manufacturer", manufacturer) ;
				for (Map map : equipmentList) {
					
					Map temMap = new HashMap();
					temMap.put("type_id", map.get("TYPE_ID"));
					temMap.put("kind_id", map.get("KIND_ID"));
					temMap.put("product_id", map.get("PRODUCT_ID"));

					kindList = (List) DataAccessor.query("suplEquipment.getAllKind", temMap, DataAccessor.RS_TYPE.LIST); 
					map.put("kindList", kindList);
					productList = (List) DataAccessor.query("suplEquipment.getAllProducts", temMap, DataAccessor.RS_TYPE.LIST);
					map.put("productList", productList);
					supplierList = (List) DataAccessor.query("suplEquipment.getAllSuppliers", temMap, DataAccessor.RS_TYPE.LIST);
					map.put("supplierList", supplierList);
//					numberList = (List) DataAccessor.query("suplEquipment.getAllPRDCNumber", temMap, DataAccessor.RS_TYPE.LIST);
//					map.put("numberList", numberList);
					
				}
				outputMap.put("equipmentList", equipmentList);
				//
				insureTypeList = (List<Map>) DataAccessor.query("insureType.queryInsureTypeList", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureTypeList", insureTypeList);
				outputMap.put("insureTypeJsonList", Output.serializer.serialize(insureTypeList));
				insureList = (List<Map>) DataAccessor.query("rentContract.readInsureByRectId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureList", insureList);

				otherFeeList = (List<Map>) DataAccessor.query("rentContract.readOtherFeeByRectId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("otherFeeList", otherFeeList);
				
				remarkList = (List<Map>) DataAccessor.query("rentContract.readContractRemark", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("remarkList", remarkList);
				
				//
				provinces = (List<Map>) DataAccessor.query( "area.getProvinces", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("provinces", provinces);
				context.contextMap.put("provinceId", rentContract.get("PROVINCE_ID"));
				citys = (List<Map>) DataAccessor.query("area.getCitysByProvinceId", context.contextMap,DataAccessor.RS_TYPE.LIST);
				outputMap.put("citys", citys);
				context.contextMap.put("cityId", rentContract.get("CITY_ID"));
				area = (List<Map>) DataAccessor.query("area.getAreaByCityId", context.contextMap,DataAccessor.RS_TYPE.LIST);
				outputMap.put("area", area);
				//
				insureCompanyList = (List<Map>) DataAccessor.query("insuCompany.queryInsureCompanyListForSelect", null, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureCompanyList", insureCompanyList);
				//
				Map baseRate = PayRate.getBaseRate();
				outputMap.put("baseRateJson", Output.serializer.serialize(baseRate));
				//
				companyList = (List) DataAccessor.query("companyManage.queryCompanyAlias", null, DataAccessor.RS_TYPE.LIST);
				outputMap.put("companyList", companyList);
				//
				Map dataDictionaryMap = new HashMap();
				
				dataDictionaryMap.put("dataType", "融资租赁合同类型");
				contractType = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("contractType", contractType);
				
				dataDictionaryMap.put("dataType", "支付方式");
				payWays = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("payWays", payWays);
				
				dataDictionaryMap.put("dataType", "租赁期满处理方式");
				dealWays = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("dealWays", dealWays);
			
				dataDictionaryMap.put("dataType", "锁码方式");
				lockList = (List) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("lockList", lockList);
				
				suplList=(List)DictionaryUtil.getDictionary("供应商保证");
				outputMap.put("suplList", suplList);
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		
		}
		
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/rentcontract/updateRentContract.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
	
	}
	
	/**
	 * 合同修改
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void updateRentContract(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		Integer custType = HTMLUtil.getIntParam(context.request, "CUST_TYPE", -1);

		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {
				DataAccessor.getSession().startTransaction();
				
				// 0自然人 1法人
				if (custType.equals(0)) {
					context.contextMap.put("CORP_ORAGNIZATION_CODE", "");
					context.contextMap.put("OPEN_BANK", "");
					context.contextMap.put("BANK_ACCOUNTS", "");
				} else if (custType.equals(1)) {
					context.contextMap.put("NATU_IDCARD", "");
					context.contextMap.put("NATU_MATE_NAME", "");
					context.contextMap.put("NATU_MATE_IDCARD", "");
				} else {
					errList.add("承租人类型非法");
					throw new Exception();
				}
				//
				DataAccessor.getSession().update("rentContract.updateRentContract", context.contextMap);
				//
				PaylistUtil.setBaseRate(context.contextMap);
				DataAccessor.getSession().update("rentContract.updateRentContractSchema", context.contextMap);
				
				//
				deleteContractIrr(context);
				operateContractIrr(context);
				//
				deleteEquipment(context);
				operateEquipment(context);

				//
				deleteInsure(context);
				operateInsure(context);
				
				//
				deleteOtherFee(context);
				operateOtherFee(context);
				
				DataAccessor.getSession().commitTransaction();
				
			} catch (Exception e) {
				e.printStackTrace();
				errList.add("合同修改错误");
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} finally {
				try {
					DataAccessor.getSession().endTransaction();
				} catch (SQLException e) {
					errList.add("合同修改关闭事物错误");
					logger.info("合同修改关闭事物错误");
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
			}
			
			if (errList.isEmpty()) {
				Output.jspSendRedirect(context, "defaultDispatcher?__action=rentContract.queryRentContract");
			} else {
				outputMap.put("errList", errList);
				Output.jspOutput(outputMap, context, "/error.jsp");
			}
			
		}
		
	
	}
	
	/**
	 * show rent contract
	 * @param context
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public void showRentContract(Context context)  throws SQLException {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		Map rentContract = null;
		Map schema = null;
		
		List<Map> equipmentList = null;
		List<Map> insureTypeList = null;
		List<Map> insureList = null;
		List<Map> otherFeeList = null;
		List<Map> remarkList = null;
		
		List<Map> insureCompanyList = null;
		List<Map> companyList = null;
		List<Map> provinces = null;
		List<Map> citys = null;
		List<Map> contractType = null;
		List<Map> payWays = null;
		List<Map> dealWays = null;
		List<Map> schemaIrrList = null;
		List suplList=null;
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {
				outputMap.put("RECT_ID", context.contextMap.get("RECT_ID"));
				//
				rentContract = (Map) DataAccessor.query("rentContract.readRentContractByRectId", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("rentContract", rentContract);
				//
				schema = (Map) DataAccessor.query("rentContract.readSchemaByRectId", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("schema", schema);
				schemaIrrList = (List<Map>) DataAccessor.query("rentContract.readSchemaIrrByRecsId", schema, DataAccessor.RS_TYPE.LIST); 
				outputMap.put("schemaIrrList", schemaIrrList);
				//
				equipmentList = (List<Map>) DataAccessor.query("rentContract.readEquipmentByRectId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("equipmentList", equipmentList);
				//
				insureTypeList = (List<Map>) DataAccessor.query("insureType.queryInsureTypeList", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureTypeList", insureTypeList);
				insureList = (List<Map>) DataAccessor.query("rentContract.readInsureByRectId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureList", insureList);

				otherFeeList = (List<Map>) DataAccessor.query("rentContract.readOtherFeeByRectId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("otherFeeList", otherFeeList);
				
				remarkList = (List<Map>) DataAccessor.query("rentContract.readContractRemark", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("remarkList", remarkList);
				
				//
				provinces = (List<Map>) DataAccessor.query( "area.getProvinces", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("provinces", provinces);
				context.contextMap.put("provinceId", rentContract.get("PROVINCE_ID"));
				citys = (List<Map>) DataAccessor.query("area.getCitysByProvinceId", context.contextMap,DataAccessor.RS_TYPE.LIST);
				outputMap.put("citys", citys);
				//
				insureCompanyList = (List<Map>) DataAccessor.query("insuCompany.queryInsureCompanyListForSelect", null, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureCompanyList", insureCompanyList);
				//
				companyList = (List) DataAccessor.query("companyManage.queryCompanyAlias", null, DataAccessor.RS_TYPE.LIST);
				outputMap.put("companyList", companyList);
				//
				Map dataDictionaryMap = new HashMap();
				
				dataDictionaryMap.put("dataType", "融资租赁合同类型");
				contractType = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("contractType", contractType);

				dataDictionaryMap.put("dataType", "支付方式");
				payWays = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("payWays", payWays);
				
				dataDictionaryMap.put("dataType", "租赁期满处理方式");
				dealWays = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
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
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		
		}
		
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/rentcontract/showRentContract.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
	
	}
	
	/**
	 * delete equipment
	 * @param context
	 * @throws SQLException
	 */
	public void deleteContractIrr(Context context) throws SQLException {
		DataAccessor.getSession().delete("rentContract.deleteRentContractIrr", context.contextMap);
	}
	
	/**
	 * delete equipment
	 * @param context
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public void deleteEquipment(Context context) throws SQLException {
		
		//
		List<Map> equipmentIds = DataAccessor.getSession().queryForList("rentContract.queryEquipmentIdsByRectId", context.contextMap);
		//
		// DataAccessor.getSession().delete("rentContract.deleteRentContractDetailByRectId", context.contextMap);
		DataAccessor.getSession().delete("rentContract.deleteRentContractDetailByRectIdFalse", context.contextMap);
		
		//
		DataAccessor.getSession().startBatch();
		
		for (Map map : equipmentIds) {
			
			// DataAccessor.getSession().delete("rentContract.deleteRentContractEquipment", map);
			DataAccessor.getSession().delete("rentContract.deleteRentContractEquipmentFalse", map);
		
		}
		
		DataAccessor.getSession().executeBatch();

	}
	/**
	 * delete insure
	 * @param context
	 * @throws SQLException
	 */
	public void deleteInsure(Context context) throws SQLException {
		
		// DataAccessor.getSession().delete("rentContract.deleteRentContractInsureByRectId", context.contextMap);
		
		DataAccessor.getSession().delete("rentContract.deleteRentContractInsureByRectIdFalse", context.contextMap);
			
	}
	/**
	 * delete other fee
	 * @param context
	 * @throws SQLException
	 */
	public void deleteOtherFee(Context context) throws SQLException {
		
		// DataAccessor.getSession().delete("rentContract.deleteRentContractOtherFeeByRectId", context.contextMap);
		
		DataAccessor.getSession().delete("rentContract.deleteRentContractOtherFeeByRectIdFalse", context.contextMap);
		
	}
	/**
	 * 操作设备。
	 * @param context
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public void operateEquipment(Context context) throws SQLException {
		
		// String[] types = HTMLUtil.getParameterValues(context.request, "TYPE", "");
		// String[] kinds = HTMLUtil.getParameterValues(context.request, "KIND", "");
		// String[] products = HTMLUtil.getParameterValues(context.request, "PRODUCT", "");
		String[] suppiers = HTMLUtil.getParameterValues(context.request, "SUPPIER", "");
		String[] staybuyPrices = HTMLUtil.getParameterValues(context.request, "STAYBUY_PRICE", "");
		String[] unitPrices = HTMLUtil.getParameterValues(context.request, "UNIT_PRICE", "");
		String[] shuiPrices = HTMLUtil.getParameterValues(context.request, "SHUI_PRICE", "");
		String[] amounts = HTMLUtil.getParameterValues(context.request, "AMOUNT", "");
		String[] units = HTMLUtil.getParameterValues(context.request, "UNIT", "");
		// String[] memos = HTMLUtil.getParameterValues(context.request, "MEMO", "");
		String[] lock_code = HTMLUtil.getParameterValues(context.request, "LOCK_CODE", "");
		
		String[] typeNames = HTMLUtil.getParameterValues(context.request, "TYPE_NAME", "");
		String[] kindNames = HTMLUtil.getParameterValues(context.request, "KIND_NAME", "");
		String[] productNames = HTMLUtil.getParameterValues(context.request, "PRODUCT_NAME", "");
		
		//Modify by Michael 2012 5-4 在初审时维护机号
		//String[] numberNames = HTMLUtil.getParameterValues(context.request, "NUMBER_NAME", "");
		String[] numberNames = HTMLUtil.getParameterValues(context.request, "NUMBER", "");
		
		String[] suppierNames = HTMLUtil.getParameterValues(context.request, "SUPPIER_NAME", "");
		// String[] eqmtIds = HTMLUtil.getParameterValues(context.request, "EQMT_ID", "0");
		
		//Add by Michael 2012 4-18 增加重车相关资料
		String[] carColor = HTMLUtil.getParameterValues(context.request, "CAR_COLOR", "");
		String[] carRigsterNumber = HTMLUtil.getParameterValues(context.request, "CAR_RIGSTER_NUMBER", "");
		String[] carIDNumber = HTMLUtil.getParameterValues(context.request, "CAR_ID_NUMBER", "");
		String[] carEngineNumber = HTMLUtil.getParameterValues(context.request, "CAR_ENGINE_NUMBER", "");
		String[] carImportDomestic = HTMLUtil.getParameterValues(context.request, "CAR_IMPORT_DOMESTIC", "");
		
		Map map = new HashMap();
		map.put("RECT_ID", context.contextMap.get("RECT_ID"));
		map.put("RECS_ID", context.contextMap.get("RECS_ID"));
		map.put("s_employeeId", context.contextMap.get("s_employeeId"));
		Long eqmt_id = 0l;
		
		//DataAccessor.getSession().startTransaction();
		
		for (int i=0;i<typeNames.length;i++) {
			
			Integer amount = HTMLUtil.parseIntParam(amounts[i], 0);
			map.put("SUEQ_ID", suppiers[i]);  
			map.put("THING_KIND", typeNames[i]); 
			map.put("THING_NAME", kindNames[i]); 
			map.put("MODEL_SPEC", productNames[i]); 
			map.put("THING_NUMBER", numberNames[i]); 
			map.put("BRAND", suppierNames[i]); 
			
			map.put("UNIT_PRICE", DataUtil.doubleUtil(unitPrices[i]));  
			map.put("SHUI_PRICE", DataUtil.doubleUtil(shuiPrices[i]));  
			// map.put("MEMOS", memos[i]);
			map.put("LOCK_CODE", lock_code[i]);
			
			//设备留购款，一个合同100块，在第一台设备加上100块，其他设备都为0
			//map.put("STAYBUY_PRICE", DataUtil.doubleUtil(staybuyPrices[i])); 
			if (i==0) {
				map.put("STAYBUY_PRICE", "100.00"); 
			}else {
				map.put("STAYBUY_PRICE", "0.00"); 
			}
			
			map.put("UNIT", units[i]); 
			
			//Add by Michael 2012 4-18 增加重车相关资料
			map.put("CAR_COLOR", carColor[i]); 
			map.put("CAR_RIGSTER_NUMBER", carRigsterNumber[i]); 
			map.put("CAR_ID_NUMBER", carIDNumber[i]); 
			map.put("CAR_ENGINE_NUMBER", carEngineNumber[i]); 
			map.put("CAR_IMPORT_DOMESTIC", carImportDomestic[i]); 
			
			for (int j=0; j<amount ; j++) {
				
				eqmt_id = (Long) DataAccessor.getSession().insert("rentContract.createRentContractEquip", map);
				map.put("EQMT_ID", eqmt_id);
				
				DataAccessor.getSession().insert("rentContract.createRentContractDetail", map);
			
			}
			
		}
		
		//DataAccessor.getSession().commitTransaction();
		//DataAccessor.getSession().endTransaction();
	

	}
	
	
	/**
	 * 管理费收入
	 * @param context
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public void operateManagerFee(Context context) throws SQLException {
	
		List feeSetList =null;
		Map where=null;
		try {
			feeSetList = (List) DataAccessor.query(
					"creditReportManage.getFeeSetListAll", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
	
		DataAccessor.getSession().startBatch();
		for (int i = 0; i < feeSetList.size(); i++) {
			Map tempMap = new HashMap();
			where = (Map) feeSetList.get(i);
			tempMap.put("FEE_SET_ID", where.get("ID"));
			tempMap.put("CREATE_SHOW_NAME", where.get("CREATE_SHOW_NAME"));
			tempMap.put("IS_LEASERZE_COST", where.get("IS_LEASERZE_COST"));
			tempMap.put("CREATE_FILED_NAME", where.get("CREATE_FILED_NAME")) ;
			tempMap.put("CREATE_ID", context.contextMap.get("s_employeeId"));
			tempMap.put("RECT_ID", context.contextMap.get("RECT_ID"));
			tempMap.put("FEE", DataUtil.doubleUtil(context.contextMap.get(where.get("CREATE_FILED_NAME"))));
			//Add by Michael 2012 12-21 增加费用来源
			tempMap.put("SOURCE_CODE", context.contextMap.get(where.get("CREATE_FILED_NAME")+"_SOURCE")) ;
			DataAccessor.getSession().insert("rentContract.createManagerFee", tempMap);
		}	
		DataAccessor.getSession().executeBatch();
		//----------------------------------------------------------------
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**
	 * 操作保险
	 * @param context
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public void operateInsure(Context context) throws SQLException {
		
		String[] insureItems = HTMLUtil.getParameterValues(context.request, "INSURE_ITEM", "");
		String[] startDates = HTMLUtil.getParameterValues(context.request, "INSURE_START_DATE", "");
		String[] endDates = HTMLUtil.getParameterValues(context.request, "INSURE_END_DATE", "");
		String[] insureRates = HTMLUtil.getParameterValues(context.request, "INSURE_RATE", "");
		String[] insurePrice = HTMLUtil.getParameterValues(context.request, "INSURE_PRICE", "0");
		String[] insureMemo = HTMLUtil.getParameterValues(context.request, "INSURE_MEMO", "");
		// String[] inusreIds = HTMLUtil.getParameterValues(context.request, "RECI_ID", "0");
		
		Map map = new HashMap();
		map.put("RECT_ID", context.contextMap.get("RECT_ID"));
		map.put("s_employeeId", context.contextMap.get("s_employeeId"));
		
		DataAccessor.getSession().startBatch();
		
		for (int i=0;i<insureItems.length;i++) {
			
			map.put("INSURE_ITEM", insureItems[i]);  
			map.put("START_DATE", startDates[i]);
			map.put("END_DATE", endDates[i]); 
			map.put("INSURE_RATE", DataUtil.doubleUtil(insureRates[i]));  
			map.put("INSURE_PRICE", DataUtil.doubleUtil(insurePrice[i])); 
			map.put("MEMO", insureMemo[i]);  
			
			DataAccessor.getSession().insert("rentContract.createRentContractInsure", map);
		}
		
		DataAccessor.getSession().executeBatch();

	}
	/**
	 * 其它费用
	 * @param context
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public void operateOtherFee(Context context) throws SQLException {
		
		String[] otherNames = HTMLUtil.getParameterValues(context.request, "OTHER_NAME", "");
		String[] otherPrices = HTMLUtil.getParameterValues(context.request, "OTHER_PRICE", "");
		String[] otherDates = HTMLUtil.getParameterValues(context.request, "OTHER_DATE", "");
		String[] otherMemos = HTMLUtil.getParameterValues(context.request, "OTHER_MEMO", "");
		// String[] otherIds = HTMLUtil.getParameterValues(context.request, "RECO_ID", "0");
		
		Map map = new HashMap();
		map.put("RECT_ID", context.contextMap.get("RECT_ID"));
		map.put("s_employeeId", context.contextMap.get("s_employeeId"));
		
		DataAccessor.getSession().startBatch();
		
		for (int i=0;i<otherNames.length;i++) {
			
			map.put("OTHER_NAME", otherNames[i]);  
			map.put("OTHER_PRICE", DataUtil.doubleUtil(otherPrices[i]));  
			map.put("OTHER_DATE", otherDates[i]);
			map.put("MEMO", otherMemos[i]); 
				
			DataAccessor.getSession().insert("rentContract.createRentContractOtherFee", map);
			
		}
		
		DataAccessor.getSession().executeBatch();

	}
	
	/**
	 * 
	 * @param context
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public void operateContractIrr(Context context) throws SQLException {
		
		String[] irr_month_prices = HTMLUtil.getParameterValues(context.request, "PAY_IRR_MONTH_PRICE", "");
		String[] irr_month_price_starts = HTMLUtil.getParameterValues(context.request, "PAY_IRR_MONTH_PRICE_START", "");
		String[] irr_month_price_ends = HTMLUtil.getParameterValues(context.request, "PAY_IRR_MONTH_PRICE_END", "");
		
		Map map = new HashMap();
		map.put("recs_id", context.contextMap.get("RECS_ID"));
		map.put("s_employeeId", context.contextMap.get("s_employeeId"));
		
		DataAccessor.getSession().startBatch();
		
		for (int i=0;i<irr_month_prices.length;i++) {
			map.put("irr_month_price", irr_month_prices[i]);  
			map.put("irr_month_price_start", irr_month_price_starts[i]);  
			map.put("irr_month_price_end", irr_month_price_ends[i]);
			
			DataAccessor.getSession().insert("rentContract.createContractIrr", map);
		}
		
		DataAccessor.getSession().executeBatch();
	}
	
	/**
	 * 操作备注
	 * @param context
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void operateRemark(Context context) throws Exception {
		Map map = new HashMap();
		map.put("s_employeeId", context.contextMap.get("s_employeeId"));
		map.put("RECT_ID", context.contextMap.get("RECT_ID")==null?0:context.contextMap.get("RECT_ID"));
		map.put("PRCD_ID", context.contextMap.get("PRCD_ID")==null?0:context.contextMap.get("PRCD_ID"));
		
		String remark = String.valueOf(context.contextMap.get("REMARK") == null ? "" : context.contextMap.get("REMARK")).trim();
		if (remark != null && !remark.equals("")) {
			map.put("REMARK", context.contextMap.get("REMARK"));
		} else {
			map.put("REMARK", "融资租赁合同初审");
		}
		
		DataAccessor.getSession().insert("rentContract.createRentContractRemark", map);

	}
	/**
	 * pact page
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void managePact(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;

		List<Map> pucsContracts = null;
		List<Map> paylists = null;
		List<Map> windLists = null;
		List<Map> deliveryNotices = null;
		List<Map> payments = null;
		List<Map> upload = null;
		List<Map> creditcorpreport=null;
		Map memoMap = null;
		List<Map> rentEquipmentList = null;
		List<Map> creditEquipmentList = null;
		Map supl =null;
		List allBankAccount=null;
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {
				outputMap.put("PRCD_ID", context.contextMap.get("PRCD_ID"));
				outputMap.put("RECT_ID", context.contextMap.get("RECT_ID"));
				outputMap.put("contract_type", context.contextMap.get("contract_type"));
				List equipList = (List) DataAccessor.query(
						"deliveryNotice.getEquipByRectId", context.contextMap,DataAccessor.RS_TYPE.LIST);
				//如果发货通知单的货物没有的话就不能新建
				if(equipList.size()==0){
					outputMap.put("equipstatus", "-1");
				}else{
					outputMap.put("equipstatus", context.contextMap.get("status"));
					
				}
				//
				outputMap.put("rcstatus", context.contextMap.get("status"));
				pucsContracts = (List<Map>) DataAccessor.query("rentContractPact.queryPucsContractByPrcdId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("pucsContracts", pucsContracts);
				Map  pucsContractsFlagMap = (Map) DataAccessor.query("rentContractPact.queryPucsContractFlagByRectId", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("pucsContractsFlag", pucsContractsFlagMap.get("SHORTFALL"));
				//
				paylists = (List<Map>) DataAccessor.query("rentContractPact.queryPaylistByRectId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("paylists", paylists);
				//
				windLists = (List<Map>) DataAccessor.query("rentContractPact.queryWindListByRectId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("windLists", windLists);
				//
				deliveryNotices = (List<Map>) DataAccessor.query("rentContractPact.queryDeliveryNoticesByRectId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("deliveryNotices", deliveryNotices);
				//
				payments = (List<Map>) DataAccessor.query("rentContractPact.queryPaymentsByRectId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("payments", payments);
				//
				upload = (List<Map>) DataAccessor.query("rentContractPact.queryUploadByRectId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("upload", upload);
				
				//
				memoMap = (Map) DataAccessor.query("rentContractPact.selectNewMemo", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("memoMap", memoMap);	
				
				//
				rentEquipmentList = (List<Map>) DataAccessor.query("rentContractPact.selectRentEquipment", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("rentEquipmentList", rentEquipmentList);	
				creditEquipmentList = (List<Map>) DataAccessor.query("rentContractPact.selectCreidtEquipment", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("creditEquipmentList", creditEquipmentList);	
				//如果购销合同下的设备在付款审批单中都有了就不能增加了

				//查询供应商保证
				supl = (Map)DataAccessor.query("rentContractPact.querySupl",context.getContextMap(),DataAccessor.RS_TYPE.MAP);
				outputMap.put("supl", supl);
				
				if(pucsContracts.size()>0){
					List rentEquipmentListsize = (List)DataAccessor.query("payment.getEquipByRectId", context.contextMap, DataAccessor.RS_TYPE.LIST);
					if(rentEquipmentListsize.size()==0){
						outputMap.put("rentEquipmentListstatus", "-1");
					}else{
						outputMap.put("rentEquipmentListstatus", context.contextMap.get("status"));					
					}
				}
				
				context.contextMap.put("credit_id", context.contextMap.get("PRCD_ID"));
				creditcorpreport = (List<Map>) DataAccessor.query("creditPriorRecords.getCorpReports", context.contextMap, DataAccessor.RS_TYPE.LIST);
				
				if(creditcorpreport!=null&&creditcorpreport.size()>0){
					
					outputMap.put("creditcorpreport", creditcorpreport);
				}
				
				//银行账号
				int is= (Integer) DataAccessor.query("rentContract.isCONTRACT", context.contextMap, DataAccessor.RS_TYPE.OBJECT) ;
				if(is==0) {
					allBankAccount=(List<Map>)DataAccessor.query("exportContractPdf.queryAllBankAccount2", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
				} else {
					allBankAccount=(List<Map>)DataAccessor.query("exportContractPdf.queryAllBankAccount1", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
				}
				List bankAccount=(List<Map>)DataAccessor.query("exportContractPdf.getBankAccount", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
				for(int i=0;bankAccount!=null&&i<bankAccount.size();i++) {
					//allBankAccount是拿的主要银行,bankAccount拿的所有银行,所以把供货商名字和金额PUT进bankAccount
					((Map)(bankAccount.get(i))).put("NAME",((Map)allBankAccount.get(0)).get("NAME"));
					((Map)(bankAccount.get(i))).put("MONEY",((Map)allBankAccount.get(0)).get("MONEY"));
				}
				outputMap.put("allBankAccount", bankAccount);
				
				//查询是否已经生成合同 （如果isCONTRACT=0则没有生成合同）
				int  isCONTRACT= (Integer) DataAccessor.query("rentContract.isCONTRACT", context.contextMap, DataAccessor.RS_TYPE.OBJECT) ;
				outputMap.put("isCONTRACT", isCONTRACT);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		
		}
		
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/rentcontract/pact.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
		
	}
		
		/**
		 * pact page
		 * @param context
		 */
		@SuppressWarnings("unchecked")
		public void managePactForShow(Context context) {
			
			Map outputMap = new HashMap();
			List errList = context.errList;

			List<Map> pucsContracts = null;
			List<Map> paylists = null;
			List<Map> windLists = null;
			List<Map> deliveryNotices = null;
			List<Map> payments = null;
			List<Map> upload = null;
			List<Map> creditcorpreport = null;
			Map memoMap = null;
			List<Map> rentEquipmentList = null;
			List<Map> creditEquipmentList = null;
			Map supl=null;
			List allBankAccount=null;
			List insorupds =  null;
			/*-------- data access --------*/		
			if(errList.isEmpty()){	
			
				try {
					outputMap.put("PRCD_ID", context.contextMap.get("PRCD_ID"));
					outputMap.put("RECT_ID", context.contextMap.get("RECT_ID"));
					outputMap.put("rcstatus", context.contextMap.get("status"));
					outputMap.put("contract_type", context.contextMap.get("contract_type"));
					//判断是否有购销合同
					pucsContracts = (List<Map>) DataAccessor.query("rentContractPact.queryPucsContractByPrcdId", context.contextMap, DataAccessor.RS_TYPE.LIST);
					outputMap.put("pucsContracts", pucsContracts);
					Map  pucsContractsFlagMap = (Map) DataAccessor.query("rentContractPact.queryPucsContractFlagByRectId", context.contextMap, DataAccessor.RS_TYPE.MAP);
					outputMap.put("pucsContractsFlag", pucsContractsFlagMap.get("SHORTFALL"));
					//
					paylists = (List<Map>) DataAccessor.query("rentContractPact.queryPaylistByRectId", context.contextMap, DataAccessor.RS_TYPE.LIST);
					outputMap.put("paylists", paylists);
					//
					windLists = (List<Map>) DataAccessor.query("rentContractPact.queryWindListByRectId", context.contextMap, DataAccessor.RS_TYPE.LIST);
					outputMap.put("windLists", windLists);
					//
					deliveryNotices = (List<Map>) DataAccessor.query("rentContractPact.queryDeliveryNoticesByRectId", context.contextMap, DataAccessor.RS_TYPE.LIST);
					outputMap.put("deliveryNotices", deliveryNotices);
					//
					payments = (List<Map>) DataAccessor.query("rentContractPact.queryPaymentsByRectId", context.contextMap, DataAccessor.RS_TYPE.LIST);
					outputMap.put("payments", payments);
					//
					upload = (List<Map>) DataAccessor.query("rentContractPact.queryUploadByRectId", context.contextMap, DataAccessor.RS_TYPE.LIST);
					outputMap.put("upload", upload);
					
					//
					memoMap = (Map) DataAccessor.query("rentContractPact.selectNewMemo", context.contextMap, DataAccessor.RS_TYPE.MAP);
					outputMap.put("memoMap", memoMap);	
					
					//
					rentEquipmentList = (List<Map>) DataAccessor.query("rentContractPact.selectRentEquipment", context.contextMap, DataAccessor.RS_TYPE.LIST);
					outputMap.put("rentEquipmentList", rentEquipmentList);	
					creditEquipmentList = (List<Map>) DataAccessor.query("rentContractPact.selectCreidtEquipment", context.contextMap, DataAccessor.RS_TYPE.LIST);
					outputMap.put("creditEquipmentList", creditEquipmentList);	
					
					context.contextMap.put("credit_id", context.contextMap.get("PRCD_ID"));
					creditcorpreport = (List<Map>) DataAccessor.query("creditPriorRecords.getCorpReports", context.contextMap, DataAccessor.RS_TYPE.LIST);
				
					if(creditcorpreport!=null&&creditcorpreport.size()>0){
						
						outputMap.put("creditcorpreport", creditcorpreport);
					}
					//查询供应商保证
					if(context.contextMap.get("RECT_ID") == null || context.contextMap.get("RECT_ID").equals("")){
						supl = (Map)DataAccessor.query("rentContractPact.querySuplByCreditId",context.getContextMap(),DataAccessor.RS_TYPE.MAP);
					}else {
						supl = (Map)DataAccessor.query("rentContractPact.querySupl",context.getContextMap(),DataAccessor.RS_TYPE.MAP);
					}
					outputMap.put("supl", supl);
					
					//加入供应商开户银行帐号选择 add by Shen Qi see mantis 280
					//is=0 没有生成合同 否则已经生成合同
					int is= (Integer) DataAccessor.query("rentContract.isCONTRACT", context.contextMap, DataAccessor.RS_TYPE.OBJECT) ;
					if(is==0) {
						allBankAccount=(List<Map>)DataAccessor.query("exportContractPdf.queryAllBankAccount2", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
					} else {
						allBankAccount=(List<Map>)DataAccessor.query("exportContractPdf.queryAllBankAccount1", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
					}
					List bankAccount=(List<Map>)DataAccessor.query("exportContractPdf.getBankAccount", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
					for(int i=0;bankAccount!=null&&i<bankAccount.size();i++) {
						//allBankAccount是拿的主要银行,bankAccount拿的所有银行,所以把供货商名字和金额PUT进bankAccount
						((Map)(bankAccount.get(i))).put("NAME",((Map)allBankAccount.get(0)).get("NAME"));
						((Map)(bankAccount.get(i))).put("MONEY",((Map)allBankAccount.get(0)).get("MONEY"));
					}
					outputMap.put("allBankAccount", bankAccount);
					int isVouch=(Integer) DataAccessor.query("exportContractPdf.isVouch", context.contextMap, DataAccessor.RS_TYPE.OBJECT) ;
					outputMap.put("isVouch", isVouch);
					
					int isMarkMachine=(Integer) DataAccessor.query("exportContractPdf.isAdvanceMachine", context.contextMap, DataAccessor.RS_TYPE.OBJECT) ;
					outputMap.put("isMarkMachine", isMarkMachine);
					
					//应征份数
					Map params = new HashMap();
					String contract_type = (String) context.contextMap.get("contract_type");
					if("4".equals(contract_type) || "11".equals(contract_type)){
						params.put("CONTRACT_TYPE",Integer.parseInt(contract_type)+1);
						params.put("cardFlag",2);
						params.put("PRCD_ID",context.contextMap.get("PRCD_ID"));
						insorupds =(List)DataAccessor.query("rentFile.selectRentFile", params, DataAccessor.RS_TYPE.LIST);
						if(insorupds!=null && insorupds.size()>0){ 
							Map insorupdMap = new HashMap();
							
							for(Object insorupd:insorupds){
								String key = null;
								String fileName = (String) ((Map)insorupd).get("FILE_NAME");
								
									if("买卖合同".equals(fileName)){
										key = "_01";
									}else if("股东会或董事会出售资产同意决议书".equals(fileName)){
										key = "_02";
									}else if("融资租赁合同".equals(fileName)){
										key = "_03";
									}else if("承诺暨授权书".equals(fileName)){
										key = "_04";
									}else if("付款指示书".equals(fileName)){
										key = "_05";
									}else if("租赁物验收证明暨起租通知书".equals(fileName)){
										key = "_06";
									}else if("租金支付明细表".equals(fileName)){
										key = "_07";
									}else if("机动车辆抵押合同".equals(fileName)){
										key = "_08";
									}else if("车辆处分授权委托书".equals(fileName)){
										key = "_09";
									}else if("租赁物委托管理三方协议".equals(fileName)){
										key = "_10";
									}else if("授权书（挂靠行授权处分）".equals(fileName)){
										key = "_11";
									}else if("声明书".equals(fileName)){
										key = "_12";
									}else if("车辆挂靠管理协议".equals(fileName)){
										key = "_14";
									}else if("自动扣款授权书".equals(fileName)){
										key = "_19";
									}else if("GPS系统设置承诺书".equals(fileName)){
										key = "_20";
									}else if("开票资料确认书".equals(fileName)){
										key = "_21";
									}else if("咨询服务协议书".equals(fileName)){
										key = "_22";
									}							
								if(key!=null){
									insorupdMap.put(key, ((Map)insorupd).get("WANT_COUNT"));
								}							
							}
							outputMap.put("insorupd", insorupdMap);
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
			
			}
			
			if (errList.isEmpty()) {
				Output.jspOutput(outputMap, context, "/rentcontract/pactForShow.jsp");
			} else {
				outputMap.put("errList", errList);
				Output.jspOutput(outputMap, context, "/error.jsp");
			}
	
	}
		
	
		/**
		 * pact page
		 * @param context
		 */
		@SuppressWarnings("unchecked")
		public void managePactForShowByPay(Context context) {
			
			Map outputMap = new HashMap();
			List errList = context.errList;

			List<Map> pucsContracts = null;
			List<Map> paylists = null;
			List<Map> windLists = null;
			List<Map> deliveryNotices = null;
			List<Map> payments = null;
			List<Map> upload = null;
			Map memoMap = null;
			List<Map> rentEquipmentList = null;
			
			/*-------- data access --------*/		
			if(errList.isEmpty()){	
			
				try {
					outputMap.put("PRCD_ID", context.contextMap.get("PRCD_ID"));
					outputMap.put("RECT_ID", context.contextMap.get("RECT_ID"));
					outputMap.put("rcstatus", context.contextMap.get("status"));
					//
					memoMap = (Map) DataAccessor.query("rentContractPact.selectNewMemo", context.contextMap, DataAccessor.RS_TYPE.MAP);
					outputMap.put("memoMap", memoMap);	
					//
					rentEquipmentList = (List<Map>) DataAccessor.query("rentContractPact.selectRentEquipment", context.contextMap, DataAccessor.RS_TYPE.LIST);
					outputMap.put("rentEquipmentList", rentEquipmentList);	
					
				} catch (Exception e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
			
			}
			if (errList.isEmpty()) {
				Output.jspOutput(outputMap, context, "/lockManagement/lockPactForShow.jsp");
			} else {
				outputMap.put("errList", errList);
				Output.jspOutput(outputMap, context, "/error.jsp");
			}
	
	}
		
	/**
	 * 作废合同
	 * @param context
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void invalidRentContract(Context context)  {
		List errList = context.errList;
		List collectionList = null;
		int[] recpIds = null;
		if(errList.isEmpty()){
			try {
				collectionList = (List)DataAccessor.query("rentContract.queryRecpid", context.contextMap, DataAccessor.RS_TYPE.LIST);
				DataAccessor.getSession().startTransaction();				
				DataAccessor.getSession().update("rentContract.invalidrentContract", context.contextMap);
				for(int y=0;y<collectionList.size();y++){
					int recpId = DataUtil.intUtil(collectionList.get(y));
					context.contextMap.put("recpId", recpId);
					DataAccessor.getSession().update("rentContract.invalidCollection", context.contextMap);
				}
				DataAccessor.getSession().commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}finally{
				try {
					DataAccessor.getSession().endTransaction();
				} catch (Exception e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
			}
		}
		if(errList.isEmpty()){
			Output.jspSendRedirect(context, "defaultDispatcher?__action=rentContract.queryRentContract");
		}


	}

	/**
	 * 2012/01/19 Yang Yun
	 * 判断合同是否可以作废
	 * 有销账记录不能作废合同
	 */
	public void validateInvaliRent(Context context){
		Integer result = null;
		Map<String, Object> outputMap = new HashMap<String, Object>();
		try {
			result = (Integer) DataAccessor.query("rentContract.validateInvaliRent", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
			outputMap.put("resultCount", result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Output.jsonOutput(outputMap, context);
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.brick.service.core.AService#preExecute(java.lang.String,
	 *      com.brick.service.entity.Context)
	 */
	@Override
	protected boolean preExecute(String action, Context context) {
		return super.preExecute(action, context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.brick.service.core.AService#afterExecute(java.lang.String,
	 *      com.brick.service.entity.Context)
	 */
	@Override
	protected void afterExecute(String action, Context context) {
		//
		if ("rentContract.createRentContract".equals(action)) {
			
			Long creditId = DataUtil.longUtil(context.contextMap.get("PRCD_ID"));
			Long contractId = DataUtil.longUtil(context.contextMap.get("RECT_ID"));
			String logType = "融资租赁合同";
			String logTitle = "初审";
			String logCode = String.valueOf(context.contextMap.get("LEASE_CODE"));
			String memo = "融资租赁合同初审";
			int state = 1;
			Long userId = DataUtil.longUtil(context.contextMap.get("s_employeeId"));
			Long otherId = null;
			
			BusinessLog.addBusinessLog(creditId, contractId, logType, logTitle, logCode, memo, state, userId, otherId, (String)context.contextMap.get("IP"));
		}else if ("rentContract.updateRentContract".equals(action)) {
			
			Long creditId = DataUtil.longUtil(context.contextMap.get("PRCD_ID"));
			Long contractId = DataUtil.longUtil(context.contextMap.get("RECT_ID"));
			String logType = "融资租赁合同";
			String logTitle = "修改";
			String logCode = String.valueOf(context.contextMap.get("LEASE_CODE"));
			String memo = "融资租赁合同修改";
			int state = 1;
			Long userId = DataUtil.longUtil(context.contextMap.get("s_employeeId"));
			Long otherId = null;
			
			BusinessLog.addBusinessLog(creditId, contractId, logType, logTitle, logCode, memo, state, userId, otherId,(String)context.contextMap.get("IP"));
		}
	}
	
	/**
	 * 
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void lockManager(Context context) {
		Map outputMap = new HashMap();
		List lockList = null;
		try {
			context.contextMap.put("dataType", "锁码方式");
			lockList = (List) DataAccessor.query(
					"dataDictionary.queryDataDictionary", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

		outputMap.put("lockList", lockList);
		Output.jsonOutput(outputMap, context);
	}
	
	/**
	 * 导出一般租赁确认函_合同管理
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void includeLeases(Context context){
		//ConfirmationLetterPDF.includeLeasesToPdf(context);
		try{
			
			List<Map> eqmts=(List<Map>)DataAccessor.query("rentContract.queryeqmts", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
			Map info=(Map)DataAccessor.query("rentContract.queryContract",context.getContextMap(),DataAccessor.RS_TYPE.MAP);
			ConfirmationLetterPDF.includeLeasesToPdf(context,info,eqmts);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 导出委托租赁确认函_合同管理
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void consignmentFinancialLeasing(Context context){
		//ConfirmationLetterPDF.consignmentFinancialLeasingToPdf(context);
		try{
			List<Map> eqmts=(List<Map>)DataAccessor.query("rentContract.queryeqmts", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
			Map info=(Map)DataAccessor.query("rentContract.queryContract",context.getContextMap(),DataAccessor.RS_TYPE.MAP);
			ConfirmationLetterPDF.consignmentFinancialLeasingToPdf(context,info,eqmts);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	/**
	 * 导出一般租赁确认函_合同浏览
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void includeLeasesPrjt(Context context){
		try{
			List<Map> eqmts=null;
			Map info=null;
			if(context.getContextMap().get("RECT_ID")!=null&&!"".equals(context.getContextMap().get("RECT_ID"))){
				eqmts=(List<Map>)DataAccessor.query("rentContract.queryeqmts", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
				info=(Map)DataAccessor.query("rentContract.queryContract",context.getContextMap(),DataAccessor.RS_TYPE.MAP);
				
			}else{
				eqmts=(List<Map>)DataAccessor.query("rentContract.queryeqmtsPrjt", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
				info=(Map)DataAccessor.query("rentContract.queryContractPrjt",context.getContextMap(),DataAccessor.RS_TYPE.MAP);
				
			}

			ConfirmationLetterPDF.includeLeasesToPdf(context,info,eqmts);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 导出委托租赁确认函_合同浏览
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void consignmentFinancialLeasingPrjt(Context context){
		try{
			List<Map> eqmts=null;
			Map info=null;
			if(context.getContextMap().get("RECT_ID")!=null && !"".equals(context.getContextMap().get("RECT_ID"))){
				eqmts=(List<Map>)DataAccessor.query("rentContract.queryeqmts", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
				info=(Map)DataAccessor.query("rentContract.queryContract",context.getContextMap(),DataAccessor.RS_TYPE.MAP);
				
			}else{
				eqmts=(List<Map>)DataAccessor.query("rentContract.queryeqmtsPrjt", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
				info=(Map)DataAccessor.query("rentContract.queryContractPrjt",context.getContextMap(),DataAccessor.RS_TYPE.MAP);
				
			}
			ConfirmationLetterPDF.consignmentFinancialLeasingToPdf(context,info,eqmts);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 导出密码控制表
	 * @param context
	 */
	public void passwordControlTablePDF(Context context){
		PasswordControlTablePDF.expPDF(context);
	}
	public void expPasswordPDF(Context context){
		PasswordControlTablePDF.expCreditEuipPDF(context);
	}
	
	/**
	 * 导出合同资料
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void contractExcel(Context context){
		List title = new ArrayList() ;
		title.add("合同号") ;
		title.add("承租人名称") ;
		title.add("省") ;
		title.add("市") ;
		title.add("区") ;
		title.add("区域主管") ;
		title.add("客户经理") ;
		title.add("实际TR") ;
		title.add("未税总金额") ;
		title.add("保证金") ;
		title.add("客户TR") ;
		title.add("期数") ;
		title.add("起租日期") ;
		title.add("评审通过") ;
		title.add("实际拨款") ;
		title.add("利差") ;
		Map paramMap = new HashMap() ;
		Map rsMap = new HashMap() ;
		
		try {
//			String[]  con = HTMLUtil.getParameterValues(context.getRequest(), "credit_idxx", "00");
//			context.contextMap.put("ids", con) ;
			paramMap.put("id", context.contextMap.get("s_employeeId"));
			rsMap = (Map) DataAccessor.query("employee.getEmpInforById", paramMap, DataAccessor.RS_TYPE.MAP);
			context.contextMap.put("p_usernode", rsMap.get("NODE"));
			
			List rent = (ArrayList) DataAccessor.query("rentContract.queryRentContract", context.contextMap, DataAccessor.RS_TYPE.LIST);
			if(rent != null && rent.size() > 0){
				String[] con= new String[rent.size()] ;
				for(int i=0;i<con.length;i++){
					if(((Map)rent.get(i)).get("PRCD_ID") != null){ 
						con[i] = ((Map)rent.get(i)).get("PRCD_ID").toString() ;
					}
				}
				context.contextMap.put("ids", con) ;
			}
			context.contextMap.put("content", DataAccessor.query("rentContract.contractExcel", context.contextMap, DataAccessor.RS_TYPE.LIST)) ;
			new ContractExcel().ContractExcelJoin(title, context) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	// Add by Michael 2012 6-21
	public void showLockCodeForModify(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;

		List<Map> pucsContracts = null;
		List<Map> paylists = null;
		List<Map> windLists = null;
		List<Map> deliveryNotices = null;
		List<Map> payments = null;
		List<Map> upload = null;
		Map memoMap = null;
		List<Map> rentEquipmentList = null;
		List lockList=null;
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {
				outputMap.put("PRCD_ID", context.contextMap.get("PRCD_ID"));
				outputMap.put("RECT_ID", context.contextMap.get("RECT_ID"));
				outputMap.put("rcstatus", context.contextMap.get("status"));
				
				Map dataDictionaryMap = new HashMap();
				dataDictionaryMap.put("dataType", "锁码方式");
				lockList = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("lockList", lockList);
				//
				memoMap = (Map) DataAccessor.query("rentContractPact.selectNewMemo", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("memoMap", memoMap);	
				//
				if("".equals(context.contextMap.get("RECT_ID")) ||null==context.contextMap.get("RECT_ID")){
					rentEquipmentList = (List<Map>) DataAccessor.query("rentContractPact.selectCreidtEquipment", context.contextMap, DataAccessor.RS_TYPE.LIST);
				}else{
					rentEquipmentList = (List<Map>) DataAccessor.query("rentContractPact.selectRentEquipment", context.contextMap, DataAccessor.RS_TYPE.LIST);
				}
				outputMap.put("rentEquipmentList", rentEquipmentList);	
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		
		}
		if (errList.isEmpty()) {
			if("".equals(context.contextMap.get("RECT_ID")) ||null==context.contextMap.get("RECT_ID")){
				Output.jspOutput(outputMap, context, "/lockManagement/lockPactForCreditMDF.jsp");
			}else{
				Output.jspOutput(outputMap, context, "/lockManagement/lockPactForMDF.jsp");
			}
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
}

	@SuppressWarnings("unchecked")
	public void queryRentForMDFLock(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		PagingInfo<Object> dw = null;

		List lockList=null;
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				dw = baseService.queryForListWithPaging("rentContract.queryRentContractByModifyLockCode", context.contextMap, "PRCD_ID", ORDER_TYPE.DESC);
				
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
		outputMap.put("QSELECT_STATUS", context.contextMap.get("QSELECT_STATUS"));
		outputMap.put("RENTSTAUTS", context.contextMap.get("RENTSTAUTS"));
		outputMap.put("lockList", lockList);

		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/lockManagement/modifyLockManage.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}
	
	/**
	 * 修改锁码方式并记录日志
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void modifyLockCodeByEqmtID(Context context) {
		
		Map outputMap = new HashMap();
		SqlMapClient sqlClient = DataAccessor.getSession();
		List errList = context.errList;
		Map rsMap = null;
		Map eqmtDetailMap=null;
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {
				sqlClient.startTransaction();
				if("".equals(context.contextMap.get("RECT_ID")) ||null==context.contextMap.get("RECT_ID")){
					sqlClient.update("rentContractPact.updateCreditLockCodeByEqmtId", context.contextMap);
					sqlClient.insert("rentContractPact.insertMDFLockCodeLog", context.contextMap);
					eqmtDetailMap = (Map) DataAccessor.query("rentContractPact.selectCreditEqmtDetail", context.contextMap, DataAccessor.RS_TYPE.MAP);
				}else{
					sqlClient.update("rentContractPact.updateLockCodeByEqmtId", context.contextMap);
					sqlClient.insert("rentContractPact.insertMDFLockCodeLog", context.contextMap);
					rsMap = (Map) DataAccessor.query("rentContractPact.selectCreditIDForMDF", context.contextMap, DataAccessor.RS_TYPE.MAP);
					rsMap.put("LOCK_CODE",context.contextMap.get("LOCK_CODE"));
					sqlClient.update("rentContractPact.updateCreditLockCodeByCreditId", rsMap);
					eqmtDetailMap = (Map) DataAccessor.query("rentContractPact.selectCreditIDForMDF", context.contextMap, DataAccessor.RS_TYPE.MAP);
				}
				
				sqlClient.commitTransaction();
				
				String msg="合同号："+eqmtDetailMap.get("LEASE_CODE")+"，设备名称："+eqmtDetailMap.get("THING_NAME")+"，设备型号："+eqmtDetailMap.get("MODEL_SPEC")
						+"的锁码方式变更为："+context.contextMap.get("LOCK_CODE_TEXT");
				//发送锁码修改邮件
				MailSettingTo mailSettingTo=new MailSettingTo();
				mailSettingTo.setEmailFrom("tacfinance_service@tacleasing.cn");
				mailSettingTo.setEmailSubject("锁码修改信息通知 ");
				mailSettingTo.setEmailContent(msg);
				mailSettingTo.setCreateBy("184");//184是系统

				this.mailUtilService.sendMail(231,mailSettingTo);
			} catch (Exception e) {
				logger.info("--------  修改锁码错误--------");
				e.printStackTrace();
				errList.add("修改锁码错误");
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} finally {
				try {
					sqlClient.endTransaction();
				} catch (SQLException e) {
					errList.add("修改锁码错误 关闭事物错误");
					logger.info("--------  修改锁码错误 关闭事物错误");
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
			}
			
		}
		
		if(errList.isEmpty()){
			Output.jsonOutput(outputMap, context);
		}else{
			outputMap.put("errList", errList);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void queryMDFLockCodeLog(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		DataWrap dw = null;

		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				dw = (DataWrap) DataAccessor.query("rentContractPact.selectLockCodeMDFLog", context.contextMap, DataAccessor.RS_TYPE.PAGED);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}

		/*-------- output --------*/
		outputMap.put("dw", dw);

		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/lockManagement/queryLockCodeMDFLog.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}
	
	
	/*
	 * add by Michael 2012 07-06 
	 * For 修改设备放置地查询报告使用
	 */
	@SuppressWarnings("unchecked")
	public void queryRentForMDFEqmtAddress(Context context) {
		
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
				
				dw = (DataWrap) DataAccessor.query("rentContract.queryRentContract", context.contextMap, DataAccessor.RS_TYPE.PAGED);
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
		outputMap.put("QSELECT_STATUS", context.contextMap.get("QSELECT_STATUS"));
		outputMap.put("RENTSTAUTS", context.contextMap.get("RENTSTAUTS"));
		outputMap.put("RECP_STATUS", context.contextMap.get("RECP_STATUS")) ;
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/rentcontract/queryRentContractForMDFEqmtAddr.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/*
	 * add by Michael 2012 07-06 
	 * For 修改设备放置地查询报告使用
	 */
	public void showEqmtAddrForMDF(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map rsMap;
	
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {
				outputMap.put("LEASE_CODE", context.contextMap.get("LEASE_CODE"));

				rsMap = (Map) DataAccessor.query("rentContract.queryEqmtAddrForMDF", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("rsMap", rsMap);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		
		}
		
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/rentcontract/eqmtAddrPactForMDF.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

}
	
	// Add by Michael 2012 7-06 For 修改设备放置地修改
	public void updateRentEqmtAddr(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;


		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			SqlMapClient sqlMap = DataAccessor.getSession();
			try {
				sqlMap.startTransaction();

				baseService.insertActionLog(context, "修改设备放置地", 
						"修改员工：[" + context.contextMap.get("userName") + "],将合同"+context.contextMap.get("LEASE_CODE")+"设备放置地由"+context.contextMap.get("EQUPMENT_ADDRESS_OLD")+"   改为   "+context.contextMap.get("EQUPMENT_ADDRESS"));
				DataAccessor.getSession().update("rentContract.updateRentContractEqmtAddr", context.contextMap);
				DataAccessor.getSession().update("rentContract.updatePrjtCreditEqmtAddr", context.contextMap);
				DataAccessor.getSession().update("rentContract.updateRentCollectionEqmtAddr", context.contextMap);
				
				/* Yang Yun 2012/11/29 修改保单异常状态*/
				context.contextMap.put("list_type", "30");
				sqlMap.update("insurance.updateInsuExceptionStatus", context.contextMap);
				//查出保险公司的邮箱地址
				String incpMail = (String) sqlMap.queryForObject("insurance.getIncpEmailByLeaseCode", context.contextMap);
				
				MailSettingTo mailSettingTo = new MailSettingTo();
				
				mailSettingTo.setEmailSubject("修改设备放置地");
				Map<String, Object> data = (Map<String, Object>) DataAccessor.getSession().queryForObject("insurance.getEqmtInfoForChangeEqmtAddr", context.contextMap);
				if (data != null && data.size() > 1) {
					StringBuffer sb = new StringBuffer("<html>" +
							"<head><style type=\"text/css\">" +
							"#dataBody td {border: 1px solid #A6C9E2;} " +
							"#dataBody th {border: 1px solid white;background-color: #A6C9E2;} " +
							"#dataBody table {background-color: white;border: 1px solid #A6C9E2;}" +
							"</style></head>" +
							"<body><div id='dataBody'>");
					sb.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"3\" style=\"width: 1200px;\">");
					sb.append("<tr>");
					sb.append("<th>保单号</th>");
					sb.append("<th>保险公司</th>");
					sb.append("<th>承租人</th>");
					sb.append("<th>新租赁物放置地</th>");
					sb.append("</tr>");
					sb.append("<tr>");
					sb.append("<td>" + data.get("INCU_CODE") + "&nbsp;</td>");
					sb.append("<td>" + data.get("INCP_NAME") + "&nbsp;</td>");
					sb.append("<td>" + data.get("CUST_NAME") + "&nbsp;</td>");
					sb.append("<td>" + context.contextMap.get("EQUPMENT_ADDRESS") + "&nbsp;</td>");
					sb.append("</tr>");
					sb.append("</table>");
					mailSettingTo.setEmailContent(sb.toString());
					mailSettingTo.setCreateBy(String.valueOf(context.contextMap.get("s_employeeId")));
					mailSettingTo.setEmailTo(incpMail);
					mailUtilService.sendMail(200,mailSettingTo);
				}
				sqlMap.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}finally {
				try {
					sqlMap.endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		
		}
		if(errList.isEmpty()){
			outputMap.put("returnStr", "修改成功！");
			Output.jsonOutput(outputMap, context);
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	public void saveExpectedDate(Context context) {
		
		context.contextMap.put("PAY_DETAIL_ID",0);
		
		try {
			DataAccessor.execute("rentContract.insertExpectedDate",context.contextMap,OPERATION_TYPE.INSERT);
		}catch (Exception e) {
			e.printStackTrace();
			logger.debug("更新预估拨款日期出错!");
			Output.jsonFlageOutput(false,context);
		}
		Output.jsonFlageOutput(true,context);
	}

	public void queryRentFileSenderFollow(Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		PagingInfo<Object> dw = null;

		Map rsMap = null;
		Map paramMap = new HashMap();
		paramMap.put("id", context.contextMap.get("s_employeeId"));
		List dispatch_user=null;
		boolean hwReception=false;
		boolean hwReject=false;
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				rsMap = (Map) DataAccessor.query("employee.getEmpInforById", paramMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("p_usernode", rsMap.get("NODE"));
				
				dw = baseService.queryForListWithPaging("rentContract.queryRentFileSenderFollow", context.contextMap, "MODIFY_TIME", ORDER_TYPE.DESC);
				dispatch_user = (List) DataAccessor.query("rentContract.getAllDispatchUser", context.contextMap, DataAccessor.RS_TYPE.LIST);
				
				List<String> resourceIdList=(List<String>) DataAccessor.query("supplier.getResourceIdListByEmplId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				for(int i=0;resourceIdList!=null&&i<resourceIdList.size();i++) {
					if("281".equals(resourceIdList.get(i))) {  //业管收件
						hwReception=true;
					}
					if("282".equals(resourceIdList.get(i))) {  //业管退件
						hwReject=true;
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
		outputMap.put("QSELECT_STATUS", context.contextMap.get("QSELECT_STATUS"));
		outputMap.put("dispatch_user", dispatch_user);
		outputMap.put("hwReception", hwReception);
		outputMap.put("hwReject", hwReject);
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/rentcontract/queryRentFileSenderFollow.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	// Add by Michael 2012 11-14 For 文审收件
	public void receptionRentFile(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			SqlMapClient sqlMap = DataAccessor.getSession();
			try {
				sqlMap.startTransaction();
				
				String user_id = (String) DataAccessor.query(
						"rentContract.getRentFileDispatchUserByPayMoneyPass", context.contextMap,
						DataAccessor.RS_TYPE.OBJECT);
				if(user_id!=null && ! user_id.equals("")){
					context.contextMap.put("DISPATCH_USER_ID", user_id);
				}else{
					//TODO
					if("3".equals(context.contextMap.get("CONTRACT_TYPE")) || "4".equals(context.contextMap.get("CONTRACT_TYPE"))){
						user_id = (String) DataAccessor.query(
								"rentContract.getRentFileDispatchUserByAutoType", context.contextMap,
								DataAccessor.RS_TYPE.OBJECT);
					}else if("8".equals(context.contextMap.get("CONTRACT_TYPE")) 
							||"10".equals(context.contextMap.get("CONTRACT_TYPE")) 
							||"12".equals(context.contextMap.get("CONTRACT_TYPE"))
							||"13".equals(context.contextMap.get("CONTRACT_TYPE"))
							||"14".equals(context.contextMap.get("CONTRACT_TYPE"))){
						user_id = (String) DataAccessor.query(
								"rentContract.getRentFileDispatchUserByCarType", context.contextMap,
								DataAccessor.RS_TYPE.OBJECT);
					}else{
						user_id = (String) DataAccessor.query(
								"rentContract.getRentFileDispatchUserByRentType", context.contextMap,
								DataAccessor.RS_TYPE.OBJECT);
					}
					
					if("".equals(user_id)||null==user_id){
						throw new Exception("没有文审人员可以分派！");
					}
					
					context.contextMap.put("DISPATCH_USER_ID", user_id);
				}

				//更新分派user 的最新事件
				sqlMap.update("rentContract.updateDispatchUserDatetime",context.contextMap);
				sqlMap.update("rentContract.updateRentFileStateByHWReception", context.contextMap);

				sqlMap.commitTransaction();
				super.addSysLog(Long.valueOf(context.contextMap
						.get("CREDIT_ID").toString()), Long
								.parseLong(StringUtils
										.isEmpty((String) context.contextMap
												.get("RECT_ID")) ? "0"
										: (String) context.contextMap
												.get("RECT_ID")), "业管收件", "业管收件",
						"", "业管文审已确认收件", DataUtil.longUtil(context.contextMap
								.get("s_employeeId")),(String)context.contextMap.get("IP"));
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}finally {
				try {
					sqlMap.endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		
		}
		if(errList.isEmpty()){
			Output.jspSendRedirect(context,
					"defaultDispatcher?__action=rentContract.queryRentFileSenderFollow");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	// Add by Michael 2012 11-14 For 文审退件
	public void rejectRentFile(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			SqlMapClient sqlMap = DataAccessor.getSession();
			try {
				sqlMap.startTransaction();

				sqlMap.update("rentContract.updateRentFileStateByHWReject", context.contextMap);

				sqlMap.commitTransaction();
				super.addSysLog(Long.valueOf(context.contextMap
						.get("CREDIT_ID").toString()), Long
								.parseLong(StringUtils
										.isEmpty((String) context.contextMap
												.get("RECT_ID")) ? "0"
										: (String) context.contextMap
												.get("RECT_ID")), "业管收件", "业管收件",
						"", "业管文审已退件", DataUtil.longUtil(context.contextMap
								.get("s_employeeId")),(String)context.contextMap.get("IP"));
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}finally {
				try {
					sqlMap.endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		if(errList.isEmpty()){
			Output.jspSendRedirect(context,
					"defaultDispatcher?__action=rentContract.queryRentFileSenderFollow");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	/**
	 * 支票管理页面查询
	 * @param context
	 */
	public void queryRentCheckManage (Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		PagingInfo<Object> dw = null;

		Map rsMap = null;
		Map paramMap = new HashMap();
		paramMap.put("id", context.contextMap.get("s_employeeId"));
		Boolean salesKeyin=false;
		Boolean hwExam=false;
		Boolean financeExam=false;
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				rsMap = (Map) DataAccessor.query("employee.getEmpInforById", paramMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("p_usernode", rsMap.get("NODE"));
				
				dw = baseService.queryForListWithPaging("rentContract.querySchemaCheck", context.contextMap, "WIND_RESULT_DATE", ORDER_TYPE.DESC);
	
				// 支票录入、业管审核、财务审核权限
				List<String> resourceIdList=(List<String>) DataAccessor.query("supplier.getResourceIdListByEmplId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				for(int i=0;resourceIdList!=null&&i<resourceIdList.size();i++) {
					if("291".equals(resourceIdList.get(i))) {
						salesKeyin=true;
					}
					if("290".equals(resourceIdList.get(i))) {
						hwExam=true;
					}
					if("292".equals(resourceIdList.get(i))) {
						financeExam=true;
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
		outputMap.put("salesKeyin", salesKeyin);
		outputMap.put("hwExam", hwExam);
		outputMap.put("financeExam", financeExam);
		
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/rentCheckManage/queryRentCheckManage.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/**
	 * 初始化创建支票明细
	 * @param context
	 */
	public void initRentCheck (Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		List checkManagePayLines=null;
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				context.contextMap.put("credit_id", context.contextMap.get("PRCD_ID"));
				if("SALES".equals(context.contextMap.get("TYPE"))){
					checkManagePayLines=(List) DataAccessor.query("riskAudit.queryCheckManagePaylines",context.contextMap, DataAccessor.RS_TYPE.LIST);
				}else{
					checkManagePayLines=(List) DataAccessor.query("riskAudit.queryAllCheckManagePaylines",context.contextMap, DataAccessor.RS_TYPE.LIST);
				}
				
				Map schemeMap=(Map) DataAccessor.query("riskAudit.selectCreditScheme",context.contextMap, DataAccessor.RS_TYPE.MAP);
				List checkPayLines=null;
				List memoList =null;
				List checkTypeList = null;
				if(checkManagePayLines.size()==0){
					//含税租金
					List<Map> irrMonthPaylines = StartPayService.queryPackagePayline(context.contextMap.get("PRCD_ID"), Integer.valueOf(1));
					Map<String, Object> paylist = new HashMap<String, Object>();
					paylist.put("TOTAL_VALUEADDED_TAX", schemeMap.get("TOTAL_VALUEADDED_TAX"));
					paylist.put("LEASE_PERIOD", schemeMap.get("LEASE_TERM"));
					paylist.put("oldirrMonthPaylines", irrMonthPaylines);
					StartPayService.packagePaylinesForValueAdded(paylist);
					List newIrrMonthPaylines = (List) paylist.get("irrMonthPaylines");
	
					List checkPaySchema=(List) DataAccessor.query("riskAudit.getCheckPaylines",context.contextMap, DataAccessor.RS_TYPE.LIST);
					//含税租金明细
					List<Map> payLinesList=StartPayService.upPackagePaylinesForValueTax(newIrrMonthPaylines);
					//支票支付明细
					List<Map> checkPayLinesTemp=StartPayService.upRentCheckPayLines(checkPaySchema);
		
					checkPayLines= new ArrayList();
					Map checkPayLineMap=null;
					int period_num=0;
					for (Map map : checkPayLinesTemp) {
						period_num=DataUtil.intUtil(map.get("PERIOD_NUM"));
						if(period_num==DataUtil.intUtil(((Map)payLinesList.get(period_num-1)).get("PERIOD_NUM"))){
							checkPayLineMap=new HashMap();
							checkPayLineMap.put("PERIOD_NUM", ((Map)payLinesList.get(period_num-1)).get("PERIOD_NUM"));
							checkPayLineMap.put("MONTH_PRICE_TAX", ((Map)payLinesList.get(period_num-1)).get("MONTH_PRICE_TAX"));
							checkPayLines.add(checkPayLineMap);
						}
					}
				}
				outputMap.put("checkManagePayLines", checkManagePayLines);
				
				memoList=(List)DictionaryUtil.getDictionary("支票备注说明");
				outputMap.put("memoList", memoList);
				checkTypeList=(List)DictionaryUtil.getDictionary("支票类别");
				outputMap.put("checkTypeList", checkTypeList);
				outputMap.put("TYPE", context.contextMap.get("TYPE"));
				outputMap.put("checkPayLines", checkPayLines);
				outputMap.put("LEASE_CODE", context.contextMap.get("LEASE_CODE"));
				outputMap.put("CUST_NAME", context.contextMap.get("CUST_NAME"));
				outputMap.put("credit_id", context.contextMap.get("credit_id"));
				outputMap.put("schemeMap", context.contextMap.get("schemeMap"));
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		
		if(errList.isEmpty()){
			if("SALES".equals(context.contextMap.get("TYPE"))){
				Output.jspOutput(outputMap, context, "/rentCheckManage/initRentCheck.jsp");
			}else if("HW".equals(context.contextMap.get("TYPE"))){
				Output.jspOutput(outputMap, context, "/rentCheckManage/rentCheckHWExam.jsp");
			}else if("FINANCE".equals(context.contextMap.get("TYPE"))){
				Output.jspOutput(outputMap, context, "/rentCheckManage/rentCheckFinanceExam.jsp");
			}else if("SHOW".equals(context.contextMap.get("TYPE"))){
				Output.jspOutput(outputMap, context, "/rentCheckManage/rentCheckDetailForShow.jsp");
			}
			//Output.jspSendRedirect(context, "defaultDispatcher?__action=rentContract.queryRentCheckManage");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	//Add by Michael 2012 12-12 For 创建支票明细、业管审核、财务审核
	public void createRentCheckPayLines (Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		SqlMapClient sqlMap = DataAccessor.getSession();
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				sqlMap.startTransaction();
				if("SALES".equals(context.contextMap.get("TYPE"))){
					String[] PERIOD_NUM = HTMLUtil.getParameterValues(context.request, "PERIOD_NUM", "");
					String[] MONTH_PRICE_TAX = HTMLUtil.getParameterValues(context.request, "MONTH_PRICE_TAX", "");
					String[] CHECKOUT_DATE = HTMLUtil.getParameterValues(context.request, "CHECKOUT_DATE", "");
					String[] CHECK_NUM = HTMLUtil.getParameterValues(context.request, "CHECK_NUM", "");
					String[] CHECK_MONEY = HTMLUtil.getParameterValues(context.request, "CHECK_MONEY", "");
					String[] MEMO = HTMLUtil.getParameterValues(context.request, "MEMO", "");
					String[] ID= HTMLUtil.getParameterValues(context.request, "ID", "");
					String[] IDS= HTMLUtil.getParameterValues(context.request, "IDS", "");
					String[] DRAWER= HTMLUtil.getParameterValues(context.request, "DRAWER", "");
					Map map = new HashMap();
					for (int i=0;i<PERIOD_NUM.length;i++) {
						map.put("CREDIT_ID", context.contextMap.get("credit_id"));
						map.put("PERIOD_NUM", PERIOD_NUM[i]);  
						map.put("MONTH_PRICE_TAX", MONTH_PRICE_TAX[i]); 
						map.put("CHECKOUT_DATE", CHECKOUT_DATE[i]); 
						map.put("CHECK_NUM", CHECK_NUM[i]); 
						map.put("CHECK_MONEY", CHECK_MONEY[i]); 
						map.put("MEMO", MEMO[i]); 
						map.put("s_employeeId", context.contextMap.get("s_employeeId"));
						map.put("ID", ID[i]);
						map.put("DRAWER", DRAWER[i]);
						map.put("TYPE", "0");
						if(!ID[i].equals("")){
							map.put("REMARK", "更新");
							sqlMap.update("rentContract.updateRentCheckPayLines", map);
						}else{
							map.put("REMARK", "新增");
							Long longID=(Long) sqlMap.insert("rentContract.createRentCheckPayLines", map);
							map.put("ID", longID);
						}
						sqlMap.insert("rentContract.createRentCheckPayManageLog", map);
					}
					//如果ID有减少说明有支票被删除
					if(IDS.length>ID.length){
						//判断出 哪些ID有减少
						Set setIDS = new HashSet(Arrays.asList(IDS));        
						Set setID = new HashSet(Arrays.asList(ID)); 
						setIDS.removeAll(setID);
						String[] reduceID=(String[]) setIDS.toArray();
						Map tempCheckPayLines=null;
						for (int i=0;i<reduceID.length;i++) {
							map.put("ID", reduceID[i]);
							sqlMap.update("rentContract.deleteRentCheckPayLines", map);
							tempCheckPayLines=(Map) sqlMap.queryForObject("rentContract.getCheckPaylines",map, DataAccessor.RS_TYPE.MAP);
							tempCheckPayLines.put("REMARK", "删除");
							sqlMap.insert("rentContract.createRentCheckPayManageLog", tempCheckPayLines);
						}
					}
				}else if("HW".equals(context.contextMap.get("TYPE"))){
					String[] PERIOD_NUM = HTMLUtil.getParameterValues(context.request, "PERIOD_NUM", "");
					String[] MONTH_PRICE_TAX = HTMLUtil.getParameterValues(context.request, "MONTH_PRICE_TAX", "");
					String[] CHECKOUT_DATE = HTMLUtil.getParameterValues(context.request, "CHECKOUT_DATE", "");
					String[] CHECK_NUM = HTMLUtil.getParameterValues(context.request, "CHECK_NUM", "");
					String[] CHECK_MONEY = HTMLUtil.getParameterValues(context.request, "CHECK_MONEY", "");
					String[] MEMO = HTMLUtil.getParameterValues(context.request, "MEMO", "");
					String[] ID= HTMLUtil.getParameterValues(context.request, "ID", "");
					String[] DRAWER= HTMLUtil.getParameterValues(context.request, "DRAWER", "");
					Map map = new HashMap();
					for (int i=0;i<PERIOD_NUM.length;i++) {
						map.put("CREDIT_ID", context.contextMap.get("credit_id"));
						map.put("PERIOD_NUM", PERIOD_NUM[i]);  
						map.put("MONTH_PRICE_TAX", MONTH_PRICE_TAX[i]); 
						map.put("CHECKOUT_DATE", CHECKOUT_DATE[i]); 
						map.put("CHECK_NUM", CHECK_NUM[i]); 
						map.put("CHECK_MONEY", CHECK_MONEY[i]); 
						map.put("MEMO", MEMO[i]); 
						map.put("ID", ID[i]);
						map.put("DRAWER", DRAWER[i]);
						map.put("s_employeeId", context.contextMap.get("s_employeeId"));
						map.put("REMARK", "业管确认");
						sqlMap.update("rentContract.updateRentCheckPayLinesByHW", map);
						sqlMap.insert("rentContract.createRentCheckPayManageLog", map);
					}
					//更新支票业管审核的状况为 1
					sqlMap.update("rentContract.updateRentCheckAllStateByHW", context.contextMap);
							
				}else if("FINANCE".equals(context.contextMap.get("TYPE"))){
					String[] PERIOD_NUM = HTMLUtil.getParameterValues(context.request, "PERIOD_NUM", "");
					String[] MONTH_PRICE_TAX = HTMLUtil.getParameterValues(context.request, "MONTH_PRICE_TAX", "");
					String[] CHECKOUT_DATE = HTMLUtil.getParameterValues(context.request, "CHECKOUT_DATE", "");
					String[] CHECK_NUM = HTMLUtil.getParameterValues(context.request, "CHECK_NUM", "");
					String[] CHECK_MONEY = HTMLUtil.getParameterValues(context.request, "CHECK_MONEY", "");
					String[] MEMO = HTMLUtil.getParameterValues(context.request, "MEMO", "");
					String[] ID= HTMLUtil.getParameterValues(context.request, "ID", "");
					String[] DRAWER= HTMLUtil.getParameterValues(context.request, "DRAWER", "");
					Map map = new HashMap();
					for (int i=0;i<PERIOD_NUM.length;i++) {
						map.put("CREDIT_ID", context.contextMap.get("credit_id"));
						map.put("PERIOD_NUM", PERIOD_NUM[i]);  
						map.put("MONTH_PRICE_TAX", MONTH_PRICE_TAX[i]); 
						map.put("CHECKOUT_DATE", CHECKOUT_DATE[i]); 
						map.put("CHECK_NUM", CHECK_NUM[i]); 
						map.put("CHECK_MONEY", CHECK_MONEY[i]); 
						map.put("MEMO", MEMO[i]); 
						map.put("ID", ID[i]);
						map.put("DRAWER", DRAWER[i]);
						map.put("s_employeeId", context.contextMap.get("s_employeeId"));
						map.put("REMARK", "财务确认");
						sqlMap.update("rentContract.updateRentCheckPayLinesByFinance", map);
						sqlMap.insert("rentContract.createRentCheckPayManageLog", map);
					}
					sqlMap.update("rentContract.updateRentCheckAllStateByFinance", context.contextMap);
				}
					
				sqlMap.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}finally {
				try {
					sqlMap.endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
					errList.add(e);
				}
			}
		}
		
		if(errList.isEmpty()){
			//Output.jspOutput(outputMap, context, "/rentcontract/initRentCheck.jsp");
			Output.jspSendRedirect(context, "defaultDispatcher?__action=rentContract.queryRentCheckManage");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	public void queryCheckPaySchema(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {
				List checkPaySchema=(List) DataAccessor.query("riskAudit.getCheckPaylines",context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("checkPaySchema", checkPaySchema);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		
		}
		
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/rentCheckManage/rentCheckSchema.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	/**
	 * 支票明细管理页面查询
	 * @param context
	 */
	public void queryRentCheckManageDetail (Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		PagingInfo<Object> dw = null;
		Boolean checkDetailManage=false;
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				
				dw = baseService.queryForListWithPaging("rentContract.queryAllRentCheckDetail", context.contextMap, "CHECK_OUT_DATE", ORDER_TYPE.ASC);
				
				// 支票明细管理 权限
				List<String> resourceIdList=(List<String>) DataAccessor.query("supplier.getResourceIdListByEmplId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				for(int i=0;resourceIdList!=null&&i<resourceIdList.size();i++) {
					if("293".equals(resourceIdList.get(i))) {
						checkDetailManage=true;
					}
				}
				
				context.contextMap.put("dataType", "支票退票原因");
				outputMap.put("RETURN_REASON_LIST", (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST));
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		/*-------- output --------*/
		outputMap.put("dw", dw);
		outputMap.put("checkDetailManage", checkDetailManage);
		outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
		outputMap.put("QSTART_DATE", context.contextMap.get("QSTART_DATE"));
		outputMap.put("QEND_DATE", context.contextMap.get("QEND_DATE"));
		outputMap.put("DELIVERY_STATUS", context.contextMap.get("DELIVERY_STATUS"));
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/rentCheckManage/queryRentCheckDetailManage.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	//查询支票管理日志
	public void queryCheckPayManageLog(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {
				//此支票关联的合同信息
				List checkPayLeaseCode=(List) baseService.queryForList("rentContract.queryRentCheckLeaseCode",context.contextMap);
				List checkPayManageLog=(List) baseService.queryForList("rentContract.queryRentCheckManageLogByID",context.contextMap);
				//查询上传的附件
				List checkFileUpList=(List)baseService.queryForList("rentFile.selectRentCheckDetailDoc", context.contextMap);
				outputMap.put("checkPayManageLog", checkPayManageLog);
				outputMap.put("checkFileUpList", checkFileUpList);
				outputMap.put("checkPayLeaseCode", checkPayLeaseCode);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/rentCheckManage/rentCheckDetailManageLog.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	//支票投递、入账、退票操作管理
	public void manageCheckPayDetail(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		SqlMapClient sqlMap = DataAccessor.getSession();
		String remark="";
		/**
		 * STAE 状态说明
		 * 0:正常  （待投递）
		 * 1：作废
		 * 2：投递（已投递）
		 * 3：入账（已入账）
		 * 4：退票
		 * 5：转移
		 * 6：接受
		 * 7：退回
		 * 8：确认退回
		 */
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				sqlMap.startTransaction();
				
				if(!"".equals(context.contextMap.get("MEMO"))||null!=context.contextMap.get("MEMO")){
					context.contextMap.put("FINANCE_MEMO", context.contextMap.get("MEMO")+"<br>");
				}else{
					context.contextMap.put("FINANCE_MEMO", context.contextMap.get("MEMO"));
				}
				
				if("DELETE".equals(context.contextMap.get("TYPE"))){  //作废
					context.contextMap.put("ACTION_TYPE", "支票作废");
					remark=context.contextMap.get("DELIVERY_DATE")+"支票作废。备注说明："+context.contextMap.get("MEMO");
					context.contextMap.put("STATE", 1);					
					sqlMap.update("rentContract.deleteRentCheck", context.contextMap);
				}else if("DELIVERY".equals(context.contextMap.get("TYPE"))){  //投递
					String bankCode = (String) context.contextMap.get("DELIVERY_BANK");
					context.contextMap.put("ACTION_TYPE", "支票投递");
					remark=context.contextMap.get("DELIVERY_DATE")+"支票投递。备注说明："+context.contextMap.get("MEMO")+";投递银行："+DictionaryUtil.getFlag("支票投递银行", bankCode);
					
					String companyCode =  (String) this.baseService.queryForObj("rentContract.getCompanyCodeByCheckId", context.contextMap);
					if("2".equals(companyCode)){//裕国 邮件提醒
						MailSettingTo mailSettingTo=new MailSettingTo();
						mailSettingTo.setEmailSubject("裕国支票投递邮件提醒 ");
						mailSettingTo.setEmailContent("支票号码："+ context.contextMap.get("CHECK_NUM")+","+remark);
						mailSettingTo.setCreateBy("184");//184是系统
						this.mailUtilService.sendMail(2014,mailSettingTo);						
					}
					
					context.contextMap.put("STATE", 2);	
					sqlMap.update("rentContract.updateRentCheckDelivery", context.contextMap);

				}else if("RECORDED".equals(context.contextMap.get("TYPE"))){  //入账
					context.contextMap.put("ACTION_TYPE", "支票入账");
					remark=context.contextMap.get("DELIVERY_DATE")+"支票入账。备注说明："+context.contextMap.get("MEMO");
					context.contextMap.put("RECORDED_STATUS", 1);
					context.contextMap.put("STATE", 3);	
					sqlMap.update("rentContract.updateRentCheckRecorded", context.contextMap);
				}else if("RETURN".equals(context.contextMap.get("TYPE"))){   //退票
					context.contextMap.put("ACTION_TYPE", "支票退票");
					remark=context.contextMap.get("DELIVERY_DATE")+"支票退票。退票原因："+context.contextMap.get("RETURN_REASON_TEXT")+"。备注说明："+context.contextMap.get("MEMO");
					context.contextMap.put("RECORDED_STATUS", 2);
					context.contextMap.put("STATE", 4);
					context.contextMap.put("RETURN_REASON", context.contextMap.get("RETURN_REASON"));
					sqlMap.update("rentContract.updateRentCheckRecorded", context.contextMap);
					
					//addDunRecord逾期催收插入信息					 
					Map checkInfo = (Map) sqlMap.queryForObject("rentContract.getCheckInfo", context.contextMap);
					String cust_code = (String) checkInfo.get("CUST_CODE");
					String content = "[支票退票通知]:支票号码: " + String.valueOf(checkInfo.get("CHECK_NUM")) +",";
					content += "出票日期: " + String.valueOf(checkInfo.get("CHECK_OUT_DATE")) +",";
					content += "退票日期: " + DateUtil.dateToStr(new Date()) +",";
					content += "退票原因: " + context.contextMap.get("RETURN_REASON_TEXT") +",";
					content += "退票金额: " + String.valueOf(checkInfo.get("CHECK_MONEY")) +"。";
					Map dunRecord =  new HashMap();
					dunRecord.put("clerk_id", null);
					dunRecord.put("ANSWERPHONE_NAME", "000");
					dunRecord.put("PHONE_NUMBER", "000");
					dunRecord.put("RESULT", 16);
					dunRecord.put("CALL_CONTENT", content);
					dunRecord.put("CUST_CODE", cust_code);
					sqlMap.insert("dunTask.addDunRecord", dunRecord);					
				}else if("TRANSFER".equals(context.contextMap.get("TYPE"))){   //转移
					context.contextMap.put("ACTION_TYPE", "支票转移");
					remark=context.contextMap.get("DELIVERY_DATE")+"支票转移。转移到："+context.contextMap.get("TRANSFER_TO")+"。备注说明："+context.contextMap.get("MEMO");
					context.contextMap.put("STATE", 5);
					context.contextMap.put("TRANSFER_TO", context.contextMap.get("TRANSFER_TO"));
					sqlMap.update("rentContract.updateRentCheckState", context.contextMap);
				}else if("ACCEPT".equals(context.contextMap.get("TYPE"))){   //接收
					context.contextMap.put("ACTION_TYPE", "接收支票");
					remark=context.contextMap.get("DELIVERY_DATE")+"接收支票。备注说明："+context.contextMap.get("MEMO");
					context.contextMap.put("STATE", 6);
					sqlMap.update("rentContract.updateRentCheckState", context.contextMap);
				}else if("BACK".equals(context.contextMap.get("TYPE"))){   //退回
					context.contextMap.put("ACTION_TYPE", "支票退回");
					remark=context.contextMap.get("DELIVERY_DATE")+"支票退回。退回："+context.contextMap.get("BACK_TO")+"。备注说明："+context.contextMap.get("MEMO");
					context.contextMap.put("STATE", 7);
					context.contextMap.put("BACK_TO", context.contextMap.get("BACK_TO"));
					sqlMap.update("rentContract.updateRentCheckState", context.contextMap);
				}else if("BACKCONFIRM".equals(context.contextMap.get("TYPE"))){   //确认退回
					context.contextMap.put("ACTION_TYPE", "确认退回");
					remark=context.contextMap.get("DELIVERY_DATE")+"确认退回。"+"备注说明："+context.contextMap.get("MEMO");
					context.contextMap.put("STATE", 8);
					sqlMap.update("rentContract.updateRentCheckState", context.contextMap);
				}
				Map checkPayDetailMap=(Map) DataAccessor.query("rentContract.querySchemaCheckManageDetail",context.contextMap, DataAccessor.RS_TYPE.MAP);
				checkPayDetailMap.put("REMARK", remark);
				checkPayDetailMap.put("ACTION_TYPE", context.contextMap.get("ACTION_TYPE"));
				checkPayDetailMap.put("CHECKOUT_DATE",checkPayDetailMap.get("CHECK_OUT_DATE"));
				checkPayDetailMap.put("s_employeeId",context.contextMap.get("s_employeeId"));
				sqlMap.insert("rentContract.createRentCheckPayManageLog", checkPayDetailMap);
				
				sqlMap.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}finally {
				try {
					sqlMap.endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		
		}
		
		if (errList.isEmpty()) {
			Output.jspSendRedirect(context, "defaultDispatcher?__action=rentContract.queryAllRentCheckManageDetail");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/**
	 * @author michael
	 * 批量投递支票功能
	 * 2013-01-06
	 * @param context
	 */
	public void batchDelivery(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		SqlMapClient sqlMap = DataAccessor.getSession();
		String remark="";
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				//参数从前台获取需要进行批量投递的支票ID
				String strIDS=(String) context.contextMap.get("ids");
				String [] ids=strIDS.split(",");
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
				sqlMap.startTransaction();
				
				for(int i=0;i<ids.length;i++) {
					context.contextMap.put("ID", ids[i]);
					remark= sf.format(new Date())+"支票投递。批量投递通过;投递银行："+context.contextMap.get("DELIVERY_BANK_TEXT");
					
					
					String companyCode =  (String) this.baseService.queryForObj("rentContract.getCompanyCodeByCheckId", context.contextMap);
					
					if("2".equals(companyCode)){//裕国 邮件提醒
						String checkNum =  (String) this.baseService.queryForObj("rentContract.getCheckNumberByCheckId", context.contextMap);
						MailSettingTo mailSettingTo=new MailSettingTo();
						mailSettingTo.setEmailSubject("裕国支票投递邮件提醒 ");
						mailSettingTo.setEmailContent("支票号码："+ checkNum+","+remark);
						mailSettingTo.setCreateBy("184");//184是系统
						this.mailUtilService.sendMail(2014,mailSettingTo);						
					}
					
					
					context.contextMap.put("STATE", 2);	
					sqlMap.update("rentContract.updateRentCheckDelivery", context.contextMap);

					

					
					Map checkPayDetailMap=(Map) DataAccessor.query("rentContract.querySchemaCheckManageDetail",context.contextMap, DataAccessor.RS_TYPE.MAP);
					checkPayDetailMap.put("REMARK", remark);
					checkPayDetailMap.put("ACTION_TYPE", "支票投递");
					checkPayDetailMap.put("CHECKOUT_DATE",checkPayDetailMap.get("CHECK_OUT_DATE"));
					checkPayDetailMap.put("s_employeeId",context.contextMap.get("s_employeeId"));
					sqlMap.insert("rentContract.createRentCheckPayManageLog", checkPayDetailMap);
					
					

				}
				
				sqlMap.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}finally {
				try {
					sqlMap.endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		
		}
		
		if (errList.isEmpty()) {
			queryAllRentCheckManageDetail(context);
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/**
	 * 支票管理上传附件  2012 12-13
	 * @author michael 
	 * @param context
	 * @throws IOException
	 */
	public void uploadRentCheckDoc(Context context) throws IOException {
		List fileItems = (List) context.contextMap.get("uploadList");
		List errList = context.errList ;
		Map outputMap = new HashMap() ;
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		
		for (Iterator iterator = fileItems.iterator(); iterator.hasNext();) {
			FileItem fileItem = (FileItem) iterator.next();
			InputStream in = fileItem.getInputStream();
			if (!fileItem.getName().equals("")) {
				SqlMapClient sqlMapClient = DataAccessor.getSession();
				try {
					sqlMapClient.startTransaction();
					saveFileToDisk(context, fileItem, sqlMapClient);
					sqlMapClient.commitTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
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
		if(errList.isEmpty()){
			outputMap.put("returnStr", "上传成功");
			queryRentCheckManageDetail(context);
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}

	/**
	 * 保存文件到硬盘中 并将保存信息存入数据库
	 * 
	 * @param context
	 * @param fileItem
	 * @return
	 */
	public String saveFileToDisk(Context context, FileItem fileItem,
			SqlMapClient sqlMapClient) {
		String filePath = fileItem.getName();
		String type = filePath.substring(filePath.lastIndexOf(".") + 1);
		List errList = context.errList;
		Map contextMap = context.contextMap;
		String bootPath = null;
		bootPath = this.getUploadPath("rentCheckDetailDoc");
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
					contextMap.put("file_name", fileItem.getName());
					contextMap.put("title", "支票附件");

					sqlMapClient.insert(
							"rentFile.insertRentCheckDetailDoc", contextMap);
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
		return null;
	}
	
	/**
	 * 
	 * @return 读取upload-config.xml文件 获取保存根路径
	 */
	public String getUploadPath(String xmlPath) {
		String path = null;
		try {
			SAXReader reader = new SAXReader();
			Document document = reader.read(Resources
					.getResourceAsReader("config/upload-config.xml"));
			Element root = document.getRootElement();
			List nodes = root.elements("action");
			for (Iterator it = nodes.iterator(); it.hasNext();) {
				Element element = (Element) it.next();
				Element nameElement = element.element("name");
				String s = nameElement.getText();
				if (xmlPath.equals(s)) {
					Element pathElement = element.element("path");
					path = pathElement.getText();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		return path;
	}

	/**
	 * 
	 * @return 下载
	 */
	public void download(Context context) {
		String savaPath = (String) context.contextMap.get("path");
		String name = (String) context.contextMap.get("name");
		String bootPath = this.getUploadPath("rentCheckDetailDoc");
		String path = bootPath + savaPath;
		File file = new File(path);
		context.response.reset();
		context.response.setCharacterEncoding("gb2312");
		OutputStream output = null;
		FileInputStream fis = null;
		try {
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
	
	/**
	 * 2012-12-14
	 * For 录入其他支票管理页面
	 * @author michael
	 * @param context
	 */
	public void queryForKeyInOtherCheckManage (Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		PagingInfo<Object> dw = null;

		Map rsMap = null;
		Map paramMap = new HashMap();
		paramMap.put("id", context.contextMap.get("s_employeeId"));
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				rsMap = (Map) DataAccessor.query("employee.getEmpInforById", paramMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("p_usernode", rsMap.get("NODE"));
				
				dw = baseService.queryForListWithPaging("rentContract.queryRentContract", context.contextMap, "WIND_RESULT_DATE", ORDER_TYPE.DESC);
				
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
			Output.jspOutput(outputMap, context, "/rentCheckManage/queryForKeyInOtherCheck.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	public void queryRentCheckSchema (Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		List checkSchemaList=null;
		Map schemaMap=null;
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				
				checkSchemaList = baseService.queryForList("riskAudit.getCheckPaylinesForSales", context.contextMap);
				schemaMap=(Map) baseService.queryForObj("creditReportManage.selectCreditScheme", context.contextMap);
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		/*-------- output --------*/
		outputMap.put("checkSchemaList", checkSchemaList);
		outputMap.put("schemaMap", schemaMap);
		outputMap.put("LEASE_CODE", context.contextMap.get("LEASE_CODE"));
		outputMap.put("CUST_NAME", context.contextMap.get("CUST_NAME"));
		
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/rentCheckManage/keyInOtherCheckSchema.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	public void createRentCheckSchema (Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		List checkSchemaList=null;
		Map schemaMap=null;
		String[] CHECK_PAY_START = HTMLUtil.getParameterValues(context.request, "CHECK_PAY_START", "0");
		String[] CHECK_PAY_END = HTMLUtil.getParameterValues(context.request, "CHECK_PAY_END", "0");
		if(errList.isEmpty()){		
			try {
				for (int i=0; i<CHECK_PAY_START.length; i++) {
					Map paramMap = new HashMap();
					paramMap.put("CHECK_PAY_START", DataUtil.intUtil(CHECK_PAY_START[i]));
					paramMap.put("CHECK_PAY_END", DataUtil.intUtil(CHECK_PAY_END[i]));
					paramMap.put("credit_id", context.contextMap.get("credit_id"));
					paramMap.put("s_employeeId", context.contextMap.get("s_employeeId"));
					//TYPE 1:表示是自行输入 ；0：表示是审查录入
					paramMap.put("TYPE", "1");
					baseService.insert("riskAudit.addCreditSchemaCheck", paramMap);				
				}
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		if(errList.isEmpty()){
			queryForKeyInOtherCheckManage(context);
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	/**
	 * 初始化创建支票明细
	 * @param context
	 */
	public void initRentCheckForSales (Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				context.contextMap.put("credit_id", context.contextMap.get("PRCD_ID"));
				List checkManagePayLines=(List) DataAccessor.query("riskAudit.queryCheckManagePaylinesForSales",context.contextMap, DataAccessor.RS_TYPE.LIST);
				Map schemeMap=(Map) DataAccessor.query("riskAudit.selectCreditScheme",context.contextMap, DataAccessor.RS_TYPE.MAP);
				List checkPayLines=null;
				List memoList =null;
				List checkTypeList= null;
				//含税租金
				List<Map> irrMonthPaylines = StartPayService.queryPackagePayline(context.contextMap.get("PRCD_ID"), Integer.valueOf(1));
				Map<String, Object> paylist = new HashMap<String, Object>();
				paylist.put("TOTAL_VALUEADDED_TAX", schemeMap.get("TOTAL_VALUEADDED_TAX"));
				paylist.put("LEASE_PERIOD", schemeMap.get("LEASE_TERM"));
				paylist.put("oldirrMonthPaylines", irrMonthPaylines);
				StartPayService.packagePaylinesForValueAdded(paylist);
				List newIrrMonthPaylines = (List) paylist.get("irrMonthPaylines");

				List checkPaySchema=(List) DataAccessor.query("riskAudit.getCheckPaylinesForSales",context.contextMap, DataAccessor.RS_TYPE.LIST);
				//含税租金明细
				List<Map> payLinesList=StartPayService.upPackagePaylinesForValueTax(newIrrMonthPaylines);
				//支票支付明细
				List<Map> checkPayLinesTemp=StartPayService.upRentCheckPayLines(checkPaySchema);
	
				checkPayLines= new ArrayList();
				Map checkPayLineMap=null;
				int period_num=0;
				for (Map map : checkPayLinesTemp) {
					period_num=DataUtil.intUtil(map.get("PERIOD_NUM"));
					if(period_num==DataUtil.intUtil(((Map)payLinesList.get(period_num-1)).get("PERIOD_NUM"))){
						checkPayLineMap=new HashMap();
						checkPayLineMap.put("PERIOD_NUM", ((Map)payLinesList.get(period_num-1)).get("PERIOD_NUM"));
						checkPayLineMap.put("MONTH_PRICE_TAX", ((Map)payLinesList.get(period_num-1)).get("MONTH_PRICE_TAX"));
						checkPayLines.add(checkPayLineMap);
					}
				}

				if(checkPayLines.size()>=checkManagePayLines.size()){
					for(int i=0;i<checkManagePayLines.size();i++){
						checkPayLines.remove(0);
					}
				}
				
				outputMap.put("checkManagePayLines", checkManagePayLines);
				
				memoList=(List)DictionaryUtil.getDictionary("支票备注说明");
				outputMap.put("memoList", memoList);
				checkTypeList=(List)DictionaryUtil.getDictionary("支票类别");
				outputMap.put("checkTypeList", checkTypeList);
				outputMap.put("TYPE", context.contextMap.get("TYPE"));
				outputMap.put("checkPayLines", checkPayLines);
				outputMap.put("LEASE_CODE", context.contextMap.get("LEASE_CODE"));
				outputMap.put("CUST_NAME", context.contextMap.get("CUST_NAME"));
				outputMap.put("credit_id", context.contextMap.get("credit_id"));
				outputMap.put("schemeMap", context.contextMap.get("schemeMap"));
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/rentCheckManage/initRentCheckForOther.jsp");
			//Output.jspSendRedirect(context, "defaultDispatcher?__action=rentContract.queryRentCheckManage");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	public void createRentCheckPayLinesForOther (Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		SqlMapClient sqlMap = DataAccessor.getSession();
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				sqlMap.startTransaction();
				
				String[] PERIOD_NUM = HTMLUtil.getParameterValues(context.request, "PERIOD_NUM", "");
				String[] MONTH_PRICE_TAX = HTMLUtil.getParameterValues(context.request, "MONTH_PRICE_TAX", "");
				String[] CHECKOUT_DATE = HTMLUtil.getParameterValues(context.request, "CHECKOUT_DATE", "");
				String[] CHECK_NUM = HTMLUtil.getParameterValues(context.request, "CHECK_NUM", "");
				String[] CHECK_MONEY = HTMLUtil.getParameterValues(context.request, "CHECK_MONEY", "");
				String[] MEMO = HTMLUtil.getParameterValues(context.request, "MEMO", "");
				String[] ID= HTMLUtil.getParameterValues(context.request, "ID", "");
				String[] IDS= HTMLUtil.getParameterValues(context.request, "IDS", "");
				String[] DRAWER= HTMLUtil.getParameterValues(context.request, "DRAWER", "");
				Map map = new HashMap();
				for (int i=0;i<PERIOD_NUM.length;i++) {
					map.put("CREDIT_ID", context.contextMap.get("credit_id"));
					map.put("PERIOD_NUM", PERIOD_NUM[i]);  
					map.put("MONTH_PRICE_TAX", MONTH_PRICE_TAX[i]); 
					map.put("CHECKOUT_DATE", CHECKOUT_DATE[i]); 
					map.put("CHECK_NUM", CHECK_NUM[i]); 
					map.put("CHECK_MONEY", CHECK_MONEY[i]); 
					map.put("MEMO", MEMO[i]); 
					map.put("s_employeeId", context.contextMap.get("s_employeeId"));
					map.put("ID", ID[i]);
					map.put("DRAWER", DRAWER[i]);
					map.put("TYPE", "1");
					if(!ID[i].equals("")){
						map.put("REMARK", "更新");
						sqlMap.update("rentContract.updateRentCheckPayLines", map);
					}else{
						map.put("REMARK", "新增");
						Long longID=(Long) sqlMap.insert("rentContract.createRentCheckPayLines", map);
						map.put("ID", longID);
					}
					sqlMap.insert("rentContract.createRentCheckPayManageLog", map);
				}
				//如果ID有减少说明有支票被删除
				if(IDS.length>ID.length){
					//判断出 哪些ID有减少
					Set setIDS = new HashSet(Arrays.asList(IDS));        
					Set setID = new HashSet(Arrays.asList(ID)); 
					setIDS.removeAll(setID);
					String[] reduceID=(String[]) setIDS.toArray();
					Map tempCheckPayLines=null;
					for (int i=0;i<reduceID.length;i++) {
						map.put("ID", reduceID[i]);
						sqlMap.update("rentContract.deleteRentCheckPayLines", map);
						tempCheckPayLines=(Map) sqlMap.queryForObject("rentContract.getCheckPaylines",map, DataAccessor.RS_TYPE.MAP);
						tempCheckPayLines.put("REMARK", "删除");
						sqlMap.insert("rentContract.createRentCheckPayManageLog", tempCheckPayLines);
					}
				}
				
				sqlMap.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}finally {
				try {
					sqlMap.endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
					errList.add(e);
				}
			}
		}
		
		if(errList.isEmpty()){
			//Output.jspOutput(outputMap, context, "/rentcontract/initRentCheck.jsp");
			queryForKeyInOtherCheckManage(context);
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/**
	 * @author michael
	 * Add by Michael 2012 12-18
	 * 文审人员分派案件规则
	 */
	public void contractDispatchUserRule(Context context){
		List errList = context.errList;
		List dispatchUserList=null;
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				dispatchUserList=(List) DataAccessor.query("rentContract.getAllAuditorEmployees",context.contextMap, DataAccessor.RS_TYPE.LIST);
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		
		}
	}
	
	
	/**
	 * 2013-01-14
	 * 初始化创建支票明细 For 改版
	 * @param context
	 */
	public void initKeyInRentCheckDetail (Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		List memoList = null;
		List checkTypeList= null;
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				
				memoList=(List)DictionaryUtil.getDictionary("支票备注说明");
				outputMap.put("memoList", memoList);
				checkTypeList=(List)DictionaryUtil.getDictionary("支票类别");
				outputMap.put("checkTypeList", checkTypeList);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/rentCheckManage/keyInRentCheckDetail.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/**
	 * 录入支票明细时，根据收入客户名称查询出合同列表List
	 * @author michael
	 * @param context
	 * 
	 */	
	public void getAllLeaseCodeByCustName(Context context){
		List errList = context.errList;
		Map outputMap = new HashMap();
		List lease_codes = null;
		if (errList.isEmpty()) {
			try {
				lease_codes = (List) DataAccessor.query("rentContract.getAllLeaseCodeByCustName",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("lease_codes", lease_codes);
			Output.jsonOutput(outputMap, context);
		}
	}
	
	/**
	 * 录入支票明细时，根据合同号产生的 CREDIT_ID查询期数
	 * @author michael
	 * @param context
	 * 
	 */
	public void getLeaseTermByCreditID(Context context){
		List errList = context.errList;
		Map outputMap = new HashMap();
		Long lease_term = null;
		if (errList.isEmpty()) {
			try {
				lease_term = (Long) DataAccessor.query("rentContract.getLeaseTermByCreditID",
						context.contextMap, DataAccessor.RS_TYPE.OBJECT);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("lease_term", lease_term);
			Output.jsonOutput(outputMap, context);
		}
	}
	
	/**
	 * 录入支票明细时，一张支票对应一个合同的多个期数，一张支票对应多个合同
	 * @author michael
	 * @param context
	 * 
	 */
	public void createRentCheckDetail (Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		SqlMapClient sqlMap = DataAccessor.getSession();
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				sqlMap.startTransaction();
				//支票状态说明：
				/*
				 * 0:正常  （待投递）
				 * 1：作废
				 * 2：投递（已投递）
				 * 3：入账（已入账）
				 * 4：退票
				 * 5：转移
				 * 6：接受
				 * 7：退回
				 */

				//先保存支票明细
				if(!context.contextMap.get("ID").equals("")){
					context.contextMap.put("ACTION_TYPE", "更新支票");
					context.contextMap.put("REMARK", "更新");
					sqlMap.update("rentContract.updateRentCheckPayLines_new", context.contextMap);
					context.contextMap.put("CHECK_ID", context.contextMap.get("ID"));
				}else{
					context.contextMap.put("ACTION_TYPE", "新增支票");
					context.contextMap.put("REMARK", "新增");
					Long longID=(Long) sqlMap.insert("rentContract.createRentCheckPayLines_new", context.contextMap);
					context.contextMap.put("CHECK_ID", longID);
				}
				
				Long LogID=(Long) sqlMap.insert("rentContract.createRentCheckPayManageLog_new", context.contextMap);
				
				//产生支票ID后，插入合同、期数等信息
				//1.先将此支票原来对应的合同关系都删除
				sqlMap.update("rentContract.deleteRentCheckSchema", context.contextMap);
				//2.保存支票合同、客户、期数对应关系
				String[] CREDIT_ID = HTMLUtil.getParameterValues(context.request, "LEASE_CODE", "");
				String[] CUST_NAME = HTMLUtil.getParameterValues(context.request, "CUST_NAME", "");
				String[] CHECK_START = HTMLUtil.getParameterValues(context.request, "START_PERIOD_NUM", "");
				String[] CHECK_END = HTMLUtil.getParameterValues(context.request, "END_PERIOD_NUM", "");
	
				Map map = new HashMap();
				for (int i=0;i<CREDIT_ID.length;i++) {
					map.put("CREDIT_ID", CREDIT_ID[i]);
					map.put("CUST_NAME", CUST_NAME[i]);  
					map.put("CHECK_START", CHECK_START[i]); 
					map.put("CHECK_END", CHECK_END[i]); 
					map.put("CHECK_ID", context.contextMap.get("CHECK_ID"));
					map.put("s_employeeId", context.contextMap.get("s_employeeId"));
					map.put("LOG_ID", LogID);
					sqlMap.insert("rentContract.addRentCheckSchema", map);
				}

				sqlMap.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}finally {
				try {
					sqlMap.endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
					errList.add(e);
				}
			}
		}
		
		if(errList.isEmpty()){
			//Output.jspOutput(outputMap, context, "/rentcontract/initRentCheck.jsp");
			context.contextMap.put("QSEARCH_VALUE", context.contextMap.get("CUST_NAME_R"));
			queryRentCheckManageDetailForKeyIn(context);
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/**
	 * 支票明细管理页面查询
	 * @param context
	 */
	public void queryAllRentCheckManageDetail (Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		PagingInfo<Object> dw = null;
		Boolean checkDetailManage=false;
		List memoList=null;
		List  typeList = new ArrayList();	 
		
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				
				dw = baseService.queryForListWithPaging("rentContract.queryAllRentCheckDetail_new", context.contextMap, "CHECK_OUT_DATE", ORDER_TYPE.ASC);
				
				// 支票明细管理 权限
				List<String> resourceIdList=(List<String>) DataAccessor.query("supplier.getResourceIdListByEmplId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				for(int i=0;resourceIdList!=null&&i<resourceIdList.size();i++) {
					if("293".equals(resourceIdList.get(i))) {
						checkDetailManage=true;
					}
				}
				
				context.contextMap.put("dataType", "支票退票原因");
				outputMap.put("RETURN_REASON_LIST", (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST));
				
				//支票投递银行
				context.contextMap.put("dataType", "支票投递银行");
				outputMap.put("DELIVERY_BANK", (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST));
			
				memoList=(List)DictionaryUtil.getDictionary("支票备注说明");
				outputMap.put("memoList", memoList);
				
				//支票类别
				typeList=(List)DictionaryUtil.getDictionary("支票类别");
				outputMap.put("typeList", typeList);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		/*-------- output --------*/
		outputMap.put("dw", dw);
		outputMap.put("checkDetailManage", checkDetailManage);
		outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
		outputMap.put("QSTART_DATE", context.contextMap.get("QSTART_DATE"));
		outputMap.put("QEND_DATE", context.contextMap.get("QEND_DATE"));
		outputMap.put("DELIVERY_STATUS", context.contextMap.get("DELIVERY_STATUS"));
		outputMap.put("MEMO", context.contextMap.get("MEMO"));
		outputMap.put("CHECK_TYPE", context.contextMap.get("CHECK_TYPE"));
		outputMap.put("companyCode", context.contextMap.get("companyCode"));
		outputMap.put("companys", LeaseUtil.getCompanys());	
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/rentCheckManage/queryRentCheckDetailManage.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/**
	 * 检查支票号是否存在
	 * @author michael
	 * @param context
	 */
	public void checkRentCheckNum(Context context){
		List errList = context.errList;
		Map outputMap = new HashMap();
		int COUNT = 0;
		if (errList.isEmpty()) {
			try {
				COUNT = (Integer) DataAccessor.query("rentContract.checkRentCheckNum",
						context.contextMap, DataAccessor.RS_TYPE.OBJECT);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("COUNT", COUNT);
			Output.jsonOutput(outputMap, context);
		}
	}
	
	/**
	 * 支票录入管理页面
	 * @param context
	 */
	
	
	/**
	 * 支票管理页面查询
	 * @param context
	 */
	public void queryRentCheckManageDetailForKeyIn (Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		PagingInfo<Object> dw = null;

		Boolean salesKeyin=false;
		Boolean hwExam=false;
		Boolean financeExam=false;
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				dw = baseService.queryForListWithPaging("rentContract.queryRentCheckDetailForKeyIn", context.contextMap, "CUST_NAME", ORDER_TYPE.DESC);
	
				// 支票录入、业管审核、财务审核权限
				List<String> resourceIdList=(List<String>) DataAccessor.query("supplier.getResourceIdListByEmplId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				for(int i=0;resourceIdList!=null&&i<resourceIdList.size();i++) {
					if("291".equals(resourceIdList.get(i))) {
						salesKeyin=true;
					}
					if("290".equals(resourceIdList.get(i))) {
						hwExam=true;
					}
					if("292".equals(resourceIdList.get(i))) {
						financeExam=true;
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
		outputMap.put("salesKeyin", salesKeyin);
		outputMap.put("hwExam", hwExam);
		outputMap.put("financeExam", financeExam);
		outputMap.put("companyCode", context.contextMap.get("companyCode"));
		outputMap.put("companys", LeaseUtil.getCompanys());	
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/rentCheckManage/queryRentCheckDetailList.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	
	/**
	 * 初始化创建支票明细
	 * @param context
	 */
	public void initRentCheckNew (Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		List rentCheckLeaseCodeList=null;
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				
				Map rentCheckDetail=(Map) DataAccessor.query("rentContract.queryRentCheckDetailForID",context.contextMap, DataAccessor.RS_TYPE.MAP);
				rentCheckLeaseCodeList=(List) DataAccessor.query("rentContract.queryRentCheckLeaseCode",context.contextMap, DataAccessor.RS_TYPE.LIST);
				List memoList =null;
				List checkTypeList= null;
				
				outputMap.put("rentCheckDetail", rentCheckDetail);
				outputMap.put("rentCheckLeaseCodeList", rentCheckLeaseCodeList);
				memoList=(List)DictionaryUtil.getDictionary("支票备注说明");
				outputMap.put("memoList", memoList);
				outputMap.put("TYPE", context.contextMap.get("TYPE"));
				checkTypeList=(List)DictionaryUtil.getDictionary("支票类别");
				outputMap.put("checkTypeList", checkTypeList);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		
		if(errList.isEmpty()){
			if("SALES".equals(context.contextMap.get("TYPE"))){
				Output.jspOutput(outputMap, context, "/rentCheckManage/keyInRentCheckDetail.jsp");
			}else if("HW".equals(context.contextMap.get("TYPE"))){
				Output.jspOutput(outputMap, context, "/rentCheckManage/rentCheckDetailHWExam.jsp");
			}else if("FINANCE".equals(context.contextMap.get("TYPE"))){
				Output.jspOutput(outputMap, context, "/rentCheckManage/rentCheckDetailFinanceExam.jsp");
			}else if("SHOW".equals(context.contextMap.get("TYPE"))){
				Output.jspOutput(outputMap, context, "/rentCheckManage/rentCheckDetailShow.jsp");
			}
			//Output.jspSendRedirect(context, "defaultDispatcher?__action=rentContract.queryRentCheckManage");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	//Add by Michael 2013 01-18 For 业管审核、财务审核
	public void createRentCheckExam (Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		SqlMapClient sqlMap = DataAccessor.getSession();
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				sqlMap.startTransaction();
				
				if("HW".equals(context.contextMap.get("TYPE"))){
					context.contextMap.put("all_check_ids", context.contextMap.get("ID"));
					context.contextMap.put("ACTION_TYPE", "业管审核支票");
					context.contextMap.put("REMARK", "业管审核");
					sqlMap.update("rentContract.updateRentCheckPayLines_new", context.contextMap);
					context.contextMap.put("CHECK_ID", context.contextMap.get("ID"));
					
					Long LogID=(Long) sqlMap.insert("rentContract.createRentCheckPayManageLog_new", context.contextMap);
					
					//产生支票ID后，插入合同、期数等信息
					//1.先将此支票原来对应的合同关系都删除
					sqlMap.update("rentContract.deleteRentCheckSchema", context.contextMap);
					//2.保存支票合同、客户、期数对应关系
					String[] CREDIT_ID = HTMLUtil.getParameterValues(context.request, "LEASE_CODE", "");
					String[] CUST_NAME = HTMLUtil.getParameterValues(context.request, "CUST_NAME", "");
					String[] CHECK_START = HTMLUtil.getParameterValues(context.request, "START_PERIOD_NUM", "");
					String[] CHECK_END = HTMLUtil.getParameterValues(context.request, "END_PERIOD_NUM", "");
		
					Map map = new HashMap();
					for (int i=0;i<CREDIT_ID.length;i++) {
						map.put("CREDIT_ID", CREDIT_ID[i]);
						map.put("CUST_NAME", CUST_NAME[i]);  
						map.put("CHECK_START", CHECK_START[i]); 
						map.put("CHECK_END", CHECK_END[i]); 
						map.put("CHECK_ID", context.contextMap.get("CHECK_ID"));
						map.put("s_employeeId", context.contextMap.get("s_employeeId"));
						map.put("LOG_ID", LogID);
						sqlMap.insert("rentContract.addRentCheckSchema", map);
					}
					
					//更新支票业管审核的状况为 1
					sqlMap.update("rentContract.updateRentCheckAllStateByHW", context.contextMap);
							
				}else if("FINANCE".equals(context.contextMap.get("TYPE"))){
					context.contextMap.put("all_check_ids", context.contextMap.get("ID"));
					context.contextMap.put("ACTION_TYPE", "财务审核支票");
					context.contextMap.put("REMARK", "财务审核");
					sqlMap.update("rentContract.updateRentCheckPayLines_new", context.contextMap);
					context.contextMap.put("CHECK_ID", context.contextMap.get("ID"));
					
					Long LogID=(Long)sqlMap.insert("rentContract.createRentCheckPayManageLog_new", context.contextMap);
					
					//产生支票ID后，插入合同、期数等信息
					//1.先将此支票原来对应的合同关系都删除
					sqlMap.update("rentContract.deleteRentCheckSchema", context.contextMap);
					//2.保存支票合同、客户、期数对应关系
					String[] CREDIT_ID = HTMLUtil.getParameterValues(context.request, "LEASE_CODE", "");
					String[] CUST_NAME = HTMLUtil.getParameterValues(context.request, "CUST_NAME", "");
					String[] CHECK_START = HTMLUtil.getParameterValues(context.request, "START_PERIOD_NUM", "");
					String[] CHECK_END = HTMLUtil.getParameterValues(context.request, "END_PERIOD_NUM", "");
		
					Map map = new HashMap();
					for (int i=0;i<CREDIT_ID.length;i++) {
						map.put("CREDIT_ID", CREDIT_ID[i]);
						map.put("CUST_NAME", CUST_NAME[i]);  
						map.put("CHECK_START", CHECK_START[i]); 
						map.put("CHECK_END", CHECK_END[i]); 
						map.put("CHECK_ID", context.contextMap.get("CHECK_ID"));
						map.put("s_employeeId", context.contextMap.get("s_employeeId"));
						map.put("LOG_ID", LogID);
						sqlMap.insert("rentContract.addRentCheckSchema", map);
					}
					
					//更新支票财务审核的状况为 1
					sqlMap.update("rentContract.updateRentCheckAllStateByFinance", context.contextMap);
				}
					
				sqlMap.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}finally {
				try {
					sqlMap.endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
					errList.add(e);
				}
			}
		}
		
		if(errList.isEmpty()){
			//Output.jspOutput(outputMap, context, "/rentcontract/initRentCheck.jsp");
			Output.jspSendRedirect(context, "defaultDispatcher?__action=rentContract.queryRentCheckManageDetailForKeyIn&QSEARCH_VALUE="+context.contextMap.get("CHECK_NUM"));
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	public void queryCheckPayDetailLog(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {
				List checkPaySchema=(List) DataAccessor.query("rentContract.getRentCheckPayDetailLog",context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("checkPaySchema", checkPaySchema);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		
		}
		
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/rentCheckManage/rentCheckSchema.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	public void modifyDeliveryDate(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {
				DataAccessor.execute("rentContract.modifyDeliveryDate", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		
		if (errList.isEmpty()) {
			Output.jsonOutput(outputMap, context);
		}
	}

	/**
	 * @author michael
	 * 批量入账支票功能
	 * 2013-01-06
	 * @param context
	 */
	public void batchRecored(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		SqlMapClient sqlMap = DataAccessor.getSession();
		String remark="";
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				//参数从前台获取需要进行批量投递的支票ID
				String strIDS=(String) context.contextMap.get("ids");
				String [] ids=strIDS.split(",");
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
				sqlMap.startTransaction();
				
				for(int i=0;i<ids.length;i++) {
					context.contextMap.put("ID", ids[i]);
					remark= sf.format(new Date())+"支票入账。批量入账通过;";
					context.contextMap.put("RECORDED_STATUS", 1);
					context.contextMap.put("STATE", 3);	
					sqlMap.update("rentContract.updateRentCheckRecorded", context.contextMap);

					Map checkPayDetailMap=(Map) DataAccessor.query("rentContract.querySchemaCheckManageDetail",context.contextMap, DataAccessor.RS_TYPE.MAP);
					checkPayDetailMap.put("REMARK", remark);
					checkPayDetailMap.put("ACTION_TYPE", "支票入账");
					checkPayDetailMap.put("CHECKOUT_DATE",checkPayDetailMap.get("CHECK_OUT_DATE"));
					checkPayDetailMap.put("s_employeeId",context.contextMap.get("s_employeeId"));
					sqlMap.insert("rentContract.createRentCheckPayManageLog", checkPayDetailMap);
				}
				
				sqlMap.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}finally {
				try {
					sqlMap.endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		
		}
		
		if (errList.isEmpty()) {
			Output.jsonOutput(outputMap, context);
		}
	}
	
	
	/**
	 * @author michael
	 * 批量退票支票功能
	 * 2013-01-06
	 * @param context
	 */
	public void batchReturnBack(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		SqlMapClient sqlMap = DataAccessor.getSession();
		String remark="";
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				//参数从前台获取需要进行批量投递的支票ID
				String strIDS=(String) context.contextMap.get("ids");
				String [] ids=strIDS.split(",");
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
				sqlMap.startTransaction();
				
				for(int i=0;i<ids.length;i++) {
					context.contextMap.put("ID", ids[i]);
					remark= sf.format(new Date())+"支票退票。批量退票通过;";
					context.contextMap.put("STATE", 4);	
					sqlMap.update("rentContract.updateRentCheckRecorded", context.contextMap);

					Map checkPayDetailMap=(Map) DataAccessor.query("rentContract.querySchemaCheckManageDetail",context.contextMap, DataAccessor.RS_TYPE.MAP);
					checkPayDetailMap.put("REMARK", remark);
					checkPayDetailMap.put("ACTION_TYPE", "支票退票");
					checkPayDetailMap.put("CHECKOUT_DATE",checkPayDetailMap.get("CHECK_OUT_DATE"));
					checkPayDetailMap.put("s_employeeId",context.contextMap.get("s_employeeId"));
					sqlMap.insert("rentContract.createRentCheckPayManageLog", checkPayDetailMap);
					
					
					//addDunRecord逾期催收插入信息
					

					String returnReason = DictionaryUtil.getFlag("支票退票原因", (String)context.contextMap.get("RETURN_REASON"));
					Map checkInfo = (Map) sqlMap.queryForObject("rentContract.getCheckInfo", context.contextMap);
					String cust_code = (String) checkInfo.get("CUST_CODE");
					String content = "[支票退票通知]:支票号码: " + String.valueOf(checkInfo.get("CHECK_NUM")) +",";
					content += "出票日期: " + String.valueOf(checkInfo.get("CHECK_OUT_DATE")) +",";
					content += "退票日期: " + DateUtil.dateToStr(new Date()) +",";
					content += "退票原因: " + returnReason +",";
					content += "退票金额: " + String.valueOf(checkInfo.get("CHECK_MONEY")) +"。";
					Map dunRecord =  new HashMap();
					dunRecord.put("clerk_id", null);
					dunRecord.put("ANSWERPHONE_NAME", "000");
					dunRecord.put("PHONE_NUMBER", "000");
					dunRecord.put("RESULT", 16);
					dunRecord.put("CALL_CONTENT", content);
					dunRecord.put("CUST_CODE", cust_code);
					sqlMap.insert("dunTask.addDunRecord", dunRecord);
				}
				
				sqlMap.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}finally {
				try {
					sqlMap.endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		
		}
		
		if (errList.isEmpty()) {
			queryAllRentCheckManageDetail(context);
		}
	}
	
	/**
	 * @author michael
	 * 批量业管确认功能
	 * 2013-02-06
	 * @param context
	 */
	public void batchHWExam(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		SqlMapClient sqlMap = DataAccessor.getSession();
		String remark="";
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				//参数从前台获取需要进行批量投递的支票ID
				String strIDS=(String) context.contextMap.get("ids");
				String [] ids=strIDS.split(",");
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
				sqlMap.startTransaction();
				
				context.contextMap.put("all_check_ids", context.contextMap.get("ids"));
				
				//更新支票业管审核的状况为 1
				sqlMap.update("rentContract.updateRentCheckAllStateByHW", context.contextMap);
				
				for(int i=0;i<ids.length;i++) {
					context.contextMap.put("ID", ids[i]);
					remark= sf.format(new Date())+"业管批量确认。";
					Map checkPayDetailMap=(Map) DataAccessor.query("rentContract.querySchemaCheckManageDetail",context.contextMap, DataAccessor.RS_TYPE.MAP);
					checkPayDetailMap.put("REMARK", remark);
					checkPayDetailMap.put("ACTION_TYPE", "业管审核支票");
					checkPayDetailMap.put("CHECKOUT_DATE",checkPayDetailMap.get("CHECK_OUT_DATE"));
					checkPayDetailMap.put("s_employeeId",context.contextMap.get("s_employeeId"));
					sqlMap.insert("rentContract.createRentCheckPayManageLog", checkPayDetailMap);
				}
				
				sqlMap.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}finally {
				try {
					sqlMap.endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		if (errList.isEmpty()) {
			Output.jsonOutput(outputMap, context);
		}
	}
	
	/**
	 * @author michael
	 * 批量财务确认功能
	 * 2013-02-06
	 * @param context
	 */
	public void batchFinanceExam(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		SqlMapClient sqlMap = DataAccessor.getSession();
		String remark="";
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				//参数从前台获取需要进行批量投递的支票ID
				String strIDS=(String) context.contextMap.get("ids");
				String [] ids=strIDS.split(",");
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
				sqlMap.startTransaction();
				
				context.contextMap.put("all_check_ids", context.contextMap.get("ids"));
				
				//更新支票财务审核的状况为 1
				sqlMap.update("rentContract.updateRentCheckAllStateByFinance", context.contextMap);
				
				for(int i=0;i<ids.length;i++) {
					context.contextMap.put("ID", ids[i]);
					remark= sf.format(new Date())+"财务批量确认。";
					Map checkPayDetailMap=(Map) DataAccessor.query("rentContract.querySchemaCheckManageDetail",context.contextMap, DataAccessor.RS_TYPE.MAP);
					checkPayDetailMap.put("REMARK", remark);
					checkPayDetailMap.put("ACTION_TYPE", "财务审核支票");
					checkPayDetailMap.put("CHECKOUT_DATE",checkPayDetailMap.get("CHECK_OUT_DATE"));
					checkPayDetailMap.put("s_employeeId",context.contextMap.get("s_employeeId"));
					sqlMap.insert("rentContract.createRentCheckPayManageLog", checkPayDetailMap);
				}
				sqlMap.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}finally {
				try {
					sqlMap.endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		if (errList.isEmpty()) {
			Output.jsonOutput(outputMap, context);
		}
	}
	
	/**
	 * @author michael
	 * 批量作废支票功能
	 * 2013-01-06
	 * @param context
	 */
	public void batchCancel(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		SqlMapClient sqlMap = DataAccessor.getSession();
		String remark="";
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				//参数从前台获取需要进行批量投递的支票ID
				String strIDS=(String) context.contextMap.get("ids");
				String [] ids=strIDS.split(",");
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
				sqlMap.startTransaction();
				
				for(int i=0;i<ids.length;i++) {
					context.contextMap.put("ID", ids[i]);
					remark= sf.format(new Date())+"支票作废。批量作废支票;";
					context.contextMap.put("STATE", 1);	
					sqlMap.update("rentContract.updateRentCheckDelivery", context.contextMap);

					Map checkPayDetailMap=(Map) DataAccessor.query("rentContract.querySchemaCheckManageDetail",context.contextMap, DataAccessor.RS_TYPE.MAP);
					checkPayDetailMap.put("REMARK", remark);
					checkPayDetailMap.put("ACTION_TYPE", "支票作废");
					checkPayDetailMap.put("CHECKOUT_DATE",checkPayDetailMap.get("CHECK_OUT_DATE"));
					checkPayDetailMap.put("s_employeeId",context.contextMap.get("s_employeeId"));
					sqlMap.insert("rentContract.createRentCheckPayManageLog", checkPayDetailMap);
				}
				
				sqlMap.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}finally {
				try {
					sqlMap.endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		
		}
		
		if (errList.isEmpty()) {
			queryAllRentCheckManageDetail(context);
		}
	}
	
	/**
	 * 结清文件申请
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryRentSettleFileSend (Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		PagingInfo<Object> dw = null;

		Map rsMap = null;
		Map paramMap = new HashMap();
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				dw = baseService.queryForListWithPaging("rentContract.queryRentSettleFileSend", context.contextMap, "WIND_RESULT_DATE", ORDER_TYPE.DESC);
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}

		outputMap.put("dw", dw);
		outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
		outputMap.put("RENTSTAUTS", context.contextMap.get("RENTSTAUTS"));
		outputMap.put("RECP_STATUS", context.contextMap.get("RECP_STATUS"));
		outputMap.put("STATE", context.contextMap.get("STATE"));
		
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/rentcontract/settleFileApplySend.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	/**
	 * 结清文件审批
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryRentSettleFileExam (Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		PagingInfo<Object> dw = null;

		Map rsMap = null;
		Map paramMap = new HashMap();
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				dw = baseService.queryForListWithPaging("rentContract.queryRentSettleFileSendForExam", context.contextMap, "ID", ORDER_TYPE.DESC);
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}

		outputMap.put("dw", dw);
		outputMap.put("STATE", context.contextMap.get("STATE"));
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/rentcontract/settleFileApplyExam.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	public void queryRentSettleFileDetail(Context context) {	
		Map outputMap = new HashMap();
		List errList = context.errList;		
		List insorupd =new ArrayList();
		Map infor = new HashMap();
		Map rentSettleFileApply=null;
		if("3".equals(context.contextMap.get("CONTRACT_TYPE")) || "4".equals(context.contextMap.get("CONTRACT_TYPE"))){
			context.contextMap.put("FILE_TYPE", 5);
		}else{
			context.contextMap.put("FILE_TYPE", 4);
		}
		try {
			insorupd=(List)DataAccessor.query("rentFile.selectRentSettleFileDetail", context.contextMap, DataAccessor.RS_TYPE.LIST);
			//查询承租人资料和合同资料的信息
			infor=(Map)DataAccessor.query("rentFile.selectInforForSales", context.contextMap, DataAccessor.RS_TYPE.MAP);
			rentSettleFileApply=(Map)DataAccessor.query("rentContract.selectRentSettleApplyDetail", context.contextMap, DataAccessor.RS_TYPE.MAP);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}	
		if (errList.isEmpty()) {
			outputMap.put("insorupd",insorupd);
			outputMap.put("infor",infor);
			outputMap.put("RECT_ID", context.contextMap.get("RECT_ID"));
			outputMap.put("rentSettleFileApply", rentSettleFileApply);
			if("EXAM".equals(context.contextMap.get("TYPE"))){
				Output.jspOutput(outputMap, context, "/rentcontract/rentSettleFileExam.jsp");
			}else{
				Output.jspOutput(outputMap, context, "/rentcontract/rentSettleFile.jsp");
			}
		} else {
			Output.jspOutput(outputMap, context, "/error.jsp");
		}	
	}	

	public void upDateRentSettleFileExam(Context context) throws IOException {
		Map outputMap = new HashMap();
		boolean flag=true;
		try{		
			DataAccessor.execute("rentContract.updateRentSettleExam", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
				
		}  catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}	
		Output.jsonFlageOutput(flag, context);
	}
	
	/**
	 * 合同寄件管理（查询要寄件的合同文件）
	 * @param context
	 */
	public void queryRentPostFile(Context context) {	
		Map outputMap = new HashMap();
		List errList = context.errList;		
		List insorupd =new ArrayList();
		Map infor = new HashMap();
		List postNameList=null;
			try {
				insorupd=(List)DataAccessor.query("rentFile.selectRentFileForPost", context.contextMap, DataAccessor.RS_TYPE.LIST);
				//查询承租人资料和合同资料的信息
				infor=(Map)DataAccessor.query("rentFile.selectInfor", context.contextMap, DataAccessor.RS_TYPE.MAP);
				postNameList=(List)DictionaryUtil.getDictionary("快递公司");
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}	
		if (errList.isEmpty()) {
			outputMap.put("insorupd",insorupd);
			outputMap.put("infor",infor);
			outputMap.put("prcd_id", context.contextMap.get("prcd_id"));
			outputMap.put("postNameList", postNameList);
			outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
			outputMap.put("page_from", "C");
			Output.jspOutput(outputMap, context, "/rentcontract/rentFilePostManage.jsp");
		} else {
			Output.jspOutput(outputMap, context, "/error.jsp");
		}	
	}	
	
	/**
	 * 合同寄件管理（保存寄件文件明细）
	 * @param context
	 * @throws IOException
	 */
	public void saveRentPostFileDetail(Context context) throws IOException {
		Map outputMap = new HashMap();
		SqlMapClient sqlMapClient = DataAccessor.getSession();
		String strLogString = "";
		try {	

				sqlMapClient.startTransaction();
				if("".equals(context.contextMap.get("POST_ID"))||context.contextMap.get("POST_ID")==null){
					//保存寄件管理状态
					Long POST_ID=(Long)sqlMapClient.insert("rentFile.saveRentFilePost", context.contextMap);
					
					for (int i = 1; i <=DataUtil.intUtil(context.contextMap.get("statusLengthC")); i++) {
						context.contextMap.put("REFI_ID",context.contextMap.get("REFI_ID"+String.valueOf(i)));
						context.contextMap.put("FILE_COUNT",context.contextMap.get("FILE_COUNT"+String.valueOf(i)));
						context.contextMap.put("MEMO",context.contextMap.get("MEMO"+String.valueOf(i)));
						context.contextMap.put("prcd_id", context.contextMap.get("prcd_id"));
						context.contextMap.put("FILE_NAME", context.contextMap.get("FILE_NAME"+String.valueOf(i)));
						context.contextMap.put("POST_ID", POST_ID);
						sqlMapClient.insert("rentFile.saveRentPostFileDetail", context.contextMap);
					}
					
					//Add by Michael 在更新文件的邮寄状态
					sqlMapClient.update("rentFile.updateRentPostFileFlag", context.contextMap);

				}else{
					for (int i = 1; i <=DataUtil.intUtil(context.contextMap.get("statusLengthC")); i++) {
						context.contextMap.put("REFI_ID",context.contextMap.get("REFI_ID"+String.valueOf(i)));
						context.contextMap.put("FILE_COUNT",context.contextMap.get("FILE_COUNT"+String.valueOf(i)));
						context.contextMap.put("MEMO",context.contextMap.get("MEMO"+String.valueOf(i)));
						context.contextMap.put("prcd_id", context.contextMap.get("prcd_id"));
						context.contextMap.put("FILE_NAME", context.contextMap.get("FILE_NAME"+String.valueOf(i)));
						sqlMapClient.insert("rentFile.updateRentPostFileDetail", context.contextMap);
					}
					
					//Add by Michael 在更新文件的默认状态
					sqlMapClient.update("rentFile.updateRentPostFileDefFlag", context.contextMap);
					//Add by Michael 在更新文件的邮寄状态
					sqlMapClient.update("rentFile.updateRentPostFileFlag", context.contextMap);
					
					sqlMapClient.insert("rentFile.updateRentFilePost", context.contextMap);
				}
				
				if(!"".equals(context.contextMap.get("POST_CODE")) && !"".equals(context.contextMap.get("POST_DATE"))){
					strLogString="业管寄件：寄件日期："+context.contextMap.get("POST_DATE")+"；快递公司："+context.contextMap.get("POST_NAME")+"；寄件单号："+context.contextMap.get("POST_CODE")+"<br>";
					strLogString+="寄送文件有："+context.contextMap.get("file_names");
				}
				
				sqlMapClient.commitTransaction();

				if (!"".equals(strLogString)){
			    	BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap.get("prcd_id")),DataUtil.longUtil(0),
					   		 "合同文件寄件操作",
				   		 	 "合同文件寄件操作",
				   		 	 null,
				   		 	 strLogString,
				   		 	 1,
				   		 	 DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
				   		 	 DataUtil.longUtil(0),
				   		 	 context.getRequest().getRemoteAddr());
				}
		}  catch (Exception e) {
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
		if ("F".equals(context.contextMap.get("page_from"))) {
			queryRentPostFileManage(context);
		} else {
			queryRentContract(context);
		}
		
		//Output.jspSendRedirect(context,"defaultDispatcher?__action=rentContract.queryRentContract");
	}	
	
	public void queryRentPostFileManage (Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		PagingInfo<Object> dw = null;

		Map rsMap = null;
		Map paramMap = new HashMap();
		paramMap.put("id", context.contextMap.get("s_employeeId"));
		Boolean postFileUpdate=false;
		List postNameList = null;
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				rsMap = (Map) DataAccessor.query("employee.getEmpInforById", paramMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("p_usernode", rsMap.get("NODE"));
				
				dw = baseService.queryForListWithPaging("rentContract.queryRentPostFileManage", context.contextMap, "POST_ID", ORDER_TYPE.DESC);
				postNameList=(List)DictionaryUtil.getDictionary("快递公司");
				//Add by Michael  增加更新修改操作
				
				List<String> resourceIdList=(List<String>) DataAccessor.query("supplier.getResourceIdListByEmplId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				for(int i=0;resourceIdList!=null&&i<resourceIdList.size();i++) {
					if("339".equals(resourceIdList.get(i))) {
						postFileUpdate=true;
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
		outputMap.put("POST_CODE", context.contextMap.get("POST_CODE"));
		outputMap.put("POST_NAME", context.contextMap.get("POST_NAME"));
		outputMap.put("postFileUpdate", postFileUpdate);
		outputMap.put("postNameList", postNameList);
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/rentcontract/queryPostRentFileManage.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
	}
	
	public void queryRentPostFileByID (Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;		
		List insorupd =new ArrayList();
		Map infor = new HashMap();
		Map postMap = null;
		List postNameList = null;
			try {
				insorupd=(List)DataAccessor.query("rentFile.selectRentPostFileByPostID", context.contextMap, DataAccessor.RS_TYPE.LIST);
				//查询承租人资料和合同资料的信息
				infor=(Map)DataAccessor.query("rentFile.selectInfor", context.contextMap, DataAccessor.RS_TYPE.MAP);
				postMap =(Map)DataAccessor.query("rentFile.selectPostDetailByID", context.contextMap, DataAccessor.RS_TYPE.MAP);
				postNameList=(List)DictionaryUtil.getDictionary("快递公司");
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}	
		if (errList.isEmpty()) {
			outputMap.put("insorupd",insorupd);
			outputMap.put("infor",infor);
			outputMap.put("postMap",postMap);
			outputMap.put("prcd_id", context.contextMap.get("prcd_id"));
			outputMap.put("POST_ID", context.contextMap.get("POST_ID"));
			outputMap.put("postNameList", postNameList);
			outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
			outputMap.put("page_from", "F");
			if("UPDATE".equals(context.contextMap.get("FLAG"))){
				Output.jspOutput(outputMap, context, "/rentcontract/rentFilePostManage.jsp");
			}else{
				Output.jspOutput(outputMap, context, "/rentcontract/rentFilePostShow.jsp");
			}
			
		} else {
			Output.jspOutput(outputMap, context, "/error.jsp");
		}	
	}
	
	/**
	 * @author michael
	 * 批量转移支票功能
	 * 2013-01-06
	 * @param context
	 */
	public void batchTransfer(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		SqlMapClient sqlMap = DataAccessor.getSession();
		String remark="";
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				//参数从前台获取需要进行批量转移的支票ID
				String strIDS=(String) context.contextMap.get("ids");
				String [] ids=strIDS.split(",");
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
				sqlMap.startTransaction();
				
				for(int i=0;i<ids.length;i++) {
					context.contextMap.put("ID", ids[i]);
					remark=sf.format(new Date())+"支票批量转移。转移到："+context.contextMap.get("TRANSFER_TO")+"。备注说明："+context.contextMap.get("FINANCE_MEMO");
					context.contextMap.put("STATE", 5);	
					sqlMap.update("rentContract.updateRentCheckState", context.contextMap);

					Map checkPayDetailMap=(Map) DataAccessor.query("rentContract.querySchemaCheckManageDetail",context.contextMap, DataAccessor.RS_TYPE.MAP);
					checkPayDetailMap.put("REMARK", remark);
					checkPayDetailMap.put("ACTION_TYPE", "支票批量转移");
					checkPayDetailMap.put("CHECKOUT_DATE",checkPayDetailMap.get("CHECK_OUT_DATE"));
					checkPayDetailMap.put("s_employeeId",context.contextMap.get("s_employeeId"));
					sqlMap.insert("rentContract.createRentCheckPayManageLog", checkPayDetailMap);
				}
				
				sqlMap.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}finally {
				try {
					sqlMap.endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		
		}
		
		if (errList.isEmpty()) {
			queryAllRentCheckManageDetail(context);
		}
	}
	
	/**
	 * @author michael
	 * 批量接受支票功能
	 * 2013-01-06
	 * @param context
	 */
	public void batchAccept(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		SqlMapClient sqlMap = DataAccessor.getSession();
		String remark="";
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				//参数从前台获取需要进行批量转移的支票ID
				String strIDS=(String) context.contextMap.get("ids");
				String [] ids=strIDS.split(",");
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
				sqlMap.startTransaction();
				
				for(int i=0;i<ids.length;i++) {
					context.contextMap.put("ID", ids[i]);
					remark=sf.format(new Date())+"接收支票。备注说明："+context.contextMap.get("FINANCE_MEMO");
					context.contextMap.put("STATE", 6);	
					sqlMap.update("rentContract.updateRentCheckState", context.contextMap);

					Map checkPayDetailMap=(Map) DataAccessor.query("rentContract.querySchemaCheckManageDetail",context.contextMap, DataAccessor.RS_TYPE.MAP);
					checkPayDetailMap.put("REMARK", remark);
					checkPayDetailMap.put("ACTION_TYPE", "支票批量接受");
					checkPayDetailMap.put("CHECKOUT_DATE",checkPayDetailMap.get("CHECK_OUT_DATE"));
					checkPayDetailMap.put("s_employeeId",context.contextMap.get("s_employeeId"));
					sqlMap.insert("rentContract.createRentCheckPayManageLog", checkPayDetailMap);
				}
				
				sqlMap.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}finally {
				try {
					sqlMap.endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		
		}
		
		if (errList.isEmpty()) {
			queryAllRentCheckManageDetail(context);
		}
	}
	
	/**
	 * @author zhangbo
	 * 批量退回
	 */
	public void batchBackTo(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		SqlMapClient sqlMap = DataAccessor.getSession();
		String remark="";
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				//参数从前台获取需要进行批量转移的支票ID
				String strIDS=(String) context.contextMap.get("ids");
				String [] ids=strIDS.split(",");
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
				sqlMap.startTransaction();
				
				for(int i=0;i<ids.length;i++) {
					context.contextMap.put("ID", ids[i]);
					remark=sf.format(new Date())+" 退回支票。备注说明："+context.contextMap.get("FINANCE_MEMO");
					context.contextMap.put("STATE", 7);	
					sqlMap.update("rentContract.updateRentCheckState", context.contextMap);

					Map checkPayDetailMap=(Map) DataAccessor.query("rentContract.querySchemaCheckManageDetail",context.contextMap, DataAccessor.RS_TYPE.MAP);
					checkPayDetailMap.put("REMARK", remark);
					checkPayDetailMap.put("ACTION_TYPE", "支票批量退回");
					checkPayDetailMap.put("CHECKOUT_DATE",checkPayDetailMap.get("CHECK_OUT_DATE"));
					checkPayDetailMap.put("s_employeeId",context.contextMap.get("s_employeeId"));
					sqlMap.insert("rentContract.createRentCheckPayManageLog", checkPayDetailMap);
				}
				
				sqlMap.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}finally {
				try {
					sqlMap.endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		
		}
		
		if (errList.isEmpty()) {
			queryAllRentCheckManageDetail(context);
		}
	}
	/**
	 * 修改投递银行
	 */
	public void updateBankById(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {
				DataAccessor.execute("rentContract.updateBankById", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		if (errList.isEmpty()) {
			Output.jsonOutput(outputMap, context);
		}
	}
	
	/**
	 * 导出全部支票报表excel
	 * @param qStartDate 开始日期
	 * @param qEndDate 结束日期
	 * @param searchValue 搜索内容
	 * @param deliveryStatus 支票状态
	 * @param memo 支票备注
	 * @return
	 */
	public static List<Map<String, Object>> exportCheckAll(String qStartDate, String qEndDate, String searchValue, String deliveryStatus, String memo){
		Map<String,String> param=new HashMap<String,String>();
		param.put("QSTART_DATE", qStartDate);
		param.put("QEND_DATE", qEndDate);
		param.put("QSEARCH_VALUE", searchValue);
		param.put("DELIVERY_STATUS", deliveryStatus);
		param.put("MEMO", memo);
		List<Map<String,Object>> resultList=null;
		double totalMomey = 0;
		try {
			resultList=(List<Map<String,Object>>)DataAccessor.query("rentContract.queryAllRentCheckDetail_new",param,RS_TYPE.LIST);
			for (Map<String, Object> m : resultList) {
				totalMomey += Double.parseDouble(m.get("CHECK_MONEY").toString());
				Integer state = (Integer)(m.get("STATE"));
				switch (state) {
				case 0:
					m.put("STATE", "待投递");
					break;
				case 1:
					m.put("STATE", "已作废");
					break;
				case 2:
					m.put("STATE", "已投递");
					break;
				case 3:
					m.put("STATE", "已入账");
					break;
				case 4:
					m.put("STATE", "已退票");
					break;
				case 5:
					m.put("STATE", "已转移");
					break;
				case 6:
					m.put("STATE", "已接收");
					break;
				case 7:
					m.put("STATE", "已退回");
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}
	//退回后，等待退回确认邮件提醒job
		public void getBackCheckTowDaysForJob() throws Exception {
			//判断今天是否是工作日，如果是工作日才发送邮件,否则不发邮件
			try {
				if (baseService.isWorkingDay()==false) {
					return; 
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			Map<String,Object> map=new HashMap<String,Object>();
			try {
				List<Map> checkUserList=(List<Map>) DataAccessor.query("rentContract.getBackCheckTowDaysForJob",map,DataAccessor.RS_TYPE.LIST);
				if(checkUserList.size()>0){
					for(Map mapUser :checkUserList){
						map.put("CREATE_ID", mapUser.get("CREATE_ID"));
						//支票号码List
						List<Map> checkNumList=(List<Map>) DataAccessor.query("rentContract.getBackCheckNumList",map,DataAccessor.RS_TYPE.LIST);
						//添加邮件
						MailSettingTo mailSettingTo =new MailSettingTo();
						//邮件内容
						mailSettingTo.setEmailContent(getBackTowDaysMailContent(mapUser,checkNumList));
						mailSettingTo.setEmailTo((String)mapUser.get("EMAIL"));
						mailSettingTo.setEmailCc("xiaqingmei@tacleasing.cn");
						//邮件主题
						mailSettingTo.setEmailSubject("[系统通知]：支票退回通知，需要您在系统中退回确认");
						mailUtilService.sendMail(mailSettingTo);
					}
				}
			} catch (Exception e) {
				throw e;
			}
		}
		
		private String getBackTowDaysMailContent(Map<String, Object> mapUser,List<Map> checkNumList){
			if (mapUser == null) {
				return null;
			}
			String code ="";
			if (checkNumList != null) {
				
				for(int i=0;i<checkNumList.size();i++){
					code=code+"<br>"+checkNumList.get(i).get("CHECK_NUM");
				}
			}
			StringBuffer sb = new StringBuffer();
				sb.append("<html><head></head>");
				sb.append("<font size='3'><b>您好:<b><br></font>" + "<font size='3'>截至今日08：55，您有"+mapUser.get("CHECKCOUNT")
						+"張支票被退回，请您及时查看并确认退回。<br>支票号为："+code+"<br></font><br><br><br>"
				);
				sb.append("</html>");
			return sb.toString();
		}
	/**
	 * 导出选中支票报表excel
	 * @param ids 要查询的支票id(如："1,2,5")
	 * @return
	 */
	public static List<Map<String, Object>> exportCheck(String ids){
		Map<String,String[]> param=new HashMap<String,String[]>();
		param.put("ids", ids.split(","));
		List<Map<String,Object>> resultList=null;
		double totalMomey = 0;
		try {
			resultList=(List<Map<String,Object>>)DataAccessor.query("rentContract.queryAllRentCheckDetailByIds",param,RS_TYPE.LIST);
			for (Map<String, Object> m : resultList) {
				totalMomey += Double.parseDouble(m.get("CHECK_MONEY").toString());
				Integer state = (Integer)(m.get("STATE"));
				switch (state) {
				case 0:
					m.put("STATE", "待投递");
					break;
				case 1:
					m.put("STATE", "已作废");
					break;
				case 2:
					m.put("STATE", "已投递");
					break;
				case 3:
					m.put("STATE", "已入账");
					break;
				case 4:
					m.put("STATE", "已退票");
					break;
				case 5:
					m.put("STATE", "已转移");
					break;
				case 6:
					m.put("STATE", "已接收");
					break;
				case 7:
					m.put("STATE", "已退回");
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}
	
	
	//还款操作
	public void repayment(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map params = new HashMap();
		params.put("rect_id", context.contextMap.get("_RECT_ID"));
		params.put("FIRST_PAYDATE", context.contextMap.get("_FIRST_PAYDATE"));
		params.put("START_DATE", context.contextMap.get("_START_DATE"));
		params.put("status", 0);//复核状态
		try {
			DataAccessor.getSession().startTransaction();
			DataAccessor.getSession().update("rentContract.updateContractSchemaStartDate", params);
			DataAccessor.getSession().update("rentContract.updateContractSchemaFirstPayDate", params);
			DataAccessor.getSession().update("rentContract.updateRentContractRectStatus", params);
			
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
			DataAccessor.getSession().commitTransaction();
		} catch (SQLException e) {
			logger.info("--------  合同还款 出错");
			e.printStackTrace();
			errList.add("合同还款 出错");
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);			
		}finally {
			try {
				DataAccessor.getSession().endTransaction();
			} catch (SQLException e) {
				errList.add("合同还款  关闭事务错误");
				logger.info("--------  合同还款  关闭事务错误");
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		
		if (errList.isEmpty()) {
			// Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=rentContract.queryRentContract");
			Output.jspSendRedirect(context, "defaultDispatcher?__action=rentContract.queryRentContract");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	
	/**
	 * 补件页面查询
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryRentContractForLossFile (Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		PagingInfo<Object> dw = null;
		Map rsMap = null;
		Map paramMap = new HashMap();
		paramMap.put("id", context.contextMap.get("s_employeeId"));
		//默认查询补件人为自己的合同
		if(context.contextMap.get("SELF_DISPATCH")==null || "".equals(context.contextMap.get("SELF_DISPATCH"))) {
			context.contextMap.put("SELF_DISPATCH", "true");
		}
		if(context.contextMap.get("LOSS_ONLY")==null || "".equals(context.contextMap.get("LOSS_ONLY"))) {
			context.contextMap.put("LOSS_ONLY", "true");
		}
		Boolean editThingNum=false;
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				rsMap = (Map) DataAccessor.query("employee.getEmpInforById", paramMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("p_usernode", rsMap.get("NODE"));
				
				dw = baseService.queryForListWithPaging("rentContract.queryRentContractForLossFile", context.contextMap, "WIND_RESULT_DATE", ORDER_TYPE.DESC);
				
				//Add by Michael 2012 5-7 增加维护机号权限
				
				List<String> resourceIdList=(List<String>) DataAccessor.query("supplier.getResourceIdListByEmplId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				for(int i=0;resourceIdList!=null&&i<resourceIdList.size();i++) {
					if("209".equals(resourceIdList.get(i))) {
						editThingNum=true;
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
		outputMap.put("QSELECT_STATUS", context.contextMap.get("QSELECT_STATUS"));
		outputMap.put("RENTSTAUTS", context.contextMap.get("RENTSTAUTS"));
		outputMap.put("RECP_STATUS", context.contextMap.get("RECP_STATUS"));
		outputMap.put("editThingNum", editThingNum);
		outputMap.put("SELF_DISPATCH", context.contextMap.get("SELF_DISPATCH"));
		outputMap.put("LOSS_ONLY", context.contextMap.get("LOSS_ONLY"));
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/rentcontract/queryRentContractForLossFile.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

}
