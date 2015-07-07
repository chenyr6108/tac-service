package com.brick.support.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.web.HTMLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;


/**
 * 
 * 资信删除
 * 
 * @author wuzd
 * @date 2010,7,26
 */

public class SupportCreditReportDelete extends AService {
	Log logger = LogFactory.getLog(SupportCreditReportDelete.class);

	/**
	 * 资信管理
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void creditManage(Context context) {
		Map outputMap = new HashMap(); 
		DataWrap dw = null;
		try {
			dw = (DataWrap) DataAccessor.query("creditReportDelete.getCreditReports", context.contextMap,DataAccessor.RS_TYPE.PAGED);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("dw", dw);
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("start_date", context.contextMap.get("start_date"));
		outputMap.put("end_date", context.contextMap.get("end_date"));
		outputMap.put("credit_type", context.contextMap.get("credit_type"));
		Output.jspOutput(outputMap, context, "/support/supportCreditReportDelete.jsp");
	}
	
	/**
	 * 担保人维护
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void creditManageForGuarantor(Context context) {
		Map outputMap = new HashMap(); 
		DataWrap dw = null;
		try {
			dw = (DataWrap) DataAccessor.query("creditReportDelete.getCreditReports", context.contextMap,DataAccessor.RS_TYPE.PAGED);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("dw", dw);
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("start_date", context.contextMap.get("start_date"));
		outputMap.put("end_date", context.contextMap.get("end_date"));
		outputMap.put("credit_type", context.contextMap.get("credit_type"));
		Output.jspOutput(outputMap, context, "/support/supportGuarantor.jsp");
	}
	/**
	 * 删除资信
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void delete(Context context) { 
		try {
			
			HashMap paramMap = new HashMap();
			paramMap.put("credit_id", Integer.parseInt(context.contextMap.get("credit_id").toString()));			
			DataAccessor.execute("creditReportDelete.deleteCreditPro",paramMap,DataAccessor.OPERATION_TYPE.UPDATE);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Output.jspSendRedirect(context,"defaultDispatcher?__action=supportCreditReportDelete.creditManage");
	}
	
	/**
	 * ajax查测该资信是否生成合同
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void checkRent(Context context) {
		Map outputMap = new HashMap();
		Map rentMap = new HashMap();
		Map controlMap =  new HashMap();
		List controlList =null;
		try {
			rentMap = (Map) DataAccessor.query("creditReportDelete.checkRent",context.contextMap, DataAccessor.RS_TYPE.MAP);
			controlMap = (Map) DataAccessor.query("creditReportDelete.checkControlCount",context.contextMap, DataAccessor.RS_TYPE.MAP);
			if (Integer.parseInt(controlMap.get("CNTT").toString())!=0) {
				controlList = (List) DataAccessor.query("creditReportDelete.checkControl",context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("controlList", controlList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} finally {
			outputMap.put("rentMap", rentMap.get("CNT"));
			outputMap.put("controlMap", controlMap.get("CNTT"));
			Output.jsonOutput(outputMap, context);
		}
	}
	
	/**
	 * 基本利率管理
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void propManage(Context context) {
		Map outputMap = new HashMap(); 
		DataWrap dw = null;
		try {
			dw = (DataWrap) DataAccessor.query("creditReportDelete.getProp", context.contextMap,DataAccessor.RS_TYPE.PAGED);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("dw", dw);
		Output.jspOutput(outputMap, context, "/support/supportProportionConfig.jsp");
	}
	
	/**
	 * 删除基本利率
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void deleteProp(Context context) { 
		Map outputMap = new HashMap(); 
		try {		
			DataAccessor.execute("creditReportDelete.deleteProp",context.contextMap,DataAccessor.OPERATION_TYPE.DELETE);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Output.jspSendRedirect(context,"defaultDispatcher?__action=supportCreditReportDelete.propManage");
	}
	
	/**
	 * 更新基本利率
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void updateProp(Context context) { 
		try {	
			Map  data = new HashMap();
			String ID[] = HTMLUtil.getParameterValues(context.request,"ID", "") ;			
			String ADJUST_TIME[] = HTMLUtil.getParameterValues(context.request, "ADJUST_TIME", "") ;
			String SIX_MONTHS[] = HTMLUtil.getParameterValues(context.request, "SIX_MONTHS", "") ;
			String ONE_YEAR[] = HTMLUtil.getParameterValues(context.request, "ONE_YEAR", "") ;
			String ONE_THREE_YEARS[] = HTMLUtil.getParameterValues(context.request, "ONE_THREE_YEARS", "") ;
			String THREE_FIVE_YEARS[] = HTMLUtil.getParameterValues(context.request, "THREE_FIVE_YEARS", "") ;
			String OVER_FIVE_YEARS[] = HTMLUtil.getParameterValues(context.request, "OVER_FIVE_YEARS", "") ;
			String REMARK[] = HTMLUtil.getParameterValues(context.request, "REMARK", "") ;
			
			
    		DataAccessor.getSession().startTransaction();
    		DataAccessor.getSession().startBatch()  ;
    		
            		for(int i=0 ;i<ID.length;i++){
            		    data.put("ID", ID[i]);
            		    data.put("ADJUST_TIME", ADJUST_TIME[i]);
            		    data.put("SIX_MONTHS", SIX_MONTHS[i]);
            		    data.put("ONE_YEAR", ONE_YEAR[i]);
            		    data.put("ONE_THREE_YEARS", ONE_THREE_YEARS[i]);
            		    data.put("THREE_FIVE_YEARS", THREE_FIVE_YEARS[i]);
            		    data.put("OVER_FIVE_YEARS", OVER_FIVE_YEARS[i]);
            		    data.put("REMARK", REMARK[i]);
            		    String id=ID[i];
	           		     if(id.equals("0")){
	           		    	DataAccessor.getSession().update("creditReportDelete.saveProp",data); 
	        		     }  
	           		     else{
	           		    	DataAccessor.getSession().insert("creditReportDelete.updateProp",data);
	           		     }	    
            		}
            	DataAccessor.getSession().executeBatch();	
    		DataAccessor.getSession().commitTransaction();
    	   

		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Output.jspSendRedirect(context,"defaultDispatcher?__action=supportCreditReportDelete.propManage");
	}	
}
