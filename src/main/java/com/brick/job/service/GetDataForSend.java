package com.brick.job.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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

public class GetDataForSend extends BaseService {
	
	private MailUtilService mailUtilService;
	
	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}

	public void doService() throws Exception{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		//paramMap.put("year", DateUtil.getCurrentYear());
		paramMap.put("year", "2013");
//		String filePath = "D:\\home\\filsoft\\financelease\\birtReport" + 
		String filePath = "\\\\"+LeaseUtil.getIPAddress()+"\\home\\filsoft\\financelease\\birtReport" + 
				File.separator + "捞数据" + File.separator + DateUtil.dateToString(new Date(), "yyyy_MM_dd_HH_mm_SSS");
		String fileName = "起租案件统计汇总.xls";
		OutputStream out = null;
		HSSFWorkbook wb = null;
		try {
			File path = new File(filePath);
			path.mkdirs();
			File f = new File(path, fileName);
			wb = new HSSFWorkbook();
			//sheet1
			HSSFSheet sheet1 = wb.createSheet("提案统计");
			List<Map<String, Object>> data = (List<Map<String, Object>>) this.queryForList("job.getDataForStartDateCollect", paramMap);
			this.drawSheet(sheet1, data, getHeadByHardcode1());
			out = new FileOutputStream(f);
			wb.write(out);
			//发送Email
			MailSettingTo mailSettingTo = new MailSettingTo();
			mailSettingTo.setEmailAttachPath(f.getPath());
			mailUtilService.sendMail(126, mailSettingTo);
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
	
	
	public void drawSheet(HSSFSheet sheet, List<Map<String, Object>> data, List<String> head){
		if (data == null || data.size() == 0) {
			return;
		}
		HSSFCell cell1 = null;
		HSSFCell cell2 = null;
		//表头
		HSSFRow row1 = sheet.createRow(0);
		HSSFRow row2 = sheet.createRow(1);
		int index = 0;
		for (String s : head) {
			cell1 = row1.createCell(index);
			cell1.setCellValue(s + "月");
			
			cell2 = row2.createCell(index);
			cell2.setCellValue("件数");
			cell2 = row2.createCell(index + 1);
			cell2.setCellValue("金额");
			cell2 = row2.createCell(index + 2);
			cell2.setCellValue("比例");
			/*head.add(s);*/
			index += 3;
		}
		HSSFRow row3 = sheet.createRow(2);
		index = 0;
		Double payMoney = 0D;
		Integer count = 0;
		for (String s : head) {
			payMoney = 0D;
			count = 0;
			for (Map<String, Object> m : data) {
				if (s.equals(String.valueOf(m.get("M")))) {
					payMoney = (Double) m.get("PAY_MONEY");
					count = (Integer) m.get("C");
					payMoney = payMoney == null ? 0 : payMoney;
					count = count == null ? 0 : count;
				}
			}
			cell1 = row3.createCell(index);
			cell1.setCellType(cell1.CELL_TYPE_NUMERIC);
			cell1.setCellValue(count);
			index ++;
			cell1 = row3.createCell(index);
			cell1.setCellType(cell1.CELL_TYPE_NUMERIC);
			cell1.setCellValue(payMoney);
			index += 2;
		}
	}
	
	public List<String> getHeadByHardcode1(){
		List<String> orderedHead = new ArrayList<String>();
		orderedHead.add("1");
		orderedHead.add("2");
		orderedHead.add("3");
		orderedHead.add("4");
		orderedHead.add("5");
		orderedHead.add("6");
		orderedHead.add("7");
		orderedHead.add("8");
		orderedHead.add("9");
		orderedHead.add("10");
		orderedHead.add("11");
		orderedHead.add("12");
		return orderedHead;
	}
	
}
