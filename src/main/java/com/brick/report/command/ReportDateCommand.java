package com.brick.report.command;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.brick.base.command.BaseCommand;
import com.brick.base.to.ReportDateTo;
import com.brick.base.util.ReportDateUtil;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;

public class ReportDateCommand extends BaseCommand{
	
	public void getReportDate(Context context) throws Exception{
		
		Map outputMap = new HashMap();
		List<ReportDateTo> list = ReportDateUtil.getReportDateList();		
		outputMap.put("list", list);
		Output.jspOutput(outputMap, context, "/report/reportDateList.jsp");
	}

}
