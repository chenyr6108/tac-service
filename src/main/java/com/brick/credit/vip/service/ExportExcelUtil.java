package com.brick.credit.vip.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.brick.util.DataUtil;
import com.brick.util.DateUtil;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Border;
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
/**
 * 导出财务报表 
 * @author Administrator
 *
 */
public class ExportExcelUtil {
	Log logger = LogFactory.getLog(ExportExcelUtil.class);
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

	public ByteArrayOutputStream exportExcel(Map<String, Map> exportMap) {
		Map obj1 = new HashMap();
		Map obj2 = new HashMap();
		Map obj3 = new HashMap();
		Map bili1 = new HashMap();
		Map bili2 = new HashMap();
		Map bili3 = new HashMap();
		Map chayi12 = new HashMap();
		Map chayi23 = new HashMap();
		
		obj1 = exportMap.get("obj1");
		obj2 = exportMap.get("obj2");
		obj3 = exportMap.get("obj3");
		
		bili1 = exportMap.get("bili1");
		bili2 = exportMap.get("bili2");
		bili3 = exportMap.get("bili3");
		
		chayi12 = exportMap.get("chayi12");
		chayi23 = exportMap.get("chayi23");
			
		WritableSheet sheet = null;
		
		try {
			workbookSettings.setEncoding("ISO-8859-1");
			sheet = wb.createSheet("BSPL-1", 1);
			
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
			
			
			// 设置列宽
			sheet.setColumnView(0, 4);
			sheet.setColumnView(1, 20);
			sheet.setColumnView(2, 15);
			sheet.setColumnView(3, 15);
			sheet.setColumnView(4, 15);
			sheet.setColumnView(5, 15);
			sheet.setColumnView(6, 15);
			sheet.setColumnView(7, 15);
			sheet.setColumnView(8, 15);
			sheet.setColumnView(9, 15);
			sheet.setColumnView(10,4);

						
			NumberFormat cnum=new NumberFormat("#,##0.0");
			NumberFormat pnum=new NumberFormat("##0.00%");
			NumberFormat fnum=new NumberFormat("##0.00");
			NumberFormat nnum=new NumberFormat("##"); 
			DateFormat   dateFormat=new DateFormat("yyyy-mm-dd");
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
			DecimalFormat   nf2=new   DecimalFormat("#DIV/0");	
			
			///////////////////////////////////////////////////////////////////////////
			int n=0;
			Label cell = null;
			
			cell=new Label(0,0,"");  
			sheet.addCell(cell);
			cell=new Label(1,0,"",format2_BOTTOM);  
			sheet.mergeCells(1, 0, 9, 0);
			sheet.addCell(cell);
			sheet.setRowView(0,300,false); 
			n=n+1;
			
			cell=new Label(0,1,""); 
			sheet.addCell(cell);
			cell=new Label(1,1,"项目名称",format4); 
			sheet.addCell(cell);
			cell=new Label(2,1,"财务报表资产负债表暨水平分析(年度) ",format4); 
			sheet.mergeCells(2, 1, 8, 1);
			sheet.addCell(cell);
			cell=new Label(9,1,"单位（千元）",format4); 
			sheet.addCell(cell);
			sheet.setRowView(1,320,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"科目\\日期 ",format3); 
			sheet.addCell(cell); 
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
			Date PROJECT_ITEM1=df.parse(obj1.get("PROJECT_ITEM")+"");
			DateTime dateLabel=new DateTime(2,n, PROJECT_ITEM1,dateCellFormat_left);
			sheet.addCell(dateLabel);
			cell=new Label(3,n,"",format3); 
			sheet.addCell(cell); 
			cell=new Label(4,n,"两期差异",format3); 
			sheet.addCell(cell);
			Date PROJECT_ITEM2=df.parse(obj2.get("PROJECT_ITEM")+"");
			DateTime dateLabe2=new DateTime(5,n, PROJECT_ITEM2,dateCellFormat_left);
			sheet.addCell(dateLabe2);
			cell=new Label(6,n,"",format3); 
			sheet.addCell(cell); 
			cell=new Label(7,n,"两期差异",format3); 
			sheet.addCell(cell);
			Date PROJECT_ITEM3=df.parse(obj3.get("PROJECT_ITEM")+"");
			DateTime dateLabe3=new DateTime(8,n, PROJECT_ITEM3,dateCellFormat_left);
			sheet.addCell(dateLabe3);
			cell=new Label(9,n,"",format3); 
			sheet.addCell(cell); 
			sheet.setRowView(n,300,false); 
			n=n+1;
						
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"现金及约当现金 ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("CA_CASH_PRICE")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("ca_cash_price_bili")+""))/100,pcellFormat_right);
			//number=new Number(3,n,Double.parseDouble(bili1.get("ca_cash_price_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("ca_cash_price_bili")+"")),ccellFormat_right);
			//number=new Number(4,n,Double.parseDouble(chayi12.get("ca_cash_price_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("CA_CASH_PRICE")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("ca_cash_price_bili")+""))/100,pcellFormat_right);
			//number=new Number(6,n,Double.parseDouble(bili2.get("ca_cash_price_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("ca_cash_price_bili")+"")),ccellFormat_right);
			//number=new Number(7,n,Double.parseDouble(chayi23.get("ca_cash_price_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("CA_CASH_PRICE")+""),ccellFormat_right);
			sheet.addCell(number);

			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("ca_cash_price_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(ca_cash_price_bili),pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"短期投资 ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("CA_SHORT_INVEST")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("ca_short_invest_bili")+""))/100,pcellFormat_right);
			//number=new Number(3,n,Double.parseDouble(bili1.get("ca_short_invest_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("ca_short_invest_bili")+"")),ccellFormat_right);
			//number=new Number(4,n,Double.parseDouble(chayi12.get("ca_short_invest_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("CA_SHORT_INVEST")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("ca_short_invest_bili")+""))/100,pcellFormat_right);
			//number=new Number(6,n,Double.parseDouble(bili2.get("ca_short_invest_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("ca_short_invest_bili")+"")),ccellFormat_right);
			//number=new Number(7,n,Double.parseDouble(chayi23.get("ca_short_invest_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("CA_SHORT_INVEST")+""),ccellFormat_right);
			sheet.addCell(number);

			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("ca_short_invest_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("ca_short_invest_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"应收票据 ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("CA_BILLS_SHOULD")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("ca_bills_should_bili")+""))/100,pcellFormat_right);
			//number=new Number(3,n,Double.parseDouble(bili1.get("ca_bills_should_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("ca_bills_should_bili")+"")),ccellFormat_right);
			//number=new Number(4,n,Double.parseDouble(chayi12.get("ca_bills_should_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("CA_BILLS_SHOULD")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("ca_bills_should_bili")+""))/100,pcellFormat_right);
			//number=new Number(6,n,Double.parseDouble(bili2.get("ca_bills_should_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("ca_bills_should_bili")+"")),ccellFormat_right);
			//number=new Number(7,n,Double.parseDouble(chayi23.get("ca_bills_should_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("CA_BILLS_SHOULD")+""),ccellFormat_right);
			sheet.addCell(number);

			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("ca_bills_should_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("ca_bills_should_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"应收账款 ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("CA_FUNDS_SHOULD")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("ca_funds_should_bili")+""))/100,pcellFormat_right);
			//number=new Number(3,n,Double.parseDouble(bili1.get("ca_funds_should_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("ca_funds_should_bili")+"")),ccellFormat_right);
			//number=new Number(4,n,Double.parseDouble(chayi12.get("ca_funds_should_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("CA_FUNDS_SHOULD")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("ca_funds_should_bili")+""))/100,pcellFormat_right);
			//number=new Number(6,n,Double.parseDouble(bili2.get("ca_funds_should_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("ca_funds_should_bili")+"")),ccellFormat_right);
			//number=new Number(7,n,Double.parseDouble(chayi23.get("ca_funds_should_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("CA_FUNDS_SHOULD")+""),ccellFormat_right);
			sheet.addCell(number);

			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("ca_funds_should_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("ca_funds_should_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"其他应收款 ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("CA_OTHER_FUNDS_SHOULD")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("ca_other_funds_should_bili")+""))/100,pcellFormat_right);
			//number=new Number(3,n,Double.parseDouble(bili1.get("ca_other_funds_should_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("ca_other_funds_should_bili")+"")),ccellFormat_right);
			//number=new Number(4,n,Double.parseDouble(chayi12.get("ca_other_funds_should_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("CA_OTHER_FUNDS_SHOULD")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("ca_other_funds_should_bili")+""))/100,pcellFormat_right);
			//number=new Number(6,n,Double.parseDouble(bili2.get("ca_other_funds_should_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("ca_other_funds_should_bili")+"")),ccellFormat_right);
			//number=new Number(7,n,Double.parseDouble(chayi23.get("ca_other_funds_should_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("CA_OTHER_FUNDS_SHOULD")+""),ccellFormat_right);
			sheet.addCell(number);

			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("ca_other_funds_should_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("ca_other_funds_should_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"存货 ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("CA_GOODS_STOCK")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("ca_goods_stock_bili")+""))/100,pcellFormat_right);
			//number=new Number(3,n,Double.parseDouble(bili1.get("ca_goods_stock_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("ca_goods_stock_bili")+"")),ccellFormat_right);
			//number=new Number(4,n,Double.parseDouble(chayi12.get("ca_goods_stock_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("CA_GOODS_STOCK")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("ca_goods_stock_bili")+""))/100,pcellFormat_right);
			//number=new Number(6,n,Double.parseDouble(bili2.get("ca_goods_stock_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("ca_goods_stock_bili")+"")),ccellFormat_right);
//			number=new Number(7,n,Double.parseDouble(chayi23.get("ca_goods_stock_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("CA_GOODS_STOCK")+""),ccellFormat_right);
			sheet.addCell(number);

			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("ca_goods_stock_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("ca_goods_stock_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"其他流动资产 ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("CA_OTHER")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("ca_other_bili")+""))/100,pcellFormat_right);
//			number=new Number(3,n,Double.parseDouble(bili1.get("ca_other_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("ca_other_bili")+"")),ccellFormat_right);
//			number=new Number(4,n,Double.parseDouble(chayi12.get("ca_other_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("CA_OTHER")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("ca_other_bili")+""))/100,pcellFormat_right);
//			number=new Number(6,n,Double.parseDouble(bili2.get("ca_other_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("ca_other_bili")+"")),ccellFormat_right);
//			number=new Number(7,n,Double.parseDouble(chayi23.get("ca_other_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("CA_OTHER")+""),ccellFormat_right);
			sheet.addCell(number);

			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("ca_other_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("ca_other_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"流动资产 ",format6); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("CA_SUM")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("ca_sum_bili")+""))/100,pcellFormat_right);
			//number=new Number(3,n,Double.parseDouble(bili1.get("ca_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("ca_sum_bili")+"")),ccellFormat_right);
//			number=new Number(4,n,Double.parseDouble(chayi12.get("ca_sum_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("CA_SUM")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("ca_sum_bili")+""))/100,pcellFormat_right);
//			number=new Number(6,n,Double.parseDouble(bili2.get("ca_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("ca_sum_bili")+"")),ccellFormat_right);
//			number=new Number(7,n,Double.parseDouble(chayi23.get("ca_sum_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("CA_SUM")+""),ccellFormat_right);
			sheet.addCell(number);

			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("ca_sum_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("ca_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"土地 ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("FA_LAND")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("fa_land_bili")+""))/100,pcellFormat_right);
//			number=new Number(3,n,Double.parseDouble(bili1.get("fa_land_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("fa_land_bili")+"")),ccellFormat_right);
//			number=new Number(4,n,Double.parseDouble(chayi12.get("fa_land_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("FA_LAND")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("fa_land_bili")+""))/100,pcellFormat_right);
//			number=new Number(6,n,Double.parseDouble(bili2.get("fa_land_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("fa_land_bili")+"")),ccellFormat_right);
//			number=new Number(7,n,Double.parseDouble(chayi23.get("fa_land_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("FA_LAND")+""),ccellFormat_right);
			sheet.addCell(number);

			
			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("fa_land_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("fa_land_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"建筑物 ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("FA_BUILDINGS")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("fa_buildings_bili")+""))/100,pcellFormat_right);
//			number=new Number(3,n,Double.parseDouble(bili1.get("fa_buildings_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("fa_buildings_bili")+"")),ccellFormat_right);
//			number=new Number(4,n,Double.parseDouble(chayi12.get("fa_buildings_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("FA_BUILDINGS")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("fa_buildings_bili")+""))/100,pcellFormat_right);
//			number=new Number(6,n,Double.parseDouble(bili2.get("fa_buildings_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("fa_buildings_bili")+"")),ccellFormat_right);
//			number=new Number(7,n,Double.parseDouble(chayi23.get("fa_buildings_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("FA_BUILDINGS")+""),ccellFormat_right);
			sheet.addCell(number);

			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("fa_buildings_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("fa_buildings_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"机器设备 ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("FA_EQUIPMENTS")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("fa_equipments_bili")+""))/100,pcellFormat_right);
//			number=new Number(3,n,Double.parseDouble(bili1.get("fa_equipments_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("fa_equipments_bili")+"")),ccellFormat_right);
//			number=new Number(4,n,Double.parseDouble(chayi12.get("fa_equipments_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("FA_EQUIPMENTS")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("fa_equipments_bili")+""))/100,pcellFormat_right);
//			number=new Number(6,n,Double.parseDouble(bili2.get("fa_equipments_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("fa_equipments_bili")+"")),ccellFormat_right);
//			number=new Number(7,n,Double.parseDouble(chayi23.get("fa_equipments_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("FA_EQUIPMENTS")+""),ccellFormat_right);
			sheet.addCell(number);

			
			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("fa_equipments_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("fa_equipments_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"租赁资产 ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("FA_RENT_ASSETS")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("fa_rent_assets_bili")+""))/100,pcellFormat_right);
//			number=new Number(3,n,Double.parseDouble(bili1.get("fa_rent_assets_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("fa_rent_assets_bili")+"")),ccellFormat_right);
//			number=new Number(4,n,Double.parseDouble(chayi12.get("fa_rent_assets_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("FA_RENT_ASSETS")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("fa_rent_assets_bili")+""))/100,pcellFormat_right);
//			number=new Number(6,n,Double.parseDouble(bili2.get("fa_rent_assets_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("fa_rent_assets_bili")+"")),ccellFormat_right);
//			number=new Number(7,n,Double.parseDouble(chayi23.get("fa_rent_assets_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("FA_RENT_ASSETS")+""),ccellFormat_right);
			sheet.addCell(number);

			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("fa_rent_assets_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("fa_rent_assets_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"运输工具及生财器具 ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("FA_TRANSPORTS")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("fa_transports_bili")+""))/100,pcellFormat_right);
//			number=new Number(3,n,Double.parseDouble(bili1.get("fa_transports_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("fa_transports_bili")+"")),ccellFormat_right);
//			number=new Number(4,n,Double.parseDouble(chayi12.get("fa_transports_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("FA_TRANSPORTS")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("fa_transports_bili")+""))/100,pcellFormat_right);
//			number=new Number(6,n,Double.parseDouble(bili2.get("fa_transports_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("fa_transports_bili")+"")),ccellFormat_right);
//			number=new Number(7,n,Double.parseDouble(chayi23.get("fa_transports_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("FA_TRANSPORTS")+""),ccellFormat_right);
			sheet.addCell(number);

			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("fa_transports_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("fa_transports_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"其他固定资产  ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("FA_OTHER")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("fa_other_bili")+""))/100,pcellFormat_right);
//			number=new Number(3,n,Double.parseDouble(bili1.get("fa_other_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("fa_other_bili")+"")),ccellFormat_right);
//			number=new Number(4,n,Double.parseDouble(chayi12.get("fa_other_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("FA_OTHER")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("fa_other_bili")+""))/100,pcellFormat_right);
//			number=new Number(6,n,Double.parseDouble(bili2.get("fa_other_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("fa_other_bili")+"")),ccellFormat_right);
//			number=new Number(7,n,Double.parseDouble(chayi23.get("fa_other_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("FA_OTHER")+""),ccellFormat_right);
			sheet.addCell(number);

			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("fa_other_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("fa_other_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"减:累计折旧 ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("FA_DEPRECIATIONS")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("fa_depreciations_bili")+""))/100,pcellFormat_right);
//			number=new Number(3,n,Double.parseDouble(bili1.get("fa_depreciations_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("fa_depreciations_bili")+"")),ccellFormat_right);
//			number=new Number(4,n,Double.parseDouble(chayi12.get("fa_depreciations_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("FA_DEPRECIATIONS")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("fa_depreciations_bili")+""))/100,pcellFormat_right);
//			number=new Number(6,n,Double.parseDouble(bili2.get("fa_depreciations_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("fa_depreciations_bili")+"")),ccellFormat_right);
//			number=new Number(7,n,Double.parseDouble(chayi23.get("fa_depreciations_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("FA_DEPRECIATIONS")+""),ccellFormat_right);
			sheet.addCell(number);

			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("fa_depreciations_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("fa_depreciations_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"未完工程及预付设备款 ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("FA_INCOMPLETED_PROJECTS")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("fa_incompleted_projects_bili")+""))/100,pcellFormat_right);
//			number=new Number(3,n,Double.parseDouble(bili1.get("fa_incompleted_projects_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("fa_incompleted_projects_bili")+"")),ccellFormat_right);
//			number=new Number(4,n,Double.parseDouble(chayi12.get("fa_incompleted_projects_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("FA_INCOMPLETED_PROJECTS")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("fa_incompleted_projects_bili")+""))/100,pcellFormat_right);
//			number=new Number(6,n,Double.parseDouble(bili2.get("fa_incompleted_projects_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("fa_incompleted_projects_bili")+"")),ccellFormat_right);
//			number=new Number(7,n,Double.parseDouble(chayi23.get("fa_incompleted_projects_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("FA_INCOMPLETED_PROJECTS")+""),ccellFormat_right);
			sheet.addCell(number);

			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("fa_incompleted_projects_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("fa_incompleted_projects_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"固定资产 ",format6); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("FA_SUM")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("fa_sum_bili")+""))/100,pcellFormat_right);
//			number=new Number(3,n,Double.parseDouble(bili1.get("fa_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("fa_sum_bili")+"")),ccellFormat_right);
//			number=new Number(4,n,Double.parseDouble(chayi12.get("fa_sum_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("FA_SUM")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("fa_sum_bili")+""))/100,pcellFormat_right);
//			number=new Number(6,n,Double.parseDouble(bili2.get("fa_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("fa_sum_bili")+"")),ccellFormat_right);
//			number=new Number(7,n,Double.parseDouble(chayi23.get("fa_sum_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("FA_SUM")+""),ccellFormat_right);
			sheet.addCell(number);

			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("fa_sum_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("fa_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"长期投资 ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("LANG_INVEST")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("lang_invest_bili")+""))/100,pcellFormat_right);
//			number=new Number(3,n,Double.parseDouble(bili1.get("lang_invest_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("lang_invest_bili")+"")),ccellFormat_right);
//			number=new Number(4,n,Double.parseDouble(chayi12.get("lang_invest_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("LANG_INVEST")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("lang_invest_bili")+""))/100,pcellFormat_right);
//			number=new Number(6,n,Double.parseDouble(bili2.get("lang_invest_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("lang_invest_bili")+"")),ccellFormat_right);
//			number=new Number(7,n,Double.parseDouble(chayi23.get("lang_invest_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("LANG_INVEST")+""),ccellFormat_right);
			sheet.addCell(number);

			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("lang_invest_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("lang_invest_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"其他资产 ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("OTHER_ASSETS")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("other_assets_bili")+""))/100,pcellFormat_right);
//			number=new Number(3,n,Double.parseDouble(bili1.get("other_assets_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("other_assets_bili")+"")),ccellFormat_right);
//			number=new Number(4,n,Double.parseDouble(chayi12.get("other_assets_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("OTHER_ASSETS")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("other_assets_bili")+""))/100,pcellFormat_right);
//			number=new Number(6,n,Double.parseDouble(bili2.get("other_assets_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("other_assets_bili")+"")),ccellFormat_right);
//			number=new Number(7,n,Double.parseDouble(chayi23.get("other_assets_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("OTHER_ASSETS")+""),ccellFormat_right);
			sheet.addCell(number);

			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("other_assets_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("other_assets_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"资产总额 ",format8); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("CAPITAL_SUM")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("capital_sum_bili")+""))/100,pcellFormat_right);
//			number=new Number(3,n,Double.parseDouble(bili1.get("capital_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("capital_sum_bili")+"")),ccellFormat_right);
//			number=new Number(4,n,Double.parseDouble(chayi12.get("capital_sum_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("CAPITAL_SUM")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("capital_sum_bili")+""))/100,pcellFormat_right);
//			number=new Number(6,n,Double.parseDouble(bili2.get("capital_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("capital_sum_bili")+"")),ccellFormat_right);
//			number=new Number(7,n,Double.parseDouble(chayi23.get("capital_sum_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("CAPITAL_SUM")+""),ccellFormat_right);
			sheet.addCell(number);

			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("capital_sum_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("capital_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"短期借款 ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("SD_SHORT_DEBT")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("sd_short_debt_bili")+""))/100,pcellFormat_right);
//			number=new Number(3,n,Double.parseDouble(bili1.get("sd_short_debt_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("sd_short_debt_bili")+"")),ccellFormat_right);
//			number=new Number(4,n,Double.parseDouble(chayi12.get("sd_short_debt_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("SD_SHORT_DEBT")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("sd_short_debt_bili")+""))/100,pcellFormat_right);
//			number=new Number(6,n,Double.parseDouble(bili2.get("sd_short_debt_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("sd_short_debt_bili")+"")),ccellFormat_right);
//			number=new Number(7,n,Double.parseDouble(chayi23.get("sd_short_debt_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("SD_SHORT_DEBT")+""),ccellFormat_right);
			sheet.addCell(number);

			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("sd_short_debt_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("sd_short_debt_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"应付票据 ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("SD_BILLS_SHOULD")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("sd_bills_should_bili")+""))/100,pcellFormat_right);
//			number=new Number(3,n,Double.parseDouble(bili1.get("sd_bills_should_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("sd_bills_should_bili")+"")),ccellFormat_right);
//			number=new Number(4,n,Double.parseDouble(chayi12.get("sd_bills_should_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("SD_BILLS_SHOULD")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("sd_bills_should_bili")+""))/100,pcellFormat_right);
//			number=new Number(6,n,Double.parseDouble(bili2.get("sd_bills_should_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("sd_bills_should_bili")+"")),ccellFormat_right);
//			number=new Number(7,n,Double.parseDouble(chayi23.get("sd_bills_should_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("SD_BILLS_SHOULD")+""),ccellFormat_right);
			sheet.addCell(number);
//			String sd_bills_should_bili=bili3.get("sd_bills_should_bili")+"";
//			if(sd_bills_should_bili.equals("NaN")){
//				sd_bills_should_bili=0+"";
//			}
//			number=new Number(9,n,Double.parseDouble(sd_bills_should_bili),pcellFormat_right);
			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("sd_bills_should_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("sd_bills_should_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"应付账款 ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("SD_FUNDS_SHOULD")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("sd_funds_should_bili")+""))/100,pcellFormat_right);
//			number=new Number(3,n,Double.parseDouble(bili1.get("sd_funds_should_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("sd_funds_should_bili")+"")),ccellFormat_right);
//			number=new Number(4,n,Double.parseDouble(chayi12.get("sd_funds_should_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("SD_FUNDS_SHOULD")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("sd_funds_should_bili")+""))/100,pcellFormat_right);
//			number=new Number(6,n,Double.parseDouble(bili2.get("sd_funds_should_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("sd_funds_should_bili")+"")),ccellFormat_right);
//			number=new Number(7,n,Double.parseDouble(chayi23.get("sd_funds_should_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("SD_FUNDS_SHOULD")+""),ccellFormat_right);
			sheet.addCell(number);
//			String sd_funds_should_bili=bili3.get("sd_funds_should_bili")+"";
//			if(sd_funds_should_bili.equals("NaN")){
//				sd_funds_should_bili=0+"";
//			}
//			number=new Number(9,n,Double.parseDouble(sd_funds_should_bili),pcellFormat_right);
			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("sd_funds_should_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("sd_funds_should_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"其他应付款 ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("SD_OTHER_PAY")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("sd_other_pay_bili")+""))/100,pcellFormat_right);
//			number=new Number(3,n,Double.parseDouble(bili1.get("sd_other_pay_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("sd_other_pay_bili")+"")),ccellFormat_right);
//			number=new Number(4,n,Double.parseDouble(chayi12.get("sd_other_pay_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("SD_OTHER_PAY")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("sd_other_pay_bili")+""))/100,pcellFormat_right);
//			number=new Number(6,n,Double.parseDouble(bili2.get("sd_other_pay_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("sd_other_pay_bili")+"")),ccellFormat_right);
//			number=new Number(7,n,Double.parseDouble(chayi23.get("sd_other_pay_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("SD_OTHER_PAY")+""),ccellFormat_right);
			sheet.addCell(number);
//			String sd_other_pay_bili=bili3.get("sd_other_pay_bili")+"";
//			if(sd_other_pay_bili.equals("NaN")){
//				sd_other_pay_bili=0+"";
//			}
//			number=new Number(9,n,Double.parseDouble(sd_other_pay_bili),pcellFormat_right);
			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("sd_other_pay_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("sd_other_pay_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"股东往来  ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("SD_SHAREHOLDERS")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("sd_shareholders_bili")+""))/100,pcellFormat_right);
//			number=new Number(3,n,Double.parseDouble(bili1.get("sd_shareholders_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("sd_shareholders_bili")+"")),ccellFormat_right);
//			number=new Number(4,n,Double.parseDouble(chayi12.get("sd_shareholders_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("SD_SHAREHOLDERS")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("sd_shareholders_bili")+""))/100,pcellFormat_right);
//			number=new Number(6,n,Double.parseDouble(bili2.get("sd_shareholders_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("sd_shareholders_bili")+"")),ccellFormat_right);
//			number=new Number(7,n,Double.parseDouble(chayi23.get("sd_shareholders_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("SD_SHAREHOLDERS")+""),ccellFormat_right);
			sheet.addCell(number);
//			String sd_shareholders_bili=bili3.get("sd_shareholders_bili")+"";
//			if(sd_shareholders_bili.equals("NaN")){
//				sd_shareholders_bili=0+"";
//			}
//			number=new Number(9,n,Double.parseDouble(sd_shareholders_bili),pcellFormat_right);
			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("sd_shareholders_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("sd_shareholders_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"一年内到期之长期负债",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("SD_ONE_YEAR")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("sd_one_year_bili")+""))/100,pcellFormat_right);
//			number=new Number(3,n,Double.parseDouble(bili1.get("sd_one_year_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("sd_one_year_bili")+"")),ccellFormat_right);
//			number=new Number(4,n,Double.parseDouble(chayi12.get("sd_one_year_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("SD_ONE_YEAR")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("sd_one_year_bili")+""))/100,pcellFormat_right);
//			number=new Number(6,n,Double.parseDouble(bili2.get("sd_one_year_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("sd_one_year_bili")+"")),ccellFormat_right);
//			number=new Number(7,n,Double.parseDouble(chayi23.get("sd_one_year_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("SD_ONE_YEAR")+""),ccellFormat_right);
			sheet.addCell(number);
//			String sd_one_year_bili=bili3.get("sd_one_year_bili")+"";
//			if(sd_one_year_bili.equals("NaN")){
//				sd_one_year_bili=0+"";
//			}
//			number=new Number(9,n,Double.parseDouble(sd_one_year_bili),pcellFormat_right);
			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("sd_one_year_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("sd_one_year_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"其他流动负债",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("SD_OTHER")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("sd_other_bili")+""))/100,pcellFormat_right);
//			number=new Number(3,n,Double.parseDouble(bili1.get("sd_other_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("sd_other_bili")+"")),ccellFormat_right);
//			number=new Number(4,n,Double.parseDouble(chayi12.get("sd_other_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("SD_OTHER")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("sd_other_bili")+""))/100,pcellFormat_right);
//			number=new Number(6,n,Double.parseDouble(bili2.get("sd_other_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("sd_other_bili")+"")),ccellFormat_right);
//			number=new Number(7,n,Double.parseDouble(chayi23.get("sd_other_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("SD_OTHER")+""),ccellFormat_right);
			sheet.addCell(number);
//			String sd_other_bili=bili3.get("sd_other_bili")+"";
//			if(sd_other_bili.equals("NaN")){
//				sd_other_bili=0+"";
//			}
//			number=new Number(9,n,Double.parseDouble(sd_other_bili),pcellFormat_right);
			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("sd_other_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("sd_other_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"流动负债",format6); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("SD_SUM")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("sd_sum_bili")+""))/100,pcellFormat_right);
//			number=new Number(3,n,Double.parseDouble(bili1.get("sd_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("sd_sum_bili")+"")),ccellFormat_right);
//			number=new Number(4,n,Double.parseDouble(chayi12.get("sd_sum_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("SD_SUM")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("sd_sum_bili")+""))/100,pcellFormat_right);
//			number=new Number(6,n,Double.parseDouble(bili2.get("sd_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("sd_sum_bili")+"")),ccellFormat_right);
//			number=new Number(7,n,Double.parseDouble(chayi23.get("sd_sum_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("SD_SUM")+""),ccellFormat_right);
			sheet.addCell(number);
//			String sd_sum_bili=bili3.get("sd_sum_bili")+"";
//			if(sd_sum_bili.equals("NaN")){
//				sd_sum_bili=0+"";
//			}
//			number=new Number(9,n,Double.parseDouble(sd_sum_bili),pcellFormat_right);
			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("sd_sum_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("sd_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"长期借款",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("LANG_DEBT")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("lang_debt_bili")+""))/100,pcellFormat_right);
//			number=new Number(3,n,Double.parseDouble(bili1.get("lang_debt_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("lang_debt_bili")+"")),ccellFormat_right);
//			number=new Number(4,n,Double.parseDouble(chayi12.get("lang_debt_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("LANG_DEBT")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("lang_debt_bili")+""))/100,pcellFormat_right);
//			number=new Number(6,n,Double.parseDouble(bili2.get("lang_debt_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("lang_debt_bili")+"")),ccellFormat_right);
//			number=new Number(7,n,Double.parseDouble(chayi23.get("lang_debt_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("LANG_DEBT")+""),ccellFormat_right);
			sheet.addCell(number);
//			String lang_debt_bili=bili3.get("lang_debt_bili")+"";
//			if(lang_debt_bili.equals("NaN")){
//				lang_debt_bili=0+"";
//			}
//			number=new Number(9,n,Double.parseDouble(lang_debt_bili),pcellFormat_right);
			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("lang_debt_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("lang_debt_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"其他长期负债 ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("OTHER_LONG_DEBT")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("other_long_debt_bili")+""))/100,pcellFormat_right);
//			number=new Number(3,n,Double.parseDouble(bili1.get("other_long_debt_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("other_long_debt_bili")+"")),ccellFormat_right);
//			number=new Number(4,n,Double.parseDouble(chayi12.get("other_long_debt_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("OTHER_LONG_DEBT")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("other_long_debt_bili")+""))/100,pcellFormat_right);
//			number=new Number(6,n,Double.parseDouble(bili2.get("other_long_debt_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("other_long_debt_bili")+"")),ccellFormat_right);
//			number=new Number(7,n,Double.parseDouble(chayi23.get("other_long_debt_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("OTHER_LONG_DEBT")+""),ccellFormat_right);
			sheet.addCell(number);
//			String other_long_debt_bili=bili3.get("other_long_debt_bili")+"";
//			if(other_long_debt_bili.equals("NaN")){
//				other_long_debt_bili=0+"";
//			}
//			number=new Number(9,n,Double.parseDouble(other_long_debt_bili),pcellFormat_right);
			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("other_long_debt_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("other_long_debt_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"长期负债 ",format6); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("LD_SUM")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("ld_sum_bili")+""))/100,pcellFormat_right);
//			number=new Number(3,n,Double.parseDouble(bili1.get("ld_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("ld_sum_bili")+"")),ccellFormat_right);
//			number=new Number(4,n,Double.parseDouble(chayi12.get("ld_sum_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("LD_SUM")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("ld_sum_bili")+""))/100,pcellFormat_right);
//			number=new Number(6,n,Double.parseDouble(bili2.get("ld_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("ld_sum_bili")+"")),ccellFormat_right);
//			number=new Number(7,n,Double.parseDouble(chayi23.get("ld_sum_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("LD_SUM")+""),ccellFormat_right);
			sheet.addCell(number);
//			String ld_sum_bili=bili3.get("ld_sum_bili")+"";
//			if(ld_sum_bili.equals("NaN")){
//				ld_sum_bili=0+"";
//			}
//			number=new Number(9,n,Double.parseDouble(ld_sum_bili),pcellFormat_right);
			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("ld_sum_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("ld_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"其他负债 ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("OTHER_DEBT")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("other_debt_bili")+""))/100,pcellFormat_right);
//			number=new Number(3,n,Double.parseDouble(bili1.get("other_debt_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("other_debt_bili")+"")),ccellFormat_right);
//			number=new Number(4,n,Double.parseDouble(chayi12.get("other_debt_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("OTHER_DEBT")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("other_debt_bili")+""))/100,pcellFormat_right);
//			number=new Number(6,n,Double.parseDouble(bili2.get("other_debt_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("other_debt_bili")+"")),ccellFormat_right);
//			number=new Number(7,n,Double.parseDouble(chayi23.get("other_debt_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("OTHER_DEBT")+""),ccellFormat_right);
			sheet.addCell(number);
//			String other_debt_bili=bili3.get("other_debt_bili")+"";
//			if(other_debt_bili.equals("NaN")){
//				other_debt_bili=0+"";
//			}
//			number=new Number(9,n,Double.parseDouble(other_debt_bili),pcellFormat_right);
			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("other_debt_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("other_debt_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"负债总额 ",format8); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("DEBT_SUM")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("debt_sum_bili")+""))/100,pcellFormat_right);
//			number=new Number(3,n,Double.parseDouble(bili1.get("debt_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("debt_sum_bili")+"")),ccellFormat_right);
//			number=new Number(4,n,Double.parseDouble(chayi12.get("debt_sum_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("DEBT_SUM")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("debt_sum_bili")+""))/100,pcellFormat_right);
//			number=new Number(6,n,Double.parseDouble(bili2.get("debt_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("debt_sum_bili")+"")),ccellFormat_right);
//			number=new Number(7,n,Double.parseDouble(chayi23.get("debt_sum_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("DEBT_SUM")+""),ccellFormat_right);
			sheet.addCell(number);
//			String debt_sum_bili=bili3.get("debt_sum_bili")+"";
//			if(debt_sum_bili.equals("NaN")){
//				debt_sum_bili=0+"";
//			}
//			number=new Number(9,n,Double.parseDouble(debt_sum_bili),pcellFormat_right);
			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("debt_sum_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("debt_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"实收股本 ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("SHARE_CAPITAL")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("share_capital_bili")+""))/100,pcellFormat_right);
//			number=new Number(3,n,Double.parseDouble(bili1.get("share_capital_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("share_capital_bili")+"")),ccellFormat_right);
//			number=new Number(4,n,Double.parseDouble(chayi12.get("share_capital_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("SHARE_CAPITAL")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("share_capital_bili")+""))/100,pcellFormat_right);
//			number=new Number(6,n,Double.parseDouble(bili2.get("share_capital_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("share_capital_bili")+"")),ccellFormat_right);
//			number=new Number(7,n,Double.parseDouble(chayi23.get("share_capital_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("SHARE_CAPITAL")+""),ccellFormat_right);
			sheet.addCell(number);
//			String share_capital_bili=bili3.get("share_capital_bili")+"";
//			if(share_capital_bili.equals("NaN")){
//				share_capital_bili=0+"";
//			}
//			number=new Number(9,n,Double.parseDouble(share_capital_bili),pcellFormat_right);
			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("share_capital_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("share_capital_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"资本公积 ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("SURPLUS_CAPITAL")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("surplus_capital_bili")+""))/100,pcellFormat_right);
//			number=new Number(3,n,Double.parseDouble(bili1.get("surplus_capital_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("surplus_capital_bili")+"")),ccellFormat_right);
//			number=new Number(4,n,Double.parseDouble(chayi12.get("surplus_capital_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("SURPLUS_CAPITAL")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("surplus_capital_bili")+""))/100,pcellFormat_right);
//			number=new Number(6,n,Double.parseDouble(bili2.get("surplus_capital_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("surplus_capital_bili")+"")),ccellFormat_right);
//			number=new Number(7,n,Double.parseDouble(chayi23.get("surplus_capital_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("SURPLUS_CAPITAL")+""),ccellFormat_right);
			sheet.addCell(number);
//			String surplus_capital_bili=bili3.get("surplus_capital_bili")+"";
//			if(surplus_capital_bili.equals("NaN")){
//				surplus_capital_bili=0+"";
//			}
//			number=new Number(9,n,Double.parseDouble(surplus_capital_bili),pcellFormat_right);
			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("surplus_capital_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("surplus_capital_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"累积盈余 ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("SURPLUS_INCOME")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("surplus_income_bili")+""))/100,pcellFormat_right);
//			number=new Number(3,n,Double.parseDouble(bili1.get("surplus_income_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("surplus_income_bili")+"")),ccellFormat_right);
//			number=new Number(4,n,Double.parseDouble(chayi12.get("surplus_income_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("SURPLUS_INCOME")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("surplus_income_bili")+""))/100,pcellFormat_right);
//			number=new Number(6,n,Double.parseDouble(bili2.get("surplus_income_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("surplus_income_bili")+"")),ccellFormat_right);
//			number=new Number(7,n,Double.parseDouble(chayi23.get("surplus_income_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("SURPLUS_INCOME")+""),ccellFormat_right);
			sheet.addCell(number);
//			String surplus_income_bili=bili3.get("surplus_income_bili")+"";
//			if(surplus_income_bili.equals("NaN")){
//				surplus_income_bili=0+"";
//			}
//			number=new Number(9,n,Double.parseDouble(surplus_income_bili),pcellFormat_right);
			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("surplus_income_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("surplus_income_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"本期损益 ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("THIS_LOSTS")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("this_losts_bili")+""))/100,pcellFormat_right);
//			number=new Number(3,n,Double.parseDouble(bili1.get("this_losts_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("this_losts_bili")+"")),ccellFormat_right);
//			number=new Number(4,n,Double.parseDouble(chayi12.get("this_losts_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("THIS_LOSTS")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("this_losts_bili")+""))/100,pcellFormat_right);
//			number=new Number(6,n,Double.parseDouble(bili2.get("this_losts_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("this_losts_bili")+"")),ccellFormat_right);
//			number=new Number(7,n,Double.parseDouble(chayi23.get("this_losts_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("THIS_LOSTS")+""),ccellFormat_right);
			sheet.addCell(number);
//			String this_losts_bili=bili3.get("this_losts_bili")+"";
//			if(this_losts_bili.equals("NaN")){
//				this_losts_bili=0+"";
//			}
//			number=new Number(9,n,Double.parseDouble(this_losts_bili),pcellFormat_right);
			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("this_losts_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("this_losts_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"调整项目 ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("PROJECT_CHANGED")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("project_changed_bili")+""))/100,pcellFormat_right);
//			number=new Number(3,n,Double.parseDouble(bili1.get("project_changed_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("project_changed_bili")+"")),ccellFormat_right);
//			number=new Number(4,n,Double.parseDouble(chayi12.get("project_changed_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("PROJECT_CHANGED")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("project_changed_bili")+""))/100,pcellFormat_right);
//			number=new Number(6,n,Double.parseDouble(bili2.get("project_changed_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("project_changed_bili")+"")),ccellFormat_right);
//			number=new Number(7,n,Double.parseDouble(chayi23.get("project_changed_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("PROJECT_CHANGED")+""),ccellFormat_right);
			sheet.addCell(number);
//			String project_changed_bili=bili3.get("project_changed_bili")+"";
//			if(project_changed_bili.equals("NaN")){
//				project_changed_bili=0+"";
//			}
//			number=new Number(9,n,Double.parseDouble(project_changed_bili),pcellFormat_right);
			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("project_changed_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("project_changed_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"净值总额 ",format8); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("REAL_SUM")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili1.get("real_sum_bili")+""))/100,pcellFormat_right);
//			number=new Number(3,n,Double.parseDouble(bili1.get("real_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(chayi12.get("real_sum_bili")+"")),ccellFormat_right);
//			number=new Number(4,n,Double.parseDouble(chayi12.get("real_sum_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("REAL_SUM")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili2.get("real_sum_bili")+""))/100,pcellFormat_right);
//			number=new Number(6,n,Double.parseDouble(bili2.get("real_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(chayi23.get("real_sum_bili")+"")),ccellFormat_right);
//			number=new Number(7,n,Double.parseDouble(chayi23.get("real_sum_bili")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("REAL_SUM")+""),ccellFormat_right);
			sheet.addCell(number);
//			String real_sum_bili=bili3.get("real_sum_bili")+"";
//			if(real_sum_bili.equals("NaN")){
//				real_sum_bili=0+"";
//			}
//			number=new Number(9,n,Double.parseDouble(real_sum_bili),pcellFormat_right);
			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili3.get("real_sum_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili3.get("real_sum_bili")+"")/100,pcellFormat_right);
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
			n++;
			
			cell=new Label(1,n,"上次说明：",format3);
			sheet.mergeCells(1, n, 9, n); 
			sheet.addCell(cell);
			n++ ;
			cell=new Label(1,n,((Map)exportMap.get("remark1Map")).get("REMARK") +"",format3);
			sheet.mergeCells(1, n, 9, n+5); 
			sheet.addCell(cell);
			n+=5;
			
			///////////////////////////////////////////////////////////////////////////////////////
			
			Map bili21 = new HashMap();
			Map bili22 = new HashMap();
			Map bili23 = new HashMap();
			
			Map czlv12 = new HashMap();
			Map czlv23 = new HashMap();
			
			bili21 = exportMap.get("bili21");
			bili22 = exportMap.get("bili22");
			bili23 = exportMap.get("bili23");
			
			czlv12 = exportMap.get("czlv12");
			czlv23 = exportMap.get("czlv23");
			
			
			sheet = wb.createSheet("BSPL-2", 2);
			
			sheet.setColumnView(0, 4);
			sheet.setColumnView(1, 20);
			sheet.setColumnView(2, 15);
			sheet.setColumnView(3, 15);
			sheet.setColumnView(4, 15);
			sheet.setColumnView(5, 15);
			sheet.setColumnView(6, 15);
			sheet.setColumnView(7, 15);
			sheet.setColumnView(8, 15);
			sheet.setColumnView(9, 15);
			sheet.setColumnView(10,4);
			
			n=0;
			
			cell=new Label(0,0,"");  
			sheet.addCell(cell);
			cell=new Label(1,0,"",format2_BOTTOM);  
			sheet.mergeCells(1, 0, 9, 0);
			sheet.addCell(cell);
			sheet.setRowView(0,300,false); 
			n=n+1;
			
			cell=new Label(0,1,""); 
			sheet.addCell(cell);
			cell=new Label(1,1,"项目名称",format4); 
			sheet.addCell(cell);
			cell=new Label(2,1,"损益表暨水平分析(年度) ",format4); 
			sheet.mergeCells(2, 1, 8, 1);
			sheet.addCell(cell);
			cell=new Label(9,1,"单位（千元）",format4); 
			sheet.addCell(cell);
			sheet.setRowView(1,320,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"科目\\日期 ",format3); 
			sheet.addCell(cell); 
			Date S_START_DATE1=df.parse(obj1.get("S_START_DATE")+"");
			
			DateTime dateLabe21=new DateTime(2,n, S_START_DATE1,dateCellFormat_left);
			sheet.addCell(dateLabe21);
			cell=new Label(3,n,"",format3); 
			sheet.addCell(cell); 
			cell=new Label(4,n,"成长率%",format3); 
			sheet.addCell(cell);
			Date S_START_DATE2=df.parse(obj2.get("S_START_DATE")+"");
			DateTime dateLabe22=new DateTime(5,n, S_START_DATE2,dateCellFormat_left);
			sheet.addCell(dateLabe22);
			cell=new Label(6,n,"",format3); 
			sheet.addCell(cell); 
			cell=new Label(7,n,"成长率%",format3); 
			sheet.addCell(cell);
			Date S_START_DATE3=df.parse(obj3.get("S_START_DATE")+"");
			DateTime dateLabe23=new DateTime(8,n, S_START_DATE3,dateCellFormat_left);
			sheet.addCell(dateLabe23);
			cell=new Label(9,n,"",format3); 
			sheet.addCell(cell); 
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"销售收入净额  ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("S_SALE_NET_INCOME")+""),ccellFormat_right);
			sheet.addCell(number);		
			number=new Number(3,n,1,pcellFormat_right);
			sheet.addCell(number);
			String s_sale_net_income_bili=czlv12.get("s_sale_net_income_bili")+"";
//			if(s_sale_net_income_bili.equals("NaN")){
//				s_sale_net_income_bili=0+"";
//			}
//			number=new Number(4,n,Double.parseDouble(s_sale_net_income_bili),pcellFormat_right);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(czlv12.get("s_sale_net_income_bili")+""))/100,pcellFormat_right);
			//number=new Number(4,n,Double.parseDouble(czlv12.get("s_sale_net_income_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("S_SALE_NET_INCOME")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(6,n,1,pcellFormat_right);
			sheet.addCell(number);
//			String s_sale_net_income_bili2=czlv23.get("s_sale_net_income_bili")+"";
//			if(s_sale_net_income_bili2.equals("NaN")){
//				s_sale_net_income_bili2=0+"";
//			}
//			number=new Number(7,n,Double.parseDouble(s_sale_net_income_bili2),pcellFormat_right);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(czlv23.get("s_sale_net_income_bili")+""))/100,pcellFormat_right);
			//number=new Number(7,n,Double.parseDouble(czlv23.get("s_sale_net_income_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
//			String S_SALE_NET_INCOME=obj3.get("S_SALE_NET_INCOME")+"";
//			if(S_SALE_NET_INCOME.equals("NaN")){
//				S_SALE_NET_INCOME=0+"";
//			}
//			number=new Number(8,n,Double.parseDouble(S_SALE_NET_INCOME),pcellFormat_right);
			number=new Number(8,n,Double.parseDouble(this.getDoubleNumString(obj3.get("S_SALE_NET_INCOME")+"")),ccellFormat_right);
			//number=new Number(8,n,Double.parseDouble(obj3.get("S_SALE_NET_INCOME")+""),ccellFormat_right);
			sheet.addCell(number);
			number=new Number(9,n,1,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"减:销售成本 ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("S_SALE_COST")+""),ccellFormat_right);
			sheet.addCell(number);		
//			String s_sale_cost_bili21=bili21.get("s_sale_cost_bili")+"";
//			if(s_sale_cost_bili21.equals("NaN")){
//				s_sale_cost_bili21=0+"";
//			}
//			number=new Number(3,n,Double.parseDouble(s_sale_cost_bili21),pcellFormat_right);
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili21.get("s_sale_cost_bili")+""))/100,pcellFormat_right);
			//number=new Number(3,n,Double.parseDouble(bili21.get("s_sale_cost_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
//			String s_sale_cost_bili=czlv12.get("s_sale_cost_bili")+"";
//			if(s_sale_cost_bili.equals("NaN")){
//				s_sale_cost_bili=0+"";
//			}
//			number=new Number(4,n,Double.parseDouble(s_sale_cost_bili),pcellFormat_right);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(czlv12.get("s_sale_cost_bili")+""))/100,pcellFormat_right);
			//number=new Number(4,n,Double.parseDouble(czlv12.get("s_sale_cost_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("S_SALE_COST")+""),ccellFormat_right);
			sheet.addCell(number);
//			String s_sale_cost_bili2=bili22.get("s_sale_cost_bili")+"";
//			if(s_sale_cost_bili2.equals("NaN")){
//				s_sale_cost_bili2=0+"";
//			}
//			number=new Number(6,n,Double.parseDouble(s_sale_cost_bili2),pcellFormat_right);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili22.get("s_sale_cost_bili")+""))/100,pcellFormat_right);
			//number=new Number(6,n,Double.parseDouble(bili22.get("s_sale_cost_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
//			String s_sale_cost_bili22=czlv23.get("s_sale_cost_bili")+"";
//			if(s_sale_cost_bili22.equals("NaN")){
//				s_sale_cost_bili22=0+"";
//			}
//			number=new Number(7,n,Double.parseDouble(s_sale_cost_bili22),pcellFormat_right);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(czlv23.get("s_sale_cost_bili")+""))/100,pcellFormat_right);
			//number=new Number(7,n,Double.parseDouble(czlv23.get("s_sale_cost_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("S_SALE_COST")+""),ccellFormat_right);
			sheet.addCell(number);
//			String s_sale_cost_bili23=bili23.get("s_sale_cost_bili")+"";
//			if(s_sale_cost_bili23.equals("NaN")){
//				s_sale_cost_bili23=0+"";
//			}
//			number=new Number(9,n,Double.parseDouble(s_sale_cost_bili23),pcellFormat_right);
			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili23.get("s_sale_cost_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili23.get("s_sale_cost_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"其他业务毛利 ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("S_OTHER_GROSS_PROFIT")+""),ccellFormat_right);
			sheet.addCell(number);		
//			String s_other_gross_profit_bili21=bili21.get("s_other_gross_profit_bili")+"";
//			if(s_other_gross_profit_bili21.equals("NaN")){
//				s_other_gross_profit_bili21=0+"";
//			}
//			number=new Number(3,n,Double.parseDouble(s_other_gross_profit_bili21),pcellFormat_right);
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili21.get("s_other_gross_profit_bili")+""))/100,pcellFormat_right);
			//number=new Number(3,n,Double.parseDouble(bili21.get("s_other_gross_profit_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
//			String s_other_gross_profit_bili=czlv12.get("s_other_gross_profit_bili")+"";
//			if(s_other_gross_profit_bili.equals("NaN")){
//				s_other_gross_profit_bili=0+"";
//			}
//			number=new Number(4,n,Double.parseDouble(s_other_gross_profit_bili),pcellFormat_right);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(czlv12.get("s_other_gross_profit_bili")+""))/100,pcellFormat_right);
			//number=new Number(4,n,Double.parseDouble(czlv12.get("s_other_gross_profit_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("S_OTHER_GROSS_PROFIT")+""),ccellFormat_right);
			sheet.addCell(number);
//			String s_other_gross_profit_bili22=bili22.get("s_other_gross_profit_bili")+"";
//			if(s_other_gross_profit_bili22.equals("NaN")){
//				s_other_gross_profit_bili22=0+"";
//			}
//			number=new Number(6,n,Double.parseDouble(s_other_gross_profit_bili22),pcellFormat_right);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili22.get("s_other_gross_profit_bili")+""))/100,pcellFormat_right);
			//number=new Number(6,n,Double.parseDouble(bili22.get("s_other_gross_profit_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
//			String s_other_gross_profit_bili23=czlv23.get("s_other_gross_profit_bili")+"";
//			if(s_other_gross_profit_bili23.equals("NaN")){
//				s_other_gross_profit_bili23=0+"";
//			}
//			number=new Number(7,n,Double.parseDouble(s_other_gross_profit_bili23),pcellFormat_right);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(czlv23.get("s_other_gross_profit_bili")+""))/100,pcellFormat_right);
			//number=new Number(7,n,Double.parseDouble(czlv23.get("s_other_gross_profit_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("S_OTHER_GROSS_PROFIT")+""),ccellFormat_right);
			sheet.addCell(number);
//			String s_other_gross_profit_bili233=bili23.get("s_other_gross_profit_bili")+"";
//			if(s_other_gross_profit_bili233.equals("NaN")){
//				s_other_gross_profit_bili233=0+"";
//			}
//			number=new Number(9,n,Double.parseDouble(s_other_gross_profit_bili233),pcellFormat_right);
			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili23.get("s_other_gross_profit_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili23.get("s_other_gross_profit_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"销售毛利 ",format6); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("S_SGP_SUM")+""),ccellFormat_right);
			sheet.addCell(number);		
//			String s_sgp_sum_bili21=bili21.get("s_sgp_sum_bili")+"";
//			if(s_sgp_sum_bili21.equals("NaN")){
//				s_sgp_sum_bili21=0+"";
//			}
//			number=new Number(3,n,Double.parseDouble(s_sgp_sum_bili21),pcellFormat_right);
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili21.get("s_sgp_sum_bili")+""))/100,pcellFormat_right);
			//number=new Number(3,n,Double.parseDouble(bili21.get("s_sgp_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
//			String s_sgp_sum_bili=czlv12.get("s_sgp_sum_bili")+"";
//			if(s_sgp_sum_bili.equals("NaN")){
//				s_sgp_sum_bili=0+"";
//			}
//			number=new Number(4,n,Double.parseDouble(s_sgp_sum_bili),pcellFormat_right);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(czlv12.get("s_sgp_sum_bili")+""))/100,pcellFormat_right);
			//number=new Number(4,n,Double.parseDouble(czlv12.get("s_sgp_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("S_SGP_SUM")+""),ccellFormat_right);
			sheet.addCell(number);
//			String s_sgp_sum_bili22=bili22.get("s_sgp_sum_bili")+"";
//			if(s_sgp_sum_bili22.equals("NaN")){
//				s_sgp_sum_bili22=0+"";
//			}
//			number=new Number(6,n,Double.parseDouble(s_sgp_sum_bili22),pcellFormat_right);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili22.get("s_sgp_sum_bili")+""))/100,pcellFormat_right);
			//number=new Number(6,n,Double.parseDouble(bili22.get("s_sgp_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
//			String s_sgp_sum_bili23=czlv23.get("s_sgp_sum_bili")+"";
//			if(s_sgp_sum_bili23.equals("NaN")){
//				s_sgp_sum_bili23=0+"";
//			}
//			number=new Number(7,n,Double.parseDouble(s_sgp_sum_bili23),pcellFormat_right);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(czlv23.get("s_sgp_sum_bili")+""))/100,pcellFormat_right);
			//number=new Number(7,n,Double.parseDouble(czlv23.get("s_sgp_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("S_SGP_SUM")+""),ccellFormat_right);
			sheet.addCell(number);
//			String s_sgp_sum_bili233=bili23.get("s_sgp_sum_bili")+"";
//			if(s_sgp_sum_bili233.equals("NaN")){
//				s_sgp_sum_bili233=0+"";
//			}
//			number=new Number(9,n,Double.parseDouble(s_sgp_sum_bili233),pcellFormat_right);
			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili23.get("s_sgp_sum_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili23.get("s_sgp_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"减:营业费用 ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("S_OPERATING_EXPENSES")+""),ccellFormat_right);
			sheet.addCell(number);		
//			String s_operating_expenses_bili21=bili21.get("s_operating_expenses_bili")+"";
//			if(s_operating_expenses_bili21.equals("NaN")){
//				s_operating_expenses_bili21=0+"";
//			}
//			number=new Number(3,n,Double.parseDouble(s_operating_expenses_bili21),pcellFormat_right);
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili21.get("s_operating_expenses_bili")+""))/100,pcellFormat_right);
			//number=new Number(3,n,Double.parseDouble(bili21.get("s_operating_expenses_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
//			String s_operating_expenses_bili=czlv12.get("s_operating_expenses_bili")+"";
//			if(s_operating_expenses_bili.equals("NaN")){
//				s_operating_expenses_bili=0+"";
//			}
//			number=new Number(4,n,Double.parseDouble(s_operating_expenses_bili),pcellFormat_right);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(czlv12.get("s_operating_expenses_bili")+""))/100,pcellFormat_right);
			//number=new Number(4,n,Double.parseDouble(czlv12.get("s_operating_expenses_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("S_OPERATING_EXPENSES")+""),ccellFormat_right);
			sheet.addCell(number);
//			String s_operating_expenses_bili22=bili22.get("s_operating_expenses_bili")+"";
//			if(s_operating_expenses_bili22.equals("Infinity")||s_operating_expenses_bili22.equals("NaN")){
//				s_operating_expenses_bili22=0+"";
//			}
//			number=new Number(6,n,Double.parseDouble(s_operating_expenses_bili22),pcellFormat_right);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili22.get("s_operating_expenses_bili")+""))/100,pcellFormat_right);
			//number=new Number(6,n,Double.parseDouble(bili22.get("s_operating_expenses_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
//			String s_operating_expenses_bili23=czlv23.get("s_operating_expenses_bili")+"";
//			if(s_operating_expenses_bili23.equals("NaN")){
//				s_operating_expenses_bili23=0+"";
//			}
//			number=new Number(7,n,Double.parseDouble(s_operating_expenses_bili23),pcellFormat_right);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(czlv23.get("s_operating_expenses_bili")+""))/100,pcellFormat_right);
			//number=new Number(7,n,Double.parseDouble(czlv23.get("s_operating_expenses_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("S_OPERATING_EXPENSES")+""),ccellFormat_right);
			sheet.addCell(number);
//			String s_operating_expenses_bili233=bili23.get("s_operating_expenses_bili")+"";
//			if(s_operating_expenses_bili233.equals("NaN")){
//				s_operating_expenses_bili233=0+"";
//			}
//			number=new Number(9,n,Double.parseDouble(s_operating_expenses_bili233),pcellFormat_right);
			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili23.get("s_operating_expenses_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili23.get("s_operating_expenses_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"营业利益 ",format6); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("S_BP_SUM")+""),ccellFormat_right);
			sheet.addCell(number);		
//			String s_bp_sum_bili21=bili21.get("s_bp_sum_bili")+"";
//			if(s_bp_sum_bili21.equals("NaN")){
//				s_bp_sum_bili21=0+"";
//			}
//			number=new Number(3,n,Double.parseDouble(s_bp_sum_bili21),pcellFormat_right);
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili21.get("s_bp_sum_bili")+""))/100,pcellFormat_right);
			//number=new Number(3,n,Double.parseDouble(bili21.get("s_bp_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
//			String s_bp_sum_bili=czlv12.get("s_bp_sum_bili")+"";
//			if(s_bp_sum_bili.equals("NaN")){
//				s_bp_sum_bili=0+"";
//			}
//			number=new Number(4,n,Double.parseDouble(s_bp_sum_bili),pcellFormat_right);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(czlv12.get("s_bp_sum_bili")+""))/100,pcellFormat_right);
			//number=new Number(4,n,Double.parseDouble(czlv12.get("s_bp_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("S_BP_SUM")+""),ccellFormat_right);
			sheet.addCell(number);
//			String s_bp_sum_bili22=bili22.get("s_bp_sum_bili")+"";
//			if(s_bp_sum_bili22.equals("-Infinity")||s_bp_sum_bili22.equals("Infinity")||s_bp_sum_bili22.equals("NaN")){
//				s_bp_sum_bili22=0+"";
//			}
//			number=new Number(6,n,Double.parseDouble(s_bp_sum_bili22),pcellFormat_right);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili22.get("s_bp_sum_bili")+""))/100,pcellFormat_right);
			//number=new Number(6,n,Double.parseDouble(bili22.get("s_bp_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
//			String s_bp_sum_bili23=czlv23.get("s_bp_sum_bili")+"";
//			if(s_bp_sum_bili23.equals("NaN")){
//				s_bp_sum_bili23=0+"";
//			}
//			number=new Number(7,n,Double.parseDouble(s_bp_sum_bili23),pcellFormat_right);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(czlv23.get("s_bp_sum_bili")+""))/100,pcellFormat_right);
			//number=new Number(7,n,Double.parseDouble(czlv23.get("s_bp_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("S_BP_SUM")+""),ccellFormat_right);
			sheet.addCell(number);
//			String s_bp_sum_bili233=bili23.get("s_bp_sum_bili")+"";
//			if(s_bp_sum_bili233.equals("NaN")){
//				s_bp_sum_bili233=0+"";
//			}
//			number=new Number(9,n,Double.parseDouble(s_bp_sum_bili233),pcellFormat_right);
			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili23.get("s_bp_sum_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili23.get("s_bp_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"营业外收入 ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("S_NONBUSINESS_INCOME")+""),ccellFormat_right);
			sheet.addCell(number);		
//			String s_nonbusiness_income_bili21=bili21.get("s_nonbusiness_income_bili")+"";
//			if(s_nonbusiness_income_bili21.equals("NaN")){
//				s_nonbusiness_income_bili21=0+"";
//			}
//			number=new Number(3,n,Double.parseDouble(s_nonbusiness_income_bili21),pcellFormat_right);
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili21.get("s_nonbusiness_income_bili")+""))/100,pcellFormat_right);
			//number=new Number(3,n,Double.parseDouble(bili21.get("s_nonbusiness_income_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
//			String s_nonbusiness_income_bili=czlv12.get("s_nonbusiness_income_bili")+"";
//			if(s_nonbusiness_income_bili.equals("NaN")){
//				s_nonbusiness_income_bili=0+"";
//			}
//			number=new Number(4,n,Double.parseDouble(s_nonbusiness_income_bili),pcellFormat_right);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(czlv12.get("s_nonbusiness_income_bili")+""))/100,pcellFormat_right);
			//number=new Number(4,n,Double.parseDouble(czlv12.get("s_nonbusiness_income_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("S_NONBUSINESS_INCOME")+""),ccellFormat_right);
			sheet.addCell(number);
//			String s_nonbusiness_income_bili22=bili22.get("s_nonbusiness_income_bili")+"";
//			if(s_nonbusiness_income_bili22.equals("NaN")){
//				s_nonbusiness_income_bili22=0+"";
//			}
//			number=new Number(6,n,Double.parseDouble(s_nonbusiness_income_bili22),pcellFormat_right);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili22.get("s_nonbusiness_income_bili")+""))/100,pcellFormat_right);
			//number=new Number(6,n,Double.parseDouble(bili22.get("s_nonbusiness_income_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
//			String s_nonbusiness_income_bili23=czlv23.get("s_nonbusiness_income_bili")+"";
//			if(s_nonbusiness_income_bili23.equals("NaN")){
//				s_nonbusiness_income_bili23=0+"";
//			}
//			number=new Number(7,n,Double.parseDouble(s_nonbusiness_income_bili23),pcellFormat_right);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(czlv23.get("s_nonbusiness_income_bili")+""))/100,pcellFormat_right);
			//number=new Number(7,n,Double.parseDouble(czlv23.get("s_nonbusiness_income_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("S_NONBUSINESS_INCOME")+""),ccellFormat_right);
			sheet.addCell(number);
//			String s_nonbusiness_income_bili233=bili23.get("s_nonbusiness_income_bili")+"";
//			if(s_nonbusiness_income_bili233.equals("NaN")){
//				s_nonbusiness_income_bili233=0+"";
//			}
//			number=new Number(9,n,Double.parseDouble(s_nonbusiness_income_bili233),pcellFormat_right);
			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili23.get("s_nonbusiness_income_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili23.get("s_nonbusiness_income_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"利息支出 ",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("S_INTEREST_EXPENSE")+""),ccellFormat_right);
			sheet.addCell(number);		
//			String s_interest_expense_bili21=bili21.get("s_interest_expense_bili")+"";
//			if(s_interest_expense_bili21.equals("NaN")){
//				s_interest_expense_bili21=0+"";
//			}
//			number=new Number(3,n,Double.parseDouble(s_interest_expense_bili21),pcellFormat_right);
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili21.get("s_interest_expense_bili")+""))/100,pcellFormat_right);
			//number=new Number(3,n,Double.parseDouble(bili21.get("s_interest_expense_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
//			String s_interest_expense_bili=czlv12.get("s_interest_expense_bili")+"";
//			if(s_interest_expense_bili.equals("NaN")){
//				s_interest_expense_bili=0+"";
//			}
//			number=new Number(4,n,Double.parseDouble(s_interest_expense_bili),pcellFormat_right);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(czlv12.get("s_interest_expense_bili")+""))/100,pcellFormat_right);
			//number=new Number(4,n,Double.parseDouble(czlv12.get("s_interest_expense_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("S_INTEREST_EXPENSE")+""),ccellFormat_right);
			sheet.addCell(number);
//			String s_interest_expense_bili22=bili22.get("s_interest_expense_bili")+"";
//			if(s_interest_expense_bili22.equals("NaN")){
//				s_interest_expense_bili22=0+"";
//			}
//			number=new Number(6,n,Double.parseDouble(s_interest_expense_bili22),pcellFormat_right);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili22.get("s_interest_expense_bili")+""))/100,pcellFormat_right);
			//number=new Number(6,n,Double.parseDouble(bili22.get("s_interest_expense_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
//			String s_interest_expense_bili23=czlv23.get("s_interest_expense_bili")+"";
//			if(s_interest_expense_bili23.equals("NaN")){
//				s_interest_expense_bili23=0+"";
//			}
//			number=new Number(7,n,Double.parseDouble(s_interest_expense_bili23),pcellFormat_right);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(czlv23.get("s_interest_expense_bili")+""))/100,pcellFormat_right);
			//number=new Number(7,n,Double.parseDouble(czlv23.get("s_interest_expense_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("S_INTEREST_EXPENSE")+""),ccellFormat_right);
			sheet.addCell(number);
//			String s_interest_expense_bili233=bili23.get("s_interest_expense_bili")+"";
//			if(s_interest_expense_bili233.equals("NaN")){
//				s_interest_expense_bili233=0+"";
//			}
//			number=new Number(9,n,Double.parseDouble(s_interest_expense_bili233),pcellFormat_right);
			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili23.get("s_interest_expense_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili23.get("s_interest_expense_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"其他营业外支出",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("S_OTHER_NONBUSINESS_EXPENSE")+""),ccellFormat_right);
			sheet.addCell(number);		
//			String s_other_nonbusiness_expense_bili21=bili21.get("s_other_nonbusiness_expense_bili")+"";
//			if(s_other_nonbusiness_expense_bili21.equals("NaN")){
//				s_other_nonbusiness_expense_bili21=0+"";
//			}
//			number=new Number(3,n,Double.parseDouble(s_other_nonbusiness_expense_bili21),pcellFormat_right);
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili21.get("s_other_nonbusiness_expense_bili")+""))/100,pcellFormat_right);
			//number=new Number(3,n,Double.parseDouble(bili21.get("s_other_nonbusiness_expense_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
//			String s_other_nonbusiness_expense_bili=czlv12.get("s_other_nonbusiness_expense_bili")+"";
//			if(s_other_nonbusiness_expense_bili.equals("NaN")){
//				s_other_nonbusiness_expense_bili=0+"";
//			}
//			number=new Number(4,n,Double.parseDouble(s_other_nonbusiness_expense_bili),pcellFormat_right);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(czlv12.get("s_other_nonbusiness_expense_bili")+""))/100,pcellFormat_right);
			//number=new Number(4,n,Double.parseDouble(czlv12.get("s_other_nonbusiness_expense_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("S_OTHER_NONBUSINESS_EXPENSE")+""),ccellFormat_right);
			sheet.addCell(number);
//			String s_other_nonbusiness_expense_bili22=bili22.get("s_other_nonbusiness_expense_bili")+"";
//			if(s_other_nonbusiness_expense_bili22.equals("NaN")){
//				s_other_nonbusiness_expense_bili22=0+"";
//			}
//			number=new Number(6,n,Double.parseDouble(s_other_nonbusiness_expense_bili22),pcellFormat_right);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili22.get("s_other_nonbusiness_expense_bili")+""))/100,pcellFormat_right);
			//number=new Number(6,n,Double.parseDouble(bili22.get("s_other_nonbusiness_expense_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
//			String s_other_nonbusiness_expense_bili23=czlv23.get("s_other_nonbusiness_expense_bili")+"";
//			if(s_other_nonbusiness_expense_bili23.equals("NaN")){
//				s_other_nonbusiness_expense_bili23=0+"";
//			}
//			number=new Number(7,n,Double.parseDouble(s_other_nonbusiness_expense_bili23),pcellFormat_right);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(czlv23.get("s_other_nonbusiness_expense_bili")+""))/100,pcellFormat_right);
			//number=new Number(7,n,Double.parseDouble(czlv23.get("s_other_nonbusiness_expense_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("S_OTHER_NONBUSINESS_EXPENSE")+""),ccellFormat_right);
			sheet.addCell(number);
//			String s_other_nonbusiness_expense_bili233=bili23.get("s_other_nonbusiness_expense_bili")+"";
//			if(s_other_nonbusiness_expense_bili233.equals("NaN")){
//				s_other_nonbusiness_expense_bili233=0+"";
//			}
//			number=new Number(9,n,Double.parseDouble(s_other_nonbusiness_expense_bili233),pcellFormat_right);
			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili23.get("s_other_nonbusiness_expense_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili23.get("s_other_nonbusiness_expense_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"减:营业外支出",format6); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("S_NBE_SUM")+""),ccellFormat_right);
			sheet.addCell(number);		
//			String s_nbe_sum_bili21=bili21.get("s_nbe_sum_bili")+"";
//			if(s_nbe_sum_bili21.equals("NaN")){
//				s_nbe_sum_bili21=0+"";
//			}
//			number=new Number(3,n,Double.parseDouble(s_nbe_sum_bili21),pcellFormat_right);
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili21.get("s_nbe_sum_bili")+""))/100,pcellFormat_right);
			//number=new Number(3,n,Double.parseDouble(bili21.get("s_nbe_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
//			String s_nbe_sum_bili=czlv12.get("s_nbe_sum_bili")+"";
//			if(s_nbe_sum_bili.equals("NaN")){
//				s_nbe_sum_bili=0+"";
//			}
//			number=new Number(4,n,Double.parseDouble(s_nbe_sum_bili),pcellFormat_right);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(czlv12.get("s_nbe_sum_bili")+""))/100,pcellFormat_right);
			//number=new Number(4,n,Double.parseDouble(czlv12.get("s_nbe_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("S_NBE_SUM")+""),ccellFormat_right);
			sheet.addCell(number);
//			String s_nbe_sum_bili22=bili22.get("s_nbe_sum_bili")+"";
//			if(s_nbe_sum_bili22.equals("NaN")){
//				s_nbe_sum_bili22=0+"";
//			}
//			number=new Number(6,n,Double.parseDouble(s_nbe_sum_bili22),pcellFormat_right);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili22.get("s_nbe_sum_bili")+""))/100,pcellFormat_right);
			//number=new Number(6,n,Double.parseDouble(bili22.get("s_nbe_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
//			String s_nbe_sum_bili23=czlv23.get("s_nbe_sum_bili")+"";
//			if(s_nbe_sum_bili23.equals("NaN")){
//				s_nbe_sum_bili23=0+"";
//			}
//			number=new Number(7,n,Double.parseDouble(s_nbe_sum_bili23),pcellFormat_right);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(czlv23.get("s_nbe_sum_bili")+""))/100,pcellFormat_right);
			//number=new Number(7,n,Double.parseDouble(czlv23.get("s_nbe_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("S_NBE_SUM")+""),ccellFormat_right);
			sheet.addCell(number);
//			String s_nbe_sum_bili233=bili23.get("s_nbe_sum_bili")+"";
//			if(s_nbe_sum_bili233.equals("NaN")){
//				s_nbe_sum_bili233=0+"";
//			}
//			number=new Number(9,n,Double.parseDouble(s_nbe_sum_bili233),pcellFormat_right);
			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili23.get("s_nbe_sum_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili23.get("s_nbe_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"税前损益",format6); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("S_BEFORE_SUM")+""),ccellFormat_right);
			sheet.addCell(number);		
//			String s_before_sum_bili21=bili21.get("s_before_sum_bili")+"";
//			if(s_before_sum_bili21.equals("NaN")){
//				s_before_sum_bili21=0+"";
//			}
//			number=new Number(3,n,Double.parseDouble(s_before_sum_bili21),pcellFormat_right);
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili21.get("s_before_sum_bili")+""))/100,pcellFormat_right);
			//number=new Number(3,n,Double.parseDouble(bili21.get("s_before_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
//			String s_before_sum_bili=czlv12.get("s_before_sum_bili")+"";
//			if(s_before_sum_bili.equals("NaN")){
//				s_before_sum_bili=0+"";
//			}
//			number=new Number(4,n,Double.parseDouble(s_before_sum_bili),pcellFormat_right);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(czlv12.get("s_before_sum_bili")+""))/100,pcellFormat_right);
//			number=new Number(4,n,Double.parseDouble(czlv12.get("s_before_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("S_BEFORE_SUM")+""),ccellFormat_right);
			sheet.addCell(number);
//			String s_before_sum_bili22=bili22.get("s_before_sum_bili")+"";
//			if(s_before_sum_bili22.equals("-Infinity")||s_before_sum_bili22.equals("Infinity")||s_before_sum_bili22.equals("NaN")){
//				s_before_sum_bili22=0+"";
//			}
//			number=new Number(6,n,Double.parseDouble(s_before_sum_bili22),pcellFormat_right);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili22.get("s_before_sum_bili")+""))/100,pcellFormat_right);
			//number=new Number(6,n,Double.parseDouble(bili22.get("s_before_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
//			String s_before_sum_bili23=czlv23.get("s_before_sum_bili")+"";
//			if(s_before_sum_bili23.equals("NaN")){
//				s_before_sum_bili23=0+"";
//			}
//			number=new Number(7,n,Double.parseDouble(s_before_sum_bili23),pcellFormat_right);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(czlv23.get("s_before_sum_bili")+""))/100,pcellFormat_right);
			//number=new Number(7,n,Double.parseDouble(czlv23.get("s_before_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("S_BEFORE_SUM")+""),ccellFormat_right);
			sheet.addCell(number);
//			String s_before_sum_bili233=bili23.get("s_before_sum_bili")+"";
//			if(s_before_sum_bili233.equals("NaN")){
//				s_before_sum_bili233=0+"";
//			}
//			number=new Number(9,n,Double.parseDouble(s_before_sum_bili233),pcellFormat_right);
			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili23.get("s_before_sum_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili23.get("s_before_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"减:所得税费用(利益)",format3); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("S_INCOME_TAX_EXPENSE")+""),ccellFormat_right);
			sheet.addCell(number);		
//			String s_income_tax_expense_bili21=bili21.get("s_income_tax_expense_bili")+"";
//			if(s_income_tax_expense_bili21.equals("NaN")){
//				s_income_tax_expense_bili21=0+"";
//			}
//			number=new Number(3,n,Double.parseDouble(s_income_tax_expense_bili21),pcellFormat_right);
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili21.get("s_income_tax_expense_bili")+""))/100,pcellFormat_right);
			//number=new Number(3,n,Double.parseDouble(bili21.get("s_income_tax_expense_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
//			String s_income_tax_expense_bili=czlv12.get("s_income_tax_expense_bili")+"";
//			if(s_income_tax_expense_bili.equals("NaN")){
//				s_income_tax_expense_bili=0+"";
//			}
//			number=new Number(4,n,Double.parseDouble(s_income_tax_expense_bili),pcellFormat_right);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(czlv12.get("s_income_tax_expense_bili")+""))/100,pcellFormat_right);
			//number=new Number(4,n,Double.parseDouble(czlv12.get("s_income_tax_expense_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("S_INCOME_TAX_EXPENSE")+""),ccellFormat_right);
			sheet.addCell(number);
//			String s_income_tax_expense_bili22=bili22.get("s_income_tax_expense_bili")+"";
//			if(s_income_tax_expense_bili22.equals("NaN")){
//				s_income_tax_expense_bili22=0+"";
//			}
//			number=new Number(6,n,Double.parseDouble(s_income_tax_expense_bili22),pcellFormat_right);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili22.get("s_income_tax_expense_bili")+""))/100,pcellFormat_right);
			//number=new Number(6,n,Double.parseDouble(bili22.get("s_income_tax_expense_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
//			String s_income_tax_expense_bili23=czlv23.get("s_income_tax_expense_bili")+"";
//			if(s_income_tax_expense_bili23.equals("NaN")){
//				s_income_tax_expense_bili23=0+"";
//			}
//			number=new Number(7,n,Double.parseDouble(s_income_tax_expense_bili23),pcellFormat_right);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(czlv23.get("s_income_tax_expense_bili")+""))/100,pcellFormat_right);
			//number=new Number(7,n,Double.parseDouble(czlv23.get("s_income_tax_expense_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("S_INCOME_TAX_EXPENSE")+""),ccellFormat_right);
			sheet.addCell(number);
//			String s_income_tax_expense_bili233=bili23.get("s_income_tax_expense_bili")+"";
//			if(s_income_tax_expense_bili233.equals("NaN")){
//				s_income_tax_expense_bili233=0+"";
//			}
//			number=new Number(9,n,Double.parseDouble(s_income_tax_expense_bili233),pcellFormat_right);
			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili23.get("s_income_tax_expense_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili23.get("s_income_tax_expense_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			sheet.setRowView(n,300,false);
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"税后损益",format8); 
			sheet.addCell(cell); 
			number=new Number(2,n,Double.parseDouble(obj1.get("S_AFTER_SUM")+""),ccellFormat_right);
			sheet.addCell(number);		
//			String s_after_sum_bili21=bili21.get("s_after_sum_bili")+"";
//			if(s_after_sum_bili21.equals("NaN")){
//				s_after_sum_bili21=0+"";
//			}
//			number=new Number(3,n,Double.parseDouble(s_after_sum_bili21),pcellFormat_right);
			number=new Number(3,n,Double.parseDouble(this.getDoubleNumString(bili21.get("s_after_sum_bili")+""))/100,pcellFormat_right);
			//number=new Number(3,n,Double.parseDouble(bili21.get("s_after_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
//			String s_after_sum_bili=czlv12.get("s_after_sum_bili")+"";
//			if(s_after_sum_bili.equals("NaN")){
//				s_after_sum_bili=0+"";
//			}
//			number=new Number(4,n,Double.parseDouble(s_after_sum_bili),pcellFormat_right);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(czlv12.get("s_after_sum_bili")+""))/100,pcellFormat_right);
			//number=new Number(4,n,Double.parseDouble(czlv12.get("s_after_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(obj2.get("S_AFTER_SUM")+""),ccellFormat_right);
			sheet.addCell(number);
//			String s_after_sum_bili22=bili22.get("s_after_sum_bili")+"";
//			if(s_after_sum_bili22.equals("-Infinity")||s_after_sum_bili22.equals("Infinity")||s_after_sum_bili22.equals("NaN")){
//				s_after_sum_bili22=0+"";
//			}
//			number=new Number(6,n,Double.parseDouble(s_after_sum_bili22),pcellFormat_right);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili22.get("s_after_sum_bili")+""))/100,pcellFormat_right);
			//number=new Number(6,n,Double.parseDouble(bili22.get("s_after_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
//			String s_after_sum_bili23=czlv23.get("s_after_sum_bili")+"";
//			if(s_after_sum_bili23.equals("NaN")){
//				s_after_sum_bili23=0+"";
//			}
//			number=new Number(7,n,Double.parseDouble(s_after_sum_bili23),pcellFormat_right);
			number=new Number(7,n,Double.parseDouble(this.getDoubleNumString(czlv23.get("s_after_sum_bili")+""))/100,pcellFormat_right);
			//number=new Number(7,n,Double.parseDouble(czlv23.get("s_after_sum_bili")+"")/100,pcellFormat_right);
			sheet.addCell(number);
			number=new Number(8,n,Double.parseDouble(obj3.get("S_AFTER_SUM")+""),ccellFormat_right);
			sheet.addCell(number);
//			String s_after_sum_bili233=bili23.get("s_after_sum_bili")+"";
//			if(s_after_sum_bili233.equals("NaN")){
//				s_after_sum_bili233=0+"";
//			}
//			number=new Number(9,n,Double.parseDouble(s_after_sum_bili233),pcellFormat_right);
			number=new Number(9,n,Double.parseDouble(this.getDoubleNumString(bili23.get("s_after_sum_bili")+""))/100,pcellFormat_right);
			//number=new Number(9,n,Double.parseDouble(bili23.get("s_after_sum_bili")+"")/100,pcellFormat_right);
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
			n++;
			cell=new Label(1,n,"上次说明：",format3);
			sheet.mergeCells(1, n, 9, n); 
			sheet.addCell(cell);
			n++ ;
			cell=new Label(1,n,((Map)exportMap.get("remark2Map")).get("REMARK") +"",format3);
			sheet.mergeCells(1, n, 9, n+5); 
			sheet.addCell(cell);
			n+=5;
			
			///////////////////////////////////////////////////////////////////////////////////
			sheet = wb.createSheet("RATIO", 3);
			
			sheet.setColumnView(0, 4);
			sheet.setColumnView(1, 10);
			sheet.setColumnView(2, 15);
			sheet.setColumnView(3, 15);
			sheet.setColumnView(4, 20);
			sheet.setColumnView(5, 20);
			sheet.setColumnView(6, 20);
			sheet.setColumnView(7, 4);
		
			n=0;
			
			cell=new Label(0,0,"");  
			sheet.addCell(cell);
			cell=new Label(1,0,"",format2_BOTTOM);  
			sheet.mergeCells(1, 0, 6, 0);
			sheet.addCell(cell);
			sheet.setRowView(0,300,false); 
			n=n+1;
			
			cell=new Label(0,1,""); 
			sheet.addCell(cell);
			cell=new Label(1,1,"共同比分析 ",format9); 
			sheet.mergeCells(1, 1, 6, 1);
			sheet.addCell(cell);
			sheet.setRowView(1,320,false); 
			n=n+1;
						
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"科目\\年度 ",format5); 
			sheet.mergeCells(1, n, 3, n);
			sheet.addCell(cell); 
			DateTime dateLabe3l=new DateTime(4,n, PROJECT_ITEM1,dateCellFormat_center);
			sheet.addCell(dateLabe3l);
			DateTime dateLabe32=new DateTime(5,n, PROJECT_ITEM2,dateCellFormat_center);
			sheet.addCell(dateLabe32);
			DateTime dateLabe33=new DateTime(6,n, PROJECT_ITEM3,dateCellFormat_center);
			sheet.addCell(dateLabe33); 
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"资产 ",format5); 
			sheet.mergeCells(1, n, 1, n+3);
			sheet.addCell(cell); 
			cell=new Label(2,n,"流动资产 ",format5); 
			sheet.mergeCells(2, n, 3, n);
			sheet.addCell(cell); 	
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(bili1.get("ca_sum_bili")+""))/100,pcellFormat_center);
//			number=new Number(4,n,Double.parseDouble(bili1.get("ca_sum_bili")+"")/100,pcellFormat_center);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(this.getDoubleNumString(bili2.get("ca_sum_bili")+""))/100,pcellFormat_center);
//			number=new Number(5,n,Double.parseDouble(bili2.get("ca_sum_bili")+"")/100,pcellFormat_center);
			sheet.addCell(number);
//			String ca_sum_bilinew=bili3.get("ca_sum_bili")+"";
//			if(ca_sum_bilinew.equals("NaN")){
//				ca_sum_bilinew=0+"";
//			}
//			number=new Number(6,n,Double.parseDouble(ca_sum_bilinew),pcellFormat_right);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili3.get("ca_sum_bili")+""))/100,pcellFormat_center);
			//number=new Number(6,n,Double.parseDouble(bili3.get("ca_sum_bili")+"")/100,pcellFormat_center);
			sheet.addCell(number);	
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(2,n,"固定资产 ",format5); 
			sheet.mergeCells(2, n, 3, n);
			sheet.addCell(cell); 	
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(bili1.get("fa_sum_bili")+""))/100,pcellFormat_center);
//			number=new Number(4,n,Double.parseDouble(bili1.get("fa_sum_bili")+"")/100,pcellFormat_center);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(this.getDoubleNumString(bili2.get("fa_sum_bili")+""))/100,pcellFormat_center);
//			number=new Number(5,n,Double.parseDouble(bili2.get("fa_sum_bili")+"")/100,pcellFormat_center);
			sheet.addCell(number);

			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili3.get("fa_sum_bili")+""))/100,pcellFormat_center);
			//number=new Number(6,n,Double.parseDouble(bili3.get("fa_sum_bili")+"")/100,pcellFormat_center);
			sheet.addCell(number);	
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(2,n,"长期资产 ",format5); 
			sheet.mergeCells(2, n, 3, n);
			sheet.addCell(cell); 	
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(bili1.get("lang_invest_bili")+""))/100,pcellFormat_center);
//			number=new Number(4,n,Double.parseDouble(bili1.get("lang_invest_bili")+"")/100,pcellFormat_center);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(this.getDoubleNumString(bili2.get("lang_invest_bili")+""))/100,pcellFormat_center);
//			number=new Number(5,n,Double.parseDouble(bili2.get("lang_invest_bili")+"")/100,pcellFormat_center);
			sheet.addCell(number);

			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili3.get("lang_invest_bili")+""))/100,pcellFormat_center);
			//number=new Number(6,n,Double.parseDouble(bili3.get("lang_invest_bili")+"")/100,pcellFormat_center);
			sheet.addCell(number);	
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(2,n,"其他资产 ",format5); 
			sheet.mergeCells(2, n, 3, n);
			sheet.addCell(cell); 	
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(bili1.get("other_assets_bili")+""))/100,pcellFormat_center);
//			number=new Number(4,n,Double.parseDouble(bili1.get("other_assets_bili")+"")/100,pcellFormat_center);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(this.getDoubleNumString(bili2.get("other_assets_bili")+""))/100,pcellFormat_center);
//			number=new Number(5,n,Double.parseDouble(bili2.get("other_assets_bili")+"")/100,pcellFormat_center);
			sheet.addCell(number);

			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili3.get("other_assets_bili")+""))/100,pcellFormat_center);
			//number=new Number(6,n,Double.parseDouble(bili3.get("other_assets_bili")+"")/100,pcellFormat_center);
			sheet.addCell(number);	
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"负债",format5); 
			sheet.mergeCells(1, n, 1, n+2);
			sheet.addCell(cell); 
			cell=new Label(2,n,"流动负债 ",format5); 
			sheet.mergeCells(2, n, 3, n);
			sheet.addCell(cell); 	
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(bili1.get("sd_sum_bili")+""))/100,pcellFormat_center);
//			number=new Number(4,n,Double.parseDouble(bili1.get("sd_sum_bili")+"")/100,pcellFormat_center);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(this.getDoubleNumString(bili2.get("sd_sum_bili")+""))/100,pcellFormat_center);
//			number=new Number(5,n,Double.parseDouble(bili2.get("sd_sum_bili")+"")/100,pcellFormat_center);
			sheet.addCell(number);

			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili3.get("sd_sum_bili")+""))/100,pcellFormat_center);
			//number=new Number(6,n,Double.parseDouble(bili3.get("sd_sum_bili")+"")/100,pcellFormat_center);
			sheet.addCell(number);	
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(2,n,"长期负债 ",format5); 
			sheet.mergeCells(2, n, 3, n);
			sheet.addCell(cell); 	
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(bili1.get("ld_sum_bili")+""))/100,pcellFormat_center);
//			number=new Number(4,n,Double.parseDouble(bili1.get("ld_sum_bili")+"")/100,pcellFormat_center);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(this.getDoubleNumString(bili2.get("ld_sum_bili")+""))/100,pcellFormat_center);
//			number=new Number(5,n,Double.parseDouble(bili2.get("ld_sum_bili")+"")/100,pcellFormat_center);
			sheet.addCell(number);

			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili3.get("ld_sum_bili")+""))/100,pcellFormat_center);
			//number=new Number(6,n,Double.parseDouble(bili3.get("ld_sum_bili")+"")/100,pcellFormat_center);
			sheet.addCell(number);	
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(2,n,"其他负债 ",format5); 
			sheet.mergeCells(2, n, 3, n);
			sheet.addCell(cell); 	
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(bili1.get("other_debt_bili")+""))/100,pcellFormat_center);
//			number=new Number(4,n,Double.parseDouble(bili1.get("other_debt_bili")+"")/100,pcellFormat_center);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(this.getDoubleNumString(bili2.get("other_debt_bili")+""))/100,pcellFormat_center);
//			number=new Number(5,n,Double.parseDouble(bili2.get("other_debt_bili")+"")/100,pcellFormat_center);
			sheet.addCell(number);

			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili3.get("other_debt_bili")+""))/100,pcellFormat_center);
			//number=new Number(6,n,Double.parseDouble(bili3.get("other_debt_bili")+"")/100,pcellFormat_center);
			sheet.addCell(number);	
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"股权 ",format5); 
			sheet.mergeCells(1, n, 1, n);
			sheet.addCell(cell); 
			cell=new Label(2,n,"股东权益  ",format5); 
			sheet.mergeCells(2, n, 3, n);
			sheet.addCell(cell); 	
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(bili1.get("real_sum_bili")+""))/100,pcellFormat_center);
//			number=new Number(4,n,Double.parseDouble(bili1.get("real_sum_bili")+"")/100,pcellFormat_center);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(this.getDoubleNumString(bili2.get("real_sum_bili")+""))/100,pcellFormat_center);
//			number=new Number(5,n,Double.parseDouble(bili2.get("real_sum_bili")+"")/100,pcellFormat_center);
			sheet.addCell(number);

			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili3.get("real_sum_bili")+""))/100,pcellFormat_center);
			//number=new Number(6,n,Double.parseDouble(bili3.get("real_sum_bili")+"")/100,pcellFormat_center);
			sheet.addCell(number);	
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);
			cell=new Label(1,n,"财务比率分析 ",format9); 
			sheet.mergeCells(1, n, 6, n);
			sheet.addCell(cell);
			sheet.setRowView(1,320,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"科目\\年度 ",format5); 
			sheet.mergeCells(1, n, 3, n);
			sheet.addCell(cell); 
			DateTime dateLabe32l=new DateTime(4,n, PROJECT_ITEM1,dateCellFormat_center);
			sheet.addCell(dateLabe32l);
			DateTime dateLabe322=new DateTime(5,n, PROJECT_ITEM2,dateCellFormat_center);
			sheet.addCell(dateLabe322);
			DateTime dateLabe323=new DateTime(6,n, PROJECT_ITEM3,dateCellFormat_center);
			sheet.addCell(dateLabe323); 
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"财务结构",format5); 
			sheet.mergeCells(1, n, 1, n+5);
			sheet.addCell(cell); 
			cell=new Label(2,n,"自有资本比率",format5); 
			sheet.mergeCells(2, n, 2, n+1);
			sheet.addCell(cell); 
			cell=new Label(3,n,"净值 ",format5); 
			sheet.addCell(cell);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(bili1.get("real_sum_bili")+""))/100,pcellFormat_center2);
//			number=new Number(4,n,Double.parseDouble(bili1.get("real_sum_bili")+"")/100,pcellFormat_center2);
			sheet.mergeCells(4, n, 4, n+1);
			sheet.addCell(number);
			number=new Number(5,n,Double.parseDouble(this.getDoubleNumString(bili2.get("real_sum_bili")+""))/100,pcellFormat_center2);
//			number=new Number(5,n,Double.parseDouble(bili2.get("real_sum_bili")+"")/100,pcellFormat_center2);
			sheet.mergeCells(5, n, 5, n+1);
			sheet.addCell(number);

			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(bili3.get("real_sum_bili")+""))/100,pcellFormat_center2);
			//number=new Number(6,n,Double.parseDouble(bili3.get("real_sum_bili")+"")/100,pcellFormat_center2);
			sheet.mergeCells(6, n, 6, n+1);
			sheet.addCell(number);	
			sheet.setRowView(n,300,false); 
			n=n+1;
								
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(3,n,"资产总额 ",format5); 
			sheet.addCell(cell);	
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			Map ratio1 = new HashMap();
			Map ratio2 = new HashMap();
			Map ratio3 = new HashMap();
			
			ratio1 = exportMap.get("ratio1");
			ratio2 = exportMap.get("ratio2");
			ratio3 = exportMap.get("ratio3");
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(2,n,"固定比率",format5); 
			sheet.mergeCells(2, n, 2, n+1);
			sheet.addCell(cell); 
			cell=new Label(3,n,"固定资产+长投",format5); 
			sheet.addCell(cell);

			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(ratio1.get("gudingbilv")+""))/100,pcellFormat_center2);
			//number=new Number(4,n,Double.parseDouble(ratio1.get("gudingbilv")+"")/100,pcellFormat_center2);
			sheet.mergeCells(4, n, 4, n+1);
			sheet.addCell(number);

			number=new Number(5,n,Double.parseDouble(this.getDoubleNumString(ratio2.get("gudingbilv")+""))/100,pcellFormat_center2);
			//number=new Number(5,n,Double.parseDouble(ratio2.get("gudingbilv")+"")/100,pcellFormat_center2);
			sheet.mergeCells(5, n, 5, n+1);
			sheet.addCell(number);

			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(ratio3.get("gudingbilv")+""))/100,pcellFormat_center2);
			//number=new Number(6,n,Double.parseDouble(ratio3.get("gudingbilv")+"")/100,pcellFormat_center2);
			sheet.mergeCells(6, n, 6, n+1);
			sheet.addCell(number);	
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(3,n,"净值+长期负债",format5); 
			sheet.addCell(cell);	
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(2,n,"负债比率",format5); 
			sheet.mergeCells(2, n, 2, n+1);
			sheet.addCell(cell); 
			cell=new Label(3,n,"负债总额",format5); 
			sheet.addCell(cell);

			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(ratio1.get("fuzhaibilv")+""))/100,pcellFormat_center2);
			//number=new Number(4,n,Double.parseDouble(ratio1.get("fuzhaibilv")+"")/100,pcellFormat_center2);
			sheet.mergeCells(4, n, 4, n+1);
			sheet.addCell(number);

			number=new Number(5,n,Double.parseDouble(this.getDoubleNumString(ratio2.get("fuzhaibilv")+""))/100,pcellFormat_center2);
			//number=new Number(5,n,Double.parseDouble(ratio2.get("fuzhaibilv")+"")/100,pcellFormat_center2);
			sheet.mergeCells(5, n, 5, n+1);
			sheet.addCell(number);

			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(ratio3.get("fuzhaibilv")+""))/100,pcellFormat_center2);
			//number=new Number(6,n,Double.parseDouble(ratio3.get("fuzhaibilv")+"")/100,pcellFormat_center2);
			sheet.mergeCells(6, n, 6, n+1);
			sheet.addCell(number);	
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(3,n,"净值",format5); 
			sheet.addCell(cell);	
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"经营效率",format5); 
			sheet.mergeCells(1, n, 1, n+7);
			sheet.addCell(cell); 
			cell=new Label(2,n,"应收账款周转率",format5); 
			sheet.mergeCells(2, n, 2, n+1);
			sheet.addCell(cell); 
			cell=new Label(3,n,"营收净值 ",format5); 
			sheet.addCell(cell);
			cell=new Label(4,n,DataUtil.StringUtil(Math.round(Double.parseDouble(this.getDoubleNumString(ratio1.get("yingshouzhangkuanzhouzhuanlv").toString()))*10)/100.0d)+"次",format5); 
			sheet.addCell(cell);
			cell=new Label(5,n,DataUtil.StringUtil(Math.round(Double.parseDouble(this.getDoubleNumString(ratio2.get("yingshouzhangkuanzhouzhuanlv").toString()))*10)/100.0d)+"次",format5); 
			sheet.addCell(cell);
			cell=new Label(6,n,DataUtil.StringUtil(Math.round(Double.parseDouble(this.getDoubleNumString(ratio3.get("yingshouzhangkuanzhouzhuanlv").toString()))*10)/100.0d)+"次",format5); 
			sheet.addCell(cell);		
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(3,n,"应收款项",format5); 
			sheet.addCell(cell);
			cell=new Label(4,n,DataUtil.StringUtil(ratio1.get("yingshouzhangkuanzhouzhuanlv2"))+"天",format5); 
			sheet.addCell(cell);
			cell=new Label(5,n,DataUtil.StringUtil(ratio2.get("yingshouzhangkuanzhouzhuanlv2"))+"天",format5); 
			sheet.addCell(cell);
			cell=new Label(6,n,DataUtil.StringUtil(ratio3.get("yingshouzhangkuanzhouzhuanlv2"))+"天",format5); 
			sheet.addCell(cell);		
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(2,n,"存货周转率",format5); 
			sheet.mergeCells(2, n, 2, n+1);
			sheet.addCell(cell); 
			cell=new Label(3,n,"销货成本 ",format5); 
			sheet.addCell(cell);
			cell=new Label(4,n,DataUtil.StringUtil(Math.round(Double.parseDouble(this.getDoubleNumString(ratio1.get("cunhuozhouzhuanlv").toString()))*10)/100.0d)+"次",format5); 
			sheet.addCell(cell);
			cell=new Label(5,n,DataUtil.StringUtil(Math.round(Double.parseDouble(this.getDoubleNumString(ratio2.get("cunhuozhouzhuanlv").toString()))*10)/100.0d)+"次",format5); 
			sheet.addCell(cell);
			cell=new Label(6,n,DataUtil.StringUtil(Math.round(Double.parseDouble(this.getDoubleNumString(ratio3.get("cunhuozhouzhuanlv").toString()))*10)/100.0d)+"次",format5); 
			sheet.addCell(cell);		
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(3,n,"存货",format5); 
			sheet.addCell(cell);
			cell=new Label(4,n,DataUtil.StringUtil(ratio1.get("cunhuozhouzhuanlv2"))+"天",format5); 
			sheet.addCell(cell);
			cell=new Label(5,n,DataUtil.StringUtil(ratio2.get("cunhuozhouzhuanlv2"))+"天",format5); 
			sheet.addCell(cell);
			cell=new Label(6,n,DataUtil.StringUtil(ratio3.get("cunhuozhouzhuanlv2"))+"天",format5); 
			sheet.addCell(cell);		
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(2,n,"应付账款周转率",format5); 
			sheet.mergeCells(2, n, 2, n+1);
			sheet.addCell(cell); 
			cell=new Label(3,n,"销货成本 ",format5); 
			sheet.addCell(cell);
			cell=new Label(4,n,DataUtil.StringUtil(Math.round(Double.parseDouble(this.getDoubleNumString(ratio1.get("yingfuzhangkuanzhouzhuanlv").toString()))*10)/100.0d)+"次",format5); 
			sheet.addCell(cell);
			cell=new Label(5,n,DataUtil.StringUtil(Math.round(Double.parseDouble(this.getDoubleNumString(ratio2.get("yingfuzhangkuanzhouzhuanlv").toString()))*10)/100.0d)+"次",format5); 
			sheet.addCell(cell);
			cell=new Label(6,n,DataUtil.StringUtil(Math.round(Double.parseDouble(this.getDoubleNumString(ratio3.get("yingfuzhangkuanzhouzhuanlv").toString()))*10)/100.0d)+"次",format5); 
			sheet.addCell(cell);		
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(3,n,"应付账款",format5); 
			sheet.addCell(cell);
			cell=new Label(4,n,DataUtil.StringUtil(ratio1.get("yingfuzhangkuanzhouzhuanlv2"))+"天",format5); 
			sheet.addCell(cell);
			cell=new Label(5,n,DataUtil.StringUtil(ratio2.get("yingfuzhangkuanzhouzhuanlv2"))+"天",format5); 
			sheet.addCell(cell);
			cell=new Label(6,n,DataUtil.StringUtil(ratio3.get("yingfuzhangkuanzhouzhuanlv2"))+"天",format5); 
			sheet.addCell(cell);		
			sheet.setRowView(n,300,false); 
			n=n+1;	
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(2,n,"总资产周转率",format5); 
			sheet.mergeCells(2, n, 2, n+1);
			sheet.addCell(cell); 
			cell=new Label(3,n,"营收净值",format5); 
			sheet.addCell(cell);
			cell=new Label(4,n,DataUtil.StringUtil(Math.round(Double.parseDouble(this.getDoubleNumString(ratio1.get("zongzichanzhouzhuanlv").toString()))*10)/100.0d)+"次",format5); 
			sheet.addCell(cell);
			cell=new Label(5,n,DataUtil.StringUtil(Math.round(Double.parseDouble(this.getDoubleNumString(ratio2.get("zongzichanzhouzhuanlv").toString()))*10)/100.0d)+"次",format5); 
			sheet.addCell(cell);
			cell=new Label(6,n,DataUtil.StringUtil(Math.round(Double.parseDouble(this.getDoubleNumString(ratio3.get("zongzichanzhouzhuanlv").toString()))*10)/100.0d)+"次",format5); 
			sheet.addCell(cell);		
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(3,n,"资产总额",format5); 
			sheet.addCell(cell);	
			cell=new Label(4,n,DataUtil.StringUtil(ratio1.get("zongzichanzhouzhuanlv2"))+"天",format5); 
			sheet.addCell(cell);
			cell=new Label(5,n,DataUtil.StringUtil(ratio2.get("zongzichanzhouzhuanlv2"))+"天",format5); 
			sheet.addCell(cell);
			cell=new Label(6,n,DataUtil.StringUtil(ratio3.get("zongzichanzhouzhuanlv2"))+"天",format5); 
			sheet.addCell(cell);		
			sheet.setRowView(n,300,false); 
			n=n+1;	
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"偿还能力",format5); 
			sheet.mergeCells(1, n, 1, n+7);
			sheet.addCell(cell); 
			cell=new Label(2,n,"流动比率",format5); 
			sheet.mergeCells(2, n, 2, n+1);
			sheet.addCell(cell); 
			cell=new Label(3,n,"流动资产",format5); 
			sheet.addCell(cell);

			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(ratio1.get("liudongbilv")+""))/100,pcellFormat_center2);
			//number=new Number(4,n,Double.parseDouble(ratio1.get("liudongbilv")+"")/100,pcellFormat_center2);
			sheet.mergeCells(4, n, 4, n+1);
			sheet.addCell(number);

			number=new Number(5,n,Double.parseDouble(this.getDoubleNumString(ratio2.get("liudongbilv")+""))/100,pcellFormat_center2);
			//number=new Number(5,n,Double.parseDouble(ratio2.get("liudongbilv")+"")/100,pcellFormat_center2);
			sheet.mergeCells(5, n, 5, n+1);
			sheet.addCell(number);

			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(ratio3.get("liudongbilv")+""))/100,pcellFormat_center2);
			//number=new Number(6,n,Double.parseDouble(ratio3.get("liudongbilv")+"")/100,pcellFormat_center2);
			sheet.mergeCells(6, n, 6, n+1);
			sheet.addCell(number);		
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(3,n,"流动负债",format5); 
			sheet.addCell(cell);	
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(2,n,"速动比率",format5); 
			sheet.mergeCells(2, n, 2, n+1);
			sheet.addCell(cell); 
			cell=new Label(3,n,"速动资产",format5); 
			sheet.addCell(cell);

			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(ratio1.get("sudongbilv")+""))/100,pcellFormat_center2);
			//number=new Number(4,n,Double.parseDouble(ratio1.get("sudongbilv")+"")/100,pcellFormat_center2);
			sheet.mergeCells(4, n, 4, n+1);
			sheet.addCell(number);

			number=new Number(5,n,Double.parseDouble(this.getDoubleNumString(ratio2.get("sudongbilv")+""))/100,pcellFormat_center2);
			//number=new Number(5,n,Double.parseDouble(ratio2.get("sudongbilv")+"")/100,pcellFormat_center2);
			sheet.mergeCells(5, n, 5, n+1);
			sheet.addCell(number);

			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(ratio3.get("sudongbilv")+""))/100,pcellFormat_center2);
			//number=new Number(6,n,Double.parseDouble(ratio3.get("sudongbilv")+"")/100,pcellFormat_center2);
			sheet.mergeCells(6, n, 6, n+1);
			sheet.addCell(number);		
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(3,n,"流动负债",format5); 
			sheet.addCell(cell);	
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(2,n,"利息保障倍数",format5); 
			sheet.mergeCells(2, n, 2, n+1);
			sheet.addCell(cell); 
			cell=new Label(3,n,"税前息前盈余",format5); 
			sheet.addCell(cell);
			if(ratio1.get("lixibaozhangbeishu")!=null){

				number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(ratio1.get("lixibaozhangbeishu")+""))/100,pcellFormat_center2);
				//number=new Number(4,n,Double.parseDouble(ratio1.get("lixibaozhangbeishu")+"")/100,pcellFormat_center2);
				sheet.mergeCells(4, n, 4, n+1);
				sheet.addCell(number);
			}else{
				cell=new Label(4,n,"N/A",format5);
				sheet.mergeCells(4, n, 4, n+1);
				sheet.addCell(cell);
			}
			if(ratio2.get("lixibaozhangbeishu")!=null){

				number=new Number(5,n,Double.parseDouble(this.getDoubleNumString(ratio2.get("lixibaozhangbeishu")+""))/100,pcellFormat_center2);
				//number=new Number(5,n,Double.parseDouble(ratio2.get("lixibaozhangbeishu")+"")/100,pcellFormat_center2);
				sheet.mergeCells(5, n, 5, n+1);
				sheet.addCell(number);
			}else{
				cell=new Label(5,n,"N/A",format5);
				sheet.mergeCells(5, n, 5, n+1);
				sheet.addCell(cell);
			}
			if(ratio3.get("lixibaozhangbeishu")!=null){

				number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(ratio3.get("lixibaozhangbeishu")+""))/100,pcellFormat_center2);
				//number=new Number(6,n,Double.parseDouble(ratio3.get("lixibaozhangbeishu")+"")/100,pcellFormat_center2);
				sheet.mergeCells(6, n, 6, n+1);
				sheet.addCell(number);
			}else{
				cell=new Label(6,n,"N/A",format5);
				sheet.mergeCells(6, n, 6, n+1);
				sheet.addCell(cell);
			}	
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(3,n,"利息支出 ",format5); 
			sheet.addCell(cell);	
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(2,n,"",format5); 
			sheet.mergeCells(2, n, 2, n+1);
			sheet.addCell(cell); 
			cell=new Label(3,n,"利息支出",format5); 
			sheet.addCell(cell);

			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(ratio1.get("changhuannengli")+""))/100,pcellFormat_center2);
			//number=new Number(4,n,Double.parseDouble(ratio1.get("changhuannengli")+"")/100,pcellFormat_center2);
			sheet.mergeCells(4, n, 4, n+1);
			sheet.addCell(number);

			number=new Number(5,n,Double.parseDouble(this.getDoubleNumString(ratio2.get("changhuannengli")+""))/100,pcellFormat_center2);
			//number=new Number(5,n,Double.parseDouble(ratio2.get("changhuannengli")+"")/100,pcellFormat_center2);
			sheet.mergeCells(5, n, 5, n+1);
			sheet.addCell(number);

			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(ratio3.get("changhuannengli")+""))/100,pcellFormat_center2);
			//number=new Number(6,n,Double.parseDouble(ratio3.get("changhuannengli")+"")/100,pcellFormat_center2);
			sheet.mergeCells(6, n, 6, n+1);
			sheet.addCell(number);		
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(3,n,"营收净值",format5); 
			sheet.addCell(cell);	
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(1,n,"获利能力",format5); 
			sheet.mergeCells(1, n, 1, n+9);
			sheet.addCell(cell); 
			cell=new Label(2,n,"毛利率",format5); 
			sheet.mergeCells(2, n, 2, n+1);
			sheet.addCell(cell); 
			cell=new Label(3,n,"营业毛利",format5); 
			sheet.addCell(cell);

			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(ratio1.get("hl_maolilv")+""))/100,pcellFormat_center2);
			//number=new Number(4,n,Double.parseDouble(ratio1.get("hl_maolilv")+"")/100,pcellFormat_center2);
			sheet.mergeCells(4, n, 4, n+1);
			sheet.addCell(number);

			number=new Number(5,n,Double.parseDouble(this.getDoubleNumString(ratio2.get("hl_maolilv")+""))/100,pcellFormat_center2);
			//number=new Number(5,n,Double.parseDouble(ratio2.get("hl_maolilv")+"")/100,pcellFormat_center2);
			sheet.mergeCells(5, n, 5, n+1);
			sheet.addCell(number);

			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(ratio3.get("hl_maolilv")+""))/100,pcellFormat_center2);
			//number=new Number(6,n,Double.parseDouble(ratio3.get("hl_maolilv")+"")/100,pcellFormat_center2);
			sheet.mergeCells(6, n, 6, n+1);
			sheet.addCell(number);		
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(3,n,"营收净值",format5); 
			sheet.addCell(cell);	
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);  
			cell=new Label(2,n,"营业利益率",format5); 
			sheet.mergeCells(2, n, 2, n+1);
			sheet.addCell(cell); 
			cell=new Label(3,n,"营业利益",format5); 
			sheet.addCell(cell);

			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(ratio1.get("hl_yingyeliyilv")+""))/100,pcellFormat_center2);
			//number=new Number(4,n,Double.parseDouble(ratio1.get("hl_yingyeliyilv")+"")/100,pcellFormat_center2);
			sheet.mergeCells(4, n, 4, n+1);
			sheet.addCell(number);

			number=new Number(5,n,Double.parseDouble(this.getDoubleNumString(ratio2.get("hl_yingyeliyilv")+""))/100,pcellFormat_center2);
			//number=new Number(5,n,Double.parseDouble(ratio2.get("hl_yingyeliyilv")+"")/100,pcellFormat_center2);
			sheet.mergeCells(5, n, 5, n+1);
			sheet.addCell(number);

			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(ratio3.get("hl_yingyeliyilv")+""))/100,pcellFormat_center2);
			//number=new Number(6,n,Double.parseDouble(ratio3.get("hl_yingyeliyilv")+"")/100,pcellFormat_center2);
			sheet.mergeCells(6, n, 6, n+1);
			sheet.addCell(number);		
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(3,n,"营收净值",format5); 
			sheet.addCell(cell);	
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);  
			cell=new Label(2,n,"纯益率",format5); 
			sheet.mergeCells(2, n, 2, n+1);
			sheet.addCell(cell); 
			cell=new Label(3,n,"税后纯益",format5); 
			sheet.addCell(cell);

			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(ratio1.get("hl_chunyilv")+""))/100,pcellFormat_center2);
			//number=new Number(4,n,Double.parseDouble(ratio1.get("hl_chunyilv")+"")/100,pcellFormat_center2);
			sheet.mergeCells(4, n, 4, n+1);
			sheet.addCell(number);

			number=new Number(5,n,Double.parseDouble(this.getDoubleNumString(ratio2.get("hl_chunyilv")+""))/100,pcellFormat_center2);
			//number=new Number(5,n,Double.parseDouble(ratio2.get("hl_chunyilv")+"")/100,pcellFormat_center2);
			sheet.mergeCells(5, n, 5, n+1);
			sheet.addCell(number);

			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(ratio3.get("hl_chunyilv")+""))/100,pcellFormat_center2);
			//number=new Number(6,n,Double.parseDouble(ratio3.get("hl_chunyilv")+"")/100,pcellFormat_center2);
			sheet.mergeCells(6, n, 6, n+1);
			sheet.addCell(number);		
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(3,n,"营收净值",format5); 
			sheet.addCell(cell);	
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);  
			cell=new Label(2,n,"净值获利率",format5); 
			sheet.mergeCells(2, n, 2, n+1);
			sheet.addCell(cell); 
			cell=new Label(3,n,"税后纯益",format5); 
			sheet.addCell(cell);

			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(ratio1.get("hl_jingzhihuolilv")+""))/100,pcellFormat_center2);
			//number=new Number(4,n,Double.parseDouble(ratio1.get("hl_jingzhihuolilv")+"")/100,pcellFormat_center2);
			sheet.mergeCells(4, n, 4, n+1);
			sheet.addCell(number);

			number=new Number(5,n,Double.parseDouble(this.getDoubleNumString(ratio2.get("hl_jingzhihuolilv")+""))/100,pcellFormat_center2);
			//number=new Number(5,n,Double.parseDouble(ratio2.get("hl_jingzhihuolilv")+"")/100,pcellFormat_center2);
			sheet.mergeCells(5, n, 5, n+1);
			sheet.addCell(number);

			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(ratio3.get("hl_jingzhihuolilv")+""))/100,pcellFormat_center2);
			//number=new Number(6,n,Double.parseDouble(ratio3.get("hl_jingzhihuolilv")+"")/100,pcellFormat_center2);
			sheet.mergeCells(6, n, 6, n+1);
			sheet.addCell(number);		
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(3,n,"净值",format5); 
			sheet.addCell(cell);	
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell);  
			cell=new Label(2,n,"总资产获利率",format5); 
			sheet.mergeCells(2, n, 2, n+1);
			sheet.addCell(cell); 
			cell=new Label(3,n,"税后纯益",format5); 
			sheet.addCell(cell);
//			String zongzichanhuolilv=ratio1.get("zongzichanhuolilv")+"";
//			if(zongzichanhuolilv.equals("NaN")||zongzichanhuolilv.equals("Infinity")){
//				zongzichanhuolilv=0+"";
//			}
//			number=new Number(4,n,Double.parseDouble(zongzichanhuolilv),pcellFormat_center2);
			number=new Number(4,n,Double.parseDouble(this.getDoubleNumString(ratio1.get("zongzichanhuolilv")+""))/100,pcellFormat_center2);
			//number=new Number(4,n,Double.parseDouble(ratio1.get("zongzichanhuolilv")+"")/100,pcellFormat_center2);
			sheet.mergeCells(4, n, 4, n+1);
			sheet.addCell(number);
//			String zongzichanhuolilv2=ratio2.get("zongzichanhuolilv")+"";
//			if(zongzichanhuolilv2.equals("NaN")||zongzichanhuolilv2.equals("Infinity")){
//				zongzichanhuolilv2=0+"";
//			}
//			number=new Number(5,n,Double.parseDouble(zongzichanhuolilv2),pcellFormat_center2);
			number=new Number(5,n,Double.parseDouble(this.getDoubleNumString(ratio2.get("zongzichanhuolilv")+""))/100,pcellFormat_center2);
			//number=new Number(5,n,Double.parseDouble(ratio2.get("zongzichanhuolilv")+"")/100,pcellFormat_center2);
			sheet.mergeCells(5, n, 5, n+1);
			sheet.addCell(number);
//			String zongzichanhuolilv3=ratio3.get("zongzichanhuolilv")+"";
//			if(zongzichanhuolilv3.equals("NaN")||zongzichanhuolilv3.equals("Infinity")){
//				zongzichanhuolilv3=0+"";
//			}
//			number=new Number(6,n,Double.parseDouble(zongzichanhuolilv3),pcellFormat_center2);
			number=new Number(6,n,Double.parseDouble(this.getDoubleNumString(ratio3.get("zongzichanhuolilv")+""))/100,pcellFormat_center2);
			//number=new Number(6,n,Double.parseDouble(ratio3.get("zongzichanhuolilv")+"")/100,pcellFormat_center2);
			sheet.mergeCells(6, n, 6, n+1);
			sheet.addCell(number);		
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			cell=new Label(0,n,""); 
			sheet.addCell(cell); 
			cell=new Label(3,n,"资产总额",format5); 
			sheet.addCell(cell);	
			sheet.setRowView(n,300,false); 
			n=n+1;
			
			
		} catch (Exception e) {
			
			LogPrint.getLogStackTrace(e, logger);
		}

		return baos;
	}
	public String getDoubleNumString(String objString){
		if(objString.equals("NaN")||objString.equals("Infinity")||objString.equals("-Infinity")){
			return "0";
		}else{
			return objString;
		}
	}

}
