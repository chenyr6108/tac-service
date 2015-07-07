package com.brick.bpm.filter;

/**
 * 流程路径定义查询条件
 * @author zhangyizhou
 *
 */
public class SequenceDefFilter extends ProcessDefFilter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String sequenceDefId;

	public String getSequenceDefId() {
		return sequenceDefId;
	}

	public void setSequenceDefId(String sequenceDefId) {
		this.sequenceDefId = sequenceDefId;
	}
	
}
