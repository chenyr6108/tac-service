package com.brick.bpm.def;

import java.io.Serializable;
/**
 * 流程定义
 * @author zhangyizhou
 *
 */
public class Process implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 流程id
	 */
	private String processDefId;
	
	/**
	 * 流程类型
	 */
	private String processType;
	
	/**
	 * 流程显示名
	 */
	private String processName;
	
	/**
	 * 状态
	 */
	private int status;
	
	/**
	 * 开始结点
	 */
	private String startEvent;

	public String getProcessDefId() {
		return processDefId;
	}

	public void setProcessDefId(String processDefId) {
		this.processDefId = processDefId;
	}

	public String getProcessType() {
		return processType;
	}

	public void setProcessType(String processType) {
		this.processType = processType;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStartEvent() {
		return startEvent;
	}

	public void setStartEvent(String startEvent) {
		this.startEvent = startEvent;
	}

}
