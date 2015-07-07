package com.brick.base.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.service.DataSessionService;
import com.brick.base.to.DataSession;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;

public class DataSessionCommand extends BaseCommand {
	
	private DataSessionService dataSessionService;
	
	public DataSessionService getDataSessionService() {
		return dataSessionService;
	}

	public void setDataSessionService(DataSessionService dataSessionService) {
		this.dataSessionService = dataSessionService;
	}

	public void saveDataSession(Context context){
		System.out.println("================saveDataSession================");
		String inputData = (String) context.contextMap.get("inputData");
		String pageCode = (String) context.contextMap.get("pageCode");
		String user_id = String.valueOf(context.contextMap.get("s_employeeId"));
		String credit_id = (String) context.contextMap.get("credit_id");
		DataSession data = new DataSession();
		data.setUser_id(user_id);
		data.setCode_id(credit_id);
		data.setItem_html(inputData);
		data.setPage_code(pageCode);
		try {
			dataSessionService.doSaveDataSession(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String, Object> outputMap = new HashMap<String, Object>();
		outputMap.put("result", "test");
		Output.jsonOutput(outputMap, context);
	}
	
	/*public void saveDataSession(Context context){
		System.out.println("===============开始暂存=============");
		String bodyData = (String) context.contextMap.get("bodyData");
		String page_code = (String) context.contextMap.get("pageCode");
		String credit_id = (String) context.contextMap.get("credit_id");
		String user_id = String.valueOf(context.contextMap.get("s_employeeId"));
		System.out.println("===>>>pageCode");
		DataSession data = new DataSession();
		data.setUser_id(user_id);
		data.setItem_id(credit_id);
		data.setItem_html(bodyData);
		data.setPage_code(page_code);
		try {
			dataSessionService.doSaveDataSession(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String, Object> outputMap = new HashMap<String, Object>();
		outputMap.put("result", "test");
		Output.jsonOutput(outputMap, context);
	}*/
	
	public void getDataSession(Context context){
		String pageCode = (String) context.contextMap.get("pageCode");
		String credit_id = (String) context.contextMap.get("credit_id");
		String user_id = String.valueOf(context.contextMap.get("s_employeeId"));
		DataSession data = new DataSession();
		data.setUser_id(user_id);
		data.setCode_id(credit_id);
		data.setPage_code(pageCode);
		List<Map<String, String>> result = (List<Map<String, String>>) baseService.queryForList("businessSupport.getDataSessionByUserId", data);
		Output.jsonArrayListOutput(result, context);
	}
	
}
