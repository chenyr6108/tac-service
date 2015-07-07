package com.brick.permission.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.log.service.LogPrint;
import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 角色管理
 * 
 * @author li shaojie
 * @date Apr 13, 2010
 */

public class RoleService extends BaseCommand {
	Log logger = LogFactory.getLog(RoleService.class);

	/**
	 * 添加角色
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void add(Context context) {
		List errList = context.errList;

		try {
			DataAccessor.execute("role.create", context.contextMap,
					DataAccessor.OPERATION_TYPE.INSERT);
		} catch (Exception e) {
			errList.add("添加角色失败！");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}

		Output.jspSendRedirect(context,
				"defaultDispatcher?__action=role.getAllRoles&__currentPage=1");
	}

	/**
	 * 查询所有可用角色
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getAllRoles(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;

		// 查询条件
		String content = null;
		if (context.contextMap.get("content") == null) {
			content = "";
		} else {
			content = (String) context.contextMap.get("content");
			content = content.trim();
		}
		DataWrap dw = null;
		try {
			dw = (DataWrap) DataAccessor.query("role.getAllRoles",
					context.contextMap, DataAccessor.RS_TYPE.PAGED);
		} catch (Exception e) {
			errList.add("查询失败！");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
		outputMap.put("dw", dw);
		outputMap.put("content", content);
		Output.jspOutput(outputMap, context, "/permission/roleManage.jsp");
	}

	/**
	 * 根绝角色id 查询该角色
	 * 
	 * @param context
	 */
	@SuppressWarnings("static-access")
	public void getRoleByID(Context context) {
		this.commonQuery("role.getRoleByID", context, DataAccessor.RS_TYPE.MAP,
				Output.OUTPUT_TYPE.JSON);
	}
	
	public void update(Context context){
		try {
			DataAccessor.execute("role.update", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Output.jspSendRedirect(context,
		"defaultDispatcher?__action=role.getAllRoles&__currentPage=1");
	}
	
	public void invalidRole(Context context){
		SqlMapClient sqlMap = DataAccessor.getSession();
		try {
			sqlMap.startTransaction();
			sqlMap.update("role.invalid", context.contextMap);
			sqlMap.delete("role.deleteRes2RolForRol", context.contextMap);
			sqlMap.delete("role.deleteUser2RolForRol", context.contextMap);
			baseService.insertActionLog(context, "角色作废", 
					"作废角色：[" + context.contextMap.get("roleName") + "],并删除对应的‘资源-角色’,‘人员-角色’权限明细。");
			sqlMap.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} finally {
			try {
				sqlMap.endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		Output.jspSendRedirect(context,
		"defaultDispatcher?__action=role.getAllRoles&__currentPage=1");
	}
	
	
	/**
	 * 获取该角色的所有配置信息
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void loadRoleForDeploy(Context context){
		Map outputMap=new HashMap();
		Map role=null; 
		DataWrap dw=null;
		try {
			// 批量删除
			String[] str = context.getRequest().getParameterValues("ids1");
			if (str!=null && str.length!=0) {
				for (int i = 0; i < str.length; i++) {
					Map map = new HashMap();
					map.put("roleId", context.contextMap.get("id"));
					map.put("resourceId", str[i]);
					DataAccessor.execute("role.invalidReource", map, DataAccessor.OPERATION_TYPE.UPDATE);
				}
			}
			String msg = "删除所选";
			outputMap.put("msg", msg);
			
			role=(Map)DataAccessor.query("role.getRoleByID", context.contextMap, DataAccessor.RS_TYPE.MAP);
			dw=(DataWrap) DataAccessor.query("permission.getAllResousesForDeploy", context.contextMap,  DataAccessor.RS_TYPE.PAGED);
		} catch (Exception e) {
			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("role", role);
		outputMap.put("dw", dw);
		outputMap.put("__action", context.contextMap.get("__action"));
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("cardFlag", context.contextMap.get("cardFlag"));
		Output.jspOutput(outputMap, context, "/permission/deployRole.jsp");
	}
	
	
	/**
	 * 删除资源角色关系
	 * @param context
	 */
	public void invalidResource(Context context){
		try {
			DataAccessor.execute("role.invalidReource", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
		} catch (Exception e) {
			 e.printStackTrace();
			 LogPrint.getLogStackTrace(e, logger);
		} 
		Output.jspSendRedirect(context, "defaultDispatcher?__action=role.loadRoleForDeploy&cardFlag=0&id="+context.contextMap.get("roleId"));
	}
	
	/**
	 * 查询所有未包含的权限
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void loadResourcesForDeploy(Context context){
		Map outputMap=new HashMap();
		Map role=null; 
		DataWrap dw=null;
		try {
			// 批量添加
			String[] str = context.getRequest().getParameterValues("ids2");
			if (str!=null && str.length!=0) {
				for (int i = 0; i < str.length; i++) {
					Map map = new HashMap();
					map.put("roleId", context.contextMap.get("id"));
					map.put("resourceId", str[i]);
					DataAccessor.execute("role.validReource", map, DataAccessor.OPERATION_TYPE.INSERT);
				}
			}
			String msg = "添加所选";
			outputMap.put("msg", msg);
			
			role=(Map)DataAccessor.query("role.getRoleByID", context.contextMap, DataAccessor.RS_TYPE.MAP);
			dw=(DataWrap) DataAccessor.query("permission.getAllResousesNoForDeploy", context.contextMap,  DataAccessor.RS_TYPE.PAGED);
		} catch (Exception e) {
			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("role", role);
		outputMap.put("dw", dw);
		outputMap.put("__action", context.contextMap.get("__action"));
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("cardFlag", context.contextMap.get("cardFlag"));
		Output.jspOutput(outputMap, context, "/permission/deployRole.jsp");
	}
	
	/**
	 * 添加资源角色关系
	 * @param context
	 */
	public void validResource(Context context){
		try {
			DataAccessor.execute("role.validReource", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
		} catch (Exception e) {
			 e.printStackTrace();
			 LogPrint.getLogStackTrace(e, logger);
		} 
		Output.jspSendRedirect(context, "defaultDispatcher?__action=role.loadResourcesForDeploy&cardFlag=1&id="+context.contextMap.get("roleId"));
	}
	
	/**
	 * 获取该角色的所有用户信息
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void loadUserForDeploy(Context context){
		Map outputMap=new HashMap();
		Map role=null; 
		DataWrap dw=null;
		List jobList = null;
		try {
			// 批量删除
			String[] str = context.getRequest().getParameterValues("ids3");
			if (str!=null && str.length!=0) {
				for (int i = 0; i < str.length; i++) {
					Map map = new HashMap();
					map.put("roleId", context.contextMap.get("id"));
					map.put("employeeId", str[i]);
					DataAccessor.execute("role.invalidUser2Role", map, DataAccessor.OPERATION_TYPE.UPDATE);
				}
			}
			String msg = "删除所选";
			outputMap.put("msg", msg);
			
			role=(Map)DataAccessor.query("role.getRoleByID", context.contextMap, DataAccessor.RS_TYPE.MAP);
			dw=(DataWrap) DataAccessor.query("role.loadUserForDeploy", context.contextMap,  DataAccessor.RS_TYPE.PAGED);
			context.contextMap.put("dataType", "员工职位");
			jobList = (List) DataAccessor.query("dataDictionary.queryDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("role", role);
		outputMap.put("dw", dw);
		outputMap.put("jobList", jobList);
		outputMap.put("__action", context.contextMap.get("__action"));
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("cardFlag", context.contextMap.get("cardFlag"));
		Output.jspOutput(outputMap, context, "/permission/deployRole.jsp");
	}
	
	/**
	 * 获取该角色的所有未包含的用户信息
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void loadUserNoForDeploy(Context context){
		Map outputMap=new HashMap();
		Map role=null; 
		DataWrap dw=null;
		List jobList = null;
		try {
			// 批量添加
			String[] str = context.getRequest().getParameterValues("ids4");
			if (str!=null && str.length!=0) {
				for (int i = 0; i < str.length; i++) {
					Map map = new HashMap();
					map.put("roleId", context.contextMap.get("id"));
					map.put("employeeId", str[i]);
					DataAccessor.execute("role.validUser2Role", map, DataAccessor.OPERATION_TYPE.INSERT);
				}
			}
			String msg = "添加所选";
			outputMap.put("msg", msg);
			
			
			role=(Map)DataAccessor.query("role.getRoleByID", context.contextMap, DataAccessor.RS_TYPE.MAP);
			dw=(DataWrap) DataAccessor.query("role.loadUserNoForDeploy", context.contextMap,  DataAccessor.RS_TYPE.PAGED);
			
			context.contextMap.put("dataType", "员工职位");
			jobList = (List) DataAccessor.query("dataDictionary.queryDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("jobList", jobList);
		outputMap.put("role", role);
		outputMap.put("dw", dw);
		outputMap.put("__action", context.contextMap.get("__action"));
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("cardFlag", context.contextMap.get("cardFlag"));
		Output.jspOutput(outputMap, context, "/permission/deployRole.jsp");
	}
	
	/**
	 * 添加资源角色关系
	 * @param context
	 */
	public void validUser2Role(Context context){
		try {
			DataAccessor.execute("role.validUser2Role", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
		} catch (Exception e) {
			 e.printStackTrace();
			 LogPrint.getLogStackTrace(e, logger);
		} 
		context.contextMap.put("cardFlag", 3);
		context.contextMap.put("id", context.contextMap.get("roleId"));
		this.loadUserNoForDeploy(context);
//		Output.jspSendRedirect(context, "defaultDispatcher?__action=role.loadUserNoForDeploy&cardFlag=3&id="+context.contextMap.get("roleId") + 
//				"&content=" + context.contextMap.get("content"));
	}
	
	/**
	 * 删除资源角色关系
	 * @param context
	 */
	public void invalidUser2Role(Context context){
		try {
			DataAccessor.execute("role.invalidUser2Role", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
		} catch (Exception e) {
			 e.printStackTrace();
			 LogPrint.getLogStackTrace(e, logger);
		} 
		Output.jspSendRedirect(context, "defaultDispatcher?__action=role.loadUserForDeploy&cardFlag=2&id="+context.contextMap.get("roleId"));
	}
}
