package com.brick.baseManage.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;

public class ActivityLoggingService extends AService {
	Log logger = LogFactory.getLog(ActivityLoggingService.class) ;
	
	//分页列表
	@SuppressWarnings("unchecked")
	public void queryActLog(Context context) {
		Map outputMap = new HashMap() ;
		DataWrap dw = null ;
		List dataType = null ;
		List errList = context.errList ;
		try{
			dataType = (List<Map>)DictionaryUtil.getDictionary("案件状况分类");
			dw = (DataWrap) DataAccessor.query("activityLogging.queryActLog", context.contextMap, DataAccessor.RS_TYPE.PAGED) ;
		}catch(Exception e){
			e.printStackTrace() ;
			LogPrint.getLogStackTrace(e, logger) ;
			errList.add("系统设置--活动日志列表错误!请联系管理员") ;
		}
		outputMap.put("dw",dw) ;
		outputMap.put("dataType", dataType);
		outputMap.put("searchActLogType", context.contextMap.get("searchActLogType")) ;
		outputMap.put("content", context.contextMap.get("content"));
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/baseManage/actLogManage/actLogManager.jsp") ;
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	//添加页初始化
	@SuppressWarnings("unchecked")
	public void createPreActLog(Context context){
		Map outputMap = new HashMap() ;
		List fileInfor = null;
		List errList = context.errList ;
		try{
			fileInfor = (List<Map>)DictionaryUtil.getDictionary("案件状况分类");
			outputMap.put("dataType", fileInfor); 
		}catch(Exception e){
			e.printStackTrace() ;
			LogPrint.getLogStackTrace(e, logger) ;
			errList.add("系统设置--活动日志添加页初始化错误!请联系管理员") ;
		}
		if(errList.isEmpty() ){
			Output.jspOutput(outputMap, context, "/baseManage/actLogManage/actLogCreate.jsp") ;
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
		
	}
	
	//添加
	@SuppressWarnings("unchecked")
	public void createActLog(Context context) {
		List errList = context.errList ;
		Map outputMap = new HashMap() ;
		try{
			DataAccessor.execute("activityLogging.createActLog", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT) ;
		}catch(Exception e){
			e.printStackTrace() ;
			LogPrint.getLogStackTrace(e, logger) ;
			errList.add("系统设置--活动日志添加错误!请联系管理员") ;
		}
		if(errList.isEmpty()){
			Output.jspSendRedirect(context, "defaultDispatcher?__action=activityLogging.queryActLog") ;
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	//修改页初始化
	@SuppressWarnings("unchecked")
	public void updatePreActLog(Context context) {
		Map outputMap = new HashMap() ;
		List dataType = null;
		List errList = context.errList ;
		try{
			dataType = (List<Map>)DictionaryUtil.getDictionary("案件状况分类");
			outputMap.put("dataType", dataType); 
			outputMap.put("actlog",DataAccessor.query("activityLogging.updatePreActLog", context.contextMap, DataAccessor.RS_TYPE.MAP)) ;
		}catch(Exception e){
			e.printStackTrace() ;
			LogPrint.getLogStackTrace(e, logger) ;
			errList.add("系统设置--活动日志修改页初始化错误!请联系管理员") ;
		}
		if(errList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/baseManage/actLogManage/actLogUpdate.jsp") ;
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	//修改
	public void updateActLog(Context context){
		Map outputMap = new HashMap() ;
		List errList = context.errList ;
		try{
			DataAccessor.execute("activityLogging.updateActLog", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE) ;
		}catch(Exception e){
			e.printStackTrace() ;
			LogPrint.getLogStackTrace(e, logger) ;
			errList.add("系统设置--活动日志修改错误!请联系管理员") ;
		}
		if(errList.isEmpty()){
			Output.jspSendRedirect(context, "defaultDispatcher?__action=activityLogging.queryActLog") ;
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	//删除
	public void deleteActLog(Context context){
		Map outputMap = new HashMap() ;
		List errList = context.errList ;
		try{
			DataAccessor.execute("activityLogging.deleteActLog", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE) ;
		}catch(Exception e){
			e.printStackTrace() ;
			LogPrint.getLogStackTrace(e, logger) ;
			errList.add("系统设置--活动日志删除错误!请联系管理员") ;
		}
		if(errList.isEmpty()){
			Output.jspSendRedirect(context, "defaultDispatcher?__action=activityLogging.queryActLog") ;
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	//查看
	@SuppressWarnings("unchecked")
	public void showActLog(Context context){
		Map outputMap = new HashMap() ;
		List dataType = null;
		List errList = context.errList ;
		try{
			dataType = (List<Map>)DictionaryUtil.getDictionary("案件状况分类");
			outputMap.put("dataType", dataType); 
			outputMap.put("actlog",DataAccessor.query("activityLogging.updatePreActLog", context.contextMap, DataAccessor.RS_TYPE.MAP)) ;
		}catch(Exception e){
			e.printStackTrace() ;
			LogPrint.getLogStackTrace(e, logger) ;
			errList.add("系统设置--查看活动日志错误!请联系管理员") ;
		}
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/baseManage/actLogManage/actLogShow.jsp") ;
		} else {
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
		
	}
}
