package com.brick.support.shopping.to;

import com.brick.base.to.BaseTo;
import com.brick.util.StringUtils;

public class ShoppingCartTO extends BaseTo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String user_id;
	private String items = "";
	private Integer item_type = 1;
	private Integer status;
	private Double items_money;
	private int item_count;
	private int order_type;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public Integer getItem_type() {
		return item_type;
	}
	public void setItem_type(Integer item_type) {
		this.item_type = item_type;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public int getItem_count() {
		if (StringUtils.isEmpty(items)) {
			item_count = 0;
		} else {
			item_count = items.split(",").length;
		}
		return item_count;
	}
	public void setItem_count(Integer item_count) {
		this.item_count = item_count;
	}
	public String getItems() {
		return items;
	}
	public void setItems(String items) {
		this.items = items;
	}
	public Double getItems_money() {
		return items_money;
	}
	public void setItems_money(Double items_money) {
		this.items_money = items_money;
	}
	public int getOrder_type() {
		return order_type;
	}
	public void setOrder_type(int order_type) {
		this.order_type = order_type;
	}
}
