package com.brick.riskSystem.command;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.brick.base.command.BaseCommand;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;

public class RiskControlCommand extends BaseCommand {

	public static final Logger log = Logger.getLogger(RiskControlCommand.class);
	
	public void riskControlShow(Context context){
		
		Map<String, Object> outputMap = new HashMap<String, Object>();
		List<Map<String, Object>> user2Rol = null;
		List<Map<String, Object>> rols = null;
		List<Map<String, Object>> users = null;
		try {
			context.contextMap.put("db", "待补");
			context.contextMap.put("cs", "测试");
			context.contextMap.put("xt", "系统");
			user2Rol = (List<Map<String, Object>>) DataAccessor.query("permission.getAllUser2Rol", context.contextMap, DataAccessor.RS_TYPE.LIST);
			rols = (List<Map<String, Object>>) DataAccessor.query("permission.getAllRol", context.contextMap, DataAccessor.RS_TYPE.LIST);
			users = (List<Map<String, Object>>) DataAccessor.query("permission.getAllUser", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			log.debug("com.brick.permission.service.PermissionService.getAllResource() " + e);
			e.printStackTrace();
		}
		outputMap.put("user2Rol", user2Rol);
		try {
			outputMap.put("MODIFY_DATE", 
					user2Rol != null && user2Rol.size() > 0 ? 
							DateUtil.dateToString(
									(Date) user2Rol.get(0).get("MODIFY_DATE"), 
									"yyyy-MM-dd HH:mm:ss SSS")
							: "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		outputMap.put("rols", rols);
		outputMap.put("users", users);
		outputMap.put("errorMsg", context.contextMap.get("errorMsg"));
		Output.jspOutput(outputMap, context, "/riskSystem/riskControlSetting.jsp");
	}

}
