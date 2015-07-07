package com.brick.insurance.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.to.PagingInfo;
import com.brick.log.service.LogPrint;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.StringUtils;
import com.brick.util.web.HTMLUtil;
import com.ibatis.sqlmap.client.SqlMapClient;
/*
 * 新车投保
 */
public class InsureService extends BaseCommand {
	static Log logger = LogFactory.getLog(InsureService.class);
	/**
	 * 保险管理页
	 * 
	 */
	@SuppressWarnings("unchecked")
	public  void queryAll(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		PagingInfo<Object> dw = null;
		List insuCompany = null;
		/*2011/12/12 Yang Yun Add pay status search field. Start*/
		String pay_state = null;
		/*2011/12/12 Yang Yun Add pay status search field. End*/
		if (errList.isEmpty()) {
			try {
				/*2011/12/12 Yang Yun Add pay status search field. Start*/
				if (context.contextMap.get("pay_state") == null) {
					pay_state = "Y";
				} else {
					pay_state = ((String) context.contextMap.get("pay_state")).trim();
				}
				context.contextMap.put("pay_state", pay_state);
				/*2011/12/12 Yang Yun Add pay status search field. End*/
				dw = baseService.queryForListWithPaging("insurance.insuranceManage", context.contextMap, "CUST_NAME");
						/*(DataWrap) DataAccessor.query("insurance.insuranceManage",
						context.contextMap, DataAccessor.RS_TYPE.PAGED);*/
				insuCompany = (List) DataAccessor.query("insurance.getCompany",context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				errList.add("保险管理页 : " + e);
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		
		if (errList.isEmpty()) {
			outputMap.put("dw", dw);
			outputMap.put("insuCompany", insuCompany);
			outputMap.put("output_status", context.contextMap.get("output_status"));
			/*2011/12/12 Yang Yun Add pay status search field. Start*/
			outputMap.put("pay_state", pay_state);
			/*2011/12/12 Yang Yun Add pay status search field. End*/
			outputMap.put("content", context.contextMap.get("content"));
			outputMap.put("insu_company", context.contextMap.get("insu_company"));
			Output
					.jspOutput(outputMap, context,
							"/insurance/insure/insuranceManage.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}
	
	/**
	 * 续保
	 * @param context
	 */
	public void renewalInsuList(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList;
		List InsuType=null;
		List eqmtType=new ArrayList();
		Map  eqmt=new HashMap();
		List insuCompany=new ArrayList();
		Map<String, Object> paraMap = new HashMap<String, Object>();
		List<Map<String, Object>> eqmtList = null;
		if (errList.isEmpty()) {
			try {
				//查出所有险种List
				context.contextMap.put("TYPE", "险种");
				InsuType = (List) DataAccessor.query("insurance.getInsureType",context.contextMap, DataAccessor.RS_TYPE.LIST);
				//查出设备和合同号
				eqmtList = (List) DataAccessor.query("insurance.getEqmtByInsuId",context.contextMap, DataAccessor.RS_TYPE.LIST);
				if (eqmtList != null && eqmtList.size() > 0) {
					outputMap.put("contract",eqmtList.get(0));
					for (Map<String, Object> map : eqmtList) {
						paraMap.put("RECD_ID", map.get("RECD_ID"));
						eqmt=(Map) DataAccessor.query("insurance.getEqmtByRecdId", paraMap, DataAccessor.RS_TYPE.MAP);
						eqmtType.add(eqmt);
					}
				}
			/*//查出所有的设备
				String[] cboxs = HTMLUtil.getParameterValues(context.request,"cbox", "");
				//String RECD_ID=null;
				if(cboxs!=null){
					//查合同号
					context.contextMap.put("RECD_ID", cboxs[0]);
					contract= (Map) DataAccessor.query("insurance.getContractCode",context.contextMap, DataAccessor.RS_TYPE.MAP);
					outputMap.put("contract",contract);
					for( int i=0;i<cboxs.length;i++){
						context.contextMap.put("RECD_ID", cboxs[i]);
						eqmt=(Map) DataAccessor.query("insurance.getEqmtByRecdId",context.contextMap, DataAccessor.RS_TYPE.MAP);
						eqmtType.add(eqmt);
					}
				}*/
			//查出所有的保险公司
				insuCompany=(List) DataAccessor.query("insurance.getCompany",context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				errList.add("保险添加页 : " + e);
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		
		if (errList.isEmpty()) {
			outputMap.put("incu_id", context.contextMap.get("incu_id"));
			outputMap.put("InsuType",InsuType);
			outputMap.put("insuCompany",insuCompany );
			outputMap.put("eqmtType", eqmtType);
			Output.jspOutput(outputMap, context, "/insurance/insure/insureCreate.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/*
	 * 录入保单的前置页
	 */
	@SuppressWarnings("unchecked")
	public  void enterInsuList(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List InsuType=null;
		List eqmtType=new ArrayList();
		Map  eqmt=new HashMap();
		Map contract=null;
		List insuCompany=new ArrayList();
		if (errList.isEmpty()) {
			try {
			//查出所有险种List
				context.contextMap.put("TYPE", "险种");
				InsuType = (List) DataAccessor.query("insurance.getInsureType",context.contextMap, DataAccessor.RS_TYPE.LIST);
			//查出所有的设备
				String[] cboxs = HTMLUtil.getParameterValues(context.request,"cbox", "");
				//String RECD_ID=null;
				if(cboxs!=null){
					//查合同号
					context.contextMap.put("RECD_ID", cboxs[0]);
					contract= (Map) DataAccessor.query("insurance.getContractCode",context.contextMap, DataAccessor.RS_TYPE.MAP);
					outputMap.put("contract",contract);
					for( int i=0;i<cboxs.length;i++){
						context.contextMap.put("RECD_ID", cboxs[i]);
						eqmt=(Map) DataAccessor.query("insurance.getEqmtByRecdId",context.contextMap, DataAccessor.RS_TYPE.MAP);
						eqmtType.add(eqmt);
					}
				}
			//查出所有的保险公司
				insuCompany=(List) DataAccessor.query("insurance.getCompany",context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				errList.add("保险添加页 : " + e);
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		
		if (errList.isEmpty()) {
			
			outputMap.put("InsuType",InsuType);
			outputMap.put("insuCompany",insuCompany );
			outputMap.put("eqmtType", eqmtType);
			Output
					.jspOutput(outputMap, context,
							"/insurance/insure/insureCreate.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}
	/*
	 * ajax返回公司详细信息
	 */
	@SuppressWarnings("unchecked")
	public void getInsuPolicyType(Context context){
		Map insuCompany=null;
		Map outputMap = new HashMap();
		try {
			insuCompany=(Map)DataAccessor.query("insuCompany.getCompanyById", context.contextMap, DataAccessor.RS_TYPE.MAP);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("insuCompany", insuCompany);
		Output.jsonOutput(outputMap, context);
	}
	/*
	 * 保存保单
	 */
	@SuppressWarnings("unchecked")
	public void createInsuList(Context context) {

		List errList = context.errList;	
		//获得选择的险种ID
		String[] intpIds = HTMLUtil.getParameterValues(context.request,"intp_id", "");
		//获得设备ID
		String[] eqmts=HTMLUtil.getParameterValues(context.request,"EQMT", "");
		Long incu_id = null;
		Map outputMap = new HashMap() ;
		String incu_id_old = null;
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		if (errList.isEmpty()) {
			try {
				sqlMapper.startTransaction() ;
				/* 2012/1/6 Yang Yun 增加续保前保单号字段. */
				incu_id_old = (String) context.contextMap.get("incu_id_old");
				if (!StringUtils.isEmpty(incu_id_old)) {
					incu_id_old = incu_id_old.trim();
				}
				context.contextMap.put("incu_id_old", incu_id_old);
				/* 2012/1/6 Yang Yun 增加续保前保单号字段. */
				incu_id = (Long) sqlMapper.insert("insurance.createInsuList", context.contextMap);
				context.contextMap.put("insu_id", incu_id);
				//将险种加入中间表
				if (intpIds != null) {
					//DataAccessor.getSession().startBatch();
					sqlMapper.startBatch();
					for (String intpId : intpIds) {			    
						context.contextMap.put("intp_id", intpId);
						sqlMapper.insert("insurance.createInsutype2InsuList",context.contextMap);
					}
					sqlMapper.executeBatch();
	
				}
				//将设备加入中间表
				if (eqmts != null) {
					sqlMapper.startBatch();
					for (String eqmt : eqmts) {	
						
						context.contextMap.put("eqmt_id", eqmt);
						context.contextMap.put("RECD_ID", eqmt);
						sqlMapper.insert("insurance.createEqmt2InsuList",context.contextMap);
						//将设备的投保状态更新为以投保
						context.contextMap.put("insure_status", 1);
						sqlMapper.update("insurance.updateEqmt", context.contextMap);
					}
					sqlMapper.executeBatch();					
					sqlMapper.commitTransaction();
				}
			} catch (Exception e) {
        			    
        				e.printStackTrace();
        				LogPrint.getLogStackTrace(e, logger);
        				errList.add("系统设置--保险公司添加错误!请联系管理员");
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
		    if (StringUtils.isEmpty(incu_id_old)) {
		    	Output.jspSendRedirect(context,"defaultDispatcher?__action=insure.queryAll");
			} else {
				Output.jspSendRedirect(context,"defaultDispatcher?__action=renewal.queryAll");
			}
		}else{
		       //跳转错误页面
			outputMap.put("errList",errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}

	}
	/*
	 * 查看保单
	 */
	@SuppressWarnings("unchecked")
	public void showInsuList(Context context) {
		List errList = context.errList;	
		Map outputMap = new HashMap();
		Map insuList=new HashMap();
		List InsuType=null;
		List eqmtId=new ArrayList();
		List eqmtType=new ArrayList();
		Map  eqmt=new HashMap();
		Map  eq=new HashMap();
		Map insuCompany=new HashMap();
		try {
			//查出保单
			insuList=(Map)DataAccessor.query("insurance.getInsuListById", context.contextMap, DataAccessor.RS_TYPE.MAP);
			//根据id查出保险公司信息
			context.contextMap.put("INCP_ID",insuList.get("INCP_ID"));
			insuCompany=(Map)DataAccessor.query("insurance.getInsuCompanyById", context.contextMap, DataAccessor.RS_TYPE.MAP);
			//根据保单的id查出中间表投保的险种
			context.contextMap.put("INSU_ID", insuList.get("INCU_ID"));
			InsuType=(List) DataAccessor.query("insurance.getInsureTypeById",context.contextMap, DataAccessor.RS_TYPE.LIST);
			//根据保单id在中间表查出投保的设备
			eqmtId=(List) DataAccessor.query("insurance.getEqmtByIncuId",context.contextMap, DataAccessor.RS_TYPE.LIST);
			//查出设备的详细信息
			for(int i=0;i<eqmtId.size();i++){
				eqmt=(Map) eqmtId.get(i);
				context.contextMap.put("EQMT_ID", eqmt.get("EQMT_ID"));
//				context.contextMap.put("RECD_ID", eqmt.get("EQMT_ID"));
				eq=(Map) DataAccessor.query("insurance.getEqmtById",context.contextMap, DataAccessor.RS_TYPE.MAP);
				eqmtType.add(eq);
			}
		} catch (Exception e) { 
			errList.add("读取保单信息错误!" + e.toString());
			e.printStackTrace();
		}
		
		if (errList.isEmpty()) {
			outputMap.put("insuList", insuList);
			outputMap.put("InsuType",InsuType);
			outputMap.put("insuCompany",insuCompany );
			outputMap.put("eqmtType", eqmtType);
			Output.jspOutput(outputMap, context, "/insurance/insuList/insureShow.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}
	/*
	 * 修改保单前置
	 */
	@SuppressWarnings("unchecked")
	public void updateInsuListPre(Context context) {
		List errList = context.errList;	
		Map outputMap = new HashMap();

		Map insuList=new HashMap();
		List InsuType=null;
		List eqmtId=new ArrayList();
		List eqmtType=new ArrayList();
		Map  eqmt=new HashMap();
		Map  eq=new HashMap();
		Map insuCompanyMap=new HashMap();
		List insuCompany=new ArrayList();
		try {
			//查出保单
			insuList=(Map)DataAccessor.query("insurance.getInsuListById", context.contextMap, DataAccessor.RS_TYPE.MAP);
			//根据id查出保险公司信息
			context.contextMap.put("INCP_ID",insuList.get("INCP_ID"));
			insuCompany = (List) DataAccessor.query("insurance.getCompany",context.contextMap, DataAccessor.RS_TYPE.LIST);

			insuCompanyMap=(Map)DataAccessor.query("insurance.getInsuCompanyById", context.contextMap, DataAccessor.RS_TYPE.MAP);
			//根据保单的id查出中间表投保的险种
			context.contextMap.put("INSU_ID", insuList.get("INCU_ID"));
			context.contextMap.put("TYPE", "险种");
			InsuType=(List) DataAccessor.query("insurance.getInsureTypeByIdforUpdate",context.contextMap, DataAccessor.RS_TYPE.LIST);
			//根据保单id在中间表查出投保的设备
			eqmtId=(List) DataAccessor.query("insurance.getEqmtByIncuId",context.contextMap, DataAccessor.RS_TYPE.LIST);
			//查出设备的详细信息
			for(int i=0;i<eqmtId.size();i++){
				eqmt=(Map) eqmtId.get(i);
				context.contextMap.put("EQMT_ID", eqmt.get("EQMT_ID"));
//				context.contextMap.put("RECD_ID", eqmt.get("EQMT_ID"));
				eq=(Map) DataAccessor.query("insurance.getEqmtById",context.contextMap, DataAccessor.RS_TYPE.MAP);
				eqmtType.add(eq);
			}
		} catch (Exception e) { 
			errList.add("读取保单信息错误!" + e.toString());
			e.printStackTrace();
		}
		
		if (errList.isEmpty()) {
			outputMap.put("insuList", insuList);
			outputMap.put("InsuType",InsuType);
			outputMap.put("insuCompanyMap",insuCompanyMap );
			outputMap.put("insuCompany",insuCompany );
			outputMap.put("eqmtType", eqmtType);
			Output.jspOutput(outputMap, context, "/insurance/insuList/insureUpdate.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}


	}
	/*
	 * 修改保单
	 */
	@SuppressWarnings("unchecked")
	public void updateInsuList(Context context) {
		List errList = context.errList;	
		//获得选择的险种ID
		String[] intpIds = HTMLUtil.getParameterValues(context.request,"intp_id", "");
		//获得设备ID
		String[] eqmts=HTMLUtil.getParameterValues(context.request,"EQMT", "");
		//Map insureName = null;
		//Long incu_id = null;
		Map outputMap = new HashMap() ;
		
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		if (errList.isEmpty()) {
			try {
				sqlMapper.startTransaction() ;
				sqlMapper.insert("insurance.updateInsuList", context.contextMap);
				context.contextMap.put("insu_id", context.request.getParameter("INCU_ID"));
				//删除险种中间表
				sqlMapper.delete("insurance.delInsuType2InsuList", context.contextMap);
				//将险种加入中间表
				if (intpIds != null) {
					//DataAccessor.getSession().startBatch();
					sqlMapper.startBatch();
					for (String intpId : intpIds) {			    
						context.contextMap.put("intp_id", intpId);
						sqlMapper.insert("insurance.createInsutype2InsuList",context.contextMap);
					}
					sqlMapper.executeBatch();
	
				}
				//删除设备中间表---》1.连中间表修改投保状态2.删除中间表
				sqlMapper.update("insurance.updateEqmtStatus", context.contextMap);
				sqlMapper.delete("insurance.delEqmt2InsuList",  context.contextMap);
				//将设备加入中间表
				if (eqmts != null) {
					sqlMapper.startBatch();
					for (String eqmt : eqmts) {			    
						context.contextMap.put("eqmt_id", eqmt);
						context.contextMap.put("RECD_ID", eqmt);
						sqlMapper.insert("insurance.createEqmt2InsuList",context.contextMap);
						//将设备的投保状态更新为以投保
						context.contextMap.put("insure_status", 1);
						sqlMapper.update("insurance.updateEqmt", context.contextMap);
					}
					sqlMapper.executeBatch();					
					sqlMapper.commitTransaction();
				}
			} catch (Exception e) {
        			    
        				e.printStackTrace();
        				LogPrint.getLogStackTrace(e, logger);
        				errList.add("系统设置--保险公司添加错误!请联系管理员");
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
		    
			Output.jspSendRedirect(context,"defaultDispatcher?__action=insuranceList.queryAll");
		
		}else{
		       //跳转错误页面
			outputMap.put("errList",errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}

	}
	/*
	 * 作废保单
	 */
	@SuppressWarnings("unchecked")
	public void delInsuList(Context context) {
		List errList = context.errList;	
		Map outputMap = new HashMap();
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		if (errList.isEmpty()) {
			try {
				sqlMapper.startTransaction() ;
				sqlMapper.update("insurance.delInsuList", context.contextMap);
				context.contextMap.put("insu_id", context.request.getParameter("INCU_ID"));
				//删除险种中间表
				sqlMapper.delete("insurance.delInsuType2InsuList", context.contextMap);
				//删除设备中间表---》1.连中间表修改投保状态2.删除中间表
				sqlMapper.update("insurance.updateEqmtStatus", context.contextMap);
				sqlMapper.delete("insurance.delEqmt2InsuList",  context.contextMap);
					sqlMapper.executeBatch();					
					sqlMapper.commitTransaction();
				} catch (Exception e) {
        			    
        				e.printStackTrace();
        				LogPrint.getLogStackTrace(e, logger);
        				errList.add("系统设置--删除错误!请联系管理员");
			} finally {
			    
			    
        				try {
        					sqlMapper.endTransaction();
        					
        					
        				} catch (Exception e) {
        				    
        					e.printStackTrace();
        					LogPrint.getLogStackTrace(e, logger);
        					errList.add(e);
        				}
			
		}
		if (errList.isEmpty()) { 
		    /*2011/12/12 Yang Yun Merger “作废保单”. Start*/
			//Output.jspSendRedirect(context,"defaultDispatcher?__action=cancellation.queryAll");
			Output.jspSendRedirect(context,"defaultDispatcher?__action=insuranceCommand.getAllInsu");
			/*2011/12/12 Yang Yun Merger “作废保单”. End*/
		}else{
		       //跳转错误页面
			outputMap.put("errList",errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
		}

	}
	
}
