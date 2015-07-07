package com.brick.log.command;

import java.util.HashMap;
import java.util.Map;

import com.brick.base.command.BaseCommand;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;

public class ActionLogCommand extends BaseCommand {
	
	public void getAllLog(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		PagingInfo<Object> pagingInfo = null;
		try {
			pagingInfo = baseService.queryForListWithPaging("actionLog.getAllLog", context.contextMap, "CREATE_DATE", ORDER_TYPE.DESC);
		} catch (Exception e) {
			context.contextMap.put("errorMsg", e.getMessage());
			e.printStackTrace();
		}
		outputMap.put("search_content", context.contextMap.get("search_content"));
		outputMap.put("errorMsg", context.contextMap.get("errorMsg"));
		outputMap.put("pagingInfo", pagingInfo);
		Output.jspOutput(outputMap, context, "/actionLog/actionLogShow.jsp");
	}
}
