package com.brick.baseManage.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;

/**
 * 保险险种
 * @author cheng
 * @date 2010 6, 29
 * @version  1.0
 */
public class InsureTypeService extends AService {
	Log logger = LogFactory.getLog(InsureTypeService.class);

	public static final Logger log = Logger.getLogger(InsureTypeService.class);
	@Override
	protected void afterExecute(String action, Context context) {
		super.afterExecute(action, context);
	}

	@Override
	protected boolean preExecute(String action, Context context) {
		return super.preExecute(action, context);
	}
	
	/**
	 * 插入一条险种的信息
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void create(Context context) {
	    
		List errList = context.errList;
		Map outputMap = new HashMap() ;
		if(errList.isEmpty()) {
		    
			try {
			    
				DataAccessor.execute("insureType.create", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
			
			} catch (Exception e) {
			    
				log.error("com.brick.baseManage.service.InsureTypeService.create" + e.getMessage());
				e.printStackTrace();
				errList.add("com.brick.baseManage.service.InsureTypeService.create" + e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--保险险种添加错误!请联系管理员");
			}
		}
		
		if(errList.isEmpty()) {
		    
			Output.jspSendRedirect(context,"defaultDispatcher?__action=insureType.queryInsureTypeAllInfo&__currentPage=1");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	
	/**
	 * 查看一条险保的详细信息 查看使用
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getInsureTypeByIdForShow(Context context) {
	    
		Map outputMap = new HashMap();
		List errList = context.errList;
	
		Map insureType = null;
		
		
		if(errList.isEmpty()) {
		    
			try {
				insureType = (Map)DataAccessor.query("insureType.getInsureTypeById", context.contextMap, DataAccessor.RS_TYPE.MAP);
			
			} catch (Exception e) {
			    
				log.error("com.brick.baseManage.service.InsureTypeService.getInsureTypeByIdForShow" + e.getMessage());
				e.printStackTrace();
				errList.add("com.brick.baseManage.service.InsureTypeService.getInsureTypeByIdForShow" + e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--保险险种查看错误!请联系管理员");
			}
		}
		
		if(errList.isEmpty()) {
		    
			outputMap.put("insuretype", insureType);
			Output.jspOutput(outputMap, context, "/baseManage/insureCompanyManage/insureTypeShow.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	/**
	 * 查看险种的详细信息 AJAX 方法 返回json 格式数据
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getInsureTypeById(Context context) {
	    
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		Map insureType = null;
		
		if(errList.isEmpty()) {
		    
			try {
			    
				insureType = (Map)DataAccessor.query("insureType.getInsureTypeById", context.contextMap, DataAccessor.RS_TYPE.MAP);
			
			} catch (Exception e) {
			    
				log.error("com.brick.baseManage.service.InsureTypeService.getInsureTypeById" + e.getMessage());
				e.printStackTrace();
				errList.add("com.brick.baseManage.service.InsureTypeService.getInsureTypeById" + e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--保险险种查看（AJAX）错误!请联系管理员");
			}
		}
		
		if(errList.isEmpty()) {
		    
			outputMap.put("insuretype", insureType);
			
			//Output.jspOutput(outputMap, context, "/baseManage/insureCompanyManage/insureTypeUpdate.jsp");
			
			Output.jsonOutput(outputMap, context);
		}
	}
	/**
	 * 更新险种的信息
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void update(Context context) {
	    
		List errList = context.errList;
		Map outputMap = new HashMap() ;
		if(errList.isEmpty()) {
			try {
			    
				DataAccessor.execute("insureType.update", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			
			} catch (Exception e) {
			    
				log.error("com.brick.baseManage.service.InsureTypeService.update" + e.getMessage());
				
				errList.add("com.brick.baseManage.service.InsureTypeService.update" + e.getMessage());
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--保险险种更新错误!请联系管理员");
			}
		}
		
		if(errList.isEmpty()) {
		    
			Output.jspSendRedirect(context,"defaultDispatcher?__action=insureType.queryInsureTypeAllInfo");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	/**
	 * 删除一条险种信息
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void delete(Context context){
	    
		List errList = context.errList;
		Map outputMap = new HashMap() ;
		if(errList.isEmpty()) {
			try {
			    
				DataAccessor.execute("insureType.delete", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			
			} catch (Exception e) {
			    
				log.error("com.brick.baseManage.service.InsureTypeService.delete" + e.getMessage());
				
				errList.add("com.brick.baseManage.service.InsureTypeService.delete" + e.getMessage());
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--保险险种删除错误!请联系管理员");
			}
		}
		
		if(errList.isEmpty()) {
		    
			Output.jspSendRedirect(context,"defaultDispatcher?__action=insureType.queryInsureTypeAllInfo");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	/**
	 * 查找所有符合条件的险种
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryInsureTypeAllInfo(Context context) {
	    
		Map outputMap = new HashMap();
		List errList = context.errList;
	
		DataWrap dw = null;
		
		
		if(errList.isEmpty()) {
			try {
			    
				dw = (DataWrap) DataAccessor.query("insureType.queryInsureTypeAllInfo", context.contextMap, DataAccessor.RS_TYPE.PAGED);
			
			} catch (Exception e) {
			    
				log.error("com.brick.baseManage.service.InsureTypeService.queryInsureTypeAllInfo" + e.getMessage());
				
				errList.add("com.brick.baseManage.service.InsureTypeService.queryInsureTypeAllInfo" + e.getMessage());
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--保险险种列表错误!请联系管理员");
			}
		}
		
		
		if(errList.isEmpty()) {
		    
			outputMap.put("dw", dw);
			outputMap.put("content", context.contextMap.get("content"));
			
			outputMap.put("intp_type", context.contextMap.get("intp_type"));
			outputMap.put("motor_flag", context.contextMap.get("motor_flag"));
			
			Output.jspOutput(outputMap, context,"/baseManage/insureCompanyManage/insureTypeManager.jsp");
		
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}

	
}
