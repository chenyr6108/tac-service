package com.brick.collection.service;
 

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.brick.util.DataUtil;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;


public class ExportExcel {
	Log logger = LogFactory.getLog(ExportExcel.class);
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

	@SuppressWarnings("unchecked")
	public ByteArrayOutputStream export(String name,List list) {
		 
		WritableSheet sheet = null;
		try {
			/* 解决中文乱码 */
			workbookSettings.setEncoding("ISO-8859-1");
			sheet = wb.createSheet(name, 1);
			// 大标题 字体黑体 20号 水平|垂直 居中对齐 加粗
			WritableFont font1 = new WritableFont(
					WritableFont.createFont("黑体"), 15, WritableFont.BOLD);
			WritableCellFormat format1 = new WritableCellFormat(font1);
			format1.setAlignment(Alignment.CENTRE);
			format1.setVerticalAlignment(VerticalAlignment.CENTRE);

			WritableFont font2 = new WritableFont(
					WritableFont.createFont("宋体"), 11, WritableFont.BOLD);
			WritableCellFormat format2 = new WritableCellFormat(font2);
			format2.setAlignment(Alignment.CENTRE);
			format2.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format2.setBorder(Border.ALL, BorderLineStyle.THIN);
			format2.setWrap(true);
			// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
			WritableFont font3 = new WritableFont(
					WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
			WritableCellFormat format3 = new WritableCellFormat(font3);
			format3.setAlignment(Alignment.LEFT);
			format3.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format3.setWrap(true);
			WritableFont font4 = new WritableFont(
					WritableFont.createFont("宋体"), 11);
			WritableCellFormat format4 = new WritableCellFormat(font4);
			format4.setAlignment(Alignment.CENTRE);
			format4.setVerticalAlignment(VerticalAlignment.CENTRE);
			format4.setBorder(Border.ALL, BorderLineStyle.THIN);
			format4.setWrap(true);

			WritableFont font5 = new WritableFont(
					WritableFont.createFont("宋体"), 12, WritableFont.BOLD);
			WritableCellFormat format5 = new WritableCellFormat(font5);
			format5.setAlignment(Alignment.CENTRE);
			format5.setVerticalAlignment(VerticalAlignment.CENTRE);

			// 设置列宽
			sheet.setColumnView(0, 7);
			sheet.setColumnView(1, 15);
			sheet.setColumnView(2, 35);
			sheet.setColumnView(3, 20);
			sheet.setColumnView(4, 20);
			sheet.setColumnView(5, 20);
			sheet.setColumnView(6, 15);
			sheet.setColumnView(7, 15);
			sheet.setColumnView(8, 15);
			sheet.setColumnView(9, 12);
			sheet.setColumnView(10, 15);
			sheet.setColumnView(11, 12);
			sheet.setColumnView(12, 15); 
			sheet.setColumnView(13, 15);
			sheet.setColumnView(14, 15);
			sheet.setColumnView(15, 15);
			Label cell = null;
			//金钱格式右对齐
			NumberFormat cnum=new NumberFormat("#,##0.00");
			WritableCellFormat ccellFormat_right=new WritableCellFormat(cnum);
			Number number=null;
           // sheet.mergeCells(0, 8, 0, 8);
			// sheet.setRowView(0, 1000);
			// sheet.setColumnView(0, 20);
			cell = new Label(0, 0, "序号", format2);
			sheet.addCell(cell);
			cell = new Label(1, 0, "税编", format2);
			sheet.addCell(cell);
			cell = new Label(2, 0, "承租人名称", format2);
			sheet.addCell(cell);
			cell = new Label(3, 0, "承租人编号", format2);
			sheet.addCell(cell);

			cell = new Label(4, 0, "支付表编号", format2);
			sheet.addCell(cell);
			cell = new Label(5, 0, "支付方式", format2);
			sheet.addCell(cell);
			cell = new Label(6, 0, "期次", format2);
			sheet.addCell(cell);

			cell = new Label(7, 0, "起日", format2);
			sheet.addCell(cell);
			cell = new Label(8, 0, "迄日", format2);
			sheet.addCell(cell);

			cell = new Label(9, 0, "应付日期", format2);
			sheet.addCell(cell);
			cell = new Label(10, 0, "应付租金", format2);
			sheet.addCell(cell);
			cell = new Label(11, 0, "预开日", format2);
			sheet.addCell(cell);

			cell = new Label(12, 0, "发票日期", format2);
			sheet.addCell(cell);
			cell = new Label(13, 0, "发票编号", format2);
			sheet.addCell(cell);
			cell = new Label(14, 0, "开票人", format2);
			sheet.addCell(cell);
			cell = new Label(15, 0, "利息", format2);
			sheet.addCell(cell);
			
			
			int i=1;			
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				Map object = (Map) iterator.next();
				cell = new Label(0, i, object.get("RECD_ID").toString(), format3);
				sheet.addCell(cell);
				cell = new Label(1, i, object.get("CORP_TAX_CODE").toString(), format3);
				sheet.addCell(cell);
				cell = new Label(2, i, object.get("CUST_NAME").toString(), format3);
				sheet.addCell(cell);
				cell = new Label(3, i, object.get("CUST_CODE").toString(), format3);
				sheet.addCell(cell);
				cell = new Label(4, i, object.get("RECP_CODE").toString(), format3);
				sheet.addCell(cell);
				if (object.get("PAY_WAY").toString().equals("11")) {
					object.put("fs", "期初等额本息支付");				
				}
				if (object.get("PAY_WAY").toString().equals("12")) {
					object.put("fs", "期初等额本金支付");				
				}
				if (object.get("PAY_WAY").toString().equals("13")) {
					object.put("fs", "期初不等额支付");				
				}
				if (object.get("PAY_WAY").toString().equals("21")) {
					object.put("fs", "期末等额本息支付");				
				}
				if (object.get("PAY_WAY").toString().equals("22")) {
					object.put("fs", "期末等额本金支付");				
				}
				if (object.get("PAY_WAY").toString().equals("23")) {
					object.put("fs", "期末不等额支付");				
				}									
				cell = new Label(5, i, object.get("fs").toString(), format3);
				sheet.addCell(cell);
				cell = new Label(6, i, object.get("PERIOD_NUM").toString()+"/"+object.get("LEASE_PERIOD").toString(), format3);
				sheet.addCell(cell);
//				if (DataUtil.intUtil(object.get("PAY_WAY"))<20) {
//					object.put("qi",object.get("QI").toString());	
//				}
//				else{
//					object.put("qi",object.get("ZHI").toString());
//				}
//				cell = new Label(7, i,object.get("qi").toString(), format3);
				cell = new Label(7, i,object.get("QI_DATE").toString(), format3);
				sheet.addCell(cell);
//				cell = new Label(8, i,object.get("ZHI").toString(), format3);
				cell = new Label(8, i,object.get("QIQI_DATE").toString(), format3);
				sheet.addCell(cell);
				cell = new Label(9, i,object.get("PAY_DATE").toString(), format3);
				sheet.addCell(cell);
				if (object.get("IRR_MONTH_PRICE")==null) {
					object.put("IRR_MONTH_PRICE","0");	
				}
				number=new Number(10,i,Double.parseDouble(object.get("IRR_MONTH_PRICE").toString()),ccellFormat_right);
				sheet.addCell(number); 				
				if (object.get("RESERVE_TIME")==null) {
					object.put("Y",object.get("YU").toString());	
				}
				else{
					object.put("Y",object.get("RESERVE_TIME").toString());
				}				
				cell = new Label(11, i,object.get("Y").toString(), format3);
				sheet.addCell(cell);			
				cell = new Label(12, i,"", format3);
				sheet.addCell(cell);
				cell = new Label(13, i,"", format3);
				sheet.addCell(cell);
				cell = new Label(14, i,"", format3);
				sheet.addCell(cell);
				number=new Number(15,i,(Double)object.get("REN_PRICE"),ccellFormat_right);
				sheet.addCell(number); 							
				i++;
			}			


			// 写入文件
			System.out.println("success");

			// wb.close();
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		// finally {
		// try {
		// if (wb != null) {
		// wb.close();
		// }
		// } catch (WriteException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
		return baos;
	}

	public void close() {
		try {
			wb.write();
			wb.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}

}
