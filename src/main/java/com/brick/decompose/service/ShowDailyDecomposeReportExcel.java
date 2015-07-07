package com.brick.decompose.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import com.brick.util.DataUtil;
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

public class ShowDailyDecomposeReportExcel extends AService {

	Log logger = LogFactory.getLog(ShowDailyDecomposeReportExcel.class);
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
	
	//Add by Michael 2012-3-22 每日销账报表
	@SuppressWarnings("unchecked")
	public void dailyFinaDecomposeReportDetailToExcel(Context context)
	{
		String ficb_type="待分解来款";		
		context.contextMap.put("ficb_type", ficb_type);
		
		Map outputMap = new HashMap();

		List finaTodayFinaIncome=new ArrayList();;//现金销账
		List finaLastDecompose=new ArrayList();;//暂收款销账
		List finaLastDynamicDecompose=new ArrayList();;//暂收款余额变动表	

		List errList = context.errList ;
		try {
			finaTodayFinaIncome = (List) DataAccessor.query("decompose.getDailyCurrencyDecomposeRpt", context.contextMap, DataAccessor.RS_TYPE.LIST);
			finaLastDecompose = (List) DataAccessor.query("decompose.getLastDecomposeRpt", context.contextMap, DataAccessor.RS_TYPE.LIST);
			finaLastDynamicDecompose=(List) DataAccessor.query("decompose.getLastDynamicDecomposeRpt", context.contextMap, DataAccessor.RS_TYPE.LIST);
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("财务报表--销账日报表出错!请联系管理员");			
		}
		
		ByteArrayOutputStream baos = null;
		String strFileName = "销账日报表.xls";
		
		ShowDailyDecomposeReportExcel showDecomposeReportExcel = new ShowDailyDecomposeReportExcel();
		showDecomposeReportExcel.createexl();
		baos = showDecomposeReportExcel.exportDailyFinaDecomposeReportDetailExcel(context,finaTodayFinaIncome,finaLastDecompose,finaLastDynamicDecompose);
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");		
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
			ServletOutputStream out1 = context.response.getOutputStream();
			showDecomposeReportExcel.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}		

	
	//Add by Michael 2012-3-22 销账日报表
public ByteArrayOutputStream exportDailyFinaDecomposeReportDetailExcel(Context context,List<Map> finaTodayFinaIncome,List<Map> finaLastDecompose,List<Map> finaLastDynamicDecompose ) {
			WritableSheet sheet = null;
			
			try {
				workbookSettings.setEncoding("ISO-8859-1");
				sheet = wb.createSheet("销账日报表", 1);
				
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
				sheet.setColumnView(2, 40);
				sheet.setColumnView(3,20);
				sheet.setColumnView(4,20);
				sheet.setColumnView(5, 15);
				sheet.setColumnView(6, 15);
				sheet.setColumnView(7, 40);
				sheet.setColumnView(8, 4);
				
				int n=0;
				Label cell = null;				
				
				cell=new Label(0,0,"");  
				sheet.addCell(cell);
				cell=new Label(1,0,"",format2_BOTTOM);  
				sheet.mergeCells(1, 0, 7, 0);
				sheet.addCell(cell);
				n=n+1;
				sheet.setRowView(0,300,false); 
				
				cell=new Label(0,1,""); 
				sheet.addCell(cell);
				cell=new Label(1,1,"现金销账",format4); 
				sheet.mergeCells(1, 1, 7, 1); 
				sheet.addCell(cell);
				n=n+1;
				sheet.setRowView(1,320,false); 
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				cell=new Label(1,n,"来款日期",format5); 
				sheet.mergeCells(1, n, 1, n); 
				sheet.addCell(cell); 
				cell=new Label(2,n,"承租人",format5); 
				sheet.mergeCells(2, n, 2, n); 
				sheet.addCell(cell); 
				cell=new Label(3,n,"客户编号",format5); 
				sheet.mergeCells(3, n,3, n); 
				sheet.addCell(cell); 
				cell=new Label(4,n,"合同号",format5); 
				sheet.mergeCells(4, n,4, n); 
				sheet.addCell(cell); 
				cell=new Label(5,n,"分解项目",format5); 
				sheet.mergeCells(5, n,5, n); 
				sheet.addCell(cell); 
				cell=new Label(6,n,"分解金额",format5); 
				sheet.mergeCells(6, n,6, n); 
				sheet.addCell(cell); 
				cell=new Label(7,n,"来款银行",format5); 
				sheet.mergeCells(7, n,7, n); 
				sheet.addCell(cell);
				
				sheet.setRowView(n,300,false); 
				n=n+1;
				
				double TOTAL_REAL_PRICE=0.0;
				
				int i = 0;
				for (Object object2 : finaTodayFinaIncome) {
					Map finaIncome=(Map)object2;
					cell=new Label(0,n,""); 
					sheet.addCell(cell);
					
					if(finaIncome.get("OPPOSING_DATE")!=null){
						SimpleDateFormat df2=new SimpleDateFormat("yyyy-MM-dd");
						Date start_date2=df2.parse(finaIncome.get("OPPOSING_DATE")+"");
						DateTime dateLabel2=new DateTime(1,n, start_date2,dateCellFormat_center);
						sheet.addCell(dateLabel2); 
					}else{
						cell=new Label(1,n,""); 
						sheet.addCell(cell);
					}
									
					i = i + 1;						
					
					cell=new Label(2,n,DataUtil.StringUtil(finaIncome.get("CUST_NAME")+""),format5);
					sheet.addCell(cell);
					cell=new Label(3,n,DataUtil.StringUtil(finaIncome.get("CUST_CODE")+""),format5);
					sheet.addCell(cell);
					cell=new Label(4,n,DataUtil.StringUtil(finaIncome.get("LEASE_CODE")+""),format5);
					sheet.addCell(cell);
					cell=new Label(5,n,DataUtil.StringUtil(finaIncome.get("FICB_ITEM")+""),format5);
					sheet.addCell(cell);				
					number=new Number(6,n,Double.parseDouble(finaIncome.get("REAL_PRICE")+""),ccellFormat_right);
					sheet.addCell(number);
					cell=new Label(7,n,DataUtil.StringUtil(finaIncome.get("BANK_NAME")+""),format5);
					sheet.addCell(cell);					
					
					TOTAL_REAL_PRICE=TOTAL_REAL_PRICE+Double.parseDouble(finaIncome.get("REAL_PRICE")+"");
										
					sheet.setRowView(n,300,false);  
					n=n+1;
				}
				 		
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				cell=new Label(1,n,"小计",format5);
				sheet.mergeCells(1, n, 5, n);
				sheet.addCell(cell);
				number=new Number(6,n,TOTAL_REAL_PRICE,ccellFormat_right);
				sheet.addCell(number);
				
				
				cell=new Label(7,n,""); 
				sheet.addCell(cell);
				cell=new Label(8,n,""); 
				sheet.addCell(cell);
				
				sheet.setRowView(n,300,false);  
				n=n+1;
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				cell=new Label(1,n,"暂存款销账",format4); 
				sheet.mergeCells(1, n, 7, n); 
				sheet.addCell(cell);
				n=n+1;
				sheet.setRowView(1,320,false); 
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				cell=new Label(1,n,"来款日期",format5); 
				sheet.mergeCells(1, n, 1, n); 
				sheet.addCell(cell); 
				cell=new Label(2,n,"承租人",format5); 
				sheet.mergeCells(2, n, 2, n); 
				sheet.addCell(cell); 
				cell=new Label(3,n,"客户编号",format5); 
				sheet.mergeCells(3, n,3, n); 
				sheet.addCell(cell); 
				cell=new Label(4,n,"合同号",format5); 
				sheet.mergeCells(4, n,4, n); 
				sheet.addCell(cell); 
				cell=new Label(5,n,"分解项目",format5); 
				sheet.mergeCells(5, n,5, n); 
				sheet.addCell(cell); 
				cell=new Label(6,n,"分解金额",format5); 
				sheet.mergeCells(6, n,6, n); 
				sheet.addCell(cell); 
				cell=new Label(7,n,"来款银行",format5); 
				sheet.mergeCells(7, n,7, n); 
				sheet.addCell(cell);
				
				sheet.setRowView(n,300,false); 
				n=n+1;
				
				double TOTAL_REAL_PRICE_LAST=0.0;		

				for (Object object2 : finaLastDecompose) {
					Map finaIncome=(Map)object2;
					cell=new Label(0,n,""); 
					sheet.addCell(cell);
					
					if(finaIncome.get("OPPOSING_DATE")!=null){
						SimpleDateFormat df2=new SimpleDateFormat("yyyy-MM-dd");
						Date start_date2=df2.parse(finaIncome.get("OPPOSING_DATE")+"");
						DateTime dateLabel2=new DateTime(1,n, start_date2,dateCellFormat_center);
						sheet.addCell(dateLabel2); 
					}else{
						cell=new Label(1,n,""); 
						sheet.addCell(cell);
					}				
					
					cell=new Label(2,n,DataUtil.StringUtil(finaIncome.get("CUST_NAME")+""),format5);
					sheet.addCell(cell);
					cell=new Label(3,n,DataUtil.StringUtil(finaIncome.get("CUST_CODE")+""),format5);
					sheet.addCell(cell);
					cell=new Label(4,n,DataUtil.StringUtil(finaIncome.get("LEASE_CODE")+""),format5);
					sheet.addCell(cell);
					cell=new Label(5,n,DataUtil.StringUtil(finaIncome.get("FICB_ITEM")+""),format5);
					sheet.addCell(cell);				
					number=new Number(6,n,Double.parseDouble(finaIncome.get("REAL_PRICE")+""),ccellFormat_right);
					sheet.addCell(number);
					cell=new Label(7,n,DataUtil.StringUtil(finaIncome.get("BANK_NAME")+""),format5);
					sheet.addCell(cell);					
					
					TOTAL_REAL_PRICE_LAST=TOTAL_REAL_PRICE_LAST+Double.parseDouble(finaIncome.get("REAL_PRICE")+"");
										
					sheet.setRowView(n,300,false);  
					n=n+1;
				}
				 		
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				cell=new Label(1,n,"小计",format5);
				sheet.mergeCells(1, n, 5, n);
				sheet.addCell(cell);
				number=new Number(6,n,TOTAL_REAL_PRICE_LAST,ccellFormat_right);
				sheet.addCell(number);
				
				cell=new Label(7,n,""); 
				sheet.addCell(cell);
				cell=new Label(8,n,""); 
				sheet.addCell(cell);
				
				sheet.setRowView(n,300,false);  
				n=n+1;	

				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				cell=new Label(1,n,"暂收款-余额变动表",format4); 
				sheet.mergeCells(1, n, 7, n); 
				sheet.addCell(cell);
				n=n+1;
				sheet.setRowView(1,320,false); 
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				cell=new Label(1,n,"来款日期",format5); 
				sheet.mergeCells(1, n, 1, n); 
				sheet.addCell(cell); 
				cell=new Label(2,n,"来款单位",format5); 
				sheet.mergeCells(2, n, 2, n); 
				sheet.addCell(cell); 
				cell=new Label(3,n,"期初余额",format5); 
				sheet.mergeCells(3, n,3, n); 
				sheet.addCell(cell); 
				cell=new Label(4,n,"本期新增",format5); 
				sheet.mergeCells(4, n,4, n); 
				sheet.addCell(cell); 
				cell=new Label(5,n,"本期减少",format5); 
				sheet.mergeCells(5, n,5, n); 
				sheet.addCell(cell); 
				cell=new Label(6,n,"期末余额",format5); 
				sheet.mergeCells(6, n,6, n); 
				sheet.addCell(cell); 
				cell=new Label(7,n,"来款银行",format5); 
				sheet.mergeCells(7, n,7, n); 
				sheet.addCell(cell);
				
				sheet.setRowView(n,300,false); 
				n=n+1;
				
				double TOTAL_QMYE=0.0;	
				double SUM_QMYE=0.0;
				double TOTAL_QCYE=0.0;
				double TOTAL_BQXZ=0.0;
				double TOTAL_BQJS=0.0;

				for (Object object2 : finaLastDynamicDecompose) {
					Map finaIncome=(Map)object2;
					cell=new Label(0,n,""); 
					sheet.addCell(cell);
					
					if(finaIncome.get("OPPOSING_DATE")!=null){
						SimpleDateFormat df2=new SimpleDateFormat("yyyy-MM-dd");
						Date start_date2=df2.parse(finaIncome.get("OPPOSING_DATE")+"");
						DateTime dateLabel2=new DateTime(1,n, start_date2,dateCellFormat_center);
						sheet.addCell(dateLabel2); 
					}else{
						cell=new Label(1,n,""); 
						sheet.addCell(cell);
					}				
					
					SUM_QMYE=Double.parseDouble(finaIncome.get("CURRENT_NEW")+"")+Double.parseDouble(finaIncome.get("INCOME_MONEY")+"")-Double.parseDouble(finaIncome.get("CURRENT_REDUCE")+"");
					
					cell=new Label(2,n,DataUtil.StringUtil(finaIncome.get("OPPOSING_UNIT")+""),format5);
					sheet.addCell(cell);
					number=new Number(3,n,Double.parseDouble(finaIncome.get("INCOME_MONEY")+""),ccellFormat_right);
					sheet.addCell(number);
					number=new Number(4,n,Double.parseDouble(finaIncome.get("CURRENT_NEW")+""),ccellFormat_right);
					sheet.addCell(number);
					number=new Number(5,n,Double.parseDouble(finaIncome.get("CURRENT_REDUCE")+""),ccellFormat_right);
					sheet.addCell(number);				
					number=new Number(6,n,SUM_QMYE,ccellFormat_right);
					sheet.addCell(number);
					cell=new Label(7,n,DataUtil.StringUtil(finaIncome.get("BANK_NAME")+""),format5);
					sheet.addCell(cell);					
					
					TOTAL_QMYE=TOTAL_QMYE+SUM_QMYE;
					TOTAL_QCYE=TOTAL_QCYE+Double.parseDouble(finaIncome.get("INCOME_MONEY")+"");
					TOTAL_BQXZ=TOTAL_BQXZ+Double.parseDouble(finaIncome.get("CURRENT_NEW")+"");
					TOTAL_BQJS=TOTAL_BQJS+Double.parseDouble(finaIncome.get("CURRENT_REDUCE")+"");					
					SUM_QMYE=SUM_QMYE+0.0;
										
					sheet.setRowView(n,300,false);  
					n=n+1;
				}
				 		
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				cell=new Label(1,n,"小计",format5);
				sheet.mergeCells(1, n, 2, n);
				sheet.addCell(cell);
				number=new Number(3,n,TOTAL_QCYE,ccellFormat_right);
				sheet.addCell(number);
				number=new Number(4,n,TOTAL_BQXZ,ccellFormat_right);
				sheet.addCell(number);
				number=new Number(5,n,TOTAL_BQJS,ccellFormat_right);
				sheet.addCell(number);
				number=new Number(6,n,TOTAL_QMYE,ccellFormat_right);
				sheet.addCell(number);				
				
				cell=new Label(7,n,""); 
				sheet.addCell(cell);
				cell=new Label(8,n,""); 
				sheet.addCell(cell);
				
				sheet.setRowView(n,300,false);  
				n=n+1;					
				
				cell=new Label(0,1,"",format2_RIGHT);
				sheet.mergeCells(0, 1, 0, n-1);
				sheet.addCell(cell);
				cell=new Label(8,1,"",format2_LEFT);
				sheet.mergeCells(8, 1, 7, n-1);
				sheet.addCell(cell);
				cell=new Label(1,n,"",format2_Top);
				sheet.mergeCells(1, n, 7, n); 
				sheet.addCell(cell);
						
			}catch(Exception e){
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
			return baos;
		}
	
}
