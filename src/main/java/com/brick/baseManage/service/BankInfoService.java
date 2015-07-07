package com.brick.baseManage.service;

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
 * 
 * @author 康侃
 * @创建日期 2010-6-28
 * @版本 V 1.0
 */
public class BankInfoService extends AService {	
	Log logger = LogFactory.getLog(BankInfoService.class);
	@Override
	protected void afterExecute(String action, Context context) {
		super.afterExecute(action, context);
	}
	@Override
	protected boolean preExecute(String action, Context context) {
		return super.preExecute(action, context);
	}
	
	/**
	 * 查询所有的银行信息 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryBankAllInfo(Context context) {		
		Map outputMap = new HashMap();
		List errList = context.errList;
		DataWrap dw = null;		
		if (errList.isEmpty()) {		
			try {			
				dw = (DataWrap) DataAccessor.query("bankInfo.queryBankAllInfo", context.contextMap,DataAccessor.RS_TYPE.PAGED);			
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--银行信息列表错误!请联系管理员");
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("dw", dw);
			outputMap.put("content", context.contextMap.get("content"));
			Output.jspOutput(outputMap, context,"/baseManage/bankInfoManage/bankInfoManager.jsp");
		} else {
			// 跳转到错误页面
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	/**
	 * 进入新建银行信息页面 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getCreateBankJsp(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List parentBank = null;
		if(errList.isEmpty()) {
			try {
				parentBank = (List)DataAccessor.query("bankInfo.getParentBank", context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--银行信息添加页初始化错误!请联系管理员");
				//errList.add("com.brick.bankinfo.service.BankInfoManager.getCreateBankJsp"+ e.getMessage());
			}
		}		
		if(errList.isEmpty()) {
			outputMap.put("parentBank", parentBank);
			Output.jspOutput(outputMap, context,"/baseManage/bankInfoManage/bankInfoCreate.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
		
	/**
	 * 保存银行信息 后到银行信息查看页面
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void create(Context context) {		
		List errList = context.errList;
		Map outputMap = new HashMap() ;
		if(errList.isEmpty()) {
			try {
				DataAccessor.execute("bankInfo.create", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--银行信息添加错误!请联系管理员");
				//errList.add("com.brick.bankinfo.service.BankInfoManager.create"+ e.getMessage());
			}
		}		
		if(errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=bankInfo.queryBankAllInfo");
		}else{
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}	
	
	/**
	 * 根据id来查找对应银行的信息，用于查看
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getBankByIdForShow(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map bank = null;
		if (errList.isEmpty()) {
			try {
				bank = (Map) DataAccessor.query("bankInfo.getBankInfoById", context.contextMap,DataAccessor.RS_TYPE.MAP);			
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--银行信息查看 错误!请联系管理员");
				//errList.add("com.brick.bankinfo.service.BankInfoManager.getCompanyByIdForShow"+ e.getMessage());
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("bank", bank);
			Output.jspOutput(outputMap, context,"/baseManage/bankInfoManage/bankInfoShow.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	/**
	 * 根据id来查找对应银行的信息,用于更新
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getBankById(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List parentBank = null;
		Map bank = null;
		if (errList.isEmpty()) {
			try {
				bank = (Map) DataAccessor.query("bankInfo.getBankInfoById", context.contextMap,DataAccessor.RS_TYPE.MAP);
				parentBank = (List)DataAccessor.query("bankInfo.getParentBankForUpdate", context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--银行信息修改页初始化错误!请联系管理员");
				//errList.add("com.brick.bankinfo.service.BankInfoManager.getEmployeeById"+ e.getMessage());
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("bank", bank);
			outputMap.put("parentBank", parentBank);
			Output.jspOutput(outputMap, context,"/baseManage/bankInfoManage/bankInfoUpdate.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}	
	/**
	 * 更新银行信息
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void update(Context context) {
		List errList = context.errList;		
		Map outputMap = new HashMap() ;
		if(errList.isEmpty()) {
			try {
				DataAccessor.execute("bankInfo.update", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--银行信息修改错误!请联系管理员");
				//errList.add("com.brick.bankinfo.service.BankInfoManager.update"+ e.getMessage());
			}
		}		
		if(errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=bankInfo.queryBankAllInfo");
		}else{
			//跳转到错误页面
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	/**
	 * 作废一条银行记录
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void invalid(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap() ;
		if (errList.isEmpty()) {
			try {
				DataAccessor.getSession().update("bankInfo.invalid", context.contextMap);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--银行信息删除（作废）错误!请联系管理员");
				//errList.add("com.brick.bankinfo.service.BankInfoManager.invalid"+ e.getMessage());
			}
		}
		if (errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=bankInfo.queryBankAllInfo");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	/**
	 * 根据父接点id来查找     子结点的个数
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getChildCountByParentId(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList;
		Integer count = null;
		Integer account = null;		
		if(errList.isEmpty()) {
			try {
				count = (Integer)DataAccessor.query("bankInfo.getChildCountByParentId", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
				account = (Integer)DataAccessor.query("bankInfo.getChildAccountByBabiId", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--银行信息查找子节点错误!请联系管理员");
				//errList.add("com.brick.bankinfo.service.BankInfoManager.getChildCountByParentId"+ e.getMessage());
			}
		}		
		if(errList.isEmpty()) {
			outputMap.put("count", count+account);
			Output.jsonOutput(outputMap, context);
		}
	}
}

