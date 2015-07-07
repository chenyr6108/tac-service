package com.brick.bpm.filter;

import java.io.Serializable;

/**
 * 任务查询条件
 * @author zhangyizhou
 *
 */
public class TaskFilter implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public static final String SORT_TASKID = "sort";
	
	public static final String SORT_PRIORITY = "priority";
	
	public static final String SORT_ENDDATE = "endDate";
	
	private Integer taskId;
	
	private Integer taskStatus;
	
	private Integer flowId;
	
	private Integer processId;
	
	private String userId;
	
	private String sort;

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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(Integer taskStatus) {
		this.taskStatus = taskStatus;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

}
