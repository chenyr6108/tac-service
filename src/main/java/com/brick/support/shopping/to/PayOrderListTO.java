package com.brick.support.shopping.to;

import com.brick.base.to.BaseTo;

public class PayOrderListTO extends BaseTo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String order_id;
	private Integer status;
	private String item_id;
	private Integer item_type;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getItem_id() {
		return item_id;
	}
	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}
	public Integer getItem_type() {
		return item_type;
	}
	public void setItem_type(Integer item_type) {
		this.item_type = item_type;
	}
	
	
}
