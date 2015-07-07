package com.brick.support.shopping.to;

import com.brick.base.to.BaseTo;

public class PayOrderTO extends BaseTo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String order_code;
	private Integer status;
	private Double order_money;
	private Integer order_type;
	private String order_link;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOrder_code() {
		return order_code;
	}
	public void setOrder_code(String order_code) {
		this.order_code = order_code;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Double getOrder_money() {
		return order_money;
	}
	public void setOrder_money(Double order_money) {
		this.order_money = order_money;
	}
	public Integer getOrder_type() {
		return order_type;
	}
	public void setOrder_type(Integer order_type) {
		this.order_type = order_type;
	}
	public String getOrder_link() {
		return order_link;
	}
	public void setOrder_link(String order_link) {
		this.order_link = order_link;
	}
	
}
