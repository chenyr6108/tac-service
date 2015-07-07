package com.brick.bpm.def;

import java.io.Serializable;

/**
 * 流程对象定义
 * @author zhangyizhou
 *
 */
public class FlowObject implements Serializable {
	
	public static final String FLOWCLASS_STARTEVENT = "R";
	
	public static final String FLOWCLASS_ENDEVENT = "E";
	
	public static final String FLOWCLASS_ACTIVITY = "A";
	
	public static final String FLOWCLASS_GATEWAY = "G";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 流程
	 */
	private String processDefId;
	
	/**
	 * 标识
	 */
	private String flowDefId;
	
	/**
	 * 标识(存储用)
	 */
	private Integer flowDefCode;
	
	/**
	 * 显示名
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
	 * 多实例
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

	public Integer getFlowType() {
		return flowType;
	}

	public void setFlowType(Integer flowType) {
		this.flowType = flowType;
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
