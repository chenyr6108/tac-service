package com.brick.baseManage.service;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.log.service.LogPrint;
/**
 * 
 * @author 康侃
 * @创建日期 2010-7-1
 * @版本 V 1.0
 */
public class BankAccountService extends BaseCommand {
	Log logger = LogFactory.getLog(BankAccountService.class);
	/**
	 * 查询所有的银行账号信息        分页显示所有银行账号
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryBankAccountAllInfo(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		DataWrap dw = null;
		if (errList.isEmpty()) {
			try {
				dw = (DataWrap) DataAccessor.query("bankAccount.queryBankAccountAllInfo", context.contextMap,DataAccessor.RS_TYPE.PAGED);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--银行账号列表错误!请联系管理员");
				//errList.add("com.brick.bankinfo.service.BankAccountManager.queryBankAccountAllInfo"+ e.getMessage());
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("dw", dw);
			outputMap.put("content", context.contextMap.get("content"));
			Output.jspOutput(outputMap, context,"/baseManage/bankAccountManage/bankAccountManager.jsp");
		} else {
			// 跳转到错误页面
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	/**
	 * 进入添加账户页面
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getCreateAccountJsp(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List bankList = null;
		List accountPropertyList = null;
		List parentAccount = null;
		Map dataDictionaryMap = new HashMap();
		List<Map> accountType = null;
		if(errList.isEmpty()) {
			try {
				//add by xuyuefei  2014/8/21
				accountPropertyList=(List)DataAccessor.query("bankAccount.getAllAccountProperty", context.contextMap, DataAccessor.RS_TYPE.LIST);
				// 银行列表
				bankList = (List)DataAccessor.query("bankInfo.queryBankAllInfo", context.contextMap, DataAccessor.RS_TYPE.LIST);
				// 主账号
				parentAccount = (List)DataAccessor.query("bankAccount.getParentAccount", context.contextMap, DataAccessor.RS_TYPE.LIST);
				
				//
				dataDictionaryMap.put("dataType", "银行账号类型");
				accountType = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--银行账号添加页初始化错误!请联系管理员");
				//errList.add("com.brick.bankinfo.service.BankAccountManager.getCreateAccountJsp"+ e.getMessage());
			}
		}		
		if(errList.isEmpty()) {
			outputMap.put("accountPropertyList", accountPropertyList);
			outputMap.put("bankList", bankList);
			outputMap.put("parentAccount", parentAccount);
			outputMap.put("parentAccountJson", Output.serializer.serialize(parentAccount));
			outputMap.put("accountType", accountType);
			Output.jspOutput(outputMap, context,"/baseManage/bankAccountManage/bankAccountCreate.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
	 	}
	}
	/**
	 * 创建一个新银行账户
	 * @param contex
	 */
	@SuppressWarnings("unchecked")
	public void create(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap() ;
		if(errList.isEmpty()) {
			try {
				DataAccessor.execute("bankAccount.create", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--银行账号添加错误!请联系管理员");
				//errList.add("com.brick.bankinfo.service.BankInfoManager.create"+ e.getMessage());
			}
		}	
		if(errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=bankAccount.queryBankAccountAllInfo");
		}else{
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	/*
	 * 根据id来查找某个银行账号的全部信息,查看使用
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getBankAccountByIdForShow(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map account = null;
		if (errList.isEmpty()) {
			try {
				account = (Map) DataAccessor.query("bankAccount.getBankAccountInfoById", context.contextMap,DataAccessor.RS_TYPE.MAP);				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--查看银行账号错误!请联系管理员");
			//	errList.add("com.brick.bankinfo.service.BankAccountManager.getBankAccountByIdForShow"+ e.getMessage());
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("account", account);
			Output.jspOutput(outputMap, context,"/baseManage/bankAccountManage/bankAccountShow.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	/**
	 * 删除一条记录 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void invalid(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap() ;
		if(errList.isEmpty()) {
			try {
				DataAccessor.execute("bankAccount.invalid", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--删除银行账号错误!请联系管理员");
			//	errList.add("com.brick.bankinfo.service.BankAccountManager.invalid" + e.getMessage());
			}
		}		
		if(errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=bankAccount.queryBankAccountAllInfo");
		}else{
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}

	/*
	 * 根据id来查找某个银行账号的全部信息,用于修改
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getBankAccountById(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map account = null;
		List bank = null;
		List parentAccount = null;
		List parentAccountList = null;
		Map dataDictionaryMap = new HashMap();
		List<Map> accountType = null;
		List<HashMap> accountProperty=null;
		if (errList.isEmpty()) {
			try {
				//新增抓取账号性质List
				accountProperty=(List<HashMap>)DataAccessor.query("bankAccount.getAllAccountProperty", context.contextMap,DataAccessor.RS_TYPE.LIST);
				account = (Map) DataAccessor.query("bankAccount.getBankAccountInfoById", context.contextMap,DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("babi_id", account.get("BABI_ID"));
				bank = (List)DataAccessor.query("bankInfo.queryBankAllInfo", context.contextMap,DataAccessor.RS_TYPE.LIST);	
				dataDictionaryMap.put("dataType", "银行账号类型");
				accountType = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);	
				parentAccount = (List)DataAccessor.query("bankAccount.getParentAccountForUpdate", context.contextMap, DataAccessor.RS_TYPE.LIST);
				parentAccountList = (List)DataAccessor.query("bankAccount.getParentAccount", context.contextMap, DataAccessor.RS_TYPE.LIST);

			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--银行账号修改页初始化错误!请联系管理员");
				//errList.add("com.brick.bankinfo.service.BankAccountManager.getBankAccountById"+ e.getMessage());
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("accountProperty", accountProperty);
			outputMap.put("account", account);
			outputMap.put("bank", bank);
			outputMap.put("accountType", accountType);
			outputMap.put("parentAccount", parentAccount);
			outputMap.put("parentAccountList", parentAccountList);
			outputMap.put("parentAccountJson", Output.serializer.serialize(parentAccountList));
			Output.jspOutput(outputMap, context,"/baseManage/bankAccountManage/bankAccountUpdate.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}	
	/**
	 * 更新数据库中的信息         
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void update(Context context) {
		List errList = context.errList;		
		Map outputMap = new HashMap() ;
		if(errList.isEmpty()) {
			try {
				DataAccessor.execute("bankAccount.update", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--修改银行账号错误!请联系管理员");
				//errList.add("com.brick.bankinfo.service.BankInfoManager.update"+ e.getMessage());
			}
		}		
		if(errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=bankAccount.queryBankAccountAllInfo");
		}else{
			//跳转到错误页面
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	
		
	/**
	 * 根据父账户id来查找     子结点的个数
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getAccountCountByParentId(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList;
		Integer count = null;
		
		if(errList.isEmpty()) {
			try {
				count = (Integer)DataAccessor.query("bankAccount.getAccountCountByParentId", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--银行账号子节点查询错误!请联系管理员");
			//	errList.add("com.brick.bankinfo.service.BankAccountManager.getAccountCountByParentId"
				//		+ e.getMessage());
			}
		}
		
		if(errList.isEmpty()) {
			outputMap.put("count", count);
			Output.jsonOutput(outputMap, context);
		}
	}
	/**
	 * 根据account_no来查找此账户身份存在
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getCountByAccountNo(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		Integer count = null;
		
		if(errList.isEmpty()) {
			try {
				count = (Integer)DataAccessor.query("bankAccount.getCountByAccountNo", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--查询银行账号重复 错误!请联系管理员");
			//	errList.add("com.brick.bankinfo.service.BankAccountManager.getCountByAccountNo" + e.getMessage());
			}
		}
		
		if(errList.isEmpty()) {
			outputMap.put("count", count);
			Output.jsonOutput(outputMap, context);
		}
	}
	
	/**
	 * Add by Michael 2012 12-25 增加银行黑名单
	 * @author michael
	 * @param context
	 */
	public void addBlackBank(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList;
		Integer count = null;
		
		if(errList.isEmpty()) {
			try {
				count = (Integer)DataAccessor.query("bankAccount.getBlackBankByBankName", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
				if (count==0){
					DataAccessor.execute("bankAccount.addBlackBank", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
				}
				outputMap.put("count", count);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("添加银行黑名单错误");
			}
		}
		
		if(errList.isEmpty()) {
			
			Output.jsonOutput(outputMap, context);
		}
	}
	
	/**
	 * Add by Michael 2012 12-25 删除银行黑名单
	 * @author michael
	 * @param context
	 */
	public void deleteBlackBank(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList;
		Integer count = null;
		
		if(errList.isEmpty()) {
			try {
				baseService.update("bankAccount.deleteBlackBankByID", context.contextMap);
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("删除银行黑名单错误");
			}
		}
		
		if(errList.isEmpty()) {
			
			Output.jsonOutput(outputMap, context);
		}
	}
	
	public void queryAllBlackBankInfo(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		PagingInfo<Object> dw = null;
		if (errList.isEmpty()) {
			try {
				dw = baseService.queryForListWithPaging("bankAccount.queryAllBlackBankInfo", context.contextMap, "CREATE_TIME", ORDER_TYPE.DESC);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("查询银行黑名单错误!请联系管理员");
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("dw", dw);
			outputMap.put("content", context.contextMap.get("content"));
			Output.jspOutput(outputMap, context,"/baseManage/blackBankManage/blackBankManager.jsp");
		} else {
			// 跳转到错误页面
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	//验证帐号
	public void checkNo(Context context) throws Exception{
		int result=Integer.parseInt(this.baseService.queryForObj("bankAccount.checkNo",context.contextMap).toString());
		PrintWriter out=context.getResponse().getWriter();
		Map map=new HashMap();
		if(result>0){
			map.put("suc", "fail");
			Output.jsonOutput(map, context);
		}else{
			map.put("suc", "ok");
			Output.jsonOutput(map, context);
		}
	}
	
	//通过银行账号得到编码
	public void getBankCode(Context context) throws Exception{
		HashMap map=(HashMap)this.baseService.queryForObj("bankAccount.getCode",context.contextMap);
		Output.jsonOutput(map, context);
	}
}	


