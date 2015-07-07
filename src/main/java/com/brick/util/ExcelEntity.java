package com.brick.util;

import jxl.write.WritableCellFormat;

public class ExcelEntity 
{
	//字体样式(包括字体，大小,单元格边框,字体格式（居中....）)
	public WritableCellFormat format;
	
	//内容
	public String content;
	
	//跨几行
	public int rowSpan;
	
	//跨几列
	public int colSpan;
	
	//该信息显示在第几行
	public int row;
	
	//从第几列开始显示（跨列用到）
	public int col;
	
	
	public WritableCellFormat getFormat() {
		return format;
	}

	public void setFormat(WritableCellFormat format) {
		this.format = format;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public int getRowSpan() {
		return rowSpan;
	}

	public void setRowSpan(int rowSpan) {
		this.rowSpan = rowSpan;
	}
	
	public int getColSpan() {
		return colSpan;
	}

	public void setColSpan(int colSpan) {
		this.colSpan = colSpan;
	}
	
	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}
	
	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}
}
