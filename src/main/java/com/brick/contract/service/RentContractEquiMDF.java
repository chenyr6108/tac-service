package com.brick.contract.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.baseManage.service.BusinessLog;
import com.brick.collection.support.PayRate;
import com.brick.collection.util.PaylistUtil;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.contract.util.ConfirmationLetterPDF;
import com.brick.credit.to.CreditTo;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.DataUtil;
import com.brick.util.NumberUtils;
import com.brick.util.web.HTMLUtil;

/**
 * 合同模块的操作
 * @author Michael
 * @date 9 4, 2011
 * @version
 */
public class RentContractEquiMDF extends AService {

	Log logger = LogFactory.getLog(RentContractEquiMDF.class);
	
	private MailUtilService mailUtilService;
	
	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}
	
	/**
	 * 管理页面查询（合同浏览）
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryRentContractForShow (Context context) {
		
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
				dw = (DataWrap) DataAccessor.query("rentContractEquiMDF.queryRentContract", context.contextMap, DataAccessor.RS_TYPE.PAGED);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/*-------- output --------*/
		outputMap.put("dw", dw);
		outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
		outputMap.put("QSTART_DATE", context.contextMap.get("QSTART_DATE"));
		outputMap.put("QEND_DATE", context.contextMap.get("QEND_DATE"));
		outputMap.put("QSELECT_STATUS", context.contextMap.get("QSELECT_STATUS"));
		outputMap.put("RENTSTAUTS", context.contextMap.get("RENTSTAUTS"));

		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/rentcontract/queryRentContractForEquiMDF.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
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
		List<Map> manufacturerlist=null;
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		 
			try {
				outputMap.put("RECT_ID", context.contextMap.get("RECT_ID"));
				outputMap.put("PRCD_ID", context.contextMap.get("PRCD_ID"));
				//
				rentContract = (Map) DataAccessor.query("rentContractEquiMDF.readRentContractByRectId", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("rentContract", rentContract);
	
				typeList = (List<Map>) DataAccessor.query("suplEquipment.getAllType", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("typeList", typeList);
				outputMap.put("typeJsonList", Output.serializer.serialize(typeList));
				//
				manufacturerlist=(List<Map>) DataAccessor.query("rentContractEquiMDF.getAllManufacturer", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("manufacturerlist", manufacturerlist);
				
				equipmentList = (List<Map>) DataAccessor.query("rentContractEquiMDF.readEquipmentByRectId", context.contextMap, DataAccessor.RS_TYPE.LIST);
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
				}
				outputMap.put("equipmentList", equipmentList);

				Map baseRate = PayRate.getBaseRate();
				outputMap.put("baseRateJson", Output.serializer.serialize(baseRate));
	
				Map dataDictionaryMap = new HashMap();
			
				dataDictionaryMap.put("dataType", "锁码方式");
				lockList = (List) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("lockList", lockList);
			} catch (Exception e) {
				e.printStackTrace();
				errList.add(e);
			}		
		}		
		if (errList.isEmpty()) {			
			Output.jspOutput(outputMap, context, "/rentcontract/modifyRentContractEquipment.jsp");
			//测试 Add By Michael 2011 10/24
			//Output.jspOutput(outputMap, context, "/rentcontract/creditEquipmentMDF.jsp");
			
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
//	修改 合同设备
public void modifyEquipment(Context context) throws SQLException {
	Map outputMap = new HashMap();
	List errList = context.errList;	
	Map map = new HashMap();
	String[] eqmt_ids = HTMLUtil.getParameterValues(context.request, "EQMT_ID", "");
	String[] suppierNames = HTMLUtil.getParameterValues(context.request, "SUPPIER_NAME", "");
	String[] productNames = HTMLUtil.getParameterValues(context.request, "PRODUCT_NAME", "");
	String[] thingNames = HTMLUtil.getParameterValues(context.request, "KIND_NAME", "");
	String[] thingKinds = HTMLUtil.getParameterValues(context.request, "TYPE_NAME", "");	
	String[] SUEQ_IDsOld= HTMLUtil.getParameterValues(context.request, "SUEQ_ID", "");
	String[] suppiers = HTMLUtil.getParameterValues(context.request, "SUPPIER", "");
	String[] thingNumbers = HTMLUtil.getParameterValues(context.request, "NUMBER_NAME", "");
	
	map.put("RECT_ID", context.contextMap.get("RECT_ID"));
	map.put("PRCD_ID", context.contextMap.get("PRCD_ID"));	
	if(errList.isEmpty()){		 	
		try {
			DataAccessor.getSession().startTransaction();
			for (int i=0;i<eqmt_ids.length;i++) {
			
				map.put("EQMT_ID", eqmt_ids[i]); 
				map.put("SUEQ_ID", suppiers[i]); 
				map.put("SUEQ_IDOld", SUEQ_IDsOld[i]); 				
				map.put("THING_KIND", thingKinds[i]); 
				map.put("THING_NAME", thingNames[i]); 
				map.put("MODEL_SPEC", productNames[i]); 
				map.put("BRAND", suppierNames[i]); 
				map.put("THING_NUMBER", thingNumbers[i]);				
				DataAccessor.getSession().update("rentContractEquiMDF.updateCONTRACTEqui", map);
				DataAccessor.getSession().update("rentContractEquiMDF.updateEQUIPMENTDetail", map);
				DataAccessor.getSession().update("rentContractEquiMDF.updateCREDITEQUIPMENT", map);
			}
			DataAccessor.getSession().commitTransaction();
		}catch (Exception e) {
			e.printStackTrace();
			errList.add("合同设备修改错误");
			
		}finally {
			try {
				DataAccessor.getSession().endTransaction();
			} catch (SQLException e) {
				errList.add("合同设备修改关闭事物错误");
				logger.info("合同设备修改关闭事物错误");
				e.printStackTrace();
			}
		}
		if (errList.isEmpty()) {
			Output.jspSendRedirect(context, "defaultDispatcher?__action=rentContractEquiMDF.queryRentContractForShow");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

}	

public void queryCarRentContractForShow (Context context) {
	
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
			dw = (DataWrap) DataAccessor.query("rentContractEquiMDF.queryCarRentContract", context.contextMap, DataAccessor.RS_TYPE.PAGED);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*-------- output --------*/
	outputMap.put("dw", dw);
	outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
	outputMap.put("QSTART_DATE", context.contextMap.get("QSTART_DATE"));
	outputMap.put("QEND_DATE", context.contextMap.get("QEND_DATE"));
	outputMap.put("QSELECT_STATUS", context.contextMap.get("QSELECT_STATUS"));
	outputMap.put("RENTSTAUTS", context.contextMap.get("RENTSTAUTS"));
	outputMap.put("CAR_NUMBER", context.contextMap.get("CAR_NUMBER"));
	
	if(errList.isEmpty()){
		Output.jspOutput(outputMap, context, "/rentcontract/queryCarRentContractForEquiMDF.jsp");
	}else{
		outputMap.put("errList", errList);
		Output.jspOutput(outputMap, context, "/error.jsp");
	}
}


@SuppressWarnings("unchecked")
public void initCarRentUpdate(Context context) {
	
	Map outputMap = new HashMap();
	List errList = context.errList;
	
	Map rentContract = null;
	Map schema = null;
	
	List<Map> typeList = null;
	List<Map> kindList = null;
	List<Map> productList = null;
	List<Map> supplierList = null;
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
	List<Map> manufacturerlist=null;
	/*-------- data access --------*/		
	if(errList.isEmpty()){	
	 
		try {
			outputMap.put("RECT_ID", context.contextMap.get("RECT_ID"));
			outputMap.put("PRCD_ID", context.contextMap.get("PRCD_ID"));
			outputMap.put("productionType", context.contextMap.get("productionType"));
			//
			rentContract = (Map) DataAccessor.query("rentContractEquiMDF.readRentContractByRectId", context.contextMap, DataAccessor.RS_TYPE.MAP);
			outputMap.put("rentContract", rentContract);

			typeList = (List<Map>) DataAccessor.query("suplEquipment.getAllType", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("typeList", typeList);
			outputMap.put("typeJsonList", Output.serializer.serialize(typeList));
			//
			manufacturerlist=(List<Map>) DataAccessor.query("rentContractEquiMDF.getAllManufacturer", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("manufacturerlist", manufacturerlist);
			
			equipmentList = (List<Map>) DataAccessor.query("rentContractEquiMDF.readCarEquipmentByCreditId", context.contextMap, DataAccessor.RS_TYPE.LIST);
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
			}
			outputMap.put("equipmentList", equipmentList);

			Map baseRate = PayRate.getBaseRate();
			outputMap.put("baseRateJson", Output.serializer.serialize(baseRate));

			Map dataDictionaryMap = new HashMap();
		
			dataDictionaryMap.put("dataType", "锁码方式");
			lockList = (List) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("lockList", lockList);
		} catch (Exception e) {
			e.printStackTrace();
			errList.add(e);
		}		
	}		
	if (errList.isEmpty()) {			
		Output.jspOutput(outputMap, context, "/rentcontract/modifyCarRentContractEquipment.jsp");
		
	} else {
		outputMap.put("errList", errList);
		Output.jspOutput(outputMap, context, "/error.jsp");
	}
}


public void modifyCarEquipment(Context context) throws SQLException {
	Map outputMap = new HashMap();
	List errList = context.errList;	
	Map map = new HashMap();
	
	String[] SUEQ_ID= HTMLUtil.getParameterValues(context.request, "SUEQ_ID", "");
	String[] suppiers = HTMLUtil.getParameterValues(context.request, "SUPPIER", "");
	String[] carColor = HTMLUtil.getParameterValues(context.request, "CAR_COLOR", "");
	String[] carRigsterNumber = HTMLUtil.getParameterValues(context.request, "CAR_RIGSTER_NUMBER", "");
	String[] carIDNumber = HTMLUtil.getParameterValues(context.request, "CAR_ID_NUMBER", "");
	String[] carEngineNumber = HTMLUtil.getParameterValues(context.request, "CAR_ENGINE_NUMBER", "");
	String[] carImportDomestic = HTMLUtil.getParameterValues(context.request, "CAR_IMPORT_DOMESTIC", "");
	
	String[] PRCE_ID= HTMLUtil.getParameterValues(context.request, "PRCE_ID", "");
	
	map.put("RECT_ID", context.contextMap.get("RECT_ID"));
	map.put("PRCD_ID", context.contextMap.get("PRCD_ID"));	
	if(errList.isEmpty()){		 	
		try {
			DataAccessor.getSession().startTransaction();
			for (int i=0;i<SUEQ_ID.length;i++) {

				map.put("SUEQ_ID", SUEQ_ID[i]);
				map.put("CAR_COLOR", carColor[i]);	
				map.put("CAR_RIGSTER_NUMBER", carRigsterNumber[i]);
				map.put("CAR_ID_NUMBER", carIDNumber[i]);
				map.put("CAR_ENGINE_NUMBER", carEngineNumber[i]);
				map.put("CAR_IMPORT_DOMESTIC", carImportDomestic[i]);
				
				map.put("PRCE_ID", PRCE_ID[i]);

				DataAccessor.getSession().update("rentContractEquiMDF.updateCarCREDITEQUIPMENT", map);
			}
			DataAccessor.getSession().commitTransaction();
		}catch (Exception e) {
			e.printStackTrace();
			errList.add("重车合同设备修改错误");
			
		}finally {
			try {
				DataAccessor.getSession().endTransaction();
			} catch (SQLException e) {
				errList.add("重车合同设备修改关闭事物错误");
				logger.info("重车合同设备修改关闭事物错误");
				e.printStackTrace();
			}
		}
		if (errList.isEmpty()) {
			String productionType = (String) context.contextMap.get("productionType");
			if("3".equals(productionType)){
				Output.jspSendRedirect(context, "defaultDispatcher?__action=rentContractEquiMDF.queryCarRentContracts");
			}else{
				Output.jspSendRedirect(context, "defaultDispatcher?__action=rentContractEquiMDF.queryCarRentContractForShow");
			}
			
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

}	


public void queryRentContractThingNumberForShow (Context context) {
	
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
			dw = (DataWrap) DataAccessor.query("rentContractEquiMDF.queryRentContractThingNumber", context.contextMap, DataAccessor.RS_TYPE.PAGED);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*-------- output --------*/
	outputMap.put("dw", dw);
	outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
	outputMap.put("QSTART_DATE", context.contextMap.get("QSTART_DATE"));
	outputMap.put("QEND_DATE", context.contextMap.get("QEND_DATE"));
	outputMap.put("QSELECT_STATUS", context.contextMap.get("QSELECT_STATUS"));
	outputMap.put("RENTSTAUTS", context.contextMap.get("RENTSTAUTS"));

	if(errList.isEmpty()){
		Output.jspOutput(outputMap, context, "/rentcontract/queryRentContractForThingNumberMDF.jsp");
	}else{
		outputMap.put("errList", errList);
		Output.jspOutput(outputMap, context, "/error.jsp");
	}
}

@SuppressWarnings("unchecked")
public void initThingNumberRentUpdate(Context context) {
	
	Map outputMap = new HashMap();
	List errList = context.errList;
	
	Map rentContract = null;
	Map schema = null;
	
	List<Map> typeList = null;
	List<Map> kindList = null;
	List<Map> productList = null;
	List<Map> supplierList = null;
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
	List<Map> manufacturerlist=null;
	/*-------- data access --------*/		
	if(errList.isEmpty()){	
	 
		try {
			outputMap.put("RECT_ID", context.contextMap.get("RECT_ID"));
			outputMap.put("PRCD_ID", context.contextMap.get("PRCD_ID"));
			//
			rentContract = (Map) DataAccessor.query("rentContractEquiMDF.readRentContractByRectId", context.contextMap, DataAccessor.RS_TYPE.MAP);
			outputMap.put("rentContract", rentContract);

			typeList = (List<Map>) DataAccessor.query("suplEquipment.getAllType", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("typeList", typeList);
			outputMap.put("typeJsonList", Output.serializer.serialize(typeList));
			//
			manufacturerlist=(List<Map>) DataAccessor.query("rentContractEquiMDF.getAllManufacturer", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("manufacturerlist", manufacturerlist);
			
			equipmentList = (List<Map>) DataAccessor.query("rentContractEquiMDF.getEquipmentByCreditIdForThingNum", context.contextMap, DataAccessor.RS_TYPE.LIST);
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
			}
			outputMap.put("equipmentList", equipmentList);

			Map baseRate = PayRate.getBaseRate();
			outputMap.put("baseRateJson", Output.serializer.serialize(baseRate));

			Map dataDictionaryMap = new HashMap();
		
			dataDictionaryMap.put("dataType", "锁码方式");
			lockList = (List) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("lockList", lockList);
		} catch (Exception e) {
			e.printStackTrace();
			errList.add(e);
		}		
	}		
	if (errList.isEmpty()) {			
		Output.jspOutput(outputMap, context, "/rentcontract/modifyRentContractThingNum.jsp");
		
	} else {
		outputMap.put("errList", errList);
		Output.jspOutput(outputMap, context, "/error.jsp");
	}
}


//修改 合同设备机号留购款
public void modifyThingNumber(Context context) throws SQLException {
Map outputMap = new HashMap();
List errList = context.errList;	
Map map = new HashMap();
String[] eqmt_ids = HTMLUtil.getParameterValues(context.request, "EQMT_ID", "");
String[] SUEQ_IDsOld= HTMLUtil.getParameterValues(context.request, "SUEQ_ID", "");
String[] thingNumbers = HTMLUtil.getParameterValues(context.request, "NUMBER_NAME", "");
String[] staybuyPrice = HTMLUtil.getParameterValues(context.request, "STAYBUY_PRICE", "");


map.put("RECT_ID", context.contextMap.get("RECT_ID"));
map.put("PRCD_ID", context.contextMap.get("PRCD_ID"));	
if(errList.isEmpty()){		 	
	try {
		DataAccessor.getSession().startTransaction();
		for (int i=0;i<eqmt_ids.length;i++) {
		
			map.put("EQMT_ID", eqmt_ids[i]); 
			map.put("SUEQ_IDOld", SUEQ_IDsOld[i]);
			map.put("THING_NUMBER", thingNumbers[i]);	
			DataAccessor.getSession().update("rentContractEquiMDF.updateCONTRACTThingNum", map);
			//增加修改设备表的机号
			DataAccessor.getSession().update("rentContractEquiMDF.updateEqmtThingNum", map);
		}
		/* Yang Yun 2012/11/29 修改保单异常状态*/
		map.put("list_type", "20");
		map.put("s_employeeId", String.valueOf(context.contextMap.get("s_employeeId")));
		DataAccessor.getSession().update("insurance.updateInsuExceptionStatusByRectId", map);
		// 发送Mail
		
		String incpMail = (String) DataAccessor.getSession().queryForObject("insurance.getIncpEmailByRectId", context.contextMap);
		List<Map<String, Object>> dataList = DataAccessor.getSession().queryForList("insurance.getEqmtInfoForChangeNum", context.contextMap);
		String mailContent = getMailContentForChangeNum(dataList);
		MailSettingTo mailSettingTo = new MailSettingTo();
		mailSettingTo.setEmailSubject("修改机号");
		mailSettingTo.setEmailContent(mailContent);
		mailSettingTo.setCreateBy(String.valueOf(context.contextMap.get("s_employeeId")));
		mailSettingTo.setEmailTo(incpMail);
		mailUtilService.sendMail(200,mailSettingTo);
		
		DataAccessor.getSession().commitTransaction();
	}catch (Exception e) {
		e.printStackTrace();
		errList.add("修改合同设备机号留购款错误");
		
	}finally {
		try {
			DataAccessor.getSession().endTransaction();
		} catch (SQLException e) {
			errList.add("修改合同设备机号留购款关闭事物错误");
			logger.info("修改合同设备机号留购款关闭事物错误");
			e.printStackTrace();
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

private String getMailContentForChangeNum(List<Map<String, Object>> dataList){
	if (dataList == null) {
		return null;
	}
	StringBuffer sb = new StringBuffer("<html>" +
			"<head><style type=\"text/css\">" +
			"#dataBody td {border: 1px solid #A6C9E2;} " +
			"#dataBody th {border: 1px solid white;background-color: #A6C9E2;} " +
			"#dataBody table {background-color: white;border: 1px solid #A6C9E2;}" +
			"</style></head>" +
			"<body><div id='dataBody'>");
	if (dataList.size() > 0) {
		sb.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"3\" style=\"width: 1200px;\">");
		sb.append("<tr>");
		sb.append("<th>保单号</th>");
		sb.append("<th>保险公司</th>");
		sb.append("<th>承租人</th>");
		sb.append("<th>设备名称</th>");
		sb.append("<th>设备型号</th>");
		sb.append("<th>设备单价</th>");
		sb.append("<th>新机号</th>");
		sb.append("</tr>");
		for (Map<String, Object> data : dataList) {
			sb.append("<tr>");
			sb.append("<td>" + data.get("INCU_CODE") + "&nbsp;</td>");
			sb.append("<td>" + data.get("INCP_NAME") + "&nbsp;</td>");
			sb.append("<td>" + data.get("CUST_NAME") + "&nbsp;</td>");
			sb.append("<td>" + data.get("THING_NAME") + "&nbsp;</td>");
			sb.append("<td>" + data.get("MODEL_SPEC") + "&nbsp;</td>");
			sb.append("<td>" + data.get("UNIT_PRICE") + "&nbsp;</td>");
			sb.append("<td>" + data.get("THING_NUMBER") + "&nbsp;</td>");
			sb.append("</tr>");
		}
		sb.append("</table>");
	}
	return sb.toString();
}


public static List  getCarConfirm(String a){
	Map map =new HashMap();
	map.put("a", a);
	List list=null;
	System.out.println(a);
	try {
		list = (List) DataAccessor.query("employee.getEmpInforByIdTemp", map, DataAccessor.RS_TYPE.LIST);
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return list;
}

public void queryBusRentContractForPayers (Context context) {
	
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
			dw = (DataWrap) DataAccessor.query("rentContractEquiMDF.queryBusRentContractForPayer", context.contextMap, DataAccessor.RS_TYPE.PAGED);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*-------- output --------*/
	outputMap.put("dw", dw);
	outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
	
	if(errList.isEmpty()){
		Output.jspOutput(outputMap, context, "/rentcontract/queryBusRentContractForPayers.jsp");
	}else{
		outputMap.put("errList", errList);
		Output.jspOutput(outputMap, context, "/error.jsp");
	}
}



public void queryCarRentContracts (Context context) {
	
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
			dw = (DataWrap) DataAccessor.query("rentContractEquiMDF.queryCarRentContracts", context.contextMap, DataAccessor.RS_TYPE.PAGED);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*-------- output --------*/
	outputMap.put("dw", dw);
	outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
	outputMap.put("RENTSTAUTS", context.contextMap.get("RENTSTAUTS"));
	outputMap.put("CAR_NUMBER", context.contextMap.get("CAR_NUMBER"));
	
	if(errList.isEmpty()){
		Output.jspOutput(outputMap, context, "/rentcontract/queryCarRentContracts.jsp");
	}else{
		outputMap.put("errList", errList);
		Output.jspOutput(outputMap, context, "/error.jsp");
	}
}
}

