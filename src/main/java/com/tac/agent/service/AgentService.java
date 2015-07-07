package com.tac.agent.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.brick.base.service.BaseService;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;
import com.tac.agent.dao.AgentDao;
import com.tac.agent.to.Agent;

public class AgentService extends BaseService {
	public AgentDao agentDao;
	
	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}

	/**
	 * 根据用户id获取代理对象
	 * @param userId			用户id
	 * @param agentModuleId 	代理模组id
	 * @return					null为没有代理
	 */
	public Agent getAgentByUserIdAndModuleId(int userId, String agentModuleId) throws Exception{
		return this.agentDao.getAgentByUserIdAndModuleId(userId, agentModuleId);
	}
	
	/**
	 * 根据条件查询所有代理信息
	 * @param param 查询条件
	 * @return
	 */
	public List<Agent> getAllAgent(Map<String, Object> param) throws Exception{
		return this.agentDao.getAllAgent(param);
	}
	
	/**
	 * 添加代理信息
	 * @param agent
	 */
	public void addAgent(Agent agent) throws Exception{
		this.agentDao.addAgent(agent);
	}
	
	/**
	 * 更新代理状态
	 * @param agentId 	代理信息id
	 * @param status	代理状态
	 */
	public void updateAgentStatus(int agentId, int status) throws Exception{
		this.agentDao.updateAgentStatus(agentId, status);
	}
	
	/**
	 * 更新代理信息
	 * @param agent		更新后的agent
	 */
	public void updateAgent(Agent agent) throws Exception{
		this.agentDao.updateAgent(agent);
	}
	
	/**
	 * 根据查询条件检查重复的代理信息
	 * @param userId 		用户id
	 * @param agentModules	代理模组ids
	 * @param startTime		代理开始时间
	 * @param endTime		代理结束时间
	 * @return
	 */
	public List<Agent> checkRepeatAgent(int userId, List<String> agentModules, Timestamp startTime, Timestamp endTime) throws Exception{
		return this.agentDao.getAgents(userId, agentModules, startTime, endTime);
	}
	
	/**
	 * 根据当前代理信息
	 * @param userId			被代理人id
	 * @param agentModuleName	代理模组名
	 * @return
	 */
	public Agent getAgent(int userId, String agentModuleName) throws Exception {
		return this.agentDao.getAgent(userId, agentModuleName);
	}
	
	/**
	 * 根据当前代理信息
	 * @param userId			代理人id
	 * @param agentModuleName	代理模组名
	 * @return
	 */
	public List<Agent> getAgentByAgentUserId(int userId, String agentModuleName) throws Exception {
		return (List<Agent>)this.agentDao.getAgentByAgentUserId(userId, agentModuleName);
	}

	/**
	 * 删除代理
	 * @param context
	 */
	public void deleteAgentById(Context context) throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", context.contextMap.get("agentId"));
		param.put("updateUserId", context.contextMap.get("s_employeeId"));
		this.agentDao.deleteAgentById(param);
	}

	/**
	 * 添加代理
	 * @param context
	 */
	@Transactional(rollbackFor = Exception.class) 
	public void addAgent(Context context) throws Exception{
		Map<String, Object> params = new HashMap<String, Object>();
		//处理结果返回信息
		String msg = "0";
		//重复的代理信息
		List<Agent> repeatAgents = null;
		//查询代理模组
		if(context.contextMap.get("agentModules") != null){
			int userId = Integer.parseInt(context.contextMap.get("userId").toString());
			int agentUserId = Integer.parseInt(context.contextMap.get("agentId").toString());
			Timestamp startTime = new Timestamp(DateUtil.strToDate(context.contextMap.get("agentStartTime").toString(), "yyyy-MM-dd HH:mm").getTime());
			Timestamp endTime = new Timestamp(DateUtil.strToDate(context.contextMap.get("agentEndTime").toString(), "yyyy-MM-dd HH:mm").getTime());
			String agentModules = context.contextMap.get("agentModules").toString();
			List<String> agentModuleIds = new ArrayList<String>();
			for(String m : agentModules.split(",")){
				agentModuleIds.add(m);
			}
			//验证是否重复
			repeatAgents = this.checkRepeatAgent(userId, agentModuleIds, startTime, endTime);
			if(repeatAgents == null || repeatAgents.size() == 0){
				Agent agent = new Agent();
				agent.setCreateUserId(Integer.parseInt(context.contextMap.get("s_employeeId").toString()));
				agent.setUserId(userId);
				agent.setAgentUserId(agentUserId);
				agent.setStartTime(startTime);
				agent.setEndTime(endTime);
				agent.setRemark(context.contextMap.get("agentRemark")==null?"":new String(context.contextMap.get("agentRemark").toString().getBytes("ISO-8859-1"),"UTF-8"));
				for(String m : agentModuleIds){
					agent.setAgentModuleId(m);
					this.addAgent(agent);
				}
			} else {
				msg = "2";
			}
		}
		context.contextMap.put("msg", msg);
	}
}
