package com.brick.contract.service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;

import jxl.format.Alignment;
import jxl.format.VerticalAlignment;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WriteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.log.service.LogPrint;
import com.brick.service.entity.Context;
import com.brick.util.ExcelEntity;
import com.brick.util.PublicExcel;


public class ContractExcel 
{
	Log logger = LogFactory.getLog(ContractExcel.class);
	String excelName=null;//该Excel导出文件名
	String excelHead=null;//该Excel头部信息
	int cell=0;//该Excel列数
	int row=0;//该Excel头部所占的行数
	WritableCellFormat format1=null;//该Excel列表的通用样式
	
	
	@SuppressWarnings("unchecked")
	public void ContractExcelJoin(List contract,Context context)
	{
		
		
		
		ByteArrayOutputStream baos = null;
		excelName="合同资料.xls";
		excelHead="合同资料";
		
		row=2;
		format1=this.tableFont();
		
		int colNum=contract.size();
		cell=colNum ;
		
		List cellList=this.cellSome(colNum);
		List titleList=this.titleListMethod(contract);
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
	@SuppressWarnings("unchecked")
	public List cellSome(int colNum)
	{
		List cellList=new ArrayList();
		cellList.add(20);//合同号
		cellList.add(25);//承租人名称
		cellList.add(10);//省
		cellList.add(10);//市
		cellList.add(10);//区
		cellList.add(10);//区域主管
		cellList.add(10);//客户经理
		cellList.add(10);//实际TR
		cellList.add(15);//未税总金额
		cellList.add(15);//保证金
		cellList.add(10);//客户TR
		cellList.add(5);//期数
		cellList.add(15);//起租日期
		cellList.add(15);//评审通过
		cellList.add(15);//实际拨款日
		cellList.add(15);//利差
		return cellList;
	}
	
	
	//设置标题
	@SuppressWarnings("unchecked")
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
		col0.setColSpan(bouns.size());
		col0.setRow(0);
		col0.setCol(0);
		col0.setContent("合同资料");
		titleList.add(col0);
		
		//设置标题
		for(int i=0;i<bouns.size();i++)
		{
			ExcelEntity col3=new ExcelEntity();
			col3.setFormat(format);
			col3.setRowSpan(0);
			col3.setColSpan(0);
			col3.setRow(1);
			col3.setCol(i);
			col3.setContent((String)bouns.get(i));
			titleList.add(col3);
		}
		return titleList;
	}
	
	
	//设置内容
	@SuppressWarnings("unchecked")
	public List contextListMethod(Context context)
	{
		List content = (List) context.contextMap.get("content") ;
		List contextList=new ArrayList();
		WritableCellFormat format=this.tableFont1();//标题通用样式
		WritableCellFormat   contentFromart   =   new   WritableCellFormat(NumberFormats.TEXT); 
		for(int i=0;i<content.size();i++)
		{
			Map temp = (Map) content.get(i) ;
			ExcelEntity col4=new ExcelEntity();
			col4.setFormat(format1);
			col4.setRowSpan(0);
			col4.setColSpan(0);
			col4.setRow(2+i);
			col4.setCol(0);
			col4.setContent(temp.get("HTH") == null ? "" : temp.get("HTH").toString());
			contextList.add(col4);
			
			col4=new ExcelEntity();
			col4.setFormat(format1);
			col4.setRowSpan(0);
			col4.setColSpan(0);
			col4.setRow(2+i);
			col4.setCol(1);
			col4.setContent(temp.get("CZR") == null ? "" : temp.get("CZR").toString());
			contextList.add(col4);
			
			col4=new ExcelEntity();
			col4.setFormat(format1);
			col4.setRowSpan(0);
			col4.setColSpan(0);
			col4.setRow(2+i);
			col4.setCol(2);
			col4.setContent(temp.get("SHENG") == null ? "" : temp.get("SHENG").toString());
			contextList.add(col4);
			
			col4=new ExcelEntity();
			col4.setFormat(format1);
			col4.setRowSpan(0);
			col4.setColSpan(0);
			col4.setRow(2+i);
			col4.setCol(3);
			col4.setContent(temp.get("SHI") == null ? "" : temp.get("SHI").toString());
			contextList.add(col4);
			
			col4=new ExcelEntity();
			col4.setFormat(format1);
			col4.setRowSpan(0);
			col4.setColSpan(0);
			col4.setRow(2+i);
			col4.setCol(4);
			col4.setContent(temp.get("QU") == null ? "" : temp.get("QU").toString());
			contextList.add(col4);
			
			col4=new ExcelEntity();
			col4.setFormat(format1);
			col4.setRowSpan(0);
			col4.setColSpan(0);
			col4.setRow(2+i);
			col4.setCol(5);
			col4.setContent(temp.get("ZHUGUAN") == null ? "" : temp.get("ZHUGUAN").toString());
			contextList.add(col4);
			
			col4=new ExcelEntity();
			col4.setFormat(format1);
			col4.setRowSpan(0);
			col4.setColSpan(0);
			col4.setRow(2+i);
			col4.setCol(6);
			col4.setContent(temp.get("YWY") == null ? "" : temp.get("YWY").toString());
			contextList.add(col4);
			
			col4=new ExcelEntity();
			col4.setFormat(format);
			col4.setRowSpan(0);
			col4.setColSpan(0);
			col4.setRow(2+i);
			col4.setCol(7);
			col4.setContent(temp.get("SJTR") == null ? "" : temp.get("SJTR").toString()+"%");
			contextList.add(col4);
			
			col4=new ExcelEntity();
			col4.setFormat(format);
			col4.setRowSpan(0);
			col4.setColSpan(0);
			col4.setRow(2+i);
			col4.setCol(8);
			col4.setContent(temp.get("WSZJE") == null ? "" : temp.get("WSZJE").toString());
			contextList.add(col4);
			
			col4=new ExcelEntity();
			col4.setFormat(format);
			col4.setRowSpan(0);
			col4.setColSpan(0);
			col4.setRow(2+i);
			col4.setCol(9);
			col4.setContent(temp.get("BZJ") == null ? "" : temp.get("BZJ").toString());
			contextList.add(col4);
			
			col4=new ExcelEntity();
			col4.setFormat(format);
			col4.setRowSpan(0);
			col4.setColSpan(0);
			col4.setRow(2+i);
			col4.setCol(10);
			col4.setContent(temp.get("KHTR") == null ? "" : temp.get("KHTR").toString()+"%");
			contextList.add(col4);
			
			col4=new ExcelEntity();
			col4.setFormat(format);
			col4.setRowSpan(0);
			col4.setColSpan(0);
			col4.setRow(2+i);
			col4.setCol(11);
			col4.setContent(temp.get("QS") == null ? "" : temp.get("QS").toString());
			contextList.add(col4);
			
			col4=new ExcelEntity();
			col4.setFormat(format);
			col4.setRowSpan(0);
			col4.setColSpan(0);
			col4.setRow(2+i);
			col4.setCol(12);
			col4.setContent(temp.get("QZRQ") == null ? "" : temp.get("QZRQ").toString());
			contextList.add(col4);
			
			col4=new ExcelEntity();
			col4.setFormat(format);
			col4.setRowSpan(0);
			col4.setColSpan(0);
			col4.setRow(2+i);
			col4.setCol(13);
			col4.setContent(temp.get("PSTGR") == null ? "" : temp.get("PSTGR").toString());
			contextList.add(col4);
			
			col4=new ExcelEntity();
			col4.setFormat(format);
			col4.setRowSpan(0);
			col4.setColSpan(0);
			col4.setRow(2+i);
			col4.setCol(14);
			col4.setContent(temp.get("SJZFR") == null ? "" : temp.get("SJZFR").toString());
			contextList.add(col4);
			
			col4=new ExcelEntity();
			col4.setFormat(format);
			col4.setRowSpan(0);
			col4.setColSpan(0);
			col4.setRow(2+i);
			col4.setCol(15);
			col4.setContent(temp.get("PV_PRICE") == null ? "" : temp.get("PV_PRICE").toString());
			contextList.add(col4);
		}
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
