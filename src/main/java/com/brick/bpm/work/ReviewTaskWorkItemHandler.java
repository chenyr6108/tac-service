package com.brick.bpm.work;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.brick.bpm.service.InstanceService;
import com.brick.bpm.service.TaskService;

public class ReviewTaskWorkItemHandler implements WorkItemHandler {
	
	private InstanceService bpmInstanceService;
	
	private TaskService taskService;
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) throws Exception {
		this.bpmInstanceService.activeFlow(workItem.getFlowId());
		
		this.taskService.startTaskByFlowId(workItem.getFlowId());

		this.bpmInstanceService.pendFlow(workItem.getFlowId());
		this.bpmInstanceService.pendProcess(workItem.getProcessId());
	}
	

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) throws Exception {
		manager.abortWorkItem(workItem.getId());
		
	}

	public void setBpmInstanceService(InstanceService bpmInstanceService) {
		this.bpmInstanceService = bpmInstanceService;
	}

	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}


	public TaskService getTaskService() {
		return taskService;
	}
	
}
