package com.brick.invoice.to;

import com.brick.base.to.BaseTo;

public class InvoiceTO extends BaseTo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String invoice_id;
	private String credit_id;
	private String invoice_code;
	private Double invoice_money;
	private Double total_money;
	private Double remanent_money;
	private Integer invoice_status;
	private Integer invoice_type;
	private String invoice_type_desc;
	private String drawer;
	private Integer status;
	private String memo;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getInvoice_id() {
		return invoice_id;
	}
	public void setInvoice_id(String invoice_id) {
		this.invoice_id = invoice_id;
	}
	public String getCredit_id() {
		return credit_id;
	}
	public void setCredit_id(String credit_id) {
		this.credit_id = credit_id;
	}
	public String getInvoice_code() {
		return invoice_code;
	}
	public void setInvoice_code(String invoice_code) {
		this.invoice_code = invoice_code;
	}
	public Double getInvoice_money() {
		return invoice_money;
	}
	public void setInvoice_money(Double invoice_money) {
		this.invoice_money = invoice_money;
	}
	public Double getTotal_money() {
		return total_money;
	}
	public void setTotal_money(Double total_money) {
		this.total_money = total_money;
	}
	public Double getRemanent_money() {
		return remanent_money;
	}
	public void setRemanent_money(Double remanent_money) {
		this.remanent_money = remanent_money;
	}
	public Integer getInvoice_status() {
		return invoice_status;
	}
	public void setInvoice_status(Integer invoice_status) {
		this.invoice_status = invoice_status;
	}
	public Integer getInvoice_type() {
		return invoice_type;
	}
	public void setInvoice_type(Integer invoice_type) {
		this.invoice_type = invoice_type;
	}
	public String getInvoice_type_desc() {
		return invoice_type_desc;
	}
	public void setInvoice_type_desc(String invoice_type_desc) {
		this.invoice_type_desc = invoice_type_desc;
	}
	public String getDrawer() {
		return drawer;
	}
	public void setDrawer(String drawer) {
		this.drawer = drawer;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
}
