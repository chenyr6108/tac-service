package com.brick.activitiesLog.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import com.brick.log.service.LogPrint;


public class ActivitiesStatisticTotalExcel {
	 Log logger = LogFactory.getLog(ActivitiesStatisticTotalExcel.class);
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


	public ByteArrayOutputStream exportactivitTotalExcel(Map content) {
		WritableSheet sheet = null;
		if(content == null){
			content = new HashMap() ;
		}
		try {
			workbookSettings.setEncoding("ISO-8859-1");
			sheet = wb.createSheet("每日业务活动统计表", 1);
			
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
			sheet.setColumnView(1, 15);
			sheet.setColumnView(2, 10);
			sheet.setColumnView(3, 10);
			sheet.setColumnView(4, 10);
			sheet.setColumnView(5, 10);
			sheet.setColumnView(6, 10);
			sheet.setColumnView(7, 10);
			sheet.setColumnView(8, 10);
			sheet.setColumnView(9, 10);
			sheet.setColumnView(10, 10);
			sheet.setColumnView(11, 10);
			sheet.setColumnView(12, 10);
			sheet.setColumnView(13, 10);
			sheet.setColumnView(14, 20);
			sheet.setColumnView(15, 20);
			sheet.setColumnView(16, 4);
						
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
			cell=new Label(1,1,"每日业务活动统计",format4); 
			sheet.mergeCells(1, 1, 15, 1); 
			sheet.addCell(cell);
			n=n+1;
			sheet.setRowView(1,320,false); 
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(1,n,content.get("year") == null ? "" : content.get("year").toString(),format5); 
			sheet.mergeCells(1, n, 1, n); 
			sheet.addCell(cell); 
			cell=new Label(2,n,"外出访客数",format5); 
			sheet.mergeCells(2, n, 6, n); 
			sheet.addCell(cell); 
			cell=new Label(7,n,"有望客户数",format5); 
			sheet.mergeCells(7, n,9, n); 
			sheet.addCell(cell); 
			cell=new Label(10,n,"报价数",format5); 
			sheet.mergeCells(10, n,10, n+1); 
			sheet.addCell(cell); 
			cell=new Label(11,n,"送件数",format5); 
			sheet.mergeCells(11, n,11, n+1); 
			sheet.addCell(cell); 
			cell=new Label(12,n,"签约数",format5); 
			sheet.mergeCells(12, n,12, n+1); 
			sheet.addCell(cell); 
			cell=new Label(13,n,"入保证金",format5); 
			sheet.mergeCells(13, n,13, n+1); 
			sheet.addCell(cell); 
			cell=new Label(14,n,"当日动拨金额",format5); 
			sheet.mergeCells(14, n,14, n+1); 
			sheet.addCell(cell); 
			cell=new Label(15,n,"当月累计动发金额",format5); 
			sheet.mergeCells(15, n,15, n+1); 
			sheet.addCell(cell); 
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(1,n,"日期",format5); 
			sheet.addCell(cell);
			cell=new Label(2,n,"首次拜访",format5); 
			sheet.addCell(cell);
			cell=new Label(3,n,"勘厂",format5); 
			sheet.addCell(cell);
			
			cell=new Label(4,n,"客户服务",format5); 
			sheet.addCell(cell);
			cell=new Label(5,n,"经销商拜访",format5); 
			sheet.addCell(cell);
			cell=new Label(6,n,"回访",format5); 
			sheet.addCell(cell);
			
			cell=new Label(7,n," H ",format5); 
			sheet.addCell(cell);
			cell=new Label(8,n," A ",format5); 
			sheet.addCell(cell);
			cell=new Label(9,n," B ",format5); 
			sheet.addCell(cell);
			n=n+1;
			


			int i = 0;
			for (Object object2 : (List)content.get("activitTotalList")) {
				Map activitTotal =(Map)object2;
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
												
				i = i + 1;						
				cell=new Label(1,n,DataUtil.StringUtil(activitTotal.get("CREATE_DATE")),format5);  
				sheet.addCell(cell);
				cell=new Label(2,n,DataUtil.StringUtil(activitTotal.get("CREATECOUNT")),format5);  
				sheet.addCell(cell);
				cell=new Label(3,n,DataUtil.StringUtil(activitTotal.get("VISITFACTORYCOUNT")),format5);  
				sheet.addCell(cell);
				cell=new Label(4,n,DataUtil.StringUtil(activitTotal.get("FUHECOUNT")),format5);  
				sheet.addCell(cell);
				//期初余额	
				if(activitTotal.get("CUSTVISITCOUNT")!=null){
					cell=new Label(5,n,DataUtil.StringUtil(activitTotal.get("CUSTVISITCOUNT")),format5);  
					sheet.addCell(cell);
				}else{
					cell=new Label(5,n,"0",format5);  
					sheet.addCell(cell);
				}
				if(activitTotal.get("BACKVISITCOUNT")!=null){
					cell=new Label(6,n,DataUtil.StringUtil(activitTotal.get("BACKVISITCOUNT")),format5);  
					sheet.addCell(cell);
				}else{
					cell=new Label(6,n,"0",format5);  
					sheet.addCell(cell);
				}
				if(activitTotal.get("HCOUNT")!=null){
					cell=new Label(7,n,DataUtil.StringUtil(activitTotal.get("HCOUNT")),format5);  
					sheet.addCell(cell);
				}else{
					cell=new Label(7,n,"0",format5);  
					sheet.addCell(cell);
				}
				if(activitTotal.get("ACOUNT")!=null){
					cell=new Label(8,n,DataUtil.StringUtil(activitTotal.get("ACOUNT")),format5);  
					sheet.addCell(cell);
				}else{
					cell=new Label(8,n,"0",format5);  
					sheet.addCell(cell);
				}
				if(activitTotal.get("BCOUNT")!=null){
					cell=new Label(9,n,DataUtil.StringUtil(activitTotal.get("BCOUNT")),format5);  
					sheet.addCell(cell);
				}else{
					cell=new Label(9,n,"0",format5);  
					sheet.addCell(cell);
				}
				if(activitTotal.get("FIRSTPRICECOUNT")!=null){
					cell=new Label(10,n,DataUtil.StringUtil(activitTotal.get("FIRSTPRICECOUNT")),format5);  
					sheet.addCell(cell);
				}else{
					cell=new Label(10,n,"0",format5);  
					sheet.addCell(cell);
				}
				if(activitTotal.get("SENDCOUNT")!=null){
					cell=new Label(11,n,DataUtil.StringUtil(activitTotal.get("SENDCOUNT")),format5);  
					sheet.addCell(cell);
				}else{
					cell=new Label(11,n,"0",format5);  
					sheet.addCell(cell);
				}
				if(activitTotal.get("SHENGHECOUNT")!=null){
					cell=new Label(12,n,DataUtil.StringUtil(activitTotal.get("SHENGHECOUNT")),format5);  
					sheet.addCell(cell);
				}else{
					cell=new Label(12,n,"0",format5);  
					sheet.addCell(cell);
				}
				if(activitTotal.get("MARGINCOUNT")!=null){
					cell=new Label(13,n,DataUtil.StringUtil(activitTotal.get("MARGINCOUNT")),format5);  
					sheet.addCell(cell);
				}else{
					cell=new Label(13,n,"0",format5);  
					sheet.addCell(cell);
				}
				if(activitTotal.get("PAY_MONEYDAYCOUNT")!=null){
					number=new Number(14,n,Double.parseDouble(activitTotal.get("PAY_MONEYDAYCOUNT")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(14,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
				if(activitTotal.get("PAY_MONEYMONTHCOUNT")!=null){
					number=new Number(15,n,Double.parseDouble(activitTotal.get("PAY_MONEYMONTHCOUNT")+""),ccellFormat_right);
					sheet.addCell(number);
				}else{
					number=new Number(15,n,0,ccellFormat_right);
					sheet.addCell(number);
				}
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
			sheet.mergeCells(1, n, 15, n); 
			sheet.addCell(cell);
					
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		return baos;
	}

@SuppressWarnings("unchecked")
private String toString(Map map,String key){
	return map.get(key)==null?" ":map.get(key).toString();
}

}
