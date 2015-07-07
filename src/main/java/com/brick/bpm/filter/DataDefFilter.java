package com.brick.bpm.filter;

/**
 * 流程数据定义查询条件
 */
public class DataDefFilter extends ProcessDefFilter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String dataDefId;

	public String getDataDefId() {
		return dataDefId;
	}

	public void setDataDefId(String dataDefId) {
		this.dataDefId = dataDefId;
	}

}
