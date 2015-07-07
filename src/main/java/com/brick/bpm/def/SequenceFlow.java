package com.brick.bpm.def;

import java.io.Serializable;

/**
 * 顺序流定义
 * @author zhangyizhou
 *
 */
public class SequenceFlow implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 流程
	 */
	private String processDefId;
	
	/**
	 * 
	 */
	private String sequenceDefId;
	
	/**
	 * 
	 */
	private String sequenceName;
	
	/**
	 * 定义来源
	 */
	private String source;
	
	/**
	 * 定义去向
	 */
	private String target;
	
	/**
	 * 定义执行条件
	 */
	private Object condition;
	
	private Integer isDefault;

	public String getProcessDefId() {
		return processDefId;
	}

	public void setProcessDefId(String processDefId) {
		this.processDefId = processDefId;
	}

	public String getSequenceDefId() {
		return sequenceDefId;
	}

	public void setSequenceDefId(String sequenceDefId) {
		this.sequenceDefId = sequenceDefId;
	}

	public String getSequenceName() {
		return sequenceName;
	}

	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public Object getCondition() {
		return condition;
	}

	public void setCondition(Object condition) {
		this.condition = condition;
	}

	public Integer getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Integer isDefault) {
		this.isDefault = isDefault;
	}

}
