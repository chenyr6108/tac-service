package com.brick.chartDirector;

import java.util.List;

public class ChartDataSet {
	
	private String title;
	
	private double[] yData;

	//x坐标段
	private Double xDataStart;
	private Double xDataEnd;
	
	public double[] getyData() {
		return yData;
	}
	public void setyData(double[] yData) {
		this.yData = yData;
	}
	public void setyData(Double[] yData) {
		//封装Y轴数据
		double[] data = new double[yData.length];
		for (int i = 0; i < data.length; i++) {
			data[i] = yData[i] == null ? 0 : yData[i];
		}
		this.yData = data;
	}
	public void setyData(List<Double> yData) {
		Double[] data = {};
		data = yData.toArray(data);
		this.setyData(data);
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Double getxDataStart() {
		return xDataStart;
	}
	public void setxDataStart(Double xDataStart) {
		this.xDataStart = xDataStart;
	}
	public Double getxDataEnd() {
		return xDataEnd;
	}
	public void setxDataEnd(Double xDataEnd) {
		this.xDataEnd = xDataEnd;
	}
	
}
