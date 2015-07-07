package com.brick.department.service;

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
 * @author 吴振东
 * @创建日期 2010-7-2
 * @版本 V 1.0
 */
public class CompanyManageService extends AService {	
	Log logger = LogFactory.getLog(CompanyManageService.class);
	@Override
	protected void afterExecute(String action, Context context) {
		super.afterExecute(action, context);
	}
	@Override
	protected boolean preExecute(String action, Context context) {
		return super.preExecute(action, context);
	}
	
	/**
	 * 查询所有的公司
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryAllCompany(Context context) {		
		Map outputMap = new HashMap();
		List errList = context.errList;
		DataWrap dw = null;		
		if (errList.isEmpty()) {		
			try {			
				dw = (DataWrap) DataAccessor.query("companyManage.queryAllCompany", context.contextMap,DataAccessor.RS_TYPE.PAGED);			
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("dw", dw);
			outputMap.put("content", context.contextMap.get("content"));
			Output.jspOutput(outputMap, context,"/department/companyManager.jsp"); 
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	
	/**
	 * 根据id来查找对应公司的信息，用于查看
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getCompanyByIdForShow(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map company = null;

		if (errList.isEmpty()) {
			try {
				company = (Map) DataAccessor.query("companyManage.getCompanyInfoById", context.contextMap,DataAccessor.RS_TYPE.MAP);
				
				Map dataDictionaryMap = new HashMap();
				dataDictionaryMap.put("dataType", "企业类型");
				List<Map> companyTypes = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("companyTypes", companyTypes);
				
			} catch (Exception e) {
				errList.add("com.brick.bankinfo.service.BankInfoManager.getCompanyByIdForShow"+ e.getMessage());
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("company", company);
			Output.jspOutput(outputMap, context,"/department/companyShow.jsp");	
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}	

	
	/**
	 * 作废一条公司记录
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void invalid(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		if (errList.isEmpty()) {
			try {
				DataAccessor.getSession().update("companyManage.invalid", context.contextMap);
			} catch (Exception e) {
				e.printStackTrace();
				errList.add("com.brick.bankinfo.service.BankInfoManager.invalid"+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		if (errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=companyManage.queryAllCompany");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
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
		if(errList.isEmpty()) {
			try {
				count = (Integer)DataAccessor.query("companyManage.getChildCountByParentId", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
			} catch (Exception e) {
				e.printStackTrace();
				errList.add("com.brick.bankinfo.service.BankInfoManager.getChildCountByParentId"+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if(errList.isEmpty()) {
			outputMap.put("count", count);
			Output.jsonOutput(outputMap, context);
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}	
	}
	
	/**
	 * 进入新建公司信息页面 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getCreateCompanyJsp(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List parentCompany = null;
		if(errList.isEmpty()) {
			try {
				parentCompany = (List)DataAccessor.query("companyManage.getParentCompany", context.contextMap, DataAccessor.RS_TYPE.LIST);
				
				Map dataDictionaryMap = new HashMap();
				dataDictionaryMap.put("dataType", "企业类型");
				List<Map> companyTypes = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("companyTypes", companyTypes);
				
			} catch (Exception e) {
				e.printStackTrace();
				errList.add("com.brick.bankinfo.service.BankInfoManager.getCreateBankJsp"+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if(errList.isEmpty()) {
			outputMap.put("parentCompany", parentCompany);
			Output.jspOutput(outputMap, context,"/department/companyCreate.jsp");	
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}	

	
	/**
	 * 保存公司信息 后到公司信息管理页面
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void create(Context context) {	
		Map outputMap = new HashMap();
		List errList = context.errList;
		if(errList.isEmpty()) {
			try {
				DataAccessor.execute("companyManage.create", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
			} catch (Exception e) {
				e.printStackTrace();
				//errList.add("com.brick.bankinfo.service.BankInfoManager.create"+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if(errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=companyManage.queryAllCompany");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}	

	
	/**
	 * 根据id来查找对应公司的信息,用于更新
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getCompanyById(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List parentCompany = null;
		Map company = new HashMap();
		Map parentCompanyMap = new HashMap();
		if (errList.isEmpty()) {
			try {
				company = (Map) DataAccessor.query("companyManage.getCompanyInfoById", context.contextMap,DataAccessor.RS_TYPE.MAP);
				parentCompany = (List)DataAccessor.query("companyManage.getParentCompanyForUpdate", context.contextMap, DataAccessor.RS_TYPE.LIST);
				parentCompanyMap = (Map) DataAccessor.query("companyManage.getParentCompanyInfoById", context.contextMap,DataAccessor.RS_TYPE.MAP);
				
				Map dataDictionaryMap = new HashMap();
				dataDictionaryMap.put("dataType", "企业类型");
				List<Map> companyTypes = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("companyTypes", companyTypes);
				
			} catch (Exception e) {
				e.printStackTrace();
				errList.add("com.brick.bankinfo.service.BankInfoManager.getEmployeeById"+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("company", company);
			outputMap.put("parentCompany", parentCompany);
			outputMap.put("parentCompanyMap", parentCompanyMap);
			Output.jspOutput(outputMap, context,"/department/companyUpdate.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
		
	
	/**
	 * 更新公司信息
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void update(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;		
		if(errList.isEmpty()) {
			try {			
				DataAccessor.execute("companyManage.update", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			} catch (Exception e) {
				e.printStackTrace();
				//errList.add("com.brick.bankinfo.service.BankInfoManager.update"+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if(errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=companyManage.queryAllCompany");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}	
}

