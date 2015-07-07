package com.brick.bpm.work;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.brick.bpm.service.InstanceService;

public class StartEventWorkItemHandler implements WorkItemHandler {

	private InstanceService bpmInstanceService;
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager)
			throws Exception {
		bpmInstanceService.startProcess(workItem.getProcessId());
		manager.activeWorkItem(workItem.getFlowId());
		manager.completeWorkItem(workItem.getFlowId(), null);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager)
			throws Exception {
		manager.abortWorkItem(workItem.getFlowId());
	}

	public void setBpmInstanceService(InstanceService bpmInstanceService) {
		this.bpmInstanceService = bpmInstanceService;
	}

}
