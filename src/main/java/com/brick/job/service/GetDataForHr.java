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

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.brick.base.service.BaseService;
import com.brick.base.to.ReportDateTo;
import com.brick.base.util.LeaseUtil;
import com.brick.base.util.ReportDateUtil;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.util.DateUtil;

public class GetDataForHr extends BaseService {
private MailUtilService mailUtilService;
	
	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}

	public void doService() throws Exception{
		//String filePath = "D:\\home\\filsoft\\financelease\\birtReport" + 
		String filePath = "\\\\"+LeaseUtil.getIPAddress()+"\\home\\filsoft\\financelease\\birtReport" + 
				File.separator + "待补-逾期" + File.separator + DateUtil.dateToString(new Date(), "yyyy_MM_dd");
		String fileName = "待补-逾期情况表.xls";
		OutputStream out = null;
		HSSFWorkbook wb = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			ReportDateTo reportDate = ReportDateUtil.getDateByDate(DateUtil.dateToStr(new Date()));
			if (reportDate.getMonth() == 1) {
				reportDate.setMonth((short) 12);
				reportDate.setYear(reportDate.getYear() - 1);
			} else {
				reportDate.setMonth((short) (reportDate.getMonth() - 1));
			}
			reportDate = ReportDateUtil.getDateByYearAndMonth(reportDate.getYear(), reportDate.getMonth());
			File path = new File(filePath);
			path.mkdirs();
			File f = new File(path, fileName);
			wb = new HSSFWorkbook();
			//sheet1
			HSSFSheet sheet1 = wb.createSheet("当月拨款案件待补情况");
			List<Map<String, Object>> data4Of1 = (List<Map<String, Object>>) this.queryForList("job.getDataForHr_lack" ,reportDate);
			this.drawSheet(sheet1, data4Of1, getHeadByHardcode1());
			//sheet2
			HSSFSheet sheet2 = wb.createSheet("当月新增逾期91天以上案件");
			List<Map<String, Object>> data4Of2 = (List<Map<String, Object>>) this.queryForList("job.getDataForHr_dun" ,reportDate);
			this.drawSheet(sheet2, data4Of2, getHeadByHardcode2());
			out = new FileOutputStream(f);
			wb.write(out);
			//发送Email
			MailSettingTo mailSettingTo = new MailSettingTo();
			mailSettingTo.setEmailAttachPath(f.getPath());
			mailUtilService.sendMail(132, mailSettingTo);
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
		orderedHead.add("客户名称");
		orderedHead.add("合同号");
		orderedHead.add("经办人");
		orderedHead.add("单位主管");
		orderedHead.add("办事处");
		orderedHead.add("拨款金额");
		orderedHead.add("拨款日期");
		orderedHead.add("发票");
		orderedHead.add("发票时间");
		orderedHead.add("相片");
		orderedHead.add("相片时间");
		orderedHead.add("待补");
		orderedHead.add("利差");
		orderedHead.add("租赁方式");
		return orderedHead;
	}
	
	public List<String> getHeadByHardcode2(){
		List<String> orderedHead = new ArrayList<String>();
		orderedHead.add("客户名称");
		orderedHead.add("合同号");
		orderedHead.add("经办人");
		orderedHead.add("单位主管");
		orderedHead.add("办事处");
		orderedHead.add("原始主管");
		orderedHead.add("原始办事处");
		orderedHead.add("拨款金额");
		orderedHead.add("拨款日期");
		orderedHead.add("首次逾期91天及以上日期");
		orderedHead.add("利差");
		orderedHead.add("总租金");
		orderedHead.add("剩余租金");
		orderedHead.add("租赁方式");
		return orderedHead;
	}
	
}
