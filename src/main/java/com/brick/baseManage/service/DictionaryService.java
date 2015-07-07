package com.brick.baseManage.service;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import org.apache.log4j.Logger;

import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.web.HTMLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.log.service.LogPrint;



/***
 * @author cheng
 * @version 2010-7-6
 *  数据字典操作
 */
public class DictionaryService extends AService{
	private static Logger logger = Logger.getLogger(DictionaryService.class);
	Log logg= LogFactory.getLog(DictionaryService.class);
	
	/**
	 * create dictionary
	 * 
	 * */
	@SuppressWarnings("unchecked")
	public void createDictionary(Context context) {
	    
		List errList = context.errList;
		Map  outputMap = context.contextMap;
		
		String[] defaultValue= HTMLUtil.getParameterValues(context.getRequest(),"DEFAULT_VALUE", "");
		String[] status = HTMLUtil.getParameterValues(context.getRequest(), "STATUS", "");
		String[] flag = HTMLUtil.getParameterValues(context.getRequest(),"FLAG" , "");
		String[] code = HTMLUtil.getParameterValues(context.getRequest(),"CODE" , "");
		String[] type = HTMLUtil.getParameterValues(context.getRequest(),"TYPE" , "");
		String[] remark = HTMLUtil.getParameterValues(context.getRequest(),"REMARK" , "");
		String[] shortName = HTMLUtil.getParameterValues(context.getRequest(),"SHORTNAME" , "");
		String[] level = HTMLUtil.getParameterValues(context.getRequest(),"LEVEL_NUM" , "");
		Map data = new HashMap();
		
		try {
		    
			if (errList.isEmpty()) {
			    				
				DataAccessor.getSession().startTransaction();
        			DataAccessor.getSession().startBatch();	
				
        			    for(int i=0;i<flag.length ;i++){
        			
        				
        				data.put("DEFAULT_VALUE",defaultValue[i] );
        				data.put("STATUS",status[i] );
        				data.put("FLAG",flag[i] );
        				data.put("CODE",code[i] );
        				data.put("TYPE",type[i] );
        				data.put("REMARK",remark[i] );
        				data.put("SHORTNAME",shortName[i] );
        				data.put("LEVEL_NUM",level[i] );
        				
        				data.put("CREATE_USER_ID", context.request.getSession().getAttribute("s_employeeId"));
        				data.put("MODIFY_USER_ID", context.request.getSession().getAttribute("s_employeeId"));
        				
        				
        			    	DataAccessor.getSession().insert("dataDictionary.createDictionary", data);
        			    	
        			    }
			    DataAccessor.getSession().executeBatch();
        			    
			    DataAccessor.getSession().commitTransaction();
			}
			
		     } catch (Exception e) {
		    
			logger.debug("com.brick.dataDictionary.service.DictionaryService.createDictionary.createDictionary"+e.getMessage());
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logg);
			errList.add("系统设置--数据字典添加错误!请联系管理员");
			
			outputMap.put("msg",e);
			
			
		     } finally {
			 
        		    		try {
        				    DataAccessor.getSession().endTransaction();
        				    
        				} catch (SQLException e) {
        				    
        				    logger.debug("com.brick.dataDictionary.service.DictionaryService.createDictionary"+e.getMessage());
        				    e.printStackTrace();
        				    LogPrint.getLogStackTrace(e, logg);
        					errList.add(e);
        				   }
		    
				}
		
		if (errList.isEmpty()) {
		    
		    /**forward to page of list */
			Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=dataDictionary.getAllDictionary");

		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
		
	}
	
	
	/**
	 * delete dictionary
	 * 
	 * */
	@SuppressWarnings("unchecked")
	public void deleteDictionary(Context context) {
		List errList = context.errList;
		Map outputMap = context.contextMap;
		try {
			if (errList.isEmpty()) {
				DataAccessor.execute("dataDictionary.deleteDictionary", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
				
				}
			
			} catch (Exception e) {
			    
			logger.debug("com.brick.dataDictionary.service.DictionaryService.deleteDictionary"+e.getMessage());
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logg);
			errList.add("系统设置--数据字典删除（作废）错误!请联系管理员");
			} finally {
			}
		
		if (errList.isEmpty()) {
			    
			    Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=dataDictionary.getAllDictionary");
			    
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	
	/**
	 * update flag 
	 * 修改 类型下的条目名称
	 * */
	@SuppressWarnings("unchecked")
	public void updateFlag(Context context) {
		List errList = context.errList;
		Map outputMap = context.contextMap;
		
		try {
			if (errList.isEmpty()) {
				DataAccessor.execute("dataDictionary.updateFlag", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			}
		} catch (Exception e) {
			logger.debug("com.brick.dataDictionary.service.DictionaryService.updateFlag"+e.getMessage().toString());
			e.printStackTrace();
			outputMap.put("msg", e);
			LogPrint.getLogStackTrace(e, logg);
			errList.add("系统设置--数据字典修改类型下条目 错误!请联系管理员");
		} finally {
			/**forward to page of list*/
			if(errList.isEmpty()){
				String type = (String) context.contextMap.get("type");
				Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=dataDictionary.getTypeDetail&execute=1&type="+type);
			} else {
				outputMap.put("errList", errList) ;
				Output.jspOutput(outputMap, context, "/error.jsp") ;
			}
		}
	}
	
	
	
	///感觉不是同一连接
	/**
	 * update Dictionary 
	 * 修改
	 * */
	@SuppressWarnings("unchecked")
	public void updateDictionaryByid(Context context) {
		List errList = context.errList;
		Map outputMap = context.contextMap;
		
		String[] defaultValue= HTMLUtil.getParameterValues(context.getRequest(),"DEFAULT_VALUE", "");
		String[] status = HTMLUtil.getParameterValues(context.getRequest(), "STATUS", "");
		String[] flag = HTMLUtil.getParameterValues(context.getRequest(),"FLAG" , "");
		String[] code = HTMLUtil.getParameterValues(context.getRequest(),"CODE" , "");
		String[] type = HTMLUtil.getParameterValues(context.getRequest(),"TYPE" , "");
		String[] remark = HTMLUtil.getParameterValues(context.getRequest(),"REMARK" , "");
		String[] shortName = HTMLUtil.getParameterValues(context.getRequest(),"SHORTNAME" , "");
		String[] level = HTMLUtil.getParameterValues(context.getRequest(),"LEVEL_NUM" , "");
		String[] id = HTMLUtil.getParameterValues(context.getRequest(),"DATA_ID","");
		Map data = new HashMap();

		
		
		try {
			if (errList.isEmpty()) {
			    	
			    	DataAccessor.getSession().startTransaction();
    				DataAccessor.getSession().startBatch();	
			    	
    				int i=0;
				 for( ;i<id.length ;i++){
	        			
     				
     				data.put("DEFAULT_VALUE",defaultValue[i] );
     				data.put("STATUS",status[i] );
     				data.put("FLAG",flag[i] );
     				data.put("CODE",code[i] );
     				data.put("TYPE",type[i] );
     				data.put("REMARK",remark[i] );
     				data.put("SHORTNAME",shortName[i] );
     				data.put("LEVEL_NUM",level[i] );
     				data.put("DATA_ID", id[i]);
     				
     				data.put("MODIFY_USER_ID", context.request.getSession().getAttribute("s_employeeId"));
     				
     				
     			    	DataAccessor.getSession().update("dataDictionary.updateByid", data);
     			    }
			    DataAccessor.getSession().executeBatch();
     			    
			    DataAccessor.getSession().commitTransaction();
			    
			    if(flag.length > id.length){
				
				DataAccessor.getSession().startTransaction();
        			DataAccessor.getSession().startBatch();	
				
				for(  ;i<flag.length ;i++){
				    
	        				data.put("DEFAULT_VALUE",defaultValue[i] );
	        				data.put("STATUS",status[i] );
	        				data.put("FLAG",flag[i] );
	        				data.put("CODE",code[i] );
	        				data.put("TYPE",type[i] );
	        				data.put("REMARK",remark[i] );
	        				data.put("SHORTNAME",shortName[i] );
	        				data.put("LEVEL_NUM",level[i] );
	        				
	        				data.put("CREATE_USER_ID", context.request.getSession().getAttribute("s_employeeId"));
	        				data.put("MODIFY_USER_ID", context.request.getSession().getAttribute("s_employeeId"));
	        				
	        				
	        			    	DataAccessor.getSession().insert("dataDictionary.createDictionary", data);
	        		
				}
				
				    DataAccessor.getSession().executeBatch();
				    DataAccessor.getSession().commitTransaction();
				
			    }
			    
			}
			
		} catch (Exception e) {
		    
			logger.debug("com.brick.dataDictionary.service.DictionaryService.updateDictionaryByid"+e.getMessage());
			e.printStackTrace();
			outputMap.put("msg", e);
			LogPrint.getLogStackTrace(e, logg);
			errList.add("系统设置--数据字典修改页初始化错误!请联系管理员");
			
		} finally {
		    
        		    	try {
        		    	    
        			    DataAccessor.getSession().endTransaction();
        			    
        			} catch (SQLException e) {
        			    
        			    logger.debug("com.brick.dataDictionary.service.DictionaryService.updateDictionaryByid"+e.getMessage());

        			    e.printStackTrace();
        			    LogPrint.getLogStackTrace(e, logg);
        				errList.add(e);
        			}
			
			
			}
		if(errList.isEmpty()){
		    
		    Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=dataDictionary.getAllDictionary");
		
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	
	/**
	 * get all dictionary 
	 * 
	 * */
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
			logger.debug("com.brick.dataDictionary.service.DictionaryService:"+e.getMessage());
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logg);
			errList.add("系统设置--数据字典列表错误!请联系管理员");
		} 
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/baseManage/dataDictionary/dataDictionaryManage.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	
	/**
	 * 查看类型下的条目标示具体信息
	 *  
	 *  */
	@SuppressWarnings("unchecked")
	public void getTypeDetail(Context context) {
//		if (context.contextMap.get("b") != null) {
//			if (context.contextMap.get("b").equals("1")) {
//				context.contextMap.put("type", context.contextMap.get("TYPE"));
//			}
//		}
		
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
			logger.debug("com.brick.dataDictionary.service.DictionaryService:"+e.getMessage());
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logg);
			errList.add("系统设置--数据字典查看错误!请联系管理员");
		} 
		if(errList.isEmpty()){
			if (!"".equals(context.contextMap.get("execute")) &&context.contextMap.get("execute")!=null) {
				if (context.contextMap.get("execute").equals("1")) {
					Output.jspOutput(outputMap, context, "/dataDictionary/dataDictionary_execute.jsp");
				}
			} else {			
				Output.jspOutput(outputMap, context, "/baseManage/dataDictionary/dataDictionaryDetail.jsp");
			}
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	/**
	 * 查看类型下的条目标示具体信息 修改页面跳转
	 *  
	 *  */
	@SuppressWarnings("unchecked")
	public void getDataDetail(Context context){
	    
	    List errList = context.errList;
	    Map outputMap = new HashMap();
	    List data = new ArrayList();
	  
	    	try {
	    	    	if(errList.isEmpty()){
	    	    	    
	    	    	    data =  (List) DataAccessor.query("dataDictionary.getTypeDetail",context.contextMap, RS_TYPE.LIST);
	    	    	    
	    	    	    
	    	    	outputMap.put("result", data);
	    	    	    
	    	    	}
		   
	    	    } 
            	    	catch (Exception e) {
            	    	    
            	    	logger.debug("com.brick.dataDictionary.service.DictionaryService:"+e.getMessage());
            	    	e.printStackTrace();
            	    	LogPrint.getLogStackTrace(e, logg);
            	    	errList.add("系统设置--数据字典修改页初始化错误!请联系管理员");
            
            		}
            		
        		finally{
        		    
        		    
        		    
        		        }
	
        	if(errList.isEmpty()){
        	    Output.jspOutput(outputMap, context,"/baseManage/dataDictionary/dataDictionaryDetail.jsp");
        	} else {
        		outputMap.put("errList", errList) ;
    			Output.jspOutput(outputMap, context, "/error.jsp") ;
        	}
	}
	
	
	
	/**
	 * 检测类型是否存在
	 * 
	 * */
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
			LogPrint.getLogStackTrace(e, logg);
			errList.add("系统设置--数据字典检测类型重复错误!请联系管理员");
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
			LogPrint.getLogStackTrace(e, logg);
			errList.add("系统设置--数据字典修改错误!请联系管理员");
		} 
		if(errList.isEmpty()){
			String type =(String) context.contextMap.get("TYPE");
			Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=dataDictionary.getTypeDetail&b=1&type="+type);
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	
	public void getFlagByType(Context context) throws Exception{
		String code = (String) context.contextMap.get("code");
		List list = DictionaryUtil.getDictionaryForAll(code);
		Map outputMap = new HashMap();
		outputMap.put("list", list);
		Output.jsonOutput(outputMap, context);
	}
}
