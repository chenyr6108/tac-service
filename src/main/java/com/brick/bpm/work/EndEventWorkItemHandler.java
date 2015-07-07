package com.brick.bpm.work;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.brick.bpm.service.InstanceService;

public class EndEventWorkItemHandler implements WorkItemHandler {
	
	private InstanceService bpmInstanceService;
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) throws Exception {
		bpmInstanceService.activeFlow(workItem.getFlowId());
		//TODO :test
		//完成结点
		bpmInstanceService.completeFlow(workItem.getFlowId());
		//完成流程
		bpmInstanceService.completeProcess(workItem.getProcessId());
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) throws Exception {
		manager.abortWorkItem(workItem.getId());
		
	}

	public void setBpmInstanceService(InstanceService bpmInstanceService) {
		this.bpmInstanceService = bpmInstanceService;
	}

}
