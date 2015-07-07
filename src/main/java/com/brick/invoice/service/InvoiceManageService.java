package com.brick.invoice.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.brick.base.exception.ProcessException;
import com.brick.base.service.BaseService;
import com.brick.base.util.LeaseUtil;
import com.brick.common.mail.service.MailUtilService;
import com.brick.invoice.to.InvoiceTO;
import com.brick.service.entity.Context;
import com.brick.util.StringUtils;

public class InvoiceManageService extends BaseService {
	 
	private MailUtilService mailUtilService;

	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}
	
	/**
	 * 支票入口
	 * @param CreditId
	 * @throws SQLException 
	 * @throws Exception 
	 */
	@Transactional
	public void doAddProject(String credit_id, String create_by) throws ProcessException, SQLException{
		if (hasMe(credit_id)) {
			throw new ProcessException("该案件已经进入发票管理系统，操作失败！");
		} else {
			InvoiceTO invoice = new InvoiceTO();
			invoice.setCredit_id(credit_id);
			invoice.setTotal_money(LeaseUtil.getTotalContractPriceByCreditId(credit_id));
			invoice.setRemanent_money(LeaseUtil.getTotalContractPriceByCreditId(credit_id));
			invoice.setStatus(0);
			invoice.setInvoice_status(0);
			invoice.setCreate_by(create_by);
			invoice.setModify_by(create_by);
			insert("invoice.addPrjtInvoice", invoice);
			String suplId = LeaseUtil.getSuplIdByCreditId(credit_id);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("suplId", suplId);
			update("invoice.invoiceCreditMinusOne", paramMap);
		}
	}
	
	/**
	 * 是否已进入发票管理系统
	 * @param credit_id
	 * @return
	 */
	public boolean hasMe(String credit_id){
		boolean flag = false;
		InvoiceTO invoice = new InvoiceTO();
		invoice.setCredit_id(credit_id);
		Integer c = (Integer) queryForObj("invoice.hasMe", invoice);
		if (c != null && c > 0) {
			flag = true;
		}
		return flag;
	}
	
	@Transactional
	public void updateRemanentMoney(InvoiceTO invoice,boolean needUpdateFileStatus){
		Double invoiceMoney = null;
		Double totalMoney = null;
		invoiceMoney = (Double) queryForObj("invoice.getInvoiceMoneyByInvoiceId", invoice);
		totalMoney = (Double) queryForObj("invoice.getTotalMoneyByInvoiceId", invoice);
		InvoiceTO i = (InvoiceTO) queryForObj("invoice.getInvoiceById", invoice);
		invoice.setRemanent_money(totalMoney - invoiceMoney);
		if (totalMoney - invoiceMoney <= 0) {
			//补齐
			invoice.setInvoice_status(1);
			if(needUpdateFileStatus) {
			//更新合同资料发票
				doUpdateInvoiceStatus(i.getCredit_id(), 1, "发票管理已补齐", invoice.getModify_by());
			}
			
		} else {
			invoice.setInvoice_status(0);
			if(needUpdateFileStatus) {
			//更新合同资料发票
				doUpdateInvoiceStatus(i.getCredit_id(), 0, "发票管理未补齐", invoice.getModify_by());
			}
		}
		update("invoice.updateRemanentMoney", invoice);
	}
	
	@Transactional
	public void updateRemanentMoney(InvoiceTO invoice){
		updateRemanentMoney(invoice,true);
	}
	
	private void doUpdateInvoiceStatus(String credit_id, int flag, String msg, String modify_by){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("credit_id", credit_id);
		paramMap.put("flag", flag);
		paramMap.put("msg", msg);
		paramMap.put("modify_by", modify_by);
		Map<String, Object> refd = (Map<String, Object>) queryForObj("invoice.getFileDetailIdByCreditId", paramMap);
		if (refd != null && !String.valueOf(flag).equals(String.valueOf(refd.get("IS_ALREADY")))) {
			paramMap.put("refd_id", refd.get("REFD_ID"));
			update("invoice.updateFileDetail", paramMap);
			insert("invoice.insertFileDetailLog", paramMap);
		}
		
	}
	
	@Transactional
	public void batchUpdateInvoice(InvoiceTO invoice){
		InvoiceTO old = null;
		for (String s : invoice.getId().split(",")) {
			invoice.setId(s);
			old = (InvoiceTO) queryForObj("invoice.getInvoicesById", invoice);
			if (hasNewInvoice(old, invoice)) {
				update("invoice.updateInvoice", old);
				insert("invoice.addInvoiceLog", old);
			} else {
				System.out.println("无更新内容！");
			}
		}
		updateRemanentMoney(invoice);
	}

	@Transactional
	public void updateInvoice(InvoiceTO invoice) {
		InvoiceTO old = (InvoiceTO) queryForObj("invoice.getInvoicesById", invoice);
		if (hasDifferent(old, invoice)) {
			update("invoice.updateInvoice", invoice);
			insert("invoice.addInvoiceLog", invoice);
			updateRemanentMoney(invoice);
		} else {
			System.out.println("无更新内容！");
		}
	}
	
	private boolean hasNewInvoice(InvoiceTO old, InvoiceTO target){
		boolean flag = false;
		if(target.getInvoice_code() != null){
			old.setInvoice_code(target.getInvoice_code());
			flag = true;
		}
		if(target.getInvoice_money() != null){
			old.setInvoice_money(target.getInvoice_money());
			flag = true;
		}
		if(target.getInvoice_type() != null){
			old.setInvoice_type(target.getInvoice_type());
			flag = true;
		}
		if(target.getDrawer() != null){
			old.setDrawer(target.getDrawer());
			flag = true;
		}
		if(target.getMemo() != null){
			old.setMemo(target.getMemo());
			flag = true;
		}
		if(target.getStatus() != null){
			old.setStatus(target.getStatus());
			flag = true;
		}
		old.setModify_by(target.getModify_by());
		return flag;
	}
	
	private boolean hasDifferent(InvoiceTO old, InvoiceTO target){
		boolean flag = false;
		if(!target.getInvoice_code().equals(old.getInvoice_code())){
			flag = true;
		}
		if(!target.getInvoice_money().equals(old.getInvoice_money())){
			flag = true;
		}
		if(!target.getInvoice_type().equals(old.getInvoice_type())){
			flag = true;
		}
		if(!target.getDrawer().equals(old.getDrawer())){
			flag = true;
		}
		if(!target.getMemo().equals(old.getMemo())){
			flag = true;
		}
		if(!target.getStatus().equals(old.getStatus())){
			flag = true;
		}
		return flag;
	}

	@Transactional
	public void doAddInvoices(Context context) {
		String codeFrom = (String) context.contextMap.get("codeFrom");
		String codeTo = (String) context.contextMap.get("codeTo");
  		String money = (String) context.contextMap.get("money");
  		String drawer = (String) context.contextMap.get("drawer");
  		String invoice_type = (String) context.contextMap.get("invoice_type");
  		String invoice_id = (String) context.contextMap.get("invoice_id");
  		Object id = null;
  		InvoiceTO invoice = null;
  		if(existsInvoice(codeFrom,codeTo)) {
  			throw new ProcessException("系统中已存在该发票号,请核实!!");
  		}
		long codeFromNum = Long.parseLong(codeFrom);
		long codeToNum = Long.parseLong(codeTo);
		Double moneyDouble = Double.parseDouble(money);
		for (; codeFromNum <= codeToNum; codeFromNum++) {
			invoice = new InvoiceTO();
			invoice.setInvoice_id(invoice_id);
			invoice.setInvoice_code(String.valueOf(codeFromNum));
			invoice.setInvoice_money(moneyDouble);
			invoice.setInvoice_type(Integer.parseInt(invoice_type));
			invoice.setDrawer(drawer);
			invoice.setStatus(0);
			invoice.setCreate_by(String.valueOf(context.contextMap.get("s_employeeId")));
			invoice.setModify_by(String.valueOf(context.contextMap.get("s_employeeId")));
			id = insert("invoice.addInvoice", invoice);
			invoice.setId(String.valueOf(id));
			insert("invoice.addInvoiceLog", invoice);
		}
		invoice = new InvoiceTO();
		invoice.setInvoice_id(invoice_id);
		updateRemanentMoney(invoice);
	}
	
	/**
	 * 验证发票号是否已存在
	 * @param from
	 * @param to
	 * @return
	 */
	@Transactional
	public boolean existsInvoice(String from, String to) {
		Map<String,Object> param = new HashMap<String, Object>();
		param.put("from",from);
		param.put("to", to);
		Integer count = (Integer) queryForObj("invoice.existsInvoice", param);
		return count > 0;
	}
}
