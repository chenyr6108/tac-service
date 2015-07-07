package com.brick.invoice.command;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.brick.base.command.BaseCommand;
import com.brick.base.to.PagingInfo;
import com.brick.base.util.LeaseUtil;
import com.brick.invoice.service.InvoiceManageService;
import com.brick.invoice.to.InvoiceTO;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.StringUtils;

public class InvoiceManageCommand extends BaseCommand {
	
	Logger logger = Logger.getLogger(InvoiceManageCommand.class);
	
	private InvoiceManageService invoiceManageService;
	
	public InvoiceManageService getInvoiceManageService() {
		return invoiceManageService;
	}

	public void setInvoiceManageService(InvoiceManageService invoiceManageService) {
		this.invoiceManageService = invoiceManageService;
	}

	
	public void queryPage(Context context) throws Exception{
		Map<String, Object> outputMap = new HashMap<String, Object>();
		PagingInfo<Object> pagingInfo = null;
		if (context.contextMap.get("contract_type") == null) {
			context.contextMap.put("contract_type", "7");
		}
		if (context.contextMap.get("invoice_status") == null) {
			context.contextMap.put("invoice_status", "0");
		}
		try {
			pagingInfo = baseService.queryForListWithPaging("invoice.queryPage", context.contextMap, "CREDIT_RUNCODE");
			outputMap.put("pagingInfo", pagingInfo);
			outputMap.put("contract_type", context.contextMap.get("contract_type"));
			outputMap.put("invoice_status", context.contextMap.get("invoice_status"));
			outputMap.put("search_supl_name", context.contextMap.get("search_supl_name"));
			outputMap.put("search_content", context.contextMap.get("search_content"));
			outputMap.put("contractTypeSelection", LeaseUtil.getAllContractType());
			outputMap.put("invoice_id", context.contextMap.get("invoice_id"));
			outputMap.put("msg", context.contextMap.get("msg"));
			Output.jspOutput(outputMap, context, "/invoice/invoiceManage.jsp");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw e;
		}
	}
	
	public void addProject(Context context) throws Exception{
		String credit_id = (String) context.contextMap.get("credit_id");
		try {
			invoiceManageService.doAddProject(credit_id, String.valueOf(context.contextMap.get("s_employeeId")));
			context.contextMap.put("msg", "操作成功！");
		} catch (Exception e) {
			logger.info(e.getMessage());
			context.contextMap.put("msg", e.getMessage());
		}
		queryPage(context);
	}
	
	
	
	public void getCreditIdByLeaseCode(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		String leaseCode = (String) context.contextMap.get("lease_code");
		String credit_id = null;
		//Double total_money = null;
		try {
			credit_id = LeaseUtil.getCreditIdByLeaseCode(leaseCode);
			//total_money = LeaseUtil.getTotalPriceByCreditId(credit_id);
		} catch (SQLException e) {
			logger.error(e);
		}
		outputMap.put("credit_id", credit_id);
		//outputMap.put("total_money", total_money);
		Output.jsonOutput(outputMap, context);
	}
	
	public void addInvoices(Context context) throws Exception{
		try {
			invoiceManageService.doAddInvoices(context);
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw e;
		}
		queryPage(context);
	}
	
	public void showInvoicesDetail(Context context) throws Exception{
		Map<String, Object> outputMap = new HashMap<String, Object>();
		List<InvoiceTO> invoices = null;
		try {
			invoices = (List<InvoiceTO>) baseService.queryForList("invoice.showInvoicesDetail", context.contextMap);
			outputMap.put("invoices", invoices);
			outputMap.put("IS_ALREADY", context.getContextMap().get("IS_ALREADY"));
			Output.jspOutput(outputMap, context, "/invoice/invoicesDetail.jsp");
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}
	
	public void updateInvoice(Context context) throws Exception{
		try {
			InvoiceTO invoice = (InvoiceTO) context.getFormBean("invoice");
			invoice.setModify_by(String.valueOf(context.contextMap.get("s_employeeId")));
			context.contextMap.put("invoice_id", invoice.getInvoice_id());
			invoiceManageService.updateInvoice(invoice);
			queryPage(context);
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}
	
	public void updateInvoiceStatus(Context context) throws Exception{
		try {
			InvoiceTO invoice = (InvoiceTO) context.getFormBean("invoice");
			invoice.setModify_by(String.valueOf(context.contextMap.get("s_employeeId")));
			baseService.update("invoice.updateInvoiceStatus", invoice);
			context.contextMap.put("invoice_id", invoice.getInvoice_id());
			invoiceManageService.updateRemanentMoney(invoice);
			queryPage(context);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw e;
		}
	}
	
	public void batchUpdateInvoice(Context context) throws Exception{
		InvoiceTO invoice = (InvoiceTO) context.getFormBean("invoice");
		invoice.setModify_by(String.valueOf(context.contextMap.get("s_employeeId")));
		try {
			invoiceManageService.batchUpdateInvoice(invoice);
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
		context.contextMap.put("invoice_id", invoice.getInvoice_id());
		queryPage(context);
	}
	
	public void showInvoiceLog(Context context){
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("id", context.contextMap.get("id"));
		List<InvoiceTO> logs = (List<InvoiceTO>) baseService.queryForList("invoice.showInvoicesLog", map);
		
		map.put("logs", logs);
		Output.jspOutput(map, context, "/invoice/invoiceLog.jsp");
	}
	
}
