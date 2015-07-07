package com.brick.bpm.work;

import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.brick.bpm.ins.FlowInstance;
import com.brick.bpm.ins.SequenceInstance;
import com.brick.bpm.service.InstanceService;
import com.brick.bpm.util.BpmConst;
import com.brick.bpm.util.ElUtil;

public class GatewayWorkItemHandler implements WorkItemHandler {

	private InstanceService bpmInstanceService;
	
	private ElUtil elUtil;
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) throws Exception {

		bpmInstanceService.activeFlow(workItem.getFlowId());
		bpmInstanceService.completeFlow(workItem.getFlowId());
		
		FlowInstance currentFlow = this.bpmInstanceService.getFlowById(workItem.getFlowId());
		List<SequenceInstance> outGoings = bpmInstanceService.getOutGoings(workItem.getFlowId());
		
		//分支类关卡处理
		if(BpmConst.DIRECTION_DIVERGING.equals(currentFlow.getDirection())) {
			for (SequenceInstance outGoing : outGoings) {
				if(outGoing.getCondition() == null || "true".equals(elUtil.evaluate(workItem.getVariables(),outGoing.getCondition()))) {
					//获取下一步执行器
					FlowInstance targetFlow = bpmInstanceService.getFlowById(outGoing.getTargetId());		
					manager.executeWorkItem(targetFlow.getFlowId());
				}
			}
		}
		
		//汇合类网关处理
		if(BpmConst.DIRECTION_CONVERGING.equals(currentFlow.getDirection())) {
			if(outGoings.size() != 1) {
				new Exception("汇合类网关只能有一个出口!");	
			}
			FlowInstance targetFlow = bpmInstanceService.getFlowById(outGoings.get(0).getTargetId());		
			manager.executeWorkItem(targetFlow.getFlowId());
		}
		
		
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) throws Exception {
		manager.abortWorkItem(workItem.getId());
		
	}

	public void setBpmInstanceService(InstanceService bpmInstanceService) {
		this.bpmInstanceService = bpmInstanceService;
	}

	public void setElUtil(ElUtil elUtil) {
		this.elUtil = elUtil;
	}
	
}
