package com.brick.bpm.ins;

import java.io.Serializable;

/**
 * 流程数据实例
 * @author zhangyizhou
 *
 */
public class DataInstance implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer processId;
	
	private Integer dataId;
	
	private String processDefId;
	
	/**
	 * 数据id
	 */
	private String dataDefId;
	
	/**
	 * 数据显示名
	 */
	private String dataName;
	
	/**
	 * 数据类型
	 */
	private String dataType;
	
	private int scope;
	
	private String source;
	
	private String target;
	
	private Integer sequenceId;
	
	private String sequenceDefId;
	
	private String value;

	public Integer getProcessId() {
		return processId;
	}

	public void setProcessId(Integer processId) {
		this.processId = processId;
	}

	public Integer getDataId() {
		return dataId;
	}

	public void setDataId(Integer dataId) {
		this.dataId = dataId;
	}

	public String getProcessDefId() {
		return processDefId;
	}

	public void setProcessDefId(String processDefId) {
		this.processDefId = processDefId;
	}

	public String getDataDefId() {
		return dataDefId;
	}

	public void setDataDefId(String dataDefId) {
		this.dataDefId = dataDefId;
	}

	public String getDataName() {
		return dataName;
	}

	public void setDataName(String dataName) {
		this.dataName = dataName;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
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

	public int getScope() {
		return scope;
	}

	public void setScope(int scope) {
		this.scope = scope;
	}

	public String getSequenceDefId() {
		return sequenceDefId;
	}

	public void setSequenceDefId(String sequenceDefId) {
		this.sequenceDefId = sequenceDefId;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getSequenceId() {
		return sequenceId;
	}

	public void setSequenceId(Integer sequenceId) {
		this.sequenceId = sequenceId;
	}
	
}
