package com.brick.bpm.filter;

/**
 * 流程结点定义查询条件
 * @author zhangyizhou
 *
 */
public class FlowDefFilter extends ProcessDefFilter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5266469806632827425L;
	
	private String flowDefId;

	public String getFlowDefId() {
		return flowDefId;
	}

	public void setFlowDefId(String flowDefId) {
		this.flowDefId = flowDefId;
	}

}
