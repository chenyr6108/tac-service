package com.brick.bpm.work;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.brick.bpm.ins.FlowInstance;
import com.brick.bpm.ins.ProcessInstance;
import com.brick.bpm.ins.SequenceInstance;
import com.brick.bpm.service.InstanceService;

public class WorkItemManager {
	
	private InstanceService bpmInstanceService;
	
	private Map<String, WorkItemHandler> handlerMap = new HashMap<String, WorkItemHandler>();
	
	private StartEventWorkItemHandler startEventWorkItemHandler;
	
	private GatewayWorkItemHandler gatewayWorkItemHandler;
	
	private EndEventWorkItemHandler endEventWorkItemHandler;
	
	private ActivityWorkItemHandler activityWorkItemHandler;
	
	
	public void registerWorkItemHandler(String name, WorkItemHandler workItemHandler) {
		handlerMap.put(name, workItemHandler);
	}
	
	public void init() {
		this.registerWorkItemHandler("R", startEventWorkItemHandler);
		this.registerWorkItemHandler("G", gatewayWorkItemHandler);
		this.registerWorkItemHandler("E", endEventWorkItemHandler);
		this.registerWorkItemHandler("A", activityWorkItemHandler);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public WorkItemHandler getWorkItemHandler(String name) {
		WorkItemHandler handler = handlerMap.get(name);
		if (handler == null) {
			handler = new DefaultWorkItemHandler();
		}
		return handler;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void activeWorkItem(int workItemId) throws Exception {
		//激活流程结点
		FlowInstance flow = bpmInstanceService.getFlowById(workItemId);
		ProcessInstance process = bpmInstanceService.getProcessById(flow.getProcessId());
		//若流程为准备或待处理状态先激活流程
		if(ProcessInstance.STATE_READY.equals(process.getStatus()) || ProcessInstance.STATE_PENDING.equals(process.getStatus())) {
			bpmInstanceService.activeProcess(flow.getProcessId());
		}
		bpmInstanceService.activeFlow(flow.getFlowId());
		
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void completeWorkItem(int workItemId,Map<String,Object> variables) throws Exception {
		//完成当前节点
		bpmInstanceService.completeFlow(workItemId);
		//开启之后节点
		for(SequenceInstance sequence : bpmInstanceService.getOutGoings(workItemId)) {
			//参数提交
			bpmInstanceService.commitData(sequence.getSequenceId(), variables);
			//获取下一步执行器并执行
			executeWorkItem(sequence.getTargetId());
		}
	}
	
	public Map<String, Object> getFlowVariables(int flowId) throws Exception {
		FlowInstance targetFlow = bpmInstanceService.getFlowById(flowId);
		WorkItemHandler handler = getWorkItemHandler(targetFlow.getFlowClass());
		WorkItem workItem = new WorkItem();
		
		Map<String, Object> processVariables = new HashMap<String, Object>();
		processVariables.putAll(bpmInstanceService.getProcessData(targetFlow.getProcessId()));
		workItem.setProcessVariables(processVariables);
		
		//TODO:多重入口未处理
		List<SequenceInstance> ingoings = bpmInstanceService.getInGoings(targetFlow.getFlowId());
		Map<String, Object> flowVariables = new HashMap<String, Object>();
		for (SequenceInstance ingoing : ingoings) {
			flowVariables.putAll(bpmInstanceService.getFlowData(ingoing.getSequenceId()));
		}
		flowVariables.putAll(processVariables);
		return flowVariables;
	}
	
	public void executeWorkItem(int targetId) throws Exception {
		FlowInstance targetFlow = bpmInstanceService.getFlowById(targetId);
		WorkItemHandler handler = getWorkItemHandler(targetFlow.getFlowClass());
		WorkItem workItem = new WorkItem();
		
		Map<String, Object> processVariables = new HashMap<String, Object>();
		processVariables.putAll(bpmInstanceService.getProcessData(targetFlow.getProcessId()));
		workItem.setProcessVariables(processVariables);
		
		//TODO:多重入口未处理
		List<SequenceInstance> ingoings = bpmInstanceService.getInGoings(targetFlow.getFlowId());
		Map<String, Object> flowVariables = new HashMap<String, Object>();
		for (SequenceInstance ingoing : ingoings) {
			flowVariables.putAll(bpmInstanceService.getFlowData(ingoing.getSequenceId()));
		}
		workItem.setFlowVariables(flowVariables);
		
		BeanUtils.copyProperties(workItem, targetFlow);
		handler.executeWorkItem(workItem, this);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void abortWorkItem(int workItemId) throws Exception {
		//节点处理失败
		bpmInstanceService.abortFlow(workItemId);
	}

	public void setBpmInstanceService(InstanceService bpmInstanceService) {
		this.bpmInstanceService = bpmInstanceService;
	}

	public void setStartEventWorkItemHandler(
			StartEventWorkItemHandler startEventWorkItemHandler) {
		this.startEventWorkItemHandler = startEventWorkItemHandler;
	}

	public void setGatewayWorkItemHandler(
			GatewayWorkItemHandler gatewayWorkItemHandler) {
		this.gatewayWorkItemHandler = gatewayWorkItemHandler;
	}

	public void setEndEventWorkItemHandler(
			EndEventWorkItemHandler endEventWorkItemHandler) {
		this.endEventWorkItemHandler = endEventWorkItemHandler;
	}

	public void setActivityWorkItemHandler(
			ActivityWorkItemHandler activityWorkItemHandler) {
		this.activityWorkItemHandler = activityWorkItemHandler;
	}
	
	
	
}
