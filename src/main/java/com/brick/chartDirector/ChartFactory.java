package com.brick.chartDirector;

import javax.servlet.http.HttpServletRequest;

import com.brick.chartDirector.cylinder.BaseCylinderChart;
import com.brick.chartDirector.line.BaseLineChart;

public class ChartFactory {
	
	public static ChartResult getLineChart(HttpServletRequest request, ChartInfo chartInfo) throws Exception{
		return BaseLineChart.drawChart(request, chartInfo);
	}
	
	public static ChartResult getCylinderChart(HttpServletRequest request, ChartInfo chartInfo) throws Exception{
		return BaseCylinderChart.drawChart(request, chartInfo);
	}
	
}
