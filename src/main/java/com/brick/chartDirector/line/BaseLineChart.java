package com.brick.chartDirector.line;

import javax.servlet.http.HttpServletRequest;

import com.brick.chartDirector.ChartDataSet;
import com.brick.chartDirector.ChartInfo;
import com.brick.chartDirector.ChartResult;
import com.brick.util.StringUtils;

import ChartDirector.Chart;
import ChartDirector.Layer;
import ChartDirector.LegendBox;
import ChartDirector.PlotArea;
import ChartDirector.XYChart;

public class BaseLineChart {

	public static ChartResult drawChart(HttpServletRequest request, ChartInfo chartInfo) throws Exception {
		ChartResult chartResult = new ChartResult();
		//创建图标
		XYChart c = new XYChart(chartInfo.chartWidth, chartInfo.chartHeight, 0xeeeeff, 0x000000, 2);
//		c.setBackground(c.linearGradientColor(0, 0, 0, 100, 0x99ccff, 0xffffff), 0x888888);
		c.setRoundedFrame();
		c.setDropShadow();

		//图标标题
		c.addTitle(chartInfo.getChartTitle(), chartInfo.chartTitleFontType, chartInfo.chartTitleFontSize).setMargin2(0, 0, 10, 0);

		// Set the plotarea at (60, 80) and of 510 x 275 pixels in size. Use transparent
		// border and dark grey (444444) dotted grid lines
		//创建一个框，放在(60, 80)
		PlotArea plotArea = c.setPlotArea(70, 80, c.getWidth() - 55*2, c.getHeight() - 80*2, -1, -1, Chart.Transparent, c.dashLineColor(0x444444, 0x0101), -1);
		
		//线的说明框
		LegendBox legendBox = c.addLegend(plotArea.getLeftX(), 45, false, chartInfo.lineFontType, chartInfo.lineFontSize);
//		legendBox.setAlignment(Chart.TopRight);
		legendBox.setBackground(Chart.Transparent, Chart.Transparent);

		//xy轴的密度
		//c.yAxis().setTickDensity(plotArea.getHeight() / 20);
		//c.xAxis().setTickDensity(chartInfo.xDensity);

		//xy轴颜色，默认设置成透明
		c.xAxis().setColors(Chart.Transparent);

		c.yAxis().setColors(Chart.Transparent);
		
		// Set the x-axis margins to 15 pixels, so that the horizontal grid lines can extend
		// beyond the leftmost and rightmost vertical grid lines
		c.xAxis().setMargin(10, 10);

		//设置xy轴节点的字体和大小
		c.xAxis().setLabelStyle(chartInfo.xAxisFontType, chartInfo.xAxisFontSize);
		c.yAxis().setLabelStyle(chartInfo.yAxisFontType, chartInfo.yAxisFontSize);
		//c.yAxis2().setLabelStyle("Arial Bold", 8);

		//设置y轴说明
		c.yAxis().setTitle(chartInfo.getyTitle(), chartInfo.yTitleFontType, chartInfo.yTitleFontSize);

		//设置x轴数据，并设置x轴文子的角度
		if (chartInfo.getxLabel() == null) {
			throw new Exception("没有图表的x轴数据，请设置(xLabel)");
		} else {
			c.xAxis().setLabels(chartInfo.getxLabel()).setFontAngle(chartInfo.xLableFontAngle);
			c.xAxis().setLabelStyle(chartInfo.xAxisFontType, chartInfo.xAxisFontSize).setFontAngle(45);
			c.yAxis().setLabelFormat("{value|,}");
		}
		
		/*Mark mark = c.xAxis2().addMark(5, 0x809933ff, "今天", "华文楷体");*/

		/*// Set the mark line width to 2 pixels
		mark.setLineWidth(2);
		mark.setValue(5);
		//mark.setMargin(10,0,0,0);
		// Set the mark label font color to purple (0x9933ff)
		mark.setFontColor(0x9933ff);*/


		
		//设置y轴
		Layer layer = c.addLineLayer2();
		for (ChartDataSet dataSet : chartInfo.getChartDataList()) {
			//如果数据集中的数据位空，则跳过
			if(dataSet == null || dataSet.getyData() == null){
				continue;
			}
			
			//数据集中的x轴为空则x轴为默认，
			//如果有设置x轴数据，则表示线的开始和结束有变化需要重新开一个layer
			if (dataSet.getxDataStart() != null) {
				layer = c.addLineLayer2();
			}
			
			//设定Y周数据、 颜色（-1是自动分配颜色）、标题，并设置节点形状和大小
			layer.addDataSet(dataSet.getyData(), -1, dataSet.getTitle()).setDataSymbol(Chart.CircleShape, 8);
			
			//如果有x轴的数据则要设置x轴
			layer.setXData2(dataSet.getxDataStart() == null ? 0 : dataSet.getxDataStart(), 
					dataSet.getxDataEnd() == null ? c.xAxis().getMaxValue() : dataSet.getxDataEnd());
			
			//设施线的粗细
			layer.setLineWidth(2);
		}

		// Output the chart
		if (StringUtils.isEmpty(chartInfo.getChartName())) {
			throw new Exception("没有图表的名称，请设置(chartName)");
		}
		String chartURL = c.makeSession(request, chartInfo.getChartName());

		// Include tool tip for the chart
		String imageMap = c.getHTMLImageMap("", "", "title='" + chartInfo.getAlertTitle() + "'");
		chartResult.setChartURL(chartURL);
		chartResult.setImageMap(imageMap);
		chartResult.setChartName(chartInfo.getChartName());
		return chartResult;
	}
	
}
