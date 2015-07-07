package com.brick.bpm.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.brick.bpm.dao.TaskDao;
import com.brick.bpm.filter.TaskFilter;
import com.brick.bpm.ins.FlowInstance;
import com.brick.bpm.ins.ProcessInstance;
import com.brick.bpm.ins.Task;
import com.brick.bpm.util.BpmConst;
import com.brick.bpm.work.WorkItemManager;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.tac.user.dao.UserDAO;

public class TaskService {
	
	private WorkItemManager workItemManager;
	
	private InstanceService bpmInstanceService;
	
	private TaskDao bpmTaskDao;
	
	private UserDAO userDAO;
	
	private MailUtilService mailUtilService;
	
	public Task getTaskById(Integer taskId) throws Exception {
		TaskFilter filter = new TaskFilter();
		filter.setTaskId(taskId);
		List<Task> task = this.bpmTaskDao.selectTask(filter);
		if (task.size() != 1) {
			throw new Exception("该任务[" + taskId + "]不存在!");
		}
		return task.get(0);
	}
	
	/**
	 * 创建任务
	 * @param task
	 */
	public Integer insertTask(Task task) {
		task.setTaskId(null);
		task.setTaskStatus(BpmConst.STATE_READY);
		return this.bpmTaskDao.insertTask(task);
	}
	
	/**
	 * 开始任务
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public Integer startTask(Integer taskId) throws Exception {
		Task task = this.getTaskById(taskId);
		task.setBeginDate(new Date());
		task.setTaskStatus(BpmConst.STATE_PENDING);
		
		/**
		 * 更新当前操作人
		 */
		ProcessInstance currentProcess = this.bpmInstanceService.getProcessById(task.getProcessId());
		String currentCharge = currentProcess.getCurrentCharge();
		String currentChargeName = currentProcess.getCurrentChargeName();
		if (currentCharge == null) {
			currentCharge = "";
		}
		if (currentChargeName == null) {
			currentChargeName = "";
		}
		if (!currentCharge.contains(task.getCharge())) {
			currentCharge = currentCharge.concat(task.getCharge()).concat(" ");
			String chargeName = this.userDAO.getUserById(Integer.valueOf(task.getCharge())).getName();
			currentChargeName = currentChargeName.concat(chargeName).concat(" ");
		}
		this.bpmInstanceService.updateCurrentCharge(currentProcess.getProcessId(), currentCharge, currentChargeName);
		
		Integer result =  this.bpmTaskDao.updateTask(task);
		
		Map<String, Object> flowVariables = workItemManager.getFlowVariables(task.getFlowId());
		
		MailSettingTo mail = new MailSettingTo();
		mail.setEmailSubject("[资讯通知]：有一张核准函变更申请需您处理");
		mail.setEmailContent("您好：\n  有一张核准函变更申请需您处理,烦请进入租赁系统处理!\n  谢谢!\n" + ((flowVariables.get("LEASE_CODE")==null)?"":("\n合同号:"+flowVariables.get("LEASE_CODE").toString())));
		mail.setEmailTo(userDAO.getUserById(Integer.valueOf(task.getCharge())).getEmail());
		mail.setEmailBcc("zhangyizhou@tacleasing.cn");
		mailUtilService.sendMail(mail);
		
		return result;
		
	}
	
	/**
	 * 修改任务名单
	 * @param flowId
	 * @param userList
	 * @throws Exception 
	 */
	public void changeTaskByFlowId(Integer processId,String flowDefId, String userListStr) throws Exception {
		
		FlowInstance flow = this.bpmInstanceService.getFlowByDefId(processId, flowDefId);
		
		List<String> userListCurrent = new ArrayList<String>();
		if(userListStr != null && !userListStr.isEmpty()) {
			userListCurrent = Arrays.asList(userListStr.split(","));
		}
		
		if(BpmConst.STATE_COMPLETED.equals(flow.getFlowStatus())) {
			throw new Exception("该实例结点已完成,无法变更处理人!");
		}
		if(BpmConst.STATE_READY.equals(flow.getFlowStatus())) {
			
		}
		if(BpmConst.STATE_PENDING.equals(flow.getFlowStatus())) {
			TaskFilter filter = new TaskFilter();
			filter.setFlowId(flow.getFlowId());
			filter.setSort(TaskFilter.SORT_PRIORITY);
			List<Task> tasks = bpmTaskDao.selectTask(filter);
			
			//step 1: 删除多余的任务
			for (Task task : tasks) {
				if(!userListCurrent.contains(task.getCharge())) {
					
					if(BpmConst.STATE_PENDING.equals(task.getTaskStatus())){
						/**
						 * 更新当前操作人
						 */
						ProcessInstance currentProcess = this.bpmInstanceService.getProcessById(task.getProcessId());
						String currentCharge = currentProcess.getCurrentCharge();
						String currentChargeName = currentProcess.getCurrentChargeName();
						if (currentCharge == null) {
							currentCharge = "";
						}
						if (currentChargeName == null) {
							currentChargeName = "";
						}
						if (currentCharge.contains(task.getCharge())) {
							currentCharge = currentCharge.replaceFirst(task.getCharge().concat(" "), "");
							
							String chargeName = this.userDAO.getUserById(Integer.valueOf(task.getCharge())).getName();
							currentChargeName = currentChargeName.replaceFirst(chargeName.concat(" "),"");
							
						}
						this.bpmInstanceService.updateCurrentCharge(currentProcess.getProcessId(), currentCharge, currentChargeName);	
					}
					
					this.bpmTaskDao.deleteTask(task);
					
					//TODO:
					System.out.println(task.getTaskId()+"任务取消:"+task.getCharge());
					//tasks.remove(task);
				}
			}
			
			//step 2: 有效任务重新组成新数组,新任务空位
			Task[] currentTasks = new Task[userListCurrent.size()];
			for (int j = 0; j < userListCurrent.size(); j++) {
				for (Task task : tasks) {
					if(task.getCharge().equals(userListCurrent.get(j))) {
						currentTasks[j] = task;
						//tasks.remove(task);
						break;
					}
				}
			}
			
			
			if(BpmConst.MULTI_NONE.equals(flow.getMultiInstance())) {
				
			}
			
			if(BpmConst.MULTI_PARALLEL.equals(flow.getMultiInstance())) {
				
			}
			
			if(BpmConst.MULTI_SEQUENCE.equals(flow.getMultiInstance())) {
				boolean findActiveTask = false;
				for (int i = 0; i < userListCurrent.size(); i++) {
					if(currentTasks[i] != null && currentTasks[i].getPriority() != i) {
						currentTasks[i].setPriority(i);
						bpmTaskDao.updateTask(currentTasks[i]);
					}
					if (findActiveTask == true && currentTasks[i] != null && BpmConst.STATE_PENDING.equals(currentTasks[i].getTaskStatus()) ) {
						
						currentTasks[i].setTaskStatus(BpmConst.STATE_READY);
						currentTasks[i].setBeginDate(null);
						bpmTaskDao.updateTask(currentTasks[i]);
						
						System.out.println(currentTasks[i].getTaskId()+"任务延后:"+currentTasks[i].getCharge());
						
						/**
						 * 更新当前操作人
						 */
						ProcessInstance currentProcess = this.bpmInstanceService.getProcessById(currentTasks[i].getProcessId());
						String currentCharge = currentProcess.getCurrentCharge();
						String currentChargeName = currentProcess.getCurrentChargeName();
						if (currentCharge == null) {
							currentCharge = "";
						}
						if (currentChargeName == null) {
							currentChargeName = "";
						}
						if (currentCharge.contains(currentTasks[i].getCharge())) {
							currentCharge = currentCharge.replaceFirst(currentTasks[i].getCharge().concat(" "), "");
							
							String chargeName = this.userDAO.getUserById(Integer.valueOf(currentTasks[i].getCharge())).getName();
							currentChargeName = currentChargeName.replaceFirst(chargeName.concat(" "),"");
							
						}
						this.bpmInstanceService.updateCurrentCharge(currentProcess.getProcessId(), currentCharge, currentChargeName);
						continue;
					}
					if(currentTasks[i] == null) {
						currentTasks[i] = new Task();
						currentTasks[i].setProcessId(flow.getProcessId());
						currentTasks[i].setFlowDefId(flow.getFlowDefId());
						currentTasks[i].setFlowId(flow.getFlowId());
						currentTasks[i].setTaskStatus(BpmConst.STATE_READY);
						currentTasks[i].setOperateList(flow.getOperateList());
						currentTasks[i].setCharge(userListCurrent.get(i));
						currentTasks[i].setPriority(i);
						bpmTaskDao.insertTask(currentTasks[i]);
					}
					if (findActiveTask == true) {
						continue;
					}
					if(BpmConst.STATE_PENDING.equals(currentTasks[i].getTaskStatus())){
						findActiveTask = true;
						continue;
					}
					if(BpmConst.STATE_READY.equals(currentTasks[i].getTaskStatus())){
						this.startTask(currentTasks[i].getTaskId());
						findActiveTask = true;
						continue;
					}
					
				}
			}
			
			//判定是否需要结束流程
			calculateResult(flow.getFlowId());
		}
		
	}
	
	public void startTaskByFlowId(Integer flowId) throws Exception {
		FlowInstance flow = this.bpmInstanceService.getFlowById(flowId);
		String[] userList = flow.getUserList().split(",");
		if(userList.length == 0) {
			throw new Exception("处理人为空!");
		}
		if(BpmConst.MULTI_NONE.equals(flow.getMultiInstance()) && userList.length > 1) {
			throw new Exception("单实例结点不接受多个处理人!");
		}
		int index = 0;
		for (String user : userList) {
			Task userTask = new Task();
			userTask.setFlowId(flow.getFlowId());
			userTask.setFlowDefId(flow.getFlowDefId());
			userTask.setProcessId(flow.getProcessId());
			userTask.setOperateList(flow.getOperateList());
			userTask.setCharge(user);
			userTask.setBeginDate(new Date());
			if(BpmConst.MULTI_SEQUENCE.equals(flow.getMultiInstance())) {
				userTask.setPriority(index++);
			} else {
				userTask.setPriority(0);
			}
			Integer taskId = this.insertTask(userTask);
			if(!BpmConst.MULTI_SEQUENCE.equals(flow.getMultiInstance()) || userTask.getPriority() == 0) {
				this.startTask(taskId);
			}
		}
	}
	
	/**
	 * 代理任务
	 * @param taskId
	 * @param userId
	 * @throws Exception
	 */
	public void delegateTask(Integer taskId,String userId) throws Exception{
		String delegateName = this.userDAO.getUserById(Integer.valueOf(userId)).getName();
		
		Task task = this.getTaskById(taskId);
		task.setDelegate(userId);
		task.setDelegateName(delegateName);
		this.bpmTaskDao.updateTask(task);
		
		/**
		 * 更新当前代理人
		 */
		ProcessInstance currentProcess = this.bpmInstanceService.getProcessById(task.getProcessId());
		this.bpmInstanceService.updateCurrentDelegate(currentProcess.getProcessId(), task.getDelegate(), task.getDelegateName());
	}
	
	/**
	 * 完成任务
	 * @param taskId
	 * @param result
	 * @param userId
	 * @param comment
	 * @throws Exception
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void completeTask(Integer taskId,String result,String userId,String comment,Map<String, Object> data) throws Exception{
		Task task = this.getTaskById(taskId);
		if(!BpmConst.STATE_PENDING.equals(task.getTaskStatus())) {
			throw new Exception("该任务尚未开始,不得结束");
		}
		task.setEndDate(new Date());
		task.setResult(result);
		task.setOperator(userId);
		task.setComment(comment);
		task.setTaskStatus(BpmConst.STATE_COMPLETED);
		this.bpmTaskDao.updateTask(task);
		//TODO:
		System.out.println(taskId+"任务完成:"+userId);
		
		/**
		 * 更新当前操作人
		 */
		ProcessInstance currentProcess = this.bpmInstanceService.getProcessById(task.getProcessId());
		String currentCharge = currentProcess.getCurrentCharge();
		String currentChargeName = currentProcess.getCurrentChargeName();
		if (currentCharge == null) {
			currentCharge = "";
		}
		if (currentChargeName == null) {
			currentChargeName = "";
		}
		if (currentCharge.contains(task.getCharge())) {
			currentCharge = currentCharge.replaceFirst(task.getCharge().concat(" "), "");
			
			String chargeName = this.userDAO.getUserById(Integer.valueOf(task.getCharge())).getName();
			currentChargeName = currentChargeName.replaceFirst(chargeName.concat(" "),"");
			
		}
		this.bpmInstanceService.updateCurrentCharge(currentProcess.getProcessId(), currentCharge, currentChargeName);
		this.bpmInstanceService.updateCurrentDelegate(currentProcess.getProcessId(), "", "");
		
		if(this.calculateResult(task.getFlowId())){
			return;
		}
		
		FlowInstance flow = this.bpmInstanceService.getFlowById(task.getFlowId());
		TaskFilter readyFilter = new TaskFilter();
		readyFilter.setFlowId(flow.getFlowId());
		readyFilter.setTaskStatus(BpmConst.STATE_READY);
		List<Task> readyTaskList = bpmTaskDao.selectTask(readyFilter);
		//若为顺序多任务,启动下一任务
		if(BpmConst.MULTI_SEQUENCE.equals(flow.getMultiInstance()) && !readyTaskList.isEmpty()) {	
			this.startTask(readyTaskList.get(0).getTaskId());
		}
		
	}
	
	/**
	 * 计算处理结果,如有必要结束本流程
	 * @param flowId
	 * @throws Exception
	 */
	public boolean calculateResult(Integer flowId) throws Exception {
		
		FlowInstance flow = this.bpmInstanceService.getFlowById(flowId);
		
		
		TaskFilter pendFilter = new TaskFilter();
		pendFilter.setFlowId(flow.getFlowId());
		pendFilter.setTaskStatus(BpmConst.STATE_PENDING);
		List<Task> pendTaskList = bpmTaskDao.selectTask(pendFilter);
		
		TaskFilter readyFilter = new TaskFilter();
		readyFilter.setFlowId(flow.getFlowId());
		readyFilter.setTaskStatus(BpmConst.STATE_READY);
		List<Task> readyTaskList = bpmTaskDao.selectTask(readyFilter);
		
		TaskFilter completeFilter = new TaskFilter();
		completeFilter.setFlowId(flow.getFlowId());
		completeFilter.setTaskStatus(BpmConst.STATE_COMPLETED);
		List<Task> completeTaskList = bpmTaskDao.selectTask(completeFilter);
		
		Map<String, Object> variables = new HashMap<String, Object>();
		
		//单任务处理
		if(BpmConst.MULTI_NONE.equals(flow.getMultiInstance())) {
			if(completeTaskList.size() == 1) {
				variables.put("result", completeTaskList.get(0).getResult());
				this.workItemManager.activeWorkItem(flow.getFlowId());
				this.workItemManager.completeWorkItem(flow.getFlowId(), variables);
			}
			return true;
		}
		
		
		//一票否决处理One vote veto
		String result = BpmConst.RESULT_PASS;
		for (Task task : completeTaskList) {
			if(BpmConst.RESULT_REJECT.equals(task.getResult())) {
				result = BpmConst.RESULT_REJECT;
				variables.put("result", result);
				this.workItemManager.activeWorkItem(flow.getFlowId());
				this.workItemManager.completeWorkItem(flow.getFlowId(), variables);
				return true;
			}
		}
		
		if(pendTaskList.isEmpty() && readyTaskList.isEmpty()) {
			variables.put("result", result);
			this.workItemManager.activeWorkItem(flow.getFlowId());
			this.workItemManager.completeWorkItem(flow.getFlowId(), variables);
			return true;
		}
		
		return false;
	}
	
	
	/**
	 * 获得用户的待处理任务
	 * @param userId
	 */
	public List<Task> findTask(String userId) {
		TaskFilter filter = new TaskFilter();
		filter.setUserId(userId);
		filter.setTaskStatus(BpmConst.STATE_PENDING);
		return this.bpmTaskDao.selectTask(filter);
	}
	
	public List<Task> findTask(Integer processId, String userId) {
		TaskFilter filter = new TaskFilter();
		filter.setProcessId(processId);
		filter.setUserId(userId);
		filter.setTaskStatus(BpmConst.STATE_PENDING);
		return this.bpmTaskDao.selectTask(filter);
	}
	
	
	public List<Task> findCompleteTask(Integer processId) {
		TaskFilter filter = new TaskFilter();
		filter.setProcessId(processId);
		filter.setSort(TaskFilter.SORT_ENDDATE);
		filter.setTaskStatus(BpmConst.STATE_COMPLETED);
		return this.bpmTaskDao.selectTask(filter);
	}
	
	public List<Task> findAllTask(Integer processId) {
		TaskFilter filter = new TaskFilter();
		filter.setProcessId(processId);
		filter.setSort(TaskFilter.SORT_TASKID);
		return this.bpmTaskDao.selectTask(filter);
	}
	
	public void setBpmTaskDao(TaskDao bpmTaskDao) {
		this.bpmTaskDao = bpmTaskDao;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public void setWorkItemManager(WorkItemManager workItemManager) {
		this.workItemManager = workItemManager;
	}

	public void setBpmInstanceService(InstanceService bpmInstanceService) {
		this.bpmInstanceService = bpmInstanceService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}

}
