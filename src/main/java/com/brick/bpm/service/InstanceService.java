package com.brick.bpm.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.brick.bpm.dao.DefinitionDao;
import com.brick.bpm.dao.InstanceDao;
import com.brick.bpm.def.Data;
import com.brick.bpm.def.FlowObject;
import com.brick.bpm.def.Process;
import com.brick.bpm.def.SequenceFlow;
import com.brick.bpm.filter.DataDefFilter;
import com.brick.bpm.filter.DataFilter;
import com.brick.bpm.filter.FlowDefFilter;
import com.brick.bpm.filter.FlowFilter;
import com.brick.bpm.filter.SequenceDefFilter;
import com.brick.bpm.filter.SequenceFilter;
import com.brick.bpm.ins.DataInstance;
import com.brick.bpm.ins.FlowInstance;
import com.brick.bpm.ins.ProcessInstance;
import com.brick.bpm.ins.SequenceInstance;
import com.brick.bpm.util.BpmConst;

public class InstanceService {
	
	Log logger = LogFactory.getLog(InstanceService.class);
	
	private DefinitionDao bpmDefinitionDao;
	
	private InstanceDao bpmInstanceDao;

	public void updateUserList(Integer flowId, String userList) {
		FlowInstance currentFlow = bpmInstanceDao.selectFlowInstance(flowId);
		currentFlow.setUserList(userList);
		bpmInstanceDao.updateFlowInstance(currentFlow);
	}
	
	public void updateCurrentCharge(Integer processId, String currentCharge,String currentChargeName) {
		ProcessInstance currentProcess = bpmInstanceDao.selectProcessInstance(processId);
		currentProcess.setCurrentCharge(currentCharge);
		currentProcess.setCurrentChargeName(currentChargeName);
		bpmInstanceDao.updateProcessInstance(currentProcess);
	}
	
	public void updateCurrentDelegate(Integer processId, String currentDelegate,String currentDelegateName) {
		ProcessInstance currentProcess = bpmInstanceDao.selectProcessInstance(processId);
		currentProcess.setCurrentDelegate(currentDelegate);
		currentProcess.setCurrentDelegateName(currentDelegateName);
		bpmInstanceDao.updateProcessInstance(currentProcess);
	}
	
	public void updateProcessData(Integer processId, String dataDefId, String value) throws Exception {
		DataFilter filter = new DataFilter();
		filter.setProcessId(processId);
		filter.setDataDefId(dataDefId);
		List<DataInstance> data = bpmInstanceDao.selectDataInstance(filter);
		if (data.size() != 1) {
			throw new Exception("该流程数据不存在!");
		}
		data.get(0).setValue(value);
		bpmInstanceDao.updateDataInstance(data.get(0));
	}
	
	public String getProcessData(Integer processId, String dataDefId) throws Exception {
		DataFilter filter = new DataFilter();
		filter.setProcessId(processId);
		filter.setDataDefId(dataDefId);
		List<DataInstance> data = bpmInstanceDao.selectDataInstance(filter);
		if (data.size() != 1) {
			throw new Exception("该流程数据不存在!");
		}
		return data.get(0).getValue();
	}
	
	/**
	 * 根据编号获取流程结点
	 * @param flowId
	 * @return
	 * @throws Exception
	 */
	public FlowInstance getFlowById(Integer flowId) throws Exception {
		FlowInstance currentFlow = bpmInstanceDao.selectFlowInstance(flowId);
		if(currentFlow == null) {
			throw new Exception("该流程结点" + flowId + "不存在!");
		}
		return currentFlow;
	}
	
	
	public FlowInstance getFlowByDefId(Integer processId,String flowDefId)  throws Exception {
		FlowFilter flowFilter = new FlowFilter();
		flowFilter.setProcessId(processId);
		flowFilter.setFlowDefId(flowDefId);
		List<FlowInstance> currentFlow = bpmInstanceDao.selectFlowInstance(flowFilter);
		if(currentFlow == null || currentFlow.size() != 1) {
			throw new Exception("该流程结点" + flowDefId + "不存在!");
		}
		return currentFlow.get(0);
	}
	
	/**
	 * 结点开始
	 * @param flowId
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.MANDATORY)
	public void activeFlow(Integer flowId) throws Exception {
		FlowInstance currentFlow = this.getFlowById(flowId);
		if(!FlowInstance.STATE_READY.equals(currentFlow.getFlowStatus()) && !FlowInstance.STATE_PENDING.equals(currentFlow.getFlowStatus())) {
			throw new Exception("非准备或待处理流程结点" + flowId + "无法被激活!");
		}
		currentFlow.setFlowStatus(FlowInstance.STATE_ACTIVE);
		bpmInstanceDao.updateFlowInstance(currentFlow);
		
		//更新流程状态
		ProcessInstance currentProcess = this.getProcessById(currentFlow.getProcessId());
		currentProcess.setFlowStatus(currentProcess.getFlowStatus() | currentFlow.getFlowDefCode());
		String currentStatusName = currentProcess.getFlowStatusName();
		if (currentStatusName == null) {
			currentStatusName = "";
		}
		if (!currentStatusName.contains(currentFlow.getFlowName())) {
			currentStatusName = currentStatusName.concat(currentFlow.getFlowName()).concat(" ");
		}
		currentProcess.setFlowStatusName(currentStatusName);
		bpmInstanceDao.updateProcessInstance(currentProcess);
		
		//if(logger.isDebugEnabled()) {
		//	logger.debug("结点["+ currentFlow.getProcessDefId() +"][" + currentFlow.getFlowName() +"]激活");
			System.out.println("结点["+ currentFlow.getProcessDefId() +"][" + currentFlow.getFlowName() +"]激活");
		//}
	}
	
	/**
	 * 结点完成
	 * @param flowId
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.MANDATORY)
	public void completeFlow(Integer flowId) throws Exception {
		FlowInstance currentFlow = this.getFlowById(flowId);
		if(!FlowInstance.STATE_ACTIVE.equals(currentFlow.getFlowStatus())) {
			throw new Exception("非活动流程结点" + flowId + "无法被完成!");
		}
		currentFlow.setFlowStatus(FlowInstance.STATE_COMPLETED);
		bpmInstanceDao.updateFlowInstance(currentFlow);
		
		//更新流程状态
		if(!BpmConst.FLOWCLASS_ENDEVENT.equals(currentFlow.getFlowClass())) {
			ProcessInstance currentProcess = this.getProcessById(currentFlow.getProcessId());
			currentProcess.setFlowStatus(currentProcess.getFlowStatus() ^ currentFlow.getFlowDefCode());
			
			String currentStatusName = currentProcess.getFlowStatusName();
			if (currentStatusName == null) {
				currentStatusName = "";
			}
			if (currentStatusName.contains(currentFlow.getFlowName())) {
				currentStatusName = currentStatusName.replaceFirst(currentFlow.getFlowName().concat(" "), "");
			}
			currentProcess.setFlowStatusName(currentStatusName);
			
			bpmInstanceDao.updateProcessInstance(currentProcess);
		}
		
		//if(logger.isDebugEnabled()) {
		//	logger.debug("结点["+ currentFlow.getProcessDefId() +"][" + currentFlow.getFlowName() +"]完成");
			System.out.println("结点["+ currentFlow.getProcessDefId() +"][" + currentFlow.getFlowName() +"]完成");
		//}
	}
	
	/**
	 * 结点失败
	 * @param flowId
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void abortFlow(Integer flowId) throws Exception {
		FlowInstance currentFlow = this.getFlowById(flowId);
		if(!FlowInstance.STATE_ACTIVE.equals(currentFlow.getFlowStatus())) {
			throw new Exception("非活动流程结点" + flowId + "无法被完成!");
		}
		currentFlow.setFlowStatus(FlowInstance.STATE_ABORTED);
		bpmInstanceDao.updateFlowInstance(currentFlow);
		//if(logger.isDebugEnabled()) {
		//	logger.debug("结点["+ currentFlow.getProcessDefId() +"][" + currentFlow.getFlowName() +"]失败");
			System.out.println("结点["+ currentFlow.getProcessDefId() +"][" + currentFlow.getFlowName() +"]失败");
		//}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void pendFlow(Integer flowId) throws Exception {
		FlowInstance currentFlow = this.getFlowById(flowId);
		if(!FlowInstance.STATE_ACTIVE.equals(currentFlow.getFlowStatus())) {
			throw new Exception("非活动流程结点" + flowId + "无法待处理!");
		}
		currentFlow.setFlowStatus(FlowInstance.STATE_PENDING);
		bpmInstanceDao.updateFlowInstance(currentFlow);
		//if(logger.isDebugEnabled()) {
		//	logger.debug("结点["+ currentFlow.getProcessDefId() +"][" + currentFlow.getFlowName() +"]待处理");
			System.out.println("结点["+ currentFlow.getProcessDefId() +"][" + currentFlow.getFlowName() +"]待处理");
		//}
	}
	
	/**
	 * 获取流程结点流出
	 * @param flowId
	 * @return
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public List<SequenceInstance> getOutGoings(Integer flowId) throws Exception {
		SequenceFilter sequenceFilter = new SequenceFilter();
		sequenceFilter.setSourceId(flowId);
		return bpmInstanceDao.selectSequenceInstance(sequenceFilter);
	}
	
	/**
	 * 获取流程结点流入
	 * @param flowId
	 * @return
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public List<SequenceInstance> getInGoings(Integer flowId) throws Exception {
		SequenceFilter sequenceFilter = new SequenceFilter();
		sequenceFilter.setTargetId(flowId);
		return bpmInstanceDao.selectSequenceInstance(sequenceFilter);
	}
	
	/**
	 * 根据编号获取流程实例
	 * @param processId
	 * @return
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public ProcessInstance getProcessById(Integer processId) throws Exception {
		ProcessInstance process = bpmInstanceDao.selectProcessInstance(processId);
		if(process == null) {
			throw new Exception("该流程" + processId + "不存在!");
		}
		return process;
	}
	
	/**
	 * 流程开始
	 * @param processId
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void startProcess(Integer processId) throws Exception {
		ProcessInstance process = this.getProcessById(processId);
		if(!ProcessInstance.STATE_READY.equals(process.getStatus())) {
			throw new Exception("非准备流程" + processId + "无法被开始!");
		}
		process.setBeginDate(new Date());
		process.setStatus(ProcessInstance.STATE_ACTIVE);
		Integer result = bpmInstanceDao.updateProcessInstance(process);
		if (result != 1) {
			throw new Exception("流程["+ process.getProcessDefId() +"]开始失败!");
		}
		//if(logger.isDebugEnabled()) {
		//	logger.debug("流程["+ process.getProcessDefId() +"]开始");
			System.out.println("流程["+ process.getProcessDefId() +"]开始");
		//}
	}
	
	
	/**
	 * 流程完成
	 * @param processId
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void completeProcess(Integer processId) throws Exception {
		ProcessInstance process = this.getProcessById(processId);
		if(!ProcessInstance.STATE_ACTIVE.equals(process.getStatus())) {
			throw new Exception("非活动流程" + processId + "无法被完成!");
		}
		process.setEndDate(new Date());
		process.setStatus(ProcessInstance.STATE_COMPLETED);
		Integer result = bpmInstanceDao.updateProcessInstance(process);
		if (result != 1) {
			throw new Exception("流程["+ process.getProcessDefId() +"]完成失败!");
		}
		//if(logger.isDebugEnabled()) {
		//	logger.debug("流程["+ process.getProcessDefId() +"]完成");
			System.out.println("流程["+ process.getProcessDefId() +"]完成");
		//}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void pendProcess(Integer processId) throws Exception {
		ProcessInstance process = this.getProcessById(processId);
		if(!ProcessInstance.STATE_ACTIVE.equals(process.getStatus())) {
			throw new Exception("非活动流程" + processId + "无法设置为待处理!");
		}
		process.setStatus(ProcessInstance.STATE_PENDING);
		Integer result = bpmInstanceDao.updateProcessInstance(process);
		if (result != 1) {
			throw new Exception("流程["+ process.getProcessDefId() +"]待处理失败!");
		}
		//if(logger.isDebugEnabled()) {
		//	logger.debug("流程["+ process.getProcessDefId() +"]待处理");
			System.out.println("流程["+ process.getProcessDefId() +"]待处理");
		//}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void activeProcess(Integer processId) throws Exception {
		ProcessInstance process = this.getProcessById(processId);
		if (!ProcessInstance.STATE_READY.equals(process.getStatus()) && !ProcessInstance.STATE_PENDING.equals(process.getStatus())) {
			throw new Exception("非待处理流程" + processId + "无法激活!");
		}
		process.setStatus(ProcessInstance.STATE_ACTIVE);
		Integer result = bpmInstanceDao.updateProcessInstance(process);
		if (result != 1) {
			throw new Exception("流程["+ process.getProcessDefId() +"]激活失败!");
		}
		//if(logger.isDebugEnabled()) {
		//	logger.debug("流程["+ process.getProcessDefId() +"]激活");
			System.out.println("流程["+ process.getProcessDefId() +"]激活");
		//}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void commitData(Integer sequenceId,Map<String, Object> data) throws Exception {
		//提交数据为空,无需处理
		if(data == null || data.isEmpty()) {
			return;
		}
		//将提交数据按ID匹配更新
		DataFilter sequenceDataFilter = new DataFilter();
		sequenceDataFilter.setSequenceId(sequenceId);
		List<DataInstance> sequenceDataList = bpmInstanceDao.selectDataInstance(sequenceDataFilter);
		for (DataInstance sequenceData : sequenceDataList) {
			if(data.containsKey(sequenceData.getDataDefId())) {
				sequenceData.setValue(data.get(sequenceData.getDataDefId()).toString());
				bpmInstanceDao.updateDataInstance(sequenceData);
			}
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public Map<String, Object> getFlowData(Integer sequenceId) throws Exception {
		//将提交数据按ID匹配更新
		DataFilter sequenceDataFilter = new DataFilter();
		sequenceDataFilter.setSequenceId(sequenceId);
		List<DataInstance> sequenceDataList = bpmInstanceDao.selectDataInstance(sequenceDataFilter);
		Map<String, Object> ret = new HashMap<String, Object>();
		for (DataInstance sequenceData : sequenceDataList) {
			Object value = null;
			if(BpmConst.DATATYPE_INTEGER.equals(sequenceData.getDataType())) {
				value = Integer.valueOf(sequenceData.getValue());
			} else {
				value = sequenceData.getValue();
			}
			ret.put(sequenceData.getDataDefId(), value);
		}
		return ret;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public Map<String, Object> getProcessData(Integer processId) throws Exception {
		//将提交数据按ID匹配更新
		DataFilter processDataFilter = new DataFilter();
		processDataFilter.setProcessId(processId);
		processDataFilter.setScope(BpmConst.SCOPE_PROCESS);
		List<DataInstance> sequenceDataList = bpmInstanceDao.selectDataInstance(processDataFilter);
		Map<String, Object> ret = new HashMap<String, Object>();
		for (DataInstance processData : sequenceDataList) {
			Object value = null;
			if(BpmConst.DATATYPE_INTEGER.equals(processData.getDataType())) {
				value = Integer.valueOf(processData.getValue());
			} else {
				value = processData.getValue();
			}
			ret.put(processData.getDataDefId(), value);
		}
		return ret;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public Integer getStartEvent(Integer processId) throws Exception {
		FlowFilter startEventFilter = new FlowFilter();
		startEventFilter.setProcessId(processId);
		startEventFilter.setFlowClass(BpmConst.FLOWCLASS_STARTEVENT);
		
		List<FlowInstance> startEvent = bpmInstanceDao.selectFlowInstance(startEventFilter);
		if(startEvent.size() == 0) {
			throw new Exception("流程结点[" + processId + "]开始结点未定义");
		}
		if(startEvent.size() > 1) {
			throw new Exception("流程[" + processId + "]开始结点多于一个");
		}
		return startEvent.get(0).getFlowId();
	}
	
	/**
	 * 创建流程实例(不执行)
	 * @param processDefId
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor=Exception.class)
	public Integer createProcessInstance(String processDefId) throws Exception{
		//流程代号
		Integer processInstanceId = null; 
		//获取流程定义
		Process processDefinition = bpmDefinitionDao.selectProcess(processDefId);
		//流程不存在退出
		if(processDefinition == null) {
			throw new Exception("流程[" + processDefId + "]未定义");
		}
		FlowDefFilter flowFilter = new FlowDefFilter();
		flowFilter.setProcessDefId(processDefId);
		List<FlowObject> flowList = bpmDefinitionDao.selectFlow(flowFilter);
		SequenceDefFilter sequenceFilter = new SequenceDefFilter();
		sequenceFilter.setProcessDefId(processDefId);
		List<SequenceFlow> sequenceList = bpmDefinitionDao.selectSequence(sequenceFilter);
		DataDefFilter dataFilter = new DataDefFilter();
		dataFilter.setProcessDefId(processDefId);
		List<Data> dataList = bpmDefinitionDao.selectData(dataFilter);
		//创建流程实例
		ProcessInstance processInstance = new ProcessInstance();
		BeanUtils.copyProperties(processInstance, processDefinition);
		processInstance.setStatus(ProcessInstance.STATE_READY);
		processInstance.setFlowStatus(0);
		processInstanceId = bpmInstanceDao.insertProcessInstance(processInstance);
		
		//创建流程结点
		for(FlowObject flowDefinition : flowList) {
			FlowInstance flowInstance = new FlowInstance();
			BeanUtils.copyProperties(flowInstance, flowDefinition);
			flowInstance.setProcessId(processInstanceId);
			flowInstance.setFlowStatus(FlowInstance.STATE_READY);
			bpmInstanceDao.insertFlowInstance(flowInstance);
		}
		//创建流程路径
		for(SequenceFlow sequenceDefinition : sequenceList) {
			SequenceInstance sequenceInstance = new SequenceInstance();
			sequenceInstance.setProcessId(processInstanceId);
			BeanUtils.copyProperties(sequenceInstance, sequenceDefinition);
			//寻找并设置来源结点
			FlowFilter sourceFilter  = new FlowFilter();
			sourceFilter.setProcessId(processInstanceId);
			sourceFilter.setFlowDefId(sequenceInstance.getSource());
			List<FlowInstance> sourceFlow =  bpmInstanceDao.selectFlowInstance(sourceFilter);
			if (sourceFlow.size() == 0) {
				throw new Exception("流程结点[" + processDefId + "][" + sequenceInstance.getSource() + "]未定义");
			}
			sequenceInstance.setSourceId(sourceFlow.get(0).getFlowId());
			//寻找并设置目标结点
			FlowFilter targetFilter  = new FlowFilter();
			targetFilter.setProcessId(processInstanceId);
			targetFilter.setFlowDefId(sequenceInstance.getTarget());
			List<FlowInstance> targetFlow =  bpmInstanceDao.selectFlowInstance(targetFilter);
			if (targetFlow.size() == 0) {
				throw new Exception("流程结点[" + processDefId + "][" + sequenceInstance.getTarget() + "]未定义");
			}
			sequenceInstance.setTargetId(targetFlow.get(0).getFlowId());
			//持久化
			bpmInstanceDao.insertSequenceInstance(sequenceInstance);
		}
		//创建流程数据
		for(Data dataDefinition : dataList) {
			DataInstance dataInstance = new DataInstance();
			dataInstance.setProcessId(processInstanceId);
			BeanUtils.copyProperties(dataInstance, dataDefinition);
			dataInstance.setValue(dataDefinition.getInitValue());
			
			if(BpmConst.SCOPE_FLOW.equals(dataDefinition.getScope())) {
				SequenceFilter dataSequenceFilter = new SequenceFilter();
				dataSequenceFilter.setProcessId(processInstanceId);
				dataSequenceFilter.setSequenceDefId(dataDefinition.getSequenceDefId());
				List<SequenceInstance> dataSequence = bpmInstanceDao.selectSequenceInstance(dataSequenceFilter);
				if (dataSequence.size() != 1) {
					throw new Exception("流程路径[" + processDefId + "][" + dataDefinition.getSequenceDefId() + "]未定义");
				}
				dataInstance.setSequenceId(dataSequence.get(0).getSequenceId());
			}
			
			bpmInstanceDao.insertDataInstance(dataInstance);
		}
		return processInstanceId;
	}
	
	public void setBpmInstanceDao(InstanceDao bpmInstanceDao) {
		this.bpmInstanceDao = bpmInstanceDao;
	}

	public void setBpmDefinitionDao(DefinitionDao bpmDefinitionDao) {
		this.bpmDefinitionDao = bpmDefinitionDao;
	}
	
}
