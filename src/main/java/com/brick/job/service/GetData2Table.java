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

public class GetData2Table extends BaseService {
	
	private MailUtilService mailUtilService;
	
	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}

	public void doService() throws Exception{
		//判断是否是当月第二个工作日
		if (!isTheSecondWorkingDay()) {
			System.out.println("非第二个工作日！");
			return;
		}
		//String filePath = "D:\\home\\filsoft\\financelease\\birtReport" + 
		String filePath = "\\\\"+LeaseUtil.getIPAddress()+"\\home\\filsoft\\financelease\\birtReport" + 
				File.separator + "捞数据" + File.separator + DateUtil.dateToString(new Date(), "yyyy_MM_dd");
		String fileName = "捞数据2表格.xls";
		OutputStream out = null;
		HSSFWorkbook wb = null;
		try {
			File path = new File(filePath);
			path.mkdirs();
			File f = new File(path, fileName);
			wb = new HSSFWorkbook();
			//sheet1
			HSSFSheet sheet1 = wb.createSheet("sheet1");
			List<Map<String, Object>> data4Of1 = (List<Map<String, Object>>) this.queryForList("job.getData2Of1");
			this.drawSheet(sheet1, data4Of1, getHeadByHardcode1());
			//sheet2
			HSSFSheet sheet2 = wb.createSheet("sheet2");
			List<Map<String, Object>> data4Of2 = (List<Map<String, Object>>) this.queryForList("job.getData2Of2");
			this.drawSheet(sheet2, data4Of2, getHeadByHardcode2());
			out = new FileOutputStream(f);
			wb.write(out);
			//发送Email
			MailSettingTo mailSettingTo = new MailSettingTo();
			mailSettingTo.setEmailAttachPath(f.getPath());
			mailUtilService.sendMail(131, mailSettingTo);
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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (data == null || data.size() == 0) {
			return;
		}
		/*Map<String, Object> map = data.get(0);
		Set<String> keys = map.keySet();*/
		HSSFRow row = null;
		HSSFCell cell = null;
		//表头
		row = sheet.createRow(0);
		/*List<String> head = new ArrayList<String>();*/
		int index = 0;
		for (String s : head) {
			cell = row.createCell(index);
			cell.setCellValue(s);
			/*head.add(s);*/
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
	
	public List<String> getHeadByHardcode1(){
		List<String> orderedHead = new ArrayList<String>();
		orderedHead.add("批覆书号");
		orderedHead.add("合同号");
		orderedHead.add("承租人");
		orderedHead.add("单位");
		orderedHead.add("交机前拨款比例");
		orderedHead.add("交机前拨款金额");
		orderedHead.add("交机后拨款比例");
		orderedHead.add("交机后拨款金额");
		orderedHead.add("拨款日期");
		orderedHead.add("产品名称");
		orderedHead.add("第一类");
		orderedHead.add("第二类");
		orderedHead.add("厂牌");
		orderedHead.add("规格型号");
		orderedHead.add("数量");
		orderedHead.add("单价");
		orderedHead.add("供应商");
		orderedHead.add("制造商");
		orderedHead.add("锁码方式");
		orderedHead.add("供应商级别");
		orderedHead.add("专案");
		return orderedHead;
	}
	
	public List<String> getHeadByHardcode2(){
		List<String> orderedHead = new ArrayList<String>();
		orderedHead.add("批覆书号");
		orderedHead.add("合同号");
		orderedHead.add("承租人");
		orderedHead.add("经办人");
		orderedHead.add("单位");
		orderedHead.add("设备总价");
		orderedHead.add("保证金");
		orderedHead.add("TR");
		orderedHead.add("利差现值");
		orderedHead.add("授信净额");
		orderedHead.add("拨款日期");
		orderedHead.add("起租日期");
		orderedHead.add("租金总额");
		orderedHead.add("最末期租金缴付日");
		orderedHead.add("缴付周期");
		orderedHead.add("期数");
		orderedHead.add("交付日");
		orderedHead.add("案件状态");
		orderedHead.add("租金余额");
		orderedHead.add("逾期天数");
		orderedHead.add("专案");
		return orderedHead;
	}
	
	
}
