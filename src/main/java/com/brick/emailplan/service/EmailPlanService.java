package com.brick.emailplan.service;

import java.io.File;
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
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.util.DateUtil;

public class EmailPlanService extends BaseService{
	
	
	private MailUtilService mailUtilService;
	private String path;
	
	public void sendProjectAduitEmail() throws Exception{
		try{
		List<Map> list = (List<Map>) this.baseDAO.queryForList("decompose.getNewProjectForRentDecompose");
		
		if(list!=null && list.size()>0){
			
			String filePath = "\\\\"+LeaseUtil.getIPAddress() + path + File.separator + DateUtil.getCurrentDate();
			File file = new File(filePath);
			if(!file.exists()){
				file.mkdirs();
			}

			filePath +=  File.separator +"新案文审过案提醒（"+DateUtil.getCurrentDate()+"）.xls";
			file = new File(filePath);
			
			WorkbookSettings workbookSettings = new WorkbookSettings();
			workbookSettings.setEncoding("ISO-8859-1");
			WritableWorkbook wb = Workbook.createWorkbook(file, workbookSettings);
			WritableSheet sheet = wb.createSheet("新案文审过案提醒", 0);
			
			
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
			int row = 0;
			

			createLabel(sheet, row, column, "案件号",format);
			column += 2;

			createLabel(sheet, row, column, "合同号",format);
			column += 2;
			
			createLabel(sheet, row, column, "客户名称",format);
				
			row++;
			if(list != null && list.size()>0){
				int i = 1;
				for(Map m :list){
					column = 0;
					
					createLabel(sheet, row, column, m.get("CREDIT_RUNCODE").toString(),format2);
					column += 2;
					
					createLabel(sheet, row, column, m.get("LEASE_CODE").toString(),format2);
					column += 2;
					
					createLabel(sheet, row, column, m.get("CUST_NAME").toString(),format2);
								
					row++;
					i++;
				}
			}
		
			wb.write();
			wb.close();
			
			MailSettingTo mailSettingTo = new MailSettingTo();
			mailSettingTo.setEmailContent("新案文审过案提醒。");
			mailSettingTo.setEmailAttachPath(filePath);
			mailUtilService.sendMail(2003, mailSettingTo);
			
		}	
		}catch(Exception e){e.printStackTrace();};
	}
	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}
	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}
	
	
	public String getPath() {
		return "\\\\"+LeaseUtil.getIPAddress()+path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	private void createLabel(WritableSheet sheet,int row,int column,String content,WritableCellFormat format) throws RowsExceededException, WriteException{
		sheet.mergeCells(column, row, column+1, row);
		Label lable = new Label(column, row, content, format);
		sheet.addCell(lable);
	}
}
