package com.brick.report.service;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import javax.servlet.ServletOutputStream;

import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;

import com.brick.collection.service.ExportPaylistToExcel;
import com.brick.service.entity.Context;
import com.brick.util.ExcelEntity;
import com.brick.util.PublicExcel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;



public class ReportExcel 
{
	Log logger = LogFactory.getLog(ReportExcel.class);
	String excelName=null;//该Excel导出文件名
	String excelHead=null;//该Excel头部信息
	int cell=0;//该Excel列数
	int row=0;//该Excel头部所占的行数
	WritableCellFormat format1=null;//该Excel列表的通用样式
	
	
	public void reportExcelJoin(List list,Context context)
	{
		ByteArrayOutputStream baos = null;
		excelName="report.xls";
		excelHead="评审统计 ";
		cell=20;
		row=2;
		format1=this.tableFont();
		List cellList=this.cellSome();
		List titleList=this.titleListMethod();
		List contentList=this.contextListMethod(list);
		
		PublicExcel exl = new PublicExcel();
		exl.createexl(); 
		baos = exl.export(excelName,excelHead,cell,row,format1,cellList,titleList,contentList);
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
		cellList.add(40);
		cellList.add(15);
		cellList.add(15);
		cellList.add(15);
		cellList.add(15);
		cellList.add(15);
		cellList.add(15);
		cellList.add(15);
		cellList.add(15);
		cellList.add(15);
		cellList.add(15);
		cellList.add(15);
		cellList.add(15);
		cellList.add(20);
		cellList.add(20);
		cellList.add(20);
		cellList.add(25);
		cellList.add(25);
		cellList.add(25);
		cellList.add(25);
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
		//第一行
		//标题第一列（跨两行,一列,居中显示在第一行）
		//公司名称
		ExcelEntity col1=new ExcelEntity();
		col1.setFormat(format);
		col1.setRowSpan(1);
		col1.setColSpan(0);
		col1.setRow(0);
		col1.setCol(0);
		col1.setContent("公司名称");
		titleList.add(col1);
		
		//标题第二列（跨一行,五列,居中显示在第一行）
		//报告（未提交）
		ExcelEntity col2=new ExcelEntity();
		col2.setFormat(format);
		col2.setRowSpan(0);
		col2.setColSpan(4);
		col2.setRow(0);
		col2.setCol(1);
		col2.setContent("报告（未提交）");
		titleList.add(col2);
		
		//报告审批
		ExcelEntity col3=new ExcelEntity();
		col3.setFormat(format);
		col3.setRowSpan(0);
		col3.setColSpan(1);
		col3.setRow(0);
		col3.setCol(6);
		col3.setContent("报告（未提交）");
		titleList.add(col3);
		
		//评审
		ExcelEntity col4=new ExcelEntity();
		col4.setFormat(format);
		col4.setRowSpan(0);
		col4.setColSpan(5);
		col4.setRow(0);
		col4.setCol(8);
		col4.setContent("评审");
		titleList.add(col4);
		
		//完成合计
		ExcelEntity col5=new ExcelEntity();
		col5.setFormat(format);
		col5.setRowSpan(1);
		col5.setColSpan(0);
		col5.setRow(0);
		col5.setCol(14);
		col5.setContent("完成合计");
		titleList.add(col5);
		
		//总计
		ExcelEntity col6=new ExcelEntity();
		col6.setFormat(format);
		col6.setRowSpan(1);
		col6.setColSpan(0);
		col6.setRow(0);
		col6.setCol(15);
		col6.setContent("总计");
		titleList.add(col6);
		
		//完成率
		ExcelEntity col7=new ExcelEntity();
		col7.setFormat(format);
		col7.setRowSpan(1);
		col7.setColSpan(0);
		col7.setRow(0);
		col7.setCol(16);
		col7.setContent("完成率");
		titleList.add(col7);
		
		//最长耗时
		ExcelEntity col8=new ExcelEntity();
		col8.setFormat(format);
		col8.setRowSpan(1);
		col8.setColSpan(0);
		col8.setRow(0);
		col8.setCol(17);
		col8.setContent("最长耗时");
		titleList.add(col8);
		
		//最短耗时
		ExcelEntity col9=new ExcelEntity();
		col9.setFormat(format);
		col9.setRowSpan(1);
		col9.setColSpan(0);
		col9.setRow(0);
		col9.setCol(18);
		col9.setContent("最短耗时");
		titleList.add(col9);
		
		//平均耗时
		ExcelEntity col10=new ExcelEntity();
		col10.setFormat(format);
		col10.setRowSpan(1);
		col10.setColSpan(0);
		col10.setRow(0);
		col10.setCol(19);
		col10.setContent("平均耗时");
		titleList.add(col10);
		
		//1天以内
		ExcelEntity col11=new ExcelEntity();
		col11.setFormat(format);
		col11.setRowSpan(0);
		col11.setColSpan(0);
		col11.setRow(1);
		col11.setCol(1);
		col11.setContent("1天以内");
		titleList.add(col11);
		
		//1－2天
		ExcelEntity col12=new ExcelEntity();
		col12.setFormat(format);
		col12.setRowSpan(0);
		col12.setColSpan(0);
		col12.setRow(1);
		col12.setCol(2);
		col12.setContent("1－2天");
		titleList.add(col12);
		
		//2－3天
		ExcelEntity col13=new ExcelEntity();
		col13.setFormat(format);
		col13.setRowSpan(0);
		col13.setColSpan(0);
		col13.setRow(1);
		col13.setCol(3);
		col13.setContent("2－3天");
		titleList.add(col13);
		
		//3天以上
		ExcelEntity col14=new ExcelEntity();
		col14.setFormat(format);
		col14.setRowSpan(0);
		col14.setColSpan(0);
		col14.setRow(1);
		col14.setCol(4);
		col14.setContent("3天以上");
		titleList.add(col14);
		
		//合计
		ExcelEntity col15=new ExcelEntity();
		col15.setFormat(format);
		col15.setRowSpan(0);
		col15.setColSpan(0);
		col15.setRow(1);
		col15.setCol(5);
		col15.setContent("合计");
		titleList.add(col15);
		
		//已审批
		ExcelEntity col16=new ExcelEntity();
		col16.setFormat(format);
		col16.setRowSpan(0);
		col16.setColSpan(0);
		col16.setRow(1);
		col16.setCol(6);
		col16.setContent("已审批");
		titleList.add(col16);
		
		//未审批
		ExcelEntity col17=new ExcelEntity();
		col17.setFormat(format);
		col17.setRowSpan(0);
		col17.setColSpan(0);
		col17.setRow(1);
		col17.setCol(7);
		col17.setContent("未审批");
		titleList.add(col17);
		
		//未评审
		ExcelEntity col18=new ExcelEntity();
		col18.setFormat(format);
		col18.setRowSpan(0);
		col18.setColSpan(0);
		col18.setRow(1);
		col18.setCol(8);
		col18.setContent("未审批");
		titleList.add(col18);
		
		//评审中
		ExcelEntity col19=new ExcelEntity();
		col19.setFormat(format);
		col19.setRowSpan(0);
		col19.setColSpan(0);
		col19.setRow(1);
		col19.setCol(9);
		col19.setContent("评审中");
		titleList.add(col19);
		
		//评审通过
		ExcelEntity col20=new ExcelEntity();
		col20.setFormat(format);
		col20.setRowSpan(0);
		col20.setColSpan(0);
		col20.setRow(1);
		col20.setCol(10);
		col20.setContent("评审通过");
		titleList.add(col20);
		
		//不通过附条件
		ExcelEntity col21=new ExcelEntity();
		col21.setFormat(format);
		col21.setRowSpan(0);
		col21.setColSpan(0);
		col21.setRow(1);
		col21.setCol(11);
		col21.setContent("不通过附条件");
		titleList.add(col21);
		
		//不通过
		ExcelEntity col22=new ExcelEntity();
		col22.setFormat(format);
		col22.setRowSpan(0);
		col22.setColSpan(0);
		col22.setRow(1);
		col22.setCol(12);
		col22.setContent("不通过");
		titleList.add(col22);
		
		//通过率
		ExcelEntity col23=new ExcelEntity();
		col23.setFormat(format);
		col23.setRowSpan(0);
		col23.setColSpan(0);
		col23.setRow(1);
		col23.setCol(13);
		col23.setContent("通过率");
		titleList.add(col23);
		
		return titleList;
	}
	
	
	public List contextListMethod(List contextList)
	{
		List contextListFather=new ArrayList();
		for(int i=0;i<contextList.size();i++)
		{
			
			Map contentmap=(Map)contextList.get(i);
			List contextListSun=new ArrayList();
			contextListSun.add(contentmap.get("DECP_NAME_CN").toString());
			contextListSun.add(contentmap.get("BAOGAO_ONE").toString());
			contextListSun.add(contentmap.get("BAOGAO_ONETOTWO").toString());
			contextListSun.add(contentmap.get("BAOGAO_TWOTOTHREE").toString());
			contextListSun.add(contentmap.get("BAOGAO_THREE").toString());
			contextListSun.add(contentmap.get("BAOGAO_SUM").toString());
			contextListSun.add(contentmap.get("SHENPI_YES").toString());
			contextListSun.add(contentmap.get("SHENPI_NO").toString());
			contextListSun.add(contentmap.get("PINGSHEN_UN").toString());
			contextListSun.add(contentmap.get("PINGSHEN_ZHONG").toString());
			contextListSun.add(contentmap.get("PINGSHEN_GUO").toString());
			contextListSun.add(contentmap.get("PINGSHEN_BUTONGFU").toString());
			contextListSun.add(contentmap.get("PINGSHEN_BUTONG").toString());
			String num=null;
			if(contentmap.get("PINGSHEN_LV")!=null)
			{
				DecimalFormat df = new DecimalFormat("0.000");
				num = df.format(Double.parseDouble(contentmap.get("PINGSHEN_LV").toString()))+"%";
			}
			else
			{
				num="%";
			}
			contextListSun.add(num);
			contextListSun.add(contentmap.get("PINGSHEN_YES").toString());
			contextListSun.add(contentmap.get("BAO_TIJIAO").toString());
			String num1=null;
			if(contentmap.get("WANCHENG_LV")!=null)
			{
				DecimalFormat df = new DecimalFormat("0.000");
				num1 = df.format(Double.parseDouble(contentmap.get("WANCHENG_LV").toString()))+"%";
			}
			else
			{
				num1="%";
			}
			contextListSun.add(num1);
			contextListSun.add(contentmap.get("MAX_HCONTEXT").toString());
			contextListSun.add(contentmap.get("MIN_HCONTEXT").toString());
			contextListSun.add(contentmap.get("AVG_HCONTEXT").toString());
			contextListFather.add(contextListSun);
		}
		return contextListFather;
		
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
	
	
	
	
	
}
