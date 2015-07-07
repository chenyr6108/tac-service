package com.brick.product.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.exception.ServiceException;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.base.to.SelectionTo;
import com.brick.log.service.LogPrint;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;

/**
 * @author yangxuan
 * @version Created：2010-4-22 下午12:09:46
 *
 */

public class SuplEquipmentService extends BaseCommand {
	Log logger = LogFactory.getLog(SuplEquipmentService.class);
	//private static final Logger logger = Logger.getLogger(SuplEquipmentService.class);
	
	/**得到产品设备的列表**/
	@SuppressWarnings("unchecked")
	public void findAllSuplEquipment(Context context) {
		List errorList = context.errList;
		Map outputMap = context.contextMap;
		DataWrap dw = null;
		if (errorList.isEmpty()) {
			try {			
				dw = (DataWrap) DataAccessor.query("suplEquipment.query", context.contextMap, DataAccessor.RS_TYPE.PAGED);
				outputMap.put("dw", dw);
				outputMap.put("searchValue", context.contextMap.get("searchValue"));
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errorList.add(e);
			}
			//logger.debug("com.brick.product.service.ProductService.finaAllProducts \n"+e.getCause().getMessage());
		}
		if (errorList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/product/supplierEquipment/suplEquipmentList.jsp");
		}
	}
	/**得到产品已起租设备的列表**/
	@SuppressWarnings("unchecked")
	public void findAlreadyUpRentEq(Context context) {
		List errorList = context.errList;
		Map outputMap = context.contextMap;
		PagingInfo<Object> dw = null;
		if (errorList.isEmpty()) {
			try {			
				dw = baseService.queryForListWithPaging("suplEquipment.queryLeaseProduct", context.contextMap, "CREDIT_RUNCODE", ORDER_TYPE.DESC);
				PagingInfo<Object> pagingInfo=null;
				outputMap.put("dw", dw);
				outputMap.put("searchValue", context.contextMap.get("searchValue"));
				Map price = (Map) DataAccessor.query("suplEquipment.queryLeaseProductPrice", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("price", price);	
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errorList.add(e);
			}
		}
		if (errorList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/product/supplierEquipment/alreadyUpRentEquipmtList.jsp");
		}
	}
	/**得到产品已起租设备的列表**/
	@SuppressWarnings("unchecked")
	public void findRentCustoms(Context context) {
		List errorList = context.errList;
		Map outputMap = context.contextMap;
//		DataWrap dw = null;
		List rentCustomlist=new  ArrayList();
		if (errorList.isEmpty()) {
			try {			
				rentCustomlist =  (List) DataAccessor.query("suplEquipment.queryRentCustoms", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("rentCustomlist", rentCustomlist);
				outputMap.put("searchValue", context.contextMap.get("searchValue"));
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errorList.add(e);
			}
		}
		if (errorList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/product/supplierEquipment/rentCustoms.jsp");
		}
	}
	/**创建一个产品设备**/
	@SuppressWarnings("unchecked")
	public void createSuplEquipment(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		if (errList.isEmpty()) {
			try {
	     		if (context.contextMap.get("Status_bak")==null || "".equals(context.contextMap.get("Status_bak")) ) context.contextMap.put("Status_bak", "");
				if (context.contextMap.get("Type_bak")==null || "".equals(context.contextMap.get("Type_bak")) ) context.contextMap.put("Type_bak", "");
				if (context.contextMap.get("Remark_bak")==null || "".equals(context.contextMap.get("Remark_bak")) ) context.contextMap.put("Remark_bak", "");
				if (context.contextMap.get("Remark1")==null || "".equals(context.contextMap.get("Remark1")) ) context.contextMap.put("Remark1", "");	
				if (context.contextMap.get("Remark2")==null || "".equals(context.contextMap.get("Remark2")) ) context.contextMap.put("Remark2", "");
				if (context.contextMap.get("Remark3")==null || "".equals(context.contextMap.get("Remark3")) ) context.contextMap.put("Remark3", "");
				if (context.contextMap.get("Remark4")==null || "".equals(context.contextMap.get("Remark4")) ) context.contextMap.put("Remark4", "");
				
				DataAccessor.execute("suplEquipment.create", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
				//logger.debug("ProductServices:createSuplEquipment\n"+e.getMessage());
			} 
		}
		if (errList.isEmpty()) {
				Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=suplEquipment.findAllSuplEquipment");
		}
	}
	/**根据ID得到一个产品设备**/
	@SuppressWarnings("unchecked")
	public void getSuplEquipmentById(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		if (errList.isEmpty()) {
			try {
				outputMap.put("rs",(Map) DataAccessor.query("suplEquipment.queryByid", context.contextMap, DataAccessor.RS_TYPE.MAP));
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
				//logger.debug("ProductServices:getProductById\n"+e.getMessage());
			}
		}
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/product/supplierEquipment/suplEquipmentDetail.jsp");
		}
	}
	/**得到更新的详细数据*/
	@SuppressWarnings("unchecked")
	public void getSuplEquipmentUpdDetail(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		if (errList.isEmpty()) {
			try {
				outputMap.put("rs",(Map) DataAccessor.query("suplEquipment.queryByidForUpate", context.contextMap, DataAccessor.RS_TYPE.MAP));
				/**得到所有的供应商*/
				((Map)outputMap.get("rs")).put("allSuppliers",(List)DataAccessor.query("suplEquipment.getAllSupplier", context.contextMap, DataAccessor.RS_TYPE.LIST));
				/**得到所有的类型*/
				((Map)outputMap.get("rs")).put("allTypes", (List)DataAccessor.query("suplEquipment.getAllTypes", context.contextMap, DataAccessor.RS_TYPE.LIST));
				//查询 有多少关联多少案件
				context.contextMap.put("checkType", "SUEQ") ;
				context.contextMap.put("ID", context.contextMap.get("sueq_id").toString()) ;
				outputMap.put("count", DataAccessor.query("suplEquipment.checkCreditExist", context.contextMap, RS_TYPE.OBJECT)) ;
				//查询 有多少关联多少案件  结束
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
				//logger.debug("ProductServices:getSuplEquipmentUpdDetail\n"+e.getMessage());
			} 
		}
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/product/supplierEquipment/suplEquipmentUpdate.jsp");
		}
	}
	/**根据ID更新一个供应商提供的产品的型号**/
	@SuppressWarnings("unchecked")
	public void updateSuplEquipmentById(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		if (errList.isEmpty()) {
			try {
				DataAccessor.execute("suplEquipment.updateById", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			} catch (Exception e) {
				//logger.debug("com.brick.product_type.service.updateSuplEquipmentById:cause"+e.getCause().getMessage());
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=suplEquipment.findAllSuplEquipment");
		}
	}
	
	/**根据ID去删除一个供应商提供的设备型号**/
	@SuppressWarnings("unchecked")
	public void deleteSuplEquipmentById(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		if (errList.isEmpty()) {
			try {
				DataAccessor.execute("suplEquipment.deleteById", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
				//logger.debug("SuplEquipmentService:delteById:\n"+e.getCause().getMessage());
			} 
		}
		if (errList.isEmpty()) {
				Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=suplEquipment.findAllSuplEquipment");
		}
	}
	/**得到所有的供应商*/
	@SuppressWarnings("unchecked")
	public void getAllSupplier(Context context) {
		List errList = context.errList;
		List outputList = null;
		if (errList.isEmpty()) {
			try {
				outputList = (List)DataAccessor.query("suplEquipment.getAllSupplier", context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
				//logger.debug("SuplEquipmentService:getAllSupplier:\n"+e.getCause().getMessage());
			} 
		}
		if (errList.isEmpty()) {
				Output.jsonArrayOutput(outputList, context);
		}
	}
	/**得到所有的产品的类型*/
	public void getAllType(Context context) {
		List errList = context.errList;
		List outputList = null;
		if (errList.isEmpty()) {
			try {
				outputList = (List)DataAccessor.query("suplEquipment.getAllTypes", context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
				//logger.debug("SuplEquipmentService:getAllType:\n"+e.getCause().getMessage());
			} 
		}
		if (errList.isEmpty()) {
				Output.jsonArrayOutput(outputList, context);
		}
	}
	/**根据产品的类型ID加载产品*/
	public void getKindByTypeId(Context context) {
		List errList = context.errList;
		List outputList = null;
		if (errList.isEmpty()) {
			try {
				outputList = (List)DataAccessor.query("suplEquipment.getAllKinds", context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
				//logger.debug("SuplEquipmentService:getKindByTypeId:\n"+e.getCause().getMessage());
			}
		}
		if (errList.isEmpty()) {
			Output.jsonArrayOutput(outputList, context);
		}
	}
	/**根据产品得到产品的类号*/
	@SuppressWarnings("unchecked")
	public void getProductByKindId(Context context) {
		List errList = context.errList;
		List outputList = null;
		if (errList.isEmpty()) {
			try {
				outputList = (List)DataAccessor.query("suplEquipment.getAllProduct", context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
				//logger.debug("SuplEquipmentService:getProductByKindId:\n"+e.getCause().getMessage());
			} 
		}
		if (errList.isEmpty()) {
			Output.jsonArrayOutput(outputList, context);			
		}
	}

	@SuppressWarnings("unchecked")
	public void getAllTypes(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		List types = null; 
		List insureTypeList=null;
		insureTypeList=new ArrayList();
		List manufacturer = null ;
		if (errList.isEmpty()) {
			try {
				types = (List) DataAccessor.query("suplEquipment.getAllType", context.contextMap, DataAccessor.RS_TYPE.LIST);
				manufacturer = (List) DataAccessor.query("suplEquipment.getAllManufacturer", context.contextMap, DataAccessor.RS_TYPE.LIST);
				//
				insureTypeList = (List<Map>) DataAccessor.query("insureType.queryInsureTypeList", context.contextMap, DataAccessor.RS_TYPE.LIST);		
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("types", types);
			outputMap.put("manufacturer", manufacturer);
			outputMap.put("insureTypeList", insureTypeList);
			Output.jsonOutput(outputMap, context);
		}
	}
	
	public void getSupl(Context context){
		List<SelectionTo> data = null;
		try {
			data = (List<SelectionTo>) baseService.queryForList("suplEquipment.getSupl");
		} catch (ServiceException e) {
			logger.error(e);
		}
		Output.jsonArrayOutputForObject(data, context);
	}
	
	public void getProduct(Context context){
		List<SelectionTo> data = null;
		try {
			data = (List<SelectionTo>) baseService.queryForList("suplEquipment.getProduct", context.contextMap);
		} catch (ServiceException e) {
			logger.error(e);
		}
		Output.jsonArrayOutputForObject(data, context);
	}
	
	public void getKind(Context context){
		List<SelectionTo> data = null;
		try {
			data = (List<SelectionTo>) baseService.queryForList("suplEquipment.getKind", context.contextMap);
		} catch (ServiceException e) {
			logger.error(e);
		}
		Output.jsonArrayOutputForObject(data, context);
	}
	
	public void getType(Context context){
		List<SelectionTo> data = null;
		try {
			data = (List<SelectionTo>) baseService.queryForList("suplEquipment.getType", context.contextMap);
		} catch (ServiceException e) {
			logger.error(e);
		}
		Output.jsonArrayOutputForObject(data, context);
	}
	
	public void getManufacturer(Context context){
		List<SelectionTo> data = null;
		try {
			data = (List<SelectionTo>) baseService.queryForList("suplEquipment.getManufacturer", context.contextMap);
		} catch (ServiceException e) {
			logger.error(e);
		}
		Output.jsonArrayOutputForObject(data, context);
	}
	
	@SuppressWarnings("unchecked")
	public void getAllKindsByTypeId(Context context){
		List errList = context.errList;
		Map outputMap = new HashMap();
		List kinds = null;
		String manufacturer = "" ;
		if (errList.isEmpty()) {
			try {
				kinds = (List) DataAccessor.query("suplEquipment.getAllKind",context.contextMap, DataAccessor.RS_TYPE.LIST);
				manufacturer = (String) DataAccessor.query("suplEquipment.getManufacturerByTypeId",context.contextMap, DataAccessor.RS_TYPE.OBJECT);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("kinds", kinds);
			outputMap.put("manufacturer", manufacturer) ;
			Output.jsonOutput(outputMap, context);
		}
	}
	/**
	 * 于秋辰
	 * 根据制造商名称查出厂牌
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getAllTypesByManufacturer(Context context){
		List errList = context.errList;
		Map outputMap = new HashMap();
		List manufacturer = null;
		if (errList.isEmpty()) {
			try {
				manufacturer = (List) DataAccessor.query("suplEquipment.getAllTypeByManufacturer",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("manufacturer", manufacturer);
			Output.jsonOutput(outputMap, context);
		}
	}
	
	public void getAllProductsByKindId(Context context){
		List errList = context.errList;
		Map outputMap = new HashMap();
		List products = null;
		if (errList.isEmpty()) {
			try {
				products = (List) DataAccessor.query("suplEquipment.getAllProducts",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("products", products);
			Output.jsonOutput(outputMap, context);
		}
	}
	
	public void getAllSuppliersByProductId(Context context){
		List errList = context.errList;
		Map outputMap = new HashMap();
		List suppliers = null;
		if (errList.isEmpty()) {
			try {
				suppliers = (List) DataAccessor.query("suplEquipment.getAllSuppliers",context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("suppliers", suppliers);
			Output.jsonOutput(outputMap, context);
		}
	}
	/**根据ProductID去查询该产品型号下设备库中是否有  kk**/
	@SuppressWarnings("unchecked")
	public void getCountByProductId(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList;
		Integer count = null;
		if(errList.isEmpty()) {
			try {
				count = (Integer)DataAccessor.query("suplEquipment.getCount", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if(errList.isEmpty()) {
			outputMap.put("count", count);
			Output.jsonOutput(outputMap, context);
		}else{
			
		}	
	}
	/**根据supplier_id去查询该供应商下设备库中是否有  kk**/
	@SuppressWarnings("unchecked")
	public void getCountBySupplierId(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList;
		Integer count = null;
		if(errList.isEmpty()) {
			try {
				count = (Integer)DataAccessor.query("suplEquipment.getSuppCount", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if(errList.isEmpty()) {
			outputMap.put("count", count);
			Output.jsonOutput(outputMap, context);
		}else{
			
		}	
	}
	/**根据SUEQ_ID去查询该供设备库中产品在设备表中是否有  kk**/
	@SuppressWarnings("unchecked")
	public void getCount(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList;
		Integer count = null;
		if(errList.isEmpty()) {
			try {
				count = (Integer)DataAccessor.query("suplEquipment.getEquCount", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if(errList.isEmpty()) {
			outputMap.put("count", count);
			Output.jsonOutput(outputMap, context);
		}else{
			
		}	
	}
	
	/**
	 * 验证供应商设备是否重复
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void validateProduct(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList;
		Integer count = null;
		if(errList.isEmpty()) {
			try {
				count = (Integer)DataAccessor.query("suplEquipment.validateProduct", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if(errList.isEmpty()) {
			outputMap.put("count", count);
			Output.jsonOutput(outputMap, context);
		}else{
			
		}	
	}
	/**
	 * 验证型号是否重复
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void checkProduct(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList;
		Integer count = null;
		if(errList.isEmpty()) {
			try {
				count = (Integer)DataAccessor.query("suplEquipment.checkProduct", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if(errList.isEmpty()) {
			outputMap.put("count", count);
			Output.jsonOutput(outputMap, context);
		}else{
			
		}	
	}
	/**
	 * 验证机号是否重复
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void checkProductNumber(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList;
		Integer count = null;
		if(errList.isEmpty()) {
			try {
				count = (Integer)DataAccessor.query("suplEquipment.checkProductNumber", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if(errList.isEmpty()) {
			outputMap.put("count", count);
			Output.jsonOutput(outputMap, context);
		}else{
			
		}	
	}
	/**
	 * 验证制造商是否存在
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void checkProductType(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList;
		Integer type_id = null;
		List kindList = null;
		if(context.contextMap.get("type_name") != null){
			if(errList.isEmpty()) {
				try {
					type_id = (Integer)DataAccessor.query("suplEquipment.checkProductType", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
					if(type_id != null && type_id > 0){
						context.contextMap.put("type_id", type_id) ;
						kindList = (List) DataAccessor.query("prdcKind.queryProductByKindId", context.contextMap, DataAccessor.RS_TYPE.LIST) ;
					}
				} catch (Exception e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
			}		
		}
		if(errList.isEmpty()) {
			
			outputMap.put("type_id", type_id);
			outputMap.put("kindList", kindList) ;
			Output.jsonOutput(outputMap, context);
		}else{
		}	
	}	/**
	 * 验证供应商是否存在
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void checkSupplier(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList;
		Integer supplier_id = null;
		if(context.contextMap.get("supplier_name") != null){
			if(errList.isEmpty()) {
				try {
					supplier_id = (Integer)DataAccessor.query("suplEquipment.checkSupplier", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
				} catch (Exception e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
			}		
		}
		if(errList.isEmpty()) {
			outputMap.put("supplier_id", supplier_id);
			Output.jsonOutput(outputMap, context);
		}else{
		}	
	}
	
	/**
	 * 2011-10-19 于秋辰
	 * 查询设备是否与报告管理
	 * checkType 要搜索的类型
	 * 		ID 设备ID
	 * 		SUEQ 产品
	 * 		SUPPL 供应商
	 * 		TYPE 厂牌或制造商
	 * 		PRODUCT 型号
	 * 		KIND 名称
	 * 返回count 0未关联,1关联,2关联可用户为gly可以继续操作
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void checkCreditExist(Context context) {
		List errList = context.errList ;
		Map outputMap = new HashMap() ;
		try{
			int count = (Integer) DataAccessor.query("suplEquipment.checkCreditExist", context.contextMap, RS_TYPE.OBJECT) ;
			if(count > 0){
				if("1".equals(context.contextMap.get("s_employeeId").toString())){
					outputMap.put("count", 2) ;
				} else {
					outputMap.put("count", 1) ;
				}
			} else {
				outputMap.put("count", 0) ;
			}
		}catch(Exception e){
			e.printStackTrace() ;
			LogPrint.getLogStackTrace(e, logger) ;
			errList.add("检核设备是否存在报告中错误!请联系管理员") ;
		}
		if(errList.isEmpty()){
			Output.jsonOutput(outputMap, context);
		} else {
			outputMap.put("error", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	public void getIsBuyBackBySuplId(Context context) {
		
		Map outputMap=new HashMap();
		String isBuyBack=null;
			try {
				isBuyBack=(String)DataAccessor.query("suplEquipment.getIsBuyBackBySuplId",context.contextMap,DataAccessor.RS_TYPE.OBJECT);
			} catch(Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e,logger);
			}
			outputMap.put("isBuyBack",isBuyBack==null?"N":isBuyBack);
			Output.jsonOutput(outputMap,context);
	}
	
	/* Add by ZhangYizhou on 2014-06-20 Begin */
	/* IT201406077:补全供应商及厂牌的autocomplete功能  */
	public void getSuppliersForHint(Context context) {
		Map outputMap = new HashMap();
		List suppliers = null;
		List errList = context.errList ;
		try {
			suppliers = (List) DataAccessor.query(
					"suplEquipment.getSuppliersForHint", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--供应商自动提示错误!请联系管理员");
		}
		outputMap.put("suppliers", suppliers);
		Output.jsonOutput(outputMap, context);
	}
	
	public void getProductTypesForHint(Context context) {
		Map outputMap = new HashMap();
		List productTypes = null;
		List errList = context.errList ;
		try {
			productTypes = (List) DataAccessor.query(
					"suplEquipment.getProductTypesForHint", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--厂牌自动提示错误!请联系管理员");
		}
		outputMap.put("productTypes", productTypes);
		Output.jsonOutput(outputMap, context);
	}
	/* Add by ZhangYizhou on 2014-06-20 End */
	
}