package com.brick.util.poi;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;

import com.brick.bussinessReport.to.AccessCustomerPlanReportTO;
import com.brick.service.entity.Context;

/**
 * @author ShenQi
 * @version Created：2012-03-29
 * function: POI Excel操作
 */
public class ExcelPOI {
	
	Log logger=LogFactory.getLog(ExcelPOI.class);
	
	public HSSFWorkbook generateBonusReport(List<Map<String, Object>> dataSourceList,Context context) {
		
		ExcelFileWriter efw=new ExcelFileWriter();
		
		HSSFSheet sheet=efw.createSheet(context.contextMap.get("sheetName")==null
				?"Summary":context.contextMap.get("sheetName").toString());
		
		HSSFCellStyle BORDERED_BOLD_CENTER=ExcelStyleLoader.load(efw.getStyle(), efw.getFont(), ExcelStyleLoader.BORDERED_BOLD_CENTER);
		HSSFCellStyle DEFAULT_CENTER=ExcelStyleLoader.load(efw.getStyle(), efw.getFont(), ExcelStyleLoader.DEFAULT_CENTER);
		HSSFCellStyle BORDERED_RIGHT_MONEY=ExcelStyleLoader.load(efw.getStyle(), efw.getFont(), ExcelStyleLoader.BORDERED_RIGHT_MONEY);
		HSSFCellStyle BORDERED_BOLD_RIGHT=ExcelStyleLoader.load(efw.getStyle(), efw.getFont(), ExcelStyleLoader.BORDERED_BOLD_RIGHT);
		
        HSSFFont font=efw.getFont();
        font.setFontHeightInPoints((short) 8);
        
        //表头开始设置
            //addMergedRegion(region.getRowFrom(), region.getColumnFrom(), region.getRowTo(), region.getColumnTo())
        sheet.addMergedRegion(new Region(0, (short)(0), 4, (short)(0)));
          //setStringValue(int sheetNo, int rowNo, int cellNo, String cellValue, HSSFCellStyle cellStyle)
        efw.setStringValue(0, 0, 0, "单位", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(0, (short)(1), 4, (short)(1)));
        efw.setStringValue(0, 0, 1, "经办人", BORDERED_BOLD_CENTER);
        
        
        sheet.addMergedRegion(new Region(0, (short)(2), 0, (short)(8)));
        efw.setStringValue(0, 0, 2, "当月业绩", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(1, (short)(2), 1, (short)(3)));
        efw.setStringValue(0, 1, 2, "业绩目标", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(1, (short)(4), 1, (short)(6)));
        efw.setStringValue(0, 1, 4, "当月实际承做", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(1, (short)(7), 1, (short)(8)));
        efw.setStringValue(0, 1, 7, "达成率", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(2, (short)(2), 4, (short)(2)));
        efw.setStringValue(0, 2, 2, "净授信金额", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(2, (short)(3), 4, (short)(3)));
        efw.setStringValue(0, 2, 3, "利差现值", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(2, (short)(4), 4, (short)(4)));
        efw.setStringValue(0, 2, 4, "案\r\n件\r\n数", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(2, (short)(5), 4, (short)(5)));
        efw.setStringValue(0, 2, 5, "净售信金额", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(2, (short)(6), 4, (short)(6)));
        efw.setStringValue(0, 2, 6, "利差现值", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(2, (short)(7), 4, (short)(7)));
        efw.setStringValue(0, 2, 7, "净售信金额", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(2, (short)(8), 4, (short)(8)));
        efw.setStringValue(0, 2, 8, "利差现值", BORDERED_BOLD_CENTER);
        
        
        sheet.addMergedRegion(new Region(0, (short)(9), 0, (short)(15)));
        efw.setStringValue(0, 0, 9, "年度累计业绩", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(1, (short)(9), 1, (short)(10)));
        efw.setStringValue(0, 1, 9, "年度目标", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(1, (short)(11), 1, (short)(13)));
        efw.setStringValue(0, 1, 11, "当年累计承做", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(1, (short)(14), 1, (short)(15)));
        efw.setStringValue(0, 1, 14, "达成率", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(2, (short)(9), 4, (short)(9)));
        efw.setStringValue(0, 2, 9, "净授信金额", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(2, (short)(10), 4, (short)(10)));
        efw.setStringValue(0, 2, 10, "利差现值", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(2, (short)(11), 4, (short)(11)));
        efw.setStringValue(0, 2, 11, "案\r\n件\r\n数", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(2, (short)(12), 4, (short)(12)));
        efw.setStringValue(0, 2, 12, "净授信金额", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(2, (short)(13), 4, (short)(13)));
        efw.setStringValue(0, 2, 13, "利差现值", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(2, (short)(14), 4, (short)(14)));
        efw.setStringValue(0, 2, 14, "净授信金额", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(2, (short)(15), 4, (short)(15)));
        efw.setStringValue(0, 2, 15, "利差现值", BORDERED_BOLD_CENTER);
        
        
        sheet.addMergedRegion(new Region(0, (short)(16), 0, (short)(20)));
        efw.setStringValue(0, 0, 16, "业绩奖金计算", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(1, (short)(16), 4, (short)(16)));
        efw.setStringValue(0, 1, 16, "业绩是否达成", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(1, (short)(17), 4, (short)(17)));
        efw.setStringValue(0, 1, 17, "利差是否达成", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(1, (short)(18), 4, (short)(18)));
        efw.setStringValue(0, 1, 18, "单位奖金", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(1, (short)(19), 4, (short)(19)));
        efw.setStringValue(0, 1, 19, "案件奖金", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(1, (short)(20), 4, (short)(20)));
        efw.setStringValue(0, 1, 20, "利差奖金", BORDERED_BOLD_CENTER);
        
        
        sheet.addMergedRegion(new Region(0, (short)(21), 4, (short)(21)));
        efw.setStringValue(0, 0, 21, "奖金合计", BORDERED_BOLD_CENTER);
        //表头结束设置
        
        
        //表内容开始设置
        int sheetNo=0;
        int rowNo=4;
        int colNo=0;
        
        for(int i=0;i<dataSourceList.size();i++) {
        	rowNo++;
            colNo=0;
            sheet.setColumnWidth((short) colNo, (short) (10 * 256));
        	efw.setStringValue(sheetNo, rowNo, colNo++, (String)dataSourceList.get(i).get("DECP_NAME"), DEFAULT_CENTER);
        	sheet.setColumnWidth((short) colNo, (short) (14 * 256));
        	efw.setStringValue(sheetNo, rowNo, colNo++, (String)dataSourceList.get(i).get("EMP_NAME"), DEFAULT_CENTER);
        	sheet.setColumnWidth((short) colNo, (short) (14 * 256));
        	efw.setDoubleValue(sheetNo, rowNo, colNo++, Double.valueOf(String.valueOf(dataSourceList.get(i).get("MONTH_TARGET"))), BORDERED_RIGHT_MONEY);
        	sheet.setColumnWidth((short) colNo, (short) (14 * 256));
        	efw.setDoubleValue(sheetNo, rowNo, colNo++, Double.valueOf(String.valueOf(dataSourceList.get(i).get("MONTH_RATE_TARGET"))), BORDERED_RIGHT_MONEY);
        	sheet.setColumnWidth((short) colNo, (short) (4 * 256));
        	efw.setDoubleValue(sheetNo, rowNo, colNo++, Double.valueOf(String.valueOf(dataSourceList.get(i).get("MONTH_COUNT"))), DEFAULT_CENTER);
        	sheet.setColumnWidth((short) colNo, (short) (14 * 256));
        	efw.setDoubleValue(sheetNo, rowNo, colNo++, Double.valueOf(String.valueOf(dataSourceList.get(i).get("MONTH_PAY_MONEY"))), BORDERED_RIGHT_MONEY);
        	sheet.setColumnWidth((short) colNo, (short) (14 * 256));
        	efw.setDoubleValue(sheetNo, rowNo, colNo++, Double.valueOf(String.valueOf(dataSourceList.get(i).get("MONTH_RATE_DIFF"))), BORDERED_RIGHT_MONEY);
        	sheet.setColumnWidth((short) colNo, (short) (10 * 256));
        	efw.setStringValue(sheetNo, rowNo, colNo++, Double.valueOf(String.valueOf(dataSourceList.get(i).get("MONTH_PAY_MONEY_PERCENT")))+"%", DEFAULT_CENTER);
        	sheet.setColumnWidth((short) colNo, (short) (10 * 256));
        	efw.setStringValue(sheetNo, rowNo, colNo++, Double.valueOf(String.valueOf(dataSourceList.get(i).get("MONTH_RATE_DIFF_PERCENT")))+"%", DEFAULT_CENTER);
        	sheet.setColumnWidth((short) colNo, (short) (14 * 256));
        	efw.setDoubleValue(sheetNo, rowNo, colNo++, Double.valueOf(String.valueOf(dataSourceList.get(i).get("YEAR_TARGET"))), BORDERED_RIGHT_MONEY);
        	sheet.setColumnWidth((short) colNo, (short) (14 * 256));
        	efw.setDoubleValue(sheetNo, rowNo, colNo++, Double.valueOf(String.valueOf(dataSourceList.get(i).get("YEAR_RATE_TARGET"))), BORDERED_RIGHT_MONEY);
        	sheet.setColumnWidth((short) colNo, (short) (4 * 256));
        	efw.setDoubleValue(sheetNo, rowNo, colNo++, Double.valueOf(String.valueOf(dataSourceList.get(i).get("YEAR_COUNT"))), DEFAULT_CENTER);
        	sheet.setColumnWidth((short) colNo, (short) (14 * 256));
        	efw.setDoubleValue(sheetNo, rowNo, colNo++, Double.valueOf(String.valueOf(dataSourceList.get(i).get("YEAR_PAY_MONEY"))), BORDERED_RIGHT_MONEY);
        	sheet.setColumnWidth((short) colNo, (short) (14 * 256));
        	efw.setDoubleValue(sheetNo, rowNo, colNo++, Double.valueOf(String.valueOf(dataSourceList.get(i).get("YEAR_RATE_DIFF"))), BORDERED_RIGHT_MONEY);
        	sheet.setColumnWidth((short) colNo, (short) (10 * 256));
        	efw.setStringValue(sheetNo, rowNo, colNo++, Double.valueOf(String.valueOf(dataSourceList.get(i).get("YEAR_PAY_MONEY_PERCENT")))+"%", DEFAULT_CENTER);
        	sheet.setColumnWidth((short) colNo, (short) (10 * 256));
        	efw.setStringValue(sheetNo, rowNo, colNo++, Double.valueOf(String.valueOf(dataSourceList.get(i).get("YEAR_RATE_DIFF_PERCENT")))+"%", DEFAULT_CENTER);
        	sheet.setColumnWidth((short) colNo, (short) (10 * 256));
        	efw.setStringValue(sheetNo, rowNo, colNo++, ((BigDecimal)dataSourceList.get(i).get("MONTH_PAY_MONEY_PERCENT")).doubleValue()>=100?"是":"否", DEFAULT_CENTER);
        	sheet.setColumnWidth((short) colNo, (short) (14 * 256));
        	efw.setStringValue(sheetNo, rowNo, colNo++, ((BigDecimal)dataSourceList.get(i).get("MONTH_RATE_DIFF_PERCENT")).doubleValue()>=100?"是":"否", DEFAULT_CENTER);
        	//TODO 单位奖金暂时是空
        	sheet.setColumnWidth((short) colNo, (short) (14 * 256));
        	efw.setDoubleValue(sheetNo, rowNo, colNo++, 0, BORDERED_RIGHT_MONEY);
        	
        	Double RATE_DIFF_BONUS=0.00;
        	Double PROJECT_COUNT_BONUS=0.00;
        	
        	if(dataSourceList.get(i).get("RATE_DIFF_BONUS")!=null) {
        		RATE_DIFF_BONUS=((BigDecimal)dataSourceList.get(i).get("RATE_DIFF_BONUS")).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        	}
        	if(dataSourceList.get(i).get("PROJECT_COUNT_BONUS")!=null) {
        		PROJECT_COUNT_BONUS=((BigDecimal)dataSourceList.get(i).get("PROJECT_COUNT_BONUS")).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        	}
        	
        	sheet.setColumnWidth((short) colNo, (short) (14 * 256));
        	efw.setDoubleValue(sheetNo, rowNo, colNo++, PROJECT_COUNT_BONUS, BORDERED_RIGHT_MONEY);
        	sheet.setColumnWidth((short) colNo, (short) (14 * 256));
        	efw.setDoubleValue(sheetNo, rowNo, colNo++, RATE_DIFF_BONUS, BORDERED_RIGHT_MONEY);
        	sheet.setColumnWidth((short) colNo, (short) (14 * 256));
        	java.text.DecimalFormat FORMAT=new java.text.DecimalFormat("#0.00");
        	efw.setDoubleValue(sheetNo, rowNo, colNo++, Double.parseDouble(FORMAT.format(PROJECT_COUNT_BONUS+RATE_DIFF_BONUS)), BORDERED_RIGHT_MONEY);
        	if ("-1".equals(String.valueOf(dataSourceList.get(i).get("EMP_ID")))) {
				drawOther(rowNo, colNo, dataSourceList.get(i), efw, sheet);
			}
        }
        
        //表内容结束设置
        
        sheet.addMergedRegion(new Region(rowNo+1, (short)(0), rowNo+1, (short)(21)));
        efw.setStringValue(0, rowNo+1, 0, "奖金报表时间:"+context.contextMap.get("date"), BORDERED_BOLD_RIGHT);
        
        sheet.addMergedRegion(new Region(rowNo+5, (short)(0), rowNo+5, (short)(1)));
        efw.setStringValue(0, rowNo+5, 0, "董事长__________", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(rowNo+5, (short)(3), rowNo+5, (short)(5)));
        efw.setStringValue(0, rowNo+5, 3, "总经理__________", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(rowNo+5, (short)(7), rowNo+5, (short)(9)));
        efw.setStringValue(0, rowNo+5, 7, "财务部__________", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(rowNo+5, (short)(11), rowNo+5, (short)(13)));
        efw.setStringValue(0, rowNo+5, 11, "业务主管__________", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(rowNo+5, (short)(15), rowNo+5, (short)(17)));
        efw.setStringValue(0, rowNo+5, 15, "制表确认__________", BORDERED_BOLD_CENTER);
        
		return efw.getWorkbook();
	}
	
	private void drawOther(int rowNo, int colNo, Map<String, Object> map, ExcelFileWriter efw, HSSFSheet sheet) {
		int startRow = rowNo + 1;
		List<Map<String, Object>> assistantList = (List<Map<String, Object>>) map.get("assistantList");
		List<Map<String, Object>> managerList = (List<Map<String, Object>>) map.get("managerList");
		startRow = startRow - (assistantList == null ? 0 : assistantList.size()) - (managerList == null ? 0 : managerList.size());
		if (startRow == rowNo + 1) {
			return;
		}
		for (Map<String, Object> m2 : managerList) {
			sheet.setColumnWidth((short) colNo, (short) (10 * 256));
        	efw.setStringValue(0, startRow, colNo, String.valueOf(m2.get("EMP_NAME")), ExcelStyleLoader.load(efw.getStyle(), efw.getFont(), ExcelStyleLoader.DEFAULT_CENTER));
        	sheet.setColumnWidth((short) colNo, (short) (14 * 256));
        	efw.setDoubleValue(0, startRow, colNo+1, Double.valueOf(String.valueOf(m2.get("money"))), ExcelStyleLoader.load(efw.getStyle(), efw.getFont(), ExcelStyleLoader.BORDERED_RIGHT_MONEY));
        	startRow ++;
		}
		for (Map<String, Object> m1 : assistantList) {
			sheet.setColumnWidth((short) colNo, (short) (10 * 256));
        	efw.setStringValue(0, startRow, colNo, String.valueOf(m1.get("EMP_NAME")), ExcelStyleLoader.load(efw.getStyle(), efw.getFont(), ExcelStyleLoader.DEFAULT_CENTER));
        	sheet.setColumnWidth((short) colNo, (short) (14 * 256));
        	efw.setDoubleValue(0, startRow, colNo+1, Double.valueOf(String.valueOf(m1.get("money"))), ExcelStyleLoader.load(efw.getStyle(), efw.getFont(), ExcelStyleLoader.BORDERED_RIGHT_MONEY));
        	startRow ++;
		}
	}

	public HSSFWorkbook generateCaseReport(List<Map<String, Object>> paymentList,List<Map<String, Object>> incomeList,Context context) {

		ExcelFileWriter efw=new ExcelFileWriter();

		HSSFSheet sheet=efw.createSheet(context.contextMap.get("sheetName")==null
				?"Summary":context.contextMap.get("sheetName").toString());
		
		HSSFFont font=efw.getFont();
        font.setFontHeightInPoints((short) 8);
        
        HSSFCellStyle BORDERED_BOLD_CENTER=ExcelStyleLoader.load(efw.getStyle(), efw.getFont(), ExcelStyleLoader.BORDERED_BOLD_CENTER);
        HSSFCellStyle DEFAULT_CENTER=ExcelStyleLoader.load(efw.getStyle(), efw.getFont(), ExcelStyleLoader.DEFAULT_CENTER);
        HSSFCellStyle BORDERED_RIGHT_MONEY=ExcelStyleLoader.load(efw.getStyle(), efw.getFont(), ExcelStyleLoader.BORDERED_RIGHT_MONEY);
        
        sheet.setColumnWidth((short) 0, (short) (20 * 256));
        sheet.addMergedRegion(new Region(0, (short)(0), 0, (short)(3)));
        efw.setStringValue(0, 0, 0, "客户:"+context.contextMap.get("CUST_NAME"), BORDERED_BOLD_CENTER);
        
        
        sheet.addMergedRegion(new Region(2, (short)(0), 2, (short)(0)));
        efw.setStringValue(0, 2, 0, "支付表编号", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(2, (short)(1), 2, (short)(1)));
        efw.setStringValue(0, 2, 1, "应付日期", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(2, (short)(2), 2, (short)(2)));
        efw.setStringValue(0, 2, 2, "首期款", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(2, (short)(3), 2, (short)(3)));
        efw.setStringValue(0, 2, 3, "预期租金 ", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(2, (short)(4), 2, (short)(4)));
        efw.setStringValue(0, 2, 4, "应付租金", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(2, (short)(5), 2, (short)(5)));
        efw.setStringValue(0, 2, 5, "保证金均摊", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(2, (short)(6), 2, (short)(6)));
        efw.setStringValue(0, 2, 6, "应收凭证号", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(2, (short)(7), 2, (short)(7)));
        efw.setStringValue(0, 2, 7, "实付租金 ", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(2, (short)(8), 2, (short)(8)));
        efw.setStringValue(0, 2, 8, "罚息", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(2, (short)(9), 2, (short)(9)));
        efw.setStringValue(0, 2, 9, "期数", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(2, (short)(10), 2, (short)(10)));
        efw.setStringValue(0, 2, 10, "分解项目", BORDERED_BOLD_CENTER);
        
        sheet.addMergedRegion(new Region(1, (short)(11), 1, (short)(11)));
        efw.setStringValue(0, 1, 11, "对账函", BORDERED_BOLD_CENTER);
        
        sheet.addMergedRegion(new Region(2, (short)(11), 2, (short)(11)));
        efw.setStringValue(0, 2, 11, "来款日期", BORDERED_BOLD_CENTER);        
        sheet.addMergedRegion(new Region(2, (short)(12), 2, (short)(12)));
        efw.setStringValue(0, 2, 12, "来款金额 ", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(2, (short)(13), 2, (short)(13)));
        efw.setStringValue(0, 2, 13, "来款类型 ", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(2, (short)(14), 2, (short)(14)));
        efw.setStringValue(0, 2, 14, "来款附言 ", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(2, (short)(15), 2, (short)(15)));
        efw.setStringValue(0, 2, 15, "实收凭证号 ", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(2, (short)(16), 2, (short)(16)));
        efw.setStringValue(0, 2, 16, "发票号码 ", BORDERED_BOLD_CENTER);
        sheet.addMergedRegion(new Region(2, (short)(17), 2, (short)(17)));
        efw.setStringValue(0, 2, 17, "收据号码 ", BORDERED_BOLD_CENTER);
        
        //表内容开始设置
        int sheetNo=0;
        int rowNo=2;
        int colNo=0;
        
        int INIT_FIRST_RENT=0;
        double INIT_EXPECT_RENT=0;
        double INIT_EACH_RENT=0;
        double INIT_DEPOSIT_PRICE=0;
        double INIT_REAL_PRICE=0;
        String RECD_PERIOD=null;
        String FICB_ITEM=null;
        java.text.DecimalFormat FORMAT=new java.text.DecimalFormat("#0.00");
        for(int i=0;paymentList!=null&&i<paymentList.size();i++) {
        	rowNo++;
            colNo=0;
            
            sheet.setColumnWidth((short) colNo, (short) (20 * 256));
        	efw.setStringValue(sheetNo, rowNo, colNo++, (String)paymentList.get(i).get("RECP_CODE"), DEFAULT_CENTER);
        	sheet.setColumnWidth((short) colNo, (short) (11 * 256));
        	efw.setStringValue(sheetNo, rowNo, colNo++, (String)paymentList.get(i).get("PAY_DATE"), DEFAULT_CENTER);
        	sheet.setColumnWidth((short) colNo, (short) (10 * 256));
        	INIT_FIRST_RENT=INIT_FIRST_RENT+(Integer)paymentList.get(i).get("FIRST_RENT");
        	efw.setStringValue(sheetNo, rowNo, colNo++, String.valueOf(Double.valueOf((Integer)paymentList.get(i).get("FIRST_RENT"))), BORDERED_RIGHT_MONEY);
        	sheet.setColumnWidth((short) colNo, (short) (15 * 256));
        	INIT_EXPECT_RENT=INIT_EXPECT_RENT+(Double)paymentList.get(i).get("EXPECT_RENT");
        	efw.setStringValue(sheetNo, rowNo, colNo++, String.valueOf((Double)paymentList.get(i).get("EXPECT_RENT")), BORDERED_RIGHT_MONEY);
        	sheet.setColumnWidth((short) colNo, (short) (15 * 256));
        	INIT_EACH_RENT=INIT_EACH_RENT+(Double)paymentList.get(i).get("EACH_RENT");
        	efw.setStringValue(sheetNo, rowNo, colNo++, String.valueOf((Double)paymentList.get(i).get("EACH_RENT")), BORDERED_RIGHT_MONEY);
        	sheet.setColumnWidth((short) colNo, (short) (15 * 256));
        	INIT_DEPOSIT_PRICE=INIT_DEPOSIT_PRICE+(Double)paymentList.get(i).get("DEPOSIT_PRICE");
        	efw.setStringValue(sheetNo, rowNo, colNo++, String.valueOf(FORMAT.format((Double)paymentList.get(i).get("DEPOSIT_PRICE"))), BORDERED_RIGHT_MONEY);
        	sheet.setColumnWidth((short) colNo, (short) (15 * 256));
        	efw.setStringValue(sheetNo, rowNo, colNo++, (String)paymentList.get(i).get("K3SHOULDBILLNO"), DEFAULT_CENTER);
        	sheet.setColumnWidth((short) colNo, (short) (15 * 256));
        	INIT_REAL_PRICE=INIT_REAL_PRICE+(Double)paymentList.get(i).get("REAL_PRICE");
        	efw.setStringValue(sheetNo, rowNo, colNo++, String.valueOf((Double)paymentList.get(i).get("REAL_PRICE")), BORDERED_RIGHT_MONEY);
        	sheet.setColumnWidth((short) colNo, (short) (10 * 256));
        	efw.setStringValue(sheetNo, rowNo, colNo++, "", DEFAULT_CENTER);
        	sheet.setColumnWidth((short) colNo, (short) (10 * 256));
        	RECD_PERIOD=String.valueOf((Double)paymentList.get(i).get("RECD_PERIOD"));
        	efw.setStringValue(sheetNo, rowNo, colNo++, "null".equals(RECD_PERIOD)?"":RECD_PERIOD, DEFAULT_CENTER);
        	sheet.setColumnWidth((short) colNo, (short) (10 * 256));
        	FICB_ITEM=String.valueOf((Double)paymentList.get(i).get("FICB_ITEM"));
        	efw.setStringValue(sheetNo, rowNo, colNo++, "null".equals(FICB_ITEM)?"":FICB_ITEM, DEFAULT_CENTER);
        }
        	sheet.setColumnWidth((short) 0, (short) (20 * 256));
        	efw.setStringValue(sheetNo, rowNo+1, 0, "总计:", BORDERED_BOLD_CENTER);
        	sheet.setColumnWidth((short) 2, (short) (10 * 256));
        	efw.setStringValue(sheetNo, rowNo+1, 2, String.valueOf(INIT_FIRST_RENT), BORDERED_RIGHT_MONEY);
        	sheet.setColumnWidth((short) 3, (short) (15 * 256));
        	efw.setStringValue(sheetNo, rowNo+1, 3, String.valueOf(FORMAT.format(INIT_EXPECT_RENT)), BORDERED_RIGHT_MONEY);
        	sheet.setColumnWidth((short) 4, (short) (15 * 256));
        	efw.setStringValue(sheetNo, rowNo+1, 4, String.valueOf(INIT_EACH_RENT), BORDERED_RIGHT_MONEY);
        	sheet.setColumnWidth((short) 5, (short) (15 * 256));
        	efw.setStringValue(sheetNo, rowNo+1, 5, String.valueOf(FORMAT.format(INIT_DEPOSIT_PRICE)), BORDERED_RIGHT_MONEY);
        	sheet.setColumnWidth((short) 7, (short) (15 * 256));
        	efw.setStringValue(sheetNo, rowNo+1, 7, String.valueOf(INIT_REAL_PRICE), BORDERED_RIGHT_MONEY);
        
        rowNo=2;
        colNo=11;
        double INIT_INCOME_MONEY=0;
        for(int i=0;incomeList!=null&&i<incomeList.size();i++) {
        	rowNo++;
            colNo=11;
            
            sheet.setColumnWidth((short) colNo, (short) (11 * 256));
        	efw.setStringValue(sheetNo, rowNo, colNo++, (String)incomeList.get(i).get("OPPOSING_DATE"), DEFAULT_CENTER);
        	sheet.setColumnWidth((short) colNo, (short) (15 * 256));
        	INIT_INCOME_MONEY=INIT_INCOME_MONEY+(Double)incomeList.get(i).get("INCOME_MONEY");
        	efw.setStringValue(sheetNo, rowNo, colNo++, String.valueOf((Double)incomeList.get(i).get("INCOME_MONEY")), BORDERED_RIGHT_MONEY);
        	sheet.setColumnWidth((short) colNo, (short) (10 * 256));
        	efw.setStringValue(sheetNo, rowNo, colNo++, "1".equals((String)incomeList.get(i).get("OPPOSING_TYPE"))?"待分解来款":(String)incomeList.get(i).get("OPPOSING_TYPE"), DEFAULT_CENTER);
        	sheet.setColumnWidth((short) colNo, (short) (20 * 256));
        	efw.setStringValue(sheetNo, rowNo, colNo++, (String)incomeList.get(i).get("OPPOSING_POSTSCRIPT"), DEFAULT_CENTER);
        	sheet.setColumnWidth((short) colNo, (short) (20 * 256));
        	efw.setStringValue(sheetNo, rowNo, colNo++, (String)incomeList.get(i).get("K3REALBILLNO"), DEFAULT_CENTER);
        	sheet.setColumnWidth((short) colNo, (short) (20 * 256));
        	efw.setStringValue(sheetNo, rowNo, colNo++, (String)incomeList.get(i).get("INVOICE_NUM"), DEFAULT_CENTER);
        	sheet.setColumnWidth((short) colNo, (short) (20 * 256));
        	efw.setStringValue(sheetNo, rowNo, colNo++, (String)incomeList.get(i).get("RECEIPT_NUM"), DEFAULT_CENTER);
        }
	        sheet.setColumnWidth((short) 11, (short) (20 * 256));
	    	efw.setStringValue(sheetNo, rowNo+1, 11, "总计:", BORDERED_BOLD_CENTER);
	    	sheet.setColumnWidth((short) 12, (short) (15 * 256));
        	efw.setStringValue(sheetNo, rowNo+1, 12, String.valueOf(FORMAT.format(INIT_INCOME_MONEY)), BORDERED_RIGHT_MONEY);
        	
		return efw.getWorkbook();
	}
	
	public HSSFWorkbook accessCustomerPlanReport(List<AccessCustomerPlanReportTO> dataSourceList,Map<String,String> param) {
		
		ExcelFileWriter efw=new ExcelFileWriter();
		
		HSSFSheet sheet=efw.createSheet("客户拜访计划"+param.get("FROM_DATE")+"~"+param.get("TO_DATE"));
		
		HSSFCellStyle BORDERED_BOLD_CENTER=ExcelStyleLoader.load(efw.getStyle(), efw.getFont(), ExcelStyleLoader.BORDERED_BOLD_CENTER);
		HSSFCellStyle DEFAULT_CENTER=ExcelStyleLoader.load(efw.getStyle(), efw.getFont(), ExcelStyleLoader.DEFAULT_CENTER);
		HSSFCellStyle BORDERED_RIGHT_MONEY=ExcelStyleLoader.load(efw.getStyle(), efw.getFont(), ExcelStyleLoader.BORDERED_RIGHT_MONEY);
		HSSFCellStyle BORDERED_BOLD_RIGHT=ExcelStyleLoader.load(efw.getStyle(), efw.getFont(), ExcelStyleLoader.BORDERED_BOLD_RIGHT);
		
        HSSFFont font=efw.getFont();
        font.setFontHeightInPoints((short) 8);
        
        //表头开始设置
          //addMergedRegion(region.getRowFrom(), region.getColumnFrom(), region.getRowTo(), region.getColumnTo())
        sheet.setColumnWidth((short) 0, (short) (10 * 256));
        sheet.addMergedRegion(new Region(0, (short)(0), 0, (short)(0)));
          //setStringValue(int sheetNo, int rowNo, int cellNo, String cellValue, HSSFCellStyle cellStyle)
        efw.setStringValue(0, 0, 0, "名字", BORDERED_BOLD_CENTER);
        sheet.setColumnWidth((short) 1, (short) (10 * 256));
        sheet.addMergedRegion(new Region(0, (short)(1), 0, (short)(1)));
        efw.setStringValue(0, 0, 1, "办事处", BORDERED_BOLD_CENTER);
        sheet.setColumnWidth((short) 2, (short) (10 * 256));
        sheet.addMergedRegion(new Region(0, (short)(2), 0, (short)(2)));
        efw.setStringValue(0, 0, 2, "日期", BORDERED_BOLD_CENTER);
        sheet.setColumnWidth((short) 3, (short) (20 * 256));
        sheet.addMergedRegion(new Region(0, (short)(3), 0, (short)(3)));
        efw.setStringValue(0, 0, 3, "预计拜访开始时间", BORDERED_BOLD_CENTER);
        sheet.setColumnWidth((short) 4, (short) (20 * 256));
        sheet.addMergedRegion(new Region(0, (short)(4), 0, (short)(4)));
        efw.setStringValue(0, 0, 4, "预计拜访结束时间", BORDERED_BOLD_CENTER);
        sheet.setColumnWidth((short) 5, (short) (20 * 256));
        sheet.addMergedRegion(new Region(0, (short)(5), 0, (short)(5)));
        efw.setStringValue(0, 0, 5, "实际拜访开始时间", BORDERED_BOLD_CENTER);
        sheet.setColumnWidth((short) 6, (short) (20 * 256));
        sheet.addMergedRegion(new Region(0, (short)(6), 0, (short)(6)));
        efw.setStringValue(0, 0, 6, "实际拜访结束时间", BORDERED_BOLD_CENTER);
        sheet.setColumnWidth((short) 7, (short) (25 * 256));
        sheet.addMergedRegion(new Region(0, (short)(7), 0, (short)(7)));
        efw.setStringValue(0, 0, 7, "拜访对象", BORDERED_BOLD_CENTER);
        sheet.setColumnWidth((short) 8, (short) (15 * 256));
        sheet.addMergedRegion(new Region(0, (short)(8), 0, (short)(8)));
        efw.setStringValue(0, 0, 8, "开拓方式", BORDERED_BOLD_CENTER);
        sheet.setColumnWidth((short) 9, (short) (20 * 256));
        sheet.addMergedRegion(new Region(0, (short)(9), 0, (short)(9)));
        efw.setStringValue(0, 0, 9, "目的", BORDERED_BOLD_CENTER);
        sheet.setColumnWidth((short) 10, (short) (20 * 256));
        sheet.addMergedRegion(new Region(0, (short)(10), 0, (short)(10)));
        efw.setStringValue(0, 0, 10, "是否主管陪同", BORDERED_BOLD_CENTER);
        sheet.setColumnWidth((short) 11, (short) (15 * 256));
        sheet.addMergedRegion(new Region(0, (short)(11), 0, (short)(11)));
        efw.setStringValue(0, 0, 11, "是否请假", BORDERED_BOLD_CENTER);
        sheet.setColumnWidth((short) 12, (short) (20 * 256));
        sheet.addMergedRegion(new Region(0, (short)(12), 0, (short)(12)));
        efw.setStringValue(0, 0, 12, "请假开始时间", BORDERED_BOLD_CENTER);
        sheet.setColumnWidth((short) 13, (short) (20 * 256));
        sheet.addMergedRegion(new Region(0, (short)(13), 0, (short)(13)));
        efw.setStringValue(0, 0, 13, "请假结束时间", BORDERED_BOLD_CENTER);
        sheet.setColumnWidth((short) 14, (short) (10 * 256));
        sheet.addMergedRegion(new Region(0, (short)(14), 0, (short)(14)));
        efw.setStringValue(0, 0, 14, "省", BORDERED_BOLD_CENTER);
        sheet.setColumnWidth((short) 15, (short) (10 * 256));
        sheet.addMergedRegion(new Region(0, (short)(15), 0, (short)(15)));
        efw.setStringValue(0, 0, 15, "市", BORDERED_BOLD_CENTER);
        sheet.setColumnWidth((short) 16, (short) (10 * 256));
        sheet.addMergedRegion(new Region(0, (short)(16), 0, (short)(16)));
        efw.setStringValue(0, 0, 16, "区", BORDERED_BOLD_CENTER);
        sheet.setColumnWidth((short) 17, (short) (20 * 256));
        sheet.addMergedRegion(new Region(0, (short)(17), 0, (short)(17)));
        efw.setStringValue(0, 0, 17, "重点记录", BORDERED_BOLD_CENTER);
        sheet.setColumnWidth((short) 18, (short) (30 * 256));
        sheet.addMergedRegion(new Region(0, (short)(18), 0, (short)(18)));
        efw.setStringValue(0, 0, 18, "备注", BORDERED_BOLD_CENTER);
        sheet.setColumnWidth((short) 19, (short) (15 * 256));
        sheet.addMergedRegion(new Region(0, (short)(19), 0, (short)(19)));
        efw.setStringValue(0, 0, 19, "是否拜托同仁", BORDERED_BOLD_CENTER);
        sheet.setColumnWidth((short) 20, (short) (15 * 256));
        sheet.addMergedRegion(new Region(0, (short)(20), 0, (short)(20)));
        efw.setStringValue(0, 0, 20, "同仁姓名", BORDERED_BOLD_CENTER);
        //表头结束设置
        
        
        //表内容开始设置
        int sheetNo=0;
        int rowNo=0;
        int colNo=0;
        
        for(int i=0;i<dataSourceList.size();i++) {
        	rowNo++;
            colNo=0;
        	efw.setStringValue(sheetNo, rowNo, colNo++, dataSourceList.get(i).getName(), DEFAULT_CENTER);
        	efw.setStringValue(sheetNo, rowNo, colNo++, dataSourceList.get(i).getDeptName(), DEFAULT_CENTER);
        	efw.setStringValue(sheetNo, rowNo, colNo++, dataSourceList.get(i).getDate(), DEFAULT_CENTER);
        	efw.setStringValue(sheetNo, rowNo, colNo++, dataSourceList.get(i).getExpectFromTime(), DEFAULT_CENTER);
        	efw.setStringValue(sheetNo, rowNo, colNo++, dataSourceList.get(i).getExpectToTime(), DEFAULT_CENTER);
        	efw.setStringValue(sheetNo, rowNo, colNo++, dataSourceList.get(i).getActualFromTime(), DEFAULT_CENTER);
        	efw.setStringValue(sheetNo, rowNo, colNo++, dataSourceList.get(i).getActualToTime(), DEFAULT_CENTER);
        	efw.setStringValue(sheetNo, rowNo, colNo++, dataSourceList.get(i).getObject(), DEFAULT_CENTER);
        	efw.setStringValue(sheetNo, rowNo, colNo++, dataSourceList.get(i).getEmpolderWayDescr(), DEFAULT_CENTER);
        	efw.setStringValue(sheetNo, rowNo, colNo++, dataSourceList.get(i).getIntentDescr(), DEFAULT_CENTER);
        	efw.setStringValue(sheetNo, rowNo, colNo++, "Y".equals(dataSourceList.get(i).getWithSupervisor())?"是":"否", DEFAULT_CENTER);
        	efw.setStringValue(sheetNo, rowNo, colNo++, "Y".equals(dataSourceList.get(i).getHoliday())?"是":"否", DEFAULT_CENTER);
        	efw.setStringValue(sheetNo, rowNo, colNo++, dataSourceList.get(i).getHolidayFromTime(), DEFAULT_CENTER);
        	efw.setStringValue(sheetNo, rowNo, colNo++, dataSourceList.get(i).getHolidayToTime(), DEFAULT_CENTER);
        	efw.setStringValue(sheetNo, rowNo, colNo++, dataSourceList.get(i).getProvinceName(), DEFAULT_CENTER);
        	efw.setStringValue(sheetNo, rowNo, colNo++, dataSourceList.get(i).getCityName(), DEFAULT_CENTER);
        	efw.setStringValue(sheetNo, rowNo, colNo++, dataSourceList.get(i).getAreaName(), DEFAULT_CENTER);
        	efw.setStringValue(sheetNo, rowNo, colNo++, dataSourceList.get(i).getImportantRecordDescr(), DEFAULT_CENTER);
        	efw.setStringValue(sheetNo, rowNo, colNo++, dataSourceList.get(i).getRemark(), DEFAULT_CENTER);
        	efw.setStringValue(sheetNo, rowNo, colNo++, "Y".equals(dataSourceList.get(i).getNeedColleague())?"是":"否", DEFAULT_CENTER);
        	efw.setStringValue(sheetNo, rowNo, colNo++, dataSourceList.get(i).getEmployee(), DEFAULT_CENTER);
        }
        
        //表内容结束设置
		return efw.getWorkbook();
	} 
	
	public HSSFWorkbook caseAuditSituationReport(List<Map<String,Object>> resultList1,List<Map<String,Object>> resultList2) {
		
		java.text.DecimalFormat FORMAT=new java.text.DecimalFormat("#,##0.00");
		
		ExcelFileWriter efw=new ExcelFileWriter();
		
		HSSFSheet sheet1=efw.createSheet("当日案件审核状态");
		
		HSSFCellStyle BORDERED_BOLD_CENTER=ExcelStyleLoader.load(efw.getStyle(), efw.getFont(), ExcelStyleLoader.BORDERED_BOLD_CENTER);
		HSSFCellStyle DEFAULT_CENTER=ExcelStyleLoader.load(efw.getStyle(), efw.getFont(), ExcelStyleLoader.DEFAULT_CENTER);
		HSSFCellStyle BORDERED_RIGHT_MONEY=ExcelStyleLoader.load(efw.getStyle(), efw.getFont(), ExcelStyleLoader.BORDERED_RIGHT_MONEY);
		HSSFCellStyle BORDERED_BOLD_RIGHT=ExcelStyleLoader.load(efw.getStyle(), efw.getFont(), ExcelStyleLoader.BORDERED_BOLD_RIGHT);
		
        HSSFFont font=efw.getFont();
        font.setFontHeightInPoints((short) 8);
        
        //表头开始设置
        sheet1.setColumnWidth((short) 0, (short) (20 * 256));
        sheet1.addMergedRegion(new Region(0, (short)(0), 0, (short)(0)));
        efw.setStringValue(0, 0, 0, "案件号", BORDERED_BOLD_CENTER);
        sheet1.setColumnWidth((short) 1, (short) (30 * 256));
        sheet1.addMergedRegion(new Region(0, (short)(1), 0, (short)(1)));
        efw.setStringValue(0, 0, 1, "合同号", BORDERED_BOLD_CENTER);
        sheet1.setColumnWidth((short) 2, (short) (30 * 256));
        sheet1.addMergedRegion(new Region(0, (short)(2), 0, (short)(2)));
        efw.setStringValue(0, 0, 2, "业管通过时间", BORDERED_BOLD_CENTER);
        sheet1.setColumnWidth((short) 3, (short) (30 * 256));
        sheet1.addMergedRegion(new Region(0, (short)(3), 0, (short)(3)));
        efw.setStringValue(0, 0, 3, "财务通过时间", BORDERED_BOLD_CENTER);
        sheet1.setColumnWidth((short) 4, (short) (30 * 256));
        sheet1.addMergedRegion(new Region(0, (short)(4), 0, (short)(4)));
        efw.setStringValue(0, 0, 4, "拨款金额", BORDERED_BOLD_CENTER);
        sheet1.setColumnWidth((short) 5, (short) (30 * 256));
        sheet1.addMergedRegion(new Region(0, (short)(5), 0, (short)(5)));
        efw.setStringValue(0, 0, 5, "客户名称", BORDERED_BOLD_CENTER);
        //表头结束设置
        
        
        //表内容开始设置
        int sheetNo=0;
        int rowNo=0;
        int colNo=0;
        
        for(int i=0;i<resultList2.size();i++) {
        	rowNo++;
            colNo=0;
        	efw.setStringValue(sheetNo, rowNo, colNo++, (String)resultList2.get(i).get("CREDIT_RUNCODE"), DEFAULT_CENTER);
        	efw.setStringValue(sheetNo, rowNo, colNo++, (String)resultList2.get(i).get("LEASE_CODE"), DEFAULT_CENTER);
        	efw.setStringValue(sheetNo, rowNo, colNo++, resultList2.get(i).get("CREATE_DATE")==null?"":resultList2.get(i).get("CREATE_DATE").toString().substring(0,19), DEFAULT_CENTER);
        	efw.setStringValue(sheetNo, rowNo, colNo++, resultList2.get(i).get("CREATE_DATE_1")==null?"":resultList2.get(i).get("CREATE_DATE_1").toString().substring(0,19), DEFAULT_CENTER);
        	efw.setStringValue(sheetNo, rowNo, colNo++, "￥"+FORMAT.format(resultList2.get(i).get("PAY_MONEY")), BORDERED_RIGHT_MONEY);
        	efw.setStringValue(sheetNo, rowNo, colNo++, (String)resultList2.get(i).get("CUST_NAME"), DEFAULT_CENTER);
        }
        
        if(resultList1!=null&&resultList1.size()>0) {
        	HSSFSheet sheet2=efw.createSheet("昨日案件审核状态");
        	//表头开始设置
            sheet2.setColumnWidth((short) 0, (short) (20 * 256));
            sheet2.addMergedRegion(new Region(0, (short)(0), 0, (short)(0)));
            efw.setStringValue(1, 0, 0, "案件号", BORDERED_BOLD_CENTER);
            sheet2.setColumnWidth((short) 1, (short) (30 * 256));
            sheet2.addMergedRegion(new Region(0, (short)(1), 0, (short)(1)));
            efw.setStringValue(1, 0, 1, "合同号", BORDERED_BOLD_CENTER);
            sheet2.setColumnWidth((short) 2, (short) (30 * 256));
            sheet2.addMergedRegion(new Region(0, (short)(2), 0, (short)(2)));
            efw.setStringValue(1, 0, 2, "业管通过时间", BORDERED_BOLD_CENTER);
            sheet2.setColumnWidth((short) 3, (short) (30 * 256));
            sheet2.addMergedRegion(new Region(0, (short)(3), 0, (short)(3)));
            efw.setStringValue(1, 0, 3, "财务通过时间", BORDERED_BOLD_CENTER);
            sheet2.setColumnWidth((short) 4, (short) (30 * 256));
            sheet2.addMergedRegion(new Region(0, (short)(4), 0, (short)(4)));
            efw.setStringValue(1, 0, 4, "拨款金额", BORDERED_BOLD_CENTER);
            sheet1.setColumnWidth((short) 5, (short) (30 * 256));
            sheet1.addMergedRegion(new Region(0, (short)(5), 0, (short)(5)));
            efw.setStringValue(1, 0, 5, "客户名称", BORDERED_BOLD_CENTER);
            //表头结束设置
            
            
            //表内容开始设置
            sheetNo++;
            rowNo=0;
            colNo=0;
            
            for(int i=0;i<resultList1.size();i++) {
            	rowNo++;
                colNo=0;
            	efw.setStringValue(sheetNo, rowNo, colNo++, (String)resultList1.get(i).get("CREDIT_RUNCODE"), DEFAULT_CENTER);
            	efw.setStringValue(sheetNo, rowNo, colNo++, (String)resultList1.get(i).get("LEASE_CODE"), DEFAULT_CENTER);
            	efw.setStringValue(sheetNo, rowNo, colNo++, resultList1.get(i).get("CREATE_DATE")==null?"":resultList1.get(i).get("CREATE_DATE").toString().substring(0,19), DEFAULT_CENTER);
            	efw.setStringValue(sheetNo, rowNo, colNo++, resultList1.get(i).get("CREATE_DATE_1")==null?"":resultList1.get(i).get("CREATE_DATE_1").toString().substring(0,19), DEFAULT_CENTER);
            	efw.setStringValue(sheetNo, rowNo, colNo++, "￥"+FORMAT.format(resultList1.get(i).get("PAY_MONEY")), BORDERED_RIGHT_MONEY);
            	efw.setStringValue(sheetNo, rowNo, colNo++, (String)resultList1.get(i).get("CUST_NAME"), DEFAULT_CENTER);
            }
        }
        
        //表内容结束设置
		return efw.getWorkbook();
	}
}
