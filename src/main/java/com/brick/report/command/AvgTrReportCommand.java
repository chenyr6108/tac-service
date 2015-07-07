package com.brick.report.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.command.BaseCommand;
import com.brick.base.to.ReportDateTo;
import com.brick.base.to.SelectionTo;
import com.brick.base.util.ReportDateUtil;
import com.brick.chartDirector.ChartDataSet;
import com.brick.chartDirector.ChartInfo;
import com.brick.chartDirector.ChartResult;
import com.brick.chartDirector.line.BaseLineChart;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;
import com.brick.util.StringUtils;

public class AvgTrReportCommand extends BaseCommand {
	
	/**
	 * 平均TR图
	 * @param context
	 * @throws Exception
	 */
	public void getReport(Context context) throws Exception{
		Map<String, Object> outputMap = new HashMap<String, Object>();
		//创建图
		ChartInfo chartInfo = new ChartInfo();
		chartInfo.chartWidth = 1000;
		chartInfo.chartHeight = 400;
		chartInfo.setAlertTitle("{dataSetName}{xLabel}的平均TR是{value}");
		//设置X轴
		chartInfo.setxLabel(new String[]{"1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月"});
		chartInfo.setChartName("avgTr");
		chartInfo.setChartTitle("平均TR走势图");
		chartInfo.setyTitle("平均TR");
		List<ChartDataSet> dataSets = new ArrayList<ChartDataSet>();
		ChartDataSet data = null;
		String year = (String) context.contextMap.get("year");
		String decp = (String) context.contextMap.get("decp");
		//报表类型
		String report_year = null;
		String report_decp = null;
		String busi_type = null;
		String code_type = null;
		if (StringUtils.isEmpty(year)) {
			year = DateUtil.getCurrentYear();
			decp = "全公司";
			report_decp = "";
			code_type = "1";
			report_year = DateUtil.getCurrentYear();
			data = getLineByOne(report_year, report_decp, busi_type, code_type);
			dataSets.add(data);
		} else {
			String[] ys = year.split(",");
			String[] ds = decp.split(",");
			for (int i = 0; i < ys.length; i++) {
				if ("全公司".equals(ds[i])) {
					report_decp = "";
				} else {
					report_decp = ds[i];
				}
				report_year = ys[i];
				code_type = "1";
				data = getLineByOne(report_year, report_decp, busi_type, code_type);
				dataSets.add(data);
			}
		}
		chartInfo.setChartDataList(dataSets);
		ChartResult chartResult = BaseLineChart.drawChart(context.request, chartInfo);
		outputMap.put("year", year);
		outputMap.put("decp", decp);
		outputMap.put("chartInfo", chartInfo);
		outputMap.put("chartResult", chartResult);
		outputMap.put("office", baseService.getAllOffice());
		Output.jspOutput(outputMap, context, "/report/avgTR_Report.jsp");
	}
	
	/**
	 * 平均TR表
	 * @param context
	 * @throws Exception
	 */
	public void getReportForTable(Context context) throws Exception{
		Map<String, Object> outputMap = new HashMap<String, Object>();
		List<ChartDataSet> dataSets = new ArrayList<ChartDataSet>();
		ChartDataSet data = null;
		String year = (String) context.contextMap.get("year");
		if (StringUtils.isEmpty(year)) {
			year = DateUtil.getCurrentYear();
		}
		String code_type = (String) context.contextMap.get("code_type");
		if (StringUtils.isEmpty(code_type)) {
			code_type = "1";
		}
		String busi_type = (String)context.contextMap.get("busi_type");
		for (SelectionTo o : baseService.getAllOffice()) {
			if("1".equals(busi_type) && !o.getDisplay_name().contains("设备")){
				continue;
			}
			if("2".equals(busi_type) && !o.getDisplay_name().contains("商用车")){
				continue;
			}
			if("3".equals(busi_type) && !o.getDisplay_name().contains("乘用车")){
				continue;
			}
			data = getLineByOneForTable(year, o.getOption_value()  , busi_type, code_type,o.getDisplay_name());
			dataSets.add(data);
		}
		dataSets.add(getLineByOneForTable(year, "", busi_type, code_type,""));
		outputMap.put("year", year);
		outputMap.put("code_type", code_type);
		outputMap.put("dataSets", dataSets);
		outputMap.put("busi_type", context.contextMap.get("busi_type"));
		Output.jspOutput(outputMap, context, "/report/avgTR_table.jsp");
	}
	
	public ChartDataSet getLineByOne(String year, String decp, String busi_type, String code_type){
		//通过办事处名称得到标识（页面无办事处标识）
		String decpId=getDecpIdByDecpName(decp);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("decp", decpId);
		paramMap.put("busi_type", busi_type);
		paramMap.put("code_type", code_type);
		ReportDateTo reportDate = null;
		ChartDataSet data = new ChartDataSet();
		double[] yData = new double[]{0D,0D,0D,0D,0D,0D,0D,0D,0D,0D,0D,0D};
		for (int i = 1; i <= yData.length; i++) {
			reportDate = ReportDateUtil.getDateByYearAndMonth(Integer.parseInt(year), i);
			paramMap.put("start_date", reportDate.getBeginTime());
			paramMap.put("end_date", reportDate.getEndTime());
			yData[i - 1] = (Double) baseService.queryForObj("report.getAvgTrReportByYearAndMonth", paramMap);
		}
		data.setTitle((StringUtils.isEmpty(decp) ? "全公司" : decp) + year + "年");
		data.setyData(yData);
		return data;
	}
	
	public ChartDataSet getLineByOneForTable(String year, String decp, String busi_type, String code_type,String decpName){
		//得到办事处名称
		 
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("decp", decp);
		paramMap.put("busi_type", busi_type);
		paramMap.put("code_type", code_type);
		ReportDateTo reportDate = null;
		ChartDataSet data = new ChartDataSet();
		double[] yData = new double[]{0D,0D,0D,0D,0D,0D,0D,0D,0D,0D,0D,0D,0D};
		for (int i = 1; i <= yData.length; i++) {
			if (i == 13) {
				reportDate = ReportDateUtil.getDateByYear(Integer.parseInt(year));
			} else {
				
				reportDate = ReportDateUtil.getDateByYearAndMonth(Integer.parseInt(year), i);
			}
			paramMap.put("start_date", reportDate.getBeginTime());
			paramMap.put("end_date", reportDate.getEndTime());
			yData[i - 1] = (Double) baseService.queryForObj("report.getAvgTrReportByYearAndMonth", paramMap);
			
		}
		data.setTitle(StringUtils.isEmpty(decpName) ? "合计" : decpName);
		data.setyData(yData);
		return data;
	} 
	//通过办事处名得到办事处Id
	public String getDecpIdByDecpName(String decpName){
		if(!decpName.equals("")){
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("decpName", decpName);
		    Map decpMap=(Map) baseService.queryForObj("report.getDecpIdByDecpName", paramMap);
		    String decpId= decpMap.get("DECP_ID")==null?" ":String.valueOf(decpMap.get("DECP_ID"));
		    return decpId;
		}else{
			
			return "";
		}
	}
}
