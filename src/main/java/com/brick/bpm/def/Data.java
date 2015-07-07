package com.brick.bpm.def;

import java.io.Serializable;

/**
 * 流程数据
 * @author zhangyizhou
 *
 */
public class Data implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
	
	private String sequenceDefId;
	
	private String initValue;

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

	public String getInitValue() {
		return initValue;
	}

	public void setInitValue(String initValue) {
		this.initValue = initValue;
	}
	
	
}
