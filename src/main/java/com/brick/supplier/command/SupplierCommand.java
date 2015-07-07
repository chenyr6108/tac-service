package com.brick.supplier.command;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.eclipse.birt.report.model.api.util.StringUtil;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import com.brick.base.command.BaseCommand;
import com.brick.base.to.PagingInfo;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.supplier.service.SupplierService;
import com.brick.util.DateUtil;
import com.brick.util.StringUtils;


public class SupplierCommand extends BaseCommand{
	
	private SupplierService supplierService;
	
	public void getSupplierContactInfo(Context context)throws Exception{	
		String beginDate  = (String) context.contextMap.get("beginDate");
		String endDate  = (String) context.contextMap.get("endDate");
		java.sql.Date bdate = null;
		if(!StringUtils.isEmpty(beginDate)){
			bdate = new java.sql.Date(DateUtil.strToDay(beginDate).getTime());
		}
		java.sql.Date edate = null;
		if(!StringUtils.isEmpty(endDate)){
			edate = new java.sql.Date(DateUtil.strToDay(endDate).getTime());
		}
	
		java.sql.Date date = bdate!=null?bdate:edate;
		context.contextMap.put("date", date);
		context.contextMap.put("beginDate",bdate);
		context.contextMap.put("endDate", edate);
		
		PagingInfo pagingInfo = baseService.queryForListWithPaging("supplier.getSupplierContactInfo", context.contextMap, "id");
		
		
		List list = pagingInfo.getResultList();
		Map outputMap = new HashMap();
		outputMap.put("pagingInfo", pagingInfo);
		outputMap.put("beginDate", beginDate);
		outputMap.put("endDate", endDate);
		outputMap.put("supplierName", context.contextMap.get("supplierName"));
		//outputMap.put("supplierName", context.contextMap.get("supplierName"));
		Output.jspOutput(outputMap,context,"/supplier/supplierContactInfo.jsp");
	}
	
	
	
	public SupplierService getSupplierService() {
		return supplierService;
	}



	public void setSupplierService(SupplierService supplierService) {
		this.supplierService = supplierService;
	}



	public void exportExcel(Context context)throws Exception{
		
		String supplierName = (String) context.contextMap.get("supplierName");
		String beginDate  = (String) context.contextMap.get("beginDate");
		String endDate  = (String) context.contextMap.get("endDate");
		Date bdate = null;
		if(!StringUtil.isEmpty(beginDate)){
			bdate = DateUtil.strToDay(beginDate);
		}
		
		Date edate = null;
		if(!StringUtil.isEmpty(endDate)){
			edate = DateUtil.strToDay(endDate);
		}
		
		
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		WorkbookSettings workbookSettings = new WorkbookSettings();
		workbookSettings.setEncoding("ISO-8859-1");
		WritableWorkbook wb = Workbook.createWorkbook(baos, workbookSettings);
		WritableSheet sheet = wb.createSheet("供应商联络信息一览表", 0);
		
		
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
		format2.setAlignment(Alignment.LEFT);
		format2.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
		format2.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
		format2.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
		format2.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
		format2.setWrap(true);
		
		
		
		int column = 0;
		int row = 0;
		
		Label lable = new Label(column, row, "供应商名称", format);
		sheet.addCell(lable);
		column++;

		lable = new Label(column, row, "邮寄地址", format);
		sheet.addCell(lable);
		column++;
		
		lable = new Label(column, row, "电话", format);
		sheet.addCell(lable);
		column++;
		
		lable = new Label(column, row, "联系人姓名", format);
		sheet.addCell(lable);
		column++;
			
		row++;
		
		List<Map<String,Object>> speciallist = DictionaryUtil.getDictionary("例外供应商");
		StringBuffer speList = new StringBuffer("''");
		if(speciallist!=null){
			for(Map<String,Object> supplier:speciallist){
				speList.append(",'");
				speList.append(supplier.get("FLAG"));
				speList.append("'");
			}
		}

		List<Map<String, Object>> list = (List<Map<String, Object>>) supplierService.getSupplierContactInfo(supplierName,speList.toString(),bdate,edate);
		if(list != null && list.size()>0){
			for(Map m :list){
				column = 0;
				lable = new Label(column, row,m.get("NAME")!=null?m.get("NAME").toString():"" , format2);
				sheet.addCell(lable);
				column++;
				
				lable = new Label(column, row, m.get("LINK_WORK_ADDRESS")!=null?m.get("LINK_WORK_ADDRESS").toString():"", format2);
				sheet.addCell(lable);
				column++;
				
				lable = new Label(column, row,m.get("LINK_MOBILE")!=null?m.get("LINK_MOBILE").toString():"", format2);
				sheet.addCell(lable);
				column++;
				
				lable = new Label(column, row,m.get("LINK_NAME")!=null?m.get("LINK_NAME").toString():"", format2);
				sheet.addCell(lable);
				
				row++;
			}
		}

		wb.write();
		wb.close();

		
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");
		context.response.setHeader("Content-Disposition", "attachment;filename="+ new String("供应商联络信息一览表".getBytes("GBK"), "ISO-8859-1"));
		ServletOutputStream out = context.response.getOutputStream();
		baos.writeTo(out);
		out.flush();
	}
}
