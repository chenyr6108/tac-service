package com.brick.exportcontractpdf.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.log.service.LogPrint;

import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.entity.Context;


public class ExportWaitFileEcl extends AService {
	Log logger = LogFactory.getLog(ExportWaitFileEcl.class);
	@Override
	protected void afterExecute(String action, Context context) {
		// TODO Auto-generated method stub
		super.afterExecute(action, context);
	}

	@Override
	protected boolean preExecute(String action, Context context) {
		// TODO Auto-generated method stub
		return super.preExecute(action, context);
	}

	WritableWorkbook wb = null;
	WorkbookSettings workbookSettings = new WorkbookSettings();
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	

	
	public void createexl() {
		try {
			wb = Workbook.createWorkbook(baos, workbookSettings);
		} catch (IOException e) {

			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}

	@SuppressWarnings("unchecked")
	//public ByteArrayOutputStream export(List commandFileNames,List specialFileNames,List commonRectFiles,List specialRectFiles) {
	public ByteArrayOutputStream export(List commandFileNames,List specialFileNames,List commandFileConditions,List specialFileConditions) {
		 
		WritableSheet sheet = null;
		WritableSheet sheet2 = null;
		try {
			/* 解决中文乱码 */
			workbookSettings.setEncoding("ISO-8859-1");
			sheet = wb.createSheet("一般合同的待补文件统计", 1);
			sheet2 = wb.createSheet("委托购买合同的待补文件统计", 2);
			// 大标题 字体黑体 20号 水平|垂直 居中对齐 加粗
			WritableFont font1 = new WritableFont(
					WritableFont.createFont("黑体"), 15, WritableFont.BOLD);
			WritableCellFormat format1 = new WritableCellFormat(font1);
			format1.setAlignment(Alignment.CENTRE);
			format1.setVerticalAlignment(VerticalAlignment.CENTRE);

			WritableFont font2 = new WritableFont(
					WritableFont.createFont("宋体"), 11, WritableFont.BOLD);
			WritableCellFormat format2 = new WritableCellFormat(font2);
			format2.setAlignment(Alignment.CENTRE);
			format2.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format2.setBorder(Border.ALL, BorderLineStyle.THIN);
			format2.setWrap(true);
			// 表格部分 字体 宋体12号 水平居左对齐 垂直居中对齐 带边框 自动换行
			WritableFont font3 = new WritableFont(
					WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
			WritableCellFormat format3 = new WritableCellFormat(font3);
			format3.setAlignment(Alignment.LEFT);
			format3.setVerticalAlignment(VerticalAlignment.CENTRE);
			// format3.setBorder(Border.ALL, BorderLineStyle.THIN);
			format3.setWrap(true);
			WritableFont font4 = new WritableFont(
					WritableFont.createFont("宋体"), 11);
			WritableCellFormat format4 = new WritableCellFormat(font4);
			format4.setAlignment(Alignment.CENTRE);
			format4.setVerticalAlignment(VerticalAlignment.CENTRE);
			format4.setBorder(Border.ALL, BorderLineStyle.THIN);
			format4.setWrap(true);

			WritableFont font5 = new WritableFont(
					WritableFont.createFont("宋体"), 12, WritableFont.BOLD);
			WritableCellFormat format5 = new WritableCellFormat(font5);
			format5.setAlignment(Alignment.CENTRE);
			format5.setVerticalAlignment(VerticalAlignment.CENTRE);

			// 设置列宽
			sheet.setColumnView(0, 30);
			sheet.setColumnView(1, 35);
			sheet.setColumnView(2, 35);
			sheet.setColumnView(3, 45);
			sheet.setColumnView(4, 40);
			sheet.setColumnView(5, 40);
			sheet.setColumnView(6, 40);
			sheet.setColumnView(7, 40);
			sheet.setColumnView(8, 30);
			sheet.setColumnView(9, 35);
			sheet.setColumnView(10, 30);
			Label cell = null;
			for(int k=0;k<commandFileNames.size();k++){
				cell = new Label(0, 0, "合同号" , format2);
				sheet.addCell(cell);
				cell = new Label(1, 0, "客户名称" , format2);
				sheet.addCell(cell);				
				HashMap filename=(HashMap)commandFileNames.get(k);
				//System.out.println(filename.get("FILE_NAME").toString()+"=============");
				cell = new Label(k+2, 0, filename.get("FILE_NAME")==null?"":filename.get("FILE_NAME").toString() , format2);
				sheet.addCell(cell);
			}
			/*
			for(int k=0;k<commonRectFiles.size();k++){				
				HashMap rentMap=(HashMap)commonRectFiles.get(k);
				List commonFiles=(List) DataAccessor.query("exportContractPdf.queryAllCommandRentFiles",rentMap, DataAccessor.RS_TYPE.LIST);
				//合同的11项附件数小于总附件数时才显示合同信息
				if(commonFiles.size()<commandFileNames.size()){
					//控制这一行的显示列数，一般的是11，委托的是12	
					for(int i=0;i<commandFileNames.size();i++){
							HashMap commonFileMap=null;
							if(i<commonFiles.size()){
								commonFileMap=(HashMap)commonFiles.get(i);
							}
							cell = new Label(0, k+1, rentMap.get("LEASE_CODE")==null?"":rentMap.get("LEASE_CODE").toString() , format2);
							sheet.addCell(cell);
							cell = new Label(1, k+1, rentMap.get("CUST_NAME")==null?"":rentMap.get("CUST_NAME").toString() , format2);
							sheet.addCell(cell);				
							if(commonFileMap!=null){
								HashMap filename=(HashMap)commandFileNames.get(i);
									if(commonFileMap!=null&&commonFileMap.get("FILE_NAME").toString().equals(filename.get("FILE_NAME").toString())){
										cell = new Label(i+2, k+1, "Y" , format2);
										sheet.addCell(cell);
									}else{
										cell = new Label(i+2, k+1, "N" , format2);
										sheet.addCell(cell);
									}
							}else{
								cell = new Label(i+2, k+1, "N" , format2);
								sheet.addCell(cell);
							}
							
						}
				}
			}
			*/
			for(int k=0;k<commandFileConditions.size();k++){				
				HashMap rentMap=(HashMap)commandFileConditions.get(k);	
					for(int i=0;i<commandFileNames.size();i++){
							HashMap commonFileMap=null;
							cell = new Label(0, k+1, rentMap.get("LEASE_CODE")==null?"":rentMap.get("LEASE_CODE").toString() , format2);
							sheet.addCell(cell);
							cell = new Label(1, k+1, rentMap.get("CUST_NAME")==null?"":rentMap.get("CUST_NAME").toString() , format2);
							sheet.addCell(cell);				
							cell = new Label(i+2, k+1, rentMap.get(i).toString() , format2);
							sheet.addCell(cell);
				}
			}
			// 设置列宽
			sheet2.setColumnView(0, 30);
			sheet2.setColumnView(1, 35);
			sheet2.setColumnView(2, 35);
			sheet2.setColumnView(3, 35);
			sheet2.setColumnView(4, 40);
			sheet2.setColumnView(5, 40);
			sheet2.setColumnView(6, 40);
			sheet2.setColumnView(7, 40);
			sheet2.setColumnView(8, 30);
			sheet2.setColumnView(9, 35);
			sheet2.setColumnView(10, 30);
			for(int j=0;j<specialFileNames.size();j++){
				HashMap filename=(HashMap)specialFileNames.get(j);
				cell = new Label(0, 0, "合同号" , format2);
				sheet2.addCell(cell);
				cell = new Label(1, 0, "客户名称" , format2);
				sheet2.addCell(cell);
				//System.out.println(filename.get("FILE_NAME").toString()+"=============");
				cell = new Label(j+2, 0, filename.get("FILE_NAME")==null?"":filename.get("FILE_NAME").toString() , format2);
				sheet2.addCell(cell);
			}
			/*
			for(int k=0;k<specialRectFiles.size();k++){				
				HashMap rentMap=(HashMap)specialRectFiles.get(k);
				List commonFiles=(List) DataAccessor.query("exportContractPdf.queryAllSpecialRentFiles",rentMap, DataAccessor.RS_TYPE.LIST);
				//合同的12项附件数小于总附件数时才显示合同信息
				if(commonFiles.size()<specialRectFiles.size()){
					//控制这一行的显示列数，一般的是11，委托的是12	
					for(int i=0;i<specialFileNames.size();i++){
							HashMap commonFileMap=null;
							if(i<commonFiles.size()){
								commonFileMap=(HashMap)commonFiles.get(i);
							}
							cell = new Label(0, k+1, rentMap.get("LEASE_CODE")==null?"":rentMap.get("LEASE_CODE").toString() , format2);
							sheet2.addCell(cell);
							cell = new Label(1, k+1, rentMap.get("CUST_NAME")==null?"":rentMap.get("CUST_NAME").toString() , format2);
							sheet2.addCell(cell);				
							if(commonFileMap!=null){
								HashMap filename=(HashMap)specialRectFiles.get(i);
									if(commonFileMap!=null&&commonFileMap.get("FILE_NAME").toString().equals(filename.get("FILE_NAME").toString())){
										cell = new Label(i+2, k+1, "Y" , format2);
										sheet2.addCell(cell);
									}else{
										cell = new Label(i+2, k+1, "N" , format2);
										sheet2.addCell(cell);
									}
							}else{
								cell = new Label(i+2, k+1, "N" , format2);
								sheet2.addCell(cell);
							}
							
						}
				}
			}
			*/
			for(int k=0;k<specialFileConditions.size();k++){				
				HashMap rentMap=(HashMap)specialFileConditions.get(k);	
					for(int i=0;i<specialFileNames.size();i++){
							HashMap commonFileMap=null;
							cell = new Label(0, k+1, rentMap.get("LEASE_CODE")==null?"":rentMap.get("LEASE_CODE").toString() , format2);
							sheet2.addCell(cell);
							cell = new Label(1, k+1, rentMap.get("CUST_NAME")==null?"":rentMap.get("CUST_NAME").toString() , format2);
							sheet2.addCell(cell);				
							cell = new Label(i+2, k+1, rentMap.get(i)==null?"N":rentMap.get(i).toString() , format2);
							sheet2.addCell(cell);
				}
			}
			// 写入文件
			//System.out.println("success");

			// wb.close();
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		return baos;
	}

	public void close() {
		try {
			wb.write();
			wb.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
}
