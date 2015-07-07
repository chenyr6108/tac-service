package com.brick.report.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.to.PagingInfo;
import com.brick.base.to.ReportDateTo;
import com.brick.base.util.LeaseUtil;
import com.brick.base.util.ReportDateUtil;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;
import com.brick.util.StringUtils;

public class InsuranceForAccountantReportCommand extends BaseCommand {
	
	Log logger = LogFactory.getLog(InsuranceForAccountantReportCommand.class);
	
	public void getReportData(Context context) throws Exception{
		Map outputMap = new HashMap();
		List errlist = context.errList;
		PagingInfo<Object> dw = null;
		String date = (String) context.contextMap.get("date");
		String report_type = (String) context.contextMap.get("report_type");
		String year = null;
		String month = null;
		try {
			if (StringUtils.isEmpty(date)) {
				date = DateUtil.getCurrentYearMonth();
			}
			if (StringUtils.isEmpty(report_type)) {
				report_type = "1";
			}
			String[] dateInfo = date.split("-");
			if (dateInfo.length != 2) {
				year = DateUtil.getCurrentYear();
				month = DateUtil.getCurrentMonth();
			} else {
				year = dateInfo[0];
				month = dateInfo[1];
			}
			ReportDateTo reportDate = ReportDateUtil.getDateByYearAndMonth(Integer.parseInt(year), Integer.parseInt(month));
			context.contextMap.put("start_date", reportDate.getBeginTime());
			context.contextMap.put("end_date", reportDate.getEndTime());
			if ("1".equals(report_type)) {
				dw = baseService.queryForListWithPaging("report.getInsuDataForAcc", context.contextMap, "LEASE_CODE");
			} else if ("2".equals(report_type)) {
				dw = baseService.queryForListWithPaging("report.getSettleDataForAcc", context.contextMap, "SETTLE_DATE");
			}
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
		outputMap.put("dw", dw);
		outputMap.put("date", date);
		outputMap.put("report_type", report_type);
		outputMap.put("companyCode", context.contextMap.get("companyCode"));
		outputMap.put("companys", LeaseUtil.getCompanys());	
		Output.jspOutput(outputMap, context, "/report/insuranceForAccountantReport.jsp");
	}
}
