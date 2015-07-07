package com.brick.tables.service;



import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
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



public class ReportAllCompTbExcel 
{
	Log logger = LogFactory.getLog(ReportAllCompTbExcel.class);
	String excelName=null;//该Excel导出文件名
	String excelHead=null;//该Excel头部信息
	int cell=0;//该Excel列数
	int row=0;//该Excel头部所占的行数
	WritableCellFormat format1=null;//该Excel列表的通用样式
	
	
	public void reportAllCompTb(List list,Map map,Context context)
	{
		ByteArrayOutputStream baos = null;
		excelName="全公司各办事处进件统计表.xls";
		excelHead="进件统计表";
		cell=12;
		row=4;
		format1=this.tableFont();
		List cellList=this.cellSome();
		List titleList=this.titleListMethod(map);
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
		cellList.add(12);
		cellList.add(12);
		cellList.add(12);
		cellList.add(12);
		cellList.add(12);
		cellList.add(12);
		cellList.add(12);
		cellList.add(12);
		cellList.add(12);
		cellList.add(12);
		cellList.add(12);
		cellList.add(12);
		return cellList;
	}
	
	//设置标题
	public List titleListMethod(Map map)
	{
		
		String querydate=(String) map.get("TODAY");
		
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
		ExcelEntity col111=new ExcelEntity();
		col111.setFormat(format);
		col111.setRowSpan(1);
		col111.setColSpan(11);
		col111.setRow(0);
		col111.setCol(0);
		col111.setContent("全公司各办事处"+querydate+"进件统计表");
		titleList.add(col111);
		//第2行
		//标题第一列（跨两行,一列,居中显示在第一行）
		//公司名称
		ExcelEntity col1=new ExcelEntity();
		col1.setFormat(format);
		col1.setRowSpan(1);
		col1.setColSpan(0);
		col1.setRow(2);
		col1.setCol(0);
		col1.setContent("办事处别");
		titleList.add(col1);
		
		//标题第二列（跨一行,五列,居中显示在第一行）
		//当日<br/>进件
		ExcelEntity col2=new ExcelEntity();
		col2.setFormat(format);
		col2.setRowSpan(1);
		col2.setColSpan(0);
		col2.setRow(2);
		col2.setCol(1);
		col2.setContent("当日进件");
		titleList.add(col2);
		
		//累计进件
		ExcelEntity col3=new ExcelEntity();
		col3.setFormat(format);
		col3.setRowSpan(1);
		col3.setColSpan(0);
		col3.setRow(2);
		col3.setCol(2);
		col3.setContent("累计进件");
		titleList.add(col3);
		
		//当日已访厂
		ExcelEntity col4=new ExcelEntity();
		col4.setFormat(format);
		col4.setRowSpan(1);
		col4.setColSpan(0);
		col4.setRow(2);
		col4.setCol(3);
		col4.setContent("当日已访厂");
		titleList.add(col4);
		
		//累计已访厂
		ExcelEntity col5=new ExcelEntity();
		col5.setFormat(format);
		col5.setRowSpan(1);
		col5.setColSpan(0);
		col5.setRow(2);
		col5.setCol(4);
		col5.setContent("累计已访厂");
		titleList.add(col5);
		//累计未访厂
		ExcelEntity col6=new ExcelEntity();
		col6.setFormat(format);
		col6.setRowSpan(1);
		col6.setColSpan(0);
		col6.setRow(2);
		col6.setCol(5);
		col6.setContent("累计未访厂");
		titleList.add(col6);
		
	
		
		//已付条件
		ExcelEntity col7=new ExcelEntity();
		col7.setFormat(format);
		col7.setRowSpan(1);
		col7.setColSpan(0);
		col7.setRow(2);
		col7.setCol(6);
		col7.setContent("累计核准");
		titleList.add(col7);
		
		//审批婉拒
		ExcelEntity col8=new ExcelEntity();
		col8.setFormat(format);
		col8.setRowSpan(1);
		col8.setColSpan(0);
		col8.setRow(2);
		col8.setCol(7);
		col8.setContent("已付条件");
		titleList.add(col8);
		
		//累计未审批
		ExcelEntity col9=new ExcelEntity();
		col9.setFormat(format);
		col9.setRowSpan(1);
		col9.setColSpan(0);
		col9.setRow(2);
		col9.setCol(8);
		col9.setContent("审批婉拒");
		titleList.add(col9);
		
		//累计完成率
		ExcelEntity col10=new ExcelEntity();
		col10.setFormat(format);
		col10.setRowSpan(1);
		col10.setColSpan(0);
		col10.setRow(2);
		col10.setCol(9);
		col10.setContent("累计未审批");
		titleList.add(col10);
		
		//累计核准率
		ExcelEntity col11=new ExcelEntity();
		col11.setFormat(format);
		col11.setRowSpan(1);
		col11.setColSpan(0);
		col11.setRow(2);
		col11.setCol(10);
		col11.setContent("累计完成率");
		titleList.add(col11);
		//累计核准
		ExcelEntity col12=new ExcelEntity();
		col12.setFormat(format);
		col12.setRowSpan(1);
		col12.setColSpan(0);
		col12.setRow(2);
		col12.setCol(11);
		col12.setContent("累计核准率");
		titleList.add(col12);


		
		return titleList;
	}
	
	
	public List contextListMethod(List contextList)
	{
		List contextListFather=new ArrayList();
		//for(int i=0;i<contextList.size();i++)
		//{
			Iterator iter = contextList.iterator();
			while(iter.hasNext()){
		       
			Map contentmap=(HashMap)iter.next();
			List contextListSun=new ArrayList();
			contextListSun.add(contentmap.get("DECP_NAME_CN").toString());
			contextListSun.add(contentmap.get("TODAYMEMO").toString());
			contextListSun.add(contentmap.get("MEMO").toString());
			contextListSun.add(contentmap.get("TODAYVISIT").toString());
			contextListSun.add(contentmap.get("VISIT").toString());
			contextListSun.add(contentmap.get("NOVISIT").toString());
			contextListSun.add(contentmap.get("WINDONE").toString());
			contextListSun.add(contentmap.get("WINDCONDITION").toString());
			contextListSun.add(contentmap.get("WINDNOT").toString());
			contextListSun.add(contentmap.get("NOAPPROVE").toString());
			contextListSun.add(((Float.parseFloat(contentmap.get("COMPLETIONRATE").toString())*100)+"%"));
			contextListSun.add(((Float.parseFloat(contentmap.get("WINDONERATE").toString())*100)+"%"));
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
