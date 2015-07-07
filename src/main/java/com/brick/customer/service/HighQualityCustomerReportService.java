package com.brick.customer.service;


import java.io.File;
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


public class HighQualityCustomerReportService extends BaseService{
	
	private CommonDAO commonDAO;
	
	private String path;
	
	private MailUtilService mailUtilService;
	
	public void getReport()throws Exception{
		
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(calendar.YEAR);
		int month = calendar.get(calendar.MONTH) + 1;
		String startDate = String.valueOf(year);
		if(month<10){
			startDate += "0" + month;
		}else{
			startDate += month;
		}
		startDate += "01";
		
		month += 4;

		
		if(month>12){
			month -= 12;
			year += 1;
		}
		String endDate = String.valueOf(year);
		if(month<10){
			endDate += "0" + month;
		}else{
			endDate += month;
		}
		endDate += "01";
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("startDate",startDate);
		params.put("endDate", endDate);
		
		List<Map<String, Object>> list = (List<Map<String, Object>>) commonDAO.queryForListUseMap("report.getHighQualityCustomerReport",params);
		WritableSheet sheet = null;
		
		String filePath =  getPath() + File.separator + startDate;
		File file = new File(filePath);
		if(!file.exists()){
			file.mkdirs();
		}

		filePath +=  File.separator +"缴款正常客户名单.xls";
		file = new File(filePath);
		
		WorkbookSettings workbookSettings = new WorkbookSettings();
		workbookSettings.setEncoding("ISO-8859-1");
		WritableWorkbook wb = Workbook.createWorkbook(file, workbookSettings);
		sheet = wb.createSheet("缴款正常客户名单", 0);
		
		
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
		
	
		WritableCellFormat precent = new WritableCellFormat(NumberFormats.PERCENT_FLOAT);
		precent.setAlignment(Alignment.CENTRE);
		precent.setVerticalAlignment(VerticalAlignment.CENTRE); 
		precent.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
		precent.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
		precent.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
		precent.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
		precent.setWrap(true);
		
		
		
		int column = 0;
		int row = 1;
		
		Label lable = new Label(column, row, "序号", format);
		sheet.addCell(lable);
		column++;

		createLabel(sheet, row, column, "合同号",format);
		column += 2;
		
		createLabel(sheet, row, column, "户名",format);
		column += 2;
		
		createLabel(sheet, row, column, "联络人/电话",format);
		column += 2;
		
		createLabel(sheet, row, column, "拨款日期",format);
		column += 2;
		
		createLabel(sheet, row, column, "承做期数",format);
		column += 2;
		
		createLabel(sheet, row, column, "剩余期数",format);
		column += 2;
		
		createLabel(sheet, row, column, "承做TR",format);
		column += 2;
				
		createLabel(sheet, row, column, "逾期次数（≤7天）",format);
		column += 2;
		
		createLabel(sheet, row, column, "平均逾期天数",format);
		column += 2;
		
		createLabel(sheet, row, column, "办事处",format);
		column += 2;
		
		createLabel(sheet, row, column, "经办",format);

		
		row++;
		if(list != null && list.size()>0){
			int i = 1;
			for(Map m :list){
				column = 0;
				jxl.write.Number numberLabel  =   new  jxl.write.Number(column, row ,i,format2);  
				sheet.addCell(numberLabel);
				column++;
				
				createLabel(sheet, row, column, m.get("LEASE_CODE").toString(),format2);
				column += 2;
				
				createLabel(sheet, row, column, m.get("CUST_NAME").toString(),format2);
				column += 2;
				
				createLabel(sheet, row, column, m.get("LINK_NAME").toString() + "/" + m.get("LINK_MOBILE").toString(),format2);
				column += 2;
				
				createLabel(sheet, row, column, m.get("PAY_DATE").toString(),format2);
				column += 2;
				
				createNumberLabel(sheet, row, column, m.get("LEASE_TERM").toString(),format2);
				column += 2;
				
				createNumberLabel(sheet, row, column, m.get("LEFT_TERM").toString(),format2);
				column += 2;
				
				sheet.mergeCells(column, row, column+1, row);
				numberLabel  =   new  jxl.write.Number(column, row ,Double.parseDouble(m.get("TR").toString())/100f,precent);
				sheet.addCell(numberLabel);
				column += 2;
				
				createNumberLabel(sheet, row, column, m.get("OVERDUE").toString(),format2);
				column += 2;
				
				createNumberLabel(sheet, row, column, m.get("AVG_DAYS").toString(),format2);
				column += 2;
				
				createLabel(sheet, row, column, m.get("DECP_NAME_CN").toString(),format2);
				column += 2;
				
				createLabel(sheet, row, column, m.get("SALE_MANAGER").toString(),format2);
				
				row++;
				i++;
			}
		}
	
		wb.write();
		wb.close();
		
		MailSettingTo mailSettingTo = new MailSettingTo();
		mailSettingTo.setEmailContent("4个月之内即将到期之缴款正常客户名单");
		mailSettingTo.setEmailAttachPath(filePath);
		mailUtilService.sendMail(2001, mailSettingTo);
	}
	
	private void createLabel(WritableSheet sheet,int row,int column,String content,WritableCellFormat format) throws RowsExceededException, WriteException{
		sheet.mergeCells(column, row, column+1, row);
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
