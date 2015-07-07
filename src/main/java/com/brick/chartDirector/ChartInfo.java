package com.brick.chartDirector;

import java.util.List;

/**
 * 图表数据实体类
 * @author yangyun
 *
 */
public class ChartInfo {
	//图表宽高
	public int chartWidth = 500;
	public int chartHeight = 300;
	
	//图表标题
	private String chartTitle;
	
	//图表名称
	private String chartName = "DefaultChart";
	
	//线说明的字体
	public String lineFontType = "微软雅黑 Bold";
	
	//线说明的字型大小
	public int lineFontSize = 10;
	
	//图表标题字体
	public String chartTitleFontType = "微软雅黑 Bold";
	
	//图表标题字型大小
	public int chartTitleFontSize = 20;
	
	//Y坐标标题
	private String yTitle;
	
	//Y坐标标题字体
	public String yTitleFontType = "微软雅黑 Bold";
	
	//Y坐标标题字型大小
	public int yTitleFontSize = 12;
	
	//X坐标刻度字体
	public String xAxisFontType = "微软雅黑";
	
	//X坐标刻度字型大小
	public int xAxisFontSize = 8;
	
	//Y坐标刻度字体
	public String yAxisFontType = "微软雅黑";
	
	//Y坐标刻度字型大小
	public int yAxisFontSize = 8;
	
	//坐标刻度的密度
	public int xDensity = 40;
	public int yDensity = 20;
	
	//页面节点alert值
	private String alertTitle = "{value}";

	//x轴节点
	private String[] xLabel;
	//x轴节点文子角度（90为竖，0为横）
	public int xLableFontAngle = 90;
	
	//y轴节点
	//private String[] yLabel;
	
	//图表数据集
	private List<ChartDataSet> chartDataList;
	
	
	public String getChartTitle() {
		return chartTitle;
	}

	public void setChartTitle(String chartTitle) {
		this.chartTitle = chartTitle;
	}

	public String getChartName() {
		return chartName;
	}

	public void setChartName(String chartName) {
		this.chartName = chartName;
	}

	public String getyTitle() {
		return yTitle;
	}

	public void setyTitle(String yTitle) {
		this.yTitle = yTitle;
	}

	public String getAlertTitle() {
		return alertTitle;
	}

	/**
	 * 页面节点的alert值<br/>
	 * {x} = X轴的值<br/>
	 * {xLabel} = X轴的值<br/>
	 * {dataSetName} = 线的名称<br/>
	 * {value} = Y轴的值<br/>
	 * @param alertTitle
	 */
	public void setAlertTitle(String alertTitle) {
		this.alertTitle = alertTitle;
	}
	
	public void setXYAxisFontType(String fontType){
		this.xAxisFontType = fontType;
		this.yAxisFontType = fontType;
	}
	
	public void setXYAxisFontSize(int fontSize){
		this.xAxisFontSize = fontSize;
		this.yAxisFontSize = fontSize;
	}
	
	public void setXYAxisStyle(String fontType, int fontSize){
		setXYAxisFontType(fontType);
		setXYAxisFontSize(fontSize);
	}

	public List<ChartDataSet> getChartDataList() {
		return chartDataList;
	}

	public void setChartDataList(List<ChartDataSet> chartDataList) {
		this.chartDataList = chartDataList;
	}

	public String[] getxLabel() {
		return xLabel;
	}

	public void setxLabel(String[] xLabel) {
		this.xLabel = xLabel;
	}
	
	public void setxLabel(List<String> xLabel) {
		this.xLabel = xLabel.toArray(this.xLabel);
	}

	/*public String[] getyLabel() {
		return yLabel;
	}

	public void setyLabel(String[] yLabel) {
		this.yLabel = yLabel;
	}*/
}
