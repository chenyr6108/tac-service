package com.brick.report.service;

import java.io.File;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import com.brick.base.service.BaseService;
import com.brick.base.util.LeaseUtil;
import com.brick.common.dao.CommonDAO;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.customer.service.HighQualityCustomerReportService;
import com.brick.util.DateUtil;

public class ContractExceptRentService extends BaseService{
	private CommonDAO commonDAO;
	
	private String path;
	
	private MailUtilService mailUtilService;
	
	public void getReport()throws Exception{
		
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(calendar.YEAR);
		int month = calendar.get(calendar.MONTH) + 1;
		int day = calendar.get(calendar.DAY_OF_MONTH);
		if((month==3 && day==25)||(month==6 && day==25)||(month==9 && day==25)||(month==12 && day==31)){
			String date = DateUtil.getCurrentDate();

			String filePath =  path + File.separator + year;
			File file = new File(filePath);
			if(!file.exists()){
				file.mkdirs();
			}
			
			filePath +=  File.separator +"按季度支付表类型的合同预期租金("+date+").xls";
			file = new File(filePath);
			
			WorkbookSettings workbookSettings = new WorkbookSettings();
			workbookSettings.setEncoding("ISO-8859-1");
			WritableWorkbook wb = Workbook.createWorkbook(file, workbookSettings);
			WritableSheet sheet = wb.createSheet("按季度支付表类型的合同预期租金", 0);
			
			
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
			format2.setAlignment(Alignment.RIGHT);
			format2.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
			format2.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
			format2.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
			format2.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
			format2.setWrap(true);
			
			
			
			int column = 0;
			int row = 0;
			
			Label lable = new Label(column, row, "年", format);
			sheet.addCell(lable);
			column++;

			lable = new Label(column, row, "月", format);
			sheet.addCell(lable);
			column++;
			
			lable = new Label(column, row, "合同预期租金", format);
			sheet.addCell(lable);
			column++;
			
			lable = new Label(column, row, "利息", format);
			sheet.addCell(lable);
			column++;
		

			
			row++;
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("date", date);
			List<Map<String, Object>> list = (List<Map<String, Object>>) commonDAO.queryForListUseMap("report.getContractExceptRent",params);
			if(list != null && list.size()>0){

				for(Map m :list){
					column = 0;
					jxl.write.Number numberLabel  =   new  jxl.write.Number(column, row ,(Integer) m.get("Y"),format2);  
					sheet.addCell(numberLabel);
					column++;
					
					numberLabel  =   new  jxl.write.Number(column, row ,(Integer) m.get("M"),format2);  
					sheet.addCell(numberLabel);
					column++;
					
					BigDecimal price = (BigDecimal) m.get("PRICE");
					numberLabel  =   new  jxl.write.Number(column, row ,price.doubleValue(),format2);  
					sheet.addCell(numberLabel);
					column++;
					
					BigDecimal interst = (BigDecimal) m.get("INTEREST");
					numberLabel  =   new  jxl.write.Number(column, row ,interst.doubleValue(),format2);  
					sheet.addCell(numberLabel);			
					row++;
				}
			}
		
		    Map total = (Map) commonDAO.queryForObjUseMap("report.getContractExceptRentTotal",params);
		    BigDecimal price = (BigDecimal) total.get("PRICE");
		    BigDecimal interst = (BigDecimal) total.get("INTEREST");
		    
			jxl.write.Number numberLabel  =   new  jxl.write.Number(2, row ,price.doubleValue(),format2);  
			sheet.addCell(numberLabel);
			numberLabel  =   new  jxl.write.Number(3, row ,interst.doubleValue(),format2);  
			sheet.addCell(numberLabel);
			
			wb.write();
			wb.close();
			
			MailSettingTo mailSettingTo = new MailSettingTo();
			mailSettingTo.setEmailContent("按季度支付表类型的合同预期租金");
			mailSettingTo.setEmailAttachPath(filePath);
			mailUtilService.sendMail(2002, mailSettingTo);
			
			
			//乘用车预期租金和本金
			List<Map<String, Object>> list2 = (List<Map<String, Object>>) commonDAO.queryForListUseMap("report.getCarContractMoney",params);
			
			filePath =  getPath() + File.separator + year;
			file = new File(filePath);
			if(!file.exists()){
				file.mkdirs();
			}
			
			filePath +=  File.separator +"小车委贷案件预期租金及本金数据("+date+").xls";
			file = new File(filePath);
			
			workbookSettings = new WorkbookSettings();
			workbookSettings.setEncoding("ISO-8859-1");
			wb = Workbook.createWorkbook(file, workbookSettings);
			sheet = wb.createSheet("小车委贷案件预期租金及本金数据", 0);
			
			font = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
			format = new WritableCellFormat(font);
			format.setAlignment(Alignment.CENTRE);
			format.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
			format.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
			format.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
			format.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
			format.setWrap(true);
			

			font2 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);	
			format2 = new WritableCellFormat(font2);
			format2.setAlignment(Alignment.RIGHT);
			format2.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
			format2.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
			format2.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
			format2.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
			format2.setWrap(true);
			
			
			column = 0;
			row = 0;
			
			lable = new Label(column, row, "年", format);
			sheet.addCell(lable);
			column++;

			lable = new Label(column, row, "月", format);
			sheet.addCell(lable);
			column++;
			
			lable = new Label(column, row, "预期租金", format);
			sheet.addCell(lable);
			column++;
			
			lable = new Label(column, row, "本金", format);
			sheet.addCell(lable);
			column++;
			row++;
			if(list2 != null && list2.size()>0){
				for(Map m :list2){
					column = 0;
					jxl.write.Number numberLabel1  =   new  jxl.write.Number(column, row ,(Integer) m.get("Y"),format2);  
					sheet.addCell(numberLabel1);
					column++;
					
					numberLabel1  =   new  jxl.write.Number(column, row ,(Integer) m.get("M"),format2);  
					sheet.addCell(numberLabel1);
					column++;
					
					BigDecimal monthPrice = (BigDecimal) m.get("MONTH_PRICE");
					numberLabel1  =   new  jxl.write.Number(column, row ,monthPrice.doubleValue(),format2);  
					sheet.addCell(numberLabel1);
					column++;
					
					BigDecimal ownPrice = (BigDecimal) m.get("OWN_PRICE");
					numberLabel1  =   new  jxl.write.Number(column, row ,ownPrice.doubleValue(),format2);  
					sheet.addCell(numberLabel1);			
					row++;
				}
			}
			
			total = (Map) commonDAO.queryForObjUseMap("report.getCarContractMoneyTotal",params);
		    BigDecimal monthPrice = (BigDecimal) total.get("MONTH_PRICE");
		    BigDecimal ownPrice = (BigDecimal) total.get("OWN_PRICE");
		    
			jxl.write.Number numberLabel1  =   new  jxl.write.Number(2, row ,monthPrice.doubleValue(),format2);  
			sheet.addCell(numberLabel1);
			numberLabel1  =   new  jxl.write.Number(3, row ,ownPrice.doubleValue(),format2);  
			sheet.addCell(numberLabel1);
			
			wb.write();
			wb.close();
			
			mailSettingTo = new MailSettingTo();
			mailSettingTo.setEmailContent("小车委贷案件预期租金及本金数据");
			mailSettingTo.setEmailAttachPath(filePath);
			mailUtilService.sendMail(2006, mailSettingTo);
		}
		

	}
	
	private void createLabel(WritableSheet sheet,int row,int column,String content,WritableCellFormat format) throws RowsExceededException, WriteException{
		
		Label lable = new Label(column, row, content, format);
		sheet.addCell(lable);
	}
	
	private void createNumberLabel(WritableSheet sheet,int row,int column,String content,WritableCellFormat format) throws RowsExceededException, WriteException{
		sheet.mergeCells(column, row, column+1, row);
		jxl.write.Number numberLabel  =   new  jxl.write.Number(column, row ,Double.parseDouble(content),format);  
		sheet.addCell(numberLabel);
	}
	public static void main(String[] args) throws Exception {
		HighQualityCustomerReportService a = new HighQualityCustomerReportService();
		a.getReport();
	}

	public CommonDAO getCommonDAO() {
		return commonDAO;
	}

	public void setCommonDAO(CommonDAO commonDAO) {
		this.commonDAO = commonDAO;
	}



	public String getPath() {
		return "\\\\"+LeaseUtil.getIPAddress()+path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}
	
}
