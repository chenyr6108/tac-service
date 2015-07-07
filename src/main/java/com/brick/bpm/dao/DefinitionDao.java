package com.brick.bpm.dao;

import java.util.List;
import com.brick.base.dao.BaseDAO;
import com.brick.bpm.def.*;
import com.brick.bpm.def.Process;
import com.brick.bpm.filter.DataDefFilter;
import com.brick.bpm.filter.FlowDefFilter;
import com.brick.bpm.filter.ProcessDefFilter;
import com.brick.bpm.filter.SequenceDefFilter;


/**
 * 流程定义相关持久处理
 * @author zhangyizhou
 *
 */
public class DefinitionDao extends BaseDAO {
	
	@SuppressWarnings("unchecked")
	public List<Process> selectProcess(ProcessDefFilter filter) {
		return (List<Process>)super.getSqlMapClientTemplate().queryForList("bpmDef.selectProcess",filter);
	}
	
	public Process selectProcess(String processDefId) {
		ProcessDefFilter filter = new ProcessDefFilter();
		filter.setProcessDefId(processDefId);
		List<Process> list = this.selectProcess(filter);
		if(!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<FlowObject> selectFlow(FlowDefFilter filter) {
		return(List<FlowObject>)super.getSqlMapClientTemplate().queryForList("bpmDef.selectFlow",filter);
	}
	
	public FlowObject selectFlow(String processDefId,String flowDefId) {
		FlowDefFilter filter = new FlowDefFilter();
		filter.setProcessDefId(processDefId);
		filter.setFlowDefId(flowDefId);
		List<FlowObject> list = this.selectFlow(filter);
		if(!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<SequenceFlow> selectSequence(SequenceDefFilter filter) {
		return (List<SequenceFlow>)super.getSqlMapClientTemplate().queryForList("bpmDef.selectSequenceFlow",filter);
	}
	
	public SequenceFlow selectSequence(String processDefId,String sequenceDefId) {
		SequenceDefFilter filter = new SequenceDefFilter();
		filter.setProcessDefId(processDefId);
		filter.setSequenceDefId(sequenceDefId);
		List<SequenceFlow> list = this.selectSequence(filter);
		if(!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Data> selectData(DataDefFilter filter) {
		return (List<Data>)super.getSqlMapClientTemplate().queryForList("bpmDef.selectData",filter);
	}
	
	public Data selectData(String processDefId,String dataDefId) {
		DataDefFilter filter = new DataDefFilter();
		filter.setProcessDefId(processDefId);
		filter.setDataDefId(dataDefId);
		List<Data> list = this.selectData(filter);
		if(!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
}
