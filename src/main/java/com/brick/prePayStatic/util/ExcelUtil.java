package com.brick.prePayStatic.util;

import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jxl.demo.Demo;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;

import com.brick.prePayStatic.to.prePayStaticForDeptTo;
import com.brick.prePayStatic.to.prePayTotalForDeptTo;
import com.brick.service.core.DataAccessor;
import com.brick.service.entity.Context;
import com.brick.util.poi.ExcelFileWriter;
import com.ibm.icu.text.SimpleDateFormat;

public class ExcelUtil {
    
	      public HSSFWorkbook generateReport(Map<String,Object> params,Context context) throws Exception {
		      
	    	  
	    	  @SuppressWarnings("unchecked")
			List<prePayStaticForDeptTo> list=(List<prePayStaticForDeptTo>)params.get("dataList");
			prePayTotalForDeptTo p=(prePayTotalForDeptTo)params.get("p");
	    	  
	    	  ExcelFileWriter efw=new ExcelFileWriter();
	    	  //设置表名
	    	  HSSFSheet sheet= efw.createSheet(context.contextMap.get("sheetName")==null?"summary"
	    			  :(String)context.contextMap.get("sheetName"));

	    	        
	    	        //设置列宽
	    	        sheet.setColumnWidth(0, 3500);      
	    	        sheet.setColumnWidth(1, 3300);
	    	        sheet.setColumnWidth(3, 3300);
	    	        sheet.setColumnWidth(6, 3300);
	    	        sheet.setColumnWidth(8, 3300);
	    	        sheet.setColumnWidth(11, 3300);
	    	        sheet.setColumnWidth(13, 3300);
	    	        sheet.setColumnWidth(16, 3300);
	    	        sheet.setColumnWidth(18, 3300);
	    	        sheet.setColumnWidth(21, 3300);
	    	        sheet.setColumnWidth(23, 3300);
	    	        sheet.setColumnWidth(26, 3300);
	    	        sheet.setColumnWidth(28, 3300);
	    	        sheet.setColumnWidth(31, 3300);
	    	        sheet.setColumnWidth(33, 3300);
	    	        sheet.setColumnWidth(36, 3300);
	    	        sheet.setColumnWidth(38, 3300);
	    	        sheet.setColumnWidth(41, 3300);
	    	        sheet.setColumnWidth(43, 3300);
	    	        sheet.setColumnWidth(46, 3300);
	    	        sheet.setColumnWidth(48, 3300);
	    	        sheet.setColumnWidth(51, 3300);
	    	        sheet.setColumnWidth(53, 3300);
	    	        sheet.setColumnWidth(56, 3300);
	    	        sheet.setColumnWidth(58, 3300);
	    	  
	    	        HSSFFont headFont0=null;              
	    	        HSSFCellStyle headStyle0=null;
	    	        
	    	        headFont0=efw.getWorkbook().createFont();
	    	        headFont0.setFontHeightInPoints((short)20);                            //设置字体大小
	    	        headFont0.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);                     // 加粗   
	    	        
	    	        headStyle0=efw.getWorkbook().createCellStyle();                       //设置样式
	    	        headStyle0.setAlignment(HSSFCellStyle.ALIGN_CENTER);                  // 左右居中   
	    	        headStyle0.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);       // 上下居中    
	    	        headStyle0.setWrapText(true);                                          // 自动换行  
	    	        headStyle0.setFont(headFont0);
	    	        
	    	        HSSFRow row0=sheet.createRow(0);                                         //设置第一行
	    	        row0.setHeight((short)600);                                             //设置第一行的高度
	    	        HSSFCell cell0=row0.createCell(0);
	    	        cell0.setCellValue("交机前拨款统计表");
                    cell0.setCellStyle(headStyle0);
                    
	    	        //设置另外一个样式
                    HSSFCellStyle headStyle=efw.getWorkbook().createCellStyle();
                    HSSFFont headFont=efw.getWorkbook().createFont();
	    	        headFont.setFontHeightInPoints((short)10);  
	    	        headFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	    	        headStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);                  // 左右居中   
	    	        headStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);       // 上下居中    
	    	        headStyle.setWrapText(true);                                          // 自动换行  
	    	        headStyle.setFont(headFont);
	    	        
	    	        
	    	        //合并单元格
	    	        CellRangeAddress rang=null;
	    	        rang=new CellRangeAddress(0, 0, 0, 60);
	    	        sheet.addMergedRegion(rang);
	    	        
                    
	    	        headFont.setFontHeightInPoints((short)10);  
	    	        headFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	    	        headStyle.setFont(headFont);
	    	        
	    	        HSSFRow row1=sheet.createRow(1);
	    	        HSSFCell cell10=row1.createCell(0);           //第二行第一列
	    	        cell10.setCellValue("地区");
	    	        rang=new CellRangeAddress(1, 2, 0, 0);
	    	        sheet.addMergedRegion(rang);
	    	        cell10.setCellStyle(headStyle);
	    	        
	    	        HSSFCell cell11=row1.createCell(1);
	    	        cell11.setCellValue("1月");
	    	        rang=new CellRangeAddress(1,1,1,5);
	    	        sheet.addMergedRegion(rang);
	    	        cell11.setCellStyle(headStyle);
	    	        
	    	        HSSFCell cell12=row1.createCell(6);
	    	        cell12.setCellValue("2月");
	    	        rang=new CellRangeAddress(1,1,6,10);
	    	        sheet.addMergedRegion(rang);
	    	        cell12.setCellStyle(headStyle);
	    	        
	    	        HSSFCell cell13=row1.createCell(11);
	    	        cell13.setCellValue("3月");
	    	        rang=new CellRangeAddress(1,1,11,15);
	    	        sheet.addMergedRegion(rang);
	    	        cell13.setCellStyle(headStyle);
	    	        
	    	        HSSFCell cell14=row1.createCell(16);
	    	        cell14.setCellValue("4月");
	    	        rang=new CellRangeAddress(1,1,16,20);
	    	        sheet.addMergedRegion(rang);
	    	        cell14.setCellStyle(headStyle);
	    	        
	    	        HSSFCell cell15=row1.createCell(21);
	    	        cell15.setCellValue("5月");
	    	        rang=new CellRangeAddress(1,1,21,25);
	    	        sheet.addMergedRegion(rang);
	    	        cell15.setCellStyle(headStyle);
	    	        
	    	        HSSFCell cell16=row1.createCell(26);
	    	        cell16.setCellValue("6月");
	    	        rang=new CellRangeAddress(1,1,26,30);
	    	        sheet.addMergedRegion(rang);
	    	        cell16.setCellStyle(headStyle);
	    	        
	    	        HSSFCell cell17=row1.createCell(31);
	    	        cell17.setCellValue("7月");
	    	        rang=new CellRangeAddress(1,1,31,35);
	    	        sheet.addMergedRegion(rang);
	    	        cell17.setCellStyle(headStyle);
	    	        
	    	        HSSFCell cell18=row1.createCell(36);
	    	        cell18.setCellValue("8月");
	    	        rang=new CellRangeAddress(1,1,36,40);
	    	        sheet.addMergedRegion(rang);
	    	        cell18.setCellStyle(headStyle);
	    	        
	    	        HSSFCell cell19=row1.createCell(41);
	    	        cell19.setCellValue("9月");
	    	        rang=new CellRangeAddress(1,1,41,45);
	    	        sheet.addMergedRegion(rang);
	    	        cell19.setCellStyle(headStyle);
	    	        
	    	        HSSFCell cell110=row1.createCell(46);
	    	        cell110.setCellValue("10月");
	    	        rang=new CellRangeAddress(1,1,46,50);
	    	        sheet.addMergedRegion(rang);
	    	        cell110.setCellStyle(headStyle);
	    	        
	    	        HSSFCell cell111=row1.createCell(51);
	    	        cell111.setCellValue("11月");
	    	        rang=new CellRangeAddress(1,1,51,55);
	    	        sheet.addMergedRegion(rang);
	    	        cell111.setCellStyle(headStyle);
	    	        
	    	        HSSFCell cell112=row1.createCell(56);
	    	        cell112.setCellValue("12月");
	    	        rang=new CellRangeAddress(1,1,56,60);
	    	        sheet.addMergedRegion(rang);
	    	        cell112.setCellStyle(headStyle);
	    	        
	    	        HSSFRow row2=sheet.createRow(2);
	    	        row2.setHeight((short)1000);
	    	        int index=0;
	    	        headStyle.setWrapText(true);
	    	        for(int i=0;i<12;i++){
	    	        	index++;
	    	        	HSSFCell cell21=row2.createCell(index);
	    	        	cell21.setCellValue("总拨款金额");
	    	        	cell21.setCellStyle(headStyle);
	    	        	index++;
	    	        	HSSFCell cell22=row2.createCell(index);
	    	        	cell22.setCellValue("总拨款件数");
	    	        	cell22.setCellStyle(headStyle);
	    	        	index++;
	    	        	HSSFCell cell23=row2.createCell(index);
	    	        	cell23.setCellValue("交机前拨款金额");
	    	        	cell23.setCellStyle(headStyle);
	    	        	index++;
	    	        	HSSFCell cell24=row2.createCell(index);
	    	        	cell24.setCellValue("交机前拨款件数");
	    	        	cell24.setCellStyle(headStyle);
	    	        	index++;
	    	        	HSSFCell cell25=row2.createCell(index);
	    	        	cell25.setCellValue("交机前拨款金额占比");
	    	        	cell25.setCellStyle(headStyle);
	    	        }
	    	        
	    	        int rownum=2;
	    	       HSSFCellStyle headStyle2=efw.getWorkbook().createCellStyle();                       //设置样式
	    	        headStyle2.setAlignment(HSSFCellStyle.ALIGN_CENTER);                               // 左右居中   
	    	        headStyle2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
	    	        
	    	        HSSFCellStyle headStyle3=efw.getWorkbook().createCellStyle();
	    	        headStyle3.setAlignment(HSSFCellStyle.ALIGN_RIGHT);                  
	    	        headStyle3.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);  
	    	        
	    	        //百分比
/*	    	        HSSFCellStyle cellPercent=efw.getWorkbook().createCellStyle();
	    	        cellPercent.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
	    	        cellPercent.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
	    	       cellPercent.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));*/
	    	        
	    	        //货币格式
	    	        HSSFCellStyle cellMoney=efw.getWorkbook().createCellStyle();
	    	        HSSFDataFormat format=efw.getWorkbook().createDataFormat();
	    	        cellMoney.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
	    	        cellMoney.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
	    	        cellMoney.setDataFormat(format.getFormat("#,###,##0")); 	
	    	        
	    	        //格式化
	    	       // NumberFormat fm=NumberFormat.getNumberInstance(Locale.CHINA);
	    	        for(int i=0;i<list.size();i++){
	    	        	rownum++;
	    	        	HSSFRow row=sheet.createRow(rownum);
	    	        	HSSFCell cellk0=row.createCell(0);
	    	        	cellk0.setCellValue(list.get(i).getName());
	    	        	cellk0.setCellStyle(headStyle2);
	    	        	
	    	        	HSSFCell cellk1=row.createCell(1);
	    	        	cellk1.setCellValue(list.get(i).getActureOfJan());
	    	        	cellk1.setCellStyle(cellMoney);
	    	        	
	    	        	HSSFCell cellk2=row.createCell(2);
	    	        	cellk2.setCellValue(list.get(i).getActrueNumOfJan());
	    	        	cellk2.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk3=row.createCell(3);
	    	        	cellk3.setCellValue(list.get(i).getPrePayOfJan());
	    	        	cellk3.setCellStyle(cellMoney);
	    	        	
	    	        	HSSFCell cellk4=row.createCell(4);
	    	        	cellk4.setCellValue(list.get(i).getPrePayNumOfJan());
	    	        	cellk4.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk5=row.createCell(5);
	    	        	cellk5.setCellValue(list.get(i).getScaleOfJan());
	    	        	cellk5.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk6=row.createCell(6);
	    	        	cellk6.setCellValue(list.get(i).getActureOfFeb());
	    	        	cellk6.setCellStyle(cellMoney);
	    	        	
	    	        	HSSFCell cellk7=row.createCell(7);
	    	        	cellk7.setCellValue(list.get(i).getActrueNumOfFeb());
	    	        	cellk7.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk8=row.createCell(8);
	    	        	cellk8.setCellValue(list.get(i).getPrePayOfFeb());
	    	        	cellk8.setCellStyle(cellMoney);
	    	        	
	    	        	HSSFCell cellk9=row.createCell(9);
	    	        	cellk9.setCellValue(list.get(i).getPrePayNumOfFeb());
	    	        	cellk9.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk10=row.createCell(10);
	    	        	cellk10.setCellValue(list.get(i).getScaleOfFeb());
	    	        	cellk10.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk11=row.createCell(11);
	    	        	cellk11.setCellValue(list.get(i).getActureOfMar());
	    	        	cellk11.setCellStyle(cellMoney);
	    	        	
	    	        	HSSFCell cellk12=row.createCell(12);
	    	        	cellk12.setCellValue(list.get(i).getActrueNumOfMar());
	    	        	cellk12.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk13=row.createCell(13);
	    	        	cellk13.setCellValue(list.get(i).getPrePayOfMar());
	    	        	cellk13.setCellStyle(cellMoney);
	    	        	
	    	        	HSSFCell cellk14=row.createCell(14);
	    	        	cellk14.setCellValue(list.get(i).getPrePayNumOfMar());
	    	        	cellk14.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk15=row.createCell(15);
	    	        	cellk15.setCellValue(list.get(i).getScaleOfMar());
	    	        	cellk15.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk16=row.createCell(16);
	    	        	cellk16.setCellValue(list.get(i).getActureOfApr());
	    	        	cellk16.setCellStyle(cellMoney);
	    	        	
	    	        	HSSFCell cellk17=row.createCell(17);
	    	        	cellk17.setCellValue(list.get(i).getActrueNumOfApr());
	    	        	cellk17.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk18=row.createCell(18);
	    	        	cellk18.setCellValue(list.get(i).getPrePayOfApr());
	    	        	cellk18.setCellStyle(cellMoney);
	    	        	
	    	        	HSSFCell cellk19=row.createCell(19);
	    	        	cellk19.setCellValue(list.get(i).getPrePayNumOfApr());
	    	        	cellk19.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk20=row.createCell(20);
	    	        	cellk20.setCellValue(list.get(i).getScaleOfApr());
	    	        	cellk20.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk21=row.createCell(21);
	    	        	cellk21.setCellValue(list.get(i).getActureOfMay());
	    	        	cellk21.setCellStyle(cellMoney);
	    	        	
	    	        	HSSFCell cellk22=row.createCell(22);
	    	        	cellk22.setCellValue(list.get(i).getActrueNumOfMay());
	    	        	cellk22.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk23=row.createCell(23);
	    	        	cellk23.setCellValue(list.get(i).getPrePayOfMay());
	    	        	cellk23.setCellStyle(cellMoney);
	    	        	
	    	        	HSSFCell cellk24=row.createCell(24);
	    	        	cellk24.setCellValue(list.get(i).getPrePayNumOfMay());
	    	        	cellk24.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk25=row.createCell(25);
	    	        	cellk25.setCellValue(list.get(i).getScaleOfMay());
	    	        	cellk25.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk26=row.createCell(26);
	    	        	cellk26.setCellValue(list.get(i).getActureOfJun());
	    	        	cellk26.setCellStyle(cellMoney);
	    	        	
	    	        	HSSFCell cellk27=row.createCell(27);
	    	        	cellk27.setCellValue(list.get(i).getActrueNumOfJun());
	    	        	cellk27.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk28=row.createCell(28);
	    	        	cellk28.setCellValue(list.get(i).getPrePayOfJun());
	    	        	cellk28.setCellStyle(cellMoney);
	    	        	
	    	        	HSSFCell cellk29=row.createCell(29);
	    	        	cellk29.setCellValue(list.get(i).getPrePayNumOfJun());
	    	        	cellk29.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk30=row.createCell(30);
	    	        	cellk30.setCellValue(list.get(i).getScaleOfJun());
	    	        	cellk30.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk31=row.createCell(31);
	    	        	cellk31.setCellValue(list.get(i).getActureOfJul());
	    	        	cellk31.setCellStyle(cellMoney);
	    	        	
	    	        	HSSFCell cellk32=row.createCell(32);
	    	        	cellk32.setCellValue(list.get(i).getActrueNumOfJul());
	    	        	cellk32.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk33=row.createCell(33);
	    	        	cellk33.setCellValue(list.get(i).getPrePayOfJul());
	    	        	cellk33.setCellStyle(cellMoney);
	    	        	
	    	        	HSSFCell cellk34=row.createCell(34);
	    	        	cellk34.setCellValue(list.get(i).getPrePayNumOfJul());
	    	        	cellk34.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk35=row.createCell(35);
	    	        	cellk35.setCellValue(list.get(i).getScaleOfJul());
	    	        	cellk35.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk36=row.createCell(36);
	    	        	cellk36.setCellValue(list.get(i).getActureOfAus());
	    	        	cellk36.setCellStyle(cellMoney);
	    	        	
	    	        	HSSFCell cellk37=row.createCell(37);
	    	        	cellk37.setCellValue(list.get(i).getActrueNumOfAus());
	    	        	cellk37.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk38=row.createCell(38);
	    	        	cellk38.setCellValue(list.get(i).getPrePayOfAus());
	    	        	cellk38.setCellStyle(cellMoney);
	    	        	
	    	        	HSSFCell cellk39=row.createCell(39);
	    	        	cellk39.setCellValue(list.get(i).getPrePayNumOfAus());
	    	        	cellk39.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk40=row.createCell(40);
	    	        	cellk40.setCellValue(list.get(i).getScaleOfAus());
	    	        	cellk40.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk41=row.createCell(41);
	    	        	cellk41.setCellValue(list.get(i).getActureOfSep());
	    	        	cellk41.setCellStyle(cellMoney);
	    	        	
	    	        	HSSFCell cellk42=row.createCell(42);
	    	        	cellk42.setCellValue(list.get(i).getActrueNumOfSep());
	    	        	cellk42.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk43=row.createCell(43);
	    	        	cellk43.setCellValue(list.get(i).getPrePayOfSep());
	    	        	cellk43.setCellStyle(cellMoney);
	    	        	
	    	        	HSSFCell cellk44=row.createCell(44);
	    	        	cellk44.setCellValue(list.get(i).getPrePayNumOfSep());
	    	        	cellk44.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk45=row.createCell(45);
	    	        	cellk45.setCellValue(list.get(i).getScaleOfSep());
	    	        	cellk45.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk46=row.createCell(46);
	    	        	cellk46.setCellValue(list.get(i).getActureOfOct());
	    	        	cellk46.setCellStyle(cellMoney);
	    	        	
	    	        	HSSFCell cellk47=row.createCell(47);
	    	        	cellk47.setCellValue(list.get(i).getActrueNumOfOct());
	    	        	cellk47.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk48=row.createCell(48);
	    	        	cellk48.setCellValue(list.get(i).getPrePayOfOct());
	    	        	cellk48.setCellStyle(cellMoney);
	    	        	
	    	        	HSSFCell cellk49=row.createCell(49);
	    	        	cellk49.setCellValue(list.get(i).getPrePayNumOfOct());
	    	        	cellk49.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk50=row.createCell(50);
	    	        	cellk50.setCellValue(list.get(i).getScaleOfOct());
	    	        	cellk50.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk51=row.createCell(51);
	    	        	cellk51.setCellValue(list.get(i).getActureOfNov());
	    	        	cellk51.setCellStyle(cellMoney);
	    	        	
	    	        	HSSFCell cellk52=row.createCell(52);
	    	        	cellk52.setCellValue(list.get(i).getActrueNumOfNov());
	    	        	cellk52.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk53=row.createCell(53);
	    	        	cellk53.setCellValue(list.get(i).getPrePayOfNov());
	    	        	cellk53.setCellStyle(cellMoney);
	    	        	
	    	        	HSSFCell cellk54=row.createCell(54);
	    	        	cellk54.setCellValue(list.get(i).getPrePayNumOfNov());
	    	        	cellk54.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk55=row.createCell(55);
	    	        	cellk55.setCellValue(list.get(i).getScaleOfNov());
	    	        	cellk55.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk56=row.createCell(56);
	    	        	cellk56.setCellValue(list.get(i).getActureOfDec());
	    	        	cellk56.setCellStyle(cellMoney);
	    	        	
	    	        	HSSFCell cellk57=row.createCell(57);
	    	        	cellk57.setCellValue(list.get(i).getActrueNumOfDec());
	    	        	cellk57.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk58=row.createCell(58);
	    	        	cellk58.setCellValue(list.get(i).getPrePayOfDec());
	    	        	cellk58.setCellStyle(cellMoney);
	    	        	
	    	        	HSSFCell cellk59=row.createCell(59);
	    	        	cellk59.setCellValue(list.get(i).getPrePayNumOfDec());
	    	        	cellk59.setCellStyle(headStyle3);
	    	        	
	    	        	HSSFCell cellk60=row.createCell(60);
	    	        	cellk60.setCellValue(list.get(i).getScaleOfDec());
	    	        	cellk60.setCellStyle(headStyle3);
	    	        	
	    	        	
	    	        }
	    	        HSSFRow rowend=sheet.createRow(rownum+1);                  //末行
	    	        HSSFCell cellm0=rowend.createCell(0);
	    	        cellm0.setCellValue("合计");
	    	        cellm0.setCellStyle(headStyle2);
	    	        
	    	        HSSFCell cellm1=rowend.createCell(1);
	    	        cellm1.setCellValue(p.getActureSumOfJan());
	    	        cellm1.setCellStyle(cellMoney);
	    	        
	    	        HSSFCell cellm2=rowend.createCell(2);
	    	        cellm2.setCellValue(p.getActOfJan());
	    	        cellm2.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm3=rowend.createCell(3);
	    	        cellm3.setCellValue(p.getPrePaySumOfJan());
	    	        cellm3.setCellStyle(cellMoney);
	    	        
	    	        HSSFCell cellm4=rowend.createCell(4);
	    	        cellm4.setCellValue(p.getPreOfJan());
	    	        cellm4.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm5=rowend.createCell(5);
	    	        cellm5.setCellValue(p.getScaleSumOfJan());
	    	        cellm5.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm6=rowend.createCell(6);
	    	        cellm6.setCellValue(p.getActureSumOfFeb());
	    	        cellm6.setCellStyle(cellMoney);
	    	        
	    	        HSSFCell cellm7=rowend.createCell(7);
	    	        cellm7.setCellValue(p.getActOfFeb());
	    	        cellm7.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm8=rowend.createCell(8);
	    	        cellm8.setCellValue(p.getPrePaySumOfFeb());
	    	        cellm8.setCellStyle(cellMoney);
	    	        
	    	        HSSFCell cellm9=rowend.createCell(9);
	    	        cellm9.setCellValue(p.getPreOfFeb());
	    	        cellm9.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm10=rowend.createCell(10);
	    	        cellm10.setCellValue(p.getScaleSumOfFeb());
	    	        cellm10.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm11=rowend.createCell(11);
	    	        cellm11.setCellValue(p.getActureSumOfMar());
	    	        cellm11.setCellStyle(cellMoney);
	    	        
	    	        HSSFCell cellm12=rowend.createCell(12);
	    	        cellm12.setCellValue(p.getActOfMar());
	    	        cellm12.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm13=rowend.createCell(13);
	    	        cellm13.setCellValue(p.getPrePaySumOfMar());
	    	        cellm13.setCellStyle(cellMoney);
	    	        
	    	        HSSFCell cellm14=rowend.createCell(14);
	    	        cellm14.setCellValue(p.getPreOfMar());
	    	        cellm14.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm15=rowend.createCell(15);
	    	        cellm15.setCellValue(p.getScaleSumOfMar());
	    	        cellm15.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm16=rowend.createCell(16);
	    	        cellm16.setCellValue(p.getActureSumOfApr());
	    	        cellm16.setCellStyle(cellMoney);
	    	        
	    	        HSSFCell cellm17=rowend.createCell(17);
	    	        cellm17.setCellValue(p.getActOfApr());
	    	        cellm17.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm18=rowend.createCell(18);
	    	        cellm18.setCellValue(p.getPrePaySumOfApr());
	    	        cellm18.setCellStyle(cellMoney);
	    	        
	    	        HSSFCell cellm19=rowend.createCell(19);
	    	        cellm19.setCellValue(p.getPreOfApr());
	    	        cellm19.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm20=rowend.createCell(20);
	    	        cellm20.setCellValue(p.getScaleSumOfApr());
	    	        cellm20.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm21=rowend.createCell(21);
	    	        cellm21.setCellValue(p.getActureSumOfMay());
	    	        cellm21.setCellStyle(cellMoney);
	    	        
	    	        HSSFCell cellm22=rowend.createCell(22);
	    	        cellm22.setCellValue(p.getActOfMay());
	    	        cellm22.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm23=rowend.createCell(23);
	    	        cellm23.setCellValue(p.getPrePaySumOfMay());
	    	        cellm23.setCellStyle(cellMoney);
	    	        
	    	        HSSFCell cellm24=rowend.createCell(24);
	    	        cellm24.setCellValue(p.getPreOfMay());
	    	        cellm24.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm25=rowend.createCell(25);
	    	        cellm25.setCellValue(p.getScaleSumOfMay());
	    	        cellm25.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm26=rowend.createCell(26);
	    	        cellm26.setCellValue(p.getActureSumOfJun());
	    	        cellm26.setCellStyle(cellMoney);
	    	        
	    	        HSSFCell cellm27=rowend.createCell(27);
	    	        cellm27.setCellValue(p.getActOfJun());
	    	        cellm27.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm28=rowend.createCell(28);
	    	        cellm28.setCellValue(p.getPrePaySumOfJun());
	    	        cellm28.setCellStyle(cellMoney);
	    	        
	    	        HSSFCell cellm29=rowend.createCell(29);
	    	        cellm29.setCellValue(p.getPreOfJun());
	    	        cellm29.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm30=rowend.createCell(30);
	    	        cellm30.setCellValue(p.getScaleSumOfJun());
	    	        cellm30.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm31=rowend.createCell(31);
	    	        cellm31.setCellValue(p.getActureSumOfJul());
	    	        cellm31.setCellStyle(cellMoney);
	    	        
	    	        HSSFCell cellm32=rowend.createCell(32);
	    	        cellm32.setCellValue(p.getActOfJul());
	    	        cellm32.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm33=rowend.createCell(33);
	    	        cellm33.setCellValue(p.getPrePaySumOfJul());
	    	        cellm33.setCellStyle(cellMoney);
	    	        
	    	        HSSFCell cellm34=rowend.createCell(34);
	    	        cellm34.setCellValue(p.getPreOfJul());
	    	        cellm34.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm35=rowend.createCell(35);
	    	        cellm35.setCellValue(p.getScaleSumOfJul());
	    	        cellm35.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm36=rowend.createCell(36);
	    	        cellm36.setCellValue(p.getActureSumOfAus());
	    	        cellm36.setCellStyle(cellMoney);
	    	        
	    	        HSSFCell cellm37=rowend.createCell(37);
	    	        cellm37.setCellValue(p.getActOfAus());
	    	        cellm37.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm38=rowend.createCell(38);
	    	        cellm38.setCellValue(p.getPrePaySumOfAus());
	    	        cellm38.setCellStyle(cellMoney);
	    	        
	    	        HSSFCell cellm39=rowend.createCell(39);
	    	        cellm39.setCellValue(p.getPreOfAus());
	    	        cellm39.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm40=rowend.createCell(40);
	    	        cellm40.setCellValue(p.getScaleSumOfAus());
	    	        cellm40.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm41=rowend.createCell(41);
	    	        cellm41.setCellValue(p.getActureSumOfSep());
	    	        cellm41.setCellStyle(cellMoney);
	    	        
	    	        HSSFCell cellm42=rowend.createCell(42);
	    	        cellm42.setCellValue(p.getActOfSep());
	    	        cellm42.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm43=rowend.createCell(43);
	    	        cellm43.setCellValue(p.getPrePaySumOfSep());
	    	        cellm43.setCellStyle(cellMoney);
	    	        
	    	        HSSFCell cellm44=rowend.createCell(44);
	    	        cellm44.setCellValue(p.getPreOfSep());
	    	        cellm44.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm45=rowend.createCell(45);
	    	        cellm45.setCellValue(p.getScaleSumOfSep());
	    	        cellm45.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm46=rowend.createCell(46);
	    	        cellm46.setCellValue(p.getActureSumOfOct());
	    	        cellm46.setCellStyle(cellMoney);
	    	        
	    	        HSSFCell cellm47=rowend.createCell(47);
	    	        cellm47.setCellValue(p.getActOfOct());
	    	        cellm47.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm48=rowend.createCell(48);
	    	        cellm48.setCellValue(p.getPrePaySumOfOct());
	    	        cellm48.setCellStyle(cellMoney);
	    	        
	    	        HSSFCell cellm49=rowend.createCell(49);
	    	        cellm49.setCellValue(p.getPreOfOct());
	    	        cellm49.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm50=rowend.createCell(50);
	    	        cellm50.setCellValue(p.getScaleSumOfOct());
	    	        cellm50.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm51=rowend.createCell(51);
	    	        cellm51.setCellValue(p.getActureSumOfNov());
	    	        cellm51.setCellStyle(cellMoney);
	    	        
	    	        HSSFCell cellm52=rowend.createCell(52);
	    	        cellm52.setCellValue(p.getActOfNov());
	    	        cellm52.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm53=rowend.createCell(53);
	    	        cellm53.setCellValue(p.getPrePaySumOfNov());
	    	        cellm53.setCellStyle(cellMoney);
	    	        
	    	        HSSFCell cellm54=rowend.createCell(54);
	    	        cellm54.setCellValue(p.getPreOfNov());
	    	        cellm54.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm55=rowend.createCell(55);
	    	        cellm55.setCellValue(p.getScaleSumOfNov());
	    	        cellm55.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm56=rowend.createCell(56);
	    	        cellm56.setCellValue(p.getActureSumOfDec());
	    	        cellm56.setCellStyle(cellMoney);
	    	        
	    	        HSSFCell cellm57=rowend.createCell(57);
	    	        cellm57.setCellValue(p.getActOfDec());
	    	        cellm57.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm58=rowend.createCell(58);
	    	        cellm58.setCellValue(p.getPrePaySumOfDec());
	    	        cellm58.setCellStyle(cellMoney);
	    	        
	    	        HSSFCell cellm59=rowend.createCell(59);
	    	        cellm59.setCellValue(p.getPreOfDec());
	    	        cellm59.setCellStyle(headStyle3);
	    	        
	    	        HSSFCell cellm60=rowend.createCell(60);
	    	        cellm60.setCellValue(p.getScaleSumOfDec());
	    	        cellm60.setCellStyle(headStyle3);

	    	  return efw.getWorkbook();
	   }
	      
	      //生成交机前未补回拨款案件明细日报表
	      public HSSFWorkbook create(List<HashMap<String,Object>> params,Context context) throws Exception{
	    	  
	    	  SimpleDateFormat sd=new SimpleDateFormat("yyyy-MM-dd");
	    	  SimpleDateFormat sd2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    	  ExcelFileWriter efw=new ExcelFileWriter();
	    	  //设置表名
	    	  HSSFSheet sheet= efw.createSheet(context.contextMap.get("sheetName")==null?"summary"
	    			  :(String)context.contextMap.get("sheetName"));
	    	  
  	        sheet.setColumnWidth(0, 6000);      
  	        sheet.setColumnWidth(1, 9000);
  	        sheet.setColumnWidth(2, 3000);
  	        sheet.setColumnWidth(3, 4000);
  	        sheet.setColumnWidth(4, 4000);
  	        sheet.setColumnWidth(5, 4000);
  	        sheet.setColumnWidth(6, 4000);
  	        sheet.setColumnWidth(7, 4000);
	    	  
  	        HSSFFont headFont0=null;              
  	        HSSFCellStyle headStyle0=null;
  	        headFont0=efw.getWorkbook().createFont();
  	        headFont0.setFontHeightInPoints((short)12);                            //设置字体大小
  	        headFont0.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);                     // 加粗   
  	        
  	        headStyle0=efw.getWorkbook().createCellStyle();                       //设置样式
  	        headStyle0.setAlignment(HSSFCellStyle.ALIGN_CENTER);                  // 左右居中   
  	        headStyle0.setVerticalAlignment(HSSFCellStyle.VERTICAL_BOTTOM);       // 上下居中    
  	        headStyle0.setWrapText(true);                                          // 自动换行  
  	        headStyle0.setFont(headFont0);
	    	  
	    	  HSSFRow row0=sheet.createRow(0);
	    	  HSSFCell cell0=row0.createCell(0);
	    	  cell0.setCellValue("合同号");
	    	  cell0.setCellStyle(headStyle0);
	    	  
	    	  HSSFCell cell1=row0.createCell(1);
	    	  cell1.setCellValue("客户名称");
	    	  cell1.setCellStyle(headStyle0);
	    	  
	    	  HSSFCell cell2=row0.createCell(2);
	    	  cell2.setCellValue("经办");
	    	  cell2.setCellStyle(headStyle0);
	    	  
	    	  HSSFCell cell3=row0.createCell(3);
	    	  cell3.setCellValue("采购成本");
	    	  cell3.setCellStyle(headStyle0);
	    	  
	    	  HSSFCell cell4=row0.createCell(4);
	    	  cell4.setCellValue("拨款金额");
	    	  cell4.setCellStyle(headStyle0);
	    	  
	    	  HSSFCell cell5=row0.createCell(5);
	    	  cell5.setCellValue("拨款日");
	    	  cell5.setCellStyle(headStyle0);
	    	  
	    	  HSSFCell cell6=row0.createCell(6);
	    	  cell6.setCellValue("应补回的日期");
	    	  cell6.setCellStyle(headStyle0);
	    	  
	    	  HSSFCell cell7=row0.createCell(7);
	    	  cell7.setCellValue("延迟天数");
	    	  cell7.setCellStyle(headStyle0);
	    	  
   	       HSSFCellStyle headStyle2=efw.getWorkbook().createCellStyle();                       //设置样式
	        headStyle2.setAlignment(HSSFCellStyle.ALIGN_CENTER);                               // 左右居中   
	        headStyle2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
	        
	        
	        //货币格式
	        HSSFCellStyle cellMoney=efw.getWorkbook().createCellStyle();
	        HSSFDataFormat format=efw.getWorkbook().createDataFormat();
	        cellMoney.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
	        cellMoney.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
	        cellMoney.setDataFormat(format.getFormat("#,###,##0")); 
	    	  int index=0;
	    	  for(int i=0;i<params.size();i++){
	    		  index++;
	    		  HSSFRow row1=sheet.createRow(index);
	    		  
	    		  HSSFCell cell10=row1.createCell(0);
	    		  cell10.setCellValue(params.get(i).get("LEASE_CODE")==null?"":(String)params.get(i).get("LEASE_CODE"));
	    		  cell10.setCellStyle(headStyle2);
	    		  
	    		  HSSFCell cell11=row1.createCell(1);
	    		  cell11.setCellValue(params.get(i).get("CUST_NAME")==null?"":(String)params.get(i).get("CUST_NAME"));
	    		  cell11.setCellStyle(headStyle2);
	    		  
	    		  HSSFCell cell12=row1.createCell(2);
	    		  cell12.setCellValue(params.get(i).get("NAME")==null?"":(String)params.get(i).get("NAME"));
	    		  cell12.setCellStyle(headStyle2);
	    		  
	    		  HSSFCell cell13=row1.createCell(3);
	    		  double t1=Double.parseDouble(params.get(i).get("TOTAL").toString());
	    		  cell13.setCellValue(t1);
	    		  cell13.setCellStyle(cellMoney);
	    		  
	    		  HSSFCell cell14=row1.createCell(4);
	    		  double t2=Double.parseDouble(params.get(i).get("PAY_MONEY").toString());
	    		  cell14.setCellValue(t2);
	    		  cell14.setCellStyle(cellMoney);
	    		  
	    		  HSSFCell cell15=row1.createCell(5);
	    		  cell15.setCellValue(params.get(i).get("FINANCECONTRACT_DATE").toString());
	    		  cell15.setCellStyle(headStyle2);
	    		  
	    		  HSSFCell cell16=row1.createCell(6);
	    		  Date d=sd2.parse(params.get(i).get("SHOULD_FINISH_DATE").toString());
	    		  cell16.setCellValue(sd.format(d));
	    		  cell16.setCellStyle(headStyle2);
	    		  
	    		  HSSFCell cell17=row1.createCell(7);
	    		  String s=params.get(i).get("DELAY_DAY").toString();
	    		  if("".equals(s)){
	    			  s="0天";
	    		  }
	    		  cell17.setCellValue(s);
	    		  cell17.setCellStyle(headStyle2);
	    	  }
	    	  return efw.getWorkbook();
	      }  

}
