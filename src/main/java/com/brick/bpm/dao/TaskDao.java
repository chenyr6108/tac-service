package com.brick.bpm.dao;

import java.util.List;

import com.brick.base.dao.BaseDAO;
import com.brick.bpm.filter.TaskFilter;
import com.brick.bpm.ins.Task;

public class TaskDao extends BaseDAO {
	
	public Integer insertTask(Task task) {
		return (Integer)super.getSqlMapClientTemplate().insert("bpmTask.insertTask", task);
	}
	
	public Integer updateTask(Task task) {
		return (Integer)super.getSqlMapClientTemplate().update("bpmTask.updateTask", task);
	}
	
	public Integer deleteTask(Task task) {
		return (Integer)super.getSqlMapClientTemplate().update("bpmTask.deleteTask", task);
	}
	
	@SuppressWarnings("unchecked")
	public List<Task> selectTask(TaskFilter filter) {
		return (List<Task>)super.getSqlMapClientTemplate().queryForList("bpmTask.selectTask", filter);
	}

}
