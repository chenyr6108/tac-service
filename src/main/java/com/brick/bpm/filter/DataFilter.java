package com.brick.bpm.filter;

public class DataFilter extends ProcessFilter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer dataId;
	
	private String dataDefId;
	
	private Integer sequenceId;

	private Integer scope;
	
	public Integer getDataId() {
		return dataId;
	}

	public void setDataId(Integer dataId) {
		this.dataId = dataId;
	}

	public String getDataDefId() {
		return dataDefId;
	}

	public void setDataDefId(String dataDefId) {
		this.dataDefId = dataDefId;
	}

	public Integer getSequenceId() {
		return sequenceId;
	}

	public void setSequenceId(Integer sequenceId) {
		this.sequenceId = sequenceId;
	}

	public Integer getScope() {
		return scope;
	}

	public void setScope(Integer scope) {
		this.scope = scope;
	}
	
}
