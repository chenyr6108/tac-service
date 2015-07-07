package com.brick.bpm.dao;

import java.util.List;

import com.brick.base.dao.BaseDAO;
import com.brick.bpm.filter.DataFilter;
import com.brick.bpm.filter.FlowFilter;
import com.brick.bpm.filter.ProcessFilter;
import com.brick.bpm.filter.SequenceFilter;
import com.brick.bpm.ins.DataInstance;
import com.brick.bpm.ins.FlowInstance;
import com.brick.bpm.ins.ProcessInstance;
import com.brick.bpm.ins.SequenceInstance;

public class InstanceDao extends BaseDAO {
	
	public Integer insertProcessInstance(ProcessInstance process) {
		return (Integer)super.getSqlMapClientTemplate().insert("bpmIns.insertProcessInstance", process);
	}
	
	public Integer updateProcessInstance(ProcessInstance process) {
		return (Integer)super.getSqlMapClientTemplate().update("bpmIns.updateProcessInstance", process);
	}
	
	@SuppressWarnings("unchecked")
	public List<ProcessInstance> selectProcessInstance(ProcessFilter filter) {
		return (List<ProcessInstance>)super.getSqlMapClientTemplate().queryForList("bpmIns.selectProcessInstance", filter);
	}
	
	public ProcessInstance selectProcessInstance(Integer processId) {
		ProcessFilter filter = new ProcessFilter();
		filter.setProcessId(processId);
		List<ProcessInstance> list = this.selectProcessInstance(filter);
		if(!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	public Integer insertFlowInstance(FlowInstance flow) {
		return (Integer)super.getSqlMapClientTemplate().insert("bpmIns.insertFlowInstance", flow);
	}
	
	public Integer updateFlowInstance(FlowInstance flow) {
		return super.getSqlMapClientTemplate().update("bpmIns.updateFlowInstance", flow);
	}
	
	@SuppressWarnings("unchecked")
	public List<FlowInstance> selectFlowInstance(FlowFilter filter) {
		try {
			return (List<FlowInstance>)super.getSqlMapClientTemplate().queryForList("bpmIns.selectFlowInstance", filter);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		
	}
	
	public FlowInstance selectFlowInstance(Integer flowId) {
		FlowFilter filter = new FlowFilter();
		filter.setFlowId(flowId);
		List<FlowInstance> list = this.selectFlowInstance(filter);
		if(!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	public Integer insertSequenceInstance(SequenceInstance sequence) {
		return (Integer)super.getSqlMapClientTemplate().insert("bpmIns.insertSequenceInstance", sequence);
	}
	
	@SuppressWarnings("unchecked")
	public List<SequenceInstance> selectSequenceInstance(SequenceFilter filter) {
		return (List<SequenceInstance>)super.getSqlMapClientTemplate().queryForList("bpmIns.selectSequenceInstance", filter);
	}
	
	public SequenceInstance selectSequenceInstance(Integer sequenceId) {
		SequenceFilter filter = new SequenceFilter();
		filter.setSequenceId(sequenceId);
		List<SequenceInstance> list = this.selectSequenceInstance(filter);
		if(!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	public Integer insertDataInstance(DataInstance data) {
		return (Integer)super.getSqlMapClientTemplate().insert("bpmIns.insertDataInstance", data);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataInstance> selectDataInstance(DataFilter filter) {
		return (List<DataInstance>)super.getSqlMapClientTemplate().queryForList("bpmIns.selectDataInstance", filter);
	}
	
	public DataInstance selectDataInstance(Integer dataId) {
		DataFilter filter = new DataFilter();
		filter.setDataId(dataId);
		List<DataInstance> list = this.selectDataInstance(filter);
		if(!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	public Integer updateDataInstance(DataInstance data) {
		return (Integer)super.getSqlMapClientTemplate().update("bpmIns.updateDataInstance", data);
	}

}
