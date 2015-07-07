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
 * 产品编号管理
 * @author 于秋辰
 * @version Created：2011-05-25
 *
 */

public class ProductNumberService extends AService {
	Log logger = LogFactory.getLog(ProductNumberService.class);
	/**得到产品编号的列表**/
	@SuppressWarnings("unchecked")
	public void findAllProductNumber(Context context) {
		List errorList = context.errList;
		Map outputMap = context.contextMap;
		DataWrap dw = null;
		if (errorList.isEmpty()) {
			try {
				dw = (DataWrap) DataAccessor.query("productNumber.query", context.contextMap, DataAccessor.RS_TYPE.PAGED);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errorList.add(e);
			}
		}
		if (errorList.isEmpty()) {
			outputMap.put("dw", dw);
			outputMap.put("searchValue", context.contextMap.get("searchValue"));
			Output.jspOutput(outputMap, context, "/product/productNumber/productNumberList.jsp");
		}
	}
//	/**取得所有的类型名称*/
//	@SuppressWarnings("unchecked")
//	public void getNumberName(Context context) {
//		List errList = context.errList;
//		List outputList = null;
//		if (errList.isEmpty()) {
//			try {
//				outputList = (List)DataAccessor.query("prdcType.queryName", context.contextMap, DataAccessor.RS_TYPE.LIST);
//			} catch (Exception e) {
//				e.printStackTrace();
//				LogPrint.getLogStackTrace(e, logger);
//				errList.add(e);
//			} 
//		}
//		if (errList.isEmpty()) {
//				Output.jsonArrayOutput(outputList, context);
//		}
//	}
//	public void queryNumberByKindId(Context context) {
//		List errList = context.errList;
//		List outputList = null;
//		if (errList.isEmpty()) {
//			try {
//				outputList = (List)DataAccessor.query("prdcKind.queryProductByKindId", context.contextMap, DataAccessor.RS_TYPE.LIST);
//			} catch (Exception e) {
//				e.printStackTrace();
//				LogPrint.getLogStackTrace(e, logger);
//				errList.add(e);
//			} 
//		}
//		if (errList.isEmpty()) {
//			Output.jsonArrayOutput(outputList, context);
//		}
//	}
//	public void queryNumberByProductId(Context context) {
//		List errList = context.errList;
//		List outputList = null;
//		if (errList.isEmpty()) {
//			try {
//				outputList = (List)DataAccessor.query("productProduct.queryNumberByProductId", context.contextMap, DataAccessor.RS_TYPE.LIST);
//			} catch (Exception e) {
//				e.printStackTrace();
//				LogPrint.getLogStackTrace(e, logger);
//				errList.add(e);
//			} 
//		}
//		if (errList.isEmpty()) {
//			Output.jsonArrayOutput(outputList, context);
//		}
//	}
//	/**创建一个产品型号**/
//	@SuppressWarnings("unchecked")
//	public void createNumber(Context context) {
//		List errList = context.errList;
//		Map outputMap = new HashMap();
//		if (errList.isEmpty()) {
//			try {
//				DataAccessor.execute("productNumber.create", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
//			} catch (Exception e) {
//				e.printStackTrace();
//				LogPrint.getLogStackTrace(e, logger);
//				errList.add(e);
//			} 
//		}
//		if (errList.isEmpty()) {
//				Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=productNumber.findAllProductNumber");
//		}
//	}	
//	/**根据ID得到一个产品型号**/
//	@SuppressWarnings("unchecked")
//	public void getNumberById(Context context) {
//		List errList = context.errList;
//		Map outputMap = new HashMap();
//		Map productNumber = null;
//		List productType = null;
//		List productKind = null;
//		List productProduct = null;
//		if (errList.isEmpty()) {
//			try {
//				productNumber = (Map) DataAccessor.query("productNumber.queryByid", context.contextMap, DataAccessor.RS_TYPE.MAP);
//				productType = (List) DataAccessor.query("prdcType.queryName", context.contextMap, DataAccessor.RS_TYPE.LIST);
//				context.contextMap.put("type_id", productNumber.get("TYPE_ID"));
//				productKind = (List) DataAccessor.query("prdcKind.queryProductByKindId", context.contextMap, DataAccessor.RS_TYPE.LIST);
//				context.contextMap.put("kind_id", productNumber.get("KIND_ID"));
//				productProduct = (List) DataAccessor.query("productProduct.queryNumberByProductId", context.contextMap, DataAccessor.RS_TYPE.LIST);
//			} catch (Exception e) {
//				e.printStackTrace();
//				LogPrint.getLogStackTrace(e, logger);
//				errList.add(e);
//			} 
//		}
//		if (errList.isEmpty()) {
//			outputMap.put("productNumber", productNumber);
//			outputMap.put("productProduct", productProduct);
//	    	outputMap.put("productType", productType);
//	    	outputMap.put("productKind", productKind);
//			Output.jsonOutput(outputMap, context);
//		} else {
//
//		}
//	}
//	
//	/**根据ID去删除一个产品的几号号**/
//	@SuppressWarnings("unchecked")
//	public void deleteNumberById(Context context) {
//		List errList = context.errList;
//		Map outputMap = new HashMap();
//		if (errList.isEmpty()) {
//			try {
//				DataAccessor.execute("productNumber.deleteById", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
//			} catch (Exception e) {
//				e.printStackTrace();
//				LogPrint.getLogStackTrace(e, logger);
//				errList.add(e);
//			} 
//		}
//		if (errList.isEmpty()) {
//			Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=productNumber.findAllProductNumber");
//		}
//	}
//		
//	/**根据ID更新产品型号**/
//	@SuppressWarnings("unchecked")
//	public void updateNumberById(Context context) {
//		List errList = context.errList;
//		Map outputMap = new HashMap();
//		if (errList.isEmpty()) {
//			try {
//				DataAccessor.execute("productNumber.updateById", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
//			} catch (Exception e) {
//				e.printStackTrace();
//				LogPrint.getLogStackTrace(e, logger);
//				errList.add(e);
//			} 
//		}
//		if (errList.isEmpty()) {
//				Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=productNumber.findAllProductNumber");
//		}
//	}
//	/**根据kindID去查询该产品下有无产品型号**/
//	@SuppressWarnings("unchecked")
//	public void getNumberCountByKindId(Context context){
//		Map outputMap = new HashMap();
//		List errList = context.errList;
//		Integer count = null;
//		if(errList.isEmpty()) {
//			try {
//				count = (Integer)DataAccessor.query("productNumber.getNumberCount", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
//			} catch (Exception e) {
//				e.printStackTrace();
//				LogPrint.getLogStackTrace(e, logger);
//				errList.add(e);
//			}
//		}		
//		if(errList.isEmpty()) {
//			outputMap.put("count", count);
//			Output.jsonOutput(outputMap, context);
//		}else{
//			
//		}	
//	}

}
