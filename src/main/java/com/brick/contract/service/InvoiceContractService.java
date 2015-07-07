package com.brick.contract.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.brick.coderule.service.CodeRule;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.CurrencyConverter;
import com.brick.util.web.HTMLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;
/**
 * 合同管理  开票协议书
 * @author 吴振东
 * @创建日期 2010-7-19
 * @版本 V 1.0
 */
public class InvoiceContractService extends AService {	
	Log logger = LogFactory.getLog(InvoiceContractService.class);
	@Override
	protected void afterExecute(String action, Context context) {
		super.afterExecute(action, context);
	}
	@Override
	protected boolean preExecute(String action, Context context) {
		return super.preExecute(action, context);
	}
	/**
	 * 进入支付表选择页面 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void checkInvoice(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List invoiceCheck = null;
		Map infor = new HashMap();
		Map comInfor = new HashMap();
		if(errList.isEmpty()) {
			try {
				//查询支付表
				invoiceCheck = (List)DataAccessor.query("invoiceContract.checkInvoice", context.contextMap, DataAccessor.RS_TYPE.LIST);
				//查询基本信息  合同编号及承租人
				infor = (Map)DataAccessor.query("invoiceContract.infor", context.contextMap, DataAccessor.RS_TYPE.MAP);
				//查询公司信息 所属公司
				comInfor = (Map)DataAccessor.query("companyManage.readCompanyAliasByRectId", context.contextMap, DataAccessor.RS_TYPE.MAP);		
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
				//errList.add("com.brick.bankinfo.service.BankInfoManager.getCreateBankJsp"+ e.getMessage());
			}
		}		
		if(errList.isEmpty()) {
			outputMap.put("invoiceCheck", invoiceCheck);
			outputMap.put("infor", infor);
			outputMap.put("RECT_ID", context.contextMap.get("RECT_ID"));
			outputMap.put("comInfor", comInfor);
			Output.jspOutput(outputMap, context,"/rentcontract/invoiceCheck.jsp");	
		}
	}
	/**
	 * 进入支付表添加页面 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void create(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List invoiceCheck = null;
		Map infor = new HashMap();
		Map comInfor = new HashMap();
		String REIP_CODE="";
		List macInfor=null;
		double he=0;
		String chuan="";
		if(errList.isEmpty()) {
			try {
				//开票协议书编号 
				 REIP_CODE=CodeRule.generateInvoiceCode(context.contextMap.get("RECT_ID"));
				//乙方
				infor = (Map)DataAccessor.query("invoiceContract.infor", context.contextMap, DataAccessor.RS_TYPE.MAP);
				//甲方
				comInfor = (Map)DataAccessor.query("companyManage.readCompanyAliasByRectId", context.contextMap, DataAccessor.RS_TYPE.MAP);
				//查询设备 根据选择的支付表号
				String[] ids = HTMLUtil.getParameterValues(context.getRequest(),"ids","");
				for (int i = 0; i < ids.length; i++) {
					chuan=chuan+ids[i]+"/";
				}
				context.contextMap.put("idss",ids);				
				macInfor = (List)DataAccessor.query("invoiceContract.selectMac", context.contextMap, DataAccessor.RS_TYPE.LIST);
				for (Iterator iterator = macInfor.iterator(); iterator.hasNext();) {
					Map object = (Map) iterator.next();		
					double a=Double.parseDouble(object.get("AMOUNT").toString());
					double b=Double.parseDouble(object.get("UNIT_PRICE").toString());
					he=he+a*b;		
				}
				CurrencyConverter A2=new CurrencyConverter();	
				outputMap.put("heD", A2.toUpper(String.valueOf(he)));
				outputMap.put("chuan", chuan);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
				//errList.add("com.brick.bankinfo.service.BankInfoManager.getCreateBankJsp"+ e.getMessage());
			}
		}		
		if(errList.isEmpty()) {
			outputMap.put("invoiceCheck", invoiceCheck);
			outputMap.put("infor", infor);
			outputMap.put("he", he);
			outputMap.put("REIP_CODE", REIP_CODE);
			outputMap.put("comInfor", comInfor);
			outputMap.put("macInfor", macInfor);
			Output.jspOutput(outputMap, context,"/rentcontract/invoiceCreate.jsp");	
		}
	}	
	/**
	 * 保存开票协议书 后到合同管理页面
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void add(Context context) {	
		Map outputMap = new HashMap();
		List errList = context.errList;
		long reip_id=0;
		if(errList.isEmpty()) {
			try {
				if (!context.contextMap.get("WHOLE_STATUS").toString().equals("3")) {
					context.contextMap.put("WHOLE_TYPE", "");			
				}
				if (!context.contextMap.get("OTHER_STATUS").toString().equals("2")) {
					context.contextMap.put("OTHER_TYPE", "");			
				}						
				reip_id=(Long)DataAccessor.execute("invoiceContract.add", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
				String[] chuan=context.contextMap.get("chuan").toString().split("/");
				for (int i = 0; i < chuan.length; i++) {
					context.contextMap.put("chuan", chuan[i]);
					context.contextMap.put("reip_id",String.valueOf(reip_id));
					DataAccessor.execute("invoiceContract.updatePlan", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
				}
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
				//errList.add("com.brick.bankinfo.service.BankInfoManager.create"+ e.getMessage());
			}
		}		
		if(errList.isEmpty()) {
			Output.jspSendRedirect(context, "defaultDispatcher?__action=rentContract.queryRentContract");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	/**
	 * 进入支付表修改页面 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void update(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List macInfor=null;
		Map invoice=new HashMap();
		int he=0;
		List planList=null;
		if(errList.isEmpty()) {
			try {
				//根据开票协议书号查询支付表号并存数组中
				planList = (List)DataAccessor.query("invoiceContract.selectPlanId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				String[] ids=new String[planList.size()];
				int i=0;
				for (Iterator iterator = planList.iterator(); iterator.hasNext();) {
					Map object = (Map) iterator.next();					
					ids[i]=object.get("RECP_ID").toString();
					i++;
				}
				//查询设备 根据查询的支付表号
				context.contextMap.put("idss",ids);				
				macInfor = (List)DataAccessor.query("invoiceContract.selectMac", context.contextMap, DataAccessor.RS_TYPE.LIST);
				for (Iterator iterator = macInfor.iterator(); iterator.hasNext();) {
					Map object = (Map) iterator.next();		
					int a=Integer.parseInt(object.get("AMOUNT").toString());
					int b=Integer.parseInt(object.get("UNIT_PRICE").toString());
					he=he+a*b;		
				}
				CurrencyConverter A2=new CurrencyConverter();	
				outputMap.put("heD", A2.toUpper(String.valueOf(he)));
				//根据开票协议书号查询开票协议书信息
				invoice = (Map)DataAccessor.query("invoiceContract.selectInvoiceById", context.contextMap, DataAccessor.RS_TYPE.MAP);				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
				//errList.add("com.brick.bankinfo.service.BankInfoManager.getCreateBankJsp"+ e.getMessage());
			}
		}		
		if(errList.isEmpty()) {
			outputMap.put("he", he);
			outputMap.put("macInfor", macInfor);
			outputMap.put("invoice", invoice);
			Output.jspOutput(outputMap, context,"/rentcontract/invoiceUpdate.jsp");	
		}
	}
	
	/**
	 * 保存开票协议书 后到合同管理页面
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void updateInvoice(Context context) {	
		Map outputMap = new HashMap();
		List errList = context.errList;
		long reip_id=0;
		if(errList.isEmpty()) {
			try {
				if (!context.contextMap.get("WHOLE_STATUS").toString().equals("3")) {
					context.contextMap.put("WHOLE_TYPE", "");			
				}
				if (!context.contextMap.get("OTHER_STATUS").toString().equals("2")) {
					context.contextMap.put("OTHER_TYPE", "");			
				}						
				DataAccessor.execute("invoiceContract.updateInvoice", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
				//errList.add("com.brick.bankinfo.service.BankInfoManager.create"+ e.getMessage());
			}
		}		
		if(errList.isEmpty()) {
			Output.jspSendRedirect(context, "defaultDispatcher?__action=rentContract.queryRentContract");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/**
	 * 进入支付表查看页面 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void show(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List macInfor=null;
		Map invoice=new HashMap();
		int he=0;
		List planList=null;
		if(errList.isEmpty()) {
			try {
				//根据开票协议书号查询支付表号并存数组中
				planList = (List)DataAccessor.query("invoiceContract.selectPlanId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				String[] ids=new String[planList.size()];
				int i=0;
				for (Iterator iterator = planList.iterator(); iterator.hasNext();) {
					Map object = (Map) iterator.next();					
					ids[i]=object.get("RECP_ID").toString();
					i++;
				}
				//查询设备 根据查询的支付表号
				context.contextMap.put("idss",ids);				
				macInfor = (List)DataAccessor.query("invoiceContract.selectMac", context.contextMap, DataAccessor.RS_TYPE.LIST);
				for (Iterator iterator = macInfor.iterator(); iterator.hasNext();) {
					Map object = (Map) iterator.next();		
					int a=Integer.parseInt(object.get("AMOUNT").toString());
					int b=Integer.parseInt(object.get("UNIT_PRICE").toString());
					he=he+a*b;		
				}
				CurrencyConverter A2=new CurrencyConverter();	
				outputMap.put("heD", A2.toUpper(String.valueOf(he)));
				//根据开票协议书号查询开票协议书信息
				invoice = (Map)DataAccessor.query("invoiceContract.selectInvoiceById", context.contextMap, DataAccessor.RS_TYPE.MAP);				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
				//errList.add("com.brick.bankinfo.service.BankInfoManager.getCreateBankJsp"+ e.getMessage());
			}
		}		
		if(errList.isEmpty()) {
			outputMap.put("he", he);
			outputMap.put("macInfor", macInfor);
			outputMap.put("invoice", invoice);
			Output.jspOutput(outputMap, context,"/rentcontract/invoiceShow.jsp");	
		}
	}	
}

