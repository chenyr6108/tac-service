package com.brick.aprv.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.brick.aprv.dao.ApprovalDao;
import com.brick.aprv.filter.ApprovalFilter;
import com.brick.aprv.to.ApprovalTo;
import com.brick.base.service.BaseService;
import com.brick.bpm.DeptTo;
import com.brick.bpm.ins.FlowInstance;
import com.brick.bpm.runtime.RuntimeService;
import com.brick.bpm.service.InstanceService;
import com.brick.bpm.service.TaskService;
import com.brick.bpm.util.BpmConst;

public class ApprovalService extends BaseService {
	
	private static final String PID = "P_APRV_V0.0";
	
	private static final String DID_USERLIST = "USER_LIST";
	
	private static final String DID_RISKLIST = "RISK_LIST";
	
	private static final String DID_UPUSER = "UP_USER";
	
	private static final String DID_LEASECODE = "LEASE_CODE";
	
	private static final String VALUE_RICH = "174";
	
	private ApprovalDao approvalDao;
	
	private RuntimeService runtimeService;
	
	private InstanceService bpmInstanceService;
	
	private TaskService bpmTaskService;
	
	@Transactional(propagation=Propagation.REQUIRED)
	public List<ApprovalTo> selectApproval(ApprovalFilter filter) {
		return this.approvalDao.selectApproval(filter);
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	public ApprovalTo selectApproval(Integer aprvId) {
		ApprovalFilter filter = new ApprovalFilter();
		filter.setAprvId(aprvId);
		List<ApprovalTo> approval = this.approvalDao.selectApproval(filter);
		return approval.get(0);
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	public Integer insertApproval(ApprovalTo approval) {
		return this.approvalDao.insertApproval(approval);
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	public Integer updateApproval(ApprovalTo approval) throws Exception {
		
		if (approval.getAuditData()!=null && !approval.getAuditData().isEmpty() && approval.getProcessId() != null ) {
			
			JSONArray array = JSONArray.fromObject(approval.getAuditData());
			List<DeptTo> deptList = JSONArray.toList(array,DeptTo.class);
			String userList="";
			for (DeptTo deptTo : deptList) {
				userList += deptTo.getUserId() + ",";
			}
			if(!userList.isEmpty()) {
				userList = userList.substring(0,userList.length()-1);
			}
			
			FlowInstance flow = bpmInstanceService.getFlowByDefId(approval.getProcessId(), "toaduit1");
			
			if(BpmConst.STATE_READY.equals(flow.getFlowStatus())) {
				bpmInstanceService.updateProcessData(approval.getProcessId(), "USER_LIST", userList);
			} else if(BpmConst.STATE_PENDING.equals(flow.getFlowStatus())) {
				bpmTaskService.changeTaskByFlowId(approval.getProcessId(), "toaduit1", userList);
			}
			
		}
		return this.approvalDao.updateApproval(approval);
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	public Integer insertAndSubmitApproval(ApprovalTo approval) throws Exception {
		Integer approvalId = this.insertApproval(approval);
		this.submitApproval(approvalId);
		return approvalId;
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	public Integer submitApproval(Integer aprvId) throws Exception {
		ApprovalTo approval = this.selectApproval(aprvId);
		
		List<DeptTo> deptList = new ArrayList<DeptTo>();
		if (approval.getAuditData()!=null && !approval.getAuditData().isEmpty()) {
			JSONArray array = JSONArray.fromObject(approval.getAuditData());
			deptList = JSONArray.toList(array,DeptTo.class);
		}
		
		String userList="";
		for (DeptTo deptTo : deptList) {
			userList += deptTo.getUserId() + ",";
		}
		if(!userList.isEmpty()) {
			userList = userList.substring(0,userList.length()-1);
		}
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(DID_USERLIST,userList);
		if (approval.getUpUserId() != null) {
			param.put(DID_UPUSER,approval.getUpUserId().toString());
		}
		
		Map<String,Object> paramMap = new HashMap<String, Object>();
		paramMap.put("prjtId" , approval.getRectId());
		List<Map<String,Object>> riskUser = baseDAO.queryForListUseMap("approval.selectOriRiskUser", paramMap);
		String riskList = "";
		for (Map<String, Object> map : riskUser) {
			riskList += map.get("CREATE_USER_ID").toString() + ",";
		}
		if (!riskList.isEmpty()) {
			riskList = riskList.substring(0,riskList.length()-1);
			param.put(DID_RISKLIST,riskList);
		}
		if (riskList.isEmpty()) {
			param.put(DID_RISKLIST,VALUE_RICH);
		}
		param.put(DID_LEASECODE, approval.getLeaseCode());
		
		approval.setProcessId(runtimeService.startProcess(PID,param));
		return this.approvalDao.updateApproval(approval);
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	public Integer resubmitApproval(Integer aprvId) throws Exception {
		ApprovalTo approval = this.selectApproval(aprvId);
		
		List<DeptTo> deptList = new ArrayList<DeptTo>();
		if (approval.getAuditData()!=null && !approval.getAuditData().isEmpty()) {
			JSONArray array = JSONArray.fromObject(approval.getAuditData());
			deptList = JSONArray.toList(array,DeptTo.class);
		}
		
		String userList="";
		for (DeptTo deptTo : deptList) {
			userList += deptTo.getUserId() + ",";
		}
		if(!userList.isEmpty()) {
			userList = userList.substring(0,userList.length()-1);
		}
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(DID_USERLIST,userList);
		if (approval.getUpUserId() != null) {
			param.put(DID_UPUSER,approval.getUpUserId().toString());
		}
		if(approval.getHisProcess()==null) {
			approval.setHisProcess(approval.getProcessId().toString());
		} else {
			approval.setHisProcess(approval.getHisProcess() + "," + approval.getProcessId().toString());
		}
		
		Map<String,Object> paramMap = new HashMap<String, Object>();
		paramMap.put("prjtId" , approval.getRectId());
		List<Map<String,Object>> riskUser = baseDAO.queryForListUseMap("approval.selectOriRiskUser", paramMap);
		String riskList = "";
		for (Map<String, Object> map : riskUser) {
			riskList += map.get("CREATE_USER_ID").toString() + ",";
		}
		if (!riskList.isEmpty()) {
			riskList = riskList.substring(0,riskList.length()-1);
			param.put(DID_RISKLIST,riskList);
		}
		if (riskList.isEmpty()) {
			param.put(DID_RISKLIST,VALUE_RICH);
		}
		
		approval.setProcessId(runtimeService.startProcess(PID,param));
		return this.approvalDao.updateApproval(approval);
	}

	public ApprovalTo previewApproval(ApprovalFilter filter) {
		return this.approvalDao.previewApproval(filter);
	}
	
	public void setApprovalDao(ApprovalDao approvalDao) {
		this.approvalDao = approvalDao;
	}

	public void setRuntimeService(RuntimeService runtimeService) {
		this.runtimeService = runtimeService;
	}

	public void setBpmTaskService(TaskService bpmTaskService) {
		this.bpmTaskService = bpmTaskService;
	}

	public void setBpmInstanceService(InstanceService bpmInstanceService) {
		this.bpmInstanceService = bpmInstanceService;
	}
	
	
	
}
