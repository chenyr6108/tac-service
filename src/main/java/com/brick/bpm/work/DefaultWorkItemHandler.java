package com.brick.bpm.work;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class DefaultWorkItemHandler implements WorkItemHandler {

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) throws Exception {
		try {
			manager.activeWorkItem(workItem.getFlowId());
			manager.completeWorkItem(workItem.getFlowId(), null);
		} catch (Exception ex) {
			manager.abortWorkItem(workItem.getFlowId());
			throw ex;
		}
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) throws Exception {
		manager.abortWorkItem(workItem.getFlowId());
	}

}