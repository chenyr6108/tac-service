package com.brick.activitiesLog.service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
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
import com.brick.report.service.ReportExcel;
import com.brick.service.entity.Context;
import com.brick.util.ExcelEntity;
import com.brick.util.PublicExcel;
import java.util.Date;

public class ActivitiesStatisticsExcel {
	
	Log logger = LogFactory.getLog(ReportExcel.class);
	String excelName=null;//该Excel导出文件名
	String excelHead=null;//该Excel头部信息
	int cell=0;//该Excel列数
	int row=0;//该Excel头部所占的行数
	WritableCellFormat format1=null;//该Excel列表的通用样式
	
	Date time=new Date();
	SimpleDateFormat sf = new SimpleDateFormat("yyyy");
	SimpleDateFormat sf1 = new SimpleDateFormat("MM");
	SimpleDateFormat sf2 = new SimpleDateFormat("dd");
	String year=sf.format(time).toString();
	String month=sf1.format(time).toString();
	String day=sf2.format(time).toString();
	
	
	//合计
	int zhi0=0;
	int zhi1=0;
	int zhi2=0;
	int zhi3=0;
	int zhi4=0;
	int zhi5=0;
	int zhi6=0;
	int zhi7=0;
	int zhi8=0;
	int zhi9=0;
	int zhi10=0;
	int zhi11=0;
	int zhi12=0;
	float zhi13=0;
	float zhi14=0;
	
	String year1=null;
	String month1=null;
	String day1=null;
	
	public void activitiesStatisticExcelJoin(List list,List count,Context context,String dateTimes)
	{
		ByteArrayOutputStream baos = null;
		excelName="业务人员活动日报表("+dateTimes+").xls";
		excelHead="业务人员活动日报表 ";
		
		year1=dateTimes.substring(0, 4);
		month1=dateTimes.substring(5, 7);
		day1=dateTimes.substring(8, 10);
		
		cell=17;
		row=2;
		format1=this.tableFont();
		List cellList=this.cellSome();
		List titleList=this.titleListMethod();
		List contentList=this.contextListMethod1(list,count);
		
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
		col0.setColSpan(16);
		col0.setRow(0);
		col0.setCol(0);
		col0.setContent("业务人员营业活动日报表("+month1+"月)");
		titleList.add(col0);
		
		//第一行
		//标题第一列（跨一行,一列,居中显示在第一行）
		//单位
		ExcelEntity col1=new ExcelEntity();
		col1.setFormat(format);
		col1.setRowSpan(0);
		col1.setColSpan(0);
		col1.setRow(1);
		col1.setCol(0);
		col1.setContent(year1+"");
		titleList.add(col1);
		
		//标题第二列（跨一行,一列,居中显示在第一行）
		//部門月目標
		ExcelEntity col2=new ExcelEntity();
		col2.setFormat(format);
		col2.setRowSpan(0);
		col2.setColSpan(0);
		col2.setRow(1);
		col2.setCol(1);
		col2.setContent("单位");
		titleList.add(col2);
		
		//业务
		ExcelEntity col3=new ExcelEntity();
		col3.setFormat(format);
		col3.setRowSpan(0);
		col3.setColSpan(0);
		col3.setRow(1);
		col3.setCol(2);
		col3.setContent("业代姓名");
		titleList.add(col3);
		
		//月目標金額
		ExcelEntity col4=new ExcelEntity();
		col4.setFormat(format);
		col4.setRowSpan(0);
		col4.setColSpan(5);
		col4.setRow(1);
		col4.setCol(3);
		col4.setContent("外出访客数");
		titleList.add(col4);
		
		//客户
		ExcelEntity col5=new ExcelEntity();
		col5.setFormat(format);
		col5.setRowSpan(0);
		col5.setColSpan(2);
		col5.setRow(1);
		col5.setCol(9);
		col5.setContent("有望客户数");
		titleList.add(col5);
		
		//厂牌
		ExcelEntity col6=new ExcelEntity();
		col6.setFormat(format);
		col6.setRowSpan(0);
		col6.setColSpan(0);
		col6.setRow(1);
		col6.setCol(12);
		col6.setContent("首次报价数");
		titleList.add(col6);
		
		//客户属性
		ExcelEntity col7=new ExcelEntity();
		col7.setFormat(format);
		col7.setRowSpan(0);
		col7.setColSpan(0);
		col7.setRow(1);
		col7.setCol(13);
		col7.setContent("送件数");
		titleList.add(col7);
		
		//来源
		ExcelEntity col8=new ExcelEntity();
		col8.setFormat(format);
		col8.setRowSpan(0);
		col8.setColSpan(0);
		col8.setRow(1);
		col8.setCol(14);
		col8.setContent("签约数");
		titleList.add(col8);
		
		//客户区域
		ExcelEntity col9=new ExcelEntity();
		col9.setFormat(format);
		col9.setRowSpan(0);
		col9.setColSpan(0);
		col9.setRow(1);
		col9.setCol(15);
		col9.setContent("入保证金");
		titleList.add(col9);
		
		//租赁方式
		ExcelEntity col10=new ExcelEntity();
		col10.setFormat(format);
		col10.setRowSpan(0);
		col10.setColSpan(0);
		col10.setRow(1);
		col10.setCol(16);
		col10.setContent("当日动拨金额");
		titleList.add(col10);
		
		//保证金
		ExcelEntity col11=new ExcelEntity();
		col11.setFormat(format);
		col11.setRowSpan(0);
		col11.setColSpan(0);
		col11.setRow(1);
		col11.setCol(17);
		col11.setContent("当月累计动拨金额");
		titleList.add(col11);
		
		//租金
		ExcelEntity col12=new ExcelEntity();
		col12.setFormat(format);
		col12.setRowSpan(0);
		col12.setColSpan(0);
		col12.setRow(2);
		col12.setCol(0);
		col12.setContent("日期");
		titleList.add(col12);
		
		//2－3天
		ExcelEntity col13=new ExcelEntity();
		col13.setFormat(format);
		col13.setRowSpan(0);
		col13.setColSpan(0);
		col13.setRow(2);
		col13.setCol(1);
		col13.setContent("");
		titleList.add(col13);
		
		
		//申请拨款金额
		ExcelEntity col14=new ExcelEntity();
		col14.setFormat(format);
		col14.setRowSpan(0);
		col14.setColSpan(0);
		col14.setRow(2);
		col14.setCol(2);
		col14.setContent("");
		titleList.add(col14);
		
		//首次拜访
		ExcelEntity col00=new ExcelEntity();
		col00.setFormat(format);
		col00.setRowSpan(0);
		col00.setColSpan(0);
		col00.setRow(2);
		col00.setCol(3);
		col00.setContent("首次拜访");
		titleList.add(col00);
		
		//已撥款額
		ExcelEntity col15=new ExcelEntity();
		col15.setFormat(format);
		col15.setRowSpan(0);
		col15.setColSpan(0);
		col15.setRow(2);
		col15.setCol(4);
		col15.setContent("新开拓数");
		titleList.add(col15);
		
		//第一次拨款日期
		ExcelEntity col16=new ExcelEntity();
		col16.setFormat(format);
		col16.setRowSpan(0);
		col16.setColSpan(0);
		col16.setRow(2);
		col16.setCol(5);
		col16.setContent("勘厂");
		titleList.add(col16);
		
		//起租日期
		ExcelEntity col17=new ExcelEntity();
		col17.setFormat(format);
		col17.setRowSpan(0);
		col17.setColSpan(0);
		col17.setRow(2);
		col17.setCol(6);
		col17.setContent("客户服务");
		titleList.add(col17);
		
		//经销商拜访
		ExcelEntity col18=new ExcelEntity();
		col18.setFormat(format);
		col18.setRowSpan(0);
		col18.setColSpan(0);
		col18.setRow(2);
		col18.setCol(7);
		col18.setContent("经销商拜访");
		titleList.add(col18);
		
		//回访
		ExcelEntity col19=new ExcelEntity();
		col19.setFormat(format);
		col19.setRowSpan(0);
		col19.setColSpan(0);
		col19.setRow(2);
		col19.setCol(8);
		col19.setContent("回访");
		titleList.add(col19);
		
		//H
		ExcelEntity col20=new ExcelEntity();
		col20.setFormat(format);
		col20.setRowSpan(0);
		col20.setColSpan(0);
		col20.setRow(2);
		col20.setCol(9);
		col20.setContent("H");
		titleList.add(col20);
		
		//A
		ExcelEntity col21=new ExcelEntity();
		col21.setFormat(format);
		col21.setRowSpan(0);
		col21.setColSpan(0);
		col21.setRow(2);
		col21.setCol(10);
		col21.setContent("A");
		titleList.add(col21);
		
		//B
		ExcelEntity col22=new ExcelEntity();
		col22.setFormat(format);
		col22.setRowSpan(0);
		col22.setColSpan(0);
		col22.setRow(2);
		col22.setCol(11);
		col22.setContent("B");
		titleList.add(col22);
		
		//
		ExcelEntity col23=new ExcelEntity();
		col23.setFormat(format);
		col23.setRowSpan(0);
		col23.setColSpan(0);
		col23.setRow(2);
		col23.setCol(12);
		col23.setContent("");
		titleList.add(col23);
		
		//B
		ExcelEntity col24=new ExcelEntity();
		col24.setFormat(format);
		col24.setRowSpan(0);
		col24.setColSpan(0);
		col24.setRow(2);
		col24.setCol(13);
		col24.setContent("");
		titleList.add(col24);
		
		//B
		ExcelEntity col25=new ExcelEntity();
		col25.setFormat(format);
		col25.setRowSpan(0);
		col25.setColSpan(0);
		col25.setRow(2);
		col25.setCol(14);
		col25.setContent("");
		titleList.add(col25);
		
		//
		ExcelEntity col26=new ExcelEntity();
		col26.setFormat(format);
		col26.setRowSpan(0);
		col26.setColSpan(0);
		col26.setRow(2);
		col26.setCol(15);
		col26.setContent("");
		titleList.add(col26);
		
		//B
		ExcelEntity col27=new ExcelEntity();
		col27.setFormat(format);
		col27.setRowSpan(0);
		col27.setColSpan(0);
		col27.setRow(2);
		col27.setCol(16);
		col27.setContent("");
		titleList.add(col27);
		
		//B
		ExcelEntity col28=new ExcelEntity();
		col28.setFormat(format);
		col28.setRowSpan(0);
		col28.setColSpan(0);
		col28.setRow(2);
		col28.setCol(17);
		col28.setContent("");
		titleList.add(col28);
		
		
		ExcelEntity col29=new ExcelEntity();
		col29.setFormat(format);
		col29.setRowSpan(0);
		col29.setColSpan(0);
		col29.setRow(3);
		col29.setCol(0);
		col29.setContent(month1+"月"+day1+"日");
		titleList.add(col29);
		
		
		return titleList;
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
	
	
	public List contextListMethod1(List contextList,List count)
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
		int colNum=1; //列
		int rowNum=3; //行
		
		int number=0;//档运行完一个部门为自加一次
		
		int fatherRow=0;//获取部门总条数
		int fatherRowAdd=0;//将部门总条数相加
		int fatherId=0;//获取部门Id
		int fatherNum=0;//当运行玩一个部门会将其重置为0
		
		
		
		
		
		for(int i=0;i<contextList.size();i++)
		{
			Map contentmap=(Map)contextList.get(i);
			Map fatherCount=null;
			if(fatherNum==0)
			{
				if(count!=null)
				{
					if(count.size()>=number)
					{
						//System.out.println("number======"+number);
						fatherCount=(Map)count.get(number);
					}
				}
				
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
					
			}
			colNum=2;
			
			ExcelEntity col0=new ExcelEntity();
			
			if(contentmap.get("TOTAL")!=null)
			{
				zhi0+=Integer.parseInt(contentmap.get("TOTAL").toString());
				col0.setContent(contentmap.get("TOTAL").toString());
			}
			else
			{
				col0.setContent("0");
			}
			contextListFather.add(col0);
			
			ExcelEntity col1=new ExcelEntity();
			col1.setFormat(format);
			col1.setRowSpan(0);
			col1.setColSpan(0);
			col1.setRow(rowNum);
			col1.setCol(colNum++);
			if(contentmap.get("USERS_NAME")!=null)
			{
				col1.setContent(contentmap.get("USERS_NAME").toString());
			}
			else
			{
				col1.setContent("");
			}
			contextListFather.add(col1);
			
			col0.setFormat(format);
			col0.setRowSpan(0);
			col0.setColSpan(0);
			col0.setRow(rowNum);
			col0.setCol(colNum++);
			
			
			
				
				ExcelEntity col2=new ExcelEntity();
				col2.setFormat(format);
				col2.setRowSpan(0);
				col2.setColSpan(0);
				col2.setRow(rowNum);
				col2.setCol(colNum++);
				Object CLERK_NAME=contentmap.get("CREATECOUNT");
				if(CLERK_NAME!=null)
				{
					zhi1+=Integer.parseInt(CLERK_NAME.toString());
					col2.setContent(CLERK_NAME.toString());
				}
				else
				{
					col2.setContent("0");
				}
				contextListFather.add(col2);
				
				ExcelEntity col3=new ExcelEntity();
				col3.setFormat(format);
				col3.setRowSpan(0);
				col3.setColSpan(0);
				col3.setRow(rowNum);
				col3.setCol(colNum++);
				Object EMPL_ACHIEVEMENT=contentmap.get("VISITFACTORYCOUNT");
				if(EMPL_ACHIEVEMENT!=null)
				{
					zhi2+=Integer.parseInt(EMPL_ACHIEVEMENT.toString());
					col3.setContent(EMPL_ACHIEVEMENT.toString());
				}
				else
				{
					col3.setContent("0");
				}
				contextListFather.add(col3);
				
			
			
			colNum=6;
			ExcelEntity col4=new ExcelEntity();
			col4.setFormat(format);
			col4.setRowSpan(0);
			col4.setColSpan(0);
			col4.setRow(rowNum);
			col4.setCol(colNum++);
			Object CUST_NAME=contentmap.get("FUHECOUNT");
			if(CUST_NAME!=null)
			{
				zhi3+=Integer.parseInt(CUST_NAME.toString());
				col4.setContent(CUST_NAME.toString());
			}
			else
			{
				col4.setContent("0");
			}
			contextListFather.add(col4);
			
			ExcelEntity col5=new ExcelEntity();
			col5.setFormat(format);
			col5.setRowSpan(0);
			col5.setColSpan(0);
			col5.setRow(rowNum);
			col5.setCol(colNum++);
			Object BRAND=contentmap.get("CUSTVISITCOUNT");
			if(BRAND!=null)
			{
				zhi4+=Integer.parseInt(BRAND.toString());
				col5.setContent(BRAND.toString());
			}
			else
			{
				col5.setContent("0");
			}
			contextListFather.add(col5);
			
			ExcelEntity col6=new ExcelEntity();
			col6.setFormat(format);
			col6.setRowSpan(0);
			col6.setColSpan(0);
			col6.setRow(rowNum);
			col6.setCol(colNum++);
			Object CUSTOMER_COME=contentmap.get("BACKVISITCOUNT");
			if(CUSTOMER_COME!=null)
			{
				zhi5+=Integer.parseInt(CUSTOMER_COME.toString());
				col6.setContent(CUSTOMER_COME.toString());
			}
			else
			{
				col6.setContent("0");
			}
			contextListFather.add(col6);
			
			ExcelEntity col7=new ExcelEntity();
			col7.setFormat(format);
			col7.setRowSpan(0);
			col7.setColSpan(0);
			col7.setRow(rowNum);
			col7.setCol(colNum++);
			Object SPONSOR=contentmap.get("HCOUNT");
			if(SPONSOR!=null)
			{
				zhi6+=Integer.parseInt(SPONSOR.toString());
					col7.setContent(SPONSOR.toString());
			}
			else
			{
				col7.setContent("0");
			}
			contextListFather.add(col7);
			
			ExcelEntity col8=new ExcelEntity();
			col8.setFormat(format);
			col8.setRowSpan(0);
			col8.setColSpan(0);
			col8.setRow(rowNum);
			col8.setCol(colNum++);
			Object AREA=contentmap.get("ACOUNT");
			if(AREA!=null)
			{
				zhi7+=Integer.parseInt(AREA.toString());
				col8.setContent(AREA.toString());
			}
			else
			{
				col8.setContent("0");
			}
			contextListFather.add(col8);
			
			ExcelEntity col9=new ExcelEntity();
			col9.setFormat(format);
			col9.setRowSpan(0);
			col9.setColSpan(0);
			col9.setRow(rowNum);
			col9.setCol(colNum++);
			Object CONTRACT_TYPE=contentmap.get("BCOUNT");
			if(CONTRACT_TYPE!=null)
			{
				zhi8+=Integer.parseInt(CONTRACT_TYPE.toString());
					col9.setContent(CONTRACT_TYPE.toString());
			}
			else
			{
				col9.setContent("0");
			}
			contextListFather.add(col9);
			
			ExcelEntity col10=new ExcelEntity();
			col10.setFormat(format);
			col10.setRowSpan(0);
			col10.setColSpan(0);
			col10.setRow(rowNum);
			col10.setCol(colNum++);
			Object PLEDGE_PRICE=contentmap.get("FIRSTPRICECOUNT");
			if(PLEDGE_PRICE!=null)
			{
				zhi9+=Integer.parseInt(PLEDGE_PRICE.toString());
					col10.setContent(PLEDGE_PRICE.toString());
			}
			else
			{
				col10.setContent("0");
			}
			contextListFather.add(col10);
			
			ExcelEntity col11=new ExcelEntity();
			col11.setFormat(format);
			col11.setRowSpan(0);
			col11.setColSpan(0);
			col11.setRow(rowNum);
			col11.setCol(colNum++);
			Object LEASE_TOPRIC=contentmap.get("SENDCOUNT");
			if(LEASE_TOPRIC!=null)
			{
				zhi10+=Integer.parseInt(LEASE_TOPRIC.toString());
					col11.setContent(LEASE_TOPRIC.toString());
			}
			else
			{
				col11.setContent("0");
			}
			contextListFather.add(col11);
			
			ExcelEntity col12=new ExcelEntity();
			col12.setFormat(format);
			col12.setRowSpan(0);
			col12.setColSpan(0);
			col12.setRow(rowNum);
			col12.setCol(colNum++);
			Object LEASE_PERIOD=contentmap.get("SHENGHECOUNT");
			if(LEASE_PERIOD!=null)
			{
				zhi11+=Integer.parseInt(LEASE_PERIOD.toString());
				//System.out.println("LEASE_PERIOD="+LEASE_PERIOD);
					col12.setContent(LEASE_PERIOD.toString());
			}
			else
			{
				col12.setContent("0");
			}
			contextListFather.add(col12);
			
			
			
			ExcelEntity col14=new ExcelEntity();
			col14.setFormat(format);
			col14.setRowSpan(0);
			col14.setColSpan(0);
			col14.setRow(rowNum);
			col14.setCol(colNum++);
			Object PAY_MONEY=contentmap.get("MARGINCOUNT");
			if(PAY_MONEY!=null)
			{
				zhi12+=Integer.parseInt(PAY_MONEY.toString());
					col14.setContent(PAY_MONEY.toString());
			}
			else
			{
				col14.setContent("0");
			}
			contextListFather.add(col14);
			
			ExcelEntity col15=new ExcelEntity();
			col15.setFormat(format);
			col15.setRowSpan(0);
			col15.setColSpan(0);
			col15.setRow(rowNum);
			col15.setCol(colNum++);
			Object MODIFY_DATE=contentmap.get("PAY_MONEYDAYCOUNT");
			if(MODIFY_DATE!=null)
			{
				zhi13+=Float.parseFloat(MODIFY_DATE.toString());
					col15.setContent(MODIFY_DATE.toString());
			}
			else
			{
				col15.setContent("0");
			}
			contextListFather.add(col15);
			
			ExcelEntity col16=new ExcelEntity();
			col16.setFormat(format);
			col16.setRowSpan(0);
			col16.setColSpan(0);
			col16.setRow(rowNum);
			col16.setCol(colNum++);
			Object START_DATE=contentmap.get("PAY_MONEYMONTHCOUNT");
			if(START_DATE!=null)
			{
				zhi14+=Float.parseFloat(START_DATE.toString());
				col16.setContent(START_DATE.toString());
			}
			else
			{
				col16.setContent("0");
			}
			contextListFather.add(col16);
			
			fatherNum++;
			if(rowNum==fatherRowAdd+2)
			{
				fatherNum=0;
				number++;
			}
			rowNum++;
			colNum=1;
		}
		
		colNum=0;
		
		ExcelEntity coll=new ExcelEntity();
		coll.setFormat(format);
		coll.setRowSpan(0);
		coll.setColSpan(0);
		coll.setRow(rowNum);
		coll.setCol(colNum++);
		
		coll.setContent("");
		
		contextListFather.add(coll);
		
		ExcelEntity coll1=new ExcelEntity();
		coll1.setFormat(format);
		coll1.setRowSpan(0);
		coll1.setColSpan(0);
		coll1.setRow(rowNum);
		coll1.setCol(colNum++);
		
		coll1.setContent("");
		
		contextListFather.add(coll1);
		
		
		ExcelEntity col1=new ExcelEntity();
		col1.setFormat(format);
		col1.setRowSpan(0);
		col1.setColSpan(0);
		col1.setRow(rowNum);
		col1.setCol(colNum++);
		
			col1.setContent("合计");
		
		contextListFather.add(col1);
		
		
		ExcelEntity col00=new ExcelEntity();
		col00.setFormat(format);
		col00.setRowSpan(0);
		col00.setColSpan(0);
		col00.setRow(rowNum);
		col00.setCol(colNum++);
		
		col00.setContent(zhi0+"");
		contextListFather.add(col00);
		
		ExcelEntity col0=new ExcelEntity();
		col0.setFormat(format);
		col0.setRowSpan(0);
		col0.setColSpan(0);
		col0.setRow(rowNum);
		col0.setCol(colNum++);
		
			col0.setContent(zhi1+"");
		contextListFather.add(col0);
		
			
			
			ExcelEntity col3=new ExcelEntity();
			col3.setFormat(format);
			col3.setRowSpan(0);
			col3.setColSpan(0);
			col3.setRow(rowNum);
			col3.setCol(colNum++);
			col3.setContent(zhi2+"");
			contextListFather.add(col3);
			
		
		ExcelEntity col4=new ExcelEntity();
		col4.setFormat(format);
		col4.setRowSpan(0);
		col4.setColSpan(0);
		col4.setRow(rowNum);
		col4.setCol(colNum++);
		col4.setContent(zhi3+"");
		contextListFather.add(col4);
		
		ExcelEntity col5=new ExcelEntity();
		col5.setFormat(format);
		col5.setRowSpan(0);
		col5.setColSpan(0);
		col5.setRow(rowNum);
		col5.setCol(colNum++);
		col5.setContent(zhi4+"");
		contextListFather.add(col5);
		
		ExcelEntity col6=new ExcelEntity();
		col6.setFormat(format);
		col6.setRowSpan(0);
		col6.setColSpan(0);
		col6.setRow(rowNum);
		col6.setCol(colNum++);
		col6.setContent(zhi5+"");
		contextListFather.add(col6);
		
		ExcelEntity col7=new ExcelEntity();
		col7.setFormat(format);
		col7.setRowSpan(0);
		col7.setColSpan(0);
		col7.setRow(rowNum);
		col7.setCol(colNum++);
		col7.setContent(zhi6+"");
		contextListFather.add(col7);
		
		ExcelEntity col8=new ExcelEntity();
		col8.setFormat(format);
		col8.setRowSpan(0);
		col8.setColSpan(0);
		col8.setRow(rowNum);
		col8.setCol(colNum++);
		col8.setContent(zhi7+"");
		contextListFather.add(col8);
		
		ExcelEntity col9=new ExcelEntity();
		col9.setFormat(format);
		col9.setRowSpan(0);
		col9.setColSpan(0);
		col9.setRow(rowNum);
		col9.setCol(colNum++);
		col9.setContent(zhi8+"");
		contextListFather.add(col9);
		
		ExcelEntity col10=new ExcelEntity();
		col10.setFormat(format);
		col10.setRowSpan(0);
		col10.setColSpan(0);
		col10.setRow(rowNum);
		col10.setCol(colNum++);
		col10.setContent(zhi9+"");
		contextListFather.add(col10);
		
		ExcelEntity col11=new ExcelEntity();
		col11.setFormat(format);
		col11.setRowSpan(0);
		col11.setColSpan(0);
		col11.setRow(rowNum);
		col11.setCol(colNum++);
		col11.setContent(zhi10+"");
		contextListFather.add(col11);
		
		ExcelEntity col12=new ExcelEntity();
		col12.setFormat(format);
		col12.setRowSpan(0);
		col12.setColSpan(0);
		col12.setRow(rowNum);
		col12.setCol(colNum++);
		col12.setContent(zhi11+"");
		contextListFather.add(col12);
		
		
		
		ExcelEntity col14=new ExcelEntity();
		col14.setFormat(format);
		col14.setRowSpan(0);
		col14.setColSpan(0);
		col14.setRow(rowNum);
		col14.setCol(colNum++);
		col14.setContent(zhi12+"");
		contextListFather.add(col14);
		
		ExcelEntity col15=new ExcelEntity();
		col15.setFormat(format);
		col15.setRowSpan(0);
		col15.setColSpan(0);
		col15.setRow(rowNum);
		col15.setCol(colNum++);
		col15.setContent(zhi13+"");
		contextListFather.add(col15);
		
		ExcelEntity col16=new ExcelEntity();
		col16.setFormat(format);
		col16.setRowSpan(0);
		col16.setColSpan(0);
		col16.setRow(rowNum);
		col16.setCol(colNum++);
		col16.setContent(zhi14+"");
		contextListFather.add(col16);
		
		
		
				
		
		return contextListFather;
		
	}
	
	
}
