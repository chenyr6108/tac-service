package com.brick.ichart.to;

import java.util.List;

import com.brick.base.to.BaseTo;


public class IchartTo extends BaseTo{
	
	private static final long serialVersionUID = 1L;
	
	private String name; //曲线图名称
	
	private List<Double>  value ;//某曲线图值的集合
	
	private String color; //曲线图颜色
	
	private Integer line_width ;//线条的宽度，与页面原点大小相关
	
	

	public Integer getLine_width() {
		return line_width;
	}

	public void setLine_width(Integer line_width) {
		this.line_width = line_width;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Double> getValue() {
		return value;
	}

	public void setValue(List<Double> value) {
		this.value = value;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	
	

}
