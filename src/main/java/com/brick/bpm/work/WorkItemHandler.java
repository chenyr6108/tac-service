package com.brick.bpm.work;

public interface WorkItemHandler {

	/**
	 * 执行
	 * @param workItem
	 * @param manager
	 */
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) throws Exception;

	/**
	 * 忽略
	 * @param workItem
	 * @param manager
	 */
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) throws Exception;
    
}
