package com.brick.settle.service;

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


public class SettleExcel 
{
	Log logger = LogFactory.getLog(SettleExcel.class);
	String excelName=null;//该Excel导出文件名
	String excelHead=null;//该Excel头部信息
	int cell=7;//该Excel列数
	int row=1;//该Excel头部所占的行数
	WritableCellFormat format1=null;//该Excel列表的通用样式
	
	
	public void expExcel(Context context)
	{
		
		
		
		ByteArrayOutputStream baos = null;
		excelName="结清明细表.xls";
		excelHead="结清明细表";
		
		row=2;
		format1=this.tableFont();
		List cellList=this.cellSome();
		List titleList=this.titleListMethod();
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
	public List cellSome()
	{
		List cellList=new ArrayList();
		cellList.add(6);
		cellList.add(20);
		cellList.add(36);
		cellList.add(36);
		cellList.add(20);
		cellList.add(15);
		cellList.add(10);
		return cellList;
	}
	
	
	//设置标题
	public List titleListMethod()
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
		col0.setColSpan(6);
		col0.setRow(0);
		col0.setCol(0);
		col0.setContent("结清明细表");
		titleList.add(col0);
		
		//第一行
		//标题第一列（跨一行,一列,居中显示在第一行）
		ExcelEntity col1=new ExcelEntity();
		col1.setFormat(format);
		col1.setRowSpan(0);
		col1.setColSpan(0);
		col1.setRow(1);
		col1.setCol(0);
		col1.setContent("序号");
		titleList.add(col1);
		
		//标题第二列（跨一行,一列,居中显示在第一行）
		ExcelEntity col2=new ExcelEntity();
		col2.setFormat(format);
		col2.setRowSpan(0);
		col2.setColSpan(0);
		col2.setRow(1);
		col2.setCol(1);
		col2.setContent("合同号");
		titleList.add(col2);
		
		ExcelEntity col3=new ExcelEntity();
		col3.setFormat(format);
		col3.setRowSpan(0);
		col3.setColSpan(0);
		col3.setRow(1);
		col3.setCol(2);
		col3.setContent("承租人");
		titleList.add(col3);
		
		
		ExcelEntity col4=new ExcelEntity();
		col4.setFormat(format);
		col4.setRowSpan(0);
		col4.setColSpan(0);
		col4.setRow(1);
		col4.setCol(3);
		col4.setContent("分公司");
		titleList.add(col4);
		col4=new ExcelEntity();
		col4.setFormat(format);
		col4.setRowSpan(0);
		col4.setColSpan(0);
		col4.setRow(1);
		col4.setCol(4);
		col4.setContent("结清金额");
		titleList.add(col4);
		col4=new ExcelEntity();
		col4.setFormat(format);
		col4.setRowSpan(0);
		col4.setColSpan(0);
		col4.setRow(1);
		col4.setCol(5);
		col4.setContent("入账日");
		titleList.add(col4);
		col4=new ExcelEntity();
		col4.setFormat(format);
		col4.setRowSpan(0);
		col4.setColSpan(0);
		col4.setRow(1);
		col4.setCol(6);
		col4.setContent("结清状态");
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
		
		for(int i=0;i<content.size();i++)
		{
			Map temp = (Map) content.get(i) ;
			ExcelEntity col4=new ExcelEntity();
			col4.setFormat(contentFromart);
			col4.setRowSpan(0);
			col4.setColSpan(0);
			col4.setRow(2+i);
			col4.setCol(0);
			col4.setContent((i+1) + "");
			contextList.add(col4);
			
			col4=new ExcelEntity();
			col4.setFormat(contentFromart);
			col4.setRowSpan(0);
			col4.setColSpan(0);
			col4.setRow(2+i);
			col4.setCol(1);
			col4.setContent(temp.get("LEASE_CODE") == null ? "" : temp.get("LEASE_CODE").toString());
			contextList.add(col4);
			
			col4=new ExcelEntity();
			col4.setFormat(contentFromart);
			col4.setRowSpan(0);
			col4.setColSpan(0);
			col4.setRow(2+i);
			col4.setCol(2);
			col4.setContent(temp.get("CUST_NAME") == null ? "" : temp.get("CUST_NAME").toString());
			contextList.add(col4);
			
			col4=new ExcelEntity();
			col4.setFormat(contentFromart);
			col4.setRowSpan(0);
			col4.setColSpan(0);
			col4.setRow(2+i);
			col4.setCol(3);
			col4.setContent(temp.get("DECP_NAME_CN") == null ? "" : temp.get("DECP_NAME_CN").toString());
			contextList.add(col4);
			
			col4=new ExcelEntity();
			col4.setFormat(contentFromart);
			col4.setRowSpan(0);
			col4.setColSpan(0);
			col4.setRow(2+i);
			col4.setCol(4);
			col4.setContent(temp.get("REAL_TOTAL") == null ? "" : temp.get("REAL_TOTAL").toString());
			contextList.add(col4);
			
			col4=new ExcelEntity();
			col4.setFormat(contentFromart);
			col4.setRowSpan(0);
			col4.setColSpan(0);
			col4.setRow(2+i);
			col4.setCol(5);
			col4.setContent(temp.get("SETTLE_DATE") == null ? "" : temp.get("SETTLE_DATE").toString());
			contextList.add(col4);
			
			String state = "" ;
			if(temp.get("RECP_STATUS") != null && !"".equals(temp.get("RECP_STATUS").toString())){
				if("1".equals(temp.get("RECP_STATUS").toString())){
					state = "正常结清" ;
				} else if("3".equals(temp.get("RECP_STATUS").toString())){
					state = "提前结清" ;
				} else {
				state = "未结清" ;
			}
			}
			col4=new ExcelEntity();
			col4.setFormat(contentFromart);
			col4.setRowSpan(0);
			col4.setColSpan(0);
			col4.setRow(2+i);
			col4.setCol(6);
			col4.setContent(state);
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
