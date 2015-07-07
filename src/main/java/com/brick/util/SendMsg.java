package com.brick.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.log.service.LogPrint;
import com.brick.payMoney.service.payMoneyMessageService;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.ibatis.sqlmap.client.SqlMapClient;

public class SendMsg extends AService {
	Log logger = LogFactory.getLog(payMoneyMessageService.class);

	public String SendSMSMsg(Context context,List msgList) {

		String end = "短信发送失败";
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		if (errList.isEmpty()) {
			try {
				if(msgList.size()>0)
				{
				
					for(int i=0;i<msgList.size();i++)
					{
						Map entityMap=(Map)msgList.get(i);
						
						Integer id= (Integer)DataAccessor.execute("lockManagement.createSendMsg", entityMap,DataAccessor.OPERATION_TYPE.INSERT);
						
						entityMap.put("SENDSMS", id);
						DataAccessor.execute("lockManagement.createSendMsgDetil", entityMap,DataAccessor.OPERATION_TYPE.INSERT);
						//DataAccessor.execute("lockManagement.updatLockManageRealeaseLock", entityMap,DataAccessor.OPERATION_TYPE.UPDATE);
					}
					
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		if (errList.isEmpty()) {
			end = "短信发送成功";
		} else {
			end = "短信发送失败";
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
		return end;
	}
	public static String SendSMSMsg(List msgList) {
		
		String end = "短信发送失败";
		Map outputMap = new HashMap();
		
			try {
				if(msgList.size()>0)
				{
					
					for(int i=0;i<msgList.size();i++)
					{
						Map entityMap=(Map)msgList.get(i);
						
						Integer id= (Integer)DataAccessor.execute("lockManagement.createSendMsg", entityMap,DataAccessor.OPERATION_TYPE.INSERT);
						
						entityMap.put("SENDSMS", id);
						DataAccessor.execute("lockManagement.createSendMsgDetil", entityMap,DataAccessor.OPERATION_TYPE.INSERT);
						end="短信发送成功";
						//DataAccessor.execute("lockManagement.updatLockManageRealeaseLock", entityMap,DataAccessor.OPERATION_TYPE.UPDATE);
					}
					
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				//LogPrint.getLogStackTrace(e, logger);
			}
		
		return end;
	}
	public String SendSMSMsg(Context context,List msgList,SqlMapClient sqlMapper) {

		String end = "短信发送失败";
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		if (errList.isEmpty()) {
			try {
				if(msgList.size()>0)
				{
				
					for(int i=0;i<msgList.size();i++)
					{
						Map entityMap=(Map)msgList.get(i);
						Integer id= (Integer)sqlMapper.insert("lockManagement.createSendMsg", entityMap);
						//System.out.println(id+"===============");
//						Integer id= (Integer)DataAccessor.execute("lockManagement.createSendMsg", entityMap,DataAccessor.OPERATION_TYPE.INSERT);
						
						entityMap.put("SENDSMS", id);
						sqlMapper.insert("lockManagement.createSendMsgDetil", entityMap);
//						DataAccessor.execute("lockManagement.createSendMsgDetil", entityMap,DataAccessor.OPERATION_TYPE.INSERT);
					}
					
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		if (errList.isEmpty()) {
			end = "短信发送成功";
		} else {
			end = "短信发送失败";
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
		return end;
	}
}
