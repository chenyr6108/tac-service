package com.brick.decompose.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.log.service.LogPrint;
import com.brick.report.service.ReportExcel;
import com.brick.service.entity.Context;
import com.brick.util.ExcelEntity;
import com.brick.util.PublicExcel;

public class ShowDayIncomeExcel 
{
	
	WritableWorkbook wb = null;
	WorkbookSettings workbookSettings = new WorkbookSettings();
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
	@SuppressWarnings("unchecked")
	List fatherlist=new ArrayList();

	
	
	public void createexl() {
		try {
			wb = Workbook.createWorkbook(baos, workbookSettings);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	Log logger = LogFactory.getLog(ReportExcel.class);
	String excelName=null;//该Excel导出文件名
	String excelHead=null;//该Excel头部信息
	int cell=8;//该Excel列数
	int row=0;//该Excel头部所占的行数
	WritableCellFormat format1=null;//该Excel列表的通用样式
	
	
	@SuppressWarnings("unchecked")
	public void INcomeExcelJoin(List list,Context context)
	{
		ByteArrayOutputStream baos = null;
		
		String time=context.contextMap.get("dateState").toString();
		excelName="每日来款租金分解明细表("+time+").xls";
		
		for(int i=0;i<list.size();i++){
			Map listMap=(Map)list.get(i);
			
			Map finaComeByDay=(Map)listMap.get("finaComeByDay");
			Map finaComeByOldDay=(Map)listMap.get("finaComeByOldDay");
			List finaComeAllRent=(List)listMap.get("finaComeAllRent");
			List finaComeAllMoney=(List)listMap.get("finaComeAllMoney");
			List finaIncome=(List)listMap.get("finaIncome");
			//将没有使用过的待分解来款 加入到内容里去
			for(int x = 0; x < finaComeAllRent.size() ;x++){
				Map temp = (Map) finaComeAllRent.get(x) ;
				temp.put("RECP_ID", 0) ;
			}
			for(int x = 0; x < finaComeAllMoney.size() ;x++){
				Map temp = (Map) finaComeAllMoney.get(x) ;
				temp.put("RECP_ID", 0) ;
			}
			if(finaIncome != null){
				for(int j=0;j<finaIncome.size();j++){
					Map temp = (Map) finaIncome.get(j) ;
					Map map = new HashMap() ;
					map.put("RECP_ID", 0) ;
					map.put("FIIN_ID", temp.get("FIIN_ID")) ;
					finaComeAllRent.add(map) ;
					map.put("OPPOSING_DATE", temp.get("OPPOSING_DATE")) ;
					map.put("CUST_NAME", temp.get("OPPOSING_UNIT")) ;
					map.put("FICB_ITEM", "待分解来款") ;
					map.put("REAL_PRICE", temp.get("INCOME_MONEY")) ;
					finaComeAllMoney.add(map) ;
				}
			}
			//List recpAndIncomeMoney=(List)listMap.get("recpAndIncomeMoney");
			String bank=(String)listMap.get("bank");
			//System.out.println("bank======="+bank+"----finaComeAllMoney="+finaComeAllMoney.size());
			
			if(bank==null || bank.equals("")) {
				excelHead="日来款租金分解";
			}
			else {
				excelHead=bank+"日来款租金分解";
			}
			cell=8;
			row=4;
			format1=this.tableFont();
			List cellList=this.cellSome();
			List titleList=this.titleListMethod(finaComeByDay,finaComeByOldDay,bank);
			List contentList=this.contextListMethod(finaComeAllRent,finaComeAllMoney);
			
			Map fatherMap=new HashMap();
			
			fatherMap.put("excelHead", excelHead);
			fatherMap.put("cell", cell);
			fatherMap.put("row", row);
			fatherMap.put("format1", format1);
			fatherMap.put("cellList", cellList);
			fatherMap.put("titleList", titleList);
			fatherMap.put("contentList", contentList);
			
			fatherlist.add(fatherMap);
		}
		
		
		
		
		this.createexl(); 
		baos = this.export(fatherlist);
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(excelName.getBytes("GBK"), "ISO-8859-1"));
			ServletOutputStream out1 = context.response.getOutputStream();
			this.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {
			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public ByteArrayOutputStream export(List fatherList) 
	{
		WritableSheet sheet = null;
		try {
			/* 解决中文乱码 */
			workbookSettings.setEncoding("ISO-8859-1");
			
			for(int h=0;h<fatherList.size();h++)
			{
				Map fatherMap=(Map)fatherList.get(h);
				
				String excelHead=(String)fatherMap.get("excelHead");
				int cells=Integer.valueOf(fatherMap.get("cell").toString());
				int rows=Integer.valueOf(fatherMap.get("row").toString());
				WritableCellFormat format=(WritableCellFormat)fatherMap.get("format1");
				WritableCellFormat format1=this.tableFont1();//标题通用样式
				List cellList=(List)fatherMap.get("cellList");
				List titleList=(List)fatherMap.get("titleList");
				List contentList=(List)fatherMap.get("contentList");
				//System.out.println("===================22222222222222-----"+(h+1)+"aaaa="+excelHead+"---------contentList="+contentList.size());
				sheet = wb.createSheet(excelHead, h+1);
				
				
				//设置列宽
				for(int i=0;i<cells;i++)
				{
					int cellWidth=Integer.parseInt(cellList.get(i).toString());
					sheet.setColumnView(i, cellWidth);
				}
				
				//创建列
				Label cell = null;//文字和符号
				Number number=null;//钱
				//标题列
				for(int i=0;i<titleList.size();i++)
				{
					ExcelEntity excelEntity=(ExcelEntity)titleList.get(i);
					cell = new Label(excelEntity.getCol(), excelEntity.getRow(), excelEntity.getContent(), excelEntity.getFormat());
					if(excelEntity.getColSpan()!=1 || excelEntity.getRowSpan()!=1)
					{
						sheet.mergeCells(excelEntity.getCol(), excelEntity.getRow(),excelEntity.getCol()+excelEntity.getColSpan(), excelEntity.getRow()+excelEntity.getRowSpan());
					}
					sheet.addCell(cell);
				}
				
				//列表
				for(int i=0;i<contentList.size();i++)
				{
					
					List contentListSun=(List)contentList.get(i);
					for(int j=0;j<contentListSun.size();j++)
					{
						NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
						//分组加，
						nfFSNum.setGroupingUsed(true);
						//设定 小数位数
						nfFSNum.setMaximumFractionDigits(2);
						
						if(contentListSun.get(j) != null && !contentListSun.get(j).equals("null")){
							//System.out.println("--------------j="+j+"==============]]]"+contentListSun.get(j).toString());
							if(j<3){
								if(j==0){
									String date1=contentListSun.get(j).toString();
									String date2=date1.substring(0, 10);
									cell = new Label(j,rows,date2,format);
								} else {
									cell = new Label(j,rows,contentListSun.get(j).toString(),format);
								}
								
							} else {
								if(Double.parseDouble(contentListSun.get(j) + "")==0) {
									cell = new Label(j,rows,"0.00",format1);
								} else {
									cell = new Label(j,rows,nfFSNum.format(Double.parseDouble(contentListSun.get(j).toString())),format1);
								}
							}
						} else {
							cell = new Label(j,rows,"",format);
						}
						sheet.addCell(cell);
					}
					rows++;
				}
			}
			
	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return baos;
	}
	
	
	public void close() {
		try {
			wb.write();
			wb.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	//设置该Excel有和每一列的宽度,cellList的长度和cell（多少列）相同
	public List cellSome()
	{
		List cellList=new ArrayList();
		cellList.add(20);
		cellList.add(25);
		cellList.add(25);
		cellList.add(15);
		cellList.add(15);
		cellList.add(15);
		cellList.add(15);
		cellList.add(15);
		return cellList;
	}
	
	//设置标题
	public List titleListMethod(Map finaComeByDay,Map finaComeByOldDay,String bank)
	{
		if(finaComeByDay == null){
			finaComeByDay = new HashMap() ;
		}
		if(finaComeByOldDay == null){
			finaComeByOldDay = new HashMap() ;
		}
		
		List titleList=new ArrayList();
		
		NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
		//分组加，
		nfFSNum.setGroupingUsed(true);
		//设定 小数位数
		nfFSNum.setMaximumFractionDigits(2);
		
		//format(样式)
		//colspan(int)跨几列
		//rowspan(int)跨几行
		//content(String)内容
		//row(int)显示在第几行（有的标题有两行，说明该信息显示在第几行,从0开始算起）
		//col(int)显示在第几列（跨行时用到,从0开始算起）
		
		WritableCellFormat format=this.tableFont();//标题通用样式
		WritableCellFormat format1=this.tableFont1();//标题通用样式
		//第一行
		//标题第一列（跨两行,一列,居中显示在第一行）
		//公司名称
		ExcelEntity col1=new ExcelEntity();
		col1.setFormat(format);
		col1.setRowSpan(0);
		col1.setColSpan(2);
		col1.setRow(0);
		col1.setCol(0);
		if(bank==null || bank.equals(""))
		{
			col1.setContent("当日来款额");
		}
		else
		{
			col1.setContent(bank+"当日来款额");
		}
		
		titleList.add(col1);
		
		//标题第二列（跨一行,五列,居中显示在第一行）
		//报告（未提交）
		ExcelEntity col2=new ExcelEntity();
		col2.setFormat(format1);
		col2.setRowSpan(0);
		col2.setColSpan(4);
		col2.setRow(0);
		col2.setCol(3);
		Object INCOME_MONEYDAY=finaComeByDay.get("INCOME_MONEYDAY");
		if(INCOME_MONEYDAY!=null){
			if(Double.parseDouble(finaComeByDay.get("INCOME_MONEYDAY").toString())==0){
				col2.setContent("0.00");
			} else {
				col2.setContent(nfFSNum.format(Double.parseDouble(finaComeByDay.get("INCOME_MONEYDAY").toString())).toString());
			}
		} else {
			col2.setContent("0.00");
		}
		titleList.add(col2);
		
		//报告审批
		ExcelEntity col3=new ExcelEntity();
		col3.setFormat(format);
		col3.setRowSpan(0);
		col3.setColSpan(2);
		col3.setRow(1);
		col3.setCol(0);
		col3.setContent("前日待分解额");
		titleList.add(col3);
		
		//评审
		ExcelEntity col4=new ExcelEntity();
		col4.setFormat(format1);
		col4.setRowSpan(0);
		col4.setColSpan(4);
		col4.setRow(1);
		col4.setCol(3);
		Object INCOME_MONEYOLDDAY=finaComeByOldDay.get("INCOME_MONEYOLDDAY");
		if(INCOME_MONEYOLDDAY!=null)
		{
			if(Double.parseDouble(INCOME_MONEYOLDDAY.toString())==0)
			{
				col4.setContent("0.00");
			}
			else
			{
				col4.setContent(nfFSNum.format(Double.parseDouble(INCOME_MONEYOLDDAY.toString())).toString());
			}
		}
		else
		{
			col4.setContent("0.00");
		}
		titleList.add(col4);
		
		//完成合计
		ExcelEntity col5=new ExcelEntity();
		col5.setFormat(format);
		col5.setRowSpan(1);
		col5.setColSpan(0);
		col5.setRow(2);
		col5.setCol(0);
		col5.setContent("来款日期");
		titleList.add(col5);
		
		//总计
		ExcelEntity col6=new ExcelEntity();
		col6.setFormat(format);
		col6.setRowSpan(1);
		col6.setColSpan(0);
		col6.setRow(2);
		col6.setCol(1);
		col6.setContent("承租人");
		titleList.add(col6);
		
		//完成率
		ExcelEntity col7=new ExcelEntity();
		col7.setFormat(format);
		col7.setRowSpan(1);
		col7.setColSpan(0);
		col7.setRow(2);
		col7.setCol(2);
		col7.setContent("合同号");
		titleList.add(col7);
		
		//最长耗时
		ExcelEntity col8=new ExcelEntity();
		col8.setFormat(format);
		col8.setRowSpan(0);
		col8.setColSpan(4);
		col8.setRow(2);
		col8.setCol(3);
		col8.setContent("分解金额");
		titleList.add(col8);
		
		//最短耗时
		ExcelEntity col9=new ExcelEntity();
		col9.setFormat(format);
		col9.setRowSpan(0);
		col9.setColSpan(0);
		col9.setRow(3);
		col9.setCol(3);
		col9.setContent("保证金");
		titleList.add(col9);
		
		//平均耗时
		ExcelEntity col10=new ExcelEntity();
		col10.setFormat(format);
		col10.setRowSpan(0);
		col10.setColSpan(0);
		col10.setRow(3);
		col10.setCol(4);
		col10.setContent("租金");
		titleList.add(col10);
		
		//1天以内
		ExcelEntity col11=new ExcelEntity();
		col11.setFormat(format);
		col11.setRowSpan(0);
		col11.setColSpan(0);
		col11.setRow(3);
		col11.setCol(5);
		col11.setContent("税金");
		titleList.add(col11);
		
		//1－2天
		ExcelEntity col12=new ExcelEntity();
		col12.setFormat(format);
		col12.setRowSpan(0);
		col12.setColSpan(0);
		col12.setRow(3);
		col12.setCol(6);
		col12.setContent("待分解");
		titleList.add(col12);
		
		//2－3天
		ExcelEntity col13=new ExcelEntity();
		col13.setFormat(format);
		col13.setRowSpan(0);
		col13.setColSpan(0);
		col13.setRow(3);
		col13.setCol(7);
		col13.setContent("小计");
		titleList.add(col13);
		
		
		return titleList;
	}
	
	
	public List contextListMethod(List finaComeAllMoney,List contextList)
	{
		//System.out.println("------------------recpAndIncomeMoney="+recpAndIncomeMoney.size());
		if(finaComeAllMoney == null){
			finaComeAllMoney = new ArrayList() ;
		}
		if(contextList == null){
			contextList = new ArrayList() ;
		}
		List contextListFather=new ArrayList();
		
		for(int j=0;j<finaComeAllMoney.size();j++)
		{
			Map items=(Map)finaComeAllMoney.get(j);
			String OPPOSING_DATE=null;
			String CUST_NAME=null;
			String LEASE_CODE=null;
			double zhi1=0d;
			double zhi2=0d;
			double zhi3=0d;
			double zhi4=0d;
			double zhi5=0d;
			List contextListSun=new ArrayList();
			
			
			for(int h=0;h<contextList.size();h++)
			{
				
				Map item=(Map)contextList.get(h);
				//System.out.println("yunxinglema?RECP_ID="+item.get("RECP_ID"));
				if(items.get("RECP_ID") != null && item.get("RECP_ID") != null && items.get("RECP_ID").toString().equals(item.get("RECP_ID").toString()) && items.get("FIIN_ID").toString().equals(item.get("FIIN_ID").toString()))
				{	
					OPPOSING_DATE=item.get("OPPOSING_DATE") + "";
					CUST_NAME=item.get("CUST_NAME") + "";
					LEASE_CODE=item.get("LEASE_CODE") + "";
					if( item.get("FICB_ITEM") != null){
						if(item.get("FICB_ITEM").toString().equals("保证金")){
							if(item.get("REAL_PRICE")!=null){
								zhi2=Double.parseDouble(item.get("REAL_PRICE").toString());
								zhi1+=Double.parseDouble(item.get("REAL_PRICE").toString());
							}
						}
						if(item.get("FICB_ITEM").toString().equals("税金")){
							if(item.get("REAL_PRICE")!=null){
								zhi3=Double.parseDouble(item.get("REAL_PRICE").toString());
								zhi1+=Double.parseDouble(item.get("REAL_PRICE").toString());
							}
						}
						if(item.get("FICB_ITEM").toString().equals("待分解来款")){
							if(item.get("REAL_PRICE")!=null){
								zhi4=Double.parseDouble(item.get("REAL_PRICE").toString());
								zhi1+=Double.parseDouble(item.get("REAL_PRICE").toString());
							}
//							for(int k=0;k<recpAndIncomeMoney.size();k++)
//							{
//								Map itemrecp=(HashMap)recpAndIncomeMoney.get(k);
//								if(items.get("RECP_ID").toString().equals(itemrecp.get("RECP").toString()))
//								{
//									zhi4=Double.parseDouble(itemrecp.get("RECPMONEY").toString());
//									zhi1+=Double.parseDouble(itemrecp.get("RECPMONEY").toString());
//								}
//							}
						}
						if(item.get("FICB_ITEM").toString().equals("租金")) {
							if(item.get("REAL_PRICE")!=null) {
								zhi5=Double.parseDouble(item.get("REAL_PRICE").toString());
								zhi1+=Double.parseDouble(item.get("REAL_PRICE").toString());
							}
						}
					}
				}
			}
			contextListSun.add(OPPOSING_DATE);
			contextListSun.add(CUST_NAME);
			contextListSun.add(LEASE_CODE);
			contextListSun.add(zhi2);
			contextListSun.add(zhi5);
			contextListSun.add(zhi3);
			contextListSun.add(zhi4);
			contextListSun.add(zhi1);
			//System.out.println("OPPOSING_DATE="+OPPOSING_DATE+",CUST_NAME="+CUST_NAME+",zhi1="+zhi1+",zhi2="+zhi2+"zhi5="+zhi5+"zhi3="+zhi3+",zhi4="+zhi4+",LEASE_CODE"+LEASE_CODE);
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
	
	
	
}
