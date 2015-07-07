package com.brick.bpm.ins;

import java.io.Serializable;

public class SequenceInstance implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private Integer processId;
	
	/**
	 * 
	 */
	private Integer sequenceId;
	
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
	
	private Integer sourceId;
	
	/**
	 * 定义去向
	 */
	private String target;
	
	private Integer targetId;
	/**
	 * 定义执行条件
	 */
	private String condition;
	
	private Integer isDefault;
	
	public Integer getProcessId() {
		return processId;
	}

	public void setProcessId(Integer processId) {
		this.processId = processId;
	}

	public Integer getSequenceId() {
		return sequenceId;
	}

	public void setSequenceId(Integer sequenceId) {
		this.sequenceId = sequenceId;
	}

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

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public Integer getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Integer isDefault) {
		this.isDefault = isDefault;
	}

	public Integer getSourceId() {
		return sourceId;
	}

	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}

	public Integer getTargetId() {
		return targetId;
	}

	public void setTargetId(Integer targetId) {
		this.targetId = targetId;
	}
}
