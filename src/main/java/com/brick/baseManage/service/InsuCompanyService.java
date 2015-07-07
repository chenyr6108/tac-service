package com.brick.baseManage.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.web.HTMLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.to.DataDictionaryTo;
import com.brick.base.to.PagingInfo;
import com.brick.log.service.LogPrint;
import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 保险公司
 * @author cheng
 * @date 2010 6, 29
 * @version  1.0
 */
public class InsuCompanyService extends BaseCommand {
	Log logger = LogFactory.getLog(InsuCompanyService.class);

	public static final Logger log = Logger.getLogger(InsuCompanyService.class);

	@Override
	protected void afterExecute(String action, Context context) {
		super.afterExecute(action, context);
	}

	@Override
	protected boolean preExecute(String action, Context context) {
		return super.preExecute(action, context);
	}

	/**
	 * 插入一条保险公司
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void create(Context context) {

		List errList = context.errList;	
		String[] intpIds = HTMLUtil.getParameterValues(context.request,"intp_id", "");
		
		Map insureName = null;
		Long incp_id = null;
		Map outputMap = new HashMap() ;
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		if (errList.isEmpty()) {
			try {
				 sqlMapper.startTransaction() ;
				
				incp_id = (Long) sqlMapper.insert("insuCompany.create", context.contextMap);
				context.contextMap.put("incp_id", incp_id);
				//添加银行账户
				this.insertCompanyBankAccount(sqlMapper, context) ;
				
//				if (intpIds != null) {
//					DataAccessor.getSession().startBatch();
//					for (String intpId : intpIds) {
//					    
//						context.contextMap.put("intp_id", intpId);
//						insureName = (Map) DataAccessor.query("insureType.getInsureTypeById",context.contextMap, DataAccessor.RS_TYPE.MAP);
//						
//						System.out.println(insureName.get("INTP_NAME"));
//						context.contextMap.put("intp_name", insureName.get("INTP_NAME"));
//
//						DataAccessor.getSession().insert("insuCompany.createInsutype2Company",context.contextMap);
//					}
//					DataAccessor.getSession().executeBatch();
//					
//					DataAccessor.getSession().commitTransaction();
//				}
				sqlMapper.commitTransaction() ;
			} catch (Exception e) {
        			    
        				log.error("com.brick.baseManage.service.InsuCompanyService.create"+ e.getMessage());
        				
        				errList.add("com.brick.baseManage.service.InsuCompanyService.create"+ e.getMessage());
        				e.printStackTrace();
        				LogPrint.getLogStackTrace(e, logger);
        				errList.add("系统设置--保险公司添加错误!请联系管理员");
			} finally {
			    
			    
        				try {
        			//		DataAccessor.getSession().endTransaction();
        					sqlMapper.endTransaction() ;
        					
        				} catch (Exception e) {
        				    
        					log.error("com.brick.baseManage.service.InsuCompanyService.create"+ e.getMessage());
        					errList.add("com.brick.baseManage.service.InsuCompanyService.create"+ e.getMessage());
        					e.printStackTrace();
        					LogPrint.getLogStackTrace(e, logger);
        					errList.add(e);
        				}
			}
		}
		if (errList.isEmpty()) { 
		    
			Output.jspSendRedirect(context,"defaultDispatcher?__action=insuCompany.queryCompanyAllInfo");
		
		}else{
		       //跳转错误页面
			outputMap.put("errList",errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}

	}

	

	/**
	 * 查找所有保险公司的信息
	 * 
	 * @param context
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public void queryCompanyAllInfo(Context context) throws Exception {
		Map outputMap = new HashMap();
		PagingInfo<Object> dw = null;
		try {
			dw = baseService.queryForListWithPaging("insuCompany.queryCompanyAllInfo", context.contextMap,"INCP_ID");
		} catch (Exception e) { 
			log.error("com.brick.baseManage.service.InsuCompanyService.queryCompanyAllInfo"+ e.getMessage());
			throw e;
		}
		outputMap.put("dw", dw);
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("insuCompany_edit", baseService.checkAccessForResource("insuCompany_edit", String.valueOf(context.contextMap.get("s_employeeId"))));
		Output.jspOutput(outputMap, context,"/baseManage/insureCompanyManage/insuCompanyManager.jsp");
	}
	/**
	 * 根据id来查找某个保险公司的全部信息,查看使用
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getCompanyByIdForShow(Context context) {
	    
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		Map company = null;
		List companyBankAccount = null ;
//		List insureTypeList1 = null;
//		List insureTypeList2 = null;
//		List insureTypeList3 = null;
//		List insureTypeList4 = null;
		List<DataDictionaryTo> insuWayList = null;
		
		if (errList.isEmpty()) {
		    
			try {
				company = (Map) DataAccessor.query("insuCompany.getCompanyById", context.contextMap,DataAccessor.RS_TYPE.MAP);
				//查询保险公司银行账户
				companyBankAccount = (List) DataAccessor.query("companyBankAccount.getCompanyBankAccountByCreditId", company,DataAccessor.RS_TYPE.LIST);
				outputMap.put("companyBankAccount", companyBankAccount);
//    				   insureTypeList1 = (List) DataAccessor.query("insureType.getInsureTypeByCompanyId1",context.contextMap, DataAccessor.RS_TYPE.LIST);
//   				
//   				   insureTypeList2 = (List) DataAccessor.query("insureType.getInsureTypeByCompanyId2",context.contextMap, DataAccessor.RS_TYPE.LIST);
//   				
//   				   insureTypeList3 = (List) DataAccessor.query("insureType.getInsureTypeByCompanyId3",context.contextMap, DataAccessor.RS_TYPE.LIST);
//   				
//   				   insureTypeList4 = (List) DataAccessor.query("insureType.getInsureTypeByCompanyId4",context.contextMap, DataAccessor.RS_TYPE.LIST);
				insuWayList = baseService.getDataDictionaryByType("保单类型");   
				
			    } catch (Exception e) {
				
        			        log.error("com.brick.baseManage.service.InsuCompanyService.getCompanyByIdForShow"+ e.getMessage());
        				
        				errList.add("com.brick.baseManage.service.InsuCompanyService.getCompanyByIdForShow"+ e.getMessage());
        				e.printStackTrace();
        				LogPrint.getLogStackTrace(e, logger);
        				errList.add("系统设置--保险公司查看错误!请联系管理员");
			    }
		}

		if (errList.isEmpty()) {
		    
			outputMap.put("company", company);
			outputMap.put("insuWayList", insuWayList);
//			outputMap.put("insureTypeList1", insureTypeList1);
//			outputMap.put("insureTypeList2", insureTypeList2);
//			outputMap.put("insureTypeList3", insureTypeList3);
//			outputMap.put("insureTypeList4", insureTypeList4);

			
			Output.jspOutput(outputMap, context,"/baseManage/insureCompanyManage/insuCompanyShow.jsp");
		
		} else{
		    //跳转错误页面
			outputMap.put("errList",errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}

	}

	/*
	 * 根据id来查找某个保险公司的全部信息,更新使用
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getCompanyById(Context context) {
	    
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		
		Map company = null;
		List insureTypeList1 = null;
		List insureTypeList2 = null;
		List insureTypeList3 = null;
		List insureTypeList4 = null;
		List companyBankAccount = null ;
		List<DataDictionaryTo> insuWayList = null;
		if (errList.isEmpty()) {
			try {
				company = (Map) DataAccessor.query("insuCompany.getCompanyById", context.contextMap,DataAccessor.RS_TYPE.MAP);
				//查询保险公司银行账户
				companyBankAccount = (List) DataAccessor.query("companyBankAccount.getCompanyBankAccountByCreditId", company,DataAccessor.RS_TYPE.LIST);
				outputMap.put("companyBankAccount", companyBankAccount);
				//insureTypeList1 = (List) DataAccessor.query("insureType.getInsureTypeByCompanyId1",context.contextMap, DataAccessor.RS_TYPE.LIST);
   				
				//insureTypeList2 = (List) DataAccessor.query("insureType.getInsureTypeByCompanyId2",context.contextMap, DataAccessor.RS_TYPE.LIST);
				
				//insureTypeList3 = (List) DataAccessor.query("insureType.getInsureTypeByCompanyId3",context.contextMap, DataAccessor.RS_TYPE.LIST);
				
				//insureTypeList4 = (List) DataAccessor.query("insureType.getInsureTypeByCompanyId4",context.contextMap, DataAccessor.RS_TYPE.LIST);
				
				insuWayList = baseService.getDataDictionaryByType("保单类型");
			} catch (Exception e) {
				
			    	log.error("com.brick.baseManage.service.InsuCompanyService.getEmployeeById"+ e.getMessage());	
				errList.add("com.brick.baseManage.service.InsuCompanyService.getEmployeeById"+ e.getMessage());
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--保险公司修改页初始化错误!请联系管理员");
			}
		}

		if (errList.isEmpty()) {
		    
			outputMap.put("company", company);
			outputMap.put("insuWayList", insuWayList);
			//outputMap.put("insureTypeList1", insureTypeList1);
			//outputMap.put("insureTypeList2", insureTypeList2);
			//outputMap.put("insureTypeList3", insureTypeList3);
			//outputMap.put("insureTypeList4", insureTypeList4);

			Output.jspOutput(outputMap, context,"/baseManage/insureCompanyManage/insuCompanyUpdate.jsp");
		}else {
		    //跳转错误页面
			outputMap.put("errList",errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}

	/**
	 * 更新保险公司信息 
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void update(Context context) {
	    
		List errList = context.errList;
		String[] intpIds = HTMLUtil.getParameterValues(context.request,"intp_id", "");
		
		Long count = null;
		Map insureName = null;
		Map outputMap = new HashMap() ;
		SqlMapClient sqlMapper =  DataAccessor.getSession() ;
		if (errList.isEmpty()) {
		    
			try {
			    
				//DataAccessor.getSession().startTransaction();
				sqlMapper.startTransaction();
				count = (Long)DataAccessor.query("insuCompany.getInsutype2Company_count", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
				
				    // 删除保险公司表中的记录
				//DataAccessor.getSession().update("insuCompany.update", context.contextMap);
				sqlMapper.update("insuCompany.update", context.contextMap);
				if(count > 0) {
					    // 删除中间表中的记录
					//DataAccessor.getSession().update("insuCompany.deleteInsutype2Company",context.contextMap);
					sqlMapper.update("insuCompany.deleteInsutype2Company", context.contextMap);
				}
								
				if (intpIds != null) {
				    
						//DataAccessor.getSession().startBatch();
							sqlMapper.startBatch();
						for (String intpId : intpIds) {
						    System.out.println(intpId);
				
						    context.contextMap.put("intp_id", intpId);
						    insureName = (Map) DataAccessor.query("insureType.getInsureTypeById",context.contextMap, DataAccessor.RS_TYPE.MAP);
						    context.contextMap.put("intp_name", insureName.get("INTP_NAME"));
						    // 在中间表中重新插入记录
						   // DataAccessor.getSession().insert("insuCompany.createInsutype2Company",context.contextMap);
						    sqlMapper.insert("insuCompany.createInsutype2Company", context.contextMap);
						}
						//DataAccessor.getSession().executeBatch();
						sqlMapper.executeBatch();
				}
				//添加银行账户
				//this.insertCompanyBankAccount(DataAccessor.getSession(), context) ;
				this.insertCompanyBankAccount(sqlMapper, context) ;
				//DataAccessor.getSession().commitTransaction();
				sqlMapper.commitTransaction();
				
			} 
			
			catch (Exception e) {
				log.error("com.brick.baseManage.service.InsuCompanyService.update"+ e.getMessage());
				
				errList.add("com.brick.baseManage.service.InsuCompanyService.update"+ e.getMessage());
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--保险公司修改错误!请联系管理员");
			}finally{
				try {
					sqlMapper.endTransaction();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					logger.debug(e);
				}
			}
		}
		
		if (errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=insuCompany.queryCompanyAllInfo");
		} else {
			outputMap.put("errList",errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}

	/**
	 * 删除一条保险公司记录
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void delete(Context context) {
		List errList = context.errList;
		Long count = null;
		Map outputMap = new HashMap() ;
		
		SqlMapClient sqlMapper=DataAccessor.getSession();
		
		if (errList.isEmpty()) {
			try {
				//DataAccessor.getSession().startTransaction();
				
				count = (Long)DataAccessor.query("insuCompany.getInsutype2Company_count", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
				
				sqlMapper.startTransaction();
				if(count > 0) {
					                 //删除中间关联表中的数据
					//DataAccessor.getSession().update("insuCompany.deleteInsutype2Company", context.contextMap);
					sqlMapper.update("insuCompany.deleteInsutype2Company", context.contextMap);
				}
				                 //删除保险公司表中的记录
				//DataAccessor.getSession().update("insuCompany.deleteCompany", context.contextMap);
				sqlMapper.update("insuCompany.deleteCompany", context.contextMap);
				//DataAccessor.getSession().commitTransaction();
				sqlMapper.commitTransaction();
			
			
			} catch (Exception e) {
				log.debug("com.brick.baseManage.service.InsuCompanyService.delete"+ e.getMessage());
				
				errList.add("com.brick.baseManage.service.InsuCompanyService.delete"+ e.getMessage());
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--保险公司删除错误!请联系管理员");
			}
			finally{
				try {
					sqlMapper.endTransaction();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					logger.debug(e);
				}
			}
		}

		if (errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=insuCompany.queryCompanyAllInfo");
		} else {
			outputMap.put("errList",errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public void getMotorFlagInfo(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		                             //保险险种分类别查询，不在页面做判断
		List insureTypeList1 = null;
		List insureTypeList2 = null;
		List insureTypeList3 = null;
		List insureTypeList4 = null;

		if (errList.isEmpty()) {
			try {
			    
				//insureTypeList1 = (List) DataAccessor.query("insureType.queryInsureTypeInfo1",context.contextMap, DataAccessor.RS_TYPE.LIST);
				
				//insureTypeList2 = (List) DataAccessor.query("insureType.queryInsureTypeInfo2",context.contextMap, DataAccessor.RS_TYPE.LIST);
				
				//insureTypeList3 = (List) DataAccessor.query("insureType.queryInsureTypeInfo3",context.contextMap, DataAccessor.RS_TYPE.LIST);
				
				//insureTypeList4 = (List) DataAccessor.query("insureType.queryInsureTypeInfo4",context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
			    
				log.error("com.brick.baseManage.service.InsuCompanyService.getMotorFlagInfo"+ e.getMessage());				
				errList.add("com.brick.baseManage.service.InsuCompanyService.getMotorFlagInfo"+ e.getMessage());
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--保险公司险种分类错误!请联系管理员");
			}
		}
		
		if (errList.isEmpty()) {
		//	outputMap.put("insureTypeList1", insureTypeList1);
		//	outputMap.put("insureTypeList2", insureTypeList2);
		//	outputMap.put("insureTypeList3", insureTypeList3);
		//	outputMap.put("insureTypeList4", insureTypeList4);
			Output.jspOutput(outputMap, context,"/baseManage/insureCompanyManage/insuCompanyCreate.jsp");
		} else {
			outputMap.put("errList",errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	@SuppressWarnings("unchecked")
	public void insertCompanyBankAccount(SqlMapClient sqlMapper,Context context) throws Exception{
		
		sqlMapper.startBatch() ;
		//保险公司基本账户
		sqlMapper.update("companyBankAccount.deleteCompanyBankAccount", context.contextMap);
		Map baseBankAccount=new HashMap();
		baseBankAccount.put("s_employeeId", context.contextMap.get("s_employeeId"));
		baseBankAccount.put("BANK_NAME", context.contextMap.get("B_BANK_NAME"));
		baseBankAccount.put("BANK_ACCOUNT", context.contextMap.get("B_BANK_ACCOUNT"));
		baseBankAccount.put("STATE","0");
		baseBankAccount.put("INCP_ID", context.contextMap.get("incp_id"));
		if(baseBankAccount.get("BANK_NAME") != null && !baseBankAccount.get("BANK_NAME").toString().trim().equals("")){
			sqlMapper.insert("companyBankAccount.createCompanyBankAccount", baseBankAccount);
		}
		
		//保险公司其他账户
		sqlMapper.delete("companyBankAccount.deleteOtherCompanyBankAccount", context.contextMap);
		if(context.request.getParameterValues("BANK_NAME")!=null){
			String[] BANK_NAME=HTMLUtil.getParameterValues(context.request, "BANK_NAME", "");
			String[] BANK_ACCOUNT=HTMLUtil.getParameterValues(context.request, "BANK_ACCOUNT", "");
			for (int i = 0; i < BANK_NAME.length; i++) {
				Map bankAccount=new HashMap();
				bankAccount.put("s_employeeId", context.contextMap.get("s_employeeId"));
				bankAccount.put("BANK_NAME", BANK_NAME[i]);
				bankAccount.put("BANK_ACCOUNT", BANK_ACCOUNT[i]);
				bankAccount.put("STATE","1");
				bankAccount.put("INCP_ID", context.contextMap.get("incp_id"));
				sqlMapper.insert("companyBankAccount.createCompanyBankAccount", bankAccount);
			}
		}
		sqlMapper.executeBatch() ;
	}
}
