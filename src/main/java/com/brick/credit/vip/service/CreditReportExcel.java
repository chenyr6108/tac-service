package com.brick.credit.vip.service;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.brick.util.DataUtil;
import com.brick.util.ExcelEntity;
import com.brick.util.PublicExcel;

public class CreditReportExcel {

	
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
	SimpleDateFormat sf3 = new SimpleDateFormat("yyyy-MM-dd");
	String year=sf.format(time).toString();
	String month=sf1.format(time).toString();
	String day=sf2.format(time).toString();
	
	
	
	public void creditReportExcelJoin(List list,List count,Context context)
	{
		ByteArrayOutputStream baos = null;
		excelName="Custreport.xls";
		excelHead="客户案况 ";
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
		cellList.add(15);
		cellList.add(15);
		cellList.add(15);
		cellList.add(15);
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
		WritableCellFormat format2=this.tableFont3();//标题通用样式
		ExcelEntity col0=new ExcelEntity();
		col0.setFormat(format1);
		col0.setRowSpan(0);
		col0.setColSpan(16);
		col0.setRow(0);
		col0.setCol(0);
		col0.setContent("客户案况表");
		titleList.add(col0);
		
		//第一行
		//标题第一列（跨一行,一列,居中显示在第一行）
		//单位
		
		
		//标题第二列（跨一行,一列,居中显示在第一行）
		//部門月目標
		ExcelEntity col2=new ExcelEntity();
		col2.setFormat(format);
		col2.setRowSpan(0);
		col2.setColSpan(0);
		col2.setRow(1);
		col2.setCol(0);
		col2.setContent("单位");
		titleList.add(col2);
		
		//业务
		ExcelEntity col3=new ExcelEntity();
		col3.setFormat(format);
		col3.setRowSpan(0);
		col3.setColSpan(0);
		col3.setRow(1);
		col3.setCol(1);
		col3.setContent("客户编号");
		titleList.add(col3);
		
		//月目標金額
		ExcelEntity col4=new ExcelEntity();
		col4.setFormat(format);
		col4.setRowSpan(0);
		col4.setColSpan(0);
		col4.setRow(1);
		col4.setCol(2);
		col4.setContent("客户名称");
		titleList.add(col4);
		
		//客户
		ExcelEntity col5=new ExcelEntity();
		col5.setFormat(format);
		col5.setRowSpan(0);
		col5.setColSpan(0);
		col5.setRow(1);
		col5.setCol(3);
		col5.setContent("介绍人");
		titleList.add(col5);
		
		//厂牌
		ExcelEntity col6=new ExcelEntity();
		col6.setFormat(format);
		col6.setRowSpan(0);
		col6.setColSpan(0);
		col6.setRow(1);
		col6.setCol(4);
		col6.setContent("供应商");
		titleList.add(col6);
		
		//客户属性
		ExcelEntity col7=new ExcelEntity();
		col7.setFormat(format);
		col7.setRowSpan(0);
		col7.setColSpan(0);
		col7.setRow(1);
		col7.setCol(5);
		col7.setContent("租赁方式");
		titleList.add(col7);
		
		//来源
		ExcelEntity col8=new ExcelEntity();
		col8.setFormat(format);
		col8.setRowSpan(0);
		col8.setColSpan(0);
		col8.setRow(1);
		col8.setCol(6);
		col8.setContent("租赁物概要");
		titleList.add(col8);
		
		//客户区域
		ExcelEntity col9=new ExcelEntity();
		col9.setFormat(format);
		col9.setRowSpan(0);
		col9.setColSpan(0);
		col9.setRow(1);
		col9.setCol(7);
		col9.setContent("申请额度");
		titleList.add(col9);
		
		//租赁方式
		ExcelEntity col10=new ExcelEntity();
		col10.setFormat(format);
		col10.setRowSpan(0);
		col10.setColSpan(0);
		col10.setRow(1);
		col10.setCol(8);
		col10.setContent("案件状况");
		titleList.add(col10);
		
		//保证金
		ExcelEntity col11=new ExcelEntity();
		col11.setFormat(format);
		col11.setRowSpan(0);
		col11.setColSpan(0);
		col11.setRow(1);
		col11.setCol(9);
		col11.setContent("经办");
		titleList.add(col11);
		
		//租金
		ExcelEntity col12=new ExcelEntity();
		col12.setFormat(format);
		col12.setRowSpan(0);
		col12.setColSpan(0);
		col12.setRow(1);
		col12.setCol(10);
		col12.setContent("核准额度");
		titleList.add(col12);
		
		//2－3天
		ExcelEntity col13=new ExcelEntity();
		col13.setFormat(format);
		col13.setRowSpan(0);
		col13.setColSpan(0);
		col13.setRow(1);
		col13.setCol(11);
		col13.setContent("访厂日");
		titleList.add(col13);
		
		
		//申请拨款金额
		ExcelEntity col14=new ExcelEntity();
		col14.setFormat(format);
		col14.setRowSpan(0);
		col14.setColSpan(0);
		col14.setRow(1);
		col14.setCol(12);
		col14.setContent("送件日");
		titleList.add(col14);
		
		//已撥款額
		ExcelEntity col15=new ExcelEntity();
		col15.setFormat(format);
		col15.setRowSpan(0);
		col15.setColSpan(0);
		col15.setRow(1);
		col15.setCol(13);
		col15.setContent("核准日期");
		titleList.add(col15);
		
		//第一次拨款日期
		ExcelEntity col16=new ExcelEntity();
		col16.setFormat(format);
		col16.setRowSpan(0);
		col16.setColSpan(0);
		col16.setRow(1);
		col16.setCol(14);
		col16.setContent("预估拨款日");
		titleList.add(col16);
		
		//起租日期
		ExcelEntity col17=new ExcelEntity();
		col17.setFormat(format);
		col17.setRowSpan(0);
		col17.setColSpan(0);
		col17.setRow(1);
		col17.setCol(15);
		col17.setContent("启租额度");
		titleList.add(col17);
		
		//经销商拜访
		ExcelEntity col18=new ExcelEntity();
		col18.setFormat(format);
		col18.setRowSpan(0);
		col18.setColSpan(0);
		col18.setRow(1);
		col18.setCol(16);
		col18.setContent("启租日期");
		titleList.add(col18);
		
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
	
	public WritableCellFormat tableFont3()
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
		WritableCellFormat format2=this.tableFont3();
		int colNum=0; //列
		int rowNum=2; //行
		
		int number=0;//档运行完一个部门为自加一次
		
		int fatherRow=0;//获取部门总条数
		int fatherRowAdd=0;//将部门总条数相加
		int fatherId=0;//获取部门Id
		int fatherNum=0;//当运行玩一个部门会将其重置为0
		
		
		
		
		
		for(int i=0;i<contextList.size();i++)
		{
			Map contentmap=(Map)contextList.get(i);
			Map fatherCount=null;
//			if(fatherNum==0)
//			{
//				if(count!=null)
//				{
//					if(count.size()>=number)
//					{
//						//System.out.println("number======"+number);
//						fatherCount=(Map)count.get(number);
//						//System.out.println("----------------------------------运行了几次="+number+",为="+fatherCount);
//					}
//				}
//				
//				if(fatherCount.get("DEPT_COUNT")!=null)
//				{
//					fatherRow=Integer.parseInt(fatherCount.get("DEPT_COUNT").toString());
//					if(fatherRowAdd==0)
//					{
//						fatherRowAdd=fatherRow;
//					}
//					else
//					{
//						fatherRowAdd+=fatherRow;
//					}
//				}
//				if(fatherCount.get("DECP_ID")!=null)
//				{
//					fatherId=Integer.parseInt(fatherCount.get("DECP_ID").toString());
//				}
//				
//					ExcelEntity col=new ExcelEntity();
//					col.setFormat(format);
//					col.setRowSpan(fatherRow-1);
//					col.setColSpan(0);
//					col.setRow(rowNum);
//					col.setCol(colNum++);
//					Object DECP_NAME_CN=contentmap.get("DECP_NAME_CN");
//					if(DECP_NAME_CN!=null)
//					{
//						col.setContent(DECP_NAME_CN.toString());
//					}
//					else
//					{
//						col.setContent("");
//					}
//					contextListFather.add(col);
//					
//			}
			ExcelEntity col=new ExcelEntity();
			col.setFormat(format);
			col.setRowSpan(0);
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
			colNum=1;
			
			ExcelEntity col1=new ExcelEntity();
			col1.setFormat(format1);
			col1.setRowSpan(0);
			col1.setColSpan(0);
			col1.setRow(rowNum);
			col1.setCol(colNum++);
			if(contentmap.get("CUST_CODE")!=null)
			{
				col1.setContent(contentmap.get("CUST_CODE").toString());
			}
			else
			{
				col1.setContent("");
			}
			contextListFather.add(col1);
			
			
			
			
				
				ExcelEntity col2=new ExcelEntity();
				col2.setFormat(format);
				col2.setRowSpan(0);
				col2.setColSpan(0);
				col2.setRow(rowNum);
				col2.setCol(colNum++);
				Object CLERK_NAME=contentmap.get("CUST_NAME");
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
				col3.setRowSpan(0);
				col3.setColSpan(0);
				col3.setRow(rowNum);
				col3.setCol(colNum++);
				Object EMPL_ACHIEVEMENT=contentmap.get("SPONSOR");
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
			col4.setRow(rowNum);
			col4.setCol(colNum++);
			Object CUST_NAME=contentmap.get("BRAND");
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
			Object BRAND=contentmap.get("ZULINTYPE");
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
			Object CUSTOMER_COME=contentmap.get("CREDIT_VALUE");
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
			col7.setFormat(format2);
			col7.setRowSpan(0);
			col7.setColSpan(0);
			col7.setRow(rowNum);
			col7.setCol(colNum++);
			Object SPONSOR=contentmap.get("SQBKJE");
//			Object SPONSOR=contentmap.get("PLEDGE_PRICE");
			if(SPONSOR!=null)
			{
					col7.setContent(this.updateMon(SPONSOR.toString()));
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
			Object AREA=contentmap.get("CASESTATE");
			if(AREA!=null)
			{
				String stateStr=AREA.toString();
				col8.setContent(stateStr);
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
			Object CONTRACT_TYPE=contentmap.get("SENSORNAME");
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
			col10.setFormat(format2);
			col10.setRowSpan(0);
			col10.setColSpan(0);
			col10.setRow(rowNum);
			col10.setCol(colNum++);
			Object PLEDGE_PRICE=contentmap.get("SQBKJE");
//			Object PLEDGE_PRICE=contentmap.get("LEASE_TOPRIC");
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
			Object LEASE_TOPRIC=contentmap.get("VISITFACTORYDATE");
			if(LEASE_TOPRIC!=null)
			{
				String day=contentmap.get("VISITFACTORYDATE").toString();
					col11.setContent(day);
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
			Object LEASE_PERIOD=contentmap.get("CREATE_DATE");
			if(LEASE_PERIOD!=null)
			{
				String day=sf3.format(contentmap.get("CREATE_DATE")).toString();
				//System.out.println("LEASE_PERIOD="+LEASE_PERIOD);
					col12.setContent(day);
			}
			else
			{
				col12.setContent("");
			}
			contextListFather.add(col12);
			
			
			
			ExcelEntity col14=new ExcelEntity();
			col14.setFormat(format1);
			col14.setRowSpan(0);
			col14.setColSpan(0);
			col14.setRow(rowNum);
			col14.setCol(colNum++);
			Object PAY_MONEY=contentmap.get("HEZHUNTIME");
			if(PAY_MONEY!=null)
			{
				String day=sf3.format(contentmap.get("HEZHUNTIME")).toString();
					col14.setContent(day);
			}
			else
			{
				col14.setContent("");
			}
			contextListFather.add(col14);
			
			ExcelEntity col15=new ExcelEntity();
			col15.setFormat(format);
			col15.setRowSpan(0);
			col15.setColSpan(0);
			col15.setRow(rowNum);
			col15.setCol(colNum++);
//			Object MODIFY_DATE=contentmap.get("PAY_MONEYDAYCOUNT");
//			if(MODIFY_DATE!=null)
//			{
//					col15.setContent(MODIFY_DATE.toString());
//			}
//			else
//			{
//				col15.setContent("0");
//			}
			col15.setContent("");
			contextListFather.add(col15);
			
			ExcelEntity col16=new ExcelEntity();
			col16.setFormat(format2);
			col16.setRowSpan(0);
			col16.setColSpan(0);
			col16.setRow(rowNum);
			col16.setCol(colNum++);
			Object START_DATE=contentmap.get("SQBKJE");
//			Object START_DATE=contentmap.get("START_TOPRIC");
			if(START_DATE!=null)
			{
				col16.setContent(this.updateMon(START_DATE.toString()));
			}
			else
			{
				col16.setContent("");
			}
			contextListFather.add(col16);
			
			ExcelEntity col17=new ExcelEntity();
			col17.setFormat(format);
			col17.setRowSpan(0);
			col17.setColSpan(0);
			col17.setRow(rowNum);
			col17.setCol(colNum++);
			Object START_DATEss=contentmap.get("START_DATE");
			if(START_DATEss!=null)
			{
				String day=sf3.format(contentmap.get("START_DATE")).toString();
				col17.setContent(day);
			}
			else
			{
				col17.setContent("");
			}
			contextListFather.add(col17);
			
			fatherNum++;
			rowNum++;
			if(rowNum==fatherRowAdd+2)
			{
				fatherNum=0;
				number++;
			}
			
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
		
		DecimalFormat df1 = new DecimalFormat("#,##0.00"); 
		
		str+=df1.format(Double.parseDouble(content));
		str="￥ "+str;
		return str;
	    }	
	}	
}
