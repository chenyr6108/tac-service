/**
 * 
 */
package com.brick.report.command;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.command.BaseCommand;
import com.brick.report.service.ApprovedPercentReportService;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;
import com.brick.util.StringUtils;

/**
 * @author yangyun
 *
 */
public class ApprovedPercentReportCommand extends BaseCommand {
	
	private ApprovedPercentReportService appPercentService;
	
	public ApprovedPercentReportService getAppPercentService() {
		return appPercentService;
	}

	public void setAppPercentService(ApprovedPercentReportService appPercentService) {
		this.appPercentService = appPercentService;
	}

	public void getReport(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		String yearStr = (String) context.contextMap.get("year");
		if (StringUtils.isEmpty(yearStr)) {
			yearStr = DateUtil.getCurrentYear();
		}
		Integer year = Integer.parseInt(yearStr);
		try {
			List<Map<String, Object>> resutl = appPercentService.getReport(year);
			outputMap.put("year", yearStr);
			outputMap.put("resutl", resutl);
			Output.jspOutput(outputMap, context, "/report/approvedPercentReport.jsp");
		} catch (Exception e) {
			Output.errorPageOutput(e, context);
		}
	}
}
