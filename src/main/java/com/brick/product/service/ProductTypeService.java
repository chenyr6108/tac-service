package com.brick.product.service;

import java.util.*;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;

/**
 * 产品类型管理
 * @author 康侃
 * @version Created：2010-7-5 
 *
 */

public class ProductTypeService extends AService {
	Log logger = LogFactory.getLog(ProductTypeService.class);
	//public static final String SUCCESS = "<script type=\"text/javascript\">alert(\"操作成功!\")</script>";
	//private static final String ERROR = "<script type=\"text/javascript\">alert(\"操作失败!\")</script>";
	/**得到产品型号的列表**/
	@SuppressWarnings("unchecked")
	public void findAllProductType(Context context) {
		List errorList = context.errList;
		Map outputMap = context.contextMap;
		DataWrap dw = null;
		if (errorList.isEmpty()){
			try {
				dw = (DataWrap) DataAccessor.query("prdcType.query", context.contextMap, DataAccessor.RS_TYPE.PAGED);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errorList.add(e);
			}			
		}
		if (errorList.isEmpty()){
			outputMap.put("dw", dw);
			outputMap.put("searchValue", context.contextMap.get("searchValue"));
			Output.jspOutput(outputMap, context, "/product/productType/productTypeList.jsp");
		} else {
			
		}
	}
	/**创建一个产品型号**/
	@SuppressWarnings("unchecked")
	public void createProduct(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		if (errList.isEmpty()) {
			try {
				DataAccessor.execute("prdcType.create", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
				//context.request.getSession().setAttribute("msg", SUCCESS);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
				//context.request.getSession().setAttribute("msg", ERROR);
			} 
		}
		if (errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=productType.findAllProductType");
		}else{
			
		}
	}
	/**根据ID查看一个产品型号**/
	@SuppressWarnings("unchecked")
	public void getProductTypeById(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		Map productType = null;
		if (errList.isEmpty()) {
			try {
				productType = (Map) DataAccessor.query("prdcType.queryByid", context.contextMap, DataAccessor.RS_TYPE.MAP);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} 
		}
		if (errList.isEmpty()) {
			outputMap.put("productType", productType);
			Output.jsonOutput(outputMap, context);
		} else {

		}
	}

	/**产品型号修改**/
	@SuppressWarnings("unchecked")
	public void updateProduct(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		if (errList.isEmpty()) {
			try {
				//context.contextMap.put("Product_id", Integer.parseInt((String)context.contextMap.get("Product_id")));
				//context.contextMap.put("Status", Integer.parseInt((String)context.contextMap.get("Status")));
				//context.contextMap.put("Create_date",new SimpleDateFormat("yyyy-MM-dd").parse((String)context.contextMap.get("Create_date")));
				DataAccessor.execute("prdcType.update", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} 
		}
		if (errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=productType.findAllProductType");
		}
	}
	
	/**根据ID去删除一个产品的型号**/
	@SuppressWarnings("unchecked")
	public void deleteProductTypeById(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		if (errList.isEmpty()) {
			try {
				DataAccessor.execute("prdcType.deleteById", context.contextMap, DataAccessor.OPERATION_TYPE.DELETE);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} 
		}
		if (errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=productType.findAllProductType");
		}
	}
	/**根据ID去删除一个产品的型号**/
	@SuppressWarnings("unchecked")
	public void checkProductType(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		int count = 0 ;
		if (errList.isEmpty()) {
			try {
				context.contextMap.put("name", context.request.getParameter("name"));
				context.contextMap.put("Manufacturer", context.request.getParameter("Manufacturer"));
				count = (Integer)DataAccessor.query("prdcKind.checkTypeCount", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} 
		}
		if (errList.isEmpty()){
			outputMap.put("count", count);
			Output.jsonOutput(outputMap, context);
		}
	}
}
