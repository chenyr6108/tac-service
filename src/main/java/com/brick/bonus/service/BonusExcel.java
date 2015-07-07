package com.brick.bonus.service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.servlet.ServletOutputStream;

import jxl.format.Alignment;
import jxl.format.VerticalAlignment;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.NumberFormats;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.log.service.LogPrint;
import com.brick.report.service.ReportExcel;
import com.brick.service.entity.Context;
import com.brick.util.ExcelEntity;
import com.brick.util.PublicExcel;


public class BonusExcel 
{
	Log logger = LogFactory.getLog(BonusExcel.class);
	String excelName=null;//该Excel导出文件名
	String excelHead=null;//该Excel头部信息
	int cell=0;//该Excel列数
	int row=0;//该Excel头部所占的行数
	WritableCellFormat format1=null;//该Excel列表的通用样式
	
	
	public void bonusExcelJoin(List bonus,Context context)
	{
		
		
		
		ByteArrayOutputStream baos = null;
		excelName="奖金来款上传模板.xls";
		excelHead="奖金来款上传模板";
		
		row=2;
		format1=this.tableFont();
		
		int colNum=bonus.size();
		cell=colNum+3;
		
		List cellList=this.cellSome(colNum);
		List titleList=this.titleListMethod(bonus);
		List contentList=this.contextListMethod(context);
		
		
		PublicExcel exl = new PublicExcel();
		exl.createexl(); 
		baos = exl.export1(excelName,excelHead,cell,row,format1,cellList,titleList,contentList);
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(excelName.getBytes("GBK"), "ISO-8859-1"));
			ServletOutputStream out1 = context.response.getOutputStream();
			exl.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {
			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	
	
	//设置该Excel有和每一列的宽度,cellList的长度和cell（多少列）相同
	public List cellSome(int colNum)
	{
		List cellList=new ArrayList();
		cellList.add(20);
		cellList.add(15);
		
		for(int i=0;i<colNum;i++)
		{
			cellList.add(15);
		}
		
		
		cellList.add(25);
		return cellList;
	}
	
	
	//设置标题
	public List titleListMethod(List bouns)
	{
		List titleList=new ArrayList();
		
		
		
		
		//format(样式)
		//colspan(int)跨几列
		//rowspan(int)跨几行
		//content(String)内容
		//row(int)显示在第几行（有的标题有两行，说明该信息显示在第几行,从0开始算起）
		//col(int)显示在第几列（跨行时用到,从0开始算起）
		
		WritableCellFormat format=this.tableFont();//标题通用样式
		WritableCellFormat format1=this.tableFont2();//标题通用样式
		ExcelEntity col0=new ExcelEntity();
		col0.setFormat(format);
		col0.setRowSpan(0);
		col0.setColSpan(bouns.size()+2);
		col0.setRow(0);
		col0.setCol(0);
		col0.setContent("奖金来款上传");
		titleList.add(col0);
		
		//第一行
		//标题第一列（跨一行,一列,居中显示在第一行）
		//合同号
		ExcelEntity col1=new ExcelEntity();
		col1.setFormat(format);
		col1.setRowSpan(0);
		col1.setColSpan(0);
		col1.setRow(1);
		col1.setCol(0);
		col1.setContent("合同号");
		titleList.add(col1);
		
		//标题第二列（跨一行,一列,居中显示在第一行）
		//姓名
		ExcelEntity col2=new ExcelEntity();
		col2.setFormat(format);
		col2.setRowSpan(0);
		col2.setColSpan(0);
		col2.setRow(1);
		col2.setCol(1);
		col2.setContent("姓名");
		titleList.add(col2);
		
		//项
		for(int i=0;i<bouns.size();i++)
		{
			Map bounsMap=(Map)bouns.get(i);
			ExcelEntity col3=new ExcelEntity();
			col3.setFormat(format);
			col3.setRowSpan(0);
			col3.setColSpan(0);
			col3.setRow(1);
			col3.setCol(i+2);
			col3.setContent((String)bounsMap.get("BONUS_NAME"));
			titleList.add(col3);
		}
		
		
		//备注
		ExcelEntity col4=new ExcelEntity();
		col4.setFormat(format);
		col4.setRowSpan(0);
		col4.setColSpan(0);
		col4.setRow(1);
		col4.setCol(bouns.size()+2);
		col4.setContent("备注");
		titleList.add(col4);
		
		return titleList;
	}
	
	
	//设置内容
	public List contextListMethod(Context context)
	{
		List content = (List) context.contextMap.get("content") ;
		List contextList=new ArrayList();
		WritableCellFormat format=this.tableFont1();//标题通用样式
		WritableCellFormat   contentFromart   =   new   WritableCellFormat(NumberFormats.TEXT); 
		
//		for(int i=0;i<content.size();i++)
//		{
//			Map temp = (Map) content.get(i) ;
//			ExcelEntity col4=new ExcelEntity();
//			col4.setFormat(contentFromart);
//			col4.setRowSpan(0);
//			col4.setColSpan(0);
//			col4.setRow(2+i);
//			col4.setCol(0);
//			col4.setContent(temp.get("LEASE_CODE") == null ? "" : temp.get("LEASE_CODE").toString());
//			contextList.add(col4);
//			
//			col4=new ExcelEntity();
//			col4.setFormat(contentFromart);
//			col4.setRowSpan(0);
//			col4.setColSpan(0);
//			col4.setRow(2+i);
//			col4.setCol(1);
//			col4.setContent(temp.get("NAME") == null ? "" : temp.get("NAME").toString());
//			contextList.add(col4);
//		}
//		for(int i=0;i<300;i++)
//		{
//			ExcelEntity col4=new ExcelEntity();
//			col4.setFormat(contentFromart);
//			col4.setRowSpan(0);
//			col4.setColSpan(0);
//			col4.setRow(2+i);
//			col4.setCol(0);
//			
//			col4.setContent("");
//			contextList.add(col4);
//		}
		return contextList;
	}
	
	//设置样式（字体大小，颜色，边框样式）//标题通用样式（参数后在改进）
	public WritableCellFormat tableFont()
	{
		WritableFont font1 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
		WritableCellFormat format = new WritableCellFormat(font1);
		try {
			
			format.setAlignment(Alignment.CENTRE);
			format.setVerticalAlignment(VerticalAlignment.CENTRE);
			//format.setBorder(Border.ALL, BorderLineStyle.THIN);
			format.setWrap(true);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		
		return format;
	}
	
	public WritableCellFormat tableFont1()
	{
		WritableFont font1 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
		WritableCellFormat format = new WritableCellFormat(font1);
		try {
			
			format.setAlignment(Alignment.RIGHT);
			format.setVerticalAlignment(VerticalAlignment.CENTRE);
			//format.setBorder(Border.ALL, BorderLineStyle.THIN);
			format.setWrap(true);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		
		return format;
	}
	
	
	
	public WritableCellFormat tableFont2()
	{
		WritableFont font1 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.BOLD);
		WritableCellFormat format = new WritableCellFormat(font1);
		try {
			
			format.setAlignment(Alignment.RIGHT);
			format.setVerticalAlignment(VerticalAlignment.CENTRE);
			//format.setBorder(Border.ALL, BorderLineStyle.THIN);
			format.setWrap(true);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		
		return format;
	}
}
