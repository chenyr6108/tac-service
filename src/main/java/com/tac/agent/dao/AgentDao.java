package com.tac.agent.dao;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;
import com.brick.service.entity.Context;
import com.tac.agent.to.Agent;

public class AgentDao extends BaseDAO {
	
	/**
	 * 根据用户id获取代理对象
	 * @param userId			用户id
	 * @param agentModuleId 	代理模组id
	 * @return					null为没有代理
	 */
	public Agent getAgentByUserIdAndModuleId(int userId, String agentModuleId) throws Exception{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("userId", userId);
		param.put("agentModuleId", agentModuleId);
		return (Agent) this.getSqlMapClientTemplate().queryForObject("agent.getAgentByUserIdAndModuleId",param);
	}
	
	/**
	 * 根据条件查询所有代理信息
	 * @param param 查询条件
	 * @return
	 */
	public List<Agent> getAllAgent(Map<String, Object> param) throws Exception{
		return this.getSqlMapClientTemplate().queryForList("agent.getAllAgent",param);
	}
	
	/**
	 * 添加代理信息
	 * @param agent
	 */
	public void addAgent(Agent agent) throws Exception{
		this.getSqlMapClientTemplate().insert("agent.insertAgent",agent);
	}
	
	/**
	 * 更新代理状态
	 * @param agentId 	代理信息id
	 * @param status	代理状态
	 */
	public void updateAgentStatus(int agentId, int status) throws Exception{
		
	}
	
	/**
	 * 更新代理信息
	 * @param agent		更新后的agent
	 */
	public void updateAgent(Agent agent) throws Exception{
		
	}
	
	/**
	 * 获取代理信息
	 * @param userId 		员工id
	 * @param agentModules 代理模组ids
	 * @param startTime		代理开始时间
	 * @param endTime		代理结束时间
	 * @return
	 */
	public List<Agent> getAgents(int userId, List<String> agentModules, Timestamp startTime, Timestamp endTime) throws Exception{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("userId", userId);
		param.put("agentModules", agentModules);
		param.put("startTime", startTime);
		param.put("endTime", endTime);
		return this.getSqlMapClientTemplate().queryForList("agent.getAgents",param);
	}
	
	/**
	 * 获取当前代理信息
	 * @param userId			员工id
	 * @param agentModuleName	代理模组名
	 * @return
	 */
	public Agent getAgent(int userId, String agentModuleName) throws Exception{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("userId", userId);
		param.put("agentModuleName", agentModuleName);
		return (Agent)this.getSqlMapClientTemplate().queryForObject("agent.getAgent",param);
	}
	
	/**
	 * 获取当前代理信息
	 * @param userId			员工id
	 * @param agentModuleName	代理模组名
	 * @return
	 */
	public List<Agent> getAgentByAgentUserId(int userId, String agentModuleName) throws Exception{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("userId", userId);
		param.put("agentModuleName", agentModuleName);
		return (List<Agent>)this.getSqlMapClientTemplate().queryForList("agent.getAgentByAgentUserId",param);
	}

	/**
	 * 删除代理
	 * @param context
	 */
	public void deleteAgentById(Map<String, Object> param) throws Exception{
		this.getSqlMapClientTemplate().update("agent.deleteAgentById", param);
	}
	
}
