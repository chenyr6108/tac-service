package com.brick.report.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.math.BigDecimal;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import jxl.write.Label;



import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.DataUtil;
import com.brick.util.DateUtil;
import com.brick.util.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.tools.zip.ZipEntry;

import com.brick.base.command.BaseCommand;
import com.brick.base.service.BaseService;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.base.to.ReportDateTo;
import com.brick.base.util.LeaseUtil;
import com.brick.base.util.ReportDateUtil;
import com.brick.log.service.LogPrint;
import com.brick.moneyRate.service.MoneyRateService;

public class InsuranceService extends BaseCommand {
	Log logger = LogFactory.getLog(InsuranceService.class);
	/**
	 * 保险费
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void insuranceReport(Context context){
		Map outputMap = new HashMap();
		List errlist = context.errList;
		DataWrap dw = null;
		if(errlist.isEmpty()){
			try {
				MoneyRateService.queryMoneyRate(context) ;//查询利率（动态）
				if(context.contextMap.get("startDate") == null){
					SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
					context.contextMap.put("startDate", sf.format(new Date())) ;
				}else {
					context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
				}
				outputMap.put("startDate", new SimpleDateFormat("yyyy-MM-dd").parse(context.contextMap.get("startDate").toString())) ;				
//				context.contextMap.put("INSURE_BASE_RATE", ((Double)context.contextMap.get("INSURE_BASE_RATE") / 100) ); //百分比
				dw = (DataWrap) DataAccessor.query("priceReport.queryInsurance", context.contextMap, DataAccessor.RS_TYPE.PAGED);
			} catch (Exception e) {
				LogPrint.getLogStackTrace(e, logger);
				errlist.add(e);
			}
		}
		if(errlist.isEmpty()){
			if(errlist.isEmpty()){
				outputMap.put("dw", dw);
				outputMap.put("content", context.contextMap.get("content"));
//				outputMap.put("startDate", context.contextMap.get("startDate")) ;
//				outputMap.put("endDate", context.contextMap.get("endDate")) ;
				Output.jspOutput(outputMap, context, "/report/queryInsurance.jsp");
			}else{
				outputMap.put("errList", errlist);
				Output.jspOutput(outputMap, context, "/error.jsp");
			}
		}
		
	}
	
	/**
	 * 导出 excel
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void expToExcel(Context context){  
		List<Map> insuranceList=null;
		try {
			MoneyRateService.queryMoneyRate(context) ;//查询利率（动态）
			if(context.contextMap.get("startDate") == null){
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
				context.contextMap.put("startDate", sf.format(new Date())) ;
			}else {
				context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
			}			
//			context.contextMap.put("INSURE_BASE_RATE", ((Double)context.contextMap.get("INSURE_BASE_RATE") / 100) ); //百分比
			insuranceList=(List<Map>)DataAccessor.query("priceReport.queryInsurance", context.contextMap, DataAccessor.RS_TYPE.LIST);
//			insuranceList=(List<Map>)DataAccessor.query("priceReport.queryInsuranceToExport", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e1) {
			e1.printStackTrace();
			LogPrint.getLogStackTrace(e1, logger);
		}			
		ByteArrayOutputStream baos = null;
		String strFileName = "保险明细表("+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+").xls";
		
		InsuranceUtil insuranceUtil = new InsuranceUtil();
		insuranceUtil.createexl();
		baos = insuranceUtil.exportExcel(insuranceList);
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");		
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
			ServletOutputStream out1 = context.response.getOutputStream();
			insuranceUtil.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

	}
	
	/**
	 * 印花税
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void stampReport(Context context){
		Map outputMap = new HashMap();
		List errlist = context.errList;
		DataWrap dw = null;
		if(errlist.isEmpty()){
			try {
				//MoneyRateService.queryMoneyRate(context) ;//查询利率（动态）
				if(context.contextMap.get("startDate") == null){
					SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
					context.contextMap.put("startDate", sf.format(new Date())) ;
				}else {
					context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
				}
				outputMap.put("startDate", new SimpleDateFormat("yyyy-MM-dd").parse(context.contextMap.get("startDate").toString())) ;
				
				int year=Integer.valueOf(context.contextMap.get("startDate").toString().split("-")[0]);
				int month=Integer.valueOf(context.contextMap.get("startDate").toString().split("-")[1]);
				ReportDateTo to=ReportDateUtil.getDateByYearAndMonth(year,month);
				context.contextMap.put("startTime",to.getBeginTime());
				context.contextMap.put("endTime",to.getEndTime());
				
//				context.contextMap.put("STAMP_TAX_MONTHPRIC", ((Double)context.contextMap.get("STAMP_TAX_MONTHPRIC") / 100) ); //百分比
//				context.contextMap.put("STAMP_TAX_TOPRIC", ((Double)context.contextMap.get("STAMP_TAX_TOPRIC") / 100) ); //百分比
//				context.contextMap.put("STAMP_TAX_INSUREPRIC", ((Double)context.contextMap.get("STAMP_TAX_INSUREPRIC") / 100) ); //百分比
				dw = (DataWrap) DataAccessor.query("priceReport.queryStamp", context.contextMap, DataAccessor.RS_TYPE.PAGED);
			} catch (Exception e) {
				LogPrint.getLogStackTrace(e, logger);
				errlist.add(e);
			}
		}
		if(errlist.isEmpty()){
			if(errlist.isEmpty()){
				outputMap.put("dw", dw);
				outputMap.put("content", context.contextMap.get("content"));
//				outputMap.put("STAMP_TAX_MONTHPRIC", (Double)context.contextMap.get("STAMP_TAX_MONTHPRIC") * 1000) ;
//				outputMap.put("STAMP_TAX_TOPRIC", (Double)context.contextMap.get("STAMP_TAX_TOPRIC") * 1000) ;
				//Modify by Michael 2012 3-5 将印花税拆分成合同印花税和保险印花税
				//outputMap.put("STAMP_TAX_INSUREPRIC", (Double)context.contextMap.get("STAMP_TAX_INSUREPRIC") * 1000) ;
				//outputMap.put("startDate", context.contextMap.get("startDate")) ;
				//outputMap.put("endDate", context.contextMap.get("endDate")) ;
				outputMap.put("companyCode", context.contextMap.get("companyCode"));
				outputMap.put("companys", LeaseUtil.getCompanys());
				Output.jspOutput(outputMap, context, "/report/queryStamp.jsp");
			}else{
				outputMap.put("errList", errlist);
				Output.jspOutput(outputMap, context, "/error.jsp");
			}
		}
		
	}
	
	/**
	 * 导出 excel
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void expStampToExcel(Context context){  
		List<Map> stampList=null;
		Map content = new HashMap() ;
		try {
			//MoneyRateService.queryMoneyRate(context) ;//查询利率（动态）
			if(context.contextMap.get("startDate") == null){
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
				context.contextMap.put("startDate", sf.format(new Date())) ;
			}else {
				context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
			}
			int year=Integer.valueOf(context.contextMap.get("startDate").toString().split("-")[0]);
			int month=Integer.valueOf(context.contextMap.get("startDate").toString().split("-")[1]);
			ReportDateTo to=ReportDateUtil.getDateByYearAndMonth(year,month);
			context.contextMap.put("startTime",to.getBeginTime());
			context.contextMap.put("endTime",to.getEndTime());
			
//			context.contextMap.put("STAMP_TAX_MONTHPRIC", ((Double)context.contextMap.get("STAMP_TAX_MONTHPRIC") / 100) ); //百分比
//			context.contextMap.put("STAMP_TAX_TOPRIC", ((Double)context.contextMap.get("STAMP_TAX_TOPRIC") / 100) ); //百分比
//			context.contextMap.put("STAMP_TAX_INSUREPRIC", ((Double)context.contextMap.get("STAMP_TAX_INSUREPRIC") / 100) ); //百分比
			stampList=(List<Map>)  DataAccessor.query("priceReport.queryStamp", context.contextMap, DataAccessor.RS_TYPE.LIST) ;
//			stampList=(List<Map>)DataAccessor.query("priceReport.queryStampToExport", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e1) {
			e1.printStackTrace();
			LogPrint.getLogStackTrace(e1, logger);
		}			
		ByteArrayOutputStream baos = null;
		String strFileName = "合同印花税明细表("+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+").xls";
		
		InsuranceUtil insuranceUtil = new InsuranceUtil();
		insuranceUtil.createexl();
		
		content.put("stampList", stampList) ;
//		content.put("STAMP_TAX_MONTHPRIC", (Double)context.contextMap.get("STAMP_TAX_MONTHPRIC") * 1000) ;
//		content.put("STAMP_TAX_TOPRIC", (Double)context.contextMap.get("STAMP_TAX_TOPRIC") * 1000) ;
//		content.put("STAMP_TAX_INSUREPRIC", (Double)context.contextMap.get("STAMP_TAX_INSUREPRIC") * 1000) ;
		baos = insuranceUtil.exportStampExcel(content);
		//需要利率换成Map
//		baos = insuranceUtil.exportStampExcel(stampList);
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");		
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
			ServletOutputStream out1 = context.response.getOutputStream();
			insuranceUtil.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		
	}

	
	/**
	 * Add by Michael 2012-3-5
	 * 保险印花税
	 * @param context
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public void insuranceStampReport(Context context) throws Exception{
		Map outputMap = new HashMap();
		PagingInfo<Object> dw = null;
		String date = (String) context.contextMap.get("date");
		String year = null;
		String month = null;
		try {
			if (StringUtils.isEmpty(date)) {
				date = DateUtil.getCurrentYearMonth();
			}
			String[] dateInfo = date.split("-");
			if (dateInfo.length != 2) {
				year = DateUtil.getCurrentYear();
				month = DateUtil.getCurrentMonth();
			} else {
				year = dateInfo[0];
				month = dateInfo[1];
			}
			ReportDateTo reportDate = ReportDateUtil.getDateByYearAndMonth(Integer.parseInt(year), Integer.parseInt(month));
			context.contextMap.put("start_date", reportDate.getBeginTime());
			context.contextMap.put("end_date", reportDate.getEndTime());

			dw = baseService.queryForListWithPaging("priceReport.queryInsuranceStamp", context.contextMap, "PAY_DATE");
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
		outputMap.put("dw", dw);
		outputMap.put("date", date);
		outputMap.put("companyCode", context.contextMap.get("companyCode"));
		outputMap.put("companys", LeaseUtil.getCompanys());	
		Output.jspOutput(outputMap, context, "/report/queryInsuranceStamp.jsp");
	}
	
	/**
	 *  Add by Michael 2012-3-5
	 * 导出 保险印花税excel
	 * @param context
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public void expInsuranceStampToExcel(Context context) throws Exception{  
		List<Map<String, Object>> stampList=null;
		Map<String, Object> content = new HashMap<String, Object>() ;
		String date = (String) context.contextMap.get("date");
		String year = null;
		String month = null;
		OutputStream out = null;
		HSSFWorkbook wb = null;
		try {
			if (StringUtils.isEmpty(date)) {
				date = DateUtil.getCurrentYearMonth();
			}
			String[] dateInfo = date.split("-");
			if (dateInfo.length != 2) {
				year = DateUtil.getCurrentYear();
				month = DateUtil.getCurrentMonth();
			} else {
				year = dateInfo[0];
				month = dateInfo[1];
			}
			ReportDateTo reportDate = ReportDateUtil.getDateByYearAndMonth(Integer.parseInt(year), Integer.parseInt(month));
			context.contextMap.put("start_date", reportDate.getBeginTime());
			context.contextMap.put("end_date", reportDate.getEndTime());
			stampList = (List<Map<String, Object>>) baseService.queryForList("priceReport.queryInsuranceStamp", context.contextMap) ;
			
			String strFileName = "保险合同印花税明细表("+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+").xls";
			wb = new HSSFWorkbook();
			HSSFSheet sheet1 = wb.createSheet("sheet1");
			this.drawSheet(sheet1, stampList, getHeadByHardcode());
			
			context.response.setContentType("application/vnd.ms-excel;charset=GB2312");		
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
			out = context.response.getOutputStream();
			wb.write(out);
			out.flush();
		} catch (Exception e) {			 
			logger.error(e);
			throw e;
		} finally {
			if (out != null) {
				out.close();
			}
		}

	}	
	
	public void drawSheet(HSSFSheet sheet, List<Map<String, Object>> data, List<Map<String, String>> head){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (data == null || data.size() == 0) {
			return;
		}
		HSSFRow row = null;
		HSSFCell cell = null;
		//表头
		row = sheet.createRow(0);
		int index = 0;
		for (Map<String, String> m : head) {
			cell = row.createCell(index);
			cell.setCellValue(m.get("desc"));
			index ++;
		}
		for (int i = 1; i <= data.size(); i++) {
			row = sheet.createRow(i);
			index = 0;
			for (Map<String, String> m : head) {
				cell = row.createCell(index);
				if ("index".equals(m.get("key"))) {
					cell.setCellValue(i);
					index++;
					continue;
				}
				Object o = data.get(i - 1).get(m.get("key"));
				if (o == null) {
					cell.setCellType(cell.CELL_TYPE_STRING);
					cell.setCellValue("");
				} else if (o instanceof String) {
					String value = (String) o;
					try {
						Date d = sdf.parse(value);
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
					cell.setCellValue(DateUtil.dateToStr(value));
				} else if (o instanceof Timestamp) {
					Timestamp value = (Timestamp) o;
					cell.setCellValue(DateUtil.dateToStr(value));
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
	
	public List<Map<String, String>> getHeadByHardcode(){
		List<Map<String, String>> orderedHead = new ArrayList<Map<String, String>>();
		Map<String, String> m = new HashMap<String, String>();
		m.put("key", "index");
		m.put("desc", "序号");
		orderedHead.add(m);
		m = new HashMap<String, String>();
		m.put("key", "PAY_DATE");
		m.put("desc", "拨款日期");
		orderedHead.add(m);
		m = new HashMap<String, String>();
		m.put("key", "CREDIT_RUNCODE");
		m.put("desc", "案件号");
		orderedHead.add(m);
		m = new HashMap<String, String>();
		m.put("key", "CUST_NAME");
		m.put("desc", "客户名称");
		orderedHead.add(m);
		m = new HashMap<String, String>();
		m.put("key", "LEASE_CODE");
		m.put("desc", "合同号");
		orderedHead.add(m);
		m = new HashMap<String, String>();
		m.put("key", "FIRST_PAYDATE");
		m.put("desc", "起租日期");
		orderedHead.add(m);
		m = new HashMap<String, String>();
		m.put("key", "LEASE_TERM");
		m.put("desc", "期数");
		orderedHead.add(m);
		m = new HashMap<String, String>();
		m.put("key", "NAME");
		m.put("desc", "业务员");
		orderedHead.add(m);
		m = new HashMap<String, String>();
		m.put("key", "DECP_NAME_CN");
		m.put("desc", "办事处");
		orderedHead.add(m);
		m = new HashMap<String, String>();
		m.put("key", "SHORT_NAME");
		m.put("desc", "保险公司");
		orderedHead.add(m);
		m = new HashMap<String, String>();
		m.put("key", "INSU_PRICE");
		m.put("desc", "计税金额");
		orderedHead.add(m);
		m = new HashMap<String, String>();
		m.put("key", "TAX");
		m.put("desc", "印花税");
		orderedHead.add(m);
		return orderedHead;
	}
	
	/**
	 * 营业税
	 * @param context
	 */
	public void businessTaxReport(Context context){
		Map outputMap = new HashMap();
		List errlist = context.errList;
		DataWrap dw = null;
		String date="";
		if(errlist.isEmpty()){
			try {
//				if(context.contextMap.get("date")!=null){
//					date=(String)context.contextMap.get("date");			
////				}else{
////					DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
////					date=format.format(new Date());
//				}
//				context.contextMap.put("date", date);
				
//				MoneyRateService.queryMoneyRate(context) ;//查询利率（动态）
				if(context.contextMap.get("startDate") == null){
					SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
					context.contextMap.put("startDate", sf.format(new Date())) ;
				}else {
					context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
				}
				outputMap.put("startDate", new SimpleDateFormat("yyyy-MM-dd").parse(context.contextMap.get("startDate").toString())) ;	
				
				dw = (DataWrap) DataAccessor.query("priceReport.queryBusinessTax", context.contextMap, DataAccessor.RS_TYPE.PAGED);
			} catch (Exception e) {
				LogPrint.getLogStackTrace(e, logger);
				errlist.add(e);
			}
		}
		if(errlist.isEmpty()){
			if(errlist.isEmpty()){
				outputMap.put("dw", dw);
				outputMap.put("content", context.contextMap.get("content"));
				Output.jspOutput(outputMap, context, "/report/queryBusinessTax.jsp");
			}else{
				outputMap.put("errList", errlist);
				Output.jspOutput(outputMap, context, "/error.jsp");
			}
		}
		
	}
		
	/**
	 * 导出 excel
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void expBusinessTaxToExcel(Context context){  
		List<Map> businessTaxList=null;
		try {
			//MoneyRateService.queryMoneyRate(context) ;//查询利率（动态）
			//Modify by Michael 2012 02-28  导出excel时 带出所以资料
			if(context.contextMap.get("startDate") == null){
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
				context.contextMap.put("startDate", sf.format(new Date())) ;
			}else {
				context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
			}
			businessTaxList=(List<Map>) DataAccessor.query("priceReport.queryBusinessTax", context.contextMap, DataAccessor.RS_TYPE.LIST);
//			businessTaxList=(List<Map>)DataAccessor.query("priceReport.queryBusinessTaxToExport", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e1) {
			e1.printStackTrace();
			LogPrint.getLogStackTrace(e1, logger);
		}			
		ByteArrayOutputStream baos = null;
		String strFileName = "营业税明细表("+DataUtil.StringUtil(context.contextMap.get("date"))+").xls";
		
		InsuranceUtil insuranceUtil = new InsuranceUtil();
		insuranceUtil.createexl();
		baos = insuranceUtil.exportBusinessTaxExcel(businessTaxList);
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");		
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
			ServletOutputStream out1 = context.response.getOutputStream();
			insuranceUtil.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

	}
	
	/**
	 * 保险费余额变动表
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void insuranceDynamicReport(Context context){
		Map outputMap = new HashMap();
		List errlist = context.errList;
		DataWrap dw = null;
		if(errlist.isEmpty()){
			try {
				//MoneyRateService.queryMoneyRate(context) ;//查询利率（动态）
				if(context.contextMap.get("startDate") == null){
					SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
					context.contextMap.put("startDate", sf.format(new Date())) ;
				}else {
					context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
				}
				outputMap.put("startDate", new SimpleDateFormat("yyyy-MM-dd").parse(context.contextMap.get("startDate").toString())) ;				
				dw = (DataWrap) DataAccessor.query("priceReport.queryInsuranceDynamic", context.contextMap, DataAccessor.RS_TYPE.PAGED);
			} catch (Exception e) {
				LogPrint.getLogStackTrace(e, logger);
				errlist.add(e);
			}
		}
		if(errlist.isEmpty()){
			if(errlist.isEmpty()){
				outputMap.put("dw", dw);
				outputMap.put("content", context.contextMap.get("content"));
				outputMap.put("companyCode", context.contextMap.get("companyCode"));
				outputMap.put("companys", LeaseUtil.getCompanys());	
				Output.jspOutput(outputMap, context, "/report/queryInsuranceDynamic.jsp");
			}else{
				outputMap.put("errList", errlist);
				Output.jspOutput(outputMap, context, "/error.jsp");
			}
		}
		
	}	
	
	/**
	 * 导出 excel
	 * @param context
	 */
	//Add by Michael 2012-3-22 保险费余额变动表
	@SuppressWarnings("unchecked")
	public void expInsuranceDynamicToExcel(Context context){  
		List<Map> insuranceDynamic=null;

		try {
			if(context.contextMap.get("startDate") == null){
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
				context.contextMap.put("startDate", sf.format(new Date())) ;
			}else {
				context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
			}

			insuranceDynamic=(List<Map>) DataAccessor.query("priceReport.queryInsuranceDynamic", context.contextMap, DataAccessor.RS_TYPE.LIST);

		} catch (Exception e1) {
			e1.printStackTrace();
			LogPrint.getLogStackTrace(e1, logger);
		}			
		ByteArrayOutputStream baos = null;
		String strFileName = "保险费余额变动表.xls";
		
		InsuranceUtil insuranceUtil = new InsuranceUtil();
		insuranceUtil.createexl();
		baos = insuranceUtil.exportInsuranceDynamicExcel(insuranceDynamic);
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");		
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
			ServletOutputStream out1 = context.response.getOutputStream();
			insuranceUtil.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

	}	

	/*
	 * Add by Michael 2012 4-5
	 * 增加留购款、罚息营业税  For 营业税
	 */
	public void stayBuyPriceTaxReport(Context context){
		Map outputMap = new HashMap();
		List errlist = context.errList;
		PagingInfo<Object> dw = null;
		String date="";
		if(errlist.isEmpty()){
			try {
				if(context.contextMap.get("startDate") == null){
					SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
					context.contextMap.put("startDate", sf.format(new Date())) ;
				}else {
					context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
				}
				
				int year=Integer.valueOf(context.contextMap.get("startDate").toString().split("-")[0]);
				int month=Integer.valueOf(context.contextMap.get("startDate").toString().split("-")[1]);
				ReportDateTo to=ReportDateUtil.getDateByYearAndMonth(year,month);
				context.contextMap.put("startTime",to.getBeginTime());
				context.contextMap.put("endTime",to.getEndTime());
				if(month<10) {
					context.contextMap.put("FINANCE_DATE",context.contextMap.get("startDate").toString().split("-")[0]+"-"+context.contextMap.get("startDate").toString().split("-")[1].substring(1));
				} else {
					context.contextMap.put("FINANCE_DATE",context.contextMap.get("startDate").toString().split("-")[0]+"-"+context.contextMap.get("startDate").toString().split("-")[1]);
				}
				
				outputMap.put("startDate", new SimpleDateFormat("yyyy-MM-dd").parse(context.contextMap.get("startDate").toString())) ;	
				
				dw = baseService.queryForListWithPaging("priceReport.queryStayBuyPriceTax", context.contextMap,"LEASE_CODE", ORDER_TYPE.DESC);
			} catch (Exception e) {
				LogPrint.getLogStackTrace(e, logger);
				errlist.add(e);
			}
		}
		if(errlist.isEmpty()){
			if(errlist.isEmpty()){
				outputMap.put("dw", dw);
				outputMap.put("content", context.contextMap.get("content"));
				Output.jspOutput(outputMap, context, "/report/queryStayBuyPrice.jsp");
			}else{
				outputMap.put("errList", errlist);
				Output.jspOutput(outputMap, context, "/error.jsp");
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void stayBuyPriceTaxToExcel(Context context){  
		List<Map> businessTaxList=null;
		try {
			//MoneyRateService.queryMoneyRate(context) ;//查询利率（动态）
			if(context.contextMap.get("startDate") == null){
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
				context.contextMap.put("startDate", sf.format(new Date())) ;
			}else {
				context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
			}
			
			int year=Integer.valueOf(context.contextMap.get("startDate").toString().split("-")[0]);
			int month=Integer.valueOf(context.contextMap.get("startDate").toString().split("-")[1]);
			ReportDateTo to=ReportDateUtil.getDateByYearAndMonth(year,month);
			context.contextMap.put("startTime",to.getBeginTime());
			context.contextMap.put("endTime",to.getEndTime());
			if(month<10) {
				context.contextMap.put("FINANCE_DATE",context.contextMap.get("startDate").toString().split("-")[0]+"-"+context.contextMap.get("startDate").toString().split("-")[1].substring(1));
			} else {
				context.contextMap.put("FINANCE_DATE",context.contextMap.get("startDate").toString().split("-")[0]+"-"+context.contextMap.get("startDate").toString().split("-")[1]);
			}
			businessTaxList=(List<Map>) DataAccessor.query("priceReport.queryStayBuyPriceTax", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e1) {
			e1.printStackTrace();
			LogPrint.getLogStackTrace(e1, logger);
		}			
		ByteArrayOutputStream baos = null;
		String strFileName = "留购款营业税明细表("+DataUtil.StringUtil(context.contextMap.get("startDate"))+").xls";
		
		InsuranceUtil insuranceUtil = new InsuranceUtil();
		insuranceUtil.createexl();
		baos = insuranceUtil.stayBuyPriceTaxToExcel(businessTaxList);
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");		
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
			ServletOutputStream out1 = context.response.getOutputStream();
			insuranceUtil.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

	}	
	
	public void stayBuyPriceTaxReporForValueAdd(Context context){
		Map outputMap = new HashMap();
		List errlist = context.errList;
		PagingInfo<Object> dw = null;
		String date="";
		if(errlist.isEmpty()){
			try {
				if(context.contextMap.get("startDate") == null){
					SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
					context.contextMap.put("startDate", sf.format(new Date())) ;
				}else {
					context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
				}
				
				int year=Integer.valueOf(context.contextMap.get("startDate").toString().split("-")[0]);
				int month=Integer.valueOf(context.contextMap.get("startDate").toString().split("-")[1]);
				ReportDateTo to=ReportDateUtil.getDateByYearAndMonth(year,month);
				context.contextMap.put("startTime",to.getBeginTime());
				context.contextMap.put("endTime",to.getEndTime());
				
				if(month<10) {
					context.contextMap.put("FINANCE_DATE",context.contextMap.get("startDate").toString().split("-")[0]+"-"+context.contextMap.get("startDate").toString().split("-")[1].substring(1));
				} else {
					context.contextMap.put("FINANCE_DATE",context.contextMap.get("startDate").toString().split("-")[0]+"-"+context.contextMap.get("startDate").toString().split("-")[1]);
				}
				
				outputMap.put("startDate", new SimpleDateFormat("yyyy-MM-dd").parse(context.contextMap.get("startDate").toString())) ;	
				
				dw = baseService.queryForListWithPaging("priceReport.queryStayBuyPriceValueAddTax", context.contextMap,"LEASE_CODE", ORDER_TYPE.DESC);
			} catch (Exception e) {
				LogPrint.getLogStackTrace(e, logger);
				errlist.add(e);
			}
		}
		if(errlist.isEmpty()){
			if(errlist.isEmpty()){
				outputMap.put("dw", dw);
				outputMap.put("content", context.contextMap.get("content"));
				outputMap.put("companyCode", context.contextMap.get("companyCode"));
				outputMap.put("companys", LeaseUtil.getCompanys());	
				Output.jspOutput(outputMap, context, "/report/queryStayBuyPriceValueAdd.jsp");
			}else{
				outputMap.put("errList", errlist);
				Output.jspOutput(outputMap, context, "/error.jsp");
			}
		}
	}

	public void stayBuyPriceTaxToExcelForValueAdd(Context context){  
		List<Map> businessTaxList=null;
		try {
			//MoneyRateService.queryMoneyRate(context) ;//查询利率（动态）
			if(context.contextMap.get("startDate") == null){
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
				context.contextMap.put("startDate", sf.format(new Date())) ;
			}else {
				context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
			}
			
			int year=Integer.valueOf(context.contextMap.get("startDate").toString().split("-")[0]);
			int month=Integer.valueOf(context.contextMap.get("startDate").toString().split("-")[1]);
			ReportDateTo to=ReportDateUtil.getDateByYearAndMonth(year,month);
			context.contextMap.put("startTime",to.getBeginTime());
			context.contextMap.put("endTime",to.getEndTime());
			
			if(month<10) {
				context.contextMap.put("FINANCE_DATE",context.contextMap.get("startDate").toString().split("-")[0]+"-"+context.contextMap.get("startDate").toString().split("-")[1].substring(1));
			} else {
				context.contextMap.put("FINANCE_DATE",context.contextMap.get("startDate").toString().split("-")[0]+"-"+context.contextMap.get("startDate").toString().split("-")[1]);
			}
			
			businessTaxList=(List<Map>) DataAccessor.query("priceReport.queryStayBuyPriceValueAddTax", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e1) {
			e1.printStackTrace();
			LogPrint.getLogStackTrace(e1, logger);
		}			
		ByteArrayOutputStream baos = null;
		String strFileName = "留购款增值税明细表("+DataUtil.StringUtil(context.contextMap.get("startDate"))+").xls";
		
		InsuranceUtil insuranceUtil = new InsuranceUtil();
		insuranceUtil.createexl();
		baos = insuranceUtil.stayBuyPriceTaxToExcelValueAdd(businessTaxList);
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");		
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
			ServletOutputStream out1 = context.response.getOutputStream();
			insuranceUtil.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

	}	
	
	//Add by Michael 2012 12-07 增加管理费收入
	public void managePriceTaxReporForValueAdd(Context context){
		Map outputMap = new HashMap();
		List errlist = context.errList;
		PagingInfo<Object> dw = null;
		
		if(errlist.isEmpty()){
			try {
				if(context.contextMap.get("startDate") == null){
					SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
					context.contextMap.put("startDate", sf.format(new Date())) ;
				}else {
					context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
				}
				outputMap.put("startDate", new SimpleDateFormat("yyyy-MM-dd").parse(context.contextMap.get("startDate").toString())) ;	
				
				String date=context.contextMap.get("startDate").toString();
				
				int year=Integer.valueOf(date.split("-")[0]);
				int month=Integer.valueOf(date.split("-")[1]);
				
				ReportDateTo to=ReportDateUtil.getDateByYearAndMonth(year,month);
				context.contextMap.put("year",to.getYear());
				context.contextMap.put("beginTime",to.getBeginTime());
				context.contextMap.put("endTime",to.getEndTime());
				dw = baseService.queryForListWithPaging("priceReport.queryManageFeeValueTax", context.contextMap,"LEASE_CODE", ORDER_TYPE.DESC);
			} catch (Exception e) {
				LogPrint.getLogStackTrace(e, logger);
				errlist.add(e);
			}
		}
		if(errlist.isEmpty()){
			if(errlist.isEmpty()){
				outputMap.put("dw", dw);
				outputMap.put("content", context.contextMap.get("content"));
				outputMap.put("companyCode", context.contextMap.get("companyCode"));
				outputMap.put("companys", LeaseUtil.getCompanys());	

				Output.jspOutput(outputMap, context, "/report/queryManagePriceValueAdd.jsp");
			}else{
				outputMap.put("errList", errlist);
				Output.jspOutput(outputMap, context, "/error.jsp");
			}
		}
	}

	public void managePriceTaxToExcelForValueAdd(Context context){  
		List<Map> businessTaxList=null;
		try {
			//MoneyRateService.queryMoneyRate(context) ;//查询利率（动态）
			if(context.contextMap.get("startDate") == null){
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
				context.contextMap.put("startDate", sf.format(new Date())) ;
			}else {
				context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
			}
			String date=context.contextMap.get("startDate").toString();
			
			int year=Integer.valueOf(date.split("-")[0]);
			int month=Integer.valueOf(date.split("-")[1]);
			
			ReportDateTo to=ReportDateUtil.getDateByYearAndMonth(year,month);
			context.contextMap.put("year",to.getYear());
			context.contextMap.put("beginTime",to.getBeginTime());
			context.contextMap.put("endTime",to.getEndTime());
			businessTaxList=(List<Map>) DataAccessor.query("priceReport.queryManageFeeValueTax", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e1) {
			e1.printStackTrace();
			LogPrint.getLogStackTrace(e1, logger);
		}			
		ByteArrayOutputStream baos = null;
		String strFileName = "管理费收入增值税明细表("+DataUtil.StringUtil(context.contextMap.get("startDate"))+").xls";
		
		InsuranceUtil insuranceUtil = new InsuranceUtil();
		insuranceUtil.createexl();
		baos = insuranceUtil.managePriceTaxToExcelValueAdd(businessTaxList);
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");		
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
			ServletOutputStream out1 = context.response.getOutputStream();
			insuranceUtil.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

	}	
	
	public void managePriceTaxCustInfoToExcelForValueAdd(Context context){  
		List<Map> businessTaxList=null;
		try {
			if(context.contextMap.get("startDate") == null){
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
				context.contextMap.put("startDate", sf.format(new Date())) ;
			}else {
				context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
			}		
			String date=context.contextMap.get("startDate").toString();
			
			int year=Integer.valueOf(date.split("-")[0]);
			int month=Integer.valueOf(date.split("-")[1]);
			
			ReportDateTo to=ReportDateUtil.getDateByYearAndMonth(year,month);
			context.contextMap.put("year",to.getYear());
			context.contextMap.put("beginTime",to.getBeginTime());
			context.contextMap.put("endTime",to.getEndTime());
			businessTaxList=(List<Map>) DataAccessor.query("priceReport.queryManageFeeValueTax", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e1) {
			e1.printStackTrace();
			LogPrint.getLogStackTrace(e1, logger);
		}			
		ByteArrayOutputStream baos = null;
		String strFileName = "管理费收入增值税客户邮寄信息("+DataUtil.StringUtil(context.contextMap.get("startDate"))+").xls";
		
		InsuranceUtil insuranceUtil = new InsuranceUtil();
		insuranceUtil.createexl();
		baos = insuranceUtil.exportCustInfoToExcel(businessTaxList);
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");		
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
			ServletOutputStream out1 = context.response.getOutputStream();
			insuranceUtil.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

	}
	
	/*
	 * Add by Michael 2012 4-19
	 * 增加导出开票资料
	 */
	public void exportOpenInvoiceByMonth(Context context){
		Map outputMap = new HashMap();
		List errlist = context.errList;
		DataWrap dw = null;
		String date="";
		if(errlist.isEmpty()){
			try {
				if(context.contextMap.get("startDate") == null){
					SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
					context.contextMap.put("startDate", sf.format(new Date())) ;
				}else {
					context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
				}
				outputMap.put("startDate", new SimpleDateFormat("yyyy-MM-dd").parse(context.contextMap.get("startDate").toString())) ;	
				
				dw = (DataWrap) DataAccessor.query("priceReport.exportOpenInvoiceByMonth", context.contextMap, DataAccessor.RS_TYPE.PAGED);
			} catch (Exception e) {
				LogPrint.getLogStackTrace(e, logger);
				errlist.add(e);
			}
		}
		if(errlist.isEmpty()){
			if(errlist.isEmpty()){
				outputMap.put("dw", dw);
				outputMap.put("content", context.contextMap.get("content"));
				outputMap.put("companyCode", context.contextMap.get("companyCode"));
				outputMap.put("companys", LeaseUtil.getCompanys());	
				Output.jspOutput(outputMap, context, "/report/openInvoiceByMonth.jsp");
			}else{
				outputMap.put("errList", errlist);
				Output.jspOutput(outputMap, context, "/error.jsp");
			}
		}
	}

	
	public void queryOpenInvoiceByMonth(Context context){
		Map outputMap = new HashMap();
		List errlist = context.errList;
		DataWrap dw = null;
		String date="";
		if(errlist.isEmpty()){
			try {
				if(context.contextMap.get("startDate") == null){
					SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
					context.contextMap.put("startDate", sf.format(new Date())) ;
				}else {
					context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
				}
				outputMap.put("startDate", new SimpleDateFormat("yyyy-MM-dd").parse(context.contextMap.get("startDate").toString())) ;	
				
				dw = (DataWrap) DataAccessor.query("priceReport.queryAllInvoiceByMonth", context.contextMap, DataAccessor.RS_TYPE.PAGED);
			} catch (Exception e) {
				LogPrint.getLogStackTrace(e, logger);
				errlist.add(e);
			}
		}
		if(errlist.isEmpty()){
			if(errlist.isEmpty()){
				outputMap.put("dw", dw);
				outputMap.put("content", context.contextMap.get("content"));
				outputMap.put("companyCode", context.contextMap.get("companyCode"));
				outputMap.put("companys", LeaseUtil.getCompanys());	
				Output.jspOutput(outputMap, context, "/report/openInvoiceByMonth.jsp");
			}else{
				outputMap.put("errList", errlist);
				Output.jspOutput(outputMap, context, "/error.jsp");
			}
		}
	}
	
	/*
	 * Add by Michael 2012 4-19
	 * 增加导出开票资料
	 */
	public void exportOpenInvoiceByMonthToExcel(Context context){  
		List<Map> businessTaxList=null;
		try {
			if(context.contextMap.get("startDate") == null){
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
				context.contextMap.put("startDate", sf.format(new Date())) ;
			}else {
				context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
			}
			businessTaxList=(List<Map>) DataAccessor.query("priceReport.queryAllInvoiceByMonth", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e1) {
			e1.printStackTrace();
			LogPrint.getLogStackTrace(e1, logger);
		}			
		ByteArrayOutputStream baos = null;
		String strFileName = "开票资料-旧案("+DataUtil.StringUtil(context.contextMap.get("startDate"))+").xls";
		
		InsuranceUtil insuranceUtil = new InsuranceUtil();
		insuranceUtil.createexl();
		baos = insuranceUtil.exportOpenInvoiceToExcel(businessTaxList);
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");		
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
			ServletOutputStream out1 = context.response.getOutputStream();
			insuranceUtil.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

	}	

	/*
	 * Add by Michael 2012 4-19
	 * 增加导出开票资料
	 */
	public void exportOpenInvoiceByDay(Context context){
		Map outputMap = new HashMap();
		List errlist = context.errList;
		DataWrap dw = null;
		String date="";
		if(errlist.isEmpty()){
			try {

				outputMap.put("startDate", context.contextMap.get("startDate")) ;	
				outputMap.put("endDate",  context.contextMap.get("endDate"));
				
				dw = (DataWrap) DataAccessor.query("priceReport.exportOpenInvoiceByDay", context.contextMap, DataAccessor.RS_TYPE.PAGED);
			} catch (Exception e) {
				LogPrint.getLogStackTrace(e, logger);
				errlist.add(e);
			}
		}
		if(errlist.isEmpty()){
			if(errlist.isEmpty()){
				outputMap.put("dw", dw);
				outputMap.put("content", context.contextMap.get("content"));
				outputMap.put("companyCode", context.contextMap.get("companyCode"));
				outputMap.put("companys", LeaseUtil.getCompanys());	
				Output.jspOutput(outputMap, context, "/report/openInvoiceByDay.jsp");
			}else{
				outputMap.put("errList", errlist);
				Output.jspOutput(outputMap, context, "/error.jsp");
			}
		}
	}

	/*
	 * Add by Michael 2012 4-19
	 * 增加导出开票资料
	 */
	@SuppressWarnings("unchecked")
	public void exportOpenInvoiceByDayToExcel(Context context){  
		List<Map> businessTaxList=null;
		try {

			businessTaxList=(List<Map>) DataAccessor.query("priceReport.exportOpenInvoiceByDay", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e1) {
			e1.printStackTrace();
			LogPrint.getLogStackTrace(e1, logger);
		}			
		ByteArrayOutputStream baos = null;
		String strFileName = "开票资料-新案("+DataUtil.StringUtil(context.contextMap.get("startDate"))+").xls";
		
		InsuranceUtil insuranceUtil = new InsuranceUtil();
		insuranceUtil.createexl();
		baos = insuranceUtil.exportOpenInvoiceToExcel(businessTaxList);
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");		
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
			ServletOutputStream out1 = context.response.getOutputStream();
			insuranceUtil.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

	}	

	/*
	 * Add by Michael 2012 10-29
	 * 增加导出增值税开票资料   新案
	 */
	public void exportValueAddOpenInvoiceByDay(Context context){
		Map outputMap = new HashMap();
		List errlist = context.errList;
		PagingInfo<Object> dw = null;
		String date="";
		if(errlist.isEmpty()){
			try {

				outputMap.put("startDate", context.contextMap.get("startDate")) ;	
				outputMap.put("endDate",  context.contextMap.get("endDate"));
				
				dw = baseService.queryForListWithPaging("priceReport.exportValueAddTaxByDay", context.contextMap,"RUNNUM");
			} catch (Exception e) {
				LogPrint.getLogStackTrace(e, logger);
				errlist.add(e);
			}
		}
		if(errlist.isEmpty()){
			if(errlist.isEmpty()){
				outputMap.put("taxPlanCode", context.contextMap.get("taxPlanCode"));
				outputMap.put("dw", dw);
				outputMap.put("content", context.contextMap.get("content"));
				outputMap.put("companyCode", context.contextMap.get("companyCode"));
				outputMap.put("companys", LeaseUtil.getCompanys());	
				Output.jspOutput(outputMap, context, "/report/openValueAddInvoiceByDay.jsp");
			}else{
				outputMap.put("errList", errlist);
				Output.jspOutput(outputMap, context, "/error.jsp");
			}
		}
	}

	/*
	 * Add by Michael 2012 4-19
	 * 增加导出开票资料
	 */
	public void exportValueAddOpenInvoiceByDayToExcel(Context context){  
		List<Map> businessTaxList=null;
		try {

			businessTaxList=(List<Map>) DataAccessor.query("priceReport.exportValueAddTaxByDay", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e1) {
			e1.printStackTrace();
			LogPrint.getLogStackTrace(e1, logger);
		}			
		ByteArrayOutputStream baos = null;
		String strFileName = "增值税开票资料-新案("+DataUtil.StringUtil(context.contextMap.get("startDate"))+").xls";
		
		InsuranceUtil insuranceUtil = new InsuranceUtil();
		insuranceUtil.createexl();
		baos = insuranceUtil.exportValueAddOpenInvoiceToExcel(businessTaxList);
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");		
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
			ServletOutputStream out1 = context.response.getOutputStream();
			insuranceUtil.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

	}	
	/**
	 * 增加导出开票资料的供应商的邮寄信息
	 * @param context
	 */
	public void exportValueAddOpenInvoiceCustInfoByDayToExcel(Context context){
		List<Map> businessTaxList=null;
		try {

			businessTaxList=(List<Map>) DataAccessor.query("priceReport.exportValueAddTaxByDay", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e1) {
			e1.printStackTrace();
			LogPrint.getLogStackTrace(e1, logger);
		}			
		ByteArrayOutputStream baos = null;
		String strFileName = "增值税开票资料-新案客户邮寄地址("+DataUtil.StringUtil(context.contextMap.get("startDate"))+").xls";
		
		InsuranceUtil insuranceUtil = new InsuranceUtil();
		insuranceUtil.createexl();
		baos = insuranceUtil.exportCustInfoToExcel(businessTaxList);
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");		
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
			ServletOutputStream out1 = context.response.getOutputStream();
			insuranceUtil.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	
	public void queryValueAddOpenInvoiceByMonth(Context context){
		Map outputMap = new HashMap();
		List errlist = context.errList;
		PagingInfo<Object> dw = null;
		String date="";
		if(errlist.isEmpty()){
			try {
				if(context.contextMap.get("startDate") == null){
					SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
					context.contextMap.put("startDate", sf.format(new Date())) ;
				}else {
					context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
				}
				outputMap.put("startDate", new SimpleDateFormat("yyyy-MM-dd").parse(context.contextMap.get("startDate").toString())) ;	
				dw = baseService.queryForListWithPaging("priceReport.exportValueAddTaxByMonth", context.contextMap,"RUNNUM");
			} catch (Exception e) {
				LogPrint.getLogStackTrace(e, logger);
				errlist.add(e);
			}
		}
		if(errlist.isEmpty()){
			if(errlist.isEmpty()){
				outputMap.put("taxPlanCode", context.contextMap.get("taxPlanCode"));
				outputMap.put("dw", dw);
				outputMap.put("content", context.contextMap.get("content"));
				outputMap.put("companyCode", context.contextMap.get("companyCode"));
				outputMap.put("companys", LeaseUtil.getCompanys());	
				Output.jspOutput(outputMap, context, "/report/openValueAddInvoiceByMonth.jsp");
			}else{
				outputMap.put("errList", errlist);
				Output.jspOutput(outputMap, context, "/error.jsp");
			}
		}
	}
	
	/*
	 * Add by Michael 2012 4-19
	 * 增加导出开票资料
	 */
	public void exportValueAddOpenInvoiceByMonthToExcel(Context context){  
		List<Map> businessTaxList=null;
		try {
			if(context.contextMap.get("startDate") == null){
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
				context.contextMap.put("startDate", sf.format(new Date())) ;
			}else {
				context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
			}
			businessTaxList=(List<Map>) DataAccessor.query("priceReport.exportValueAddTaxByMonth", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e1) {
			e1.printStackTrace();
			LogPrint.getLogStackTrace(e1, logger);
		}			
		ByteArrayOutputStream baos = null;
		String strFileName = "增值税开票资料-旧案("+DataUtil.StringUtil(context.contextMap.get("startDate"))+").xls";
		
		InsuranceUtil insuranceUtil = new InsuranceUtil();
		insuranceUtil.createexl();
		baos = insuranceUtil.exportValueAddOpenInvoiceToExcel(businessTaxList);
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");		
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
			ServletOutputStream out1 = context.response.getOutputStream();
			insuranceUtil.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

	}
	
	public void exportValueAddOpenInvoiceByMonthToExcel1(Context context){  
		List<Map> resultList1=null;
		List<Map> resultList=null;
		try {
			if(context.contextMap.get("startDate") == null){
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
				context.contextMap.put("startDate", sf.format(new Date())) ;
			}else {
				context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
			}
			resultList1=(List<Map>) DataAccessor.query("priceReport.exportValueAddTaxByMonth", context.contextMap, DataAccessor.RS_TYPE.LIST);
			
			resultList=new ArrayList<Map>();
			
			int count=1;
			for(int i=0;resultList1!=null&&i<resultList1.size();i++) {
				for(int j=0;j<3;j++) {
					Map<String,Object> dataMap=new HashMap<String,Object>();
					dataMap.put("RECD_ID",resultList1.get(i).get("RECD_ID"));
					dataMap.put("RECP_ID",resultList1.get(i).get("RECP_ID"));
					dataMap.put("RUNNUM",resultList1.get(i).get("RUNNUM"));
					dataMap.put("LINK_ADDRESS",resultList1.get(i).get("LINK_ADDRESS"));
					dataMap.put("CORP_TAX_CODE",resultList1.get(i).get("CORP_TAX_CODE"));
					dataMap.put("BANK_NAME",resultList1.get(i).get("BANK_NAME"));
					dataMap.put("CUST_NAME",resultList1.get(i).get("CUST_NAME"));
					dataMap.put("PRODUCT_KIND",resultList1.get(i).get("PRODUCT_KIND"));
					dataMap.put("UNIT",resultList1.get(i).get("UNIT"));
					dataMap.put("NUMBER",resultList1.get(i).get("NUMBER"));
					dataMap.put("UNIT_PRICE",resultList1.get(i).get("UNIT_PRICE"));
					dataMap.put("TAX_RATE",resultList1.get(i).get("TAX_RATE"));
					dataMap.put("REMARK1",resultList1.get(i).get("REMARK1")+"【本融资租赁本金即属设备采购之本金】");
					dataMap.put("TAX_PLAN_CODE",resultList1.get(i).get("TAX_PLAN_CODE"));
					
					String num="";
					if((count+"").length()==1) {
						num="000";
					} else if((count+"").length()==2) {
						num="00";
					} else if((count+"").length()==3) {
						num="0";
					}
					if(j==0) {
						dataMap.put("PRODUCT_NAME","融资租赁本金");
						dataMap.put("PRICE",Double.valueOf(resultList1.get(i).get("OWN_PRICE")+"")-Double.valueOf(resultList1.get(i).get("PLEDGE_PRICE")+""));
						dataMap.put("REMARK2",num+count);
						dataMap.put("REMARK3","首租");
					} else if(j==1) {
						
						dataMap.put("PRODUCT_NAME","融资租赁利息");
						dataMap.put("PRICE",resultList1.get(i).get("REN_PRICE"));
						dataMap.put("REMARK2",num+count);
						dataMap.put("REMARK3","利息");
						
					} else {
						
						dataMap.put("PRODUCT_NAME","融资租赁本金");
						
						if(Double.valueOf(resultList1.get(i).get("PLEDGE_PRICE")+"")>0) {
							count++;
							dataMap.put("PRICE",resultList1.get(i).get("PLEDGE_PRICE"));
							dataMap.put("REMARK2",num+count);
							dataMap.put("REMARK3","保证金");
						}
						count++;
					}
					
					if(j==2&&Double.valueOf(resultList1.get(i).get("PLEDGE_PRICE")+"")<=0) {
						
					} else {
						resultList.add(dataMap);
					}
					
				}
			}
			
		} catch (Exception e1) {
			e1.printStackTrace();
			LogPrint.getLogStackTrace(e1, logger);
		}			
		ByteArrayOutputStream baos = null;
		String strFileName = "增值税开票资料-旧案("+DataUtil.StringUtil(context.contextMap.get("startDate"))+").xls";
		
		InsuranceUtil insuranceUtil = new InsuranceUtil();
		insuranceUtil.createexl();
		baos = insuranceUtil.exportValueAddOpenInvoiceToExcel1(resultList);
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");		
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
			ServletOutputStream out1 = context.response.getOutputStream();
			insuranceUtil.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

	}
	/*
	 * 
	 * 增加导出客户邮寄信息（增值税开票资料-旧案）
	 */
	public void exportValueAddOpenInvoiceCustInfoByMonthToExcel(Context context){  
		List<Map> businessTaxList=null;
		try {
			if(context.contextMap.get("startDate") == null){
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
				context.contextMap.put("startDate", sf.format(new Date())) ;
			}else {
				context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
			}
			context.contextMap.put("orderColumn", "RUNNUM");
			businessTaxList=(List<Map>) DataAccessor.query("priceReport.exportValueAddTaxByMonth", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e1) {
			e1.printStackTrace();
			LogPrint.getLogStackTrace(e1, logger);
		}			
		ByteArrayOutputStream baos = null;
		String strFileName = "增值税开票资料-旧案客户邮寄信息("+DataUtil.StringUtil(context.contextMap.get("startDate"))+").xls";
		
		InsuranceUtil insuranceUtil = new InsuranceUtil();
		insuranceUtil.createexl();
		baos = insuranceUtil.exportCustInfoToExcel(businessTaxList);
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");		
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
			ServletOutputStream out1 = context.response.getOutputStream();
			insuranceUtil.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

	}
	
	/**
	 * 增值税明细表
	 * @param context
	 */
	public void valueAddTaxReport(Context context){
		Map outputMap = new HashMap();
		List errlist = context.errList;
		PagingInfo<Object> dw = null;
		String date="";
		if(errlist.isEmpty()){
			try {

				if(context.contextMap.get("startDate") == null){
					SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
					context.contextMap.put("startDate", sf.format(new Date())) ;
				}else {
					context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
				}
				outputMap.put("startDate", new SimpleDateFormat("yyyy-MM-dd").parse(context.contextMap.get("startDate").toString())) ;	
				
				dw = baseService.queryForListWithPaging("priceReport.queryValueAddBusinessTax", context.contextMap,"LEASE_CODE", ORDER_TYPE.DESC);
			} catch (Exception e) {
				LogPrint.getLogStackTrace(e, logger);
				errlist.add(e);
			}
		}
		if(errlist.isEmpty()){
			if(errlist.isEmpty()){
				outputMap.put("dw", dw);
				outputMap.put("content", context.contextMap.get("content"));
				outputMap.put("companyCode", context.contextMap.get("companyCode"));
				outputMap.put("companys", LeaseUtil.getCompanys());	
				Output.jspOutput(outputMap, context, "/report/queryValueAddTax.jsp");
			}else{
				outputMap.put("errList", errlist);
				Output.jspOutput(outputMap, context, "/error.jsp");
			}
		}
		
	}
	
	/**
	 * 增值税明细导出 excel
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void expValueAddTaxToExcel(Context context){  
		List<Map> businessTaxList=null;
		try {
			//MoneyRateService.queryMoneyRate(context) ;//查询利率（动态）
			//Modify by Michael 2012 02-28  导出excel时 带出所以资料
			if(context.contextMap.get("startDate") == null){
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
				context.contextMap.put("startDate", sf.format(new Date())) ;
			}else {
				context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
			}
			
			businessTaxList=(List<Map>) baseService.queryForList("priceReport.queryValueAddBusinessTax", context.contextMap);
		} catch (Exception e1) {
			e1.printStackTrace();
			LogPrint.getLogStackTrace(e1, logger);
		}			
		ByteArrayOutputStream baos = null;
		String strFileName = "增值税明细表("+DataUtil.StringUtil(context.contextMap.get("date"))+").xls";
		
		InsuranceUtil insuranceUtil = new InsuranceUtil();
		insuranceUtil.createexl();
		baos = insuranceUtil.exportValueAddTaxExcel(businessTaxList);
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");		
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
			ServletOutputStream out1 = context.response.getOutputStream();
			insuranceUtil.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

	}
	
	/*
	 * Add by Michael 2013 4-3
	 * 增加销账月报表
	 */
	public void queryMonthDecomposeReport(Context context){
		Map outputMap = new HashMap();
		List errlist = context.errList;
		PagingInfo<Object> dw = null;
		String date="";
		if(errlist.isEmpty()){
			try {
				if(context.contextMap.get("startDate") == null){
					SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
					context.contextMap.put("startDate", sf.format(new Date())) ;
				}else {
					context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
				}
				outputMap.put("startDate", new SimpleDateFormat("yyyy-MM-dd").parse(context.contextMap.get("startDate").toString())) ;	
				
				dw = baseService.queryForListWithPaging("priceReport.queryMonthDecomposeReport", context.contextMap,"LEASE_CODE", ORDER_TYPE.DESC);
			} catch (Exception e) {
				LogPrint.getLogStackTrace(e, logger);
				errlist.add(e);
			}
		}
		if(errlist.isEmpty()){
			if(errlist.isEmpty()){
				outputMap.put("dw", dw);
				outputMap.put("content", context.contextMap.get("content"));
				outputMap.put("companyCode", context.contextMap.get("companyCode"));
				outputMap.put("companys", LeaseUtil.getCompanys());	
				Output.jspOutput(outputMap, context, "/report/monthDecomposeReport.jsp");
			}else{
				outputMap.put("errList", errlist);
				Output.jspOutput(outputMap, context, "/error.jsp");
			}
		}
	}
	
	//Add by Michael 2012 5-14  通过日期  查询报表
	public static Map<String,Object> queryMonthDecomposeReportToExcel(String query_date,String content,String companyCode) throws Exception {
		List monthDecomposeReport = new ArrayList();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("companyCode", companyCode) ;
		try {
			if(query_date == null || "".equals(query_date)){
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
				paramMap.put("startDate", sf.format(new Date())) ;
			}else {
				paramMap.put("startDate", query_date + "-01") ;
			}
			paramMap.put("startDate", new SimpleDateFormat("yyyy-MM-dd").parse(paramMap.get("startDate").toString())) ;	
			paramMap.put("content", content);
			monthDecomposeReport = (List) DataAccessor.query("priceReport.queryMonthDecomposeReport", paramMap,DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resultMap.put("monthDecomposeReport", monthDecomposeReport);
		return resultMap;
	}
	
	public void stayBuyPriceTaxLinkInfoToExcelForValueAdd(Context context){  
		List<Map> businessTaxList=null;
		try {
			if(context.contextMap.get("startDate") == null){
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
				context.contextMap.put("startDate", sf.format(new Date())) ;
			}else {
				context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
			}
			
			int year=Integer.valueOf(context.contextMap.get("startDate").toString().split("-")[0]);
			int month=Integer.valueOf(context.contextMap.get("startDate").toString().split("-")[1]);
			ReportDateTo to=ReportDateUtil.getDateByYearAndMonth(year,month);
			context.contextMap.put("startTime",to.getBeginTime());
			context.contextMap.put("endTime",to.getEndTime());
			
			if(month<10) {
				context.contextMap.put("FINANCE_DATE",context.contextMap.get("startDate").toString().split("-")[0]+"-"+context.contextMap.get("startDate").toString().split("-")[1].substring(1));
			} else {
				context.contextMap.put("FINANCE_DATE",context.contextMap.get("startDate").toString().split("-")[0]+"-"+context.contextMap.get("startDate").toString().split("-")[1]);
			}
			
			businessTaxList=(List<Map>) DataAccessor.query("priceReport.queryStayBuyPriceValueAddTaxLinkInfo", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e1) {
			e1.printStackTrace();
			LogPrint.getLogStackTrace(e1, logger);
		}			
		ByteArrayOutputStream baos = null;
		String strFileName = "留购款增值客户联系人("+DataUtil.StringUtil(context.contextMap.get("startDate"))+").xls";
		
		InsuranceUtil insuranceUtil = new InsuranceUtil();
		insuranceUtil.createexl();
		baos = insuranceUtil.stayBuyPriceTaxLinkerInfoToExcel(businessTaxList);
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");		
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
			ServletOutputStream out1 = context.response.getOutputStream();
			insuranceUtil.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	
	public void stayBuyPriceTaxLinkInfoToExcel(Context context){  
		List<Map> businessTaxList=null;
		try {
			if(context.contextMap.get("startDate") == null){
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
				context.contextMap.put("startDate", sf.format(new Date())) ;
			}else {
				context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
			}
			int month=Integer.valueOf(context.contextMap.get("startDate").toString().split("-")[1]);
			if(month<10) {
				context.contextMap.put("FINANCE_DATE",context.contextMap.get("startDate").toString().split("-")[0]+"-"+context.contextMap.get("startDate").toString().split("-")[1].substring(1));
			} else {
				context.contextMap.put("FINANCE_DATE",context.contextMap.get("startDate").toString().split("-")[0]+"-"+context.contextMap.get("startDate").toString().split("-")[1]);
			}
			businessTaxList=(List<Map>) DataAccessor.query("priceReport.queryStayBuyPriceTaxLinkInfo", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e1) {
			e1.printStackTrace();
			LogPrint.getLogStackTrace(e1, logger);
		}			
		ByteArrayOutputStream baos = null;
		String strFileName = "留购款营业税客户联系人("+DataUtil.StringUtil(context.contextMap.get("startDate"))+").xls";
		
		InsuranceUtil insuranceUtil = new InsuranceUtil();
		insuranceUtil.createexl();
		baos = insuranceUtil.stayBuyPriceTaxLinkerInfoToExcel(businessTaxList);
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");		
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
			ServletOutputStream out1 = context.response.getOutputStream();
			insuranceUtil.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	
	//**********************************************************************************************************************
	/**
	 * 重新导出开票资料(新案),包括各种案件类型
	 * @author ShenQi
	 * */
		
	public void queryInvoice(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		PagingInfo<Object> dw=null;
		List<Map<String,Object>> dateList=null;
		List<Map<String,Object>> invoiceTypeList=null;
		Map<String,Object> param=new HashMap<String,Object>();
		
		try {
			dateList=(List<Map<String,Object>>)DataAccessor.query("priceReport.getDateList",null,DataAccessor.RS_TYPE.LIST);
			
			param.put("dataType","开票资料类型");
			invoiceTypeList=(List<Map<String,Object>>)DataAccessor.query("dataDictionary.queryDataDictionary",param,DataAccessor.RS_TYPE.LIST);
			
			//获得财务结账周期
			if(StringUtils.isEmpty(context.contextMap.get("selectDate"))) {
				context.contextMap.put("invoiceType",4);
				context.contextMap.put("startDate",dateList.get(0).get("STARTDATE"));
				context.contextMap.put("endDate",dateList.get(0).get("ENDDATE"));
				context.contextMap.put("financeStartDate",dateList.get(0).get("STARTDATE"));
				context.contextMap.put("financeEndDate",dateList.get(0).get("ENDDATE"));
			} else {
				int year=Integer.valueOf(context.contextMap.get("selectDate").toString().split("-")[0]);
				int month=Integer.valueOf(context.contextMap.get("selectDate").toString().split("-")[1]);
				ReportDateTo to=ReportDateUtil.getDateByYearAndMonth(year,month);
				
				context.contextMap.put("startDate",to.getBeginTime());
				context.contextMap.put("endDate",to.getEndTime());
				context.contextMap.put("financeStartDate",to.getBeginTime());
				context.contextMap.put("financeEndDate",to.getEndTime());
			}
			context.contextMap.put("isPay","0");
			dw=baseService.queryForListWithPaging("priceReport.queryInvoice",context.contextMap,"FINANCECONTRACT_DATE");
			
		} catch (Exception e) {
			
		}
		
		outputMap.put("dw",dw);
		outputMap.put("dateList",dateList);
		outputMap.put("invoiceTypeList",invoiceTypeList);
		outputMap.put("invoiceType",context.contextMap.get("invoiceType"));
		outputMap.put("selectDate",context.contextMap.get("selectDate"));
		outputMap.put("content",context.contextMap.get("content"));
		outputMap.put("companyCode", context.contextMap.get("companyCode"));
		outputMap.put("companys", LeaseUtil.getCompanys());	
		Output.jspOutput(outputMap,context,"/report/exportInvoice.jsp");
	}
	
	
	public void exportInvoiceCustInfo(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		PagingInfo<Object> dw=null;
		List<Map<String,Object>> dateList=null;
		List<Map<String,Object>> invoiceTypeList=null;
		Map<String,Object> param=new HashMap<String,Object>();
		
		
		
		try {
			dateList=(List<Map<String,Object>>)DataAccessor.query("priceReport.getDateList",null,DataAccessor.RS_TYPE.LIST);
			
			param.put("dataType","开票资料类型");
			invoiceTypeList=(List<Map<String,Object>>)DataAccessor.query("dataDictionary.queryDataDictionary",param,DataAccessor.RS_TYPE.LIST);
			
			//获得财务结账周期
			if(StringUtils.isEmpty(context.contextMap.get("selectDate"))) {
				context.contextMap.put("invoiceType",4);
				context.contextMap.put("financeStartDate",dateList.get(0).get("STARTDATE"));
				context.contextMap.put("financeEndDate",dateList.get(0).get("ENDDATE"));
				context.contextMap.put("startDate",DateUtil.getFirstDayOfMonth(dateList.get(0).get("CODE").toString())+"-1");
				context.contextMap.put("endDate",DateUtil.getLastDayOfMonth(dateList.get(0).get("CODE").toString()+"-1"));
			} else {
				String yearMonth = context.contextMap.get("selectDate").toString();
				int year=Integer.valueOf(context.contextMap.get("selectDate").toString().split("-")[0]);
				int month=Integer.valueOf(context.contextMap.get("selectDate").toString().split("-")[1]);
				ReportDateTo to=ReportDateUtil.getDateByYearAndMonth(year,month);
				
				context.contextMap.put("financeStartDate",to.getBeginTime());
				context.contextMap.put("financeEndDate",to.getEndTime());
				
				context.contextMap.put("startDate",yearMonth+"-1");
				context.contextMap.put("endDate",DateUtil.getLastDayOfMonth(yearMonth+"-1"));
			}
			context.contextMap.put("needOrderBy", "Y");
			List<Map> list = (List<Map>) DataAccessor.query("priceReport.queryInvoice", context.contextMap, DataAccessor.RS_TYPE.LIST);
			ByteArrayOutputStream baos = null;
			String strFileName = "直租开票资料客户邮寄信息("+DataUtil.StringUtil(context.contextMap.get("startDate"))+").xls";
			
			InsuranceUtil insuranceUtil = new InsuranceUtil();
			insuranceUtil.createexl();
			baos = insuranceUtil.exportCustInfoToExcel(list);
			context.response.setContentType("application/vnd.ms-excel;charset=GB2312");		
			try {
				context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
				ServletOutputStream out1 = context.response.getOutputStream();
				insuranceUtil.close();
				baos.writeTo(out1);
				out1.flush();
			} catch (Exception e) {			 
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		
		} catch (Exception e) {
			
		}
		
	}
	
	public static List<Map<String,Object>> exportInvoiceColumn(String date,String invoiceType,String isPay,String companyCode) throws Exception {
		
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("invoiceType",invoiceType);
		param.put("isPay",isPay);
		int year=Integer.valueOf(date.split("-")[0]);
		int month=Integer.valueOf(date.split("-")[1]);
		ReportDateTo to=ReportDateUtil.getDateByYearAndMonth(year,month);
		param.put("financeStartDate",to.getBeginTime());
		param.put("financeEndDate",to.getEndTime());

		String beginDate = date+"-1";
		String endDate = DateUtil.getLastDayOfMonth(beginDate);
		param.put("startDate",beginDate);
		param.put("endDate",endDate);
		param.put("needOrderBy","Y");
		param.put("companyCode",companyCode);
		List<Map<String,Object>> resultList=(List<Map<String,Object>>)DataAccessor.query("priceReport.queryInvoice",param,DataAccessor.RS_TYPE.LIST);
		
		return resultList;
	}
	
	public static List<Map<String,Object>> exportInvoiceRow(String date,String invoiceType,String companyCode) throws Exception {
		
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("invoiceType",invoiceType);
		
		int year=Integer.valueOf(date.split("-")[0]);
		int month=Integer.valueOf(date.split("-")[1]);
		ReportDateTo to=ReportDateUtil.getDateByYearAndMonth(year,month);
		param.put("financeStartDate",to.getBeginTime());
		param.put("financeEndDate",to.getEndTime());
		
		String beginDate = date+"-1";
		String endDate = DateUtil.getLastDayOfMonth(beginDate);
		param.put("startDate",beginDate);
		param.put("endDate",endDate);		
		param.put("needOrderBy","Y");
		param.put("companyCode",companyCode);
		List<Map<String,Object>> resultList1=(List<Map<String,Object>>)DataAccessor.query("priceReport.queryInvoice",param,DataAccessor.RS_TYPE.LIST);
		List<Map<String,Object>> resultList=new ArrayList<Map<String,Object>>();
		
		int count=1;
		for(int i=0;resultList1!=null&&i<resultList1.size();i++) {
			for(int j=0;j<3;j++) {
				Map<String,Object> dataMap=new HashMap<String,Object>();
				dataMap.put("RECD_ID",resultList1.get(i).get("RECD_ID"));
				dataMap.put("RECP_ID",resultList1.get(i).get("RECP_ID"));
				dataMap.put("RUNNUM",resultList1.get(i).get("RUNNUM"));
				dataMap.put("LINK_ADDRESS",resultList1.get(i).get("LINK_ADDRESS"));
				dataMap.put("CORP_TAX_CODE",resultList1.get(i).get("CORP_TAX_CODE"));
				dataMap.put("BANK_NAME",resultList1.get(i).get("BANK_NAME"));
				dataMap.put("CUST_NAME",resultList1.get(i).get("CUST_NAME"));
				dataMap.put("PRODUCT_KIND",resultList1.get(i).get("PRODUCT_KIND"));
				dataMap.put("UNIT",resultList1.get(i).get("UNIT"));
				dataMap.put("NUMBER",resultList1.get(i).get("NUMBER"));
				dataMap.put("UNIT_PRICE",resultList1.get(i).get("UNIT_PRICE"));
				dataMap.put("TAX_RATE",resultList1.get(i).get("TAX_RATE"));
				dataMap.put("REMARK1",resultList1.get(i).get("REMARK1")+"【本融资租赁本金即属设备采购之本金】");
				dataMap.put("TAX_PLAN_CODE",resultList1.get(i).get("TAX_PLAN_CODE"));
				
				String num="";
				if((count+"").length()==1) {
					num="000";
				} else if((count+"").length()==2) {
					num="00";
				} else if((count+"").length()==3) {
					num="0";
				}
				if(j==0) {
					dataMap.put("PRODUCT_NAME","融资租赁本金");
					dataMap.put("PRICE",Double.valueOf(resultList1.get(i).get("OWN_PRICE")+"")-Double.valueOf(resultList1.get(i).get("PLEDGE_PRICE")+""));
					dataMap.put("REMARK2",num+count);
					dataMap.put("REMARK3","首租");
				} else if(j==1) {
					
					dataMap.put("PRODUCT_NAME","融资租赁利息");
					dataMap.put("PRICE",resultList1.get(i).get("REN_PRICE"));
					dataMap.put("REMARK2",num+count);
					dataMap.put("REMARK3","利息");
					
				} else {
					
					dataMap.put("PRODUCT_NAME","融资租赁本金");
					
					if(Double.valueOf(resultList1.get(i).get("PLEDGE_PRICE")+"")>0) {
						count++;
						dataMap.put("PRICE",resultList1.get(i).get("PLEDGE_PRICE"));
						dataMap.put("REMARK2",num+count);
						dataMap.put("REMARK3","保证金");
					}
					count++;
				}
				
				if(j==2&&Double.valueOf(resultList1.get(i).get("PLEDGE_PRICE")+"")<=0) {
					
				} else {
					resultList.add(dataMap);
				}
				
			}
		}
		
		return resultList;
	}
	//是否缴款 旧案 直租 zhangbo
	public void expToIsPayExcel(Context context){  
		List<Map> businessTaxList=null;
		try {
			if(context.contextMap.get("startDate") == null){
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
				context.contextMap.put("startDate", sf.format(new Date())) ;
			}else {
				context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
			}
			businessTaxList=(List<Map>) DataAccessor.query("priceReport.exportIsPayByMonth", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e1) {
			e1.printStackTrace();
			LogPrint.getLogStackTrace(e1, logger);
		}			
		ByteArrayOutputStream baos = null;
		String strFileName = "是否缴款增值税开票资料-旧案("+DataUtil.StringUtil(context.contextMap.get("startDate"))+").xls";
		
		InsuranceUtil insuranceUtil = new InsuranceUtil();
		insuranceUtil.createexl();
		baos = insuranceUtil.exportValueAddOpenInvoiceToExcel(businessTaxList);
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");		
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
			ServletOutputStream out1 = context.response.getOutputStream();
			insuranceUtil.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

	}
	
	public void insuranceReportForCar(Context context) throws Exception{
		String date = (String) context.contextMap.get("date");
		if(date!=null){
			String[] dateArray = date.split("-");
			context.contextMap.put("year", Integer.parseInt(dateArray[0]));
			context.contextMap.put("month", Integer.parseInt(dateArray[1]));
		}else{
			Calendar c = Calendar.getInstance();
			context.contextMap.put("year",c.get(Calendar.YEAR));
			context.contextMap.put("month",c.get(Calendar.MONTH)+1);
			date = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH)+1);
		}
		StringBuffer ids =new StringBuffer("''");
		List creditIDs = (List)DataAccessor.query("rentFile.getCreditIdForCarExportContractFile", context.contextMap,DataAccessor.RS_TYPE.LIST);
		if(creditIDs!=null){
			for(int i=0,len=creditIDs.size();i<len;i++){
				Integer creditId  = (Integer) creditIDs.get(i);
				if(creditId != null){
					Map param = new HashMap();
					param.put("creditId", creditId);				
					List results = (List)DataAccessor.query("rentFile.getCarContractFile", param,DataAccessor.RS_TYPE.LIST);
					if(results!=null || results.size()>=2){//身份证和合同多不存在
						int idcard = 0;
						int contract = 0;
						for(int j=0,len2=results.size();j<len2;j++){
							Map file  = (Map) results.get(j);
							String rentFileName = (String) file.get("FILE_NAME");
							if("车主身份证复印件".equals(rentFileName)||"客户照片".equals(rentFileName)||"营业执照、营业执照副本(含年审章)".equals(rentFileName)){
								idcard ++;
							}else if("个人委托贷款借款合同".equals(rentFileName)){
								contract++;
							}
									
						}
						if(idcard>0 && contract>0){
							ids.append(",");
							ids.append(creditId);
						}
					}
				}
			}
		}

		context.contextMap.put("ids", ids.toString());
		PagingInfo pageInfo = this.baseService.queryForListWithPaging("report.insuranceReportForCar", context.contextMap, "PAY_DATE");
		Map<String, Object> outputMap = new HashMap<String, Object>();
		outputMap.put("dw", pageInfo);
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("date", date);
		outputMap.put("companyCode", context.contextMap.get("companyCode"));
		outputMap.put("companys", LeaseUtil.getCompanys());
		Output.jspOutput(outputMap, context, "/insurance/report/carInsuranceReport.jsp");
	
	}
	
	public void exportInsuranceReportForCar(Context context) throws Exception{
		String date = (String) context.contextMap.get("date");
		if(date!=null){
			String[] dateArray = date.split("-");
			context.contextMap.put("year", Integer.parseInt(dateArray[0]));
			context.contextMap.put("month", Integer.parseInt(dateArray[1]));
		}else{
			Calendar c = Calendar.getInstance();
			context.contextMap.put("year",c.get(Calendar.YEAR));
			context.contextMap.put("month",c.get(Calendar.MONTH)+1);
			date = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH)+1);
		}
		StringBuffer ids =new StringBuffer("''");
		List creditIDs = (List)DataAccessor.query("rentFile.getCreditIdForCarExportContractFile", context.contextMap,DataAccessor.RS_TYPE.LIST);
		if(creditIDs!=null){
			for(int i=0,len=creditIDs.size();i<len;i++){
				Integer creditId  = (Integer) creditIDs.get(i);
				if(creditId != null){
					Map param = new HashMap();
					param.put("creditId", creditId);				
					List results = (List)DataAccessor.query("rentFile.getCarContractFile", param,DataAccessor.RS_TYPE.LIST);
					if(results!=null || results.size()>=2){//身份证和合同多不存在
						int idcard = 0;
						int contract = 0;
						for(int j=0,len2=results.size();j<len2;j++){
							Map file  = (Map) results.get(j);
							String rentFileName = (String) file.get("FILE_NAME");
							if("车主身份证复印件".equals(rentFileName)||"客户照片".equals(rentFileName)||"营业执照、营业执照副本(含年审章)".equals(rentFileName)){
								idcard ++;
							}else if("个人委托贷款借款合同".equals(rentFileName)){
								contract++;
							}
									
						}
						if(idcard>0 && contract>0){
							ids.append(",");
							ids.append(creditId);
						}
					}
				}
			}
		}

		context.contextMap.put("ids", ids.toString());
		context.contextMap.put("order","y");
		
		ByteArrayOutputStream baos = null;
		String strFileName = "小车委贷开票资料明细("+date+").xls";
		
		InsuranceUtil insuranceUtil = new InsuranceUtil();
		insuranceUtil.createexl();
		
		List<Map> result = (List<Map>) this.baseService.queryForList("report.insuranceReportForCar", context.contextMap);
		Double total = (Double) this.baseService.queryForObj("report.insuranceTotalReportForCar",  context.contextMap);  
		baos = insuranceUtil.exportInsuranceReportForCar(result,total);
		
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");		
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
			ServletOutputStream out1 = context.response.getOutputStream();
			insuranceUtil.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	
	public void exportServiceCharge(Context context) throws Exception{
		String date = (String) context.contextMap.get("date");
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH)+1;
		if(date!=null){
			String[] dateArray = date.split("-");
			year = Integer.parseInt(dateArray[0]);
			month = Integer.parseInt(dateArray[1]);
		}
		ReportDateTo reportDate = ReportDateUtil.getDateByYearAndMonth(year, month);
		context.contextMap.put("beginTime",reportDate.getBeginTime());
		context.contextMap.put("endTime",reportDate.getEndTime());
		List<Map> result= (List)DataAccessor.query("report.getServiceChargeReportForCar", context.contextMap,DataAccessor.RS_TYPE.LIST);



		
		ByteArrayOutputStream baos = null;
		String strFileName = "新车委贷手续费开票明细表("+date+").xls";
		
		InsuranceUtil insuranceUtil = new InsuranceUtil();
		insuranceUtil.createexl();
		
		Double total = (Double) this.baseService.queryForObj("report.getServiceChargeTotalReportForCar",  context.contextMap);  
		
		baos = insuranceUtil.exportServiceChargeReportForCar(result,total);
		
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");		
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
			ServletOutputStream out1 = context.response.getOutputStream();
			insuranceUtil.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	
}
