package com.brick.bpm.ins;

import java.io.Serializable;
import java.util.Date;

/**
 * 任务实例
 * @author zhangyizhou
 *
 */
public class Task implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 任务代号
	 */
	private Integer taskId;
	
	private Integer taskStatus;
	/**
	 * 流程结点
	 */
	private Integer flowId;
	
	private String flowDefId;
	
	private String flowName;
	
	/**
	 * 流程
	 */
	private Integer processId;
	
	/**
	 * 处理链接
	 */
	private String url;
	
	/**
	 * 任务描述
	 */
	private String description;
	
	/**
	 * 负责人
	 */
	private String charge;
	
	private String chargeName;
	
	/**
	 * 代理人
	 */
	private String delegate;
	
	private String delegateName;
	
	/**
	 * 处理人
	 */
	private String operator;
	
	private String operatorName;
	
	/**
	 * 预定时间
	 */
	private Date dueDate;
	
	/**
	 * 开始时间
	 */
	private Date beginDate;
	
	/**
	 * 结束时间
	 */
	private Date endDate;
	
	/**
	 * 处理结果
	 */
	private String result;
	
	/**
	 * 任务备注
	 */
	private String comment;
	
	/**
	 * 优先级
	 */
	private Integer priority;
	
	/**
	 * 操作列表
	 */
	private String operateList;

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public Integer getFlowId() {
		return flowId;
	}

	public void setFlowId(Integer flowId) {
		this.flowId = flowId;
	}

	public Integer getProcessId() {
		return processId;
	}

	public void setProcessId(Integer processId) {
		this.processId = processId;
	}

	public String getCharge() {
		return charge;
	}

	public void setCharge(String charge) {
		this.charge = charge;
	}

	public String getDelegate() {
		return delegate;
	}

	public void setDelegate(String delegate) {
		this.delegate = delegate;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
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

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(Integer taskStatus) {
		this.taskStatus = taskStatus;
	}

	public String getOperateList() {
		return operateList;
	}

	public void setOperateList(String operateList) {
		this.operateList = operateList;
	}

	public String getChargeName() {
		return chargeName;
	}

	public void setChargeName(String chargeName) {
		this.chargeName = chargeName;
	}

	public String getDelegateName() {
		return delegateName;
	}

	public void setDelegateName(String delegateName) {
		this.delegateName = delegateName;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
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
	
}
