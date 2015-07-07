package com.brick.financial.util;

import java.text.SimpleDateFormat;
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

@SuppressWarnings("deprecation")
public class LeaseBackInvoiceDataExcel {
	
	public HSSFWorkbook exportInvoice(List<HashMap<String,Object>> params,Context context) throws Exception{
		ExcelFileWriter efw=new ExcelFileWriter();
		HSSFSheet sheet=efw.createSheet(context.contextMap.get("sheetName")==null?
  			  "summary":(String)context.contextMap.get("sheetName"));
	      int index=0;
	      SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm");
	      
	      sheet.setColumnWidth(1, 2500);
	      sheet.setColumnWidth(2, 5000);
	      sheet.setColumnWidth(3, 8000);
	      sheet.setColumnWidth(4, 7000);
	      sheet.setColumnWidth(5, 8000);
	      sheet.setColumnWidth(6, 7000);
	      sheet.setColumnWidth(7, 3000);
	      sheet.setColumnWidth(8, 3000);
	      sheet.setColumnWidth(9, 1500);
	      sheet.setColumnWidth(10, 1500);
	      sheet.setColumnWidth(11, 1500);
	      sheet.setColumnWidth(12, 3000);
	      sheet.setColumnWidth(13, 1500);
	      sheet.setColumnWidth(14, 7000);
	      sheet.setColumnWidth(15, 2500);
	      sheet.setColumnWidth(16, 2500);
	      
          HSSFFont headFont0=null;              
          HSSFCellStyle headStyle0=null;
        
          headFont0=efw.getWorkbook().createFont();
          headFont0.setFontHeightInPoints((short)10);                            //设置字体大小
          headFont0.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);                     // 加粗   
        
          headStyle0=efw.getWorkbook().createCellStyle();                       //设置样式
          headStyle0.setAlignment(HSSFCellStyle.ALIGN_CENTER);                  // 左右居中   
          headStyle0.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);       // 上下居中    
          headStyle0.setWrapText(true);                                          // 自动换行  
          headStyle0.setFillBackgroundColor((short)59);
          headStyle0.setBorderTop(HSSFCellStyle.BORDER_THIN);
          headStyle0.setBorderBottom(HSSFCellStyle.BORDER_THIN);
          headStyle0.setBorderLeft(HSSFCellStyle.BORDER_THIN);
          headStyle0.setBorderRight(HSSFCellStyle.BORDER_THIN);
          headStyle0.setFont(headFont0);
	  	  HSSFRow row0=sheet.createRow(0);
	  	  row0.setHeight((short)350);
	  	  HSSFCell cell0=row0.createCell(0);
	  	  cell0.setCellValue("编号");
	  	  cell0.setCellStyle(headStyle0);
	  	  
	  	  
	  	  HSSFCell cell1=row0.createCell(1);
	  	  cell1.setCellValue("支付表号");
	  	  cell1.setCellStyle(headStyle0);

	  	  
	  	  HSSFCell cell2=row0.createCell(2);
	  	  cell2.setCellValue("单据编号");
	  	  cell2.setCellStyle(headStyle0);

	  	  
	  	  HSSFCell cell3=row0.createCell(3);
	  	  cell3.setCellValue("地址电话");
	  	  cell3.setCellStyle(headStyle0);

	  	  
	  	  HSSFCell cell4=row0.createCell(4);
	  	  cell4.setCellValue("税号");
	  	  cell4.setCellStyle(headStyle0);

	  	  
	  	  HSSFCell cell5=row0.createCell(5);
	  	  cell5.setCellValue("开户行帐号");
	  	  cell5.setCellStyle(headStyle0);

	  	  
	  	  HSSFCell cell6=row0.createCell(6);
	  	  cell6.setCellValue("客户名称");
	  	  cell6.setCellStyle(headStyle0);

	  	  
	  	  HSSFCell cell7=row0.createCell(7);
	  	  cell7.setCellValue("产品名称");
	  	  cell7.setCellStyle(headStyle0);

	  	  
	  	  HSSFCell cell8=row0.createCell(8);
	  	  cell8.setCellValue("规格型号");
	  	  cell8.setCellStyle(headStyle0);

	  	  
	  	  HSSFCell cell9=row0.createCell(9);
	  	  cell9.setCellValue("单位");
	  	  cell9.setCellStyle(headStyle0);

	  	  
	  	  HSSFCell cell10=row0.createCell(10);
	  	  cell10.setCellValue("数量");
	  	  cell10.setCellStyle(headStyle0);

	  	  
	  	  HSSFCell cell11=row0.createCell(11);
	  	  cell11.setCellValue("单价");
	  	  cell11.setCellStyle(headStyle0);

	  	  
	  	  HSSFCell cell12=row0.createCell(12);
	  	  cell12.setCellValue("金额");
	  	  cell12.setCellStyle(headStyle0);

	  	  
	  	  HSSFCell cell13=row0.createCell(13);
	  	  cell13.setCellValue("税率");
	  	  cell13.setCellStyle(headStyle0);

	  	  
	  	  HSSFCell cell14=row0.createCell(14);
	  	  cell14.setCellValue("备注1");
	  	  cell14.setCellStyle(headStyle0);

	  	  
	  	  HSSFCell cell15=row0.createCell(15);
	  	  cell15.setCellValue("备注2");
	  	  cell15.setCellStyle(headStyle0);

	  	  
	  	  HSSFCell cell16=row0.createCell(16);
	  	  cell16.setCellValue("备注3");
	  	  cell16.setCellStyle(headStyle0);
	  	  
          HSSFCellStyle style1=null;
          style1=efw.getWorkbook().createCellStyle();                       
          style1.setAlignment(HSSFCellStyle.ALIGN_CENTER);                  // 左右居中   
          style1.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);       // 上下居上    
          style1.setWrapText(true);                                          // 自动换行  
          style1.setBorderTop(HSSFCellStyle.BORDER_THIN);
          style1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
          style1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
          style1.setBorderRight(HSSFCellStyle.BORDER_THIN);

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
	      
	  	  for(int i=0;params!=null&&i<params.size();i++){
	  		  index++;
	  		  HSSFRow row1=sheet.createRow(index);
	  		  row1.setHeight((short)900);
	  		  
	  		  HSSFCell cellm0=row1.createCell(0);
	  		  cellm0.setCellValue(params.get(i).get("RECD_ID")==null?"":params.get(i).get("RECD_ID").toString());
	  		  cellm0.setCellStyle(style1);
	  		  
	  		  HSSFCell cellm1=row1.createCell(1);
	  		  cellm1.setCellValue(params.get(i).get("RECP_ID")==null?"":params.get(i).get("RECP_ID").toString());
		  	  cellm1.setCellStyle(style1);
	  		  
	  		  HSSFCell cellm2=row1.createCell(2);
	  		  cellm2.setCellValue(params.get(i).get("RUNNUM")==null?"":params.get(i).get("RUNNUM").toString());
		  	  cellm2.setCellStyle(style1);
	  		  
	  		  HSSFCell cellm3=row1.createCell(3);
	  		  cellm3.setCellValue(params.get(i).get("LINK_ADDRESS")==null?"":params.get(i).get("LINK_ADDRESS").toString());
		  	  cellm3.setCellStyle(style1);
	  		  
	  		  HSSFCell cellm4=row1.createCell(4);
	  		  cellm4.setCellValue(params.get(i).get("CORP_TAX_CODE")==null?"":params.get(i).get("CORP_TAX_CODE").toString());
		  	  cellm4.setCellStyle(style1);
	  		  
	  		  HSSFCell cellm5=row1.createCell(5);
	  		  cellm5.setCellValue(params.get(i).get("BANK_NAME")==null?"":params.get(i).get("BANK_NAME").toString());
		  	  cellm5.setCellStyle(style1);
	  		  
	  		  HSSFCell cellm6=row1.createCell(6);	
	  		  cellm6.setCellValue(params.get(i).get("CUST_NAME")==null?"":params.get(i).get("CUST_NAME").toString());
		  	  cellm6.setCellStyle(style1);
	  		  
	  		  HSSFCell cellm7=row1.createCell(7);
	  		  cellm7.setCellValue(params.get(i).get("PRODUCT_NAME")==null?"":params.get(i).get("PRODUCT_NAME").toString());
		  	  cellm7.setCellStyle(style1);
	  		  
	  		  HSSFCell cellm8=row1.createCell(8);
	  		  cellm8.setCellValue(params.get(i).get("PRODUCT_KIND")==null?"":params.get(i).get("PRODUCT_KIND").toString());
		  	  cellm8.setCellStyle(style1);
	  		  
	  		  HSSFCell cellm9=row1.createCell(9);
	  		  cellm9.setCellValue(params.get(i).get("UNIT")==null?"":params.get(i).get("UNIT").toString());
		  	  cellm9.setCellStyle(style1);
	  		  
	  		  HSSFCell cellm10=row1.createCell(10);
	  		  cellm10.setCellValue(params.get(i).get("NUMBER")==null?"":params.get(i).get("NUMBER").toString());
		  	  cellm10.setCellStyle(style1);
	  		  
	  		  HSSFCell cellm11=row1.createCell(11);
	  		  cellm11.setCellValue(params.get(i).get("UNIT_PRICE")==null||"".equals(params.get(i).get("UNIT_PRICE"))?"":params.get(i).get("UNIT_PRICE").toString());
		  	  cellm11.setCellStyle(cellMoney);
	  		  
	  		  HSSFCell cellm12=row1.createCell(12);
	  		  cellm12.setCellValue(params.get(i).get("PRICE")==null||"".equals(params.get(i).get("PRICE"))?0:Double.parseDouble(params.get(i).get("PRICE").toString()));
		  	  cellm12.setCellStyle(cellMoney);
	  		  
	  		  HSSFCell cellm13=row1.createCell(13);
	  		  cellm13.setCellValue(params.get(i).get("TAX_RATE")==null?"":params.get(i).get("TAX_RATE").toString());
		  	  cellm13.setCellStyle(style1);
	  		  
	  		  HSSFCell cellm14=row1.createCell(14);
	  		  cellm14.setCellValue(params.get(i).get("REMARK1")==null?"":params.get(i).get("REMARK1").toString());
		  	  cellm14.setCellStyle(style1);
	  		  
	  		  HSSFCell cellm15=row1.createCell(15);
	  		  cellm15.setCellValue(params.get(i).get("REMARK2")==null?"":params.get(i).get("REMARK2").toString());
		  	  cellm15.setCellStyle(style1);
	  		  
	  		  HSSFCell cellm16=row1.createCell(16);
	  		  cellm16.setCellValue(params.get(i).get("REMARK3")==null?"":params.get(i).get("REMARK3").toString());
		  	  cellm16.setCellStyle(style1);
	  	  }
	  	  
  	    index++;
        //合并单元格
        CellRangeAddress rang=null;
        rang=new CellRangeAddress(index, index, 0, 16);
        sheet.addMergedRegion(rang);
  	    HSSFRow endRow=sheet.createRow(index);
  	    HSSFCell beginCell=endRow.createCell(0);
  	    beginCell.setCellValue(df.format(new Date()));
  	    
		return efw.getWorkbook();
	}

}
