package com.brick.permission.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;

/**
 * 用户登录，权限分配
 * 
 * @author li shaojie
 * @date Apr 14, 2010
 */

public class AssignmentPermission extends AService {
	Log logger = LogFactory.getLog(AssignmentPermission.class);

	/**
	 * 根据登录的用户编号，获取该用户的所有权限
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void assignmentPermission(Context context) {
		context.contextMap.put("employeeId", context.request.getSession().getAttribute("s_employeeId"));
		List firstMenuList=null;
		try {
			firstMenuList=(List)DataAccessor.query("assignmentPermission.getMenuList",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		
		context.request.getSession().setAttribute("firstMenuList", firstMenuList);
		Output.jspSendRedirect( context, "../frame/main.jsp");
	}
	
	@SuppressWarnings("unchecked")
	public void getSecondMenu(Context context){
		Map outputMap=new HashMap();
		context.contextMap.put("employeeId", context.request.getSession().getAttribute("s_employeeId"));
		List secondMenuList=null;
		try {
			secondMenuList=(List)DataAccessor.query("assignmentPermission.getSecondMenu",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("secondMenuList", secondMenuList);
		Output.jspOutput(outputMap, context, "/frame/left.jsp");
	}
	
	/**
	 * 获取所有菜单列表
	 * @param context
	 */
	public void getAllMenuWithPermission(Context context) {
		Map outputMap=new HashMap();
		context.contextMap.put("employeeId", context.request.getSession().getAttribute("s_employeeId"));
		List allMenuList=null;
		try {
			allMenuList=(List)DataAccessor.query("assignmentPermission.getMenuListAll",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

		outputMap.put("allMenuList", allMenuList);
		Output.jspOutput(outputMap, context, "/sysMenuList/sysMenuListView.jsp");
	}
}
