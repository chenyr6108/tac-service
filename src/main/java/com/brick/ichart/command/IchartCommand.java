package com.brick.ichart.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.base.command.BaseCommand;
import com.brick.ichart.service.IchartService;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;

public class IchartCommand extends BaseCommand {

	Log logger=LogFactory.getLog(IchartCommand.class);
	private IchartService ichartService;
	
	public IchartService getIchartService() {
		return ichartService;
	}

	public void setIchartService(IchartService ichartService) {
		this.ichartService = ichartService;
	}

	public void getDeptListForIchart(Context context) {
		Map<String,Object> outputMap=new HashMap<String,Object>();
		context.contextMap.put("decp_id","2");
		List deptList=ichartService.getDeptList(context.contextMap);
		outputMap.put("deptList",deptList);
		Output.jsonArrayOutput(deptList, context);
	}

}
