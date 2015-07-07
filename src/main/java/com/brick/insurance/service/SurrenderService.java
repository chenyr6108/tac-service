package com.brick.insurance.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.ibatis.sqlmap.client.SqlMapClient;
/*
 * 退保管理
 * 1退保金额
 * 2退保原因
 * 3退保以后设备不能重新投保
 */
public class SurrenderService extends AService {
	static Log logger = LogFactory.getLog(SurrenderService.class);
	/**
	 * 退保管理页，在该页显示所有的保单
	 */
	@SuppressWarnings("unchecked")
	public  void queryAll(Context context) {
		//System.out.println("------------------------->保单管理页");
		Map outputMap = new HashMap();
		List errList = context.errList;
		DataWrap dw = null;
		List company=new ArrayList();

		if (errList.isEmpty()) {
			try {
				 company = (List) DataAccessor.query("insurance.getCompany",
							context.contextMap, DataAccessor.RS_TYPE.LIST);
				dw = (DataWrap) DataAccessor.query("surrender.insuListManage",
						context.contextMap, DataAccessor.RS_TYPE.PAGED);
			} catch (Exception e) {
				errList.add("退保管理页 : " + e);
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);				
			}
		}
		
		if (errList.isEmpty()) {
			outputMap.put("dw", dw);
			outputMap.put("company", company);
			outputMap.put("INCP",context.request.getParameter("INCP"));
			outputMap.put("content", context.contextMap.get("content"));
			Output
					.jspOutput(outputMap, context,
							"/insurance/surrender/surrenderManage.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}
	/**
	 * 退保单前置页面
	 */
	@SuppressWarnings("unchecked")
	public  void surrenderInsuListPre(Context context) {
		//System.out.println("------------------------->退保单前置页面");
		Map outputMap = new HashMap();
		List errList = context.errList;
		DataWrap dw = null;
		//退保页面
		if (errList.isEmpty()) {

			outputMap.put("INCU_ID", context.request.getParameter("INCU_ID"));
			Output
					.jspOutput(outputMap, context,
							"/insurance/surrender/surrenderCreatePre.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}
	/*
	 * 创建退保单
	 */
	@SuppressWarnings("unchecked")
	public  void createSurrender(Context context) throws Exception {
		//System.out.println("------------------------->创建退保单");
		Map outputMap = new HashMap();
		List errList = context.errList;
		List eqmtList=new ArrayList();
		String incu_id=context.request.getParameter("INCU_ID");
		context.contextMap.put("INCU_ID", incu_id);
		//更具保单号获取保单下的设备
		eqmtList=(List) DataAccessor.query("surrender.getEqmtInsuList",
				context.contextMap, DataAccessor.RS_TYPE.LIST);
		
		
		
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		if (errList.isEmpty()) {
			try {
				sqlMapper.startTransaction() ;
				//退保保单的创建
			    sqlMapper.insert("surrender.createInsuList", context.contextMap);
			    //保单的删除
			    sqlMapper.update("insurance.delInsuList1", context.contextMap);
			    //保单下设备状态的更改
				if (eqmtList != null) {
					sqlMapper.startBatch();
					for (Object eqmt : eqmtList) {			    
						context.contextMap.put("RECD_ID",((Map) eqmt).get("RECD_ID"));
						//将设备的投保状态更新为以投保
						context.contextMap.put("insure_status", -1);
						sqlMapper.update("insurance.updateEqmt", context.contextMap);
					}
					sqlMapper.executeBatch();					
					sqlMapper.commitTransaction();
				}
			} catch (Exception e) {
        			    
        				e.printStackTrace();
        				LogPrint.getLogStackTrace(e, logger);
        				errList.add("系统设置--创建退保单!请联系管理员");
			} finally {
			    
			    
        				try {
        					sqlMapper.endTransaction();
        					
        					
        				} catch (Exception e) {
        				    
        					e.printStackTrace();
        					LogPrint.getLogStackTrace(e, logger);
        					errList.add(e);
        				}
			}
		}
		if (errList.isEmpty()) { 
			/*2011/12/12 Yang Yun Merger "退保". Start*/
		    //Output.jspSendRedirect(context,"defaultDispatcher?__action=surrender.queryAll");
			Output.jspSendRedirect(context,"defaultDispatcher?__action=insuranceCommand.getAllInsu");
			/*2011/12/12 Yang Yun Merger "退保". End*/
		
		}else{
		       //跳转错误页面
			outputMap.put("errList",errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}

	}	
	/*
	 * 查看退保单
	 */
	@SuppressWarnings("unchecked")
	public void surrenderShow(Context context) {
		//System.out.println("------------------------->查看退保单");
		List errList = context.errList;	
		Map outputMap = new HashMap();

		Map surrender=new HashMap();
		
		try {
			//查出推保单
			surrender=(Map)DataAccessor.query("surrender.getSurrender", context.contextMap, DataAccessor.RS_TYPE.MAP);
		} catch (Exception e) { 
			errList.add("读取退保信息错误!" + e.toString());
			e.printStackTrace();
		}
		
		if (errList.isEmpty()) {
			outputMap.put("surrender", surrender);
			Output.jspOutput(outputMap, context, "/insurance/surrender/surrenderShow.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}


	}
	/*
	 * 查看退保单
	 *///surrenderUpdatePre
	@SuppressWarnings("unchecked")
	public void surrenderUpdatePre(Context context) {
		//System.out.println("------------------------->修改退保单前置");
		List errList = context.errList;	
		Map outputMap = new HashMap();

		Map surrender=new HashMap();
		
		try {
			//查出推保单
			surrender=(Map)DataAccessor.query("surrender.getSurrender", context.contextMap, DataAccessor.RS_TYPE.MAP);
		} catch (Exception e) { 
			errList.add("读取退保信息错误!" + e.toString());
			e.printStackTrace();
		}
		
		if (errList.isEmpty()) {
			outputMap.put("surrender", surrender);
			Output.jspOutput(outputMap, context, "/insurance/surrender/surrenderUpdatePre.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}
	/*
	 * 修改退保单
	 *///surrenderUpdatePre
	@SuppressWarnings("unchecked")
	public void updateSurrender(Context context) {
		//System.out.println("------------------------->修改退保单");
		List errList = context.errList;	
		Map outputMap = new HashMap();
		
		try {
	
			DataAccessor.execute("surrender.updateSurrender", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);	
		} catch (Exception e) { 
			errList.add("修改退保单错误!" + e.toString());
			e.printStackTrace();
		}
		
		if (errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=surrender.queryAll");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}
	/*
	 * 删除退保单
	 *///surrenderUpdatePre
	@SuppressWarnings("unchecked")
	public void surrenderDel(Context context) {
		//System.out.println("-------------------------> 删除退保单");
		List errList = context.errList;	
		Map outputMap = new HashMap();
		String id=context.request.getParameter("ID");
		//首先删除推保单 
		//恢复保单的状态
		try {
			DataAccessor.execute("surrender.delSurrender", context.contextMap,DataAccessor.OPERATION_TYPE.DELETE);
			DataAccessor.execute("surrender.recoverInsuList", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
		} catch (Exception e) { 
			errList.add("删除退保单错误!" + e.toString());
			e.printStackTrace();
		}
		
		if (errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=surrender.queryAll");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}
}
