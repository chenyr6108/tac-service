package com.brick.supplier.to;

import java.io.Serializable;

import com.brick.base.to.BaseTo;

/**
 * 供应商信息修改日记信息
 * @author yangliu
 *
 */
public class SupplierLogMsgTo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 修改字段中文名
	 */
	private String name;
	
	/**
	 * 修改前后信息
	 */
	private String[] msgs = new String[2];

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String[] getMsgs() {
		return msgs;
	}

	public void setMsgs(String[] msgs) {
		this.msgs = msgs;
	}

}
