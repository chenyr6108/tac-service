package com.brick.dataDictionary.service;
/***
 * @author yangxuan
 * @version Created：2010-6-7
 * function: dictionary操作
 */
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;

public class DictionaryService extends AService{
	private static Logger logger = Logger.getLogger(DictionaryService.class);
	private static final String SUCCESS = "<script type=\"text/javascript\">alert('操作成功!')</script>";
	private static final String ERROR = "<script type=\"text/javascript\">alert('操作失败!')</script>";
	
	/**create dictionary used*/
	@SuppressWarnings("unchecked")
	public void createDictionary(Context context) {
		List errList = context.errList;
		Map  outputMap = context.contextMap;
		try {
			if (errList.isEmpty()) {
				context.contextMap.put("CREATE_USER_ID", context.request.getSession().getAttribute("s_employeeId"));
				context.contextMap.put("MODIFY_USER_ID", context.request.getSession().getAttribute("s_employeeId"));
				context.contextMap.put("STATUS", "0");
				DataAccessor.execute("dataDictionary.createDictionary", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
			}
		} catch (Exception e) {
			logger.debug("com.brick.dataDictionary.service.DictionaryService:"+e.getMessage().toString());
			e.printStackTrace();
			outputMap.put("msg",ERROR);
		} finally {
			/**forward to page of list */
			Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=dataDictionary.getAllDictionary");
		}
	}
	
	/**delete dictionary*/
	@SuppressWarnings("unchecked")
	public void deleteDictionary(Context context) {
		List errList = context.errList;
		Map outputMap = context.contextMap;
		try {
			if (errList.isEmpty()) {
				DataAccessor.execute("dataDictionary.deleteDictionary", context.contextMap, DataAccessor.OPERATION_TYPE.DELETE);
			}
		} catch (Exception e) {
			logger.debug("com.brick.dataDictionary.service.DictionaryService:"+e.getMessage().toString());
			e.printStackTrace();
		} finally {
			/**forward to page of list */
			outputMap.put("msg",SUCCESS);
			Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=dataDictionary.getAllDictionary");
		}
	}
	
	
	/**update flag used*/
	@SuppressWarnings("unchecked")
	public void updateFlag(Context context) {
		List errList = context.errList;
		Map outputMap = context.contextMap;
		
		try {
			if (errList.isEmpty()) {
				DataAccessor.execute("dataDictionary.updateFlag", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			}
		} catch (Exception e) {
			logger.debug("com.brick.dataDictionary.service.DictionaryService:"+e.getMessage().toString());
			e.printStackTrace();
			outputMap.put("msg", ERROR);
		} finally {
			/**forward to page of list*/
			outputMap.put("msg", SUCCESS);
			String type = (String) context.contextMap.get("type");
			Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=dataDictionary.getTypeDetail&execute=1&type="+type);
		}
	}
	
	
	/**update Dictionary used*/
	@SuppressWarnings("unchecked")
	public void updateDictionary(Context context) {
		List errList = context.errList;
		Map outputMap = context.contextMap;
		
		try {
			if (errList.isEmpty()) {
				DataAccessor.execute("dataDictionary.updateDictionary", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			}
		} catch (Exception e) {
			logger.debug("com.brick.dataDictionary.service.DictionaryService:"+e.getMessage().toString());
			e.printStackTrace();
			outputMap.put("msg", ERROR);
		} finally {
			/**forward to page of list*/
			outputMap.put("msg", SUCCESS);
			String type = (String) context.contextMap.get("type");
			Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=dataDictionary.getTypeDetail&execute=1&type="+type);
		}
	}
	
	/**get all dictionary used*/
	@SuppressWarnings("unchecked")
	public void getAllDictionary(Context context) {
		List errList = context.errList;
		Map outputMap = context.contextMap;
		DataWrap dw = null;
		try {
			if (errList.isEmpty()) {
				dw = (DataWrap) DataAccessor.query("dataDictionary.getAllDictionary", context.contextMap, DataAccessor.RS_TYPE.PAGED);
				outputMap.put("dw", dw);
				outputMap.put("searchValue", context.contextMap.get("searchValue"));
				outputMap.put("__pageSize", context.contextMap.get("__pageSize")==null?dw.pageSize:context.contextMap.get("__pageSize"));
				
			}
		} catch (Exception e) {
			logger.debug("com.brick.dataDictionary.service.DictionaryService:"+e.getMessage().toString());
			e.printStackTrace();
		} 
		Output.jspOutput(outputMap, context, "/dataDictionary/dataDictionary_list.jsp");
	}
	
	/**查看类型下的标示 used*/
	@SuppressWarnings("unchecked")
	public void getTypeDetail(Context context) {
		if (context.contextMap.get("b") != null) {
			if (context.contextMap.get("b").equals("1")) {
				context.contextMap.put("type", context.contextMap.get("TYPE"));
			}
		}
		
		List errList = context.errList;
		Map outputMap = context.contextMap;
		DataWrap dw = null;
		try {
			if (errList.isEmpty()) {
				dw = (DataWrap) DataAccessor.query("dataDictionary.getTypeDetail", context.contextMap, DataAccessor.RS_TYPE.PAGED);
				outputMap.put("dw", dw);
				outputMap.put("searchValue", context.contextMap.get("searchValue"));
				outputMap.put("__pageSize", context.contextMap.get("__pageSize")==null?dw.pageSize:context.contextMap.get("__pageSize"));
				
			}
		} catch (Exception e) {
			logger.debug("com.brick.dataDictionary.service.DictionaryService:"+e.getMessage().toString());
			e.printStackTrace();
		} 
		if (!"".equals(context.contextMap.get("execute")) &&context.contextMap.get("execute")!=null) {
			if (context.contextMap.get("execute").equals("1")) {
				Output.jspOutput(outputMap, context, "/dataDictionary/dataDictionary_execute.jsp");
			}
		} else {			
			Output.jspOutput(outputMap, context, "/dataDictionary/dataDictionary_detail.jsp");
		}
	}
	
	/**检测类型是否存在*/
	@SuppressWarnings("unchecked")
	public void checkType(Context context) {
		List errList = context.errList;
		Map outputMap = context.contextMap;
		try {
			int count = 0;
			if (errList.isEmpty()) {
				count = (Integer)DataAccessor.query("dataDictionary.checkType", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
			}
			outputMap.put("count", count);
		} catch (Exception e) {
			logger.debug(e.getCause().getMessage().toString());
			e.printStackTrace();
		} 
		Output.jsonOutput(outputMap, context);
	}
	
	@SuppressWarnings("unchecked")
	public void update(Context context) {
		List errList = context.errList;
		Map outputMap = context.contextMap;
		try {
			if (errList.isEmpty()) {
				DataAccessor.execute("dataDictionary.update", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			}
		} catch (Exception e) {
			logger.debug(e.getCause().getMessage().toString());
			e.printStackTrace();
		} 
		String type =(String) context.contextMap.get("TYPE");
		Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=dataDictionary.getTypeDetail&b=1&type="+type);
	}
	
	@SuppressWarnings("unchecked")
	public void getFlag(Context context) {
		List errList = context.errList;
		Map outputMap = context.contextMap;
		try {
			if (errList.isEmpty()) {
				outputMap.put("rs",(Map)DataAccessor.query("dataDictionary.getFlag", context.contextMap, DataAccessor.RS_TYPE.OBJECT));
			}
		} catch (Exception e) {
			logger.debug(e.getCause().getMessage().toString());
			e.printStackTrace();
		} 
		Output.jspOutput(outputMap, context, "/dataDictionary/dataDictionary_update_detail.jsp");
	}
}
