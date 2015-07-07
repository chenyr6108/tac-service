package com.brick.product.service;



import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;

/**
 * 产品型号管理
 * @author 康侃
 * @version Created：2010-7-7 
 *
 */

public class ProductProductService extends AService {
	Log logger = LogFactory.getLog(ProductProductService.class);
	/**得到产品型号的列表**/
	@SuppressWarnings("unchecked")
	public void findAllProductProduct(Context context) {
		List errorList = context.errList;
		Map outputMap = context.contextMap;
		DataWrap dw = null;
		if (errorList.isEmpty()) {
			try {
				dw = (DataWrap) DataAccessor.query("productProduct.query", context.contextMap, DataAccessor.RS_TYPE.PAGED);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errorList.add(e);
			}
		}
		if (errorList.isEmpty()) {
			outputMap.put("dw", dw);
			outputMap.put("searchValue", context.contextMap.get("searchValue"));
			Output.jspOutput(outputMap, context, "/product/productProduct/productProductList.jsp");
		}
	}
	/**取得所有的类型名称*/
	@SuppressWarnings("unchecked")
	public void getProductName(Context context) {
		List errList = context.errList;
		List outputList = null;
		if (errList.isEmpty()) {
			try {
				outputList = (List)DataAccessor.query("prdcType.queryName", context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} 
		}
		if (errList.isEmpty()) {
				Output.jsonArrayOutput(outputList, context);
		}
	}
	public void queryProductByKindId(Context context) {
		List errList = context.errList;
		List outputList = null;
		if (errList.isEmpty()) {
			try {
				outputList = (List)DataAccessor.query("prdcKind.queryProductByKindId", context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} 
		}
		if (errList.isEmpty()) {
			Output.jsonArrayOutput(outputList, context);
		}
	}
	/**创建一个产品型号**/
	@SuppressWarnings("unchecked")
	public void createProduct(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		if (errList.isEmpty()) {
			try {
				DataAccessor.execute("productProduct.create", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} 
		}
		if (errList.isEmpty()) {
				Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=productProduct.findAllProductProduct");
		}
	}	
	/**根据ID得到一个产品型号**/
	@SuppressWarnings("unchecked")
	public void getProductById(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		Map productProduct = null;
		List productType = null;
		List productKind = null;
		if (errList.isEmpty()) {
			try {
				productProduct = (Map) DataAccessor.query("productProduct.queryByid", context.contextMap, DataAccessor.RS_TYPE.MAP);
				productType = (List) DataAccessor.query("prdcType.queryName", context.contextMap, DataAccessor.RS_TYPE.LIST);
				context.contextMap.put("type_id", productProduct.get("TYPE_ID"));
				productKind = (List) DataAccessor.query("prdcKind.queryProductByKindId", context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} 
		}
		if (errList.isEmpty()) {
			outputMap.put("productProduct", productProduct);
	    	outputMap.put("productType", productType);
	    	outputMap.put("productKind", productKind);
			Output.jsonOutput(outputMap, context);
		} else {

		}
	}
	
	/**根据ID去删除一个产品的型号**/
	@SuppressWarnings("unchecked")
	public void deleteProductById(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		if (errList.isEmpty()) {
			try {
				DataAccessor.execute("productProduct.deleteById", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} 
		}
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=productProduct.findAllProductProduct");
		}
	}
		
	/**根据ID更新产品型号**/
	@SuppressWarnings("unchecked")
	public void updateProductById(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		if (errList.isEmpty()) {
			try {
				DataAccessor.execute("productProduct.updateById", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} 
		}
		if (errList.isEmpty()) {
				Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=productProduct.findAllProductProduct");
		}
	}
	/**根据kindID去查询该产品下有无产品型号**/
	@SuppressWarnings("unchecked")
	public void getProductCountByKindId(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList;
		Integer count = null;
		if(errList.isEmpty()) {
			try {
				count = (Integer)DataAccessor.query("productProduct.getProductCount", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
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

}
