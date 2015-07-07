package com.brick.collection.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

import com.brick.service.core.AService;
import com.brick.util.Constants;
import com.brick.util.CurrencyConverter;
import com.brick.util.DataUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;

/**
 * 导出支付表excel
 * 
 * @author li shaojie
 * @date Oct 13, 2010
 * 
 */
public class ExportPaylistToExcel extends AService {
	Log logger = LogFactory.getLog(ExportPaylistToExcel.class);
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
	public ByteArrayOutputStream exportExcel(List paylists) {
		for (Object object : paylists) {
			Map paylist = (Map) object;
			WritableSheet sheet = null;
			try {
				/* 解决中文乱码 */
				workbookSettings.setEncoding("ISO-8859-1");
				sheet = wb.createSheet((String) paylist.get("RECP_CODE"), 1);

				WritableFont font2 = new WritableFont(WritableFont
						.createFont("宋体"), 10, WritableFont.BOLD);
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
				WritableFont font3 = new WritableFont(WritableFont
						.createFont("宋体"), 10, WritableFont.NO_BOLD);
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
				WritableFont font6 = new WritableFont(WritableFont
						.createFont("宋体"), 10, WritableFont.NO_BOLD);
				font6.setColour(Colour.RED);
				WritableCellFormat format6 = new WritableCellFormat(font6);
				format6.setAlignment(Alignment.LEFT); 
				format6.setVerticalAlignment(VerticalAlignment.CENTRE);
				// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
				format6.setWrap(true);
				
				WritableFont font4 = new WritableFont(WritableFont
						.createFont("宋体"), 12, WritableFont.NO_BOLD);
				WritableCellFormat format4 = new WritableCellFormat(font4);
				format4.setAlignment(Alignment.LEFT);
				format4.setBackground(jxl.format.Colour.GRAY_25);
				format4.setVerticalAlignment(VerticalAlignment.CENTRE);
				// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
				format4.setWrap(true);
				
				// 设置列宽
				sheet.setColumnView(0, 4);
				sheet.setColumnView(1, 15);
				sheet.setColumnView(2, 15);
				sheet.setColumnView(3, 15);
				sheet.setColumnView(4, 15);
				sheet.setColumnView(5, 15);
				sheet.setColumnView(6, 15);
				sheet.setColumnView(7, 15);
				sheet.setColumnView(8, 15);
				sheet.setColumnView(9, 15);
				sheet.setColumnView(10,15);
				sheet.setColumnView(11,15);
				sheet.setColumnView(12,7);
				sheet.setColumnView(13,8);
				sheet.setColumnView(14,4);
				
				int n=0;
				Label cell = null;
				NumberFormat cnum=new NumberFormat("#,##0.00");
				NumberFormat pnum=new NumberFormat("##0.00%");
				NumberFormat fnum=new NumberFormat("##0.00");
				NumberFormat nnum=new NumberFormat("##"); 
				DateFormat   dateFormat=new DateFormat("yyyy-mm-dd");
				WritableCellFormat ccellFormat_right=new WritableCellFormat(cnum);
				WritableCellFormat pcellFormat_left=new WritableCellFormat(pnum);
				WritableCellFormat pcellFormat_center=new WritableCellFormat(pnum);
				WritableCellFormat fcellFormat_left=new WritableCellFormat(fnum);
				WritableCellFormat ccellFormat_left=new WritableCellFormat(cnum);
				WritableCellFormat dateCellFormat_left=new WritableCellFormat(dateFormat);
				WritableCellFormat dateCellFormat_center=new WritableCellFormat(dateFormat);
				ccellFormat_left.setAlignment(Alignment.LEFT);
				pcellFormat_left.setAlignment(Alignment.LEFT);
				pcellFormat_center.setAlignment(Alignment.CENTRE);
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
				cell=new Label(0,0,"");  
				sheet.addCell(cell);
				cell=new Label(1,0,"",format2_BOTTOM);  
				sheet.mergeCells(1, 0, 13, 0);
				sheet.addCell(cell);
				n=n+1;
				sheet.setRowView(0,300,false); 
				
				cell=new Label(0,1,""); 
				sheet.addCell(cell);
				cell=new Label(1,1,"基本信息",format4); 
				sheet.mergeCells(1, 1, 13, 1); 
				sheet.addCell(cell);
				n=n+1;
				sheet.setRowView(1,320,false); 
				
				cell=new Label(0,2,""); 
				sheet.addCell(cell);
				cell=new Label(1,2,"合同号",format3);
				sheet.mergeCells(1, 2, 3, 2); 
				sheet.addCell(cell); 
				cell=new Label(4,2,(String) paylist.get("LEASE_CODE"),format3);
				sheet.mergeCells(4, 2, 6, 2); 
				sheet.addCell(cell);
				cell=new Label(7,2,"承租人",format3);
				sheet.mergeCells(7, 2, 8, 2); 
				sheet.addCell(cell);
				cell=new Label(9,2,(String) paylist.get("CUST_NAME"),format3);
				sheet.mergeCells(9, 2, 13, 2); 	
				sheet.addCell(cell);
				n=n+1;
				sheet.setRowView(2,300,false); 
				
				cell=new Label(0,3,""); 
				sheet.addCell(cell);
				cell=new Label(1,3,"支付表号",format3);
				sheet.mergeCells(1, 3, 3, 3); 
				sheet.addCell(cell); 
				cell=new Label(4,3,(String) paylist.get("RECP_CODE"),format3);
				sheet.mergeCells(4, 3, 6, 3); 
				sheet.addCell(cell);
				cell=new Label(7,3,"合同总金额",format3);
				sheet.mergeCells(7, 3, 8, 3); 
				sheet.addCell(cell); 
				number=new Number(9,3,(Double)paylist.get("CONTRACT_PRICE"),ccellFormat_right);
				sheet.mergeCells(9, 3, 13, 3); 	
				sheet.addCell(number);
				n=n+1;
				sheet.setRowView(3,300,false); 
				
				cell=new Label(0,4,""); 
				sheet.addCell(cell);
				cell=new Label(1,4,"设备列表",format4); 
				sheet.mergeCells(1, 4, 13, 4); 
				sheet.addCell(cell);
				n=n+1;
				sheet.setRowView(4,320,false); 
				
				cell=new Label(0,5,""); 
				sheet.addCell(cell);
				cell=new Label(1,5,"序号",format5); 
				sheet.addCell(cell); 
				cell=new Label(2,5,"设备类型",format5); 
				sheet.mergeCells(2, 5, 3, 5); 
				sheet.addCell(cell); 
				cell=new Label(4,5,"设备名称",format5); 
				sheet.mergeCells(4, 5, 5, 5); 
				sheet.addCell(cell); 
				cell=new Label(6,5,"设备型号",format5);  
				sheet.addCell(cell); 
				cell=new Label(7,5,"机号",format5);  
				sheet.addCell(cell); 
				cell=new Label(8,5,"生产商",format5); 
				sheet.mergeCells(8, 5, 9, 5); 
				sheet.addCell(cell); 
				
				cell=new Label(10,5,"锁码方式",format5);  
				sheet.addCell(cell); 
				
				cell=new Label(11,5,"单价",format5);  
				sheet.addCell(cell); 
				cell=new Label(12,5,"数量",format5);  
				sheet.addCell(cell); 
				cell=new Label(13,5,"单位",format5);  
				sheet.addCell(cell); 
				n=n+1;
				sheet.setRowView(5,320,false);  
				
				double equipmentTotal=0; 
				List payEquipment=(List)paylist.get("equipments");
				for (int i=0;i<payEquipment.size();i++) {
					Map equipment=(Map)payEquipment.get(i); 
					
					String lock_code = "";
					String code = equipment.get("LOCK_CODE").toString();
					if("1".equals(code) ){
						lock_code = "间接";
					}else if("2".equals(code) ){
						lock_code = "异常";
					}else if("3".equals(code)){
						lock_code = "直接";
					}else if("4".equals(code)){
						lock_code = "无";
					}
					
					cell=new Label(0,n,""); 
					sheet.addCell(cell);
					number=new Number(1,n,(i+1),format5); 
					sheet.addCell(number); 
					cell=new Label(2,n,equipment.get("THING_KIND")+"",format5); 
					sheet.mergeCells(2, n, 3, n); 
					sheet.addCell(cell); 
					cell=new Label(4,n,equipment.get("THING_NAME")+"",format5); 
					sheet.mergeCells(4, n, 5, n); 
					sheet.addCell(cell); 
					cell=new Label(6,n,equipment.get("MODEL_SPEC")+"",format5);  
					sheet.addCell(cell); 
					cell=new Label(7,n,equipment.get("THING_NUMBER")+"",format5);  
					sheet.addCell(cell); 
					cell=new Label(8,n,equipment.get("BRAND")+"",format5); 
					sheet.mergeCells(8, n, 9, n); 
					sheet.addCell(cell); 
					
					cell=new Label(10,n,lock_code,format5); 
					sheet.addCell(cell);
					
					number=new Number(11,n,(Double)equipment.get("UNIT_PRICE"),ccellFormat_right);
					sheet.addCell(number); 
					number=new Number(12,n,Double.parseDouble(""+equipment.get("AMOUNT")),format5); 
					sheet.addCell(number);  
					cell=new Label(13,n,equipment.get("UNIT")+"",format5);  
					sheet.addCell(cell); 
					sheet.setRowView(n,640,false);  
					n=n+1;
					//1.0E8 －－100000000
					double machineUnit=0;
					String   b;	
					machineUnit=Double.valueOf(nf2.format(equipment.get("UNIT_PRICE"))); 
					equipmentTotal=equipmentTotal+machineUnit*Double.parseDouble(""+equipment.get("AMOUNT"));
				}
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"合计(大写)",format3); 
				sheet.addCell(cell); 
				cell=new Label(2,n,CurrencyConverter.toUpper(nf2.format(equipmentTotal)),format3); 
				sheet.mergeCells(2, n, 8, n); 
				sheet.addCell(cell);
				cell=new Label(9,n,"合计(小写)",format3); 
				sheet.addCell(cell);
				number=new Number(10,n,equipmentTotal,ccellFormat_right);
				sheet.mergeCells(10, n, 13, n);
				sheet.addCell(number);
				sheet.setRowView(n,300,false); 
				n=n+1;
				 
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				cell=new Label(1,n,"融资租赁方案",format4); 
				sheet.mergeCells(1, n, 13, n); 
				sheet.addCell(cell);
				sheet.setRowView(n,320,false);  
				n=n+1;
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"租赁期数",format3);
				sheet.mergeCells(1, n, 3, n); 
				sheet.addCell(cell); 
				number=new Number(4,n,Double.parseDouble(paylist.get("LEASE_PERIOD")+""),ncellFormat_left); 
				sheet.mergeCells(4, n, 6, n); 
				sheet.addCell(number);
				cell=new Label(7,n,"租赁周期",format3);
				sheet.mergeCells(7, n, 8, n); 
				sheet.addCell(cell);
				String LEASE_TERM_STR="";
				if((paylist.get("LEASE_TERM")+"").equals("1")){
					LEASE_TERM_STR="月份";
				}else if((paylist.get("LEASE_TERM")+"").equals("3")){
					LEASE_TERM_STR="季度";
				}else if((paylist.get("LEASE_TERM")+"").equals("6")){
					LEASE_TERM_STR="半年";
				}else if((paylist.get("LEASE_TERM")+"").equals("12")){
					LEASE_TERM_STR="年度";
				}
				cell=new Label(9,n,LEASE_TERM_STR,format3);
				sheet.mergeCells(9, n, 13, n); 	
				sheet.addCell(cell);
				sheet.setRowView(n,300,false); 
				n=n+1;
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"保证金",format3);
				sheet.mergeCells(1, n, 3, n); 
				sheet.addCell(cell); 
				number=new Number(4,n,Double.parseDouble(paylist.get("PLEDGE_PRICE")+""),ccellFormat_left); 
				sheet.mergeCells(4, n, 6, n); 
				sheet.addCell(number);
				cell=new Label(7,n,"保证金处理方式",format3);
				sheet.mergeCells(7, n, 8, n); 
				sheet.addCell(cell); 
				String PLEDGE_WAY_STR="";
				if((paylist.get("PLEDGE_WAY")+"").equals("1")){
					PLEDGE_WAY_STR="用于平均抵冲金额";
				}else if((paylist.get("PLEDGE_WAY")+"").equals("0")){
					PLEDGE_WAY_STR="用于期末退还金额";
				}else if((paylist.get("PLEDGE_WAY")+"").equals("2")){
					PLEDGE_WAY_STR="用于最后抵冲含税金额/期数";
				} 
				cell=new Label(9,n,PLEDGE_WAY_STR,format3);
				sheet.mergeCells(9, n, 13, n); 	
				sheet.addCell(cell);
				sheet.setRowView(n,300,false); 
				n=n+1;
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"保证金入账",format3);
				sheet.mergeCells(1, n, 3, n); 
				sheet.addCell(cell); 
				//number=new Number(4,n,Double.parseDouble(paylist.get("PLEDGE_REALPRIC")+""),ccellFormat_left); 
				cell=new Label(4,n,"入我司 "+Double.parseDouble(paylist.get("PLEDGE_ENTER_CMPRICE")==null?"0":paylist.get("PLEDGE_ENTER_CMPRICE")+" ")
										   +"税金 "+Double.parseDouble(paylist.get("PLEDGE_ENTER_CMRATE")==null?"0":paylist.get("PLEDGE_ENTER_CMRATE")
										   +" ")+" 我司入供应商 "+Double.parseDouble(paylist.get("PLEDGE_ENTER_MCTOAG")==null?"0":paylist.get("PLEDGE_ENTER_MCTOAG")
												   +" ")+"\n入供应商 "+Double.parseDouble(paylist.get("PLEDGE_ENTER_AG")==null?"0":paylist.get("PLEDGE_ENTER_AG")+"")+"税金 "+Double.parseDouble(paylist.get("PLEDGE_ENTER_AGRATE")==null?"0":paylist.get("PLEDGE_ENTER_AGRATE")+""),format3);
				sheet.mergeCells(4, n, 6, n); 
				sheet.addCell(cell);
				cell=new Label(7,n,"",format3);
				sheet.mergeCells(7, n, 8, n); 
				sheet.addCell(cell);  
				cell=new Label(9,n,"",format3);
				sheet.mergeCells(9, n, 13, n); 	
				sheet.addCell(cell);
				sheet.setRowView(n,500,false); 
				n=n+1;
//Modify by Michael 取消管理费增加  管理费收入				
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"首期租金",format3);
				sheet.mergeCells(1, n, 3, n); 
				sheet.addCell(cell); 
				number=new Number(4,n,Double.parseDouble(paylist.get("HEAD_HIRE")+""),ccellFormat_left); 
				sheet.mergeCells(4, n, 6, n); 
				sheet.addCell(number);
				cell=new Label(7,n,"",format3);
				sheet.mergeCells(7, n, 8, n); 
				sheet.addCell(cell);  
				cell=new Label(9,n,"",format3);
				sheet.mergeCells(9, n, 13, n); 	
				sheet.addCell(cell);
				sheet.setRowView(n,300,false); 
				n=n+1;

				List feeListRZE=(List)paylist.get("feeListRZE");
				for (int i=0;i<feeListRZE.size();i++) {
					Map mapFeeListRZE=(Map)feeListRZE.get(i); 
					
					cell=new Label(0,n,""); 
					sheet.addCell(cell);
					number=new Number(1,n,(i+1),format5); 
					sheet.addCell(number); 
					cell=new Label(2,n,mapFeeListRZE.get("CREATE_SHOW_NAME")+"",format5); 
					sheet.mergeCells(2, n, 3, n); 
					sheet.addCell(cell); 
					cell=new Label(4,n,mapFeeListRZE.get("FEE")+"",format5); 
					sheet.mergeCells(4, n, 5, n); 
					sheet.addCell(cell); 
					sheet.setRowView(n,300,false);  
					n=n+1;
				}
				
				List feeList=(List)paylist.get("feeList");
				for (int i=0;i<feeList.size();i++) {
					Map mapFeeList=(Map)feeList.get(i); 
					
					cell=new Label(0,n,""); 
					sheet.addCell(cell);
					number=new Number(1,n,(i+1),format5); 
					sheet.addCell(number); 
					cell=new Label(2,n,mapFeeList.get("CREATE_SHOW_NAME")+"",format5); 
					sheet.mergeCells(2, n, 3, n); 
					sheet.addCell(cell); 
					cell=new Label(4,n,mapFeeList.get("FEE")+"",format5); 
					sheet.mergeCells(4, n, 5, n); 
					sheet.addCell(cell); 
					sheet.setRowView(n,300,false);  
					n=n+1;
				}			
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"",format3);
				sheet.mergeCells(1, n, 3, n); 
				sheet.addCell(cell); 
				number=new Number(4,n,0,pcellFormat_left); 
				sheet.mergeCells(4, n, 6, n); 
				sheet.addCell(number);
				cell=new Label(7,n,"合同利率",format3);
				sheet.mergeCells(7, n, 8, n); 
				sheet.addCell(cell);  
				number=new Number(9,n,Double.parseDouble(paylist.get("YEAR_INTEREST")+""),fcellFormat_left); 
				sheet.addCell(number);
				String YEAR_INTEREST_TYPE_STR="";
				if((paylist.get("YEAR_INTEREST_TYPE")+"").equals("1")){
					YEAR_INTEREST_TYPE_STR="(浮动)";
				}else if((paylist.get("YEAR_INTEREST_TYPE")+"").equals("1")){
					YEAR_INTEREST_TYPE_STR="(固定)";
				}
				cell=new Label(10,n,YEAR_INTEREST_TYPE_STR,format6);
				sheet.mergeCells(10, n, 13, n); 
				sheet.addCell(cell);  
				sheet.setRowView(n,300,false); 
				n=n+1;
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"客户TR",format6);
				sheet.mergeCells(1, n, 3, n); 
				sheet.addCell(cell); 
				number=new Number(4,n,Double.parseDouble(paylist.get("TR_RATE")+"")/100,pcellFormat_left); 
				sheet.mergeCells(4, n, 6, n); 
				sheet.addCell(number);
				cell=new Label(7,n,"实际TR",format6);
				sheet.mergeCells(7, n, 8, n); 
				sheet.addCell(cell);  
				number=new Number(9,n,Double.parseDouble(paylist.get("TR_IRR_RATE")+"")/100,pcellFormat_left); 
				sheet.mergeCells(9, n, 13, n); 
				sheet.addCell(number);   
				sheet.setRowView(n,300,false); 
				n=n+1;
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"营业税率",format3);
				sheet.mergeCells(1, n, 3, n); 
				sheet.addCell(cell); 
				number=new Number(4,n,Double.parseDouble(paylist.get("SALES_TAX_RATE")+"")/100,pcellFormat_left); 
				sheet.mergeCells(4, n, 6, n); 
				sheet.addCell(number);
				cell=new Label(7,n,"保险费率",format3);
				sheet.mergeCells(7, n, 8, n); 
				sheet.addCell(cell);  
				number=new Number(9,n,Double.parseDouble(paylist.get("INSURE_BASE_RATE")+"")/100,pcellFormat_left); 
				sheet.mergeCells(9, n, 13, n); 
				sheet.addCell(number);   
				sheet.setRowView(n,300,false); 
				n=n+1;
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"日罚息率",format3);
				sheet.mergeCells(1, n, 3, n); 
				sheet.addCell(cell); 
				number=new Number(4,n,Double.parseDouble(paylist.get("FINE_RATE")+""),fcellFormat_left); 
				sheet.addCell(number);
				String FINE_TYPE_STR="";
				if((paylist.get("FINE_TYPE")+"").equals("1")){
					FINE_TYPE_STR="(单利)";
				}else if((paylist.get("FINE_TYPE")+"").equals("2")){
					FINE_TYPE_STR="(复利)";
				}
				cell=new Label(5,n,FINE_TYPE_STR,format6);
				sheet.mergeCells(5, n, 6, n);
				sheet.addCell(cell); 
				cell=new Label(7,n,"概算成本(RZE)",format3);
				sheet.mergeCells(7, n, 8, n); 
				sheet.addCell(cell);  
				number=new Number(9,n,Double.parseDouble(paylist.get("LEASE_RZE")+""),ccellFormat_left); 
				sheet.mergeCells(9, n, 13, n); 
				sheet.addCell(number);   
				sheet.setRowView(n,300,false); 
				n=n+1;
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"支付方式",format3);
				sheet.mergeCells(1, n, 3, n); 
				sheet.addCell(cell); 
				String fs="";
				if((paylist.get("PAY_WAY")).equals("12")){
					fs="期初等额本息支付";
				}
				if((paylist.get("PAY_WAY")).equals("12")){
					fs="期初等额本金支付";
				}
				if((paylist.get("PAY_WAY")).equals("13")){
					fs="期初不等额支付";
				}
				if((paylist.get("PAY_WAY")).equals("21")){
					fs="期末等额本息支付";
				}
				if((paylist.get("PAY_WAY")).equals("22")){
					fs="期末等额本金支付";
				}
				if((paylist.get("PAY_WAY")).equals("23")){
					fs="期末不等额支付";
				}				
				cell=new Label(4,n,fs,format3); 
				sheet.mergeCells(4, n, 6, n); 
				sheet.addCell(cell);
				cell=new Label(7,n,"起租日期",format3);
				sheet.mergeCells(7, n, 8, n); 
				sheet.addCell(cell);  
				SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
				Date first_date=df.parse(paylist.get("FIRST_PAYDATE")+"");//修改起租日期取得的字段
//				Date start_date=df.parse(paylist.get("START_DATE")+"");
				DateTime dateLabel=new DateTime(9,n, first_date,dateCellFormat_left);
				sheet.mergeCells(9, n, 13, n); 
				sheet.addCell(dateLabel);   
				sheet.setRowView(n,300,false); 
				n=n+1;
				//wuzd 添加利差 02-22--------
				double RATE_DIFF=0;
				
				//-- Modify by Michael 2012 02/02导出excel时不重新计算利差,直接取值 ---------------------------------
//				List payliness=(List)paylist.get("paylines");
//				//利息和-成本和
////				for (Object object2 : payliness) {
////					Map payline=(Map)object2;
////					RATE_DIFF=RATE_DIFF+DataUtil.doubleUtil(payline.get("REN_PRICE"))+DataUtil.intUtil(payline.get("COST_PRICE"));
////				}
//				//现值PV
//				for (Object object2 : payliness) {
//					Map payline=(Map)object2;
//					RATE_DIFF=RATE_DIFF+DataUtil.doubleUtil(payline.get("PV_PRICE"));
//					
//				}				
				//-- Add by Michael 2012 02/02导出excel时不重新计算利差,直接取值 ---------------------------------
				RATE_DIFF=DataUtil.doubleUtil(paylist.get("RATE_DIFF"));
// --------------------------------------------------------------------------------------------------------
				
				//System.out.println(RATE_DIFF);
				//wuzd 添加利差 02-22--------
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				
				cell=new Label(1,n,"差旅费",format3);
				sheet.mergeCells(1, n, 3, n); 
				sheet.addCell(cell); 
				number=new Number(4,n,Double.parseDouble(paylist.get("BUSINESS_TRIP_PRICE")+""),ccellFormat_left); 
				sheet.mergeCells(4, n, 6, n); 
				sheet.addCell(number);  
				
				
				
				//添加首期支付日期
				cell=new Label(7,n,"首期支付日期 ",format3);
				sheet.mergeCells(7, n, 8, n); 
				sheet.addCell(cell); 
				Date start_date=df.parse(paylist.get("START_DATE")+"");
				dateLabel=new DateTime(9,n, start_date,dateCellFormat_left);
				sheet.mergeCells(9, n, 13, n); 
				sheet.addCell(dateLabel);
				//添加首期支付日期     结束
				
				
				sheet.setRowView(n,300,false); 
				n=n+1;
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"租赁物件设置场所",format3);
				sheet.mergeCells(1, n, 3, n); 
				sheet.addCell(cell); 
				cell=new Label(4,n,paylist.get("EQUPMENT_ADDRESS")+"",format3);
				sheet.mergeCells(4, n, 6, n); 
				sheet.addCell(cell);  
				
				cell=new Label(7,n,"利差",format6);
				sheet.mergeCells(7, n, 8, n); 
				sheet.addCell(cell); 
				number=new Number(9,n,Double.parseDouble(String.valueOf(RATE_DIFF)),ccellFormat_left); 
				sheet.mergeCells(9, n, 13, n); 
				sheet.addCell(number);  
				
				sheet.setRowView(n,300,false); 
				n=n+1;
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"供应商保证",format3);
				sheet.mergeCells(1, n, 3, n); 
				sheet.addCell(cell); 
				cell=new Label(4,n,paylist.get("SUPL_TRUE")==null?"":paylist.get("SUPL_TRUE")+"",format3);
				sheet.mergeCells(4, n, 6, n); 
				sheet.addCell(cell);  
				
				cell=new Label(7,n,"",format6);
				sheet.mergeCells(7, n, 8, n); 
				sheet.addCell(cell); 
				cell=new Label(9,n,"",format3);
				sheet.mergeCells(9, n, 13, n); 
				sheet.addCell(cell);  
				
				sheet.setRowView(n,300,false); 
				n=n+1;
				
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"租赁期满处理方式",format3);
				sheet.mergeCells(1, n, 3, n); 
				sheet.addCell(cell); 
				cell=new Label(4,n,paylist.get("DEAL_WAY")+"",format3);
				sheet.mergeCells(4, n, 6, n); 
				sheet.addCell(cell);  
				cell=new Label(7,n,"",format3);
				sheet.mergeCells(7, n, 8, n); 
				sheet.addCell(cell); 
				String BUY_INSURANCE_WAY="";
				cell=new Label(9,n,BUY_INSURANCE_WAY,format3);
				sheet.mergeCells(9, n, 13, n); 
				sheet.addCell(cell);  
				sheet.setRowView(n,300,false); 
				n=n+1;
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"保险公司",format3);
				sheet.mergeCells(1, n, 3, n); 
				sheet.addCell(cell); 
				cell=new Label(4,n,paylist.get("INSURANCE_COMPANY_ID")+"",format3);
				sheet.mergeCells(4, n, 6, n); 
				sheet.addCell(cell);  
				cell=new Label(7,n,"",format3);
				sheet.mergeCells(7, n, 8, n); 
				sheet.addCell(cell); 
				String BUY_INSURANCE_TIME="";
				cell=new Label(9,n,BUY_INSURANCE_TIME,format3);
				sheet.mergeCells(9, n, 13, n); 
				sheet.addCell(cell);  
				sheet.setRowView(n,300,false); 
				n=n+1;
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				cell=new Label(1,n,"保险费",format4); 
				sheet.mergeCells(1, n, 13, n); 
				sheet.addCell(cell);
				sheet.setRowView(n,320,false);  
				n=n+1;
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"保险项目",format5);
				sheet.mergeCells(1, n, 3, n); 
				sheet.addCell(cell);  
				cell=new Label(4,n,"保险期间",format5);
				sheet.mergeCells(4, n, 5, n); 
				sheet.addCell(cell);
				cell=new Label(6,n,"保险费率 ",format5); 
				sheet.addCell(cell);  
				cell=new Label(7,n,"保险费用",format5);
				sheet.mergeCells(7, n, 8, n); 
				sheet.addCell(cell);
				cell=new Label(9,n,"备注",format3);
				sheet.mergeCells(9, n, 13, n); 
				sheet.addCell(cell);
				sheet.setRowView(n,300,false); 
				n=n+1;
				
				List insures=(List)paylist.get("insures");
				double insure_price=0;
				for (Object object2 : insures) {
					Map insure=(Map)object2;
					cell=new Label(0,n,""); 
					sheet.addCell(cell); 
					cell=new Label(1,n,insure.get("INTP_NAME")+"",format5);
					sheet.mergeCells(1, n, 3, n); 
					sheet.addCell(cell);  
					cell=new Label(4,n,insure.get("START_DATE")+"到"+insure.get("END_DATE"),format5);
					sheet.mergeCells(4, n, 5, n); 
					sheet.addCell(cell);
					number=new Number(6,n,Double.parseDouble(insure.get("INSURE_RATE")+"")/100,pcellFormat_center);
					sheet.addCell(number);  
					number=new Number(7,n,Double.parseDouble(insure.get("INSURE_PRICE")+""),ccellFormat_right);
					sheet.mergeCells(7, n, 8, n); 
					sheet.addCell(number);
					cell=new Label(9,n,insure.get("MEMO")+"",format3);
					sheet.mergeCells(9, n, 13, n); 
					sheet.addCell(cell);
					sheet.setRowView(n,300,false); 
					n=n+1;
					insure_price=insure_price+Double.parseDouble(insure.get("INSURE_PRICE")+"");
				}
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"合计(大写)",format3); 
				sheet.addCell(cell); 
				cell=new Label(2,n,CurrencyConverter.toUpper(insure_price+""),format3); 
				sheet.mergeCells(2, n, 8, n); 
				sheet.addCell(cell);
				cell=new Label(9,n,"合计(小写)",format3); 
				sheet.addCell(cell);
				number=new Number(10,n,insure_price,ccellFormat_right);
				sheet.mergeCells(10, n, 13, n);
				sheet.addCell(number);
				sheet.setRowView(n,300,false); 
				n=n+1;
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				cell=new Label(1,n,"保险费",format4); 
				sheet.mergeCells(1, n, 13, n); 
				sheet.addCell(cell);
				sheet.setRowView(n,320,false);  
				n=n+1;
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"费用名称",format5);
				sheet.mergeCells(1, n, 3, n); 
				sheet.addCell(cell);  
				cell=new Label(4,n,"费用金额",format5);
				sheet.mergeCells(4, n, 6, n); 
				sheet.addCell(cell);  
				cell=new Label(7,n,"产生费用时间",format5);
				sheet.mergeCells(7, n, 8, n); 
				sheet.addCell(cell);
				cell=new Label(9,n,"备注",format3);
				sheet.mergeCells(9, n, 13, n); 
				sheet.addCell(cell);
				sheet.setRowView(n,300,false); 
				n=n+1;
				
				List otherfees=(List)paylist.get("otherfees");
				double otherfee_price=0;
				for (Object object2 : otherfees) {
					Map otherfee=(Map)object2;
					cell=new Label(0,n,""); 
					sheet.addCell(cell); 
					cell=new Label(1,n,otherfee.get("OTHER_NAME")+"",format5);
					sheet.mergeCells(1, n, 3, n); 
					sheet.addCell(cell);  
					number=new Number(4,n,Double.parseDouble(otherfee.get("OTHER_PRICE")+""),ccellFormat_right);
					sheet.mergeCells(4, n, 6, n); 
					sheet.addCell(number); 
					cell=new Label(7,n,otherfee.get("OTHER_DATE")+"",format5);
					sheet.mergeCells(7, n, 8, n); 
					sheet.addCell(cell);  
					cell=new Label(9,n,otherfee.get("MEMO")+"",format3);
					sheet.mergeCells(9, n, 13, n); 
					sheet.addCell(cell);
					sheet.setRowView(n,300,false); 
					n=n+1;
					otherfee_price=otherfee_price+Double.parseDouble(otherfee.get("OTHER_PRICE")+"");
				}
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"合计(大写)",format3); 
				sheet.addCell(cell); 
				cell=new Label(2,n,CurrencyConverter.toUpper(otherfee_price+""),format3); 
				sheet.mergeCells(2, n, 8, n); 
				sheet.addCell(cell);
				cell=new Label(9,n,"合计(小写)",format3); 
				sheet.addCell(cell);
				number=new Number(10,n,otherfee_price,ccellFormat_right);
				sheet.mergeCells(10, n, 13, n);
				sheet.addCell(number);
				sheet.setRowView(n,300,false); 
				n=n+1;
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				cell=new Label(1,n,"融资租赁租金方案",format4); 
				sheet.mergeCells(1, n, 13, n); 
				sheet.addCell(cell);
				sheet.setRowView(n,320,false);  
				n=n+1;
				
				List irrMonthPaylines=(List)paylist.get("irrMonthPaylines");
				for (Object object3 : irrMonthPaylines) {
					Map irrMonthPayline=(Map)object3;
					cell=new Label(0,n,""); 
					sheet.addCell(cell);
					cell=new Label(1,n,"应收租金",format3);
					sheet.mergeCells(1, n, 3, n); 
					sheet.addCell(cell);
					number=new Number(4,n,Double.parseDouble(irrMonthPayline.get("IRR_MONTH_PRICE")+""),ccellFormat_left);
					sheet.mergeCells(4, n, 6, n);
					sheet.addCell(number);
					cell=new Label(7,n,"对应期次",format3);
					sheet.mergeCells(7, n, 8, n); 
					sheet.addCell(cell);
					cell=new Label(9,n,"第"+irrMonthPayline.get("IRR_MONTH_PRICE_START")+"期到第"+irrMonthPayline.get("IRR_MONTH_PRICE_END")+"期",format3);
					sheet.mergeCells(9, n, 13, n); 
					sheet.addCell(cell);
					sheet.setRowView(n,300,false);  
					n=n+1;
				}
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				cell=new Label(1,n,"融资租赁还款计划",format4); 
				sheet.mergeCells(1, n, 12, n); 
				sheet.addCell(cell);
				sheet.setRowView(n,320,false);  
				n=n+1;
				
				cell=new Label(0,n,"");
				sheet.addCell(cell);
				cell=new Label(1,n,"期次",format5); 
				sheet.addCell(cell);
				cell=new Label(2,n,"支付时间",format5); 
				sheet.addCell(cell);
				cell=new Label(3,n,"应收租金",format5); 
				sheet.addCell(cell);
				cell=new Label(4,n,"预期租金",format5); 
				sheet.addCell(cell);
				cell=new Label(5,n,"本金",format5); 
				sheet.addCell(cell);
				if(Constants.TAX_PLAN_CODE_4.equals(((Map)object).get("TAX_PLAN_CODE").toString())
						||Constants.TAX_PLAN_CODE_6.equals(((Map)object).get("TAX_PLAN_CODE").toString())
						||Constants.TAX_PLAN_CODE_7.equals(((Map)object).get("TAX_PLAN_CODE").toString())
						||Constants.TAX_PLAN_CODE_8.equals(((Map)object).get("TAX_PLAN_CODE").toString())) {
					cell=new Label(6,n,"未税本金",format5); 
					sheet.addCell(cell);
					cell=new Label(7,n,"税金",format5); 
					sheet.addCell(cell);
					cell=new Label(8,n,"利息",format5); 
					sheet.addCell(cell);
					cell=new Label(9,n,"未税利息",format5); 
					sheet.addCell(cell);
					cell=new Label(10,n,"税金",format5); 
					sheet.addCell(cell);
					cell=new Label(11,n,"剩余本金",format5); 
					sheet.addCell(cell);
					/*cell=new Label(12,n,"收入",format3); 
					sheet.addCell(cell);*/
				} else {
					cell=new Label(6,n,"利息",format5); 
					sheet.addCell(cell);
					if("3".equals(((Map)object).get("TAX_PLAN_CODE").toString())) {
						cell=new Label(7,n,"未税利息",format5); 
						sheet.addCell(cell);
						cell=new Label(8,n,"税金",format5); 
						sheet.addCell(cell);
						cell=new Label(9,n,"剩余本金",format5); 
						sheet.addCell(cell);
						cell=new Label(10,n,"增值税",format5); 
						sheet.addCell(cell);
						/*cell=new Label(11,n,"收入",format3); 
						sheet.mergeCells(11, n, 12, n); 
						sheet.addCell(cell);*/
					} else {
						cell=new Label(7,n,"剩余本金",format5); 
						sheet.addCell(cell);
						cell=new Label(8,n,"营业税",format5); 
						sheet.addCell(cell);
						cell=new Label(9,n,"收入",format5); 
						sheet.mergeCells(9, n, 10, n); 
						sheet.addCell(cell);
					}
				}
				sheet.setRowView(n,300,false);  
				n=n+1;
				
				List paylines=(List)paylist.get("paylines");
				double IRR_MONTH_PRICE_sum=0;
				double IRR_PRICE_sum=0;
				double MONTH_PRICE_sum=0;
				double OWN_PRICE_sum=0;
				double REN_PRICE_sum=0;
				double SALES_TAX_sum=0;
				double REN_PRICE_SUM=0;
				double TAX_SUM=0;
				double OWN_PRICE_SUM=0;
				double TAX_SUM1=0;
				for (Object object2 : paylines) {
					Map payline=(Map)object2;
					cell=new Label(0,n,""); 
					sheet.addCell(cell);
					number=new Number(1,n,Double.parseDouble(payline.get("PERIOD_NUM")+""),ncellFormat_center);
					sheet.addCell(number);
					DateTime dt=new DateTime(2,n,(Date)payline.get("PAY_DATE"),dateCellFormat_center);
					sheet.addCell(dt);
					number=new Number(3,n,Double.parseDouble(payline.get("IRR_MONTH_PRICE")+""),ccellFormat_right);
					sheet.addCell(number);
					number=new Number(4,n,Double.parseDouble(payline.get("MONTH_PRICE")+""),ccellFormat_right);
					sheet.addCell(number);
					number=new Number(5,n,Double.parseDouble(payline.get("OWN_PRICE")+""),ccellFormat_right);
					sheet.addCell(number);
					if(Constants.TAX_PLAN_CODE_4.equals(((Map)object).get("TAX_PLAN_CODE").toString())
							||Constants.TAX_PLAN_CODE_6.equals(((Map)object).get("TAX_PLAN_CODE").toString())
							||Constants.TAX_PLAN_CODE_7.equals(((Map)object).get("TAX_PLAN_CODE").toString())
							||Constants.TAX_PLAN_CODE_8.equals(((Map)object).get("TAX_PLAN_CODE").toString())) {
						DecimalFormat df1=new DecimalFormat("####.00");
						number=new Number(6,n,Double.valueOf(df1.format(Double.parseDouble(payline.get("OWN_PRICE")+"")/1.17)),ccellFormat_right);
						sheet.addCell(number);
						number=new Number(7,n,Double.valueOf(df1.format(Double.parseDouble(payline.get("OWN_PRICE")+"")/1.17*0.17)),ccellFormat_right);
						sheet.addCell(number);
						number=new Number(8,n,Double.parseDouble(payline.get("REN_PRICE")+""),ccellFormat_right);
						sheet.addCell(number);
						number=new Number(9,n,Double.valueOf(df1.format(Double.parseDouble(payline.get("REN_PRICE")+"")/1.17)),ccellFormat_right);
						sheet.addCell(number);
						number=new Number(10,n,Double.valueOf(df1.format(Double.parseDouble(payline.get("REN_PRICE")+"")/1.17*0.17)),ccellFormat_right);
						sheet.addCell(number);
						number=new Number(11,n,Double.parseDouble(payline.get("LAST_PRICE")+""),ccellFormat_right);
						sheet.addCell(number);
						/*number=new Number(12,n,Double.parseDouble(payline.get("REN_PRICE")+"")-Double.parseDouble(payline.get("SALES_TAX")==null?"0":payline.get("SALES_TAX")+""),ccellFormat_right);
						sheet.addCell(number);*/
					} else {
						number=new Number(6,n,Double.parseDouble(payline.get("REN_PRICE")+""),ccellFormat_right);
						sheet.addCell(number);
						if("3".equals(((Map)object).get("TAX_PLAN_CODE").toString())) {
							DecimalFormat df1=new DecimalFormat("####.00");
							number=new Number(7,n,Double.valueOf(df1.format(Double.parseDouble(payline.get("REN_PRICE")+"")/1.17)),ccellFormat_right);
							sheet.addCell(number);
							number=new Number(8,n,Double.valueOf(df1.format(Double.parseDouble(payline.get("REN_PRICE")+"")/1.17*0.17)),ccellFormat_right);
							sheet.addCell(number);
							number=new Number(9,n,Double.parseDouble(payline.get("LAST_PRICE")+""),ccellFormat_right);
							sheet.addCell(number);
							number=new Number(10,n,Double.parseDouble(payline.get("SALES_TAX")==null?"0":payline.get("SALES_TAX")+""),ccellFormat_right);
							sheet.addCell(number);
							/*number=new Number(11,n,Double.parseDouble(payline.get("REN_PRICE")+"")-Double.parseDouble(payline.get("SALES_TAX")==null?"0":payline.get("SALES_TAX")+""),ccellFormat_right);
							sheet.mergeCells(11, n, 12, n); 
							sheet.addCell(number);*/
						} else {
							number=new Number(7,n,Double.parseDouble(payline.get("LAST_PRICE")+""),ccellFormat_right);
							sheet.addCell(number);
							number=new Number(8,n,Double.parseDouble(payline.get("SALES_TAX")==null?"0":payline.get("SALES_TAX")+""),ccellFormat_right);
							sheet.addCell(number);
							number=new Number(9,n,Double.parseDouble(payline.get("REN_PRICE")+"")-Double.parseDouble(payline.get("SALES_TAX")==null?"0":payline.get("SALES_TAX")+""),ccellFormat_right);
							sheet.mergeCells(9, n, 10, n); 
							sheet.addCell(number); 
						}
					}
					sheet.setRowView(n,300,false);  
					n=n+1;
					
					IRR_MONTH_PRICE_sum=IRR_MONTH_PRICE_sum+Double.parseDouble(payline.get("IRR_MONTH_PRICE")+"");
					IRR_PRICE_sum=IRR_PRICE_sum+Double.parseDouble(payline.get("IRR_PRICE")+"");
					MONTH_PRICE_sum=MONTH_PRICE_sum+Double.parseDouble(payline.get("MONTH_PRICE")+"");
					OWN_PRICE_sum=OWN_PRICE_sum+Double.parseDouble(payline.get("OWN_PRICE")+"");
					REN_PRICE_sum=REN_PRICE_sum+Double.parseDouble(payline.get("REN_PRICE")+"");
					DecimalFormat df1=new DecimalFormat("#.00");
					REN_PRICE_SUM=REN_PRICE_SUM+Double.valueOf(df1.format(Double.parseDouble(payline.get("REN_PRICE")+"")/1.17));
					TAX_SUM=TAX_SUM+Double.valueOf(df1.format(Double.parseDouble(payline.get("REN_PRICE")+"")/1.17*0.17));
					OWN_PRICE_SUM=OWN_PRICE_SUM+Double.valueOf(df1.format(Double.parseDouble(payline.get("OWN_PRICE")+"")/1.17));
					TAX_SUM1=TAX_SUM1+Double.valueOf(df1.format(Double.parseDouble(payline.get("OWN_PRICE")+"")/1.17*0.17));
					SALES_TAX_sum=SALES_TAX_sum+Double.parseDouble(payline.get("SALES_TAX")==null?"0":payline.get("SALES_TAX")+"");
				}
				 
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				cell=new Label(1,n,"合计",format3);
				sheet.mergeCells(1, n, 2, n);
				sheet.addCell(cell);
				number=new Number(3,n,IRR_MONTH_PRICE_sum,ccellFormat_right);
				sheet.addCell(number);
				/*number=new Number(6,n,IRR_PRICE_sum,ccellFormat_right);
				sheet.addCell(number);*/
				number=new Number(4,n,MONTH_PRICE_sum,ccellFormat_right);
				sheet.addCell(number);
				number=new Number(5,n,OWN_PRICE_sum,ccellFormat_right);
				sheet.addCell(number);
				if(Constants.TAX_PLAN_CODE_4.equals(((Map)object).get("TAX_PLAN_CODE").toString())
						||Constants.TAX_PLAN_CODE_6.equals(((Map)object).get("TAX_PLAN_CODE").toString())
						||Constants.TAX_PLAN_CODE_7.equals(((Map)object).get("TAX_PLAN_CODE").toString())
						||Constants.TAX_PLAN_CODE_8.equals(((Map)object).get("TAX_PLAN_CODE").toString())) {
					number=new Number(6,n,OWN_PRICE_SUM,ccellFormat_right);
					sheet.addCell(number);
					number=new Number(7,n,TAX_SUM1,ccellFormat_right);
					sheet.addCell(number);
					number=new Number(8,n,REN_PRICE_sum,ccellFormat_right);
					sheet.addCell(number);
					number=new Number(9,n,REN_PRICE_SUM,ccellFormat_right);
					sheet.addCell(number);
					number=new Number(10,n,TAX_SUM,ccellFormat_right);
					sheet.addCell(number);
					/*number=new Number(11,n,REN_PRICE_sum-SALES_TAX_sum,ccellFormat_right);
					sheet.addCell(number);*/ 
				} else {
					number=new Number(6,n,REN_PRICE_sum,ccellFormat_right);
					sheet.addCell(number);
					if("3".equals(((Map)object).get("TAX_PLAN_CODE").toString())) {
						number=new Number(7,n,REN_PRICE_SUM,ccellFormat_right);
						sheet.addCell(number);
						number=new Number(8,n,TAX_SUM,ccellFormat_right);
						sheet.addCell(number);
						
						number=new Number(10,n,SALES_TAX_sum,ccellFormat_right);
						sheet.addCell(number);
						/*number=new Number(11,n,REN_PRICE_sum-SALES_TAX_sum,ccellFormat_right);
						sheet.mergeCells(11, n, 12, n);
						sheet.addCell(number); */
					} else {
						number=new Number(8,n,SALES_TAX_sum,ccellFormat_right);
						sheet.addCell(number);
						number=new Number(9,n,REN_PRICE_sum-SALES_TAX_sum,ccellFormat_right);
						sheet.mergeCells(9, n, 10, n);
						sheet.addCell(number); 
					}
				}
				sheet.setRowView(n,300,false);  
				n=n+1;
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				cell=new Label(1,n,"备注",format4); 
				sheet.mergeCells(1, n, 12, n); 
				sheet.addCell(cell);
				sheet.setRowView(n,320,false);  
				n=n+1;

				java.text.NumberFormat nf=java.text.NumberFormat.getCurrencyInstance();
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				cell=new Label(1,n,"承租人应在融资租赁合同签署后五个工作日内，" 
						+"将首期款电汇至出租人指定的账户。\n" 
						+"首期款包括：租赁质押金、管理费、首期租金、差旅费等， 以上共计:"
						+nf.format(Double.parseDouble(paylist.get("PLEDGE_PRICE")+"")
						//+Double.parseDouble(paylist.get("MANAGEMENT_FEE")+"")
						+Double.parseDouble(paylist.get("BUSINESS_TRIP_PRICE")+"")
						+Double.parseDouble(paylist.get("HEAD_HIRE")+""))
						+"元",format3); 
				sheet.mergeCells(1, n, 13, n); 
				sheet.addCell(cell);
				sheet.setRowView(n,650,false);  
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
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}
		return baos;
	}

	//Add by Michael 2012 09-27 For 增值税
	public ByteArrayOutputStream exportExcelForValueAdded(List paylists) {
		for (Object object : paylists) {
			Map paylist = (Map) object;
			WritableSheet sheet = null;
			try {
				/* 解决中文乱码 */
				workbookSettings.setEncoding("ISO-8859-1");
				sheet = wb.createSheet((String) paylist.get("RECP_CODE"), 1);

				WritableFont font2 = new WritableFont(WritableFont
						.createFont("宋体"), 10, WritableFont.BOLD);
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
				WritableFont font3 = new WritableFont(WritableFont
						.createFont("宋体"), 10, WritableFont.NO_BOLD);
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
				WritableFont font6 = new WritableFont(WritableFont
						.createFont("宋体"), 10, WritableFont.NO_BOLD);
				font6.setColour(Colour.RED);
				WritableCellFormat format6 = new WritableCellFormat(font6);
				format6.setAlignment(Alignment.LEFT); 
				format6.setVerticalAlignment(VerticalAlignment.CENTRE);
				// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
				format6.setWrap(true);
				
				WritableFont font4 = new WritableFont(WritableFont
						.createFont("宋体"), 12, WritableFont.NO_BOLD);
				WritableCellFormat format4 = new WritableCellFormat(font4);
				format4.setAlignment(Alignment.LEFT);
				format4.setBackground(jxl.format.Colour.GRAY_25);
				format4.setVerticalAlignment(VerticalAlignment.CENTRE);
				// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
				format4.setWrap(true);
				
				// 设置列宽
				sheet.setColumnView(0, 4);
				sheet.setColumnView(1, 15);
				sheet.setColumnView(2, 8);
				sheet.setColumnView(3, 15);
				sheet.setColumnView(4, 15);
				sheet.setColumnView(5, 15);
				sheet.setColumnView(6, 15);
				sheet.setColumnView(7, 15);
				sheet.setColumnView(8, 15);
				sheet.setColumnView(9, 15);
				sheet.setColumnView(10,15);
				sheet.setColumnView(11,15);
				sheet.setColumnView(12,7);
				sheet.setColumnView(13,8);
				sheet.setColumnView(14,4);
				
				int n=0;
				Label cell = null;
				NumberFormat cnum=new NumberFormat("#,##0.00");
				NumberFormat pnum=new NumberFormat("##0.00%");
				NumberFormat fnum=new NumberFormat("##0.00");
				NumberFormat nnum=new NumberFormat("##"); 
				DateFormat   dateFormat=new DateFormat("yyyy-mm-dd");
				WritableCellFormat ccellFormat_right=new WritableCellFormat(cnum);
				WritableCellFormat pcellFormat_left=new WritableCellFormat(pnum);
				WritableCellFormat pcellFormat_center=new WritableCellFormat(pnum);
				WritableCellFormat fcellFormat_left=new WritableCellFormat(fnum);
				WritableCellFormat ccellFormat_left=new WritableCellFormat(cnum);
				WritableCellFormat dateCellFormat_left=new WritableCellFormat(dateFormat);
				WritableCellFormat dateCellFormat_center=new WritableCellFormat(dateFormat);
				ccellFormat_left.setAlignment(Alignment.LEFT);
				pcellFormat_left.setAlignment(Alignment.LEFT);
				pcellFormat_center.setAlignment(Alignment.CENTRE);
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
				cell=new Label(0,0,"");  
				sheet.addCell(cell);
				cell=new Label(1,0,"",format2_BOTTOM);  
				sheet.mergeCells(1, 0, 13, 0);
				sheet.addCell(cell);
				n=n+1;
				sheet.setRowView(0,300,false); 
				
				cell=new Label(0,1,""); 
				sheet.addCell(cell);
				cell=new Label(1,1,"基本信息",format4); 
				sheet.mergeCells(1, 1, 13, 1); 
				sheet.addCell(cell);
				n=n+1;
				sheet.setRowView(1,320,false); 
				
				cell=new Label(0,2,""); 
				sheet.addCell(cell);
				cell=new Label(1,2,"合同号",format3);
				sheet.mergeCells(1, 2, 3, 2); 
				sheet.addCell(cell); 
				cell=new Label(4,2,(String) paylist.get("LEASE_CODE"),format3);
				sheet.mergeCells(4, 2, 6, 2); 
				sheet.addCell(cell);
				cell=new Label(7,2,"承租人",format3);
				sheet.mergeCells(7, 2, 8, 2); 
				sheet.addCell(cell);
				cell=new Label(9,2,(String) paylist.get("CUST_NAME"),format3);
				sheet.mergeCells(9, 2, 13, 2); 	
				sheet.addCell(cell);
				n=n+1;
				sheet.setRowView(2,300,false); 
				
				cell=new Label(0,3,""); 
				sheet.addCell(cell);
				cell=new Label(1,3,"支付表号",format3);
				sheet.mergeCells(1, 3, 3, 3); 
				sheet.addCell(cell); 
				cell=new Label(4,3,(String) paylist.get("RECP_CODE"),format3);
				sheet.mergeCells(4, 3, 6, 3); 
				sheet.addCell(cell);
				cell=new Label(7,3,"合同总金额",format3);
				sheet.mergeCells(7, 3, 8, 3); 
				sheet.addCell(cell); 
				number=new Number(9,3,(Double)paylist.get("CONTRACT_PRICE"),ccellFormat_right);
				sheet.mergeCells(9, 3, 13, 3); 	
				sheet.addCell(number);
				n=n+1;
				sheet.setRowView(3,300,false); 
				
				cell=new Label(0,4,""); 
				sheet.addCell(cell);
				cell=new Label(1,4,"设备列表",format4); 
				sheet.mergeCells(1, 4, 13, 4); 
				sheet.addCell(cell);
				n=n+1;
				sheet.setRowView(4,320,false); 
				
				cell=new Label(0,5,""); 
				sheet.addCell(cell);
				cell=new Label(1,5,"序号",format5); 
				sheet.addCell(cell); 
				cell=new Label(2,5,"设备类型",format5); 
				sheet.mergeCells(2, 5, 3, 5); 
				sheet.addCell(cell); 
				cell=new Label(4,5,"设备名称",format5); 
				sheet.mergeCells(4, 5, 5, 5); 
				sheet.addCell(cell); 
				cell=new Label(6,5,"设备型号",format5);  
				sheet.addCell(cell); 
				cell=new Label(7,5,"机号",format5);  
				sheet.addCell(cell); 
				cell=new Label(8,5,"生产商",format5); 
				sheet.mergeCells(8, 5, 9, 5); 
				sheet.addCell(cell); 
				
				cell=new Label(10,5,"锁码方式",format5);  
				sheet.addCell(cell); 
				
				cell=new Label(11,5,"单价",format5);  
				sheet.addCell(cell); 
				cell=new Label(12,5,"数量",format5);  
				sheet.addCell(cell); 
				cell=new Label(13,5,"单位",format5);  
				sheet.addCell(cell); 
				n=n+1;
				sheet.setRowView(5,320,false);  
				
				double equipmentTotal=0; 
				List payEquipment=(List)paylist.get("equipments");
				for (int i=0;i<payEquipment.size();i++) {
					Map equipment=(Map)payEquipment.get(i); 
					
					String lock_code = "";
					String code = equipment.get("LOCK_CODE").toString();
					if("1".equals(code) ){
						lock_code = "间接";
					}else if("2".equals(code) ){
						lock_code = "异常";
					}else if("3".equals(code)){
						lock_code = "直接";
					}else if("4".equals(code)){
						lock_code = "无";
					}
					
					cell=new Label(0,n,""); 
					sheet.addCell(cell);
					number=new Number(1,n,(i+1),format5); 
					sheet.addCell(number); 
					cell=new Label(2,n,equipment.get("THING_KIND")+"",format5); 
					sheet.mergeCells(2, n, 3, n); 
					sheet.addCell(cell); 
					cell=new Label(4,n,equipment.get("THING_NAME")+"",format5); 
					sheet.mergeCells(4, n, 5, n); 
					sheet.addCell(cell); 
					cell=new Label(6,n,equipment.get("MODEL_SPEC")+"",format5);  
					sheet.addCell(cell); 
					cell=new Label(7,n,equipment.get("THING_NUMBER")+"",format5);  
					sheet.addCell(cell); 
					cell=new Label(8,n,equipment.get("BRAND")+"",format5); 
					sheet.mergeCells(8, n, 9, n); 
					sheet.addCell(cell); 
					
					cell=new Label(10,n,lock_code,format5); 
					sheet.addCell(cell);
					
					number=new Number(11,n,(Double)equipment.get("UNIT_PRICE"),ccellFormat_right);
					sheet.addCell(number); 
					number=new Number(12,n,Double.parseDouble(""+equipment.get("AMOUNT")),format5); 
					sheet.addCell(number);  
					cell=new Label(13,n,equipment.get("UNIT")+"",format5);  
					sheet.addCell(cell); 
					sheet.setRowView(n,640,false);  
					n=n+1;
					//1.0E8 －－100000000
					double machineUnit=0;
					String   b;	
					machineUnit=Double.valueOf(nf2.format(equipment.get("UNIT_PRICE"))); 
					equipmentTotal=equipmentTotal+machineUnit*Double.parseDouble(""+equipment.get("AMOUNT"));
				}
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"合计(大写)",format3); 
				sheet.addCell(cell); 
				cell=new Label(2,n,CurrencyConverter.toUpper(nf2.format(equipmentTotal)),format3); 
				sheet.mergeCells(2, n, 8, n); 
				sheet.addCell(cell);
				cell=new Label(9,n,"合计(小写)",format3); 
				sheet.addCell(cell);
				number=new Number(10,n,equipmentTotal,ccellFormat_right);
				sheet.mergeCells(10, n, 13, n);
				sheet.addCell(number);
				sheet.setRowView(n,300,false); 
				n=n+1;
				 
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				cell=new Label(1,n,"融资租赁方案",format4); 
				sheet.mergeCells(1, n, 13, n); 
				sheet.addCell(cell);
				sheet.setRowView(n,320,false);  
				n=n+1;
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"租赁期数",format3);
				sheet.mergeCells(1, n, 3, n); 
				sheet.addCell(cell); 
				number=new Number(4,n,Double.parseDouble(paylist.get("LEASE_PERIOD")+""),ncellFormat_left); 
				sheet.mergeCells(4, n, 6, n); 
				sheet.addCell(number);
				cell=new Label(7,n,"租赁周期",format3);
				sheet.mergeCells(7, n, 8, n); 
				sheet.addCell(cell);
				String LEASE_TERM_STR="";
				if((paylist.get("LEASE_TERM")+"").equals("1")){
					LEASE_TERM_STR="月份";
				}else if((paylist.get("LEASE_TERM")+"").equals("3")){
					LEASE_TERM_STR="季度";
				}else if((paylist.get("LEASE_TERM")+"").equals("6")){
					LEASE_TERM_STR="半年";
				}else if((paylist.get("LEASE_TERM")+"").equals("12")){
					LEASE_TERM_STR="年度";
				}
				cell=new Label(9,n,LEASE_TERM_STR,format3);
				sheet.mergeCells(9, n, 13, n); 	
				sheet.addCell(cell);
				sheet.setRowView(n,300,false); 
				n=n+1;
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"保证金",format3);
				sheet.mergeCells(1, n, 3, n); 
				sheet.addCell(cell); 
				number=new Number(4,n,Double.parseDouble(paylist.get("PLEDGE_PRICE")+""),ccellFormat_left); 
				sheet.mergeCells(4, n, 6, n); 
				sheet.addCell(number);
				cell=new Label(7,n,"保证金处理方式",format3);
				sheet.mergeCells(7, n, 8, n); 
				sheet.addCell(cell); 
				String PLEDGE_WAY_STR="";
				if((paylist.get("PLEDGE_WAY")+"").equals("1")){
					PLEDGE_WAY_STR="用于平均抵冲金额";
				}else if((paylist.get("PLEDGE_WAY")+"").equals("0")){
					PLEDGE_WAY_STR="用于期末退还金额";
				}else if((paylist.get("PLEDGE_WAY")+"").equals("2")){
					PLEDGE_WAY_STR="用于最后抵冲含税金额/期数";
				} 
				cell=new Label(9,n,PLEDGE_WAY_STR,format3);
				sheet.mergeCells(9, n, 13, n); 	
				sheet.addCell(cell);
				sheet.setRowView(n,300,false); 
				n=n+1;
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"保证金入账",format3);
				sheet.mergeCells(1, n, 3, n); 
				sheet.addCell(cell); 
				//number=new Number(4,n,Double.parseDouble(paylist.get("PLEDGE_REALPRIC")+""),ccellFormat_left); 
				cell=new Label(4,n,"入我司 "+Double.parseDouble(paylist.get("PLEDGE_ENTER_CMPRICE")==null?"0":paylist.get("PLEDGE_ENTER_CMPRICE")+" ")
										   +"税金 "+Double.parseDouble(paylist.get("PLEDGE_ENTER_CMRATE")==null?"0":paylist.get("PLEDGE_ENTER_CMRATE")
										   +" ")+" 我司入供应商 "+Double.parseDouble(paylist.get("PLEDGE_ENTER_MCTOAG")==null?"0":paylist.get("PLEDGE_ENTER_MCTOAG")
												   +" ")+"\n入供应商 "+Double.parseDouble(paylist.get("PLEDGE_ENTER_AG")==null?"0":paylist.get("PLEDGE_ENTER_AG")+"")+"税金 "+Double.parseDouble(paylist.get("PLEDGE_ENTER_AGRATE")==null?"0":paylist.get("PLEDGE_ENTER_AGRATE")+""),format3);
				sheet.mergeCells(4, n, 6, n); 
				sheet.addCell(cell);
				cell=new Label(7,n,"",format3);
				sheet.mergeCells(7, n, 8, n); 
				sheet.addCell(cell);  
				cell=new Label(9,n,"",format3);
				sheet.mergeCells(9, n, 13, n); 	
				sheet.addCell(cell);
				sheet.setRowView(n,500,false); 
				n=n+1;
//Modify by Michael 取消管理费增加  管理费收入				
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"首期租金",format3);
				sheet.mergeCells(1, n, 3, n); 
				sheet.addCell(cell); 
				number=new Number(4,n,Double.parseDouble(paylist.get("HEAD_HIRE")+""),ccellFormat_left); 
				sheet.mergeCells(4, n, 6, n); 
				sheet.addCell(number);
				cell=new Label(7,n,"",format3);
				sheet.mergeCells(7, n, 8, n); 
				sheet.addCell(cell);  
				cell=new Label(9,n,"",format3);
				sheet.mergeCells(9, n, 13, n); 	
				sheet.addCell(cell);
				sheet.setRowView(n,300,false); 
				n=n+1;

				List feeListRZE=(List)paylist.get("feeListRZE");
				for (int i=0;i<feeListRZE.size();i++) {
					Map mapFeeListRZE=(Map)feeListRZE.get(i); 
					
					cell=new Label(0,n,""); 
					sheet.addCell(cell);
					number=new Number(1,n,(i+1),format5); 
					sheet.addCell(number); 
					cell=new Label(2,n,mapFeeListRZE.get("CREATE_SHOW_NAME")+"",format5); 
					sheet.mergeCells(2, n, 3, n); 
					sheet.addCell(cell); 
					cell=new Label(4,n,mapFeeListRZE.get("FEE")+"",format5); 
					sheet.mergeCells(4, n, 5, n); 
					sheet.addCell(cell); 
					sheet.setRowView(n,300,false);  
					n=n+1;
				}
				
				List feeList=(List)paylist.get("feeList");
				for (int i=0;i<feeList.size();i++) {
					Map mapFeeList=(Map)feeList.get(i); 
					
					cell=new Label(0,n,""); 
					sheet.addCell(cell);
					number=new Number(1,n,(i+1),format5); 
					sheet.addCell(number); 
					cell=new Label(2,n,mapFeeList.get("CREATE_SHOW_NAME")+"",format5); 
					sheet.mergeCells(2, n, 3, n); 
					sheet.addCell(cell); 
					cell=new Label(4,n,mapFeeList.get("FEE")+"",format5); 
					sheet.mergeCells(4, n, 5, n); 
					sheet.addCell(cell); 
					sheet.setRowView(n,300,false);  
					n=n+1;
				}			
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"",format3);
				sheet.mergeCells(1, n, 3, n); 
				sheet.addCell(cell); 
				number=new Number(4,n,0,pcellFormat_left); 
				sheet.mergeCells(4, n, 6, n); 
				sheet.addCell(number);
				cell=new Label(7,n,"合同利率",format3);
				sheet.mergeCells(7, n, 8, n); 
				sheet.addCell(cell);  
				number=new Number(9,n,Double.parseDouble(paylist.get("YEAR_INTEREST")+""),fcellFormat_left); 
				sheet.addCell(number);
				String YEAR_INTEREST_TYPE_STR="";
				if((paylist.get("YEAR_INTEREST_TYPE")+"").equals("1")){
					YEAR_INTEREST_TYPE_STR="(浮动)";
				}else if((paylist.get("YEAR_INTEREST_TYPE")+"").equals("1")){
					YEAR_INTEREST_TYPE_STR="(固定)";
				}
				cell=new Label(10,n,YEAR_INTEREST_TYPE_STR,format6);
				sheet.mergeCells(10, n, 13, n); 
				sheet.addCell(cell);  
				sheet.setRowView(n,300,false); 
				n=n+1;
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"客户TR",format6);
				sheet.mergeCells(1, n, 3, n); 
				sheet.addCell(cell); 
				number=new Number(4,n,Double.parseDouble(paylist.get("TR_RATE")+"")/100,pcellFormat_left); 
				sheet.mergeCells(4, n, 6, n); 
				sheet.addCell(number);
				cell=new Label(7,n,"实际TR",format6);
				sheet.mergeCells(7, n, 8, n); 
				sheet.addCell(cell);  
				number=new Number(9,n,Double.parseDouble(paylist.get("TR_IRR_RATE")+"")/100,pcellFormat_left); 
				sheet.mergeCells(9, n, 13, n); 
				sheet.addCell(number);   
				sheet.setRowView(n,300,false); 
				n=n+1;
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"增值税税率",format3);
				sheet.mergeCells(1, n, 3, n); 
				sheet.addCell(cell); 
				number=new Number(4,n,Double.parseDouble(paylist.get("SALES_TAX_RATE")+"")/100,pcellFormat_left); 
				sheet.mergeCells(4, n, 6, n); 
				sheet.addCell(number);
				cell=new Label(7,n,"保险费率",format3);
				sheet.mergeCells(7, n, 8, n); 
				sheet.addCell(cell);  
				number=new Number(9,n,Double.parseDouble(paylist.get("INSURE_BASE_RATE")+"")/100,pcellFormat_left); 
				sheet.mergeCells(9, n, 13, n); 
				sheet.addCell(number);   
				sheet.setRowView(n,300,false); 
				n=n+1;
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"日罚息率",format3);
				sheet.mergeCells(1, n, 3, n); 
				sheet.addCell(cell); 
				number=new Number(4,n,Double.parseDouble(paylist.get("FINE_RATE")+""),fcellFormat_left); 
				sheet.addCell(number);
				String FINE_TYPE_STR="";
				if((paylist.get("FINE_TYPE")+"").equals("1")){
					FINE_TYPE_STR="(单利)";
				}else if((paylist.get("FINE_TYPE")+"").equals("2")){
					FINE_TYPE_STR="(复利)";
				}
				cell=new Label(5,n,FINE_TYPE_STR,format6);
				sheet.mergeCells(5, n, 6, n);
				sheet.addCell(cell); 
				cell=new Label(7,n,"概算成本(RZE)",format3);
				sheet.mergeCells(7, n, 8, n); 
				sheet.addCell(cell);  
				number=new Number(9,n,Double.parseDouble(paylist.get("LEASE_RZE")+""),ccellFormat_left); 
				sheet.mergeCells(9, n, 13, n); 
				sheet.addCell(number);   
				sheet.setRowView(n,300,false); 
				n=n+1;
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"支付方式",format3);
				sheet.mergeCells(1, n, 3, n); 
				sheet.addCell(cell); 
				String fs="";
				if((paylist.get("PAY_WAY")).equals("12")){
					fs="期初等额本息支付";
				}
				if((paylist.get("PAY_WAY")).equals("12")){
					fs="期初等额本金支付";
				}
				if((paylist.get("PAY_WAY")).equals("13")){
					fs="期初不等额支付";
				}
				if((paylist.get("PAY_WAY")).equals("21")){
					fs="期末等额本息支付";
				}
				if((paylist.get("PAY_WAY")).equals("22")){
					fs="期末等额本金支付";
				}
				if((paylist.get("PAY_WAY")).equals("23")){
					fs="期末不等额支付";
				}				
				cell=new Label(4,n,fs,format3); 
				sheet.mergeCells(4, n, 6, n); 
				sheet.addCell(cell);
				cell=new Label(7,n,"起租日期",format3);
				sheet.mergeCells(7, n, 8, n); 
				sheet.addCell(cell);  
				SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
				Date first_date=df.parse(paylist.get("FIRST_PAYDATE")+"");//修改起租日期取得的字段
//				Date start_date=df.parse(paylist.get("START_DATE")+"");
				DateTime dateLabel=new DateTime(9,n, first_date,dateCellFormat_left);
				sheet.mergeCells(9, n, 13, n); 
				sheet.addCell(dateLabel);   
				sheet.setRowView(n,300,false); 
				n=n+1;
				//wuzd 添加利差 02-22--------
				double RATE_DIFF=0;
				RATE_DIFF=DataUtil.doubleUtil(paylist.get("RATE_DIFF"));
// --------------------------------------------------------------------------------------------------------
				
				//System.out.println(RATE_DIFF);
				//wuzd 添加利差 02-22--------
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				
				cell=new Label(1,n,"差旅费",format3);
				sheet.mergeCells(1, n, 3, n); 
				sheet.addCell(cell); 
				number=new Number(4,n,Double.parseDouble(paylist.get("BUSINESS_TRIP_PRICE")+""),ccellFormat_left); 
				sheet.mergeCells(4, n, 6, n); 
				sheet.addCell(number);  
				
				//添加首期支付日期
				cell=new Label(7,n,"首期支付日期 ",format3);
				sheet.mergeCells(7, n, 8, n); 
				sheet.addCell(cell); 
				Date start_date=df.parse(paylist.get("START_DATE")+"");
				dateLabel=new DateTime(9,n, start_date,dateCellFormat_left);
				sheet.mergeCells(9, n, 13, n); 
				sheet.addCell(dateLabel);
				//添加首期支付日期     结束
				
				sheet.setRowView(n,300,false); 
				n=n+1;
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"租赁物件设置场所",format3);
				sheet.mergeCells(1, n, 3, n); 
				sheet.addCell(cell); 
				cell=new Label(4,n,paylist.get("EQUPMENT_ADDRESS")+"",format3);
				sheet.mergeCells(4, n, 6, n); 
				sheet.addCell(cell);  
				
				cell=new Label(7,n,"利差",format6);
				sheet.mergeCells(7, n, 8, n); 
				sheet.addCell(cell); 
				number=new Number(9,n,Double.parseDouble(String.valueOf(RATE_DIFF)),ccellFormat_left); 
				sheet.mergeCells(9, n, 13, n); 
				sheet.addCell(number);  
				
				sheet.setRowView(n,300,false); 
				n=n+1;
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"供应商保证",format3);
				sheet.mergeCells(1, n, 3, n); 
				sheet.addCell(cell); 
				cell=new Label(4,n,paylist.get("SUPL_TRUE")==null?"":paylist.get("SUPL_TRUE")+"",format3);
				sheet.mergeCells(4, n, 6, n); 
				sheet.addCell(cell);  
				
				cell=new Label(7,n,"",format6);
				sheet.mergeCells(7, n, 8, n); 
				sheet.addCell(cell); 
				cell=new Label(9,n,"",format3);
				sheet.mergeCells(9, n, 13, n); 
				sheet.addCell(cell);  
				
				sheet.setRowView(n,300,false); 
				n=n+1;
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"租赁期满处理方式",format3);
				sheet.mergeCells(1, n, 3, n); 
				sheet.addCell(cell); 
				cell=new Label(4,n,paylist.get("DEAL_WAY")+"",format3);
				sheet.mergeCells(4, n, 6, n); 
				sheet.addCell(cell);  
				//cell=new Label(7,n,"购买方式",format3);
				cell=new Label(7,n,"",format3);
				sheet.mergeCells(7, n, 8, n); 
				sheet.addCell(cell); 
				String BUY_INSURANCE_WAY="";
				cell=new Label(9,n,BUY_INSURANCE_WAY,format3);
				sheet.mergeCells(9, n, 13, n); 
				sheet.addCell(cell);  
				sheet.setRowView(n,300,false); 
				n=n+1;
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"保险公司",format3);
				sheet.mergeCells(1, n, 3, n); 
				sheet.addCell(cell); 
				cell=new Label(4,n,paylist.get("INSURANCE_COMPANY_ID")+"",format3);
				sheet.mergeCells(4, n, 6, n); 
				sheet.addCell(cell);  
				cell=new Label(7,n,"",format3);
				sheet.mergeCells(7, n, 8, n); 
				sheet.addCell(cell); 
				String BUY_INSURANCE_TIME="";
	
				cell=new Label(9,n,BUY_INSURANCE_TIME,format3);
				sheet.mergeCells(9, n, 13, n); 
				sheet.addCell(cell);  
				sheet.setRowView(n,300,false); 
				n=n+1;

				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				cell=new Label(1,n,"保险费",format4); 
				sheet.mergeCells(1, n, 13, n); 
				sheet.addCell(cell);
				sheet.setRowView(n,320,false);  
				n=n+1;
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"保险项目",format5);
				sheet.mergeCells(1, n, 3, n); 
				sheet.addCell(cell);  
				cell=new Label(4,n,"保险期间",format5);
				sheet.mergeCells(4, n, 5, n); 
				sheet.addCell(cell);
				cell=new Label(6,n,"保险费率 ",format5); 
				sheet.addCell(cell);  
				cell=new Label(7,n,"保险费用",format5);
				sheet.mergeCells(7, n, 8, n); 
				sheet.addCell(cell);
				cell=new Label(9,n,"备注",format3);
				sheet.mergeCells(9, n, 13, n); 
				sheet.addCell(cell);
				sheet.setRowView(n,300,false); 
				n=n+1;
				
				List insures=(List)paylist.get("insures");
				double insure_price=0;
				for (Object object2 : insures) {
					Map insure=(Map)object2;
					cell=new Label(0,n,""); 
					sheet.addCell(cell); 
					cell=new Label(1,n,insure.get("INTP_NAME")+"",format5);
					sheet.mergeCells(1, n, 3, n); 
					sheet.addCell(cell);  
					cell=new Label(4,n,insure.get("START_DATE")+"到"+insure.get("END_DATE"),format5);
					sheet.mergeCells(4, n, 5, n); 
					sheet.addCell(cell);
					number=new Number(6,n,Double.parseDouble(insure.get("INSURE_RATE")+"")/100,pcellFormat_center);
					sheet.addCell(number);  
					number=new Number(7,n,Double.parseDouble(insure.get("INSURE_PRICE")+""),ccellFormat_right);
					sheet.mergeCells(7, n, 8, n); 
					sheet.addCell(number);
					cell=new Label(9,n,insure.get("MEMO")+"",format3);
					sheet.mergeCells(9, n, 13, n); 
					sheet.addCell(cell);
					sheet.setRowView(n,300,false); 
					n=n+1;
					insure_price=insure_price+Double.parseDouble(insure.get("INSURE_PRICE")+"");
				}
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"合计(大写)",format3); 
				sheet.addCell(cell); 
				cell=new Label(2,n,CurrencyConverter.toUpper(insure_price+""),format3); 
				sheet.mergeCells(2, n, 8, n); 
				sheet.addCell(cell);
				cell=new Label(9,n,"合计(小写)",format3); 
				sheet.addCell(cell);
				number=new Number(10,n,insure_price,ccellFormat_right);
				sheet.mergeCells(10, n, 13, n);
				sheet.addCell(number);
				sheet.setRowView(n,300,false); 
				n=n+1;
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				cell=new Label(1,n,"保险费",format4); 
				sheet.mergeCells(1, n, 13, n); 
				sheet.addCell(cell);
				sheet.setRowView(n,320,false);  
				n=n+1;
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"费用名称",format5);
				sheet.mergeCells(1, n, 3, n); 
				sheet.addCell(cell);  
				cell=new Label(4,n,"费用金额",format5);
				sheet.mergeCells(4, n, 6, n); 
				sheet.addCell(cell);  
				cell=new Label(7,n,"产生费用时间",format5);
				sheet.mergeCells(7, n, 8, n); 
				sheet.addCell(cell);
				cell=new Label(9,n,"备注",format3);
				sheet.mergeCells(9, n, 13, n); 
				sheet.addCell(cell);
				sheet.setRowView(n,300,false); 
				n=n+1;
				
				List otherfees=(List)paylist.get("otherfees");
				double otherfee_price=0;
				for (Object object2 : otherfees) {
					Map otherfee=(Map)object2;
					cell=new Label(0,n,""); 
					sheet.addCell(cell); 
					cell=new Label(1,n,otherfee.get("OTHER_NAME")+"",format5);
					sheet.mergeCells(1, n, 3, n); 
					sheet.addCell(cell);  
					number=new Number(4,n,Double.parseDouble(otherfee.get("OTHER_PRICE")+""),ccellFormat_right);
					sheet.mergeCells(4, n, 6, n); 
					sheet.addCell(number); 
					cell=new Label(7,n,otherfee.get("OTHER_DATE")+"",format5);
					sheet.mergeCells(7, n, 8, n); 
					sheet.addCell(cell);  
					cell=new Label(9,n,otherfee.get("MEMO")+"",format3);
					sheet.mergeCells(9, n, 13, n); 
					sheet.addCell(cell);
					sheet.setRowView(n,300,false); 
					n=n+1;
					otherfee_price=otherfee_price+Double.parseDouble(otherfee.get("OTHER_PRICE")+"");
				}
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell); 
				cell=new Label(1,n,"合计(大写)",format3); 
				sheet.addCell(cell); 
				cell=new Label(2,n,CurrencyConverter.toUpper(otherfee_price+""),format3); 
				sheet.mergeCells(2, n, 8, n); 
				sheet.addCell(cell);
				cell=new Label(9,n,"合计(小写)",format3); 
				sheet.addCell(cell);
				number=new Number(10,n,otherfee_price,ccellFormat_right);
				sheet.mergeCells(10, n, 13, n);
				sheet.addCell(number);
				sheet.setRowView(n,300,false); 
				n=n+1;
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				cell=new Label(1,n,"融资租赁租金方案",format4); 
				sheet.mergeCells(1, n, 13, n); 
				sheet.addCell(cell);
				sheet.setRowView(n,320,false);  
				n=n+1;
				
				List irrMonthPaylines=(List)paylist.get("irrMonthPaylines");
				for (Object object3 : irrMonthPaylines) {
					Map irrMonthPayline=(Map)object3;
					cell=new Label(0,n,""); 
					sheet.addCell(cell);
					cell=new Label(1,n,"应收租金",format3);
					sheet.mergeCells(1, n, 3, n); 
					sheet.addCell(cell);
					number=new Number(4,n,Double.parseDouble(irrMonthPayline.get("IRR_MONTH_PRICE")+""),ccellFormat_left);
					sheet.mergeCells(4, n, 6, n);
					sheet.addCell(number);
					cell=new Label(7,n,"对应期次",format3);
					sheet.mergeCells(7, n, 8, n); 
					sheet.addCell(cell);
					cell=new Label(9,n,"第"+irrMonthPayline.get("IRR_MONTH_PRICE_START")+"期到第"+irrMonthPayline.get("IRR_MONTH_PRICE_END")+"期",format3);
					sheet.mergeCells(9, n, 13, n); 
					sheet.addCell(cell);
					sheet.setRowView(n,300,false);  
					n=n+1;
				}
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				cell=new Label(1,n,"融资租赁还款计划",format4); 
				sheet.mergeCells(1, n, 13, n); 
				sheet.addCell(cell);
				sheet.setRowView(n,320,false);  
				n=n+1;
				
				cell=new Label(0,n,"");
				sheet.addCell(cell);
				cell=new Label(1,n,"不等额",format3);
				sheet.mergeCells(1, n, 2, n); 
				sheet.addCell(cell);
				cell=new Label(3,n,"期次",format3); 
				sheet.addCell(cell);
				cell=new Label(4,n,"支付时间",format3); 
				sheet.addCell(cell);
				cell=new Label(5,n,"未税应收租金",format3); 
				sheet.addCell(cell);
				cell=new Label(6,n,"平均增值税",format3); 
				sheet.addCell(cell);
				cell=new Label(7,n,"含税应收租金",format3); 
				sheet.addCell(cell);
				cell=new Label(8,n,"合同各期租金",format3); 
				sheet.addCell(cell);
				cell=new Label(9,n,"本金",format3); 
				sheet.addCell(cell);
				cell=new Label(10,n,"利息",format3); 
				sheet.addCell(cell);
				cell=new Label(11,n,"剩余本金",format3); 
				sheet.addCell(cell);
				cell=new Label(12,n,"实际增值税",format3); 
				sheet.mergeCells(12, n, 13, n); 
				sheet.addCell(cell);
				sheet.setRowView(n,300,false);  
				n=n+1;
				
				List paylines=(List)paylist.get("paylines");
				double IRR_MONTH_PRICE_sum=0;
				double IRR_PRICE_sum=0;
				double MONTH_PRICE_sum=0;
				double OWN_PRICE_sum=0;
				double REN_PRICE_sum=0;
				double SALES_TAX_sum=0;
				double IRR_MONTH_TAX_PRICE_sum=0;
				for (Object object2 : paylines) {
					Map payline=(Map)object2;
					cell=new Label(0,n,""); 
					sheet.addCell(cell);
					String str_bu="";
					if((payline.get("LOCKED")+"").equals("1")){
						str_bu="不等额";
					}
					cell=new Label(1,n,str_bu,format3); 
					sheet.mergeCells(1, n, 2, n); 
					sheet.addCell(cell);
					number=new Number(3,n,Double.parseDouble(payline.get("PERIOD_NUM")+""),ncellFormat_center);
					sheet.addCell(number);
					DateTime dt=new DateTime(4,n,(Date)payline.get("PAY_DATE"),dateCellFormat_center);
					sheet.addCell(dt);
					number=new Number(5,n,Double.parseDouble(payline.get("IRR_MONTH_PRICE")+""),ccellFormat_right);
					sheet.addCell(number);
					number=new Number(6,n,Double.parseDouble(payline.get("VALUE_ADDED_TAX")+""),ccellFormat_right);
					sheet.addCell(number);
					number=new Number(7,n,Double.parseDouble(payline.get("IRR_MONTH_PRICE")+"")+Double.parseDouble(payline.get("VALUE_ADDED_TAX")+""),ccellFormat_right);
					sheet.addCell(number);
					number=new Number(8,n,Double.parseDouble(payline.get("MONTH_PRICE")+""),ccellFormat_right);
					sheet.addCell(number);
					number=new Number(9,n,Double.parseDouble(payline.get("OWN_PRICE")+""),ccellFormat_right);
					sheet.addCell(number);
					number=new Number(10,n,Double.parseDouble(payline.get("REN_PRICE")+""),ccellFormat_right);
					sheet.addCell(number);
					number=new Number(11,n,Double.parseDouble(payline.get("LAST_PRICE")+""),ccellFormat_right);
					sheet.addCell(number);
					number=new Number(12,n,Double.parseDouble(payline.get("VALUE_ADDED_TAX_TRUE")+""),ccellFormat_right);
					sheet.mergeCells(12, n, 13, n); 
					sheet.addCell(number); 
					sheet.setRowView(n,300,false);  
					n=n+1;
					
					IRR_MONTH_PRICE_sum=IRR_MONTH_PRICE_sum+Double.parseDouble(payline.get("IRR_MONTH_PRICE")+"");
					IRR_PRICE_sum=IRR_PRICE_sum+Double.parseDouble(payline.get("VALUE_ADDED_TAX")+"");
					MONTH_PRICE_sum=MONTH_PRICE_sum+Double.parseDouble(payline.get("MONTH_PRICE")+"");
					OWN_PRICE_sum=OWN_PRICE_sum+Double.parseDouble(payline.get("OWN_PRICE")+"");
					REN_PRICE_sum=REN_PRICE_sum+Double.parseDouble(payline.get("REN_PRICE")+"");
					SALES_TAX_sum=SALES_TAX_sum+Double.parseDouble(payline.get("VALUE_ADDED_TAX_TRUE")+"");
					IRR_MONTH_TAX_PRICE_sum=IRR_MONTH_TAX_PRICE_sum+Double.parseDouble(payline.get("IRR_MONTH_PRICE")+"")+Double.parseDouble(payline.get("VALUE_ADDED_TAX")+"");
				}
				 
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				cell=new Label(1,n,"合计",format3);
				sheet.mergeCells(1, n, 4, n);
				sheet.addCell(cell);
				number=new Number(5,n,IRR_MONTH_PRICE_sum,ccellFormat_right);
				sheet.addCell(number);
				number=new Number(6,n,IRR_PRICE_sum,ccellFormat_right);
				sheet.addCell(number);
				number=new Number(7,n,IRR_MONTH_TAX_PRICE_sum,ccellFormat_right);
				sheet.addCell(number);
				number=new Number(8,n,MONTH_PRICE_sum,ccellFormat_right);
				sheet.addCell(number);
				number=new Number(9,n,OWN_PRICE_sum,ccellFormat_right);
				sheet.addCell(number);
				number=new Number(10,n,REN_PRICE_sum,ccellFormat_right);
				sheet.addCell(number); 
				number=new Number(12,n,SALES_TAX_sum,ccellFormat_right);
				sheet.mergeCells(12, n, 13, n); 
				sheet.addCell(number);
				sheet.setRowView(n,300,false);  
				n=n+1;
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				cell=new Label(1,n,"备注",format4); 
				sheet.mergeCells(1, n, 13, n); 
				sheet.addCell(cell);
				sheet.setRowView(n,320,false);  
				n=n+1;

				java.text.NumberFormat nf=java.text.NumberFormat.getCurrencyInstance();
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				cell=new Label(1,n,"承租人应在融资租赁合同签署后五个工作日内，" 
						+"将首期款电汇至出租人指定的账户。\n" 
						+"首期款包括：租赁质押金、管理费、首期租金、差旅费等， 以上共计:"
						+nf.format(Double.parseDouble(paylist.get("PLEDGE_PRICE")+"")
						//+Double.parseDouble(paylist.get("MANAGEMENT_FEE")+"")
						+Double.parseDouble(paylist.get("BUSINESS_TRIP_PRICE")+"")
						+Double.parseDouble(paylist.get("HEAD_HIRE")+""))
						+"元",format3); 
				sheet.mergeCells(1, n, 13, n); 
				sheet.addCell(cell);
				sheet.setRowView(n,650,false);  
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
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}
		return baos;
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

	public ByteArrayOutputStream exportRZSYExcel(List paylists) {
		for (Object object : paylists) {
			List<Map> paylist = (List<Map>) object;
			WritableSheet sheet = null;
			try {
				/* 解决中文乱码 */
				workbookSettings.setEncoding("ISO-8859-1");
				sheet = wb.createSheet((String) paylist.get(0).get("CUST_NAME"), 1);

				WritableFont font2 = new WritableFont(WritableFont
						.createFont("宋体"), 10, WritableFont.BOLD);
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
				WritableFont font3 = new WritableFont(WritableFont
						.createFont("宋体"), 10, WritableFont.NO_BOLD);
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
				WritableFont font6 = new WritableFont(WritableFont
						.createFont("宋体"), 10, WritableFont.NO_BOLD);
				font6.setColour(Colour.RED);
				WritableCellFormat format6 = new WritableCellFormat(font6);
				format6.setAlignment(Alignment.LEFT); 
				format6.setVerticalAlignment(VerticalAlignment.CENTRE);
				// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
				format6.setWrap(true);
				
				WritableFont font4 = new WritableFont(WritableFont
						.createFont("宋体"), 12, WritableFont.NO_BOLD);
				WritableCellFormat format4 = new WritableCellFormat(font4);
				format4.setAlignment(Alignment.LEFT);
				format4.setBackground(jxl.format.Colour.GRAY_25);
				format4.setVerticalAlignment(VerticalAlignment.CENTRE);
				// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
				format4.setWrap(true);
				
				WritableFont font7 = new WritableFont(WritableFont
						.createFont("宋体"), 12, WritableFont.NO_BOLD);
				WritableCellFormat format7 = new WritableCellFormat(font7);
				format7.setAlignment(Alignment.RIGHT);
				format7.setBackground(jxl.format.Colour.GRAY_25);
				format7.setVerticalAlignment(VerticalAlignment.CENTRE);
				format7.setWrap(true);
				
				WritableFont font8 = new WritableFont(WritableFont
						.createFont("宋体"), 12, WritableFont.BOLD);
				WritableCellFormat format8 = new WritableCellFormat(font8);
				format8.setAlignment(Alignment.CENTRE);
				format8.setVerticalAlignment(VerticalAlignment.CENTRE);
				format8.setWrap(true);
				
				NumberFormat cnum=new NumberFormat("#,##0.00");
				NumberFormat pnum=new NumberFormat("##0.00%");
				NumberFormat fnum=new NumberFormat("##0.00");
				NumberFormat nnum=new NumberFormat("##"); 
				DateFormat   dateFormat=new DateFormat("yyyy-mm-dd");
				WritableCellFormat ccellFormat_right=new WritableCellFormat(cnum);
				WritableCellFormat pcellFormat_left=new WritableCellFormat(pnum);
				WritableCellFormat pcellFormat_center=new WritableCellFormat(pnum);
				WritableCellFormat fcellFormat_left=new WritableCellFormat(fnum);
				WritableCellFormat ccellFormat_left=new WritableCellFormat(cnum);
				WritableCellFormat dateCellFormat_left=new WritableCellFormat(dateFormat);
				WritableCellFormat dateCellFormat_center=new WritableCellFormat(dateFormat);
				ccellFormat_left.setAlignment(Alignment.LEFT);
				pcellFormat_left.setAlignment(Alignment.LEFT);
				pcellFormat_center.setAlignment(Alignment.CENTRE);
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
				sheet.setColumnView(1, 20);
				sheet.setColumnView(2, 20);
				sheet.setColumnView(3, 20);
				sheet.setColumnView(4, 20);
				sheet.setColumnView(5, 20);
				sheet.setColumnView(6, 4);

				int n=0;
				Label cell = null;
				
				cell=new Label(0,0,"");  
				sheet.addCell(cell);
				cell=new Label(1,0,DataUtil.StringUtil(paylist.get(0).get("CUST_NAME"))+"未实现融资收益分配表",format8);  
				sheet.mergeCells(1, 0, 5, 1);
				sheet.addCell(cell);
				n=n+2;
				sheet.setRowView(0,300,false); 
				
				 
				
				cell=new Label(0,2,""); 
				sheet.addCell(cell);
				cell=new Label(1,2,DataUtil.StringUtil("承租人："+paylist.get(0).get("CUST_NAME")),format4); 
				sheet.mergeCells(1, 2, 3, 2); 
				sheet.addCell(cell);
				cell=new Label(4,2,DataUtil.StringUtil("税务登记号："+(paylist.get(0).get("CORP_TAX_CODE")==null?"":(paylist.get(0).get("CORP_TAX_CODE")))),format7); 

				sheet.mergeCells(4, 2, 5, 2); 
				sheet.addCell(cell);
				cell=new Label(6,2,"");
				sheet.addCell(cell);
				n=n+1;
				sheet.setRowView(1,320,false); 
				
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				cell=new Label(1,n,"日期",format5); 
				sheet.addCell(cell);
				cell=new Label(2,n,"租金",format5); 
				sheet.addCell(cell);
				cell=new Label(3,n,"确认的融资收入",format5); 
				sheet.addCell(cell);
				cell=new Label(4,n,"租赁投资净额减少额",format5); 
				sheet.addCell(cell);
				cell=new Label(5,n,"租赁投资净额余额",format5); 
				sheet.addCell(cell);
				sheet.setRowView(n,300,false); 
				n=n+1;
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				cell=new Label(1,n,"",format5); 
				sheet.addCell(cell);
				cell=new Label(2,n,"",format5); 
				sheet.addCell(cell);
				cell=new Label(3,n,"",format5); 
				sheet.addCell(cell);
				cell=new Label(4,n,"",format5); 
				sheet.addCell(cell);
				number=new Number(5,n,Double.parseDouble(paylist.get(0).get("LEASE_TOPRIC")+""),ccellFormat_right);
				sheet.addCell(number);
				sheet.setRowView(n,300,false); 
				n=n+1;
				double monthPriceCount=0.00;
				double renpriceCount=0.00;
				double jianshaoeCount=0.00;
				for(int i=0;i<paylist.size();i++){
					cell=new Label(0,n,""); 
					sheet.addCell(cell);
					DateTime dt = null ;
					if(paylist.get(i).get("PAY_DATE") != null && !"".equals(paylist.get(i).get("PAY_DATE"))){
						dt=new DateTime(1,n,(Date)paylist.get(i).get("PAY_DATE"),dateCellFormat_center);
						sheet.addCell(dt);
					} 
					//Modify by Michael 2012 07-04 租金为预期租金栏位
//					number=new Number(2,n,Double.parseDouble(paylist.get(i).get("IRR_MONTH_PRICE")+""),ccellFormat_right);
//					monthPriceCount+=Double.parseDouble(paylist.get(i).get("IRR_MONTH_PRICE").toString());
					
					number=new Number(2,n,Double.parseDouble(paylist.get(i).get("MONTH_PRICE")+""),ccellFormat_right);
					monthPriceCount+=Double.parseDouble(paylist.get(i).get("MONTH_PRICE").toString());
					
					sheet.addCell(number);
					number=new Number(3,n,Double.parseDouble(paylist.get(i).get("REN_PRICE")+""),ccellFormat_right);
					renpriceCount+=Double.parseDouble(paylist.get(i).get("REN_PRICE").toString());
					sheet.addCell(number);
					number=new Number(4,n,Double.parseDouble(paylist.get(i).get("JIANSHAOE")+""),ccellFormat_right);
					jianshaoeCount+=Double.parseDouble(paylist.get(i).get("JIANSHAOE").toString());
					sheet.addCell(number);
					number=new Number(5,n,Double.parseDouble(paylist.get(i).get("YUE")+""),ccellFormat_right);
					sheet.addCell(number);
					sheet.setRowView(n,300,false); 
					n=n+1;
				}
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				cell=new Label(1,n,"合计",format5); 
				sheet.addCell(cell);
				number=new Number(2,n,Double.parseDouble(monthPriceCount+""),ccellFormat_right);
				sheet.addCell(number);
				number=new Number(3,n,Double.parseDouble(renpriceCount+""),ccellFormat_right);
				sheet.addCell(number);
				number=new Number(4,n,Double.parseDouble(jianshaoeCount+""),ccellFormat_right);
				sheet.addCell(number);
				n=n+1;
				
				cell=new Label(0,0,"",format2_RIGHT);
				sheet.mergeCells(0, 0, 0, n-1);
				sheet.addCell(cell);
				cell=new Label(7,0,"",format2_LEFT);
				sheet.mergeCells(7, 0, 7, n-1);
				sheet.addCell(cell);
				cell=new Label(1,n,"",format2_Top);
				sheet.mergeCells(1, n, 6, n); 
				sheet.addCell(cell);
				
			} catch (Exception e) {
				LogPrint.getLogStackTrace(e, logger);
			}
			
		}
		return baos;
	}

	//Add by Michael 2012 4-20 增加导出本金摊还表
	public ByteArrayOutputStream exportOwnPriceExcel(List paylists) {
		for (Object object : paylists) {
			List<Map> paylist = (List<Map>) object;
			WritableSheet sheet = null;
			try {
				/* 解决中文乱码 */
				workbookSettings.setEncoding("ISO-8859-1");
				sheet = wb.createSheet((String) paylist.get(0).get("CUST_NAME"), 1);

				WritableFont font2 = new WritableFont(WritableFont
						.createFont("宋体"), 10, WritableFont.BOLD);
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
				WritableFont font3 = new WritableFont(WritableFont
						.createFont("宋体"), 10, WritableFont.NO_BOLD);
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
				WritableFont font6 = new WritableFont(WritableFont
						.createFont("宋体"), 10, WritableFont.NO_BOLD);
				font6.setColour(Colour.RED);
				WritableCellFormat format6 = new WritableCellFormat(font6);
				format6.setAlignment(Alignment.LEFT); 
				format6.setVerticalAlignment(VerticalAlignment.CENTRE);
				// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
				format6.setWrap(true);
				
				WritableFont font4 = new WritableFont(WritableFont
						.createFont("宋体"), 12, WritableFont.NO_BOLD);
				WritableCellFormat format4 = new WritableCellFormat(font4);
				format4.setAlignment(Alignment.LEFT);
				format4.setBackground(jxl.format.Colour.GRAY_25);
				format4.setVerticalAlignment(VerticalAlignment.CENTRE);
				// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
				format4.setWrap(true);
				
				WritableFont font7 = new WritableFont(WritableFont
						.createFont("宋体"), 12, WritableFont.NO_BOLD);
				WritableCellFormat format7 = new WritableCellFormat(font7);
				format7.setAlignment(Alignment.RIGHT);
				format7.setBackground(jxl.format.Colour.GRAY_25);
				format7.setVerticalAlignment(VerticalAlignment.CENTRE);
				format7.setWrap(true);
				
				WritableFont font8 = new WritableFont(WritableFont
						.createFont("宋体"), 12, WritableFont.BOLD);
				WritableCellFormat format8 = new WritableCellFormat(font8);
				format8.setAlignment(Alignment.CENTRE);
				format8.setVerticalAlignment(VerticalAlignment.CENTRE);
				format8.setWrap(true);
				
				WritableCellFormat format9 = new WritableCellFormat(font8);
				format9.setAlignment(Alignment.LEFT);
				format9.setVerticalAlignment(VerticalAlignment.CENTRE);
				format9.setWrap(true);
				
				NumberFormat cnum=new NumberFormat("#,##0.00");
				NumberFormat pnum=new NumberFormat("##0.00%");
				NumberFormat fnum=new NumberFormat("##0.00");
				NumberFormat nnum=new NumberFormat("##"); 
				DateFormat   dateFormat=new DateFormat("yyyy-mm-dd");
				WritableCellFormat ccellFormat_right=new WritableCellFormat(cnum);
				WritableCellFormat pcellFormat_left=new WritableCellFormat(pnum);
				WritableCellFormat pcellFormat_center=new WritableCellFormat(pnum);
				WritableCellFormat fcellFormat_left=new WritableCellFormat(fnum);
				WritableCellFormat ccellFormat_left=new WritableCellFormat(cnum);
				WritableCellFormat dateCellFormat_left=new WritableCellFormat(dateFormat);
				WritableCellFormat dateCellFormat_center=new WritableCellFormat(dateFormat);
				ccellFormat_left.setAlignment(Alignment.LEFT);
				pcellFormat_left.setAlignment(Alignment.LEFT);
				pcellFormat_center.setAlignment(Alignment.CENTRE);
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
				sheet.setColumnView(3, 20);
				sheet.setColumnView(4, 20);
				sheet.setColumnView(5, 20);
				sheet.setColumnView(6, 4);

				int n=0;
				Label cell = null;
				
				cell=new Label(0,0,"");  
				sheet.addCell(cell);
				cell=new Label(1,0,"",format2_BOTTOM);  
				sheet.mergeCells(1, 0,6, 0);
				sheet.addCell(cell);
				n=n+1;
				sheet.setRowView(0,300,false); 
				
				cell=new Label(0,1,""); 
				sheet.addCell(cell);
				cell=new Label(1,1,"本息摊还表",format8); 
				sheet.mergeCells(1, 1, 6, 1); 
				sheet.addCell(cell);
				n=n+1;
				sheet.setRowView(1,320,false);
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				cell=new Label(1,n,DataUtil.StringUtil("致："+paylist.get(0).get("CUST_NAME")),format9); 
				sheet.mergeCells(1, n, 6, 1); 
				sheet.addCell(cell);
				n=n+1;
				sheet.setRowView(1,320,false); 
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				cell=new Label(1,n,DataUtil.StringUtil("贵司与我司签订的融资租赁合同编号为："+paylist.get(0).get("LEASE_CODE")+",其本金摊还如下："),format3); 
				sheet.mergeCells(1, n, 6, 1); 
				sheet.addCell(cell);
				n=n+1;
				sheet.setRowView(1,320,false); 	
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				cell=new Label(1,n,DataUtil.StringUtil("融资租赁还款期间"),format4); 
				sheet.mergeCells(1, n, 6, 1); 
				sheet.addCell(cell);
				n=n+1;
				sheet.setRowView(1,320,false); 	
				
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				cell=new Label(1,n,"期次",format5); 
				sheet.addCell(cell);
				cell=new Label(2,n,"租金",format5); 
				sheet.addCell(cell);
				cell=new Label(3,n,"本金",format5); 
				sheet.addCell(cell);
				cell=new Label(4,n,"利息",format5); 
				sheet.addCell(cell);
				cell=new Label(5,n,"剩余本金",format5); 
				sheet.addCell(cell);
				sheet.setRowView(n,300,false); 
				n=n+1;
				
				double monthPriceCount=0.00;
				double renpriceCount=0.00;
				double ownpriceCount=0.00;
				for(int i=0;i<paylist.size();i++){
					cell=new Label(0,n,""); 
					sheet.addCell(cell);
					number=new Number(1,n,Double.parseDouble(paylist.get(i).get("PERIOD_NUM")+""),ncellFormat_center);
					sheet.addCell(number);
					number=new Number(2,n,Double.parseDouble(paylist.get(i).get("MONTH_PRICE")+""),ccellFormat_right);
					monthPriceCount+=Double.parseDouble(paylist.get(i).get("MONTH_PRICE").toString());
					sheet.addCell(number);
					number=new Number(3,n,Double.parseDouble(paylist.get(i).get("OWN_PRICE")+""),ccellFormat_right);
					ownpriceCount+=Double.parseDouble(paylist.get(i).get("OWN_PRICE").toString());
					sheet.addCell(number);
					number=new Number(4,n,Double.parseDouble(paylist.get(i).get("REN_PRICE")+""),ccellFormat_right);
					renpriceCount+=Double.parseDouble(paylist.get(i).get("REN_PRICE").toString());
					sheet.addCell(number);
					number=new Number(5,n,Double.parseDouble(paylist.get(i).get("LAST_PRICE")+""),ccellFormat_right);
					sheet.addCell(number);

					sheet.setRowView(n,300,false); 
					n=n+1;
				}
				cell=new Label(0,n,""); 
				sheet.addCell(cell);
				cell=new Label(1,n,"合计",format5); 
				sheet.addCell(cell);
				number=new Number(2,n,Double.parseDouble(monthPriceCount+""),ccellFormat_right);
				sheet.addCell(number);
				number=new Number(3,n,Double.parseDouble(ownpriceCount+""),ccellFormat_right);
				sheet.addCell(number);
				number=new Number(4,n,Double.parseDouble(renpriceCount+""),ccellFormat_right);
				sheet.addCell(number);
				cell=new Label(5,n,""); 
				sheet.addCell(cell);
				n=n+1;
				
				cell=new Label(0,0,"",format2_RIGHT);
				sheet.mergeCells(0, 0, 0, n-1);
				sheet.addCell(cell);
				cell=new Label(7,0,"",format2_LEFT);
				sheet.mergeCells(7, 0, 7, n-1);
				sheet.addCell(cell);
				cell=new Label(1,n,"",format2_Top);
				sheet.mergeCells(1, n, 6, n); 
				sheet.addCell(cell);
				
			} catch (Exception e) {
				LogPrint.getLogStackTrace(e, logger);
			}
			
		}
		return baos;
	}

}
