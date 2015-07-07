package com.brick.product.service;


import java.util.*;

import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.OPERATION_TYPE;
import com.brick.service.core.Output;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.DataUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.birt.chart.computation.LabelLimiter.Option;

import com.brick.base.command.BaseCommand;
import com.brick.base.to.PagingInfo;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.log.service.LogPrint;


/**
 * 产品名称管理
 * @author 康侃
 * @version Created：2010-7-5 
 *
 */

public class ProductKindService extends BaseCommand {
	Log logger = LogFactory.getLog(ProductKindService.class);
//	private static final String SUCCESS = "<script type=\"text/javascript\">alert(\"操作成功!\")</script>";
//	private static final String ERROR = "<script type=\"text/javascript\">alert(\"操作失败!\")</script>";
	/**得到全部产品名称的列表**/
	@SuppressWarnings("unchecked")
	public void findAllProductKind(Context context) {
		List errorList = context.errList;
		Map outputMap = context.contextMap;
		PagingInfo<Object> dw = null;
		List dataType = null ;
		if (errorList.isEmpty()){
			try {				
				outputMap.put("dataType",dataType);
				context.contextMap.put("type", "标的物分类");
				
				List typeList1=(List)DataAccessor.query("prdcKind.getMaintaninceType1", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("typeList1",typeList1);
				
				List typeList2=(List)DataAccessor.query("prdcKind.getMaintaninceType2", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("typeList2",typeList2);
				dw = baseService.queryForListWithPaging("prdcKind.query", context.contextMap, "TYPE1");
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			} 	
		}
		if (errorList.isEmpty()){
			outputMap.put("dw", dw);
			outputMap.put("searchValue", context.contextMap.get("searchValue"));
			outputMap.put("TYPE1", context.contextMap.get("TYPE1"));
			outputMap.put("TYPE2", context.contextMap.get("TYPE2"));
			outputMap.put("PRODUCT_LEVEL", context.contextMap.get("PRODUCT_LEVEL"));
			Output.jspOutput(outputMap, context, "/product/productKind/productKindList.jsp");
		}else{
			
		}
	}
	
	/**添加页面 加载产品类型**/
	@SuppressWarnings("unchecked")
	public void queryTypeName(Context context) {
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
		if (errList.isEmpty()){
			Output.jsonArrayOutput(outputList, context);
		}
	}
	
	/**创建一个产品类型**/
	@SuppressWarnings("unchecked")
	public void createProduct(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		if (errList.isEmpty()) {
			try {
				//context.contextMap.put("Type", Integer.parseInt(context.contextMap.get("Type")==null?"0":(String)context.getContextMap().get("Type")));
				//context.contextMap.put("Name", (String)context.contextMap.get("Name"));
				//context.contextMap.put("Memo",(String) context.contextMap.get("Memo"));
				DataAccessor.execute("prdcKind.create", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} 
			if (errList.isEmpty()){
				Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=productKind.findAllProductKind");
			}else{
				
			}
		}
	}
	/**根据ID得到一个产品类型**/
	@SuppressWarnings("unchecked")
	public void getProductById(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		Map productKind = null;
		List productType = null;
		List dataType = null ;
		if (errList.isEmpty()) {
			try {
				productKind = (Map) DataAccessor.query("prdcKind.queryByid", context.contextMap, DataAccessor.RS_TYPE.MAP);
				productType = (List) DataAccessor.query("prdcType.queryName", context.contextMap, DataAccessor.RS_TYPE.LIST);
				dataType =(List<Map>)DictionaryUtil.getDictionary("产品名称中的产品类型");
				outputMap.put("dataType",dataType);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} 
		}
		if (errList.isEmpty()) {
			outputMap.put("productKind", productKind);
			outputMap.put("productType", productType);
			Output.jsonOutput(outputMap, context);
		} else {

		}
	}	

	/**根据ID更新产品类型**/
	@SuppressWarnings("unchecked")
	public void updateProductById(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		if (errList.isEmpty()) {
			try {
				//context.contextMap.put("type_id", Integer.parseInt((String)context.contextMap.get("type_id")));
				//context.contextMap.put("Type", Integer.parseInt((String)context.contextMap.get("Type")));
				//context.contextMap.put("Create_date",new SimpleDateFormat("yyyy-MM-dd").parse((String)context.contextMap.get("Create_date")));
				DataAccessor.execute("prdcKind.updateById", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);				
			} catch (Exception e) {
				e.printStackTrace();	
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		if (errList.isEmpty()) {			
				Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=productKind.findAllProductKind");
		}else{
			
		}
	}
	
	/**根据ID去删除一个产品的类型**/
	@SuppressWarnings("unchecked")
	public void deleteProductById(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		if (errList.isEmpty()) {
			try {
				DataAccessor.execute("prdcKind.deleteById", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);			
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		if (errList.isEmpty()) {
				//context.request.getSession().setAttribute("msg", SUCCESS);
				Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=productKind.findAllProductKind");
		}else{
			
		}
	}
	
	/**根据typeID去查询该类型下有无产品**/
	@SuppressWarnings("unchecked")
	public void getKindCountByTypeId(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList;
		Integer count = null;
		if(errList.isEmpty()) {
			try {
				count = (Integer)DataAccessor.query("prdcKind.getKindCount", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
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
	
	/** 产品名称是否重复 **/
	@SuppressWarnings("unchecked")
	public void checkKind(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList;
		int count = 0;
		if(errList.isEmpty()) {
			try {
				context.contextMap.put("type_id", DataUtil.intUtil(context.request.getParameter("type_id")));
				context.contextMap.put("name", context.request.getParameter("name"));
				count = (Integer)DataAccessor.query("prdcKind.checkKindCount", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
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
	
	public void getProductType2(Context context) {
		
		List<Map<String,String>> resultList=null;
		try {
			resultList=(List<Map<String,String>>)DataAccessor.query("prdcKind.getMaintaninceType2", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Output.jsonArrayListOutput(resultList,context);
	}
	
	public void getProductType1(Context context) {
		
		List<Map<String,String>> resultList=null;
		try {
			resultList=(List<Map<String,String>>)DataAccessor.query("prdcKind.getMaintaninceType1", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Output.jsonArrayListOutput(resultList,context);
	}
	
}
