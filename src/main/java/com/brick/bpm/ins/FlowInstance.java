package com.brick.bpm.ins;

import java.io.Serializable;

/**
 * 流程对象定义
 * @author zhangyizhou
 *
 */
public class FlowInstance implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 流程结点等待标识
	 */
	public static final Integer STATE_READY = -1;
	
	/**
	 * 流程结点失败标识
	 */
	public static final Integer STATE_ABORTED = 0;
	
	/**
	 * 流程结点激活标识
	 */
	public static final Integer STATE_ACTIVE = 1;
	
	/**
	 * 流程结点成功标识
	 */
	public static final Integer STATE_COMPLETED = 2;
	
	/**
	 * 流程结点待处理标识
	 */
	public static final Integer STATE_PENDING = 3;
	
	/**
	 * 流程结点暂停标识
	 */
	public static final Integer STATE_SUSPENDING = 4;
	
	/**
	 * 流程结点实例ID
	 */
	private int flowId;
	
	/**
	 * 流程实例ID
	 */
	private int processId;
	
	/**
	 * 流程结点实例状态
	 */
	private int flowStatus;
	
	/**
	 * 流程定义ID
	 */
	private String processDefId;

	/**
	 * 流程对象ID
	 */
	private String flowDefId;
	
	/**
	 * 标识(存储用)
	 */
	private Integer flowDefCode;
	
	/**
	 * 流程对象名称
	 */
	private String flowName;
	
	/**
	 * 流程对象类型
	 */
	private Integer flowType;
	
	/**
	 * 流程对象类型
	 */
	private String flowClass;
	
	/**
	 * 多重实例
	 */
	private String multiInstance;
	
	/**
	 * 关卡方向
	 */
	private String direction;
	
	/**
	 * 用户列表
	 */
	private String userList;
	
	/**
	 * 操作列表
	 */
	private String operateList;

	public int getFlowId() {
		return flowId;
	}

	public void setFlowId(int flowId) {
		this.flowId = flowId;
	}

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

	public String getFlowDefId() {
		return flowDefId;
	}

	public void setFlowDefId(String flowDefId) {
		this.flowDefId = flowDefId;
	}

	public String getFlowName() {
		return flowName;
	}

	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}

	public String getFlowClass() {
		return flowClass;
	}

	public void setFlowClass(String flowClass) {
		this.flowClass = flowClass;
	}

	public String getMultiInstance() {
		return multiInstance;
	}

	public void setMultiInstance(String multiInstance) {
		this.multiInstance = multiInstance;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public Integer getFlowType() {
		return flowType;
	}

	public void setFlowType(Integer flowType) {
		this.flowType = flowType;
	}

	public int getFlowStatus() {
		return flowStatus;
	}

	public void setFlowStatus(int flowStatus) {
		this.flowStatus = flowStatus;
	}

	public String getUserList() {
		return userList;
	}

	public void setUserList(String userList) {
		this.userList = userList;
	}

	public Integer getFlowDefCode() {
		return flowDefCode;
	}

	public void setFlowDefCode(Integer flowDefCode) {
		this.flowDefCode = flowDefCode;
	}

	public String getOperateList() {
		return operateList;
	}

	public void setOperateList(String operateList) {
		this.operateList = operateList;
	}
	
}
