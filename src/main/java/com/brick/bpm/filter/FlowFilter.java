package com.brick.bpm.filter;

public class FlowFilter extends ProcessFilter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer flowId;
	
	private String flowClass;
	
	private String flowDefId;
	
	private Integer flowStatus;

	public Integer getFlowId() {
		return flowId;
	}

	public void setFlowId(Integer flowId) {
		this.flowId = flowId;
	}

	public String getFlowDefId() {
		return flowDefId;
	}

	public void setFlowDefId(String flowDefId) {
		this.flowDefId = flowDefId;
	}

	public String getFlowClass() {
		return flowClass;
	}

	public void setFlowClass(String flowClass) {
		this.flowClass = flowClass;
	}

	public Integer getFlowStatus() {
		return flowStatus;
	}

	public void setFlowStatus(Integer flowStatus) {
		this.flowStatus = flowStatus;
	}

}
