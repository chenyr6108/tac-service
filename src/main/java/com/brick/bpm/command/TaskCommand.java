package com.brick.bpm.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.command.BaseCommand;
import com.brick.base.to.DataDictionaryTo;
import com.brick.base.to.SelectionTo;
import com.brick.bpm.dao.DefinitionDao;
import com.brick.bpm.def.FlowObject;
import com.brick.bpm.filter.FlowDefFilter;
import com.brick.bpm.ins.Task;
import com.brick.bpm.service.InstanceService;
import com.brick.bpm.service.TaskService;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;

public class TaskCommand extends BaseCommand {
	
	public static final String TASK_UI_URL = "/bpm/TaskUI.jsp";
	
	public static final String TASK_COMPLETE_URL = "/bpm/TaskComplete.jsp";
	
	public static final String DEPT_CODE = "BPM会签部门";
	
	public static final String DEPT_UI_URL = "/bpm/DeptUI.jsp";
	
	private TaskService bpmTaskService;
	
	private DefinitionDao bpmDefinitionDao;
	
	private InstanceService bpmInstanceService;
	
	public void queryUser(Context context) {
		List<SelectionTo> users = (List<SelectionTo>) baseService.queryForList("user.getUsersForAutocomplete",context.contextMap);
		List<Map<String, String>> outputUsers = new ArrayList<Map<String,String>>();
		for (SelectionTo selectionTo : users) {
			Map<String, String> temp = new HashMap<String, String>();
			temp.put("label", selectionTo.getDisplay_name());
			temp.put("value", selectionTo.getOption_value());
			outputUsers.add(temp);
		}
		Map<String, Object> output = new HashMap<String, Object>();
		Output.jsonArrayListOutput(outputUsers, context);
	}
	
	public void getAllDept(Context context) {
		String[] selectDepts = new String[0];
		if (context.getContextMap().get("selectDepts")!=null) {
			selectDepts = context.getContextMap().get("selectDepts").toString().split(",");
		}
		String[] disabledDepts = new String[0];
		if (context.getContextMap().get("disabledDepts")!=null) {
			disabledDepts = context.getContextMap().get("disabledDepts").toString().split(",");
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("type", DEPT_CODE);
		paramMap.put("companyCode", context.getContextMap().get("companyCode"));
		Map<String, Object> output = new HashMap<String, Object>();
		output.put("depts", (List<DataDictionaryTo>)this.baseService.queryForList("bpmTask.getDept", paramMap));
		output.put("selectDepts", selectDepts);
		output.put("disabledDepts", disabledDepts);
		Output.jspOutput(output, context, DEPT_UI_URL);
	}
	
	public void getFlowStatus(Context context) {
		Integer processId = Integer.valueOf(context.getContextMap().get("processId").toString());
		String flowDefId = context.getContextMap().get("flowDefId").toString();
		Map<String, Object> output = new HashMap<String, Object>();
		try {
			output.put("flowStatus", bpmInstanceService.getFlowByDefId(processId, flowDefId).getFlowStatus());
			output.put("result", "success");
		} catch (Exception e) {
			output.put("result", "fail");
		}
		Output.jsonOutput(output, context);
	}
	
	public void updateProcessData(Context context) {
		Integer processId = Integer.valueOf(context.getContextMap().get("processId").toString());
		String dataDefId = context.getContextMap().get("dataDefId").toString();
		String value = context.getContextMap().get("value").toString();
		Map<String, Object> output = new HashMap<String, Object>();
		try {
			bpmInstanceService.updateProcessData(processId, dataDefId, value);
			output.put("result", "success");
		} catch (Exception e) {
			output.put("result", "fail");
		}
		Output.jsonOutput(output, context);
	}
	
	public void getProcessData(Context context) {
		Integer processId = Integer.valueOf(context.getContextMap().get("processId").toString());
		String dataDefId = context.getContextMap().get("dataDefId").toString();
		Map<String, Object> output = new HashMap<String, Object>();
		try {
			output.put("value", bpmInstanceService.getProcessData(processId, dataDefId));
			output.put("result", "success");
		} catch (Exception e) {
			output.put("result", "fail");
		}
		Output.jsonOutput(output, context);
	}
	
	public void getFlowStatusByProcessDefId(Context context) {
		String processDefId = context.getContextMap().get("processDefId").toString();
		
		FlowDefFilter filter = new FlowDefFilter();
		filter.setProcessDefId(processDefId);
		
		List<FlowObject> flows = bpmDefinitionDao.selectFlow(filter);
		
		Map<String, Object> output = new HashMap<String, Object>();
		try {
			output.put("result", "success");
			output.put("flows", flows);
		} catch (Exception e) {
			output.put("result", "fail");
		}
		Output.jsonOutput(output, context);
	}
	
	public void getFlowStatusName(Context context) {
		Integer processId = Integer.valueOf(context.getContextMap().get("processId").toString());
		Map<String, Object> output = new HashMap<String, Object>();
		try {
			output.put("result", "success");
			output.put("flowStatusName", bpmInstanceService.getProcessById(processId).getFlowStatusName());
		} catch (Exception e) {
			output.put("result", "fail");
		}
		Output.jsonOutput(output, context);
	}
	
	public void getCurrentChargeName(Context context) {
		Integer processId = Integer.valueOf(context.getContextMap().get("processId").toString());
		Map<String, Object> output = new HashMap<String, Object>();
		try {
			output.put("result", "success");
			output.put("currentChargeName", bpmInstanceService.getProcessById(processId).getCurrentChargeName());
			output.put("currentDelegateName", bpmInstanceService.getProcessById(processId).getCurrentDelegateName());
		} catch (Exception e) {
			output.put("result", "fail");
		}
		Output.jsonOutput(output, context);
	}
	
	public void getTaskUI(Context context) {
		Integer processId = Integer.valueOf(context.getContextMap().get("processId").toString());
		String userId = context.getContextMap().get("userId").toString();
		if (userId==null || userId.isEmpty()) {
			userId = context.getContextMap().get("s_employeeId").toString();
		}
		boolean bpm_admin = baseService.checkAccessForResource("bpm_admin", String.valueOf(context.contextMap.get("s_employeeId")));
		if(bpm_admin) {
			userId = null;
		}
		
		List<Task> task = bpmTaskService.findTask(processId, userId);

		Map<String, Object> output = new HashMap<String, Object>();
		if(task.size()==1) {
			output.put("taskId", task.get(0).getTaskId());
			output.put("userId", task.get(0).getCharge());
			String operateList = task.get(0).getOperateList();
			if (operateList != null && !operateList.isEmpty()) {
				for (char operate : operateList.toCharArray()) {
					output.put("OPERATE_"+operate, true);
				}
			}
		}
		output.put("bpm_admin", bpm_admin);
		Output.jspOutput(output, context, TASK_UI_URL);
	}
	
	public void commitTask(Context context) {
		Integer taskId = Integer.valueOf(context.getContextMap().get("taskId").toString());
		String result = context.getContextMap().get("result").toString();
		String comment = context.getContextMap().get("comment").toString();
		String userId = context.getContextMap().get("s_employeeId").toString();
		
		Map<String, Object> output = new HashMap<String, Object>();
		try {
			bpmTaskService.completeTask(taskId, result, userId, comment, null);
			output.put("result", "success");
		} catch (Exception e) {
			output.put("result", "fail");
		}
		Output.jsonOutput(output, context);
	}
	
	public void delegateTask(Context context) {
		Integer taskId = Integer.valueOf(context.getContextMap().get("taskId").toString());
		String userId = context.getContextMap().get("userId").toString();
		Map<String, Object> output = new HashMap<String, Object>();
		try {
			bpmTaskService.delegateTask(taskId, userId);
			output.put("result", "success");
		} catch (Exception e) {
			output.put("result", "fail");
		}
		Output.jsonOutput(output, context);
	}
	
	public void selectCompleteTask(Context context) {
		Integer processId = Integer.valueOf(context.getContextMap().get("processId").toString());
		List<Task> completeTasks = bpmTaskService.findCompleteTask(processId);
		Map<String, Object> output = new HashMap<String, Object>();
		output.put("completeTasks", completeTasks);
		Output.jspOutput(output, context, TASK_COMPLETE_URL);
	}

	public void setBpmTaskService(TaskService bpmTaskService) {
		this.bpmTaskService = bpmTaskService;
	}

	public void setBpmInstanceService(InstanceService bpmInstanceService) {
		this.bpmInstanceService = bpmInstanceService;
	}

	public void setBpmDefinitionDao(DefinitionDao bpmDefinitionDao) {
		this.bpmDefinitionDao = bpmDefinitionDao;
	}

}
