package com.brick.bpm.runtime;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.brick.bpm.dao.InstanceDao;
import com.brick.bpm.def.FlowObject;
import com.brick.bpm.filter.FlowFilter;
import com.brick.bpm.ins.FlowInstance;
import com.brick.bpm.work.WorkItemManager;

public class TaskService {
	
	private WorkItemManager workItemManager;
	
	private InstanceDao bpmInstanceDao;
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void startTask(Integer taskId) throws Exception {
		this.workItemManager.activeWorkItem(taskId);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void completeTask(Integer taskId,Map<String, Object> data,String userId) throws Exception {
		this.workItemManager.completeWorkItem(taskId, data);
	}
	
	public List<FlowInstance> findTask() {
		FlowFilter filter = new FlowFilter();
		filter.setFlowClass(FlowObject.FLOWCLASS_ACTIVITY);
		filter.setFlowStatus(FlowInstance.STATE_PENDING);
		return bpmInstanceDao.selectFlowInstance(filter);
	}

	public void setWorkItemManager(WorkItemManager workItemManager) {
		this.workItemManager = workItemManager;
	}

	public void setBpmInstanceDao(InstanceDao bpmInstanceDao) {
		this.bpmInstanceDao = bpmInstanceDao;
	}


}
