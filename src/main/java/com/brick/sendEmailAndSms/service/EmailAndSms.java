package com.brick.sendEmailAndSms.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.DataWrap;
import com.brick.service.entity.Context;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.log.service.LogPrint;
import com.ibatis.sqlmap.client.SqlMapClient;


/**
 * 
 * @author �뽪��
 * @�������� 2011-4-2
 * @�汾 V 1.0
 */

public class EmailAndSms  extends BaseCommand
{
	Log logger = LogFactory.getLog(EmailAndSms.class);
	//查询所有的message和mail
	public void queryAllEmailAndSms(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;
		DataWrap dw = null;		
		if (errList.isEmpty()) {		
			try {			
				dw = (DataWrap) DataAccessor.query("lockManagement.queryAllmsgAndMail", context.contextMap,DataAccessor.RS_TYPE.PAGED);			
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("dw", dw);
			outputMap.put("content", context.contextMap.get("content"));
			Output.jspOutput(outputMap, context,"/sendEmailAndSmsManager/emailAndSmsManager.jsp"); 
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
	}
	
	//查询message和mail via页面条件
	public void queryEmailAndSmsByCond(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;
		PagingInfo<Object> dw = null;
		if (errList.isEmpty()) {		
			try {	
				
				dw = baseService.queryForListWithPaging("lockManagement.queryAllmsgAndMailByCondition", context.contextMap ,"SENDTIME",ORDER_TYPE.DESC);			
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("dw", dw);
			outputMap.put("cust_name", context.contextMap.get("cust_name"));
			outputMap.put("lease_code", context.contextMap.get("lease_code"));
			outputMap.put("CONTENT", context.contextMap.get("CONTENT"));
			outputMap.put("MOBILE", context.contextMap.get("MOBILE"));
			outputMap.put("status", context.contextMap.get("status"));
			outputMap.put("QSTART_DATE", context.contextMap.get("QSTART_DATE"));
			outputMap.put("CREATE_BY", context.contextMap.get("CREATE_BY"));
			
			Output.jspOutput(outputMap, context,"/sendEmailAndSmsManager/emailAndSmsManager.jsp"); 
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
	}
	
	
	//Add by Michael 查询间接锁码Email发送情况
	public void queryDailyLockSendInfo(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;
		PagingInfo<Object> dw = null;	
		if (errList.isEmpty()) {		
			try {	
				
				dw = baseService.queryForListWithPaging("financeDecomposeReport.queryDailyLockSendInfo",context.contextMap, "CREATE_TIME",ORDER_TYPE.DESC);			
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("dw", dw);
			outputMap.put("content", context.contextMap.get("content"));
			outputMap.put("LOCK_CODE", context.contextMap.get("LOCK_CODE"));
			
			Output.jspOutput(outputMap, context,"/sendEmailAndSmsManager/queryLockManageSendInfo.jsp"); 
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
	}
	
	
	public void updateDailyLockSendInfo(Context context)
	{
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		List errList = context.errList;
		Map outputMap = new HashMap();
		String[] ids=context.request.getParameterValues("ids");
		context.contextMap.put("ids", ids);
		if (errList.isEmpty()) {		
			try {	
				sqlMapper.startTransaction() ;
				sqlMapper.update("financeDecomposeReport.updateLockMsgSendTypeByID",context.contextMap);
				sqlMapper.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}finally{
				try {
					sqlMapper.endTransaction() ;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}		
		if (errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=emailAndSms.queryDailyLockSendInfo");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
}
