package com.tac.agent.command;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.brick.base.command.BaseCommand;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;
import com.brick.util.web.JsonUtils;
import com.tac.agent.service.AgentService;
import com.tac.agent.to.Agent;

public class AgentCommand extends BaseCommand{
	public AgentService agentService;

	public void setAgentService(AgentService agentService) {
		this.agentService = agentService;
	}
	
	/**
	 * 查询页面，查询所有代理信息
	 * @param context
	 * @return
	 */
	public void getAllAgents(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		PagingInfo<Object> dw = null;
		List<Integer> agentModuleList = null;
		List<Integer> statusList = new ArrayList<Integer>();
		//代理模组管理员权限
		boolean manageRole = false;
		//所有模组
		List<Map> dictionary = null;
		//所有用户
		List<Map<String,String>> users = new ArrayList<Map<String,String>>();
		try {
			manageRole = baseService.checkAccessForResource("agentManager", String.valueOf(context.contextMap.get("s_employeeId")));
			if(!manageRole){
				context.contextMap.put("currentUserId", context.contextMap.get("s_employeeId"));
			}
			//查询代理模组
			String[] agentModules = context.getRequest().getParameterValues("agentModule[]");
			if(agentModules != null && agentModules.length > 0){
				agentModuleList = new ArrayList<Integer>();
				for(String id : agentModules){
					agentModuleList.add(Integer.parseInt(id));
				}
			}
			dw = baseService.queryForListWithPaging("agent.getAllAgent", context.contextMap, "id", ORDER_TYPE.DESC);
			
			//所有用户
			List<Map> allUsers = (List<Map>)DataAccessor.query("demand.getAllUsers",context.contextMap, DataAccessor.RS_TYPE.LIST);
			Map<String, String> tempUser = null;
			for(Map u : allUsers){
				tempUser = new HashMap<String, String>();
				tempUser.put("id", u.get("ID").toString());
				tempUser.put("name", u.get("NAME").toString());
				tempUser.put("email", u.get("EMAIL")==null?"":u.get("EMAIL").toString());
				users.add(tempUser);
			}
			dictionary = (List<Map>) DictionaryUtil.getDictionary("代理模组");
		} catch (Exception e) {
			e.printStackTrace();
		}
		outputMap.put("manageRole", manageRole);
		outputMap.put("dictionary", dictionary);
		outputMap.put("users", JsonUtils.list2json(users));
		outputMap.put("dw", dw);
		outputMap.put("agentModuleList", agentModuleList);
		outputMap.put("dateBegin", context.contextMap.get("dateBegin"));
		outputMap.put("dateEnd", context.contextMap.get("dateEnd"));
		outputMap.put("status", context.contextMap.get("status"));
		outputMap.put("userName", context.contextMap.get("userName"));
		outputMap.put("agentUserName", context.contextMap.get("agentUserName"));
		Output.jspOutput(outputMap, context, "/agent/queryAgent.jsp");
	}
	
	/**
	 * 添加代理信息
	 * @param context
	 */
	public void addAgent(Context context){
		String msg = "0";
		try {
			this.agentService.addAgent(context);
			msg = context.contextMap.get("msg")==null?"0":context.contextMap.get("msg").toString();
		} catch (Exception e) {
			msg = "1";
			e.printStackTrace();
		}
		Output.txtOutput(msg, context);
	}
	
	/**
	 * 验证代理信息是否重复
	 * @param context
	 */
	public void checkAgentRepeat(Context context){
		//重复的代理信息
		List<Agent> repeatAgents = null;
		try {
			int userId = Integer.parseInt(context.contextMap.get("userId").toString());
			Timestamp startTime = new Timestamp(DateUtil.strToDate(context.contextMap.get("agentStartTime").toString(), "yyyy-MM-dd HH:mm").getTime());
			Timestamp endTime = new Timestamp(DateUtil.strToDate(context.contextMap.get("agentEndTime").toString(), "yyyy-MM-dd HH:mm").getTime());
			String agentModules = context.contextMap.get("agentModules").toString();
			List<String> agentModuleIds = new ArrayList<String>();
			for(String m : agentModules.split(",")){

				agentModuleIds.add(m);
			}
			repeatAgents = this.agentService.checkRepeatAgent(userId, agentModuleIds, startTime, endTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Output.jsonArrayOutputForList(repeatAgents, context);
	}
	
	/**
	 * 删除代理信息
	 * @param context
	 */
	public void deleteAgentById(Context context){
		//处理结果返回信息
		String msg = "0";
		try {
			this.agentService.deleteAgentById(context);
		} catch (Exception e) {
			msg = "1";
			e.printStackTrace();
		}
		Output.txtOutput(msg, context);
	}
	
}
