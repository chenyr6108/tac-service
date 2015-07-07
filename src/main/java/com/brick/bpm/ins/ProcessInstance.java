package com.brick.bpm.ins;

import java.io.Serializable;
import java.util.Date;

/**
 * 流程实例
 * @author zhangyizhou
 *
 */
public class ProcessInstance implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 流程准备标识
	 */
	public static final Integer STATE_READY = -1;
	
	/**
	 * 流程失败标识
	 */
	public static final Integer STATE_ABORTED = 0;
	
	/**
	 * 流程激活标识
	 */
	public static final Integer STATE_ACTIVE = 1;
	
	/**
	 * 流程成功标识
	 */
	public static final Integer STATE_COMPLETED = 2;
	
	/**
	 * 流程待处理标识
	 */
	public static final Integer STATE_PENDING = 3;
	
	/**
	 * 流程暂停标识
	 */
	public static final Integer STATE_SUSPENDING = 4;
	
	/**
	 * 流程实例ID
	 */
	private int processId;
	
	/**
	 * 流程定义ID
	 */
	private String processDefId;

	/**
	 * 流程名称
	 */
	private String processName;
	
	/**
	 * 流程状态
	 */
	private Integer status;
	
	/**
	 * 流程结点状态
	 */
	private Integer flowStatus;
	
	private String flowStatusName;
	
	private String currentCharge;
	
	private String currentChargeName;
	
	private String currentDelegate;
	
	private String currentDelegateName;
	
	/**
	 * 开始时间
	 */
	private Date beginDate;
	
	/**
	 * 结束时间
	 */
	private Date endDate;
	
	/**
	 * 优先级
	 */
	private int priority;
	
	public int getProcessId() {
		return processId;
	}

	public void setProcessId(int processId) {
		this.processId = processId;
	}

	public String getProcessDefId() {
		return processDefId;
	}

	public void setProcessDefId(String processDefId) {
		this.processDefId = processDefId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Integer getFlowStatus() {
		return flowStatus;
	}

	public void setFlowStatus(Integer flowStatus) {
		this.flowStatus = flowStatus;
	}

	public String getFlowStatusName() {
		return flowStatusName;
	}

	public void setFlowStatusName(String flowStatusName) {
		this.flowStatusName = flowStatusName;
	}

	public String getCurrentCharge() {
		return currentCharge;
	}

	public void setCurrentCharge(String currentCharge) {
		this.currentCharge = currentCharge;
	}

	public String getCurrentChargeName() {
		return currentChargeName;
	}

	public void setCurrentChargeName(String currentChargeName) {
		this.currentChargeName = currentChargeName;
	}

	public String getCurrentDelegate() {
		return currentDelegate;
	}

	public void setCurrentDelegate(String currentDelegate) {
		this.currentDelegate = currentDelegate;
	}

	public String getCurrentDelegateName() {
		return currentDelegateName;
	}

	public void setCurrentDelegateName(String currentDelegateName) {
		this.currentDelegateName = currentDelegateName;
	}
	
}
