package com.brick.job.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.brick.base.service.BaseService;
import com.brick.base.util.BirtReportEngine;
import com.brick.base.util.LeaseUtil;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.util.DateUtil;

public class GetData4Table extends BaseService {
	
	private BirtReportEngine birt;
	private MailUtilService mailUtilService;
	
	public BirtReportEngine getBirt() {
		return birt;
	}

	public void setBirt(BirtReportEngine birt) {
		this.birt = birt;
	}

	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}

	public void doService() throws Exception{
		//判断是否是当月第一个工作日
		if (!isTheFirstWorkingDay()) {
			System.out.println("非第一个工作日");
			return;
		}
		String filePath = "\\\\"+LeaseUtil.getIPAddress()+"\\home\\filsoft\\financelease\\birtReport" + 
				File.separator + "捞数据" + File.separator + DateUtil.dateToString(new Date(), "yyyy_MM_dd");
		String fileName = "捞数据4表格.xls";
		OutputStream out = null;
		HSSFWorkbook wb = null;
		try {
			File path = new File(filePath);
			path.mkdirs();
			File f = new File(path, fileName);
			wb = new HSSFWorkbook();
			//sheet1
			HSSFSheet sheet1 = wb.createSheet("sheet1");
			List<Map<String, Object>> data4Of1 = (List<Map<String, Object>>) this.queryForList("job.getData4Of1");
			this.drawSheet(sheet1, data4Of1);
			//sheet2
			HSSFSheet sheet2 = wb.createSheet("sheet2");
			List<Map<String, Object>> data4Of2 = (List<Map<String, Object>>) this.queryForList("job.getData4Of2");
			this.drawSheet(sheet2, data4Of2);
			//sheet3
			HSSFSheet sheet3 = wb.createSheet("sheet3");
			List<Map<String, Object>> data4Of3 = (List<Map<String, Object>>) this.queryForList("job.getData4Of3");
			this.drawSheet(sheet3, data4Of3);
			//sheet4
			HSSFSheet sheet4 = wb.createSheet("sheet4");
			List<Map<String, Object>> data4Of4 = (List<Map<String, Object>>) this.queryForList("job.getData4Of4");
			this.drawSheet(sheet4, data4Of4);
			out = new FileOutputStream(f);
			wb.write(out);
			//发送Email
			MailSettingTo mailSettingTo = new MailSettingTo();
			mailSettingTo.setEmailAttachPath(f.getPath());
			mailUtilService.sendMail(130, mailSettingTo);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}
		
	}
	
	
	public void drawSheet(HSSFSheet sheet, List<Map<String, Object>> data){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (data == null || data.size() == 0) {
			return;
		}
		Map<String, Object> map = data.get(0);
		Set<String> keys = map.keySet();
		HSSFRow row = null;
		HSSFCell cell = null;
		//表头
		row = sheet.createRow(0);
		List<String> head = new ArrayList<String>();
		int index = 0;
		for (String s : keys) {
			cell = row.createCell(index);
			cell.setCellValue(s);
			head.add(s);
			index ++;
		}
		for (int i = 1; i <= data.size(); i++) {
			row = sheet.createRow(i);
			index = 0;
			for (String s : head) {
				cell = row.createCell(index);
				Object o = data.get(i - 1).get(s);
				if (o == null) {
					cell.setCellType(cell.CELL_TYPE_STRING);
					cell.setCellValue("");
				} else if (o instanceof String) {
					String value = (String) o;
					try {
						Date d = sdf.parse(value);
//						cell.setCellType(cell.CELL_TYPE_FORMULA);
						cell.setCellValue(d);
					} catch (Exception e) {
						cell.setCellValue(value);
					}
				} else if (o instanceof Double) {
					Double value = (Double) o;
					cell.setCellType(cell.CELL_TYPE_NUMERIC);
					cell.setCellValue(value);
				} else if (o instanceof java.sql.Date) {
					java.sql.Date value = (java.sql.Date) o;
//					cell.setCellType(cell.CELL_TYPE_STRING);
					cell.setCellValue(new Date(value.getTime()));
				} else if (o instanceof Timestamp) {
					Timestamp value = (Timestamp) o;
//					cell.setCellType(cell.CELL_TYPE_STRING);
					cell.setCellValue(new Date(value.getTime()));
				} else if (o instanceof BigDecimal) {
					BigDecimal value = (BigDecimal) o;
					cell.setCellType(cell.CELL_TYPE_NUMERIC);
					cell.setCellValue(value.doubleValue());
				} else {
					String value = o.toString();
					cell.setCellType(cell.CELL_TYPE_STRING);
					cell.setCellValue(value);
				}
				index ++;
			}
		}
	}
	
	
	
	
	
}
