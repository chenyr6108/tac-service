package com.brick.bpm.runtime;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;
import com.brick.bpm.service.InstanceService;
import com.brick.bpm.work.WorkItemManager;

public class RuntimeService {
	
	private InstanceService bpmInstanceService;
	private WorkItemManager workItemManager;
	
	
	/**
	 * 执行流程
	 * @param processDefId
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor=Exception.class)
	public Integer startProcess(String processDefId) throws Exception {
		//创建流程实例
		Integer processInstanceId = bpmInstanceService.createProcessInstance(processDefId);
		//获取开始节点
		Integer startEventId = bpmInstanceService.getStartEvent(processInstanceId);
		//workItemManager.activeWorkItem(startEvent.get(0).getFlowId());
		//workItemManager.completeWorkItem(startEvent.get(0).getFlowId(), null);
		//执行开始节点
		workItemManager.executeWorkItem(startEventId);
		//返回流程代号
		return processInstanceId;
	}
	
	/**
	 * 执行流程
	 * @param processDefId
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor=Exception.class)
	public Integer startProcess(String processDefId,Map<String,Object> variables) throws Exception {
		//创建流程实例
		Integer processInstanceId = bpmInstanceService.createProcessInstance(processDefId);
		
		for (String key : variables.keySet()) {
			if(variables.get(key) == null || variables.get(key).toString().isEmpty()) {
				continue;
			}
			bpmInstanceService.updateProcessData(processInstanceId, key, variables.get(key).toString());
		}
		//获取开始节点
		Integer startEventId = bpmInstanceService.getStartEvent(processInstanceId);
		//workItemManager.activeWorkItem(startEvent.get(0).getFlowId());
		//workItemManager.completeWorkItem(startEvent.get(0).getFlowId(), null);
		//执行开始节点
		workItemManager.executeWorkItem(startEventId);
		//返回流程代号
		return processInstanceId;
	}

	public void setBpmInstanceService(InstanceService bpmInstanceService) {
		this.bpmInstanceService = bpmInstanceService;
	}

	public void setWorkItemManager(WorkItemManager workItemManager) {
		this.workItemManager = workItemManager;
	}
	
	
}
