package com.brick.report.service;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import jxl.format.Alignment;
import jxl.format.VerticalAlignment;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.log.service.LogPrint;
import com.brick.service.entity.Context;
import com.brick.util.DataUtil;
import com.brick.util.ExcelEntity;
import com.brick.util.PublicExcel;

public class AchievementExcel {

	Log logger = LogFactory.getLog(ReportExcel.class);
	String excelName=null;//该Excel导出文件名
	String excelHead=null;//该Excel头部信息
	int cell=0;//该Excel列数
	int row=0;//该Excel头部所占的行数
	WritableCellFormat format1=null;//该Excel列表的通用样式
	
	
	public void achievementExcelJoin(List list,List count,List userCount,Context context)
	{
		ByteArrayOutputStream baos = null;
		excelName="report.xls";
		excelHead="业绩进度控管表 ";
		cell=18;
		row=2;
		format1=this.tableFont();
		List cellList=this.cellSome();
		List titleList=this.titleListMethod();
		List contentList=this.contextListMethod1(list,count,userCount);
		
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
		cellList.add(20);
		cellList.add(25);
		cellList.add(15);
		cellList.add(15);
		cellList.add(15);
		cellList.add(15);
		cellList.add(15);
		cellList.add(15);
		cellList.add(15);
		cellList.add(15);
		cellList.add(25);
		cellList.add(25);
		cellList.add(15);
		cellList.add(25);
		cellList.add(25);
		cellList.add(20);
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
		//标题第一列（跨一行,一列,居中显示在第一行）
		//单位
		ExcelEntity col1=new ExcelEntity();
		col1.setFormat(format);
		col1.setRowSpan(0);
		col1.setColSpan(0);
		col1.setRow(0);
		col1.setCol(0);
		col1.setContent("单位");
		titleList.add(col1);
		
		//标题第二列（跨一行,一列,居中显示在第一行）
		//部門月目標
		ExcelEntity col2=new ExcelEntity();
		col2.setFormat(format);
		col2.setRowSpan(0);
		col2.setColSpan(0);
		col2.setRow(0);
		col2.setCol(1);
		col2.setContent("部门月目标(单位：K)");
		titleList.add(col2);
		
		//业务
		ExcelEntity col3=new ExcelEntity();
		col3.setFormat(format);
		col3.setRowSpan(0);
		col3.setColSpan(0);
		col3.setRow(0);
		col3.setCol(2);
		col3.setContent("业务");
		titleList.add(col3);
		
		//月目標金額
		ExcelEntity col4=new ExcelEntity();
		col4.setFormat(format);
		col4.setRowSpan(0);
		col4.setColSpan(0);
		col4.setRow(0);
		col4.setCol(3);
		col4.setContent("月目标金额(单位：K)");
		titleList.add(col4);
		
		//客户
		ExcelEntity col5=new ExcelEntity();
		col5.setFormat(format);
		col5.setRowSpan(0);
		col5.setColSpan(0);
		col5.setRow(0);
		col5.setCol(4);
		col5.setContent("客户");
		titleList.add(col5);
		
		//厂牌
		ExcelEntity col6=new ExcelEntity();
		col6.setFormat(format);
		col6.setRowSpan(0);
		col6.setColSpan(0);
		col6.setRow(0);
		col6.setCol(5);
		col6.setContent("厂牌");
		titleList.add(col6);
		
		//客户属性
		ExcelEntity col7=new ExcelEntity();
		col7.setFormat(format);
		col7.setRowSpan(0);
		col7.setColSpan(0);
		col7.setRow(0);
		col7.setCol(6);
		col7.setContent("客户属性");
		titleList.add(col7);
		
		//来源
		ExcelEntity col8=new ExcelEntity();
		col8.setFormat(format);
		col8.setRowSpan(0);
		col8.setColSpan(0);
		col8.setRow(0);
		col8.setCol(7);
		col8.setContent("来源");
		titleList.add(col8);
		
		//客户区域
		ExcelEntity col9=new ExcelEntity();
		col9.setFormat(format);
		col9.setRowSpan(0);
		col9.setColSpan(0);
		col9.setRow(0);
		col9.setCol(8);
		col9.setContent("客户区域");
		titleList.add(col9);
		
		//租赁方式
		ExcelEntity col10=new ExcelEntity();
		col10.setFormat(format);
		col10.setRowSpan(0);
		col10.setColSpan(0);
		col10.setRow(0);
		col10.setCol(9);
		col10.setContent("租赁方式");
		titleList.add(col10);
		
		//保证金
		ExcelEntity col11=new ExcelEntity();
		col11.setFormat(format);
		col11.setRowSpan(0);
		col11.setColSpan(0);
		col11.setRow(0);
		col11.setCol(10);
		col11.setContent("保证金");
		titleList.add(col11);
		
		//租金
		ExcelEntity col12=new ExcelEntity();
		col12.setFormat(format);
		col12.setRowSpan(0);
		col12.setColSpan(0);
		col12.setRow(0);
		col12.setCol(11);
		col12.setContent("租金");
		titleList.add(col12);
		
		//2－3天
		ExcelEntity col13=new ExcelEntity();
		col13.setFormat(format);
		col13.setRowSpan(0);
		col13.setColSpan(0);
		col13.setRow(0);
		col13.setCol(12);
		col13.setContent("租期");
		titleList.add(col13);
		
		//申请拨款金额
		ExcelEntity col14=new ExcelEntity();
		col14.setFormat(format);
		col14.setRowSpan(0);
		col14.setColSpan(0);
		col14.setRow(0);
		col14.setCol(13);
		col14.setContent("申请拨款金额");
		titleList.add(col14);
		
		//已撥款額
		ExcelEntity col15=new ExcelEntity();
		col15.setFormat(format);
		col15.setRowSpan(0);
		col15.setColSpan(0);
		col15.setRow(0);
		col15.setCol(14);
		col15.setContent("已拨款额");
		titleList.add(col15);
		
		//合约签订日期
		ExcelEntity col18=new ExcelEntity();
		col18.setFormat(format);
		col18.setRowSpan(0);
		col18.setColSpan(0);
		col18.setRow(0);
		col18.setCol(15);
		col18.setContent("合约签订日期");
		titleList.add(col18);
		
		//第一次拨款日期
		ExcelEntity col16=new ExcelEntity();
		col16.setFormat(format);
		col16.setRowSpan(0);
		col16.setColSpan(0);
		col16.setRow(0);
		col16.setCol(16);
		col16.setContent("动拨日期");
		titleList.add(col16);
		
		//起租日期
		ExcelEntity col17=new ExcelEntity();
		col17.setFormat(format);
		col17.setRowSpan(0);
		col17.setColSpan(0);
		col17.setRow(0);
		col17.setCol(17);
		col17.setContent("起租日期");
		titleList.add(col17);
		
		
		
		return titleList;
	}
	
	
	public List contextListMethod(List contextList,List count,List userCount)
	{
		//format(样式)
		//colspan(int)跨几列
		//rowspan(int)跨几行
		//content(String)内容
		//row(int)显示在第几行（有的标题有两行，说明该信息显示在第几行,从0开始算起）
		//col(int)显示在第几列（跨行时用到,从0开始算起）
		
		
		List contextListFather=new ArrayList();
		WritableCellFormat format=this.tableFont();//标题通用样式
		int rowNum=1;
		int number=1;
		
		
		for(int i=0;i<count.size();i++)
		{
			Map countSun=(Map)count.get(i);
			int numdecp_id=Integer.parseInt(countSun.get("DECP_ID").toString());
			int num=0;
			for(int j=0;j<contextList.size();j++)
			{
				Map contentmap=(Map)contextList.get(j);
				
				if(Integer.parseInt(contentmap.get("DECP_ID").toString())==numdecp_id)
				{
					if(num==0)
					{
						int counts=Integer.parseInt(countSun.get("DEPT_COUNT").toString());
						ExcelEntity col=new ExcelEntity();
						col.setFormat(format);
						col.setRowSpan(counts-1);
						col.setColSpan(0);
						col.setRow(number);
						col.setCol(1);
						Object DECP_NAME_CN=contentmap.get("DECP_NAME_CN");
						if(DECP_NAME_CN!=null)
						{
							col.setContent(DECP_NAME_CN.toString());
						}
						else
						{
							col.setContent("");
						}
						contextListFather.add(col);
						
						ExcelEntity col1=new ExcelEntity();
						col1.setFormat(format);
						col1.setRowSpan(counts-1);
						col1.setColSpan(0);
						col1.setRow(number);
						col1.setCol(2);
						Object EMPL_ACHIEVEMENT1=countSun.get("EMPL_ACHIEVEMENT");
						if(EMPL_ACHIEVEMENT1!=null)
						{
							col1.setContent(EMPL_ACHIEVEMENT1.toString());
						}
						else
						{
							col1.setContent("");
						}
						contextListFather.add(col1);
					}
					rowNum=3;
					
					
						ExcelEntity col2=new ExcelEntity();
						col2.setFormat(format);
						col2.setRowSpan(0);
						col2.setColSpan(0);
						col2.setRow(number);
						col2.setCol(rowNum++);
						Object CLERK_NAME=contentmap.get("CLERK_NAME");
						if(CLERK_NAME!=null)
						{
							col2.setContent(CLERK_NAME.toString());
						}
						else
						{
							col2.setContent("");
						}
						contextListFather.add(col2);
						
						ExcelEntity col3=new ExcelEntity();
						col3.setFormat(format);
						col3.setRowSpan(0);
						col3.setColSpan(0);
						col3.setRow(number);
						col3.setCol(rowNum++);
						Object EMPL_ACHIEVEMENT=contentmap.get("EMPL_ACHIEVEMENT");
						if(EMPL_ACHIEVEMENT!=null)
						{
							col3.setContent(EMPL_ACHIEVEMENT.toString());
						}
						else
						{
							col3.setContent("");
						}
						contextListFather.add(col3);
						
						
						
						
						
						
						
						
						
						
						
						ExcelEntity col4=new ExcelEntity();
						col4.setFormat(format);
						col4.setRowSpan(0);
						col4.setColSpan(0);
						col4.setRow(number);
						col4.setCol(rowNum++);
						Object CUST_NAME=contentmap.get("CUST_NAME");
						if(CUST_NAME!=null)
						{
							col4.setContent(CUST_NAME.toString());
						}
						else
						{
							col4.setContent("");
						}
						contextListFather.add(col4);
						
						ExcelEntity col5=new ExcelEntity();
						col5.setFormat(format);
						col5.setRowSpan(0);
						col5.setColSpan(0);
						col5.setRow(number);
						col5.setCol(rowNum++);
						Object BRAND=contentmap.get("BRAND");
						if(BRAND!=null)
						{
							col5.setContent(BRAND.toString());
						}
						else
						{
							col5.setContent("");
						}
						contextListFather.add(col5);
						
						ExcelEntity col6=new ExcelEntity();
						col6.setFormat(format);
						col6.setRowSpan(0);
						col6.setColSpan(0);
						col6.setRow(number);
						col6.setCol(rowNum++);
						Object custType=contentmap.get("CUST_TYPE");
						if(custType!=null)
						{
							col6.setContent(custType.toString());
						}
						else
						{
							col6.setContent("");
						}
						contextListFather.add(col6);
						
						ExcelEntity col7=new ExcelEntity();
						col7.setFormat(format);
						col7.setRowSpan(0);
						col7.setColSpan(0);
						col7.setRow(number);
						col7.setCol(rowNum++);
						col7.setContent("");
						contextListFather.add(col7);
						
						ExcelEntity col8=new ExcelEntity();
						col8.setFormat(format);
						col8.setRowSpan(0);
						col8.setColSpan(0);
						col8.setRow(number);
						col8.setCol(rowNum++);
						Object AREA=contentmap.get("AREA");
						if(AREA!=null)
						{
							col8.setContent(AREA.toString());
						}
						else
						{
							col8.setContent("");
						}
						contextListFather.add(col8);
						
						ExcelEntity col9=new ExcelEntity();
						col9.setFormat(format);
						col9.setRowSpan(0);
						col9.setColSpan(0);
						col9.setRow(number);
						col9.setCol(rowNum++);
						Object RECT_TYPE=contentmap.get("RECT_TYPE");
						if(RECT_TYPE!=null)
						{
							col9.setContent(RECT_TYPE.toString());
						}
						else
						{
							col9.setContent("");
						}
						contextListFather.add(col9);
						
						ExcelEntity col10=new ExcelEntity();
						col10.setFormat(format);
						col10.setRowSpan(0);
						col10.setColSpan(0);
						col10.setRow(number);
						col10.setCol(rowNum++);
						col10.setContent("");
						contextListFather.add(col10);
						
						ExcelEntity col11=new ExcelEntity();
						col11.setFormat(format);
						col11.setRowSpan(0);
						col11.setColSpan(0);
						col11.setRow(number);
						col11.setCol(rowNum++);
						Object HEAD_HIRE=contentmap.get("HEAD_HIRE");
						if(HEAD_HIRE!=null)
						{
							col11.setContent(HEAD_HIRE.toString());
						}
						else
						{
							col11.setContent("");
						}
						contextListFather.add(col11);
						
						ExcelEntity col12=new ExcelEntity();
						col12.setFormat(format);
						col12.setRowSpan(0);
						col12.setColSpan(0);
						col12.setRow(number);
						col12.setCol(rowNum++);
						col12.setContent("");
						contextListFather.add(col12);
						
						ExcelEntity col13=new ExcelEntity();
						col13.setFormat(format);
						col13.setRowSpan(0);
						col13.setColSpan(0);
						col13.setRow(number);
						col13.setCol(rowNum++);
						col13.setContent("");
						contextListFather.add(col13);
						
						ExcelEntity col14=new ExcelEntity();
						col14.setFormat(format);
						col14.setRowSpan(0);
						col14.setColSpan(0);
						col14.setRow(number);
						col14.setCol(rowNum++);
						col14.setContent("");
						contextListFather.add(col14);
						
						ExcelEntity col15=new ExcelEntity();
						col15.setFormat(format);
						col15.setRowSpan(0);
						col15.setColSpan(0);
						col15.setRow(number);
						col15.setCol(rowNum++);
						col15.setContent("");
						contextListFather.add(col15);
						
						ExcelEntity col16=new ExcelEntity();
						col16.setFormat(format);
						col16.setRowSpan(0);
						col16.setColSpan(0);
						col16.setRow(number);
						col16.setCol(rowNum++);
						Object START_DATE=contentmap.get("START_DATE");
						if(START_DATE!=null)
						{
							col16.setContent(START_DATE.toString());
						}
						else
						{
							col16.setContent("");
						}
						contextListFather.add(col16);
					
					num++;
					number++;
					rowNum=1;
					//System.out.println("DECP_NAME_CN:"+contentmap.get("DECP_NAME_CN").toString()+",rowNum="+rowNum+",num="+num+",number"+number);
				}
				
				
			}
			num=0;
			
			
		}
		//System.out.println("contextListFather:"+contextListFather.size());
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
	
	
	public List contextListMethod1(List contextList,List count,List userCount)
	{
		//format(样式)
		//colspan(int)跨几列
		//rowspan(int)跨几行
		//content(String)内容
		//row(int)显示在第几行（有的标题有两行，说明该信息显示在第几行,从0开始算起）
		//col(int)显示在第几列（跨行时用到,从0开始算起）
		
		
		List contextListFather=new ArrayList();
		WritableCellFormat format=this.tableFont();//标题通用样式
		WritableCellFormat format1=this.tableFont1();
		int colNum=0; //列
		int rowNum=1; //行
		
		int number=0;//档运行完一个部门为自加一次
		int num=0;//当运行完一个业务员会自加一次
		
		int fatherRow=0;//获取部门总条数
		int fatherRowAdd=0;//将部门总条数相加
		int fatherId=0;//获取部门Id
		int fatherNum=0;//当运行玩一个部门会将其重置为0
		float fatherMoney=0f;//部门月目标
		
		int sunRow=0;//获取业务员总条数
		int sunRowAdd=0;//将业务员总条数相加
		int sunId=0;//获取业务员Id
		int sunNum=0;//当运行完一个业务员会将其重置为0
		float sunMoney=0f;//业务员月目标
		
		
		for(int i=0;i<contextList.size();i++)
		{
			Map contentmap=(Map)contextList.get(i);
			if(fatherNum==0)
			{
				Map fatherCount=(Map)count.get(number);
				if(fatherCount.get("DEPT_COUNT")!=null)
				{
					fatherRow=Integer.parseInt(fatherCount.get("DEPT_COUNT").toString());
					if(fatherRowAdd==0)
					{
						fatherRowAdd=fatherRow;
					}
					else
					{
						fatherRowAdd+=fatherRow;
					}
				}
				if(fatherCount.get("DECP_ID")!=null)
				{
					fatherId=Integer.parseInt(fatherCount.get("DECP_ID").toString());
				}
				if(fatherCount.get("EMPL_ACHIEVEMENT")!=null)
				{
					fatherMoney=Float.parseFloat(fatherCount.get("EMPL_ACHIEVEMENT").toString());
				}
				
					ExcelEntity col=new ExcelEntity();
					col.setFormat(format);
					col.setRowSpan(fatherRow-1);
					col.setColSpan(0);
					col.setRow(rowNum);
					col.setCol(colNum++);
					Object DECP_NAME_CN=contentmap.get("DECP_NAME_CN");
					if(DECP_NAME_CN!=null)
					{
						col.setContent(DECP_NAME_CN.toString());
					}
					else
					{
						col.setContent("");
					}
					contextListFather.add(col);
					
					ExcelEntity col1=new ExcelEntity();
					col1.setFormat(format1);
					col1.setRowSpan(fatherRow-1);
					col1.setColSpan(0);
					col1.setRow(rowNum);
					col1.setCol(colNum++);
					if(fatherCount.get("EMPL_ACHIEVEMENT")!=null)
					{
						col1.setContent(this.updateMon(fatherCount.get("EMPL_ACHIEVEMENT").toString()));
					}
					else
					{
						col1.setContent("");
					}
					contextListFather.add(col1);
				
				
			}
			colNum=2;
			
			
			
			
			
			if(sunNum==0)
			{
				Map sunCount=(Map)userCount.get(num);
				
				if(sunCount.get("EMPL_COUNT")!=null)
				{
					sunRow=Integer.parseInt(sunCount.get("EMPL_COUNT").toString());
					
					if(sunRowAdd==0)
					{
						sunRowAdd=sunRow;
					}
					else
					{
						sunRowAdd+=sunRow;
					}
				}
				if(sunCount.get("ID")!=null)
				{
					sunId=Integer.parseInt(sunCount.get("ID").toString());
				}
				if(sunCount.get("EMPL_ACHIEVEMENT")!=null)
				{
					sunMoney=Float.parseFloat(sunCount.get("EMPL_ACHIEVEMENT").toString());
				}
				
				ExcelEntity col2=new ExcelEntity();
				col2.setFormat(format);
				col2.setRowSpan(sunRow-1);
				col2.setColSpan(0);
				col2.setRow(rowNum);
				col2.setCol(colNum++);
				Object CLERK_NAME=contentmap.get("CLERK_NAME");
				if(CLERK_NAME!=null)
				{
					col2.setContent(CLERK_NAME.toString());
				}
				else
				{
					col2.setContent("");
				}
				contextListFather.add(col2);
				
				ExcelEntity col3=new ExcelEntity();
				col3.setFormat(format1);
				col3.setRowSpan(sunRow-1);
				col3.setColSpan(0);
				col3.setRow(rowNum);
				col3.setCol(colNum++);
				Object EMPL_ACHIEVEMENT=contentmap.get("EMPL_ACHIEVEMENT");
				if(EMPL_ACHIEVEMENT!=null)
				{
					col3.setContent(this.updateMon(EMPL_ACHIEVEMENT.toString()));
				}
				else
				{
					col3.setContent("");
				}
				contextListFather.add(col3);
				
			
			}
			
			colNum=4;
			ExcelEntity col4=new ExcelEntity();
			col4.setFormat(format);
			col4.setRowSpan(0);
			col4.setColSpan(0);
			col4.setRow(rowNum);
			col4.setCol(colNum++);
			Object CUST_NAME=contentmap.get("CUST_NAME");
			if(CUST_NAME!=null)
			{
				col4.setContent(CUST_NAME.toString());
			}
			else
			{
				col4.setContent("");
			}
			contextListFather.add(col4);
			
			ExcelEntity col5=new ExcelEntity();
			col5.setFormat(format);
			col5.setRowSpan(0);
			col5.setColSpan(0);
			col5.setRow(rowNum);
			col5.setCol(colNum++);
			Object BRAND=contentmap.get("BRAND");
			if(BRAND!=null)
			{
				col5.setContent(BRAND.toString());
			}
			else
			{
				col5.setContent("");
			}
			contextListFather.add(col5);
			
			ExcelEntity col6=new ExcelEntity();
			col6.setFormat(format);
			col6.setRowSpan(0);
			col6.setColSpan(0);
			col6.setRow(rowNum);
			col6.setCol(colNum++);
			Object CUSTOMER_COME=contentmap.get("CUSTOMER_COME");
			//System.out.println("CUSTOMER_COME:"+CUSTOMER_COME);
			if(CUSTOMER_COME!=null)
			{
				col6.setContent(CUSTOMER_COME.toString());
			}
			else
			{
				col6.setContent("");
			}
			contextListFather.add(col6);
			
			ExcelEntity col7=new ExcelEntity();
			col7.setFormat(format);
			col7.setRowSpan(0);
			col7.setColSpan(0);
			col7.setRow(rowNum);
			col7.setCol(colNum++);
			Object SPONSOR=contentmap.get("SPONSOR");
			if(SPONSOR!=null)
			{
					col7.setContent(SPONSOR.toString());
			}
			else
			{
				col7.setContent("");
			}
			contextListFather.add(col7);
			
			ExcelEntity col8=new ExcelEntity();
			col8.setFormat(format);
			col8.setRowSpan(0);
			col8.setColSpan(0);
			col8.setRow(rowNum);
			col8.setCol(colNum++);
			Object AREA=contentmap.get("AREA");
			if(AREA!=null)
			{
				col8.setContent(AREA.toString());
			}
			else
			{
				col8.setContent("");
			}
			contextListFather.add(col8);
			
			ExcelEntity col9=new ExcelEntity();
			col9.setFormat(format);
			col9.setRowSpan(0);
			col9.setColSpan(0);
			col9.setRow(rowNum);
			col9.setCol(colNum++);
			Object CONTRACT_TYPE=contentmap.get("CONTRACT_TYPE");
			if(CONTRACT_TYPE!=null)
			{
					col9.setContent(CONTRACT_TYPE.toString());
			}
			else
			{
				col9.setContent("");
			}
			contextListFather.add(col9);
			
			ExcelEntity col10=new ExcelEntity();
			col10.setFormat(format1);
			col10.setRowSpan(0);
			col10.setColSpan(0);
			col10.setRow(rowNum);
			col10.setCol(colNum++);
			Object PLEDGE_PRICE=contentmap.get("PLEDGE_PRICE");
			if(PLEDGE_PRICE!=null)
			{
					col10.setContent(this.updateMon(PLEDGE_PRICE.toString()));
			}
			else
			{
				col10.setContent("");
			}
			contextListFather.add(col10);
			
			ExcelEntity col11=new ExcelEntity();
			col11.setFormat(format1);
			col11.setRowSpan(0);
			col11.setColSpan(0);
			col11.setRow(rowNum);
			col11.setCol(colNum++);
			Object LEASE_TOPRIC=contentmap.get("LEASE_TOPRIC");
			if(LEASE_TOPRIC!=null)
			{
					col11.setContent(this.updateMon(LEASE_TOPRIC.toString()));
			}
			else
			{
				col11.setContent("");
			}
			contextListFather.add(col11);
			
			ExcelEntity col12=new ExcelEntity();
			col12.setFormat(format);
			col12.setRowSpan(0);
			col12.setColSpan(0);
			col12.setRow(rowNum);
			col12.setCol(colNum++);
			Object LEASE_PERIOD=contentmap.get("LEASE_PERIOD");
			if(LEASE_PERIOD!=null)
			{
				//System.out.println("LEASE_PERIOD="+LEASE_PERIOD);
					col12.setContent(LEASE_PERIOD.toString());
			}
			else
			{
				col12.setContent("");
			}
			contextListFather.add(col12);
			
			ExcelEntity col13=new ExcelEntity();
			col13.setFormat(format1);
			col13.setRowSpan(0);
			col13.setColSpan(0);
			col13.setRow(rowNum);
			col13.setCol(colNum++);
			
			//加入判断,如果是null,补0 add by shenqi 解决969的bug
			Object PLEDGE_AVE_PRICE=contentmap.get("PLEDGE_AVE_PRICE")==null?"0":contentmap.get("PLEDGE_AVE_PRICE");
			Object LEASETOPRIC=contentmap.get("LEASE_TOPRIC")==null?"0":contentmap.get("LEASE_TOPRIC");
			//System.out.println("PLEDGE_AVE_PRICE="+PLEDGE_AVE_PRICE);
			//System.out.println("LEASETOPRIC="+LEASETOPRIC);
			
			if(PLEDGE_AVE_PRICE!=null && LEASETOPRIC!=null)
			{
				//System.out.println("PLEDGE_AVE_PRICE="+PLEDGE_AVE_PRICE);
				//System.out.println("LEASETOPRIC="+LEASETOPRIC);
				float princ=Float.parseFloat(LEASETOPRIC.toString())-Float.parseFloat(PLEDGE_AVE_PRICE.toString());
				col13.setContent(this.updateMon(String.valueOf(princ)));
			}
			else
			{
				col13.setContent("");
			}
			contextListFather.add(col13);
			
			ExcelEntity col14=new ExcelEntity();
			col14.setFormat(format1);
			col14.setRowSpan(0);
			col14.setColSpan(0);
			col14.setRow(rowNum);
			col14.setCol(colNum++);
			Object PAY_MONEY=contentmap.get("PAY_MONEY");
			if(PAY_MONEY!=null)
			{
					col14.setContent(this.updateMon(PAY_MONEY.toString()));
			}
			else
			{
				col14.setContent("");
			}
			contextListFather.add(col14);
			
			ExcelEntity col17=new ExcelEntity();
			col17.setFormat(format);
			col17.setRowSpan(0);
			col17.setColSpan(0);
			col17.setRow(rowNum);
			col17.setCol(colNum++);
			Object LESSOR_TIME=contentmap.get("LESSOR_TIME");
			if(LESSOR_TIME!=null)
			{
					col17.setContent(LESSOR_TIME.toString());
			}
			else
			{
				col17.setContent("");
			}
			contextListFather.add(col17);
			
			ExcelEntity col15=new ExcelEntity();
			col15.setFormat(format);
			col15.setRowSpan(0);
			col15.setColSpan(0);
			col15.setRow(rowNum);
			col15.setCol(colNum++);
			Object MODIFY_DATE=contentmap.get("MODIFY_DATE");
			if(MODIFY_DATE!=null)
			{
				String modify=MODIFY_DATE.toString().substring(0,10);
					col15.setContent(modify);
			}
			else
			{
				col15.setContent("");
			}
			contextListFather.add(col15);
			
			ExcelEntity col16=new ExcelEntity();
			col16.setFormat(format);
			col16.setRowSpan(0);
			col16.setColSpan(0);
			col16.setRow(rowNum);
			col16.setCol(colNum++);
			Object START_DATE=contentmap.get("START_DATE");
			if(START_DATE!=null)
			{
				col16.setContent(START_DATE.toString());
			}
			else
			{
				col16.setContent("");
			}
			contextListFather.add(col16);
			
			sunNum++;
			if(rowNum==sunRowAdd)
			{
				sunNum=0;
				num++;
			}
			
			fatherNum++;
			if(rowNum==fatherRowAdd)
			{
				fatherNum=0;
				number++;
			}
			rowNum++;
			colNum=0;
		}
				
		
		return contextListFather;
		
	}
	
	
	 /**  财务格式  0.00 */
	private String updateMon(String content) {
	    String str="";
	    
	    if( content == null	|| DataUtil.doubleUtil(content)==0.0){
		
		str+="￥ 0.00";
		return str;
		
	    }
	    else{
		
		DecimalFormat df1 = new DecimalFormat("#,###.00"); 
		
		str+=df1.format(Double.parseDouble(content));
		str="￥ "+str;
		return str;
	    }	
	}	
	
}
