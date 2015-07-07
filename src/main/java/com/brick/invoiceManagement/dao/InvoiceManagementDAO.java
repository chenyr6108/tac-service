package com.brick.invoiceManagement.dao;

import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;
import com.brick.service.entity.Context;
import com.brick.util.StringUtils;

public class InvoiceManagementDAO extends BaseDAO {

	public List<Map<String,Object>> stopInvoiceQuery(Map<String,Object> param) {
		return this.getSqlMapClientTemplate().queryForList("invoiceManagement.stopInvoiceQuery",param);
	}
	
	public List<String> getEffectDateList() {
		return this.getSqlMapClientTemplate().queryForList("invoiceManagement.getEffectDateList");
	}
	public List<Map<String,Object>> getFinanceDateList() {
		return this.getSqlMapClientTemplate().queryForList("invoiceManagement.getFinanceDateList");
	}
	
	public List<Map<String,Object>> getPaymentDetail(Context context) {
		return this.getSqlMapClientTemplate().queryForList("invoiceManagement.getPaymentDetail",context.contextMap);
	}
	
	public void cancelStopInvoice(Map<String,Object> param) {
		this.getSqlMapClientTemplate().update("invoiceManagement.cancelStopInvoice",param);
	}
	
	public void addStopInvoice(Map<String,Object> param) throws Exception {
		this.getSqlMapClientTemplate().insert("invoiceManagement.addStopInvoice",param);
	}
	
	public void updateStopInvoice(Map<String,Object> param) throws Exception {
		this.getSqlMapClientTemplate().update("invoiceManagement.updateStopInvoice",param);
	}
	
	public String checkStopInvoice(Map<String,Object> param) {
		return (String)this.getSqlMapClientTemplate().queryForObject("invoiceManagement.checkStopInvoice",param);
	}
	public String checkOpenInvoice(Map<String,Object> param) {
		return (String)this.getSqlMapClientTemplate().queryForObject("invoiceManagement.checkOpenInvoice",param);
	}
	public String checkNotOpenInvoice(Map<String,Object> param) {
		String result=(String)this.getSqlMapClientTemplate().queryForObject("invoiceManagement.checkNotOpenInvoice",param);
		if(StringUtils.isEmpty(result)) {
			result="Y";
		}
		return result;
	}
	
	public String checkNotStopInvoice(Map<String,Object> param) {
		return (String)this.getSqlMapClientTemplate().queryForObject("invoiceManagement.checkNotStopInvoice",param);
	}
	
	public List<Map<String,Object>> showLog(Context context) {
		return this.getSqlMapClientTemplate().queryForList("invoiceManagement.showLog",context.contextMap);
	}
	public List<Map<String,Object>> showSpecialLog(Context context) {
		return this.getSqlMapClientTemplate().queryForList("invoiceManagement.showSpecialLog",context.contextMap);
	}
	
	public void insertDunRecord(Map<String,Object> param) throws Exception {
		this.getSqlMapClientTemplate().insert("invoiceManagement.insertDunRecord",param);
	}
	
	public List<String> getDun45DaysCase() {
		return this.getSqlMapClientTemplate().queryForList("invoiceManagement.getDun45DaysCase");
	}
	
	public void updateStopDun45DaysInvoice(Map<String,Object> param) {
		this.getSqlMapClientTemplate().update("invoiceManagement.updateStopDun45DaysInvoice",param);
	}
	
	public void updateSpecialCase(Context context) throws Exception {
		this.getSqlMapClientTemplate().update("invoiceManagement.updateSpecialCase",context.contextMap);
	}
	
	public void insertSpecialCase(Context context) {
		this.getSqlMapClientTemplate().insert("invoiceManagement.insertSpecialCase",context.contextMap);
	}
	
	public List<Map<String,Object>> getEmailContentList() {
		return this.getSqlMapClientTemplate().queryForList("invoiceManagement.getEmailContentList");
	}
	
	public List<Map<String,Object>> getInvoiceListForNewCase(Map<String,Object> param) {
		return this.getSqlMapClientTemplate().queryForList("invoiceManagement.getInvoiceListForNewCase",param);
	}
	public List<Map<String,Object>> getInvoiceListForOldCase(Map<String,Object> param) {
		return this.getSqlMapClientTemplate().queryForList("invoiceManagement.getInvoiceListForOldCase",param);
	}
	
	public void insertInvoiceInfo(Map<String,Object> param) {
		this.getSqlMapClientTemplate().insert("invoiceManagement.insertInvoiceInfo",param);
	}
	
	public Map<String,Object> getInvoiceInfo(Map<String,Object> param) {
		return (Map<String,Object>)this.getSqlMapClientTemplate().queryForObject("invoiceManagement.getInvoiceInfo",param);
	}
	
	public List<Map<String,Object>> getPostAddress(Map<String,Object> param) {
		return this.getSqlMapClientTemplate().queryForList("invoiceManagement.getPostAddress",param);
	}
	
	public void insertUploadLog(Map<String,Object> param) {
		this.getSqlMapClientTemplate().insert("invoiceManagement.insertUploadLog",param);
	}
	
	public void uploadInvoiceNum(Map<String,Object> param) {
		this.getSqlMapClientTemplate().update("invoiceManagement.uploadInvoiceNum",param);
	}
	
	public Map<String,Object> getResult(Context context) {
		return (Map<String,Object>)this.getSqlMapClientTemplate().queryForObject("invoiceManagement.getResult",context.contextMap);
	}
}
