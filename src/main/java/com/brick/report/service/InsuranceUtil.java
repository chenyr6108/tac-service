package com.brick.report.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.util.DataUtil;
import com.brick.util.DateUtil;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.DateFormat;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.util.LeaseUtil;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.log.service.LogPrint;


public class InsuranceUtil {
	Log logger = LogFactory.getLog(InsuranceUtil.class);
	WritableWorkbook wb = null;
	WorkbookSettings workbookSettings = new WorkbookSettings();
	ByteArrayOutputStream baos = new ByteArrayOutputStream();

	public void createexl() {
		try {
			wb = Workbook.createWorkbook(baos, workbookSettings);
		} catch (IOException e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		
	}

	public void close() {
		try {
			wb.write();
			wb.close();
		} catch (WriteException e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} catch (IOException e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		
	}

	public ByteArrayOutputStream exportExcel(List<Map> insuranceList) {
		WritableSheet sheet = null;
		
		try {
			workbookSettings.setEncoding("ISO-8859-1");
			sheet = wb.createSheet("保险费明细表", 1);
			
			WritableFont font2 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
			WritableCellFormat format2_BOTTOM = new WritableCellFormat(font2);
			format2_BOTTOM.setAlignment(Alignment.CENTRE);
			format2_BOTTOM.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2_BOTTOM.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
			format2_BOTTOM.setWrap(true);
			
			WritableCellFormat format2_Top = new WritableCellFormat(font2);
			format2_Top.setAlignment(Alignment.CENTRE);
			format2_Top.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2_Top.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
			format2_Top.setWrap(true);
			
			WritableCellFormat format2_LEFT = new WritableCellFormat(font2);
			format2_LEFT.setAlignment(Alignment.CENTRE);
			format2_LEFT.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2_LEFT.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
			format2_LEFT.setWrap(true);
			
			WritableCellFormat format2_RIGHT = new WritableCellFormat(font2);
			format2_RIGHT.setAlignment(Alignment.CENTRE);
			format2_RIGHT.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2_RIGHT.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
			format2_RIGHT.setWrap(true);
			
			// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
			WritableFont font3 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
			WritableCellFormat format3 = new WritableCellFormat(font3);
			format3.setAlignment(Alignment.LEFT);
			format3.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format3.setWrap(true);
			// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
					 
			WritableCellFormat format5 = new WritableCellFormat(font3);
			format5.setAlignment(Alignment.CENTRE);
			format5.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format5.setWrap(true);
			WritableFont font6 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
			font6.setColour(Colour.BLUE);
			WritableCellFormat format6 = new WritableCellFormat(font6);
			format6.setAlignment(Alignment.LEFT); 
			format6.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format6.setWrap(true);
			
			
			WritableFont font8 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
			font8.setColour(Colour.BLUE);
			WritableCellFormat format8 = new WritableCellFormat(font8);
			format8.setAlignment(Alignment.LEFT); 
			format8.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format8.setWrap(true);
			
			
			WritableFont font9 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
			WritableCellFormat format9 = new WritableCellFormat(font9);
			format9.setAlignment(Alignment.LEFT);
			format9.setBackground(jxl.format.Colour.GRAY_25);
			format9.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format9.setWrap(true);
			
			
			WritableFont font4 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
			WritableCellFormat format4 = new WritableCellFormat(font4);
			format4.setAlignment(Alignment.CENTRE);
			format4.setBackground(jxl.format.Colour.GRAY_25);
			format4.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format4.setWrap(true);
			
						
			NumberFormat cnum=new NumberFormat("#,##0.0");
			NumberFormat pnum=new NumberFormat("##0.000%");
			NumberFormat fnum=new NumberFormat("##0.00");
			NumberFormat nnum=new NumberFormat("##"); 
			DateFormat   dateFormat=new DateFormat("yyyy-mm-dd");
			DateFormat   dateFormat2=new DateFormat("MMM-yy");
			WritableCellFormat dateCellFormat_center2=new WritableCellFormat(dateFormat2);
			dateCellFormat_center2.setAlignment(Alignment.CENTRE);
			WritableCellFormat ccellFormat_right=new WritableCellFormat(cnum);
			WritableCellFormat pcellFormat_left=new WritableCellFormat(pnum);
			WritableCellFormat pcellFormat_right=new WritableCellFormat(pnum);
			WritableCellFormat pcellFormat_center=new WritableCellFormat(pnum);
			WritableCellFormat pcellFormat_center2=new WritableCellFormat(pnum);
			WritableCellFormat fcellFormat_left=new WritableCellFormat(fnum);
			WritableCellFormat ccellFormat_left=new WritableCellFormat(cnum);
			WritableCellFormat dateCellFormat_left=new WritableCellFormat(dateFormat);
			WritableCellFormat dateCellFormat_center=new WritableCellFormat(dateFormat);
			ccellFormat_left.setAlignment(Alignment.LEFT);
			pcellFormat_left.setAlignment(Alignment.LEFT);
			pcellFormat_right.setAlignment(Alignment.RIGHT);
			pcellFormat_center.setAlignment(Alignment.CENTRE);
			pcellFormat_center2.setAlignment(Alignment.CENTRE);
			pcellFormat_center2.setVerticalAlignment(VerticalAlignment.CENTRE);		
			fcellFormat_left.setAlignment(Alignment.LEFT);
			dateCellFormat_left.setAlignment(Alignment.LEFT);
			dateCellFormat_center.setAlignment(Alignment.CENTRE);
			WritableCellFormat ncellFormat_center=new WritableCellFormat(nnum);
			ncellFormat_center.setAlignment(Alignment.CENTRE);
			WritableCellFormat ncellFormat_left=new WritableCellFormat(nnum);
			ncellFormat_left.setAlignment(Alignment.LEFT);
			Number number=null;
			//1.0E8 －－100000000
			DecimalFormat   nf2=new   DecimalFormat("###################");	
			
			sheet.setColumnView(0, 4);
			sheet.setColumnView(1, 15);
			sheet.setColumnView(2, 8);
			sheet.setColumnView(3,40);
			sheet.setColumnView(4,40);
			sheet.setColumnView(5, 20);
			sheet.setColumnView(6, 15);
			sheet.setColumnView(7, 40);
			sheet.setColumnView(8, 15);
			sheet.setColumnView(9, 15);
			sheet.setColumnView(10, 8);
			sheet.setColumnView(11, 15);
			sheet.setColumnView(12, 15);
			sheet.setColumnView(13, 15);
			sheet.setColumnView(14,15);
			sheet.setColumnView(15,4);
			
			int n=0;
			Label cell = null;
			
			
			cell=new Label(0,0,"");  
			sheet.addCell(cell);
			cell=new Label(1,0,"",format2_BOTTOM);  
			sheet.mergeCells(1, 0, 14, 0);
			sheet.addCell(cell);
			n=n+1;
			sheet.setRowView(0,300,false); 
			
			cell=new Label(0,1,""); 
			sheet.addCell(cell);
			cell=new Label(1,1,"保险费明细表",format4); 
			sheet.mergeCells(1, 1, 14, 1); 
			sheet.addCell(cell);
			n=n+1;
			sheet.setRowView(1,320,false); 
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(1,n,"支付期",format5); 
			sheet.mergeCells(1, n, 1, n+1); 
			sheet.addCell(cell); 
			cell=new Label(2,n,"序号",format5); 
			sheet.mergeCells(2, n, 2, n+1); 
			sheet.addCell(cell); 
			cell=new Label(3,n,"客户名称",format5); 
			sheet.mergeCells(3, n,3, n+1); 
			sheet.addCell(cell); 
			cell=new Label(4,n,"供应商",format5); 
			sheet.mergeCells(4, n,4, n+1); 
			sheet.addCell(cell); 
			cell=new Label(5,n,"合同号",format5); 
			sheet.mergeCells(5, n,5, n+1); 
			sheet.addCell(cell); 
			cell=new Label(6,n,"业务员",format5); 
			sheet.mergeCells(6, n,6, n+1); 
			sheet.addCell(cell); 
			cell=new Label(7,n,"办事处",format5); 
			sheet.mergeCells(7, n,7, n+1); 
			sheet.addCell(cell); 
			cell=new Label(8,n,"设备总价款",format5); 
			sheet.mergeCells(8, n,8, n+1); 
			sheet.addCell(cell); 
			cell=new Label(9,n,"起租日",format5); 
			sheet.mergeCells(9, n,9, n+1); 
			sheet.addCell(cell); 
			cell=new Label(10,n,"租赁期",format5); 
			sheet.mergeCells(10, n,10, n+1); 
			sheet.addCell(cell); 
			cell=new Label(11,n,"保险合同",format5); 
			sheet.mergeCells(11, n,12, n);
			sheet.addCell(cell); 
			cell=new Label(13,n,"调整差异",format5); 
			sheet.mergeCells(13, n,13, n+1); 
			sheet.addCell(cell); 
			cell=new Label(14,n,"当月实际",format5); 
			sheet.mergeCells(14, n,14, n+1); 
			sheet.addCell(cell); 
			
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(11,n,"金额",format5); 
			sheet.addCell(cell);
			cell=new Label(12,n,"当月均摊保费",format5); 
			sheet.addCell(cell);
			n=n+1;
			
			
			double LEASE_TOPRIC_sum=0;
			double INSURANCE_sum=0;
			double MONTHINSURANCE_sum=0;
			int i = 0;
			for (Object object2 : insuranceList) {
				Map insurance=(Map)object2;
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				
				if(insurance.get("FIRST_PAYDATE")!=null){
					SimpleDateFormat df2=new SimpleDateFormat("yyyy-MM-dd");
					Date start_date2=df2.parse(insurance.get("FIRST_PAYDATE")+"");
					DateTime dateLabel2=new DateTime(1,n, start_date2,dateCellFormat_center);
					sheet.addCell(dateLabel2); 
				}else{
					cell=new Label(1,n,""); 
					sheet.addCell(cell);
				}
								
				i = i + 1;						
				number=new Number(2,n,i,ncellFormat_center);
				sheet.addCell(number);
				
				cell=new Label(3,n,DataUtil.StringUtil(insurance.get("CUST_NAME")+""),format5);
				sheet.addCell(cell);
				cell=new Label(4,n,DataUtil.StringUtil(insurance.get("SUPL_NAME")+""),format5);
				sheet.addCell(cell);
				cell=new Label(5,n,DataUtil.StringUtil(insurance.get("RECP_CODE")+""),format5);
				sheet.addCell(cell);
				cell=new Label(6,n,DataUtil.StringUtil(insurance.get("NAME")+""),format5);
				sheet.addCell(cell);
				cell=new Label(7,n,DataUtil.StringUtil(insurance.get("DECP_NAME_CN")),format5);  
				sheet.addCell(cell);
				number=new Number(8,n,Double.parseDouble(insurance.get("LEASE_TOPRIC")+""),ccellFormat_right);
				sheet.addCell(number);
				
				if(insurance.get("FIRST_PAYDATE")!=null){
					SimpleDateFormat df2=new SimpleDateFormat("yyyy-MM-dd");
					Date start_date2=df2.parse(insurance.get("FIRST_PAYDATE")+"");
					DateTime dateLabel2=new DateTime(9,n, start_date2,dateCellFormat_center);
					sheet.addCell(dateLabel2); 
				}else{
					cell=new Label(9,n,""); 
					sheet.addCell(cell);
				}
				cell=new Label(10,n,DataUtil.StringUtil(insurance.get("LEASE_PERIOD")),format5);  
				sheet.addCell(cell);
				
				number=new Number(11,n,Double.parseDouble(insurance.get("INSURANCE")+""),ccellFormat_right);
				sheet.addCell(number);
				number=new Number(12,n,Double.parseDouble(insurance.get("MONTHINSURANCE")+""),ccellFormat_right);
				sheet.addCell(number);
				number=new Number(13,n,0,ccellFormat_right);
				sheet.addCell(number);
				number=new Number(14,n,Double.parseDouble(insurance.get("MONTHINSURANCE")+""),ccellFormat_right);
				sheet.addCell(number);
				
				
				
				sheet.setRowView(n,300,false);  
				n=n+1;
				
				LEASE_TOPRIC_sum=LEASE_TOPRIC_sum+Double.parseDouble(insurance.get("LEASE_TOPRIC")+"");
				INSURANCE_sum=INSURANCE_sum+Double.parseDouble(insurance.get("INSURANCE")+"");
				MONTHINSURANCE_sum=MONTHINSURANCE_sum+Double.parseDouble(insurance.get("MONTHINSURANCE")+"");

			}
			 
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(1,n,"小计",format5);
			sheet.mergeCells(1, n, 7, n);
			sheet.addCell(cell);
			number=new Number(8,n,LEASE_TOPRIC_sum,ccellFormat_right);
			sheet.addCell(number);

			cell=new Label(9,n,""); 
			sheet.addCell(cell);
			cell=new Label(10,n,""); 
			sheet.addCell(cell);
			
			number=new Number(11,n,INSURANCE_sum,ccellFormat_right);
			sheet.addCell(number);
			number=new Number(12,n,MONTHINSURANCE_sum,ccellFormat_right);
			sheet.addCell(number);
			number=new Number(13,n,0,ccellFormat_right);
			sheet.addCell(number); 
			number=new Number(14,n,MONTHINSURANCE_sum,ccellFormat_right);
			sheet.addCell(number);

			sheet.setRowView(n,300,false);  
			n=n+1;
			
			cell=new Label(0,1,"",format2_RIGHT);
			sheet.mergeCells(0, 1, 0, n-1);
			sheet.addCell(cell);
			cell=new Label(15,1,"",format2_LEFT);
			sheet.mergeCells(15, 1, 15, n-1);
			sheet.addCell(cell);
			cell=new Label(1,n,"",format2_Top);
			sheet.mergeCells(1, n, 14, n); 
			sheet.addCell(cell);
					
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		return baos;
	}

	public ByteArrayOutputStream exportStampExcel(Map content) {
		WritableSheet sheet = null;
		List stampList = (List) content.get("stampList") ;
		try {
			workbookSettings.setEncoding("ISO-8859-1");
			sheet = wb.createSheet("印花税明细表", 1);
			
			WritableFont font2 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
			WritableCellFormat format2_BOTTOM = new WritableCellFormat(font2);
			format2_BOTTOM.setAlignment(Alignment.CENTRE);
			format2_BOTTOM.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2_BOTTOM.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
			format2_BOTTOM.setWrap(true);
			
			WritableCellFormat format2_Top = new WritableCellFormat(font2);
			format2_Top.setAlignment(Alignment.CENTRE);
			format2_Top.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2_Top.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
			format2_Top.setWrap(true);
			
			WritableCellFormat format2_LEFT = new WritableCellFormat(font2);
			format2_LEFT.setAlignment(Alignment.CENTRE);
			format2_LEFT.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2_LEFT.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
			format2_LEFT.setWrap(true);
			
			WritableCellFormat format2_RIGHT = new WritableCellFormat(font2);
			format2_RIGHT.setAlignment(Alignment.CENTRE);
			format2_RIGHT.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2_RIGHT.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
			format2_RIGHT.setWrap(true);
			
			// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
			WritableFont font3 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
			WritableCellFormat format3 = new WritableCellFormat(font3);
			format3.setAlignment(Alignment.LEFT);
			format3.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format3.setWrap(true);
			// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
					 
			WritableCellFormat format5 = new WritableCellFormat(font3);
			format5.setAlignment(Alignment.CENTRE);
			format5.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format5.setWrap(true);
			WritableFont font6 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
			font6.setColour(Colour.BLUE);
			WritableCellFormat format6 = new WritableCellFormat(font6);
			format6.setAlignment(Alignment.LEFT); 
			format6.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format6.setWrap(true);
			
			
			WritableFont font8 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
			font8.setColour(Colour.BLUE);
			WritableCellFormat format8 = new WritableCellFormat(font8);
			format8.setAlignment(Alignment.LEFT); 
			format8.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format8.setWrap(true);
			
			
			WritableFont font9 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
			WritableCellFormat format9 = new WritableCellFormat(font9);
			format9.setAlignment(Alignment.LEFT);
			format9.setBackground(jxl.format.Colour.GRAY_25);
			format9.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format9.setWrap(true);
			
			
			WritableFont font4 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
			WritableCellFormat format4 = new WritableCellFormat(font4);
			format4.setAlignment(Alignment.CENTRE);
			format4.setBackground(jxl.format.Colour.GRAY_25);
			format4.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format4.setWrap(true);
			
						
			NumberFormat cnum=new NumberFormat("#,##0.00");
			NumberFormat pnum=new NumberFormat("##0.000%");
			NumberFormat fnum=new NumberFormat("##0.00");
			NumberFormat nnum=new NumberFormat("##"); 
			DateFormat   dateFormat=new DateFormat("yyyy-mm-dd");
			DateFormat   dateFormat2=new DateFormat("MMM-yy");
			WritableCellFormat dateCellFormat_center2=new WritableCellFormat(dateFormat2);
			dateCellFormat_center2.setAlignment(Alignment.CENTRE);
			WritableCellFormat ccellFormat_right=new WritableCellFormat(cnum);
			WritableCellFormat pcellFormat_left=new WritableCellFormat(pnum);
			WritableCellFormat pcellFormat_right=new WritableCellFormat(pnum);
			WritableCellFormat pcellFormat_center=new WritableCellFormat(pnum);
			WritableCellFormat pcellFormat_center2=new WritableCellFormat(pnum);
			WritableCellFormat fcellFormat_left=new WritableCellFormat(fnum);
			WritableCellFormat ccellFormat_left=new WritableCellFormat(cnum);
			WritableCellFormat dateCellFormat_left=new WritableCellFormat(dateFormat);
			WritableCellFormat dateCellFormat_center=new WritableCellFormat(dateFormat);
			ccellFormat_left.setAlignment(Alignment.LEFT);
			pcellFormat_left.setAlignment(Alignment.LEFT);
			pcellFormat_right.setAlignment(Alignment.RIGHT);
			pcellFormat_center.setAlignment(Alignment.CENTRE);
			pcellFormat_center2.setAlignment(Alignment.CENTRE);
			pcellFormat_center2.setVerticalAlignment(VerticalAlignment.CENTRE);		
			fcellFormat_left.setAlignment(Alignment.LEFT);
			dateCellFormat_left.setAlignment(Alignment.LEFT);
			dateCellFormat_center.setAlignment(Alignment.CENTRE);
			WritableCellFormat ncellFormat_center=new WritableCellFormat(nnum);
			ncellFormat_center.setAlignment(Alignment.CENTRE);
			WritableCellFormat ncellFormat_left=new WritableCellFormat(nnum);
			ncellFormat_left.setAlignment(Alignment.LEFT);
			Number number=null;
			//1.0E8 －－100000000
			DecimalFormat   nf2=new   DecimalFormat("###################");	
			
			sheet.setColumnView(0, 4);
			sheet.setColumnView(1, 8);
			sheet.setColumnView(2, 40);
			sheet.setColumnView(3, 40);
			sheet.setColumnView(4, 20);
			sheet.setColumnView(5, 10);
			sheet.setColumnView(6, 40);
			sheet.setColumnView(7, 15);
			sheet.setColumnView(8, 15);
			sheet.setColumnView(9, 15);
			sheet.setColumnView(10, 15);
			sheet.setColumnView(11, 15);
			sheet.setColumnView(12, 15);
			sheet.setColumnView(13, 15);
//			sheet.setColumnView(14,15);
//			sheet.setColumnView(15,15);
//			sheet.setColumnView(16,20);
//			sheet.setColumnView(17,4);
			
			int n=0;
			Label cell = null;
			
			
			cell=new Label(0,0,"");  
			sheet.addCell(cell);
			cell=new Label(1,0,"",format2_BOTTOM);  
			sheet.mergeCells(1, 0, 16, 0);
			sheet.addCell(cell);
			n=n+1;
			sheet.setRowView(0,300,false); 
			
			cell=new Label(0,1,""); 
			sheet.addCell(cell);
			cell=new Label(1,1,"印花税明细表",format4); 
			sheet.mergeCells(1, 1, 16, 1); 
			sheet.addCell(cell);
			n=n+1;
			sheet.setRowView(1,320,false); 
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(1,n,"序号",format5); 
			sheet.mergeCells(1, n, 1, n+1); 
			sheet.addCell(cell); 
			cell=new Label(2,n,"客户名称",format5); 
			sheet.mergeCells(2, n, 2, n+1); 
			sheet.addCell(cell); 
			cell=new Label(3,n,"供应商",format5); 
			sheet.mergeCells(3, n, 3, n+1); 
			sheet.addCell(cell); 
			cell=new Label(4,n,"合同号",format5); 
			sheet.mergeCells(4, n, 4, n+1); 
			sheet.addCell(cell); 
			cell=new Label(5,n,"业务员",format5); 
			sheet.mergeCells(5, n, 5, n+1); 
			sheet.addCell(cell); 
			cell=new Label(6,n,"办事处",format5); 
			sheet.mergeCells(6, n, 6, n+1); 
			sheet.addCell(cell); 
			cell=new Label(7,n,"融资租赁合同",format5); 
			sheet.mergeCells(7, n,9, n); 
			sheet.addCell(cell); 
			cell=new Label(10,n,"委托购买合同",format5); 
			sheet.mergeCells(10, n,12, n); 
			sheet.addCell(cell); 
			//Modify by Michael 2012 3-5 将印花税拆分成合同印花税和保险印花税
//			cell=new Label(13,n,"保险合同("+content.get("STAMP_TAX_INSUREPRIC")+"‰)",format5); 
//			sheet.mergeCells(13, n,15, n); 
//			sheet.addCell(cell); 
			cell=new Label(13,n,"小计",format5); 
			sheet.mergeCells(13, n,13, n+1); 
			sheet.addCell(cell); 
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(7,n,"计税金额",format5); 
			sheet.addCell(cell);
			cell=new Label(8,n,"税额（计算）",format5); 
			sheet.addCell(cell);
			cell=new Label(9,n,"税额（税务）",format5); 
			sheet.addCell(cell);
			cell=new Label(10,n,"计税金额",format5); 
			sheet.addCell(cell);
			cell=new Label(11,n,"税额（计算）",format5); 
			sheet.addCell(cell);
			cell=new Label(12,n,"税额（税务）",format5); 
			sheet.addCell(cell);
			//Modify by Michael 2012 3-5 将印花税拆分成合同印花税和保险印花税
//			cell=new Label(13,n,"计税金额",format5); 
//			sheet.addCell(cell);
//			cell=new Label(14,n,"税额（计算）",format5); 
//			sheet.addCell(cell);
//			cell=new Label(15,n,"税额（税务）",format5); 
//			sheet.addCell(cell);
			n=n+1;
			
			
			double ALLMONTHPRICE_sum=0;
			double RONGZISHUIE_sum=0;
			double RZSHUIWU_sum=0;
			double ALLOWNPRICE_sum=0;
			double WEITUOSHUIE_sum=0;
			double WTSHUIWU_sum=0;
			double BAOXIANJISHUIJINE_sum=0;
			double BAOXIANSHUIE_sum=0;
			double BXSHUIWU_sum=0;
			double XIAOJI_sum=0;

			int i = 0;
			for (Object object2 : stampList) {
				Map stamp=(Map)object2;
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
												
				i = i + 1;						
				number=new Number(1,n,i,ncellFormat_center);
				sheet.addCell(number);
				
				cell=new Label(2,n,DataUtil.StringUtil(stamp.get("CUST_NAME")),format5);  
				sheet.addCell(cell);
				cell=new Label(3,n,DataUtil.StringUtil(stamp.get("SUPL_NAME")),format5);  
				sheet.addCell(cell);
				cell=new Label(4,n,DataUtil.StringUtil(stamp.get("LEASE_CODE")),format5);  
				sheet.addCell(cell);
				cell=new Label(5,n,DataUtil.StringUtil(stamp.get("NAME")),format5);  
				sheet.addCell(cell);
				cell=new Label(6,n,DataUtil.StringUtil(stamp.get("DECP_NAME_CN")),format5);  
				sheet.addCell(cell);
						
				if(stamp.get("ALLMONTHPRICE")!=null){
					number=new Number(7,n,Double.parseDouble(stamp.get("ALLMONTHPRICE")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(7,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
				if(stamp.get("RONGZISHUIE")!=null){
					number=new Number(8,n,Double.parseDouble(stamp.get("RONGZISHUIE")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(8,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
				if(stamp.get("RZSHUIWU")!=null){
					number=new Number(9,n,Double.parseDouble(stamp.get("RZSHUIWU")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(9,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
				if(stamp.get("ALLOWNPRICE")!=null){
					number=new Number(10,n,Double.parseDouble(stamp.get("ALLOWNPRICE")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(10,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
				if(stamp.get("WEITUOSHUIE")!=null){
					number=new Number(11,n,Double.parseDouble(stamp.get("WEITUOSHUIE")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(11,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
				if(stamp.get("WTSHUIWU")!=null){
					number=new Number(12,n,Double.parseDouble(stamp.get("WTSHUIWU")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(12,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
				//Modify by Michael 2012 3-5 将印花税拆分成合同印花税和保险印花税
//				if(stamp.get("BAOXIANJISHUIJINE")!=null){
//					number=new Number(13,n,Double.parseDouble(stamp.get("BAOXIANJISHUIJINE")+""),ccellFormat_right);
//					sheet.addCell(number);
//				}else{
//					number=new Number(13,n,0,ccellFormat_right);
//					sheet.addCell(number);
//				}
//				if(stamp.get("BAOXIANSHUIE")!=null){
//					number=new Number(14,n,Double.parseDouble(stamp.get("BAOXIANSHUIE")+""),ccellFormat_right);
//					sheet.addCell(number);
//				}else{
//					number=new Number(14,n,0,ccellFormat_right);
//					sheet.addCell(number);
//				}
//				if(stamp.get("BXSHUIWU")!=null){
//					number=new Number(15,n,Double.parseDouble(stamp.get("BXSHUIWU")+""),ccellFormat_right);
//					sheet.addCell(number);
//				}else{
//					number=new Number(15,n,0,ccellFormat_right);
//					sheet.addCell(number);
//				}
				if(stamp.get("XIAOJI")!=null){
					number=new Number(13,n,Double.parseDouble(stamp.get("XIAOJI")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(13,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
							
				sheet.setRowView(n,300,false);  
				n=n+1;
				

				ALLMONTHPRICE_sum=ALLMONTHPRICE_sum+DataUtil.doubleUtil(stamp.get("ALLMONTHPRICE"));
				RONGZISHUIE_sum=RONGZISHUIE_sum+DataUtil.doubleUtil(stamp.get("RONGZISHUIE"));
				RZSHUIWU_sum=RZSHUIWU_sum+DataUtil.doubleUtil(stamp.get("RZSHUIWU"));
				ALLOWNPRICE_sum=ALLOWNPRICE_sum+DataUtil.doubleUtil(stamp.get("ALLOWNPRICE"));
				WEITUOSHUIE_sum=WEITUOSHUIE_sum+DataUtil.doubleUtil(stamp.get("WEITUOSHUIE"));
				WTSHUIWU_sum=WTSHUIWU_sum+DataUtil.doubleUtil(stamp.get("WTSHUIWU"));
				//Modify by Michael 2012 3-5 将印花税拆分成合同印花税和保险印花税
//				BAOXIANJISHUIJINE_sum=BAOXIANJISHUIJINE_sum+DataUtil.doubleUtil(stamp.get("BAOXIANJISHUIJINE"));
//				BAOXIANSHUIE_sum=BAOXIANSHUIE_sum+DataUtil.doubleUtil(stamp.get("BAOXIANSHUIE"));
//				BXSHUIWU_sum=BXSHUIWU_sum+DataUtil.doubleUtil(stamp.get("BXSHUIWU"));
				XIAOJI_sum=XIAOJI_sum+DataUtil.doubleUtil(stamp.get("XIAOJI"));

			}
			 
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(1,n,"小计",format5);
			sheet.mergeCells(1, n, 2, n);
			sheet.addCell(cell);
			
			number=new Number(7,n,ALLMONTHPRICE_sum,ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,RONGZISHUIE_sum,ccellFormat_right);
			sheet.addCell(number);
			number=new Number(9,n,RZSHUIWU_sum,ccellFormat_right);
			sheet.addCell(number);
			number=new Number(10,n,ALLOWNPRICE_sum,ccellFormat_right);
			sheet.addCell(number);
			number=new Number(11,n,WEITUOSHUIE_sum,ccellFormat_right);
			sheet.addCell(number);
			number=new Number(12,n,WTSHUIWU_sum,ccellFormat_right);
			sheet.addCell(number);
			//Modify by Michael 2012 3-5 将印花税拆分成合同印花税和保险印花税
//			number=new Number(13,n,BAOXIANJISHUIJINE_sum,ccellFormat_right);
//			sheet.addCell(number);
//			number=new Number(14,n,BAOXIANSHUIE_sum,ccellFormat_right);
//			sheet.addCell(number);
//			number=new Number(15,n,BXSHUIWU_sum,ccellFormat_right);
//			sheet.addCell(number);
			number=new Number(13,n,XIAOJI_sum,ccellFormat_right);
			sheet.addCell(number);

			sheet.setRowView(n,300,false);  
			n=n+1;
			
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(1,n,"税务申报",format5);
			sheet.mergeCells(1, n, 2, n);
			sheet.addCell(cell);
			

			double dd1 = Double.parseDouble(Math.round(RONGZISHUIE_sum*100)+"")/100;
			double dd2 = Double.parseDouble(Math.round(WEITUOSHUIE_sum*100)+"")/100;
			double dd3 = Double.parseDouble(Math.round(BAOXIANSHUIE_sum*100)+"")/100;

			double d1 = Double.parseDouble(Math.round(dd1 *10)+"" )/10;
			double d2 = Double.parseDouble(Math.round(dd2 *10)+"" )/10;
			double d3 = Double.parseDouble(Math.round(dd3 *10)+"" )/10;


			
			number=new Number(7,n,ALLMONTHPRICE_sum,ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,RONGZISHUIE_sum,ccellFormat_right);
			sheet.addCell(number);
			number=new Number(9,n,d1,ccellFormat_right);
			sheet.addCell(number);
			number=new Number(10,n,ALLOWNPRICE_sum,ccellFormat_right);
			sheet.addCell(number);
			number=new Number(11,n,WEITUOSHUIE_sum,ccellFormat_right);
			sheet.addCell(number);
			number=new Number(12,n,d2,ccellFormat_right);
			sheet.addCell(number);
			//Modify by Michael 2012 3-5 将印花税拆分成合同印花税和保险印花税
//			number=new Number(13,n,BAOXIANJISHUIJINE_sum,ccellFormat_right);
//			sheet.addCell(number);
//			number=new Number(14,n,BAOXIANSHUIE_sum,ccellFormat_right);
//			sheet.addCell(number);
//			number=new Number(15,n,d3,ccellFormat_right);
//			sheet.addCell(number);
			number=new Number(13,n,d1+d2+d3,ccellFormat_right);
			sheet.addCell(number);

			sheet.setRowView(n,300,false);  
			n=n+1;
			
			
			cell=new Label(0,1,"",format2_RIGHT);
			sheet.mergeCells(0, 1, 0, n-1);
			sheet.addCell(cell);
			cell=new Label(14,1,"",format2_LEFT);
			sheet.mergeCells(14, 1, 14, n-1);
			sheet.addCell(cell);
			cell=new Label(1,n,"",format2_Top);
			sheet.mergeCells(1, n, 13, n); 
			sheet.addCell(cell);
					
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		return baos;
	}

	public ByteArrayOutputStream exportInsuranceStampExcel(Map<String, Object> content) {
		WritableSheet sheet = null;
		List<Map<String, Object>> stampList = (List<Map<String, Object>>) content.get("stampList") ;
		try {
			workbookSettings.setEncoding("ISO-8859-1");
			sheet = wb.createSheet("保险合同印花税明细表", 1);
			
			WritableFont font2 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
			WritableCellFormat format2_BOTTOM = new WritableCellFormat(font2);
			format2_BOTTOM.setAlignment(Alignment.CENTRE);
			format2_BOTTOM.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2_BOTTOM.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
			format2_BOTTOM.setWrap(true);
			
			WritableCellFormat format2_Top = new WritableCellFormat(font2);
			format2_Top.setAlignment(Alignment.CENTRE);
			format2_Top.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2_Top.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
			format2_Top.setWrap(true);
			
			WritableCellFormat format2_LEFT = new WritableCellFormat(font2);
			format2_LEFT.setAlignment(Alignment.CENTRE);
			format2_LEFT.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2_LEFT.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
			format2_LEFT.setWrap(true);
			
			WritableCellFormat format2_RIGHT = new WritableCellFormat(font2);
			format2_RIGHT.setAlignment(Alignment.CENTRE);
			format2_RIGHT.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2_RIGHT.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
			format2_RIGHT.setWrap(true);
			
			// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
			WritableFont font3 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
			WritableCellFormat format3 = new WritableCellFormat(font3);
			format3.setAlignment(Alignment.LEFT);
			format3.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format3.setWrap(true);
			// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
					 
			WritableCellFormat format5 = new WritableCellFormat(font3);
			format5.setAlignment(Alignment.CENTRE);
			format5.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format5.setWrap(true);
			WritableFont font6 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
			font6.setColour(Colour.BLUE);
			WritableCellFormat format6 = new WritableCellFormat(font6);
			format6.setAlignment(Alignment.LEFT); 
			format6.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format6.setWrap(true);
			
			
			WritableFont font8 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
			font8.setColour(Colour.BLUE);
			WritableCellFormat format8 = new WritableCellFormat(font8);
			format8.setAlignment(Alignment.LEFT); 
			format8.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format8.setWrap(true);
			
			WritableFont font9 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
			WritableCellFormat format9 = new WritableCellFormat(font9);
			format9.setAlignment(Alignment.LEFT);
			format9.setBackground(jxl.format.Colour.GRAY_25);
			format9.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format9.setWrap(true);
			
			WritableFont font4 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
			WritableCellFormat format4 = new WritableCellFormat(font4);
			format4.setAlignment(Alignment.CENTRE);
			format4.setBackground(jxl.format.Colour.GRAY_25);
			format4.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format4.setWrap(true);
			
						
			NumberFormat cnum=new NumberFormat("#,##0.00");
			NumberFormat pnum=new NumberFormat("##0.000%");
			NumberFormat fnum=new NumberFormat("##0.00");
			NumberFormat nnum=new NumberFormat("##"); 
			DateFormat   dateFormat=new DateFormat("yyyy-mm-dd");
			DateFormat   dateFormat2=new DateFormat("MMM-yy");
			WritableCellFormat dateCellFormat_center2=new WritableCellFormat(dateFormat2);
			dateCellFormat_center2.setAlignment(Alignment.CENTRE);
			WritableCellFormat ccellFormat_right=new WritableCellFormat(cnum);
			WritableCellFormat pcellFormat_left=new WritableCellFormat(pnum);
			WritableCellFormat pcellFormat_right=new WritableCellFormat(pnum);
			WritableCellFormat pcellFormat_center=new WritableCellFormat(pnum);
			WritableCellFormat pcellFormat_center2=new WritableCellFormat(pnum);
			WritableCellFormat fcellFormat_left=new WritableCellFormat(fnum);
			WritableCellFormat ccellFormat_left=new WritableCellFormat(cnum);
			WritableCellFormat dateCellFormat_left=new WritableCellFormat(dateFormat);
			WritableCellFormat dateCellFormat_center=new WritableCellFormat(dateFormat);
			ccellFormat_left.setAlignment(Alignment.LEFT);
			pcellFormat_left.setAlignment(Alignment.LEFT);
			pcellFormat_right.setAlignment(Alignment.RIGHT);
			pcellFormat_center.setAlignment(Alignment.CENTRE);
			pcellFormat_center2.setAlignment(Alignment.CENTRE);
			pcellFormat_center2.setVerticalAlignment(VerticalAlignment.CENTRE);		
			fcellFormat_left.setAlignment(Alignment.LEFT);
			dateCellFormat_left.setAlignment(Alignment.LEFT);
			dateCellFormat_center.setAlignment(Alignment.CENTRE);
			WritableCellFormat ncellFormat_center=new WritableCellFormat(nnum);
			ncellFormat_center.setAlignment(Alignment.CENTRE);
			WritableCellFormat ncellFormat_left=new WritableCellFormat(nnum);
			ncellFormat_left.setAlignment(Alignment.LEFT);
			Number number=null;
			//1.0E8 －－100000000
			DecimalFormat   nf2=new   DecimalFormat("###################");	
			
			sheet.setColumnView(0, 4);
			sheet.setColumnView(1, 8);
			sheet.setColumnView(2, 40);
			sheet.setColumnView(3, 40);
			sheet.setColumnView(4, 20);
			sheet.setColumnView(5, 10);
			sheet.setColumnView(6, 40);
			sheet.setColumnView(7, 15);
			sheet.setColumnView(8, 15);
			sheet.setColumnView(9, 15);
			
			int n=0;
			Label cell = null;
			
			cell=new Label(0,0,"");  
			sheet.addCell(cell);
			cell=new Label(1,0,"",format2_BOTTOM);  
			sheet.mergeCells(1, 0, 16, 0);
			sheet.addCell(cell);
			n=n+1;
			sheet.setRowView(0,300,false); 
			
			cell=new Label(0,1,""); 
			sheet.addCell(cell);
			cell=new Label(1,1,"保险合同印花税明细表",format4); 
			sheet.mergeCells(1, 1, 16, 1); 
			sheet.addCell(cell);
			n=n+1;
			sheet.setRowView(1,320,false); 
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(1,n,"序号",format5); 
			
			sheet.mergeCells(1, n, 1, n+1); 
			sheet.addCell(cell); 
			cell=new Label(2,n,"保单号",format5); 
			
			sheet.mergeCells(2, n, 2, n+1); 
			sheet.addCell(cell); 
			cell=new Label(3,n,"保险起始日",format5); 
			
			sheet.mergeCells(3, n, 3, n+1); 
			sheet.addCell(cell); 
			cell=new Label(4,n,"客户名称",format5); 
			
			sheet.mergeCells(4, n, 4, n+1); 
			sheet.addCell(cell); 
			cell=new Label(5,n,"合同号",format5); 
			
			sheet.mergeCells(5, n, 5, n+1); 
			sheet.addCell(cell); 
			cell=new Label(6,n,"业务员",format5); 
			
			sheet.mergeCells(6, n, 6, n+1); 
			sheet.addCell(cell); 
			cell=new Label(7,n,"办事处",format5); 
			
			sheet.mergeCells(7, n, 7, n+1); 
			sheet.addCell(cell); 
			cell=new Label(8,n,"保险合同",format5); 
			
			sheet.mergeCells(8, n,8, n); 
			sheet.addCell(cell); 
			cell=new Label(9,n,"小计",format5); 
			
			sheet.mergeCells(9, n,9, n+1); 
			sheet.addCell(cell); 
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(10,n,"计税金额",format5); 
			
			sheet.addCell(cell);
			cell=new Label(11,n,"税额（计算）",format5); 
			
			sheet.addCell(cell);
			n=n+1;
			
			double BAOXIANJISHUIJINE_sum=0;
			double BAOXIANSHUIE_sum=0;
			double BXSHUIWU_sum=0;
			double XIAOJI_sum=0;

			int i = 0;
			for (Object object2 : stampList) {
				Map stamp=(Map)object2;
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
												
				i = i + 1;						
				number=new Number(1,n,i,ncellFormat_center);
				sheet.addCell(number);
				cell=new Label(2,n,DataUtil.StringUtil(stamp.get("INCU_CODE")),format5);  
				sheet.addCell(cell);
				cell=new Label(3,n,DataUtil.StringUtil(stamp.get("INSU_START_DATE")),format5);  
				sheet.addCell(cell);
				cell=new Label(4,n,DataUtil.StringUtil(stamp.get("CUST_NAME")),format5);  
				sheet.addCell(cell);
				cell=new Label(5,n,DataUtil.StringUtil(stamp.get("LEASE_CODE")),format5);  
				sheet.addCell(cell);
				cell=new Label(6,n,DataUtil.StringUtil(stamp.get("NAME")),format5);  
				sheet.addCell(cell);
				cell=new Label(7,n,DataUtil.StringUtil(stamp.get("DECP_NAME_CN")),format5);  
				sheet.addCell(cell);
						
				if(stamp.get("INSU_PRICE")!=null){
					number=new Number(8,n,Double.parseDouble(stamp.get("INSU_PRICE")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(8,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
				if(stamp.get("TAX")!=null){
					number=new Number(9,n,Double.parseDouble(stamp.get("TAX")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(9,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
				if(stamp.get("TAX")!=null){
					number=new Number(10,n,Double.parseDouble(stamp.get("TAX")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(10,n,0,ccellFormat_right);
					sheet.addCell(number);
				}				

				if(stamp.get("TAX")!=null){
					number=new Number(11,n,Double.parseDouble(stamp.get("TAX")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(11,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
							
				sheet.setRowView(n,300,false);  
				n=n+1;
				//Modify by Michael 2012 3-5 将印花税拆分成合同印花税和保险印花税
				BAOXIANJISHUIJINE_sum=BAOXIANJISHUIJINE_sum+DataUtil.doubleUtil(stamp.get("INSU_PRICE"));
				BAOXIANSHUIE_sum=BAOXIANSHUIE_sum+DataUtil.doubleUtil(stamp.get("TAX"));
				BXSHUIWU_sum=BXSHUIWU_sum+DataUtil.doubleUtil(stamp.get("TAX"));
				XIAOJI_sum=XIAOJI_sum+DataUtil.doubleUtil(stamp.get("TAX"));

			}
			 
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(1,n,"小计",format5);
			sheet.mergeCells(1, n, 2, n);
			sheet.addCell(cell);
			
			//Modify by Michael 2012 3-5 将印花税拆分成合同印花税和保险印花税
			number=new Number(8,n,BAOXIANJISHUIJINE_sum,ccellFormat_right);
			sheet.addCell(number);
			number=new Number(9,n,BAOXIANSHUIE_sum,ccellFormat_right);
			sheet.addCell(number);
			number=new Number(10,n,BXSHUIWU_sum,ccellFormat_right);
			sheet.addCell(number);
			number=new Number(11,n,XIAOJI_sum,ccellFormat_right);
			sheet.addCell(number);

			sheet.setRowView(n,300,false);  
			n=n+1;
			
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(1,n,"税务申报",format5);
			sheet.mergeCells(1, n, 2, n);
			sheet.addCell(cell);

			double dd3 = Double.parseDouble(Math.round(BAOXIANSHUIE_sum*100)+"")/100;

			double d3 = Double.parseDouble(Math.round(dd3 *10)+"" )/10;

			//Modify by Michael 2012 3-5 将印花税拆分成合同印花税和保险印花税
			number=new Number(6,n,BAOXIANJISHUIJINE_sum,ccellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,BAOXIANSHUIE_sum,ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,d3,ccellFormat_right);
			sheet.addCell(number);
			number=new Number(9,n,d3,ccellFormat_right);
			sheet.addCell(number);

			sheet.setRowView(n,300,false);  
			n=n+1;
			
			cell=new Label(0,1,"",format2_RIGHT);
			sheet.mergeCells(0, 1, 0, n-1);
			sheet.addCell(cell);
			cell=new Label(10,1,"",format2_LEFT);
			sheet.mergeCells(10, 1, 10, n-1);
			sheet.addCell(cell);
			cell=new Label(1,n,"",format2_Top);
			sheet.mergeCells(1, n, 9, n); 
			sheet.addCell(cell);
					
		}catch(Exception e){
			logger.error(e);
		}
		return baos;
	}
	
	public ByteArrayOutputStream exportBusinessTaxExcel(List<Map> businessTaxList) {
		WritableSheet sheet = null;
		
		try {
			workbookSettings.setEncoding("ISO-8859-1");
			sheet = wb.createSheet("营业税明细表", 1);
			
			WritableFont font2 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
			WritableCellFormat format2_BOTTOM = new WritableCellFormat(font2);
			format2_BOTTOM.setAlignment(Alignment.CENTRE);
			format2_BOTTOM.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2_BOTTOM.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
			format2_BOTTOM.setWrap(true);
			
			WritableCellFormat format2_Top = new WritableCellFormat(font2);
			format2_Top.setAlignment(Alignment.CENTRE);
			format2_Top.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2_Top.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
			format2_Top.setWrap(true);
			
			WritableCellFormat format2_LEFT = new WritableCellFormat(font2);
			format2_LEFT.setAlignment(Alignment.CENTRE);
			format2_LEFT.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2_LEFT.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
			format2_LEFT.setWrap(true);
			
			WritableCellFormat format2_RIGHT = new WritableCellFormat(font2);
			format2_RIGHT.setAlignment(Alignment.CENTRE);
			format2_RIGHT.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2_RIGHT.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
			format2_RIGHT.setWrap(true);
			
			// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
			WritableFont font3 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
			WritableCellFormat format3 = new WritableCellFormat(font3);
			format3.setAlignment(Alignment.LEFT);
			format3.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format3.setWrap(true);
			// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
					 
			WritableCellFormat format5 = new WritableCellFormat(font3);
			format5.setAlignment(Alignment.CENTRE);
			format5.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format5.setWrap(true);
			WritableFont font6 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
			font6.setColour(Colour.BLUE);
			WritableCellFormat format6 = new WritableCellFormat(font6);
			format6.setAlignment(Alignment.LEFT); 
			format6.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format6.setWrap(true);
			
			
			WritableFont font8 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
			font8.setColour(Colour.BLUE);
			WritableCellFormat format8 = new WritableCellFormat(font8);
			format8.setAlignment(Alignment.LEFT); 
			format8.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format8.setWrap(true);
			
			
			WritableFont font9 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
			WritableCellFormat format9 = new WritableCellFormat(font9);
			format9.setAlignment(Alignment.LEFT);
			format9.setBackground(jxl.format.Colour.GRAY_25);
			format9.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format9.setWrap(true);
			
			
			WritableFont font4 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
			WritableCellFormat format4 = new WritableCellFormat(font4);
			format4.setAlignment(Alignment.CENTRE);
			format4.setBackground(jxl.format.Colour.GRAY_25);
			format4.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format4.setWrap(true);
			
						
			NumberFormat cnum=new NumberFormat("#,##0.00");
			NumberFormat pnum=new NumberFormat("##0.000%");
			NumberFormat fnum=new NumberFormat("##0.00");
			NumberFormat nnum=new NumberFormat("##"); 
			DateFormat   dateFormat=new DateFormat("yyyy-mm-dd");
			DateFormat   dateFormat2=new DateFormat("MMM-yy");
			WritableCellFormat dateCellFormat_center2=new WritableCellFormat(dateFormat2);
			dateCellFormat_center2.setAlignment(Alignment.CENTRE);
			WritableCellFormat ccellFormat_right=new WritableCellFormat(cnum);
			WritableCellFormat pcellFormat_left=new WritableCellFormat(pnum);
			WritableCellFormat pcellFormat_right=new WritableCellFormat(pnum);
			WritableCellFormat pcellFormat_center=new WritableCellFormat(pnum);
			WritableCellFormat pcellFormat_center2=new WritableCellFormat(pnum);
			WritableCellFormat fcellFormat_left=new WritableCellFormat(fnum);
			WritableCellFormat ccellFormat_left=new WritableCellFormat(cnum);
			WritableCellFormat dateCellFormat_left=new WritableCellFormat(dateFormat);
			WritableCellFormat dateCellFormat_center=new WritableCellFormat(dateFormat);
			ccellFormat_left.setAlignment(Alignment.LEFT);
			pcellFormat_left.setAlignment(Alignment.LEFT);
			pcellFormat_right.setAlignment(Alignment.RIGHT);
			pcellFormat_center.setAlignment(Alignment.CENTRE);
			pcellFormat_center2.setAlignment(Alignment.CENTRE);
			pcellFormat_center2.setVerticalAlignment(VerticalAlignment.CENTRE);		
			fcellFormat_left.setAlignment(Alignment.LEFT);
			dateCellFormat_left.setAlignment(Alignment.LEFT);
			dateCellFormat_center.setAlignment(Alignment.CENTRE);
			WritableCellFormat ncellFormat_center=new WritableCellFormat(nnum);
			ncellFormat_center.setAlignment(Alignment.CENTRE);
			WritableCellFormat ncellFormat_left=new WritableCellFormat(nnum);
			ncellFormat_left.setAlignment(Alignment.LEFT);
			Number number=null;
			//1.0E8 －－100000000
			DecimalFormat   nf2=new   DecimalFormat("###################");	
			
			sheet.setColumnView(0, 4);
			sheet.setColumnView(1, 8);
			sheet.setColumnView(2, 40);
			sheet.setColumnView(3, 40);
			sheet.setColumnView(4, 20);
			sheet.setColumnView(5, 10);
			sheet.setColumnView(6, 40);
			sheet.setColumnView(7, 15);
			sheet.setColumnView(8, 15);
			sheet.setColumnView(9, 15);
			sheet.setColumnView(10, 15);
			sheet.setColumnView(11, 15);
			sheet.setColumnView(12, 15);
			sheet.setColumnView(13,20);
			sheet.setColumnView(14,4);
			
			int n=0;
			Label cell = null;
			
			
			cell=new Label(0,0,"");  
			sheet.addCell(cell);
			cell=new Label(1,0,"",format2_BOTTOM);  
			sheet.mergeCells(1, 0,13, 0);
			sheet.addCell(cell);
			n=n+1;
			sheet.setRowView(0,300,false); 
			
			cell=new Label(0,1,""); 
			sheet.addCell(cell);
			cell=new Label(1,1,"营业税明细表",format4); 
			sheet.mergeCells(1, 1, 13, 1); 
			sheet.addCell(cell);
			n=n+1;
			sheet.setRowView(1,320,false); 
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(1,n,"序号",format5); 
			sheet.mergeCells(1, n, 1, n+1); 
			sheet.addCell(cell); 
			cell=new Label(2,n,"客户名称",format5); 
			sheet.mergeCells(2, n, 2, n+1); 
			sheet.addCell(cell); 
			cell=new Label(3,n,"供应商",format5); 
			sheet.mergeCells(3, n, 3, n+1); 
			sheet.addCell(cell); 
			cell=new Label(4,n,"合同号",format5); 
			sheet.mergeCells(4, n, 4, n+1); 
			sheet.addCell(cell); 
			cell=new Label(5,n,"业务员",format5); 
			sheet.mergeCells(5, n, 5, n+1); 
			sheet.addCell(cell); 
			cell=new Label(6,n,"办事处",format5); 
			sheet.mergeCells(6, n, 6, n+1); 
			sheet.addCell(cell); 
			cell=new Label(7,n,"营业税",format5); 
			sheet.mergeCells(7, n,8, n); 
			sheet.addCell(cell); 
			cell=new Label(9,n,"城建税",format5); 
			sheet.mergeCells(9, n,10, n); 
			sheet.addCell(cell); 
			cell=new Label(11,n,"地区教育费附加",format5); 
			sheet.mergeCells(11, n,12, n); 
			sheet.addCell(cell); 
			cell=new Label(13,n,"小计",format5); 
			sheet.mergeCells(13, n,13, n+1); 
			sheet.addCell(cell); 
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(6,n,"计税依据",format5); 
			sheet.addCell(cell);
			cell=new Label(7,n,"税额",format5); 
			sheet.addCell(cell);
			cell=new Label(8,n,"计税依据",format5); 
			sheet.addCell(cell);
			cell=new Label(9,n,"税额",format5); 
			sheet.addCell(cell);
			cell=new Label(10,n,"计税依据",format5); 
			sheet.addCell(cell);
			cell=new Label(11,n,"税额",format5); 
			sheet.addCell(cell);
			n=n+1;
			
			
			double REN_PRICE_sum=0;
			double YYSHUIE_sum=0;
			double CJSHUIE_sum=0;
			double JYSHUIE_sum=0;
			double XIAOJI_sum=0;

			int i = 0;
			for (Object object2 : businessTaxList) {
				Map businessTax =(Map)object2;
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
												
				i = i + 1;						
				number=new Number(1,n,i,ncellFormat_center);
				sheet.addCell(number);
				
				cell=new Label(2,n,DataUtil.StringUtil(businessTax.get("CUST_NAME")),format5);  
				sheet.addCell(cell);
				cell=new Label(3,n,DataUtil.StringUtil(businessTax.get("SUPL_NAME")),format5);  
				sheet.addCell(cell);
				cell=new Label(4,n,DataUtil.StringUtil(businessTax.get("LEASE_CODE")),format5);  
				sheet.addCell(cell);
				cell=new Label(5,n,DataUtil.StringUtil(businessTax.get("NAME")),format5);  
				sheet.addCell(cell);
				cell=new Label(6,n,DataUtil.StringUtil(businessTax.get("DECP_NAME_CN")),format5);  
				sheet.addCell(cell);
						
				if(businessTax.get("REN_PRICE")!=null){
					number=new Number(7,n,Double.parseDouble(businessTax.get("REN_PRICE")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(7,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
				if(businessTax.get("YYSHUIE")!=null){
					number=new Number(8,n,Double.parseDouble(businessTax.get("YYSHUIE")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(8,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
				if(businessTax.get("YYSHUIE")!=null){
					number=new Number(9,n,Double.parseDouble(businessTax.get("YYSHUIE")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(9,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
				if(businessTax.get("CJSHUIE")!=null){
					number=new Number(10,n,Double.parseDouble(businessTax.get("CJSHUIE")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(10,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
				if(businessTax.get("YYSHUIE")!=null){
					number=new Number(11,n,Double.parseDouble(businessTax.get("YYSHUIE")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(11,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
				if(businessTax.get("JYSHUIE")!=null){
					number=new Number(12,n,Double.parseDouble(businessTax.get("JYSHUIE")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(12,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
				if(businessTax.get("XIAOJI")!=null){
					number=new Number(13,n,Double.parseDouble(businessTax.get("XIAOJI")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(13,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
							
				sheet.setRowView(n,300,false);  
				n=n+1;
				
				REN_PRICE_sum=REN_PRICE_sum+DataUtil.doubleUtil(businessTax.get("REN_PRICE"));
				YYSHUIE_sum=YYSHUIE_sum+DataUtil.doubleUtil(businessTax.get("YYSHUIE"));
				CJSHUIE_sum=CJSHUIE_sum+DataUtil.doubleUtil(businessTax.get("CJSHUIE"));
				JYSHUIE_sum=JYSHUIE_sum+DataUtil.doubleUtil(businessTax.get("JYSHUIE"));
				XIAOJI_sum=XIAOJI_sum+DataUtil.doubleUtil(businessTax.get("XIAOJI"));
			}
			 
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(1,n,"小计",format5);
			sheet.mergeCells(1, n, 2, n);
			sheet.addCell(cell);
			
			number=new Number(7,n,REN_PRICE_sum,ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,YYSHUIE_sum,ccellFormat_right);
			sheet.addCell(number);
			number=new Number(9,n,YYSHUIE_sum,ccellFormat_right);
			sheet.addCell(number);
			number=new Number(10,n,CJSHUIE_sum,ccellFormat_right);
			sheet.addCell(number);
			number=new Number(11,n,YYSHUIE_sum,ccellFormat_right);
			sheet.addCell(number);
			number=new Number(12,n,JYSHUIE_sum,ccellFormat_right);
			sheet.addCell(number);
			number=new Number(13,n,XIAOJI_sum,ccellFormat_right);
			sheet.addCell(number);

			sheet.setRowView(n,300,false);  
			n=n+1;
			
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(1,n,"税务申报",format5);
			sheet.mergeCells(1, n, 2, n);
			sheet.addCell(cell);
			number=new Number(7,n,REN_PRICE_sum,ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,YYSHUIE_sum,ccellFormat_right);
			sheet.addCell(number);
			number=new Number(9,n,YYSHUIE_sum,ccellFormat_right);
			sheet.addCell(number);
			number=new Number(10,n,CJSHUIE_sum,ccellFormat_right);
			sheet.addCell(number);
			number=new Number(11,n,YYSHUIE_sum,ccellFormat_right);
			sheet.addCell(number);
			number=new Number(12,n,JYSHUIE_sum,ccellFormat_right);
			sheet.addCell(number);
			number=new Number(13,n,XIAOJI_sum,ccellFormat_right);
			sheet.addCell(number);

			sheet.setRowView(n,300,false);  
			n=n+1;

				
			cell=new Label(0,1,"",format2_RIGHT);
			sheet.mergeCells(0, 1, 0, n-1);
			sheet.addCell(cell);
			cell=new Label(14,1,"",format2_LEFT);
			sheet.mergeCells(14, 1, 14, n-1);
			sheet.addCell(cell);
			cell=new Label(1,n,"",format2_Top);
			sheet.mergeCells(1, n, 13, n); 
			sheet.addCell(cell);
					
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		return baos;
	}

	public ByteArrayOutputStream exportRealPriceExcel(List<Map> realPriceList) {
		WritableSheet sheet = null;
		
		try {
			workbookSettings.setEncoding("ISO-8859-1");
			sheet = wb.createSheet("长期应收款余额变动表", 1);
			
			WritableFont font2 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
			WritableCellFormat format2_BOTTOM = new WritableCellFormat(font2);
			format2_BOTTOM.setAlignment(Alignment.CENTRE);
			format2_BOTTOM.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2_BOTTOM.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
			format2_BOTTOM.setWrap(true);
			
			WritableCellFormat format2_Top = new WritableCellFormat(font2);
			format2_Top.setAlignment(Alignment.CENTRE);
			format2_Top.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2_Top.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
			format2_Top.setWrap(true);
			
			WritableCellFormat format2_LEFT = new WritableCellFormat(font2);
			format2_LEFT.setAlignment(Alignment.CENTRE);
			format2_LEFT.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2_LEFT.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
			format2_LEFT.setWrap(true);
			
			WritableCellFormat format2_RIGHT = new WritableCellFormat(font2);
			format2_RIGHT.setAlignment(Alignment.CENTRE);
			format2_RIGHT.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2_RIGHT.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
			format2_RIGHT.setWrap(true);
			
			// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
			WritableFont font3 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
			WritableCellFormat format3 = new WritableCellFormat(font3);
			format3.setAlignment(Alignment.LEFT);
			format3.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format3.setWrap(true);
			// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
					 
			WritableCellFormat format5 = new WritableCellFormat(font3);
			format5.setAlignment(Alignment.CENTRE);
			format5.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format5.setWrap(true);
			WritableFont font6 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
			font6.setColour(Colour.BLUE);
			WritableCellFormat format6 = new WritableCellFormat(font6);
			format6.setAlignment(Alignment.LEFT); 
			format6.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format6.setWrap(true);
			
			
			WritableFont font8 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
			font8.setColour(Colour.BLUE);
			WritableCellFormat format8 = new WritableCellFormat(font8);
			format8.setAlignment(Alignment.LEFT); 
			format8.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format8.setWrap(true);
			
			
			WritableFont font9 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
			WritableCellFormat format9 = new WritableCellFormat(font9);
			format9.setAlignment(Alignment.LEFT);
			format9.setBackground(jxl.format.Colour.GRAY_25);
			format9.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format9.setWrap(true);
			
			
			WritableFont font4 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
			WritableCellFormat format4 = new WritableCellFormat(font4);
			format4.setAlignment(Alignment.CENTRE);
			format4.setBackground(jxl.format.Colour.GRAY_25);
			format4.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format4.setWrap(true);
			
						
			NumberFormat cnum=new NumberFormat("#,##0.00");
			NumberFormat pnum=new NumberFormat("##0.000%");
			NumberFormat fnum=new NumberFormat("##0.00");
			NumberFormat nnum=new NumberFormat("##"); 
			DateFormat   dateFormat=new DateFormat("yyyy-mm-dd");
			DateFormat   dateFormat2=new DateFormat("MMM-yy");
			WritableCellFormat dateCellFormat_center2=new WritableCellFormat(dateFormat2);
			dateCellFormat_center2.setAlignment(Alignment.CENTRE);
			WritableCellFormat ccellFormat_right=new WritableCellFormat(cnum);
			WritableCellFormat pcellFormat_left=new WritableCellFormat(pnum);
			WritableCellFormat pcellFormat_right=new WritableCellFormat(pnum);
			WritableCellFormat pcellFormat_center=new WritableCellFormat(pnum);
			WritableCellFormat pcellFormat_center2=new WritableCellFormat(pnum);
			WritableCellFormat fcellFormat_left=new WritableCellFormat(fnum);
			WritableCellFormat ccellFormat_left=new WritableCellFormat(cnum);
			WritableCellFormat dateCellFormat_left=new WritableCellFormat(dateFormat);
			WritableCellFormat dateCellFormat_center=new WritableCellFormat(dateFormat);
			ccellFormat_left.setAlignment(Alignment.LEFT);
			pcellFormat_left.setAlignment(Alignment.LEFT);
			pcellFormat_right.setAlignment(Alignment.RIGHT);
			pcellFormat_center.setAlignment(Alignment.CENTRE);
			pcellFormat_center2.setAlignment(Alignment.CENTRE);
			pcellFormat_center2.setVerticalAlignment(VerticalAlignment.CENTRE);		
			fcellFormat_left.setAlignment(Alignment.LEFT);
			dateCellFormat_left.setAlignment(Alignment.LEFT);
			dateCellFormat_center.setAlignment(Alignment.CENTRE);
			WritableCellFormat ncellFormat_center=new WritableCellFormat(nnum);
			ncellFormat_center.setAlignment(Alignment.CENTRE);
			WritableCellFormat ncellFormat_left=new WritableCellFormat(nnum);
			ncellFormat_left.setAlignment(Alignment.LEFT);
			Number number=null;
			//1.0E8 －－100000000
			DecimalFormat   nf2=new   DecimalFormat("###################");	
			
			sheet.setColumnView(0, 4);
			sheet.setColumnView(1, 8);
			sheet.setColumnView(2, 40);
			sheet.setColumnView(3, 40);
			sheet.setColumnView(4, 20);
			sheet.setColumnView(5, 15);
			sheet.setColumnView(6, 40);
			sheet.setColumnView(7, 15);
			sheet.setColumnView(8, 15);
			sheet.setColumnView(9, 15);
			sheet.setColumnView(10, 15);
			sheet.setColumnView(11, 15);
			sheet.setColumnView(12, 15);
			sheet.setColumnView(13, 15);
			sheet.setColumnView(14, 15);
			sheet.setColumnView(15, 15);
			sheet.setColumnView(16, 15);
			sheet.setColumnView(17, 15);
			sheet.setColumnView(18, 15);
			sheet.setColumnView(19, 15);
			sheet.setColumnView(20, 30);
			sheet.setColumnView(21, 4);
						
			int n=0;
			Label cell = null;
			
			
			cell=new Label(0,0,"");  
			sheet.addCell(cell);
			cell=new Label(1,0,"",format2_BOTTOM);  
			sheet.mergeCells(1, 0, 20, 0);
			sheet.addCell(cell);
			n=n+1;
			sheet.setRowView(0,300,false); 
			
			cell=new Label(0,1,""); 
			sheet.addCell(cell);
			cell=new Label(1,1,"长期应收款余额变动表",format4); 
			sheet.mergeCells(1, 1, 20, 1); 
			sheet.addCell(cell);
			n=n+1;
			sheet.setRowView(1,320,false); 
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(1,n,"序号",format5); 
			sheet.mergeCells(1, n, 1, n+1); 
			sheet.addCell(cell); 
			cell=new Label(2,n,"客户名称",format5); 
			sheet.mergeCells(2, n, 2, n+1); 
			sheet.addCell(cell); 
			cell=new Label(3,n,"供应商",format5); 
			sheet.mergeCells(3, n,3, n+1); 
			sheet.addCell(cell); 
			cell=new Label(4,n,"合同号",format5); 
			sheet.mergeCells(4, n,4, n+1); 
			sheet.addCell(cell); 
			cell=new Label(5,n,"业务员",format5); 
			sheet.mergeCells(5, n,5, n+1); 
			sheet.addCell(cell); 
			cell=new Label(6,n,"办事处",format5); 
			sheet.mergeCells(6, n,6, n+1); 
			sheet.addCell(cell); 
			cell=new Label(7,n,"期初余额",format5); 
			sheet.mergeCells(7, n,9, n); 
			sheet.addCell(cell); 
			cell=new Label(10,n,"本期应收增加",format5); 
			sheet.mergeCells(10, n,12, n); 
			sheet.addCell(cell); 
			cell=new Label(13,n,"本期应收减少",format5); 
			sheet.mergeCells(13, n,15, n); 
			sheet.addCell(cell); 
			cell=new Label(16,n,"期末余额",format5); 
			sheet.mergeCells(16, n,18, n); 
			sheet.addCell(cell); 
			cell=new Label(19,n,"期末净值",format5); 
			sheet.mergeCells(19, n,19, n+1); 
			sheet.addCell(cell); 
			cell=new Label(20,n,"保证金类型",format5); 
			sheet.mergeCells(20, n,20, n+1); 
			sheet.addCell(cell); 
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(7,n,"长期应收款",format5); 
			sheet.addCell(cell);
			cell=new Label(8,n,"未实现融资利息",format5); 
			sheet.addCell(cell);
			cell=new Label(9,n,"保证金余额",format5); 
			sheet.addCell(cell);
			
			cell=new Label(10,n,"长期应收款",format5); 
			sheet.addCell(cell);
			cell=new Label(11,n,"未实现融资利息",format5); 
			sheet.addCell(cell);
			cell=new Label(12,n,"保证金",format5); 
			sheet.addCell(cell);
			
			cell=new Label(13,n,"长期应收款",format5); 
			sheet.addCell(cell);
			cell=new Label(14,n,"未实现融资收益利息",format5); 
			sheet.addCell(cell);
			cell=new Label(15,n,"保证金",format5); 
			sheet.addCell(cell);
			
			cell=new Label(16,n,"长期应收款",format5); 
			sheet.addCell(cell);
			cell=new Label(17,n,"未实现融资利息",format5); 
			sheet.addCell(cell);
			cell=new Label(18,n,"保证金",format5); 
			sheet.addCell(cell);
			n=n+1;
			


			int i = 0;
			for (Object object2 : realPriceList) {
				Map realPrice =(Map)object2;
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
												
				i = i + 1;						
				number=new Number(1,n,i,ncellFormat_center);
				sheet.addCell(number);
				
				cell=new Label(2,n,DataUtil.StringUtil(realPrice.get("CUST_NAME")),format5);  
				sheet.addCell(cell);
				cell=new Label(3,n,DataUtil.StringUtil(realPrice.get("SUPL_NAME")),format5);  
				sheet.addCell(cell);
				cell=new Label(4,n,DataUtil.StringUtil(realPrice.get("LEASE_CODE")),format5);  
				sheet.addCell(cell);
				cell=new Label(5,n,DataUtil.StringUtil(realPrice.get("NAME")),format5);  
				sheet.addCell(cell);
				cell=new Label(6,n,DataUtil.StringUtil(realPrice.get("DECP_NAME_CN")),format5);  
				sheet.addCell(cell);
				//期初余额	
				if(realPrice.get("SQC")!=null){
					number=new Number(7,n,Double.parseDouble(realPrice.get("SQC")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(7,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
				if(realPrice.get("SQW")!=null){
					number=new Number(8,n,Double.parseDouble(realPrice.get("SQW")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(8,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
				if(realPrice.get("SQB")!=null){
					number=new Number(9,n,Double.parseDouble(realPrice.get("SQB")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(9,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
				
				//本期应收增加
				if(realPrice.get("YZC")!=null){
					number=new Number(10,n,Double.parseDouble(realPrice.get("YZC")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(10,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
				if(realPrice.get("YZW")!=null){
					number=new Number(11,n,Double.parseDouble(realPrice.get("YZW")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(11,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
				if(realPrice.get("YZB")!=null){
					number=new Number(12,n,Double.parseDouble(realPrice.get("YZB")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(12,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
				
				
				//本期应收减少
				if(realPrice.get("YJC")!=null){
					number=new Number(13,n,Double.parseDouble(realPrice.get("YJC")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(13,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
				if(realPrice.get("YJW")!=null){
					number=new Number(14,n,Double.parseDouble(realPrice.get("YJW")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(14,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
				if(realPrice.get("YJB")!=null){
					number=new Number(15,n,Double.parseDouble(realPrice.get("YJB")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(15,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
				
				
				
				//期末余额
				if(realPrice.get("SYC")!=null){
					number=new Number(16,n,Double.parseDouble(realPrice.get("SYC")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(16,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
				if(realPrice.get("SYW")!=null){
					number=new Number(17,n,Double.parseDouble(realPrice.get("SYW")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(17,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
				if(realPrice.get("SYB")!=null){
					number=new Number(18,n,Double.parseDouble(realPrice.get("SYB")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(18,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
				
				
				//期末净值
				if(realPrice.get("QIMOJINGZHI")!=null){
					number=new Number(19,n,Double.parseDouble(realPrice.get("QIMOJINGZHI")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(19,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
				//保证金类别
				String bzj = "" ;
				if(realPrice.get("PLEDGE_AVE_PRICE") != null && Double.parseDouble(realPrice.get("PLEDGE_AVE_PRICE").toString()) > 0){
					bzj += "平均冲抵" ;
				}
				if(realPrice.get("PLEDGE_BACK_PRICE") != null && Double.parseDouble(realPrice.get("PLEDGE_BACK_PRICE").toString()) > 0){
					bzj += "期末退回" ;
				}
				if(realPrice.get("PLEDGE_LAST_PRICE") != null && Double.parseDouble(realPrice.get("PLEDGE_LAST_PRICE").toString()) > 0){
					bzj += "期末冲抵" ;
				}
				cell=new Label(20,n,bzj,format5); 
				sheet.addCell(cell);
							
				sheet.setRowView(n,300,false);  
				n=n+1;
				

			}
			 
			
			cell=new Label(0,1,"",format2_RIGHT);
			sheet.mergeCells(0, 1, 0, n-1);
			sheet.addCell(cell);
			cell=new Label(21,1,"",format2_LEFT);
			sheet.mergeCells(21, 1, 21, n-1);
			sheet.addCell(cell);
			cell=new Label(1,n,"",format2_Top);
			sheet.mergeCells(1, n, 20, n); 
			sheet.addCell(cell);
					
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		return baos;
	}

	@SuppressWarnings("unchecked")
	public ByteArrayOutputStream exportDifferentExcel(Map exportMap) {
		List<Map> nianList=new ArrayList();
		List<Map>  yueList=new ArrayList();
		List<Map>  ren_priceList=new ArrayList();
		List<Map>  caiwuList=new ArrayList();
		List<Map>  shuiwuList=new ArrayList();
		List<Map>  chayiList=new ArrayList();
		List<Map>  yingyeList=new ArrayList();
		List<Map>  chengjianList=new ArrayList();
		List<Map>  jiaoyuList=new ArrayList();
		List<Map>  insure_priceList=new ArrayList();
		List<Map>  yinhuaList=new ArrayList();
		List<Map>  xiaojiList=new ArrayList();
		List<Map>  cliList=new ArrayList();
		List<Map>  sliList=new ArrayList();
		List<Map>  licList=new ArrayList();
	
		WritableSheet sheet = null;
		
		try {
			workbookSettings.setEncoding("ISO-8859-1");
			//设置sheet名称  默认为成本差异
			if(exportMap.get("sheetName") != null){
				sheet = wb.createSheet(exportMap.get("sheetName").toString(), 1) ;
			} else {
				sheet = wb.createSheet("成本差异", 1);
			}
			WritableFont font2 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
			WritableCellFormat format2_BOTTOM = new WritableCellFormat(font2);
			format2_BOTTOM.setAlignment(Alignment.CENTRE);
			format2_BOTTOM.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2_BOTTOM.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
			format2_BOTTOM.setWrap(true);
			
			WritableCellFormat format2_Top = new WritableCellFormat(font2);
			format2_Top.setAlignment(Alignment.CENTRE);
			format2_Top.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2_Top.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
			format2_Top.setWrap(true);
			
			WritableCellFormat format2_LEFT = new WritableCellFormat(font2);
			format2_LEFT.setAlignment(Alignment.CENTRE);
			format2_LEFT.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2_LEFT.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
			format2_LEFT.setWrap(true);
			
			WritableCellFormat format2_RIGHT = new WritableCellFormat(font2);
			format2_RIGHT.setAlignment(Alignment.CENTRE);
			format2_RIGHT.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2_RIGHT.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
			format2_RIGHT.setWrap(true);
			
			// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
			WritableFont font3 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
			WritableCellFormat format3 = new WritableCellFormat(font3);
			format3.setAlignment(Alignment.LEFT);
			format3.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format3.setWrap(true);
			// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
					 
			WritableCellFormat format5 = new WritableCellFormat(font3);
			format5.setAlignment(Alignment.CENTRE);
			format5.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format5.setWrap(true);
			WritableFont font6 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
			font6.setColour(Colour.BLUE);
			WritableCellFormat format6 = new WritableCellFormat(font6);
			format6.setAlignment(Alignment.LEFT); 
			format6.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format6.setWrap(true);
			
			
			WritableFont font8 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
			font8.setColour(Colour.BLUE);
			WritableCellFormat format8 = new WritableCellFormat(font8);
			format8.setAlignment(Alignment.LEFT); 
			format8.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format8.setWrap(true);
			
			
			WritableFont font9 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
			WritableCellFormat format9 = new WritableCellFormat(font9);
			format9.setAlignment(Alignment.LEFT);
			format9.setBackground(jxl.format.Colour.GRAY_25);
			format9.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format9.setWrap(true);
			
			
			WritableFont font4 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
			WritableCellFormat format4 = new WritableCellFormat(font4);
			format4.setAlignment(Alignment.CENTRE);
			format4.setBackground(jxl.format.Colour.GRAY_25);
			format4.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format4.setWrap(true);
			
						
			NumberFormat cnum=new NumberFormat("#,##0.00");
			NumberFormat pnum=new NumberFormat("##0.000%");
			NumberFormat fnum=new NumberFormat("##0.00");
			NumberFormat nnum=new NumberFormat("##"); 
			DateFormat   dateFormat=new DateFormat("yyyy-mm-dd");
			DateFormat   dateFormat2=new DateFormat("MMM-yy");
			WritableCellFormat dateCellFormat_center2=new WritableCellFormat(dateFormat2);
			dateCellFormat_center2.setAlignment(Alignment.CENTRE);
			WritableCellFormat ccellFormat_right=new WritableCellFormat(cnum);
			WritableCellFormat pcellFormat_left=new WritableCellFormat(pnum);
			WritableCellFormat pcellFormat_right=new WritableCellFormat(pnum);
			WritableCellFormat pcellFormat_center=new WritableCellFormat(pnum);
			WritableCellFormat pcellFormat_center2=new WritableCellFormat(pnum);
			WritableCellFormat fcellFormat_left=new WritableCellFormat(fnum);
			WritableCellFormat ccellFormat_left=new WritableCellFormat(cnum);
			WritableCellFormat dateCellFormat_left=new WritableCellFormat(dateFormat);
			WritableCellFormat dateCellFormat_center=new WritableCellFormat(dateFormat);
			ccellFormat_left.setAlignment(Alignment.LEFT);
			pcellFormat_left.setAlignment(Alignment.LEFT);
			pcellFormat_right.setAlignment(Alignment.RIGHT);
			pcellFormat_center.setAlignment(Alignment.CENTRE);
			pcellFormat_center2.setAlignment(Alignment.CENTRE);
			pcellFormat_center2.setVerticalAlignment(VerticalAlignment.CENTRE);		
			fcellFormat_left.setAlignment(Alignment.LEFT);
			dateCellFormat_left.setAlignment(Alignment.LEFT);
			dateCellFormat_center.setAlignment(Alignment.CENTRE);
			WritableCellFormat ncellFormat_center=new WritableCellFormat(nnum);
			ncellFormat_center.setAlignment(Alignment.CENTRE);
			WritableCellFormat ncellFormat_left=new WritableCellFormat(nnum);
			ncellFormat_left.setAlignment(Alignment.LEFT);
			Number number=null;
			//1.0E8 －－100000000
			DecimalFormat   nf2=new   DecimalFormat("###################");	
			
			nianList = (List<Map> ) exportMap.get("nianList");
			yueList = (List<Map> ) exportMap.get("yueList");
			ren_priceList = (List<Map> ) exportMap.get("ren_priceList");
			caiwuList = (List<Map> ) exportMap.get("caiwuList");
			shuiwuList = (List<Map> ) exportMap.get("shuiwuList");
			chayiList = (List<Map> ) exportMap.get("chayiList");
			yingyeList = (List<Map> ) exportMap.get("yingyeList");
			chengjianList = (List<Map> ) exportMap.get("chengjianList");
			jiaoyuList = (List<Map> ) exportMap.get("jiaoyuList");
			insure_priceList = (List<Map> ) exportMap.get("insure_priceList");
			chengjianList = (List<Map> ) exportMap.get("chengjianList");
			yinhuaList = (List<Map> ) exportMap.get("yinhuaList");
			xiaojiList = (List<Map> ) exportMap.get("xiaojiList");
			cliList = (List<Map> ) exportMap.get("cliList");
			sliList = (List<Map> ) exportMap.get("sliList");
			licList = (List<Map> ) exportMap.get("licList");
			
			sheet.setColumnView(0, 4);
			sheet.setColumnView(1, 8);
			sheet.setColumnView(2, 20);
			//小计列
			sheet.setColumnView(3, 17);
			for(int i = 0;i<nianList.size();i++){
				sheet.setColumnView(i+4, 15);
			}
			
			sheet.setColumnView(nianList.size()+4, 4);
						
			int n=0;
			Label cell = null;
			
			
			cell=new Label(0,0,"");  
			sheet.addCell(cell);
			cell=new Label(1,0,"",format2_BOTTOM);  
			sheet.mergeCells(1, 0, nianList.size()+3, 0);
			sheet.addCell(cell);
			n=n+1;
			sheet.setRowView(0,300,false); 
			
			cell=new Label(0,1,""); 
			sheet.addCell(cell);
			cell=new Label(1,1,"成本差异",format4); 
			sheet.mergeCells(1, 1, nianList.size()+3, 1); 
			sheet.addCell(cell);
			n=n+1;
			sheet.setRowView(1,320,false); 
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(1,n,"项目",format5); 
			sheet.mergeCells(1, n, 2, n+1); 
			sheet.addCell(cell);		
			//添加小计
			cell=new Label(3,n,"小计",format5) ;
			sheet.mergeCells(3, n, 3, n+1) ;
			sheet.addCell(cell) ;
			//添加小计 结束
			for(int i=0;i<nianList.size();i++){
				cell=new Label(i+4,n,nianList.get(i).get("NIAN")+"年",format5); 
				sheet.addCell(cell);
			}
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);		
			for(int i=0;i<yueList.size();i++){
				cell=new Label(i+4,n,yueList.get(i).get("YUE")+"月",format5); 
				sheet.addCell(cell);
			}
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(1,n,"收入",format5); 
			sheet.mergeCells(1, n, 1, n+2);
			sheet.addCell(cell);
//			cell=new Label(2,n,"应确认收入",format5); 
//			sheet.addCell(cell);
//			//添加小计 
//			number=new Number(3,n,DataUtil.doubleUtil(exportMap.get("ren_priceSubtotal")),ccellFormat_right) ;
//			sheet.addCell(number) ;
//			//添加小计 结束
//			for(int i=0;i<ren_priceList.size();i++){
//				number=new Number(i+4,n,DataUtil.doubleUtil(ren_priceList.get(i).get("REN_PRICE")),ccellFormat_right);
//				sheet.addCell(number);
//			}
//			sheet.setRowView(n,300,false); 
//			n=n+1;
						
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(2,n,"财务确认收入",format5); 
			sheet.addCell(cell);
			//添加小计 
			number=new Number(3,n,DataUtil.doubleUtil(exportMap.get("caiwuSubtotal")),ccellFormat_right) ;
			sheet.addCell(number) ;
			//添加小计 结束
			for(int i=0;i<caiwuList.size();i++){
				number=new Number(i+4,n,DataUtil.doubleUtil(caiwuList.get(i).get("CAIWU")),ccellFormat_right);
				sheet.addCell(number);
			}
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(2,n,"税务确认收入",format5); 
			sheet.addCell(cell);
			//添加小计 
			number=new Number(3,n,DataUtil.doubleUtil(exportMap.get("shuiwuSubtotal")),ccellFormat_right) ;
			sheet.addCell(number) ;
			//添加小计 结束
			for(int i=0;i<shuiwuList.size();i++){
				number=new Number(i+4,n,DataUtil.doubleUtil(shuiwuList.get(i).get("SHUIWU")),ccellFormat_right);
				sheet.addCell(number);
			}
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(2,n,"差异",format5); 
			sheet.addCell(cell);
			//添加小计 
			number=new Number(3,n,DataUtil.doubleUtil(exportMap.get("chayiSubtotal")),ccellFormat_right) ;
			sheet.addCell(number) ;
			//添加小计 结束
			for(int i=0;i<chayiList.size();i++){
				number=new Number(i+4,n,DataUtil.doubleUtil(chayiList.get(i).get("CHAYI")),ccellFormat_right);
				sheet.addCell(number);
			}
			sheet.setRowView(n,300,false); 
			n=n+1;

			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(1,n,"税务成本",format5); 
			sheet.mergeCells(1, n, 1, n+5);
			sheet.addCell(cell);
			cell=new Label(2,n,"营业税",format5); 
			sheet.addCell(cell);
			//添加小计 
			number=new Number(3,n,DataUtil.doubleUtil(exportMap.get("yingyeSubtotal")),ccellFormat_right) ;
			sheet.addCell(number) ;
			//添加小计 结束
			for(int i=0;i<yingyeList.size();i++){
				number=new Number(i+4,n,DataUtil.doubleUtil(yingyeList.get(i).get("YINGYE")),ccellFormat_right);
				sheet.addCell(number);
			}
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(2,n,"城建",format5); 
			sheet.addCell(cell);
			//添加小计 
			number=new Number(3,n,DataUtil.doubleUtil(exportMap.get("chengjianSubtotal")),ccellFormat_right) ;
			sheet.addCell(number) ;
			//添加小计 结束
			for(int i=0;i<chengjianList.size();i++){
				number=new Number(i+4,n,DataUtil.doubleUtil(chengjianList.get(i).get("CHENGJIAN")),ccellFormat_right);
				sheet.addCell(number);
			}
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(2,n,"地方教育费附加",format5); 
			sheet.addCell(cell);
			//添加小计 
			number=new Number(3,n,DataUtil.doubleUtil(exportMap.get("jiaoyuSubtotal")),ccellFormat_right) ;
			sheet.addCell(number) ;
			//添加小计 结束
			for(int i=0;i<jiaoyuList.size();i++){
				number=new Number(i+4,n,DataUtil.doubleUtil(jiaoyuList.get(i).get("JIAOYU")),ccellFormat_right);
				sheet.addCell(number);
			}
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(2,n,"保险费",format5); 
			sheet.addCell(cell);
			//添加小计 
			number=new Number(3,n,DataUtil.doubleUtil(exportMap.get("insure_priceSubtotal")),ccellFormat_right) ;
			sheet.addCell(number) ;
			//添加小计 结束
			for(int i=0;i<insure_priceList.size();i++){
				number=new Number(i+4,n,DataUtil.doubleUtil(insure_priceList.get(i).get("INSURE_PRICE")),ccellFormat_right);
				sheet.addCell(number);
			}
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(2,n,"印花税",format5); 
			sheet.addCell(cell);
			//添加小计 
			number=new Number(3,n,DataUtil.doubleUtil(exportMap.get("yinhuaSubtotal")),ccellFormat_right) ;
			sheet.addCell(number) ;
			//添加小计 结束
			for(int i=0;i<yinhuaList.size();i++){
				number=new Number(i+4,n,DataUtil.doubleUtil(yinhuaList.get(i).get("YINHUA")),ccellFormat_right);
				sheet.addCell(number);
			}
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(2,n,"小计",format5); 
			sheet.addCell(cell);
			//添加小计 
			number=new Number(3,n,DataUtil.doubleUtil(exportMap.get("xiaojiSubtotal")),ccellFormat_right) ;
			sheet.addCell(number) ;
			//添加小计 结束
			for(int i=0;i<xiaojiList.size();i++){
				number=new Number(i+4,n,DataUtil.doubleUtil(xiaojiList.get(i).get("XIAOJI")),ccellFormat_right);
				sheet.addCell(number);
			}
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(1,n,"主营业务利润",format5); 
			sheet.mergeCells(1, n, 1, n+2);
			sheet.addCell(cell);
			cell=new Label(2,n,"财务利润",format5); 
			sheet.addCell(cell);
			//添加小计 
			number=new Number(3,n,DataUtil.doubleUtil(exportMap.get("cliSubtotal")),ccellFormat_right) ;
			sheet.addCell(number) ;
			//添加小计 结束
			for(int i=0;i<cliList.size();i++){
				number=new Number(i+4,n,DataUtil.doubleUtil(cliList.get(i).get("CLI")),ccellFormat_right);
				sheet.addCell(number);
			}
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(2,n,"税务利润",format5); 
			sheet.addCell(cell);
			//添加小计 
			number=new Number(3,n,DataUtil.doubleUtil(exportMap.get("sliSubtotal")),ccellFormat_right) ;
			sheet.addCell(number) ;
			//添加小计 结束
			for(int i=0;i<xiaojiList.size();i++){
				number=new Number(i+4,n,DataUtil.doubleUtil(sliList.get(i).get("SLI")),ccellFormat_right);
				sheet.addCell(number);
			}
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(2,n,"差异",format5); 
			sheet.addCell(cell);
			//添加小计 
			number=new Number(3,n,DataUtil.doubleUtil(exportMap.get("licSubtotal")),ccellFormat_right) ;
			sheet.addCell(number) ;
			//添加小计 结束
			for(int i=0;i<licList.size();i++){
				number=new Number(i+4,n,DataUtil.doubleUtil(licList.get(i).get("LIC")),ccellFormat_right);
				sheet.addCell(number);
			}
			sheet.setRowView(n,300,false); 
			n=n+1;
					
			cell=new Label(0,1,"",format2_RIGHT);
			sheet.mergeCells(0, 1, 0, n-1);
			sheet.addCell(cell);
			cell=new Label(nianList.size()+4,1,"",format2_LEFT);
			sheet.mergeCells(nianList.size()+4, 1, nianList.size()+4, n-1);
			sheet.addCell(cell);
			cell=new Label(1,n,"",format2_Top);
			sheet.mergeCells(1, n, nianList.size()+3, n); 
			sheet.addCell(cell);
			n++ ;
//				//融资汇总上边框
//			cell = new Label(2,n,"",format2_BOTTOM) ;
//			sheet.mergeCells(2, n, 3, n) ;
//			sheet.addCell(cell) ;
//			n++ ;
//				//融资汇总左右边框
//			cell = new Label(1,n,"",format2_RIGHT) ;
//			sheet.mergeCells(1, n, 1, n+2) ;
//			sheet.addCell(cell) ;
//			cell = new Label(4,n,"",format2_LEFT) ;
//			sheet.mergeCells(4, n, 4, n+2) ;
//			sheet.addCell(cell) ;
//			//添加融资租赁合同总额
//			cell = new Label(2,n,"融资租赁合同总额",format5) ;
//			sheet.addCell(cell) ;
//			number=new Number(3,n,DataUtil.doubleUtil(0.0),ccellFormat_right) ;
//			sheet.addCell(number) ;
//			n++ ;
//			//添加设备总额
//			cell = new Label(2,n,"融资租赁设备款",format5) ;
//			sheet.addCell(cell) ;
//			number=new Number(3,n,DataUtil.doubleUtil(exportMap.get("shebeiTotal")),ccellFormat_right) ;
//			sheet.addCell(number) ;
//			n++ ;
//			//添加保险（年）
//			cell = new Label(2,n,"保险费（年）",format5) ;
//			sheet.addCell(cell) ;
//			number=new Number(3,n,DataUtil.doubleUtil(exportMap.get("insure_priceSubtotal")),ccellFormat_right) ;
//			sheet.addCell(number) ;
//			n++ ;
//			 //融资汇总下边框
//			cell = new Label(2,n,"",format2_Top) ;
//			sheet.mergeCells(2, n, 3, n) ;
//			sheet.addCell(cell) ;
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		return baos;
	}
	
	public ByteArrayOutputStream exportNotToAllotExcel(List<Map> notToAllotList,String totalCount,String totalAmount) {
		WritableSheet sheet = null;
		
		try {
			workbookSettings.setEncoding("ISO-8859-1");
			sheet = wb.createSheet("已核准未拨款表", 1);
			
			WritableFont font2 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
			WritableCellFormat format2_BOTTOM = new WritableCellFormat(font2);
			format2_BOTTOM.setAlignment(Alignment.CENTRE);
			format2_BOTTOM.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2_BOTTOM.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
			format2_BOTTOM.setWrap(true);
			
			WritableCellFormat format2_Top = new WritableCellFormat(font2);
			format2_Top.setAlignment(Alignment.CENTRE);
			format2_Top.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2_Top.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
			format2_Top.setWrap(true);
			
			WritableCellFormat format2_LEFT = new WritableCellFormat(font2);
			format2_LEFT.setAlignment(Alignment.CENTRE);
			format2_LEFT.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2_LEFT.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
			format2_LEFT.setWrap(true);
			
			WritableCellFormat format2_RIGHT = new WritableCellFormat(font2);
			format2_RIGHT.setAlignment(Alignment.CENTRE);
			format2_RIGHT.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2_RIGHT.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
			format2_RIGHT.setWrap(true);
			
			// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
			WritableFont font3 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
			WritableCellFormat format3 = new WritableCellFormat(font3);
			format3.setAlignment(Alignment.LEFT);
			format3.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format3.setWrap(true);
			// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
					 
			WritableCellFormat format5 = new WritableCellFormat(font3);
			format5.setAlignment(Alignment.CENTRE);
			format5.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format5.setWrap(true);
			WritableFont font6 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
			font6.setColour(Colour.BLUE);
			WritableCellFormat format6 = new WritableCellFormat(font6);
			format6.setAlignment(Alignment.LEFT); 
			format6.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format6.setWrap(true);
			
			
			WritableFont font8 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
			font8.setColour(Colour.BLUE);
			WritableCellFormat format8 = new WritableCellFormat(font8);
			format8.setAlignment(Alignment.LEFT); 
			format8.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format8.setWrap(true);
			
			
			WritableFont font9 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
			WritableCellFormat format9 = new WritableCellFormat(font9);
			format9.setAlignment(Alignment.LEFT);
			format9.setBackground(jxl.format.Colour.GRAY_25);
			format9.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format9.setWrap(true);
			
			
			WritableFont font4 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
			WritableCellFormat format4 = new WritableCellFormat(font4);
			format4.setAlignment(Alignment.CENTRE);
			format4.setBackground(jxl.format.Colour.GRAY_25);
			format4.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format4.setWrap(true);
			
						
			NumberFormat cnum=new NumberFormat("#,##0.00");
			NumberFormat pnum=new NumberFormat("##0.000%");
			NumberFormat fnum=new NumberFormat("##0.00");
			NumberFormat nnum=new NumberFormat("##"); 
			DateFormat   dateFormat=new DateFormat("yyyy-mm-dd");
			DateFormat   dateFormat2=new DateFormat("MMM-yy");
			WritableCellFormat dateCellFormat_center2=new WritableCellFormat(dateFormat2);
			dateCellFormat_center2.setAlignment(Alignment.CENTRE);
			WritableCellFormat ccellFormat_right=new WritableCellFormat(cnum);
			WritableCellFormat ccellFormat_center=new WritableCellFormat(cnum);
			WritableCellFormat pcellFormat_left=new WritableCellFormat(pnum);
			WritableCellFormat pcellFormat_right=new WritableCellFormat(pnum);
			WritableCellFormat pcellFormat_center=new WritableCellFormat(pnum);
			WritableCellFormat pcellFormat_center2=new WritableCellFormat(pnum);
			WritableCellFormat fcellFormat_left=new WritableCellFormat(fnum);
			WritableCellFormat ccellFormat_left=new WritableCellFormat(cnum);
			WritableCellFormat dateCellFormat_left=new WritableCellFormat(dateFormat);
			WritableCellFormat dateCellFormat_center=new WritableCellFormat(dateFormat);
			ccellFormat_left.setAlignment(Alignment.LEFT);
			ccellFormat_center.setAlignment(Alignment.CENTRE);
			ccellFormat_center.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
			
			pcellFormat_left.setAlignment(Alignment.LEFT);
			pcellFormat_right.setAlignment(Alignment.RIGHT);
			pcellFormat_center.setAlignment(Alignment.CENTRE);
			pcellFormat_center2.setAlignment(Alignment.CENTRE);
			pcellFormat_center2.setVerticalAlignment(VerticalAlignment.CENTRE);		
			fcellFormat_left.setAlignment(Alignment.LEFT);
			dateCellFormat_left.setAlignment(Alignment.LEFT);
			dateCellFormat_center.setAlignment(Alignment.CENTRE);
			WritableCellFormat ncellFormat_center=new WritableCellFormat(nnum);
			ncellFormat_center.setAlignment(Alignment.CENTRE);
			WritableCellFormat ncellFormat_left=new WritableCellFormat(nnum);
			ncellFormat_left.setAlignment(Alignment.LEFT);
			Number number=null;
			//1.0E8 －－100000000
			DecimalFormat   nf2=new   DecimalFormat("###################");	
			
			sheet.setColumnView(0, 4);
			sheet.setColumnView(1, 8);
			sheet.setColumnView(2, 20);
			sheet.setColumnView(3, 40);
			sheet.setColumnView(4, 40);
			sheet.setColumnView(5, 20);
			sheet.setColumnView(6, 20);
			sheet.setColumnView(7, 20);
			sheet.setColumnView(8, 20);
			sheet.setColumnView(9, 20);
			sheet.setColumnView(10, 20);
			sheet.setColumnView(11, 20);
			
			int n=0;
			Label cell = null;
			
			
			cell=new Label(0,0,"");  
			sheet.addCell(cell);
			cell=new Label(1,0,"",format2_BOTTOM);  
			sheet.mergeCells(1, 0, 11, 0);
			sheet.addCell(cell);
			n=n+1;
			sheet.setRowView(0,300,false); 
			
			cell=new Label(0,1,""); 
			sheet.addCell(cell);
			cell=new Label(1,1,"已核准未拨款表",format4); 
			sheet.mergeCells(1, 1, 11, 1); 
			sheet.addCell(cell);
			n=n+1;
			sheet.setRowView(1,320,false); 
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(1,n,"序号",format5); 
			sheet.addCell(cell); 
			cell=new Label(2,n,"合同编号",format5); 
			sheet.addCell(cell); 
			cell=new Label(3,n,"客户名称",format5); 
			sheet.addCell(cell); 
			cell=new Label(4,n,"供应商",format5); 
			sheet.addCell(cell); 
			cell=new Label(5,n,"经办人",format5); 
			sheet.addCell(cell); 
			cell=new Label(6,n,"主管",format5); 
			sheet.addCell(cell); 
			cell=new Label(7,n,"核准日期(a)",format5); 
			sheet.addCell(cell);
			cell=new Label(8,n,"有效期(b)",format5); 
			sheet.addCell(cell);
			cell=new Label(9,n,"有效天数(c=b-a)",format5); 
			sheet.addCell(cell);
			cell=new Label(10,n,"办事处",format5); 
			sheet.addCell(cell);
			cell=new Label(11,n,"金额",ccellFormat_center); 
			sheet.addCell(cell);
			sheet.setRowView(n,300,false); 
			n=n+1;


			int i = 0;
			for (Object object2 : notToAllotList) {
				Map realPrice =(Map)object2;
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
												
				i = i + 1;						
				number=new Number(1,n,i,ncellFormat_center);
				sheet.addCell(number);
				
				cell=new Label(2,n,DataUtil.StringUtil(realPrice.get("LEASE_CODE")),format5);  
				sheet.addCell(cell);
				cell=new Label(3,n,DataUtil.StringUtil(realPrice.get("CUST_NAME")),format5);  
				sheet.addCell(cell);
				cell=new Label(4,n,DataUtil.StringUtil(realPrice.get("BRAND")),format5);  
				sheet.addCell(cell);
				
				cell=new Label(5,n,DataUtil.StringUtil(realPrice.get("NAME")),format5);  
				sheet.addCell(cell);
				
				cell=new Label(6,n,DataUtil.StringUtil(realPrice.get("SUPERNAME")),format5);  
				sheet.addCell(cell);
				
				if(realPrice.get("WIND_RESULT_DATE")!=null) {
					SimpleDateFormat df2=new SimpleDateFormat("yyyy-MM-dd");
					Date start_date2=df2.parse(realPrice.get("WIND_RESULT_DATE")+"");
					DateTime dateLabel2=new DateTime(7,n,start_date2,dateCellFormat_center);  
					sheet.addCell(dateLabel2);
				} else {
					cell=new Label(7,n,""); 
					sheet.addCell(cell);
				}
				
				if(realPrice.get("WIND_RESULT_DATE")!=null) {
					SimpleDateFormat df2=new SimpleDateFormat("yyyy-MM-dd");
					Date start_date2=df2.parse(realPrice.get("EFFECTDATE")+"");
					DateTime dateLabel2=new DateTime(8,n,start_date2,dateCellFormat_center);  
					sheet.addCell(dateLabel2);
				} else {
					cell=new Label(8,n,""); 
					sheet.addCell(cell);
				}
				
				cell=new Label(9,n,DataUtil.StringUtil(realPrice.get("VALID_DAY")),format5);  
				sheet.addCell(cell);
				
				cell=new Label(10,n,DataUtil.StringUtil(realPrice.get("DECP_NAME_CN")),format5);  
				sheet.addCell(cell);
				
				if(realPrice.get("AMOUNT")!=null) {
					number=new Number(11,n,Double.parseDouble(realPrice.get("AMOUNT")+""),ccellFormat_center);
					sheet.addCell(number);
				} else {
					number=new Number(11,n,0,ccellFormat_center);
					sheet.addCell(number);
				}
//				if(realPrice.get("GRANT_PRICE")!=null){
//					number=new Number(5,n,Double.parseDouble(realPrice.get("GRANT_PRICE")+""),ccellFormat_right);
//					sheet.addCell(number);
//				}else{
//					number=new Number(5,n,0,ccellFormat_right);
//					sheet.addCell(number);
//				}
//				if(realPrice.get("wind_result_date")!=null){
//					SimpleDateFormat df2=new SimpleDateFormat("yyyy-MM-dd");
//					Date start_date2=df2.parse(realPrice.get("wind_result_date")+"");
//					DateTime dateLabel2=new DateTime(7,n, start_date2,dateCellFormat_center);
//					sheet.addCell(dateLabel2); 
//				}else{
//					cell=new Label(7,n,""); 
//					sheet.addCell(cell);
//				}
//				if(realPrice.get("PAY_MONEY")!=null){
//					number=new Number(7,n,Double.parseDouble(realPrice.get("PAY_MONEY")+""),ccellFormat_right);
//					sheet.addCell(number);
//				}else{
//					number=new Number(7,n,0,ccellFormat_right);
//					sheet.addCell(number);
//				}
//				Double pay_money = Double.parseDouble(realPrice.get("PAY_MONEY")==null?"0":realPrice.get("PAY_MONEY")+"");
//				Double payCount = Double.parseDouble(realPrice.get("PAYCOUNT")==null?"0":realPrice.get("PAYCOUNT")+"");
//				if(realPrice.get("APPRORIATEMON")!=null){
//					number=new Number(9,n,Double.parseDouble(realPrice.get("APPRORIATEMON")+""),ccellFormat_right);
//					sheet.addCell(number);
//				} else {
//					number=new Number(9,n,0,ccellFormat_right);
//					sheet.addCell(number);
//				}
				sheet.setRowView(n,300,false);  
				n=n+1;
				

			}
			
			cell=new Label(0,1,"",format2_RIGHT);
			sheet.mergeCells(0, 1, 0, n-1);
			sheet.addCell(cell);
			cell=new Label(10,1,"",format2_LEFT);
			sheet.mergeCells(11, 1, 11, n-1);
			sheet.addCell(cell);
			cell=new Label(1,n,"",format2_Top);
			sheet.mergeCells(1, n, 11, n); 
			sheet.addCell(cell);
			
			sheet.setRowView(n+1, 300, false);
			cell=new Label(11,n+1,"总数: "+totalCount+"    总金额: "+totalAmount,ccellFormat_right); 
			sheet.addCell(cell);
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		return baos;
	}

@SuppressWarnings("unchecked")
public ByteArrayOutputStream exportInterestExcel(Map exportMap) {

	WritableSheet sheet = null;
	
	try {
		workbookSettings.setEncoding("ISO-8859-1");
		//设置sheet名称  默认为成本差异
		if(exportMap.get("sheetName") != null){
			sheet = wb.createSheet(exportMap.get("sheetName").toString(), 1) ;
		} else {
			sheet = wb.createSheet("利息收入明细表", 1);
		}
		
		WritableFont font1=new WritableFont(WritableFont.createFont("宋体"),13,WritableFont.NO_BOLD);
		WritableCellFormat format1=new WritableCellFormat(font1);
		format1.setAlignment(Alignment.CENTRE);
		format1.setVerticalAlignment(VerticalAlignment.CENTRE);
		format1.setWrap(true);

		// 表格部分 字体 宋体10号 水平居左对齐 垂直居中对齐 带边框 自动换行
		WritableFont font3 = new WritableFont(
				WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
		WritableCellFormat format3 = new WritableCellFormat(font3);
		format3.setAlignment(Alignment.CENTRE);
		format3.setVerticalAlignment(VerticalAlignment.CENTRE);
		format3.setWrap(true);

		// 设置列宽
		sheet.setColumnView(0, 8);
		sheet.setColumnView(1, 15);
		sheet.setColumnView(2, 40);
		sheet.setColumnView(3, 40);
		sheet.setColumnView(4, 20);
		sheet.setColumnView(5, 15);
		sheet.setColumnView(6, 40);
		sheet.setColumnView(7, 22);
		sheet.setColumnView(8, 20);
		sheet.setColumnView(9, 20);
	
					
		NumberFormat cnum=new NumberFormat("#,##0.00");
		NumberFormat pnum=new NumberFormat("##0.000%");
		NumberFormat fnum=new NumberFormat("##0.00");
		NumberFormat nnum=new NumberFormat("##"); 
		DateFormat   dateFormat2=new DateFormat("MMM-yy");
		WritableCellFormat dateCellFormat_center2=new WritableCellFormat(dateFormat2);
		dateCellFormat_center2.setAlignment(Alignment.CENTRE);
		WritableCellFormat pcellFormat_left=new WritableCellFormat(pnum);
		WritableCellFormat pcellFormat_right=new WritableCellFormat(pnum);
		WritableCellFormat pcellFormat_center=new WritableCellFormat(pnum);
		WritableCellFormat pcellFormat_center2=new WritableCellFormat(pnum);
		WritableCellFormat fcellFormat_left=new WritableCellFormat(fnum);
		WritableCellFormat ccellFormat_left=new WritableCellFormat(cnum);
		WritableCellFormat ccellFormat_right=new WritableCellFormat(cnum);
		ccellFormat_left.setAlignment(Alignment.LEFT);
		pcellFormat_left.setAlignment(Alignment.LEFT);
		pcellFormat_right.setAlignment(Alignment.RIGHT);
		pcellFormat_center.setAlignment(Alignment.CENTRE);
		pcellFormat_center2.setAlignment(Alignment.CENTRE);
		pcellFormat_center2.setVerticalAlignment(VerticalAlignment.CENTRE);		
		fcellFormat_left.setAlignment(Alignment.LEFT);
		ccellFormat_right.setAlignment(Alignment.RIGHT);
		WritableCellFormat ncellFormat_center=new WritableCellFormat(nnum);
		ncellFormat_center.setAlignment(Alignment.CENTRE);
		WritableCellFormat ncellFormat_left=new WritableCellFormat(nnum);
		ncellFormat_left.setAlignment(Alignment.LEFT);
		Number number=null;
		//1.0E8 －－100000000
		DecimalFormat   nf2=new   DecimalFormat("###################");	
		
		Label cell = null;
		
		cell=new Label(0,0,"利息收入明细表",format1);
		sheet.addCell(cell);
		sheet.mergeCells(0,0,9,0);
		
		cell=new Label(0,1,"序号",format1);
		sheet.addCell(cell);
		cell=new Label(1,1,"期间",format1);
		sheet.addCell(cell);
		cell=new Label(2,1,"客户名称",format1);
		sheet.addCell(cell);
		cell=new Label(3,1,"供应商",format1);
		sheet.addCell(cell);
		cell=new Label(4,1,"合同号",format1);
		sheet.addCell(cell);
		cell=new Label(5,1,"业务员",format1);
		sheet.addCell(cell);
		cell=new Label(6,1,"办事处",format1);
		sheet.addCell(cell);
		cell=new Label(7,1,"财务确认利息收入",format1);
		sheet.addCell(cell);
		cell=new Label(8,1,"税务确认收入",format1);
		sheet.addCell(cell);
		cell=new Label(9,1,"差异额",format1);
		sheet.addCell(cell);
		
		List<Map> content=(List<Map>)exportMap.get("content");
		Iterator it=content.iterator();
		int i=2,j=1;
		while(it.hasNext()){
			Map map = (Map)it.next();
			cell=new Label(0,i,Integer.toString(j),format3);
			sheet.addCell(cell);
			
			cell=new Label(1,i,toString(map,"TIME"),format3);
			sheet.addCell(cell);

			cell=new Label(2,i,toString(map,"CUST_NAME"),format3);
			sheet.addCell(cell);
			cell=new Label(3,i,toString(map,"SUPL_NAME"),format3);
			sheet.addCell(cell);
			cell=new Label(4,i,toString(map,"LEASE_CODE"),format3);
			sheet.addCell(cell);
			cell=new Label(5,i,toString(map,"NAME"),format3);
			sheet.addCell(cell);
			cell=new Label(6,i,toString(map,"DECP_NAME_CN"),format3);
			sheet.addCell(cell);
			//Modify by Michael 2012 01/13 修改导出Excel 财务利息
			if(map.get("CAI")!=null){
				number=new Number(7,i,Double.parseDouble(map.get("CAI")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(7,i,0,ccellFormat_right);
				sheet.addCell(number);
			}
			if(map.get("SHUI")!=null){
				number=new Number(8,i,Double.parseDouble(map.get("SHUI")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(8,i,0,ccellFormat_right);
				sheet.addCell(number);
			}

			Double now = Double.parseDouble(map.get("CAI")==null?"0":map.get("CAI")+"");
			Double ren_price = Double.parseDouble(map.get("SHUI")==null?"0":map.get("SHUI")+"");
			number=new Number(9,i,ren_price-now,ccellFormat_right);
			sheet.addCell(number);
			i++;j++;
		}
	}catch(Exception e){
		e.printStackTrace();
		LogPrint.getLogStackTrace(e, logger);
	}
	return baos;
}
//保证金余额表
@SuppressWarnings("unchecked")
public ByteArrayOutputStream exportPledgeExcel(Map exportMap) {
	
	WritableSheet sheet = null;
	
	try {
		workbookSettings.setEncoding("ISO-8859-1");
		if(exportMap.get("sheetName") != null){
			sheet = wb.createSheet(exportMap.get("sheetName").toString(), 1) ;
		} else {
			sheet = wb.createSheet("保证金余额表", 1);
		}
		
		WritableFont font1=new WritableFont(WritableFont.createFont("宋体"),13,WritableFont.NO_BOLD);
		WritableCellFormat format1=new WritableCellFormat(font1);
		format1.setAlignment(Alignment.CENTRE);
		format1.setVerticalAlignment(VerticalAlignment.CENTRE);
		format1.setWrap(true);
		
		// 表格部分 字体 宋体10号 水平居左对齐 垂直居中对齐 带边框 自动换行
		WritableFont font3 = new WritableFont(
				WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
		WritableCellFormat format3 = new WritableCellFormat(font3);
		format3.setAlignment(Alignment.CENTRE);
		format3.setVerticalAlignment(VerticalAlignment.CENTRE);
		format3.setWrap(true);
		
		// 设置列宽
		sheet.setColumnView(0, 35);
		sheet.setColumnView(1, 20);
		sheet.setColumnView(2, 15);
		sheet.setColumnView(3, 20);
		sheet.setColumnView(4, 20);
		sheet.setColumnView(5, 20);
		sheet.setColumnView(6, 20);
		sheet.setColumnView(7, 30);
		
		
		NumberFormat cnum=new NumberFormat("#,##0.00");
		NumberFormat pnum=new NumberFormat("##0.000%");
		NumberFormat fnum=new NumberFormat("##0.00");
		NumberFormat nnum=new NumberFormat("##"); 
		DateFormat   dateFormat2=new DateFormat("MMM-yy");
		WritableCellFormat dateCellFormat_center2=new WritableCellFormat(dateFormat2);
		dateCellFormat_center2.setAlignment(Alignment.CENTRE);
		WritableCellFormat pcellFormat_left=new WritableCellFormat(pnum);
		WritableCellFormat pcellFormat_right=new WritableCellFormat(pnum);
		WritableCellFormat pcellFormat_center=new WritableCellFormat(pnum);
		WritableCellFormat pcellFormat_center2=new WritableCellFormat(pnum);
		WritableCellFormat fcellFormat_left=new WritableCellFormat(fnum);
		WritableCellFormat ccellFormat_left=new WritableCellFormat(cnum);
		WritableCellFormat ccellFormat_right=new WritableCellFormat(cnum);
		ccellFormat_left.setAlignment(Alignment.LEFT);
		pcellFormat_left.setAlignment(Alignment.LEFT);
		pcellFormat_right.setAlignment(Alignment.RIGHT);
		pcellFormat_center.setAlignment(Alignment.CENTRE);
		pcellFormat_center2.setAlignment(Alignment.CENTRE);
		pcellFormat_center2.setVerticalAlignment(VerticalAlignment.CENTRE);		
		fcellFormat_left.setAlignment(Alignment.LEFT);
		ccellFormat_right.setAlignment(Alignment.RIGHT);
		WritableCellFormat ncellFormat_center=new WritableCellFormat(nnum);
		ncellFormat_center.setAlignment(Alignment.CENTRE);
		WritableCellFormat ncellFormat_left=new WritableCellFormat(nnum);
		ncellFormat_left.setAlignment(Alignment.LEFT);
		Number number=null;
		//1.0E8 －－100000000
		DecimalFormat   nf2=new   DecimalFormat("###################");	
		
		Label cell = null;
		
		cell=new Label(0,0,"保证金余额表",format1);
		sheet.addCell(cell);
		sheet.mergeCells(0,0,7,0);
		
		cell=new Label(0,1,"客户",format1);
		sheet.addCell(cell);
		cell=new Label(1,1,"合同号",format1);
		sheet.addCell(cell);
		cell=new Label(2,1,"地区",format1);
		sheet.addCell(cell);
		cell=new Label(3,1,"保证金期初余额",format1);
		sheet.addCell(cell);
		cell=new Label(4,1,"保证金本期增加",format1);
		sheet.addCell(cell);
		cell=new Label(5,1,"保证金本期减少",format1);
		sheet.addCell(cell);
		cell=new Label(6,1,"保证金期末余额",format1);
		sheet.addCell(cell);
		cell=new Label(7,1,"保证金类别",format1);
		sheet.addCell(cell);
		
		List<Map> content=(List<Map>)exportMap.get("content");
		Iterator it=content.iterator();
		int i=2 ;
		while(it.hasNext()){
			Map map = (Map)it.next();
			cell=new Label(0,i,toString(map,"CUST_NAME"),format3);
			sheet.addCell(cell);
			
			cell=new Label(1,i,toString(map,"LEASE_CODE"),format3);
			sheet.addCell(cell);
			
			cell=new Label(2,i,toString(map,"AREA"),format3);
			sheet.addCell(cell);
			if(map.get("QICHU")!=null){
				number=new Number(3,i,Double.parseDouble(map.get("QICHU")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(3,i,0,ccellFormat_right);
				sheet.addCell(number);
			}
			if(map.get("PLEDGE_PRICE")!=null){
				number=new Number(4,i,Double.parseDouble(map.get("PLEDGE_PRICE")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(4,i,0,ccellFormat_right);
				sheet.addCell(number);
			}
			if(map.get("BENQIJIANSHAO")!=null){
				number=new Number(5,i,Double.parseDouble(map.get("BENQIJIANSHAO")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(5,i,0,ccellFormat_right);
				sheet.addCell(number);
			}
			
			if(map.get("QIMOYUE")!=null){
				number=new Number(6,i,Double.parseDouble(map.get("QIMOYUE")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(6,i,0,ccellFormat_right);
				sheet.addCell(number);
			}
			String pledgeType = "" ;
			if(map.get("PLEDGE_AVE_PRICE") != null && Double.parseDouble(map.get("PLEDGE_AVE_PRICE")+"") > 0.0){
				pledgeType += "平均冲抵  " ;
			}
			if(map.get("PLEDGE_BACK_PRICE") != null && Double.parseDouble(map.get("PLEDGE_BACK_PRICE")+"") > 0.0){
				pledgeType += "期末退回  " ;
			}
			if(map.get("PLEDGE_LAST_PRICE") != null && Double.parseDouble(map.get("PLEDGE_LAST_PRICE")+"") > 0.0){
				pledgeType += "期末冲抵" ;
			}
			cell=new Label(7,i,pledgeType,format3);
			sheet.addCell(cell);
			i++;
		}
	}catch(Exception e){
		e.printStackTrace();
		LogPrint.getLogStackTrace(e, logger);
	}
	return baos;
}

//Add by Michael 2012-3-22 保险费余额变动表
public ByteArrayOutputStream exportInsuranceDynamicExcel(List<Map> insuranceDynamic) {
		WritableSheet sheet = null;
		
		try {
			workbookSettings.setEncoding("ISO-8859-1");
			sheet = wb.createSheet("保险费余额变动表", 1);
			
			WritableFont font2 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
			WritableCellFormat format2_BOTTOM = new WritableCellFormat(font2);
			format2_BOTTOM.setAlignment(Alignment.CENTRE);
			format2_BOTTOM.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2_BOTTOM.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
			format2_BOTTOM.setWrap(true);
			
			WritableCellFormat format2_Top = new WritableCellFormat(font2);
			format2_Top.setAlignment(Alignment.CENTRE);
			format2_Top.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2_Top.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
			format2_Top.setWrap(true);
			
			WritableCellFormat format2_LEFT = new WritableCellFormat(font2);
			format2_LEFT.setAlignment(Alignment.CENTRE);
			format2_LEFT.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2_LEFT.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
			format2_LEFT.setWrap(true);
			
			WritableCellFormat format2_RIGHT = new WritableCellFormat(font2);
			format2_RIGHT.setAlignment(Alignment.CENTRE);
			format2_RIGHT.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2_RIGHT.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
			format2_RIGHT.setWrap(true);
			
			// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
			WritableFont font3 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
			WritableCellFormat format3 = new WritableCellFormat(font3);
			format3.setAlignment(Alignment.LEFT);
			format3.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format3.setWrap(true);
			// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
					 
			WritableCellFormat format5 = new WritableCellFormat(font3);
			format5.setAlignment(Alignment.CENTRE);
			format5.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format5.setWrap(true);
			WritableFont font6 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
			font6.setColour(Colour.BLUE);
			WritableCellFormat format6 = new WritableCellFormat(font6);
			format6.setAlignment(Alignment.LEFT); 
			format6.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format6.setWrap(true);
			
			
			WritableFont font8 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
			font8.setColour(Colour.BLUE);
			WritableCellFormat format8 = new WritableCellFormat(font8);
			format8.setAlignment(Alignment.LEFT); 
			format8.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format8.setWrap(true);
			
			
			WritableFont font9 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
			WritableCellFormat format9 = new WritableCellFormat(font9);
			format9.setAlignment(Alignment.LEFT);
			format9.setBackground(jxl.format.Colour.GRAY_25);
			format9.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format9.setWrap(true);
			
			
			WritableFont font4 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
			WritableCellFormat format4 = new WritableCellFormat(font4);
			format4.setAlignment(Alignment.CENTRE);
			format4.setBackground(jxl.format.Colour.GRAY_25);
			format4.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format4.setWrap(true);
			
						
			NumberFormat cnum=new NumberFormat("#,##0.0");
			NumberFormat pnum=new NumberFormat("##0.000%");
			NumberFormat fnum=new NumberFormat("##0.00");
			NumberFormat nnum=new NumberFormat("##"); 
			DateFormat   dateFormat=new DateFormat("yyyy-mm-dd");
			DateFormat   dateFormat2=new DateFormat("MMM-yy");
			WritableCellFormat dateCellFormat_center2=new WritableCellFormat(dateFormat2);
			dateCellFormat_center2.setAlignment(Alignment.CENTRE);
			WritableCellFormat ccellFormat_right=new WritableCellFormat(cnum);
			WritableCellFormat pcellFormat_left=new WritableCellFormat(pnum);
			WritableCellFormat pcellFormat_right=new WritableCellFormat(pnum);
			WritableCellFormat pcellFormat_center=new WritableCellFormat(pnum);
			WritableCellFormat pcellFormat_center2=new WritableCellFormat(pnum);
			WritableCellFormat fcellFormat_left=new WritableCellFormat(fnum);
			WritableCellFormat ccellFormat_left=new WritableCellFormat(cnum);
			WritableCellFormat dateCellFormat_left=new WritableCellFormat(dateFormat);
			WritableCellFormat dateCellFormat_center=new WritableCellFormat(dateFormat);
			ccellFormat_left.setAlignment(Alignment.LEFT);
			pcellFormat_left.setAlignment(Alignment.LEFT);
			pcellFormat_right.setAlignment(Alignment.RIGHT);
			pcellFormat_center.setAlignment(Alignment.CENTRE);
			pcellFormat_center2.setAlignment(Alignment.CENTRE);
			pcellFormat_center2.setVerticalAlignment(VerticalAlignment.CENTRE);		
			fcellFormat_left.setAlignment(Alignment.LEFT);
			dateCellFormat_left.setAlignment(Alignment.LEFT);
			dateCellFormat_center.setAlignment(Alignment.CENTRE);
			WritableCellFormat ncellFormat_center=new WritableCellFormat(nnum);
			ncellFormat_center.setAlignment(Alignment.CENTRE);
			WritableCellFormat ncellFormat_left=new WritableCellFormat(nnum);
			ncellFormat_left.setAlignment(Alignment.LEFT);
			Number number=null;
			//1.0E8 －－100000000
			DecimalFormat   nf2=new   DecimalFormat("###################");	
			
			sheet.setColumnView(0, 4);
			sheet.setColumnView(1, 15);
			sheet.setColumnView(2, 8);
			sheet.setColumnView(3,40);
			sheet.setColumnView(4,40);
			sheet.setColumnView(5, 20);
			sheet.setColumnView(6, 15);
			sheet.setColumnView(7, 40);
			sheet.setColumnView(8, 15);
			sheet.setColumnView(9, 15);
			sheet.setColumnView(10, 8);
			sheet.setColumnView(11, 15);
			sheet.setColumnView(12, 15);
			sheet.setColumnView(13, 15);
			sheet.setColumnView(14,15);
			sheet.setColumnView(15,15);
			sheet.setColumnView(16,15);
			sheet.setColumnView(17,4);
			
			int n=0;
			Label cell = null;
			
			
			cell=new Label(0,0,"");  
			sheet.addCell(cell);
			cell=new Label(1,0,"",format2_BOTTOM);  
			sheet.mergeCells(1, 0, 15, 0);
			sheet.addCell(cell);
			n=n+1;
			sheet.setRowView(0,300,false); 
			
			cell=new Label(0,1,""); 
			sheet.addCell(cell);
			cell=new Label(1,1,"保险费余额变动表",format4); 
			sheet.mergeCells(1, 1, 15, 1); 
			sheet.addCell(cell);
			n=n+1;
			sheet.setRowView(1,320,false); 
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(1,n,"支付期",format5); 
			sheet.mergeCells(1, n, 1, n); 
			sheet.addCell(cell); 
			cell=new Label(2,n,"序号",format5); 
			sheet.mergeCells(2, n, 2, n); 
			sheet.addCell(cell); 
			cell=new Label(3,n,"客户名称",format5); 
			sheet.mergeCells(3, n,3, n); 
			sheet.addCell(cell); 
			cell=new Label(4,n,"供应商",format5); 
			sheet.mergeCells(4, n,4, n); 
			sheet.addCell(cell); 
			cell=new Label(5,n,"合同号",format5); 
			sheet.mergeCells(5, n,5, n); 
			sheet.addCell(cell); 
			cell=new Label(6,n,"业务员",format5); 
			sheet.mergeCells(6, n,6, n); 
			sheet.addCell(cell); 
			cell=new Label(7,n,"办事处",format5); 
			sheet.mergeCells(7, n,7, n); 
			sheet.addCell(cell); 
			cell=new Label(8,n,"设备总价款",format5); 
			sheet.mergeCells(8, n,8, n); 
			sheet.addCell(cell); 
			cell=new Label(9,n,"起租日",format5); 
			sheet.mergeCells(9, n,9, n); 
			sheet.addCell(cell); 
			cell=new Label(10,n,"租赁期",format5); 
			sheet.mergeCells(10, n,10, n); 
			sheet.addCell(cell); 
			cell=new Label(11,n,"保费",format5); 
			sheet.mergeCells(11, n,11, n);
			sheet.addCell(cell); 
			cell=new Label(12,n,"期初余额",format5); 
			sheet.mergeCells(12, n,12, n); 
			sheet.addCell(cell); 
			cell=new Label(13,n,"本期新增",format5); 
			sheet.mergeCells(13, n,13, n); 
			sheet.addCell(cell); 
			
			cell=new Label(14,n,"本期减少",format5); 
			sheet.mergeCells(14, n,14, n); 
			sheet.addCell(cell); 
			
			cell=new Label(15,n,"期末余额",format5); 
			sheet.mergeCells(15, n,15, n); 
			sheet.addCell(cell); 
			
			sheet.setRowView(n,300,false); 
			n=n+1;

			int i = 0;
			for (Object object2 : insuranceDynamic) {
				Map insurance=(Map)object2;
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				
				if(insurance.get("FIRST_PAYDATE")!=null){
					SimpleDateFormat df2=new SimpleDateFormat("yyyy-MM-dd");
					Date start_date2=df2.parse(insurance.get("FIRST_PAYDATE")+"");
					DateTime dateLabel2=new DateTime(1,n, start_date2,dateCellFormat_center);
					sheet.addCell(dateLabel2); 
				}else{
					cell=new Label(1,n,""); 
					sheet.addCell(cell);
				}
								
				i = i + 1;						
				number=new Number(2,n,i,ncellFormat_center);
				sheet.addCell(number);
				
				cell=new Label(3,n,DataUtil.StringUtil(insurance.get("CUST_NAME")+""),format5);
				sheet.addCell(cell);
				cell=new Label(4,n,DataUtil.StringUtil(insurance.get("SUPL_NAME")+""),format5);
				sheet.addCell(cell);
				cell=new Label(5,n,DataUtil.StringUtil(insurance.get("RECP_CODE")+""),format5);
				sheet.addCell(cell);
				cell=new Label(6,n,DataUtil.StringUtil(insurance.get("NAME")+""),format5);
				sheet.addCell(cell);
				cell=new Label(7,n,DataUtil.StringUtil(insurance.get("DECP_NAME_CN")),format5);  
				sheet.addCell(cell);
				number=new Number(8,n,Double.parseDouble(insurance.get("LEASE_TOPRIC")+""),ccellFormat_right);
				sheet.addCell(number);
				
				if(insurance.get("FIRST_PAYDATE")!=null){
					SimpleDateFormat df2=new SimpleDateFormat("yyyy-MM-dd");
					Date start_date2=df2.parse(insurance.get("FIRST_PAYDATE")+"");
					DateTime dateLabel2=new DateTime(9,n, start_date2,dateCellFormat_center);
					sheet.addCell(dateLabel2); 
				}else{
					cell=new Label(9,n,""); 
					sheet.addCell(cell);
				}
				cell=new Label(10,n,DataUtil.StringUtil(insurance.get("LEASE_PERIOD")),format5);  
				sheet.addCell(cell);
				
				number=new Number(11,n,Double.parseDouble(insurance.get("INSURANCE")+""),ccellFormat_right);
				sheet.addCell(number);
				number=new Number(12,n,Double.parseDouble(insurance.get("QCYE")+""),ccellFormat_right);
				sheet.addCell(number);
				number=new Number(13,n,Double.parseDouble(insurance.get("BQXZ")+""),ccellFormat_right);
				sheet.addCell(number);
				number=new Number(14,n,Double.parseDouble(insurance.get("MONTHINSURANCE")+""),ccellFormat_right);
				sheet.addCell(number);				
				number=new Number(15,n,Double.parseDouble(insurance.get("QMYE")+""),ccellFormat_right);
				sheet.addCell(number);					
				
				sheet.setRowView(n,300,false);  
				n=n+1;
			}
			 			
			cell=new Label(0,1,"",format2_RIGHT);
			sheet.mergeCells(0, 1, 0, n-1);
			sheet.addCell(cell);
			cell=new Label(16,1,"",format2_LEFT);
			sheet.mergeCells(16, 1, 16, n-1);
			sheet.addCell(cell);
			cell=new Label(1,n,"",format2_Top);
			sheet.mergeCells(1, n, 16, n); 
			sheet.addCell(cell);
					
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		return baos;
	}

public ByteArrayOutputStream stayBuyPriceTaxToExcel(List<Map> businessTaxList) {
	WritableSheet sheet = null;
	
	try {
		workbookSettings.setEncoding("ISO-8859-1");
		sheet = wb.createSheet("留购款营业税明细表", 1);
		
		WritableFont font2 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
		WritableCellFormat format2_BOTTOM = new WritableCellFormat(font2);
		format2_BOTTOM.setAlignment(Alignment.CENTRE);
		format2_BOTTOM.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_BOTTOM.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
		format2_BOTTOM.setWrap(true);
		
		WritableCellFormat format2_Top = new WritableCellFormat(font2);
		format2_Top.setAlignment(Alignment.CENTRE);
		format2_Top.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_Top.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
		format2_Top.setWrap(true);
		
		WritableCellFormat format2_LEFT = new WritableCellFormat(font2);
		format2_LEFT.setAlignment(Alignment.CENTRE);
		format2_LEFT.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_LEFT.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
		format2_LEFT.setWrap(true);
		
		WritableCellFormat format2_RIGHT = new WritableCellFormat(font2);
		format2_RIGHT.setAlignment(Alignment.CENTRE);
		format2_RIGHT.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_RIGHT.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
		format2_RIGHT.setWrap(true);
		
		// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
		WritableFont font3 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
		WritableCellFormat format3 = new WritableCellFormat(font3);
		format3.setAlignment(Alignment.LEFT);
		format3.setVerticalAlignment(VerticalAlignment.CENTRE);
		// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
		format3.setWrap(true);
		// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
				 
		WritableCellFormat format5 = new WritableCellFormat(font3);
		format5.setAlignment(Alignment.CENTRE);
		format5.setVerticalAlignment(VerticalAlignment.CENTRE);
		format5.setWrap(true);
		WritableFont font6 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
		font6.setColour(Colour.BLUE);
		WritableCellFormat format6 = new WritableCellFormat(font6);
		format6.setAlignment(Alignment.LEFT); 
		format6.setVerticalAlignment(VerticalAlignment.CENTRE);
		format6.setWrap(true);
		
		WritableFont font8 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
		font8.setColour(Colour.BLUE);
		WritableCellFormat format8 = new WritableCellFormat(font8);
		format8.setAlignment(Alignment.LEFT); 
		format8.setVerticalAlignment(VerticalAlignment.CENTRE);
		format8.setWrap(true);
		
		WritableFont font9 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
		WritableCellFormat format9 = new WritableCellFormat(font9);
		format9.setAlignment(Alignment.LEFT);
		format9.setBackground(jxl.format.Colour.GRAY_25);
		format9.setVerticalAlignment(VerticalAlignment.CENTRE);
		format9.setWrap(true);
		
		WritableFont font4 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
		WritableCellFormat format4 = new WritableCellFormat(font4);
		format4.setAlignment(Alignment.CENTRE);
		format4.setBackground(jxl.format.Colour.GRAY_25);
		format4.setVerticalAlignment(VerticalAlignment.CENTRE);
		format4.setWrap(true);
					
		NumberFormat cnum=new NumberFormat("#,##0.00");
		NumberFormat pnum=new NumberFormat("##0.000%");
		NumberFormat fnum=new NumberFormat("##0.00");
		NumberFormat nnum=new NumberFormat("##"); 
		DateFormat   dateFormat=new DateFormat("yyyy-mm-dd");
		DateFormat   dateFormat2=new DateFormat("MMM-yy");
		WritableCellFormat dateCellFormat_center2=new WritableCellFormat(dateFormat2);
		dateCellFormat_center2.setAlignment(Alignment.CENTRE);
		WritableCellFormat ccellFormat_right=new WritableCellFormat(cnum);
		WritableCellFormat pcellFormat_left=new WritableCellFormat(pnum);
		WritableCellFormat pcellFormat_right=new WritableCellFormat(pnum);
		WritableCellFormat pcellFormat_center=new WritableCellFormat(pnum);
		WritableCellFormat pcellFormat_center2=new WritableCellFormat(pnum);
		WritableCellFormat fcellFormat_left=new WritableCellFormat(fnum);
		WritableCellFormat ccellFormat_left=new WritableCellFormat(cnum);
		WritableCellFormat dateCellFormat_left=new WritableCellFormat(dateFormat);
		WritableCellFormat dateCellFormat_center=new WritableCellFormat(dateFormat);
		ccellFormat_left.setAlignment(Alignment.LEFT);
		pcellFormat_left.setAlignment(Alignment.LEFT);
		pcellFormat_right.setAlignment(Alignment.RIGHT);
		pcellFormat_center.setAlignment(Alignment.CENTRE);
		pcellFormat_center2.setAlignment(Alignment.CENTRE);
		pcellFormat_center2.setVerticalAlignment(VerticalAlignment.CENTRE);		
		fcellFormat_left.setAlignment(Alignment.LEFT);
		dateCellFormat_left.setAlignment(Alignment.LEFT);
		dateCellFormat_center.setAlignment(Alignment.CENTRE);
		WritableCellFormat ncellFormat_center=new WritableCellFormat(nnum);
		ncellFormat_center.setAlignment(Alignment.CENTRE);
		WritableCellFormat ncellFormat_left=new WritableCellFormat(nnum);
		ncellFormat_left.setAlignment(Alignment.LEFT);
		Number number=null;
		//1.0E8 －－100000000
		DecimalFormat   nf2=new   DecimalFormat("###################");	
		
		sheet.setColumnView(0, 4);
		sheet.setColumnView(1, 8);
		sheet.setColumnView(2, 40);
		sheet.setColumnView(3, 40);
		sheet.setColumnView(4, 20);
		sheet.setColumnView(5, 10);
		sheet.setColumnView(6, 40);
		sheet.setColumnView(7, 15);
		sheet.setColumnView(8, 15);
		sheet.setColumnView(9, 15);
		sheet.setColumnView(10, 15);
		sheet.setColumnView(11, 15);
		sheet.setColumnView(12, 15);
		sheet.setColumnView(13,20);
		sheet.setColumnView(14,8);
		sheet.setColumnView(15,25);
		sheet.setColumnView(16,4);
		
		int n=0;
		Label cell = null;
		
		
		cell=new Label(0,0,"");  
		sheet.addCell(cell);
		cell=new Label(1,0,"",format2_BOTTOM);  
		sheet.mergeCells(1, 0,15, 0);
		sheet.addCell(cell);
		n=n+1;
		sheet.setRowView(0,300,false); 
		
		cell=new Label(0,1,""); 
		sheet.addCell(cell);
		cell=new Label(1,1,"留购款、罚息营业税明细表",format4); 
		sheet.mergeCells(1, 1, 15, 1); 
		sheet.addCell(cell);
		n=n+1;
		sheet.setRowView(1,320,false); 
		
		cell=new Label(0,n,""); 
		sheet.addCell(cell);
		cell=new Label(1,n,"序号",format5); 
		sheet.mergeCells(1, n, 1, n+1); 
		sheet.addCell(cell); 
		cell=new Label(2,n,"客户名称",format5); 
		sheet.mergeCells(2, n, 2, n+1); 
		sheet.addCell(cell); 
		cell=new Label(3,n,"供应商",format5); 
		sheet.mergeCells(3, n, 3, n+1); 
		sheet.addCell(cell); 
		cell=new Label(4,n,"合同号",format5); 
		sheet.mergeCells(4, n, 4, n+1); 
		sheet.addCell(cell); 
		cell=new Label(5,n,"业务员",format5); 
		sheet.mergeCells(5, n, 5, n+1); 
		sheet.addCell(cell); 
		cell=new Label(6,n,"办事处",format5); 
		sheet.mergeCells(6, n, 6, n+1); 
		sheet.addCell(cell); 
		cell=new Label(7,n,"分解项目",format5); 
		sheet.mergeCells(7, n, 7, n+1); 
		sheet.addCell(cell); 
		cell=new Label(8,n,"营业税",format5); 
		sheet.mergeCells(8, n,9, n); 
		sheet.addCell(cell); 
		cell=new Label(10,n,"城建税",format5); 
		sheet.mergeCells(10, n,11, n); 
		sheet.addCell(cell); 
		cell=new Label(12,n,"地区教育费附加",format5); 
		sheet.mergeCells(12, n,13, n); 
		sheet.addCell(cell); 
		cell=new Label(14,n,"小计",format5); 
		sheet.mergeCells(14, n,14, n+1); 
		sheet.addCell(cell); 
		cell=new Label(15,n,"税务登记号",format5); 
		sheet.mergeCells(15, n,15, n+1); 
		sheet.addCell(cell); 
		sheet.setRowView(n,300,false); 
		n=n+1;
		
		cell=new Label(0,n,""); 
		sheet.addCell(cell);
		cell=new Label(7,n,"计税依据",format5); 
		sheet.addCell(cell);
		cell=new Label(8,n,"税额",format5); 
		sheet.addCell(cell);
		cell=new Label(9,n,"计税依据",format5); 
		sheet.addCell(cell);
		cell=new Label(10,n,"税额",format5); 
		sheet.addCell(cell);
		cell=new Label(11,n,"计税依据",format5); 
		sheet.addCell(cell);
		cell=new Label(12,n,"税额",format5); 
		sheet.addCell(cell);
		n=n+1;
		
		
		double REN_PRICE_sum=0;
		double YYSHUIE_sum=0;
		double CJSHUIE_sum=0;
		double JYSHUIE_sum=0;
		double XIAOJI_sum=0;

		int i = 0;
		for (Object object2 : businessTaxList) {
			Map businessTax =(Map)object2;
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
											
			i = i + 1;						
			number=new Number(1,n,i,ncellFormat_center);
			sheet.addCell(number);
			
			cell=new Label(2,n,DataUtil.StringUtil(businessTax.get("CUST_NAME")),format5);  
			sheet.addCell(cell);
			cell=new Label(3,n,DataUtil.StringUtil(businessTax.get("SUPL_NAME")),format5);  
			sheet.addCell(cell);
			cell=new Label(4,n,DataUtil.StringUtil(businessTax.get("LEASE_CODE")),format5);  
			sheet.addCell(cell);
			cell=new Label(5,n,DataUtil.StringUtil(businessTax.get("NAME")),format5);  
			sheet.addCell(cell);
			cell=new Label(6,n,DataUtil.StringUtil(businessTax.get("DECP_NAME_CN")),format5);  
			sheet.addCell(cell);
			
			cell=new Label(7,n,DataUtil.StringUtil(businessTax.get("FICB_ITEM")),format5);  
			sheet.addCell(cell);			
					
			if(businessTax.get("REAL_PRICE")!=null){
				number=new Number(8,n,Double.parseDouble(businessTax.get("REAL_PRICE")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(8,n,0,ccellFormat_right);
				sheet.addCell(number);
			}
			if(businessTax.get("YYSHUIE")!=null){
				number=new Number(9,n,Double.parseDouble(businessTax.get("YYSHUIE")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(9,n,0,ccellFormat_right);
				sheet.addCell(number);
			}
			if(businessTax.get("YYSHUIE")!=null){
				number=new Number(10,n,Double.parseDouble(businessTax.get("YYSHUIE")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(10,n,0,ccellFormat_right);
				sheet.addCell(number);
			}
			if(businessTax.get("CJSHUIE")!=null){
				number=new Number(11,n,Double.parseDouble(businessTax.get("CJSHUIE")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(11,n,0,ccellFormat_right);
				sheet.addCell(number);
			}
			if(businessTax.get("YYSHUIE")!=null){
				number=new Number(12,n,Double.parseDouble(businessTax.get("YYSHUIE")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(12,n,0,ccellFormat_right);
				sheet.addCell(number);
			}
			if(businessTax.get("JYSHUIE")!=null){
				number=new Number(13,n,Double.parseDouble(businessTax.get("JYSHUIE")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(13,n,0,ccellFormat_right);
				sheet.addCell(number);
			}
			if(businessTax.get("XIAOJI")!=null){
				number=new Number(14,n,Double.parseDouble(businessTax.get("XIAOJI")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(14,n,0,ccellFormat_right);
				sheet.addCell(number);
			}

			cell=new Label(15,n,DataUtil.StringUtil(businessTax.get("CORP_TAX_CODE")),format5);  
			sheet.addCell(cell);
			sheet.setRowView(n,300,false);  
			n=n+1;
			
			REN_PRICE_sum=REN_PRICE_sum+DataUtil.doubleUtil(businessTax.get("REAL_PRICE"));
			YYSHUIE_sum=YYSHUIE_sum+DataUtil.doubleUtil(businessTax.get("YYSHUIE"));
			CJSHUIE_sum=CJSHUIE_sum+DataUtil.doubleUtil(businessTax.get("CJSHUIE"));
			JYSHUIE_sum=JYSHUIE_sum+DataUtil.doubleUtil(businessTax.get("JYSHUIE"));
			XIAOJI_sum=XIAOJI_sum+DataUtil.doubleUtil(businessTax.get("XIAOJI"));
		}
		 
		cell=new Label(0,n,""); 
		sheet.addCell(cell);
		cell=new Label(1,n,"小计",format5);
		sheet.mergeCells(1, n, 2, n);
		sheet.addCell(cell);
		
		number=new Number(8,n,REN_PRICE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(9,n,YYSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(10,n,YYSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(11,n,CJSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(12,n,YYSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(13,n,JYSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(14,n,XIAOJI_sum,ccellFormat_right);
		sheet.addCell(number);

		sheet.setRowView(n,300,false);  
		n=n+1;
		
		
		cell=new Label(0,n,""); 
		sheet.addCell(cell);
		cell=new Label(1,n,"税务申报",format5);
		sheet.mergeCells(1, n, 2, n);
		sheet.addCell(cell);
		number=new Number(8,n,REN_PRICE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(9,n,YYSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(10,n,YYSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(11,n,CJSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(12,n,YYSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(13,n,JYSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(14,n,XIAOJI_sum,ccellFormat_right);
		sheet.addCell(number);

		sheet.setRowView(n,300,false);  
		n=n+1;

			
		cell=new Label(0,1,"",format2_RIGHT);
		sheet.mergeCells(0, 1, 0, n-1);
		sheet.addCell(cell);
		cell=new Label(16,1,"",format2_LEFT);
		sheet.mergeCells(16, 1, 16, n-1);
		sheet.addCell(cell);
		cell=new Label(1,n,"",format2_Top);
		sheet.mergeCells(1, n, 15, n); 
		sheet.addCell(cell);
				
	}catch(Exception e){
		e.printStackTrace();
		LogPrint.getLogStackTrace(e, logger);
	}
	return baos;
}

public ByteArrayOutputStream stayBuyPriceTaxToExcelValueAdd(List<Map> businessTaxList) {
	WritableSheet sheet = null;
	
	try {
		workbookSettings.setEncoding("ISO-8859-1");
		sheet = wb.createSheet("留购款增值税明细表", 1);
		
		WritableFont font2 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
		WritableCellFormat format2_BOTTOM = new WritableCellFormat(font2);
		format2_BOTTOM.setAlignment(Alignment.CENTRE);
		format2_BOTTOM.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_BOTTOM.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
		format2_BOTTOM.setWrap(true);
		
		WritableCellFormat format2_Top = new WritableCellFormat(font2);
		format2_Top.setAlignment(Alignment.CENTRE);
		format2_Top.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_Top.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
		format2_Top.setWrap(true);
		
		WritableCellFormat format2_LEFT = new WritableCellFormat(font2);
		format2_LEFT.setAlignment(Alignment.CENTRE);
		format2_LEFT.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_LEFT.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
		format2_LEFT.setWrap(true);
		
		WritableCellFormat format2_RIGHT = new WritableCellFormat(font2);
		format2_RIGHT.setAlignment(Alignment.CENTRE);
		format2_RIGHT.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_RIGHT.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
		format2_RIGHT.setWrap(true);
		
		// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
		WritableFont font3 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
		WritableCellFormat format3 = new WritableCellFormat(font3);
		format3.setAlignment(Alignment.LEFT);
		format3.setVerticalAlignment(VerticalAlignment.CENTRE);
		// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
		format3.setWrap(true);
		// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
				 
		WritableCellFormat format5 = new WritableCellFormat(font3);
		format5.setAlignment(Alignment.CENTRE);
		format5.setVerticalAlignment(VerticalAlignment.CENTRE);
		format5.setWrap(true);
		WritableFont font6 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
		font6.setColour(Colour.BLUE);
		WritableCellFormat format6 = new WritableCellFormat(font6);
		format6.setAlignment(Alignment.LEFT); 
		format6.setVerticalAlignment(VerticalAlignment.CENTRE);
		format6.setWrap(true);
		
		WritableFont font8 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
		font8.setColour(Colour.BLUE);
		WritableCellFormat format8 = new WritableCellFormat(font8);
		format8.setAlignment(Alignment.LEFT); 
		format8.setVerticalAlignment(VerticalAlignment.CENTRE);
		format8.setWrap(true);
		
		WritableFont font9 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
		WritableCellFormat format9 = new WritableCellFormat(font9);
		format9.setAlignment(Alignment.LEFT);
		format9.setBackground(jxl.format.Colour.GRAY_25);
		format9.setVerticalAlignment(VerticalAlignment.CENTRE);
		format9.setWrap(true);
		
		WritableFont font4 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
		WritableCellFormat format4 = new WritableCellFormat(font4);
		format4.setAlignment(Alignment.CENTRE);
		format4.setBackground(jxl.format.Colour.GRAY_25);
		format4.setVerticalAlignment(VerticalAlignment.CENTRE);
		format4.setWrap(true);
					
		NumberFormat cnum=new NumberFormat("#,##0.00");
		NumberFormat pnum=new NumberFormat("##0.000%");
		NumberFormat fnum=new NumberFormat("##0.00");
		NumberFormat nnum=new NumberFormat("##"); 
		DateFormat   dateFormat=new DateFormat("yyyy-mm-dd");
		DateFormat   dateFormat2=new DateFormat("MMM-yy");
		WritableCellFormat dateCellFormat_center2=new WritableCellFormat(dateFormat2);
		dateCellFormat_center2.setAlignment(Alignment.CENTRE);
		WritableCellFormat ccellFormat_right=new WritableCellFormat(cnum);
		WritableCellFormat pcellFormat_left=new WritableCellFormat(pnum);
		WritableCellFormat pcellFormat_right=new WritableCellFormat(pnum);
		WritableCellFormat pcellFormat_center=new WritableCellFormat(pnum);
		WritableCellFormat pcellFormat_center2=new WritableCellFormat(pnum);
		WritableCellFormat fcellFormat_left=new WritableCellFormat(fnum);
		WritableCellFormat ccellFormat_left=new WritableCellFormat(cnum);
		WritableCellFormat dateCellFormat_left=new WritableCellFormat(dateFormat);
		WritableCellFormat dateCellFormat_center=new WritableCellFormat(dateFormat);
		ccellFormat_left.setAlignment(Alignment.LEFT);
		pcellFormat_left.setAlignment(Alignment.LEFT);
		pcellFormat_right.setAlignment(Alignment.RIGHT);
		pcellFormat_center.setAlignment(Alignment.CENTRE);
		pcellFormat_center2.setAlignment(Alignment.CENTRE);
		pcellFormat_center2.setVerticalAlignment(VerticalAlignment.CENTRE);		
		fcellFormat_left.setAlignment(Alignment.LEFT);
		dateCellFormat_left.setAlignment(Alignment.LEFT);
		dateCellFormat_center.setAlignment(Alignment.CENTRE);
		WritableCellFormat ncellFormat_center=new WritableCellFormat(nnum);
		ncellFormat_center.setAlignment(Alignment.CENTRE);
		WritableCellFormat ncellFormat_left=new WritableCellFormat(nnum);
		ncellFormat_left.setAlignment(Alignment.LEFT);
		Number number=null;
		//1.0E8 －－100000000
		DecimalFormat   nf2=new   DecimalFormat("###################");	
		
		sheet.setColumnView(0, 4);
		sheet.setColumnView(1, 8);
		sheet.setColumnView(2, 40);
		sheet.setColumnView(3, 40);
		sheet.setColumnView(4, 20);
		sheet.setColumnView(5, 10);
		sheet.setColumnView(6, 40);
		sheet.setColumnView(7, 15);
		sheet.setColumnView(8, 15);
		sheet.setColumnView(9, 15);
		sheet.setColumnView(10, 15);
		sheet.setColumnView(11, 15);
		sheet.setColumnView(12, 15);
		sheet.setColumnView(13,20);
		sheet.setColumnView(14,8);
		sheet.setColumnView(15,25);
		sheet.setColumnView(16,100);
		sheet.setColumnView(17,100);
		
		int n=0;
		Label cell = null;
		
		
		cell=new Label(0,0,"");  
		sheet.addCell(cell);
		cell=new Label(1,0,"",format2_BOTTOM);  
		sheet.mergeCells(1, 0,17, 0);
		sheet.addCell(cell);
		n=n+1;
		sheet.setRowView(0,400,false); 
		
		cell=new Label(0,1,""); 
		sheet.addCell(cell);
		cell=new Label(1,1,"留购款、罚息增值税明细表",format4); 
		sheet.mergeCells(1, 1, 17, 1); 
		sheet.addCell(cell);
		n=n+1;
		sheet.setRowView(1,320,false); 
		
		cell=new Label(0,n,""); 
		sheet.addCell(cell);
		cell=new Label(1,n,"序号",format5); 
		sheet.mergeCells(1, n, 1, n+1); 
		sheet.addCell(cell); 
		cell=new Label(2,n,"客户名称",format5); 
		sheet.mergeCells(2, n, 2, n+1); 
		sheet.addCell(cell); 
		cell=new Label(3,n,"供应商",format5); 
		sheet.mergeCells(3, n, 3, n+1); 
		sheet.addCell(cell); 
		cell=new Label(4,n,"合同号",format5); 
		sheet.mergeCells(4, n, 4, n+1); 
		sheet.addCell(cell); 
		cell=new Label(5,n,"业务员",format5); 
		sheet.mergeCells(5, n, 5, n+1); 
		sheet.addCell(cell); 
		cell=new Label(6,n,"办事处",format5); 
		sheet.mergeCells(6, n, 6, n+1); 
		sheet.addCell(cell); 
		cell=new Label(7,n,"分解项目",format5); 
		sheet.mergeCells(7, n, 7, n+1); 
		sheet.addCell(cell); 
		cell=new Label(8,n,"增值税",format5); 
		sheet.mergeCells(8, n,9, n); 
		sheet.addCell(cell); 
		cell=new Label(10,n,"城建税",format5); 
		sheet.mergeCells(10, n,11, n); 
		sheet.addCell(cell); 
		cell=new Label(12,n,"地区教育费附加",format5); 
		sheet.mergeCells(12, n,13, n); 
		sheet.addCell(cell); 
		cell=new Label(14,n,"小计",format5); 
		sheet.mergeCells(14, n,14, n+1); 
		sheet.addCell(cell); 
		cell=new Label(15,n,"税务登记号",format5); 
		sheet.mergeCells(15, n,15, n+1); 
		sheet.addCell(cell);
		cell=new Label(16,n,"地址电话",format5); 
		sheet.mergeCells(16, n,16, n+1); 
		sheet.addCell(cell);
		cell=new Label(17,n,"开户行帐号",format5); 
		sheet.mergeCells(17, n,17, n+1); 
		sheet.addCell(cell);
		sheet.setRowView(n,300,false); 
		n=n+1;
		
		cell=new Label(0,n,""); 
		sheet.addCell(cell);
		cell=new Label(7,n,"计税依据",format5); 
		sheet.addCell(cell);
		cell=new Label(8,n,"税额",format5); 
		sheet.addCell(cell);
		cell=new Label(9,n,"计税依据",format5); 
		sheet.addCell(cell);
		cell=new Label(10,n,"税额",format5); 
		sheet.addCell(cell);
		cell=new Label(11,n,"计税依据",format5); 
		sheet.addCell(cell);
		cell=new Label(12,n,"税额",format5); 
		sheet.addCell(cell);
		n=n+1;
		
		
		double REN_PRICE_sum=0;
		double YYSHUIE_sum=0;
		double CJSHUIE_sum=0;
		double JYSHUIE_sum=0;
		double XIAOJI_sum=0;

		int i = 0;
		for (Object object2 : businessTaxList) {
			Map businessTax =(Map)object2;
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
											
			i = i + 1;						
			number=new Number(1,n,i,ncellFormat_center);
			sheet.addCell(number);
			
			cell=new Label(2,n,DataUtil.StringUtil(businessTax.get("CUST_NAME")),format5);  
			sheet.addCell(cell);
			cell=new Label(3,n,DataUtil.StringUtil(businessTax.get("SUPL_NAME")),format5);  
			sheet.addCell(cell);
			cell=new Label(4,n,DataUtil.StringUtil(businessTax.get("LEASE_CODE")),format5);  
			sheet.addCell(cell);
			cell=new Label(5,n,DataUtil.StringUtil(businessTax.get("NAME")),format5);  
			sheet.addCell(cell);
			cell=new Label(6,n,DataUtil.StringUtil(businessTax.get("DECP_NAME_CN")),format5);  
			sheet.addCell(cell);
			
			cell=new Label(7,n,DataUtil.StringUtil(businessTax.get("FICB_ITEM")),format5);  
			sheet.addCell(cell);			
					
			if(businessTax.get("REAL_PRICE")!=null){
				number=new Number(8,n,Double.parseDouble(businessTax.get("REAL_PRICE")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(8,n,0,ccellFormat_right);
				sheet.addCell(number);
			}
			if(businessTax.get("YYSHUIE")!=null){
				number=new Number(9,n,Double.parseDouble(businessTax.get("YYSHUIE")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(9,n,0,ccellFormat_right);
				sheet.addCell(number);
			}
			if(businessTax.get("YYSHUIE")!=null){
				number=new Number(10,n,Double.parseDouble(businessTax.get("YYSHUIE")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(10,n,0,ccellFormat_right);
				sheet.addCell(number);
			}
			if(businessTax.get("CJSHUIE")!=null){
				number=new Number(11,n,Double.parseDouble(businessTax.get("CJSHUIE")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(11,n,0,ccellFormat_right);
				sheet.addCell(number);
			}
			if(businessTax.get("YYSHUIE")!=null){
				number=new Number(12,n,Double.parseDouble(businessTax.get("YYSHUIE")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(12,n,0,ccellFormat_right);
				sheet.addCell(number);
			}
			if(businessTax.get("JYSHUIE")!=null){
				number=new Number(13,n,Double.parseDouble(businessTax.get("JYSHUIE")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(13,n,0,ccellFormat_right);
				sheet.addCell(number);
			}
			if(businessTax.get("XIAOJI")!=null){
				number=new Number(14,n,Double.parseDouble(businessTax.get("XIAOJI")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(14,n,0,ccellFormat_right);
				sheet.addCell(number);
			}

			cell=new Label(15,n,DataUtil.StringUtil(businessTax.get("CORP_TAX_CODE")),format5);  
			sheet.addCell(cell);
			cell=new Label(16,n,DataUtil.StringUtil(businessTax.get("LINK_ADDRESS")),format5);  
			sheet.addCell(cell);
			cell=new Label(17,n,DataUtil.StringUtil(businessTax.get("BANK_NAME")),format5);  
			sheet.addCell(cell);
			sheet.setRowView(n,300,false);  
			n=n+1;
			
			REN_PRICE_sum=REN_PRICE_sum+DataUtil.doubleUtil(businessTax.get("REAL_PRICE"));
			YYSHUIE_sum=YYSHUIE_sum+DataUtil.doubleUtil(businessTax.get("YYSHUIE"));
			CJSHUIE_sum=CJSHUIE_sum+DataUtil.doubleUtil(businessTax.get("CJSHUIE"));
			JYSHUIE_sum=JYSHUIE_sum+DataUtil.doubleUtil(businessTax.get("JYSHUIE"));
			XIAOJI_sum=XIAOJI_sum+DataUtil.doubleUtil(businessTax.get("XIAOJI"));
		}
		 
		cell=new Label(0,n,""); 
		sheet.addCell(cell);
		cell=new Label(1,n,"小计",format5);
		sheet.mergeCells(1, n, 2, n);
		sheet.addCell(cell);
		
		number=new Number(8,n,REN_PRICE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(9,n,YYSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(10,n,YYSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(11,n,CJSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(12,n,YYSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(13,n,JYSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(14,n,XIAOJI_sum,ccellFormat_right);
		sheet.addCell(number);

		sheet.setRowView(n,300,false);  
		n=n+1;
		
		
		cell=new Label(0,n,""); 
		sheet.addCell(cell);
		cell=new Label(1,n,"税务申报",format5);
		sheet.mergeCells(1, n, 2, n);
		sheet.addCell(cell);
		number=new Number(8,n,REN_PRICE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(9,n,YYSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(10,n,YYSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(11,n,CJSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(12,n,YYSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(13,n,JYSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(14,n,XIAOJI_sum,ccellFormat_right);
		sheet.addCell(number);

		sheet.setRowView(n,300,false);  
		n=n+1;

			
		cell=new Label(0,1,"",format2_RIGHT);
		sheet.mergeCells(0, 1, 0, n-1);
		sheet.addCell(cell);
		cell=new Label(18,1,"",format2_LEFT);
		sheet.mergeCells(18, 1, 18, n-1);
		sheet.addCell(cell);
		cell=new Label(1,n,"",format2_Top);
		sheet.mergeCells(1, n, 17, n); 
		sheet.addCell(cell);
				
	}catch(Exception e){
		e.printStackTrace();
		LogPrint.getLogStackTrace(e, logger);
	}
	return baos;
}

public ByteArrayOutputStream managePriceTaxToExcelValueAdd(List<Map> businessTaxList) {
	WritableSheet sheet = null;
	
	try {
		workbookSettings.setEncoding("ISO-8859-1");
		sheet = wb.createSheet("管理费收入增值税明细表", 1);
		
		WritableFont font2 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
		WritableCellFormat format2_BOTTOM = new WritableCellFormat(font2);
		format2_BOTTOM.setAlignment(Alignment.CENTRE);
		format2_BOTTOM.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_BOTTOM.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
		format2_BOTTOM.setWrap(true);
		
		WritableCellFormat format2_Top = new WritableCellFormat(font2);
		format2_Top.setAlignment(Alignment.CENTRE);
		format2_Top.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_Top.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
		format2_Top.setWrap(true);
		
		WritableCellFormat format2_LEFT = new WritableCellFormat(font2);
		format2_LEFT.setAlignment(Alignment.CENTRE);
		format2_LEFT.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_LEFT.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
		format2_LEFT.setWrap(true);
		
		WritableCellFormat format2_RIGHT = new WritableCellFormat(font2);
		format2_RIGHT.setAlignment(Alignment.CENTRE);
		format2_RIGHT.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_RIGHT.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
		format2_RIGHT.setWrap(true);
		
		// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
		WritableFont font3 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
		WritableCellFormat format3 = new WritableCellFormat(font3);
		format3.setAlignment(Alignment.LEFT);
		format3.setVerticalAlignment(VerticalAlignment.CENTRE);
		// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
		format3.setWrap(true);
		// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
				 
		WritableCellFormat format5 = new WritableCellFormat(font3);
		format5.setAlignment(Alignment.CENTRE);
		format5.setVerticalAlignment(VerticalAlignment.CENTRE);
		format5.setWrap(true);
		WritableFont font6 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
		font6.setColour(Colour.BLUE);
		WritableCellFormat format6 = new WritableCellFormat(font6);
		format6.setAlignment(Alignment.LEFT); 
		format6.setVerticalAlignment(VerticalAlignment.CENTRE);
		format6.setWrap(true);
		
		WritableFont font8 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
		font8.setColour(Colour.BLUE);
		WritableCellFormat format8 = new WritableCellFormat(font8);
		format8.setAlignment(Alignment.LEFT); 
		format8.setVerticalAlignment(VerticalAlignment.CENTRE);
		format8.setWrap(true);
		
		WritableFont font9 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
		WritableCellFormat format9 = new WritableCellFormat(font9);
		format9.setAlignment(Alignment.LEFT);
		format9.setBackground(jxl.format.Colour.GRAY_25);
		format9.setVerticalAlignment(VerticalAlignment.CENTRE);
		format9.setWrap(true);
		
		WritableFont font4 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
		WritableCellFormat format4 = new WritableCellFormat(font4);
		format4.setAlignment(Alignment.CENTRE);
		format4.setBackground(jxl.format.Colour.GRAY_25);
		format4.setVerticalAlignment(VerticalAlignment.CENTRE);
		format4.setWrap(true);
					
		NumberFormat cnum=new NumberFormat("#,##0.00");
		NumberFormat pnum=new NumberFormat("##0.000%");
		NumberFormat fnum=new NumberFormat("##0.00");
		NumberFormat nnum=new NumberFormat("##"); 
		DateFormat   dateFormat=new DateFormat("yyyy-mm-dd");
		DateFormat   dateFormat2=new DateFormat("MMM-yy");
		WritableCellFormat dateCellFormat_center2=new WritableCellFormat(dateFormat2);
		dateCellFormat_center2.setAlignment(Alignment.CENTRE);
		WritableCellFormat ccellFormat_right=new WritableCellFormat(cnum);
		WritableCellFormat pcellFormat_left=new WritableCellFormat(pnum);
		WritableCellFormat pcellFormat_right=new WritableCellFormat(pnum);
		WritableCellFormat pcellFormat_center=new WritableCellFormat(pnum);
		WritableCellFormat pcellFormat_center2=new WritableCellFormat(pnum);
		WritableCellFormat fcellFormat_left=new WritableCellFormat(fnum);
		WritableCellFormat ccellFormat_left=new WritableCellFormat(cnum);
		WritableCellFormat dateCellFormat_left=new WritableCellFormat(dateFormat);
		WritableCellFormat dateCellFormat_center=new WritableCellFormat(dateFormat);
		ccellFormat_left.setAlignment(Alignment.LEFT);
		pcellFormat_left.setAlignment(Alignment.LEFT);
		pcellFormat_right.setAlignment(Alignment.RIGHT);
		pcellFormat_center.setAlignment(Alignment.CENTRE);
		pcellFormat_center2.setAlignment(Alignment.CENTRE);
		pcellFormat_center2.setVerticalAlignment(VerticalAlignment.CENTRE);		
		fcellFormat_left.setAlignment(Alignment.LEFT);
		dateCellFormat_left.setAlignment(Alignment.LEFT);
		dateCellFormat_center.setAlignment(Alignment.CENTRE);
		WritableCellFormat ncellFormat_center=new WritableCellFormat(nnum);
		ncellFormat_center.setAlignment(Alignment.CENTRE);
		WritableCellFormat ncellFormat_left=new WritableCellFormat(nnum);
		ncellFormat_left.setAlignment(Alignment.LEFT);
		Number number=null;
		//1.0E8 －－100000000
		DecimalFormat   nf2=new   DecimalFormat("###################");	
		
		sheet.setColumnView(0, 4);
		sheet.setColumnView(1, 8);
		sheet.setColumnView(2, 40);
		sheet.setColumnView(3, 40);
		sheet.setColumnView(4, 20);
		sheet.setColumnView(5, 10);
		sheet.setColumnView(6, 40);
		sheet.setColumnView(7, 15);
		sheet.setColumnView(8, 15);
		sheet.setColumnView(9, 15);
		sheet.setColumnView(10, 15);
		sheet.setColumnView(11, 15);
		sheet.setColumnView(12, 15);
		sheet.setColumnView(13,20);
		sheet.setColumnView(14,8);
		sheet.setColumnView(15,25);
		sheet.setColumnView(16,10);
		sheet.setColumnView(17,100);
		sheet.setColumnView(18,100);
		
		int n=0;
		Label cell = null;
		
		
		cell=new Label(0,0,"");  
		sheet.addCell(cell);
		cell=new Label(1,0,"",format2_BOTTOM);  
		sheet.mergeCells(1, 0,18, 0);
		sheet.addCell(cell);
		n=n+1;
		sheet.setRowView(0,300,false); 
		
		cell=new Label(0,1,""); 
		sheet.addCell(cell);
		cell=new Label(1,1,"管理费收入增值税明细表",format4); 
		sheet.mergeCells(1, 1, 18, 1); 
		sheet.addCell(cell);
		n=n+1;
		sheet.setRowView(1,320,false); 
		
		cell=new Label(0,n,""); 
		sheet.addCell(cell);
		cell=new Label(1,n,"序号",format5); 
		sheet.mergeCells(1, n, 1, n+1); 
		sheet.addCell(cell); 
		cell=new Label(2,n,"客户名称",format5); 
		sheet.mergeCells(2, n, 2, n+1); 
		sheet.addCell(cell); 
		cell=new Label(3,n,"供应商",format5); 
		sheet.mergeCells(3, n, 3, n+1); 
		sheet.addCell(cell); 
		cell=new Label(4,n,"合同号",format5); 
		sheet.mergeCells(4, n, 4, n+1); 
		sheet.addCell(cell); 
		cell=new Label(5,n,"业务员",format5); 
		sheet.mergeCells(5, n, 5, n+1); 
		sheet.addCell(cell); 
		cell=new Label(6,n,"办事处",format5); 
		sheet.mergeCells(6, n, 6, n+1); 
		sheet.addCell(cell); 
		cell=new Label(7,n,"分解项目",format5); 
		sheet.mergeCells(7, n, 7, n+1); 
		sheet.addCell(cell); 
		cell=new Label(8,n,"增值税",format5); 
		sheet.mergeCells(8, n,9, n); 
		sheet.addCell(cell); 
		cell=new Label(10,n,"城建税",format5); 
		sheet.mergeCells(10, n,11, n); 
		sheet.addCell(cell); 
		cell=new Label(12,n,"地区教育费附加",format5); 
		sheet.mergeCells(12, n,13, n); 
		sheet.addCell(cell); 
		cell=new Label(14,n,"小计",format5); 
		sheet.mergeCells(14, n,14, n+1); 
		sheet.addCell(cell); 
		cell=new Label(15,n,"税务登记号",format5); 
		sheet.mergeCells(15, n,15, n+1); 
		sheet.addCell(cell); 
		cell=new Label(16,n,"来源",format5); 
		sheet.mergeCells(16, n,16, n+1); 
		sheet.addCell(cell); 
		cell=new Label(17,n,"地址电话",format5); 
		sheet.mergeCells(17, n,17, n+1); 
		sheet.addCell(cell);
		cell=new Label(18,n,"开户行帐号",format5); 
		sheet.mergeCells(18, n,18, n+1); 
		sheet.addCell(cell);
		sheet.setRowView(n,300,false); 
		n=n+1;
		
		cell=new Label(0,n,""); 
		sheet.addCell(cell);
		cell=new Label(7,n,"计税依据",format5); 
		sheet.addCell(cell);
		cell=new Label(8,n,"税额",format5); 
		sheet.addCell(cell);
		cell=new Label(9,n,"计税依据",format5); 
		sheet.addCell(cell);
		cell=new Label(10,n,"税额",format5); 
		sheet.addCell(cell);
		cell=new Label(11,n,"计税依据",format5); 
		sheet.addCell(cell);
		cell=new Label(12,n,"税额",format5); 
		sheet.addCell(cell);
		n=n+1;
		
		
		double REN_PRICE_sum=0;
		double YYSHUIE_sum=0;
		double CJSHUIE_sum=0;
		double JYSHUIE_sum=0;
		double XIAOJI_sum=0;

		int i = 0;
		for (Object object2 : businessTaxList) {
			Map businessTax =(Map)object2;
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
											
			i = i + 1;						
			number=new Number(1,n,i,ncellFormat_center);
			sheet.addCell(number);
			
			cell=new Label(2,n,DataUtil.StringUtil(businessTax.get("CUST_NAME")),format5);  
			sheet.addCell(cell);
			cell=new Label(3,n,DataUtil.StringUtil(businessTax.get("SUPL_NAME")),format5);  
			sheet.addCell(cell);
			cell=new Label(4,n,DataUtil.StringUtil(businessTax.get("LEASE_CODE")),format5);  
			sheet.addCell(cell);
			cell=new Label(5,n,DataUtil.StringUtil(businessTax.get("NAME")),format5);  
			sheet.addCell(cell);
			cell=new Label(6,n,DataUtil.StringUtil(businessTax.get("DECP_NAME_CN")),format5);  
			sheet.addCell(cell);
			
			cell=new Label(7,n,DataUtil.StringUtil(businessTax.get("FICB_ITEM")),format5);  
			sheet.addCell(cell);			
					
			if(businessTax.get("REAL_PRICE")!=null){
				number=new Number(8,n,Double.parseDouble(businessTax.get("REAL_PRICE")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(8,n,0,ccellFormat_right);
				sheet.addCell(number);
			}
			if(businessTax.get("YYSHUIE")!=null){
				number=new Number(9,n,Double.parseDouble(businessTax.get("YYSHUIE")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(9,n,0,ccellFormat_right);
				sheet.addCell(number);
			}
			if(businessTax.get("YYSHUIE")!=null){
				number=new Number(10,n,Double.parseDouble(businessTax.get("YYSHUIE")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(10,n,0,ccellFormat_right);
				sheet.addCell(number);
			}
			if(businessTax.get("CJSHUIE")!=null){
				number=new Number(11,n,Double.parseDouble(businessTax.get("CJSHUIE")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(11,n,0,ccellFormat_right);
				sheet.addCell(number);
			}
			if(businessTax.get("YYSHUIE")!=null){
				number=new Number(12,n,Double.parseDouble(businessTax.get("YYSHUIE")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(12,n,0,ccellFormat_right);
				sheet.addCell(number);
			}
			if(businessTax.get("JYSHUIE")!=null){
				number=new Number(13,n,Double.parseDouble(businessTax.get("JYSHUIE")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(13,n,0,ccellFormat_right);
				sheet.addCell(number);
			}
			if(businessTax.get("XIAOJI")!=null){
				number=new Number(14,n,Double.parseDouble(businessTax.get("XIAOJI")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(14,n,0,ccellFormat_right);
				sheet.addCell(number);
			}

			cell=new Label(15,n,DataUtil.StringUtil(businessTax.get("CORP_TAX_CODE")),format5);  
			sheet.addCell(cell);
			
			cell=new Label(16,n,DataUtil.StringUtil(businessTax.get("FLAG")),format5);  
			sheet.addCell(cell);
			cell=new Label(17,n,DataUtil.StringUtil(businessTax.get("LINK_ADDRESS")),format5);  
			sheet.addCell(cell);
			cell=new Label(18,n,DataUtil.StringUtil(businessTax.get("BANK_NAME")),format5);  
			sheet.addCell(cell);
			
			sheet.setRowView(n,300,false);  
			n=n+1;
			
			REN_PRICE_sum=REN_PRICE_sum+DataUtil.doubleUtil(businessTax.get("REAL_PRICE"));
			YYSHUIE_sum=YYSHUIE_sum+DataUtil.doubleUtil(businessTax.get("YYSHUIE"));
			CJSHUIE_sum=CJSHUIE_sum+DataUtil.doubleUtil(businessTax.get("CJSHUIE"));
			JYSHUIE_sum=JYSHUIE_sum+DataUtil.doubleUtil(businessTax.get("JYSHUIE"));
			XIAOJI_sum=XIAOJI_sum+DataUtil.doubleUtil(businessTax.get("XIAOJI"));
		}
		 
		cell=new Label(0,n,""); 
		sheet.addCell(cell);
		cell=new Label(1,n,"小计",format5);
		sheet.mergeCells(1, n, 2, n);
		sheet.addCell(cell);
		
		number=new Number(8,n,REN_PRICE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(9,n,YYSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(10,n,YYSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(11,n,CJSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(12,n,YYSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(13,n,JYSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(14,n,XIAOJI_sum,ccellFormat_right);
		sheet.addCell(number);

		sheet.setRowView(n,300,false);  
		n=n+1;
		
		
		cell=new Label(0,n,""); 
		sheet.addCell(cell);
		cell=new Label(1,n,"税务申报",format5);
		sheet.mergeCells(1, n, 2, n);
		sheet.addCell(cell);
		number=new Number(8,n,REN_PRICE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(9,n,YYSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(10,n,YYSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(11,n,CJSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(12,n,YYSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(13,n,JYSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(14,n,XIAOJI_sum,ccellFormat_right);
		sheet.addCell(number);

		sheet.setRowView(n,300,false);  
		n=n+1;

			
		cell=new Label(0,1,"",format2_RIGHT);
		sheet.mergeCells(0, 1, 0, n-1);
		sheet.addCell(cell);
		cell=new Label(19,1,"",format2_LEFT);
		sheet.mergeCells(19, 1, 19, n-1);
		sheet.addCell(cell);
		cell=new Label(1,n,"",format2_Top);
		sheet.mergeCells(1, n, 18, n); 
		sheet.addCell(cell);
				
	}catch(Exception e){
		e.printStackTrace();
		LogPrint.getLogStackTrace(e, logger);
	}
	return baos;
}

//导出开票资料
public ByteArrayOutputStream exportOpenInvoiceToExcel(List<Map> businessTaxList) {
	WritableSheet sheet = null;
	
	try {
		workbookSettings.setEncoding("ISO-8859-1");
		sheet = wb.createSheet("开票资料", 1);
		
		WritableFont font2 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
		WritableCellFormat format2_BOTTOM = new WritableCellFormat(font2);
		format2_BOTTOM.setAlignment(Alignment.CENTRE);
		format2_BOTTOM.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_BOTTOM.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
		format2_BOTTOM.setWrap(true);
		
		WritableCellFormat format2_Top = new WritableCellFormat(font2);
		format2_Top.setAlignment(Alignment.CENTRE);
		format2_Top.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_Top.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
		format2_Top.setWrap(true);
		
		WritableCellFormat format2_LEFT = new WritableCellFormat(font2);
		format2_LEFT.setAlignment(Alignment.CENTRE);
		format2_LEFT.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_LEFT.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
		format2_LEFT.setWrap(true);
		
		WritableCellFormat format2_RIGHT = new WritableCellFormat(font2);
		format2_RIGHT.setAlignment(Alignment.CENTRE);
		format2_RIGHT.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_RIGHT.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
		format2_RIGHT.setWrap(true);
		
		// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
		WritableFont font3 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
		WritableCellFormat format3 = new WritableCellFormat(font3);
		format3.setAlignment(Alignment.LEFT);
		format3.setVerticalAlignment(VerticalAlignment.CENTRE);
		// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
		format3.setWrap(true);
		// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
				 
		WritableCellFormat format5 = new WritableCellFormat(font3);
		format5.setAlignment(Alignment.CENTRE);
		format5.setVerticalAlignment(VerticalAlignment.CENTRE);
		format5.setWrap(true);
		WritableFont font6 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
		font6.setColour(Colour.BLUE);
		WritableCellFormat format6 = new WritableCellFormat(font6);
		format6.setAlignment(Alignment.LEFT); 
		format6.setVerticalAlignment(VerticalAlignment.CENTRE);
		format6.setWrap(true);
		
		WritableFont font8 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
		font8.setColour(Colour.BLUE);
		WritableCellFormat format8 = new WritableCellFormat(font8);
		format8.setAlignment(Alignment.LEFT); 
		format8.setVerticalAlignment(VerticalAlignment.CENTRE);
		format8.setWrap(true);
		
		WritableFont font9 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
		WritableCellFormat format9 = new WritableCellFormat(font9);
		format9.setAlignment(Alignment.LEFT);
		format9.setBackground(jxl.format.Colour.GRAY_25);
		format9.setVerticalAlignment(VerticalAlignment.CENTRE);
		format9.setWrap(true);
		
		WritableFont font4 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
		WritableCellFormat format4 = new WritableCellFormat(font4);
		format4.setAlignment(Alignment.CENTRE);
		format4.setBackground(jxl.format.Colour.GRAY_25);
		format4.setVerticalAlignment(VerticalAlignment.CENTRE);
		format4.setWrap(true);
					
		NumberFormat cnum=new NumberFormat("#,##0.00");
		NumberFormat pnum=new NumberFormat("##0.000%");
		NumberFormat fnum=new NumberFormat("##0.00");
		NumberFormat nnum=new NumberFormat("##"); 
		DateFormat   dateFormat=new DateFormat("yyyy-mm-dd");
		DateFormat   dateFormat2=new DateFormat("MMM-yy");
		WritableCellFormat dateCellFormat_center2=new WritableCellFormat(dateFormat2);
		dateCellFormat_center2.setAlignment(Alignment.CENTRE);
		WritableCellFormat ccellFormat_right=new WritableCellFormat(cnum);
		WritableCellFormat pcellFormat_left=new WritableCellFormat(pnum);
		WritableCellFormat pcellFormat_right=new WritableCellFormat(pnum);
		WritableCellFormat pcellFormat_center=new WritableCellFormat(pnum);
		WritableCellFormat pcellFormat_center2=new WritableCellFormat(pnum);
		WritableCellFormat fcellFormat_left=new WritableCellFormat(fnum);
		WritableCellFormat ccellFormat_left=new WritableCellFormat(cnum);
		WritableCellFormat dateCellFormat_left=new WritableCellFormat(dateFormat);
		WritableCellFormat dateCellFormat_center=new WritableCellFormat(dateFormat);
		ccellFormat_left.setAlignment(Alignment.LEFT);
		pcellFormat_left.setAlignment(Alignment.LEFT);
		pcellFormat_right.setAlignment(Alignment.RIGHT);
		pcellFormat_center.setAlignment(Alignment.CENTRE);
		pcellFormat_center2.setAlignment(Alignment.CENTRE);
		pcellFormat_center2.setVerticalAlignment(VerticalAlignment.CENTRE);		
		fcellFormat_left.setAlignment(Alignment.LEFT);
		dateCellFormat_left.setAlignment(Alignment.LEFT);
		dateCellFormat_center.setAlignment(Alignment.CENTRE);
		WritableCellFormat ncellFormat_center=new WritableCellFormat(nnum);
		ncellFormat_center.setAlignment(Alignment.CENTRE);
		WritableCellFormat ncellFormat_left=new WritableCellFormat(nnum);
		ncellFormat_left.setAlignment(Alignment.LEFT);
		Number number=null;
		//1.0E8 －－100000000
		DecimalFormat   nf2=new   DecimalFormat("###################");	
		
		sheet.setColumnView(0, 10);
		sheet.setColumnView(1, 8);
		sheet.setColumnView(2, 40);
		sheet.setColumnView(3, 40);
		sheet.setColumnView(4, 20);
		sheet.setColumnView(5, 15);
		sheet.setColumnView(6, 10);
		sheet.setColumnView(7, 15);
		sheet.setColumnView(8, 15);
		sheet.setColumnView(9, 25);
		sheet.setColumnView(10, 25);
		sheet.setColumnView(11, 4);
		
		int n=0;
		Label cell = null;
		
		
//		cell=new Label(0,0,"");  
//		sheet.addCell(cell);
//		cell=new Label(1,0,"",format2_BOTTOM);  
//		sheet.mergeCells(1, 0,11, 0);
//		sheet.addCell(cell);
//		n=n+1;
//		sheet.setRowView(0,300,false); 
		
//		cell=new Label(0,1,""); 
//		sheet.addCell(cell);
//		cell=new Label(1,1,"开票资料",format4); 
//		sheet.mergeCells(1, 1, 10, 1); 
//		sheet.addCell(cell);
//		n=n+1;
//		sheet.setRowView(1,320,false); 
		
		cell=new Label(0,n,"编号"); 
		sheet.mergeCells(0, n, 0, n); 
		sheet.addCell(cell);
		cell=new Label(1,n,"支付表编号",format5); 
		sheet.mergeCells(1, n, 1, n); 
		sheet.addCell(cell); 
		cell=new Label(2,n,"承租人",format5); 
		sheet.mergeCells(2, n, 2, n); 
		sheet.addCell(cell); 
		cell=new Label(3,n,"供应商",format5); 
		sheet.mergeCells(3, n, 3, n); 
		sheet.addCell(cell); 
		cell=new Label(4,n,"合同号",format5); 
		sheet.mergeCells(4, n, 4, n); 
		sheet.addCell(cell); 
		cell=new Label(5,n,"办事处",format5); 
		sheet.mergeCells(5, n, 5, n); 
		sheet.addCell(cell); 
		cell=new Label(6,n,"单位主管",format5); 
		sheet.mergeCells(6, n, 6, n); 
		sheet.addCell(cell); 
		cell=new Label(7,n,"利息",format5); 
		sheet.mergeCells(7, n, 7, n); 
		sheet.addCell(cell); 
		cell=new Label(8,n,"预期租金",format5); 
		sheet.mergeCells(8, n,8, n); 
		sheet.addCell(cell); 
		cell=new Label(9,n,"计税依据",format5); 
		sheet.mergeCells(9, n,9, n); 
		sheet.addCell(cell); 
		cell=new Label(10,n,"备注",format5); 
		sheet.mergeCells(10, n,10, n); 
		sheet.addCell(cell); 

		sheet.setRowView(n,300,false); 
		n=n+1;		

		int i = 0;
		for (Object object2 : businessTaxList) {
			Map businessTax =(Map)object2;
											
			i = i + 1;	
			cell=new Label(0,n,DataUtil.StringUtil(businessTax.get("RECD_ID")),format5);  
			sheet.addCell(cell);
			
			cell=new Label(1,n,DataUtil.StringUtil(businessTax.get("RECP_ID")),format5);  
			sheet.addCell(cell);

			cell=new Label(2,n,DataUtil.StringUtil(businessTax.get("CUST_NAME")),format5);  
			sheet.addCell(cell);
			cell=new Label(3,n,DataUtil.StringUtil(businessTax.get("SUPL_NAME")),format5);  
			sheet.addCell(cell);
			cell=new Label(4,n,DataUtil.StringUtil(businessTax.get("LEASE_CODE")),format5);  
			sheet.addCell(cell);
			cell=new Label(5,n,DataUtil.StringUtil(businessTax.get("DECP_NAME_CN")),format5);  
			sheet.addCell(cell);
			cell=new Label(6,n,DataUtil.StringUtil(businessTax.get("NAME")),format5);  
			sheet.addCell(cell);
			
			if(businessTax.get("REN_PRICE")!=null){
				number=new Number(7,n,Double.parseDouble(businessTax.get("REN_PRICE")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(7,n,0,ccellFormat_right);
				sheet.addCell(number);
			}		
					
			if(businessTax.get("MONTH_PRICE")!=null){
				number=new Number(8,n,Double.parseDouble(businessTax.get("MONTH_PRICE")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(8,n,0,ccellFormat_right);
				sheet.addCell(number);
			}

			cell=new Label(9,n,DataUtil.StringUtil(businessTax.get("CORP_TAX_CODE")),format5);  
			sheet.addCell(cell);
			cell=new Label(10,n,DataUtil.StringUtil(businessTax.get("REMARK")),format5);  
			sheet.addCell(cell);			
						
			sheet.setRowView(n,300,false);  
			n=n+1;

		}
			
//		cell=new Label(0,1,"",format2_RIGHT);
//		sheet.mergeCells(0, 1, 0, n-1);
//		sheet.addCell(cell);
//		cell=new Label(11,1,"",format2_LEFT);
//		sheet.mergeCells(11, 1, 11, n-1);
//		sheet.addCell(cell);
//		cell=new Label(1,n,"",format2_Top);
//		sheet.mergeCells(1, n, 10, n); 
//		sheet.addCell(cell);
				
	}catch(Exception e){
		e.printStackTrace();
		LogPrint.getLogStackTrace(e, logger);
	}
	return baos;
}

//导出开票资料
public ByteArrayOutputStream exportCustInfoToExcel(List<Map> businessTaxList) {
	WritableSheet sheet = null;
	
	try {
		
		workbookSettings.setEncoding("ISO-8859-1");
		sheet = wb.createSheet("客户邮寄地址", 1);
		
		
		sheet.setColumnView(0, 20);
		sheet.setColumnView(1, 20);
		sheet.setColumnView(2, 20);
		sheet.setColumnView(3, 20);
		sheet.setColumnView(4, 20);
		sheet.setColumnView(5, 80);
		sheet.setColumnView(6, 40);
		int n=0;
		Label cell = null;
		cell=new Label(0,n,"客户名称"); 
		sheet.mergeCells(0, n, 0, n); 
		sheet.addCell(cell);
		cell=new Label(1,n,"联系人"); 
		sheet.mergeCells(1, n, 1, n); 
		sheet.addCell(cell);
		cell=new Label(2,n,"手机"); 
		sheet.mergeCells(2, n, 2, n); 
		sheet.addCell(cell);
		cell=new Label(3,n,"固话"); 
		sheet.mergeCells(3, n, 3, n); 
		sheet.addCell(cell);
		cell=new Label(4,n,"Email"); 
		sheet.mergeCells(4, n, 4, n); 
		sheet.addCell(cell); 
		cell=new Label(5,n,"邮寄地址"); 
		sheet.mergeCells(5, n, 5, n); 
		sheet.addCell(cell); 
		cell=new Label(6,n,"办事处"); 
		sheet.mergeCells(6, n, 6, n); 
		sheet.addCell(cell); 
		cell=new Label(7,n,"寄件客户编码"); 
		sheet.mergeCells(7, n, 7, n); 
		sheet.addCell(cell); 
		cell=new Label(8,n,"寄件人"); 
		sheet.mergeCells(8, n, 8, n); 
		sheet.addCell(cell); 
		cell=new Label(9,n,"寄件公司"); 
		sheet.mergeCells(9, n, 9, n); 
		sheet.addCell(cell); 
		cell=new Label(10,n,"寄件人联系电话"); 
		sheet.mergeCells(10, n, 10, n); 
		sheet.addCell(cell); 
		cell=new Label(11,n,"寄件人地址"); 
		sheet.mergeCells(11, n, 11, n); 
		sheet.addCell(cell); 
		cell=new Label(12,n,"托寄物内容"); 
		sheet.mergeCells(12, n, 12, n); 
		sheet.addCell(cell); 
		cell=new Label(13,n,"数量"); 
		sheet.mergeCells(13, n, 13, n); 
		sheet.addCell(cell); 
		cell=new Label(14,n,"收货人"); 
		sheet.mergeCells(14, n, 14, n); 
		sheet.addCell(cell); 
		cell=new Label(15,n,"收货公司"); 
		sheet.mergeCells(15, n, 15, n); 
		sheet.addCell(cell); 
		cell=new Label(16,n,"收货人联系电话"); 
		sheet.mergeCells(16, n, 16, n); 
		sheet.addCell(cell); 
		cell=new Label(17,n,"收货地址"); 
		sheet.mergeCells(17, n, 17, n); 
		sheet.addCell(cell); 
		cell=new Label(18,n,"备注"); 
		sheet.mergeCells(18, n, 18, n); 
		sheet.addCell(cell);
		cell=new Label(19,n,"付款方式"); 
		sheet.mergeCells(19, n, 19, n); 
		sheet.addCell(cell);
		cell=new Label(20,n,"保价"); 
		sheet.mergeCells(20, n, 20, n); 
		sheet.addCell(cell);
		cell=new Label(21,n,"保价金额"); 
		sheet.mergeCells(21, n, 21, n); 
		sheet.addCell(cell);
		cell=new Label(22,n,"代收货款"); 
		sheet.mergeCells(22, n, 22, n); 
		sheet.addCell(cell);
		cell=new Label(23,n,"代收货款卡号"); 
		sheet.mergeCells(23, n, 23, n); 
		sheet.addCell(cell);
		cell=new Label(24,n,"代收货款金额"); 
		sheet.mergeCells(24, n, 24, n); 
		sheet.addCell(cell);
		cell=new Label(25,n,"备注1"); 
		sheet.mergeCells(25, n, 25, n); 
		sheet.addCell(cell);
		cell=new Label(26,n,"备注2"); 
		sheet.mergeCells(26, n, 26, n); 
		sheet.addCell(cell);
		cell=new Label(27,n,"备注3"); 
		sheet.mergeCells(27, n, 27, n); 
		sheet.addCell(cell);
		cell=new Label(28,n,"其他"); 
		sheet.mergeCells(28, n, 28, n); 
		sheet.addCell(cell);
		cell=new Label(29,n,"快递方式"); 
		sheet.mergeCells(29, n, 29, n); 
		sheet.addCell(cell);
		cell=new Label(30,n,"快递付款方式"); 
		sheet.mergeCells(30, n, 30, n); 
		sheet.addCell(cell);
		n++;
		String name = DictionaryUtil.getFlag("邮寄资料快递信息", "寄件人");
		String company = DictionaryUtil.getFlag("邮寄资料快递信息", "寄件公司");
		String phone = DictionaryUtil.getFlag("邮寄资料快递信息", "寄件人联系电话");
		String address = DictionaryUtil.getFlag("邮寄资料快递信息", "寄件人地址");
		List express_pay_way = DictionaryUtil.getDictionary("快递付款方式");
		for(Map m:businessTaxList){
			cell=new Label(0,n,m.get("CUST_NAME")!=null?String.valueOf(m.get("CUST_NAME")):""); 
			sheet.mergeCells(0, n, 0, n); 
			sheet.addCell(cell);
			cell=new Label(1,n,m.get("LINK_NAME")!=null?String.valueOf(m.get("LINK_NAME")):""); 
			sheet.mergeCells(1, n, 1, n); 
			sheet.addCell(cell);
			cell=new Label(2,n,m.get("LINK_MOBILE")!=null?String.valueOf(m.get("LINK_MOBILE")):""); 
			sheet.mergeCells(2, n, 2, n); 
			sheet.addCell(cell);
			cell=new Label(3,n,m.get("LPHONE")!=null?String.valueOf(m.get("LPHONE")):""); 
			sheet.mergeCells(3, n, 3, n); 
			sheet.addCell(cell);
			cell=new Label(4,n,m.get("LINK_EMAIL")!=null?String.valueOf(m.get("LINK_EMAIL")):""); 
			sheet.mergeCells(4, n, 4, n); 
			sheet.addCell(cell); 
			cell=new Label(5,n,m.get("LW_ADDRESS")!=null?String.valueOf(m.get("LW_ADDRESS")):""); 
			sheet.mergeCells(5, n, 5, n); 
			sheet.addCell(cell); 
			String decpName = m.get("DECP_NAME_CN")!=null?String.valueOf(m.get("DECP_NAME_CN")):"";
			cell=new Label(6,n,decpName); 
			sheet.mergeCells(6, n, 6, n); 
			sheet.addCell(cell); 
			cell=new Label(8,n,name); 
			sheet.mergeCells(8, n, 8, n); 
			sheet.addCell(cell); 
			cell=new Label(9,n,company); 
			sheet.mergeCells(9, n, 9, n); 
			sheet.addCell(cell);
			cell=new Label(10,n,phone); 
			sheet.mergeCells(10, n, 10, n); 
			sheet.addCell(cell);
			cell=new Label(11,n,address); 
			sheet.mergeCells(11, n, 11, n); 
			sheet.addCell(cell);
			cell=new Label(14,n,m.get("LINK_NAME")!=null?String.valueOf(m.get("LINK_NAME")):""); 
			sheet.mergeCells(14, n, 14, n); 
			sheet.addCell(cell);
			cell=new Label(15,n,m.get("CUST_NAME")!=null?String.valueOf(m.get("CUST_NAME")):""); 
			sheet.mergeCells(15, n, 15, n); 
			sheet.addCell(cell);
			cell=new Label(16,n,m.get("LINK_MOBILE")!=null?String.valueOf(m.get("LINK_MOBILE")):""); 
			sheet.mergeCells(16, n, 16, n); 
			sheet.addCell(cell);
			cell=new Label(17,n,m.get("LW_ADDRESS")!=null?String.valueOf(m.get("LW_ADDRESS")):""); 
			sheet.mergeCells(17, n, 17, n); 
			sheet.addCell(cell);
			cell=new Label(22,n,"N-否"); 
			sheet.mergeCells(22, n, 22, n); 
			sheet.addCell(cell);
			
			String express = (String) m.get("EXPRESS");
			if(express==null){
				express = "顺丰";
				if("苏州总公司".equals(decpName)||"昆山设备".equals(decpName)
						||"南京设备".equals(decpName)
						||"上海设备".equals(decpName)
						||"上海商用车".equals(decpName)
						||"苏州商用车".equals(decpName)
						||"苏州设备".equals(decpName)
						||"苏州小车".equals(decpName)
						||"苏州乘用车".equals(decpName)
						||"上海乘用车".equals(decpName)
						||"杭州乘用车".equals(decpName)
						||"宁波设备".equals(decpName)){
					express = "汇通";
				}
			}
			cell=new Label(29,n,express); 
			sheet.mergeCells(29, n, 29, n); 
			sheet.addCell(cell);
			String expressPayWay = m.get("EXPRESS_PAY_WAY")!=null?String.valueOf(m.get("EXPRESS_PAY_WAY")):"2" ;
			String payWay = "";
			for(int i =0,j=express_pay_way.size();i<j;i++){
				Map payway = (Map) express_pay_way.get(i);
				if(expressPayWay.equals(payway.get("CODE"))){
					payWay = (String) payway.get("FLAG");
				}
			}
			cell=new Label(30,n,payWay); 
			sheet.mergeCells(30, n, 30, n); 
			sheet.addCell(cell);
			n++;
		}
				
	}catch(Exception e){
		e.printStackTrace();
		LogPrint.getLogStackTrace(e, logger);
	}
	return baos;
}

//导出开票资料
public ByteArrayOutputStream exportValueAddOpenInvoiceToExcel(List<Map> businessTaxList) {
	WritableSheet sheet = null;
	
	try {
		
		String taxPlanCode="";
		if(businessTaxList!=null&&businessTaxList.size()>0) {
			taxPlanCode=businessTaxList.get(0).get("TAX_PLAN_CODE")+"";
		}
		workbookSettings.setEncoding("ISO-8859-1");
		sheet = wb.createSheet("增值税开票资料", 1);
		
		WritableFont font2 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
		WritableCellFormat format2_BOTTOM = new WritableCellFormat(font2);
		format2_BOTTOM.setAlignment(Alignment.CENTRE);
		format2_BOTTOM.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_BOTTOM.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
		format2_BOTTOM.setWrap(true);
		
		WritableCellFormat format2_Top = new WritableCellFormat(font2);
		format2_Top.setAlignment(Alignment.CENTRE);
		format2_Top.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_Top.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
		format2_Top.setWrap(true);
		
		WritableCellFormat format2_LEFT = new WritableCellFormat(font2);
		format2_LEFT.setAlignment(Alignment.CENTRE);
		format2_LEFT.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_LEFT.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
		format2_LEFT.setWrap(true);
		
		WritableCellFormat format2_RIGHT = new WritableCellFormat(font2);
		format2_RIGHT.setAlignment(Alignment.CENTRE);
		format2_RIGHT.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_RIGHT.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
		format2_RIGHT.setWrap(true);
		
		// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
		WritableFont font3 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
		WritableCellFormat format3 = new WritableCellFormat(font3);
		format3.setAlignment(Alignment.LEFT);
		format3.setVerticalAlignment(VerticalAlignment.CENTRE);
		// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
		format3.setWrap(true);
		// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
				 
		WritableCellFormat format5 = new WritableCellFormat(font3);
		format5.setAlignment(Alignment.CENTRE);
		format5.setVerticalAlignment(VerticalAlignment.CENTRE);
		format5.setWrap(true);
		WritableFont font6 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
		font6.setColour(Colour.BLUE);
		WritableCellFormat format6 = new WritableCellFormat(font6);
		format6.setAlignment(Alignment.LEFT); 
		format6.setVerticalAlignment(VerticalAlignment.CENTRE);
		format6.setWrap(true);
		
		WritableFont font8 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
		font8.setColour(Colour.BLUE);
		WritableCellFormat format8 = new WritableCellFormat(font8);
		format8.setAlignment(Alignment.LEFT); 
		format8.setVerticalAlignment(VerticalAlignment.CENTRE);
		format8.setWrap(true);
		
		WritableFont font9 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
		WritableCellFormat format9 = new WritableCellFormat(font9);
		format9.setAlignment(Alignment.LEFT);
		format9.setBackground(jxl.format.Colour.GRAY_25);
		format9.setVerticalAlignment(VerticalAlignment.CENTRE);
		format9.setWrap(true);
		
		WritableFont font4 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
		WritableCellFormat format4 = new WritableCellFormat(font4);
		format4.setAlignment(Alignment.CENTRE);
		format4.setBackground(jxl.format.Colour.GRAY_25);
		format4.setVerticalAlignment(VerticalAlignment.CENTRE);
		format4.setWrap(true);
					
		NumberFormat cnum=new NumberFormat("#,##0.00");
		NumberFormat pnum=new NumberFormat("##0.000%");
		NumberFormat fnum=new NumberFormat("##0.00");
		NumberFormat nnum=new NumberFormat("##"); 
		DateFormat   dateFormat=new DateFormat("yyyy-mm-dd");
		DateFormat   dateFormat2=new DateFormat("MMM-yy");
		WritableCellFormat dateCellFormat_center2=new WritableCellFormat(dateFormat2);
		dateCellFormat_center2.setAlignment(Alignment.CENTRE);
		WritableCellFormat ccellFormat_right=new WritableCellFormat(cnum);
		WritableCellFormat pcellFormat_left=new WritableCellFormat(pnum);
		WritableCellFormat pcellFormat_right=new WritableCellFormat(pnum);
		WritableCellFormat pcellFormat_center=new WritableCellFormat(pnum);
		WritableCellFormat pcellFormat_center2=new WritableCellFormat(pnum);
		WritableCellFormat fcellFormat_left=new WritableCellFormat(fnum);
		WritableCellFormat ccellFormat_left=new WritableCellFormat(cnum);
		WritableCellFormat dateCellFormat_left=new WritableCellFormat(dateFormat);
		WritableCellFormat dateCellFormat_center=new WritableCellFormat(dateFormat);
		ccellFormat_left.setAlignment(Alignment.LEFT);
		pcellFormat_left.setAlignment(Alignment.LEFT);
		pcellFormat_right.setAlignment(Alignment.RIGHT);
		pcellFormat_center.setAlignment(Alignment.CENTRE);
		pcellFormat_center2.setAlignment(Alignment.CENTRE);
		pcellFormat_center2.setVerticalAlignment(VerticalAlignment.CENTRE);		
		fcellFormat_left.setAlignment(Alignment.LEFT);
		dateCellFormat_left.setAlignment(Alignment.LEFT);
		dateCellFormat_center.setAlignment(Alignment.CENTRE);
		WritableCellFormat ncellFormat_center=new WritableCellFormat(nnum);
		ncellFormat_center.setAlignment(Alignment.CENTRE);
		WritableCellFormat ncellFormat_left=new WritableCellFormat(nnum);
		ncellFormat_left.setAlignment(Alignment.LEFT);
		Number number=null;
		//1.0E8 －－100000000
		DecimalFormat   nf2=new   DecimalFormat("###################");	
		
		sheet.setColumnView(0, 10);
		sheet.setColumnView(1, 10);
		sheet.setColumnView(2, 20);
		sheet.setColumnView(3, 80);
		sheet.setColumnView(4, 20);
		sheet.setColumnView(5, 70);
		sheet.setColumnView(6, 40);
		sheet.setColumnView(7, 10);
		sheet.setColumnView(8, 10);
		sheet.setColumnView(9, 10);
		sheet.setColumnView(10, 10);
		sheet.setColumnView(11, 10);
		sheet.setColumnView(12, 15);
		sheet.setColumnView(13, 10);
		sheet.setColumnView(14, 20);
		
		int n=0;
		Label cell = null;
		cell=new Label(0,n,"编号"); 
		sheet.mergeCells(0, n, 0, n); 
		sheet.addCell(cell);
		cell=new Label(1,n,"支付表编号"); 
		sheet.mergeCells(1, n, 1, n); 
		sheet.addCell(cell);
		cell=new Label(2,n,"单据编号"); 
		sheet.mergeCells(2, n, 2, n); 
		sheet.addCell(cell);
		cell=new Label(3,n,"地址电话",format5); 
		sheet.mergeCells(3, n, 3, n); 
		sheet.addCell(cell); 
		cell=new Label(4,n,"税号",format5); 
		sheet.mergeCells(4, n, 4, n); 
		sheet.addCell(cell); 
		cell=new Label(5,n,"开户行帐号",format5); 
		sheet.mergeCells(5, n, 5, n); 
		sheet.addCell(cell); 
		cell=new Label(6,n,"客户名称",format5); 
		sheet.mergeCells(6, n, 6, n); 
		sheet.addCell(cell); 
		cell=new Label(7,n,"产品名称",format5); 
		sheet.mergeCells(7, n,7, n); 
		sheet.addCell(cell); 
		cell=new Label(8,n,"规格型号",format5); 
		sheet.mergeCells(8, n, 8, n); 
		sheet.addCell(cell); 
		cell=new Label(9,n,"单位",format5); 
		sheet.mergeCells(9, n, 9, n); 
		sheet.addCell(cell); 
		cell=new Label(10,n,"数量",format5); 
		sheet.mergeCells(10, n,10, n); 
		sheet.addCell(cell); 
		cell=new Label(11,n,"单价",format5); 
		sheet.mergeCells(11, n,11, n); 
		sheet.addCell(cell); 
		if("4".equals(taxPlanCode)) {
			cell=new Label(12,n,"融资租赁本金",format5); 
			sheet.mergeCells(12, n,12, n); 
			sheet.addCell(cell);
			
			cell=new Label(13,n,"融资租赁本金",format5); 
			sheet.mergeCells(13, n,13, n); 
			sheet.addCell(cell);
			
			cell=new Label(14,n,"融资租赁利息",format5); 
			sheet.mergeCells(14, n,14, n); 
			sheet.addCell(cell);
			
			cell=new Label(15,n,"税率",format5); 
			sheet.mergeCells(15, n,15, n); 
			sheet.addCell(cell); 
			
			cell=new Label(16,n,"备注1",format5); 
			sheet.mergeCells(16, n,16, n); 
			sheet.addCell(cell); 
			
			cell=new Label(17,n,"备注2",format5); 
			sheet.mergeCells(17, n,17, n); 
			sheet.addCell(cell);
			
			cell=new Label(18,n,"拨款日期",format5); 
			sheet.mergeCells(18, n,18, n); 
			sheet.addCell(cell);
			
			 //是否缴款
			cell=new Label(19,n,"是否缴款",format5); 
			sheet.mergeCells(19, n,19, n); 
			sheet.addCell(cell);
			//销帐日期
			cell=new Label(20,n,"销帐日期",format5); 
			sheet.mergeCells(20, n,20, n); 
			sheet.addCell(cell);
			
		} else {
			cell=new Label(12,n,"金额",format5); 
			sheet.mergeCells(12, n,12, n); 
			sheet.addCell(cell);
			
			cell=new Label(13,n,"税率",format5); 
			sheet.mergeCells(13, n,13, n); 
			sheet.addCell(cell); 
			
			cell=new Label(14,n,"备注1",format5); 
			sheet.mergeCells(14, n,14, n); 
			sheet.addCell(cell); 
			
			cell=new Label(15,n,"备注2",format5); 
			sheet.mergeCells(15, n,15, n); 
			sheet.addCell(cell);
		}
		sheet.setRowView(n,300,false); 
		n=n+1;		

		int i = 0;
		for (Object object2 : businessTaxList) {
			Map businessTax =(Map)object2;
										
			i = i + 1;	
			cell=new Label(0,n,DataUtil.StringUtil(businessTax.get("RECD_ID")),format5);  
			sheet.addCell(cell);
			cell=new Label(1,n,DataUtil.StringUtil(businessTax.get("RECP_ID")),format5);  
			sheet.addCell(cell);

			cell=new Label(2,n,DataUtil.StringUtil(businessTax.get("RUNNUM")),format5);  
			sheet.addCell(cell);
			cell=new Label(3,n,DataUtil.StringUtil(businessTax.get("LINK_ADDRESS")),format5);  
			sheet.addCell(cell);
			cell=new Label(4,n,DataUtil.StringUtil(businessTax.get("CORP_TAX_CODE")),format5);  
			sheet.addCell(cell);
			cell=new Label(5,n,DataUtil.StringUtil(businessTax.get("BANK_NAME")),format5);  
			sheet.addCell(cell);
			cell=new Label(6,n,DataUtil.StringUtil(businessTax.get("CUST_NAME")),format5);  
			sheet.addCell(cell);
			cell=new Label(7,n,DataUtil.StringUtil(businessTax.get("PRODUCT_NAME")),format5);  
			sheet.addCell(cell);
			cell=new Label(8,n,DataUtil.StringUtil(businessTax.get("PRODUCT_KIND")),format5);  
			sheet.addCell(cell);
			cell=new Label(9,n,DataUtil.StringUtil(businessTax.get("UNIT")),format5);  
			sheet.addCell(cell);
			cell=new Label(10,n,DataUtil.StringUtil(businessTax.get("NUMBER")),format5);  
			sheet.addCell(cell);
			cell=new Label(11,n,DataUtil.StringUtil(businessTax.get("UNIT_PRICE")),format5);  
			sheet.addCell(cell);
			
			DecimalFormat df1=new DecimalFormat("####.00");
			if("4".equals(taxPlanCode)) {
				if(businessTax.get("OWN_PRICE")!=null){
					number=new Number(12,n,Double.valueOf(df1.format(Double.parseDouble(businessTax.get("OWN_PRICE")+"")-Double.parseDouble(businessTax.get("PLEDGE_PRICE")+""))),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(12,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
				
				number=new Number(13,n,Double.valueOf(df1.format(Double.parseDouble(businessTax.get("PLEDGE_PRICE")+""))),ccellFormat_right);
				sheet.addCell(number);
				
				if(businessTax.get("REN_PRICE")!=null){
					number=new Number(14,n,Double.valueOf(df1.format(Double.parseDouble(businessTax.get("REN_PRICE")+""))),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(14,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
				
				if(businessTax.get("TAX_RATE")!=null){
					number=new Number(15,n,Double.parseDouble(businessTax.get("TAX_RATE")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(15,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
	
				cell=new Label(16,n,DataUtil.StringUtil(businessTax.get("REMARK1")),format5);  
				sheet.addCell(cell);
				
				cell=new Label(18,n,DataUtil.StringUtil(businessTax.get("PAY_DATE")),format5);  
				sheet.addCell(cell);
				//zhangbo add 0107 是否缴款
				if(("1").equals(DataUtil.StringUtil(businessTax.get("ISPAY")))){
					cell=new Label(19,n,"已缴款",format5);  
					sheet.addCell(cell);
					//查询销帐日期
					Map<String, String> map = new HashMap<String, String>();
					map.put("RECP_ID", DataUtil.StringUtil(businessTax.get("RECP_ID")));
					map.put("PERIOD_NUM", DataUtil.StringUtil(businessTax.get("PERIOD_NUM")));
					Map<String, String> decomposeDateMap = new HashMap<String, String>();
					decomposeDateMap = (Map<String,String>)DataAccessor.query("priceReport.getDecomposeDateMapByRecpId", map, RS_TYPE.MAP);
					cell=new Label(20,n,DataUtil.StringUtil(decomposeDateMap.get("DECOMPOSE_DATE")),format5);  
					sheet.addCell(cell);
				}else if(("0").equals(DataUtil.StringUtil(businessTax.get("ISPAY")))){
					cell=new Label(19,n,"未缴款",format5);  
					sheet.addCell(cell);
					cell=new Label(20,n,"/",format5);  
					sheet.addCell(cell);
				}
			} else {
				if(businessTax.get("REN_PRICE")!=null){
					number=new Number(12,n,Double.valueOf(df1.format(Double.parseDouble(businessTax.get("REN_PRICE")+""))),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(12,n,0,ccellFormat_right);
					sheet.addCell(number);
				}		
						
				if(businessTax.get("TAX_RATE")!=null){
					number=new Number(13,n,Double.parseDouble(businessTax.get("TAX_RATE")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(13,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
	
				cell=new Label(14,n,DataUtil.StringUtil(businessTax.get("REMARK1")),format5);  
				sheet.addCell(cell);
			}			
			sheet.setRowView(n,300,false);  
			n=n+1;
		}
				
	}catch(Exception e){
		e.printStackTrace();
		LogPrint.getLogStackTrace(e, logger);
	}
	return baos;
}

//导出开票资料
public ByteArrayOutputStream exportValueAddOpenInvoiceToExcel1(List<Map> businessTaxList) {
	WritableSheet sheet = null;
	
	try {
		
		String taxPlanCode="";
		if(businessTaxList!=null&&businessTaxList.size()>0) {
			taxPlanCode=businessTaxList.get(0).get("TAX_PLAN_CODE")+"";
		}
		workbookSettings.setEncoding("ISO-8859-1");
		sheet = wb.createSheet("增值税开票资料", 1);
		
		WritableFont font2 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
		WritableCellFormat format2_BOTTOM = new WritableCellFormat(font2);
		format2_BOTTOM.setAlignment(Alignment.CENTRE);
		format2_BOTTOM.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_BOTTOM.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
		format2_BOTTOM.setWrap(true);
		
		WritableCellFormat format2_Top = new WritableCellFormat(font2);
		format2_Top.setAlignment(Alignment.CENTRE);
		format2_Top.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_Top.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
		format2_Top.setWrap(true);
		
		WritableCellFormat format2_LEFT = new WritableCellFormat(font2);
		format2_LEFT.setAlignment(Alignment.CENTRE);
		format2_LEFT.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_LEFT.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
		format2_LEFT.setWrap(true);
		
		WritableCellFormat format2_RIGHT = new WritableCellFormat(font2);
		format2_RIGHT.setAlignment(Alignment.CENTRE);
		format2_RIGHT.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_RIGHT.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
		format2_RIGHT.setWrap(true);
		
		// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
		WritableFont font3 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
		WritableCellFormat format3 = new WritableCellFormat(font3);
		format3.setAlignment(Alignment.LEFT);
		format3.setVerticalAlignment(VerticalAlignment.CENTRE);
		// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
		format3.setWrap(true);
		// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
				 
		WritableCellFormat format5 = new WritableCellFormat(font3);
		format5.setAlignment(Alignment.CENTRE);
		format5.setVerticalAlignment(VerticalAlignment.CENTRE);
		format5.setWrap(true);
		WritableFont font6 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
		font6.setColour(Colour.BLUE);
		WritableCellFormat format6 = new WritableCellFormat(font6);
		format6.setAlignment(Alignment.LEFT); 
		format6.setVerticalAlignment(VerticalAlignment.CENTRE);
		format6.setWrap(true);
		
		WritableFont font8 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
		font8.setColour(Colour.BLUE);
		WritableCellFormat format8 = new WritableCellFormat(font8);
		format8.setAlignment(Alignment.LEFT); 
		format8.setVerticalAlignment(VerticalAlignment.CENTRE);
		format8.setWrap(true);
		
		WritableFont font9 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
		WritableCellFormat format9 = new WritableCellFormat(font9);
		format9.setAlignment(Alignment.LEFT);
		format9.setBackground(jxl.format.Colour.GRAY_25);
		format9.setVerticalAlignment(VerticalAlignment.CENTRE);
		format9.setWrap(true);
		
		WritableFont font4 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
		WritableCellFormat format4 = new WritableCellFormat(font4);
		format4.setAlignment(Alignment.CENTRE);
		format4.setBackground(jxl.format.Colour.GRAY_25);
		format4.setVerticalAlignment(VerticalAlignment.CENTRE);
		format4.setWrap(true);
					
		NumberFormat cnum=new NumberFormat("#,##0.00");
		NumberFormat pnum=new NumberFormat("##0.000%");
		NumberFormat fnum=new NumberFormat("##0.00");
		NumberFormat nnum=new NumberFormat("##"); 
		DateFormat   dateFormat=new DateFormat("yyyy-mm-dd");
		DateFormat   dateFormat2=new DateFormat("MMM-yy");
		WritableCellFormat dateCellFormat_center2=new WritableCellFormat(dateFormat2);
		dateCellFormat_center2.setAlignment(Alignment.CENTRE);
		WritableCellFormat ccellFormat_right=new WritableCellFormat(cnum);
		WritableCellFormat pcellFormat_left=new WritableCellFormat(pnum);
		WritableCellFormat pcellFormat_right=new WritableCellFormat(pnum);
		WritableCellFormat pcellFormat_center=new WritableCellFormat(pnum);
		WritableCellFormat pcellFormat_center2=new WritableCellFormat(pnum);
		WritableCellFormat fcellFormat_left=new WritableCellFormat(fnum);
		WritableCellFormat ccellFormat_left=new WritableCellFormat(cnum);
		WritableCellFormat dateCellFormat_left=new WritableCellFormat(dateFormat);
		WritableCellFormat dateCellFormat_center=new WritableCellFormat(dateFormat);
		ccellFormat_left.setAlignment(Alignment.LEFT);
		pcellFormat_left.setAlignment(Alignment.LEFT);
		pcellFormat_right.setAlignment(Alignment.RIGHT);
		pcellFormat_center.setAlignment(Alignment.CENTRE);
		pcellFormat_center2.setAlignment(Alignment.CENTRE);
		pcellFormat_center2.setVerticalAlignment(VerticalAlignment.CENTRE);		
		fcellFormat_left.setAlignment(Alignment.LEFT);
		dateCellFormat_left.setAlignment(Alignment.LEFT);
		dateCellFormat_center.setAlignment(Alignment.CENTRE);
		WritableCellFormat ncellFormat_center=new WritableCellFormat(nnum);
		ncellFormat_center.setAlignment(Alignment.CENTRE);
		WritableCellFormat ncellFormat_left=new WritableCellFormat(nnum);
		ncellFormat_left.setAlignment(Alignment.LEFT);
		Number number=null;
		//1.0E8 －－100000000
		DecimalFormat   nf2=new   DecimalFormat("###################");	
		
		sheet.setColumnView(0, 10);
		sheet.setColumnView(1, 10);
		sheet.setColumnView(2, 20);
		sheet.setColumnView(3, 80);
		sheet.setColumnView(4, 20);
		sheet.setColumnView(5, 70);
		sheet.setColumnView(6, 40);
		sheet.setColumnView(7, 10);
		sheet.setColumnView(8, 10);
		sheet.setColumnView(9, 10);
		sheet.setColumnView(10, 10);
		sheet.setColumnView(11, 10);
		sheet.setColumnView(12, 15);
		sheet.setColumnView(13, 10);
		sheet.setColumnView(14, 20);
		
		int n=0;
		Label cell = null;
		cell=new Label(0,n,"编号"); 
		sheet.mergeCells(0, n, 0, n); 
		sheet.addCell(cell);
		cell=new Label(1,n,"支付表编号"); 
		sheet.mergeCells(1, n, 1, n); 
		sheet.addCell(cell);
		cell=new Label(2,n,"单据编号"); 
		sheet.mergeCells(2, n, 2, n); 
		sheet.addCell(cell);
		cell=new Label(3,n,"地址电话",format5); 
		sheet.mergeCells(3, n, 3, n); 
		sheet.addCell(cell); 
		cell=new Label(4,n,"税号",format5); 
		sheet.mergeCells(4, n, 4, n); 
		sheet.addCell(cell); 
		cell=new Label(5,n,"开户行帐号",format5); 
		sheet.mergeCells(5, n, 5, n); 
		sheet.addCell(cell); 
		cell=new Label(6,n,"客户名称",format5); 
		sheet.mergeCells(6, n, 6, n); 
		sheet.addCell(cell); 
		cell=new Label(7,n,"产品名称",format5); 
		sheet.mergeCells(7, n,7, n); 
		sheet.addCell(cell); 
		cell=new Label(8,n,"规格型号",format5); 
		sheet.mergeCells(8, n, 8, n); 
		sheet.addCell(cell); 
		cell=new Label(9,n,"单位",format5); 
		sheet.mergeCells(9, n, 9, n); 
		sheet.addCell(cell); 
		cell=new Label(10,n,"数量",format5); 
		sheet.mergeCells(10, n,10, n); 
		sheet.addCell(cell); 
		cell=new Label(11,n,"单价",format5); 
		sheet.mergeCells(11, n,11, n); 
		sheet.addCell(cell); 
			cell=new Label(12,n,"金额",format5); 
			sheet.mergeCells(12, n,12, n); 
			sheet.addCell(cell);
			
			cell=new Label(13,n,"税率",format5); 
			sheet.mergeCells(13, n,13, n); 
			sheet.addCell(cell); 
			
			cell=new Label(14,n,"备注1",format5); 
			sheet.mergeCells(14, n,14, n); 
			sheet.addCell(cell); 
			
			cell=new Label(15,n,"备注2",format5); 
			sheet.mergeCells(15, n,15, n); 
			sheet.addCell(cell);
		sheet.setRowView(n,300,false); 
		n=n+1;		

		int i = 0;
		for (Object object2 : businessTaxList) {
			Map businessTax =(Map)object2;
										
			i = i + 1;	
			cell=new Label(0,n,DataUtil.StringUtil(businessTax.get("RECD_ID")),format5);  
			sheet.addCell(cell);
			cell=new Label(1,n,DataUtil.StringUtil(businessTax.get("RECP_ID")),format5);  
			sheet.addCell(cell);

			cell=new Label(2,n,DataUtil.StringUtil(businessTax.get("RUNNUM")),format5);  
			sheet.addCell(cell);
			cell=new Label(3,n,DataUtil.StringUtil(businessTax.get("LINK_ADDRESS")),format5);  
			sheet.addCell(cell);
			cell=new Label(4,n,DataUtil.StringUtil(businessTax.get("CORP_TAX_CODE")),format5);  
			sheet.addCell(cell);
			cell=new Label(5,n,DataUtil.StringUtil(businessTax.get("BANK_NAME")),format5);  
			sheet.addCell(cell);
			cell=new Label(6,n,DataUtil.StringUtil(businessTax.get("CUST_NAME")),format5);  
			sheet.addCell(cell);
			cell=new Label(7,n,DataUtil.StringUtil(businessTax.get("PRODUCT_NAME")),format5);  
			sheet.addCell(cell);
			cell=new Label(8,n,DataUtil.StringUtil(businessTax.get("PRODUCT_KIND")),format5);  
			sheet.addCell(cell);
			cell=new Label(9,n,DataUtil.StringUtil(businessTax.get("UNIT")),format5);  
			sheet.addCell(cell);
			cell=new Label(10,n,DataUtil.StringUtil(businessTax.get("NUMBER")),format5);  
			sheet.addCell(cell);
			cell=new Label(11,n,DataUtil.StringUtil(businessTax.get("UNIT_PRICE")),format5);  
			sheet.addCell(cell);
			
			DecimalFormat df1=new DecimalFormat("####.00");
				if(businessTax.get("PRICE")!=null){
					number=new Number(12,n,Double.valueOf(df1.format(Double.parseDouble(businessTax.get("PRICE")+""))),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(12,n,0,ccellFormat_right);
					sheet.addCell(number);
				}		
						
				if(businessTax.get("TAX_RATE")!=null){
					number=new Number(13,n,Double.parseDouble(businessTax.get("TAX_RATE")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(13,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
	
				cell=new Label(14,n,DataUtil.StringUtil(businessTax.get("REMARK1")),format5);  
				sheet.addCell(cell);
				
				cell=new Label(15,n,DataUtil.StringUtil(businessTax.get("REMARK2")),format5);  
				sheet.addCell(cell);
			sheet.setRowView(n,300,false);  
			n=n+1;
		}
				
	}catch(Exception e){
		e.printStackTrace();
		LogPrint.getLogStackTrace(e, logger);
	}
	return baos;
}

public ByteArrayOutputStream exportValueAddTaxExcel(List<Map> businessTaxList) {
	WritableSheet sheet = null;
	
	try {
		workbookSettings.setEncoding("ISO-8859-1");
		sheet = wb.createSheet("增值税明细表", 1);
		
		WritableFont font2 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
		WritableCellFormat format2_BOTTOM = new WritableCellFormat(font2);
		format2_BOTTOM.setAlignment(Alignment.CENTRE);
		format2_BOTTOM.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_BOTTOM.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
		format2_BOTTOM.setWrap(true);
		
		WritableCellFormat format2_Top = new WritableCellFormat(font2);
		format2_Top.setAlignment(Alignment.CENTRE);
		format2_Top.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_Top.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
		format2_Top.setWrap(true);
		
		WritableCellFormat format2_LEFT = new WritableCellFormat(font2);
		format2_LEFT.setAlignment(Alignment.CENTRE);
		format2_LEFT.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_LEFT.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
		format2_LEFT.setWrap(true);
		
		WritableCellFormat format2_RIGHT = new WritableCellFormat(font2);
		format2_RIGHT.setAlignment(Alignment.CENTRE);
		format2_RIGHT.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_RIGHT.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
		format2_RIGHT.setWrap(true);
		
		// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
		WritableFont font3 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
		WritableCellFormat format3 = new WritableCellFormat(font3);
		format3.setAlignment(Alignment.LEFT);
		format3.setVerticalAlignment(VerticalAlignment.CENTRE);
		// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
		format3.setWrap(true);
		// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
				 
		WritableCellFormat format5 = new WritableCellFormat(font3);
		format5.setAlignment(Alignment.CENTRE);
		format5.setVerticalAlignment(VerticalAlignment.CENTRE);
		// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
		format5.setWrap(true);
		WritableFont font6 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
		font6.setColour(Colour.BLUE);
		WritableCellFormat format6 = new WritableCellFormat(font6);
		format6.setAlignment(Alignment.LEFT); 
		format6.setVerticalAlignment(VerticalAlignment.CENTRE);
		// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
		format6.setWrap(true);
		
		WritableFont font8 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
		font8.setColour(Colour.BLUE);
		WritableCellFormat format8 = new WritableCellFormat(font8);
		format8.setAlignment(Alignment.LEFT); 
		format8.setVerticalAlignment(VerticalAlignment.CENTRE);
		// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
		format8.setWrap(true);
		
		WritableFont font9 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
		WritableCellFormat format9 = new WritableCellFormat(font9);
		format9.setAlignment(Alignment.LEFT);
		format9.setBackground(jxl.format.Colour.GRAY_25);
		format9.setVerticalAlignment(VerticalAlignment.CENTRE);
		// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
		format9.setWrap(true);
		
		WritableFont font4 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
		WritableCellFormat format4 = new WritableCellFormat(font4);
		format4.setAlignment(Alignment.CENTRE);
		format4.setBackground(jxl.format.Colour.GRAY_25);
		format4.setVerticalAlignment(VerticalAlignment.CENTRE);
		// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
		format4.setWrap(true);
		
		NumberFormat cnum=new NumberFormat("#,##0.00");
		NumberFormat pnum=new NumberFormat("##0.000%");
		NumberFormat fnum=new NumberFormat("##0.00");
		NumberFormat nnum=new NumberFormat("##"); 
		DateFormat   dateFormat=new DateFormat("yyyy-mm-dd");
		DateFormat   dateFormat2=new DateFormat("MMM-yy");
		WritableCellFormat dateCellFormat_center2=new WritableCellFormat(dateFormat2);
		dateCellFormat_center2.setAlignment(Alignment.CENTRE);
		WritableCellFormat ccellFormat_right=new WritableCellFormat(cnum);
		WritableCellFormat pcellFormat_left=new WritableCellFormat(pnum);
		WritableCellFormat pcellFormat_right=new WritableCellFormat(pnum);
		WritableCellFormat pcellFormat_center=new WritableCellFormat(pnum);
		WritableCellFormat pcellFormat_center2=new WritableCellFormat(pnum);
		WritableCellFormat fcellFormat_left=new WritableCellFormat(fnum);
		WritableCellFormat ccellFormat_left=new WritableCellFormat(cnum);
		WritableCellFormat dateCellFormat_left=new WritableCellFormat(dateFormat);
		WritableCellFormat dateCellFormat_center=new WritableCellFormat(dateFormat);
		ccellFormat_left.setAlignment(Alignment.LEFT);
		pcellFormat_left.setAlignment(Alignment.LEFT);
		pcellFormat_right.setAlignment(Alignment.RIGHT);
		pcellFormat_center.setAlignment(Alignment.CENTRE);
		pcellFormat_center2.setAlignment(Alignment.CENTRE);
		pcellFormat_center2.setVerticalAlignment(VerticalAlignment.CENTRE);		
		fcellFormat_left.setAlignment(Alignment.LEFT);
		dateCellFormat_left.setAlignment(Alignment.LEFT);
		dateCellFormat_center.setAlignment(Alignment.CENTRE);
		WritableCellFormat ncellFormat_center=new WritableCellFormat(nnum);
		ncellFormat_center.setAlignment(Alignment.CENTRE);
		WritableCellFormat ncellFormat_left=new WritableCellFormat(nnum);
		ncellFormat_left.setAlignment(Alignment.LEFT);
		Number number=null;
		//1.0E8 －－100000000
		DecimalFormat   nf2=new   DecimalFormat("###################");	
		
		sheet.setColumnView(0, 4);
		sheet.setColumnView(1, 8);
		sheet.setColumnView(2, 40);
		sheet.setColumnView(3, 40);
		sheet.setColumnView(4, 20);
		sheet.setColumnView(5, 10);
		sheet.setColumnView(6, 40);
		sheet.setColumnView(7, 15);
		sheet.setColumnView(8, 15);
		sheet.setColumnView(9, 15);
		sheet.setColumnView(10, 15);
		sheet.setColumnView(11, 15);
		sheet.setColumnView(12, 15);
		sheet.setColumnView(13,20);
		sheet.setColumnView(14,4);
		
		int n=0;
		Label cell = null;
		
		
		cell=new Label(0,0,"");  
		sheet.addCell(cell);
		cell=new Label(1,0,"",format2_BOTTOM);  
		sheet.mergeCells(1, 0,13, 0);
		sheet.addCell(cell);
		n=n+1;
		sheet.setRowView(0,300,false); 
		
		cell=new Label(0,1,""); 
		sheet.addCell(cell);
		cell=new Label(1,1,"增值税明细表",format4); 
		sheet.mergeCells(1, 1, 13, 1); 
		sheet.addCell(cell);
		n=n+1;
		sheet.setRowView(1,320,false); 
		
		cell=new Label(0,n,""); 
		sheet.addCell(cell);
		cell=new Label(1,n,"序号",format5); 
		sheet.mergeCells(1, n, 1, n+1); 
		sheet.addCell(cell); 
		cell=new Label(2,n,"客户名称",format5); 
		sheet.mergeCells(2, n, 2, n+1); 
		sheet.addCell(cell); 
		cell=new Label(3,n,"供应商",format5); 
		sheet.mergeCells(3, n, 3, n+1); 
		sheet.addCell(cell); 
		cell=new Label(4,n,"合同号",format5); 
		sheet.mergeCells(4, n, 4, n+1); 
		sheet.addCell(cell); 
		cell=new Label(5,n,"业务员",format5); 
		sheet.mergeCells(5, n, 5, n+1); 
		sheet.addCell(cell); 
		cell=new Label(6,n,"办事处",format5); 
		sheet.mergeCells(6, n, 6, n+1); 
		sheet.addCell(cell); 
		cell=new Label(7,n,"增值税",format5); 
		sheet.mergeCells(7, n,8, n); 
		sheet.addCell(cell); 
		cell=new Label(9,n,"城建税",format5); 
		sheet.mergeCells(9, n,10, n); 
		sheet.addCell(cell); 
		cell=new Label(11,n,"地区教育费附加",format5); 
		sheet.mergeCells(11, n,12, n); 
		sheet.addCell(cell); 
		cell=new Label(13,n,"小计",format5); 
		sheet.mergeCells(13, n,13, n+1); 
		sheet.addCell(cell); 
		sheet.setRowView(n,300,false); 
		n=n+1;
		
		
		cell=new Label(0,n,""); 
		sheet.addCell(cell);
		cell=new Label(6,n,"计税依据",format5); 
		sheet.addCell(cell);
		cell=new Label(7,n,"税额",format5); 
		sheet.addCell(cell);
		cell=new Label(8,n,"计税依据",format5); 
		sheet.addCell(cell);
		cell=new Label(9,n,"税额",format5); 
		sheet.addCell(cell);
		cell=new Label(10,n,"计税依据",format5); 
		sheet.addCell(cell);
		cell=new Label(11,n,"税额",format5); 
		sheet.addCell(cell);
		n=n+1;
		
		
		double REN_PRICE_sum=0;
		double YYSHUIE_sum=0;
		double CJSHUIE_sum=0;
		double JYSHUIE_sum=0;
		double XIAOJI_sum=0;

		int i = 0;
		for (Object object2 : businessTaxList) {
			Map businessTax =(Map)object2;
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
											
			i = i + 1;						
			number=new Number(1,n,i,ncellFormat_center);
			sheet.addCell(number);
			
			cell=new Label(2,n,DataUtil.StringUtil(businessTax.get("CUST_NAME")),format5);  
			sheet.addCell(cell);
			cell=new Label(3,n,DataUtil.StringUtil(businessTax.get("SUPL_NAME")),format5);  
			sheet.addCell(cell);
			cell=new Label(4,n,DataUtil.StringUtil(businessTax.get("LEASE_CODE")),format5);  
			sheet.addCell(cell);
			cell=new Label(5,n,DataUtil.StringUtil(businessTax.get("NAME")),format5);  
			sheet.addCell(cell);
			cell=new Label(6,n,DataUtil.StringUtil(businessTax.get("DECP_NAME_CN")),format5);  
			sheet.addCell(cell);
					
			if(businessTax.get("REN_PRICE")!=null){
				number=new Number(7,n,Double.parseDouble(businessTax.get("REN_PRICE")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(7,n,0,ccellFormat_right);
				sheet.addCell(number);
			}
			if(businessTax.get("YYSHUIE")!=null){
				number=new Number(8,n,Double.parseDouble(businessTax.get("YYSHUIE")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(8,n,0,ccellFormat_right);
				sheet.addCell(number);
			}
			if(businessTax.get("YYSHUIE")!=null){
				number=new Number(9,n,Double.parseDouble(businessTax.get("YYSHUIE")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(9,n,0,ccellFormat_right);
				sheet.addCell(number);
			}
			if(businessTax.get("CJSHUIE")!=null){
				number=new Number(10,n,Double.parseDouble(businessTax.get("CJSHUIE")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(10,n,0,ccellFormat_right);
				sheet.addCell(number);
			}
			if(businessTax.get("YYSHUIE")!=null){
				number=new Number(11,n,Double.parseDouble(businessTax.get("YYSHUIE")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(11,n,0,ccellFormat_right);
				sheet.addCell(number);
			}
			if(businessTax.get("JYSHUIE")!=null){
				number=new Number(12,n,Double.parseDouble(businessTax.get("JYSHUIE")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(12,n,0,ccellFormat_right);
				sheet.addCell(number);
			}
			if(businessTax.get("XIAOJI")!=null){
				number=new Number(13,n,Double.parseDouble(businessTax.get("XIAOJI")+""),ccellFormat_right);
				sheet.addCell(number);
			}else{
				number=new Number(13,n,0,ccellFormat_right);
				sheet.addCell(number);
			}
						
			sheet.setRowView(n,300,false);  
			n=n+1;
			
			REN_PRICE_sum=REN_PRICE_sum+DataUtil.doubleUtil(businessTax.get("REN_PRICE"));
			YYSHUIE_sum=YYSHUIE_sum+DataUtil.doubleUtil(businessTax.get("YYSHUIE"));
			CJSHUIE_sum=CJSHUIE_sum+DataUtil.doubleUtil(businessTax.get("CJSHUIE"));
			JYSHUIE_sum=JYSHUIE_sum+DataUtil.doubleUtil(businessTax.get("JYSHUIE"));
			XIAOJI_sum=XIAOJI_sum+DataUtil.doubleUtil(businessTax.get("XIAOJI"));
		}
		 
		cell=new Label(0,n,""); 
		sheet.addCell(cell);
		cell=new Label(1,n,"小计",format5);
		sheet.mergeCells(1, n, 2, n);
		sheet.addCell(cell);
		
		number=new Number(7,n,REN_PRICE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(8,n,YYSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(9,n,YYSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(10,n,CJSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(11,n,YYSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(12,n,JYSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(13,n,XIAOJI_sum,ccellFormat_right);
		sheet.addCell(number);

		sheet.setRowView(n,300,false);  
		n=n+1;
		
		
		cell=new Label(0,n,""); 
		sheet.addCell(cell);
		cell=new Label(1,n,"税务申报",format5);
		sheet.mergeCells(1, n, 2, n);
		sheet.addCell(cell);
		number=new Number(7,n,REN_PRICE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(8,n,YYSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(9,n,YYSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(10,n,CJSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(11,n,YYSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(12,n,JYSHUIE_sum,ccellFormat_right);
		sheet.addCell(number);
		number=new Number(13,n,XIAOJI_sum,ccellFormat_right);
		sheet.addCell(number);

		sheet.setRowView(n,300,false);  
		n=n+1;

			
		cell=new Label(0,1,"",format2_RIGHT);
		sheet.mergeCells(0, 1, 0, n-1);
		sheet.addCell(cell);
		cell=new Label(14,1,"",format2_LEFT);
		sheet.mergeCells(14, 1, 14, n-1);
		sheet.addCell(cell);
		cell=new Label(1,n,"",format2_Top);
		sheet.mergeCells(1, n, 13, n); 
		sheet.addCell(cell);
				
	}catch(Exception e){
		e.printStackTrace();
		LogPrint.getLogStackTrace(e, logger);
	}
	return baos;
}

//导出开票资料
public ByteArrayOutputStream exportRentReceiptList(List<Map> receiptList) {
	WritableSheet sheet = null;
	
	try {
		workbookSettings.setEncoding("ISO-8859-1");
		sheet = wb.createSheet("本金收据列表", 1);
		
		WritableFont font2 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
		WritableCellFormat format2_BOTTOM = new WritableCellFormat(font2);
		format2_BOTTOM.setAlignment(Alignment.CENTRE);
		format2_BOTTOM.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_BOTTOM.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
		format2_BOTTOM.setWrap(true);
		
		WritableCellFormat format2_Top = new WritableCellFormat(font2);
		format2_Top.setAlignment(Alignment.CENTRE);
		format2_Top.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_Top.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
		format2_Top.setWrap(true);
		
		WritableCellFormat format2_LEFT = new WritableCellFormat(font2);
		format2_LEFT.setAlignment(Alignment.CENTRE);
		format2_LEFT.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_LEFT.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
		format2_LEFT.setWrap(true);
		
		WritableCellFormat format2_RIGHT = new WritableCellFormat(font2);
		format2_RIGHT.setAlignment(Alignment.CENTRE);
		format2_RIGHT.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_RIGHT.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
		format2_RIGHT.setWrap(true);
		
		// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
		WritableFont font3 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
		WritableCellFormat format3 = new WritableCellFormat(font3);
		format3.setAlignment(Alignment.LEFT);
		format3.setVerticalAlignment(VerticalAlignment.CENTRE);
		// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
		format3.setWrap(true);
		// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
				 
		WritableCellFormat format5 = new WritableCellFormat(font3);
		format5.setAlignment(Alignment.CENTRE);
		format5.setVerticalAlignment(VerticalAlignment.CENTRE);
		format5.setWrap(true);
		WritableFont font6 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
		font6.setColour(Colour.BLUE);
		WritableCellFormat format6 = new WritableCellFormat(font6);
		format6.setAlignment(Alignment.LEFT); 
		format6.setVerticalAlignment(VerticalAlignment.CENTRE);
		format6.setWrap(true);
		
		WritableFont font8 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
		font8.setColour(Colour.BLUE);
		WritableCellFormat format8 = new WritableCellFormat(font8);
		format8.setAlignment(Alignment.LEFT); 
		format8.setVerticalAlignment(VerticalAlignment.CENTRE);
		format8.setWrap(true);
		
		WritableFont font9 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
		WritableCellFormat format9 = new WritableCellFormat(font9);
		format9.setAlignment(Alignment.LEFT);
		format9.setBackground(jxl.format.Colour.GRAY_25);
		format9.setVerticalAlignment(VerticalAlignment.CENTRE);
		format9.setWrap(true);
		
		WritableFont font4 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
		WritableCellFormat format4 = new WritableCellFormat(font4);
		format4.setAlignment(Alignment.CENTRE);
		format4.setBackground(jxl.format.Colour.GRAY_25);
		format4.setVerticalAlignment(VerticalAlignment.CENTRE);
		format4.setWrap(true);
					
		NumberFormat cnum=new NumberFormat("#,##0.00");
		NumberFormat pnum=new NumberFormat("##0.000%");
		NumberFormat fnum=new NumberFormat("##0.00");
		NumberFormat nnum=new NumberFormat("##"); 
		DateFormat   dateFormat=new DateFormat("yyyy-mm-dd");
		DateFormat   dateFormat2=new DateFormat("MMM-yy");
		WritableCellFormat dateCellFormat_center2=new WritableCellFormat(dateFormat2);
		dateCellFormat_center2.setAlignment(Alignment.CENTRE);
		WritableCellFormat ccellFormat_right=new WritableCellFormat(cnum);
		WritableCellFormat pcellFormat_left=new WritableCellFormat(pnum);
		WritableCellFormat pcellFormat_right=new WritableCellFormat(pnum);
		WritableCellFormat pcellFormat_center=new WritableCellFormat(pnum);
		WritableCellFormat pcellFormat_center2=new WritableCellFormat(pnum);
		WritableCellFormat fcellFormat_left=new WritableCellFormat(fnum);
		WritableCellFormat ccellFormat_left=new WritableCellFormat(cnum);
		WritableCellFormat dateCellFormat_left=new WritableCellFormat(dateFormat);
		WritableCellFormat dateCellFormat_center=new WritableCellFormat(dateFormat);
		ccellFormat_left.setAlignment(Alignment.LEFT);
		pcellFormat_left.setAlignment(Alignment.LEFT);
		pcellFormat_right.setAlignment(Alignment.RIGHT);
		pcellFormat_center.setAlignment(Alignment.CENTRE);
		pcellFormat_center2.setAlignment(Alignment.CENTRE);
		pcellFormat_center2.setVerticalAlignment(VerticalAlignment.CENTRE);		
		fcellFormat_left.setAlignment(Alignment.LEFT);
		dateCellFormat_left.setAlignment(Alignment.LEFT);
		dateCellFormat_center.setAlignment(Alignment.CENTRE);
		WritableCellFormat ncellFormat_center=new WritableCellFormat(nnum);
		ncellFormat_center.setAlignment(Alignment.CENTRE);
		WritableCellFormat ncellFormat_left=new WritableCellFormat(nnum);
		ncellFormat_left.setAlignment(Alignment.LEFT);
		Number number=null;
		//1.0E8 －－100000000
		DecimalFormat   nf2=new   DecimalFormat("###################");	
		
		sheet.setColumnView(0, 10);
		sheet.setColumnView(1, 40);
		sheet.setColumnView(2, 20);
		sheet.setColumnView(3, 10);
		sheet.setColumnView(4, 20);
		sheet.setColumnView(5, 20);
		sheet.setColumnView(6, 20);
		sheet.setColumnView(7, 20);
		
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		
		int n=0;
		Label cell = null;
		cell=new Label(0,n,"序号"); 
		sheet.mergeCells(0, n, 0, n); 
		sheet.addCell(cell);
		cell=new Label(1,n,"客户名称"); 
		sheet.mergeCells(1, n, 1, n); 
		sheet.addCell(cell);
		cell=new Label(2,n,"合同编号"); 
		sheet.mergeCells(2, n, 2, n); 
		sheet.addCell(cell);
		cell=new Label(3,n,"还款期数",format5); 
		sheet.mergeCells(3, n, 3, n); 
		sheet.addCell(cell); 
		cell=new Label(4,n,"收据金额",format5); 
		sheet.mergeCells(4, n, 4, n); 
		sheet.addCell(cell); 
		cell=new Label(5,n,"收据编号",format5); 
		sheet.mergeCells(5, n, 5, n); 
		sheet.addCell(cell); 
		cell=new Label(6,n,"原始收据编号",format5); 
		sheet.mergeCells(6, n, 6, n); 
		sheet.addCell(cell); 
		cell=new Label(7,n,"生成日期",format5); 
		sheet.mergeCells(7, n,7, n); 
		sheet.addCell(cell); 
		
		sheet.setRowView(n,300,false); 
		n=n+1;		

		int i = 0;
		for (Object object2 : receiptList) {
			Map receiptMap =(Map)object2;
										
			i = i + 1;	
			cell=new Label(0,n,DataUtil.StringUtil(i),format5);  
			sheet.addCell(cell);
			cell=new Label(1,n,DataUtil.StringUtil(receiptMap.get("CUSTNAME")),format5);  
			sheet.addCell(cell);
			cell=new Label(2,n,DataUtil.StringUtil(receiptMap.get("LEASECODE")),format5);  
			sheet.addCell(cell);
			cell=new Label(3,n,DataUtil.StringUtil(receiptMap.get("RECDPERIOD")),format5);  
			sheet.addCell(cell);
			cell=new Label(4,n,DataUtil.StringUtil(receiptMap.get("REALOWNPRICE")),format5);  
			sheet.addCell(cell);
			cell=new Label(5,n,DataUtil.StringUtil(receiptMap.get("PRINCIPALRUNCODE")),format5);  
			sheet.addCell(cell);
			cell=new Label(6,n,DataUtil.StringUtil(receiptMap.get("ORIPRINCIPALRUNCODE")),format5);  
			sheet.addCell(cell);
			cell=new Label(7,n,DataUtil.StringUtil(sf.format(receiptMap.get("CREATEDATE"))),format5);  
			sheet.addCell(cell);
									
			sheet.setRowView(n,300,false);  
			n=n+1;
		}
				
	}catch(Exception e){
		e.printStackTrace();
		LogPrint.getLogStackTrace(e, logger);
	}
	return baos;
}

//导出留购款联系人
public ByteArrayOutputStream stayBuyPriceTaxLinkerInfoToExcel(List<Map> businessTaxList) {
	WritableSheet sheet = null;
	
	try {
		workbookSettings.setEncoding("ISO-8859-1");
		sheet = wb.createSheet("留购款客户联系人", 1);
		
		WritableFont font2 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
		WritableCellFormat format2_BOTTOM = new WritableCellFormat(font2);
		format2_BOTTOM.setAlignment(Alignment.CENTRE);
		format2_BOTTOM.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_BOTTOM.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
		format2_BOTTOM.setWrap(true);
		
		WritableCellFormat format2_Top = new WritableCellFormat(font2);
		format2_Top.setAlignment(Alignment.CENTRE);
		format2_Top.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_Top.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
		format2_Top.setWrap(true);
		
		WritableCellFormat format2_LEFT = new WritableCellFormat(font2);
		format2_LEFT.setAlignment(Alignment.CENTRE);
		format2_LEFT.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_LEFT.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
		format2_LEFT.setWrap(true);
		
		WritableCellFormat format2_RIGHT = new WritableCellFormat(font2);
		format2_RIGHT.setAlignment(Alignment.CENTRE);
		format2_RIGHT.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2_RIGHT.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
		format2_RIGHT.setWrap(true);
		
		// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
		WritableFont font3 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
		WritableCellFormat format3 = new WritableCellFormat(font3);
		format3.setAlignment(Alignment.LEFT);
		format3.setVerticalAlignment(VerticalAlignment.CENTRE);
		// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
		format3.setWrap(true);
		// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
				 
		WritableCellFormat format5 = new WritableCellFormat(font3);
		format5.setAlignment(Alignment.CENTRE);
		format5.setVerticalAlignment(VerticalAlignment.CENTRE);
		format5.setWrap(true);
		WritableFont font6 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
		font6.setColour(Colour.BLUE);
		WritableCellFormat format6 = new WritableCellFormat(font6);
		format6.setAlignment(Alignment.LEFT); 
		format6.setVerticalAlignment(VerticalAlignment.CENTRE);
		format6.setWrap(true);
		
		WritableFont font8 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
		font8.setColour(Colour.BLUE);
		WritableCellFormat format8 = new WritableCellFormat(font8);
		format8.setAlignment(Alignment.LEFT); 
		format8.setVerticalAlignment(VerticalAlignment.CENTRE);
		format8.setWrap(true);
		
		WritableFont font9 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
		WritableCellFormat format9 = new WritableCellFormat(font9);
		format9.setAlignment(Alignment.LEFT);
		format9.setBackground(jxl.format.Colour.GRAY_25);
		format9.setVerticalAlignment(VerticalAlignment.CENTRE);
		format9.setWrap(true);
		
		WritableFont font4 = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
		WritableCellFormat format4 = new WritableCellFormat(font4);
		format4.setAlignment(Alignment.CENTRE);
		format4.setBackground(jxl.format.Colour.GRAY_25);
		format4.setVerticalAlignment(VerticalAlignment.CENTRE);
		format4.setWrap(true);
					
		NumberFormat cnum=new NumberFormat("#,##0.00");
		NumberFormat pnum=new NumberFormat("##0.000%");
		NumberFormat fnum=new NumberFormat("##0.00");
		NumberFormat nnum=new NumberFormat("##"); 
		DateFormat   dateFormat=new DateFormat("yyyy-mm-dd");
		DateFormat   dateFormat2=new DateFormat("MMM-yy");
		WritableCellFormat dateCellFormat_center2=new WritableCellFormat(dateFormat2);
		dateCellFormat_center2.setAlignment(Alignment.CENTRE);
		WritableCellFormat ccellFormat_right=new WritableCellFormat(cnum);
		WritableCellFormat pcellFormat_left=new WritableCellFormat(pnum);
		WritableCellFormat pcellFormat_right=new WritableCellFormat(pnum);
		WritableCellFormat pcellFormat_center=new WritableCellFormat(pnum);
		WritableCellFormat pcellFormat_center2=new WritableCellFormat(pnum);
		WritableCellFormat fcellFormat_left=new WritableCellFormat(fnum);
		WritableCellFormat ccellFormat_left=new WritableCellFormat(cnum);
		WritableCellFormat dateCellFormat_left=new WritableCellFormat(dateFormat);
		WritableCellFormat dateCellFormat_center=new WritableCellFormat(dateFormat);
		ccellFormat_left.setAlignment(Alignment.LEFT);
		pcellFormat_left.setAlignment(Alignment.LEFT);
		pcellFormat_right.setAlignment(Alignment.RIGHT);
		pcellFormat_center.setAlignment(Alignment.CENTRE);
		pcellFormat_center2.setAlignment(Alignment.CENTRE);
		pcellFormat_center2.setVerticalAlignment(VerticalAlignment.CENTRE);		
		fcellFormat_left.setAlignment(Alignment.LEFT);
		dateCellFormat_left.setAlignment(Alignment.LEFT);
		dateCellFormat_center.setAlignment(Alignment.CENTRE);
		WritableCellFormat ncellFormat_center=new WritableCellFormat(nnum);
		ncellFormat_center.setAlignment(Alignment.CENTRE);
		WritableCellFormat ncellFormat_left=new WritableCellFormat(nnum);
		ncellFormat_left.setAlignment(Alignment.LEFT);
		Number number=null;
		//1.0E8 －－100000000
		DecimalFormat   nf2=new   DecimalFormat("###################");	
		
		sheet.setColumnView(0, 4);
		sheet.setColumnView(1, 40);
		sheet.setColumnView(2, 20);
		sheet.setColumnView(3, 30);
		sheet.setColumnView(4, 30);
		sheet.setColumnView(5, 40);
		sheet.setColumnView(6, 60);
		sheet.setColumnView(7, 60);
		sheet.setColumnView(8, 60);

		int n=0;
		Label cell = null;

		cell=new Label(0,n,"序号",format5); 
		sheet.addCell(cell); 
		cell=new Label(1,n,"客户名称",format5); 
		sheet.addCell(cell); 
		cell=new Label(2,n,"联系人",format5); 
		sheet.addCell(cell); 
		cell=new Label(3,n,"手机",format5); 
		sheet.addCell(cell); 
		cell=new Label(4,n,"固话",format5); 
		sheet.addCell(cell); 
		cell=new Label(5,n,"Email",format5); 
		sheet.addCell(cell); 
		cell=new Label(6,n,"邮寄地址",format5); 
		sheet.addCell(cell); 
		cell=new Label(7,n,"快递方式",format5); 
		sheet.addCell(cell); 
		cell=new Label(8,n,"快递付款方式",format5); 
		sheet.addCell(cell); 
		
		sheet.setRowView(n,300,false); 
		n=n+1;
		List express_pay_way = DictionaryUtil.getDictionary("快递付款方式");
		int i = 0;
		for (Object object2 : businessTaxList) {
			Map businessTax =(Map)object2;
											
			i = i + 1;						
			number=new Number(0,n,i,ncellFormat_center);
			sheet.addCell(number);
			
			cell=new Label(1,n,DataUtil.StringUtil(businessTax.get("CUST_NAME")),format5);  
			sheet.addCell(cell);
			cell=new Label(2,n,DataUtil.StringUtil(businessTax.get("LINK_NAME")),format5);  
			sheet.addCell(cell);
			cell=new Label(3,n,DataUtil.StringUtil(businessTax.get("LINK_MOBILE")),format5);  
			sheet.addCell(cell);
			cell=new Label(4,n,DataUtil.StringUtil(businessTax.get("LINK_PHONE")),format5);  
			sheet.addCell(cell);
			cell=new Label(5,n,DataUtil.StringUtil(businessTax.get("LINK_EMAIL")),format5);  
			sheet.addCell(cell);
			cell=new Label(6,n,DataUtil.StringUtil(businessTax.get("LINK_WORK_ADDRESS")),format5);  
			sheet.addCell(cell);
			
			String decpName = (String) businessTax.get("DECP_NAME_CN");
			
			String express = (String) businessTax.get("EXPRESS");
			if(express==null){
				express = "顺丰";
				if("苏州总公司".equals(decpName)||"昆山设备".equals(decpName)
						||"南京设备".equals(decpName)
						||"上海设备".equals(decpName)
						||"上海商用车".equals(decpName)
						||"苏州商用车".equals(decpName)
						||"苏州设备".equals(decpName)
						||"苏州小车".equals(decpName)
						||"苏州乘用车".equals(decpName)
						||"上海乘用车".equals(decpName)
						||"杭州乘用车".equals(decpName)
						||"宁波设备".equals(decpName)){
					express = "汇通";
				}
			}
			cell=new Label(7,n,DataUtil.StringUtil(express),format5);  
			sheet.addCell(cell);
			String expressPayWay = businessTax.get("EXPRESS_PAY_WAY")!=null?String.valueOf(businessTax.get("EXPRESS_PAY_WAY")):"2" ;
			String payWay = "";
			for(int index =0,j=express_pay_way.size();index<j;index++){
				Map payway = (Map) express_pay_way.get(index);
				if(expressPayWay.equals(payway.get("CODE"))){
					payWay = (String) payway.get("FLAG");
				}
			}
			cell=new Label(8,n,DataUtil.StringUtil(payWay),format5);  
			sheet.addCell(cell);
			
			sheet.setRowView(n,300,false);  
			n=n+1;
			
		}
					
	}catch(Exception e){
		e.printStackTrace();
		LogPrint.getLogStackTrace(e, logger);
	}
	return baos;
}
	public ByteArrayOutputStream exportInsuranceReportForCar(List list,Double total){
		WritableSheet sheet = null;
		
		try {
			workbookSettings.setEncoding("ISO-8859-1");
			sheet = wb.createSheet("新车委贷开票明细表", 0);
			
			
			
			WritableFont font = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
			WritableCellFormat format = new WritableCellFormat(font);
			format.setAlignment(Alignment.CENTRE);
			format.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
			format.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
			format.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
			format.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
			format.setWrap(true);
			

			WritableFont font2 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);	
			WritableCellFormat format2 = new WritableCellFormat(font2);
			format2.setAlignment(Alignment.CENTRE);
			format2.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
			format2.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
			format2.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
			format2.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
			format2.setWrap(true);
						

			WritableCellFormat format3 = new WritableCellFormat(font2);
			format3.setAlignment(Alignment.RIGHT);
			format3.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format3.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
			format3.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
			format3.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
			format3.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
			format3.setWrap(true);
		
			sheet.setColumnView(0, 20);
			sheet.setColumnView(1, 20);
			sheet.setColumnView(2, 10);
			sheet.setColumnView(3, 20);
			sheet.setColumnView(4, 20);
			sheet.setColumnView(5, 20);
			
			int n=0;
			Label cell = null;
			cell=new Label(0,n,"新车委贷开票明细表",format); 
			sheet.mergeCells(0, n, 5, n); 
			sheet.addCell(cell);
			
			n++;
			cell=new Label(0,n,"合同号",format); 
			sheet.mergeCells(0, n, 0, n); 
			sheet.addCell(cell);
			
			cell=new Label(1,n,"客户名称",format); 
			sheet.mergeCells(1, n, 1, n); 
			sheet.addCell(cell);
			
			cell=new Label(2,n,"期数",format); 
			sheet.mergeCells(2, n, 2, n); 
			sheet.addCell(cell);
			
			cell=new Label(3,n,"支付时间",format); 
			sheet.mergeCells(3, n, 3, n); 
			sheet.addCell(cell); 
			
			cell=new Label(4,n,"利息",format); 
			sheet.mergeCells(4, n, 4, n); 
			sheet.addCell(cell); 
			
			cell=new Label(5,n,"区域办事处",format); 
			sheet.mergeCells(5, n, 5, n); 
			sheet.addCell(cell); 
			
			n++;		

			int i = 0;
			for (Object object2 : list) {
				Map receiptMap =(Map)object2;
											
				i = i + 1;	
				cell=new Label(0,n,DataUtil.StringUtil(receiptMap.get("LEASE_CODE")),format2);  
				sheet.addCell(cell);
				cell=new Label(1,n,DataUtil.StringUtil(receiptMap.get("CUST_NAME")),format2);  
				sheet.addCell(cell);
				cell=new Label(2,n,DataUtil.StringUtil(receiptMap.get("PERIOD_NUM")),format2);  
				sheet.addCell(cell);
				cell=new Label(3,n,DataUtil.StringUtil(receiptMap.get("PAY_DATE")),format2);  
				sheet.addCell(cell);
				Double renPrice = (Double) receiptMap.get("REN_PRICE");
				jxl.write.Number numberLabel1  =   new  jxl.write.Number(4, n ,renPrice.doubleValue(),format3); 			
				sheet.addCell(numberLabel1);
				
				cell=new Label(5,n,DataUtil.StringUtil(receiptMap.get("DECP_NAME_CN")),format2);  
				sheet.addCell(cell);									
				n++;
			}
			cell=new Label(0,n,"合计",format2);  
			sheet.addCell(cell);
			
			cell=new Label(1,n,list.size()+"个",format2);  
			sheet.addCell(cell);
			
			jxl.write.Number numberLabel1  =   new  jxl.write.Number(4, n ,total.doubleValue(),format3); 			
			sheet.addCell(numberLabel1);
		
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		return baos;
	}
	
	
	
	public ByteArrayOutputStream exportServiceChargeReportForCar(List list,Double total){
		WritableSheet sheet = null;
		
		try {
			workbookSettings.setEncoding("ISO-8859-1");
			sheet = wb.createSheet("新车委贷手续费开票明细表", 0);
			
			
			
			WritableFont font = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
			WritableCellFormat format = new WritableCellFormat(font);
			format.setAlignment(Alignment.CENTRE);
			format.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
			format.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
			format.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
			format.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
			format.setWrap(true);
			

			WritableFont font2 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);	
			WritableCellFormat format2 = new WritableCellFormat(font2);
			format2.setAlignment(Alignment.CENTRE);
			format2.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
			format2.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
			format2.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
			format2.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
			format2.setWrap(true);
						

			WritableCellFormat format3 = new WritableCellFormat(font2);
			format3.setAlignment(Alignment.RIGHT);
			format3.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format3.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
			format3.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
			format3.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
			format3.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
			format3.setWrap(true);
		
			sheet.setColumnView(0, 20);
			sheet.setColumnView(1, 20);
			sheet.setColumnView(2, 20);
			sheet.setColumnView(3, 20);
			sheet.setColumnView(4, 20);
			
			int n=0;
			Label cell = null;
			cell=new Label(0,n,"新车委贷手续费开票明细表",format); 
			sheet.mergeCells(0, n, 5, n); 
			sheet.addCell(cell);
			
			n++;
			cell=new Label(0,n,"合同号",format); 
			sheet.mergeCells(0, n, 0, n); 
			sheet.addCell(cell);
			
			cell=new Label(1,n,"客户名称",format); 
			sheet.mergeCells(1, n, 1, n); 
			sheet.addCell(cell);
			
			cell=new Label(2,n,"手续费类型",format); 
			sheet.mergeCells(2, n, 2, n); 
			sheet.addCell(cell);
			
			cell=new Label(3,n,"手续费",format); 
			sheet.mergeCells(3, n, 3, n); 
			sheet.addCell(cell); 
			
			
			cell=new Label(4,n,"区域办事处",format); 
			sheet.mergeCells(4, n, 4, n); 
			sheet.addCell(cell); 
			
			n++;		

			int i = 0;
			for (Object object2 : list) {
				Map receiptMap =(Map)object2;
											
				i = i + 1;	
				cell=new Label(0,n,DataUtil.StringUtil(receiptMap.get("LEASE_CODE")),format2);  
				sheet.addCell(cell);
				cell=new Label(1,n,DataUtil.StringUtil(receiptMap.get("CUST_NAME")),format2);  
				sheet.addCell(cell);
				cell=new Label(2,n,DataUtil.StringUtil(getServiceChargeType((Integer)receiptMap.get("INCOME_PAY_TYPE"))),format2);  
				sheet.addCell(cell);
				Double renPrice = (Double) receiptMap.get("INCOME_PAY");
				jxl.write.Number numberLabel1  =   new  jxl.write.Number(3, n ,renPrice.doubleValue(),format3); 			
				sheet.addCell(numberLabel1);
				
				cell=new Label(4,n,DataUtil.StringUtil(receiptMap.get("DECP_NAME_CN")),format2);  
				sheet.addCell(cell);									
				n++;
			}
			cell=new Label(0,n,"合计",format2);  
			sheet.addCell(cell);
		
			
			jxl.write.Number numberLabel1  =   new  jxl.write.Number(3, n ,total.doubleValue(),format3); 			
			sheet.addCell(numberLabel1);
		
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		return baos;
	}
	
	private String getServiceChargeType(int type){
		String str ="";
		switch(type){
			case 1:
				str = "无";
				break;
			case 2:
				str = "非月结";
				break;
			case 3:
				str = "月结";
				break;
		}
		return str;
	}
	private String toString(Map map,String key){
		return map.get(key)==null?" ":map.get(key).toString();
	}

}
