package com.brick.bpm.work;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.brick.bpm.def.FlowObject;
import com.brick.bpm.def.Process;


/**
 * 工作流处理资源对象(数据传输包)
 * @author zhangyizhou
 *
 */
public class WorkItem implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer flowId;
	
	private Integer processId;
	
	private Process processDefinition;
	
	private Map<String, Object> processVariables = new HashMap<String, Object>();
	
	private Map<String, Object> flowVariables = new HashMap<String, Object>();
	
	private FlowObject currentFlowObject;
	
	public int getId() {
		return flowId;
	}

	public void setId(int id) {
		this.flowId = id;
	}

	public Integer getFlowId() {
		return flowId;
	}

	public void setFlowId(Integer flowId) {
		this.flowId = flowId;
	}

	public Integer getProcessId() {
		return processId;
	}

	public void setProcessId(Integer processId) {
		this.processId = processId;
	}

	public Process getProcessDefinition() {
		return processDefinition;
	}

	public void setProcessDefinition(Process processDefinition) {
		this.processDefinition = processDefinition;
	}

	public Map<String, Object> getVariables() {
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.putAll(processVariables);
		variables.putAll(flowVariables);
		return variables;
	}

	public Map<String, Object> getProcessVariables() {
		return processVariables;
	}

	public void setProcessVariables(Map<String, Object> processVariables) {
		this.processVariables = processVariables;
	}

	public Map<String, Object> getFlowVariables() {
		return flowVariables;
	}

	public void setFlowVariables(Map<String, Object> flowVariables) {
		this.flowVariables = flowVariables;
	}

	public FlowObject getCurrentFlowObject() {
		return currentFlowObject;
	}

	public void setCurrentFlowObject(FlowObject currentFlowObject) {
		this.currentFlowObject = currentFlowObject;
	}
	
}
