package com.brick.contribution.command;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;

import com.brick.base.command.BaseCommand;
import com.brick.contribution.service.ContributionService;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;

public class ContributionCommand extends BaseCommand {
	Log logger=LogFactory.getLog(ContributionCommand.class);
	private ContributionService contributionService;

	public void setContributionService(ContributionService contributionService) {
		this.contributionService = contributionService;
	}
	
	public void getContributions(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		Calendar calendar = Calendar.getInstance();
		int startYear = 2010;
		Date dataTime = calendar.getTime();
		if(context.contextMap.get("YEAR")==null ||
				"".equals(context.contextMap.get("YEAR")) ||
				(context.contextMap.get("YEAR")!=null && Integer.parseInt(context.contextMap.get("YEAR").toString()) > calendar.get(Calendar.YEAR)) ||
				(context.contextMap.get("YEAR")!=null && Integer.parseInt(context.contextMap.get("YEAR").toString()) < startYear)) {
			context.contextMap.put("YEAR",-1);
			context.contextMap.put("BEGIN_DATE","2010-01-01");
			context.contextMap.put("END_DATE",DateUtil.dateToString(dataTime,"yyyy-MM-dd"));
		} else {
			context.contextMap.put("BEGIN_DATE",context.contextMap.get("YEAR") + "-01-01");
			context.contextMap.put("END_DATE",context.contextMap.get("YEAR") + "-12-31");
		}
		if(context.contextMap.get("days")==null||"".equals(context.contextMap.get("days"))) {
			context.contextMap.put("days","31");
		}
		if(context.contextMap.get("type")==null||"".equals(context.contextMap.get("type"))) {
			context.contextMap.put("type","upUser");
		}
		List<Map<String, Object>> resultList = this.contributionService.getContrubutions(context.contextMap);
		outputMap.put("days", context.contextMap.get("days"));
		outputMap.put("type", context.contextMap.get("type"));
		outputMap.put("resultList", resultList);
		outputMap.put("YEAR",context.contextMap.get("YEAR"));
		outputMap.put("dataTime",dataTime);
		Output.jspOutput(outputMap, context, "/contribution/contribution.jsp");
	}
	
	/**
	 * 导出excel
	 * @param context
	 */
	public void exportExcel(Context context){
        try {
			String pType = context.contextMap.get("pType").toString();
			String dType = context.contextMap.get("dType").toString();
			Calendar calendar = Calendar.getInstance();
			int startYear = 2010;
			if(context.contextMap.get("YEAR")==null ||
					"".equals(context.contextMap.get("YEAR")) ||
					(context.contextMap.get("YEAR")!=null && Integer.parseInt(context.contextMap.get("YEAR").toString()) > calendar.get(Calendar.YEAR)) ||
					(context.contextMap.get("YEAR")!=null && Integer.parseInt(context.contextMap.get("YEAR").toString()) < startYear)) {
				context.contextMap.put("YEAR",-1);
				context.contextMap.put("BEGIN_DATE","2010-01-01");
				context.contextMap.put("END_DATE",DateUtil.dateToString(calendar.getTime(),"yyyy-MM-dd"));
			} else {
				context.contextMap.put("BEGIN_DATE",context.contextMap.get("YEAR") + "-01-01");
				context.contextMap.put("END_DATE",context.contextMap.get("YEAR") + "-12-31");
			}
			HSSFWorkbook wb = new HSSFWorkbook();
			String yearName = context.contextMap.get("YEAR").toString() + "年度";
			if(yearName.equals("-1年度")){
				yearName = "全部";
			}
			for(String p : pType.split(",")){
				for(String d : dType.split(",")){
					context.contextMap.put("type",p);
					context.contextMap.put("days",d);
					List<Map<String, Object>> resultList = this.contributionService.getContrubutions(context.contextMap);
				    this.makeExcel(wb, p, d, resultList, yearName + "数据，制表时间：" + DateUtil.dateToString(calendar.getTime(),"yyyy-MM-dd HH:mm:ss"));
				}
			}
			//写回客户端
			context.response.setContentType("application/vnd.ms-excel");
			context.response.setCharacterEncoding("UTF-8");
	        String fileName = yearName + "贡献度数据.xls";
			fileName = new String(fileName.getBytes("GB2312"), "ISO_8859_1");
			context.response.setHeader("Content-Disposition","attachment; filename=" + fileName + "");
		    ServletOutputStream sos = context.response.getOutputStream();
		    wb.write(sos);
		    sos.flush();
		    sos.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * 创建excel
	 * @param wb excel
	 * @param pType 分类
	 * @param dType	  逾期天数
	 * @param resultList  查询结果
	 * @param time  数据时间
	 */
	private void makeExcel(HSSFWorkbook wb, String pType, String dType, List<Map<String, Object>> resultList, String time){
		Map<String, String> nameMap = new HashMap<String, String>();
		nameMap.put("upUser", "主管");
		nameMap.put("user", "業務員");
		nameMap.put("appUser", "審查主管");
		HSSFSheet sheet = wb.createSheet(nameMap.get(pType) + "(" + dType + "天)");
	    HSSFFont font = wb.createFont();
	    font.setFontName("宋体");
	    font.setFontHeightInPoints((short) 11);
	    // 创建单元格样式
	    HSSFCellStyle style = wb.createCellStyle();
	    style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
	    style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	    style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	    style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	    style.setBorderRight(HSSFCellStyle.BORDER_THIN);
	    style.setBorderTop(HSSFCellStyle.BORDER_THIN);
	    style.setWrapText(true);
	    style.setFont(font);
	    //脚时间单元格样式
	    HSSFCellStyle footStyle = wb.createCellStyle();
	    footStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
	    footStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	    footStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	    footStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
	    footStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	    footStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	    footStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
	    footStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
	    HSSFDataFormat format = wb.createDataFormat();  
	    footStyle.setDataFormat(format.getFormat("yyyy-MM-dd hh:mm")); 
	    footStyle.setFont(font);
	    // 创建右对齐单元格货币样式
	    HSSFCellStyle rigthStyle = wb.createCellStyle();
	    rigthStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
	    rigthStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
	    rigthStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	    rigthStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	    rigthStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
	    rigthStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
	    rigthStyle.setFont(font);
	    // 货币样式
	    HSSFDataFormat moneyStyle = wb.createDataFormat();
	    rigthStyle.setDataFormat(moneyStyle.getFormat("#,##0.00"));
	    
	    // 创建右对齐单元格百分比样式样式
	    HSSFCellStyle percentStyle = wb.createCellStyle();
	    percentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
	    percentStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
	    percentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	    percentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	    percentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
	    percentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
	    percentStyle.setFont(font);
	    // 百分比样式
	    percentStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
	    
	    //第一行标题行
	    String[] titles = {nameMap.get(pType),"合同筆數","淨撥款金額","利息總額","利差總額","平均TR","逾期合同數（"+ dType +"天以上）",
	    		"逾期合同百分比（"+ dType +"天以上）","逾期金額      （"+ dType +"天以上）","逾期金額百分比（"+ dType +"天以上）"};
	    HSSFRow row = sheet.createRow(0);
	    for(int i = 0; i < titles.length; i++){
	    	HSSFCell cell = row.createCell(i);
		    cell.setCellStyle(style);
		    cell.setCellValue(titles[i]);
	    }
	    for(int i = 0; i < resultList.size(); i++){
		    sheet.autoSizeColumn(0);
		    sheet.autoSizeColumn(1);
		    sheet.setColumnWidth((short) 2, 5000);
		    sheet.setColumnWidth((short) 3, 5000);
		    sheet.setColumnWidth((short) 4, 5000);
		    sheet.setColumnWidth((short) 6, 4000);
		    sheet.setColumnWidth((short) 7, 4100);
		    sheet.setColumnWidth((short) 8, 5000);
		    sheet.setColumnWidth((short) 9, 4100);
		    int totalCount = Integer.parseInt(resultList.get(i).get("TOTAL_COUNT").toString());
		    double leaseRze = Double.parseDouble(resultList.get(i).get("LEASE_RZE").toString());
		    double renPrice = Double.parseDouble(resultList.get(i).get("REN_PRICE").toString());
		    double rateDiff = Double.parseDouble(resultList.get(i).get("RATE_DIFF").toString());
		    double dunPrice = Double.parseDouble(resultList.get(i).get("DUN_PRICE").toString());
		    double tr = Double.parseDouble(resultList.get(i).get("TR").toString());
		    int count = Integer.parseInt(resultList.get(i).get("DUN_COUNT").toString());
	    	row = sheet.createRow(i + 1);

	    	HSSFCell cell = row.createCell(0);
		    cell.setCellStyle(style);
		    cell.setCellValue(resultList.get(i).get("NAME")==null?"":resultList.get(i).get("NAME").toString());

		    cell = row.createCell(1);
		    cell.setCellStyle(style);
		    cell.setCellValue(totalCount);

		    cell = row.createCell(2);
		    cell.setCellStyle(rigthStyle);
		    cell.setCellValue(leaseRze);

		    cell = row.createCell(3);
		    cell.setCellStyle(rigthStyle);
		    cell.setCellValue(renPrice);

		    cell = row.createCell(4);
		    cell.setCellStyle(rigthStyle);
		    cell.setCellValue(rateDiff);

		    cell = row.createCell(5);
		    cell.setCellStyle(percentStyle);
		    cell.setCellValue(leaseRze == 0 ? 0 : tr * 0.01 / leaseRze);
		    
		    cell = row.createCell(6);
		    cell.setCellStyle(style);
		    cell.setCellValue(count);
		    
		    cell = row.createCell(7);
		    cell.setCellStyle(percentStyle);
		    cell.setCellValue(totalCount == 0 ? 0 : count * 1.0 / totalCount);
		    
		    cell = row.createCell(8);
		    cell.setCellStyle(rigthStyle);
		    cell.setCellValue(dunPrice);
		    
		    cell = row.createCell(9);
		    cell.setCellStyle(percentStyle);
		    cell.setCellValue(leaseRze == 0 ? 0 : dunPrice * 1.0 / leaseRze);
	    }
	    row = sheet.createRow(resultList.size() + 2);
	    HSSFCell cell = row.createCell(0);
	    cell.setCellValue(time);
	    cell.setCellStyle(footStyle);
	    //合并单元格数
	    int cells = 3;
	    for(int i = 0; i < cells; i ++){
		    cell = row.createCell(i + 1);
		    cell.setCellStyle(footStyle);
	    }
	    //合并单元格
	    sheet.addMergedRegion(new CellRangeAddress(resultList.size() + 2, resultList.size() + 2, 0, cells));
	}

}
