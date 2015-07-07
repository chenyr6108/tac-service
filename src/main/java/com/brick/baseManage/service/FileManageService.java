package com.brick.baseManage.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.crypto.Data;

import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.log.service.LogPrint;
import com.ibatis.sqlmap.client.SqlMapClient;
/**
 * 
 * @author 吴振东
 * @创建日期 2010-7-16
 * @版本 V 1.0
 */
public class FileManageService extends AService {	
	Log logger = LogFactory.getLog(FileManageService.class);
	@Override
	protected void afterExecute(String action, Context context) {
		super.afterExecute(action, context);
	}
	@Override
	protected boolean preExecute(String action, Context context) {
		return super.preExecute(action, context);
	}	
	/**
	 * 查询所有的资料
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryAllFile(Context context) {		
		Map outputMap = new HashMap();
		List errList = context.errList;
		DataWrap dw = null;		
		if (errList.isEmpty()) {		
			try {			
				dw = (DataWrap) DataAccessor.query("fileService.queryAllFile", context.contextMap,DataAccessor.RS_TYPE.PAGED);			
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--资料管理列表错误!请联系管理员");
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("dw", dw);
			outputMap.put("content", context.contextMap.get("content"));
			outputMap.put("file_type", context.contextMap.get("file_type"));
			outputMap.put("state", context.contextMap.get("state"));
			outputMap.put("file_contract_type", context.contextMap.get("file_contract_type"));
			Output.jspOutput(outputMap, context,"/baseManage/fileManage/fileManager.jsp"); 
		} else {
			// 跳转到错误页面
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	/**
	 * 进入新建资料页面 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getCreateFileJsp(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map dataDictionaryMap = new HashMap();
		List fileInfor = null;
		List InforFileType = null;
		if(errList.isEmpty()) {
			try {				
				fileInfor = (List<Map>)DictionaryUtil.getDictionary("资料类型");
				dataDictionaryMap.put("dataType", "合同资料类型");
				InforFileType = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("InforFileType", InforFileType); 
				outputMap.put("fileInfor", fileInfor); 
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--资料管理添加页初始化错误!请联系管理员");
				//errList.add("com.brick.bankinfo.service.BankInfoManager.getCreateBankJsp"+ e.getMessage());
			}
		}		
		if(errList.isEmpty()) {
			Output.jspOutput(outputMap, context,"/baseManage/fileManage/fileCreate.jsp");	
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}	

	/**
	 * 保存资料信息 后到资料信息管理页面
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void create(Context context) {		
		List errList = context.errList;
		Map outputMap = new HashMap() ;
		if(errList.isEmpty()) {
			try {
				DataAccessor.execute("fileService.create", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--资料管理添加错误!请联系管理员");
				//errList.add("com.brick.bankinfo.service.BankInfoManager.create"+ e.getMessage());
			}
		}		
		if(errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=fileService.queryAllFile");
		}else{
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}	
	/**
	 * 根据id来查找对应资料的信息，用于查看
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getFileByIdForShow(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map fileInfor = new HashMap();
		Map dataDictionaryMap = new HashMap();
		List Infor = null;
		if (errList.isEmpty()) {
			try {
				fileInfor = (Map) DataAccessor.query("fileService.getFileInfoById", context.contextMap,DataAccessor.RS_TYPE.MAP);	
				dataDictionaryMap.put("dataType", "资料类型");
				Infor = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("Infor", Infor); 				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--资料管理查看错误!请联系管理员");
				//errList.add("com.brick.bankinfo.service.BankInfoManager.getCompanyByIdForShow"+ e.getMessage());
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("fileInfor", fileInfor);
			Output.jspOutput(outputMap, context,"/baseManage/fileManage/fileShow.jsp");	
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	/**
	 * 根据id来查找对应的资料信息,用于更新
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getFileById(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map fileInfor = new HashMap();
		Map dataDictionaryMap = new HashMap();
		List Infor = null;
		List InforFileType = null;
		if (errList.isEmpty()) {
			try {
				fileInfor = (Map) DataAccessor.query("fileService.getFileInfoById", context.contextMap,DataAccessor.RS_TYPE.MAP);
				dataDictionaryMap.put("dataType", "资料类型");
				Infor = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				dataDictionaryMap.put("dataType", "合同资料类型");
				InforFileType = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("Infor", Infor); 
				outputMap.put("InforFileType", InforFileType); 
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--资料管理修改页初始化错误!请联系管理员");
				//errList.add("com.brick.bankinfo.service.BankInfoManager.getEmployeeById"+ e.getMessage());
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("fileInfor", fileInfor);
			Output.jspOutput(outputMap, context,"/baseManage/fileManage/fileUpdate.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	/**
	 * 更新资料信息
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void update(Context context) {
		List errList = context.errList;		
		Map outputMap = new HashMap() ;
		if(errList.isEmpty()) {
			try {			
				DataAccessor.execute("fileService.update", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--资料管理修改错误!请联系管理员");
				//errList.add("com.brick.bankinfo.service.BankInfoManager.update"+ e.getMessage());
			}
		}		
		if(errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=fileService.queryAllFile");
		}else{
			//跳转到错误页面
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}	
	/**
	 * 作废一条资料记录
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void invalid(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap() ;
		SqlMapClient sqlMapper=DataAccessor.getSession();
		
		if (errList.isEmpty()) {
			try {
				//是否要添加事务
				sqlMapper.startTransaction();
				sqlMapper.update("fileService.invalid", context.contextMap);
				sqlMapper.commitTransaction();
			} catch (Exception e) {
				
				try {
					sqlMapper.endTransaction();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--资料管理删除（作废）错误!请联系管理员");
				//errList.add("com.brick.bankinfo.service.BankInfoManager.invalid"+ e.getMessage());
			}
		}
		if (errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=fileService.queryAllFile");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	/**
	 * 查询提醒日志管理页
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryRentLog(Context context) {		
		Map outputMap = new HashMap();
		List errList = context.errList;
		DataWrap dw = null;	
		Map rsMap = null;
		Map paramMap = new HashMap();
		paramMap.put("id", context.contextMap.get("s_employeeId"));		
		if (errList.isEmpty()) {		
			try {	
				rsMap = (Map) DataAccessor.query("employee.getEmpInforById",paramMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("p_usernode", rsMap.get("node"));
				dw = (DataWrap) DataAccessor.query("fileService.queryRentLog", context.contextMap,DataAccessor.RS_TYPE.PAGED);			
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("项目管理--资料提醒列表错误!请联系管理员");
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("dw", dw);
			outputMap.put("content", context.contextMap.get("content"));
			outputMap.put("start_date", context.contextMap.get("start_date"));
			outputMap.put("end_date", context.contextMap.get("end_date"));
			outputMap.put("create_startdate", context.contextMap.get("create_startdate"));
			outputMap.put("create_enddate", context.contextMap.get("create_enddate"));
			Output.jspOutput(outputMap, context,"/baseManage/fileManage/rentLogManager.jsp"); 
		} else {
			// 跳转到错误页面
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	/**
	 * 作废一条提醒日志
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void invalidRentLog(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap() ;
		if (errList.isEmpty()) {
			try {
				DataAccessor.getSession().update("fileService.invalidRentLog", context.contextMap);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("项目管理--资料提醒删除（作废）错误!请联系管理员");
				//errList.add("com.brick.bankinfo.service.BankInfoManager.invalid"+ e.getMessage());
			}
		}
		if (errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=fileService.queryRentLog");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	/**
	 * 根据提醒日志ID查询所对对应的资料
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryLogMore(Context context) {		
		Map outputMap = new HashMap();
		List errList = context.errList;
		List logMoreList = null;		
	
		if (errList.isEmpty()) {		
			try {			
				logMoreList = (List) DataAccessor.query("fileService.queryLogMore", context.contextMap,DataAccessor.RS_TYPE.LIST);			
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("项目管理--资料提醒查看错误!请联系管理员");
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("logMoreList", logMoreList);
			Output.jspOutput(outputMap, context,"/baseManage/fileManage/logMore.jsp"); 
		} else {
			// 跳转到错误页面
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}	
	
	
	
	
	
	
	/**
	 * 查询所有提醒日志中对应材料
	 * @param context
	 */
	public void queryFile(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;
		DataWrap logMoreList = null;		
		
		if (errList.isEmpty()) {		
			try {			
				logMoreList = (DataWrap) DataAccessor.query("fileService.queryLogMoreFile", context.contextMap,DataAccessor.RS_TYPE.PAGED);			
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("项目管理--资料提醒查看错误!请联系管理员");
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("logMoreList", logMoreList);
			Output.jsonOutput(outputMap, context);
		} else {
			// 跳转到错误页面
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
}

