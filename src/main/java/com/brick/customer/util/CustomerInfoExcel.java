package com.brick.customer.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.brick.service.entity.Context;
import com.brick.util.poi.ExcelFileWriter;

public class CustomerInfoExcel {
           
	      @SuppressWarnings("unchecked")
		public HSSFWorkbook createReport(Map<String,Object> params,Context context) throws Exception{
	    	  
	    	  ExcelFileWriter efw=new ExcelFileWriter();
	    	  HSSFSheet sheet=efw.createSheet(context.contextMap.get("sheetName")==null?
	    			  "summary":(String)context.contextMap.get("sheetName"));
	    	  List<HashMap<String,Object>> list=(List<HashMap<String,Object>>)params.get("cusInfo");
	    	  
	    	  sheet.setColumnWidth(0, 5000);
	    	  sheet.setColumnWidth(1, 3000);
	    	  sheet.setColumnWidth(2, 5300);
	    	  sheet.setColumnWidth(3, 3600);
	    	  sheet.setColumnWidth(4, 4600);
	    	  sheet.setColumnWidth(5, 10000);
	    	  sheet.setColumnWidth(6, 6000);
	    	  sheet.setColumnWidth(7, 4000);
	    	  sheet.setColumnWidth(8, 3500);
	    	  sheet.setColumnWidth(9, 3500);
	    	  sheet.setColumnWidth(10, 3500);
	    	  sheet.setColumnWidth(11, 4200);
	    	  sheet.setColumnWidth(12, 4200);
	    	  sheet.setColumnWidth(13, 4200);
	    	  sheet.setColumnWidth(14, 4800);
	    	  sheet.setColumnWidth(15, 5000);
	    	  sheet.setColumnWidth(16, 5000);
	    	  sheet.setColumnWidth(17, 3000);
	    	  sheet.setColumnWidth(18, 4500);
	    	  sheet.setColumnWidth(19, 4500);
	    	  sheet.setColumnWidth(20, 3000);
	    	  sheet.setColumnWidth(21, 4300);
	    	  sheet.setColumnWidth(22, 4000);
	    	  sheet.setColumnWidth(23, 7000);
	    	  sheet.setColumnWidth(24, 10000);

	    	  
  	          HSSFFont headFont0=null;              
  	          HSSFCellStyle headStyle0=null;
  	        
  	          headFont0=efw.getWorkbook().createFont();
  	          headFont0.setFontHeightInPoints((short)13);                            //设置字体大小
  	          headFont0.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);                     // 加粗   
  	        
  	          headStyle0=efw.getWorkbook().createCellStyle();                       //设置样式
  	          headStyle0.setAlignment(HSSFCellStyle.ALIGN_CENTER);                  // 左右居中   
  	          headStyle0.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);       // 上下居中    
  	          headStyle0.setWrapText(true);                                          // 自动换行  
  	          headStyle0.setFillBackgroundColor((short)59);
  	          headStyle0.setFont(headFont0);
  	          
  	         //货币格式
  	          HSSFCellStyle cellMoney=efw.getWorkbook().createCellStyle();
  	          HSSFDataFormat format=efw.getWorkbook().createDataFormat();
  	          cellMoney.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
  	          cellMoney.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
  	          cellMoney.setDataFormat(format.getFormat("#,###,##0")); 	
	    	  
	    	  HSSFRow row0=sheet.createRow(0);
	    	  HSSFCell cell0=row0.createCell(0);
	    	  cell0.setCellValue("承租人编号");
	    	  cell0.setCellStyle(headStyle0);
	    	  
	    	  HSSFCell cell1=row0.createCell(1);
	    	  cell1.setCellValue("客户经理");
	    	  cell1.setCellStyle(headStyle0);
	    	  
	    	  HSSFCell cell2=row0.createCell(2);
	    	  cell2.setCellValue("企业名称");
	    	  cell2.setCellStyle(headStyle0);
	    	  
	    	  HSSFCell cell3=row0.createCell(3);
	    	  cell3.setCellValue("组织机构代码号");
	    	  cell3.setCellStyle(headStyle0);
	    	  
	    	  HSSFCell cell4=row0.createCell(4);
	    	  cell4.setCellValue("承租人所在省市");
	    	  cell4.setCellStyle(headStyle0);
	    	  
	    	  HSSFCell cell5=row0.createCell(5);
	    	  cell5.setCellValue("公司办公地址");
	    	  cell5.setCellStyle(headStyle0);
	    	  
	    	  HSSFCell cell6=row0.createCell(6);
	    	  cell6.setCellValue("虚拟帐号");
	    	  cell6.setCellStyle(headStyle0);
	    	  
	    	  HSSFCell cell7=row0.createCell(7);
	    	  cell7.setCellValue("承租人状态");
	    	  cell7.setCellStyle(headStyle0);
	    	  
	    	  HSSFCell cell8=row0.createCell(8);
	    	  cell8.setCellValue("成立日期");
	    	  cell8.setCellStyle(headStyle0);
	    	  
	    	  HSSFCell cell9=row0.createCell(9);
	    	  cell9.setCellValue("注册资本");
	    	  cell9.setCellStyle(headStyle0);
	    	  
	    	  HSSFCell cell10=row0.createCell(10);
	    	  cell10.setCellValue("实收资本");
	    	  cell10.setCellStyle(headStyle0);
	    	  
	    	  HSSFCell cell11=row0.createCell(11);
	    	  cell11.setCellValue("营业执照注册号");
	    	  cell11.setCellStyle(headStyle0);
	    	  
	    	  HSSFCell cell12=row0.createCell(12);
	    	  cell12.setCellValue("税务编号");
	    	  cell12.setCellStyle(headStyle0);
	    	  
	    	  HSSFCell cell13=row0.createCell(13);
	    	  cell13.setCellValue("税务登记号");
	    	  cell13.setCellStyle(headStyle0);
	    	  
	    	  HSSFCell cell14=row0.createCell(14);
	    	  cell14.setCellValue("有效期");
	    	  cell14.setCellStyle(headStyle0);
	    	  
	    	  HSSFCell cell15=row0.createCell(15);
	    	  cell15.setCellValue("注册地址");
	    	  cell15.setCellStyle(headStyle0);
	    	  
	    	  HSSFCell cell16=row0.createCell(16);
	    	  cell16.setCellValue("经营范围");
	    	  cell16.setCellStyle(headStyle0);
	    	  
	    	  HSSFCell cell17=row0.createCell(17);
	    	  cell17.setCellValue("公司邮编");
	    	  cell17.setCellStyle(headStyle0);
	    	  
	    	  HSSFCell cell18=row0.createCell(18);
	    	  cell18.setCellValue("公司网址");
	    	  cell18.setCellStyle(headStyle0);
	    	  
	    	  HSSFCell cell19=row0.createCell(19);
	    	  cell19.setCellValue("公司邮箱");
	    	  cell19.setCellStyle(headStyle0);
	    	  
	    	  HSSFCell cell20=row0.createCell(20);
	    	  cell20.setCellValue("法人代表人");
	    	  cell20.setCellStyle(headStyle0);
	    	  
	    	  HSSFCell cell21=row0.createCell(21);
	    	  cell21.setCellValue("法人身份证号码");
	    	  cell21.setCellStyle(headStyle0);
	    	  
	    	  HSSFCell cell22=row0.createCell(22);
	    	  cell22.setCellValue("法人联系方式");
	    	  cell22.setCellStyle(headStyle0);
	    	  
	    	  HSSFCell cell23=row0.createCell(23);
	    	  cell23.setCellValue("法人代表住址");
	    	  cell23.setCellStyle(headStyle0);
	    	  
	    	  HSSFCell cell24=row0.createCell(24);
	    	  cell24.setCellValue("备注");
	    	  cell24.setCellStyle(headStyle0);
	    	  
	    	  
	    	  for(int i=0;i<list.size();i++){
	    		HSSFRow row1=sheet.createRow(i+1);
	    		
	    		HSSFCell cellr0=row1.createCell(0);
	    		cellr0.setCellValue((String)list.get(i).get("CUST_CODE"));
	    		
	    		HSSFCell cellr1=row1.createCell(1);
	    		cellr1.setCellValue((String)list.get(i).get("NAME"));
	    		
	    		HSSFCell cellr2=row1.createCell(2);
	    		cellr2.setCellValue((String)list.get(i).get("CUST_NAME"));
	    		
	    		HSSFCell cellr3=row1.createCell(3);
	    		cellr3.setCellValue((String)list.get(i).get("CORP_ORAGNIZATION_CODE"));
	    		
	    		HSSFCell cellr4=row1.createCell(4);
	    		cellr4.setCellValue((String)list.get(i).get("CUST_AREA"));
	    		
	    		HSSFCell cellr5=row1.createCell(5);
	    		cellr5.setCellValue((String)list.get(i).get("CORP_WORK_ADDRESS"));
	    		
	    		HSSFCell cellr6=row1.createCell(6);
	    		cellr6.setCellValue((String)list.get(i).get("VIRTUAL_CODE"));
	    		
	    		HSSFCell cellr7=row1.createCell(7);
	    		String s=null;
	    		int type=Integer.parseInt(list.get(i).get("STATETYPE").toString());
	    		switch(type){
	    		case 0:
	    			s="潜在客户";
	    			break;
	    		case 1:
	    			s="新客户";
	    			break;
	    		case 2:
	    			s="已提交客户";
	    			break;
	    		case 3:
	    			s="既有客户";
	    			break;
	    		case 4:
	    			s="其他";
	    			break;
	    		}
	    		cellr7.setCellValue(s);
	    		
	    		HSSFCell cellr8=row1.createCell(8);
	    		cellr8.setCellValue((String)list.get(i).get("CORP_SETUP_DATE"));
	    		
	    		HSSFCell cellr9=row1.createCell(9);
	    		double n=list.get(i).get("CORP_REGISTE_CAPITAL")==null?0:(Double)list.get(i).get("CORP_REGISTE_CAPITAL");
	    		cellr9.setCellValue(n);
	    		cellr9.setCellStyle(cellMoney);
	    		
	    		HSSFCell cellr10=row1.createCell(10);
	    		double m=list.get(i).get("CORP_PAICLUP_CAPITAL")==null?0:(Double)list.get(i).get("CORP_PAICLUP_CAPITAL");
	    		cellr10.setCellValue(m);
	    		cellr10.setCellStyle(cellMoney);
	    		
	    		HSSFCell cellr11=row1.createCell(11);
	    		cellr11.setCellValue((String)list.get(i).get("CORP_BUSINESS_LICENSE"));
	    		
	    		HSSFCell cellr12=row1.createCell(12);
	    		cellr12.setCellValue((String)list.get(i).get("TAX_CODE"));
	    		
	    		HSSFCell cellr13=row1.createCell(13);
	    		cellr13.setCellValue((String)list.get(i).get("CORP_TAX_CODE"));
	    		
	    		HSSFCell cellr14=row1.createCell(14);
	    		cellr14.setCellValue((String)list.get(i).get("CORP_PERIOD_VALIDITY"));
	    		
	    		HSSFCell cellr15=row1.createCell(15);
	    		cellr15.setCellValue((String)list.get(i).get("CORP_WORK_ADDRESS"));
	    		
	    		HSSFCell cellr16=row1.createCell(16);
	    		cellr16.setCellValue((String)list.get(i).get("CORP_BUSINESS_RANGE"));
	    		
	    		HSSFCell cellr17=row1.createCell(17);
	    		cellr17.setCellValue((String)list.get(i).get("CORP_COMPANY_ZIP"));
	    		
	    		HSSFCell cellr18=row1.createCell(18);
	    		cellr18.setCellValue((String)list.get(i).get("CORP_COMPANY_WEBSITE"));
	    		
	    		HSSFCell cellr19=row1.createCell(19);
	    		cellr19.setCellValue((String)list.get(i).get("CORP_COMPANY_EMAIL"));
	    		
	    		HSSFCell cellr20=row1.createCell(20);
	    		cellr20.setCellValue((String)list.get(i).get("CORP_HEAD_SIGNATURE"));
	    		
	    		HSSFCell cellr21=row1.createCell(21);
	    		cellr21.setCellValue((String)list.get(i).get("CORP_HS_IDCARD"));
	    		
	    		HSSFCell cellr22=row1.createCell(22);
	    		cellr22.setCellValue((String)list.get(i).get("CORP_HS_LINK_MODE"));
	    		
	    		HSSFCell cellr23=row1.createCell(23);
	    		cellr23.setCellValue((String)list.get(i).get("CORP_HS_HOME_ADDRESS"));
	    		
	    		HSSFCell cellr24=row1.createCell(24);
	    		cellr24.setCellValue((String)list.get(i).get("REMARK"));
	    	  }
	    	  return efw.getWorkbook();
	      }
}
