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

public class GetDataForCommittedProject extends BaseService {
	
	private MailUtilService mailUtilService;
	
	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}

	public void doService() throws Exception{
		String dateStrForFileName = DateUtil.dateToString(new Date(), "yyyy_MM_dd_HH_mm_SSS");
		String fileName = "";
		//String today = "2014-7-1";
		String today = DateUtil.dateToString(new Date());
		fileName += generateFile(1, today, dateStrForFileName) + ";";
		fileName += generateFile(2, today, dateStrForFileName) + ";";
		fileName += generateFile(3, today, dateStrForFileName);
		//发送Email
		MailSettingTo mailSettingTo = new MailSettingTo();
		mailSettingTo.setEmailAttachPath(fileName);
		mailUtilService.sendMail(125, mailSettingTo);
	}
	
	public String generateFile(int productionCode, String today, String dateStrForFileName) throws Exception{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		ReportDateTo monthReportDateTo = ReportDateUtil.getDateByDate(today);
		paramMap.put("month_start_date", monthReportDateTo.getBeginTime());
		paramMap.put("month_end_date", monthReportDateTo.getEndTime());
		paramMap.put("productionCode", productionCode);
		String production = null;
		if (productionCode == 1) {
			production = "设备";
		} else if (productionCode == 2) {
			production = "商用车";
		} else if (productionCode == 3) {
			production = "乘用车";
		}
		//String filePath = "D:\\home\\filsoft\\financelease\\birtReport" + 
		String filePath = "\\\\"+LeaseUtil.getIPAddress()+"\\home\\filsoft\\financelease\\birtReport" + 
				File.separator + "捞数据" + File.separator + dateStrForFileName;
		String fileName = "每月访厂提案统计(" + production + ")" + monthReportDateTo.getMonth() + "月.xls";
		OutputStream out = null;
		HSSFWorkbook wb = null;
		try {
			File path = new File(filePath);
			path.mkdirs();
			File f = new File(path, fileName);
			wb = new HSSFWorkbook();
			//sheet1
			HSSFSheet sheet1 = wb.createSheet("提案统计");
			List<Map<String, Object>> data4Of1 = (List<Map<String, Object>>) this.queryForList("job.getDataForCommitted", paramMap);
			this.drawSheet(sheet1, data4Of1, getHeadByHardcode1());
			//sheet2
			HSSFSheet sheet2 = wb.createSheet("访厂未提案统计");
			List<Map<String, Object>> data4Of2 = (List<Map<String, Object>>) this.queryForList("job.getDataForVisit", paramMap);
			this.drawSheet(sheet2, data4Of2, getHeadByHardcode2());
			//sheet3
			HSSFSheet sheet3 = wb.createSheet("结案统计");
			List<Map<String, Object>> data4Of3 = (List<Map<String, Object>>) this.queryForList("job.getDataForFinish", paramMap);
			this.drawSheet(sheet3, data4Of3, getHeadByHardcode1());
			out = new FileOutputStream(f);
			wb.write(out);
			return f.getPath();
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
		orderedHead.add("案件号");
		orderedHead.add("办事处");
		orderedHead.add("承租人名称");
		orderedHead.add("供应商名称");
		orderedHead.add("租赁物名称");
		orderedHead.add("合同总价");
		orderedHead.add("融资金额");
		orderedHead.add("访厂人员");
		orderedHead.add("提案时间");
		orderedHead.add("结案时间");
		orderedHead.add("审查过案天数");
		orderedHead.add("提案次数");
		orderedHead.add("业务员");
		orderedHead.add("初级评审人");
		orderedHead.add("审批人");
		orderedHead.add("案件状态");
		orderedHead.add("租赁方式");
		orderedHead.add("专案");
		orderedHead.add("案件来源");
		orderedHead.add("评分");
		return orderedHead;
	}
	
	public List<String> getHeadByHardcode2(){
		List<String> orderedHead = new ArrayList<String>();
		orderedHead.add("案件号");
		orderedHead.add("办事处");
		orderedHead.add("承租人名称");
		orderedHead.add("供应商名称");
		orderedHead.add("租赁物名称");
		orderedHead.add("合同总价");
		orderedHead.add("融资金额");
		orderedHead.add("业务员");
		orderedHead.add("访厂人员");
		orderedHead.add("访厂日");
		orderedHead.add("案件状态");
		orderedHead.add("租赁方式");
		orderedHead.add("专案");
		orderedHead.add("案件来源");
		return orderedHead;
	}
	
	
}
