package com.brick.financial.util;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;

import com.brick.service.entity.Context;
import com.brick.util.poi.ExcelFileWriter;
import com.ibm.icu.text.SimpleDateFormat;

public class VATInvoice {
         
	//导出当月拨款利息明细
	   public HSSFWorkbook exportRentDetail(Context context,List<HashMap<String,Object>> lst) throws Exception{
		   ExcelFileWriter efw=new ExcelFileWriter();
			HSSFSheet sheet=efw.createSheet(context.contextMap.get("sheetName")==null?
		  			  "summary":(String)context.contextMap.get("sheetName"));
			
			  sheet.setColumnWidth(0, 9000);
			  sheet.setColumnWidth(1, 5000);
			  sheet.setColumnWidth(2, 4000);
			  sheet.setColumnWidth(3, 4000);
			  sheet.setColumnWidth(4, 4000);
			  sheet.setColumnWidth(5, 4000);
			  sheet.setColumnWidth(6, 4000);
			
	          //设置表头样式 
	          HSSFFont headFont1=null;              
	          HSSFCellStyle headStyle1=null;
	        
	          headFont1=efw.getWorkbook().createFont();
	          headFont1.setFontHeightInPoints((short)11);                            //设置字体大小
	          headFont1.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);                     // 加粗   
	        
	          headStyle1=efw.getWorkbook().createCellStyle();                       //设置样式
	          headStyle1.setAlignment(HSSFCellStyle.ALIGN_CENTER);                  // 左右居中   
	          headStyle1.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);       // 上下居中    
	          headStyle1.setWrapText(true);                                          // 自动换行  
	          headStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
	          headStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	          headStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	          headStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
	          headStyle1.setFont(headFont1);
	          
	          //设置 样式 (居中)
	          HSSFCellStyle center=null;
	          center=efw.getWorkbook().createCellStyle();                       //设置样式
	          center.setAlignment(HSSFCellStyle.ALIGN_CENTER);                  // 左右居中   
	          center.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);       // 上下居中    
	          center.setWrapText(true);                                          // 自动换行  
	          center.setBorderTop(HSSFCellStyle.BORDER_THIN);
	          center.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	          center.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	          center.setBorderRight(HSSFCellStyle.BORDER_THIN);
	          
	          //设置 样式 (居中)
	          HSSFCellStyle right=null;
	          right=efw.getWorkbook().createCellStyle();                       //设置样式
	          right.setAlignment(HSSFCellStyle.ALIGN_RIGHT);                  // 左右居右
	          right.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);       // 上下居中    
	          right.setWrapText(true);                                          // 自动换行  
	          right.setBorderTop(HSSFCellStyle.BORDER_THIN);
	          right.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	          right.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	          right.setBorderRight(HSSFCellStyle.BORDER_THIN);
	          
		        //货币格式
		      HSSFCellStyle cellMoney=efw.getWorkbook().createCellStyle();
		      HSSFDataFormat format=efw.getWorkbook().createDataFormat();
		      cellMoney.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		      cellMoney.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
		      cellMoney.setBorderTop(HSSFCellStyle.BORDER_THIN);
		      cellMoney.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		      cellMoney.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		      cellMoney.setBorderRight(HSSFCellStyle.BORDER_THIN);
		      cellMoney.setDataFormat(format.getFormat("#,###,##0.00")); 	
		      
	          
				HSSFRow row1=sheet.createRow(0);
				HSSFCell cell0=row1.createCell(0);
				cell0.setCellValue("客户名称");
				cell0.setCellStyle(headStyle1);
				
				HSSFCell cell1=row1.createCell(1);
				cell1.setCellValue("合同号");
				cell1.setCellStyle(headStyle1);
				
				HSSFCell cell2=row1.createCell(2);
				cell2.setCellValue("起租日");
				cell2.setCellStyle(headStyle1);
				
				HSSFCell cell3=row1.createCell(3);
				cell3.setCellValue("拨款日");
				cell3.setCellStyle(headStyle1);
				
				HSSFCell cell4=row1.createCell(4);
				cell4.setCellValue("期数");
				cell4.setCellStyle(headStyle1);
				
				HSSFCell cell5=row1.createCell(5);
				cell5.setCellValue("支付日");
				cell5.setCellStyle(headStyle1);
				
				HSSFCell cell6=row1.createCell(6);
				cell6.setCellValue("利息/未税利息");
				cell6.setCellStyle(headStyle1);
			    
				int index=0;
				if(lst.size()>0){
				for(int i=0;i<lst.size();i++){
					index++;
					
					HSSFRow row2=sheet.createRow(index);
					HSSFCell cellm0=row2.createCell(0);
					cellm0.setCellValue(lst.get(i).get("CUST_NAME").toString());
					cellm0.setCellStyle(center);
					
					HSSFCell cellm1=row2.createCell(1);
					cellm1.setCellValue(lst.get(i).get("LEASE_CODE").toString());
					cellm1.setCellStyle(center);
					
					HSSFCell cellm2=row2.createCell(2);
					cellm2.setCellValue(lst.get(i).get("START_DATE").toString());
					cellm2.setCellStyle(center);
					
					HSSFCell cellm3=row2.createCell(3);
					cellm3.setCellValue(lst.get(i).get("FINANCECONTRACT_DATE").toString());
					cellm3.setCellStyle(center);
					
					HSSFCell cellm4=row2.createCell(4);
					cellm4.setCellValue(lst.get(i).get("PERIOD_NUM").toString());
					cellm4.setCellStyle(center);
					
					HSSFCell cellm5=row2.createCell(5);
					cellm5.setCellValue(lst.get(i).get("PAY_DATE").toString());
					cellm5.setCellStyle(center);
					
					HSSFCell cellm6=row2.createCell(6);
					double d=Double.parseDouble(lst.get(i).get("REN_PRICE").toString());
					cellm6.setCellValue(d);
					cellm6.setCellStyle(cellMoney);
				}
				}
				index++;
		        //合并单元格
		        CellRangeAddress rang=null;
		        rang=new CellRangeAddress(index, index, 0, 6);
		        sheet.addMergedRegion(rang);
		       
		       SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		       HSSFRow row3= sheet.createRow(index);
		       HSSFCell cell30=row3.createCell(0);
		       cell30.setCellValue(df.format(new Date()));
			return efw.getWorkbook();
	   }
	  
	   //导出增值税本金发票开具明细
	   public HSSFWorkbook exportInvoice(Context context,List<HashMap<String,Object>> lst) throws Exception{
		   ExcelFileWriter efw=new ExcelFileWriter();
			HSSFSheet sheet=efw.createSheet(context.contextMap.get("sheetName")==null?
		  			  "summary":(String)context.contextMap.get("sheetName"));
			  
			  sheet.setColumnWidth(1, 9000);
			  sheet.setColumnWidth(2, 5000);
			  sheet.setColumnWidth(3, 4000);
			  sheet.setColumnWidth(4, 4000);
			  sheet.setColumnWidth(5, 4000);
			  sheet.setColumnWidth(6, 4000);
			
	          HSSFFont headFont0=null;              
	          HSSFCellStyle headStyle0=null;
	        
	          headFont0=efw.getWorkbook().createFont();
	          headFont0.setFontHeightInPoints((short)15);                            //设置字体大小
	          headFont0.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);                     // 加粗   
	        
	          headStyle0=efw.getWorkbook().createCellStyle();                       //设置样式
	          headStyle0.setAlignment(HSSFCellStyle.ALIGN_CENTER);                  // 左右居中   
	          headStyle0.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);       // 上下居中    
	          headStyle0.setWrapText(true);                                          // 自动换行  
	          headStyle0.setFont(headFont0);
	          
	          //设置表头样式 
	          HSSFFont headFont1=null;              
	          HSSFCellStyle headStyle1=null;
	        
	          headFont1=efw.getWorkbook().createFont();
	          headFont1.setFontHeightInPoints((short)11);                            //设置字体大小
	          headFont1.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);                     // 加粗   
	        
	          headStyle1=efw.getWorkbook().createCellStyle();                       //设置样式
	          headStyle1.setAlignment(HSSFCellStyle.ALIGN_CENTER);                  // 左右居中   
	          headStyle1.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);       // 上下居中    
	          headStyle1.setWrapText(true);                                          // 自动换行  
	          headStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
	          headStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	          headStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	          headStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
	          headStyle1.setFont(headFont1);
	          
	          //设置 样式 (居中)
	          HSSFCellStyle center=null;
	          center=efw.getWorkbook().createCellStyle();                       //设置样式
	          center.setAlignment(HSSFCellStyle.ALIGN_CENTER);                  // 左右居中   
	          center.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);       // 上下居中    
	          center.setWrapText(true);                                          // 自动换行  
	          center.setBorderTop(HSSFCellStyle.BORDER_THIN);
	          center.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	          center.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	          center.setBorderRight(HSSFCellStyle.BORDER_THIN);
	          
	          //设置 样式 (居中)
	          HSSFCellStyle right=null;
	          right=efw.getWorkbook().createCellStyle();                       //设置样式
	          right.setAlignment(HSSFCellStyle.ALIGN_RIGHT);                  // 左右居右
	          right.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);       // 上下居中    
	          right.setWrapText(true);                                          // 自动换行  
	          right.setBorderTop(HSSFCellStyle.BORDER_THIN);
	          right.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	          right.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	          right.setBorderRight(HSSFCellStyle.BORDER_THIN);
			
	        //合并单元格
	        CellRangeAddress rang=null;
	        rang=new CellRangeAddress(0, 0, 0, 6);
	        sheet.addMergedRegion(rang);
			HSSFRow row0=sheet.createRow(0);
			row0.setHeight((short)400);
			HSSFCell cellHead=row0.createCell(0);
			cellHead.setCellValue("本金发票开具明细表(月报表)");
			cellHead.setCellStyle(headStyle0);
			
			HSSFRow row1=sheet.createRow(1);
			HSSFCell cell0=row1.createCell(0);
			cell0.setCellValue("序号");
			cell0.setCellStyle(headStyle1);
			
			HSSFCell cell1=row1.createCell(1);
			cell1.setCellValue("客户");
			cell1.setCellStyle(headStyle1);
			
			HSSFCell cell2=row1.createCell(2);
			cell2.setCellValue("合同号");
			cell2.setCellStyle(headStyle1);
			
			HSSFCell cell3=row1.createCell(3);
			cell3.setCellValue("国别地区");
			cell3.setCellStyle(headStyle1);
			
			HSSFCell cell4=row1.createCell(4);
			cell4.setCellValue("本金未税");
			cell4.setCellStyle(headStyle1);
			
			HSSFCell cell5=row1.createCell(5);
			cell5.setCellValue("本金税金");
			cell5.setCellStyle(headStyle1);
			
			HSSFCell cell6=row1.createCell(6);
			cell6.setCellValue("本金含税");
			cell6.setCellStyle(headStyle1);
			
			int index=1;
			for(int i=0;i<lst.size();i++){
				HSSFRow row2=sheet.createRow(i+2);
				HSSFCell cellm0=row2.createCell(0);
				cellm0.setCellValue(index);
				cellm0.setCellStyle(center);
				
				HSSFCell cellm1=row2.createCell(1);
				cellm1.setCellValue(lst.get(i).get("CUST_NAME").toString());
				cellm1.setCellStyle(center);
				
				HSSFCell cellm2=row2.createCell(2);
				cellm2.setCellValue(lst.get(i).get("LEASE_CODE").toString());
				cellm2.setCellStyle(center);
				
				HSSFCell cellm3=row2.createCell(3);
				cellm3.setCellValue(lst.get(i).get("DECP_NAME_CN").toString());
				cellm3.setCellStyle(center);
				
				HSSFCell cellm4=row2.createCell(4);
				cellm4.setCellValue(lst.get(i).get("princpleOutstandingTax").toString());
				cellm4.setCellStyle(right);
				
				HSSFCell cellm5=row2.createCell(5);
				cellm5.setCellValue(lst.get(i).get("tax").toString());
				cellm5.setCellStyle(right);
				
				HSSFCell cellm6=row2.createCell(6);
				cellm6.setCellValue(lst.get(i).get("OWN_PRICE").toString());
				cellm6.setCellStyle(right);
				index++;
			}
				
			return efw.getWorkbook();
	   }
	   
	   //导出增值税抵减明细
	   public HSSFWorkbook exportVATOffsetAmountInvoice(Context context,List<HashMap<String,Object>> lst) throws Exception{
		   ExcelFileWriter efw=new ExcelFileWriter();
			HSSFSheet sheet=efw.createSheet(context.contextMap.get("sheetName")==null?
		  			  "summary":(String)context.contextMap.get("sheetName"));
			  
			  sheet.setColumnWidth(0, 2000);
			  sheet.setColumnWidth(1, 7000);
			  sheet.setColumnWidth(2, 9000);
			  sheet.setColumnWidth(3, 4000);
			  sheet.setColumnWidth(4, 4000);
			  sheet.setColumnWidth(5, 4000);
			  sheet.setColumnWidth(6, 3700);
			  sheet.setColumnWidth(7, 4000);
			  sheet.setColumnWidth(8, 5000);
			  sheet.setColumnWidth(9, 4000);
			
	          HSSFFont headFont0=null;              
	          HSSFCellStyle headStyle0=null;
	        
	          headFont0=efw.getWorkbook().createFont();
	          headFont0.setFontHeightInPoints((short)15);                            //设置字体大小
	          headFont0.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);                     // 加粗   
	        
	          headStyle0=efw.getWorkbook().createCellStyle();                       //设置样式
	          headStyle0.setAlignment(HSSFCellStyle.ALIGN_CENTER);                  // 左右居中   
	          headStyle0.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);       // 上下居中    
	          headStyle0.setWrapText(true);                                          // 自动换行  
	          headStyle0.setFont(headFont0);
	          
	          //设置表头样式 
	          HSSFFont headFont1=null;              
	          HSSFCellStyle headStyle1=null;
	        
	          headFont1=efw.getWorkbook().createFont();
	          headFont1.setFontHeightInPoints((short)11);                            //设置字体大小
	          headFont1.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);                     // 加粗   
	        
	          headStyle1=efw.getWorkbook().createCellStyle();                       //设置样式
	          headStyle1.setAlignment(HSSFCellStyle.ALIGN_CENTER);                  // 左右居中   
	          headStyle1.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);       // 上下居中    
	          headStyle1.setWrapText(true);                                          // 自动换行  
	          headStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
	          headStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	          headStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	          headStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
	          headStyle1.setFont(headFont1);
	          
	          //设置 样式 (居中)
	          HSSFCellStyle center=null;
	          center=efw.getWorkbook().createCellStyle();                       //设置样式
	          center.setAlignment(HSSFCellStyle.ALIGN_CENTER);                  // 左右居中   
	          center.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);       // 上下居中    
	          center.setWrapText(true);                                          // 自动换行  
	          center.setBorderTop(HSSFCellStyle.BORDER_THIN);
	          center.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	          center.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	          center.setBorderRight(HSSFCellStyle.BORDER_THIN);
	          
	          //设置 样式 (居中)
	          HSSFCellStyle right=null;
	          right=efw.getWorkbook().createCellStyle();                       //设置样式
	          right.setAlignment(HSSFCellStyle.ALIGN_RIGHT);                  // 左右居右
	          right.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);       // 上下居中    
	          right.setWrapText(true);                                          // 自动换行  
	          right.setBorderTop(HSSFCellStyle.BORDER_THIN);
	          right.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	          right.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	          right.setBorderRight(HSSFCellStyle.BORDER_THIN);
			
	        //合并单元格
	        CellRangeAddress rang=null;
	        rang=new CellRangeAddress(0, 0, 0, 9);
	        sheet.addMergedRegion(rang);
			HSSFRow row0=sheet.createRow(0);
			row0.setHeight((short)400);
			HSSFCell cellHead=row0.createCell(0);
			cellHead.setCellValue("应税服务减除项目清单(本金)");
			cellHead.setCellStyle(headStyle0);
			
			HSSFRow row1=sheet.createRow(1);
			HSSFCell cell0=row1.createCell(0);
			cell0.setCellValue("序号");
			cell0.setCellStyle(headStyle1);
			
			HSSFCell cell1=row1.createCell(1);
			cell1.setCellValue("开票方纳税人识别号");
			cell1.setCellStyle(headStyle1);
			
			HSSFCell cell2=row1.createCell(2);
			cell2.setCellValue("开票方单位名称");
			cell2.setCellStyle(headStyle1);
			
			HSSFCell cell3=row1.createCell(3);
			cell3.setCellValue("凭证种类");
			cell3.setCellStyle(headStyle1);
			
			HSSFCell cell4=row1.createCell(4);
			cell4.setCellValue("发票代码");
			cell4.setCellStyle(headStyle1);
			
			HSSFCell cell5=row1.createCell(5);
			cell5.setCellValue("发票号码");
			cell5.setCellStyle(headStyle1);
			
			HSSFCell cell6=row1.createCell(6);
			cell6.setCellValue("服务项目名称");
			cell6.setCellStyle(headStyle1);
			
			HSSFCell cell7=row1.createCell(7);
			cell7.setCellValue("金额");
			cell7.setCellStyle(headStyle1);
			
			HSSFCell cell8=row1.createCell(8);
			cell8.setCellValue("合同号");
			cell8.setCellStyle(headStyle1);
			
			HSSFCell cell9=row1.createCell(9);
			cell9.setCellValue("国别地区");
			cell9.setCellStyle(headStyle1);
			
			int index=1;
			for(int i=0;i<lst.size();i++){
				HSSFRow row2=sheet.createRow(i+2);
				HSSFCell cellm0=row2.createCell(0);
				cellm0.setCellValue(index);
				cellm0.setCellStyle(center);
				
				HSSFCell cellm1=row2.createCell(1);
				cellm1.setCellValue(lst.get(i).get("CORP_TAX_CODE")==null?"":lst.get(i).get("CORP_TAX_CODE").toString());
				cellm1.setCellStyle(center);
				
				HSSFCell cellm2=row2.createCell(2);
				cellm2.setCellValue(lst.get(i).get("CUST_NAME")==null?"":lst.get(i).get("CUST_NAME").toString());
				cellm2.setCellStyle(center);
				
				HSSFCell cellm3=row2.createCell(3);
				cellm3.setCellValue(lst.get(i).get("taxType").toString());
				cellm3.setCellStyle(center);
				
				HSSFCell cellm4=row2.createCell(4);
				cellm4.setCellValue(lst.get(i).get("RECD_ID").toString());
				cellm4.setCellStyle(center);
				
				HSSFCell cellm5=row2.createCell(5);
				cellm5.setCellValue(lst.get(i).get("INVOICE_CODE")==null?"":lst.get(i).get("INVOICE_CODE").toString());
				cellm5.setCellStyle(center);
					
				HSSFCell cellm6=row2.createCell(6);
				cellm6.setCellValue(lst.get(i).get("projectName").toString());
				cellm6.setCellStyle(center);
				
				HSSFCell cellm7=row2.createCell(7);
				cellm7.setCellValue(lst.get(i).get("LEASE_TOPRIC").toString());
				cellm7.setCellStyle(right);
				
				HSSFCell cellm8=row2.createCell(8);
				cellm8.setCellValue(lst.get(i).get("LEASE_CODE").toString());
				cellm8.setCellStyle(center);
				
				HSSFCell cellm9=row2.createCell(9);
				cellm9.setCellValue(lst.get(i).get("DECP_NAME_CN").toString());
				cellm9.setCellStyle(center);
				index++;
			}
				
			return efw.getWorkbook();
	   }
	   
	   //导出增值税抵减余额变动(月报表)
	   public HSSFWorkbook exportVATBalanceInvoice(Context context,List<HashMap<String,Object>> lst) throws Exception{
		   ExcelFileWriter efw=new ExcelFileWriter();
			HSSFSheet sheet=efw.createSheet(context.contextMap.get("sheetName")==null?
		  			  "summary":(String)context.contextMap.get("sheetName"));
			  
			  sheet.setColumnWidth(0, 2000);
			  sheet.setColumnWidth(1, 7000);
			  sheet.setColumnWidth(2, 9000);
			  sheet.setColumnWidth(3, 4000);
			  sheet.setColumnWidth(4, 4000);
			  sheet.setColumnWidth(5, 4000);
			  sheet.setColumnWidth(6, 3700);
			  sheet.setColumnWidth(7, 4000);
			  sheet.setColumnWidth(8, 5000);
			  sheet.setColumnWidth(9, 4000);
			  sheet.setColumnWidth(10, 4000);
			  sheet.setColumnWidth(11, 4000);
			
	          HSSFFont headFont0=null;              
	          HSSFCellStyle headStyle0=null;
	        
	          headFont0=efw.getWorkbook().createFont();
	          headFont0.setFontHeightInPoints((short)15);                            //设置字体大小
	          headFont0.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);                     // 加粗   
	        
	          headStyle0=efw.getWorkbook().createCellStyle();                       //设置样式
	          headStyle0.setAlignment(HSSFCellStyle.ALIGN_CENTER);                  // 左右居中   
	          headStyle0.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);       // 上下居中    
	          headStyle0.setWrapText(true);                                          // 自动换行  
	          headStyle0.setFont(headFont0);
	          
	          //设置表头样式 
	          HSSFFont headFont1=null;              
	          HSSFCellStyle headStyle1=null;
	        
	          headFont1=efw.getWorkbook().createFont();
	          headFont1.setFontHeightInPoints((short)11);                            //设置字体大小
	          headFont1.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);                     // 加粗   
	        
	          headStyle1=efw.getWorkbook().createCellStyle();                       //设置样式
	          headStyle1.setAlignment(HSSFCellStyle.ALIGN_CENTER);                  // 左右居中   
	          headStyle1.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);       // 上下居中    
	          headStyle1.setWrapText(true);                                          // 自动换行  
	          headStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
	          headStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	          headStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	          headStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
	          headStyle1.setFont(headFont1);
	          
	          //设置 样式 (居中)
	          HSSFCellStyle center=null;
	          center=efw.getWorkbook().createCellStyle();                       //设置样式
	          center.setAlignment(HSSFCellStyle.ALIGN_CENTER);                  // 左右居中   
	          center.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);       // 上下居中    
	          center.setWrapText(true);                                          // 自动换行  
	          center.setBorderTop(HSSFCellStyle.BORDER_THIN);
	          center.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	          center.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	          center.setBorderRight(HSSFCellStyle.BORDER_THIN);
	          
	          //设置 样式 (居中)
	          HSSFCellStyle right=null;
	          right=efw.getWorkbook().createCellStyle();                       //设置样式
	          right.setAlignment(HSSFCellStyle.ALIGN_RIGHT);                  // 左右居右
	          right.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);       // 上下居中    
	          right.setWrapText(true);                                          // 自动换行  
	          right.setBorderTop(HSSFCellStyle.BORDER_THIN);
	          right.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	          right.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	          right.setBorderRight(HSSFCellStyle.BORDER_THIN);
			
	        //合并单元格
	        CellRangeAddress rang=null;
	        rang=new CellRangeAddress(0, 0, 0, 11);
	        sheet.addMergedRegion(rang);
			HSSFRow row0=sheet.createRow(0);
			row0.setHeight((short)400);
			HSSFCell cellHead=row0.createCell(0);
			cellHead.setCellValue("应税服务减除项目清单(本金)");
			cellHead.setCellStyle(headStyle0);
			
			HSSFRow row1=sheet.createRow(1);
			HSSFCell cell0=row1.createCell(0);
			cell0.setCellValue("序号");
			cell0.setCellStyle(headStyle1);
			
			HSSFCell cell1=row1.createCell(1);
			cell1.setCellValue("开票方纳税人识别号");
			cell1.setCellStyle(headStyle1);
			
			HSSFCell cell2=row1.createCell(2);
			cell2.setCellValue("开票方单位名称");
			cell2.setCellStyle(headStyle1);
			
			HSSFCell cell3=row1.createCell(3);
			cell3.setCellValue("凭证种类");
			cell3.setCellStyle(headStyle1);
			
			HSSFCell cell4=row1.createCell(4);
			cell4.setCellValue("发票代码");
			cell4.setCellStyle(headStyle1);
			
			HSSFCell cell5=row1.createCell(5);
			cell5.setCellValue("发票号码");
			cell5.setCellStyle(headStyle1);
			
			HSSFCell cell6=row1.createCell(6);
			cell6.setCellValue("服务项目名称");
			cell6.setCellStyle(headStyle1);
			
			HSSFCell cell7=row1.createCell(7);
			cell7.setCellValue("金额");
			cell7.setCellStyle(headStyle1);
			
			HSSFCell cell8=row1.createCell(8);
			cell8.setCellValue("合同号");
			cell8.setCellStyle(headStyle1);
			
			HSSFCell cell9=row1.createCell(9);
			cell9.setCellValue("国别地区");
			cell9.setCellStyle(headStyle1);
			
			HSSFCell cell10=row1.createCell(10);
			cell10.setCellValue("已抵减金额");
			cell10.setCellStyle(headStyle1);
			
			HSSFCell cell11=row1.createCell(11);
			cell11.setCellValue("未抵减金额");
			cell11.setCellStyle(headStyle1);
			
			int index=1;
			for(int i=0;i<lst.size();i++){
				HSSFRow row2=sheet.createRow(i+2);
				HSSFCell cellm0=row2.createCell(0);
				cellm0.setCellValue(index);
				cellm0.setCellStyle(center);
				
				HSSFCell cellm1=row2.createCell(1);
				cellm1.setCellValue(lst.get(i).get("CORP_TAX_CODE")==null?"":lst.get(i).get("CORP_TAX_CODE").toString());
				cellm1.setCellStyle(center);
				
				HSSFCell cellm2=row2.createCell(2);
				cellm2.setCellValue(lst.get(i).get("CUST_NAME")==null?"":lst.get(i).get("CUST_NAME").toString());
				cellm2.setCellStyle(center);
				
				HSSFCell cellm3=row2.createCell(3);
				cellm3.setCellValue(lst.get(i).get("taxType").toString());
				cellm3.setCellStyle(center);
				
				HSSFCell cellm4=row2.createCell(4);
				cellm4.setCellValue(lst.get(i).get("RECD_ID").toString());
				cellm4.setCellStyle(center);
				
				HSSFCell cellm5=row2.createCell(5);
				cellm5.setCellValue(lst.get(i).get("INVOICE_CODE")==null?"":lst.get(i).get("INVOICE_CODE").toString());
				cellm5.setCellStyle(center);
				
				HSSFCell cellm6=row2.createCell(6);
				cellm6.setCellValue(lst.get(i).get("projectName").toString());
				cellm6.setCellStyle(center);
				
				HSSFCell cellm7=row2.createCell(7);
				cellm7.setCellValue(lst.get(i).get("LEASE_TOPRIC").toString());
				cellm7.setCellStyle(right);
				
				HSSFCell cellm8=row2.createCell(8);
				cellm8.setCellValue(lst.get(i).get("LEASE_CODE").toString());
				cellm8.setCellStyle(center);
				
				HSSFCell cellm9=row2.createCell(9);
				cellm9.setCellValue(lst.get(i).get("DECP_NAME_CN").toString());
				cellm9.setCellStyle(center);
				
				HSSFCell cellm10=row2.createCell(10);
				cellm10.setCellValue(lst.get(i).get("BALANCE").toString());
				cellm10.setCellStyle(right);
				
				HSSFCell cellm11=row2.createCell(11);
				cellm11.setCellValue(lst.get(i).get("LAST_PRICE").toString());
				cellm11.setCellStyle(right);
				index++;
			}
				
			return efw.getWorkbook();
	   }
}
