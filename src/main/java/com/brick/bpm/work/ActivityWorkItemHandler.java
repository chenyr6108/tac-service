package com.brick.bpm.work;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.brick.bpm.ins.FlowInstance;
import com.brick.bpm.service.InstanceService;
import com.brick.bpm.service.TaskService;
import com.brick.bpm.util.ElUtil;

public class ActivityWorkItemHandler implements WorkItemHandler {
	
	private InstanceService bpmInstanceService;
	
	private TaskService bpmTaskService;
	
	private ElUtil elUtil;
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) throws Exception {
		
		FlowInstance flow = this.bpmInstanceService.getFlowById(workItem.getFlowId());
		String userList = this.elUtil.evaluate(workItem.getVariables(), flow.getUserList());
		this.bpmInstanceService.updateUserList(workItem.getFlowId(),userList);
		
		this.bpmInstanceService.activeFlow(workItem.getFlowId());
		
		this.bpmTaskService.startTaskByFlowId(workItem.getFlowId());
		
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

	public void setBpmTaskService(TaskService bpmTaskService) {
		this.bpmTaskService = bpmTaskService;
	}


	public void setElUtil(ElUtil elUtil) {
		this.elUtil = elUtil;
	}
	
}
